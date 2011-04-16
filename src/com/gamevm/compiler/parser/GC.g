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
	METHOD_INVOCATION;
	FIELD_DECLARATION;
	PARAMETER_LIST;
	IF_CLAUSE;
	ELSE_IF_CLAUSE;
	ELSE_CLAUSE;
	BODY;
	ARRAY_ACCESS;
	NEW_ARRAY;
}

@header {
package com.gamevm.compiler.parser;
}

@lexer::header {
package com.gamevm.compiler.parser;
}

program:
	package_definition import_statement* class_definition EOF! 
	;
	
package_definition:
	'package'^ qualifiedName ';'!
  	;

import_statement:
	'import'^ qualifiedImportName ';'!
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
	'extends'^ classOrInterfaceType
	;
	
implements_clause:
	'implements'^ classOrInterfaceType (','! classOrInterfaceType)*
	;
	
class_member:
	field_declaration | method_definition | class_definition
	;
	
field_declaration:
	modifiers? type IDENT ('=' expression)? ';'
		-> ^(FIELD_DECLARATION modifiers? IDENT type expression?)
	;
	
method_definition:
	modifiers type? IDENT'(' parameter_list ')' 
	body 
		-> ^(METHOD_DEFINITION modifiers IDENT type? parameter_list body )
	;
	
modifiers:
	access_modifier 'static'? 'final'? 
	;
	
access_modifier:
	'private' | 'protected' | 'public'
	;

	
statement:
	  variable_init ';'!
	| expression ';'!
	| if_statement
	| for_loop
	| while_loop
	| return_statement ';'!
	;

variable_init:
	variable_decl ('=' expression)? -> ^(VARIABLE_DECLARATION variable_decl expression?)
	;
	
parameter_list:
	(variable_decl more_parameters)? -> ^(PARAMETER_LIST variable_decl? more_parameters?)
	;
	
more_parameters:
	(','! variable_decl)*
	;
	
assignment:
	qualified_access '='^ expression
	;
	
lvalue:
	IDENT^ | IDENT^ '['! expression ']'!
	;
	
method_invocation:
	IDENT '(' (expression (',' expression)*)? ')'
	-> ^(METHOD_INVOCATION IDENT expression*)
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
	relation (('&&'^ | '||'^) relation)*
	;
	

	
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
	qualified_access ('='^ expression)? | '('! expression ')'! | literal
	;
	
qualified_access:
	base_term_array ('.'^ base_term_array)*
	;

base_term_array:
	  base_term ('['^ expression ']'!)*
	| new_array_operator
	;

base_term:
	IDENT | method_invocation | new_operator
	;
	
new_operator:
	'new'^ type actual_parameter_list
	;
	
new_array_operator:
	'new' type array_dimensions
	-> ^(NEW_ARRAY type array_dimensions)
	;
	
actual_parameter_list:
	'('! (expression (','! expression)*)? ')'!
	;
	
array_dimensions:
	('['! expression ']'!)+
	;
	
literal:
	INTEGER_LITERAL | STRING_LITERAL | CHAR_LITERAL | BOOLEAN_LITERAL;
	
type:
      classOrInterfaceType('[' ']')*
    | primitiveType('[' ']')*
	;
	
classOrInterfaceType:
	IDENT ('.' IDENT)*
	;
	
primitiveType:
	  'byte'
	| 'short'
	| 'char'
	| 'int'
	| 'long'
	| 'float'
	| 'double'
	| 'boolean'
	;
	
qualifiedName:  
	IDENT ('.' IDENT)*
    ;
    
qualifiedImportName:
	IDENT ('.' IDENT)*
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
	
BOOLEAN_LITERAL:
	'true' | 'false'
	;


IDENT: CHAR (CHAR | DIGIT | '_')* ;
INTEGER_LITERAL: DIGIT+;
BUILTIN_VAR: '$' INTEGER_LITERAL;
WS: (' ' | '\t' | '\r' | '\n' | '\f')+ { $channel = HIDDEN; };

COMMENT: '//' (~('\n' | '\r'))* NL* { $channel = HIDDEN; };
MULTILINE_COMMENT: '/*' .* '*/' { $channel = HIDDEN; };
