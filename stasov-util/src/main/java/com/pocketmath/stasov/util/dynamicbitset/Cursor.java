package com.pocketmath.stasov.util.dynamicbitset;

import javax.swing.border.EmptyBorder;

/**
 * Created by etucker on 5/23/16.
 */
class Cursor {
    final SBS3 core;

    int componentIndex;
    boolean inSparse;
    boolean inDense;
    long startPos;
    long endPos;
    boolean sparseValue;

    /**
     * The first block at which a new component was first present.
     */
    int lastComponentBoundary;

    /**
     * The first block at which a new value was first present.
     */
    int lastValueBoundary;

    boolean lastValue;
    int currentChangeBlock;
    int previousChangeBlock;
    int denseDataIndex;
    long[] denseData;
    int changeComponentIndex;
    int previousChangeComponentIndex;

    private boolean validateInvariants() {
        if (inSparse == inDense && currentChangeBlock != -1) throw new IllegalStateException();
        if (startPos == endPos && startPos != 0) throw new IllegalStateException();
        if (currentChangeBlock < previousChangeBlock && currentChangeBlock != -1)
            throw new IllegalStateException("currentChangeBlock: " + currentChangeBlock + ", previousChangeBlock: " + previousChangeBlock);
        if (denseDataIndex >= 0 && denseData == null) throw new IllegalStateException();
        if (changeComponentIndex < previousChangeComponentIndex && changeComponentIndex != -1)
            throw new IllegalStateException();
        if (denseDataIndex > core.maxBlocksPerDense()) throw new IllegalStateException();
        if (componentIndex > core.maxBlock()) throw new IllegalStateException();
        return true;
    }

    public Cursor(final SBS3 core) {
        this.core = core;
        reset();
        assert (validateInvariants());
    }

    public final void reset(final int arrayIndex) {
        this.componentIndex = arrayIndex;
        inSparse = false;
        inDense = false;
        startPos = 0;
        endPos = 0;
        sparseValue = false;

        lastComponentBoundary = -1;
        lastValueBoundary = -1;

        lastValue = false;
        currentChangeBlock = -1;
        previousChangeBlock = -1;
        denseDataIndex = -1;
        denseData = null;
        changeComponentIndex = -1;
        previousChangeComponentIndex = -1;

        assert (validateInvariants());
    }

    public void reset() {
        reset(-1);
    }

    //public void resetInPlace() {
    //    reset(Math.max(-1, componentIndex - 1));
    //}

    public SBS3 getCore() {
        return core;
    }

    public boolean inSparse() {
        return inSparse;
    }

    public void setInSparse() {
        assert (validateInvariants());
        this.inSparse = true;
        this.inDense = false;
        assert (validateInvariants());
    }

    public boolean inDense() {
        return inDense;
    }

    public void setInDense() {
        assert (validateInvariants());
        this.inSparse = false;
        this.inDense = true;
        assert (validateInvariants());
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        assert (validateInvariants());
        this.startPos = startPos;
        assert (validateInvariants());
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        assert (validateInvariants());
        this.endPos = endPos;
        assert (validateInvariants());
    }

    public int getStartBlock() {
        final long r = getStartPos() / 64L;
        if (r > Integer.MAX_VALUE) throw new IllegalStateException();
        return (int) r;
    }

    public int getEndBlock() {
        final long r = getEndPos() / 64L;
        if (r > Integer.MAX_VALUE) throw new IllegalStateException();
        return (int) r;
    }

    public boolean isSparseValue() {
        return sparseValue;
    }

    public void setSparseValue(boolean sparseValue) {
        assert (validateInvariants());
        this.sparseValue = sparseValue;
        assert (validateInvariants());
    }

    public boolean getLastValue() {
        return lastValue;
    }

    public void setLastValue(boolean lastValue) {
        assert (validateInvariants());
        this.lastValue = lastValue;
        assert (validateInvariants());
    }

    public int getCurrentChangeBlock() {
        return currentChangeBlock;
    }

