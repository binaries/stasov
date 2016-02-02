package com.pocketmath.stasov.util.multimaps;

import com.pocketmath.stasov.util.PrettyPrintable;
import com.pocketmath.stasov.util.TreeAlgorithm;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by etucker on 3/22/15.
 */
public class Long2Long2ObjectSortedMultiValueMap<V extends Comparable<V>> extends AbstractSortedMultiValueMap<V>
        implements ILong2Long2ObjectMultiValueSortedMap<V>, PrettyPrintable {

    private final Long2ObjectMap<Long2ObjectSortedMultiValueMap<V>> map;

    public Long2Long2ObjectSortedMultiValueMap(Comparator<V> valueComparator, TreeAlgorithm treeAlg) {
        super(valueComparator, treeAlg);
        switch (treeAlgorithm) {
            case REDBLACK:  { map = new Long2ObjectRBTreeMap<Long2ObjectSortedMultiValueMap<V>>(); break; }
            case AVL:       { map = new Long2ObjectAVLTreeMap<Long2ObjectSortedMultiValueMap<V>>(); break; }
            default:        { throw new IllegalStateException(); }
        }
    }

    public Long2Long2ObjectSortedMultiValueMap(Comparator<V> valueComparator) {
        this(valueComparator, TreeAlgorithm.AVL);
    }

    @Override
    public void put(final long key1, final long key2, final V value) {
        Long2ObjectSortedMultiValueMap<V> subMap = map.get(key1);
        if (subMap == null) {
            subMap = new Long2ObjectSortedMultiValueMap<V>(valueComparator, treeAlgorithm);
            map.put(key1, subMap);
        }
        subMap.put(key2, value);
    }

    /**
     * Put the same value for one key1 and several key2s.
     *
     * @param key1
     * @param keys2
     * @param value
     */
    public void put(final long key1, final long[] keys2, final V value) {
        Long2ObjectSortedMultiValueMap<V> subMap = map.get(key1);
        if (subMap == null) {
            subMap = new Long2ObjectSortedMultiValueMap<V>(valueComparator, treeAlgorithm);
            map.put(key1, subMap);
        }
        for (final long key2: keys2) subMap.put(key2, value);
    }

    @Override
    public ObjectSortedSet<V> getSorted(final long key1, final long key2) {
        final Long2ObjectSortedMultiValueMap<V> subMap = map.get(key1);
        return (subMap == null) ? null : subMap.getSorted(key2);
    }

    @Override
    public ObjectSet<V> get(final long key1, final long key2) {
        return getSorted(key1, key2);
    }

    @Override
    public LongSet getKeys2(final long key1) {
        final Long2ObjectSortedMultiValueMap<V> subMap = map.get(key1);
        return (subMap == null) ? null : subMap.getKeys();
    }

    @Override
    public boolean containsKey(final long key1) {
        return map.containsKey(key1);
    }

    @Override
    public boolean containsKey(final long key1, final long key2) {
        final Long2ObjectSortedMultiValueMap<V> subMap = map.get(key1);
        if (subMap == null) return false;
        return subMap.containsKey(key2);
    }

    public boolean matchesAll(final long key1, final long[] keys2, final V value) {
        final Long2ObjectSortedMultiValueMap subMap = map.get(key1);
        return subMap != null ? subMap.matchesAll(keys2, value) : false;
    }

    public void remove(final long key1, final long key2, final long value) {
        final Long2ObjectSortedMultiValueMap<V> subMap = map.get(key1);
        if (subMap == null) return;
        subMap.remove(key2, value);
    }

    public void remove(final long key1, final long key2) {
        final Long2ObjectSortedMultiValueMap<V> subMap = map.get(key1);
        if (subMap == null) return;
        subMap.remove(key2);
    }

    public void remove(final long key1) {
        map.remove(key1);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }

    @Override
    public String toString() {
        return "DualLong2ObjectMultiValueMap{" +
                "multiValueMap=" + map +
                '}';
    }

    public String prettyPrint(final String prefix, final String key1Prefix, final String key2Prefix, final String valuePrefix) {
        if (map.isEmpty()) return "EMPTY\n\r";
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw);
        final String t1 = prefix + "  " + key1Prefix;
        final String t2 = prefix + "    " + key2Prefix;
        final String t3 = prefix + "      " + valuePrefix;
        w.println(prefix + "{");
        for (final Long2ObjectMap.Entry<Long2ObjectSortedMultiValueMap<V>> entry1 : map.long2ObjectEntrySet()) {
            w.println(t1 + entry1.getKey() + " = ");
            for (final Map.Entry<Long, ObjectSortedSet<V>> entry2 : entry1.getValue().entrySet()) {
                w.println(t2 + entry2.getKey() + " = ");
                for (final V value : entry2.getValue()) {
                    if (value instanceof PrettyPrintable) {
                        PrettyPrintable prettyPrintable = (PrettyPrintable) value;
                        w.println(t3 + prettyPrintable.prettyPrint(t3 + "  "));
                    } else
                        w.println(t3 + value);
                }
            }
        }
        w.println(prefix + "}");

        return sw.toString();
    }

    public String prettyPrint(final String prefix) {
        return prettyPrint(prefix, "", "", "");
    }

    public String prettyPrint() {
        return prettyPrint("", "", "", "");
    }
}
