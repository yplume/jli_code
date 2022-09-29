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

public class SVAOSRRemoveFolders
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
  //private String[] docTypes = { "BASIC_SERVICE_DATE", "CANDIDATES_CASE", "EDUCATION_NEW", "GENERAL", "LONG_SERVICE", 
//  "OFFICER_REVIEW_BOARD", "OVERSEAS_OUT_OF_TERRITORY", "REVIEWS_ACR", "REVIEWS_SERVICE", "SPECIAL_CIRCUMSTANCES" };

  //production
  private String[] docTypes = { "icmut01352001", "CANDIDATES_CASE001", "EDUCATION_NEW001", "GENERAL001", "LONG_SERVICE001", 
		  "icmut01372001", "icmut01390001", "REVIEWS_ACR001", "REVIEWS_SERVICE001", "icmut01388001" };

  //testing
  //private String[] docTypes = { "icmut01081001", "CANDIDATES_CASE001", "EDUCATION_NEW001", "GENERAL001", "LONG_SERVICE001", 
  //"icmut01151001", "icmut01147001", "REVIEWS_ACR001", "REVIEWS_SERVICE001", "icmut01113001" };
 // private String[] docTypes = { "icmut01081001", "CANDIDATES_CASE001", "EDUCATION_NEW001", "icmut01113001"};
		   // "OFFICER_REVIEW_BOARD001", "icmut01390001", , "REVIEWS_ACR001", "icmut01384001", "REVIEWS_SERVICE001", "LONG_SERVICE001",  "SPECIAL_CIRCUMSTANCES" };
		  
  //final String queryCheck = "SELECT count(*) FROM ICMADMIN.EXPORTLOG where itemid= ?";
  //final String queryInsert = "INSERT INTO FOLDERLOG (PRIMARYID, DATE) values (?,?)";
  //final String queryInsertFolder = "INSERT INTO FOLDERLOG (FOLDERNAME, DATE) values (?,?)";
  
  //String queryInsertMissing = "INSERT into ICMADMIN.MISSIMGS (ITEMOBJ, ERRMSG) VALUES (?,?)";
	
  public static void main(String[] arg) {
    System.out.println("main ");
    
    SVAOSRRemoveFolders sva = new SVAOSRRemoveFolders();
  }
  

  public SVAOSRRemoveFolders()
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
      //_itemType = p.getProperty("CMITEMTYPE");
      //_targetitemType = p.getProperty("CMTARGETITEMTYPE");
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
  
      RemoveItems();
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
  

  public void RemoveItems()
  {
    stmt = null;
    rs = null;
    stmt1 = null;
    
    rs1 = null;
    rs2 = null;
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
      
      for (int i = 0; i < docTypes.length; i++) {
    	  //1. Find all unused folder itemid 
    	  String getUnusedFolderSQL = "select ITEMID from " + docTypes[i] + " a left join ICMstlinks001001  b on a.itemid=b.SOURCEITEMID where b.SOURCEITEMID is null";
    	  rs = stmt.executeQuery(getUnusedFolderSQL);
          System.out.println("After rs..."+getUnusedFolderSQL);
          String deleteParentLinkItemID = "";
         
          while(rs.next()){
        	  
        	  deleteParentLinkItemID = rs.getString("ITEMID");
	    	  //2.	Remove from link to parent item type
	    	  String deleteParentLinkSQL = "Delete from ICMstlinks001001 where targetitemid= '" + deleteParentLinkItemID + "'";
	    	  stmt1.executeUpdate(deleteParentLinkSQL);
	    	  //3.	Remove empty folder
	    	  String deleteEmptyFolderSQL = "Delete from " + docTypes[i] + " where itemid= '" + deleteParentLinkItemID + "'";
	    	  stmt1.executeUpdate(deleteEmptyFolderSQL);
	    	  System.out.println("deleteParentLinkSQL ==="+deleteParentLinkSQL);
	    	  
	    	  System.out.println("deleteEmptyFolderSQL ==="+deleteEmptyFolderSQL);
          }
     
        System.out.println("OUTSIDE While!");
        //test
        //String updateSQL1 = "Update icmut01121001 set attr0000001042= 'REVIEWS_FIVE_YEAR' where attr0000001042='REVIEWS - FIVE YEAR'";
        //production
        String updateSQL1 = "Update icmut01384001 set attr0000001532= 'REVIEWS_FIVE_YEAR' where attr0000001532='REVIEWS - FIVE YEAR'";
        stmt1.executeUpdate(updateSQL1);
        System.out.println("updateSQL1 ==="+updateSQL1);
        //test
        //String updateSQL2 = "Update icmut01137001 set attr0000001042= 'PROMOTION_TO_GLORY' where attr0000001042='PROMOTION TO GLORY'";
        //production
        String updateSQL2 = "Update icmut01378001 set attr0000001532= 'PROMOTION_TO_GLORY' where attr0000001532='PROMOTION TO GLORY'";
        stmt1.executeUpdate(updateSQL2);
        System.out.println("updateSQL2 ==="+updateSQL2);
        
      }
      
      System.out.println("OUTSIDE For!");

      rs.close();
      rs = null;
      System.out.println("5555");
      
      
      
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