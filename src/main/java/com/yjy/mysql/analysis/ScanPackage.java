package com.yjy.mysql.analysis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class ScanPackage {

	private static final Logger log = LoggerFactory.getLogger(ScanPackage.class);
	
	public static List<Class<?>> getClassesByPackageName(String packageName) {
		log.info("ScanPackage getClassesByPackageName, packageName : {}", packageName);
		List<Class<?>> classes = new ArrayList<Class<?>>();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			String path = packageName.replace(".", "/");
			Enumeration<URL> resources = classLoader.getResources(path);
			log.info("ScanPackage getClassesByPackageName, path : {}, resources : {}", path, resources.hasMoreElements());
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = (URL)resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			log.info("ScanPackage getClassesByPackageName, dirsSize : {}", dirs.size());
			for (Iterator<File> localIterator = dirs.iterator(); localIterator.hasNext(); ) {
				File directory = (File)localIterator.next();
				classes.addAll(findClasses(directory, packageName));
			}
		} catch (Exception e) {
			log.error("getClassesByPackageName throw an error", e);
		}
		return classes;
	}
	
	public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		log.info("ScanPackage findClasses, path : {}, dir : {}, exist: {}, read: {}, write: {}, exec: {}, packageName: {}",
				directory.getAbsolutePath(), directory.getName(), directory.exists(), directory.canRead(), directory.canWrite(), directory.canExecute(), packageName);
		File[] arrayOfFile;
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			log.info("ScanPackage findClasses > !directory.exists()");
			return classes;
		}
		File[] files = directory.listFiles();
		log.info("ScanPackage findClasses, filesSize : {}", files.length);
		int j = (arrayOfFile = files).length; 
		for (int i = 0; i < j; ++i) {
			File file = arrayOfFile[i];
			String fileName = file.getName();
			log.info("ScanPackage findClasses, fileName : {}", fileName);
			if (file.isDirectory()) {
				if (StringUtils.isNotBlank(fileName) && fileName.contains(".")) {
					throw new AssertionError();
				}
				classes.addAll(findClasses(file, packageName + '.' + file.getName()));
			} else if (fileName.endsWith(".class")) {
				String className = packageName + '.' + fileName.substring(0, fileName.length() - 6);
				log.info("ScanPackage findClasses, addClasses : {}", className);
				classes.add(Class.forName(className));
			}
		}
		return classes;
	}

}