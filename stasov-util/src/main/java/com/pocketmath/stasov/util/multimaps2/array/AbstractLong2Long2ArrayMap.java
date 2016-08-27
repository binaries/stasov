package com.pocketmath.stasov.util.multimaps2.array;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Collection;

/**
 * Created by etucker on 8/22/16.
 */
public abstract class AbstractLong2Long2ArrayMap<RawKeyType1, RawKeyType2, V> implements ILong2Long2ArrayMap<V> {

    private Long2ObjectMap<Long2ObjectMap<IArraySet<V>>> map = newFirstLevelMap();

    protected abstract Long2ObjectMap<Long2ObjectMap<IArraySet<V>>> newFirstLevelMap();

    protected abstract Long2ObjectMap<IArraySet<V>> newSecondLevelMap();

    protected abstract IArraySet<V> newThirdLevelSet();

    @Override
    public IArraySet<V> getArraySet(long key1, long key2) {
        final Long2ObjectMap<IArraySet<V>> t2 = map.get(key1);
        if (t2 == null) return null;
        final IArraySet<V> t3 = t2.get(key2);
        return t3;
    }

    @Override
    public void addEach(final long key1, final long key2, final Collection<V> collector) {
        final Long2ObjectMap<IArraySet<V>> t2 = map.get(key1);
        if (t2 == null)
            return;
        final IArraySet<V> t3 = t2.get(key2);
        if (t3 == null)
            return;
        final int t3EndIndex = t3.endIndex();
        for (int i = t3.startIndex(); i <= t3EndIndex; i++) {
            final V item = t3.get(i);
            assert item != null;
            collector.add(item);
        }
    }

    @Override
    public void addEach(final long key1, final long[] keys2, final Collection<V> collector) {
        final Long2ObjectMap<IArraySet<V>> t2 = map.get(key1);
        if (t2 == null)
            return;
        for (final long key2 : keys2) {
            final IArraySet<V> t3 = t2.get(key2);
            if (t3 == null)
                return;
            final int t3EndIndex = t3.endIndex();
            for (int i = t3.startIndex(); i <= t3EndIndex; i++) {
                final V item = t3.get(i);
                assert item != null;
                collector.add(item);
            }
        }
    }

    @Override
    public void removeEach(final long key1, final long key2, final Collection<V> collector) {
        final Long2ObjectMap<IArraySet<V>> t2 = map.get(key1);
        if (t2 == null)
            return;
        final IArraySet<V> t3 = t2.get(key2);
        if (t3 == null)
            return;
        final int t3EndIndex = t3.endIndex();
        for (int i = t3.startIndex(); i <= t3EndIndex; i++) {
            final V item = t3.get(i);
            assert item != null;
            collector.remove(item);
        }
    }

    @Override
    public void removeEach(final long key1, final long[] keys2, final Collection<V> collector) {
        final Long2ObjectMap<IArraySet<V>> t2 = map.get(key1);
        if (t2 == null)
            return;
        for (final long key2 : keys2) {
            final IArraySet<V> t3 = t2.get(key2);
            if (t3 == null)
                return;
            final int t3EndIndex = t3.endIndex();
            for (int i = t3.startIndex(); i <= t3EndIndex; i++) {
                final V item = t3.get(i);
                assert item != null;
                collector.remove(item);
            }
        }
    }

    /*
    @Override
    public void addEach(final long key1, final RawKeyType2 rawKey2, final IKeyTranslator translator, final Collection<V> collector) {
        final Long2ObjectMap<IArraySet<V>> t2 = map.get(key1);
        if (t2 == null)
            return;
        final long key2 = translator.translate(key1, rawKey2);
        final IArraySet<V> t3 = t2.get(key2);
        if (t3 == null)
            return;
        final int t3EndIndex = t3.endIndex();
        for (int i = t3.startIndex(); i <= t3EndIndex; i++) {
            final V item = t3.get(i);
            assert item != null;
            collector.add(item);
        }
    }

    @Override
    public void addEach(final long key1, final Collection<RawKeyType2> rawKeys2, final IKeyTranslator translator, final Collection<V> collector) {
        final Long2ObjectMap<IArraySet<V>> t2 = map.get(key1);
        if (t2 == null)
            return;
        for (final RawKeyType2 rawKey2 : rawKeys2) {
            final long key2 = translator.translate(key1, rawKey2);
            final IArraySet<V> t3 = t2.get(key2);
            if (t3 == null)
                return;
            final int t3EndIndex = t3.endIndex();
            for (int i = t3.startIndex(); i <= t3EndIndex; i++) {
                final V item = t3.get(i);
                assert item != null;
                collector.add(item);
            }
        }
    }
    */

    @Override
    public void put(long key1, long key2, V value) {
        Long2ObjectMap<IArraySet<V>> t2;
        IArraySet<V> t3;

        t2 = map.get(key1);
        if (t2 == null) {
            t2 = newSecondLevelMap();
            map.put(key1, t2);
            t3 = newThirdLevelSet();
            t2.put(key2, t3);
        } else {
            t3 = t2.get(key2);
            if (t3 == null) {
                t3 = newThirdLevelSet();
                t2.put(key2, t3);
            }
        }

        assert t3 != null;
        assert t2 != null;
        assert t2.get(key2) == t3;
        assert map.get(key1) == t2;

        t3.add(value);

        assert t3.contains(value);
    }

    @Override
    public void put(long key1, long[] keys2, V value) {
        Long2ObjectMap<IArraySet<V>> t2;
        IArraySet<V> t3;

        t2 = map.get(key1);
        if (t2 == null) {
            t2 = newSecondLevelMap();
            map.put(key1, t2);
            for (final long key2 : keys2) {
                t3 = newThirdLevelSet();
                t2.put(key2, t3);
                t3.add(value);
            }
        } else {
            for (final long key2 : keys2) {
                t3 = t2.get(key2);
                if (t3 == null) {
                    t3 = newThirdLevelSet();
                    t2.put(key2, t3);
                    t3.add(value);
                }
            }
        }
    }

    @Override
    public boolean containsKey(long key1, long key2) {
        Long2ObjectMap<IArraySet<V>> t2;

        t2 = map.get(key1);
        if (t2 == null)
            return false;

        return t2.containsKey(key2);
    }

    @Override
    public boolean containsKey(long key1) {
        return map.containsKey(key1);
    }

    @Override
    public LongSet getKeys1() {
        return map.keySet();
    }

    @Override
    public LongSet getKeys2(long key1) {
        Long2ObjectMap<IArraySet<V>> t2;

        t2 = map.get(key1);
        if (t2 == null)
            return null;
        return t2.keySet();
    }

    @Override
    public void remove(long key1) {
        map.remove(key1);
    }

    @Override
    public void remove(long key1, long key2) {
        Long2ObjectMap<IArraySet<V>> t2;

        t2 = map.get(key1);
        if (t2 == null)
            return;
        t2.remove(key2);

        if (t2.isEmpty())
            map.remove(key1);
    }

    @Override
    public void remove(long key1, long key2, V value) {
        Long2ObjectMap<IArraySet<V>> t2;
        IArraySet<V> t3;

        t2 = map.get(key1);
        if (t2 == null)
            return;
        t3 = t2.get(key2);
        if (t3 == null)
            return;
        t3.remove(value);

        if (t3.isEmpty())
            t2.remove(key2);
        if (t2.isEmpty())
            map.remove(key1);

    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String prettyPrint(String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String prettyPrint(String prefix, String key1Prefix, String key2Prefix, String valuePrefix) {
        return null;
    }

}
