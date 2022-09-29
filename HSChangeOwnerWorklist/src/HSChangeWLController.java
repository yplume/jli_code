/*
 * Created on Mar 13, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */


import java.net.URL;
import java.sql.SQLException;

import javax.sql.PooledConnection;
import java.sql.Connection;



/**
 * @author JLI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HSChangeWLController implements Runnable {

	private String m_BaseURL = "";
	private XMLReader m_pmxr = null;
	private HSChangeOwnerWL m_hs = null;
	private int m_SleepTime = 60000;
	private Connection m_conn = null;
	private boolean threadAlive = true;
	
	public HSChangeWLController(){
		
		m_pmxr = new XMLReader();
		m_pmxr.readXMLConfig();				
		m_hs = new HSChangeOwnerWL(m_pmxr.getCmServerName(), m_pmxr.getCmUserName(), m_pmxr.getCmPassword(), m_pmxr.getLSServerName());
		
		if (m_pmxr.getReaderDelay() > 0)	{
			m_SleepTime = m_pmxr.getReaderDelay() * 1000;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub

		while (threadAlive)	{
			try {
				System.out.println("---HSChangeWLController---");
					//m_hs.setBaseURL(getBaseURL());
				m_hs.HSChangeOwnerWL();
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
		
		HSChangeWLController pmc = new HSChangeWLController();
		Thread td = new Thread(pmc);
		td.start();
	}
	

}
