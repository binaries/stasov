package com.pocketmath.stasov.engine3.core.vesseltree;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.engine.EngineConfig;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by etucker on 8/16/16.
 */
class AttributeValues implements Comparable<AttributeValues> {

    final Object id;
    final long attrTypeId;
    final long[] inclVals;
    final long[] exclVals;

    AttributeValues(final Object id, final long attrTypeId, final LongSortedSet inclVals, final LongSortedSet exclVals) {
        Objects.requireNonNull(id);
        this.id = id;
        this.attrTypeId = attrTypeId;
        this.inclVals = inclVals == null ? null : inclVals.toLongArray();
        this.exclVals = exclVals == null ? null : exclVals.toLongArray();
    }

    public int compareTo(final AttributeValues o) {
        assert (o != null);

        int r = 0;

        // TODO: require id is typed as Comparable
        if (id instanceof Comparable && o.id instanceof Comparable) {
            r = ((Comparable) id).compareTo(o.id);
        } else {
            throw new UnsupportedOperationException("id was not instance of comparable");
        }
        if (r != 0) return r;

        final AttrSvcBase attrSvc = EngineConfig.getConfig().getAttrSvcBase();

        r = attrSvc.getAttrsComparator().compare(this.attrTypeId, o.attrTypeId);
        if (r != 0) return r;

        if (inclVals == null && o.inclVals != null) return -1;
        if (inclVals != null && o.inclVals == null) return +1;
        boolean inclValsEqual = inclVals == null && o.inclVals == null;
        if (!inclValsEqual) {
            if (this.inclVals.length <= o.inclVals.length) {
                for (int i = 0; i < this.inclVals.length; i++) {
                    r = Long.compare(this.inclVals[i], o.inclVals[i]);
                    if (r != 0) return r;
                }
                if (this.inclVals.length == o.inclVals.length) inclValsEqual = true;
            } else {
                for (int i = 0; i < o.inclVals.length; i++) {
                    r = Long.compare(o.inclVals[i], this.inclVals[i]);
                    if (r != 0) return r;
                }
            }
        }

        if (exclVals == null && o.exclVals != null) return -1;
        if (exclVals != null && o.exclVals == null) return +1;
        boolean exclValsEqual = exclVals == null && o.exclVals == null;
        if (!exclValsEqual) {
            if (this.exclVals.length <= o.exclVals.length) {
                for (int i = 0; i < this.exclVals.length; i++) {
                    r = Long.compare(this.exclVals[i], o.exclVals[i]);
                    if (r != 0) return r;
                }
                if (this.exclVals.length == o.exclVals.length) exclValsEqual = true;
            } else {
                for (int i = 0; i < o.exclVals.length; i++) {
                    r = Long.compare(o.exclVals[i], this.exclVals[i]);
                    if (r != 0) return r;
                }
            }
        }

        if (inclValsEqual && exclValsEqual) {
            return 0;
        }

        // return the longer one if not equal
        if (!inclValsEqual) {
            r = Integer.compare(this.inclVals.length, o.inclVals.length);
            if (r != 0) return r;
        }

        if (!exclValsEqual) {
            r = Integer.compare(this.exclVals.length, o.exclVals.length);
            if (r != 0) return r;  // r will be != 0 or there is a value in the code
        }

        // solution is totally solved; reaching this point means something is faulty in the code
        throw new IllegalStateException();
    }

    public long getAttrTypeId() {
        return attrTypeId;
    }

    public long[] getInclVals() {
        return inclVals;
    }

    public long[] getExclVals() {
        return exclVals;
    }

    public Object getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AttrVals{" +
                "attrTypeId=" + attrTypeId +
                ", inclVals=" + Arrays.toString(inclVals) +
                ", exclVals=" + Arrays.toString(exclVals) +
                '}';
    }
}