package com.pocketmath.stasov.attributes;

import com.google.common.collect.Sets;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by etucker on 3/29/15.
 */
public class AttrConfig {

    private final Set<String> attributesPackages;

    public AttrConfig(final Set<String> attributesPackages) {
        this.attributesPackages = Collections.unmodifiableSet(attributesPackages);
    }

    public AttrConfig() {
        this(Sets.newHashSet(new String[]{"com.pocketmath.stasov.attrs"}));
    }

    Iterable<String> getAttributesPackages() {
        return attributesPackages;
    }

}