    public void setCurrentChangeBlock(int currentChangeBlock) {
        assert (validateInvariants());
        this.currentChangeBlock = currentChangeBlock;
        assert (validateInvariants());
    }

    public int getPreviousChangeBlock() {
        return previousChangeBlock;
    }

    public void setPreviousChangeBlock(int previousChangeBlock) {
        assert (validateInvariants());
        this.previousChangeBlock = previousChangeBlock;
        assert (validateInvariants());
    }

    public int getChangeComponentIndex() {
        return changeComponentIndex;
    }

    public int getPreviousChangeComponentIndex() {
        return previousChangeComponentIndex;
    }

    public boolean isPastEnd() {
        assert (validateInvariants());
        assert componentIndex <= core.dataSize;
        return componentIndex >= core.dataSize;
    }

    public boolean beforeStart() {
        return currentChangeBlock < 0;
    }

    public boolean endsBeforeStartOf(final Cursor o) {
        assert (getEndPos() < o.getStartPos()) == (getEndBlock() < o.getStartBlock());
        return getEndPos() < o.getStartPos();
    }

    public boolean endsBeforeEndOf(final Cursor o) {
        assert (getEndPos() < o.getEndPos()) == (getEndBlock() < o.getEndBlock());
        return getEndPos() < o.getEndPos();
    }

    public boolean endsAfterEndOf(final Cursor o) {
        assert (getEndPos() > o.getEndPos()) == (getEndBlock() > o.getEndBlock());
        return getEndPos() > o.getEndPos();
    }

    public boolean endsAtEndOf(final Cursor o) {
        assert (getEndPos() == o.getEndPos()) == (getEndBlock() == o.getEndBlock());
        return getEndPos() == o.getEndPos();
    }

    public boolean startsBeforeStartOf(final Cursor o) {
        assert (getStartPos() < o.getStartPos()) == (getStartBlock() < o.getStartBlock());
        return getStartPos() < o.getStartPos();
    }

    public boolean startsAfterStartOf(final Cursor o) {
        assert (getStartPos() > o.getStartPos()) == (getStartBlock() > o.getStartBlock());
        return getStartPos() > o.getStartPos();
    }

    public boolean startsAtStartOf(final Cursor o) {
        assert (getStartPos() == o.getStartPos()) == (getStartBlock() == o.getStartBlock());
        return getStartPos() == o.getStartPos();
    }

    protected boolean hasNextComponent(final int fromIndex) {
        return fromIndex + 1 < core.dataSize;
    }

    /**
     * Moves to the next position that's enclosed by a single sparse or dense component.
     *
     * @return the array index of the next component with negative if sparse else positive if dense
     */
    protected int nextComponentIndex(final int fromIndex) {
        int nextCI = fromIndex + 1;
        if (nextCI >= core.dataSize) {
            throw new IllegalStateException();
        }
        return nextCI;
    }

    private boolean componentIsSparse(final int index) {
        return SBS3.Conv.isSparse(core.getComponent(index));
    }

    private boolean componentIsDense(final int index) {
        return SBS3.Conv.isDense(core.getComponent(index));
    }

    private int firstSetBitBlockOffset(final long a) {
        if (SBS3.Conv.isSparse(a)) {
            return SBS3.Conv.sparseValue(a) ? 0 : -1;
        } else {
            assert SBS3.Conv.isDense(a);
            final int denseIndex = SBS3.Conv.denseIndex(a);
            final long[] denseData = core.dense[denseIndex];
            for (int i = 0; i < denseData.length; i++) {
                if (denseData[i] != SBS3.ALL_CLEAR)
                    return i;
            }
            return -1;
        }
    }

    private int firstClearBitBlockOffset(final long a) {
        if (SBS3.Conv.isSparse(a)) {
            return !SBS3.Conv.sparseValue(a) ? 0 : -1;
        } else {
            assert SBS3.Conv.isDense(a);
            final int denseIndex = SBS3.Conv.denseIndex(a);
            final long[] denseData = core.dense[denseIndex];
            for (int i = 0; i < denseData.length; i++) {
                if (denseData[i] != SBS3.ALL_SET)
                    return i;
            }
            return -1;
        }
    }

