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

 Import Manager
     Overview
         For an overview and detailed description, refer to the
         "Import Manager" section in the header documentation of the
         TExportManagerICM.java sample file.

     Configuration
         For configuration documentation, refer to the TImportExportICM.ini
         default configuration file and the TExportManagerICM.java header
         documentation.

         For detailed command line usage, refer to the next comment block
         below, just above the main method definition.
     
     Restart Capability
         Refer to the "RESTART CAPABILITY" documentation in the next comment
         block below, just above the main method definition with the 
         command line usage.

     LIMITATIONS
         Refer to the TExportManagerICM.java header documentation for
         limitations for this tool as part of the overview.

 ******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;

/************************************************************************************************
 *          FILENAME: TImportManagerICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Manages the entire set of items to be imported from a source system,
 *                    exported as a "Master Package" by Export Manager and consisting of 
 *                    one or more Export Packages (batches) of data.
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java TImportManagerICM <options>
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
 *                             -v/volumes  <Folder 1>,<Folder 2>,... (Volumes / Storage Abolute Path Locations where package data is stored.  Package data does to have to appear in the same relative storage location.)
 *                             -r/restart  (Force restart of an existing master package in progress)
 *                             -k/kill     (Force killing / starting over importing of an import that was in progress.  If always importing as new, this will cause duplicates.)
 *
 *                     Notes:  * Defaults will be used for optional parameters 
 *                               'database', 'user', and 'password' if not specified.  
 *                             * Default file name of "TImportExportICM.ini" will be
 *                               used for configuration file name if none is specified. 
 *                             * Configuration file is optional.  If TImportExportICM.ini
 *                               is not found, defaults will be used.
 *                             * If an existing tracking file is found, the tool will prompt
 *                               the user whether to continue where it left off if the 
 *                               -r/restart or -k/kill commands are not used.
 *                             * The packageNNN subfolders within the "masterPackage" folder
 *                               at each volume / storage location can be moved to 
 *                               any other "masterPackage" folder at any other storage
 *                               location provided the whole directory successfully copies.
 *                               The tool can detect and handle such changes, including 
 *                               changes to storage locations.
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
 *                             java TImportManagerICM -d icmnlsdb -u icmadmin -p password
 *
 *                    Document:  n/a
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: The source and target item types must be identical in names and structure.
 *                    Versioning setting of 'Never' or 'By Application' is recommended.
 *                    Versioning set to 'Always' will not preserve versions correctly and 
 *                    will result in intermediate versions created.
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 *                    TExportPackageICM.java
 *                    SLinksICM.java
 ************************************************************************************************/
public class TImportManagerICM{

    // Enumaration Constants

    // Default Settings
    private static final String  DEFAULT_DATABASE              = SConnectDisconnectICM.DEFAULT_DATABASE;
    private static final String  DEFAULT_USERNAME              = SConnectDisconnectICM.DEFAULT_USERNAME;
    private static final String  DEFAULT_PASSWORD              = SConnectDisconnectICM.DEFAULT_PASSWORD;
    private static final String  DEFAULT_CONNECT_OPTS          = "";
    public  static final String  DEFAULT_INI_FILE_NAME         = TExportManagerICM.DEFAULT_INI_FILE_NAME;
    public  static final String  DEFAULT_MASTER_PACKAGE_NAME   = TExportManagerICM.DEFAULT_MASTER_PACKAGE_NAME;
    private static final boolean DEFAULT_RETRY_ABORTED_PACKAGES= true;  // Will always retry by default.
    private static final int     DEFAULT_SKIP_PACKAGE_AFTER_ATTEMPT_NUM                  = 10;
    private static final boolean DEFAULT_USE_TRANSACTIONS                                = true;
    private static final boolean DEFAULT_DATA_MODEL_SUPPORTS_CHILDREN                    = true;  // Whether or not the tool can assume there are no children to perform faster.
    private static final boolean DEFAULT_DATA_MODEL_SUPPORTS_LINKS                       = true;  // Whether or not the tool can assume there are no links to improve performance.
    private static final boolean DEFAULT_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS       = true;  // Whether or not the tool can assume no folder contents to improve performance.
    private static final boolean DEFAULT_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT = true;  // Whether or not the tool can assume no resource items or no resource items that have content to improve performance.
    private static final boolean DEFAULT_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT          = true;  // Whether or not the tool can assume no document parts or no document parts that have content to improve performance.

    // Command Line Argument Usage Constants
    private static final String  COMMANDLINE_OPTION_DATABASE          = "-d/database <you database name>";
    private static final String  COMMANDLINE_OPTION_USERNAME          = "-u/user     <CM user id>";
    private static final String  COMMANDLINE_OPTION_PASSWORD          = "-p/password <CM user's password>";
    private static final String  COMMANDLINE_OPTION_CONNECT_OPTIONS   = "-o/options  <Connect String Options>";
    private static final String  COMMANDLINE_OPTION_CONFIG_FILE       = "-i/ini      <Alternate Configuration File>";
    private static final String  COMMANDLINE_OPTION_MASTER_NAME       = "-m/master   <Master Package Name> (Name of master package exported from Export Manager.  This is the the base name of the master directories in storage locations, master file (summary), and tracking file.)";
    private static final String  COMMANDLINE_OPTION_LOG_FILE_LOCATION = "-l/log      <Folder -- Absolute Path to Master Log File Folder> (Location guaranteed to have enough space through the whole process during import, writing tracking during progress and summary file after completion.)";
    private static final String  COMMANDLINE_OPTION_STORAGE_LOCATIONS = "-v/volumes  <Folder 1>,<Folder 2>,... (Volumes / Storage Abolute Path Locations where package data is stored.  Package data does to have to appear in the same relative storage location.)";
    private static final String  COMMANDLINE_OPTION_RESTART           = "-r/restart  (Force restart of an existing master package in progress)";
    private static final String  COMMANDLINE_OPTION_OVERWRITE         = "-k/kill     (Force killing / starting over importing of an import that was in progress.  If always importing as new, this will cause duplicates.)";

    // Configuration Constants
    private static final String  EXPECTED_OUT_OF_SPACE_MESSAGE        = "There is not enough space on the disk"; // Determined by IBM Java 1.3.1 on Windows.
    public  static final String  MASTER_FOLDER_NAME                   = TExportManagerICM.MASTER_FOLDER_NAME;
    public  static final String  COMMON_PACKAGE_FOLDER_BASE_NAME      = TExportManagerICM.COMMON_PACKAGE_FOLDER_BASE_NAME;      // Name used for the central package file of all export packages (batches).
    public  static final String  COMMON_EXPORT_PACKAGE_NAME           = TExportManagerICM.COMMON_EXPORT_PACKAGE_NAME;  // Name used for the central package file of all export packages (batches).
    public  static final String  STANDARD_LIST_DELIMITER              = ",";
    public  static final String  STORAGE_LOCATION_DELIMITER           = STANDARD_LIST_DELIMITER;
    public  static final String  MASTER_EXPORT_PACKAGE_FILE_EXT       = "mpk";
    public  static final String  MASTER_IMPORT_RECORD_FILE_EXT        = "map";
    public  static final String  MASTER_SUMMARY_REPORT_FILE_EXT       = "sum";
    public  static final String  MASTER_TRACKING_FILE_EXT             = "itk";

    // Configuration File Tags
    private static final String  NEWLINE                                  = System.getProperty("line.separator");
    private static final String  CONFIG_TAG_RETRY_ABORTED_PACKAGES        = "Retry Aborted Packages";
    private static final String  CONFIG_TAG_SKIP_PACKAGE_AFTER_ATTEMPT_NUM= "Skip Package After Attempt Num";
    // Repeats from Export Manager:
    private static final String  CONFIG_TAG_STORAGE_LOCATIONS                                = TExportManagerICM.CONFIG_TAG_STORAGE_LOCATIONS;
    private static final String  CONFIG_TAG_LOG_FILE_DIRECTORY                               = TExportManagerICM.CONFIG_TAG_LOG_FILE_DIRECTORY;
    private static final String  CONFIG_TAG_MASTER_PACKAGE_NAME                              = TExportManagerICM.CONFIG_TAG_MASTER_PACKAGE_NAME;
    //Reuse Value in TExportManagerICM:  private static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_CHILDREN                     = CONFIG_TAG_DATA_MODEL_SUPPORTS_CHILDREN;
    //Reuse Value in TExportManagerICM:  private static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_LINKS                        = CONFIG_TAG_DATA_MODEL_SUPPORTS_LINKS;
    //Reuse Value in TExportManagerICM:  private static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS        = CONFIG_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS;
    //Reuse Value in TExportManagerICM:  private static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT  = CONFIG_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT;
    //Reuse Value in TExportManagerICM:  private static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT           = CONFIG_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT;

    // Master Export Package File Information
    private static final String  MASTER_PACKAGE_VERSION                   = "4.02.04";
    private static final String  MASTER_PACKAGE_TAG_FILE_IDENTIFIER       = TExportManagerICM.FILE_TAG_MASTER_SUMMARY_IDENTIFIER;
    private static final String  MASTER_PACKAGE_TAG_PACKAGE_EXPORTED      = TExportManagerICM.FILE_TAG_PACKAGE_INFO;

