package com.pocketmath.stasov.util.cache;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;

/**
 * Created by etucker on 3/29/15.
 */
public class Object2LongFIFOCacheMap<K> {

    private final Object2LongLinkedOpenHashMap<K> map = new Object2LongLinkedOpenHashMap<K>();
    private final int capacity;

    public Object2LongFIFOCacheMap(final int capacity, final long defaultReturnValue) {
        this.capacity = capacity;
        map.defaultReturnValue(defaultReturnValue);
    }

    public Object2LongFIFOCacheMap(final int capacity) {
        this.capacity = capacity;
    }

    public Object2LongFIFOCacheMap() {
        this(1024*1024);
    }

    public void defaultReturnValue(long value) {
        map.defaultReturnValue(value);
    }

    public void put(final K key, final long value) {
        assert(value>=1);
        if (map.size() >= capacity) {
            map.removeFirstLong();
        }
    }

    public long get(final K key) {
        return map.get(key);
    }



    public int capacity() {
        return capacity;
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean isFull() {
        return map.size() == capacity;
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(K name) {
        return map.containsKey(name);
    }

    public boolean containsValue(long id) {
        return map.containsValue(id);
    }

    public long getLong(K s) {
        return map.getLong(s);
    }
}
