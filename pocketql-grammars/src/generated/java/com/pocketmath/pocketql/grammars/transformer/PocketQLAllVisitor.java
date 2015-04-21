// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLAll.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.transformer;

import com.pocketmath.stasov.util.StasovStrings;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PocketQLAllParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PocketQLAllVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PocketQLAllParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(@NotNull PocketQLAllParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLAllParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(@NotNull PocketQLAllParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLAllParser#terminal_leaf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerminal_leaf(@NotNull PocketQLAllParser.Terminal_leafContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLAllParser#eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEq(@NotNull PocketQLAllParser.EqContext ctx);
}