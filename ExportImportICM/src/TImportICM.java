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
 ******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;

/************************************************************************************************
 *          FILENAME: TImportICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Imports items specified in an Export Package file.
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java TImportICM <options>
 *                    
 *                    Options:  If any of the following are not specified, defaults will
 *                              be used where possible, or the user will be prompted.
 *
 *                             -d/database <you database name>
 *                             -u/user     <CM user id>
 *                             -p/password <CM user's password>
 *                             -o/options  <Connect String Options>
 *                             -f/file     <File Name of Export Package>
 *                             -i/ini      <Alternate Configuration File>
 *                             -r/restart  (+skip) <Import Operation Tracking File>
 *
 *                      Note:  * User will be prompted for 'file' if not specified.
 *                             * Defaults will be used for optional parameters
 *                               'database', 'user', and 'password' if not specified.  
 *                             * Default file name of "TImportExportICM.ini" will be
 *                               used for configuration file name if none is specified. 
 *                             * Configuration file is optional.  If TImportExportICM.ini
 *                               is not found, defaults will be used.
 *
 *                       RESTART CAPABILITY:
 *                               Failed or incomplete past import or restart operations
 *                               may be restarted from where they left off or skip
 *                               the failed/incomplete items.  
 *        
 *                               The same Export Package must be loaded along with the
 *                               tracking file name (*.trk) that was generated from
 *                               that import process, specified by the Tracking File
 *                               Name property.
 *
 *                               Any items marked as completed in each phase of that
 *                               file will not be attempted to be imported.  By default
 *                               all incomplete and items not started for each phase
 *                               of the import process will be restarted in each
 *                               respective phase.
 *
 *                               The "+skip" option (disabled by default) will result
 *                               in omission of all incomplete items which will enable
 *                               those items to be skipped in that phase and all future
 *                               phases in which that item was started by not completed.
 *
 *                               For information on the file syntax, please refer to the
 *                               TExportPackageICM.restartImport() Javadoc on the sample
 *                               tool API.
 *
 *                    Example:
 *                               java TImportICM -f myExporteFiles.xpk
 *
 *                    Document:  For complete information please refer to the
 *                               Sample Import / Export Tool & API document
 *                               --> TExportPackageICM.doc
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: The source and target item types must be identical in names and structure.
 *                    Versioning setting of 'Never' or 'By Application' is recommended.
 *                    Versioning set to 'Always' will not preserve versions correctly and 
 *                    will result in intermediate versions created.
 *
 *                    Please refer to the Sample Import / Export Tool & API document,
 *                    TExportPackageICM.doc, for Requirements & Limitations.
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 *                    TExportPackageICM.java
 *                    SLinksICM.java
 ************************************************************************************************/
public class TImportICM{
    
    // Constants
    public static final String DEFAULT_INI_FILE_NAME = "TImportExportICM.ini";
    
