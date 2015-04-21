// Generated from /Users/etucker/IdeaProjects/stasov4/pocketql-grammars/src/main/resources/PocketQLNormalForm.g4 by ANTLR 4.5
package com.pocketmath.pocketql.grammars.nf;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PocketQLNormalFormLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, AND=5, OR=6, NOT=7, IN=8, WS=9, ALPHANUM=10;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "AND", "OR", "NOT", "IN", "WS", "ALPHANUM"
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


	public PocketQLNormalFormLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PocketQLNormalForm.g4"; }

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
		case 9: 
			ALPHANUM_action((RuleContext)_localctx, actionIndex); 
			break;
		}
	}
	private void ALPHANUM_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: 
			setText(getText().substring(1, getText().length()-1)); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\f@\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\b\3"+
		"\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\13\6\13\61\n\13\r\13\16\13\62\3\13\3"+
		"\13\3\13\6\138\n\13\r\13\16\139\3\13\3\13\3\13\5\13?\n\13\2\2\f\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\3\2\13\4\2CCcc\4\2PPpp\4\2FF"+
		"ff\4\2QQqq\4\2TTtt\4\2VVvv\4\2KKkk\5\2\13\f\17\17\"\"\5\2\62;C\\c|C\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\3\27\3\2\2\2\5"+
		"\31\3\2\2\2\7\33\3\2\2\2\t\35\3\2\2\2\13\37\3\2\2\2\r#\3\2\2\2\17&\3\2"+
		"\2\2\21*\3\2\2\2\23-\3\2\2\2\25>\3\2\2\2\27\30\7?\2\2\30\4\3\2\2\2\31"+
		"\32\7*\2\2\32\6\3\2\2\2\33\34\7.\2\2\34\b\3\2\2\2\35\36\7+\2\2\36\n\3"+
		"\2\2\2\37 \t\2\2\2 !\t\3\2\2!\"\t\4\2\2\"\f\3\2\2\2#$\t\5\2\2$%\t\6\2"+
		"\2%\16\3\2\2\2&\'\t\3\2\2\'(\t\5\2\2()\t\7\2\2)\20\3\2\2\2*+\t\b\2\2+"+
		",\t\3\2\2,\22\3\2\2\2-.\t\t\2\2.\24\3\2\2\2/\61\t\n\2\2\60/\3\2\2\2\61"+
		"\62\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63?\3\2\2\2\64\67\7$\2\2\658\t"+
		"\n\2\2\668\5\23\n\2\67\65\3\2\2\2\67\66\3\2\2\289\3\2\2\29\67\3\2\2\2"+
		"9:\3\2\2\2:;\3\2\2\2;<\7$\2\2<=\3\2\2\2=?\b\13\2\2>\60\3\2\2\2>\64\3\2"+
		"\2\2?\26\3\2\2\2\b\2\60\62\679>\3\3\13\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}