grammar PocketQLNormalForm;
// A grammar for conjunctive normal form.

expr: WS* and_group (WS+ OR WS+ and_group)* WS*;

and_group:  (not_leaf|leaf) (WS+ AND WS+ (not_leaf|leaf))*;

not_leaf:   NOT WS+ leaf;
leaf:   in | eq;

in:     ALPHANUM WS+ IN WS+ list;
eq:     ALPHANUM WS+ '=' WS+ ALPHANUM;
// not_in: ALPHANUM WS+ NOTIN WS+ list;

list:   '(' WS+ (ALPHANUM WS+ ',' WS+)+ ')';

// NOTIN   : NOT WS+ IN;

AND     : [Aa][Nn][Dd];
OR      : [Oo][Rr];
NOT     : [Nn][Oo][Tt];
IN      : [Ii][Nn];
WS      : (' '|'\t'|'\n'|'\r') ;
ALPHANUM: (
    ([a-z]|[A-Z]|[0-9])+
    | ( '"' ([a-z]|[A-Z]|[0-9]|WS)+ '"') {setText(getText().substring(1, getText().length()-1));} // strip the quotes
   );