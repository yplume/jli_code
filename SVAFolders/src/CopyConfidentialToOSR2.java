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
public class CopyConfidentialToOSR2 {

    private String m_BaseURL = "";
    //private DKDatastoreICM m_dsICM = null;
    private DKDocRoutingServiceICM m_RoutingService;
    private String m_serverName;
    private String m_userName;
    private String m_password;
    private ResultSet rs = null;
    private ResultSet rs1 = null;
    private Statement stmt1 = null;
    private Statement stmt = null;
    private Connection conn = null;
    private Connection conn1 = null;
    
   
    //private String m_OutPutPath;
    //private String m_sqlServerDBName;
    
    
    /*public AribaMediator(String cmServerName, String cmUserName, String cmPassword, String SQLServer) {
        this(cmServerName, cmUserName, cmPassword, SQLServer);
    }*/
    
    public static void main(String[] args) 
    {
    	System.out.println("Calling Main ...");
    	CopyConfidentialToOSR2 md = new CopyConfidentialToOSR2();
    	   	
    }
    
    /*public MoveDocs(String cmServerName, String cmUserName, String cmPassword, String outPutPath, String sqlDatabase) {
        
        m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        m_OutPutPath = outPutPath;
        m_sqlServerDBName = sqlDatabase;
    }*/

    public CopyConfidentialToOSR2() {
    	System.out.println("Calling MoveDocs ...");
    	m_serverName = "icmnlsdb";
        m_userName = "icmadmin";
        m_password = "Bigblue123";
        //String SOURCE_ITEM_TYPE = "OSR_Addendums";
        String SOURCE_ITEM_TYPE = "OSR_Confidential";
		String TARGET_ITEM_TYPE = "OSR2";
		
		//MergeItemType(SOURCE_ITEM_TYPE, TARGET_ITEM_TYPE);
		copyToOSR(SOURCE_ITEM_TYPE, TARGET_ITEM_TYPE);
	}
 
