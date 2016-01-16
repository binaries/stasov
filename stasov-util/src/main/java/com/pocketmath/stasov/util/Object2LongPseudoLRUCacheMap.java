package com.pocketmath.stasov.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by etucker on 1/12/16.
 */
public class Object2LongPseudoLRUCacheMap<K> {

    static private class Entry {
        private long value;
        private int count;

        Entry(long value, int count) {
            this.value = value;
            this.count = count;
        }

        void setValue(long value) {
            this.value = value;
        }

        long getValue() {
            return value;
        }

        void setCount(int count) {
            this.count = count;
        }

        int getCount() {
            return count;
        }
    }

    private int count;

    private final Map<K,Entry> map = new HashMap<K, Entry>();

    public void put(final K key, final long value) {
        Entry entry = map.get(key);
        if (entry == null) {
            entry = new Entry(value, count);
        } else {
            entry.setValue(value);
            entry.setCount(count);
        }
        map.put(key, entry);
    }
}
