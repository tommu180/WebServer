package edu.upenn.cis.cis455.webserver.api;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import edu.upenn.cis.cis455.webserver.HttpRequest;

/**
 * @author Todd J. Green, modified by Nick Taylor
 */
public class ServletEngine {
	private HttpRequest request;
	private String webxmlLoc;
	private Handler h;
	private HashMap<String,HttpServlet> servlets;
	private HttpServletContextM context;
	private HttpServletConfig config;
	private HttpServletSessionM fs;
	private static HashMap<String, HttpServletSessionM> sessionStorage;
	private String defaultTimeOut;
	
	public Handler getHandler() {
		return this.h;
	}
	
	/**
	 * @return the session key value map in servlet engine
	 */
	public static HashMap<String, HttpServletSessionM> getSessions() {
		if (sessionStorage == null) {
			sessionStorage = new HashMap<String, HttpServletSessionM>();//For test
		}
		return sessionStorage;
	}
	
	public HashMap<String, String> getUrlPattern() {
		return h.m_urlPattern;
	}
	
	public ServletEngine(String webxmlLoc) throws Exception {
		this.webxmlLoc = webxmlLoc;
		h = parseWebdotxml(webxmlLoc);
		
		context = createContext(h);
		servlets = createServlets(h);
		sessionStorage = new HashMap<String, HttpServletSessionM>();
		fs = null;
		
//		for (Map.Entry<String, String> entry : h.m_urlPattern.entrySet()) {
//			System.out.println(entry.getKey() + ":" + entry.getValue());
//		}
		
		
	}
	
	/**
	 * Delete invalidated session
	 */
	public static void deleteInvalidatedSession() {
		for (String entry : sessionStorage.keySet()) {
			if (!sessionStorage.get(entry).isValid()) {
				sessionStorage.remove(entry);
			}
		}
	}
	
	public void setRequest(HttpRequest request) {
		this.request = request;
	}
		
	/**
	 * parse web.xml
	 * @param webdotxml
	 * @return
	 * @throws Exception
	 */
	public Handler parseWebdotxml(String webdotxml) throws Exception {
		Handler h = new Handler();
		File file = new File(webdotxml);
		if (file.exists() == false) {
			System.err.println("error: cannot find " + file.getPath());
			System.exit(-1);
		}
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(file, h);
		defaultTimeOut = h.getTimeOut();
		return h;
	}
	
	private HttpServletContextM createContext(Handler h) {
		HttpServletContextM fc;
		fc = new HttpServletContextM(h.getM_displayName());
		for (String param : h.m_contextParams.keySet()) {
			fc.setInitParam(param, h.m_contextParams.get(param));
		}
		return fc;
	}
	
	private HashMap<String,HttpServlet> createServlets(Handler h) throws Exception {
		HashMap<String,HttpServlet> servlets = new HashMap<String,HttpServlet>();
		for (String servletName : h.m_servlets.keySet()) {
			config = new HttpServletConfig(servletName, context);
			String className = h.m_servlets.get(servletName);
			Class servletClass = Class.forName(className);
			HttpServlet servlet = (HttpServlet) servletClass.newInstance();
			HashMap<String,String> servletParams = h.m_servletParams.get(servletName);
			if (servletParams != null) {
				for (String param : servletParams.keySet()) {
					config.setInitParam(param, servletParams.get(param));
				}
			}
			servlet.init(config);
			servlets.put(servletName, servlet);
		}
		return servlets;
	}
	
	/**
	 * shut down servlet engine
	 */
	public void shutdown() {
		if (servlets != null) {
            for(HttpServlet servlet : servlets.values()) {
                servlet.destroy();
            }
            servlets = null;
        }
	}
	
	/**
	 * Handle http dynamic request
	 * @param urlPatternString
	 * @param out
	 * @throws ServletException
	 * @throws IOException
	 */
	public void handleRequest(String urlPatternString, OutputStream out) throws ServletException, IOException {
		HttpServletRequestM request = new HttpServletRequestM(this.request, urlPatternString, defaultTimeOut);
		Map<String,ArrayList<String>> show = request.getParameterMap();
		for (Map.Entry<String, ArrayList<String>> entry : show.entrySet()) {
			//System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		
		HttpServletResponseM response = new HttpServletResponseM(out, request);
		String servletName = h.m_urlPattern.get(urlPatternString);
		HttpServlet servlet = servlets.get(servletName);
		if (servlet == null) {
			System.err.println("error: cannot find mapping for servlet ");
		}
		servlet.service(request, response);
		response.flushBuffer();		
		fs = (HttpServletSessionM) request.getSession(false);
	}
}
 
