package edu.upenn.cis.cis455.webserver.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis.cis455.webserver.HttpResponse;
import edu.upenn.cis.cis455.webserver.LogRecorder;

/**
 * @author Chunxiao Mu
 *
 */
public class HttpServletResponseM implements HttpServletResponse {
	
	private HashMap<String, ArrayList<String>> headersMap;
	private ServletResponseWriter buffer;
	private OutputStream out;
	
	private HttpServletRequestM request;
	
	private int sc_status;
	
	private ArrayList<Cookie> cookies;
	
	private String contentType;
	private String encoding;
	private boolean isCommitted;
	private int bufferSize;
	private int contentLength;
	private Locale locale;
	
	public HttpServletResponseM(OutputStream out, HttpServletRequestM request) {
		this.request = request;
		this.headersMap = new HashMap<String, ArrayList<String>>();
		this.out = out;
		this.buffer = new ServletResponseWriter(out, this);
		
		this.contentType = "text/html";
		this.encoding = "ISO-8859-1";
		this.cookies = new ArrayList<Cookie>();
		this.bufferSize = 0;
	}
	
	public void addCookie(Cookie arg0) {
		this.cookies.add(arg0);
	}

	public boolean containsHeader(String arg0) {
		return headersMap.containsKey(arg0);
	}

	public String encodeURL(String arg0) {
		return arg0;
	}

	public String encodeRedirectURL(String arg0) {
		return arg0;
	}

	public String encodeUrl(String arg0) {
		return encodeURL(arg0);
	}

	public String encodeRedirectUrl(String arg0) {
		return encodeRedirectURL(arg0);
	}

	public void sendError(int arg0, String arg1) throws IOException {
		if (isCommitted) {
            throw new IllegalStateException("Already commited");
        } else {
	        this.sc_status = arg0;
	        buffer.println("<HTML><BODY><P>" + arg0 + "-" + arg1 + "</P></BODY></HTML>");
	        buffer.flush();
        }
	}

	public void sendError(int arg0) throws IOException {
		if (isCommitted) {
            throw new IllegalStateException("Already committed");
        } else {
	        this.sc_status = arg0;
	        buffer.println("<HTML><BODY><P>" + arg0 + "-" + HttpResponse.getStatusMessage(arg0) + "</P></BODY></HTML>");
	        buffer.flush();
        }
	}

	public void sendRedirect(String arg0) throws IOException {
		if (isCommitted) {
			throw new IllegalStateException("Redirect already committed");
		} else {
			sc_status = 302;
			String redirUrl = "";
			if (arg0.charAt(0) == '/' || arg0.startsWith("http")) {
				redirUrl = arg0;
			} else {
				String uri = request.getRequestURI();
				if (!uri.endsWith("/")) {
					uri += "/";
				}
				redirUrl = uri + arg0;
			}
			setHeader("Location", redirUrl);
		}
	}

	public void setDateHeader(String arg0, long arg1) {
		ArrayList<String> replace = new ArrayList<String>();
		replace.add(new Date(arg1).toString());
		headersMap.put(arg0, replace);
	}

	public void addDateHeader(String arg0, long arg1) {
		ArrayList<String> headerVal = null;
		if (headersMap.containsKey(arg0)) {
			headerVal = headersMap.get(arg0);
		} else {
			headerVal = new ArrayList<String>();
		}
		headerVal.add(new Date(arg1).toString());
	}

	public void setHeader(String arg0, String arg1) {
		ArrayList<String> headerVal = new ArrayList<String>();
        headerVal.add(arg1);
        headersMap.put(arg0, headerVal);
	}

	public void addHeader(String arg0, String arg1) {
		ArrayList<String> headerVal;
        if (headersMap.containsKey(arg0)) {
            headerVal = headersMap.get(arg0);
        }
        else {
            headerVal = new ArrayList<String>();
        }
        headerVal.add(arg1);
        headersMap.put(arg0, headerVal);
	}

	public void setIntHeader(String arg0, int arg1) {
		ArrayList<String> headerVal = new ArrayList<String>();
        headerVal.add(String.valueOf(arg1));
        headersMap.put(arg0, headerVal);
	}

	public void addIntHeader(String arg0, int arg1) {
		ArrayList<String> headerVal;
        if (headersMap.containsKey(arg0)) {
            headerVal = headersMap.get(arg0);
        }
        else {
            headerVal = new ArrayList<String>();
        }
        headerVal.add(String.valueOf(arg1));
        headersMap.put(arg0, headerVal);
	}

	public void setStatus(int arg0) {
		this.sc_status = arg0;

	}

	public void setStatus(int arg0, String arg1) {

	}

	public String getCharacterEncoding() {
		return encoding;
	}

