tree grammar ClassDeclarationParser;

options {
  language = Java;
  tokenVocab = GC;
  ASTLabelType = CommonTree;
}

@header {
package com.gamevm.compiler.parser;

import java.util.Map;
import java.util.HashMap;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Code;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Type;
import com.gamevm.compiler.assembly.Variable;
}

@members {

	private String packageName;
	private List<Field> fields = new ArrayList<Field>();
	private List<Method> methods = new ArrayList<Method>();
	private List<Code<ASTNode>> methodImplementations = new ArrayList<Code<ASTNode>>();
	private List<Code<ASTNode>> fieldInitializers = new ArrayList<Code<ASTNode>>();
	
	private List<String> imports = new ArrayList<String>();
	
	private String parentClass;
	private List<String> interfaces = new ArrayList<String>();

}

program returns [ClassDefinition<ASTNode> result]:
	package_definition import_statement* class_definition
	
	{
		$result = $class_definition.value;
	}
;

package_definition:
	^('package' name)
	//-------------------------------------------------------
	{ 
		packageName = $name.text;
	}
;

import_statement:
	^('import' name)
	//-------------------------------------------------------
	{ 
		imports.add($name.text);
	}
;

class_definition returns [ClassDefinition<ASTNode> value]:
	^('class' modifiers IDENT extension_clause class_member*)
	
	{
		ClassDeclaration header = new ClassDeclaration(packageName + "." + $IDENT.text, fields.toArray(new Field[] {}), methods.toArray(new Method[] {}));
		$value = new ClassDefinition<ASTNode>(header, methodImplementations, fieldInitializers);
	}
;

class_member:
	  field_declaration 
	| method_definition 
	| class_definition	
;
	
field_declaration:
	^(FIELD_DECLARATION modifiers IDENT type expression?)

	{
		fields.add(new Field((Type)$type.node.getValue(), $IDENT.text));
		ASTNode initCode = $expression.node;
		fieldInitializers.add((initCode != null) ? Code.getASTCode(initCode) : null);
	}
;
	
method_definition:
	{ 
		Type returnType = null;
		Variable[] parameters = new Variable[] {};
	}
	^(METHOD_DEFINITION 
	modifiers 
	IDENT 
	(type { returnType = (Type)$type.node.getValue(); }) ? 
	(parameter_list { parameters = $parameter_list.result.toArray(new Variable[] {});} ) ?
	body)
	
	{
		methods.add(new Method(returnType, $IDENT.text, parameters));
		ASTNode code = $body.node;
		methodImplementations.add((code != null) ? Code.getASTCode(code) : null);
	}

;

	
extension_clause:
	extends_clause? implements_clause?
	;
	
extends_clause:
	^('extends' name)
	;
	
implements_clause:
	^('implements' name*)
	;
	
modifiers:
	access_modifier? 'static'? 'final'? 
	;
	
access_modifier:
	'private' | 'protected' | 'public'
	;
	

statement returns [ASTNode node]:
	(
	  s=assignment
	| s=if_statement
	| s=for_statement
	| s=while_statement
	| s=return_statement
	| s=method_invocation
	| s=variable_init
	) 
	
	{ 
		$node = $s.node; 
	}
;
	
variable_init returns [ASTNode node]:
	^(VARIABLE_DECLARATION type IDENT expression?)
	
	{ 
		ASTNode nameNode = new ASTNode(ASTNode.TYPE_NAME);
		nameNode.setValue($IDENT.text);
		$node = new ASTNode(ASTNode.TYPE_VAR_DECL, $type.node, nameNode); 
	}
;
	
method_invocation returns [ASTNode node]:
	{ $node = new ASTNode(ASTNode.TYPE_METHOD_INVOCATION); }
	^(fname=IDENT (expression { $node.addNode($expression.node); } )*)
	
	{ 
		ASTNode n = new ASTNode(ASTNode.TYPE_NAME);
		n.setValue($fname.text);
		$node.insertNode(0, n);
	}
;
	
parameter_list returns [List<Variable> result]:
{ $result = new ArrayList<Variable>(); }
	^(PARAMETER_LIST (type pname=IDENT { $result.add(new Variable((Type)$type.node.getValue(), $pname.text)); })*)
	;
	
for_statement returns [ASTNode node]:
	{ $node = new ASTNode(ASTNode.TYPE_FOR_LOOP); }
	^('for' assignment expression (statement { $node.addNode($statement.node); })* body)
  
  { 
  	$node.insertNode(0, $expression.node);
  	$node.insertNode(0, $assignment.node);
  }