    //-------------------------------------------------------------
    // Main
    //-------------------------------------------------------------
    /**
     * Run the Sample.
     * @param argv[] String Array containing arguments.  Optional arguments are <databse> <userName> <password>.
     */
    public static void main(String argv[]) throws Exception{
    
        // Defaults for connecting to the database.
        String  database = SConnectDisconnectICM.DEFAULT_DATABASE;
        String  userName = SConnectDisconnectICM.DEFAULT_USERNAME;
        String  password = SConnectDisconnectICM.DEFAULT_PASSWORD;
        String  connOpts = "";
        String  xpkFileName = null;
        String  iniFileName = null;
        String  restartTrackingFile = null;
        boolean restartSkipIncompleteEnable = false;
        
        //------------------------------------------------------------
        // Checking for input parameters
        //--------------------------------------------------------------
        for(int i=0; i < argv.length; i++){
            // -d/database <xxxxxxxx>
            if((argv[i].compareToIgnoreCase("-database")==0) ||
               (argv[i].compareToIgnoreCase("-d")==0)){
                if(argv.length < (i+1)) throw new Exception("-d/database option missing database name.");
                database = argv[++i];
            // -u/user <xxxxxxxx>
            }else if((argv[i].compareToIgnoreCase("-user")==0) ||
                    (argv[i].compareToIgnoreCase("-u")==0)){
                if(argv.length < (i+1)) throw new Exception("-u/user option missing user name.");
                userName = argv[++i];
            // -p/password <xxxxxxxx>
            }else if((argv[i].compareToIgnoreCase("-password")==0) ||
                    (argv[i].compareToIgnoreCase("-p")==0)){
                if(argv.length < (i+1)) throw new Exception("-p/password option missing password.");
                password = argv[++i];
            // -o/options <Connect String Options>
            }else if((argv[i].compareToIgnoreCase("-options")==0) ||
                    (argv[i].compareToIgnoreCase("-o")==0)){
                if(argv.length < (i+1)) throw new Exception("-o/options option missing options.");
                connOpts = argv[++i];
            // -f/file <File Name for Export Package>
            }else if((argv[i].compareToIgnoreCase("-file")==0) ||
                    (argv[i].compareToIgnoreCase("-f")==0)){
                if(argv.length < (i+1)) throw new Exception("-f/file option missing export package file name.");
                xpkFileName = argv[++i];
            // -i/ini <Alternate Configuration File>
            }else if((argv[i].compareToIgnoreCase("-ini")==0) ||
                    (argv[i].compareToIgnoreCase("-i")==0)){
                if(argv.length < (i+1)) throw new Exception("-i/ini option missing alternate configuration file name.");
                iniFileName = argv[++i];
            // -r/restart  (+skip) <Import Operation Tracking File>
            }else if((argv[i].compareToIgnoreCase("-restart")==0) ||
                    (argv[i].compareToIgnoreCase("-r")==0)){
                if(argv.length < (i+1)) throw new Exception("-r/restart option missing following parameters.");
                if(argv[i+1].trim().compareToIgnoreCase("+skip")==0){
                    if(argv.length < (i+2)) throw new Exception("-r/restart option missing tracking file name.");
                    restartSkipIncompleteEnable = true;
                    i++; // increment counter to position on "+skip".
                }
                restartTrackingFile = argv[++i];
            }
        }//end for

        String ver = SConnectDisconnectICM.VERSION;

        System.out.println("===========================================");
        System.out.println("IBM DB2 Content Manager                v"+ver);
        System.out.println("Tool Program:  TImportICM");
        System.out.println("-------------------------------------------");
        System.out.println(" Database: "+database);
        System.out.println(" UserName: "+userName);
        System.out.println(" ConnOpts: "+connOpts);
        if(xpkFileName==null) System.out.println("     File: <Prompt User>");
        else                  System.out.println("     File: "+xpkFileName);
        if(iniFileName==null) System.out.println("   Config: <Default = "+DEFAULT_INI_FILE_NAME+">");
        else                  System.out.println("   Config: "+iniFileName);
        if(restartTrackingFile!=null){      System.out.println("     Mode: Restart Enabled -- +Skip Incomplete");
            if(restartSkipIncompleteEnable) System.out.println("     Omit: Skip Incomplete");
            else                            System.out.println("     Omit: None");
        } else                              System.out.println("     Mode: Standard Import");
        System.out.println("===========================================");
        
        try{
            //-------------------------------------------------------------
            // Input Validaiton
            //-------------------------------------------------------------
            validateConfigFileChoice(iniFileName);

            //-------------------------------------------------------------
            // Connect to datastore
            //-------------------------------------------------------------
            // See Sample SConnectDisconnectICM for more information
            System.out.println("Connecting to datastore (Database '"+database+"', UserName '"+userName+"')...");

                DKDatastoreICM dsICM = new DKDatastoreICM();        // Create new datastore object.
                dsICM.connect(database,userName,password,connOpts); // Connect to the datastore.

            System.out.println("Connected to datastore (Database '"+dsICM.datastoreName()+"', UserName '"+dsICM.userName()+"').");

            //-------------------------------------------------------------
            // Import Items
            //-------------------------------------------------------------
            System.out.println("Importing Items...");

                // If filename not specified at command line, prompt user
                if(xpkFileName==null)
                    xpkFileName = promptUser("> Please Enter Central File Name of Export Package: ");

                // Export the Selected Items
                importItems(dsICM,xpkFileName,iniFileName,restartTrackingFile,restartSkipIncompleteEnable);

            System.out.println("Imported Items.");

            //-------------------------------------------------------------
            // Disconnect from datastore & Destroy Reference
            //-------------------------------------------------------------
            // See Sample SConnectDisconnectICM for more information
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
            SConnectDisconnectICM.printException(exc);  // Print the exception using the function listed below.
        } catch (Exception exc) {
            SConnectDisconnectICM.printException(exc);  // Print the exception using the function listed below.
        }
    }// end main

