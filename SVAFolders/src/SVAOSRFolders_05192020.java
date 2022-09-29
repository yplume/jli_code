import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.DKDatastoreExtICM;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;
import java.util.Date;

public class SVAOSRFolders_05192020
{
  private String _cmServer = null;
  private String _cmDBName = null;
  private String _cmDBUserName = null;
  private String _cmDBPassword = null;
  private String _itemType = null;
  private String _targetitemType = null;
  private String _cmUs = null;
  private String _cmPw = null;
  private String _dbName = null;
  private String _startDate = null;
  private String _endDate = null;
  
  private Connection conn = null;
  private Connection conn1 = null;
  private Statement stmt = null;
  private ResultSet rs = null;
  private Statement stmt1 = null;
  private ResultSet rs1 = null;
  
  private String sql = null;
  private String sql1 = null;
  private String url = null;
  
  private String itemIDStart = "";
  private String itemIDEnd = "";
	
  //private String OUTPUT_FILE_PATH = null;
 // private String _subFolder = null;
  //private int counter = 0;
  //private String aFile = null;
  private String errorMessage = null;
  boolean errorOccurred = false;
  //private int failIndex = 0;
  
  private String mimeType = null;
  private static final String SAMPLE_HIER_FOLDER_ITEM_TYPE_NAME =  "EmployeesSV";
  
  //private String[] docTypes = { "APPOINTMENTS", "BASIC_SERVICE_DATE", "CANDIDATES_CASE" };
  //private String[] docTypes = { "APPOINTMENTS", "BASIC SERVICE DATE", "CANDIDATES CASE", "CITIZENSHIP", "COMMISSIONING", "EDUCATION", "FAMILY", "FINANCES", "General", "GENERAL", "HEALTH", "LONG SERVICE", 
	//	    "OFFICER REVIEW BOARD", "OVERSEAS / OUT OF TERRITORY", "PHOTOS", "PROMOTION TO GLORY", "PROMOTIONS", "RETIREMENT", "REVIEWS - ACR", "REVIEWS - FIVE YEAR", "REVIEWS - SERVICE", "SPECIAL CIRCUMSTANCES" };
  private String[] docTypes = { "APPOINTMENTS", "BASIC_SERVICE_DATE", "CANDIDATES_CASE", "CITIZENSHIP", "COMMISSIONING", "EDUCATION_NEW", "FAMILY", "FINANCES", "GENERAL", "HEALTH", "LONG_SERVICE", 
		    "OFFICER_REVIEW_BOARD", "OVERSEAS_OUT_OF_TERRITORY", "PHOTOS", "PROMOTION_TO_GLORY", "PROMOTIONS", "RETIREMENT", "REVIEWS_ACR", "REVIEWS_FIVE_YEAR", "REVIEWS_SERVICE", "SPECIAL_CIRCUMSTANCES" };
		  
  final String queryCheck = "SELECT count(*) FROM ICMADMIN.EXPORTLOG where itemid= ?";
  //final String queryInsert = "INSERT INTO FOLDERLOG (PRIMARYID, DATE) values (?,?)";
  final String queryInsert = "INSERT INTO FOLDERLOG (FOLDERNAME, DATE) values (?,?)";
  
  public static void main(String[] arg) {
    System.out.println("main ");
    
    SVAOSRFolders_05192020 sva = new SVAOSRFolders_05192020(arg);
  }
  

