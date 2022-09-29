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
public class CopyDocs {

    private String m_BaseURL = "";
    //private DKDatastoreICM m_dsICM = null;
    private DKDocRoutingServiceICM m_RoutingService;
    private String m_serverName;
    private String m_userName;
    private String m_password;
    //private String m_OutPutPath;
    //private String m_sqlServerDBName;
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    //private String url = ("jdbc:db2://win2012jl:50000/icmnlsdb");
    private String url = ("jdbc:db2://USETHQDMCM85:50000/icmnlsdb");
    static String startID = "";
    static String endID = "";
    
    
    /*public AribaMediator(String cmServerName, String cmUserName, String cmPassword, String SQLServer) {
        this(cmServerName, cmUserName, cmPassword, SQLServer);
    }*/
    
    public static void main(String[] args) 
    {
    	System.out.println("Calling Main ...");
    	startID = args[0];
    	endID = args[1];
    	
    	CopyDocs md = new CopyDocs();
    	   	
    }
    
    /*public MoveDocs(String cmServerName, String cmUserName, String cmPassword, String outPutPath, String sqlDatabase) {
        
        m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        m_OutPutPath = outPutPath;
        m_sqlServerDBName = sqlDatabase;
    }*/

    public CopyDocs() {
    	System.out.println("Calling MoveDocs ...");
    	//m_serverName = "icmnlsdb";
        //m_userName = "icmadmin";
        //m_password = "Bigblue1";
        m_serverName = "icmsvadb";
        m_userName = "icmadmin";
        m_password = "Icm8doc6";
        //String SOURCE_ITEM_TYPE = "Officers";
        String SOURCE_ITEM_TYPE = "OSR_HS";
		String TARGET_ITEM_TYPE = "OSR";
		//MergeItemType(SOURCE_ITEM_TYPE, TARGET_ITEM_TYPE);
		System.out.println("............................................. MergeItemType  ...................................................");
		MergeItemType(SOURCE_ITEM_TYPE, TARGET_ITEM_TYPE, "A1001001A19I13A94001E00000","A1001001A19I13A95143G00040");
		System.out.println("............................................. MergeItemType000000000000000000000  ...................................................");
		MergeItemType(SOURCE_ITEM_TYPE, TARGET_ITEM_TYPE, "A1001001A19I13A95143G00040","A1001001A19I13B00239C00056");
	    }
 
