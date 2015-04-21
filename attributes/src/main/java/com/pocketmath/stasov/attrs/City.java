package com.pocketmath.stasov.attrs;

import com.pocketmath.stasov.attributes.AttributeHandler;

/**
 * Created by etucker on 3/13/15.
 */
public class City extends AttributeHandler {

    public long find(String input) {
        if (input.equalsIgnoreCase("Austin")) return 1;
        else if (input.equalsIgnoreCase("Singapore")) return 2;
        else if (input.equalsIgnoreCase("San Francisco")) return 3;
        else if (input.equalsIgnoreCase("Houston")) return 4;
        return -1;
    }

}
