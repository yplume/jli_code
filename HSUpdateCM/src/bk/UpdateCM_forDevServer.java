							  
import java.sql.ResultSet;
							 
import java.sql.Statement;
						  
				   
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.dkCollection;
import java.sql.SQLException;
import com.ibm.mm.sdk.common.DKException;
import java.util.Date;
import com.ibm.mm.sdk.common.dkDataObject;
import com.ibm.mm.sdk.common.DKWorkPackageICM;
import com.ibm.mm.sdk.server.DKDatastoreExtICM;
import com.ibm.mm.sdk.common.dkDatastore;
import com.ibm.mm.sdk.common.DKDocRoutingServiceICM;
import java.util.ArrayList;
import java.sql.DriverManager;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import java.sql.Connection;

// 
// Decompiled by Procyon v0.5.36
// 

public class UpdateCM_forDevServer
{
    private XMLReader m_pmxr;
    private int count;
    private int waitingTime;
    private Connection connection;
    private Connection connectionUS = null;
	private Connection connectionCA = null;
	private DKDatastoreICM dsICM;
    private String attribute;
    private String m_BaseURL;
    private String lastMovedTime;
    final long DAY_IN_MILLIS = 86400000L;
    
    public UpdateCM_forDevServer() {
        this.m_pmxr = null;
        this.count = 0;
        this.waitingTime = 0;
        this.connection = null;
        this.dsICM = null;
        this.attribute = null;
        this.m_BaseURL = "";
        this.lastMovedTime = null;
    }
    
