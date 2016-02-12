package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.attributes.AttributeHandler;
import com.pocketmath.stasov.util.*;
import com.pocketmath.stasov.util.multimaps.Long2LongMultiValueSortedMap;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 3/16/15.
 */
class MatchTree {

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

    static class NodeComparator implements Comparator<MatchNode> {

        public int compare(MatchNode o1, MatchNode o2) {
            return Long.compare(o1.id, o2.id);
        }
    }

    static final Comparator NODE_COMPARATOR = new NodeComparator();

    private final MatchNode root = new MatchNode();

    private final Weights attrWeights = new Weights();

    protected final Tracker tracker;

    private final AttrSvcBase attrSvc;

    private final IdTranslator idTranslator;
/*
    static class BitSetTranslator0 {
        private final Seq sequence = new Seq(TreeAlgorithm.AVL);
        public void addToBitSet(final long[] keys, final BitSet bitSet) {
            for(final long key : keys) {
                int index = sequence.getOrAssignIndex(key);
                bitSet.set(index);
            }
        }
        public LongSortedSet toIds(final BitSet bitSet, final LongComparator idComparator) {
            if (bitSet == null) return null;
            if (bitSet.isEmpty()) return null;
            LongSortedSet set = new LongRBTreeSet(idComparator);

            // Note: How to iterate over bitset documented here:  http://docs.oracle.com/javase/6/docs/api/java/util/BitSet.html#nextSetBit(int)
            for (int pos = bitSet.nextSetBit(0); pos >= 0; pos = bitSet.nextSetBit(pos+1)) {
                //System.out.println("pos=" + pos);
                final long key = sequence.getKey(pos);
                set.add(key);
            }
            return set;
        }
    }
*/

    class AttrVals implements Comparable<AttrVals> {
        final long attrTypeId;
        final long[] inclVals;
        final long[] exclVals;

        AttrVals(final long attrTypeId, final LongSortedSet inclVals, final LongSortedSet exclVals) {
            this.attrTypeId = attrTypeId;
            this.inclVals = inclVals == null ? null : inclVals.toLongArray();
            this.exclVals = exclVals == null ? null : exclVals.toLongArray();
        }

        public int compareTo(final AttrVals o) {
            assert(o != null);

            int r = attrSvc.getAttrsComparator().compare(this.attrTypeId, o.attrTypeId);
            if (r != 0) return r;

            if (inclVals == null && o.inclVals != null) return -1;
            if (inclVals != null && o.inclVals == null) return +1;
            boolean inclValsEqual = inclVals == null && o.inclVals == null;
            if (!inclValsEqual) {
                if (this.inclVals.length <= o.inclVals.length) {
                    for (int i = 0; i < this.inclVals.length; i++) {
                        r = Long.compare(this.inclVals[i], o.inclVals[i]);
                        if (r != 0) return r;
                    }
                    if (this.inclVals.length == o.inclVals.length) inclValsEqual = true;
                } else {
                    for (int i = 0; i < o.inclVals.length; i++) {
                        r = Long.compare(o.inclVals[i], this.inclVals[i]);
                        if (r != 0) return r;
                    }
                }
            }

            if (exclVals == null && o.exclVals != null) return -1;
            if (exclVals != null && o.exclVals == null) return +1;
            boolean exclValsEqual = exclVals == null && o.exclVals == null;
            if (!exclValsEqual) {
                if (this.exclVals.length <= o.exclVals.length) {
                    for (int i = 0; i < this.exclVals.length; i++) {
                        r = Long.compare(this.exclVals[i], o.exclVals[i]);
                        if (r != 0) return r;
                    }
                    if (this.exclVals.length == o.exclVals.length) exclValsEqual = true;
                } else {
                    for (int i = 0; i < o.exclVals.length; i++) {
                        r = Long.compare(o.exclVals[i], this.exclVals[i]);
                        if (r != 0) return r;
                    }
                }
            }

            if (inclValsEqual && exclValsEqual) {
                return 0;
            }

            // return the longer one if not equal
            if (!inclValsEqual) {
                r = Integer.compare(this.inclVals.length, o.inclVals.length);
                if (r != 0) return r;
            }

            if (!exclValsEqual) {
                r = Integer.compare(this.exclVals.length, o.exclVals.length);
                if (r != 0) return r;  // r will be != 0 or there is a value in the code
            }

            // solution is totally solved; reaching this point means something is faulty in the code
            throw new IllegalStateException();
        }