    // Summary File Tags
    private static final String  FILE_TAG_MASTER_SUMMARY_IDENTIFIER       = "<Master Package Import Summary>";
    private static final String  FILE_TAG_SECTION_CONFIG_BEGIN            = "CONFIGURATION:";
    private static final String  FILE_TAG_SECTION_CONFIG_HEADER           = "              Setting   Value                                                " + NEWLINE
                                                                          + "----------------------  -----------------------------------------------------";
    private static final String  FILE_TAG_MASTER_PACKAGE_NAME             = "  Master Package Name:  ";
    private static final String  FILE_TAG_DATABASE                        = "       Datastore Name:  ";
    private static final String  FILE_TAG_USERNAME                        = "         CM User Name:  ";
    private static final String  FILE_TAG_CONNOPTS                        = "      Connect Options:  ";
    private static final String  FILE_TAG_RETRY_ATTEMPTS                  = "    # Retries / Issue:  ";
    private static final String  FILE_TAG_RETRY_DELAY_MS                  = "Retry Delay Time (ms):  ";
    private static final String  FILE_TAG_SKIP_PACKAGE_AFTER_ATTEMPT_NUM  = " Skip Pkg After Retry:  ";
    private static final String  FILE_TAG_RETRY_ABORTED_PACKAGES          = "Retry Previous Aborts:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_CHILDREN                    = "SUPPORTED    Children:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_LINKS                       = "                Links:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS       = "Folders With Contents:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT = "    Resource Contents:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT          = "        Part Contents:  ";
    private static final String  FILE_TAG_CONFIG_FILE_NAME                      = "          Config File:  ";
    private static final String  FILE_TAG_MASTER_LOG_FILE_DIRECTORY             = "    Master Log Folder:  ";
    private static final String  FILE_TAG_MASTER_EXPORT_PACKAGE_FILE            = "Master Export Package:  ";
    private static final String  FILE_TAG_MASTER_SUMMARY_FILE                   = "       Import Summary:  ";
    private static final String  FILE_TAG_MASTER_IMPORT_RECORD_FILE             = "  Item Mapping Record:  ";
    private static final String  FILE_TAG_MASTER_TRACKING_FILE                  = " Master Tracking File:  ";
    private static final String  FILE_TAG_STORAGE_LOCATIONS                     = "    Storage Locations:  ";
    private static final String  FILE_TAG_SECTION_CONFIG_END                    = "-----------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_STATS_BEGIN                   = "STATISTICS:";
    private static final String  FILE_TAG_SECTION_STATS_HEADER                  = "                Statistic   Value                                            " + NEWLINE
                                                                                + "--------------------------  -------------------------------------------------";
    private static final String  FILE_TAG_STAT_TOTAL_PACKAGES                   = "               # Packages:  ";
    private static final String  FILE_TAG_STAT_TOTAL_ITEMS                      = "                  # Items:  ";
    private static final String  FILE_TAG_STAT_TOTAL_COMPLETED_PACKAGES         = "    # Packages Successful:  ";
    private static final String  FILE_TAG_STAT_TOTAL_FAILED_PACKAGES            = "       # Packages Skipped:  ";
    private static final String  FILE_TAG_STAT_NUM_TOOL_RESTARTS                = "          # Tool Restarts:  ";
    private static final String  FILE_TAG_STAT_NUM_MASTER_LEVEL_ERRORS          = "    # Master-Level Errors:  ";
    private static final String  FILE_TAG_STAT_NUM_PACKAGE_LEVEL_ERRORS         = "   # Package-Level Errors:  ";
    private static final String  FILE_TAG_STAT_START_TIMESTAMP                  = "               Start Time:  ";
    private static final String  FILE_TAG_STAT_END_TIMESTAMP                    = "          Completion Time:  ";
    private static final String  FILE_TAG_SECTION_STATS_END                     = "-----------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_BEGIN  = "PACKAGE LIST:";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_HEADER = "Tag            Package#   # Items           After Item ID                 Last Item ID           Folder                        " + NEWLINE
                                                                                + "-------------  --------  ----------  ----------------------------  ----------------------------  ------------------------------";
    private static final String  FILE_TAG_PACKAGE_INFO                          = "Package Info:  ";
    private static final String  FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_END    = "-------------------------------------------------------------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_FAILED_PACKAGE_LIST_BEGIN     = "INCOMPLETE PACKAGES: ";
    private static final String  FILE_TAG_SECTION_FAILED_PACKAGE_LIST_HEADER    = "Tag            Package#   # Items           After Item ID                 Last Item ID           Folder                        " + NEWLINE
                                                                                + "-------------  --------  ----------  ----------------------------  ----------------------------  ------------------------------";
    private static final String  FILE_TAG_FAILED_PACKAGE                        = "  Incomplete:  ";
    private static final String  FILE_TAG_SECTION_FAILED_PACKAGE_LIST_END       = "-----------------------------------------------------------------------------";

    // Tracking File Event & Information Tags
    private static final String  TRACKING_TAG_ALL_PACKAGES_STARTED        = "Starting All Packages:  ";
    private static final String  TRACKING_TAG_ALL_PACKAGES_COMPLETED      = "Completed All Packages:  ";
    private static final String  TRACKING_TAG_FAILURE_MASTER_LEVEL        = "Failure at master-level:  ";
    private static final String  TRACKING_TAG_FAILURE_PACKAGE_LEVEL       = "Failure at package-level:  ";
    private static final String  TRACKING_TAG_FAILURE_WRITE_LEVEL         = "Failure on writing package:  ";
    private static final String  TRACKING_TAG_FILE_IDENTIFIER             = "<Master Import Tracking File>";
    private static final String  TRACKING_TAG_PACKAGE_STARTED             = "Package Started:  ";
    private static final String  TRACKING_TAG_PACKAGE_COMPLETED           = "Package Completed:  ";
    private static final String  TRACKING_TAG_PACKAGE_ABANDONED           = "Package Abandoned:  ";
    private static final String  TRACKING_TAG_SUMMARY_STARTED             = "Started Writing Summary:  ";
    private static final String  TRACKING_TAG_SUMMARY_COMPLETED           = "Completed Writing Summary:  ";
    private static final String  TRACKING_TAG_TOOL_START                  = "Tool Start:  ";
    private static final String  TRACKING_TAG_VOLUME_FULL                 = "STORAGE LOCATION FULL:  ";
    private static final String  TRACKING_TAG_VOLUME_CURRENT              = "NEW STORAGE LOCATION:  ";

    // Import Record FIle Tags
    public  static final String  IMPORT_RECORD_TAG_IDENTIFIER             = "<Master Import Record of Completed Items>";
    public  static final String  IMPORT_RECORD_TAG_FORMAT_VERSION         = "4.02.13";
    public  static final String  IMPORT_RECORD_TAG_SECTION_MAPPING_BEGIN  = "BEGIN COMPLETED PACKAGE: ";
    public  static final String  IMPORT_RECORD_TAG_SECTION_MAPPING_END    = "END COMPLETED PACKAGE: ";
    public  static final String  IMPORT_RECORD_TAG_PACKAGE_MAPPING        = "ItemInfoPack:";

    // Variables
    // - Connect Variables
    String  _database               = null;  // -d/database <xxxxxxxx>
    String  _userName               = null;  // -u/user <xxxxxxxx>
    String  _password               = null;  // -p/password <xxxxxxxx>
    String  _connOpts               = null;  // -o/options <Connect String Options>
    // - Primary Option Variables
    String  _iniFileName            = null;  // -i/ini <Alternate Configuration File>
    File    _masterLogFileDirectory = null;  // -l/log <Mast Log File Location> (Location guaranteed to have enough space through the whole process to write tracking & summary files.)
    String  _masterPackageName      = null;  // -m/master <Master Package Name> (Name of master package used as base name for master directory, tracking file, and summary file.)
    File[]  _storageLocations       = null;  // -v/volume <Folder 1>,<Folder 2>,... (Volumes / Storage Locations Availble>
    boolean _useTransactions        = true;  // Cannot be turned off in this version.
    // - Objects
    DKDatastoreICM                   _dsICM                      = null;
    TExportPackageICM.ImportOptions  _importOptions              = null;
    TExportManagerICM_AttemptManager _masterLevelAttemptManager  = null;  // Managed Operation: Exporting all packages.
    TExportManagerICM_AttemptManager _packageLevelAttemptManager = null;  // Managed Operation: Selection & Export of single package/batch.
    // - Internal Variables
    boolean   _dataModelSupportsChildren                 = true; // Whether or not the tool can assume there are no children to perform faster.
    boolean   _dataModelSupportsLinks                    = true; // Whether or not the tool can assume there are no links to improve performance.
    boolean   _dataModelSupportsFoldersWithContents      = true; // Whether or not the tool can assume no folder contents to improve performance.
    boolean   _dataModelSupportsResourceItemsWithContent = true; // Whether or not the tool can assume no resource items or no resource items that have content to improve performance.
    boolean   _dataModelSupportsPartsWithContent         = true; // Whether or not the tool can assume no document parts or no document parts that have content to improve performance.
    boolean   _allPackagesCompleted       = false; // Tracks whether or not all packages have been completed.
    TreeMap<Integer,TExportManagerICM_PackageInfo>
              _completedPackageInfoList   = null;  // Tracks the key information for the summary report on all individual packages that were successful.  Contains TExportManagerICM_PackageInfo objects located by key.  Key obtained by PackageInfo.getKey() or getPackageKey(packageNum).
    int       _currentPackageNum          = -1;    // The current package number
    TreeMap<Integer,TExportManagerICM_PackageInfo>
              _failedPackageInfoList      = null;  // Tracks all incomplete packages that were skipped due to to many failures.  Contains TExportPackageICM_PackageInfo objects located by key.  Key obtained by PackageInfo.getKey() or getPackageKey(packageNum).
    String    _lastPackageEndingItemID    = null;  // The item ID of that last item of the last package.
    File      _masterImportRecordFile     = null;  // File recording all item Id mapping.  This is needed for the Reconciliation and Completion Marker tools.  This is updated after every successful package import.
    File      _masterPackageFile          = null;  // Master Export Package File written by Export Manager as its summary report.
    File      _masterSummaryFile          = null;  // Summary file written at after all items have been exported.
    File      _masterTrackingFile         = null;  // Tracking file used to restart the tool if it crashes or needs to be restarted later.
    boolean   _newRunRequested            = false; // Whether or not the user requested to start over, replacing any existing data.
    TreeMap<Integer,TExportManagerICM_PackageInfo>
              _packageInfoList            = null;  // All packages loaded for import.  Contains TExportPackageICM_PackageInfo objects located by key.  Key obtained by PackageInfo.getKey() or getPackageKey(packageNum).
    boolean   _restartRequested           = false; // Whether or not the user requested to restart an existing master package.
    boolean   _retryAbortedPackages       = false; // Determines whether or not any package aborted after a number of failures should now be retried.
    int       _skipPackageAfterAttemptNum = -1;    // After this many attempts due to failures, the tool will give up on the package and mark it as failed.
    int       _statNumToolRestarts        = -1;    // Number of times the tool has restarted (for statistical purposes)
    long      _statNumTotalItems          = -1;    // Number of actual items exported in total.
    int       _statNumMasterLevelErrors   = -1;    // Number of times that the master-level attempt manager experienced errors (for statistical purposes)
    int       _statNumPackageLevelErrors  = -1;    // Number of times that the package-level attempt manager experienced errors (for statistical purposes)
    Timestamp _statStartedTimestamp       = null;  // Time that the tool orginally started (for statistical purposes)
    Timestamp _statCompletedTimestamp     = null;  // Time that the tool actually completed the export (for statistical purposes)
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
            TImportManagerICM importMgr = new TImportManagerICM(argv);
            
            // Execute the Import Manager Primary Program
            importMgr.run();

            //-------------------------------------------------------------
            // Sample program completed without exception
            //-------------------------------------------------------------
            System.out.println("");
            System.out.println("==========================================");
            System.out.println("Import Manager Completed.");
            System.out.println("==========================================");
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
    private TImportManagerICM() throws Exception{
        throw new Exception("The TImportManagerICM object cannot be created by the no-argument constructor.  Use the constructor that takes the main arguments.");
    }//end CTOR()

