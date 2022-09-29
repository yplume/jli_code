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
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

public class SVAOSRFoldersV2
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
  private Statement stmt2 = null;
  private ResultSet rs1 = null;
  
  private String sql = null;
  private String sql1 = null;
  private String url = null;
  
  private String itemIDStart = "";
  private String itemIDEnd = "";
	
  private String errorMessage = null;
  boolean errorOccurred = false;
  //private int failIndex = 0;
  
  private String mimeType = null;
  private static final String SAMPLE_HIER_FOLDER_ITEM_TYPE_NAME =  "EmployeesSV2";
  
  //private String[] docTypes = { "Appointments", "Basic_Service_Date", "Candidates_Case", "Citizenship", "Commissioning", "Education_New", "Family", "Finances", "General", "Health", "Long_Service", 
  //"Officer_Review_Board", "Overseas_Out_Of_Territory", "Photos", "Promotion_To_Glory", "Promotions", "Retirement", "Reviews_Acr", "Reviews_Five_Year", "Reviews_Service", "Special_Circumstances" };

  private String[] docTypes = { "APPOINTMENTS", "BASIC_SERVICE_DATE", "CANDIDATES_CASE", "CITIZENSHIP", "KEEPSAFE", "CONFIDENTIAL", "COMMISSIONING", "EDUCATION_NEW", "FAMILY", "FINANCES", "GENERAL", "HEALTH", "LONG_SERVICE", 
		    "OFFICER_REVIEW_BOARD", "OVERSEAS_OUT_OF_TERRITORY", "PHOTOS", "PROMOTION_TO_GLORY", "PROMOTIONS", "RETIREMENT", "REVIEWS_ACR", "REVIEWS_FIVE_YEAR", "REVIEWS_SERVICE", "SPECIAL_CIRCUMSTANCES" };

  final String queryCheck = "SELECT count(*) FROM ICMADMIN.EXPORTLOG where itemid= ?";
  //final String queryInsert = "INSERT INTO FOLDERLOG (PRIMARYID, DATE) values (?,?)";
  final String queryInsertFolder = "INSERT INTO FOLDERLOG (FOLDERNAME, DATE) values (?,?)";
  
  String queryInsertMissing = "INSERT into ICMADMIN.MISSIMGS (ITEMOBJ, ERRMSG) VALUES (?,?)";
	
  public static void main(String[] arg) {
    System.out.println(" main ");
    
    SVAOSRFoldersV2 sva = new SVAOSRFoldersV2(arg);
  }
 
  public SVAOSRFoldersV2(String arg[])
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
      _targetitemType = p.getProperty("CMTARGETITEMTYPE");
      _cmDBName = p.getProperty("CMDBNAME");
      _cmDBUserName = p.getProperty("DBUSER");
      _cmDBPassword = p.getProperty("CMDBPASSWORD");
      //_startDate = p.getProperty("STARTDATE");
      //_endDate = p.getProperty("ENDDATE");
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
  
      loopThruItems();
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
  
  public String CapitalizeWords (String itemTypeName){
  	
  	  	String word = itemTypeName.toLowerCase();
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
  
  public void loopThruItems()
  {
    stmt = null;
    rs = null;
    sql = null;
    stmt1 = null;
    rs1 = null;
    sql1 = null;
    DKDatastoreICM dsICM = null;
    PreparedStatement insertMissing = null;
    PreparedStatement insertFolder = null;
    DKDatastoreExtICM dsExtICM = null;
    //dkIterator iter = null;
    
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
    try
    {
    	
      System.out.println(timestamp);
      
      System.out.println("user ........." + url + _cmDBName + _cmDBPassword);
      System.out.println("conection pooling.............");
      
      dsICM = new DKDatastoreICM();

      System.out.println("creatingg connection pooling ->" + _cmUs + _cmPw + _cmDBName);
      
      dsICM = ICMConnectionPool.getConnection(_cmUs, _cmPw, _cmDBName);
      System.out.println("After creatingg connection pooling" + url + _cmDBName + _cmDBPassword);
      
      dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT);
      
      conn = DriverManager.getConnection(url, _cmDBUserName, _cmDBPassword);
      
      System.out.println("After getConnection");
      
      stmt 	= conn.createStatement();
      stmt1 = conn.createStatement();
      
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR2001 left join FOLDERLOG on OSR_PrimaryID = foldername  WHERE foldername is null and itemid>='A1001001A18G05B61830B00254' and itemid<='A1001001A18G05B61835B00282'";
      ////String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR2001 left join FOLDERLOG on OSR_PrimaryID = foldername  WHERE foldername is null and OSR_PrimaryID = '2-04-192843-0'";
      String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, OSR_RetirementDate, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR2001 left join FOLDERLOG on OSR_PrimaryID = foldername  WHERE foldername is null and cc_lastname is not null order by cc_lastname asc";
      
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, OSR_RetirementDate, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR2001 left join FOLDERLOG on OSR_PrimaryID = foldername  WHERE  cc_lastname is not null order by cc_lastname desc fetch first 335 rows only";
      
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR2001 left join FOLDERLOG on OSR_PrimaryID = foldername  WHERE foldername is null and cc_lastname is not null order by cc_lastname asc FETCH FIRST 10 ROWS ONLY";
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR001 WHERE OSR_PrimaryID='2-04-192843-0' or OSR_PrimaryID='8-04-990791-1'";
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, OSR_PrimaryID FROM OSR001 WHERE OSR_PrimaryID='8-04-990797-1' or OSR_PrimaryID='8-04-990796-1'";
    	  
      insertFolder 		= conn.prepareStatement(queryInsertFolder);
      insertMissing  	= conn.prepareStatement(queryInsertMissing);
		
      System.out.println("After getNameFoldersSQL..." + getNameFoldersSQL);
      rs1 = stmt1.executeQuery(getNameFoldersSQL);
      System.out.println("After rs1...");
      String fName = "";
      String lName = "";
      String dob = "";
      String rdate = "";
      String primID = "";
      String midint = "";
      //String sealed = "";
      //String closed = "";
      String reopen = "";
      String secID  = "";
      String dobq = "";
      String rdateq = "";
     // String primIDq = "";
      String midintq = "";
      //String sealedq = "";
      //String closedq = "";
      String reopenq = "";
      String secIDq  = "";
      int count = 0;
     
      //DKDDO empDDO = dsICM.createDDOFromPID("97 3 ICM12 LSconnection12 EmployeesSV159 26 A1001001A20D16B53312J0022918 A20D16B53312J002291 14 1177");
     //test
     // DKDDO empDDO = dsICM.createDDOFromPID("97 3 ICM12 LSconnection12 EmployeesSV159 26 A1001001A20D16B53312J0022918 A20D16B53312J002291 14 1177");
      //production
      DKDDO empDDO = dsICM.createDDOFromPID("92 3 ICM8 icmsvadb12 EmployeesSV259 26 A1001001A22H26B75223A4622318 A22H26B75223A462231 14 1438");
      
      while (rs1!=null && rs1.next()) { //while #1
    	
        count++;
       
        System.out.println("timestamp = " + new Timestamp(System.currentTimeMillis()));
        System.out.println("FOLDER COUNT = " + count);
        fName 	= rs1.getString("cc_firstname");
        lName 	= rs1.getString("cc_lastname").replace( "'" , "''" );
        dob 	= rs1.getString("osr_dob");
        rdate	= rs1.getString("OSR_RetirementDate");
        primID 	= rs1.getString("OSR_PrimaryID");
        midint	= rs1.getString("CC_MiddleInitial");
        //sealed	= rs1.getString("CC_SEALEDENVELOPEONFILE");
        //closed	= rs1.getString("CC_CLOSEDCASEDATE");
        reopen	= rs1.getString("CC_REOPENEDCASEDATE");
        secID	= rs1.getString("OSR_SecondID");
        //lName = lName.replace( "'" , "''" );
        System.out.println("OSR fName 	= " + fName);
        System.out.println("OSR lName 	= " + lName);
        System.out.println("OSR dob 	= " + dob);
        System.out.println("OSR rdate 	= " + rdate);
       
        System.out.println("OSR primID, OSR_SecondID 	= " + primID +", "+ rs1.getString("OSR_SecondID"));
        
        DKDDO ddoNameFolder = dsICM.createDDO("EmployeesSV2", DKConstant.DK_CM_FOLDER);
        
        System.out.println("====dsICM.getRootFolder() 	= " + dsICM.getRootFolder());
	    setHierarchicalAttrs(ddoNameFolder, empDDO, lName+" "+fName);
        //setHierarchicalAttrs(ddoNameFolder, dsICM.getRootFolder(), primID);
	    
	    
        System.out.println("---------------CREATE TOP FOLDERS!!-----------");
        
        ddoNameFolder.add();
        
        System.out.println("Insert into Folder log table.");
        Date date = new Date();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		insertFolder.setString(1, primID);
		System.out.println("primID ="+primID);
		insertFolder.setDate(2, sqlDate);
		System.out.println("After setdate");
		insertFolder.addBatch();
		//System.out.println("after insert");
		insertFolder.executeBatch();
		System.out.println("After insert execute");
		
        System.out.println("ADD ATTRIBUTES To Top ddoNameFolder");
        
        if(fName!="" && fName!=null)
            ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_FirstName"), fName);
       
        if(lName!="" && lName!=null)
            ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "CC_LastName"), lName);
        
        if(primID != "" && primID != null)
			ddoNameFolder.setData(ddoNameFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_PrimaryID"), primID);

		System.out.println("Open ddoNameFolder");
        
          System.out.println("----------Find SectionTitle and run query--------");
          System.out.println("Is 999999 = " + primID.trim().equalsIgnoreCase("9-99-999999-9"));
          //find all available section titles of this employee (primary ID) and create folders with _F
          //then loop thru all OSR2 items and section title (keywordname) it belong to
          for (int i = 0; i < docTypes.length; i++) {
          //check ACLCODE equal to 1251(OS&R), then update ACLCODE of osr2 items (ACL OS&R in prod 1251, in VM is 1054)
        	  String findSectionTitleQuery = null;
        	  if(primID.trim().equalsIgnoreCase("9-99-999999-9"))
        	  		findSectionTitleQuery = "select itemtypeid, a.itemid, a.aclcode, OSR_SECTIONTITLE, targetitemid, sourceitemid, keywordname from osr2001 a inner join ICMstlinks001001 b on a.itemid=targetitemid inner join icmstitems001001 c on sourceitemid = c.itemid inner join icmstnlskeywords on itemtypeid=keywordcode where osr_sectiontitle is not null and keywordname = '" + docTypes[i] + "' and keywordclass=2 and cc_lastname ='" + lName + "' and cc_firstname='" + fName + "'";
        	  else
        			findSectionTitleQuery = "select itemtypeid, a.itemid, a.aclcode, OSR_SECTIONTITLE, targetitemid, sourceitemid, keywordname from osr2001 a inner join ICMstlinks001001 b on a.itemid=targetitemid inner join icmstitems001001 c on sourceitemid = c.itemid inner join icmstnlskeywords on itemtypeid=keywordcode where osr_sectiontitle is not null and keywordname = '" + docTypes[i] + "' and keywordclass=2 and OSR_PrimaryID ='" + primID + "'";
        	  
        	  //String findSectionTitleQuery = "select itemtypeid, a.itemid, a.aclcode, OSR_SECTIONTITLE, targetitemid, sourceitemid, keywordname from osr2001 a inner join ICMstlinks001001 b on a.itemid=targetitemid inner join icmstitems001001 c on sourceitemid = c.itemid inner join icmstnlskeywords on itemtypeid=keywordcode where a.ACLCODE = 1054 and osr_sectiontitle is not null and keywordclass=2 and OSR_PrimaryID ='" + primID + "'";
          System.out.println("findSectionTitleQuery = " + findSectionTitleQuery);
          
          //stmt = conn.createStatement();
          
          rs = stmt.executeQuery(findSectionTitleQuery);
          
          System.out.println("docTypes[i] = " +docTypes[i]);
          
          dsICM.checkOut(ddoNameFolder);
          
          if(rs.next()) {
          
        	  String sectionTitleName = docTypes[i];
        	  System.out.println("SectionTitleName = "+sectionTitleName);
        	  String acl = CapitalizeWords(sectionTitleName);
        	  //String acl = sectionTitleName.substring(0, sectionTitleName.indexOf("_"));
              System.out.println("ACL = "+acl);
              String sectionTitleItemTypeName = acl + "_F";
              
              //System.out.println("findSectionTitleQuery = " + findSectionTitleQuery);
              
              //rs = stmt.executeQuery(getFileSQL);
              
              System.out.println("Section Title    = " + sectionTitleName);
              System.out.println("sectionTitleItemTypeName    = " + sectionTitleItemTypeName);
               
              
              //dsICM.checkOut(ddoNameFolder);
              
              //create new docType folder under employee folder
              DKDDO ddoSectionTitleFolder = dsICM.createDDO(sectionTitleItemTypeName, DKConstant.DK_CM_FOLDER);
              
              setHierarchicalAttrs(ddoSectionTitleFolder, ddoNameFolder, acl);
              
              System.out.println("Created ddoSectionTitleFolder ");
              
              ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "OSR_SectionTitle"), acl);

              ddoSectionTitleFolder.add();
              
              dsICM.checkOut(ddoSectionTitleFolder);
              
              System.out.println("After checkout ddoSectionTitleFolder ");
              
              do {
            	  //dsICM.checkOut(ddoSectionTitleFolder);
	        	  String itemID = rs.getString(2);
	        	  System.out.println("***itemID = " + itemID);
          
         
          
	            System.out.println("ADD ATTRIBUTES second level ddoSectionTitleFolder");
	            
	            DKFolder dkSubFolder = (DKFolder)ddoSectionTitleFolder.getData(ddoSectionTitleFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKFOLDER));
	            
	            System.out.println("loopThru OSR Items checkout ddoSectionTitleFolder>>>>>");
	            
	            //dsICM.checkOut(ddoSectionTitleFolder);
	            
	            System.out.println("-------- Update items ACL inside SectionTitle ---------");
	            
	           
	            String targetItemType = acl + "_D";
	            	
	            short propId = ddoSectionTitleFolder.propertyId(DKConstantICM.DK_ICM_PROPERTY_ACL);
	            
	            ddoSectionTitleFolder.setProperty(propId, acl);
	            System.out.println("--------- Update items ACL inside SectionTitle ---->" + sectionTitleName + "----" + acl);
	            
	            //ddoSectionTitleFolder.update();
	            
	            System.out.println("itemID = " + itemID);
	
	            StringBuffer query = new StringBuffer();
	            query.append("//OSR2");
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
	            //dkResultSetCursor cursor = dsICM.execute(query.toString(), DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
	            //DKResults results = (DKResults)dsICM.evaluate(query.toString(), DKConstant.DK_CM_XQPE_QL_TYPE, options);
	            System.out.println("After Evaluating Query");
	            
													   
	            dkIterator iter = results.createIterator();
																 
	            
	            System.out.println("results ="+results.cardinality());
	            
	            DKDDO itemDDO = (DKDDO)iter.next();
	            //copying each document to new item type
	            //while (iter.more()){
		            
		            ////////////////////Move Items//////////////////
		            DKDDO ddoTarget = dsICM.createDDO(targetItemType, DKConstant.DK_CM_DOCUMENT);
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
		            
		           
			        while(iter.more()){ // for all attributes of the item
			        	aDef = (DKAttrDefICM) iter.next();
						aObject = null;
						
						try {
							attrName = aDef.getName();
							System.out.println("attrName = "+attrName);
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
	        						attrName = "OSR_DOB";
	        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue)); //"2001-08-12"
	        						
	        					}else if (attrName.equalsIgnoreCase("OSR_RetirementDate") && itemDDO.getDataByName(attrName)!=null) {
	        						attrValue = itemDDO.getDataByName(attrName).toString();
	        						//aObject = itemDDO.getDataByName(attrName);
	        						attrName = "OSR_RetirementDate";
	        						//System.out.println("Set OSR_RetirementDate");
	        						//ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
	        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), java.sql.Date.valueOf(attrValue)); //"2001-08-12"
	        					
	        					}else if (attrName.equalsIgnoreCase("CC_CLOSEDCASEDATE") && itemDDO.getDataByName(attrName)!=null) {
	        						attrValue = itemDDO.getDataByName(attrName).toString();
	        						//aObject = itemDDO.getDataByName(attrName);
	        						attrName = "CC_CLOSEDCASEDATE";
	        						//System.out.println("Set CC_CLOSEDCASEDATE");
	        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
	        					
	        					}else if (attrName.equalsIgnoreCase("CC_REOPENEDCASEDATE") && itemDDO.getDataByName(attrName)!=null) {
	        						attrValue = itemDDO.getDataByName(attrName).toString();
	        						//aObject = itemDDO.getDataByName(attrName);
	        						attrName = "CC_REOPENEDCASEDATE";
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
	        						attrName = "OSR_SecondID";
	        						//System.out.println("Set OSR_SecondID");
	        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
	        						
	        					}else if (attrName.equalsIgnoreCase("OSR_PrimaryID") && itemDDO.getDataByName(attrName)!=null) {
	        						attrValue = itemDDO.getDataByName(attrName).toString().trim();
	        						//aObject = itemDDO.getDataByName(attrName);
	        						attrName = "OSR_PrimaryID";
	        						//System.out.println("Set OSR_PrimaryID");
	        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
	        					
	        					}else if (attrName.equalsIgnoreCase("OSR_Description") && itemDDO.getDataByName(attrName)!=null) {
	        						attrValue = itemDDO.getDataByName(attrName).toString().trim();
	        						//aObject = itemDDO.getDataByName(attrName);
	        						attrName = "OSR_Description";
	        						//System.out.println("Set OSR_PrimaryID");
	        						ddoTarget.setData(ddoTarget.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName), attrValue); // basic string type
	        					}
	        					
	        					
	        					short propId1 = ddoTarget.propertyId(DKConstantICM.DK_ICM_PROPERTY_ACL);
	        		            
	        		            ddoTarget.setProperty(propId1, acl);
	        		            //System.out.println("--------- Update doc ACL inside SectionTitle 2----->" + sectionTitleName + "---" + acl);
	        		            
		        				
		        			} catch (Exception exc) {
		        				System.out.println("exc....="+exc.getMessage());
		        			}
		        		}
				        ////////Copy docs/////////////////////////////////////////////////
				      	//copying documents                                              /
				        ////////////////////////BLOCK COPY IMAGES/////////////////////////
			        	
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
				            	
				            	//System.out.println("part.getContent() = "+part.getContent());
				            	
				            	if(part.getContent()!=null){
					            	System.out.println("Inside part......");
					            
					            	String mimeType = part.getMimeType();
					            	part1.setMimeType(mimeType);
					            	part1.setContent(part.getContent());
				                
					                dkParts1.addElement(part1);
					                System.out.println("part1().getObjectType == "+part1.getPidObject().getObjectType());      
				            	}else{
				            		System.out.println("Missing Image; itemDDO = "+itemDDO.getPidObject().toString());
				            		insertMissing.setString(1, itemDDO.getPidObject().toString());
				            		System.out.println("Itemid ="+itemDDO.getPidObject().toString());
				            		insertMissing.setString(2, attrValue);
				            		System.out.println("After setdate");
				            		insertMissing.addBatch();
				            		insertMissing.executeBatch();
				            		System.out.println("After sql");
				            	      
				            	}
				            	part=null;
				            	
				            	part1=null;
				            	
				            }
				            dkParts1=null;
				            System.out.println("ddoTarget add");
				            ddoTarget.add();
				            
				            dkParts=null;
					        ////////end copy////////
				        
			            	System.out.println("After Copy Item parts.");
			                
			            
			            //////////////////////Ending////////////////////
			            
			            System.out.println("loopThru OSR2 Items add itemDDO to dkSubFolder & checking in ddoSectionTitleFolder<<<<");
			            dkSubFolder.addElement(ddoTarget);
			            //update any new created section title including empth 'PHOTOS'
			            ddoSectionTitleFolder.update();
			            //dsICM.checkIn(ddoSectionTitleFolder);
			            
			            System.out.println("loopThru OSR2 Items checking in ddoNameFolder<<<");
			            System.out.println();
			            System.out.println();
			            
			            //ddoNameFolder.update();
			            //dsICM.checkIn(ddoNameFolder);
			            
			            
			        }else {
		              System.out.println("loopThruItems: Could not find item.");
					}
					
				//////////////////////////////////BLOCK COPY IMAGES///////////////////////////////////		
						
				itemDDO = null;
	            ddoTarget = null;
	           
	            //} 
	          }while (rs!=null && rs.next());//end while for each primary ID #2
              ddoSectionTitleFolder.update();
              dsICM.checkIn(ddoSectionTitleFolder);
                  
          }//end if
          
          ddoNameFolder.update();
          dsICM.checkIn(ddoNameFolder);
          
      }//end for loop
        System.out.println();
        System.out.println("OUTSIDE WHILE LOOP!");
          
      }// end while #1
      
      insertFolder.close();
      insertFolder= null;
      insertMissing.close();
      insertFolder=null;
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
      
      System.out.println("----------------DONE------------------");
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
        if (insertFolder != null) {
        	insertFolder.close();
        	insertFolder = null;
        }
        if (insertMissing != null) {
        	insertMissing.close();
        	insertMissing = null;
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