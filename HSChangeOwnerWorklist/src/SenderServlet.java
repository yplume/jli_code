
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
	private HSChangeWLController m_hs = null;
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
		m_hs.setBaseURL(rootURL);			
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
//		super.init(arg0);
//		URL url = Loader.getResource("log4j.properties");
//		System.out.println("Log4jURL = " + url.getPath());
//		PropertyConfigurator.configure(url);

//		logger.debug("This is a test of the logger.  found log4j.properties." );
		m_hs = new HSChangeWLController();
//		m_pmc.setPauseExcution(true);
			m_t = new Thread(m_hs);
			m_t.start();
	}
	
	public void destroy()	{
		m_hs.setThreadAlive(false);
		m_hs = null;
		m_t = null;
	}
}