package hw1Test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServletConfig extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();
		out.println("<HTML><BODY>");
		out.println("<P>The value of Servlet Name is: " + config.getServletName() + "</P>");
		out.println("<P>The value of 'TestParam' is: " + context.getServerInfo() + "</P>");
		out.println("<P>Go to <A HREF=\"Request\">test request and response</A>.</P>");
		out.println("</BODY></HTML>");		
	}
}
