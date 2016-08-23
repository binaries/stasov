package com.pocketmath.stasov.engine3.core.vesseltree;

/**
 * Created by etucker on 8/23/16.
 */
public class VesselException extends Exception {
    VesselException() {
    }

    VesselException(String message) {
        super(message);
    }

    VesselException(String message, Throwable cause) {
        super(message, cause);
    }

    VesselException(Throwable cause) {
        super(cause);
    }

    VesselException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
