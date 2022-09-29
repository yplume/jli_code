/*BEGINPROLOGUE****************************************************************
 * @copyright(disclaimer)                                                     *
 *                                                                            *
 * DISCLAIMER OF WARRANTIES.                                                  *
 *                                                                            *
 * The following IBM Content Manager Enterprise Edition code is sample code   *
 * created by IBM Corporation. IBM grants you a nonexclusive copyright        *
 * license to use this sample code example to generate similar function       *
 * tailored to your own specific needs. This sample code is not part of any   *
 * standard IBM product and is provided to you solely for the purpose of      *
 * assisting you in the development of your applications. This example has    *
 * not been thoroughly tested under all conditions. IBM, therefore cannot     *
 * guarantee nor may you imply reliability, serviceability, or function of    *
 * these programs. The code is provided "AS IS", without warranty of any      *
 * kind. IBM shall not be liable for any damages arising out of your or any   *
 * other parties use of the sample code, even if IBM has been advised of the  *
 * possibility of such damages. If you do not agree with these terms, do not  *
 * use the sample code.                                                       *
 *                                                                            *
 * Licensed Materials - Property of IBM                                       *
 * 5724-B19, 5697-H60                                                         *
 * © Copyright IBM Corp. 1994, 2013 All Rights Reserved.                      *
 *                                                                            *
 * US Government Users Restricted Rights - Use, duplication or disclosure     *
 * restricted by GSA ADP Schedule Contract with IBM Corp.                     *
 *                                                                            *
 * @endCopyright                                                              *
 ******************************************************************************

Connecting to a Datastore
    The first step in using the IBM DB2 Content Manager Version 8 APIs is to
    establish a connection with the datastore.  The DKDatastoreICM.connect()
    method takes up to four pieces of information.  The most important, and
    most often used are the first three, Database Name, User Name, and
    Password.  The fourth parameter, connection options, can be left blank.
    Please refer to the "Connection Options" section for more information.
    
Disconnecting from the Datastore
    When disconnecting from the datastore, use the disconnect() and destroy()
    methods.  The destroy() methods become important especially dealing with
    Remote, Client, & CS Packages.

Error Handling
    The program model below demonstrates how to properly catch & print Errors.
    The most important exception to catch is the DKException class.  In Java,
    java.lang.Exception must also be handled.  
    
    IMPORTANT NOTE:
        Exceptions should not be used for program logic.  User programs should
        not rely on catching exceptions to detect if something exists in the
        datastore or not, or for any case other than truly exceptional cases.
        If a program is written correctly, exceptions should never be thrown.  
        
        Using exceptions in program logic will decrease performance and can
        render trace & log information useless for debugging & support.
    
    Please examine all of the exception information, which includes up to six
    key pieces of information.  Print functions are available in this sample
    to help print the most important information.
    
    There are numerous sub-classes of DKException.  Depending on the program,
    it may be best to handle each or some exceptions individually.

    DKException 
    Information  Description
    -----------  -----------------------------------------------------
        Name     Exception Class Name.  Will contain sub-class name.
      Message    A specific message wlll explain the error.  The 
                 message may contain a great deal of information at
                 times, sometimes encapsulating important variable
                 states at the time the error was detected.
    Message ID   A unique Message ID identifies this error type and
                 matches it to a core message used above.
    Error State  May contain additional error information regarding
                 the state of the OOAPI or Library Server Error. 
                 In the case when Library Server detects an error,
                 the following four pieces of information are packaged
                 here
                        Library Server Failure State
                        ----------------------------
                           -  Return Code  
                           -  Reason Code
                           -  Ext / SQL Return Code
                           -  Ext / SQL Reason Code
    Error Code   May contain the Library Server Return Code.               
    Stack Trace  A very important piece of information indicating the
                 failure point in the user program and exactly where
                 the error was last detected or handled by the API. 
    ------------------------------------------------------------------

Connection Options
    The 4th parameter of the DKDatastoreICM.connect() method, the connection
    string, is an optional parameter will be left blank in almost all cases.
    The information accepted here is typically created, gathered, and recorded
    during installation.  Configuration (.ini) files are used to access this
    information when needed instead of requiring it specified for every
    connection.  This option is available for programmatic changes to the 
    recorded information for a specific connection, when the recorded
    information is missing or not correct for your connection, or for
    additional connection-specific features.  This allows you to programatically
    override INI file settings for a specific connection.  If two applications
    share the same INI files and one prefers to use different values, it can
    override the settings for its own connections.
    
    An example use of this parameter would be to set the DB/SQL User Name &
    Password if it is both different from the DB2 Content Manager User Name
    & Password and was not specified when requested during install or
    has changed since without updating the product setup.

    The most common value for this option is just an empty string, "", if
    everything was set up & configured correctly.  The second-most common
    value, and by far the most common non-empty string value, is the database
    schema, needed when connecting to a remote DB2 Content Manager server
    if it was manually configured and not all settings are available in the
    property files.  The typical value in this case is the default DB2
    Content Manager administrator user name, "SCHEMA=ICMADMIN".

    Again, in almost all circumstances, it is expected that "" be used as the
    value for this parameter.

    Format:
        Specify the options separated by semicolons (;).  

                 Options   Values                     Description
        ----------------   -------------------------  -----------------------------------
                  SQLUID = <DB User Name>             Database / SQL User Name or ID
                  SQLPWD = <DB User Name's Password>  Database / SQL User Name's Password
                    NPWD = <New Password>             New DB2 Content Manager Password
                  SCHEMA = <DB Schema>                The underlying database tables are
                                                      created under a schema.  Tables are
                                                      qualified with the correct schema.
                                                      This is typically the first DB2 
                                                      Content Manager admininstrator user
                                                      name, "ICMADMIN".
                    LANG = <Language Code to Use>     Language Code to Use
        TRACESERVERLEVEL = <LS Trace Level>           library server logging level
                  DBAUTH = <"CLIENT" or "SERVER">     Turns on or off DB2 Authentication.
                LANCACHE = <"YES" or "NO">            Turn Lan Caching On, which allows 
                                                      caching of Resources in the default 
                                                      Resource Manager for objects stored 
                                                      on other Resource Managers.
               JDBCDRIVER = <JDBC Driver Class Name>  The name of JDBC driver class to use with
                                                      this connection to the library server
                                                      database.  The default driver for DB2 UDB
                                                      is com.ibm.db2.jcc.DB2Driver used in type 4
                                                      mode (when all port information available
                                                      in the INI files), except for an iSeries
                                                      library server which uses type 2 mode with
                                                      com.ibm.db2.jdbc.app.DB2Driver instead.
                                                      You must specify both JDBCDRIVER and
                                                      JDBCURL options if submitting either option
                                                      or else incompatible settings are used.
                                                      This option is currently supported for DB2
                                                      databases only.
                  JDBCURL = <Driver-Specific URL>     The database URL for the desired library
                                                      server database following the correct format
                                                      specific to the JDBC driver you have chosen.
                                                      By default, the APIs build the JDBC URL
                                                      automatically using settings ICMREMOTEDB,
                                                      ICMHOSTNAME, and ICMPORT in cmicmsrvs.ini.
                                                      If all settings are available, type 4 mode
                                                      is used or if not, type 2 mode is used.
                                                      You must specify both JDBCDRIVER and
                                                      JDBCURL options if submitting either option
                                                      or else incompatible settings are used.
                                                      This option is currently supported for DB2
                                                      databases only.
*******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;

/************************************************************************************************
 *          FILENAME: SConnectDisconnectICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Connect, Disconnect, Error Handling, Printing Complete Error Information. 
 *                    ---------------------------------------------------------------------------
 *     DEMONSTRATION: Connect
 *                    Disconnect
 *                    Error Handling, Catching
 *                    Printing Complete Error Messages
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java SConnectDisconnectICM <database> <userName> <password>
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: None
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: None
 ************************************************************************************************/
