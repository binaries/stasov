package com.pocketmath.stasov.util.numbers;

/**
 * Created by etucker on 2/1/16.
 */
public class NumberRuntimeException extends RuntimeException {

    public NumberRuntimeException() {
    }

    public NumberRuntimeException(String message) {
        super(message);
    }

    public NumberRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NumberRuntimeException(Throwable cause) {
        super(cause);
    }

    public NumberRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
