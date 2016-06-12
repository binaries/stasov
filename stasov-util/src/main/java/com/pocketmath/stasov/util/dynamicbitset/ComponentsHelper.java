package com.pocketmath.stasov.util.dynamicbitset;

import com.sun.istack.internal.NotNull;
import net.nicoulaj.compilecommand.annotations.Inline;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Created by etucker on 6/8/16.
 */
class ComponentsHelper {

    @Inline
    private static int block(final long a) {
        return (int)(a >> 32);
    }

    private static abstract class Finder {

        @Inline
        protected abstract boolean match(final long actual, final int block, final SBS3 core);

        /**
         *
         * @param block
         * @param startIndex
         * @param core
         * @return the component index or -1 if array is empty
         */
        @Inline
        public final int findAtOrNearby(final int block, final int startIndex, final SBS3 core) {
            if (core.array[startIndex] != SBS3.EMPTY) {
                return startIndex;
            }

            assert core.end >= 0;
            assert core.end + 1 > 0; // prevent possible overflow later

            int i = startIndex - 1;
            int j = startIndex + 1;
            for (;;) {
                assert i < j;
                assert i != j;
                assert i >= -1;
                assert j <= core.end + 1; // the case of end + 1 > 0 above is an overflow check
                assert i < startIndex;
                assert j > startIndex;

                final long ai = core.array[i], aj = core.array[j];

                if (i >= 0) {
                    if (match(ai, block, core)) {
                        return i;
                    } else if (ai != SBS3.EMPTY && core.startBlock(ai) < block) {
                        i = -i - 1;
                        assert i < 0;
                    } else {
                        i--;
                    }
                    continue;
                } else {
                    assert i < 0;
                    if (j < 0) {
                        return i;
                    }
                }

                if (j >= 0 && j < core.end) {
                    if (match(aj, block, core)) {
                        return j;
                    } else if (aj != SBS3.EMPTY && core.endBlock(aj) > block) {
                        j = -j - 1;
                        assert j < 0;
                    } else {
                        j++;
                    }
                    continue;
                } else {
                    assert j < 0;
                    assert i < 0;
                    return i;
                }

            }
        }

        /**
         *
         * @param block
         * @param core
         * @return the component index
         */
        public int find(final int block, final SBS3 core) {
            assert block > 0;

            int min = 0;
            int max = core.end;

            int index0 = 0;

            for (;;) {

                final int delta = max - min;
                assert delta >= 0;
                if (delta == 0) {
                    final int retVal = -1 * index0 - 1;
                    assert retVal < 0;
                    return retVal;
                }

                index0 = min + delta / 2;
                assert index0 >= 0;

                final long x = core.array[index0];
                final int index1 = findAtOrNearby(block, index0, core);
                if (index1 < 0) {
                    return index0;
                } else {
                    final int pivot = block(core.array[index1]);
                    if (block > pivot) {
                        min = index1;
                    } else if (block < pivot) {
                        max = index1;
                    } else {
                        assert block == pivot;
                        throw new IllegalStateException("already exists");
                    }
                }
            }
        }

    }

    private final static Finder NON_EMPTY_FINDER = new Finder() {

        @Override
        @Inline
        protected boolean match(final long actual, final long expected, @Nullable final SBS3 core) {
            return actual != SBS3.EMPTY;
        }

    };

    private final static Finder EXPECTED_IS_WITHIN_ACTUAL_COMPONENT_FINDER = new Finder() {

        @Override
        @Inline
        protected boolean match(final long actual, final int expected, @Nonnull final SBS3 core) {
            assert expected != SBS3.EMPTY;
            assert core != null;

            final int actualStartBlock = SBS3.Conv.startBlock(actual);
            final int expectedStartBlock = expected;

            if (actual == SBS3.EMPTY)
                return false;
            if (expectedStartBlock < actualStartBlock)
                return false;
            assert expectedStartBlock >= actualStartBlock;

            final int actualLength = core.length(actual);
            final int actualPastEnd = actualStartBlock + actualLength;
            assert actualPastEnd >= 0; // overflow check
            return
                    expectedStartBlock >= actualStartBlock
                    && expectedStartBlock < actualPastEnd;
        }
    };

