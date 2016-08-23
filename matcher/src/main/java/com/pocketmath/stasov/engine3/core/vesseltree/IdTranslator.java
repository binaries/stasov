package com.pocketmath.stasov.engine3.core.vesseltree;

import com.pocketmath.stasov.engine.EngineConfig;
import com.pocketmath.stasov.engine.MatchNode;
import com.pocketmath.stasov.engine.Tracker;
import com.pocketmath.stasov.util.IDAllocator;
import com.pocketmath.stasov.util.numbers.Numbers;
import com.pocketmath.stasov.util.validate.ValidationException;
import it.unimi.dsi.fastutil.longs.Long2LongRBTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;

import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 1/22/16.
 */
class IdTranslator<IDT> {

    private Logger logger = Logger.getLogger(getClass().getName());
    {
        // shameless hard coded logging setup

        final EngineConfig cfg = EngineConfig.getConfig();
        final Level level = cfg.getLogLevel();

        final ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(level);

        logger.setLevel(level);
        logger.addHandler(consoleHandler);
    }

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
        if (ido.size() != oid.size()) throw new IllegalStateException();
        if (ido.size() != idAllocator.allocatedCount()) throw new IllegalStateException("ido.size(): " + ido.size() + "  idAllocator.allocatedCount(): " + idAllocator.allocatedCount());
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

    public void remove(final IDT o) {
        checkInvariants();
        long id = oid.getLong(o);
        if (id == oid.defaultReturnValue())
            throw new IllegalStateException("attempting to remove non-existent object: " + o);
        oid.remove(o);
        ido.remove(id);
        idAllocator.deallocateId(id);
        checkInvariants();
    }

    private static class Refresh {
        private final Set<Long> oldIdsToRemove;
        private final Map<Long,Long> oldIdToNewIdUpdates;

        public Refresh(final Set<Long> oldIdsToRemove, final Map<Long, Long> oldIdToNewIdUpdates) {
            this.oldIdsToRemove = oldIdsToRemove;
            this.oldIdToNewIdUpdates = oldIdToNewIdUpdates;
        }

        public void validate() throws ValidationException {
            if (oldIdsToRemove == null) throw new ValidationException();
            if (oldIdToNewIdUpdates == null) throw new ValidationException();

            // Verify that the items of removal set and the keys of map of new things are mutually exclusive.
            for (final Long oldId : oldIdsToRemove) {
                if (oldId <= 0) throw new ValidationException();

                if (oldIdToNewIdUpdates.containsKey(oldId)) throw new ValidationException();
            }
            for (final Long oldId : oldIdToNewIdUpdates.keySet()) {
                if (oldId <= 0) throw new ValidationException();

                if (oldIdsToRemove.contains(oldId)) throw new ValidationException();

                final Long newId = oldIdToNewIdUpdates.get(oldId);
                if (newId == null) throw new ValidationException();
                if (newId <= 0) throw new ValidationException();
            }
        }

        public Set<Long> getOldIdsToRemove() {
            return oldIdsToRemove;
        }

        public Map<Long, Long> getOldIdToNewIdUpdate() {
            return oldIdToNewIdUpdates;
        }

        public int size() {
            return Math.addExact(oldIdsToRemove.size(), oldIdToNewIdUpdates.size()); // checks for overflow
        }
    }

    private static void refactorBitSet(final Refresh refresh, final BitSet bitSet) {
        if (refresh == null) throw new IllegalArgumentException();
        if (bitSet == null) throw new IllegalArgumentException();

        for (final long removal : refresh.getOldIdsToRemove()) bitSet.clear(Numbers.toIntPosC(removal));

        final Map<Long,Long> updates = refresh.getOldIdToNewIdUpdate();
        final Set<Long> newIds = new HashSet<Long>(updates.values());
        for (final long oldId : updates.keySet()) {
            final int oldIdi = Numbers.toIntPosC(oldId);
            if (bitSet.get(oldIdi)) {
                final Long newId = updates.get(oldId);
                if (newId == null) throw new IllegalStateException();
                final int newIdi = Numbers.toIntPosC(newId);
                if (!newIds.contains(oldId)) bitSet.clear(oldIdi);
                bitSet.set(newIdi);
            }
        }
    }

