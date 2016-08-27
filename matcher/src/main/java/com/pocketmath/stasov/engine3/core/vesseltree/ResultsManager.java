package com.pocketmath.stasov.engine3.core.vesseltree;

import com.pocketmath.stasov.util.pool.AbstractPool;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 8/27/16.
 */
public class ResultsManager {

    private static class Pool extends AbstractPool<Results> {

        public Pool(int capacity, int initialSize, boolean prepopulate) {
            // TODO: Integrate logger more seamlessly with framework / app environment.
            super(capacity, initialSize, prepopulate, Logger.getLogger(ResultsManager.class.getName()), Level.WARNING);
        }

        @Override
        protected Results newInstance() {
            return new Results();
        }
    }

    private AbstractPool<Results> pool;
    {
        final int cores = Runtime.getRuntime().availableProcessors();

        // TODO: Make configurable.
        pool = new Pool(Math.multiplyExact(16384, cores), Math.multiplyExact(128, cores), true);
    }

    public Results getResultsObject() {
        return pool.getInstance();
    }

    public void reclaimResultsObject(final Results collector) {
        pool.reclaim(collector);
    }

}
