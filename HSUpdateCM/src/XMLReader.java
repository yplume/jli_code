/*
 * Created on Jul 27, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLReader {
	public static final String MAIL_ROUTER_XML_CONFIG_FILE_NAME = "Config.xml";
	
	private String m_CMServerName = "";
	private String m_CMUserName = "";
	private String m_CMPassword = "";
	private String m_DBServerName = "";
	private String m_DBName = "";
	private String m_DBUser = "";
	private String m_DBPass = "";
	private int m_WaitingTime = 0;
	private int m_UpdateSleepTime = 0;
	private String m_WorkflowName = "";
	private String m_ErrorWorklist = "";
	private String m_Attribute = "";
	
	//private static final String XML_FILE_NAME = "Config.xml"; 	
	

	public int getWaitingTime() {
		return m_WaitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		m_WaitingTime = waitingTime;
	}

	public int getUpdateSleepTime() {
		return m_UpdateSleepTime;
	}

	public void setUpdateSleepTime(int updateSleepTime) {
		m_UpdateSleepTime = updateSleepTime;
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
	 * @return Returns the DBServerName.
	 */
	public String getDBServerName() {
		return m_DBServerName;
	}
	/**
	 * @param cmPassword The DBServerName to set.
	 */
	public void setDBServerName(String dbServerName) {
		this.m_DBServerName= dbServerName;
	}
	/**
	 * @return Returns the DBName.
	 */
	public String getDBName() {
		return m_DBName;
	}
	/**
	 * @param cmPassword The DBName to set.
	 */
	public void setDBName(String dbName) {
		this.m_DBName= dbName;
	}
	
	/**
	 * @return Returns the DBUser
	 */
	public String getDBUser() {
		return m_DBUser;
	}
	/**
	 * @param cmPassword The DBUser to set.
	 */
	public void setDBUser(String dbUser) {
		this.m_DBUser = dbUser;
	}
	/**
	 * @return Returns the DBPass.
	 */
	public String getDBPass() {
		return m_DBPass;
	}
	/**
	 * @param cmPassword The DBPass to set.
	 */
	public void setDBPass(String dbPass) {
		this.m_DBPass = dbPass;
	}
	/**
	 * @return Returns the workflowName.
	 */
	public String getWorkflowName() {
		return m_WorkflowName;
	}
	/**
	 * @param workflowName The workflowName to set.
	 */
	public void setWorkflowName(String workflowName) {
		this.m_WorkflowName = workflowName;
	}
	/**
	 * @return Returns the attr1.
	 */
	public String getError() {
		return m_ErrorWorklist;
	}
	/**
	 * @param attr1 The attr1 to set.
	 */
	public void setError(String errorWorklist) {
		this.m_ErrorWorklist = errorWorklist;
	}
	
	/**
	 * @return Returns the attr2.
	 */
	public String getAttr() {
		return m_Attribute;
	}
	/**
	 * @param attr2 The attr2 to set.
	 */
	public void setAttr(String attribute) {
		this.m_Attribute = attribute;
	}
	
	public void readXMLConfig()	{
		final ClassLoader loader = this.getClass().getClassLoader(); 
		URL url = loader.getResource(MAIL_ROUTER_XML_CONFIG_FILE_NAME);
		File configFile = new File(MAIL_ROUTER_XML_CONFIG_FILE_NAME);	
		System.out.println("configFile="+configFile.getPath());
		
		//final ClassLoader loader = this.getClass().getClassLoader(); 
		//URL url = loader.getResource(XML_FILE_NAME);
		//File configFile = new File(url.getPath());
		//final ClassLoader loader = this.getClass().getClassLoader(); 
		//URL url = loader.getResource(MAIL_ROUTER_XML_CONFIG_FILE_NAME);
		//File configFile = new File(MAIL_ROUTER_XML_CONFIG_FILE_NAME);

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
				if (scChildNode.getNodeName().equals("DBUserName")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setDBUser("");
					} else {
						setDBUser(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("DBPassword")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setDBPass("");
					} else {
						setDBPass(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("WorkflowName")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setWorkflowName("");
					} else {
						setWorkflowName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("Error")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setError("");
					} else {
						setError(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("Attribute")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setAttr("");
					} else {
						setAttr(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				
				if (scChildNode.getNodeName().equals("UpdateSleepTime")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setUpdateSleepTime(300);
					} else {
						setUpdateSleepTime(Integer.parseInt(scChildNode.getFirstChild().getNodeValue()));
						if (getUpdateSleepTime() == 0)	{
							setUpdateSleepTime(300);
						}
					}	
				}
				if (scChildNode.getNodeName().equals("WaitingTime")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setWaitingTime(60); 
					} else {
						setWaitingTime(Integer.parseInt(scChildNode.getFirstChild().getNodeValue()));
						if (getWaitingTime() == 0)	{
							setWaitingTime(60);
						}
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

	
	public static void main(String[] args){
		XMLReader xr = new XMLReader();
		xr.readXMLConfig();
	}
}
