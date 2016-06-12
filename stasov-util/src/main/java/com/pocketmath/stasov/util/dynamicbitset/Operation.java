package com.pocketmath.stasov.util.dynamicbitset;

import com.pocketmath.stasov.util.validate.ValidationException;
import com.pocketmath.stasov.util.validate.ValidationRuntimeException;

/**
 * Created by etucker on 6/7/16.
 */
abstract class Operation {

    private boolean validateInBounds(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) throws ValidationRuntimeException {
        try {
            if (!(startBlock >= SBS3.minBlock())) throw new ValidationException();
            if (!(endBlock >= SBS3.minBlock())) throw new ValidationException();
            if (!(startBlock <= SBS3.maxBlock())) throw new ValidationException();
            if (!(endBlock <= SBS3.maxBlock())) throw new ValidationException();
            if (!(s0.getStartBlock() >= startBlock)) throw new ValidationException();
            if (!(s0.getEndBlock() <= endBlock)) throw new ValidationException();
            if (!(s1.getStartBlock() >= startBlock)) throw new ValidationException();
            if (!(s1.getEndBlock() <= endBlock)) throw new ValidationException();
        } catch (final ValidationException ve) {
            throw new ValidationRuntimeException(ve);
        }
        return true;
    }

    /**
     * Operation will be skipped at the then-current index if this returns true.
     *
     * @param s0
     * @param s1
     * @return
     */
    protected abstract boolean noChanges(final Cursor s0, final Cursor s1);

    /**
     * Operation will be skipped at the then-current index if this returns true.
     *
     * @param v1
     * @return
     */
    protected abstract boolean noChange1(final boolean v1);

    /**
     * Operation will be skipped at the then-current index if this returns true.
     *
     * @param v1
     * @return
     */
    protected abstract boolean noChange1(final long v1);

    /**
     * Operation on two sparse components.
     *
     * @param v0 sparse component
     * @param v1 sparse component
     * @return
     */
    protected abstract boolean op(final boolean v0, final boolean v1);

    /**
     * Operation on a sparse component and a sub section of a dense component.
     *
     * @param v0 sparse component
     * @param v1 subsection of dense component
     * @return
     */
    protected abstract SBS3.OpResult op(final boolean v0, final long v1);

    /**
     * Operation on a sub section of a dense component and a sparse component.
     *
     * @param v0 subsection of dense component
     * @param v1 sparse component
     * @return
     */
    protected abstract long op(final long v0, final boolean v1);

    /**
     * Operation on a sub section each of two dense components.
     *
     * @param v0 subsection of dense component
     * @param v1 subsection of dense component
     * @return
     */
    protected abstract long op(final long v0, final long v1);

    public void op(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
        assert validateInBounds(s0, s1, startBlock, endBlock);

        if (noChanges(s0, s1)) {
            // no changes necessary
            return;
        }

        // splits occur if and as necessary
        lowerSplit(s0, s1, startBlock, endBlock);
        lookaheadWithUpperSplit(s0, s1, startBlock, endBlock);

        assert SBS3.Conv.startBlock(s0.getComponent()) == startBlock;
        assert s0.getCore().length(s0.getComponent()) == endBlock - startBlock;

        doOp(s0, s1, startBlock, endBlock);
    }

