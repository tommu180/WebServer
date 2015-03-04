package edu.upenn.cis.cis455.webserver.test;

import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;
import edu.upenn.cis.cis455.webserver.HttpRequest;
import edu.upenn.cis.cis455.webserver.api.HttpServletRequestM;
import edu.upenn.cis.cis455.webserver.api.HttpServletResponseM;
import edu.upenn.cis.cis455.webserver.api.ServletResponseWriter;

public class ServletResponseWriterTest extends TestCase {
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
	
	HttpServletResponseM part4 = new HttpServletResponseM(outputStream, requestM);
	ServletResponseWriter testObj = new ServletResponseWriter(outputStream, part4);
	
	public void testWriter() {
		testObj.write("aaaaa");
		assertEquals(6, testObj.getContentLength());
	}
}
