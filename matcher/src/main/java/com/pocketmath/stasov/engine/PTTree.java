package com.pocketmath.stasov.engine;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by etucker on 5/1/15.
 */
public class PTTree {

    public static interface Leaf {

    }

    public static abstract class Node {
        private String text;

        private Node parent;

        protected Node(Node parent) {
            this.parent = parent;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public abstract boolean hasChildren();

        public abstract void addChild(Node child) throws UnsupportedOperationException;

        public abstract void removeChild(Node child) throws UnsupportedOperationException;

        public abstract Set<Node> getChildren() throws UnsupportedOperationException;

        public abstract void prettyPrint(PrintWriter out);
    }

    public static abstract class NodeWithChildren extends Node {
        private Set<Node> children = new HashSet<Node>();

        /**
         *
         * @param parent Null parents are permitted but not recommended.
         */
        protected NodeWithChildren(Node parent) {
            super(parent);
        }

        @Override
        public void addChild(Node child) {
            if (child == null) throw new IllegalArgumentException("child was null");
            children.add(child);
        }

        @Override
        public void removeChild(Node child) {
            if (child == null) throw new IllegalArgumentException("child was null");
            if (children.contains(child)) children.remove(child);
            else throw new IllegalArgumentException("attempt to remove child that was not a child of this node");
        }

        public void addChildren(Collection<Node> children) { this.children.addAll(children); }

        public Set<Node> getChildren() {
            return children;
        }

        @Override
        public boolean hasChildren() {
            return children != null && ! children.isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            NodeWithChildren that = (NodeWithChildren) o;

            return getChildren().equals(that.getChildren());

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + getChildren().hashCode();
            return result;
        }

        protected void prettyPrint(PrintWriter out, String separator, boolean useParens) {
            if (useParens) out.print('(');
            Node child = null;
            final Iterator<Node> itr = getChildren().iterator();
            while (itr.hasNext()) {
                child = itr.next();
                child.prettyPrint(out);
                if (itr.hasNext())
                    out.print(separator);
            }
            if (useParens) out.print(')');
        }

        @Override
        public String toString() {
            return "{" +
                    children +
                    '}';
        }
    }

    public static abstract class NodeWithChild extends Node {
        private Node child;
        private Set<Node> set = new ObjectArraySet<Node>(1);

        public NodeWithChild(Node parent, Node child) {
            super(parent);
            this.child = child;
            this.set.add(child);
        }

        public NodeWithChild(Node parent) {
            this(parent, null);
        }

        public Node getChild() {
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            return child;
        }

        @Override
        public void addChild(Node child) {
            if (child == null) throw new IllegalArgumentException("parameter child was null");
            if (this.child != null) throw new IllegalStateException("attempt to add child when maximum number of children (1) already exists");
            this.child = child;
            this.set.clear();
            this.set.add(child);
            assert(child != null && set.size() == 1);
        }

        @Override
        public void removeChild(Node child) {
            if (child == null) throw new IllegalArgumentException("parameter child was null");
            if (child.equals(this.child)) {
                this.child = null;
                set.clear();
            } else {
                throw new IllegalArgumentException("attempt to remove child that was not a child of this node");
            }
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
        }

        public void setChild(Node child) {
            if (child == null) throw new IllegalArgumentException("parameter child was null");
            this.child = child;
        }

        @Override
        public boolean hasChildren() {
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            return child != null;
        }

        @Override
        public Set<Node> getChildren() {
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            return set;
        }

        @Override
        public void prettyPrint(PrintWriter out) {
            child.prettyPrint(out);
        }
    }
/*
    static class ParenthesizedNode extends NodeWithChild {
        public ParenthesizedNode(Node parent, Node child) {
            super(parent, child);
        }
    }
*/
    public static class AndNode extends NodeWithChildren {
        public AndNode(Node parent) {
            super(parent);
        }

        @Override
        public void prettyPrint(PrintWriter out) {
            if (getChildren().size() > 1) {
                System.out.println("AND, CHILDREN > 1: " + this);
            }
            prettyPrint(out, " AND ", false);
        }

        @Override
        public String toString() {
            return "And " + super.toString();
        }
    }

    public static class OrNode extends NodeWithChildren {
        public OrNode(Node parent) {
            super(parent);
        }

        @Override
        public void prettyPrint(PrintWriter out) {
            prettyPrint(out, " OR ", getChildren().size() > 1);
        }

