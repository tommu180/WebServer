package edu.upenn.cis.cis455.webserver;

public class HttpResponse {
	
	public static final int SC_BAD_REQUEST = 400;
	public static final int SC_FORBIDDEN = 403;
	public static final int SC_INTERNAL_ERROR = 500;
	public static final int SC_MOVED = 301;
	public static final int sc_REDIRECT = 302;
	public static final int SC_NOT_FOUND = 404;
	public static final int SC_NOT_IMPLEMENTED = 501;
	public static final int SC_OK = 200;
	public static final int SC_UNAUTHORIZED = 401;

	/**
	 * Get the message of given status code
	 * @param sc
	 * @return status message in string
	 */
	public static String getStatusMessage(int sc) {
		switch (sc) {
		case SC_BAD_REQUEST:
			return "Bad Request";
		case SC_FORBIDDEN:
			return "Forbidden";
		case SC_INTERNAL_ERROR:
			return "Internal Error";
		case SC_MOVED:
			return "Moved";
		case SC_NOT_FOUND:
			return "Not Found";
		case SC_NOT_IMPLEMENTED:
			return "Not Implemented";
		case SC_OK:
			return "OK";
		case sc_REDIRECT:
			return "Redirect";
		case SC_UNAUTHORIZED:
			return "Unauthorized";
		default:
			return "Unknown Status Code " + sc;
		}
	}
}
