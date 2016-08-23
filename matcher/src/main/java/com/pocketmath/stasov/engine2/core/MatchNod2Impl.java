package com.pocketmath.stasov.engine2.core;

import com.pocketmath.stasov.util.multimaps.IMultiValueMap;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by etucker on 3/19/16.
 */
public class MatchNod2Impl<ValueType,MatchNod2> extends MatchNod2 {

    /**
     * Value to matches
     */
    private final IMultiValueMap<ValueType,MatchNod2> map = null;

    @Override
    public void onInputValues(@Nonnull Vessel vessel, @Nonnull Set<Value> inputValues) {
        for (final Value inputValue : inputValues) {
            map.get(inputValue);
        }
    }

}
