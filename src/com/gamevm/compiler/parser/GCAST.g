grammar GCAST;

options {
  language = Java;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Modifier;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.compiler.assembly.Variable;
import com.gamevm.compiler.translator.Code;
}

@lexer::header {
package com.gamevm.compiler.parser;
}

@members {

	private String packageName;
	private String className;
	private List<Field> fields = new ArrayList<Field>();
	private List<Method> methods = new ArrayList<Method>();
	private List<Code<ASTNode>> methodImplementations = new ArrayList<Code<ASTNode>>();
	private List<ASTNode> staticConstructor = new ArrayList<ASTNode>();
	private List<ASTNode> implicitConstructor = new ArrayList<ASTNode>();
	
	private List<Type> imports = new ArrayList<Type>();
	
	private String parentClass;
	private List<String> interfaces = new ArrayList<String>();
	
	private ASTNode getVariableNode(int line, int position, String text) {
		return new ASTNode(ASTNode.TYPE_VARIABLE, line, position, text.length(), text);
	}

}

program returns [ClassDefinition<ASTNode> classdef]:
	package_definition import_statement* class_definition EOF
	
	{
		classdef = $class_definition.value;
	}
;
	
package_definition:
	'package' qualifiedName ';'
	
	{
		packageName = $qualifiedName.text;
		Type.setCurrentPackage(packageName);
	}
  ;

import_statement:
	'import' qualifiedImportName ';'

	{
		imports.add(Type.importType($qualifiedImportName.text));
	}	
;

class_definition returns [ClassDefinition<ASTNode> value]:
	modifiers 'class' IDENT extension_clause
	
	{
		className = $IDENT.text;
		String fullName = packageName + "." + className;
		Type.importType(fullName);
	}
	
	'{'
	class_member*
	'}'
	
	{
		
		ClassDeclaration header = new ClassDeclaration($modifiers.value, packageName + "." + className, fields.toArray(new Field[] {}), methods.toArray(new Method[] {}), imports.toArray(new Type[] {}));
		$value = new ClassDefinition<ASTNode>(header, Code.getASTCode(staticConstructor), Code.getASTCode(implicitConstructor), methodImplementations);
	}
;
	
extension_clause:
	extends_clause? implements_clause?
	;
	
extends_clause:
	'extends' classOrInterfaceType
	;
	
implements_clause:
	'implements' classOrInterfaceType (',' classOrInterfaceType)*
	;
	
class_member:
	field_declaration | method_definition | class_definition
	;
	
field_declaration:
	modifiers? type IDENT ('=' e=expression)? ';'
	
	{
		Field f = new Field($modifiers.value, (Type)$type.node.getValue(), $IDENT.text);
		fields.add(f);
		
		if ($e.node != null) {
			ASTNode fieldNode = getVariableNode($IDENT.line, $IDENT.pos, $IDENT.text);
			ASTNode assignment = new ASTNode(ASTNode.TYPE_ASSIGNMENT, fieldNode, $e.node);
			if (f.isStatic()) {
				staticConstructor.add(assignment);
			} else {
				implicitConstructor.add(assignment);
			}
		}
	}

;
	
method_definition:
	{ 
		Type returnType = Type.VOID;
	}

	modifiers 
	(type 			{ returnType = (Type)$type.node.getValue(); } )? 
	IDENT
	'(' 
	parameter_list 
	')' 
	body 
	
	{
		Variable[] parameters = $parameter_list.result.toArray(new Variable[] {});
		String name = $IDENT.text;
		if (name.equals(className))
			name = "<init>";
	
		methods.add(new Method($modifiers.value, returnType, name, parameters));
		ASTNode code = $body.node;
		methodImplementations.add((code != null) ? Code.getASTCode(code) : null);
	}

;
	
modifiers returns [int value]:
	{
		int accessModifier = Modifier.DEFAULT;
		boolean staticModifier = false;
		boolean finalModifier = false;
	}

	access_modifier 	{ accessModifier = $access_modifier.value; }
	('static' 			{ staticModifier = true; })? 
	('final' 			{ finalModifier = true; })? 
	
	{
		$value = Modifier.getFlag(accessModifier, staticModifier, finalModifier);
	}
;
	
access_modifier returns [int value]:
	  'private' 	{ $value = Modifier.PRIVATE; }
	| 'protected' 	{ $value = Modifier.PROTECTED; }
	| 'public'		{ $value = Modifier.PUBLIC; }
	;

	
statement returns [ASTNode node]:
	( s = variable_init end=';'		{ $s.node.moveEndPositionTo($end.line, $end.pos + 1); }
	| s = expression end=';'		{ $s.node.moveEndPositionTo($end.line, $end.pos + 1); }
	| s = if_statement
	| s = for_loop
	| s = while_loop
	| s = body
	| s = return_statement end=';' 	{ $s.node.moveEndPositionTo($end.line, $end.pos + 1); }
	)
	
	{
		$node = $s.node;
		assert($node != null);
	}
