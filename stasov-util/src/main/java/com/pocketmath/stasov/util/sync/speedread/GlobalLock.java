package com.pocketmath.stasov.util.sync.speedread;

/**
 * Created by etucker on 1/29/16.
 */
class GlobalLock implements Lock {
    // The use of volatile is crucial on the following fields as it enables "global synchronization",
    // rather, synchronization across all threads.

    private volatile Object owner = null;
    private volatile long lockedUntil = -1;
    private volatile long unlockedUntil = -1;

    private volatile long lastUpdate = -1;

    @Override
    public long getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public Object getOwner() {
        return owner;
    }

    protected void setOwner(Object owner) {
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

    public void updateUnlockedUntil(final long until, final Object owner) {
        this.owner = owner;
        this.unlockedUntil = until;
        this.lastUpdate = System.currentTimeMillis();

    }

    public void updateLockedUntil(final long until, final Object owner) {
        this.owner = owner;
        this.lockedUntil = until;
        this.lastUpdate = System.currentTimeMillis();

    }

    public void release() {
        this.lockedUntil = 0L;
        this.owner = null;
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "GlobalLock{" +
                "owner=" + owner +
                ", lockedUntil=" + lockedUntil +
                ", unlockedUntil=" + unlockedUntil +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
