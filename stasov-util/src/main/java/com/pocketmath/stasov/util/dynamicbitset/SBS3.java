package com.pocketmath.stasov.util.dynamicbitset;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.nicoulaj.compilecommand.annotations.Inline;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.BitSet;
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
 * Created by etucker on 4/8/16.
 */
public class SBS3 implements LongIterable {

    long[] array;
    int dataSize;

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

    private static final long MIN_POSITION = 0L;
    private static final long MAX_POSITION = 64L * ((long)MAX_BLOCK - 1);
    private static final int MAX_DENSE_INDEX = MAX_BLOCK;

    private static final int MAX_BLOCKS_PER_DENSE = 32768;
    private static final int INITIAL_BLOCKS_PER_DENSE = 16;

    public static int minBlock() {
        return MIN_BLOCK;
    }

    public static int maxBlock() {
        return MAX_BLOCK;
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
        this.array[0] = Conv.createSparse(0, maxBlock(), false);
        if (array.length > 1) Arrays.fill(array, 1, capacity, EMPTY);
        this.dataSize = 1;
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

    long getComponent(final int index) {
        return array[index];
    }

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
            assert(a != EMPTY);
            final long retVal = PosConv.blockToStartPosition(startBlock(a));
            assert retVal >= minPosition();
            assert retVal <= maxPosition();
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
            if (length < 0) throw new IllegalArgumentException();
            if (length > maxBlock()) throw new IllegalArgumentException();
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
            if (aboveMaxPosition(position) || position < 0)
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
        assert(a != EMPTY);
        if (Conv.isSparse(a)) {
            return Conv.sparseLength(a);
        } else {
            assert(Conv.isDense(a));
            return fastDenseLength(a);
        }
    }

    @Inline
    protected int startBlock(final long a) {
        return Conv.startBlock(a);
    }

    @Inline
    protected int endBlock(final long a) {
        return Conv.startBlock(a) + length(a);
    }

    @Inline
    protected long startPosition(final long a) {
        return Conv.startPosition(a);
    }

    @Inline
    protected long endPosition(final long a) {
        return Conv.startPosition(a) + (long)length(a) * 64L - 1L;
    }

    protected int splitThreshold() {
        return splitThreshold;
    }

    long[] denseArray(final long a) {
        assert Conv.isDense(a);
        final int denseIndex = Conv.denseIndex(a);
        final long[] denseArray = dense[denseIndex];
        assert denseArray != null;
        return denseArray;
    }

    long createDenseNoPrefill(final int startBlock, final int length) {
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
        dense[denseIndex] = null;
        if (denseFreeSize >= denseFree.length) {
            assert(denseSize == dense.length);
            denseFree = IntArrays.grow(denseFree, Math.max((int)(denseFree.length * 1.5), denseFree.length + 8));
        }
        denseFree[denseFreeSize++] = denseIndex;
    }

    protected void convertToDense(final int index) {
        assert(validateInvariants());
        final long sparse = array[index];
        assert(sparse != EMPTY);
        assert(!Conv.isDense(sparse));
        final int startBlock = Conv.startBlock(sparse);
        assert(startBlock < maxBlock());
        final int len = Conv.sparseLength(sparse);
        if (len > maxBlocksPerDense()) {
            print(System.err);
            throw new IllegalStateException("too many blocks; please split before converting to dense; len: " + len);
        }
        assert(len > 0);
        final boolean sparseValue = Conv.sparseValue(sparse);
        assert(validateContinuous());
        final int denseIndex = createDense(len, sparseValue);
        assert(dense[denseIndex] != null);
        assert(dense[denseIndex].length == len);
        final long denseA = Conv.createDense(startBlock, denseIndex);
        assert(dense[denseIndex].length == len);
        assert(Conv.isDense(denseA));
        assert(Conv.startBlock(denseA) == startBlock);
        assert(Conv.denseIndex(denseA) == denseIndex);

        array[index] = denseA;
        assert(validateInvariants());
    }

