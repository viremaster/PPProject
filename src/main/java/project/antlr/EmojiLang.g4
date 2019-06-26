grammar EmojiLang;

/** Pascal program. */
program
    : PROGRAM ID SEMI block EOF
    ;

/** Grouped sequence of statements. */
block
    : LBRACE (stat SEMI)+ RBRACE
    ;

/** Statement. */
stat: type ID ASS expr				 		#declvar
	| target ASS expr                		#assStat
    | IF LPAR expr RPAR stat (ELSE stat)? 	#ifStat
    | WHILE LPAR expr RPAR stat           	#whileStat
    | block                          		#blockStat
    | PARBEGIN LBRACE block block RBRACE	#parStat
    | LOCK ID								#lockStat
    | UNLOCK ID								#unlockStat
    | GLOBAL type ID ASS expr				#declgvar
    //| IN LPAR target RPAR  				#inStat
    //| OUT LPAR expr RPAR   				#outStat
    ;

/** Target of an assignment. */
target 
    : ID               #idTarget
    ;

/** Expression. */
expr: prfOp expr        #prfExpr
    | expr multOp expr  #multExpr
    | expr plusOp expr  #plusExpr
    | expr compOp expr  #compExpr
    | expr boolOp expr  #boolExpr
    | LPAR expr RPAR    #parExpr
    | ID                #idExpr
    | NUM               #numExpr
    | TRUE              #trueExpr
    | FALSE             #falseExpr
    ;

/** Prefix operator. */
prfOp: MINUS | NOT;

/** Multiplicative operator. */
multOp: STAR;

/** Additive operator. */
plusOp: PLUS | MINUS;

/** Boolean operator. */
boolOp: AND | OR;

/** Comparison operator. */
compOp: LE | LT | GE | GT | EQ | NE;

/** Data type. */
type: INT  #intType
    | BOOLEAN  #boolType
    ;

// Keywords
GLOBAL:  G L O B A L ;
UNLOCK:  U N L O C K ;
LOCK: 	 L O C K ;
PARBEGIN:P A R B E G I N ;
PAREND:  P A R E N D ;
BOOLEAN: B O O L E A N ;
ELSE:    E L S E ;
END:     E N D ;
FALSE:   F A L S E ;
IN:      I N ;
INT: 	 I N T ;
IF:      I F ;
OUT:     O U T ;
PROGRAM: P R O G R A M ;
TRUE:    T R U E ;
WHILE:   W H I L E ;

OR:		'||';
AND:	'&&';
NOT:	'!';
ASS:    '=';
COLON:  ':';
COMMA:  ',';
DOT:    '.';
DQUOTE: '"';
EQ:     '==';
GE:     '>=';
GT:     '>';
LE:     '<=';
LBRACE: '{';
LPAR:   '(';
LT:     '<';
MINUS:  '-';
NE:     '!=';
PLUS:   '+';
RBRACE: '}';
RPAR:   ')';
SEMI:   ';';
SLASH:  '/';
STAR:   '*';

// Content-bearing token types
ID: LETTER (LETTER | DIGIT)*;
NUM: DIGIT (DIGIT)*;
//STR: DQUOTE .*? DQUOTE;

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];

// Skipped token types
//COMMENT: LBRACE .*? RBRACE -> skip;
WS: [ \t\r\n]+ -> skip;

fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];
