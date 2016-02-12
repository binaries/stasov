package com.pocketmath.stasov.util.optimizedrangemap;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.function.Consumer;

/**
 * Created by etucker on 2/5/16.
 */
public class NDimensionalOptiDoubleRangeMap<T extends Comparable<T>> {

    final Int2ObjectMap<AbstractOptiDoubleRangeMap<T>> maps = new Int2ObjectLinkedOpenHashMap<>();

    public void put(final double[][] x, final T t) {
        final int xlen = x.length;
        for (int i = 0; i < xlen; i++) {
            AbstractOptiDoubleRangeMap<T> m = maps.get(i);
            if (m == null) {
                m = new AbstractOptiDoubleRangeMap<>();
                maps.put(i, m);
            }
            final int ylen = x[i].length;
            if (ylen != 2) throw new UnsupportedOperationException();
            m.put(x[i][0], x[i][1], t);
        }
    }

    public void forEach(final double[] x, final Consumer<T> consumer) {
        final int xlen = x.length;
        for (int i = 0; i < xlen; i++) {
            final AbstractOptiDoubleRangeMap<T> m = maps.get(i);
            if (m == null) continue;
            m.forEach(x[i], consumer);
        }
    }

    public void remove(final float[][] x, final T t) {
        throw new UnsupportedOperationException("not yet implemented");
      /*  final int xlen = x.length;
        for (int i = 0;i < xlen; i++) {
            final OptiRangeMap<T> m = maps.get(i);
            if (m == null) continue;
            m.remove(x[i], t);
            if (!m.contains(x[i])) maps.remove(i);
        }
        */
    }

}
