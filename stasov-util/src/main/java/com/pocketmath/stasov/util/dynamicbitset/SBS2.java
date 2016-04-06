package com.pocketmath.stasov.util.dynamicbitset;

import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.objects.*;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.*;

/**
 * A position is the numerical place indicator of a bit which can be set or unset.
 *
 * An index is a place in an array.
 *
 * Created by etucker on 3/24/16.
 */
abstract class SBS2 {

    /**
     * For each item:
     *   Bits  0-31:  starting position value
     *   Bits 32-62:  sparse: length, dense: array index of node
     *   Bit     63:  0=sparse; 1=dense
     */
    private long[] ranges = null;

    private final int chunkSize = 512;

    private static Comparator<HMNode> HMNODE_COMPARATOR_WITH_NULL = new Comparator<HMNode>() {
        @Override
        public int compare(final @Nonnull HMNode o1, final @Nonnull HMNode o2) {
            if (o1 == null) {
                if (o2 == null) return 0;
                return 1;
            }  else {
                if (o2 == null) return -1;
            }
            return Long.compare(o1.startPosition, o2.startPosition);
        }
    };

    private static Comparator<HMNode> HMNODE_COMPARATOR = new Comparator<HMNode>() {
        @Override
        public int compare(final @Nonnull HMNode o1, final @Nonnull HMNode o2) {
            return Long.compare(o1.startPosition, o2.startPosition);
        }
    };

    private static class HMNode {
        private final int startPosition;
        private final long[] data;

        public HMNode(
                final @Nonnegative int startPosition,
                final @Nonnegative int endPosition,
                final boolean bitsSet) {

            assert(startPosition % 64 == 0);
            assert(endPosition % 64 == 0);
            this.startPosition = startPosition;
            int start = startPosition / 64;
            int end = Math.addExact(Math.addExact(endPosition / 64, (endPosition % 64 > 0 ? 1 : 0)), 1); // TODO: make faster
            int len = Math.subtractExact(end, start); // TODO: make faster
            this.data = new long[len];
            if (bitsSet)
                Arrays.fill(this.data, 0xFFFFFFFFFFFFFFFFL);
        }

        public int getStartPosition() {
            return startPosition;
        }

        public int getEndPosition() {
            return startPosition + data.length;
        }

        public long[] getData() {
            return data;
        }

        public int getSize() {
            return data.length;
        }

    }

    private static class HM {
        private HMNode[] nodes;
        private Object2IntMap nodesCache = null;
        private int size = 0;

        public HM(final int capacity) {
            this.nodes = new HMNode[capacity];
        }

        public int getIndex(final HMNode node) {
            Objects.requireNonNull(node);
            if (nodesCache == null) {
                for (int i = 0; i < nodes.length; i++) {
                    if (node.equals(nodes[i]))
                        return i;
                }
            } else {
                return nodesCache.getInt(node);
            }
            return -1;
        }

        public boolean contains(final HMNode node) {
            return getIndex(node) >= 0;
        }

        protected void growIfNeeded() {
            final int capacity = nodes.length;
            if (capacity <= size) {
                final int newSize = Math.min(0x7FFFFFFF, Math.max(Math.multiplyExact(capacity,2), Math.addExact(capacity,2)));
                final HMNode[] newArray = new HMNode[newSize];
                System.arraycopy(nodes, 0, newArray, 0, nodes.length);
            }
        }

        protected void buildCacheIfNeeded() {
            if (nodesCache == null) {
                if (size > 128) {
                    nodesCache = new Object2IntOpenHashMap<>();
                    nodesCache.defaultReturnValue(-1);
                    for (int i = 0; i < nodes.length; i++) {
                        final HMNode n = nodes[i];
                        if (n != null) {
                            nodesCache.put(n, i);
                        }
                    }
                }
            }
        }

        public int add(HMNode node) {
            int index;

            index = getIndex(node);
            if (index >= 0) return index;

            growIfNeeded();
            buildCacheIfNeeded();
            index = size++;
            nodes[index] = node;
            if (nodesCache != null)
                nodesCache.put(node, index);
            return index;
        }

        public void remove(int index) {
            nodes[index] = null;
            size--;
        }

        public HMNode getNode(int index) {
            return nodes[index];
        }
    }

    private HM map = null;

    public SBS2() {

    }

    private int toPositionValueOnBoundary(final long rawPosition) {
        final int retVal = (int)( rawPosition >> 32 );
        assert(retVal >= 0);
        assert(retVal % 64 == 0); // check that we're on a boundary
        return retVal;
    }

