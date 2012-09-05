package com.gamevm.compiler.tools.nativeclasses;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.gamevm.compiler.Type;
import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.compiler.assembly.Field;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.Variable;
import com.gamevm.utils.xml.ElementParser;
import com.gamevm.utils.xml.XML;

public class XMLClassReader implements ElementParser<ClassDeclaration> {
	
	private String packageName;
	
	public XMLClassReader(String packageName) {
		this.packageName = packageName;
	}
	
	private class MethodParser implements ElementParser<Method> {

		@Override
		public Method parse(Element e) {
			String name = e.getAttribute("name");		
			int modifier = e.hasAttribute("modifier") ? Integer.parseInt(e.getAttribute("modifier")) : Modifier.PUBLIC;
			Type returnType = e.hasAttribute("returnType") ? Type.getType(e.getAttribute("type")) : Type.VOID;
			
			Variable[] parameters = new Variable[e.getChildNodes().getLength()];
			
			int i = 0;
			for (Element param : XML.getChildren(e)) {
				parameters[i++] = new Variable(Type.getType(param.getAttribute("type")), param.getAttribute("name"));
			}
			
			return new Method(modifier, returnType, name, parameters);
		}
		
	}
	
	private class FieldParser implements ElementParser<Field> {

		@Override
		public Field parse(Element e) {
			String name = e.getAttribute("name");		
			int modifier = e.hasAttribute("modifier") ? Integer.parseInt(e.getAttribute("modifier")) : Modifier.PUBLIC;
			Type type = e.hasAttribute("type") ? Type.getType(e.getAttribute("type")) : Type.VOID;
			
			return new Field(modifier, type, name);
		}
		
	}
	
	private class TypeParser implements ElementParser<Type> {

		@Override
		public Type parse(Element e) {
			return Type.getType(e.getAttribute("name"));
		}
		
	}
	
	private class ImportsParser implements ElementParser<Type> {

		@Override
		public Type parse(Element e) {
			return Type.importType(e.getAttribute("type"));
		}
		
	}
	
	public ClassDeclaration parse(Element classElement) {
		
		String name = classElement.getAttribute("name");
		int modifier = Integer.parseInt(classElement.getAttribute("modifier"));
		
		Type[] imports = XML.parseChildren(XML.getChild(classElement, "imports"), new ImportsParser(), Type.class);
		
		Type parentClass = classElement.hasAttribute("parent") ? Type.getType(classElement.getAttribute("parent")) : null;
		
		Method[] methods = XML.parseChildren(XML.getChild(classElement, "methods"), new MethodParser(), Method.class);
		Field[] fields = XML.parseChildren(XML.getChild(classElement, "fields"), new FieldParser(), Field.class);
		Type[] parentInterfaces = XML.parseChildren(XML.getChild(classElement, "implements"), new TypeParser(), Type.class);
		
		return new ClassDeclaration(modifier, packageName + "." + name, fields, methods, parentClass, parentInterfaces, imports);
	}

}
