package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletException;

import edu.upenn.cis.cis455.webserver.api.ServletEngine;

/**
 * @author cis455
 *
 */
/**
 * @author cis455
 *
 */
public class WorkerThreadImpl extends Thread {
	private static boolean isShutdown = false;
	private BlockingQueueImpl<Socket> socketQueue;
	private Socket clientSocket;
	private ServletEngine appServe;
	
	private BufferedReader in;
	private OutputStream out;
	
	private String rootDir;
	
	private HttpRequest request;
	private String responseString;
	private String continue100;
	private byte[] responseData;
	
	private boolean onlyHeader;
	private boolean has100;
	private boolean servletRequest;
	private String resPattern;
	
	public WorkerThreadImpl(BlockingQueueImpl<Socket> reqQueue, String rootDir, ServletEngine appServer1) {
		this.socketQueue = reqQueue;
		this.rootDir = rootDir;
		this.appServe = appServer1;
		//TODO
	}
	
	public void run() {
		while(!isShutdown) {
			request = new HttpRequest();
			try {
				clientSocket = socketQueue.take();
			} catch(InterruptedException e) {
				break;
			}
			requestParser();
			if (servletRequest) {
				//System.out.println("ServeletRequest Here");
				appServe.setRequest(request);
				try {
					appServe.handleRequest(resPattern, clientSocket.getOutputStream());
					continue;
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					LogRecorder.addErrorMessage(e, "Sevlet exception while handling servlet request");
					e.printStackTrace();
				} catch (IOException e) {
					LogRecorder.addErrorMessage(e, "client get outputstream problem");
					e.printStackTrace();
				}
			} else {
				handleRequest();
			}
			sendResponse(onlyHeader);
		}
	}
	
	/**
	 * Parse the request
	 */
	private void requestParser() {
		try{
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String line;
			boolean isInitLine = true;
	        while ((line = in.readLine()) != null && line.length() != 0) {
	        	if (isInitLine) {
	        		String[] tokens = line.split("\\s+");
	        		request.setMethod(tokens[0].toUpperCase());
	        		String path = tokens[1];
	        		request.setPath(path);
	        		request.setVersion(tokens[2]);
	        		try {
	        			request.setUri(new URI(path));
	    	            //System.out.println("uri.path:" + request.getUri().getPath());
        	        }
        	        catch (URISyntaxException urise) {
        	        	request.setUri(null);
        	        	LogRecorder.addErrorMessage(urise, "Cannot create uri given path");
        	        }
	        		isInitLine = false;
	        	} else {
	        		//System.out.println("!?"+line);
		        	request.getHeaderStrings().add(line);
	        	}
	        }
	        
	        servletRequest = isServletRequest();
	        
	        //System.out.println("path:" + request.getUri().getPath());
	        //System.out.println("query:" + request.getUri().getQuery());
	        request.setQuery(request.getUri().getQuery());
	        request.setHeaders();
	        request.setbR(in);
	        request.setRequestSocket(clientSocket);
		} catch (IOException e) {
			LogRecorder.addErrorMessage(e, "Socket read failure");
			e.printStackTrace();
		}
	}

	/**
	 * Check weather it is a servlet request
	 * @return true or false
	 */
	private boolean isServletRequest() {
		String suspiciousPath = request.getPath();
		
		int matchType = 0;
		
		HashMap<String, String> urlPatternMap = appServe.getUrlPattern();
		
		Set<String> urlPatterns = urlPatternMap.keySet();
		
		String servletClass = "";
		
		//System.out.println("suspicious:" + suspiciousPath);
		for (String pattern : urlPatterns) {
			pattern = "/" + pattern;
			//System.out.println("pattern:" + pattern);
			if (pattern.length() != 1 && pattern.charAt(1) != '*') {
				if (pattern.endsWith("/*") && suspiciousPath.contains(pattern.substring(1, pattern.length() - 2))) {
					matchType = 3;
					servletClass = urlPatternMap.get(pattern.substring(1));
					//System.out.println("pattern:" + pattern);
					//System.out.println("servletClass:" + servletClass);
					this.resPattern = pattern.substring(1);
				} else if (suspiciousPath.endsWith(pattern.substring(1))) {
					matchType = 2;
					servletClass = urlPatternMap.get(pattern.substring(1));
					//System.out.println("pattern:" + pattern);
					//System.out.println("servletClass:" + servletClass);
					this.resPattern = pattern.substring(1);
				}
			} else if (pattern.length() != 2){
				if (suspiciousPath.endsWith(pattern.substring(2, pattern.length()))) {
					if (matchType < 1) {
						matchType = 1;
						servletClass = urlPatternMap.get(pattern.substring(1));
						//System.out.println("pattern:" + pattern);
						//System.out.println("servletClass:" + servletClass);
						this.resPattern = pattern.substring(1);
					}
				}
			} else {
				buildErrorResponse(HttpResponse.SC_BAD_REQUEST);
			}
		}
		if (servletClass == null || servletClass.equals(""))
			return false;
		return true;
	}
	
