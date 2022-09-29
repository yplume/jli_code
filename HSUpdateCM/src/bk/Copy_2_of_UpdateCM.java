/*
 * Created on Jul 27, 2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.ibm.mm.sdk.common.DKConstant;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKDocRoutingServiceICM;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKPid;
import com.ibm.mm.sdk.common.DKWorkPackageICM;
import com.ibm.mm.sdk.common.dkCollection;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreICM;


public class Copy_2_of_UpdateCM {

	//private EmailSender m_ems = null;
	private XMLReader m_pmxr = null;
	private int count = 0;
	private int waitingTime = 0;
	private Connection connection = null;
	private Connection connectionUS = null;
	private Connection connectionCA = null;
	private DKDatastoreICM dsICM = null;
	private String attribute = null;
	private String m_BaseURL = "";
	private String lastMovedTime = null;
	final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    public void UpdateWorkflow(){
	    System.out.println("UpdateWorkflow ....");
	    //TODO generate report
	    try{
	    	m_pmxr = new XMLReader();
	    	m_pmxr.readXMLConfig();
			String url = "jdbc:as400://"+ m_pmxr.getDBServerName();
			String user = m_pmxr.getDBUser();
			String pass = m_pmxr.getDBPass();
			System.out.println("Before get AS400 driver!!!");
	    	//DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
	    	//DriverManager.registerDriver((java.sql.Driver)Class.forName("com.ibm.as400.access.AS400JDBCDriver").newInstance());
	    	//connection = DriverManager.getConnection("jdbc:as400://"+ m_pmxr.getDBServerName() + "/" + m_pmxr.getDBName() + ";date format=iso; time format=iso", m_pmxr.getDBUser(), m_pmxr.getDBPass());
			
			Class.forName("com.ibm.as400.access.AS400JDBCDriver");
			
	    	System.out.println("Load driver!!"+url+" - "+user+" -  "+pass);
	    	try{
	    		connectionUS = DriverManager.getConnection(url, user, pass);
	    		connectionCA = DriverManager.getConnection(url, "KOFAX02", "ju41776");
		    }catch(Exception e){
				System.out.println("AS400 err="+e.getMessage());
			}
	    	System.out.println("After load driver and get AS400 connection!");
	    	ArrayList missRecord = new ArrayList(); 
	    	attribute = m_pmxr.getAttr();
	    	dsICM = new DKDatastoreICM();
	        System.out.println("After init DKDatastore!");
	        dsICM = ICMConnectionPool.getConnection(m_pmxr.getCmUserName(), m_pmxr.getCmPassword(), m_pmxr.getCmServerName());
	        System.out.println("After get CM conntion!");
	        DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
            com.ibm.mm.sdk.server.DKDatastoreExtICM dsExtICM = (com.ibm.mm.sdk.server.DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
            dkCollection workPackages = routingService.listWorkPackages("WaitingForUpdateWorkflow", "");
            dkCollection workPackagesDelete = routingService.listWorkPackages("DeletedItems", "");
            dkCollection workPackagesAPEx = routingService.listWorkPackages("APException", "");
            //dkCollection workPackagesStartWaitingForUpdateWL = routingService.listWorkPackages("StartWaitingForUpdateProcessWL", "");
            DKDDO ddo = null;
            DKDDO ddoDelete = null;
            DKDDO ddoAPE = null;
            DKDDO ddoSWFU = null;
            DKWorkPackageICM workPackage;
            DKWorkPackageICM workPackageDelete;
            DKWorkPackageICM workPackageAPE;
            DKWorkPackageICM workPackageStartWaitingForUpdateWL;
            dkIterator iter;
            dkIterator iterDelete;
            dkIterator iterAPE;
            dkIterator iterStartWaitingForUpdateWL;
            System.out.println("Number of items in APException worklist:"+workPackagesAPEx.cardinality());
            System.out.println("Number of items in WaitingForUpdateWorkflow worklist:"+workPackages.cardinality());
            System.out.println("Number of items in DeletedItems worklist:"+workPackagesDelete.cardinality());
            //System.out.println("Number of items in StartWaitingForUpdateProcessWL worklist:"+workPackagesStartWaitingForUpdateWL.cardinality());
            
///////////////////////////////////Start StartWaitingForUpdateProcessWL  Worklist///////////////////////////////
            /*if (workPackagesStartWaitingForUpdateWL.cardinality() == 0) {
            	workPackagesStartWaitingForUpdateWL = null;
            } else {
            
            	iterStartWaitingForUpdateWL = workPackagesStartWaitingForUpdateWL.createIterator();
            	
            	workPackagesStartWaitingForUpdateWL = null;
                
                while (iterStartWaitingForUpdateWL.more()) {
                	
                	System.out.println("Get item and set delete attribute to true!");
                	
                	workPackageStartWaitingForUpdateWL = (DKWorkPackageICM) iterStartWaitingForUpdateWL.next();
                    
                	ddoSWFU = dsICM.createDDO(workPackageStartWaitingForUpdateWL.getItemPidString());
                    
                    boolean bNoAvailableItem = true;
                    
                    String checkedOutByStr = "";
                    
                    if (dsExtICM.isCheckedOut(ddoSWFU)) {
                        checkedOutByStr = dsExtICM.checkedOutUserid(ddoDelete);
                        
                        if (checkedOutByStr.equalsIgnoreCase(m_pmxr.getCmUserName())) {
                            bNoAvailableItem = false;
                        }
                    } else {
                        bNoAvailableItem = false;
                        dsICM.checkOut(ddoSWFU);
                    }
                    if (bNoAvailableItem == false) {
                        System.out.println("End current proess!");
                        
                        routingService.terminateProcess(workPackageStartWaitingForUpdateWL.getPidString());
                        
                        System.out.println("Start WaitingForUpdateProcess!");
                        
						routingService.startProcess("WaitingForUpdateProcess", ddoSWFU.getPidObject().toString(), 100, "ICMADMIN");
						
                    }   
                    if (dsExtICM.isCheckedOut(ddoSWFU)) {
                        dsICM.checkIn(ddoSWFU);
                    }
                }                  
            }
            */
            ///////////////////////////////////////////End////////////////////////////////////////////////
            ///////////////////////////////////Start Monitor Delte Worklist///////////////////////////////
            if (workPackagesDelete.cardinality() == 0) {
                workPackagesDelete = null;
            } else {
            
            	iterDelete = workPackagesDelete.createIterator();
            	
                workPackagesDelete = null;
                
                while (iterDelete.more()) {
                	
                	System.out.println("Get item and set delete attribute to true!");
                	
                    workPackageDelete = (DKWorkPackageICM) iterDelete.next();
                    
                    ddoDelete = dsICM.createDDO(workPackageDelete.getItemPidString());
                    
                    boolean bNoAvailableItem = true;
                    
                    String checkedOutByStr = "";
                    
                    if (dsExtICM.isCheckedOut(ddoDelete)) {
                        checkedOutByStr = dsExtICM.checkedOutUserid(ddoDelete);
                        
                        if (checkedOutByStr.equalsIgnoreCase(m_pmxr.getCmUserName())) {
                            bNoAvailableItem = false;
                        }
                    } else {
                        bNoAvailableItem = false;
                        dsICM.checkOut(ddoDelete);
                    }
                    if (bNoAvailableItem == false) {
                        ddoDelete.retrieve(com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_ATTRONLY | com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_CHILDREN | com.ibm.mm.sdk.common.DKConstant.DK_CM_CHECKOUT);
                        System.out.println("Set deleted attr to true!");
                    	ddoDelete.setData(ddoDelete.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "Deleted"), "true");
                    	
                    	ddoDelete.update();	
						System.out.println("Update DeletedItems ready to off the process!");
						routingService.continueProcess(workPackageDelete.getPidString(), "Archive", "ICMADMIN");
						
                    }   
                    if (dsExtICM.isCheckedOut(ddoDelete)) {
                        dsICM.checkIn(ddoDelete);
                    }
                }                  
            }
            
            ///////////////////////////////////////////End////////////////////////////////////////////////
            
            if (workPackages.cardinality() == 0) {
                workPackages = null;
            } else {
                String barCode = "";
                //short dataId;
                String sql = "";
                String tableName = "";
                iter = workPackages.createIterator();
                workPackages = null;
                
                Date today = new Date();                  
                
                String exceptionStepName = "";
                String processName = "";
                
                
                while (iter.more()) {
                	
                	//exceptionStepName = "Send Back To APException";
                	tableName = "";
                	System.out.println("Get item!!");
                    workPackage = (DKWorkPackageICM) iter.next();
                    Timestamp lastMovedTimestamp = workPackage.getTimeLastMoved();
                    
                    Date lastMovedDate = new Date(lastMovedTimestamp.getTime());
                    int diffInDays = (int) ((today.getTime() - lastMovedTimestamp.getTime())/ DAY_IN_MILLIS );
                    System.out.println("DiffInDays and lastMovedTimestamp :"+diffInDays+"&&"+lastMovedTimestamp);
                    
                    ddo = dsICM.createDDO(workPackage.getItemPidString());
                    
                    System.out.println("Check DDO ItemID = "+ddo.getPidObject().getIdString());
                    
                    processName = workPackage.getProcessName();
                    
                    //check item type then point to different table
                    if (ddo.getObjectType().toString().equalsIgnoreCase("APC")){
                    	connection = connectionCA;
                    	tableName = "ARCPDTA71";
                    	exceptionStepName = "Send To Error";
                    }
                    if (ddo.getObjectType().toString().equalsIgnoreCase("APU")){
                    	connection = connectionUS;
                    	tableName = "HSIPDTA71";
                    	
                    	if(processName.equalsIgnoreCase("UpdateCM"))
                    		exceptionStepName = "Send To Error";
                    	else
                    		exceptionStepName = "Send Back To ErrorWorklist";
                    	//exceptionStepName = "Send Back To APException";
                    	
                    }
                    System.out.println("Item Type: "+ddo.getObjectType().toString()+"--"+tableName);
                    
                    boolean bNoAvailableItem = true;
                    String checkedOutByStr = "";
                    if (dsExtICM.isCheckedOut(ddo)) {
                        checkedOutByStr = dsExtICM.checkedOutUserid(ddo);
                        if (checkedOutByStr.equalsIgnoreCase(m_pmxr.getCmUserName())) {
                            bNoAvailableItem = false;
                        }
                    } else {
                        bNoAvailableItem = false;
                        dsICM.checkOut(ddo);
                    }
                    if (bNoAvailableItem == false) {
                        ddo.retrieve(com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_ATTRONLY | com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_CHILDREN | com.ibm.mm.sdk.common.DKConstant.DK_CM_CHECKOUT);

                        barCode = "";
                        
                        if (diffInDays < m_pmxr.getWaitingTime()) {
                        	//if (ddo.getDataByName("BarCode") != null) {
                        	if (ddo.getDataByName("BarCode") != null && ddo.getDataByName(attribute) == null) {
        	            	    		barCode = ddo.getDataByName("BarCode").toString().trim();
	            	    		
        	    				System.out.println("BarCode = "+barCode);
        	    				
	            	    		ArrayList resultAL   = new ArrayList(); 
	            	    		resultAL = getLineAL (barCode, connection, tableName);
	            	    		
	            	    		if (resultAL.size() > 0) {
	        	    			try{
	        	    				
	        	    				if ((String)resultAL.get(0) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "Company"), 		(String)resultAL.get(0));
	        	    				if ((String)resultAL.get(1) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "DocumentType"), 	(String)resultAL.get(1));
	        	    				if ((String)resultAL.get(2) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "DocumentNumber"), 	(String)resultAL.get(2));
	        	    				if ((String)resultAL.get(3) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "GLDate"), 			java.sql.Date.valueOf((String)resultAL.get(3)));
	        	    				if ((String)resultAL.get(4) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "BatchNumber"), 	(String)resultAL.get(4));
	        	    				if ((String)resultAL.get(5) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "BatchType"), 		(String)resultAL.get(5));
	        	    				if ((String)resultAL.get(6) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "BatchDate"), 		java.sql.Date.valueOf((String)resultAL.get(6)));
	        	    				if ((String)resultAL.get(7) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "GLPeriodNum"), 	(String)resultAL.get(7));
	        	    				if ((String)resultAL.get(8) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "FiscalYear"), 		(String)resultAL.get(8));
	        	    				if ((String)resultAL.get(9) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "AddressNum"), 		(String)resultAL.get(9));
	        	    				if ((String)resultAL.get(10) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "InvoiceDate"), 	java.sql.Date.valueOf((String)resultAL.get(10)));
	        	    				if ((String)resultAL.get(11) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "DueDate"), 		java.sql.Date.valueOf((String)resultAL.get(11)));
	        	    				if ((String)resultAL.get(12) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "InvoiceNum"), 		(String)resultAL.get(12));
	        	    				if ((String)resultAL.get(13) != null)
	        	    					ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "PONumber"), 		(String)resultAL.get(13));
	        	    				
									ddo.update();	
									
									System.out.println("Update CM ready to off the process!");
									if(processName.equalsIgnoreCase("UpdateCM"))
										routingService.continueProcess(workPackage.getPidString(), "Continue", "ICMADMIN");
									else
										routingService.continueProcess(workPackage.getPidString(), "Archive", "ICMADMIN");
									//routingService.continueProcess(workPackage.getPidString(), "Continue", "ICMADMIN");
	            	    			
	        	    			} catch (DKException exc) {
	        	    				System.out.println("DKException Error in routing statement " + exc);
	        	    				routingService.continueProcess(workPackage.getPidString(), exceptionStepName, "ICMADMIN");
	        	    			}
	            	    		}
	                        }
            	    		
            	        } else { //If an item stay in worklist more than 60 day move to error queue
            	        	
            	        	System.out.println("Send to "+exceptionStepName+" worklist.");
            	        	routingService.continueProcess(workPackage.getPidString(), exceptionStepName, "ICMADMIN");
            	        }
            	    }
                    if (dsExtICM.isCheckedOut(ddo)) {
                        dsICM.checkIn(ddo);
                    }
                } //while
            } //end else
            
            ////////////////////APException Worklist////////////////////
            if (workPackagesAPEx.cardinality() == 0) {
            	workPackagesAPEx = null;
            } else {
                String barCode = "";
                //short dataId;
                String sql = "";
                String tableName = "";
                iterAPE = workPackagesAPEx.createIterator();
                workPackagesAPEx = null;
                
                //Date today = new Date();                  
                
                //String exceptionStepName = "";
                String processName = "";
                
                
                while (iterAPE.more()) {
                	
                	//exceptionStepName = "Send Back To APException";
                	tableName = "";
                	System.out.println("Get item!!!!!!!!!!!!!");
                	workPackageAPE = (DKWorkPackageICM) iterAPE.next();
                    //Timestamp lastMovedTimestamp = workPackage.getTimeLastMoved();
                    
                    //Date lastMovedDate = new Date(lastMovedTimestamp.getTime());
                    //int diffInDays = (int) ((today.getTime() - lastMovedTimestamp.getTime())/ DAY_IN_MILLIS );
                    //System.out.println("DiffInDays and lastMovedTimestamp :"+diffInDays+"&&"+lastMovedTimestamp);
                    
                    ddoAPE = dsICM.createDDO(workPackageAPE.getItemPidString());
                    
                    System.out.println("Check ddoAPE ItemID = "+ddoAPE.getPidObject().getIdString());
                    
                    processName = workPackageAPE.getProcessName();
                    
                    //check item type then point to different table
                    if (ddoAPE.getObjectType().toString().equalsIgnoreCase("APC")){
                    	tableName = "ARCPDTA71";
                    	//exceptionStepName = "Send To Error";
                    }
                    if (ddoAPE.getObjectType().toString().equalsIgnoreCase("APU")){
                    	tableName = "HSIPDTA71";
                    	
                    	//if(processName.equalsIgnoreCase("UpdateCM"))
                    	//	exceptionStepName = "Send To Error";
                    	//else
                    	//	exceptionStepName = "Send Back To APException";
                    	
                    }
                    System.out.println("Item Type: "+ddoAPE.getObjectType().toString()+"--"+tableName);
                    
                    boolean bNoAvailableItem = true;
                    String checkedOutByStr = "";
                    if (dsExtICM.isCheckedOut(ddoAPE)) {
                        checkedOutByStr = dsExtICM.checkedOutUserid(ddoAPE);
                        if (checkedOutByStr.equalsIgnoreCase(m_pmxr.getCmUserName())) {
                            bNoAvailableItem = false;
                        }
                    } else {
                        bNoAvailableItem = false;
                        dsICM.checkOut(ddoAPE);
                    }
                    if (bNoAvailableItem == false) {
                        ddoAPE.retrieve(com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_ATTRONLY | com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_CHILDREN | com.ibm.mm.sdk.common.DKConstant.DK_CM_CHECKOUT);

                        barCode = "";
                        
                        //if (diffInDays < m_pmxr.getWaitingTime()) {
                        if (ddoAPE.getDataByName("BarCode") != null) {
                        	//if (ddoAPE.getDataByName("BarCode") != null && ddoAPE.getDataByName(attribute) == null) {
    	            	    		barCode = ddoAPE.getDataByName("BarCode").toString().trim();
	            	    		
        	    				System.out.println("BarCode = "+barCode);
        	    				
	            	    		ArrayList resultAL   = new ArrayList(); 
	            	    		resultAL = getLineAL (barCode, connection, tableName);
	            	    		
	            	    		if (resultAL.size() > 0) {
	        	    			try{
	        	    				
	        	    				if ((String)resultAL.get(0) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "Company"), 		(String)resultAL.get(0));
	        	    				if ((String)resultAL.get(1) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "DocumentType"), 	(String)resultAL.get(1));
	        	    				if ((String)resultAL.get(2) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "DocumentNumber"), 	(String)resultAL.get(2));
	        	    				if ((String)resultAL.get(3) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "GLDate"), 			java.sql.Date.valueOf((String)resultAL.get(3)));
	        	    				if ((String)resultAL.get(4) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "BatchNumber"), 	(String)resultAL.get(4));
	        	    				if ((String)resultAL.get(5) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "BatchType"), 		(String)resultAL.get(5));
	        	    				if ((String)resultAL.get(6) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "BatchDate"), 		java.sql.Date.valueOf((String)resultAL.get(6)));
	        	    				if ((String)resultAL.get(7) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "GLPeriodNum"), 	(String)resultAL.get(7));
	        	    				if ((String)resultAL.get(8) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "FiscalYear"), 		(String)resultAL.get(8));
	        	    				if ((String)resultAL.get(9) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "AddressNum"), 		(String)resultAL.get(9));
	        	    				if ((String)resultAL.get(10) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "InvoiceDate"), 	java.sql.Date.valueOf((String)resultAL.get(10)));
	        	    				if ((String)resultAL.get(11) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "DueDate"), 		java.sql.Date.valueOf((String)resultAL.get(11)));
	        	    				if ((String)resultAL.get(12) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "InvoiceNum"), 		(String)resultAL.get(12));
	        	    				if ((String)resultAL.get(13) != null)
	        	    					ddoAPE.setData(ddoAPE.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, "PONumber"), 		(String)resultAL.get(13));
	        	    				
	        	    				ddoAPE.update();	
									
									System.out.println("Update APE CM ready to off the process! " +processName);
									if(processName.equalsIgnoreCase("UpdateCM"))
										System.out.println(" Off the process!");
									//	routingService.terminateProcess(workPackageAPE.getPidString());
									else {
										
										routingService.terminateProcess(workPackageAPE.getPidString());
										System.out.println(" Terminate Process !");
									//	routingService.continueProcess(workPackage.getPidString(), "Archive", "ICMADMIN");
									//routingService.continueProcess(workPackage.getPidString(), "Continue", "ICMADMIN");
									}
	        	    			} catch (DKException exc) {
	        	    				System.out.println("DKException APE Error in routing statement " + exc);
	        	    			//	routingService.continueProcess(workPackage.getPidString(), exceptionStepName, "ICMADMIN");
	        	    			}
	            	    		}
	                        }
            	    		
            	        /*} else { //If an item stay in worklist more than 60 day move to error queue
            	        	
            	        	System.out.println("Send to "+exceptionStepName+" worklist.");
            	        	routingService.continueProcess(workPackage.getPidString(), exceptionStepName, "ICMADMIN");
            	        }*/
            	    }
                    if (dsExtICM.isCheckedOut(ddoAPE)) {
                        dsICM.checkIn(ddoAPE);
                    }
                } //while
            } //end else
            ddoAPE = null;
            iterAPE = null;
            workPackageAPE = null;
           
            /////////////////////////END//////////////////////////////
            dsExtICM = null;
            ddo = null;
            iter = null;
            workPackage = null;
            routingService = null;
                
	    } catch (DKException exc) {
            System.out.println("run: Error in routing statement " + exc);
           // emailError(exc.getMessage(), "DKException - checkForUpdates");
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } catch (Exception exc) {
            System.out.println("run: Error in Exception " + exc);
           // emailError(exc.getMessage(), "Exception - checkForUpdates");
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } finally {
        	if (connection != null) {
                try {
                	connection.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
                connection = null;
            }	

            if (dsICM != null) {
                try {
                    ICMConnectionPool.returnConnection(dsICM);
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("<Ck update>Error returning connection to the pool." + e.getMessage());
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

    public String convertJulianDate (String julianDate) {
    	
    	String newDate = julianDate.substring(1,6);
    	String output = null;
    	try {
	    	DateFormat fmt1 = new SimpleDateFormat("yyDDD");   
	        Date date = fmt1.parse(newDate);   
	        DateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");   
	        output = fmt2.format(date);   
	    }catch (Exception ex){
    		 System.out.println(ex);
    	}
    	return output;
    }
    
	public ArrayList getLineAL(String barCode, Connection connection, String tableName) {
		ArrayList lineAL = new ArrayList();
		System.out.println("After connection & barCode !!"+barCode);
	    Statement stmt = null;
	    ResultSet rs = null;
	    String asDocumentCompany = null;
	    String asDocumentNumber = null;
	    String asDocumentType = null;
	    String asGLDate = null;
	    String asBatchNumber = null;
	    String asBatchType = null;
	    String asBatchDate = null;
	    String asGLPeriodNumber = null;
	    String asFiscalYear = null;
	    String asAddressNumber = null;
	    String asInvoiceDate = null;
	    String asDueDate = null;
	    String asInvoiceNumber = null;
	    String asPONumber = null;
	    
	    try {
		    stmt = connection.createStatement();
	        System.out.println("Successfully connected!!!!");
	        String query = "SELECT count(*) as count FROM " + tableName + ".F0911HH WHERE GLR2 ='" + barCode + "'"; 
	        
            rs = stmt.executeQuery(query);
            int count = 0;
            //System.out.println("Query count!");
            while (rs.next()) {
                count = rs.getInt(1);
            }
            System.out.println("query & Count = "+query + " ++++++ " + count);
            if (count > 0) {
             
            	/********************Test muli attributes**************************
            	query ="SELECT GLKCO AS DocumentCompany ,";
		        query += "GLDCT AS DocumentType, ";
		        query += "GLDOC AS DocumentNumber, "; 
		        query += "GLDGJ AS GLDate ,";
		        query += "GLICU AS BatchNumber ,";
		        query += "GLICUT AS BatchType, ";
		        query += "GLDICJ AS BatchDate, ";
		        query += "GLPN AS GLPeriodNumber, ";
		        query += "GLFY AS FiscalYear, ";
		        query += "RPDIVJ AS InvoiceDate, ";
		        query += "RPDDJ AS DueDate, ";
		        query += "RPVINV AS InvoiceNumber, ";
		        query += "RPPO AS PONumber ";
		        query += "FROM " + tableName + ".F0911HH a inner join " + tableName + ".F0411LE b ";
				query += "On a.GLDCT = b.RPDCT and a.GLDOC = b.RPDOC and a.GLKCO = b.RPKCO where a.GLR2 ='" + barCode + "' ";
		        rs = stmt.executeQuery(query); 
		        while (rs.next()) {
		        	System.out.println("DocumentCompany="+rs.getString("DocumentCompany"));
		        	System.out.println("DocumentType="+rs.getString("DocumentType"));
		        	System.out.println("DocumentNumber="+rs.getString("DocumentNumber"));
		        	System.out.println("GLDate="+rs.getString("GLDate"));
		        	System.out.println("BatchNumber="+rs.getString("BatchNumber"));
		        	System.out.println("BatchType="+rs.getString("BatchType"));
		        	System.out.println("BatchDate="+convertJulianDate(rs.getString("BatchDate")));
		        	System.out.println("GLPeriodNumber="+rs.getString("GLPeriodNumber"));
		        	System.out.println("FiscalYear="+rs.getString("FiscalYear"));
		        	System.out.println("InvoiceDate="+rs.getString("InvoiceDate"));
		        	System.out.println("DueDate="+rs.getString("DueDate"));
		        	System.out.println("InvoiceNumber="+rs.getString("InvoiceNumber"));
		        	System.out.println("PONumber="+rs.getString("PONumber"));
			    }
            	**********************Test end************************************/
		        
		        query = "SELECT GLKCO AS DocumentCompany ,";
		        query += "GLDCT AS DocumentType, ";
		        query += "GLDOC AS DocumentNumber, "; 
		        query += "GLDGJ AS GLDate ,";
		        query += "GLICU AS BatchNumber ,";
		        query += "GLICUT AS BatchType, ";
		        query += "GLDICJ AS BatchDate, ";
		        query += "GLPN AS GLPeriodNumber, ";
		        query += "GLFY AS FiscalYear ";
		        query += "FROM " + tableName + ".F0911HH where GLR2 ='" + barCode + "' "; 
		        query += "FETCH FIRST 1 ROWS ONLY";
				rs = stmt.executeQuery(query); 
				while (rs.next()) {
					asDocumentCompany 	= rs.getString("DocumentCompany");
					asDocumentType 		= rs.getString("DocumentType");
					asDocumentNumber 	= rs.getString("DocumentNumber");
					asGLDate 			= convertJulianDate(rs.getString("GLDate"));
					asBatchNumber 		= rs.getString("BatchNumber");
					asBatchType 		= rs.getString("BatchType");
					asBatchDate 		= convertJulianDate(rs.getString("BatchDate"));
					asGLPeriodNumber 	= rs.getString("GLPeriodNumber");
					asFiscalYear 		= rs.getString("FiscalYear");
					
					query = "SELECT RPAN8 AS AddressNumber, ";
					query += "RPDIVJ AS InvoiceDate, ";
					query += "RPDDJ AS DueDate, ";
					query += "RPVINV AS InvoiceNumber, ";
					query += "RPPO AS PONumber ";
					query += "FROM " + tableName + ".F0411LE WHERE RPDCT = '" + asDocumentType + "' AND RPDOC = " + asDocumentNumber + " AND RPKCO = '" + asDocumentCompany + "' FETCH FIRST 1 ROWS ONLY";
					rs = stmt.executeQuery(query); 
					while (rs.next()) {
						asAddressNumber 	= rs.getString("AddressNumber");
						asInvoiceDate 		= convertJulianDate(rs.getString("InvoiceDate"));
						asDueDate 			= convertJulianDate(rs.getString("DueDate"));
						asInvoiceNumber 	= rs.getString("InvoiceNumber");
						asPONumber 			= rs.getString("PONumber");
					}
					lineAL.add(asDocumentCompany);
					lineAL.add(asDocumentType);
					lineAL.add(asDocumentNumber);
					lineAL.add(asGLDate);
					lineAL.add(asBatchNumber);
					lineAL.add(asBatchType);
					lineAL.add(asBatchDate);
					lineAL.add(asGLPeriodNumber);
					lineAL.add(asFiscalYear);
					lineAL.add(asAddressNumber);
					lineAL.add(asInvoiceDate);
					lineAL.add(asDueDate);
					lineAL.add(asInvoiceNumber);
					lineAL.add(asPONumber);
					System.out.println("After set ArrayList!");
				}
            }
		stmt.close();
		rs.close();
		stmt = null;
		rs = null;
	    } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("run: Error in retreiving sql = " + sqle.getMessage());
        
	    } catch (Exception ex) {
	    	System.out.println(" Error finally =" + ex.getMessage());
	    } finally {
	    	if (rs != null) {
                
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(" Error finally =" + ex.getMessage());
                }
                rs = null;
            }

            if (stmt != null) {
                
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    System.out.println(" Error finally =" + ex.getMessage());
                }
                stmt = null;
            }
	    
	    }
		return lineAL;
	}	

}
