package com.pocketmath.stasov.engine2.core;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by etucker on 3/16/16.
 */
public abstract class MatchNod2 {

    private final long attrTypeId;

    public MatchNod2(final long attrTypeId) {
        if (attrTypeId <= 0) throw new IllegalArgumentException();

        this.attrTypeId = attrTypeId;
    }

    public long getAttrTypeId() {
        return attrTypeId;
    }

    public abstract void onInputValues(
            final @Nonnull Vessel vessel,
            final @Nonnull Set<Value> inputValues);
}