;

variable_init returns [ASTNode node]:
	variable_decl ('=' expression)?
	
	{
		$node = $variable_decl.node;
		if ($expression.node != null)
			$node.addNode($expression.node);
	}
	;
	
parameter_list returns [List<Variable> result]:
	(formal_parameter more_parameters)?
	
	{
	
		if ($more_parameters.result != null) {
			$result = $more_parameters.result;
			if ($formal_parameter.value != null)
				$result.add(0, $formal_parameter.value);
		} else {
			$result = new ArrayList<Variable>(); 
		}
	}
;
	
more_parameters returns [List<Variable> result]:
	{ 
		$result = new ArrayList<Variable>(); 
	}

	(',' formal_parameter { $result.add($formal_parameter.value); } )*
	;
	
method_invocation returns [ASTNode node]:
	{
		$node = new ASTNode(ASTNode.TYPE_METHOD_INVOCATION);
	}

	IDENT
	'(' 
	(e1=expression 		{ $node.addNode($e1.node); }
	(',' e2=expression	{ $node.addNode($e2.node); } )*)? 
	end = ')'
	
	{
		ASTNode n = getVariableNode($IDENT.line, $IDENT.pos, $IDENT.text);
		$node.insertNode(0, n);
		$node.moveEndPositionTo($end.line, $end.pos+1);
	}
	;
	
if_statement returns [ASTNode node]:
	start='if' '(' expression ')' b=statement ('else' e=statement)?
	
	{
		$node = new ASTNode(ASTNode.TYPE_IF, $expression.node, $b.node);
		if ($e.node != null)
			$node.addNode($e.node);
		$node.moveStartPositionTo($start.line, $start.pos);
	}
;
	
for_loop returns [ASTNode node]:
	{
		$node = new ASTNode(ASTNode.TYPE_FOR_LOOP);
	}

	start='for'
	'(' 
	(i=statement { $node.addNode($i.node); })? ';' 
	(expression { $node.addNode($expression.node); })? ';' 
	(p=statement { $node.addNode($p.node); })* ')'
	b=statement
	
	{
		$node.insertNode(2, $b.node);
		$node.moveStartPositionTo($start.line, $start.pos);
	}
	;
	
while_loop returns [ASTNode node]:
	start='while' '(' expression ')' statement

	{
		$node = new ASTNode(ASTNode.TYPE_WHILE_LOOP, $expression.node, $statement.node);
		$node.moveStartPositionTo($start.line, $start.pos);
	}	
;
	
formal_parameter returns [Variable value]:
	type IDENT	
	
	{
		$value = new Variable((Type)$type.node.getValue(), $IDENT.text);
	}
;
	
	
variable_decl returns [ASTNode node]:
	type IDENT
	
	{
		return new ASTNode(ASTNode.TYPE_VAR_DECL, $type.node, getVariableNode($IDENT.line, $IDENT.pos, $IDENT.text));
	}
	;
	
body returns [ASTNode node]:
	{
		$node = new ASTNode(ASTNode.TYPE_BLOCK);
	}

	start='{'
	(statement { $node.addNode($statement.node); })*
	end='}'
	
	{
		$node.moveStartPositionTo($start.line, $start.pos);
		$node.moveEndPositionTo($end.line, $end.pos+1);
	}
;
	
return_statement returns [ASTNode node]:
	start='return' expression

	{
		$node = new ASTNode(ASTNode.TYPE_RETURN, $expression.node);
		$node.moveStartPositionTo($start.line, $start.pos);
		assert(node != null);
	}
;
	
expression returns [ASTNode node]:
	{
		int type = -1;
	}

	op1=relation { $node = $op1.node; }
	((
	  '&&'	{ type = ASTNode.TYPE_OP_LAND; } 
	| '||'	{ type = ASTNode.TYPE_OP_LOR; }
	) 
	op2=relation 	{ $node = new ASTNode(type, $node, $op2.node); }
	)*
	
	{
		assert(node != null);
	}
;
	

	
relation returns [ASTNode node]:
	{
		int type = -1;
	}

	op1=add 	 { $node = $op1.node; }
	((
	    '>'		{ type = ASTNode.TYPE_OP_GTH; } 	
	  | '<' 	{ type = ASTNode.TYPE_OP_LTH; } 
	  | '>=' 	{ type = ASTNode.TYPE_OP_GEQ; } 
	  | '<=' 	{ type = ASTNode.TYPE_OP_LEQ; } 
	  | '==' 	{ type = ASTNode.TYPE_OP_EQU; } 
	  | '!='	{ type = ASTNode.TYPE_OP_NEQ; } 
	) 
	op2=add 		{ $node = new ASTNode(type, $node, $op2.node); }
	)*
	
	{
		assert(node != null);
	}
