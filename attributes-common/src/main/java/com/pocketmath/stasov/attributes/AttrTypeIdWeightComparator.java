package com.pocketmath.stasov.attributes;

import com.pocketmath.stasov.util.Weights;
import it.unimi.dsi.fastutil.longs.AbstractLongComparator;
import it.unimi.dsi.fastutil.longs.LongComparator;

/**
 * Created by etucker on 4/3/15.
 */
class AttrTypeIdWeightComparator extends AbstractLongComparator implements LongComparator {

    private Weights attrWeights;

    public AttrTypeIdWeightComparator(final Weights attrWeights) {
        this.attrWeights = attrWeights;
    }

    @Override
    public int compare(long attrTypeId1, long attrTypeId2) {
        final double w1 = attrWeights.getOrAssignWeight(attrTypeId1);
        final double w2 = attrWeights.getOrAssignWeight(attrTypeId2);
        final int r = Double.compare(w1, w2);
        if (r != 0) return r;
        return Long.compare(attrTypeId1, attrTypeId2);
    }
}
