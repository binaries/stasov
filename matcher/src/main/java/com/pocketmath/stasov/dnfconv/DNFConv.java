package com.pocketmath.stasov.dnfconv;

/**
 * Created by etucker on 6/9/15.
 */
public class DNFConv {

    public static String convertToDNF(final String spec)  {
        final DNFConvTree.Node root = DNFConvTreeBuilder.parse(spec);
        DNFConvTree.transformToDNF(root);
        return root.toPocketTL();
    }

}