    private long toPositionRaw(final int positionValue) {
        return positionValue << 32;
    }

    private boolean isDense(final long rawPosition) {
        return ( ((int)rawPosition) & 0x80000000 ) == 0x80000000;
    }

    private boolean isSparse(final long rawPosition) {
        return ! isDense(rawPosition);
    }

    private int toDenseDataIndex(final long rawPosition) {
        return ((int)rawPosition) & 0x7FFFFFFF;
    }

    protected long toSparseLength(final long rawPosition, final int length) {
        return (rawPosition & 0x80000000) & (length & 0x7FFFFFFF);
    }

    private int toSparsePositionLength(final long rawPosition) {
        return ((int)rawPosition) & 0x7FFFFFFF;
    }

    protected boolean lengthSignIndicatesSet(int length) {
        return length >= 0;
    }

    private long toDenseRangeRaw(final int position, final int denseDataIndex) {
        return ((long)(0x80000000 + denseDataIndex & 0x7FFFFFFF)) + position << 32;
    }

    /**
     *
     * @param position
     * @param minimumIndex the lowest array index to search; aids optimization
     * @return
     */
    private int nextOrExactRangeStartIndex(final int position, final int minimumIndex) {
        assert(minimumIndex >= 0);
        final long positionKey = toLongPositionKey(position);
        if (positionKey > ranges[ranges.length - 1]) return -ranges.length;
        int x = LongArrays.binarySearch(ranges, minimumIndex, ranges.length, positionKey);
        if (x < 0) x = -x + 1;
        return x;
    }

    private int previousOrExactRangeStartIndex(final int position, final int maximumIndex) {
        assert(maximumIndex <= ranges.length);
        final long positionKey = toLongPositionKey(position);
        if (positionKey < ranges[0]) return -1;
        int x = LongArrays.binarySearch(ranges, 0, maximumIndex, positionKey);
        if (x < 0) x = -x + 1;
        return x;
    }

    private void forEachSetPosition(
            final @Nonnegative BitSetConsumer consumer,
            final @Nonnegative int startPosition) {

        final long positionKey = toLongPositionKey(startPosition);
        if (positionKey > ranges[ranges.length - 1]) return;
        int x = LongArrays.binarySearch(ranges, positionKey);
        if (x < 0) x = -x + 1;
        for (; x < ranges.length; x++) {
            final long raw = ranges[x];
            if (isSparse(raw)) {
                // sparse handling
                final int _startPosition = toPositionValueOnBoundary(raw);
                final int _length = toSparsePositionLength(raw);
                if (lengthSignIndicatesSet(_length)) {
                    consumer.consumeRange(_startPosition, _length);
                } else {
                    // It's an unset range so don't consume it.
                }
            } else {
                // dense handling
                assert(isDense(raw));
                final int _startPosition = toPositionValueOnBoundary(raw);
                final int _denseDataIndex = toDenseDataIndex(raw);
                final HMNode node = map.getNode(_denseDataIndex);
                final int _nodeStartPosition = node.getStartPosition();
                final long[] nodeData = node.getData();
                final int nodeSize = node.getSize();

                nodeDataLoop: for (int i = 0; i < nodeData.length; i++) {
                    for (int j = 0; j < 64; j++) {
                        if (i + j >= nodeSize) break nodeDataLoop;
                        final long l = nodeData[i];
                        if (((l >> j) & 1) == 1) {
                            consumer.consume(_nodeStartPosition + j);
                        }
                    }
                }
            }
        }
    }

    public void forEachSetPosition(final @Nonnegative BitSetConsumer consumer) {
        forEachSetPosition(consumer, 0);
    }

    protected abstract boolean mustConvertOnSubRange(boolean rangeIsSet);

    /**
     *
     * @param target The array on which to perform operations.
     * @param bitsAreSet True means all bits are set.  False means all bits are clear.
     * @param positionsCount Number of positions.
     */
    protected abstract void rawLongApply(
            final long[] target,
            final boolean bitsAreSet,
            final int arrayStartIndex,
            final int positionsCount);

    protected abstract void rawLongApply(
            final long[] targetArray0,
            final long[] array1,
            final @Nonnegative int i0,
            final @Nonnegative int len,
            final @Nonnegative int i1);

    protected abstract void rawLongApply(
            final long[] target,
            final int arrayStartIndex,
            final int len);

    protected abstract long rawLongApply(
            final long l0,
            final long l1);

