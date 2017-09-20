package com.yjy.mysql.analysis;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.*;

public class ParseXML {
	
	public static Document getDocument(String xmlPath) {
		Document document = null;
		try {
			document = new SAXReader().read(ParseXML.class.getResourceAsStream(xmlPath));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}
	
	public static Map<String, Object> getConfig(String xmlPath) {
		List<String> packages = new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		Document document = getDocument(xmlPath);
		Element autoElement = (Element)document.selectSingleNode("/config/base");
		map.put("auto", autoElement.selectSingleNode("auto").getText());
		map.put("showsql", autoElement.selectSingleNode("showsql").getText());
		map.put("dialect", autoElement.selectSingleNode("dialect").getText());
		Element packageElement = (Element)document.selectSingleNode("/config/package/list");
		List<?> valueList = packageElement.selectNodes("value");
		for (Iterator<?> localIterator = valueList.iterator(); localIterator.hasNext(); ) {
			Element e = (Element)localIterator.next();
			packages.add(e.getText());
		}
		map.put("list", packages);
		Element dbElement = (Element)document.selectSingleNode("/config/db");
		map.put("username", dbElement.selectSingleNode("username").getText());
		map.put("password", dbElement.selectSingleNode("password").getText());
		map.put("url", dbElement.selectSingleNode("url").getText());
		map.put("driver", dbElement.selectSingleNode("driver").getText());
		return map;
	}

}