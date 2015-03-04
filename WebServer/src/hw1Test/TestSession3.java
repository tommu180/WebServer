package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TestSession3 extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		PrintWriter out = response.getWriter();
		out.println("<HTML><BODY>");
		if (session == null) {
			out.println("<P>Session invalidated.</P>");
		} else {
			out.println("<P>session isn't invalidated</P>");
		}
		out.println("<P>Go back to <A HREF=\"Redirect\">test cookie and session</A>.</P>");
		out.println("</BODY></HTML>");
	}
}
