package edu.upenn.cis.cis455.webserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chunxiao Mu
 * Get MIME type with suffix.
 */
public class MIMETypeMap {
	public static final String html = "text/html";
	public static final String gif = "image/gif";
	public static final String jpg = "image/jpeg";
	public static final String pdf = "application/pdf";
	public static final String txt = "text/plain";
	public static final String png = "image/png";
	public static final String ico = "image/x-icon";
	
	
	public static final Map<String, String> typeMap;
	static {
        HashMap<String, String> cpMap = new HashMap<String, String>();
        cpMap.put("html", html);
        cpMap.put("gif", gif);
        cpMap.put("jpg", jpg);
        cpMap.put("jpeg", jpg);
        cpMap.put("pdf", pdf);
        cpMap.put("txt", txt);
        cpMap.put("png", png);
        cpMap.put("ico", ico);
        typeMap = Collections.unmodifiableMap(cpMap);
    }
}