  public SVAOSRFolders_05192020(String arg[])
  {
	  	itemIDStart = arg[0];
		itemIDEnd 	= arg[1];
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
      _targetitemType = p.getProperty("CMTARGETITEMTYPE");
      _cmDBName = p.getProperty("CMDBNAME");
      _cmDBUserName = p.getProperty("DBUSER");
      _cmDBPassword = p.getProperty("CMDBPASSWORD");
      _startDate = p.getProperty("STARTDATE");
      _endDate = p.getProperty("ENDDATE");
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
      System.out.println(" Source Item Type:   " + _itemType);
      System.out.println(" Target Item Type:   " + _targetitemType);
      System.out.println("=================================================\n");
  
      loopThruItems(_itemType, _targetitemType);
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
  

  public void loopThruItems(String it, String tit)
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
    DKDatastoreExtICM dsExtICM = null;
    try
    {
      	
      System.out.println("user ........." + url + _cmDBName + _cmDBPassword);
      System.out.println("conection pooling.............");
      
      dsICM = new DKDatastoreICM();

      System.out.println("creatingg connection pooling ->" + _cmUs + _cmPw + _cmDBName);
      
      dsICM = ICMConnectionPool.getConnection(_cmUs, _cmPw, _cmDBName);
      System.out.println("After creatingg connection pooling" + url + _cmDBName + _cmDBPassword);
      
      dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT);
      
      conn = DriverManager.getConnection(url, _cmDBUserName, _cmDBPassword);
      
      System.out.println("After getConnection");
      
      stmt = conn.createStatement();
      stmt1 = conn.createStatement();
      
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR001 WHERE CREATETS>='"+_startDate+"' AND CREATETS<='"+_endDate+"'";
      //remove DOB, CC_SEALEDENVELOPEONFILE, CC_CLOSEDCASEDATE, OSR_SecondID, OSR_RetirementDate, CC_REOPENEDCASEDATE
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR001 WHERE itemid>='"+ itemIDStart +"' and itemid<='"+ itemIDEnd +"'"; 
      String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR001 inner join FOLDERLOG on OSR_PrimaryID <> foldername";
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR001 WHERE OSR_PrimaryID='2-04-192843-0' or OSR_PrimaryID='8-04-990791-1'";
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, OSR_PrimaryID FROM OSR001 WHERE OSR_PrimaryID='8-04-990797-1' or OSR_PrimaryID='8-04-990796-1'";
    	  
      insert = conn.prepareStatement(queryInsert);
		
      System.out.println("After getNameFoldersSQL..." + getNameFoldersSQL);
      rs1 = stmt1.executeQuery(getNameFoldersSQL);
      System.out.println("After rs1...");
      String fName = "";
      String lName = "";
      String dob = "";
      String rdate = "";
      String primID = "";
      String midint = "";
      String sealed = "";
      String closed = "";
      String reopen = "";
      String secID  = "";
      String dobq = "";
      String rdateq = "";
     // String primIDq = "";
      String midintq = "";
      String sealedq = "";
      String closedq = "";
      String reopenq = "";
      String secIDq  = "";
      int count = 0;
      
      //DKDDO empDDO = dsICM.createDDOFromPID("97 3 ICM12 LSconnection12 EmployeesSV159 26 A1001001A20D16B53312J0022918 A20D16B53312J002291 14 1177");
     // DKDDO empDDO = dsICM.createDDOFromPID("97 3 ICM12 LSconnection12 EmployeesSV159 26 A1001001A20D16B53312J0022918 A20D16B53312J002291 14 1177");
      DKDDO empDDO = dsICM.createDDOFromPID("91 3 ICM8 icmsvadb11 EmployeesSV59 26 A1001001A20D03B72201E0104918 A20D03B72201E010491 14 1442");
      while (rs1.next()) {
    	System.out.println();
    	System.out.println();
        count++;
        System.out.println("COUNT = " + count);
        fName 	= rs1.getString("cc_firstname");
        lName 	= rs1.getString("cc_lastname");
        dob 	= rs1.getString("osr_dob");
        rdate	= rs1.getString("OSR_RetirementDate");
        primID 	= rs1.getString("OSR_PrimaryID");
        midint	= rs1.getString("CC_MiddleInitial");
        sealed	= rs1.getString("CC_SEALEDENVELOPEONFILE");
        closed	= rs1.getString("CC_CLOSEDCASEDATE");
        reopen	= rs1.getString("CC_REOPENEDCASEDATE");
        secID	= rs1.getString("OSR_SecondID");
        System.out.println("OSR fName 	= " + fName);
        System.out.println("OSR lName 	= " + lName);
        System.out.println("OSR dob 	= " + dob);
        System.out.println("OSR rdate 	= " + rdate);
        System.out.println("OSR primID, OSR_SecondID 	= " + primID +", "+ rs1.getString("OSR_SecondID"));
        

        //DKDDO ddoNameFolderRootFolder = dsICM.createDDO("EmployeesSV1", DKConstant.DK_CM_FOLDER);
        DKDDO ddoNameFolder = dsICM.createDDO("EmployeesSV", DKConstant.DK_CM_FOLDER);
        
        System.out.println("====dsICM.getRootFolder() 	= " + dsICM.getRootFolder());
	    //..The parent folder and name must be set for all hierarchical items.
	    //  The parent is optional if the system default folder is enabled, however an application
	    //  should always specify the parent if it is aware of the hierarchical model.
        //setHierarchicalAttrs(ddoNameFolder, dsICM.getRootFolder(), fName+" "+lName);
        //setHierarchicalAttrs(ddoNameFolderRootFolder, dsICM.getRootFolder(), "EmployeeRecords");
        setHierarchicalAttrs(ddoNameFolder, empDDO, lName+" "+fName);
        //setHierarchicalAttrs(ddoNameFolder, dsICM.getRootFolder(), primID);
	    
	    //..Persist the folder in the CM system
	    //ddo.add();
        printFolderContents(ddoNameFolder);
	    
        System.out.println("---------------CREATE TOP FOLDERS!!-----------");
        //ddoNameFolderRootFolder.add();
        
        ddoNameFolder.add();
        
        System.out.println("Insert into Folder log tabe.");
        Date date = new Date();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		insert.setString(1, primID);
		System.out.println("primID ="+primID);
		insert.setDate(2, sqlDate);
		System.out.println("After setdate");
		//insert.setString(2, it);
		//insert.setString(3, it);
		insert.addBatch();
		//System.out.println("after insert");
		insert.executeBatch();
		//}//end check exportlog 
		System.out.println("After insert execute");
		
        System.out.println("ADD ATTRIBUTES Top ddoNameFolder");
        
        if(fName!="" && fName!=null)
            ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_FirstName"), fName);
       
        if(lName!="" && lName!=null)
            ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_LastName"), lName);
        
        if(midint !="" && midint!=null){
            //ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_MiddleInitial"), midint);
            midintq = " CC_MiddleInitial='"+ midint + "'";
        }else
        	midintq = " CC_MiddleInitial is null";
        if(dob!="" && dob!=null){
            //ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_DOB"), 	java.sql.Date.valueOf(dob));
            dobq = " OSR_DOB='"+ dob + "'";
        }else
        	dobq = " OSR_DOB is null";
        if(sealed!="" && sealed!=null){
        	//ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_SEALEDENVELOPEONFILE"), sealed);
        	sealedq = " CC_SEALEDENVELOPEONFILE='"+ sealed + "'";
        }else
        	sealedq = " CC_SEALEDENVELOPEONFILE is null";
        
		if (rdate != "" && rdate != null){
			//ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_RetirementDate"), java.sql.Date.valueOf(rdate));
			rdateq = " OSR_RetirementDate='"+ rdate + "'";
		}else
			rdateq = " OSR_RetirementDate is null";
        
		if (closed != ""&& closed != null){
			//ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_CLOSEDCASEDATE"), closed);
			closedq = " CC_CLOSEDCASEDATE='"+ closed + "'";
		}else
			closedq = " CC_CLOSEDCASEDATE is null";
        if (reopen != ""&& reopen != null){
			//ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_REOPENEDCASEDATE"), reopen);
			reopenq = " CC_REOPENEDCASEDATE='"+ reopen + "'";
        }else
			reopenq = " CC_REOPENEDCASEDATE is null";
        
        /*if (reopen != ""&& reopen != null){
			ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_ScannedCaseDate"), reopen);
			reopenq = " CC_ScannedCaseDate='"+ reopen + "'";
        }else
			reopenq = " CC_ScannedCaseDate is null";
        */
        if (primID != "" && primID != null)
			ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_PrimaryID"), primID);

		if (secID != ""&& secID != null){
			//ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_SecondID"), secID);
			secIDq = " OSR_SecondID='"+ secID + "'";
		}else
			secIDq = " OSR_SecondID is null";
        
		
		System.out.println("Open ddoNameFolder!!!");
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
          //else if (docTypes[i].equalsIgnoreCase("REVIEWS_SERVICE")) 
        	//  sectionTitle = "BASIC_SERVICE_DATE";
          else if (docTypes[i].equalsIgnoreCase("REVIEWS_SERVICE")) 
        	  sectionTitle = "REVIEWS - SERVICE";
          else if (docTypes[i].equalsIgnoreCase("BASIC_SERVICE_DATE")) 
        	  sectionTitle = "BASIC SERVICE DATE";
          else if (docTypes[i].equalsIgnoreCase("EDUCATION_NEW")) 
        	  sectionTitle = "EDUCATION";
          else
        	  sectionTitle = docTypes[i].replace("_", " ");
          
          DKFolder dkNameFolder = (DKFolder)ddoNameFolder.getData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKFOLDER));
          
