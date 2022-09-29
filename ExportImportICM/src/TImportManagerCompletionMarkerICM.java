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
         For an overview and detailed description, refer to the
         "Completion Marker" section in the header documentation of
         the TExportManagerICM.java sample file.
     
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
import java.sql.Timestamp;

/************************************************************************************************
 *          FILENAME: TImportManagerCompletionMarkerICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Given a partial or complete Master Imported Item Mapping file from
 *                    an Import Manager process, this will mark all of the completed items
 *                    by appending a prefix, appending a suffix, or replacing an attribute
 *                    value of the users choice, or reindex the completed items.,
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java TImportManagerCompletionMarkerICM <options>
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
 *                             java TImportManagerCompletionMarkerICM -d icmnlsdb -u icmadmin -p password
 *
 *                    Document:  n/a
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: A partial or complete Master Imported Item Mapping must exist in the
 *                    specified Master Log File Directory.
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 *                    TExportPackageICM.java
 *                    SLinksICM.java
 ************************************************************************************************/
public class TImportManagerCompletionMarkerICM{

    // Enumaration Constants

    // Default Settings
    private static final String  DEFAULT_DATABASE                     = SConnectDisconnectICM.DEFAULT_DATABASE;
    private static final String  DEFAULT_USERNAME                     = SConnectDisconnectICM.DEFAULT_USERNAME;
    private static final String  DEFAULT_PASSWORD                     = SConnectDisconnectICM.DEFAULT_PASSWORD;
    private static final String  DEFAULT_CONNECT_OPTS                 = "";
    public  static final String  DEFAULT_INI_FILE_NAME                = TExportManagerICM.DEFAULT_INI_FILE_NAME;
    public  static final String  DEFAULT_MASTER_PACKAGE_NAME          = TExportManagerICM.DEFAULT_MASTER_PACKAGE_NAME;
    private static final int     DEFAULT_SKIP_ITEM_AFTER_ATTEMPT_NUM  = 10;

    // Command Line Argument Usage Constants
    private static final String  COMMANDLINE_OPTION_DATABASE          = "-d/database <you database name>";
    private static final String  COMMANDLINE_OPTION_USERNAME          = "-u/user     <CM user id>";
    private static final String  COMMANDLINE_OPTION_PASSWORD          = "-p/password <CM user's password>";
    private static final String  COMMANDLINE_OPTION_CONNECT_OPTIONS   = "-o/options  <Connect String Options>";
    private static final String  COMMANDLINE_OPTION_CONFIG_FILE       = "-i/ini      <Alternate Configuration File>";
    private static final String  COMMANDLINE_OPTION_MASTER_NAME       = "-m/master   <Master Package Name> (Name of master package exported from Export Manager.  This is the the base name of the master directories in storage locations, master file (summary), and tracking file.)";
    private static final String  COMMANDLINE_OPTION_LOG_FILE_LOCATION = "-l/log      <Folder -- Absolute Path to Master Log File Folder> (Location guaranteed to have enough space through the whole process during import, writing tracking during progress and summary file after completion.)";

    // Configuration Constants
    public  static final String  MASTER_IMPORT_RECORD_FILE_EXT        = "map";
    public  static final String  MASTER_SUMMARY_FILE_EXT              = ".marker.sum";
    public  static final String  MASTER_TRACKING_FILE_EXT             = ".marker.trk";

    // Configuration File Tags
    private static final String  NEWLINE                                  = System.getProperty("line.separator");
    private static final String  CONFIG_TAG_SKIP_ITEM_AFTER_ATTEMPT_NUM   = "Skip Item After Attempt Number";
    private static final String  CONFIG_TAG_MARK_BY_ATTRIBUTE_REPLACEMENT = "Mark by Attribute Replacement";
    private static final String  CONFIG_TAG_MARK_BY_ATTRIBUTE_PREFIX      = "Mark by Attribute Prefix";
    private static final String  CONFIG_TAG_MARK_BY_ATTRIBUTE_SUFFIX      = "Mark by Attribute Suffix";
    private static final String  CONFIG_TAG_MARK_BY_ITEMTYPE_REINDEX      = "Mark by Item Type Reindex";
    // Repeats from Export Manager:
    private static final String  CONFIG_TAG_LOG_FILE_DIRECTORY            = TExportManagerICM.CONFIG_TAG_LOG_FILE_DIRECTORY;
    private static final String  CONFIG_TAG_MASTER_PACKAGE_NAME           = TExportManagerICM.CONFIG_TAG_MASTER_PACKAGE_NAME;

    // Master Export Package File Information
    private static final String  MASTER_PACKAGE_VERSION                   = TExportManagerICM.MASTER_PACKAGE_VERSION;
    
    // Import Record FIle Tags
    //--> Use constants in TImportManagerICM

    // Summary File Tags
    private static final String  FILE_TAG_MASTER_SUMMARY_IDENTIFIER       = "<Master Completion Marker Summary>";
    private static final String  FILE_TAG_SECTION_CONFIG_BEGIN            = "CONFIGURATION:";
    private static final String  FILE_TAG_SECTION_CONFIG_HEADER           = "              Setting   Value                                                " + NEWLINE
                                                                          + "----------------------  -----------------------------------------------------";
    private static final String  FILE_TAG_MASTER_PACKAGE_NAME             = "  Master Package Name:  ";
    private static final String  FILE_TAG_DATABASE                        = "       Datastore Name:  ";
    private static final String  FILE_TAG_USERNAME                        = "         CM User Name:  ";
    private static final String  FILE_TAG_CONNOPTS                        = "      Connect Options:  ";
    private static final String  FILE_TAG_MARKERS_COUNT                   = " # Completion Markers:  ";
    private static final String  FILE_TAG_MARKER                          = "               Marker:  ";
    private static final String  FILE_TAG_RETRY_ATTEMPTS                  = "    # Retries / Issue:  ";
    private static final String  FILE_TAG_RETRY_DELAY_MS                  = "Retry Delay Time (ms):  ";
    private static final String  FILE_TAG_SKIP_ITEM_AFTER_ATTEMPT_NUM     = " Skip Pkg After Retry:  ";
    private static final String  FILE_TAG_CONFIG_FILE_NAME                      = "          Config File:  ";
    private static final String  FILE_TAG_MASTER_LOG_FILE_DIRECTORY             = "    Master Log Folder:  ";
    private static final String  FILE_TAG_MASTER_SUMMARY_FILE                   = "       Marker Summary:  ";
    private static final String  FILE_TAG_MASTER_IMPORT_RECORD_FILE             = "  Item Mapping Record:  ";
    private static final String  FILE_TAG_MASTER_TRACKING_FILE                  = " Master Tracking File:  ";
    private static final String  FILE_TAG_SECTION_CONFIG_END                    = "-----------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_STATS_BEGIN                   = "STATISTICS:";
    private static final String  FILE_TAG_SECTION_STATS_HEADER                  = "                Statistic   Value                                            " + NEWLINE
                                                                                + "--------------------------  -------------------------------------------------";
    private static final String  FILE_TAG_STAT_TOTAL_PACKAGES                   = "               # Packages:  ";
    private static final String  FILE_TAG_STAT_TOTAL_ITEMS                      = "        # Completed Items:  ";
    private static final String  FILE_TAG_STAT_TOTAL_UPDATED_ITEMS              = "           # Items Marked:  ";
    private static final String  FILE_TAG_STAT_TOTAL_ALREADY_MARKED_ITEMS       = "   # Items Already Marked:  ";
    private static final String  FILE_TAG_STAT_TOTAL_FAILED_ITEMS               = "   # Items Failed Marking:  ";
    private static final String  FILE_TAG_STAT_NUM_MASTER_LEVEL_ERRORS          = "    # Master-Level Errors:  ";
    private static final String  FILE_TAG_STAT_NUM_PACKAGE_LEVEL_ERRORS         = "   # Package-Level Errors:  ";
    private static final String  FILE_TAG_STAT_NUM_ITEM_LEVEL_ERRORS            = "    # Item-Marking Errors:  ";
    private static final String  FILE_TAG_STAT_START_TIMESTAMP                  = "               Start Time:  ";
    private static final String  FILE_TAG_STAT_END_TIMESTAMP                    = "          Completion Time:  ";
    private static final String  FILE_TAG_SECTION_STATS_END                     = "-----------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_BEGIN  = "PROCESSED PACKAGE LIST:";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_HEADER = "Tag            Package#   # Items           After Item ID                 Last Item ID           Folder                        " + NEWLINE
                                                                                + "-------------  --------  ----------  ----------------------------  ----------------------------  ------------------------------";
    private static final String  FILE_TAG_PACKAGE_INFO                          = "Package Info:  ";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_END    = "-------------------------------------------------------------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_FAILED_ITEM_LIST_BEGIN        = "FAILED ITEMS:";
    private static final String  FILE_TAG_SECTION_FAILED_ITEM_LIST_HEADER       = "Tag           Item Information   Reason                             " + NEWLINE
                                                                                + "------------  -----------------  ----------------------------------------------------------------------------------------------";
    private static final String  FILE_TAG_FAILED_ITEM                           = "Failed Item:  ";
    private static final String  FILE_TAG_SECTION_FAILED_ITEM_LIST_END          = "-------------------------------------------------------------------------------------------------------------------------------";

    // Tracking File Constants
    private static final String  TRACKING_TAG_ALL_PACKAGES_STARTED        = "Starting All Packages:  ";
    private static final String  TRACKING_TAG_ALL_PACKAGES_COMPLETED      = "Completed All Packages:  ";
    private static final String  TRACKING_TAG_FAILURE_MASTER_LEVEL        = "Failure at master-level:  ";
    private static final String  TRACKING_TAG_FAILURE_PACKAGE_LEVEL       = "Failure at package-level:  ";
    private static final String  TRACKING_TAG_FAILURE_ITEM_LEVEL          = "Failure updating item:  ";
    private static final String  TRACKING_TAG_FILE_IDENTIFIER             = "<Completion Marker Tracking File>";
    private static final String  TRACKING_TAG_ITEM_ABANDONED              = "Item Abandoned:  ";
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
    TExportManagerICM_AttemptManager _masterLevelAttemptManager  = null;  // Managed Operation: 
    TExportManagerICM_AttemptManager _packageLevelAttemptManager = null;  // Managed Operation: 
    TExportManagerICM_AttemptManager _itemLevelAttemptManager    = null;  // Managed Operation: 
    // - Internal Variables
    TreeMap<Integer,TImportManagerCompletionMarkerICM_CompletedPackage>
              _completedPackageList      = null;  // Tracks the key information for the summary report on all individual packages that were successful.  Contains TImportManagerCompletionMarkerICM_CompeletedPackage objects located by key.  Key obtained by CompletedPackage.getKey().
    TreeMap<String,TImportManagerCompletionMarkerICM_Marker>
              _completionMarkers         = null;  // Tracks the markers for each item type for how to mark all items of that item type.  The list is accessed through the key obtaind from TImportManagerCompletionMarker.getKey().
    int       _currentPackageNum         = -1;    // The current package number
    TreeMap<String,TImportManagerCompletionMarkerICM_FailedItem>
              _failedItemList            = null;  // Tracks all items that could not be updated.
    File      _masterImportRecordFile    = null;  // File recording all item Id mapping.  This is needed for the Reconciliation and Completion Marker tools.  This is updated after every successful package import.
    File      _masterSummaryFile         = null;  // Summary file written at after all items have been marked.
    File      _masterTrackingFile        = null;  // Tracking file used to restart the tool if it crashes or needs to be restarted later.
    int       _skipItemAfterAttemptNum   = -1;    // After this many attempts due to failures, the tool will give up on the items and move on.
    long      _statNumTotalItems         = -1;    // Number of actual items exported in total.
    long      _statNumUpdatedItems       = -1;    // Number of items actually updated by the tool
    int       _statNumMasterLevelErrors  = -1;    // Number of times that the master-level attempt manager experienced errors (for statistical purposes)
    int       _statNumPackageLevelErrors = -1;    // Number of times that the package-level attempt manager experienced errors (for statistical purposes)
    int       _statNumItemLevelErrors    = -1;    // Number of times that the item-level attempt manager experienced errors (for statistical purposes)
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
            TImportManagerCompletionMarkerICM completionMarker = new TImportManagerCompletionMarkerICM(argv);
            
            // Execute the Import Manager Primary Program
            completionMarker.run();

            //-------------------------------------------------------------
            // Sample program completed without exception
            //-------------------------------------------------------------
            System.out.println("");
            System.out.println("===========================================");
            System.out.println("Import Manager Completion Marker Completed.");
            System.out.println("===========================================");
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
    // Import Manager Operation
    //=================================================================
    // The following are the methods essential to ExportManager's 
    // operation as an object.
    
   /**
    * Preventing creation with no arguments.
    **/
    private TImportManagerCompletionMarkerICM() throws Exception{
        throw new Exception("The TImportManagerCompletionMarkerICM object cannot be created by the no-argument constructor.  Use the constructor that takes the main arguments.");
    }//end CTOR()

