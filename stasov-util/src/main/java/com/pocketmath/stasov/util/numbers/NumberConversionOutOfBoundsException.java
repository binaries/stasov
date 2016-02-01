package com.pocketmath.stasov.util.numbers;

/**
 * Created by etucker on 2/1/16.
 */
public class NumberConversionOutOfBoundsException extends NumberRuntimeException {
    public NumberConversionOutOfBoundsException() {
    }

    public NumberConversionOutOfBoundsException(String message) {
        super(message);
    }

    public NumberConversionOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NumberConversionOutOfBoundsException(Throwable cause) {
        super(cause);
    }

    public NumberConversionOutOfBoundsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
