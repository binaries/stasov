// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLAll.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.transformer;

import com.pocketmath.stasov.util.StasovStrings;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PocketQLAllParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, AND=4, OR=5, NOT=6, IN=7, W=8, ALPHANUM=9;
	public static final int
		RULE_start = 0, RULE_expr = 1, RULE_terminal_leaf = 2, RULE_eq = 3;
	public static final String[] ruleNames = {
		"start", "expr", "terminal_leaf", "eq"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "'='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "AND", "OR", "NOT", "IN", "W", "ALPHANUM"
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
	public String getGrammarFileName() { return "PocketQLAll.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	public StringBuilder psb = new StringBuilder();
	public boolean changed = false;

	// convenience method
	private final static String conjoin(final String s1, final String s2, final String conjunction) {
	    return StasovStrings.conjoin(s1,s2,conjunction);
	}

	//
	// ALL-IMPORTANT :) METHODS FOR LOGGING / DEBUGGING
	//
	// TODO: Integrate with logging framework.
	//
	// Logging here is assumed to be fairly in depth or entirely off.  Logging of the parser
	// is considered low level in the overall perspective of the application.

	// (Intentionally short name.)
	/**
	 * Are we logging?
	 */
	protected boolean l() { return true; /* TODO: Set by configuration. */ }

	/**
	 * Log by rule with a message.
	 */
	protected void log(final String ruleName, final String msg) { System.out.println("[" + ruleName + "]: " + msg); }

	/**
	 * Log only that we're in the specified rule.
	 */
	protected void log(final String ruleName) { log(ruleName,""); }

	public PocketQLAllParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class StartContext extends ParserRuleContext {
		public String value;
		public ExprContext e;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_start; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLAllListener ) ((PocketQLAllListener)listener).enterStart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLAllListener ) ((PocketQLAllListener)listener).exitStart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLAllVisitor ) return ((PocketQLAllVisitor<? extends T>)visitor).visitStart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StartContext start() throws RecognitionException {
		StartContext _localctx = new StartContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_start);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(8); 
			((StartContext)_localctx).e = expr(0);
			}
			 ((StartContext)_localctx).value = ((StartContext)_localctx).e.value; changed=true; if (l()) log("start" + ((StartContext)_localctx).e.value); 
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

	public static class ExprContext extends ParserRuleContext {
		public String value;
		public ExprContext e1;
		public ExprContext e2;
		public ExprContext e3;
		public EqContext e;
		public ExprContext ne;
		public ExprContext ex;
		public List<TerminalNode> OR() { return getTokens(PocketQLAllParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(PocketQLAllParser.OR, i);
		}
		public List<TerminalNode> AND() { return getTokens(PocketQLAllParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(PocketQLAllParser.AND, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> W() { return getTokens(PocketQLAllParser.W); }
		public TerminalNode W(int i) {
			return getToken(PocketQLAllParser.W, i);
		}
		public EqContext eq() {
			return getRuleContext(EqContext.class,0);
		}
		public List<TerminalNode> NOT() { return getTokens(PocketQLAllParser.NOT); }
		public TerminalNode NOT(int i) {
			return getToken(PocketQLAllParser.NOT, i);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLAllListener ) ((PocketQLAllListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLAllListener ) ((PocketQLAllListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLAllVisitor ) return ((PocketQLAllVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			switch (_input.LA(1)) {
			case T__0:
				{
				setState(12); 
				match(T__0);
				setState(16);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==W) {
					{
					{
					setState(13); 
					match(W);
					}
					}
					setState(18);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(19); 
				((ExprContext)_localctx).e1 = expr(0);
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==W) {
					{
					{
					setState(20); 
					match(W);
					}
					}
					setState(25);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(26); 
				match(OR);
				setState(30);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==W) {
					{
					{
					setState(27); 
					match(W);
					}
					}
					setState(32);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(33); 
				((ExprContext)_localctx).e2 = expr(0);
				setState(37);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==W) {
					{
					{
					setState(34); 
					match(W);
					}
					}
					setState(39);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(40); 
				match(T__1);
				setState(44);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==W) {
					{
					{
					setState(41); 
					match(W);
					}
					}
					setState(46);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(47); 
				match(AND);
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==W) {
					{
					{
					setState(48); 
					match(W);
					}
					}
					setState(53);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(54); 
				((ExprContext)_localctx).e3 = expr(2);

				            String c1 = conjoin(((ExprContext)_localctx).e1.value, ((ExprContext)_localctx).e3.value, " AND ");
				            String c2 = conjoin(((ExprContext)_localctx).e2.value, ((ExprContext)_localctx).e3.value, " AND ");
				            ((ExprContext)_localctx).value =  conjoin(c1, c2, " OR ");
				            if (l()) log("expr", "( e1=expr OR e2=expr ) AND e3=expr");
				        
				}
				break;
			case ALPHANUM:
				{
				{
				setState(57); 
				((ExprContext)_localctx).e = eq();
				}
				 ((ExprContext)_localctx).value = ((ExprContext)_localctx).e.value; 
				}
				break;
			case NOT:
				{
				{
				setState(60); 
				match(NOT);
				setState(62); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(61); 
					match(W);
					}
					}
					setState(64); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==W );
				setState(66); 
				match(NOT);
				setState(68); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(67); 
					match(W);
					}
					}
					setState(70); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==W );
				setState(72); 
				((ExprContext)_localctx).ne = expr(0);
				}

				            ((ExprContext)_localctx).value =  ((ExprContext)_localctx).ne.value;
				            if (l()) log("expr", "double negation elimination");
				        
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(256);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprContext(_parentctx, _parentState);
					_localctx.e1 = _prevctx;
					_localctx.e1 = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(78);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(82);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==W) {
						{
						{
						setState(79); 
						match(W);
						}
						}
						setState(84);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(85); 
					match(AND);
					setState(89);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==W) {
						{
						{
						setState(86); 
						match(W);
						}
						}
						setState(91);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(92); 
					match(T__0);
					setState(96);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==W) {
						{
						{
						setState(93); 
						match(W);
						}
						}
						setState(98);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(99); 
					((ExprContext)_localctx).e2 = expr(0);
					setState(103);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==W) {
						{
						{
						setState(100); 
						match(W);
						}
						}
						setState(105);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(106); 
					match(OR);
					setState(110);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==W) {
						{
						{
						setState(107); 
						match(W);
						}
						}
						setState(112);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(113); 
					((ExprContext)_localctx).e3 = expr(0);
					setState(117);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==W) {
						{
						{
						setState(114); 
						match(W);
						}
						}
						setState(119);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(120); 
					match(T__1);

					                      String c1 = conjoin(((ExprContext)_localctx).e1.value, ((ExprContext)_localctx).e2.value, " AND ");
					                      String c2 = conjoin(((ExprContext)_localctx).e1.value, ((ExprContext)_localctx).e3.value, " AND ");
					                      ((ExprContext)_localctx).value =  conjoin(c1, c2, " OR ");
					                      if (l()) log("expr", "e1=expr AND ( e2=expr OR e3=expr )");
					                  
					setState(252);
					switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
					case 1:
						{
						}
						break;
					case 2:
						{
						setState(123); 
						match(T__0);
						setState(124); 
						((ExprContext)_localctx).e1 = expr(0);
						setState(128);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(125); 
							match(W);
							}
							}
							setState(130);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(131); 
						match(T__1);
						setState(135);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(132); 
							match(W);
							}
							}
							setState(137);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(138); 
						match(AND);
						setState(142);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(139); 
							match(W);
							}
							}
							setState(144);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(145); 
						match(T__0);
						setState(149);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(146); 
							match(W);
							}
							}
							setState(151);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(152); 
						((ExprContext)_localctx).e2 = expr(0);
						setState(156);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(153); 
							match(W);
							}
							}
							setState(158);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(159); 
						match(T__1);

						                          ((ExprContext)_localctx).value =  '(' + ((ExprContext)_localctx).e1.value + ") AND (" + ((ExprContext)_localctx).e2.value + ')';
						                          if (l()) log("expr", "AND");
						                      
						}
						break;
					case 3:
						{
						setState(162); 
						((ExprContext)_localctx).e1 = expr(0);
						setState(166);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(163); 
							match(W);
							}
							}
							setState(168);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(169); 
						match(AND);
						setState(173);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(170); 
							match(W);
							}
							}
							setState(175);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(176); 
						((ExprContext)_localctx).e2 = expr(0);

						                          ((ExprContext)_localctx).value =  ((ExprContext)_localctx).e1.value + " AND " + ((ExprContext)_localctx).e2.value;
						                          if (l()) log("expr", "AND");
						                      
						}
						break;
					case 4:
						{
						setState(179); 
						match(T__0);
						setState(180); 
						((ExprContext)_localctx).e1 = expr(0);
						setState(184);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(181); 
							match(W);
							}
							}
							setState(186);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(187); 
						match(T__1);
						setState(191);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(188); 
							match(W);
							}
							}
							setState(193);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(194); 
						match(OR);
						setState(198);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(195); 
							match(W);
							}
							}
							setState(200);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(201); 
						match(T__0);
						setState(205);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(202); 
							match(W);
							}
							}
							setState(207);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(208); 
						((ExprContext)_localctx).e2 = expr(0);
						setState(212);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(209); 
							match(W);
							}
							}
							setState(214);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(215); 
						match(T__1);

						                          ((ExprContext)_localctx).value =  '(' + ((ExprContext)_localctx).e1.value + ") OR (" + ((ExprContext)_localctx).e2.value + ')';
						                          if (l()) log("expr", "OR");
						                      
						}
						break;
					case 5:
						{
						setState(218); 
						((ExprContext)_localctx).e1 = expr(0);
						setState(222);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(219); 
							match(W);
							}
							}
							setState(224);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(225); 
						match(OR);
						setState(229);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(226); 
							match(W);
							}
							}
							setState(231);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(232); 
						((ExprContext)_localctx).e2 = expr(0);

						                          ((ExprContext)_localctx).value = ((ExprContext)_localctx).e1.value + " OR " + ((ExprContext)_localctx).e2.value;
						                          if (l()) log("expr", "OR");
						                      
						}
						break;
					case 6:
						{
						setState(235); 
						match(T__0);
						setState(239);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(236); 
							match(W);
							}
							}
							setState(241);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(242); 
						((ExprContext)_localctx).ex = expr(0);
						setState(246);
						_errHandler.sync(this);
						_la = _input.LA(1);
						while (_la==W) {
							{
							{
							setState(243); 
							match(W);
							}
							}
							setState(248);
							_errHandler.sync(this);
							_la = _input.LA(1);
						}
						setState(249); 
						match(T__1);

						                          ((ExprContext)_localctx).value = '(' + ((ExprContext)_localctx).ex.value + ')';
						                          if (l()) log("expr", "parens");
						                      
						}
						break;
					}
					}
					} 
				}
				setState(258);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Terminal_leafContext extends ParserRuleContext {
		public String value;
		public EqContext e;
		public TerminalNode NOT() { return getToken(PocketQLAllParser.NOT, 0); }
		public EqContext eq() {
			return getRuleContext(EqContext.class,0);
		}
		public List<TerminalNode> W() { return getTokens(PocketQLAllParser.W); }
		public TerminalNode W(int i) {
			return getToken(PocketQLAllParser.W, i);
		}
		public Terminal_leafContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_terminal_leaf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLAllListener ) ((PocketQLAllListener)listener).enterTerminal_leaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLAllListener ) ((PocketQLAllListener)listener).exitTerminal_leaf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLAllVisitor ) return ((PocketQLAllVisitor<? extends T>)visitor).visitTerminal_leaf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Terminal_leafContext terminal_leaf() throws RecognitionException {
		Terminal_leafContext _localctx = new Terminal_leafContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_terminal_leaf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			switch (_input.LA(1)) {
			case ALPHANUM:
				{
				setState(259); 
				((Terminal_leafContext)_localctx).e = eq();

				        ((Terminal_leafContext)_localctx).value =  ((Terminal_leafContext)_localctx).e.value;
				    
				}
				break;
			case NOT:
				{
				setState(262); 
				match(NOT);
				setState(264); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(263); 
					match(W);
					}
					}
					setState(266); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==W );
				setState(268); 
				((Terminal_leafContext)_localctx).e = eq();

				        ((Terminal_leafContext)_localctx).value =  "NOT " + ((Terminal_leafContext)_localctx).e.value;
				    
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			         System.out.println("VALUE: " + _localctx.value); 
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
		public String value;
		public Token a;
		public Token b;
		public List<TerminalNode> ALPHANUM() { return getTokens(PocketQLAllParser.ALPHANUM); }
		public TerminalNode ALPHANUM(int i) {
			return getToken(PocketQLAllParser.ALPHANUM, i);
		}
		public List<TerminalNode> W() { return getTokens(PocketQLAllParser.W); }
		public TerminalNode W(int i) {
			return getToken(PocketQLAllParser.W, i);
		}
		public EqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLAllListener ) ((PocketQLAllListener)listener).enterEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLAllListener ) ((PocketQLAllListener)listener).exitEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLAllVisitor ) return ((PocketQLAllVisitor<? extends T>)visitor).visitEq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqContext eq() throws RecognitionException {
		EqContext _localctx = new EqContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_eq);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(275); 
			((EqContext)_localctx).a = match(ALPHANUM);
			setState(279);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==W) {
				{
				{
				setState(276); 
				match(W);
				}
				}
				setState(281);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(282); 
			match(T__2);
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==W) {
				{
				{
				setState(283); 
				match(W);
				}
				}
				setState(288);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(289); 
			((EqContext)_localctx).b = match(ALPHANUM);
			}
			 ((EqContext)_localctx).value =  ((EqContext)_localctx).a.getText() + "=" + ((EqContext)_localctx).b.getText(); if (l()) log("eq", "value=" + _localctx.value); 
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1: 
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: 
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\13\u0128\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\3\3\3\3\3\7\3\21\n\3\f\3\16\3\24"+
		"\13\3\3\3\3\3\7\3\30\n\3\f\3\16\3\33\13\3\3\3\3\3\7\3\37\n\3\f\3\16\3"+
		"\"\13\3\3\3\3\3\7\3&\n\3\f\3\16\3)\13\3\3\3\3\3\7\3-\n\3\f\3\16\3\60\13"+
		"\3\3\3\3\3\7\3\64\n\3\f\3\16\3\67\13\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\6\3A\n\3\r\3\16\3B\3\3\3\3\6\3G\n\3\r\3\16\3H\3\3\3\3\3\3\3\3\5\3O\n"+
		"\3\3\3\3\3\7\3S\n\3\f\3\16\3V\13\3\3\3\3\3\7\3Z\n\3\f\3\16\3]\13\3\3\3"+
		"\3\3\7\3a\n\3\f\3\16\3d\13\3\3\3\3\3\7\3h\n\3\f\3\16\3k\13\3\3\3\3\3\7"+
		"\3o\n\3\f\3\16\3r\13\3\3\3\3\3\7\3v\n\3\f\3\16\3y\13\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\7\3\u0081\n\3\f\3\16\3\u0084\13\3\3\3\3\3\7\3\u0088\n\3\f\3\16"+
		"\3\u008b\13\3\3\3\3\3\7\3\u008f\n\3\f\3\16\3\u0092\13\3\3\3\3\3\7\3\u0096"+
		"\n\3\f\3\16\3\u0099\13\3\3\3\3\3\7\3\u009d\n\3\f\3\16\3\u00a0\13\3\3\3"+
		"\3\3\3\3\3\3\3\3\7\3\u00a7\n\3\f\3\16\3\u00aa\13\3\3\3\3\3\7\3\u00ae\n"+
		"\3\f\3\16\3\u00b1\13\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3\u00b9\n\3\f\3\16\3"+
		"\u00bc\13\3\3\3\3\3\7\3\u00c0\n\3\f\3\16\3\u00c3\13\3\3\3\3\3\7\3\u00c7"+
		"\n\3\f\3\16\3\u00ca\13\3\3\3\3\3\7\3\u00ce\n\3\f\3\16\3\u00d1\13\3\3\3"+
		"\3\3\7\3\u00d5\n\3\f\3\16\3\u00d8\13\3\3\3\3\3\3\3\3\3\3\3\7\3\u00df\n"+
		"\3\f\3\16\3\u00e2\13\3\3\3\3\3\7\3\u00e6\n\3\f\3\16\3\u00e9\13\3\3\3\3"+
		"\3\3\3\3\3\3\3\7\3\u00f0\n\3\f\3\16\3\u00f3\13\3\3\3\3\3\7\3\u00f7\n\3"+
		"\f\3\16\3\u00fa\13\3\3\3\3\3\3\3\5\3\u00ff\n\3\7\3\u0101\n\3\f\3\16\3"+
		"\u0104\13\3\3\4\3\4\3\4\3\4\3\4\6\4\u010b\n\4\r\4\16\4\u010c\3\4\3\4\3"+
		"\4\5\4\u0112\n\4\3\4\3\4\3\5\3\5\7\5\u0118\n\5\f\5\16\5\u011b\13\5\3\5"+
		"\3\5\7\5\u011f\n\5\f\5\16\5\u0122\13\5\3\5\3\5\3\5\3\5\3\5\2\3\4\6\2\4"+
		"\6\b\2\2\u014d\2\n\3\2\2\2\4N\3\2\2\2\6\u0111\3\2\2\2\b\u0115\3\2\2\2"+
		"\n\13\5\4\3\2\13\f\b\2\1\2\f\3\3\2\2\2\r\16\b\3\1\2\16\22\7\3\2\2\17\21"+
		"\7\n\2\2\20\17\3\2\2\2\21\24\3\2\2\2\22\20\3\2\2\2\22\23\3\2\2\2\23\25"+
		"\3\2\2\2\24\22\3\2\2\2\25\31\5\4\3\2\26\30\7\n\2\2\27\26\3\2\2\2\30\33"+
		"\3\2\2\2\31\27\3\2\2\2\31\32\3\2\2\2\32\34\3\2\2\2\33\31\3\2\2\2\34 \7"+
		"\7\2\2\35\37\7\n\2\2\36\35\3\2\2\2\37\"\3\2\2\2 \36\3\2\2\2 !\3\2\2\2"+
		"!#\3\2\2\2\" \3\2\2\2#\'\5\4\3\2$&\7\n\2\2%$\3\2\2\2&)\3\2\2\2\'%\3\2"+
		"\2\2\'(\3\2\2\2(*\3\2\2\2)\'\3\2\2\2*.\7\4\2\2+-\7\n\2\2,+\3\2\2\2-\60"+
		"\3\2\2\2.,\3\2\2\2./\3\2\2\2/\61\3\2\2\2\60.\3\2\2\2\61\65\7\6\2\2\62"+
		"\64\7\n\2\2\63\62\3\2\2\2\64\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\66"+
		"8\3\2\2\2\67\65\3\2\2\289\5\4\3\49:\b\3\1\2:O\3\2\2\2;<\5\b\5\2<=\b\3"+
		"\1\2=O\3\2\2\2>@\7\b\2\2?A\7\n\2\2@?\3\2\2\2AB\3\2\2\2B@\3\2\2\2BC\3\2"+
		"\2\2CD\3\2\2\2DF\7\b\2\2EG\7\n\2\2FE\3\2\2\2GH\3\2\2\2HF\3\2\2\2HI\3\2"+
		"\2\2IJ\3\2\2\2JK\5\4\3\2KL\3\2\2\2LM\b\3\1\2MO\3\2\2\2N\r\3\2\2\2N;\3"+
		"\2\2\2N>\3\2\2\2O\u0102\3\2\2\2PT\f\3\2\2QS\7\n\2\2RQ\3\2\2\2SV\3\2\2"+
		"\2TR\3\2\2\2TU\3\2\2\2UW\3\2\2\2VT\3\2\2\2W[\7\6\2\2XZ\7\n\2\2YX\3\2\2"+
		"\2Z]\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\^\3\2\2\2][\3\2\2\2^b\7\3\2\2_a\7\n"+
		"\2\2`_\3\2\2\2ad\3\2\2\2b`\3\2\2\2bc\3\2\2\2ce\3\2\2\2db\3\2\2\2ei\5\4"+
		"\3\2fh\7\n\2\2gf\3\2\2\2hk\3\2\2\2ig\3\2\2\2ij\3\2\2\2jl\3\2\2\2ki\3\2"+
		"\2\2lp\7\7\2\2mo\7\n\2\2nm\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2"+
		"\2\2rp\3\2\2\2sw\5\4\3\2tv\7\n\2\2ut\3\2\2\2vy\3\2\2\2wu\3\2\2\2wx\3\2"+
		"\2\2xz\3\2\2\2yw\3\2\2\2z{\7\4\2\2{\u00fe\b\3\1\2|\u00ff\3\2\2\2}~\7\3"+
		"\2\2~\u0082\5\4\3\2\177\u0081\7\n\2\2\u0080\177\3\2\2\2\u0081\u0084\3"+
		"\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2\2\2\u0084"+
		"\u0082\3\2\2\2\u0085\u0089\7\4\2\2\u0086\u0088\7\n\2\2\u0087\u0086\3\2"+
		"\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a"+
		"\u008c\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u0090\7\6\2\2\u008d\u008f\7\n"+
		"\2\2\u008e\u008d\3\2\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090"+
		"\u0091\3\2\2\2\u0091\u0093\3\2\2\2\u0092\u0090\3\2\2\2\u0093\u0097\7\3"+
		"\2\2\u0094\u0096\7\n\2\2\u0095\u0094\3\2\2\2\u0096\u0099\3\2\2\2\u0097"+
		"\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u009a\3\2\2\2\u0099\u0097\3\2"+
		"\2\2\u009a\u009e\5\4\3\2\u009b\u009d\7\n\2\2\u009c\u009b\3\2\2\2\u009d"+
		"\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a1\3\2"+
		"\2\2\u00a0\u009e\3\2\2\2\u00a1\u00a2\7\4\2\2\u00a2\u00a3\b\3\1\2\u00a3"+
		"\u00ff\3\2\2\2\u00a4\u00a8\5\4\3\2\u00a5\u00a7\7\n\2\2\u00a6\u00a5\3\2"+
		"\2\2\u00a7\u00aa\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9"+
		"\u00ab\3\2\2\2\u00aa\u00a8\3\2\2\2\u00ab\u00af\7\6\2\2\u00ac\u00ae\7\n"+
		"\2\2\u00ad\u00ac\3\2\2\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3\2\2\2\u00af"+
		"\u00b0\3\2\2\2\u00b0\u00b2\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b3\5\4"+
		"\3\2\u00b3\u00b4\b\3\1\2\u00b4\u00ff\3\2\2\2\u00b5\u00b6\7\3\2\2\u00b6"+
		"\u00ba\5\4\3\2\u00b7\u00b9\7\n\2\2\u00b8\u00b7\3\2\2\2\u00b9\u00bc\3\2"+
		"\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bd\3\2\2\2\u00bc"+
		"\u00ba\3\2\2\2\u00bd\u00c1\7\4\2\2\u00be\u00c0\7\n\2\2\u00bf\u00be\3\2"+
		"\2\2\u00c0\u00c3\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2"+
		"\u00c4\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c4\u00c8\7\7\2\2\u00c5\u00c7\7\n"+
		"\2\2\u00c6\u00c5\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8"+
		"\u00c9\3\2\2\2\u00c9\u00cb\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00cf\7\3"+
		"\2\2\u00cc\u00ce\7\n\2\2\u00cd\u00cc\3\2\2\2\u00ce\u00d1\3\2\2\2\u00cf"+
		"\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d2\3\2\2\2\u00d1\u00cf\3\2"+
		"\2\2\u00d2\u00d6\5\4\3\2\u00d3\u00d5\7\n\2\2\u00d4\u00d3\3\2\2\2\u00d5"+
		"\u00d8\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d9\3\2"+
		"\2\2\u00d8\u00d6\3\2\2\2\u00d9\u00da\7\4\2\2\u00da\u00db\b\3\1\2\u00db"+
		"\u00ff\3\2\2\2\u00dc\u00e0\5\4\3\2\u00dd\u00df\7\n\2\2\u00de\u00dd\3\2"+
		"\2\2\u00df\u00e2\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00e3\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e3\u00e7\7\7\2\2\u00e4\u00e6\7\n"+
		"\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e9\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7"+
		"\u00e8\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00eb\5\4"+
		"\3\2\u00eb\u00ec\b\3\1\2\u00ec\u00ff\3\2\2\2\u00ed\u00f1\7\3\2\2\u00ee"+
		"\u00f0\7\n\2\2\u00ef\u00ee\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1\u00ef\3\2"+
		"\2\2\u00f1\u00f2\3\2\2\2\u00f2\u00f4\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f4"+
		"\u00f8\5\4\3\2\u00f5\u00f7\7\n\2\2\u00f6\u00f5\3\2\2\2\u00f7\u00fa\3\2"+
		"\2\2\u00f8\u00f6\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fb\3\2\2\2\u00fa"+
		"\u00f8\3\2\2\2\u00fb\u00fc\7\4\2\2\u00fc\u00fd\b\3\1\2\u00fd\u00ff\3\2"+
		"\2\2\u00fe|\3\2\2\2\u00fe}\3\2\2\2\u00fe\u00a4\3\2\2\2\u00fe\u00b5\3\2"+
		"\2\2\u00fe\u00dc\3\2\2\2\u00fe\u00ed\3\2\2\2\u00ff\u0101\3\2\2\2\u0100"+
		"P\3\2\2\2\u0101\u0104\3\2\2\2\u0102\u0100\3\2\2\2\u0102\u0103\3\2\2\2"+
		"\u0103\5\3\2\2\2\u0104\u0102\3\2\2\2\u0105\u0106\5\b\5\2\u0106\u0107\b"+
		"\4\1\2\u0107\u0112\3\2\2\2\u0108\u010a\7\b\2\2\u0109\u010b\7\n\2\2\u010a"+
		"\u0109\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010a\3\2\2\2\u010c\u010d\3\2"+
		"\2\2\u010d\u010e\3\2\2\2\u010e\u010f\5\b\5\2\u010f\u0110\b\4\1\2\u0110"+
		"\u0112\3\2\2\2\u0111\u0105\3\2\2\2\u0111\u0108\3\2\2\2\u0112\u0113\3\2"+
		"\2\2\u0113\u0114\b\4\1\2\u0114\7\3\2\2\2\u0115\u0119\7\13\2\2\u0116\u0118"+
		"\7\n\2\2\u0117\u0116\3\2\2\2\u0118\u011b\3\2\2\2\u0119\u0117\3\2\2\2\u0119"+
		"\u011a\3\2\2\2\u011a\u011c\3\2\2\2\u011b\u0119\3\2\2\2\u011c\u0120\7\5"+
		"\2\2\u011d\u011f\7\n\2\2\u011e\u011d\3\2\2\2\u011f\u0122\3\2\2\2\u0120"+
		"\u011e\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0123\3\2\2\2\u0122\u0120\3\2"+
		"\2\2\u0123\u0124\7\13\2\2\u0124\u0125\3\2\2\2\u0125\u0126\b\5\1\2\u0126"+
		"\t\3\2\2\2\'\22\31 \'.\65BHNT[bipw\u0082\u0089\u0090\u0097\u009e\u00a8"+
		"\u00af\u00ba\u00c1\u00c8\u00cf\u00d6\u00e0\u00e7\u00f1\u00f8\u00fe\u0102"+
		"\u010c\u0111\u0119\u0120";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}