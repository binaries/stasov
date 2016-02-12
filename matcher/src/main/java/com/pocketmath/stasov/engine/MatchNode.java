package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttributeHandler;
import com.pocketmath.stasov.attributes.RangeMatchAttributeHandler;
import com.pocketmath.stasov.attributes.ThreeDimensionalRangeMatchAttributeHandler;
import com.pocketmath.stasov.util.multimaps.ILong2Long2ObjectMultiValueMap;
import com.pocketmath.stasov.util.multimaps.Long2Long2ObjectMultiValueHashMap;
import com.pocketmath.stasov.util.multimaps.Long2Long2ObjectMultiValueSortedMap;
import com.pocketmath.stasov.util.IDAllocator;
import com.pocketmath.stasov.util.PrettyPrintable;
import com.pocketmath.stasov.util.IndexAlgorithm;
import com.pocketmath.stasov.util.optimizedrangemap.OptiDoubleRMEntry;
import com.pocketmath.stasov.util.optimizedrangemap.multidimensional.ThreeDimensionalOptiDoubleRangeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by etucker on 2/2/16.
 */
class MatchNode implements Comparable<MatchNode>, PrettyPrintable {

    private static final IDAllocator idAllocator = IDAllocator.newIDAllocatorLongMax();

    private final IndexAlgorithm indexAlgorithm = EngineConfig.getConfig().getPreferredIndexAlgorithm();

    private static final float MATCH_THRESHOLD = 0.5f;

    private Long2ObjectMap<ThreeDimensionalOptiDoubleRangeMap<MatchNode>> attrIdTo3DRanges = null;

    /**
     * use directly with extreme care
     */
    long id;

    /**
     * inclusionary values
     * use directly with extreme care
     */
    final ILong2Long2ObjectMultiValueMap<MatchNode> inclusionary;

    /**
     * exclusionary values
     * use directly with extreme care
     */
    final ILong2Long2ObjectMultiValueMap<MatchNode> exclusionary;

    private BitSet matches;

    private MatchNode(final BitSet matches) {
        inclusionary = newMultiMapMap();
        exclusionary = newMultiMapMap();
        this.id = idAllocator.allocateId();
        this.matches = matches;
    }

    protected  ILong2Long2ObjectMultiValueMap<MatchNode> newMultiMapMap() {
        switch (indexAlgorithm) {
            case HASH: return new Long2Long2ObjectMultiValueHashMap<MatchNode>(IndexAlgorithm.DYNAMIC1);
            case AVL: return new Long2Long2ObjectMultiValueSortedMap<MatchNode>(MatchTree.NODE_COMPARATOR, IndexAlgorithm.AVL);
            case REDBLACK: return new Long2Long2ObjectMultiValueSortedMap<MatchNode>(MatchTree.NODE_COMPARATOR, IndexAlgorithm.REDBLACK);
            default: throw new UnsupportedOperationException();
        }
    }

    public MatchNode() {
        this(null);
    }

    @Override
    protected void finalize() {
        idAllocator.deallocateId(id);
    }

    private static class InstanceMatchConsumer implements Consumer<OptiDoubleRMEntry<MatchNode>> {
        ObjectSet<MatchNode> instanceMatchNodes = null;
        final AttributeHandler handler;
        final double x,y,z;

        public InstanceMatchConsumer(AttributeHandler handler, double x, double y, double z) {
            this.handler = handler;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void accept(OptiDoubleRMEntry<MatchNode> entry) {

            final double px = entry.

            final float score = handler.score3DRange(x,y,z, px, py, pz);
            if (score < MATCH_THRESHOLD) return;

            if (instanceMatchNodes == null) {
                instanceMatchNodes = new ObjectArraySet<>();
            }
            instanceMatchNodes.add(matchNode);
        }

        public ObjectSet<MatchNode> getInstanceMatchNodes() {
            assert(instanceMatchNodes == null ^ !instanceMatchNodes.isEmpty());
            return instanceMatchNodes;
        }
    }
/*
    private static class MDConsumer<T extends Comparable<T>> implements ThreeDimensionalOptiDoubleRangeMap.Consensus3DConsumer<T> {


        @Override
        public boolean match(
                @Nonnull final ThreeDimensionalOptiDoubleRangeMap.MultiDimensionalComponentOptiDoubleRMEntry<T> entry,
                @Nonnegative final int dimension) {

            switch (dimension) {
                case 0 : {
                    final double x = getX();
                    return x >= entry.getX0() && x < entry.getX1();
                }
                case 1 : {
                    final double y = getY();
                    return y >= entry.getX0() && y < entry.getX1();
                }
                case 2 : {
                    final double z = getZ();
                    return z >= entry.getX0() && z < entry.getX1();
                }
            }
        }
    }
    */