          //sectionTitle = docTypes[i].replace("_", " ");
          
          System.out.println("----------Pick SectionTitle and run query--------");
          
          String itemID = "";
          
          if (sectionTitle.equalsIgnoreCase("GENERAL"))
        	  //getFileSQL = "SELECT itemid FROM OSR001 where cc_lastname = '" + lName + "' and cc_firsatname = '" + fName + "' and (osr_sectiontitle = 'GENERAL' OR osr_sectiontitle = 'General') and OSR_PrimaryID = '" + primID + "'";
          	  getFileSQL = "SELECT itemid FROM OSR001 where cc_lastname = '" + lName + "' and cc_firstname = '" + fName + "' and (osr_sectiontitle = 'GENERAL' OR osr_sectiontitle = 'General') and OSR_PrimaryID = '" + primID + "' and " + midintq+ " and " + dobq + " and " + sealedq + " and " + rdateq + " and " + closedq + " and " + reopenq + " and " + secIDq;
          else
        	  getFileSQL = "SELECT itemid FROM OSR001 where cc_lastname = '" + lName + "' and cc_firstname = '" + fName + "' and osr_sectiontitle = '" + sectionTitle + "'" + " and OSR_PrimaryID = '" + primID + "' and " + midintq + " and " + dobq + " and " + sealedq + " and " + rdateq + " and " + closedq + " and " + reopenq + " and " + secIDq;
         
           
          System.out.println("getFileSQL="+getFileSQL);
          
