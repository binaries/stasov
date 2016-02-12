package com.pocketmath.stasov.pmtl.dnfconv;

import com.pocketmath.stasov.pmtl.PocketTLLanguageException;

/**
 * Created by etucker on 6/9/15.
 */
public class DNFConv {

    public static String convertToDNF(final String pmtl) throws PocketTLLanguageException {
        final DNFConvTree tree = TreeBuilder.parse(pmtl); // load the tree, but don't findRangedPoints it yet
        tree.transformToDNF(); // CONVERT!
        return tree.toPocketTL();
    }

}
