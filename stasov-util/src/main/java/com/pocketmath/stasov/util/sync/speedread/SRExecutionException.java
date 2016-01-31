package com.pocketmath.stasov.util.sync.speedread;

/**
 * Created by etucker on 1/31/16.
 */
public class SRExecutionException extends Exception {
    SRExecutionException() {
    }

    SRExecutionException(String message) {
        super(message);
    }

    SRExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    SRExecutionException(Throwable cause) {
        super(cause);
    }

    SRExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
