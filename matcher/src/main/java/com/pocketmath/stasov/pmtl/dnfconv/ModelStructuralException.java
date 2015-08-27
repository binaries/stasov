package com.pocketmath.stasov.pmtl.dnfconv;

/**
 * Created by etucker on 8/25/15.
 */
public class ModelStructuralException extends TreeStructuralException {

    public ModelStructuralException() {
    }

    public ModelStructuralException(Throwable cause) {
        super(cause);
    }

    public ModelStructuralException(String message) {
        super(message);
    }

    public ModelStructuralException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelStructuralException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ModelStructuralException(DNFConvModels.Node node, String message) {
        super("exception on node=" + node + "; message=" + message);
    }
}
