package com.pocketmath.stasov.engine3.core.vesseltree;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.attributes.handler.base.AttributeHandler;
import com.pocketmath.stasov.engine.EngineConfig;
import com.pocketmath.stasov.util.StasovArrays;
import com.pocketmath.stasov.util.Weights;
import com.pocketmath.stasov.util.multimaps.Long2LongMultiValueSortedMap;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 8/16/16.
 */
public class VesselTreeBuilder {

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

    private final Vessel root = new Vessel();

    private final Weights attrWeights = new Weights();

    protected final Tracker tracker;

    private final AttrSvcBase attrSvc;

    private final IdTranslator idTranslator;

    public class AndGroupBuilder {
        private final Long2LongMultiValueSortedMap<Long> inclVals = new Long2LongMultiValueSortedMap<Long>();
        private final Long2LongMultiValueSortedMap<Long> exclVals = new Long2LongMultiValueSortedMap<Long>();
        private final long[] matches;

        public AndGroupBuilder(final long[] matches) {
            this.matches = matches;
        }

        public void addInclusionaryValue(final Object id, final long attrTypeId, final long inclVal, final String inclValString) {
            Objects.requireNonNull(id);
            if (attrTypeId < 1) throw new IllegalArgumentException("attrTypeId=" + attrTypeId + " was less than 1.");
            //if (inclVal < 1) throw new IllegalArgumentException("inclVal=" + inclVal + " was less than 1.");
            inclVals.put(attrTypeId, inclVal);
        }

        public void addExclusionaryValue(final Object id, final long attrTypeId, final long exclVal, final String exclValString) {
            Objects.requireNonNull(id);
            if (attrTypeId < 1) throw new IllegalArgumentException("attrTypeId=" + attrTypeId + " was less than 1.");
            //if (exclVal < 1) throw new IllegalArgumentException("exclVal=" + exclVal + " was less than 1.");
            exclVals.put(attrTypeId, exclVal);
        }

        AndGroup toAndGroup() {
            final AndGroup ag = new AndGroup(matches);
            final LongSet attrTypeIds = new LongOpenHashSet();
            attrTypeIds.addAll(inclVals.getKeys());
            attrTypeIds.addAll(exclVals.getKeys());

            // the following loop is just looking for inconsistent states
            for(final long attrTypeId: attrTypeIds) {
                final LongSortedSet _inclVals = inclVals.getSorted(attrTypeId);
                final LongSortedSet _exclVals = exclVals.getSorted(attrTypeId);
                if (_inclVals == null && _exclVals == null) throw new IllegalStateException(); // TODO: match exception type.
                if (_inclVals == null && _exclVals.isEmpty()) throw new IllegalStateException(); // TODO: match exception type.
                if (_exclVals == null && _inclVals.isEmpty()) throw new IllegalStateException(); // TODO: match exception type.
            }

            return ag;
        }
    }

    public class AndGroup {
        private final SortedSet<AttributeValues> components = new TreeSet<AttributeValues>();
        private final long[] matches;

        AndGroup(final long[] matches) {
            this.matches = Arrays.copyOf(matches, matches.length);
        }

        public void add(final Object id, final long attrTypeId, final LongSortedSet inclVals, final LongSortedSet exclVals) {
            Objects.requireNonNull(id);
            if (attrTypeId < 1) throw new IllegalArgumentException("attrTypeId=" + attrTypeId + " was less than 1.");
            //if (inclVals != null) for (final long inclVal : inclVals) if (inclVal < 1) throw new IllegalArgumentException("inclVal=" + inclVal + " was less than 1.");
            //if (exclVals != null) for (final long exclVal : exclVals) if (exclVal < 1) throw new IllegalArgumentException("exclVal=" + exclVal + " was less than 1.");
            final AttributeValues attrVals = new AttributeValues(id, attrTypeId, inclVals, exclVals);
            components.add(attrVals);
        }

        boolean isEmpty() {
            return components.isEmpty();
        }

