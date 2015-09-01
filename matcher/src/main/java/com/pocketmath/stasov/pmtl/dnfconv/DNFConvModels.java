package com.pocketmath.stasov.pmtl.dnfconv;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by etucker on 8/17/15.
 */
public class DNFConvModels {

    static interface Leaf {

    }

    /**
     * Note: Parents are weak references while children if any are strong references.
     */
    public static abstract class Node implements Cloneable {
        //private String text;

        private static AtomicLong idSequence = new AtomicLong(1L);

        private final long id;

        private WeakReference<Node> parent = null;

        /**
         *
         * @param parent
         * @param addAsChild If true add this newly created node to the parent as a child.
         */
        protected Node(Node parent, boolean addAsChild) {
            if (parent == null && addAsChild) throw new IllegalArgumentException("parent cannot be null with addAsChild enabled");
            if (parent != null) {
                this.parent = new WeakReference<Node>(parent);
                if (addAsChild) parent.addChild(this);
            }
            this.id = idSequence.getAndIncrement();
        }

        /**
         * By default adds this newly created node as a child.
         * @param parent
         */
        protected Node(Node parent) {
            this(parent, false);
        }

        public long getId() {
            return id;
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

        public abstract void removeChild(Node child, boolean silentOnNotExists) throws UnsupportedOperationException;

        public void removeChild(Node child) throws UnsupportedOperationException {
            removeChild(child, false);
        }

        public abstract void removeAllChildren() throws UnsupportedOperationException;

        public abstract Set<Node> getChildren() throws UnsupportedOperationException;

        public abstract boolean containsChild(final Node child);

        @Override
        public abstract Object clone();

        public abstract void validate() throws ModelStructuralException;

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

        public String toPocketTL() {
            return prettyString();
        }

        @Override
        public String toString() {
            return "id=" + id;
        }
/*
        @Override
        public boolean equals(Object o) {
            //System.out.println("equals = " + o + ";  THIS ==== " + this);
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(getId(), node.getId());
        }

        @Override
        public int hashCode() {
            //System.out.println("h = " + Objects.hash(getId()) + "  THIS ===== " + this);
            return Objects.hash(getId());
        }
        */
    }

    static abstract class NodeWithChildren<T extends NodeWithChildren> extends Node {
        private Set<Node> children = new ObjectArraySet<Node>();  //new HashSet<Node>();

        /**
         * Copy constructor.
         *
         * @param a
         * @param parent
         * @param addAsChild
         */
        protected NodeWithChildren(T a, Node parent, boolean addAsChild) {
            super(parent, addAsChild);
            for (Object c : a.getChildren()) {
                Node newChild = (Node)((Node)c).clone();
                newChild.setParent(this);
                addChild(newChild);
            }
        }

        protected NodeWithChildren(Node parent, boolean addAsChild) {
            super(parent, addAsChild);
        }

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
        public void removeChild(Node child, boolean silentOnNotExists) {
            if (child == null) throw new IllegalArgumentException("child was null");
            if (children.contains(child)) children.remove(child);
            else if (!silentOnNotExists) throw new IllegalArgumentException("attempt to remove child that was not a child of this node");
        }

        @Override
        public void removeAllChildren() throws UnsupportedOperationException {
            children.clear();
        }

        public void addChildren(Collection<Node> children) { this.children.addAll(children); }

        @Override
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
            //System.out.println("containsChild: " + child + " children ==== " + children);
            if (children.isEmpty()) return false;
            return children.contains(child);
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
                    '}' + super.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeWithChildren<?> that = (NodeWithChildren<?>) o;
            return Objects.equals(getChildren(), that.getChildren());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getChildren());
        }

