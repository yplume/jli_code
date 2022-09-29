/*
 * Created on Jan 06, 2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class CANRoyaltiesExport {

	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private ResultSet rs1 = null;
	private String url1 = null;
	private int numPerFolder = 500;
	private String aFile = null;
	private String errFile = "c:\\CANRoyalties\\errLogFile.txt";
	private int numFolder = 1;
	private String _folder = null;
	private int _numPerFolder = 500;
	private String itemIDStart = "";
	private String itemIDEnd = "";
	
	private String  OUTPUT_FILE_PATH   = "";
	
	public CANRoyaltiesExport (String arg[]) {
		
		itemIDStart = arg[0];
		itemIDEnd 	= arg[1];
		
		exportCM(itemIDStart, itemIDEnd);
	
	}
	
	public void exportCM (String itemIDStart, String itemIDEnd) {
		String contractNumite = null;
	    String itemid = "";
	    String imageName = "";
	    String folder = "";
	    String createts = "";
	    String mimeTypeid = "";
	    String fileSize = "";
	    String contractType = "";
	    String division = "";
	    String rasNo = "";
	    String contractDate = "";
	    String pageCount = "";
	    String scanBatchName = "";
	    String dateScanned = "";
	   // String title = "";
	   // String isbn = "";
	   // String author = "";
	    StringBuffer meteDateSB = new StringBuffer();
	    StringBuffer errLogFileSB = new StringBuffer();
	    BufferedWriter outputlog = null;
	    int failIndex = 0;
	    
	    try {
	    	//BufferedReader in = new BufferedReader(new FileReader(OUTPUT_FILE_PATH + "test1.txt"));
			//System.out.println("000"+in.readLine());
			//while ((contractNum = in.readLine()) != null) {
			//    System.out.println("contract#: "+contractNum);
    		System.out.println("Load driver...");
        	//Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
        	Class.forName("com.ibm.db2.jcc.DB2Driver");
        	url1 = "jdbc:db2://icdwplebesdb01:50000/icmnlsdb";
        	System.out.println("=================================================");
    	    System.out.println("Connecting To DB2 To DB Lookup (CM LS):");
    	    System.out.println("-------------------------------------------------");
    	    System.out.println(" Database URL:   " + url1);
    	    System.out.println("=================================================\n");
        	
    	    //check if exportlog exists
    	    File logFile = new File (aFile);
    	    File errLogFile = new File (errFile);
            
    	    aFile = folder + "\\exportLog.log";
    	    boolean logExists = logFile.exists();
            System.out.println("logExists ==>"+logExists);
            
            if (logExists) {
            	// means need find array index and restart the application from failed point.
            	failIndex = findFailPoint(aFile);
            	System.out.println("failIndex ==>"+failIndex);
            } 
            
    	    conn = DriverManager.getConnection(url1,"icmadmin","Bigblue1");
			
    		stmt = conn.createStatement();
		   
    		/*String 	SQL = "select a.itemid,a.createts,mimetypeid as \"MIME TYPE\", b.resourcelength AS \"FILE SIZE\",a.attr0000001063 AS \"CONTRACT TYPE\",a.attr0000001064 AS \"DIVISION\",";
			SQL+= " a.attr0000001065 AS \"RAS NO\",a.attr0000001069 AS \"CONTRACT DATE\", a.attr0000001046 AS \"PAGE COUNT\", a.attr0000001036 AS \"SCAN BATCH NAME\", a.attr0000001060 AS \"DATE SCANNED\",";
			SQL+= " d.attr0000001066 AS \"TITLE\",e.attr0000001067 AS \"ISBN\",f.attr0000001068 AS \"AUTHOR\"";
			SQL+= " icmadmin.icmut00300001 b inner join icmadmin.icmstri001001 c on b.itemid =c.targetitemid";
			SQL+= "	inner join icmadmin.ICMUT01032001 a on c.sourceitemid =a.itemid where attr0000001064<>'PENG'"; 
			
*/			//query all line items\rows of Royalities item type
    		String 	SQL = "select a.itemid, a.createts,mimetypeid as \"MIME TYPE\", b.resourcelength AS \"FILE SIZE\",a.attr0000001063 AS \"CONTRACT TYPE\",a.attr0000001064 AS \"DIVISION\",";
			SQL+= " a.attr0000001065 AS \"RAS NO\",a.attr0000001069 AS \"CONTRACT DATE\", a.attr0000001046 AS \"PAGE COUNT\", a.attr0000001036 AS \"SCAN BATCH NAME\", a.attr0000001060 AS \"DATE SCANNED\"";
			SQL+= " from ";
			SQL+= " icmadmin.icmut00300001 b inner join icmadmin.icmstri001001 c on b.itemid =c.targetitemid";
			SQL+= "	inner join icmadmin.ICMUT01032001 a on c.sourceitemid =a.itemid where attr0000001064<>'PENG' and a.itemid>=\""+ itemIDStart +"\" and a.itemid>=\""+ itemIDEnd +"\""; 
			
			System.out.println(" SQL:   " + SQL);
			
        	rs = stmt.executeQuery(SQL);
        	
        	int counter = 0;
        	
        	String 	SQL1 = "select d.attr0000001066 AS \"TITLE\",e.attr0000001067 AS \"ISBN\",f.attr0000001068 AS \"AUTHOR\"";
					SQL1+= " from icmadmin.ICMUT01032001 a inner join icmadmin.ICMUT01033001 b";
					SQL1+= " on a.itemid=b.itemid inner join icmadmin.ICMUT01034001 c";
					SQL1+= " on a.itemid=c.itemid inner join icmadmin.ICMUT01035001 d"; 
					SQL1+= " on a.itemid=d.itemid ";
					SQL1+= " where a.itemid= ?";
	
        	PreparedStatement childrenComponents = null;	
        	childrenComponents = conn.prepareStatement(SQL1);
        	int itemidCounter = 0;
        	
        	//create initial folder
        	createOutPutPath();
        	System.out.println("OUTPUT_FILE_PATH in exportCM ==>"+OUTPUT_FILE_PATH);
        	BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH + "output.csv"));
            
    	    
			while (rs.next()) {
				
	    		imageName = null;
	    		itemid 	= null;
	    		itemid = rs.getString("ITEMID");
	    		
	    		meteDateSB = new StringBuffer(itemid);
	    		meteDateSB.append(","+createts);
	    		meteDateSB.append(","+mimeTypeid);
	    		meteDateSB.append(","+fileSize);
	    		meteDateSB.append(","+contractType);
	    		meteDateSB.append(","+division);
	    		meteDateSB.append(","+rasNo);
	    		meteDateSB.append(","+contractDate);
	    		meteDateSB.append(","+pageCount);
	    		meteDateSB.append(","+scanBatchName);
	    		meteDateSB.append(","+dateScanned);
	    		//meteDateSB.append(","+title);
	    		//meteDateSB.append(","+isbn);
	    		//meteDateSB.append(","+author);
	    		//meteDateSB.append("/n");
	    		
	    		System.out.println("set value to prepared statement");
	    		
	    		childrenComponents.setString(1, itemid);
	    		
	    		rs1 = childrenComponents.executeQuery();
	    		
	    		System.out.println("before call get image");
	    		
	    		//only update items that has image
	    		if (getImage (itemid, conn, stmt, meteDateSB, rs1, bw)) {
		    		//counter++;
		    		itemidCounter++;
		        	System.out.println(itemid+"<--contract counter->"+counter);
		        	
	    		}else{
	    			//log 
	    			System.out.println(itemid+"<--contract counter->"+counter);
	    			errLogFileSB.append(itemid+"\n");
	    		}
	    		if (itemidCounter%_numPerFolder==0) {
	        		System.out.println("call create new folder");
	        		//folderCounter++;
	        		createOutPutPath();
	        	}
	    		
	        	outputlog = new BufferedWriter(new FileWriter(aFile));
	        	
	        	
	    	 } // end while loop
			bw.flush();
			bw.close();
			
	    	rs.close();
	    	rs = null;
			
		} catch (Exception ex) {
			
		}
	}
	
	public void createOutPutPath() {
    	
    	String numberText = intToString (numFolder,4);
        
        
        //boolean success1 = (new File(_folder)).mkdir();
        //System.out.println("Folder created success ->"+success1);
        
        OUTPUT_FILE_PATH = "c:\\CANRoyalties\\" + numberText + "\\";
        //System.out.println("OUTPUT_FILE_PATH->"+OUTPUT_FILE_PATH);
        //OUTPUT_FILE_PATH = "\\\\cm4\\c$\\"+folder+"\\";
        //OUTPUT_FILE_PATH = "\\\\WNPCDDBSPP01\\Kofax Dual Release\\TesT\\"+folder+"\\";
        boolean success = (new File(OUTPUT_FILE_PATH)).mkdir();
        //boolean success = (new File("\\\\newmail\\files\\support\\test\\1\\")).mkdir();
        numFolder++;
        System.out.println("Folder created success ==>"+success);
    	//return path;
    }
	
	public  String intToString(int num, int digits) { 
        String output = Integer.toString(num); 
        while (output.length() < digits) output = "0" + output; 
        return output; 
    }     
	
	public int findFailPoint(String logFile) {
    	
    	int indexNumber = 0;
    	
    	try {
    	    BufferedReader in = new BufferedReader(new FileReader(logFile));
    	    
    	    String [] failPoint = new String[2];
    	    
    	    String str = null;
    	    
    	   
    	    
    	    while ((str = in.readLine()) != null) {
    	    	failPoint = str.split("\t");
    	    	indexNumber = Integer.parseInt(failPoint[0]);
    	    	System.out.println("failPoint ->"+failPoint[0]);
    	    }
    	    in.close();
    	   
    	    
    	} catch (IOException e) {
    		
    		System.out.println("Error-->"+e.getMessage());
    	}
    	 return indexNumber;
    }
    
	/*public void makeFolder (int counter) {
		int packageNum = 1;
		if(counter>2) {
    		packageNum++;
    		File newPackages = new File("C:\\cmexport\\package"+packageNum);
    		newPackages.mkdir();
    	}
	}*/
	
	public boolean getImage (String itemID, Connection conn, Statement stat, StringBuffer meteDateSB, ResultSet rs1, BufferedWriter bw) {
		
		String title = "";
	    String isbn = "";
	    String author = "";
		boolean isImage = true;
		/*"select mimetypeid, a.itemid,a.createts,b.resourcelength,a.attr0000001063,a.attr0000001064,a.attr0000001065,a.attr0000001069, a.attr0000001046, a.attr0000001036, a.attr0000001060,";
		"d.attr0000001066,e.attr0000001067,f.attr0000001068";
		"from icmadmin.icmut00300001 b inner join icmadmin.icmstri001001 c on b.itemid =c.targetitemid";
		"inner join icmadmin.ICMUT01032001 a on c.sourceitemid =a.itemid inner join icmadmin.ICMUT01033001 d";
		"on a.itemid=d.itemid inner join icmadmin.ICMUT01034001 e ";
		"on a.itemid=e.itemid inner join icmadmin.ICMUT01035001 f ";
		"on a.itemid=f.itemid";
		"where attr0000001064<>'PENG' ";*/
		
		
		
		/*String 	SQL = "select d.attr0000001066 AS \"TITLE\",e.attr0000001067 AS \"ISBN\",f.attr0000001068 AS \"AUTHOR\"";
				SQL+= " from icmadmin.ICMUT01032001 a inner join icmadmin.ICMUT01033001 b";
				SQL+= " on a.itemid=b.itemid inner join icmadmin.ICMUT01034001 c";
				SQL+= " on a.itemid=c.itemid inner join icmadmin.ICMUT01035001 d"; 
				SQL+= " on a.itemid=d.itemid ";
				SQL+= " where a.itemid='"+ itemID +"'";*/
		
		
		DKDDO ddo = null;
		DKAttrDefICM aDef;
		DKDatastoreExtICM dsExtICM = null;
        DKResults results = null;
    	//HashMap<Integer, ArrayList<String>> lineAtt = new HashMap<Integer, ArrayList<String>>();
    	String queryString = "/CANRoyalties[@VERSIONID = latest-version(.) AND (@ITEMID=\"" + itemID + "\")]";		
	    DKNVPair parms[] = new DKNVPair[2];
	    parms[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
	    // Specify any Retrieval Options desired.  Default is ATTRONLY.
	    parms[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
	    // Evaluate the query, seting the results into (results)
		DKDatastoreICM dsICM = null;
		
		try {
			
			dsICM = new DKDatastoreICM();
			dsICM = ICMConnectionPool.getConnection("icmadmin", "Bigblue1", "icmnlsdb");
			dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT);
			System.out.println("Get Connection from Pool!");
	    	DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            dkRetrieveOptions.resourceContent(true);
		    results = (DKResults)dsICM.evaluate(queryString, DKConstant.DK_CM_XQPE_QL_TYPE, parms);
		   
		    int read = 0;
        	byte[] bytes = new byte[1024];
			if (results==null){
		    	System.out.println("readImage: Error!!");
		    }
			System.out.println("results->"+results.cardinality());
		    // Set up variables for progress monitoring screen
			if (results.cardinality()==0){
				System.out.println("readImage: Could not find results = " + results.cardinality());
				return  false;
		    }else{
				dkIterator iter = results.createIterator();
				
				while (iter.more())	{
					ddo = (DKDDO)iter.next();
					
					if(dsExtICM.isCheckedOut(ddo))
						dsICM.checkIn(ddo);
					
					ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
					
					short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
					
					if (dataid > 0) {				
						
						DKParts dkParts = (DKParts)ddo.getData(dataid);
						
						System.out.println("dkParts="+dkParts.cardinality());
						
						if (dkParts.cardinality() == 0)	{
							return false;
						}
						System.out.println("Document has '"+dkParts.cardinality()+"' parts."); 
						
						dkIterator iter1 = dkParts.createIterator();
						
						int partCounter = 1;
						
						while (iter1.more()) {
							
							DKLobICM part = (DKLobICM) iter1.next();
							
                            part.retrieve(dkRetrieveOptions.dkNVPair());
                            
							System.out.println(" PID ID:  "+(part.getPidObject()).pidString()+"--"+intToString (partCounter,4));
                            
							if(part.getContent()!=null){
								
				            	System.out.println("Inside part......");
				            	
				            	OutputStream out = new FileOutputStream(new File(OUTPUT_FILE_PATH + "\\"+ itemID + "_" + partCounter + ".tif"));
	                            
				            	out.write(part.getContent());
				            	
				            	meteDateSB.append(", , , , , , , , , , ,");
								meteDateSB.append("," + OUTPUT_FILE_PATH + "\\"+ itemID + "_" + partCounter + ".tif");
				            	meteDateSB.append("\n");
				            	
				            	System.out.println("write part......");
				            	// create target ddo att
				            	//String mimeType = part.getMimeType();
				            	//part1.setMimeType("image/tiff");
				            	//System.out.println("Inside mimeType......"+mimeType);
				            	//part1.setMimeType(mimeType);
				            	//part1.setContent(part.getContent());
				            	out.flush();
	                        	out.close();
	                        	out = null;
	                        	//System.out.println("Writr image to file path : " + OUTPUT_FILE_PATH  + itemType +"_"+ itemID +"_"+intToString (partCounter,4)+".tif");
	                        	System.out.println("Writr image to file path : " + OUTPUT_FILE_PATH + "\\"+ itemID + "_" + partCounter + ".tif");
	                        	partCounter++;
							}else
								return false;
							
							partCounter++;
							/*OutputStream out = new FileOutputStream(new File(OUTPUT_FILE_PATH  + itemType +"_"+ itemID +"_"+intToString (partCounter,4)+".tif"));
                            InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(),-1,-1); 
                            System.out.println("get image ...");
                           try {
                            while ((read = inputStream.read(bytes)) != -1) {
                        		out.write(bytes, 0, read);
                        	}
                           }catch(Exception e){
                           	System.out.println("Exception in read:"+e.getMessage());
                           }
                            part = null;
                        	inputStream.close();
                        	inputStream = null;*/
                        	
						}
						iter1 = null;
						
					} else {
		                System.out.println("readImage: Could not find item.");
		            }
				// handle memory leak
				results = null;
				queryString = null;
				parms = null;
				}
				//handle memory leak
				iter = null;
				ddo = null;
				
				while (rs1.next()) {               
					title	= rs.getString(1);        
					isbn 	= rs.getString(2); 
					author 	= rs.getString(3); 
					meteDateSB.append(", , , , , , , , , , , ,");
					meteDateSB.append(","+title);
					meteDateSB.append(","+isbn);
					meteDateSB.append(","+author);
					meteDateSB.append("\n");
					
					System.out.println("before write metaData ="+meteDateSB);
                    bw.write(meteDateSB.toString());  
                    
				}	
			}
			//ICMConnectionPool.returnConnection(dsICM);
			
	    } catch (DKException dke)	{
	    	dke.printStackTrace();
	    	//setErrorOccurred(true);
			//setErrorMessage("Exception: "+ dke.getMessage());
			
			
	    } catch (InstantiationException ie)	{
	    	ie.printStackTrace();
	    	//setErrorOccurred(true);
			//setErrorMessage("Exception: "+ ie.getMessage());
			
	    } catch (Exception ex)	{
	    	ex.printStackTrace();
			System.out.println("exception------>"+ex.getLocalizedMessage());
			//setErrorOccurred(true);
			//setErrorMessage("Exception: "+ ex.getMessage());
			
		} finally {
			if (dsICM != null) {
				
				try {
                    ICMConnectionPool.returnConnection(dsICM);
                    ICMConnectionPool.destroyConnections();
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("Error returning connection to the pool." + e.getMessage());
                    //setErrorOccurred(true);
        			//setErrorMessage("Exception: "+e.getMessage());
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
	
	public void exportImage(DKRetrieveOptionsICM dkRetrieveOptions, short dataid, DKDDO ddo, String itemID) {
		
		try {
			
			if (dataid > 0) {
                DKParts dkParts = (DKParts) ddo.getData(dataid);
                if (dkParts.cardinality() == 0 || dkParts.cardinality() > 1) {
                    // log an error
                }
                dkIterator iter3 = dkParts.createIterator();
                System.out.println("parts: "+dkParts.cardinality());
                int partCounter = 0;
                
                int read = 0;
            	byte[] bytes = new byte[1024];
                while (iter3.more()) {
                    DKLobICM part = (DKLobICM) iter3.next();
                    //part.retrieve(OUTPUT_FILE_PATH  + contractNum+".tiff",dkRetrieveOptions.dkNVPair());
                    part.retrieve(dkRetrieveOptions.dkNVPair());
                    
                    OutputStream out = new FileOutputStream(new File(OUTPUT_FILE_PATH  + itemID +".tiff"));
                    InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(),-1,-1); 
                    System.out.println("get image ...");
                    while ((read = inputStream.read(bytes)) != -1) {
                		out.write(bytes, 0, read);
                	}
                 
                	inputStream.close();
                	out.flush();
                	out.close();
                    
                    System.out.println("image file path : " + OUTPUT_FILE_PATH  + itemID+".tiff");
                    
                }
            } else {
                System.out.println("readImage: Could not find item.");
            }
		} catch (DKException dke) {
	        dke.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } catch (InstantiationException ie) {
	        ie.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } 
    }
	public void getChild(String[][] childrenValues, int countChildren, DKChildCollection children, String subattName, ArrayList<String> parentAttributes, HashMap<Integer, ArrayList<String>> lineAtt) {
		int count = 1;
		String childValue = null;
		try {
			dkIterator iter1 = children.createIterator(); // Create an iterator to go through Child Collection
	        System.out.println("children.cardinality() ="+children.cardinality() +"++++");
	        while(iter1.more()){ // list all sub-attributes for each entity
	            DKDDO child = (DKDDO) iter1.next(); // Move pointer to next child & return that object.
	       	 //	System.out.println("   child =    "+ child.getDataByName("SSName") );
	            ArrayList tempAL = new ArrayList();
	            System.out.println(" parentAttributesALLL="+parentAttributes);
	            tempAL.add(parentAttributes);
	            child.retrieve(DKConstant.DK_CM_CONTENT_ATTRONLY);
	            if (child.getDataByName(subattName)!=null) {
	            	System.out.println("   childdddddddddddddddddddd =    "+ child.getDataByName(subattName));
	            	System.out.println("   lineAtt.size() =    "+ lineAtt.size());
	            	childValue = child.getDataByName(subattName).toString();
	            	tempAL.add(childValue);
	            	//childrenValues[countChildren][count] = childValue;
	            	
	            	System.out.println("count="+count);
	            	System.out.println("countChildren="+countChildren);
	            	
            		if (countChildren>1){
	            		if (lineAtt.size()<count){
	            			lineAtt.put(count, tempAL);
	            			System.out.println("add lineAtt1");
	            		}else{	
	            			lineAtt.get(count).add(childValue);
	            			System.out.println("lineAtt.get");
		            		
	            		}
	            	}else{
	            		lineAtt.put(count, tempAL);
	            		System.out.println("add lineAtt2");
	            		
	            	}
	            	System.out.println("add childValue to childrenValues="+childrenValues);
	            }
	            
	            //attributes[count][childValue]
	            //lineAtt.put(count, tempAL);
	            count++;
	            //parentAttributes = null;
	            tempAL = null;
	            //System.out.println("HashMap1="+lineAtt.get(1));
	            System.out.println("childrenValues="+childrenValues);
		    }
		} catch (DKException dke) {
	        dke.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } catch (InstantiationException ie) {
	        ie.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } 
    }
	
	public void getSubEntities(DKItemTypeDefICM entityDef, DKDDO ddo, ArrayList<String> parentAttributes, HashMap<Integer, ArrayList<String>> lineAtt) {
		String subEntity ="";
    	String subattName ="";
    	int hasC;

		try {
			DKSequentialCollection compTypes = (DKSequentialCollection) entityDef.listSubEntities();
			dkIterator iter = compTypes.createIterator();
			
			//compTypes.cardinality();
			//create a string[][] array to contain all children values
			String childrenValues [][] = new String[5][200];
			int countChildren = 1;//count for every sub att
			while (iter.more()) { // list all sub-entities
				DKComponentTypeDefICM compType = (DKComponentTypeDefICM) iter.next();
				subEntity = compType.getName();
				System.out.println("   subEntity =    "+ compType.getName() +": "+compType.getDescription() );
				
				DKSequentialCollection subattrColl = (DKSequentialCollection) compType.listAllAttributes();
				dkIterator iter1 = subattrColl.createIterator();
				
				while (iter1.more()) { // look thru each sub attribute
					DKAttrDefICM attr4 = (DKAttrDefICM) iter1.next(); // Move pointer to next element & return that object.
					subattName = attr4.getName();
					
					System.out.println("subattName="+subattName);
					//get value for each sub attribute
					short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_CHILD,subEntity);
                    DKChildCollection children = (DKChildCollection) ddo.getData(dataid);
                    
                    getChild(childrenValues, countChildren, children, subattName, parentAttributes, lineAtt);
                    
                    countChildren++;
				
				}
				for(int i=1; i<lineAtt.size(); i++) {
					ArrayList test = lineAtt.get(i);
					System.out.println("test="+test);
					
				}
        	}
			System.out.println("HashMap size="+lineAtt.size());
			
				
		} catch (DKException dke) {
	        dke.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } catch (InstantiationException ie) {
	        ie.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        try {
	        } catch (Exception e) {
	            System.out.println(" Error Alert Email Exception =" + e.getMessage());
	        }
	    } 
    }
	public static void main (String arg []) {
			
		DKDatastoreICM dsICM = null;    
		DKResults results;
		DKDDO itemDDo = null;
		String querystring = null;
		String itemType = null;
		String contractNum = null;
		
	    try {
	    	CANRoyaltiesExport export = new CANRoyaltiesExport(arg);
	    	
	 		
	    } //end try
	    //-----------------------------------------------------
	    
	     catch (Exception exc) {
	     
	      exc.printStackTrace();
		} finally {
		  	
			if (dsICM != null) {
				try {
					
					dsICM = null;
				} catch (Exception e) { 
					System.out.println("[ICMGetSearchResultOnCM]Error returning connection to the pool." + e.getMessage());
				}
			}
		}	
	}
	
}