        Iterator<AttributeValues> componentsIterator() {
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

    public VesselTreeBuilder(final AttrSvcBase attrSvc, final Tracker tracker, final IdTranslator idTranslator) {
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

        final ObjectSet<Vessel> layerNodes = new ObjectLinkedOpenHashSet<Vessel>();
        layerNodes.add(root);

        final Iterator<AttributeValues> componentsItr = ag.componentsIterator();
        while (componentsItr.hasNext()) {
            final AttributeValues component = componentsItr.next();

            final long attrTypeId = component.getAttrTypeId();
            if (attrTypeId <= 0) throw new IllegalStateException(); // TODO: Refine exception.

            final AttributeHandler handler = attrSvc.lookupHandler(attrTypeId);
            if (handler == null) throw new IllegalStateException(); // TODO: Refine exception.

            final long[] inclVals = component.getInclVals();
            final long[] exclVals = component.getExclVals();

            for (final Vessel node: layerNodes) {
                if (node == null) throw new NullPointerException("node was null");

                Vessel newNextNode = null;
                final Long2ObjectMap<Vessel> nextLayerNodes = new Long2ObjectRBTreeMap<Vessel>();

                if (inclVals != null) for (final long inclVal : inclVals) {

                    //boolean existing = true;
                    Long2ObjectMap<Vessel> possibleExistingNodes = null;
                    //final LongSet nonExisting = new LongRBTreeSet();
                    if (node.inclusionary == null) throw new NullPointerException("node.inclusionary was null");
                    final Set<Vessel> existingNextNodes = node.inclusionary.get(attrTypeId, inclVal);
                    if (existingNextNodes != null) {
                        existingNodesLoop: for (final Vessel existingNextNode : existingNextNodes) {
                            // Great!  We've got inclusion.  Now, look for exclusion ...
                            if (!node.exclusionary.matchesAll(attrTypeId, exclVals, existingNextNode)) {
                                //existing = false;
                                //nonExisting.add(inclVal);
                                break existingNodesLoop;
                            } else {
                                if (possibleExistingNodes == null)
                                    possibleExistingNodes = new Long2ObjectRBTreeMap<Vessel>();
                                possibleExistingNodes.put(inclVal, existingNextNode);
                                final Object id = component.getId();
                                final String input = component.;
                                final Vessel entryPoint = existingNextNode;
                                final long exitPoint = inclVal;
                                if (! (id instanceof Serializable)) throw new UnsupportedOperationException(); // TODO use strong typing
                                if (! (id instanceof Comparable)) throw new UnsupportedOperationException(); // TODO use strong typing
                                handler.afterPrimaryIndex((Serializable)(Comparable)id, input, entryPoint, exitPoint);
                            }
                        }
                    }

                    if (possibleExistingNodes != null) {
                        for (final Map.Entry<Long,Vessel> n : possibleExistingNodes.entrySet()) {

                        }
                        nextLayerNodes.putAll(possibleExistingNodes);
                    } else {
                        if (newNextNode == null) {
                            newNextNode = new Vessel();
                            nextLayerNodes.put(inclVal, newNextNode);
                            //for (final long _inclVal : nonExisting) nextLayerNodes.put(_inclVal, newNextNode);
                        }
                    }
                }

                for (final Long2ObjectMap.Entry<Vessel> entry : nextLayerNodes.long2ObjectEntrySet()) {
                    node.addInclVal(attrTypeId, entry.getLongKey(), entry.getValue());
                    if (exclVals != null) node.addExclVals(attrTypeId, exclVals, entry.getValue());
                }

                layerNodes.clear();
                layerNodes.addAll(nextLayerNodes.values());
            }

        } // end while (componentsItr.hasNext())

        final BitSet onBits = new BitSet();
        //idTranslator.addToBitSet(ag.getMatches(), onBits);
        addToBitSet(ag.getMatches(), onBits);


        //bitSetTranslator.addToBitSet(ag.getMatches(), onBits);

        for (Vessel node: layerNodes) {
            BitSet bitSet = node.getMatches();
            if (bitSet == null) {
                bitSet = new BitSet();
                node.setMatches(bitSet);
            }
            bitSet.or(onBits);
            tracker.setMatches(bitSet, node);
        }
    }

    private class ResultsInclusionConsumer<ResultType extends Serializable & Comparable<ResultType>>
            implements ScoredResultWithMetaConsumer<ResultType,ObjectSet<Vessel>> {

        private ObjectSet<Vessel> set = null;

        @Override
        public void accept(final float score, @Nonnull final ResultType result, @Nonnull final ObjectSet<Vessel> subSet) {
            Objects.requireNonNull(subSet);
            if (score <= 0f) return;
            if (set == null) set = new ObjectOpenHashSet<Vessel>();
            set.addAll(subSet);
        }

        public ObjectSet<Vessel> getSet() {
            return set;
        }
    }

    private class ResultsExclusionConsumer<ResultType extends Serializable & Comparable<ResultType>>
            implements ScoredResultWithMetaConsumer<ResultType,ObjectSet<Vessel>> {

        private ObjectSet<Vessel> set;

        public ResultsExclusionConsumer(final ObjectSet<Vessel> set) {
            this.set = set;
        }

        @Override
        public void accept(final float score, @Nonnull final ResultType result, @Nonnull final ObjectSet<Vessel> subSet) {
            Objects.requireNonNull(subSet);
            if (score <= 0f) return;
            if (set == null) set = new ObjectOpenHashSet<Vessel>();
            set.removeAll(subSet);
        }

        public ObjectSet<Vessel> getSet() {
            return set;
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

        ArrayList<Vessel> layerNodes = new ArrayList<Vessel>();
        ArrayList<Vessel> nextLayerNodes = new ArrayList<Vessel>();
        layerNodes.add(root);

        BitSet bitSet = null;

        while (!layerNodes.isEmpty()) {
            //System.out.println("layerNodes=" + layerNodes);
            for (final Vessel node : layerNodes) {
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
                        System.out.println(queryValue);
                        assert(queryValue >= 0 || queryValue == AttributeHandler.CUSTOM_INDEX || queryValue == AttributeHandler.NOT_FOUND);

                        if (queryValue == AttributeHandler.CUSTOM_INDEX) {
                            final AttributeHandler handler = attrSvc.lookupHandler(attrTypeId);
                            final ResultsInclusionConsumer resultsConsumerInclusion = new ResultsInclusionConsumer();

                            for (final String input : query.getInputStrings(attrTypeId)) {
                                if (logger.isLoggable(Level.FINEST)) logger.log(Level.FINEST, "input = " + input);
                                final Object entryPoint = node;
                                handler.customIndexQueryInclusionary(input, resultsConsumerInclusion, entryPoint);
                            }

                            final ObjectSet set = resultsConsumerInclusion.getSet();
                            if (logger.isLoggable(Level.FINEST)) logger.log(Level.FINEST, "set = " + set);

                            if (set != null && ! set.isEmpty()) {
                                final ResultsExclusionConsumer resultsExclusionConsumer = new ResultsExclusionConsumer(set);

                                for (final String input : query.getInputStrings(attrTypeId)) {
                                    final Object entryPoint = node;
                                    handler.customIndexQueryExclusionary(input, resultsExclusionConsumer, entryPoint);
                                }

                                nextLayerNodes.addAll(set);
                            }

                        } else if (nodeValues.contains(queryValue)) {
                            final ObjectSet<Vessel> inclNextNodes;
                            final ObjectSet<Vessel> exclNextNodes;

                            inclNextNodes = node.getIncl(attrTypeId, queryValue);
                            assert (inclNextNodes != null);
                            assert (!inclNextNodes.isEmpty());

                            exclNextNodes = node.getExcl(attrTypeId, queryValue);

                            final ObjectSet<Vessel> nextNodes = new ObjectOpenHashSet<Vessel>(inclNextNodes);
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

    private class RemoveConsumer implements Consumer<Vessel> {
        private long id = -1l;

        public void setId(final long id) {
            this.id = id;
        }

        @Override
        public void accept(Vessel node) {
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