    private void doOp(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {

        if (s0.inSparse()) {
            if (s1.inSparse()) {
                assert s0.inSparse() && s1.inSparse();
                s0.setSparseValue(
                        op(s0.getSparseValue(), s1.getSparseValue()));
                return;
            } else {
                assert s0.inSparse() && s1.inDense();
                final int startBlock0 = s0.getStartBlock(), startBlock1 = s1.getStartBlock();
                final boolean v0 = s0.getSparseValue();
                final long[] denseData1 = s1.getDenseData();
                final int len = endBlock - startBlock;
                assert len >= 0;

                int i = 0;
                if (i < len) {
                    final SBS3.OpResult firstResultValue = op(v0, denseData1[i]);
                    if (firstResultValue == SBS3.OpResult.INDETERMINATE) {
                        s0.convertToDense();
                        assert s0.inDense();
                        doOp(s0, s1, startBlock + i, endBlock);
                        return;
                    }
                    i++;
                    for (; i < len; i++) {
                        final SBS3.OpResult opResult = op(v0, denseData1[i]);
                        if (opResult != firstResultValue) {
                            assert opResult == SBS3.OpResult.INDETERMINATE
                                    || opResult ==
                                    (firstResultValue == SBS3.OpResult.TRUE ? SBS3.OpResult.FALSE : SBS3.OpResult.TRUE);
                            s0.convertToDense();
                            assert s0.inDense();
                            doOp(s0, s1, startBlock + i, endBlock);
                            return;
                        }
                    }
                    assert s0.inSparse();
                    assert firstResultValue != SBS3.OpResult.INDETERMINATE;
                    assert SBS3.OpResult.values().length == 3; // so that if we change the structure we remember to check or change this logic
                    switch (firstResultValue) {
                        case TRUE : { s0.setSparseValue(true); break; }
                        case FALSE : { s1.setSparseValue(false); break; }
                        default : throw new IllegalStateException();
                    }
                    return;
                }
            }
        } else {
            assert s0.inDense();
            if (s1.inSparse()) {
                assert s0.inDense() && s1.inSparse();

                final int startBlock0 = s0.getStartBlock(), startBlock1 = s1.getStartBlock();
                final long[] denseData0 = s0.getDenseData();
                final boolean v1 = s1.getSparseValue();
                final int len = endBlock - startBlock;
                assert len >= 0;

                final int start0 = startBlock - startBlock0;
                final int end0 = start0 + len;
                assert start0 > 0;
                assert end0 > 0;

                for (int i = start0; i < end0; i++) {
                    denseData0[i] = op(denseData0[i], v1);
                }
                return;
            } else {
                assert s0.inDense() && s1.inDense();

                final int startBlock0 = s0.getStartBlock(), startBlock1 = s1.getStartBlock();
                final long[] denseData0 = s0.getDenseData(), denseData1 = s1.getDenseData();
                final int len = endBlock - startBlock;

                final int start0 = startBlock - startBlock0, start1 = startBlock - startBlock1;
                final int end0 = start0 + len;
                assert start0 > 0;
                assert end0 > 0;

                final int offset1 = start1 - start0;
                assert offset1 > 0;

                for (int i = start0; i < end0; i++) {
                    denseData0[i] = op(denseData0[i], denseData1[i + offset1]);
                }
                return;
            }
        }
    }

    private long lowerSplit(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
        // check if we need to split or can merge
        if (s0.startsAfterStartOf(s1)) {
            // lower split will be needed
            // (not maybe -- definitely will -- because we would not encounter this case now otherwise)
            return s0.splitLower(startBlock);
        }
        return SBS3.EMPTY;
    }

    private long lookaheadWithUpperSplit(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {

        // look ahead to see if upper split will be warranted
        // if it is, make the split!
        if (s0.endsAfterEndOf(s1)) {
            // upper split may be needed
            // do the look-ahead
            final int s0EndBlock = s0.getEndBlock();
            int s1LookaheadComponents = 0;
            loop1:
            for (; ; s1LookaheadComponents++) {
                final long s1LA = s1.lookAheadComponents(s1LookaheadComponents);
                if (s1LA == SBS3.EMPTY)
                    break loop1;
                if (SBS3.Conv.isSparse(s1LA)) {
                    if (!noChange1(SBS3.Conv.sparseValue(s1LA))) {
                        // could consider this for merge
                        continue loop1;
                    }
                } else {
                    assert SBS3.Conv.isDense(s1LA);
                    // look through the dense, and if we get to the end without encountering a non-zero value
                    // then we don't need to split
                    final int s1LAStartBlock = SBS3.Conv.startBlock(s1LA);
                    final long[] denseData = s1.getCore().getDenseData(SBS3.Conv.denseIndex(s1LA));
                    for (int i = 0; i < denseData.length && i + s1LAStartBlock <= s0EndBlock; i++) {
                        if (!noChange1(denseData[i])) {
                            // must upper split
                            final int firstBlockOfSecondComponent = i + s1LAStartBlock;
                            assert firstBlockOfSecondComponent >= 0;
                            return s0.splitUpper(firstBlockOfSecondComponent);
                        }
                    }
                }
            }
        }
        return SBS3.EMPTY;
    }

/*
    public void ss(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
        assert s0.inSparse();
        assert s1.inSparse();
        assert validateInBounds(s0, s1, startBlock, endBlock);
    }
    public void sd(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
        assert s0.inSparse();
        assert s1.inDense();
        assert validateInBounds(s0, s1, startBlock, endBlock);
    }
    public void ds(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
        assert s0.inDense();
        assert s1.inSparse();
        assert validateInBounds(s0, s1, startBlock, endBlock);
    }
    public void dd(final Cursor s0, final Cursor s1, final int startBlock, final int endBlock) {
        assert s0.inDense();
        assert s1.inDense();
        assert validateInBounds(s0, s1, startBlock, endBlock);
    }
    */
}
