package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TestSession1 extends HttpServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.setAttribute("Mysession", "111111");
		PrintWriter out = response.getWriter();
		out.println("<HTML><BODY>");
		out.println("<P>Set new session 'Mysession' to 111111.</P>");
		out.println("<P><A HREF=\"SessionTestB\">invalidate session</A>.</P>");
		out.println("</BODY></HTML>");
	}
}
