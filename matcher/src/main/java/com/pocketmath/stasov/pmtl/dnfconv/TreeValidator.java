package com.pocketmath.stasov.pmtl.dnfconv;

import com.pocketmath.stasov.pmtl.dnfconv.DNFConvModels.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by etucker on 6/28/15.
 */
public class TreeValidator {

    private static void validate(final DNFConvTree tree, final Node current, final Set<Node> visited) throws TreeStructuralException {
        if (tree == null) throw new IllegalArgumentException("tree was null");
        if (current == null) throw new IllegalStateException();
        if (visited == null) throw new IllegalArgumentException("visited was null");

        if (visited.contains(current)) throw new TreeStructuralException();

        if (current instanceof Leaf) return;
        else if (!current.hasChildren()) throw new TreeStructuralException("non-leaf node has no children; node: " + current + "; parent: " + current.getParent() + "; node.isRoot: " + tree.isRoot(current));

        if (current.getChildren().size() < 1) throw new IllegalStateException();

        // check children
        for (final Node node : current.getChildren()) {
            if (!current.containsChild(node))
                throw new TreeStructuralException("missing child: " + node);
            if (!node.getParent().equals(current))
                throw new TreeParentMistmatchStructuralException(node, node.getParent(), current);
            if (node.getParent() != current) throw new TreeParentMistmatchStructuralException(node, node.getParent(), current);
            validate(tree, node, visited);
        }
    }

    /**
     * Verify that the tree is structurally valid.  This method checks parent-child relationships and ensures
     * that (1) every child points to a parent (2) every parent points to its children.
     * @param tree
     */
    static void validate(final DNFConvTree tree) throws TreeStructuralException {
        final Node root = tree.getRoot();

        // check validity of root
        if (root.getParent() != null) throw new TreeStructuralException("root had a parent");

        // check everything else (recursively)
        validate(tree, root, new HashSet<Node>());
    }

}
