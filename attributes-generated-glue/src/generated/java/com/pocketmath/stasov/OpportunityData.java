// GENERATED FILE.  EDITING NOT RECOMMENDED.
// Generated at Thu Aug 27 14:08:59 CDT 2015 on host redtail.local by user etucker

package com.pocketmath.stasov;

import java.util.Set;

import com.pocketmath.stasov.engine.OpportunityDataBase;

// Generated imports:
import com.pocketmath.stasov.attrs.City;
import com.pocketmath.stasov.attrs.CreativeSize;
import com.pocketmath.stasov.attrs.DeviceId;
import com.pocketmath.stasov.attrs.DeviceType;

public abstract class OpportunityData extends OpportunityDataBase {

    @Override
    public final Set<String> getData(final long attrTypeId) {
        assert(attrTypeId < Integer.MAX_VALUE);
        assert(attrTypeId > 0);
        switch ((int)attrTypeId) {
            case 1 : { return getCity(); }
            case 2 : { return getCreativeSize(); }
            case 3 : { return getDeviceId(); }
            case 4 : { return getDeviceType(); }
            default : throw new UnsupportedOperationException("The attribute type was not found for attrTypeId=" + attrTypeId);
        }
    }

    protected abstract Set<String> getCity();
    protected abstract Set<String> getCreativeSize();
    protected abstract Set<String> getDeviceId();
    protected abstract Set<String> getDeviceType();
}