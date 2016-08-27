package com.pocketmath.stasov.util.log;

import it.unimi.dsi.fastutil.ints.*;

import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A wrapper to manage high volume log messages by ignoring repeated logs about the same event.
 *
 * Events refer to something that happens causing a log event.  Events can occur multiple times.  The event id should
 * generally be unique to a place in the code or sort of happening, but all instances of a specific event (or "type of
 * event") should receive the same event id.
 *
 * Created by etucker on 8/26/16.
 */
public class TimeRateLimitedLogger {

    private static class LogEntry {

        private final long timestamp;
        private final int eventId;
        private final StackTraceElement callerStackTrace;
        private final Level logLevel;
        private final String message;
        private final Object[] parameters;

        /**
         *
         * @param timestamp Time at which logging occurred
         * @param eventId Event id
         * @param callerStackTrace Stack trace of the caller to be recorded in logging
         * @param logLevel Log level for logger
         * @param message Message for logger
         * @param parameters Parameters to logger message
         */
        LogEntry(final long timestamp, final int eventId, final StackTraceElement callerStackTrace, final Level logLevel, final String message, final Object[] parameters) {
            if (timestamp < 0) throw new IllegalArgumentException();
            if (eventId < 0) throw new IllegalArgumentException();
            Objects.requireNonNull(callerStackTrace);
            if (message == null && (parameters != null || parameters.length > 0)) throw new IllegalArgumentException();
            this.timestamp = timestamp;
            this.eventId = eventId;
            this.callerStackTrace = callerStackTrace;
            this.logLevel = logLevel;
            this.message = message;
            this.parameters = parameters;
        }
    }

    private static long PURGE_CHECK_CYCLE_FREQUENCY_MS = 300*1000;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private Logger logger;

    private Int2ObjectMap<LogEntry> latestEventIdsToLogEntries = new Int2ObjectLinkedOpenHashMap<>();

    private Int2LongMap lastLogTime;
    {
        lastLogTime = new Int2LongLinkedOpenHashMap();
        lastLogTime.defaultReturnValue(-1L);
    }

    private long logFrequencyMS;

    private final String messagePrefix;

    public TimeRateLimitedLogger(final Logger logger, final long logFrequencyMS, final String messagePrefix) {
        Objects.requireNonNull(logger);
        if (logFrequencyMS < 0) throw new IllegalArgumentException();
        if (messagePrefix != null && messagePrefix.length() > 256) throw new IllegalArgumentException("message prefix was too long (over 256 characters); length: " + messagePrefix.length());
        this.logger = logger;
        this.logFrequencyMS = logFrequencyMS;
        this.messagePrefix = messagePrefix == null? "" : messagePrefix;

        executorService.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        tryPurgeAll();
                    }
                },
                logFrequencyMS,
                PURGE_CHECK_CYCLE_FREQUENCY_MS,
                TimeUnit.MILLISECONDS);
    }

    protected void finalize() {
        executorService.shutdown();
    }

    /**
     *
     * @return number of objects removed
     */
    int tryPurgeAll() {
        IntArrayList keysToRemove = null;
        final long timeNow = System.currentTimeMillis();
        final long deadlineAfter = Math.addExact(timeNow, logFrequencyMS);
        for (final Int2ObjectMap.Entry<LogEntry> entry : latestEventIdsToLogEntries.int2ObjectEntrySet()) {
            final int key = entry.getIntKey();
            final LogEntry logEntry = entry.getValue();
            assert key == logEntry.eventId;
            if (logEntry.timestamp < deadlineAfter) {
                if (keysToRemove == null)
                    keysToRemove = new IntArrayList();
                assert !keysToRemove.contains(key);
                keysToRemove.add(key);
            }
        }

        if (keysToRemove == null) {
            return 0;
        } else {
            for (int keyToRemove : keysToRemove) {
                assert latestEventIdsToLogEntries.containsKey(keyToRemove);
                final LogEntry removedValue = latestEventIdsToLogEntries.remove(keyToRemove);
                if (removedValue == null)
                    throw new IllegalStateException();
            }
            return keysToRemove.size();
        }
    }

    /**
     *
     * @param eventId
     * @return true if object was removed else false
     */
    boolean tryPurge(final int eventId) {
        if (eventId < 0) throw new IllegalArgumentException();
        final LogEntry logEntry = latestEventIdsToLogEntries.get(eventId);
        if (logEntry == null)
            throw new IllegalStateException("eventId was not found -- possible bad argument?  eventId: " + eventId);

        final long timeNow = System.currentTimeMillis();
        final long deadlineAfter = Math.addExact(timeNow, logFrequencyMS);
        if (logEntry.timestamp < deadlineAfter) {
            latestEventIdsToLogEntries.remove(eventId);
            return true;
        } else {
            return false;
        }
    }

    private void doLog(final LogEntry le) {
        logger.logrb(
                le.logLevel,
                le.callerStackTrace.getClassName(),
                le.callerStackTrace.getMethodName(),
                (ResourceBundle)null,
                messagePrefix + "[" + le.eventId + "][" + le.timestamp + "ms] " + le.message,
                le.parameters);
    }

    /**
     *
     * @param eventId
     * @param logLevel
     * @param message
     * @param parameters
     * @return true if logged or false if ignored
     */
    public boolean tryLog(final int eventId, final Level logLevel, final String message, final Object... parameters) {
        final long timeNow = System.currentTimeMillis();
        final long oldestInBounds = Math.addExact(timeNow, logFrequencyMS);
        final long lastLogTimeMS = lastLogTime.get(eventId);

        long lastRecorded = lastLogTime.get(eventId);
        if (lastRecorded == lastLogTime.defaultReturnValue() || lastRecorded < oldestInBounds) {

            synchronized (this) {

                lastRecorded = lastLogTime.get(eventId);
                if (lastRecorded == lastLogTime.defaultReturnValue() || lastRecorded < oldestInBounds) {

                    final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                    final StackTraceElement caller = stackTrace[2];

                    final LogEntry le = new LogEntry(timeNow, eventId, caller, logLevel, message, parameters);
                    lastLogTime.put(eventId, timeNow);
                }
            }

            return true;

        } else {
            onRejected(eventId, logLevel, message, parameters);
            return false;
        }
    }

    /**
     * Subclasses may override for additional functionality.
     */
    protected void onRejected(final int eventId, final Level logLevel, final String message, final Object... parameters) {
        // do nothing
    }

}