;

add returns [ASTNode node]:
	{
		int type = -1;
	}

	op1=mult	{ $node = $op1.node; }		 
	((
	  '+'		{ type = ASTNode.TYPE_OP_PLUS; } 
	| '-'		{ type = ASTNode.TYPE_OP_MINUS; }
	) 
	op2=mult 		{ $node = new ASTNode(type, $node, $op2.node); }
	)*
	
	{
		assert(node != null);
	}
;

mult returns [ASTNode node]:
	{
		int type = -1;
	}

	op1=unary 	 { $node = $op1.node; }
	((
	  '*'		{ type = ASTNode.TYPE_OP_MULT; } 
	| '/'		{ type = ASTNode.TYPE_OP_DIV; }
	| '%'		{ type = ASTNode.TYPE_OP_MOD; }
	) 
	op2=unary 		{ $node = new ASTNode(type, $node, $op2.node); }
	)*
	
	{
		assert(node != null);
	}
;
	
unary returns [ASTNode node]:
	{
		boolean isNegated = false;
		int startLine = -1;
		int startPos = -1;
	}
	
	('+' | minus 
	{ 
		if (startLine < 0) {
			startLine = $minus.line;
			startPos = $minus.pos;
		} 
		isNegated =  !isNegated; 
	}
	)* 
	negation
	
	{
		if (isNegated) {
			$node = new ASTNode(ASTNode.TYPE_OP_NEG, $negation.node);
			$node.moveStartPositionTo(startLine, startPos);
		} else {
			$node = $negation.node;
		}
		assert(node != null);
	}
;
	
negation returns [ASTNode node]:
	{
		boolean isNegated = false;
		int startLine = -1;
		int startPos = -1;
	}
	
	(neg='!' 
	{ 
		if (startLine < 0) {
			startLine = $neg.line;
			startPos = $neg.pos;
		} 
		isNegated =  !isNegated; 
	}
	)* 
	term
	
	{
		if (isNegated) {
			$node = new ASTNode(ASTNode.TYPE_OP_LNEG, $negation.node);
			$node.moveStartPositionTo(startLine, startPos);
		} else {
			$node = $term.node;
		}
		assert(node != null);
	}
;
	
minus returns [int line, int pos]:
	neg='-'
	
	{
		$line = $neg.line;
		$pos = $neg.pos;
	}
;
	
term returns [ASTNode node]:
	 ( qualified_access ('=' e=expression)? 
	  	{
	  		if ($e.node != null) {
	  			$node = new ASTNode(ASTNode.TYPE_ASSIGNMENT, $qualified_access.node, $e.node);
	  		} else {
	  			$node = $qualified_access.node;
	  		}
	  	}
	| start='(' expression ')' 
		{
			$node = $expression.node;
			$node.moveStartPositionTo($start.line, $start.pos);
		}
	| literal
		{
			$node = $literal.node;
		}
	)
	
	{
		assert($node != null);
	}
;

qualified_access returns [ASTNode node]:
	op1=base_term_array 	{ $node = $op1.node; }
	('.'
	op2=base_term_array 	{ $node = new ASTNode(ASTNode.TYPE_QUALIFIED_ACCESS, $node, $op2.node); }
	)*
	
	{
		assert($node != null);
	}	
;

base_term_array returns [ASTNode node]:
	( base_term 			{ $node = $base_term.node; }
	  (
	  	'[' expression ']'	{ $node = new ASTNode(ASTNode.TYPE_ARRAY_ACCESS, $node, $expression.node); }
	  )*
	| new_array_operator	{ $node = $new_array_operator.node; }
	)
	
	{
		assert($node != null);
	}
;

base_term returns [ASTNode node]:
	( IDENT 				{ $node = getVariableNode($IDENT.line, $IDENT.pos, $IDENT.text); }
	| method_invocation 	{ $node = $method_invocation.node; }
	| new_operator			{ $node = $new_operator.node; }
	)
	
	{
		assert($node != null);
	}
;
	
new_operator returns [ASTNode node]:
	start='new' type actual_parameter_list

	{
		$node = new ASTNode(ASTNode.TYPE_OP_NEW, $type.node);
		for (ASTNode n : $actual_parameter_list.nodes) {
			$node.addNode(n);
		}
		$node.moveStartPositionTo($start.line, $start.pos);
		$node.moveEndPositionTo($actual_parameter_list.endLine, $actual_parameter_list.endPos);
		assert(node != null);
	}
;
	
