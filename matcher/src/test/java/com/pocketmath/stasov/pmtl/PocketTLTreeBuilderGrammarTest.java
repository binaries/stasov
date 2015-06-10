package com.pocketmath.stasov.pmtl;

import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderBaseVisitor;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderLexer;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderParser;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.testng.annotations.Test;

/**
 * Created by etucker on 5/1/15.
 */
public class PocketTLTreeBuilderGrammarTest {

    @Test
    public void simpleTree1() {

        final String input = "a=1";

        final PocketTLTreeBuilderLexer nflexer;
        final CommonTokenStream nftokens;
        final PocketTLTreeBuilderParser nfparser;

        nflexer = new PocketTLTreeBuilderLexer(new ANTLRInputStream(input));
        nftokens = new CommonTokenStream(nflexer);
        nfparser = new PocketTLTreeBuilderParser(nftokens);

        PocketTLTreeBuilderParser.FilterContext filter = nfparser.filter();

        System.out.println(filter.toStringTree());

        PocketTLTreeBuilderVisitor visitor = new PocketTLTreeBuilderBaseVisitor() {

        };
        visitor.visit(filter);


    }

}
