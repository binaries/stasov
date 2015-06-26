package com.pocketmath.stasov.pmtl;

/**
 * Created by etucker on 6/23/15.
 */
public class PocketTLDataException extends PocketTLException {
    public PocketTLDataException() {
        super();
    }

    public PocketTLDataException(Throwable cause) {
        super(cause);
    }

    public PocketTLDataException(String message) {
        super(message);
    }

    public PocketTLDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public PocketTLDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