;

while_statement returns [ASTNode node]:
	^('while' expression body) { $node = new ASTNode(ASTNode.TYPE_WHILE_LOOP, $expression.node, $body.node); };

if_statement returns [ASTNode node]:
	{ $node = new ASTNode(ASTNode.TYPE_IF); }
	^('if' expression body { $node.addNode($expression.node); $node.addNode($body.node); }
	 (else_if_statement { $node.addNode($else_if_statement.node); })* 
	 (else_statement { $node.addNode($else_statement.node); })?)
;
	
	
else_if_statement returns [ASTNode node]:
	^(ELSE_IF_CLAUSE expression body) { $node = new ASTNode(ASTNode.TYPE_IF, $expression.node, $body.node); }
;

else_statement returns [ASTNode node]:
	^('else' body) { $node = new ASTNode(ASTNode.TYPE_IF, null, $body.node); }
;

return_statement returns [ASTNode node]:
	^('return' expression) { $node = new ASTNode(ASTNode.TYPE_RETURN, $expression.node); }
	;
	
body returns [ASTNode node]:
	{ $node = new ASTNode(ASTNode.TYPE_BLOCK); }
	^(BODY 
	(statement { $node.addNode($statement.node); })
	*)
	;
	
type returns [ASTNode node]:
	  'void' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_void")); }
	| 'byte' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_byte")); }
	| 'short' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_short")); }
	| 'char' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_char")); }
	| 'int' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_int")); }
	| 'long' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_long")); }
	| 'float' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_float")); }
	| 'double' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_double")); }
	| 'boolean' { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType("_boolean")); }
	| name { $node = new ASTNode(ASTNode.TYPE_TYPE); $node.setValue(Type.getType($name.text)); }
	
	;

expression returns [ASTNode node]:
	  ^('&&' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_LAND, $op1.node, $op2.node); }
	| ^('||' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_LOR, $op1.node, $op2.node); }
	| ^('!=' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_NEQ, $op1.node, $op2.node); }
	| ^('==' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_EQU, $op1.node, $op2.node); }
	| ^('>=' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_GEQ, $op1.node, $op2.node); }
	| ^('<=' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_LEQ, $op1.node, $op2.node); }
	| ^('>' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_GTH, $op1.node, $op2.node); }
	| ^('<' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_LTH, $op1.node, $op2.node); }
	| ^('+' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_PLUS, $op1.node, $op2.node); }
	| ^('-' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_MINUS, $op1.node, $op2.node); }
	| ^('/' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_DIV, $op1.node, $op2.node); }
	| ^('*' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_MULT, $op1.node, $op2.node); }
	| ^('%' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_OP_MOD, $op1.node, $op2.node); }
	| ^(NEGATION op1=expression) { $node = new ASTNode(ASTNode.TYPE_OP_NEG, $op1.node); }
	| ^('!' op1=expression) { $node = new ASTNode(ASTNode.TYPE_OP_LNEG, $op1.node); }
	| name { $node = new ASTNode(ASTNode.TYPE_NAME); $node.setValue($name.text);  }
	| INTEGER_LITERAL { $node = new ASTNode(ASTNode.TYPE_LITERAL); $node.setValue(Long.parseLong($INTEGER_LITERAL.text)); }
	| STRING_LITERAL { $node = new ASTNode(ASTNode.TYPE_LITERAL); $node.setValue($STRING_LITERAL.text); }
	| CHAR_LITERAL { $node = new ASTNode(ASTNode.TYPE_LITERAL); $node.setValue($CHAR_LITERAL.text.charAt(0)); }
	| method_invocation { $node = $method_invocation.node; }
	;
	
	
assignment returns [ASTNode node]:
	^('=' lvalue expression) { $node = new ASTNode(ASTNode.TYPE_ASSIGNMENT, $lvalue.node, $expression.node);  }
	;
	
lvalue returns [ASTNode node]:
	  (name) { $node = new ASTNode(ASTNode.TYPE_NAME); $node.setValue($name.text); } 
	| ^(name expression) { 
	ASTNode nameNode = new ASTNode(ASTNode.TYPE_NAME);
	nameNode.setValue($name.text);
	$node = new ASTNode(ASTNode.TYPE_ARRAY_ACCESS, nameNode, $expression.node); }
	;
	
name returns [String text]:
	  NAME 					{ $text = $NAME.text; } 
	| IDENT					{ $text = $IDENT.text; } 	
;
	
