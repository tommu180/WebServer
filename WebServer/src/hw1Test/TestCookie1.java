package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestCookie1 extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie c = new Cookie("cookie", "8080");
		c.setMaxAge(3600);
		response.addCookie(c);
		//response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Cookie Servlet 1</TITLE></HEAD><BODY>");
		out.println("<P>Added cookie=8080 to response.</P>");
		out.println("<P><A HREF=\"SessionTestA\">Add a session in response</A>.</P>");
		out.println("<P><A HREF=\"CookieTestB\">Remove cookie in response</A>.</P>");
		out.println("</BODY></HTML>");
	}
}