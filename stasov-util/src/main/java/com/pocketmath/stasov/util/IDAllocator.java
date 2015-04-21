package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Queue;

/**
 * Created by etucker on 3/23/15.
 *
 * This class is fully threadsafe.
 */
public class IDAllocator {

    private volatile Queue<Long> freeList = new ArrayDeque<Long>();
    private volatile LongSortedSet allocated = new LongRBTreeSet();

    private volatile long seq;

    public synchronized long allocateId() {
        if (!freeList.isEmpty()) return freeList.poll();
        while (allocated.contains(seq++));
        allocated.add(seq);
        return seq;
    }

    public synchronized void deallocateId(final long id) {
        freeList.add(id);
        allocated.remove(id);
    }

}
