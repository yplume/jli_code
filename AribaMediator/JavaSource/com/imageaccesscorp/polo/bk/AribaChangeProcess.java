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


import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.DKDatastoreICM;

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AribaChangeProcess {

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

    public AribaChangeProcess(String cmServerName, String cmUserName, String cmPassword) {
        
        m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        AribaMediator();
    }

    public void AribaMediator() {
    	System.out.println("Calling ChangeProcess ...");
    
    	monitorChangeItemTypeWorkList();
    	monitorChangeItemTypeWorkListCOGS();
    }
    
        
    public void monitorChangeItemTypeWorkList() {
        //System.out.println(" inside changeItemTypeWorkList & m_TargetItemType..."+m_TargetItemType);

        DKDatastoreICM dsICM = null;
        
        try {
            dsICM = new DKDatastoreICM();
            dsICM = ICMConnectionPool.getConnection(m_userName, m_password, m_serverName);
//	    	dsICM.connect(m_serverName, m_userName, m_password, "SCHEMA=ICMADMIN");
            DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
            com.ibm.mm.sdk.server.DKDatastoreExtICM dsExtICM = (com.ibm.mm.sdk.server.DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
            //dkCollection workPackages = routingService.listWorkPackages("NONPO MDM", "");
            dkCollection workPackages = routingService.listWorkPackages("NONPO MDM", "");
            DKDDO ddoSource = null;
            DKDDO ddoTarget = null;
            if (workPackages.cardinality() == 0) {
                workPackages = null;
            } else {
            	
            	System.out.println("Number of SAP Invoice items " + workPackages.cardinality());
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
                //for each item in worklist
                while (iter.more() && count<5){
                	
                    workPackage = (DKWorkPackageICM) iter.next();
                    
                    pid = workPackage.getPidString();
                    
                    ddoSource = dsICM.createDDO(workPackage.getItemPidString());
                    //Create a DDO for Targett of Document Model
                    ddoTarget = dsICM.createDDO("SAP_Ariba_Invoices", DKConstant.DK_CM_DOCUMENT);
               	
                    System.out.println("Create SAP_Ariba_Invoices");
                    
                	DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
                	DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity("SAP_Invoices");
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
            					if (attrName.equalsIgnoreCase("Tx_Number") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "AssignmentNumber";
            						System.out.println("set Tx_Number ");
            	                  	ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					
            					}else if (attrName.equalsIgnoreCase("Queue") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "RejectCode";
            						System.out.println("set RejectCode");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), "Vendor Add"); // basic string type
            					}else if (attrName.equalsIgnoreCase("Vendor_Name") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Vendor_Name";
            						System.out.println("set Vendor_Name");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					}else if (attrName.equalsIgnoreCase("Vendor_Number") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Vendor_Number";
            						System.out.println("set Vendor_Number");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					}else if (attrName.equalsIgnoreCase("Invoice_Num") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Num";
            						System.out.println("set Invoice_Num");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					}else if (attrName.equalsIgnoreCase("Invoice_Amount") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Amount";
            						System.out.println("set Invoice_Amount");
            						//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						if (attrValue!=null & attrValue.length()>0) {
        					            BigDecimal bd = new BigDecimal(attrValue);
        					            ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), bd);
        					        } 
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
            					}else if (attrName.equalsIgnoreCase("Notes") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Comments";
            						System.out.println("set Comments");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					
            					}
            					attrValue = "";
            			} catch (Exception exc) {
            				System.out.println("exc...="+exc.getMessage());
            			}
            		}//for each attribute end
                    
                    
                    System.out.println("Copying image");
					
                    DKLobICM part = null;
                    //copy image
                    short dataid_S = ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
                    if (dataid_S > 0) {
                        DKParts dkParts = (DKParts) ddoSource.getData(dataid_S);
                        if (dkParts.cardinality() == 0 || dkParts.cardinality() > 1) {
                            // log an error
                        }
                        dkIterator iter3 = dkParts.createIterator();
                        int partCounter = 0;
                        while (iter3.more()) {
                            part = (DKLobICM) iter3.next();
                            part.retrieve();

                            //	mime type must be pdf otherwise convertion from pdf to image will fail
                            if (!part.getMimeType().equalsIgnoreCase("image/tiff")) {			//"application/pdf"
                                System.out.println("readImage: -Exception: Mime type = " + part.getMimeType());
                            }
                            //ii.setImage(part.getContent());
                        }
                    } else {
                        System.out.println("readImage: Could not find item for transaction# = " + attrName.equalsIgnoreCase("Tx_Number"));
                    }
                    
                    DKLobICM base = (DKLobICM) dsICM.createDDO("ICMBASE",DKConstantICM.DK_ICM_PART_BASE);
                    
                    // Setting the MIME Type (type of content)
                    base.setMimeType("image/tiff");
                    
                    DKParts dkParts = (DKParts) ddoTarget.getData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS));
            		System.out.println("Get item DataId");
            		short dataid_T = ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS);
            		if(dataid_T==0)
                        throw new Exception("No DKParts Attribute Found!  DDO is either not a Document Model classified Item or Document has not been explicitly retrieved.");
                        
                    dkParts = (DKParts) ddoTarget.getData(dataid_T); 
                    System.out.println("Save part.");
            		
            		dkParts.addElement(part);
            		ddoTarget.add();
            		dsICM.checkIn(ddoSource);
                    // Create a DDO for Targett of Document Model
                    //dsExtICM.moveObject(ddoSource,ddoTarget, DKConstant.DK_CM_CHECKIN+DKConstant.DK_CM_KEEP_IN_AUTOFOLDER);
                    System.out.println("Start routing.");
                    routingService.startProcess("AribaExceptionProcess", ddoTarget.getPidObject().pidString(), 100, "ICMADMIN");
                    count++;
                    //}
                    
                }//end while
                System.out.println("-------------DONE-------------.");
                ddoSource = null;
                ddoTarget = null;
                workPackage = null;
                iter = null;
                routingService = null;
                
            }//end else	
        } catch (Exception exc) {
            System.out.println("monitorChangeItemTypeWorkList MDM : Error in Exception " + exc);
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } finally {
            if (dsICM != null) {
                try {
                    ICMConnectionPool.returnConnection(dsICM);
                    //dsICM.disconnect();
                    //dsICM.destroy();
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("Error returning connection to the pool." + e.getMessage());
                }
            }
        }//end final
    } //end monitorChangeItemTypeWorkList	

    public void monitorChangeItemTypeWorkListCOGS() {
        //System.out.println(" inside changeItemTypeWorkList & m_TargetItemType..."+m_TargetItemType);

        DKDatastoreICM dsICM = null;
        
        try {
            dsICM = new DKDatastoreICM();
            dsICM = ICMConnectionPool.getConnection(m_userName, m_password, m_serverName);
//	    	dsICM.connect(m_serverName, m_userName, m_password, "SCHEMA=ICMADMIN");
            DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
            com.ibm.mm.sdk.server.DKDatastoreExtICM dsExtICM = (com.ibm.mm.sdk.server.DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
            //dkCollection workPackages = routingService.listWorkPackages("NONPO MDM", "");
            dkCollection workPackages = routingService.listWorkPackages("COGS MDM", "");
            DKDDO ddoSource = null;
            DKDDO ddoTarget = null;
            if (workPackages.cardinality() == 0) {
                workPackages = null;
            } else {
            	
            	System.out.println("Number of SAP Invoice items NonPOWatingForPAApproval " + workPackages.cardinality());
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
                //for each item in worklist
                while (iter.more() && count<5){
                	
                    workPackage = (DKWorkPackageICM) iter.next();
                    
                    pid = workPackage.getPidString();
                    
                    ddoSource = dsICM.createDDO(workPackage.getItemPidString());
                    //Create a DDO for Targett of Document Model
                    ddoTarget = dsICM.createDDO("SAP_Ariba_Invoices", DKConstant.DK_CM_DOCUMENT);
               	
                    System.out.println("Create SAP_Ariba_Invoices");
                    
                	DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
                	DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity("SAP_Invoices");
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
            					if (attrName.equalsIgnoreCase("Tx_Number") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "AssignmentNumber";
            						System.out.println("set Tx_Number ");
            	                  	ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					
            					}else if (attrName.equalsIgnoreCase("Queue") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "RejectCode";
            						System.out.println("set RejectCode");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), "Vendor Add"); // basic string type
            					}else if (attrName.equalsIgnoreCase("Vendor_Name") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Vendor_Name";
            						System.out.println("set Vendor_Name");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					}else if (attrName.equalsIgnoreCase("Vendor_Number") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Vendor_Number";
            						System.out.println("set Vendor_Number");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					}else if (attrName.equalsIgnoreCase("Invoice_Num") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Num";
            						System.out.println("set Invoice_Num");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					}else if (attrName.equalsIgnoreCase("Invoice_Amount") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Amount";
            						System.out.println("set Invoice_Amount");
            						//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						if (attrValue!=null & attrValue.length()>0) {
        					            BigDecimal bd = new BigDecimal(attrValue);
        					            ddoTarget.setData(ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), bd);
        					        } 
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
            					}else if (attrName.equalsIgnoreCase("Notes") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "Comments";
            						System.out.println("set Comments");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					
            					}
            					attrValue = "";
            			} catch (Exception exc) {
            				System.out.println("exc...="+exc.getMessage());
            			}
            		}//for each attribute end
                    
                    
                    System.out.println("Copying image");
					
                    DKLobICM part = null;
                    //copy image
                    short dataid_S = ddoSource.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
                    if (dataid_S > 0) {
                        DKParts dkParts = (DKParts) ddoSource.getData(dataid_S);
                        if (dkParts.cardinality() == 0 || dkParts.cardinality() > 1) {
                            // log an error
                        }
                        dkIterator iter3 = dkParts.createIterator();
                        int partCounter = 0;
                        while (iter3.more()) {
                            part = (DKLobICM) iter3.next();
                            part.retrieve();

                            //	mime type must be pdf otherwise convertion from pdf to image will fail
                            if (!part.getMimeType().equalsIgnoreCase("image/tiff")) {			//"application/pdf"
                                System.out.println("readImage: -Exception: Mime type = " + part.getMimeType());
                            }
                            //ii.setImage(part.getContent());
                        }
                    } else {
                        System.out.println("readImage: Could not find item for transaction# = " + attrName.equalsIgnoreCase("Tx_Number"));
                    }
                    
                    DKLobICM base = (DKLobICM) dsICM.createDDO("ICMBASE",DKConstantICM.DK_ICM_PART_BASE);
                    
                    // Setting the MIME Type (type of content)
                    base.setMimeType("image/tiff");
                    
                    DKParts dkParts = (DKParts) ddoTarget.getData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS));
            		System.out.println("Get item DataId");
            		short dataid_T = ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS);
            		if(dataid_T==0)
                        throw new Exception("No DKParts Attribute Found!  DDO is either not a Document Model classified Item or Document has not been explicitly retrieved.");
                        
                    dkParts = (DKParts) ddoTarget.getData(dataid_T); 
                    System.out.println("Save part.");
            		
            		dkParts.addElement(part);
            		ddoTarget.add();
            		dsICM.checkIn(ddoSource);
                    // Create a DDO for Targett of Document Model
                    //dsExtICM.moveObject(ddoSource,ddoTarget, DKConstant.DK_CM_CHECKIN+DKConstant.DK_CM_KEEP_IN_AUTOFOLDER);
                    System.out.println("Start routing.");
                    routingService.startProcess("AribaExceptionProcess", ddoTarget.getPidObject().pidString(), 100, "ICMADMIN");
                    
                    //}
                    count++;
                    
                }//end while
                System.out.println("-------------DONE COGS-------------.");
                ddoSource = null;
                ddoTarget = null;
                workPackage = null;
                iter = null;
                routingService = null;
                
            }//end else	
        } catch (Exception exc) {
            System.out.println("monitorChangeItemTypeWorkList COGS : Error in Exception " + exc);
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } finally {
            if (dsICM != null) {
                try {
                    ICMConnectionPool.returnConnection(dsICM);
                    //dsICM.disconnect();
                    //dsICM.destroy();
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("Error returning connection to the pool." + e.getMessage());
                }
            }
        }//end final
    } //end monitorChangeItemTypeWorkList	

    public static void main(String[] args) {
    	System.out.println("Start main");
    	AribaChangeProcess ap = new AribaChangeProcess("icmnlsdb", "icmadmin", "BigBlue1");
	}
	

}
