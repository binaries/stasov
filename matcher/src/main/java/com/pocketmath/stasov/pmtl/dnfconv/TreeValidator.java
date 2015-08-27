package com.pocketmath.stasov.pmtl.dnfconv;

import com.pocketmath.stasov.pmtl.dnfconv.DNFConvModels.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by etucker on 6/28/15.
 */
public class TreeValidator {

    private static void validate(final DNFConvTree tree, final Node current, final Set<Node> visited, final boolean requireNonLeafNodesHaveLeaves) throws TreeStructuralException {
        if (tree == null) throw new IllegalArgumentException("tree was null");
        if (current == null) throw new IllegalStateException();
        if (visited == null) throw new IllegalArgumentException("visited was null");

        if (visited.contains(current)) throw new TreeStructuralException();

        if (current instanceof Leaf) return;
        else if (requireNonLeafNodesHaveLeaves && !current.hasChildren()) throw new TreeStructuralException("non-leaf node has no children; node: " + current + "; parent: " + current.getParent() + "; node.isRoot: " + tree.isRoot(current));

        if (requireNonLeafNodesHaveLeaves && current.getChildren().size() < 1) throw new IllegalStateException();

        current.validate();

        //if (!tree.isRoot(current) && current.getParent() == null) throw new TreeStructuralException("non-root node had null parent");

        // check children
        if (current.hasChildren()) {
            for (final Node node : current.getChildren()) {
                if (node == null) throw new IllegalStateException();
                if (!current.containsChild(node))
                    throw new TreeStructuralException("missing child: " + node);
                if (node.getParent() == null) throw new IllegalStateException("Parent was null for node=" + node);
                if (!node.getParent().equals(current))
                    throw new TreeParentMistmatchStructuralException(node, node.getParent(), current);
                if (node.getParent() != current)
                    throw new TreeParentMistmatchStructuralException(node, node.getParent(), current);
                node.validate();
                validate(tree, node, visited, requireNonLeafNodesHaveLeaves);
            }
        }
    }

    /**
     * Verify that the tree is structurally valid.  This method checks parent-child relationships and ensures
     * that (1) every child points to a parent (2) every parent points to its children.
     * @param tree
     */
    static void validate(final DNFConvTree tree, final boolean topMustBeRoot, final boolean requireNonLeafNodesHaveLeaves) throws TreeStructuralException {
        if (tree == null) throw new IllegalArgumentException("tree was null");

        final Node top = tree.getRoot();
        if (top == null) throw new IllegalStateException("top was null");

        // check validity of root
        if (topMustBeRoot)
            if (top.getParent() != null) throw new TreeStructuralException("root had a parent");

        // check everything else (recursively)
        validate(tree, top, new HashSet<Node>(), requireNonLeafNodesHaveLeaves);
    }

    static void validate(final Node top, final boolean topMustBeRoot, final boolean requireNonLeafNodesHaveLeaves) throws TreeStructuralException {
        if (top == null) throw new IllegalArgumentException("top was null");

        DNFConvTree tree = new DNFConvTree(top);
        validate(tree, topMustBeRoot, requireNonLeafNodesHaveLeaves);
    }

}