    private int findEmptySlot(final int block, final SBS3 core) {
        return NON_EMPTY_FINDER.find(block, core);
    }

    static int find(final int block, final SBS3 core) {
        return EXPECTED_IS_WITHIN_ACTUAL_COMPONENT_FINDER.find(block, core);
    }

    static boolean delete(final long a, final SBS3 core) {
        final int capacity = core.array.length;
        assert core.end < capacity;

        final int x = find(a, core);
        if (x >= 0) {
            core.array[x] = SBS3.EMPTY;
            core.decrementSize();
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param componentIndex
     * @param chasmSize
     * @param capacityIncreaseFactor
     * @param core
     * @return new capacity
     */
    private static int expand(
            final int componentIndex,
            final int chasmSize,
            final float capacityIncreaseFactor,
            final SBS3 core) {

        if (componentIndex < 0) throw new IllegalArgumentException();
        if (chasmSize <= 0) throw new IllegalArgumentException();
        if (capacityIncreaseFactor <= 1f) throw new IllegalArgumentException();
        if (core == null) throw new IllegalArgumentException();

        final int capacity = core.array.length;
        assert capacity >= core.end;

        final int newCapacity;

        if (capacity - core.end >= chasmSize) {
            newCapacity = capacity; // no change
            System.arraycopy(core.array, componentIndex, core.array, componentIndex + chasmSize, core.end - componentIndex);
            Arrays.fill(core.array, componentIndex, componentIndex + chasmSize, SBS3.EMPTY);
        } else {
            newCapacity = Math.max((int)(capacity * capacityIncreaseFactor), capacity + chasmSize);
            assert newCapacity >= capacity;
            assert newCapacity >= 0;
            final long[] newArray = new long[newCapacity];
            System.arraycopy(core.array, 0, newArray, 0, componentIndex);
            System.arraycopy(core.array, componentIndex, newArray, componentIndex + chasmSize, core.end - componentIndex);
        }

        int newEnd = core.end + chasmSize;
        for (int i = newEnd; i >= 0; i--) {
            if (core.array[i] != SBS3.EMPTY) {
                newEnd = i;
                break;
            }
        }
        core.end = newEnd;

        assert newCapacity >= capacity;
        assert newCapacity > 0;

        return newCapacity;
    }

    private static boolean insertComponent(final long a, final int startingComponentIndex, final int maxDistance, final SBS3 core) {
        assert startingComponentIndex >= 0;
        assert maxDistance >= 0;
        assert core != null;

        final int capacity = core.array.length;
        final int maxComponentIndex = Math.min(capacity, Math.addExact(startingComponentIndex, maxDistance));
        boolean openSlot = false;
        int i = startingComponentIndex + 1;
        for (; i <= maxComponentIndex; i++) {
            final long ai = core.array[i];
            if (ai == SBS3.EMPTY) {
                openSlot = true;
            } else if (ai == a) {
                return true;
            } else {
                break;
            }
        }
        if (openSlot) {
            final int midPointDelta = Math.max(Math.subtractExact(startingComponentIndex, i) / 2, 1);
            final int midPoint = Math.addExact(midPointDelta, startingComponentIndex);
            assert core.array[midPoint] == SBS3.EMPTY;
            core.array[midPoint] = a;
            core.incrementSize();
            return true;
        }
        return false;
    }

    static void split(final int block, final SBS3 core) {
        if (block < 0) throw new IllegalArgumentException();
        if (core == null) throw new IllegalArgumentException();

        final int chasmSize = 8;
        final float capacityIncreaseFactor = 1.25f;

        final int componentIndex = find(block, core);
        if (componentIndex < 0)
            throw new IllegalStateException("component not found; index: " + componentIndex);

        final long a0 = core.array[componentIndex];
        if (a0 == SBS3.EMPTY)
            throw new IllegalStateException();

        final int length = core.length(a0);
        final int startBlock = core.startBlock(a0);
        final int endBlock = core.endBlock(a0);

        assert startBlock >= 0;
        assert endBlock >= 0;
        assert endBlock >= startBlock;

        assert block >= startBlock;
        assert block <= endBlock;

        final int startBlockLower = startBlock;
        final int lengthLower = Math.subtractExact(block, startBlock);

        final int startBlockUpper = endBlock;
        final int lengthUpper = Math.incrementExact(Math.subtractExact(endBlock, block));

        assert lengthLower >= 0;
        assert lengthUpper >= 0;
        assert length == lengthLower + lengthUpper;
        assert startBlockLower <= startBlockUpper;

        if (startBlockUpper <= startBlockLower)
            throw new IllegalStateException();

        if (SBS3.Conv.isSparse(a0)) {
            final boolean v = SBS3.Conv.sparseValue(a0);
            final long al = SBS3.Conv.createSparse(startBlockLower, lengthLower, v);
            final long ah = SBS3.Conv.createDense(startBlockUpper, lengthUpper, v);

        } else {
            assert SBS3.Conv.isDense(a0);

            final long al = core.createDenseNoPrefill(startBlockLower, lengthLower);
            final long ah = core.createDenseNoPrefill(startBlockUpper, lengthUpper);

            final long[] arrayLower = core.denseArray(al);
            final long[] arrayUpper = core.denseArray(ah);
            if (arrayLower == null)
                throw new IllegalStateException();
            if (arrayUpper == null)
                throw new IllegalStateException();
            assert SBS3.Conv.isDense(al);
            assert SBS3.Conv.isDense(ah);
            assert arrayLower.length == lengthLower;
            assert arrayUpper.length == lengthUpper;

            final long[] arraySource = core.denseArray(a0);
            assert arraySource.length == arrayLower.length + arrayUpper.length;

            {
                assert SBS3.Conv.startBlock(a0) == SBS3.Conv.startBlock(al);
                System.arraycopy(arraySource, 0, arrayLower, 0, lengthLower);
            }
            {
                final int offset = Math.subtractExact(startBlockUpper, startBlock);
                assert offset >= 0;
                assert offset == arraySource.length - lengthUpper;
                System.arraycopy(arrayLower, offset, arrayUpper, 0, lengthUpper);
            }
        }

        assert componentIndex >= 0;
        assert core.array[componentIndex] != SBS3.EMPTY;
        final int upperComponentIndex = Math.incrementExact(componentIndex);
        core.array[componentIndex] = al;

        final boolean insertedAttempt1 = insertComponent(ah, upperComponentIndex, chasmSize, core);
        if (insertedAttempt1)
            return;

        expand(upperComponentIndex, chasmSize, capacityIncreaseFactor, core);

        final boolean insertedAttempt2 = insertComponent(ah, upperComponentIndex, chasmSize, core);
        if (!insertedAttempt2)
            throw new IllegalStateException();

    }

    static int insert(
            final long a,
            final int chasmSize,
            final float capacityIncreaseFactor,
            final SBS3 core) {

        assert chasmSize > 0;
        assert capacityIncreaseFactor > 1f;

        final int capacity = core.array.length;
        assert capacity >= core.end;

        final int x = ComponentsHelper.findEmptySlot(a, core);
        if (x >= 0) {
            core.array[x] = a;
            core.incrementSize();
            return x;
        }

        assert x < 0;

        final int x1 = -1 * x + 1;
        assert x1 >= 0;

        final int newCapacity = expand(x1, chasmSize, capacityIncreaseFactor, core);

        final int distanceIntoChasm = chasmSize / 2 + 1;
        assert distanceIntoChasm <= chasmSize;
        assert distanceIntoChasm > 0;
        final int x2 = x1 + distanceIntoChasm;
        assert x2 <= core.end;
        assert core.array[x2] == SBS3.EMPTY;
        core.array[x2] = a;

        return x1;
    }

}
