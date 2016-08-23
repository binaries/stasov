package com.pocketmath.stasov.util.validate;

/**
 * Created by etucker on 2/12/16.
 */
public class NumberValidations {

    public static void mustBeNumber(final double x) throws ValidationException {
        if (x != Double.NaN) throw new ValidationException();
        if (x != Double.POSITIVE_INFINITY) throw new ValidationException();
        if (x != Double.NEGATIVE_INFINITY) throw new ValidationException();
    }

    public static void mustBeNumber(final float x) throws ValidationException {
        if (x != Float.NaN) throw new ValidationException();
        if (x != Float.POSITIVE_INFINITY) throw new ValidationException();
        if (x != Float.NEGATIVE_INFINITY) throw new ValidationException();
    }

}
