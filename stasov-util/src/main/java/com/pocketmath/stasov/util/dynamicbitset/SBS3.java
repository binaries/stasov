package com.pocketmath.stasov.util.dynamicbitset;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrays;

import java.io.PrintStream;
import java.util.Arrays;

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
public class SBS3 {

    private long[] array;
    private int dataSize;

    private static long EMPTY = Long.MAX_VALUE;

    private long[][] dense;
    private int denseSize;
    private int[] denseFree;
    private int denseFreeSize;

    private int splitThreshold;
    private int splitBleed;

    private static long ALL_CLEAR = 0L;
    private static long ALL_SET = 0xFFFFFFFFFFFFFFFFL;

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

    protected static class Conv {

        static boolean isSparse(final long a) {
            assert(a != EMPTY);
            return (a & 0x1L) == 0x0L;
        }

        static boolean isDense(final long a) {
            assert(a != EMPTY);
            return (a & 0x1L) == 0x1L;
        }

        static int startBlock(final long a) {
            assert(a != EMPTY);
            return (int) (a >> 32);
        }

        static long startPosition(final long a) {
            assert(a != EMPTY);
            return PosConv.blockToStartPosition(startBlock(a));
        }

        static int sparseLength(final long a) {
            assert(a != EMPTY);
            return ((int) a) >> 2;
        }

        static int denseIndex(final long a) {
            assert(a != EMPTY);
            return ((int) a) >> 2;
        }

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

        static long blockToStartPosition(final int block) {
            return ((long)block) * 64L;
        }

        static long positionToKey(final long position) {
            assert(position <= maxPosition());

            final int block = positionToBlock(position);
            //System.out.println("positionToBlock result: " + block);

            final long retVal = ((long)block) << 32;
            assert(retVal >= 0);
            return retVal;
        }

        static boolean aboveMaxPosition(final long position) {
            return position > maxPosition();
        }

        static void requireInBoundsPosition(final long position) {
            if (aboveMaxPosition(position) || position < 0)
                throw new IllegalArgumentException();
        }

    }

    private static class BitUtil {

        public static long flip(final long a) {
            return a ^ 0xFFFFFFFFFFFFFFFFL;
        }

    }

    protected int fastDenseLength(final long a) {
        assert(a != EMPTY);
        final int denseIndex = Conv.denseIndex(a);
        final long[] data = dense[denseIndex];
        return data.length;
    }

    protected int length(final long a) {
        assert(a != EMPTY);
        if (Conv.isSparse(a)) {
            return Conv.sparseLength(a);
        } else {
            assert(Conv.isDense(a));
            return fastDenseLength(a);
        }
    }

    protected int endBlock(final long a) {
        return Conv.startBlock(a) + length(a);
    }

    protected long endPosition(final long a) {
        return Conv.startPosition(a) + (long)length(a) * 64L - 1L;
    }

