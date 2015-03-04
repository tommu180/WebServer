package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TestSession2 extends HttpServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String attr = (String) session.getAttribute("Mysession");
		PrintWriter out = response.getWriter();
		out.println("<HTML><BODY>");
		out.println("<P>Mysession value is '" + attr + "'.</P>");
		session.invalidate();
		out.println("<P>Session invalidated.</P>");
		out.println("<P><A HREF=\"SessionTestC\">Check session removal</A>.</P>");
		out.println("</BODY></HTML>");
	}
}