    private boolean firstBitValue(final long a) {
        if (SBS3.Conv.isSparse(a)) {
            return SBS3.Conv.sparseValue(a);
        } else {
            assert SBS3.Conv.isDense(a);
            final int denseIndex = SBS3.Conv.denseIndex(a);
            final long[] denseData = core.dense[denseIndex];
            assert denseData.length > 0;
            return (denseData[0] & 0x8000000000000000L) != 0L;
        }
    }

    /**
     *
     * @param denseData
     * @param currentIndex
     * @return next index
     */
    private int nextClearBlockOffsetDense(
            final long[] denseData,
            int currentIndex) {
        for (int i = currentIndex; i < denseData.length; i++) {
            if (denseData[i] == SBS3.ALL_CLEAR) {
                return i;
            }
        }
        return -1;
    }

    /**
     *
     * @param denseData
     * @param currentIndex
     * @return next index
     */
    private int nextSetBlockOffsetDense(
            final long[] denseData,
            int currentIndex) {
        for (int i = currentIndex; i < denseData.length; i++) {
            if (denseData[i] != SBS3.ALL_CLEAR) {
                return i;
            }
        }
        return -1;
    }

    public int nextBlock() {
        assert validateInvariants();

        if (beforeStart()) {
            int ci = -1;
            if (!hasNextComponent(ci))
                return -1;
            ci = nextComponentIndex(ci);
            final long component = core.getComponent(ci);
            if (SBS3.Conv.isSparse(component)) {
                setInSparse();
            } else {
                assert SBS3.Conv.isDense(component);
                final int denseIndex = SBS3.Conv.denseIndex(component);
                denseData = core.getDenseData(denseIndex);
                denseDataIndex = 0;
                setInDense();
            }
            if (core.length(component) <= 0)
                throw new UnsupportedOperationException();
            lastValue = firstBitValue(component);
            currentChangeBlock = 0;
            componentIndex = ci;
            return 0;
        } else {
            assert currentChangeBlock >= 0;
            assert componentIndex >= 0;
        }

        int ci = componentIndex;

        for (;;) {
            if (inDense && denseDataIndex < denseData.length) {
                // we're inside a dense component
                final int blockOffset =
                        lastValue ?
                                nextClearBlockOffsetDense(denseData, denseDataIndex) :
                                nextSetBlockOffsetDense(denseData, denseDataIndex);
                if (blockOffset < 0) {
                    if (hasNextComponent(ci)) {
                        ci = nextComponentIndex(ci);
                        // We are on a component change boundary.
                    } else {
                        return -1;
                    }
                } else {
                    final long component = core.getComponent(componentIndex);
                    assert blockOffset >= 0;
                    assert Math.addExact(
                            blockOffset,
                            SBS3.Conv.startBlock(component))
                            <= core.maxBlock();
                    assert Math.addExact(denseDataIndex, blockOffset) < core.maxBlocksPerDense();
                    lastValue = !lastValue;
                    denseDataIndex += blockOffset;
                    assert validateInvariants();
                    final int block = SBS3.Conv.startBlock(component) + blockOffset;
                    previousChangeBlock = currentChangeBlock;
                    currentChangeBlock = block;
                    return block; // We are on a value change boundary.
                }
            } else if (hasNextComponent(ci)) {
                // not in dense or have exhausted dense, so move forward into a new
                // component (which may be dense or sparse)
                ci = nextComponentIndex(ci);
                // We are on a component change boundary.
                componentIndex = ci;
                final long component = core.getComponent(componentIndex);
                if (SBS3.Conv.isDense(component)) {
                    final int denseIndex = SBS3.Conv.denseIndex(component);
                    denseData = core.getDenseData(denseIndex);
                    denseDataIndex = 0;
                    setInDense();
                    // now continue the loop which will engage this as dense component
                    // from its beginning
                } else {
                    assert SBS3.Conv.isSparse(component);
                    final boolean value = SBS3.Conv.sparseValue(component);
                    if (value != lastValue) {
                        lastValue = !lastValue;
                        final int block = SBS3.Conv.startBlock(component);
                        previousChangeBlock = currentChangeBlock;
                        currentChangeBlock = block;
                        setInSparse();
                        return block; // We are on a value change boundary.
                    } else {
                        // continue the loop to find the next component
                    }
                }
            } else {
                // no next component, so return -1
                assert !hasNextComponent(componentIndex);
                return -1;
            }
        }
    }

