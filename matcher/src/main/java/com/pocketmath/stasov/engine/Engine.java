package com.pocketmath.stasov.engine;

import java.io.Serializable;

/**
 * Created by etucker on 1/24/16.
 */
public abstract class Engine<ObjectType extends Serializable & Comparable> {

    abstract void index(String pmtl, ObjectType id) throws IndexingException;

    abstract Object[] query(OpportunityDataBase opportunity);

    abstract void remove(final ObjectType id) throws IndexingException;

    abstract String prettyPrint();

    public static <ObjectType extends Serializable & Comparable> Engine newSafeEngine() {
        return new EngineBase<ObjectType>();
    }

    public static <ObjectType extends Serializable & Comparable> Engine newFastEngine() {
        final boolean SAFE = false;
        return new EngineBase<ObjectType>(SAFE);
    }

    public static Engine<Long> newLongEngine() {
        return new EngineBase<Long>();
    }

    public static Engine<String> newStringEngine() {
        return new EngineBase<String>();
    }

    public static <ObjectType extends Serializable & Comparable> Engine newDefaultEngine() {
        return newSafeEngine();
    }

}
