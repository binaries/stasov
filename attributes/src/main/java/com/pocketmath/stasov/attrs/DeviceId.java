package com.pocketmath.stasov.attrs;

import com.pocketmath.stasov.attributes.AttributeHandler;
import com.pocketmath.stasov.attributes.AttributeType;
import com.pocketmath.stasov.attributes.Order;

/**
 * Created by etucker on 3/30/15.
 */
@AttributeType
public class DeviceId extends AttributeHandler {

    @Override
    public long find(String input) {
        if (input.equalsIgnoreCase("0")) {
            return 1L;
        }
        return -1L;
    }

}
