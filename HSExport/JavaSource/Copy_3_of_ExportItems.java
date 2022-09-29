
/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;
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


public class Copy_3_of_ExportItems {

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
	    try{
	    	m_pmxr = new XMLReader();
	    	m_pmxr.readXMLConfig();
	    	System.out.println("<ExportItems>after readXMLConfig");
			
	    	String cmServer 		= m_pmxr.getCmServerName();
	    	String cmUser 			= m_pmxr.getCmUserName();
			String cmPass 			= m_pmxr.getCmPassword();
			String UNC 				= m_pmxr.getUNC();
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
				
	    	DKDDO ddo = null;	
			/*DKNVPair parms[] = new DKNVPair[2];
			parms[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_IDONLY));
			//parms[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
		    // Specify any Retrieval Options desired.  Default is ATTRONLY.
		    parms[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
		    // Evaluate the query, seting the results into (results)
*/		    
	    	DKNVPair options[] = new DKNVPair[3];
		   //options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "200"); // No Maximum(Default)
		    options[0] = new	DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum(Default)
		   //options[1] = new	DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new	Integer(DKConstant.DK_CM_CONTENT_YES));
		    options[1] = new	DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new	Integer(DKConstant.DK_CM_CONTENT_IDONLY));
		   //options[1] = new	DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new	Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
		    options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,null);

		    DKDatastoreICM dsICM = new DKDatastoreICM();
			
	    	dsICM = ICMConnectionPool.getConnection(cmUser, cmPass, cmServer);
			System.out.println("<ExportItems>After Get CM conntion!");
			String  queryString = "";
			String  createts = "";
			String  barCode  = "";
			String  scanDateStr = "";
			String  emailDateStr = "";
			Timestamp createtsObj = null;
			Date scanDate = null;
			Date emailDate = null;
					
            for (int i=0; i<itemTypes.size(); i++) {
            	if (initRun.equalsIgnoreCase("1")){
            		System.out.println("<ExportItems>initRun = "+initRun);
            		if(itemTypes.get(i).equals("APU")){
            			queryString = "/" + (String)itemTypes.get(i) + "[@VERSIONID = latest-version(.) AND ((@ScanDate>\"" + xDaysAgo + "\") AND (@ScanDate<=\"" + dateNow + "\")) OR ((@EmailDate>\"" + xDaysAgo + "\") AND (@EmailDate<=\"" + dateNow + "\"))]";	
            		//queryString = "/" + (String)itemTypes.get(i) + "[@VERSIONID = latest-version(.) AND (@CREATETS>\"" + xDaysAgo + "\") AND (@CREATETS<=\"" + nowDate + "\")]";	
            	//queryString = "/" + (String)itemTypes.get(i) + "[@VERSIONID = latest-version(.) AND (@CREATETS>\"" + xDaysAgo + "\") AND (@CREATETS<=\"2014-05-22 13:57:56.509\")]";	
            		}else
            			queryString = "/" + (String)itemTypes.get(i) + "[@VERSIONID = latest-version(.) AND (@ScanDate>\"" + xDaysAgo + "\") AND (@ScanDate<=\"" + dateNow + "\")]";	
            		
            	}else
            		queryString = "/" + (String)itemTypes.get(i) + "[@VERSIONID = latest-version(.)]";		
            	
            	System.out.println("<ExportItems>queryString = "+queryString);
            	dkResultSetCursor cursor = dsICM.execute(queryString, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
				System.out.println("<ExportItems>cursor = "+cursor.cardinality());
				
			    
			    while((ddo = cursor.fetchNext())!=null) {
			    	//ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
			    	//ddo.retrieve(DKConstant.DK_CM_CONTENT_ATTRONLY);
			    	ddo.retrieve(DKConstant.DK_CM_CONTENT_IDONLY);
					createtsObj = (Timestamp)ddo.getPropertyByName("SYSROOTATTRS.CREATETS");
					createts = df.format(createtsObj);
					barCode  = (String) ddo.getData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"BarCode")); 
					
					System.out.println("<ExportItems>barCode = "+barCode);
					//check barcode if only contain 8 numeric character
					if (barCode != null && ((barCode.trim().length() == 8) && (barCode.matches("[0-9]+")))) {
						
						scanDate = (Date) ddo.getData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"ScanDate")); 
						//System.out.println("<ExportItems>scanDate ="+scanDate);
						if (scanDate != null)
							scanDateStr = scanDate.toString();
						else{
							emailDate = (Date) ddo.getData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"EmailDate")); 
							scanDateStr = emailDate.toString();
						}
						System.out.println("<ExportItems>scanDateStr ="+scanDateStr);
						GenerateOutputFile(fw, createts, barCode, scanDateStr, (String)itemTypes.get(i));
					}
					
					ddo = null;
					createts = "";
					barCode  = "";
					scanDate = null;
					scanDateStr = "";
					createtsObj = null;
			    }
			    cursor.close();
	        }
            fw.flush();
			fw.close();
			
                
	    } catch (DKException exc) {
            System.out.println("<ExportItems>run: Error in routing statement " + exc);
           // emailError(exc.getMessage(), "DKException - checkForUpdates");
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } catch (Exception exc) {
            System.out.println("<ExportItems>run: Error in Exception " + exc);
           // emailError(exc.getMessage(), "Exception - checkForUpdates");
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } finally {
        	if (dsICM != null) {
                try {
                    ICMConnectionPool.returnConnection(dsICM);
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("<ExportItems>Error returning connection to the pool." + e.getMessage());
                }
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
