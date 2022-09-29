
/**
 * @author JLi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


import java.sql.*;
import java.io.*;
import java.text.*;
import java.util.*;

import com.ibm.mm.sdk.common.DKAttrDefICM;
import com.ibm.mm.sdk.common.DKConstant;
import com.ibm.mm.sdk.common.DKConstantICM;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKLobICM;
import com.ibm.mm.sdk.common.DKNVPair;
import com.ibm.mm.sdk.common.DKParts;
import com.ibm.mm.sdk.common.DKResults;
import com.ibm.mm.sdk.common.DKRetrieveOptionsICM;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreExtICM;
import com.ibm.mm.sdk.server.DKDatastoreICM;


public class UpdateHealth{
	
	
	String  _cmServer               = null;  // -cmserver
	String  _cmDBName               = null;  // -b
	String  _cmDBUserName           = null;  // -n
	String  _cmDBPassword           = null;  // -w
	String  _databaseName           = null;  // -m
    String  _userName               = null;  // -u
    String  _password               = null;  // -p
    String  _table                  = null;  // -t
    String  _itemType               = null;  // -i
    String  _itemIDStr              = null;  // -s
    String  _begin	                = null;  // -g
    String  _end	                = null;  // -e
    String  _view	                = null;  // -v
    String  _cmUs	                = null;  // -v
    String  _cmPw	                = null;  // -v
    Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	Statement stmt1 = null;
	ResultSet rs1 = null;
	String sql0 = "";
	String sql = "";
	String sql1 = "";
	String sql2 = "";
	String url = "";
	String url1 = "";
	String OUTPUT_FILE_PATH	= null;
	String listFilePath = null;
	String aFile = "C:\\WVO_ini\\output.log";
	String errorMessage = null;
	boolean errorOccurred = false;
	static String startID ="";
	static String endID ="";
	
	public static void main(String[] args) 
    {
    	System.out.println("Calling Main ...");
    	startID = args[0];
    	endID = args[1];
    	System.out.println("111111");
    	UpdateHealth md = new UpdateHealth(startID, endID);
    	System.out.println("222222222");	
    }
	public UpdateHealth (String sID, String eID) {
		
		loadIniFile(sID, eID);
	}
	
	
	
    public void loadIniFile(String sID,  String eID) {
        
        
        //=====================load ini file========================//
    	try {
    		 
	        /*Properties p = new Properties();
	        p.load(new FileInputStream("c:\\WVO_ini\\config.ini"));
	        
	        _cmServer 		= p.getProperty("CMSERVERNAME");
	        _cmDBName  		= p.getProperty("CMDBNAME");
	        _cmDBUserName 	= p.getProperty("CMDBUSER");
	        _cmDBPassword 	= p.getProperty("CMDBPASSWORD");
	        _databaseName 	= p.getProperty("DBNAME");
	        _userName 		= p.getProperty("DBUSER");
	        _password 		= p.getProperty("DBPASSWORD");
	        _table 			= p.getProperty("EXPORTTABLE");
	        _itemType 		= p.getProperty("CMITEMTYPE");
	        _begin 			= p.getProperty("BEGIN");
	        _end 			= p.getProperty("END");
	        _view 			= p.getProperty("INDEXCLASS");
	        _cmUs			= p.getProperty("CMUSER");
	        _cmPw 			= p.getProperty("CMPASSWORD");
	        */
	        
	        //=========================end===============================//
	        loadDriver ();
	        
	        updateStatus (sID, eID);
	        
    	}catch (Exception ex){
    		System.out.println("Error out-->"+ex.getMessage());
    		setErrorOccurred(true);
			setErrorMessage("Exception: "+ex.getMessage());
    	}
    }
    
    public void updateStatus (String sID, String eID) {
    	
    	Statement retrieve= null;
    	Statement update= null;
        
    	String sqlString = "";
    	


		
		//String sql1 = "select * from osr001 a where a.OSR_SectionTitle='HEALTH' and a.osr_dob is null and a.OSR_PrimaryID is not null and lastchangedts>'2019-10-05 13:30:30.1' and lastchangedts<'2019-10-05 20:30:30.1'";
    	String query  = "select OSR_PrimaryID from osr001 a where a.OSR_SectionTitle='HEALTH' and a.osr_dob is null and a.OSR_PrimaryID is not null";
    	//String query  = "select OSR_PrimaryID from osr001 a where a.OSR_SectionTitle='HEALTH' and a.osr_dob is null and (a.OSR_PrimaryID='8-04-990781-1' or a.OSR_PrimaryID='8-04-990780-1')";
    	//String query1 = "select osr_dob, CC_SEALEDENVELOPEONFILE from osr001 where OSR_PrimaryID=? AND osr_SectionTitle='APPOINTMENTS' ";
    	String query1 = "select osr_dob, CC_SEALEDENVELOPEONFILE from osr001 where OSR_PrimaryID=? AND osr_SectionTitle='GENERAL'";
		PreparedStatement ps = null;
		
		try {
			//conn = DriverManager.getConnection(url1,"icmadmin","Bigblue1");
			conn = DriverManager.getConnection(url1,"icmadmin","Icm8doc6");
			ps = conn.prepareStatement(query1);
			
			String pID = "";
			String dob = "";
			String sealFile = "";
			
			
			retrieve = conn.createStatement();
			update = conn.createStatement();
			rs = retrieve.executeQuery(query);
			while (rs.next()) {
				System.out.println("1111");
				pID = rs.getString("OSR_PrimaryID");
				System.out.println(" pID="+pID);
				ps.setString(1, pID);
				System.out.println(" 333333");
	    		//ps.setString(1, pidLike);
	    		rs1 = ps.executeQuery();
	    		System.out.println(" 44444");
	    		if(rs1!=null && rs1.next()) {
	    			System.out.println(" 555");
	    			dob = rs1.getString(1);
	    			System.out.println(" 666");
	    			sealFile = rs1.getString(2);
	    			System.out.println(" 7777");
	    			sqlString =  "UPDATE osr001 SET osr_dob = '" + dob + "', cc_SealedEnvelopeonfile='" + sealFile + "' WHERE OSR_PrimaryID='"+ pID +"' AND osr_SectionTitle='HEALTH'";
	    			update.executeUpdate(sqlString);
	    			System.out.println(" 8888");
	    			System.out.println("AFTER update sql - >"+sqlString);
	    		}
				System.out.println("update sql");
		    	
		    	
            }
			
			
			//output.close();
			
	    }catch (SQLException ex){
	    	System.out.println(" Error SException =" + ex.getMessage());
	    	//setErrorOccurred(true);
			//setErrorMessage("Exception: "+ex.getMessage());
	    
	    } finally {
	    	System.out.println("999999");
	    	try {
	    		if (rs != null) 
	                rs.close();
	            if (stmt!= null) {
	                stmt.close();
	            }
	        	if (conn != null)	{
	            	conn.close();
	            	conn = null;
	            }
	        	if (update != null) {
	            	update.close();
	            }
	        	
	    	} catch (SQLException sqle) {
	            sqle.printStackTrace();
	            setErrorOccurred(true);
				setErrorMessage("Exception: "+sqle.getMessage());
				
	        }catch (Exception ioe) {
	    		System.out.println("Finally IO exception--->"+ioe.getMessage());  
	    		setErrorOccurred(true);
				setErrorMessage("Exception: "+ioe.getMessage());
	    	}
	    }
    	
    	//return sqlString;
    }
    
    public boolean updateACL (String itemType, String contract, String acl) {
    	
    	DKDDO ddo = null;		
    	DKDatastoreExtICM dsExtICM = null;
		DKResults results =null;
		String queryString = "/"+ itemType +"[@VERSIONID = latest-version(.) AND (@Contract_Number=\"" + contract + "\")]";		
	    DKNVPair parms[] = new DKNVPair[2];
	    parms[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
	    // Specify any Retrieval Options desired.  Default is ATTRONLY.
	    parms[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
	    // Evaluate the query, seting the results into (results)
		DKDatastoreICM dsICM = null;
		try {
			
			dsICM = new DKDatastoreICM();
			dsICM = ICMConnectionPool.getConnection(_cmUs, _cmPw, _cmServer);
			dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT);
			System.out.println("Get Connection from Pool!!!"+itemType);
	    	DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            dkRetrieveOptions.baseAttributes(true);
            dkRetrieveOptions.basePropertyAclName(true);
            
		    results = (DKResults)dsICM.evaluate(queryString, DKConstant.DK_CM_XQPE_QL_TYPE, parms);
			if (results==null){
		    	System.out.println("readImage: Error!!");
		    }
			System.out.println("results & contract & itemType ->"+results.cardinality()+"--"+contract+"---"+itemType);
		    if (results.cardinality()==0){
				System.out.println("readImage: Could not find item for itemid# = " + contract);
				return false;
				
		    }else{
				dkIterator iter = results.createIterator();
				while (iter.more())	{ // retreive may be multiple contracts as same number
					ddo = (DKDDO)iter.next();
					if(dsExtICM.isCheckedOut(ddo))
						dsICM.checkIn(ddo);
					ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
					String aclName = "";
					for(short propid=1; propid<=ddo.propertyCount(); propid++) {
			            String name  = ddo.getPropertyName(propid);
			            Object value = ddo.getProperty(propid);
			            if (name.equalsIgnoreCase("SYSROOTATTRS.ACLCODE")) {
			            	aclName = (String)ddo.getProperty(propid);
			            	System.out.println("  aclName:  "+aclName);
				            dsICM.checkOut(ddo);
				            System.out.println("Updated ACL-->"+acl );
				            Object o = acl;
				            ddo.setProperty(propid, o);
				            ddo.update();
				            System.out.println("checkOut and update ACL" );
				            ddo.update(); // Update target document with part removed
						    dsICM.checkIn(ddo);
			            }
			        }
					
				results = null;
				queryString = null;
				parms = null;
				}
				iter = null;
				
			}
			
	    } catch (DKException dke)	{
	    	dke.printStackTrace();
	    	setErrorOccurred(true);
			setErrorMessage("Exception: "+ dke.getMessage());
			return false;
			
	    } catch (InstantiationException ie)	{
	    	ie.printStackTrace();
	    	setErrorOccurred(true);
			setErrorMessage("Exception: "+ ie.getMessage());
			return false;
			
	    } catch (Exception ex)	{
	    	ex.printStackTrace();
			System.out.println("exception------>"+ex.getLocalizedMessage());
			setErrorOccurred(true);
			setErrorMessage("Exception: "+ ex.getMessage());
			return false;
			
		} finally {
			if (dsICM != null) {
				try {
					ICMConnectionPool.returnConnection(dsICM);
					ICMConnectionPool.destroyConnections();
                    dsICM = null;
               } catch (Exception e) {
                   System.out.println("Error returning connection to the pool." + e.getMessage());
                   setErrorOccurred(true);
       			   setErrorMessage("Exception: "+e.getMessage());
       			   return false;
               }
			}
			if (results != null) {
				results = null;
			}
			if (parms != null) {
				parms = null;
			}
		}
		return true;
   }
    
	public void loadDriver () {
		
	    try {
        	
         	System.out.println("Load driver...");
        	//Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
        	Class.forName("com.ibm.db2.jcc.DB2Driver");
        	
        	//Conenct to DB2 in CM LS
        	url1 = "jdbc:db2://USETHQDMCM85:50000/icmsvadb";
        	//url1 = "jdbc:db2://win2012jl:50000/icmnlsdb";
        	
        	System.out.println("=================================================");
    	    System.out.println("Connecting To DB2 To DB Lookup (CM LS):");
    	    System.out.println("-------------------------------------------------");
    	    System.out.println(" Database URL:   " + url1);
    	    //System.out.println(" DB2 UserName:   " + _cmDBUserName);
    	    System.out.println("=================================================\n");
        	
        	
    	   // updateStatus ();
        	
	    }catch(Exception e){
	    	System.out.println(e);
	    	setErrorOccurred(true);
			setErrorMessage("Exception: "+e.getMessage());
			
	    } finally {
	    	try {
	    		
	        	if (conn != null)	{
	            	conn.close();
	            	conn = null;
	            }
	            
	    	} catch (SQLException sqle) {
	            sqle.printStackTrace();
	            setErrorOccurred(true);
				setErrorMessage("Exception: "+sqle.getMessage());
	        }
	    }
	
	}
    public String getErrorMessage() 
	{
		return errorMessage;
	}
	
	public void setErrorMessage(String string) 
	{
		errorMessage = string;
	}
	public boolean getErrorOccurred() 
	{
		return errorOccurred;
	}
	public void setErrorOccurred(boolean b) 
	{
		errorOccurred = b;
	}

}
