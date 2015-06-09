package com.pocketmath.stasov.engine;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by etucker on 5/1/15.
 */
public class PTTree {

    private Logger logger = Logger.getLogger(getClass().getName());
    {
        // shameless hard coded logging setup

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);

        logger.setLevel(Level.FINEST);
        logger.addHandler(consoleHandler);

    }

    public static interface Leaf {

    }

    /**
     * Note: Parents are weak references while children if any are strong references.
     */
    public static abstract class Node {
        //private String text;

        private WeakReference<Node> parent = null;

        protected Node(Node parent) {
            if (parent != null) {
                this.parent = new WeakReference<Node>(parent);
                //parent.addChild(this);
            }
        }

        /**
         * Overriding methods must call super.
         * @return
         */
        public Node getParent() {
            return parent == null ? null : parent.get();
        }

        /**
         * Overriding methods must call super.
         * @param parent
         */
        public void setParent(Node parent) {
            if (parent == null) throw new IllegalArgumentException("argument parent was null");
            this.parent = new WeakReference<Node>(parent);
        }

        public abstract boolean hasChildren();

        public abstract void addChild(Node child) throws UnsupportedOperationException;

        public abstract void removeChild(Node child) throws UnsupportedOperationException;

        public abstract void removeAllChildren() throws UnsupportedOperationException;

        public abstract Set<Node> getChildren() throws UnsupportedOperationException;

        public boolean containsChild(final Node child) { return false; }

        public abstract void prettyPrint(PrintWriter out);

        protected String prettyString() {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            prettyPrint(pw);
            pw.flush();
            sw.flush();
            try {
                sw.close();
            } catch (IOException e) {
                e.printStackTrace(); // TODO: Refine exception handling
                throw new IllegalStateException(e);
            }
            pw.close();
            return sw.toString();
        }
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

        @Override
        public void removeAllChildren() throws UnsupportedOperationException {
            children.clear();
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
        public boolean containsChild(final Node child) {
            if (child == null) throw new IllegalArgumentException("input was null");
            if (children.isEmpty()) return false;
            return children.contains(child);
        }

/*        @Override
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
        }*/

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
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
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
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            if (child.equals(this.child)) {
                this.child = null;
                set.clear();
            } else {
                throw new IllegalArgumentException("attempt to remove child that was not a child of this node");
            }
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
        }

        @Override
        public void removeAllChildren() throws UnsupportedOperationException {
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            child = null;
            set.clear();
        }

        public void setChild(Node child) {
            if (child == null) throw new IllegalArgumentException("parameter child was null");
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            this.child = child;
            set.clear();
            set.add(child);
        }

        @Override
        public boolean containsChild(final Node child) {
            if (child == null) throw new IllegalArgumentException("input was null");
            if (this.child == null) return false;
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            return child.equals(this.child);
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
            //if (getChildren().size() > 1) {
            //    System.out.println("AND, CHILDREN > 1: " + this);
            //}
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

        @Override
        public void prettyPrint(PrintWriter out) {
            out.print(" NOT ");
            getChild().prettyPrint(out);
        }

        @Override
        public String toString() {
            return "NotNode{} " + super.toString();
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
        public void removeAllChildren() throws UnsupportedOperationException {
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

    public PTTree(final Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    private boolean isRoot(final Node root) {
        return root.getParent() == null;
    }

    protected final void logNode(final Node node, final Level level, final String msgPrefix) {

        if (!logger.isLoggable(level)) return;

        final LogRecord logRecord = new LogRecord(level, msgPrefix + node.prettyString());

        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        StackTraceElement ste = null;
        for (StackTraceElement stackTraceElement: stackTrace) {
            final String className = stackTraceElement.getClassName();
            final String methodName = stackTraceElement.getMethodName();
            if (stackTraceElement.getMethodName().endsWith("getStackTrace")
                ||
                    className.equals(this.getClass().getCanonicalName())
                    &&
                    (methodName.endsWith("logOutputNode")
                    || methodName.endsWith("logInputNode")
                    || methodName.endsWith("logNode"))) {

                // do nothing
            } else {
                ste = stackTraceElement;
                break;
            }
        }

        if (ste == null) throw new IllegalStateException();

        final String className = ste.getClassName();
        final String methodName = ste.getMethodName();

        logRecord.setSourceClassName(className);
        logRecord.setSourceMethodName(methodName);

        logger.log(logRecord);
    }

    private void logOutputNode(final Node node) {
        logNode(node, Level.FINER, "out node = ");
    }

    private void logInputNode(final Node node) {
        logNode(node, Level.FINER, "in node = " );
    }

    /**
     *
     * @param node
     * @return the topmost node after any
     */
    private Node convertNot(final NotNode node, final State state) {
        final Node parent = node.getParent();

        if (! node.hasChildren()) {
            logger.log(Level.WARNING, "input node with no children; will disregard and use parent");
            return parent;
        }

        if (!parent.containsChild(node)) {
            throw new IllegalStateException("parent did not contain the child it was derived from.\n\rparent = " + parent + "\n\rchild = " + node);
        }

        if (parent instanceof AndNode) {
            // do nothing
        } else if (parent instanceof OrNode) {
            // do nothing
        } else if (parent instanceof NotNode) { // case: double negation
            if (!isRoot(parent)) {
                // double negation found, remove both intermediate NOTs
                final NotNode notNode = (NotNode) parent;
                Node grandparent = parent.getParent();

                if (grandparent == root) return parent;

                node.getChild().setParent(grandparent);
                grandparent.addChild(node.getChild());

                grandparent.removeChild(parent);
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

        Object[] logParams = { node, parent };
        logger.log(Level.FINER, "node = {0}, parent = {1}", logParams);

        if (! node.hasChildren()) {
            logger.log(Level.WARNING, "input node with no children; will disregard and use parent");
            return parent;
        }

        if (!parent.containsChild(node)) {
            throw new IllegalStateException("parent did not contain the child it was derived from.\n\rparent = " + parent + "\n\rchild = " + node);
        }

        if (parent instanceof AndNode) { // de-dup
            // rollup
            for (final Node child : node.getChildren()) {
                ((AndNode)parent).addChild(child);
                child.setParent(parent);
                state.setModified(true);
            }
            parent.removeChild(node);
            logger.log(Level.FINER, "parent of AndNode: output node = {0}", parent);
            return parent;

        } else if (parent instanceof OrNode) {
            logger.log(Level.FINER, "parent of OrNode: output node = {0}", parent);
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
            logger.log(Level.FINER, "parent of NotNode: output node = {0}", grandparent);
            return grandparent;

        } else {
            throw new IllegalStateException();
        }

    }
    
    private Node convertOr(final OrNode node, final State state) {

        final Node parent = node.getParent();

        Object[] logParams = { node, parent };
        logger.log(Level.FINER, "input node = {0}, parent = {1}", logParams);
        logInputNode(node);

        if (! node.hasChildren()) {
            logger.log(Level.WARNING, "input node with no children; will disregard and use parent");
            return parent;
        }

        if (!parent.containsChild(node)) {
            if (isRoot(parent)) logger.log(Level.SEVERE, "found root node but it didn't contain the child");
            logger.log(Level.SEVERE, "parent did not contain child it was derived from");
            throw new IllegalStateException("parent did not contain the child it was derived from.\n\rparent = " + parent + "\n\rchild = " + node);
        }

        // all ors multiply
        //
        if (parent instanceof AndNode) {
            //  (a + b) c ===> ac + bc
            logger.log(Level.FINEST, "parent instanceof AndNode");

            // TODO: check we are in proper form? -- if so, don't modify!   Is this covered by at-root checking in method convert(...)?

            final Node grandparent = parent.getParent();

            final OrNode topOrNode;
            if (grandparent instanceof OrNode) {
                topOrNode = (OrNode) grandparent;
            } else {
                topOrNode = new OrNode(grandparent);
                grandparent.addChild(topOrNode);
            }

            grandparent.removeChild(parent);

            parent.removeChild(node);
            node.setParent(grandparent);

            for (final Node l0 : node.getChildren()) {
                final AndNode newAndNode = new AndNode(topOrNode);
                topOrNode.addChild(newAndNode);
                newAndNode.addChild(l0);
                l0.setParent(newAndNode);

                for (final Node l1 : parent.getChildren()) {
                    assert(l1 != null);
                    assert(l1 != node);
                    assert(!l1.equals(node));
                    newAndNode.addChild(l1);
                    l1.setParent(newAndNode);
                }
            }

            state.setModified(true);
            logOutputNode(topOrNode);
            //logger.log(Level.FINER, "output node = {0}", newOrNode);
            return topOrNode;

        } else if (parent instanceof OrNode) { // de-dup
            logger.log(Level.FINEST, "parent instanceof OrNode");
            for (final Node orChild : node.getChildren()) {
                if (!orChild.equals(node)) {
                    ((OrNode) parent).addChild(orChild);
                    orChild.setParent(parent);
                }
            }
            state.setModified(true);
            logOutputNode(parent);
            return parent;

        } else if (parent instanceof NotNode) {
            logger.log(Level.FINEST, "parent instanceof NotNode");
            final Node grandparent = parent.getParent();
            final AndNode newAndNode = new AndNode(grandparent);
            for (final Node orChild : node.getChildren()) {
                final NotNode newNotNode = new NotNode(newAndNode);
                newAndNode.addChild(newNotNode);
                newNotNode.setChild(orChild);
            }
            if (newAndNode.hasChildren()) grandparent.addChild(newAndNode);
            grandparent.removeChild(parent);
            state.setModified(true);
            logOutputNode(grandparent);
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
        PTTree tree = new PTTree(new OrNode(null));
        tree.convert();
        Node convertedRoot = tree.getRoot();
    }

}
