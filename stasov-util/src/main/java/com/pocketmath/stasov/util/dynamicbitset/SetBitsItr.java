package com.pocketmath.stasov.util.dynamicbitset;

import it.unimi.dsi.fastutil.longs.LongIterator;

/**
 * Created by etucker on 5/23/16.
 */
class SetBitsItr implements LongIterator {

    private SBS3 core;
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

    public SetBitsItr(SBS3 core) {
        this.core = core;
    }

    private long nextNextLong() {

        loop0:
        while (arrayIndex < core.end) {
            if (core.getComponent(arrayIndex) == SBS3.EMPTY) {
                arrayIndex++;
                continue;
            }
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
                    if (arrayIndex >= core.end)
                        return -1L;

                    final long a = core.array[arrayIndex];
                    startPosition = SBS3.Conv.startPosition(a);

                    if (SBS3.Conv.isSparse(a)) {
                        if (!SBS3.Conv.sparseValue(a)) {
                            continue loop1;
                        } else {
                            inPositiveSparse = true;
                            inDense = false;

                            sparsePosition = 0;
                            sparsePositionLength = Math.multiplyExact(64, SBS3.Conv.sparseLength(a));
                            break loop1;
                        }
                    } else {
                        assert (SBS3.Conv.isDense(a));
                        inPositiveSparse = false;
                        inDense = true;

                        final int denseIndex = SBS3.Conv.denseIndex(a);
                        denseDataIndex = 0;
                        denseData = core.dense[denseIndex];
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
