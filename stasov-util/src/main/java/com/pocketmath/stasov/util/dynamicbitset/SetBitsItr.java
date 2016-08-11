package com.pocketmath.stasov.util.dynamicbitset;

import it.unimi.dsi.fastutil.longs.LongIterator;

/**
 * Created by etucker on 8/9/16.
 */
class SetBitsItr implements LongIterator {

    private SBS3 core;
    private long n = -1;

    SetBitsItr(final SBS3 core) {
        this.core = core;
        this.n = nextSetPosition();
    }

    private int index = -1;
    private long position = -1;

    private long denseUntil = -1, sparseUntil = -1;

    private long denseStartPosition = -1;
    private long[] dd = null;
    private int ddIndex = -1;
    private long d = SBS3.EMPTY;
    private short dBitPos = -1;

    private boolean inDense() {
        return denseUntil >= 0 && denseUntil < position;
    }

    private boolean inSparse() {
        return sparseUntil >= 0 && denseUntil < position;
    }

    @Override
    public long nextLong() {
        final long r = n;
        n = nextSetPosition();
        return r;
    }

    @Override
    public int skip(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        return n >= 0;
    }

    @Override
    public Long next() {
        return nextLong();
    }

    private long nextSetPosition() {
        for (;;) {

            position++;

            if (inSparse()) {
                return position;
            } else if (inDense()) {
                for (; ddIndex < dd.length; ddIndex++) {
                    d = dd[ddIndex];
                    for (; dBitPos < 64; dBitPos++) {
                        if (((1L << dBitPos) & d) > 0) {
                            position =
                                    Math.addExact(
                                            Math.addExact(denseStartPosition, ddIndex),
                                            dBitPos);
                            return position;
                        }
                    }
                }
            }

            index++;
            if (index > core.end) {
                // set so that these don't infinitely increase causing overflow
                index--;
                position = -1;

                return -1L;
            }

            final long a = core.getComponent(index);
            if (a == core.EMPTY)
                continue;

            if (SBS3.Conv.isSparse(a)) {
                if (SBS3.Conv.sparseValue(a)) {
                    final int startBlock = SBS3.Conv.startBlock(a);
                    final int length = SBS3.Conv.sparseLength(a);
                    final long startPosition = core.startPosition(a);
                    sparseUntil = Math.addExact(startBlock, length);
                    position = startPosition;
                } else {
                    continue;
                }
            } else if (SBS3.Conv.isDense(a)) {
                final int denseIndex = SBS3.Conv.denseIndex(a);
                final long[] dd = core.getDenseData(denseIndex);
                denseUntil = core.endPosition(a);
                denseStartPosition = SBS3.Conv.startPosition(a);
                ddIndex = 0;
                dBitPos = 0;
                for (; ddIndex < dd.length; ddIndex++) {
                    d = dd[ddIndex];
                    for (; dBitPos < 64; dBitPos++) {
                        if (((1L << dBitPos) & d) > 0) {
                            position =
                                    Math.addExact(
                                        Math.addExact(denseStartPosition, ddIndex),
                                        dBitPos);
                            return position;
                        }
                    }
                }
            }
        }
    }

}
