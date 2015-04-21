package com.pocketmath.stasov.engine;

/**
 * Created by etucker on 4/5/15.
 */
public class IndexingException extends Exception {

    public IndexingException() {
    }

    public IndexingException(Throwable cause) {
        super(cause);
    }

    public IndexingException(String message) {
        super(message);
    }

    public IndexingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IndexingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
