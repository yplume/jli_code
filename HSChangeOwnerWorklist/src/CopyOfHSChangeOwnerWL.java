
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.text.*;
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.DKDatastoreICM;

public class CopyOfHSChangeOwnerWL {

	private String m_serverName;
    private String m_userName;
    private String m_password;
    private String m_OutPutPath;
    private String m_sqlServerDBName;
    private String m_LSServerName;
    
	public CopyOfHSChangeOwnerWL(String lsServerName, String cmServerName, String cmUserName, String cmPassword){
		m_serverName = cmServerName;
        m_userName = cmUserName;
        m_password = cmPassword;
        m_LSServerName = lsServerName;
        //HSChangeOwnerWL();
	}
	public void HSChangeOwnerWL() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        Date date = new Date();  
        
        System.out.println("Calling Monitor Pre My WorkList: " +date);
   	 	monitorPreMyWorkList();
    	
    }
    public void monitorPreMyWorkList(){
    	
    	 DKDatastoreICM dsICM = null;
         String pid = null;

         try {
             dsICM = new DKDatastoreICM();
             dsICM = ICMConnectionPool.getConnection("icmadmin", "BigBlue1", "icmnlsdb");
             System.out.println("After connection pool....");
// 	    	dsICM.connect(m_serverName, m_userName, m_password, "SCHEMA=ICMADMIN");
             DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
             com.ibm.mm.sdk.server.DKDatastoreExtICM dsExtICM = (com.ibm.mm.sdk.server.DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
             //System.out.println("routingService = "+routingService);
             
             //dkCollection workPackages = routingService.listWorkPackages("ICCAApplicationSubmissions", "");
             //dkCollection workPackages = routingService.listWorkPackages("My Worklist", "");
             //dkCollection workPackages = routingService.listWorkPackages("ErrorWorklist", "");
             dkCollection workPackages = routingService.listWorkPackages("Pre My Worklist", "");
             //System.out.println("workPackages = "+workPackages);
             
             DKDDO ddo = null;
             
             //String db2URL = "jdbc:db2://usnymetia3:50000/icmnlsdb";
             String db2URL = "jdbc:db2://"+ m_LSServerName +":50000/icmnlsdb";
             Connection conn = DriverManager.getConnection(db2URL,"icmadmin","bigblue1");
             
             if (workPackages.cardinality() == 0) {
                 workPackages = null;
             } else {
            	 //System.out.println("Number of items in My Worklist -> " + workPackages.cardinality());
            	 System.out.println("Number of items in Pre My Worklist -> " + workPackages.cardinality());
                 dkIterator iter = workPackages.createIterator();
                 DKWorkPackageICM workPackage;
                 String queue = "";
                 
                 String itemID = "";
                 String eventQuery = "";
                 ResultSet rs = null;
                 Statement stmt = null;
                 HashMap hm = new HashMap();
                 
                 short dataId;
                 while (iter.more()) {
                     workPackage = (DKWorkPackageICM) iter.next();
                     pid = workPackage.getPidString();
                     ddo = dsICM.createDDO(workPackage.getItemPidString());
                     itemID = ((DKPidICM)ddo.getPidObject()).getItemId();
                     System.out.println("Pre My Worklist get owner name = "+workPackage.getOwner());
                     
                     //ignor the owner update if owner is 'icmadmin'
                     if(workPackage.getOwner().equalsIgnoreCase("icmadmin")){
	                     //workPackage.setOwner("Test");
	                     //routingService.setWorkPackageOwner(workPackage.getPidString(), "Test");
	                     boolean bNoAvailableItem = true;
	                     boolean exportImage = false;
	                     String checkedOutByStr = "";
	                     String itemType = "";
	                     
	                     
	                     if (dsExtICM.isCheckedOut(ddo)) {
	                         checkedOutByStr = dsExtICM.checkedOutUserid(ddo);
	                         //if (checkedOutByStr.equalsIgnoreCase("icmadmin")) {
	                         //    bNoAvailableItem = false;
	                         //}
	                     } else {
	                         bNoAvailableItem = false;
	                         dsICM.checkOut(ddo);
	                     }
	                     if (bNoAvailableItem == false) {
	                     	itemType = ddo.getObjectType().toString();
	                     	System.out.println("APU itemType = "+ itemType);
	                         
	                     	ddo.retrieve(com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_ATTRONLY | com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_CHILDREN | com.ibm.mm.sdk.common.DKConstant.DK_CM_CHECKOUT);
	                    
	                     	if (ddo.getDataByName("Queue") != null) {
	                     		queue = ddo.getDataByName("Queue").toString().toUpperCase();
	                     		System.out.println("Change owner to: "+queue);
	                     		//routingService.setWorkPackageOwner(workPackage.getPidString(), queue);
	                     		routingService.continueProcess(workPackage.getPidString(), "Continue", queue);
	                     	}
	                         //System.out.println("assignNum before exportImage = " + assignNum + exportImage);
	    
	                     }
	                     
	                     dsICM.checkIn(ddo);
	                 }else{
                    	 System.out.println("Contine with the owner: "+workPackage.getOwner());
                    	 routingService.continueProcess(workPackage.getPidString(), "Continue", workPackage.getOwner());
                     }
                 }//end while
                 ddo = null;
                 workPackage = null;
                 iter = null;
                 routingService = null;
                 System.out.println("<------End while loop.----->");
             }//end else	
         } catch (Exception exc) {
             System.out.println("monitorMyWorkList: Error in Exception " + exc);
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
         }//en
    }
    public static void main(String[]arg){
    	
    	
    	class updateOwners extends TimerTask {
   		 
    		public void run() {
    	 
		        CopyOfHSChangeOwnerWL hs = new CopyOfHSChangeOwnerWL();
    		}
    	}
    	 Timer timer = new Timer();
         timer.schedule(new updateOwners(), 10, 5*60*1000); 
         //timer.schedule(new checkZip(), 0, 60*60*1000); 
    
    }
}
