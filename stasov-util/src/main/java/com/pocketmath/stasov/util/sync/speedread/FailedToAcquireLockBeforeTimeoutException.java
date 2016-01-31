package com.pocketmath.stasov.util.sync.speedread;

import java.util.concurrent.ExecutionException;

/**
 * Created by etucker on 1/31/16.
 */
public class FailedToAcquireLockBeforeTimeoutException extends SRExecutionException {
    public FailedToAcquireLockBeforeTimeoutException() {
    }

    public FailedToAcquireLockBeforeTimeoutException(String message) {
        super(message);
    }

    public FailedToAcquireLockBeforeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToAcquireLockBeforeTimeoutException(Throwable cause) {
        super(cause);
    }

    public FailedToAcquireLockBeforeTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
