// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLNormalForm.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.nf;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PocketQLNormalFormParser}.
 */
public interface PocketQLNormalFormListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PocketQLNormalFormParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(@NotNull PocketQLNormalFormParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNormalFormParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(@NotNull PocketQLNormalFormParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLNormalFormParser#and_group}.
	 * @param ctx the parse tree
	 */
	void enterAnd_group(@NotNull PocketQLNormalFormParser.And_groupContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNormalFormParser#and_group}.
	 * @param ctx the parse tree
	 */
	void exitAnd_group(@NotNull PocketQLNormalFormParser.And_groupContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLNormalFormParser#not_leaf}.
	 * @param ctx the parse tree
	 */
	void enterNot_leaf(@NotNull PocketQLNormalFormParser.Not_leafContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNormalFormParser#not_leaf}.
	 * @param ctx the parse tree
	 */
	void exitNot_leaf(@NotNull PocketQLNormalFormParser.Not_leafContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLNormalFormParser#leaf}.
	 * @param ctx the parse tree
	 */
	void enterLeaf(@NotNull PocketQLNormalFormParser.LeafContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNormalFormParser#leaf}.
	 * @param ctx the parse tree
	 */
	void exitLeaf(@NotNull PocketQLNormalFormParser.LeafContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLNormalFormParser#in}.
	 * @param ctx the parse tree
	 */
	void enterIn(@NotNull PocketQLNormalFormParser.InContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNormalFormParser#in}.
	 * @param ctx the parse tree
	 */
	void exitIn(@NotNull PocketQLNormalFormParser.InContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLNormalFormParser#eq}.
	 * @param ctx the parse tree
	 */
	void enterEq(@NotNull PocketQLNormalFormParser.EqContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNormalFormParser#eq}.
	 * @param ctx the parse tree
	 */
	void exitEq(@NotNull PocketQLNormalFormParser.EqContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketQLNormalFormParser#list}.
	 * @param ctx the parse tree
	 */
	void enterList(@NotNull PocketQLNormalFormParser.ListContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketQLNormalFormParser#list}.
	 * @param ctx the parse tree
	 */
	void exitList(@NotNull PocketQLNormalFormParser.ListContext ctx);
}