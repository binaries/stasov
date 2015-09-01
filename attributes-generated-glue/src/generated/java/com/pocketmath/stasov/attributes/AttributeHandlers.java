// GENERATED FILE.  EDITING NOT RECOMMENDED.

// Generated at Thu Aug 27 14:08:59 CDT 2015 on host redtail.local by user etucker

package com.pocketmath.stasov.attributes;

import java.util.HashSet;
import java.util.Set;

import it.unimi.dsi.fastutil.longs.*;

// Generated imports:
import com.pocketmath.stasov.attrs.City;
import com.pocketmath.stasov.attrs.CreativeSize;
import com.pocketmath.stasov.attrs.DeviceId;
import com.pocketmath.stasov.attrs.DeviceType;

public class AttributeHandlers extends AttrSvcBase {

    private final City city;
    private final CreativeSize creativesize;
    private final DeviceId deviceid;
    private final DeviceType devicetype;

    public AttributeHandlers() {
        final Set<AttrMeta> attrMetas = new HashSet<AttrMeta>();
        city = new City();
        attrMetas.add(new AttrMeta("City", "city", 1));
        creativesize = new CreativeSize();
        attrMetas.add(new AttrMeta("CreativeSize", "creativesize", 2));
        deviceid = new DeviceId();
        attrMetas.add(new AttrMeta("DeviceId", "deviceid", 3));
        devicetype = new DeviceType();
        attrMetas.add(new AttrMeta("DeviceType", "devicetype", 4));

        register(attrMetas);
    }

    protected final AttributeHandler lookupHandler(final long attrTypeId) {
        assert(attrTypeId < Integer.MAX_VALUE);
        switch ((int)attrTypeId) {
            case 1 : { return city; }
            case 2 : { return creativesize; }
            case 3 : { return deviceid; }
            case 4 : { return devicetype; }
            default : throw new UnsupportedOperationException();
        }
    }

}