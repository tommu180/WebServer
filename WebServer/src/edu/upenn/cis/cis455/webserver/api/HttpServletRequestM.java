package edu.upenn.cis.cis455.webserver.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.upenn.cis.cis455.webserver.HttpRequest;

/**
 * @author Chunxiao Mu
 */
public class HttpServletRequestM implements HttpServletRequest {
	private Properties m_props = new Properties();
	private HttpServletSessionM m_session = null;
	
	private String defaultTimeOut;
	private HttpRequest request;
	private Map<String,ArrayList<String>> m_params;
	private Map<String, ArrayList<String>> headersValMap;
	private String urlPattern;
	private String encoding;
	private Locale locale;
	private String contentType;
	private int contentLen;
	private boolean setYet;

	public HttpServletRequestM(HttpRequest request, String urlPattern, String timeOut) {
		this.request = request;
		this.m_params = new HashMap<String, ArrayList<String>>();
		
		if (request.getHeaders().containsKey("sessionId")) {
			//System.out.println("got the session ID");
			m_session = ServletEngine.getSessions().get(request.getHeaders().get("sessionId").get(0));
			if (m_session != null && m_session.isValid() && (m_session.getLastAccessedTime() - new Date().getTime())/1000 > m_session.getMaxInactiveInterval()) {
				m_session.invalidate();
			}
		} else {
			m_session = null;
		}
		
		this.headersValMap = request.getHeaders();
		this.urlPattern = urlPattern;
		this.encoding = "ISO-8859-1";
		this.locale = null;
		this.contentType = "application/x-www-form-urlencoded";
		this.contentLen = -1;
		this.setYet = false;
		if (timeOut != null) {
			this.defaultTimeOut = timeOut;
		} else {
			this.defaultTimeOut = "10";
		}
		//System.out.println("DEFAULT TIMEOUT:" + defaultTimeOut);
	}
	
	public void setParameter(String key, String value) {
		if (!m_params.containsKey(key)) {
			ArrayList<String> val = new ArrayList<String>();
			val.add(value);
			m_params.put(key, val);
		} else {
			ArrayList<String> val = m_params.get(key);
			val.add(value);
		}
	}
	
	public void clearParameters() {
		m_params.clear();
	}
	
	public boolean hasSession() {
		return ((m_session != null) && m_session.isValid());
	}
	