	public String getContentType() {
		return this.contentType;
	}
	
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	public PrintWriter getWriter() throws IOException {
		return this.buffer;
	}

	public void setCharacterEncoding(String arg0) {
		this.encoding = arg0;
	}

	public void setContentLength(int arg0) {
		this.contentLength = buffer.getContentLength();
	}

	public void setContentType(String arg0) {
		this.contentType = arg0;
	}

	public void setBufferSize(int arg0) {
		if (isCommitted) {
			throw new IllegalStateException("can't set buffer size as it is commited");
		}
		this.bufferSize = arg0;
	}

	public int getBufferSize() {
		return this.bufferSize;
	}

	public void flushBuffer() throws IOException {
		buffer.flush();
		buffer.close();
		out.close();
	}

	public void resetBuffer() throws IllegalStateException {
		if (isCommitted) {
			throw new IllegalStateException();
		} else {
			buffer = new ServletResponseWriter(out, this);
		}
	}

	public boolean isCommitted() {
		return isCommitted;
	}
	
	/**
	 * Flush the header first
	 */
	public void flushHeader() {
		//resetBuffer();
		if (sc_status == 0) {
			setStatus(200);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        StringBuffer headerVal = new StringBuffer();
        headerVal.append(request.getProtocol() + " " + sc_status + " " + HttpResponse.getStatusMessage(sc_status) + "\r\n");
//        System.out.println("1st line:" + headerVal.toString());
        headerVal.append("Date: " + dateFormat.format(new Date()) + "\r\n");
        for (String headerName : headersMap.keySet()) {
            headerVal.append(headerName + ": " + headersMap.get(headerName).get(0) + "\r\n");
        }
        //System.out.println("IF request.hasSession? " + request.hasSession());
        if (request.hasSession() && request.getSession().isNew()) {
        	ServletEngine.deleteInvalidatedSession();
        	HttpServletSessionM sessionM = (HttpServletSessionM) request.getSession(false);
        	Cookie sessionCookie = new Cookie("sessionId", sessionM.getId());
        	
        	//HttpSession calExpireTime = request.getSession(false);
        	int maxAge = (int) ((sessionM.getLastAccessedTime() - new Date().getTime())/1000 + sessionM.getMaxInactiveInterval());
        	//maxAge = maxAge/1000;
        	sessionCookie.setMaxAge(maxAge*60);
//        	if (!sessionM.isValid()) {
//        		System.out.println("Here the session is invaid");
//        		sessionCookie.setMaxAge(0);
//        	}
        	headerVal.append("Set-Cookie: ").append(getCookieInfo(sessionCookie)).append("\r\n");
        }
        if (cookies.size() > 0) {
        	for (Cookie cur : cookies) {
        		headerVal.append("Set-Cookie: ").append(getCookieInfo(cur)).append("\r\n");
        	}
        }
        
        headerVal.append("Content-Length: " + buffer.getContentLength() + "\r\n");
        //System.out.println("buffer.getContentLength:" + buffer.getContentLength());
        headerVal.append("Content-Type: " + contentType + "\r\n");
        headerVal.append("Content-Encoding: " + encoding + "\r\n");
        headerVal.append("Connection: close\r\n\r\n");
        //System.out.println(headerVal.toString());
        try {
			out.write(headerVal.toString().getBytes());
			out.flush();
			isCommitted = true;
			//System.out.println("flushed ");
		} catch (IOException e) {
			LogRecorder.addErrorMessage(e, "cannot write or flush");
			e.printStackTrace();
		}
	}
	
	/**
	 * Set reponse header when cookie is passed
	 * @param cookie
	 * @return
	 */
	public String getCookieInfo(Cookie cookie) {
		StringBuilder sb = new StringBuilder();
		sb.append(cookie.getName()).append("=").append(cookie.getValue());
		if (cookie.getComment() != null)
            sb.append("; Comment=").append(cookie.getComment());
        if (cookie.getDomain() != null)
            sb.append("; Domain=").append(cookie.getDomain());
        if (cookie.getPath() != null)
            sb.append("; Path=").append(cookie.getPath());
        if (cookie.getMaxAge() >= 0) {
        	long expireDate = cookie.getMaxAge() * 1000;
        	expireDate += Calendar.getInstance().getTimeInMillis();
    	    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    	    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    sb.append("; Expires=").append(dateFormat.format(expireDate));
        }
        if (cookie.getSecure())
        	sb.append("; Secure");
        return sb.toString();
	}

	public void reset() throws IllegalStateException {
		if (isCommitted) {
			throw new IllegalStateException();
		} else {
			this.headersMap.clear();
			this.buffer = new ServletResponseWriter(out, this);
		}
	}

	public void setLocale(Locale arg0) {
		this.locale = arg0;
	}

	public Locale getLocale() {
		return locale;
	}
}
