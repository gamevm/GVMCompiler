grammar GC;

options {
  language = Java;
  output = AST;
  ASTLabelType = CommonTree;
}

tokens {
	INT_NEGATION;
	CONST_DECLARATION;
	VARIABLE_DECLARATION;
	FUNCTION_DECLARATION;
	METHOD_DEFINITION;
	FIELD_DECLARATION;
	PARAMETER_LIST;
	IF_CLAUSE;
	ELSE_IF_CLAUSE;
	ELSE_CLAUSE;
	BODY;
}

@header {
package com.gamevm.compiler.parser;
}

@lexer::header {
package com.gamevm.compiler.parser;
}

program:
	package_definition import_statement* class_definition EOF! ;
	
package_definition:
	'package'^ name ';'!
  	;

import_statement:
	'import'^ name ';'!
	;

class_definition:
	modifiers 'class'^ IDENT extension_clause
	'{'!
	class_member*
	'}'!
	;
	
extension_clause:
	extends_clause? implements_clause?
	;
	
extends_clause:
	'extends'^ name
	;
	
implements_clause:
	'implements'^ name (','! name)*
	;
	
class_member:
	field_declaration | method_definition | class_definition
	;
	
field_declaration:
	modifiers type IDENT ('=' expression)? ';'
		-> ^(FIELD_DECLARATION IDENT type expression?)
	;
	
method_definition:
	modifiers type? IDENT'(' parameter_list ')' 
	body 
		-> ^(METHOD_DEFINITION modifiers IDENT type? parameter_list body )
	;
	
modifiers:
	access_modifier? 'static'? 'final'? 
	;
	
access_modifier:
	'private' | 'protected' | 'public'
	;
	
name:
	NAME | IDENT;
	
statement:
	  variable_init ';'!
	| const_decl ';'!
	| function_decl
	| assignment ';'!
	| function_invocation ';'!
	| if_statement
	| for_loop
	| while_loop
	| return_statement ';'!
	| print_statement
	;
	
print_statement:
	'print'^ '('! expression ')'! ';'!
	;

const_decl:
	'const' variable_decl '=' expression -> ^(CONST_DECLARATION variable_decl expression)
	;

variable_init:
	variable_decl ('=' expression)? -> ^(VARIABLE_DECLARATION variable_decl expression?)
	;
	
function_decl:
	type IDENT '(' parameter_list ')' 
	body -> ^(FUNCTION_DECLARATION IDENT type parameter_list body )
	;
	
parameter_list:
	(variable_decl more_parameters)? -> ^(PARAMETER_LIST variable_decl? more_parameters?)
	;
	
more_parameters:
	(','! variable_decl)*
	;
	
assignment:
	IDENT '='^ expression
	;
	
function_invocation:
	IDENT^ '('! (expression (','! expression)*)? ')'!
	;
	
if_statement:
	'if'^ '('! expression ')'! body else_if_statement* else_statement?
	;
	
else_if_statement:
	'else' 'if' '(' expression ')' body -> ^(ELSE_IF_CLAUSE expression body)
	;
	
else_statement:
	'else'^ body
	;
	
for_loop:
	'for'^ '('! (variable_init | assignment)? ';'! expression? ';'! statement* ')'! body
	;
	
while_loop:
	'while'^ '('! expression ')'! body
	;
	
variable_decl:
	type IDENT
	;
	
body:
	'{'
	statement*
	'}'
	-> ^(BODY statement*)
	;
	
return_statement:
	'return'^ expression
	;
	
expression:
	relation (('&&'^ | '||'^) relation)*;
	
relation:
	add (('>'^ | '<'^ | '>='^ | '<='^ | '=='^ | '!='^) add)*;

add:
	mult (('+'^ | '-'^) mult)*;

mult:
	unary (('*'^ | '/'^ | '%'^) unary)*;
	
unary:
	('+' | minus)* negation;
	
negation:
	('!'^)* term;
	
minus:
	'-' -> INT_NEGATION;
	
term:
	BUILTIN_VAR | IDENT | '('! expression ')'! | literal | function_invocation | 'readInt' '('! ')'! | 'readLine' '('! ')'!;
	
literal:
	INTEGER_LITERAL | STRING_LITERAL | CHAR_LITERAL ;
	
type:
	  'byte'
	| 'short'
	| 'char'
	| 'int'
	| 'long'
	| 'float'
	| 'double'
	| 'boolean'
	| 'void'
	| IDENT
	;
	
// -----------------------------------------------------------------------------

fragment CHAR: 'a'..'z' | 'A'..'Z';
fragment DIGIT: '0'..'9';
fragment NL : ('\n' | '\r');

STRING_LITERAL: 
	'"' 
	{ StringBuilder b = new StringBuilder(); }
	(
		c = ~('"' | NL) { b.appendCodePoint(c); } 
		| '\\' '"' { b.appendCodePoint('"'); } 
	)*
	'"' 
	{ setText(b.toString()); };
	
CHAR_LITERAL:
	'\'' . '\''
	{ setText(getText().substring(1,2)); };


IDENT: CHAR (CHAR | DIGIT | '_')* ;
NAME: IDENT ('.' IDENT)*;
INTEGER_LITERAL: DIGIT+;
BUILTIN_VAR: '$' INTEGER_LITERAL;
WS: (' ' | '\t' | '\r' | '\n' | '\f')+ { $channel = HIDDEN; };

COMMENT: '//' (~('\n' | '\r'))* NL* { $channel = HIDDEN; };
MULTILINE_COMMENT: '/*' .* '*/' { $channel = HIDDEN; };
