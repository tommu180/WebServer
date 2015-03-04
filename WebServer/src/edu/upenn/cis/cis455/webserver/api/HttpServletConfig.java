package edu.upenn.cis.cis455.webserver.api;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * @author Chunxiao Mu
 */
public class HttpServletConfig implements ServletConfig {
	private String name;
	private HttpServletContextM context;
	private HashMap<String,String> initParams;
	
	public HttpServletConfig(String name, HttpServletContextM context) {
		this.name = name;
		this.context = context;
		initParams = new HashMap<String,String>();
	}

	public String getInitParameter(String name) {
		return initParams.get(name);
	}
	
	public Enumeration getInitParameterNames() {
		Set<String> keys = initParams.keySet();
		Vector<String> atts = new Vector<String>(keys);
		return atts.elements();
	}
	
	public ServletContext getServletContext() {
		return context;
	}
	
	public String getServletName() {
		return name;
	}

	public void setInitParam(String name, String value) {
		initParams.put(name, value);
	}
}
