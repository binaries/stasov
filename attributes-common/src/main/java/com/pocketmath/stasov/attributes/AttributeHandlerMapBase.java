package com.pocketmath.stasov.attributes;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

/**
 * Created by etucker on 6/23/15.
 */
public class AttributeHandlerMapBase extends AttributeHandler {

    private static final long NOT_FOUND = -1;

    private final Object2LongMap<String> map = new Object2LongOpenHashMap<String>();
    {
        map.defaultReturnValue(NOT_FOUND);
    }

    protected final void put(final String name, final long id) {
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
