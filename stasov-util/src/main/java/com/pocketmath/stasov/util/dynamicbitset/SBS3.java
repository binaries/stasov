package com.pocketmath.stasov.util.dynamicbitset;

import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.objects.ObjectArrays;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.LongConsumer;

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
    private int nextDenseFree;

    private static long ALL_CLEAR = 0L;
    private static long ALL_SET = 0xFFFFFFFFFFFFFFFFL;

    private static final int MAX_BLOCK = 0x0FFFFFFF;
    private static final long MAX_POSITION = 64L * (long)MAX_BLOCK;
    private static final int MAX_DENSE_INDEX = MAX_BLOCK;

    private static final int MAX_BLOCKS_PER_DENSE = 32768;
    private static final int INITIAL_BLOCKS_PER_DENSE = 16;

    protected static int maxBlock() {
        return MAX_BLOCK;
    }

    public static long maxPosition() {
        return MAX_POSITION;
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

    public SBS3(final int capacity) {
        if (capacity < 1) throw new IllegalArgumentException();
        this.array = new long[capacity];
        this.array[0] = Conv.createSparse(0, maxBlock(), false);
        this.dataSize = 1;
        LongArrays.fill(array, 1, array.length, EMPTY);
    }

    public SBS3() {
        this(8);
    }

    protected static class Conv {

        static boolean isSparse(final long a) {
            return (a & 0x1) == 0x0;
        }

        static boolean isDense(final long a) {
            return (a & 0x1) == 0x1;
        }

        static int startBlock(final long a) {
            return (int) (a >> 32);
        }

        static int sparseLength(final long a) {
            return ((int) a) >> 2;
        }

        static int denseIndex(final long a) {
            return ((int) a) >> 2;
        }

        static boolean sparseValue(final long a) {
            return (0x2 & a) == 0x2;
        }

        static long createSparse(final int startBlock, final int length, final boolean set) {
            if (startBlock < 0) throw new IllegalArgumentException();
            if (length < 0) throw new IllegalArgumentException();
            if (length > maxBlock()) throw new IllegalArgumentException();
            //System.out.println("startBlock: " + Integer.toBinaryString(startBlock));
            //System.out.println("length: " + Integer.toBinaryString(length));
            //System.out.println("length << 2: " + Integer.toBinaryString(length << 2));
            //System.out.println("set: " + set);
            final long retVal = (((long)startBlock) << 32) | ((long)length << 2) | (set ? 2 : 0);
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
            final long retVal = ((long)startBlock << 32) | ((long)denseIndex << 2) | 2 | 1;
            assert(Conv.isDense(retVal));
            assert(Conv.startBlock(retVal) == startBlock);
            assert(Conv.denseIndex(retVal) == denseIndex);
            //assert(Conv.denseIndex(retVal) == )
            return retVal;
        }

    }

    protected static class PosConv {

        static int positionToBlock(final long position) {
            return (int)(position / 64);
        }

        static long positionToKey(final long position) {
            final int block = positionToBlock(position);
            return ((long)block) << 32;
        }

        static boolean aboveMaxPosition(final long position) {
            return position > maxPosition();
        }

        static void requireInBoundsPosition(final long position) {
            if (aboveMaxPosition(position) || position < 0)
                throw new IllegalArgumentException();
        }

    }

    private int createDense(final int len, final boolean fillValue) {
        if (len > maxBlocksPerDense()) throw new IllegalArgumentException("len=" + len);
        final long[] newArray = new long[len];
        Arrays.fill(newArray, fillValue ? ALL_SET : ALL_CLEAR);

        if (dense == null) {
            dense = new long[8][];
            nextDenseFree = 0;
            dense[0] = newArray;
            return 0;
        }

        if (nextDenseFree >= dense.length) {
            for (int i = dense.length; i >= 0; i--) {
                final long[] x = dense[i];
                if (x != null) {
                    dense[i] = newArray;
                    return i;
                }
            }
            dense = ObjectArrays.grow(dense, Math.max((int)(dense.length * 1.5), dense.length + 8));
        }

        final int x = nextDenseFree;
        dense[x] = newArray;
        nextDenseFree++;

        System.out.println("nextDenseFree: " + nextDenseFree);
        assert(nextDenseFree >= dense.length || dense[nextDenseFree] == null);

        return x;
    }

    private long convertToDense(final int index, final long sparse) {
        assert(sparse != EMPTY);
        assert(!Conv.isDense(sparse));
        final int startBlock = Conv.startBlock(sparse);
        assert(startBlock < maxBlock());
        final int len = Conv.sparseLength(sparse);
        if (len > maxBlocksPerDense()) {
            System.out.println('w');
        }
        assert(len > 0);
        assert(len <= maxBlocksPerDense());
        final boolean sparseValue = Conv.sparseValue(sparse);
        final int denseIndex = createDense(len, sparseValue);
        assert(dense[denseIndex] != null);
        final long retVal = Conv.createDense(startBlock, denseIndex);
        assert(Conv.isDense(retVal));
        assert(Conv.startBlock(retVal) == startBlock);
        assert(Conv.denseIndex(retVal) == denseIndex);
        return retVal;
    }

    protected int findIndex(final long a) {
        final int index = LongArrays.binarySearch(array, a);
        return index < 0 ? Math.max(0, -index - 1) : index;
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
            final int newCapacity,
            final float spareBlocksMultiplier,
            final int minSpareBlocks,
            final int maxSpareBlocks) {
        assert(newCapacity >= 0);
        if (newCapacity <= array.length) return;
        final int prevLen = array.length;
        final int spareBlocks = Math.min(Math.max((int)(newCapacity * spareBlocksMultiplier), minSpareBlocks), maxSpareBlocks);
        final int growth = newCapacity - array.length + spareBlocks;
        LongArrays.grow(array, growth);
        LongArrays.fill(array, prevLen, array.length, EMPTY);
    }

    private void ensureArrayCapacity(final int newCapacity) {
        ensureArrayCapacity(newCapacity, 1.2F, 4, 8192);
    }

    private void ensureArrayCapacityNoSpare(final int newCapacity) {
        assert(newCapacity >= 0);
        if (newCapacity <= array.length) return;
        final int prevLen = array.length;
        final int growth = newCapacity - array.length;
        LongArrays.grow(array, growth);
        LongArrays.fill(array, prevLen, array.length, EMPTY);
    }

    private void trimArray(final int extra) {
        if (extra < 0) throw new IllegalArgumentException();
        final int maxNewCapacity = dataSize + extra;
        LongArrays.trim(array, maxNewCapacity);
    }

    public void readOptimize() {
        trimArray(0);
        // TODO: optimize dense
    }

    public void readWriteOptimize() {
        trimArray(Math.max(256, Math.multiplyExact(dataSize, 2)));
        // TODO: optimize dense
    }

    private void sortArray() {
        assert(dataSize <= array.length);
        assert(validateInvariants());
        Arrays.sort(array, 0, dataSize);
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

    private void split(final int index, final int numBlocksDistance) {
        if (numBlocksDistance <= 0) throw new IllegalArgumentException();
        assert(validateInvariants());

        final int newSize = dataSize + 1;
        final long a = array[index];
        final int len = Conv.sparseLength(a);
        assert(len > 0);
        if (len <= numBlocksDistance) return;
        ensureArrayCapacity(newSize);
        if (Conv.isSparse(a)) {
            final int sBlock0 = Conv.startBlock(a);
            final int sBlock1 = Math.addExact(sBlock0, numBlocksDistance);
            final int len0 = numBlocksDistance;
            final int len1 = len - numBlocksDistance;
            assert(sBlock0 >= 0);
            assert(sBlock1 >= 0);
            assert(sBlock1 > sBlock0);
            assert(len0 > 0);
            assert(len1 > 0);
            final boolean set = Conv.sparseValue(a);
            final long a1 = Conv.createSparse(sBlock0, len0, set);
            final long a2 = Conv.createSparse(sBlock1, len1, set);
            array[index] = a1;
            array[dataSize] = a2;
            dataSize = newSize;
            sortArray();
        } else {
            // TODO not yet implemented
            throw new IllegalStateException("not yet implemented");
        }
        assert(validateInvariants());
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
                    array[x0] = convertToDense(x0, a);
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
                        array[x0] = convertToDense(x0, a);
                        assert(validateInvariants());
                        return startBlock;
                    }
                }
            } else {
                if (zDiff <= 8) {
                    array[x0] = convertToDense(x0, a);
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

        assert(Conv.isDense(a));
        final int sBlock = Conv.startBlock(position);
        final int positionBlock = PosConv.positionToBlock(position);
        final int blockOffset = positionBlock - sBlock;
        final int positionOffset = (int) position % 64;
        final int denseIndex = Conv.denseIndex(a);
        final long x = 0x8000000000000000L >> positionOffset;

        assert(sBlock >= 0);
        assert(positionBlock >= 0);
        assert(blockOffset >= 0);
        assert(positionOffset >= 0);
        assert(denseIndex >= 0);
        assert(denseIndex < dense.length);
        assert(x != 0);

        final long[] data = dense[denseIndex];
        System.out.println("denseIndex: " + denseIndex);
        if (data == null)
            throw new IllegalStateException("denseIndex = " + denseIndex);
        final long d = data[blockOffset];
        data[blockOffset] |= x;

        assert(validateInvariants());
        return (d & x) == 0;
    }

    protected boolean clearDenseByPosition(final long a, final long position) {
        assert(Conv.isDense(a));
        final int sBlock = Conv.startBlock(position);
        final int positionBlock = PosConv.positionToBlock(position);
        final int blockOffset = positionBlock - sBlock;
        final int positionOffset = (int) position % 64;
        final int denseIndex = Conv.denseIndex(a);
        final long x = ~(0x8000000000000000L >> positionOffset);
        final long[] data = dense[denseIndex];
        final long d = data[blockOffset];
        data[blockOffset] &= x;
        return (d & x) != 0;
    }

    private boolean validateMonotonicIncreasing() {

        int startBlock = -1;

        for (int i = 0; i < dataSize; i++) {
            final long a = array[i];
            final int nextStartBlock = Conv.startBlock(a);
            if (nextStartBlock < startBlock) {
                throw new IllegalStateException("startBlock=" + startBlock + " at array index=" + i);
            }
            startBlock = nextStartBlock;
        }
        return true;
    }

    private boolean validateEmptyMarking() {
        if (Arrays.stream(array, 0, dataSize).anyMatch( a -> a == EMPTY )) return false;
        if (Arrays.stream(array, dataSize, array.length).anyMatch( a -> a != EMPTY )) return false;
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
            }
        }
        return true;
    }

    private boolean validateInvariants() {
        if (!validateMonotonicIncreasing()) return false;
        if (!validateEmptyMarking()) return false;
        if (!validateDenseHaveArrays()) return false;
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
        if (Conv.isSparse(initialA)) {
            final boolean av = Conv.sparseValue(initialA);
            if (av) {
                return false;
            } else {

                {
                    final int index = findIndex(key);
                    final long a = array[index];

                    final int sBlock = Conv.startBlock(a);
                    final int targetBlock = PosConv.positionToBlock(position);

                    final int distanceToTargetBlockFromStart = targetBlock - sBlock;

                    if (distanceToTargetBlockFromStart > 8) {
                        split(index, distanceToTargetBlockFromStart - 1);
                    }
                }

                {
                    final int index = findIndex(key);
                    final long a = array[index];

                    final int sBlock = Conv.startBlock(a);
                    final int targetBlock = PosConv.positionToBlock(position);
                    final int len = Conv.sparseLength(a);

                    final int distanceFromTargetBlockToEnd = sBlock + len - targetBlock;

                    if (distanceFromTargetBlockToEnd > 8) {
                        split(index, 8);
                    }
                }

                {
                    final int index = findIndex(key);
                    final long a = array[index];

                    System.out.println("begin");
                    print(System.out);
                    System.out.println("end");

                    final long denseA = convertToDense(index, a);
                    final boolean modified = setDenseByPosition(denseA, position);
                    array[index] = denseA;

                    assert(validateInvariants());
                    return modified;
                }
            }
        } else {
            assert(Conv.isDense(initialA));

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
        final int index = findIndex(position);
        final long a = array[index];
        if (Conv.isSparse(a)) {
            final boolean av = Conv.sparseValue(a);
            if (!av) {
                return false;
            } else {
                final int len = Conv.sparseLength(a);
                if (len > 8) {
                    final int sBlock = Conv.startBlock(a);
                    final int positionBlock = PosConv.positionToBlock(position);
                    final int offset = sBlock - positionBlock;
                    split(index, offset);
                }
                final int newIndex = findIndex(position);
                final long a1 = array[newIndex];
                final long a2 = convertToDense(newIndex, a1);
                final boolean modified = clearDenseByPosition(a2, position);
                array[index] = a2;
                return modified;
            }
        } else {
            assert(Conv.isDense(a));
            return setDenseByPosition(a, position);
        }
    }

    public void print(final PrintStream out) {
        final NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(12);
        for (int i = 0; i < dataSize; i++) {
            final long a = array[i];
            //System.out.println("a: " + Long.toHexString(a));
            if (Conv.isSparse(a)) {
                final int sBlock = Conv.startBlock(a);
                final int len = Conv.sparseLength(a);
                final int zBlock = sBlock + len;
                final boolean av = Conv.sparseValue(a);
                out.print(av ? '1' : '0');
                out.print(":[");
                out.print(sBlock);
                out.print("..");
                out.print(zBlock-1);
                out.println(']');
            } else {
                assert(Conv.isDense(a));
                final int sBlock = Conv.startBlock(a);
                final int denseIndex = Conv.denseIndex(a);
                final long[] data = dense[denseIndex];
                for (int j = 0; j < data.length; j++) {
                    final String hex = Long.toHexString(data[j]);
                    out.print(sBlock + j);
                    out.print(' ');
                    out.println(hex);
                }
            }
        }
    }

    public static void main(String args[]) {
        SBS3 o = new SBS3();
        o.print(System.out);

        o.set(1);
        o.set(2);
        o.set(64);
        o.set(127);

        System.out.println("result: ");
        o.print(System.out);
    }

}
