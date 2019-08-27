package com.yun.zhe.util;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.yun.zhe.entity.BeanInfo;
/**
 *  刘云哲
 *
 */
public class XmlHandler extends DefaultHandler {
	public Map<String, BeanInfo> getBeanMap() {
		return beanMap;
	}

	public void setBeanMap(Map<String, BeanInfo> beanMap) {
		this.beanMap = beanMap;
	}


	private Map<String,BeanInfo> beanMap;
	private BeanInfo beanInfo;
    /**
     * 把解析之后的放到一个map中
     */
	@Override
	public void startDocument() throws SAXException {
		/**
		 * 文档解析之前创建一个bean容器
		 */
		beanMap=new HashMap<>();
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	  if("bean".equalsIgnoreCase(qName)) {
		  beanInfo=new BeanInfo();
		  String id=attributes.getValue("id");
		  String className=attributes.getValue("className");
		  String path=attributes.getValue("path");
		  beanInfo.setId(id);
		  beanInfo.setClassName(className);
		  beanInfo.setPath(path);
	  }
	
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		/**
		 * 结束之后需要把 beanInfo 置为null防止强引用
		 */
		if("bean".equalsIgnoreCase(qName)) {
			beanMap.put(beanInfo.getPath(), beanInfo);
			beanInfo=null;
		}
		
	}
	
	
	@Override
	public void endDocument() throws SAXException {
	}
}
