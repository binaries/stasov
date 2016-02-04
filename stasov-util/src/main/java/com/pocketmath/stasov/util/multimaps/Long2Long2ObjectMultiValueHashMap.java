package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.PrettyPrintable;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by etucker on 2/3/16.
 */
public class Long2Long2ObjectMultiValueHashMap<V extends Comparable<V>> implements ILong2Long2ObjectMultiValueMap<V> {

    private final Long2ObjectMap<ILong2ObjectMultiValueMap<V>> map =
            new Long2ObjectOpenHashMap<>();

    private long size = 0;

    @Override
    public void put(long key1, long key2, V value) {
        ILong2ObjectMultiValueMap<V> map2 = map.get(key1);
        if (map2 == null) {
            map2 = new Long2ObjectMultiValueHashMap<>();
            map.put(key1, map2);
        }
        map2.put(key2, value);
    }

    @Override
    public void put(long key1, long[] keys2, V value) {
        for (final long key2 : keys2) {
            put(key1, key2, value);
        }
    }

    @Override
    public boolean containsKey(long key1, long key2) {
        final ILong2ObjectMultiValueMap<V> map2 = map.get(key1);
        if (map2 == null) return false;
        return map2.containsKey(key2);
    }

    @Override
    public boolean containsKey(long key1) {
        return map.containsKey(key1);
    }

    @Override
    public ObjectSet<V> get(long key1, long key2) {
        final ILong2ObjectMultiValueMap<V> map2 = map.get(key1);
        if (map2 == null) return null;
        return map2.get(key2);
    }

    @Override
    public LongSet getKeys1() {
        return map.keySet();
    }

    @Override
    public LongSet getKeys2(long key1) {
        final ILong2ObjectMultiValueMap map2 = map.get(key1);
        if (map2 == null) return null;
        return map2.getKeys();
    }

    @Override
    public void remove(long key1) {
        final ILong2ObjectMultiValueMap<V> removed = map.remove(key1);
        if (removed == null) return;
        size -= removed.size();
    }

    @Override
    public void remove(long key1, long key2) {
        final ILong2ObjectMultiValueMap<V> map2 = map.get(key1);
        if (map2 == null) return;
        final ObjectSet<V> set = map2.get(key2);
        if (set == null) return;
        map2.remove(key2);
        size -= set.size();
    }

    @Override
    public void remove(long key1, long key2, long value) {
        final ILong2ObjectMultiValueMap<V> map2 = map.get(key1);
        if (map2 == null) return;
        final ObjectSet<V> set = map2.get(key1);
        if (set == null) return;
        if (set.remove(value)) size--;
    }

    @Override
    public boolean isEmpty() {
        assert(size >= 0);
        return size <= 0;
    }

    @Override
    public boolean matchesAll(long key1, long[] keys2, V value) {
        final ILong2ObjectMultiValueMap subMap = map.get(key1);
        return subMap != null ? subMap.matchesAll(keys2, value) : false;
    }

    @Override
    public String prettyPrint(String prefix) {
        return null;
    }

    @Override
    public String prettyPrint(final String prefix, final String key1Prefix, final String key2Prefix, final String valuePrefix) {
        return Long2Long2ObjectMapUtil.<V>prettyPrint(this, prefix, key1Prefix, key2Prefix, valuePrefix);
    }

}
