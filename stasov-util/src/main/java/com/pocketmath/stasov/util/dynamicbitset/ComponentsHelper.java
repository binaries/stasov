package com.pocketmath.stasov.util.dynamicbitset;

import net.nicoulaj.compilecommand.annotations.Inline;

import javax.annotation.Nonnegative;
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
         * The nearest non-empty spot.  That's all!
         *
         * @param startIndex
         * @param lowerIndexBound inclusive
         * @param upperIndexBound inclusive
         * @param core
         * @return
         */
        @Inline
        public final int findPivot(
                final int startIndex,
                final int lowerIndexBound,
                final int upperIndexBound,
                final SBS3 core) {

            assert lowerIndexBound >= 0;
            assert upperIndexBound <= core.end;

            if (core.getComponent(startIndex) != SBS3.EMPTY)
                return startIndex;

            int aboveIndex = startIndex, belowIndex = startIndex;

            while (aboveIndex <= upperIndexBound || belowIndex >= lowerIndexBound) {
                if (aboveIndex <= upperIndexBound) {
                    final long a = core.getComponent(aboveIndex);
                    if (a != SBS3.EMPTY) {
                        return aboveIndex;
                    }
                    aboveIndex++;
                }
                if (belowIndex >= lowerIndexBound) {
                    final long a = core.getComponent(belowIndex);
                    if (a != SBS3.EMPTY) {
                        return belowIndex;
                    }
                    belowIndex--;
                }
            }
            throw new IllegalStateException("The index was not in range (match or non-match).");
        }

        /**
         *
         * @param block
         * @param startIndex
         * @param core
         * @return the component index or negative one less index where insertion could occur
         */
        @Inline
        public final int findPivot1(
                final int block,
                final int startIndex,
                final SBS3 core) {

            if (core.getComponent(startIndex) != SBS3.EMPTY)
                return startIndex;

            int aboveIndex = startIndex, belowIndex = startIndex;

            int upperMissIndex = -1;

            while (aboveIndex <= core.end || belowIndex >= 0) {
                if (aboveIndex <= core.end) {
                    final long a = core.getComponent(aboveIndex);
                    if (a != SBS3.EMPTY) {
                        if (match(a, block, core)) {
                            return aboveIndex;
                        } else {
                            upperMissIndex = aboveIndex;
                        }
                    }
                    aboveIndex++;
                }
                if (belowIndex >= 0) {
                    final long a = core.getComponent(belowIndex);
                    if (a != SBS3.EMPTY) {
                        if (match(a, block, core)) {
                            return belowIndex;
                        } else {
                            if (upperMissIndex >= 0) {
                                return -upperMissIndex - 1;
                            }
                        }
                    }
                    belowIndex--;
                }
            }
            throw new IllegalStateException("The index was not in range (match or non-match).");
        }

        /**
         *
         * @param block
         * @param startIndex
         * @param core
         * @param returnUpperMiss
         * @return the component index or -1 if array is empty
         */
        /*
        @Inline
        public final int findAtOrNearby1(
                final int block,
                final int startIndex,
                final SBS3 core,
                final boolean returnUpperMiss) {

            if (core.getComponent(startIndex) != SBS3.EMPTY)
                return startIndex;

            assert core.end >= 0;
            assert core.end + 1 > 0; // prevent possible overflow later

            int i = Math.max(Math.decrementExact(startIndex), 0);
            int j = Math.min(Math.incrementExact(startIndex), core.end);
            if (i == j) {

            }

            for (;;) {
                assert i < j;
                assert i != j;
                assert i >= -1;
                assert j <= core.end + 1; // the case of end + 1 > 0 above is an overflow check
                assert i < startIndex;
                assert j > startIndex;

                final long ai = core.getComponent(i), aj = core.getComponent(j);

                if (i >= 0) {
                    if (match(ai, block, core)) {
                        return i;
                    } else if (ai != SBS3.EMPTY && core.startBlock(ai) < block) {
                        i = -i - 1;
                        assert i < 0;
                    } else if (i > 0) {
                        i--;
                    }
                    continue;
                } else {
                    assert i < 0;
                    if (j < 0) {
                        final int retVal = returnUpperMiss ? j : i;
                        assert retVal < 0;
                        return retVal;
                    }
                }

                if (j >= 0 && j < core.end) {
                    if (match(aj, block, core)) {
                        assert j >= 0;
                        assert j >= i;
                        assert j < core.end;
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
                    final int retVal = returnUpperMiss ? j : i;
                    assert retVal < 0;
                }

            }
        }
*/

        /**
         *
         * @param block
         * @param core
         * @return the component index
         */
        public int find(final int block, final SBS3 core) {
            assert block >= core.minBlock() : "block is " + block;
            assert block <= core.maxBlock();

            assert core.validateInvariants();

            int min = 0;
            int max = core.end;

            int index0;
            int previousIndex0 = -1, previousIndex1 = -1;

            //final long qa = SBS3.Conv.createEphemeralQueryComponent(block);

            int c = 0;

            for (;;) {

                final int delta = max - min;
                //assert delta >= 0;

                index0 = min + delta / 2;
                if (c != 0) {
                    index0 = Math.addExact(index0, c);
                    index0 = Math.max(index0, min);
                    index0 = Math.min(index0, max);
                }

                assert index0 >= 0;
                assert index0 >= min;
                assert index0 <= core.end;
                assert index0 <= max;

                final int index1 = findPivot(index0, min, max, core);  // TODO: eliminate the redundant lookup (getComponent(...) call) that's happening inside this method

                assert index1 >= 0;
                assert index1 >= min;
                assert index1 <= core.end;
                assert index1 <= max;

                final long a = core.getComponent(index1);
                final int aStartBlock = SBS3.Conv.startBlock(a);

                assert a != SBS3.EMPTY;

                if (aStartBlock <= block) {
                    if (block <= core.endBlock(a)) {
                        assert core.containsBlock(a, block);
                        return index1;
                    } else {
                        min = index1;
                    }
                } else {
                    assert aStartBlock > block;
                    assert core.endBlock(a) > block;
                    max = index1;
                }

                if (min == max) {
                    core.print(a, System.out);
                }

                //assert index0 != previousIndex0 : "index0: " + index0;
                //assert (previousIndex0 = index0) == 0 || true; // hack to only do this logic within assertion

                //assert index1 != previousIndex1 : "index1: " + index1;
                if (index1 == previousIndex1) {
                    c = Math.incrementExact(c);
                    c = Math.multiplyExact(c, -1);
                } else {
                    c = 0;
                }
                previousIndex1 = index1;
            }
        }

    /**
         *
         * @param block
         * @param core
         * @return the component index
         */

    /*
        public int find2(final int block, final SBS3 core) {
            assert block >= 0 : "block is " + block;

            assert core.validateInvariants();

            int min = 0;
            int max = core.end;

            int index0 = 0;

            for (;;) {

                final int delta = max - min;
                assert delta >= 0;
                if (delta == 0) {
                    final int retVal = -1 * index0 - 1;
                    assert retVal < 0;
                    //return retVal;
                    throw new IllegalStateException();
                }

                index0 = min + delta / 2;
                assert index0 >= 0;

                final int index1 = findPivot(block, index0, core);

                if (index1 >= 0)
                    return index1;

                final int index1Miss = -index1 + 1;
                assert index1Miss >= 0;

                final int index2 = Math.min(index1Miss, core.maxBlock());

                final long a2 = core.getComponent(index2);
                final int a2StartBlock = core.startBlock(a2);
                final int a2EndBlock = core.endBlock(a2);

                assert block < a2StartBlock || block > a2EndBlock;

                if (block > a2EndBlock) {
                    // we need to look higher!
                    if (min > index2)
                        min = index2;
                }

                if (block < a2StartBlock) {
                    // we need to look lower!
                    if (max < index2)
                        max = index2;
                }
            }
        }*/

    } // finder

    private final static Finder NON_EMPTY_FINDER = new Finder() {

        @Override
        @Inline
        protected boolean match(final long actual, @Nonnegative final int block, @Nullable final SBS3 core) {
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

    private static int findEmptySlot(final int block, final SBS3 core) {
        return NON_EMPTY_FINDER.find(block, core);
    }

    static int find(final int block, final SBS3 core) {
        return EXPECTED_IS_WITHIN_ACTUAL_COMPONENT_FINDER.find(block, core);
    }

    /*
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
    */

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

        assert core.validateInvariants();

        final int capacity = core.array.length;
        assert capacity >= core.end;
        assert core.end >= 0;

        final int newCapacity;

        int filledToIndex = 0;

        if (Math.subtractExact(capacity,core.end) >= chasmSize) {
            newCapacity = capacity; // no change
            //System.arraycopy(core.array, componentIndex, core.array, componentIndex + chasmSize, core.end - componentIndex);
            final int x1 = Math.addExact(componentIndex, chasmSize);

            System.arraycopy(
                    core.array,
                    componentIndex,
                    core.array,
                    x1,
                    Math.max(0, Math.subtractExact(core.end, Math.decrementExact(componentIndex))));

            Arrays.fill(
                    core.array,
                    componentIndex,
                    x1,
                    SBS3.EMPTY);

            filledToIndex = Math.addExact(core.end, chasmSize);

        } else {
            newCapacity = Math.max((int)(capacity * capacityIncreaseFactor), Math.addExact(capacity,chasmSize));
            assert newCapacity >= capacity;
            assert newCapacity >= 0;
            final long[] newArray = new long[newCapacity];
            final int x1 = Math.addExact(componentIndex, chasmSize);

            System.arraycopy(
                    core.array,
                    0,
                    newArray,
                    0,
                    componentIndex);

            Arrays.fill(
                    newArray,
                    componentIndex,
                    x1,
                    SBS3.EMPTY);

            final int len2 = Math.max(0, Math.subtractExact(core.end, Math.decrementExact(componentIndex)));
            System.arraycopy(
                    core.array,
                    componentIndex,
                    newArray,
                    x1,
                    len2);

            filledToIndex = Math.addExact(x1, len2);

            core.array = newArray;
        }

        int newEnd =
                Math.decrementExact(
                        Math.addExact(core.end,chasmSize));
        for (int i = newEnd; i >= 0; i--) {
            final long a = core.getComponent(i);
            if (a == 0) {
                core.array[i] = SBS3.EMPTY;
            } else if (a != SBS3.EMPTY) {
                newEnd = i;
                break;
            }
        }

        final int onePastNewEnd = Math.incrementExact(newEnd);
        if (onePastNewEnd < core.array.length) {
            Arrays.fill(
                    core.array,
                    Math.max(onePastNewEnd, filledToIndex),
                    core.array.length,
                    SBS3.EMPTY);
        }

        core.end = newEnd;

        assert newEnd < newCapacity : "newCapacity: " + newCapacity + "; newEnd: " + newEnd;

        assert newCapacity >= capacity;
        assert newCapacity > 0;

        assert core.validateInvariants();

        return newCapacity;
    }

    /**
     *
     * @param a
     * @param startingComponentIndex
     * @param maxDistance
     * @param core
     * @return the component index where insertion took place else -1
     */
    private static int insertComponent(
            final long a,
            final int startingComponentIndex,
            final int maxDistance,
            final SBS3 core) {

        assert core.validateComponent(a);

        assert a != SBS3.EMPTY;
        assert startingComponentIndex >= 0;
        assert maxDistance >= 0;
        assert core != null;
        assert core.array != null;

        final int capacity = core.array.length;

        final int maxComponentIndex = Math.min(
                Math.decrementExact(capacity),
                Math.addExact(startingComponentIndex, maxDistance));

        final long startPosition = core.startPosition(a);

        int firstOpenSlot = -1, lastOpenSlot = -1;
        int i = startingComponentIndex + 1;
        for (; i <= maxComponentIndex; i++) {
            assert i >= 0;
            //assert i <= core.end + 1;
            final long ai = core.getComponent(i);
            assert ai != SBS3.EMPTY ? core.validateComponent(ai) : true : "validating component at index: " + i;
            if (ai == SBS3.EMPTY) {
                if (firstOpenSlot < 0)
                    firstOpenSlot = i;
                lastOpenSlot = i;
                break; // there's an open slot, so we'll insert
            } else if (ai == a) {
                return i; // surprise!  we tried to insert something that already exists
            } else {
                if (core.endPosition(ai) < startPosition) {
                    continue;
                } else {
                    return -1;
                }
            }
        }

        if (lastOpenSlot >= 0) {
            // calculate midpoint
            final int span = Math.subtractExact(lastOpenSlot, firstOpenSlot);
            final int midSpan = span / 2;
            final int mid = firstOpenSlot + midSpan;
            final int midAdjusted =
                    Math.max(
                            firstOpenSlot,
                            Math.min(mid, lastOpenSlot)
                    );

            assert core.getComponent(midAdjusted) == SBS3.EMPTY;
            assert core.validatePossibleComponentOrEmpty(a);

            core.setComponent(midAdjusted, a);
            core.incrementSize();
            if (midAdjusted > core.end)
                core.end = midAdjusted;
            return midAdjusted;
        } else {
            return -1; // there's no space to insert.  we'll return that we've failed to insert
        }
    }

    private static boolean allClear(final long[] data, final int start, final int end) {
        for (int i = start; i <= end; i++) {
            if (data[i] != 0L) {
                return false;
            }
        }
        return true;
    }

    private static boolean allSet(final long[] data, final int start, final int length) {
        for (int i = start; i < length; i++) {
            if (data[i] != 0xFFFFFFFFFFFFFFFFL) {
                return false;
            }
        }
        return true;
    }

    static int trySplitBefore(final int block, final SBS3 core) {
        if (core == null) throw new IllegalArgumentException();

        assert core.validateBlock(block);

        final int blockBefore = Math.decrementExact(block);
        if (blockBefore < 0) {
            return -1;
        }
        assert core.validateBlock(blockBefore);
        return trySplitAfter(blockBefore, false, core);
    }

    static int trySplitAfter(final int block, final SBS3 core) {
        return trySplitAfter(block, true, core);
    }

        /**
         * Splits components between the given block and very next block number.
         *
         * Will not split if the block number given is at the end of component
         * because the very next block will be on a new component which would
         * be the split boundary meaning there is no change.
         *
         * Will also not split if the block is at the maximum block as no further
         * split can occur.
         *
         * @param block
         * @param core
         * @param returnLowerBlockComponent if true return the lower split block's component from the split else return the split upper's
         * @return split block's new component index if it changed else -1
         */
    private static int trySplitAfter(final int block, final boolean returnLowerBlockComponent, final SBS3 core) {
        if (core == null) throw new IllegalArgumentException();

        assert core.validateBlock(block);

        if (block < core.minBlock()) throw new IllegalArgumentException();
        if (block > core.maxBlock()) throw new IllegalArgumentException();

        if (block == core.maxBlock()) {
            return -1;
        }

        final int chasmSize = 8;
        final float capacityIncreaseFactor = 1.25f;

        final int componentIndex = find(block, core);
        if (componentIndex < 0)
            throw new IllegalStateException("component not found; index: " + componentIndex);
        assert componentIndex <= core.end;

        final long a0 = core.array[componentIndex];
        assert core.validateComponent(a0);
        if (a0 == SBS3.EMPTY)
            throw new IllegalStateException();

        final int length = core.length(a0);
        final int startBlock = core.startBlock(a0);
        final int endBlock = core.endBlock(a0);

        assert core.validateBlock(startBlock);
        assert core.validateBlock(endBlock);

        assert endBlock >= startBlock;

        assert block >= startBlock;
        assert block <= endBlock;

        final int delta = 1;

        final int startBlockLower = startBlock;
        final int lengthLower = Math.addExact(Math.subtractExact(block, startBlock), delta);

        final int startBlockUpper = Math.addExact(startBlock, lengthLower);
        final int lengthUpper = Math.subtractExact(Math.subtractExact(endBlock, block), 0); // TODO: no subtract delta?

        if (lengthLower <= 0)
            return -1;

        if (lengthUpper <= 0)
            return -1;

        if (startBlockLower == startBlockUpper)
            return -1;

        assert lengthLower >= 0;
        assert lengthUpper >= 0;
        assert lengthLower > 0;
        assert lengthUpper > 0;
        assert length == lengthLower + lengthUpper : "length: " + length + "; lower: " + lengthLower + "; upper: " + lengthUpper;
        assert startBlockLower < startBlockUpper;

        if (startBlockUpper <= startBlockLower)
            throw new IllegalStateException();

        assert Math.subtractExact(Math.addExact(startBlockLower, lengthLower), 1) <= core.maxBlock();
        assert Math.subtractExact(Math.addExact(startBlockUpper, lengthUpper), 1) <= core.maxBlock() : "startBlockUpper: " + startBlockUpper + "; lengthUpper: " + lengthLower + "; maxBlock: " + core.maxBlock();

        final long al, ah;

        if (SBS3.Conv.isSparse(a0)) {
            final boolean v = SBS3.Conv.sparseValue(a0);
            al = SBS3.Conv.createSparse(startBlockLower, lengthLower, v);
            ah = SBS3.Conv.createSparse(startBlockUpper, lengthUpper, v);

        } else {
            assert SBS3.Conv.isDense(a0);

            final boolean lowerDense, upperDense;

            final long[] denseData = core.denseArray(a0);

            // TODO: use single pass method for allClear and allSet
            if (allClear(denseData, 0, lengthLower)) {
                al = SBS3.Conv.createSparse(startBlockLower, lengthLower, false);
                lowerDense = false;
            } else if (allSet(denseData, 0, lengthLower)) {
                al = SBS3.Conv.createSparse(startBlockLower, lengthLower, true);
                lowerDense = false;
            } else {
                al = core.createDenseNoPrefill(startBlockLower, lengthLower);
                lowerDense = true;
            }

            // TODO: use single pass method for allClear and allSet
            if (allClear(denseData, lengthLower, lengthUpper)) {
                ah = SBS3.Conv.createSparse(startBlockUpper, lengthUpper, false);
                upperDense = false;
            } else if (allSet(denseData, lengthLower, lengthUpper)) {
                ah = SBS3.Conv.createSparse(startBlockUpper, lengthUpper, true);
                upperDense = false;
            } else {
                ah = core.createDenseNoPrefill(startBlockUpper, lengthUpper);
                upperDense = true;
            }

            if (lowerDense) {
                final long[] arrayLower = core.denseArray(al);
                if (arrayLower == null)
                    throw new IllegalStateException();
                assert SBS3.Conv.isDense(al);
                assert arrayLower.length == lengthLower;
                final long[] arraySource = core.denseArray(a0);
                //assert arraySource.length == arrayLower.length + arrayUpper.length;

                assert SBS3.Conv.startBlock(a0) == SBS3.Conv.startBlock(al);
                System.arraycopy(arraySource, 0, arrayLower, 0, lengthLower);
            }

            if (upperDense) {
                final long[] arrayUpper = core.denseArray(ah);

                if (arrayUpper == null)
                    throw new IllegalStateException();
                assert SBS3.Conv.isDense(ah);

                assert arrayUpper.length == lengthUpper;

                final long[] arraySource = core.denseArray(a0);
                //assert arraySource.length == arrayLower.length + arrayUpper.length;

                final int offset = Math.subtractExact(startBlockUpper, startBlock);
                assert offset >= 0;
                assert offset == arraySource.length - lengthUpper;
                System.arraycopy(arraySource, offset, arrayUpper, 0, lengthUpper);
            }

        }

        assert core.validateComponent(al);
        assert core.validateComponent(ah);

        assert componentIndex >= 0;
        assert core.array[componentIndex] != SBS3.EMPTY;
        final int upperComponentIndex = Math.incrementExact(componentIndex);
        core.setComponent(componentIndex, al);

        final int insertedAttempt1 = insertComponent(ah, upperComponentIndex, chasmSize, core);
        if (insertedAttempt1 >= 0)
            return returnLowerBlockComponent ? componentIndex : insertedAttempt1;

        expand(upperComponentIndex, chasmSize, capacityIncreaseFactor, core);

        final int insertedAttempt2 = insertComponent(ah, upperComponentIndex, chasmSize, core);
        if (insertedAttempt2 >= 0)
            return returnLowerBlockComponent ? componentIndex : insertedAttempt2;

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

        final int x = ComponentsHelper.findEmptySlot(block(a), core);
        if (x >= 0) {
            //core.array[x] = a;
            core.setComponent(x, a);
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
        //core.array[x2] = a;
        core.setComponent(x2, a);

        return x1;
    }

}
