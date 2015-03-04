package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet2 extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    PrintWriter out = response.getWriter();
	    out.println("<P>Continue to <A HREF=\"Redirect\">test redirect</A>.</P>");
	    out.println("<br>");
	    out.println("<P>Continue to <A HREF=\"CookieTestA\">test cookie</A>.</P>");
	    out.println("<br>");
	    response.sendError(400);
	}
}
