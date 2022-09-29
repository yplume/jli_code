

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SenderServlet extends HttpServlet implements Servlet {
	
//	private Logger logger = null;
	private UpdateCMController m_ucmc = null;
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
		m_ucmc.setBaseURL(rootURL);			
	}
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest arg0, HttpServletResponse arg1)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public void init(ServletConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("<--------------SenderServlet------->");
		m_ucmc = new UpdateCMController();
		m_t = new Thread(m_ucmc);
		m_t.start();
	}
	
	public void destroy()	{
		m_ucmc.setThreadAlive(false);
		m_ucmc = null;
		m_t = null;
	}

}