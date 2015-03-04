package edu.upenn.cis.cis455.webserver.test;

import java.net.URI;
import java.util.ArrayList;

import junit.framework.TestCase;
import edu.upenn.cis.cis455.webserver.HttpRequest;
import edu.upenn.cis.cis455.webserver.api.HttpServletRequestM;

public class HttpServletRequestMTest extends TestCase {
	HttpRequest part1 = new HttpRequest();
	String part2 = "/calculate/*";
	String part21 = "/calculate";
	String part3 = "30";
	
	HttpServletRequestM testObj = new HttpServletRequestM(part1, part2, part3);
	
	public void testRequest() throws Exception {
		testObj.setParameter("a", "b");
		testObj.setParameter("a", "c");
		testObj.setParameter("e", "d");
		part1.setQuery("num1=1&num2=2");
		part1.setMethod("Get");
		
		//test get/set Parameters
		assertEquals("b", testObj.getParameter("a"));
		String[] retStrings = {"b", "c"};
		String[] ret = testObj.getParameterValues("a");
		for (int i = 0; i < ret.length; i++) {
			assertTrue(ret[i].equals(retStrings[i]));
		}
		
		//test getCookies;
		assertEquals(null, testObj.getCookies());
		
		//test getDateHeader
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(" Fri, 31 Dec 1999 235959 GMT");
		part1.getHeaders().put("modified", temp);
		assertEquals(946684799000L, testObj.getDateHeader("Modified"));
		
		//test getHeader
		assertEquals(" Fri, 31 Dec 1999 235959 GMT", testObj.getHeader("modified"));
		temp = new ArrayList<String>();
		temp.add("6");
		part1.getHeaders().put("num", temp);
		assertEquals(6, testObj.getIntHeader("num"));
		
		//test getMethod
		assertEquals("GET", testObj.getMethod());
		
		//test getPathInfo
		URI uri = new URI("/calculate/a/b/index.html");
		part1.setUri(uri);
		assertEquals("/a/b/index.html", testObj.getPathInfo());
		
		//test getRequestURI
		assertEquals("/calculate/a/b/index.html", testObj.getRequestURI());
		
		//test getSession
//		temp.clear();
//		HttpServletSessionM sessionM = new HttpServletSessionM("30");
//		temp.add(sessionM.getId());
//		part1.getHeaders().put("sessionId", temp);
//		HttpServletRequestM testObj2 = new HttpServletRequestM(part1, part21, part3);
//		TestHarness servletContainer = new TestHarness("a");
//		servletContainer.getSessions().put(sessionM.getId(),sessionM);
		assertEquals(null, testObj.getRequestedSessionId());
	}
}
