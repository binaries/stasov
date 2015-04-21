package com.pocketmath.stasov.attrs;

import com.pocketmath.stasov.attributes.AttributeHandler;

/**
 * Created by etucker on 3/14/15.
 */
public class DeviceType extends AttributeHandler {

    public long find(String input) {
        if (input.equalsIgnoreCase("iPhone")) {
            return 1;
        }
        return -1;
    }

}
