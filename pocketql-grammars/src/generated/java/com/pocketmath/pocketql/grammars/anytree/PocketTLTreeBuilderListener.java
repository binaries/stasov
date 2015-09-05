// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketTLTreeBuilder.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.anytree;


import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PocketTLTreeBuilderParser}.
 */
public interface PocketTLTreeBuilderListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#filter}.
	 * @param ctx the parse tree
	 */
	void enterFilter(@NotNull PocketTLTreeBuilderParser.FilterContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#filter}.
	 * @param ctx the parse tree
	 */
	void exitFilter(@NotNull PocketTLTreeBuilderParser.FilterContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull PocketTLTreeBuilderParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull PocketTLTreeBuilderParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void enterOr_expression(@NotNull PocketTLTreeBuilderParser.Or_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#or_expression}.
	 * @param ctx the parse tree
	 */
	void exitOr_expression(@NotNull PocketTLTreeBuilderParser.Or_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void enterAnd_expression(@NotNull PocketTLTreeBuilderParser.And_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#and_expression}.
	 * @param ctx the parse tree
	 */
	void exitAnd_expression(@NotNull PocketTLTreeBuilderParser.And_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(@NotNull PocketTLTreeBuilderParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(@NotNull PocketTLTreeBuilderParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#not_atom}.
	 * @param ctx the parse tree
	 */
	void enterNot_atom(@NotNull PocketTLTreeBuilderParser.Not_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#not_atom}.
	 * @param ctx the parse tree
	 */
	void exitNot_atom(@NotNull PocketTLTreeBuilderParser.Not_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(@NotNull PocketTLTreeBuilderParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(@NotNull PocketTLTreeBuilderParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator(@NotNull PocketTLTreeBuilderParser.OperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator(@NotNull PocketTLTreeBuilderParser.OperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#eq}.
	 * @param ctx the parse tree
	 */
	void enterEq(@NotNull PocketTLTreeBuilderParser.EqContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#eq}.
	 * @param ctx the parse tree
	 */
	void exitEq(@NotNull PocketTLTreeBuilderParser.EqContext ctx);
	/**
	 * Enter a parse tree produced by {@link PocketTLTreeBuilderParser#in}.
	 * @param ctx the parse tree
	 */
	void enterIn(@NotNull PocketTLTreeBuilderParser.InContext ctx);
	/**
	 * Exit a parse tree produced by {@link PocketTLTreeBuilderParser#in}.
	 * @param ctx the parse tree
	 */
	void exitIn(@NotNull PocketTLTreeBuilderParser.InContext ctx);
}