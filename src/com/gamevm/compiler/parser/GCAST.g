grammar GCAST;

options {
  language = Java;
}


@header {
package com.gamevm.compiler.parser;

import java.util.Collection;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Modifier;
import com.gamevm.compiler.assembly.Variable;
import com.gamevm.compiler.assembly.code.DefaultTreeCode;
import com.gamevm.compiler.assembly.code.TreeCode;
}

@lexer::header {
package com.gamevm.compiler.parser;
}

@members {

	private String packageName;
	private String className;
	private List<Field> fields = new ArrayList<Field>();
	private List<Method> methods = new ArrayList<Method>();
	private List<TreeCode<ASTNode>> methodImplementations = new ArrayList<TreeCode<ASTNode>>();
	private ASTNode staticConstructor = new ASTNode(ASTNode.TYPE_BLOCK);
	private ASTNode implicitConstructor = new ASTNode(ASTNode.TYPE_BLOCK);
	
	private List<Type> imports = new ArrayList<Type>();
	
	private String parentClass;
	private List<String> interfaces = new ArrayList<String>();
	
	private ASTNode getVariableNode(int line, int position, String text) {
		return new ASTNode(ASTNode.TYPE_VARIABLE, line, position, text.length(), text);
	}
	
	private List<ParserError> errors = new ArrayList<ParserError>();
	
	@Override
	public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        errors.add(new ParserError(e, tokenNames));
    }
    
    public List<ParserError> getErrors() {
    	return errors;
    }
	
	private void checkNode(String rulename, ASTNode n) {
		assert(n != null);
		//System.out.println("Checking node in rule " + rulename);
	}

}

program returns [ClassDefinition<TreeCode<ASTNode>> classdef]:
	package_definition import_statement* class_definition EOF
	
	{
		classdef = $class_definition.value;
	}
;
	
package_definition:
	PACKAGE qualifiedName SEMICOLON
	
	{
		packageName = $qualifiedName.text;
		Type.setCurrentPackage(packageName);
	}
  ;

import_statement:
	IMPORT qualifiedImportName SEMICOLON

	{
		imports.add(Type.importType($qualifiedImportName.text));
	}	
;

class_definition returns [ClassDefinition<TreeCode<ASTNode>> value]:
	modifiers CLASS IDENT extension_clause
	
	{
		className = $IDENT.text;
		String fullName = packageName + "." + className;
		Type.importType(fullName);
	}
	
	BRACE_O
	class_member*
	BRACE_C
	
	{	
		ClassDeclaration header = new ClassDeclaration($modifiers.value, packageName + "." + className, fields.toArray(new Field[] {}), methods.toArray(new Method[] {}), imports.toArray(new Type[] {}));
		if (staticConstructor.getChildCount() == 0)
			staticConstructor = null;
		if (implicitConstructor.getChildCount() == 0)
			implicitConstructor = null;
		$value = new ClassDefinition<TreeCode<ASTNode>>(ClassDefinition.AST_HEADER, header, new DefaultTreeCode<ASTNode>(staticConstructor), new DefaultTreeCode<ASTNode>(implicitConstructor), methodImplementations);
	}
;
	
extension_clause:
	extends_clause? implements_clause?
	;
	
extends_clause:
	EXTENDS classOrInterfaceType
	;
	
implements_clause:
	IMPLEMENTS classOrInterfaceType (COMMA classOrInterfaceType)*
	;
	
class_member:
	field_declaration | method_definition | class_definition
	;
	
