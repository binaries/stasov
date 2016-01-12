package com.pocketmath.stasov.pmtl.dnfconv;

import com.pocketmath.stasov.pmtl.dnfconv.DNFConvModels.*;

import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderLexer;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderParser;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderParser.*;
import com.pocketmath.stasov.pmtl.PocketTLLanguageException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by etucker on 5/19/15.
 */
class TreeBuilder {

    private static Logger logger = Logger.getLogger(TreeBuilder.class.getName());
    static {
        // shameless hard coded logging setup

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.WARNING);

        logger.setLevel(Level.WARNING);
        logger.addHandler(consoleHandler);
    }

    private static final boolean notSupported = false;

    /**
     *
     * @param ctx
     * @param parent
     * @return An unconverted parse tree based upon the input grammar.
     * @throws PocketTLLanguageException
     */
    private static Node parse(final ParserRuleContext ctx, final Node parent) throws PocketTLLanguageException {
        if (ctx == null) throw new IllegalArgumentException("context was null");
        if (parent == null) throw new IllegalArgumentException("parent was null");

        validate(parent, false, false);

        if (ctx instanceof And_expressionContext) {
            final And_expressionContext and_expressionContext = (And_expressionContext) ctx;
            final Node p;
            logger.log(Level.FINEST, "AND NOT : {0}", and_expressionContext.NOT());

            final boolean not;
            if (and_expressionContext.NOT() != null) {
                if (!notSupported) throw new NotIsUnsupportedOperationException();
                final NotNode notNode = new NotNode(parent);
                parent.addChild(notNode);
                p = notNode;
                not = true;
            } else {
                p = parent;
                not = false;
            }

            validate(parent, false, false);

            final AndNode andNode = new AndNode(p, false);
            p.addChild(andNode);
            andNode.setParent(p);

            validate(parent, false, false);

            for (final TermContext termContext : and_expressionContext.term()) {
                validate(p, false, false);

                logger.log(Level.FINEST, "termContext.getText(): {0}", termContext.getText());

                final Node child = parse(termContext, andNode);
                logger.log(Level.FINEST, "child: {0}", child);
                andNode.addChild(child);
                logger.log(Level.FINEST, "andNode: {0}", andNode);
                child.setParent(andNode);

                validate(p, false, true);
                validate(p.getParent(), false, true);
            }

            validate(p, false, true);

            return not ? p : andNode;

        } else if (ctx instanceof Or_expressionContext) {
            final Or_expressionContext or_expressionContext = (Or_expressionContext) ctx;
            final Node p;
            logger.log(Level.FINEST, "OR NOT : {0}", or_expressionContext.NOT());

            final boolean not;
            if (or_expressionContext.NOT() != null) {
                if (!notSupported) throw new NotIsUnsupportedOperationException();
                final NotNode notNode = new NotNode(parent);
                parent.addChild(notNode);
                p = notNode;
                not = true;
            } else {
                p = parent;
                not = false;
            }

            final OrNode orNode = new OrNode(p, false);
            p.addChild(orNode);
            orNode.setParent(p);

            for (final And_expressionContext and_expressionContext : or_expressionContext.and_expression()) {
                final Node child = parse(and_expressionContext, orNode);
                orNode.addChild(child);
                child.setParent(orNode);
            }

            validate(parent, false, true);

            return not ? p : orNode;

        } else if (ctx instanceof EqContext) {
            final EqContext eqContext = (EqContext) ctx;

            final TermContext termContext = (TermContext) ctx.getParent();

            logger.log(Level.FINEST, "EQ term NOT : {0}", termContext.NOT());

            Node n = parent;

            validate(n, false, false);

            final boolean not;

            if (termContext.NOT() != null) {
                if (!notSupported) throw new NotIsUnsupportedOperationException();
                not = true;
                NotNode notNode = new NotNode(n);
                n.addChild(notNode);
                notNode.setParent(n);
                n = notNode;
            } else not = false;

            validate(n, false, false);

            // get the variable name
            if (termContext.ID() == null) throw new IllegalStateException(); // TODO: improve exception handling
            final String variableName = termContext.ID().getText();
            if (variableName == null) throw new IllegalStateException(); // TODO: improve exception handling

            final SortedSet positiveValues = new TreeSet(); // TODO: provide comparator that handles all types we will encounter
            final SortedSet negativeValues = new TreeSet();

            final String value = eqContext.atom().getText();
            positiveValues.add(value);

            final InNode inNode = new InNode(n); //inNodesMap.get(ctx);
            n.addChild(inNode);
            inNode.setParent(n);
            inNode.setVariableName(variableName);
            inNode.addPositiveValues(positiveValues);
            inNode.addNegativeValues(negativeValues);

            validate(parent, false, true);

            logger.log(Level.FINEST, "inNode: {0}", inNode);

            return not ? n : inNode;

        } else if (ctx instanceof InContext) {
            final InContext inContext = (InContext) ctx;

            final TermContext termContext = (TermContext) ctx.getParent();

            //System.out.println("IN NOT: " + termContext.NOT());

            // get the variable name
            if (termContext.ID() == null) throw new IllegalStateException(); // TODO: improve exception handling
            final String variableName = termContext.ID().getText();
            if (variableName == null) throw new IllegalStateException(); // TODO: improve exception handling

            // next steps build positive values

            // get in separate variable in case we wish to query size, null checking, etc.
            final List<AtomContext> atomContexts = inContext.atom();

            assert(atomContexts != null);

            // using sorted set for consistency / ease of testing and debugging
            final SortedSet positiveValues = new TreeSet(); // TODO: provide comparator that handles all types we will encounter

            for (PocketTLTreeBuilderParser.AtomContext atomContext : atomContexts) {
                final String value = atomContext.getText(); // TODO: transformToDNF values to appropriate types to faciliate type-specific actions?
                // TODO: format checking
                if (value == null) throw new IllegalStateException(); // TODO: improve exception handling
                positiveValues.add(value);
            }

            // next steps build negative values following same modus operandi
            final List<PocketTLTreeBuilderParser.Not_atomContext> not_atomContexts =
                    inContext.not_atom();

            final SortedSet negativeValues = new TreeSet();

            for (PocketTLTreeBuilderParser.Not_atomContext not_atomContext : not_atomContexts) {
                final String value = not_atomContext.atom().getText(); // take off the 'NOT'
                if (value == null) throw new IllegalStateException();
                negativeValues.add(value);
            }

            final InNode inNode = new InNode(parent); //inNodesMap.get(ctx);
            parent.addChild(inNode);
            inNode.setVariableName(variableName);
            inNode.addPositiveValues(positiveValues);
            inNode.addNegativeValues(negativeValues);

            validate(parent, false, true);

            return inNode;
        } else if (ctx instanceof PocketTLTreeBuilderParser.AtomContext) {
            final AtomContext atomContext = (AtomContext) ctx;
            throw new UnsupportedOperationException();

        } else if (ctx instanceof PocketTLTreeBuilderParser.Not_atomContext) {
            final Not_atomContext not_atomContext = (Not_atomContext) ctx;
            throw new UnsupportedOperationException();

        } else if (ctx instanceof PocketTLTreeBuilderParser.ExpressionContext) {
            final ExpressionContext expressionContext = (ExpressionContext) ctx;
            validate(parent, false, false);
            return parse(expressionContext.or_expression(), parent);

        } else if (ctx instanceof PocketTLTreeBuilderParser.FilterContext) {
            final FilterContext filterContext = (FilterContext) ctx;
            //if (filterContext.NOT() != null) {
            //    throw new UnsupportedOperationException();
            //} else {
            validate(parent, false, false);
                return parse(filterContext.expression(), parent);
            //}

        } else if (ctx instanceof PocketTLTreeBuilderParser.OperatorContext) {
            final OperatorContext operatorContext = (OperatorContext) ctx;
            throw new UnsupportedOperationException();

        } else if (ctx instanceof PocketTLTreeBuilderParser.TermContext) {
            final TermContext termContext = (TermContext) ctx;

            final ExpressionContext expression = termContext.expression();
            if (expression != null) {
                validate(parent, false, false);
                return parse(expression, parent);
            }

            final InContext in = termContext.in();
            if (in != null) {
                validate(parent, false, false);
                return parse(in, parent);
            }

            final EqContext eq = termContext.eq();
            if (eq != null) {
                validate(parent, false, false);
                return parse(eq, parent);
            }

            throw new PocketTLLanguageException("text=" + ctx.getText());

        }

        throw new IllegalStateException("ctx class = " + ctx.getClass());

        //return parent; // TODO: Not sure about this.
    }

    static DNFConvTree parse(final String input) throws PocketTLLanguageException {
        final PocketTLTreeBuilderLexer lexer;
        final CommonTokenStream tokens;
        final PocketTLTreeBuilderParser parser;

        lexer = new PocketTLTreeBuilderLexer(new ANTLRInputStream(input));
        tokens = new CommonTokenStream(lexer);
        parser = new PocketTLTreeBuilderParser(tokens);

        final ExpressionContext expression = parser.expression();

        final Node root = new OrNode(null, false);

        final ParserRuleContext ctx = expression;
        parse(ctx, root);

        final DNFConvTree tree = new DNFConvTree(root);
        try {
            TreeValidator.validate(tree, true, true);
        } catch (TreeStructuralException e) {
            throw new IllegalStateException(e);
        }
        return tree;
    }

    private static void validate(final Node top, final boolean topMustBeRoot, final boolean requireNonLeafNodesHaveLeaves) {
        try {
            TreeValidator.validate(top, topMustBeRoot, requireNonLeafNodesHaveLeaves);
        } catch (TreeStructuralException e) {
            throw new IllegalStateException(e);
        }
    }

}
