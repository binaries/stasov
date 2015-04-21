// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLNormalForm.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.nf;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PocketQLNormalFormParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PocketQLNormalFormVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PocketQLNormalFormParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(@NotNull PocketQLNormalFormParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLNormalFormParser#and_group}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_group(@NotNull PocketQLNormalFormParser.And_groupContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLNormalFormParser#not_leaf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot_leaf(@NotNull PocketQLNormalFormParser.Not_leafContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLNormalFormParser#leaf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeaf(@NotNull PocketQLNormalFormParser.LeafContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLNormalFormParser#in}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIn(@NotNull PocketQLNormalFormParser.InContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLNormalFormParser#eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEq(@NotNull PocketQLNormalFormParser.EqContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketQLNormalFormParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList(@NotNull PocketQLNormalFormParser.ListContext ctx);
}