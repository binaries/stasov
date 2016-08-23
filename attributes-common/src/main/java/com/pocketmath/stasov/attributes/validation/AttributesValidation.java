package com.pocketmath.stasov.attributes.validation;

import com.pocketmath.stasov.util.validate.ValidationException;

/**
 * Created by etucker on 8/23/16.
 */
public class AttributesValidation {

    public static boolean validateAttributeId(final long id) throws ValidationException {
        if (id < 0)
            throw new ValidationException("id was less than 0.  id: " + id);
        return true;
    }

    public static boolean validateAttributeValidId(long valueId) throws ValidationException {
        return true;
    }
}
