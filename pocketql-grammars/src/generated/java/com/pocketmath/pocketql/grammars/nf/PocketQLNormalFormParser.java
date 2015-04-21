// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLNormalForm.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.nf;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PocketQLNormalFormParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, AND=5, OR=6, NOT=7, IN=8, WS=9, ALPHANUM=10;
	public static final int
		RULE_expr = 0, RULE_and_group = 1, RULE_not_leaf = 2, RULE_leaf = 3, RULE_in = 4, 
		RULE_eq = 5, RULE_list = 6;
	public static final String[] ruleNames = {
		"expr", "and_group", "not_leaf", "leaf", "in", "eq", "list"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'='", "'('", "','", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "AND", "OR", "NOT", "IN", "WS", "ALPHANUM"
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
	public String getGrammarFileName() { return "PocketQLNormalForm.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PocketQLNormalFormParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExprContext extends ParserRuleContext {
		public List<And_groupContext> and_group() {
			return getRuleContexts(And_groupContext.class);
		}
		public And_groupContext and_group(int i) {
			return getRuleContext(And_groupContext.class,i);
		}
		public List<TerminalNode> WS() { return getTokens(PocketQLNormalFormParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketQLNormalFormParser.WS, i);
		}
		public List<TerminalNode> OR() { return getTokens(PocketQLNormalFormParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(PocketQLNormalFormParser.OR, i);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNormalFormVisitor ) return ((PocketQLNormalFormVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(17);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(14); 
				match(WS);
				}
				}
				setState(19);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(20); 
			and_group();
			setState(35);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(22); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(21); 
						match(WS);
						}
						}
						setState(24); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WS );
					setState(26); 
					match(OR);
					setState(28); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(27); 
						match(WS);
						}
						}
						setState(30); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WS );
					setState(32); 
					and_group();
					}
					} 
				}
				setState(37);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(38); 
				match(WS);
				}
				}
				setState(43);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class And_groupContext extends ParserRuleContext {
		public List<Not_leafContext> not_leaf() {
			return getRuleContexts(Not_leafContext.class);
		}
		public Not_leafContext not_leaf(int i) {
			return getRuleContext(Not_leafContext.class,i);
		}
		public List<LeafContext> leaf() {
			return getRuleContexts(LeafContext.class);
		}
		public LeafContext leaf(int i) {
			return getRuleContext(LeafContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(PocketQLNormalFormParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(PocketQLNormalFormParser.AND, i);
		}
		public List<TerminalNode> WS() { return getTokens(PocketQLNormalFormParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketQLNormalFormParser.WS, i);
		}
		public And_groupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_and_group; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).enterAnd_group(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).exitAnd_group(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNormalFormVisitor ) return ((PocketQLNormalFormVisitor<? extends T>)visitor).visitAnd_group(this);
			else return visitor.visitChildren(this);
		}
	}

	public final And_groupContext and_group() throws RecognitionException {
		And_groupContext _localctx = new And_groupContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_and_group);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			switch (_input.LA(1)) {
			case NOT:
				{
				setState(44); 
				not_leaf();
				}
				break;
			case ALPHANUM:
				{
				setState(45); 
				leaf();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(65);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(49); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(48); 
						match(WS);
						}
						}
						setState(51); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WS );
					setState(53); 
					match(AND);
					setState(55); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(54); 
						match(WS);
						}
						}
						setState(57); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==WS );
					setState(61);
					switch (_input.LA(1)) {
					case NOT:
						{
						setState(59); 
						not_leaf();
						}
						break;
					case ALPHANUM:
						{
						setState(60); 
						leaf();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					} 
				}
				setState(67);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
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

	public static class Not_leafContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(PocketQLNormalFormParser.NOT, 0); }
		public LeafContext leaf() {
			return getRuleContext(LeafContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(PocketQLNormalFormParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketQLNormalFormParser.WS, i);
		}
		public Not_leafContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not_leaf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).enterNot_leaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).exitNot_leaf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNormalFormVisitor ) return ((PocketQLNormalFormVisitor<? extends T>)visitor).visitNot_leaf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Not_leafContext not_leaf() throws RecognitionException {
		Not_leafContext _localctx = new Not_leafContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_not_leaf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68); 
			match(NOT);
			setState(70); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(69); 
				match(WS);
				}
				}
				setState(72); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(74); 
			leaf();
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

	public static class LeafContext extends ParserRuleContext {
		public InContext in() {
			return getRuleContext(InContext.class,0);
		}
		public EqContext eq() {
			return getRuleContext(EqContext.class,0);
		}
		public LeafContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_leaf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).enterLeaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).exitLeaf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNormalFormVisitor ) return ((PocketQLNormalFormVisitor<? extends T>)visitor).visitLeaf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LeafContext leaf() throws RecognitionException {
		LeafContext _localctx = new LeafContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_leaf);
		try {
			setState(78);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(76); 
				in();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(77); 
				eq();
				}
				break;
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
		public TerminalNode ALPHANUM() { return getToken(PocketQLNormalFormParser.ALPHANUM, 0); }
		public TerminalNode IN() { return getToken(PocketQLNormalFormParser.IN, 0); }
		public ListContext list() {
			return getRuleContext(ListContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(PocketQLNormalFormParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketQLNormalFormParser.WS, i);
		}
		public InContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_in; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).enterIn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).exitIn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNormalFormVisitor ) return ((PocketQLNormalFormVisitor<? extends T>)visitor).visitIn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InContext in() throws RecognitionException {
		InContext _localctx = new InContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_in);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80); 
			match(ALPHANUM);
			setState(82); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(81); 
				match(WS);
				}
				}
				setState(84); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(86); 
			match(IN);
			setState(88); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(87); 
				match(WS);
				}
				}
				setState(90); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(92); 
			list();
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
		public List<TerminalNode> ALPHANUM() { return getTokens(PocketQLNormalFormParser.ALPHANUM); }
		public TerminalNode ALPHANUM(int i) {
			return getToken(PocketQLNormalFormParser.ALPHANUM, i);
		}
		public List<TerminalNode> WS() { return getTokens(PocketQLNormalFormParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketQLNormalFormParser.WS, i);
		}
		public EqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).enterEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).exitEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNormalFormVisitor ) return ((PocketQLNormalFormVisitor<? extends T>)visitor).visitEq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqContext eq() throws RecognitionException {
		EqContext _localctx = new EqContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_eq);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94); 
			match(ALPHANUM);
			setState(96); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(95); 
				match(WS);
				}
				}
				setState(98); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(100); 
			match(T__0);
			setState(102); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(101); 
				match(WS);
				}
				}
				setState(104); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(106); 
			match(ALPHANUM);
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

	public static class ListContext extends ParserRuleContext {
		public List<TerminalNode> WS() { return getTokens(PocketQLNormalFormParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(PocketQLNormalFormParser.WS, i);
		}
		public List<TerminalNode> ALPHANUM() { return getTokens(PocketQLNormalFormParser.ALPHANUM); }
		public TerminalNode ALPHANUM(int i) {
			return getToken(PocketQLNormalFormParser.ALPHANUM, i);
		}
		public ListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).enterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNormalFormListener ) ((PocketQLNormalFormListener)listener).exitList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNormalFormVisitor ) return ((PocketQLNormalFormVisitor<? extends T>)visitor).visitList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListContext list() throws RecognitionException {
		ListContext _localctx = new ListContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108); 
			match(T__1);
			setState(110); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(109); 
				match(WS);
				}
				}
				setState(112); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==WS );
			setState(126); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(114); 
				match(ALPHANUM);
				setState(116); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(115); 
					match(WS);
					}
					}
					setState(118); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WS );
				setState(120); 
				match(T__2);
				setState(122); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(121); 
					match(WS);
					}
					}
					setState(124); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==WS );
				}
				}
				setState(128); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==ALPHANUM );
			setState(130); 
			match(T__3);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\f\u0087\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\7\2\22\n\2\f\2\16\2"+
		"\25\13\2\3\2\3\2\6\2\31\n\2\r\2\16\2\32\3\2\3\2\6\2\37\n\2\r\2\16\2 \3"+
		"\2\7\2$\n\2\f\2\16\2\'\13\2\3\2\7\2*\n\2\f\2\16\2-\13\2\3\3\3\3\5\3\61"+
		"\n\3\3\3\6\3\64\n\3\r\3\16\3\65\3\3\3\3\6\3:\n\3\r\3\16\3;\3\3\3\3\5\3"+
		"@\n\3\7\3B\n\3\f\3\16\3E\13\3\3\4\3\4\6\4I\n\4\r\4\16\4J\3\4\3\4\3\5\3"+
		"\5\5\5Q\n\5\3\6\3\6\6\6U\n\6\r\6\16\6V\3\6\3\6\6\6[\n\6\r\6\16\6\\\3\6"+
		"\3\6\3\7\3\7\6\7c\n\7\r\7\16\7d\3\7\3\7\6\7i\n\7\r\7\16\7j\3\7\3\7\3\b"+
		"\3\b\6\bq\n\b\r\b\16\br\3\b\3\b\6\bw\n\b\r\b\16\bx\3\b\3\b\6\b}\n\b\r"+
		"\b\16\b~\6\b\u0081\n\b\r\b\16\b\u0082\3\b\3\b\3\b\2\2\t\2\4\6\b\n\f\16"+
		"\2\2\u0093\2\23\3\2\2\2\4\60\3\2\2\2\6F\3\2\2\2\bP\3\2\2\2\nR\3\2\2\2"+
		"\f`\3\2\2\2\16n\3\2\2\2\20\22\7\13\2\2\21\20\3\2\2\2\22\25\3\2\2\2\23"+
		"\21\3\2\2\2\23\24\3\2\2\2\24\26\3\2\2\2\25\23\3\2\2\2\26%\5\4\3\2\27\31"+
		"\7\13\2\2\30\27\3\2\2\2\31\32\3\2\2\2\32\30\3\2\2\2\32\33\3\2\2\2\33\34"+
		"\3\2\2\2\34\36\7\b\2\2\35\37\7\13\2\2\36\35\3\2\2\2\37 \3\2\2\2 \36\3"+
		"\2\2\2 !\3\2\2\2!\"\3\2\2\2\"$\5\4\3\2#\30\3\2\2\2$\'\3\2\2\2%#\3\2\2"+
		"\2%&\3\2\2\2&+\3\2\2\2\'%\3\2\2\2(*\7\13\2\2)(\3\2\2\2*-\3\2\2\2+)\3\2"+
		"\2\2+,\3\2\2\2,\3\3\2\2\2-+\3\2\2\2.\61\5\6\4\2/\61\5\b\5\2\60.\3\2\2"+
		"\2\60/\3\2\2\2\61C\3\2\2\2\62\64\7\13\2\2\63\62\3\2\2\2\64\65\3\2\2\2"+
		"\65\63\3\2\2\2\65\66\3\2\2\2\66\67\3\2\2\2\679\7\7\2\28:\7\13\2\298\3"+
		"\2\2\2:;\3\2\2\2;9\3\2\2\2;<\3\2\2\2<?\3\2\2\2=@\5\6\4\2>@\5\b\5\2?=\3"+
		"\2\2\2?>\3\2\2\2@B\3\2\2\2A\63\3\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2D"+
		"\5\3\2\2\2EC\3\2\2\2FH\7\t\2\2GI\7\13\2\2HG\3\2\2\2IJ\3\2\2\2JH\3\2\2"+
		"\2JK\3\2\2\2KL\3\2\2\2LM\5\b\5\2M\7\3\2\2\2NQ\5\n\6\2OQ\5\f\7\2PN\3\2"+
		"\2\2PO\3\2\2\2Q\t\3\2\2\2RT\7\f\2\2SU\7\13\2\2TS\3\2\2\2UV\3\2\2\2VT\3"+
		"\2\2\2VW\3\2\2\2WX\3\2\2\2XZ\7\n\2\2Y[\7\13\2\2ZY\3\2\2\2[\\\3\2\2\2\\"+
		"Z\3\2\2\2\\]\3\2\2\2]^\3\2\2\2^_\5\16\b\2_\13\3\2\2\2`b\7\f\2\2ac\7\13"+
		"\2\2ba\3\2\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2\2ef\3\2\2\2fh\7\3\2\2gi\7\13"+
		"\2\2hg\3\2\2\2ij\3\2\2\2jh\3\2\2\2jk\3\2\2\2kl\3\2\2\2lm\7\f\2\2m\r\3"+
		"\2\2\2np\7\4\2\2oq\7\13\2\2po\3\2\2\2qr\3\2\2\2rp\3\2\2\2rs\3\2\2\2s\u0080"+
		"\3\2\2\2tv\7\f\2\2uw\7\13\2\2vu\3\2\2\2wx\3\2\2\2xv\3\2\2\2xy\3\2\2\2"+
		"yz\3\2\2\2z|\7\5\2\2{}\7\13\2\2|{\3\2\2\2}~\3\2\2\2~|\3\2\2\2~\177\3\2"+
		"\2\2\177\u0081\3\2\2\2\u0080t\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0080"+
		"\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085\7\6\2\2\u0085"+
		"\17\3\2\2\2\26\23\32 %+\60\65;?CJPV\\djrx~\u0082";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}