        @Override
        public void validate() throws ModelStructuralException {
            // TODO
        }
    }

    static abstract class NodeWithChild<T extends NodeWithChild> extends Node {
        private Node child;
        private Set<Node> set = new ObjectArraySet<Node>(1);

        /**
         * Copy constructor.
         *
         * @param a
         * @param parent
         * @param addAsChild
         */
        protected NodeWithChild(T a, Node parent, boolean addAsChild) {
            this(parent, (Node) a.getChild().clone(), addAsChild);
            this.getChild().setParent(this);
        }

        NodeWithChild(Node parent, Node child, boolean addAsChild) {
            super(parent, addAsChild);
            if (child != null) addChild(child);
        }

        public NodeWithChild(Node parent, boolean addAsChild) {
            this(parent, null, addAsChild);
        }

        public NodeWithChild(Node parent) { this(parent, null, true); }

        public Node getChild() {
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            return child;
        }

        @Override
        public void addChild(Node child) {
            assert(this.child != null && set.size() == 1 || this.child == null && set.isEmpty());
            if (child == null) throw new IllegalArgumentException("parameter child was null");
            if (this.child != null) throw new IllegalStateException("attempt to add child when maximum number of children (1) already exists");
            this.child = child;
            this.set.clear();
            this.set.add(child);
            assert(child != null && set.size() == 1);
        }

        @Override
        public void removeChild(Node child, boolean silentOnNotExists) {
            if (child == null) throw new IllegalArgumentException("parameter child was null");
            assert(child != null && set.size() == 1 || child == null && set.isEmpty());
            if (child.equals(this.child)) {
                this.child = null;
                set.clear();
            } else {
                if (!silentOnNotExists) throw new IllegalArgumentException("attempt to remove child that was not a child of this node");
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
            if (!(this.child != null && set.size() == 1 || this.child == null && set.isEmpty())) {
                throw new IllegalStateException("child=" + this.child + "; set.size()=" + set.size());
            }
            this.child = child;
            set.clear();
            set.add(child);
        }

        @Override
        public boolean containsChild(final Node child) {
            if (child == null) throw new IllegalArgumentException("input was null");
            if (child == null && !set.isEmpty()) throw new IllegalStateException();
            if (this.child == null) return false;
            if (this.child == child) return true;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NodeWithChild)) return false;

            NodeWithChild<?> that = (NodeWithChild<?>) o;

            if (getChild() != null ? !getChild().equals(that.getChild()) : that.getChild() != null) return false;
            return !(set != null ? !set.equals(that.set) : that.set != null);

        }

        @Override
        public int hashCode() {
            int result = getChild() != null ? getChild().hashCode() : 0;
            result = 31 * result + (set != null ? set.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "child=" + child + super.toString();
        }

        @Override
        public void validate() throws ModelStructuralException {
            if (!(child != null && set.size() == 1 || child == null && set.isEmpty())) throw new ModelStructuralException(this, "child=" + child + "; set.size()=" + set.size());
        }
    }
    /*
        static class ParenthesizedNode extends NodeWithChild {
            public ParenthesizedNode(Node parent, Node child) {
                super(parent, child);
            }
        }
    */
    public static class AndNode extends NodeWithChildren<AndNode> {
        public AndNode(Node parent, boolean addAsChild) {
            super(parent, addAsChild);
        }

        public AndNode(Node parent) {
            super(parent);
        }

        protected AndNode(AndNode a, Node parent, boolean addAsChild) {
            super(a, parent, addAsChild);
        }

        @Override
        public Object clone() {
            return new AndNode(this, null, false);
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

    public static class OrNode extends NodeWithChildren<OrNode> {
        public OrNode(Node parent, boolean addAsChild) {
            super(parent, addAsChild);
        }

        public OrNode(Node parent) {
            super(parent);
        }

        protected OrNode(OrNode a, Node parent, boolean addAsChild) {
            super(a, parent, addAsChild);
        }

        @Override
        public Object clone() {
            return new OrNode(this, null, false);
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

    public static class NotNode extends NodeWithChild<NotNode> {
        public NotNode(Node parent) {
            super(parent, false);
        }

        protected NotNode(NotNode a, Node parent, boolean addAsChild) {
            super(a, parent, addAsChild);
        }

        @Override
        public Object clone() {
            return new NotNode(this, null, false);
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

        @Override
        public Object clone() {
            final InNode node = new InNode(null);
            node.variableName = this.variableName;
            node.positiveValues.addAll(this.positiveValues);
            node.negativeValues.addAll(this.negativeValues);
            try {
                node.validate();
            } catch (ModelStructuralException e) {
                throw new IllegalStateException(e);
            }
            return node;
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
                    if (posChild instanceof CharSequence)
                        out.print("\"" + posChild + "\"");
                    else
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
                    if (negChild instanceof CharSequence)
                        out.print("\"" + negChild + "\"");
                    else
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
        public void removeChild(Node child, boolean silentOnNotExists) throws UnsupportedOperationException {
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
        public boolean containsChild(Node child) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InNode inNode = (InNode) o;

            if (getVariableName() != null ? !getVariableName().equals(inNode.getVariableName()) : inNode.getVariableName() != null)
                return false;
            if (getPositiveValues() != null ? !getPositiveValues().equals(inNode.getPositiveValues()) : inNode.getPositiveValues() != null)
                return false;
            return !(negativeValues != null ? !negativeValues.equals(inNode.negativeValues) : inNode.negativeValues != null);

        }

        @Override
        public int hashCode() {
            int result = getVariableName() != null ? getVariableName().hashCode() : 0;
            result = 31 * result + (getPositiveValues() != null ? getPositiveValues().hashCode() : 0);
            result = 31 * result + (negativeValues != null ? negativeValues.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "InNode{" +
                    "var='" + variableName + '\'' +
                    ", neg=" + TreeUtil.idTypesToString(negativeValues) +
                    ", pos=" + TreeUtil.idTypesToString(positiveValues) +
                    "} ";
        }

        @Override
        public void validate() throws ModelStructuralException {
            if (negativeValues == null) throw new ModelStructuralException();
            if (positiveValues == null) throw new ModelStructuralException();
            if (negativeValues.isEmpty() && positiveValues.isEmpty()) throw new ModelStructuralException();
            if (variableName == null) throw new ModelStructuralException();
            if (variableName.isEmpty()) throw new ModelStructuralException();
        }
    }

}
