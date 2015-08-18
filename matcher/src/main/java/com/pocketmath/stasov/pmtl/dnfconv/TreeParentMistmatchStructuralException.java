package com.pocketmath.stasov.pmtl.dnfconv;

/**
 * Created by etucker on 6/28/15.
 */
public class TreeParentMistmatchStructuralException extends TreeStructuralException {

    TreeParentMistmatchStructuralException(final DNFConvModels.Node child, final DNFConvModels.Node actualParent, final DNFConvModels.Node expectedParent) {
        super("child: " + child + "; actual parent: " + actualParent + "; expected parent: " + expectedParent);
    }

}