public class SConnectDisconnectICM{
    
    // Public Constants for Defaults Used by Samples
    public static String DEFAULT_DATABASE = "icmnlsdb";
    public static String DEFAULT_USERNAME = "icmadmin";
    public static String DEFAULT_PASSWORD = "BigBlue1";

    public static String VERSION          = DKConstantICM.DK_ICM_RELEASE_VERSION;

    // Notify Samples if Text Search is enabled.  Set to 'true' if the text search
    // engine is installed and the datastore enabled for TIE/NSE text search.
    // For more information, refer to the SItemTypeCreationICM and SSearchICM samples.
    public static boolean TEXT_SEARCH_ENABLED = false;

    //-------------------------------------------------------------
    // Main
    //-------------------------------------------------------------
    /**
     * Run the Sample.
     * @param argv[] String Array containing arguments.  Optional arguments are <databse> <userName> <password>.
     */
    public static void main(String argv[]) throws DKException, Exception{
    
        // Defaults for connecting to the database.
        String database = DEFAULT_DATABASE;
        String userName = DEFAULT_USERNAME;
        String password = DEFAULT_PASSWORD;
        
        //------------------------------------------------------------
        // Checking for input parameters
        //--------------------------------------------------------------
        if (argv.length < 3) { // if not all 3 arguments were specified, use defaults and report correct usage.
            System.out.println("Usage: " );
            System.out.println("  java SConnectDisconnectICM <database> <userName> <password>" );
            System.out.println("  *** Some parameters not specified, using defaults..." );
            System.out.println("");
        } else {  // otherwise enough parameters were specified, use the first 3 for the 3 parameters.
            if (argv.length > 0) database = argv[0];
            if (argv.length > 1) userName = argv[1];
            if (argv.length > 2) password = argv[2];
        }//end else
        String ver = SConnectDisconnectICM.VERSION;

        System.out.println("===========================================");
        System.out.println("IBM DB2 Content Manager                v"+ver);
        System.out.println("Sample Program:  SConnectDisconnectICM");
        System.out.println("-------------------------------------------");
        System.out.println(" Database: "+database);
        System.out.println(" UserName: "+userName);
        System.out.println("===========================================");
        
        try{
            //-------------------------------------------------------------
            // Connect to datastore
            //-------------------------------------------------------------
            // Note: The 4th paramater of the connect method can be left
            //       blank in almost all circumstances.  The system can
            //       determine this information through other means.  
            //       Options include DB User Name & Password, 
            //       New DB2 Content Manager Password, DB Schema,
            //       Language Code, Trace Level, DB Authentication, and
            //       Lan Caching.  Please consult the Application
            //       Programming Guide (APG), Application Programming
            //       Reference (APR), and Web Based Technical Support
            //       (WBTS) for more information.  More details are
            //       provided in the header documentation of this sample.
            //-------------------------------------------------------------
            System.out.println("Connecting to datastore (Database '"+database+"', UserName '"+userName+"')...");

                DKDatastoreICM dsICM = new DKDatastoreICM();  // Create new datastore object.
                dsICM.connect(database,userName,password,""); // Connect to the datastore.

            System.out.println("Connected to datastore (Database '"+dsICM.datastoreName()+"', UserName '"+dsICM.userName()+"').");

            //-------------------------------------------------------------
            // Disconnect from datastore & Destroy Reference
            //-------------------------------------------------------------
            System.out.println("Disconnecting from datastore & destroying reference...");            

                dsICM.disconnect();
                dsICM.destroy();

            System.out.println("Disconnected from datastore & destroying reference.");
        
            //-------------------------------------------------------------
            // Sample program completed without exception
            //-------------------------------------------------------------
            System.out.println("\n==========================================");
            System.out.println("Sample program completed.");
            System.out.println("==========================================\n");
        }
        //------------------------------------------------------------
        // Catch & Print Exceptions        
        //------------------------------------------------------------
        catch (DKException exc){
            printException(exc);  // Print the exception using the function listed below.
            throw(exc);
        } catch (Exception exc) {
            printException(exc);  // Print the exception using the function listed below.
            throw(exc);
        }
    }// end main

