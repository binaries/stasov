package com.pocketmath.stasov.util.dynamicbitset;

import com.pocketmath.stasov.util.validate.ValidationException;
import com.pocketmath.stasov.util.validate.ValidationRuntimeException;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.nicoulaj.compilecommand.annotations.Inline;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.IllegalFormatCodePointException;
import java.util.Objects;

/**
 * Format of sparse entry is a 64-bit long with bits as:
 * <pre>
 *   0..31  : block position
 *   31..61 : length
 *   62     : sign (1 is set, 0 is clear)
 *   63     : false is sparse else true is dense
 * </pre>
 *
 * Format of dense entry is a 64-bit long with bits as:
 * <pre>
 *     0..31  : block position
 *     31..61 : dense index
 *     62     : not used
 *     63     : false is sparse else true is dense
 * </pre>
 *
 * Note that zero (0) is not a valid value in any case.  If sparse, the length must be greater than zero.  If dense, the flag at bit 63 is 1.
 *
 * Created by etucker on 4/8/16.
 */
public class SBS3 implements LongIterable {

    long[] array;
//    int dataSize;

    static final long EMPTY = Long.MAX_VALUE;

    long[][] dense;
    int denseSize;
    private int[] denseFree;
    private int denseFreeSize;

    private int splitThreshold;
    private int splitBleed;

    static final long ALL_CLEAR = 0L;
    static final long ALL_SET = 0xFFFFFFFFFFFFFFFFL;

    private static final int MIN_BLOCK = 0;
    private static final int MAX_BLOCK = 0x0FFFFFFF;
    static {
        assert MIN_BLOCK >= 0;
        assert MAX_BLOCK >= 0;
    }

    private static final long MIN_POSITION = 0L;
    private static final long MAX_POSITION =
            Math.subtractExact(
                Math.multiplyExact(
                        64L,
                        Math.addExact(
                                (long)MAX_BLOCK,
                                1L)
                ),
                1L);
    static {
        assert MIN_POSITION >= 0L;
        assert MAX_POSITION >= 0L;
        assert MIN_POSITION <= MAX_POSITION;
    }

    private static final int MAX_BLOCKS = Math.incrementExact(MAX_BLOCK);

    private static final int MAX_DENSE_INDEX = MAX_BLOCK;

    private static final int MAX_BLOCKS_PER_DENSE = 32768;
    private static final int INITIAL_BLOCKS_PER_DENSE = 16;

    public static int minBlock() {
        return MIN_BLOCK;
    }

    /**
     * max block identifier
     *
     * @return
     */
    public static int maxBlock() {
        return MAX_BLOCK;
    }

    /**
     * max number of blocks
     *
     * @return
     */
    public static int maxBlocks() {
        return MAX_BLOCKS;
    }

    public static long minPosition() {
        return MIN_POSITION;
    }

    public static long maxPosition() {
        return MAX_POSITION;
    }

    public static int minIntegerPosition() {
        return (int) Math.max(Integer.MIN_VALUE, minPosition());
    }

    public static int maxIntegerPosition() {
        return (int) Math.min(Integer.MAX_VALUE - 1, maxPosition());
    }

    protected static int maxDenseIndex() {
        return MAX_DENSE_INDEX;
    }

    protected static final int maxBlocksPerDense() {
        return MAX_BLOCKS_PER_DENSE;
    }

    protected static final int initialBlocksPerDense() {
        return INITIAL_BLOCKS_PER_DENSE;
    }

    public SBS3(final int capacity, final int splitThreshold, final int splitBleed) {
        if (capacity < 1) throw new IllegalArgumentException();
        if (splitThreshold < 1) throw new IllegalArgumentException();
        if (splitBleed > splitThreshold - 1) throw new IllegalArgumentException();
        this.splitThreshold = splitThreshold;
        this.splitBleed = splitBleed;
        this.array = new long[capacity];
        this.array[0] = Conv.createSparse(0, maxBlocks(), false);
        if (array.length > 1) Arrays.fill(array, 1, capacity, EMPTY);
        this.end = 0;

        this.denseFree = null;
        this.denseFreeSize = 0;

        assert(validateInvariants());
    }

    public SBS3(final int capacity, final int splitThreshold) {
        this(capacity, splitThreshold, 4);
    }

    public SBS3(final int capacity) {
        this(capacity, 8);
    }

    public SBS3() {
        this(8);
    }

    @Inline
    long getComponent(final int componentIndex) {
        return array[componentIndex];
    }

    @Inline
    void setComponent(final int componentIndex, final long component) {
        if (array[componentIndex] != EMPTY && array[componentIndex] != component)
            deleteComponent(componentIndex);
        array[componentIndex] = component;
    }

    @Inline
    long[] getDenseData(final int denseIndex) {
        return dense[denseIndex];
    }

    protected static class Conv {

        @Inline
        static boolean isSparse(final long a) {
            assert(a != EMPTY);
            return (a & 0x1L) == 0x0L;
        }

        @Inline
        static boolean isDense(final long a) {
            assert(a != EMPTY);
            return (a & 0x1L) == 0x1L;
        }

        @Inline
        static int startBlock(final long a) {
            assert(a != EMPTY);
            final int retVal = (int) (a >> 32);
            assert retVal >= 0;
            return retVal;
        }

        @Inline
        static long startPosition(final long a) {
            assert a != EMPTY;
            final long retVal = PosConv.blockToStartPosition(startBlock(a));
            assert retVal >= minPosition() : retVal;
            assert retVal <= maxPosition() : retVal;
            return retVal;
        }

        @Inline
        static int sparseLength(final long a) {
            assert(a != EMPTY);
            final int retVal = ((int) a) >> 2;
            assert retVal >= 0;
            return retVal;
        }

        @Inline
        static int denseIndex(final long a) {
            assert(a != EMPTY);
            final int retVal = ((int) a) >> 2;
            assert retVal >= 0;
            return retVal;
        }

        @Inline
        static boolean sparseValue(final long a) {
            assert(a != EMPTY);
            return (0x2L & a) == 0x2L;
        }

