package com.pocketmath.stasov.util.multimaps2.array;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * Created by etucker on 8/23/16.
 */
public abstract class AbstractLong2Long2ArrayHashMap<V> extends AbstractLong2Long2ArrayMap<V> {

    @Override
    protected Long2ObjectMap<Long2ObjectMap<IArraySet<V>>> newFirstLevelMap() {
        return new Long2ObjectOpenHashMap<>();
    }

    @Override
    protected Long2ObjectMap<IArraySet<V>> newSecondLevelMap() {
        return new Long2ObjectOpenHashMap<>();
    }

}
