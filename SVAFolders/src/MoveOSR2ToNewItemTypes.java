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
public class MoveOSR2ToNewItemTypes {

    private String m_BaseURL = "";
    //private DKDatastoreICM m_dsICM = null;
    private DKDocRoutingServiceICM m_RoutingService;
    private String m_serverName;
    private String m_userName;
    private String m_password;
    //private String m_OutPutPath;
    //private String m_sqlServerDBName;
    private Connection conn = null;
    private ResultSet rs = null;
    private Statement stmt = null;
    
    
    /*public AribaMediator(String cmServerName, String cmUserName, String cmPassword, String SQLServer) {
        this(cmServerName, cmUserName, cmPassword, SQLServer);
    }*/
    
    public static void main(String[] args) 
    {
    	System.out.println("Calling Main ...");
    	MoveOSR2ToNewItemTypes md = new MoveOSR2ToNewItemTypes();
    	   	
    }
    
    /*public MoveDocs(String cmServerName, String cmUserName, String cmPassword, String outPutPath, String sqlDatabase) {
        
        m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        m_OutPutPath = outPutPath;
        m_sqlServerDBName = sqlDatabase;
    }*/

    public String CapitalizeWords (String itemTypeName){
    	
    	itemTypeName = "everyone_loves_java";

    	String word = itemTypeName;
	    // stores each characters to a char array
	    char[] charArray = word.toCharArray();
	    boolean foundSpace = true;

	    for(int i = 0; i < charArray.length; i++) {

	      // if the array element is a letter
	      if(Character.isLetter(charArray[i])) {

	        // check space is present before the letter
	        if(foundSpace) {

	          // change the letter into uppercase
	          charArray[i] = Character.toUpperCase(charArray[i]);
	          foundSpace = false;
	        }
	      }

	      else {
	        // if the new character is not character
	        foundSpace = true;
	      }
	    }

	    // convert the char array to the string
	    word = String.valueOf(charArray);
	    System.out.println("word: " + word);
    	
    	return word;
    }
    
