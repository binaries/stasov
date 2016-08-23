package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.IndexAlgorithm;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.LinkedHashSet;

/**
 * Cache with FIFO removal when load factor exceeded.
 *
 * Created by etucker on 8/15/16.
 */
public abstract class Long2ObjectMultiValueHashReadCachedMap<V extends Comparable<V>> extends Long2ObjectMultiValueHashMap<V> {

    private class CacheEntry {
        private V[] values;

        public CacheEntry() {
            this.values = null;
        }

        public CacheEntry(final V[] values) {
            this.values = values;
        }

        public V[] getValues() {
            return values;
        }

        public void setValues(final V[] values) {
            this.values = values;
        }

        public boolean isEmpty() {
            return this.values == null;
        }
    }

    private final Long2ObjectLinkedOpenHashMap<CacheEntry> cache = new Long2ObjectLinkedOpenHashMap<>();

    private final float loadFactor;

    public Long2ObjectMultiValueHashReadCachedMap(final float loadFactor, final IndexAlgorithm valuesIndexAlgorithm) {
        super(valuesIndexAlgorithm);
        this.loadFactor = loadFactor;
    }

    public Long2ObjectMultiValueHashReadCachedMap() {
        super();
        this.loadFactor = 3f;
    }

    /**
     *
     * @return cache entries removed
     */
    protected int tryTrimCache() {
        int removalsCount = 0;
        while (size() > 0 && cache.size() > 0 && ((float)cache.size()) / ((float)size()) > loadFactor) {
            final CacheEntry cacheEntry = cache.removeFirst();
            if (cacheEntry == null)
                throw new IllegalStateException();
            removalsCount++;
        }
        return removalsCount;
    }

    protected void invalidateCache(final long key) {
        cache.remove(key);
    }

    protected abstract V[] newValuesArray(final int size);

    public V[] getArray(final long key) {
        CacheEntry ce = cache.get(key);
        if (ce == null) {
            // miss
            final ObjectSet<V> values = get(key);
            if (values == null) {
                //final CacheEntry ceNew = new CacheEntry();
                //cache.put(key, ceNew);
                //ce = ceNew;
                return null;
            } else {
                assert values != null;
                tryTrimCache();
                final V[] valuesArray = values.toArray(newValuesArray(values.size()));
                final CacheEntry ceNew = new CacheEntry(valuesArray);
                cache.put(key, ceNew);
                ce = ceNew;
            }
        }
        assert ce != null;
        assert ce.getValues() == null && ce.isEmpty() || ce.getValues() != null && ! ce.isEmpty();
        return ce.getValues();
    }

    @Override
    public void put(final long key, final V value) {
        if (value == null)
            throw new IllegalArgumentException();
        super.put(key, value);
        invalidateCache(key);
        tryTrimCache();
    }

    @Override
    public void remove(final long key) {
        super.remove(key);
        invalidateCache(key);
    }

    @Override
    public void remove(final long key, final V value) {
        if (value == null)
            throw new IllegalStateException();
        super.remove(key, value);
        invalidateCache(key);
    }

    public void clear() {
        cache.clear();
    }
}
