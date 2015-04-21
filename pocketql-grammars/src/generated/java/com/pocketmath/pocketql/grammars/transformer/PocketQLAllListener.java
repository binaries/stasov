// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLAll.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.transformer;

import com.pocketmath.stasov.util.StasovStrings;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PocketQLAllParser}.
 */
public interface PocketQLAllListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PocketQLAllParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(@NotNull PocketQLAllParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLAllParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(@NotNull PocketQLAllParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLAllParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(@NotNull PocketQLAllParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLAllParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(@NotNull PocketQLAllParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLAllParser#terminal_leaf}.
	 * @param ctx the parse tree
	 */
	void enterTerminal_leaf(@NotNull PocketQLAllParser.Terminal_leafContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLAllParser#terminal_leaf}.
	 * @param ctx the parse tree
	 */
	void exitTerminal_leaf(@NotNull PocketQLAllParser.Terminal_leafContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLAllParser#eq}.
	 * @param ctx the parse tree
	 */
	void enterEq(@NotNull PocketQLAllParser.EqContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLAllParser#eq}.
	 * @param ctx the parse tree
	 */
	void exitEq(@NotNull PocketQLAllParser.EqContext ctx);
}