	/**
	 * Handle request;
	 */
	private void handleRequest() {
//		String method = request.getMethod().toUpperCase();
		String method = request.getMethod();
		onlyHeader = false;
		has100 = false;
		try {
			if (request == null || method == null || request.getVersion().equals("HTTP/1.1") && !request.hasHost()) {
				System.out.println("here");
				buildErrorResponse(HttpResponse.SC_BAD_REQUEST);
			} else if (method.equals("GET")) {
				if (request.getHeaders().containsKey("expect") && !request.getVersion().equals("HTTP/1.0")) {
					continue100 = "HTTP/1.1 100 Continue\r\n\r\n";
					has100 = true;
				}
				handleGetMethod();
			} else if (method.equals("HEAD")) {
				handleHeadMethod();
			} else {
				buildErrorResponse(HttpResponse.SC_NOT_IMPLEMENTED);
			}
		} catch (UnknownHostException e) {
			System.out.println("unknown host");// TODO: handle exception
			LogRecorder.addErrorMessage(e, "unknown host");
		} catch (IOException e){
			System.out.println("Cannot get canonical path");
			LogRecorder.addErrorMessage(e, "cannot get canonical path");
		} 
	}
	
	/**
	 * Handle GET Request
	 * @throws IOException
	 */
	private void handleGetMethod() throws IOException {
		String path = request.getPath();
		//System.out.println("Handle GET Method path:" + path);
		if (path.equals("/control")) {
			buildControlResponse(ThreadPoolImpl.getStates());
		} else if (path.equals("/shutdown")) {
			buildshutDownResponse();
			HttpServer.shutdownServer();
		} else if (request.getQuery() != null) {
			System.out.println("with query");
			
		} else {
			path = new File(rootDir).getCanonicalPath() + path;
			path = new File(path).getCanonicalPath();
			//System.out.println("path:" + path);
			//System.out.println("rootDir" + rootDir);
			if (!path.contains(this.rootDir)) {
				buildErrorResponse(HttpResponse.SC_FORBIDDEN);
			} else {
				getData(path, true);
			}
		}
	}
	
	/**
	 * handle HEAD request
	 * @throws IOException
	 */
	private void handleHeadMethod() throws IOException {
		onlyHeader = true;
		String path = request.getPath();
		path = new File(rootDir).getCanonicalPath() + path;
		getData(path, false);
	}
	
	/**
	 * Build error response based on the status code
	 * @param sc
	 */
	private void buildErrorResponse(int sc) {
		String errorContent = "<HTML><BODY><P>HTTP Error " + sc + " - " + HttpResponse.getStatusMessage(sc) + "</P></BODY></HTML>\r\n";
		StringBuilder sb = new StringBuilder();
		sb.append(request.getVersion()+ " " + String.valueOf(sc) + " " + HttpResponse.getStatusMessage(sc)+"\r\n");
		sb.append("Date: " + getServerTime() + "\r\n");
		sb.append("Content-Type:" + "text/html" + "\r\n");
		sb.append("Content-Length:" + String.valueOf(errorContent.getBytes().length) + "\r\n");
		sb.append("Connection: close\r\n\r\n");
		this.responseData = errorContent.getBytes();
		responseString = sb.toString();
	}
	
