package com.pocketmath.stasov.engine2.core;

import java.util.Objects;

/**
 * Created by etucker on 3/19/16.
 */
public class Values {

    private static class Entry {
        private final Value value;

        public Entry(Value value) {
            this.value = value;
        }

        public void addInclusion(final MatchNod2 node) {

        }
        public void addExclusion(final MatchNod2 node) {

        }

        public Value getValue() {
            return value;
        }
    }

    private Entry[] array = new Entry[1];
    private int size = 0;
    private int longestContiguous = 0;

    private final double minimumLoadFactor;
    private final double maximumLoadFactor;

    public Values(double minimumLoadFactor, double maximumLoadFactor) {
        this.minimumLoadFactor = minimumLoadFactor;
        this.maximumLoadFactor = maximumLoadFactor;
    }

    public Values() {
        this(0.1d, 0.5d);
    }

    private static int hash(final Value value, final int length) {
        assert(value != null);
        assert(length >= 0);
        return value.hashCode() % length;
    }

    public boolean function(final Value value, final Vessel vessel) {
        assert(value != null);
        if (array.length == 0) return false;
        if (array.length == 1) {
            final Entry existingEntry = array[0];
            if (existingEntry == null) return false;
            if (value.in(existingEntry.getValue())) {
                existingEntry.on(vessel);
            }
        }

        final int startingIndex = hash(value);
        int i = startingIndex;
        for (; i < array.length; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) continue;
            boolean in = value.in(existingEntry.getValue());
            if (in) return true;
        }
        i = 0;
        for (; i < startingIndex; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) continue;
            boolean in = value.in(existingEntry.getValue());
            if (in) return true;
        }

        return false;
    }

    public boolean in(final Value value) {
        assert(value != null);
        if (array.length == 0) return false;
        if (array.length == 1) {
            final Entry existingEntry = array[0];
            if (existingEntry == null) return false;
            return value.in(existingEntry.getValue());
        }

        final int startingIndex = hash(value);
        int i = startingIndex;
        for (; i < array.length; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) continue;
            boolean in = value.in(existingEntry.getValue());
            if (in) return true;
        }
        i = 0;
        for (; i < startingIndex; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) continue;
            boolean in = value.in(existingEntry.getValue());
            if (in) return true;
        }

        return false;
    }

    public boolean contains(final Value value) {
        assert(value != null);
        if (array.length == 0) return false;
        if (array.length == 1) {
            final Entry existingEntry = array[0];
            if (existingEntry == null) return false;
            return value.equals(existingEntry.getValue());
        }

        final int startingIndex = hash(value);
        int i = startingIndex;
        for (; i < array.length; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) continue;
            final boolean found = value.equals(existingEntry.getValue());
            if (found) return true;
        }
        i = 0;
        for (; i < startingIndex; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) continue;
            final boolean found = value.equals(existingEntry.getValue());
            if (found) return true;
        }

        return false;
    }

    private static int addToArray(final Entry[] array, final Entry entry) {
        Objects.requireNonNull(array);

        int addedCount = 0;

        final Value value = entry.getValue();

        final int startingIndex = hash(value, array.length);
        int i = startingIndex;
        for (; i < array.length; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) {
                array[i] = entry;
                return ++addedCount;
            } else if (existingEntry.getValue().equals(value)) {
                return addedCount;
            }
        }
        i = 0;
        for (; i < startingIndex; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) {
                array[i] = entry;
                return ++addedCount;
            } else if (existingEntry.getValue().equals(value)) {
                return addedCount;
            }
        }
        return addedCount;
    }

    /**
     *
     * @param value
     * @return true if the set was modified
     */
    public boolean add(final Value value) {
        Objects.requireNonNull(value);

        recapacity();

        final Entry entry = new Entry(value);

        return addToArray(array, entry) > 0;
    }

    /**
     *
     * @param value
     * @return true if the set was modified
     */
    public boolean remove(final Value value) {
        Objects.requireNonNull(value);

        int removedCount = 0;

        final int startingIndex = hash(value, array.length);
        int i = startingIndex;
        for (; i < array.length; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) {
                return false;
            } else if (existingEntry.getValue().equals(value)) {
                array[i] = null;
                return true;
            }
        }
        i = 0;
        for (; i < startingIndex; i++) {
            final Entry existingEntry = array[i];
            if (existingEntry == null) {
                return false;
            } else if (existingEntry.getValue().equals(value)) {
                array[i] = null;
                return true;
            }
        }

        return false;
    }

    protected void recapacity(final int newCapacity) {
        if (newCapacity < 0) throw new IllegalArgumentException();
        if (newCapacity < size) throw new IllegalArgumentException();
        if (newCapacity == size) throw new IllegalArgumentException();

        final Entry[] newArray = new Entry[newCapacity];

        int newSize = 0;

        for (int i = 0; i < array.length; i++) {
            final Entry entry = array[i];
            newSize += addToArray(newArray, entry);
        }

        if (newSize != size) throw new IllegalStateException();

        array = newArray;
    }

    /**
     * Suggest recapacity should be checked and possibly happen.
     *
     * @return true if capacity was adjusted.
     */
    public boolean recapacity() {
        final double lf = loadFactor();
        if (maximumLoadFactor < minimumLoadFactor) throw new IllegalStateException();
        if (maximumLoadFactor < 0d) throw new IllegalStateException();
        if (minimumLoadFactor < 0d) throw new IllegalStateException();
        if (maximumLoadFactor > 1d) throw new IllegalStateException();
        if (minimumLoadFactor > 1d) throw new IllegalStateException();
        if (size == 0) {
            recapacity(1);
            return true;
        } else if (lf >= maximumLoadFactor) {
            recapacity(Math.max((int)(size * 2d), size + 4));
            return true;
        } else if (lf <= minimumLoadFactor) {
            recapacity(Math.max((int)(size * (1d/2d)), size + 4));
            return true;
        }
        return false;
    }

    protected double loadFactor() {
        return ((double)size) / ((double)array.length);
    }

    public int getSize() {
        return size;
    }
}
