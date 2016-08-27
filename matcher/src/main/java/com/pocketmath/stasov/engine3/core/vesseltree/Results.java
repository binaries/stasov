package com.pocketmath.stasov.engine3.core.vesseltree;

import com.pocketmath.stasov.util.IResettable;
import com.pocketmath.stasov.util.dynamicbitset.SBS3;
import com.pocketmath.stasov.util.dynamicbitset.SBS3Iterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

/**
 * Created by etucker on 8/27/16.
 */
public class Results implements IResettable {
    private static final int MAX_CAPACITY_PER_COLLECTION = 1024;
    private static final int TRIM_FREQUENCY = 1024;

    private int countSinceTrim = 0;

    private ObjectLinkedOpenHashSet<Vessel> collection1 = new ObjectLinkedOpenHashSet<>(), collection2 = new ObjectLinkedOpenHashSet<>();
    private SBS3 matchesObject = new SBS3();
    private SBS3Iterator matchesObjectIterator = SBS3.reusableIterator();

    void setRoot(final Vessel root) {
        assert root != null;
        assert collection1.isEmpty();
        collection1.add(root);
    }

    void swap() {
        ObjectLinkedOpenHashSet<Vessel> t = collection1;
        collection1 = collection2;
        collection2 = t;
    }

    boolean collection1Empty() {
        return collection1.isEmpty();
    }

    SBS3 getMatchesObject() {
        return matchesObject;
    }

    ObjectLinkedOpenHashSet<Vessel> getCollection1() {
        return collection1;
    }

    ObjectLinkedOpenHashSet<Vessel> getCollection2() {
        return collection2;
    }

    @Override
    public void reset() {
        assert countSinceTrim >= 0;
        collection1.clear();
        collection2.clear();
        matchesObject.clearAndOptimizeForReuse();
        if (++countSinceTrim >= TRIM_FREQUENCY) {
            countSinceTrim = 0;
            collection1.trim(MAX_CAPACITY_PER_COLLECTION);
            collection2.trim(MAX_CAPACITY_PER_COLLECTION);
        }
        assert countSinceTrim >= 0;
        assert countSinceTrim <= TRIM_FREQUENCY;
    }

    public LongIterator matchesIterator() {
        assert matchesObject != null;
        return matchesObject.positionsIterator(matchesObjectIterator);
    }
}
