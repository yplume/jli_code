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
public class MoveToConfidential {

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
    	MoveToConfidential md = new MoveToConfidential();
    	   	
    }
    
    /*public MoveDocs(String cmServerName, String cmUserName, String cmPassword, String outPutPath, String sqlDatabase) {
        
        m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        m_OutPutPath = outPutPath;
        m_sqlServerDBName = sqlDatabase;
    }*/

    public MoveToConfidential() {
    	System.out.println("Calling MoveDocs ...");
    	m_serverName = "icmnlsdb";
        m_userName = "icmadmin";
        m_password = "Bigblue123";
        //String SOURCE_ITEM_TYPE = "OSR_Addendums";
        String SOURCE_ITEM_TYPE = "OSR_Confidential";
		String TARGET_ITEM_TYPE = "OSR2";
		
    	MergeItemType(SOURCE_ITEM_TYPE, TARGET_ITEM_TYPE);
    }
 
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
            
            // Set query params
            //
 //           DKResults results =null;
            
            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            
            DKNVPair options[] = new DKNVPair[3];
            options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0");                                            // Max number of search results. 0 means no max.
            options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_IDONLY));   // Retrieve only item id from server
            options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);                                           // Must mark the end of the NVPair
            // Search for ALL documents in source item-type
            System.out.println("Calling connect3 ...");
            
            dkRetrieveOptions.resourceContent(true);
            
            //String query = "/" + sDDO;
            //String query = "/" + sDDO +"[@itemid =\"A1001001A19D30B33543E00000\" or itemid=\"A1001001A19D30B33554G00002\"]";
            String url = "jdbc:db2://win2012jl:50000/icmnlsdb";
            conn = DriverManager.getConnection(url, "icmadmin", "BigBlue123");
            System.out.println("Calling connect4 ...");
            //return all matches itemid of emp in OSR_Confidential and employeesSV table
            //String query = "select b.itemid from icmut01344001 a left outer join employeessv001 b on attr0000001636=osr_primaryid where osr_primaryid is not null";
            String query = "select a.itemid t1, b.itemid as t2 from icmut01185001 a left outer join employeessv2001 b on attr0000001073=osr_primaryid where osr_primaryid is not null";
            //production OSR_Confidential->icmut01344001
            //String query = "select a.itemid t1, b.itemid as t2 from icmut01344001 a left outer join employeessv001 b on attr0000001636=osr_primaryid where osr_primaryid is not null";
            
            stmt = conn.createStatement();
            stmt1 = conn.createStatement();
            rs = stmt.executeQuery(query);
            //System.out.println("rs ..."+rs.next());
            while (rs!=null && rs.next()) {
            //then check if IDNumber of osr confidencial has a folder in EmployeeSV
            	System.out.println("Calling sql t2 ..."+rs.getString("t2"));
            	String employeeItemID = rs.getString("t2");
            	String osrconfidentialItemID = rs.getString("t1");
                
            //then for each employee itemid (sourceitemid) find if confidential folder exist (targetitemid)
            String query1 = "select itemid from confidential001 a inner join ICMstlinks001001 b on a.itemid=targetitemid where sourceitemid= '" + rs.getString("t2") + "'";
            
            rs1 = stmt1.executeQuery(query1);
            System.out.println("Calling 1 "+query1);
            if(rs1.next()){
            	System.out.println("Calling 2 confidential folder exists .............");
	            //if confidencial folder exists, add new merged item into confidencial folder, if not, create one
            	osrPid = copyToOSR(osrconfidentialItemID, sDDO, tDDO);
            	String query2 = "insert into ICMstlinks001001(targetitemid, sourceitemid, linktype, changed) values ( '" + osrPid + "', '" + rs1.getString("itemid") + "',2,CURRENT_TIMESTAMP)";
            	System.out.println("Calling 2 = "+osrPid);
            	stmt1.executeUpdate(query2);
            }else{ 
            //else create new confidencial folder
            //Todo:
            	System.out.println("Else ......");
            DKDDO ddoFolder  = dsICM.createDDO("Confidential_F", DKConstant.DK_CM_FOLDER);

            ddoFolder.setData(ddoFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_SectionTitle"), "Confidential_F");

            ddoFolder.add();
            
            //retrieve pid then find itemid of confidential folder 
            ddoFolder.getPidObject().pidString();
            
            //then add merged itemID to link table, b.itemid as sourceitemid and confidencial folder as targetitemid
            //adding item to confidencial folder, confidencial folder as targetitemid and confidential item is sourceitemid
            String query3 = "insert into ICMstlinks001001(targetitemid, sourceitemid, linktype, changed) values ('A1001001A22E04B42723I00002','A1001001A20K24C00229G00000',2,CURRENT_TIMESTAMP)";
            //add confidencial folder into EmployeesSV folder
            String query4 = "insert into ICMstlinks001001(targetitemid, sourceitemid, linktype, changed) values ('A1001001A22E04B42723I00002','A1001001A20K24C00229G00000',2,CURRENT_TIMESTAMP)";
            
            
            dkResultSetCursor cursor = dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
            //DKAttrDefICM aDef;
            //Object aObject;
            //String attrName = null;
            //String attrValue = null;
//            int n = 0;
            int count = 0;
			//int move_count = 0;
			//if (moveNumber.equalsIgnoreCase("all"))
			//	move_count = 100000;
			//else
			//	move_count = Integer.parseInt(moveNumber);
            // Iterate through hit list to move each document
            //
            //DKDDO  ddoSource;
            //DKDDO  ddoTarget;
            dkIterator iter = null;
 //           System.out.println("number of docs ready to move ="+cursor.cardinality());      
 //			results = (DKResults)dsICM.evaluate(query, DKConstant.DK_CM_XQPE_QL_TYPE, options);
 //			dkIterator iter1 = results.createIterator();
            // Create emtpy ddo object in target itemtype
            //
            //DKDDO ddo2 = dsICM.createDDO(TARGET_ITEM_TYPE, DKConstant.DK_CM_DOCUMENT);
			/*while ( (ddoSource = cursor.fetchNext()) != null) //for each row (document)
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
            					}else if (attrName.equalsIgnoreCase("Invoice_Number") && ddoSource.getDataByName(attrName)!=null) {
                						attrValue = ddoSource.getDataByName(attrName).toString();
            						aObject = ddoSource.getDataByName(attrName);
            						attrName = "Invoice_Num";
            						System.out.println("Invoice_Num");
            						
            						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
            						
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
*/
            	
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
					
        					if (attrName.equalsIgnoreCase("CC_ScannedCaseDate") && itemDDO.getDataByName(attrName)!=null) {
        						System.out.println("inside CC_ScannedCaseDate");
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_ScannedCaseDate";
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue)); //"2001-08-12"
        						System.out.println("Set CC_ScannedCaseDate = "+attrValue);
        					}else if (attrName.equalsIgnoreCase("CC_NHQMAILDATE") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_NHQMAILDATE";
        						System.out.println("Set CC_NHQMAILDATE");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("KfxScannedBy") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = ddoSource.getDataByName(attrName);
        						attrName = "KfxScannedByNew";
        						System.out.println("set KfxScannedByNew");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					
        					}else if (attrName.equalsIgnoreCase("KfxScannedByNew") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = ddoSource.getDataByName(attrName);
        						attrName = "KfxScannedByNew";
        						System.out.println("set KfxScannedByNew");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					
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
        					}else if (attrName.equalsIgnoreCase("OSR_SectionTitle") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_SectionTitle";
        						System.out.println("Set OSR_SectionTitle");
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
	            	//part.retrieve(DKConstant.DK_CM_CONTENT_YES);
	                //DKDDO contentDDO = (DKDDO)iter.next();
	            	
	            	// create target ddo att
	            	//mimeType = part.getMimeType();
	            	part1.setMimeType("image/tiff");
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
	            }
//ddo.setContentFromClientFile.add();
	         //ddoTarget.add();
	            
	            ddoTarget.add();
				}
        	}
   // }
            	//ddoSource = null;
                //ddoTarget = null;
                
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

