// A grammar for use on PMTL already in disjunctive normal form.

grammar PocketQLNormalForm;

expr: WS* and_group (WS+ OR WS+ and_group)* WS*;

and_group:  (not_leaf|leaf) (WS+ AND WS+ (not_leaf|leaf))*;

not_leaf:   NOT WS+ leaf;
leaf:   in; // | eq;

in:     ALPHANUM WS+ IN WS+ list;
//eq:     ALPHANUM WS+ '=' WS+ ALPHANUM;
// not_in: ALPHANUM WS+ NOTIN WS+ list;

list:   '(' WS+ (ALPHANUM WS+ ',' WS+)+ ')';

// NOTIN   : NOT WS+ IN;

AND     : [Aa][Nn][Dd];
OR      : [Oo][Rr];
NOT     : [Nn][Oo][Tt];
IN      : [Ii][Nn];
ALPHANUM: (
    ([a-z]|[A-Z]|[0-9])+
    | ( '"' ([a-z]|[A-Z]|[0-9]|WS)+ '"') {setText(getText().substring(1, getText().length()-1));} // strip the quotes
    | ( '{' ([a-z]|[A-Z]|[0-9]|','|'.'|WS)+ '}') {setText(getText().substring(1, getText().length()-1));} // strip the curly brackets

   ); // TODO: Rename ALPHANUM to something more descriptive of what it's actually for given that the scope has broadened to non-alphanumeric characters.  Such renaming would require changes to user code relying on the generated grammar code as well as this grammar.
WS      : (' '|'\t'|'\n'|'\r') ;