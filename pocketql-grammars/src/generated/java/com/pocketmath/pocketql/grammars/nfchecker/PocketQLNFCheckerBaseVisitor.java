// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLNFChecker.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.nfchecker;

import com.pocketmath.stasov.util.StasovStrings;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link PocketQLNFCheckerVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class PocketQLNFCheckerBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements PocketQLNFCheckerVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitNormal_form(@NotNull PocketQLNFCheckerParser.Normal_formContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitTerminal_leaf(@NotNull PocketQLNFCheckerParser.Terminal_leafContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitEq(@NotNull PocketQLNFCheckerParser.EqContext ctx) { return visitChildren(ctx); }
}