    /*
    public void MergeItemType(String sDDO, String tDDO) {
        //System.out.println(" inside changeItemTypeWorkList & m_TargetItemType..."+m_TargetItemType);
    	String osrPid = null;
        DKDatastoreICM dsICM = null;
		DKDatastoreExtICM dsExtICM = null;
		
        try {
        	dsICM = new DKDatastoreICM();
            //dsICM.connect("icmnlsdb", "icmadmin", "BigBlue1", "SCHEMA=ICMADMIN");
            dsICM.connect("icmnlsdb", "icmadmin", "BigBlue123", "SCHEMA=ICMADMIN");
            System.out.println("Calling connect1 ...");
            // Get the datastore extension object
            //
            dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT);
            System.out.println("Calling connect2 ...");
            
            StringBuffer query = new StringBuffer();
            query.append("//OSR_Confidential");
            query.append("[@ITEMID=\"");
            query.append("\" AND @VERSIONID=latest-version(.)");
            query.append("]");           

            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            DKNVPair options[] = new DKNVPair[3];
            options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0");                                            // Max number of search results. 0 means no max.
            options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_YES));
    	    options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);   	    

            System.out.println("Evaluating Query:  " + query.toString());
            DKResults results = (DKResults)dsICM.evaluate(query.toString(), DKConstant.DK_CM_XQPE_QL_TYPE, options);
            
            dkRetrieveOptions.resourceContent(true);
            
            dkIterator iter = results.createIterator();
			 
            System.out.println("results ="+results.cardinality());
            
            DKDDO itemDDO = (DKDDO)iter.next();
            	
////////////////////Move Items//////////////////
            DKDDO ddoTarget = dsICM.createDDO(tDDO, DKConstant.DK_CM_DOCUMENT);
            System.out.println("Create target DDO");
            DKAttrDefICM aDef;
            Object aObject;
            String attrName = null;
            String attrValue = null;
            System.out.println("retrive source DDO");
            
            itemDDO.retrieve();
			DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
            DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity("OSR_Confidential");
            DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
            iter = allAttrs.createIterator();
            
            while(iter.more()){ // for all attributes of the item
 					aDef = (DKAttrDefICM) iter.next();
					aObject = null;
					try {
							attrName = aDef.getName();
						
							System.out.println("attrName ="+attrName);
							
        					if (attrName.equalsIgnoreCase("CC_FirstName") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_FirstName";
        						System.out.println("Set CC_FirstName");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("CC_LastName") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_LastName";
        						System.out.println("Set CC_LastName");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("IDNumber") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "IDNumber";
        						System.out.println("Set IDNumber");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}
        				
        			} catch (Exception exc) {
        				System.out.println("exc...="+exc.getMessage());
        			}
        		}
	        ////////Copy docs/////////
	      //copying documents
	        short dataid = itemDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
	       	short dataid1 = ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 

			if(dataid>0){ // If parts exist, continue
				
	            DKParts dkParts = (DKParts) itemDDO.getData(dataid); 
   		        System.out.println("dkParts# = "+dkParts.cardinality());
   		        DKParts dkParts1 = (DKParts) ddoTarget.getData(dataid1); 
		      
	            if(dkParts==null)
	                throw new Exception("DKParts attribute is null but the dataid exists.  It may have not been retrieve with the correct retrieval options.  Item Info: ITEMID = '"+((DKPidICM)itemDDO.getPidObject()).getItemId()+"', Object Type = '"+((DKPidICM)itemDDO.getPidObject()).getObjectType());
	            // Go Through Contents
	            
	            dkIterator iter0 = dkParts.createIterator();
	            
	            System.out.println("Document Had '"+dkParts.cardinality()+"' Parts.");
	            //OutputStream out = null;
	         
	            while(iter0.more()){
	                // nothing for now
	            	
	            	DKLobICM part = (DKLobICM) iter0.next();
	            	//create part for ddo1
	            	DKLobICM part1 = (DKLobICM)  dsICM.createDDO("ICMBASE",DKConstantICM.DK_ICM_SEMANTIC_TYPE_BASE);
	            	
	            	part.retrieve(dkRetrieveOptions.dkNVPair());
	            	
	            	System.out.println("part.getContent() = "+part.getContent());
	            	
	            	if(part.getContent()!=null){
		            	System.out.println("Inside part......");
		            
		            	String mimeType = part.getMimeType();
		            	part1.setMimeType(mimeType);
		            	part1.setContent(part.getContent());
	                
		                dkParts1.addElement(part1);
		                System.out.println("part1().getObjectType == "+part1.getPidObject().getObjectType());      
	            	}else{
	            		System.out.println("Missing Image; itemDDO = "+itemDDO.getPidObject().toString());
	            	      
	            	}
	            	part=null;
	            	
	            	part1=null;
	            	
	            }
	            dkParts1=null;
	            System.out.println("ddoTarget add");
	            ddoTarget.add();
	            
	            dkParts=null;
		        ////////end copy////////
                
                //iter = null;
                //cursor.destroy();
        	    dsICM.disconnect();
                dsICM.destroy();       
                System.out.println("Move Item Type is Done!.");
            
	        }else {
	            System.out.println("loopThruItems: Could not find item.");
			}
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
    */
    String copyToOSR(String sourceitemID, String sDDO, String tDDO){
    	System.out.println("Inside copy to osr.");
    	DKDatastoreICM dsICM = null;
		DKDatastoreExtICM dsExtICM = null;
		System.out.println("Create target DDO");
        DKAttrDefICM aDef;
        Object aObject;
        String attrName = null;
        String attrValue = null;
        System.out.println("retrive source DDO");
        dkIterator iter = null;
        DKDDO  itemDDO;
        String pid = null;
        
        try {
        	dsICM = new DKDatastoreICM();
            dsICM.connect("icmnlsdb", "icmadmin", "BigBlue123", "SCHEMA=ICMADMIN");
            
            // Get the datastore extension object
            //
            dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT);

            // Set query params
            //
 //           DKResults results =null;
            
            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            
            DKNVPair options[] = new DKNVPair[3];
            options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0");                                            // Max number of search results. 0 means no max.
            options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_IDONLY));   // Retrieve only item id from server
            options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);                                           // Must mark the end of the NVPair
            // Search for ALL documents in source item-type
            StringBuffer query = new StringBuffer();
            query.append("//OSR_Confidential");
            query.append("[@ITEMID=\"");
            query.append(sourceitemID);
            query.append("\" AND @VERSIONID=latest-version(.)");
            query.append("]"); 
            dkRetrieveOptions.resourceContent(true);
            dkResultSetCursor cursor = dsICM.execute(query.toString(), DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
            while ( (itemDDO = cursor.fetchNext()) != null){ // for all attributes of the item
				// create an empty ddo
            	DKDDO ddoTarget = dsICM.createDDO(tDDO, DKConstant.DK_CM_DOCUMENT);
				itemDDO.retrieve();
				DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
 	            DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity(sDDO);
 	            DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
 	            iter = allAttrs.createIterator();
 	            //DKSequentialCollection compTypes = (DKSequentialCollection) entityDef.listSubEntities();
				//dkIterator iter2 = compTypes.createIterator();
				
                //String[] subentity = null;
                //int numSubAttr = 0;
                while(iter.more()){ // for all attributes of the item
 					aDef = (DKAttrDefICM) iter.next();
					aObject = null;
					try {
							attrName = aDef.getName();
						
							//attrName = aDef.getName();
							System.out.println("attrName ="+attrName);
							System.out.println("itemDDO.getDataByName(attrName)="+itemDDO.getDataByName(attrName));
					
        					if (attrName.equalsIgnoreCase("IDNumber") && itemDDO.getDataByName(attrName)!=null) {
        						System.out.println("inside IDNumber");
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_PrimaryID";
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue);
        						System.out.println("Set OSR_PrimaryID = "+attrValue);
        					
        					}else if (attrName.equalsIgnoreCase("CC_FirstName") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_FirstName";
        						System.out.println("Set CC_FirstName");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("CC_LastName") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_LastName";
        						System.out.println("Set CC_LastName");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}
        					ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_SectionTitle"), ""); // basic string type
        					
        				
        			} catch (Exception exc) {
        				System.out.println("exc...="+exc.getMessage());
        			}
        		}
	        ////////Copy docs/////////
	      //copying documents
	        short dataid = itemDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
	       	short dataid1 = ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 

			if(dataid>0){ // If parts exist, continue
				
	            DKParts dkParts = (DKParts) itemDDO.getData(dataid); 
   		        System.out.println("dkParts# = "+dkParts.cardinality());
   		        DKParts dkParts1 = (DKParts) ddoTarget.getData(dataid1); 
		      
	            if(dkParts==null)
	                throw new Exception("DKParts attribute is null but the dataid exists.  It may have not been retrieve with the correct retrieval options.  Item Info: ITEMID = '"+((DKPidICM)itemDDO.getPidObject()).getItemId()+"', Object Type = '"+((DKPidICM)itemDDO.getPidObject()).getObjectType());
	            // Go Through Contents
	            
	            dkIterator iter0 = dkParts.createIterator();
	            
	            System.out.println("Document Had '"+dkParts.cardinality()+"' Parts.");
	            
	            while(iter0.more()){
	                // nothing for now
	            	
	            	DKLobICM part = (DKLobICM) iter0.next();
	            	//create part for ddo1
	            	DKLobICM part1 = (DKLobICM)  dsICM.createDDO("ICMBASE",DKConstantICM.DK_ICM_SEMANTIC_TYPE_BASE);
	            	part.retrieve(dkRetrieveOptions.dkNVPair());
	            	//part.retrieve(DKConstant.DK_CM_CONTENT_YES);
	                //DKDDO contentDDO = (DKDDO)iter.next();
	            	
	            	// create target ddo att
	            	mimeType = part.getMimeType();
	            	part1.setMimeType(mimeType);
	            	part1.setContent(part.getContent());
                    dkParts1.addElement(part1);
	            	
	                System.out.println("part1().getObjectType= "+part1.getPidObject().getObjectType());      
	            }
	            ddoTarget.add();
	            pid = ddoTarget.getPidObject().getIdString();
	            pid = pid.substring(3, 29);
				
				System.out.println("PID = " + pid);
				
				
				}
        	}
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
        return pid;
    }
}