	/**
	 * build shut down response
	 */
	private void buildshutDownResponse() {
		String shutdownMessage = "<HTML><BODY><P>Server shut down " + "</P></BODY></HTML>\r\n";
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 " + "200" + " " + "OK"+"\r\n");
		sb.append("Date: " + getServerTime() + "\r\n");
		sb.append("Content-Type:" + "text/html" + "\r\n");
		sb.append("Content-Length:" + String.valueOf(shutdownMessage.getBytes().length) + "\r\n");
		sb.append("Connection: close\r\n\r\n");
		this.responseData = shutdownMessage.getBytes();
		responseString = sb.toString();
	}
	
	/**
	 * Build control response based on the states of threads
	 * @param states
	 */
	private void buildControlResponse(ArrayList<String> states) {
		String threadStates = "<P>Chunxiao Mu</P><P>chunxmu</P>";
		for (int i = 0; i < states.size(); i++) {
			threadStates += "<P>" + states.get(i) + "</P>";
			threadStates += "\r\n";
		}
		threadStates += LogRecorder.attachLog();
		String data = "<HTML><BODY><a href='shutdown'>shutdown</a>" + threadStates + "</BODY></HTML>\r\n";
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 " + "200" + " " + "OK"+"\r\n");
		sb.append("Date: " + getServerTime() + "\r\n");
		sb.append("Content-Type:" + "text/html" + "\r\n");
		sb.append("Content-Length:" + String.valueOf(data.getBytes().length) + "\r\n");
		sb.append("Connection: close\r\n\r\n");
		this.responseData = data.getBytes();
		responseString = sb.toString();
	}
	
	/**
	 * Get folder response if the request is a folder
	 * @param path
	 */
	private void getFolder(String path) {
		File folder = new File(path);
		try{
			folder.getAbsoluteFile();
		} catch (SecurityException se){
			buildErrorResponse(HttpResponse.SC_UNAUTHORIZED);
			return;
		}
		File[] list = folder.listFiles();
		if (list == null){
			//System.out.println("The folder does not exist.");
			buildFolderResponse("The folder does not exit");
		} else if (list.length == 0){
			//System.out.println("The folder is empty.");
			buildFolderResponse("The folder is empty.");
		} else {
			String nameList = "";
			for (int i = 0; i < list.length; i++) {
				nameList += "<P>"+ list[i].getName() + "</P>";
				nameList += "\n";
			}
			buildFolderResponse(nameList);
		}
	}
	
	
	/**
	 * Build response when requested is a folder
	 * @param str
	 */
	private void buildFolderResponse(String str) {
		String folderView = "<HTML><BODY><P>Folder: \n" + str + "</P></BODY></HTML>\r\n";
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 " + "200" + " " + "OK"+"\r\n");
		sb.append("Date: " + getServerTime() + "\r\n");
		sb.append("Content-Type:" + "text/html" + "\r\n");
		sb.append("Content-Length:" + String.valueOf(folderView.getBytes().length) + "\r\n");
		sb.append("Connection: close\r\n\r\n");
		this.responseData = folderView.getBytes();
		responseString = sb.toString();
	}
	
