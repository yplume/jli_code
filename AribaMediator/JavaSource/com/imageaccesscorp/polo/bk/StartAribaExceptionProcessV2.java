/*
 * Created on Mar 15, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.imageaccesscorp.polo;

//import ICMConnectionPool;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.text.*;

import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.DKDatastoreICM;

/**
 * @author jli
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class StartAribaExceptionProcessV2 {

    private String m_BaseURL = "";
    private DKDatastoreICM m_dsICM = null;
    private DKDocRoutingServiceICM m_RoutingService;
    private String m_serverName;
    private String m_userName;
    private String m_password;
    private String m_OutPutPath;
    private String m_sqlServerDBName;
    private String m_LSServerName;
    
    /*public AribaMediator(String cmServerName, String cmUserName, String cmPassword, String SQLServer) {
        this(cmServerName, cmUserName, cmPassword, SQLServer);
    }*/
    
    static public void main(String arg[]) {
    	StartAribaExceptionProcessV2 sae = new StartAribaExceptionProcessV2("usncdvcmls", "icmadmin", "BigBlue1");
    	
    }

    
    public StartAribaExceptionProcessV2(String cmServerName, String cmUserName, String cmPassword) {
        
        m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        
        System.out.println("Calling StartAribaExceptionProcessV2 ...");
    	monitorExportToKofaxWorkList();
    	
    }

    
    public void monitorExportToKofaxWorkList() {
        System.out.println("Start to process Rejected Invoices WorkList ....");

        DKDatastoreICM dsICM = null;
        String pid = null;

        try {
            dsICM = new DKDatastoreICM();
            dsICM = ICMConnectionPool.getConnection(m_userName, m_password, m_serverName);
//	    	dsICM.connect(m_serverName, m_userName, m_password, "SCHEMA=ICMADMIN");
            DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
            com.ibm.mm.sdk.server.DKDatastoreExtICM dsExtICM = (com.ibm.mm.sdk.server.DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
            //System.out.println("routingService = "+routingService);
            
            //dkCollection workPackages = routingService.listWorkPackages("ICCAApplicationSubmissions", "");
            dkCollection workPackages = routingService.listWorkPackages("Rejected Invoices", "");
            //System.out.println("workPackages = "+workPackages);
            
            DKDDO ddo = null;
            
            String db2URL = "jdbc:db2://"+ m_LSServerName +":50000/icmnlsdb";
            Connection conn = DriverManager.getConnection(db2URL,m_userName,m_password);
            
            if (workPackages.cardinality() == 0) {
                workPackages = null;
            } else {
                System.out.println("Number of items in Rejected Invoices -> " + workPackages.cardinality());
                dkIterator iter = workPackages.createIterator();
                DKWorkPackageICM workPackage;
                String itemID = "";
                while (iter.more()) {
                    workPackage = (DKWorkPackageICM) iter.next();
                    pid = workPackage.getPidString();
                    ddo = dsICM.createDDO(workPackage.getItemPidString());
                    itemID = ((DKPidICM)ddo.getPidObject()).getItemId();
                   
                    System.out.println("Export To Kofax itemID = "+itemID);
                    boolean bNoAvailableItem = true;
                    String checkedOutByStr = "";
                    String itemType = "";
                    
                    
                    if (dsExtICM.isCheckedOut(ddo)) {
                        checkedOutByStr = dsExtICM.checkedOutUserid(ddo);
                        if (checkedOutByStr.equalsIgnoreCase(m_userName)) {
                            bNoAvailableItem = false;
                        }
                    } else {
                        bNoAvailableItem = false;
                        dsICM.checkOut(ddo);
                    }
                    
                    System.out.println("Starting new process");
                    
                    routingService.startProcess("AribaExceptionProcessV2", workPackage.getItemPidString(), 100, "icmadmin");
                    System.out.println("Terminate the v1 process.");
                    
                    routingService.terminateProcess(workPackage.getPidString());
                    
                }//end while
                ddo = null;
                workPackage = null;
                iter = null;
                routingService = null;
                System.out.println("<------End while loop.----->");
            }//end else	
        } catch (Exception exc) {
            System.out.println("monitorExportToKofaxWorkList: Error in Exception " + exc);
            ICMConnectionPool.clearConnections();
            exc.printStackTrace();
        } finally {
            if (dsICM != null) {
                try {
                    ICMConnectionPool.returnConnection(dsICM);
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("Error returning connection to the pool." + e.getMessage());
                }
            }
        }//end final
    } //end monitorNewWorkList	
    
    
}
