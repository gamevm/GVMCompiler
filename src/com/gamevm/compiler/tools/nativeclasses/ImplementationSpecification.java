package com.gamevm.compiler.tools.nativeclasses;

public class ImplementationSpecification {
	
	private String name;
	private String version;
	private String module;
	private String prefix;
	private String suffix;
	
	public ImplementationSpecification(String name, String version, String module, String prefix, String suffix) {
		super();
		this.name = name;
		this.version = version;
		this.module = module;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getModule() {
		return module;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}
	
	

}