        static long createSparse(final int startBlock, final int length, final boolean set) {
            if (startBlock < 0) throw new IllegalArgumentException();
            if (length <= 0) throw new IllegalArgumentException();
            if (length > maxBlocks()) throw new IllegalArgumentException();
            //System.out.println("startBlock: " + Integer.toBinaryString(startBlock));
            //System.out.println("length: " + Integer.toBinaryString(length));
            //System.out.println("length << 2: " + Integer.toBinaryString(length << 2));
            //System.out.println("set: " + set);
            final long retVal = (((long)startBlock) << 32) | (((long)length) << 2) | (set ? 2L : 0L);
            //System.out.println("retVal: " + Long.toBinaryString(retVal));
            //System.out.println("retVal.startBlock=" + Integer.toHexString(Conv.startBlock(retVal)) + ", startBlock=" + Integer.toHexString(startBlock));
            //System.out.println("retVal.len=" + Integer.toHexString(Conv.sparseLength(retVal)) + ", length=" + Integer.toHexString(length));
            assert(Conv.startBlock(retVal) == startBlock);
            assert(Conv.sparseLength(retVal) == length);
            assert(Conv.sparseValue(retVal) == set);
            assert sparseLength(retVal) > 0;
            return retVal;
        }

        static long createDense(final int startBlock, final int denseIndex) {
            if (startBlock < 0) throw new IllegalArgumentException();
            if (denseIndex < 0) throw new IllegalArgumentException();
            if (denseIndex > maxDenseIndex()) throw new IllegalArgumentException();
            final long retVal = (((long)startBlock) << 32) | (((long)denseIndex) << 2) | 1L;
            assert(Conv.isDense(retVal));
            assert(Conv.startBlock(retVal) == startBlock);
            assert(Conv.denseIndex(retVal) == denseIndex);
            return retVal;
        }

        @Inline
        public static long createEphemeralQueryComponent(final int block) {
            final long retVal = ((long)block) << 32;
            return retVal;
        }
    }

    protected static class PosConv {

        @Inline
        static int positionToBlock(final long position) {
            assert(position <= maxPosition());

            final int retVal = (int)(position / 64L);
            /*
            final int retVal;
            if (position % 64 == 0)
                if (position == 0)
                    retVal = 0;
                else
                    retVal = (int)(position / 64L); // + 1;
            else
                retVal = (int)(position / 64L);
            */

            assert(retVal >= 0);
            assert(retVal <= maxBlock());
            return retVal;
        }

        @Inline
        static long blockToStartPosition(final int block) {
            return ((long)block) * 64L;
        }

        @Inline
        static long blockToEndPosition(final int block) {
            return blockToStartPosition(block) + 63L;
        }

        @Inline
        static long positionToKey(final long position) {
            assert(position <= maxPosition());

            final int block = positionToBlock(position);
            //System.out.println("positionToBlock result: " + block);

            final long retVal = ((long)block) << 32;
            assert(retVal >= 0);
            return retVal;
        }

        @Inline
        static boolean aboveMaxPosition(final long position) {
            return position > maxPosition();
        }

        @Inline
        static void requireInBoundsPosition(final long position) {
            if (position > maxPosition() || position < minPosition())
                throw new IllegalArgumentException();
        }

    }

    private static class BitUtil {

        @Inline
        public static long flip(final long a) {
            return a ^ 0xFFFFFFFFFFFFFFFFL;
        }

    }

    @Inline
    protected int fastDenseLength(final long a) {
        assert(a != EMPTY);
        final int denseIndex = Conv.denseIndex(a);
        final long[] data = dense[denseIndex];
        return data.length;
    }

    @Inline
    protected int length(final long a) {
        assert a != EMPTY;
        final int len;
        if (Conv.isSparse(a)) {
            len = Conv.sparseLength(a);
            assert len > 0;
            assert len <= maxBlocks();
        } else {
            assert(Conv.isDense(a));
            len = fastDenseLength(a);
            assert len > 0;
            assert len <= maxBlocksPerDense();
            assert len <= maxBlocks();
        }
        return len;
    }

    @Inline
    protected int startBlock(final long a) {
        final int startBlock = Conv.startBlock(a);
        assert startBlock >= 0;
        return startBlock;
    }

    @Inline
    protected int endBlock(final long a) {
        // TODO: optimize?
        int x = length(a);
        assert x > 0;
        assert x <= maxBlocks();
        assert Conv.isDense(a) ? x < maxBlocksPerDense() : true;
        x = Math.decrementExact(x);
        if (x < 0)
            throw new IllegalStateException();
        final int startBlock = Conv.startBlock(a);
        assert startBlock >= 0;
        x = Math.addExact(startBlock, x);
        return x;
    }

    @Inline
    protected long startPosition(final long a) {
        assert a != EMPTY;
        return Conv.startPosition(a);
    }

    @Inline
    protected long endPosition(final long a) {
        assert a != EMPTY;
        return Conv.startPosition(a) + (long)length(a) * 64L - 1L;
    }

    @Inline
    protected boolean containsPosition(final long a, final long position) {
        return startPosition(a) <= position && endPosition(a) >= position;
    }

    protected boolean validateContainsPosition(final long a, final long position) throws ValidationRuntimeException {
        try {
            if (a == SBS3.EMPTY) throw new ValidationException("component was unexpectedly empty");
            if (startPosition(a) > position) throw new ValidationException("startPosition(a): " + startPosition(a) + "; position: " + position);
            if (endPosition(a) < position) throw new ValidationException("endPosition(a): " + endPosition(a) + "; position: " + position);
            return true;
        } catch (ValidationException ve) {
            throw new ValidationRuntimeException(ve);
        }
    }

    /**
     * determines whether the component contains the block
     *
     * @param a component
     * @param block
     * @return
     */
    @Inline
    boolean containsBlock(final long a, final long block) {
        // TODO: optimize
        final int startBlock = startBlock(a), endBlock = endBlock(a);
        return startBlock <= block && endBlock >= block;
    }

    protected int splitThreshold() {
        return splitThreshold;
    }

    long[] denseArray(final long a) {
        assert validateComponent(a);

        assert Conv.isDense(a);
        final int denseIndex = Conv.denseIndex(a);
        final long[] denseArray = dense[denseIndex];
        assert denseArray != null;
        return denseArray;
    }

