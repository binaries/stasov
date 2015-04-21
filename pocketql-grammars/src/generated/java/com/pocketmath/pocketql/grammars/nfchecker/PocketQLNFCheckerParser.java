// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLNFChecker.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.nfchecker;

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
public class PocketQLNFCheckerParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, AND=2, OR=3, NOT=4, IN=5, W=6, ALPHANUM=7;
	public static final int
		RULE_normal_form = 0, RULE_terminal_leaf = 1, RULE_eq = 2;
	public static final String[] ruleNames = {
		"normal_form", "terminal_leaf", "eq"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "AND", "OR", "NOT", "IN", "W", "ALPHANUM"
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
	public String getGrammarFileName() { return "PocketQLNFChecker.g4"; }

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

	public PocketQLNFCheckerParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Normal_formContext extends ParserRuleContext {
		public String value;
		public List<Terminal_leafContext> terminal_leaf() {
			return getRuleContexts(Terminal_leafContext.class);
		}
		public Terminal_leafContext terminal_leaf(int i) {
			return getRuleContext(Terminal_leafContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(PocketQLNFCheckerParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(PocketQLNFCheckerParser.AND, i);
		}
		public List<TerminalNode> W() { return getTokens(PocketQLNFCheckerParser.W); }
		public TerminalNode W(int i) {
			return getToken(PocketQLNFCheckerParser.W, i);
		}
		public List<TerminalNode> OR() { return getTokens(PocketQLNFCheckerParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(PocketQLNFCheckerParser.OR, i);
		}
		public Normal_formContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_normal_form; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNFCheckerListener ) ((PocketQLNFCheckerListener)listener).enterNormal_form(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNFCheckerListener ) ((PocketQLNFCheckerListener)listener).exitNormal_form(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNFCheckerVisitor ) return ((PocketQLNFCheckerVisitor<? extends T>)visitor).visitNormal_form(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Normal_formContext normal_form() throws RecognitionException {
		Normal_formContext _localctx = new Normal_formContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_normal_form);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(6); 
				terminal_leaf();
				setState(21);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(8); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(7); 
							match(W);
							}
							}
							setState(10); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==W );
						setState(12); 
						match(AND);
						setState(14); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(13); 
							match(W);
							}
							}
							setState(16); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==W );
						setState(18); 
						terminal_leaf();
						}
						} 
					}
					setState(23);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
				}
				setState(25); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(24); 
					match(W);
					}
					}
					setState(27); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==W );
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==OR) {
					{
					{
					setState(29); 
					match(OR);
					setState(31); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(30); 
						match(W);
						}
						}
						setState(33); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==W );
					setState(35); 
					terminal_leaf();
					setState(50);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==W) {
						{
						{
						setState(37); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(36); 
							match(W);
							}
							}
							setState(39); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==W );
						setState(41); 
						match(AND);
						setState(43); 
						_errHandler.sync(this);
						_la = _input.LA(1);
						do {
							{
							{
							setState(42); 
							match(W);
							}
							}
							setState(45); 
							_errHandler.sync(this);
							_la = _input.LA(1);
						} while ( _la==W );
						setState(47); 
						terminal_leaf();
						}
						}
						setState(52);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					setState(57);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				{
				setState(58); 
				terminal_leaf();
				}
				break;
			}

			        ((Normal_formContext)_localctx).value = getContext().getText();
			        if (l()) log("normal_form", "value=" + _localctx.value);
			        System.out.println("nf=" + _localctx.value);
			    
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

	public static class Terminal_leafContext extends ParserRuleContext {
		public String value;
		public EqContext e;
		public TerminalNode NOT() { return getToken(PocketQLNFCheckerParser.NOT, 0); }
		public EqContext eq() {
			return getRuleContext(EqContext.class,0);
		}
		public List<TerminalNode> W() { return getTokens(PocketQLNFCheckerParser.W); }
		public TerminalNode W(int i) {
			return getToken(PocketQLNFCheckerParser.W, i);
		}
		public Terminal_leafContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_terminal_leaf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNFCheckerListener ) ((PocketQLNFCheckerListener)listener).enterTerminal_leaf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNFCheckerListener ) ((PocketQLNFCheckerListener)listener).exitTerminal_leaf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNFCheckerVisitor ) return ((PocketQLNFCheckerVisitor<? extends T>)visitor).visitTerminal_leaf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Terminal_leafContext terminal_leaf() throws RecognitionException {
		Terminal_leafContext _localctx = new Terminal_leafContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_terminal_leaf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			switch (_input.LA(1)) {
			case ALPHANUM:
				{
				setState(63); 
				((Terminal_leafContext)_localctx).e = eq();

				        ((Terminal_leafContext)_localctx).value =  ((Terminal_leafContext)_localctx).e.value;
				    
				}
				break;
			case NOT:
				{
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
		public List<TerminalNode> ALPHANUM() { return getTokens(PocketQLNFCheckerParser.ALPHANUM); }
		public TerminalNode ALPHANUM(int i) {
			return getToken(PocketQLNFCheckerParser.ALPHANUM, i);
		}
		public List<TerminalNode> W() { return getTokens(PocketQLNFCheckerParser.W); }
		public TerminalNode W(int i) {
			return getToken(PocketQLNFCheckerParser.W, i);
		}
		public EqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eq; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNFCheckerListener ) ((PocketQLNFCheckerListener)listener).enterEq(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PocketQLNFCheckerListener ) ((PocketQLNFCheckerListener)listener).exitEq(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PocketQLNFCheckerVisitor ) return ((PocketQLNFCheckerVisitor<? extends T>)visitor).visitEq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqContext eq() throws RecognitionException {
		EqContext _localctx = new EqContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_eq);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(79); 
			((EqContext)_localctx).a = match(ALPHANUM);
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==W) {
				{
				{
				setState(80); 
				match(W);
				}
				}
				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(86); 
			match(T__0);
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==W) {
				{
				{
				setState(87); 
				match(W);
				}
				}
				setState(92);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(93); 
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\td\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\6\2\13\n\2\r\2\16\2\f\3\2\3\2\6\2\21\n\2\r\2\16\2\22"+
		"\3\2\7\2\26\n\2\f\2\16\2\31\13\2\3\2\6\2\34\n\2\r\2\16\2\35\3\2\3\2\6"+
		"\2\"\n\2\r\2\16\2#\3\2\3\2\6\2(\n\2\r\2\16\2)\3\2\3\2\6\2.\n\2\r\2\16"+
		"\2/\3\2\7\2\63\n\2\f\2\16\2\66\13\2\7\28\n\2\f\2\16\2;\13\2\3\2\5\2>\n"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\6\3G\n\3\r\3\16\3H\3\3\3\3\3\3\5\3N\n\3"+
		"\3\3\3\3\3\4\3\4\7\4T\n\4\f\4\16\4W\13\4\3\4\3\4\7\4[\n\4\f\4\16\4^\13"+
		"\4\3\4\3\4\3\4\3\4\3\4\2\2\5\2\4\6\2\2n\2=\3\2\2\2\4M\3\2\2\2\6Q\3\2\2"+
		"\2\b\27\5\4\3\2\t\13\7\b\2\2\n\t\3\2\2\2\13\f\3\2\2\2\f\n\3\2\2\2\f\r"+
		"\3\2\2\2\r\16\3\2\2\2\16\20\7\4\2\2\17\21\7\b\2\2\20\17\3\2\2\2\21\22"+
		"\3\2\2\2\22\20\3\2\2\2\22\23\3\2\2\2\23\24\3\2\2\2\24\26\5\4\3\2\25\n"+
		"\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27\30\3\2\2\2\30\33\3\2\2\2\31\27"+
		"\3\2\2\2\32\34\7\b\2\2\33\32\3\2\2\2\34\35\3\2\2\2\35\33\3\2\2\2\35\36"+
		"\3\2\2\2\369\3\2\2\2\37!\7\5\2\2 \"\7\b\2\2! \3\2\2\2\"#\3\2\2\2#!\3\2"+
		"\2\2#$\3\2\2\2$%\3\2\2\2%\64\5\4\3\2&(\7\b\2\2\'&\3\2\2\2()\3\2\2\2)\'"+
		"\3\2\2\2)*\3\2\2\2*+\3\2\2\2+-\7\4\2\2,.\7\b\2\2-,\3\2\2\2./\3\2\2\2/"+
		"-\3\2\2\2/\60\3\2\2\2\60\61\3\2\2\2\61\63\5\4\3\2\62\'\3\2\2\2\63\66\3"+
		"\2\2\2\64\62\3\2\2\2\64\65\3\2\2\2\658\3\2\2\2\66\64\3\2\2\2\67\37\3\2"+
		"\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:>\3\2\2\2;9\3\2\2\2<>\5\4\3\2=\b"+
		"\3\2\2\2=<\3\2\2\2>?\3\2\2\2?@\b\2\1\2@\3\3\2\2\2AB\5\6\4\2BC\b\3\1\2"+
		"CN\3\2\2\2DF\7\6\2\2EG\7\b\2\2FE\3\2\2\2GH\3\2\2\2HF\3\2\2\2HI\3\2\2\2"+
		"IJ\3\2\2\2JK\5\6\4\2KL\b\3\1\2LN\3\2\2\2MA\3\2\2\2MD\3\2\2\2NO\3\2\2\2"+
		"OP\b\3\1\2P\5\3\2\2\2QU\7\t\2\2RT\7\b\2\2SR\3\2\2\2TW\3\2\2\2US\3\2\2"+
		"\2UV\3\2\2\2VX\3\2\2\2WU\3\2\2\2X\\\7\3\2\2Y[\7\b\2\2ZY\3\2\2\2[^\3\2"+
		"\2\2\\Z\3\2\2\2\\]\3\2\2\2]_\3\2\2\2^\\\3\2\2\2_`\7\t\2\2`a\3\2\2\2ab"+
		"\b\4\1\2b\7\3\2\2\2\20\f\22\27\35#)/\649=HMU\\";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}