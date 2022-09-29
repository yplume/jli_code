

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SenderServlet extends HttpServlet implements Servlet {
	
	private ExportController m_ec = null;
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
		String requestURL = request.getRequestURL().toString();
		String requestURI = request.getRequestURI();
		String rootURL = requestURL.substring(0, requestURL.indexOf(requestURI)) + request.getContextPath();
		m_ec.setBaseURL(rootURL);			
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
		m_ec = new ExportController();
		m_t = new Thread(m_ec);
		m_t.start();
	}
	
	public void destroy()	{
		m_ec.setThreadAlive(false);
		m_ec = null;
		m_t = null;
	}

}