    long createDenseNoPrefill(final int startBlock, final int length) {
        assert validateBlock(startBlock);

        if (length > maxBlocksPerDense()) throw new IllegalArgumentException();
        final long[] newArray = new long[length];
        final int denseIndex = assignDenseIndex(newArray);
        return Conv.createDense(startBlock, denseIndex);
    }

    /**
     *
     * @param len
     * @param fillValue
     * @return dense index
     */
    private int createDense(final int len, final boolean fillValue) {
        if (len > maxBlocksPerDense()) throw new IllegalArgumentException("len=" + len);
        final long[] newArray = new long[len];
        Arrays.fill(newArray, fillValue ? ALL_SET : ALL_CLEAR);

        final int denseIndex = assignDenseIndex(newArray);

        return denseIndex;
    }

    private int assignDenseIndex(final long[] data) {

        if (data.length < 1)
            throw new IllegalArgumentException();

        if (dense == null) {
            dense = new long[8][];
            denseSize = 0;
        }

        if (denseFreeSize > 0) {
            return denseFree[--denseFreeSize];
        }
        if (denseSize >= dense.length) {
            assert(denseSize == dense.length);
            dense = ObjectArrays.grow(dense, Math.max((int)(dense.length * 1.5), dense.length + 8));
        }
        final int denseIndex = denseSize;
        dense[denseIndex] = data;
        denseSize++;
        return denseIndex;
    }

    private void removeDense(final int denseIndex) {
        assert denseIndex >= 0;
        dense[denseIndex] = null;
        if (denseFree == null) {
            assert denseFreeSize == 0;
            denseFree = new int[8];
        } else if (denseFreeSize >= denseFree.length) {
            assert(denseSize == dense.length);
            denseFree = IntArrays.grow(denseFree, Math.max((int)(denseFree.length * 1.5), denseFree.length + 8));
        }
        denseFree[denseFreeSize++] = denseIndex;
        assert denseFreeSize > 0;
    }

    void deleteComponent(final int componentIndex) {
        final long a = getComponent(componentIndex);
        if (Conv.isDense(a)) {
            final int denseIndex = Conv.denseIndex(a);
            if (denseIndex < 0)
                throw new IllegalStateException();
            if (dense[denseIndex] == null)
                throw new IllegalStateException();
            removeDense(denseIndex);
        }
    }

    protected void convertToDense(final int index) {
        assert (validateInvariants());
        final long sparse = array[index];
        assert (sparse != EMPTY) : "index: " + index;
        assert (!Conv.isDense(sparse)) : "index: " + index;
        final int startBlock = Conv.startBlock(sparse);
        assert (startBlock <= maxBlock()) : "index: " + index;
        final int len = Conv.sparseLength(sparse);
        if (len > maxBlocksPerDense()) {
            print(System.err);
            throw new IllegalStateException("too many blocks; please split before converting to dense; len: " + len);
        }
        assert (len > 0) : "index: " + index;
        final boolean sparseValue = Conv.sparseValue(sparse);
        assert (validateContinuous()) : "index: " + index;
        final int denseIndex = createDense(len, sparseValue);
        assert (dense[denseIndex] != null);
        assert (dense[denseIndex].length == len);
        final long denseA = Conv.createDense(startBlock, denseIndex);
        assert (dense[denseIndex].length == len);
        assert (Conv.isDense(denseA));
        assert (Conv.startBlock(denseA) == startBlock);
        assert (Conv.denseIndex(denseA) == denseIndex);

        array[index] = denseA;
        assert length(getComponent(index)) > 0;
        assert length(getComponent(index)) <= maxBlocksPerDense();
        assert validateInvariants();
    }

    public void readOptimize() {
        assert(validateInvariants());

        LongArrays.trim(array, Math.min(0, end - 1));

        final long[][] newDense = new long[denseSize][];
        int newDenseSize = 0;

        for (int i = 0; i <= end; i++) {
            final long a = array[i];
            if (Conv.isDense(a)) {
                final int denseIndex = Conv.denseIndex(a);
                final int newDenseIndex = newDenseSize;
                newDense[newDenseIndex] = dense[denseIndex];
                newDenseSize++;
                final int startBlock = Conv.startBlock(a);
                final long newA = Conv.createDense(startBlock, newDenseIndex);
                array[i] = newA;
            }
        }
        this.dense = newDense;
        this.denseSize = newDenseSize;

        denseFree = null;
        denseFreeSize = 0;

        assert(validateInvariants());
    }

    public void readWriteOptimize() {
        LongArrays.trim(array, Math.max(256, Math.multiplyExact(end, 2)));
        ObjectArrays.trim(dense, Math.max(128, Math.multiplyExact(denseSize, 2)));
        IntArrays.trim(denseFree, Math.max(128, Math.multiplyExact(denseFreeSize,2)));
    }

    protected boolean setDenseByPosition(final long a, final long position) {
        assert(validateInvariants());

        assert(a != EMPTY);
        assert(Conv.isDense(a));
        final int sBlock = Conv.startBlock(a);
        //System.out.println("das sblocken: " + sBlock + " hex: " + Integer.toHexString(sBlock));
        final int positionBlock = PosConv.positionToBlock(position);
        //System.out.println("das positionblocken: " + positionBlock + " hex: " + Integer.toHexString(positionBlock));
        assert(positionBlock >= sBlock);
        //final int blockOffset = positionBlock - sBlock;
        final int positionOffset = (int)(position % 64);
        final int denseIndex = Conv.denseIndex(a);

        final long startPosition = Conv.startPosition(a);
        final int denseDataOffset = (int)((position - startPosition) / 64);

        final long[] data = dense[denseIndex];

        //System.out.println("start block: " + Conv.startBlock(a));
        //System.out.println("position: " + position);
        //System.out.println("block's start position: " + startPosition);
        //System.out.println("denseDataOffset: " + denseDataOffset);
        //System.out.println("dense data length: " + data.length);


        //System.out.println("das denseindexen: " + denseIndex);
        final long x = 0x1L << positionOffset;
        //System.out.println("positionOffset: " + positionOffset);
        //System.out.println("x:" + Long.toHexString(x));

        assert(sBlock >= 0);
        assert(positionBlock >= 0);
        //assert(blockOffset >= 0);
        assert(denseDataOffset >= 0);
        assert(positionOffset >= 0);
        assert(denseIndex >= 0);
        assert(denseIndex < dense.length);
        assert(x != 0);

        //System.out.println("denseIndex: " + denseIndex);
        if (data == null)
            throw new IllegalStateException("denseIndex = " + denseIndex);
        //System.out.println("data.length: " + data.length);
        //print(System.out);
        //if (blockOffset >= data.length) {
        //    System.out.println("WTF");
        //}
        final long d = data[denseDataOffset];
        data[denseDataOffset] |= x;
        //System.out.println("data[blockOffset]:" + Long.toHexString(data[blockOffset]));

        assert(validateInvariants());
        return (d & x) == 0;
    }