    private void sparseApply(
            final int index,
            final long raw,
            final long oRaw,
            final int startPosition,
            final int endPosition,
            final int absLength,
            final int rawLength) {

      //  if (lengthSignIndicatesSet(rawLength)) {
      //      // the section being considered is not set
      //      // flip the sign
      //      ranges[index] = toSparseLength(raw, -rawLength);
      //  }
        throw new IllegalStateException("not yet implemented");
    }

    private void denseApply(
            final @Nonnegative int rangeIndex,
            final long raw,
            final SBS2 o,
            final @Nonnull HMNode node,
            final @Nonnegative int startPosition,
            final @Nonnegative int endPosition) {

        assert(rangeIndex >= 0);
        assert(node != null);
        assert(startPosition >= 0);
        assert(endPosition >= 0);

        final int nodeStartPosition = node.getStartPosition();
        final int offsetInBitPosition = startPosition - nodeStartPosition;
        if (offsetInBitPosition < 0) throw new IllegalStateException();

        final long[] nodeData = node.getData();

        int insideLongStartInclusive =
                Math.max(0, 64 - offsetInBitPosition % 64);

        int y = endPosition - startPosition + offsetInBitPosition;
        int i = offsetInBitPosition / 64;

        if (endPosition - startPosition + offsetInBitPosition <= 64) {
            // beginning and end are now
            nodeData[i] = rawLongApply(
                    o,
                    nodeData[i],
                    insideLongStartInclusive,
                    64 - endPosition % 64);
        } else {
            // just beginning
            nodeData[i] = rawLongApply(
                    o,
                    nodeData[i],
                    insideLongStartInclusive,
                    64);
            i++;

            int z = (endPosition - startPosition + offsetInBitPosition) / 64;

            int zMinus1 = z - 1;
            if (i < zMinus1) {
                // continue to fill
                rawLongApply(o, nodeData, i, zMinus1, startPosition + i * 64, startPosition + zMinus1 * 64);
            }

            // handle the end
            nodeData[z] = rawLongApply(
                    o,
                    nodeData[z],
                    0,
                    64 - endPosition % 64);
        }

    }

    /**
     * To be called by another object, and any changes will take place
     * only on the passed in parameter nodeData.
     *
     * Will keep processing until either reaching endPosition or encountering
     * a condition which cannot be processed further.  Always returns the number
     * of positions processed.
     *
     * @param target The node data which to be modified.
     * @param oArrayIndexBegin The array index inclusive to begin operating from.
     * @param oArrayIndexEnd The array index exclusive at whih to stop.
     * @param startPosition Indicates the start position inclusive corresponding to the arrayIndexBegin.
     * @param endPosition Indicates the end position exclusive corresponding to the arrayIndexEnd.
     * @return The number of positions processed.  May be zero.
     */
    private int applyOn(
            final long[] target,
            final int oArrayIndexBegin,
            final int oArrayIndexEnd,
            final int startPosition,
            final int endPosition) {

        assert(target != null);
        assert(oArrayIndexBegin >= 0);
        assert(oArrayIndexEnd >= 0);
        assert(oArrayIndexEnd > oArrayIndexBegin);
        assert(startPosition >= 0);
        assert(startPosition % 64 == 0);
        assert(endPosition >= 0);
        assert(endPosition % 64 == 0);

        final int x = Arrays.binarySearch(ranges, toPositionRaw(startPosition));
        int rangeIndex = x;
        if (x < 0) rangeIndex = -x - 1;
        final long raw = ranges[rangeIndex];

        final int len;

        if (isSparse(raw)) {
            final int localStartPosition = toPositionValueOnBoundary(raw);
            //final int positionOffset = localStartPosition - startPosition;          assert(positionOffset >= 0); assert(positionOffset % 64 == 0);
            final int rawLen = toSparsePositionLength(raw);
            final int absRawLen = Math.abs(rawLen);
            final int localEndPosition = localStartPosition + absRawLen;            assert(localEndPosition >= 0); assert( localEndPosition % 64 == 0);
            final int innerEndPosition = Math.min(localEndPosition, endPosition);   assert(innerEndPosition >= 0); assert( innerEndPosition % 64 == 0);
            len = Math.min(absRawLen, innerEndPosition - startPosition) / 64;       assert(len >= 0); assert(len % 64 == 0);
            rawLongApply(target, lengthSignIndicatesSet(rawLen), oArrayIndexBegin, len);
        } else {
            assert(isDense(raw));
            final int nodeIndex = toDenseDataIndex(raw);                            assert(nodeIndex >= 0);
            final HMNode node = map.getNode(nodeIndex);                             assert(node != null);
            final int localStartPosition = node.getStartPosition();                 assert(localStartPosition % 64 == 0);
            final int localEndPosition = node.getEndPosition();                     assert(localEndPosition % 64 == 0);
            final long[] nodeData = node.getData();                                 assert(nodeData != null);
            final int positionOffset = localStartPosition - startPosition;          assert(positionOffset >= 0); assert(positionOffset % 64 == 0);
            final int arrayIndexBegin = positionOffset / 64 + oArrayIndexBegin;
            len = Math.min(
                    oArrayIndexEnd - oArrayIndexBegin,
                    node.getSize() - arrayIndexBegin);                              assert(len >= 0); assert(len % 64 == 0);
            rawLongApply(target, nodeData, oArrayIndexBegin, len, arrayIndexBegin);
        }

        assert(Math.multiplyExact(len,64) >= 0);
        return len * 64;
    }

