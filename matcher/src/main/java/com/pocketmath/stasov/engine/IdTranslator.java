package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.util.IDAllocator;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by etucker on 1/22/16.
 */
class IdTranslator<IDT> {

    private final Tracker tracker;
    private final IDAllocator idAllocator = IDAllocator.newIDAllocatorIntMax();

    private final Long2ObjectMap<IDT> ido = new Long2ObjectOpenHashMap<IDT>();
    { ido.defaultReturnValue(null); }

    private final Object2LongMap<IDT> oid = new Object2LongOpenHashMap<IDT>();
    { oid.defaultReturnValue(-1l); }

    public IdTranslator(final Tracker tracker) {
        this.tracker = tracker;
    }

    private void checkInvariants() {
        if (tracker == null) throw new IllegalStateException();
        if (idAllocator == null) throw new IllegalStateException();
        if (ido == null) throw new IllegalStateException();
        if (oid == null) throw new IllegalStateException();
    }

    /**
     * toId without the internal safety checks.
     *
     * @param o
     * @return
     */
    public long fastToId(final IDT o) {
        long id = oid.getLong(o);
        if (id != oid.defaultReturnValue()) return id;
        id = idAllocator.allocateId();
        if (id <= 0) throw new IllegalStateException();
        ido.put(id, o);
        oid.put(o, id);
        return id;
    }

    public long toId(final IDT o) {
        checkInvariants();
        if (o == null) throw new IllegalArgumentException();
        final long id = fastToId(o);
        checkInvariants();
        return id;
    }

    /**
     * toO without the internal safety checks.
     *
     * @param id
     * @return corresponding object or null if not present
     */
    public IDT fastToO(final long id) {
        return ido.get(id); // use of value other than null in defaultReturnValue should use .equals(...) test
    }

    /**
     *
     * @param id
     * @return corresponding object or null if not present
     */
    public IDT toO(final long id) {
        checkInvariants();
        if (id < 1) throw new IllegalArgumentException();
        final IDT o = fastToO(id);
        checkInvariants();
        return o;
    }

    private static void refactorBitSet(final Long2LongMap m, final BitSet oldBS, final BitSet newBS) {
        if (m == null) throw new IllegalArgumentException();
        if (oldBS == null) throw new IllegalArgumentException();
        if (newBS == null) throw new IllegalArgumentException();
        for (int oldId = oldBS.nextSetBit(0); oldId >= 0; oldId = oldBS.nextSetBit(oldId)) {
            if (oldId == Integer.MAX_VALUE) break; // or (i+1) would overflow (per Java 8 API)
            final long newId = m.get((long)oldId);
            if (newId > Integer.MAX_VALUE) throw new IllegalStateException();
            if (newId > 0) newBS.set((int)newId);
        }
    }

    /**
     *
     * @param n max number of ids to refresh
     * @return
     */
    private Long2LongMap allocateRefreshedIds(final int n) {
        checkInvariants();
        final Long2LongMap map = new Long2LongOpenHashMap();
        final Iterator<Long> itr = idAllocator.allocatedIdsDescendingIterator();
        int i = 0;
        while (itr.hasNext() && i < n) {
            final long oldId = itr.next();
            if (!tracker.inUse(oldId)) continue;
            idAllocator.deallocateId(oldId);
            final long newId = idAllocator.allocateId();
            map.put(oldId, newId);
        }
        checkInvariants();
        return map;
    }

    private void updateMatches(final Long2LongMap map) {
        checkInvariants();
        for (final MatchTree.Node n : tracker.allNodes()) {
            final BitSet oldBS = n.getMatches();
            final BitSet newBS = new BitSet();
            refactorBitSet(map, oldBS, newBS);
            if (oldBS.cardinality() < newBS.cardinality()) throw new IllegalStateException("matches unexpectedly grew during conversion");
            n.setMatches(newBS);
        }
        checkInvariants();
    }

    private void updateObjectMappings(final Long2LongMap map) {
        checkInvariants();
        if (map == null) throw new IllegalStateException();
        for (final Map.Entry<Long, Long> entry : map.entrySet()) {
            final Long oldId = entry.getKey();
            final Long newId = entry.getValue();
            if (oldId == null) throw new IllegalStateException();
            if (newId == null) throw new IllegalStateException();
            final long loldId = oldId.longValue();
            final long lnewId = newId.longValue();
            if (loldId < 1) throw new IllegalStateException();
            if (lnewId < 1) throw new IllegalStateException();
            final IDT o = ido.get(loldId);
            ido.remove(loldId);
            oid.remove(o);
            ido.put(lnewId, o);
            oid.put(o, lnewId);
        }
        checkInvariants();
    }

    /**
     *
     * @param n max number of ids to refresh
     * @return
     */
    public int refreshIds(final int n) {
        checkInvariants();
        if (n < 1) throw new IllegalArgumentException("The max number of IDs to refresh must be at least one.");
        Long2LongMap map = allocateRefreshedIds(n);
        updateMatches(map);
        updateObjectMappings(map);
        checkInvariants();
        return map.size();
    }

}