    protected boolean clearDenseByPosition(final long a, final long position) {
        assert(validateInvariants());

        assert(a != EMPTY);
        assert(Conv.isDense(a));
        final int sBlock = Conv.startBlock(a);
        //System.out.println("das sblocken: " + sBlock + " hex: " + Integer.toHexString(sBlock));
        final int positionBlock = PosConv.positionToBlock(position);
        //System.out.println("das positionblocken: " + positionBlock + " hex: " + Integer.toHexString(positionBlock));
        assert(positionBlock >= sBlock);
        //final int blockOffset = positionBlock - sBlock;
        final int positionOffset = (int)(position % 64);
        final int denseIndex = Conv.denseIndex(a);

        final long startPosition = Conv.startPosition(a);
        final int denseDataOffset = (int)((position - startPosition) / 64);

        final long[] data = dense[denseIndex];

        //System.out.println("start block: " + Conv.startBlock(a));
        //System.out.println("position: " + position);
        //System.out.println("block's start position: " + startPosition);
        //System.out.println("denseDataOffset: " + denseDataOffset);
        //System.out.println("dense data length: " + data.length);


        //System.out.println("das denseindexen: " + denseIndex);
        final long x = BitUtil.flip( 1L << positionOffset );
        //System.out.println("positionOffset: " + positionOffset);
        //System.out.println("x:" + Long.toHexString(x));

        assert(sBlock >= 0);
        assert(positionBlock >= 0);
        //assert(blockOffset >= 0);
        assert(denseDataOffset >= 0);
        assert(positionOffset >= 0);
        assert(denseIndex >= 0);
        assert(denseIndex < dense.length);
        assert(x != 0);

        //System.out.println("denseIndex: " + denseIndex);
        if (data == null)
            throw new IllegalStateException("denseIndex = " + denseIndex);
        //System.out.println("data.length: " + data.length);
        //print(System.out);
        //if (blockOffset >= data.length) {
        //    System.out.println("WTF");
        //}
        final long d = data[denseDataOffset];
        data[denseDataOffset] &= x;
        //System.out.println("data[blockOffset]:" + Long.toHexString(data[blockOffset]));

        assert(validateInvariants());
        return (d & x) == d;
    }

    private boolean validateContinuous() {
        int pBlock = 0;
        for (int i = 0; i <= end; i++) {
            final long a = array[i];
            if (a == EMPTY)
                continue;
            final int sBlock = Conv.startBlock(a);
            if (sBlock != pBlock) {
                print(System.err);
                throw new IllegalStateException("not continuous at start block: " + sBlock + " with previous end block: " + pBlock);
            }
            pBlock = sBlock + length(a);
        }
        return true;
    }

    private boolean validateMonotonicIncreasing() {
        long past = Long.MIN_VALUE;
        for (int i = 0; i <= end; i++) {
            final long a = array[i];
            if (a == EMPTY)
                continue;
            if (a <= past) {
                print(System.err);
                throw new IllegalStateException("broken at index: " + i);
            }
            past = a;
        }
        return true;
    }

    private boolean validateMonotonicIncreasingByBlock() {

        int startBlock = -1;

        for (int i = 0; i <= end; i++) {
            final long a = array[i];
            if (a == EMPTY)
                continue;
            final int nextStartBlock = Conv.startBlock(a);
            if (nextStartBlock < startBlock) {
                print(System.err);
                throw new IllegalStateException("startBlock=" + startBlock + " at array index=" + i);
            }
            startBlock = nextStartBlock;
        }
        return true;
    }

    private boolean validateEmptyMarking() {
        //if (Arrays.stream(array, 0, dataSize).anyMatch( a -> a == EMPTY )) throw new IllegalStateException();
        //if (Arrays.stream(array, dataSize, array.length).anyMatch( a -> a != EMPTY )) throw new IllegalStateException();
        return true;
    }

    private boolean validateDenseHaveArrays() {
        for (int i = 0; i <= end; i++) {
            final long a = array[i];
            if (a == EMPTY)
                continue;
            if (Conv.isDense(a)) {
                final int denseIndex = Conv.denseIndex(a);
                if (denseIndex < 0) throw new IllegalStateException();
                if (denseIndex >= dense.length) throw new IllegalStateException();
                if (dense[denseIndex] == null) throw new IllegalStateException();
                if (dense[denseIndex].length < 1) throw new IllegalStateException();
            }
        }
        return true;
    }

    private boolean validateOutsideEndAreEmpty() throws ValidationException {
        int i;
        if (end < array.length) {
            for (i = Math.incrementExact(end); i < array.length; i++) {
                if (array[i] != EMPTY) {
                    throw new ValidationException("not empty past end at component index: " + i);
                }
            }
        }
        return true;
    }

    private boolean validateAllPossibleComponentOrEmpty() throws ValidationException {
        for (int i = 0; i <= end; i++) {
            final long a = array[i];
            if (!validatePossibleComponentOrEmpty(a))
                return false;
        }
        return true;
    }