    /**
     *
     * @param o
     * @param nodeData
     * @param arrayIndexBegin
     * @param arrayIndexEnd
     * @param startPosition
     * @param endPosition
     * @return positions processed
     */
    protected int rawLongApply(
            SBS2 o,
            long[] nodeData,
            int arrayIndexBegin,
            int arrayIndexEnd,
            int startPosition,
            int endPosition) {

        if (o == null) {
            rawLongApply(nodeData, arrayIndexBegin, arrayIndexEnd);
            final int retVal = ( arrayIndexEnd - arrayIndexBegin ) * 64;
            assert(retVal >= 0);
            assert(retVal % 64 == 0);
            return retVal;
        }

        return o.applyOn(nodeData, arrayIndexBegin, arrayIndexEnd, startPosition, endPosition);
    }

    protected void process(final int startPositionInclusive, final int endPositionExclusive, SBS2 o) {
        final long positionKey = toLongPositionKey(startPositionInclusive);
        if (positionKey > ranges[ranges.length - 1]) {
            // TODO: expand
            throw new IllegalStateException("not yet implemented");
        }

        int b = startPositionInclusive;

        // x: index of ranges
        int x = LongArrays.binarySearch(ranges, positionKey);
        if (x < 0) x = -x + 1;

        loop1: while (b < endPositionExclusive && x < ranges.length) {

            long raw = ranges[x];
            final int inLocalRangeStartPosition = toPositionValueOnBoundary(raw);

            if (isSparse(raw)) {
                // sparse handling
                final int _length = toSparsePositionLength(raw);
                final int _absLength = Math.abs(_length);

                if (inLocalRangeStartPosition < b || inLocalRangeStartPosition + _absLength > b) {
                    // determine if conversion is required
                    final boolean rangeIsSet = lengthSignIndicatesSet(_length);
                    if (mustConvertOnSubRange(rangeIsSet)) {
                        createDenseRange(x, inLocalRangeStartPosition, inLocalRangeStartPosition + _absLength, rangeIsSet);
                        continue loop1;
                    }
                } else {
                    assert (b <= inLocalRangeStartPosition);
                    assert (b >= inLocalRangeStartPosition + _absLength);
                    // conversion not required
                    final long oRaw = o.getByPosition(inLocalRangeStartPosition);
                    sparseApply(x, raw, oRaw, inLocalRangeStartPosition, inLocalRangeStartPosition + _absLength, _absLength, _length);
                    b += _absLength;
                }
            } else {
                // dense handling
                assert (isDense(raw));
                final int _denseDataIndex = toDenseDataIndex(raw);
                final HMNode node = map.getNode(_denseDataIndex);
                final int localStartPositionInclusive = b;
                final int localEndPositionExclusive =
                        Math.min(endPositionExclusive, node.getEndPosition());

                final long oRaw = o.getByPosition(inLocalRangeStartPosition);
                denseApply(x, raw, oRaw, node, localStartPositionInclusive, localEndPositionExclusive);

                b = localEndPositionExclusive;
            }
            x++;
        }
    }

    private void createDenseRange(final int rangeIndex, final int startPosition, final int endPosition, final boolean rangeIsSet) {
        final HMNode node = new HMNode(startPosition, endPosition, rangeIsSet);
        final int nodesIndex = map.add(node);
        final long range = toDenseRangeRaw(startPosition, nodesIndex);
        ranges[rangeIndex] = range;
    }

    public void clear(final int startPositionInclusive, final int endPositionExclusive) {

    }

    class AndBitSetConsumer extends BitSetConsumer {
        @Override
        public void consume(@Nonnegative int position) {

        }

