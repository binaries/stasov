package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttributeHandler;
import com.pocketmath.stasov.util.multimaps.ILong2Long2ObjectMultiValueMap;
import com.pocketmath.stasov.util.multimaps.Long2Long2ObjectMultiValueHashMap;
import com.pocketmath.stasov.util.multimaps.Long2Long2ObjectMultiValueSortedMap;
import com.pocketmath.stasov.util.IDAllocator;
import com.pocketmath.stasov.util.PrettyPrintable;
import com.pocketmath.stasov.util.IndexAlgorithm;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.BitSet;

/**
 * Created by etucker on 2/2/16.
 */
class MatchNode implements Comparable<MatchNode>, PrettyPrintable {

    private static volatile IDAllocator idAllocator = IDAllocator.newIDAllocatorLongMax();

    private final IndexAlgorithm indexAlgorithm = EngineConfig.getConfig().getPreferredIndexAlgorithm();

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
            case HASH: return new Long2Long2ObjectMultiValueHashMap<MatchNode>();
            case AVL: return new Long2Long2ObjectMultiValueSortedMap<MatchNode>(MatchTree.NODE_COMPARATOR, IndexAlgorithm.AVL);
            case REDBLACK: return new Long2Long2ObjectMultiValueSortedMap<MatchNode>(MatchTree.NODE_COMPARATOR, IndexAlgorithm.REDBLACK);
            default: throw new IllegalStateException();
        }
    }

    public MatchNode() {
        this(null);
    }

    @Override
    protected void finalize() {
        idAllocator.deallocateId(id);
    }

    public ObjectSortedSet<MatchNode> calculateInclusionary(final long attrId, final long valueId) {
        assert(attrId > 0);
        assert(valueId >= 0 || valueId == AttributeHandler.USE_FUZZY_MATCH);

       /* final ObjectSortedSet<MatchNode> matchNodes = inclusionary.get(attrId, valueId);
        if (valueId == AttributeHandler.USE_FUZZY_MATCH) {
            final AttributeHandler handler = null;
            final String input = null;
            assert(handler instanceof FuzzyMatchAttributeHandler);
            final float[] values = handler.convert(input);
            final float score = handler.score(values);
//            if (score > MATCH_THRESHOLD) {

//            }

            throw new UnsupportedOperationException("fuzzy implementation not complete");
        } else {
            return matchNodes;
        }*/
        return null;
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