    boolean validateInvariants() {
        try {
            if (!validateAllPossibleComponentOrEmpty()) throw new IllegalStateException();
            if (!validateMonotonicIncreasing()) throw new IllegalStateException();
            if (!validateMonotonicIncreasingByBlock()) throw new IllegalStateException();
            if (!validateContinuous()) throw new IllegalStateException();
            if (!validateEmptyMarking()) throw new IllegalStateException();
            if (!validateDenseHaveArrays()) throw new IllegalStateException();
            if (!validateOutsideEndAreEmpty()) throw new IllegalStateException();
            return true;
        } catch (ValidationException ve) {
            throw new ValidationRuntimeException(ve);
        }
    }

    private boolean setOrClear(final long position, final boolean set) {
        PosConv.requireInBoundsPosition(position);

        final long a0;

        final int block = PosConv.positionToBlock(position);
        assert validateBlock(block);
        final int componentIndex = findComponentIndex(block);
        if (componentIndex < 0)
            throw new IllegalStateException();

        // Use a block to reduce scope of variables.
        //
        // (Prevents accidental use of the wrong variables after the blocked section.)
        {
            final long a = getComponent(componentIndex);

            assert validateContainsPosition(a, position);

            if (Conv.isSparse(a)) {

                if (Conv.sparseValue(a) == set) {
                    // no changes required
                    return false;
                }

                assert findComponentIndex(block) == componentIndex;

                // simple, non-optimized split
                // TODO optimize

                final int split0componentIndex = ComponentsHelper.trySplitBefore(block, this);
                final int split1componentIndex = ComponentsHelper.trySplitAfter(block, this);

                final int postSplitComponentIndex;

                if (split1componentIndex >= 0) {
                    postSplitComponentIndex = split1componentIndex;
                } else if (split0componentIndex >= 0) {
                    postSplitComponentIndex = split0componentIndex;
                } else {
                    postSplitComponentIndex = componentIndex;
                }

                if (findComponentIndex(block) != postSplitComponentIndex) {
                    print(System.err);
                    throw new IllegalStateException();
                }
                assert findComponentIndex(block) == postSplitComponentIndex : "findComponentIndex(block): " + findComponentIndex(block) + "; postSplitComponentIndex: " + postSplitComponentIndex;
                convertToDense(postSplitComponentIndex);

                a0 = getComponent(postSplitComponentIndex);

                assert length(a0) == 1 : "length: " + length(a0);

                assert validateContainsPosition(a0, position);

            } else {
                assert Conv.isDense(a);

                // yay!  no need to split
                a0 = a;
                assert validateContainsPosition(a0, position) : "componentIndex: " + componentIndex;
            }
        }

        assert validateContainsPosition(a0, position);
        assert Conv.isDense(a0);

        final long[] denseData = denseArray(a0);
        final int startBlock = Conv.startBlock(a0);
        assert validateBlock(startBlock);
        final int offset = Math.subtractExact(block, startBlock);
        assert offset >= 0;
        assert offset <= maxBlocksPerDense();
        assert offset < denseData.length : "offset: " + offset + "; length: " + denseData.length + "; a0.startBlock: " + startBlock(a0) + "; block: " + block;
        final long d = denseData[offset];
/*
        final long[] denseData1 = denseArray(a0);
        final int startBlock1 = Conv.startBlock(a0);

        final int blockOffset = block - startBlock1;
        assert blockOffset >= 0;

        final long d = denseData1[blockOffset];
*/
        final long x = 0x1L << (int)(position % 64);

        final long d1 = set ? d | x : d & ~x;

        if (d1 != d) {
            //safd setComponent(componentIndex, d1);
            denseData[offset] = d1;
            return true;
        }
        return false;
    }

    boolean validatePossibleComponentOrEmpty(final long a) {
        if (a == 0)
            throw new ValidationRuntimeException(
                    new ValidationException("zero is not a valid representation of component"));
        return true;
    }

    boolean validateComponent(final long a) {
        validatePossibleComponentOrEmpty(a);

        try {
            if (a == EMPTY) throw new ValidationException("copmonent was empty");

            final int length = length(a);
            if (length <= 0) throw new ValidationException("length: " + length);

            final int startBlock = startBlock(a);
            if (startBlock < minBlock()) throw new ValidationException();
            if (startBlock > maxBlock()) throw new ValidationException();

            final int endBlock = endBlock(a);
            if (endBlock < minBlock()) throw new ValidationException();
            if (endBlock > maxBlock()) throw new ValidationException("endBlock: " + endBlock + "; maxBlock: " + maxBlock());

            final long startPosition = startPosition(a);
            if (startPosition < minPosition()) throw new ValidationException();
            if (startPosition > maxPosition()) throw new ValidationException();

            final long endPosition = endPosition(a);
            if (endPosition < minPosition()) throw new ValidationException();
            if (endPosition > maxPosition()) throw new ValidationException("endPosition: " + endPosition + "; maxPosition: " + maxPosition());

            if (Conv.isSparse(a)) {
                // do nothing
            } else if (Conv.isDense(a)) {
                final int denseIndex = Conv.denseIndex(a);
                if (denseIndex > MAX_DENSE_INDEX) throw new ValidationException();
                if (denseIndex < 0) throw new ValidationException();
                final long[] data = dense[denseIndex];
                if (data == null) throw new ValidationException();
                if (data.length > MAX_BLOCKS_PER_DENSE) throw new ValidationException();
                for (int i = 0; i < denseFreeSize; i++) {
                    final int di = denseFree[i];
                    if (di == denseIndex) throw new ValidationException();
                }
            } else {
                throw new ValidationException();
            }
        } catch (ValidationException ve) {
            throw new ValidationRuntimeException(ve);
        }

        return true;
    }

    boolean validateBlock(final int block) {
        try {
            if (block < minBlock()) throw new ValidationException();
            if (block > maxBlock()) throw new ValidationException();
        } catch (ValidationException ve) {
            throw new ValidationRuntimeException(ve);
        }
        return true;
    }

    boolean validatePosition(final int position) {
        try {
            if (position < minPosition()) throw new ValidationException();
            if (position > maxPosition()) throw new ValidationException();
        } catch (ValidationException ve) {
            throw new ValidationRuntimeException(ve);
        }

        return true;
    }

    public boolean set(final long position) {
        assert position >= minPosition();
        assert position <= maxPosition();

        return setOrClear(position, true);
    }

