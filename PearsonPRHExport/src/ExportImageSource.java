
/**
 * @author JLi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */




package wvo;
import java.sql.*;
import java.io.*;
import java.text.*;
import java.util.*;
import sun.misc.Signal;


import com.ibm.mm.sdk.common.DKConstant;
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



public class ExportImageSource {
	
	//private String  _listFile				= null;
	private String  _folder					= null;
	private String  _cmServer               = null;  // -cmserver
	private String  _cmDBName               = null;  // -b
	private String  _cmDBUserName           = null;  // -n
	private String  _cmDBPassword           = null;  // -w
	//String  _database               = null;  // -d
	private String  _databaseName           = null;  // -m
	private String  _userName               = null;  // -u
	private String  _password               = null;  // -p
	private String  _table                  = null;  // -t
	private String  _itemType               = null;  // -i
	private String  _itemIDStr              = null;  // -s
	private String  _begin	                = null;  // -g
	private String  _end	                = null;  // -e
	private String  _view	                = null;  // -v
	private String  _cmUs	                = null;  // -v
	private String  _cmPw	                = null;  // -v
	private int 	numFolder 					= 1;
	//String itemID	 		= null;
	private Connection conn = null;
	private Connection conn1 = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	private Statement stmt1 = null;
	private ResultSet rs1 = null;
	private String sql0 = null;
	private String sql = null;
	private String sql1 = null;
	private String sql2 = null;
	private String url = null;
	private String url1 = null;
	private String OUTPUT_FILE_PATH	= null;
	private String _listFilePath = null;
	private int _numPerFolder = 0;
	private String _subFolder = null;
	private int counter 		= 0;
	private String aFile = null;
	private String errorMessage = null;
	boolean errorOccurred = false;
	private int failIndex = 0;
	
	public ExportImageSource (String listFile, String folder, String subFolder, String num) {
		_listFilePath 		= listFile;
		_folder				= folder;	
		_subFolder			= subFolder;
		_numPerFolder		= Integer.parseInt(num);

	}
	
	public void ExportImageSource1 () {
		System.out.println ("call ExportImageSource() ");
		
		loadIniFile(_listFilePath, _folder, _subFolder, _numPerFolder);
		
		loadDriver ();
	
	}
	public ArrayList parseListFile (String listFilePath) {
		
		ArrayList al = new ArrayList();
		
		try{
		  
		  int counter = 0;
		  // Open a file
		  
		  FileInputStream fstream = new FileInputStream(new File(listFilePath));
		 
		  // Get the object of DataInputStream
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine = null;;
	
		  //Read File Line By Line
		  while ((strLine = br.readLine()) != null)   {
			  // Print the content on the console
		  	String [] columns = new String [2];
		  	columns = strLine.split("\t");
		  	
		  	System.out.println ("ArrayList size in parseListFile->"+al.size());
			al.add(counter,columns);
			counter++;		
		  }
		  //Close the input stream
		  in.close();
		  
		}catch (Exception e){//Catch exception if any
		    System.err.println("Error: " + e.getMessage());
		    setErrorOccurred(true);
			setErrorMessage("Exception: "+e.getMessage());
				
		}
	    return al;
	}
	