field_declaration:
	modifiers? type IDENT (OP_ASSIGN e=expression)? SEMICOLON
	
	{
		Field f = new Field($modifiers.value, (Type)$type.node.getValue(), $IDENT.text);
		fields.add(f);
		
		if ($e.node != null) {
			ASTNode fieldNode = getVariableNode($IDENT.line, $IDENT.pos, $IDENT.text);
			ASTNode assignment = new ASTNode(ASTNode.TYPE_ASSIGNMENT, fieldNode, $e.node);
			if (f.isStatic()) {
				staticConstructor.addNode(assignment);
			} else {
				implicitConstructor.addNode(assignment);
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
	PARENTHESES_O
	parameter_list 
	PARENTHESES_C 
	body 
	
	{
		Variable[] parameters = $parameter_list.result.toArray(new Variable[] {});
		String name = $IDENT.text;
		if (name.equals(className))
			name = "<init>";
	
		methods.add(new Method($modifiers.value, returnType, name, parameters));
		ASTNode code = $body.node;
		methodImplementations.add((code != null) ? new DefaultTreeCode<ASTNode>(code) : null);
	}

;
	
modifiers returns [int value]:
	{
		int accessModifier = Modifier.DEFAULT;
		boolean staticModifier = false;
		boolean finalModifier = false;
	}

	access_modifier 	{ accessModifier = $access_modifier.value; }
	(STATIC 			{ staticModifier = true; })? 
	(FINAL 			{ finalModifier = true; })? 
	
	{
		$value = Modifier.getFlag(accessModifier, staticModifier, finalModifier);
	}
;
	
access_modifier returns [int value]:
	  PRIVATE 	{ $value = Modifier.PRIVATE; }
	| PROTECTED 	{ $value = Modifier.PROTECTED; }
	| PUBLIC		{ $value = Modifier.PUBLIC; }
	;

	
statement returns [ASTNode node]:
	( s = variable_init end=SEMICOLON		{ $s.node.moveEndPositionTo($end.line, $end.pos + 1); }
	| s = expression end=SEMICOLON		{ $s.node.moveEndPositionTo($end.line, $end.pos + 1); }
	| s = if_statement
	| s = for_loop
	| s = while_loop
	| s = body
	| s = return_statement end=SEMICOLON 	{ $s.node.moveEndPositionTo($end.line, $end.pos + 1); }
	)
	
	{
		$node = $s.node;
		checkNode("statement", $node);
	}
;

variable_init returns [ASTNode node]:
	variable_decl (OP_ASSIGN expression)?
	
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

	(COMMA formal_parameter { $result.add($formal_parameter.value); } )*
	;
	
method_invocation returns [ASTNode node]:
	{
		$node = new ASTNode(ASTNode.TYPE_METHOD_INVOCATION);
	}

	IDENT
	PARENTHESES_O 
	(e1=expression 		{ $node.addNode($e1.node); }
	(COMMA e2=expression	{ $node.addNode($e2.node); } )*)? 
	end = PARENTHESES_C
	
	{
		ASTNode n = getVariableNode($IDENT.line, $IDENT.pos, $IDENT.text);
		$node.insertNode(0, n);
		$node.moveEndPositionTo($end.line, $end.pos+1);
	}
	;
	
if_statement returns [ASTNode node]:
	start=IF PARENTHESES_O expression PARENTHESES_C b=statement (ELSE e=statement)?
	
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

	start=FOR
	PARENTHESES_O 
	((i=variable_init | i=expression) { $node.addNode($i.node); })? SEMICOLON 
	(e=expression { $node.addNode($e.node); })? 
	(SEMICOLON p=expression { $node.addNode($p.node); })* PARENTHESES_C
	b=statement
	
	{
		$node.insertNode(2, $b.node);
		$node.moveStartPositionTo($start.line, $start.pos);
	}
	;
	
while_loop returns [ASTNode node]:
	start=WHILE PARENTHESES_O expression PARENTHESES_C statement

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

	start=BRACE_O
	(statement { $node.addNode($statement.node); })*
	end=BRACE_C
	
	{
		$node.moveStartPositionTo($start.line, $start.pos);
		$node.moveEndPositionTo($end.line, $end.pos+1);
	}
;
	
return_statement returns [ASTNode node]:
	start=RETURN expression

	{
		$node = new ASTNode(ASTNode.TYPE_RETURN, $expression.node);
		$node.moveStartPositionTo($start.line, $start.pos);
		checkNode("return_statement", $node);
	}
;
	
expression returns [ASTNode node]:
	{
		int type = -1;
	}

	op1=relation { $node = $op1.node; }
	((
	  OP_LOGICAL_AND	{ type = ASTNode.TYPE_OP_LAND; } 
	| OP_LOGICAL_OR  	{ type = ASTNode.TYPE_OP_LOR; }
	) 
	op2=relation 	{ $node = new ASTNode(type, $node, $op2.node); }
	)*
	
	{
		checkNode("expression", $node);
	}
;
	

	
relation returns [ASTNode node]:
	{
		int type = -1;
	}

	op1=add			{ $node = $op1.node; }
	((
	    OP_GREATER		{ type = ASTNode.TYPE_OP_GTH; } 	
	  | OP_LESSER		{ type = ASTNode.TYPE_OP_LTH; } 
	  | OP_GREATER_EQU	{ type = ASTNode.TYPE_OP_GEQ; } 
	  | OP_LESSER_EQU 	{ type = ASTNode.TYPE_OP_LEQ; } 
	  | OP_EQUALS 		{ type = ASTNode.TYPE_OP_EQU; } 
	  | OP_NOT_EQUALS	{ type = ASTNode.TYPE_OP_NEQ; } 
	) 
	op2=add 		{ $node = new ASTNode(type, $node, $op2.node); }
	)*
	
	{
		checkNode("relation", $node);
	}
;

add returns [ASTNode node]:
	{
		int type = -1;
	}

	op1=mult	{ $node = $op1.node; }		 
	((
	  OP_ADD		{ type = ASTNode.TYPE_OP_PLUS; } 
	| OP_SUBTRACT	{ type = ASTNode.TYPE_OP_MINUS; }
	) 
	op2=mult 		{ $node = new ASTNode(type, $node, $op2.node); }
	)*
	
	{
		checkNode("add", $node);
	}
;

mult returns [ASTNode node]:
	{
		int type = -1;
	}

	op1=unary 	 { $node = $op1.node; }
	((
	  OP_MULTIPLY	{ type = ASTNode.TYPE_OP_MULT; } 
	| OP_DIVIDE		{ type = ASTNode.TYPE_OP_DIV; }
	| OP_MODULO		{ type = ASTNode.TYPE_OP_MOD; }
	) 
	op2=unary 		{ $node = new ASTNode(type, $node, $op2.node); }
	)*
	
	{
		checkNode("mult", $node);
	}
;
	
unary returns [ASTNode node]:
	{
		boolean isNegated = false;
		int startLine = -1;
		int startPos = -1;
	}
	
	(OP_ADD | minus 
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
		checkNode("unary", $node);
	}
;
	
negation returns [ASTNode node]:
	{
		boolean isNegated = false;
		int startLine = -1;
		int startPos = -1;
	}
	
	(neg=OP_LOGICAL_NEGATION
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
		checkNode("negation", $node);
	}
;
	
minus returns [int line, int pos]:
	neg=OP_SUBTRACT
	
	{
		$line = $neg.line;
		$pos = $neg.pos;
	}
;
	
term returns [ASTNode node]:
	 ( qualified_access (OP_ASSIGN e=expression)? 
	  	{
	  		if ($e.node != null) {
	  			$node = new ASTNode(ASTNode.TYPE_ASSIGNMENT, $qualified_access.node, $e.node);
	  		} else {
	  			$node = $qualified_access.node;
	  		}
	  	}
	| start=PARENTHESES_O e2=expression PARENTHESES_C 
		{
			$node = $e2.node;
			$node.moveStartPositionTo($start.line, $start.pos);
		}
	| literal
		{
			$node = $literal.node;
		}
	)
	
	{
		checkNode("term", $node);
	}
;

qualified_access returns [ASTNode node]:
	op1=base_term_array 	{ $node = $op1.node; }
	(DOT
	op2=base_term_array 	{ $node = new ASTNode(ASTNode.TYPE_QUALIFIED_ACCESS, $node, $op2.node); }
	)*
	
	{
		checkNode("qualified_access", $node);
	}	
;

base_term_array returns [ASTNode node]:
	( base_term 			{ $node = $base_term.node; }
	  (
	  	BRACKETS_O expression BRACKETS_C	{ $node = new ASTNode(ASTNode.TYPE_ARRAY_ACCESS, $node, $expression.node); }
	  )*
	| new_array_operator	{ $node = $new_array_operator.node; }
	)
	
	{
		checkNode("base_term_array", $node);
	}
;

base_term returns [ASTNode node]:
	( IDENT 				{ $node = getVariableNode($IDENT.line, $IDENT.pos, $IDENT.text); }
	| method_invocation 	{ $node = $method_invocation.node; }
	| new_operator			{ $node = $new_operator.node; }
	)
	
	{
		checkNode("base_term", $node);
	}
;
	
new_operator returns [ASTNode node]:
	start=NEW type actual_parameter_list

	{
		$node = new ASTNode(ASTNode.TYPE_OP_NEW, $type.node);
		for (ASTNode n : $actual_parameter_list.nodes) {
			$node.addNode(n);
		}
		$node.moveStartPositionTo($start.line, $start.pos);
		$node.moveEndPositionTo($actual_parameter_list.endLine, $actual_parameter_list.endPos);
		checkNode("new_operator", $node);
	}
;
	
new_array_operator returns [ASTNode node]:
	start=NEW type array_dimensions
	
	{
		$node = new ASTNode(ASTNode.TYPE_OP_NEW_ARRAY, $type.node);
		for (ASTNode n : $array_dimensions.nodes) {
			$node.addNode(n);
		}
		$node.moveStartPositionTo($start.line, $start.pos);
		$node.moveEndPositionTo($array_dimensions.endLine, $array_dimensions.endPos+1);
		checkNode("new_array_operator", $node);
	}
;
	
actual_parameter_list returns [Collection<ASTNode> nodes, int endLine, int endPos]:
	{
		$nodes = new ArrayList<ASTNode>();
	}
	
	PARENTHESES_O (a=expression 	{ $nodes.add($a.node); }
	(COMMA b=expression	{ $nodes.add($b.node); }
	)*)? 
	end=PARENTHESES_C

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
	BRACKETS_O expression end=BRACKETS_C	{ $nodes.add($expression.node); }
	)+

	{
		$endLine = $end.line;
		$endPos = $end.pos;
	}
;
	
literal returns [ASTNode node]:
	( l=INTEGER_LITERAL
		{
			$node = new ASTNode(ASTNode.TYPE_LITERAL, $l.line, $l.pos, $l.text.length(), new LiteralObject(Integer.parseInt($l.text), Type.INT));
		}
	| l=STRING_LITERAL
		{
			$node = new ASTNode(ASTNode.TYPE_LITERAL, $l.line, $l.pos, $l.text.length()+2, new LiteralObject($l.text, Type.STRING));
		}
	| l=CHAR_LITERAL 
		{
			$node = new ASTNode(ASTNode.TYPE_LITERAL, $l.line, $l.pos, $l.text.length(), new LiteralObject($l.text.charAt(0), Type.CHAR));
		}
	| l=BOOLEAN_LITERAL
		{
			$node = new ASTNode(ASTNode.TYPE_LITERAL, $l.line, $l.pos, $l.text.length(), new LiteralObject(Boolean.valueOf($l.text), Type.BOOLEAN));
		}
	)
	
	{
		checkNode("literal", $node);
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
    BRACKETS_O end=BRACKETS_C	
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
		checkNode("type", $node);
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
	DOT i2=IDENT	
		{ 
			text.append('.');
			text.append($i2.text); 
		}
	)*
	
	{
		$node = new ASTNode(ASTNode.TYPE_TYPE, $i1.line, $i1.pos, text.length(), Type.getType(text.toString()));
		checkNode("classOrInterfaceType", $node);
	}
;
	
primitiveType returns [ASTNode node]:
	{
		Type type = null;
		int line;
		int pos;
		int textLength;
	}

	( t=BYTE	{ type = Type.BYTE; }
	| t=SHORT	{ type = Type.SHORT; }
	| t=CHAR	{ type = Type.CHAR;  }
	| t=INT		{ type = Type.INT; }
	| t=LONG	{ type = Type.LONG; }
	| t=FLOAT	{ type = Type.FLOAT; }
	| t=DOUBLE	{ type = Type.DOUBLE; }
	| t=BOOLEAN	{ type = Type.BOOLEAN; }
	| t=VOID	{ type = Type.VOID; }
	)
	{
		
		$node = new ASTNode(ASTNode.TYPE_TYPE, $t.line, $t.pos, $t.text.length(), type);
		checkNode("primitiveType", $node);
	}
	
;
	
qualifiedName returns [String text]:  
	{
		StringBuilder b = new StringBuilder();
	}

	i1=IDENT 		{ b.append($i1.text); }
	(
	DOT i2=IDENT	{ b.append("." + $i2.text); }
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
	DOT i2=IDENT	{ b.append("." + $i2.text); }
	)*
	
	{
		$text = b.toString();
	}
;
    	
// -----------------------------------------------------------------------------

fragment CHARACTER: 'a'..'z' | 'A'..'Z';
fragment DIGIT: '0'..'9';
fragment NL : ('\n' | '\r');

PACKAGE: 'package' ;
IMPORT: 'import' ;
CLASS: 'class' ;
EXTENDS: 'extends' ;
IMPLEMENTS: 'implements' ;
STATIC: 'static' ;
FINAL: 'final' ;
PUBLIC: 'public' ;
PROTECTED: 'protected' ;
PRIVATE: 'private' ;
IF: 'if' ;
ELSE: 'else' ;
FOR: 'for' ;
WHILE: 'while' ;
RETURN: 'return' ;
NEW: 'new' ;
BYTE: 'byte' ;
SHORT: 'short' ;
INT: 'int' ;
LONG: 'long' ;
FLOAT: 'float' ;
DOUBLE: 'double' ;
CHAR: 'char' ;
BOOLEAN: 'boolean' ;
VOID: 'void' ;
BRACE_O: '{' ;
BRACE_C: '}' ;
PARENTHESES_O: '(' ;
PARENTHESES_C: ')' ;
BRACKETS_O: '[' ;
BRACKETS_C: ']' ;
SEMICOLON: ';' ;
COMMA: ',' ;
DOT: '.' ;
OP_ASSIGN: '=' ;
OP_EQUALS: '==' ;
OP_NOT_EQUALS: '!=' ;
OP_GREATER: '>' ;
OP_LESSER: '<' ;
OP_GREATER_EQU: '>=' ;
OP_LESSER_EQU: '<=' ;
OP_LOGICAL_AND: '&&' ;
OP_LOGICAL_OR: '||' ;
OP_ADD: '+' ;
OP_SUBTRACT: '-' ;
OP_MULTIPLY: '*' ;
OP_DIVIDE: '/' ;
OP_MODULO: '%' ;
OP_LOGICAL_NEGATION: '!' ;

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

INTEGER_LITERAL: DIGIT+;

IDENT: CHARACTER (CHARACTER | DIGIT | '_')* ;

WS: (' ' | '\t' | '\r' | '\n' | '\f')+ { $channel = HIDDEN; };

COMMENT: '//' (~('\n' | '\r'))* NL* { $channel = HIDDEN; };
MULTILINE_COMMENT: '/*' .* '*/' { $channel = HIDDEN; };
