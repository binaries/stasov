grammar PocketQLNFChecker;

// Helpful resource especially (1) re left factoring and (2) re-write rules.
// BUT ... Dated ... ANTLR 4 no longer supports rewrites like in ANTLR 3.
// http://meri-stuff.blogspot.com/2011/09/antlr-tutorial-expression-language.html

@header
{
import com.pocketmath.stasov.util.StasovStrings;
}

@parser::members
{
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
}

@init
{
psb = new StringBuilder();
changed = false;
}

normal_form returns [boolean match=false] :
    (
        (terminal_leaf (W+ AND W+ terminal_leaf)* W+ (OR W+ terminal_leaf (W+ AND W+ terminal_leaf)*)*)
        |
        terminal_leaf
    )
    {
        $match = true;
    }
;

terminal_leaf returns [String value]:
(
    e=eq
    {
        $value = $e.value;
    }
    | NOT W+ e=eq
    {
        $value = "NOT " + $e.value;
    }
) {         System.out.println("VALUE: " + $value); }
;


//leaf:       terminal_leaf | not_leaf;
//not_leaf:   NOT W+ leaf;


//in:     ALPHANUM WS+ IN WS+ list;
eq returns [String value] :
    ( a=ALPHANUM W* '=' W* b=ALPHANUM )
    { $value = $a.getText() + "=" + $b.getText(); if (l()) log("eq", "value=" + $value); }
;

// not_in: ALPHANUM WS+ NOTIN WS+ list;

//list:   '(' WS+ (ALPHANUM WS+ ',' WS+)+ ')';

// NOTIN   : NOT WS+ IN;

AND     : [Aa][Nn][Dd];
OR      : [Oo][Rr];
NOT     : [Nn][Oo][Tt];
IN      : [Ii][Nn];

// whitespace
W      : (' '|'\t'|'\n'|'\r');

ALPHANUM: (
    ([a-z]|[A-Z]|[0-9])+
    | ( '"' ([a-z]|[A-Z]|[0-9]|' '|'\t'|'\n'|'\r')+ '"')
   );
