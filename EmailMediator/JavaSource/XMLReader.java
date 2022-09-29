/*
 * Created on Mar 7, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.net.URL;
import java.io.*;
import java.util.*;

//import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class XMLReader  {
	public static final String MAIL_ROUTER_XML_CONFIG_FILE_NAME = "EmailSenderConfig.xml";
	
	private int m_ReaderDelay;
	private String m_CMServerName = "";
	private String m_CMUserName = "";
	private String m_CMPassword = "";
	private String m_DBServerName = "";
	private String m_DBName = "";
	//private Logger logger = null;
	private String m_MailServerName = "";
	private String m_MailUserName = "";
	private String m_MailPassword = "";
	private String m_MailTransportProtocol = "";
	private String m_MailMessage = "";
	private String m_ReturnEmailAddress = "";
	private String m_ErrorAlertEmailAddress = "";
	private int m_WaitingDays;
	private int m_EmailAttempts;
	private String m_MonitoredWL = "";
	private String m_NextWN = "";
	private String m_PreWN = "";
	
	/**
	 * @return Returns the m_MailMessage.
	 */
	public String getMailMessage() {
		return m_MailMessage;
	}
	/**
	 * @param mailMessage The m_MailMessage to set.
	 */
	public void setMailMessage(String mailMessage) {
		m_MailMessage = mailMessage;
	}
	/**
	 * @return Returns the m_MailServerName.
	 */
	public String getMailServerName() {
		return m_MailServerName;
	}
	/**
	 * @param mailServerName The m_MailServerName to set.
	 */
	public void setMailServerName(String mailServerName) {
		m_MailServerName = mailServerName;
	}
	/**
	 * @return Returns the m_MailTransportProtocol.
	 */
	public String getMailTransportProtocol() {
		return m_MailTransportProtocol;
	}
	/**
	 * @param mailTransportProtocol The m_MailTransportProtocol to set.
	 */
	public void setMailTransportProtocol(String mailTransportProtocol) {
		m_MailTransportProtocol = mailTransportProtocol;
	}
	/**
	 * @return Returns the m_MailUserName.
	 */
	public String getMailUserName() {
		return m_MailUserName;
	}
	/**
	 * @param mailUserName The m_MailUserName to set.
	 */
	public void setMailUserName(String mailUserName) {
		m_MailUserName = mailUserName;
	}
	/**
	 * @return Returns the m_MailUserName.
	 */
	public String getMailPassword() {
		return m_MailPassword;
	}
	/**
	 * @param mailUserName The m_MailUserName to set.
	 */
	public void setMailPassword(String mailPassword) {
		m_MailPassword = mailPassword;
	}	
	
	public XMLReader()	{
//		URL url = Loader.getResource("log4j.properties");
//	    PropertyConfigurator.configure(url.getPath());
//		logger = Logger.getLogger("++++++++++++++"+XMLReader.class.getName());
		
	}

	public int getReaderDelay() {
		return m_ReaderDelay;
	}

	public void setReaderDelay(int theReaderDelay) {
		m_ReaderDelay = theReaderDelay;
	}

	/**
	 * @return Returns the m_EmailAttempts.
	 */
	public int getEmailAttempts() {
		return m_EmailAttempts;
	}
	/**
	 * @param m_EmailAttempts The m_EmailAttempts to set.
	 */
	public void setEmailAttempts(int m_EmailAttempts) {
		this.m_EmailAttempts = m_EmailAttempts;
	}
	/**
	 * @return Returns the cmServerName.
	 */
	public String getCmServerName() {
		return m_CMServerName;
	}
	/**
	 * @param cmServerName The cmServerName to set.
	 */
	public void setCmServerName(String cmServerName) {
		this.m_CMServerName = cmServerName;
	}
	/**
	 * @return Returns the cmUserName.
	 */
	public String getCmUserName() {
		return m_CMUserName;
	}
	/**
	 * @param cmUserName The cmUserName to set.
	 */
	public void setCmUserName(String cmUserName) {
		this.m_CMUserName = cmUserName;
	}
	/**
	 * @return Returns the m_WaitingDays.
	 */
	public int getWaitingDays() {
		return m_WaitingDays;
	}
	/**
	 * @param m_WaitingDays The m_WaitingDays to set.
	 */
	public void setWaitingDays(int m_WaitingDays) {
		this.m_WaitingDays = m_WaitingDays;
	}
	/**
	 * @return Returns the cmPassword.
	 */
	public String getCmPassword() {
		return m_CMPassword;
	}
	/**
	 * @param cmPassword The cmPassword to set.
	 */
	public void setCmPassword(String cmPassword) {
		this.m_CMPassword = cmPassword;
	}
	/**
	 * @return Returns the cmPassword.
	 */
	public String getDBServerName() {
		return m_DBServerName;
	}
	/**
	 * @param cmPassword The cmPassword to set.
	 */
	public void setDBServerName(String dbServerName) {
		this.m_DBServerName= dbServerName;
	}
	/**
	 * @return Returns the cmPassword.
	 */
	public String getDBName() {
		return m_DBName;
	}
	/**
	 * @param cmPassword The cmPassword to set.
	 */
	public void setDBName(String dbName) {
		this.m_DBName= dbName;
	}

	
	public void readXMLConfig()	{
		
		final ClassLoader loader = this.getClass().getClassLoader(); 
		URL url = loader.getResource(MAIL_ROUTER_XML_CONFIG_FILE_NAME);
		File configFile = new File(MAIL_ROUTER_XML_CONFIG_FILE_NAME);	
		System.out.println("configFile="+configFile.getPath());
		
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
		try {

			
			docBuildFact.setIgnoringElementContentWhitespace(false);
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(configFile);
			Node rootNode = doc.getFirstChild();
			ArrayList scChildren = getXMLChildren("#text", rootNode, true);
			Iterator scChildrenIt = scChildren.iterator();
			while (scChildrenIt.hasNext())	{
				Node scChildNode = (Node)scChildrenIt.next();
				if (scChildNode.getNodeName().equals("CMServerName")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setCmServerName("");
					} else {
						setCmServerName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("CMUserName")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setCmUserName("");
					} else {
						setCmUserName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("CMPassword")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setCmPassword("");
					} else {
						setCmPassword(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("DBServerName")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setDBServerName("");
					} else {
						setDBServerName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("DBName")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setDBName("");
					} else {
						setDBName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
								
				if (scChildNode.getNodeName().equals("MailServerName")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setMailServerName("");
					} else {
						setMailServerName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("MailUserName")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setMailUserName("");
					} else {
						setMailUserName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("MailPassword")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setMailPassword("");
					} else {
						setMailPassword(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("ReturnEmailAddress")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setReturnEmailAddress("");
					} else {
						setReturnEmailAddress(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("ErrorAlertEmailAddress")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setErrorAlertEmailAddress("");
					} else {
						setErrorAlertEmailAddress(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				
				if (scChildNode.getNodeName().equals("MailTransport")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setMailTransportProtocol("");
					} else {
						setMailTransportProtocol(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				
				if (scChildNode.getNodeName().equals("MailMessage")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setMailMessage("");
					} else {
						setMailMessage(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				
//				if (scChildNode.getNodeName().equals("FullMailMessage")) {
//					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
//						setFullMailMessage("");
//					} else {
//						setFullMailMessage(scChildNode.getFirstChild().getNodeValue());
//					}	
//				}
				
				if (scChildNode.getNodeName().equals("ReaderSleepTime")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setReaderDelay(60);
					} else {
						setReaderDelay(Integer.parseInt(scChildNode.getFirstChild().getNodeValue()));
						if (getReaderDelay() == 0)	{
							setReaderDelay(60);
						}
					}	
				}
				if (scChildNode.getNodeName().equals("WaitingDays")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setWaitingDays(5);
					} else {
						setWaitingDays(Integer.parseInt(scChildNode.getFirstChild().getNodeValue()));
					}
				}
				if (scChildNode.getNodeName().equals("EmailAttempts")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setEmailAttempts(2);
					} else {
						setEmailAttempts(Integer.parseInt(scChildNode.getFirstChild().getNodeValue()));
					}	
				}
			}
			System.out.println("XMLReader Finished ");

		} catch (ParserConfigurationException pce)	{
			System.out.println("XML Config file Parser failed with " + pce.toString());			
//			logger.fatal("XML Config file Parser failed with ", pce);
		} catch (SAXException se) {
			System.out.println("XML Config file Parser method failed with SAX exception " + se.toString());
			//			logger.fatal("XML Config file Parser method failed with SAX exception ", se);
		} catch (IOException ioe)	{
			System.out.println("XML Config file Parser method failed with io exception " + ioe.toString());
//			logger.fatal("XML Config file Parser method failed with io exception ", ioe);			
		} 
	}
	
	private ArrayList getXMLChildren(String childName, Node parent, boolean exclude)	{
		ArrayList childNodes = new ArrayList();
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)		{
			//System.out.println("Looking for " + childName + " under " + parent.getNodeName() + " found - " + children.item(i).getNodeName());
			if (exclude) {
				if (!children.item(i).getNodeName().equals(childName))	{
					childNodes.add(children.item(i));
			//		System.out.println("found exclude " + children.item(i).getNodeName());
				}
			} else {
				if (children.item(i).getNodeName().equals(childName))	{
					childNodes.add(children.item(i));
				//	System.out.println("found include " + children.item(i).getNodeName());
				}			
			}
		}
		return childNodes;
	}

	/**
	 * @return Returns the m_ReturnEmailAddress.
	 */
	public String getReturnEmailAddress() {
		return m_ReturnEmailAddress;
	}
	/**
	 * @param returnEmailAddress The m_ReturnEmailAddress to set.
	 */
	public void setReturnEmailAddress(String returnEmailAddress) {
		m_ReturnEmailAddress = returnEmailAddress;
	}
	/**
	 * @return Returns the m_ErrorAlertEmailAddress.
	 */
	public String getErrorAlertEmailAddress() {
		return m_ErrorAlertEmailAddress;
	}
	/**
	 * @param returnEmailAddress The m_ErrorAlertEmailAddress to set.
	 */
	public void setErrorAlertEmailAddress(String errorAlertEmailAddress) {
		m_ErrorAlertEmailAddress = errorAlertEmailAddress;
	}	
	public static void main(String[] args){
		XMLReader xr = new XMLReader();
		xr.readXMLConfig();
	}
}
