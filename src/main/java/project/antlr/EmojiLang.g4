grammar EmojiLang;

/** Pascal program. */
program
    : PROGRAM ID SEMI block EOF
    ;

/** Grouped sequence of statements. */
block
    : LBRACE (stat SEMI)* RBRACE
    ;

/** Statement. */
stat: type ID ASS expr				 		#declvar
	| target ASS expr                		#assStat
    | IF LPAR expr RPAR stat (ELSE stat)? 	#ifStat
    | WHILE LPAR expr RPAR stat           	#whileStat
    | block                          		#blockStat
    | PARBEGIN LPAR block RPAR				#parStat
    | LOCK ID								#lockStat
    | UNLOCK ID								#unlockStat
    | GLOBAL type ID ASS expr				#declgvar
    | JOIN 									#joinstat
    //| IN LPAR target RPAR  				#inStat
    | OUT LPAR expr RPAR   					#outStat
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
multOp: STAR | SLASH;

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
JOIN:	 J O I N ;
GLOBAL:  G L O B A L ;
UNLOCK:  U N L O C K | '\u1F513';
LOCK: 	 L O C K | '\u1F512';
PARBEGIN:P A R B E G I N ;
PAREND:  P A R E N D ;
BOOLEAN: B O O L E A N | '\u1F313';
ELSE:    E L S E | '\u21AA';
END:     E N D ;
FALSE:   F A L S E | '\u1F311';
IN:      I N ;
INT: 	 I N T | '\u1F522';
IF:      I F | '\u1F500';
OUT:     O U T ;
PROGRAM: P R O G R A M | '\u{1F4BB}';
TRUE:    T R U E | '\u1F315';
WHILE:   W H I L E | '\u267B';

OR:		'||' | '\u23F8';
AND:	'&&' | '\u2194';
NOT:	'!' | '\u274c';
ASS:    '=' | '\u2B05';
COLON:  ':';
COMMA:  ',';
DOT:    '.';
DQUOTE: '"';
EQ:     '==' | '\u1F504';
GE:     '>=' | '\u23ED';
GT:     '>' | '\u23E9';
LE:     '<=' | '\u23EE';
LBRACE: '{';
LPAR:   '(' | '\u25C0';
LT:     '<' | '\u23EA';
MINUS:  '-' | '\u2796';
NE:     '!=' | '\u274c''\u1F504';
PLUS:   '+' | '\u2795';
RBRACE: '}';
RPAR:   ')' | '\u25B6';
SEMI:   ';' | '\u1F6AB';
SLASH:  '/';
STAR:   '*';

// Content-bearing token types
ID: LETTER (LETTER | DIGIT)*; //| '\u1F600'..'\u1F6AA' | '\u1F300'..'\u1F500';
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