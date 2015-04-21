// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLNFChecker.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.nfchecker;

import com.pocketmath.stasov.util.StasovStrings;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PocketQLNFCheckerParser}.
 */
public interface PocketQLNFCheckerListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PocketQLNFCheckerParser#normal_form}.
	 * @param ctx the parse tree
	 */
	void enterNormal_form(@NotNull PocketQLNFCheckerParser.Normal_formContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNFCheckerParser#normal_form}.
	 * @param ctx the parse tree
	 */
	void exitNormal_form(@NotNull PocketQLNFCheckerParser.Normal_formContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLNFCheckerParser#terminal_leaf}.
	 * @param ctx the parse tree
	 */
	void enterTerminal_leaf(@NotNull PocketQLNFCheckerParser.Terminal_leafContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNFCheckerParser#terminal_leaf}.
	 * @param ctx the parse tree
	 */
	void exitTerminal_leaf(@NotNull PocketQLNFCheckerParser.Terminal_leafContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLNFCheckerParser#eq}.
	 * @param ctx the parse tree
	 */
	void enterEq(@NotNull PocketQLNFCheckerParser.EqContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNFCheckerParser#eq}.
	 * @param ctx the parse tree
	 */
	void exitEq(@NotNull PocketQLNFCheckerParser.EqContext ctx);
}