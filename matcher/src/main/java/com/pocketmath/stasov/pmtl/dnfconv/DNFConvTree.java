package com.pocketmath.stasov.pmtl.dnfconv;

import com.pocketmath.stasov.pmtl.dnfconv.DNFConvModels.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by etucker on 5/1/15.
 */
class DNFConvTree {

    private final boolean validationOn = true;

    private static Logger logger = Logger.getLogger(DNFConvTree.class.getName());
    static {
        // shameless hard coded logging setup

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.WARNING);

        logger.setLevel(Level.FINEST);
        logger.addHandler(consoleHandler);
    }

    private static Logger transformationLogger = Logger.getLogger(DNFConvTree.class.getName() + "_TRANSFORMATION");
    static {

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);

        transformationLogger.setLevel(Level.FINEST);
        transformationLogger.addHandler(consoleHandler);

    }

    private Node root;

    DNFConvTree(final Node root) {
        this.root = root;
    }

    Node getRoot() {
        return root;
    }

    public boolean isRoot(final Node node) {
        if (node == null) throw new IllegalArgumentException("argument root was null");
        if (node.getParent() == null) {
            if (this.root != node) throw new IllegalStateException();
            return true;
        } else {
            if (this.root == node) throw new IllegalStateException("node marked as root had parent.  node=" + node + "; parent=" + node.getParent());
            if (this.root.equals(node)) throw new IllegalStateException("node marked as root had parent.  node=" + node + "; parent=" + node.getParent());
            return false;
        }
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
        if (node == null) throw new IllegalArgumentException();
        if (state == null) throw new IllegalArgumentException();
        validate(node, false, true);

        logger.entering(getClass().getName(), "convertNot");

        final Node parent = node.getParent();
        validate(parent, false, true);

        if (! node.hasChildren()) {
            logger.log(Level.WARNING, "input node with no children; will disregard and use parent");
            return parent;
        }

        if (!parent.containsChild(node)) {
            throw new IllegalStateException("parent did not contain the child it was derived from.\n\rparent = " + parent + "\n\rchild = " + node);
        }

        if (parent instanceof AndNode) {
            return parent;
        } else if (parent instanceof OrNode) {
            return parent;
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

        throw new IllegalStateException();
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
        logger.entering(getClass().getName(), "convertAnd");

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
            final OrNode newOrNode = new OrNode(grandparent, false);
            for (final Node andChild : node.getChildren()) {
                final NotNode newNotNode = new NotNode(newOrNode);
                andChild.setParent(newNotNode);
                newNotNode.setChild(andChild);
                newOrNode.addChild(newNotNode);
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
        logger.entering(getClass().getName(), "convertOr");
        validate();

        final Node parent = node.getParent();

        {
            final Object[] logParams = {node, parent};
            logger.log(Level.FINER, "input node = {0}, parent = {1}", logParams);
        }
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

            final Node n0 = parent.getParent();
            final Node and = parent; // and node
            final Node or = node; // or node

         //   for (Node node1 : n2.getChildren()) {
         //
         //   }

            validate();

            final Node newOrNode = new OrNode(n0);

            //System.out.println("n0=" + n0);

            n0.addChild(newOrNode);

            n0.removeChild(and); // remove the old 'and' node from the grandparent

            //System.out.println("n0=" + n0);

            //validate();

            for (final Object orChildObj : or.getChildren().toArray()) {
                Node orChild = (Node) orChildObj;
                //final Set<Node> n1ChildrenToRemove = new HashSet<Node>();

                //validate();

                final Node newAndNode = new AndNode(newOrNode, false);
                newAndNode.setParent(newOrNode);
                newOrNode.addChild(newAndNode);

                {
                    //System.out.println("root=" + root);

                    final Node orChildClone = (Node) orChild.clone();
                    newAndNode.addChild(orChildClone);
                    orChildClone.setParent(newAndNode);

                    logger.log(Level.FINEST, "added orChildClone={0}", orChildClone);

                    validate(orChildClone, false, true);

                    validate();

                    if (orChildClone.hasChildren()) {
                        //if (!(orChildClone instanceof Leaf)) {
                        orChildClone.removeChild(and, true);

                        for (Node n : orChildClone.getChildren()) {
                            //if (n instanceof InNode) {
                            n.setParent(orChildClone);
                            //}
                            validate(n);
                        }
                    }

                    validate(orChildClone);
                    validate(root);
                    validate(getRoot());

                    validate();
                }

                if (and.getChildren().size() == 1 && and.containsChild(or)) continue;

                int i = 0;

                for (final Object andChildObj : and.getChildren().toArray()) {
                    Node andChild = (Node) andChildObj;

                    if (andChild.equals(or)) continue;

                    //validate();

                    //System.out.println("it's okay! " + i++);

                    {
                        final Node andChildClone = (Node) andChild.clone();
                        newAndNode.addChild(andChildClone);
                        andChildClone.setParent(newAndNode);

                        if (andChildClone.hasChildren()) {
                        //if (!(andChildClone instanceof Leaf)) {
                            andChildClone.removeChild(or, true);

                            for (Node n : andChildClone.getChildren()) {
                                //if (n instanceof InNode) {
                                n.setParent(andChildClone);
                                //}
                            }
                        }

                        validate();
                    }

                    //System.out.println("newAndNode: " + newAndNode);

                    //System.out.println("id= ," + "id=");

                    //n1ChildrenToRemove.add(or);
                    //newOrNode.addChild(newAndNode);
                    validate();
                }
                validate();
                //for (Node n1ChildToRemove : n1ChildrenToRemove) and.removeChild(n1ChildToRemove);
                //validate();
            }

            //System.out.println("root = " + getRoot());

            validate();
            return newOrNode;

/*

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
            //node.setParent(grandparent);

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

            //parent.removeAllChildren(); // old parent will be destroyed

            state.setModified(true);
            logOutputNode(topOrNode);

            validate();
            return topOrNode;
            */

        } else if (parent instanceof OrNode) { // de-dup
            validate();

            // Move all children of the child to the parent.
            // In other words, move the grandchildren to be the children of the parent.

            final Node n1 = parent;
            final Node n2 = node;

            logger.log(Level.FINEST, "parent instanceof OrNode (n2.getChildren().size()={0})", n2.getChildren().size());
            for (final Node n3 : n2.getChildren()) {
                logger.log(Level.FINEST, "n3: " + n3);
                if (!(n3 instanceof Leaf) && !n3.hasChildren()) throw new IllegalStateException(new TreeStructuralException());
                n1.addChild(n3);
                n3.setParent(n1);
            }
            if (!n1.containsChild(n2)) throw new IllegalStateException(new TreeStructuralException());
            {
                final Object[] logParams = new Object[]{n1, n2};
                logger.log(Level.FINEST, "n1: {0}; n2: {1}", logParams);
            }
            n1.removeChild(n2);
            if (!n1.hasChildren()) {
                if (isRoot(n1)) {
                    throw new IllegalStateException("empty root");
                } else {
                    final Node n0 = n1.getParent();
                    n0.removeChild(n1);
                    if (isRoot(n0) && !n0.hasChildren()) throw new IllegalStateException("empty root");
                }
            }
            n2.removeAllChildren();
            if (n2.hasChildren()) throw new IllegalStateException(new TreeStructuralException());

/*
            for (final Node orChild : node.getChildren()) {
                if (orChild != node) {
                    ((OrNode) parent).addChild(orChild);
                    orChild.setParent(parent);
                }
            }
            */
            state.setModified(true);
            logOutputNode(parent);

            validate();
            return parent;

        } else if (parent instanceof NotNode) {
            validate();

            logger.log(Level.FINEST, "parent instanceof NotNode");
            final Node grandparent = parent.getParent();
            final AndNode newAndNode = new AndNode(grandparent, false);
            for (final Node orChild : node.getChildren()) {
                final NotNode newNotNode = new NotNode(newAndNode);
                newAndNode.addChild(newNotNode);
                newNotNode.setChild(orChild);
                orChild.setParent(newNotNode);
            }
            if (newAndNode.hasChildren()) grandparent.addChild(newAndNode);
            grandparent.removeChild(parent);
            state.setModified(true);
            logOutputNode(grandparent);

            validate();
            return grandparent;

        } else {
            throw new IllegalStateException();
        }
    }

    private static final int MAX_CYCLES = 1024;

    private Node convertFromLeaf(final Node leaf, final State state) {
        if (leaf == null) throw new IllegalArgumentException("leaf was null");
        if (state == null) throw new IllegalArgumentException("state was null");
        if (!(leaf instanceof Leaf)) throw new IllegalArgumentException("leaf did not implement interface Leaf");
        validate();

        Node currentNode = leaf;

        int i;
        for (i = 0; i < MAX_CYCLES; i++) {
            validate();
            if (currentNode == null) throw new IllegalStateException();

            if (logger.isLoggable(Level.FINEST)) {
                //final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                final List<Object> logParams =
                        Lists.<Object>asList(Integer.valueOf(i), currentNode.prettyString(), new Object[]{});
                logger.log(Level.FINEST, "current node [{0}]", logParams);
            }

            if (currentNode == currentNode.getParent())
                throw new IllegalStateException();

            if (isRoot(currentNode))
                return currentNode;

            if (currentNode instanceof AndNode) {
                currentNode = convertAnd((AndNode) currentNode, state);
                if (currentNode == null) throw new IllegalStateException();

            } else if (currentNode instanceof OrNode) {
                currentNode = convertOr((OrNode) currentNode, state);
                if (currentNode == null) throw new IllegalStateException();

            } else if (currentNode instanceof NotNode) {
                currentNode = convertNot((NotNode) currentNode, state);
                if (currentNode == null) throw new IllegalStateException();

            } else if (currentNode instanceof Leaf) {
                currentNode = currentNode.getParent();
                if (currentNode == null) throw new IllegalStateException();

            } else {
                throw new UnsupportedOperationException();
            }

            validate();
            if (currentNode == null) throw new IllegalStateException();
        }
        if (i >= MAX_CYCLES) throw new IllegalStateException("max cycles exceeded");

        throw new IllegalStateException("unable to navigate or transform tree");
    }

    private static class Call {
        private final Method method;
        private final Object[] params;

        public Call(Method method, Object[] params) {
            this.method = method;
            this.params = params;
        }
    }

    private static class History {
    }

    private void trackHistory() {
        
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
        validate();

        if (top instanceof Leaf) {
            convertFromLeaf(top, state);
        } else {
            if (top.hasChildren()) {
                for (final Node child : top.getChildren()) { // TODO: Optimize O(n^2) performance
                    validate();
                    convertFromLeaves(child, state);
                    validate();
                    if (state.isModified()) return;
                }
            } else {
                throw new IllegalStateException("non-leaf node with no children, node=" + top);
            }
        }

        validate();
    }

    public void transformToDNF() {
        validate();
        final State state = new State();
        int cycles = 0;
        do {
            if (cycles++ > MAX_CYCLES) throw new IllegalStateException("cycles exceeded");
            state.reset();
            convertFromLeaves(root, state);
            if (transformationLogger.isLoggable(Level.FINEST)) {
                transformationLogger.log(Level.FINEST, "intermediate transformation: {0}", toPocketTL());
            }
        } while (state.isModified());
        validate();
    }

    public String toPocketTL() {
        return getRoot().toPocketTL();
    }

    public void prettyPrint(final PrintWriter out) {
        getRoot().prettyPrint(out);
    }

    private void validate(final Node node) {
        if (!validationOn) return;
        try {
            if (!(node instanceof Leaf) && !node.hasChildren())
                throw new ValidationException("non-leaf node with no children; node=" + node);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.toPocketTL());
            throw new IllegalStateException(e);
        }
    }

    private void validate() {
        if (!validationOn) return;
        try {
            TreeValidator.validate(this, true, true);
        } catch (Exception e) {
            logger.log(Level.SEVERE, this.toPocketTL());
            throw new IllegalStateException(e);
        }
    }

    private void validate(final Node top, final boolean topMustBeRoot, final boolean requireNonLeafNodesHaveLeaves) {
        if (!validationOn) return;
        try {
            TreeValidator.validate(top, topMustBeRoot, requireNonLeafNodesHaveLeaves);
        } catch (TreeStructuralException e) {
            throw new IllegalStateException(e);
        }
    }

}