    private static class IM3Consumer extends ThreeDimensionalOptiDoubleRangeMap.Consensus3DConsumer<MatchNode> {

        final AttributeHandler handler;

        public IM3Consumer(@Nonnegative int initialDimension, final double x, final double y, final double z, @Nonnull final AttributeHandler handler) {
            super(initialDimension, x, y, z);
            this.handler = handler;
        }

        @Override
        public boolean match(@Nonnull ThreeDimensionalOptiDoubleRangeMap.MultiDimensionalComponentOptiDoubleRMEntry<MatchNode> entry, @Nonnegative int dimension) {
            return handler.score3DRange(getX(), getY(), getZ(), entry.getX0())
        }
    }

    public ObjectSet<MatchNode> calculateInclusionary(final Engine engine, final OpportunityQueryBase query, final long attrId, final long valueId) {
        assert(attrId > 0);
        assert(valueId >= 0 || valueId == AttributeHandler.USE_3D_RANGE_MATCH);

        final ObjectSet<MatchNode> matchNodes = inclusionary.get(attrId, valueId);
        if (valueId == AttributeHandler.USE_3D_RANGE_MATCH) {
            final AttributeHandler handler = engine.getAttrSvc().lookupHandler(attrId);

            if (attrIdTo3DRanges == null) return null;
            final ThreeDimensionalOptiDoubleRangeMap<MatchNode> rmap = attrIdTo3DRanges.get(attrId);
            if (rmap == null) return null;

            final Set<String> inputs = query.getInputStrings(attrId);
            if (inputs == null) throw new IllegalStateException("no input found! (null)");
            if (inputs.isEmpty()) throw new IllegalStateException("no input found! (empty)");

            assert (handler instanceof RangeMatchAttributeHandler);
            final double[] point = handler.findRangedPoints(input);

            final ThreeDimensionalOptiDoubleRangeMap.ConsensusConsumer<MatchNode> consumer =
                    new ThreeDimensionalOptiDoubleRangeMap.ConsensusConsumer<MatchNode>() {
                        @Override
                        public boolean match(@Nonnull ThreeDimensionalOptiDoubleRangeMap.MultiDimensionalComponentOptiDoubleRMEntry<MatchNode> entry, @Nonnegative int dimension) {
                            return true;
                        }
                    };

            final double x = point[0], y = point[1], z = point[2];


            final ObjectSet<ThreeDimensionalOptiDoubleRangeMap.MultiDimensionalComponentOptiDoubleRMEntry<MatchNode>> candidates =
                    consumer.getMatches();

            for (ThreeDimensionalOptiDoubleRangeMap.MultiDimensionalComponentOptiDoubleRMEntry<MatchNode> candidate : candidates) {
                handler.score3DRange(x, y, z, candidate.)
            }

            final InstanceMatchConsumer instConsumer = new InstanceMatchConsumer();

            for (final String input : inputs) {

                if (point != null) {
                    assert (point.length == 3);
                    final double x = point[0], y = point[1], z = point[2];

                    rmap.forEach(x, y, z, instConsumer);
                }
            }

            return instConsumer.getInstanceMatchNodes();
        } else {
            return matchNodes;
        }

//       throw new UnsupportedOperationException("fuzzy implementation not complete");
    }

    public void setMatches(final BitSet matches) {
        this.matches = matches;
    }

    public BitSet getMatches() {
        return matches;
    }

    void addInclVals(final long attrTypeId, final long[] inclVals, final MatchNode child) {
        inclusionary.put(attrTypeId, inclVals, child);
    }

    void addInclVal(final long attrTypeId, final long inclVal, final MatchNode child) {
        inclusionary.put(attrTypeId, inclVal, child);
    }

    void addExclVals(final long attrTypeId, final long[] exclVals, final MatchNode child) {
        exclusionary.put(attrTypeId, exclVals, child);
    }

    public void addExclVal(final long attrTypeId, final long exclVal, final MatchNode child) {
        exclusionary.put(attrTypeId, exclVal, child);
    }

    public int compareTo(MatchNode o) {
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
        w.println(t + "Node{");
        w.println(t + "id=" + id);
        w.println(t + "inclusionary=");
        w.println(t + inclusionary.prettyPrint(t + "  ", "typeId  ", "valueId ", "|       "));
        w.println(t + "exclusionary=");
        w.println(t + exclusionary.prettyPrint(t + "  ", "typeId  ", "valueId ", "|       "));
        //if (matches != null) w.println(t+"matches.cardinality()=" + matches.cardinality());
        if (matches != null) w.println(t + "matches=" + matches);
        w.println(t + "}");

        return sw.toString();
    }
}
