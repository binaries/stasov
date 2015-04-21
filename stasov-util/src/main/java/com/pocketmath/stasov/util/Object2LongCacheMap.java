package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;

/**
 * Created by etucker on 3/29/15.
 */
public class Object2LongCacheMap<K> {

    private final Object2LongLinkedOpenHashMap<K> map = new Object2LongLinkedOpenHashMap<K>();
    private final int capacity;

    public Object2LongCacheMap(final int capacity) {
        this.capacity = capacity;
    }

    public Object2LongCacheMap() {
        this(1024*1024);
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
}