    public boolean clear(final long position) {
        assert position >= minPosition();
        assert position <= maxPosition();

        return setOrClear(position, false);
    }

    /**
     *
     * @param position
     * @return true if the data of this object was modified else false
     */
    /*
    public boolean set(final long position) {
        PosConv.requireInBoundsPosition(position);
        assert(validateInvariants());

        final int block = PosConv.positionToBlock(position);
        final int initialIndex = findComponentIndex(key);
        final long initialA = array[initialIndex];
        //System.out.println("position: " + position + ", max=" + maxPosition());
        //System.out.println("key:      " + Long.toHexString(key));
        //System.out.println("initialA: " + Long.toHexString(initialA));
        //System.out.println("initialIndex: " + Long.toHexString(initialIndex));
        //if (initialIndex > 0) {
        //    System.out.println("previous start block: " + Conv.startBlock(array[initialIndex - 1]));
        //    System.out.println("previous start position: " + Conv.startPosition(array[initialIndex - 1]));
        //    //System.out.println("previous end position  : " + Conv.startPosition(array[initialIndex - 1]));
        //}
        assert(initialIndex >= 0);
        assert(initialIndex < dataSize);
        if (Conv.isSparse(initialA)) {
            final boolean av = Conv.sparseValue(initialA);
            if (av) {
                return false;
            } else {

                {
                    final int index = findComponentIndex(block);
                    final long a = array[index];
                    //assert(print(a, System.out));

                    final int sBlock = Conv.startBlock(a);
                    final int targetBlock = PosConv.positionToBlock(position);

                    final int distanceToTargetBlockFromStart = targetBlock - sBlock;

                    // check for lower split
                    if (distanceToTargetBlockFromStart > splitThreshold()) {
                        split(index, distanceToTargetBlockFromStart - splitBleed);
                        assert(validateInvariants());
                    }
                }

                {
                    final int index = findComponentIndex(block);
                    final long a = array[index];
                    //assert(print(a, System.out));

                    final int sBlock = Conv.startBlock(a);
                    final int targetBlock = PosConv.positionToBlock(position);
                    final int len = Conv.sparseLength(a);

                    final int distanceFromTargetBlockToEnd = sBlock + len - targetBlock;

                    // check for upper split
                    if (distanceFromTargetBlockToEnd > splitThreshold()) {
                        split(index, splitThreshold + splitBleed);
                        assert(validateInvariants());
                    }
                }

                {
                    final int index = findComponentIndex(block);
                    final long a = array[index];
                    //assert(print(a, System.out));

                    //System.out.println("begin");
                    //print(System.out);
                    //System.out.println("end");

                    assert(validateInvariants());
                    convertToDense(index);
                    final long denseA = array[index];
                    assert(validateInvariants());

                    final boolean modified = setDenseByPosition(denseA, position);
                    //assert(print(denseA, System.out));
                    assert(validateInvariants());

                    return modified;
                }
            }
        } else {
            assert(Conv.isDense(initialA));

            //assert(print(initialA, System.out));
            //System.out.println(dataSize);

            assert(validateInvariants());
            return setDenseByPosition(initialA, position);
        }
    }
    */

    /**
     *
     * @param position
     * @return true if the data of this object was modified else false
     */
    /*
    public boolean clear(final long position) {
        PosConv.requireInBoundsPosition(position);
        assert(validateInvariants());

        final int block = PosConv.positionToBlock(position);
        final int initialIndex = findComponentIndex(block);
        final long initialA = array[initialIndex];
        assert(initialIndex >= 0);
        assert(initialIndex < dataSize);
        if (Conv.isSparse(initialA)) {
            final boolean av = Conv.sparseValue(initialA);
            if (!av) {
                return false;
            } else {

                {
                    final int index = findComponentIndex(block);
                    final long a = array[index];

                    final int sBlock = Conv.startBlock(a);
                    final int targetBlock = PosConv.positionToBlock(position);

                    final int distanceToTargetBlockFromStart = targetBlock - sBlock;

                    // check for lower split
                    if (distanceToTargetBlockFromStart > splitThreshold()) {
                        split(index, distanceToTargetBlockFromStart - splitBleed);
                        assert(validateInvariants());
                    }
                }

                {
                    final int index = findComponentIndex(block);
                    final long a = array[index];

                    final int sBlock = Conv.startBlock(a);
                    final int targetBlock = PosConv.positionToBlock(position);
                    final int len = Conv.sparseLength(a);

                    final int distanceFromTargetBlockToEnd = sBlock + len - targetBlock;

                    // check for upper split
                    if (distanceFromTargetBlockToEnd > splitThreshold()) {
                        split(index, splitThreshold + splitBleed);
                        assert(validateInvariants());
                    }
                }

                {
                    final int index = findComponentIndex(block);
                    final long a = array[index];

                    assert(validateInvariants());
                    convertToDense(index);
                    final long denseA = array[index];
                    assert(validateInvariants());

                    final boolean modified = clearDenseByPosition(denseA, position);
                    assert(validateInvariants());

                    return modified;
                }
            }
        } else {
            assert(Conv.isDense(initialA));
            assert(validateInvariants());
            return clearDenseByPosition(initialA, position);
        }
    }
    */

    public boolean get(final long position) {
        PosConv.requireInBoundsPosition(position);

        final int block = PosConv.positionToBlock(position);
        final int index = findComponentIndex(block);
        final long a = array[index];

        assert(index >= 0);

        if (Conv.isSparse(a)) {
            final boolean retVal = Conv.sparseValue(a);
            return retVal;
        } else {
            assert(Conv.isDense(a));
            final int denseIndex = Conv.denseIndex(a);
            final long[] data = dense[denseIndex];
            final int sBlock = Conv.startBlock(a);
            final int positionBlock = PosConv.positionToBlock(position);
            final int blockOffset = positionBlock - sBlock;
            final long d = data[blockOffset];
            final int positionOffset = (int)(position % 64);

            final long x = 0x1L << positionOffset;

            boolean retVal = (x & d) != 0;

            assert(denseIndex >= 0);
            assert(data != null);
            assert(sBlock >= 0);
            assert(positionBlock >= 0);
            assert(blockOffset >= 0);
            assert(positionOffset >= 0);

            return retVal;
        }
    }

