package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestCookie3 extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				if (cookies[i].getName().equals("cookie")) {
					cookie = cookies[i];
				}
			}
		}
		PrintWriter out = response.getWriter();
		out.println("<HTML><BODY>");
		if (cookie == null) {
			out.println("<P>Cookie removed</P>");
		} else {
			out.println("<P>Still in request Header</P>");
		}
		out.println("<P>Go back to <A HREF=\"Redirect\">test cookie and session</A>.</P>");
		out.println("</BODY></HTML>");
	}
}
