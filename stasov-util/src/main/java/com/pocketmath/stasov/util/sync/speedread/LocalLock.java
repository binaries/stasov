package com.pocketmath.stasov.util.sync.speedread;

import javax.annotation.Nonnull;

/**
 * Created by etucker on 1/29/16.
 */
class LocalLock implements Lock {
    private Object owner = null;
    private long lockedUntil = -1;
    private long unlockedUntil = -1;

    private long lastUpdate = -1;

    @Override
    public long getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public Object getOwner() {
        return owner;
    }

    protected void setOwner(@Nonnull Object owner) {
        this.owner = owner;
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public long getLockedUntil() {
        return lockedUntil;
    }

    protected void setLockedUntil(long lockedUntil) {
        this.lockedUntil = lockedUntil;
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public long getUnlockedUntil() {
        return unlockedUntil;
    }

    protected void setUnlockedUntil(long unlockedUntil) {
        this.unlockedUntil = unlockedUntil;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void copyFrom(final @Nonnull Lock source) {
        if (source == null) throw new IllegalArgumentException();

        this.owner = source.getOwner();
        this.lockedUntil = source.getLockedUntil();
        this.unlockedUntil = source.getUnlockedUntil();

        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "LocalLock{" +
                "owner=" + owner +
                ", lockedUntil=" + lockedUntil +
                ", unlockedUntil=" + unlockedUntil +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
