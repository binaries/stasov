package com.pocketmath.stasov.util.multimaps2.array;

import java.util.*;

/**
 * <p>Optimized for read performance.  Add and remove methods are better thaan O(n) but not optimal.</p>
 *
 * <p>Iteration should take place as follows:</p>
 *
 * <pre>
 * for (int i = o.startIndex(); i < endIndex(); i++) {
 *     // some logic
 * }
 * </pre>
 *
 * Created by etucker on 8/20/16.
 */
public abstract class StasovArraySet<V> implements IArraySet<V> {

    final static float LOAD_FACTOR = 1.5f;

    private V[] array;
    final Comparator<V> comparator;
    private int endIndex;
    private boolean sorted;

    public StasovArraySet(final Comparator<V> comparator, final int initialCapacity) {
        if (comparator == null) throw new IllegalArgumentException();
        if (initialCapacity < 0) throw new IllegalArgumentException();
        this.array = newArray(initialCapacity);
        this.comparator = comparator;
        this.endIndex = -1;
        this.sorted = true;
    }

    public StasovArraySet(final int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException();
        this.array = newArray(initialCapacity);
        this.comparator = null;
        this.endIndex = -1;
        this.sorted = true;
    }

    public StasovArraySet(final Comparator<V> comparator, final V[] values, final boolean fast) {
        if (comparator == null) throw new IllegalArgumentException();
        if (values == null) throw new IllegalArgumentException();
        if (values.length == 0) throw new IllegalArgumentException();
        if (!fast)
            for (final V value : values)
                if (value == null)
                    throw new IllegalArgumentException("input array contained null");
        this.array = values;
        this.comparator = comparator;
        this.endIndex = values.length - 1;
        this.sorted = false;
    }

    public StasovArraySet(final V[] values, final boolean fast) {
        if (values == null) throw new IllegalArgumentException();
        if (values.length == 0) throw new IllegalArgumentException();
        if (!fast)
            for (final V value : values)
                if (value == null)
                    throw new IllegalArgumentException("input array contained null");
        this.array = values;
        this.comparator = null;
        this.endIndex = values.length - 1;
        this.sorted = false;
    }

    public StasovArraySet(final Comparator<V> comparator, final V[] values) {
        this(comparator, values, false);
    }

    public StasovArraySet(final V[] values) {
        this(values, false);
    }

    public StasovArraySet(final Comparator<V> comparator, final Collection<V> values, final boolean fast) {
        if (!fast)
            for (final V value : values)
                if (value == null)
                    throw new IllegalArgumentException("input array contained null");
        this.array = values.toArray(newArray(values.size()));
        this.comparator = comparator;
        this.endIndex = values.size() - 1;
        this.sorted = false;
    }

    public StasovArraySet(final Comparator<V> comparator, final Collection<V> values) {
        this(comparator, values, false);
    }

    abstract V[] newArray(final int size);

    protected void resize(final int newCapacity) {
        if (newCapacity < size())
            throw new IllegalStateException();
        final V[] newArray = newArray(newCapacity);
        System.arraycopy(array, 0, newArray, 0, newCapacity);
        this.array = newArray;
    }

    public void sort() {
        if (sorted)
            return;
        if (comparator == null)
            Arrays.sort(array, 0, endIndex + 1);
        else
            Arrays.sort(array, 0, endIndex + 1, comparator);
        sorted = true;
    }

    protected int findIndex(final V value) {
        Objects.requireNonNull(value);
        sort();
        return Arrays.binarySearch(array, 0, endIndex + 1, value);
    }

    @Override
    public int startIndex() {
        return 0;
    }

    @Override
    public int endIndex() {
        return endIndex;
    }

    @Override
    public V fastGet(final int index) {
        return array[index];
    }

    @Override
    public V get(final int index) {
        if (index < 0)
            throw new IllegalArgumentException();
        return fastGet(index);
    }

    protected void fastPut(final int index, final V value) {
        array[index] = value;
        if (index > endIndex)
            endIndex = index;
    }

    @Override
    public int size() {
        return endIndex + 1;
    }

    @Override
    public boolean isEmpty() {
        return endIndex >= 0;
    }

    public int currentCapacity() {
        return array.length;
    }

    private void grow() {
        final int currentSize = size();
        final int newCapacity = Math.max(
                (int)(((float)currentSize) * LOAD_FACTOR),
                Math.addExact(currentSize, 2));
        resize(newCapacity);
    }

    @Override
    public void fastAdd(final V value) {
        if (endIndex + 1 < array.length) {
            assert endIndex + 1 < currentCapacity();
            array[++endIndex] = value;
        } else {
            grow();
            assert endIndex + 1 < currentCapacity();
            array[++endIndex] = value;
            assert endIndex < currentCapacity();
        }
        sorted = false;
    }

    @Override
    public boolean add(final V value) {
        if (value == null)
            throw new IllegalArgumentException();
        if (findIndex(value) >= 0)
            return false;
        fastAdd(value);
        assert !sorted;
        return true;
    }

    @Override
    public void addAll(final Collection<V> values) {
        final int addSize = values.size();
        if (endIndex + 1 + addSize < array.length) {
            final int newCapacity = Math.addExact(size(), addSize);
            resize(newCapacity);
        }
        for (final V value : values) {
            array[endIndex++] = value;
        }
        sorted = false;
    }

    @Override
    public void addAll(final V[] values) {
        final int addSize = values.length;
        if (endIndex + 1 + addSize < array.length) {
            final int newCapacity = Math.addExact(size(), addSize);
            resize(newCapacity);
        }
        System.arraycopy(values, 0, array, endIndex, addSize);
        endIndex = Math.addExact(endIndex, addSize);
        sorted = false;
    }

    @Override
    public boolean remove(final V value) {
        if (value == null)
            throw new IllegalArgumentException();
        final int index = findIndex(value);
        if (index < 0)
            return false;
        array[endIndex] = array[index];
        endIndex--;
        sorted = false;
        return true;
    }

    @Override
    public void removeAll(final Set<V> values) {
        Objects.requireNonNull(values);
        if (values.size() < 1)
            return;

        if (values.size() == 1)
            remove(values.iterator().next());

        final Comparator<V> nullComparator = new Comparator<V>() {
            @Override
            public int compare(V o1, V o2) {
                if (o1 == null)
                    return +1;
                else if (o2 == null)
                    return -1;
                else if (comparator != null)
                    return comparator.compare(o1, o2);
                else
                    return ((Comparable<V>)o1).compareTo(o2);
            }
        };

        for (final V value : values) {
            final int index = findIndex(value);
            array[index] = null;
        }

        Arrays.sort(array, 0, endIndex + 1, nullComparator);
        endIndex -= values.size();
    }

    @Override
    public boolean contains(V value) {
        sort();
        return findIndex(value) >= 0;
    }

    @Override
    public void readOptimize() {
        resize(size());
        sort();
    }

    @Override
    public void writeOptimize() {
        grow();
        sort();
    }

    @Override
    public void clear() {
        endIndex = -1;
        sorted = true;
    }

}
