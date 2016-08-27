package com.pocketmath.stasov.util.pool;

import com.pocketmath.stasov.util.log.TimeRateLimitedLogger;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 8/25/16.
 */
public abstract class AbstractPool<T> {

    private int capacity;

    private final Queue<T> queue;

    private TimeRateLimitedLogger newInstanceRateLimitedLogger;
    private final Level newInstanceLogLevel;

    public AbstractPool(final int capacity, final int initialSize, final boolean prepopulate, final Logger newInstanceLogger, final Level newInstanceLogLevel) {
        if (capacity <= 0) throw new IllegalArgumentException();
        if (initialSize < 0) throw new IllegalArgumentException();
        if (initialSize > capacity) throw new IllegalArgumentException();
        if (newInstanceLogger != null ^ newInstanceLogLevel == null) throw new IllegalArgumentException();
        this.queue = new ArrayDeque<T>(initialSize);
        this.newInstanceRateLimitedLogger = new TimeRateLimitedLogger(newInstanceLogger, 60*1000, null);
        this.newInstanceLogLevel = newInstanceLogLevel;
        if (prepopulate) {
            for (int i = 0; i < initialSize; i++) {
                final T o = newInstance();
                queue.offer(o);
            }
            if (queue.size() != initialSize)
                throw new IllegalStateException();
        }
    }

    public AbstractPool(final int capacity, final int initialSize) {
        this(capacity, initialSize, true, null, null);
    }

    public AbstractPool(final int capacity) {
        this(capacity, 1, true, null, null);
    }

    /**
     *
     * @param o
     * @return true if reclaimed or false if discarded
     */
    public boolean reclaim(final T o) {
        Objects.requireNonNull(o);
        if (queue.size() > capacity)
            return false;
        queue.offer(o);
        return true;
    }

    public T getInstance() {
        T o = queue.poll();
        if (o != null)
            return o;
        onNewInstance();
        o = newInstance();
        Objects.requireNonNull(o);
        return o;
    }

    protected abstract T newInstance();

    public static int NEW_INSTANCE_EVENT_ID = 1;

    /**
     * Subclasses may override this method to receive events.  Subclasses should call super.
     */
    protected void onNewInstance() {
        if (newInstanceRateLimitedLogger != null) {
            assert newInstanceLogLevel != null;
            newInstanceRateLimitedLogger.tryLog(
                    NEW_INSTANCE_EVENT_ID,
                    newInstanceLogLevel,
                    "New instance created.  Seeing this too much after startup could mean you're running out of objects.  Try increasing the pool capacity.",
                    null);
        }
    }

}
