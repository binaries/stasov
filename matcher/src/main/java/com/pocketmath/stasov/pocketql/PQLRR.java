package com.pocketmath.stasov.pocketql;

import com.pocketmath.pocketql.grammars.transformer.PocketQLAllBaseListener;
import com.pocketmath.pocketql.grammars.transformer.PocketQLAllLexer;
import com.pocketmath.pocketql.grammars.transformer.PocketQLAllParser;
import com.pocketmath.stasov.util.StasovStrings;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by etucker on 4/5/15.
 */
public class PQLRR {

    static class Transformer extends PocketQLAllBaseListener {

        final private StringBuilder sb = new StringBuilder();
        private boolean changed = false;

        @Override
        public void visitTerminal(@NotNull TerminalNode node) {
            //System.out.println(node.getText());
        }
/*
        @Override
        public void enterLeaf(@NotNull PocketQLAllParser.LeafContext ctx) {
            //sb.append(ctx.getText());
        }

        @Override
        public void enterLeaf_and_expr(@NotNull PocketQLAllParser.Leaf_and_exprContext ctx) {

        }

        @Override
        public void enterLeaf_and_mult(@NotNull PocketQLAllParser.Leaf_and_multContext ctx) {
            assert(ctx.expr().size() == 2);

            final String cmpn1 = '(' + StasovStrings.conjoin(ctx.leaf().getText(), ctx.expr(0).getText(), " AND ") + ')';
            final String cmpn2 = '(' + StasovStrings.conjoin(ctx.leaf().getText(), ctx.expr(1).getText(), " AND ") + ')';

            final String combined = '(' + StasovStrings.conjoin(cmpn1, cmpn2, " OR ") + ')';

            this.sb.append(combined);
            changed = true;
        }

        @Override
        public void enterMult_and_leaf(@NotNull PocketQLAllParser.Mult_and_leafContext ctx) {
            assert(ctx.expr().size() == 2);

            final String cmpn1 = '(' + StasovStrings.conjoin(ctx.expr(2).getText(), ctx.expr(0).getText(), " AND ") + ')';
            final String cmpn2 = '(' + StasovStrings.conjoin(ctx.expr(2).getText(), ctx.expr(1).getText(), " AND ") + ')';

            System.out.println("cmpn1: " + cmpn1);
            System.out.println("cmpn2: " + cmpn2);
            final String combined = '(' + StasovStrings.conjoin(cmpn1, cmpn2, " OR ") + ')';

            this.sb.append(combined);
            changed = true;
        }

        @Override
        public void enterLeaf_or_expr(@NotNull PocketQLAllParser.Leaf_or_exprContext ctx) {

        }

        @Override
        public void enterNot_leaf(@NotNull PocketQLAllParser.Not_leafContext ctx) {

        }

        @Override
        public void enterNot_paren_and(@NotNull PocketQLAllParser.Not_paren_andContext ctx) {
            assert(ctx.expr().size() == 2);

            StringBuffer sb = new StringBuffer();
            sb.append("NOT " + ctx.expr(0).getText() + " OR NOT " + ctx.expr(1).getText());
            this.sb.append(sb);
            changed = true;
        }

        @Override
        public void enterNot_paren_or(@NotNull PocketQLAllParser.Not_paren_orContext ctx) {
            assert(ctx.expr().size() == 2);

            StringBuffer sb = new StringBuffer();
            sb.append("NOT " + ctx.expr(0).getText() + " AND NOT " + ctx.expr(1).getText());
            this.sb.append(sb);
            changed = true;
        }

        @Override
        public void enterParen_expr_and_expr(@NotNull PocketQLAllParser.Paren_expr_and_exprContext ctx) {

        }

        @Override
        public void enterParen_expr_and_leaf(@NotNull PocketQLAllParser.Paren_expr_and_leafContext ctx) {

        }

        @Override
        public void enterParen_leaf(@NotNull PocketQLAllParser.Paren_leafContext ctx) {
            sb.append(ctx.leaf().getText());
            changed = true;
        }

        @Override
        public void enterParen_leaf_and_expr(@NotNull PocketQLAllParser.Paren_leaf_and_exprContext ctx) {

        }

        @Override
        public void enterDouble_paren_expr(@NotNull PocketQLAllParser.Double_paren_exprContext ctx) {
            sb.append('(' + ctx.expr().getText() + ')');
            changed = true;
        }

        @Override
        public void enterDouble_not(@NotNull PocketQLAllParser.Double_notContext ctx) {
            System.out.println("doublenot");
            sb.append(ctx.expr().getText());
            changed = true;
        }
/*
        @Override
        public void exitExpr2(@NotNull PocketQLAllParser.Expr2Context ctx) {
            final String s = '(' + ctx.expr().getText() + ')';
            sb.append(s);
            changed = true;
        }

        @Override
         public void enterExpr3(@NotNull PocketQLAllParser.Expr3Context ctx) {
            final List<PocketQLAllParser.ExprContext> exprs = ctx.expr();
            final int n = exprs.size();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n-1; i++) {
                sb.append(ctx.expr(i).getText() + " AND " + ctx.expr(n-1).getText());
                if (i < n-2) sb.append(" OR ");
            }
            this.sb.append(sb);
            changed = true;
        }
/*
        @Override
        public void enterExpr4(@NotNull PocketQLAllParser.Expr4Context ctx) {
            final String s = ctx.expr(0).getText()
                    + " AND "
                    + ctx.expr(1).getText()
                    + " OR "
                    + ctx.expr(0).getText()
                    + " AND "
                    + ctx.expr(2).getText();
        }
        */

