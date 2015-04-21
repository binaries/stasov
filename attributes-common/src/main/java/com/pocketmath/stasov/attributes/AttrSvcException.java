package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 3/28/15.
 */
public class AttrSvcException extends Exception {
    AttrSvcException() {
        super();
    }

    AttrSvcException(String message) {
        super(message);
    }

    AttrSvcException(String message, Throwable cause) {
        super(message, cause);
    }

    AttrSvcException(Throwable cause) {
        super(cause);
    }

    AttrSvcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