        @Override
        public String toString() {
            return "OrNode{} " + super.toString();
        }
    }

    public static class NotNode extends NodeWithChild {
        public NotNode(Node parent) {
            super(parent);
        }

        @Override
        public void removeChild(Node child) {
            throw new UnsupportedOperationException();
        }
    }

    public static abstract class AtomNode extends Node {
        public AtomNode(Node parent) {
            super(parent);
        }
    }

    public static class InNode extends Node implements Leaf {
        private String variableName;
        private final Set positiveValues;
        private final Set negativeValues;

        public InNode(Node parent, String variableName, Set positiveValues, Set negativeValues) {
            super(parent);
            this.negativeValues = negativeValues;
            this.variableName = variableName;
            this.positiveValues = positiveValues;
        }

        public InNode(Node parent) {
            super(parent);
            this.variableName = null;
            this.positiveValues = new TreeSet();
            this.negativeValues = new TreeSet();
        }

        public Set getPositiveValues() {
            return positiveValues;
        }

        public String getVariableName() {
            return variableName;
        }

        @Override
        public void prettyPrint(PrintWriter out) {
            // print positive values
            if (! positiveValues.isEmpty()) {
                Object posChild = null;
                out.print(variableName + " IN (");
                final Iterator posItr = positiveValues.iterator();
                while(posItr.hasNext()) {
                    posChild = posItr.next();
                    out.print(posChild);
                    if (posItr.hasNext())
                        out.print(", ");
                }
                out.print(')');
            }

            if (! positiveValues.isEmpty() && ! negativeValues.isEmpty()) {
                out.print(" AND ");
            }

            // print negative values
            if (! negativeValues.isEmpty()) {
                Object negChild = null;
                out.print("NOT " + variableName + " IN (");
                final Iterator negItr = negativeValues.iterator();
                while(negItr.hasNext()) {
                    negChild = negItr.next();
                    out.print(negChild);
                    if (negItr.hasNext())
                        out.print(", ");
                }
                out.print(')');
            }
        }

        public void setVariableName(final String variableName) { this.variableName = variableName; }

        public void addPositiveValue(final Object value) { positiveValues.add(value); }

        public void addPositiveValues(final Collection values) { positiveValues.addAll(values); }

        public void addNegativeValue(final Object value) {
            negativeValues.add(value);
        }

        public void addNegativeValues(final Collection values) { negativeValues.addAll(values); }

        @Override
        public void addChild(Node child) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasChildren() {
            return false;
        }

        @Override
        public void removeChild(Node child) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Node> getChildren() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "InNode{" +
                    "var='" + variableName + '\'' +
                    ", neg=" + PTTreeUtil.idTypesToString(negativeValues) +
                    ", pos=" + PTTreeUtil.idTypesToString(positiveValues) +
                    "} ";
        }
    }

    private Node root;

    public PTTree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    private boolean isRoot(final Node root) {
        return this.root == root;
    }

    /**
     *
     * @param node
     * @return the topmost node after any
     */
    private Node convertNot(final NotNode node, final State state) {
        final Node parent = node.getParent();

        if (parent instanceof AndNode) {
            // do nothing
        } else if (parent instanceof OrNode) {
            // do nothing
        } else if (parent instanceof NotNode) { // case: double negation
            if (parent != root) {
                // double negation found, remove both intermediate NOTs
                final NotNode notNode = (NotNode) parent;
                Node grandparent = parent.getParent();

                if (grandparent == root) return parent;

                node.getChild().setParent(grandparent);
                state.setModified(true);
                return grandparent;
            }
        }

        return null;
    }
/*
    private Node convertParenthesized(final ParenthesizedNode node) {
        final Node parent = node.getParent();

        // case: double paren
        if (parent instanceof ParenthesizedNode) {
            Node child = node.getChild();
            child.setParent(parent);
            ((ParenthesizedNode) parent).setChild(child);
            return parent;
        }

    }
*/

