package com.pocketmath.stasov.pmtl.dnfconv;

/**
 * Created by etucker on 6/28/15.
 */
public class TreeException extends Exception {
    TreeException() {
        super();
    }

    TreeException(Throwable cause) {
        super(cause);
    }

    TreeException(String message) {
        super(message);
    }

    TreeException(String message, Throwable cause) {
        super(message, cause);
    }

    TreeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
