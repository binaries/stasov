package com.pocketmath.stasov.engine;

import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderBaseVisitor;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderLexer;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderParser;
import com.pocketmath.pocketql.grammars.anytree.PocketTLTreeBuilderVisitor;
import com.pocketmath.stasov.util.PrettyPrintable;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.testng.Assert.*;

/**
 * Created by etucker on 5/17/15.
 */
public class PTTreeTest {

/*    protected PTTree.Node parse(final String input) throws Exception {
        PocketTLTreeBuilderLexer lexer;
        CommonTokenStream tokens;
        PocketTLTreeBuilderParser parser;

        lexer = new PocketTLTreeBuilderLexer(new ANTLRInputStream(input));
        tokens = new CommonTokenStream(lexer);
        parser = new PocketTLTreeBuilderParser(tokens);

        parser.expression();


        ParseTreeBuilder builder = new ParseTreeBuilder();
        parser.addParseListener(builder);

        PocketTLTreeBuilderVisitor visitor = new PocketTLTreeBuilderBaseVisitor();

        visitor.visit(parser.expression());

        return builder.getRoot();
    }*/

    PrintWriter writer = null;

    @BeforeMethod
    public void setUp() throws Exception {
        writer = new PrintWriter(System.out);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        writer.flush();
        writer.close();
    }

    @Test
    public void test1() throws Exception {
        final PTTree.Node tree = TreeBuilder.parse("chicken in (1,2,3)");
        tree.prettyPrint(writer);
    }

    @Test
    public void test2_positive_and_negative() throws Exception {
        final PTTree.Node tree = TreeBuilder.parse("chicken in (1,not 2,3)");
        tree.prettyPrint(writer);
    }

    @Test
    public void test3() throws Exception {
        final PTTree.Node tree = TreeBuilder.parse("chicken IN (1,2,3) AND robot IN (4)");
        tree.prettyPrint(writer);
    }

    @Test
    public void test4() throws Exception {
        final PTTree.Node tree = TreeBuilder.parse("chicken IN (1,2,3) OR robot IN (4)");
        tree.prettyPrint(writer);
        //System.out.println(tree);
        //PTTree.convert(tree);
        //writer.println();
        //tree.prettyPrint(writer);
    }
}