	public void getPostBody() {
		BufferedReader in = null;
		try {//TODO: choose servlet after there is no 4xx response
			in = getReader();
			setYet = true;
			if (request.getMethod().toUpperCase().equals("POST")) {
	        	//System.out.println("herer");
	        	String conLen = request.getHeaders().get("content-length".toLowerCase()).get(0);
	        	conLen = conLen.substring(1);
	        	//System.out.println(conLen);
	        	contentLen = Integer.valueOf(conLen);
	        	//System.out.println("len:" + contentLen);
	        	int val = 0;
	        	char[] body = new char[contentLen];
	        	int i = 0;
				while (i < contentLen && (val = in.read()) != -1) {
					body[i] = (char) val;
					i++;
				}
	        	String bodyPara = new String(body);
	        	//System.out.println(bodyPara);
	        	String[] para = bodyPara.split("&");
	        	for (String paraA : para) {
	        		String[] parts = paraA.split("=");
	        		setParameter(parts[0], parts[1]);
	        	}
	        } else {
	        	String urlPara = request.getQuery();
	        	if (urlPara != null) {
		        	String[] para = urlPara.split("&");
		        	for (String paraA : para) {
		        		String[] parts = paraA.split("=");
		        		setParameter(parts[0], parts[1]);
		        	}
	        	} else {
	        		
	        	}
	        }
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getAuthType()
	 */
	public String getAuthType() {
		return BASIC_AUTH;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getCookies()
	 */
	public Cookie[] getCookies() {
		ArrayList<Cookie> tempCookies = request.getCookies();
		if (tempCookies != null) {
			return tempCookies.toArray(new Cookie[tempCookies.size()]);
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
	 */
	public long getDateHeader(String dateHeader) {
		dateHeader = dateHeader.toLowerCase();
        if (headersValMap.containsKey(dateHeader)) {
            try {
            	return parseTime(dateHeader, " E, dd MMM yyyy HHmmss z");
            } catch (ParseException ex) {}
        	try {
                return parseTime(dateHeader, " E, dd-MMM-yy HHmmss z");
        	} catch (ParseException ex) {}
        	try {
                return parseTime(dateHeader, " E MMM dd HHmmss yyyy");
        	} catch (ParseException ex) {
                throw new IllegalArgumentException("Cannot be converted to Date.");
            }
        }
        return -1;
	}
	
	private long parseTime(String dateHeader, String format) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.parse(request.getHeaders().get(dateHeader).get(0)).getTime();
    }

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String headerName) {
		if (headersValMap.containsKey(headerName.toLowerCase()))
			return headersValMap.get(headerName.toLowerCase()).get(0);
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
	 */
	public Enumeration<String> getHeaders(String arg0) {
        return Collections.enumeration(headersValMap.get(arg0.toLowerCase()));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
	 */
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(headersValMap.keySet());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
	 */
	public int getIntHeader(String arg0) {
		if (headersValMap.containsKey(arg0.toLowerCase()))
			return Integer.valueOf(headersValMap.get(arg0.toLowerCase()).get(0));
		return -1;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getMethod()
	 */
	public String getMethod() {
		//return m_method;
		return request.getMethod().toUpperCase();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getPathInfo()
	 */
	public String getPathInfo() {
		if (urlPattern.charAt(urlPattern.length() - 1) != '*') {
			return null;
		}
		String matchString = urlPattern.substring(0, urlPattern.length() - 1);
		//System.out.println("matchString:" + matchString);
		int matchStart = request.getPath().lastIndexOf(matchString);
		if (matchStart != -1) {
			int matchEnd = matchStart + matchString.length() - 1;
			String pathInfo = request.getPath().substring(matchEnd);
			String[] retStrings = pathInfo.split("\\?");
			return retStrings[0];
		} else {
			return null;
		}
	}

	/* (non-Javadoc) 
	 * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
	 */
	public String getPathTranslated() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getContextPath()
	 */
	public String getContextPath() {
		return "";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getQueryString()
	 */
	public String getQueryString() {
		return request.getQuery();
		// should return the HTTP GET query string, i.e., the portion
		//after the “?” when a GET form is posted.
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
	 */
	public String getRemoteUser() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
	 */
	public boolean isUserInRole(String arg0) {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
	 */
	public String getRequestedSessionId() {
		if (hasSession()) {
			return m_session.getId();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getRequestURI()
	 */
	public String getRequestURI() {
		return request.getUri().getPath();
//		return getContextPath() + getServletPath() + getPathInfo();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getRequestURL()
	 */
	public StringBuffer getRequestURL() {
		StringBuffer ret = new StringBuffer();
		ret.append(this.getScheme());
        ret.append("://");
        ret.append(this.getServerName());
        ret.append(":");
        ret.append(this.getServerPort());
        ret.append(this.getRequestURI());
        return ret;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getServletPath()
	 */
	public String getServletPath() {
		if (urlPattern.charAt(urlPattern.length() - 1) != '*')
			return urlPattern;
		return urlPattern.substring(0, urlPattern.length() - 1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
	 */
	public HttpSession getSession(boolean arg0) {
		if (arg0) {
			if (!hasSession()) {
				m_session = new HttpServletSessionM(defaultTimeOut);
			}
		} else {
			if (!hasSession()) {
				m_session = null;
			}
		}
		//System.out.println("request session set");
		if (m_session != null)
			m_session.setLastAccess(new Date());
		return m_session;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getSession()
	 */
	public HttpSession getSession() {
		return getSession(true);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
	 */
	public boolean isRequestedSessionIdValid() {
		return m_session.isValid();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
	 */
	public boolean isRequestedSessionIdFromCookie() {
		return hasSession() && !m_session.isNew();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
	 */
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) {
		return m_props.get(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getAttributeNames()
	 */
	public Enumeration<Object> getAttributeNames() {
		return m_props.keys();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		return this.encoding;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
	 */
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		this.encoding = arg0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getContentLength()
	 */
	public int getContentLength() {
		return contentLen;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getContentType()
	 */
	public String getContentType() {
		if (request.getHeaders().containsKey("Content-Type".toLowerCase())) {
			return request.getHeaders().get("content-type").get(0);
		}
		return contentType;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getInputStream()
	 */
	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String arg0) {
		if (!setYet)
			getPostBody();
		return m_params.get(arg0).get(0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterNames()
	 */
	public Enumeration<String> getParameterNames() {
		if (!setYet)
			getPostBody();
		return Collections.enumeration(m_params.keySet());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String arg0) {
		if (!setYet)
			getPostBody();
		String[] ret = new String[m_params.get(arg0).size()];
		int i = 0;
		for (String str : m_params.get(arg0)) {
			ret[i++] = str;
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getParameterMap()
	 */
	public Map<String, ArrayList<String>> getParameterMap() {
		if (!setYet)
			getPostBody();
		return m_params;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getProtocol()
	 */
	public String getProtocol() {
		return request.getVersion();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getScheme()
	 */
	public String getScheme() {
		return "http";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getServerName()
	 */
	public String getServerName() {
		return request.getRequestSocket().getInetAddress().getHostName();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getServerPort()
	 */
	public int getServerPort() {
		return request.getServerSocket().getLocalPort();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getReader()
	 */
	public BufferedReader getReader() throws IOException {
		return request.getbR();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRemoteAddr()
	 */
	public String getRemoteAddr() {
		return request.getRequestSocket().getInetAddress().getHostAddress();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRemoteHost()
	 */
	public String getRemoteHost() {
		return request.getRequestSocket().getInetAddress().getHostName();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String arg0, Object arg1) {
		m_props.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String arg0) {
		m_props.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocale()
	 */
	public Locale getLocale() {
		return locale;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocales()
	 */
	public Enumeration<Locale> getLocales() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#isSecure()
	 */
	public boolean isSecure() {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
	 */
	public String getRealPath(String arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getRemotePort()
	 */
	public int getRemotePort() {
		return request.getRequestSocket().getPort();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocalName()
	 */
	public String getLocalName() {
		return request.getRequestSocket().getLocalAddress().getHostName();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocalAddr()
	 */
	public String getLocalAddr() {
		return request.getRequestSocket().getLocalAddress().getHostAddress();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getLocalPort()
	 */
	public int getLocalPort() {
		return request.getRequestSocket().getLocalPort();
	}
}
