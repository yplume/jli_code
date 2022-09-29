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
public class UnzipFilesController implements Runnable {

	private String m_BaseURL = "";
	private XMLReader m_pmxr = null;
	private UnzipFiles m_uz = null;
	private int m_SleepTime = 120000;
	private Connection m_conn = null;
	private boolean threadAlive = true;
	
	public UnzipFilesController(){
		
		m_uz = new UnzipFiles();
		/*m_pmxr = new XMLReader();
		m_pmxr.readXMLConfig();				
		m_hs = new HSChangeOwnerWL(m_pmxr.getCmServerName(), m_pmxr.getCmUserName(), m_pmxr.getCmPassword(), m_pmxr.getLSServerName());
		
		if (m_pmxr.getReaderDelay() > 0)	{
			m_SleepTime = m_pmxr.getReaderDelay() * 1000;
		}*/
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub

		while (threadAlive)	{
			try {
				System.out.println("---UnzipFilesController 2 mins---");
					//m_hs.setBaseURL(getBaseURL());
				m_uz.UnzipFiles1();
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
		
		UnzipFilesController uzfc = new UnzipFilesController();
		Thread td = new Thread(uzfc);
		td.start();
	}
	

}