    public LongIterator positionsIterator() {
        return new SetBitsItr(this);
    }

    @Override
    public LongIterator iterator() {
        return positionsIterator();
    }

    private Cursor perInstanceBlockTraveller = null;

    protected Cursor perInstanceBlockTraveller() {
        if (perInstanceBlockTraveller == null) {
            perInstanceBlockTraveller = newblockTraveller();
        } else {
            perInstanceBlockTraveller.reset();
        }
        return perInstanceBlockTraveller;
    }

    protected Cursor newblockTraveller() {
        return new Cursor(this);
    }

    private enum Op {
        AND, OR, XOR
    }

    /**
     * A wrapper to permit assert mode specific functionality.
     */
    private abstract class AssertRunnable implements Runnable {
        public boolean arun() {
            run();
            return true;
        }
    }

    public boolean equals(final BitSet obs, final int startPosition, final int endPosition) {
        Objects.requireNonNull(obs);
        if (startPosition < 0)
            throw new IllegalArgumentException();
        if (endPosition < 0)
            throw new IllegalArgumentException();
        if (startPosition > MAX_POSITION)
            throw new UnsupportedOperationException();
        if (endPosition > MAX_POSITION)
            throw new UnsupportedOperationException();

        final LongIterator posItr = positionsIterator();
        while (posItr.hasNext()) {
            final long p = posItr.nextLong();
            if (p > Integer.MAX_VALUE)
                throw new IllegalStateException();
            if (!(p >= startPosition && p <= endPosition))
                continue;
            if (!obs.get((int)p))
                return false;
        }
        for (int i = startPosition; i >= 0 && i <= endPosition; i = obs.nextSetBit(i)) {
            if (!get((long)i))
                return false;
        }
        return true;
    }

    public boolean equals(final BitSet obs, final long startPosition, final long endPosition) {
        if (startPosition > Integer.MAX_VALUE)
            throw new UnsupportedOperationException();
        if (endPosition > Integer.MAX_VALUE)
            throw new UnsupportedOperationException();

        return equals(obs, (int) startPosition, (int) endPosition);
    }

    public void set(final BitSet obs, final int startPosition, final int endPosition) {
        Objects.requireNonNull(obs);
        if (startPosition < 0)
            throw new IllegalArgumentException();
        if (endPosition < 0)
            throw new IllegalArgumentException();
        if (startPosition > MAX_POSITION)
            throw new UnsupportedOperationException();
        if (endPosition > MAX_POSITION)
            throw new UnsupportedOperationException();

        for (int i = startPosition; i >= 0 && i <= endPosition; i = obs.nextSetBit(i)) {
            set((long)i);
        }
    }

    public void set(final BitSet obs, final long startPosition, final long endPosition) {
        if (startPosition > Integer.MAX_VALUE)
            throw new UnsupportedOperationException();
        if (endPosition > Integer.MAX_VALUE)
            throw new UnsupportedOperationException();

        set(obs, (int) startPosition, (int) endPosition);
    }

    public void set(final BitSet obs) {
        set(obs, 0, Integer.MAX_VALUE);
    }

    public void clear(final BitSet obs, final int startPosition, final int endPosition) {
        Objects.requireNonNull(obs);
        if (startPosition < 0)
            throw new IllegalArgumentException();
        if (endPosition < 0)
            throw new IllegalArgumentException();
        if (startPosition > MAX_POSITION)
            throw new UnsupportedOperationException();
        if (endPosition > MAX_POSITION)
            throw new UnsupportedOperationException();

        for (int i = startPosition; i >= 0 && i <= endPosition; i = obs.nextSetBit(i)) {
            clear((long)i);
        }
    }

    public void clear(final BitSet obs, final long startPosition, final long endPosition) {
        if (startPosition > Integer.MAX_VALUE)
            throw new UnsupportedOperationException();
        if (endPosition > Integer.MAX_VALUE)
            throw new UnsupportedOperationException();

        clear(obs, (int) startPosition, (int) endPosition);
    }

    public void clear(final BitSet obs) {
        clear(obs, 0, Integer.MAX_VALUE);
    }


    /**
     * Modifications should only occur on a single instance of the object at once, so it
     * should be safe to reuse the cursor.
     */
    protected Cursor s0Cursor() {
        return perInstanceBlockTraveller();
    }

    protected Cursor s1Cursor() {
        return newblockTraveller();
    }

    static enum OpResult {
        TRUE, FALSE, INDETERMINATE
    }

    static class And extends Operation {

        @Override
        protected boolean noChanges(Cursor s0, Cursor s1) {
            //if (s1.inSparse()) {
            //    if (s1.getSparseValue()) {
            //        return true;
            //    }
            //}
            // TODO implement
            return false;
        }

        @Override
        protected boolean noChange1(boolean v1) {
            return v1;
        }

        @Override
        protected boolean noChange1(long v1) {
            return v1 == 0xFFFFFFFFFFFFFFFFL;
        }

        @Override
        public boolean op(final boolean v0, final boolean v1) {
            return v0 && v1;
        }

        @Override
        public OpResult op(final boolean v0, final long v1) {
            if (!v0)
                return OpResult.FALSE;
            else if (v1 == ALL_CLEAR)
                return OpResult.FALSE;
            else if (v1 == ALL_SET)
                return OpResult.TRUE;
            else
                return OpResult.INDETERMINATE;
        }

        @Override
        public long op(final long v0, final boolean v1) {
            return !v1 ? ALL_CLEAR : v0;
        }

        @Override
        public long op(final long v0, final long v1) {
            return v0 & v1;
        }
    }

