package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Collection;

/**
 * A hybrid structure to enable very fast direct array access.  with direct array access comes great responsibility --
 * don't modify the array!!!
 *
 * Created by etucker on 3/20/15.
 */
public class TransparentLongArraySet {

    public long[] array;

    private final LongSet set;

    public TransparentLongArraySet(final Collection<Long> collection) {
        this.set = new LongRBTreeSet(collection);
        update();
    }

    public TransparentLongArraySet(final long[] array) {
        this.set = new LongRBTreeSet(array);
        update();
    }

    public void addAndUpdate(final long[] items) {
        for (final long item : items) {
            add(item);
        }
        update();
    }

    public void addAndUpdate(final long item) {
        add(item);
        update();
    }

    public boolean contains(final long item) {
        return set.contains(item);
    }

    public void clearAndUpdate() {
        set.clear();
        update();
    }

    protected void add(final long item) {
        set.add(item);
    }

    protected void update() {
        array = set.toLongArray();
    }

}
