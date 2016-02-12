package com.pocketmath.stasov.attrs;

import com.pocketmath.stasov.attributes.AttributeHandler;
import com.pocketmath.stasov.attributes.AttributeType;

/**
 * Created by etucker on 2/4/16.
 */
@AttributeType
public class ZipCode extends AttributeHandler {
    @Override
    public long find(String input) {
        return Long.parseLong(input);
    }
}
