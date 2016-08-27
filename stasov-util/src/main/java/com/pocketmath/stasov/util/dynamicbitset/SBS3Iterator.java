package com.pocketmath.stasov.util.dynamicbitset;

import com.pocketmath.stasov.util.IResettable;
import it.unimi.dsi.fastutil.longs.LongIterator;

/**
 * Created by etucker on 8/9/16.
 */
public class SBS3Iterator implements LongIterator, IResettable {

    private SBS3 core;
    private long n = -1;

    SBS3Iterator(final SBS3 core) {
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

    public void reset() {
        n = -1;
        index = -1;
        position = -1;
        denseUntil = -1;
        sparseUntil = -1;

        denseStartPosition = -1;
        dd = null;
        ddIndex = -1;
        d = SBS3.EMPTY;
        dBitPos = -1;
    }

    SBS3Iterator resetAndLoad(final SBS3 core) {
        reset();
        this.core = core;
        return this;
    }

    private void resetSparse() {
        sparseUntil = -1;
    }

    private void resetDense() {
        denseUntil = -1;
        dd = null;
        ddIndex = 0;
        d = SBS3.EMPTY;
        dBitPos = 0;
    }

    private boolean inDense() {
        return denseUntil >= 0 && position < denseUntil;
    }

    private boolean inSparse() {
        return sparseUntil >= 0 && position < sparseUntil;
    }

    private boolean inSparse(final long position) {
        return sparseUntil >= 0 && sparseUntil < position;
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

    private long advanceDense() {
        for (; ddIndex < dd.length; ddIndex++) {
            d = dd[ddIndex];
            if (dBitPos >= 64)
                dBitPos = 0;
            for (; dBitPos < 64; dBitPos++) {
                if (((1L << dBitPos) & d) != 0) {
                    final long newPosition =
                            Math.addExact(
                                    Math.addExact(denseStartPosition, ddIndex),
                                    dBitPos);
                    assert newPosition >= position;
                    position = newPosition;
                    assert position < denseUntil;
                    dBitPos++;
                    if (dBitPos >= 64)
                        ddIndex++;
                    assert inDense() : "position: " + position;
                    return position;
                }
            }
        }
        return -1L;
    }

    private long nextSetPosition() {
        for (;;) {

            if (index > core.end) {
                return -1L;
            }

            if (inSparse(position+1)) {
                position++;
                return position;
            } else if (inDense()) {
                for (; ddIndex < dd.length; ddIndex++) {
                    d = dd[ddIndex];

                    if (dBitPos >= 64)
                        dBitPos = 0;

                    final long r = advanceDense();
                    if (r >= 0)
                        return r;
                }
            }

            index++;
            if (index > core.end) {
                return -1L;
            }

            final long a = core.getComponent(index);
            if (a == core.EMPTY)
                continue;

            if (SBS3.Conv.isSparse(a)) {
                resetDense();
                if (SBS3.Conv.sparseValue(a)) {
                    final int startBlock = SBS3.Conv.startBlock(a);
                    final int length = SBS3.Conv.sparseLength(a);
                    final long startPosition = core.startPosition(a);
                    assert length > 0;
                    sparseUntil = Math.addExact(startBlock, length);
                    position = startPosition;
                    assert sparseUntil > 0;
                    assert position >= 0;
                } else {
                    continue;
                }
            } else if (SBS3.Conv.isDense(a)) {
                resetSparse();
                final int denseIndex = SBS3.Conv.denseIndex(a);
                dd = core.getDenseData(denseIndex);
                denseUntil = Math.incrementExact(core.endPosition(a));
                denseStartPosition = SBS3.Conv.startPosition(a);
                assert denseUntil > 0;
                assert denseStartPosition >= 0;
                ddIndex = 0;
                dBitPos = 0;

                final long r = advanceDense();
                if (r >= 0)
                    return r;
            } else {
                throw new IllegalStateException();
            }
            assert inSparse() ^ inDense();
        }
    }

}