   /**
    * Create an instance of the Import Manager.
    * @param commandlineArgs - Command line arguments from main(String[] argsv)
    **/
    public TImportManagerCompletionMarkerICM(String[] commandlineArgs) throws Exception{
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
        _masterLevelAttemptManager = null;
        _packageLevelAttemptManager= null;
        _itemLevelAttemptManager   = null;
        // - Internal Variables
        _completedPackageList      = null;  // Tracks the key information for the summary report on all individual packages that were successful.  Contains TImportManagerCompletionMarkerICM_CompeletedPackage objects located by key.  Key obtained by CompletedPackage.getKey().
        _completionMarkers         = new TreeMap<String,TImportManagerCompletionMarkerICM_Marker>();  // Load now since needed before initObjects() // Tracks the markers for each item type for how to mark all items of that item type.  The list is accessed through the key obtaind from TImportManagerCompletionMarker.getKey().
        _currentPackageNum         = 1;     // Start counting at '1'.
        _failedItemList            = null;  // Tracks all items that could not be updated in this run.
        _masterImportRecordFile    = null;  // File recording all item Id mapping.  This is needed for the Reconciliation and Completion Marker tools.  This is updated after every successful package import.
        _masterSummaryFile         = null;  // Summary file written at after all items have been marked.
        _masterTrackingFile        = null;  // Tracking file used to restart the tool if it crashes or needs to be restarted later.
        _skipItemAfterAttemptNum   = DEFAULT_SKIP_ITEM_AFTER_ATTEMPT_NUM; // After this many attempts due to failures, the tool will give up on the items and move on.
        _statNumTotalItems         = 0;     // Number of actual items exported in total.
        _statNumUpdatedItems       = 0;     // Number of actual items updated.
        _statNumMasterLevelErrors  = 0;     // Number of times that the master-level attempt manager experienced errors (for statistical purposes)
        _statNumPackageLevelErrors = 0;     // Number of times that the package-level attempt manager experienced errors (for statistical purposes)
        _statNumItemLevelErrors    = 0;     // Number of times that the item-level attempt manager experienced errors (for statistical purposes)
        _statStartedTimestamp      = new Timestamp(System.currentTimeMillis());
        _statCompletedTimestamp    = null;  // Time that the tool actually completed the export (for statistical purposes)
        
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
                // Property: Master Package File Name
                else if(property.compareToIgnoreCase(CONFIG_TAG_MASTER_PACKAGE_NAME)==0){
                    if(value.compareTo("")!=0) _masterPackageName = value;  // Only save non-blank values.
                }
                // Property: Skip Item After Attempt Number
                else if(property.compareToIgnoreCase(CONFIG_TAG_SKIP_ITEM_AFTER_ATTEMPT_NUM)==0){
                    if(value.compareTo("")!=0) setSkipItemAfterAttemptNum(value);  // Only save non-blank values.
                }
                // Property: Mark by Attribute Replacement
                else if(property.compareToIgnoreCase(CONFIG_TAG_MARK_BY_ATTRIBUTE_REPLACEMENT)==0){
                    if(value.compareTo("")!=0) addAttributeReplacementSetting(value,lineNum);  // Only save non-blank values.
                }
                // Property: Mark by Attribute Prefix
                else if(property.compareToIgnoreCase(CONFIG_TAG_MARK_BY_ATTRIBUTE_PREFIX)==0){
                    if(value.compareTo("")!=0) addAttributePrefixSetting(value,lineNum);  // Only save non-blank values.
                }
                // Property: Mark by Attribute Prefix
                else if(property.compareToIgnoreCase(CONFIG_TAG_MARK_BY_ATTRIBUTE_SUFFIX)==0){
                    if(value.compareTo("")!=0) addAttributeSuffixSetting(value,lineNum);  // Only save non-blank values.
                }
                // Property: Mark by Attribute Prefix
                else if(property.compareToIgnoreCase(CONFIG_TAG_MARK_BY_ITEMTYPE_REINDEX)==0){
                    if(value.compareTo("")!=0) addItemTypeReindexSetting(value,lineNum);  // Only save non-blank values.
                }
            }//end if(separatorLoc > 0){
        }//end for(int lineNum = 1; (line = file.readLine())!=null); lineNum++){ // Continue until reach end of file.
            
        // Close File
        file.close();
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
        _completedPackageList       = new TreeMap<Integer,TImportManagerCompletionMarkerICM_CompletedPackage>(); // TreeMap of TImportManagerCompletionMarkerICM_CompletedPackage objects with key value CompletedPackage.getKey().
        // Already Initialized for Reading Configuration File:  _completionMarkers          = new TreeMap(); // Tracks the markers for each item type for how to mark all items of that item type.  The list is accessed through the key obtaind from TImportManagerCompletionMarker.getKey().
        _dsICM                      = new DKDatastoreICM(); // Will be created later, but create instance now so we can validate database alias used.
        _options                    = new TExportPackageICM.Options(_iniFileName);
        _failedItemList             = new TreeMap<String,TImportManagerCompletionMarkerICM_FailedItem>(); // TreeMap of TImportManagerCompletionMarkerICM_FailedItem with key value Item Id + version Id.
        _masterImportRecordFile     = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_IMPORT_RECORD_FILE_EXT);
        _masterSummaryFile          = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_SUMMARY_FILE_EXT);
        _masterTrackingFile         = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_TRACKING_FILE_EXT);
        // Attempt Managers need to be loaded in order
        _masterLevelAttemptManager  = new TExportManagerICM_AttemptManager("Master Package Attempt Manager",commandlineArgs);
        _packageLevelAttemptManager = new TExportManagerICM_AttemptManager(_masterLevelAttemptManager,"Single Package Attempt Manager",commandlineArgs);
        _itemLevelAttemptManager    = new TExportManagerICM_AttemptManager(_packageLevelAttemptManager,"Single Item Update Attempt Manager",commandlineArgs);
    }//end initObjects()

   /**
    * As instructions for how to mark a particular item type as complete, 
    * add the setting specified in the string.  
    * 
    * This setting specifies that for the specified item type, the specied
    * attribute will be replaced with the specified value.
    *
    * Setting Format:
    *
    *     <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
    *   
    * @param value   = String format for the setting.
    * @param lineNum = Line number in the configuration file for error reporting.
    **/
    private void addAttributeReplacementSetting(String value, int lineNum) throws IllegalArgumentException{
        // Get Item Type Name from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        int beginIndex = 0;
        int endIndex   = value.indexOf("::");
        if(endIndex<0) throw new IllegalArgumentException("Invalid format of setting for marking items complete by replacing an attribute value.  Missing \"::\" from required string that distinguishes the item type from the attribute at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        String itemTypeName = value.substring(beginIndex,endIndex).trim();
        if((itemTypeName==null)||(itemTypeName.length()==0)) throw new IllegalArgumentException("Missing item type name from setting for marking items complete by replacing an attribute value.  No item type name was found prior to the \"::\" delimiter at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Get Attribute Name from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        beginIndex = endIndex+2; // Start right after the "::" that we just found.
        endIndex   = value.indexOf('=');
        if(endIndex<0) throw new IllegalArgumentException("Invalid format of setting for marking items complete by replacing an attribute value.  Missing \"=\" from required string that distinguishes the attribute name from the desired attribute value at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        String attributeName = value.substring(beginIndex,endIndex).trim();
        if((attributeName==null)||(attributeName.length()==0)) throw new IllegalArgumentException("Missing attribute name from setting for marking items complete by replacing an attribute value.  No attribute name was found after the \"::\" and before the \"=\" delimiters at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Get Value Type from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        beginIndex = value.indexOf('(',endIndex)+1;
        endIndex   = value.indexOf(')',endIndex);
        if(beginIndex<0) throw new IllegalArgumentException("Invalid format of setting for marking items complete by replacing an attribute value.  Missing \"(\" from required string that distinguishes the attribute value type from the desired attribute value at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        if(endIndex<0)   throw new IllegalArgumentException("Invalid format of setting for marking items complete by replacing an attribute value.  Missing \")\" from required string that distinguishes the attribute value type from the desired attribute value at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        String valueType = value.substring(beginIndex,endIndex).trim();
        if((valueType==null)||(valueType.length()==0)) throw new IllegalArgumentException("Missing value type from setting for marking items complete by replacing an attribute value.  No value type was found after the \"::\" and within the \"(\" and \")\" at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Get Attribute Value from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        beginIndex = endIndex + 1;   // Start right after the "=" that we just found.
        endIndex   = value.length(); // Not really needed.  Just get to end of string.
        String attributeValueStr = value.substring(beginIndex).trim();
        if((attributeValueStr==null)||(attributeValueStr.length()==0)) throw new IllegalArgumentException("Missing attribute value from setting for marking items complete by replacing an attribute value.  No attribute value was found after the \"=\" delimiter at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Convert attribute value to the correct type
        Object attributeValueObj = null;
        if(valueType.compareToIgnoreCase("String")==0){
            attributeValueObj = attributeValueStr;
        }else if(valueType.compareToIgnoreCase("Integer")==0){
            try{ attributeValueObj = Integer.valueOf(attributeValueStr); }
            catch(Exception exc){
                throw new IllegalArgumentException("Attribute value does not match the specified type for the setting for marking items complete by replacing an attribute value.  The specified value, '"+attributeValueStr+"', could not be turned into an Integer type due to error \""+exc.getMessage()+"\".  Review line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
            }
        }else if(valueType.compareToIgnoreCase("Short")==0){
            try{ attributeValueObj = Short.valueOf(attributeValueStr); }
            catch(Exception exc){
                throw new IllegalArgumentException("Attribute value does not match the specified type for the setting for marking items complete by replacing an attribute value.  The specified value, '"+attributeValueStr+"', could not be turned into an Short type due to error \""+exc.getMessage()+"\".  Review line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
            }
        }else throw new IllegalArgumentException("Invalid value type from setting for marking items complete by replacing an attribute value.  The specified value type, '"+valueType+"',  is not one of the available options.  The tool only supports String, Integer, and Short.  Review line '"+lineNum+"' of configuration file '"+_iniFileName+"' and its value '"+value+"'.");
        
        // If a setting already exists for this item type, report an error.
        String key = TImportManagerCompletionMarkerICM_Marker.getKey(itemTypeName);
        if(_completionMarkers.containsKey(key))
            throw new IllegalArgumentException("Multiple settings for marking items complete are not allowed when marking method chosen is by replacing an attribute value.  If you choose to update by attribute value, only use one setting for this item type.  An existing setting was already made prior to this additional setting at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Create a new setting object
        TImportManagerCompletionMarkerICM_Marker marker = new TImportManagerCompletionMarkerICM_Marker(itemTypeName);
        marker.setAttributeReplacement(attributeName, attributeValueObj);
        // Add to marker list
        _completionMarkers.put(marker.getKey(),marker);
    }//end addAttributeReplacementSetting()
    
   /**
    * As instructions for how to mark a particular item type as complete, 
    * add the setting specified in the string. 
    * 
    * This setting specifies that for the specified item type, the specied
    * attribute will be updated to include the specified prefix.
    *   
    * Setting Format:
    *
    *     <Item Type Name>::<Attribute Name>+=<Prefix>
    *   
    * @param value   = String format for the setting.
    * @param lineNum = Line number in the configuration file for error reporting.
    **/
    private void addAttributePrefixSetting(String value, int lineNum) throws IllegalArgumentException{
        // Get Item Type Name from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        int beginIndex = 0;
        int endIndex   = value.indexOf("::");
        if(endIndex<0) throw new IllegalArgumentException("Invalid format of setting for marking items complete by adding a prefix to an existing attribute value.  Missing \"::\" from required string that distinguishes the item type from the attribute at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        String itemTypeName = value.substring(beginIndex,endIndex).trim();
        if((itemTypeName==null)||(itemTypeName.length()==0)) throw new IllegalArgumentException("Missing item type name from setting for marking items complete by adding a prefix to an existing attribute value.  No item type name was found prior to the \"::\" delimiter at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Get Attribute Name from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        beginIndex = endIndex+2; // Start right after the "::" that we just found.
        endIndex   = value.indexOf("+=");
        if(endIndex<0) throw new IllegalArgumentException("Invalid format of setting for marking items complete by adding a prefix to an existing attribute value.  Missing \"+=\" from required string that distinguishes the attribute name from the attribute prefix at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        String attributeName = value.substring(beginIndex,endIndex).trim();
        if((attributeName==null)||(attributeName.length()==0)) throw new IllegalArgumentException("Missing attribute name from setting for marking items complete by adding a prefix to an existing attribute value.  No attribute name was found after the \"::\" and before the \"=\" delimiters at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Get Attribute Value from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        beginIndex = endIndex + 2;   // Start right after the "=" that we just found.
        endIndex   = value.length(); // Not really needed.  Just get to end of string.
        String attributeValuePrefix = value.substring(beginIndex).trim();
        if((attributeValuePrefix==null)||(attributeValuePrefix.length()==0)) throw new IllegalArgumentException("Missing attribute value from setting for marking items complete by adding a prefix to an existing attribute value.  No prefix value was found after the \"+=\" delimiter at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // If a setting already exists for this item type, report an error.
        if(_completionMarkers.containsKey(TImportManagerCompletionMarkerICM_Marker.getKey(itemTypeName)))
            throw new IllegalArgumentException("Multiple settings for marking items complete are not allowed.  If you choose to update by attribute value, only use one setting for this item type.  An existing setting was already made prior to this additional setting at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Create a new setting object
        TImportManagerCompletionMarkerICM_Marker marker = new TImportManagerCompletionMarkerICM_Marker(itemTypeName);
        marker.setAttributePrefix(attributeName, attributeValuePrefix);
        // Add to marker list
        _completionMarkers.put(marker.getKey(),marker);
    }//end addAttributePrefixSetting()

   /**
    * As instructions for how to mark a particular item type as complete, 
    * add the setting specified in the string. 
    * 
    * This setting specifies that for the specified item type, the specied
    * attribute will be updated to include the specified suffix.
    *   
    * Setting Format:
    *
    *     <Item Type Name>::<Attribute Name>=+<Suffix>
    *   
    * @param value   = String format for the setting.
    * @param lineNum = Line number in the configuration file for error reporting.
    **/
    private void addAttributeSuffixSetting(String value, int lineNum) throws IllegalArgumentException{
        // Get Item Type Name from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        int beginIndex = 0;
        int endIndex   = value.indexOf("::");
        if(endIndex<0) throw new IllegalArgumentException("Invalid format of setting for marking items complete by adding a suffix to an existing attribute value.  Missing \"::\" from required string that distinguishes the item type from the attribute at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        String itemTypeName = value.substring(beginIndex,endIndex).trim();
        if((itemTypeName==null)||(itemTypeName.length()==0)) throw new IllegalArgumentException("Missing item type name from setting for marking items complete by adding a suffix to an existing attribute value.  No item type name was found prior to the \"::\" delimiter at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Get Attribute Name from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        beginIndex = endIndex+2; // Start right after the "::" that we just found.
        endIndex   = value.indexOf("+=");
        if(endIndex<0) throw new IllegalArgumentException("Invalid format of setting for marking items complete by adding a suffix to an existing attribute value.  Missing \"+=\" from required string that distinguishes the attribute name from the attribute suffix at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        String attributeName = value.substring(beginIndex,endIndex).trim();
        if((attributeName==null)||(attributeName.length()==0)) throw new IllegalArgumentException("Missing attribute name from setting for marking items complete by adding a suffix to an existing attribute value.  No attribute name was found after the \"::\" and before the \"=\" delimiters at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Get Attribute Value from:  <Item Type Name>::<Attribute Name>=(String|Integer|Short)<Attribute Value>
        beginIndex = endIndex + 2;   // Start right after the "=" that we just found.
        endIndex   = value.length(); // Not really needed.  Just get to end of string.
        String attributeValueSuffix = value.substring(beginIndex).trim();
        if((attributeValueSuffix==null)||(attributeValueSuffix.length()==0)) throw new IllegalArgumentException("Missing attribute value from setting for marking items complete by adding a suffix to an existing attribute value.  No suffix value was found after the \"+=\" delimiter at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // If a setting already exists for this item type, report an error.
        if(_completionMarkers.containsKey(TImportManagerCompletionMarkerICM_Marker.getKey(itemTypeName)))
            throw new IllegalArgumentException("Multiple settings for marking items complete are not allowed.  If you choose to update by attribute value, only use one setting for this item type.  An existing setting was already made prior to this additional setting at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Create a new setting object
        TImportManagerCompletionMarkerICM_Marker marker = new TImportManagerCompletionMarkerICM_Marker(itemTypeName);
        marker.setAttributeSuffix(attributeName, attributeValueSuffix);
        // Add to marker list
        _completionMarkers.put(marker.getKey(),marker);
    }//end addAttributeSuffixSetting()

   /**
    * As instructions for how to mark a particular item type as complete, 
    * add the setting specified in the string. 
    * 
    * This setting specifies that for the specified item type, it will
    * be reindexed to the specified item type.
    *   
    * Setting Format:
    *
    *     <Original Item Type Name>-><New Item Type Name>
    *   
    * @param value   = String format for the setting.
    * @param lineNum = Line number in the configuration file for error reporting.
    **/
    private void addItemTypeReindexSetting(String value, int lineNum) throws IllegalArgumentException{
        // Get Original Item Type Name from:  <Original Item Type Name>-><New Item Type Name>
        int beginIndex = 0;
        int endIndex   = value.indexOf("->");
        if(endIndex<0) throw new IllegalArgumentException("Invalid format of setting for marking items complete by reindexing to a new item type.  Missing \"->\" from required string that distinguishes the current/original item type from the new item type at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        String currentItemTypeName = value.substring(beginIndex,endIndex).trim();
        if((currentItemTypeName==null)||(currentItemTypeName.length()==0)) throw new IllegalArgumentException("Missing current/original item type name from setting for marking items complete by reindexing the item to a new item type.  No current/original item type name was found prior to the \"->\" delimiter at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Get New Item Type Name from:  <Original Item Type Name>-><New Item Type Name>
        beginIndex = endIndex+2; // Start right after the "::" that we just found.
        endIndex   = value.length(); // Not really needed.  Just get to end of string.
        String newItemTypeName = value.substring(beginIndex,endIndex).trim();
        if((newItemTypeName==null)||(newItemTypeName.length()==0)) throw new IllegalArgumentException("Missing new item type name from setting for marking items complete by reindexing the item to a new item type.  No new item type name was found after the \"->\" delimiter at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // If a setting already exists for this item type, report an error.
        if(_completionMarkers.containsKey(TImportManagerCompletionMarkerICM_Marker.getKey(currentItemTypeName)))
            throw new IllegalArgumentException("Multiple settings for marking items complete are not allowed.  If you choose to mark by reindexing, only use one setting for this item type.  An existing setting was already made prior to this additional setting at line '"+lineNum+"' of configuration file '"+_iniFileName+"' which contains value '"+value+"'.");
        // Create a new setting object
        TImportManagerCompletionMarkerICM_Marker marker = new TImportManagerCompletionMarkerICM_Marker(currentItemTypeName);
        marker.setNewItemType(newItemTypeName);
        // Add to marker list
        _completionMarkers.put(marker.getKey(),marker);
    }//end addItemTypeReindexSetting()
    
   /**
    * Find all items for the specified package.
    *
    * Execute Query Will:
    *    - Sort based on Item ID (ascending)
    *    - Find items only in the range of the specified package.
    *    - Find only items of the object types of items in the package.
    *
    * @param completedPackage - CompletedPackage information object to issue the query for.
    * @return Returns an open cursor containing all items to appear
    *         in that package.
    **/
    private dkResultSetCursor executePackageQuery(TImportManagerCompletionMarkerICM_CompletedPackage completedPackage) throws InternalError, DKException, Exception{
        // Validate Input
        if(completedPackage==null) throw new InternalError("Internal Error:  Invalid argument to executePackageQuery().  Parameter _completedPackage is 'null'.");

        // First, make sure we have a connected datastore by this point.
        if(_dsICM==null)                throw new InternalError("Inernal Error: An established connection was expected by TImportManagerCompletionMarkerICM.executePackageQuery().  '_dsICM' was found to be 'null'.");
        if(_dsICM.isConnected()==false) throw new InternalError("Inernal Error: An established connection was expected by TImportManagerCompletionMarkerICM.executePackageQuery().  '_dsICM' was found never to have been connected.");
        
        // Build Query String
        String query = getCurrentPackageQuery(completedPackage);
        
        // Execute Query
        System.out.println("--- Executing Query for Package '"+_currentPackageNum+"'...");
        printDebug("    --> <executing...>");
        DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance(_dsICM);
        DKNVPair options[]  = new DKNVPair[3];
                 options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0");  // Find all since we don't know how many will actually turn up and we don't want to miss any.
                 options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY); // Only retrieving IDs for fast results.
                 options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                    // Must mark the end of the NVPair
        dkResultSetCursor cursor = _dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
        printDebug("    --> Done.");

        // Return Results
        return(cursor);
    }//end executePackageQuery()

   /**
    * Generate the necessary query string depending on whether
    * all items of a specified list of item types are in use or 
    * if provided a query string directly.  
    *
    * Generate the query needed for the current package based on
    * the last item ID of the previous package and number of items
    * in a package.
    *
    * Query will:
    *    - Sort based on Item ID (ascending)
    *    - Find items greater than last Item ID of previous package.
    *
    * @param completedPackage - CompletedPackage information object to issue the query for.
    * @return Returns the correct and completed query string
    *         for the master set.
    **/
    private String getCurrentPackageQuery(TImportManagerCompletionMarkerICM_CompletedPackage completedPackage) throws InternalError, Exception{
        System.out.println("--- Building Package '"+_currentPackageNum+"' Query...");
        StringBuffer query = new StringBuffer();
        
        // List all possible item types that are expected in this package.
        TreeMap<String,String> itemTypeList = completedPackage.getItemTypes();
        
        // Validate the list of item types against those now defined in this system.
        validateItemTypeList(itemTypeList);
        
        // Get the beginning and ending package item Ids.
        TExportManagerICM_PackageInfo packageInfo = completedPackage.getPackageInfo();
        String afterItemId = packageInfo.getAfterItemId();
        String lastItemId  = packageInfo.getLastItemId();
        
        // Add list of item types: (/<itemType1> | /<itemType2> | ...)
        if(itemTypeList.size() > 1) // if there are more than one, use paren
            query.append('(');
        // Add each item type:  /<itemTypeName>
        Iterator<String> itemTypeIter = itemTypeList.values().iterator();
        while(itemTypeIter.hasNext()){
            String itemTypeName = itemTypeIter.next();
            query.append('/');
            query.append(itemTypeName);
            printDebug("         Building: "+query.toString());
            // Add "[" of "[@ITEMID.."
            query.append('[');
            printDebug("         Building: "+query.toString());
            // If there is an Item ID that this is after, include that
            if(afterItemId!=null){
                query.append("@ITEMID>\"");      // Add: @ITEMID>"
                query.append(afterItemId);       // add: the item ID.
                query.append("\" AND ");         // Add: " AND 
                printDebug("         Building: "+query.toString());
            }//end if(afterItemId!=null){
            query.append("@ITEMID<=\"");         // Add: @ITEMID<="
            query.append(lastItemId);            // add: the item ID.
            query.append('\"');                  // add: "
            printDebug("         Building: "+query.toString());
            // If there is an attribute update to be made, limit search to those that have
            // not yet been marked.
            TImportManagerCompletionMarkerICM_Marker marker = _completionMarkers.get(TImportManagerCompletionMarkerICM_Marker.getKey(itemTypeName));
            if(marker==null) throw new IllegalArgumentException("Missing marker settings for item type '"+itemTypeName+"'.  Please review your settings in configuration file '"+_iniFileName+"' to ensure that a marking method is specified for this item type.  Ensure that a completion marker is specified for item type '"+itemTypeName+"'.");
            String attrName = marker.getAttributeName();
            if(attrName!=null){ // Only add attribute criteria if such was specified.
                Object attrReplacement = marker.getAttributeReplacementValue();
                String attrPrefix = marker.getAttributePrefix();
                String attrSuffix = marker.getAttributeSuffix();
                // - If replacing value, omit all that already have this value.
                if(attrReplacement!=null){
                    query.append(" AND @");                        // Add:  AND @
                    query.append(attrName);                        // Add:  attribute name
                    query.append("!=");                            // Add:  !=
                    // If a string type, add double quotes
                    boolean isString = false; // Save value for closing quotation marks.
                    if(attrReplacement instanceof String){
                        isString = true;
                        query.append('\"');
                    }//end if(attrReplacementValue instanceof String){
                    query.append(attrReplacement.toString()); // Add:  attribute prefix
                    if(isString) // Add closing double quotes if it is a string type.
                        query.append('\"');
                    printDebug("         Building: "+query.toString());
                }//end if(attrReplacement==true){
                // - If using a prefix, omit all that already have the prefix.
                else if(attrPrefix!=null){
                    query.append(" AND @");        // Add:  AND @
                    query.append(attrName);        // Add:  attribute name
                    query.append(" NOT LIKE \"");  // Add:  NOT LIKE "
                    query.append(attrPrefix);      // Add:  attribute prefix
                    query.append("%\"");           // Add:  %"
                    printDebug("         Building: "+query.toString());
                }//end if(attrPrefix!=null){
                // - If using a suffix, omit all that already have the suffix.
                else if(attrSuffix!=null){
                    query.append(" AND @");        // Add:  AND @
                    query.append(attrName);        // Add:  attribute name
                    query.append(" NOT LIKE \"%"); // Add:  NOT LIKE "%
                    query.append(attrSuffix);      // Add:  attribute prefix
                    query.append("\"");           // Add:  "
                    printDebug("         Building: "+query.toString());
                }//end else if(attrSuffix!=null){
                else throw new InternalError("Internal Error:  Unexpected condition where attribute marking was noted, but no method of marking was recognized at this point.");
            }//end if(attrName!=null){ // Only add attribute criteria if such was specified.
            query.append(']');                 // Add: "]
            printDebug("         Building: "+query.toString());
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
        query.append(" SORTBY(@ITEMID)");
        printDebug("         Building: "+query.toString());
        // Return Generated Query
        System.out.println("    --> QUERY:  "+query.toString());
        return(query.toString());
    }//end getQuery()   

   /**
    * Perform item marking operation.
    **/
    public void markItemsCompleted() throws DKException, Exception{
        
        System.out.println("-----------------------");
        System.out.println("-- Completion Marker --");
        System.out.println("-----------------------");

        // Track Tool Start
        toolStartup();

        // Reset the attempt manager for reattempt connect-level.
        _masterLevelAttemptManager.reset();
        
        // Repeat attempts until no more retry attempts left.
        while(_masterLevelAttemptManager.next()){
        
            try{
                // Connect
                _dsICM = new DKDatastoreICM();
                TExportManagerICM.connect(_dsICM,_database,_userName,_password,_connOpts);
                
                // Now that we have a connection, validate all markers
                validateMarkers();

                // Import All Apackages
                markAllPackages();

                // Write Completion Report
                writeSummaryReport();
                
                // Tell the attempt manager that everything is complete.  The loop should not restart.
                _masterLevelAttemptManager.setComplete();
                
            }catch(Exception exc){
                // Report Error to Log
                track(TRACKING_TAG_FAILURE_MASTER_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                _statNumMasterLevelErrors++; // save for stats
                // The attempt manager will handle error reporting & throwing if needed.
                _masterLevelAttemptManager.handleAttemptFailure(exc);
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
    * Mark all packages.  Only update those that have been fully
    * imported as indicated by the Master Imported Item Mapping file.
    **/
    private void markAllPackages() throws DKException, Exception{

        System.out.println("--------------------------");
        System.out.println("-- Marking All Packages --");
        System.out.println("--------------------------");

        // Report Starting
        trackTime(TRACKING_TAG_ALL_PACKAGES_STARTED);

        // Loop over all possible packages
        // Continue until exportNextPackage reports there
        // is no next package.
        boolean morePackages = true;
        while(morePackages){

            // Reset the attempt manager for reattempt package-level.
            _packageLevelAttemptManager.reset();

            // Repeat attempts until no more retry attempts left.
            while(_packageLevelAttemptManager.next()){

                try{
                    // Import One Package
                    morePackages = markNextPackage();

                    // Tell the attempt manager that everything is complete.  The loop should not restart.
                    _packageLevelAttemptManager.setComplete();

                }catch(Exception exc){
                    // Report Error to Log
                    track(TRACKING_TAG_FAILURE_PACKAGE_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                    _statNumPackageLevelErrors++; // Track for statistics
                        
                    // The attempt manager will handle error reporting & throwing if needed.
                    _packageLevelAttemptManager.handleAttemptFailure(exc);
                }//end }catch(Exception exc){
            }//end while(_packageLevelAttemptManager.next()){

            // Attempt managers restart after surpass single error.
            _masterLevelAttemptManager.reset();
                            
        }//end while(morePackages){

        // Report Completed
        _statCompletedTimestamp = new Timestamp(System.currentTimeMillis());
        trackTime(TRACKING_TAG_ALL_PACKAGES_COMPLETED);

        System.out.println("--- All Available Packages Completed Markup.");
    }//end markAllPackages()
    
   /**
    * Mark the next package.  The current package will be marked as completed,      
    * and if successful, the pointer will move onto the next package.
    * @return Returns true if there are likely more packages after this one.
    **/
    private boolean markNextPackage() throws DKException, Exception{
        boolean morePackages = true; // Assume there are more unless we find otherwise.

        // Get the next package, if one exists.
        printDebug("    --> Getting next package information...");
        TImportManagerCompletionMarkerICM_CompletedPackage completedPackage = getNextCompletedPackage();

        // If there is no next package, return.
        if(completedPackage==null){
            // Double check for internal error
            if(_currentPackageNum <= _completedPackageList.size())
                throw new InternalError("Internal Error:  Did not find the next CompletedPackage object when the current package was determined to be a valid package, '"+_currentPackageNum+"'.");
            printDebug("--- No packages remaining to mark.");
            morePackages = false;
        }//end if(_currentPackageNum > _completedPackageList.size()){
        else{ // Otherwise export the next package since it exists.

            System.out.println("----------------------------------");
            System.out.println("   Marking Package '"+_currentPackageNum+"' / '"+_completedPackageList.size()+"'.");
            System.out.println("----------------------------------");

            // Get next package to import
            // Track Starting New Package
            track(TRACKING_TAG_PACKAGE_STARTED,(new Integer(_currentPackageNum)).toString());
            
            // Mark Package
            printGeneral("--- Marking Package '"+_currentPackageNum+"' / '"+_completedPackageList.size()+"'...");
            markItems(completedPackage);
            printGeneral("--- Completed Marking Package '"+_currentPackageNum+"' / '"+_completedPackageList.size()+"'...");

            // Record Results
            printDebug("    --> Recording success...");
            // - Report to Tracking Log
            track(TRACKING_TAG_PACKAGE_COMPLETED, completedPackage.toString());
            // Allow next package to move on
            _currentPackageNum++;

            // Determine if there is likely a next package.
            if(_currentPackageNum > _completedPackageList.size()){ // If we moved past he last one, we are done.
                morePackages = false;
                System.out.println("--- Completed Package '"+(_currentPackageNum-1)+"'.  No Remaining Packages to Mark.");
            }else{
                morePackages = true;  // We will assume true otherwise.
                System.out.println("--- Completed Package '"+(_currentPackageNum-1)+"'.  More Packages Remain to Mark.");
            }
        }//end else{ // Otherwise export the next package since it exists.
        
        return(morePackages);
    }//end importNextPackage

   /**
    * Mark all items in the specified package that have not yet already beem marked.
    * @param packageInfo - Package Info object for the package items to update.
    **/
    private void markItems(TImportManagerCompletionMarkerICM_CompletedPackage completedPackage) throws DKUsageError, DKException, IOException, Exception{
        TreeMap<String,TExportPackageICM.ImportRecord> completedImportRecords = completedPackage.getImportRecords();
        // Get all items in the original system meeting this the current
        // package's Item ID range, completed item's item types and have
        // not yet been marked as completed.
        dkResultSetCursor cursor = executePackageQuery(completedPackage);
        // Separate out items in teh range that are in the completed list from those
        // that are not in the completed list based on the search results.
        dkCollection itemsToMarkByAttribute = new DKSequentialCollection(); // Split the DDOs between those to be marked by attribute 
        dkCollection itemsToMarkByReindex   = new DKSequentialCollection(); // and those to be marked by reindex
        dkCollection incompleteDDOs = new DKSequentialCollection(); // DDOs that are found in the search results but are not in the completed DDO list.
        DKDDO ddo = null;
        while( (ddo = cursor.fetchNext())!=null ){
            DKPidICM pid = (DKPidICM) ddo.getPidObject();
            // Determine if this DDO was marked as imported by Import Manager
            if(completedImportRecords.containsKey(TExportPackageICM.ImportRecord.getKey(pid.getItemId(),pid.getVersionNumber()))){
                // Determine which method of marking is used with this one.
                TImportManagerCompletionMarkerICM_Marker marker = _completionMarkers.get(TImportManagerCompletionMarkerICM_Marker.getKey(pid.getObjectType()));
                if(marker==null) throw new InternalError("Internal Error:  Missing completion marker for object type '"+pid.getObjectType()+"'.  Be sure that a completion marker has been specified in the configuration file '"+_iniFileName+"' for this object type.");
                // If marking by attribute, add to attribute list
                if(marker.getAttributeName()!=null)
                    itemsToMarkByAttribute.addElement(ddo);
                // Else if marking by reindexing, add to reindex list
                else if(marker.getNewItemType()!=null)
                    itemsToMarkByReindex.addElement(ddo);
                else throw new InternalError("Internal Error:  Fell through expected condition checks.  Marker is not marking by attribute or reindex for item type '"+pid.getObjectType()+"'.");
            }else{
                incompleteDDOs.addElement(ddo); // Otherwise add to the incomplete list.
            }
        }//end while( (ddo = cursor.next())!=null ){
        // If doing an attribute update, update attributes
        if(itemsToMarkByAttribute.cardinality()>0){
            // Retrieve the root level attributes for all.
            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(_dsICM);
            dkRetrieveOptions.baseAttributes(true);
            dkRetrieveOptions.basePropertyAclName(true);
            dkRetrieveOptions.functionCheckOut(true);
            _dsICM.retrieveObjects(itemsToMarkByAttribute,dkRetrieveOptions.dkNVPair());
            // For all completed items, mark the update in the DDO in the original system.
            dkIterator iter = itemsToMarkByAttribute.createIterator();
            while(iter.more()){
                ddo = (DKDDO) iter.next();
                // Determine which attribute to update
                String itemTypeName = ddo.getObjectType();
                if(_completionMarkers.containsKey(TImportManagerCompletionMarkerICM_Marker.getKey(itemTypeName))==false)
                    throw new Exception("Completed item '"+obj2String(ddo)+"' includes an object type that does not match any of the item types specified for completion marks.  Ensure that marking instructions are provided for all active item type view names in this system.");
                TImportManagerCompletionMarkerICM_Marker marker = _completionMarkers.get(TImportManagerCompletionMarkerICM_Marker.getKey(itemTypeName));
                if(marker==null) throw new InternalError("InternalError:  Could not find marker for item type '"+itemTypeName+"' after the list just reported that such a key existed.");
                String attrName     = marker.getAttributeName();
                Object newAttrValue = marker.getAttributeReplacementValue();
                String attrPrefix   = marker.getAttributePrefix();
                String attrSuffix   = marker.getAttributeSuffix();
                // Update attribute value in memory
                short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,attrName);
                if(dataid<=0){ // If not found, report failure
                    // Get the import record
                    DKPidICM pid = (DKPidICM) ddo.getPidObject();
                    TExportPackageICM.ImportRecord importRecord = (TExportPackageICM.ImportRecord) completedPackage.getImportRecords().get(TExportPackageICM.ImportRecord.getKey(pid.getItemId(),pid.getVersionNumber()));
                    if(importRecord==null) throw new InternalError("Internal Error:  Could not locate import record for item Id '"+pid.getItemId()+"', version '"+pid.getVersionNumber()+"'.");
                    String reason = "Attribute '"+attrName+"' is not found in DDO: "+obj2String(ddo);   
                    printDebug("WARNING:  "+reason);
                    recordFailureMarkingItem(completedPackage,importRecord,reason);
                }//end if(dataid<=0){ // If not found, report failure
                else{ // Otherwise continue modifying
                    // Modify attribute value
                    if(newAttrValue!=null){ // if completely replacing the value,
                        ddo.setData(dataid,newAttrValue);
                    }//end if(newAttrValue!=null){ // if completely replacing the value,
                    else{ // Otherwise a prefix and/or suffix is used
                        // Get existing value
                        Object valueObj = ddo.getData(dataid);
                        String valueStr = null;
                        try{ valueStr = (String) valueObj; }                            
                        catch(ClassCastException exc){
                            throw new IllegalArgumentException("Attribute '"+attrName+"' in item '"+obj2String(ddo)+"' was not a string attribute.  Prefixes and suffixes are not supported on anything other than fixed or variable length strings.");
                        }//end catch(ClassCastException exc){
                        // If it is null, replace by empty string.
                        if(valueStr==null)
                            valueStr = "";
                        if(attrPrefix!=null){ // If adding a prefix
                            valueStr = attrPrefix + valueStr;
                        }//end if(attrPrefix!=null){ // If adding a prefix
                        if(attrSuffix!=null){ // If adding a suffix
                            valueStr = valueStr + attrSuffix;
                        }//end if(attrSuffix){ // If adding a suffix
                        // Save updated value.
                        ddo.setData(dataid,valueStr);
                    }//end else{
                    // Save changes
                    // - Reset the attempt manager for reattempt writing.
                    _itemLevelAttemptManager.reset();
                    while(_itemLevelAttemptManager.next()){
                        try{
                            // Verifying that the item is checked out by the current user, 
                            validateCheckedOutByCurrentUser(ddo);
                            // -> Save DDO
                            printDebug("        - Saving Item...");
                            ddo.update(DKConstant.DK_CM_CHECKIN);
                            printDebug("        - Completed Update...");
                            _statNumUpdatedItems++;
                            // Tell the attempt manager that everything is complete.  The loop should not restart.
                            _itemLevelAttemptManager.setComplete();
                        }catch(Exception exc){ // Handle update errors
                            // Report Error to Log
                            track(TRACKING_TAG_FAILURE_ITEM_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                            _statNumItemLevelErrors++; // Track for statistics
                            // The attempt manager will handle error reporting & throwing if needed.
                            _itemLevelAttemptManager.handleAttemptFailure(exc);
                            // If allowed to continue, determine if it is time to skip this 
                            // item and move onto the next.
                            if(_itemLevelAttemptManager.getAttemptNum() > _skipItemAfterAttemptNum){
                                DKPidICM pid = (DKPidICM) ddo.getPidObject();
                                printGeneral("==============================================");
                                printGeneral("SKIPPING ITEM '"+pid.getItemId()+"'");
                                printGeneral("----------------------------------------------");
                                printGeneral("Exceeded '"+_skipItemAfterAttemptNum+"' Failures ");
                                printGeneral("==============================================");
                                // - Note in the trace file that it is abandoned.
                                track(TRACKING_TAG_ITEM_ABANDONED,pid.getItemId());
                                // - Record failed item.
                                TExportPackageICM.ImportRecord importRecord = (TExportPackageICM.ImportRecord) completedPackage.getImportRecords().get(TExportPackageICM.ImportRecord.getKey(pid.getItemId(),pid.getVersionNumber()));
                                if(importRecord==null) throw new InternalError("Internal Error:  Could not locate ImportRecord  for item '"+pid.getItemId()+"', version '"+pid.getVersionNumber()+"'.");
                                recordFailureMarkingItem(completedPackage,importRecord,"Item '"+obj2String(ddo)+"' could not be updated due to error '"+exc.getMessage()+"'.");
                                // - Move onto the next
                                _itemLevelAttemptManager.setComplete();
                            }//end if(_packageLevelAttemptManager.getAttemptNum() > _skipPackageAfterAttemptNum){
                        }//end }catch(Exception exc){ // Handle update errors
                    }//end while(_writeAttemptManager.next()){
                }//end else{ // Otherwise continue modifying
            }//end while(completedDDOsIter.hasNext()){
        }//end if(markByAttribute){
        // Else if doing a reindex, do reindex
        else if(itemsToMarkByReindex.cardinality()>0){ 
            // For all completed items, mark the update in the DDO in the original system.
            dkIterator iter = itemsToMarkByReindex.createIterator();
            while(iter.more()){
                ddo = (DKDDO) iter.next();
                // Determine which item type to change to
                String itemTypeName = ddo.getObjectType();
                if(_completionMarkers.containsKey(TImportManagerCompletionMarkerICM_Marker.getKey(itemTypeName))==false)
                    throw new Exception("Completed item '"+obj2String(ddo)+"' includes an object type that does not match any of the item types specified for completion marks.  Ensure that marking instructions are provided for all active item type view names in this system.");
                TImportManagerCompletionMarkerICM_Marker marker = _completionMarkers.get(TImportManagerCompletionMarkerICM_Marker.getKey(itemTypeName));
                if(marker==null) throw new InternalError("Internal Error:  Could no locate completion marker for item type '"+itemTypeName+"' after it was reported as valid by the sorted list through the key.");
                String newItemType = marker.getNewItemType();
                if(newItemType==null) throw new InternalError("InternalError:  Completion marker that was first beleived to be marking by reindex now doesn't appear to have a new item type setting.");
                // Change Item Type
                // - Reset the attempt manager for reattempt writing.
                _itemLevelAttemptManager.reset();
                while(_itemLevelAttemptManager.next()){
                    try{
                        // -> Save DDO
                        printDebug("        - Checking Out Item...");
                        _dsICM.checkOut(ddo);
                        printDebug("        - <done.>");
                        printDebug("        - Reindexing Item...");
                        _dsICM.moveObject(ddo, newItemType);
                        printDebug("        - <done.>");
                        printDebug("        - Checking In Item...");
                        printDebug("        - <done.>");
                        _dsICM.checkIn(ddo);
                        _statNumUpdatedItems++;
                        // Tell the attempt manager that everything is complete.  The loop should not restart.
                        _itemLevelAttemptManager.setComplete();
                    }catch(Exception exc){ // Handle update errors
                        // Report Error to Log
                        track(TRACKING_TAG_FAILURE_ITEM_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                        _statNumItemLevelErrors++; // Track for statistics
                        // The attempt manager will handle error reporting & throwing if needed.
                        _itemLevelAttemptManager.handleAttemptFailure(exc);
                        // If allowed to continue, determine if it is time to skip this 
                        // item and move onto the next.
                        if(_itemLevelAttemptManager.getAttemptNum() > _skipItemAfterAttemptNum){
                            DKPidICM pid = (DKPidICM)ddo.getPidObject();
                            printGeneral("==============================================");
                            printGeneral("SKIPPING ITEM '"+pid.getItemId()+"'");
                            printGeneral("----------------------------------------------");
                            printGeneral("Exceeded '"+_skipItemAfterAttemptNum+"' Failures ");
                            printGeneral("==============================================");
                            // - Note in the trace file that it is abandoned.
                            track(TRACKING_TAG_ITEM_ABANDONED,pid.getItemId());
                            // - Record failed item.
                            // Get the import record
                            TExportPackageICM.ImportRecord importRecord = completedPackage.getImportRecords().get(TExportPackageICM.ImportRecord.getKey(pid.getItemId(),pid.getVersionNumber()));
                            if(importRecord==null) throw new InternalError("Internal Error:  Could not locate the ImportRecord for item '"+pid.getItemId()+"', version '"+pid.getVersionNumber()+"'.");
                            recordFailureMarkingItem(completedPackage,importRecord,"Item '"+obj2String(ddo)+"' could not be reindexed due to error '"+exc.getMessage()+"'.");
                            // - Move onto the next
                            _itemLevelAttemptManager.setComplete();
                        }//end if(_packageLevelAttemptManager.getAttemptNum() > _skipPackageAfterAttemptNum){
                    }//end }catch(Exception exc){ // Handle update errors
                }//end while(_writeAttemptManager.next()){
            }//end while(completedDDOsIter.hasNext()){
        }//end else if(markByReindex){ 
        else{
            printGeneral("--- All completed items in package '"+_currentPackageNum+"' have already been marked.");   
        }
    }//end markItems()

   /**
    * Get the next PackageInfo object for the next package to be imported.
    * If the user is trying failed packages, first get them from that list.
    * Otherwise pick up after the last completed package.
    * @return Returns the TExportManager_PackageInfo object for the next
    *         package to import.  Returns 'null' if there is no next package.
    **/
    private TImportManagerCompletionMarkerICM_CompletedPackage getNextCompletedPackage(){
        // Search for next package that needs to be marked.  
        TImportManagerCompletionMarkerICM_CompletedPackage completedPackage = null;
        boolean foundNext   = false;
        boolean noMoreExist = false;
        while((foundNext==false) && (noMoreExist==false)){ // continue until we find one
            // Determine if it is on the available list of completed packages.
            if(_completedPackageList.containsKey(TImportManagerCompletionMarkerICM_CompletedPackage.getKey(_currentPackageNum))){
                completedPackage = _completedPackageList.get(TImportManagerCompletionMarkerICM_CompletedPackage.getKey(_currentPackageNum));
                if(completedPackage==null) throw new InternalError("Internal Error:  Could not locate next completed package '"+_currentPackageNum+"' in the total completed package list of size '"+_completedPackageList.size()+"'.  If this happens, it is likely that the program attempted to find a next package to mark when there were no more packages to import.");
                foundNext = true;
            }//end if(_completedPackageList.containsKey(TImportManagerCompletionMarkerICM_CompletedPackage.getKey(_currentPackageNum))){
            else{ // Othwise try next package number
                // If didn't find next, try the next one
                _currentPackageNum++;
            }//end else{ // Othwise try next package number
            
            // If we walked off the end, that means that this was probalby a restart that
            // was retrying aborted packages.  We need to stop of there is no next package
            if(_currentPackageNum > _completedPackageList.size()){
                printDebug("        - No more packages to mark complete.  All have been completed.");
                noMoreExist=true;
            }//end if(_currentPackageNum > _completedPackageList.size()){
        }//end while((foundNext==false) && (noMoreExist==false)){ // continue until we find one

        // If no more exist, return 'null'
        if(noMoreExist)
            completedPackage = null;

        // Return the completed package that we found
        return(completedPackage);
    }//end getNextPackageInfo()

   /**
    * Load the completed package information from the Master Imported Item Mapping
    * file.
    **/
    private void loadPackageInformation() throws FileNotFoundException, IOException, Exception{
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
        TreeMap<String,TExportPackageICM.ImportRecord> currentImportRecords = null;
        for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.
            printDebug("LINE "+lineNum+":  "+line);
        
            // Find a start tag
            if(line.startsWith(TImportManagerICM.IMPORT_RECORD_TAG_SECTION_MAPPING_BEGIN)){
                String value = line.substring(TImportManagerICM.IMPORT_RECORD_TAG_SECTION_MAPPING_BEGIN.length());
                currentPackageInfo = new TExportManagerICM_PackageInfo(value,true,_options);
                // Drop any previous data that was in progress that did not complete with an end tag.
                currentImportRecords = new TreeMap<String,TExportPackageICM.ImportRecord>();
            }
            // When a package is found, put int the package designatded as started
            else if(line.startsWith(TImportManagerICM.IMPORT_RECORD_TAG_PACKAGE_MAPPING)){
                // If no package was started, report format error
                if(currentPackageInfo==null)
                    throw new InternalError("Invalid format of data in Master Imported Item Mapping Record '"+_masterImportRecordFile.getAbsolutePath()+"'.  Found a mapping record at line '"+lineNum+"' where no package start tag found first.");
                // Use object to parse its own string format
                TExportPackageICM.ImportRecord importRecord = new TExportPackageICM.ImportRecord(line);
                // Add to temporary list until the end tag is found.
                currentImportRecords.put(importRecord.getKey(),importRecord);
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
                // If already on list, report error.
                if(_completedPackageList.containsKey(currentPackageInfo.getKey()))
                    throw new InternalError("Unexpected duplicate package section found in Master Imported Item Mapping Record '"+_masterImportRecordFile.getAbsolutePath()+"'.  Found a tag indicating the end of of the list of completed items for package \"endPackageInfo.toString()\" at line "+lineNum+" but the tool has already recognized a section earlier in this file that identified a package by this number.");
                // Save package completed package information to master list
                TImportManagerCompletionMarkerICM_CompletedPackage completedPackage = new TImportManagerCompletionMarkerICM_CompletedPackage(currentPackageInfo,currentImportRecords);
                _completedPackageList.put(completedPackage.getKey(),completedPackage);
                // Update total count
                _statNumTotalItems += currentImportRecords.size();
                // Reset current info to 'null' to indicate no package section open
                currentPackageInfo   = null;
                currentImportRecords = null;
            }
        }//end for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.

        // Close the file
        file.close();

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
        System.out.println("Tool Program:  TImportManagerCompletionMarkerICM");
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
        System.out.println("Tool Program:  TImportManagerCompletionMarkerICM");
        System.out.println("------------------------------------------------");
        if(_masterPackageName.compareToIgnoreCase(DEFAULT_MASTER_PACKAGE_NAME)==0) 
              System.out.println("       Master Name: "+_masterPackageName);
        else  System.out.println("       Master Name: "+_masterPackageName+" (default)");
              System.out.println("         Datastore: "+_database);
              System.out.println("         User Name: "+_userName);
              System.out.println("      Connect Opts: "+_connOpts);
        if(_iniFileName.compareToIgnoreCase(DEFAULT_INI_FILE_NAME)==0)
              System.out.println("       Config File: "+_iniFileName+" (default)");
        else  System.out.println("       Config File: "+_iniFileName);
              System.out.println("       Master Logs: "+_masterLogFileDirectory.getAbsolutePath());
              System.out.println("    Mapping Record: "+_masterImportRecordFile.getAbsolutePath());
        System.out.println("================================================");
        System.out.println("");
    }//end printSettingInformation()

   /**
    * If an item couldn't be updated, after a configurable number of failures,
    * the item will be skipped / abandonded.  This method is called to record
    * the event to inform the user that this item was not updated.
    * @param completedPackage - CompletedPackage object that this item is a part of
    * @param importRecord     - Import Record of the faild item.
    * @param reason           - Explanation of why it couldn't be marked.
    **/
    private void recordFailureMarkingItem(TImportManagerCompletionMarkerICM_CompletedPackage completedPackage, TExportPackageICM.ImportRecord importRecord, String reason){
        // Validate input
        if(completedPackage==null) throw new InternalError("Internal Error:  Invalid argument to recordFailureMarkingItem(completedPackage,importRecord,reason).  parameter 'completdPackage' is 'null'.");
        if(importRecord==null) throw new InternalError("Internal Error:  Invalid argument to recordFailureMarkingItem(completedPackage,importRecord,reason).  parameter 'importRecord' is 'null'.");
        if(reason==null) throw new InternalError("Internal Error:  Invalid argument to recordFailureMarkingItem(completedPackage,importRecord,reason).  parameter 'reason' is 'null'.");
        if(reason.trim().length()==0) throw new InternalError("Internal Error:  Invalid argument to recordFailureMarkingItem(completedPackage,importRecord,reason).  parameter 'reason' is an empty string '"+reason+"'.");
        
        // Create Failed Item object
        TImportManagerCompletionMarkerICM_FailedItem failedItem = new TImportManagerCompletionMarkerICM_FailedItem(completedPackage,importRecord,reason);
        
        // Add to list
        _failedItemList.put(failedItem.getKey(),failedItem);
    }//end recordFailureMarkingItem
    
   /**
    * Execute the Primary Program
    **/
    public void run() throws DKException, Exception{
        
        // Peform the feature requested by the user.

        // This tool only marks imported items as complete.
        markItemsCompleted();
        
    }//end run()

   /**
    * Set the number of attempts after which the tool should give
    * up on a particular item due to errors.  The aborted item
    * will be noted.  Validate for only valid values
    * @param numAsStr - Number of attempts
    **/
    private void setSkipItemAfterAttemptNum(String numAsStr){
        // Convert to int, catching incorrect format in string.
        try{ _skipItemAfterAttemptNum = Integer.valueOf(numAsStr).intValue(); }
        catch(ClassCastException exc){
            throw new IllegalArgumentException("The setting specified, '"+numAsStr+"', for the attempt number after which the tool should skip / abort a particular item due to errors is invalid.  The number must be a whole number, such as 10.  Make sure your value does not include any decimal places and contains only numbers.  Examine your properties file '"+_iniFileName+"' for tag '"+CONFIG_TAG_SKIP_ITEM_AFTER_ATTEMPT_NUM+"'.");
        }
        // Ensure > 0
        if(_skipItemAfterAttemptNum <= 0) throw new IllegalArgumentException("The setting specified, '"+_skipItemAfterAttemptNum+"', for the attempt number after which the tool should skip / abort a particular item due to errors is invalid.  The number must be a positive (>=1) whole number, such as 10.  Make sure your value does not include any decimal places and contains only numbers.  Examine your properties file '"+_iniFileName+"' for tag '"+CONFIG_TAG_SKIP_ITEM_AFTER_ATTEMPT_NUM+"'.");
    }//end setSkipPackageAfterAttemptNum()

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
        
        // Load All Packages Master Package Summary File
        loadPackageInformation();

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
    * Validate if the specified DDO is checked out by the current user.
    * The DDO must be retrieve with at least ATTRONLY retrieve option.
    * @param ddo - DDO to determine if it is checked out by the current user.
    **/
    private void validateCheckedOutByCurrentUser(DKDDO ddo) throws InternalError, Exception{
        // Check the checked out property
        short propid = ddo.propertyId(DKConstantICM.DK_ICM_PROPERTY_CHECKEDOUTUSERID);
        // Report error if no property found
        if(propid<=0)
            throw new InternalError("No lock owner / checked out userid property found.  Either the item was not retrieved with at least ATTRONLY retrieve options or the locked by / checked out user id property is missing.  Item:  "+TImportManagerCompletionMarkerICM.obj2String(ddo)+"");
        // Get the checked out user id
        String lockOwner = (String) ddo.getProperty(propid);
        if(lockOwner==null)
            throw new InternalError("Lock owner / checked out userid property value is missing ('null').  The property was found at propery ID '"+propid+"', but the value was 'null'.  Item:  "+TImportManagerCompletionMarkerICM.obj2String(ddo)+"");
        if(lockOwner.trim().length()==0)
            throw new InternalError("Lock owner / checked out userid property value is missing (empty string '"+lockOwner+"').  The property was found at property ID '"+propid+"', but the value was 'null'.  Item:  "+TImportManagerCompletionMarkerICM.obj2String(ddo)+"");
        // Compare against the current user
        if(lockOwner.compareToIgnoreCase(_userName)!=0)
            throw new Exception("Item is locked by another user.  Could not mark item '"+TImportManagerCompletionMarkerICM.obj2String(ddo)+"' because user "+lockOwner+" currently holds the lock.");
    }//end validateCheckedOutByCurrentUser()

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
    * Once a connection is established, this method validates all markers
    * to ensure that valid item types and attributes were selected.
    **/
    private void validateMarkers() throws DKException, Exception{
        printGeneral("--- Validating Markers...");
        printDebug("    -> <Checking Against Definitions / Loading Cache>");
        // Go through all markers and have them validate themselves
        Iterator<TImportManagerCompletionMarkerICM_Marker> iter = _completionMarkers.values().iterator();
        while(iter.hasNext()){
            TImportManagerCompletionMarkerICM_Marker marker = iter.next();
            marker.validate(_dsICM);
        }//end while(iter.hasNext()){
        printDebug("    -> <Validation Complete>");
    }//end validateMarkers

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
        //   _completedPackageInfoList  : Not Null
        if(_completedPackageList==null)  throw new InternalError("Inernal Error: Object variable '_completedPackageList' is null when it should have been initialized by 'TImportManagerCompletionMarkerICM.initObjects()'.");
        //   _completionMarkers         : Not Null
        if(_completionMarkers==null)  throw new InternalError("Inernal Error: Object variable '_completionMarkers' is null when it should have been initialized by 'TImportManagerCompletionMarkerICM.initObjects()'.");
        //   _failedPackageInfoList     : Not Null
        if(_failedItemList==null)  throw new InternalError("Inernal Error: Object variable '_failedItemList' is null when it should have been initialized by 'TImportManagerCompletionMarkerICM.initObjects()'.");
        //   _masterLevelAttemptManager : Not Null
        if(_masterLevelAttemptManager==null)  throw new InternalError("Inernal Error: Object variable '_masterLevelAttemptManager' is null when it should have been initialized by 'TImportManagerCompletionMarkerICM.initObjects()'.");
        //   _packageLevelAttemptManager: Not Null
        if(_packageLevelAttemptManager==null) throw new InternalError("Inernal Error: Object variable '_packageLevelAttemptManager' is null when it should have been initialized by 'TImportManagerCompletionMarkerICM.initObjects()'.");
        //   _itemLevelAttemptManager: Not Null
        if(_itemLevelAttemptManager==null) throw new InternalError("Inernal Error: Object variable '_itemLevelAttemptManager' is null when it should have been initialized by 'TImportManagerCompletionMarkerICM.initObjects()'.");
        //   _masterImportRecordFile    : Not Null & Exists
        if(_masterImportRecordFile==null) throw new InternalError("Inernal Error: Object variable '_masterImportRecordFile' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        if(_masterImportRecordFile.exists()==false) throw new IllegalArgumentException("Cannot locate the Master Import Mapping Record file '"+_masterImportRecordFile.getAbsolutePath()+"' in the specified Master Log File Directory '"+_masterLogFileDirectory.getAbsolutePath()+"'.  This file should have been created by TImportManagerICM as it completed packages.  Provide a partial or complete mapping file.");
        //   _masterSummaryFile         : Not Null
        if(_masterSummaryFile==null)          throw new InternalError("Inernal Error: Object variable '_masterSummaryFile' is null when it should have been initialized by 'TImportManagerCompletionMarkerICM.initObjects()'.");
        //   _masterTrackingFile        : Not Null
        if(_masterTrackingFile==null)         throw new InternalError("Inernal Error: Object variable '_masterTrackingFile' is null when it should have been initialized by 'TImportManagerCompletionMarkerICM.initObjects()'.");
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
        fileStr.append(_completedPackageList.size());
        fileStr.append(newline);
        // - # Total Items
        fileStr.append(FILE_TAG_STAT_TOTAL_ITEMS);
        fileStr.append(_statNumTotalItems);
        fileStr.append(newline);
        // - # Updated Items
        fileStr.append(FILE_TAG_STAT_TOTAL_UPDATED_ITEMS);
        fileStr.append(_statNumUpdatedItems);
        fileStr.append(newline);
        // - # Already Marked
        fileStr.append(FILE_TAG_STAT_TOTAL_ALREADY_MARKED_ITEMS);
        fileStr.append(_statNumTotalItems - _statNumUpdatedItems - _failedItemList.size());
        fileStr.append(newline);
        // - # Failed Items
        fileStr.append(FILE_TAG_STAT_TOTAL_FAILED_ITEMS);
        fileStr.append(_failedItemList.size());
        fileStr.append(newline);
        // - # Master-Level Errors
        fileStr.append(FILE_TAG_STAT_NUM_MASTER_LEVEL_ERRORS);
        fileStr.append(_statNumMasterLevelErrors);
        fileStr.append(newline);
        // - # Package-Level Errors
        fileStr.append(FILE_TAG_STAT_NUM_PACKAGE_LEVEL_ERRORS);
        fileStr.append(_statNumPackageLevelErrors);
        fileStr.append(newline);
        // - # Item-Level Errors
        fileStr.append(FILE_TAG_STAT_NUM_ITEM_LEVEL_ERRORS);
        fileStr.append(_statNumItemLevelErrors);
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
        printDebug("        - <Preparing Completed Package List>...");
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_HEADER);
        fileStr.append(newline);
        // Loop through all packages, write thier package info.
        Iterator<TImportManagerCompletionMarkerICM_CompletedPackage> completedPackageIter = _completedPackageList.values().iterator();
        while(completedPackageIter.hasNext()){
            TImportManagerCompletionMarkerICM_CompletedPackage completedPackage = completedPackageIter.next();
            TExportManagerICM_PackageInfo packageInfo = completedPackage.getPackageInfo();
            // Write package description
            fileStr.append(FILE_TAG_PACKAGE_INFO);
            fileStr.append(packageInfo.toString());
            fileStr.append(newline);
        }//end while(packageInfoIter.hasNext()){
        // - Write end section tag.        
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write Failed & Aborted Item List
        printDebug("        - <Preparing Failed/Aborted Item List>...");
        fileStr.append(FILE_TAG_SECTION_FAILED_ITEM_LIST_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_FAILED_ITEM_LIST_HEADER);
        fileStr.append(newline);
        // Loop through all packages, write thier package info.
        Iterator<TImportManagerCompletionMarkerICM_FailedItem> failedItemIter = _failedItemList.values().iterator();
        while(failedItemIter.hasNext()){
            TImportManagerCompletionMarkerICM_FailedItem failedItem = failedItemIter.next();
            // Write package description
            fileStr.append(FILE_TAG_FAILED_ITEM);
            fileStr.append(failedItem.toString());
            fileStr.append(newline);
        }//end while(packageInfoIter.hasNext()){
        // - Write end section tag.        
        fileStr.append(FILE_TAG_SECTION_FAILED_ITEM_LIST_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write to Disk
        printDebug("    --> <Preparing to Write>...");
        // - Double check that there is no existing file by this name.  It should have been
        //   deleted at the beginning of exportAllPackages()
        if(_masterSummaryFile.exists()){
            throw new InternalError("Internal Error:  The Master Completion Marker Process Summary file '"+_masterSummaryFile.getAbsolutePath()+"' should have been deleted at the beginning of TImportMangerCompletionMarkerICM.markItemsComplete().  However, it exists prior to writing the summary report in the TImportManagerCompletionMarkerICM.writeSummaryReport() method.");
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
            printError("ERROR WRITING COMPLETION MARKER PROCESS SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"' due to: "+exc.getMessage());
            // If it was because it ran out of space, report specialized error
            if(exc.getMessage().compareToIgnoreCase(TExportManagerICM.EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                throw new IOException("RAN OUT OF DISK SPACE WRITING COMPLETION MARKER SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"'.  The marking process had completed successfully, but the tool could not write the summary file.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
            }
            throw new IOException("ERROR WRITING COMPLETION MARKER PROCESS SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"' due to error \""+exc.getMessage()+"\".  The marking process had completed successfully, but the tool could not write the summary file.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when the error can be bypassed on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
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
        // - Retry Attempts
        //Not recorded in this object:  fileStr.append(FILE_TAG_RETRY_ATTEMPTS);
        //Not recorded in this object:  fileStr.append(tbd);
        //Not recorded in this object:  fileStr.append(newline);
        // - Retry Delay Time (ms)
        //Not recorded in this object:  fileStr.append(FILE_TAG_RETRY_DELAY_MS);
        //Not recorded in this object:  fileStr.append(tbd);
        //Not recorded in this object:  fileStr.append(newline);
        // - Retry Aborted Packages
        //Not recorded in this object.
        // - Skip Package After Attempt Num
        //Not recorded in this object.
        // - Markers
        fileStr.append(FILE_TAG_MARKERS_COUNT);
        fileStr.append(newline);
        Iterator<TImportManagerCompletionMarkerICM_Marker> iter = _completionMarkers.values().iterator();
        while(iter.hasNext()){
            TImportManagerCompletionMarkerICM_Marker marker = iter.next();
            fileStr.append(FILE_TAG_MARKER);
            fileStr.append(marker.toString());
            fileStr.append(newline);
        }//end while(iter.hasNext()){
        // - Configuration File
        fileStr.append(FILE_TAG_CONFIG_FILE_NAME);
        fileStr.append(_iniFileName);
        fileStr.append(newline);
        // - Master Log File Location
        fileStr.append(FILE_TAG_MASTER_LOG_FILE_DIRECTORY);
        fileStr.append(_masterLogFileDirectory.getAbsolutePath());
        fileStr.append(newline);
        // - Master Export Package File
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

   /** 
    * Create a quick single-line description of a DKDDO object.
    * Format:  DKDDO*<<objectType>[@ITEMID="<itemid>"]>
    **/
    public static String obj2String(DKDDO ddo)
    {
        if(ddo==null) return("DKDDO<NULL>");
        String type = "";
        // Determine Type
             if(ddo instanceof DKVideoStreamICM) type = "DKVideoStreamICM";
        else if(ddo instanceof DKStreamICM)      type = "DKStreamICM";
        else if(ddo instanceof DKTextICM)        type = "DKTextICM";
        else if(ddo instanceof DKImageICM)       type = "DKImageICM";
        else if(ddo instanceof DKLobICM)         type = "DKLobICM";
        else                                     type = "DKDDO";
          
        DKPid pid = ddo.getPidObject();
        if(pid==null) return(type+"<PID IS NULL>");
        String objectType = pid.getObjectType();
        try{
            // If was a DKPidICM, no class-cast exception
            DKPidICM pidICM = (DKPidICM) pid;
            String   itemId = pidICM.getItemId();
            String   verId  = pidICM.getVersionNumber();
            return(type+"<"+objectType+"[@ITEMID=\""+itemId+"\" AND @VERSION="+verId+"]>");
        }catch(ClassCastException exc){
            // If it was NOT a DKPidICM, there would be an access voilation.
            return(type+"<"+objectType+"[NOT ICM PID:'"+exc.getMessage()+"']>");
        }
    }//end obj2String: DKDDO

    //=================================================================
    // Internal Classes
    //=================================================================
   
}//end class TImportManagerCompletionMarkerICM

/************************************************************************************************
 *             CLASS: TImportManagerCompletionMarkerICM_CompletedPackage
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Object responsible for tracking information about a completed package
 *                    used by TImportManagerCompletionMarkerICM and TImportManagerReconcilerICM.
 ************************************************************************************************/
class TImportManagerCompletionMarkerICM_CompletedPackage{
    
    // Constants

    // Variables
    TExportManagerICM_PackageInfo                  _packageInfo   = null; // Describes the package that is marked as completed.
    TreeMap<String,TExportPackageICM.ImportRecord> _importRecords = null; // Import records of all individual items completed access by key item id + version id.

   /**
    * Preventing creation with no arguments.
    **/
    private TImportManagerCompletionMarkerICM_CompletedPackage() throws Exception{
        throw new Exception("The TImportManagerCompletionMarkerICM_CompletedPackage object cannot be created by the no-argument constructor.  Use the constructor that takes the main arguments.");
    }//end CTOR()

   /**
    * Create an instance of the Completed Package.
    * @param packageInfo   - PackageInfo object describing the completed pacakge.
    * @param importRecords - ImportRecords of all items completed for this package, accessed by key of itemid + versionid.
    **/
    public TImportManagerCompletionMarkerICM_CompletedPackage(TExportManagerICM_PackageInfo packageInfo, TreeMap<String,TExportPackageICM.ImportRecord> importRecords){
        initCTOR(packageInfo, importRecords);
    }//end CTOR(String name, String[] commandlineArgs)    

   /**
    * Initialize all variables.  This should be called by every valid constructor.
    * @param packageInfo   - PackageInfo object describing the completed pacakge.
    * @param importRecords - ImportRecords of all items completed for this package, accessed by key of itemid + versionid.
    **/
    public void initCTOR(TExportManagerICM_PackageInfo packageInfo, TreeMap<String,TExportPackageICM.ImportRecord> importRecords){
        // Start off with defaults
        initByDefaults();
        // Save Parameter Variables
        _packageInfo        = packageInfo;
        _importRecords      = importRecords;
        
        // Validate Data
        validateSettings();
    }//end init(ParentManager, String name, String[] commandlineArgs)    

   /**
    * Start off with the defaults.  These can be later overridden.
    **/
    private void initByDefaults(){
        _packageInfo   = null;
        _importRecords = null;
    }//end initByDefaults()

   /**
    * Create any object needed.
    **/
    private void initObjects(){
        // Create Objects
        // No objects to be created
    }//end initObjects()


   /**
    * Gets the list of import records for all completed items in this package.
    * @return Returns a TreeMap of all ImportRecords accessed by key of Item Id + Version Id.
    **/
    public java.util.TreeMap<String,TExportPackageICM.ImportRecord> getImportRecords(){
        return(_importRecords);
    }//end getImportRecords()

   /** 
    * Get the list of item type names (base or view names) specified
    * in the Import Records.
    * @return Returns a sorted TreeMap based on key of the item type or view name.
    **/
    public TreeMap<String,String> getItemTypes(){
        TreeMap<String,String> itemTypes = new TreeMap<String,String>();
        // Go through all Import Records
        Iterator iter = _importRecords.values().iterator();
        while(iter.hasNext()){
            TExportPackageICM.ImportRecord importRecord = (TExportPackageICM.ImportRecord) iter.next();
            String itemType = importRecord.getItemType();
            // If the item type name is not yet recorded, add it to the list.
            if(itemTypes.containsKey(getItemTypesKey(itemType))==false)
                itemTypes.put(getItemTypesKey(itemType),itemType);
        }//end while(iter.hasNext()){
        return(itemTypes);
    }//end getItemTypes

   /**
    * Constructs the key for finding an item type among the item type list
    * returned by getItemTypes().  The key is used to find an item type
    * in sorted TreeMap lists.
    * @param itemTypeName - Name of the item type to construct the key for.
    * @return Returns the key to find the item type among sorted lists.
    **/
    public static String getItemTypesKey(String itemTypeName){
        return(itemTypeName.toUpperCase());
    }//end getKey()

   /**
    * Gets the key for use with this object in sorted TreeMap lists.
    * @return Returns the key to find this package among sorted lists.
    **/
    public Integer getKey(){
        return(_packageInfo.getKey());
    }//end getKey()

   /**
    * If you do not have the instance of the CompletedPackage object,
    * use this method to construct a key.  It Gets the key for getting
    * the corresponding object in sorted TreeMap lists.
    * @param packageNum - Number of the package to create the key for.
    * @return Returns the key to find this package among sorted lists.
    **/
    public static Integer getKey(int packageNum){
        return(TExportManagerICM_PackageInfo.getKey(packageNum));
    }//end getKey(int packageNum)

   /**
    * Get the package info object that describes the completed package.
    * @return Returns the PackageInfo.
    **/
    public TExportManagerICM_PackageInfo getPackageInfo(){
        return(_packageInfo);
    }//end getPackageInfo()

   /**
    * Get the package number.
    * @return Returns the number of this package.
    **/
    public int getPackageNum(){
        return(_packageInfo.getPackageNum());
    }//end getPackageNum()

   /**
    * Write the contents to a String.  Reuses PackageInfo.toString()
    * @return Returns a string in the format specified by PackageInfo.toString().
    **/
    public String toString(){
        if(_packageInfo==null)
            return("TImportManagerCOmpletionMarkerICM_CompletedPackage<NULL>");
        else
            return(_packageInfo.toString());
    }//end toString()

   /**
    * Validate the settings of this object.
    **/
    private void validateSettings(){
        // Validate Input
        if(_packageInfo==null)                  throw new InternalError("Internal Error Creating CompletedPackage:  Variable '_packageInfo' is 'null'.  A valid PackageInfo object instance is required.");
        if(_importRecords==null)                throw new InternalError("Internal Error Creating CompletedPackage:  Variable '_importRecords' is 'null'.  A valid isntance containing zero of more Import Records is required.");
    }//end validateSettings()
   
}//end class TImportManagerCompletionMarkerICM_CompletedPackage
              
/************************************************************************************************
 *             CLASS: TImportManagerCompletionMarkerICM_FailedItem
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Object responsible for tracking information about a single failed item
 *                    that could not be updated by TImportManagerCompletionMarkerICM
 ************************************************************************************************/
class TImportManagerCompletionMarkerICM_FailedItem{
    
    // Constants

    // Variables
    TExportPackageICM.ImportRecord                     _importRecord     = null;
    TImportManagerCompletionMarkerICM_CompletedPackage _completedPackage = null; 
    String                                             _reason           = null;

   /**
    * Preventing creation with no arguments.
    **/
    private TImportManagerCompletionMarkerICM_FailedItem() throws Exception{
        throw new Exception("The TImportManagerCompletionMarkerICM_FailedItem object cannot be created by the no-argument constructor.");
    }//end CTOR()

   /**
    * Create an instance of the Failed Item
    * @param completedPackage - Compelted Package that this item belongs to.
    * @param importRecord     - Import record of the failed item.
    * @param reason           - Reason why the item could not be updated.
    **/
    public TImportManagerCompletionMarkerICM_FailedItem(TImportManagerCompletionMarkerICM_CompletedPackage completedPackage, TExportPackageICM.ImportRecord importRecord, String reason){
        // Validate Input
        if(completedPackage==null) throw new InternalError("Internal Error:  Invalid parameter to FailedItem constuctor.  'completedPackage' is null.");
        if(importRecord    ==null) throw new InternalError("Internal Error:  Invalid parameter to FailedItem constuctor.  'importRecord' is null.");
        if(reason==null) throw new InternalError("Internal Error:  Invalid parameter to FailedItem constuctor.  'reason' is null.");
        if(reason.trim().length()==0) throw new InternalError("Internal Error:  Invalid parameter to FailedItem constuctor.  'reason' is an empty string '"+reason+"'.");

        // Save input
        _completedPackage = completedPackage;
        _importRecord     = importRecord;
        _reason           = reason;
    }//end CTOR(args)

   /**
    * Gets the key for use with this object in sorted TreeMap lists.
    * @return Returns the key to find this package among sorted lists.
    **/
    public String getKey(){
        return(_importRecord.getKey());
    }//end getKey()

   /**
    * If you do not have the instance of the FailedItem object,
    * use this method to construct a key.  It Gets the key for getting
    * the corresponding object in sorted TreeMap lists.
    * @param itemId - Item ID of the original item.
    * @param verId  - Version ID of the original item.
    * @return Returns the key to find this package among sorted lists.
    **/
    public String getKey(String itemId, String verId){
        return(TExportPackageICM.ImportRecord.getKey(itemId.toUpperCase(),verId));
    }//end getKey(String itemId, String verId)

   /**
    * Writes a one-line description of the failed item.
    * @return Returns a one-line description of this failed item.
    **/
    public String toString(){
        StringBuffer str = new StringBuffer();
        str.append(_importRecord.toString());
        str.append("  REASON:  ");
        str.append(_reason);
        return(str.toString());
    }//end toString()

   /**
    * Validate the settings of this object.
    **/
    private void validateSettings(){
        // Validate Input
        if(_completedPackage==null)  throw new InternalError("Internal Error Creating FailedItem:  Variable '_completedPackage' is 'null'.  A valid PackageInfo object instance is required.");
        if(_importRecord==null)      throw new InternalError("Internal Error Creating FailedItem:  Variable '_importRecords' is 'null'.  A valid isntance containing zero of more Import Records is required.");
    }//end validateSettings()
   
}//end class TImportManagerCompletionMarkerICM_FailedItem

              
/************************************************************************************************
 *             CLASS: TImportManagerCompletionMarkerICM_Marker
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Object responsible for tracking the setting for a particular item type
 *                    for how to mark items of that item type as completed.  Used only by
 *                    TImportManagerCompletionMarkerICM.
 ************************************************************************************************/
class TImportManagerCompletionMarkerICM_Marker{
    
    // Constants

    // Variables
    String _attrName        = null; // Name of attribute that will be marked if marking by suffix, prefix, or replacement value.  'null' if no attribute modification specified.
    Object _attrReplacement = null; // Replacement value to replace the existing value of for the specified attribute.  'null' no replacement value specified.
    String _attrPrefix      = null; // Prefix to add to the beginning of any exisitng String attribute value.  'null' if no prefix specified.
    String _attrSuffix      = null; // Suffix to add to the end of any exisitng String attribute value.  'null' if no suffix specified.
    String _currentItemType = null; // Name of the current item type for which this marker applies.
    String _newItemType     = null; // New item type to reindex the original item type to.  'null' if no new item type specified for reindexing.

   /**
    * Preventing creation with no arguments.
    **/
    private TImportManagerCompletionMarkerICM_Marker() throws Exception{
        throw new Exception("The TImportManagerCompletionMarkerICM_Marker object cannot be created by the no-argument constructor.  Use the constructor that takes the main arguments.");
    }//end CTOR()

   /**
    * Create an instance of the Marker.
    * @param itemTypeName - The item type name for which the setting applies.
    **/
    public TImportManagerCompletionMarkerICM_Marker(String itemTypeName){
        initCTOR(itemTypeName);
    }//end CTOR(String itemTypeName)    

   /**
    * Initialize all variables.  This should be called by every valid constructor.
    * @param itemTypeName - The item type name for which the setting applies.
    **/
    public void initCTOR(String itemTypeName){
        // Start off with defaults
        initByDefaults();
        // Save Parameter Variables
        _currentItemType = itemTypeName;
        
        // Validate Data
        validateSettings();
    }//end init(ParentManager, String name, String[] commandlineArgs)    

   /**
    * Start off with the defaults.  These can be later overridden.
    **/
    private void initByDefaults(){
        _attrName        = null; // No default.  This can be set or left 'null'
        _attrReplacement = null; // No default.  This can be set or left 'null'
        _attrPrefix      = null; // No default.  This can be set or left 'null'
        _attrSuffix      = null; // No default.  This can be set or left 'null'
        _currentItemType = null; // No default.  This is required.
        _newItemType     = null; // No default.  This can be set or left 'null'
    }//end initByDefaults()

   /**
    * Create any object needed.
    **/
    private void initObjects(){
        // Create Objects
        // No objects to be created
    }//end initObjects()

   /**
    * Get the name of the attribute in this item type that this 
    * marker will use for its marking.  If this marker is not set
    * to modify an attribute, 'null' is returned.
    * @return Returns the name of the attribute to mark or 'null'
    *         if not set to modify an attribute.
    **/
    public String getAttributeName(){
        return(_attrName);
    }//end getAttributeName()

   /**
    * Get the new attribute value to repalce the existing attribute value
    * wit for the specified attribute. If this marker is not set to
    * use a replacement value for marking, 'null' is returned.
    * @return Returns the replacement attribute value use to mark the
    *         specified attribute or 'null' if not set to replace an
    *         and attribute value.
    **/
    public Object getAttributeReplacementValue(){
        return(_attrReplacement);
    }//end getAttributeReplacementValue()

   /**
    * Get the prefix to add to the beginning of the existing String value
    * for specified attribute.  If this marker is not set to use a prefix
    * for marking, 'null' is returned.
    * @return Returns the prefix to use to mark the specified attribute
    *         or 'null' if not set to add a prefix.
    **/
    public String getAttributePrefix(){
        return(_attrPrefix);
    }//end getAttributePrefix()

   /**
    * Get the suffix to add to the end of the existing String value
    * for the specified attribute. If this marker is not set to
    * use a suffix for marking, 'null' is returned.
    * @return Returns the suffix to use to mark the specified attribute
    *         or 'null' if not set to add a suffix.
    **/
    public String getAttributeSuffix(){
        return(_attrSuffix);
    }//end getAttributeSuffix()

   /**
    * Get the current item type name for which this marker applies.
    * @return Returns the current / original item type name.
    **/
    public String getItemType(){
        return(_currentItemType);
    }//end getItemType()

   /**
    * Get the new item type that the original item type should be
    * reindexed to using this marker.  If this marker is not set 
    * to reindex, 'null' is returned.
    * @return Returns the name of the new item type or 'null' if not 
    *         set to reindex.
    **/
    public String getNewItemType(){
        return(_newItemType);
    }//end getNewItemType()

   /**
    * Gets the key for use with this object in any sorted list.
    * @return Returns the key to find this among sorted lists.
    **/
    public String getKey(){
        return(_currentItemType.toUpperCase());
    }//end getKey()

   /**
    * If you don't have the instance of the Marker object and cannot
    * use the no-argument non-static method, use this method to construct
    * a key for use with this object in any sorted list.
    * @param currentItemTypeName - The current name of the item type that
    *                              this marker is for.
    * @return Returns the key to find this among sorted lists.
    **/
    public static String getKey(String currentItemTypeName){
        return(currentItemTypeName.toUpperCase());
    }//end getKey(String currentItemTypeName)

   /**
    * Set the attribute replacement value to use with this marker.
    * @param attrName  - Name of attribute to use the prefix with
    * @param attrValue - Value to replace any existing value with.
    **/
    public void setAttributeReplacement(String attrName, Object attrValue){
        // Validate Input
        if(attrName==null)              throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrName' to Marker.setAttributeReplacement(String attrName, Object attrValue).  Attribute name was 'null'.");
        if(attrName.trim().length()==0) throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrName' to Marker.setAttributeReplacement(String attrName, Object attrValue).  Attribute name was an empty string '"+attrName+"'.");
        if(attrValue==null)             throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrValue' to Marker.setAttributeReplacement(String attrName, Object attrValue).  Attribute value was 'null'.  Only non-null values allowed.  Attribute name parameter was '"+attrName+"'.");

        // Make sure not set to prefix or suffix
        if(_attrPrefix!=null) throw new InternalError("Internal Error:  This marker for itme type '"+_currentItemType+"' is already set to add a prefix '"+_attrPrefix+"' to attribute '"+_attrName+"'.  A marker cannot support other methods of marking if marking by attribute replacement is selected.");
        if(_attrSuffix!=null) throw new InternalError("Internal Error:  This marker for itme type '"+_currentItemType+"' is already set to add a suffix '"+_attrSuffix+"' to attribute '"+_attrName+"'.  A marker cannot support other methods of marking if marking by attribute replacement is selected.");
        
        // Make sure not set to reindex
        if(_newItemType!=null) throw new InternalError("Internal Error:  This marker for item type '"+_currentItemType+"' is already set to reindex to type '"+_newItemType+"'.  A marker cannot support both methods of marking.");
        
        // Save prefix value. This object supports allowing a prefix and a suffix.
        _attrName        = attrName;
        _attrReplacement = attrValue;
    }//end setAttributeReplacement

   /**
    * Set the attribute prefix to use with this marker.
    * @param attrName   - Name of attribute to use the prefix with
    * @param attrPrefix - Prefix to add to the beginning of an existing String attribute value.
    **/
    public void setAttributePrefix(String attrName, String attrPrefix){
        // Validate Input
        if(attrName==null)                throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrName' to Marker.setAttributePrefix(String attrName, String prefix).  Attribute name was 'null'.");
        if(attrName.trim().length()==0)   throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrName' to Marker.setAttributePrefix(String attrName, String prefix).  Attribute name was an empty string '"+attrName+"'.");
        if(attrPrefix==null)              throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrPrefix' to Marker.setAttributePrefix(String attrName, String prefix).  Attribute prefix was 'null'.  Attribute name parameter was '"+attrName+"'.");
        if(attrPrefix.trim().length()==0) throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrPrefix' to Marker.setAttributePrefix(String attrName, String prefix).  Attribute prefix was an empty string '"+attrPrefix+"'.  Attribute name parameter was '"+attrName+"'.");

        // Make sure not set to replacement
        if(_attrReplacement!=null) throw new InternalError("Internal Error:  This marker for itme type '"+_currentItemType+"' is already set to replace an attribute '"+_attrName+"' with value '"+_attrReplacement+"'.  A marker cannot support other methods of marking if marking by attribute replacement is already selected.");
        
        // Make sure not set to reindex
        if(_newItemType!=null) throw new InternalError("Internal Error:  This marker for item type '"+_currentItemType+"' is already set to reindex to type '"+_newItemType+"'.  A marker cannot support both methods of marking.");
        
        // Save prefix value. This object supports allowing a prefix and a suffix.
        _attrName   = attrName;
        _attrPrefix = attrPrefix;
    }//end setAttributePrefix

   /**
    * Set the attribute suffix to use with this marker.
    * @param attrName   - Name of attribute to use the prefix with
    * @param attrSuffix - Suffix to append to String attibutes.
    **/
    public void setAttributeSuffix(String attrName, String attrSuffix){
        // Validate Input
        if(attrName==null)                throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrName' to Marker.setAttributeSuffix(String attrName, String suffix).  Attribute name was 'null'.");
        if(attrName.trim().length()==0)   throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrName' to Marker.setAttributeSuffix(String attrName, String suffix).  Attribute name was an empty string '"+attrName+"'.");
        if(attrSuffix==null)              throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrSuffix' to Marker.setAttributeSuffix(String attrName, String suffix).  Attribute suffix was 'null'.  Attribute name parameter was '"+attrName+"'.");
        if(attrSuffix.trim().length()==0) throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'attrSuffix' to Marker.setAttributeSuffix(String attrName, String suffix).  Attribute suffix was an empty string '"+attrSuffix+"'.  Attribute name parameter was '"+attrName+"'.");

        // Make sure not set to replacement
        if(_attrReplacement!=null) throw new InternalError("Internal Error:  This marker for itme type '"+_currentItemType+"' is already set to replace an attribute '"+_attrName+"' with value '"+_attrReplacement+"'.  A marker cannot support other methods of marking if marking by attribute replacement is already selected.");
        
        // Make sure not set to reindex
        if(_newItemType!=null) throw new InternalError("Internal Error:  This marker for item type '"+_currentItemType+"' is already set to reindex to type '"+_newItemType+"'.  A marker cannot support both methods of marking.");
        
        // Save prefix value. This object supports allowing a prefix and a suffix.
        _attrName   = attrName;
        _attrSuffix = attrSuffix;
    }//end setAttributeSuffix

   /**
    * Set the new item type to reindex to with this marker.
    * @param newItemType - Item type to reindex all items of the original item type to.
    **/
    public void setNewItemType(String newItemType){
        // Validate Input
        if(newItemType==null)              throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'newItemType' to Marker.setNewItemType(String newItemType).  New item type name was 'null'.");
        if(newItemType.trim().length()==0) throw new InternalError("Internal Error:  For item type '"+_currentItemType+"' marker, invalid parameter 'newItemType' to Marker.setNewItemType(String newItemType).  New item type name was an empty string '"+newItemType+"'.");

        // Make sure not set to prefix, suffix, or replacement
        if(_attrPrefix!=null) throw new InternalError("Internal Error:  This marker for itme type '"+_currentItemType+"' is already set to add a prefix '"+_attrPrefix+"' to attribute '"+_attrName+"'.  A marker cannot support other methods of marking if marking by reindexing to another item type is selected.");
        if(_attrSuffix!=null) throw new InternalError("Internal Error:  This marker for itme type '"+_currentItemType+"' is already set to add a suffix '"+_attrSuffix+"' to attribute '"+_attrName+"'.  A marker cannot support other methods of marking if marking by reindexing to another item type is selected.");
        if(_attrReplacement!=null) throw new InternalError("Internal Error:  This marker for itme type '"+_currentItemType+"' is already set to replace an attribute '"+_attrName+"' with value '"+_attrReplacement+"'.  A marker cannot support other methods of marking if marking by reindexing to another item type is already selected.");
        
        // Save prefix value. This object supports allowing a prefix and a suffix.
        _newItemType = newItemType;
    }//end setNewItemType()

   /**
    * One-line description of this marker.
    * @return Returns a single-line description of this marker.
    **/
    public String toString(){
        StringBuffer str = new StringBuffer();
        if(_attrReplacement!=null){
            str.append("Replacement Marker: ");
            str.append(_currentItemType);
            str.append("::");
            str.append(_attrName);
            str.append(" = ");
            str.append(_attrReplacement);
        }else if(_attrPrefix!=null){
            str.append("Attr Prefix Marker: ");
            str.append(_currentItemType);
            str.append("::");
            str.append(_attrName);
            str.append(" += ");
            str.append(_attrPrefix);
        }else if(_attrSuffix!=null){
            str.append("Attr Suffix Marker: ");
            str.append(_currentItemType);
            str.append("::");
            str.append(_attrName);
            str.append(" =+ ");
            str.append(_attrSuffix);
        }else if(_newItemType!=null){
            str.append("   Re-index Marker: ");
            str.append(_currentItemType);
            str.append(" -> ");
            str.append(_newItemType);
        }else{
            str.append("Inactive Marker: ");
            str.append(_currentItemType);
            str.append("::<No Marking Method Selected>");
        }
        return(str.toString());
    }//end toString()

   /**
    * Once a connection is established, this method validates that the marker
    * uses valid item types and attributes.
    * @param connected DKDatastoreICM object that the markers will be used with.
    **/
    public void validate(DKDatastoreICM dsICM) throws DKException, Exception{
        // Validate Input
        if(dsICM==null) throw new InternalError("Internal Error:  Invalid input to Marker validation method.  'dsICM' is null.");
        if(dsICM.isConnected()==false) throw new InternalError("Internal Error:  Invalid input to Marker validation method.  'dsICM' has never been connected.");

        // Get the datastore definition object
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
        DKComponentTypeViewDefICM currentCompTypeViewDef = null; // Save reference for later.
        {
            // Validate that the original item type is valid
            dkEntityDef entityDef = dsDefICM.retrieveEntity(_currentItemType);
            if(entityDef==null) throw new IllegalArgumentException("Alleged current item type name '"+_currentItemType+"' either does not exist as any form of an entity type in the '"+dsICM.datastoreName()+"' datastore or user '"+dsICM.userName()+"' does not have the necessary access to the item type.  The alleged item type name '"+_currentItemType+"' was used in completion marker setting.  Modify the configuration file to use valid item types that the user has access to.");
            // Make sure it is a root
            DKComponentTypeViewDefICM compTypeViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(_currentItemType);
            currentCompTypeViewDef = compTypeViewDef; //Save reference for later
            if(compTypeViewDef.isRoot()==false)
                throw new IllegalArgumentException("Alleged current item type name '"+_currentItemType+"' is not a root component type and therefore is not the item type name in the '"+dsICM.datastoreName()+"' datastore.  The name provided, '"+_currentItemType+"', is a child component type.  Import / Export can only be performed at the item level through the root components.  Since only root components are supported, no markers for child components are needed.  Review the alleged current item type name '"+_currentItemType+"' listed in the configuration file for completion markers.  Use only base item type names.");
            // Detect if it is a view, not the base item type.
            if(compTypeViewDef.getName().compareToIgnoreCase(compTypeViewDef.getComponentTypeName())!=0)
                throw new IllegalArgumentException("Alleged current item type name '"+_currentItemType+"' is not the base item type name in the '"+dsICM.datastoreName()+"' datastore.  Instead, '"+_currentItemType+"' is a view name of item type '"+compTypeViewDef.getComponentTypeName()+"'.  Non-base views typically act like a filter for the base item type.  To ensure that all necessary attributes are available to this tool, use only base views.  Review the alleged current item type name '"+_currentItemType+"' listed in the configuration file for completion markers.  Use only base item type names.");    
            // Make sure it that the base view is the active view
            DKComponentTypeViewDefICM activeCompTypeViewDef = dsDefICM.getActiveComponentTypeView(_currentItemType);
            if(activeCompTypeViewDef.getName().compareToIgnoreCase(_currentItemType)!=0)
                throw new IllegalArgumentException("Base item type '"+_currentItemType+"' is not the view that is currently active in the '"+dsICM.datastoreName()+"' datastore for user '"+dsICM.userName()+"'.  Instead, '"+activeCompTypeViewDef.getName()+"' is the active view for item type '"+_currentItemType+"'.  Non-base views typically act like a filter for the base item type.  To ensure that all necessary attributes are available to this tool, use only active base views.  Ensure that the item type name '"+_currentItemType+"' is the active view in the system.  Otherwise review the validity of item type name '"+_currentItemType+"' listed in the configuration file for completion markers.  Use only active base item type names.");    
            // Check an alternate was of ensuring that the base view is the active view.
            try{DKItemTypeDefICM itemTypeDef = (DKItemTypeDefICM) entityDef;}
            catch(ClassCastException exc){
                throw new IllegalArgumentException("Alleged current item type name '"+_currentItemType+"' is not the base item type view name in the '"+dsICM.datastoreName()+"' datastore.  Non-base views typically act like a filter for the base item type.  The tool requires that all attributes be available.  Review the alleged current item type name '"+_currentItemType+"' listed in the configuration file for completion markers.  Use only base item type names.");    
            }
        }
        
        // if a new item type is used, validate that the new item type is valid.
        if(_newItemType!=null){
            // Validate that the new item type is valid
            dkEntityDef entityDef = dsDefICM.retrieveEntity(_newItemType);
            if(entityDef==null) throw new IllegalArgumentException("Alleged new item type name '"+_newItemType+"' either does not exist as any form of an entity type in the '"+dsICM.datastoreName()+"' datastore or user '"+dsICM.userName()+"' does not have the necessary access to the item type.  The alleged item type name '"+_newItemType+"' was used in completion marker setting.  Modify the configuration file to use valid item types that the user has access to.");
            // Make sure it is a root
            DKComponentTypeViewDefICM compTypeViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(_newItemType);
            if(compTypeViewDef.isRoot()==false)
                throw new IllegalArgumentException("Alleged new item type name '"+_newItemType+"' is not a root component type and therefore is not the item type name in the '"+dsICM.datastoreName()+"' datastore.  The name provided, '"+_newItemType+"', is a child component type.  Import / Export can only be performed at the item level through the root components.  Since only root components are supported, no markers for child components are needed.  Review the alleged new item type name '"+_newItemType+"' listed in the configuration file for completion markers.  Use only base item type names.");
            // Detect if it is a view, not the base item type.
            if(compTypeViewDef.getName().compareToIgnoreCase(compTypeViewDef.getComponentTypeName())!=0)
                throw new IllegalArgumentException("Alleged new item type name '"+_newItemType+"' is not the base item type name in the '"+dsICM.datastoreName()+"' datastore.  Instead, '"+_newItemType+"' is a view name of item type '"+compTypeViewDef.getComponentTypeName()+"'.  Non-base views typically act like a filter for the base item type.  To ensure that all necessary attributes are available to this tool, use only base views.  Review the alleged new item type name '"+_newItemType+"' listed in the configuration file for completion markers.  Use only base item type names.");    
            // Make sure it that the base view is the active view
            DKComponentTypeViewDefICM activeCompTypeViewDef = dsDefICM.getActiveComponentTypeView(_newItemType);
            if(activeCompTypeViewDef.getName().compareToIgnoreCase(_newItemType)!=0)
                throw new IllegalArgumentException("Base item type '"+_newItemType+"' is not the view that is currently active in the '"+dsICM.datastoreName()+"' datastore for user '"+dsICM.userName()+"'.  Instead, '"+activeCompTypeViewDef.getName()+"' is the active view for item type '"+_newItemType+"'.  Non-base views typically act like a filter for the base item type.  To ensure that all necessary attributes are available to this tool, use only active base views.  Ensure that the item type name '"+_newItemType+"' is the active view in the system.  Otherwise review the validity of item type name '"+_newItemType+"' listed in the configuration file for completion markers.  Use only active base item type names.");    
            // Check an alternate was of ensuring that the base view is the active view.
            try{DKItemTypeDefICM itemTypeDef = (DKItemTypeDefICM) entityDef;}
            catch(ClassCastException exc){
                throw new IllegalArgumentException("Alleged new item type name '"+_newItemType+"' is not the base item type view name in the '"+dsICM.datastoreName()+"' datastore.  Non-base views typically act like a filter for the base item type.  The tool requires that all attributes be available.  Review the alleged new item type name '"+_newItemType+"' listed in the configuration file for completion markers.  Use only base item type names.");    
            }
        }//end if(_newItemType!=null){
        
        // If an attribute is selected,
        if(_attrName!=null){
            DKAttrDefICM attrDef = (DKAttrDefICM) currentCompTypeViewDef.retrieveAttr(_attrName);
            // Validate that it is a valid attribute in this item type.
            if(attrDef==null)
                throw new IllegalArgumentException("No such attribute exists in item type '"+_currentItemType+"' named '"+_attrName+"' for use with a completion marker.  Choose an attribute that belongs to this item type.  Modify the completion marker settings in the configuration file.");
                
            // If replacement value is used, ensure that the attribute type is valid
            if(_attrReplacement!=null){
                // Check attribute value types
                // - If String
                if(    (attrDef.getType()==DKConstant.DK_CM_CHAR)
                    || (attrDef.getType()==DKConstant.DK_CM_VARCHAR)
                  ){
                    if((_attrReplacement instanceof String)==false)
                        throw new IllegalArgumentException("Invalid value type specified based on the actual type of attribute selected for the completion marker for item type '"+_currentItemType+"'.  The attribute selected, '"+_attrName+"', is a fixed or variable length attribute, but the type of value specified was not a String.  Be sure to only use the \"(String)\" type designation for a replacement value in the completion marker settings in the configuration file.");
                    // Check length
                    if(((String)_attrReplacement).length() > attrDef.getSize())
                        throw new IllegalArgumentException("The replacement value specified for attribute '"+_attrName+"' for the item type '"+_currentItemType+"' completion marker exceeds the maximum string length, '"+attrDef.getSize()+"', defined for this attribute with its value '"+_attrReplacement+"' of size '"+((String)_attrReplacement).length()+"'. ");
                }
                // - If Integer
                else if(    (attrDef.getType()==DKConstant.DK_CM_INTEGER)
                  ){
                    if((_attrReplacement instanceof Integer)==false)
                        throw new IllegalArgumentException("Invalid value type specified based on the actual type of attribute selected for the completion marker for item type '"+_currentItemType+"'.  The attribute selected, '"+_attrName+"', is an Integer attribute, but the type of value specified was not an Integer.  Be sure to only use the \"(Integer)\" type designation for a replacement value in the completion marker settings in the configuration file.");
                    // Check Min
                    if(((Integer)_attrReplacement).intValue() < attrDef.getMin())
                        throw new IllegalArgumentException("The replacement value specified for attribute '"+_attrName+"' for the item type '"+_currentItemType+"' completion marker violdates the minimum value , '"+attrDef.getMin()+"', defined for this attribute with its value '"+_attrReplacement+"'. ");
                    // Check Max
                    if(((Integer)_attrReplacement).intValue() > attrDef.getMax())
                        throw new IllegalArgumentException("The replacement value specified for attribute '"+_attrName+"' for the item type '"+_currentItemType+"' completion marker violdates the maximum value , '"+attrDef.getMax()+"', defined for this attribute with its value '"+_attrReplacement+"'. ");
                }
                // - If Short
                else if(    (attrDef.getType()==DKConstant.DK_CM_SHORT)
                  ){
                    if((_attrReplacement instanceof Short)==false)
                        throw new IllegalArgumentException("Invalid value type specified based on the actual type of attribute selected for the completion marker for item type '"+_currentItemType+"'.  The attribute selected, '"+_attrName+"', is a Short attribute, but the type of value specified was not a Short.  Be sure to only use the \"(Short)\" type designation for a replacement value in the completion marker settings in the configuration file.");
                    // Check Min
                    if(((Short)_attrReplacement).intValue() < attrDef.getMin())
                        throw new IllegalArgumentException("The replacement value specified for attribute '"+_attrName+"' for the item type '"+_currentItemType+"' completion marker violdates the minimum value , '"+attrDef.getMin()+"', defined for this attribute with its value '"+_attrReplacement+"'. ");
                    // Check Max
                    if(((Short)_attrReplacement).intValue() > attrDef.getMax())
                        throw new IllegalArgumentException("The replacement value specified for attribute '"+_attrName+"' for the item type '"+_currentItemType+"' completion marker violdates the maximum value , '"+attrDef.getMax()+"', defined for this attribute with its value '"+_attrReplacement+"'. ");
                }
                // Can only support String, Short, and Integer
                else{
                    throw new IllegalArgumentException("Invalid type of attribute selected for completion marker for item type '"+_currentItemType+"'.  The attribute selected, '"+_attrName+"', is not one of the allowed types:  fixed-lenth character string, variable-length character string, Integer, or Short.");    
                }
            }//end if(_attrReplacement!=null){
            
            // If prefix or suffix is used, ensure that the attribute type is valid.
            if((_attrPrefix!=null)||(_attrSuffix!=null)){
                if(    (attrDef.getType()!=DKConstant.DK_CM_CHAR)
                    && (attrDef.getType()!=DKConstant.DK_CM_VARCHAR)
                  ){
                    throw new IllegalArgumentException("Invalid type of attribute selected for completion marker for item type '"+_currentItemType+"' with prefix or suffix marking method.  The attribute selected, '"+_attrName+"', one of the allowed types when prefix or suffix values are used:  fixed-lenth character string, variable-length character string");                
                }
            }//end if((_attrPrefix!=null)||(_attrSuffix)){
        }//end if(_attrName!=null){
        
    }//end validateMarkers

   /**
    * Validate the settings of this object.
    **/
    private void validateSettings(){
        // Validate Input
        if(_currentItemType==null)                 throw new InternalError("Internal Error Creating Marker:  Variable '_currentItemType' is 'null'.  A valid PackageInfo object instance is required.");
    }//end validateSettings()
   
}//end class TImportManagerCompletionMarkerICM_Marker
