package com.pocketmath.stasov.attributes;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.pocketmath.stasov.attributes.handler.base.ExactMatchAttributeHandler;
import com.pocketmath.stasov.util.StasovArrays;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Arrays;

/**
 * Created by etucker on 6/23/15.
 */
public abstract class AttributeHandlerMapAutoId extends ExactMatchAttributeHandler {

    private long sequence = 0;

    private final Object2LongMap<String> map = new Object2LongOpenHashMap<String>();
    {
        map.defaultReturnValue(NOT_FOUND);
    }

    @Override
    public void put(final String name, final long id) {
        final String _name = name.toLowerCase();
        if (map.containsKey(_name)) throw new IllegalArgumentException("duplicate: " + name);
        if (map.containsValue(id)) throw new IllegalArgumentException("duplicate: " + id);
        map.put(name, id);
    }

    protected final void add(final String name) {
        put(name.toLowerCase(), ++sequence);
    }

    @Override
    public final long find(final String input) {
        return map.getLong(input.toLowerCase());
    }

    @Override
    public Iterable<String> sampleValues(final Order order) throws UnsupportedOperationException {
        if (order == null) throw new IllegalArgumentException("order was null");
        switch (order) {
            case UNDEFINED: {
                return Iterables.unmodifiableIterable(map.keySet());
            }
            case ASCENDING: {
                final ObjectSet<String> names = map.keySet();
                final String[] array = names.toArray(new String[names.size()]);
                Arrays.sort(array);
                return Iterables.unmodifiableIterable(Arrays.asList(array));
            }
            case DESCENDING: {
                final ObjectSet<String> names = map.keySet();
                final String[] array = names.toArray(new String[names.size()]);
                Arrays.sort(array);
                return Iterables.unmodifiableIterable(Lists.reverse(Arrays.asList(array)));
            }
            case RANDOM: {
                final ObjectSet<String> names = map.keySet();
                final String[] array = names.toArray(new String[names.size()]);
                StasovArrays.randomizeOrder(array);
                return Iterables.unmodifiableIterable(Arrays.asList(array));
            }
        }
        throw new IllegalStateException();
    }
}
