package com.pocketmath.stasov.attributes.handler.base;

import com.pocketmath.stasov.scoring.ScoredResultWithMetaConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by etucker on 3/14/16.
 */
public abstract class MapMultiEntryPointCustomAttributeHandler<IdType extends Serializable & Comparable<IdType>, EntryPointType, ValueType extends Serializable & Comparable<ValueType>>
        extends CustomIndexAttributeHandler<IdType, EntryPointType, ValueType> {

    public static abstract class SegmentHandler<IdType extends Serializable & Comparable<IdType>, ValueType extends Serializable & Comparable<ValueType>> {

        public abstract void customIndexQueryInclusionary(@Nonnull String input, @Nonnull ScoredResultWithMetaConsumer resultsConsumer);

        public abstract void customIndexQueryExclusionary(@Nonnull String input, @Nonnull ScoredResultWithMetaConsumer resultsConsumer);

        public abstract void beforePrimaryRemove(@Nonnull IdType id);

        /**
         *
         * @param id
         * @param input
         * @param exit This value will be supplied in results.
         */
        public abstract void afterPrimaryIndex(@Nonnull IdType id, @Nonnull String input, @Nonnull ValueType exit);
    }

    private final Map<EntryPointType, SegmentHandler> map = null;

    protected abstract SegmentHandler newSegment(@Nonnull EntryPointType entryPoint);

    private SegmentHandler getOrCreateSegment(final @Nonnull EntryPointType entryPoint) {
        Objects.requireNonNull(entryPoint);
        SegmentHandler seg = map.get(entryPoint);
        if (seg == null) {
            seg = newSegment(entryPoint);
            map.put(entryPoint, seg);
        }
        Objects.requireNonNull(seg);
        return seg;
    }

    @Override
    public void customIndexQueryInclusionary(@Nonnull String input, @Nonnull ScoredResultWithMetaConsumer resultsConsumer, @Nonnull EntryPointType entry) {
        final SegmentHandler seg = map.get(entry);
        if (seg == null) return;
        seg.customIndexQueryInclusionary(input, resultsConsumer);
    }

    @Override
    public void customIndexQueryExclusionary(@Nonnull String input, @Nonnull ScoredResultWithMetaConsumer resultsConsumer, @Nonnull EntryPointType entry) {
        final SegmentHandler seg = map.get(entry);
        if (seg == null) return;
        seg.customIndexQueryExclusionary(input, resultsConsumer);
    }

    @Override
    public void beforePrimaryRemove(@Nonnull IdType id) {
        final List<SegmentHandler> removals = new LinkedList<>();
        for (final Map.Entry<EntryPointType,SegmentHandler> entry : map.entrySet()) {
            Objects.requireNonNull(entry);
            final EntryPointType entryPoint = entry.getKey();
            final SegmentHandler seg = entry.getValue();
            Objects.requireNonNull(entryPoint);
            Objects.requireNonNull(seg);
            seg.beforePrimaryRemove(id);
        }
    }

    @Override
    public void afterPrimaryIndex(@Nonnull IdType id, @Nonnull String input, @Nullable EntryPointType entry, @Nullable ValueType exit) {
        final SegmentHandler seg = getOrCreateSegment(entry);
        Objects.requireNonNull(seg);
        seg.afterPrimaryIndex(id, input, exit);
    }

}