    public MoveOSR2ToNewItemTypes() {
    	System.out.println("Calling MoveDocs ...");
    	m_serverName = "icmnlsdb";
        m_userName = "icmadmin";
        m_password = "Bigblue123";
        //String SOURCE_ITEM_TYPE = "OSR_Addendums";
        String SOURCE_ITEM_TYPE = "Officers";
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
            
            /*DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            
            DKNVPair options[] = new DKNVPair[3];
            options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0");                                            // Max number of search results. 0 means no max.
            options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_IDONLY));   // Retrieve only item id from server
            options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);                                           // Must mark the end of the NVPair
            // Search for ALL documents in source item-type
            //
            dkRetrieveOptions.resourceContent(true);
            */
            Class.forName("com.ibm.db2.jcc.DB2Driver");
            String url = "jdbc:db2://win2012jl:50000/icmnlsdb";
            //String url = "jdbc:db2://" + m_lsName + ":50000/icmsvadb";
            conn = DriverManager.getConnection(url, m_userName, m_password);
            
            //find all OSR2 items
            String query = "select itemtypeid, a.itemid, a.aclcode, OSR_SECTIONTITLE, targetitemid, sourceitemid, keywordname from osr2001 a inner join ICMstlinks001001 b on a.itemid=targetitemid inner join icmstitems001001 c on sourceitemid = c.itemid inner join icmstnlskeywords on itemtypeid=keywordcode where a.ACLCODE = 1054 and osr_sectiontitle is not null and keywordclass=2";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            
            while (rs!=null && rs.next()) {
            	
            	String itemID = "";
            	String soruceOSR2FolderItemType = "";
            	itemID = rs.getString(2);
            	soruceOSR2FolderItemType = rs.getString(7);
            	System.out.println("soruceOSR2FolderItemType = "+soruceOSR2FolderItemType);
                
            	String targetOSR2FolderItemType = CapitalizeWords(soruceOSR2FolderItemType);
            	System.out.println("targetOSR2FolderItemType = "+targetOSR2FolderItemType);
            
            StringBuffer queryItemID = new StringBuffer();
            queryItemID.append("//OSR2");
            queryItemID.append("[@ITEMID=\"");
            queryItemID.append(itemID);
            queryItemID.append("\" AND @VERSIONID=latest-version(.)");
            queryItemID.append("]");           

            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            DKNVPair[] options = new DKNVPair[2];
            options[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_YES));
    	    // Specify any Retrieval Options desired.  Default is ATTRONLY.
            options[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);   	    

            System.out.println("Evaluating Query:  " + query.toString());
            DKResults results = (DKResults)dsICM.evaluate(queryItemID.toString(), DKConstant.DK_CM_XQPE_QL_TYPE, options);
            
            dkRetrieveOptions.resourceContent(true);
            dkIterator iter = results.createIterator();
            //retrieve from source
            DKDDO itemDDO = (DKDDO)iter.next();
            

            ////////////////////Move Items//////////////////
            DKDDO ddoTarget = dsICM.createDDO(targetOSR2FolderItemType, DKConstant.DK_CM_DOCUMENT);
            //System.out.println("Create target DDO");
            DKAttrDefICM aDef;
            Object aObject;
            String attrName = null;
            String attrValue = null;
            //System.out.println("retrive source DDO");
            
            itemDDO.retrieve();
			DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
            DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity("OSR2");
            DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
            iter = allAttrs.createIterator();
            //System.out.println("retrive source DDO att");
            
	        while(iter.more()){ // for all attributes of the item
	        	aDef = (DKAttrDefICM) iter.next();
				aObject = null;
				
				try {
					attrName = aDef.getName();
					//System.out.println("attrName ="+attrName);
					//System.out.println("itemDDO.getDataByName(attrName)="+itemDDO.getDataByName(attrName));
					
        					if (attrName.equalsIgnoreCase("CC_ScannedCaseDate") && itemDDO.getDataByName(attrName)!=null) {
        						//System.out.println("inside CC_ScannedCaseDate");
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_ScannedCaseDate";
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue)); //"2001-08-12"
        						System.out.println("Set CC_ScannedCaseDate = "+attrValue);
        					}else if (attrName.equalsIgnoreCase("CC_NHQMAILDATE") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_NHQMAILDATE";
        						//System.out.println("Set CC_NHQMAILDATE");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("KfxScannedByNew") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = ddoSource.getDataByName(attrName);
        						attrName = "KfxScannedByNew";
        						//System.out.println("set KfxScannedByNew");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					
        					}else if (attrName.equalsIgnoreCase("KfxScannedByNew") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = ddoSource.getDataByName(attrName);
        						attrName = "KfxScannedByNew";
        						//System.out.println("set KfxScannedByNew");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					
        					}else if (attrName.equalsIgnoreCase("CC_FirstName") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_FirstName";
        						//System.out.println("Set CC_FirstName");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("CC_LastName") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_LastName";
        						//System.out.println("Set CC_LastName");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("CC_MiddleInitial") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_MiddleInitial";
        						//System.out.println("Set CC_MiddleInitial ");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("OSR_DOB") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        						formatter.format(java.sql.Date.valueOf(attrValue));
        						attrName = "OSR_DOB ";
        						//System.out.println("Set OSR_DOB");
        						//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        						//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue)); //"2001-08-12"
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_DOB"), java.sql.Date.valueOf(formatter.format(java.sql.Date.valueOf(attrValue)))); //"2001-08-12"
      				          	//System.out.println("Set OSR_DOBBBBB = "+attrValue);
        					}else if (attrName.equalsIgnoreCase("CC_SEALEDENVELOPEONFILE") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_SEALEDENVELOPEONFILE";
        						//System.out.println("Set CC_SEALEDENVELOPEONFILE");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("OSR_RetirementDate") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_RetirementDate ";
        						//System.out.println("Set OSR_RetirementDate");
        						//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue)); //"2001-08-12"
        					}else if (attrName.equalsIgnoreCase("CC_CLOSEDCASEDATE") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_CLOSEDCASEDATE  ";
        						//System.out.println("Set CC_CLOSEDCASEDATE");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("CC_REOPENEDCASEDATE") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_REOPENEDCASEDATE  ";
        						//System.out.println("Set CC_REOPENEDCASEDATE");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					
        					}else if (attrName.equalsIgnoreCase("OSR_SectionTitle") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_SectionTitle";
        						//System.out.println("Set OSR_SectionTitle");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("OSR_SecondID") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_SecondID ";
        						//System.out.println("Set OSR_SecondID");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        						
        					}else if (attrName.equalsIgnoreCase("OSR_PrimaryID") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString().trim();
        						//aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_PrimaryID";
        						//System.out.println("Set OSR_PrimaryID");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}
        				
        				
        			} catch (Exception exc) {
        				System.out.println("exc....="+exc.getMessage());
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
	            	
	            	System.out.println("part.getContent() = "+part.getContent());
	            	
	            	if(part.getContent()!=null){
		            	System.out.println("Inside part......");
		            
		            	// create target ddo att
		            	//mimeType = part.getMimeType();
		            	String mimeType = part.getMimeType();
		            	part1.setMimeType(mimeType);
		            	part1.setContent(part.getContent());
	                
		                dkParts1.addElement(part1);
		                System.out.println("part1().getObjectType == "+part1.getPidObject().getObjectType());      
	            	}else{
	            		System.out.println("Missing Image; itemDDO = "+itemDDO.getPidObject().toString());
	            		//insertMissing.setString(1, itemDDO.getPidObject().toString());
	            		System.out.println("Itemid ="+itemDDO.getPidObject().toString());
	            		//insertMissing.setString(2, attrValue);
	            		System.out.println("After setdate");
	            		//insertMissing.addBatch();
	            		//insertMissing.executeBatch();
	            		//System.out.println("After sql");
	            	      
	            	}
	            	part=null;
	            	
	            	part1=null;
	            	
	            }
	            dkParts1=null;
	            
	            ddoTarget.add();
	            
	            dkParts=null;
		        ////////end copy////////
		        System.out.println("After Copy Item Type.");
	                
	           
	            
	            //////////////////////Ending////////////////////
	            
	            System.out.println("loopThru OSR Items add itemDDO to dkSubFolder & checking in ddoSectionTitleFolder<<<<");
	            
	            
	            
	        }else {
              System.out.println("loopThruItems: Could not find item.");
			}
			//ddoTarget.add();
	        
	        ////////end copy////////
            	

            	//ddoSource = null;
                //ddoTarget = null;
                
                //iter = null;
                //cursor.destroy();
        	    dsICM.disconnect();
                dsICM.destroy();       
                System.out.println("Move Item Type is Done!.");
            }//big while
            
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

