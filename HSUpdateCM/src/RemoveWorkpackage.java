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


public class RemoveWorkpackage {
	
	private DKDatastoreICM dsICM = null;
	
	public RemoveWorkpackage(){
		RemoveWorkpackage1();
	}
	
	public void RemoveWorkpackage1(){
		System.out.println("Calling RemoveWorkpackage ...");
		   try{
		   String timeStamp = null;
		   dsICM = new DKDatastoreICM();
		   System.out.println("After init DKDatastore!");
		   dsICM = ICMConnectionPool.getConnection("icmadmin",
		"BigBlue1", "icmnlsdb");
		   System.out.println("After get CM conntion!");
		   DKDocRoutingServiceICM routingService = new
		DKDocRoutingServiceICM(dsICM);

		   DKNVPair options[] = new DKNVPair[3];
		   //options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "200"); // No Maximum(Default)
		   options[0] = new	DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum(Default)
		   //options[1] = new	DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new	Integer(DKConstant.DK_CM_CONTENT_YES));
		   options[1] = new	DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new	Integer(DKConstant.DK_CM_CONTENT_IDONLY));
		   //options[1] = new	DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new	Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
		   options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,null);

		   //String query = "/APU/REFERENCEDBY/@REFERENCER =>WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME =\"UpdateCM\"]/@ITEMID]";
		   //String query = "/APU[@SYSROOTATTRS.CREATETS BETWEEN \"2014-07-01-00.00.00.000001\" AND \"2018-11-24-00.00.00.000001\"]/REFERENCEDBY/@REFERENCER =>WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME =\"UpdateCM\"]/@ITEMID]";
		   String query = "/APC[@SYSROOTATTRS.CREATETS BETWEEN \"2014-07-01-00.00.00.000001\" AND \"2018-11-24-00.00.00.000001\"]/REFERENCEDBY/@REFERENCER =>WORKPACKAGE[@PROCESSITEMID = /ROUTINGPROCESS[@PROCESSNAME =\"UpdateCM\"]/@ITEMID]";
		   
		   System.out.println("Query is : " + query);
		   timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		   System.out.println("before query timestamp: " + timeStamp);
		   dkResultSetCursor cursor = dsICM.execute(query,DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
		   timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		   System.out.println("after query timestamp: " + timeStamp);
		   long count = dsICM.executeCount(query,DKConstantICM.DK_CM_XQPE_QL_TYPE, options);

		   System.out.println("Query executed. Total workpackagesare " + count);
		   //DKDocRoutingServiceICM routingService = new	DKDocRoutingServiceICM(dsICM); // Obtain the core routing serviceobject.
		   DKDDO ddo = null;
	 	   int processedDoc = 0;
		   int processMax = 100000;
		  
		   //while (((ddo = cursor.fetchNext()) != null) & processedDoc	< processMax )
		   while (((ddo = cursor.fetchNext()) != null))
		   {
		     System.out.println("Fetch ddo .....");

		//ddo.retrieve(com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_ATTRONLY	| com.ibm.mm.sdk.common.DKConstant.DK_CM_CONTENT_CHILDREN |	com.ibm.mm.sdk.common.DKConstant.DK_CM_CHECKOUT);

		   timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		   System.out.println("after fetch timestamp: " + timeStamp);

		      String wpPidStr =((DKPid)ddo.getPidObject()).pidString();

		      System.out.println("get wpPidStr : " +wpPidStr);

		      if (wpPidStr != null)
		      {
		       timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		        System.out.println("before terminate process timestamp: " + timeStamp);
		        routingService.terminateProcess(wpPidStr);
		       timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		        System.out.println("after terminate process timestamp: " + timeStamp);

		        System.out.println("Remove item fromWorkflow..."+processedDoc);

		      }     //}
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
		RemoveWorkpackage rw = new RemoveWorkpackage();
		
	}
}
