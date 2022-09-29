
/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.ibm.mm.sdk.common.DKConstant;
import com.ibm.mm.sdk.common.DKConstantICM;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKNVPair;
import com.ibm.mm.sdk.common.dkResultSetCursor;
import com.ibm.mm.sdk.server.DKDatastoreICM;


public class Copy_5_of_ExportItems {

	//private EmailSender m_ems = null;
	private XMLReader m_pmxr = null;
	private ArrayList itemTypes = null;;
	private int count = 0;
	private int waitingTime = 0;
	private Connection connection = null;
	private DKDatastoreICM dsICM = null;
	private String attribute = null;
	private String m_BaseURL = "";
	private String lastMovedTime = null;
	final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

	
	public void GenerateOutputFile(FileWriter fw, String createts, String barCode, String scanDate, String itemType) {
		
		try {
			if(createts!=null){
				fw.write(createts);
				fw.write(",");
			}else{
				fw.write(",");
			
			}
			if(barCode!=null){
				fw.write(barCode);
				fw.write(",");
			}else{
				fw.write(",");
			
			}
			if(scanDate!=null){
				fw.write(scanDate);
				fw.write(",");
			}else{
				fw.write(",");
			
			}
			if(itemType!=null){
				fw.write(itemType);
				fw.write("\r\n");
			}else{
				fw.write(",");
				fw.write("\r\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public void Export(){
	    System.out.println("<ExportItems>Start Exporting ....");
	    //String [] itemTypes = {"APU","APC"};
	    //TODO generate report
	    m_pmxr = new XMLReader();
    	m_pmxr.readXMLConfig();
    	System.out.println("<ExportItems>after readXMLConfig");
		
    	String cmServer 		= m_pmxr.getCmServerName();
    	String cmUser 			= m_pmxr.getCmUserName();
		String cmPass 			= m_pmxr.getCmPassword();
		String UNC 				= m_pmxr.getUNC();
		String conf_APU 		= m_pmxr.getAPUTableName();
		String conf_APC 		= m_pmxr.getAPCTableName();
		String conf_JE 			= m_pmxr.getJETableName();
		String conf_JEC 		= m_pmxr.getJECTableName();
		String conf_BarCode		= m_pmxr.getBarCode();
		String conf_ScanDate	= m_pmxr.getScanDate();
		String conf_EmailDate 	= m_pmxr.getEmailDate();
		String conf_Deleted 	= m_pmxr.getDeleted();
		
		System.out.println("conf_APU = " + conf_APU);
		
	    String url1 = "jdbc:db2://"+cmServer+":50000/icmnlsdb";
		
	    Connection conn = null;
		
		System.out.println("<ExportItems>After Get DB2 conntion!" + url1);
		
		Statement stmt = null;

		ResultSet rs = null; 

		String sql = "";
		
	    try{
	    	System.out.println("<ExportItems>InitRun ="+m_pmxr.getInitRun());
			System.out.println("<ExportItems>UNC ="+UNC);
			System.out.println("<ExportItems>DateRange() ="+ m_pmxr.getDateRange());
			
			String initRun			= m_pmxr.getInitRun();
			itemTypes				= (ArrayList)m_pmxr.getItemTypes();
			System.out.println("<ExportItems>Item Types = "+itemTypes);
			
			File currFile			= new File(UNC+"IMG.txt");
			
			String appendFileName	= null;
			Date now 				= new Date();
			int x = m_pmxr.getDateRange();
			Calendar cal = GregorianCalendar.getInstance();
			cal.add( Calendar.DAY_OF_YEAR, -x);
			Date xDaysAgoDate = cal.getTime();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
			
			String nowDate = df.format(now);
			String xDaysAgo= df.format(xDaysAgoDate);
						
			/////////////////////using ScanDate////////////////////////
			
			String dateNow = df.format( new Date());
			
			//////////////////////using ScanDate/////////////////////////
			//check a output file exists
			if(currFile.exists()) {
				//DateFormat df1 = new SimpleDateFormat("yy-MM-dd");  
				appendFileName = (nowDate.replaceAll("\\-", "")).substring(2);
				System.out.println("<ExportItems>appendFileName="+appendFileName);
				//rename current file to IMGYYMMDD
				boolean success = currFile.renameTo(new File(UNC+"IMG"+appendFileName+".txt"));
				if(success)
					System.out.println("<ExportItems>Rename a file!");
			}
			
			FileWriter fw = new FileWriter(currFile);
			System.out.println(xDaysAgo);
				
			
			String  queryString = "";
			String  createts = "";
			String  barCode  = "";
			String  scanDateStr = "";
			String  emailDateStr = "";
			Timestamp createtsObj = null;
			Date createsDate = null;
			Date scanDate = null;
			Date emailDate = null;
			
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			conn = DriverManager.getConnection(url1,cmUser,cmPass);
			stmt = conn.createStatement();
			
			for (int i=0; i<itemTypes.size(); i++) {
				
				String tableName = "";
				/*prod code
				 * if (itemTypes.get(i).equals("APU"))
						tableName = "icmut01029001";
				if (itemTypes.get(i).equals("APC"))
						tableName = "icmut01021001";
				if (itemTypes.get(i).equals("JE"))
						tableName = "icmut01009001";
				if (itemTypes.get(i).equals("JEC"))
						tableName = "icmut01031001";*/
				
				if (itemTypes.get(i).equals("APU"))
					tableName = conf_APU;
				if (itemTypes.get(i).equals("APC"))
					tableName = conf_APC;
				if (itemTypes.get(i).equals("JE"))
					tableName = conf_JE;
				if (itemTypes.get(i).equals("JEC"))
					tableName = conf_JEC;
				System.out.println("tableName == "+tableName);
				
            	if (initRun.equalsIgnoreCase("1")){
            		
            		System.out.println("<ExportItems>initRun = "+initRun);
            		if(itemTypes.get(i).equals("APU")){
            			//prod code
            			sql =  "select CREATETS, "+conf_BarCode+", "+conf_ScanDate+", "+conf_EmailDate+" from " + tableName + " where (("+conf_EmailDate+">'" + xDaysAgo +"' and "+conf_EmailDate+"<'" + nowDate +"') or ("+conf_ScanDate+">'" + xDaysAgo +"' and "+conf_ScanDate+"<'" + nowDate + "')) and "+conf_Deleted+" is null";
        			   	//queryString = "/" + (String)itemTypes.get(i) + "[@VERSIONID = latest-version(.) AND (@CREATETS>\"" + xDaysAgo + "\") AND (@CREATETS<=\"" + nowDate + "\")]";	
            	//queryString = "/" + (String)itemTypes.get(i) + "[@VERSIONID = latest-version(.) AND (@CREATETS>\"" + xDaysAgo + "\") AND (@CREATETS<=\"2014-05-22 13:57:56.509\")]";	
            		}else
            			sql =  "select CREATETS, "+conf_BarCode+", "+conf_ScanDate+" from " + tableName + " where ("+conf_ScanDate+">'" + xDaysAgo +"' and "+conf_ScanDate+"<'" + nowDate + "')";
    			   	
            	}else
            		sql =  "select CREATETS, "+conf_BarCode+", "+conf_ScanDate+" from " + tableName;
            	
            	//try {
					//System.out.println("listFilePath in itemsLookup = "+list+"======"+listFilePath);
					
					//output = new BufferedWriter(new FileWriter(aFile));
					
					rs	= stmt.executeQuery(sql);
			    	System.out.println("SQL == "+sql);
					
			    	while(rs.next()){
			    		//prod code
			    		createsDate = rs.getDate("CREATETS");
			    		createts = createsDate.toString();
			    		System.out.println("createsDate == "+createsDate);
			    		//System.out.println("creates == "+createsDate.toString());
			    		
				    	barCode = rs.getString(conf_BarCode);
				    	System.out.println("barCode == "+barCode);
				    	
				    	if (barCode != null && ((barCode.trim().length() == 8) && (barCode.matches("[0-9]+")))) {
				    		scanDate = rs.getDate(conf_ScanDate);
				    		//scanDateStr = scanDate.toString();
				    		if (scanDate != null)
								scanDateStr = scanDate.toString();
							else{
								emailDate = rs.getDate(conf_EmailDate);
								scanDateStr = emailDate.toString();
								//scanDateStr = emailDate.toString();
							}
							System.out.println("<ExportItems>scanDateStr = "+scanDateStr);
							GenerateOutputFile(fw, createts, barCode, scanDateStr, (String)itemTypes.get(i));
				    	}
			    	}
			    	
			}
			fw.flush();
			fw.close();
			System.out.println(" -----DONE----");
			}catch (Exception ex){
		    	System.out.println(" Error Exception = " + ex.getMessage());
		    
		    } finally {
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
		        	
		    	} catch (SQLException sqle) {
		            sqle.printStackTrace();
		       
		    	}
		    }
			
	}
	
    /**
     * @return Returns the m_BaseURL.
     */
    public String getBaseURL() {
        return m_BaseURL;
    }

    /**
     * @param baseURL The m_BaseURL to set.
     */
    public void setBaseURL(String baseURL) {
        m_BaseURL = baseURL;
    }
    
    /*public static void main(String[] args){
    	XMLReader m_pmxr = new XMLReader();
		m_pmxr.readXMLConfig();	
    	ExportItems m_eit = new ExportItems();
    	m_eit.Export();
		System.out.println("getUNC = "+m_pmxr.getUNC());
	}*/
}
