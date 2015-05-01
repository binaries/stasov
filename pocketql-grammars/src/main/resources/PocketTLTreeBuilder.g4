grammar PocketTLTreeBuilder;

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

filter: ((NOT expression) | expression) EOF;

expression : or_expression;

or_expression : and_expression (OR and_expression)*;

and_expression : term (AND term)*;

term : in | atom (operator atom)? | LPAREN expression RPAREN;

atom : ID | INT | FLOAT | STRING | TRUE | FALSE;

operator : LT | GT | EQ | NEQ;

in: ID IN LPAREN atom (',' ((NOT atom) | atom) )* RPAREN;

TRUE : [tT][rR][uU][eE];
FALSE : [fF][aA][lL][sS][eE];
INT : (('-'|'+') [0-9]+) | [0-9]+;
FLOAT : (('-'|'+') [0-9]+ '.' [0-9]*) | ([0-9]+ '.' [0-9]*);
STRING : '"' ([a-zA-Z0-9]|'_')* '"';
ID : ([a-zA-Z]|'_') ([a-zA-Z0-9]|'_')*;
