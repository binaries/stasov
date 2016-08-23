package com.pocketmath.stasov.attrs;

import com.pocketmath.stasov.attributes.handler.base.AttributeHandler;
import com.pocketmath.stasov.attributes.AttributeType;

/**
 * Created by etucker on 3/30/15.
 */
@AttributeType
public class DeviceId extends AttributeHandler {

    @Override
    public long find(String input) {
        if (input.equalsIgnoreCase("1")) {
            return 1L;
        }
        return NOT_FOUND;
    }

}
