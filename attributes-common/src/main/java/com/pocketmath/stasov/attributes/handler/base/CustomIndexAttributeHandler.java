package com.pocketmath.stasov.attributes.handler.base;

import com.pocketmath.stasov.scoring.ScoredResultWithMetaConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Created by etucker on 2/2/16.
 */
public abstract class CustomIndexAttributeHandler<IdType extends Serializable & Comparable<IdType>, EntryPointType, ValueType extends Serializable & Comparable<ValueType>>
        extends AttributeHandler<IdType,EntryPointType,ValueType> {

    @Override
    public final long find(@Nullable final String input) {
        return CUSTOM_INDEX;
    }

    @Override
    public abstract void afterPrimaryIndex(
            @Nonnull IdType id,
            @Nonnull String input,
            @Nullable EntryPointType entryPoint,
            @Nullable ValueType exit);

    @Override
    public abstract void beforePrimaryRemove(@Nonnull IdType id);

    @Override
    public abstract void customIndexQueryInclusionary(
            @Nonnull final String input,
            @Nonnull final ScoredResultWithMetaConsumer resultsConsumer,
            @Nonnull final EntryPointType entryPoint);

    @Override
    public abstract void customIndexQueryExclusionary(
            @Nonnull final String input,
            @Nonnull final ScoredResultWithMetaConsumer resultsConsumer,
            @Nonnull final EntryPointType entryPoint);

}
