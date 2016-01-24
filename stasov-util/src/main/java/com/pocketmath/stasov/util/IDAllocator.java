package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by etucker on 3/23/15.
 *
 * This class is fully threadsafe.
 */
public class IDAllocator {

    private volatile PriorityQueue<Long> freeList = new PriorityQueue<Long>(); // prefer lower numbers
    private volatile LongSortedSet allocated = new LongRBTreeSet();

    private final long maxId;

    private volatile long seq;

    protected IDAllocator(final long maxId) {
        this.maxId = maxId;
    }

    @Deprecated
    public IDAllocator() {
        this(Long.MAX_VALUE);
    }

    public static IDAllocator newIDAllocator(final long maxId) {
        return new IDAllocator(maxId);
    }

    public static IDAllocator newIDAllocatorLongMax() {
        return newIDAllocator(Long.MAX_VALUE);
    }

    public static IDAllocator newIDAllocatorIntMax() {
        return newIDAllocator(Integer.MAX_VALUE);
    }

    public synchronized long allocateId() {
        if (!freeList.isEmpty()) return freeList.poll();
        do
            if (seq >= maxId)
                throw new IllegalStateException("no more available internal IDs");
        while (allocated.contains(seq++));
        allocated.add(seq);
        return seq;
    }

    public synchronized void deallocateId(final long id) {
        freeList.add(id);
        allocated.remove(id);
    }

}
