package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.Cookie;

/**
 * @author Chunxiao Mu
 * Stores every information of a request
 */
public class HttpRequest {
	private String method, version, path;
    private URI uri;
    private int contentLen;
    private String query;
    private ArrayList<String> headerStrings;
    private HashMap<String, ArrayList<String>> headers;
    private ArrayList<Cookie> cookies;
    private Socket requestSocket;
    private ServerSocket serverSocket;
    private BufferedReader bR;
    
    public HttpRequest() {
    	super();
    	this.method = null;
    	this.version = null;
    	this.uri = null;
    	this.query = null;
    	this.headerStrings = new ArrayList<String>();
    	this.headers = new HashMap<String, ArrayList<String>>();
    	this.serverSocket = HttpServer.getServerSocket();
    }
    
    public String getVersion() {
		return version;
	}

    
	public void setVersion(String version) {
		this.version = version;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ArrayList<String> getHeaderStrings() {
		return headerStrings;
	}

	public void setHeaderStrings(ArrayList<String> headerStrings) {
		this.headerStrings = headerStrings;
	}

	public HashMap<String, ArrayList<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap<String, ArrayList<String>> headers) {
		this.headers = headers;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setContentLen(int contentLen) {
		this.contentLen = contentLen;
	}

	public String getMethod() {
    	return this.method;
    }
    
	public String retPath() {
		return this.path;
	}
	
    public String getPath() {
    	return this.uri.getPath();
    }
    
    public int getContentLen() {
    	return this.contentLen;
    }
    
    public String getHost() {
    	if (headers.containsKey("Host"))
    		return headers.get("Host").get(0);
    	return null;
    }
    
    /**
     * Check if has host
     * @return
     * @throws UnknownHostException
     */
    public boolean hasHost() throws UnknownHostException {
    	if (headers.containsKey("host")){
//    		String hostVal = headers.get("host").get(0);
////    		System.out.println(headers.get("host").get(0));
////    		System.out.println(InetAddress.getByName("localhost"));
//    		if (hostVal.contains("localhost") || hostVal.contains("127.0.0.1")) {
//    			return true;
//    		}
    		return true;
    	}
		return false;
    }
    
    /**
     * Using headerComponent string array to generate header key value pair.
     */
    public void setHeaders() {
    	String[] headerComponent;
    	ArrayList<String> headersVals;
        for (String line : headerStrings) {
           headerComponent = line.split(":");//TODO:deal with multiple tab and spaces.
           //System.out.println("headerCompnent0:" + headerComponent[0] + "-" + headerComponent[1]);
           if (headerComponent.length == 2) {
        	   if (headerComponent[0].equals("Cookie")) {
        		   this.cookies = new ArrayList<Cookie>();
        		   //System.out.println("!!!!!!!!Cookie:"+headerComponent[1]);
        		   String[] cookiePairs = headerComponent[1].split(";");
        		   ArrayList<String> nameValPair = null;
        		   if (headers.containsKey("cookie")) {
        			   nameValPair = headers.get("cookie");
        		   } else {
        			   nameValPair = new ArrayList<String>();
        		   }
        		   for (String s : cookiePairs) {
        			   nameValPair.add(s);
        			   //System.out.println("This is Cookie String:" + s);
        			   String[] names = s.split("=");//TODO;
        			   if (names[0].equals(" sessionId")) {
        				   ArrayList<String> sessionId = new ArrayList<String>();
        				   sessionId.add(names[1]);
        				   headers.put("sessionId", sessionId);
        			   } else {
	        			   Cookie newCookie = new Cookie(names[0].substring(1), names[1]);
	        			   this.cookies.add(newCookie);
        			   }
        		   }
        		   headers.put(headerComponent[0].toLowerCase(), nameValPair);
        		   continue;
        	   }
           } else if (headerComponent.length > 2) {
        	   String content = "";
        	   for (int i = 1; i < headerComponent.length; i++)
        		   content += headerComponent[i];
        	   headerComponent[1] = content;
           }
           if (!headers.containsKey(headerComponent[0].toLowerCase())) {
        	   headersVals = new ArrayList<String>();
        	   headersVals.add(headerComponent[1]);
        	   headers.put(headerComponent[0].toLowerCase(), headersVals);
           } else {
        	   headers.get(headerComponent[0]).add(headerComponent[1]);
           }
        }
    }

    
	/**
	 * @return a list of cookie
	 */
	public ArrayList<Cookie> getCookies() {
		return cookies;
	}

	public Socket getRequestSocket() {
		return requestSocket;
	}

	public void setRequestSocket(Socket requestSocket) {
		this.requestSocket = requestSocket;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public BufferedReader getbR() {
		return bR;
	}

	public void setbR(BufferedReader bR) {
		this.bR = bR;
	}
}
