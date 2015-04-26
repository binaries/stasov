/**
 * A grammar which translates any valid boolean expression into a psuedo disjunctive normal form (DNF)
 * called PocketQL Normal Form (PQLNF) as required by other PocketQL modules.
 *
 * Example of DNF:   a AND b OR c AND d OR e AND f AND g
 *
 * DNF consists of atomic variables joined by the AND conjunction, and then these groups are in
 * turn joined together by the OR conjunction.
 *
 * The PocketQL Normal Form differs from strict DNF by considering the following to be leaves
 * treated as atomic elements would be by DNF:
 *   The equals statement:    a = 1
 *   The in list statement:   a IN (1,2,3)
 *
 * As such, the following is valid PQLNF:   a = 1 AND b = 2 OR c IN (1,2,3) AND d=7
 *
 * No parentheses are present in PQLNF.
 *
 * Double negations are factored out, and the following are valid PQLNF:
 *    a = 1 AND NOT b = 2 OR c IN (1,2,3) AND d=7
 *    a = 1 AND b = 2 OR NOT c IN (1,2,3) AND d=7
 *    a = 1 AND b = 2 OR c IN (1,NOT 2,3) AND d=7
 *
 * Strings are also legal:
 *    city = "Austin" AND deviceType="iPhone"
 */
grammar PocketQLAll;

// Helpful resource especially (1) re left factoring and (2) re-write rules.
// BUT ... Dated ... ANTLR 4 no longer supports rewrites like in ANTLR 3.
// http://meri-stuff.blogspot.com/2011/09/antlr-tutorial-expression-language.html

@header
{
import com.pocketmath.stasov.util.StasovStrings;
}

