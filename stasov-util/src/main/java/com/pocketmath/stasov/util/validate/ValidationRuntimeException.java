package com.pocketmath.stasov.util.validate;

/**
 * Created by etucker on 2/6/16.
 */
public class ValidationRuntimeException extends RuntimeException {

    public ValidationRuntimeException(String message, ValidationException cause) {
        super(message, cause);
    }

    public ValidationRuntimeException(ValidationException cause) {
        super(cause);
    }

    public ValidationRuntimeException(String message, ValidationException cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