    void op(final SBS3 o, final Operation op) {

        final Cursor s0 = s0Cursor();
        final Cursor s1 = o.s1Cursor();
        final boolean hasFirstPosition0 = s0.nextComponent();
        final boolean hasFirstPosition1 = s1.nextComponent();

        if (!hasFirstPosition0 && !hasFirstPosition1) {
            // nothing to do
            return;
        } else if (!hasFirstPosition0 || !hasFirstPosition1) {
            throw new UnsupportedOperationException("no handling for this case yet");
        }

        assert hasFirstPosition0;
        assert hasFirstPosition1;

        /**
         * Loop over each component.  If there is a change, execute accordingly.
         *
         * Will always call op.XX on new component boundaries.  The op may call
         * methods to determine if this is also a change boundary.
         */

        for (;;) {

            /* Change misalignment:
             *    { s0 }
             *            { s1 }
             */
            if (s0.endsBeforeStartOf(s1)) {
                s0.nextComponent();
                continue;
            }

            /* Change misalignment:
             *            { s0 }
             *    { s1 }
             */
            if (s1.endsBeforeStartOf(s0)) {
                s1.nextComponent();
                continue;
            }

            final int startBlock = s0.startsBeforeStartOf(s1) ? s1.getStartBlock() : s0.getStartBlock();
            final int endBlock = s0.endsBeforeEndOf(s1) ? s0.getEndBlock() : s1.getEndBlock();

            assert startBlock >= minBlock();
            assert startBlock <= maxBlock();
            assert endBlock >= minBlock();
            assert endBlock <= maxBlock();

            /*
            // Do operations
            if (s0.inSparse()) {
                if (s1.inSparse()) {
                    op.ss(s0, s1, startBlock, endBlock);
                } else {
                    assert s1.inDense();
                    op.sd(s0, s1, startBlock, endBlock);
                }
            } else {
                assert s0.inDense();
                if (s1.inSparse()) {
                    op.ds(s0, s1, startBlock, endBlock);
                } else {
                    assert s1.inDense();
                    op.dd(s0, s1, startBlock, endBlock);
                }
            }*/

            op.op(s0, s1, startBlock, endBlock);

            // Advance
            if (s0.peekNextComponentEndBlock() > s1.peekNextComponentEndBlock()) {
                if (s0.nextComponent())
                    continue;
                if (s1.nextComponent())
                    continue;
            } else {
                if (s1.nextComponent())
                    continue;
                if (s0.nextComponent())
                    continue;
            }
            assert !s0.hasNextComponent() && !s1.hasNextComponent();
            return;
        }
    }

    private final static And AND_OP = new And();

    public void and(final SBS3 o) {
        op(o, AND_OP);
    }

//    public void or(final SBS3 o) {
//
//    }
//
//    public void not(final SBS3 o) {
//
//    }
//
//    public void xor(final SBS3 o) {
//
//    }
//
//    public void clear() {
//
//    }

    /**
     * Print component to support debugging.
     *
     * @param a
     * @param out
     * @return
     */
    protected boolean print(final long a, final PrintStream out) {

        final int sBlock = Conv.startBlock(a);
        final int zBlock = endBlock(a);
        out.print("--" + (Conv.isSparse(a) ? "sparse" : "dense") + " component:  raw hex value: ");
        out.print(Long.toHexString(a));
        out.print(", block range: [");
        out.print(sBlock);
        out.print("..");
        out.print(zBlock);
        out.print("], position range: [");
        out.print(Conv.startPosition(a));
        out.print("..");
        out.print(endPosition(a));
        out.println(']');

        if (Conv.isSparse(a)) {
            final int len = Conv.sparseLength(a);
            final boolean av = Conv.sparseValue(a);
            out.print("  value: ");
            out.print(av ? '1' : '0');
        } else {
            if (!Conv.isDense(a))
                throw new IllegalStateException();
            final int denseIndex = Conv.denseIndex(a);
            final long[] data = dense[denseIndex];
            out.print("  denseIndex: ");
            out.print(denseIndex);
            out.println();
            out.println("  blocks:");
            for (int j = 0; j < data.length; j++) {
                final String hex = Long.toHexString(data[j]);
                out.print("    block: ");
                out.print(sBlock + j);
                out.print(' ');
                out.print("    hex value: ");
                out.println(hex);
                out.print("    values: {");
                String v = null;
                for (int k = 0; k < 64; k++) {
                    final long mask = 1L << k;
                    final long x = data[j] & mask;
                    assert x >= 0;
                    if (x != 0) {
                        if (v != null) {
                            out.print(v);
                            out.print(", ");
                        }
                        v = Integer.toString(k);
;                   }
                }
                if (v != null)
                    out.print(v);
                out.println('}');
            }
        }
        return true;
    }

    /**
     * Print all components to support debugging.
     *
     * @param out
     */
    public void print(final PrintStream out) {
        //out.println("end: " + end);
        for (int i = 0; i <= end; i++) {
            final long a = array[i];
            if (a == EMPTY)
                continue;
            //System.out.println("a: " + Long.toHexString(a));
            out.println("component at index " + i + ":");
            print(a, out);
            out.println();
        }
    }

    int size = 0;

    void incrementSize() {
        size++;
    }

    void decrementSize() {
        size--;
    }

    /**
     * The maximum component index.  There must always be at least one component.
     */
    int end;

    int findComponentIndex(final int block) {
        assert validateBlock(block);
        return ComponentsHelper.find(block, this);
    }

    public boolean contains(final long a) {
        final int componentIndex = ComponentsHelper.find(startBlock(a), this);
        return componentIndex >= 0;
    }

    /*
    public boolean delete(final long a) {
        return ComponentsHelper.delete(a, this);
    }
    */

    public void insert(final long a) {
        ComponentsHelper.insert(a, 5, 1.25f, this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
      /*  sb.append(
                "SBS3{" +
                "array=" + Arrays.toString(array) +
                ", dense=" + Arrays.toString(dense) +
                ", denseSize=" + denseSize +
                ", denseFree=" + Arrays.toString(denseFree) +
                ", denseFreeSize=" + denseFreeSize +
                ", splitThreshold=" + splitThreshold +
                ", splitBleed=" + splitBleed +
                ", perInstanceBlockTraveller=" + perInstanceBlockTraveller +
                ", size=" + size +
                ", end=" + end +
                '}'
        );
        */

        sb.append("\n");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        print(ps);
        final String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        sb.append(content);

        sb.append("\n");

        return sb.toString();

    }
}
