package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.util.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import java.io.PrintWriter;
import java.io.StringWriter;
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

    static class NodeComparator implements Comparator<Node> {

        public int compare(Node o1, Node o2) {
            return Long.compare(o1.id, o2.id);
        }
    }

    static final Comparator NODE_COMPARATOR = new NodeComparator();

    static class Node implements Comparable<Node>, PrettyPrintable {

        private static volatile IDAllocator idAllocator = IDAllocator.newIDAllocatorLongMax();

        private long id;

        /**
         * inclusionary values
         */
        final private DualLong2ObjectMultiValueMap<Node> inclusionary =
                new DualLong2ObjectMultiValueMap<Node>(NODE_COMPARATOR, TreeAlgorithm.AVL);

        /**
         * exclusionary values
         */
        final private DualLong2ObjectMultiValueMap<Node> exclusionary =
                new DualLong2ObjectMultiValueMap<Node>(NODE_COMPARATOR, TreeAlgorithm.AVL);

        private BitSet matches;

        private Node(final BitSet matches) {
            this.id = idAllocator.allocateId();
            this.matches = matches;
        }

        public Node() {
            this(null);
        }

        @Override
        protected void finalize() {
            idAllocator.deallocateId(id);
        }

        public void setMatches(final BitSet matches) {
            this.matches = matches;
        }

        public BitSet getMatches() {
            return matches;
        }

        void addInclVals(final long attrTypeId, final long[] inclVals, final Node child) {
            inclusionary.put(attrTypeId, inclVals, child);
        }

        void addInclVal(final long attrTypeId, final long inclVal, final Node child) {
            inclusionary.put(attrTypeId, inclVal, child);
        }

        void addExclVals(final long attrTypeId, final long[] exclVals, final Node child) {
            exclusionary.put(attrTypeId, exclVals, child);
        }

        public void addExclVal(final long attrTypeId, final long exclVal, final Node child) {
            exclusionary.put(attrTypeId, exclVal, child);
        }

        public int compareTo(Node o) {
            return Long.compare(id, o.id);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "id=" + id +
                    ", inclusionary=" + (inclusionary.isEmpty() ? "EMPTY" : inclusionary) +
                    ", exclusionary=" + (exclusionary.isEmpty() ? "EMPTY" : exclusionary) +
                    ", matches=" + matches +
                    '}';
        }

        public String prettyPrint(String prefix) {
            StringWriter sw = new StringWriter();
            PrintWriter w = new PrintWriter(sw);

            final String t = prefix;

            w.println();
            w.println(t+"Node{");
            w.println(t+"id=" + id );
            w.println(t+"inclusionary=");
            w.println(t+inclusionary.prettyPrint(t + "  ", "typeId  ", "valueId ", "|       "));
            w.println(t+"exclusionary=");
            w.println(t+exclusionary.prettyPrint(t + "  ", "typeId  ", "valueId ", "|       "));
            //if (matches != null) w.println(t+"matches.cardinality()=" + matches.cardinality());
            if (matches != null) w.println(t+"matches=" + matches);
            w.println(t+"}");

            return sw.toString();
        }
    }

    private final Node root = new Node();

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
        private final Long2LongMultiValueMap<Long> inclVals = new Long2LongMultiValueMap<Long>();
        private final Long2LongMultiValueMap<Long> exclVals = new Long2LongMultiValueMap<Long>();
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
                final LongSortedSet _inclVals = inclVals.get(attrTypeId);
                final LongSortedSet _exclVals = exclVals.get(attrTypeId);
                if (_inclVals == null && _exclVals == null) throw new IllegalStateException(); // TODO: refine exception type.
                if (_inclVals == null && _exclVals.isEmpty()) throw new IllegalStateException(); // TODO: refine exception type.
                if (_exclVals == null && _inclVals.isEmpty()) throw new IllegalStateException(); // TODO: refine exception type.
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

        final ObjectSet<Node> layerNodes = new ObjectLinkedOpenHashSet<Node>();
        layerNodes.add(root);

        final Iterator<AttrVals> componentsItr = ag.componentsIterator();
        while (componentsItr.hasNext()) {
            final AttrVals component = componentsItr.next();

            final long attrTypeId = component.getAttrTypeId();
            if (attrTypeId <= 0) throw new IllegalStateException(); // TODO: Refine exception.

            final long[] inclVals = component.getInclVals();
            final long[] exclVals = component.getExclVals();

            for (final Node node: layerNodes) {
                if (node == null) throw new NullPointerException("node was null");

                Node newNextNode = null;
                final Long2ObjectMap<Node> nextLayerNodes = new Long2ObjectRBTreeMap<Node>();

                if (inclVals != null) for (final long inclVal : inclVals) {

                    //boolean existing = true;
                    Long2ObjectMap<Node> possibleExistingNodes = null;
                    //final LongSet nonExisting = new LongRBTreeSet();
                    if (node.inclusionary == null) throw new NullPointerException("node.inclusionary was null");
                    final SortedSet<Node> existingNextNodes = node.inclusionary.get(attrTypeId, inclVal);
                    if (existingNextNodes != null) {
                        existingNodesLoop: for (final Node existingNextNode : existingNextNodes) {
                            // Great!  We've got inclusion.  Now, look for exclusion ...
                            if (!node.exclusionary.matchesAll(attrTypeId, exclVals, existingNextNode)) {
                                //existing = false;
                                //nonExisting.add(inclVal);
                                break existingNodesLoop;
                            } else {
                                if (possibleExistingNodes == null)
                                    possibleExistingNodes = new Long2ObjectRBTreeMap<Node>();
                                possibleExistingNodes.put(inclVal, existingNextNode);
                            }
                        }
                    }

                    if (possibleExistingNodes != null) {
                        nextLayerNodes.putAll(possibleExistingNodes);
                    } else {
                        if (newNextNode == null) {
                            newNextNode = new Node();
                            nextLayerNodes.put(inclVal, newNextNode);
                            //for (final long _inclVal : nonExisting) nextLayerNodes.put(_inclVal, newNextNode);
                        }
                    }
                }

                for (final Long2ObjectMap.Entry<Node> entry : nextLayerNodes.long2ObjectEntrySet()) {
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

        for (Node node: layerNodes) {
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

        ArrayList<Node> layerNodes = new ArrayList<Node>();
        ArrayList<Node> nextLayerNodes = new ArrayList<Node>();
        layerNodes.add(root);

        BitSet bitSet = null;

        while (!layerNodes.isEmpty()) {
            //System.out.println("layerNodes=" + layerNodes);
            for (Node node : layerNodes) {
                final BitSet matches = node.getMatches();
                if (matches != null) {
                    if (bitSet == null) bitSet = new BitSet();
                    bitSet.or(matches);
                }
                for (final long attrTypeId : attrTypeIds) {
                    final LongSet nodeValues = node.inclusionary.getKeys2(attrTypeId);
                    if (nodeValues == null) continue;
                    final LongSet queryValues = query.translateValues(attrTypeId); // multiValueMap.get(attrTypeId);
                    if (queryValues == null) continue;
                    for (final long queryValue : queryValues) {
                        if (nodeValues.contains(queryValue)) {
                            final ObjectSortedSet<Node> inclNextNodes = node.inclusionary.get(attrTypeId, queryValue);
                            assert (inclNextNodes != null);
                            assert (!inclNextNodes.isEmpty());

                            final ObjectSortedSet<Node> exclNextNodes = node.exclusionary.get(attrTypeId, queryValue);

                            final ObjectSet<Node> nextNodes = new ObjectOpenHashSet<Node>(inclNextNodes);
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

   private class RemoveConsumer implements Consumer<MatchTree.Node> {
        private long id = -1l;

        public void setId(final long id) {
            this.id = id;
        }

        @Override
        public void accept(MatchTree.Node node) {
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
