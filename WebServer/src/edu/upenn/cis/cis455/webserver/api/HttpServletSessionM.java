package edu.upenn.cis.cis455.webserver.api;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author Chunxiao Mu
 */
public class HttpServletSessionM implements HttpSession {
	private UUID id;
	private Date createdAt;
	private Date lastAccesse;
	private int timeInterval;
		
	private boolean isNew;

	
	private Properties m_props = new Properties();
	private boolean m_valid = true;
	
	public HttpServletSessionM(String timeOut) {
    	createdAt = new Date();
    	lastAccesse = createdAt;
    	id = UUID.randomUUID();
    	isNew = true;
    	
        ServletEngine.getSessions().put(id.toString(), this);
        this.setMaxInactiveInterval(Integer.parseInt(timeOut));
	}
	
	public void setLastAccess(Date date) {
		this.lastAccesse = date;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getCreationTime()
	 */
	public long getCreationTime() {
		if (isValid()) {
			return this.createdAt.getTime();
		} else {
			throw new IllegalStateException("session id is invalid");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getId()
	 */
	public String getId() {
		if (isValid()) {
			return id.toString();
		} else {
			throw new IllegalStateException("session id is invalid");
		}		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getLastAccessedTime()
	 */
	public long getLastAccessedTime() {
		if (isValid()) {
			return lastAccesse.getTime();
		} else {
			throw new IllegalStateException("session id is invalid");
		}		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getServletContext()
	 */
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
	 */
	public void setMaxInactiveInterval(int arg0) {
		this.timeInterval = arg0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
	 */
	public int getMaxInactiveInterval() {
		return this.timeInterval;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getSessionContext()
	 */
	public HttpSessionContext getSessionContext() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) {
		if (isValid()) {
			return m_props.get(arg0);
		} else {
			throw new IllegalStateException("session id is invalid");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
	 */
	public Object getValue(String arg0) {
		return getAttribute(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getAttributeNames()
	 */
	public Enumeration<Object> getAttributeNames() {
		if (isValid()) {
			return m_props.keys();
		} else {
			throw new IllegalStateException("session id is invalid");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getValueNames()
	 */
	public String[] getValueNames() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String arg0, Object arg1) {
		if (isValid()) {
			m_props.put(arg0, arg1);
		} else {
			throw new IllegalStateException("session id is invalid");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
	 */
	public void putValue(String arg0, Object arg1) {
		setAttribute(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String arg0) {
		if (isValid()) {
			m_props.remove(arg0);
		} else {
			throw new IllegalStateException("session id is invalid");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
	 */
	public void removeValue(String arg0) {
		removeAttribute(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#invalidate()
	 */
	public void invalidate() {
		m_valid = false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#isNew()
	 */
	public boolean isNew() {
		if (isValid()) {
			return this.isNew;
		} else {
			throw new IllegalStateException("session id is invalid");
		}
	}

	public boolean isValid() {
		return m_valid;
	}
	
}
