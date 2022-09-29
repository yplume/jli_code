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
public class CopyToConfidential {

    private String m_BaseURL = "";
    //private DKDatastoreICM m_dsICM = null;
    private DKDocRoutingServiceICM m_RoutingService;
    private String m_serverName;
    private String m_userName;
    private String m_password;
    private ResultSet rs = null;
    private ResultSet rs1 = null;
    private Statement stmt1 = null;
    private Statement stmt2 = null;
    private Statement stmt = null;
    private Connection conn = null;
    private Connection conn1 = null;
    
   
    public static void main(String[] args) 
    {
    	System.out.println("Calling Main ...");
    	CopyToConfidential md = new CopyToConfidential();
    	   	
    }
    
    

    public CopyToConfidential() {
    	System.out.println("Calling CopyDocs ...");
    	m_serverName = "icmnlsdb";
        m_userName = "icmadmin";
        m_password = "Bigblue123";
        //String SOURCE_ITEM_TYPE = "OSR_Addendums";
        String SOURCE_ITEM_TYPE = "OSR_Confidential";
		String TARGET_ITEM_TYPE = "Confidential_D";
		
    	MergeItemType(SOURCE_ITEM_TYPE, TARGET_ITEM_TYPE);
    }
 
    
    public void MergeItemType(String sDDO, String tDDO) {
        //System.out.println(" inside changeItemTypeWorkList & m_TargetItemType..."+m_TargetItemType);
    	String osrPid = null;
    	String osrItemID = null;
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
            
            
            //String query = "/" + sDDO;
            //String query = "/" + sDDO +"[@itemid =\"A1001001A19D30B33543E00000\" or itemid=\"A1001001A19D30B33554G00002\"]";
            String url = "jdbc:db2://win2012jl:50000/icmnlsdb";
            conn = DriverManager.getConnection(url, "icmadmin", "BigBlue123");
            System.out.println("Calling connect4 ...");
            //return all matches itemid of emp in OSR_Confidential and employeesSV table
            //String query = "select b.itemid from icmut01344001 a left outer join employeessv001 b on attr0000001636=osr_primaryid where osr_primaryid is not null";
            String query = "select a.itemid t1, b.itemid as t2 from icmut01185001 a left outer join employeessv2001 b on attr0000001073=osr_primaryid where osr_primaryid is not null";
            //production OSR_Confidential->icmut01344001, VM->icmut01185001
            //String query = "select a.itemid t1, b.itemid as t2 from icmut01344001 a left outer join employeessv001 b on attr0000001636=osr_primaryid where osr_primaryid is not null";
            Statement stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            //System.out.println("rs ..."+rs.next());
            while (rs!=null && rs.next()) {
            //then check if IDNumber of osr confidencial has a folder in EmployeeSV
            	String employeeItemID = rs.getString("t2");
            	String osrconfidentialItemID = rs.getString("t1");
            	System.out.println("employeeItemID ..."+employeeItemID);
            	System.out.println("osrconfidentialItemID ..."+osrconfidentialItemID);
            	//for each osr confidential item move to Confidential_D and get new itemid
            	DKDDO Confidential_Dddo = copyToConfidential(osrconfidentialItemID, sDDO, tDDO);
            	osrPid = Confidential_Dddo.getPidObject().getIdString();
            	osrItemID = osrPid.substring(3, 29);
	            
            	System.out.println("osrItemID ..."+osrItemID);
            	
            	//then for each employee itemid (sourceitemid) find if confidential folder exist (targetitemid)
            	//String query1 = "select itemid from confidential001 a inner join ICMstlinks001001 b on a.itemid=targetitemid where sourceitemid= '" + rs.getString("t2") + "'";
            	//check if this primary ID (sourceitemid) has Confidential folder associate
            	String query1 = "select itemid from confidential001 a inner join ICMstlinks001001 b on a.itemid=targetitemid where sourceitemid= '" + employeeItemID + "'";
                
            	stmt1 = conn.createStatement();
            	stmt2 = conn.createStatement();
                
            rs1 = stmt1.executeQuery(query1);
            System.out.println("Calling 1 "+query1);
            if(rs1.next()){
            	System.out.println("Calling 2 confidential folder exists .............");
	            //if confidencial folder exists, add new merged item into confidencial folder, if not, create one
            	//osrPid = copyToOSR(osrconfidentialItemID, sDDO, tDDO);
            	//add osr confidential item to CONFIDENTIAL folder
            	String query2 = "insert into ICMstlinks001001(targetitemid, sourceitemid, linktype, changed) values ( '" + osrItemID + "', '" + rs1.getString("itemid") + "',2,CURRENT_TIMESTAMP)";
            	//add CONFIDENTIAL folder to EmployeesSV2 folder
            	String query5 = "insert into ICMstlinks001001(targetitemid, sourceitemid, linktype, changed) values ( '" + rs1.getString("itemid") + "', '" + employeeItemID + "',2,CURRENT_TIMESTAMP)";
            	System.out.println("Calling 2 = "+osrItemID);
            	stmt1.addBatch(query2);// = conn.createStatement();
                stmt1.addBatch(query5);
                stmt1.executeBatch();
            	//stmt1.executeUpdate(query2);
            	//stmt1.executeUpdate(query5);
            }else{ 
            //else create new confidencial folder
            //Todo:
            	System.out.println("Else ......");
            	//DKDDO ddoNameFolder = dsICM.createDDO("EmployeesSV2", DKConstant.DK_CM_FOLDER);
                
            	//ddoNameFolder.add();
            	
            	//create new docType folder under employee folder
                DKDDO ddoSectionTitleFolder = dsICM.createDDO("Confidential_F", DKConstant.DK_CM_FOLDER);
                
                ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_SectionTitle"), "Confidential_F");
            
                ddoSectionTitleFolder.add();
            
                dsICM.checkOut(ddoSectionTitleFolder);
            
                DKFolder dkSubFolder = (DKFolder)ddoSectionTitleFolder.getData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKFOLDER));
	            
                short propId = ddoSectionTitleFolder.propertyId(DKConstantICM.DK_ICM_PROPERTY_ACL);
	            
	            ddoSectionTitleFolder.setProperty(propId, "Confidential");
	            System.out.println("--------- Update items ACL inside SectionTitle ---->----Confidential");
	             
	            String folderItemid = ddoSectionTitleFolder.getPidObject().getIdString();
	            folderItemid = folderItemid.substring(3, 29);
	            System.out.println("---------folderItemid--"+folderItemid);
	            
	            //then add merged itemID to link table, b.itemid as sourceitemid and confidencial folder as targetitemid
	            //adding item to confidential folder, confidential folder as targetitemid and confidential item is sourceitemid
	            String query3 = "insert into ICMstlinks001001(targetitemid, sourceitemid, linktype, changed) values ('" + osrItemID + "','" + folderItemid + "',2,CURRENT_TIMESTAMP)";
	            //add confidencial folder into EmployeesSV folder
	            String query4 = "insert into ICMstlinks001001(targetitemid, sourceitemid, linktype, changed) values ('" + folderItemid + "','" + employeeItemID + "',2,CURRENT_TIMESTAMP)";
	            
	            //stmt1.executeUpdate(query3);
	            //stmt1.executeUpdate(query4);
	            
	            stmt2.executeUpdate(query3);// = conn.createStatement();
	            //stmt2.addBatch(query4);
	            stmt2.executeUpdate(query4);
	            
	            dkSubFolder.addElement(Confidential_Dddo);
	            //update any new created section title including empth 'PHOTOS'
	            ddoSectionTitleFolder.update();
	            dsICM.checkIn(ddoSectionTitleFolder);
	                 
                //iter = null;
                //cursor.destroy();
        	    dsICM.disconnect();
                dsICM.destroy();       
                System.out.println("Move Item Type is Done!.");
            }//else
            
            }//big loop
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
    DKDDO copyToConfidential(String sourceitemID, String sDDO, String tDDO){
    	System.out.println("Inside copy to osr, sDDO -> " + sDDO + "tDDO -> " + tDDO);
    	DKDatastoreICM dsICM = null;
		DKDatastoreExtICM dsExtICM = null;
		System.out.println("Create target DDO");
        DKAttrDefICM aDef;
        Object aObject;
        String attrName = null;
        String attrValue = null;
        dkIterator iter = null;
        DKDDO  itemDDO = null;
        DKDDO  Confidential_DDDO = null;
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
            //dkResultSetCursor cursor = dsICM.execute(query.toString(), DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
            //while ( (itemDDO = cursor.fetchNext()) != null){ // for all attributes of the item
				// create an empty ddo
            DKResults results = (DKResults)dsICM.evaluate(query.toString(), DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
            iter = results.createIterator();
            
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
        						System.out.println("Set OSR_PrimaryID = "+attrValue);
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue);
        					
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
	            	//mimeType = part.getMimeType();
	            	part1.setMimeType("image/tiff");
	            	part1.setContent(part.getContent());
                    dkParts1.addElement(part1);
	            	
	                System.out.println("part1().getObjectType= "+part1.getPidObject().getObjectType());      
	            }
	            ddoTarget.add();
	            pid = ddoTarget.getPidObject().getIdString();
	            System.out.println("PID = " + pid);
	            pid = pid.substring(3, 29);
	            Confidential_DDDO = ddoTarget;
				System.out.println("PID itemid = " + pid);
				
				
				//}
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
        return Confidential_DDDO;
    //    return pid;
    }
}

