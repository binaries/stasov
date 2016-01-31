package com.pocketmath.stasov.util.sync.speedread;

/**
 * Created by etucker on 1/31/16.
 */
public class InCallException extends SRExecutionException {
    InCallException() {
    }

    InCallException(String message) {
        super(message);
    }

    InCallException(String message, Throwable cause) {
        super(message, cause);
    }

    InCallException(Throwable cause) {
        super(cause);
    }

    InCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
