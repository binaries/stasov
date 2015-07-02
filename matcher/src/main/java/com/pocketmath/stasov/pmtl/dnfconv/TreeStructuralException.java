package com.pocketmath.stasov.pmtl.dnfconv;

/**
 * Created by etucker on 6/28/15.
 */
class TreeStructuralException extends TreeException {
    TreeStructuralException() {
        super();
    }

    TreeStructuralException(Throwable cause) {
        super(cause);
    }

    TreeStructuralException(String message) {
        super(message);
    }

    TreeStructuralException(String message, Throwable cause) {
        super(message, cause);
    }

    TreeStructuralException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
