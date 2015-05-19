package com.pocketmath.stasov.engine;

import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderBaseListener;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderParser;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by etucker on 5/16/15.
 */
public class ParseTreeBuilder extends PocketTLTreeBuilderBaseListener {

    private Logger logger = Logger.getLogger(getClass().getName());
    {
        // shameless hard coded logging setup

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINEST);

        logger.setLevel(Level.FINEST);
        logger.addHandler(consoleHandler);

    }

    private PTTree.Node root = new PTTree.AndNode(null);
    private PTTree.Node current = root;

    @Override
    public void enterAnd_expression(@NotNull PocketTLTreeBuilderParser.And_expressionContext ctx) {
        super.enterAnd_expression(ctx);
        PTTree.AndNode andNode = new PTTree.AndNode(current);
        current.addChild(andNode);
        current = andNode;
    }

    @Override
    public void enterOr_expression(@NotNull PocketTLTreeBuilderParser.Or_expressionContext ctx) {
        super.enterOr_expression(ctx);
        PTTree.OrNode orNode = new PTTree.OrNode(current);
        current.addChild(orNode);
        current = orNode;
    }

    // We can only care about exit value because we're sourcing raw text at leaf level.
    // Normally we'd need to look at the entry for non-leaf structural building.
    @Override
    public void exitIn(@NotNull PocketTLTreeBuilderParser.InContext ctx) {
        super.enterIn(ctx);
        final PTTree.Node parent = current;
        assert(! (parent instanceof PTTree.Leaf));

        // get the variable name
        if (ctx.ID() == null) throw new IllegalStateException(); // TODO: improve exception handling
        final String variableName = ctx.ID().getText();
        if (variableName == null) throw new IllegalStateException(); // TODO: improve exception handling

        // next steps build positive values

        // get in separate variable in case we wish to query size, null checking, etc.
        final List<PocketTLTreeBuilderParser.AtomContext> atomContexts =
                ctx.atom();

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
                ctx.not_atom();

        final SortedSet negativeValues = new TreeSet();

        for (PocketTLTreeBuilderParser.Not_atomContext not_atomContext : not_atomContexts) {
            final String value = not_atomContext.atom().getText(); // take off the 'NOT'
            if (value == null) throw new IllegalStateException();
            negativeValues.add(value);
        }

        PTTree.InNode inNode = new PTTree.InNode(parent, variableName, positiveValues, negativeValues);

        current.addChild(inNode);
        current = inNode;
    }

    public PTTree.Node getRoot() {
        return root;
    }

}
