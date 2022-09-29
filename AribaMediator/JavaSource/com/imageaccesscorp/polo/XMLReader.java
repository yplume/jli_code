/*
 * Created on Mar 7, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.imageaccesscorp.polo;

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
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class XMLReader  {
	public static final String MAIL_ROUTER_XML_CONFIG_FILE_NAME = "PoloConfigAriba.xml";
	
	private int m_ReaderDelay;
	private String m_CMServerName = "";
	private String m_CMUserName = "";
	private String m_CMPassword = "";
	private String m_MonitoredWL = "";
	private String m_OutPutPath = "";
	private String m_SQLServerDBName = "";
	private String m_LSServerName = "";
	
	/**
	 * @return Returns the m_LSServerName.
	 */
	public String getLSServerName() {
		return m_LSServerName;
	}
	/**
	 * @param mailMessage The m_LSServerName to set.
	 */
	public void setLSServerName(String lsServerName) {
		m_LSServerName = lsServerName;
	}
	
	/**
	 * @return Returns the m_SQLServerDBName.
	 */
	public String getSQLServerDB() {
		return m_SQLServerDBName;
	}
	/**
	 * @param mailMessage The m_SQLServerDBName to set.
	 */
	public void setSQLServerDB(String sqlServerDB) {
		m_SQLServerDBName = sqlServerDB;
	}
	/**
	 * @return Returns the m_OutPutPath.
	 */
	public String getOutPutPath() {
		return m_OutPutPath;
	}
	/**
	 * @param mailMessage The m_OutPutPath to set.
	 */
	public void setOutPutPath(String outPutPath) {
		m_OutPutPath = outPutPath;
	}
	
	public int getReaderDelay() {
		return m_ReaderDelay;
	}

	public void setReaderDelay(int theReaderDelay) {
		m_ReaderDelay = theReaderDelay;
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
	 * @return Returns the m_MonitoredWL.
	 */
	public String getMonitoredWorkList() {
		return m_MonitoredWL;
	}
	/**
	 * @param cmPassword The m_MonitoredWL to set.
	 */
	public void setMonitoredWorkList(String monitoredWL) {
		this.m_MonitoredWL= monitoredWL;
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
				
				if (scChildNode.getNodeName().equals("SQLServerDBName")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setSQLServerDB("");
					} else {
						setSQLServerDB(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("LSServerName")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setLSServerName("");
					} else {
						setLSServerName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				
				if (scChildNode.getNodeName().equals("OutPutPath")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setOutPutPath("");
					} else {
						setOutPutPath(scChildNode.getFirstChild().getNodeValue());
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
