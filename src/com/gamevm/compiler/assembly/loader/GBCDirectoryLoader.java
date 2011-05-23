package com.gamevm.compiler.assembly.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class GBCDirectoryLoader extends AbstractGBCLoader {
	
	private Collection<File> searchPaths;
	
	public GBCDirectoryLoader(File... searchPaths) {
		this.searchPaths = new ArrayList<File>();
		this.searchPaths.addAll(Arrays.asList(searchPaths));
	}
	
	@Override
	protected File getClassFile(String typeName) throws IOException {
		String path = typeName.replace('.', '/') + ".gbc";
		for (File sp : searchPaths) {
			File result = new File(sp, path);
			if (result.exists()) {
				return result;
			}
		}
		throw new IOException("No class file found for type " + typeName);
	}

}
