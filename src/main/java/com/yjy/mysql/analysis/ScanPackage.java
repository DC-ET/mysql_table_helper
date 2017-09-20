package com.yjy.mysql.analysis;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class ScanPackage {
	
	public static List<Class<?>> getClassesByPackageName(String packageName) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			String path = packageName.replace(".", "/");
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = (URL)resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			for (Iterator<File> localIterator = dirs.iterator(); localIterator.hasNext(); ) {
				File directory = (File)localIterator.next();
				classes.addAll(findClasses(directory, packageName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}
	
	public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		File[] arrayOfFile;
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		int j = (arrayOfFile = files).length; 
		for (int i = 0; i < j; ++i) {
			File file = arrayOfFile[i];
			String fileName = file.getName();
			if (file.isDirectory()) {
				if (StringUtils.isNotBlank(fileName) && fileName.contains(".")) {
					throw new AssertionError();
				}
				classes.addAll(findClasses(file, packageName + '.' + file.getName()));
			} else if (fileName.endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6)));
			}
		}
		return classes;
	}

}