        public long getAttrTypeId() {
            return attrTypeId;
        }

        public long[] getInclVals() {
            return inclVals;
        }

        public long[] getExclVals() {
            return exclVals;
        }

        @Override
        public String toString() {
            return "AttrVals{" +
                    "attrTypeId=" + attrTypeId +
                    ", inclVals=" + Arrays.toString(inclVals) +
                    ", exclVals=" + Arrays.toString(exclVals) +
                    '}';
        }
    }

    public class AndGroupBuilder {
        private final Long2LongMultiValueSortedMap<Long> inclVals = new Long2LongMultiValueSortedMap<Long>();
        private final Long2LongMultiValueSortedMap<Long> exclVals = new Long2LongMultiValueSortedMap<Long>();
        private final long[] matches;
        public AndGroupBuilder(final long[] matches) {
            this.matches = matches;
        }

        public void addInclusionaryValue(final long attrTypeId, final long inclVal) {
            if (attrTypeId < 1) throw new IllegalArgumentException("attrTypeId=" + attrTypeId + " was less than 1.");
            if (inclVal < 1) throw new IllegalArgumentException("inclVal=" + inclVal + " was less than 1.");
            inclVals.put(attrTypeId, inclVal);
        }

        public void addExclusionaryValue(final long attrTypeId, final long exclVal) {
            if (attrTypeId < 1) throw new IllegalArgumentException("attrTypeId=" + attrTypeId + " was less than 1.");
            if (exclVal < 1) throw new IllegalArgumentException("exclVal=" + exclVal + " was less than 1.");
            exclVals.put(attrTypeId, exclVal);
        }

        AndGroup toAndGroup() {
            final AndGroup ag = new AndGroup(matches);
            final LongSet attrTypeIds = new LongOpenHashSet();
            attrTypeIds.addAll(inclVals.getKeys());
            attrTypeIds.addAll(exclVals.getKeys());
            for(final long attrTypeId: attrTypeIds) {
                final LongSortedSet _inclVals = inclVals.getSorted(attrTypeId);
                final LongSortedSet _exclVals = exclVals.getSorted(attrTypeId);
                if (_inclVals == null && _exclVals == null) throw new IllegalStateException(); // TODO: match exception type.
                if (_inclVals == null && _exclVals.isEmpty()) throw new IllegalStateException(); // TODO: match exception type.
                if (_exclVals == null && _inclVals.isEmpty()) throw new IllegalStateException(); // TODO: match exception type.
                ag.add(attrTypeId, _inclVals, _exclVals);
            }
            return ag;
        }
    }

    public class AndGroup {
        private final SortedSet<AttrVals> components = new TreeSet<AttrVals>();
        private final long[] matches;

        AndGroup(final long[] matches) {
            this.matches = Arrays.copyOf(matches, matches.length);
        }

        public void add(final long attrTypeId, final LongSortedSet inclVals, final LongSortedSet exclVals) {
            if (attrTypeId < 1) throw new IllegalArgumentException("attrTypeId=" + attrTypeId + " was less than 1.");
            if (inclVals != null) for (final long inclVal : inclVals) if (inclVal < 1) throw new IllegalArgumentException("inclVal=" + inclVal + " was less than 1.");
            if (exclVals != null) for (final long exclVal : exclVals) if (exclVal < 1) throw new IllegalArgumentException("exclVal=" + exclVal + " was less than 1.");
            final AttrVals attrVals = new AttrVals(attrTypeId, inclVals, exclVals);
            components.add(attrVals);
        }

        boolean isEmpty() {
            return components.isEmpty();
        }

        Iterator<AttrVals> componentsIterator() {
            return components.iterator();
        }

        long[] getMatches() {
            return matches;
        }

        @Override
        public String toString() {
            return "AndGroup{" +
                    "components=" + components +
                    ", matches=" + Arrays.toString(matches) +
                    '}';
        }
    }

