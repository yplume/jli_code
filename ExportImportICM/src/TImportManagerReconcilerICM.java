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

 Completion Marker
     Overview
         For an overview and detailed description, refer to the "Reconciler"
         section in the header documentation of the TExportManagerICM.java
         sample file.
     
     Configuration
         For configuration documentation, refer to the TImportExportICM.ini
         default configuration file and the TExportManagerICM.java header
         documentation.

         For a brief description and command line usage, refer to the next
         comment block below, just above the main method definition.

     LIMITATIONS
         Refer to the TExportManagerICM.java header documentation for
         limitations for this tool as part of the overview.

 ******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;
import java.util.*;
import java.util.TreeMap;
import java.util.TreeSet;
import java.sql.Timestamp;

/************************************************************************************************
 *          FILENAME: TImportManagerReconcilerICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: After the import process has completed using Import Manager, given
 *                    the complete Master Imported Item Mapping file and Master Export
 *                    Package file, this tool will re-examine the original system based on
 *                    the original selection criteria and compare against the list of
 *                    items successfully imported.  Any items not found in the successfully
 *                    imported item list, such as any new items on the original system or
 *                    those that failed import, will be identified.  The identified "delta"
 *                    will be written to the Reconciler Summary file that can be used with
 *                    Export Manager to export the list of items as a new Master Package.
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java TImportManagerReconcilerICM <options>
 *                    
 *                    Options:  If any of the following are not specified, defaults will
 *                              be used where possible, or the user will be prompted.
 *
 *                             -d/database <you database name>
 *                             -u/user     <CM user id>
 *                             -p/password <CM user's password>
 *                             -o/options  <Connect String Options>
 *                             -i/ini      <Alternate Configuration File>
 *                             -m/master   <Master Package Name> (Name of master package exported from Export Manager.  This is the the base name of the master directories in storage locations, master file (summary), and tracking file.)
 *                             -l/log      <Folder -- Absolute Path to Master Log File Folder> (Location guaranteed to have enough space through the whole process during import, writing tracking during progress and summary file after completion.)
 *
 *                     Notes:  * Defaults will be used for optional parameters 
 *                               'database', 'user', and 'password' if not specified.  
 *                             * Default file name of "TImportExportICM.ini" will be
 *                               used for configuration file name if none is specified. 
 *                             * Configuration file is optional.  If TImportExportICM.ini
 *                               is not found, defaults will be used.
 *
 *                    Example:
 *                             java TImportManagerReconcilerICM -d icmnlsdb -u icmadmin -p password
 *
 *                    Document:  n/a
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: A complete Master Imported Item Mapping (output by Import Manager)
 *                    and Master Package File (output by Export Manager) must exist in
 *                    the specified Master Log File Directory.
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 ************************************************************************************************/
public class TImportManagerReconcilerICM{

    // Enumaration Constants

    // Default Settings
    private static final String  DEFAULT_DATABASE                     = SConnectDisconnectICM.DEFAULT_DATABASE;
    private static final String  DEFAULT_USERNAME                     = SConnectDisconnectICM.DEFAULT_USERNAME;
    private static final String  DEFAULT_PASSWORD                     = SConnectDisconnectICM.DEFAULT_PASSWORD;
    private static final String  DEFAULT_CONNECT_OPTS                 = "";
    public  static final String  DEFAULT_INI_FILE_NAME                = TExportManagerICM.DEFAULT_INI_FILE_NAME;
    public  static final String  DEFAULT_MASTER_PACKAGE_NAME          = TExportManagerICM.DEFAULT_MASTER_PACKAGE_NAME;

    // Command line Argument Usage Constants
    private static final String  COMMANDLINE_OPTION_DATABASE          = "-d/database <you database name>";
    private static final String  COMMANDLINE_OPTION_USERNAME          = "-u/user     <CM user id>";
    private static final String  COMMANDLINE_OPTION_PASSWORD          = "-p/password <CM user's password>";
    private static final String  COMMANDLINE_OPTION_CONNECT_OPTIONS   = "-o/options  <Connect String Options>";
    private static final String  COMMANDLINE_OPTION_CONFIG_FILE       = "-i/ini      <Alternate Configuration File>";
    private static final String  COMMANDLINE_OPTION_MASTER_NAME       = "-m/master   <Master Package Name> (Name of master package exported from Export Manager.  This is the the base name of the master directories in storage locations, master file (summary), and tracking file.)";
    private static final String  COMMANDLINE_OPTION_LOG_FILE_LOCATION = "-l/log      <Folder -- Absolute Path to Master Log File Folder> (Location guaranteed to have enough space through the whole process during import, writing tracking during progress and summary file after completion.)";

    // Configuration Constants
    private static final String  MASTER_IMPORT_RECORD_FILE_EXT        = "map";
    private static final String  MASTER_PACKAGE_FILE_EXT              = "mpk";
    private static final String  MASTER_SUMMARY_FILE_EXT              = "reconcile.sum";
    private static final String  MASTER_TRACKING_FILE_EXT             = "reconcile.trk";

    // Configuration File Tags
    private static final String  NEWLINE                                  = System.getProperty("line.separator");
    // Repeats from Export Package
    private static final String  CONFIG_TAG_EXPORTPACKAGE_RETRIEVE_DENIED = "RetrieveDenied";
    // Repeats from Export Manager:
    private static final String  CONFIG_TAG_LOG_FILE_DIRECTORY            = TExportManagerICM.CONFIG_TAG_LOG_FILE_DIRECTORY;
    private static final String  CONFIG_TAG_MASTER_PACKAGE_NAME           = TExportManagerICM.CONFIG_TAG_MASTER_PACKAGE_NAME;

    // Master Export Package File Information
    private static final String  MASTER_PACKAGE_VERSION                   = TExportManagerICM.MASTER_PACKAGE_VERSION;
    private static final String  MASTER_PACKAGE_TAG_FILE_IDENTIFIER       = TExportManagerICM.FILE_TAG_MASTER_SUMMARY_IDENTIFIER;
    private static final String  MASTER_PACKAGE_TAG_PACKAGE_EXPORTED      = TExportManagerICM.FILE_TAG_PACKAGE_INFO;
    
    // Import Record File Tags
    //--> Use constants in TImportManagerICM

    // Summary File Tags
    public  static final String  FILE_TAG_MASTER_SUMMARY_IDENTIFIER             = "<Reconciliation Summary>";
    private static final String  FILE_TAG_SECTION_CONFIG_BEGIN                  = "CONFIGURATION:";
    private static final String  FILE_TAG_SECTION_CONFIG_HEADER                 = "              Setting   Value                                                " + NEWLINE
                                                                                + "----------------------  -----------------------------------------------------";
    private static final String  FILE_TAG_MASTER_PACKAGE_NAME                   = "  Master Package Name:  ";
    private static final String  FILE_TAG_DATABASE                              = "       Datastore Name:  ";
    private static final String  FILE_TAG_USERNAME                              = "         CM User Name:  ";
    private static final String  FILE_TAG_CONNOPTS                              = "      Connect Options:  ";
    public  static final String  FILE_TAG_ITEMTYPES_SELECTED                    = "  Item Types Selected:  ";
    private static final String  FILE_TAG_RETRY_ATTEMPTS                        = "    # Retries / Issue:  ";
    private static final String  FILE_TAG_RETRY_DELAY_MS                        = "Retry Delay Time (ms):  ";
    private static final String  FILE_TAG_CONFIG_FILE_NAME                      = "          Config File:  ";
    private static final String  FILE_TAG_MASTER_LOG_FILE_DIRECTORY             = "    Master Log Folder:  ";
    private static final String  FILE_TAG_MASTER_PACKAGE_FILE                   = "  Master Package File:  ";
    private static final String  FILE_TAG_MASTER_IMPORT_RECORD_FILE             = "  Item Mapping Record:  ";
    private static final String  FILE_TAG_MASTER_SUMMARY_FILE                   = "    Reconcile Summary:  ";
    private static final String  FILE_TAG_MASTER_TRACKING_FILE                  = "        Tracking File:  ";
    private static final String  FILE_TAG_SECTION_CONFIG_END                    = "-----------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_STATS_BEGIN                   = "STATISTICS:";
    private static final String  FILE_TAG_SECTION_STATS_HEADER                  = "                            Statistic   Value                                            " + NEWLINE
                                                                                + "-------------------------------------  -------------------------------------------------";
    private static final String  FILE_TAG_STAT_TOTAL_PACKAGES                   = "                          # Packages:  ";
    private static final String  FILE_TAG_STAT_TOTAL_EXPORTED_ITEMS             = "         # Items Originally Exported:  ";
    private static final String  FILE_TAG_STAT_TOTAL_IMPORTED_ITEMS             = "        # Items Recorded as Imported:  ";
    private static final String  FILE_TAG_STAT_TOTAL_ITEMS_FOUND                = "             # Items Currently Found:  ";
    private static final String  FILE_TAG_STAT_TOTAL_VERIFIED_ITEMS             = "        # Items Verified as Imported:  ";
    private static final String  FILE_TAG_STAT_TOTAL_MISSING_ITEMS              = "# New / Missing Items / Not Imported:  ";
    private static final String  FILE_TAG_STAT_NUM_CONNECT_LEVEL_ERRORS         = "              # Connect-Level Errors:  ";
    private static final String  FILE_TAG_STAT_NUM_QUERY_LEVEL_ERRORS           = "                # Query-Level Errors:  ";
    private static final String  FILE_TAG_STAT_START_TIMESTAMP                  = "                          Start Time:  ";
    private static final String  FILE_TAG_STAT_END_TIMESTAMP                    = "                     Completion Time:  ";
    private static final String  FILE_TAG_SECTION_STATS_END                     = "----------------------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_BEGIN  = "PROCESSED PACKAGE LIST:";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_HEADER = "Tag            Package#   # Items           After Item ID                 Last Item ID           Folder                        " + NEWLINE
                                                                                + "-------------  --------  ----------  ----------------------------  ----------------------------  ------------------------------";
    private static final String  FILE_TAG_PACKAGE_INFO                          = "Package Info:  ";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_END    = "-------------------------------------------------------------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_MISSING_ITEM_LIST_BEGIN       = "MISSING ITEMS:";
    private static final String  FILE_TAG_SECTION_MISSING_ITEM_LIST_HEADER      = "Tag            PID" + NEWLINE
                                                                                + "-------------  ----------------------------------------------------------------------------------------------------------------";
    public  static final String  FILE_TAG_MISSING_ITEM                          = "Missing Item:  ";
    private static final String  FILE_TAG_SECTION_MISSING_ITEM_LIST_END         = "-------------------------------------------------------------------------------------------------------------------------------";

