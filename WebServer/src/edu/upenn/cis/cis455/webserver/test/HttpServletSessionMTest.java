package edu.upenn.cis.cis455.webserver.test;

import junit.framework.TestCase;
import edu.upenn.cis.cis455.webserver.api.HttpServletSessionM;

public class HttpServletSessionMTest extends TestCase {
	HttpServletSessionM session = new HttpServletSessionM("30");
	
	public void testSession() {
		assertTrue(session.isNew());
		assertTrue(session.getLastAccessedTime() == session.getCreationTime());
		session.setMaxInactiveInterval(45);
		assertEquals(45, session.getMaxInactiveInterval());
		assertEquals(true, session.isValid());
		session.invalidate();
		assertEquals(false, session.isValid());
	}
}
