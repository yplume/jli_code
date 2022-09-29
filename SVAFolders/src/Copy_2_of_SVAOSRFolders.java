import com.ibm.mm.sdk.common.DKAttrDefICM;
import com.ibm.mm.sdk.common.DKConstant;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKDatastoreDefICM;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKFolder;
import com.ibm.mm.sdk.common.DKNVPair;
import com.ibm.mm.sdk.common.DKPidICM;
import com.ibm.mm.sdk.common.DKResults;
import com.ibm.mm.sdk.common.DKRetrieveOptionsICM;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.DKDatastoreExtICM;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;


public class Copy_2_of_SVAOSRFolders
{
  private String _cmServer = null;
  private String _cmDBName = null;
  private String _cmDBUserName = null;
  private String _cmDBPassword = null;
  
  private String _itemType = null;
  
  private String _cmUs = null;
  private String _cmPw = null;
  
  private String _dbName = null;
  
  private Connection conn = null;
  private Connection conn1 = null;
  private Statement stmt = null;
  private ResultSet rs = null;
  private Statement stmt1 = null;
  private ResultSet rs1 = null;
  
  private String sql = null;
  private String sql1 = null;
  
  private String url = null;
  
  private String OUTPUT_FILE_PATH = null;
  private String _subFolder = null;
  private int counter = 0;
  private String aFile = null;
  private String errorMessage = null;
  boolean errorOccurred = false;
  private int failIndex = 0;
  
  private String mimeType = null;
  private static final String SAMPLE_HIER_FOLDER_ITEM_TYPE_NAME =  "EmployeesSV";
  
  //private String[] docTypes = { "APPOINTMENTS", "BASIC_SERVICE_DATE", "CANDIDATES_CASE" };
  //private String[] docTypes = { "APPOINTMENTS", "BASIC SERVICE DATE", "CANDIDATES CASE", "CITIZENSHIP", "COMMISSIONING", "EDUCATION", "FAMILY", "FINANCES", "General", "GENERAL", "HEALTH", "LONG SERVICE", 
	//	    "OFFICER REVIEW BOARD", "OVERSEAS / OUT OF TERRITORY", "PHOTOS", "PROMOTION TO GLORY", "PROMOTIONS", "RETIREMENT", "REVIEWS - ACR", "REVIEWS - FIVE YEAR", "REVIEWS - SERVICE", "SPECIAL CIRCUMSTANCES" };
  private String[] docTypes = { "APPOINTMENTS", "BASIC_SERVICE_DATE", "CANDIDATES_CASE", "CITIZENSHIP", "COMMISSIONING", "EDUCATION", "FAMILY", "FINANCES", "GENERAL", "HEALTH", "LONG_SERVICE", 
		    "OFFICER_REVIEW_BOARD", "OVERSEAS_OUT_OF_TERRITORY", "PHOTOS", "PROMOTION_TO_GLORY", "PROMOTIONS", "RETIREMENT", "REVIEWS_ACR", "REVIEWS_FIVE_YEAR", "REVIEWS_SERVICE", "SPECIAL_CIRCUMSTANCES" };
		  
  final String queryCheck = "SELECT count(*) FROM ICMADMIN.EXPORTLOG where itemid= ?";
  final String queryInsert = "INSERT INTO EXPORTLOG (itemid, itemtype) values (?,?)";
  
  public static void main(String[] arg) {
    System.out.println("main ");
    
    Copy_2_of_SVAOSRFolders sva = new Copy_2_of_SVAOSRFolders();
  }
  

  public Copy_2_of_SVAOSRFolders()
  {
    loadIniFile();
    loadDriver();
  }
  

  public void loadIniFile()
  {
    try
    {
      Properties p = new Properties();
      p.load(new FileInputStream("c:\\SVA\\config.ini"));
      
      _cmServer = p.getProperty("CMSERVERNAME");
      _cmUs = p.getProperty("CMUSER");
      _cmPw = p.getProperty("CMPASSWORD");
      _dbName = p.getProperty("DBNAME");
      _itemType = p.getProperty("CMITEMTYPE");
      _cmDBName = p.getProperty("CMDBNAME");
      _cmDBUserName = p.getProperty("DBUSER");
      _cmDBPassword = p.getProperty("CMDBPASSWORD");
    }
    catch (Exception ex) {
      System.out.println("Error out-->" + ex.getMessage());
      setErrorOccurred(true);
      setErrorMessage("Exception: " + ex.getMessage());
    }
  }
  
