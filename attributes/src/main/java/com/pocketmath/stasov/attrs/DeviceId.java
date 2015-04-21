package com.pocketmath.stasov.attrs;

import com.pocketmath.stasov.attributes.AttributeHandler;

/**
 * Created by etucker on 3/30/15.
 */
public class DeviceId extends AttributeHandler {

    public DeviceId() {
    }

    public long find(String input) {
        if (input.equalsIgnoreCase("0")) {
            return 1L;
        }
        return -1L;
    }

}
