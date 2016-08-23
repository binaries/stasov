package com.pocketmath.stasov.engine2.core;

import com.pocketmath.stasov.engine.OpportunityQueryBase;

import java.util.BitSet;

/**
 * Created by etucker on 3/16/16.
 */
public class OrGrou2 {

    private AndGrou2Impl2[] array;

    public void function(final BitSet bs) {
        assert(bs != null);
        assert(query != null);

        for (int i = 0; i < array.length; i++) {
            array[i].function(bs, query);
        }
    }

}
