package com.pocketmath.stasov.attributes;

import com.pocketmath.stasov.util.Object2LongFIFOCacheMap;

/**
 * Created by etucker on 1/12/16.
 */
class AttributeHandlerFIFOCacheMap extends ExactMatchAttributeHandler {

    private final Object2LongFIFOCacheMap<String> map;

    public AttributeHandlerFIFOCacheMap(final int capacity) {
        map = new Object2LongFIFOCacheMap<String>(capacity, NOT_FOUND);
    }

    @Override
    public final void put(final String name, final long id) {
        final String _name = name.toLowerCase();
        if (map.containsKey(_name)) throw new IllegalArgumentException("duplicate: " + name);
        if (map.containsValue(id)) throw new IllegalArgumentException("duplicate: " + id);
        map.put(_name, id);
    }

    @Override
    public final long find(final String input) {
        return map.getLong(input.toLowerCase());
    }

}
