package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet1 extends HttpServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    PrintWriter out = response.getWriter();
	    out.println("<html><head><title>TestRequestInformation</title></head><body>");
	    out.println("RequestURL:"+request.getRequestURL()+"<br>");
	    if (!request.getRequestURL().toString().equals("http://localhost:8080/Request")) {
	    	out.println("<br>" + "getURL incorrect" + "<br>");
	    }
	    out.println("RequestURI:"+request.getRequestURI()+"<br>");
	    if (!request.getRequestURI().toString().equals("/Request")) {
	    	out.println("<br>" + "getURI incorrect" + "<br>");
	    }
	    out.println("PathInfo:"+request.getPathInfo()+"<br>");
	    if (request.getPathInfo() != null) {
	    	out.println("<br>" + "getPathInfo incorrect" + "<br>");
	    }
	    out.println("Context path:"+request.getContextPath()+"<br>");
	    if (!request.getContextPath().toString().equals("")) {
	    	out.println("<br>" + "getContextPath incorrect" + "<br>");
	    }
	    out.println("LocalAddress:"+ request.getLocalAddr()+"<br>");
	    out.println("LocalName:"+ request.getLocalName()+"<br>");
	    out.println("Header:"+request.getHeader("host")+"<br>");
	    out.println("<P>Continue to <A HREF=\"Response\">test response</A>.</P>");
	    out.println("</body></html>");
	}
}
