package com.pocketmath.stasov.util;

import it.unimi.dsi.fastutil.doubles.Double2DoubleMap;
import it.unimi.dsi.fastutil.doubles.Double2DoubleRBTreeMap;

/**
* Created by etucker on 3/21/15.
*/
public class Weights {
    public static double DEFAULT_WEIGHT = 0.5;
    public static double WEIGHT_FLOOR = 0.0;
    public static double WEIGHT_CEILING = 1.0;
    private Double2DoubleMap map = new Double2DoubleRBTreeMap();
    public double getOrAssignWeight(final long key) {
        final double w;
        if (map.containsValue(key)) {
            w = map.get(key);
        } else {
            w = DEFAULT_WEIGHT;
            map.put(key, w);
        }
        return w;
    }
    public void remove(final long key) {
        map.remove(key);
    }
    public void clear() {
        map.clear();
    }
    public void setWeight(final long key, final double weight) {
        if (weight < WEIGHT_FLOOR) throw new IllegalArgumentException();
        if (weight > WEIGHT_CEILING) throw new IllegalArgumentException();
        map.put(key, weight);
    }
}
