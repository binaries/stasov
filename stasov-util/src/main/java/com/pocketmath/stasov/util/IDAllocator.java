package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by etucker on 3/23/15.
 *
 * This class is threadsafe.
 */
public class IDAllocator {

    private volatile PriorityQueue<Long> freeList = new PriorityQueue<Long>(); // prefer lower numbers
    private volatile PriorityQueue<Long> allocated = new PriorityQueue<Long>(LongComparators.OPPOSITE_COMPARATOR); // prefer higher numbers when iterating or force deallocating

    private final long maxId;

    private volatile long seq;

    protected IDAllocator(final long maxId) {
        this.maxId = maxId;
        checkInvariants();
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

    private void checkInvariants() {
        if (freeList == null) throw new IllegalStateException();
        if (allocated == null) throw new IllegalStateException();
        if (maxId < 1) throw new IllegalStateException();
        if (seq >= maxId) throw new IllegalStateException();
        if (freeList.size() > maxId - allocated.size()) throw new IllegalStateException("maxId=" + maxId + ", allocated.size()=" + allocated.size() + ", freeList.size()=" + freeList.size() + ", maxId - allocated.size() = " + (maxId - allocated.size()));
        if (freeList.size() > maxId) throw new IllegalStateException();
        if (allocated.size() > maxId) throw new IllegalStateException();
    }

    public synchronized long allocateId() {
        checkInvariants();

        if (!freeList.isEmpty()) return freeList.poll();
        do
            if (seq >= maxId)
                throw new IllegalStateException("no more available internal IDs");
        while (allocated.contains(seq++));
        allocated.add(seq);

        checkInvariants();

        return seq;
    }

    public synchronized void deallocateId(final long id) {
        checkInvariants();

        freeList.add(id);
        allocated.remove(id);

        checkInvariants();
    }

    /**
     *
     * @return from largest ID down
     */
    public synchronized Iterator<Long> allocatedIdsDescendingIterator() {
        checkInvariants();
        return allocated.iterator();
    }

    public synchronized long deallocate() {
        checkInvariants();

        final Long id = allocated.poll();
        if (id == null) return -1l;
        freeList.add(id);

        checkInvariants();

        return id.longValue();
    }

    public synchronized long allocatedCount() {
        return allocated.size();
    }

    public synchronized long availableCount() {
        return maxId - allocated.size();
    }

}