  public int findFailPoint(String logFile)
  {
    int indexNumber = 0;
    try
    {
      BufferedReader in = new BufferedReader(new FileReader(logFile));
      
      String[] failPoint = new String[2];
      
      String str = null;
      


      while ((str = in.readLine()) != null) {
        failPoint = str.split("\t");
        indexNumber = Integer.parseInt(failPoint[0]);
        System.out.println("failPoint ->" + failPoint[0]);
      }
      in.close();

    }
    catch (IOException e)
    {
      System.out.println("Error-->" + e.getMessage());
    }
    return indexNumber;
  }
  


  public String processPath(String folder)
  {
    String output = "\\\\" + folder.substring(2).replaceAll("\\\\", "\\\\\\\\");
    return output;
  }
  
  public String intToString(int num, int digits) {
    String output = Integer.toString(num);
    while (output.length() < digits) output = "0" + output;
    return output;
  }
/*  
  public DKDDO getItemDDO(DKDatastoreICM dsICM, String itemID)
  {
    int read = 0;
    Vector mType = new Vector();
    DKDDO ddo = null;
    DKDatastoreExtICM dsExtICM = null;
    

    DKResults results = null;
    DKDDO parentDDO = null;
    try {
      String queryString = "/OSR[@VERSIONID = latest-version(.) AND (@ITEMID=\"" + itemID + "\")]";
      
      DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
      DKNVPair[] options = new DKNVPair[3];
      options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS,"0");
  	  options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer (DKConstant.DK_CM_CONTENT_NO));
  	  options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);

      System.out.println("Evaluating Query:  " + queryString.toString());
      results = (DKResults)dsICM.evaluate(queryString.toString(), DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
      

      dkIterator iter = results.createIterator();
      parentDDO = (DKDDO)iter.next();
      
      if (parentDDO != null) 
    	  System.out.println("Parent DDO found:  Type = '" + parentDDO.getObjectType() + "', CompID = '" + ((DKPidICM)parentDDO.getPidObject()).getComponentId() + "'"); 
      else {
        System.out.println("No parent found.");
      }
    } catch (DKException dke) {
      dke.printStackTrace();
      setErrorOccurred(true);
      setErrorMessage("Exception: " + dke.getMessage());
    }
    catch (InstantiationException ie)
    {
      ie.printStackTrace();
      setErrorOccurred(true);
      setErrorMessage("Exception: " + ie.getMessage());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("exception------>" + ex.getLocalizedMessage());
      setErrorOccurred(true);
      setErrorMessage("Exception: " + ex.getMessage());
    }
    
    return parentDDO;
  }
*/
  
  public void loadDriver()
  {
    try
    {
      System.out.println("Load driver...");
      
      Class.forName("com.ibm.db2.jcc.DB2Driver");
      url = ("jdbc:db2://" + _cmServer + ":50000/" + _dbName);
      System.out.println("=================================================");
      System.out.println("Connecting To DB2 To DB Lookup (CM LS):");
      System.out.println("-------------------------------------------------");
      System.out.println(" Database URL:   " + url);
      System.out.println(" DB2 Name:   " + _dbName);
      System.out.println("=================================================\n");
  
      loopThruItems(_itemType);
    }
    catch (Exception e) {
      System.out.println(e);
      setErrorOccurred(true);
      setErrorMessage("Exception: " + e.getMessage());
    } finally {
      try {
        if (conn1 != null) {
          conn1.close();
          conn1 = null;
        }
        if (conn != null) {
          conn.close();
          conn = null;
        }
      }
      catch (SQLException sqle) {
        sqle.printStackTrace();
      }
      exit();
    }
  }
  

