package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import java.util.Comparator;

/**
 * Created by etucker on 3/26/15.
 */
public class Long2LongMultiValueMap<V extends Comparable<V>> extends AbstractMultiValueMap<V> {

    private final Long2ObjectMap<LongSortedSet> map;

    public Long2LongMultiValueMap(final TreeAlgorithm treeAlgorithm) {
        super(null, treeAlgorithm);
        switch (treeAlgorithm) {
            case REDBLACK:  { map = new Long2ObjectRBTreeMap<LongSortedSet>(); break; }
            case AVL: { map = new Long2ObjectAVLTreeMap<LongSortedSet>(); break; }
            default: { throw new IllegalStateException(); }
        }
    }

    public Long2LongMultiValueMap() {
        this(TreeAlgorithm.AVL);
    }

    public void put(final long key, final long value) {
        LongSortedSet set = map.get(key);
        if (set == null) {
            switch(treeAlgorithm) {
                case REDBLACK:  { set = new LongRBTreeSet(); break; }
                case AVL:       { set = new LongAVLTreeSet(); break; }
                default: { throw new IllegalStateException(); }
            }
            map.put(key, set);
        }
        set.add(value);
    }

    public void fastPut(final long key, final LongSortedSet set) {
        map.put(key, set);
    }

    public LongSortedSet get(final long key) {
        return map.get(key);
    }

    public boolean contains(final long key, final long value) {
        LongSortedSet set = map.get(key);
        if (set == null) return false;
        return set.contains(value);
    }

    public boolean containsKey(final long key) {
        return map.containsKey(key);
    }

    public void remove(final long key, final long value) {
        final LongSortedSet set = map.get(key);
        if (set != null) set.remove(value);
        else return;
        if (set.isEmpty()) map.remove(key);
    }

    public void remove(final long key) {
        map.remove(key);
    }

    public LongSet getKeys() {
        return map.keySet();
    }

    public void clear() {
        map.clear();
    }

    @Override
    public String toString() {
        return "Long2LongMultiValueMap{" +
                "multiValueMap=" + map +
                '}';
    }
}