    /**
     *
     * @param n max number of ids to refresh
     * @return
     */
    private Refresh allocateRefreshedIds(final int n) {
        checkInvariants();
        logger.log(Level.FINEST, "tracker: " + tracker.toString());

        final Set<Long> oldIdsToRemove = new LinkedHashSet<Long>();
        final Map<Long,Long> oldIdToNewIdUpdates = new Long2LongRBTreeMap();

        logger.log(Level.FINEST, "idAllocator.allocatedCount(): " + idAllocator.allocatedCount());
        final Iterator<Long> itr = idAllocator.allocatedIdsDescendingIterator();
        int i = 0;
        while (itr.hasNext() && i < n) {
            final long oldId = itr.next();
            if (!tracker.inUse(oldId)) {
                logger.log(Level.FINEST, "not in use, old (untranslated) ID: " + oldId);
                oldIdsToRemove.add(oldId);
                continue;
            }

            final long allocatedCount0 = idAllocator.allocatedCount();

            idAllocator.deallocateId(oldId);
            final long newId = idAllocator.allocateId();

            final long allocatedCount1 = idAllocator.allocatedCount();
            if (allocatedCount0 != allocatedCount1) throw new IllegalStateException("lost an id? allocatedCount0=" + allocatedCount0 + " allocatedCount1=" + allocatedCount1);

            oldIdToNewIdUpdates.put(oldId, newId);
            i++;
        }
        checkInvariants();
        return new Refresh(oldIdsToRemove,oldIdToNewIdUpdates);
    }

    private void updateMatches(final Refresh refresh) {
        checkInvariants();
        for (final MatchNode n : tracker.allNodes()) {
            final BitSet bs = n.getMatches();
            refactorBitSet(refresh, bs);
            n.setMatches(bs); // possibly redundant

            /*
            final BitSet oldBS = n.getMatches();
            final BitSet newBS = new BitSet();
            refactorBitSet(map, oldBS, newBS);
            if (oldBS.cardinality() < newBS.cardinality()) throw new IllegalStateException("matches unexpectedly grew during conversion");
            n.setMatches(newBS);
            */
        }
        checkInvariants();
    }

    private void updateObjectMappings(final Refresh refresh) {
        checkInvariants();
        final Map<Long, Long> map = refresh.getOldIdToNewIdUpdate();
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

            if (o == null) throw new IllegalStateException();

            final int idoSize0 = ido.size();
            final int oidSize0 = oid.size();

            ido.remove(loldId);
            oid.remove(o);
            ido.put(lnewId, o);
            oid.put(o, lnewId);

            final int idoSize1 = ido.size();
            final int oidSize1 = oid.size();
            if (idoSize0 != idoSize1) throw new IllegalStateException();
            if (oidSize0 != oidSize1) throw new IllegalStateException();
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
        final Refresh refresh = allocateRefreshedIds(n);
        try {
            refresh.validate();
        } catch (ValidationException ve) {
            throw new IllegalStateException();
        }
        updateMatches(refresh);
        updateObjectMappings(refresh);
        checkInvariants();
        return refresh.size();
    }

    public void addToBitSet(final IDT[] objects, final BitSet bitSet) {
        for(final IDT o : objects) {
            final long id = toId(o);
            bitSet.set(Numbers.toIntPosC(id));

            //int index = sequence.getOrAssignIndex(key);
            //bitSet.set(index);
        }
    }

    public void addToBitSet(final long[] objects, final BitSet bitSet) {
        for(final long o : objects) {
            final long idl;
            try {
                 idl = toId((IDT) new Long(o));
            } catch (ClassCastException cce) {
                throw new UnsupportedOperationException("support for non-long object types is incomplete");
            }
            final int id = (int) idl;
            if (idl > id) throw new IllegalStateException();
            if (id <= 0) throw new IllegalStateException();
            bitSet.set(id);

            //int index = sequence.getOrAssignIndex(key);
            //bitSet.set(index);
        }
    }

    public SortedSet<IDT> toOs(final BitSet bitSet) { //}, final Comparator<IDT> idComparator) {
        if (bitSet == null) return null;
        if (bitSet.isEmpty()) return null;
        SortedSet<IDT> set = new ObjectRBTreeSet<IDT>();

        // Note: How to iterate over dynamicbitset documented here:  http://docs.oracle.com/javase/6/docs/api/java/util/BitSet.html#nextSetBit(int)
        for (int pos = bitSet.nextSetBit(0); pos >= 0; pos = bitSet.nextSetBit(pos+1)) {
            //System.out.println("pos=" + pos);
            //final long key = sequence.getKey(pos);
            final IDT key = toO(pos);
            set.add(key);
        }
        return set;
    }

    int objectsCount() {
        return oid.size();
    }

    int idsCount() {
        return ido.size();
    }

}