  public void loopThruItems(String it)
  {
    stmt = null;
    rs = null;
    stmt1 = null;
    rs1 = null;
    sql = null;
    sql1 = null;
    DKDatastoreICM dsICM = null;
    PreparedStatement check = null;
    PreparedStatement insert = null;
    String stopPoint = "";
    try
    {
      System.out.println("user ........." + url + _cmDBName + _cmDBPassword);
      System.out.println("conection pooling.............");
      
      dsICM = new DKDatastoreICM();

      System.out.println("creatingg connection pooling ->" + _cmUs + _cmPw + _cmDBName);
      
      dsICM = ICMConnectionPool.getConnection(_cmUs, _cmPw, _cmDBName);
      System.out.println("After creatingg connection pooling" + url + _cmDBName + _cmDBPassword);
      
      conn = DriverManager.getConnection(url, _cmDBUserName, _cmDBPassword);
      stmt = conn.createStatement();
      stmt1 = conn.createStatement();
      
      String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, osr_dob, OSR_PrimaryID FROM OSR001";
      
      System.out.println("after getNameFoldersSQL..." + getNameFoldersSQL);
      rs1 = stmt1.executeQuery(getNameFoldersSQL);
      System.out.println("after rs1...");
      String fName = "";
      String lName = "";
      String dob = "";
      String primID = "";
      int count = 0;
      
      while (rs1.next()) {
    	System.out.println();
    	System.out.println();
        count++;
        System.out.println("COUNT = " + count);
        fName 	= rs1.getString("cc_firstname");
        lName 	= rs1.getString("cc_lastname");
        dob 	= rs1.getString("osr_dob");
        primID 	= rs1.getString("OSR_PrimaryID");
        System.out.println("OSR fName 	= " + fName);
        System.out.println("OSR lName 	= " + lName);
        System.out.println("OSR dob 	= " + dob);
        System.out.println("OSR primID 	= " + primID);
        

        //DKDDO ddoNameFolder = dsICM.createDDO("Employee", DKConstant.DK_CM_FOLDER);
        
        //DKDDO ddoNameFolder = createFolder(dsICM, systemRootFolder, "HierTest", "Hier Test");
        
        DKDDO ddoNameFolder = dsICM.createDDO("EmployeesSV", DKConstant.DK_CM_FOLDER);
        
	    //..The parent folder and name must be set for all hierarchical items.
	    //  The parent is optional if the system default folder is enabled, however an application
	    //  should always specify the parent if it is aware of the hierarchical model.
        //setHierarchicalAttrs(ddoNameFolder, dsICM.getRootFolder(), fName+" "+lName);
        setHierarchicalAttrs(ddoNameFolder, dsICM.getRootFolder(), primID);
	    
	    //..Persist the folder in the CM system
	    //ddo.add();
	    
	    
        System.out.println("---------------CREATE TOP FOLDERS!!-----------");
        
        ddoNameFolder.add();
        
        System.out.println("ADD ATTRIBUTES Top ddoNameFolder");
        
        //ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_FirstName"), fName);
        
        //ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_LastName"), lName);
        
        //ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_DOB"), 	java.sql.Date.valueOf(dob));
        
        //ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_PrimaryID"), primID);
        

        
        System.out.println("Open ddoNameFolder!!!!!!!!!!!!!!");
        //DKFolder dkNameFolder = (DKFolder)ddoNameFolder.getData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKFOLDER));
        
        String sectionTitle = "";
        //String updateACLQuery = "UPDATE OSR001 SET ACLCODE = ? WHERE ITEMID = ?";
        //PreparedStatement ps = null;
        //ps = conn.prepareStatement(updateACLQuery);
        String getFileSQL = "";
 
        for (int i = 0; i < docTypes.length; i++) {
        	
          System.out.println();
          
          if (docTypes[i].equalsIgnoreCase("OVERSEAS_OUT_OF_TERRITORY")) 
        	  sectionTitle = "OVERSEAS / OUT OF TERRITORY";
          else if (docTypes[i].equalsIgnoreCase("REVIEWS_ACR")) 
        	  sectionTitle = "REVIEWS - ACR";
          else if (docTypes[i].equalsIgnoreCase("REVIEWS_FIVE_YEAR")) 
        	  sectionTitle = "REVIEWS - FIVE YEAR";
          else if (docTypes[i].equalsIgnoreCase("REVIEWS_SERVICE")) 
        	  sectionTitle = "REVIEWS - SERVICE";
          else
        	  sectionTitle = docTypes[i].replace("_", " ");
          
          DKFolder dkNameFolder = (DKFolder)ddoNameFolder.getData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKFOLDER));
          
          //sectionTitle = docTypes[i].replace("_", " ");
          
          System.out.println("-------------Pick SectionTitle and run query-----------");
          
          String itemID = "";
          
          if (sectionTitle.equalsIgnoreCase("GENERAL"))
        	  getFileSQL = "SELECT itemid FROM OSR001 where cc_lastname = '" + lName + "' and cc_firstname = '" + fName + "' and (osr_sectiontitle = 'GENERAL' OR osr_sectiontitle = 'General') and OSR_PrimaryID = '" + primID + "'";
          else
        	  getFileSQL = "SELECT itemid FROM OSR001 where cc_lastname = '" + lName + "' and cc_firstname = '" + fName + "' and osr_sectiontitle = '" + sectionTitle + "'" + " and OSR_PrimaryID = '" + primID + "'";
           
          System.out.println("getFileSQL="+getFileSQL);
          
          rs = stmt.executeQuery(getFileSQL);
          
          System.out.println("loopThru OSR Items docTypes               = " + sectionTitle);
          
          
          
          while (rs.next())
          {
            System.out.println("If docType exist then add Items && checkout ddoNameFolder>>>>>");
            
            dsICM.checkOut(ddoNameFolder);
            
            //create new docType folder under employee folder
            DKDDO ddoSectionTitleFolder = dsICM.createDDO(docTypes[i], DKConstant.DK_CM_FOLDER);
            setHierarchicalAttrs(ddoSectionTitleFolder, ddoNameFolder, sectionTitle);
            
            System.out.println("Created ddoSectionTitleFolder ");
            
            ddoSectionTitleFolder.add();
            
            System.out.println("ADD ATTRIBUTES second level ddoSectionTitleFolder");
            
            /*ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_FirstName"), fName);
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_LastName"), lName);
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_DOB"), 	java.sql.Date.valueOf(dob));
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_PrimaryID"), 	primID);
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_SectionTitle"), sectionTitle);
            */
           
            DKFolder dkSubFolder = (DKFolder)ddoSectionTitleFolder.getData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKFOLDER));
            

            System.out.println("loopThru OSR Items checkout ddoSectionTitleFolder>>>>>");
            
            dsICM.checkOut(ddoSectionTitleFolder);
            
            System.out.println("------------- Update items ACL inside SectionTitle -----------");
            
            itemID = rs.getString("itemid");
            //ps.setString(1, "2");
            //ps.setString(2, itemID);
            //int updateRowcount = ps.executeUpdate();
            
            //short propId =ddoSectionTitleFolder.addProperty(DKConstantICM.DK_ICM_PROPERTY_ACL);
            short propId =ddoSectionTitleFolder.propertyId(DKConstantICM.DK_ICM_PROPERTY_ACL);
            System.out.println("------------- Update items ACL inside SectionTitle 1-----------");
            
            ddoSectionTitleFolder.setProperty(propId, docTypes[i] + "_ACL");
            System.out.println("------------- Update items ACL inside SectionTitle 2-----------");
            
            ddoSectionTitleFolder.update();
            
            System.out.println("itemID =" + itemID);


            StringBuffer query = new StringBuffer();
            query.append("//OSR");
            query.append("[@ITEMID=\"");
            query.append(itemID);
            
            query.append("\" AND @VERSIONID=latest-version(.)");
            query.append("]");           

            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            DKNVPair[] options = new DKNVPair[2];
            options[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_YES));
    	    // Specify any Retrieval Options desired.  Default is ATTRONLY.
            options[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);   	    

