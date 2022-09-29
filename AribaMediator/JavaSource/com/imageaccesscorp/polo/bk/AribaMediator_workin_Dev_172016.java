/*
 * Created on Mar 15, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.imageaccesscorp.polo;

//import ICMConnectionPool;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.text.*;
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.DKDatastoreICM;

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AribaMediator_workin_Dev_172016 {

    private String m_BaseURL = "";
    private DKDatastoreICM m_dsICM = null;
    private DKDocRoutingServiceICM m_RoutingService;
    private String m_serverName;
    private String m_userName;
    private String m_password;
    private String m_OutPutPath;
    private String m_sqlServerDBName;
    
    
    /*public AribaMediator(String cmServerName, String cmUserName, String cmPassword, String SQLServer) {
        this(cmServerName, cmUserName, cmPassword, SQLServer);
    }*/

    public AribaMediator_workin_Dev_172016(String cmServerName, String cmUserName, String cmPassword, String outPutPath, String sqlDatabase) {
        
        m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        m_OutPutPath = outPutPath;
        m_sqlServerDBName = sqlDatabase;
    }

    public void AribaMediator() {
    	System.out.println("Calling AribaMediator ...");
    	monitorExportToKofaxWorkList();
    	monitorChangeItemTypeWorkList();
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
    private void generateXML(String assignNum, String specialHandlingCode){
    	System.out.println("m_OutPutPath="+m_OutPutPath);
    	String tifPath = m_OutPutPath + assignNum + ".TIF";
    	//tifPath = tifPath.replaceAll("\\", "\\");
    	//System.out.println("tifPath="+tifPath);
    	Date date = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy h:mm:ss a");
    	String formattedDate = sdf.format(date);
    	System.out.println(formattedDate); // 12/01/2011 4:48:16 PM
    	
    	String xmlBody = "<ImportSession>\n";
		xmlBody += "\t<Batches>\n";
		xmlBody += "\t\t<Batch Name=\"" + formattedDate + "\" BatchClassName=\"Incorrect Rejection-Reprocessing\" Priority=\"1\" Processed=\"0\">\n";
		xmlBody += "\t\t\t<BatchFields>\n";
		xmlBody += "\t\t\t\t<BatchField Name=\"SpecialHandlingCode\" Value=\"" + specialHandlingCode + "\" />\n";
		xmlBody += "\t\t\t</BatchFields>\n";
      

		xmlBody += "\t\t\t<Documents>\n";
		xmlBody += "\t\t\t\t<Document FormTypeName=\"FT_Ariba Invoice3\">\n";
		xmlBody += "\t\t\t\t\t<Pages>\n";
		//xmlBody += "\t\t\t\t\t\t<Page ImportFileName=\"" + tifPath + "\"/>\n";
		xmlBody += "\t\t\t\t\t\t<Page ImportFileName=\"C:\\ExportToKofax\\Images\\" + assignNum + ".tif\"/>\n";
		xmlBody += "\t\t\t\t\t</Pages>\n";
		
		/*xmlBody += "\t\t\t\t\t<IndexFields>\n";
		
		Set entrySet = hm.entrySet();

		Iterator entrySetIterator = entrySet.iterator();
		System.out.println("------------------Write Attributes-----------------");
		   
		while (entrySetIterator.hasNext()) {

		   Map.Entry entry = (Map.Entry)entrySetIterator.next();

		   xmlBody += "\t\t\t\t\t\t<IndexField Name=\""+entry.getKey()+"\" Value=\""+entry.getValue()+"\"/>\n";
		   
		   System.out.println("key: " + entry.getKey() + " value: " + entry.getValue());
		}
		System.out.println("--------------------------End----------------------");
		
		xmlBody += "\t\t\t\t\t</IndexFields>\n";*/
		xmlBody += "\t\t\t\t</Document>\n";
		xmlBody += "\t\t\t</Documents>\n";
		xmlBody += "\t\t</Batch>\n";
		xmlBody += "\t</Batches>\n";
		xmlBody += "</ImportSession>";
		
		try {
			System.out.println("generateXML file ="+m_OutPutPath + assignNum + ".xml");
			//m_OutPutPath = "\\\\"+(m_OutPutPath.substring(2)).replaceAll("\\\\", "\\\\\\\\");
			//System.out.println("m_OutPutPath =>"+"\\\\USNCPDKTMRTR1V\\\\ExportToKofax\\\\" + assignNum + ".xml");
			
			//File file = new File("\\\\USNCPDKTMRTR1V\\ExportToKofax\\" + assignNum + ".xml");
			File file = new File(m_OutPutPath + assignNum + ".xml");
			
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(xmlBody);
			output.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }
    
    private void updateFromExportAriba(String assignNum){
    	
    	Connection conn = null;
    	Statement stmt = null;
    	ResultSet rs = null;
    	String sql = null;
		try{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
	        conn = DriverManager.getConnection("jdbc:sqlserver://USNCPDKFXCAP1v;DatabaseName="+ m_sqlServerDBName +";user=test;password=test;integratedSecurity=false");
	        stmt = conn.createStatement();
	        sql = "Update dbo.ExportAriba set flag='1' where ExportID="+ assignNum;
			stmt.executeUpdate(sql);
			System.out.println("Update query = "+sql);
			stmt.close();
			conn.close();
		}catch(Exception e){
	    	System.out.println(e);
	    	
	    } finally {
	    	try {
	    		
	        	if (conn != null)	{
	            	conn.close();
	            	conn = null;
	            }
	        	if (stmt != null)	{
	        		stmt.close();
	        		stmt = null;
	            }
	    	} catch (SQLException sqle) {
	            sqle.printStackTrace();
	        }
	    }
    }
    private void exportImage(DKDatastoreICM dsICM, DKDDO ddo, String assignNum) {
        
        try {
        	int read = 0;
			OutputStream out = null;
			DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
			dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM); // Reset retrieve options to "IDONLY"
            dkRetrieveOptions.resourceContent(true);
            System.out.println("Start exportImage, path="+m_OutPutPath);
            byte[] bytes = new byte[1024];
            ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
            //System.out.println("dataid="+dataid);
            if (dataid > 0) {
                DKParts dkParts = (DKParts) ddo.getData(dataid);
                if (dkParts.cardinality() == 0 || dkParts.cardinality() > 1) {
                    // log an error
                	System.out.println("Retreive image error.");
                }
                dkIterator iter = dkParts.createIterator();
                int partCounter = 0;
                //System.out.println("exportImage path="+m_OutPutPath);
                while (iter.more()) {
                    DKLobICM part = (DKLobICM) iter.next();
                    part.retrieve(dkRetrieveOptions.dkNVPair());
                    out = new FileOutputStream(new File(m_OutPutPath + "\\Images\\"  + assignNum +".tif"));
                    InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(),-1,-1); 
                    //System.out.println("inputStream="+inputStream);
                    try {
	                    while ((read = inputStream.read(bytes)) != -1) {
	                		out.write(bytes, 0, read);
	                	//	imageSize+=read;
	                	}
                    }catch(Exception e){
                    	System.out.println("Exception in read:"+e.getMessage());
                    }
                    System.out.println("Creating Tiff");
                    part = null;
                	inputStream.close();
                	inputStream = null;
                }
                out.close();
            	out = null;
            } else {
                System.out.println("readImage: Could not find item for transaction# = " + ddo.getDataByName(""));
            }
 
        } catch (DKException dke) {
            dke.printStackTrace();
            
        } catch (InstantiationException ie) {
            ie.printStackTrace();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }   
    }

    public void monitorExportToKofaxWorkList() {
        System.out.println("Start to process ExportToKofax WorkList ....");

        DKDatastoreICM dsICM = null;
        String pid = null;

        try {
            dsICM = new DKDatastoreICM();
            dsICM = ICMConnectionPool.getConnection(m_userName, m_password, m_serverName);
//	    	dsICM.connect(m_serverName, m_userName, m_password, "SCHEMA=ICMADMIN");
            System.out.println("After get connection ");
            DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
            System.out.println("After create service ");
            com.ibm.mm.sdk.server.DKDatastoreExtICM dsExtICM = (com.ibm.mm.sdk.server.DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
            System.out.println("After get dsExt dsExtICM= "+dsExtICM);
            //System.out.println("routingService = "+routingService);
            
            //dkCollection workPackages = routingService.listWorkPackages("ICCAApplicationSubmissions", "");
            dkCollection workPackages = routingService.listWorkPackages("Export To Kofax", "");
            //System.out.println("workPackages = "+workPackages);
            System.out.println("After get workpackage = "+workPackages.cardinality());

            DKDDO ddo = null;
            if (workPackages.cardinality() == 0) {
                workPackages = null;
            } else {
                System.out.println("Number of items in Export To Kofax - " + workPackages.cardinality());
                dkIterator iter = workPackages.createIterator();
                DKWorkPackageICM workPackage;
                String assignNum = "";
                String vendorName = "";
                String vendorNum = "";
                String amount = "";
                String invoiceNum = "";
                String invoiceDate = "";
                String po = "";
                String rejectCode = "";
                String specialHandlingCode = "";
                HashMap hm = new HashMap();
                
                short dataId;
                while (iter.more()) {
                    workPackage = (DKWorkPackageICM) iter.next();
                    pid = workPackage.getPidString();
                    ddo = dsICM.createDDO(workPackage.getItemPidString());
                    boolean bNoAvailableItem = true;
                    String checkedOutByStr = "";
                    if (dsExtICM.isCheckedOut(ddo)) {
                        checkedOutByStr = dsExtICM.checkedOutUserid(ddo);
                        if (checkedOutByStr.equalsIgnoreCase(m_userName)) {
                            bNoAvailableItem = false;
                        }
                    } else {
                        bNoAvailableItem = false;
                        dsICM.checkOut(ddo);
                    }
                    if (bNoAvailableItem == false) {
                        ddo.retrieve(com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_ATTRONLY | com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_CHILDREN | com.ibm.mm.sdk.common.DKConstant.DK_CM_CHECKOUT);
 
                        if (ddo.getDataByName("AssignmentNumber") != null) {
                        	assignNum = ddo.getDataByName("AssignmentNumber").toString().toUpperCase();
                        }
                        if (ddo.getDataByName("SpecialHandlingCode") != null) {
                        	specialHandlingCode = ddo.getDataByName("SpecialHandlingCode").toString();
                             //   hm.put("AssignmentNuspecialHandlingCoder",assignNum);
                        }
                        
                        /*if (ddo.getDataByName("PO_Number") != null) {
                            po = ddo.getDataByName("PO_Number").toString();
                            hm.put("PO_Number",po);
                        }

                        if (ddo.getDataByName("Vendor_Name") != null) {
                        	vendorName = ddo.getDataByName("Vendor_Name").toString();
                        	hm.put("Vendor_Name",vendorName);
                        }

                        if (ddo.getDataByName("Vendor_Number") != null) {
                        	vendorNum = ddo.getDataByName("Vendor_Number").toString();
                        	hm.put("Vendor_Number",vendorNum);
                        }

                        if (ddo.getDataByName("Invoice_Num") != null) {
                        	vendorNum = ddo.getDataByName("Invoice_Num").toString();
                        	hm.put("Invoice_Num",vendorNum);
                        }

                        if (ddo.getDataByName("Invoice_Amount") != null) {
                        	amount = ddo.getDataByName("Invoice_Amount").toString();
                        	hm.put("Invoice_Amount",amount);
                        }
                        
                        if (ddo.getDataByName("Invoice_Date") != null) {
                        	invoiceDate = ddo.getDataByName("Invoice_Date").toString();
                        	hm.put("Invoice_Date",invoiceDate);
                        }
                        
                        if (ddo.getDataByName("RejectCode") != null) {
                        	rejectCode = ddo.getDataByName("RejectCode").toString();
                        	hm.put("RejectCode",rejectCode);
                        }*/
                        
                        
                        System.out.println("assignNum = " + assignNum);

                        //Export image and xml for Kofax
                        exportImage(dsICM,ddo,assignNum);
                        generateXML(assignNum,specialHandlingCode);
                        
                        dsICM.checkIn(ddo);
                        routingService.continueProcess(workPackage.getPidString(), "Archive", "ICMADMIN");
                        System.out.println("After Archive.");
                        System.out.println("Update "+assignNum+" record from ExportAriba.");
                        
                        updateFromExportAriba(assignNum);
                        System.out.println("<------End process.----->");
                    }
                }//end while
                ddo = null;
                workPackage = null;
                iter = null;
                routingService = null;
                System.out.println("<------End while loop.----->");
            }//end else	
        } catch (Exception exc) {
            System.out.println("monitorExportToKofaxWorkList: Error in Exception " + exc);
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } finally {
            if (dsICM != null) {
                try {
                    ICMConnectionPool.returnConnection(dsICM);
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("Error returning connection to the pool." + e.getMessage());
                }
            }
        }//end final
    } //end monitorNewWorkList	
    
    public void monitorChangeItemTypeWorkList() {
        //System.out.println(" inside changeItemTypeWorkList & m_TargetItemType..."+m_TargetItemType);

        DKDatastoreICM dsICM = null;
        
        try {
            dsICM = new DKDatastoreICM();
            dsICM = ICMConnectionPool.getConnection(m_userName, m_password, m_serverName);
//	    	dsICM.connect(m_serverName, m_userName, m_password, "SCHEMA=ICMADMIN");
            DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
            com.ibm.mm.sdk.server.DKDatastoreExtICM dsExtICM = (com.ibm.mm.sdk.server.DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
            dkCollection workPackages = routingService.listWorkPackages("Ready To Change IT", "");
            DKDDO ddoSource = null;
            DKDDO ddoTarget = null;
            if (workPackages.cardinality() == 0) {
                workPackages = null;
            } else {
            	
            	System.out.println("Number of Ariba Invoice items " + workPackages.cardinality());
                dkIterator iter = workPackages.createIterator();
                DKWorkPackageICM workPackage;
                
                String pid = "";

                DKAttrDefICM aDef;
                Object aObject;
                String attrName = null;
                String attrValue = null;
                int n = 0;
                int count = 0;
              
                short dataId;
                while (iter.more()) {
                	
                    workPackage = (DKWorkPackageICM) iter.next();
                    
                    pid = workPackage.getPidString();
                    
                    ddoSource = dsICM.createDDO(workPackage.getItemPidString());
                    //Create a DDO for Targett of Document Model
                    ddoTarget = dsICM.createDDO("SAPInvoices_Removed", DKConstant.DK_CM_DOCUMENT);
               	
                    System.out.println("Retreive and set attributes to new item type.");
                    
                	DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
                	DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity("SAP_Ariba_Invoices");
                    DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
                    dkIterator iter1 = allAttrs.createIterator();
                    
                    ddoSource.retrieve(com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_ITEMTREE_NO_LINKS | com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_CHILDREN | com.ibm.mm.sdk.common.DKConstant.DK_CM_CHECKOUT);
                    while(iter1.more()){ // for all attributes of the item
                    	//System.out.println("@@@@@@@@@@@@starting attr@@@@@@@@@@@@@@@@@@");

            			aDef = (DKAttrDefICM) iter1.next();
            			aObject = null;
            			
            			try {
            					attrName = aDef.getName();
            					//System.out.println("attrName ="+attrName);
            					if (attrName.equalsIgnoreCase("AssignmentNumber") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						attrName = "Tx_Number";
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						System.out.println("Set AssignmentNumber = "+attrValue);
            					/*}else if (attrName.equalsIgnoreCase("Invoice_Number") && ddoSource.getDataByName(attrName)!=null) {
                						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Num";
            						System.out.println("Invoice_Num");
            						
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					*/	
            					}else if (attrName.equalsIgnoreCase("RejectCode") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						attrName = "Queue";
            						System.out.println("Set Queue");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            							
            					}else if (attrName.equalsIgnoreCase("Comments") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Notes";
            						System.out.println("set Notes");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					
            					}else if (attrName.equalsIgnoreCase("Invoice_Date") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Date";
            						System.out.println("set Invoice_Date");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						
            					}else if (attrName.equalsIgnoreCase("PO_Number") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "PO_Number";
            						System.out.println("set PO_Number");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						
            					}else if (attrName.equalsIgnoreCase("Invoice_Currenc") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Currenc";
            						System.out.println("set Invoice_Currenc");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						
            					}else if (attrName.equalsIgnoreCase("Tax_Amount") && ddoSource.getDataByName(attrName)!=null) {
            						if (attrValue!=null & attrValue.length()>0) {
        					            BigDecimal bd = new BigDecimal(attrValue);
        					            System.out.println("set Tax_Amount = "+bd);
        					            ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), bd);
        					        } 
            						
            					}else if (attrName.equalsIgnoreCase("Buyer_Email_Add") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Buyer_Email_Add";
            						System.out.println("set Buyer_Email_Add");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					
            					}else if (attrName.equalsIgnoreCase("SAMAccountName") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "SAMAccountName";
            						System.out.println("set SAMAccountName");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					
            					}else if(ddoSource.getDataByName(attrName)!=null){
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						
            						System.out.println("monitorchangeItemTypeWorkList attrValue ="+attrName+attrValue);
            					//if(!attrName.equals("CartonId") && !attrName.equals("Notes") && !attrName.equals("InvoiceDate") && !attrName.equals("TotalDue"))
            					//{
            						switch (aDef.getType()) {
            				        case DKConstant.DK_CM_DATAITEM_TYPE_DATE :
            				        	ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue)); //"2001-08-12"
            				          break;
            				        case DKConstant.DK_CM_DATAITEM_TYPE_TIME :
            				        	ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Time.valueOf(attrValue));
            				          break;
            				        case DKConstant.DK_CM_DATAITEM_TYPE_TIMESTAMP : // "2001-08-12 10:00:00.123456"
            				    		System.out.println("DKConstant.DK_CM_DATAITEM_TYPE_TIMESTAMP");
            				        	ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Timestamp.valueOf(attrValue));
            						     
            						  break;
            				        case DKConstant.DK_CM_DATAITEM_TYPE_SHORT : // 
            				        	ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.lang.Long.valueOf(attrValue));
            				          break;
            				        case DKConstant.DK_CM_DATAITEM_TYPE_LONG : // 
            				        	ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.lang.Long.valueOf(attrValue));
            				          break;
            				        case DKConstant.DK_CM_DATAITEM_TYPE_FLOAT : // 
            				        	ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.lang.Double.valueOf(attrValue));
            				          break;
            				        case DKConstant.DK_CM_DATAITEM_TYPE_DECIMAL : // 
            					        if (attrValue!=null & attrValue.length()>0) {
            					            BigDecimal bd = new BigDecimal(attrValue);
            					            System.out.println("set bd = "+bd);
            					            System.out.println("set attrName = "+attrName+ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName));           					           
            					            ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), bd);
            					        } 
            				          break;
            				        default :
            				    		System.out.println("default");
            				        	ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            				        	
            						}
            						
        						attrValue = "";
            					}
            			} catch (Exception exc) {
            				System.out.println("exc...="+exc.getMessage());
            			}
            		}
                    
                    // Create a DDO for Targett of Document Model
                    dsExtICM.moveObject(ddoSource,ddoTarget, DKConstant.DK_CM_CHECKIN+DKConstant.DK_CM_KEEP_IN_AUTOFOLDER);
                    System.out.println("After move Item Type.");
                    routingService.continueProcess(workPackage.getPidString(), "Terminate", "ICMADMIN");
                    
                    //}
                   
                }//end while
                ddoSource = null;
                ddoTarget = null;
                workPackage = null;
                iter = null;
                routingService = null;
            }//end else	
        } catch (Exception exc) {
            System.out.println("monitorChangeItemTypeWorkList: Error in Exception " + exc);
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } finally {
            if (dsICM != null) {
                try {
                    ICMConnectionPool.returnConnection(dsICM);
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("Error returning connection to the pool." + e.getMessage());
                }
            }
        }//end final
    } //end monitorChangeItemTypeWorkList	

}