    /*
    protected int findIndex_(final long a) {
        assert a != EMPTY;
        int index = LongArrays.binarySearch(array, 0, dataSize, a);
        //System.out.println("dataSize: " + dataSize);

        index = index < 0 ? Math.max(0,-(index + 2)) : index;

        // TODO: optimize
        if (index + 1 < dataSize) {
            if (Conv.startBlock(array[index + 1]) == Conv.startBlock(a)) {
                index++;
            }
        }

        if (index < 0) throw new IllegalStateException("index:" + index + " at a:" + Long.toHexString(a));
        assert(index < dataSize);

        return index;
    }

    */

    /*
    protected void clearSparse(final int index) {
        final long a = array[index];
        assert(Conv.isSparse(a));
        if (!Conv.sparseValue(a)) return;
        final int startBlock = Conv.startBlock(a);
        final int length = Conv.sparseLength(a);
        final long aNew = Conv.createSparse(startBlock, length, false);
        array[index] = aNew;
    }

    protected void setSparse(final int index) {
        final long a = array[index];
        assert(Conv.isSparse(a));
        if (!Conv.sparseValue(a)) return;
        final int startBlock = Conv.startBlock(a);
        final int length = Math.abs(Conv.sparseLength(a));
        final long aNew = Conv.createSparse(startBlock, length, true);
        array[index] = aNew;
    }

    protected void clearDense(final int index) {
        final long a = array[index];
        assert(Conv.isDense(a));
        final int denseIndex = Conv.denseIndex(a);
        final long[] denseArray = dense[denseIndex];
        Arrays.fill(denseArray, ALL_CLEAR);
    }

    private int denseIsAll(final int index) {
        final long a = array[index];
        final int denseIndex = Conv.denseIndex(a);
        final long[] data = dense[denseIndex];
        boolean allClear = true, allSet = true;
        int i;
        for (i = 0; i < data.length && (allClear || allSet); i++) {
            final long x = data[i];

            if (allSet) {
                if (x == ALL_CLEAR) {
                    allSet = false;
                    continue;
                }
            }

            if (allClear) {
                if (x == ALL_SET) {
                    allClear = false;
                    continue;
                }
            }

            allClear = false;
            allSet = false;
        }
        if (allClear) return 0;
        if (allSet) return 1;
        return -1;
    }

    private int denseIsAll(final int index, final int zBlock) {
        final long a = array[index];
        final int startBlock = Conv.startBlock(a);
        final int denseIndex = Conv.denseIndex(a);
        final long[] data = dense[denseIndex];
        boolean allClear = true, allSet = true;
        final int k = zBlock - startBlock;
        if (k < 0) throw new IllegalStateException();
        if (k > data.length) throw new IllegalStateException();
        int i;
        for (i = 0; i < k && (allClear || allSet); i++) {
            final long x = data[i];

            if (allSet) {
                if (x == ALL_CLEAR) {
                    allSet = false;
                    continue;
                }
            }

            if (allClear) {
                if (x == ALL_SET) {
                    allClear = false;
                    continue;
                }
            }

            allClear = false;
            allSet = false;
        }
        if (allClear) return 0;
        if (allSet) return 1;
        return -1;
    }
*/
    /*
    private void ensureArrayCapacity(
            final int newMinimumCapacity,
            final float spareBlocksMultiplier,
            final int minSpareBlocks,
            final int maxSpareBlocks) {
        assert(newMinimumCapacity >= 0);
        if (newMinimumCapacity <= array.length) return;
        final int prevLen = array.length;
        final int spareBlocks = Math.min(Math.max((int)(newMinimumCapacity * spareBlocksMultiplier), minSpareBlocks), maxSpareBlocks);
        final int newLength = newMinimumCapacity + spareBlocks;
        assert(newLength > array.length);
        assert(newLength >= newMinimumCapacity);
        assert(validateInvariants());
        array = LongArrays.grow(array, newLength);
        assert(array.length >= newMinimumCapacity);
        Arrays.fill(array, prevLen, array.length, EMPTY);
        assert(validateInvariants());
    }

    private void ensureArrayCapacity(final int newCapacity) {
        ensureArrayCapacity(newCapacity, 1.2F, 4, 8192);
    }

    private void ensureArrayCapacityNoSpare(final int newCapacity) {
        assert(newCapacity >= 0);
        if (newCapacity <= array.length) return;
        final int prevLen = array.length;
        final int growth = newCapacity - array.length;
        array = LongArrays.grow(array, growth);
        LongArrays.fill(array, prevLen, array.length, EMPTY);
    }
    */

