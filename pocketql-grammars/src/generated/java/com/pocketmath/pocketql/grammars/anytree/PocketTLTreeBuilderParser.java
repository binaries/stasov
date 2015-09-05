// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketTLTreeBuilder.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.anytree;


import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PocketTLTreeBuilderParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, AND=2, OR=3, NOT=4, IN=5, LT=6, GT=7, EQ=8, NEQ=9, LPAREN=10, 
		RPAREN=11, TRUE=12, FALSE=13, INT=14, FLOAT=15, STRING=16, ID=17, WS=18;
	public static final int
		RULE_filter = 0, RULE_expression = 1, RULE_or_expression = 2, RULE_and_expression = 3, 
		RULE_term = 4, RULE_not_atom = 5, RULE_atom = 6, RULE_operator = 7, RULE_eq = 8, 
		RULE_in = 9;
	public static final String[] ruleNames = {
		"filter", "expression", "or_expression", "and_expression", "term", "not_atom", 
		"atom", "operator", "eq", "in"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "','", null, null, null, null, "'<'", "'>'", "'='", "'!='", "'('", 
		"')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "AND", "OR", "NOT", "IN", "LT", "GT", "EQ", "NEQ", "LPAREN", 
		"RPAREN", "TRUE", "FALSE", "INT", "FLOAT", "STRING", "ID", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override
	@NotNull
	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "PocketTLTreeBuilder.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }



	public PocketTLTreeBuilderParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FilterContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(PocketTLTreeBuilderParser.EOF, 0); }
		public FilterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterFilter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitFilter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitFilter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FilterContext filter() throws RecognitionException {
		FilterContext _localctx = new FilterContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_filter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(20); 
			expression();
			setState(21); 
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public Or_expressionContext or_expression() {
			return getRuleContext(Or_expressionContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23); 
			or_expression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Or_expressionContext extends ParserRuleContext {
		public List<And_expressionContext> and_expression() {
			return getRuleContexts(And_expressionContext.class);
		}
		public And_expressionContext and_expression(int i) {
			return getRuleContext(And_expressionContext.class,i);
		}
		public TerminalNode NOT() { return getToken(PocketTLTreeBuilderParser.NOT, 0); }
		public List<TerminalNode> WS() { return getTokens(PocketTLTreeBuilderParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketTLTreeBuilderParser.WS, i);
		}
		public List<TerminalNode> OR() { return getTokens(PocketTLTreeBuilderParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(PocketTLTreeBuilderParser.OR, i);
		}
		public Or_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_or_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterOr_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitOr_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitOr_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Or_expressionContext or_expression() throws RecognitionException {
		Or_expressionContext _localctx = new Or_expressionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_or_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(25); 
				match(NOT);
				}
				break;
			}
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(28); 
				match(WS);
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(34); 
			and_expression();
			setState(56);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(38);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==WS) {
						{
						{
						setState(35); 
						match(WS);
						}
						}
						setState(40);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(41); 
					match(OR);
					setState(43); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(42); 
						match(WS);
						}
						}
						setState(45); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WS );
					setState(47); 
					and_expression();
					setState(51);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(48); 
							match(WS);
							}
							} 
						}
						setState(53);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
					}
					}
					} 
				}
				setState(58);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class And_expressionContext extends ParserRuleContext {
		public List<TermContext> term() {
			return getRuleContexts(TermContext.class);
		}
		public TermContext term(int i) {
			return getRuleContext(TermContext.class,i);
		}
		public TerminalNode NOT() { return getToken(PocketTLTreeBuilderParser.NOT, 0); }
		public List<TerminalNode> AND() { return getTokens(PocketTLTreeBuilderParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(PocketTLTreeBuilderParser.AND, i);
		}
		public List<TerminalNode> WS() { return getTokens(PocketTLTreeBuilderParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketTLTreeBuilderParser.WS, i);
		}
		public And_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterAnd_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitAnd_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitAnd_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final And_expressionContext and_expression() throws RecognitionException {
		And_expressionContext _localctx = new And_expressionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_and_expression);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(59); 
				match(NOT);
				}
				break;
			}
			setState(62); 
			term();
			setState(84);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(66);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==WS) {
						{
						{
						setState(63); 
						match(WS);
						}
						}
						setState(68);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(69); 
					match(AND);
					setState(71); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(70); 
						match(WS);
						}
						}
						setState(73); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WS );
					setState(75); 
					term();
					setState(79);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
					while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(76); 
							match(WS);
							}
							} 
						}
						setState(81);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
					}
					}
					} 
				}
				setState(86);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TermContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PocketTLTreeBuilderParser.ID, 0); }
		public EqContext eq() {
			return getRuleContext(EqContext.class,0);
		}
		public InContext in() {
			return getRuleContext(InContext.class,0);
		}
		public TerminalNode NOT() { return getToken(PocketTLTreeBuilderParser.NOT, 0); }
		public List<TerminalNode> WS() { return getTokens(PocketTLTreeBuilderParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketTLTreeBuilderParser.WS, i);
		}
		public TerminalNode LPAREN() { return getToken(PocketTLTreeBuilderParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(PocketTLTreeBuilderParser.RPAREN, 0); }
		public TermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_term; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitTerm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitTerm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TermContext term() throws RecognitionException {
		TermContext _localctx = new TermContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_term);
		int _la;
		try {
			int _alt;
			setState(117);
			switch (_input.LA(1)) {
			case NOT:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(88);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(87); 
					match(NOT);
					}
				}

				setState(90); 
				match(ID);
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WS) {
					{
					{
					setState(91); 
					match(WS);
					}
					}
					setState(96);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(99);
				switch (_input.LA(1)) {
				case EQ:
					{
					setState(97); 
					eq();
					}
					break;
				case IN:
					{
					setState(98); 
					in();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 2);
				{
				setState(101); 
				match(LPAREN);
				setState(105);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(102); 
						match(WS);
						}
						} 
					}
					setState(107);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
				}
				setState(108); 
				expression();
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WS) {
					{
					{
					setState(109); 
					match(WS);
					}
					}
					setState(114);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(115); 
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Not_atomContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PocketTLTreeBuilderParser.NOT, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(PocketTLTreeBuilderParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketTLTreeBuilderParser.WS, i);
		}
		public Not_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterNot_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitNot_atom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitNot_atom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Not_atomContext not_atom() throws RecognitionException {
		Not_atomContext _localctx = new Not_atomContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_not_atom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119); 
			match(NOT);
			setState(121); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(120); 
				match(WS);
				}
				}
				setState(123); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(125); 
			atom();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(PocketTLTreeBuilderParser.ID, 0); }
		public TerminalNode INT() { return getToken(PocketTLTreeBuilderParser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(PocketTLTreeBuilderParser.FLOAT, 0); }
		public TerminalNode STRING() { return getToken(PocketTLTreeBuilderParser.STRING, 0); }
		public TerminalNode TRUE() { return getToken(PocketTLTreeBuilderParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(PocketTLTreeBuilderParser.FALSE, 0); }
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_atom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << INT) | (1L << FLOAT) | (1L << STRING) | (1L << ID))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperatorContext extends ParserRuleContext {
		public EqContext eq() {
			return getRuleContext(EqContext.class,0);
		}
		public OperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperatorContext operator() throws RecognitionException {
		OperatorContext _localctx = new OperatorContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_operator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129); 
			eq();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EqContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(PocketTLTreeBuilderParser.EQ, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(PocketTLTreeBuilderParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketTLTreeBuilderParser.WS, i);
		}
		public EqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitEq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqContext eq() throws RecognitionException {
		EqContext _localctx = new EqContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_eq);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131); 
			match(EQ);
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(132); 
				match(WS);
				}
				}
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(138); 
			atom();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InContext extends ParserRuleContext {
		public TerminalNode IN() { return getToken(PocketTLTreeBuilderParser.IN, 0); }
		public TerminalNode LPAREN() { return getToken(PocketTLTreeBuilderParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PocketTLTreeBuilderParser.RPAREN, 0); }
		public List<Not_atomContext> not_atom() {
			return getRuleContexts(Not_atomContext.class);
		}
		public Not_atomContext not_atom(int i) {
			return getRuleContext(Not_atomContext.class,i);
		}
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public List<TerminalNode> WS() { return getTokens(PocketTLTreeBuilderParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketTLTreeBuilderParser.WS, i);
		}
		public InContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_in; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).enterIn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketTLTreeBuilderListener ) ((PocketTLTreeBuilderListener)listener).exitIn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketTLTreeBuilderVisitor ) return ((PocketTLTreeBuilderVisitor<? extends T>)visitor).visitIn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InContext in() throws RecognitionException {
		InContext _localctx = new InContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_in);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(140); 
			match(IN);
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(141); 
				match(WS);
				}
				}
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(147); 
			match(LPAREN);
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(148); 
				match(WS);
				}
				}
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(156);
			switch (_input.LA(1)) {
			case NOT:
				{
				setState(154); 
				not_atom();
				}
				break;
			case TRUE:
			case FALSE:
			case INT:
			case FLOAT:
			case STRING:
			case ID:
				{
				setState(155); 
				atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(161);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(158); 
					match(WS);
					}
					} 
				}
				setState(163);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			setState(183);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(164); 
				match(T__0);
				setState(168);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==WS) {
					{
					{
					setState(165); 
					match(WS);
					}
					}
					setState(170);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(173);
				switch (_input.LA(1)) {
				case NOT:
					{
					setState(171); 
					not_atom();
					}
					break;
				case TRUE:
				case FALSE:
				case INT:
				case FLOAT:
				case STRING:
				case ID:
					{
					setState(172); 
					atom();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(178);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(175); 
						match(WS);
						}
						} 
					}
					setState(180);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
				}
				}
				}
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(186); 
				match(WS);
				}
				}
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(192); 
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\24\u00c5\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\3\2\3\2\3\2\3\3\3\3\3\4\5\4\35\n\4\3\4\7\4 \n\4\f\4\16\4#\13\4\3"+
		"\4\3\4\7\4\'\n\4\f\4\16\4*\13\4\3\4\3\4\6\4.\n\4\r\4\16\4/\3\4\3\4\7\4"+
		"\64\n\4\f\4\16\4\67\13\4\7\49\n\4\f\4\16\4<\13\4\3\5\5\5?\n\5\3\5\3\5"+
		"\7\5C\n\5\f\5\16\5F\13\5\3\5\3\5\6\5J\n\5\r\5\16\5K\3\5\3\5\7\5P\n\5\f"+
		"\5\16\5S\13\5\7\5U\n\5\f\5\16\5X\13\5\3\6\5\6[\n\6\3\6\3\6\7\6_\n\6\f"+
		"\6\16\6b\13\6\3\6\3\6\5\6f\n\6\3\6\3\6\7\6j\n\6\f\6\16\6m\13\6\3\6\3\6"+
		"\7\6q\n\6\f\6\16\6t\13\6\3\6\3\6\5\6x\n\6\3\7\3\7\6\7|\n\7\r\7\16\7}\3"+
		"\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\7\n\u0088\n\n\f\n\16\n\u008b\13\n\3\n\3"+
		"\n\3\13\3\13\7\13\u0091\n\13\f\13\16\13\u0094\13\13\3\13\3\13\7\13\u0098"+
		"\n\13\f\13\16\13\u009b\13\13\3\13\3\13\5\13\u009f\n\13\3\13\7\13\u00a2"+
		"\n\13\f\13\16\13\u00a5\13\13\3\13\3\13\7\13\u00a9\n\13\f\13\16\13\u00ac"+
		"\13\13\3\13\3\13\5\13\u00b0\n\13\3\13\7\13\u00b3\n\13\f\13\16\13\u00b6"+
		"\13\13\7\13\u00b8\n\13\f\13\16\13\u00bb\13\13\3\13\7\13\u00be\n\13\f\13"+
		"\16\13\u00c1\13\13\3\13\3\13\3\13\2\2\f\2\4\6\b\n\f\16\20\22\24\2\3\3"+
		"\2\16\23\u00d6\2\26\3\2\2\2\4\31\3\2\2\2\6\34\3\2\2\2\b>\3\2\2\2\nw\3"+
		"\2\2\2\fy\3\2\2\2\16\u0081\3\2\2\2\20\u0083\3\2\2\2\22\u0085\3\2\2\2\24"+
		"\u008e\3\2\2\2\26\27\5\4\3\2\27\30\7\2\2\3\30\3\3\2\2\2\31\32\5\6\4\2"+
		"\32\5\3\2\2\2\33\35\7\6\2\2\34\33\3\2\2\2\34\35\3\2\2\2\35!\3\2\2\2\36"+
		" \7\24\2\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"$\3\2\2\2#"+
		"!\3\2\2\2$:\5\b\5\2%\'\7\24\2\2&%\3\2\2\2\'*\3\2\2\2(&\3\2\2\2()\3\2\2"+
		"\2)+\3\2\2\2*(\3\2\2\2+-\7\5\2\2,.\7\24\2\2-,\3\2\2\2./\3\2\2\2/-\3\2"+
		"\2\2/\60\3\2\2\2\60\61\3\2\2\2\61\65\5\b\5\2\62\64\7\24\2\2\63\62\3\2"+
		"\2\2\64\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\669\3\2\2\2\67\65\3\2\2"+
		"\28(\3\2\2\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;\7\3\2\2\2<:\3\2\2\2=?\7\6"+
		"\2\2>=\3\2\2\2>?\3\2\2\2?@\3\2\2\2@V\5\n\6\2AC\7\24\2\2BA\3\2\2\2CF\3"+
		"\2\2\2DB\3\2\2\2DE\3\2\2\2EG\3\2\2\2FD\3\2\2\2GI\7\4\2\2HJ\7\24\2\2IH"+
		"\3\2\2\2JK\3\2\2\2KI\3\2\2\2KL\3\2\2\2LM\3\2\2\2MQ\5\n\6\2NP\7\24\2\2"+
		"ON\3\2\2\2PS\3\2\2\2QO\3\2\2\2QR\3\2\2\2RU\3\2\2\2SQ\3\2\2\2TD\3\2\2\2"+
		"UX\3\2\2\2VT\3\2\2\2VW\3\2\2\2W\t\3\2\2\2XV\3\2\2\2Y[\7\6\2\2ZY\3\2\2"+
		"\2Z[\3\2\2\2[\\\3\2\2\2\\`\7\23\2\2]_\7\24\2\2^]\3\2\2\2_b\3\2\2\2`^\3"+
		"\2\2\2`a\3\2\2\2ae\3\2\2\2b`\3\2\2\2cf\5\22\n\2df\5\24\13\2ec\3\2\2\2"+
		"ed\3\2\2\2fx\3\2\2\2gk\7\f\2\2hj\7\24\2\2ih\3\2\2\2jm\3\2\2\2ki\3\2\2"+
		"\2kl\3\2\2\2ln\3\2\2\2mk\3\2\2\2nr\5\4\3\2oq\7\24\2\2po\3\2\2\2qt\3\2"+
		"\2\2rp\3\2\2\2rs\3\2\2\2su\3\2\2\2tr\3\2\2\2uv\7\r\2\2vx\3\2\2\2wZ\3\2"+
		"\2\2wg\3\2\2\2x\13\3\2\2\2y{\7\6\2\2z|\7\24\2\2{z\3\2\2\2|}\3\2\2\2}{"+
		"\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080\5\16\b\2\u0080\r\3\2\2\2\u0081"+
		"\u0082\t\2\2\2\u0082\17\3\2\2\2\u0083\u0084\5\22\n\2\u0084\21\3\2\2\2"+
		"\u0085\u0089\7\n\2\2\u0086\u0088\7\24\2\2\u0087\u0086\3\2\2\2\u0088\u008b"+
		"\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008c\3\2\2\2\u008b"+
		"\u0089\3\2\2\2\u008c\u008d\5\16\b\2\u008d\23\3\2\2\2\u008e\u0092\7\7\2"+
		"\2\u008f\u0091\7\24\2\2\u0090\u008f\3\2\2\2\u0091\u0094\3\2\2\2\u0092"+
		"\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0095\3\2\2\2\u0094\u0092\3\2"+
		"\2\2\u0095\u0099\7\f\2\2\u0096\u0098\7\24\2\2\u0097\u0096\3\2\2\2\u0098"+
		"\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009e\3\2"+
		"\2\2\u009b\u0099\3\2\2\2\u009c\u009f\5\f\7\2\u009d\u009f\5\16\b\2\u009e"+
		"\u009c\3\2\2\2\u009e\u009d\3\2\2\2\u009f\u00a3\3\2\2\2\u00a0\u00a2\7\24"+
		"\2\2\u00a1\u00a0\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a3"+
		"\u00a4\3\2\2\2\u00a4\u00b9\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6\u00aa\7\3"+
		"\2\2\u00a7\u00a9\7\24\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00ac\3\2\2\2\u00aa"+
		"\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00af\3\2\2\2\u00ac\u00aa\3\2"+
		"\2\2\u00ad\u00b0\5\f\7\2\u00ae\u00b0\5\16\b\2\u00af\u00ad\3\2\2\2\u00af"+
		"\u00ae\3\2\2\2\u00b0\u00b4\3\2\2\2\u00b1\u00b3\7\24\2\2\u00b2\u00b1\3"+
		"\2\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5"+
		"\u00b8\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7\u00a6\3\2\2\2\u00b8\u00bb\3\2"+
		"\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bf\3\2\2\2\u00bb"+
		"\u00b9\3\2\2\2\u00bc\u00be\7\24\2\2\u00bd\u00bc\3\2\2\2\u00be\u00c1\3"+
		"\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c2\3\2\2\2\u00c1"+
		"\u00bf\3\2\2\2\u00c2\u00c3\7\r\2\2\u00c3\25\3\2\2\2\36\34!(/\65:>DKQV"+
		"Z`ekrw}\u0089\u0092\u0099\u009e\u00a3\u00aa\u00af\u00b4\u00b9\u00bf";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}