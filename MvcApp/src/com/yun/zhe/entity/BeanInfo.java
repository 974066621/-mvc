package com.yun.zhe.entity;

public class BeanInfo {
    private  String className;
    private String id;
    private String path;
	public BeanInfo(String className, String id, String path) {
		super();
		this.className = className;
		this.id = id;
		this.path = path;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BeanInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "className:___ "+className+"____id"+ id;
	}
	
}