    //=================================================================
    // Tool Functions
    //=================================================================
    // The following are wrapper functions for functionality covered
    // in this sample.  These functions can be used by other samples.

   /**
    * Export items selected by the query string.
    * @param dsICM       - Connected datastore to export from.
    * @param xpkFileName - Central File Name for Export Package.
    * @param iniFileName - (Optional) Configuration File Name, or null to use default.
    * @param restartTrackingFile - Import Operations Tracking File name for restart
    *                              mode or 'null' for standard import mode.
    * @param restartSkipIncompleteEnable - If restart mode enabled (restartTrackingFileName
    *                                      is not 'null'), this determines whether or not
    *                                      to omit failed/incomplete items that were started
    *                                      but did not complete.
    **/
    public static void importItems(DKDatastoreICM dsICM, String xpkFileName, String iniFileName, String restartTrackingFile, boolean restartSkipIncompleteEnable) throws DKException, Exception{

        // Create Options Object from iniFile if found.
        TExportPackageICM.ImportOptions importOptions = null;
        try{ // If not found, use defaults.
            if(iniFileName==null) // If none specified, try default file name.
                importOptions = new TExportPackageICM.ImportOptions(DEFAULT_INI_FILE_NAME);
            else
                importOptions = new TExportPackageICM.ImportOptions(iniFileName);
        } catch( FileNotFoundException exc){
            importOptions = null;  // Defaults will automatically be used.            
        }       

        // Create Export Package Object based on Saved File.
        TExportPackageICM exportPackage = new TExportPackageICM(xpkFileName,importOptions);
        
        // Import items into this datastore.
        // If restart mode, use restart API
        TExportPackageICM.ImportRecord[] importRecords = null;
        if(restartTrackingFile!=null)
            importRecords = exportPackage.restartImport(restartTrackingFile,restartSkipIncompleteEnable,dsICM,importOptions);
        else
            importRecords = exportPackage.importItems(dsICM,importOptions);
        
        // Print Imported Items
        System.out.println("");
        System.out.println("Items Imported:");
        for(int i=0; i<importRecords.length; i++){
            DKPidICM pidICM = (DKPidICM)importRecords[i].getImportedItem().getPidObject();
            System.out.println("     ItemID = '"+pidICM.getItemId()+"' ("+pidICM.getObjectType()+")");
        }
        System.out.println("");
    }

   /**
    * Prompt the user for input.
    * @param messageStr - Message to display to the user.
    * @return Returns input that the user entered.
    **/
    public static String promptUser(String messageStr) throws java.io.IOException{
        
        java.io.InputStreamReader inputStreamReader = new java.io.InputStreamReader(System.in);      // create a new inputsream reader from the system's input stream
        java.io.BufferedReader    commandlineReader = new java.io.BufferedReader(inputStreamReader); // create a buffered reader to read from the command line
        
        // Prompt User
        System.out.println("");
        System.out.println(messageStr);
        
        // Read the input from the user.
        String responseStr = commandlineReader.readLine();
        
        return(responseStr);
    }

    //=================================================================
    // Internal Functions
    //=================================================================

   /**
    * Validate the configuration file chosen.  If user specified an ini file,
    * make sure it exists.  Throw error if not.
    * @param iniFileName - Configuration File Name
    **/
    private static void validateConfigFileChoice(String iniFileName) throws Exception{
        // Determine if user specified a file
        if(iniFileName!=null){
            try{
                File file = new File(iniFileName);
                if(file.exists()==false)
                    throw new FileNotFoundException();
            }catch(java.io.FileNotFoundException exc){
                throw new FileNotFoundException("The configuration file specified, '"+iniFileName+"', was not found.");
            }
        }
    }

}//end class TExportICM
              