    // Tracking File Constants
    private static final String  TRACKING_TAG_VERIFY_ALL_STARTED          = "Starting Verification:  ";
    private static final String  TRACKING_TAG_VERIFY_ALL_COMPLETED        = "Completed Verification:  ";
    private static final String  TRACKING_TAG_FAILURE_CONNECT_LEVEL       = "Failure at connect-level:  ";
    private static final String  TRACKING_TAG_FAILURE_QUERY_LEVEL         = "Failure at query-level:  ";
    private static final String  TRACKING_TAG_FILE_IDENTIFIER             = "<Reconciler Tracking File>";
    private static final String  TRACKING_TAG_ITEM_MISSING                = "Item Abandoned:  ";
    private static final String  TRACKING_TAG_PACKAGE_STARTED             = "Package Started:  ";
    private static final String  TRACKING_TAG_PACKAGE_COMPLETED           = "Package Completed:  ";
    private static final String  TRACKING_TAG_SUMMARY_STARTED             = "Started Writing Summary:  ";
    private static final String  TRACKING_TAG_SUMMARY_COMPLETED           = "Completed Writing Summary:  ";
    private static final String  TRACKING_TAG_TOOL_START                  = "Tool Start:  ";
    
    // Variables
    // - Connect Variables
    String  _database                   = null;  // -d/database <xxxxxxxx>
    String  _userName                   = null;  // -u/user <xxxxxxxx>
    String  _password                   = null;  // -p/password <xxxxxxxx>
    String  _connOpts                   = null;  // -o/options <Connect String Options>
    // - Primary Option Variables
    String  _iniFileName                = null;  // -i/ini <Alternate Configuration File>
    File    _masterLogFileDirectory     = null;  // -l/log <Mast Log File Location> (Location guaranteed to have enough space through the whole process to write tracking & summary files.)
    String  _masterPackageName          = null;  // -m/master <Master Package Name> (Name of master package used as base name for master directory, tracking file, and summary file.)
    // - Objects
    DKDatastoreICM                   _dsICM                      = null;
    TExportPackageICM.Options        _options                    = null;
    TExportManagerICM_AttemptManager _connectLevelAttemptManager = null;  // Managed Operation: 
    TExportManagerICM_AttemptManager _queryLevelAttemptManager   = null;  // Managed Operation: 
    // - Internal Variables
    TreeSet<String>
              _completedImportRecords    = null;  // *Keys* to All TExportPackageICM.ImportRecord objects for all items that have completd import.  These are sorted and accessed by key ImportRecord.getKey().
    DKPidICM  _lastProcessedPid          = null;  // If a failure occurs while reconciling, the last PID processed is always tracked to the tool can recover and pick up where it left off.
    String    _itemTypesSelected         = null;  // List of item types originally selected in the Master Package file.
    TreeMap<Integer,TExportManagerICM_PackageInfo>
              _masterPackageInfoList     = null;  // All packages in sequential order found in the Master Package File.  These are sorted and accessed by PackageInfo.getKey().
    ArrayList<String>
              _missingItemList           = null;  // All items that are found in the original system that are not found in the completed item list.  Contains a list of Pid strings as java.lang.String objects.
    File      _masterImportRecordFile    = null;  // File recording all item Id mapping.  This is needed for the Reconciliation and Completion Marker tools.  This is updated after every successful package import.
    File      _masterPackageFile         = null;  // Master Package file written by Export Manager and read by Import Manager for the import process that is being reconciled.
    File      _masterSummaryFile         = null;  // Summary file written at after all items have been reconciled.
    File      _masterTrackingFile        = null;  // Tracking file used to restart the tool if it crashes or needs to be restarted later.
    long      _statNumTotalExportedItems = -1;    // Number of items originally exported.
    long      _statNumTotalFoundItems    = -1;    // Total number of items in the original system that current match the selection criteria.
    long      _statNumTotalImportedItems = -1;    // Number of actual items succesfully imported.
    long      _statNumTotalVerifiedItems = -1;    // Number of imported items found in the original system.
    int       _statNumConnectLevelErrors = -1;    // Number of times that the connect-level attempt manager experienced errors (for statistical purposes)
    int       _statNumQueryLevelErrors   = -1;    // Number of times that the query-level attempt manager experienced errors (for statistical purposes)
    Timestamp _statStartedTimestamp      = null;  // Time that the tool orginally started (for statistical purposes)
    Timestamp _statCompletedTimestamp    = null;  // Time that the tool actually completed the export (for statistical purposes)
    //-------------------------------------------------------------
    // Main
    //-------------------------------------------------------------
    /**
     * Run the Sample.
     * @param argv[] String Array containing arguments.  Optional arguments are <databse> <userName> <password>.
     */
    public static void main(String argv[]) throws Exception{

        try{
            // Setup Tool
            TImportManagerReconcilerICM reconciler = new TImportManagerReconcilerICM(argv);
            
            // Execute the Primary Program
            reconciler.run();

            //-------------------------------------------------------------
            // Sample program completed without exception
            //-------------------------------------------------------------
            System.out.println("");
            System.out.println("====================================");
            System.out.println("Import Manager Reconciler Completed.");
            System.out.println("====================================");
            System.out.println("");
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
    // Reconcilder Operation
    //=================================================================
    
   /**
    * Preventing creation with no arguments.
    **/
    private TImportManagerReconcilerICM() throws Exception{
        throw new Exception("The TImportManagerReconcilerICM object cannot be created by the no-argument constructor.  Use the constructor that takes the main arguments.");
    }//end CTOR()

   /**
    * Create an instance of the Reconciler tool.
    * @param commandlineArgs - Command line arguments from main(String[] argsv)
    **/
    public TImportManagerReconcilerICM(String[] commandlineArgs) throws Exception{
        // Display Startup Information
        printStartupInformation();
        // Start off with defaults
        initByDefaults();
        // Override Settings by Configuration File
        initByConfigurationFile(commandlineArgs);
        // Initialize Settings by Parse Command line Arguments
        initByCommandline(commandlineArgs);
        // Create Objects
        initObjects(commandlineArgs);
        // Prompt User for Remaining That Have No Defaults.
        initMissingOptionsByPrompting();
        // Validate Settings
        validateSettings();
        // Load All Packages Master Package Summary File
        loadPackageInformation();
        // Load Imported Item Mapping
        loadImportedItemMapping();
        // Display Setting Information
        printSettingInformation();
    }//end CTOR(String commandlineArgs[])

   /**
    * Start off with the defaults.  These can be later overridden.
    **/
    private void initByDefaults(){
        // Set defaults for optional command line arguments.
        // - Connect Variables
        _database                   = DEFAULT_DATABASE;
        _userName                   = DEFAULT_USERNAME;
        _password                   = DEFAULT_PASSWORD;
        _connOpts                   = DEFAULT_CONNECT_OPTS;
        // - Primary Variables
        _iniFileName                = DEFAULT_INI_FILE_NAME;
        _masterLogFileDirectory     = null;      // No Default
        _masterPackageName          = DEFAULT_MASTER_PACKAGE_NAME;
        // - Objects
        _dsICM                     = null;
        _options                   = null;
        _connectLevelAttemptManager= null;
        _queryLevelAttemptManager  = null;
        // - Internal Variables
        _completedImportRecords    = null;  // Keys of All TExportPackageICM.ImportRecord objects for all items that have completd import.  These are sorted and accessed by key ImportRecord.getKey().
        _lastProcessedPid          = null;  // If a failure occurs while reconciling, the last PID processed is always tracked to the tool can recover and pick up where it left off.
        _masterPackageInfoList     = null;  // All packages in sequential order found in the Master Package File.  These are sorted and accessed by PackageInfo.getKey().
        _missingItemList           = null;  // All items that are found in the original system that are not found in the completed item list.  Contains a list of Pid strings as java.lang.String objects.
        _masterImportRecordFile    = null;  // File recording all item Id mapping.  This is needed for the Reconciliation and Completion Marker tools.  This is updated after every successful package import.
        _masterPackageFile         = null;  // Master Package file written by Export Manager and read by Import Manager for the import process that is being reconciled.
        _masterSummaryFile         = null;  // Summary file written at after all items have been reconciled.
        _masterTrackingFile        = null;  // Tracking file used to restart the tool if it crashes or needs to be restarted later.
        _statNumTotalExportedItems = 0;     // Number of items originally exported.
        _statNumTotalFoundItems    = 0;     // Total number of items in the original system that current match the selection criteria.
        _statNumTotalImportedItems = 0;     // Number of actual items succesfully imported.
        _statNumTotalVerifiedItems = 0;     // Number of imported items found in the original system.
        _statNumConnectLevelErrors = 0;     // Number of times that the connect-level attempt manager experienced errors (for statistical purposes)
        _statNumQueryLevelErrors   = 0;     // Number of times that the query-level attempt manager experienced errors (for statistical purposes)
        _statStartedTimestamp      = new Timestamp(System.currentTimeMillis());
        _statCompletedTimestamp    = null;  // Time that the tool actually completed the process (for statistical purposes)
    }//end initMissingOptionsByDefaults()

   /**
    * Initialize the settings based on the environment variables.  It will use
    * the configuration file name specified by command line arguments, but if
    * none specified, will use defaults.
    * @param commandlineArgs - Command line arguments from main(String argv[]).
    **/
    private void initByConfigurationFile(String[] commandlineArgs) throws IllegalArgumentException, FileNotFoundException, IOException{

        // First, check the command line arguments for the file name first.
        String  iniFileName    = TExportManagerICM.getCommandlineChoice(commandlineArgs,"-i","-ini"     ,false,COMMANDLINE_OPTION_CONFIG_FILE);
        // If specified at command line, save value
        if(iniFileName!=null)
            _iniFileName = iniFileName;
        // Ensure that the ini file specified actually exists.
        TExportManagerICM.validateFileExists(_iniFileName,"If a configuration file is specified, it must exist and appear in the proper directory.");

        // Parse file, reading values.
        // Open File
        FileReader fileReader = new FileReader(_iniFileName);
        BufferedReader file   = new BufferedReader(fileReader);

        // Scan line by line, reading entries applicable to this object.
        String line = null;
        for(int lineNum = 1; (line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.
            int separatorLoc = line.indexOf("=");
            if(separatorLoc > 0){
                String property = line.substring(0,separatorLoc).trim();
                String value    = line.substring(separatorLoc+1).trim();
                // Handle depending on property name
                // Property: Master Log File Folder
                if(property.compareToIgnoreCase(CONFIG_TAG_LOG_FILE_DIRECTORY)==0){
                    if(value.compareTo("")!=0) _masterLogFileDirectory = new File(value);  // Only save non-blank values.
                }
                // Property: Master Package Name
                else if(property.compareToIgnoreCase(CONFIG_TAG_MASTER_PACKAGE_NAME)==0){
                    if(value.compareTo("")!=0) _masterPackageName = value;  // Only save non-blank values.
                }
                // Report Error if Export Package is not configured to Deny Retrieve
                else if(property.compareToIgnoreCase(CONFIG_TAG_EXPORTPACKAGE_RETRIEVE_DENIED)==0){
                    if(value.compareToIgnoreCase("TRUE")!=0)
                        throw new IllegalArgumentException("The Reconciler tool only works on master packages exported with the \"PERFORMANCE BOOST IF NO INDIRECT SELECTION\" option enabled.  This requires that the \""+CONFIG_TAG_EXPORTPACKAGE_RETRIEVE_DENIED+"\" option be set to \"TRUE\" in the Export Package section of the configuration file '"+_iniFileName+"'.  The Reconciler can only reconcile against directly selected items, not indirectly selected items.  If you wish to continue and only reconcile against those that are directly selected, set the \""+CONFIG_TAG_EXPORTPACKAGE_RETRIEVE_DENIED+"\" option to \"TRUE\" according to the Reconciler section \"PERFORMANCE BOOST IF NO INDIRECT SELECTION\".");
                }
            }//end if(separatorLoc > 0){
        }//end for(int lineNum = 1; (line = file.readLine())!=null); lineNum++){ // Continue until reach end of file.
            
        // Close File
        file.close();
        
        // Drop References
        file       = null;
        fileReader = null;
        
    }//end initByConfigurationFile()

   /**
    * For any settings specified at the command line, override the defaults
    * specified.  The command line arguemnts will be validated.
    * @param argv - Command line arguments from main(String args[])
    **/
    private void initByCommandline(String argv[]) throws Exception{
        //------------------------------------------------------------
        // Parse input parameters
        //--------------------------------------------------------------
        // -d/database <xxxxxxxx>
        String  database           = TExportManagerICM.getCommandlineChoice(argv,"-d","-database",false,COMMANDLINE_OPTION_DATABASE);
        // -u/user <xxxxxxxx>
        String  userName           = TExportManagerICM.getCommandlineChoice(argv,"-u","-user"    ,false,COMMANDLINE_OPTION_USERNAME);
        // -p/password <xxxxxxxx>
        String  password           = TExportManagerICM.getCommandlineChoice(argv,"-p","-password",false,COMMANDLINE_OPTION_PASSWORD);
        // -o/options <Connect String Options>
        String  connOpts           = TExportManagerICM.getCommandlineChoice(argv,"-o","-options" ,false,COMMANDLINE_OPTION_CONNECT_OPTIONS);
        // -i/ini <Alternate Configuration File>
        String  iniFileName        = TExportManagerICM.getCommandlineChoice(argv,"-i","-ini"     ,false,COMMANDLINE_OPTION_CONFIG_FILE);
        // -l/log <Folder -- Mast Log File Folder> (Location guaranteed to have enough space through the whole process to write tracking & summary files.)
        String  masterLogFileDirStr= TExportManagerICM.getCommandlineChoice(argv,"-l","-log"     ,false,COMMANDLINE_OPTION_LOG_FILE_LOCATION);
        // -m/master <Master Package Name> (Name of master package used as base name for master directory, tracking file, and summary file.)
        String  masterPackageName  = TExportManagerICM.getCommandlineChoice(argv,"-m","-master"  ,false,COMMANDLINE_OPTION_MASTER_NAME);
        
        // Save any non-null settings
        if(database           !=null) _database           = database;
        if(userName           !=null) _userName           = userName;
        if(password           !=null) _password           = password;
        if(connOpts           !=null) _connOpts           = connOpts;
        if(iniFileName        !=null) _iniFileName        = iniFileName;
        if(masterPackageName  !=null) _masterPackageName  = masterPackageName;
        if(masterLogFileDirStr!=null) _masterLogFileDirectory = new File(masterLogFileDirStr);
        
    }//end initByCommandline()
    
   /**
    * For any settings that are not currently set (such as not specified
    * by the user through the command line and did not have a default, prompt
    * the user.
    **/
    private void initMissingOptionsByPrompting() throws IOException{
        // All inputs must be made through command line or through configuration file.            
    }//end initMissingOptionsByDefaults()

   /**
    * Create any object needed, such as attempt managers.
    * @param commandlineArgs - Command line arguments from main.
    **/
    private void initObjects(String[] commandlineArgs) throws FileNotFoundException, Exception{
        // Create Objects
        _completedImportRecords     = new TreeSet<String>(); // TreeSet of *keys* to TExportPackageICM.ImportRecord objects for all items that have completd import.  These are sorted and accessed by key ImportRecord.getKey().
        _masterPackageInfoList      = new TreeMap<Integer,TExportManagerICM_PackageInfo>(); // TreeMap of TExportManagerICM_PackageInfo objects with key value PackageInfo.getKey().
        // Already Initialized for Reading Configuration File:  _completionMarkers          = new TreeMap(); // Tracks the markers for each item type for how to mark all items of that item type.  The list is accessed through the key obtaind from TImportManagerCompletionMarker.getKey().
        _dsICM                      = new DKDatastoreICM(); // Will be created later, but create instance now so we can validate database alias used.
        _options                    = new TExportPackageICM.Options(_iniFileName);
        _missingItemList            = new ArrayList<String>();  // List of String objects.
        _masterImportRecordFile     = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_IMPORT_RECORD_FILE_EXT);
        _masterPackageFile          = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_PACKAGE_FILE_EXT);
        _masterSummaryFile          = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_SUMMARY_FILE_EXT);
        _masterTrackingFile         = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_TRACKING_FILE_EXT);
        // Attempt Managers need to be loaded in order
        _connectLevelAttemptManager = new TExportManagerICM_AttemptManager("Connect-Level Attempt Manager",commandlineArgs);
        _queryLevelAttemptManager   = new TExportManagerICM_AttemptManager(_connectLevelAttemptManager,"Query-Level Attempt Manager",commandlineArgs);
    }//end initObjects()

   /**
    * Find all items in the original system based on the original selection criteria
    * and have not already been processed by this run of this tool.
    *
    * Execute Query Will:
    *    - Sort based on Item ID (ascending)
    *    - Find items greater than last Item ID of the last processed item.
    *
    * @return Returns an open cursor containing all items meeting the 
    *         original selection criteria that have not already been processed.
    **/
    private dkResultSetCursor executeQuery() throws InternalError, DKException, Exception{
        // First, make sure we have a connected datastore by this point.
        if(_dsICM==null)                throw new InternalError("Inernal Error: An established connection was expected by TImportManagerReconcilerICM.executePackageQuery().  '_dsICM' was found to be 'null'.");
        if(_dsICM.isConnected()==false) throw new InternalError("Inernal Error: An established connection was expected by TImportManagerReconcilerICM.executePackageQuery().  '_dsICM' was found never to have been connected.");
        
        // Build Query String
        String query = getQuery();
        
        // Execute Query
        System.out.println("--- Executing Query...");
        printDebug("    --> <executing...>");
        DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance(_dsICM);
        DKNVPair options[]  = new DKNVPair[3];
                 options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0");  // Find all since we don't know how many will actually turn up and we don't want to miss any.
                 options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY); // Only retrieving IDs for fast results.
                 options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                                         // Must mark the end of the NVPair
        dkResultSetCursor cursor = _dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
        printDebug("    --> Done.");

        // Drop References
        query = null;

        // Return Results
        return(cursor);
    }//end executeQuery()

   /**
    * Generate the necessary query string depending to find all
    * items in the original system based on the original selection
    * criteria and have not already been processed by this run of
    * this tool.
    *
    * Query will:
    *    - Sort based on Item ID (ascending)
    *    - Find items greater than last Item ID of the last processed item.
    *
    * @return Returns the correct and completed query string.
    **/
    private String getQuery() throws InternalError, Exception{
        
        System.out.println("--- Building Query...");
        StringBuffer query = new StringBuffer();
        
        // Get all item types as an ArrayList
        java.util.ArrayList<String> itemTypeList = getValidatedItemTypeList();
        
        // Add list of item types: (/<itemType1> | /<itemType2> | ...)
        if(itemTypeList.size() > 1) // if there are more than one, use paren
            query.append('(');
        // Add each item type:  /<itemTypeName>
        Iterator<String> itemTypeIter = itemTypeList.iterator();
        while(itemTypeIter.hasNext()){
            String itemTypeName = itemTypeIter.next();
            query.append('/');
            query.append(itemTypeName);
            printDebug("         Building: "+query.toString());
            // If continuing after a previous package, pick up where
            // left off: ...[@ITEMID>"..."]
            if(_lastProcessedPid!=null){
                query.append("[@ITEMID>=\"");            // Add [@ITEMID>="
                query.append(_lastProcessedPid.getItemId()); // add the item ID.
                query.append("\" AND @VERSIONID>=");         // Add " AND @VERSOIN=
                query.append(_lastProcessedPid.getVersionNumber());
                query.append(']');                    // Add "]
                printDebug("         Building: "+query.toString());
            }
            // If there is another, add the OR now.
            if(itemTypeIter.hasNext())
                query.append(" | ");
            itemTypeName = null; // Free reference
        }            
        if(itemTypeList.size() > 1) // if there are more than one, use paren
            query.append(')');
        // Free References
        itemTypeIter = null;
        itemTypeList = null;

        printDebug("         Building: "+query.toString());
        // Sort by Item ID
        query.append(" SORTBY(@ITEMID,@VERSIONID)");
        printDebug("         Building: "+query.toString());
        
        // Return Generated Query
        System.out.println("    --> QUERY:  "+query.toString());
        return(query.toString());
    }//end getQuery()   

   /**
    * Parse, validate, and separate the selected item types and place
    * them in an ArrayList.  Errors will be thrown for any that the
    * user does not have access to or do not exist.
    * @return Returns a java.util.ArrayList of java.lang.String objects
    *         for each validated name.
    **/
    private ArrayList<String> getValidatedItemTypeList() throws InternalError, Exception{
        // First, make sure we have a connected datastore by this point.
        if(_dsICM==null)                throw new InternalError("Inernal Error: An established connection was expected by TImportManagerReconcilerICM.getValidatedItemTypeList().  '_dsICM' was found to be 'null'.");
        if(_dsICM.isConnected()==false) throw new InternalError("Inernal Error: An established connection was expected by TImportManagerReconcilerICM.getValidatedItemTypeList().  '_dsICM' was found never to have been connected.");
        // Get Datastore Definition
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) _dsICM.datastoreDef();

        printDebug("    --> Validating Item Type List: "+_itemTypesSelected);

        // Prepare New List
        ArrayList<String> itemTypeList = new ArrayList<String>();
        
        // Parse Item Type List.
        StringTokenizer itemTypeTokens = new StringTokenizer(_itemTypesSelected,TExportManagerICM.ITEMTYPE_LIST_DELIMITER);
        while(itemTypeTokens.hasMoreTokens()){
            String itemTypeName = itemTypeTokens.nextToken().trim();
            printDebug("        - "+itemTypeName);
            // If not wildcard, validate.  Otherwise leave wildcard as is.
            if(itemTypeName.compareTo("*")!=0){
                // Validate
                dkEntityDef entityDef = dsDefICM.retrieveEntity(itemTypeName);
                if(entityDef==null) throw new Exception("Alleged item type name '"+itemTypeName+"' either does not exist as any form of an entity type in the '"+_dsICM.datastoreName()+"' datastore or user '"+_dsICM.userName()+"' does not have the necessary access to the item type.");
                // Make sure it is a root
                DKComponentTypeViewDefICM compTypeViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(itemTypeName);
                if(compTypeViewDef.isRoot()==false)
                    throw new Exception("Alleged item type name '"+itemTypeName+"' is not a root component type and therefore is not the item type name in the '"+_dsICM.datastoreName()+"' datastore.  The name provided, '"+itemTypeName+"', is a child component type.  The process calling this validation routing does not support children.  Use only base item type names.");
                // Detect if it is a view, not the base item type.
                if(compTypeViewDef.getName().compareToIgnoreCase(compTypeViewDef.getComponentTypeName())!=0)
                    throw new Exception("Alleged item type name '"+itemTypeName+"' is not the base item type name in the '"+_dsICM.datastoreName()+"' datastore.  Instead, '"+itemTypeName+"' is a view name of item type '"+compTypeViewDef.getComponentTypeName()+"'.  The process calling this validation routine does not support non-base views.  Use only base item type names.");    
                // Make sure it that the base view is the active view
                DKComponentTypeViewDefICM activeCompTypeViewDef = dsDefICM.getActiveComponentTypeView(itemTypeName);
                if(activeCompTypeViewDef.getName().compareToIgnoreCase(itemTypeName)!=0)
                    throw new Exception("Base item type '"+itemTypeName+"' is not the view that is currently active in the '"+_dsICM.datastoreName()+"' datastore for user '"+_dsICM.userName()+"'.  Instead, '"+activeCompTypeViewDef.getName()+"' is the active view for item type '"+itemTypeName+"'.  The process calling this validation routine does not support active non-base views.  Use only active base item type names.");    
                // Check an alternate was of ensuring that the base view is the active view.
                try{DKItemTypeDefICM itemTypeDef = (DKItemTypeDefICM) entityDef;}
                catch(ClassCastException exc){
                    throw new Exception("Alleged item type name '"+itemTypeName+"' is not the base item type view name in the '"+_dsICM.datastoreName()+"' datastore.  The processs that called this validation routine does not support non-base views.  Use only base item type names.");    
                }
            }//end if(itemTypeName.compareTo("*")!=0){ // If not wildcard
            // Add to list
            itemTypeList.add(itemTypeName);
        }//end while(itemTypeTokens.hasMoreTokens()){

        // Return Validated List
        return(itemTypeList);
    }//end getValidatedItemTypeList()

   /**
    * Perform item reconcile operation.
    **/
    public void reconcile() throws DKException, Exception{
        
        System.out.println("----------------");
        System.out.println("-- Reconciler --");
        System.out.println("----------------");

        // Track Tool Start
        toolStartup();

        // Reset the attempt manager for reattempt connect-level.
        _connectLevelAttemptManager.reset();
        
        // Repeat attempts until no more retry attempts left.
        while(_connectLevelAttemptManager.next()){
        
            try{
                // Connect
                _dsICM = new DKDatastoreICM();
                TExportManagerICM.connect(_dsICM,_database,_userName,_password,_connOpts);
                
                // Verify Against Original Selection Criteria
                reconcileAgainstOriginalCriteria();

                // Write Completion Report
                writeSummaryReport();
                
                // Tell the attempt manager that everything is complete.  The loop should not restart.
                _connectLevelAttemptManager.setComplete();
                
            }catch(Exception exc){
                // Report Error to Log
                track(TRACKING_TAG_FAILURE_CONNECT_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                _statNumConnectLevelErrors++; // save for stats
                // The attempt manager will handle error reporting & throwing if needed.
                _connectLevelAttemptManager.handleAttemptFailure(exc);
            }finally{ // Always disconnect & destroy connection
                //-------------------------------------------------------------
                // Disconnect from datastore & Destroy Reference
                //-------------------------------------------------------------
                // Warn if Error 
                System.out.println("--- Disconnecting From Datastore...");
                printDebug("    --> <disconnecting...>");
                try{  _dsICM.disconnect();  }
                catch(Exception exc){
                    System.out.println("WARNING:  Disconnect Reported Error:  "+exc.getMessage());
                }
                printDebug("    --> <destroying object...>");
                try{  _dsICM.destroy();     }
                catch(Exception exc){
                    System.out.println("WARNING:  Destroy Reported Error:  "+exc.getMessage());
                }
                System.out.println("    --> Disconnect Successful.");
            }//end finally
            
        }//end while(connectLevelAttemptManager.next()){
    }//end markItemsComplete()

   /**
    * Reconcile all items that match the original selection criteria against the 
    * completed item import records.
    **/
    private void reconcileAgainstOriginalCriteria() throws DKException, Exception{

        System.out.println("-----------------------------------------");
        System.out.println("-- Verifying Against Original Criteria --");
        System.out.println("-----------------------------------------");

        // Report Starting
        trackTime(TRACKING_TAG_VERIFY_ALL_STARTED);

        // Reset the attempt manager for reattempt query-level.
        _queryLevelAttemptManager.reset();
        // Repeat attempts until no more retry attempts left.
        while(_queryLevelAttemptManager.next()){

            try{
                // Reconcile Remaining Items
                reconcileRemainingItems();

                // Tell the attempt manager that everything is complete.  The loop should not restart.
                _queryLevelAttemptManager.setComplete();

            }catch(Exception exc){
                // Report Error to Log
                track(TRACKING_TAG_FAILURE_QUERY_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                _statNumQueryLevelErrors++; // Track for statistics
                        
                // The attempt manager will handle error reporting & throwing if needed.
                _queryLevelAttemptManager.handleAttemptFailure(exc);
            }//end }catch(Exception exc){
        }//end while(_queryLevelAttemptManager.next()){

        // Attempt managers restart after surpass single error.
        _connectLevelAttemptManager.reset();
                            
        // Report Completed
        _statCompletedTimestamp = new Timestamp(System.currentTimeMillis());
        trackTime(TRACKING_TAG_VERIFY_ALL_COMPLETED);

        System.out.println("--- All Items Reconciled.");
    }//end reconcileAgainstOriginalCriteria()
    
   /**
    * For all remaining items based on the selection criteria that have not already 
    * been reconciled, verify the rest.
    **/
    private void reconcileRemainingItems() throws DKException, Exception{

        System.out.println("-------------------------------------");
        // If this method is restarted due to a reattempt from a layer above, report where it is picking up from...
        if(_lastProcessedPid!=null)
            System.out.println("   Resuming Reconcilation of Items");
        else // Otherwise this is the first run
            System.out.println("   Reconciling Items");
        System.out.println("-------------------------------------");
        printGeneral("Processing...");

        // Execute Query for all remaining items meeting the selection criteria.
        dkResultSetCursor cursor = executeQuery();

        // For each item found, 
        //   1 - If it was completed, mark as completed
        //   2 - If it was not completed (new item or failed import), mark on missing item list.
        //   3 - Record successful processing
        DKDDO ddo = null;
        while( (ddo = cursor.fetchNext())!=null ){
            printDebug("   --> Processing item '"+(_statNumTotalFoundItems+1)+"':  "+TImportManagerCompletionMarkerICM.obj2String(ddo));
            DKPidICM pid = (DKPidICM) ddo.getPidObject();
            // Determine if this DDO was marked as imported by Import Manager
            if(_completedImportRecords.contains(TExportPackageICM.ImportRecord.getKey(pid.getItemId(),pid.getVersionNumber()))){
                // Mark as complete
                _statNumTotalVerifiedItems++; // Just keep count
                printDebug("        - Verified.");
            }else{ // Otherwise it is missing, so add to missing item list
                _missingItemList.add(pid.pidString());
                printDebug("        - Missing.");
            }
            // Record last processed item id
            _lastProcessedPid = pid;
            // Keep count of total items found
            _statNumTotalFoundItems++;
            // Drop References
            ddo = null;
            pid = null;
        }//end while( (ddo = cursor.next())!=null ){
        
        // Close Cursor
        cursor.close();
        cursor = null;

        System.out.println("-------------------------------------------");
        System.out.println("   Completed Reconciling Remaining Items");
        System.out.println("-------------------------------------------");
        
    }//end markItems()

   /**
    * Load the imported item mapping information for the list of successfully imported
    * items from the Master Imported Item Mapping file.
    **/
    private void loadImportedItemMapping() throws FileNotFoundException, IOException, Exception{
        printDebug("    --> Loading from master mapping file '"+_masterImportRecordFile.getAbsolutePath()+"'...");

        // Open File
        FileReader fileReader = new FileReader(_masterImportRecordFile.getAbsolutePath());
        BufferedReader file   = new BufferedReader(fileReader);

        // Read & Validate File Identifier & Version Check
        String line = file.readLine();
        if(!line.startsWith(TImportManagerICM.IMPORT_RECORD_TAG_IDENTIFIER))
            throw new IllegalArgumentException("The Master Imported Item Mapping Record, '"+_masterImportRecordFile.getAbsolutePath()+"', does not appear to be valid.  This file should have been written in a format that this tool can understand.");
        String fileVersion = line.substring(line.lastIndexOf('v')+1);
        if(fileVersion.compareTo(TImportManagerICM.IMPORT_RECORD_TAG_FORMAT_VERSION) != 0){
            throw new IllegalArgumentException("WARNING:  Master Imported Item Record was not created from the same file format version as is understood by this tool.  File is from version '"+fileVersion+"' version, but the current version is '"+TImportManagerICM.IMPORT_RECORD_TAG_FORMAT_VERSION+"'.  The old format may not be compatable with the new format.  Use the same version of Import Manager, Export Manager, and their dependent tools");
        }//end if(fileVersion.compareTo(IMPORT_RECORD_TAG_FORMAT_VERSION) != 0){

        // Read File Line-by-line, handling each package as found.
        TExportManagerICM_PackageInfo currentPackageInfo = null;
        TreeSet<String> currentImportRecords = null;  // *keys* to ImportRecords, not the ImportRecords themselves.
        for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.
            printDebug("LINE "+lineNum+":  "+line);
        
            // Find a start tag
            if(line.startsWith(TImportManagerICM.IMPORT_RECORD_TAG_SECTION_MAPPING_BEGIN)){
                String value = line.substring(TImportManagerICM.IMPORT_RECORD_TAG_SECTION_MAPPING_BEGIN.length());
                currentPackageInfo = new TExportManagerICM_PackageInfo(value,true,_options);
                // Drop any previous data that was in progress that did not complete with an end tag.
                currentImportRecords = new TreeSet<String>();
            }
            // When a package is found, put int the package designatded as started
            else if(line.startsWith(TImportManagerICM.IMPORT_RECORD_TAG_PACKAGE_MAPPING)){
                // If no package was started, report format error
                if(currentPackageInfo==null)
                    throw new InternalError("Invalid format of data in Master Imported Item Mapping Record '"+_masterImportRecordFile.getAbsolutePath()+"'.  Found a mapping record at line '"+lineNum+"' where no package start tag found first.");
                // Use object to parse its own string format
                TExportPackageICM.ImportRecord importRecord = new TExportPackageICM.ImportRecord(line);
                // Add to temporary list until the end tag is found.
                currentImportRecords.add(importRecord.getKey());
                // Drop reference to import record, releasing memory
                importRecord = null;
            }
            // When a completed tag is found, save all Import Records found.
            else if(line.startsWith(TImportManagerICM.IMPORT_RECORD_TAG_SECTION_MAPPING_END)){
                String value = line.substring(TImportManagerICM.IMPORT_RECORD_TAG_SECTION_MAPPING_END.length());
                TExportManagerICM_PackageInfo endPackageInfo = new TExportManagerICM_PackageInfo(value,true,_options);
                // If there is no corresponding start for this end tag, report an error.
                if(currentPackageInfo==null)
                    throw new InternalError("Invalid format of data in Master Imported Item Mapping Record '"+_masterImportRecordFile.getAbsolutePath()+"'.  Found a tag indicating the end of of the list of completed items for package \"endPackageInfo.toString()\" at line "+lineNum+" but no start tag was found.");
                // If the end tag doesn't correspond to the package that was started, report an error.
                else if(currentPackageInfo.getPackageNum()!=endPackageInfo.getPackageNum())
                    throw new InternalError("Invalid format of data in Master Imported Item Mapping Record '"+_masterImportRecordFile.getAbsolutePath()+"'.  Found a tag indicating the end of of the list of completed items for package \"endPackageInfo.toString()\" at line "+lineNum+" but it does not match the lest begin package tag found for package \""+currentPackageInfo.toString()+"\".");
                // Save the import records in the master import record list for all packages.
                _completedImportRecords.addAll(currentImportRecords);
                // Update total count
                _statNumTotalImportedItems += currentImportRecords.size();
                // Reset current info to 'null' to indicate no package section open
                currentPackageInfo   = null;
                currentImportRecords = null;
            }
        }//end for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.

        // Close the file
        file.close();

        // Drop references
        file       = null; 
        fileReader = null;

        printDebug("    --> Imported Item Mapping Loaded.");
    }//end loadImportedItemMapping()
    
   /**
    * Load the package list to import from the master package file.
    * If any are missing in a sequence or out of order, an error will
    * be thrown
    **/
    private void loadPackageInformation() throws FileNotFoundException, IOException, Exception{
        printDebug("    --> Loading from master package file '"+_masterPackageFile.getAbsolutePath()+"'...");

        // Open File
        FileReader fileReader = new FileReader(_masterPackageFile.getAbsolutePath());
        BufferedReader file   = new BufferedReader(fileReader);

        // Read & Validate File Identifier & Version Check
        String line = file.readLine();
        if(!line.startsWith(MASTER_PACKAGE_TAG_FILE_IDENTIFIER))
            throw new IllegalArgumentException("File specified, '"+_masterPackageFile.getAbsolutePath()+"', does not appear to be a Master Package File.  This is the file that should have been exported using Export Manager.");
        String fileVersion = line.substring(line.lastIndexOf('v')+1);
        if(fileVersion.compareTo(MASTER_PACKAGE_VERSION) != 0){
            throw new IllegalArgumentException("WARNING:  Master Package File was not exported from the same version of this tool.  File is from version '"+fileVersion+"' version, but the current version is '"+MASTER_PACKAGE_VERSION+"'.  The old format may not be compatable with the new format.  Use the same version of Import Manager and Export Manager tools.");
        }//end if(fileVersion.compareTo(MASTER_PACKAGE_VERSION) != 0){

        // Read File Line-by-line, handling each package as found.
        int nextPackageNumExpected = 1;  // Start out with reading package 1 first.
        for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.
            printDebug("LINE "+lineNum+":  "+line);

            // CONFIGURATION
            // - Use Item Type List / Export All Choice from Oritinal Master Package Configuration
            if(line.startsWith(TExportManagerICM.FILE_TAG_EXPORT_ALL_CHOICE)){
                _itemTypesSelected = line.substring(TExportManagerICM.FILE_TAG_EXPORT_ALL_CHOICE.length()).trim();
                if(_itemTypesSelected.length()==0){ // Make sure this is set correctly.
                    throw new Exception("The master package file, '"+_masterPackageFile.getAbsolutePath()+"', that was allegedly created by Export Manager and used by Import Mangager for the import operation is missing the original item type list selection (also referred to as Export All Choice).  The reconciler tool needs this information specified in the Master Package File in order to re-run the original query using the original selection criteria.  The version of Export Manager that works with Reconciler only supports selection through the item type list.");
                }//end if(_itemTypesSelected.length()==0){ // Make sure this is set correctly.
            }
            // PACKAGES
            // - Export Package
            if(line.startsWith(MASTER_PACKAGE_TAG_PACKAGE_EXPORTED)){
                String value = line.substring(MASTER_PACKAGE_TAG_PACKAGE_EXPORTED.length());
                TExportManagerICM_PackageInfo packageInfo = new TExportManagerICM_PackageInfo(value,true,_options);
                // Detect out of order
                if(packageInfo.getPackageNum()!=nextPackageNumExpected)
                    throw new Exception("Package out of order in line '"+lineNum+"' in tracking file '"+_masterPackageFile.getAbsolutePath()+"' package list section.  Expected package number '"+nextPackageNumExpected+"', but instead found package '"+packageInfo.getPackageNum()+"'.  Package \""+packageInfo.toString()+"\" was constructed from tracking file entry \""+value+"\".");
                //Only done in order:  // Detect if already marked completed.  If done twice, that indicates duplication occurred of an entire package.  This is highly unlikely.
                //Only done in order:  if(_packageInfoList.containsKey(packageInfo.getKey())){
                //Only done in order:      TExportManagerICM_PackageInfo existingPackageInfo = (TExportManagerICM_PackageInfo) _completedPackageInfoList.get(packageInfo.getKey());
                //Only done in order:      throw new Exception("More than one completion notices found for same package number, '"+packageInfo.getPackageNum()+"'.  The second notice was found at line '"+lineNum+"' in tracking file '"+_masterTrackingFile.getAbsolutePath()+"' package progress section.  Package \""+packageInfo.toString()+"\" was constructed from tracking file entry \""+value+"\".  Already found completed package tracked as \""+existingPackageInfo.toString()+"\".");
                //Only done in order:  }
                // Save as completed package
                printDebug("--> Recognized package '"+nextPackageNumExpected+"': "+packageInfo.toString());
                _masterPackageInfoList.put(packageInfo.getKey(),packageInfo);
                nextPackageNumExpected++; // and increment the package number to the next one expected
                // Record total count
                _statNumTotalExportedItems += packageInfo.getNumItems();
            }
            // ABANDONED FOLDERS
            // <not applicable to import tool>
            
        }//end for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.

        // Close the file
        file.close();

        // If did not fine item type selection list in file, report error.
        if((_itemTypesSelected==null)||(_itemTypesSelected.length()==0)) // If not set
            throw new Exception("Could not find list of original item types selected in the Master Package file '"+_masterPackageFile.getAbsolutePath()+"'.  This file that should have been created by Export Manager and used by Import Mangager for the import operation is missing the original item type list selection (also referred to as Export All Choice).  The reconciler tool needs this information specified in the Master Package File in order to re-run the original query using the original selection criteria.  The version of Export Manager that works with Reconciler only supports selection through the item type list.");

        printDebug("    --> Package Loading Complete.");
    }//end loadPackageInformation()
    
   /**
    * If debug printing is enabled in the TExportPackageICM.Options, print the
    * specified message.  If it is turned off, ignore the request.
    * If no Export Options have been loaded yet (null object), assume
    * enabled.
    * @param debugMessage = Debug message to print if debug printing enabled.
    **/
    private void printDebug(String debugMessage){
        if(    (_options==null)
            || (_options.getPrintDebugEnable())
          ){
            System.out.println(debugMessage);   
        }
    }//end printDebug

   /**
    * If error printing is enabled, print the
    * specified message.  If it is turned off, ignore the request.
    * @param errorMessage = Error message to print if error printing enabled.
    **/
    private void printError(String errorMessage){
        // Always print error messages
        System.out.println(errorMessage);
    }//end printTrace

   /**
    * If general tool information printing is enabled, print the
    * specified message.  If it is turned off, ignore the request.
    * @param generalMessage = General message to print if general printing enabled.
    **/
    private void printGeneral(String errorMessage){
        // Always print general messages
        System.out.println(errorMessage);
    }//end printGeneral

   /**
    * If trace printing is enabled in the TExportPackageICM.Options, print the
    * specified message.  If it is turned off, ignore the request.
    * If no Export Options have been loaded yet (null object), assume
    * enabled.
    * @param traceMessage = Trace message to print if trace printing enabled.
    **/
    private void printTrace(String traceMessage){
        if(    (_options==null)
            || (_options.getPrintTraceEnable())
          ){
            System.out.println(traceMessage);   
        }
    }//end printTrace
  
   /**
    * Print tool startup information.  The Export Manager should have
    * processed all setting options by this point.
    **/
    private void printStartupInformation(){
        String ver = SConnectDisconnectICM.VERSION;

        System.out.println("");
        System.out.println("================================================");
        System.out.println("IBM DB2 Content Manager                    V"+ver);
        System.out.println("Tool Program:  TImportManagerReconcilerICM");
        System.out.println("================================================");
        System.out.println("Reading Configuration...");
        System.out.println("");
    }//end printStartupInformation()

   /**
    * Print tool startup information.  The Export Manager should have
    * processed all setting options by this point.
    **/
    private void printSettingInformation(){

        String ver = SConnectDisconnectICM.VERSION;

        System.out.println("");
        System.out.println("================================================");
        System.out.println("IBM DB2 Content Manager                    V"+ver);
        System.out.println("Tool Program:  TImportManagerReconcilerICM");
        System.out.println("------------------------------------------------");
        if(_masterPackageName.compareToIgnoreCase(DEFAULT_MASTER_PACKAGE_NAME)==0) 
              System.out.println("        Master Name: "+_masterPackageName);
        else  System.out.println("        Master Name: "+_masterPackageName+" (default)");
              System.out.println("          Datastore: "+_database);
              System.out.println("          User Name: "+_userName);
              System.out.println("         Item Types: "+_itemTypesSelected);
              System.out.println("       Connect Opts: "+_connOpts);
        if(_iniFileName.compareToIgnoreCase(DEFAULT_INI_FILE_NAME)==0)
              System.out.println("        Config File: "+_iniFileName+" (default)");
        else  System.out.println("        Config File: "+_iniFileName);
              System.out.println("        Master Logs: "+_masterLogFileDirectory.getAbsolutePath());
              System.out.println("Master Package File: "+_masterPackageFile.getAbsolutePath());
              System.out.println("     Mapping Record: "+_masterImportRecordFile.getAbsolutePath());
        System.out.println("================================================");
        System.out.println("");
    }//end printSettingInformation()
    
   /**
    * Execute the Primary Program
    **/
    public void run() throws DKException, Exception{
        
        // Peform the feature requested by the user.

        reconcile();
        
    }//end run()

   /**
    * Write the specified name-value pair to the tracking file.
    * @param tag   - Tag name identifying what is being written.
    * @param value - Value to go with the tag.
    **/
    private void track(String tag, String val) throws IOException{
        // Prepare Line
        String line = tag + val;
        try{
            TExportManagerICM.fileAppendLn(_masterTrackingFile.getAbsolutePath(),line);
            
        }catch(IOException exc){
            String message = "ERROR WRITING MASTER TRACKING FILE '"+_masterTrackingFile.getAbsolutePath()+"' due to error \""+exc.getMessage()+"\".  Could not not write to the tracking file.  The tool must be able to write to the tracking file.  Restart the tool using the tracking file when the tool will be able to write to this file and there is enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"', avoiding error '"+exc.getMessage()+"'.";
            printError(message);
            // If it was because it ran out of space, report specialized error
            if(exc.getMessage().compareToIgnoreCase(TExportManagerICM.EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                throw new IOException("RAN OUT OF DISK SPACE WRITING MASTER TRACKING FILE '"+_masterTrackingFile.getAbsolutePath()+"'.  The tool could not write to the tracking file since there was no more disk space.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
            }
            throw new IOException(message);
        }//end catch(IOException exc){
    }//end track
    
   /**
    * Write the specified tag with the current time to the tracking file.
    * The current timestamp will be added as the value to the tag.
    * @param tag   - Tag name identifying what is being written.
    **/
    private void trackTime(String tag) throws IOException{
        // Get timestamp as string
        Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        String timestampStr = timestamp.toString();
        track(tag,timestampStr);
        timestampStr = null; // Free reference
        timestamp    = null; // Free reference
    }//end track

   /**
    * Startup the export tool.  Starting tracking of the tool and 
    * restart where left off if the tool has been restarted.
    * If this is the first, run a new log will be created.
    **/
    private void toolStartup() throws IOException, Exception{
        printDebug("    --> Tool Startup Routine...");

        // Clear any existing summary package file
        if(_masterSummaryFile.exists()){
            try{
                boolean deleted = _masterSummaryFile.delete();
                // Throw error if not successfully deleted.
                if(deleted==false)
                    throw new Exception("File.delete() [_masterSummaryFile.delete()] indicated failure by returning false.  No exception thrown.");
            }catch(Exception exc){
                throw new Exception("Tool does not have access to delete file '"+_masterSummaryFile.getAbsolutePath()+"' due to error \""+exc.getMessage()+"\".  The tool must be able to delete this file.  So that it can overwrite it when this run of the tool completes.");
            }//end }catch(IOException exc){
        }//en dif(diskSpaceHolder.exists()){

        // Start new tracking file.
        toolStartupCreateNewTrackingFile();

        // Tool Started
        printDebug("        - Tracking Tool Start Information...>");
        trackTime(TRACKING_TAG_TOOL_START);
        // record start time
        // <Already set by initByDefaults()

    }//end toolStartup()

   /**
    * Create a new tracking file, loaded with the configuration and other information
    * that goes at the start of the tracking file.
    **/
    private void toolStartupCreateNewTrackingFile() throws IOException{
        printDebug("        - Creating New Tracking File:  "+_masterTrackingFile.getAbsolutePath());
        printDebug("        - <preparing new header...>");
        // Get the system's newline separator.
        String newline = System.getProperty("line.separator");
        StringBuffer fileStr = new StringBuffer(); // Write first to string buffer.
        // - File Type Identifier
        fileStr.append(TRACKING_TAG_FILE_IDENTIFIER);
        // - Write Version of Package Tool
        fileStr.append(" v" + MASTER_PACKAGE_VERSION);
        fileStr.append(newline);
        fileStr.append(newline);
        // Write Configuration
        printDebug("        - <preparing configuration...>");
        fileStr.append(getConfigurationDescription());
        // Create New File
        printDebug("        - <writing new file...>");
        try{
            TExportManagerICM.fileCreate(_masterTrackingFile.getAbsolutePath(), fileStr.toString());
        }catch(IOException exc){
            String message = "ERROR WRITING MASTER TRACKING FILE '"+_masterTrackingFile.getAbsolutePath()+"' due to error \""+exc.getMessage()+"\".  Could not not write to the tracking file.  The tool must be able to write to the tracking file.  Restart the tool using the tracking file when the tool will be able to write to this file and there is enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"', avoiding error '"+exc.getMessage()+"'.";
            printError(message);
            // If it was because it ran out of space, report specialized error
            if(exc.getMessage().compareToIgnoreCase(TExportManagerICM.EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                throw new IOException("RAN OUT OF DISK SPACE WRITING MASTER TRACKING FILE '"+_masterTrackingFile.getAbsolutePath()+"'.  The tool could not write to the tracking file since there was no more disk space.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
            }
            throw new IOException(message);
        }//end catch(IOException exc){
        printDebug("        - <completed>");
    }//end toolStartupCreateNewTrackingFile()

   /**
    * Validate the specified list of item types to ensure that they 
    * exist in the system from the view of the currently logged in user.
    * @param itemTypeList - Sorted treeMap of item type names.  The key
    *                       value is not used by this function.
    **/
    private void validateItemTypeList(TreeMap<String,String> itemTypeList) throws InternalError, Exception{
        // First, make sure we have a connected datastore by this point.
        if(_dsICM==null)                throw new InternalError("Inernal Error: An established connection was expected by TExportManagerICM.getValidatedItemTypeList().  '_dsICM' was found to be 'null'.");
        if(_dsICM.isConnected()==false) throw new InternalError("Inernal Error: An established connection was expected by TExportManagerICM.getValidatedItemTypeList().  '_dsICM' was found never to have been connected.");
        // Get Datastore Definition
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) _dsICM.datastoreDef();

        printDebug("    --> Validating Item Type List...");

        // Iterate through all item type names listed.
        Iterator<String> iter = itemTypeList.values().iterator();
        while(iter.hasNext()){
            String itemTypeName = iter.next();
            printDebug("        - "+itemTypeName);
            // If not wildcard, validate.  Otherwise leave wildcard as is.
            if(itemTypeName.compareTo("*")!=0){
                // Validate
                dkEntityDef entityDef = dsDefICM.retrieveEntity(itemTypeName);
                if(entityDef==null) throw new Exception("Alleged item type name '"+itemTypeName+"' either does not exist as any form of an entity type in the '"+_dsICM.datastoreName()+"' datastore or user '"+_dsICM.userName()+"' does not have the necessary access to the item type.");
                // Make sure it is a root
                DKComponentTypeViewDefICM compTypeViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(itemTypeName);
                if(compTypeViewDef.isRoot()==false)
                    throw new Exception("Alleged item type name '"+itemTypeName+"' is not a root component type and therefore is not the item type name in the '"+_dsICM.datastoreName()+"' datastore.  The name provided, '"+itemTypeName+"', is a child component type.  The process calling this validation routing does not support children.  Use only base item type names.");
                // Detect if it is a view, not the base item type.
                if(compTypeViewDef.getName().compareToIgnoreCase(compTypeViewDef.getComponentTypeName())!=0)
                    throw new Exception("Alleged item type name '"+itemTypeName+"' is not the base item type name in the '"+_dsICM.datastoreName()+"' datastore.  Instead, '"+itemTypeName+"' is a view name of item type '"+compTypeViewDef.getComponentTypeName()+"'.  The process calling this validation routine does not support non-base views.  Use only base item type names.");    
                // Make sure it that the base view is the active view
                DKComponentTypeViewDefICM activeCompTypeViewDef = dsDefICM.getActiveComponentTypeView(itemTypeName);
                if(activeCompTypeViewDef.getName().compareToIgnoreCase(itemTypeName)!=0)
                    throw new Exception("Base item type '"+itemTypeName+"' is not the view that is currently active in the '"+_dsICM.datastoreName()+"' datastore for user '"+_dsICM.userName()+"'.  Instead, '"+activeCompTypeViewDef.getName()+"' is the active view for item type '"+itemTypeName+"'.  The process calling this validation routine does not support active non-base views.  Use only active base item type names.");    
                // Check an alternate was of ensuring that the base view is the active view.
                try{DKItemTypeDefICM itemTypeDef = (DKItemTypeDefICM) entityDef;}
                catch(ClassCastException exc){
                    throw new Exception("Alleged item type name '"+itemTypeName+"' is not the base item type view name in the '"+_dsICM.datastoreName()+"' datastore.  The processs that called this validation routine does not support non-base views.  Use only base item type names.");    
                }
            }//end if(itemTypeName.compareTo("*")!=0){ // If not wildcard
        }//end while(iter.hasNext()){

    }//end validatedItemTypeList()

   /**
    * Validate settings that can be easily validated without an open 
    * connection to the datastore.  Do no validate any that are already
    * validated.
    **/
    private void validateSettings() throws IllegalArgumentException, DKException, Exception{
        // Validate object neede for validation of others first.
        if(_dsICM==null) throw new InternalError("Inernal Error: Object variable '_dsICM' is null when it should have been initialized by 'TExportManagerICM.initObjects()'.");

        // Variables
        // - Connect Variables
        //   _database
        String[]     availableDsNames    = _dsICM.listDataSourceNames();
        StringBuffer availableDsNamesStr = new StringBuffer(); // Record printable version for error handling.
        boolean found = false; // Tracks if it is found in the available list.
        for(int i=0; i<availableDsNames.length; i++){ // Continue through all names until found.
            availableDsNamesStr.append(availableDsNames[i]);
            availableDsNamesStr.append(", ");
            if(availableDsNames[i].compareToIgnoreCase(_database)==0)
                found = true;
        }//end for(int i=0; i<availableDsName.length; i++){
        if(!found) throw new IllegalArgumentException("Datastore alias name '"+_database+"' is not a valid catalogged alias name available to this tool.  Ensure that the environment is set up correctly to process the configuration files in the <IBMCMROOT>/cmgmt directory.  The list of datastore alias names available to this tool are: "+availableDsNamesStr.toString());
        //   _userName                  : Validated during connect.
        //   _password                  : Validated during connect.
        //   _connOpts                  : No Validation
        // - Primary Variables
        //   _iniFileName               : Validated by initByComfigurationFile()
        //   _masterLogFileDirectory    : Make sure exists, is absolute path, and is directory
        if(_masterLogFileDirectory==null) throw new InternalError("Internal Error:  Master Log File Directory object '_masterLogFileDirectory' is 'null' in TExportManagerICM.validateSettings().  An instance of 'File' was expected.");
        if(_masterLogFileDirectory.isAbsolute()==false)  throw new IllegalArgumentException("The name specified for the master log files folder, '"+_masterLogFileDirectory.getAbsolutePath()+"', is not an absolute path.  Provide an absolute path to avoid any ambiguity for the folder in which the master summary and tracking files can be written to.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_LOG_FILE_LOCATION+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_LOG_FILE_DIRECTORY+"'.");
        if(_masterLogFileDirectory.exists()==false)      throw new IllegalArgumentException("The folder specified for the master log files does not exist.  Provide a valid location in which the master summary and tracking files can be written to.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_LOG_FILE_LOCATION+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_LOG_FILE_DIRECTORY+"'.");
        if(_masterLogFileDirectory.isDirectory()==false) throw new IllegalArgumentException("The name specified for the master log files folder, '"+_masterLogFileDirectory.getAbsolutePath()+"', is not a directory / folder.  Provide a valid folder in which the master summary and tracking files can be written to.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_LOG_FILE_LOCATION+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_LOG_FILE_DIRECTORY+"'.");
        //   _masterPackageName         : Make sure non-null, non-empty, must start with alphabetical character.
        if(_masterPackageName==null) throw new IllegalArgumentException("No master package name is specified (null).  A valid master package name is required.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_MASTER_NAME+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_MASTER_PACKAGE_NAME+"'.");
        if(_masterPackageName.trim().length()==0)   throw new IllegalArgumentException("No master package name is specified (empty string '"+_masterPackageName+"').  A valid master package name is required.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_MASTER_NAME+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_MASTER_PACKAGE_NAME+"'.");
        // Internal Variables
        // - Objects
        //   _dsICM                     : Validated above
        //   _completedImportRecords    : Not Null
        if(_completedImportRecords==null)  throw new InternalError("Inernal Error: Object variable '_completedImportRecords' is null when it should have been initialized by 'TImportManagerReconcilerICM.initObjects()'.");
        //   _missingItemList           : Not Null
        if(_missingItemList==null)  throw new InternalError("Inernal Error: Object variable '_missingItemList' is null when it should have been initialized by 'TImportManagerReconcilerICM.initObjects()'.");
        //   _connectLevelAttemptManager : Not Null
        if(_connectLevelAttemptManager==null)  throw new InternalError("Inernal Error: Object variable '_connectLevelAttemptManager' is null when it should have been initialized by 'TImportManagerReconcilerICM.initObjects()'.");
        //   _queryLevelAttemptManager: Not Null
        if(_queryLevelAttemptManager==null) throw new InternalError("Inernal Error: Object variable '_queryLevelAttemptManager' is null when it should have been initialized by 'TImportManagerReconcilerICM.initObjects()'.");
        //   _masterImportRecordFile    : Not Null & Exists
        if(_masterImportRecordFile==null) throw new InternalError("Inernal Error: Object variable '_masterImportRecordFile' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        if(_masterImportRecordFile.exists()==false) throw new IllegalArgumentException("Cannot locate the Master Import Mapping Record file '"+_masterImportRecordFile.getAbsolutePath()+"' in the specified Master Log File Directory '"+_masterLogFileDirectory.getAbsolutePath()+"'.  This file should have been created by TImportManagerICM as it completed packages.  Provide a partial or complete mapping file.");
        //   _masterPackageFile         : Not Null & Exists
        if(_masterPackageFile==null) throw new InternalError("Inernal Error: Object variable '_masterPackageFile' is null when it should have been initialized by 'TImportManagerReconcilerICM.initObjects()'.");
        if(_masterPackageFile.exists()==false) throw new IllegalArgumentException("Cannot locate the Master Package file '"+_masterPackageFile.getAbsolutePath()+"' in the specified Master Log File Directory '"+_masterLogFileDirectory.getAbsolutePath()+"'.  This file should have been created by TExportManagerICM and used as they key file for coordinating all individual packages for import.");
        //   _masterSummaryFile         : Not Null
        if(_masterSummaryFile==null)          throw new InternalError("Inernal Error: Object variable '_masterSummaryFile' is null when it should have been initialized by 'TImportManagerReconcilerICM.initObjects()'.");
        //   _masterTrackingFile        : Not Null
        if(_masterTrackingFile==null)         throw new InternalError("Inernal Error: Object variable '_masterTrackingFile' is null when it should have been initialized by 'TImportManagerReconcilerICM.initObjects()'.");
    }//end validateSettings()

   /**
    * After all packages have been imported, write a summary report
    * nicely formatting the final report.
    **/
    private void writeSummaryReport() throws IOException{
        printGeneral("--- Writing Summary File '"+_masterSummaryFile.getAbsolutePath()+"'...");

        // Track Starting
        track(TRACKING_TAG_SUMMARY_STARTED,_masterSummaryFile.getAbsolutePath());

        // Write file to string buffer, then write the string buffer.
        printDebug("   --> Preparing Content...");
        StringBuffer fileStr = new StringBuffer();
        // Get the system's newline separator.
        String newline = System.getProperty("line.separator");
        
        // Write Identifier
        // - File Type Identifier
        printDebug("        - <Preparing Identifier>...");
        fileStr.append(FILE_TAG_MASTER_SUMMARY_IDENTIFIER);
        // - Write Version of Package Tool
        fileStr.append(" v" + MASTER_PACKAGE_VERSION);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write Configuration Information
        printDebug("        - <Preparing Configuration>...");
        fileStr.append(getConfigurationDescription());
        // Write Statistics   
        printDebug("        - <Preparing Statistics>...");
        fileStr.append(FILE_TAG_SECTION_STATS_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_STATS_HEADER);
        fileStr.append(newline);
        // - # Total Packages
        fileStr.append(FILE_TAG_STAT_TOTAL_PACKAGES);
        fileStr.append(_masterPackageInfoList.size());
        fileStr.append(newline);
        // - # Total Items Exported
        fileStr.append(FILE_TAG_STAT_TOTAL_EXPORTED_ITEMS);
        fileStr.append(_statNumTotalExportedItems);
        fileStr.append(newline);
        // - # Total Items Imported
        fileStr.append(FILE_TAG_STAT_TOTAL_IMPORTED_ITEMS);
        fileStr.append(_statNumTotalImportedItems);
        fileStr.append(newline);
        // - # Total Items Found in Original System
        fileStr.append(FILE_TAG_STAT_TOTAL_ITEMS_FOUND);
        fileStr.append(_statNumTotalFoundItems);
        fileStr.append(newline);
        // - # Total Items Verified
        fileStr.append(FILE_TAG_STAT_TOTAL_VERIFIED_ITEMS);
        fileStr.append(_statNumTotalVerifiedItems);
        fileStr.append(newline);
        // - # Total Missing Items
        fileStr.append(FILE_TAG_STAT_TOTAL_MISSING_ITEMS);
        fileStr.append(_missingItemList.size());
        fileStr.append(newline);
        // - # Connect-Level Errors
        fileStr.append(FILE_TAG_STAT_NUM_CONNECT_LEVEL_ERRORS);
        fileStr.append(_statNumConnectLevelErrors);
        fileStr.append(newline);
        // - # Query-Level Errors
        fileStr.append(FILE_TAG_STAT_NUM_QUERY_LEVEL_ERRORS);
        fileStr.append(_statNumQueryLevelErrors);
        fileStr.append(newline);
        // - Original Start Time
        fileStr.append(FILE_TAG_STAT_START_TIMESTAMP);
        fileStr.append(_statStartedTimestamp);
        fileStr.append(newline);
        // - Time Completed
        fileStr.append(FILE_TAG_STAT_END_TIMESTAMP);
        fileStr.append(_statCompletedTimestamp);
        fileStr.append(newline);
        // - Print End Tag
        fileStr.append(FILE_TAG_SECTION_STATS_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write Completed Packages Processed List
        printDebug("        - <Preparing Processed Package List>...");
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_HEADER);
        fileStr.append(newline);
        // Loop through all packages, write thier package info.
        Iterator<TExportManagerICM_PackageInfo> iter = _masterPackageInfoList.values().iterator();
        while(iter.hasNext()){
            TExportManagerICM_PackageInfo packageInfo = iter.next();
            // Write package description
            fileStr.append(FILE_TAG_PACKAGE_INFO);
            fileStr.append(packageInfo.toString());
            fileStr.append(newline);
        }//end while(packageInfoIter.hasNext()){
        // - Write end section tag.        
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write Missging Item List
        printDebug("        - <Preparing Missing Item List>...");
        fileStr.append(FILE_TAG_SECTION_MISSING_ITEM_LIST_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_MISSING_ITEM_LIST_HEADER);
        fileStr.append(newline);
        // Loop through all packages, write thier package info.
        Iterator<String> missingItemIter = _missingItemList.iterator();
        while(missingItemIter.hasNext()){
            String missingItemPidStr = missingItemIter.next();
            // Write package description
            fileStr.append(FILE_TAG_MISSING_ITEM);
            fileStr.append(missingItemPidStr);
            fileStr.append(newline);
        }//end while(packageInfoIter.hasNext()){
        // - Write end section tag.        
        fileStr.append(FILE_TAG_SECTION_MISSING_ITEM_LIST_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write to Disk
        printDebug("    --> <Preparing to Write>...");
        // - Double check that there is no existing file by this name.  It should have been
        //   deleted at the beginning of exportAllPackages()
        if(_masterSummaryFile.exists()){
            throw new InternalError("Internal Error:  The Reconciler Summary file '"+_masterSummaryFile.getAbsolutePath()+"' should have been deleted at the beginning of TImportMangerReconcilerICM.reconcile().  However, it exists prior to writing the summary report in the TImportManagerReconcilerICM.writeSummaryReport() method.");
        }//end if(_masterSummaryFile.exists()){
        // - Open file for writing
        printDebug("   --> Opening File...");
        FileWriter file = null;
        try{
            file = new FileWriter(_masterSummaryFile);
            // - Write to disk.
            printDebug("   --> Writing File...");
            file.write(fileStr.toString());
        }catch(IOException exc){
            printError("ERROR WRITING RECONCILER PROCESS SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"' due to: "+exc.getMessage());
            // If it was because it ran out of space, report specialized error
            if(exc.getMessage().compareToIgnoreCase(TExportManagerICM.EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                throw new IOException("RAN OUT OF DISK SPACE WRITING RECONCILER SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"'.  The reconcile process had completed successfully, but the tool could not write the summary file.  The master log file location really must be reliable and have sufficient disk space.  Re-run the tool when enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
            }
            throw new IOException("ERROR WRITING RECONCILER SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"' due to error \""+exc.getMessage()+"\".  The reconcile process had completed successfully, but the tool could not write the summary file.  The master log file location really must be reliable and have sufficient disk space.  Re-run the tool when the error can be bypassed on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
        }//end catch(IOException exc){
        
        // Close the file
        printDebug("   --> Closing File...");
        file.close();
        printGeneral("   --> Completed Summary File '"+_masterSummaryFile.getAbsolutePath()+"'.");

        // Track Completion
        track(TRACKING_TAG_SUMMARY_COMPLETED,_masterSummaryFile.getAbsolutePath());
        
    }//end writeSummaryReport()

   /**
    * Get a multi-line description of the configuration for writing to file.
    * @return Returns a multi-line string for the configuration settings.
    **/
    private String getConfigurationDescription(){
        // Get the system's newline separator.
        String newline = System.getProperty("line.separator");
        StringBuffer fileStr = new StringBuffer();
        fileStr.append(FILE_TAG_SECTION_CONFIG_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_CONFIG_HEADER);
        fileStr.append(newline);
        // - Master Name
        fileStr.append(FILE_TAG_MASTER_PACKAGE_NAME);
        fileStr.append(_masterPackageName);
        fileStr.append(newline);
        // - Datastore
        fileStr.append(FILE_TAG_DATABASE);
        fileStr.append(_database);
        fileStr.append(newline);
        // - User ID
        fileStr.append(FILE_TAG_USERNAME);
        fileStr.append(_userName);
        fileStr.append(newline);
        // - Password
        // <omit>
        // - Connect Options
        fileStr.append(FILE_TAG_CONNOPTS);
        fileStr.append(_connOpts);
        fileStr.append(newline);
        // - Item Types Selected
        fileStr.append(FILE_TAG_ITEMTYPES_SELECTED);
        fileStr.append(_itemTypesSelected);
        fileStr.append(newline);
        // - Retry Attempts
        //Not recorded in this object:  fileStr.append(FILE_TAG_RETRY_ATTEMPTS);
        //Not recorded in this object:  fileStr.append(tbd);
        //Not recorded in this object:  fileStr.append(newline);
        // - Retry Delay Time (ms)
        //Not recorded in this object:  fileStr.append(FILE_TAG_RETRY_DELAY_MS);
        //Not recorded in this object:  fileStr.append(tbd);
        //Not recorded in this object:  fileStr.append(newline);
        // - Configuration File
        fileStr.append(FILE_TAG_CONFIG_FILE_NAME);
        fileStr.append(_iniFileName);
        fileStr.append(newline);
        // - Master Log File Location
        fileStr.append(FILE_TAG_MASTER_LOG_FILE_DIRECTORY);
        fileStr.append(_masterLogFileDirectory.getAbsolutePath());
        fileStr.append(newline);
        // - Master Export Package File
        fileStr.append(FILE_TAG_MASTER_PACKAGE_FILE);
        fileStr.append(_masterPackageFile.getAbsolutePath());
        fileStr.append(newline);
        // - Master Imported Item Mapping File
        fileStr.append(FILE_TAG_MASTER_IMPORT_RECORD_FILE);
        fileStr.append(_masterImportRecordFile.getAbsolutePath());
        fileStr.append(newline);
        // - Master Summary File
        fileStr.append(FILE_TAG_MASTER_SUMMARY_FILE);
        fileStr.append(_masterSummaryFile.getAbsolutePath());
        fileStr.append(newline);
        // - Master Tracking File
        fileStr.append(FILE_TAG_MASTER_TRACKING_FILE);
        fileStr.append(_masterTrackingFile.getAbsolutePath());
        fileStr.append(newline);
        // - End of Section
        fileStr.append(FILE_TAG_SECTION_CONFIG_END);
        fileStr.append(newline);
        fileStr.append(newline);
        return(fileStr.toString());
    }//end getConfigurationDescription

    //=================================================================
    // Tool Functions
    //=================================================================
    // The following are wrapper functions for functionality covered
    // in this sample.  These functions can be used by other samples.

    //=================================================================
    // Internal Classes
    //=================================================================
   
}//end class TImportManagerReconcilerICM
