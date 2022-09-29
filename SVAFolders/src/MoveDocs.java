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
import com.ibm.mm.sdk.client.*;
import com.ibm.mm.sdk.server.*;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.ibm.mm.sdk.server.DKDatastoreExtICM;
/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MoveDocs {

    private String m_BaseURL = "";
    //private DKDatastoreICM m_dsICM = null;
    private DKDocRoutingServiceICM m_RoutingService;
    private String m_serverName;
    private String m_userName;
    private String m_password;
    //private String m_OutPutPath;
    //private String m_sqlServerDBName;
    
    
    /*public AribaMediator(String cmServerName, String cmUserName, String cmPassword, String SQLServer) {
        this(cmServerName, cmUserName, cmPassword, SQLServer);
    }*/
    
    public static void main(String[] args) 
    {
    	System.out.println("Calling Main ...");
    	MoveDocs md = new MoveDocs();
    	   	
    }
    
    /*public MoveDocs(String cmServerName, String cmUserName, String cmPassword, String outPutPath, String sqlDatabase) {
        
        m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        m_OutPutPath = outPutPath;
        m_sqlServerDBName = sqlDatabase;
    }*/

    public MoveDocs() {
    	System.out.println("Calling MoveDocs ...");
    	m_serverName = "icmnlsdb";
        m_userName = "icmadmin";
        m_password = "Bigblue1";
        String SOURCE_ITEM_TYPE = "OSR_Addendums";
		String TARGET_ITEM_TYPE = "OSR";
		
    	MergeItemType(SOURCE_ITEM_TYPE, TARGET_ITEM_TYPE);
    }
 
    public void MergeItemType(String sDDO, String tDDO) {
        //System.out.println(" inside changeItemTypeWorkList & m_TargetItemType..."+m_TargetItemType);

        DKDatastoreICM dsICM = null;
		DKDatastoreExtICM dsExtICM = null;
		
        try {
        	dsICM = new DKDatastoreICM();
            //dsICM.connect("icmnlsdb", "icmadmin", "BigBlue1", "SCHEMA=ICMADMIN");
            dsICM.connect("icmnlsdb", "icmadmin", "Bigblue1", "SCHEMA=ICMADMIN");
            
            // Get the datastore extension object
            //
            dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT);

            // Set query params
            //
 //           DKResults results =null;
            DKNVPair options[] = new DKNVPair[3];
            options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0");                                            // Max number of search results. 0 means no max.
            options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_IDONLY));   // Retrieve only item id from server
            options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);                                           // Must mark the end of the NVPair
            // Search for ALL documents in source item-type
            //
            String query = "/" + sDDO;
            dkResultSetCursor cursor = dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
            DKAttrDefICM aDef;
            Object aObject;
            String attrName = null;
            String attrValue = null;
//            int n = 0;
            int count = 0;
			//int move_count = 0;
			//if (moveNumber.equalsIgnoreCase("all"))
			//	move_count = 100000;
			//else
			//	move_count = Integer.parseInt(moveNumber);
            // Iterate through hit list to move each document
            //
            DKDDO  ddoSource;
            DKDDO  ddoTarget;
            dkIterator iter = null;
 //           System.out.println("number of docs ready to move ="+cursor.cardinality());      
 //			results = (DKResults)dsICM.evaluate(query, DKConstant.DK_CM_XQPE_QL_TYPE, options);
 //			dkIterator iter1 = results.createIterator();
            // Create emtpy ddo object in target itemtype
            //
            //DKDDO ddo2 = dsICM.createDDO(TARGET_ITEM_TYPE, DKConstant.DK_CM_DOCUMENT);
			while ( (ddoSource = cursor.fetchNext()) != null) //for each row (document)
            { 
//				ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
//				ddo.retrieve(DKConstant.DK_CM_CONTENT_YES);
				// create an empty ddo
				ddoTarget = dsICM.createDDO(tDDO, DKConstant.DK_CM_DOCUMENT);
				
				ddoSource.retrieve();
				DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
 	            DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity(sDDO);
 	            DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
 	            iter = allAttrs.createIterator();
 	            //DKSequentialCollection compTypes = (DKSequentialCollection) entityDef.listSubEntities();
				//dkIterator iter2 = compTypes.createIterator();
				
                String[] subentity = null;
                int numSubAttr = 0;
                while(iter.more()){ // for all attributes of the item
 					aDef = (DKAttrDefICM) iter.next();
					aObject = null;
					try {
						attrName = aDef.getName();
						
            					//System.out.println("attrName ="+attrName);
            					if (attrName.equalsIgnoreCase("CC_LastName") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						attrName = "CC_LastName";
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						System.out.println("Set CC_LastName = "+attrValue);
            					/*}else if (attrName.equalsIgnoreCase("Invoice_Number") && ddoSource.getDataByName(attrName)!=null) {
                						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Num";
            						System.out.println("Invoice_Num");
            						
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					*/	
            					}else if (attrName.equalsIgnoreCase("CC_FirstName") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						attrName = "CC_FirstName";
            						System.out.println("Set CC_FirstName");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            							
            					}else if (attrName.equalsIgnoreCase("CC_MiddleInitial") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "CC_MiddleInitial";
            						System.out.println("set CC_MiddleInitial");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            					
            					}else if (attrName.equalsIgnoreCase("OSR_SectionTitle") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "OSR_SectionTitle";
            						System.out.println("set OSR_SectionTitle");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						
            					}else if (attrName.equalsIgnoreCase("KfxScannedBy") && ddoSource.getDataByName(attrName)!=null) {
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						//aObject = ddoSource.getDataByName(attrName);
            						attrName = "KfxScannedBy";
            						System.out.println("set KfxScannedBy");
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						
            					}else if(ddoSource.getDataByName(attrName)!=null){
            						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						
            						System.out.println("MergeItemType attrValue ="+attrName+attrValue);
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
                	if(!dsExtICM.isCheckedOut(ddoSource))
                		dsICM.checkOut(ddoSource);
                    // Create a DDO for Targett of Document Model
                    dsExtICM.moveObject(ddoSource,ddoTarget, DKConstant.DK_CM_CHECKIN+DKConstant.DK_CM_KEEP_IN_AUTOFOLDER);
                    System.out.println("After move Item Type.");
                    
                    
                    //}
                   
                }//end while
                ddoSource = null;
                ddoTarget = null;
                
                iter = null;
                cursor.destroy();
        	    dsICM.disconnect();
                dsICM.destroy();       
                System.out.println("Move Item Type is Done!.");
        } catch (Exception exc) {
            System.out.println("MergeItemType: Error in Exception " + exc);
            //ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } finally {
            if (dsICM != null) {
                try {
                    //ICMConnectionPool.returnConnection(dsICM);
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("Error in finally." + e.getMessage());
                }
            }
        }//end final
    }
}

