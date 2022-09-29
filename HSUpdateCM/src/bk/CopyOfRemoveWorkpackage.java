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
import com.ibm.mm.sdk.common.DKWorkPackageICM;
import com.ibm.mm.sdk.common.dkCollection;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.*;
import com.ibm.mm.sdk.common.*;
import java.io.BufferedWriter;


public class CopyOfRemoveWorkpackage {
	
	private DKDatastoreICM dsICM = null;
	
	public CopyOfRemoveWorkpackage(){
		RemoveWorkpackage1();
	}
	
	public void RemoveWorkpackage1(){
		System.out.println("Calling RemoveWorkpackage ...");
		try{
		dsICM = new DKDatastoreICM();
	    System.out.println("After init DKDatastore!");
	    dsICM = ICMConnectionPool.getConnection("icmadmin", "BigBlue1", "icmnlsdb");
	    System.out.println("After get CM conntion!");
	    DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
		
		DKNVPair options[] = new DKNVPair[3];
		//options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "200"); // No Maximum (Default) 
		options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default) 
		//options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_YES)); 
		options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_NO)); 
		//options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY)); 
		options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,null); 
	
		String query = "/APU/REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]";
		//String query = "/APU[@VERSIONID = latest-version(.) AND @SEMANTICTYPE = 1 AND REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]]";
		     //String query = "/APU[[@ITEMID BETWEEN \"A1001001A18J24B20119J00000\" AND \"A1001001A18J24B20618A19998\"] AND /APU/REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]]";
		//String query = "/APU[@BarCode = \"20000001\" AND REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]]" ;
		//String query = "/APU[(@ITEMID BETWEEN \"A1001001A18J24B20119J00000\" AND \"A1001001A18K04A10754E16674\") AND REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]]" ;
		//String query = "/APU[@BarCode LIKE \"300%\" AND @VERSIONID = latest-version(.) AND @SEMANTICTYPE = 1 AND REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME =  \"UpdateCM\"]/@ITEMID]]";
		     //String query = "/APU[(@BarCode LIKE \"300%\") AND REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]]" ;
		//String query = "/APU[ REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]]" ;
				//"AND ./REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]]";
		 
		//String query = "/APU/REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID]";
		//String query = "/APU[@VERSIONID = latest-version(.) AND @SEMANTICTYPE = 1 AND REFERENCEDBY/@REFERENCER => WORKPACKAGE[@PROCESSITEMID =/ROUTINGPROCESS[@PROCESSNAME = \"UpdateCM\"]/@ITEMID AND ../@WORKNODENAME = \"WaitingForUpdateWorkflow\"]]";
		System.out.println("Query is : " + query);
		dkResultSetCursor cursor = dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
		//long count = dsICM.executeCount(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
	
		//System.out.println("Query executed. Total workpackages are " + count);
		//DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM); // Obtain the core routing service object.
		DKDDO ddo = null;
		int processedDoc = 0;
		int processMax = 1000000;
		String company = "";
		//BufferedWriter bufferedWriter = null;
		//bufferedWriter.write("Starting to process max of " + processMax + " work packages in worklist  WaitingForUpdateWorkflow");
		//bufferedWriter.newLine();
		//bufferedWriter.flush();
	
		//while (((ddo = cursor.fetchNext()) != null)) 
		while (((ddo = cursor.fetchNext()) != null) & processedDoc < processMax ) 
		{ 
			System.out.println("Fetch ddo ....");
			//ddo.retrieve(com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_ATTRONLY | com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_CHILDREN | com.ibm.mm.sdk.common.DKConstant.DK_CM_CHECKOUT);
			//if (ddo.getDataByName("Step") != null) {
				//company = ddo.getDataByName("Company").toString();
				//System.out.println("company = "+company);
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
	        Date date = new Date();  
	        
	        System.out.println("get next one : " +date);
	   	 	
				String wpPidStr = ((DKPid)ddo.getPidObject()).pidString();
				
				System.out.println("get wpPidStr : " +wpPidStr);
				
				if (wpPidStr != null)
				{ 
					routingService.terminateProcess(wpPidStr); 
					
					System.out.println("Remove item from Workflow..."+processedDoc);
					//bufferedWriter.write("Processed : " + wpPidStr );
					//bufferedWriter.newLine();
					//bufferedWriter.flush();
				} // end of if pid null check.
			//}
			processedDoc++;
			
		} // end of while loop
	
		cursor.close();
		cursor.destroy();
		
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
      }
	}
	public static void main(String [] arg){
		CopyOfRemoveWorkpackage rw = new CopyOfRemoveWorkpackage();
		
	}
}