    public MatchTree(final AttrSvcBase attrSvc, final Tracker tracker, final IdTranslator idTranslator) {
        if (attrSvc == null) throw new IllegalArgumentException();
        if (tracker == null) throw new IllegalArgumentException();
        if (idTranslator == null) throw new IllegalArgumentException();

        this.attrSvc = attrSvc;
        this.tracker = tracker;
        this.idTranslator = idTranslator;
    }

    private void addToBitSet(final long[] ids, final BitSet bitSet) {
        for(final long idl : ids) {

            final int id = (int) idl;
            if (idl > id) throw new IllegalStateException();
            if (id <= 0) throw new IllegalStateException();

            bitSet.set(id);
        }
    }

    public AndGroupBuilder newAndGroupBuilder(final long[] matches) {
        return new AndGroupBuilder(matches);
    }

    public void addAndGroup(final AndGroupBuilder agb) {
        final AndGroup ag = agb.toAndGroup();
        addAndGroup(ag);
    }

    void addAndGroup(final AndGroup ag) {
        if (ag == null) throw new IllegalArgumentException("ag was null");
        if (ag.isEmpty()) throw new IllegalArgumentException("attempting to add empty AndGroup");

        logger.log(Level.FINEST, "adding AndGroup: " + ag);

        final ObjectSet<MatchNode> layerNodes = new ObjectLinkedOpenHashSet<MatchNode>();
        layerNodes.add(root);

        final Iterator<AttrVals> componentsItr = ag.componentsIterator();
        while (componentsItr.hasNext()) {
            final AttrVals component = componentsItr.next();

            final long attrTypeId = component.getAttrTypeId();
            if (attrTypeId <= 0) throw new IllegalStateException(); // TODO: Refine exception.

            final long[] inclVals = component.getInclVals();
            final long[] exclVals = component.getExclVals();

            for (final MatchNode node: layerNodes) {
                if (node == null) throw new NullPointerException("node was null");

                MatchNode newNextNode = null;
                final Long2ObjectMap<MatchNode> nextLayerNodes = new Long2ObjectRBTreeMap<MatchNode>();

                if (inclVals != null) for (final long inclVal : inclVals) {

                    //boolean existing = true;
                    Long2ObjectMap<MatchNode> possibleExistingNodes = null;
                    //final LongSet nonExisting = new LongRBTreeSet();
                    if (node.inclusionary == null) throw new NullPointerException("node.inclusionary was null");
                    final Set<MatchNode> existingNextNodes = node.inclusionary.get(attrTypeId, inclVal);
                    if (existingNextNodes != null) {
                        existingNodesLoop: for (final MatchNode existingNextNode : existingNextNodes) {
                            // Great!  We've got inclusion.  Now, look for exclusion ...
                            if (!node.exclusionary.matchesAll(attrTypeId, exclVals, existingNextNode)) {
                                //existing = false;
                                //nonExisting.add(inclVal);
                                break existingNodesLoop;
                            } else {
                                if (possibleExistingNodes == null)
                                    possibleExistingNodes = new Long2ObjectRBTreeMap<MatchNode>();
                                possibleExistingNodes.put(inclVal, existingNextNode);
                            }
                        }
                    }

                    if (possibleExistingNodes != null) {
                        nextLayerNodes.putAll(possibleExistingNodes);
                    } else {
                        if (newNextNode == null) {
                            newNextNode = new MatchNode();
                            nextLayerNodes.put(inclVal, newNextNode);
                            //for (final long _inclVal : nonExisting) nextLayerNodes.put(_inclVal, newNextNode);
                        }
                    }
                }

                for (final Long2ObjectMap.Entry<MatchNode> entry : nextLayerNodes.long2ObjectEntrySet()) {
                    node.inclusionary.put(attrTypeId, entry.getLongKey(), entry.getValue());
                    if (exclVals != null) node.exclusionary.put(attrTypeId, exclVals, entry.getValue());
                }

                layerNodes.clear();
                layerNodes.addAll(nextLayerNodes.values());
            }

        } // end while (componentsItr.hasNext())

        final BitSet onBits = new BitSet();
        //idTranslator.addToBitSet(ag.getMatches(), onBits);
        addToBitSet(ag.getMatches(), onBits);


        //bitSetTranslator.addToBitSet(ag.getMatches(), onBits);

        for (MatchNode node: layerNodes) {
            BitSet bitSet = node.getMatches();
            if (bitSet == null) {
                bitSet = new BitSet();
                node.setMatches(bitSet);
            }
            bitSet.or(onBits);
            tracker.setMatches(bitSet, node);
        }
    }