        @Override
        public void consumeRange(@Nonnegative int startPositionInclusive, @Nonnegative int endPositionExclusive) {

        }
    }

    public static void and(
            final @Nonnull SBS2 a,
            final @Nonnull SBS2 b) {


    }

    private int nextOrExactRangeStartIndex(final int position) {
        return nextOrExactRangeStartIndex(position, ranges.length);
    }


    private int previousOrExactRangeStartIndex(final int position) {
        return previousOrExactRangeStartIndex(position, 0);
    }

    public static abstract class BitSetConsumer {
        private int minSparseIndex = -1;
        private int maxSparseIndex = -1;
        private int minDenseIndex = -1;
        private int maxDenseIndex = -1;

        public int getMinSparseIndex() {
            return minSparseIndex;
        }

        public void setMinSparseIndex(int minSparseIndex) {
            this.minSparseIndex = minSparseIndex;
        }

        public int getMaxSparseIndex() {
            return maxSparseIndex;
        }

        public void setMaxSparseIndex(int maxSparseIndex) {
            this.maxSparseIndex = maxSparseIndex;
        }

        public int getMinDenseIndex() {
            return minDenseIndex;
        }

        public void setMinDenseIndex(int minDenseIndex) {
            this.minDenseIndex = minDenseIndex;
        }

        public int getMaxDenseIndex() {
            return maxDenseIndex;
        }

        public void setMaxDenseIndex(int maxDenseIndex) {
            this.maxDenseIndex = maxDenseIndex;
        }

        public abstract void consume(final @Nonnegative int position);

        public abstract void consumeRange(final @Nonnegative int startPositionInclusive, final @Nonnegative int endPositionExclusive);
    }

    private int nextSparseRangeStartPoint(final int position, final int minimumIndex) {
        int x = LongArrays.binarySearch(sparseRanges, minimumIndex, sparseRanges.length, toLongPositionKey(position));
        if (x < 0) x = -x + 1;
        return x;
    }

    private int nextSparseRangeStartPoint(final int position) {
        return nextSparseRangeStartPoint(position, 0);
    }

    private int nextDenseRangeStartPoint(final int position, final int minimumIndex) {
        int x = IntArrays.binarySearch(map.denseRanges, minimumIndex, map.denseRanges.length, position);
        if (x < 0) x = -x + 1;
        return x;
    }

    private int nextDenseRangeStartPoint(final int position) {
        return nextDenseRangeStartPoint(position, 0);
    }

    private long toLongPositionKey(final int position) {
        return position << 32;
    }

    /**
     * @param position
     * @return true if set, false if cannot set
     */
    private boolean sparseSet(final int position) {
        int index = LongArrays.binarySearch(sparseRanges, toLongPositionKey(position));
        int len = (int) sparseRanges[index];
        return !(len < 0);
    }

    /**
     * @param position
     * @return true if unset, false if cannot unset
     */
    private boolean sparseUnset(final int position) {
        int index = LongArrays.binarySearch(sparseRanges, toLongPositionKey(position));
        int len = (int) sparseRanges[index];
        return len < 0;
    }

    /**
     *
     * @param position
     * @return true if set, false if cannot set
     */
    private boolean denseSet(final int position) {
        final HMNode node = map.findNode(position);
        if (node == null) return false;
        final long x = position - node.getStartPosition();
        final int a = (int)(x) / 64;
        final int b = 64 - (int)(x) % 64;
        map.denseRanges[a] |= 1 << b;
        return true;
    }

    /**
     * @param position
     * @return true if unset, false if cannot unset
     */
    private boolean denseUnset(final int position) {
        final HMNode node = map.findNode(position);
        if (node == null) return false;
        final long x = position - node.getStartPosition();
        final int a = (int)(x) / 64;
        final int b = 64 - (int)(x) % 64;
        map.denseRanges[a] &= 0 << b;
        return true;
    }



    public void and(final SBS2 a, final SBS2 b, final int startIndex, final int len) {
        int aRangeIndex = 0;
        long bLastValue = 0;
        while (aRangeIndex < a.ranges.length) {
            final int rangeStartValue0 = (int) (ranges[aRangeIndex] >> 32);
            final int rangeStartValue1 = map
            final int rangeStartValue = Math.min(rangeStartValue0, rangeStartValue1);


            final int rangeLen = (int) ranges[aRangeIndex];
            final int rangeEndValue = rangeStartValue + Math.abs(rangeLen);

            final
            while () {

            }
        }
    }

}
