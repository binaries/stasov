// GENERATED FILE.  EDITING NOT RECOMMENDED.
// Generated at Thu Apr 16 18:42:55 CDT 2015 on host redtail.local by user etucker

package com.pocketmath.stasov.attributes;

import java.util.HashSet;
import java.util.Set;

import it.unimi.dsi.fastutil.longs.*;

// Generated imports:
import com.pocketmath.stasov.attrs.City;
import com.pocketmath.stasov.attrs.DeviceId;
import com.pocketmath.stasov.attrs.DeviceType;

public class AttributeHandlers extends AttrSvcBase {

    private final City city;
    private final DeviceId deviceid;
    private final DeviceType devicetype;

    public AttributeHandlers() {
        final Set<AttrMeta> attrMetas = new HashSet<AttrMeta>();
        city = new City();
        attrMetas.add(new AttrMeta("City", "city", 1));
        deviceid = new DeviceId();
        attrMetas.add(new AttrMeta("DeviceId", "deviceid", 2));
        devicetype = new DeviceType();
        attrMetas.add(new AttrMeta("DeviceType", "devicetype", 3));

        register(attrMetas);
    }

    protected final AttributeHandler lookupHandler(final long attrTypeId) {
        assert(attrTypeId < Integer.MAX_VALUE);
        switch ((int)attrTypeId) {
            case 1 : { return city; }
            case 2 : { return deviceid; }
            case 3 : { return devicetype; }
            default : throw new UnsupportedOperationException();
        }
    }

}