    /**
     *
     * For sparse blocks, change is from true to false or false to true.
     * Dense block changes are indicated as a switch from non-empty (true) to empty (false) and vice versa.
     *
     * @return the next block with a change.
     */
    /*
    public int nextChangeBlock2() {
        assert (validateInvariants());
        if (inDense()) {
            for (; denseDataIndex < denseData.length; denseDataIndex++) {
                final boolean denseAllClear = denseData[denseDataIndex] == SBS3.ALL_CLEAR;
                if ((!denseAllClear) ^ lastValue) {
                    lastValue = !lastValue;
                    previousChangeBlock = currentChangeBlock;
                    currentChangeBlock = getStartBlock() + denseDataIndex;
                    previousChangeComponentIndex = changeComponentIndex;
                    changeComponentIndex = componentIndex;
                    assert (validateInvariants());
                    return currentChangeBlock;
                }
            }
        }
        for (; componentIndex < core.dataSize - 1; ) {
            componentIndex++;
            assert componentIndex >= 0 : "componentIndex = " + componentIndex;
            final long a = core.array[componentIndex];
            setStartPos(core.startPosition(a));
            setEndPos(core.endPosition(a));
            if (SBS3.Conv.isSparse(a)) {
                setInSparse();
                sparseValue = SBS3.Conv.sparseValue(a);
                if (sparseValue ^ lastValue || beforeStart()) {
                    lastValue = beforeStart() ? !sparseValue : !lastValue;
                    previousChangeBlock = currentChangeBlock;
                    currentChangeBlock = getStartBlock();
                    previousChangeComponentIndex = changeComponentIndex;
                    changeComponentIndex = componentIndex;
                    assert (validateInvariants());
                    return currentChangeBlock;
                }
            } else {
                assert (SBS3.Conv.isDense(a));
                setInDense();
                final int denseIndex = SBS3.Conv.denseIndex(a);
                denseData = core.dense[denseIndex];
                for (denseDataIndex = 0; denseDataIndex < denseData.length; denseDataIndex++) {
                    final boolean denseAllClear = denseData[denseDataIndex] == SBS3.ALL_CLEAR;
                    if (denseAllClear && lastValue || !denseAllClear && !lastValue || beforeStart()) {
                        lastValue = beforeStart() ? denseAllClear : !lastValue;
                        previousChangeBlock = currentChangeBlock;
                        currentChangeBlock = getStartBlock() + denseDataIndex;
                        previousChangeComponentIndex = changeComponentIndex;
                        changeComponentIndex = componentIndex;
                        assert (validateInvariants());
                        return currentChangeBlock;
                    }
                }
            }
        }
        assert (validateInvariants());
        return -1;
    }
    */

    public boolean getPreviousValue() {
        return !lastValue; // TODO: this is a hack ...
    }

    public long getBlock(final int index) {
        assert (validateInvariants());
        return core.array[index];
    }

    public void setBlock(final int index, final long value) {
        assert (validateInvariants());
        core.array[index] = value;
        assert (validateInvariants());
    }

    public int maxIndex() {
        return core.dataSize;
    }

    private int backwardComponentIndex(final int currentIndex, final int distance) {
        int i = currentIndex - 1;
        int travelled = 0;
        for (;;) {
            if (i < 0) {
                return -1;
            } else if (core.array[i] == SBS3.EMPTY) {
                i--;
            } else if (travelled < distance) {
                travelled++;
                if (travelled >= distance) {
                    assert travelled == distance;
                    return i;
                }
            }
        }
    }

