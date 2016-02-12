package com.pocketmath.stasov.util.optimizedrangemap;

/**
 * Created by etucker on 2/7/16.
 */
public class Opti3DDoubleRMEntry<T> {

    private final double x, y, z;
    private final T t;

    public Opti3DDoubleRMEntry(final double x, final double y, final double z, final T t) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.t = t;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public T getT() {
        return t;
    }

}
