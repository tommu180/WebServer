package edu.upenn.cis.cis455.webserver.test;

import junit.framework.TestCase;
import edu.upenn.cis.cis455.webserver.api.HttpServletConfig;
import edu.upenn.cis.cis455.webserver.api.HttpServletContextM;

public class HttpServletConfigTest extends TestCase {
	HttpServletContextM part1 = new HttpServletContextM("chunxmu");
	HttpServletConfig testObj = new HttpServletConfig("full house", part1);
	
	
	public void testConfig() {
		testObj.setInitParam("a", "b");
		assertEquals("b", testObj.getInitParameter("a"));
		assertEquals("full house", testObj.getServletName());
		assertEquals(part1, testObj.getServletContext());
	}
}
