package com.zoi7.mysql.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author yjy
 * 2018-07-12 18:43
 */
public class ScanJar {

    private static final Logger log = LoggerFactory.getLogger(ScanJar.class);

    public static Set<Class<?>> getClassesByPackageName(String packageName) throws IOException, ClassNotFoundException {
        log.debug("packageName : {}", packageName);
        Set<Class<?>> classes = new HashSet<Class<?>>();
        //通过当前线程得到类加载器从而得到URL的枚举
        Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader()
                .getResources(packageName.replace(".", "/"));
        while (urlEnumeration.hasMoreElements()) {
            //得到的结果大概是：jar:file:/C:/Users/ibm/.m2/repository/junit/junit/4.12/junit-4.12.jar!/org/junit
            URL url = urlEnumeration.nextElement();
            String protocol = url.getProtocol();
            if ("jar".equalsIgnoreCase(protocol)) {
                //转换为JarURLConnection
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                if (connection != null) {
                    JarFile jarFile = connection.getJarFile();
                    if (jarFile != null) {
                        //得到该jar文件下面的类实体
                        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                        while (jarEntryEnumeration.hasMoreElements()) {
                            JarEntry entry = jarEntryEnumeration.nextElement();
                            String jarEntryName = entry.getName();
                            //这里我们需要过滤不是class文件和不在basePack包名下的类
                            if (jarEntryName.contains(".class") && jarEntryName.replaceAll("/", ".").startsWith(packageName)) {
                                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
                                Class<?> cls = Class.forName(className);
                                classes.add(cls);
                            }
                        }
                    }
                }
            }
        }
        return classes;
    }

}
