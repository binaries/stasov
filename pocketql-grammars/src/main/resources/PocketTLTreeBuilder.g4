grammar PocketTLTreeBuilder;

@header
{
}

@parser::members
{
}

AND : [aA][nN][dD];
OR : [oO][rR];
NOT : [nN][oO][tT];
IN : [iI][nN];
LT : '<'; // TODO: not yet supported
GT : '>'; // TODO: not yet supported
EQ : '=';
NEQ : '!=';
LPAREN : '(';
RPAREN : ')';

//filter: ((NOT expression) | expression) EOF;

filter: expression EOF;

expression : or_expression;

or_expression : NOT? WS* and_expression (WS* OR WS+ and_expression WS*)*;

and_expression : NOT? term (WS* AND WS+ term WS*)*;

//term : in | atom (WS* operator WS* atom)? | LPAREN WS* expression WS* RPAREN;
term : NOT? ID WS* (eq | in) | LPAREN WS* expression WS* RPAREN;

not_atom : NOT WS+ atom;
atom : ID | INT | FLOAT | STRING | TRUE | FALSE;

//operator : LT | GT | EQ | NEQ;
operator : eq;

eq : EQ WS* atom;

in: IN WS* LPAREN WS* (not_atom | atom) WS* (',' WS* (not_atom | atom) WS* )* WS* RPAREN
;

TRUE : [tT][rR][uU][eE];
FALSE : [fF][aA][lL][sS][eE];
INT : (('-'|'+') [0-9]+) | [0-9]+;
FLOAT : (('-'|'+') [0-9]+ '.' [0-9]*) | ([0-9]+ '.' [0-9]*);
STRING : '"' ([a-zA-Z0-9]|'_')* '"';
ID : ([a-zA-Z]|'_') ([a-zA-Z0-9]|'_')*;
WS : ' ';