    public void loadIniFile(String listFile, String folder, String subFolder, int num) {
        
        
        //=====================load ini file========================//
    	try {
    		 System.out.println("listFile222->"+listFile);
        Properties p = new Properties();
        p.load(new FileInputStream("c:\\WVO_ini\\config.ini"));
        
        _cmServer 		= p.getProperty("CMSERVERNAME");
        _cmDBName  		= p.getProperty("CMDBNAME");
        _cmDBUserName 	= p.getProperty("CMDBUSER");
        _cmDBPassword 	= p.getProperty("CMDBPASSWORD");
        //_database = p.getProperty("CMUSER");
        _databaseName 	= p.getProperty("DBNAME");
        _userName 		= p.getProperty("DBUSER");
        _password 		= p.getProperty("DBPASSWORD");
        _table 			= p.getProperty("EXPORTTABLE");
        _itemType 		= p.getProperty("CMITEMTYPE");
        //_itemIDStr 		= p.getProperty("CMUSER");
        _begin 			= p.getProperty("BEGIN");
        _end 			= p.getProperty("END");
        _view 			= p.getProperty("INDEXCLASS");
        _cmUs			= p.getProperty("CMUSER");
        _cmPw 			= p.getProperty("CMPASSWORD");
        
        System.out.println("_view->"+_view);
        //=========================end===============================//
        
        /*_folder = folder;
        _subFolder = subFolder;
        numPerFolder = Integer.parseInt(num);
        _listFilePath = listFile;*/
        
        if (_folder.startsWith("\\\\")) {
        	System.out.println("000");
        	_folder = "\\\\"+(_folder.substring(2)).replaceAll("\\\\", "\\\\\\\\");
        	System.out.println("111");
        } else {
        	System.out.println("222");
        	_folder = _folder.replaceAll("\\\\", "\\\\\\\\");
        }
        
        System.out.println("_folder ==>"+_folder);
        
        aFile = _folder + "\\exportLog.log";
        
        //check if exportlog exists
        File logFile = new File (aFile);
        
        boolean logExists = logFile.exists();
        System.out.println("logExists ==>"+logExists);
        
        if (logExists) {
        	// means need find array index and restart the application from failed point.
        	failIndex = findFailPoint(aFile);
        	System.out.println("failIndex ==>"+failIndex);
        } 
        
        createOutPutPath ();
        
        System.out.println("OUTPUT_FILE_PATH is ==>"+OUTPUT_FILE_PATH);
    	}catch (Exception ex){
    		System.out.println("Error out-->"+ex.getMessage());
    		setErrorOccurred(true);
			setErrorMessage("Exception: "+ex.getMessage());
    	}
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
    
    public void createOutPutPath() {
    	
    	String numberText = intToString (numFolder,4);
        
        
        boolean success1 = (new File(_folder)).mkdir();
        //System.out.println("Folder created success ->"+success1);
        
        OUTPUT_FILE_PATH = _folder + "\\\\" + _subFolder + numberText +"\\\\";
        //System.out.println("OUTPUT_FILE_PATH->"+OUTPUT_FILE_PATH);
        //OUTPUT_FILE_PATH = "\\\\cm4\\c$\\"+folder+"\\";
        //OUTPUT_FILE_PATH = "\\\\WNPCDDBSPP01\\Kofax Dual Release\\TesT\\"+folder+"\\";
        boolean success = (new File(OUTPUT_FILE_PATH)).mkdir();
        //boolean success = (new File("\\\\newmail\\files\\support\\test\\1\\")).mkdir();
        numFolder++;
        System.out.println("Folder created success ==>"+success);
    	//return path;
    }
        
    public  String processPath(String folder) { 
    	 String output = "\\\\"+(folder.substring(2)).replaceAll("\\\\", "\\\\\\\\");
         return output; 
    }
    
    public  String intToString(int num, int digits) { 
        String output = Integer.toString(num); 
        while (output.length() < digits) output = "0" + output; 
        return output; 
    } 

    public void itemsLookup () {
    	
    	int i = 0;
    	ArrayList list = new ArrayList();
    	String sqlString = "";
    	String [] eachRow = null;
    	String itemType = null;
    	String contract = null;
    	BufferedWriter outputlog = null;
    	String itemIDStr= null;
		list = parseListFile(_listFilePath);
		
		System.out.println("List size and file path in itemsLookup = "+list.size()+"======"+_listFilePath);
		try {
			
			// find all contracts
			for (i=0; i<list.size(); i++) {
				
				outputlog = new BufferedWriter(new FileWriter(aFile));
				
				eachRow = new String [2];
				
				itemType = null;
				
				contract = null;
				// if run from a fail over then skip indexes those already exported
				
				if (failIndex > 0 && i >= failIndex) {
					
					eachRow = (String[])list.get(i);
					
					itemType = eachRow[0];
					
					contract = eachRow[1];
					
					System.out.println("Exporting again. ItemType "+itemType+" contract# "+contract+" failIndex "+i);
		    		
		    		if (i>0 && (i%_numPerFolder==0)) {
		        		System.out.println("call create new folder");
		        		createOutPutPath();
		        	}		
			    	
		    		
			    	/**
			    	 * check if it's a workflow item type
			    	 */
		    		if (itemType.equalsIgnoreCase("Check_Credit_Cards")) {
		    			sqlString = "SELECT ITEMID FROM ICMADMIN.ICMUT01011001 where ATTR0000001026='"+ contract +"'";
		    			//sqlString = "SELECT ITEMID FROM ICMADMIN.ICMUT01015001 where ATTR0000001025='"+ contract +"'";
		    			itemType = "Check_Credit_Cards";
		    		} else	
	    				sqlString = "SELECT ITEMID FROM " + _databaseName + "."+itemType+"001 where Contract_Number='"+ contract +"'";
		    			
		    		//sqlString = "SELECT ITEMID FROM " + _databaseName + "."+itemType+"001 where Contract_Number='"+ contract +"'";
		    		
		    			outputlog.write( i + "\t" +contract );
				    	outputlog.newLine();
				    	outputlog.flush();
				    	
		    			loopThruItems (itemType,sqlString);
				} else {
										
					eachRow = (String[])list.get(i);
					
					itemType = eachRow[0];
					
					contract = eachRow[1];
					
					System.out.println("ItemType contract#------->"+itemType+"<--->"+contract+"<--->"+i);
		    		
		    		if (i>0 && (i%_numPerFolder==0)) {
		        		System.out.println("call create new folder");
		        		createOutPutPath();
		        	}		
			    	/**
			    	 * check if it's a workflow item type
			    	 */
		    		if (itemType.equalsIgnoreCase("Check_Credit_Cards")) {
		    			sqlString = "SELECT ITEMID FROM ICMADMIN.ICMUT01011001 where ATTR0000001026='"+ contract +"'";
		    			itemType = "Check_Credit_Cards";
		    		} else	
	    				sqlString = "SELECT ITEMID FROM " + _databaseName + "."+itemType+"001 where Contract_Number='"+ contract +"'";
	    			System.out.println("sqlString------->"+sqlString);
	    		
	    			outputlog.write( i + "\t" +contract );
			    	outputlog.newLine();
			    	outputlog.flush();
				    	
		    		loopThruItems (itemType,sqlString);
				}
		    	
			}
			//outputlog.close();
			
	    }catch (IOException ioe) {
	    		System.out.println("IO exception--->"+ioe.getMessage());  
	    		setErrorOccurred(true);
				setErrorMessage("Exception: "+ioe.getMessage());
				
	    }finally{
	    	try {
	    		
		    	if (outputlog != null) {
		    		outputlog.flush();
		    		outputlog.close();
		    		outputlog = null;
		    	}
	    	 }catch (IOException ioe) {
	    		System.out.println("Finally IO exception--->"+ioe.getMessage());  
	    	 }
	    }
	    
	    //return sqlString;
    }
    
    public boolean getImage (String itemID, String itemType) {
    	boolean isImage = true;
    	DKDDO ddo = null;		
    	DKDatastoreExtICM dsExtICM = null;
		//dkResultSetCursor results = null;
		//dkResultSetCursor tmpresults = null;
		DKResults results =null;
		String queryString = "/"+ itemType +"[@VERSIONID = latest-version(.) AND (@ITEMID=\"" + itemID + "\")]";		
	    DKNVPair parms[] = new DKNVPair[2];
	    parms[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
	    // Specify any Retrieval Options desired.  Default is ATTRONLY.
	    parms[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
	    // Evaluate the query, seting the results into (results)
		DKDatastoreICM dsICM = null;
		try {
			
			System.out.println("Connecting to CM ... -c->"+_cmServer+"----n->"+_cmDBUserName+"=="+_cmUs+"=="+_cmPw);
			dsICM = new DKDatastoreICM();
			dsICM = ICMConnectionPool.getConnection(_cmUs, _cmPw, _cmServer);
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
				System.out.println("readImage: Could not find item for itemid# = " + itemID);
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
						 
						dkIterator iter3 = dkParts.createIterator();
						int partCounter = 1;
						int off = 0;
						while (iter3.more()) {
							
							DKLobICM part = (DKLobICM) iter3.next();
                            part.retrieve(dkRetrieveOptions.dkNVPair());
							System.out.println(" PID ID:  "+(part.getPidObject()).pidString()+"--"+intToString (partCounter,4));
                            
							OutputStream out = new FileOutputStream(new File(OUTPUT_FILE_PATH  + itemType +"_"+ itemID +"_"+intToString (partCounter,4)+".tif"));
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
                        	inputStream = null;
                        	out.flush();
                        	out.close();
                        	out = null;
                        	System.out.println("Writr image to file path : " + OUTPUT_FILE_PATH  + itemType +"_"+ itemID +"_"+intToString (partCounter,4)+".tif");
                        	partCounter++;
						}
						iter3 = null;
						
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
			}
			//ICMConnectionPool.returnConnection(dsICM);
			
	    } catch (DKException dke)	{
	    	dke.printStackTrace();
	    	setErrorOccurred(true);
			setErrorMessage("Exception: "+ dke.getMessage());
			
			
	    } catch (InstantiationException ie)	{
	    	ie.printStackTrace();
	    	setErrorOccurred(true);
			setErrorMessage("Exception: "+ ie.getMessage());
			
	    } catch (Exception ex)	{
	    	ex.printStackTrace();
			System.out.println("exception------>"+ex.getLocalizedMessage());
			setErrorOccurred(true);
			setErrorMessage("Exception: "+ ex.getMessage());
			
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
        	url1 = "jdbc:db2://"+_cmServer+":50000/"+_cmDBName;
        	System.out.println("=================================================");
    	    System.out.println("Connecting To DB2 To DB Lookup (CM LS):");
    	    System.out.println("-------------------------------------------------");
    	    System.out.println(" Database URL:   " + url1);
    	    System.out.println(" DB2 UserName:   " + _cmDBUserName);
    	    System.out.println("=================================================\n");
        	
        	itemsLookup ();
        	
	    }catch(Exception e){
	    	System.out.println(e);
	    	setErrorOccurred(true);
			setErrorMessage("Exception: "+ e.getMessage());
	    } finally {
	    	try {
	    		if (conn1 != null)	{
	            	conn1.close();
	            	conn1 = null;
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
        	
    public String loopThruItems (String it, String selectSQLStat) {
    	String itemid 			= null;
    	String imageName     	= null;
    	String status	     	= null;
    	int caseIDValue			= 0;
    	PreparedStatement update= null;
    	
    	stmt 					= null;
    	rs   					= null;
    	sql						= null;
    	sql0					= null;
    	try{
    		conn = DriverManager.getConnection(url1,_cmDBUserName,_cmDBPassword);
    			
    		stmt = conn.createStatement();
		
    		if (it.equalsIgnoreCase("Check_Credit_Cards"))
    			sql0 =  "UPDATE " + _databaseName + ".ICMUT01011001 SET ATTR0000001039 = ? WHERE itemid = ?";
    		else
    			sql0 =  "UPDATE " + _databaseName + "." + it  + "001 SET HISTORIC_REDACT_STATUS = ? WHERE itemid = ?";
    		update	= conn.prepareStatement(sql0);
    	
    		rs = stmt.executeQuery(selectSQLStat);
    		
	    	while (rs.next()) {
	    		imageName = null;
	    		itemid 	= null;
	    		itemid = rs.getString("ITEMID");
	    		System.out.println("before call get image");
	    		//only update items that has image
	    		if (getImage (itemid, it)) {
		    		counter++;
		        	System.out.println(itemid+"<--contract counter->"+counter);
		        	try{
		        	//update.setInt(1,1);
		        	update.setString(1,"1");
		            
					update.setString(2,itemid);
					
					update.addBatch();
					System.out.println("after update.addBatch()");
					update.executeBatch();
		        	}catch (SQLException sqle) {
		        		System.out.println(" Error Exception =" + sqle.getMessage());
		            	setErrorOccurred(true);
		        		setErrorMessage("Exception: "+ sqle.getMessage());
		        	}
	    		}
	        	if (counter%_numPerFolder==0) {
	        		System.out.println("call commit!");
	        		
	        		conn.commit();
	        	}		
	    	 } // end while loop
	    	rs.close();
	    	rs = null;
	    	
	    	System.out.println("itemid------------->"+itemid);
        
    }catch (Exception ex){
    	System.out.println(" Error Exception22 =" + ex.getMessage());
    	
    	System.out.println(" set error to class ");
		setErrorOccurred(true);
		setErrorMessage("Exception: "+ ex.getMessage());
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
        	if (update != null) {
            	update.close();
            	update = null;
            }
    	} catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    return itemid;
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
	
	public void exit () {
		System.out.println("Calling system exit.");
		System.exit(1);        
	}

}
