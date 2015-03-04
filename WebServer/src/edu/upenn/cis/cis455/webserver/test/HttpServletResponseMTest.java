package edu.upenn.cis.cis455.webserver.test;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;
import edu.upenn.cis.cis455.webserver.HttpRequest;
import edu.upenn.cis.cis455.webserver.api.HttpServletRequestM;
import edu.upenn.cis.cis455.webserver.api.HttpServletResponseM;

public class HttpServletResponseMTest extends TestCase {
	HttpRequest part1 = new HttpRequest();
	String part2 = "/calculate/*";
	String part21 = "/calculate";
	String part3 = "30";
	
	HttpServletRequestM requestM = new HttpServletRequestM(part1, part2, part3);
	
	OutputStream outputStream = new OutputStream() {
		
		@Override
		public void write(int b) throws IOException {
			System.out.println(b);
		}
	};
	
	HttpServletResponseM testObj = new HttpServletResponseM(outputStream, requestM);
	
	public void testResponse() throws IOException {
		assertEquals(0, testObj.getBufferSize());
		assertEquals("ISO-8859-1", testObj.getCharacterEncoding());
		assertEquals("text/html", testObj.getContentType());
		assertEquals("aa=bb", testObj.getCookieInfo(new Cookie("aa", "bb")));
		assertEquals(null, testObj.getOutputStream());
	}
}
