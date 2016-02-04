package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.IndexAlgorithm;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * Created by etucker on 2/3/16.
 */
public class Long2ObjectMultiValueHashMap<V> extends AbstractMultiValueMap<V>
        implements ILong2ObjectMultiValueMap<V> {

    private Long2ObjectMap<ObjectSet<V>> map = new Long2ObjectOpenHashMap<>();

    private long size = 0;

    private final IndexAlgorithm valuesIndexAlgorithm;

    /**
     * @param valuesIndexAlgorithm Used as the algorithm to store sets of values which have the same keys.  Note internally hash is used in all other mappings.
     */
    public Long2ObjectMultiValueHashMap(final IndexAlgorithm valuesIndexAlgorithm) {
        this.valuesIndexAlgorithm = valuesIndexAlgorithm;
    }

    public Long2ObjectMultiValueHashMap() {
        this(IndexAlgorithm.HASH);
    }

    @Override
    public void put(long key, V value) {
        ObjectSet<V> set = map.get(key);
        if (set == null) {
            switch (valuesIndexAlgorithm) {
                case HASH : { set = new ObjectLinkedOpenHashSet<>(); break; }
                case ARRAY : { set = new ObjectArraySet<>(); break; }
                default : throw new UnsupportedOperationException();
            }

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
    public boolean matchesAll(long[] keys, V value) {
        for (long key : keys) {
            if (!value.equals(map.get(key))) return false;
        }
        return true;
    }

    @Override
    public long size() {
        return size;
    }
}
