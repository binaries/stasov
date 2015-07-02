package com.pocketmath.stasov.pmtl.dnfconv;

/**
 * Created by etucker on 6/28/15.
 */
public class TreeParentMistmatchStructuralException extends TreeStructuralException {

    TreeParentMistmatchStructuralException(final DNFConvTree.Node child, final DNFConvTree.Node actualParent, final DNFConvTree.Node expectedParent) {
        super("child: " + child + "; actual parent: " + actualParent + "; expected parent: " + expectedParent);
    }

}
