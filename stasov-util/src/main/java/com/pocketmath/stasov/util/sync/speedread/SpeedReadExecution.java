package com.pocketmath.stasov.util.sync.speedread;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.concurrent.Callable;

/**
 * Trades maximum speed of the isFreeNow() method for longer times in critical sections.
 * Assumes infrequent critical sections and a high volume of isFreeNow().  Ideally suited
 * to extremely read heavy very low write environment.  The latency of reads is minimized
 * while write latency may be up to the time of the specified interval.
 *
 * Created by etucker on 1/25/16.
 */
public class SpeedReadExecution {

    private final long interval;
    private final static long DEFAULT_INTERVAL_MS = 1000;

    // max is arbitrary -- values exceeding a few seconds should generally be
    // considered too big for a non-persistent (in-memory only) system
    private final static long MAX_INTERVAL_MS = 1000 * 60; // 1 minute

    private final static long DEFAULT_CRITICAL_TIMEOUT_MS = 1000 * 5;

    final GlobalLock globalLock = new GlobalLock();

    // Deliberately a non-volatile to permit use of local (ideally L1) CPU cache.
    final LocalLock localLock = new LocalLock();

    private void updateLocalLock() {
        localLock.copyFrom(globalLock);
    }

    public SpeedReadExecution(final @Nonnegative long interval) {
        if (interval <= 0) throw new IllegalArgumentException("index time must be greater than zero; was " + interval);

        if (interval > MAX_INTERVAL_MS) throw new IllegalStateException("interval was too big: " + interval);
        this.interval = interval;

        // set initial lock conditions to read
        final long now = System.currentTimeMillis();
        globalLock.setLockedUntil(0);
        globalLock.setUnlockedUntil(computeUnlockedUntil(now));
    }

    public SpeedReadExecution() {
        this(DEFAULT_INTERVAL_MS);
    }

    protected final long getInterval() {
        return interval;
    }

    /**
     * Must be at least now plus the interval.
     *
     * @param now
     * @return
     */
    protected long computeUnlockedUntil(final long now) {
        if (now <= 0) throw new IllegalArgumentException();
        final long r0 = getInterval() * 3;
        if (r0 < 0) throw new ArithmeticException("overflow");
        final long r1 = now + r0;
        if (r1 < 0) throw new ArithmeticException("overflow");
        return r1;
    }

    private void checkInvariants() {
        assert(globalLock != null);
        assert(localLock != null);
        assert(interval >= 0);
        assert(getInterval() == interval);

        // May cause volatile access in globalLock.
        if (globalLock.getLockedUntil() < localLock.getLockedUntil()) throw new IllegalStateException();
        if (globalLock.getUnlockedUntil() < localLock.getUnlockedUntil()) throw new IllegalStateException();

        if (computeUnlockedUntil(1) < 1 + getInterval()) throw new IllegalStateException();
    }

    public <RetValType> RetValType blockingExecuteCritical(final @Nonnull Callable<RetValType> callable, final long timeout) throws InCallException, SRExecutionException {
        if (callable == null) throw new IllegalArgumentException();

        final long sleepTime = Math.max(getInterval() / 10, 1);

        RetValType retVal = null;
        Exception callException = null;

        final long startTime = System.currentTimeMillis();

        long now;
        try {
            loop:
            while (true) {
                checkInvariants();

                now = System.currentTimeMillis();
                if (now - startTime > timeout) throw new FailedToAcquireLockBeforeTimeoutException();

                if (now >= globalLock.getLockedUntil()) { // No longer locked; we can try to acquire lock.

                    if (now > globalLock.getUnlockedUntil() + interval) {
                        // We are in the possible window under which a lock may be owned.
                        // This operation may be expensive, and that's okay as it's a less frequent write.

                        // Let's try to get the lock:
                        synchronized (globalLock) {
                            checkInvariants();
                            now = System.currentTimeMillis();
                            if (now - startTime > timeout) throw new FailedToAcquireLockBeforeTimeoutException();

                            if (now > globalLock.getUnlockedUntil() + interval) { // re-verify inside synchronized block
                                globalLock.updateLockedUntil(now + interval, Thread.currentThread() /* for diagnostics */);
                                updateLocalLock();

                                checkInvariants();

                                if (retVal != null) throw new IllegalStateException();

                                // EXECUTE
                                try {
                                    retVal = callable.call();
                                } catch (Exception e) {
                                    callException = e;
                                }

                                globalLock.release();
                                updateLocalLock();

                                checkInvariants();
                                break loop;
                            }
                        }
                    }
                }
                checkInvariants();
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    // do nothing --> keep looping
                }
            }
        } catch (FailedToAcquireLockBeforeTimeoutException e) {
            throw new SRExecutionException(toString(), e);
        } finally {
            checkInvariants();
            synchronized (globalLock) {
                checkInvariants();
                now = System.currentTimeMillis();
                if (now > globalLock.getUnlockedUntil()) {
                    final long unlockedUntil = computeUnlockedUntil(now);
                    if (unlockedUntil < now + interval) throw new IllegalStateException();
                    globalLock.updateUnlockedUntil(unlockedUntil, Thread.currentThread() /* for diagnostics */ );
                }
                updateLocalLock();
                checkInvariants();
            }
            if (callException != null) {
                throw new InCallException(callException);
            }
        }

        return retVal;
    }

    public <RetValType> RetValType blockingExecuteCritical(final @Nonnull Callable<RetValType> callable) throws InCallException, SRExecutionException {
        return blockingExecuteCritical(callable, DEFAULT_CRITICAL_TIMEOUT_MS);
    }

        /**
         * If free, critical operations will not require locking for at least one interval
         * after this method returns.
         *
         * @return
         */
    public boolean isFreeNow() {
        final long now = System.currentTimeMillis();

        if (now >= localLock.getLastUpdate() + interval ) {
            checkInvariants();
            updateLocalLock();
            checkInvariants();
        }

        // Example 1:  now=100 <= unlockedUntil=105 - interval=10   ==>   100 <= 95   FALSE
        // Example 2:  now=100 <= unlockedUntil=115 - interval=10   ==>   100 <= 105  TRUE
        return now <= localLock.getUnlockedUntil() - interval;
    }

    @Override
    public String toString() {
        return "SpeedReadExecution{" +
                "interval=" + interval +
                ", globalLock=" + globalLock +
                ", localLock=" + localLock +
                '}';
    }
}