    protected int splitThreshold() {
        return splitThreshold;
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

    protected int findIndex(final long a) {
        assert(a != EMPTY);
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

    protected void andDense(final int x0, final SBS3 o1, final int x1, final int sBlock, final int zBlock) {
        final long a = array[x0], b = o1.array[x1];
        final int di0 = Conv.denseIndex(a), di1 = Conv.denseIndex(b);
        final long[] data0 = dense[di0], data1 = o1.dense[di1];
        final int sBlock0 = Conv.startBlock(a), sBlock1 = Conv.startBlock(b);
        final int offset0 = sBlock0 - sBlock, offset1 = sBlock1 - sBlock;
        final int n = zBlock - Math.min(sBlock0 + data0.length, sBlock1 + data1.length);
        for (int i = 0; i < n; i++) {
            data0[i+offset0] &= data1[i+offset1];
        }
    }

    protected void clearDense(final int x0, final int startBlock, final int zBlock) {
        assert(x0 >= 0);
        assert(startBlock >= 0);
        assert(zBlock >= 0);
        assert(zBlock >= startBlock);
        final long a = array[x0];
        assert(Conv.isDense(a));
        final int denseIndex = Conv.denseIndex(a);
        final long[] data = dense[denseIndex];
        final int sBlock = Conv.startBlock(a);
        final int offset = Math.subtractExact(startBlock, sBlock);
        final int n = Math.subtractExact(zBlock, (sBlock - data.length));
        Arrays.fill(data, offset, n, ALL_CLEAR);
    }

    /**
     *
     * @param startBlock block to start computing from
     * @param o1
     * @return one past the last block computed
     */
    protected int and(final int startBlock, final SBS3 o1) {
        final int x0 = findIndex(startBlock), x1 = o1.findIndex(startBlock);
        final long a = array[x0], b = o1.array[x1];
        final int zBlock0 = x0 < dataSize - 1 ? Conv.startBlock(array[x0 + 1]) : Integer.MAX_VALUE;
        final int zBlock1 = x0 < o1.dataSize - 1 ? Conv.startBlock(o1.array[x1 + 1]) : Integer.MAX_VALUE;
        final int zBlock = Math.min(zBlock0, zBlock1);  // will be one past the first end block to be reached
        final int zDiff = zBlock0 - zBlock1;
        final boolean at = Conv.isSparse(a), bt = Conv.isSparse(b);

        if (at && bt) { // both sparse
            if (zBlock0 <= zBlock1) {
                if (!Conv.sparseValue(b)) // b is 0
                    clearSparse(x0);
            } else {
                if (zDiff <= 8) {
                    convertToDense(x0);
                } else {
                    splitMultiple(x0, zBlock1, zDiff > 2048 ? zDiff > 8192 ? 8 : 4 : 2);
                }
                assert(validateInvariants());
                return startBlock;
            }
        } else if (!at && !bt) {
            andDense(x0, o1, x1, startBlock, zBlock);
        } else if (at && !bt) {
            if (zBlock0 <= zBlock1) {
                final int bv = o1.denseIsAll(x1, zBlock);
                switch (bv) {
                    case 0: {
                        if (!Conv.sparseValue(b))
                            clearSparse(x0);
                        break;
                    }
                    default: {
                        convertToDense(x0);
                        assert(validateInvariants());
                        return startBlock;
                    }
                }
            } else {
                if (zDiff <= 8) {
                    convertToDense(x0);
                } else {
                    splitMultiple(x0, zBlock1, zDiff > 2048 ? zDiff > 8192 ? 8 : 4 : 2);
                }
                assert(validateInvariants());
                return startBlock;
            }
        } else if (!at && bt) {
            if (!Conv.sparseValue(b)) {
                clearDense(x0, startBlock, zBlock);
            }
        } else {
            throw new IllegalStateException();
        }

        assert(validateInvariants());
        return zBlock;
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

        final long key = PosConv.positionToKey(position);
        final int initialIndex = findIndex(key);
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
                    final int index = findIndex(key);
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
                    final int index = findIndex(key);
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
                    final int index = findIndex(key);
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

        final long key = PosConv.positionToKey(position);
        final int initialIndex = findIndex(key);
        final long initialA = array[initialIndex];
        assert(initialIndex >= 0);
        assert(initialIndex < dataSize);
        if (Conv.isSparse(initialA)) {
            final boolean av = Conv.sparseValue(initialA);
            if (!av) {
                return false;
            } else {

                {
                    final int index = findIndex(key);
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
                    final int index = findIndex(key);
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
                    final int index = findIndex(key);
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

        final long key = PosConv.positionToKey(position);
        final int index = findIndex(key);
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

    private class SetBitsItr implements LongIterator {

        boolean inPositiveSparse = false;
        boolean inDense = false;

        long sparsePosition = 0;
        long sparsePositionLength = 0;

        long[] denseData = null;
        int denseDataIndex = 0;
        long denseDataItem = 0;

        int denseDataPositionOffset = 0;

        long startPosition = 0;
        int arrayIndex = 0;

        long nextValue = nextNextLong();

        private long nextNextLong() {

            loop0: while (arrayIndex < dataSize) {
                if (inPositiveSparse) {
                    if (sparsePosition < sparsePositionLength)
                        return sparsePosition++;
                } else if (inDense) {
                    if (denseDataIndex < denseData.length) {
                        if (denseDataPositionOffset < 64) {
                            final long mask = 1L << denseDataPositionOffset++;
                            final boolean value = (mask & denseDataItem) == 0;
                            if (value)
                                return startPosition + denseDataIndex * 64 + denseDataPositionOffset;
                        } else {
                            denseDataItem = denseData[++denseDataIndex];
                            denseDataPositionOffset = 0;
                        }
                    }
                } else {
                    loop1:
                    while (true) {
                        arrayIndex++;
                        if (arrayIndex >= dataSize)
                            return -1L;

                        final long a = array[arrayIndex];
                        startPosition = Conv.startPosition(a);

                        if (Conv.isSparse(a)) {
                            if (!Conv.sparseValue(a)) {
                                continue loop1;
                            } else {
                                sparsePosition = 0;
                                sparsePositionLength = Math.multiplyExact(64, Conv.sparseLength(a));
                                break loop1;
                            }
                        } else {
                            assert(Conv.isDense(a));
                            final int denseIndex = Conv.denseIndex(a);
                            denseDataIndex = 0;
                            denseData = dense[denseIndex];
                            denseDataItem = denseData[denseDataIndex];
                            denseDataPositionOffset = 0;
                            break loop1;
                        }
                    }
                }
            }
            throw new IllegalStateException();
        }

        @Override
        public long nextLong() {
            final long retVal = nextValue;
            if (nextValue >= 0)
                nextValue = nextNextLong();
            return retVal;
        }

        @Override
        public int skip(int n) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return nextValue >= 0;
        }

        @Override
        public Long next() {
            return nextLong();
        }
    }

    public LongIterator positionsIterator() {
        return new SetBitsItr();
    }

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
            //System.out.println("a: " + Long.toHexString(a));
            out.print(i+":");
            print(a, out);
        }
    }

}