            System.out.println("Evaluating Query:  " + query.toString());
            DKResults results = (DKResults)dsICM.evaluate(query.toString(), DKConstant.DK_CM_XQPE_QL_TYPE, options);
            

            dkIterator iter = results.createIterator();
            
            DKDDO itemDDO = (DKDDO)iter.next();
            
            DKDDO SVDocsDDO = dsICM.createDDO("SVDocs", DKConstant.DK_CM_DOCUMENT);
            
            //////////////add parts//////////////////
            short dataid = SVDocsDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
            if (dataid > 0) {
                DKParts dkParts = (DKParts) SVDocsDDO.getData(dataid);
                if (dkParts.cardinality() == 0 || dkParts.cardinality() > 1) {
                    // log an error
                	System.out.println("dkParts error.....");
                }
                dkIterator iter1 = dkParts.createIterator();
                System.out.println("parts: "+dkParts.cardinality());
                
            
	            while (iter1.more()) {
	            	
	            	
	                DKLobICM part = (DKLobICM) iter1.next();
	                dkRetrieveOptions.resourceContent(true);
	                part.retrieve(dkRetrieveOptions.dkNVPair());
	                part.retrieve();
	                dkParts.addElement(part);
	            }
            }
            
            
            int counter = 0;
            int packagecounter = 0;
            
