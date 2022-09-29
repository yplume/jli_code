import com.ibm.mm.sdk.common.DKConstantICM;
import com.ibm.mm.sdk.common.DKDatastorePool;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.dkDatastore;
import com.ibm.mm.sdk.server.DKDatastoreICM;

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
  public static synchronized DKDatastoreICM getConnection(String userId, String password, String server)
    throws IllegalAccessException, InstantiationException, DKException, Exception {
    
    // Create a String (fullClassName) and set it to the ICM datastore class name
    // $$$
    String fullClassName = "com.ibm.mm.sdk.server.DKDatastoreICM";
    if (connectionPool == null) {
    	System.out.println("ICMConnectionPool: getting new connection");

      
      // Set connectionPool to a new DKDatastorePool using fullClassName
      connectionPool = new DKDatastorePool(fullClassName);
      connectionPool.setDatastoreName(server);
      connectionPool.initConnections(userId, password, 0);
      connectionPool.setTimeOut(2);
    } 
    if (connectionPool.getDatastoreName().equals(""))
     connectionPool.setDatastoreName(server);
    
    DKDatastoreICM connection =
      (DKDatastoreICM) connectionPool.getConnection(userId, password);

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
    }
  }
  
public static synchronized void destroyConnections() {
    try { 
    	if (connectionPool != null) {
      connectionPool.destroy();
      connectionPool = null;
    	}
    } catch (Exception exc) {
    }
  }
}