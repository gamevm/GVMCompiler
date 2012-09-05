package com.gamevm.compiler.tools.nativeclasses;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.gamevm.compiler.assembly.ClassDeclaration;
import com.gamevm.utils.xml.AttributeFilter;
import com.gamevm.utils.xml.FilteredNodeIterable;
import com.gamevm.utils.xml.XML;

public class XMLNativeReader {
	
	private Document document;
	private List<ClassDeclaration> classes;
	
	public XMLNativeReader(File file) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document = db.parse(file);
	}
	
	private void processPackage(Element packageElement, String parentPackage) {
		String packageName = parentPackage + "." + packageElement.getAttribute("name");
		classes.addAll(XML.parse(XML.getChildren(packageElement, "class"), new XMLClassReader(packageName)));
		for (Element p : XML.getChildren(packageElement, "package")) {
			processPackage(p, packageName);
		}
	}
	
	public List<ClassDeclaration> read() throws ParserConfigurationException, SAXException, IOException {
		classes = new ArrayList<ClassDeclaration>();
		
		for (Element packageElement : XML.getChildren(document.getDocumentElement(), "package")) {
			processPackage(packageElement, "");
		}
		
		return classes;
	}
	
	public ImplementationSpecification getImplementation(String name) {
		FilteredNodeIterable<Element> implementations = new FilteredNodeIterable<Element>(document.getElementsByTagName("implementation"), new AttributeFilter("name", name));
		
		Element impl = implementations.iterator().next();
		
		return new ImplementationSpecification(impl.getAttribute("name"), impl.getAttribute("version"), impl.getAttribute("module"), impl.getAttribute("prefix"), impl.getAttribute("suffix"));
	}

}
