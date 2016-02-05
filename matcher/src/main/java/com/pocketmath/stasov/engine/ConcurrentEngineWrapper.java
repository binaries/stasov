package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.util.sync.speedread.SpeedReadExecution;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Assumes the index operation will never take more than X milliseconds.
 *
 * Created by etucker on 1/21/16.
 */
public class ConcurrentEngineWrapper<ObjectType extends Serializable & Comparable> extends Engine<ObjectType> {

    private final Engine<ObjectType> engine;

    private SpeedReadExecution execution;

    public ConcurrentEngineWrapper(final Engine engine, final long lockInterval) {
        if (engine == null) throw new IllegalArgumentException();
        if (lockInterval < 0) throw new IllegalArgumentException("index time must be greater than zero; was " + lockInterval);
        this.engine = engine;
        this.execution = new SpeedReadExecution(lockInterval);
    }

    public ConcurrentEngineWrapper(final Engine engine) {
        this.engine = engine;
        this.execution = new SpeedReadExecution();
    }

    private class IndexCallable implements Callable {
        private final String pmtl;
        private final ObjectType id;

        public IndexCallable(final String pmtl, final ObjectType id) {
            this.pmtl = pmtl;
            this.id = id;
        }

        @Override
        public Object call() throws Exception {
            engine.index(pmtl, id);
            return null;
        }
    }

    @Override
    public void index(String pmtl, ObjectType id) throws IndexingException {
        IndexCallable callable = new IndexCallable(pmtl, id);
        try {
            execution.blockingExecuteCritical(callable);
        } catch (Exception e) {
            throw new IndexingException(e);
        }
    }

    @Override
    public ObjectType[] query(OpportunityDataBase opportunity) {
        if (execution.isFreeNow()) {
            return engine.query(opportunity);
        } else {
            // TODO: Some logging about lock not free or execution not possible?
            return null;
        }
    }

    private class RemoveCallable implements Callable {
        private ObjectType id;

        public RemoveCallable(final ObjectType id) {
            if (id == null) throw new IllegalArgumentException();
            this.id = id;
        }

        @Override
        public Object call() throws Exception {
            return null;
        }
    }

    @Override
    public void remove(ObjectType id) throws IndexingException {
        final RemoveCallable callable = new RemoveCallable(id);
        try {
            execution.blockingExecuteCritical(callable);
        } catch (Exception e) {
            throw new IndexingException(e);
        }
    }

    @Override
    String prettyPrint() {
        return engine.prettyPrint();
    }

    @Override
    AttrSvcBase getAttrSvc() {
        return engine.getAttrSvc();
    }
}
