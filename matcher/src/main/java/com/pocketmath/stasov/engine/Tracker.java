package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.util.Long2ObjectMultiValueMap;

import java.util.BitSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by etucker on 1/23/16.
 */
class Tracker {

    private final Long2ObjectMultiValueMap<MatchTree.Node> activeIds =
            new Long2ObjectMultiValueMap<MatchTree.Node>(MatchTree.NODE_COMPARATOR);

    private final Set<MatchTree.Node> allNodes = new TreeSet<MatchTree.Node>(MatchTree.NODE_COMPARATOR);

    public void associate(final long id, final MatchTree.Node node) {
        activeIds.put(id, node);
    }

    public void disassociate(final long id, final MatchTree.Node node) {
        activeIds.remove(id, node);
    }

    public void setMatches(final BitSet matchIds, final MatchTree.Node node) {
        for (int i = matchIds.nextSetBit(0); i >= 0; i = matchIds.nextSetBit(i+1)) {
            // operate on index i here
            associate((long)i, node);

            if (i == Integer.MAX_VALUE) break; // or (i+1) would overflow
        }
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

    public Set<MatchTree.Node> allNodes() {
        return allNodes;
    }

}
