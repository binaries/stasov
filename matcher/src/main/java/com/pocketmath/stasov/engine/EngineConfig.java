package com.pocketmath.stasov.engine;

import java.util.logging.Level;

/**
 * Created by etucker on 2/1/16.
 */
public class EngineConfig {

    private static final EngineConfig config = new EngineConfig();

    public final static EngineConfig getConfig() {
        return config;
    }

    public Level getLogLevel() {
        return Level.FINEST;
    }

}
