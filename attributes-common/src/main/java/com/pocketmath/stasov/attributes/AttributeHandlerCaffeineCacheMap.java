package com.pocketmath.stasov.attributes;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pocketmath.stasov.attributes.handler.base.ExactMatchAttributeHandler;

/**
 * Created by etucker on 1/12/16.
 */
class AttributeHandlerCaffeineCacheMap extends ExactMatchAttributeHandler {

    private final Cache<String,Long> cache;

    public AttributeHandlerCaffeineCacheMap(final int capacity) {
        this.cache = Caffeine.newBuilder().maximumSize(capacity).build();
    }

    @Override
    public void put(String name, long id) {
        cache.put(name, id);
    }

    @Override
    public long find(String input) {
        Long o = cache.getIfPresent(input);
        if (o == null) return NOT_FOUND;
        return o.longValue();
    }
}
