package com.pocketmath.stasov.attributes;

/**
 * Created by etucker on 4/4/15.
 */
public class AttrMeta {
    private final String className;
    private final String typeName;
    private final long typeId;

    public AttrMeta(String className, String typeName, long typeId) {
        this.className = className;
        this.typeName = typeName;
        this.typeId = typeId;
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
}
