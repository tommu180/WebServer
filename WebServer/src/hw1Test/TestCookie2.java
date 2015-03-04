package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestCookie2 extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals("cookie")) {
				cookie = cookies[i];
			}
		}
		PrintWriter out = response.getWriter();
		out.println("<HTML><BODY>");
		if (cookie == null) {
			out.println("<P>'cookie' is null.</P>");
		} else {
			out.println("<P>Successfully get cookie</P>");
			out.println("<P>Cookie value:" + cookie.getValue() + "</P>");
			out.println("<P>Deleted cookie 8080 in response.</P>");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
		out.println("<P>Continue to <A HREF=\"CookieTestC\">check results of removing cookie</A>.</P>");
		out.println("</BODY></HTML>");
	}
}