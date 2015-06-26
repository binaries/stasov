package com.pocketmath.stasov.pmtl;

/**
 * Created by etucker on 6/23/15.
 */
public class PocketTLException extends Exception {
    public PocketTLException() {
        super();
    }

    public PocketTLException(Throwable cause) {
        super(cause);
    }

    public PocketTLException(String message) {
        super(message);
    }

    public PocketTLException(String message, Throwable cause) {
        super(message, cause);
    }

    public PocketTLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
