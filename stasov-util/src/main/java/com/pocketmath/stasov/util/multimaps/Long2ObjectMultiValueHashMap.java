package com.pocketmath.stasov.util.multimaps;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * Created by etucker on 2/3/16.
 */
public class Long2ObjectMultiValueHashMap<V> extends AbstractMultiValueMap<V>
        implements ILong2ObjectMultiValueMap<V> {

    private Long2ObjectMap<ObjectSet<V>> map = new Long2ObjectOpenHashMap<>();

    private long size = 0;

    @Override
    public void put(long key, V value) {
        ObjectSet<V> set = map.get(key);
        if (set == null) {
            set = new ObjectLinkedOpenHashSet<>();
            map.put(key, set);
        }
        if (set.add(value)) size++;
    }

    @Override
    public boolean containsKey(long key) {
        return map.containsKey(key);
    }

    @Override
    public ObjectSet<V> get(long key) {
        return map.get(key);
    }

    @Override
    public void remove(long key) {
        final ObjectSet<V> set = map.remove(key);
        if (set != null) size -= set.size();
    }

    @Override
    public void remove(long key, V value) {
        final ObjectSet<V> set = map.get(key);
        if (set == null) return;
        if (set.remove(value)) size--;
        if (set.isEmpty()) map.remove(key);

    }

    @Override
    public int occurrences(long key) {
        final ObjectSet<V> set = map.get(key);
        if (set == null) return 0;
        return set.size();
    }

    @Override
    public LongSet getKeys() {
        return map.keySet();
    }

    @Override
    public long size() {
        return size;
    }
}
