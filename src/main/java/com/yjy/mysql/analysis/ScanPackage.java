package com.yjy.mysql.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ScanPackage {

	private static final Logger log = LoggerFactory.getLogger(ScanPackage.class);
	
	public static Set<Class<?>> getClassesByPackageName(String packageName) {
		log.debug("packageName : {}", packageName);
		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			String path = packageName.replace(".", "/");
			Enumeration<URL> resources = classLoader.getResources(path);
			log.debug("path : {}, resources : {}", path, resources.hasMoreElements());
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			log.debug("dirsSize : {}", dirs.size());
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
		} catch (Exception e) {
			log.error("error", e);
		}
		return classes;
	}
	
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		log.debug("path : {}, dir : {}, exist: {}, read: {}, write: {}, exec: {}, packageName: {}",
				directory.getAbsolutePath(), directory.getName(), directory.exists(), directory.canRead(),
				directory.canWrite(), directory.canExecute(), packageName);
		File[] arrayOfFile;
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			log.warn("!directory.exists()");
			return classes;
		}
		File[] files = directory.listFiles();
		if (files == null) {
			log.warn("files is null");
			return classes;
		}
		log.debug("filesSize : {}", files.length);
		int j = (arrayOfFile = files).length; 
		for (int i = 0; i < j; ++i) {
			File file = arrayOfFile[i];
			String fileName = file.getName();
			if (file.isDirectory()) {
				if (fileName.contains(".")) {
					throw new AssertionError();
				}
				classes.addAll(findClasses(file, packageName + '.' + file.getName()));
			} else if (fileName.endsWith(".class")) {
				String className = packageName + '.' + fileName.substring(0, fileName.length() - 6);
				log.debug("addClasses : {}", className);
				classes.add(Class.forName(className));
			}
		}
		return classes;
	}

}