    public void UpdateWorkflow() {
        System.out.println("UpdateWorkflow ....");
        try {
		 
            (this.m_pmxr = new XMLReader()).readXMLConfig();
							 
            //final String url = "jdbc:as400://" + this.m_pmxr.getDBServerName();
            final String url = "jdbc:as400://E;database name=HSIPDTA71;prompt=false;translate binary=true;naming=system";
            final String url1 = "jdbc:as400://" + this.m_pmxr.getDBServerName() + ";database name=ARCPDTA71;prompt=false;translate binary=true;naming=system";
            final String user = this.m_pmxr.getDBUser();
            final String pass = this.m_pmxr.getDBPass();
            System.out.println("Before get AS400 driver!");
																				 
																														   
																																																	 
   
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
   
            System.out.println("---------------111---------------"+this.m_pmxr.getDBServerName());												 
            connectionUS = DriverManager.getConnection(url, user, pass);
            System.out.println("---------------222---------------");												 
            //System.out.println("DriverManager!"+DriverManager.getConnection(url, "KOFAX02", "ju41776"));
    		connectionCA = DriverManager.getConnection(url1, "KOFAX02", "IMG21DOC");																 
    		System.out.println("---------------333---------------");												 
            										
	
            System.out.println("After load driver and get AS400 connection!");
            final ArrayList missRecord = new ArrayList();
            this.attribute = this.m_pmxr.getAttr();
            this.dsICM = new DKDatastoreICM();
            System.out.println("After init DKDatastore!");
            this.dsICM = ICMConnectionPool.getConnection(this.m_pmxr.getCmUserName(), this.m_pmxr.getCmPassword(), this.m_pmxr.getCmServerName());
            System.out.println("After get CM conntion!");
            DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM((dkDatastore)this.dsICM);
            DKDatastoreExtICM dsExtICM = (DKDatastoreExtICM)this.dsICM.getExtension("DATASTORE_EXTENSION");
            dkCollection workPackages = routingService.listWorkPackages("WaitingForUpdateWorkflow", "");
            dkCollection workPackagesDelete = routingService.listWorkPackages("DeletedItems", "");
            dkCollection workPackagesAPEx = routingService.listWorkPackages("APException", "");
																																	   
            DKDDO ddo = null;
            DKDDO ddoDelete = null;
            DKDDO ddoAPE = null;
            final DKDDO ddoSWFU = null;
										 
							   
												   
            System.out.println("Number of items in APException worklist:" + workPackagesAPEx.cardinality());
            System.out.println("Number of items in WaitingForUpdateWorkflow worklist:" + workPackages.cardinality());
            System.out.println("Number of items in DeletedItems worklist:" + workPackagesDelete.cardinality());
																																				  
																											  
																										  
            if (workPackagesDelete.cardinality() == 0) {
                workPackagesDelete = null;
            }
            else {
			
                final dkIterator iterDelete = workPackagesDelete.createIterator();
			 
                workPackagesDelete = null;
				
                while (iterDelete.more()) {
				 
                    System.out.println("Get item and set delete attribute to true!");
				 
                    final DKWorkPackageICM workPackageDelete = (DKWorkPackageICM)iterDelete.next();
					
                    ddoDelete = this.dsICM.createDDO(workPackageDelete.getItemPidString());
					
                    boolean bNoAvailableItem = true;
					
                    String checkedOutByStr = "";
					
                    if (dsExtICM.isCheckedOut((dkDataObject)ddoDelete)) {
                        checkedOutByStr = dsExtICM.checkedOutUserid((dkDataObject)ddoDelete);
						
                        if (checkedOutByStr.equalsIgnoreCase(this.m_pmxr.getCmUserName())) {
                            bNoAvailableItem = false;
                        }
                    }
                    else {
                        bNoAvailableItem = false;
                        this.dsICM.checkOut((dkDataObject)ddoDelete);
                    }
                    if (!bNoAvailableItem) {
                        ddoDelete.retrieve(26);
                        System.out.println("Set deleted attr to true!");
                        ddoDelete.setData(ddoDelete.dataId("ATTR", "Deleted"), (Object)"true");
					 
                        ddoDelete.update();
                        System.out.println("Update DeletedItems ready to off the process!");
                        routingService.continueProcess(workPackageDelete.getPidString(), "Archive", "ICMADMIN");
	  
                    }
                    if (dsExtICM.isCheckedOut((dkDataObject)ddoDelete)) {
                        this.dsICM.checkIn((dkDataObject)ddoDelete);
                    }
                }
            }
			
																										  
			
            if (workPackages.cardinality() == 0) {
                workPackages = null;
            }
            else {
                String barCode = "";
							   
                final String sql = "";
                String tableName = "";
                final dkIterator iter = workPackages.createIterator();
                workPackages = null;
				
                final Date today = new Date();
				
                String exceptionStepName = "";
                String processName = "";
				
				
                while (iter.more()) {
				 
																  
                    tableName = "";
                    System.out.println("Get item!!");
                    final DKWorkPackageICM workPackage = (DKWorkPackageICM)iter.next();
                    final Timestamp lastMovedTimestamp = (Timestamp)workPackage.getTimeLastMoved();
					
                    final Date lastMovedDate = new Date(lastMovedTimestamp.getTime());
                    final int diffInDays = (int)((today.getTime() - lastMovedTimestamp.getTime()) / 86400000L);
                    System.out.println("DiffInDays and lastMovedTimestamp :" + diffInDays + "&&" + lastMovedTimestamp);
					
                    ddo = this.dsICM.createDDO(workPackage.getItemPidString());
					
                    System.out.println("Check DDO ItemID = " + ddo.getPidObject().getIdString());
					
                    processName = workPackage.getProcessName();
					
																   
                    if (ddo.getObjectType().toString().equalsIgnoreCase("APC")){
                    	connection = connectionCA;
                    	tableName = "ARCPDTA71";
                    	exceptionStepName = "Send To Error";
                    }
                    if (ddo.getObjectType().toString().equalsIgnoreCase("APU")){
                    	connection = connectionUS;
                    	tableName = "HSIPDTA71";
                     
                        if (processName.equalsIgnoreCase("UpdateCM")) {
                            exceptionStepName = "Send To Error";
                        }
                        else {
                            exceptionStepName = "Send Back To ErrorWorklist";
                        }
					 
                    }
                    System.out.println("Item Type: " + ddo.getObjectType().toString() + "--" + tableName);
					
                    boolean bNoAvailableItem2 = true;
                    String checkedOutByStr2 = "";
                    if (dsExtICM.isCheckedOut((dkDataObject)ddo)) {
                        checkedOutByStr2 = dsExtICM.checkedOutUserid((dkDataObject)ddo);
                        if (checkedOutByStr2.equalsIgnoreCase(this.m_pmxr.getCmUserName())) {
                            bNoAvailableItem2 = false;
                        }
                    }
                    else {
                        bNoAvailableItem2 = false;
                        this.dsICM.checkOut((dkDataObject)ddo);
                    }
                    if (!bNoAvailableItem2) {
                        ddo.retrieve(26);

                        barCode = "";
						
                        if (diffInDays < this.m_pmxr.getWaitingTime()) {
																	  
                            if (ddo.getDataByName("BarCode") != null && ddo.getDataByName(this.attribute) == null) {
                                barCode = ddo.getDataByName("BarCode").toString().trim();
					
                                System.out.println("BarCode = " + barCode);
				 
                                ArrayList resultAL = new ArrayList();
                                resultAL = this.getLineAL(barCode, this.connection, tableName);
					
                                if (resultAL.size() > 0) {
                                    try {
				  
                                        if (resultAL.get(0) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "Company"), (Object)resultAL.get(0));
                                        }
                                        if (resultAL.get(1) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "DocumentType"), (Object)resultAL.get(1));
                                        }
                                        if (resultAL.get(2) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "DocumentNumber"), (Object)resultAL.get(2));
                                        }
                                        if (resultAL.get(3) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "GLDate"), (Object)java.sql.Date.valueOf((String) resultAL.get(3)));
                                        }
                                        if (resultAL.get(4) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "BatchNumber"), (Object)resultAL.get(4));
                                        }
                                        if (resultAL.get(5) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "BatchType"), (Object)resultAL.get(5));
                                        }
                                        if (resultAL.get(6) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "BatchDate"), (Object)java.sql.Date.valueOf((String)resultAL.get(6)));
                                        }
                                        if (resultAL.get(7) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "GLPeriodNum"), (Object)resultAL.get(7));
                                        }
                                        if (resultAL.get(8) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "FiscalYear"), (Object)resultAL.get(8));
                                        }
                                        if (resultAL.get(9) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "AddressNum"), (Object)resultAL.get(9));
                                        }
                                        if (resultAL.get(10) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "InvoiceDate"), (Object)java.sql.Date.valueOf((String)resultAL.get(10)));
                                        }
                                        if (resultAL.get(11) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "DueDate"), (Object)java.sql.Date.valueOf((String)resultAL.get(11)));
                                        }
                                        if (resultAL.get(12) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "InvoiceNum"), (Object)resultAL.get(12));
                                        }
                                        if (resultAL.get(13) != null) {
                                            ddo.setData(ddo.dataId("ATTR", "PONumber"), (Object)resultAL.get(13));
                                        }
                                        ddo.update();
		 
                                        System.out.println("Update CM ready to off the process!");
                                        if (processName.equalsIgnoreCase("UpdateCM")) {
                                            routingService.continueProcess(workPackage.getPidString(), "Continue", "ICMADMIN");
                                        }
                                        else {
																							
                                            routingService.continueProcess(workPackage.getPidString(), "Archive", "ICMADMIN");
                                        }
                                    }
                                    catch (DKException exc) {
                                        System.out.println("DKException Error in routing statement " + exc);
                                        routingService.continueProcess(workPackage.getPidString(), exceptionStepName, "ICMADMIN");
                                    }
                                }
                            }
                        }
                        else {
					  
                            System.out.println("Send to " + exceptionStepName + " worklist.");
                            routingService.continueProcess(workPackage.getPidString(), exceptionStepName, "ICMADMIN");
                        }
                    }
                    if (dsExtICM.isCheckedOut((dkDataObject)ddo)) {
                        this.dsICM.checkIn((dkDataObject)ddo);
                    }
                }
            }
			
																		
            if (workPackagesAPEx.cardinality() == 0) {
                workPackagesAPEx = null;
            }
            else {
                String barCode = "";
							   
                final String sql = "";
                String tableName = "";
                final dkIterator iterAPE = workPackagesAPEx.createIterator();
                workPackagesAPEx = null;
                String processName2 = "";
															
				
												
										
				
				
                while (iterAPE.more()) {
				 
																  
                    tableName = "";
                    System.out.println("Get item!!!!!!!!!!!!!");
                    final DKWorkPackageICM workPackageAPE = (DKWorkPackageICM)iterAPE.next();
																					
					
																				  
																												
																												   
					
                    ddoAPE = this.dsICM.createDDO(workPackageAPE.getItemPidString());
					
                    System.out.println("Check ddoAPE ItemID = " + ddoAPE.getPidObject().getIdString());
					
                    processName2 = workPackageAPE.getProcessName();
					
																   
                    if (ddoAPE.getObjectType().toString().equalsIgnoreCase("APC")) {
                        tableName = "ARCPDTA71";
														   
                    }
                    if (ddoAPE.getObjectType().toString().equalsIgnoreCase("APU")) {
                        tableName = "HSIPDTA71";
					 
																   
															
						   
																	   
					 
                    }
                    System.out.println("Item Type: " + ddoAPE.getObjectType().toString() + "--" + tableName);
					
                    boolean bNoAvailableItem3 = true;
                    String checkedOutByStr3 = "";
                    if (dsExtICM.isCheckedOut((dkDataObject)ddoAPE)) {
                        checkedOutByStr3 = dsExtICM.checkedOutUserid((dkDataObject)ddoAPE);
                        if (checkedOutByStr3.equalsIgnoreCase(this.m_pmxr.getCmUserName())) {
                            bNoAvailableItem3 = false;
                        }
                    }
                    else {
                        bNoAvailableItem3 = false;
                        this.dsICM.checkOut((dkDataObject)ddoAPE);
                    }
                    if (!bNoAvailableItem3) {
                        ddoAPE.retrieve(26);

                        barCode = "";
						
																	 
                        if (ddoAPE.getDataByName("BarCode") != null) {
																													
                            barCode = ddoAPE.getDataByName("BarCode").toString().trim();
					
                            System.out.println("BarCode = " + barCode);
				 
                            ArrayList resultAL2 = new ArrayList();
                            resultAL2 = this.getLineAL(barCode, this.connection, tableName);
					
                            if (resultAL2.size() > 0) {
                                try {
				  
                                    if (resultAL2.get(0) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "Company"), (Object)resultAL2.get(0));
                                    }
                                    if (resultAL2.get(1) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "DocumentType"), (Object)resultAL2.get(1));
                                    }
                                    if (resultAL2.get(2) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "DocumentNumber"), (Object)resultAL2.get(2));
                                    }
                                    if (resultAL2.get(3) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "GLDate"), (Object)java.sql.Date.valueOf((String)resultAL2.get(3)));
                                    }
                                    if (resultAL2.get(4) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "BatchNumber"), (Object)resultAL2.get(4));
                                    }
                                    if (resultAL2.get(5) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "BatchType"), (Object)resultAL2.get(5));
                                    }
                                    if (resultAL2.get(6) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "BatchDate"), (Object)java.sql.Date.valueOf((String)resultAL2.get(6)));
                                    }
                                    if (resultAL2.get(7) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "GLPeriodNum"), (Object)resultAL2.get(7));
                                    }
                                    if (resultAL2.get(8) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "FiscalYear"), (Object)resultAL2.get(8));
                                    }
                                    if (resultAL2.get(9) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "AddressNum"), (Object)resultAL2.get(9));
                                    }
                                    if (resultAL2.get(10) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "InvoiceDate"), (Object)java.sql.Date.valueOf((String)resultAL2.get(10)));
                                    }
                                    if (resultAL2.get(11) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "DueDate"), (Object)java.sql.Date.valueOf((String)resultAL2.get(11)));
                                    }
                                    if (resultAL2.get(12) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "InvoiceNum"), (Object)resultAL2.get(12));
                                    }
                                    if (resultAL2.get(13) != null) {
                                        ddoAPE.setData(ddoAPE.dataId("ATTR", "PONumber"), (Object)resultAL2.get(13));
                                    }
                                    ddoAPE.update();
		 
                                    System.out.println("Update APE CM ready to off the process! " + processName2);
                                    if (processName2.equalsIgnoreCase("UpdateCM")) {
                                        System.out.println(" Off the process!");
                                    }
                                    else {
		  
                                        routingService.terminateProcess(workPackageAPE.getPidString());
                                        System.out.println(" Terminate Process !");
																							  
																							  
                                    }
                                }
                                catch (DKException exc2) {
                                    System.out.println("DKException APE Error in routing statement " + exc2);
																											  
                                }
                            }
                        }
				   
																								  
					  
																					
																												
						
                    }
                    if (dsExtICM.isCheckedOut((dkDataObject)ddoAPE)) {
                        this.dsICM.checkIn((dkDataObject)ddoAPE);
                    }
                }
            }
            ddoAPE = null;
            final dkIterator iterAPE = null;
            final DKWorkPackageICM workPackageAPE = null;
		   
																	  
            dsExtICM = null;
            ddo = null;
            final dkIterator iter = null;
            final DKWorkPackageICM workPackage = null;
            routingService = null;
				
        }
        catch (DKException exc3) {
            System.out.println("run: Error in routing statement " + exc3);
																			
            ICMConnectionPool.clearConnections();
            exc3.printStackTrace();
        }
        catch (Exception exc4) {
            System.out.println("run: Error in Exception " + exc4);
																		  
            ICMConnectionPool.clearConnections();
            exc4.printStackTrace();
        }
        finally {
            if (this.connection != null) {
                try {
                    this.connection.close();
                }
                catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
                this.connection = null;
            }

            if (this.dsICM != null) {
                try {
                    ICMConnectionPool.returnConnection((dkDatastore)this.dsICM);
                    this.dsICM = null;
                }
                catch (Exception e) {
                    System.out.println("<Ck update>Error returning connection to the pool." + e.getMessage());
                }
            }
        }
        if (this.connection != null) {
            try {
                this.connection.close();
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }
            this.connection = null;
        }
        if (this.dsICM != null) {
            try {
                ICMConnectionPool.returnConnection((dkDatastore)this.dsICM);
                this.dsICM = null;
            }
            catch (Exception e) {
                System.out.println("<Ck update>Error returning connection to the pool." + e.getMessage());
            }
        }
    }
    
    public String getBaseURL() {
        return this.m_BaseURL;
    }
    
	   
										   
	   
    public void setBaseURL(final String baseURL) {
        this.m_BaseURL = baseURL;
    }
    
    public String convertJulianDate(final String julianDate) {
	 
        final String newDate = julianDate.substring(1, 6);
        String output = null;
        try {
            final DateFormat fmt1 = new SimpleDateFormat("yyDDD");
            final Date date = fmt1.parse(newDate);
            final DateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
            output = fmt2.format(date);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        return output;
    }
    
    public ArrayList getLineAL(final String barCode, final Connection connection, final String tableName) {
        final ArrayList lineAL = new ArrayList();
        System.out.println("After connection & barCode !!" + barCode);
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
												 
            while (rs.next()) {
                count = rs.getInt(1);
            }
            System.out.println("query & Count = " + query + " ++++++ " + count);
            if (count > 0) {
			 
																				
                query = "SELECT GLKCO AS DocumentCompany ,";
                query = String.valueOf(query) + "GLDCT AS DocumentType, ";
                query = String.valueOf(query) + "GLDOC AS DocumentNumber, ";
                query = String.valueOf(query) + "GLDGJ AS GLDate ,";
                query = String.valueOf(query) + "GLICU AS BatchNumber ,";
                query = String.valueOf(query) + "GLICUT AS BatchType, ";
                query = String.valueOf(query) + "GLDICJ AS BatchDate, ";
                query = String.valueOf(query) + "GLPN AS GLPeriodNumber, ";
                query = String.valueOf(query) + "GLFY AS FiscalYear ";
											 
										
											   
									   
                query = String.valueOf(query) + "FROM " + tableName + ".F0911HH where GLR2 ='" + barCode + "' ";
																												 
										 
							 
																				  
																			
																				
																
																		  
																	  
																						 
																				
																		
																		  
																  
																			  
																	
		
																				
		  
													  
											 
												
									   
											
										   
										   
											  
										 
																					
                query = String.valueOf(query) + "FETCH FIRST 1 ROWS ONLY";
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    asDocumentCompany = rs.getString("DocumentCompany");
                    asDocumentType = rs.getString("DocumentType");
                    asDocumentNumber = rs.getString("DocumentNumber");
                    asGLDate = this.convertJulianDate(rs.getString("GLDate"));
                    asBatchNumber = rs.getString("BatchNumber");
                    asBatchType = rs.getString("BatchType");
                    asBatchDate = this.convertJulianDate(rs.getString("BatchDate"));
                    asGLPeriodNumber = rs.getString("GLPeriodNumber");
                    asFiscalYear = rs.getString("FiscalYear");
	 
                    query = "SELECT RPAN8 AS AddressNumber, ";
                    query = String.valueOf(query) + "RPDIVJ AS InvoiceDate, ";
                    query = String.valueOf(query) + "RPDDJ AS DueDate, ";
                    query = String.valueOf(query) + "RPVINV AS InvoiceNumber, ";
                    query = String.valueOf(query) + "RPPO AS PONumber ";
                    query = String.valueOf(query) + "FROM " + tableName + ".F0411LE WHERE RPDCT = '" + asDocumentType + "' AND RPDOC = " + asDocumentNumber + " AND RPKCO = '" + asDocumentCompany + "' FETCH FIRST 1 ROWS ONLY";
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        asAddressNumber = rs.getString("AddressNumber");
                        asInvoiceDate = this.convertJulianDate(rs.getString("InvoiceDate"));
                        asDueDate = this.convertJulianDate(rs.getString("DueDate"));
                        asInvoiceNumber = rs.getString("InvoiceNumber");
                        asPONumber = rs.getString("PONumber");
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
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("run: Error in retreiving sql = " + sqle.getMessage());
		
        }
        catch (Exception ex) {
            System.out.println(" Error finally =" + ex.getMessage());
        }
        finally {
            if (rs != null) {
				
                try {
                    rs.close();
                }
                catch (SQLException ex2) {
                    System.out.println(" Error finally =" + ex2.getMessage());
                }
                rs = null;
            }

            if (stmt != null) {
				
                try {
                    stmt.close();
                }
                catch (SQLException ex2) {
                    System.out.println(" Error finally =" + ex2.getMessage());
                }
                stmt = null;
            }
	 
        }
        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException ex2) {
                System.out.println(" Error finally =" + ex2.getMessage());
            }
            rs = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException ex2) {
                System.out.println(" Error finally =" + ex2.getMessage());
            }
            stmt = null;
        }
        return lineAL;
    }

}