	/**
	 * Get requested data
	 * @param path
	 * @param needData
	 */
	private void getData(String path, boolean needData) {
		FileInputStream fileReader = null;
		String[] tokens = null;
		tokens = path.split("[.]");
//		for (int i = 0; i < tokens.length; i++) {
//			System.out.println("tokens:" + tokens[i]);
//		}
		if (tokens.length < 2) {
			getFolder(path);
			return;
		}
		
		Date time = null;
		boolean ifMod = false;
		boolean ifUMod = false;
		if (request.getHeaders().containsKey("If-Modified-Since".toLowerCase())) {
			String ifModified = request.getHeaders().get("If-Modified-Since".toLowerCase()).get(0);
			ifMod = true;
			time = parseTime(time, ifModified);
			if (time != null) {
				System.out.println("If-Modified-Since:"+time);
			}
		} else if (request.getHeaders().containsKey("If-Unmodified-Since".toLowerCase())) {
			ifUMod = true;
			String ifUnModified = request.getHeaders().get("If-Unmodified-Since".toLowerCase()).get(0);
			time = parseTime(time, ifUnModified);
			if (time != null) {
				System.out.println("If-Modified-Since:"+time);
			}
		}
		
		if (time != null && time.after(Calendar.getInstance().getTime())) {
			time = null;
			ifMod = false;
			ifUMod = false;
		}
		
		try {
			if (time != null) {
				File resouce = new File(path);
				//System.out.println("resource:" + resouce.lastModified());
				//System.out.println("time:" + time.getTime());
				if (ifMod && time.getTime() >= resouce.lastModified()) {
					StringBuilder sb = new StringBuilder();
					sb.append("HTTP/1.1 " + "304" + " " + "Not Modified"+"\r\n");
					sb.append("Date: " + getServerTime() + "\r\n");
					sb.append("\r\n");
					this.responseString = sb.toString();
					onlyHeader = true;
					return;
				}
				if (ifUMod && time.getTime() <= resouce.lastModified()) {
					StringBuilder sb = new StringBuilder();
					sb.append("HTTP/1.1 " + "412" + " " + "Precondition failed"+"\r\n");
					sb.append("\r\n");
					this.responseString = sb.toString();
					onlyHeader = true;
					return;
				}
			}
			
			fileReader = new FileInputStream(path);
			StringBuilder sb = new StringBuilder();
			sb.append("HTTP/1.1 " + "200" + " " + "OK"+"\r\n");
			sb.append("Date: " + getServerTime() + "\r\n");
			if (MIMETypeMap.typeMap.containsKey(tokens[1])) {
				sb.append("Content-Type:" + MIMETypeMap.typeMap.get(tokens[1]) + "\r\n");
			} else {
				sb.append("Content-Type:" + "application/octet-stream" + "\r\n");
			}
			sb.append("Content-Length:" + String.valueOf(fileReader.available()) + "\r\n");
			sb.append("Connection: close\r\n\r\n");
			responseData =  new byte[fileReader.available()];
			
			if (needData) {
				fileReader.read(responseData);
			}
			responseString = sb.toString();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find file");
			buildErrorResponse(HttpResponse.SC_NOT_FOUND);
		} catch (IOException e) {
			System.out.println("Cannot read resources");
		} finally {
			try {
				if (fileReader != null)
					fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Parse time from header
	 * @param time
	 * @param ifModified
	 * @return
	 */
	private Date parseTime(Date time, String ifModified) {
		String[] timeFormat;
		timeFormat = ifModified.split("\\s+");
		try {
			switch (timeFormat.length) {
			case 7:
				SimpleDateFormat formatter1 = new SimpleDateFormat(" E, dd MMM yyyy HHmmss z");
				time = formatter1.parse(ifModified);
				break;
			case 5:
				SimpleDateFormat formatter2 = new SimpleDateFormat(" E, dd-MMM-yy HHmmss z");
				time = formatter2.parse(ifModified);
				break;
			case 6:
				SimpleDateFormat formatter3 = new SimpleDateFormat(" E MMM dd HHmmss yyyy");
				time = formatter3.parse(ifModified);
				break;
			default:
				break;
			}
		} catch	(ParseException e) {
			time = null;
			e.printStackTrace();
		}
		return time;
	}
	
	/**
	 * Send response to client
	 * @param isHead
	 */
	private void sendResponse(boolean isHead) {
		try {
			out = clientSocket.getOutputStream();
			if (has100) {
				responseString = continue100 + responseString;
			}
			out.write(responseString.getBytes());
			//out2.flush();
			if (!isHead) {
				out.write(responseData);
				out.flush();
			}
            clientSocket.close();
		} catch (IOException e) {
			System.out.println("Socket send response failed");
			LogRecorder.addErrorMessage(e, "send response failed");
		}
	}
	
	/**
	 * Get server time
	 * @return
	 */
	private String getServerTime() {
	    Calendar calendar = Calendar.getInstance();
	    SimpleDateFormat dateFormat = new SimpleDateFormat(
	        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    return dateFormat.format(calendar.getTime());
	}
	
	public String getUrl() {
		return request.retPath();
	}
	
	public void shutDownThread() {
		this.isShutdown = true;
	}
}