        @Override
        public void enterEveryRule(@NotNull ParserRuleContext ctx) {
            System.out.println(ctx.getText());
        }

        /*
                        @Override
                        public void enterExpr100(@NotNull PocketQLAllParser.Expr100Context ctx) {
                            final String s = ctx.expr(0).getText() + " AND " + ctx.expr(2).getText() + " OR " + ctx.expr(1).getText() + " AND " + ctx.expr(2);
                            System.out.println(s);
                            sb.append(s);
                            changed = true;
                        }

                        @Override
                        public void enterExpr101(@NotNull PocketQLAllParser.Expr101Context ctx) {
                            final String s = ctx.expr(0).getText() + " AND " + ctx.expr(1).getText() + " AND " + ctx.expr(2).getText();
                            System.out.println(s);
                            sb.append(s);
                            changed = true;
                        }

                        @Override
                        public void enterExpr102(@NotNull PocketQLAllParser.Expr102Context ctx) {
                            final String s = ctx.expr().getText();
                            System.out.println(s);
                            sb.append(s);
                            changed = true;
                        }

                        @Override
                        public void enterExpr11(@NotNull PocketQLAllParser.Expr11Context ctx) {
                            StringBuilder sb = new StringBuilder();
                            List<PocketQLAllParser.ExprContext> exprs = ctx.expr();
                            ListIterator<PocketQLAllParser.ExprContext> exprsItr = exprs.listIterator();
                            int i = 0;
                            sb.append(ctx.expr(i++).getText());
                            while (i < exprs.size()) sb.append(" AND " + ctx.expr(i++).getText());

                            System.out.println(sb.toString());
                            this.sb.append(sb);
                            changed = true;
                        }

                        @Override
                        public void enterExpr30(@NotNull PocketQLAllParser.Expr30Context ctx) {
                            final String s = ctx.expr().getText();
                            sb.append(s);
                            changed = true;
                        }

                        @Override
                        public void exitExpr200(@NotNull PocketQLAllParser.Expr200Context ctx) {
                            final String s = ctx.leaf().getText();
                            sb.append(s);
                            changed = true;
                        }

                        @Override
                        public void exitExpr201(@NotNull PocketQLAllParser.Expr201Context ctx) {
                            final String s = ctx.not_leaf().getText();
                            sb.append(s);
                            changed = true;
                        }
                */
        /*
        @Override
        public void enterNormal_form(@NotNull PocketQLAllParser.Normal_formContext ctx) {
            final String s = ctx.getText();
            int n = ctx.getChildCount();
            //for (int i = 0; i < n; i++) System.out.println("[" + ctx.getChild(i).getText() + "]");
            System.out.println("normal form enter: " + s);
            sb.append(s);
        }*/

        public String getOutput() {
            return sb.toString();
        }

        public boolean isChanged() {
            return changed;
        }
    }

    public static void main(String argsp[]) {

        String inputString = "(City=Austin and DeviceType=iPhone)";

        PocketQLAllLexer nflexer = new PocketQLAllLexer(new ANTLRInputStream(inputString));
        CommonTokenStream nftokens = new CommonTokenStream(nflexer);

        PocketQLAllParser nfparser = new PocketQLAllParser(nftokens);

        Transformer transformer = new Transformer();

        ParseTreeWalker nfwalker = new ParseTreeWalker();
        nfwalker.walk(transformer, nfparser.expr());
    }
}
