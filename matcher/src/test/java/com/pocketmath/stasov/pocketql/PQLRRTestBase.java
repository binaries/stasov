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

    private static boolean isNormalForm(final String input) throws Exception {
        final PocketQLNFCheckerLexer lexer =
                new PocketQLNFCheckerLexer(new ANTLRInputStream(input));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final PocketQLNFCheckerParser parser = new PocketQLNFCheckerParser(tokens);

        final PocketQLNFCheckerParser.Normal_formContext nfctx = parser.normal_form();
        return nfctx != null && !nfctx.isEmpty();
    }

    private boolean l() {
        return true;
    }

    private void log(final String msg) {
        System.out.println("[log] " + msg);
    }

    protected String parse(final String input) throws Exception {

        PocketQLAllLexer nflexer;
        CommonTokenStream nftokens;

        PocketQLAllParser nfparser;

        PQLRR.Transformer transformer;

        ParseTreeWalker nfwalker;

        String _input = input;

        String output = null;
        for (int i = 0; i < 10; i++) {
            if (isNormalForm(_input)) {
                if (l()) log("returning, normal form found: " + _input);
                return _input;
            }

            nflexer = new PocketQLAllLexer(new ANTLRInputStream(_input));
            nftokens = new CommonTokenStream(nflexer);

            nfparser = new PocketQLAllParser(nftokens);

//            transformer = new PQLRR.Transformer();

  //          Listener

            //nfwalker = new ParseTreeWalker();
            //nfwalker.walk(new PocketQLAllBaseListener(), nfparser.start());

 //           nfparser.changed = false;

 //           PocketQLAllParser.Normal_formContext normal_form = nfparser.normal_form();
 //           if (normal_form != null && normal_form.value != null) return normal_form.value;

            PocketQLAllParser.StartContext start = nfparser.start();
            output = start.value;
            //System.out.println("start value = " + output);
            System.out.println("transformation cycle: " + output);

            _input = output;

            //if (nfparser.changed) {
            //    assert(output != null);
            //    assert(!output.isEmpty());
            //    _input = output;
            //} else return output;
        }

        //return transformer.getOutput();
        //return output;
        return null;
    }

}