    public void readOptimize() {
        assert(validateInvariants());

        LongArrays.trim(array, dataSize);

        final long[][] newDense = new long[denseSize][];
        int newDenseSize = 0;

        for (int i = 0; i < dataSize; i++) {
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
        LongArrays.trim(array, Math.max(256, Math.multiplyExact(dataSize, 2)));
        ObjectArrays.trim(dense, Math.max(128, Math.multiplyExact(denseSize, 2)));
        IntArrays.trim(denseFree, Math.max(128, Math.multiplyExact(denseFreeSize,2)));
    }

    /*
    private void sortArray() {
        assert(dataSize <= array.length);

        long c0 = -1;
        assert((c0 = Arrays.stream(array).filter(a -> a != EMPTY).count()) >= 0);

        Arrays.sort(array, 0, dataSize);

        long c1 = -1;
        assert((c1 = Arrays.stream(array).filter(a -> a != EMPTY).count()) >= 0);
        assert(c0 == c1);

        assert(validateInvariants());
    }
    */

    /*
    private void splitMultiple(final int index, final int zBlock, final int numBlocks) {
        assert(findIndex(zBlock) > index);
        final int newSize = dataSize + numBlocks - 1;
        ensureArrayCapacity(newSize);
        final long a = array[index];
        if (Conv.isSparse(a)) {
            final int sBlock = Conv.startBlock(a);
            final int distance = zBlock - sBlock;
            if (distance < numBlocks) throw new IllegalStateException();
            final int interval = Math.max(1, Math.floorDiv(distance, numBlocks));
            final boolean set = Conv.sparseValue(a);
            array[index] = EMPTY;
            final int n = index + numBlocks;
            int _sBlock = sBlock;
            for (int i = index; i < n; i++) {
                final int _len;
                if (interval + _sBlock >= zBlock) {
                    _len = zBlock - (interval + _sBlock);
                } else {
                    _len = interval;
                }
                array[i + dataSize] = Conv.createSparse(_sBlock, _len, set);
                _sBlock += interval;
            }
            dataSize = newSize;
            sortArray();
        } else {
            // TODO not yet implemented
            throw new IllegalStateException("not yet implemented");
        }
    }
*/

    private int split(final int index, final int numBlocksDistance) {
        assert(validateInvariants());
        final long a0 = array[index];
        if (!Conv.isSparse(a0)) throw new IllegalArgumentException();
        final int a0len = Conv.sparseLength(a0);
        if (a0len < numBlocksDistance) return 0;
        final int a0StartBlock = Conv.startBlock(a0);
        final int a0EndBlock = a0StartBlock + a0len - 1;
        final boolean set = Conv.sparseValue(a0);
        final long new0, new1;
        assert(index+1 < dataSize ? Conv.startBlock(array[index+1]) == a0EndBlock + 1 : true);
        new0 = Conv.createSparse(a0StartBlock, numBlocksDistance, set);
        final int len1 = a0len - numBlocksDistance;
        new1 = Conv.createSparse(a0StartBlock + numBlocksDistance, len1, set);
        ensureArrayCapacity(dataSize+1);
        array[index] = new0;
        array[dataSize] = new1;
        dataSize++;
        sortArray();
        assert(validateInvariants());
        return numBlocksDistance;
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
        for (int i = 0; i < dataSize; i++) {
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
        for (int i = 0; i < dataSize; i++) {
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

        for (int i = 0; i < dataSize; i++) {
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
        if (Arrays.stream(array, 0, dataSize).anyMatch( a -> a == EMPTY )) throw new IllegalStateException();
        if (Arrays.stream(array, dataSize, array.length).anyMatch( a -> a != EMPTY )) throw new IllegalStateException();
        return true;
    }

    private boolean validateDenseHaveArrays() {
        for (int i = 0; i < dataSize; i++) {
            final long a = array[i];
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

    private boolean validateInvariants() {
        if (!validateMonotonicIncreasing()) throw new IllegalStateException();
        if (!validateMonotonicIncreasingByBlock()) throw new IllegalStateException();
        if (!validateContinuous()) throw new IllegalStateException();
        if (!validateEmptyMarking()) throw new IllegalStateException();
        if (!validateDenseHaveArrays()) throw new IllegalStateException();
        return true;
    }

    /**
     *
     * @param position
     * @return true if the data of this object was modified else false
     */
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

    /**
     *
     * @param position
     * @return true if the data of this object was modified else false
     */
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

    public boolean get(final long position) {
        PosConv.requireInBoundsPosition(position);

        final long block = PosConv.positionToBlock(position);
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

            //System.out.println("_denseIndex:" + denseIndex);
            //System.out.println("_positionOffset:" + positionOffset);
            //System.out.println("_d:" + d);
            //System.out.println("_x:" + x);

            //assert(print(a,System.out));

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

    /**
     * No cursors are advanced by this method.
     *
     * @param s0
     * @param s1
     * @param startBlock
     * @param endBlock
     */
    private void mergeAnd(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
        assert(startBlock <= endBlock);

        BitSet _obs0 = null, _obs1 = null;
        assert((_obs0 = new BitSet()) != null);
        assert((_obs1 = new BitSet()) != null);
        final BitSet obs0 = _obs0, obs1 = _obs1;
        assert(new AssertRunnable() {
            public void run() {
                final long startPos = PosConv.blockToStartPosition(startBlock);
                final long endPos = PosConv.blockToEndPosition(endBlock);
                s0.getCore().set(obs0, startPos, endPos);
                s1.getCore().set(obs1, startPos, endPos);
            }
        }.arun());

        int ai0 = s0.getChangeComponentIndex(), ai1 = s1.getChangeComponentIndex();

        loop1: while (true) {

            assert(ai0 >= 0);
            assert(ai1 >= 0);

            final long a0 = s0.getBlock(ai0);
            final int startBlock0 = Conv.startBlock(a0);
            final int endBlock0 = endBlock(a0); // TODO: optimize

            final long a1 = s1.getBlock(ai1);
            final int startBlock1 = Conv.startBlock(a1);
            final int endBlock1 = endBlock(a1); // TODO: optimize

            assert(startBlock0 >= startBlock);
            assert(startBlock0 <= endBlock);
            assert(startBlock1 >= startBlock);
            assert(startBlock1 <= endBlock);

            assert(startBlock0 <= endBlock1 || startBlock1 <= endBlock0);

            final int startOffset0 = startBlock - startBlock0;
            final int endOffset0 = endBlock - endBlock0; // TODO: optimize
            final int len0 = endOffset0 - startOffset0 + 1;

            final int startOffset1 = startBlock - startBlock1;
            final int endOffset1 = endBlock - endBlock1; // TODO: optimize
            final int len1 = endOffset1 - startOffset1 + 1;

            final int minLen = Math.min(len0, len1);

            if (Conv.isDense(a0) && Conv.isDense(a1)) {
                final long[] denseData0 = dense[Conv.denseIndex(a0)];
                final long[] denseData1 = dense[Conv.denseIndex(a1)];

                for (int i = 0; i < minLen; i++) {
                    final int i0 = i + startOffset0;
                    final int i1 = i + startOffset1;
                    final long d0 = denseData0[i0];
                    final long d1 = denseData1[i1];
                    final long r = d0 & d1;
                    denseData0[i0] = r;
                }

            } else if (Conv.isDense(a0) && Conv.isSparse(a1)) {
                if (!Conv.sparseValue(a1)) {
                    final long[] denseData0 = dense[Conv.denseIndex(a0)];
                    Arrays.fill(denseData0, startOffset0, startOffset0 + minLen, EMPTY);
                }

            } else if (Conv.isSparse(a0) && Conv.isSparse(a1)) {
                if (Conv.sparseValue(a0) && !Conv.sparseValue(a1)) {
                    if (len0 > minLen) {
                        split(ai0, minLen);
                    } else assert minLen == len0;
                    assert(minLen <= len0);
                    final long newA0 = Conv.createSparse(Conv.startBlock(a0), minLen, false);
                    array[ai0] = newA0;
                }

            } else if (Conv.isSparse(a0) && Conv.isDense(a1)) {
                if (Conv.sparseValue(a0)) {
                    if (len0 > minLen) {
                        split(ai0, minLen);
                    } else assert minLen == len0;
                    assert(minLen <= len0);
                    convertToDense(ai0);
                    final long newA0 = array[ai0];
                    final long[] denseData0 = dense[Conv.denseIndex(newA0)];
                    final long[] denseData1 = dense[Conv.denseIndex(a1)];

                    for (int i = 0; i < minLen; i++) {
                        final int i0 = i + startOffset0;
                        final int i1 = i + startOffset1;
                        final long d0 = denseData0[i0];
                        final long d1 = denseData1[i1];
                        final long r = d0 & d1;
                        denseData0[i0] = r;
                    }
                }
            }

            if (startBlock0 + minLen >= endBlock) {
                assert startBlock0 + minLen == endBlock;
                break loop1;
            } else {
                final long b0 = s0.getBlock(ai0);
                final long b1 = s1.getBlock(ai1);

                if (endBlock0 == endBlock1) {
                    ai0++;
                    ai1++;
                } else if (endBlock0 < endBlock1) {
                    ai0++;
                } else {
                    ai1++;
                }

                if (ai0 > s0.maxIndex() || ai1 > s1.maxIndex()) {
                    break loop1;
                }
            }
        }

        assert(new AssertRunnable() {
            public void run() {

                obs0.and(obs1);

                final long startPos = PosConv.blockToStartPosition(startBlock);
                final long endPos = PosConv.blockToEndPosition(endBlock);
                assert s0.getCore().equals(obs0, startPos, endPos);
                assert s1.getCore().equals(obs1, startPos, endPos);
            }
        }.arun());
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

        public boolean noChanges(final boolean v0, final boolean v1) {
            return !v0 || v1;
        }

        public boolean op(final boolean v0, final boolean v1) {
            return v0 && v1;
        }

        public OpResult op(final boolean v0, final long v1) {
            if (v1 == ALL_CLEAR)
                return op(v0, false) ? OpResult.TRUE : OpResult.FALSE;
            else if (v1 == ALL_SET)
                return op(v0, true) ? OpResult.TRUE : OpResult.FALSE;
            else
                return OpResult.INDETERMINATE;
        }

        public long op(final long v0, final boolean v1) {
            return !v1 ? ALL_CLEAR : v0;
        }

        public long op(final long v0, final long v1) {
            return v0 & v1;
        }
/*
        @Override
        public void g(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
            super.ss(s0, s1, startBlock, endBlock);

            if (noChanges(s0, s1)) {
                // no changes necessary
                return;
            }

            lowerSplit(s0, s1, startBlock, endBlock);
            lookaheadWithUpperSplit(s0, s1, startBlock, endBlock);

            assert Conv.startBlock(s0.getComponent()) == startBlock;
            assert s0.getCore().length(s0.getComponent()) == endBlock - startBlock;

            op(s0, s1);
        }
        */
    }

        /*

        @Override
        public void ss(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
            super.ss(s0, s1, startBlock, endBlock);

            {
                final long a0 = s0.getComponent(), a1 = s1.getComponent();
                final boolean v0 = Conv.sparseValue(a0), v1 = Conv.sparseValue(a1);

                if (noChanges(v0, v1)) {
                    // no changes necessary
                    return;
                }
            }

            lowerSplit(s0, s1, startBlock, endBlock);
            lookaheadWithUpperSplit(s0, s1, startBlock, endBlock);

            final long a0 = s0.getComponent(), a1 = s1.getComponent();
            final boolean v0 = Conv.sparseValue(a0), v1 = Conv.sparseValue(a1);
            assert Conv.startBlock(a0) == startBlock;
            assert Conv.sparseLength(a0) == endBlock - startBlock;

            final int newLength0 = endBlock - startBlock;
            final boolean newValue0 = op(v0, v1);

            final long newA0 = Conv.createSparse(startBlock, newLength0, newValue0);
            s0.setComponent(newA0);
        }

        @Override
        public void sd(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
            super.sd(s0, s1, startBlock, endBlock);

        }

        @Override
        public void ds(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
            super.ds(s0, s1, startBlock, endBlock);

        }

        @Override
        public void dd(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
            super.dd(s0, s1, startBlock, endBlock);

        }
    }
    */

    void op(final SBS3 o, final Operation op) {

        final Cursor s0 = s0Cursor();
        final Cursor s1 = s1Cursor();

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
                    return;
                if (s1.nextComponent())
                    return;
                return;
            } else {
                if (s1.nextComponent())
                    return;
                if (s0.nextComponent())
                    return;
                return;
            }

        }
    }

    /*
    public void op1(final SBS3 o, final Op op) {

        final Cursor s0 = perInstanceBlockTraveller();
        final Cursor s1 = o.newblockTraveller();

        s0.nextBlock();
        s1.nextBlock();

        loop0:
        while (true) {

            // if there is no overlap, then fast forward until there is
            loop1:
            while (true) {
                if (s0.isPastEnd() || s1.isPastEnd()) {
                    return;
                } else if (s1.getCurrentChangeBlock() < s0.getPreviousChangeBlock()) {
                    int changeIndex = s1.nextBlock();
                    if (changeIndex < 0) {
                        return;
                    }
                    break loop1;
                } else if (s1.getCurrentChangeBlock() > s0.getCurrentChangeBlock()) {
                    int changeIndex = s0.nextBlock();
                    if (changeIndex < 0) {
                        return;
                    }
                    break loop1;
                } else {
                    s0.nextBlock();
                    s1.nextBlock();
                }
            }

            // detect overlap
            int i = Math.max(s0.getPreviousChangeBlock(), s1.getPreviousChangeBlock());
            int j = Math.min(s0.getCurrentChangeBlock(), s1.getCurrentChangeBlock());

            // consider the nature of the overlap
            final boolean s0pv = s0.getPreviousValue(), s1pv = s1.getPreviousValue();
            switch (op) {
                case AND: {
                    if (s0pv == s1pv) {
                        // do nothing
                    } else if (!s0pv && s1pv) {
                        // do nothing
                    } else if (s0pv && !s1pv) {
                        mergeAnd(s0, s1, i, j);
                    }
                    break;
                }
            }
        }
    }
    */

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

    protected boolean print(final long a, final PrintStream out) {
        if (Conv.isSparse(a)) {
            final int sBlock = Conv.startBlock(a);
            final int len = Conv.sparseLength(a);
            final int zBlock = endBlock(a);
            final boolean av = Conv.sparseValue(a);
            out.print("s:");
            out.print(Long.toHexString(a));
            out.print(" ");
            out.print(av ? '1' : '0');
            out.print(":[");
            out.print(sBlock);
            out.print("..");
            out.print(zBlock);
            out.print("]  pos start: ");
            out.print(Conv.startPosition(a));
            out.print("  pos end: ");
            out.print(endPosition(a));
            out.println(']');
        } else {
            assert(Conv.isDense(a));
            final int sBlock = Conv.startBlock(a);
            final int denseIndex = Conv.denseIndex(a);
            final long[] data = dense[denseIndex];
            out.print("d:");
            out.print(Long.toHexString(a));
            out.print("  pos start: ");
            out.print(Conv.startPosition(a));
            out.print("  pos end: ");
            out.print(endPosition(a));
            out.println();
            for (int j = 0; j < data.length; j++) {
                final String hex = Long.toHexString(data[j]);
                out.print(sBlock + j);
                out.print(' ');
                out.println(hex);
            }
        }
        return true;
    }

    public void print(final PrintStream out) {
        out.println("array data size: " + dataSize);
        for (int i = 0; i < dataSize; i++) {
            final long a = array[i];
            if (a == EMPTY)
                continue;
            //System.out.println("a: " + Long.toHexString(a));
            out.print(i+":");
            print(a, out);
        }
    }

    int size = 0;

    void incrementSize() {
        size++;
    }

    void decrementSize() {
        size--;
    }


    int end = 0;

    int findComponentIndex(final int block) {
        assert block >= 0;
        return ComponentsHelper.find(block, this);
    }

    public boolean contains(final long a) {
        final int componentIndex = ComponentsHelper.find(startBlock(a), this);
        return componentIndex >= 0;
    }

    public boolean delete(final long a) {
        return ComponentsHelper.delete(a, this);
    }

    public void insert(final long a) {
        ComponentsHelper.insert(a, 5, 1.25f);
    }

}
