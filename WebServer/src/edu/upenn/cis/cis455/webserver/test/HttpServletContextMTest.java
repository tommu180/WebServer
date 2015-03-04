package edu.upenn.cis.cis455.webserver.test;

import junit.framework.TestCase;
import edu.upenn.cis.cis455.webserver.api.HttpServletContextM;

public class HttpServletContextMTest extends TestCase {
	HttpServletContextM testObj = new HttpServletContextM("Chunxiao Mu");
	
	public void testContext() {
		assertEquals(testObj, testObj.getContext("a"));
		
		testObj.setAttribute("a", "b");
		assertEquals("b", testObj.getAttribute("a"));
		
		testObj.removeAttribute("a");
		assertEquals(null, testObj.getAttribute("a"));
		
		assertEquals("/home/cis455/workspace/HW1/web/index.html", testObj.getRealPath("/index.html"));
		assertEquals(null, testObj.getRealPath("/../../../index.html"));//TODO:
		
		assertEquals("Httpserver/0.1 Chunxiao", testObj.getServerInfo());
		
		assertEquals("Chunxiao Mu", testObj.getServletContextName());
	}
}
