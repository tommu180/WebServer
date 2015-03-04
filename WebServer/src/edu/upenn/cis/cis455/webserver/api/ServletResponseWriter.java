package edu.upenn.cis.cis455.webserver.api;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ServletResponseWriter extends PrintWriter {
	private HttpServletResponseM response;
	private int bodyLength;

	public ServletResponseWriter(OutputStream out, HttpServletResponseM response) {
		super(out);
		this.response = response;
		this.bodyLength = 0;
	}
	
	/**
	 * flush header, then body
	 */
	@Override
    public void flush() {
        if (!response.isCommitted()) {
            response.flushHeader();
        }
        super.flush();
    }

    @Override
    public void write(String s) {
        write(s, 0, s.length());
    }
    
    @Override
    public void write(String s, int off, int len) {
        super.write(s, off, len);
        bodyLength += len + 1;//TODO:
    }
    
	public int getContentLength() {
		return bodyLength;
	}
}
