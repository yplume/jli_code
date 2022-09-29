package com.imageaccesscorp.polo.prlreport;

import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;
import java.lang.reflect.*;
import javax.servlet.http.*;

/**
 * ICM connection pooling.
 */
public class ICMConnectionPool implements DKConstantICM {  
  // Note we have a global, private, static pool named connectionPool 
  private static DKDatastorePool connectionPool;

  /**
   * Obtains a connection from the connection pool.  If a connection does
   * not exist, a new one is established.
   */
  public static synchronized DKDatastoreICM getConnection(HttpServletRequest request, HttpServletResponse response)
    throws IllegalAccessException, InstantiationException, DKException, Exception {
    
    // Create a String (fullClassName) and set it to the ICM datastore class name
    // $$$
    String fullClassName = "com.ibm.mm.sdk.server.DKDatastoreICM";
    HttpSession session = request.getSession(false);
	if(session == null){
		System.out.println("****Idle user****"); 
		response.sendRedirect(response.encodeRedirectURL("Login?Error=Account has been idle"));
//		return null;
	}
    
    String server = (String) session.getAttribute("server");
    String userid = (String) session.getAttribute("userid");
    String password = (String) session.getAttribute("password");
    System.out.println("[ICMConnectionPool] Connection and server name ... "+server+userid+password);    	

    if (connectionPool == null) {
      // Set connectionPool to a new DKDatastorePool using fullClassName
      connectionPool = new DKDatastorePool(fullClassName);
      connectionPool.setDatastoreName(server);
      connectionPool.setConnectString("");
      connectionPool.setMinPoolSize(3);
      connectionPool.setMaxPoolSize(200);
      //connectionPool.setMinAndMaxPoolSize(3, 300);
      connectionPool.initConnections(userid, password, 1);
      connectionPool.setTimeOut(15);
      connectionPool.setReclaimUnusedConnection(true);  
      //connectionPool.setStateTraceLevel(DKDatastorePool.DKPoolTraceLevel.ERROR);
      System.out.println("[ICMConnectionPool] setReclaimUnusedConnection and server name ... "+server);    	
    } 
    if (connectionPool.getDatastoreName().equals("")){
    	connectionPool.setDatastoreName(server);
    	System.out.println(userid + " [ICMConnectionPool] datastore empty ...");    	
    }
    DKDatastoreICM connection =  (DKDatastoreICM) connectionPool.getConnection(userid, password);
System.out.println(userid + " [ICMConnectionPool] get connection " + connection);
//01/20/2005 Handle memory better
	session = null;
	server = null;
	userid = null;
	password = null;
	fullClassName = null;
    return connection;
  }

  /**
   * Returns a connection to the connection pool.
   */
  public static synchronized void returnConnection(dkDatastore connection)
    throws Exception {
    connectionPool.returnConnection(connection);

  }
  public static synchronized void clearConnections() {
    try {
      connectionPool.clearConnections();
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }
  
public static synchronized void destroyConnections() {
    try { 
    	if (connectionPool != null) {
      connectionPool.destroy();
      connectionPool = null;
    	}
    } catch (Exception exc) {
      exc.printStackTrace();
    }
  }
}