          rs = stmt.executeQuery(getFileSQL);
          
          System.out.println("loopThru OSR Items docTypes    = " + sectionTitle);
           
          //handle to create "Photos" empty folders if doesn't exists
          System.out.println("If docType exist then add Items && checkout ddoNameFolder>>>"+docTypes[i]+">>>sectiontitle>>>"+sectionTitle);
          
          dsICM.checkOut(ddoNameFolder);
          
          //create new docType folder under employee folder
          DKDDO ddoSectionTitleFolder = dsICM.createDDO(docTypes[i], DKConstant.DK_CM_FOLDER);
          setHierarchicalAttrs(ddoSectionTitleFolder, ddoNameFolder, sectionTitle);
          
          System.out.println("Created ddoSectionTitleFolder ");
          
          ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_SectionTitle"), sectionTitle);

          ddoSectionTitleFolder.add();
          
          dsICM.checkOut(ddoSectionTitleFolder);
          System.out.println("After add ddoSectionTitleFolder ");
          
          while (rs.next())
          {
            /*System.out.println("If docType exist then add Items && checkout ddoNameFolder>>>>>"+docTypes[i]+">>>sectiontitle>>>"+sectionTitle);
            
            dsICM.checkOut(ddoNameFolder);
            
            //create new docType folder under employee folder
            DKDDO ddoSectionTitleFolder = dsICM.createDDO(docTypes[i], DKConstant.DK_CM_FOLDER);
            setHierarchicalAttrs(ddoSectionTitleFolder, ddoNameFolder, sectionTitle);
            
            System.out.println("Created ddoSectionTitleFolder ");
            
            ddoSectionTitleFolder.add();
            */
            System.out.println("ADD ATTRIBUTES second level ddoSectionTitleFolder");
            
            /*ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_FirstName"), fName);
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_LastName"), lName);
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_DOB"), 	java.sql.Date.valueOf(dob));
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_PrimaryID"), 	primID);
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_SectionTitle"), sectionTitle);
             */
           
            DKFolder dkSubFolder = (DKFolder)ddoSectionTitleFolder.getData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKFOLDER));
            

            System.out.println("loopThru OSR Items checkout ddoSectionTitleFolder>>>>>");
            
            //dsICM.checkOut(ddoSectionTitleFolder);
            
            System.out.println("-------- Update items ACL inside SectionTitle ---------");
            
            itemID = rs.getString("itemid");
            //ps.setString(1, "2");
            //ps.setString(2, itemID);
            //int updateRowcount = ps.executeUpdate();
            
            //short propId =ddoSectionTitleFolder.addProperty(DKConstantICM.DK_ICM_PROPERTY_ACL);
            short propId =ddoSectionTitleFolder.propertyId(DKConstantICM.DK_ICM_PROPERTY_ACL);
            System.out.println("--------- Update items ACL inside SectionTitle 1--------" + docTypes[i]);
            
            ddoSectionTitleFolder.setProperty(propId, docTypes[i] + "_ACL");
            System.out.println("--------- Update items ACL inside SectionTitle 2--------");
            
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
            
            dkRetrieveOptions.resourceContent(true);
            dkIterator iter = results.createIterator();
            
            DKDDO itemDDO = (DKDDO)iter.next();
            

            ////////////////////Move Items//////////////////
            DKDDO ddoTarget = dsICM.createDDO(tit, DKConstant.DK_CM_DOCUMENT);
            System.out.println("Create target DDO");
            DKAttrDefICM aDef;
            Object aObject;
            String attrName = null;
            String attrValue = null;
            System.out.println("retrive source DDO");
            
            itemDDO.retrieve();
			DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
            DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity("OSR");
            DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
            iter = allAttrs.createIterator();
            System.out.println("retrive source DDO att");
            
	        while(iter.more()){ // for all attributes of the item
	        	aDef = (DKAttrDefICM) iter.next();
				aObject = null;
				
				try {
					attrName = aDef.getName();
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
        					}else if (attrName.equalsIgnoreCase("CC_MiddleInitial") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_MiddleInitial";
        						System.out.println("Set CC_MiddleInitial ");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("OSR_DOB") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_DOB ";
        						System.out.println("Set OSR_DOB");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("CC_SEALEDENVELOPEONFILE") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_SEALEDENVELOPEONFILE ";
        						System.out.println("Set CC_SEALEDENVELOPEONFILE");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("OSR_RetirementDate") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_RetirementDate ";
        						System.out.println("Set OSR_RetirementDate");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("CC_CLOSEDCASEDATE") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_CLOSEDCASEDATE  ";
        						System.out.println("Set CC_CLOSEDCASEDATE");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("CC_REOPENEDCASEDATE") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "CC_REOPENEDCASEDATE  ";
        						System.out.println("Set CC_REOPENEDCASEDATE");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					
        					}else if (attrName.equalsIgnoreCase("OSR_SectionTitle") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_SectionTitle";
        						System.out.println("Set OSR_SectionTitle");
        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
        					}else if (attrName.equalsIgnoreCase("OSR_SecondID") && itemDDO.getDataByName(attrName)!=null) {
        						attrValue = itemDDO.getDataByName(attrName).toString();
        						aObject = itemDDO.getDataByName(attrName);
        						attrName = "OSR_SecondID ";
        						System.out.println("Set OSR_SecondID");
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
	            	String mimeType = part.getMimeType();
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
	            }
	            //ddo.setContentFromClientFile.add();
	            //ddoTarget.add();
	            
	            ddoTarget.add();
		        
		        ////////end copy////////
		        
	            	//if(!dsExtICM.isCheckedOut(itemDDO))
	            	//	dsICM.checkOut(itemDDO);
	                // Create a DDO for Targett of Document Model
	                //dsExtICM.moveObject(itemDDO,ddoTarget, DKConstant.DK_CM_CHECKIN+DKConstant.DK_CM_KEEP_IN_AUTOFOLDER);
	                System.out.println("After Copy Item Type.");
	                
	                
	                //}
	               
	            //}//end while
	            //itemDDO = null;
	            //ddoTarget = null;
	            
	            //////////////////////Ending////////////////////
	            
	            System.out.println("loopThru OSR Items add itemDDO to dkSubFolder & checking in ddoSectionTitleFolder<<<<");
	            dkSubFolder.addElement(ddoTarget);
	            //update any new created section title including empth 'PHOTOS'
	            ddoSectionTitleFolder.update();
	            //dsICM.checkIn(ddoSectionTitleFolder);
	            
	            System.out.println("loopThru OSR Items checking in ddoNameFolder<<<");
	            ddoNameFolder.update();
	            //dsICM.checkIn(ddoNameFolder);
	            
	            
	        }else {
              System.out.println("loopThruItems: Could not find item.");
			}
			/*ddoTarget.add();
	        
	        ////////end copy////////
	        
            	
            
            //////////////////////Ending////////////////////
            
            System.out.println("loopThru OSR Items add itemDDO to dkSubFolder & checking in ddoSectionTitleFolder<<<<<<<");
            dkSubFolder.addElement(ddoTarget);
            ddoSectionTitleFolder.update();
            dsICM.checkIn(ddoSectionTitleFolder);
            
            System.out.println("loopThru OSR Items checking in ddoNameFolder<<<<<<<<<<");
            //dkNameFolder.addElement(ddoSectionTitleFolder);
            ddoNameFolder.update();
            dsICM.checkIn(ddoNameFolder);
            */
            itemDDO = null;
            ddoTarget = null;
            
          }//end each item inside section title
          ddoSectionTitleFolder.update();
          dsICM.checkIn(ddoSectionTitleFolder);
          
          ddoNameFolder.update();
          dsICM.checkIn(ddoNameFolder);
        }//end for each section titles
        System.out.println();
        System.out.println("OUTSIDE WHILE LOOP!");
      }
      
      insert.close();
      insert = null;
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
      //}
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
      System.out.println("  --> Attribute " + name + " does not exist, so create it.");
      attr = (DKAttrDefICM)dsDef.createAttr();
      attr.setName(name);
      attr.setDescription("Sample attribute named: " + name);
      attr.setType(type);
      if (size != 0) { attr.setSize(size);
      }
      attr.add();
      System.out.println("  --> Attribute " + name + " created.");
    } else {
      System.out.println("  --> Attribute " + name + " already exists.");
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
  
  public static void printFolderContents(DKDDO folder) throws Exception{
      
      String folderItemId = ((DKPidICM)folder.getPidObject()).getItemId();

      // Get the DKFolder object.        
      short dataid = folder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER); 
      if(dataid==0)
          throw new Exception("No DKFolder Attribute Found!  DDO is either not a Folder or Folder Contents have not been explicitly retrieved.");
      DKFolder dkFolder = (DKFolder) folder.getData(dataid); 
      
      // Print the list        
      System.out.println("Folder ("+folderItemId+") Contents:");
      dkIterator iter = dkFolder.createIterator(); // Create an Iterator go to through DDO Collection.
      while(iter.more()){                          // While there are items still to be printed, continue
          DKDDO ddo = (DKDDO) iter.next();         // Move pointer to next element & return that object.
          System.out.println("     Item Id:  "+((DKPidICM)ddo.getPidObject()).getItemId() +" ("+ddo.getPidObject().getObjectType()+")");
      }
  }
  public void exit() {
    System.out.println("Calling system exit.");
    System.exit(1);
  }
}