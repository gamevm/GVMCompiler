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
import com.gamevm.compiler.assembly.Modifier;
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
	^('package' qualifiedName)
	//-------------------------------------------------------
	{ 
		packageName = $qualifiedName.text;
		Type.setCurrentPackage(packageName);
	}
;

import_statement:
	^('import' qualifiedImportName)
	//-------------------------------------------------------
	{ 
		imports.add($qualifiedImportName.text);
		Type.importType($qualifiedImportName.text);
	}
;

class_definition returns [ClassDefinition<ASTNode> value]:
	^('class' modifiers IDENT extension_clause class_member*)
	
	{
		ClassDeclaration header = new ClassDeclaration($modifiers.value, packageName + "." + $IDENT.text, fields.toArray(new Field[] {}), methods.toArray(new Method[] {}));
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
		fields.add(new Field($modifiers.value, (Type)$type.node.getValue(), $IDENT.text));
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
		methods.add(new Method($modifiers.value, returnType, $IDENT.text, parameters));
		ASTNode code = $body.node;
		methodImplementations.add((code != null) ? Code.getASTCode(code) : null);
	}

;

	
extension_clause:
	extends_clause? implements_clause?
	;
	
extends_clause:
	^('extends' classOrInterfaceType)
	;
	
implements_clause:
	^('implements' classOrInterfaceType*)
	;
	
modifiers returns [int value]:
	{ 
		int accessModifier = Modifier.DEFAULT;
		boolean staticModifier = false;
		boolean finalModifier = false;
	}

	(access_modifier 	{ accessModifier = $access_modifier.value; })?
	('static'			{ staticModifier = true; })? 		
	('final'			{ staticModifier = true; })? 			
	
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
	(
	  s=expression
	| s=if_statement
	| s=for_statement
	| s=while_statement
	| s=return_statement
	| s=variable_init
	) 
	
	{ 
		$node = $s.node; 
	}
;
	
variable_init returns [ASTNode node]:
	{
		ASTNode expression = null;
	}

	^(VARIABLE_DECLARATION type IDENT (expression { expression = $expression.node; })?)
	
	{ 
		ASTNode nameNode = new ASTNode(ASTNode.TYPE_NAME);
		nameNode.setValue($IDENT.text);
		$node = new ASTNode(ASTNode.TYPE_VAR_DECL, $type.node, nameNode); 
		if (expression != null)
			$node.addNode(expression);
	}
;
	
method_invocation returns [ASTNode node]:
	{ $node = new ASTNode(ASTNode.TYPE_METHOD_INVOCATION); }
	^(METHOD_INVOCATION fname=IDENT (expression { $node.addNode($expression.node); } )*)
	
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
	^('for' a=expression c=expression (statement { $node.addNode($statement.node); })* body)
  
  { 
  	$node.insertNode(0, $body.node);
  	$node.insertNode(0, $c.node);
  	$node.insertNode(0, $a.node);
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
	{
		int dimension = 0;
	}

	basic_type ('[' ']' { dimension++; })*
	
	{
		$node = new ASTNode(ASTNode.TYPE_TYPE);
		if (dimension > 0) {
			$node.setValue(Type.getArrayType($basic_type.type, dimension));
		} else {
			$node.setValue($basic_type.type);
		}
	}
	
	;
	
basic_type returns [Type type]:
	  'void' { $type = Type.getType("_void"); }
	| 'byte' { $type = Type.getType("_byte"); }
	| 'short' { $type = Type.getType("_short"); }
	| 'char' { $type = Type.getType("_char"); }
	| 'int' { $type = Type.getType("_int"); }
	| 'long' { $type = Type.getType("_long"); }
	| 'float' { $type = Type.getType("_float"); }
	| 'double' { $type = Type.getType("_double"); }
	| 'boolean' { $type = Type.getType("_boolean"); }
	| id1=IDENT { StringBuilder id = new StringBuilder(); id.append($id1.text); } ('.' id2=IDENT { id.append("." + $id2.text); } )* { $type = Type.getType(id.toString()); }
	;
	
qualifiedName returns [String text]:  
	id1=IDENT { StringBuilder id = new StringBuilder(); id.append($id1.text); } ('.' id2=IDENT { id.append("." + $id2.text); })* { $text = id.toString(); }
    ;
    
qualifiedImportName returns [String text]: 
	id1=IDENT { StringBuilder id = new StringBuilder(); id.append($id1.text); } ('.' id2=IDENT { id.append("." + $id2.text); })* { $text = id.toString(); }
    ;
    
classOrInterfaceType returns [String text]:
	id1=IDENT { StringBuilder id = new StringBuilder(); id.append($id1.text); } ('.' id2=IDENT { id.append("." + $id2.text); })* { $text = id.toString(); }
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
	| ^('.' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_QUALIFIED_ACCESS, $op1.node, $op2.node); }
	| ^('[' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_ARRAY_ACCESS, $op1.node, $op2.node); }
	| ^('=' op1=expression op2=expression) { $node = new ASTNode(ASTNode.TYPE_ASSIGNMENT, $op1.node, $op2.node); }
	| INTEGER_LITERAL { $node = new ASTNode(ASTNode.TYPE_LITERAL); $node.setValue(Long.parseLong($INTEGER_LITERAL.text)); $node.setValueType(Type.INT); }
	| STRING_LITERAL { $node = new ASTNode(ASTNode.TYPE_LITERAL); $node.setValue($STRING_LITERAL.text); $node.setValueType(Type.getType("gc.String")); }
	| CHAR_LITERAL { $node = new ASTNode(ASTNode.TYPE_LITERAL); $node.setValue($CHAR_LITERAL.text.charAt(0)); $node.setValueType(Type.CHAR); }
	| BOOLEAN_LITERAL { $node = new ASTNode(ASTNode.TYPE_LITERAL); $node.setValue(Boolean.valueOf($BOOLEAN_LITERAL.text)); $node.setValueType(Type.BOOLEAN); }
	| IDENT { $node = new ASTNode(ASTNode.TYPE_VARIABLE); $node.setValue($IDENT.text); }
	| method_invocation { $node = $method_invocation.node; }
	;
	
	
//assignment returns [ASTNode node]:
//	^('=' lvalue expression) { $node = new ASTNode(ASTNode.TYPE_ASSIGNMENT, $lvalue.node, $expression.node);  }
//	;
	
//lvalue returns [ASTNode node]:
//	  (name) { $node = new ASTNode(ASTNode.TYPE_NAME); $node.setValue($name.text); } 
//	| ^(name expression) { 
//	ASTNode nameNode = new ASTNode(ASTNode.TYPE_NAME);
//	nameNode.setValue($name.text);
//	$node = new ASTNode(ASTNode.TYPE_ARRAY_ACCESS, nameNode, $expression.node); }
//	;
	

	
