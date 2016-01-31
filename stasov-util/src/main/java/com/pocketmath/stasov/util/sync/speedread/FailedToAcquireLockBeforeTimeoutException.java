package com.pocketmath.stasov.util.sync.speedread;

import java.util.concurrent.ExecutionException;

/**
 * Created by etucker on 1/31/16.
 */
public class FailedToAcquireLockBeforeTimeoutException extends SRExecutionException {
    FailedToAcquireLockBeforeTimeoutException() {
    }

    FailedToAcquireLockBeforeTimeoutException(String message) {
        super(message);
    }

    FailedToAcquireLockBeforeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    FailedToAcquireLockBeforeTimeoutException(Throwable cause) {
        super(cause);
    }

    FailedToAcquireLockBeforeTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
