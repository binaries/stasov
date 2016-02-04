package com.pocketmath.stasov.engine;

import com.pocketmath.stasov.util.IndexAlgorithm;

import java.util.logging.Level;

/**
 * Created by etucker on 2/1/16.
 */
public class EngineConfig {

    private static final EngineConfig config = new EngineConfig();

    public final static EngineConfig getConfig() {
        return config;
    }

    public IndexAlgorithm getPreferredIndexAlgorithm() {
        return IndexAlgorithm.HASH;
    }

    public Level getLogLevel() {
        return Level.WARNING;
    }

}
