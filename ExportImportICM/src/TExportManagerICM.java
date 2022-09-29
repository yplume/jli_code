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

Overview
    For an overview of all Sample Import / Export Tools and how these tools
    fit in with existing tools, refer to on-line support document #1167209. 

Single-Package Tools & API
    The Sample Import / Export Tools & API (TExportPackageICM, TExportICM, 
    & TImportICM) that were introduced in Version 8 Release 1 with fix pack 1 are
    designed as a building block that enables higher level applications and tools
    to make use of easily importing and exporting a set of selected items and
    handle intersections with existing data and references. The existing tool
    allows another tool to use it to select and export items as a single batch,
    formally referred to as a single "package". The single package requires
    sufficient disk space and a higher level tool or user to handle errors that
    the single-batch tool cannot recover from without intervention. All package
    data is written to the current directory on a single disk. When a single
    package becomes large, the file system can have trouble handling large sets.

Import / Export Management Tools
    A new set four Sample Import / Export Management Tools are available with
    Version 8 Release 3 to more comprehensively automate import and export of
    large data sets, handle high-volume scalability issues, manage performance,
    and much more. 
 
    The new tools build upon and enhance the existing single-package Sample
    Import / Export API, and provide enterprise-level performance optimization
    with the foresight that a higher level tool can provide. The following
    sample tools are part of the Import / Expoert Management tools.
 
New Tools 

    - Export Manager 
    - Import Manager 
    - Completion Marker 
    - Reconciler

Export Manager

    Export all items from a specified list of item types or an entire system.
    Break up the large set into multiple batches based on a configurable
    number of items per package, each written their own subdirectory as part
    of a "Master Package". Specify a list of storage volumes / storage
    locations to spread package data across automatically as each disk fills
    up. Includes advanced optimization that can save up to two-thirds of the
    performance cost depending on your data model configuration. Automatically
    detect and recover from many errors related to an enterprise-level export
    process. Provides extensive and configurable recover, restart, and resume
    capabilities to minimize time lost when recovering from errors. 

    Exported Files
        Refer to on-line support document #1168370 for information on what
        files are exported, what each contain, and their nomenclature.

    Configuration Options
        There are many configuration choices and options to review and 
        configure.  Using the configuration file is the recommended way
        to easily configure the tool.  Refer to the TImportExportICM.ini
        configuration file for full documented details. 
        
        Command line configuration options are documented in the next 
        comment block below, above the main method definition.

        ENHANCED PERFORMANCE OPTIONS
            Be sure to review the section "PERFORMANCE BOOST IF NO
            INDIRECT SELECTION" in the TImportExportICM.ini file
            for details on significantly improving the performance
            if your needs meet specific conditions.

    Sample Tool File
        TExportManagerICM.java

    LIMITATIONS
        This sample tool currently does not fully support multiple versions
        of items. It is currently tested against a flat model using documents
        with multiple parts, and no references between items such as folders,
        links, or reference attributes.  Before using this sample tool against
        other configurations and data models, fully test all tools you intend
        to use in a protected test environment.  Before using in production,
        review, test, and initiate your backup and recovery plans.

Import Manager

    Import the "Master Package" exported using Export Manager. Automatically
    detect movement of package folders across any new or altered storage
    volumes / storage locations. Automatically correct XML data for each 
    exported item with location changes. Automatically detect and recover
    from many errors related to an enterprise-level import process. Provides
    extensive and configurable recover, restart, and resume capabilities to
    minimize time lost when recovering from errors. Skip, record, and
    optionally return to individual packages after a configurable number of
    retry attempts fail to complete an item. 
    
    Configuration Options
        There are many configuration choices and options to review and 
        configure.  Using the configuration file is the recommended way
        to easily configure the tool.  Refer to the TImportExportICM.ini
        configuration file for full documented details.

        Command line configuration options are documented in the header
        comments in the TImportManagerICM.java sample file, in the
        comment block above the main method definition.

    Sample Tool File
        TImportManagerICM.java

    LIMITATIONS
        This sample tool currently does not fully support multiple versions
        of items. It is currently tested against a flat model using documents
        with multiple parts, and no references between items such as folders,
        links, or reference attributes.  Before using this sample tool against
        other configurations and data models, fully test all tools you intend
        to use in a protected test environment.  Before using in production,
        review, test, and initiate your backup and recovery plans.

Completion Marker

    While the import process is ongoing or completed with the Import Manager
    tool, return to the original system and mark the original items that have
    been successfully imported as complete. If you are migrating data, you
    can use this tool to hide items shortly after they are completed to
    eliminate the amount of time that items will appear as-is on both systems
    while import is still in progress. For each item type, there are four 
    "markers" available for marking items as complete (listed below). Each 
    marker is only applied once.
    
    Markers
        Attribute Replacement
            Replace the existing value of a specified attribute with the
            specified value. 
        
        Attribute Prefix 
            Add a specified prefix to the beginning of the existing
            value for a specified attribute. 
            
        Attribute Suffix
            Add a specified suffix to the end of the existing value for
            a specified attribute. 

        ItemType Reindex
            Change the item type of all completed items of the specified
            item type to the specified new item type. This marker only
            supports flat models (no child components).

    Configuration Options
        There are many configuration choices and options to review and 
        configure.  Using the configuration file is the recommended way
        to easily configure the tool.  Refer to the TImportExportICM.ini
        configuration file for full documented details.

        Command line configuration options are documented in the header
        comments in the TImportManagerCompletionMarkerICM.java sample
        file, in the comment block above the main method definition.

    Sample Tool File
        TImportManagerCompletionMarkerICM.java

    LIMITATIONS
        This tool is currently tested with Attribute Replacement,
        Attribute Prefix, and ItemType Reindex markers and with data
        sets based on the limitations of Export Manager and Import
        Manager identified above.  Before using this sample tool against
        other configurations and data models, fully test all tools you intend
        to use in a protected test environment.  Before using in production,
        review, test, and initiate your backup and recovery plans.

Reconciler

    After the import process has completed using Import Manager, re-examine
    the original system based on the original selection criteria and compare
    against the list of items successfully imported. Any items presently
    found on the original system that are not listed as imported by Import
    Manager, such as any new items added since the export process or those
    that failed import, will be identified. With the Reconcile Summary,
    return to Export Manager to export the missing items as a new Master 
    Package. Repeat the process until you are satisfied that all items are
    successfully imported. 

    Configuration Options
        There are many configuration choices and options to review and 
        configure.  Using the configuration file is the recommended way
        to easily configure the tool.  Refer to the TImportExportICM.ini
        configuration file for full documented details.

        Command line configuration options are documented in the header
        comments in the TImportManagerReconcilerICM.java sample file,
        in the comment block above the main method definition.

    Sample Tool File
        TImportManagerReconcilerICM.java

    LIMITATIONS
        This tool is currently tested with data sets based on the
        limitations of Export Manager and Import Manager identified
        above.  Before using this sample tool against other configurations
        and data models, fully test all tools you intend to use in a
        protected test environment.  Before using in production, review,
        test, and initiate your backup and recovery plans.

 ******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;