new_array_operator returns [ASTNode node]:
	start='new' type array_dimensions
	
	{
		$node = new ASTNode(ASTNode.TYPE_OP_NEW_ARRAY, $type.node);
		for (ASTNode n : $array_dimensions.nodes) {
			$node.addNode(n);
		}
		$node.moveStartPositionTo($start.line, $start.pos);
		$node.moveEndPositionTo($array_dimensions.endLine, $array_dimensions.endPos+1);
		assert(node != null);
	}
;
	
actual_parameter_list returns [Collection<ASTNode> nodes, int endLine, int endPos]:
	{
		$nodes = new ArrayList<ASTNode>();
	}
	
	'(' (a=expression 	{ $nodes.add($a.node); }
	(',' b=expression	{ $nodes.add($b.node); }
	)*)? 
	end=')'

	{
		$endLine = $end.line;
		$endPos = $end.pos;
	}
;
	
array_dimensions returns [Collection<ASTNode> nodes, int endLine, int endPos]:
	{
		$nodes = new ArrayList<ASTNode>();
	}

	(
	'[' expression end=']'	{ $nodes.add($expression.node); }
	)+

	{
		$endLine = $end.line;
		$endPos = $end.pos;
	}
;
	
literal returns [ASTNode node]:
	( l=INTEGER_LITERAL
		{
			$node = new ASTNode(ASTNode.TYPE_LITERAL, $l.line, $l.pos, $l.text.length(), Integer.parseInt($l.text));
			$node.setValueType(Type.INT);
		}
	| l=STRING_LITERAL
		{
			$node = new ASTNode(ASTNode.TYPE_LITERAL, $l.line, $l.pos, $l.text.length(), $l.text);
			$node.setValueType(Type.getType("gc.String"));
		}
	| l=CHAR_LITERAL 
		{
			$node = new ASTNode(ASTNode.TYPE_LITERAL, $l.line, $l.pos, $l.text.length(), $l.text.charAt(0));
			$node.setValueType(Type.CHAR);
		}
	| l=BOOLEAN_LITERAL
		{
			$node = new ASTNode(ASTNode.TYPE_LITERAL, $l.line, $l.pos, $l.text.length(), Boolean.valueOf($l.text));
			$node.setValueType(Type.BOOLEAN);
		}
	)
	
	{
		assert(node != null);
	}
;
	
type returns [ASTNode node]:
	{
		int dimension = 0;
		int endLine = -1;
		int endPosition = -1;
	}
	
	(t=classOrInterfaceType | t=primitiveType)
  	(
    '[' end=']'	
    	{ 
    		dimension++;  
    		endLine = $end.line;
    		endPosition = $end.pos;
    	}
	)*
	
	{
		$node = $t.node;
		if (dimension > 0) {
			$node.setValue(Type.getArrayType((Type)$node.getValue(), dimension));
		}
		if (endLine >= 0)
			$node.moveEndPositionTo(endLine, endPosition+1);
		assert(node != null);
	}
;
	
classOrInterfaceType returns [ASTNode node]:
	{
		StringBuilder text = new StringBuilder();
	}

	i1=IDENT 
		{ 
			text.append($i1.text);
		}
	(
	'.' i2=IDENT	
		{ 
			text.append('.');
			text.append($i2.text); 
		}
	)*
	
	{
		$node = new ASTNode(ASTNode.TYPE_TYPE, $i1.line, $i1.pos, text.length(), Type.getType(text.toString()));
		assert(node != null);
	}
;
	
primitiveType returns [ASTNode node]:
	{
		Type type = null;
		int line;
		int pos;
		int textLength;
	}

	( t='byte'		{ type = Type.BYTE; }
	| t='short'		{ type = Type.SHORT; }
	| t='char'		{ type = Type.CHAR;  }
	| t='int'		{ type = Type.INT; }
	| t='long'		{ type = Type.LONG; }
	| t='float'		{ type = Type.FLOAT; }
	| t='double'	{ type = Type.DOUBLE; }
	| t='boolean'	{ type = Type.BOOLEAN; }
	| t='void'		{ type = Type.VOID; }
	)
	{
		
		$node = new ASTNode(ASTNode.TYPE_TYPE, $t.line, $t.pos, $t.text.length(), type);
		assert(node != null);
	}
	
;
	
qualifiedName returns [String text]:  
	{
		StringBuilder b = new StringBuilder();
	}

	i1=IDENT 		{ b.append($i1.text); }
	(
	'.' i2=IDENT	{ b.append("." + $i2.text); }
	)*
	
	{
		$text = b.toString();
	}
;
    
qualifiedImportName returns [String text]:
	{
		StringBuilder b = new StringBuilder();
	}

	i1=IDENT 		{ b.append($i1.text); }
	(
	'.' i2=IDENT	{ b.append("." + $i2.text); }
	)*
	
	{
		$text = b.toString();
	}
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
