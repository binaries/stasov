// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLNFChecker.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.nfchecker;

import com.pocketmath.stasov.util.StasovStrings;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PocketQLNFCheckerParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PocketQLNFCheckerVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PocketQLNFCheckerParser#normal_form}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormal_form(@NotNull PocketQLNFCheckerParser.Normal_formContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLNFCheckerParser#terminal_leaf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerminal_leaf(@NotNull PocketQLNFCheckerParser.Terminal_leafContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLNFCheckerParser#eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEq(@NotNull PocketQLNFCheckerParser.EqContext ctx);
}