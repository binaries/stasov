package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 3/29/15.
 */
public class DuplicateAttributeException extends Exception {

    public DuplicateAttributeException() {
    }

    public DuplicateAttributeException(String message) {
        super(message);
    }

    public DuplicateAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateAttributeException(Throwable cause) {
        super(cause);
    }

    public DuplicateAttributeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