    /**
     *
     * @param query index 0 is attrTypeId, index 1 is value
     * @return
     */
    public SortedSet query(final OpportunityQueryBase query) {
        // step 1: multiValueMap attrs into sort order by weights
        final long[] attrTypeIds = attrSvc.getAttrTypeIds();
        assert(StasovArrays.isSorted(attrTypeIds, attrSvc.getAttrsComparator()));

        ArrayList<MatchNode> layerNodes = new ArrayList<MatchNode>();
        ArrayList<MatchNode> nextLayerNodes = new ArrayList<MatchNode>();
        layerNodes.add(root);

        BitSet bitSet = null;

        while (!layerNodes.isEmpty()) {
            //System.out.println("layerNodes=" + layerNodes);
            for (MatchNode node : layerNodes) {
                final BitSet matches = node.getMatches();
                if (matches != null) {
                    if (bitSet == null) bitSet = new BitSet();
                    bitSet.or(matches);
                }
                for (final long attrTypeId : attrTypeIds) {
                    final LongSet nodeValues = node.inclusionary.getKeys2(attrTypeId);
                    if (nodeValues == null) continue;
                    final LongSet queryValues = query.translateValues(attrTypeId); // multiValueMap.getSorted(attrTypeId);
                    if (queryValues == null) continue;

                    for (final long queryValue : queryValues) {
                        assert(queryValue >= 0 || queryValue == AttributeHandler.USE_3D_RANGE_MATCH || queryValue == AttributeHandler.NOT_FOUND);
                        final ObjectSet<MatchNode> inclNextNodes;
                        final ObjectSet<MatchNode> exclNextNodes;

                        if (queryValue == AttributeHandler.USE_3D_RANGE_MATCH) {
                            inclNextNodes = null;
                            exclNextNodes = null;

                        } else if (nodeValues.contains(queryValue)) {
                            inclNextNodes = node.inclusionary.get(attrTypeId, queryValue);
                            assert (inclNextNodes != null);
                            assert (!inclNextNodes.isEmpty());

                            exclNextNodes = node.exclusionary.get(attrTypeId, queryValue);

                            final ObjectSet<MatchNode> nextNodes = new ObjectOpenHashSet<MatchNode>(inclNextNodes);
                            if (exclNextNodes != null) nextNodes.removeAll(exclNextNodes);

                            nextLayerNodes.addAll(nextNodes);
                        }
                    }
                }
            }
            layerNodes.clear();
            ArrayList nextNextLayerNodes = layerNodes;
            layerNodes = nextLayerNodes;
            nextLayerNodes = nextNextLayerNodes;
        }

        if (bitSet == null) return null;
        //final LongSortedSet ids = bitSetTranslator.toIds(bitSet, LongComparators.NATURAL_COMPARATOR);

        final SortedSet ids = idTranslator.toOs(bitSet);
        return ids;
    }

   private class RemoveConsumer implements Consumer<MatchNode> {
        private long id = -1l;

        public void setId(final long id) {
            this.id = id;
        }

        @Override
        public void accept(MatchNode node) {
            final BitSet matches = node.getMatches();
            matches.clear((int)id);
        }
    };

    private final RemoveConsumer REMOVE_CONSUMER = new RemoveConsumer();

    public void remove(final long id) {
        if (!tracker.inUse(id))
            throw new IllegalStateException("attempt to remove an id not in use; (internal) id=" + id);
        REMOVE_CONSUMER.setId(id);
        tracker.operateOnNodes(id, REMOVE_CONSUMER);
    }

    @Override
    public String toString() {
        return "com.pocketmath.stasov.engine.MatchTree{" +
                "root=" + root +
                ", attrWeights=" + attrWeights +
                ", idTranslator=" + idTranslator +
                '}';
    }

    public String prettyPrint() {
        return root.prettyPrint("");
    }

}
