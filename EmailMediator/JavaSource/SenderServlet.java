import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.PooledConnection;
import javax.servlet.http.*;


public class SenderServlet extends HttpServlet implements Servlet {
	
//	private Logger logger = null;
	private PoloMailController m_pmc = null;
	private Thread m_t = null;
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public SenderServlet() {
		super();
	}

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest arg0, HttpServletResponse arg1)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		RequestDispatcher dispatcher = request.getRequestDispatcher("PoloDisplay");
		String rc = request.getParameter("PoloMailController");
		String requestURL = request.getRequestURL().toString();
		String requestURI = request.getRequestURI();
		String rootURL = requestURL.substring(0, requestURL.indexOf(requestURI)) + request.getContextPath();
		m_pmc.setBaseURL(rootURL);			
	}
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest arg0, HttpServletResponse arg1)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public void init(ServletConfig arg0) throws ServletException {
		m_pmc = new PoloMailController();
		m_t = new Thread(m_pmc);
		System.out.println("started ..........");
		m_t.start();
	}
	
	public void destroy()	{
		m_pmc.setThreadAlive(false);
		m_pmc = null;
		m_t = null;
	}

}