// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketTLTreeBuilder.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.anytree;


import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PocketTLTreeBuilderLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, AND=2, OR=3, NOT=4, IN=5, LT=6, GT=7, EQ=8, NEQ=9, LPAREN=10, 
		RPAREN=11, TRUE=12, FALSE=13, INT=14, FLOAT=15, STRING=16, ID=17, WS=18;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "AND", "OR", "NOT", "IN", "LT", "GT", "EQ", "NEQ", "LPAREN", "RPAREN", 
		"TRUE", "FALSE", "INT", "FLOAT", "STRING", "ID", "WS"
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


	public PocketTLTreeBuilderLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PocketTLTreeBuilder.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 15: 
			STRING_action((RuleContext)_localctx, actionIndex); 
			break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: 
			setText(getText().substring(1, getText().length()-1)); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\24\u008d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3"+
		"\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3"+
		"\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\6\17R\n\17\r\17"+
		"\16\17S\3\17\6\17W\n\17\r\17\16\17X\5\17[\n\17\3\20\3\20\6\20_\n\20\r"+
		"\20\16\20`\3\20\3\20\7\20e\n\20\f\20\16\20h\13\20\3\20\6\20k\n\20\r\20"+
		"\16\20l\3\20\3\20\7\20q\n\20\f\20\16\20t\13\20\5\20v\n\20\3\21\3\21\7"+
		"\21z\n\21\f\21\16\21}\13\21\3\21\3\21\3\21\3\21\3\22\5\22\u0084\n\22\3"+
		"\22\7\22\u0087\n\22\f\22\16\22\u008a\13\22\3\23\3\23\2\2\24\3\3\5\4\7"+
		"\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\3\2\24\4\2CCcc\4\2PPpp\4\2FFff\4\2QQqq\4\2TTtt\4\2VVvv\4\2KK"+
		"kk\4\2WWww\4\2GGgg\4\2HHhh\4\2NNnn\4\2UUuu\4\2--//\3\2\62;\7\2\"\"\62"+
		";C\\aac|\5\2C\\aac|\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\u0096\2\3\3\2\2"+
		"\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3"+
		"\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2"+
		"\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2"+
		"\2\2\3\'\3\2\2\2\5)\3\2\2\2\7-\3\2\2\2\t\60\3\2\2\2\13\64\3\2\2\2\r\67"+
		"\3\2\2\2\179\3\2\2\2\21;\3\2\2\2\23=\3\2\2\2\25@\3\2\2\2\27B\3\2\2\2\31"+
		"D\3\2\2\2\33I\3\2\2\2\35Z\3\2\2\2\37u\3\2\2\2!w\3\2\2\2#\u0083\3\2\2\2"+
		"%\u008b\3\2\2\2\'(\7.\2\2(\4\3\2\2\2)*\t\2\2\2*+\t\3\2\2+,\t\4\2\2,\6"+
		"\3\2\2\2-.\t\5\2\2./\t\6\2\2/\b\3\2\2\2\60\61\t\3\2\2\61\62\t\5\2\2\62"+
		"\63\t\7\2\2\63\n\3\2\2\2\64\65\t\b\2\2\65\66\t\3\2\2\66\f\3\2\2\2\678"+
		"\7>\2\28\16\3\2\2\29:\7@\2\2:\20\3\2\2\2;<\7?\2\2<\22\3\2\2\2=>\7#\2\2"+
		">?\7?\2\2?\24\3\2\2\2@A\7*\2\2A\26\3\2\2\2BC\7+\2\2C\30\3\2\2\2DE\t\7"+
		"\2\2EF\t\6\2\2FG\t\t\2\2GH\t\n\2\2H\32\3\2\2\2IJ\t\13\2\2JK\t\2\2\2KL"+
		"\t\f\2\2LM\t\r\2\2MN\t\n\2\2N\34\3\2\2\2OQ\t\16\2\2PR\t\17\2\2QP\3\2\2"+
		"\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T[\3\2\2\2UW\t\17\2\2VU\3\2\2\2WX\3\2"+
		"\2\2XV\3\2\2\2XY\3\2\2\2Y[\3\2\2\2ZO\3\2\2\2ZV\3\2\2\2[\36\3\2\2\2\\^"+
		"\t\16\2\2]_\t\17\2\2^]\3\2\2\2_`\3\2\2\2`^\3\2\2\2`a\3\2\2\2ab\3\2\2\2"+
		"bf\7\60\2\2ce\t\17\2\2dc\3\2\2\2eh\3\2\2\2fd\3\2\2\2fg\3\2\2\2gv\3\2\2"+
		"\2hf\3\2\2\2ik\t\17\2\2ji\3\2\2\2kl\3\2\2\2lj\3\2\2\2lm\3\2\2\2mn\3\2"+
		"\2\2nr\7\60\2\2oq\t\17\2\2po\3\2\2\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2sv\3"+
		"\2\2\2tr\3\2\2\2u\\\3\2\2\2uj\3\2\2\2v \3\2\2\2w{\7$\2\2xz\t\20\2\2yx"+
		"\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2\2|~\3\2\2\2}{\3\2\2\2~\177\7$\2\2"+
		"\177\u0080\3\2\2\2\u0080\u0081\b\21\2\2\u0081\"\3\2\2\2\u0082\u0084\t"+
		"\21\2\2\u0083\u0082\3\2\2\2\u0084\u0088\3\2\2\2\u0085\u0087\t\22\2\2\u0086"+
		"\u0085\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2"+
		"\2\2\u0089$\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008c\t\23\2\2\u008c&\3"+
		"\2\2\2\20\2SXZ`flruy{\u0083\u0086\u0088\3\3\21\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}