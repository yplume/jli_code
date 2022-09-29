/*
 * Created on Mar 13, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.sql.Connection;

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UpdateCMController implements Runnable {

	private boolean m_PauseExcution = true;
	private String m_BaseURL = "";
	private XMLReader m_pmxr = null;
	private UpdateCM m_ucm = null;
	private int m_SleepTime = 60000;
	private Connection m_conn = null;
	private boolean threadAlive = true;
	
	public UpdateCMController()	{
		
		m_pmxr = new XMLReader();
		m_pmxr.readXMLConfig();	
		System.out.println("<------------UpdateCMController------->");
		m_ucm = new UpdateCM();
		if (m_pmxr.getUpdateSleepTime()> 0)	{
			m_SleepTime = m_pmxr.getUpdateSleepTime() * 1000;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub

		while (threadAlive)	{
			try {
System.out.println("UpdateCMControler");
				m_ucm.setBaseURL(getBaseURL());
				m_ucm.UpdateWorkflow();
				Thread.sleep(m_SleepTime);

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
		
		UpdateCMController pmc = new UpdateCMController();
		Thread td = new Thread(pmc);
		td.start();
	}
	

}
