package edu.upenn.cis.cis455.webserver.test;

import java.io.File;

import junit.framework.TestCase;
import edu.upenn.cis.cis455.webserver.api.HttpServletSessionM;
import edu.upenn.cis.cis455.webserver.api.ServletEngine;

public class ServletEngineTest extends TestCase {
	
	public void test() throws Exception {
		String addString = new File(".").getCanonicalPath() + "/conf/web.xml";
		System.out.println(addString);
		ServletEngine testObj = new ServletEngine(addString);
		assertEquals(0, testObj.getSessions().size());
		testObj.getSessions().put("111111111", new HttpServletSessionM("111111111"));
		assertEquals(true, testObj.getUrlPattern().containsKey("calculate/*"));
	}
}
