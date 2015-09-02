package com.pocketmath.stasov.engine; /**
 * Created by etucker on 3/8/15.
 */

import com.pocketmath.pocketql.grammars.nf.PocketQLNormalFormBaseListener;
import com.pocketmath.pocketql.grammars.nf.PocketQLNormalFormLexer;
import com.pocketmath.pocketql.grammars.nf.PocketQLNormalFormParser;
import com.pocketmath.stasov.attributes.AttrSvcBase;
import com.pocketmath.stasov.pmtl.PocketTLDataException;
import com.pocketmath.stasov.pmtl.PocketTLLanguageException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

class PocketTLIndexer {

    private final AttrSvcBase attrSvc;

    private class BranchBuilder extends PocketQLNormalFormBaseListener {

        private final long[] matches;

        final private MatchTree tree;
        private MatchTree.AndGroupBuilder andGroupBuilder = null;
        private boolean not = false;

        public BranchBuilder(final MatchTree tree, final long[] matches) {
            this.tree = tree;
            this.matches = matches;
        }

        @Override
        public void enterAnd_group(@NotNull PocketQLNormalFormParser.And_groupContext ctx) {
            andGroupBuilder = tree.newAndGroupBuilder(matches); // TODO: matches
        }

        @Override
        public void exitAnd_group(@NotNull PocketQLNormalFormParser.And_groupContext ctx) {
            tree.addAndGroup(andGroupBuilder);
            andGroupBuilder = null;
        }

        @Override
        public void enterIn(@NotNull PocketQLNormalFormParser.InContext ctx) {
            super.enterIn(ctx);
            final TerminalNode varNameTN = ctx.ALPHANUM();
            final String varName = varNameTN.getText().toLowerCase();
            final long attrTypeId = attrSvc.findTypeId(varName);
            if (attrTypeId < 1) throw new IllegalArgumentException(new PocketTLDataException("could not find varName: " + varName));
            final List<TerminalNode> valueNodes = ctx.list().ALPHANUM();
            for (TerminalNode valueNode : valueNodes) {
                final String valueString = valueNode.getText();
                final long valueId = attrSvc.findValue(attrTypeId, valueString);
                if (valueId < 1) throw new IllegalArgumentException( new PocketTLDataException("value not found for varName: " + varName + "; attrTypeId: " + attrTypeId + "; value: " + valueString) );
                if (not)
                    andGroupBuilder.addExclusionaryValue(attrTypeId, valueId);
                else
                    andGroupBuilder.addInclusionaryValue(attrTypeId, valueId);
            }
        }

        @Override
        public void enterEq(@NotNull PocketQLNormalFormParser.EqContext ctx) {
            super.enterEq(ctx);

            final TerminalNode varNameTN = ctx.ALPHANUM(0);
            final String varName = varNameTN.getText().toLowerCase();
            final long attrTypeId = attrSvc.findTypeId(varName);
            if (attrTypeId < 1) throw new IllegalArgumentException(new PocketTLDataException("could not find varName: " + varName));

            final TerminalNode valueTN = ctx.ALPHANUM(1);
            final String valueString = valueTN.getText();
            final long valueId = attrSvc.findValue(attrTypeId, valueString);
            if (valueId < 1) throw new IllegalArgumentException( new PocketTLDataException("value not found for varName: " + varName + "; attrTypeId: " + attrTypeId + "; value: " + valueString) );
            if (not)
                andGroupBuilder.addExclusionaryValue(attrTypeId, valueId);
            else
                andGroupBuilder.addInclusionaryValue(attrTypeId, valueId);
        }

        @Override
        public void enterNot_leaf(@NotNull PocketQLNormalFormParser.Not_leafContext ctx) {
            not = true;
        }

        @Override
        public void exitNot_leaf(@NotNull PocketQLNormalFormParser.Not_leafContext ctx) {
            not = false;
        }
    }

    public PocketTLIndexer(final AttrSvcBase attrSvc) {
        this.attrSvc = attrSvc;
    }

    void index(final MatchTree tree, final String inputString, final long[] matches) throws IndexingException {

        PocketQLNormalFormLexer nflexer =
                new PocketQLNormalFormLexer(new ANTLRInputStream(inputString));

        CommonTokenStream nftokens = new CommonTokenStream(nflexer);

        PocketQLNormalFormParser nfparser = new PocketQLNormalFormParser(nftokens);

        final DiagnosticErrorListener diagnosticErrorListener = new DiagnosticErrorListener(true);

        nflexer.removeErrorListeners();
        nflexer.addErrorListener(diagnosticErrorListener);

        nfparser.removeErrorListeners();
        nfparser.addErrorListener(diagnosticErrorListener);

        BranchBuilder branch = new BranchBuilder(tree, matches);

        ParseTreeWalker nfwalker = new ParseTreeWalker();
        nfwalker.walk(branch, nfparser.expr());
    }

}
