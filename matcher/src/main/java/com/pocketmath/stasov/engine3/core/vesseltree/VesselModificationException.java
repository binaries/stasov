package com.pocketmath.stasov.engine3.core.vesseltree;

/**
 * Created by etucker on 8/23/16.
 */
public class VesselModificationException extends VesselException {
    VesselModificationException() {
    }

    VesselModificationException(String message) {
        super(message);
    }

    VesselModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    VesselModificationException(Throwable cause) {
        super(cause);
    }

    VesselModificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
