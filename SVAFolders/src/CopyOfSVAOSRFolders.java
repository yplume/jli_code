import com.ibm.mm.sdk.common.DKAttrDefICM;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKDatastoreDefICM;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKFolder;
import com.ibm.mm.sdk.common.DKNVPair;
import com.ibm.mm.sdk.common.DKPidICM;
import com.ibm.mm.sdk.common.DKResults;
import com.ibm.mm.sdk.common.DKRetrieveOptionsICM;
import com.ibm.mm.sdk.common.dkIterator;
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


public class CopyOfSVAOSRFolders
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
  
  private String[] docTypes = { "APPOINTMENTS", "BASIC SERVICE DATE", "CANDIDATES CASE", "CITIZENSHIP", "COMMISSIONING", "EDUCATION", "FAMILY", "FINANCES", "General", "GENERAL", "HEALTH", "LONG SERVICE", 
    "OFFICER REVIEW BOARD", "OVERSEAS / OUT OF TERRITORY", "PHOTOS", "PROMOTION TO GLORY", "PROMOTIONS", "RETIREMENT", "REVIEWS - ACR", "REVIEWS - FIVE YEAR", "REVIEWS - SERVICE", "SPECIAL CIRCUMSTANCES" };
  
  final String queryCheck = "SELECT count(*) FROM ICMADMIN.EXPORTLOG where itemid= ?";
  final String queryInsert = "INSERT INTO EXPORTLOG (itemid, itemtype) values (?,?)";
  
  public static void main(String[] arg) {
    System.out.println("main ");
    
    CopyOfSVAOSRFolders sva = new CopyOfSVAOSRFolders();
  }
  

  public CopyOfSVAOSRFolders()
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
      options[0] = new DKNVPair("MAX_RESULTS", "1");
      options[1] = new DKNVPair("RETRIEVE_OPTION", dkRetrieveOptions);
      options[2] = new DKNVPair("End", null);
      

      System.out.println("Evaluating Query:  " + queryString.toString());
      results = (DKResults)dsICM.evaluate(queryString.toString(), (short)7, options);
      

      dkIterator iter = results.createIterator();
      parentDDO = (DKDDO)iter.next();
      
      if (parentDDO != null) System.out.println("Parent DDO found:  Type = '" + parentDDO.getObjectType() + "', CompID = '" + ((DKPidICM)parentDDO.getPidObject()).getComponentId() + "'"); else {
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
      System.out.println(" DB2 UserName:   " + _dbName);
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
    String itemid = null;
    String userid = null;
    String createts = null;
    String sqlString = null;
    String metaData = null;
    String metaDataTitle = null;
    stmt = null;
    rs = null;
    stmt1 = null;
    rs1 = null;
    sql = null;
    sql1 = null;
    Vector tempContainer = new Vector();
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
      




      String getNameFoldersSQL = "SELECT distinct cc_lastname, cc_firstname, osr_dob FROM OSR001";
      
      System.out.println("after getNameFoldersSQL..." + getNameFoldersSQL);
      rs1 = stmt1.executeQuery(getNameFoldersSQL);
      System.out.println("after rs1...");
      String fName = "";
      String lName = "";
      int count = 0;
      
      while (rs1.next()) {
        count++;
        System.out.println("COUNT = " + count);
        fName = rs1.getString("cc_firstname");
        lName = rs1.getString("cc_lastname");
        System.out.println("OSR fName = " + fName);
        System.out.println("OSR lName = " + lName);
        

        DKDDO ddoNameFolder = dsICM.createDDO("OSR", 2);
        System.out.println("---------------CREATE TOP FOLDERS!!-----------");
        
        ddoNameFolder.add();
        
        ddoNameFolder.setData(ddoNameFolder.dataId("ATTR", "CC_FirstName"), fName);
        
        ddoNameFolder.setData(ddoNameFolder.dataId("ATTR", "CC_LastName"), lName);
        System.out.println("ADD ATTRIBUTES ddoNameFolder");
 



        System.out.println("Open ddoNameFolder!!!!!!!!!!!!!!");
        DKFolder dkNameFolder = (DKFolder)ddoNameFolder.getData(ddoNameFolder.dataId("ATTR", "DKFolder"));
        
        String sectionTitle = "";
        String updateACLQuery = "UPDATE OSR001 SET ACLCODE = ? WHERE ITEMID = ?";
        PreparedStatement ps = null;
        ps = conn.prepareStatement(updateACLQuery);
        String getFileSQL = "";
        

        for (int i = 0; i < docTypes.length; i++) {
          System.out.println("-------------Create SectionTitle Folder-----------");
          String itemID = "";
          getFileSQL = "SELECT itemid FROM OSR001 where cc_lastname = '" + lName + "' and cc_firstname = '" + fName + "' and osr_sectiontitle = '" + docTypes[i] + "'";
          rs = stmt.executeQuery(getFileSQL);
          
          System.out.println("loopThru OSR Items docTypes =" + docTypes[i]);
          
          sectionTitle = docTypes[i];
          
          while (rs.next())
          {
            System.out.println("If docType exist then add Items && checkout ddoNameFolder>>>>>");
            dsICM.checkOut(ddoNameFolder);
            
            DKDDO ddoSectionTitleFolder = dsICM.createDDO("OSR", 2);
            
            System.out.println("Created ddoSectionTitleFolder ");
            
            ddoSectionTitleFolder.add();
            
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId("ATTR", "CC_FirstName"), fName);
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId("ATTR", "CC_LastName"), lName);
            ddoSectionTitleFolder.setData(ddoSectionTitleFolder.dataId("ATTR", "OSR_SectionTitle"), sectionTitle);
            
            System.out.println("ADD ATTRIBUTES ddoSectionTitleFolder");
            

            DKFolder dkSubFolder = (DKFolder)ddoSectionTitleFolder.getData(ddoSectionTitleFolder.dataId("ATTR", "DKFolder"));
            


            System.out.println("loopThru OSR Items checkout ddoSectionTitleFolder>>>>>");
            
            dsICM.checkOut(ddoSectionTitleFolder);
            
            System.out.println("------------- Update items ACL inside SectionTitle -----------");
            

            itemID = rs.getString("itemid");
            ps.setString(1, "2");
            ps.setString(2, itemID);
            int updateRowcount = ps.executeUpdate();
            
            System.out.println("itemID =" + itemID);
            




            StringBuffer query = new StringBuffer();
            query.append("//OSR");
            query.append("[@ITEMID=\"");
            query.append(itemID);
            
            query.append("\" AND @VERSIONID=latest-version(.)");
            query.append("]");
            

            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            DKNVPair[] options = new DKNVPair[3];
            options[0] = new DKNVPair("MAX_RESULTS", "1");
            options[1] = new DKNVPair("RETRIEVE_OPTION", dkRetrieveOptions);
            options[2] = new DKNVPair("End", null);
            

            System.out.println("Evaluating Query:  " + query.toString());
            DKResults results = (DKResults)dsICM.evaluate(query.toString(), (short)7, options);
            

            dkIterator iter = results.createIterator();
            
            DKDDO itemDDO = (DKDDO)iter.next();
            

            System.out.println("loopThru OSR Items checking in ddoSectionTitleFolder<<<<<<<");
            dkSubFolder.addElement(itemDDO);
            ddoSectionTitleFolder.update();
            dsICM.checkIn(ddoSectionTitleFolder);
            
            System.out.println("loopThru OSR Items checking in ddoNameFolder<<<<<<<<<<");
            dkNameFolder.addElement(ddoSectionTitleFolder);
            ddoNameFolder.update();
            dsICM.checkIn(ddoNameFolder);
          }
        }
        
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