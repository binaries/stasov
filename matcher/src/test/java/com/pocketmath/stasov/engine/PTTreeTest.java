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
    }

    @Test
    public void test100_transforming_tree_no_changes_expected() throws Exception {
        // first build the tree
        final PTTree.Node tree = TreeBuilder.parse("chicken IN (1,2,3)");
        tree.prettyPrint(writer);

        writer.println();

        // transform tree
        PTTree.convert(tree);
        tree.prettyPrint(writer);
    }

    @Test
    public void test101_transforming_tree_no_changes_expected() throws Exception {
        // first build the tree
        final PTTree.Node tree = TreeBuilder.parse("chicken IN (1,2,3) AND robot IN (4)");
        tree.prettyPrint(writer);

        writer.println();

        // transform tree
        PTTree.convert(tree);
        tree.prettyPrint(writer);
    }

    @Test
    public void test102_transforming_tree_no_structural_changes_expected() throws Exception {
        // first build the tree
        final PTTree.Node tree = TreeBuilder.parse("chicken IN (1,2,3) OR robot IN (4) AND lobster IN (5)");
        tree.prettyPrint(writer);

        writer.println();

        // transform tree
        PTTree.convert(tree);
        tree.prettyPrint(writer);
    }

    @Test
    public void test200_transforming_tree_structural_changes_expected() throws Exception {
        // first build the tree
        final PTTree.Node tree = TreeBuilder.parse("(chicken IN (1,2,3) OR robot IN (4)) AND lobster IN (5)");
        tree.prettyPrint(writer);

        writer.println();

        // transform tree
        PTTree.convert(tree);
        tree.prettyPrint(writer);
    }
}