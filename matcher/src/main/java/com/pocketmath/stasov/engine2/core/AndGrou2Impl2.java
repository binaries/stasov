package com.pocketmath.stasov.engine2.core;

import com.pocketmath.stasov.engine.OpportunityQueryBase;

import java.util.*;

/**
 * Created by etucker on 3/16/16.
 */
public class AndGrou2Impl2 implements AndGrou2 {

    private MatchNod2[] array;
    private int size = 0;
    private boolean optimized = false;

    private Set<String> indexToValue(final int index, final OpportunityQueryBase query) {
        assert(index >= 0);
        assert(query != null);

        return query.getInputStrings(array[index].getAttrTypeId());
    }

    @Override
    public void function(final BitSet bs, final OpportunityQueryBase query) {
        assert(bs != null);
        assert(query != null);
        assert(size <= array.length);

        for (int i = 0; i < size; ++i) {
            final MatchNod2 node = array[i];
            if (node == null) continue;
            final Set<String> values = indexToValue(i, query);
            for (final String v : values)
                node.function(bs, v);
        }
    }

    public synchronized void add(final Set<MatchNod2> nodes) {
        if (array.length - size < nodes.size()) {
            MatchNod2[] newArray = new MatchNod2[0];
        }

        int existingCount = 0;

        for (final MatchNod2 existingNode : array) {
            if (nodes.contains(existingNode))
                existingCount++;
        }

        if (existingCount == nodes.size()) return;

        optimized = false;

        if (array.length - size < nodes.size() - existingCount) {
            // need to grow the array

        }

        // just add them into empty spaces
        int i = 0;
        for (final MatchNod2 node : nodes) {
            if (array[i] == null && Arrays.binarySearch(array, node) < 0) {
                array[i] = node;
                size++;
            }
        }


        /*
        final SortedSet<MatchNod2> sortedSet = new TreeSet<>();
        sortedSet.addAll(sortedSet);
        sortedSet.addAll(Arrays.asList(array));
        array = sortedSet.toArray(new MatchNod2[array.length]);
        */
    }

    public synchronized void remove(final MatchNod2[] nodes) {

        optimized = false;
    }

    private double loadFactor() {
        return ((double)size) / ((double)array.length);
    }

    private void resize(final int newSize) {
        if (newSize < size) throw new IllegalArgumentException();

    }

    public synchronized void optimize() {
        if (optimized) return;
        if (loadFactor() < 0.7d) resize(Math.max(5, (int) (size * 1.1d)));
        Arrays.sort(array);
        optimized = true;
    }

    public int size() {
        assert(Arrays.binarySearch(array, null) >= 0);
        return array.length;
    }

}
