package edu.upenn.cis.cis455.webserver.api;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

/**
 * @author Chunxiao Mu
 */
public class HttpServletContextM implements ServletContext {
	private HashMap<String,Object> attributes;
	private HashMap<String,String> initParams;
	private String contextPath;
	private String dispName;
	
	public HttpServletContextM(String displayName) {
		attributes = new HashMap<String,Object>();
		initParams = new HashMap<String,String>();
		this.dispName = displayName;
		this.contextPath = new File(".").getAbsolutePath();
		this.contextPath = contextPath.substring(0, contextPath.length()-1) + "web";
		//System.out.println("comtextPath:" + contextPath);
		//System.out.println("displayName:" + this.getServletContextName());
	}
	
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	public Enumeration<String> getAttributeNames() {
		Set<String> keys = attributes.keySet();
		Vector<String> atts = new Vector<String>(keys);
		return atts.elements();
	}
	
	public ServletContext getContext(String name) {
		return this;
	}
	
	public String getInitParameter(String name) {
		return initParams.get(name);
	}
	
	public Enumeration<String> getInitParameterNames() {
		Set<String> keys = initParams.keySet();
		Vector<String> atts = new Vector<String>(keys);
		return atts.elements();
	}
	
	public int getMajorVersion() {
		return 0;
	}
	//
	public String getMimeType(String file) {
		return null;
	}
	
	public int getMinorVersion() {
		return 1;
	}
	//
	public RequestDispatcher getNamedDispatcher(String name) {
		return null;
	}
	
	public String getRealPath(String path) {//TODO:
		if (new File(contextPath).exists()) {
            try {
                File realPath = new File(contextPath + path);
                return realPath.getCanonicalPath().startsWith(contextPath)? realPath.getCanonicalPath() : null;
            }
            catch (IOException ex) {
            	System.out.println("Cannot get real path");
                return null;
            }
        }
        else {
            return null;
        }
	}
	//
	public RequestDispatcher getRequestDispatcher(String name) {
		return null;
	}
	//
	public java.net.URL getResource(String path) {
		return null;
	}
	//
	public java.io.InputStream getResourceAsStream(String path) {
		return null;
	}
	//
	@SuppressWarnings("rawtypes")
	public java.util.Set getResourcePaths(String path) {
		return null;
	}
	
	public String getServerInfo() {
		return "Httpserver/" + getMajorVersion() + "." + getMinorVersion() + " Chunxiao";
	}
	//
	public Servlet getServlet(String name) {
		return null;
	}
	
	public String getServletContextName() {
		return dispName;
	}
	//
	public Enumeration<String> getServletNames() {
		return null;
	}
	//
	public Enumeration<String> getServlets() {
		return null;
	}
	//
	public void log(Exception exception, String msg) {
		log(msg, (Throwable) exception);
	}
	//
	public void log(String msg) {
		System.err.println(msg);
	}
	//
	public void log(String message, Throwable throwable) {
		System.err.println(message);
		throwable.printStackTrace(System.err);
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	public void setAttribute(String name, Object object) {
		attributes.put(name, object);
	}
	
	void setInitParam(String name, String value) {
		initParams.put(name, value);
	}
}
