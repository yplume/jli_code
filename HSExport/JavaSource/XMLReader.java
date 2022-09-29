
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
	public static final String MAIL_ROUTER_XML_CONFIG_FILE_NAME = "ExportConfig.xml";
	
	private String m_CMServerName = "";
	private String m_CMUserName = "";
	private String m_CMPassword = "";
	private int m_DateRange = 0;
	private int m_UpdateSleepTime = 0;
	private String m_UNC = "";
	private String m_InitRun = "";
	private ArrayList m_ItemTypes;
	
	private String m_APUTableName = "";
	private String m_APCTableName = "";
	private String m_JETableName = "";
	private String m_JECTableName = "";
	private String m_BarCode = "";
	private String m_ScanDate = "";
	private String m_EmailDate = "";
	private String m_Deleted = "";
	
	//private static final String XML_FILE_NAME = "Config.xml"; 	
	
	public String getInitRun() {
		return m_InitRun;
	}

	public void setInitRun(String InitRun) {
		m_InitRun = InitRun;
	}
	public String getUNC() {
		return m_UNC;
	}

	public void setUNC(String UNC) {
		m_UNC = UNC;
	}

	public int getDateRange() {
		return m_DateRange;
	}

	public void setDateRange(int dateRange) {
		m_DateRange = dateRange;
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
	
	public void setItemTypes(ArrayList itemTypes) {
		this.m_ItemTypes = itemTypes;
	}

	public ArrayList getItemTypes() {
		return m_ItemTypes;
	}
	
	//add table and attribute DB2 name
	public void setAPUTableName(String APUTableName) {
		this.m_APUTableName = APUTableName;
	}

	public String getAPUTableName() {
		return m_APUTableName;
	}
	
	public void setAPCTableName(String APCTableName) {
		this.m_APCTableName = APCTableName;
	}

	public String getAPCTableName() {
		return m_APCTableName;
	}
	
	public void setJETableName(String JETableName) {
		this.m_JETableName = JETableName;
	}

	public String getJETableName() {
		return m_JETableName;
	}
	
	public void setJECTableName(String JECTableName) {
		this.m_JECTableName = JECTableName;
	}

	public String getJECTableName() {
		return m_JECTableName;
	}
	
	public void setBarCode(String BarCode) {
		this.m_BarCode = BarCode;
	}

	public String getBarCode() {
		return m_BarCode;
	}
	
	public void setScanDate(String ScanDate) {
		this.m_ScanDate = ScanDate;
	}

	public String getScanDate() {
		return m_ScanDate;
	}
	
	public void setEmailDate(String EmailDate) {
		this.m_EmailDate = EmailDate;
	}

	public String getEmailDate() {
		return m_EmailDate;
	}
	public void setDeleted(String Deleted) {
		this.m_Deleted = Deleted;
	}

	public String getDeleted() {
		return m_Deleted;
	}
	private void ReadItemTypes(Node rootNode) {
		System.out.println("ReadItemTypes");
		ArrayList it = getXMLChildren("ItemTypes", rootNode, false);
		ArrayList its = new ArrayList();
		Node itMapNode = (Node)it.get(0);
		ArrayList itChildren = getXMLChildren("#text", itMapNode, true);
		Iterator itChildrenIt = itChildren.iterator();
		while (itChildrenIt.hasNext())	{
			Node itChildNode = (Node)itChildrenIt.next();
			if (itChildNode.getNodeName().equals("name")) {
				if (itChildNode.getChildNodes().getLength() == 0 ) 	{
					its.add(null);
				} else {
					its.add(itChildNode.getFirstChild().getNodeValue());
				}	
			}
		}
		setItemTypes(its);
	}

	
	public void readXMLConfig()	{
		final ClassLoader loader = this.getClass().getClassLoader(); 
		URL url = loader.getResource(MAIL_ROUTER_XML_CONFIG_FILE_NAME);
		File configFile = new File(MAIL_ROUTER_XML_CONFIG_FILE_NAME);	
		System.out.println("configFile = "+configFile.getPath());
		
		
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
				
				
				
				
				if (scChildNode.getNodeName().equals("UpdateSleepTime")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setUpdateSleepTime(-1);
					} else {
						setUpdateSleepTime(Integer.parseInt(scChildNode.getFirstChild().getNodeValue()));
						if (getUpdateSleepTime() == 0)	{
							setUpdateSleepTime(0);
						}
					}	
				}
				if (scChildNode.getNodeName().equals("DateRange")) {
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setDateRange(3); 
					} else {
						setDateRange(Integer.parseInt(scChildNode.getFirstChild().getNodeValue()));
						if (getDateRange() == 0)	{
							setDateRange(3);
						}
					}	
				}
				if (scChildNode.getNodeName().equals("UNCPath")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setUNC("");
					} else {
						setUNC(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("InitRun")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setInitRun("0");
					} else {
						setInitRun(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("APUTableName")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setAPUTableName("0");
					} else {
						setAPUTableName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("APCTableName")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setAPCTableName("0");
					} else {
						setAPCTableName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("JETableName")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setJETableName("0");
					} else {
						setJETableName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("JECTableName")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setJECTableName("0");
					} else {
						setJECTableName(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("BarCode")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setBarCode("0");
					} else {
						setBarCode(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("ScanDate")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setScanDate("0");
					} else {
						setScanDate(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("EmailDate")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setEmailDate("0");
					} else {
						setEmailDate(scChildNode.getFirstChild().getNodeValue());
					}	
				}
				if (scChildNode.getNodeName().equals("Deleted")) {
					
					if (scChildNode.getChildNodes().getLength() == 0 ) 	{
						setDeleted("0");
					} else {
						setDeleted(scChildNode.getFirstChild().getNodeValue());
					}	
				}
			}
			ReadItemTypes(rootNode);	
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
			if (exclude) {
				if (!children.item(i).getNodeName().equals(childName))	{
					childNodes.add(children.item(i));
				}
			} else {
				if (children.item(i).getNodeName().equals(childName))	{
					childNodes.add(children.item(i));
				}			
			}
		}
		return childNodes;
	}

	
	public static void main(String[] args){
		XMLReader xr = new XMLReader();
		xr.readXMLConfig();
		System.out.println("getUNC = "+xr.getUNC());
	}
}
