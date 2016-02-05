package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;

import javax.sql.rowset.serial.SerialArray;
import java.io.Serializable;

/**
 * Created by etucker on 1/24/16.
 */
public abstract class Engine<ObjectType extends Serializable & Comparable> {

    public abstract void index(String pmtl, ObjectType id) throws IndexingException;

    public abstract ObjectType[] query(OpportunityDataBase opportunity); // TODO: make return type ObjectType

    public abstract void remove(final ObjectType id) throws IndexingException;

    public void update(String pmtl, ObjectType id) throws IndexingException {
        remove(id);
        index(pmtl, id);
    }

    abstract String prettyPrint();

    abstract AttrSvcBase getAttrSvc();

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

    public static <ObjectType extends Serializable & Comparable> Engine newConcurrentEngine() {
        return new ConcurrentEngineWrapper<ObjectType>(newSafeEngine());
    }

    public static <ObjectType extends Serializable & Comparable> Engine newConcurrentFastEngine() {
        return new ConcurrentEngineWrapper<ObjectType>(newFastEngine());
    }

    public static Engine<Long> newLongConcurrentEngineEngine() {
        return new ConcurrentEngineWrapper<Long>(newSafeEngine());
    }

    public static Engine<String> newLongConcurrentStringEngine() {
        return new ConcurrentEngineWrapper<String>(newStringEngine());
    }

    public static <ObjectType extends Serializable & Comparable> Engine newDefaultEngine() {
        return newLongEngine();
    }
}
