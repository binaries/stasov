package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.util.multimaps.Long2ObjectMultiValueSortedMap;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.BitSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * Created by etucker on 1/23/16.
 */
class Tracker {

    private final Long2ObjectMultiValueSortedMap<MatchNode> activeIds =
            new Long2ObjectMultiValueSortedMap<MatchNode>(MatchTree.NODE_COMPARATOR);

    private final Set<MatchNode> allNodes = new TreeSet<MatchNode>(MatchTree.NODE_COMPARATOR);

    public void associate(@Nonnegative final long id, @Nonnull final MatchNode node) {
        if (id < 1) throw new IllegalArgumentException();
        if (node == null) throw new IllegalArgumentException();
        activeIds.put(id, node);
        allNodes.add(node);
    }

    public void disassociate(@Nonnegative final long id, @Nonnull final MatchNode node) {
        if (id < 1) throw new IllegalArgumentException();
        if (node == null) throw new IllegalArgumentException();
        activeIds.remove(id, node);
    }

    public void diassociate(@Nonnegative final long id) {
        if (id < 1) throw new IllegalArgumentException();
        activeIds.remove(id);
    }

    public void setMatches(final BitSet matchIds, final MatchNode node) {
        for (int i = matchIds.nextSetBit(0); i >= 0; i = matchIds.nextSetBit(i+1)) {
            // operate on index i here
            associate((long)i, node);

            if (i == Integer.MAX_VALUE) break; // or (i+1) would overflow
        }
        allNodes.add(node);
    }

    public void operateOnNodes(final long id, Consumer<MatchNode> consumer) {
        final ObjectSortedSet<MatchNode> nodes = activeIds.getSorted(id);
        nodes.forEach(consumer);
    }

    public boolean inUse(final long id) {
        return activeIds.occurrences(id) != 0;
    }

    private boolean allInUse() {
        for (long id : activeIds()) {
            if (!inUse(id)) return false;
        }
        return true;
    }

    public Set<Long> activeIds() {
        assert(allInUse());
        return activeIds.getKeys();
    }

    public Set<MatchNode> allNodes() {
        return allNodes;
    }

    @Override
    public String toString() {
        return "Tracker{" +
                "activeIds=" + activeIds +
                ", allNodes=" + allNodes +
                '}';
    }

}