@parser::members
{

// register-like variables for intermediate results
private String e1v = null;
private String e2v = null;
private String e3v = null;

// convenience method
private final static String conjoin(final String s1, final String s2, final String conjunction) {
    return StasovStrings.conjoin(s1,s2,conjunction);
}

// convenience method
private final static String p(final String s) {
    return '(' + s + ')';
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
}

start returns [String value] :
    (e=expr)
        {
            $value=$e.value;
            if (l()) log("expr", "RESULT=" + $value);
        }
;

exp returns [String v] :
(
    e1=atom W* { e1v = $e1.v; }
    (
        EOF { $v=e1v; }
        |
        AND e2=exp EOF { $v=conjoin(e1v,$e2.v," AND "); }
        |
        OR e2=exp EOF { $v=conjoin(e1v,$e2.v," OR "); }
    )
    |
    '(' W*
    (
        ( e3=atom EOF ) { $v=$e3.v; }
        |
        ( '(' e4=exp ')' EOF ) { $v=p($e4.v); }
    )
    W* ')' EOF
)
    |
(
    NOT W*
    (
        e5=exp EOF { $v=$e5.v; }
        |
        NOT e6=exp EOF { $v=$e6.v; }
    )
)
;

atom returns [String v] :
    e=eq { $v=$e.value; }
;



expr returns [String value] :
    (e=eq)
        {
            $value=$e.value;
            if (l()) log("expr", "eq0");
            if (l()) log("expr", "RESULT=" + $value);
        }
    | ( NOT W+ NOT W+ ne=expr )
        {
            $value = $ne.value;
            if (l()) log("expr", "double negation elimination");
            if (l()) log("expr", "RESULT=" + $value);
        }
    | '(' W* e=eq W* ')'
        {
            if (l()) log("expr", "atomic parens elimination");
            $value = $e.value;
            if (l()) log("expr", "RESULT=" + $value);
        }
    (
        '(' W*
        e1=expr { $value = $e1.value; }
        W* OR W* e2=expr { $value += " OR " + $e2.value; }
        (W* OR W* e3=expr { $value += " OR " + $e3.value; } )*
        ')' W*
    )
       {
           if (l()) log("expr", "parens OR" + $value);
           if (l()) log("expr", "RESULT=" + $value);
       }

  //  | e1=and_expr W+ AND W+ e2=and_expr
  //      { $value= }
  //  | or_expr W+ AND W+ or_expr
    | '(' W* e1=expr W* OR W* e2=expr W* ')' W* AND W* e3=expr
        {
           if (l()) log("expr",
                "( e1=expr OR e2=expr ) AND e3=expr, INPUT: "
                + getContext().getText()
                + "VALUES: e1=,"
                + $e1.value
                + ", e2="
                + $e2.value
                + ", e3="
                + $e3.value);
            String c1 = conjoin( p($e1.value), p($e3.value), " AND ");
            String c2 = conjoin( p($e2.value), p($e3.value), " AND ");
            $value = conjoin(c1, c2, " OR ");
            if (l()) log("expr", "RESULT=" + $value);
        }
    | e1=expr W* AND W* '(' W* e2=expr W* OR W* e3=expr W* ')'
        {
            if (l()) log("expr",
                "e1=expr AND ( e2=expr OR e3=expr ), INPUT: "
                + getContext().getText()
                + ", VALUES: e1="
                + $e1.value
                + ", e2="
                + $e2.value
                + ", e3="
                + $e3.value);
            String c1 = conjoin( p($e1.value), p($e2.value), " AND ");
            String c2 = conjoin( p($e1.value), p($e3.value), " AND ");
            $value = conjoin(c1, c2, " OR ");
            if (l()) log("expr", "RESULT=" + $value);
        }
  // | '(' W* e1=expr {$value = $e1.value;} (W* AND W* e2=expr {$value = " AND " + $e2.value;})* ')'
  //      {
  //          if (l()) log("expr", "AND*");
  //      }
   (
       | '(' e1=expr W* ')' W* AND W* '(' W* e2=expr W* ')'
            {
                $value = p($e1.value) + " AND " + p($e2.value);
                if (l()) log("expr", "AND");
                if (l()) log("expr", "RESULT=" + $value);
            }

       | e1=expr W* AND W* e2=expr
            {
                $value = $e1.value + " AND " + $e2.value;
                if (l()) log("expr", "AND");
                if (l()) log("expr", "RESULT=" + $value);
            }
       | '(' e1=expr W* ')' W* OR W* '(' W* e2=expr W* ')'
            {
                $value = conjoin( p($e1.value), p($e2.value), " OR ");
                if (l()) log("expr", "OR");
                if (l()) log("expr", "RESULT=" + $value);
            }
       |(
            e1=expr { $value = $e1.value; }
            W* OR W* e2=expr { $value += " OR " + $e2.value; }
            (W* OR W* e3=expr { $value += " OR " + $e3.value; } )*
        )
            {
                if (l()) log("expr", " OR");
                if (l()) log("expr", "RESULT=" + $value);
            }
       | '(' W* ex=expr W* ')'
            {
                $value='(' + $ex.value + ')';
                if (l()) log("expr", "parens");
                if (l()) log("expr", "RESULT=" + $value);
            }
       | e1=expr W* AND W* e2=expr
            {
                $value = conjoin( $e1.value, $e2.value, " AND ");
                if (l()) log("expr", "AND simple");
                if (l()) log("expr", "RESULT=" + $value);
            }
       | e1=expr W* OR W* e2=expr
            {
                $value = conjoin( $e1.value, $e2.value, " OR ");
                if (l()) log("expr", "OR simple");
                if (l()) log("expr", "RESULT=" + $value);
            }
   )
;



/*
(
    | leaf_and_mult
    | mult_and_leaf

    | leaf_or_expr
    | leaf_and_expr

    | paren_leaf_and_expr
    | paren_expr_and_leaf

    | not_paren_and
    | not_paren_or

    | double_not {System.out.println("nootty"); psb.append("NNNnnotty");}

    | double_paren_expr
    | paren_leaf

    | paren_expr_and_expr

    | leaf
)
(
    (W* AND W* expr)*
    | (W* OR W* expr)*
)
;
*/
/*
leaf_or_expr:   leaf W+ OR W* expr;   // do nothing
leaf_and_expr:  leaf W+ AND W* expr;  // do nothing

leaf_and_mult:  leaf W+ AND W* '(' W* expr W* OR W* expr W* ')';   // multiply;  ==>  ( leaf AND expr1 ) OR ( leaf AND expr2 )
mult_and_leaf:  '(' W* expr W* OR W* expr W* ')' W* AND W+ expr;   // multiply;  ==>  ( leaf AND expr1 ) OR ( leaf AND expr2 )

paren_leaf_and_expr: '(' W* leaf W+ AND W* expr ')';     //  ==>  leaf AND ( expr )
paren_expr_and_leaf: '(' expr W* AND W+ leaf W* ')';     //  ==>  leaf AND ( expr )

not_paren_and:  NOT '(' expr W* AND W* expr ')';  //  ==>  NOT expr1 OR NOT expr2
not_paren_or:   NOT '(' expr W* OR W* expr ')';   //  ==>  NOT expr1 AND NOT expr2

double_not:     (NOT W+ NOT W+ expr);

double_paren_expr:    '(' W* '(' W* expr W* ')' W* ')';   //  ==>  ( expr )

paren_leaf:      '(' W* leaf W* ')';   //  ==> leaf

paren_expr_and_expr:  '(' W* expr W* AND W* expr W* ')';   //  ==>  expr1 AND expr2
*/
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
