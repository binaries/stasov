package com.pocketmath.stasov.engine;

import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderBaseVisitor;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderLexer;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderParser;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderVisitor;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderParser.*;
import com.pocketmath.stasov.engine.PTTree.*;
import com.sun.tools.javac.tree.JCTree;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by etucker on 5/19/15.
 */
public class TreeBuilder {

    private static Node parse(final ParserRuleContext ctx, Node parent) {
        if (ctx instanceof And_expressionContext) {
            final And_expressionContext and_expressionContext = (And_expressionContext) ctx;
            final AndNode andNode = new AndNode(parent);

            for (final TermContext termContext : and_expressionContext.term()) {
                final Node child = parse(termContext, andNode);
                andNode.addChild(child);
            }

            return andNode;

        } else if (ctx instanceof Or_expressionContext) {
            final Or_expressionContext or_expressionContext = (Or_expressionContext) ctx;
            final OrNode orNode = new OrNode(parent);

            for (final And_expressionContext and_expressionContext : or_expressionContext.and_expression()) {
                final Node child = parse(and_expressionContext, orNode);
                orNode.addChild(child);
            }

            return orNode;

        } else if (ctx instanceof InContext) {
            final InContext inContext = (InContext) ctx;

            // get the variable name
            if (inContext.ID() == null) throw new IllegalStateException(); // TODO: improve exception handling
            final String variableName = inContext.ID().getText();
            if (variableName == null) throw new IllegalStateException(); // TODO: improve exception handling

            // next steps build positive values

            // get in separate variable in case we wish to query size, null checking, etc.
            final List<AtomContext> atomContexts = inContext.atom();

            assert(atomContexts != null);

            // using sorted set for consistency / ease of testing and debugging
            final SortedSet positiveValues = new TreeSet(); // TODO: provide comparator that handles all types we will encounter

            for (PocketTLTreeBuilderParser.AtomContext atomContext : atomContexts) {
                final String value = atomContext.getText(); // TODO: convert values to appropriate types to faciliate type-specific actions?
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

            final PTTree.InNode inNode = new InNode(parent); //inNodesMap.get(ctx);
            inNode.setVariableName(variableName);
            inNode.addPositiveValues(positiveValues);
            inNode.addNegativeValues(negativeValues);

            return inNode;
        } else if (ctx instanceof PocketTLTreeBuilderParser.AtomContext) {
            final AtomContext atomContext = (AtomContext) ctx;
            throw new UnsupportedOperationException();

        } else if (ctx instanceof PocketTLTreeBuilderParser.Not_atomContext) {
            final Not_atomContext not_atomContext = (Not_atomContext) ctx;
            throw new UnsupportedOperationException();

        } else if (ctx instanceof PocketTLTreeBuilderParser.ExpressionContext) {
            final ExpressionContext expressionContext = (ExpressionContext) ctx;
            return parse(expressionContext.or_expression(), parent);

        } else if (ctx instanceof PocketTLTreeBuilderParser.FilterContext) {
            final FilterContext filterContext = (FilterContext) ctx;
            if (filterContext.NOT() != null) {
                throw new UnsupportedOperationException();
            } else {
                return parse(filterContext.expression(), parent);
            }

        } else if (ctx instanceof PocketTLTreeBuilderParser.OperatorContext) {
            final OperatorContext operatorContext = (OperatorContext) ctx;
            throw new UnsupportedOperationException();

        } else if (ctx instanceof PocketTLTreeBuilderParser.TermContext) {
            final TermContext termContext = (TermContext) ctx;

            final ExpressionContext expression = termContext.expression();
            if (expression != null) {
                return parse(expression, parent);
            }

            final InContext in = termContext.in();
            if (in != null) {
                return parse(in, parent);
            }

            throw new UnsupportedOperationException();

        }

        return parent; // TODO: Not sure about this.
    }

    public static PTTree.Node parse(final String input) throws Exception {
        final PocketTLTreeBuilderLexer lexer;
        final CommonTokenStream tokens;
        final PocketTLTreeBuilderParser parser;

        lexer = new PocketTLTreeBuilderLexer(new ANTLRInputStream(input));
        tokens = new CommonTokenStream(lexer);
        parser = new PocketTLTreeBuilderParser(tokens);

        final ExpressionContext expression = parser.expression();

        final Node root = new PTTree.AndNode(null);

        ParserRuleContext ctx = expression;
        return parse(ctx, root);
    }

}