    //******************************************************************
    // printException
    //******************************************************************
    /**
    * Prints the specified exception.
    * @param exc  DKException to print.
    */
    public static void printException(DKException exc){
        System.out.println("");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println("X     !!! Exception !!!    X");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println("       Name: " + exc.name());
        System.out.println("    Message: " + exc.getMessage());
        System.out.println(" Message ID: " + exc.getErrorId());
        System.out.println("Error State: " + exc.errorState());
        System.out.println(" Error Code: " + exc.errorCode());
        exc.printStackTrace();
        System.out.println("----------------------------------");
    }
    /**
    * Prints the specified exception.
    * @param exc  Exception to print.
    */
    public static void printException(Exception exc){
        System.out.println("");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println("X     !!! Exception !!!    X");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println("    Name: " + exc.getClass().getName());
        System.out.println(" Message: " + exc.getMessage());
        exc.printStackTrace();
        System.out.println("----------------------------------");
    }

    //=================================================================
    // Wrapper Functions
    //=================================================================
    // The following are wrapper functions for functionality covered
    // in this sample.  These functions can be used by other samples.

   /** 
    * Connect to DB2 Content Manager datastore.
    * @param database  Name of the DB2 Content Manager database.
    * @param userName  DB2 Content Manager User Name.
    * @param password  Password for the specified User Name.
    * @param options   Leave blank, "", in almost all circumstances. 
    *                  The system can determine this information through 
    *                  other means.  Options include 
    *                  DB User Name & Password, new DB2 Content Manager
    *                  Password, DB Schema, Language Code, Trace Level,
    *                  DB Authentication, and Lan Caching.  Please
    *                  consult the Application Programming Guide
    *                  (APG), Application Programming Reference (APR),
    *                  and Web Based Technical Support (WBTS) for more
    *                  information.  Many more details are provided
    *                  in the header documentation of this sample.
    * @return  Returns a connected DKDatastoreICM object.
    **/
    public static DKDatastoreICM connect(String database, String userName, String password, String connectionOptions) throws DKException, Exception{
        System.out.println("Connecting to datastore (Database '"+database+"', UserName '"+userName+"')...");

            DKDatastoreICM dsICM = new DKDatastoreICM(); // Create new datastore object.
            dsICM.connect(database,userName,password,connectionOptions); // Connect to the datastore.
            // Note: In almost all cases, connectionOptions = "".

        System.out.println("Connected to datastore (Database '"+dsICM.datastoreName()+"', UserName '"+dsICM.userName()+"').");
        return(dsICM);
    }

   /**
    * Disconnect from datastore & destroy reference
    * @param datastore     Connected datastore to disconnect from and destroy reference.
    **/
    public static void disconnect(dkDatastore datastore) throws DKException, Exception{
        System.out.println("Disconnecting from datastore & destroying reference...");            

            datastore.disconnect();
            datastore.destroy();

        System.out.println("Disconnected from datastore & destroying reference.");
    }

    
}//end class SConnectDisconnectICM
              
