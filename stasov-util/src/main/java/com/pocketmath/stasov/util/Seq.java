package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2IntAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2IntRBTreeMap;
import it.unimi.dsi.fastutil.longs.Long2IntSortedMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;

/**
 * Created by etucker on 3/22/15.
 */
public class Seq {

    transient private static final long EMPTY_KEY = -1L;
    transient public static final int EMPTY_INDEX = -1;

    private final LongArrayList array;
    transient private final Long2IntSortedMap map;
    transient private final IntArrayList freeList;

    public Seq(final TreeAlgorithm mapTreeAlg) {
        switch(mapTreeAlg) {
            case REDBLACK:  { map = new Long2IntRBTreeMap(); break; }
            case AVL:       { map = new Long2IntAVLTreeMap(); break; }
            default:        { throw new IllegalStateException(); }
        }
        map.defaultReturnValue(EMPTY_INDEX);
        array = new LongArrayList();
        array.add(EMPTY_KEY); // filler to prevent clash with default return value of index=0
        freeList = new IntArrayList();
    }

    public Seq() {
        this(TreeAlgorithm.AVL);
    }

    public int getIndex(final long key) {
        return map.get(key);
    }

    public int getOrAssignIndex(final long key) {
        int index = map.get(key);
        if (index != EMPTY_INDEX) {
            return index;
        } else {
            index = (!freeList.isEmpty()) ? freeList.popInt() : array.size();
            array.add(key);
            map.put(key, index);
            assert(array.get(index)==key);
            return index;
        }
    }

    public long getKey(final int index) {
        return array.get(index);
    }

    public boolean containsKey(final long key) {
        return map.containsKey(key);
    }

    public boolean containsIndex(final int index) {
        if (index >= array.size()) return false;
        return array.get(index) != EMPTY_KEY;
    }

    public void removeKey(final long key) {
        int index = map.get(key);
        if (index != EMPTY_INDEX) {
            array.set(index, EMPTY_KEY);
            assert(!freeList.contains(index));
            freeList.add(index);
        }
        map.remove(key);
        assert(!map.containsKey(EMPTY_KEY));
    }

    public void removeIndex(final int index) {
        if (index >= array.size()) return;
        final long key = array.get(index);
        if (key != EMPTY_KEY) {
            map.remove(key);
            assert(!freeList.contains(index));
            freeList.add(index);
        }
        array.set(index, EMPTY_KEY);
        assert(!map.containsKey(EMPTY_KEY));
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        array.clear();
        map.clear();
        freeList.clear();
    }
}
