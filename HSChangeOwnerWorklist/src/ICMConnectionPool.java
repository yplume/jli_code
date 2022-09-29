
//import org.apache.log4j.Logger;

import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;

/**
* ICM connection pooling.
*/
public class ICMConnectionPool implements DKConstantICM {  
// Note we have a global, private, static pool named connectionPool 
private static DKDatastorePool connectionPool;
//private static Logger logger = Logger.getLogger(ICMConnectionPool.class.getName());

/**
 * Obtains a connection from the connection pool.  If a connection does
 * not exist, a new one is established.
 */
public static synchronized DKDatastoreICM getConnection(String userId, String password, String server)
  throws IllegalAccessException, InstantiationException, DKException, Exception {
  
  // Create a String (fullClassName) and set it to the ICM datastore class name
  // $$$
  String fullClassName = "com.ibm.mm.sdk.server.DKDatastoreICM";
  if (connectionPool == null) {
System.out.println("ICMConnectionPool: getting new connection");
//  	logger.debug("Setting new Connection Pool");
    
    // Set connectionPool to a new DKDatastorePool using fullClassName
    connectionPool = new DKDatastorePool(fullClassName);
    connectionPool.setDatastoreName(server);
//   connectionPool.setConnectString("");
    connectionPool.setMaxPoolSize(130);
    connectionPool.setMinPoolSize(1);
    connectionPool.initConnections(userId, password, 1);
    connectionPool.setTimeOut(3);
  } 
  if (connectionPool.getDatastoreName().equals(""))
    connectionPool.setDatastoreName(server);
  
  DKDatastoreICM connection =
    (DKDatastoreICM) connectionPool.getConnection(userId, password);
//  logger.debug(userId + " Connection to Datastore " + server + " returned");

  return connection;
}

/**
 * Returns a connection to the connection pool.
 */
public static synchronized void returnConnection(dkDatastore connection)
  throws Exception {
  connectionPool.returnConnection(connection);
//  logger.debug("Connection to Datastore dismissed");
}
public static synchronized void clearConnections() {
  try {
    connectionPool.clearConnections();
  } catch (Exception exc) {
  //	logger.error("Failed to clear connection in Connection Pool", exc);
  }
}

public static synchronized void destroyConnections() {
  try { 
  	if (connectionPool != null) {
    connectionPool.destroy();
    connectionPool = null;
  	}
  } catch (Exception exc) {
  //	logger.error("Failed to destroy connection in Connection Pool", exc);
  }
}
}