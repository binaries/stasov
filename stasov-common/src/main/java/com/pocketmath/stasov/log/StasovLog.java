package com.pocketmath.stasov.log;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

/**
 * Created by etucker on 2/28/16.
 */
public class StasovLog {

    public static Logger getLogger(@Nonnull final Class clazz) {
        return Logger.getLogger(clazz.getName());
    }

}
