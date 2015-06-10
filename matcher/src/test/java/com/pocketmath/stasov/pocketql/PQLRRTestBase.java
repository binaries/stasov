package com.pocketmath.stasov.pocketql;

import com.pocketmath.pocketql.grammars.nfchecker.PocketQLNFCheckerLexer;
import com.pocketmath.pocketql.grammars.nfchecker.PocketQLNFCheckerParser;
import com.pocketmath.pocketql.grammars.transformer.PocketQLAllLexer;
import com.pocketmath.pocketql.grammars.transformer.PocketQLAllParser;
import org.antlr.runtime.debug.BlankDebugEventListener;
import org.antlr.runtime.debug.DebugEventHub;
import org.antlr.runtime.debug.DebugEventListener;
import org.antlr.runtime.debug.TraceDebugEventListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.testng.Assert;

/**
 * Created by etucker on 4/8/15.
 */
public class PQLRRTestBase {

    protected final void assertEqualsIgnoreCase(final String s1, final String s2) {
        Assert.assertEquals(s1.toLowerCase(), s2.toLowerCase());
    }

    protected final void assertNotEqualsIgnoreCase(final String s1, final String s2) {
        Assert.assertNotEquals(s1.toLowerCase(), s2.toLowerCase());
    }

    private boolean isNormalForm(final String input) throws Exception {
        if (input == null) throw new IllegalArgumentException("input was null");
        if (input.isEmpty()) throw new IllegalArgumentException("input was empty");

        final PocketQLNFCheckerLexer lexer =
                new PocketQLNFCheckerLexer(new ANTLRInputStream(input));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final PocketQLNFCheckerParser parser = new PocketQLNFCheckerParser(tokens);

        final PocketQLNFCheckerParser.StartContext nfctx = parser.start();

        final boolean match = nfctx != null && nfctx.match && input.equals(nfctx.getText());

        if (l()) log("normal form match=" + match + "  " + nfctx.getText());

        return match;
    }

    private boolean l() {
        return true;
    }

    private void log(final String msg) {
        System.out.println("[log] " + msg);
    }

    private static class PocketQLAllDebuggableParser extends PocketQLAllParser {
        public PocketQLAllDebuggableParser(TokenStream input, boolean trace) {
            super(input);
            if (trace) {
                ParseTreeListener debugListener = new PocketQLAllParser.TraceListener();
                this.addParseListener(debugListener);
            }
        }
    }

    protected String parse(final String input) throws Exception {
        if (input == null) throw new IllegalArgumentException("input was null");

        String _input = input;
        String _last_input = null;

        String output = null;
        for (int i = 0; i < 100; i++) {
            if (_input.equals(_last_input)) {
                if (l()) log("stuck transformation (infinite loop) detected for: " + _input);
                return null;
            }

            if (isNormalForm(_input)) {
                if (l()) log("returning, normal form found: " + _input);
                return _input;
            }

            final PocketQLAllLexer nflexer;
            final CommonTokenStream nftokens;
            final PocketQLAllParser nfparser;

            nflexer = new PocketQLAllLexer(new ANTLRInputStream(_input));
            nftokens = new CommonTokenStream(nflexer);
            nfparser = new PocketQLAllDebuggableParser(nftokens, true);

            //final PocketQLAllParser.StartContext start = nfparser.start();
            final PocketQLAllParser.ExpContext exp = nfparser.exp();
            output = exp.v;

            if (l()) log("transformation cycle: " + output);

            if (output == null) return null;

            _last_input = _input;
            _input = output;

        }

        return null;
    }

}