package com.pocketmath.stasov.attrs;

import com.pocketmath.stasov.attributes.AttributeHandler;
import com.pocketmath.stasov.attributes.AttributeType;

/**
 * Created by etucker on 3/14/15.
 */
@AttributeType
public class DeviceType extends AttributeHandler {

    @Override
    public long find(String input) {
        if (input.equalsIgnoreCase("iPhone")) {
            return 1;
        } else if (input.equalsIgnoreCase("Android")) {
            return 2;
        }
        return -1;
    }

}
