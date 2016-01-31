package com.pocketmath.stasov.util.sync.speedread;

/**
 * Created by etucker on 1/29/16.
 */
interface Lock {

    public long getLastUpdate();

    public Object getOwner();

    public long getLockedUntil();

    public long getUnlockedUntil();

}