/************************************************************************************************
 *          FILENAME: TExportManagerICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: manages the entire set of items to be exported from a source system
 *                    and breaks them up into Export Packages (batches).
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java TExportManagerICM <options>
 *                    
 *                    Options:  If any of the following are not specified, defaults will
 *                              be used where possible, or the user will be prompted.
 *
 *                             -d/database      <you database name>
 *                             -u/user          <CM user id>
 *                             -p/password      <CM user's password>
 *                             -o/options       <Connect String Options>
 *                             -a/all           <*|itemType1>,<itemType2>,... (Export All Items in System / Specified Item Types)
 *                             -q/query         <XQPE Query String> (Currently Not Supported)
 *                             -i/ini           <Alternate Configuration File>
 *                             -n/num           <Number of Directly Selected Items Per Package (default 500)>
 *                             -m/master        <Master Package Name> (Name of master package used as base name for master directory, tracking file, and summary file.)
 *                             -l/log           <Folder -- Absolute Path to Master Log File Folder> (Location guaranteed to have enough space through the whole process to write tracking & summary files.)
 *                             -v/volumes       <Folder 1>,<Folder 2>,... (Volumes / Storage Abolute Path Locations Availble>
 *                             -r/restart       (Optional.  Force restart of an existing master package)
 *                             -k/kill          (Optional.  Force killing / overwrite of existing process.  Not a restart)
 *                             -diff/difference (Optional.  Force selection of only missing items identified by the Reconciler tool )
 *
 *                     Notes:  * By default, all items are exported.
 *                             * Performance will be better if all item types are listed
 *                               versus using the '*' wilcard.
 *                             * The master file location should be on a different
 *                               disk that will not run out of space or on the last
 *                               disk to be used.
 *                             * If -a/all is true (all items are to be exported,
 *                               -q/query is ignored.
 *                             * Defaults will be used for optional parameters 
 *                               'database', 'user', and 'password' if not specified.  
 *                             * Double-quotes should surround the query string.
 *                             * Default file name of "TImportExportICM.ini" will be
 *                               used for configuration file name if none is specified. 
 *                             * Configuration file is optional.  If TImportExportICM.ini
 *                               is not found, defaults will be used.
 *                             * If an existing tracking file is found, the tool will prompt
 *                               the user whether to continue where it left off if the 
 *                               -r/restart or -k/kill commands are not used.
 *                             * After the import process is complete, the 
 *                               TImportManagerReconcilerICM tool can identify items that
 *                               meet the original item type selection criteria that were
 *                               not successfully imported.  If this reconcile summary
 *                               exists, the tool will prompt the user.  You can force
 *                               the tool to read use the delta by using the
 *                               'diff/difference' option instead of using the
 *                               auto-detection feature.
 *                             * The packageNNN subfolders within the "masterPackage" folder
 *                               at each volume / storage location can be moved to 
 *                               any other "masterPackage" folder at any other storage
 *                               location provided the whole directory successfully copies.
 *                               The tool can detect and handle such changes, including 
 *                               changes to storage locations.
 *
 *                    Example:
 *                             java TExportManagerICM -d icmnlsdb -u icmadmin -p password
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
public class TExportManagerICM{

    // Enumaration Constants
    private static final short  FEATURE_UNSPECIFIED           = -1;
    private static final short  FEATURE_EXPORT                =  1;
    private static final short  FEATURE_RECONCILE             =  2;

    // Default Settings
    private static final String  DEFAULT_DATABASE                        = SConnectDisconnectICM.DEFAULT_DATABASE;
    private static final String  DEFAULT_USERNAME                        = SConnectDisconnectICM.DEFAULT_USERNAME;
    private static final String  DEFAULT_PASSWORD                        = SConnectDisconnectICM.DEFAULT_PASSWORD;
    private static final String  DEFAULT_CONNECT_OPTS                    = "";
    public  static final String  DEFAULT_INI_FILE_NAME                   = "TImportExportICM.ini";
    private static final String  DEFAULT_EXPORT_ALL_CHOICE               = null;           // Require Input
    private static final short   DEFAULT_FEATURE_MODE                    = FEATURE_EXPORT; // By default, will just export.
    public  static final String  DEFAULT_MASTER_PACKAGE_NAME             = "master";
    private static final int     DEFAULT_NUM_ITEMS_PER_PACKAGE           = 500;            // Number of directly selected items allowed per package.
    public  static final int     DEFAULT_RETRY_ATTEMPTS                  = 3;              // by defalut try 3 times.
    public  static final int     DEFAULT_RETRY_DELAY_AT_ATTEMPT_NUM      = 2;              // By default, allow the firt retry immediately.
    public  static final long    DEFAULT_RETRY_DELAY_MS                  = 60000;          // 1 minute delay
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
    private static final String  COMMANDLINE_OPTION_EXPORT_ALL_CHOICE = "-a/all      <*|itemType1>,<itemType2>,... (Export All Items in System / Specified Item Types)";
    private static final String  COMMANDLINE_OPTION_QUERY             = "-q/query    <XQPE Query String>";
    private static final String  COMMANDLINE_OPTION_CONFIG_FILE       = "-i/ini      <Alternate Configuration File>";
    private static final String  COMMANDLINE_OPTION_RETRY_ATTEMPTS    = "-n/num      <Number of Directly Selected Items Per Package (default 500)>";
    private static final String  COMMANDLINE_OPTION_MASTER_NAME       = "-m/master   <Master Package Name> (Name of master package used as base name for master directory, tracking file, and summary file.)";
    private static final String  COMMANDLINE_OPTION_LOG_FILE_LOCATION = "-l/log      <Folder -- Absolute Path to Master Log File Folder> (Location guaranteed to have enough space through the whole process to write tracking & summary files.)";
    private static final String  COMMANDLINE_OPTION_STORAGE_LOCATIONS = "-v/volumes  <Folder 1>,<Folder 2>,... (Volumes / Storage Abolute Path Locations Availble>";
    private static final String  COMMANDLINE_OPTION_RESTART           = "-r/restart  (Force restart of an existing master package)";
    private static final String  COMMANDLINE_OPTION_OVERWRITE         = "-k/kill     (Force killing / overwrite of existing process.  Not a restart)";
    private static final String  COMMANDLINE_OPTION_DIFFERENCE        = "-diff/difference (Optional.  Force selection of only missing items identified by the Reconciler tool )";

    // Configuration Constants
    public  static final String  EXPECTED_OUT_OF_SPACE_MESSAGE          = "There is not enough space on the disk"; // Determined by IBM Java 1.3.1 on Windows.
    public  static final String  MASTER_FOLDER_NAME                     = "masterPackage";
    public  static final String  COMMON_PACKAGE_FOLDER_BASE_NAME        = "package";      // Name used for the central package file of all export packages (batches).
    public  static final String  COMMON_EXPORT_PACKAGE_NAME             = "package.xpk";  // Name used for the central package file of all export packages (batches).
    private static final int     PACKAGE_FOLDER_NAME_CONFLICT_NUM_MAX   = 1000;
    public  static final String  STANDARD_LIST_DELIMITER                = ",";
    public  static final String  ITEMTYPE_LIST_DELIMITER                = STANDARD_LIST_DELIMITER;
    public  static final String  STORAGE_LOCATION_DELIMITER             = STANDARD_LIST_DELIMITER;
    public  static final String  MASTER_SUMMARY_REPORT_FILE_EXT         = "mpk";
    public  static final String  MASTER_TRACKING_FILE_EXT               = "etk";
    private static final String  RECONCILE_SUMMARY_FILE_EXT             = "reconcile.sum";

    // Configuration File Tags
    private static final String  NEWLINE                                    = System.getProperty("line.separator");
    public  static final String  CONFIG_TAG_DELAY_AT_RETRY_NUM              = "Delay At Retry Number";
    private static final String  CONFIG_TAG_EXPORT_ALL_CHOICE               = "Export Item Types";
    public  static final String  CONFIG_TAG_LOG_FILE_DIRECTORY              = "Master Log File Folder";
    public  static final String  CONFIG_TAG_MASTER_PACKAGE_NAME             = "Master Package Name";
    private static final String  CONFIG_TAG_NUM_ITEMS_PER_PACKAGE           = "Num Items Per Package";
    private static final String  CONFIG_TAG_QUERY                           = "Export Query";
    public  static final String  CONFIG_TAG_RETRY_DELAY_MS                  = "Delay Time (ms)";
    public  static final String  CONFIG_TAG_RETRY_ATTEMPTS                  = "Num Retry Attempts Per Issue";
    public  static final String  CONFIG_TAG_STORAGE_LOCATIONS               = "Storage Locations";
    public  static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_CHILDREN                    = "Data Model Supports Children";
    public  static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_LINKS                       = "Data Model Supports Links";
    public  static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS       = "Data Model Supports Folders With Contents";
    public  static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT = "Data Model Supports Resource Items With Contents";
    public  static final String  CONFIG_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT          = "Data Model Supports Document Parts With Contents";

    // Summary File Tags
    public  static final String  MASTER_PACKAGE_VERSION                   = "4.02.04";

    public  static final String  FILE_TAG_MASTER_SUMMARY_IDENTIFIER       = "<Master Export Package>";
    private static final String  FILE_TAG_SECTION_CONFIG_BEGIN            = "CONFIGURATION:";
    private static final String  FILE_TAG_SECTION_CONFIG_HEADER           = "              Setting   Value                                                " + NEWLINE
                                                                          + "----------------------  -----------------------------------------------------";
    private static final String  FILE_TAG_MASTER_PACKAGE_NAME             = "  Master Package Name:  ";
    private static final String  FILE_TAG_DATABASE                        = "       Datastore Name:  ";
    private static final String  FILE_TAG_USERNAME                        = "         CM User Name:  ";
    private static final String  FILE_TAG_CONNOPTS                        = "      Connect Options:  ";
    private static final String  FILE_TAG_ITEM_SELECTION_METHOD           = "Item Selection Method:  ";
    public  static final String  FILE_TAG_EXPORT_ALL_CHOICE               = "  Item Types Selected:  ";
    private static final String  FILE_TAG_QUERY                           = "                Query:  ";
    private static final String  FILE_TAG_RETRY_ATTEMPTS                  = "    # Retries / Issue:  ";
    private static final String  FILE_TAG_RETRY_DELAY_MS                  = "Retry Delay Time (ms):  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_CHILDREN                    = "SUPPORTED    Children:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_LINKS                       = "                Links:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS       = "Folders With Contents:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT = "    Resource Contents:  ";
    private static final String  FILE_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT          = "        Part Contents:  ";
    private static final String  FILE_TAG_CONFIG_FILE_NAME                = "          Config File:  ";
    private static final String  FILE_TAG_NUM_ITEMS_PER_PACKAGE           = "    # Items / Package:  ";
    private static final String  FILE_TAG_MASTER_LOG_FILE_DIRECTORY       = "    Master Log Folder:  ";
    private static final String  FILE_TAG_MASTER_SUMMARY_FILE             = "  Master Package File:  ";
    private static final String  FILE_TAG_MASTER_TRACKING_FILE            = " Master Tracking File:  ";
    private static final String  FILE_TAG_STORAGE_LOCATIONS               = "    Storage Locations:  ";
    private static final String  FILE_TAG_SECTION_CONFIG_END              = "-----------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_STATS_BEGIN             = "STATISTICS:";
    private static final String  FILE_TAG_SECTION_STATS_HEADER            = "                Statistic   Value                                            " + NEWLINE
                                                                          + "--------------------------  -------------------------------------------------";
    private static final String  FILE_TAG_STAT_TOTAL_PACKAGES             = "               # Packages:  ";
    private static final String  FILE_TAG_STAT_TOTAL_ITEMS                = "                  # Items:  ";
    private static final String  FILE_TAG_STAT_NUM_STORAGE_LOCATIONS_USED = " # Storage Locations Used:  ";
    private static final String  FILE_TAG_STAT_STORAGE_LOCATIONS_USED     = "   Storage Locations Used:  ";
    private static final String  FILE_TAG_STAT_NUM_TOOL_RESTARTS          = "          # Tool Restarts:  ";
    private static final String  FILE_TAG_STAT_NUM_MASTER_LEVEL_ERRORS    = "    # Master-Level Errors:  ";
    private static final String  FILE_TAG_STAT_NUM_PACKAGE_LEVEL_ERRORS   = "   # Package-Level Errors:  ";
    private static final String  FILE_TAG_STAT_NUM_WRITE_ERRORS           = "           # Write Errors:  ";
    private static final String  FILE_TAG_STAT_START_TIMESTAMP            = "               Start Time:  ";
    private static final String  FILE_TAG_STAT_END_TIMESTAMP              = "          Completion Time:  ";
    private static final String  FILE_TAG_SECTION_STATS_END               = "-----------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_PACKAGE_LIST_BEGIN      = "PACKAGE LIST:";
    private static final String  FILE_TAG_SECTION_PACKAGE_LIST_HEADER     = "Tag            Package#   # Items           After Item ID                 Last Item ID           Folder                        " + NEWLINE
                                                                          + "-------------  --------  ----------  ----------------------------  ----------------------------  ------------------------------";
    public  static final String  FILE_TAG_PACKAGE_INFO                    = "Package Info:  ";
    private static final String  FILE_TAG_SECTION_PACKAGE_LIST_END        = "-------------------------------------------------------------------------------------------------------------------------------";
    private static final String  FILE_TAG_SECTION_ABANDONED_FOLDERS_BEGIN = "ABANDONED FOLDERS: ";
    private static final String  FILE_TAG_SECTION_ABANDONED_FOLDERS_HEADER= "          Status   Abandoned Folder (Clean Up Manually)                      " + NEWLINE
                                                                          + "-----------------  ----------------------------------------------------------";
    private static final String  FILE_TAG_ABANDONED_DIRECTORY             = "Abandoned Folder:  ";
    private static final String  FILE_TAG_SECTION_ABANDONED_FOLDERS_END   = "-----------------------------------------------------------------------------";
    // Enumerated File Tag Values
    private static final String  FILE_TAG_ITEM_SELECTION_BY_DIFFERENCES   = "<By Reconcile Summary Differences>";
    private static final String  FILE_TAG_ITEM_SELECTION_BY_ITEMTYPE      = "<By Item Type List>";
    private static final String  FILE_TAG_ITEM_SELECTION_BY_QUERY         = "<By Query>";

    // Tracking File Event & Information Tags
    private static final String  TRACKING_TAG_ALL_PACKAGES_STARTED           = "Starting All Packages:  ";
    private static final String  TRACKING_TAG_ALL_PACKAGES_COMPLETED         = "Completed All Packages:  ";
    private static final String  TRACKING_TAG_FAILURE_FOLDER_CLEANUP         = "Failure to clean up folder:  ";
    private static final String  TRACKING_TAG_FAILURE_MASTER_LEVEL           = "Failure at master-level:  ";
    private static final String  TRACKING_TAG_FAILURE_PACKAGE_LEVEL          = "Failure at package-level:  ";
    private static final String  TRACKING_TAG_FAILURE_WRITE_LEVEL            = "Failure on writing package:  ";
    private static final String  TRACKING_TAG_FILE_IDENTIFIER                = "<Master Export Tracking File>";
    private static final String  TRACKING_TAG_MISSING_ITEM_PACKAGE_COMPLETED = "Missing Item Package Completed At:  ";
    private static final String  TRACKING_TAG_PACKAGE_STARTED                = "Package Started:  ";
    private static final String  TRACKING_TAG_PACKAGE_COMPLETED              = "Package Completed:  ";
    private static final String  TRACKING_TAG_SUMMARY_STARTED                = "Started Writing Summary:  ";
    private static final String  TRACKING_TAG_SUMMARY_COMPLETED              = "Completed Writing Summary:  ";
    private static final String  TRACKING_TAG_TOOL_START                     = "Tool Start:  ";
    private static final String  TRACKING_TAG_VOLUME_FULL                    = "STORAGE LOCATION FULL:  ";
    private static final String  TRACKING_TAG_VOLUME_CURRENT                 = "NEW STORAGE LOCATION:  ";

    // Reconcile Summary File Tags
    private static final String  RECONCILE_SUMMARY_FILE_IDENTIFIER        = TImportManagerReconcilerICM.FILE_TAG_MASTER_SUMMARY_IDENTIFIER;
    private static final String  RECONCILE_SUMMARY_TAG_MISSING_ITEM       = TImportManagerReconcilerICM.FILE_TAG_MISSING_ITEM;
    private static final String  RECONCILE_SUMMARY_TAG_ITEMTYPES_SELECTED = TImportManagerReconcilerICM.FILE_TAG_ITEMTYPES_SELECTED;

    // Variables
    // - Connect Variables
    String  _database               = null;  // -d/database <xxxxxxxx>
    String  _userName               = null;  // -u/user <xxxxxxxx>
    String  _password               = null;  // -p/password <xxxxxxxx>
    String  _connOpts               = null;  // -o/options <Connect String Options>
    // - Primary Option Variables
    String  _exportAllChoice        = null;  // -a/all      <*|itemType1>,<itemType2>,... (Export All Items in System / Specified Item Types)
    short   _featureMode            = FEATURE_UNSPECIFIED;           // Feature operation selected, such as export or reconcile.
    String  _iniFileName            = null;  // -i/ini <Alternate Configuration File>
    File    _masterLogFileDirectory = null;  // -l/log <Mast Log File Location> (Location guaranteed to have enough space through the whole process to write tracking & summary files.)
    String  _masterPackageName      = null;  // -m/master <Master Package Name> (Name of master package used as base name for master directory, tracking file, and summary file.)
    Integer _numItemsPerPackage     = null;  // -n/num <Number of Directly Selected Items Per Package>
    String  _query                  = null;  // -q/query <XQPe Query String>
    File[]  _storageLocations       = null;  // -v/volume <Folder 1>,<Folder 2>,... (Volumes / Storage Locations Availble>
    // - Objects
    DKDatastoreICM                   _dsICM                      = null;
    TExportPackageICM.ExportOptions  _exportOptions              = null;
    TExportManagerICM_AttemptManager _masterLevelAttemptManager  = null;  // Managed Operation: Exporting all packages.
    TExportManagerICM_AttemptManager _packageLevelAttemptManager = null;  // Managed Operation: Selection & Export of single package/batch.
    TExportManagerICM_AttemptManager _writeAttemptManager        = null;  // Managed Operation: Writing single package/batch to disk.
    // - Internal Variables
    boolean   _dataModelSupportsChildren                 = true;  // Whether or not the tool can assume there are no children to perform faster.
    boolean   _dataModelSupportsLinks                    = true;  // Whether or not the tool can assume there are no links to improve performance.
    boolean   _dataModelSupportsFoldersWithContents      = true;  // Whether or not the tool can assume no folder contents to improve performance.
    boolean   _dataModelSupportsResourceItemsWithContent = true;  // Whether or not the tool can assume no resource items or no resource items that have content to improve performance.
    boolean   _dataModelSupportsPartsWithContent         = true;  // Whether or not the tool can assume no document parts or no document parts that have content to improve performance.
    ArrayList<String>
              _abandonedDirectoryInfoList    = null;  // Tracks all abandoned directories, failed exports of packages that the tool could not successfully clean up.  Contains java.lang.String objects.
    boolean   _allPackagesCompleted          = false; // Tracks whether or not all packages have been completed.
    TreeMap<Integer,TExportManagerICM_PackageInfo>
              _completedPackageInfoList      = null;  // Tracks the key information for the summary report on all individual packages that were successful.  Contains TExportManagerICM_PackageInfo objects mapped by key obtained from PackageInfo.getKey() or getPackageKey(packageNum).
    int       _currentPackageNum             = -1;    // The current package number
    int       _currentStorageLocationNum     = -1;    // The current storage location in use.  All previous ones probably ran out of space.
    boolean   _exportReconcileDifferences    = false; // Whether or not to select the items identified by the reconcile summary file.
    boolean   _hasRetriedAllStorageLocations = false; // If all storage locations have been tried once and we hit the end, this allows them to be tried again, once.
    String    _lastPackageEndingItemID       = null;  // The item ID of that last item of the last package.
    String    _lastPackageEndingItemPid      = null;  // The last Mising Item PID processed if selecting the differences highlighted in the Reconcile Summary File.
    File      _masterSummaryFile             = null;  // Summary file written at after all items have been exported.
    File      _masterTrackingFile            = null;  // Tracking file used to restart the tool if it crashes or needs to be restarted later.
    File      _reconcileSummaryFile          = null;  // File reference to summary file from the Reconcile tool.
    TreeMap<String,TExportManagerICM_MissingItem>
              _missingItems                  = null;  // Sorted list containing all missing items identified by the Reconcile Summary File.  Contains TExportManagerICM_MissingItem items access by key MissingItem.getKey().
    boolean   _newRunRequested               = false; // Whether or not the user requested to start over, replacing any existing data.
    boolean   _restartRequested              = false; // Whether or not the user requested to restart an existing master package.
    int       _statNumToolRestarts           = -1;    // Number of times the tool has restarted (for statistical purposes)
    long      _statNumTotalItems             = -1;    // Number of actual items exported in total.
    int       _statNumMasterLevelErrors      = -1;    // Number of times that the master-level attempt manager experienced errors (for statistical purposes)
    int       _statNumPackageLevelErrors     = -1;    // Number of times that the package-level attempt manager experienced errors (for statistical purposes)
    int       _statNumWriteErrors            = -1;    // Number of times that the write attempt manager experienced errors (for statistical purposes)
    Timestamp _statStartedTimestamp          = null;  // Time that the tool orginally started (for statistical purposes)
    Timestamp _statCompletedTimestamp        = null;  // Time that the tool actually completed the export (for statistical purposes)
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
            TExportManagerICM exportMgr = new TExportManagerICM(argv);
            
            // Execute the Export Manager Primary Program
            exportMgr.run();

            //-------------------------------------------------------------
            // Sample program completed without exception
            //-------------------------------------------------------------
            System.out.println("");
            System.out.println("==========================================");
            System.out.println("Export Manager Completed.");
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
    // Export Manager Operation
    //=================================================================
    // The following are the methods essential to ExportManager's 
    // operation as an object.
    
   /**
    * Preventing creation with no arguments.
    **/
    private TExportManagerICM() throws Exception{
        throw new Exception("The TExportManagerICM object cannot be created by the no-argument constructor.  Use the constructor that takes the main arguments.");
    }//end CTOR()

   /**
    * Create an instance of the Export Manager.
    * @param commandlineArgs - Command line arguments from main(String[] argsv)
    **/
    public TExportManagerICM(String[] commandlineArgs) throws Exception{
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
        _database                      = DEFAULT_DATABASE;
        _userName                      = DEFAULT_USERNAME;
        _password                      = DEFAULT_PASSWORD;
        _connOpts                      = DEFAULT_CONNECT_OPTS;
        // - Primary Variables
        _exportAllChoice               = DEFAULT_EXPORT_ALL_CHOICE;
        _exportReconcileDifferences    = false;     // Do not export Reconcile differences by default.
        _featureMode                   = DEFAULT_FEATURE_MODE;
        _iniFileName                   = DEFAULT_INI_FILE_NAME;
        _query                         = null;      // No Default
        _masterLogFileDirectory        = null;      // No Default
        _masterPackageName             = DEFAULT_MASTER_PACKAGE_NAME;
        _numItemsPerPackage            = new Integer(DEFAULT_NUM_ITEMS_PER_PACKAGE);
        _storageLocations              = null;      // No Default
        // - Objects
        _dsICM                         = null;
        _exportOptions                 = null;
        _masterLevelAttemptManager     = null;
        _packageLevelAttemptManager    = null;
        _writeAttemptManager           = null;
        // - Internal Variables
        _abandonedDirectoryInfoList    = null;  // Tracks all abandoned directories, failed exports of packages that the tool could not successfully clean up.  Contains java.lang.String objects.
        _allPackagesCompleted          = false; // Tracks whether or not the main exportAllPackages method should be run.
        _completedPackageInfoList      = null;  // Tracks the key information for the summary report on all individual packages that were successful.  Contains TExportManagerICM_PackageInfo objects located by key.  Key obtained by PackageInfo.getKey() or getPackageKey(packageNum).
        _currentPackageNum             = 1;     // Start counting at '1'.
        _currentStorageLocationNum     = 0;     // Start at the first storage location.  Index starts at '0'
        _hasRetriedAllStorageLocations = false;  // If all storage locations have been tried once and we hit the end, this allows them to be tried again, once.
        _lastPackageEndingItemID       = null;  // By default, not continuing after another package.
        _lastPackageEndingItemPid      = null;  // The last Mising Item PID processed if selecting the differences highlighted in the Reconcile Summary File.
        _masterSummaryFile             = null;  // Summary file written at after all items have been exported.
        _masterTrackingFile            = null;  // Tracking file used to restart the tool if it crashes or needs to be restarted later.
        _reconcileSummaryFile          = null;  // File written by TImportManagerReconcilerICM
        _missingItems                  = null;  // Sorted list containing all missing items identified by the Reconcile Summary File.  Contains TExportManagerICM_MissingItem items access by key MissingItem.getKey().
        _newRunRequested               = false; // Not forcing overwrite / starting over by default.
        _restartRequested              = false; // Not forcing a restart by default.
        _statNumToolRestarts           = 0;     // Number of time the tool has restarted (for statistical purposes)
        _statNumTotalItems             = 0;     // Number of actual items exported in total.
        _statNumMasterLevelErrors      = 0;     // Number of times that the master-level attempt manager experienced errors (for statistical purposes)
        _statNumPackageLevelErrors     = 0;     // Number of times that the package-level attempt manager experienced errors (for statistical purposes)
        _statNumWriteErrors            = 0;     // Number of times that the write attempt manager experienced errors (for statistical purposes)
        _statStartedTimestamp          = new Timestamp(System.currentTimeMillis());
        _statCompletedTimestamp        = null;  // Time that the tool actually completed the export (for statistical purposes)
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
        String  iniFileName    = getCommandlineChoice(commandlineArgs,"-i","-ini"     ,false,COMMANDLINE_OPTION_CONFIG_FILE);
        // If specified at command line, save value
        if(iniFileName!=null)
            _iniFileName = iniFileName;
        // Ensure that the ini file specified actually exists.
        validateFileExists(_iniFileName,"If a configuration file is specified, it must exist and appear in the proper directory.");

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
                // Property: Export All Items
                if(property.compareToIgnoreCase(CONFIG_TAG_EXPORT_ALL_CHOICE)==0){
                    if(value.compareTo("")!=0) _exportAllChoice = value;  // Only save non-blank values.
                }
                // Property: Query
                else if(property.compareToIgnoreCase(CONFIG_TAG_QUERY)==0){
                    if(value.compareTo("")!=0){
                        _query = value;  // Only save non-blank values.
                        // This is currently not supported by the tool
                        throw new IllegalArgumentException("Option '"+CONFIG_TAG_QUERY+"' is currently not supported by Export Manager.  Specify selection through option '"+CONFIG_TAG_EXPORT_ALL_CHOICE+"' instead.");
                    }//end if(value.compareTo("")!=0){
                }
                // Property: Num Items Per Package
                else if(property.compareToIgnoreCase(CONFIG_TAG_NUM_ITEMS_PER_PACKAGE)==0){
                    if(value.compareTo("")!=0) setNumItemsPerPackage(value);  // Only save non-blank values.
                }
                // Property: Storage Locations
                else if(property.compareToIgnoreCase(CONFIG_TAG_STORAGE_LOCATIONS)==0){
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
                // Property: Data Model Supports Children
                else if(property.compareToIgnoreCase(CONFIG_TAG_DATA_MODEL_SUPPORTS_CHILDREN)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsChildren = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsChildren = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+CONFIG_TAG_DATA_MODEL_SUPPORTS_CHILDREN+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Data Model Supports Links
                else if(property.compareToIgnoreCase(CONFIG_TAG_DATA_MODEL_SUPPORTS_LINKS)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsLinks = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsLinks = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+CONFIG_TAG_DATA_MODEL_SUPPORTS_LINKS+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Data Model Supports Folders with Contents
                else if(property.compareToIgnoreCase(CONFIG_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsFoldersWithContents = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsFoldersWithContents = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+CONFIG_TAG_DATA_MODEL_SUPPORTS_FOLDERS_WITH_CONTENTS+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Data Model Supports Resource Items with Content
                else if(property.compareToIgnoreCase(CONFIG_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsResourceItemsWithContent = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsResourceItemsWithContent = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+CONFIG_TAG_DATA_MODEL_SUPPORTS_RESOURCE_ITEMS_WITH_CONTENT+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
                }
                // Property: Data Model Supports Parts With Content
                else if(property.compareToIgnoreCase(CONFIG_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT)==0){
                    if( (value.compareToIgnoreCase("yes")==0) || (value.compareToIgnoreCase("true")==0))
                        _dataModelSupportsPartsWithContent = true;
                    else if( (value.compareToIgnoreCase("no")==0) || (value.compareToIgnoreCase("false")==0))
                        _dataModelSupportsPartsWithContent = false;
                    else
                        throw new IllegalArgumentException("Invalid configuration setting '"+CONFIG_TAG_DATA_MODEL_SUPPORTS_PARTS_WITH_CONTENT+"' value '"+value+"' in configuration file '"+_iniFileName+"'.  Expected value 'yes'/'true' or 'no'/false'");
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
        String  database           = getCommandlineChoice(argv,"-d","-database",false,COMMANDLINE_OPTION_DATABASE);
        // -u/user <xxxxxxxx>
        String  userName           = getCommandlineChoice(argv,"-u","-user"    ,false,COMMANDLINE_OPTION_USERNAME);
        // -p/password <xxxxxxxx>
        String  password           = getCommandlineChoice(argv,"-p","-password",false,COMMANDLINE_OPTION_PASSWORD);
        // -o/options <Connect String Options>
        String  connOpts           = getCommandlineChoice(argv,"-o","-options" ,false,COMMANDLINE_OPTION_CONNECT_OPTIONS);
        // -q/query <XQPe Query String>
        String  query              = getCommandlineChoice(argv,"-q","-query"   ,false,COMMANDLINE_OPTION_QUERY);
        // -i/ini <Alternate Configuration File>
        String  iniFileName        = getCommandlineChoice(argv,"-i","-ini"     ,false,COMMANDLINE_OPTION_CONFIG_FILE);
        // -a/all <*|itemType1>,<itemType2>,... (Export All Items in System / Specified Item Types)
        String  exportAllChoice    = getCommandlineChoice(argv,"-a","-all"     ,false,COMMANDLINE_OPTION_EXPORT_ALL_CHOICE);
        // -n/num <Number of Directly Selected Items Per Package>
        String  numItemsPerPackage = getCommandlineChoice(argv,"-n","-num"     ,false,COMMANDLINE_OPTION_RETRY_ATTEMPTS);
        // -l/log <Folder -- Mast Log File Folder> (Location guaranteed to have enough space through the whole process to write tracking & summary files.)
        String  masterLogFileDirStr= getCommandlineChoice(argv,"-l","-log"     ,false,COMMANDLINE_OPTION_LOG_FILE_LOCATION);
        // -m/master <Master Package Name> (Name of master package used as base name for master directory, tracking file, and summary file.)
        String  masterPackageName  = getCommandlineChoice(argv,"-m","-master"  ,false,COMMANDLINE_OPTION_MASTER_NAME);
        // -v/volume <Folder 1>,<Folder 2>,... (Volumes / Storage Locations Availble>
        String  storageLocations   = getCommandlineChoice(argv,"-v","-volumes" ,false,COMMANDLINE_OPTION_STORAGE_LOCATIONS);
        // -r/restart  (Force restart of an existing master package)
        String  restartRequested   = getCommandlineChoice(argv,"-r","-restart" ,false,COMMANDLINE_OPTION_RESTART);
        // -k/kill     (Force killing / overwrite of existing process.  Not a restart)
        String  newRunRequested    = getCommandlineChoice(argv,"-k","-kill"    ,false,COMMANDLINE_OPTION_OVERWRITE);
        // -diff/difference
        String  selectByDiffReqested = getCommandlineChoice(argv,"-diff","-difference",false,COMMANDLINE_OPTION_DIFFERENCE);
        
        // Save any non-null settings
        if(database            !=null) _database                   = database;
        if(userName            !=null) _userName                   = userName;
        if(password            !=null) _password                   = password;
        if(connOpts            !=null) _connOpts                   = connOpts;
        if(iniFileName         !=null) _iniFileName                = iniFileName;
        if(exportAllChoice     !=null) _exportAllChoice            = exportAllChoice;
        if(numItemsPerPackage  !=null) setNumItemsPerPackage(numItemsPerPackage);
        if(masterPackageName   !=null) _masterPackageName          = masterPackageName;
        if(masterLogFileDirStr !=null) _masterLogFileDirectory     = new File(masterLogFileDirStr);
        if(storageLocations    !=null) setStorageLocations(storageLocations);
        if(restartRequested    !=null) _restartRequested           = true;
        if(newRunRequested     !=null) _newRunRequested            = true;
        if(selectByDiffReqested!=null) _exportReconcileDifferences = true;

        // Invalid Options
        if(query              !=null){
            _query              = query;
            // Not currently supported
            throw new IllegalArgumentException("Option '"+COMMANDLINE_OPTION_QUERY+"' is currently not supported by Export Manager.  Specify selection through option '"+COMMANDLINE_OPTION_QUERY+"' instead.");
        }//end if(query              !=null){
        
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
        _abandonedDirectoryInfoList = new ArrayList<String>();  // ArrayList of java.lang.String objects.
        _completedPackageInfoList   = new TreeMap<Integer,TExportManagerICM_PackageInfo>();    // Sorted tree of TExportManagerICM_PackageInfo objects located by key.  Key obtained by PackageInfo.getKey() or getPackageKey(packageNum)
        _dsICM                      = new DKDatastoreICM(); // Will be created later, but create instance now so we can validate database alias used.
        _exportOptions              = new TExportPackageICM.ExportOptions(_iniFileName);
        _masterLevelAttemptManager  = new TExportManagerICM_AttemptManager("Master Package Attempt Manager",commandlineArgs);
        _masterSummaryFile          = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_SUMMARY_REPORT_FILE_EXT);
        _masterTrackingFile         = new File(_masterLogFileDirectory,_masterPackageName+'.'+MASTER_TRACKING_FILE_EXT);
        _reconcileSummaryFile       = new File(_masterLogFileDirectory,_masterPackageName+'.'+RECONCILE_SUMMARY_FILE_EXT);
        _missingItems               = new TreeMap<String,TExportManagerICM_MissingItem>();  // Sorted list containing all missing items identified by the Reconcile Summary File.  Contains TExportManagerICM_MissingItem items access by key MissingItem.getKey().
        _packageLevelAttemptManager = new TExportManagerICM_AttemptManager(_masterLevelAttemptManager,"Single Package Attempt Manager",commandlineArgs);
        _writeAttemptManager        = new TExportManagerICM_AttemptManager(_packageLevelAttemptManager,"Write Attempt Manager",commandlineArgs);
    }//end initObjects()

   /**
    * Clear all the files in the specified folder.  Since no subfolders
    * are expected, an error is thrown if a folder is found.
    * @param folder - folder to clear the contents of
    * @param throwError - If 'true', will throw error if it cannot be cleaned.
    *                     If 'false', will return 'true' or 'false' indiciating
    *                     whether cleaning was successful.
    * @param Returns 'true' if the folder was successfully cleaned or 'false' if
    *        it could not be completely cleaned.
    **/
    public boolean clearFolder(File folder,boolean throwError) throws InternalError, Exception{
        // Validate Input
        if(folder==null)                throw new InternalError("Internal Tool Error:  TExportManagerICM.clearFolder(File folder) received an invalid argument.  'folder' was 'null'.");
        if(folder.exists()==false)      throw new InternalError("Internal Tool Error:  TExportManagerICM.clearFolder(File folder) received an invalid argument.  'folder' by the name of '"+folder.getAbsolutePath()+"' does not exist.  Only an existing folder can be cleared.");
        if(folder.isDirectory()==false) throw new InternalError("Internal Tool Error:  TExportManagerICM.clearFolder(File folder) received an invalid argument.  'folder' by the name of '"+folder.getAbsolutePath()+"' is not a folder/directory.  Only an existing folder can be cleared.");

        boolean success = true; // Assume success. If validation fails, will set to false.

        // List all contentents
        File[] files = folder.listFiles();
        for(int i=0; i<files.length; i++){ // Go through all contents in the folder one at a time.
            File file = files[i]; // Get the next file
            // If it is a folder, throw an error since we only delete files
            if(file.isDirectory()) throw new InternalError("Internal Tool Error:  TExportManagerICM.clearFolder(File folder) did not expect a folder that had any subfolders.  The 'folder' by the name of '"+folder.getAbsolutePath()+"' was expected to only contain files and no subdirectories.  However directory '"+file.getAbsolutePath()+"' was found.");
            // Otherwise delete the file
            try{
                boolean deleted = file.delete();
                // Throw error if not successfully deleted.
                if(deleted==false)
                    throw new Exception("File.delete() indicated failure by returning false.  No exception thrown.");
            }
            catch(Exception exc){
                String errorMessage = "While attempting to clean up folder '"+folder.getAbsolutePath()+"', an error occurred deleting file '"+file.getAbsolutePath()+"'.  The tool could not delete this file.  Review the following error and delete all contents in this folder manually: "+exc.getMessage();
                printError("WARNING: "+errorMessage);
                trackFailedCleanup(folder);  // Keep track of this failure so it can be cleaned up later
                if(throwError) throw new Exception(errorMessage);
                success = false;
            }
            if(file.exists()){ // Double check
                String errorMessage = "WARNING: Internal Tool Error:  TExportManagerICM.clearFolder(File folder) successfully deleted file '"+file.getAbsolutePath()+"', but the subsequent exists() check failed.";
                printError("WARNING: "+errorMessage);
                trackFailedCleanup(folder);  // Keep track of this failure so it can be cleaned up later
                if(throwError) throw new InternalError(errorMessage);
                success = false;
            }//end if(file.exists()){ // Double check
        }//end for(int i=0; i<files.length; i++){ // Go through all contents in the folder one at a time.
        return(success);
    }//end clearFolder()

   /**
    * Find all items for the current package.
    *
    * Execute Query Will:
    *    - Sort based on Item ID (ascending)
    *    - Find items greater than last Item ID of previous package.
    *    - Find only the maximum number of items allowed in a package.
    *
    * @return Returns an open cursor containing all items to appear
    *         in that package.
    **/
    private dkResultSetCursor executePackageQuery() throws InternalError, DKException, Exception{
        // First, make sure we have a connected datastore by this point.
        if(_dsICM==null)                throw new InternalError("Inernal Error: An established connection was expected by TExportManagerICM.executePackageQuery().  '_dsICM' was found to be 'null'.");
        if(_dsICM.isConnected()==false) throw new InternalError("Inernal Error: An established connection was expected by TExportManagerICM.executePackageQuery().  '_dsICM' was found never to have been connected.");
        
        // Build Query String
        String query = getCurrentPackageQuery();
        
        // Execute Query
        System.out.println("--- Executing Query for Package '"+_currentPackageNum+"'...");
        printDebug("    --> <executing...>");
    	DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance(_dsICM);
        DKNVPair options[]  = new DKNVPair[3];
                 options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, _numItemsPerPackage.toString());  // Get only the number that can fit in the current batch.
                 options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY);         // Only retrieving IDs for fast results.
                 options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                            // Must mark the end of the NVPair
        dkResultSetCursor cursor = _dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
        printDebug("    --> Done.");

        // Return Results
        return(cursor);
    }//end executePackageQuery()

   /**
    * Perform export operation.
    **/
    public void export() throws DKException, Exception{
        
        System.out.println("-----------------");
        System.out.println("-- Export Mode --");
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
                connect(_dsICM,_database,_userName,_password,_connOpts);

                // Export All Apackages
                exportAllPackages();

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
    }//end export()

   /**
    * Export all packages.  Detect if one was already started,
    * and if so, continue where it left off.
    **/
    private void exportAllPackages() throws DKException, Exception{

        System.out.println("----------------------------");
        System.out.println("-- Exporting All Packages --");
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
                throw new Exception("Could not clear existing master summary file by the name of '"+_masterSummaryFile.getAbsolutePath()+"'.  When a new export process beings, any existing such file becomes invalid.  Delete the file manually because the tool could not delete it for the following reason: "+exc.getMessage());
            }
        }//end if(_masterSummaryFile.exists()){

        // Export all if they have not yet been completed
        if(_allPackagesCompleted==false){
            
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
                        // Export One Package
                        morePackages = exportNextPackage();
                        
                        // Tell the attempt manager that everything is complete.  The loop should not restart.
                        _packageLevelAttemptManager.setComplete();

                    }catch(Exception exc){
                        // Report Error to Log
                        track(TRACKING_TAG_FAILURE_PACKAGE_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                        _statNumPackageLevelErrors++; // Track for statistics
                        // The attempt manager will handle error reporting & throwing if needed.
                        _packageLevelAttemptManager.handleAttemptFailure(exc);
                    }
                
                }//end while(_packageLevelAttemptManager.next()){

                // Attempt managers restart after surpass single error.
                _masterLevelAttemptManager.reset();
                            
            }//end while(morePackages){

            // Report Completed Packages
            trackTime(TRACKING_TAG_ALL_PACKAGES_COMPLETED);
        }//end if(_allPackagesCompleted==false){
        // Record current completed timestamp
        _statCompletedTimestamp = new Timestamp(System.currentTimeMillis());

        System.out.println("--- All Packages Completed Export (Total '"+(_currentPackageNum-1)+"').");
    }//end exportAllPackages()
    
   /**
    * Export the next package.  The current package will be written,
    * and if successful, the pointer will move onto the next package.
    * @return Returns true if there are likely more packages after this one.
    *         If the package is full, there is a good chance of subsequent
    *         packages.  Returns 'false' if there is no next package or
    *         if the current package is empty.
    **/
    private boolean exportNextPackage() throws DKException, Exception{
        boolean morePackages = true; // Assume there are more unless we find otherwise.

        System.out.println("----------------------------");
        System.out.println("   Exporting Package '"+_currentPackageNum+"'");
        System.out.println("----------------------------");

        // Track Starting New Package
        track(TRACKING_TAG_PACKAGE_STARTED,(new Integer(_currentPackageNum)).toString());

        System.out.println("--- Loading / Selecting Package Items...");

        // If exporting differences highlighted by a Reconcile Summary, select those items
        dkCollection retrievedResults = null;
        if(_exportReconcileDifferences){
            // Get the next batch of missing items according to number of items allowed in a package.
            retrievedResults = getNextBatchOfMissingItems();
            
            // Assume Absolute Responsibility OVer Retrieve
            retrievePackageItems(retrievedResults);
        }else{ // Otherwise use query to find selected items
            // Execute Query
            dkResultSetCursor cursor = executePackageQuery();

            // Assume Absolute Responsibility Over Retrieve
            retrievedResults = retrievePackageItems(cursor);
        }//end }else{ // Otherwise use query to find selected items

        // Prepare everything needed to create the Package Info object later
        TExportManagerICM_PackageInfo packageInfo = null;
        // - Get last DDO of result set for creating the PackageInfo class
        dkIterator iter = retrievedResults.createIterator();
        DKDDO lastDDO = null;
        while(iter.more()){
            lastDDO = (DKDDO) iter.next();
        }//end while(iter.more()){
        // - Get Item Ids needed for package info.
        DKPidICM lastPid = (DKPidICM) lastDDO.getPidObject();
        String afterItemId = _lastPackageEndingItemID;  // This package start after the last package's last item ID. (not inclusive)
        String lastItemId  = lastPid.getItemId();       // This package ends on this package's last Item ID. (inclusive)
        String lastItemPid = lastPid.pidString();       // This package ends on this PID String. (inclusive)
                
        // Only continue if there were items found.
        if(retrievedResults.cardinality() > 0){
            File packageFolder = null;

            // Create Package
            // -> Create new package in memory.
            printDebug("--- Creating New Export Package Object Created in Memory.");
            TExportPackageICM exportPackage = new TExportPackageICM(retrievedResults.cardinality(),_exportOptions);
            printDebug("    --> New Object Created in Memory.");
            // -> Add items from cursor one at a time
            printDebug("--- Adding package '"+_currentPackageNum+"' set of '"+retrievedResults.cardinality()+"' to Export Package object.");
            exportPackage.addItems(retrievedResults, _exportOptions);
            printDebug("--- Completed Adding package '"+_currentPackageNum+"' set of '"+retrievedResults.cardinality()+"' to Export Package object.");
            
            // Write package
            System.out.println("--- Writing Package '"+_currentPackageNum+"'...");
            // Reset the attempt manager for reattempt writing.
            _writeAttemptManager.reset();
            // Repeat attempts until no more retry attempts left.
            File currentPackageFile = null;
            while(_writeAttemptManager.next()){
                try{
                    // -> Create folder
                    printDebug("        - Creating Package Folder...");
                    packageFolder = getNewPackageFolder(); // Get a valid, empty, package folder at an available storage location.
                    // -> Determine export package file name.  Reuse same name for each batch.
                    currentPackageFile = new File(packageFolder,COMMON_EXPORT_PACKAGE_NAME);
                    printDebug("        - Export Package File Chosen: "+currentPackageFile.getAbsolutePath());
                    // -> Write to disk
                    // Write to Disk
                    exportPackage.write(currentPackageFile.getAbsolutePath(),_exportOptions);
                    // -> Create the Package Info object now that everything is written to this packageFolder.
                    packageInfo = new TExportManagerICM_PackageInfo(_currentPackageNum,retrievedResults.cardinality(),afterItemId,lastItemId,packageFolder,_exportOptions);
                    // Tell the attempt manager that everything is complete.  The loop should not restart.
                    _writeAttemptManager.setComplete();
                }catch(IOException exc){ // Detect out of disk space errors
                    // Report Error to Log
                    track(TRACKING_TAG_FAILURE_WRITE_LEVEL,exc.getClass().getName() + ": " + exc.getMessage());
                    _statNumWriteErrors++; // Track for statistics

                    boolean needNewFolder = false; // Tracks whether or not a new folder is needed due to new volume or error cleaning up.
                    // Clean up
                    boolean cleaned = clearFolder(packageFolder,false);
                    // -> If couldn't clean up, report to tracking file
                    // <Already handled by clearFolder()
                    // -> If couldn't clean, a new folder will be needed.
                    if(cleaned==false){
                        // - Need a new folder to continue
                        needNewFolder = true;
                    }//end if(cleaned==false){
                    // If out of disk space, move onto next volume & retry.
                    // - This condition is detected by a known message for this case
                    if(exc.getMessage().indexOf(EXPECTED_OUT_OF_SPACE_MESSAGE)>=0){
                        // - Track Volume Full
                        track(TRACKING_TAG_VOLUME_FULL,_storageLocations[_currentStorageLocationNum].getAbsolutePath());
                        selectNextStorageLocation();
                        // - Reset the write attempt manager since it is a new location
                        _writeAttemptManager.reset();
                        // - Need a new folder at the next location
                        needNewFolder = true;
                    }else{ // If other write error, retry a few times and then move onto next volume.
                        // If this was the last attempt, try the next storage location
                        if(_writeAttemptManager.isLastAttempt()){
                            selectNextStorageLocation();
                            // - Reset the write attempt manager since it is a new location
                            _writeAttemptManager.reset();
                            // - Need a new folder at the next location
                            needNewFolder = true;
                        }//end if(_writeAttemptManager.isLastAttempt()){
                        else{ // Otherwise just retry
                            // No additional operations needed.  Attempt Manager will take care of it.
                        }
                    }
                    // If need a new folder, get a new folder
                    if(needNewFolder){
                        // - Use different folder.
                        packageFolder = getNewPackageFolder(); // Get a valid, empty, package folder at an available storage location.
                        // - Don't forget about the central package file name in now in a different folder.
                        currentPackageFile = new File(packageFolder,COMMON_EXPORT_PACKAGE_NAME);
                    }//end if(cleaned==false){
                    // The attempt manager will handle error reporting & throwing if needed.
                    _writeAttemptManager.handleAttemptFailure(exc);
                }
            }//end while(_writeAttemptManager.next()){

            // Record Success
            printDebug("        - Recording success...");
            // - Save Completed Package Info
            _completedPackageInfoList.put(packageInfo.getKey(),packageInfo);
            _statNumTotalItems += retrievedResults.cardinality();
            // Report to Tracking Log
            track(TRACKING_TAG_PACKAGE_COMPLETED, packageInfo.toString());
            // Write additional information if exporting missing items based on Reconcile Summary or using normal means
            if(_exportReconcileDifferences==true)
                track(TRACKING_TAG_MISSING_ITEM_PACKAGE_COMPLETED, lastItemPid);
            // Allow next package to move on
            _lastPackageEndingItemID  = lastItemId;  // Save the last item ID.
            _lastPackageEndingItemPid = lastItemPid; // Save the last Pid String
            _currentPackageNum++;
        }//end if(retrievedResults.cardinality() > 0){
        else{
            printDebug("--- No Items Found for Next Package.");   
        }
        
        // Determine if there is likely a next package.
        if(retrievedResults.cardinality() < _numItemsPerPackage.intValue()){ // if there are less than the max, then this isn't full and therefore there is not next package.
            morePackages = false;
            System.out.println("--- Completed Package '"+(_currentPackageNum-1)+"'.  No Remaining Packages to Export.");
        }else{
            morePackages = true;  // We will assume true otherwise.
            System.out.println("--- Completed Package '"+(_currentPackageNum-1)+"'.  More Packages Remain to Export.");
        }
        return(morePackages);
    }//end exportNextPackage

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
    * @return Returns the correct and completed query string.
    **/
    private String getCurrentPackageQuery() throws InternalError, Exception{
        System.out.println("--- Building Package '"+_currentPackageNum+"' Query...");
        StringBuffer query = new StringBuffer();
        // If user specified all item of a set of item types
        if(_exportAllChoice!=null){
            // Report other query string overridden if set
            if(_query!=null) printDebug("    --> Export All Items from Item Type List Overrides Query Specified.");
            if(_lastPackageEndingItemID!=null) printDebug("    --> Continuing after item '"+_lastPackageEndingItemID+"' from package '"+(_currentPackageNum - 1)+"'.");
            
            ArrayList<String> itemTypeList = getValidatedItemTypeList();
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
                if(_lastPackageEndingItemID!=null){
                    query.append("[@ITEMID>\"");            // Add [@ITEMID>"
                    query.append(_lastPackageEndingItemID); // add the item ID.
                    query.append("\"]");                    // Add "]
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
        }else{// else use query string as specified
            query.append(_query);
        }        
        printDebug("         Building: "+query.toString());
        // Sort by Item ID
        query.append(" SORTBY(@ITEMID)");
        printDebug("         Building: "+query.toString());
        // Return Generated Query
        System.out.println("    --> QUERY:  "+query.toString());
        return(query.toString());
    }//end getQuery()   

   /**
    * Get a valid, empty, package folder at an available storage location.
    *
    * A subfolder at the current storage location will be created if necessary.
    * If one exists, it must be empty.  If it is not empty, an error will be
    * thrown.
    *
    * Package Folder Name & Location:
    *     <Storage Location>\<Master Folder>\<Base Package Folder Name><Current Package Num>
    *
    * @return Returns a File object for the newly created folder.
    **/
    private File getNewPackageFolder() throws Exception, InternalError{

        printDebug("--- Getting New Package Folder...");

        // Get current storage location
        File currentStorageLocation = _storageLocations[_currentStorageLocationNum];
        printDebug("    -> Storage Location: "+currentStorageLocation.getAbsolutePath());

        // Get master folder
        File masterFolder = new File(currentStorageLocation,MASTER_FOLDER_NAME);
        printDebug("          Master Folder: "+currentStorageLocation.getAbsolutePath());
        
        // Create master folder if it doesn't exist
        if(masterFolder.exists()==false){
            masterFolder.mkdir();
            printDebug("            - Created.");
        }else{
            printDebug("            - Already Exists.");
        }
        
        // Loop until a good folder name is found.
        File packageFolder = null;
        for(int i=1; (packageFolder==null) && (i<PACKAGE_FOLDER_NAME_CONFLICT_NUM_MAX); i++){ // Continue searching unless we find a folder or if we should give up.

            // Create name
            StringBuffer currentPackageFolderName = new StringBuffer();
            currentPackageFolderName.append(COMMON_PACKAGE_FOLDER_BASE_NAME);
            currentPackageFolderName.append(_currentPackageNum);
            // If the first choice was not available, try a different names
            if(i>1){
                currentPackageFolderName.append('r');
                currentPackageFolderName.append(i);
            }//end if(i>1){

            // Create Folder
            packageFolder  = new File(masterFolder,currentPackageFolderName.toString());
                printDebug("         Package Folder: "+packageFolder.getAbsolutePath());
            
            // If it exists, validate it and empty it
            if(packageFolder.exists()){
                printDebug("            - Already Exists.");
                if(packageFolder.isDirectory()==false)
                    throw new Exception("Error creating Export Package sub-folder '"+currentPackageFolderName+"' in master folder '"+masterFolder.getName()+"' at Volume / Storage Location '"+currentStorageLocation.getAbsolutePath()+"'.  A file with the path '"+packageFolder.getAbsolutePath()+"' already exists.  This file needs to be deleted.  Since this could be a loss of data on your system, it must be deleted manually.  No folder was expected to exist with this name.  In this case a *file* exists with this name.");
                // Empty the folder
                printDebug("            - Clearing / Deleting Contents of Existing Folder.");
                boolean cleaned = clearFolder(packageFolder,false);
                // If it could not be emptied, drop this one and try the next.
                if(cleaned==false){
                    printDebug("            - Clearing Failed.");
                    packageFolder = null;
                }//end if(cleaned){
            }else{ // Otherwise create it.
                packageFolder.mkdir();        
                printDebug("            - Created.");
            }       
        }//end for(int i=1; i<PACKAGE_FOLDER_NAME_CONFLICT_NUM_MAX; i++){

        // Double check that it exists now.
        if(packageFolder.exists()==false)
            throw new InternalError("Internal Error:  Creation of new package folder '"+packageFolder.getAbsolutePath()+"' completed, but a subsequent check for its existance failed.");

        return(packageFolder);
    }//end getNewPackageFolder()

   /**
    * Get the next batch of Missing Items based on the last package's ending
    * Pid String.  Get the next number of items starting with the next item
    * after the item by the last Pid processed.  Get the maximum number of 
    * items allowed for this package.
    * @return Returns the next set of items to export.
    **/
    private dkCollection getNextBatchOfMissingItems() throws Exception{
        printDebug("    --> Getting Next Batch of Missing Items...");

        // Create new collection
        dkCollection coll = new DKSequentialCollection();
        
        // Create & Add DDOs to the collection for the next batch after
        // the last processed Pid.
        // - Get all remaining items including and after the last processed Pid
        Iterator<TExportManagerICM_MissingItem> iter = null;
        if(_lastPackageEndingItemPid!=null){ // If we are continuing after another package,
            SortedMap<String,TExportManagerICM_MissingItem> remainingPlusLast = _missingItems.tailMap(TExportManagerICM_MissingItem.getKey(_lastPackageEndingItemPid));
            printDebug("        - There are '"+(remainingPlusLast.size()-1)+"' items left in the Missing Item List.");
            iter = remainingPlusLast.values().iterator();
            // - Throw out First one, since it was the last one that we already processed.
            if(iter.hasNext())
                iter.next();
        }else{ // Otherwise we are using the whole list
            iter = _missingItems.values().iterator();
            printDebug("        - There are '"+(_missingItems.size())+"' items left in the Missing Item List.");
        }
        // - Until there are no more left or we reach the package limit, add all after
        //   the first one.
        for(int i=0; (i<_numItemsPerPackage.intValue()) && (iter.hasNext()); i++){
            TExportManagerICM_MissingItem missingItem = iter.next();
            DKDDO ddo = missingItem.getDDO(_dsICM);
            coll.addElement(ddo);
        }//end for(int i=0; (i<_numItemsPerPackage) && (iter.hasNext(); i++){
        printDebug("    --> Found Next Batch of '"+coll.cardinality()+"' Missing Items.");
        return(coll);
    }//end getNextBatchOfMissingItems()
    
   /**
    * Parse, validate, and separate the item types listed for all 
    * items to be exported from and place them in an ArrayList.
    * Errors will be thrown for any that the user does not have access 
    * to or do not exist.
    * @return Returns a java.util.ArrayList of java.lang.String objects
    *         for each validated name.
    **/
    private ArrayList<String> getValidatedItemTypeList() throws InternalError, Exception{
        // First, make sure we have a connected datastore by this point.
        if(_dsICM==null)                throw new InternalError("Inernal Error: An established connection was expected by TExportManagerICM.getValidatedItemTypeList().  '_dsICM' was found to be 'null'.");
        if(_dsICM.isConnected()==false) throw new InternalError("Inernal Error: An established connection was expected by TExportManagerICM.getValidatedItemTypeList().  '_dsICM' was found never to have been connected.");
        // Get Datastore Definition
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) _dsICM.datastoreDef();

        System.out.println("    --> Validating Item Type List: "+_exportAllChoice);

        // Prepare New List
        ArrayList<String> itemTypeList = new ArrayList<String>();
        
        // Parse Item Type List.
        StringTokenizer itemTypeTokens = new StringTokenizer(_exportAllChoice,ITEMTYPE_LIST_DELIMITER);
        while(itemTypeTokens.hasMoreTokens()){
            String itemTypeName = itemTypeTokens.nextToken().trim();
            printDebug("        - "+itemTypeName);
            // If not wildcard, validate.  Otherwise leave wildcard as is.
            if(itemTypeName.compareTo("*")!=0){
                // Validate
                dkEntityDef entityDef = dsDefICM.retrieveEntity(itemTypeName);
                if(entityDef==null) _masterLevelAttemptManager.terminateAndThrow("Alleged item type name '"+itemTypeName+"' either does not exist as any form of an entity type in the '"+_database+"' datastore or user '"+_userName+"' does not have the necessary access to the item type.  The alleged item type name '"+itemTypeName+"' was listed in the list of item types for which all items were to be exported, '"+_exportAllChoice+"'.  Modify the configuration file '"+_iniFileName+"' or command line arguments to specify valid item types that this user has access to.");
                // Make sure it is a root
                DKComponentTypeViewDefICM compTypeViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(itemTypeName);
                if(compTypeViewDef.isRoot()==false)
                    _masterLevelAttemptManager.terminateAndThrow("Alleged item type name '"+itemTypeName+"' is not a root component type and therefore is not the item type name in the '"+_database+"' datastore.  The name provided, '"+itemTypeName+"', is a child component type.  Import / Export can only be performed at the item level through the root components.  Individual children cannot be imported.  Review the alleged item type name '"+itemTypeName+"' listed in the list of item types for which all items were to be exported, '"+_exportAllChoice+"'.  Use only base item type names.");
                // Detect if it is a view, not the base item type.
                if(compTypeViewDef.getName().compareToIgnoreCase(compTypeViewDef.getComponentTypeName())!=0)
                    _masterLevelAttemptManager.terminateAndThrow("Data Loss Warning: Alleged item type name '"+itemTypeName+"' is not the base item type name in the '"+_database+"' datastore.  Instead, '"+itemTypeName+"' is a view name of item type '"+compTypeViewDef.getComponentTypeName()+"'.  Non-base views typically act like a filter for the base item type.  Such a filter will likely cause loss of data in the export operation because only the viewable information will be exported.  Review the alleged item type name '"+itemTypeName+"' listed in the list of item types for which all items were to be exported, '"+_exportAllChoice+"'.  Use only base item type names.");    
                // Make sure it that the base view is the active view
                DKComponentTypeViewDefICM activeCompTypeViewDef = dsDefICM.getActiveComponentTypeView(itemTypeName);
                if(activeCompTypeViewDef.getName().compareToIgnoreCase(itemTypeName)!=0)
                    _masterLevelAttemptManager.terminateAndThrow("Data Loss Warning: Base item type '"+itemTypeName+"' is not the view that is currently active in the '"+_database+"' datastore for user '"+_userName+"'.  Instead, '"+activeCompTypeViewDef.getName()+"' is the active view for item type '"+itemTypeName+"'.  Non-base views typically act like a filter for the base item type.  Such a filter will likely cause loss of data in the export operation because only the viewable information will be exported.  Modify the user and/or system settings so that the base item type view '"+itemTypeName+"' is the active view in the system.  Otherwise review the validity of item type name '"+itemTypeName+"' listed in the list of item types for which all items were to be exported, '"+_exportAllChoice+"'.  Use only active base item type names.");    
                // Check an alternate was of ensuring that the base view is the active view.
                try{DKItemTypeDefICM itemTypeDef = (DKItemTypeDefICM) entityDef;}
                catch(ClassCastException exc){
                    _masterLevelAttemptManager.terminateAndThrow("Data Loss Warning: Alleged item type name '"+itemTypeName+"' is not the base item type view name in the '"+_database+"' datastore.  Non-base views typically act like a filter for the base item type.  Such a filter will likely cause loss of data in the export operation because only the viewable information will be exported.  Review the alleged item type name '"+itemTypeName+"' listed in the list of item types for which all items were to be exported, '"+_exportAllChoice+"'.  Use only base item type names.");    
                }
            }//end if(itemTypeName.compareTo("*")!=0){ // If not wildcard
            // Add to list
            itemTypeList.add(itemTypeName);
        }//end while(itemTypeTokens.hasMoreTokens()){

        // Return Validated List
        return(itemTypeList);
    }//end getValidatedItemTypeList()

   /**
    * Determine if the specified DDO is for a resource-classified item type.
    * @param ddo - DDO to check if it is a resource item.
    * @return Returns 'true' if this ddo is from a resource-classified item type,
    *         'false' otherwise.
    **/
    private boolean isResourceItem(DKDDO ddo) throws Exception{
        boolean isResource = false;

        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) _dsICM.datastoreDef();
        
        String objectType = ddo.getPidObject().getObjectType();
        
        DKItemTypeViewDefICM itemTypeDef = (DKItemTypeViewDefICM) dsDefICM.retrieveComponentTypeView(objectType);

        if(itemTypeDef==null) // If the ItemTypeView doesn't exist, report error.  This happens after an XML Item import.
            throw new InternalError("Internal Error:  Could not look up the component type (view) definition for the objct type, '"+objectType+"', found in the search results.  Make sure that the current user has sufficient access.");

        if (itemTypeDef.getClassification() == DKConstantICM.DK_ICM_ITEMTYPE_CLASS_RESOURCE_ITEM)
            isResource = true;
        else
            isResource = false;

        return(isResource);
    }//end isResourceItem()
    
   /**
    * If debug printing is enabled in the Export Options, print the
    * specified message.  If it is turned off, ignore the request.
    * If no Export Options have been loaded yet (null object), assume
    * enabled.
    * @param debugMessage = Debug message to print if debug printing enabled.
    **/
    private void printDebug(String debugMessage){
        if(    (_exportOptions==null)
            || (_exportOptions.getPrintDebugEnable())
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
    * If trace printing is enabled in the Export Options, print the
    * specified message.  If it is turned off, ignore the request.
    * If no Export Options have been loaded yet (null object), assume
    * enabled.
    * @param traceMessage = Trace message to print if trace printing enabled.
    **/
    private void printTrace(String traceMessage){
        if(    (_exportOptions==null)
            || (_exportOptions.getPrintTraceEnable())
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
        System.out.println("Tool Program:  TExportManagerICM");
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
        System.out.println("Tool Program:  TExportManagerICM");
        System.out.println("-----------------------------------------------");
        if(_masterPackageName.compareToIgnoreCase(DEFAULT_MASTER_PACKAGE_NAME)==0) 
              System.out.println("    Master Name: "+_masterPackageName);
        else  System.out.println("    Master Name: "+_masterPackageName+" (default)");
              System.out.println("      Datastore: "+_database);
              System.out.println("      User Name: "+_userName);
              System.out.println("   Connect Opts: "+_connOpts);
        if(_exportReconcileDifferences) 
              System.out.println("   Connect Opts: "+FILE_TAG_ITEM_SELECTION_BY_DIFFERENCES);
        else if(_exportAllChoice!=null)
              System.out.println("   Connect Opts: "+FILE_TAG_ITEM_SELECTION_BY_ITEMTYPE);
        else  System.out.println("   Connect Opts: "+FILE_TAG_ITEM_SELECTION_BY_QUERY);
        if(_exportAllChoice!=null)
              System.out.println("     Item Types: "+_exportAllChoice);
        else  System.out.println("          Query: "+_query);
        if(_dataModelSupportsChildren)
              System.out.println("       Children: Yes");
        else  System.out.println("       Children: No  (disabled)");
        if(_dataModelSupportsLinks)
              System.out.println("          Links: Yes");
        else  System.out.println("          Links: No  (disabled)");
        if(_dataModelSupportsFoldersWithContents)
              System.out.println("Folder Contents: Yes");
        else  System.out.println("Folder Contents: No  (disabled)");
        if(_dataModelSupportsResourceItemsWithContent)
              System.out.println("      Resources: Yes");
        else  System.out.println("      Resources: No  (disabled)");
        if(_dataModelSupportsPartsWithContent)
              System.out.println("          Parts: Yes");
        else  System.out.println("          Parts: No  (disabled)");
        if(_iniFileName.compareToIgnoreCase(DEFAULT_INI_FILE_NAME)==0)
              System.out.println("    Config File: "+_iniFileName+" (default)");
        else  System.out.println("    Config File: "+_iniFileName);
        if(_numItemsPerPackage.intValue()==DEFAULT_NUM_ITEMS_PER_PACKAGE)
              System.out.println("# Items/Package: "+_numItemsPerPackage+" (default)");
        else  System.out.println("# Items/Package: "+_numItemsPerPackage);
              System.out.println("    Master Logs: "+_masterLogFileDirectory.getAbsolutePath());
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
    * Retrieve the query results according to the definition of the data model.
    * Economize server operations utilizing the minimum retrieval operations needed
    * to retrieve all of the data.  
    *
    * If the export options are set to deny retrieval to the Export Package, this
    * master utility is responsible.  Otherwise, just return a collection without
    * retrieving more becaues the tool will complete that operation.
    *
    * @param cursor           - Cursor for the search results
    * @return Returns a collection of all items retrieved from the cursor for
    *         the maximum number of items allowed in a package, or as many
    *         remain in the cursor.
    **/
    private dkCollection retrievePackageItems(dkResultSetCursor cursor) throws Exception{
        printDebug("--- Managing Retrieving Package Items...");
        // Create return collection
        dkCollection coll = new DKSequentialCollection();
        
        // First, put into a collection.
        printDebug("    --> Building Collection...");
        DKDDO ddo = null;
        while((ddo = cursor.fetchNext())!=null){ // continue until we read the entire cursor
            coll.addElement(ddo);
        }//end while((ddo = cursor.fetchNext())!=null){ // continue until we read the entire cursor
        printDebug("    --> '"+coll.cardinality()+"' Items in Collection.");
        
        // Process the collection
        retrievePackageItems(coll);
        
        return(coll);
    }//end retrievePackageItems(cursor)
        

   /**
    * Retrieve the query results according to the definition of the data model.
    * Economize server operations utilizing the minimum retrieval operations needed
    * to retrieve all of the data.  
    *
    * If the export options are set to deny retrieval to the Export Package, this
    * master utility is responsible.  Otherwise, just return a collection without
    * retrieving more because the tool will complete that operation.
    *
    * @param coll - Collection of items to retrieve
    **/
    private void retrievePackageItems(dkCollection coll) throws Exception{

        // If the Export Manager is going to take on ABSOLUTE responsibility
        // for retrieving, then retrieve.  Otherwise let the Export Package API
        // take care of it.
        if(_exportOptions.isRetrieveDenied()==true){
            printDebug("    --> Export Mangaer will take absolute responsibilty for retrieving.");

            // Retrieve Meta-Data using Multi-Item Retrieve
            printDebug("    --> Retrieving Meta-Data With Multi-Item Retrieve...");
            printDebug("        - <Building Retrieve Option>...");
            // Determine Retrieve Option for Child Level Required.
            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(_dsICM);
            if(_dataModelSupportsChildren){  // Select options for "ITEMTREE NO LINKS"
                dkRetrieveOptions.baseAttributes(true);
                dkRetrieveOptions.basePropertyAclName(true);
                dkRetrieveOptions.childListOneLevel(true);
                dkRetrieveOptions.childListAllLevels(true);
                dkRetrieveOptions.childAttributes(true);
            }else{ // otherwise, we only need attributes  (select options for "ATTRONLY"
                dkRetrieveOptions.baseAttributes(true);
                dkRetrieveOptions.basePropertyAclName(true);
            }
            if(_dataModelSupportsPartsWithContent){ // Then select parts table of contents.  Content will not actually be retrieved.
                dkRetrieveOptions.partsList(true);
                dkRetrieveOptions.partsAttributes(true);               // Also retrieve the attributes so they can be optimized in one multi-item retrieve call now.
                dkRetrieveOptions.partsPropertyAclName(true);
            }
            printDebug("        - Retrieve options chosen:  "+dkRetrieveOptions.toString(false));
            printDebug("        - Calling Multi-Item Retrieve...");
            _dsICM.retrieveObjects(coll,dkRetrieveOptions.dkNVPair());
            printDebug("        - <completed multi-item retrieve>");
            
            // Retrieve Links
            printDebug("    --> Retrieving Links...");
            printDebug("        - <Building Retrieve Option>...");
            dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(_dsICM); // Reset retrieve options to "IDONLY"
            if(_dataModelSupportsLinks){ // Includes folders if links are selected.
                dkRetrieveOptions.linksInbound(true);
                dkRetrieveOptions.linksOutbound(true);
                // Outbound links require system attributes
                dkRetrieveOptions.baseAttributes(true);
                DKProjectionListICM dkProjectionList = DKProjectionListICM.createInstance(_dsICM);
                dkProjectionList.addProjection("*",DKConstantICM.DK_ICM_SYSTEM_ATTRS);  // Only need system attributes refreshed to get the semantic type.
                dkRetrieveOptions.attributeFilters(dkProjectionList);
            }else if(_dataModelSupportsFoldersWithContents){
                dkRetrieveOptions.linksOutbound(true);
                // Outbound links require system attributes
                dkRetrieveOptions.baseAttributes(true);
                DKProjectionListICM dkProjectionList = DKProjectionListICM.createInstance(_dsICM);
                dkProjectionList.addProjection("*",DKConstantICM.DK_ICM_SYSTEM_ATTRS);  // Only need system attributes refreshed to get the semantic type.
                dkRetrieveOptions.attributeFilters(dkProjectionList);
            }
            printDebug("        - Retrieve options chosen:  "+dkRetrieveOptions.toString(false));
            // If any were selected, perform retrieve
            if(dkRetrieveOptions.linksInbound()||dkRetrieveOptions.linksOutbound()){
                printDebug("        - Performing Retrieve of Links...");
                _dsICM.retrieveObjects(coll,dkRetrieveOptions.dkNVPair()); // Call multi-item retrieve, but it will delegate to single item link retrieval calls            
                printDebug("        - Completed Retrieve of Links.");
            }else{
                printDebug("        - Skipped Link Retrieval.");
            }
            
            // Retrieve Resource Item's Resource Content
            printDebug("    --> Retrieving Resource Item's Content...");
            if(_dataModelSupportsResourceItemsWithContent){
                // Build retrieve option
                dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(_dsICM); // Reset retrieve options to "IDONLY"
                dkRetrieveOptions.resourceContent(true);
                dkRetrieveOptions.behaviorSkipResourceAttrRefresh(true);
                dkRetrieveOptions.behaviorSkipExistenceCheck(true); // Required to be used with skip RM Attr Refresh.
                printDebug("        - Retrieve options chosen:  "+dkRetrieveOptions.toString(false));
                // Iterate over all items, if any are resource items, call retrieve on them
                dkIterator iter = coll.createIterator();
                while(iter.more()){
                    DKDDO ddo = (DKDDO) iter.next();
                    if(isResourceItem(ddo)){
                        printDebug("        - Calling Single Item Retrieve on a Resoruce Item...");   
                        ddo.retrieve(dkRetrieveOptions.dkNVPair());
                    }//end if(isResoruceItem(ddo)){
                }//end while(iter.more()){
            }else{
                printDebug("        - Skipped Resource Item's Content Retrieval.");
            }

            // Retrieve Parts's Resource Content
            printDebug("    --> Retrieving Part's Content...");
            if(_dataModelSupportsPartsWithContent){
                // Build retrieve option
                dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(_dsICM); // Reset retrieve options to "IDONLY"
                dkRetrieveOptions.resourceContent(true);
                dkRetrieveOptions.behaviorSkipResourceAttrRefresh(true);
                dkRetrieveOptions.behaviorSkipExistenceCheck(true); // Required to be used with skip RM Attr Refresh.
                printDebug("        - Retrieve options chosen:  "+dkRetrieveOptions.toString(false));
                // Iterate over all items, if any have a parts collection , call retrieve on any parts found
                dkIterator iter = coll.createIterator();
                while(iter.more()){
                    DKDDO ddo = (DKDDO) iter.next();
                    // Check for DKParts attribute
                    short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
                    // Only if there is a DKParts collection, look for parts                
                    if(dataid > 0){
                        // Get Parts collection
                        DKParts dkParts = (DKParts) ddo.getData(dataid); 
                        if(dkParts!=null){ // Only continue if parts collection is not null.
                            dkIterator partsIter = dkParts.createIterator();
                            DKDDO      part      = (DKDDO) partsIter.next();
                            printDebug("        - Calling retrieve on a part...");
                            part.retrieve(dkRetrieveOptions.dkNVPair());
                            printDebug("        - Completed retrieve on a part.");
                        }//end if(dkParts!=null){ // Only continue if parts collection is not null.
                    }//end if(dataid > 0){
                }//end while(iter.more()){
            }else{
                printDebug("        - Skipped Part's Content Retrieval.");
            }
        }//end if(_exportOptions.isRetrieveDenied()==true){
        else{
            printDebug("    --> Export Mangaer will delegate responsibilty for retrieving to the Export Package API.");
        }//end else of if(_exportOptions.isRetrieveDenied()==true){
        
        printDebug("--- Managed Package Item Retrieval Complete for collection of '"+coll.cardinality()+"' items.");
    }//end retrievePackageItems(dkCollection);

   /**
    * Execute the Primary Program
    **/
    public void run() throws DKException, Exception{
        
        // Peform the feature requested by the user.
        
        switch(_featureMode){
            case FEATURE_EXPORT:     // If export operation selected, perform export
                export();
                break;
            //not available: case FEATURE_RECONCILE:  // If reconcile operation selected, perform Reconciliation
            //not available:     reconcile();
            //not available:     break;
            default:
                throw new InternalError("Internal Error:  Unknown feature mode identifier '"+_featureMode+"'.");
        }//end switch(featureChoice){
        
    }//end run()

   /**
    * Select the next available storage location.  This should be called when
    * the previous location fills up.
    **/
    private void selectNextStorageLocation() throws Exception{
        // Throw error if reached last storage location if we already retried tried all locations
        if((_hasRetriedAllStorageLocations==true) && (_currentStorageLocationNum >= _storageLocations.length)){
            _writeAttemptManager.terminateAndThrow("The last available Volume / Storage Location '"+_storageLocations[_currentStorageLocationNum]+"' has run out of disk space.  More storage locations are needed..");
        }//end if((_hasRetriedAllStorageLocations==true) && (_currentStorageLocationNum >= _storageLocations.length)){

        // Otherwise, if we haven't tried them all, reset back to '1'
        else if((_hasRetriedAllStorageLocations==false) && (_currentStorageLocationNum >= _storageLocations.length)){
            _currentStorageLocationNum = 1;
            _hasRetriedAllStorageLocations = true;
            printDebug("--- Reached end of storage locations.  Retrying previous locations again.");
        }//end else if((_hasRetriedAllStorageLocations==false) && (_currentStorageLocationNum >= _storageLocations.length)){
        
        // Otherwise just increment to the next storage location
        else{ 
            // Increment Storage Location
            _currentStorageLocationNum++;
        }//end else{
        
        // Provide Notice
        System.out.println("--- NEW STORAGE LOCATION SELECTED:  "+_storageLocations[_currentStorageLocationNum]);
        
        // Track to change
        track(TRACKING_TAG_VOLUME_CURRENT,_storageLocations[_currentStorageLocationNum].getAbsolutePath());
        
    }//end selectNextStorageLocation();

   /**
    * Set the number of directly selected items per package.  Validate
    * for only valid values.
    * @param numItemsPerPackageStr - String setting.
    **/
    private void setNumItemsPerPackage(String numItemsPerPackageStr) throws IllegalArgumentException{
        // Convert to int, catching incorrect format in string.
        try{ _numItemsPerPackage = Integer.valueOf(numItemsPerPackageStr); }
        catch(ClassCastException exc){
            throw new IllegalArgumentException("Number of directly selected items per package option value '"+numItemsPerPackageStr+"' is not a valid whole number.  The number of items per package must be a whole number, such as 3502.  Make sure your value does not include any decimal places and contains only numbers.  Examine your coomandline argument '-n/num' and properties file '"+_iniFileName+"' for tag '"+CONFIG_TAG_NUM_ITEMS_PER_PACKAGE+"'.");
        }
        // Ensure > 0
        if(_numItemsPerPackage.intValue() <= 0) throw new IllegalArgumentException("Number of directly selected items per package option value '"+_numItemsPerPackage+"' is not a positive whole number.  The number of items per package must be a positive ( >= 1 ), such as 3502.  Make sure your value is not zero ('0') and is not negative ('-').  Examine your coomandline argument '-n/num' and properties file '"+_iniFileName+"' for tag '"+CONFIG_TAG_NUM_ITEMS_PER_PACKAGE+"'.");
    }//end setNumItemsPerPackage()

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
            fileAppendLn(_masterTrackingFile.getAbsolutePath(),line);
            
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
    * Keep track of the specified abandond folder.
    * @param folder - Folder that couldn't be cleaned up (deleted)
    **/
    private void trackFailedCleanup(File folder) throws IOException{
        // Report to tracking file.
        track(TRACKING_TAG_FAILURE_FOLDER_CLEANUP,folder.getAbsolutePath());
        // Add to list of failed folders.
        _abandonedDirectoryInfoList.add(folder.getAbsolutePath());
    }//end trackFailedCleanup()

   /**
    * Startup the export tool.  Starting tracking of the tool and 
    * restart where left off if the tool has been restarted.
    * If this is the first, run a new log will be created.
    **/
    private void toolStartup() throws IOException, Exception{
        printDebug("    --> Tool Startup Routine...");

        // If selecting differences from reconcile summary file, load data
        boolean loadDifferenceData = toolStartupDetermineIfLoadDifferences();
        if(loadDifferenceData){
            toolStartupLoadDifferences();
        }//end if(loadDifferenceData){

        // Determine if any existing package should be reloaded
        boolean reloadFromTracking = toolStartupDetermineIfReloadTracking();
        if(reloadFromTracking){ // Restart if needed
            toolStartupReloadFromTracking();
        }//end if(reloadFromTracking){ // Restart if needed

        // If never started, create new file, starting with header information
        if(_statNumToolRestarts<=0){
            toolStartupCreateNewTrackingFile();
        }//end if(_statNumToolRestarts<=0){

        // Tool Started
        printDebug("        - Tracking Tool Start Information...>");
        trackTime(TRACKING_TAG_TOOL_START);
        _statNumToolRestarts++;
        // record start time
        // <Already set by initByDefaults()
        // Record starting volume
        track(TRACKING_TAG_VOLUME_CURRENT,_storageLocations[_currentStorageLocationNum].getAbsolutePath());
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
            fileCreate(_masterTrackingFile.getAbsolutePath(), fileStr.toString());
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
    * Determine if we need to load the Reconcile Summary Differences.
    * If the file exists and the user didn't specify to force loading,
    * prompt the user.  If it will load, set the flag to select items
    * by differneces only.
    * @return Returns 'true' if the tool should load the reconcile summary
    *         differences or 'false' if it should not.
    **/
    private boolean toolStartupDetermineIfLoadDifferences() throws IOException{
        boolean loadDifferences = false;
        // If set to force loading, simply report that it should be loaded.
        if(_exportReconcileDifferences==true){ // If not set to force load diffen
            printDebug("        - Determined Reconcile Summary Differences must be loaded.");
            loadDifferences = true;
        }//end if(_exportReconcileDifferences==false){ // If not set to force load diffen
        // Else it was not specified to force, but if the file exists,\
        // set the flag to true & report it should load.
        else if(_reconcileSummaryFile.exists()){
            printDebug("        - Detected Reconcile Summary Differences File.");
            // Prompt user
            System.out.println("");
            System.out.println(" RECONCILED DIFFERENCES DETECTED:");
            System.out.println("     Master Package Name:  "+_masterPackageName);
            System.out.println("    Master Log Directory:  "+_masterLogFileDirectory.getAbsolutePath());
            System.out.println("  Reconcile Summary File:  "+_reconcileSummaryFile.getAbsolutePath());
            System.out.println("");
            System.out.println("  A reconcile summary report was found.  Reconcile summary reports are      ");
            System.out.println("  written by the TImportManagerReconcilerICM tool when used to compare the  ");
            System.out.println("  documented result of an import process through TImportManagerICM against  ");
            System.out.println("  the original system to ensure that all items were successfully imported.  ");
            System.out.println("  A report was found which was likely written by a previous import process  ");
            System.out.println("  using the same Master Log Directory and Master Package Name.              ");
            System.out.println("                                                                            ");
            System.out.println("  Do you want to export the differences identified by the Reconcile Summary ");
            System.out.println("  File?  (Y/N)                                                              ");
            String answer = promptUser("  >  ").trim();
            // Validate Answer
            if((answer==null)||(answer.length()==0)) throw new IllegalArgumentException("Invalid response '"+answer+"' to prompt to load reconcile differences.  Expected 'Y' or 'N'.");
            if( (answer.compareToIgnoreCase("Y")==0) || (answer.compareToIgnoreCase("YES")==0)){
                printGeneral("--> Understood 'Yes', export only items identified in reconcile summary differences.");
                _exportReconcileDifferences = true;
                loadDifferences = true;
            }else if( (answer.compareToIgnoreCase("N")==0) || (answer.compareToIgnoreCase("NO")==0)){
                printGeneral("--> Understood 'No', do not export the items identified in the reconcile summary.");
                _exportReconcileDifferences = false;
                loadDifferences = false;
            }else{
                throw new IllegalArgumentException("Invalid response '"+answer+"' to prompt to load reconcile differences.  Expected 'Y' or 'N'.");
            }//end }else{
        }// else if(_reconcileSummaryFile.exists()){
        else{ // Otherwise do not load differences
            printDebug("        - No Reconcile Summary Differences Detected.");
            loadDifferences = false;
        }
        return(loadDifferences);
    }//end toolStartupDetermineIfLoadDifferences()

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
                System.out.println(" PREVIOUS MASTER PACKAGE DETECTED:");
                System.out.println("     Master Package Name:  "+_masterPackageName);
                System.out.println("    Master Log Directory:  "+_masterLogFileDirectory.getAbsolutePath());
                System.out.println("    Master Tracking File:  "+_masterTrackingFile.getAbsolutePath());
                System.out.println("");
                String answer = promptUser(" Do you want to continue existing package?  (Y/N)>  ").trim();
                // Validate Answer
                if((answer==null)||(answer.length()==0)) throw new IllegalArgumentException("Invalid response '"+answer+"' to prompt to continue exisitng package.  Expected 'Y' or 'N'.");
                if( (answer.compareToIgnoreCase("Y")==0) || (answer.compareToIgnoreCase("YES")==0)){
                    printGeneral("--> Understood 'Yes', continue where existing master package left off.");
                    reloadFromTracking = true;    
                }else if( (answer.compareToIgnoreCase("N")==0) || (answer.compareToIgnoreCase("NO")==0)){
                    printGeneral("--> Understood 'No', do not continue where existing master package left off.");
                    reloadFromTracking = false;
                    // Then prompt to overwrite
                    System.out.println("");
                    System.out.println("");
                    answer = promptUser("Overwrite existing data? (Y/N)>  ").trim();
                    if( (answer.compareToIgnoreCase("Y")==0) || (answer.compareToIgnoreCase("YES")==0)){
                        printGeneral("--> Understood 'Yes', overwrite existing master package.");
                    }else{
                        throw new IllegalArgumentException("User selected not to overwrite a package that already exists.  Choose a different master file log location or master file name, answer yes to the 'continue from existing package' prompt, or use the command line input to specify restart or new package.");   
                    }
                }else{
                    throw new IllegalArgumentException("Invalid response '"+answer+"' to prompt to continue existing package.  Expected 'Y' or 'N'.");
                }//end }else{
            }//end if(_masterTrackingFile.exists()){
            else{
                printDebug("        - No Previous Data Found.  Starting New Master Package.");
            }//end else of if(_masterTrackingFile.exists()){
        }//end else of if(_restartRequested){
        return(reloadFromTracking);
    }//end toolStartupDetermineIfReloadTracking()

   /**
    * Load the differences identified in the Reconcile Summary File.
    **/
    private void toolStartupLoadDifferences() throws FileNotFoundException, IOException, Exception{
        printDebug("    --> Loading Reconcile Summary Differences '"+_reconcileSummaryFile.getAbsolutePath()+"'...");
        
        // Open File
        FileReader fileReader = new FileReader(_reconcileSummaryFile.getAbsolutePath());
        BufferedReader file   = new BufferedReader(fileReader);

        // Read & Validate File Identifier & Version Check
        String line = file.readLine();
        if(!line.startsWith(RECONCILE_SUMMARY_FILE_IDENTIFIER))
            throw new IllegalArgumentException("File specified, '"+_reconcileSummaryFile.getAbsolutePath()+"', does not appear to be a Reconcile Summary File.");
        String fileVersion = line.substring(line.lastIndexOf('v')+1);
        if(fileVersion.compareTo(MASTER_PACKAGE_VERSION) != 0){
            throw new IllegalArgumentException("WARNING:  Reconcile Summary File was not exported from the same version of this tool set.  File is from version '"+fileVersion+"' version, but the current version is '"+MASTER_PACKAGE_VERSION+"'.  The old format may not be compatable with the new format.  Use the same version of Export Manager and Import Manager Reconciler that was used to write the Reconcile Summary File.");
        }//end if(fileVersion.compareTo(MASTER_PACKAGE_VERSION) != 0){

        // Read File Line-by-line, handling each package as found.
        for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.
            printDebug("LINE "+lineNum+":  "+line);

            // Just find all Missing Items
            if(line.startsWith(RECONCILE_SUMMARY_TAG_MISSING_ITEM)){
                String value = line.substring(RECONCILE_SUMMARY_TAG_MISSING_ITEM.length());
                if(value==null || value.trim().length()==0)
                    throw new InternalError("Invalid Missing Item entry at line '"+lineNum+"' of Reconcile Summary File '"+_reconcileSummaryFile.getAbsolutePath()+"'.  The value did not contain any data.  Expected all missing item entries to have valid Pid information.");
                // Create New Missing Item Record
                TExportManagerICM_MissingItem missingItem = new TExportManagerICM_MissingItem(value);
                // Save on Missing Items List
                _missingItems.put(missingItem.getKey(),missingItem);
            }
            // If we find the item types selected list, verify against current list
            // selected and make sure they are the same
            else if(line.startsWith(RECONCILE_SUMMARY_TAG_ITEMTYPES_SELECTED)){
                String value = line.substring(RECONCILE_SUMMARY_TAG_ITEMTYPES_SELECTED.length());
                if(value.compareToIgnoreCase(_exportAllChoice)!=0)
                    throw new Exception("The master package configuration that created Reconcile Summary File '"+_reconcileSummaryFile.getAbsolutePath()+"' is not the same as the current configuration.  For this configuration parameter, they must be identical.  The selection of item types for which items are to be exported from in the Reconcile Summary File is '"+value+"' (line '"+lineNum+"') while the current package is '"+_exportAllChoice+"'.  You cannot change the selection criteria at this point.  Any Master Package created based on differences from a previous package must use the identical item type selection criteria.");
            }
            
        }//end for(int lineNum = 2;(line = file.readLine())!=null; lineNum++){ // Continue until reach end of file.

        // Close the file
        file.close();

        printDebug("    --> Reload Complete.");
    }//end toolStartupLoadDifferences()

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
            throw new IllegalArgumentException("File specified, '"+_masterTrackingFile.getAbsolutePath()+"', does not appear to be a Master Export Package Tracking File.");
        String fileVersion = line.substring(line.lastIndexOf('v')+1);
        if(fileVersion.compareTo(MASTER_PACKAGE_VERSION) != 0){
            throw new IllegalArgumentException("WARNING:  Master Export Package Tracking File was not exported from the same version of this tool.  File is from version '"+fileVersion+"' version, but the current version is '"+MASTER_PACKAGE_VERSION+"'.  The old format may not be compatable with the new format.  Use the same version of Export Manager and Import Manager tools as was used to write the existing file.");
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
            // - Item Selection Method (List of Item Types or Query)
            else if(line.startsWith(FILE_TAG_ITEM_SELECTION_METHOD)){
                String value = line.substring(FILE_TAG_ITEM_SELECTION_METHOD.length()).trim();
                if(value.compareToIgnoreCase(FILE_TAG_ITEM_SELECTION_BY_DIFFERENCES)!=0){
                    if(_exportReconcileDifferences==false)
                        throw new IllegalArgumentException("Method of item selection currently specified in the configuraiton file '"+_iniFileName+"' or by command line options does not match the method identified, '"+value+"', at line '"+lineNum+"' in the tracking file, '"+_masterTrackingFile.getAbsolutePath()+"'.  You can only restart an existing export process if you plan to continue the original package that was in progress.  You must delete or overwrite the original package instead of resuming where you were if you are not going to use the original selection criteria.");
                }else if(value.compareToIgnoreCase(FILE_TAG_ITEM_SELECTION_BY_ITEMTYPE)!=0){
                    if(_exportAllChoice==null)
                        throw new IllegalArgumentException("Method of item selection currently specified in the configuraiton file '"+_iniFileName+"' or by command line options does not match the method identified, '"+value+"', at line '"+lineNum+"' in the tracking file, '"+_masterTrackingFile.getAbsolutePath()+"'.  You can only restart an existing export process if you plan to continue the original package that was in progress.  You must delete or overwrite the original package instead of resuming where you were if you are not going to use the original selection criteria.");
                }else if(value.compareToIgnoreCase(TExportManagerICM.FILE_TAG_ITEM_SELECTION_BY_QUERY)!=0){
                    if(_query==null)
                        throw new IllegalArgumentException("Method of item selection currently specified in the configuraiton file '"+_iniFileName+"' or by command line options does not match the method identified, '"+value+"', at line '"+lineNum+"' in the tracking file, '"+_masterTrackingFile.getAbsolutePath()+"'.  You can only restart an existing export process if you plan to continue the original package that was in progress.  You must delete or overwrite the original package instead of resuming where you were if you are not going to use the original selection criteria.");
                }else{
                    throw new IllegalArgumentException("Unknown method of item selection specified in the tracking file '"+_masterTrackingFile.getAbsolutePath()+"' at line '"+lineNum+"'.");
                }
            }
            // - Export All Choice
            else if(line.startsWith(FILE_TAG_EXPORT_ALL_CHOICE)){
                String compareToValue = "";
                if(_exportAllChoice!=null) compareToValue = _exportAllChoice;
                String value = line.substring(FILE_TAG_EXPORT_ALL_CHOICE.length());
                if(value.compareToIgnoreCase(compareToValue)!=0)
                    throw new Exception("The master package configuration that created tracking file '"+_masterTrackingFile.getAbsolutePath()+"' is not the same as the current configuration.  For this configuration parameter, they must be identical.  The selection of item types for which items are to be exported from in the tracking file is '"+value+"' (line '"+lineNum+"') while the current package is '"+compareToValue+"'.  You cannot change the selection criteria at this point.  Complete this master package with the existing selection criteria and then create a separate master package for the difference.");
            }
            // - Query
            else if(line.startsWith(FILE_TAG_QUERY)){
                String compareToValue = "";
                if(_query!=null) compareToValue = _query;
                String value = line.substring(FILE_TAG_QUERY.length());
                if(value.compareToIgnoreCase(compareToValue)!=0)
                    throw new Exception("The master package configuration that created tracking file '"+_masterTrackingFile.getAbsolutePath()+"' is not the same as the current configuration.  For this configuration parameter, they must be identical.  The query for which items are to be exported from in the tracking file is '"+value+"' (line '"+lineNum+"') while the current package is '"+compareToValue+"'.  You cannot change the selection criteria at this point.  Complete this master package with the existing selection criteria and then create a separate master package for the difference.");
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
            // - # Items/Package
            else if(line.startsWith(FILE_TAG_NUM_ITEMS_PER_PACKAGE)){
                String value = line.substring(FILE_TAG_NUM_ITEMS_PER_PACKAGE.length());
                Integer valueInt = null;
                try{ valueInt = Integer.valueOf(value); }
                catch(Exception exc){
                    throw new Exception("Error in line '"+lineNum+"' in tracking file '"+_masterTrackingFile.getAbsolutePath()+"' configuration section.  Expected a whole number, but instead found '"+value+"'.  The following error occurred while attempting to convert it to a nubmer:  "+exc.getMessage());
                }
                if(valueInt != _numItemsPerPackage)
                    printGeneral("WARNING:  The master package configuration that created tracking file '"+_masterTrackingFile.getAbsolutePath()+"' is not the same as the current configuration.  The number of directly selected items per package in the tracking file is '"+valueInt+"' (line '"+lineNum+"') while the current package is '"+_numItemsPerPackage+"'.  The tool will use the new number of items per package '"+_numItemsPerPackage+"'.");
            }
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
            // - Storage Locations Available
            // <Omit, take on any changes.  When completed packages are loaded, their existance will be checked>

            // TRACKING
            // - Volume Full
            // <No state inofrmation needed>
            // - Current Volume
            else if(line.startsWith(TRACKING_TAG_VOLUME_CURRENT)){
                String value = line.substring(TRACKING_TAG_VOLUME_CURRENT.length());
                // Find volume among current volume entries.  If exists, set that to current volume
                int volumeNum = findStorageLocation(value);
                if(volumeNum >= 0){
                    printDebug("Noted previous move onto storage location '"+volumeNum+"' (line '"+lineNum+"'): "+value+"");
                    _currentStorageLocationNum = volumeNum;
                }else{
                    printGeneral("WARNING:  The master tracking file '"+_masterTrackingFile.getAbsolutePath()+"' includes a reference to a storage location '"+value+"' (line '"+lineNum+"') that is no longer on the list of storage locations in the current configuration..");
                }
            }
            // - Package Started
            // <omit - not needed to resume>
            // - Package Completed
            else if(line.startsWith(TRACKING_TAG_PACKAGE_COMPLETED)){
                String value = line.substring(TRACKING_TAG_PACKAGE_COMPLETED.length());
                TExportManagerICM_PackageInfo packageInfo = new TExportManagerICM_PackageInfo(value,_storageLocations,_exportOptions);
                // Detect out of order
                if(packageInfo.getPackageNum()!=_currentPackageNum)
                    throw new Exception("Package out of order in line '"+lineNum+"' in tracking file '"+_masterTrackingFile.getAbsolutePath()+"' configuration section.  Expected package number '"+_currentPackageNum+"', but instead found package '"+packageInfo.getPackageNum()+"'.  Package \""+packageInfo.toString()+"\" was constructed from tracking file entry \""+value+"\".");
                // Save as completed package
                printDebug("--> Recognized package '"+_currentPackageNum+"': "+packageInfo.toString());
                _completedPackageInfoList.put(packageInfo.getKey(),packageInfo);
                _lastPackageEndingItemID = packageInfo.getLastItemId();
                _currentPackageNum++; // and increment the current package number to the next
                _statNumTotalItems += packageInfo.getNumItems();
            }
            // - Missing Item Package Completed
            else if(line.startsWith(TRACKING_TAG_MISSING_ITEM_PACKAGE_COMPLETED)){
                String value = line.substring(TRACKING_TAG_MISSING_ITEM_PACKAGE_COMPLETED.length());
                // Package information already handled by package completed condition, this
                // just tracks last PID
                _lastPackageEndingItemID = value.trim();
                printDebug("--> Missing Item Package Completed Ending With '"+value+"'");
            }
            // - Failed Folder Cleanup
            else if(line.startsWith(TRACKING_TAG_FAILURE_FOLDER_CLEANUP)){
                String value = line.substring(TRACKING_TAG_FAILURE_FOLDER_CLEANUP.length());
                // Add to list of failed folders.
                _abandonedDirectoryInfoList.add(value);
                printDebug("--> Recognized abandoned directory:  "+value);
            }
            // - All Packages Started: TRACKING_TAG_ALL_PACKAGES_STARTED
            // <ignore>
            // - All Packages Completed
            else if(line.startsWith(TRACKING_TAG_ALL_PACKAGES_COMPLETED)){
                String value = line.substring(TRACKING_TAG_ALL_PACKAGES_COMPLETED.length());
                _allPackagesCompleted = true;
                printDebug("--> Found out that master package was completed exporting all packages at '"+value+"'.");
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
            // - Failure at write level            
            else if(line.startsWith(TRACKING_TAG_FAILURE_WRITE_LEVEL)){
                String value = line.substring(TRACKING_TAG_FAILURE_WRITE_LEVEL.length());
                _statNumWriteErrors++;
                printDebug("--> Recognized failure at write level.");
            }
            // - Summary Started: TRACKING_TAG_SUMMARY_STARTED
            // <no action needed>
            // - Summary Completed: 
            else if(line.startsWith(TRACKING_TAG_SUMMARY_COMPLETED)){
                String value = line.substring(TRACKING_TAG_SUMMARY_COMPLETED.length());
                // If summary file doesn't exist, let it re-write it
                if(_masterSummaryFile.exists()==false){
                    printGeneral("WARNING:  Master Export Package File / Summary file was listed as completed in the tracking file, but file '"+_masterSummaryFile.getAbsolutePath()+"' does not exist.  The tool will regenerate the file.");
                }else{
                    throw new IllegalArgumentException("WARNING:  Master Export Package File / Summary file was listed as coiimpleted int the tracking file.  Assuming the file was generated correctly, there is no need to restart this tool.  However, if you wish to regenerate the file, delete the master export package file / summary file '"+_masterSummaryFile.getAbsolutePath()+"'.");
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

        // Validate Anything Else
        // - If there are missing item packages completed, make sure that the last 
        //   actually exists in the missing item list
        if(_lastPackageEndingItemPid!=null){
            if(_missingItems.containsKey(TExportManagerICM_MissingItem.getKey(_lastPackageEndingItemPid))==false)
                throw new InternalError("Internal Error:  Could not find the last completed missing item package's last item PID, '"+_lastPackageEndingItemPid+"', in the missing item list loaded from the current Reconcile Summary.");
        }//end if(_lastPackageEndingItemPid!=null){
        
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
        //   _exportAllChoice           : Validated after connection established.
        //   _exportReconcileDifferences: No Validation
        //   _featureMode               : No Validation
        //   _iniFileName               : Validated by initByComfigurationFile()
        //   _masterLogFileDirectory    : Make sure exists, is absolute path, and is directory
        if(_masterLogFileDirectory==null) throw new InternalError("Internal Error:  Master Log File Directory object '_masterLogFileDirectory' is 'null' in TExportManagerICM.validateSettings().  An instance of 'File' was expected.");
        if(_masterLogFileDirectory.isAbsolute()==false)  throw new IllegalArgumentException("The name specified for the master log files folder, '"+_masterLogFileDirectory.getAbsolutePath()+"', is not an absolute path.  Provide an absolute path to avoid any ambiguity for the folder in which the master summary and tracking files can be written to.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_LOG_FILE_LOCATION+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_LOG_FILE_DIRECTORY+"'.");
        if(_masterLogFileDirectory.exists()==false)      throw new IllegalArgumentException("The folder specified for the master log files does not exist.  Provide a valid location in which the master summary and tracking files can be written to.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_LOG_FILE_LOCATION+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_LOG_FILE_DIRECTORY+"'.");
        if(_masterLogFileDirectory.isDirectory()==false) throw new IllegalArgumentException("The name specified for the master log files folder, '"+_masterLogFileDirectory.getAbsolutePath()+"', is not a directory / folder.  Provide a valid folder in which the master summary and tracking files can be written to.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_LOG_FILE_LOCATION+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_LOG_FILE_DIRECTORY+"'.");
        //   _masterPackageName         : Make sure non-null, non-empty, must start with alphabetical character.
        if(_masterPackageName==null) throw new IllegalArgumentException("No master package name is specified (null).  A valid master package name is required.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_MASTER_NAME+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_MASTER_PACKAGE_NAME+"'.");
        if(_masterPackageName.trim().length()==0)   throw new IllegalArgumentException("No master package name is specified (empty string '"+_masterPackageName+"').  A valid master package name is required.  Review your connect string options for correct usage of '"+COMMANDLINE_OPTION_MASTER_NAME+"' and your configuration file '"+_iniFileName+"' option '"+CONFIG_TAG_MASTER_PACKAGE_NAME+"'.");
        //   _query                     : Make sure this or _exportAllChoice specified
        if((_exportAllChoice==null) || (_exportAllChoice.trim().length()==0)){
            if((_query==null) || (_query.trim().length()==0)){
                throw new IllegalArgumentException("Neither a list of item types to export all items from or a specific query was specified.  One must be specified in order for the tool to know what to export.  Review your command line options '"+COMMANDLINE_OPTION_EXPORT_ALL_CHOICE+"' and '"+COMMANDLINE_OPTION_QUERY+"'.  Review your configuration file '"+_iniFileName+"' for options '"+CONFIG_TAG_EXPORT_ALL_CHOICE+"' and '"+CONFIG_TAG_QUERY+"'.");
            }//end if((_query==null) || (_query.trim().length()==0)){
        }//end if((_exportAllChoice==null) || (_exportAllChoice.trim().length()==0)){
        //   _numItemsPerPackage        : Already Validated by setItemsPerPackage() method.
        // Internal Variables
        if(_restartRequested && _newRunRequested)
            throw new IllegalArgumentException("Both restart an existing master package and start over have been specified.  Use only 'r|restart', 'k|kill', or niether.  The tool will prompt if an existing master package tracking file is found if neither is selected.  Review the command line argument documentation.");
        // - Objects
        //   _dsICM                     : Validated above
        //   _masterLevelAttemptManager : Not Null
        if(_masterLevelAttemptManager==null)  throw new InternalError("Inernal Error: Object variable '_masterLevelAttemptManager' is null when it should have been initialized by 'TExportManagerICM.initObjects()'.");
        //   _packageLevelAttemptManager: Not Null
        if(_packageLevelAttemptManager==null) throw new InternalError("Inernal Error: Object variable '_packageLevelAttemptManager' is null when it should have been initialized by 'TExportManagerICM.initObjects()'.");
        //   _writeAttemptManager       : Not Null
        if(_writeAttemptManager==null)        throw new InternalError("Inernal Error: Object variable '_writeAttemptManager' is null when it should have been initialized by 'TExportManagerICM.initObjects()'.");
        //   _masterSummaryFile         : Not Null
        if(_masterSummaryFile==null)          throw new InternalError("Inernal Error: Object variable '_masterSummaryFile' is null when it should have been initialized by 'TExportManagerICM.initObjects()'.");
        //   _masterTrackingFile        : Not Null
        if(_masterTrackingFile==null)         throw new InternalError("Inernal Error: Object variable '_masterTrackingFile' is null when it should have been initialized by 'TExportManagerICM.initObjects()'.");
        //   _reconcileSummaryFile      : Not Null, exists if user forces difference selection
        if(_reconcileSummaryFile==null)       throw new InternalError("Inernal Error: Object variable '_reconcileSummaryFile' is null when it should have been initialized by 'TExportManagerICM.initObjects()'.");
        if((_exportReconcileDifferences==true)&&(_reconcileSummaryFile.exists()==false)) throw new IllegalArgumentException("No reconcile summary file found to match the force reconcile option specified at the command line.  Expected the reconcile summary file '"+_reconcileSummaryFile.getAbsolutePath()+"' in directory '"+_masterLogFileDirectory.getAbsolutePath()+"' output by TImportManagerReconcilerICM if commandline option '"+COMMANDLINE_OPTION_DIFFERENCE+"' is used.");
        //   _missingItems              : Not Null
        if(_missingItems==null)               throw new InternalError("Inernal Error: Object variable '_missingItems' is null when it should have been initialized by 'TExportManagerICM.initObjects()'.");
        
    }//end validateSettings()

   /**
    * After all packages have been exported, write a summary report
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
        // - # Storage Locations Used
        fileStr.append(FILE_TAG_STAT_NUM_STORAGE_LOCATIONS_USED);
        int numLocationsUsed = _currentStorageLocationNum;
        // If we wrapped back, list all locations as used.
        if(_hasRetriedAllStorageLocations)
            numLocationsUsed = _storageLocations.length;
        fileStr.append(numLocationsUsed);
        fileStr.append(newline);
        // - Storage Locations Used
        fileStr.append(FILE_TAG_STAT_STORAGE_LOCATIONS_USED);
        for(int i=0; i<numLocationsUsed; i++){
            fileStr.append(_storageLocations[i]);
            // If there is a next, add delimiter
            if((i+1)<_currentStorageLocationNum)
                fileStr.append(STORAGE_LOCATION_DELIMITER);
        }//end for(int i=0; i<_currentStorageLocationNum; i++){
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
        // - # Write Errors
        fileStr.append(FILE_TAG_STAT_NUM_WRITE_ERRORS);
        fileStr.append(_statNumWriteErrors);
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

        // Write Package List
        printDebug("        - <Preparing Package List>...");
        fileStr.append(FILE_TAG_SECTION_PACKAGE_LIST_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_PACKAGE_LIST_HEADER);
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
        fileStr.append(FILE_TAG_SECTION_PACKAGE_LIST_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write Orphaned Directories
        printDebug("        - <Preparing Report of Abandoned Directories>...");
        fileStr.append(FILE_TAG_SECTION_ABANDONED_FOLDERS_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        fileStr.append(FILE_TAG_SECTION_ABANDONED_FOLDERS_HEADER);
        fileStr.append(newline);

        // Loop through the abandonded directory list, writing each
        Iterator<String> abandonedDirIter = _abandonedDirectoryInfoList.iterator();
        while(abandonedDirIter.hasNext()){
            String abandonedDir = abandonedDirIter.next();
            // Double check it hasn't since been reused successfully
            // Loop through all packages, write thier package info.
            boolean hasBeenReused = false;
            packageInfoIter = _completedPackageInfoList.values().iterator();
            while(packageInfoIter.hasNext()){
                TExportManagerICM_PackageInfo packageInfo = packageInfoIter.next();
                if(packageInfo.getFolder().getAbsolutePath().compareToIgnoreCase(abandonedDir)==0){
                    hasBeenReused = true;
                    printDebug("        - <Found abandoned folder '"+abandonedDir+"' has been reused.");
                }
            }//end while(packageInfoIter.hasNext()){

            // Write entry if it has not been reused
            if(hasBeenReused==false){
                // Write entry
                fileStr.append(FILE_TAG_ABANDONED_DIRECTORY);
                fileStr.append(abandonedDir);
                fileStr.append(newline);
            }//end if(hasBeenReused==false){
        }//end while(packageInfoIter.hasNext()){
        
        fileStr.append(FILE_TAG_SECTION_ABANDONED_FOLDERS_END);
        fileStr.append(newline);
        fileStr.append(newline);
        
        // Write to Disk
        printDebug("    --> <Preparing to Write>...");
        // - Double check that there is no existing file by this name.  It should have been
        //   deleted at the beginning of exportAllPackages()
        if(_masterSummaryFile.exists()){
            throw new InternalError("Internal Error:  The master summary file '"+_masterSummaryFile.getAbsolutePath()+"' should have been deleted at the beginning of TExportMangerICM.exportAllPackages().  However, it exists prior to writing the summary report in the TExportManagerICM.writeSummaryReport() method.");
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
            printError("ERROR WRITING MASTER PACKAGE SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"' due to: "+exc.getMessage());
            // If it was because it ran out of space, report specialized error
            if(exc.getMessage().compareToIgnoreCase(EXPECTED_OUT_OF_SPACE_MESSAGE)==0){
                throw new IOException("RAN OUT OF DISK SPACE WRITING MASTER PACKAGE SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"'.  Export had completed successfully, but the tool could not write the summary file.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when enough space is available on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
            }
            throw new IOException("ERROR WRITING MASTER PACKAGE SUMMARY FILE '"+_masterSummaryFile.getAbsolutePath()+"' due to error \""+exc.getMessage()+"\".  Export had completed successfully, but the tool could not write the summary file.  The master log file location really must be reliable and have sufficient disk space.  Restart the tool using the tracking file when the error can be bypassed on '"+_masterLogFileDirectory.getAbsolutePath()+"'.");
        }//end catch(IOException exc){
        
        // Close the file
        printDebug("   --> Closing File...");
        file.close();
        printGeneral("   --> Completed Summary File '"+_masterSummaryFile.getAbsolutePath()+"'.");

        // Track Starting
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
        // - Item Selection Method (List of Item Types or Query)
        fileStr.append(FILE_TAG_ITEM_SELECTION_METHOD);
        if(_exportReconcileDifferences) fileStr.append(FILE_TAG_ITEM_SELECTION_BY_DIFFERENCES);
        else if(_exportAllChoice!=null) fileStr.append(FILE_TAG_ITEM_SELECTION_BY_ITEMTYPE);
        else                            fileStr.append(FILE_TAG_ITEM_SELECTION_BY_QUERY);
        fileStr.append(newline);
        // - Export All Choice
        fileStr.append(FILE_TAG_EXPORT_ALL_CHOICE);
        if(_exportAllChoice==null) fileStr.append("");
        else                       fileStr.append(_exportAllChoice);
        fileStr.append(newline);
        // - Query
        fileStr.append(FILE_TAG_QUERY);
        if(_query==null) fileStr.append("");
        else             fileStr.append(_query);
        fileStr.append(newline);
        // - Retry Attempts
        //Not recorded in this object:  fileStr.append(FILE_TAG_RETRY_ATTEMPTS);
        //Not recorded in this object:  fileStr.append(tbd);
        //Not recorded in this object:  fileStr.append(newline);
        // - Retry Delay Time (ms)
        //Not recorded in this object:  fileStr.append(FILE_TAG_RETRY_DELAY_MS);
        //Not recorded in this object:  fileStr.append(tbd);
        //Not recorded in this object:  fileStr.append(newline);
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
        // - # Items/Package
        fileStr.append(FILE_TAG_NUM_ITEMS_PER_PACKAGE);
        fileStr.append(_numItemsPerPackage);
        fileStr.append(newline);
        // - Master Log File Location
        fileStr.append(FILE_TAG_MASTER_LOG_FILE_DIRECTORY);
        fileStr.append(_masterLogFileDirectory.getAbsolutePath());
        fileStr.append(newline);
        // - Master Summary File
        fileStr.append(FILE_TAG_MASTER_SUMMARY_FILE);
        fileStr.append(_masterSummaryFile.getAbsolutePath());
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
                fileStr.append(ITEMTYPE_LIST_DELIMITER);
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

   /**
    *  Connect the specified datastore isntance with the specified
    *  credentials.  
    *
    * @param dsName     - Name or alias of the datastore to connect to.
    * @param dsUserId   - DB2 Content Manager userid for the datastore
    *                     named by the 'dsName' parameter.
    * @param dsPassword - Password for the DB2 Content Manager userid named
    *                     by 'dsUserId'.
    * @param dsOptions  - Options for the datastore as a Connect String
    *                     as documented by the SConnectDisconnectICM sample.
    * @return none.
    **/
    public static void connect(DKDatastoreICM dsICM, String dsName, String dsUserId, String dsPassword, String dsOptions) throws DKException, IllegalArgumentException, Exception{
    
        // Input validation
        if(dsICM     ==null) throw new IllegalArgumentException("Internal Utility Function Usage Error:  'dsICM' parameter NULL.");
        if(dsName    ==null) throw new IllegalArgumentException("Internal Utility Function Usage Error:  'dsName' parameter NULL.");
        if(dsUserId  ==null) throw new IllegalArgumentException("Internal Utility Function Usage Error:  'dsUserId' parameter NULL.");
        if(dsPassword==null) throw new IllegalArgumentException("Internal Utility Function Usage Error:  'dsPassword' parameter NULL.");
        if(dsName.trim().compareTo("")==0)     throw new IllegalArgumentException("Internal Utility Function Usage Error:  'dsName' parameter is empty string.");
        if(dsUserId.trim().compareTo("")==0)   throw new IllegalArgumentException("Internal Utility Function Usage Error:  'dsUserId' parameter is empty string.");
        if(dsPassword.trim().compareTo("")==0) throw new IllegalArgumentException("Internal Utility Function Usage Error:  'dsOptions' parameter is empty string.");
        if(dsICM.isConnected()==true) throw new IllegalArgumentException("Internal Utility Function Usage Error:  DKDatastoreICM instance 'dsICM' has not been disconnected prior to using the connect() utility function.");

        System.out.println("--- Connecting to Datastore...");
        System.out.println("    ->   DS = "+dsName);
        System.out.println("    -> User = "+dsUserId);
        System.out.println("    -> Opts = "+dsOptions);
        System.out.println("    -> <communicating...>");
        dsICM.connect(dsName, dsUserId, dsPassword, dsOptions);
        System.out.println("    -> Connected.");
        System.out.println("           DS Type:  "+dsICM.datastoreType());
        System.out.println("          Platform:  "+platform2String(dsICM.platform()));
        System.out.println("           DB Type:  "+dbType2String(dsICM.getDBType()));
        
    }//end connect()

   /** 
    *  Get the string name for the database platform identifier.
    *  @param dbType - Database Type Identifier
    *  @return String name corresponding to the dbType.
    **/
    private static String dbType2String(int dbType){
        StringBuffer str = new StringBuffer();
        
        switch(dbType){
            case DKConstantICM.DK_DATA_BASE_TYPE_DB2:
                str.append("DB2");
                break;
            case DKConstantICM.DK_DATA_BASE_TYPE_ORACLE:
                str.append("DB2");
                break;
            default:
                str.append("Unknown");
                break;
        }//end switch(dbType)
        str.append(" (");
        str.append(dbType);
        str.append(')');
        return(str.toString());
    }//end dbType2String()

   /**
    * Append the specified text and add a newline separator to the specified file.  <BR><BR>
    * @param fileName - Name of file to append to. 'null' if disabled.
    * @param text     - text to write (newline separator will
    *                   be added).
    **/
    public static void fileAppendLn(String fileName, String text) throws IOException{
        // Disabled if fileName is 'null'
        if(fileName!=null){
            // Get the system's newline separator.
            String newline = System.getProperty("line.separator");
            FileWriter file = new FileWriter(fileName,true);
            file.write(text + newline);
            file.close();
        }//end if(fileName!=null){
    }//end fileAppendLn()
    
   /**
    * Create or overwrite an existing file of the specified file name.  Write the
    * specified text as the first line and append a newline separator.              <BR><BR>
    * @param fileName - Name of file to create/ovewrite.  'null' if disabled.
    * @param text     - Initial text to write as first line (newline separator will
    *                   be added).
    **/
    public static void fileCreate(String fileName, String text) throws IOException{
        // Disabled if fileName is 'null'
        if(fileName!=null){
            // Get the system's newline separator.
            String newline = System.getProperty("line.separator");
            FileWriter file = new FileWriter(fileName,false);
            file.write(text + newline);
            file.close();
        }//end if(fileName!=null){
    }//end fileCreate()

   /**
    * Copy the file from the specified location to the specified destination
    * @param sourceFile - Source File.  Path optional.
    * @param targetFile - Target File.  Path optional.
    **/
    private static void fileCopy(File sourceFile, File targetFile) throws FileNotFoundException, IOException{
        String newline = System.getProperty("line.separator");

        // Load file into memory
        StringBuffer fileContents = new StringBuffer();
        {
            // - Open File
            FileReader fileReader = new FileReader(sourceFile);
            BufferedReader file   = new BufferedReader(fileReader);
            // - Copy the file line by line.
            String line = null;
            while((line = file.readLine())!=null){ // Continue until reach end of file.
                fileContents.append(line);
            }//end while((line = file.readLine())!=null){ // Continue until reach end of file.
            // - Close File
            file.close();
        }
        
        // Write File
        {
            // - Open File
            FileWriter file = null;
            file = new FileWriter(targetFile);
            // - Write to disk.
            file.write(fileContents.toString());
            // - Close File
            file.close();
        }
    }//end fileCopy()

   /**
    * Get the boolean value of the specified command line option choice,
    * if specified.  A boolean value is returned in all cases.  If it is
    * specified by the user and is a valid boolean description, the users
    * choice is returned.  If no value is specified, the default that is
    * specified to this function is returned.  If the option is found, but 
    * no following value exists, either an empty string will be returned
    * (if it is the last argument) or the next command will be returned.
    * If the option is just a flag, check for 'null' to mean it was not
    * specified, otherwise any valid string returned means that the flag
    * was found.  Note that the flag is case insensitive.  If the flag
    * is specified more than once, an error will be thrown.  Flags should
    * begin with a '-'.
    *
    * Command Line Options:
    *    <flag> <true|false>
    *
    * @param mainArgs       - Argument list from main function.
    * @param flag           - Flag to check for (case insensitive).
    * @param synonymFlag    - Some command line arguments have a short hand
    *                         or other synonym that can be used for the same
    *                         option.  If one exists, specify it here.
    *                         For example, if the flag is '-userId', a synonym
    *                         might be '-u' for easier usage.  A synonym flag
    *                         is optional.
    * @param valueRequired  - Whether or not this option is required, including
    *                         a value.
    * #param defaultValue   - Default to return if no option is specified.
    * @param correctUsage   - if valueRequired, this is the correct usage.
    * @return Returns:  -1 : No argument specified.
    *                    0 : False
    *                    1 : True
    **/
    public static short getCommandlineBooleanChoice(String mainArgs[], String flag, String synonymFlag, boolean valueRequired, String correctUsage) throws Exception{
        short value = -1;
        
        // Get the string value specified by the user.
        String valueStr = getCommandlineChoice(mainArgs,flag,synonymFlag,valueRequired,correctUsage);
        
        // Combine the two flags into one description for easier addition to error messages.
        StringBuffer flagInfo = new StringBuffer(flag); // Start with the required flag.
        if(synonymFlag!=null){
            flagInfo.append('/');
            flagInfo.append(synonymFlag);
        }
        
        // Validate & set the return value.
        if(valueStr!=null){
            if(valueStr.compareToIgnoreCase("true")==0)
                value = 1; // true
            else if(valueStr.compareToIgnoreCase("false")==0)
                value = 0; // false
            else throw new Exception("Invalid command line option value.  For option '"+flagInfo.toString()+"', expected value 'true' or 'false'.  Please use the correct usage: "+correctUsage);
        }
        // otherwise we assume the default.
        //Already set to default:  else{  
        //Already set to default:      value = -1
        //Already set to default:  }
        
        return(value);
    }//end getCommandlineBooleanChoice

   /**
    * Get the value of the specified command line option choice, if specified.
    * If it is specified, the String value will be returned.  If no value
    * is specified, 'null' will be returned.  If the option is found, but 
    * no following value exists, either an empty string will be returned
    * (if it is the last argument) or the next command will be returned.
    * If the option is just a flag, check for 'null' to mean it was not
    * specified, otherwise any valid string returned means that the flag
    * was found.  Note that the flag is case insensitive.  If the flag
    * is specified more than once, an error will be thrown.  Flags should
    * begin with a '-'.
    *
    * Command Line Options:
    *    <flag> <value (optional)>
    *
    * @param mainArgs       - Argument list from main function.
    * @param flag           - Flag to check for (case insensitive).
    * @param synonymFlag    - Some command line arguments have a short hand
    *                         or other synonym that can be used for the same
    *                         option.  If one exists, specify it here.
    *                         For example, if the flag is '-userId', a synonym
    *                         might be '-u' for easier usage.  A synonym flag
    *                         is optional.
    * @param valueRequired  - Whether or not this option is required, including
    *                         a value.
    * @param correctUsage   - if valueRequired, this is the correct usage.
    * @return Returns any valid string if the specified flag was found at the  
    *         command line, or 'null' if it was not found.  If it was found, the 
    *         String returned will contain the following value or the next
    *         command (to be diregarded such as in cases when only existence
    *         of flag is to be checked).
    **/
    public static String getCommandlineChoice(String mainArgs[], String flag, String synonymFlag, boolean valueRequired, String correctUsage) throws IllegalArgumentException{
        String value = null;
        
        // Validate Input
        if(flag==null)                  throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'flag' is 'null'.  Specify a valid string starting with '-'.");        
        if(flag.trim().length()==0)     throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'flag' may not be an empty string.  Specify a valid flag starting with '-'.");
        if(flag.startsWith("-")==false) throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'flag' must start with '-'.  Flag found to be '"+flag+"'.  Examples:  -UserId   or   -"+flag);
        if(flag.trim().length()==1)     throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'flag' only contains a '-'.  There must be a following name after the '-'.  Example:  -userId");
        if(synonymFlag!=null){
            if(synonymFlag.trim().length()==0)     throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'synonymFlag' may not be an empty string.  While a synonym is optional, such as '-u' for flag '-userId', if one is to be specified, it must be non-null.  Specify a valid flag starting with '-'.  To specify that there is no synonym for flag '"+flag+"', specifying 'null'.");
            if(synonymFlag.startsWith("-")==false) throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'synonymFlag' must start with '-' if any value is specified.  Synonym flag found to be '"+synonymFlag+"'.  Examples:  -UserId   or   -"+synonymFlag);
            if(synonymFlag.trim().length()==1)     throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'synonymFlag' only contains a '-'.  If any synonym is specified, there must be a following name after the '-'.  Example:  -userId.  Specify a valid flag starting with '-' or use 'null' to indicate that there is no synonym for flag '"+flag+"'.");
        }
        if(valueRequired){
            if(correctUsage==null)              throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'correctUsage' must be specified if 'valueRequired' is true.  Correct Usage is set to 'null'.");   
            if(correctUsage.trim().length()==0) throw new IllegalArgumentException("Internal Tool Error: Invalid input to command line parser 'getCommandlineChoice'.  'correctUsage' must be specified if 'valueRequired' is true.  Correct Usage is set to an empty string."); 
        }
        
        // Combine the two flags into one description for easier addition to error messages.
        StringBuffer flagInfo = new StringBuffer(flag); // Start with the required flag.
        if(synonymFlag!=null){
            flagInfo.append('/');
            flagInfo.append(synonymFlag);
        }
        
        // Loop through all arguments in main, processing input.
        for(int i=0; i < mainArgs.length; i++){
            if(                              (mainArgs[i].compareToIgnoreCase(flag)==0)
                || ((synonymFlag != null) && (mainArgs[i].compareToIgnoreCase(synonymFlag)==0)) // Only check synonym flags if one is specified.
              ){
                // If it was already specified, throw an exception
                if(value!=null){ // throw different exceptions depending on whether or not this is the last argument.
                    if((i+1)==mainArgs.length) throw new IllegalArgumentException("Command line option '"+flagInfo+"' may only be specified once.  Already accepted value '"+value+"', but also found flag specified at end of option list with no following value.");
                    else                       throw new IllegalArgumentException("Command line option '"+flagInfo+"' may only be specified once.  Already accepted value '"+value+"', but also found '"+mainArgs[i+1]+"'.");
                }
                // If at end of args, there is no following value.
                if((i+1)==mainArgs.length)
                    value = "";
                else // otherwise we found a value
                    value = mainArgs[++i];
            }
        }//end for

        //Validate if required
        if(valueRequired){
            if(value == null)     throw new IllegalArgumentException("Command line option '"+flagInfo.toString()+"' is required and was not specified.  You must specify option \""+correctUsage+"\".");   
            if(value.length()==0) throw new IllegalArgumentException("Command line option '"+flagInfo.toString()+"' is missing a required value.  You must specify a value for option \""+correctUsage+"\".");   
        }

        return(value);
    }//end getCommandlineChoice

   /** 
    *  Get the string name for the platform identifier.
    *  @param platform - Platform Identifier
    *  @return String name corresponding to the platform.
    **/
    private static String platform2String(int platform){
        StringBuffer str = new StringBuffer();
        
        switch(platform){
            case DKConstantICM.RM_PLATFORM_WIN:
                str.append("WIN");
                break;
            case DKConstantICM.RM_PLATFORM_AIX:
                str.append("AIX");
                break;
            case DKConstantICM.RM_PLATFORM_SUN:
                str.append("SUN");
                break;
            case DKConstantICM.RM_PLATFORM_390:
                str.append("390");
                break;
            case DKConstantICM.RM_PLATFORM_LINUX:
                str.append("LINUX");
                break;
            case DKConstantICM.RM_PLATFORM_ISERIES:
                str.append("ISERIES");
                break;
            case DKConstantICM.RM_PLATFORM_HP:
                str.append("HP");
                break;
            default:
                str.append("Unknown");
                break;
        
        }//end switch(dbType)
        str.append(" (");
        str.append(platform);
        str.append(')');
        return(str.toString());
    }//end platform2String()

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

   /**
    * Validate that the specified file exists.  Throws an error if it does not exist.
    * @param fileName     - File Name of the file to check if it exists.
    * @param correctUsage - Correct usage information explaining what file was expected.
    **/
    public static void validateFileExists(String fileName, String correctUsage) throws IllegalArgumentException, FileNotFoundException{
        
        // Determine if a file name was specified.
        if(fileName==null)                  throw new IllegalArgumentException("Internal Method Usage Error.  No 'fileName' was specified for method 'validateFileExists(String fileName,String correctUsage)'.  Parameter value was 'null'");
        if(fileName.trim().length()<=0)     throw new IllegalArgumentException("Internal Method Usage Error.  No 'fileName' was specified for method 'validateFileExists(String fileName,String correctUsage)'.  Parameter value was an empty string (\""+fileName+"\").");
        if(correctUsage==null)              throw new IllegalArgumentException("Internal Method Usage Error.  No 'correctUsage' was specified for method 'validateFileExists(String fileName,String correctUsage)'.  Parameter value was 'null'");
        if(correctUsage.trim().length()<=0) throw new IllegalArgumentException("Internal Method Usage Error.  No 'correctUsage' was specified for method 'validateFileExists(String fileName,String correctUsage)'.  Parameter value was an empty string (\""+correctUsage+"\").");

        try{
            File file = new File(fileName.trim());
            if(file.exists()==false)
                throw new FileNotFoundException("File \""+fileName+"\" not found.  Ensure that the file exists and is in the proper directory.  If no absolute path is specified, the file exists relative to the working directory thta invoked this tool.  Correct Usage:  "+correctUsage);
        }catch(java.io.FileNotFoundException exc){
            throw new FileNotFoundException("File \""+fileName+"\" not found.  Ensure that the file exists and is in the proper directory.  If no absolute path is specified, the file exists relative to the working directory thta invoked this tool.  Correct Usage:  "+correctUsage);
        }
    }//end validateFileExits()

    //=================================================================
    // Internal Classes
    //=================================================================
    
   
}//end class TExportManagerICM

/************************************************************************************************
 *             CLASS: TExportManagerICM_MissingItem
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Object responsible for tracking information about a single missing item
 *                    item that was marked as missing by the Reconcile Summary File.
 ************************************************************************************************/
class TExportManagerICM_MissingItem{
    
    // Constants

    // Variables
    String _pidStr  = null;  // PID String of the item that was highlighed as missing in the Reconcile Summary File.
    int    _lineNum = -1;    // Line number of the location of the PID string in the Reconcile Summary File that is the source of this PID String. 

   /**
    * Preventing creation with no arguments.
    **/
    private TExportManagerICM_MissingItem() throws Exception{
        throw new Exception("The TExportManagerICM_MissingItem object cannot be created by the no-argument constructor.");
    }//end CTOR()

   /**
    * Create an instance of the Missing Item
    * @param pidString - PID String of the missing item as denoted by the Missing Item entry in the Reconcile Summary File.
    **/
    public TExportManagerICM_MissingItem(String pidString){
        // Validate Input
        if(pidString==null)              throw new InternalError("Internal Error:  Invalid parameter to MissingItem constuctor.  'pidString' is null.");
        if(pidString.trim().length()==0) throw new InternalError("Internal Error:  Invalid parameter to MissingItem constuctor.  'pidString' is an empty string ('"+pidString+"').");

        // Save input
        _pidStr = pidString;
    }//end CTOR(args)

   /**
    * Gets the key for use with this object in sorted TreeMap lists.
    * @return Returns the key to find this object among sorted lists.
    **/
    public String getKey(){
        return(_pidStr.toUpperCase());
    }//end getKey()

   /**
    * If you do not have the instance of the MissingItem object,
    * use this method to construct a key.  It Gets the key for getting
    * the corresponding object in sorted TreeMap lists.
    * @param pidString - Pid String of the item.
    * @return Returns the key to find this object among sorted lists.
    **/
    public static String getKey(String pidString){
        return(pidString.toUpperCase());
    }//end getKey(String pidString)

   /** 
    * Create a new DKDDO object using the specified datastore.
    * @param dsICM - Connected DKDatastoreICM object.
    * @return Returns a new DKDDO object for the Missing Item.
    **/
    public DKDDO getDDO(DKDatastoreICM dsICM) throws InternalError, Exception{
        // Validate Input
        if(dsICM==null) throw new InternalError("Internal Error:  Invalid parameter to MissingItem.getDDO(DKDatastoreICM dsICM).  Parameter 'dsICM' is 'null'.");
        if(dsICM.isConnected()==false) throw new InternalError("Internal Error:  Invalid parameter to MissingItem.getDDO(DKDatastoreICM dsICM).  Parameter 'dsICM' was never connected.");
        
        // Create DKDDO object
        DKDDO ddo = null;
        try{
            ddo = dsICM.createDDOFromPID(_pidStr);
        }catch(Exception exc){
            throw new Exception("Could not recreate DKDDO object for missing item Pid '"+_pidStr+"' due to error \""+exc.getMessage()+"\".  If the item no longer exists or cannot be accessed anymore, re-run the Reconcile tool for get a current list of available PIDs.");
        }
        
        return(ddo);        
    }//end getDDO()

   /**
    * If you do not have the instance of the MissingItem object,
    * use this method to construct a key.  It Gets the key for getting
    * the corresponding object in sorted TreeMap lists.
    * @param ddo - DDO for the item that is the missing item.
    * @return Returns the key to find this object among sorted lists.
    **/
    public static String getKey(DKDDO ddo ){
        return(ddo.getPidObject().pidString());
    }//end getKey(DKDDO ddo)

   /**
    * Writes a one-line description of the missing item.
    * @return Returns a one-line description of this missing item.
    **/
    public String toString(){
        StringBuffer str = new StringBuffer();
        str.append("Missing Item:  ");
        str.append(_pidStr);
        return(str.toString());
    }//end toString()

   /**
    * Validate the settings of this object.
    **/
    private void validateSettings(){
        // Validate Input
        if(_pidStr==null)  throw new InternalError("Internal Error Creating MissingItem:  Variable '_pidStr' is 'null'.");
    }//end validateSettings()
   
}//end class TExportManagerICM_MissingItem
              
              
