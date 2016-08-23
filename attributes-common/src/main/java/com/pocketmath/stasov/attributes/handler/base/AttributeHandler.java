package com.pocketmath.stasov.attributes.handler.base;

import com.pocketmath.stasov.attributes.Order;
import com.pocketmath.stasov.scoring.ScoredResultWithMeta;
import com.pocketmath.stasov.scoring.ScoredResultWithMetaConsumer;
import com.pocketmath.stasov.util.validate.ValidationException;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 3/13/15.
 */
public abstract class AttributeHandler<IdType extends Serializable & Comparable<IdType>, EntryPointType, ValueType> {

    /**
     * A placeholder or no-op value.
     */
    public static final long NULL = 0;

    /**
     * Nothing found.
     */
    public static final long NOT_FOUND = -1;

    /**
     * Not found in cache: try next layer.
     */
    public static final long CACHED_NOT_FOUND = -2;

    /**
     * Query a custom index.
     */
    public static final long CUSTOM_INDEX = -3;

    /**
     * One dimensional range (line).
     */
    public static final long RANGE_1D = -4;

    protected static final float DEFAULT_NOT_MATCH_SCORE = 0f;
    protected static final float DEFAULT_MATCH_SCORE = 1f;

    /**
     * Returns a unique identifier representing the input value.  The use of long integers
     * is intended to provide better performance, and long integers are used internally
     * for matching when available.
     *
     * @param input
     * @return
     */
    public abstract long find(@Nonnull final String input);

    /**
     * To support CustomIndexAttributeHandler
     *
     * @param input
     * @param resultsConsumer
     */
    public void customIndexQueryInclusionary(
            @Nonnull final String input,
            @Nonnull final ScoredResultWithMetaConsumer resultsConsumer,
            @Nonnull final EntryPointType entry) {

        throw new UnsupportedOperationException();
    }

    /**
     * To support CustomIndexAttributeHandler
     *
     * @param input
     * @param resultsConsumer
     * @param entry
     */
    public void customIndexQueryExclusionary(
            @Nonnull final String input,
            @Nonnull final ScoredResultWithMetaConsumer resultsConsumer,
            @Nonnull final EntryPointType entry) {

        throw new UnsupportedOperationException();
    }

    /**
     * Verify that the input is valid.  This is a best effort method that may not always be implemented.
     *
     * Throwing an exception as opposed to relying on a return value permits more granular
     * feedback when validation fails.
     *
     * @param input
     * @throws ValidationException If the input is invalid.
     */
    public void tryValidate(@Nonnull final String input) throws ValidationException {
        // by default do nothing
    }

    /**
     * Overriding methods should call super.
     *
     * @param id
     * @param input
     * @param entry
     * @param exit
     */
    public void afterPrimaryIndex(
            @Nonnull final IdType id,
            @Nonnull final String input,
            @Nullable final EntryPointType entry,
            @Nullable final ValueType exit) {

        // tryValidate
        Objects.requireNonNull(id);
        Objects.requireNonNull(input);
        Objects.requireNonNull(entry);
        Objects.requireNonNull(exit);

        // by default do nothing else
    }

    /**
     * Overriding methods should call super.
     *
     * @param id
     */
    public void beforePrimaryRemove(
            @Nonnull final IdType id) {

        // tryValidate
        Objects.requireNonNull(id);

        // by default do nothing else
    }


    /**
     * To facilitate testing.
     *
     * @return
     * @param order
     */
    public Iterable<String> sampleValues(@Nonnull final Order order) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    private Logger logger = Logger.getLogger(getClass().getName());
    {
        // shameless hard coded logging setup

        final Level level = Level.FINEST;

        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(level);

        logger.setLevel(level);
        logger.addHandler(consoleHandler);
    }

    public Logger getIndexingLogger() {
        return logger;
    }

}
