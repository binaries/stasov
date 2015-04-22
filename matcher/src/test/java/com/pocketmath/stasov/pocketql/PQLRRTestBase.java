package com.pocketmath.stasov.pocketql;

import com.pocketmath.pocketql.grammars.nfchecker.PocketQLNFCheckerLexer;
import com.pocketmath.pocketql.grammars.nfchecker.PocketQLNFCheckerParser;
import com.pocketmath.pocketql.grammars.transformer.PocketQLAllLexer;
import com.pocketmath.pocketql.grammars.transformer.PocketQLAllParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Created by etucker on 4/8/15.
 */
public class PQLRRTestBase {


    private boolean isNormalForm(final String input) throws Exception {
        if (input == null) throw new IllegalArgumentException("input was null");
        if (input.isEmpty()) throw new IllegalArgumentException("input was empty");

        final PocketQLNFCheckerLexer lexer =
                new PocketQLNFCheckerLexer(new ANTLRInputStream(input));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final PocketQLNFCheckerParser parser = new PocketQLNFCheckerParser(tokens);

        final PocketQLNFCheckerParser.Normal_formContext nfctx = parser.normal_form();

        final boolean match = nfctx != null && nfctx.match && nfctx.getText().equals(input);

        if (l()) log("normal form match=" + match + "  " + nfctx.getText());

        return match;
    }

    private boolean l() {
        return true;
    }

    private void log(final String msg) {
        System.out.println("[log] " + msg);
    }

    protected String parse(final String input) throws Exception {

        String _input = input;

        String output = null;
        for (int i = 0; i < 10; i++) {
            if (isNormalForm(_input)) {
                if (l()) log("returning, normal form found: " + _input);
                return _input;
            }

            final PocketQLAllLexer nflexer;
            final CommonTokenStream nftokens;
            final PocketQLAllParser nfparser;

            nflexer = new PocketQLAllLexer(new ANTLRInputStream(_input));
            nftokens = new CommonTokenStream(nflexer);
            nfparser = new PocketQLAllParser(nftokens);

            final PocketQLAllParser.StartContext start = nfparser.start();
            output = start.value;

            System.out.println("transformation cycle: " + output);

            if (output == null) return null;

            _input = output;

        }

        return null;
    }

}