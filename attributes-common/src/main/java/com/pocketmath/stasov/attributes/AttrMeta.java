package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 4/4/15.
 */
public class AttrMeta {
    private final String className;
    private final String typeName;
    private final long typeId;
    private final boolean cached;
    private final int cacheSize;

    public AttrMeta(String className, String typeName, long typeId, boolean cached, int cacheSize) {
        this.className = className;
        this.typeName = typeName;
        this.typeId = typeId;
        this.cached = cached;
        this.cacheSize = cacheSize;
    }

    public String getClassName() {
        return className;
    }

    public String getTypeName() {
        return typeName;
    }

    public long getTypeId() {
        return typeId;
    }

    public boolean isCached() {
        return cached;
    }

    public int getCacheSize() {
        return cacheSize;
    }
}