    private int backwardComponentIndex(final int currentIndex) {
        return backwardComponentIndex(currentIndex, 1);
    }

    private int forwardComponentIndex(final int currentIndex, final int distance) {
        int i = currentIndex + 1;
        int travelled = 0;
        for (;;) {
            if (i >= core.dataSize) {
                return -1;
            } else if (core.array[i] == SBS3.EMPTY) {
                i++;
            } else if (travelled < distance) {
                travelled++;
                if (travelled >= distance) {
                    assert travelled == distance;
                    return i;
                }
            }
        }
    }

    private int forwardComponentIndex(final int currentIndex) {
        return forwardComponentIndex(currentIndex, 1);
    }

    public boolean nextComponent() {
        final int r = forwardComponentIndex(componentIndex);
        if (r >= 0) {
            componentIndex = r;
            return true;
        } else {
            return false;
        }

        /*
        if (componentIndex < core.dataSize) {
            componentIndex++;

            for (;;) {
                if (componentIndex >= core.dataSize) {
                    return false;
                } else if (core.array[componentIndex] == SBS3.EMPTY) {
                    componentIndex++;
                } else {
                    return true;
                }
            }
        } else {
            return false;
        }
        */
    }

    /**
     *
     * @param distance how many components ahead to look
     * @return the component or EMPTY if no such component exists (past end)
     */
    public long lookAheadComponents(final int distance) {
        final int r = forwardComponentIndex(componentIndex, distance);
        if (r >= 0) {
            return core.array[r];
        } else {
            return SBS3.EMPTY;
        }

        /*
        assert distance >= 0;
        int travelled = 0;
        int i = componentIndex;
        for (;;) {
            if (i >= core.dataSize) {
                assert i == core.dataSize;
                return SBS3.EMPTY;
            } else if (core.array[i] == SBS3.EMPTY) {
                i++;
            } else {
                travelled++;
                if (travelled >= distance) {
                    assert travelled == distance;
                    return core.array[i];
                }
            }
        }
        */
    }

    public int peekNextComponentEndBlock() {
        final int r = forwardComponentIndex(componentIndex);
        if (r >= 0) {
            final long a = core.array[r];
            return core.endBlock(a);
        } else {
            return -1;
        }

        /*
        int i = componentIndex + 1;
        for (;;) {
            if (i >= core.dataSize) {
                return -1;
            } else if (core.array[i] == SBS3.EMPTY) {
                i++;
            } else {
                return core.length(core.array[i]);
            }
        }
        */
    }

    public long getComponent() {
        assert componentIndex >= 0;
        assert componentIndex <= core.dataSize;
        assert core.dataSize <= core.array.length; // TODO: Move this logic to SBS class
        final long a = core.array[componentIndex];
        assert a != SBS3.EMPTY;
        return a;
    }

    public boolean getSparseValue() {
        final long a = getComponent();
        return SBS3.Conv.sparseValue(a);
    }

    public long[] getDenseData() {
        final long a = getComponent();
        final int denseIndex = SBS3.Conv.denseIndex(a);
        assert denseIndex >= 0;
        final long[] retVal = core.getDenseData(denseIndex);
        assert retVal != null;
        return retVal;
    }

    public void convertToDense() {
        final long a = getComponent();
        assert SBS3.Conv.isSparse(a);
        assert !SBS3.Conv.isDense(a);

        core.convertToDense(componentIndex);

        assert SBS3.Conv.isDense(getComponent());
    }

    public static enum SplitCursorBlock {
        BOTTOM,
        TOP,
        FIRST_OF_SPLIT
    }

    private int split(final int block) {
        
    }

    /**
     *
     * @param block first block
     * @return
     */
    public void split(final int block, final SplitCursorBlock scb) {
        final int newComponentIndex = core.split(block);
        switch (scb) {
            case BOTTOM =
            case FIRST_OF_SPLIT : { componentIndex = newComponentIndex; break; }
        }
    }

}