            while (iter.more()) {
            	String metaData = "";
            	String dataValue ="";
            	String att ="";
            	
            	System.out.println("counter ="+counter);
            	
            	
            	DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
			    DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemType);
			    DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
			    dkIterator attIter = allAttrs.createIterator();
			    
            	ddo = (DKDDO) iter.next();
            	ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
            	
                
                short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
                String imageName = "test";
                
                String value = ddo.getProperty((short)6).toString().replaceAll("[ |.|:]", "-");
            
                System.out.println("                           Value:  "+value);

                    
            	
              
    			System.out.println("metaData  att ="+metaData);
    			
                if (dataid > 0) {
                    DKParts dkParts = (DKParts) ddo.getData(dataid);
                    if (dkParts.cardinality() == 0 || dkParts.cardinality() > 1) {
                        // log an error
                    }
                    dkIterator iter3 = dkParts.createIterator();
                    System.out.println("parts: "+dkParts.cardinality());
                    int partCounter = 0;
                    
                    int read = 0;
                    // byte array design is not good for large documents exporting.
                	byte[] bytes = new byte[1024];
                	
                	
            		while (iter3.more()) {
                    	
                    	
                        DKLobICM part = (DKLobICM) iter3.next();
                        dkRetrieveOptions.resourceContent(true);
                        part.retrieve(dkRetrieveOptions.dkNVPair());
                        part.retrieve();
                        
                        OutputStream out = new FileOutputStream(new File("c:\\CMExport\\" +outPut + "\\"+packagecounter+"\\"  + value +".tif"));
                        //OutputStream out = new FileOutputStream(new File("e:\\CMExport\\" +outPut + "\\"  + value +".tif"));
                        InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(),-1,-1); 
                        System.out.println("get image ...");
                        while ((read = inputStream.read(bytes)) != -1) {
                    		out.write(bytes, 0, read);
                    	}
                     
                    	inputStream.close();
                    	out.flush();
                    	out.close();
                    	out = null;
                    	inputStream=null;
                    	part = null;
                    	
                    	System.out.println("image file path : c:\\CMExport\\" +outPut + "\\"+packagecounter+"\\"  + value +".tif");
                    	//System.out.println("image file path : e:\\CMExport\\" +outPut + "\\"   + value +".tif");
                        //	mime type must be pdf otherwise convertion from pdf to image will fail
                       // if (!part.getMimeType().equalsIgnoreCase("image/tiff")) {			//"application/pdf"
                        //    System.out.println("readImage: -Exception: Mime type = " + part.getMimeType());
                       // }
                        System.out.println("          Resource XDO:  Yes - Lobbb");
            		}
            	//}
                } else {
                    System.out.println("readImage: Could not find item.");
                }
                String newLine = System.getProperty("line.separator"); 

                metaData += "Image_Path c:\\CMExport\\" +outPut + "\\"+packagecounter+"\\"  + value +".tiff"+ newLine; 
                //metaData += "Image_Path e:\\CMExport\\" +outPut + "\\"  + value +".tiff"+ newLine; 
                System.out.println("before write metaData ="+metaData);
                bw.write(metaData);     
                counter++;
                metaData = null;
            }
            
            ///////////////end adding///////////////
            

            System.out.println("loopThru OSR Items add itemDDO to dkSubFolder & checking in ddoSectionTitleFolder<<<<<<<");
            dkSubFolder.addElement(itemDDO);
            ddoSectionTitleFolder.update();
            dsICM.checkIn(ddoSectionTitleFolder);
            
            System.out.println("loopThru OSR Items checking in ddoNameFolder<<<<<<<<<<");
            //dkNameFolder.addElement(ddoSectionTitleFolder);
            ddoNameFolder.update();
            dsICM.checkIn(ddoNameFolder);
          }
        }
        System.out.println();
        System.out.println("OUTSIDE WHILE LOOP!");
      }
      

      rs1.close();
      rs1 = null;
      rs.close();
      rs = null;
      stmt1.close();
      stmt1 = null;
      stmt.close();
      stmt = null;
      conn.close();
      conn = null;
      
      System.out.println("----------------DONE!------------------");
    }
    catch (Exception ex) {
      System.out.println(" Error Exception loopThruItems =" + ex.getMessage());
      
      System.out.println(" set error to class ");
      setErrorOccurred(true);
      setErrorMessage("Exception: " + ex.getMessage());
    } finally {
      try {
        if (dsICM != null)
        {
          try
          {
            ICMConnectionPool.returnConnection(dsICM);
            ICMConnectionPool.destroyConnections();
            dsICM = null;
          } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error returning connection to the pooling." + e.getMessage());
            setErrorOccurred(true);
            setErrorMessage("Exception: " + e.getMessage());
          }
        }
        if (insert != null) {
          insert.close();
          insert = null;
        }
        if (check != null) {
          check.close();
          check = null;
        }
        

        if (rs1 != null)
          rs1.close();
        if (rs != null)
          rs.close();
        if (stmt1 != null) {
          stmt1.close();
          stmt1 = null;
        }
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
        if (conn != null) {
          conn.close();
          conn = null;
        }
      }
      catch (SQLException sqle) {
        sqle.printStackTrace();
      }
    }
  }
  
  private static DKDDO createFolder(DKDatastoreICM dsICM, DKDDO parent, String itemType, String name) throws DKException, Exception {
	    
	    DKDDO ddo = dsICM.createDDO(itemType, DKConstant.DK_CM_FOLDER);
	        
	    //..The parent folder and name must be set for all hierarchical items.
	    //  The parent is optional if the system default folder is enabled, however an application
	    //  should always specify the parent if it is aware of the hierarchical model.
	    setHierarchicalAttrs(ddo, parent, name);

	    //..Persist the folder in the CM system
	    ddo.add();
	    
	    return ddo;
	  }
	  
	  /**
	   * A utility method to set the ICM$NAME attribute and the parent folder.  The name is 
	   * set into the DDO attribute when adding or updating an item.  The parent is set in 
	   * a DDO property when adding an item.
	   * 
	   * @param parent the parent folder to be set into the DDO
	   * @param name the name to be set in the DDO
	   */
	  private static void setHierarchicalAttrs(DKDDO ddo, DKDDO parent, String name) throws DKUsageError {
	    
	    //..Set the ICM$NAME attribute, which is the naming attribute for the hierarchical model
	    short dataId = ddo.dataId(DKConstantICM.DK_ICM_NAME_ATTR);
	    if (dataId > 0)
	     ddo.setData(dataId, name);
	    
	    //..Add theDK_ICM_PROPERTY_PARENT_FOLDER property which specifies the parent of this item.
	    //  As part of creating the item, the item will be placed in this folder.
	    ddo.addProperty(DKConstantICM.DK_ICM_PROPERTY_PARENT_FOLDER, parent);
	  }

  static DKAttrDefICM getAttr(DKDatastoreDefICM dsDef, String name, short type, int size)
    throws DKException, Exception
  {
    DKAttrDefICM attr = null;
    

    attr = (DKAttrDefICM)dsDef.retrieveAttr(name);
    
    if (attr == null) {
      System.out.println("  --> Attribute " + name + " does not exist, so create it...");
      attr = (DKAttrDefICM)dsDef.createAttr();
      attr.setName(name);
      attr.setDescription("Sample attribute named: " + name);
      attr.setType(type);
      if (size != 0) { attr.setSize(size);
      }
      attr.add();
      System.out.println("  --> Attribute " + name + " created.");
    } else {
      System.out.println("  --> Attribute " + name + " already exists...");
    }
    
    return attr;
  }
  
  public String getErrorMessage()
  {
    return errorMessage;
  }
  
  public void setErrorMessage(String string)
  {
    errorMessage = string;
  }
  
  public boolean getErrorOccurred() {
    return errorOccurred;
  }
  
  public void setErrorOccurred(boolean b) {
    errorOccurred = b;
  }
  
  public void exit() {
    System.out.println("Calling system exit.");
    System.exit(1);
  }
}