   /**
    * Create an instance of the Import Manager.
    * @param commandlineArgs - Command line arguments from main(String[] argsv)
    **/
    public TImportManagerICM(String[] commandlineArgs) throws Exception{
        // Display Startup Information
        printStartupInformation();
        // Start off with defaults
        initByDefaults();
        // Override Settings by Configuration File
        initByConfigurationFile(commandlineArgs);
        // Initialize Settings by Parse Command Line Arguments
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
        _database                  = DEFAULT_DATABASE;
        _userName                  = DEFAULT_USERNAME;
        _password                  = DEFAULT_PASSWORD;
        _connOpts                  = DEFAULT_CONNECT_OPTS;
        // - Primary Variables
        _iniFileName               = DEFAULT_INI_FILE_NAME;
        _masterLogFileDirectory    = null;      // No Default
        _masterPackageName         = DEFAULT_MASTER_PACKAGE_NAME;
        _storageLocations          = null;      // No Default
        _useTransactions           = DEFAULT_USE_TRANSACTIONS;
        // - Objects
        _dsICM                     = null;
        _importOptions             = null;
        _masterLevelAttemptManager = null;
        _packageLevelAttemptManager= null;
        // - Internal Variables
        _allPackagesCompleted      = false; // Tracks whether or not the main exportAllPackages method should be run.
        _completedPackageInfoList  = null;  // Tracks the key information for the summary report on all individual packages that were successful.  Contains TExportManagerICM_PackageInfo objects.
        _currentPackageNum         = 1;     // Start counting at '1'.
        _failedPackageInfoList     = null;  // Tracks all incomplete packages that were skipped due to to many failures.
        _lastPackageEndingItemID   = null;  // By default, not continuing after another package.
        _masterImportRecordFile    = null;  // File recording all item Id mapping.  This is needed for the Reconciliation and Completion Marker tools.  This is updated after every successful package import.
        _masterPackageFile         = null;  // Master Export Package File written by Export Manager as its summary report.
        _masterSummaryFile         = null;  // Summary file written at after all items have been exported.
        _masterTrackingFile        = null;  // Tracking file used to restart the tool if it crashes or needs to be restarted later.
        _newRunRequested           = false; // Not forcing overwrite / starting over by default.
        _packageInfoList           = null;  // Tracks all packages to be imported.
        _restartRequested          = false; // Not forcing a restart by default.
        _retryAbortedPackages      = DEFAULT_RETRY_ABORTED_PACKAGES; // Determines whether or not any package aborted after a number of failures should now be retried.
        _skipPackageAfterAttemptNum= DEFAULT_SKIP_PACKAGE_AFTER_ATTEMPT_NUM; // After this many attempts due to failures, the tool will give up on the package and mark it as failed.
        _statNumToolRestarts       = 0;     // Number of time the tool has restarted (for statistical purposes)
        _statNumTotalItems         = 0;     // Number of actual items exported in total.
        _statNumMasterLevelErrors  = 0;     // Number of times that the master-level attempt manager experienced errors (for statistical purposes)
        _statNumPackageLevelErrors = 0;     // Number of times that the package-level attempt manager experienced errors (for statistical purposes)
        _statStartedTimestamp      = new Timestamp(System.currentTimeMillis());
        _statCompletedTimestamp    = null;  // Time that the tool actually completed the export (for statistical purposes)
        _dataModelSupportsChildren                 = DEFAULT_DATA_MODEL_SUPPORTS_CHILDREN;
        _dataModelSupportsLinks                    = DEFAULT_DATA_MODEL_SUPPORTS_LINKS;
        _dataModelSupportsFoldersWithContents      = DEFAULT_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS;
        _dataModelSupportsResourceItemsWithContent = DEFAULT_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT;
        _dataModelSupportsPartsWithContent         = DEFAULT_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT;
        
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
        while((line = file.readLine())!=null){ // Continue until reach end of file.
            int separatorLoc = line.indexOf("=");
            if(separatorLoc > 0){
                String property = line.substring(0,separatorLoc).trim();
                String value    = line.substring(separatorLoc+1).trim();
                // Handle depending on property name
                // Property: Storage Locations
                if(property.compareToIgnoreCase(CONFIG_TAG_STORAGE_LOCATIONS)==0){
                    if(value.compareTo("")!=0) setStorageLocations(value);  // Only save non-blank values.
                }
                // Property: Master Log File Folder
                else if(property.compareToIgnoreCase(CONFIG_TAG_LOG_FILE_DIRECTORY)==0){
                    if(value.compareTo("")!=0) _masterLogFileDirectory = new File(value);  // Only save non-blank values.
                }
                // Property: Master Package File Name
                else if(property.compareToIgnoreCase(CONFIG_TAG_MASTER_PACKAGE_NAME)==0){
                    if(value.compareTo("")!=0) _masterPackageName = value;  // Only save non-blank values.
                }
                // Property: Retry Aborted Packages
                else if(property.compareToIgnoreCase(CONFIG_TAG_RETRY_ABORTED_PACKAGES)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _retryAbortedPackages = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _retryAbortedPackages = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+CONFIG_TAG_RETRY_ABORTED_PACKAGES+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Skip Package After Attempt Number
                else if(property.compareToIgnoreCase(CONFIG_TAG_SKIP_PACKAGE_AFTER_ATTEMPT_NUM)==0){
                    if(value.compareTo("")!=0) setSkipPackageAfterAttemptNum(value);  // Only save non-blank values.
                }
                // Property: Use Transactions
                // - Currently cannot be modified. They are always used.
                // Property: Data Model Supports Children
                else if(property.compareToIgnoreCase(TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_CHILDREN)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsChildren = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsChildren = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_CHILDREN+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Data Model Supports Links
                else if(property.compareToIgnoreCase(TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_LINKS)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsLinks = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsLinks = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_LINKS+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Data Model Supports Folders with Contents
                else if(property.compareToIgnoreCase(TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsFoldersWithContents = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsFoldersWithContents = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Data Model Supports Resource Items with Content
                else if(property.compareToIgnoreCase(TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsResourceItemsWithContent = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsResourceItemsWithContent = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Data Model Supports Parts With Content
                else if(property.compareToIgnoreCase(TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsPartsWithContent = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsPartsWithContent = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+TExportManagerICM.CONFIG_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
            }//end if(separatorLoc > 0){
        }//end while((line = file.readLine())!=null){
            
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
        // -v/volume <Folder 1>,<Folder 2>,... (Volumes / Storage Locations Availble>
        String  storageLocations   = TExportManagerICM.getCommandlineChoice(argv,"-v","-volumes" ,false,COMMANDLINE_OPTION_STORAGE_LOCATIONS);
        // -r/restart  (Force restart of an existing master package)
        String  restartRequested   = TExportManagerICM.getCommandlineChoice(argv,"-r","-restart" ,false,COMMANDLINE_OPTION_RESTART);
        // -k/kill     (Force killing / overwrite of existing process.  Not a restart)
        String  newRunRequested    = TExportManagerICM.getCommandlineChoice(argv,"-k","-kill"    ,false,COMMANDLINE_OPTION_OVERWRITE);
        
        // Save any non-null settings
        if(database           !=null) _database           = database;
        if(userName           !=null) _userName           = userName;
        if(password           !=null) _password           = password;
        if(connOpts           !=null) _connOpts           = connOpts;
        if(iniFileName        !=null) _iniFileName        = iniFileName;
        if(masterPackageName  !=null) _masterPackageName  = masterPackageName;
        if(masterLogFileDirStr!=null) _masterLogFileDirectory = new File(masterLogFileDirStr);
        if(storageLocations   !=null) setStorageLocations(storageLocations);
        if(restartRequested   !=null) _restartRequested   = true;
        if(newRunRequested    !=null) _newRunRequested    = true;
        
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
        _completedPackageInfoList   = new TreeMap<Integer,TExportManagerICM_PackageInfo>(); // TreeMap of TExportManagerICM_PackageInfo objects with key value PackageInfo.getKey() (essentially package number).
        _dsICM                      = new DKDatastoreICM(); // Will be created later, but create instance now so we can validate database alias used.
        _importOptions              = new TExportPackageICM.ImportOptions(_iniFileName);
        _failedPackageInfoList      = new TreeMap<Integer,TExportManagerICM_PackageInfo>(); // TreeMap of TExportManagerICM_PackageInfo objects with key value PackageInfo.getKey() (essentially package number).
        _masterLevelAttemptManager  = new TExportManagerICM_AttemptManager("Master Package Attempt Manager",commandlineArgs);
        _masterImportRecordFile     = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_IMPORT_RECORD_FILE_EXT);
        _masterPackageFile          = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_EXPORT_PACKAGE_FILE_EXT);
        _masterSummaryFile          = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_SUMMARY_REPORT_FILE_EXT);
        _masterTrackingFile         = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_TRACKING_FILE_EXT);
        _packageInfoList            = new TreeMap<Integer,TExportManagerICM_PackageInfo>();  // Sorted list of TExportManagerICM_PackageInfo objects located by key.  Key obtained by PackageInfo.getKey() or getPackageKey(packageNum).
        _packageLevelAttemptManager = new TExportManagerICM_AttemptManager(_masterLevelAttemptManager,"Single Package Attempt Manager",commandlineArgs);
        
        // Override Import Options Tracking File Setting
        File tempPackageTrackingFile = new File(_masterLogFileDirectory,_masterPackageName+"_tempSinglePackageLog.trk");
        _importOptions.setTrackingFileName(tempPackageTrackingFile.getAbsolutePath());
    }//end initObjects()

   /**
    * Perform import operation.
    **/
    public void importProcess() throws DKException, Exception{
        
        System.out.println("-----------------");
        System.out.println("-- Import Mode --");
        System.out.println("-----------------");

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

                // Import All Apackages
                importAllPackages();

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
    }//end importProcess()

   /**
    * Import all packages.  Detect if one was already started,
    * and if so, continue where it left off.
    **/
    private void importAllPackages() throws DKException, Exception{

        System.out.println("----------------------------");
        System.out.println("-- Importing All Packages --");
        System.out.println("----------------------------");

        // First delete any summary output file that already exists because it is now 
        // invalid
        if(_masterSummaryFile.exists()){
            // Delete it
            try{
                boolean deleted = _masterSummaryFile.delete();
                // Throw error if not successfully deleted.
                if(deleted==false)
                    throw new Exception("File.delete() [_masterSummaryFile.delete()] indicated failure by returning false.  No exception thrown.");
                // Ensure deleted
                if(_masterSummaryFile.exists()) throw new Exception("The delete operation on file '"+_masterSummaryFile.getAbsolutePath()+"' did not report any error, but a subsequent existance check indicates it still exists.");
            }catch(Exception exc){
                throw new Exception("Could not clear existing master summary file by the name of '"+_masterSummaryFile.getAbsolutePath()+"'.  When a new import process beings, any existing such file becomes invalid.  Delete the file manually because the tool could not delete it for the following reason: "+exc.getMessage());
            }
        }//end if(_masterSummaryFile.exists()){

        // Import all if they have not yet been completed or double check if we 
        // are retrying aborted packages.
        if(_retryAbortedPackages || (_allPackagesCompleted==false)){
            
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
                        // If using transactions, start an explicit transaction
                        if(_useTransactions){
                            printDebug("--> Starting Explicit Transaction");
                            _dsICM.startTransaction();
                        }//end if(_useTransactions){
                        // Import One Package
                        morePackages = importNextPackage();

                        // If using transactions, commit explicit transaction
                        if(_useTransactions){
                            printDebug("--> Committing Explicit Transaction");
                            _dsICM.commit();
                        }//end if(_useTransactions){

                        // Tell the attempt manager that everything is complete.  The loop should not restart.
                        _packageLevelAttemptManager.setComplete();

                    }catch(Exception exc){
                        // If using transactions, rollback explicit transaction
                        if(_useTransactions){
                            printDebug("--> ERROR --> Rolling Back Explicit Transaction");
                            try{_dsICM.rollback();}
                            catch(Exception exc2){
                                printDebug("ERROR:  Explicit Transaction Rollback Failed.  Regardless, changes are not comitted.  Error behind rollback error:  "+exc2.getMessage());
                            }
                        }//end if(_useTransactions){
                        
                        // Report Error to Log
                        track(TRACKING_TAG_FAILURE_PACKAGE_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                        _statNumPackageLevelErrors++; // Track for statistics
                        
                        // If it ran out of disk space, throw a fatal error
                        if((exc!=null)&&(exc.getMessage()!=null)&&(exc.getMessage().compareToIgnoreCase(EXPECTED_OUT_OF_SPACE_MESSAGE)==0)){
                            TExportManagerICM_PackageInfo packageInfo = _packageInfoList.get(TExportManagerICM_PackageInfo.getKey(_currentPackageNum));
                            if(packageInfo==null) throw new InternalError("Internal Error:  Could not locate the PackageInfo object for package '"+_currentPackageNum+"'.");
                            throw new Exception("RAN OUT OF DISK SPACE AT '"+packageInfo.getFolder().getAbsolutePath()+"'.  In order to import each package, there must be enough disk space to write tracking and summery files for each package in their respective folder.  Make more space available or move this package folder to the master folder at another volume / storage location.  The tool will be able to locate it at the new location.");
                        }//end if(exc.getMessage().compareToIgnoreCase(EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                        
                        // The attempt manager will handle error reporting & throwing if needed.
                        _packageLevelAttemptManager.handleAttemptFailure(exc);
                        
                        // If allowed to continue, determine if it is time to skip this 
                        // package and move onto the next.
                        if(_packageLevelAttemptManager.getAttemptNum() > _skipPackageAfterAttemptNum){
                            printGeneral("======================");
                            printGeneral("SKIPPING PACKAGE '"+_currentPackageNum+"'");
                            printGeneral("----------------------");
                            printGeneral("Exceeded '"+_skipPackageAfterAttemptNum+"' Failures ");
                            printGeneral("======================");
                            // - Note in the trace file that it is abandoned.
                            TExportManagerICM_PackageInfo packageInfo = _packageInfoList.get(TExportManagerICM_PackageInfo.getKey(_currentPackageNum));
                            if(packageInfo==null) throw new InternalError("Internal Error:  Could not locate PackageInfo object for package '"+_currentPackageNum+"'.");
                            track(TRACKING_TAG_PACKAGE_ABANDONED,packageInfo.toString());
                            // - Save failed package on failed package list.
                            _failedPackageInfoList.put(packageInfo.getKey(),packageInfo);
                            // - Move onto the next
                            _currentPackageNum++;
                            // If there are no more packages, stop
                            if(_currentPackageNum > _packageInfoList.size()){
                                printGeneral("--- There are no more packages to import.");
                                morePackages = false;
                            }else{
                                printGeneral("--- Moving on to package '"+_currentPackageNum+"'...");
                                morePackages = true;
                            }
                        }//end if(_packageLevelAttemptManager.getAttemptNum() > _skipPackageAfterAttemptNum){
                    }//end }catch(Exception exc){
                
                }//end while(_packageLevelAttemptManager.next()){

                // Attempt managers restart after surpass single error.
                _masterLevelAttemptManager.reset();
                            
            }//end while(morePackages){

            // Report Completed Packages
            trackTime(TRACKING_TAG_ALL_PACKAGES_COMPLETED);
        }//end if(_allPackagesCompleted==false){
        // Record current completed timestamp
        _statCompletedTimestamp = new Timestamp(System.currentTimeMillis());

        // Delete any remaining temporary files
        File tempPackageTrackingFile = null;
        try{
            tempPackageTrackingFile = new File(_masterLogFileDirectory,_masterPackageName+"_tempSinglePackageLog.trk");
            tempPackageTrackingFile.delete();
        }catch(Exception exc){
            printGeneral("WARNING:  Could not clear up temporary file '"+tempPackageTrackingFile.getAbsolutePath()+"' due to error \""+exc.getMessage()+"\".");   
        }


        // Validate that all packages are accounted for.  All packages should
        // appear either on the completed list or failed list, and none on both.
        // For each package, make sure it is on one of the two lists.
        for(int i=1; i<_packageInfoList.size(); i++){
            TExportManagerICM_PackageInfo packageInfo = _packageInfoList.get(TExportManagerICM_PackageInfo.getKey(i));
            if(packageInfo==null) throw new InternalError("Internal Error:  Could not locate PackageInfo object for package '"+_currentPackageNum+"'.");
            boolean found = false;
            // Check both lists
            if(_completedPackageInfoList.containsKey(packageInfo.getKey()))
                found = true;
            if(_failedPackageInfoList.containsKey(packageInfo.getKey())){
                // If on other list, report error since it cannot be on both.
                if(found) // already found
                    throw new InternalError("Internal Error:  Package '"+i+"' found on both the completed and failed/aborted package lists.  A package should only appear on one list, not both.  Package: "+packageInfo.toString());
                found = true;
            }//end if(_completedPackageInfoList.containsKey(packageInfo.getKey())){
            
            // If not found, report error.
            if(found==false) // not found
                throw new InternalError("Internal Error:  Package '"+i+"' is not accounted for.  It did not appear on either the completed or failed/aborted package list.  The package should have completed for failed out.  Just because it is not accounted for, does not mean that it was not imported.  Review the tracking file for completion status.  If the tool lost track of the package and it was successfully import, the tool might re-import the same package again if restarted.  Restart with caution to ensure no duplicate information is created.  Package: "+packageInfo.toString());
        }//end for(int i=1; i<_packageInfoList.size(); i++){
        
        System.out.println("--- All Packages Completed Import (Total '"+(_currentPackageNum-1)+"').");
    }//end importAllPackages()
    
   /**
    * Import the next package.  The current package will be imported,      
    * and if successful, the pointer will move onto the next package.
    * @return Returns true if there are likely more packages after this one.
    **/
    private boolean importNextPackage() throws DKException, Exception{
        boolean morePackages = true; // Assume there are more unless we find otherwise.

        // Get the next package, if one exists.
        printDebug("    --> Getting next package information...");
        TExportManagerICM_PackageInfo packageInfo = getNextPackageInfo();

        // If there is no next package, return.
        if(packageInfo==null){
            // Double check for internal error
            if(_currentPackageNum <= _packageInfoList.size())
                throw new InternalError("Internal Error:  Did not find the next PackageInfo object when the current package was determined to be a valid package, '"+_currentPackageNum+"'.");
            printDebug("--- No packages remaining to import.");
            morePackages = false;
        }//end if(_currentPackageNum > _packageInfoList.size()){
        else{ // Otherwise export the next package since it exists.

            System.out.println("----------------------------------");
            System.out.println("   Importing Package '"+_currentPackageNum+"' / '"+_packageInfoList.size()+"'.");
            System.out.println("----------------------------------");

            // Get next package to import
            // Track Starting New Package
            track(TRACKING_TAG_PACKAGE_STARTED,(new Integer(_currentPackageNum)).toString());
            printGeneral("--- Loading Package '"+_currentPackageNum+"' / '"+_packageInfoList.size()+"'...");
            
            // Load the package
            // - Validate Package File Exits
            File packageFile = packageInfo.getPackageFile();
            if(packageFile.exists()==false) throw new Exception("MISSING PACKAGE FILE '"+packageFile.getAbsolutePath()+"' from package '"+_currentPackageNum+"'.  Expected file '"+COMMON_EXPORT_PACKAGE_NAME+"' in folder '"+packageInfo.getFolder().getAbsolutePath()+"'.  Be sure all file were copied.  Each successful export package should have a central package file by this name.");
            printDebug("    --> Validated Pacage File '"+packageFile.getAbsolutePath()+"'.");

            // - Load Package
            printDebug("    --> Loading Export Package Data...");
            TExportPackageICM exportPackage = new TExportPackageICM(packageFile.getAbsolutePath(), _importOptions);
            printDebug("    --> Package Loaded.");

            // Import Package
            printGeneral("--- Importing Package '"+_currentPackageNum+"' / '"+_packageInfoList.size()+"'...");
            TExportPackageICM.ImportRecord[] importRecords = exportPackage.importItems(_dsICM, _importOptions);
            printGeneral("--- Completed Importing Package '"+_currentPackageNum+"' / '"+_packageInfoList.size()+"'...");

            // Validte Records
            if(importRecords.length!=packageInfo.getNumItems())
                throw new InternalError("An inconsistency was found in the number of items indicated as imported by the single-package import tool.  Package '' contains '"+packageInfo.getNumItems()+"' items, but only '"+importRecords.length+"' items were recorded as imported by the single-package tool.");

            // Record Results
            printDebug("    --> Recording success...");
            // - Record PID Mapping for the Reconciliation & Completion Marker tool.
            recordMapping(importRecords);
            // - Report to Tracking Log
            track(TRACKING_TAG_PACKAGE_COMPLETED, packageInfo.toString());
            // - Save Package Info to completed list
            _completedPackageInfoList.put(packageInfo.getKey(),packageInfo);
            // - Remove it from the failed list if it was retying an aborted package.
            if(_failedPackageInfoList.containsKey(packageInfo.getKey())){
                _failedPackageInfoList.remove(packageInfo.getKey());   
                printDebug("    --> Clearing prior failed/aborted status of package '"+packageInfo.getPackageNum()+"'.");
            }//end if(_failedPackageInfoList.containsKey(packageInfo.getKey())){
            // Allow next package to move on
            _currentPackageNum++;

            // Determine if there is likely a next package.
            if(_currentPackageNum > _packageInfoList.size()){ // If we moved past he last one, we are done.
                morePackages = false;
                System.out.println("--- Completed Package '"+(_currentPackageNum-1)+"'.  No Remaining Packages to Import.");
            }else{
                morePackages = true;  // We will assume true otherwise.
                System.out.println("--- Completed Package '"+(_currentPackageNum-1)+"'.  More Packages Remain to Import.");
            }
        }//end else{ // Otherwise export the next package since it exists.
        
        return(morePackages);
    }//end importNextPackage

   /**
    * Find the specified folder among the storage location list.  If found,
    * return the storage location number.
    * @param folderStr - Absolute path to a folder.
    * @return Returns the storage location number in which it was found, -1 if not found.
    **/
    private int findStorageLocation(String folderStr){
        int foundAtLocation = -1;
        for(int i=0; (i<_storageLocations.length) && (foundAtLocation<0); i++){
            if(_storageLocations[i].getAbsolutePath().compareToIgnoreCase(folderStr.trim())==0){
                foundAtLocation = i; // note that we found it.
            }//end if(_storageLocations[i].getAbsolutePath().compareToIgnoreCase(folderStr.trim())==0){
        }//end for(int i=0; (i<_storageLocations.length) && (foundAtLocation<0); i++){
        return(foundAtLocation);
    }//end findStorageLocation()

   /**
    * Get the next PackageInfo object for the next package to be imported.
    * If the user is trying failed packages, first get them from that list.
    * Otherwise pick up after the last completed package.
    * @return Returns the TExportManager_PackageInfo object for the next
    *         package to import.
    **/
    private TExportManagerICM_PackageInfo getNextPackageInfo(){
        // Search for next package that needs to be imorted.  
        TExportManagerICM_PackageInfo packageInfo = null;
        boolean foundNext   = false;
        boolean noMoreExist = false;
        while((foundNext==false) && (noMoreExist==false)){ // continue until we find one
            // Get current package from the complete package list in the master package.
            packageInfo = _packageInfoList.get(TExportManagerICM_PackageInfo.getKey(_currentPackageNum));
            if(packageInfo==null) throw new InternalError("Internal Error:  Could not locate next package '"+_currentPackageNum+"' in the total package list of size '"+_packageInfoList.size()+"'.  If this happens, it is likely that the program attempted to find a next package to import when there were no more packages to import.");
            // If if has already been completed, try the next number
            if(_completedPackageInfoList.containsKey(packageInfo.getKey())){
                printDebug("        - Package '"+_currentPackageNum+"' already completed.  Checking next...");
                foundNext=false;
            }//end if(_completedPackageInfoList.containsKey(packageInfo.getKey())){
            else{ // Otherwise it wasn't completed, so check if it was aborted.
                // If we are retrying aborted packages
                if(_retryAbortedPackages)
                    foundNext = true;
                // or if it isn't on the failed list, use this package.
                else if(_failedPackageInfoList.containsKey(packageInfo.getKey())==false)
                    foundNext = true;
                // Otherwise we aren't retrying packages and it is marked as failed, so move onto the next.
                else if((_retryAbortedPackages==false) && (_failedPackageInfoList.containsKey(packageInfo.getKey())==true))
                    foundNext = false;
                else // Error - should reach this point
                    throw new InternalError("Internal Error:  Logic fell through all expected conditions while selecting the next package number that should be imported.");
            }//end else{ // Otherwise it wasn't completed, so check if it was aborted.

            // If didn't find next, try the next one
            if(foundNext==false)
                _currentPackageNum++;
            
            // If we walked off the end, that means that this was probalby a restart that
            // was retrying aborted packages.  We need to stop of there is no next package
            if(_currentPackageNum > _packageInfoList.size()){
                printDebug("        - No more packages to complete.  All have either completed or aborted.");
                noMoreExist=true;
            }
        }//end while(packageInfo==null){ // continue until we find one

        // If no more exist, return 'null'
        if(noMoreExist)
            packageInfo = null;

        // Return the packageInfo that we found
        return(packageInfo);
    }//end getNextPackageInfo()

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
            // <ignore all configuration data for the export system>
            // PACKAGES
            // - Export Package
            if(line.startsWith(MASTER_PACKAGE_TAG_PACKAGE_EXPORTED)){
                String value = line.substring(MASTER_PACKAGE_TAG_PACKAGE_EXPORTED.length());
                TExportManagerICM_PackageInfo packageInfo = new TExportManagerICM_PackageInfo(value,_storageLocations,_importOptions);
                // Detect out of order
                if(packageInfo.getPackageNum()!=nextPackageNumExpected)
                    throw new Exception("Package out of order in line '"+lineNum+"' in tracking file '"+_masterPackageFile.getAbsolutePath()+"' package list section.  Expected package number '"+_currentPackageNum+"', but instead found package '"+packageInfo.getPackageNum()+"'.  Package \""+packageInfo.toString()+"\" was constructed from tracking file entry \""+value+"\".");
                //Only done in order:  // Detect if already marked completed.  If done twice, that indicates duplication occurred of an entire package.  This is highly unlikely.
                //Only done in order:  if(_packageInfoList.containsKey(packageInfo.getKey())){
                //Only done in order:      TExportManagerICM_PackageInfo existingPackageInfo =  _completedPackageInfoList.get(packageInfo.getKey());
                //Only done in order:      throw new Exception("More than one completion notices found for same package number, '"+packageInfo.getPackageNum()+"'.  The second notice was found at line '"+lineNum+"' in tracking file '"+_masterTrackingFile.getAbsolutePath()+"' package progress section.  Package \""+packageInfo.toString()+"\" was constructed from tracking file entry \""+value+"\".  Already found completed package tracked as \""+existingPackageInfo.toString()+"\".");
                //Only done in order:  }
                // Save as completed package
                printDebug("--> Recognized package '"+nextPackageNumExpected+"': "+packageInfo.toString());
                _packageInfoList.put(packageInfo.getKey(),packageInfo);
                nextPackageNumExpected++; // and increment the package number to the next one expected
                // Record total count
                _statNumTotalItems += packageInfo.getNumItems();
            }
            // ABANDONED FOLDERS
            // <not applicable to import tool>
            
        }//end for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.

        // Close the file
        file.close();

        printDebug("    --> Package Loading Complete.");
    }//end loadPackageInformation()
    
   /**
    * If debug printing is enabled in the TExportPackageICM.ImportOptions, print the
    * specified message.  If it is turned off, ignore the request.
    * If no Export Options have been loaded yet (null object), assume
    * enabled.
    * @param debugMessage = Debug message to print if debug printing enabled.
    **/
    private void printDebug(String debugMessage){
        if(    (_importOptions==null)
            || (_importOptions.getPrintDebugEnable())
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
    * If trace printing is enabled in the TExportPackageICM.ImportOptions, print the
    * specified message.  If it is turned off, ignore the request.
    * If no Export Options have been loaded yet (null object), assume
    * enabled.
    * @param traceMessage = Trace message to print if trace printing enabled.
    **/
    private void printTrace(String traceMessage){
        if(    (_importOptions==null)
            || (_importOptions.getPrintTraceEnable())
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
        System.out.println("===============================================");
        System.out.println("IBM DB2 Content Manager                    V"+ver);
        System.out.println("Tool Program:  TImportManagerICM");
        System.out.println("===============================================");
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
        System.out.println("===============================================");
        System.out.println("IBM DB2 Content Manager                    V"+ver);
        System.out.println("Tool Program:  TImportManagerICM");
        System.out.println("-----------------------------------------------");
        if(_masterPackageName.compareToIgnoreCase(DEFAULT_MASTER_PACKAGE_NAME)==0) 
              System.out.println("       Master Name: "+_masterPackageName);
        else  System.out.println("       Master Name: "+_masterPackageName+" (default)");
              System.out.println("         Datastore: "+_database);
              System.out.println("         User Name: "+_userName);
              System.out.println("      Connect Opts: "+_connOpts);
        if(_iniFileName.compareToIgnoreCase(DEFAULT_INI_FILE_NAME)==0)
              System.out.println("       Config File: "+_iniFileName+" (default)");
        else  System.out.println("       Config File: "+_iniFileName);
        if(_retryAbortedPackages)
              System.out.println("Retry Aborted Pkgs: Yes");
        else  System.out.println("Retry Aborted Pkgs: No");
              System.out.println("       Master Logs: "+_masterLogFileDirectory.getAbsolutePath());
        StringBuffer storageInfo = new StringBuffer();
        for(int i=0; i<_storageLocations.length; i++){
            storageInfo.append(_storageLocations[i].getAbsolutePath());
            storageInfo.append(", ");
        }
              System.out.println("Package Volumes: "+storageInfo.toString());
        System.out.println("===============================================");
        System.out.println("");
    }//end printSettingInformation()

   /**
    * Record PID Mapping for the Reconciliation & Completion Marker tool.
    * Record the mapping from the previous item Id to the new item Ids
    * in a separate file.
    * @param importRecords - TExportPackageICM.ImportRecord array returned by 
    *                        TExportPackageICM.importItems().
    **/
    private void recordMapping(TExportPackageICM.ImportRecord[] importRecords) throws IOException{

        printDebug("    --> Recording Imported Item Mapping...");
        printDebug("        - Preparing Record in Memory...");
        StringBuffer fileStr = new StringBuffer();
        // Get the system's newline separator.
        String newline = System.getProperty("line.separator");
        // - If file doesn't yet exist, write header for new record
        if(_masterImportRecordFile.exists()==false){
            // - File Type Identifier
            fileStr.append(IMPORT_RECORD_TAG_IDENTIFIER);
            // - Write Version of Package Tool
            fileStr.append(" v" + IMPORT_RECORD_TAG_FORMAT_VERSION);
            fileStr.append(newline);
        }//end if(_masterImportRecordFile.exists()==false){
        // - Begin Mapping Section Tag + PACKAGE NUM (Allows later detection if record
        //   did not complete being written)
        fileStr.append(IMPORT_RECORD_TAG_SECTION_MAPPING_BEGIN);
        TExportManagerICM_PackageInfo packageInfo = _packageInfoList.get(TExportManagerICM_PackageInfo.getKey(_currentPackageNum));
        if(packageInfo==null) throw new InternalError("Internal Error:  Could not locate PackageInfo object for package '"+_currentPackageNum+"'.");
        fileStr.append(packageInfo.toString());
        fileStr.append(newline);
        // - Write Mapping for each record
        for(int i=0; i < importRecords.length; i++){
            TExportPackageICM.ImportRecord importRecord = (TExportPackageICM.ImportRecord) importRecords[i];
            fileStr.append(importRecord.toString());
            fileStr.append(newline);
        }//end for(int i=0; i < importRecords.length; i++){
        // - End Tag
        fileStr.append(IMPORT_RECORD_TAG_SECTION_MAPPING_END);
        fileStr.append(packageInfo.toString());
        fileStr.append(newline);

        // If we are on the first package, create new mapping record.
        boolean append = false;
        if(_currentPackageNum==1){
            append = false;
        }//end if(_currentPackageNum==1){
        // If not on package 1 and it does not exist, report that it is missing.
        else if(_masterImportRecordFile.exists()==false){
            throw new InternalError("IMPORT MAPPING RECORD '"+_masterImportRecordFile.getAbsolutePath()+"' IS MISSING!  After importing package '"+_currentPackageNum+"', the tool attempted to record the mapping from the original system to the new system in this file.  However, it should have been created after package '1' was imported and saved with that package's mapping along with all subsequent packages up until this package, '"+_currentPackageNum+"'.  Be sure you are still using the same master log file directory as before if you have restarted your import.  The tool cannot continue because it would lose all mappings from earlier packages which are needed for the Reconciliation and Completion Marker tools.  Find file '"+_masterImportRecordFile.getName()+"' that was expected in '"+_masterLogFileDirectory.getAbsolutePath()+"'.  If you wish to assume this loss, get a copy of this file from another package and clear its contents except for the file identifier tag and version.");
        }//end else if(_masterImportRecordFile.exists()==false){
        // Otherwise append to existing file.
        else{
            append = true;
        }//end else{
        // Write to Disk / Overwrite any exisitng file
        // - Open file for writing
        printDebug("        - <Opening File>...");
        FileWriter file = null;
        try{
            file = new FileWriter(_masterImportRecordFile.getAbsolutePath(),append);
            // - Write to disk.
            printDebug("        - <Writing File>...");
            file.write(fileStr.toString());
        }catch(IOException exc){
            String message = "ERROR WRITING THE IMPORT MAPPING RECORD '"+_masterImportRecordFile.getAbsolutePath()+"' for package '"+packageInfo.getPackageNum()+"' due to \""+exc.getMessage()+"\".  The tool must be able to append to this record in order to privide the mapping from the previous system to this system for the Reconcialiation and Completion Marker tools.   Make sure this directory, '"+_masterLogFileDirectory.getAbsolutePath()+"', and file, '"+_masterImportRecordFile.getAbsolutePath()+"', can be written to.  The master log file directory must always have enough space to save this information.";
            printError(message);
            // If it was because it ran out of space, report specialized error
            if(exc.getMessage().compareToIgnoreCase(EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                throw new IOException("RAN OUT OF DISK SPACE "+message);
            }
            throw new IOException(message);
        }//end catch(IOException exc){
        // Close the file
        printDebug("        - <Closing File>...");
        file.close();
        
        printDebug("    --> Completed Recording Imported Item Mapping...");
    }//end recordMapping()

   /**
    * Execute the Primary Program
    **/
    public void run() throws DKException, Exception{
        
        // Peform the feature requested by the user.

        // This tool only imports items
        importProcess();
        
    }//end run()

   /**
    * Set the number of attempts after which the tool should give
    * up on a particular package due to errors.  The aborted package
    * will be noted.  Validate for only valid values
    * @param numAsStr - Number of attempts
    **/
    private void setSkipPackageAfterAttemptNum(String numAsStr){
        // Convert to int, catching incorrect format in string.
        try{ _skipPackageAfterAttemptNum = Integer.valueOf(numAsStr).intValue(); }
        catch(ClassCastException exc){
            throw new IllegalArgumentException("The setting specified, '"+numAsStr+"', for the attempt number after which the tool should skip / abort a particular package due to errors is invalid.  The number must be a whole number, such as 10.  Make sure your value does not include any decimal places and contains only numbers.  Examine your properties file '"+_iniFileName+"' for tag '"+CONFIG_TAG_SKIP_PACKAGE_AFTER_ATTEMPT_NUM+"'.");
        }
        // Ensure > 0
        if(_skipPackageAfterAttemptNum <= 0) throw new IllegalArgumentException("The setting specified, '"+_skipPackageAfterAttemptNum+"', for the attempt number after which the tool should skip / abort a particular package due to errors is invalid.  The number must be a positive (>=1) whole number, such as 10.  Make sure your value does not include any decimal places and contains only numbers.  Examine your properties file '"+_iniFileName+"' for tag '"+CONFIG_TAG_SKIP_PACKAGE_AFTER_ATTEMPT_NUM+"'.");
    }//end setSkipPackageAfterAttemptNum()

   /**
    * Set the storage location(s) for use in storage of exported data.
    *
    * Storage locations specified in a comma-delimited list:
    *
    *    <Storage Location 1>, <Storage Location 2>, ...
    *
    * @param storageLocationsStr - comma-delimited list of storage locations.
    **/
    private void setStorageLocations(String storageLocationsStr) throws IllegalArgumentException{
        // Validate Input
        if(storageLocationsStr==null) throw new IllegalArgumentException("The storage locations option specified was 'null'.  Expected valid list of storage locations.");
        if(storageLocationsStr.trim().length()==0) throw new IllegalArgumentException("The storage locations option specified was '"+storageLocationsStr+"' (empty string).  Expected valid list of storage locations.  Expected a valid folder in which the exported data could be placed.  Review storage location entries in the command line argument option -v/volume or '"+_iniFileName+"' configuration file at tag '"+CONFIG_TAG_STORAGE_LOCATIONS+"'.");
        
        // Parse Storage Location List.
        StringTokenizer locationTokens = new StringTokenizer(storageLocationsStr,STORAGE_LOCATION_DELIMITER);

        // Create file array
        File[] storageLocations = new File[locationTokens.countTokens()];
        
        for(int i=0; locationTokens.hasMoreTokens(); i++){
            String storageLocation = locationTokens.nextToken().trim();
            storageLocations[i] = new File(storageLocation);
            // Validate that it exists and is a folder
            if(storageLocations[i].isAbsolute()==false)  throw new IllegalArgumentException("Volume / Storage location '"+storageLocations[i].getAbsolutePath()+"' is not an absolute path.  Expected an absolute path to a valid folder in which the exported package data could be placed.  Review storage location entry '"+storageLocations[i].getAbsolutePath()+"' from specified list '"+storageLocationsStr+"' entered in the command line argument option -v/volume or '"+_iniFileName+"' configuration file at tag '"+CONFIG_TAG_STORAGE_LOCATIONS+"'.");
            if(storageLocations[i].exists()==false)      throw new IllegalArgumentException("Volume / Storage location '"+storageLocations[i].getAbsolutePath()+"' does not exist.  Expected an absolute path to a valid folder in which the exported package data could be placed.  Review storage location entry '"+storageLocations[i].getAbsolutePath()+"' from specified list '"+storageLocationsStr+"' entered in the command line argument option -v/volume or '"+_iniFileName+"' configuration file at tag '"+CONFIG_TAG_STORAGE_LOCATIONS+"'.");
            if(storageLocations[i].isDirectory()==false) throw new IllegalArgumentException("Volume / Storage location '"+storageLocations[i].getAbsolutePath()+"' is not a directory / folder.  Expected an absolute path to a valid folder in which the exported package data could be placed.  Review storage location entry '"+storageLocations[i].getAbsolutePath()+"' from specified list '"+storageLocationsStr+"' entered in the command line argument option -v/volume or '"+_iniFileName+"' configuration file at tag '"+CONFIG_TAG_STORAGE_LOCATIONS+"'.");
        }//end while(locationTokens.hasMoreTokens()){
        
        // Save file array
        _storageLocations = storageLocations;
    }

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
            if(exc.getMessage().compareToIgnoreCase(EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
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
        
        // Determine if an existing import was in progress that needs to be resumed
        boolean reloadFromTracking = toolStartupDetermineIfReloadTracking();

        // Reload progress information to continue where left off.
        // Otherwise will start from beginning if no reloaded.
        if(reloadFromTracking){ // Continue where left off
            toolStartupReloadFromTracking();
        }//end if(reloadFromTracking){ // Restart if needed

        // If never started, create new tracking file, starting with header information
        if(_statNumToolRestarts<=0){
            toolStartupCreateNewTrackingFile();
        }//end if(_statNumToolRestarts<=0){

        // Tool Started
        printDebug("        - Tracking Tool Start Information...>");
        trackTime(TRACKING_TAG_TOOL_START);
        _statNumToolRestarts++;
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
            if(exc.getMessage().compareToIgnoreCase(EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                throw new IOException("RAN OUT OF DISK SPACE WRITING MASTER TRACKING FILE '"+_masterTrackingFile.getAbsolutePath()+"'.  The tool could not write to the tracking file since there was no more disk space.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
            }
            throw new IOException(message);
        }//end catch(IOException exc){
        printDebug("        - <completed>");
    }//end toolStartupCreateNewTrackingFile()

   /**
    * Determine if the tool should reload an existing msater package from tracking.
    * @return Returns 'true' if an existing tracking should be reloaded, 'false' if a 
    *         new one should be created (overwriting any exisitng)
    **/
    private boolean toolStartupDetermineIfReloadTracking() throws IllegalArgumentException, IOException{
        // If in restart mode or if an existing tracking file is found and not in new mode, 
        // reload and pick up where it left off.
        boolean reloadFromTracking = false;  // Determines whether or not we need to reload an existing run from the tracking file.
        if(_restartRequested){
            printDebug("        - Detected Restart Request.");
            // Make sure tracking file exists
            if(_masterTrackingFile.exists()==false)
                throw new IllegalArgumentException("Tool cannot honor restart request because no tracking file was found for master package '"+_masterPackageName+"'.  Based on the master log file location specified, '"+_masterLogFileDirectory.getAbsolutePath()+"', expected to find tracking file '"+_masterTrackingFile.getAbsolutePath()+"'.");
            reloadFromTracking = true;  // Indicate that the tracking file will need to be laoded.
        }//end if(_restartRequested){
        else if(_newRunRequested){ // If a new run was requested, just overwrite automatically
            if(_masterTrackingFile.exists())
                printDebug("        - New Process Requested.  Overwriting prevous data:  "+_masterTrackingFile.getAbsolutePath());
            else 
                printDebug("        - New Process Requested.  No previous data found.");
        }//end else if(_newRunRequested){
        else{ // Otherwise if the user did not specify, prompt if an existing file is found
            printDebug("        - Detecting Previous Data...");
            if(_masterTrackingFile.exists()){
                // Prompt user
                System.out.println("");
                System.out.println(" PREVIOUS IMPORT PROCESS DETECTED:");
                System.out.println("     Master Package Name:  "+_masterPackageName);
                System.out.println("    Master Log Directory:  "+_masterLogFileDirectory.getAbsolutePath());
                System.out.println("    Master Tracking File:  "+_masterTrackingFile.getAbsolutePath());
                System.out.println("");
                String answer = TExportManagerICM.promptUser(" Do you want to continue existing import process?  (Y/N) >  ").trim();
                // Validate Answer
                if((answer==null)||(answer.length()==0)) throw new IllegalArgumentException("Invalid response '"+answer+"' to prompt to continue exisitng package.  Expected 'Y' or 'N'.");
                if( (answer.compareToIgnoreCase("Y")==0) || (answer.compareToIgnoreCase("YES")==0)){
                    printGeneral("--> Understood 'Yes', continue where existing import process left off.");
                    reloadFromTracking = true;    
                }else if( (answer.compareToIgnoreCase("N")==0) || (answer.compareToIgnoreCase("NO")==0)){
                    printGeneral("--> Understood 'No', do not continue where existing import process left off.");
                    reloadFromTracking = false;
                    // Then prompt to overwrite
                    System.out.println("");
                    System.out.println("WARNING:  If Conflict handling feature is not enabled in your settings");
                    System.out.println("          and there are no unique attributes, any data already imported");
                    System.out.println("          by the previous proces will be duplicated.  If you do start over,");
                    System.out.println("          back up the tracking file listed above.");
                    System.out.println("");
                    answer = TExportManagerICM.promptUser("Start over & overwriting existing tracking data? (Y/N) >  ").trim();
                    if( (answer.compareToIgnoreCase("Y")==0) || (answer.compareToIgnoreCase("YES")==0)){
                        printGeneral("--> Understood 'Yes', overwrite existing master package.");
                    }else{
                        throw new IllegalArgumentException("User selected not to start over from beginning and overwrite existing tracking data for a detected prior import process.  Choose a different master file log location or master file name, answer yes to the 'continue from existing import' prompt, or use the command line input to specify restart or new import process.");   
                    }
                }
            }//end if(_masterTrackingFile.exists()){
            else{
                printDebug("        - No Previous Import Process Found.  Starting New Import Process.");
            }//end else of if(_masterTrackingFile.exists()){
        }//end else of if(_restartRequested){
        return(reloadFromTracking);
    }//end toolStartupDetermineIfReloadTracking()

   /**
    * Reload an existing master package based on the master tracking file.
    **/
    private void toolStartupReloadFromTracking() throws FileNotFoundException, IOException, Exception{
        printDebug("    --> Reloading from tracking file '"+_masterTrackingFile.getAbsolutePath()+"'...");

        // Open File
        FileReader fileReader = new FileReader(_masterTrackingFile.getAbsolutePath());
        BufferedReader file   = new BufferedReader(fileReader);

        // Read & Validate File Identifier & Version Check
        String line = file.readLine();
        if(!line.startsWith(TRACKING_TAG_FILE_IDENTIFIER))
            throw new IllegalArgumentException("File specified, '"+_masterTrackingFile.getAbsolutePath()+"', does not appear to be a Master Import Process Tracking File.");
        String fileVersion = line.substring(line.lastIndexOf('v')+1);
        if(fileVersion.compareTo(MASTER_PACKAGE_VERSION) != 0){
            throw new IllegalArgumentException("WARNING:  Master Import Process Tracking File was not exported from the same version of this tool.  File is from version '"+fileVersion+"' version, but the current version is '"+MASTER_PACKAGE_VERSION+"'.  The old format may not be compatable with the new format.  Use the same version of Export Manager and Import Manager tools as was used to write the existing file.");
        }//end if(fileVersion.compareTo(MASTER_PACKAGE_VERSION) != 0){

        // Read File Line-by-line, handling each package as found.
        for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.
            printDebug("LINE "+lineNum+":  "+line);

            // CONFIGURATION
            // - Master Name
            if(line.startsWith(FILE_TAG_MASTER_PACKAGE_NAME)){
                String value = line.substring(FILE_TAG_MASTER_PACKAGE_NAME.length());
                if(value.compareTo(_masterPackageName)!=0) // Case sensitive
                    throw new Exception("The master package configuration that created tracking file '"+_masterTrackingFile.getAbsolutePath()+"' is not the same as the current configuration.  For this configuration parameter, they must be identical.  The master package name in the tracking file is '"+value+"' (line '"+lineNum+"') while the current package is '"+_masterPackageName+"'.");
            }
            // - Datastore
            else if(line.startsWith(FILE_TAG_DATABASE)){
                String value = line.substring(FILE_TAG_DATABASE.length());
                if(value.compareToIgnoreCase(_database)!=0)
                    throw new Exception("The master package configuration that created tracking file '"+_masterTrackingFile.getAbsolutePath()+"' is not the same as the current configuration.  For this configuration parameter, they must be identical.  The datastore alias name in the tracking file is '"+value+"' (line '"+lineNum+"') while the current package is '"+_database+"'.");
            }
            // - User ID
            else if(line.startsWith(FILE_TAG_USERNAME)){
                String value = line.substring(FILE_TAG_USERNAME.length());
                if(value.compareToIgnoreCase(_userName)!=0)
                    printGeneral("WARNING:  The master package configuration that created tracking file '"+_masterTrackingFile.getAbsolutePath()+"' is not the same as the current configuration.  The CM User Name in the tracking file is '"+value+"' (line '"+lineNum+"') while the current package is '"+_userName+"'.  The tool will use the new name '"+_userName+"'.");
            }
            // - Password
            // <omit>
            // - Connect Options
            else if(line.startsWith(FILE_TAG_CONNOPTS)){
                String value = line.substring(FILE_TAG_CONNOPTS.length());
                if(value.compareToIgnoreCase(_connOpts)!=0)
                    printGeneral("WARNING:  The master package configuration that created tracking file '"+_masterTrackingFile.getAbsolutePath()+"' is not the same as the current configuration.  The DB2 Content Manager Connect Options in the tracking file is '"+value+"' (line '"+lineNum+"') while the current package is '"+_connOpts+"'.  The tool will use the new options '"+_connOpts+"'.");
            }
            // - Retry Attempts
            // <itnore any difference>
            // - Retry Delay Time (ms)
            // <itnore any difference>
            // - Data Model Supports Children
            // <itnore any difference>
            // - Data Model Supports Links
            // <itnore any difference>
            // - Data Model Supports Folder Contetns
            // <itnore any difference>
            // - Data Model Supports Resource Items with Contents
            // <itnore any difference>
            // - Data Model Supports Parts with Contents
            // <itnore any difference>
            // - Configuration File
            // <ignore - use current file>
            // - Master Log File Location
            else if(line.startsWith(FILE_TAG_MASTER_LOG_FILE_DIRECTORY)){
                String value = line.substring(FILE_TAG_MASTER_LOG_FILE_DIRECTORY.length());
                if(value.compareToIgnoreCase(_masterLogFileDirectory.getAbsolutePath())!=0)
                    printGeneral("WARNING:  The master package configuration that created tracking file '"+_masterTrackingFile.getAbsolutePath()+"' is not the same as the current configuration.  The master log file directory for the tracking and summary files in the tracking file is '"+value+"' (line '"+lineNum+"') while the current package is '"+_masterLogFileDirectory.getAbsolutePath()+"'.  The tool will use the new location '"+_masterLogFileDirectory.getAbsolutePath()+"'.  It clearly found the existing tracking file which appears to be value.");
            }
            // - Master Summary File
            // <Already covered by master log file directory>
            // - Master Tracking File
            // <Already covered by master log file directory>

            // TRACKING
            // - Package Started
            // <omit - not needed to resume>
            // - Package Completed
            else if(line.startsWith(TRACKING_TAG_PACKAGE_COMPLETED)){
                String value = line.substring(TRACKING_TAG_PACKAGE_COMPLETED.length());
                TExportManagerICM_PackageInfo packageInfo = new TExportManagerICM_PackageInfo(value,_storageLocations,_importOptions);
                // Detect out of order
                // If they skip a package and need to go back, this will need to handle completed packages out of order.  PackageInfo objects are expected in order in the "_packageInfoList", but it doesn't matter for the _completedPackageInfoList.
                //completed list can be out of order:  if(packageInfo.getPackageNum()!=_currentPackageNum)
                //completed list can be out of order:      throw new Exception("Package out of order in line '"+lineNum+"' in tracking file '"+_masterTrackingFile.getAbsolutePath()+"' configuration section.  Expected package number '"+_currentPackageNum+"', but instead found package '"+packageInfo.getPackageNum()+"'.  Package \""+packageInfo.toString()+"\" was constructed from tracking file entry \""+value+"\".");
                // Detect if already marked completed.  If done twice, that indicates duplication occurred of an entire package.  This is highly unlikely.
                if(_completedPackageInfoList.containsKey(packageInfo.getKey())){
                    TExportManagerICM_PackageInfo existingPackageInfo = _completedPackageInfoList.get(packageInfo.getKey());
                    if(existingPackageInfo==null) throw new InternalError("Internal Error:  Could not lcoate the existing PackageInfo object for package '"+packageInfo.getKey()+"'.");
                    throw new Exception("More than one completion notices found for same package number, '"+packageInfo.getPackageNum()+"'.  The second notice was found at line '"+lineNum+"' in tracking file '"+_masterTrackingFile.getAbsolutePath()+"' package progress section.  Package \""+packageInfo.toString()+"\" was constructed from tracking file entry \""+value+"\".  Already found completed package tracked as \""+existingPackageInfo.toString()+"\".");
                }
                // Save as completed package
                printDebug("--> Recognized completed package '"+_currentPackageNum+"': "+packageInfo.toString());
                _completedPackageInfoList.put(packageInfo.getKey(),packageInfo);
                //completed list can be out of order: _currentPackageNum++; // and increment the current package number to the next
                // Remove from failed list if it previously failed
                if(_failedPackageInfoList.containsKey(packageInfo.getKey())){
                    _failedPackageInfoList.remove(packageInfo.getKey());
                    printDebug("    - Removing previous failed status.");
                }//end if(_failedPackageInfoList.containsKey(packageInfo.getPackageNum())){
            }
            // - Skipped (Abandoned) Packages (not lost, just skipped due to errors)
            else if(line.startsWith(TRACKING_TAG_PACKAGE_ABANDONED)){
                String value = line.substring(TRACKING_TAG_PACKAGE_COMPLETED.length());
                TExportManagerICM_PackageInfo packageInfo = new TExportManagerICM_PackageInfo(value,_storageLocations,_importOptions);
                // If marked as completed (by a restart later), omit from failed folder list.
                // Only continue to record if it is now found.
                if(_completedPackageInfoList.containsKey(packageInfo.getKey())==false){
                    // Else if already noted as failed, omit duplicate failed listing.
                    if(_failedPackageInfoList.containsKey(packageInfo.getKey())==false){
                        // otherwise add to failed folder list
                        printDebug("--> Recognized failed & aborted package '"+_currentPackageNum+"': "+packageInfo.toString());
                        _failedPackageInfoList.put(packageInfo.getKey(),packageInfo);
                    }//end if(_failedPackageInfoList.containsKey(packageInfo.getKey()==false){
                    else{
                        printDebug("--> Ignoring repeat failed & aborted status for package '"+_currentPackageNum+"': "+packageInfo.toString());
                    }
                }//end if(_completedPackgeInfoList.hasKey(packageInfo.getKey()==false){
                else{
                    printDebug("--> Ignoring failed package status of package '"+_currentPackageNum+"' since it has since been completed.  Package Information: "+packageInfo.toString());
                }
            }//end else if(line.startsWith(TRACKING_TAG_ABANDONED_PACKAGE)){
            // - All Packages Started: TRACKING_TAG_ALL_PACKAGES_STARTED
            // <ignore>
            // - All Packages Completed
            else if(line.startsWith(TRACKING_TAG_ALL_PACKAGES_COMPLETED)){
                String value = line.substring(TRACKING_TAG_ALL_PACKAGES_COMPLETED.length());
                _allPackagesCompleted = true;
                printDebug("--> Found out that master import process completed importing of all package information at '"+value+"'.");
            }
            // - Failure at master level            
            else if(line.startsWith(TRACKING_TAG_FAILURE_MASTER_LEVEL)){
                String value = line.substring(TRACKING_TAG_FAILURE_MASTER_LEVEL.length());
                _statNumMasterLevelErrors++;
                printDebug("--> Recognized failure at master level.");
            }
            // - Failure at package level      
            else if(line.startsWith(TRACKING_TAG_FAILURE_PACKAGE_LEVEL)){
                String value = line.substring(TRACKING_TAG_FAILURE_PACKAGE_LEVEL.length());
                _statNumPackageLevelErrors++;
                printDebug("--> Recognized failure at package level.");
            }
            // - Summary Started: TRACKING_TAG_SUMMARY_STARTED
            // <no action needed>
            // - Summary Completed: 
            else if(line.startsWith(TRACKING_TAG_SUMMARY_COMPLETED)){
                String value = line.substring(TRACKING_TAG_SUMMARY_COMPLETED.length());
                // If summary file doesn't exist, let it re-write it
                if(_masterSummaryFile.exists()==false){
                    printGeneral("WARNING:  Master Import Process Summary file was listed as completed in the tracking file, '"+_masterTrackingFile.getAbsolutePath()+"', but file '"+_masterSummaryFile.getAbsolutePath()+"' does not exist.  The tool will regenerate the file.");
                }else{
                    throw new IllegalArgumentException("WARNING:  Master Import Process Summary file was listed as completed in the tracking file, '"+_masterTrackingFile.getAbsolutePath()+"'.  Assuming the file was generated correctly, there is no need to restart this tool.  However, if you wish to regenerate the file, delete the Master Import Process Summary file '"+_masterSummaryFile.getAbsolutePath()+"'.");
                }
            }
            // - Tool Started: 
            else if(line.startsWith(TRACKING_TAG_TOOL_START)){
                String value = line.substring(TRACKING_TAG_TOOL_START.length());
                // Record number of restarts
                _statNumToolRestarts++;
                // If this is the first start tag found, record start time
                if(_statNumToolRestarts <= 1){
                    try{ _statStartedTimestamp = Timestamp.valueOf(value); }
                    catch(Exception exc){
                        throw new Exception("Error in line '"+lineNum+"' in tracking file '"+_masterTrackingFile.getAbsolutePath()+"' tracking section.  Expected a timestamp, but instead found '"+value+"'.  The following error occurred while attempting to convert it to a Timestamp:  "+exc.getMessage());
                    }
                }//end if(_statNumToolRestarts > 0){
                printDebug("--> Recognized tool start at '"+value+"'");
            }
            
        }//end for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.

        // Close the file
        file.close();

        printDebug("    --> Reload Complete.");
    }//end toolStartupReloadFromTracking()

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
        if(!found) throw new IllegalArgumentException("Datastore alias name '"+_database+"' is not a valid catalogged alias name available to this tool.  Ensure that the environment is setup correctly to process the configuration files in the <IBMCMROOT>/cmgmt directory.  The list of datastore alias names available to this tool are: "+availableDsNamesStr.toString());
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
        if(_restartRequested && _newRunRequested)
            throw new IllegalArgumentException("Both restart an existing master package and start over have been specified.  Use only 'r|restart', 'k|kill', or niether.  The tool will prompt if an existing master package tracking file is found if neither is selected.  Review the command line argument documentation.");
        // - Objects
        //   _dsICM                     : Validated above
        //   _completedPackageInfoList  : Not Null
        if(_completedPackageInfoList==null)  throw new InternalError("Inernal Error: Object variable '_completedPackageInfoList' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        //   _failedPackageInfoList     : Not Null
        if(_failedPackageInfoList==null)  throw new InternalError("Inernal Error: Object variable '_failedPackageInfoList' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        //   _masterLevelAttemptManager : Not Null
        if(_masterLevelAttemptManager==null)  throw new InternalError("Inernal Error: Object variable '_masterLevelAttemptManager' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        //   _packageLevelAttemptManager: Not Null
        if(_packageLevelAttemptManager==null) throw new InternalError("Inernal Error: Object variable '_packageLevelAttemptManager' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        //   _packageInfoList           : Not Validated / Loaded Later
        if(_packageInfoList==null)  throw new InternalError("Inernal Error: Object variable '_packageInfoList' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        //   _masterImportRecordFile    : Not Null
        if(_masterImportRecordFile==null) throw new InternalError("Inernal Error: Object variable '_masterImportRecordFile' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        //   _masterPackageFile         : Not Null & Exists
        if(_masterPackageFile==null) throw new InternalError("Inernal Error: Object variable '_masterPackageFile' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        if(_masterPackageFile.exists()==false) throw new IllegalArgumentException("Cannot locate the master export package file '"+_masterPackageFile.getAbsolutePath()+"' in the specified Master Log File Directory '"+_masterLogFileDirectory.getAbsolutePath()+"'.  This file should have been created by TExportManagerICM as its summary file.");
        //   _masterSummaryFile         : Not Null
        if(_masterSummaryFile==null)          throw new InternalError("Inernal Error: Object variable '_masterSummaryFile' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
        //   _masterTrackingFile        : Not Null
        if(_masterTrackingFile==null)         throw new InternalError("Inernal Error: Object variable '_masterTrackingFile' is null when it should have been initialized by 'TImportManagerICM.initObjects()'.");
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
        fileStr.append(_currentPackageNum - 1); // After the last completes, the number moves onto the next one.
        fileStr.append(newline);
        // - # Total Items
        fileStr.append(FILE_TAG_STAT_TOTAL_ITEMS);
        fileStr.append(_statNumTotalItems);
        fileStr.append(newline);
        // - # Completed Packages
        fileStr.append(FILE_TAG_STAT_TOTAL_COMPLETED_PACKAGES);
        fileStr.append(_completedPackageInfoList.size());
        fileStr.append(newline);
        // - # Failed Packages
        fileStr.append(FILE_TAG_STAT_TOTAL_FAILED_PACKAGES);
        fileStr.append(_failedPackageInfoList.size());
        fileStr.append(newline);
        // - # Manual Tool Restarts
        fileStr.append(FILE_TAG_STAT_NUM_TOOL_RESTARTS);
        fileStr.append(_statNumToolRestarts-1);
        fileStr.append(newline);
        // - # Master-Level Errors
        fileStr.append(FILE_TAG_STAT_NUM_MASTER_LEVEL_ERRORS);
        fileStr.append(_statNumMasterLevelErrors);
        fileStr.append(newline);
        // - # Package-Level Errors
        fileStr.append(FILE_TAG_STAT_NUM_PACKAGE_LEVEL_ERRORS);
        fileStr.append(_statNumPackageLevelErrors);
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

        // Write Completed Package List
        printDebug("        - <Preparing Completed Package List>...");
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_HEADER);
        fileStr.append(newline);
        // Loop through all packages, write thier package info.
        Iterator<TExportManagerICM_PackageInfo> packageInfoIter = _completedPackageInfoList.values().iterator();
        while(packageInfoIter.hasNext()){
            TExportManagerICM_PackageInfo packageInfo = packageInfoIter.next();
            // Write package description
            fileStr.append(FILE_TAG_PACKAGE_INFO);
            fileStr.append(packageInfo.toString());
            fileStr.append(newline);
        }//end while(packageInfoIter.hasNext()){
        // - Write end section tag.        
        fileStr.append(FILE_TAG_SECTION_COMPLETED_PACKAGE_LIST_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write Failed & Aborted Package List
        printDebug("        - <Preparing Failed/Aborted Package List>...");
        fileStr.append(FILE_TAG_SECTION_FAILED_PACKAGE_LIST_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_FAILED_PACKAGE_LIST_HEADER);
        fileStr.append(newline);
        // Loop through all packages, write thier package info.
        packageInfoIter = _failedPackageInfoList.values().iterator();
        while(packageInfoIter.hasNext()){
            TExportManagerICM_PackageInfo packageInfo = packageInfoIter.next();
            // Write package description
            fileStr.append(FILE_TAG_PACKAGE_INFO);
            fileStr.append(packageInfo.toString());
            fileStr.append(newline);
        }//end while(packageInfoIter.hasNext()){
        // - Write end section tag.        
        fileStr.append(FILE_TAG_SECTION_FAILED_PACKAGE_LIST_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write to Disk
        printDebug("    --> <Preparing to Write>...");
        // - Double check that there is no existing file by this name.  It should have been
        //   deleted at the beginning of exportAllPackages()
        if(_masterSummaryFile.exists()){
            throw new InternalError("Internal Error:  The Master Import Process Summary file '"+_masterSummaryFile.getAbsolutePath()+"' should have been deleted at the beginning of TImportMangerICM.importAllPackages().  However, it exists prior to writing the summary report in the TImportManagerICM.writeSummaryReport() method.");
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
            printError("ERROR WRITING MASTER IMPORT PROCESS SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"' due to: "+exc.getMessage());
            // If it was because it ran out of space, report specialized error
            if(exc.getMessage().compareToIgnoreCase(EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                throw new IOException("RAN OUT OF DISK SPACE WRITING MASTER IMPORT PROCESS SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"'.  Import had completed successfully, but the tool could not write the summary file.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
            }
            throw new IOException("ERROR WRITING MASTER IMPORT PROCESS SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"' due to error \""+exc.getMessage()+"\".  Import had completed successfully, but the tool could not write the summary file.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when the error can be bypassed on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
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
        // - Data Model Supports Children
        fileStr.append(FILE_TAG_DATA_MODEL_SUPPORTS_CHILDREN);
        if(_dataModelSupportsChildren)
            fileStr.append("Yes");
        else
            fileStr.append("No  (disabled)");
        fileStr.append(newline);
        // - Data Model Supports Links
        fileStr.append(FILE_TAG_DATA_MODEL_SUPPORTS_LINKS);
        if(_dataModelSupportsLinks)
            fileStr.append("Yes");
        else
            fileStr.append("No  (disabled)");
        fileStr.append(newline);
        // - Data Model Supports Folder Contetns
        fileStr.append(FILE_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS);
        if(_dataModelSupportsFoldersWithContents)
            fileStr.append("Yes");
        else
            fileStr.append("No  (disabled)");
        fileStr.append(newline);
        // - Data Model Supports Resource Items with Contents
        fileStr.append(FILE_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT);
        if(_dataModelSupportsResourceItemsWithContent)
            fileStr.append("Yes");
        else
            fileStr.append("No  (disabled)");
        fileStr.append(newline);
        // - Data Model Supports Parts with Contents
        fileStr.append(FILE_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT);
        if(_dataModelSupportsPartsWithContent)
            fileStr.append("Yes");
        else
            fileStr.append("No  (disabled)");
        fileStr.append(newline);
        // - Configuration File
        fileStr.append(FILE_TAG_CONFIG_FILE_NAME);
        fileStr.append(_iniFileName);
        fileStr.append(newline);
        // - Master Log File Location
        fileStr.append(FILE_TAG_MASTER_LOG_FILE_DIRECTORY);
        fileStr.append(_masterLogFileDirectory.getAbsolutePath());
        fileStr.append(newline);
        // - Master Export Package File
        fileStr.append(FILE_TAG_MASTER_EXPORT_PACKAGE_FILE);
        fileStr.append(_masterPackageFile.getAbsolutePath());
        fileStr.append(newline);
        // - Master Summary File
        fileStr.append(FILE_TAG_MASTER_SUMMARY_FILE);
        fileStr.append(_masterSummaryFile.getAbsolutePath());
        fileStr.append(newline);
        // - Master Export Package File
        fileStr.append(FILE_TAG_MASTER_IMPORT_RECORD_FILE);
        fileStr.append(_masterImportRecordFile.getAbsolutePath());
        fileStr.append(newline);
        // - Master Tracking File
        fileStr.append(FILE_TAG_MASTER_TRACKING_FILE);
        fileStr.append(_masterTrackingFile.getAbsolutePath());
        fileStr.append(newline);
        // - Storage Locations Available
        fileStr.append(FILE_TAG_STORAGE_LOCATIONS);
        for(int i=0; i<_storageLocations.length; i++){
            fileStr.append(_storageLocations[i]);
            // If there is another, add delimiter
            if((i+1)<_storageLocations.length)
                fileStr.append(TExportManagerICM.ITEMTYPE_LIST_DELIMITER);
        }//end for(int i=0; i<_storageLocations.length; i++){
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
    
   
}//end class TImportManagerICM
              