    private Node convertAnd(final AndNode node, final State state) {
        final Node parent = node.getParent();

        if (parent instanceof AndNode) { // de-dup
            // rollup
            for (Node child : node.getChildren()) {
                ((AndNode)parent).addChild(child);
                child.setParent(parent);
                state.setModified(true);
            }
            return parent;

        } else if (parent instanceof OrNode) {
            return parent;

        } else if (parent instanceof NotNode) {
            final Node grandparent = parent.getParent();
            final OrNode newOrNode = new OrNode(grandparent);
            for (final Node andChild : node.getChildren()) {
                final NotNode newNotNode = new NotNode(newOrNode);
                newNotNode.setChild(andChild);
            }
            if (newOrNode.hasChildren()) grandparent.addChild(newOrNode);
            grandparent.removeChild(parent);
            state.setModified(true);
            return grandparent;

        } else {
            throw new IllegalStateException();
        }

    }
    
    private Node convertOr(final OrNode node, final State state) {

        final Node parent = node.getParent();

        // all ors multiply
        //  ( a + b ) cd   ==> acd + bcd
        //
        if (parent instanceof AndNode) {
            final OrNode newOrNode = new OrNode(parent);

            for (final Node orChild : node.getChildren()) { // all the OrNode children
                final AndNode newAndNode = new AndNode(newOrNode);
                newAndNode.addChild(orChild);
                for (final Node andChild : ((AndNode) parent).getChildren()) {
                    if (!andChild.equals(orChild)) {
                        newAndNode.addChild(andChild);
                    }
                }
                if (newAndNode.hasChildren()) {
                    newOrNode.addChild(newAndNode);
                }
            }

            state.setModified(true);
            return newOrNode;

        } else if (parent instanceof OrNode) { // de-dup
            for (final Node orChild : node.getChildren()) {
                if (!orChild.equals(node)) {
                    ((OrNode) parent).addChild(orChild);
                    orChild.setParent(parent);
                }
            }
            state.setModified(true);
            return parent;

        } else if (parent instanceof NotNode) {
            final Node grandparent = parent.getParent();
            final AndNode newAndNode = new AndNode(grandparent);
            for (final Node orChild : node.getChildren()) {
                final NotNode newNotNode = new NotNode(newAndNode);
                newNotNode.setChild(orChild);
            }
            if (newAndNode.hasChildren()) grandparent.addChild(newAndNode);
            grandparent.removeChild(parent);
            state.setModified(true);
            return grandparent;

        } else {
            throw new IllegalStateException();
        }
    }

    private static final int MAX_CYCLES = 1024;

    private Node convertFromLeaf(final Node leaf, final State state) {
        if (!(leaf instanceof Leaf)) throw new IllegalArgumentException("leaf did not implement interface Leaf");

        Node currentNode = leaf;

        int i;
        for (i = 0; i < MAX_CYCLES; i++) {

            if (currentNode == currentNode.getParent())
                throw new IllegalStateException();

            if (isRoot(currentNode))
                return leaf;

            if (currentNode instanceof AndNode) {
                currentNode = convertAnd((AndNode) currentNode, state);
            } else if (currentNode instanceof OrNode) {
                currentNode = convertOr((OrNode) currentNode, state);
            } else if (currentNode instanceof NotNode) {
                currentNode = convertNot((NotNode) currentNode, state);
            } else if (currentNode instanceof Leaf) {
                currentNode = currentNode.getParent();
            } else {
                throw new UnsupportedOperationException();
            }
        }
        if (i >= MAX_CYCLES) throw new IllegalStateException("max cycles exceeded");

        throw new IllegalStateException("unable to navigate or transform tree");
    }

    private static class State {
        private boolean modified = false;

        public boolean isModified() {
            return modified;
        }

        public void setModified(boolean modified) {
            this.modified = modified;
        }

        public void reset() {
            modified = false;
        }
    }

    private void convertFromLeaves(final Node top, final State state) {
        if (top instanceof Leaf) {
            convertFromLeaf(top, state);
        } else {
            if (top.hasChildren()) {
                for (Node child : top.getChildren()) { // TODO: Optimize O(n^2) performance
                    convertFromLeaves(child, state);
                    if (state.isModified()) return;
                }
            } else {
                throw new IllegalStateException("non-leaf node with no children, node=" + top);
            }
        }
    }

    public void convert() {
        final State state = new State();
        do {
            state.reset();
            convertFromLeaves(root, state);
        } while (state.isModified());
    }

    public static void convert(Node root) {
        PTTree tree = new PTTree(root);
        tree.convert();
    }

    public static void main(String args[]) {
        PTTree tree = new PTTree(new AndNode(null));
        tree.convert();
        Node convertedRoot = tree.getRoot();
    }

}