    public void MergeItemType(String sDDO, String tDDO, String startID, String endID) {
        //System.out.println(" inside changeItemTypeWorkList & m_TargetItemType..."+m_TargetItemType);

        DKDatastoreICM dsICM = null;
		DKDatastoreExtICM dsExtICM = null;
		
        try {
        	dsICM = new DKDatastoreICM();
            //dsICM.connect("icmnlsdb", "icmadmin", "BigBlue1", "SCHEMA=ICMADMIN");
        	dsICM.connect("icmnlsdb", "icmadmin", "Bigblue1", "SCHEMA=ICMADMIN");
        	//dsICM.connect("icmsvadb", "icmadmin", "Icm8doc6", "SCHEMA=ICMADMIN");
            
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
            //
            dkRetrieveOptions.resourceContent(true);
            
            System.out.println("Before Query ...");
            String query = "/" + sDDO +"[@ITEMID >\""+ startID+"\" AND @ITEMID<=\""+endID+"\"]";
            
            //String query = "/" + sDDO;
            //String query = "/" + sDDO +"[@ITEMID >=\"A1001001A18D09B43212H11075\" AND @ITEMID<=\"A1001001A18H17B35853C15347\"]";
            //String query = "/" + sDDO +"[@ITEMID >\"A1001001A18E03B35929H08551\" AND @ITEMID<=\"A1001001A18E17B11101J15295\"]";
            //String query = "/" + sDDO +"[@ITEMID >\""+ startID+"\" AND @ITEMID<=\""+endID+"\"]";
            
            //String query = "/" + sDDO +"[@ITEMID >\"A1001001A18H17B35853C15347\"]";
            System.out.println("After Query ..."+query);
            //String query = "/" + sDDO +"[@Off_Document =\"test\"]";
           
            dkResultSetCursor cursor = dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
            
            dkIterator iter = null;
 
            	
////////////////////Move Items//////////////////
            //DKDDO ddoTarget = dsICM.createDDO("OSR", DKConstant.DK_CM_DOCUMENT);
            System.out.println("Create target DDO");
            DKAttrDefICM aDef;
            Object aObject;
            String attrName = null;
            String attrValue = null;
            System.out.println("retrive source DDO");
            
            DKDDO  itemDDO;
            /*itemDDO.retrieve();
			DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
            DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity("OSR");
            DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
            iter = allAttrs.createIterator();
            System.out.println("retrive source DDO att");*/
            
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
					
        					/* for Officers\OS&R - Closed Cases  item type
        					 * if (attrName.equalsIgnoreCase("Off_Document") && itemDDO.getDataByName(attrName)!=null) {
        						System.out.println("inside Off_Document");
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_LastName";
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); //"2001-08-12"
        						System.out.println("Set CC_LastName = "+attrValue);
        					}else if (attrName.equalsIgnoreCase("Off_Description") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_FirstName";
        						System.out.println("Set CC_FirstName");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("Off_ScanDate") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = ddoSource.getDataByName(attrName);
        						attrName = "CC_ScannedCaseDate";
        						System.out.println("set CC_ScannedCaseDate");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue));
        						//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					
        					}*//*else if (attrName.equalsIgnoreCase("KfxScannedByNew") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = ddoSource.getDataByName(attrName);
        						attrName = "KfxScannedByNew";
        						System.out.println("set KfxScannedByNew");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					
        					}else */
							//for OSR Health Service item type
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
							}else if (attrName.equalsIgnoreCase("OSR_HS_MaidenName") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_MaidenName";
        						System.out.println("Set CC_MaidenName");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
							/*}else if (attrName.equalsIgnoreCase("OSR_HS_MaidenName") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_MiddleInitial";
        						System.out.println("Set CC_MiddleInitial");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
*/        					}else if (attrName.equalsIgnoreCase("ScanDate") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_ScannedCaseDate";
        						System.out.println("Set CC_ScannedCaseDate");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue)); // basic string type
        					}else if (attrName.equalsIgnoreCase("OSR_PrimaryID") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_PrimaryID";
        						System.out.println("Set OSR_PrimaryID");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("OSR_SecondID") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_SecondID";
        						System.out.println("Set OSR_SecondID");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}
							ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"OSR_SectionTitle"), "HEALTH"); // basic string type
							//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"OSR_SectionTitle"), "GENERAL"); // basic string type
							
        			} catch (Exception exc) {
        				System.out.println("exc...="+exc.getMessage());
        			}
        			//add new item as General section title
        			//attrName = "OSR_SectionTitle";
        			//System.out.println("Set OSR_SectionTitle");
        			//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), "GENERAL"); 
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
	            	System.out.println("Inside iter0.more()");
	            	DKLobICM part = (DKLobICM) iter0.next();
	            	//create part for ddo1
	            	DKLobICM part1 = (DKLobICM) dsICM.createDDO("ICMBASE",DKConstantICM.DK_ICM_SEMANTIC_TYPE_BASE);
	            	
	            	part.retrieve(dkRetrieveOptions.dkNVPair());
	            	//part.retrieve(DKConstant.DK_CM_CONTENT_YES);
	                //DKDDO contentDDO = (DKDDO)iter.next();
	            	System.out.println("part.getContent() = "+part.getContent());
	            	
	            	if(part.getContent()!=null){
		            	System.out.println("Inside part......");
		            	
		            	// create target ddo att
		            	String mimeType = part.getMimeType();
		            	//part1.setMimeType("image/tiff");
		            	System.out.println("Inside mimeType......"+mimeType);
		            	part1.setMimeType(mimeType);
		            	part1.setContent(part.getContent());
	                //InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(),-1,-1); 
	                /*try {
	                while ((read = inputStream.read(bytes)) != -1) {
	            		out.write(bytes, 0, read);
	            	//	imageSize+=read;
	            	}
	                }catch(Exception e){
	                   	System.out.println("Exception in read:"+e.getMessage());
	                   }
		            	*/
		                dkParts1.addElement(part1);
		               
		                System.out.println("part1().getObjectType= "+part1.getPidObject().getObjectType());      
	            	}else{
	            		System.out.println("Missing Image; itemDDO = "+itemDDO.getPidObject().toString());
	            		String sql = "insert into ICMADMIN.MISSIMGS (ITEMOBJ, ERRMSG) VALUES ('" + itemDDO.getPidObject().toString() + "', '"+attrValue+"')";
	            		conn = DriverManager.getConnection(url, "icmadmin", "Bigblue1");
	            		stmt = conn.createStatement();
	            		stmt.executeUpdate(sql);
	            	    System.out.println("After sql");
	            	      
	            	}
	            	part=null;
	            	
	            	part1=null;
	            	
	            	dkParts1=null;
	            	
	            }
	            iter0=null;
//ddo.setContentFromClientFile.add();
	         //ddoTarget.add();
	            
	            ddoTarget.add();
	            
	            dkParts=null;
	            dkParts1=null;
				}
        	}
   // }
            	//ddoSource = null;
                //ddoTarget = null;
                
                //iter = null;
                //cursor.destroy();
        	    dsICM.disconnect();
                dsICM.destroy();   
                cursor.destroy();
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

