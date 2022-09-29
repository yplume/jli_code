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
 * @author jason li
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PoloMailController implements Runnable {

	private boolean m_PauseExcution = true;
	private String m_BaseURL = "";
	private XMLReader m_pmxr = null;
	private SendMail m_sm = null;
	private int m_SleepTime = 60000;
	private Connection m_conn = null;
	private boolean threadAlive = true;
	
	public PoloMailController()	{
		
		m_pmxr = new XMLReader();
		m_pmxr.readXMLConfig();				
		m_sm = new SendMail(m_pmxr.getMailServerName(), m_pmxr.getMailUserName(),m_pmxr.getMailPassword(), 
				m_pmxr.getMailTransportProtocol(), m_pmxr.getReturnEmailAddress(),m_pmxr.getErrorAlertEmailAddress(), m_conn, m_pmxr.getWaitingDays(), 
				m_pmxr.getEmailAttempts(), m_pmxr.getCmServerName(), m_pmxr.getCmUserName(), m_pmxr.getCmPassword(), m_pmxr.getMailMessage(),m_pmxr.getDBServerName(), 
				m_pmxr.getDBName());
		
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
				m_sm.setBaseURL(getBaseURL());
				m_sm.sendMail1();
				Thread.sleep(m_SleepTime);

			} catch (InterruptedException ie)	{
				System.out.println("Error in thread: " + ie.getMessage());
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
		
		PoloMailController pmc = new PoloMailController();
		Thread td = new Thread(pmc);
		td.start();
	}
	

}
