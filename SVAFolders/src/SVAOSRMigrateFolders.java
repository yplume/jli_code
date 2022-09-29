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

public class SVAOSRMigrateFolders
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
  private ResultSet rs2 = null;
  private ResultSet rs3 = null;
  private ResultSet rs4 = null;
  
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
  private String[] docTypes = { "BASIC_SERVICE_DATE", "CANDIDATES_CASE", "EDUCATION_NEW", "GENERAL", "LONG_SERVICE", 
		    "OFFICER_REVIEW_BOARD", "OVERSEAS_OUT_OF_TERRITORY", "PROMOTION_TO_GLORY", "REVIEWS_ACR", "REVIEWS_FIVE_YEAR", "REVIEWS_SERVICE", "SPECIAL_CIRCUMSTANCES" };
		  
  //final String queryCheck = "SELECT count(*) FROM ICMADMIN.EXPORTLOG where itemid= ?";
  //final String queryInsert = "INSERT INTO FOLDERLOG (PRIMARYID, DATE) values (?,?)";
  //final String queryInsertFolder = "INSERT INTO FOLDERLOG (FOLDERNAME, DATE) values (?,?)";
  
  //String queryInsertMissing = "INSERT into ICMADMIN.MISSIMGS (ITEMOBJ, ERRMSG) VALUES (?,?)";
	
  public static void main(String[] arg) {
    System.out.println("main ");
    
    SVAOSRMigrateFolders sva = new SVAOSRMigrateFolders();
  }
  

  public SVAOSRMigrateFolders()
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
      _startDate = p.getProperty("STARTDATE");
      _endDate = p.getProperty("ENDDATE");
    }
    catch (Exception ex) {
      System.out.println("Error out-->" + ex.getMessage());
      
    }
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
  

  public void loopThruItems()
  {
    stmt = null;
    rs = null;
    stmt1 = null;
    
    rs1 = null;
    rs2 = null;
    rs3 = null;
    rs4 = null;
    sql = null;
    sql1 = null;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
    try
    {
    	
      System.out.println(timestamp);
      
      System.out.println("user ........." + url + _cmDBName + _cmDBPassword);
      conn = DriverManager.getConnection(url, _cmDBUserName, _cmDBPassword);
      
      System.out.println("After getConnection");
      
      stmt 	= conn.createStatement();
      stmt1 = conn.createStatement();
      //stmt2	= conn.createStatement();
      
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR001 WHERE CREATETS>='"+_startDate+"' AND CREATETS<='"+_endDate+"'";
      //remove DOB, CC_SEALEDENVELOPEONFILE, CC_CLOSEDCASEDATE, OSR_SecondID, OSR_RetirementDate, CC_REOPENEDCASEDATE
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR001 WHERE itemid>='"+ itemIDStart +"' and itemid<='"+ itemIDEnd +"'"; 
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, CC_MiddleInitial, osr_dob, CC_SEALEDENVELOPEONFILE, OSR_RetirementDate, CC_CLOSEDCASEDATE, CC_REOPENEDCASEDATE, OSR_PrimaryID, OSR_SecondID FROM OSR001 left join FOLDERLOG on OSR_PrimaryID = foldername  WHERE foldername is null and itemid>='"+ itemIDStart +"' and itemid<='"+ itemIDEnd +"'";
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, OSR_PrimaryID FROM OSR2001";
      //String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, OSR_PrimaryID FROM OSR001 WHERE OSR_PrimaryID='8-04-990797-1' or OSR_PrimaryID='8-04-990796-1'";
      //find all special section titles without underscore those need change to with underscores, CANDIDATES ASE -> CANDIDATES_ASE S.T.
      /*StringBuffer getUnderScoreST = new StringBuffer("select itemid, osr_sectiontitle, osr_primaryid from osr2001 where osr_sectiontitle='BASIC SERVICE DATE' ");
      getUnderScoreST.append(" or osr_sectiontitle='CANDIDATES ASE' ");
      getUnderScoreST.append(" or osr_sectiontitle='EDUCATION'");
      getUnderScoreST.append(" or osr_sectiontitle='General'");
      getUnderScoreST.append(" or osr_sectiontitle='LONG SERVICE'");
      getUnderScoreST.append(" or osr_sectiontitle='OFFICER REVIEW BOARDD'");
      getUnderScoreST.append(" or osr_sectiontitle='OVERSEAS / OUT OF TERRITORY'");
      getUnderScoreST.append(" or osr_sectiontitle='REVIEWS - ACR'");
      getUnderScoreST.append(" or osr_sectiontitle='REVIEWS - SERVICE'");
      getUnderScoreST.append(" or osr_sectiontitle='REVIEWS FIVE YEAR'");
      getUnderScoreST.append(" or osr_sectiontitle='SPECIAL_CIRCUMSTANCES'");
       *///String mainSQL = "select itemid, osr_primaryid from employeessv2001";
      //StringBuffer getUnderScoreST = new StringBuffer("select itemid, osr_sectiontitle, osr_primaryid from osr2001 where osr_sectiontitle='BASIC_SERVICE_DATE' ");
      StringBuffer getUnderScoreST = new StringBuffer("select itemid, osr_sectiontitle, osr_primaryid from ");
      getUnderScoreST.append(" (SELECT itemid,  osr_sectiontitle, osr_primaryid,");
      getUnderScoreST.append("            ROW_NUMBER() OVER (PARTITION BY OSR_SECTIONTITLE, OSR_PRIMARYID ORDER BY itemid DESC) as sectiontitle_rank");
      getUnderScoreST.append(" FROM osr2001) ranked where sectiontitle_rank = 1 and ");
      getUnderScoreST.append(" (osr_sectiontitle='BASIC_SERVICE_DATE' ");
      getUnderScoreST.append(" or osr_sectiontitle='CANDIDATES_CASE' ");
      getUnderScoreST.append(" or osr_sectiontitle='EDUCATION_NEW'");
      getUnderScoreST.append(" or osr_sectiontitle='GENERAL'");
      getUnderScoreST.append(" or osr_sectiontitle='LONG_SERVICE'");
      getUnderScoreST.append(" or osr_sectiontitle='OFFICER_REVIEW_BOARD'");
      getUnderScoreST.append(" or osr_sectiontitle='OVERSEAS_OUT_OF_TERRITORY'");
      getUnderScoreST.append(" or osr_sectiontitle='REVIEWS_ACR'");
      getUnderScoreST.append(" or osr_sectiontitle='REVIEWS_SERVICE'");
      //getUnderScoreST.append(" or osr_sectiontitle='REVIEWS_FIVE_YEAR'");
      //getUnderScoreST.append(" or osr_sectiontitle='PROMOTION_TO_GLORY'");
      getUnderScoreST.append(" or osr_sectiontitle='SPECIAL_CIRCUMSTANCES')");
      
      //insertFolder 		= conn.prepareStatement(queryInsertFolder);
      //insertMissing  	= conn.prepareStatement(queryInsertMissing);
		
      System.out.println("After getUnderScoreST..." + getUnderScoreST);
      rs = stmt.executeQuery(getUnderScoreST.toString());
      System.out.println("After rs1...");
      String underScoreSTItemID = "";
      String sectionTitle = "";
      String primID = "";
      int count = 0;
      
      //loop all underscore section titles
      while (rs.next()) {
    	System.out.println();
    	System.out.println();
        count++;
       
        System.out.println("timestamp = " + new Timestamp(System.currentTimeMillis()));
        System.out.println("Section title COUNT = " + count);
        underScoreSTItemID 	= rs.getString("itemid");
        sectionTitle 		= rs.getString("osr_sectiontitle");
        primID 				= rs.getString("OSR_PrimaryID");
        System.out.println("underScoreSTItemID	= " + underScoreSTItemID);
        
        String underScoreSourceItemIDQuery = "select * from ICMstlinks001001 where targetitemid=  '" + underScoreSTItemID + "'";
        
        rs1 = stmt1.executeQuery(underScoreSourceItemIDQuery);
        
        String underScoreSourceItemID = null;
        	
        if(rs1!=null && rs1.next()){
        	underScoreSourceItemID = rs1.getString("sourceitemid");
        }
        Date date = new Date();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			
		System.out.println();
      
		String STTableName = null;
		
		if (sectionTitle.equalsIgnoreCase("OVERSEAS_OUT_OF_TERRITORY")) {
			sectionTitle = "OVERSEAS / OUT OF TERRITORY";
			STTableName = "icmut01390001";
			//STTableName = "icmut01147001";
		}
		else if (sectionTitle.equalsIgnoreCase("REVIEWS_ACR")) {
			sectionTitle = "REVIEWS - ACR";
			STTableName = "REVIEWS_ACR001";
		}
		/*else if (sectionTitle.equalsIgnoreCase("REVIEWS_FIVE_YEAR")) {
			sectionTitle = "REVIEWS - FIVE YEAR";
			//STTableName = "icmut01384001";
			STTableName = "icmut01121001";
		}*/
		else if (sectionTitle.equalsIgnoreCase("CANDIDATES_CASE")) {
			sectionTitle = "CANDIDATES CASE";
			STTableName = "CANDIDATES_CASE001";
		}
		else if (sectionTitle.equalsIgnoreCase("REVIEWS_SERVICE")) {
			sectionTitle = "REVIEWS - SERVICE";
			STTableName = "REVIEWS_SERVICE001";
		}
		else if (sectionTitle.equalsIgnoreCase("BASIC_SERVICE_DATE")) {
			sectionTitle = "BASIC SERVICE DATE";
			STTableName = "icmut01352001";
			//STTableName = "icmut01081001";
		}
		else if (sectionTitle.equalsIgnoreCase("EDUCATION_NEW")) {
			sectionTitle = "EDUCATION";
			STTableName = "EDUCATION_NEW001";
		}
		else if (sectionTitle.equalsIgnoreCase("LONG_SERVICE")) {
			sectionTitle = "LONG SERVICE";
			STTableName = "LONG_SERVICE001";
		}
		else if (sectionTitle.equalsIgnoreCase("OFFICER_REVIEW_BOARD")) {
			sectionTitle = "OFFICER REVIEW BOARD";
			STTableName = "icmut01372001";
			//STTableName = "icmut01151001";
		}
		/*else if (sectionTitle.equalsIgnoreCase("PROMOTION_TO_GLORY")) {
			sectionTitle = "PROMOTION TO GLORY";
			//STTableName = "icmut01378001";
			STTableName = "icmut01137001";
		}*/
		else if (sectionTitle.equalsIgnoreCase("SPECIAL_CIRCUMSTANCES")) {
			sectionTitle = "SPECIAL CIRCUMSTANCES";
			STTableName = "icmut01388001";
			//STTableName = "icmut01113001";
		}
		else if (sectionTitle.equalsIgnoreCase("General")) {
			sectionTitle = "GENERAL";
			STTableName = "GENERAL001";
		}
		System.out.println("sectionTitle 	= " + sectionTitle + underScoreSourceItemID );
		
		//find matched without underscore section title so that  need to migrate to underscore ones
		String getNoUnderScoreSetionTitleSQL = "SELECT itemid, osr_sectiontitle, OSR_PrimaryID FROM OSR2001 where osr_sectiontitle = '" + sectionTitle + "' and OSR_PrimaryID='" + primID + "'";
		System.out.println("getNoUnderScoreSetionTitleSQL 	= " + getNoUnderScoreSetionTitleSQL );
		rs1 = stmt1.executeQuery(getNoUnderScoreSetionTitleSQL);
      
		String tid = null; 
		//String pid = rs.getString("OSR_PrimaryID"); 
      
		//loop thru all match section titles, if nounderscore exist, then find source folder itemid. we will update old folder to new folder
		if(rs1!=null && rs1.next()){
    	  
			tid = rs1.getString("itemid"); 
			
			String getOldSourceItemIDQuery = "select sourceitemid from ICMstlinks001001 where targetitemid=  '" + tid + "'";
			
			System.out.println("getOldSourceItemIDQuery 	= " + getOldSourceItemIDQuery );
			
			rs1 = stmt1.executeQuery(getOldSourceItemIDQuery);
			System.out.println("1111");
			
			if(rs1!=null && rs1.next()) {
				System.out.println("2222");
				
				String osid = rs1.getString("sourceitemid");
				System.out.println("3333");
				
				System.out.println("osid 	= " + osid );
			
				if(!underScoreSourceItemID.equalsIgnoreCase(osid)){
				
			//Find source\folder’s parent itemid
				String getParentItemIDQuery = "select sourceitemid from ICMstlinks001001 where targetitemid=  '" + osid + "'";
				
				rs1 = stmt1.executeQuery(getParentItemIDQuery);
				
				String parentItemID = null;
				
				if(rs1!=null && rs1.next()) {
					parentItemID  = rs1.getString("sourceitemid");
				}
			/*
			//Find all source\folder (without underscore) itemid 
			String oldTargetItemIDQuery = "select targetitemid from ICMstlinks001001 where sourceitemid=  '" + osid + "'";
			
			rs2 = stmt.executeQuery(oldTargetItemIDQuery);
			
			//check without underscore one exists then
			if(rs2!=null && rs2.next()){
			*/	
				System.out.println("getOldSourceItemIDQuery 	= " + getOldSourceItemIDQuery );
				//String oldTargetItemID = rs2.getString("targetitemid");
				
				String updateQuery = "update ICMstlinks001001 set sourceitemid= '" + underScoreSourceItemID + "' where sourceitemid='"+osid+"'";
			
				stmt1.executeUpdate(updateQuery);
				
				System.out.println("updateQuery="+updateQuery );
				
				//remove link from w/o underscore folder to EmployeeSV folder
				String deleteQuery = "delete from ICMstlinks001001 where  SOURCEITEMID= '" + parentItemID + "' and  targetitemid= '" +osid+ "'";
				
				//remove the without underscore folder from section title item type table
				String deleteFolderQuery = "delete from "+STTableName+" where  itemid = '" + osid + "' ";
				
				stmt1.executeUpdate(deleteQuery);
				stmt1.executeUpdate(deleteFolderQuery);
			//}
				System.out.println("deleteFolderQuery="+deleteFolderQuery );
				System.out.println("After Deleting ...................");
				
				}
			}
		}
          
        System.out.println("OUTSIDE IF!");
      }
      System.out.println("444");
      rs.close();
      rs = null;
      System.out.println("5555");
      
      rs1.close();
      rs1 = null;
      System.out.println("666");
      
      /*rs2.close();
      rs2 = null;
      rs3.close();
      rs3 = null;
      rs4.close();
      rs4 = null;*/
      stmt.close();
      stmt = null;
      
      stmt1.close();
      stmt1 = null;
      conn.close();
      conn = null;
      
      System.out.println("----------------DONE!------------------");
      //}
    }
    catch (Exception ex) {
      System.out.println(" Error Exception loopThruItems =" + ex.getMessage());
      
      System.out.println(" set error to class ");
      
    } finally {
      try {
       
        if (rs1 != null)
            rs1.close();
        
        /*if (rs2 != null)
            rs2.close();
        if (rs3 != null)
            rs3.close();
        if (rs4 != null)
            rs4.close();
        */
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
  
 
  public void exit() {
    System.out.println("Calling system exit.");
    System.exit(1);
  }
}