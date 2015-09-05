// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketTLTreeBuilder.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.anytree;


import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PocketTLTreeBuilderParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PocketTLTreeBuilderVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#filter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilter(@NotNull PocketTLTreeBuilderParser.FilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(@NotNull PocketTLTreeBuilderParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#or_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr_expression(@NotNull PocketTLTreeBuilderParser.Or_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#and_expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd_expression(@NotNull PocketTLTreeBuilderParser.And_expressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(@NotNull PocketTLTreeBuilderParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#not_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot_atom(@NotNull PocketTLTreeBuilderParser.Not_atomContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(@NotNull PocketTLTreeBuilderParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator(@NotNull PocketTLTreeBuilderParser.OperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEq(@NotNull PocketTLTreeBuilderParser.EqContext ctx);
	/**
	 * Visit a parse tree produced by {@link PocketTLTreeBuilderParser#in}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIn(@NotNull PocketTLTreeBuilderParser.InContext ctx);
}