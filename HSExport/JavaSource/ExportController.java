
import java.sql.Connection;

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExportController implements Runnable {

	private boolean m_PauseExcution = true;
	private String m_BaseURL = "";
	private XMLReader m_pmxr = null;
	private ExportItems m_eit = null;
	private int m_SleepTime = 60000;
	private Connection m_conn = null;
	private boolean threadAlive = true;
	private long m_SleepTimeMill = 999999999;
	
	public ExportController()	{
		
		m_pmxr = new XMLReader();
		m_pmxr.readXMLConfig();	
		m_eit = new ExportItems();
		if (m_pmxr.getUpdateSleepTime()== -1)	{
			m_SleepTimeMill = Long.MAX_VALUE;
		}
		else if (m_pmxr.getUpdateSleepTime()== 0)	{
			m_SleepTimeMill = 0;
		}
		else {
			m_SleepTimeMill = m_pmxr.getUpdateSleepTime() * 1000;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub

		while (threadAlive)	{
			try {
				m_eit.setBaseURL(getBaseURL());
				System.out.println("Sleeping time will be = "+m_SleepTimeMill);
				m_eit.Export();
				System.out.println("Sleep time zzzzzz..."+m_SleepTimeMill);
				Thread.sleep(m_SleepTimeMill);
			} catch (InterruptedException ie)	{
			}
		}
	}

	/**
	 * @return Returns the m_BaseURL.
	 */
	public String getBaseURL() {
		return m_BaseURL;
	}
	/**
	 * @param baseURL The m_BaseURL to set.
	 */
	public void setBaseURL(String baseURL) {
		m_BaseURL = baseURL;
	}
	/**
	 * @return Returns the threadAlive.
	 */
	public boolean isThreadAlive() {
		return threadAlive;
	}
	/**
	 * @param threadAlive The threadAlive to set.
	 */
	public void setThreadAlive(boolean threadAlive) {
		this.threadAlive = threadAlive;		
	}

	public static void main(String[] args) {
		
		ExportController pmc = new ExportController();
		Thread td = new Thread(pmc);
		td.start();
	}
	

}
