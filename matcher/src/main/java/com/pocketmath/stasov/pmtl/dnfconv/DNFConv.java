package com.pocketmath.stasov.pmtl.dnfconv;

import com.pocketmath.stasov.pmtl.PocketTLLanguageException;

/**
 * Created by etucker on 6/9/15.
 */
public class DNFConv {

    public static String convertToDNF(final String spec) throws PocketTLLanguageException {
        final DNFConvTree.Node root = DNFConvTreeBuilder.parse(spec);
        DNFConvTree.transformToDNF(root);
        return root.toPocketTL();
    }

}
