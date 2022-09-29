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

 TExportPackageICM
     See TExportPackageICM.doc and TExportPackageICM.html.
     
 ******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import com.ibm.mm.sdk.xml.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/************************************************************************************************
 *          FILENAME: TExportPackageICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Import / Export Package Tool and API.  The Export Package object
 *                    provides the entire API set and options to export and import items
 *                    from one datastore to another.  The object may work with both source
 *                    and target systems in the same session or may be saved persistently,
 *                    copied to a target system around a firewall, loaded, and then
 *                    imported.
 *
 *                    Document:  For complete information please refer to the
 *                               Sample Import / Export Tool & API document
 *                               --> TExportPackageICM.doc
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: Not Executable
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: No prerequisit tool operations or commands, but requirements and
 *                    limitations apply to the overall Sample Import / Export Tool API.
 *  
 *                    Please refer to the Sample Import / Export Tool & API document,
 *                    TExportPackageICM.doc, for Requirements & Limitations.
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 *                    SLinksICM.java
 ************************************************************************************************/
/**
 * <B> Import / Export Package Tool and API                </B><BR>
 * <I> for IBM DB2 Content Manager&REG V8.3                </I><BR><BR>
 *
 * The Export Package object provides the entire API set and options to export and
 * import items from one datastore to another.  The object may work with both source
 * and target systems in the same session or may be saved persistently, copied to a
 * target system around a firewall, loaded, and then imported.                       <BR><BR>
 *
 * For more information, please refer to the <A href="TExportPackageICM.doc">Design Document</A>.
 **/
public class TExportPackageICM{

    // Documented Constants
    public  static final String T_ICM_EXPORT_PACKAGE_FILE_EXT                   = ".xpk";
    public  static final String T_ICM_EXPORT_PACKAGE_VERSION                    = "4.07.22";
    public  static final String T_ICM_EXPORT_PACKAGE_FILE_IDENTIFIER            = "<ICM Export Package>";
    private static final String T_ICM_EXPORT_PACKAGE_SELECTED_ITEMS_TAG_BEGIN   = "ITEMS SELECTED DIRECTLY:";
    private static final String T_ICM_EXPORT_PACKAGE_SELECTED_ITEMS_TAG_END     = "-- end --";
    private static final String T_ICM_EXPORT_PACKAGE_REFERENCED_ITEMS_TAG_BEGIN = "ITEMS REFERENCED INDIRECTLY:";
    private static final String T_ICM_EXPORT_PACKAGE_REFERENCED_ITEMS_TAG_END   = "-- end --";
    private static final String T_ICM_EXPORT_PACKAGE_FOLDER_INFO_TAG_BEGIN      = "FOLDER INFORMATION:";
    private static final String T_ICM_EXPORT_PACKAGE_FOLDER_INFO_TAG_END        = "-- end --";
    private static final String T_ICM_EXPORT_PACKAGE_LINK_INFO_TAG_BEGIN        = "LINK INFORMATION:";
    private static final String T_ICM_EXPORT_PACKAGE_LINK_INFO_TAG_END          = "-- end --";
    private static final String T_ICM_EXPORT_PACKAGE_REFATTR_INFO_TAG_BEGIN     = "REFERNCE ATTRIBUTE INFORMATION:";
    private static final String T_ICM_EXPORT_PACKAGE_REFATTR_INFO_TAG_END       = "-- end --";
    public  static final String T_ICM_EXPORT_PACKAGE_TRACKING_FILE_IDENTIFIER   = "<ICM Export Package Tracking File>";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_PASS1_TAG_BEGIN   = "PASS [1 OF 3]:  IMPORT CORE ITEM VERSIONS...";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_PASS1_TAG_END     = "-- end --";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_PASS2_TAG_BEGIN   = "PASS [2 OF 3]:  APPLY FOLDER CONTENTS, LINKS, & REFERENCE ATTRIBUTES...";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_PASS2_TAG_END     = "-- end --";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_PASS3_TAG_BEGIN   = "PASS [3 OF 3]:  CHECK ALL ITEMS IN...";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_PASS3_TAG_END     = "-- end --";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_NONE    = "          ";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_STARTING= "STARTING: ";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE= "COMPLETE: ";
    private static final String T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_OMITTING= "OMITTING: ";
    private static final int    T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_TAG_LEN = 10;

    // 
    // Option Constants For ImportOptions, ExportOptions, and Options Outer Classes.
    // 
    public static final int     OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS     = 101;
    public static final int     OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS      = 102;
    public static final int     OPTION_VERSION_GAP_HANDLING_PROMPT              = 103;
    public static final int     OPTION_FILE_VERSION_WARNING_EXCEPTION           = 201;
    public static final int     OPTION_FILE_VERSION_WARNING_IGNORE              = 202;
    public static final int     OPTION_FILE_VERSION_WARNING_PROMPT              = 203;
    public static final int     OPTION_CONFLICTS_ALWAYS_NEW                     = 301;
    public static final int     OPTION_CONFLICTS_UNIQUE_THEN_NEW                = 302;
    public static final int     OPTION_CONFLICTS_UNIQUE_THEN_ERROR              = 303;
    public static final int     OPTION_CONFLICTS_UNIQUE_THEN_SKIP               = 304;
    public static final int     OPTION_CONFLICTS_UNIQUE_THEN_PROMPT             = 305;
    public static final int     OPTION_UNIQUENESS_DETECT_THEN_ERROR             = 401;
    public static final int     OPTION_UNIQUENESS_DETECT_THEN_PROMPT            = 402;
    public static final int     OPTION_UNIQUENESS_USER_SPECIFIED                = 403;
    public static final int     OPTION_UNIQUENESS_PROMPT                        = 404;
    public static final int     OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION    = 501;
    public static final int     OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS        = 502;
    public static final int     OPTION_SELECTED_ITEM_PROMPT_IF_VERSIONS         = 503;
    public static final int     OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION     = 601;
    public static final int     OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS       = 602;
    public static final int     OPTION_FOLDER_CONTENT_REMOVE_VALUE              = 603;
    public static final int     OPTION_FOLDER_CONTENT_PROMPT_IF_VERSIONS        = 604;
    public static final int     OPTION_FOLDER_CONTENT_PROMPT_ALWAYS             = 605;
    public static final int     OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION       = 701;
    public static final int     OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS         = 702;
    public static final int     OPTION_LINKED_ITEMS_REMOVE_LINK                 = 703;
    public static final int     OPTION_LINKED_ITEMS_PROMPT_IF_VERSIONS          = 704;
    public static final int     OPTION_LINKED_ITEMS_PROMPT_ALWAYS               = 705;
    public static final int     OPTION_REFATTR_VALUE_EXPORT_REFERENCED_VERSION  = 801;
    public static final int     OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS        = 802;
    public static final int     OPTION_REFATTR_VALUE_REMOVE_VALUE               = 803;
    public static final int     OPTION_REFATTR_VALUE_PROMPT_IF_VERSIONS         = 804;
    public static final int     OPTION_REFATTR_VALUE_PROMPT_ALWAYS              = 805;
    public static final boolean OPTION_PRINT_TRACE_DEFAULT                      = false;
    public static final boolean OPTION_PRINT_DEBUG_DEFAULT                      = false;
    public static final String  OPTION_TRACKING_FILE_EXT                        = ".trk";
    public static final String  OPTION_TRACKING_FILE_DEFAULT                    = "TExportPackageICM"+OPTION_TRACKING_FILE_EXT;
    // Private Constants
    protected static final int     NO                                           = 0;
    protected static final int     YES                                          = 1;
    protected static final int     UNKNOWN                                      = 2;
    private   static final boolean CONVERT_ALL_NULL_STRINGS_TO_EMPTY_STRING     = false;  // If true, the fixRequiredStringAttributes method will replace all string attributes that have a null value when imported to an empty string ("").  Otherwise, only those that are defined as required will be converted.  For more information, refer to fixRequiredStringAttributes().

    // Private Variables
    private TreeMap<String,ItemInfoPack>      _itemInfoTree           = null; // Sorted, Searchable, Tree of Items.  Holds ItemInfoPack    objects
    private Hashtable<String,FolderInfoPack>  _folderInfoHT           = null; //                                     Holds FolderInfoPack  objects
    private Hashtable<String,LinkInfoPack>    _linksInfoHT            = null; //                                     Holds LinkInfoPack    objects
    private Hashtable<String,RefAttrInfoPack> _refAttrInfoHT          = null; //                                     Holds RefAttrInfoPack objects
    private TreeMap<String,ItemInfoPack>      _preRegisterTree        = null; // Sorted, Searchable, Tree of Items.
    private TreeMap<String,CompInfoPack>      _verComponentMap        = null; // Holds Versioned Component PID Mapping.  Holds VerCompInfoPack objects
    private TreeMap<String,ItemInfoPack>      _restartP1CmpltItemMap  = null; // Restart Capability: Pass1 - Items known to have completed.
    private TreeMap<String,ItemInfoPack>      _restartP1OmitItemMap   = null; // Restart Capability: Pass1 - Items that should be omitted in the current restart process.
    private TreeMap<String,ItemInfoPack>      _restartP2CmpltItemMap  = null; // Restart Capability: Pass2 - Items known to have completed.
    private TreeMap<String,ItemInfoPack>      _restartP2OmitItemMap   = null; // Restart Capability: Pass2 - Items that should be omitted in the current restart process.
    private TreeMap<String,ItemInfoPack>      _restartP3CmpltItemMap  = null; // Restart Capability: Pass3 - Items known to have completed.
    private TreeMap<String,ItemInfoPack>      _restartP3OmitItemMap   = null; // Restart Capability: Pass3 - Items that should be omitted in the current restart process.
    
   /**
    * Create an Export Package, loading with initial data set to be
    * obtained by the specified query on the specified datastore.
    * Add items specified in the query string and all cascading referenced
    * items found in the reference attributes, links, and folder contents.          <BR><BR>
    * @param dsICM         - Connected datastore to export from.
    * @param queryStr      - XQPe query string selecting Items to export.
    * @param exportoptions - (Optional) TExportPackageICM.ExportOptions object, 'null' for defaults.
    *                        Please refer to the ExportOptions constructor information on
    *                        default settings.
    **/
    public TExportPackageICM(DKDatastoreICM dsICM, String queryStr,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{

        if(exportOptions==null) // If not specified, create defaults.
            exportOptions = new TExportPackageICM.ExportOptions();
            
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM(dsICM,query="+queryStr+","+obj2String(exportOptions)+")");
        verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.

        // Step 1:  Retrieve Items Directly Specified for Export
        dkCollection results = findItems(dsICM,queryStr,exportOptions);
        
        init(results.cardinality()*2,exportOptions);

        // Add Selected Items to Export Package.
        addItems(results,exportOptions);

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM(dsICM,queryStr,exportOptions)");
    }

   /**
    * Create an Export Package, loading with dataset that was exported to the
    * specified Export Package file.  Additional sets may be loaded afterwards.
    * Duplicate information will be consolidated.                                   <BR><BR>
    * @param centralFileName - Central Export Package file responsible an entire
    *                          saved package state and all files needed.
    * @param importOptions   - (Optional) Import Options specified in a TExportPackageICM.ImportOptions
    *                          object.  If 'null' specified, defaults will be used.
    *                          Please refer to the ImportOptions constructor information on
    *                          default settings.
    **/
    public TExportPackageICM(String centralFileName,TExportPackageICM.ImportOptions importOptions) throws DKException, Exception{
        if(importOptions==null)
            importOptions = new TExportPackageICM.ImportOptions();
        if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM(centralFileName="+centralFileName+","+obj2String(importOptions)+")");

        // Initialize with size as the number of lines in the file.
        init(getNumLinesInFile(centralFileName,importOptions),importOptions);
        // Load Data Set.
        read(centralFileName,importOptions);

        if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM(centralFileName="+centralFileName+","+obj2String(importOptions)+")");
    }
   /**
    * Create an Export Package with the specified initial hash table size.          <BR><BR>
    * @param numItemsExpected - An estimate of the number of items expected.  It is
    *                           recommended to select the number of items directly selected
    *                           for export.  This will determine the initial hashtable size.
    *                           Selecting a number too small will decrease performance and
    *                           selecting a number too large will waste memory.
    * @param options          - (Optional) Options specified in a TExportPackageICM.Option object, 'null' for defaults.
    *                           Please refer to the Options constructor information on
    *                           default settings.
    **/
    public TExportPackageICM(int numItemsExpected, TExportPackageICM.Options options){
        if(options==null)
            options = new TExportPackageICM.ExportOptions();
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM(numItemsExpected="+numItemsExpected+","+obj2String(options)+")");
        init(numItemsExpected,options);
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM(numItemsExpected="+numItemsExpected+","+obj2String(options)+")");
    }

   /**
    * Initialize an Export Package.                                                 <BR><BR>
    * @param numItemsExpected - An estimate of the number of items expected.  It is
    *                           recommended to select the number of items directly selected
    *                           for export.  This will determine the initial hashtable size.
    *                           Selecting a number too small will decrease performance and
    *                           selecting a number too large will waste memory.
    * @param options         - (Required) TExportPackageICM.Option object.  Null not accepted in private methods.
    **/
    private void init(int numItemsExpected, TExportPackageICM.Options options){
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.init(numItemsExpected="+numItemsExpected+","+obj2String(options)+")");
       
        _itemInfoTree          = new TreeMap<String,ItemInfoPack>();
        _folderInfoHT          = new Hashtable<String,FolderInfoPack>(numItemsExpected);
        _linksInfoHT           = new Hashtable<String,LinkInfoPack>(numItemsExpected);
        _refAttrInfoHT         = new Hashtable<String,RefAttrInfoPack>(numItemsExpected);
        _preRegisterTree       = new TreeMap<String,ItemInfoPack>();
        _verComponentMap       = new TreeMap<String,CompInfoPack>();
        _restartP1CmpltItemMap = new TreeMap<String,ItemInfoPack>();
        _restartP1OmitItemMap  = new TreeMap<String,ItemInfoPack>();
        _restartP2CmpltItemMap = new TreeMap<String,ItemInfoPack>();
        _restartP2OmitItemMap  = new TreeMap<String,ItemInfoPack>();
        _restartP3CmpltItemMap = new TreeMap<String,ItemInfoPack>();
        _restartP3OmitItemMap  = new TreeMap<String,ItemInfoPack>();
        
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.init(numItemsExpected="+numItemsExpected+","+obj2String(options)+")");
    }
    
   /**
    * Add Item to Export Package.  Only Root DDOs accepted.                         <BR><BR>
    * @param ddo           - Root DDO for item.
    * @param exportOptions - (Optional) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Use 'null' for defaults.
    *                        Please refer to the ExportOptions constructor information on
    *                        default settings.
    **/
    public void addItem(DKDDO ddo,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions==null) // If no options specified, use defaults.
            exportOptions = new TExportPackageICM.ExportOptions();
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.addItem("+obj2String(ddo)+","+obj2String(exportOptions)+")");

        // Ensure that it is a Root DDO.
        if(isRoot(ddo,exportOptions)==false)
            throw new Exception("Child components cannot be selected for export.  Only entire items can be exported or imported.  Therefore only root DDOs may be selected.  Child component found: ITEMID = '"+((DKPidICM)ddo.getPidObject()).getItemId()+"', COMPID = '"+((DKPidICM)ddo.getPidObject()).getComponentId()+"', Object Type = '"+((DKPidICM)ddo.getPidObject()).getObjectType()+"'.");
        // Ensure that it is not a Doc Routing Item.
        if(isDocRoutingItem(ddo,exportOptions)==true)
            throw new Exception("Document routing items cannot be selected for export.  Document Routing items are tied to other items (processes, work lists, etc.) and definitions different than that of the items of user-defined item types.  Item found: ITEMID = '"+((DKPidICM)ddo.getPidObject()).getItemId()+"', COMPID = '"+((DKPidICM)ddo.getPidObject()).getComponentId()+"', Object Type = '"+((DKPidICM)ddo.getPidObject()).getObjectType()+"'.");
        // Ensure that it is not a Doc Model Part.
        if(isDocModelPart(ddo,exportOptions)==true)
            throw new Exception("An individual document part cannot be exported.  Only entire items can be exported or imported.  DDO found: ITEMID = '"+((DKPidICM)ddo.getPidObject()).getItemId()+"', COMPID = '"+((DKPidICM)ddo.getPidObject()).getComponentId()+"', Object Type = '"+((DKPidICM)ddo.getPidObject()).getObjectType()+"'.");
        // Validate tha the DDO used a valid item type for exporting
        validateItemType(ddo);

        // Find and Add all Cascading Referenced Items
        addItemAndAllReferencedItems(ddo,exportOptions);
        
        // Mark item as directly selected for reporting information.
        ItemInfoPack itemInfoPack = new ItemInfoPack(true,ddo);                // Create object to get key.
        itemInfoPack = _itemInfoTree.get(itemInfoPack.getKey()); // Get actual item info pack with key.
        itemInfoPack.setSelectedDirectly(true);

        // Add all the versions of the selected item if necessary.
        addAllVersionsIfNecessary(ddo,exportOptions);

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.addItem("+obj2String(ddo)+","+obj2String(exportOptions)+")");
    }

   /**
    * Add all the versions of the selected item if necessary.
    * Add other versions depending on options and if not registered
    * as selected by the addItems() function.  We only need to deal
    * with versioned items, if the exportOptions specify accordingly,
    * and if not directly selected by the user.                                     <BR><BR>
    * @param ddo           - Root DDO for item already packaged for export (from the 
    *                        addItem() function.
    * @param exportOptions - (Required) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Null not accepted in private methods.
    **/
    private void addAllVersionsIfNecessary(DKDDO ddo,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.addAllVersionsIfNecessary("+obj2String(ddo)+","+obj2String(exportOptions)+")");

        // Proceed with check only if it is versioned.
        if(isVersioned(ddo,exportOptions)){ 
            if(exportOptions.getPrintDebugEnable()) System.out.println("   ItemType does have versioning enabled.");
            // Get all unselected Versions
            dkCollection unselectedVersions = findUnselectedVersions(ddo,exportOptions);
            if(exportOptions.getPrintDebugEnable()) System.out.println("   '"+unselectedVersions.cardinality()+"' Unselected Versions Found.");
            // If there are any unselected Versions,
            if(unselectedVersions.cardinality() > 0){
                // Package Up Version List
                String verListStr = getVersionListStr(unselectedVersions);
                // If options specify accordingly,
                if(exportOptions.getAnswer_isSelectedItemPolicy_exportAll(verListStr)){
                    if(exportOptions.getPrintDebugEnable()) System.out.println("   Export Option Chosen: Export All.");
                    // For all versions not selected, add them.
                    dkIterator iter = unselectedVersions.createIterator();
                    while(iter.more()){
                        DKDDO unselectedDDO = (DKDDO) iter.next();
                        // Find and Add all Cascading Referenced Items
                        addItemAndAllReferencedItems(unselectedDDO,exportOptions);
                    }
                } else
                    if(exportOptions.getPrintDebugEnable()) System.out.println("   Export Option Chosen: Do Not Export All.");
                
            }
        } else
            if(exportOptions.getPrintDebugEnable()) System.out.println("   ItemType does NOT have versioning enabled.");

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.addAllVersionsIfNecessary("+obj2String(ddo)+","+obj2String(exportOptions)+")");
    }
    
   /**
    * Add items specified in the query string and all cascading
    * referenced items found in the reference attributes, links, and folder contents.   <BR><BR>
    * @param dsICM         - Connected datastore to export from.
    * @param queryStr      - XQPe query string selecting Items to export.
    * @param exportOptions - (Optional) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Use 'null' for defaults.
    *                        Please refer to the ExportOptions constructor information on
    *                        default settings.
    **/
    public void addItems(DKDatastoreICM dsICM, String queryStr,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions==null) // If no options specified, use defaults.
            exportOptions = new TExportPackageICM.ExportOptions();
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.addItems("+obj2String(dsICM)+",queryStr="+queryStr+","+obj2String(exportOptions)+")");
        verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.

        // Step 1:  Retrieve Items Directly Specified for Export
        dkCollection results = findItems(dsICM,queryStr,exportOptions);
        
        // Add Selected Items to Export Package.
        addItems(results,exportOptions);

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.addItems("+obj2String(dsICM)+",queryStr="+queryStr+","+obj2String(exportOptions)+")");
    }

   /**
    * Add items in the collection and all cascading
    * referenced items found in the reference attributes, links, and folder contents.   <BR><BR>
    * @param ddoColl       - dkCollection or DKResults collection containing Items.
    * @param exportOptions - (Optional) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Use 'null' for defaults.
    *                        Please refer to the ExportOptions constructor information on
    *                        default settings.
    **/
    public synchronized void addItems(dkCollection ddoColl,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        // SYNCHRONIZED:  Method synchronized around modification of the object's
        //                preRegisterTree, only modified by this function.  Other functions
        //                may read, but not write to this tree.
        if(exportOptions==null) // If no options specified, use defaults.
            exportOptions = new TExportPackageICM.ExportOptions();
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.addItems("+obj2String(ddoColl)+","+obj2String(exportOptions)+")");

        // Make sure that the pre-register selected tree is clear.  It may not be clear
        // if an error was encountered in this function.
        clearRegister(exportOptions);

        // Register All Selected, So addItem() can tell whether it needs to
        // import versions not selected.  Otherwise it will think that some
        // are not selected yet, just because they have not been processed yet.
        dkIterator iter = ddoColl.createIterator();
        while(iter.more())
            registerSelected((DKDDO) iter.next(),exportOptions);

        // Add Selected Items to Export Package.
        iter = ddoColl.createIterator();
        while(iter.more())
            addItem((DKDDO) iter.next(),exportOptions);

        // Clear the pre-register selected tree.
        clearRegister(exportOptions);

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.addItems("+obj2String(ddoColl)+","+obj2String(exportOptions)+")");
    }

   /**
    * Add the specified item if it has not been already.  If not already added,
    * Find all referenced Items cascading from the specified item.  Find
    * recursively all items pointed to by reference attributes, in folder
    * contents, or in links related to this item.                                   <BR><BR>
    * @param ddo           - Root DDO for item.
    * @param exportOptions - (Required) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Null not accepted in private methods.
    **/
    private void addItemAndAllReferencedItems(DKDDO ddo,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.addItemAndAllReferencedItems("+obj2String(ddo)+","+obj2String(exportOptions)+")");

        // If already added, we have reached end of recursive calls.
        if(isPackaged(ddo,exportOptions)==false){
            // First, Add This Item to Hashtable
            addItemToHashtable(false, ddo, exportOptions);       // Mark as indirectly referenced.

            // Retrieve All Meta Data, But Not Resource Content Yet to Maintaing Memory Usage Scaling
            if(exportOptions.isRetrieveDenied()==false){  // Did the caller deny the tool the ability to retrieve?  If not, assume responsibility.
                DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance((DKDatastoreICM)ddo.getDatastore());
            	dkRetrieveOptions.baseAttributes(true);
            	dkRetrieveOptions.basePropertyAclName(true);
            	dkRetrieveOptions.childListOneLevel(true);
            	dkRetrieveOptions.childListAllLevels(true);
            	dkRetrieveOptions.childAttributes(true);
            	dkRetrieveOptions.linksInbound(true);
            	dkRetrieveOptions.linksOutbound(true);
            	dkRetrieveOptions.linksDescriptors(true);
            	dkRetrieveOptions.partsList(true);
            	dkRetrieveOptions.partsAttributes(true);
            	dkRetrieveOptions.partsPropertyAclName(true);
            	dkRetrieveOptions.resourceContent(false);       // But No Content At This Time Yet
                ddo.retrieve(dkRetrieveOptions.dkNVPair());
            }

            // Second, handle folder contents.
            findAndAddAllFolderContents(ddo,exportOptions);
                        
            // Third, handle links.
            findAndAddAllLinkedItems(ddo,exportOptions);
            
            // Fouth, handle any reference attributes.
            findAndAddAllReferenceAttrItems(ddo,exportOptions);

            // Fifth, handle parts.
            findAndNoteDocumentModelParts(ddo,exportOptions);
            
        }//end if not yet packaged.

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.addItemAndAllReferencedItems("+obj2String(ddo)+","+obj2String(exportOptions)+")");
    }

   /**
    * Add Item to Hashtable.                                                        <BR><BR>
    * @param selectedDirectly - Optional report statistic that determines if it was directly selected by user or if indirectly referenced.
    * @param ddo              - Root DDO for item.
    * @param options          - (Required) Options specified in a TExportPackageICM.Options
    *                           object.  Null not accepted in private methods.
    **/
    private void addItemToHashtable(boolean selectedDirectly, DKDDO ddo,TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.addItemToHashtable("+obj2String(selectedDirectly)+","+obj2String(ddo)+","+obj2String(options)+")");

        // Make sure it isn't already added
        if(isPackaged(ddo,options))
            throw new Exception("Item already added to package!  Algorithm should only add each item once.");
        
        // Add ItemInfo Package
        ItemInfoPack itemInfoPack = new ItemInfoPack(selectedDirectly,ddo);
        _itemInfoTree.put(itemInfoPack.getKey(),itemInfoPack);

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.addItemToHashtable("+obj2String(selectedDirectly)+","+obj2String(ddo)+","+obj2String(options)+")");
    }
   /**
    * Detect unique criteria.  The goal is to auto-detect one attribute on the root
    * level that distinguishes an item in the target system as unique to be compared
    * against the items to be imported.  A single attribute is needed, but if the
    * tool is unable to narrow down the search results to one, then the Uniqueness
    * Detection Policy will handle.  One of the following will be returned in order
    * of preference: <BR>
    *  <OL>
    *       <LI> One Unique, Required Attribute.                              </LI>
    *       <LI> One Unique, Nullable Attribute.                              </LI>
    *       <LI> Combined List of Multiple Unique, Required Attributes Followed by Unique, Nullable. </LI>
    *       <LI> No Attributes.                                               </LI>
    * </OL>                                                                         <BR><BR>
    * @param dsICM       - Connected Target Datstore
    * @param viewTypeStr - Item Type Name or Item Type View Name.  Component Types or Component View Types not accepted.
    * @param options     - (Required) Options specified in a TExportPackageICM.Options
    *                      object.  Null not accepted in private methods.
    * @return Returns an array of Strings containing attribute name(s) according to the
    *         criteria listed.
    **/
    private static String[] detectUniqueCriteria(DKDatastoreICM dsICM, String viewTypeStr, TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.detectUniqueCriteria("+obj2String(dsICM)+","+viewTypeStr+","+obj2String(options)+")");

        DKSequentialCollection uniqueRequiredAttrColl = new DKSequentialCollection();
        DKSequentialCollection uniqueNullableAttrColl = new DKSequentialCollection();
        String[] retval    = null;
        String   retvalStr = "";   // For debug output.

        // Retrieve the Item Type Definition
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
        DKItemTypeViewDefICM itemTypeViewDef = dsDefICM.retrieveItemTypeView(viewTypeStr);
        if(itemTypeViewDef==null)
            throw new Exception("Either the Item Type or Item Type View '"+viewTypeStr+"' is not defined on the target system or the user does not have the privileges required to access that type.");
        
        // Go through user attributes on root level.
        dkCollection attrs = itemTypeViewDef.listAttrs();
        dkIterator iter = attrs.createIterator();
        while(iter.more()){
            DKAttrDefICM attrDef = (DKAttrDefICM) iter.next();   
            if(attrDef.isUnique()){       // If unique, add to one of the two lists.
                if(attrDef.isNullable())  // If nullable, add to nullable list.
                    uniqueNullableAttrColl.addElement(attrDef);
                else                      // Otherwise it is required.
                    uniqueRequiredAttrColl.addElement(attrDef);
            }
        }
        // Go through all user attr groups on root level.
        dkCollection attrGroups = itemTypeViewDef.listAttrGroups();
        iter = attrGroups.createIterator();
        while(iter.more()){
            DKAttrGroupDefICM attrGroupDef = (DKAttrGroupDefICM) iter.next();
            attrs = attrGroupDef.listAttrs();
            dkIterator iter2 = attrs.createIterator();
            while(iter2.more()){
                DKAttrDefICM attrDef = (DKAttrDefICM) iter2.next();   
                if(attrDef.isUnique()){       // If unique, add to one of the two lists.
                    if(attrDef.isNullable())  // If nullable, add to nullable list.
                        uniqueNullableAttrColl.addElement(attrDef);
                    else                      // Otherwise it is required.
                        uniqueRequiredAttrColl.addElement(attrDef);
                }
            }
        }

        // Debug Information
        if(options.getPrintDebugEnable()){
            String listStr = "";
            iter = uniqueRequiredAttrColl.createIterator();
            while(iter.more()){
                   DKAttrDefICM attrDef = (DKAttrDefICM) iter.next();
                   listStr += (attrDef.getName() + ",");
            }
            System.out.println("   Identified '"+uniqueRequiredAttrColl.cardinality()+"' Unique Required Attrs:  "+listStr);
            iter = uniqueNullableAttrColl.createIterator();
            while(iter.more()){
                   DKAttrDefICM attrDef = (DKAttrDefICM) iter.next();
                   listStr += (attrDef.getName() + ",");
            }
            System.out.println("   Identified '"+uniqueNullableAttrColl.cardinality()+"' Unique Nullable Attrs:  "+listStr);
        }
        
        // If a single unique required attr exists, return it.
        if( (uniqueRequiredAttrColl.cardinality()==1) && (uniqueNullableAttrColl.cardinality()==0) ){
            if(options.getPrintDebugEnable()) System.out.println("   Detected that a single unique required attribute exists.");
            // Get single Attr.
            iter = uniqueRequiredAttrColl.createIterator();
            DKAttrDefICM attrDef = (DKAttrDefICM) iter.next();
            // Package up for return.
            retval = new String[1];
            retval[0] = attrDef.getName();
            retvalStr = attrDef.getName();
        } // Else if a single unique nullable attr exists, return it.
        else if( (uniqueRequiredAttrColl.cardinality()==0) && (uniqueNullableAttrColl.cardinality()==1) ){
            if(options.getPrintDebugEnable()) System.out.println("   Detected that a single unique nullable attribute exists.");
            // Get single Attr.
            iter = uniqueNullableAttrColl.createIterator();
            DKAttrDefICM attrDef = (DKAttrDefICM) iter.next();
            // Package up for return.
            retval = new String[1];
            retval[0] = attrDef.getName();
            retvalStr = attrDef.getName();
        } // Else combine the lists and return them.
        else{
            if(options.getPrintDebugEnable()) System.out.println("   A single attribute could not be chosen by the uniqueness detection algorithm.  Handing results back for Uniqueness Detection Policy to handle.");
            // Prepare return variable.
            int arraySize = uniqueRequiredAttrColl.cardinality() + uniqueNullableAttrColl.cardinality();
            retval = new String[arraySize];
            if(options.getPrintDebugEnable()) System.out.println("   Return Array Created of Size '"+arraySize+"'.");
            // Package Unique, Required Attrs (if any)
            int i = 0;
            iter = uniqueRequiredAttrColl.createIterator();
            while(iter.more()){
                DKAttrDefICM attrDef = (DKAttrDefICM) iter.next();
                retval[i]  = attrDef.getName();
                retvalStr += (attrDef.getName() + ',');
                i++;
            }
            // Package Unique, Nullable Attrs (if any)
            iter = uniqueNullableAttrColl.createIterator();
            while(iter.more()){
                DKAttrDefICM attrDef = (DKAttrDefICM) iter.next();
                retval[i] = attrDef.getName();
                i++;
                retvalStr += (attrDef.getName() + ',');
            }
        }
        
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.detectUniqueCriteria(dsICM,"+viewTypeStr+",options) return(String["+retval.length+"] = '"+retvalStr+"')");
        return(retval);
    }

   /**
    * Find all versions of the specified item that have not yet been selected for
    * export.                                                                       <BR><BR>
    * @param ddo     - DDO of a single version to use to look up all other versions
    *                  that have not yet been selected for export.  This object should
    *                  have already been checked to make sure that it is a root.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                   object.  Null not accepted in private methods.
    * @return Returns a dkCollection collection with the results, if any.  An empty
    *         collection will be returned if there are no other versions that have 
    *         not yet been selected for export.
    **/
    private dkCollection findUnselectedVersions(DKDDO ddo,TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.findUnselectedVersions("+obj2String(ddo)+","+obj2String(options)+")");
        DKSequentialCollection unselectedVersionsColl = new DKSequentialCollection();

        // Only check if versioning is not enabled for this item type
        DKDatastoreICM       dsICM        = (DKDatastoreICM)    ddo.getDatastore();
        DKDatastoreDefICM    dsDefICM     = (DKDatastoreDefICM) dsICM.datastoreDef();
        String               itemTypeName = ddo.getObjectType();
        DKItemTypeViewDefICM itemViewDef  = dsDefICM.retrieveItemTypeView(itemTypeName);
        if(itemViewDef.getVersionControl() == DKConstantICM.DK_ICM_VERSION_CONTROL_NEVER){
            if(options.getPrintDebugEnable()) System.out.println("   Version Control is Not Enabled for ItemType '"+itemTypeName+"'.");
        }else{ // Otherwise versioning is turned on.
            if(options.getPrintDebugEnable()) System.out.println("   Version Control is Enabled, Set to '"+itemViewDef.getVersionControl()+"' for ItemType '"+itemTypeName+"'.");

            // Build Query String to Search for All Versions of Items.
            String queryStr = "/"+itemTypeName+"[@ITEMID=\""+((DKPidICM)ddo.getPidObject()).getItemId()+"\"]";
            if(options.getPrintDebugEnable()) System.out.println("   Query:  "+queryStr);

            // Perform Search
            DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance(dsICM);
            DKNVPair queryOptions[] = new DKNVPair[3];
            queryOptions[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default)        // Specify max using a string value.
            queryOptions[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY);            // Always specify desired Retrieve Options.
            queryOptions[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                               // Must mark the end of the NVPair
            dkResultSetCursor cursor = dsICM.execute(queryStr, DKConstantICM.DK_CM_XQPE_QL_TYPE, queryOptions);
            
            // Look through reasults, checking to see if it is already selected.
            DKDDO foundDDO = null;
            while((foundDDO = cursor.fetchNext()) != null){
                // If it is not already packaged & not registerd as selected,
                // but not yet added, add to unselected list.
                if(    (isPackaged(foundDDO,options)==false) 
                    && (isRegistered(foundDDO,options)==false) ) 
                    unselectedVersionsColl.addElement(foundDDO);
            }
            
            cursor.destroy();      // Close & Destroy Cursor, Ending Implied Transaction.
        }

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.findUnselectedVersions("+obj2String(ddo)+",options) return("+obj2String(unselectedVersionsColl)+")");
        return(unselectedVersionsColl);
    }

   /**
    * Find an existing item matching the criteria specified.  If more than one found,
    * throw exception.  If none found, return 'null'.                               <BR><BR>
    * @param targetDS   - Target Datastore to Search.
    * @param viewType   - Item Type or Item Type View Name.
    * @param versionID  - Version ID of the item to be imported.
    * @param attrName   - Attribute Name to use in the search.
    * @param attrVal    - Attribute Value to use in the search.
    * @param options     - (Required) Options specified in a TExportPackageICM.Options
    *                      object.  Null not accepted in private methods.
    * @return Returns a single DDO found or 'null' if none found.
    **/
    private DKDDO findExistingItemVersion(DKDatastoreICM targetDS, String viewTypeName, String versionID, String uniqueAttrName, Object uniqueAttrVal, TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.findExistingItemVersion("+obj2String(targetDS)+","+viewTypeName+","+versionID+","+uniqueAttrName+","+obj2String(uniqueAttrVal)+","+obj2String(options)+")");

        // Build the Query String
        String queryStr = '/' + viewTypeName + "[@VERSIONID=" + versionID + " AND @"+uniqueAttrName+'=';
        // If String, Char, Date, Time, or Timestamp, then use double-quotes.
        if(    (uniqueAttrVal instanceof String) 
            || (uniqueAttrVal instanceof java.sql.Date) 
            || (uniqueAttrVal instanceof java.sql.Time) 
            || (uniqueAttrVal instanceof java.sql.Timestamp) )
            queryStr += ('\"' + uniqueAttrVal.toString() + '\"');
        else // otherwise use no quoates.
            queryStr += uniqueAttrVal;
        queryStr += ']';
        if(options.getPrintDebugEnable()) System.out.println("   Query:  "+queryStr);
        
        // Perform Search
        DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance(targetDS);
        DKNVPair queryOptions[] = new DKNVPair[3];
        queryOptions[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default)        // Specify max using a string value.
        queryOptions[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY);            // Always specify desired Retrieve Options.
        queryOptions[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                               // Must mark the end of the NVPair
        dkResultSetCursor cursor = targetDS.execute(queryStr, DKConstantICM.DK_CM_XQPE_QL_TYPE, queryOptions);

        // Handle Results
        DKDDO ddo = null;
        DKDDO cur = null;
        for(int count=0; (cur = cursor.fetchNext())!= null; count++){
            if(count > 1){ // if more than one found, throw exception.  Unique critia
                           // specified do not adequately identify a single unique 
                           // existing item.
                throw new Exception("More than one item version was found that match the uniqueness critia specified.  Only one should exist.  Please re-evaluate the uniqueness policy and criteria chosen.  Entity Type '"+viewTypeName+"', Item Version '"+versionID+"', Attribute '"+uniqueAttrName+"', and value '"+uniqueAttrVal+"'.  ");               
            }
            ddo = cur;
        }

        cursor.destroy();      // Close & Destroy Cursor, Ending Implied Transaction.

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.findExistingItemVersion(targetDS,"+viewTypeName+","+versionID+","+uniqueAttrName+","+obj2String(uniqueAttrVal)+",options)  return("+obj2String(ddo)+")");
        return(ddo);
    }

   /**
    * Find the latest existing item version matching the item criteria.
    * If none found, return 'null'.                                                 <BR><BR>
    * @param targetDS   - Target Datastore to Search.
    * @param viewType   - Item Type or Item Type View Name.
    * @param attrName   - Attribute Name to use in the search.
    * @param attrVal    - Attribute Value to use in the search.
    * @param options    - (Required) Options specified in a TExportPackageICM.Options
    *                     object.  Null not accepted in private methods.
    * @return Returns a single DDO found or 'null' if none found.
    **/
    private DKDDO findLatestExistingItemVersion(DKDatastoreICM targetDS, String viewTypeName, String uniqueAttrName, Object uniqueAttrVal, TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.findLastExistingItemVersion("+obj2String(targetDS)+","+viewTypeName+","+uniqueAttrName+","+obj2String(uniqueAttrVal)+","+obj2String(options)+")");

        // Build the Query String
        String queryStr = '/' + viewTypeName + "[@"+uniqueAttrName+'=';
        // If String, Char, Date, Time, or Timestamp, then use double-quotes.
        if(    (uniqueAttrVal instanceof String) 
            || (uniqueAttrVal instanceof java.sql.Date) 
            || (uniqueAttrVal instanceof java.sql.Time) 
            || (uniqueAttrVal instanceof java.sql.Timestamp) )
            queryStr += ('\"' + uniqueAttrVal.toString() + '\"');
        else // otherwise use no quoates.
            queryStr += uniqueAttrVal;
        queryStr += ']';
        if(options.getPrintDebugEnable()) System.out.println("   Query:  "+queryStr);
        
        // Perform Search
        DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance(targetDS);
        DKNVPair queryOptions[] = new DKNVPair[3];
        queryOptions[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default)        // Specify max using a string value.
        queryOptions[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY);            // Always specify desired Retrieval Options.
        queryOptions[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                               // Must mark the end of the NVPair
        dkResultSetCursor cursor = targetDS.execute(queryStr, DKConstantICM.DK_CM_XQPE_QL_TYPE, queryOptions);

        // Handle Results
        DKDDO latestVersionDDO = null;
        int   latestVersionNum = -1;
        DKDDO ddo = null;
        for(int count=0; (ddo = cursor.fetchNext())!= null; count++){
            // If we have found no latest version, just take the first one for first
            // time through this loop.
            if(latestVersionDDO==null){
                       latestVersionDDO    = ddo;
                String latestVersionNumStr = ((DKPidICM)ddo.getPidObject()).getVersionNumber();
                       latestVersionNum    = Integer.valueOf(latestVersionNumStr).intValue();
            }
            else{ // otherwise check versions, if later version, take that one.
                String ddoVersionNumStr = ((DKPidICM)ddo.getPidObject()).getVersionNumber();
                int    ddoVersionNum    = Integer.valueOf(ddoVersionNumStr).intValue();
                if(ddoVersionNum > latestVersionNum){ // if later version, take that one.
                    latestVersionDDO = ddo;
                    latestVersionNum = ddoVersionNum;
                }
            }
        }

        cursor.destroy();      // Close & Destroy Cursor, Ending Implied Transaction.

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.findLastExistingItemVersion(targetDS,"+viewTypeName+","+uniqueAttrName+","+obj2String(uniqueAttrVal)+",options)  return("+obj2String(ddo)+")");
        return(ddo);
    }
   
   /**
    * Find the Items directly selected by the user.                                 <BR><BR>
    * @param dsICM       - Connected datastore to export from.
    * @param queryStr    - XQPe query string selecting Items to export.
    * @param options     - (Required) Options specified in a TExportPackageICM.Options
    *                      object.  Null not accepted in private methods.
    * @return Returns a dkCollection with the results, if any.
    **/
    private dkCollection findItems(DKDatastoreICM dsICM, String queryStr,TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.findItems("+obj2String(dsICM)+",queryStr="+queryStr+","+obj2String(options)+")");
        verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.

        // Step 1:  Retrieve Items Directly Specified for Export
        // Perform Search
        DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance(dsICM);
        DKNVPair queryOptions[] = new DKNVPair[3];
        queryOptions[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default)      // Specify max using a string value.
        queryOptions[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY);          // Always specify desired Retrieval Options.
        queryOptions[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                             // Must mark the end of the NVPair
        dkResultSetCursor cursor = dsICM.execute(queryStr, DKConstantICM.DK_CM_XQPE_QL_TYPE, queryOptions);
        
        // Create Results Collection
        DKDDO ddo = null;
        DKSequentialCollection results = new DKSequentialCollection();
        while((ddo = cursor.fetchNext())!=null){ // Get the next ddo & stop when ddo == null.
            results.addElement(ddo);
        }
        
        cursor.destroy();  // Close & Destroy Cursor, Ending Implied Transaction.

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.findItems(dsICM,queryStr,options) return("+obj2String(results)+")");
        return(results);
    }
   /**
    * Append the specified text and add a newline separator to the specified file.  <BR><BR>
    * @param fileName - Name of file to append to. 'null' if disabled.
    * @param text     - text to write (newline separator will
    *                   be added).
    **/
    private void fileAppendLn(String fileName, String text) throws IOException{
        // Disabled if fileName is 'null'
        if(fileName!=null){
            // Get the system's newline separator.
            String newline = System.getProperty("line.separator");
            FileWriter file = new FileWriter(fileName,true);
            file.write(text + newline);
            file.close();
        }//end if(fileName!=null){
    }
   /**
    * Create or overwrite an existing file of the specified file name.  Write the
    * specified text as the first line and append a newline separator.              <BR><BR>
    * @param fileName - Name of file to create/ovewrite.  'null' if disabled.
    * @param text     - Initial text to write as first line (newline separator will
    *                   be added).
    **/
    private void fileCreate(String fileName, String text) throws IOException{
        // Disabled if fileName is 'null'
        if(fileName!=null){
            // Get the system's newline separator.
            String newline = System.getProperty("line.separator");
            FileWriter file = new FileWriter(fileName,false);
            file.write(text + newline);
            file.close();
        }//end if(fileName!=null){
    }

   /**
    * Subroutine of addItemAndAllReferencedItems to find and add all folder contents.  <BR><BR>
    * @param ddo           - Root DDO for item.
    * @param exportOptions - (Required) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Null not accepted in private methods.
    **/
    private void findAndAddAllFolderContents(DKDDO ddo,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.findAndAddAllFolderContents("+obj2String(ddo)+","+obj2String(exportOptions)+")");
   
        Integer semanticType = (Integer) ddo.getPropertyByName(DKConstantICM.DK_ICM_PROPERTY_SEMANTIC_TYPE);
        if(semanticType.intValue() == DKConstantICM.DK_ICM_SEMANTIC_TYPE_FOLDER){
            // Retrieve Folder Collection.
            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER); 
            if(dataid==0) // if no DKFolder collection is found in this folder, throw error.
                throw new Exception("Folder Item is missing its DKFolder collection.  It may have not been retrieved with the correct retrieval options.  Item Info: ITEMID = '"+((DKPidICM)ddo.getPidObject()).getItemId()+"', Object Type = '"+((DKPidICM)ddo.getPidObject()).getObjectType());
            DKFolder dkFolder = (DKFolder) ddo.getData(dataid); 
            if(dkFolder==null)
                throw new Exception("Folder Item's DKFolder attribute is null!  It may have not been retrieve with the correct retrieval options.  Item Info: ITEMID = '"+((DKPidICM)ddo.getPidObject()).getItemId()+"', Object Type = '"+((DKPidICM)ddo.getPidObject()).getObjectType());
            // Go Through Folder Contents
            dkIterator iter = dkFolder.createIterator();
            while(iter.more()){
                DKDDO contentDDO = (DKDDO)iter.next();
                dkCollection unselectedVersions = null;
                String       contentVerListStr  = null;
                if(isVersioned(contentDDO,exportOptions)==false)
                    contentVerListStr = "none";
                else{
                    unselectedVersions = findUnselectedVersions(contentDDO, exportOptions);
                    contentVerListStr  = getVersionListStr(unselectedVersions);
                }
                    
                // Get the folder content policy
                String itemDesc = getCompDesc(ddo);
                int folderContentPolicy = exportOptions.getAnswer_folderContentPolicy(itemDesc,contentVerListStr);
                // Remove, simply omit separate folder content info packet.
                if(folderContentPolicy==TExportPackageICM.OPTION_FOLDER_CONTENT_REMOVE_VALUE){
                    if(exportOptions.getPrintDebugEnable()){
                        System.out.println("  Option Choice:  Remove Content From Folder.");
                        System.out.println("                  Folder  = "+obj2String(ddo));
                        System.out.println("                  Content = "+obj2String(contentDDO));
                    }                    
                // Else, export at least one content & info packet.
                } else {
                    if(exportOptions.getPrintDebugEnable()) System.out.println("  Export at least the latest version first.");
                    // Export at least the latest version first.
                    addItemAndAllReferencedItems(contentDDO,exportOptions);
                    // Add Info Package to Manage This Relationship.
                    FolderInfoPack folderInfoPack = new FolderInfoPack(ddo,contentDDO);
                    if(_folderInfoHT.containsKey(folderInfoPack.getKey())==false) // If not already recorded,
                        _folderInfoHT.put(folderInfoPack.getKey(),folderInfoPack);

                    // If all versions are to be exported, export all now.
                    if(    (unselectedVersions != null)  // If not versioned, will be null.
                        && (folderContentPolicy==TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS) ){
                        if(exportOptions.getPrintDebugEnable()) System.out.println("  Option Choice:  Export All Folder Content Versions.");
                        dkIterator iter2 = unselectedVersions.createIterator();
                        while(iter2.more()){
                           DKDDO ddoVer = (DKDDO) iter2.next();
                           addItemAndAllReferencedItems(ddoVer,exportOptions);
                        }
                    }
                }// end else export at least one content & info packet.
            }
            // Lastly, strip off all folder contents from object since tracked
            // in separe information packages.
            dkFolder.removeAllElements();
        }//end if semType folder.

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.findAndAddAllFolderContents("+obj2String(ddo)+","+obj2String(exportOptions)+")");
    }

   /**
    * Subroutine of addItemAndAllReferencedItems to find and add all items linked to.   <BR><BR>
    * @param ddo           - Root DDO for item.
    * @param exportOptions - (Required) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Null not accepted in private methods.
    **/
    private void findAndAddAllLinkedItems(DKDDO ddo,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.findAndAddAllLinkedItems("+obj2String(ddo)+","+obj2String(exportOptions)+")");

        // List all Data Items
        for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
            Short type   = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
            switch(type.intValue()){
                case DKConstant.DK_CM_DATAITEM_TYPE_LINKCOLLECTION:
                   //String name  = ddo.getDataName(dataid);
                   Object value = ddo.getData(dataid);
                   if(value!=null){
                        if(value instanceof DKLinkCollection){ // if it is a link collection go through and add references.
                            DKLinkCollection linkColl = (DKLinkCollection) value;
                            dkIterator iter = linkColl.createIterator();
                            while(iter.more()){ // go through all in the collection.
                                DKLink dklink  = (DKLink) iter.next();
                                DKDDO  source  = (DKDDO) dklink.getSource();
                                DKDDO  target  = (DKDDO) dklink.getTarget();
                                DKDDO linkItem = (DKDDO) dklink.getLinkItem();
                                DKDDO  other   = null;  // Other DDO, source if inbound, target if outbound.
                                // Determine 'other' as target if this is source, source if this is not source.
                                if(((DKPidICM)source.getPidObject()).getItemId().compareTo(((DKPidICM)ddo.getPidObject()).getItemId())==0) // if this is source, then other is target
                                    other = target;
                                else // otherwise this is the target and the other is the source.
                                    other = source;
                                dkCollection unselectedOtherItemVersions = null;
                                dkCollection unselectedLinkItemVersions  = null;
                                String       otherItemVerListStr = null;
                                String       linkItemVerListStr  = null;
                                if(isVersioned(other,exportOptions)==false)
                                    otherItemVerListStr = "none";
                                else{
                                    unselectedOtherItemVersions = findUnselectedVersions(other, exportOptions);
                                    otherItemVerListStr  = getVersionListStr(unselectedOtherItemVersions);
                                }
                                if(linkItem!=null){  // if the link item is specified.
                                    if(isVersioned(linkItem,exportOptions)==false)
                                        linkItemVerListStr = "none";
                                    else{
                                        unselectedLinkItemVersions = findUnselectedVersions(linkItem, exportOptions);
                                        linkItemVerListStr  = getVersionListStr(unselectedLinkItemVersions);
                                    }
                                } else
                                    linkItemVerListStr = "n/a";
                                
                                // Get the linked item policy
                                String itemDesc = getCompDesc(ddo);
                                int linkedItemsPolicy = exportOptions.getAnswer_linkedItemsPolicy(itemDesc,otherItemVerListStr,linkItemVerListStr);
                                // Remove, simply omit separate folder content info packet.
                                if(linkedItemsPolicy==TExportPackageICM.OPTION_LINKED_ITEMS_REMOVE_LINK){
                                    if(exportOptions.getPrintDebugEnable()){
                                        System.out.println("  Option Choice:  Remove Link.");
                                        System.out.println("                  Source  = "+obj2String(source));
                                        System.out.println("                  Target  = "+obj2String(target));
                                    }                    
                                // Else, export at least one content & info packet.
                                } else {
                                    if(exportOptions.getPrintDebugEnable()) System.out.println("  Export at least the latest version first.");
                                    // Export at least the latest version first.
                                    // Export referenced items if they are not already.
                                    addItemAndAllReferencedItems(other,exportOptions);
                                    if(linkItem!=null) // link item is optional.
                                        addItemAndAllReferencedItems(linkItem,exportOptions);
                                    // Add Info Package to Manage This Relationship.
                                    LinkInfoPack linkInfoPack = new LinkInfoPack(dklink);
                                    if(_linksInfoHT.containsKey(linkInfoPack.getKey())==false) // If not already recorded,
                                        _linksInfoHT.put(linkInfoPack.getKey(),linkInfoPack);

                                    // If all versions are to be exported, export all now.
                                    if(linkedItemsPolicy==TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS){
                                        if(exportOptions.getPrintDebugEnable()) System.out.println("  Option Choice:  Export All Folder Content Versions.");
                                        // Other DDOs                                     
                                        if( unselectedOtherItemVersions != null){  // If not versioned, will be null.
                                            dkIterator iter2 = unselectedOtherItemVersions.createIterator();
                                            while(iter2.more()){
                                            DKDDO ddoVer = (DKDDO) iter2.next();
                                            addItemAndAllReferencedItems(ddoVer,exportOptions);
                                            }
                                        }
                                        // Link Items
                                        if( unselectedLinkItemVersions != null){  // If not versioned, will be null.
                                            dkIterator iter2 = unselectedLinkItemVersions.createIterator();
                                            while(iter2.more()){
                                            DKDDO ddoVer = (DKDDO) iter2.next();
                                            addItemAndAllReferencedItems(ddoVer,exportOptions);
                                            }
                                        }
                                        
                                    }
                                }// end else export at least one content & info packet.
                            }
                            // Clear out link collection data, since links is not tracked
                            // separately in info packages.
                            linkColl.removeAllElements();
                        }//end if is DKLinkCollection
                    }
                    break;
                default:
                    break;
            }// end swith on data item type
        }// end for all data items in DDO

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.findAndAddAllLinkedItems("+obj2String(ddo)+","+obj2String(exportOptions)+")");
    }

   /**
    * Subroutine of addItemAndAllReferencedItems to find and add all items referenced in
    * reference attributes.                                                         <BR><BR>
    * @param ddo           - Root DDO for item.
    * @param exportOptions - (Required) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Null not accepted in private methods.
    **/
    private void findAndAddAllReferenceAttrItems(DKDDO ddo,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.findAndAddAllReferenceAttrItems("+obj2String(ddo)+","+obj2String(exportOptions)+")");

        ArrayList<DKChildCollection> childCollections = new ArrayList<DKChildCollection>(); // when obtaining data items, collect child collections.

        // List all Data Items
        for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
            Object value = null;
            Short  type  = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
            switch(type.intValue()){
                case DKConstant.DK_CM_DATAITEM_TYPE_DDOOBJECT:
                case DKConstant.DK_CM_DATAITEM_TYPE_XDOOBJECT:
                case DKConstant.DK_CM_DATAITEM_TYPE_DATAOBJECTBASE:
                    String name  = ddo.getDataName(dataid);
                    value = ddo.getData(dataid);
                    if(exportOptions.getPrintDebugEnable()) System.out.println("Found Reference Attribute '"+name+"' with value '"+obj2String(value)+"'.");
                    // Reference Attribute
                    if(value!=null){
                        dkCollection unselectedVersions = null;
                        String       valueVerListStr  = null;
                        if(isVersioned((DKDDO)value,exportOptions)==false)
                            valueVerListStr = "none";
                        else{
                            unselectedVersions = findUnselectedVersions((DKDDO)value, exportOptions);
                            valueVerListStr    = getVersionListStr(unselectedVersions);
                        }
                            
                        // Get the folder content policy
                        String itemDesc = getCompDesc(ddo);
                        int refAttrValuePolicy = exportOptions.getAnswer_refAttrValuePolicy(itemDesc,name,valueVerListStr);
                        // Remove, simply omit ref attr value info packet.
                        if(refAttrValuePolicy==TExportPackageICM.OPTION_REFATTR_VALUE_REMOVE_VALUE){
                            if(exportOptions.getPrintDebugEnable()){
                                System.out.println("  Option Choice:  Remove Ref Attr Value.");
                                System.out.println("                  RefAttr = "+name);
                                System.out.println("                    Value = "+obj2String(value));
                            }                    
                        // Else, export at least one content & info packet.
                        } else {
                            if(exportOptions.getPrintDebugEnable()) System.out.println("  Export at least the referenced version first.");
                            // Export at least the referenced version first.
                            addItemAndAllReferencedItems((DKDDO)value,exportOptions);
                            // Add Info Package to Manage This Relationship.
                            RefAttrInfoPack refAttrInfoPack = new RefAttrInfoPack((DKDDO)ddo,name);
                            if(_refAttrInfoHT.containsKey(refAttrInfoPack.getKey())==false) // If not already recorded,
                                _refAttrInfoHT.put(refAttrInfoPack.getKey(),refAttrInfoPack);

                            // If all versions are to be exported, export all now.
                            if(    (unselectedVersions != null)  // If not versioned, will be null.
                                && (refAttrValuePolicy==TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS) ){
                                if(exportOptions.getPrintDebugEnable()) System.out.println("  Option Choice:  Export All Ref Attr Value Versions.");
                                dkIterator iter2 = unselectedVersions.createIterator();
                                while(iter2.more()){
                                DKDDO ddoVer = (DKDDO) iter2.next();
                                addItemAndAllReferencedItems(ddoVer,exportOptions);
                                }
                            }
                        }// end else export at least one content & info packet.
                        
                        // Null out since it will now be traced separately.
                        ddo.setData(dataid,null);
                    }//end if value not null.
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                    value = ddo.getData(dataid);
                    if(value!=null){
                        if(value instanceof DKChildCollection){ // if it is a child collection, add it to a list for printing children later
                            if(exportOptions.getPrintDebugEnable()) System.out.println("Found Child Collection of Type '"+((DKChildCollection)value).getAssociatedAttrName()+"'.");
                            childCollections.add((DKChildCollection)value);
                        }
                    }
                    break;
                default:
                    break;
            }// end swith on data item type
        }// end for all data items in DDO
        
        // Handle Children
        for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
            DKChildCollection childCollection = childCollections.get(i); // get each child collection
            dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
            while(iter.more()){ // While there are children, print each
                DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                findAndAddAllReferenceAttrItems(child,exportOptions);
            }
        }

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.findAndAddAllReferenceAttrItems("+obj2String(ddo)+","+obj2String(exportOptions)+")");
    }

   /**
    * Subroutine of addItemAndAllReferencedItems to find and add all Document Model Parts.
    * Parts only need to be noted.  They should be exported in the XML.              <BR><BR>
    * @param ddo           - Root DDO for item.
    * @param exportOptions - (Required) Export Options specified in a TExportPackageICM.ExportOptions
    *                        object.  Null not accepted in private methods.
    **/
    private void findAndNoteDocumentModelParts(DKDDO ddo,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.findAndNoteDocumentModelParts("+obj2String(ddo)+","+obj2String(exportOptions)+")");

        if(exportOptions.getPrintDebugEnable()) System.out.println("  Parts currently not tracked separately.");
      
        /*// Retrieve Parts Collection.
        short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
        if(dataid>0){ // If parts exist, continue
            DKParts dkParts = (DKParts) ddo.getData(dataid); 
            if(dkParts==null)
                throw new Exception("DKParts attribute is null but the dataid exists.  It may have not been retrieve with the correct retrieval options.  Item Info: ITEMID = '"+((DKPidICM)ddo.getPidObject()).getItemId()+"', Object Type = '"+((DKPidICM)ddo.getPidObject()).getObjectType());
            // Go Through Contents
            dkIterator iter = dkParts.createIterator();
            while(iter.more()){
                // nothing for now
            }
        }//end if parts exist.
        */
        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.findAndNoteDocumentModelParts("+obj2String(ddo)+","+obj2String(exportOptions)+")");
    }

   /**
    * Get the fully-specified path from the fully-specified file name.              <BR><BR>
    * @param fullySpecifiedFileName - Fully-specified file name, including path.
    * @param options                - (Required) Options specified in a TExportPackageICM.Options
    *                                 object.  Null not accepted in private methods.
    * @return Returns the path given in the fully-specified file name, or 'null' if none given.
    **/
    private static String getFilePath(String fileName,TExportPackageICM.Options options) throws Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.getFilePath(fileName="+fileName+")");
        
        String filePath = null;

        // Find the last location of the '\' or '/'.
        int index = fileName.lastIndexOf('\\');
        if(index==-1) // if '\' doesn't occur, check for forward-slash.
            index = fileName.lastIndexOf('/');
        if(index==-1) // if still not found, return null, since no path exists.
            filePath = null;
        else // otherwise return the substring
            filePath = fileName.substring(0,index+1);
            
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.getFilePath(fileName) return("+obj2String(filePath)+")");
        return(filePath);
    }

   /**
    * Get the central file name stub, removing the core name without path or
    * the export package extension.                                                 <BR><BR>
    * @param fullySpecifiedFileName - Fully-specified file name, including path
    *                                 and optionally the export package extension.
    * @param options                - (Required) Options specified in a TExportPackageICM.Options
    *                                 object.  Null not accepted in private methods.
    * @return Returns the core name (stub) of the file given.  Strips off path & 
    *         export package extension.
    **/
    private static String getFileNameStub(String fileName,TExportPackageICM.Options options) throws Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.getFileNameStub(fileName="+fileName+")");

        String fileNameStub = null;

        // Find the last location of the '\' or '/'.
        int beginIndex = fileName.lastIndexOf('\\');
        if(beginIndex==-1) // if '\' doesn't occur, check for forward-slash.
            beginIndex = fileName.lastIndexOf('/');
        if(beginIndex==-1) // if none exists, just start with beginning of string.
            beginIndex = -1;  // Use -1, not 0 because one will be added to the real start later.
        // Find the extension if it exists.
        String extName = getFileNameExt(fileName,options);
        if(extName==null) // if it doesn't exist, get all remaining.
            fileNameStub = fileName.substring(beginIndex+1);
        else{
            // If it is the export package extension, strip off
            if(extName.compareToIgnoreCase(T_ICM_EXPORT_PACKAGE_FILE_EXT)==0)
                fileNameStub = fileName.substring(beginIndex+1,fileName.length()-4); // Start after slash
            else // otherwise leave extension.
                fileNameStub = fileName.substring(beginIndex+1); // Start after slash
        }

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.getFileNameStub(fileName) return("+obj2String(fileNameStub)+")");
        return(fileNameStub);
    }

   /**
    * Get the desription of the component specified by the DDO.                     <BR><BR>
    * @param ddo - component as a DKDDO object.
    * @return Returns the description of the item indicated in the ddo.
    **/
    private static String getCompDesc(DKDDO ddo){
        DKPidICM pidICM = (DKPidICM)ddo.getPidObject();
        String objectType = pidICM.getObjectType();
        String itemId     = pidICM.getItemId();
        String version    = pidICM.getVersionNumber();
        String compId     = pidICM.getComponentId();
        return("Item '"+itemId+"', v'"+version+"', Type '"+objectType+"', Comp '"+compId+"'");
    }
   /**
    * Get the desription of the item specified by the DDO.                          <BR><BR>
    * @param ddo - item as a DKDDO object.
    * @return Returns the description of the item indicated in the ddo.
    **/
    private static String getItemDesc(DKDDO ddo){
        DKPidICM pidICM = (DKPidICM)ddo.getPidObject();
        String objectType = pidICM.getObjectType();
        String itemId     = pidICM.getItemId();
        String version    = pidICM.getVersionNumber();
        return("Item '"+itemId+"', v'"+version+"', Type '"+objectType+"'");
    }

   /**
    * Get the filename extension of the specified file.                             <BR><BR>
    * @param fileName - Any file name with or without file path.
    * @param options  - (Required) Options specified in a TExportPackageICM.Options
    *                   object.  Null not accepted in private methods.
    * @return Returns the filename extension if one exists, null if none exists.
    **/
    private static String getFileNameExt(String fileName,TExportPackageICM.Options options) throws Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.getFileNameExt(fileName="+fileName+")");

        String fileNameExt = null;

        // Find the extension if it exists.
        int index = fileName.lastIndexOf('.');        
        if(index==-1) // if it doesn't exist, return 'null'
            fileNameExt = null;
        else
            fileNameExt = fileName.substring(index);
            
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.getFileNameExt(fileName) return("+obj2String(fileNameExt)+")");
        return(fileNameExt);            
    }
   /**
    * Count the number of lines in the specified file.                              <BR><BR>
    * @param fileName - Name of file to count the number of lines in.
    * @param options  - (Required) Options specified in a TExportPackageICM.Options
    *                   object.  Null not accepted in private methods.
    **/
    private int getNumLinesInFile(String fileName,TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.getNumLinesInFile(fileName="+fileName+","+obj2String(options)+")");

        // Open File
        FileReader fileReader = new FileReader(fileName);
        BufferedReader file   = new BufferedReader(fileReader);

        // Read Line-by-line
        int count = 0;
        while(file.readLine()!=null){ // Continue until reach end of file.
            count++;
        }

        // Close the file
        file.close();
        
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.getNumLinesInFile(fileName,options) return("+count+")");
        return(count);
    }
   /**
    * Get a list of all unique attributes names that are defined for the component type 
    * of the specified component.                                                   <BR><BR>
    * @param component - Component to obtain a unique attribute list for.  Component must
    *                    be a ddo still actively connected to its datastore.
    * @param options   - (Required) Options specified in a TExportPackageICM.Options
    *                    object.  Null not accepted in private methods.
    * @return Returns an array of Strings containing attribute name(s) according to the
    *         criteria listed.
    **/
    private static ArrayList<String> getUniqueAttrList(DKDDO component, TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.getUniqueAttrs("+obj2String(component)+","+obj2String(options)+")");

        ArrayList<String> uniqueAttrList = new ArrayList<String>();

        // Retrieve the Comp View Definition
        DKDatastoreICM dsICM = (DKDatastoreICM) component.getDatastore();
        verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
        DKComponentTypeViewDefICM compTypeViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(component.getObjectType());
        if(compTypeViewDef==null)
            throw new Exception("Either the Component Type or Component Type View '"+component.getObjectType()+"' is not defined on the target system or the user does not have the privileges required to access that type.");
        
        // Go through user attributes.
        dkCollection attrs = compTypeViewDef.listAttrs();
        dkIterator iter = attrs.createIterator();
        while(iter.more()){
            DKAttrDefICM attrDef = (DKAttrDefICM) iter.next();   
            if(attrDef.isUnique()){       // If unique, add it to the list
                uniqueAttrList.add(attrDef.getName());
            }
        }
        // Go through all user attr groups.
        dkCollection attrGroups = compTypeViewDef.listAttrGroups();
        iter = attrGroups.createIterator();
        while(iter.more()){
            DKAttrGroupDefICM attrGroupDef = (DKAttrGroupDefICM) iter.next();
            attrs = attrGroupDef.listAttrs();
            dkIterator iter2 = attrs.createIterator();
            while(iter2.more()){
                DKAttrDefICM attrDef = (DKAttrDefICM) iter2.next();   
                if(attrDef.isUnique()){       // If unique, add to the list.
                    uniqueAttrList.add(attrDef.getName());
                }
            }
        }

        // Debug Information
        if(options.getPrintDebugEnable()){
            String listStr = "";
            for(int i=0; i<uniqueAttrList.size(); i++){
                   listStr += (uniqueAttrList.get(i) + ",");
            }
            System.out.println("   Identified '"+uniqueAttrList.size()+"' Unique Attrs:  "+listStr);
        }
        
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.getUniqueAttrs("+obj2String(component)+","+obj2String(options)+") return("+obj2String(uniqueAttrList)+")");
        return(uniqueAttrList);
    }
   /**
    * Get the version list as a string.  Package collection of DDOs specified
    * as a comma-separated list of version numbers.                                 <BR><BR>
    * @param coll    - Collection of versions of the same item.
    * @return Returns a string representation of the list of versions.
    **/
    private String getVersionListStr(dkCollection coll) throws DKException, Exception{
        String verListStr = "";
        if(coll.cardinality() > 0){
            // Package Up Version List
            dkIterator iter = coll.createIterator();
            while(iter.more()){
                DKDDO ddo = (DKDDO) iter.next();
                String verStr = ((DKPidICM)ddo.getPidObject()).getVersionNumber();
                verListStr += (verStr + ',');
            }
            // Drop last comma in list.
            verListStr = verListStr.substring(0,verListStr.length()-1); 
        } else // otherwise it is empty.
            verListStr = "none";
        return(verListStr);
    }
   /**
    * Import all items added to this package.  If multiple versions of the same
    * item were imported, they should be imported into multiple versions of the 
    * the new item.                                                                 <BR><BR>
    * @param targetDS      - Target datastore to import items to.
    * @param importOptions - (Optional) Import Options specified in a TExportPackageICM.ImportOptions
    *                        object.  Use 'null' for defaults.
    *                        Please refer to the ImportOptions constructor information on
    *                        default settings.
    * @return Returns an array of the TExportPackageICM.ImportRecord objects providing access
    *         to the imported items, Item Id mapping, and more.
    **/
    public synchronized TExportPackageICM.ImportRecord[] importItems(DKDatastoreICM targetDS,TExportPackageICM.ImportOptions importOptions) throws DKException, Exception{
        if(importOptions==null) // If no options specified, use defaults.
            importOptions = new TExportPackageICM.ImportOptions();
        if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.importItems("+obj2String(targetDS)+","+obj2String(importOptions)+")");
        verifyConnectedDatastore(targetDS); // Validate that the datastore is connectected.

        // Call the import operation with restart mode turned off
        TExportPackageICM.ImportRecord[] importRecords = importItems(targetDS,false,importOptions);
        
        if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.importItems("+obj2String(targetDS)+","+obj2String(importOptions)+")  return(TExportPackageICM.ImportRecord["+importRecords.length+"])");
        return(importRecords);
    }
   
   /**
    * Import all items added to this package.  If multiple versions of the same
    * item were imported, they should be imported into multiple versions of the 
    * the new item.                                                                 <BR><BR>
    * @param targetDS          - Target datastore to import items to.
    * @param restartModeEnable - If true, restart mode is enabled and this MUST 
    *                            be called from restartItems().  Otherwise import
    *                            will start from scratch.
    * @param options           - (Required) Options specified in a TExportPackageICM.Options
    *                            object.  Null not accepted in private methods.
    * @return Returns an array of the TExportPackageICM.ImportRecord objects providing access
    *         to the imported items, Item Id mapping, and more.
    **/
    private synchronized TExportPackageICM.ImportRecord[] importItems(DKDatastoreICM targetDS,boolean restartModeEnable,TExportPackageICM.ImportOptions importOptions) throws DKException, Exception{
        if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.importItems("+obj2String(targetDS)+","+obj2String(restartModeEnable)+","+obj2String(importOptions)+")");

        // Initialize & Beging Import Operation Tracking
        String trackingFileName = importOptions.getTrackingFileName(); // 'null' if disabled.
        // Get the system's newline separator.
        String newline = System.getProperty("line.separator");
        fileCreate(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_FILE_IDENTIFIER+" v"+TExportPackageICM.T_ICM_EXPORT_PACKAGE_VERSION+newline); // Add 2nd newline for readability.

        // Prepare for import of versioned items that may require Component PID Mapping
        clearComponentPidInfo(importOptions);

        // First Pass:  Go through all items, importing each without 
        //              Links, Referneces, or Folder Contents.
        fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS1_TAG_BEGIN+newline); // Add 2nd newline for readability.
        fileAppendLn(trackingFileName,(new ItemInfoPack(false,null,null,null)).tableHeader(TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_TAG_LEN));
        ItemInfoPack lastItemPack = null; // Save reference to last item when loading, to handle multiple versions of an item.
        Collection<ItemInfoPack>   itemColl     = _itemInfoTree.values();
        Iterator<ItemInfoPack>     itemListIter = itemColl.iterator();
        while(itemListIter.hasNext()){
            ItemInfoPack itemInfoPack = itemListIter.next();
            // Restart capability:  If already imported or if told to omit/skip, mark
            // (PASS 1)             for ommission and continue
            if(restartModeEnable && _restartP1CmpltItemMap.containsKey(itemInfoPack.getKey())){
                fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE+itemInfoPack.toString());
                // Retrieve the completed item based on object type, version, and itemid.
                // This was completed items can still be used in phase 2, unless reference
                // attributes are involved.
                // Get the completed info pack
                ItemInfoPack completedItemPack = _restartP1CmpltItemMap.get(itemInfoPack.getKey());
                // Retrieve the item
                DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(targetDS);
            	dkRetrieveOptions.baseAttributes(true);
            	dkRetrieveOptions.basePropertyAclName(true);
                DKDDO completedItem = completedItemPack.retrieveImportedItem(targetDS,dkRetrieveOptions,importOptions);
                // Set completed item in item info pack we are going through.
                itemInfoPack.setImportedItem(completedItem);
            }else if(restartModeEnable && _restartP1OmitItemMap.containsKey(itemInfoPack.getKey())){
                fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_OMITTING+itemInfoPack.toString());
            }else{ // otherwise import it
                fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_STARTING+itemInfoPack.toString());
                itemInfoPack.importItem(targetDS,_refAttrInfoHT,_verComponentMap,lastItemPack,importOptions);
                fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE+itemInfoPack.toString());
                lastItemPack = itemInfoPack; // Save reference to last item when loading, to handle multiple versions of an item.
            }
        }
        fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS1_TAG_END+newline); // Add 2nd newline for readability.

        // Free Up Memory No Longer Needed / Free Component PID Mapping
        clearComponentPidInfo(importOptions);

        // Second Pass:  Folder Contents, Links, & References Attributes.
        fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS2_TAG_BEGIN+newline); // Add 2nd newline for readability.
        // -- Folder Contents
        Enumeration<FolderInfoPack> folderList = _folderInfoHT.elements();
        while(folderList.hasMoreElements()){
            FolderInfoPack folderInfoPack = folderList.nextElement();
            if(restartModeEnable) // if restart mode enabled, send omitted map.
                folderInfoPack.addToImportedItem(_itemInfoTree,_restartP2OmitItemMap,importOptions);
            else 
                folderInfoPack.addToImportedItem(_itemInfoTree,null,importOptions);
        }
        // -- Links
        Enumeration<LinkInfoPack> linkInfoList = _linksInfoHT.elements();
        while(linkInfoList.hasMoreElements()){
            LinkInfoPack linkInfoPack = linkInfoList.nextElement();
            if(restartModeEnable) // if restart mode enabled, send omitted map.
                linkInfoPack.addToImportedItem(_itemInfoTree,_restartP2OmitItemMap,importOptions);
            else
                linkInfoPack.addToImportedItem(_itemInfoTree,null,importOptions);
        }
        // -- Reference Attributes
        Enumeration<RefAttrInfoPack> refAttrInfoList = _refAttrInfoHT.elements();
        while(refAttrInfoList.hasMoreElements()){
            RefAttrInfoPack refAttrInfoPack = refAttrInfoList.nextElement();
            if(restartModeEnable) // if restart mode enabled, send omitted map.
                refAttrInfoPack.addToImportedItem(_itemInfoTree,_restartP2CmpltItemMap,_restartP2OmitItemMap,importOptions);
            else
                refAttrInfoPack.addToImportedItem(_itemInfoTree,null,null,importOptions);
        }
        // -- Update Each that changed in pass 2.  Don't check in, since other versions of same checked out item may exist.
        fileAppendLn(trackingFileName,(new ItemInfoPack(false,null,null,null)).tableHeader(TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_TAG_LEN));
        itemColl     = _itemInfoTree.values();
        itemListIter = itemColl.iterator();
        while(itemListIter.hasNext()){
            ItemInfoPack itemInfoPack = itemListIter.next();
            if(itemInfoPack.isChangedInPhase2()){ // if it was modified in phase 2, update & check in
                // Restart capability:  If already imported or if told to omit/skip, mark
                // (PASS 2)             for ommission and continue
                if(restartModeEnable && _restartP2CmpltItemMap.containsKey(itemInfoPack.getKey())){
                    fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE+itemInfoPack.toString());
                }else if(restartModeEnable && _restartP2OmitItemMap.containsKey(itemInfoPack.getKey())){
                    fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_OMITTING+itemInfoPack.toString());
                }else{ // otherwise import it
                    fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_STARTING+itemInfoPack.toString());
                    retrieveResourceAndPartContentIntoExistingDDO(itemInfoPack.getImportedItem(),importOptions);
                    itemInfoPack.getImportedItem().update();
                    itemInfoPack.setChangedInPhase2(false); // reset indicator.
                    fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE+itemInfoPack.toString());
                }
            }//end if(itemInfoPack.isChangedInPhase2()){
        }//end while(itemListIter.hasNext()){
        fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS2_TAG_END+newline);

        // -- Lastly check all in. (PASS 3)
        fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS3_TAG_BEGIN+newline);
        fileAppendLn(trackingFileName,(new ItemInfoPack(false,null,null,null)).tableHeader(TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_TAG_LEN));
        itemListIter = itemColl.iterator();
        while(itemListIter.hasNext()){
            ItemInfoPack itemInfoPack = (ItemInfoPack) itemListIter.next();
            // Restart capability:  If already imported or if told to omit/skip, mark
            // (PASS 3)             for ommission and continue
            if(restartModeEnable && _restartP3CmpltItemMap.containsKey(itemInfoPack.getKey())){
                fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE+itemInfoPack.toString());
            }else if(restartModeEnable && _restartP3OmitItemMap.containsKey(itemInfoPack.getKey())){
                fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_OMITTING+itemInfoPack.toString());
            }else{ // otherwise check it in
                fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_STARTING+itemInfoPack.toString());
                DKDatastoreICM    dsICM    = ((DKDatastoreICM)itemInfoPack.getImportedItem().getDatastore());
                DKDatastoreExtICM dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
                verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.
                DKDDO item = itemInfoPack.getImportedItem();
                // If it is versioned, check if it is already checked in by an earlier version.
                if(isVersioned(item, importOptions)){
                    if(dsExtICM.isCheckedOut(item))// Only check in if it isn't already
                        dsICM.checkIn(item);
                } else // otherwise just check it in
                    dsICM.checkIn(item);
                fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE+itemInfoPack.toString());
            }
        }
        fileAppendLn(trackingFileName,TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS3_TAG_END+newline); // Add 2nd newline for readability.
    
        // Create Array of Imported Items.
        ImportRecord[] importRecords = new ImportRecord[_itemInfoTree.size()];
        itemColl     = _itemInfoTree.values();
        itemListIter = itemColl.iterator();
        for(int i=0; i<importRecords.length; i++){
            ItemInfoPack itemInfoPack = itemListIter.next();
            if(itemInfoPack==null)
                throw new Exception("Unexpectedly hit end of list while preparing array of imported items to return to user.  Size must have unexpectedly changed.");
            importRecords[i] = new ImportRecord(itemInfoPack);
        }
        
        if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.importItems("+obj2String(targetDS)+","+obj2String(restartModeEnable)+","+obj2String(importOptions)+")  return(TExportPackageICM.ImportRecord["+importRecords.length+"])");
        return(importRecords);
    }

   /**
    * Determine if an Item is in the package.                                       <BR><BR>
    * @param ddo     - Root DDO for item.
    * @param options - (Optional) Options specified in a TExportPackageICM.Options
    *                  object.  Use 'null' for defaults.
    *                  Please refer to the Options constructor information on
    *                  default settings.
    **/
    public boolean isPackaged(DKDDO ddo,TExportPackageICM.Options options) throws Exception{
        if(options==null) // If no options specified, use defaults.
            options = new TExportPackageICM.Options();
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.isPackaged("+obj2String(ddo)+","+obj2String(options)+")");

        boolean retVal = false;

        ItemInfoPack itemInfoPack = new ItemInfoPack(false,ddo);  // Create object to get key.
        if(_itemInfoTree.containsKey(itemInfoPack.getKey()))       // Use key to check table.
            retVal = true;
        else
            retVal = false;

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.isPackaged(ddo,options) return("+obj2String(retVal)+")");
        return(retVal);
    }    
   /**
    * Determine if an Item is pre-registered for selection.  For more information,
    * see the registerSelected() function.                                          <BR><BR>
    * @param ddo     - Root DDO for item.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private boolean isRegistered(DKDDO ddo,TExportPackageICM.Options options) throws Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.isRegistered("+obj2String(ddo)+","+obj2String(options)+")");

        boolean retVal = false;

        ItemInfoPack itemInfoPack = new ItemInfoPack(false,ddo);  // Create object to get key.
        if(_preRegisterTree.containsKey(itemInfoPack.getKey()))       // Use key to check table.
            retVal = true;
        else
            retVal = false;

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.isRegistered(ddo,options) return("+obj2String(retVal)+")");
        return(retVal);
    }    
   /**
    * Determines if the ddo is a document model item or not.                        <BR><BR>
    * @param ddo     - DDO to check if it is a document model item DDO.  Only root components
    *                  will register as such.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    * @return Returns true if is classified as document model, false if not.
    **/
    private boolean isDocModel(DKDDO ddo,TExportPackageICM.Options options) throws DKUsageError, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.isDocModel("+obj2String(ddo)+")");
        
        // Short-cut:  Just check if it has a DKParts collection.  In all cases that
        //             this function is called, DKParts should have been retrieve for
        //             all documents.
        boolean retval = false;
        short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
        if(dataid==0)
            retval = false;
        else
            retval = true;

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.isDocModel(ddo) return("+obj2String(retval)+")");
        return (retval);
    }//end isDocModel
   /**
    * Determines if the ddo is a document model part or not.                        <BR><BR>
    * @param ddo     - DDO to check if it is a document part ('part' type of the Document Model)
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    * @return Returns true if part, false if not.
    **/
    private boolean isDocModelPart(DKDDO ddo,TExportPackageICM.Options options) throws DKUsageError, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.isDocModelPart("+obj2String(ddo)+")");

        boolean retval = false;

        // Get the datastore definition object.
        DKDatastoreICM    dsICM    = (DKDatastoreICM)    ddo.getDatastore();
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
        verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.

        // Determine if it is a document part.
        DKPidICM pidICM = (DKPidICM) ddo.getPidObject();
        String objectType = pidICM.getObjectType();
        DKItemTypeViewDefICM itemTypeViewDef = (DKItemTypeViewDefICM) dsDefICM.retrieveItemTypeView(objectType);
        if(itemTypeViewDef.getClassification()==DKConstantICM.DK_ICM_ITEMTYPE_CLASS_DOC_PART)
            retval = true;
        else
            retval = false;
        
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.isDocModelPart(ddo) return("+obj2String(retval)+")");
        return (retval);
    }//end isDocModelPart
   /**
    * Determines if the ddo document routing item or not.  Doc routing item determination
    * is based on item type id code in the 200 range.                               <BR><BR>
    * @param ddo     - DDO to check if it is a document routing item.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    * @return Returns true if doc routing item (item type id in 200 range), false if not.
    **/
    private boolean isDocRoutingItem(DKDDO ddo,TExportPackageICM.Options options) throws DKUsageError, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.isDocRoutingItem("+obj2String(ddo)+")");

        boolean retval = false;

        // Get the datastore definition object.
        DKDatastoreICM    dsICM    = (DKDatastoreICM)    ddo.getDatastore();
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
        verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.

        // Check the item type id to see if it is in the 200 range.
        DKPidICM pidICM = (DKPidICM) ddo.getPidObject();
        String objectType = pidICM.getObjectType();
        DKItemTypeViewDefICM itemTypeViewDef = (DKItemTypeViewDefICM) dsDefICM.retrieveItemTypeView(objectType);
        int itemTypeId = itemTypeViewDef.getItemTypeId();
        if( (itemTypeId > 200) && (itemTypeId < 300) )
            retval = true;
        else
            retval = false;
        
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.isDocRoutingItem(ddo) return("+obj2String(retval)+")");
        return (retval);
    }//end isDocRoutingItem
   
   /**
    * Determines if the ddo is a resource item or not.                              <BR><BR>
    * @param ddo     - DDO to check if it is a resource item DDO.  Only root components
    *                  will register as resource.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    * @return Returns true if is classified as a resource, false if not.
    **/
    private boolean isResource(DKDDO ddo,TExportPackageICM.Options options) throws DKUsageError, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.isResource("+obj2String(ddo)+")");
        
        // Short-cut:  Just check if it is an instance of DKLobICM, which all
        // resource classes are or a subclass of.
        boolean retval = false;
        if(ddo instanceof DKLobICM)
            retval = true;
        else
            retval = false;

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.isResource(ddo) return("+obj2String(retval)+")");
        return (retval);
    }//end isResource
   /**
    * Determines if the ddo is a root ddo or not.                                   <BR><BR>
    * @param ddo     - DDO to check if it is a root DDO.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    * @return Returns true if root, false if child.
    **/
    private boolean isRoot(DKDDO ddo,TExportPackageICM.Options options) throws DKUsageError, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.isRoot("+obj2String(ddo)+")");

        // Get the datastore definition object.
        DKDatastoreICM    dsICM    = (DKDatastoreICM)    ddo.getDatastore();
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
        verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.

        // Determine if it is a root.
        DKPidICM pidICM = (DKPidICM) ddo.getPidObject();
        String objectType = pidICM.getObjectType();
        DKComponentTypeViewDefICM compTypeViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(objectType);
        boolean retval = compTypeViewDef.isRoot();

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.isRoot(ddo) return("+obj2String(retval)+")");
        return (retval);
    }//end isRoot
   /**
    * Determines if the root DDO's object type (Item Type) is versioned or not.     <BR><BR>
    * @param ddo     - Root DDO to check if it is versioned DDO.  DDO must already have
    *                  passed an isRoot() check.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    * @return Returns true if versioned, false if versioning set to NEVER.
    **/
    private boolean isVersioned(DKDDO ddo,TExportPackageICM.Options options) throws DKUsageError, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.isVersioned("+obj2String(ddo)+")");

        boolean retval = false;

        // Get the datastore definition object.
        DKDatastoreICM    dsICM    = (DKDatastoreICM)    ddo.getDatastore();
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
        verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.
        
        // Get the Item Type Definition.
        String objectType = ddo.getObjectType();
        DKItemTypeViewDefICM itemTypeViewDef = dsDefICM.retrieveItemTypeView(objectType);
        // If versioning set to never, return false, otherwise return true.
        if(itemTypeViewDef.getVersionControl()==DKConstantICM.DK_ICM_VERSION_CONTROL_NEVER)
            retval = false;
        else
            retval = true;

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.isVersioned(ddo) return("+obj2String(retval)+")");
        return (retval);
    }//end isVersioned

   /**
    * For trace and debug information, determine if an object is null or not.
    * Return a description that indicates that the object is valid or "null" if not.  Where
    * applicable, print out more useful information from Link's perspective.
    * Used for printing to the logger.                                              <BR><BR>
    * @param object  Object to check for quick printing
    * @Return  Returns an indication that the object is valid or the text "null" if not.
    **/
    static String obj2String(Object object)
    {
        if (object == null)
            return ("null");
        else if (object instanceof DKDDO)
            return (object.getClass().getName() + "[Item ID '" + ((DKPidICM) ((DKDDO) object).getPidObject()).getItemId() + "' Ver '"+ ((DKPidICM) ((DKDDO) object).getPidObject()).getVersionNumber() + "' (" + ((DKDDO) object).getPidObject().getObjectType() + ")]");
        else if (object instanceof DKFolder)
            return (object.getClass().getName() + "[Size: " + ((DKFolder) object).cardinality() + "]");
        else if (object instanceof DKLinkCollection)
            return (object.getClass().getName() + "[Size: " + ((DKLinkCollection) object).cardinality() + "]");
        else if (object instanceof DKParts)
            return (object.getClass().getName() + "[Size: " + ((DKParts) object).cardinality() + "]");
        else if (object instanceof DKSequentialCollection)
            return (object.getClass().getName() + "[Size: " + ((DKSequentialCollection) object).cardinality() + "]");
        else if (object instanceof dkCollection)
            return (object.getClass().getName() + "[Size: " + ((dkCollection) object).cardinality() + "]");
        else if (object instanceof String)
            return ((String) object);
        else if (object instanceof Hashtable)
            return (object.getClass().getName() + "[Size: " + ((Hashtable) object).size() + "]");
        else if (object instanceof Vector)
            return (object.getClass().getName() + "[Size: " + ((Vector) object).size() + "]");
        else if (object instanceof TreeMap)
            return (object.getClass().getName() + "[Size: " + ((TreeMap) object).size() + "]");
        else if (object instanceof ArrayList)
            return (object.getClass().getName() + "[Size: " + ((ArrayList) object).size() + "]");
        else if (object.getClass().getName().compareTo(TExportPackageICM.Options.class.getName()) == 0)
            return object.toString();
        else if (object.getClass().getName().compareTo(TExportPackageICM.ImportOptions.class.getName()) == 0)
            return object.toString();
        else if (object.getClass().getName().compareTo(TExportPackageICM.ExportOptions.class.getName()) == 0)
            return object.toString();
        else
        {
            return (object.getClass().getName());
        }
    }
    static String obj2String(boolean val){
        if(val) return("true");
        else    return("false");
    }
   /**
    * Remove all unmapped children from the bottom up.  The lowest level must be removed
    * first, then the next level up, and so forth until all childrena are removed.  They
    * must be done incrementally because the cascade delete rule is not guaranteed.
    * If delete rule is restrict, we have to do it from the bottom up.             <BR><BR>
    *
    * Mapping is determined by the importOverExisting() function in ItemInfoPack.  Please
    * refer to that function to see how this function fits in.
    *                                                                              <BR><BR>
    * @param root    - Root DDO for item.
    * @param curDDO  - Current DDO being processed.  If starting from root, this would
    *                  also be the root.  It MUST be a member of the item tree of the
    *                  specified root.
    * @param reSortedImportedChildrenMap
    *                - Must be resorted based on mapped information.
    *                  2-level TreeMap of imported children.  First level
    *                  sorts by component view type name, second sorts
    *                  by componentid.  Contains component DKDDO objects.
    *                  Unmapped children may be omitted, since they are not needed.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private void permanentlyRemoveUnmappedChildren(DKDDO root,DKDDO curDDO,TreeMap<String,TreeMap<String,DKDDO>> reSortedImportedChildrenMap, TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.permanentlyRemoveUnmappedChildren("+obj2String(root)+","+obj2String(curDDO)+","+obj2String(reSortedImportedChildrenMap)+","+obj2String(options)+")");

        ArrayList<DKChildCollection> childCollections = new ArrayList<DKChildCollection>(); // when obtaining data items, collect child collections.

        // List all Data Items
        for(short dataid=1; dataid<=curDDO.dataCount(); dataid++) { // go through all attributes in the ddo
            Object value = null;
            Short  type  = (Short) curDDO.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
            switch(type.intValue()){
                case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                    value = curDDO.getData(dataid);
                    if(value!=null){
                        if(value instanceof DKChildCollection){ // if it is a child collection, add it to a list for printing children later
                            if(options.getPrintDebugEnable()) System.out.println("Found Child Collection of Type '"+((DKChildCollection)value).getAssociatedAttrName()+"'.");
                            childCollections.add((DKChildCollection)value);
                        }
                    }
                    break;
                default:
                    break;
            }// end swith on data item type
        }// end for all data items in DDO

        // Handle Children
        for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
            DKChildCollection childCollection = childCollections.get(i); // get each child collection
            dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
            while(iter.more()){ // While there are children, check each
                DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                permanentlyRemoveUnmappedChildren(root,child,reSortedImportedChildrenMap,options);
            }
            // one done traversing, this routine should now be at the lowest level.
            if(childCollection.cardinality()>0){
                // Remove Each child of this type that isn't mapped.
                iter = childCollection.createIterator();
                boolean somethingWasRemoved = false; // Don't update if nothing changed.
                while(iter.more()){ // while there are more to review, continue
                    DKDDO child = (DKDDO) iter.next(); // Get the next child of this type.
                    String key_compType = ((DKPidICM)child.getPidObject()).getObjectType();  // Get the object type, which is the key to the first level of the TreeMap.
                    String key_compId   = ((DKPidICM)child.getPidObject()).getComponentId(); // Get the component id, which is the key to the map of that type (2nd level of TreeMap).
                    // Determine if the child should be removed
                    boolean removeChild = false;
                    // Access children of this type
                    TreeMap<String,DKDDO> importedChildrenOfTypeMap = reSortedImportedChildrenMap.get(key_compType);
                    // If there are mapped children of this type...
                    if((importedChildrenOfTypeMap!=null)&&(importedChildrenOfTypeMap.size()>0)){
                        // Now check to see if it is maped
                        if(importedChildrenOfTypeMap.containsKey(key_compId)){
                            removeChild = false;
                        } else // Otherwise it isn't mapped, so remove.
                            removeChild = true;
                    } else // Otherwise if no children are found of this type, just remove
                        removeChild = true;
                        
                    if(removeChild){ // Remove child from collection
                        childCollection.removeElementAt(iter); // Remove
                        somethingWasRemoved = true;            // Note that something was removed.
                        if(options.getPrintDebugEnable()) System.out.println("  Removed Unmapped child '"+key_compId+"' of type '"+key_compType+"'.");
                        // Get new iterator since last was invalidated.
                        iter = childCollection.createIterator();
                    } else { // otherwise it was mapped.
                        if(options.getPrintDebugEnable()) System.out.println("  Found mapped child '"+key_compId+"' of type '"+key_compType+"'.");
                    }
                    
                }//end while(iter.more(){
                
                try{
                    // Only call update if at least one child was removed.
                    if(somethingWasRemoved){
                        root.update();                    // May get min cardinality errors, which is okay.  Leave them since if delete rule cascade, one of the higher levels will take it off too.
                    }
                } catch(DKException exc){
                    if(options.getPrintDebugEnable()) System.out.println("WARNING:  Possible error encountered when removing children from bottom up.  Cardinality minimum cardinality violation errors are expected.  As long as the delete rule is 'CASCADE', it will automatically be removed as this algorithm continues up the tree.");
                    if(options.getPrintDebugEnable()) System.out.println("  Error:  "+exc.getMessage());
                    SConnectDisconnectICM.printException(exc);
                    throw (exc);
                }
            }
        }

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.permanentlyRemoveUnmappedChildren("+obj2String(root)+","+obj2String(curDDO)+","+obj2String(reSortedImportedChildrenMap)+","+obj2String(options)+")");
    }
   /**
    * Clear all mapped component Pid Info in the version Component Mapping.  This is
    * intended for use before & after the entire set of items are imported.         <BR><BR>
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private void clearComponentPidInfo(TExportPackageICM.Options options){
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.clearComponentPidInfo("+obj2String(options)+")");

        // Clear if needed.
        if(_verComponentMap.size() > 0){
            if(options.getPrintDebugEnable()) System.out.println("  Clearing '"+_verComponentMap.size()+"' Versioned Component Mappings...");
            _verComponentMap.clear();
        }else{
            if(options.getPrintDebugEnable()) System.out.println("  Nothing to clear.  No versioned component mappings exist.");
        }

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.clearComponentPidInfo("+obj2String(options)+")");
    }

   /**
    * Clear the register of selected items.  After all items have been processed in a
    * collection/query, the register tracking those that may have not yet been processed
    * may be cleared.                                                               <BR><BR>
    * @param ddo     - Root DDO for item.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private void clearRegister(TExportPackageICM.Options options){
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.clearRegister("+obj2String(options)+")");

        _preRegisterTree.clear();

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.clearRegister(options)");
    }    
   /**
    * Clear all restart tracking information.  This is intended for use before &
    * after the entire set of items are imported through restart mode.              <BR><BR>
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private void clearRestartTracking(TExportPackageICM.Options options){
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.clearRestartTracking("+obj2String(options)+")");

        // Clear if needed.
        if(_restartP1CmpltItemMap.size() > 0)
            _restartP1CmpltItemMap.clear();
        if(_restartP1OmitItemMap.size() > 0)
            _restartP1OmitItemMap.clear();
        if(_restartP2CmpltItemMap.size() > 0)
            _restartP2CmpltItemMap.clear();
        if(_restartP2OmitItemMap.size() > 0)
            _restartP2OmitItemMap.clear();
        if(_restartP3CmpltItemMap.size() > 0)
            _restartP3CmpltItemMap.clear();
        if(_restartP3OmitItemMap.size() > 0)
            _restartP3OmitItemMap.clear();

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.clearRestartTracking("+obj2String(options)+")");
    }

   /**
    * Since minimum cardinality can be violated when removing children from bottom up, 
    * errors are possible.  As long as the delete rules on those children were 'CASCADE',
    * all children would have still been removed.  However, this function will serve  
    * the final check to make sure that absolutely no children remain.              <BR><BR>
    * @param ddo     - Root DDO for item.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private void doubleCheckRemoveAllChildren(DKDDO ddo,TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.doubleCheckRemoveAllChildren("+obj2String(ddo)+","+obj2String(options)+")");

        ArrayList<DKChildCollection> childCollections = new ArrayList<DKChildCollection>(); // when obtaining data items, collect child collections.

        // List all Data Items
        for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
            Object value = null;
            Short  type  = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
            switch(type.intValue()){
                case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                    value = ddo.getData(dataid);
                    if(value!=null){
                        if(value instanceof DKChildCollection){ // if it is a child collection, add it to a list for printing children later
                            if(options.getPrintDebugEnable()) System.out.println("Found Child Collection of Type '"+((DKChildCollection)value).getAssociatedAttrName()+"'.");
                            childCollections.add((DKChildCollection)value);
                        }
                    }
                    break;
                default:
                    break;
            }// end swith on data item type
        }// end for all data items in DDO
        
        // Handle Children
        for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
            DKChildCollection childCollection = childCollections.get(i); // get each child collection
            dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
            while(iter.more()){ // While there are children, print each
                DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                // Since a child exists that shouldn't throw error.
                throw new Exception("All children could not be successfully removed.  This is probably because some child had a minimum cardinality > 0, but did not have delete rule set to 'CASCADE'.  Please turn on debug printing in the options object and review the warnings displayed.");
            }
        }

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.doubleCheckRemoveAllChildren("+obj2String(ddo)+","+obj2String(options)+")");
    }
   /**
    * Process a started item -- subroutine of readImportOperationsTrackingFile().   <BR><BR>
    * @param startedItem    - Started ItemInfoPack or 'null' if none started.
    * @param currentPass    - Current pass that we are processing (0-3).  '0' will
    *                         result in an error since we need to know which pass it
    *                         was in.
    * @param skipIncomplete - Whether or not to omit or try to re-import.  If 'true',
    *                         mark it to be omitted, else do not mark to omit so that
    *                         it will be re-tried.
    * @param currentItem    - Current item being processed to compare against started
    *                         item.  If they are not the same, the started item should
    *                         then be marked as incomplete.
    * @param line           - Current line of the tracking file read for error reporting reasons.
    * @param options        - (Required) Options specified in a TExportPackageICM.Options
    *                         object.  Null not accepted in private methods.
    **/
    private void processStartedItem(ItemInfoPack startedItem, int currentPass, boolean skipIncomplete,ItemInfoPack currentItem,String line,TExportPackageICM.Options options) throws Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.processStartedItem("+obj2String(startedItem)+","+currentPass+","+obj2String(skipIncomplete)+","+obj2String(currentItem)+",line["+line.length()+"],"+obj2String(options)+")");

        // If we are to skip incomplete, mark it to be omitted:
        if(skipIncomplete){
            //Only process if there was a started item.
            if(startedItem!=null){
                // If there is no current item or If the started item is not the current
                // item, mark as incomplete.
                if( (currentItem==null) || (startedItem.getKey().compareTo(currentItem.getKey())!=0) ){
                    if(currentPass==0)
                        throw new Exception("No pass start tag identifier found before operation line.  Either no start tag exists in file before this line or a pass end tag was specified with no new begin pass tag between that end tag and this line.  Line: '"+line+"'.");
                    else if(currentPass==1){
                        if(_restartP1OmitItemMap.containsKey(startedItem.getKey())==false)     // if it is not yet added, add it.
                            _restartP1OmitItemMap.put(startedItem.getKey(),startedItem);
                        // Any item omitted from pass 1 must be omitted from passes 2 & 3
                        if(_restartP2OmitItemMap.containsKey(startedItem.getKey())==false)     // if it is not yet added, add it.
                            _restartP2OmitItemMap.put(startedItem.getKey(),startedItem);
                        if(_restartP3OmitItemMap.containsKey(startedItem.getKey())==false)     // if it is not yet added, add it.
                            _restartP3OmitItemMap.put(startedItem.getKey(),startedItem);
                    } else if(currentPass==2){
                        if(_restartP2OmitItemMap.containsKey(startedItem.getKey())==false)     // if it is not yet added, add it.
                            _restartP2OmitItemMap.put(startedItem.getKey(),startedItem);
                        // Any item omitted from pass 2 must be omitted from pass 3
                        if(_restartP3OmitItemMap.containsKey(startedItem.getKey())==false)     // if it is not yet added, add it.
                            _restartP3OmitItemMap.put(startedItem.getKey(),startedItem);
                    } else if(currentPass==3){
                        if(_restartP3OmitItemMap.containsKey(startedItem.getKey())==false)     // if it is not yet added, add it.
                            _restartP3OmitItemMap.put(startedItem.getKey(),startedItem);
                    } else
                        throw new Exception("Internal Error:  Unexpected 'currentPass' value '"+currentPass+"'.  Expected value 0 through 3.");
                }//end if( (currentItem==null) || (startedItem.getKey().compareTo(currentItem.getKey()!=0) ){
            }//end if(startedItem!=null){
        }//end if(skipIncomplete){

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.processStartedItem("+obj2String(startedItem)+","+currentPass+","+obj2String(skipIncomplete)+","+obj2String(currentItem)+",line["+line.length()+"],"+obj2String(options)+")");
    }
   /**
    * Add the data from the specified export package file to this export package's
    * information.  Multiple sets of exported information can be stored in this object.
    * Duplicate information will be consolidated.                                   <BR><BR>
    * @param centralFileName - Central Export Package file responsible an entire
    *                          saved package state and all files needed.
    * @param importOptions   - (Optional) Import Options specified in a TExportPackageICM.ImportOptions
    *                          object.  Use 'null' for defaults.
    *                          Please refer to the ImportOptions constructor information on
    *                          default settings.
    **/
    public void read(String centralFileName,TExportPackageICM.ImportOptions importOptions) throws DKException, Exception{
        if(importOptions==null) // If no options specified, use defaults.
            importOptions = new TExportPackageICM.ImportOptions();
        if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.read(centralFileName="+centralFileName+","+obj2String(importOptions)+")");

        // Get File Name Parts
        String filePath     = getFilePath(centralFileName,importOptions);
        // If no path specified, set to current path
        if(filePath==null){
            Properties props = System.getProperties();
            filePath = props.getProperty("user.dir") + '/';
        }//end if(filePath==null){
        
        // Read Central Package File
        readCentralPackageFile(filePath,centralFileName,importOptions);

        if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.read(centralFileName="+centralFileName+","+obj2String(importOptions)+")");
    }
   /**
    * Add the data from the specified export package file to this export package's
    * information.  Multiple sets of exported information can be stored in this object.
    * Duplicate information will be consolidated.                                   <BR><BR>
    * @param filePath         - Fully-specified path of the location where the package
    *                           is to be written.
    * @param centralFileName  - Central Export Package file responsible an entire
    *                           saved package state and all files needed.
    * @param importOptions    - (Required) Import Options specified in a TExportPackageICM.ImportOptions
    *                           object.  Null not accepted in private methods.
    **/
    private void readCentralPackageFile(String filePath, String centralFileName, TExportPackageICM.ImportOptions importOptions) throws DKException, Exception{
        if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.readCentralPackageFile(filePath="+filePath+",centralFileName="+centralFileName+","+obj2String(importOptions)+")");

        // Open File
        FileReader fileReader = new FileReader(centralFileName);
        BufferedReader file   = new BufferedReader(fileReader);

        // Read & Validate File Identifier & Version Check
        String line = file.readLine();
        if(!line.startsWith(T_ICM_EXPORT_PACKAGE_FILE_IDENTIFIER))
            throw new Exception("File specified, '"+centralFileName+"', does not appear to be an Export Package file.");
        String fileVersion = line.substring(line.lastIndexOf('v')+1);
        if(fileVersion.compareTo(T_ICM_EXPORT_PACKAGE_VERSION) != 0)
            if(importOptions.getAnswer_isFileVersionWarning_exception(fileVersion))
                throw new Exception("WARNING:  Export Package File was not exported from the same version of this tool.  File is from version '"+fileVersion+"' version, but the current version is '"+T_ICM_EXPORT_PACKAGE_VERSION+"'.  The old format may not be compatable with the new format.  To continue, please set the enableVersionWarning flag to false.");

        // Create Blank Info Packs to use for Pack Type information.
        ItemInfoPack    blankItemInfoPack    = new ItemInfoPack(false,null,null,null);
        FolderInfoPack  blankFolderInfoPack  = new FolderInfoPack((String)null,(String)null,(String)null,(String)null);
        LinkInfoPack    blankLinkInfoPack    = new LinkInfoPack(null,null,null,null,null,null,(String)null);
        RefAttrInfoPack blankRefAttrInfoPack = new RefAttrInfoPack(null,null,null,null,null,null);

        // Read File Line-by-line, handling each package as found.
        while((line = file.readLine())!=null){ // Continue until reach end of file.
            if(importOptions.getPrintDebugEnable()) System.out.println("  Reading Line:  "+line);
            // Item Info Packs
            if(line.startsWith(blankItemInfoPack.getPackType())){
            	
                ItemInfoPack itemInfoPack = new ItemInfoPack(filePath,line);
                if(_itemInfoTree.containsKey(itemInfoPack.getKey())==false)     // if it is not yet added, add it.
                    _itemInfoTree.put(itemInfoPack.getKey(),itemInfoPack);
            } // Folder Info Packs
            else if(line.startsWith(blankFolderInfoPack.getPackType())){
                FolderInfoPack folderInfoPack = new FolderInfoPack(line);
                if(_folderInfoHT.containsKey(folderInfoPack.getKey())==false)   // if it is not yet added, add it.
                    _folderInfoHT.put(folderInfoPack.getKey(),folderInfoPack);
            } // Link Info Packs
            else if(line.startsWith(blankLinkInfoPack.getPackType())){
                LinkInfoPack linkInfoPack = new LinkInfoPack(line);
                if(_linksInfoHT.containsKey(linkInfoPack.getKey())==false)      // if it is not yet added, add it.
                    _linksInfoHT.put(linkInfoPack.getKey(),linkInfoPack);
            } // Ref Attr Info Packs
            else if(line.startsWith(blankRefAttrInfoPack.getPackType())){
                RefAttrInfoPack refAttrInfoPack = new RefAttrInfoPack(line);
                if(_refAttrInfoHT.containsKey(refAttrInfoPack.getKey())==false) // if it is not yet added, add it.
                    _refAttrInfoHT.put(refAttrInfoPack.getKey(),refAttrInfoPack);
            }
        }

        // Close the file
        file.close();

        if(importOptions.getPrintDebugEnable()) System.out.println("          # Items:  "+_itemInfoTree.size());
        if(importOptions.getPrintDebugEnable()) System.out.println("# Folder Contents:  "+_folderInfoHT.size());
        if(importOptions.getPrintDebugEnable()) System.out.println("          # Links:  "+_linksInfoHT.size());
        if(importOptions.getPrintDebugEnable()) System.out.println("# Ref Attr Values:  "+_refAttrInfoHT.size());
        if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.readCentralPackageFile(filePath="+filePath+",centralFileName="+centralFileName+","+obj2String(importOptions)+")");
    }
   /**
    * Add the data from the specified import operations tracking file to the restart
    * maps.  This data contains the information of completed and omitted items from a   
    * previous import process.  All items that were completed will appear on the completed
    * restart maps so that they may be skipped later by the importItems() operation.  If
    * the skipIncomplete option is 'false', all previously omitted items will be ignored
    * in this operation so that they will be attempted for import again.  If skipIncomplete
    * is 'true', any previously marked as omitted or any that are started but not completed
    * will be tracked in the restart 'omit' maps and will be omitted by the importItems()
    * operation.  Multiple tracking files may be combined by an external tool as long
    * as the versions are identical.                                                <BR><BR>
    *
    * <u>File Syntax</u>                                                                <BR>
    *       Please refer to the documentation in TExportPackageICM.restartImport().
    *                                                                               <BR><BR>
    * @param trackingFileName - Import Operation Tracking File saved from a call to 
    *                           a previous importItems() or restartImport() call.  
    *                           The file is created according to the 
    *                           TExportPackageICM.ImportOptions object according
    *                           to its tracking file setting.
    * @param skipIncomplete   - Skip any items that were started, but were not completed.
    *                           This is best used to ignore / omit the last item that failed.
    * @param importOptions    - (Required) Import Options specified in a TExportPackageICM.ImportOptions
    *                           object.  Null not accepted in private methods.
    **/
    private void readImportOperationsTrackingFile(String trackingFileName, boolean skipIncomplete, TExportPackageICM.ImportOptions importOptions) throws DKException, Exception{
        if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.readImportOperationsTrackingFile("+trackingFileName+","+obj2String(skipIncomplete)+","+obj2String(importOptions)+")");

        // Open File
        FileReader fileReader = new FileReader(trackingFileName);
        BufferedReader file   = new BufferedReader(fileReader);

        // Read & Validate File Identifier & Version Check
        String line = file.readLine();
        if(!line.startsWith(T_ICM_EXPORT_PACKAGE_TRACKING_FILE_IDENTIFIER))
            throw new Exception("File specified, '"+trackingFileName+"', does not appear to be an Import Tracking File.  The file specified does not have the appropriate identifier tag.");
        String fileVersion = line.substring(line.lastIndexOf('v')+1);
        if(fileVersion.compareTo(T_ICM_EXPORT_PACKAGE_VERSION) != 0)
            if(importOptions.getAnswer_isFileVersionWarning_exception(fileVersion))
                throw new Exception("WARNING:  Import Tracking File was not created by the same version of this tool.  File is from version '"+fileVersion+"' version, but the current version is '"+T_ICM_EXPORT_PACKAGE_VERSION+"'.  The old format may not be compatable with the new format.  To continue, please set the enableVersionWarning flag to false.");

        // Create Blank Info Packs to use for Pack Type information.
        ItemInfoPack    blankItemInfoPack    = new ItemInfoPack(false,null,null,null);
        //NOT IN USE: The following 3 lines are currently not needed since the restart capability
        //NOT IN USE: currently is does not cover the actual application of these packs to the items
        //NOT IN USE: in memory in phase 2.
        //NOT IN USE:FolderInfoPack  blankFolderInfoPack  = new FolderInfoPack((String)null,(String)null,(String)null,(String)null);
        //NOT IN USE:LinkInfoPack    blankLinkInfoPack    = new LinkInfoPack(null,null,null,null,null,null,(String)null);
        //NOT IN USE:RefAttrInfoPack blankRefAttrInfoPack = new RefAttrInfoPack(null,null,null,null,null,null);

        // Read File Line-by-line, handling each package as found.
        // The file will be read from top to bottom, paying attention to which pass
        // identifier was last processed.  Depending on which pass (1 through 3) 
        // determines which restart map each is added to.
        int currentPass = 0;  // Current Pass being read in from the file.  Every time
                              // a pass tag is read in, the current pass is set to the
                              // specified pass.
        ItemInfoPack startedItem = null; // Track any item that was started so we will
                                         // know if something was started and not completed.
        while((line = file.readLine())!=null){ // Continue until reach end of file.
            if(importOptions.getPrintDebugEnable()) System.out.println("  Reading Line:  "+line);
            // Process Line Possibilities
            if(line.trim().compareTo("")==0){  // Blank Line
                // Do nothing   
            }else if(line.startsWith("#")){    // Comment
                // Do nothing                
            }else if(line.startsWith("//")){   // Comment
                // Do nothing                
            // Check for (and Process) Pass Tags
            }else if(line.startsWith(T_ICM_EXPORT_PACKAGE_TRACKING_PASS1_TAG_BEGIN)){
                processStartedItem(startedItem,currentPass,skipIncomplete,null,line,importOptions);
                startedItem = null; // Reset started item since it is a new section.
                currentPass = 1;                        
            }else if(line.startsWith(T_ICM_EXPORT_PACKAGE_TRACKING_PASS2_TAG_BEGIN)){
                processStartedItem(startedItem,currentPass,skipIncomplete,null,line,importOptions);
                startedItem = null; // Reset started item since it is a new section.
                currentPass = 2;
            }else if(line.startsWith(T_ICM_EXPORT_PACKAGE_TRACKING_PASS3_TAG_BEGIN)){
                processStartedItem(startedItem,currentPass,skipIncomplete,null,line,importOptions);
                startedItem = null; // Reset started item since it is a new section.
                currentPass = 3;
            }else if(line.startsWith(T_ICM_EXPORT_PACKAGE_TRACKING_PASS1_TAG_END)){
                processStartedItem(startedItem,currentPass,skipIncomplete,null,line,importOptions);
                startedItem = null; // Reset started item since it is an end of a section.
                currentPass = 0;
            }else if(line.startsWith(T_ICM_EXPORT_PACKAGE_TRACKING_PASS2_TAG_END)){
                processStartedItem(startedItem,currentPass,skipIncomplete,null,line,importOptions);
                startedItem = null; // Reset started item since it is an end of a section.
                currentPass = 0;
            }else if(line.startsWith(T_ICM_EXPORT_PACKAGE_TRACKING_PASS3_TAG_END)){
                processStartedItem(startedItem,currentPass,skipIncomplete,null,line,importOptions);
                startedItem = null; // Reset started item since it is an end of a section.
                currentPass = 0;
            // Else it is either an option line or invalid information that should
            }else{
                // Process Operation Lines
                String operation   = line.substring(0,T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_TAG_LEN);
                String infoPackStr = line.substring(T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_TAG_LEN);
                if(importOptions.getPrintDebugEnable()) System.out.println("  Operation:  "+operation);
                if(importOptions.getPrintDebugEnable()) System.out.println("  Info Pack:  "+infoPackStr);
                if(operation.compareToIgnoreCase(T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_STARTING)==0){
                    if(infoPackStr.startsWith(blankItemInfoPack.getPackType())){
                        ItemInfoPack itemInfoPack = new ItemInfoPack("THIS_PACK_FOR_COMPARISON_ONLY",infoPackStr);
                        // Mark any started item as no longer started since this is another
                        // started item.
                        processStartedItem(startedItem,currentPass,skipIncomplete,null,line,importOptions);
                        // Mark this item as the new started item.
                        startedItem=itemInfoPack;
                    }//end if(infoPackStr.startsWith(blankItemInfoPack.getPackType())){
                } else if(operation.compareToIgnoreCase(T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE)==0){
                    // Mark any operation with the complete operation as complete for any
                    // restart-supported item info packs:
                    // Item Info Packs
                    if(infoPackStr.startsWith(blankItemInfoPack.getPackType())){
                        ItemInfoPack itemInfoPack = new ItemInfoPack("THIS_PACK_FOR_COMPARISON_ONLY",infoPackStr);
                        // Mark any started item as no longer started, but make sure
                        // that this is the started item.  If not, mark the previously
                        // started item as incomplete.
                        processStartedItem(startedItem,currentPass,skipIncomplete,itemInfoPack,line,importOptions);
                        startedItem=null; // There is no started item now.
                        // Depending on pass #, place into correct restart map.
                        if(currentPass==0)
                            throw new Exception("No pass start tag identifier found before operation line.  Either no start tag exists in file before this line or a pass end tag was specified with no new begin pass tag between that end tag and this line.  Line: '"+line+"'.");
                        else if(currentPass==1){
                            if(_restartP1CmpltItemMap.containsKey(itemInfoPack.getKey())==false)     // if it is not yet added, add it.
                                _restartP1CmpltItemMap.put(itemInfoPack.getKey(),itemInfoPack);
                        } else if(currentPass==2){
                            if(_restartP2CmpltItemMap.containsKey(itemInfoPack.getKey())==false)     // if it is not yet added, add it.
                                _restartP2CmpltItemMap.put(itemInfoPack.getKey(),itemInfoPack);
                        } else if(currentPass==3){
                            if(_restartP3CmpltItemMap.containsKey(itemInfoPack.getKey())==false)     // if it is not yet added, add it.
                                _restartP3CmpltItemMap.put(itemInfoPack.getKey(),itemInfoPack);
                        } else
                            throw new Exception("Internal Error:  Unexpected 'currentPass' value '"+currentPass+"'.  Expected value 0 through 3.");
                    }//end if(infoPackStr.startsWith(blankItemInfoPack.getPackType())){
                } else if(operation.compareToIgnoreCase(T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_OMITTING)==0){
                    if(infoPackStr.startsWith(blankItemInfoPack.getPackType())){
                        ItemInfoPack itemInfoPack = new ItemInfoPack("THIS_PACK_FOR_COMPARISON_ONLY",infoPackStr);
                        // Mark any started item as no longer started, but make sure
                        // that this is the started item.  If not, mark the previously
                        // started item as incomplete.
                        processStartedItem(startedItem,currentPass,skipIncomplete,itemInfoPack,line,importOptions);
                        startedItem=null; // There is no started item now.
                        // If we are to skip omitted, track them here.  If we are to 
                        // reattempt import of them, do not track so they appear to have
                        // never been started.
                        // Item Info Packs
                        if(skipIncomplete==false){
                            // Depending on pass #, place into correct restart map.
                            // --> Same algorithm as processStartedItem(), therefore will
                            //     re-use that function, using the current item as if it
                            //     were the started item.
                            processStartedItem(itemInfoPack,currentPass,skipIncomplete,null,line,importOptions);
                        }//end if(skipIncomplete==false){
                    }//end if(infoPackStr.startsWith(blankItemInfoPack.getPackType())){
                }else if(operation.compareToIgnoreCase(T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_NONE)==0){
                    // Do Nothing
                }else{ // invalid operation
                    throw new Exception("An unknown operation name '"+operation+"' was found.  Please refer to the TExportPackageICM.restartImport() Javadoc for correct file syntax.");   
                }
            }
        }//end while((line = file.readLine())!=null){
        // If file unexpectedly ended, process any remaining incomplete item(s)
        // as incomplete.
        processStartedItem(startedItem,currentPass,skipIncomplete,null,"ABRUPT_END_OF_FILE",importOptions);
        startedItem=null; // There is no started item now.

        // Lastly, any items completed in pass 1 but not completed in pass 2, but not
        // omitted in pass 2, that have reference attribute packs associated with them
        // cannot be marked as complete in pass 1.  They must be redone.
        Collection<ItemInfoPack> pass1CompletedAsColl = _restartP1CmpltItemMap.values();
        Iterator<ItemInfoPack>   pass1CompletedAsIter = pass1CompletedAsColl.iterator();
        while(pass1CompletedAsIter.hasNext()){
            ItemInfoPack itemInfoPack = pass1CompletedAsIter.next();
            // If not complete in pass 2
            if(!_restartP2CmpltItemMap.containsKey(itemInfoPack.getKey())){
                // and If not omitted in pass 2
                if(!_restartP2OmitItemMap.containsKey(itemInfoPack.getKey())){
                    // Compare to every refAttrInfoPack.
                    Enumeration<RefAttrInfoPack> refAttrPacksAsEnum = _refAttrInfoHT.elements();
                    while(refAttrPacksAsEnum.hasMoreElements()){
                        RefAttrInfoPack refAttrInfoPack = refAttrPacksAsEnum.nextElement();
                        // If found
                        if(refAttrInfoPack.isOriginalTarget(itemInfoPack.getOriginalItemId(),itemInfoPack.getVersionId())){
                            // remove from completed in pass 1
                            _restartP1CmpltItemMap.remove(itemInfoPack.getKey());
                            // restart iter. 
                            pass1CompletedAsIter = pass1CompletedAsColl.iterator();
                        }//end if(refAttrInfoPack.isOriginalTarget(itemInfoPack.getOriginalItemId(),itemInfoPack.getVersionId()){
                    }//end while(refAttrPacksAsEnum.hasMoreElements()){
                }//end if(!_restartP2OmitItemMap.hasKey(itemInfoPack.getKey())){
            }//end if(!_restartP2CmpltItemMap.hasKey(itemInfoPack.getKey())){
        }//end while(pass1CompletedAsIter.hasNext()){

        // Close the file
        file.close();

        if(importOptions.getPrintDebugEnable()) System.out.println("          # Items:  "+_itemInfoTree.size());
        if(importOptions.getPrintDebugEnable()) System.out.println("# Folder Contents:  "+_folderInfoHT.size());
        if(importOptions.getPrintDebugEnable()) System.out.println("          # Links:  "+_linksInfoHT.size());
        if(importOptions.getPrintDebugEnable()) System.out.println("# Ref Attr Values:  "+_refAttrInfoHT.size());
        if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.readImportOperationsTrackingFile("+trackingFileName+","+obj2String(skipIncomplete)+","+obj2String(importOptions)+")");
    }
    
   /**
    * Recreate an ItemInfoPack from the string written by ItemInfoPack.toString()
    * @param filePath - current file path of the xml file location for this item.
    * @param dataStr  - Data string in the exact format returned by toString().
    **/
    ItemInfoPack recreateItemInfoPack(String filePath, String dataStr) throws Exception{
        ItemInfoPack itemInfoPack = new ItemInfoPack(filePath,dataStr);
        return(itemInfoPack);
    }//end recreateItemInfoPack()
    
   /**
    * Pre-register selected items when a collection of items are to be added.  This allows
    * the single add item function that will be called for each to be aware of other
    * items that have not yet been processed.  This helps in determining which versions
    * have not been selected for import.                                            <BR><BR>
    * @param ddo     - Root DDO for item.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private void registerSelected(DKDDO ddo,TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.registerSelected("+obj2String(ddo)+","+obj2String(options)+")");

        // Only register if not already registered.
        if(isRegistered(ddo,options)==false){
            if(options.getPrintDebugEnable()) System.out.println("   Item has not yet been pre-registered.");
            
            // Add ItemInfo Package
            ItemInfoPack itemInfoPack = new ItemInfoPack(false,ddo);
            _preRegisterTree.put(itemInfoPack.getKey(),itemInfoPack);
        } else
            if(options.getPrintDebugEnable()) System.out.println("   Item is already pre-registered.");
        
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.registerSelected("+obj2String(ddo)+","+obj2String(options)+")");
    }
   /**
    * Release resource and parts contents in the existing ddo specified.  This allows
    * the existing ddo to drop these references & memory until it is needed.  This ability
    * is required for scaleability.                                                 <BR><BR>
    * @param ddo     - Root DDO for item.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private void releaseResourceAndPartContentFromDDO(DKDDO ddo, TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.releaseResourceAndPartContentFromDDO("+obj2String(ddo)+","+obj2String(options)+")");
        
        if(isResource(ddo,options)){
            // Release resoruce content.
            ((DKLobICM)ddo).setContent(null);
        } else if(isDocModel(ddo,options)){
            // Remove Parts Collection
            short ddoDataId = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
                    
            if(ddoDataId==0)
                throw new Exception("Internal Error:  No DKParts Attribute Found in DDO even though it is a document model item.  DDO is either not an Item of a Document Model classified Item Type or Document has not been explicitly retrieved.");

            ddo.setData(ddoDataId,null);
        }

        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.releaseResourceAndPartContentFromDDO("+obj2String(ddo)+","+obj2String(options)+")");
    }
   /** <pre>
    * Restart a previous import operation that failed or did not complete.  The operation
    * may restart where the previous operation left off or skip the failed / incomplete
    * step.                                                                         
    * 
    * The same Export Package must be loaded along with the Import Operations Tracking
    * File (*.trk) that was generated from that import process, specified by the 
    * TExportPackageICM.ImportOptions object's tracking file setting.  Please refer to
    * its setTrackingFile() Javadoc for more information.                           
    *
    * The importItems() function will be reissued in restart mode.  Please refer to the
    * importItems() Javadoc for information on the import operation.                
    *
    * Failed or incomplete past import or restart operations may be restarted from where
    * they left off or skip the failed/incomplete items.                            
    *
    * Any items marked as completed in each phase of that file will not be attempted to
    * be imported.  By default all incomplete and items not started for each phase of
    * the import process will be restarted in each respective phase.                 
    *
    * The skipIncomplete option value of 'true' will result in omission of all
    * incomplete items which will enable those items to be skipped in that phase and all
    * future phases in which that item was started by not completed.                
    * 
    * FILE SYNTAX: The tracking file contains information on started, completed, failed,
    *              omitted, and incomplete import operations for each of the three passes
    *              of the import operation.
    *
    *              The file is started with the Tracking File Identifier Tag,
    *              TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_FILE_IDENTIFIER followed
    *              by " v<Export Package Version>".  The version is the version of the
    *              export package that wrote the file during a previous importItem() or
    *              restartImport() operation.  That version is compared against the current
    *              version of the tool, TExportPackageICM.T_ICM_EXPORT_PACKAGE_VERSION,
    *              and a warning may be thrown depending on the import options if they do
    *              not match.
    *
    *              Next, in any order, regardless of duplicate entries, or split sections,
    *              pass tag identifiers indicate which of the three passes the following
    *              information contains.  Only the beginning tag is necessary, which directs
    *              the read operation to know which pass each line entry pertains to.
    *              For example, a file may contain tags in this order: pass1,pass3,pass2,
    *              pass2,pass1.  End tags are not necessary, but if specified the function
    *              will not accept any input before a new start tag is specified.  An
    *              exception will always be thrown if data is started before a begin tag
    *              is found.
    *              
    *                  Required Pass Tags:
    *                      TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS1_TAG_BEGIN
    *                      TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS2_TAG_BEGIN
    *                      TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS3_TAG_BEGIN
    *                  Optional Pass Tags:
    *                      TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS1_TAG_END
    *                      TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS2_TAG_END
    *                      TExportPackageICM.T_ICM_EXPORT_PACKAGE_TRACKING_PASS3_TAG_END
    *
    *              Within each pass section, line entries of itemInfoPack information
    *              for items that were processed during each pass are indicated, starting
    *              with the operation performed.  Operation tags are case-insensitive.  
    *
    *                  Operation Tag Length:
    *                       TExportPacageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_TAG_LEN
    *                  Operation Tags:
    *                       TExportPacageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_NONE
    *                       TExportPacageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_STARTING
    *                       TExportPacageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_COMPLETE
    *                       TExportPacageICM.T_ICM_EXPORT_PACKAGE_TRACKING_OPERATION_OMITTING
    *
    *               Any Started tag not followed by a complete tag is considered an
    *               incomplete import operation.  Any omitted tag is considered an
    *               incomplete operation.  Any complete tag is the only tag representing
    *               a completed operation.
    *
    *               Comments are any lines that begin with "//", '#', or start with the 
    *               NONE operation tag.
    * </pre>                                                                        <BR><BR>
    * @param trackingFileName - Import Operation Tracking File saved from a call to 
    *                           a previous importItems() or restartImport() call.  
    *                           The file is created according to the 
    *                           TExportPackageICM.ImportOptions object according
    *                           to its tracking file setting.
    * @param skipIncomplete   - Skip any items that were started, but were not completed.
    *                           This is best used to ignore / omit the last item that failed.
    * @param targetDS         - Target datastore to import items to.
    * @param importOptions    - (Optional) Import Options specified in a TExportPackageICM.ImportOptions
    *                           object.  Use 'null' for defaults.
    *                           Please refer to the ImportOptions constructor information on
    *                           default settings.
    * @return Returns an array of the TExportPackageICM.ImportRecord objects providing access
    *         to the imported items, Item Id mapping, and more.
    **/
    public synchronized TExportPackageICM.ImportRecord[] restartImport(String trackingFileName, boolean skipIncomplete, DKDatastoreICM targetDS,TExportPackageICM.ImportOptions importOptions) throws DKException, Exception{
        if(importOptions==null) // If no options specified, use defaults.
            importOptions = new TExportPackageICM.ImportOptions();
        if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.restartImport("+trackingFileName+","+obj2String(skipIncomplete)+","+obj2String(targetDS)+","+obj2String(importOptions)+")");
        verifyConnectedDatastore(targetDS); // Validate that the datastore is connectected.

        // Clear existing restart information
        clearRestartTracking(importOptions);

        // Load the tracking file
        readImportOperationsTrackingFile(trackingFileName,skipIncomplete,importOptions);
        
        // Call importItems which will run based on these settings.
        TExportPackageICM.ImportRecord[] importRecords = importItems(targetDS,true,importOptions);

        // Free memory no longer needed.
        clearRestartTracking(importOptions);

        if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.restartImport("+trackingFileName+","+obj2String(skipIncomplete)+","+obj2String(targetDS)+","+obj2String(importOptions)+")");
        return(importRecords);
    }
   /**
    * Retrieve resource and parts contents into the existing ddo specified.  This allows
    * the existing ddo to drop these references & memory until it is needed.  This ability
    * is required for scaleability.                                                 <BR><BR>
    * @param ddo     - Root DDO for item.
    * @param options - (Required) Options specified in a TExportPackageICM.Options
    *                  object.  Null not accepted in private methods.
    **/
    private void retrieveResourceAndPartContentIntoExistingDDO(DKDDO ddo, TExportPackageICM.Options options) throws DKException, Exception{
        if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.retrieveResourceAndPartContentIntoExistingDDO("+obj2String(ddo)+","+obj2String(options)+")");
        
        // Get Resource Content & Parts
        boolean resource = false;
        boolean docModel = false;
        if( (resource = isResource(ddo,options)) ||(docModel = isDocModel(ddo,options)) ){
            DKDatastoreICM dsICM = (DKDatastoreICM) ddo.getDatastore();
            verifyConnectedDatastore(dsICM); // Validate that the datastore is still connectected.
            DKDDO tempDDO = dsICM.createDDOFromPID((DKPidICM)ddo.getPidObject());
            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            dkRetrieveOptions.partsList(true);
            dkRetrieveOptions.partsAttributes(true);
            dkRetrieveOptions.partsPropertyAclName(true);
            dkRetrieveOptions.resourceContent(true);
            tempDDO.retrieve(dkRetrieveOptions.dkNVPair());
                
            if(resource){
                // Copy the resoruce content into the DDO we are going to export.
                ((DKLobICM)ddo).setContent(((DKLobICM)tempDDO).getContent());
            } else if(docModel){
                // Copy all parts to the DDO we are going to export.
                short ddoDataId     = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
                short tempDDODataId = tempDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
                
                if(tempDDODataId==0)
                    throw new Exception("Internal Error:  No DKParts Attribute Found in 'tempDDO' even though it is a document model item.  DDO is either not an Item of a Document Model classified Item Type or Document has not been explicitly retrieved.");

                // If no parts colleciton exists on the original DDO, add appropriate
                // properties
                if(ddoDataId==0){
                    ddoDataId = ddo.addData(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
                    ddo.addDataProperty(ddoDataId, DKConstant.DK_CM_PROPERTY_TYPE, new Short(DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_XDO));
                }
                DKParts dkParts = (DKParts) tempDDO.getData(tempDDODataId); 
                ddo.setData(ddoDataId,dkParts);
            } else
                throw new Exception("Internal Error!  Item should have been a resource or a document model item since already detected it as such.");
        }
        
        if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.retrieveResourceAndPartContentIntoExistingDDO("+obj2String(ddo)+","+obj2String(options)+")");
    }
    
   /**
    * Validate that the specified item type:
    * <UL>
    *    <LI>Is an item type / not a child component type.</LI>
    *    <LI>Is the base view of the item type.</LI>
    *    <LI>Is the active view of the item type.</LI>
    * </UL>
    * @param ddo - Root component DDO of the item to validate.
    **/
    private static void validateItemType(DKDDO ddo) throws IllegalArgumentException, DKException, Exception{
        // Get the Datastore from the DDO.
        DKDatastoreICM dsICM = (DKDatastoreICM) ddo.getDatastore();
        // First, make sure we have a connected datastore by this point.
        if(dsICM==null)                throw new IllegalArgumentException("A connected datastore was expected in the DDO selected.  However, there was no DKDatastoreICM instance in the DKDDO object.  The datastore in this DDO is 'null'.  If the DDO was passed individually or in a collection to the TExportPackageICM.add() method for selecting items to export, the DKDDO object supplied was invalid.  If a query was specified, then an internal error has occurred.");
        if(dsICM.isConnected()==false) throw new IllegalArgumentException("A connected datastore was expected in the DDO selected.  However, the DKDatastoreICM instance in the DKDDO object was never connected.  If the DDO was passed individually or in a collection to the TExportPackageICM.add() method for selecting items to export, the DKDDO object supplied was invalid.  If a query was specified, then an internal error has occurred.  Make sure that the DDOs supplied are from a valid open connection.");
        // Get the object type from the DDO
        String itemTypeName = ddo.getObjectType().trim();
        // Make sure the object type is at least not null.
        if(itemTypeName==null)       throw new IllegalArgumentException("The object type of the selected DKDDO is missing ('null').  The DKDDO object must have a valid object type.  If it is missing, it indicates that it was probably not created through the recommended procedure for creating or obtaining a DKDDO instance.  If the DDO was passed individually or in a collection to the TExportPackageICM.add() method for selecting items to export, the DKDDO object supplied was invalid.  If a query was specified, then an internal error has occurred.");
        if(itemTypeName.length()==0) throw new IllegalArgumentException("The object type of the selected DKDDO is missing (empty string '"+itemTypeName+"').  The DKDDO object must have a valid object type.  If it is missing, it indicates that it was probably not created through the recommended procedure for creating or obtaining a DKDDO instance.  If the DDO was passed individually or in a collection to the TExportPackageICM.add() method for selecting items to export, the DKDDO object supplied was invalid.  If a query was specified, then an internal error has occurred.");
        
        // Get Datastore Definition
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();

        // Validate
        dkEntityDef entityDef = dsDefICM.retrieveEntity(itemTypeName);
        if(entityDef==null) throw new IllegalArgumentException("Alleged item type name '"+itemTypeName+"' either does not exist as any form of an entity type in the '"+dsICM.datastoreName()+"' datastore or user '"+dsICM.userName()+"' does not have the necessary access to the item type.  The alleged item type name '"+itemTypeName+"' was listed as the object type of one of the selected DKDDO objects.  Use only item types that this user has access to.");
        // Make sure it is a root
        DKComponentTypeViewDefICM compTypeViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(itemTypeName);
        if(compTypeViewDef.isRoot()==false)
            throw new IllegalArgumentException("Alleged item type name '"+itemTypeName+"' is not a root component type and therefore is not the item type name in the '"+dsICM.datastoreName()+"' datastore.  The name provided, '"+itemTypeName+"', is a child component type.  Import / Export can only be performed at the item level through the root components.  Individual children cannot be imported.  Review the alleged item type name '"+itemTypeName+"' listed as the object type of one of the selected DKDDO objects.  Use only DDOs of base item type names.");
        // Detect if it is a view, not the base item type.
        if(compTypeViewDef.getName().compareToIgnoreCase(compTypeViewDef.getComponentTypeName())!=0)
            throw new IllegalArgumentException("Data Loss Warning: Alleged item type name '"+itemTypeName+"' is not the base item type name in the '"+dsICM.datastoreName()+"' datastore.  Instead, '"+itemTypeName+"' is a view name of item type '"+compTypeViewDef.getComponentTypeName()+"'.  Non-base views typically act like a filter for the base item type.  Such a filter will likely cause loss of data in the export operation because only the viewable information will be exported.  Review the alleged item type name '"+itemTypeName+"' was listed as the object type of one of the selected DKDDO objects.  Use only DDOs of base item type names.");    
        // Make sure it that the base view is the active view
        DKComponentTypeViewDefICM activeCompTypeViewDef = dsDefICM.getActiveComponentTypeView(itemTypeName);
        if(activeCompTypeViewDef.getName().compareToIgnoreCase(itemTypeName)!=0)
            throw new IllegalArgumentException("Data Loss Warning: Base item type '"+itemTypeName+"' is not the view that is currently active in the '"+dsICM.datastoreName()+"' datastore for user '"+dsICM.userName()+"'.  Instead, '"+activeCompTypeViewDef.getName()+"' is the active view for item type '"+itemTypeName+"'.  Non-base views typically act like a filter for the base item type.  Such a filter will likely cause loss of data in the export operation because only the viewable information will be exported.  Modify the user and/or system settings so that the base item type view '"+itemTypeName+"' is the active view in the system.  Otherwise review the validity of item type name '"+itemTypeName+"' listed as the object type of one of the selected DKDDO objects.  Use only active base item type names.");    
        // Check an alternate was of ensuring that the base view is the active view.
        try{DKItemTypeDefICM itemTypeDef = (DKItemTypeDefICM) entityDef;}
        catch(ClassCastException exc){
            throw new IllegalArgumentException("Data Loss Warning: Alleged item type name '"+itemTypeName+"' is not the base item type name in the '"+dsICM.datastoreName()+"' datastore.  Instead, '"+itemTypeName+"' is a view name of item type '"+compTypeViewDef.getComponentTypeName()+"'.  Non-base views typically act like a filter for the base item type.  Such a filter will likely cause loss of data in the export operation because only the viewable information will be exported.  Review the alleged item type name '"+itemTypeName+"' was listed as the object type of one of the selected DKDDO objects.  Use only DDOs of base item type names.");
        }
    }//end validateItemType()
    
   /**
    * Verifies that the specified datastore has been connected and has not been
    * explicitly disconnected since.  This does not validate an open connection,
    * but could be modified to use validation if desired.  Throws an exception
    * if this check is violated.  Will also check for 'null' value.                 <BR><BR>
    * @param dsICM - DKDatastoreICM to verify that the connect() method was called
    *                and that the disconnect() method was not called since.
    * @exception Throws an exception if this check is violated.
    **/
    private static void verifyConnectedDatastore(DKDatastoreICM dsICM) throws DKException, Exception{
        // check for null value
        if(dsICM==null) 
            throw new Exception("Datastore object is 'null'.  If given as a parameter, please specify a valid datastore object.");
            
        // Check connection (This method will only validate that connect() was called and
        //                   that disconnect() was not called since.
        if(dsICM.isConnected()==false)
            throw new Exception("Datastore is not connected.  Either DKDatastoreICM.connect() was never called or DKDatastoreICM.disconnect() has been called since.  Either a source or target system datastore is not connected.  The datastore was obtained by a function parameter or obtained from a DDO object.  The datastore object is named '"+dsICM.datastoreName()+"', which is expected to be blank if not set or if the object destroy() has been called.  The datastore must be connected for this operation.  If this datastore is not the parameter of the function called, this operation needed required access to that datastore to retrieve information that was not held in local memory to free up memory for scaleability.");
    }//end verifyConnectedDatastore()

   /**
    * Write Exported Package.  Package will be written as a number of files,
    * with the specified main export package the owns all other created files.      <BR><BR>
    * @param centralFileName - Central Export Package file responsible for entire
    *                          package state and all files needed.
    * @param exportOptions   - (Optional) Export Options specified in a TExportPackageICM.ExportOptions
    *                          object.  Use 'null' for defaults.
    *                          Please refer to the ExportOptions constructor information on
    *                          default settings.
    **/
    public void write(String centralFileName,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions==null) // If no options specified, use defaults.
            exportOptions = new TExportPackageICM.ExportOptions();
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.write(centralFileName="+centralFileName+","+obj2String(exportOptions)+")");

        // Get File Name Parts
        String filePath     = getFilePath(centralFileName,exportOptions);
        String fileNameStub = getFileNameStub(centralFileName,exportOptions);
        // If no path specified, set to current path
        if(filePath==null){
            Properties props = System.getProperties();
            filePath = props.getProperty("user.dir") + '/';
        }//end if(filePath==null){
        
        // Write all DDOs to file or XML.
        writeItems(filePath,fileNameStub,exportOptions);
        
        // Write Central Package File
        writeCentralPackageFile(filePath, fileNameStub,exportOptions);

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.write(centralFileName="+centralFileName+","+obj2String(exportOptions)+")");
    }

   /**
    * Write all items to file or XML.                                               <BR><BR>
    * @param filePath            - Fully-specified path of the location where the package
    *                              is to be written.
    * @param centralFileNameStub - Name of central package file, minus extension, that may
    *                              not yet be written.  Nomenclature will center around this.
    *                              The stub must not contain the extension or a path.
    * @param exportOptions       - (Required) Export Options specified in a TExportPackageICM.ExportOptions
    *                              object.  Null not accepted in private methods.
    **/
    private void writeItems(String filePath, String centralFileNameStub,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.writeItems(filePath="+filePath+",centralFileNameStub="+centralFileNameStub+","+obj2String(exportOptions)+")");

        Collection<ItemInfoPack> itemColl     = _itemInfoTree.values();
        Iterator<ItemInfoPack>   itemListIter = itemColl.iterator();
        while(itemListIter.hasNext()){
            ItemInfoPack itemInfoPack = itemListIter.next();
            itemInfoPack.export(filePath, centralFileNameStub,exportOptions);
        }

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.writeItems(filePath="+filePath+",centralFileNameStub="+centralFileNameStub+","+obj2String(exportOptions)+")");
    }

   /**
    * Write central package file including information all "info packs", including 
    * information on selected items, folders, links, and reference attributes.
    * Individual Items should already be written.
    *                                                                               <BR><BR>
    * @param filePath            - Fully-specified path of the location where the package
    *                              is to be written.
    * @param centralFileNameStub - Name of central package file, minus extension, that may
    *                              not yet be written.  Nomenclature will center around this.
    *                              The stub must not contain the extension or a path.
    * @param exportOptions       - (Required) Export Options specified in a TExportPackageICM.ExportOptions
    *                              object.  Null not accepted in private methods.
    **/
    private void writeCentralPackageFile(String filePath, String centralFileNameStub,TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
        if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.writeCentralPackageFile(filePath="+filePath+",centralFileNameStub="+centralFileNameStub+","+obj2String(exportOptions)+")");

        // Create New File
        FileWriter file = new FileWriter(filePath+centralFileNameStub+T_ICM_EXPORT_PACKAGE_FILE_EXT);
        // Write file to string buffer, then write the string buffer.
        StringBuffer fileStr = new StringBuffer();
        // Get the system's newline separator.
        String newline = System.getProperty("line.separator");

        // Write Identifier
        fileStr.append(T_ICM_EXPORT_PACKAGE_FILE_IDENTIFIER);
        // Write Version of Package Tool
        fileStr.append(" v" + T_ICM_EXPORT_PACKAGE_VERSION);
        fileStr.append(newline);
        fileStr.append(newline);
        
        // Write Directly Selected Items
        fileStr.append(T_ICM_EXPORT_PACKAGE_SELECTED_ITEMS_TAG_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        // Write Table Header 
        fileStr.append((new ItemInfoPack(false,null,null,null)).tableHeader(0));
        fileStr.append(newline);
        // Write Selected Items
        Collection<ItemInfoPack> itemColl     = _itemInfoTree.values();
        Iterator<ItemInfoPack>   itemListIter = itemColl.iterator();
        while(itemListIter.hasNext()){
            ItemInfoPack itemInfoPack = itemListIter.next();
            if(itemInfoPack.isSelectedDirectly()){    // Only write directly selected items.
                fileStr.append(itemInfoPack.toString());
                fileStr.append(newline);
            }
        }
        fileStr.append(T_ICM_EXPORT_PACKAGE_SELECTED_ITEMS_TAG_END);
        fileStr.append(newline);
        fileStr.append(newline);
        
        // Write Indirectly Selected Items
        fileStr.append(T_ICM_EXPORT_PACKAGE_REFERENCED_ITEMS_TAG_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        // Write Table Header 
        fileStr.append((new ItemInfoPack(true,null,null,null)).tableHeader(0));
        fileStr.append(newline);
        // Write Selected Items
        itemColl     = _itemInfoTree.values();
        itemListIter = itemColl.iterator();
        while(itemListIter.hasNext()){
            ItemInfoPack itemInfoPack = itemListIter.next();
            if(itemInfoPack.isSelectedDirectly()==false){    // Only write indirectly referenced items.
                fileStr.append(itemInfoPack.toString());
                fileStr.append(newline);
            }
        }
        fileStr.append(T_ICM_EXPORT_PACKAGE_REFERENCED_ITEMS_TAG_END);
        fileStr.append(newline);
        fileStr.append(newline);

        // Write Folder Information
        fileStr.append(T_ICM_EXPORT_PACKAGE_FOLDER_INFO_TAG_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        // Write Table Header 
        fileStr.append((new FolderInfoPack((String)null,(String)null,(String)null,(String)null).tableHeader()));
        fileStr.append(newline);
        // Write Selected Items
        Enumeration<FolderInfoPack> folderInfoList = _folderInfoHT.elements();
        while(folderInfoList.hasMoreElements()){
            FolderInfoPack folderInfoPack = folderInfoList.nextElement();
            fileStr.append(folderInfoPack.toString());
            fileStr.append(newline);
        }
        fileStr.append(T_ICM_EXPORT_PACKAGE_FOLDER_INFO_TAG_END);
        fileStr.append(newline);
        fileStr.append(newline);
        
        // Write Link Information
        fileStr.append(T_ICM_EXPORT_PACKAGE_LINK_INFO_TAG_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        // Write Table Header 
        fileStr.append((new LinkInfoPack(null,null,null,null,null,null,null)).tableHeader());
        fileStr.append(newline);
        // Write Selected Items
        Enumeration<LinkInfoPack> linkInfoList = _linksInfoHT.elements();
        while(linkInfoList.hasMoreElements()){
            LinkInfoPack linkInfoPack = linkInfoList.nextElement();
            fileStr.append(linkInfoPack.toString());
            fileStr.append(newline);
        }
        fileStr.append(T_ICM_EXPORT_PACKAGE_LINK_INFO_TAG_END);
        fileStr.append(newline);
        fileStr.append(newline);
        
        // Write Refernece Attr Information
        fileStr.append(T_ICM_EXPORT_PACKAGE_REFATTR_INFO_TAG_BEGIN);
        fileStr.append(newline);
        fileStr.append(newline);
        // Write Table Header 
        fileStr.append((new RefAttrInfoPack(null,null,null,null,null,null)).tableHeader());
        fileStr.append(newline);
        // Write Selected Items
        Enumeration<RefAttrInfoPack> refAttrInfoList = _refAttrInfoHT.elements();
        while(refAttrInfoList.hasMoreElements()){
            RefAttrInfoPack refAttrInfoPack = refAttrInfoList.nextElement();
            fileStr.append(refAttrInfoPack.toString());
            fileStr.append(newline);
        }
        fileStr.append(T_ICM_EXPORT_PACKAGE_REFATTR_INFO_TAG_END);
        fileStr.append(newline);
        fileStr.append(newline);
        
        // Write the file
        file.write(fileStr.toString());
        
        // Close the file
        file.close();

        if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.writeCentralPackageFile(filePath="+filePath+",centralFileNameStub="+centralFileNameStub+","+obj2String(exportOptions)+")");
    }

    //=============================================================================
    // Helper Classes
    //=============================================================================

   /**
    * Info Pack to describe item information, like which were selected, which were
    * indirectly referenced, and old to new itemid mapping.  An Item Pack is responsible
    * for managing all of core information for an item being exported from one system 
    * and/or imported into another.  It is responsible for all of individual item-level
    * operations.
    **/
    class ItemInfoPack{
        
        private String  _packType = "ItemInfoPack";
        private boolean _selectedDirectly;
        private String  _originalItemID;
        private String  _newItemID;
        private String  _versionID;
        private String  _objectType;
        private DKDDO   _originalDDO;
        private DKDDO   _newDDO;
        private String  _exportedFileName;
        private String  _exportedFilePath;
        private boolean _changedInPhase2;
        
        private String  _exportedResourceContentInfo;   // Holds the one-line description of all resource content associated with this item.  Contains a semicolon delimited list of labels with the name of the file that the content is writtent to.  For example: {label1=resFileName1;label2=resFileName2}
        private HashMap<String,String> _resourceContentLabelToFileMap; // Contains a paired label and file name for all resource content stored with this item.  The label is the key and the value is the corresponding file name for that label.
	    
       /**
        * @param selectedDirectly - True if it was directly selected by user, false if indirectly referenced.
        * @param originalItemID   - Item ID of the exported item.
        * @param versionID        - Version ID of the exported item.
        * @param objectType       - Object Type / Item Type / View Type of the item.
        **/
        public ItemInfoPack(boolean selectedDirectly,String originalItemID,String versionID,String objectType) throws Exception{
            init(selectedDirectly,originalItemID,versionID,objectType,null,null,null);
        }
       /**
        * @param selectedDirectly - True if it was directly selected by user, false if indirectly referenced.
        * @param ddo              - Root DDO for the item.
        **/
        public ItemInfoPack(boolean selectedDirectly,DKDDO ddo) throws Exception{
            init(selectedDirectly,((DKPidICM)ddo.getPidObject()).getItemId(),((DKPidICM)ddo.getPidObject()).getVersionNumber(),ddo.getPidObject().getObjectType(),ddo,null,null);
        }
       /**
        * Create an Info Pack from the data string in the exact format returned by toString().  <BR><BR>
        * @param filePath - current file path of the xml file location for this item.
        * @param dataStr  - Data string in the exact format returned by toString().
        **/
        public ItemInfoPack(String filePath,String dataStr) throws Exception{
            fromString(filePath,dataStr);    
        }
       /**
        * @param selectedDirectly - True if it was directly selected by user, false if indirectly referenced.
        * @param originalItemID   - Item ID of the exported item.
        * @param versionID        - Version ID of the exported item.
        * @param objectType       - Object Type / Item Type / View Type of the item.
        * @param originalDDO      - Original DDO object exported.
        * @param newDDO           - New DDO object imported.
        * @param newItemID        - Item ID of the imported DDO.
        **/
        private void init(boolean selectedDirectly,String originalItemID,String versionID,String objectType, DKDDO originalDDO, DKDDO newDDO, String newItemID) throws Exception{
            // Validate new item id and new DDO are same if specified
            if((newDDO!=null)&&(newItemID!=null)){
                String newDDOItemId = ((DKPidICM)newDDO.getPidObject()).getItemId();
                if(newDDOItemId.compareTo(newItemID)!=0)
                    throw new Exception("The 'newItemID' does not match the item id found in the 'newDDO'.");
            }
            _selectedDirectly = selectedDirectly;
            _originalItemID   = originalItemID;
            _versionID        = versionID;
            _objectType       = objectType;
            _newItemID        = newItemID;
            _originalDDO      = originalDDO;
            _newDDO           = newDDO;
            _exportedFileName = null;
            _exportedFilePath = null;         // Only applicable when importing.  Not saved in export package file.
            _changedInPhase2  = false;        // Only applicable when importing.  Used by TExportPacageICM in phase 2.
            // If the new DDO is specified and the newItemID is not, get item ID from ddo.
            if((newDDO!=null)&&(newItemID==null)){
                String _newItemID = ((DKPidICM)newDDO.getPidObject()).getItemId();
            }
        }
       /**
        * Compile list of unique attrs based for all component types identified in the tree
        * of chlid collections.  A sorted tree will be returned with those unique attrs
        * in the same key.  For each key in the tree map inputed, the tree outputed will
        * contain all of the same keys.  The keys are the component type names.     <BR><BR>
        * @param childrenMap - 2-level TreeMap of children.  First level sorts by
        *                      component view type name, second sorts by componentid.
        *                      Contains component DKDDO objects.  There should be one
        *                      submap (2nd level entry) for each child component view
        *                      type in the item.
        * @param options     - (Required) Options specified in a TExportPackageICM.Options
        *                      object.  Null not accepted in private methods.
        * @return Returns a 1-level TreeMap with all of the keys found in the input tree,
        *         one for each component view type name found.  Contains ArrayLists of 
        *         unique attribute names.
        **/
        private TreeMap<String,ArrayList<String>> compileUniqueAttrList(TreeMap<String,TreeMap<String,DKDDO>> childrenMap, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.compileUniqueAttrList("+obj2String(childrenMap)+","+obj2String(options)+")");

            // Create TreeMap for uniqueAttrs.
            TreeMap<String,ArrayList<String>> uniqueAttrListMap = new TreeMap<String,ArrayList<String>>();

            // Go through all component type view names for components in the exisitng ddo.
            Set<String>      childrenKeysAsSet  = childrenMap.keySet();
            Iterator<String> childrenKeysAsIter = childrenKeysAsSet.iterator();
            while(childrenKeysAsIter.hasNext()){
                // Look up unique attrs.
                String compViewTypeName = childrenKeysAsIter.next();
                // Get one of the DDOs to send to the getUniqueAttrList function.  We are using
                // a DDO here since we need a datastore anyway.  Otherwise the compViewTypeName could
                // have just been used.
                TreeMap<String,DKDDO> childrenOfTypeMap = childrenMap.get(compViewTypeName);
                if((childrenOfTypeMap==null)||(childrenOfTypeMap.size() <= 0)) throw new Exception("Internal Error.  SubMap (2nd-level entry) of the childrenMap '"+compViewTypeName+"' is missing or empty, but no empty entries should exist from this source.");
                Collection<DKDDO> childrenOfTypeMapAsColl = childrenOfTypeMap.values();
                Iterator<DKDDO>   childrenOfTypeMapAsIter = childrenOfTypeMapAsColl.iterator();
                DKDDO             child                   = childrenOfTypeMapAsIter.next();
                ArrayList<String> uniqueAttrList          = getUniqueAttrList(child,options);
                // Add to unique attr tree
                if(uniqueAttrListMap.containsKey(compViewTypeName)) // Ensure that it doesn't already exist.
                    throw new Exception("Internal Error compiling list of unique attributes for all component types in existing item.  Found two collections for the same component view type in the sorted list of child collections.");
                uniqueAttrListMap.put(compViewTypeName,uniqueAttrList);
            }//end while(childrenKeysAsIter.hasNext()){
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.compileUniqueAttrList("+obj2String(childrenMap)+","+obj2String(options)+")  return("+obj2String(uniqueAttrListMap)+")");
            return(uniqueAttrListMap);
        }
       /**
        * Export the item held by this info pack to file.                           <BR><BR>
        * @param filePath            - Fully-specified path of the location where the package
        *                              is to be written.
        * @param centralFileNameStub - Name of central package file, minus extension, that may
        *                              not yet be written.  Nomenclature will center around this.
        *                              The stub must not contain the extension or a path.
        * @param exportOptions       - TExportPackageICM.ExportOptions object.  Use 'null'
        *                              for defaults.  Please refer to the ExportOptions
        *                              constructor for information on defaults.
        **/
        public void export(String filePath, String centralFileNameStub, TExportPackageICM.ExportOptions exportOptions) throws DKException, Exception{
            if(exportOptions==null) throw new Exception("Invalid Parameter: ExportOptions are required by TExportPackageICM.ItemInfoPack.export().  'null' was specified.");
            if(exportOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.export(filePath="+filePath+",centralFileNameStub="+centralFileNameStub+")");

            if(_originalDDO==null)
                throw new Exception("Package not currently used for export.  Probably used for import.  Package export should not be used in this mode.  Original DDO is not set and therefore cannot be exported to file.");
            else if(filePath==null)
                throw new Exception("No filepath specified with file name.  Fully-specified filename is required, including full path.");
            else if(filePath.compareTo("")==0)
                throw new Exception("No filepath specified with file name.  Fully-specified filename is required, including full path.");

            // Create an instance of the XML Services object to prepare to use for
            // conversion to XML
            // OPTIMIZE:  When available, instead of creating a new XML services
            //            object for each item version, re-use the same XML
            //            services object when using the same datastore.  Currently,
            //            this tool allows for items from many datastores and cannot
            //            count on this DKDDO coming from the same DKDatastoreICM object
            //            as the last.  If you will always export data from the same
            //            connected datastore object, create the instance once, store
            //            it globally, and reuse the same instance.
            DKDatastoreICM dsICM = (DKDatastoreICM) _originalDDO.getDatastore();
            DKXMLDataInstanceService instService = new DKXMLDataInstanceService(dsICM);
			
            // If can call retrieve,
            if(exportOptions.isRetrieveDenied()==false){
                // Get Resource Content & Parts
                // We must copy the resource content into the original ddo that we were going to
                // export, since children and everything have already been processed.  The item
                // may have changed since.
                retrieveResourceAndPartContentIntoExistingDDO(_originalDDO,exportOptions);
            }//end if(exportOptions.isRetrieveDenied()==false){

            // Determining Exported File Name (without path)
            _exportedFileName = centralFileNameStub+_originalItemID+_versionID+".xml";
            _exportedFilePath = filePath;

			// Note, DKDDO::toXML() is deprecated and replaced with new XML services
			// as shwn below.
			
            int exportOption = DKConstant.DK_CM_XML_DOM_FORMAT +
                               DKConstant.DK_CM_XML_EMBED_SYSTEM_PROPERTY + 
                               DKConstant.DK_CM_XML_EMBED_RESOURCE_PROPERTY +
                               DKConstant.DK_CM_XML_RESOURCE_STREAM_FORMAT +
                               DKConstant.DK_CM_XML_EMBED_UNIQUE_IDENTIFIER;

            DKXMLDOMItem outputItem = (DKXMLDOMItem)instService.extract(_originalDDO, exportOption);
            String fileURL = _exportedFilePath + _exportedFileName;
            java.io.FileOutputStream fileOut = new java.io.FileOutputStream(fileURL);
            outputItem.convertToStream(outputItem.getXMLItem(),fileOut);
            
            // If there are objects containing Resource Content associated with this
            // item, then:
            //   1.  Place the resource objects into the same directory as XML document
            //   2.  Record a unique label along with each file name written in the 
            //       resource content descriptor written with this InfoPack to the
            //       Export Package file (.xpk).  When the file is loaded again,
            //       an importer can piece the resource objects together as they were
            //       before they were exported.
            
            // Begin the resource content information descriptor using the starting syntax.
            _exportedResourceContentInfo="{;";
            
            Set contentLabels = outputItem.getContentLabels(); // Note, DKXMLDOMItem::getContentLabels() returns a typeless Set from JDK 1.4, but this sample knows the value is actually a Set<String> and will cast accordingly.  Alternatively when JDK 1.6 supported, you could cast to Set<String> and use annotation @SuppressWarnings("unchecked").  JDK 1.4 mentioned here is regarding Java features and the versions in which they were based, not an indication of support of this JDK version in Content Manger version 8.4.
            if(!contentLabels.isEmpty()){
                Iterator iter = contentLabels.iterator();      // Note: Iterator<String>
                if(_resourceContentLabelToFileMap == null)
                    _resourceContentLabelToFileMap = new HashMap<String,String>();
                while(iter.hasNext()){
                    String label      = (String) iter.next();  // Note: Can drop "(String)" if using generics.
                    String resFileURL = _exportedFilePath + label + ".rsc";
                    _resourceContentLabelToFileMap.put(label,resFileURL);
                    _exportedResourceContentInfo = _exportedResourceContentInfo.concat(label + "=" + resFileURL + ";");
                    java.io.BufferedInputStream inStream = new java.io.BufferedInputStream(outputItem.getContentAsStream(label));
                    java.io.FileOutputStream    resFile  = new java.io.FileOutputStream(resFileURL);
					   
                    int availByte = 0;
                    int firstByte = 0;
                    byte[] buffer = new byte[1024];
                    while ((firstByte = inStream.read()) != -1) {
                        resFile.write(firstByte);
                        availByte = inStream.available();
                        if (availByte > 1024)
                            availByte = 1024;
                        inStream.read(buffer, 0, availByte);
                        resFile.write(buffer, 0, availByte);
                    }//while ((firstByte = inStream.read()) != -1)
                     
                }//while(iter.hasNext()){
            }//if(!contentLabels.isEmpty()){
            
            _exportedResourceContentInfo = _exportedResourceContentInfo.concat("}");
			
            // Release Memory of Resource Content & Parts for Scaleability
            if(exportOptions.isRetrieveDenied()==false){ // Only manage it is is granded rights to handle retrieve.
                releaseResourceAndPartContentFromDDO(_originalDDO,exportOptions);
            }//end if(exportOptions.isRetrieveDenied()==false){
                      
            if(exportOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.export(filePath="+filePath+",centralFileNameStub="+centralFileNameStub+")");
        }
       /**
        * Document parts imported will still have the old PIDs.  We need to wipe these out,
        * replacing them with a PID as if it was just created and not yet added.    <BR><BR>
        * @param ddo     - any DDO, if it has parts, they will be processed.
        * @param options - (Required) Options specified in a TExportPackageICM.Options
        *                  object.  Null not accepted in private methods.
        **/
        private void fixDocParts(DKDDO ddo, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.fixDocParts("+obj2String(ddo)+","+obj2String(options)+")");

            // Determine if it has parts
            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 

            // Get the datastore from the new DDO so that we may set in Parts
            DKDatastoreICM dsICM = (DKDatastoreICM)ddo.getDatastore();
            verifyConnectedDatastore(dsICM); // Validate that the datastore is still connectected.

            if(dataid > 0){ // It has parts
                // Get the parts
                DKParts dkParts = (DKParts) ddo.getData(dataid); 
                if(options.getPrintDebugEnable()) System.out.println("Document Has '"+dkParts.cardinality()+"' Parts.");            
                // Go through all, wiping out PIDs
                dkIterator iter = dkParts.createIterator();
                while(iter.more()){
                    DKLobICM part = (DKLobICM) iter.next(); 
                    // Now null PID Out, since it should not exist when DKDDO.add() is called.
                    DKPidICM blankPid = new DKPidICM();
                    blankPid.setObjectType(part.getPidObject().getObjectType());
                    part.setPidObject(blankPid);
                    // Reset the datastore
                    part.setDatastore(dsICM);
                }
            }

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.fixDocParts("+obj2String(ddo)+","+obj2String(options)+")");
        }

       /** 
        * Workaround for required string attributes that probably had
        * an empty string, but now have a null due to a defect in
        * DKDDO.toXML() that does not distinguish between null and
        * empty string ("").  When DKDDO.toXML() writes string attributes
        * to an XML file, it writes both null values and empty string, "", 
        * values as the same.  When the DKDDO is imported from XML using 
        * DKDDO.fromXML(), it cannot tell whether the original value was
        * a null or an empty string.  It assumes null.                   <BR>
        *                                                                <BR>
        * This method will check the attribute definition for the object
        * type of this DKDDO object and if that attribute is marked as
        * required in the target system, there is a good chance that it
        * was required in the original system and therefore it was almost
        * certainly an empty string (""), not a null.                    <BR>
        *                                                                <BR>
        * This method will either just change those null values for strings
        * that are defined as required or will change all null string values
        * to an empty string.  If CONVERT_ALL_NULL_STRINGS_TO_EMPTY_STRING
        * is set to 'true', it will convert all, regardless of whether the
        * attribute is required.  If set to 'false', it will only set those
        * that were almost certainly an empty string in the original system.
        *                                                            <BR><BR>
        * This method will recursively check all child DDOs under this 
        * root component.                                                <BR>
        *                                                                <BR>
        * @param ddo     - any DDO, if it has parts, they will be processed.
        * @param options - (Required) Options specified in a TExportPackageICM.Options
        *                  object.  Null not accepted in private methods.
        **/
        private void fixRequiredStringAttributes(DKDDO ddo, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.fixRequiredStringAttributes("+obj2String(ddo)+","+obj2String(options)+")");

            // Get the datastore definition for the target datastore.
            DKDatastoreICM    dsICM    = (DKDatastoreICM)ddo.getDatastore();
            verifyConnectedDatastore(dsICM); // Validate that the datastore is still connectected.
            DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM)dsICM.datastoreDef();
            
            ArrayList<DKChildCollection> childCollections = new ArrayList<DKChildCollection>(); // when obtaining data items, collect child collections.

            // List all Data Items
            for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
                Object value = null;
                Short  type  = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                switch(type.intValue()){
                    case DKConstant.DK_CM_DATAITEM_TYPE_STRING:
                        String name  = ddo.getDataName(dataid);
                        value = ddo.getData(dataid);
                        if(options.getPrintDebugEnable()) System.out.println("Found String Attribute '"+name+"' with value '"+obj2String(value)+"'.");
                        // If it is null, determine if it was required
                        if(value==null){
                            // If we are to only replace required values with empty strings...
                            if(CONVERT_ALL_NULL_STRINGS_TO_EMPTY_STRING==false){ // then look up just the required ones.
                                // Get the component type view definition.
                                String cpViewName = ddo.getObjectType();
                                if((cpViewName==null)||(cpViewName.trim().length()<=0)) throw new InternalError("Internal Error:  Missing the object type in a DKDDO object being imported.  Cannot look up the compoennt type view definition without knowing the object type.");
                                DKComponentTypeViewDefICM cpViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(cpViewName);
                                if(cpViewDef==null) throw new Exception("Could not find the component type view definition for type '"+cpViewName+"' while processing a DDO for the string attribute workaround for empty strings and null values.  This is an unexpected error since the DDO was successfully created with the same name.  Make sure the user still has access to this type.");
                                // Get the attribute definition
                                DKAttrDefICM attrDef = (DKAttrDefICM) cpViewDef.retrieveAttr(name);
                                if(attrDef==null) throw new Exception("Could not find the attribute definition named '"+name+"' in component view definition for type '"+cpViewName+"' while processing a DDO for the string attribute workaround for empty strings and null values.  Make sure the user has access to this attribute and it is not filtered out by a view.");
                                // If it is required, we assume it was required in the original system,
                                // meaning that it was not null, but instead an empty string.  So
                                // set to an empty string.
                                if(attrDef.isNullable()==false){
                                    if(options.getPrintDebugEnable()) System.out.println("The string attribute is marked as required.  Therefore it probably was an empty string (\"\") in the original system, not null.  Setting to an empty string.");
                                    ddo.setData(dataid,"");
                                }else{                            
                                    if(options.getPrintDebugEnable()) System.out.println("The string attribute is nullable.  Therefore it could have been either NULL or an empty string.  Leaving as NULL.");
                                }
                            }else{ // Otherwise just replace all null string values with an empty string.
                                if(options.getPrintDebugEnable()) System.out.println("Replacing string attribute's existing 'null' value with an empty string (\"\").  Cannot determine if it was originally an empty string or null.  No required attribute violation errors will occur if set to an empty string.");
                                ddo.setData(dataid,"");
                            }
                        }//end if(value==null){
                        break;
                    case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                        value = ddo.getData(dataid);
                        if(value!=null){
                            if(value instanceof DKChildCollection){ // if it is a child collection, add it to a list for printing children later
                                if(options.getPrintDebugEnable()) System.out.println("Found Child Collection of Type '"+((DKChildCollection)value).getAssociatedAttrName()+"'.");
                                childCollections.add((DKChildCollection)value);
                            }
                        }
                        break;
                    default:
                        break;
                }// end swith on data item type
            }// end for all data items in DDO
            
            // Handle Children
            for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
                DKChildCollection childCollection = childCollections.get(i); // get each child collection
                dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
                while(iter.more()){ // While there are children, go through each
                    DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                    fixRequiredStringAttributes(child,options);
                }
            }

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.fixRequiredStringAttributes("+obj2String(ddo)+","+obj2String(options)+")");
        }//end fixRequiredStringAttributes()
       
       /**
        * Get the key to this info pack.  Returns a unique key for identifying
        * this object in a sorted list.                                          <BR><BR>
        * @return Returns the key for this pack.
        **/
        public String getKey(){
            return(_originalItemID.toUpperCase()+_versionID);   
        }//end getKey()

       /**
        * Get the New DDO / DDO of this item in the target system.  Null if not yet imported.  <BR><BR>
        * @return Returns the new DDO in the target datastore or 'null' if not yet imported.
        **/
        public DKDDO getImportedItem(){
            return(_newDDO);   
        }
       /**
        * Get the item type name of the item.
        * @return Returns the item type name for the item as it was
        *         in the original system and should appear in the
        *         target system.
        **/
        public String getItemType(){
            return(_objectType);
        }
       /**
        * Get the new Item ID of this item after it has been imported.               <BR><BR>
        * @Return returns the new Item ID of this imported item or 'null'
        *         if not yet imported.
        **/
        public String getNewItemId(){
            return(_newItemID);   
        }
       /**
        * Get the original Item ID of this item.                                    <BR><BR>
        * @Return returns the original Item ID of this item.
        **/
        public String getOriginalItemId(){
            return(_originalItemID);   
        }
       /**
        * Get the name of this information pack as it will appear in the toString() method.  <BR><BR>
        * @return Returns the info pack type name.
        **/
        public String getPackType(){
            return(_packType);   
        }
       /**
        * Get the Version Number of this item.                                      <BR><BR>
        * @Return returns the Version Number of this item.
        **/
        public String getVersionId(){
            return(_versionID);   
        }
       /**
        * Import the item to the specified datastore.                               <BR><BR>
        * @param targetDS         - Target datastore to import items to.
        * @param refAttrInfoHT    - Reference Attribute Information Packs.  These must be updated by this routine.
        * @param verCompMapTree   - TreeMap, sorted by componentid (key).  Contains CompInfoPacks.
        * @param lastItemInfoPack - Last item imported, null if none.  If was same item, but previous version, create new version.
        * @param importOptions    - Import Options specified in a TExportPackageICM.ImportOptions object.
        **/
        public void importItem(DKDatastoreICM targetDS, Hashtable<String,RefAttrInfoPack> refAttrInfoHT, TreeMap<String,CompInfoPack> verCompMapTree, ItemInfoPack lastItemInfoPack, TExportPackageICM.ImportOptions importOptions) throws DKException, Exception{
            if(importOptions==null) throw new Exception("Invalid Parameter: ImportOptions are required by TExportPackageICM.ItemInfoPack.importItem().  'null' was specified.");
            if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.importItem("+obj2String(targetDS)+","+obj2String(refAttrInfoHT)+","+obj2String(verCompMapTree)+","+obj2String(importOptions)+")");
            if(importOptions.getPrintDebugEnable()) System.out.println("  Item:  ["+_objectType+"] '"+_originalItemID+"' v'"+_versionID+"'.");
            verifyConnectedDatastore(targetDS); // Validate that the datastore is connectected.

            DKDDO   existingDDO              = null;  // Set only if found.
            DKDDO   latestExistingVersionDDO = null;  // Set only if found and above not found.
            DKPid   existingItemPid          = null;  // Tracks pid of existing item if needed and set just before update call.
            boolean isBrandNewItem           = false; // Determine if an update or add is required.
            boolean skip                     = false; // If true, will skip importing an item.
        
            // First, if never exported, export to xml file now.
            if(_exportedFileName==null){
                // set to current path
                Properties props = System.getProperties();
                String filePath = props.getProperty("user.dir") + '/';
                TExportPackageICM.ExportOptions exportOptions = new TExportPackageICM.ExportOptions();
                exportOptions.setPrintDebugEnable(importOptions.getPrintDebugEnable());
                exportOptions.setPrintTraceEnable(importOptions.getPrintTraceEnable());
                export(filePath,"temp_",exportOptions);
            }
            
            // Create the XML Services object for conversion from XML to a DKDDO.
            DKXMLDataInstanceService instServ  = new DKXMLDataInstanceService(targetDS);
            DKXMLDOMItem             importObj = null;

            String importFile = _exportedFilePath+_exportedFileName;
            int options = 0;
            java.io.FileInputStream fileIStr = new java.io.FileInputStream(importFile);

            // Convert XML document representing and item to DOM
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document        fileDoc    = docBuilder.parse(fileIStr);
            Document        propDoc    = null;

            // Create an instance of DKXMLDOMItem, the object that will hold
            // the XML document and resource content, if any,
            importObj = new DKXMLDOMItem(fileDoc, propDoc);
            
            // Parse the list of labels and content file names and add them
            // to a hashtable.
            Set<String> labels = null;
            
            if(_resourceContentLabelToFileMap != null)
                labels = _resourceContentLabelToFileMap.keySet();
            if(labels != null && !labels.isEmpty()){
                Iterator<String> iter = labels.iterator();
                while(iter.hasNext()){
                    String label    = iter.next();
                    String fileName = _resourceContentLabelToFileMap.get(label);
                    if(fileName != null){
                        java.io.FileInputStream iStr = new java.io.FileInputStream(fileName);
                        importObj.setContentAsStream(label, iStr);
                    }//if(fileName != null){
                }//while loop
            }//if(!labels.isEmpty())
            
            // Import into temporary Object.
            // Call ingest to import DDO
            options = DKConstant.DK_CM_XML_IMPORT_PID +
                      DKConstant.DK_CM_XML_DOM_FORMAT ;                      
            DKDDO importedDDO = (DKDDO) instServ.ingest(importObj,options);

            // Set the datastore in importedDDO so that functions that look up the definition can get a datstore.
            importedDDO.setDatastore(targetDS);  
        
            // Determine if the exact item and version already exist.
            // First, consult Conflict Handling
            String itemDesc = _objectType + ' ' + _originalItemID + " v" + _versionID;
            int conflictHandlingSetting = importOptions.getConflictHandling();
            if(conflictHandlingSetting != TExportPackageICM.OPTION_CONFLICTS_ALWAYS_NEW){

                if(importOptions.getPrintDebugEnable()) System.out.println("   Conflict Policy Setting Not Always New, '"+conflictHandlingSetting+"'.  Checking for Existing...");
                String uniqueAttrName = null;
                // Second, Consult Uniqueness Detection Policy
                int uniquenessDetectionPolicy = importOptions.getAnswer_uniqueDetectionPolicy(itemDesc);
                // Third, Detect Uniqueness Criteria or get from Import Options
                if(uniquenessDetectionPolicy==TExportPackageICM.OPTION_UNIQUENESS_USER_SPECIFIED){
                    uniqueAttrName = importOptions.getUserSpecifiedUniqueAttr();
                }else{ // otherwise we need to auto-detect first
                    String[] uniqueAttrs = detectUniqueCriteria(targetDS,_objectType,importOptions);
                    if(uniqueAttrs.length == 1) // if just one found, then use that
                        uniqueAttrName = uniqueAttrs[0];
                    else{
                        String uniqueAttrsStr = "";
                        for(int i=0; i<uniqueAttrs.length; i++){
                            uniqueAttrsStr += (uniqueAttrs[i] + ',');   
                            uniqueAttrsStr = uniqueAttrsStr.substring(0,uniqueAttrsStr.length()-1); // Remove Last Comma.                        }
                        }
                        if(uniqueAttrs.length == 0)
                            uniqueAttrsStr = "<none>";
                        uniqueAttrName = importOptions.prompt_userSpecifiedUniqueAttr(itemDesc,uniqueAttrsStr);
                    }
                }
                // Third, Get Existing DDO.
                short dataid = importedDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,uniqueAttrName);
                if(dataid<=0) // if not found, throw error
                    throw new Exception("Attribute '"+uniqueAttrName+"' Not Found on the Item to be Imported.  It may not exist on the original system or the original DDO is incomplete.");
                Object uniqueAttrVal = importedDDO.getData(dataid);
                if(importOptions.getPrintDebugEnable()) System.out.println("   Searching for Existing Item & Version Based on Attr '"+uniqueAttrName+"' with value '"+uniqueAttrVal+"'...");
                existingDDO = findExistingItemVersion(targetDS,_objectType,_versionID,uniqueAttrName,uniqueAttrVal,importOptions);
                // Lastly, if none exist, get latest version and see if this is the next version of it.
                if(existingDDO==null)
                    latestExistingVersionDDO = findLatestExistingItemVersion(targetDS,_objectType,uniqueAttrName,uniqueAttrVal,importOptions);
                else{ // Confirm overwrite
                    if(importOptions.getAnswer_overwriteConfirm(existingDDO)==true) // If confirmation not enabled, will return true.
                        skip = false;
                    else
                        skip = true;
                }
                    
                // if both not found determine next step depending on conflict handling setting.
                if( (existingDDO==null) && (latestExistingVersionDDO==null) ){
                    // Prompt if necessary
                    if(conflictHandlingSetting==TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_PROMPT)
                        conflictHandlingSetting = importOptions.prompt_conflictHandling_noneFound(itemDesc);
                    if(conflictHandlingSetting==TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_ERROR)
                        throw new Exception("No existing items found in target system matching the specified unique criteria.  Original Item '"+itemDesc+"' not found with attr '"+uniqueAttrName+"' having value '"+uniqueAttrVal+"'.");
                    else if(conflictHandlingSetting==TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_SKIP)
                        skip = true;
                }
            }
            
            if(skip==false){
                // If item version already exists, prepare DDO accordingly
                if(existingDDO != null){
                    if(importOptions.getPrintDebugEnable()) System.out.println("   Preparing Existing Item Version for Overwrite...");
                    // If so, prepare the old ddo to apply imported ddo over it.
                    DKRetrieveOptionsICM dkRetrieveOptionsITEMTREE = DKRetrieveOptionsICM.createInstance((DKDatastoreICM)existingDDO.getDatastore());
                    dkRetrieveOptionsITEMTREE.baseAttributes(true);
                    dkRetrieveOptionsITEMTREE.basePropertyAclName(true);
                    dkRetrieveOptionsITEMTREE.childListOneLevel(true);
                    dkRetrieveOptionsITEMTREE.childListAllLevels(true);
                    dkRetrieveOptionsITEMTREE.childAttributes(true);
                    dkRetrieveOptionsITEMTREE.linksInbound(true);
                    dkRetrieveOptionsITEMTREE.linksOutbound(true);
                    dkRetrieveOptionsITEMTREE.linksDescriptors(true);
                    dkRetrieveOptionsITEMTREE.partsList(true);
                    dkRetrieveOptionsITEMTREE.partsAttributes(true);
                    dkRetrieveOptionsITEMTREE.partsPropertyAclName(true);
                    dkRetrieveOptionsITEMTREE.resourceContent(true);
                    existingDDO.retrieve(dkRetrieveOptionsITEMTREE.dkNVPair());
                    // Check Out Item if it isn't already
                    DKDatastoreICM    dsICM    = (DKDatastoreICM)existingDDO.getDatastore();
                    DKDatastoreExtICM dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
                    verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.
                    if(dsExtICM.isCheckedOut(existingDDO)){  // If it is checked out, make sure it isn't checked out by someone else.
                        if(dsExtICM.checkedOutUserid(existingDDO).compareToIgnoreCase(dsICM.userName())!=0)
                            throw new Exception("Item '"+obj2String(existingDDO)+"' checked out by another user, '"+dsExtICM.checkedOutUserid(existingDDO)+"'.  Item must be free to be checked out by current user '"+dsICM.userName()+"'.");
                    } else // otherwise check it out since nobody has it locked.
                        dsICM.checkOut(existingDDO);
                    // Modify something to increase the version and prepare it for import.
                    prepareNewOrExistingVersionOfDDO(existingDDO,false,importOptions);
                    // Go through each to find reference attributes, update ref attr info packs.
                    updateRefAttrInfoPacks(importedDDO,refAttrInfoHT,importOptions);
                    // Import over the existing item, updating existing children where possible.
                    _newDDO = importOverExisting(importedDDO,existingDDO,verCompMapTree,importOptions);
                    // Set PID of last item.
                    //--> Set in importOverExisting()
                } //     Else if a previous version exists, try to create a new version.
                else if( (latestExistingVersionDDO != null) && isNextVersion(_originalItemID,((DKPidICM)latestExistingVersionDDO.getPidObject()).getVersionNumber(),_versionID,importOptions) ){
                    if(importOptions.getPrintDebugEnable()) System.out.println("   Creating New Version of Existing Item...");
                    // If so, prepare the old ddo by dropping removing all children.
                    DKRetrieveOptionsICM dkRetrieveOptionsITEMTREE = DKRetrieveOptionsICM.createInstance((DKDatastoreICM)latestExistingVersionDDO.getDatastore());
                    dkRetrieveOptionsITEMTREE.baseAttributes(true);
                    dkRetrieveOptionsITEMTREE.basePropertyAclName(true);
                    dkRetrieveOptionsITEMTREE.childListOneLevel(true);
                    dkRetrieveOptionsITEMTREE.childListAllLevels(true);
                    dkRetrieveOptionsITEMTREE.childAttributes(true);
                    dkRetrieveOptionsITEMTREE.linksInbound(true);
                    dkRetrieveOptionsITEMTREE.linksOutbound(true);
                    dkRetrieveOptionsITEMTREE.linksDescriptors(true);
                    dkRetrieveOptionsITEMTREE.partsList(true);
                    dkRetrieveOptionsITEMTREE.partsAttributes(true);
                    dkRetrieveOptionsITEMTREE.partsPropertyAclName(true);
                    dkRetrieveOptionsITEMTREE.resourceContent(true);
                    latestExistingVersionDDO.retrieve(dkRetrieveOptionsITEMTREE.dkNVPair());
                    // Check Out Item if it isn't already
                    DKDatastoreICM    dsICM    = (DKDatastoreICM)existingDDO.getDatastore();
                    DKDatastoreExtICM dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
                    verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.
                    if(dsExtICM.isCheckedOut(existingDDO)){  // If it is checked out, make sure it isn't checked out by someone else.
                        if(dsExtICM.checkedOutUserid(existingDDO).compareToIgnoreCase(dsICM.userName())!=0)
                            throw new Exception("Item '"+obj2String(existingDDO)+"' checked out by another user, '"+dsExtICM.checkedOutUserid(existingDDO)+"'.  Item must be free to be checked out by current user '"+dsICM.userName()+"'.");
                    } else // otherwise check it out since nobody has it locked.
                        dsICM.checkOut(existingDDO);
                    // Modify something to increase the version and prepare it for import.
                    prepareNewOrExistingVersionOfDDO(latestExistingVersionDDO,true,importOptions);
                    // Go through each to find reference attributes, update ref attr info packs.
                    updateRefAttrInfoPacks(importedDDO,refAttrInfoHT,importOptions);
                    // Import over the existing item, updating existing children where possible.
                    _newDDO = importOverExisting(importedDDO,latestExistingVersionDDO,verCompMapTree,importOptions);
                    // Set PID of last item.
                    //--> Set in importOverExisting()
                } // Else if it is a previous version of the last item imported,
                else if(isNextVersionOf(lastItemInfoPack,importOptions)){
                    // If so, prepare the old ddo by dropping removing all children.
                    if(importOptions.getPrintDebugEnable()) System.out.println("   Creating New Version of Last Item Imported...");
                    // Retrieve new copy of DDO.
                    DKDDO lastDDO   = lastItemInfoPack.getImportedItem();
                    DKDDO newVerDDO = ((DKDatastoreICM)lastDDO.getDatastore()).createDDOFromPID((DKPidICM)lastDDO.getPidObject());
                    DKRetrieveOptionsICM dkRetrieveOptionsITEMTREE = DKRetrieveOptionsICM.createInstance((DKDatastoreICM)newVerDDO.getDatastore());
                    dkRetrieveOptionsITEMTREE.baseAttributes(true);
                    dkRetrieveOptionsITEMTREE.basePropertyAclName(true);
                    dkRetrieveOptionsITEMTREE.childListOneLevel(true);
                    dkRetrieveOptionsITEMTREE.childListAllLevels(true);
                    dkRetrieveOptionsITEMTREE.childAttributes(true);
                    dkRetrieveOptionsITEMTREE.linksInbound(true);
                    dkRetrieveOptionsITEMTREE.linksOutbound(true);
                    dkRetrieveOptionsITEMTREE.linksDescriptors(true);
                    dkRetrieveOptionsITEMTREE.partsList(true);
                    dkRetrieveOptionsITEMTREE.partsAttributes(true);
                    dkRetrieveOptionsITEMTREE.partsPropertyAclName(true);
                    dkRetrieveOptionsITEMTREE.resourceContent(true);
                    newVerDDO.retrieve(dkRetrieveOptionsITEMTREE.dkNVPair());
                    // Modify something to increase the version and prepare it for import.
                    prepareNewOrExistingVersionOfDDO(newVerDDO,true,importOptions);
                    // Go through each to find reference attributes, update ref attr info packs.
                    updateRefAttrInfoPacks(importedDDO,refAttrInfoHT,importOptions);
                    // Import over the existing item, updating existing children where possible.
                    _newDDO = importOverExisting(importedDDO,newVerDDO,verCompMapTree,importOptions);
                    // Set PID of last item.
                    //--> Set in importOverExisting()
                } else { // Otherwise, assume brand new
                    if(importOptions.getPrintDebugEnable()) System.out.println("   Creating Entirely New Item...");
                    // Go through each to find reference attributes, update ref attr info packs.
                    updateRefAttrInfoPacks(importedDDO,refAttrInfoHT,importOptions);
                    // Wipe out and track versioned component PIDs.
                    wipeAllTrackAndMapVersionedComponentPIDs(importedDDO,UNKNOWN,verCompMapTree,importOptions);
                    // Create New DDO, Importing from XML.  Using importedDDO from beginning of function.
                    _newDDO = importedDDO;
                    isBrandNewItem = true;
                }

                // Set the datastore.
                _newDDO.setDatastore(targetDS);  
               
                // Fix the Doc Parts.  Wipe out PIDs & Set Datastore
                fixDocParts(_newDDO,importOptions);
                
                // Workaround for required string attributes that probably had
                // an empty string, but now have a null due to a defect in
                // DKDDO.toXML() that does not distinguish between null and
                // empty string ("").
                fixRequiredStringAttributes(_newDDO,importOptions);

                // Add to datastore or update if new version or existing item.
                if(isBrandNewItem)
                    _newDDO.add(DKConstant.DK_CM_CHECKOUT);
                else{ // Should already be checked out.
                    _newDDO.update(); // Version Already Incremented.
                }
                
                // Track New PIDs on Components for Versioned Items.
                //--> Tracked automatically in CompInfoPack because it holds reference
                //    to component, which should now have the new PID.
                
                // Note New ItemID
                _newItemID = ((DKPidICM)_newDDO.getPidObject()).getItemId();

                // Release Memory of Resource Content & Parts for Scaleability
                releaseResourceAndPartContentFromDDO(_newDDO,importOptions);
            
            }//end if(skip==false)
            else
                if(importOptions.getPrintDebugEnable()) System.out.println("   Skipped Importing This Item '"+itemDesc+"'.");
                
            if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.importItem("+obj2String(targetDS)+","+obj2String(refAttrInfoHT)+","+obj2String(verCompMapTree)+")");
        }
        
       /**
        * Apply the imported item over the existing item.  Be sure that new components
        * are marked as new, existing components are updated, and components that no
        * longer exist should be deleted.                                           <BR><BR>
        *
        * Only components with at least one unique attribute that hasn't changed may be
        * actually matched up with an existing component.  If the same unique attribute
        * exists in a different coment, the uniqueness constraint will be violated.  
        * Therefore, those children cannot be removed and added back, but instead must 
        * just be updated.  With this algorithm, no data will be corrupted.  Any case
        * where that could have happened will be prevented by breaking the relationship
        * between other versions of that component, since it is impossible to match them
        * up without the unique constraint remainint the same.
        *                                                                           <BR><BR>
        * @param importedDDO    - Imported DDO from source system.
        * @param existingDDO    - Existing DDO from target system.
        * @param verCompMapTree - TreeMap, sorted by componentid (key).  Contains CompInfoPacks.
        * @param options        - TExportPackageICM.Options object.
        * @return Returns a new rebuilt DDO correctly applying the changes from one on top of the other.
        **/
        private DKDDO importOverExisting(DKDDO importedDDO, DKDDO existingDDO,TreeMap<String,CompInfoPack> verCompMapTree, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.itemInfoPack.importOverExisting("+obj2String(importedDDO)+","+obj2String(existingDDO)+","+obj2String(verCompMapTree)+","+obj2String(options)+")");

            // If item is versioned and multiple versions exist, use special 
            // mapping to deal with unique attributes on versioned items that
            // actually have more than one version.
            if(existsMultipleVersions(existingDDO,options)){

                // Set Parent References in imported DDO tree
                // (Import operation did not set these up.  Normal ITEMTREE retrieval does).
                setParentReferences(importedDDO,null,options);

                // Sort existing children to make the more easily searchable in memory.
                TreeMap<String,TreeMap<String,DKDDO>> existingChildrenMap = new TreeMap<String,TreeMap<String,DKDDO>>();
                TreeMap<String,TreeMap<String,DKDDO>> importedChildrenMap = new TreeMap<String,TreeMap<String,DKDDO>>();
                sortChildren(existingChildrenMap,existingDDO,options);
                sortChildren(importedChildrenMap,importedDDO,options);

                // Wipe out PIDs of All Children, but if versioned, track Component PIDs
                // Still must double check to make sure they still exist (weren't removed and
                // just exist in another version.
                wipeAllTrackAndMapVersionedComponentPIDs(importedDDO,UNKNOWN,verCompMapTree,options);

                // Now that all component are mapped, double check now to make sure
                // they they exist in the current version.  If not wipe it out so that
                // a new component may be created in its spot.  It will be marked as
                // not mapped, so in versioned components with unique attrs, the mapping
                // function will take over and throw an error if it is going to be a problem.
                wipeComponentPidsIfNotExistInVersion(importedDDO,existingChildrenMap,options);

                // Compile a list of uniqueness criteria.  Since we can only compare
                // against the existing item version, only look up the ones for the existing
                // item version.
                TreeMap<String,ArrayList<String>> existingTreeUniqueAttrs = compileUniqueAttrList(existingChildrenMap,options);

                // Do special mapping only if unique attributes exist on at least one
                // child component.
                if(existsUniqueAttrsOnSomeChild(existingTreeUniqueAttrs,options)){

                    // Go through imported item, where unique attributes are found on
                    // a child component, match it up with an existing component.  Walk
                    // upwards matching the parents as well since they then must be the
                    // same as well.  For each matched, take that PID from the existing item
                    // and use it in the imported DDO.
                    mapComponentsBasedOnUniqueAttrs(importedChildrenMap,existingChildrenMap,existingTreeUniqueAttrs,options);
                } else
                    if(options.getPrintDebugEnable()) System.out.println("  Skipping mapping procedure since no unique attributes exist on any of the children in the existing item version.");
                    
                // Free references that are no longer needed.
                existingChildrenMap     = null;
                importedChildrenMap     = null;
                existingTreeUniqueAttrs = null;
            } else { // Else just wipe out the PIDs
                if(options.getPrintDebugEnable()) System.out.println("  Skipping mapping procedure since item is not versioned.");

                // Wipe out PIDs of All Children, but if versioned, track Component PIDs
                // Still must double check to make sure they still exist (weren't removed and
                // just exist in another version.)
                wipeAllTrackAndMapVersionedComponentPIDs(importedDDO,UNKNOWN,verCompMapTree,options);
            }

            // Since rebuildItemTree cannot simply mark children to be removed
            // if any of the delete rules are set to restrict, the only thing
            // we can do is permanently remove them first.
            // Create Re-Sorted Imported Children Map Based on New Mapping if Not Already Done
            TreeMap<String,TreeMap<String,DKDDO>> reSortedImportedChildrenMap = new TreeMap<String,TreeMap<String,DKDDO>>();
            sortChildren(reSortedImportedChildrenMap,importedDDO,options);
            permanentlyRemoveUnmappedChildren(existingDDO,existingDDO,reSortedImportedChildrenMap,options);
            reSortedImportedChildrenMap.clear(); // Free Memory
            reSortedImportedChildrenMap = null;

            // Go through the imported item, rebuilding child collections to 
            // so that they know which are existing, which are new, and which
            // should be removed. (Note: We cannot mark children for removal due
            // to the possibility of delete rule set to restrict.  See call to
            // "permanentlyRemoveUnmappedChildren()" above).
            rebuildItemTree(importedDDO,options);

            // Lastly, set the rebuilt DDO PID to that of the exisitng item,
            // since it will now become that existing item.
            DKPid existingItemPid = existingDDO.getPidObject();
            importedDDO.setPidObject(existingItemPid);

            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.itemInfoPack.importOverExisting("+obj2String(importedDDO)+","+obj2String(existingDDO)+","+obj2String(verCompMapTree)+","+obj2String(options)+")  return("+obj2String(importedDDO)+")");
            return(importedDDO);
        }

       /**
        * Determine if multiple versions of the specified item exist.  If item is
        * not versioned, will return false.  If only one existing version of the
        * specified item is found, false will be returned.                          <BR><BR>
        * @param ddo     - Root component of item.  Does not have to be retrieved.
        * @param options - (Required) TExportPackageICM.Options object.  Required
        *                  in private methods.
        * @return Returns true if at least two versions of the specified ITEMID are found.
        **/
        private boolean existsMultipleVersions(DKDDO ddo,TExportPackageICM.Options options)throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.existsMultipleVersions("+obj2String(ddo)+","+obj2String(options)+")");

            boolean retval = false;

            // Only proceed if item is versioned
            if(isVersioned(ddo,options)){
                // Search for 2 items with this item id.
                // Build the Query String
                String itemid = ((DKPidICM)ddo.getPidObject()).getItemId();  // Get the item id.
                String queryStr = '/' + ddo.getObjectType() + "[@ITEMID=\""+itemid+"\"]";
                if(options.getPrintDebugEnable()) System.out.println("   Query:  "+queryStr);
        
                // Perform Search  (only need to find 2, since just need to know if more than 1.
                DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance((DKDatastoreICM)ddo.getDatastore());
                DKNVPair queryOptions[] = new DKNVPair[3];
                queryOptions[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "2");                                // Specify max using a string value.
                queryOptions[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY);            // Always specify desired Retrieval Options.
                queryOptions[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                               // Must mark the end of the NVPair
                dkResultSetCursor cursor = ddo.getDatastore().execute(queryStr, DKConstantICM.DK_CM_XQPE_QL_TYPE, queryOptions);
                
                // Count Results
                int count = 0;
                while(cursor.fetchNext()!=null) // Loop until reach end of results.
                    count++;
                if(options.getPrintDebugEnable()) System.out.println("   Number of results found (expect 1, or 2 (Max = 2):  "+count);

                // If '1', then we know that there are no more.  If '2', we know there
                // is at least more than 1.  It isn't important to know how many there are
                // beyond '2' at this point.
                if(count > 1)
                    retval = true;
                else
                    retval = false;
            }//end if(isVersioned(ddo,options)){
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.existsMultipleVersions("+obj2String(ddo)+","+obj2String(options)+")  return("+obj2String(retval)+")");
            return(retval);        
        }
                
       /**
        * Determine if unique attributes were found on at least one of the child 
        * components by reviewing the contents of the TreeMap of all the unique 
        * attribute information collected for children of an item.                  <BR><BR>
        * @param uniqeAttrMap - TreeMap of all the unique attribute information.  Keys are the component view type name.
                                Contains ArrayLists of names of unique attributes in that component type.
        * @param options      - (Required) TExportPackageICM.Options object.  Required
        *                       in private methods.
        * @return Returns true if at least some unique attribute exists on at least one of the child component types.
        **/
        private boolean existsUniqueAttrsOnSomeChild(TreeMap<String,ArrayList<String>> uniqueAttrMap,TExportPackageICM.Options options)throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.existsUniqueAttrsOnSomeChild("+obj2String(uniqueAttrMap)+","+obj2String(options)+")");

            boolean retval = false;

            // Get all as a collection.
            Collection<ArrayList<String>> uniqueAttrColl = uniqueAttrMap.values();
            Iterator<ArrayList<String>>   iter           = uniqueAttrColl.iterator();
            // Iterate through until either reach end or found at least one unique attribute.
            while((iter.hasNext()) && (retval==false)){
                ArrayList<String> uniqueAttrList = iter.next();
                if(uniqueAttrList!=null){
                    if(uniqueAttrList.size()>0){
                        retval = true;   
                    }
                }
            }
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.existsUniqueAttrsOnSomeChild("+obj2String(uniqueAttrMap)+","+obj2String(options)+")   return("+obj2String(retval)+")");
            return(retval);        
        }

       /**
        * Prepare existing DDO or A new version of the DDO for import.  First, increase
        * the version of the specified DDO if it is to be a new version.  Resource items
        * will need the resource content reset.  Document Model items will have all
        * Parts removed to prepare for import.  All children will have to be removed 
        * from the bottom up, since cascade delete rule is not guaranteed.          <BR><BR>
        * @param ddo          - DDO to increase the version of.
        * @param isNewVersion - Set to true if a new version of the DDO is to be created, false if not.
        * @param options      - (Required) TExportPackageICM.Options object.  Required
        *                       in private methods.
        **/
        private void prepareNewOrExistingVersionOfDDO(DKDDO ddo,boolean isNewVersion, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.prepareNewOrExistingVersionOfDDO("+obj2String(ddo)+","+obj2String(isNewVersion)+","+obj2String(options)+")");

            // Set some attribute on root.
            setSomeAttributeOnRoot(ddo,options);
            // Remove All Parts
            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS); 
            if(dataid>0){ // If parts exist, remove all
                DKParts dkParts = (DKParts) ddo.getData(dataid); 
                if(dkParts!=null){
                    if(options.getPrintDebugEnable()) System.out.println("  Found Parts Collection.  Removing All Current Parts...");
                    dkParts.removeAllElements();
                }
            }
            // Break Folder-Content Relationships
            dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER); 
            if(dataid>0){ // If DKFolder exists, remove all
                DKFolder dkFolder = (DKFolder) ddo.getData(dataid); 
                if(dkFolder!=null){
                    if(options.getPrintDebugEnable()) System.out.println("  Found DKFolder Collection.  Removing All Current Contents from Folder...");
                    dkFolder.removeAllElements();
                }
            }
            // Break Link Relationships
            // List all Data Items and Find Link Collections Among Them.
            for(dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
                Object value = null;
                Short  type  = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                if(type.shortValue()==DKConstant.DK_CM_DATAITEM_TYPE_LINKCOLLECTION){
                    value = ddo.getData(dataid);
                    if(value!=null){
                        if(value instanceof DKLinkCollection){ // if it is a child collection, add it to a list for printing children later
                            if(options.getPrintDebugEnable()) System.out.println("Found Link Collection of Type '"+((DKLinkCollection)value).getAssociatedAttrName()+"'.");
                            ((DKLinkCollection)value).removeAllElements();
                        } else
                            throw new Exception("Unexpected object found.  Data item found that should be a link collection, but the object instance is '"+value.getClass().getName()+"' instead.");
                    }
                }
            }// end for all data items in DDO
            
            // If Resource, Reset Resource Content
            if(ddo instanceof DKLobICM){
                if(options.getPrintDebugEnable()) System.out.println("  Detected Resource Item.  Resetting Content...");
                byte[] content = ((DKLobICM)ddo).getContent();
                ((DKLobICM)ddo).setContent(content);
            }

            // Update To New Version if required
            if(isNewVersion){
                if(options.getPrintDebugEnable()) System.out.println("  Updating DDO to New Version...");
                ddo.update(DKConstant.DK_CM_VERSION_NEW);
            } else {
                if(options.getPrintDebugEnable()) System.out.println("  Updating Existing Version...");
                ddo.update();
            }

            // Remove All Children from the bottom up.
            // --> Instead, some existing children must remain and be updated
            //     in order to support versioned items that have children with 
            //     unique attributes.  The same component must remain or else the
            //     unique attribute will be violated bacause a new component will
            //     have the same value.
            // PREVIOUSLY:  permanentlyRemoveAllChildren(ddo,ddo,options);
            //              // Retrieve before double-check.
            //              ddo.retrieve(ITEMTREE);
            //              doubleCheckRemoveAllChildren(ddo,options); // Make sure all were removed.
            // NOW:  See importOverExisting() for next step.

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.prepareNewOrExistingVersionOfDDO("+obj2String(ddo)+","+obj2String(isNewVersion)+","+obj2String(options)+")");
        }
       /**
        * Determine if the new item was changed in Phase 2.  For example, if a link, folder
        * content, or reference attribute were added or modified, this should be set to true.  <BR><BR>
        * @return Returns true if phase 2 marked this package as modified.
        **/
        public boolean isChangedInPhase2(){
            return(_changedInPhase2);
        }
       /**
        * Determine if the item in the specified ItemInfoPack is the next version 
        * of this item.                                                             <BR><BR>
        * @param lastItemInfoPack - Any ItemInfoPack to compare this to, 'null' if none.
        * @param importOptions    - Import Options specified in a TExportPackageICM.ImportOptions
        *                           object.  Option Used is Version Gap Handling.
        * @return Returns true if this is the next version of the specified item, false if not.
        **/
        private boolean isNextVersionOf(ItemInfoPack lastItemInfoPack,TExportPackageICM.ImportOptions importOptions) throws Exception{
            if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.isNextVersionOf("+obj2String(lastItemInfoPack)+","+obj2String(importOptions)+")");
            
            boolean retval = false;
            if(lastItemInfoPack!=null){
                if(lastItemInfoPack.getOriginalItemId().compareTo(_originalItemID)==0){
                     // Convert current and last item's version to number.
                     String curVersionStr  = this.getVersionId();
                     String lastVersionStr = lastItemInfoPack.getVersionId();
                     retval = isNextVersion(_originalItemID,lastVersionStr,curVersionStr,importOptions);
                }
            }

            if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.isNextVersionOf(lastItemInfoPack,options) return("+obj2String(retval)+")");
            return(retval);
        }

       /**
        * Determine this item the two version specified are to be considered the next
        * version according to the version gap handling policy.                     <BR><BR>
        * @param originalItemId - Original Item ID, ID of the Item Being Imported.
        * @param lastVersion    - Last version to compare to.
        * @param curVersion     - Current Version to comare with.
        * @param importOptions  - Import Options specified in a TExportPackageICM.ImportOptions
        *                         object.  Option Used is Version Gap Handling.
        * @return Returns true if the current version is considered the next version, false if not.
        **/
        private boolean isNextVersion(String originalItemID,String lastVersionStr, String curVersionStr,TExportPackageICM.ImportOptions importOptions) throws Exception{
            if(importOptions.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.isNextVersion("+originalItemID+","+lastVersionStr+","+curVersionStr+","+obj2String(importOptions)+")");

            // Convert current and last item's version to number.
            int curVersion  = Integer.valueOf(curVersionStr).intValue();
            int lastVersion = Integer.valueOf(lastVersionStr).intValue();
            
            boolean retval = false;
            if( (lastVersion+1) == curVersion) // If this is the next version,
                retval = true;
            else{ // Otherwise look up the version gap handling policy.
                int versionGapHandlingPolicy = importOptions.getAnswer_versionGapHandling(_originalItemID,_objectType,lastVersionStr,curVersionStr);
                if((versionGapHandlingPolicy==TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS) && (curVersion > lastVersion))
                    retval = true;
                else if((versionGapHandlingPolicy==TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS) && (curVersion > lastVersion))
                    throw new Exception("Gap between versions of item detected.  Found current version of '"+curVersionStr+"', while previous version was '"+lastVersionStr+"' for original item '"+originalItemID+"'.");
                else
                    throw new Exception("Internal Error:  Item List Not In Order.  Later versions must follow earlier versions in order.  Found current version of '"+curVersionStr+"', while previous version was '"+lastVersionStr+"' for original item '"+originalItemID+"'.");
            }
            
            if(importOptions.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.isNextVersion("+originalItemID+","+lastVersionStr+","+curVersionStr+",options) return("+obj2String(retval)+")");
            return(retval);
        }
        
       /**
        * Determine if it is selected directly or indirectly referenced.            <BR><BR>
        * @return Returns true if it was directly selected by user, false if indirectly referenced.  Value dependent on whether it is explicitly set.  False by default.
        **/
        public boolean isSelectedDirectly(){
            return(_selectedDirectly);
        }

       /**
        * Go through imported item, where unique attributes are found on
        * a child component, match it up with an existing component.  Walk
        * upwards matching the parents as well since they then must be the
        * same as well.  For each matched, take that PID from the existing item
        * and use it in the imported DDO.                                           <BR><BR>
        * @param importedChildrenMap    - 2-level TreeMap of imported children.  First level
        *                                 sorts by component view type name, second sorts
        *                                 by componentid.  Contains component DKDDO objects.
        * @param existingChildrenMap    - 2-level TreeMap of existing children.  First level
        *                                 sorts by component view type name, second sorts
        *                                 by componentid.  Contains component DKDDO objects.
        * @param exitingTreeUniqueAttrs - 1-level TreeMap, sorted by compont view type name.
        *                                 Contains ArrayLists of unique attribute names.  
        *                                 Key based on component type view name, matching 
        *                                 with all component type view names in the sorted
        *                                 children list.
        * @param options                - (Required) Options specified in a TExportPackageICM.ImportOptions
        *                                 object.  Required in private methods.
        **/
        private void mapComponentsBasedOnUniqueAttrs(TreeMap<String,TreeMap<String,DKDDO>> importedChildrenMap, TreeMap<String,TreeMap<String,DKDDO>> existingChildrenMap, TreeMap<String,ArrayList<String>> existingTreeUniqueAttrs,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.mapComponentsBasedOnUniqueAttrs("+obj2String(importedChildrenMap)+","+obj2String(existingChildrenMap)+","+obj2String(existingTreeUniqueAttrs)+","+obj2String(options)+")");

            // Go through all child collections in the importedDDO
            Set<String>      importedChildrenMapKeysAsSet  = importedChildrenMap.keySet();
            Iterator<String> importedChildrenMapKeysAsIter = importedChildrenMapKeysAsSet.iterator();
            while(importedChildrenMapKeysAsIter.hasNext()){
                String key_compViewTypeName = importedChildrenMapKeysAsIter.next();
                if(options.getPrintDebugEnable()) System.out.println("  Processing imported children of type '"+key_compViewTypeName+"'...");

                // If unique attrs exist of this child type, (otherwise skip, because cannot map or none of this type exist in the exisitng item).
                ArrayList<String> uniqueAttrList = existingTreeUniqueAttrs.get(key_compViewTypeName);
                if( (uniqueAttrList!=null) && (uniqueAttrList.size()>0) ){
                    if(options.getPrintDebugEnable()) System.out.println("  Unique attrs exist on this comp type:  "+uniqueAttrList);
                    // If existing child components exist of this child type (double-check)
                    TreeMap<String,DKDDO> existingChildrenOfCompTypeMap = existingChildrenMap.get(key_compViewTypeName);
                    if((existingChildrenOfCompTypeMap!=null) && (existingChildrenOfCompTypeMap.size()>0)){
                        if(options.getPrintDebugEnable()) System.out.println("  '"+existingChildrenOfCompTypeMap.size()+"' children of this type exist on existing item.");
                        // For each child in the imported collection of this comp type,
                        TreeMap<String,DKDDO> importedChildrenOfCompTypeMap       = importedChildrenMap.get(key_compViewTypeName);
                        Collection<DKDDO>     importedChildrenOfCompTypeMapAsColl = importedChildrenOfCompTypeMap.values();
                        Iterator<DKDDO>       importedChildrenOfCompTypeMapAsIter = importedChildrenOfCompTypeMapAsColl.iterator();
                        while(importedChildrenOfCompTypeMapAsIter.hasNext()){
                            DKDDO importedChild = importedChildrenOfCompTypeMapAsIter.next();  // get the next child of this comp type view name.
                            // If not already mapped, (If mapped, would have compid.  Otherwise it is blank because we wiped it out).
                            // The PID might already be mapped if a previous version of this item was already imported in the same
                            // Export Package.  The importedDDO (working copy in memory) was cleared of DKPidICM info in two previous
                            // "wipe..." methods, replaced with blank DKPidICM, except when a mapping was already known (reference in
                            // a component map list in the "wipe..." methods (wipeAllTrackAndMapVersionedComponentPIDs() &
                            // wipeComponentPidsIfNotExistInVersion() called from importOverExisting()).  In those cases, a previous
                            // version was already imported and no additional lookup was required.  Otherwise, all first versions
                            // handled by this tool must go through a more detailed detection algorithm.
                            String importedChildCompId = ((DKPidICM)importedChild.getPidObject()).getComponentId();
                            if((importedChildCompId==null) || (importedChildCompId.compareTo("")==0) || (importedChildCompId.compareTo(" ")==0)){ // Unmapped PIDs are those previously cleared and not yet replaced by a full, known, existing DKPidICM with a valid component ID.  In V8.1 and V8.2, new DKPidICM objects return an empty string, "", for the component ID.  In V8.3, new DKPidICM objects return a single-space string, " ", for the component ID.
                                if(options.getPrintDebugEnable()) System.out.println("  Processing unmapped child component...");
                                // Try to find match against other versions of existing components
                                // of this type, based on unique attributes.  If found, get the PID from
                                // the existing version with this component id, making sure it exists
                                // in the current version.  So the unique attr values must be found on a 
                                // component of this type (same component type) in the same item
                                // (same itemid), on a version other than the current version, but must
                                // still exist in the current version.
                                
                                // We can use query to accomplish this for us.  Since uniuqe
                                // attrs cannot co-exist across different components in the
                                // same item, *regardless* of version, this guarantees that
                                // only one component id should be returned.  If prior import
                                // mapping could not map components to existing ones, this 
                                // linkage could have been broken, so we should throw an error
                                // if we do get multiple component ids back.
                                //
                                // 0. Build Query String
                                //        Query should look like:
                                //        //compViewTypeName[@ITEMID="123" AND @VERSIONID!="n"
                                //        AND (@myAttr1="test" OR @myAttr2=123 OR ...)]
                                // 1. Perform Query
                                // 2. Make sure only one compid came back.
                                //    --> If not, throw error telling user that linkage was
                                //        broken probably due to prior or current partial
                                //        import.  Delete all versions of this item and 
                                //        re-import all versions again.
                                // 3. If found, make sure still exists in current version.
                                //    --> If not, throw error telling user that the existing
                                //        version of this item has had the component removed at
                                //        some point or the component with this unique attr found
                                //        in the target system was added or imported at a different time.
                                //        Delete all versions of this item and re-import all versions again.
                                // 4. Map (use PID from existing ddo).
                                // 5. Walk up parents & map them since they too then are guaranteed to be the same.

                                // 0-2: Match against other versions of exisiting components
                                DKDDO anyExistingChild  = existingChildrenOfCompTypeMap.get(existingChildrenOfCompTypeMap.firstKey()); // Get any existing child so we can get itemid and version number from it.
                                DKDatastoreICM targetDS = (DKDatastoreICM) anyExistingChild.getDatastore();
                                verifyConnectedDatastore(targetDS); // Validate that the datastore is connectected.
                                String existingChildCompId = matchAgainstOtherVersions(importedChild,anyExistingChild,uniqueAttrList,targetDS,options);
                                anyExistingChild = null; // no longer needed.
                                targetDS         = null;
                                
                                // 3a: If found, 
                                if(existingChildCompId!=null){
                                    // 3b. Make sure still exists in current version.
                                    if(existingChildrenOfCompTypeMap.containsKey(existingChildCompId)==false)
                                        throw new Exception("One or more of the unique attribute values found in this component are also found in other versions of this component.  However, that component either was never created or was removed at some point from this component in the target system.  An existing component version cannot be added back.  If the linkage to a later version of the component was not known when this version was created, the import tool may have not made the two the same component.  Only new versions of components can be created.  The most common cause is that the existing version of this item has had the component removed at some point or the component with this unique attr found in the target system was added or imported at a different time.  Delete all versions of this item and re-import all versions again.");
                                        
                                    // 4. Map (use PID from existing ddo).
                                    DKDDO existingChild = existingChildrenOfCompTypeMap.get(existingChildCompId);
                                    DKPid pid           = existingChild.getPidObject();
                                    importedChild.setPidObject(pid);
                                    if(options.getPrintDebugEnable()) System.out.println("  Mapped Component:  "+obj2String(importedChild));

                                    // 5. Walk up parents & map them since they too then are guaranteed to be the same.
                                    mapParents(importedChild,existingChild,options);  // (Recursive)
                                    
                                }//end if(existingChildCompId!=null){
                            }//end if((importedChildCompId==null) || (importedChildCompId.compareTo("")==0)){
                        }//end while(importedChildrenOfCompTypeMapAsIter.hasNext()){
                    }//end if((existingChildrenOfCompTypeMap!=null) && (existingChildrenOfCompTypeMap.size()>0)){
                }//end if( (uniqueAttrList!=null) && (uniqueAttrList.size()>0) ){
            }//end while(importedChildrenMapKeysAsIter.hasNext()){
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.mapComponentsBasedOnUniqueAttrs("+obj2String(importedChildrenMap)+","+obj2String(existingChildrenMap)+","+obj2String(existingTreeUniqueAttrs)+","+obj2String(options)+")");
        }

       /**
        * Walk up parents, mapping them, since are guaranteed to be the same the same
        * if a child has an unchanged unique attribute.                             <BR><BR>
        * @param importedChild - Imported Item Versoin Child DDO.
        * @param existingChild - Existing Item Version Child DDO.
        * @param options       - (Required) Options specified in a TExportPackageICM.ImportOptions
        *                        object.  Required in private methods.
        **/
        private void mapParents(DKDDO importedChild, DKDDO existingChild, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.mapParents("+obj2String(importedChild)+","+obj2String(existingChild)+","+obj2String(options)+")");

            DKDDO importedParent = (DKDDO) importedChild.getParent();
            DKDDO existingParent = (DKDDO) existingChild.getParent();
            
            // Stop if at root / only continue if isn't root.
            if( (importedParent!=null) && (existingParent!=null) ){

                // Do not map parents that are root either.
                DKDDO importedGrandParent = (DKDDO) importedParent.getParent();
                DKDDO existingGrandParent = (DKDDO) existingParent.getParent();
                if( (importedGrandParent!=null) && (existingGrandParent!=null) ){

                    // Perform Mapping by taking existing parent PID and placing
                    // it as the imported parent pid.  Check if already mapped, and
                    // make sure it is the same then.
                    DKPidICM importedParentPid  = (DKPidICM) existingParent.getPidObject();
                    DKPidICM existingParentPid  = (DKPidICM) existingParent.getPidObject();
                    String importedParentCompId = importedParentPid.getComponentId();
                    String existingParentCompId = existingParentPid.getComponentId();
                    // If not mapped (compid is not set), map it.
                    if((importedParentCompId==null)||(importedParentCompId.compareTo("")==0)){
                        // Map, but using the existing PID as the imported PID
                        importedParent.setPidObject(existingParentPid);
                    } else { // Otherwise it is already mapped, so double check that the compid is the same
                        if(importedParentCompId.compareTo(existingParentCompId)!=0)
                            throw new Exception("Mapped children that are believed to have the same componentid should have the same parent.  However, while mapping parents of one mapping operation, found a parent already mapped with a different componentid.  Tried to set to '"+existingParentCompId+"' but already mapped to '"+importedParentCompId+"'.");
                    }
                    
                } else if ((importedGrandParent==null) && (existingGrandParent==null)){
                    // Accepteable configuration because they are the same, but still do not map.
                } else{ // Unaccepteable.  Components do not have same parents.
                    throw new Exception("Error.  Tree hieherchies of existing child and imported child are not the same.  Traversing the tree upwards to each parent yielded reaching the root at different times.  Both should have reached the top at the same time.");
                }
            } else if ((importedParent==null) && (existingParent==null)){
                // Accepteable configuration because they are the same, but still do not map.
            } else{ // Unaccepteable.  Components do not have same hierarchy.
                throw new Exception("Error.  Tree hieherchies of existing child and imported child are not the same.  Traversing the tree upwards to each parent yielded reaching the root at different times.  Both should have reached the top at the same time.");
            }
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.mapParents("+obj2String(importedChild)+","+obj2String(existingChild)+","+obj2String(options)+")");
        }

       /** <pre>
        * Steps 0-2 of:
        * Try to find match against other versions of existing components
        * of this type, based on unique attributes.  If found in other versions
        * return the componentid.  If not found, return null. 
        * the existing version with this component id, making sure it exists
        * in the current version.  So the unique attr values must be found on a 
        * component of this type (same component type) in the same item
        * (same itemid), on a version other than the current version, but must
        * still exist in the current version.
        *                       
        * We can use query to accomplish this for us.  Since uniuqe
        * attrs cannot co-exist across different components in the
        * same item, *regardless* of version, this guarantees that
        * only one component id should be returned.  If prior import
        * mapping could not map components to existing ones, this 
        * linkage could have been broken, so we should throw an error
        * if we do get multiple component ids back.
        * 
        * 0. Build Query String
        *        Query should look like:
        *        //compViewTypeName[@ITEMID="123" AND @VERSIONID!=n
        *        AND (@myAttr1="test" OR @myAttr2=123 OR ...)]
        * 1. Perform Query
        * 2. Make sure only one compid came back.
        *    --> If not, throw error telling user that linkage was
        *        broken probably due to prior or current partial
        *        import.  Delete all versions of this item and 
        *        re-import all versions again.
        * 3. Make sure still exists in current version.
        *    --> If not, throw error telling user that the existing
        *        version of this item has had the component removed at
        *        some point or the component with this unique attr found
        *        in the target system was added or imported at a different time.
        *        Delete all versions of this item and re-import all versions again.
        * 4. Map (use PID from existing ddo).
        * </pre>                                                                    <BR><BR>
        * @param importedChild    - Imported Item to compare against other versions of the existing
        *                           item.
        * @param anyExistingChild - Any existing child that has the curent itemid & versionid.
        * @param uniqueAttrList   - ArrayList of the unique attributes defined for this component type
        *                           definition.
        * @param options          - (Required) Options specified in a TExportPackageICM.ImportOptions
        *                           object.  Required in private methods.
        * @return Returns the component id of the child component that has other versions
        *         matching at least one of the unique attribute values.  Returns 'null' if
        *         none found.
        **/
        private String matchAgainstOtherVersions(DKDDO importedChild, DKDDO anyExistingChild, ArrayList<String> uniqueAttrList,DKDatastoreICM targetDS,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.matchAgainstOtherVersions("+obj2String(importedChild)+","+obj2String(uniqueAttrList)+","+obj2String(targetDS)+","+obj2String(options)+")");

            String existingChildCompId = null; // Component ID of match, null if no match.
                                
            // 0. Build Query String
            //        Query should look like:
            //        //compViewTypeName[@ITEMID="123" AND @VERSIONID!=n
            //        AND (@myAttr1="test" OR @myAttr2=123 OR ...)]
            String compViewTypeName = importedChild.getObjectType();
            String itemId           = ((DKPidICM)anyExistingChild.getPidObject()).getItemId();
            String currentVersion   = ((DKPidICM)anyExistingChild.getPidObject()).getVersionNumber();
            String queryStr = "//"+compViewTypeName+"[@ITEMID=\""+itemId+"\" AND @VERSIONID!="+currentVersion+" AND (";
            // Go through all unique attrs.
            for(int i=0; i<uniqueAttrList.size(); i++){
                String attrName   = uniqueAttrList.get(i);
                short  attrDataId = importedChild.dataId(attrName);
                if(attrDataId==0)
                    throw new Exception("No dataid found for unique attribute '"+attrName+"' in imported component of type '"+compViewTypeName+"' in item '"+itemId+"'.");
                Object attrVal    = importedChild.getData(attrDataId);
                if(attrVal!=null){ // Only search if not null.

                // If not first one (i=0), add " OR " between.
                if(i>0)
                    queryStr += " OR ";

                // Add to query string:  @<atrName>=
                queryStr += ('@'+attrName+'=');

                // Add value after equals sign.
                // If String, Char, Date, Time, or Timestamp, then use double-quotes.
                if(    (attrVal instanceof String) 
                    || (attrVal instanceof java.sql.Date) 
                    || (attrVal instanceof java.sql.Time) 
                    || (attrVal instanceof java.sql.Timestamp) )
                    queryStr += ('\"' + attrVal.toString() + '\"');
                else // otherwise use no quoates.
                    queryStr += attrVal;

                }//end if(attrVal!=null){
            }//end for(int i=0; i<uniqueAttrList.size(); i++){

            // end query string
            queryStr += ")]";
            if(options.getPrintDebugEnable()) System.out.println("  Query = "+queryStr);
            
            // 1. Perform Query
            DKRetrieveOptionsICM dkRetrieveOptionsIDONLY = DKRetrieveOptionsICM.createInstance(targetDS);
            DKNVPair queryOptions[] = new DKNVPair[3];
            queryOptions[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default)        // Specify max using a string value.
            queryOptions[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsIDONLY);            // Always specify desired Retrieval Options.
            queryOptions[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                               // Must mark the end of the NVPair
            dkResultSetCursor cursor = targetDS.execute(queryStr, DKConstantICM.DK_CM_XQPE_QL_TYPE, queryOptions);
            
            // 2. Make sure only one compid came back.
            //    --> If not, throw error telling user that linkage was
            //        broken probably due to prior or current partial
            //        import.  Delete all versions of this item and 
            //        re-import all versions again.
            DKDDO ddo = null;
            int count = 0;
            while((ddo = cursor.fetchNext())!=null){ // Get the next ddo & stop when ddo == null.
                String nextCompId = ((DKPidICM)ddo.getPidObject()).getComponentId();
                // If we don't have an existing child comp id yet, mark it.
                if(existingChildCompId==null)
                    existingChildCompId = nextCompId;
                else{ // otherwise make sure we only have one component id.
                    if(existingChildCompId.compareTo(nextCompId)!=0)
                        throw new Exception("Multiple Components found of type '"+compViewTypeName+"' in item '"+itemId+"' that match some of the unique attribute values of the component being processed.  The linkage was probably broken due to a prior or current partial import.  Delete all versions of this itemid in the target system and reimport all versions.");
                }
                count++;
            }
        
            cursor.destroy();  // Close & Destroy Cursor, Ending Implied Transaction.

            if(options.getPrintDebugEnable()) System.out.println("  Found '"+count+"' Component Versions Matching One or More of the Unique Attribute Values.");
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.matchAgainstOtherVersions("+obj2String(importedChild)+","+obj2String(uniqueAttrList)+","+obj2String(targetDS)+","+obj2String(options)+")  return("+obj2String(existingChildCompId)+")");
            return(existingChildCompId);
        }

       /** <pre>
        * Now that all mapping is done and unmapped existing children have been persistenly
        * removed, rebuild the entire item tree to correctly consider existing children for
        * update instead of marking them as new.  Go through imported item, rebuilding child
        * collections so that the API will know which are existing and which are new.
        *
        * <B> WARNING:  THIS FUNCTION CONTAINS USE OF A FEATURE THAT IS NOT A SUPPORTED
        *               PUBLIC FEATURE.  DO NOT USE THE FEATURE IN YOUR APPLICATIONS.
        *               IBM WILL NOT SUPPORT ANY PROBLEMS ENCOUNTERED IN USER CODE THAT
        *               USED THIS FEATURE.  This is not example usage or supported public
        *               interfaces.  The interfaces used here to alter the internal 
        *               tracking mechanism should not be used by non-IBM personel.  Such
        *               alterations will affect reliability of data persistence.  This
        *               tool must attempt modification of this internal mechanism in order
        *               to correctly map versioned child components in the special case
        *               of this import tool.
        * </B>
        * Inputs: 
        *    1. Imported DDO Tree & 2. Sorted Map by Comp Type.
        *          - Identified Unique Children Mapped
        *          - Non-Unique or Un-Identidfied Unique Children As New
        *                *Un-Identified Unique Children should be okay to create
        *                 as new this time, since they should not conflict with other
        *                 versions at this point.  For versioned items, this does
        *                 constitute a breakage in the version relationship between
        *                 what should have been the same component, likely caused by
        *                 imported over exiting or by a partial import.  The only 
        *                 solution is to delete all versions of the item and re-import.
        *          - Unmapped Existing Children Persistently Removed.
        *          
        * Outputs:
        *    1. Rebuilt DDO Tree
        *           - Mapped Children Tracked as Existing
        *           - New Children Tracked as New
        *           - Unmapped Existing Chidlren Tracked as New
        *           - NO Unmapped Existing Children Stored or Tracked (already in input).
        * </pre>                                                                    <BR><BR>
        * @param importedDDO - DDO with new and mapped existing components.
        * @param options     - (Required) Options specified in a TExportPackageICM.ImportOptions
        *                      object.  Required in private methods.
        */
        private void rebuildItemTree(DKDDO curDDO,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.rebuildItemTree("+obj2String(curDDO)+","+obj2String(options)+")");
            
            // List all Data Items
            for(short dataid=1; dataid<=curDDO.dataCount(); dataid++) { // go through all attributes in the ddo
                Object value = null;
                Short  type  = (Short) curDDO.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                switch(type.intValue()){
                    case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                        value = curDDO.getData(dataid);
                        if(value!=null){
                            if(value instanceof DKChildCollection){ // if it is a child collection, add it to a list for printing children later
                                if(options.getPrintDebugEnable()) System.out.println("Found Child Collection of Type '"+((DKChildCollection)value).getAssociatedAttrName()+"'.");
                                DKChildCollection currentChildColl = (DKChildCollection) value;
                                // Ignore if empty
                                if(currentChildColl.cardinality()>0){
                                    // ------------------------------
                                    // Rebuild Collection
                                    // ------------------------------
                                    // Create New Collection
                                    DKChildCollection rebuiltColl = new DKChildCollection();
                                    // Go through all children in old collection
                                    dkIterator iter = currentChildColl.createIterator();
                                    while(iter.more()){
                                        DKDDO child = (DKDDO) iter.next();
                                        // If mapped, mark as existing
                                        //    A child is mapped if it has a componentId, otherwise
                                        //    it was wiped out / cleared.
                                        String compId = ((DKPidICM)child.getPidObject()).getComponentId();
                                        if((compId!=null) && (compId.compareTo("")!=0)){
                                            // WARNING:  UNSUPPORTED PUBLIC FEATURE.  DO NOT USE THE
                                            //           FOLLOWING CODE IN YOUR APPLICATIONS.  IBM 
                                            //           WILL NOT SUPPORT ANY PROBLEMS ENCOUNTERED 
                                            //           IN USER CODE THAT USES THIS FEATURE.  For more
                                            //           information, please see the Javadoc for this
                                            //           function.
                                            // Turn Tracking Off       (WARNING! See Above)
                                            rebuiltColl.setTracking(false);
                                            // Add Child.  It will appear is if it was already there.
                                            rebuiltColl.addElement(child);
                                            // Turn Tracking Back On   (WARNING! See Above)
                                            rebuiltColl.setTracking(true);
                                            if(options.getPrintDebugEnable()) System.out.println("  Marked component ("+child.getObjectType()+") '"+compId+"' as existing.");
                                        } else {// If not mapped, mark as new
                                                //   A child is marked as new simply by adding it to a child collection if it
                                                //   does not already exist.
                                            rebuiltColl.addElement(child);
                                        }
                                    }//end while(iter.more()){
                                    // Save Rebuilt Collection
                                    curDDO.setData(dataid,rebuiltColl);
                                    value            = null;  // Drop reference to ensure it is not used again, since we replaced it.
                                    currentChildColl = null; 
                                    // ------------------------------
                                    // Recursively Rebuild Each Child
                                    // ------------------------------
                                    // Go through all children in old collection
                                    iter = rebuiltColl.createIterator();
                                    while(iter.more()){
                                        DKDDO child = (DKDDO) iter.next();
                                        rebuildItemTree(child,options);                                            
                                    }//end while(iter.more()){
                                }//end if(currentChildColl.cardinality()>0){
                            }//end if(value instanceof DKChildCollection){
                        }//end if(value!=null){
                        break;
                    default:
                        break;
                }// end swith on data item type
            }// end for all data items in DDO

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.rebuildItemTree("+obj2String(curDDO)+","+obj2String(options)+")");
        }

       /**
        * Retrieve All Versions of the specified component.  Store results in TreeMap sorted
        * by verion number.  Retrieves with level ATTRONLY.                         <BR><BR>
        * @param componentDDO - Component DDO to retrieve all versions for.
        * @param options      - (Required) Options specified in a TExportPackageICM.ImportOptions
        *                       object.  Required in private methods.
        * @return Returns a 1-level TreeMap with results, sorted by version number.  Contains
        *         component DDOs.
        */
        private TreeMap<String,DKDDO> retrieveAllVersionsOfComponent(DKDDO componentDDO, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.retrieveAllVersionsOfComponent("+obj2String(componentDDO)+","+obj2String(options)+")");
            
            DKDatastoreICM dsICM = (DKDatastoreICM) componentDDO.getDatastore();
            String objectType    = componentDDO.getObjectType();
            String compId        = ((DKPidICM)componentDDO.getPidObject()).getComponentId();
            verifyConnectedDatastore(dsICM); // Validate that the datastore is connectected.

            // Build Query String
            String queryStr = '/'+objectType+"[@COMPONENTID=\""+compId+"\"]";
            if(options.getPrintDebugEnable()) System.out.println("  Query = "+queryStr);
            
            // Perform Search
            DKRetrieveOptionsICM dkRetrieveOptionsATTRONLY = DKRetrieveOptionsICM.createInstance(dsICM);
            dkRetrieveOptionsATTRONLY.baseAttributes(true);
            dkRetrieveOptionsATTRONLY.basePropertyAclName(true);
            DKNVPair queryOptions[] = new DKNVPair[3];
            queryOptions[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default)          // Specify max using a string value.
            queryOptions[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptionsATTRONLY);            // Always specify desired Retrieval Options.
            queryOptions[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                                 // Must mark the end of the NVPair
            dkResultSetCursor cursor = dsICM.execute(queryStr, DKConstantICM.DK_CM_XQPE_QL_TYPE, queryOptions);
        
            // Create Results
            TreeMap<String,DKDDO> resultsMap = new TreeMap<String,DKDDO>();  // sorted by version number.
            DKDDO ddo = null;
            while((ddo = cursor.fetchNext())!=null){ // Get the next ddo & stop when ddo == null.
                String key_versionNum = ((DKPidICM)ddo.getPidObject()).getVersionNumber();
                resultsMap.put(key_versionNum,ddo);
            }
        
            cursor.destroy();  // Close & Destroy Cursor, Ending Implied Transaction.

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.retrieveAllVersionsOfComponent("+obj2String(componentDDO)+","+obj2String(options)+")  return("+obj2String(resultsMap)+")");
            return(resultsMap);
        }
       /**
        * Retrieve the item from the target datastore given the known mapped/new item id
        * in the target system.  Will place result as the new ddo / imported item ddo. <BR><BR>
        * @param targetDS          - Connected, Target DKDatastoreICM.
        * @param dkRetrieveOptions - Retrieve Options to send to the DDO.retrieve() operation.
        * @param options           - (Required) Options specified in a TExportPackageICM.ImportOptions
        *                            object.  Required in private methods.
        * @return Returns the retrieved DDO for the root component of the item.
        **/
        public DKDDO retrieveImportedItem(DKDatastoreICM targetDS,DKRetrieveOptionsICM dkRetrieveOptions,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.ItemInfoPack.retrieveImportedItem("+obj2String(targetDS)+","+dkRetrieveOptions.toString(false)+","+obj2String(options)+")");
            verifyConnectedDatastore(targetDS); // Validate that the datastore is connectected.

            // Validate that we have enough information to do this.
            if((_newItemID==null) || (_newItemID.compareTo("")==0))
                throw new Exception("Cannot use ItemInfoPack.retrieveImportedItem() on an item that has not yet been imported.  This method is intended for use with the fromString() function which would not automatically retrieve the DDO.");

            // Build Query String to find this item
            // Query should look like /myObjectType[@ITEMID="1234" AND @VERSIONID=123]
            String queryStr = '/'+_objectType+"[@ITEMID=\""+_newItemID+"\" AND @VERSIONID="+_versionID+"]";
            if(options.getPrintDebugEnable()) System.out.println("  Query = "+queryStr);

            // Perform Search
            DKNVPair queryOptions[] = new DKNVPair[3];
            queryOptions[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "1"); // Can only be 1                // Specify max using a string value.
            queryOptions[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptions);                   // Always specify desired Retrieve Options.
            queryOptions[2] = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                                // Must mark the end of the NVPair
            dkResultSetCursor cursor = targetDS.execute(queryStr, DKConstantICM.DK_CM_XQPE_QL_TYPE, queryOptions);
            
            // Get the DDO.
            DKDDO ddo = cursor.fetchNext();
            
            cursor.destroy();  // Close & Destroy Cursor, Ending Implied Transaction.
            
            if(ddo==null) // If not found, throw error
                throw new Exception("Could not find completed item '"+_newItemID+"' v'"+_versionID+"' of type '"+_objectType+"' in the specified target system.  Operations must not have completed in this system.");
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.ItemInfoPack.retrieveImportedItem("+obj2String(targetDS)+","+dkRetrieveOptions.toString(false)+","+obj2String(options)+")  return("+obj2String(ddo)+")");
            return(ddo);
        }
                                           
       /**
        * Phase 2 of import should mark this package as changed if it added a link, folder
        * content, or reference attribute.                                          <BR><BR>
        * @param isChanged = Set to true if a change was made or false to reset.
        **/
        public void setChangedInPhase2(boolean isChanged){
            _changedInPhase2 = isChanged;
        }
       /**
        * If the item is already imported, set the internal state of this pack to point
        * to that completed item.  Set to 'null' if not yet imported.  This method is 
        * intended to be used internally by the restart process of TExportPackageICM.  <BR><BR>
        * @param  DDO of item in target system.
        **/
        public void setImportedItem(DKDDO importedDDO){
            _newDDO = importedDDO;
            if(importedDDO!=null)
                _newItemID = ((DKPidICM)importedDDO.getPidObject()).getItemId();
            else
                _newItemID = null;
        }
       /**
        * Set the selectedDirectly flag used for reports.                           <BR><BR>
        * @param selectedDirectly - True if it was directly selected by user, false if indirectly referenced.
        **/
        public void setSelectedDirectly(boolean val){
            _selectedDirectly = val;   
        }
       /**
        * Set some attribute on the root ddo, so that it will be marked as changed
        * and can be updated.  This is needed to increase the version in some cases. <BR><BR>
        * @param ddo     - DDO to find and set some attribute for.
        * @param options - TExportPackageICM.Options object.
        **/
        private void setSomeAttributeOnRoot(DKDDO ddo,TExportPackageICM.Options options) throws DKUsageError, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.setSomeAttributeOnRoot("+obj2String(ddo)+","+obj2String(options)+")");

            // List all Data Items
            boolean stop = false;
            for(short dataid=1; (dataid<=ddo.dataCount()) && (stop == false); dataid++) { // go through all attributes in the ddo until we find one that we can change.
                Short type   = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                switch(type.intValue()){
                    case DKConstant.DK_CM_DATAITEM_TYPE_STRING:
                    case DKConstant.DK_CM_DATAITEM_TYPE_SHORT:
                    case DKConstant.DK_CM_DATAITEM_TYPE_LONG:
                    case DKConstant.DK_CM_DATAITEM_TYPE_FLOAT:
                    case DKConstant.DK_CM_DATAITEM_TYPE_DECIMAL:
                    case DKConstant.DK_CM_DATAITEM_TYPE_DATE:
                    case DKConstant.DK_CM_DATAITEM_TYPE_TIME:
                    case DKConstant.DK_CM_DATAITEM_TYPE_TIMESTAMP:
                    case DKConstant.DK_CM_DATAITEM_TYPE_DOUBLE:
                    case DKConstant.DK_CM_DATAITEM_TYPE_BYTEARRAY:
                    case DKConstant.DK_CM_DATAITEM_TYPE_DDOOBJECT:
                    case DKConstant.DK_CM_DATAITEM_TYPE_XDOOBJECT:
                    case DKConstant.DK_CM_DATAITEM_TYPE_DATAOBJECTBASE:
                        // Reset Value
                        Object value = ddo.getData(dataid);
                        ddo.setData(dataid,value);
                        // Stop now since we only need to modify one.
                        stop = true;
                        // Debug Info
                        if(options.getPrintDebugEnable()){
                            String name  = ddo.getDataName(dataid);
                            if(value==null) System.out.println("  Resetting Attr '"+name+"' with Value 'null' on Item '"+obj2String(ddo)+"'.");
                            else            System.out.println("  Resetting Attr '"+name+"' with Value '"+value+"' on Item '"+obj2String(ddo)+"'.");
                        }
                    default:
                        // Keep Looping
                        break;
                }// end swith on data item type

            }// end for all data items in DDO
            
            // If none found, variable 'stop' will still be false.
            if(stop==false)
                throw new Exception("Could not find any user-defined attributes on root.  This tool requires that there be some user-defined attribute on root that it may touch.  The value will remain the same.");

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.setSomeAttributeOnRoot("+obj2String(ddo)+","+obj2String(options)+")");
        }

       /**
        * Set parent references in imported DDO tree.  These references are normally 
        * set when a root item is retrieved with the ITEMTREE retrieval option.  The   
        * import from xml operation does not set these values.  Therefore, it must
        * be set here.  Recursion follows one branch at a time until it reaches the bottom
        * of one, then walks back up that branch from the bottom up.                <BR><BR>
        * @param component - Component to set parent reference of.  All sub components will
        *                    recursively have their parents set as well.
        * @param parent    - Parent or null if it is the root.
        * @param options   - (Required) TExportPackageICM.Options object.  Required
        *                    in private members.
        **/
        private void setParentReferences(DKDDO component, DKDDO parent, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.itemInfoPack.setParentReferences("+obj2String(component)+","+obj2String(parent)+","+obj2String(options)+")");

            // Set parent reference
            component.setParent(parent);

            // Find and set children's parent references to their parent (recursively)
            for(short dataid=1; dataid<=component.dataCount(); dataid++) { // go through all attributes in the ddo
                Object value = null;
                Short  type  = (Short) component.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                switch(type.intValue()){
                    case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                        value = component.getData(dataid);
                        if(value!=null){
                            if(value instanceof DKChildCollection){ // if it is a child collection, add it to a list for printing children later
                                DKChildCollection coll = (DKChildCollection) value;
                                // If not empty,
                                if(coll.cardinality()>0){
                                    // Loop through all children of this type
                                    dkIterator iter = coll.createIterator();
                                    while(iter.more()){
                                        DKDDO child = (DKDDO) iter.next(); // get the next child.
                                        // set parent references in this branch.
                                        setParentReferences(child,component,options);
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }// end swith on data item type
            }// end for all data items in DDO
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.itemInfoPack.setParentReferences("+obj2String(component)+","+obj2String(parent)+","+obj2String(options)+")");
        }

       /**
        * Sort children into a TreeMap containing a sub TreeMap of each child type that
        * exists in the ddo.  A single (sub) TreeMap will exist for each type that exists
        * in the ddo.  Empty child collections will be ignored.  Any child type not found
        * will not end up on the tree.  The key to the first level of the tree is the 
        * component view type name.  The key for the second level (sub) TreeMap is the
        * component id.  This is a recursive function, so the tree will be passed down.
        * Recursion terminates when it hits the bottom of each branch.              <BR><BR>
        * @param childrenMap - TreeMap containing up to one sub-TreeMap collection of 
        *                      every type, sorted by component view type name.  Each
        *                      sub-TreeMap is sorted by component id.
        * @param options     - (Required) TExportPackageICM.Options object.  Required
        *                      in private members.
        **/
        private void sortChildren(TreeMap<String,TreeMap<String,DKDDO>> childrenMap, DKDDO ddo, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.itemInfoPack.sortChildren("+obj2String(childrenMap)+","+obj2String(ddo)+","+obj2String(options)+")");

            // List all Data Items, find children.
            for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
                Object value = null;
                Short  type  = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                switch(type.intValue()){
                    case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                        value = ddo.getData(dataid);
                        if(value!=null){
                            if(value instanceof DKChildCollection){ // if it is a child collection, add it to a list for printing children later
                                DKChildCollection coll = (DKChildCollection) value;
                                // If not empty,
                                if(coll.cardinality()>0){
                                    // Get existing first-level entry if it exists
                                    String name = coll.getAssociatedAttrName();
                                    TreeMap<String,DKDDO> allChildrenOfTypeMap = childrenMap.get(name);
                                    // If a sub map doesn't exist of that child component view type, add one
                                    if(allChildrenOfTypeMap==null){
                                        allChildrenOfTypeMap = new TreeMap<String,DKDDO>();
                                        childrenMap.put(name,allChildrenOfTypeMap);
                                    }
                                    // Add all children of this type to the submap
                                    dkIterator iter = coll.createIterator();
                                    while(iter.more()){
                                        DKDDO child = (DKDDO) iter.next();
                                        String key  = ((DKPidICM)child.getPidObject()).getComponentId();
                                        // If there is no component ID, then remove it since in this scenario
                                        // we it isn't mapped and therefore isn't needed.
                                        if((key==null)||(key.compareTo("")==0)){
                                            if(options.getPrintDebugEnable()) System.out.println("  Omitting Unmapped Child From Sorting.  Not Needed For Scenario.");
                                        } else {
                                            allChildrenOfTypeMap.put(key,child);
                                        }
                                        // Process child Recursively
                                        sortChildren(childrenMap,child,options);
                                    }//end while(iter.more()){
                                    if(options.getPrintDebugEnable()) System.out.println("  Found & Sorted '"+coll.cardinality()+"' children of type '"+name+"'.");
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }// end swith on data item type
            }// end for all data items in DDO
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.itemInfoPack.sortChildren("+obj2String(childrenMap)+","+obj2String(ddo)+","+obj2String(options)+")");
        }
    
       /**
        * Table Header for pringing a list of these packs.                          <BR><BR>
        * @param indent - Number of spaces to indent from left.
        * @return Returns column information for a table listing of these packs.
        **/
        public String tableHeader(int indent){
            String newline = System.getProperty("line.separator");  // Get the system's newline separator.
            String indentStr = "";
            for(int i=0; i<indent; i++)
                indentStr += ' ';
            String line1 = indentStr + "Pack Type      Object Type Name   Original Item ID              VersionID    New Item ID                   Type  Exported File Name   Resource Content Info                       ";
            String line2 = indentStr + "-------------  -----------------  ----------------------------  ---------    ----------------------------  ----  -------------------  -----------------------------";
            //                          ItemInfoPack:  [Book]             '12345678901234567890123456'  v'123456' -> '12345678901234567890123456'  'd'   "C:\temp\myfile.xml"  ";label:c:\temp\resFil.rsc"
            return(line1+newline+line2);
        }
       /**
        * String representation of pack.                                            <BR><BR>
        * @return Returns a printable version of this package.
        **/
        public String toString(){
            // Calculate white space padding to keep columns in order.
            String objTypePadding = "";
            for(int i=_objectType.length(); i<32; i++)
                objTypePadding+=" ";
            String versionPadding = "";
            for(int i=_versionID.length(); i<6; i++)
                versionPadding+=" ";
            String newItemIdMsgStr = null;
            if(_newItemID==null) newItemIdMsgStr = "null                      ";
            else                 newItemIdMsgStr = _newItemID;
            String exportedFileNameMsgStr = null;
            if(_exportedFileName==null) exportedFileNameMsgStr = "null";
            else                 exportedFileNameMsgStr = _exportedFileName;
            String type = null;
            if(_selectedDirectly)
                type = "d";  // Directly selected
            else
                type = "i";  // Indirectly selected

            // ItemInfoPack:  [Book]  '12831432432'  v'1' -> 'null' 'd'  "null" {;label1=file1;}
            return(_packType+":  ["+_objectType+"]"+objTypePadding+"  '"+_originalItemID+"'  v'"+_versionID+"'"+versionPadding+" -> '"+newItemIdMsgStr+"'  '"+type+"'   \""+exportedFileNameMsgStr+"\""+"'  " + _exportedResourceContentInfo);
        }
       /**
        * Sets properties to values from the information generated by the toString()
        * method.                                                                   <BR><BR>
        * @param filePath - current file path of the xml file location for this item.
        * @param dataStr - Data string in the exact format returned by toString().
        **/
        private void fromString(String filePath,String dataStr) throws Exception{
         
            if(_resourceContentLabelToFileMap == null) 
                _resourceContentLabelToFileMap = new HashMap<String,String>();
            
            // ItemInfoPack:  [Book]  '12831432432'  v'1' -> 'null' 'd'  "null" {;label1=file1;}
            
            // Determine Beginning & End Index of ObjectType
            int beginIndex = dataStr.indexOf('[') + 1;
            int endIndex   = dataStr.indexOf(']',beginIndex);     // Continue along string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String objectType = dataStr.substring(beginIndex,endIndex).trim();
            
            // Determine Beginning & End Index of Original ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1; // Continue from last position in string
            endIndex   = dataStr.indexOf('\'',beginIndex);
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalItemID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of VersionID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String versionID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of New ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String newItemID = dataStr.substring(beginIndex,endIndex);
            // newItemID = null;  // If restart is to be enabled, we will need the itemid of completed packs.

            // Determine Beginning & End Index of Type
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String type = dataStr.substring(beginIndex,endIndex).trim();
            boolean selectedDirectly = false;
            if(type.compareTo("d")==0) // If directly selected, mark it as such.
                selectedDirectly = true;
            else if(type.compareTo("i")==0) // If directly selected, mark it as such.
                selectedDirectly = false;
            else
                throw new Exception("Data string is invalid.  Unknown type found:  '"+type+"'");

            // Determine Beginning & End Index of New FileName
            beginIndex = dataStr.indexOf('\"',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\"',beginIndex);
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String exportedFileName = dataStr.substring(beginIndex,endIndex).trim();
            if(exportedFileName.compareTo("null")==0) // If not set, make null.
                exportedFileName = null;
            
            // Search for resource object label and value - resource file name
            // Continue searching until you find '}', which is unique to the 
            // Resource Content Info.

            String  label              = null;
            String  resourceFileName   = null;
            boolean isResourcePresent  = false;
            
            // Check for first and last indexes are '{' and '}'.
            int resBeginIndex = dataStr.indexOf('{',endIndex+1);
            int resMidIndex   = dataStr.indexOf(';',resBeginIndex);
            int resEndIndex   = dataStr.indexOf('}',resMidIndex);
            
            if(resEndIndex-resMidIndex > 1){ 
                isResourcePresent = true;
                endIndex = resBeginIndex;
            }//end if(resEndIndex-resMidIndex > 1){ 
			
            while(isResourcePresent){
                
                // Determine Beginning & End Index of Label
                beginIndex = dataStr.indexOf(';',endIndex) + 1; //+ 2 for one place after ;
                endIndex   = dataStr.indexOf('=',beginIndex);
                label      = dataStr.substring(beginIndex,endIndex).trim();
                
                // Determine Beginning & End Index of File Name
                beginIndex       = endIndex+ 1;
                endIndex         = dataStr.indexOf(';',beginIndex);
                resourceFileName = dataStr.substring(beginIndex,endIndex).trim();

		        // Place label=resFileName pair into hashmap
		        _resourceContentLabelToFileMap.put(label,resourceFileName);
		        
		        int lastIndex=dataStr.indexOf('}',endIndex+1);
		        if(lastIndex-endIndex == 1)
		            isResourcePresent = false;
		            
            }//end of while loop
            
            // Initialize Object
            init(selectedDirectly,originalItemID,versionID,objectType,null,null,newItemID);
            
            // Set Any Remaining Variables
            _exportedFileName = exportedFileName;
            _exportedFilePath = filePath;
        }

       /**
        * Find all reference attributes in tree, update ref attr info pack so that
        * it has a handle to that componentDDO directly, then null out existing
        * value as part of first pass of import.  This function will be called recursively
        * on all child components until all are reviewed.                           <BR><BR>
        * @param ddo           - DDO to review along with all subchildren.
        * @param refAttrInfoHT - Reference Attribute Info Pack Hash Table.
        * @param options       - TExportPackageICM.Options object.
        **/
        private void updateRefAttrInfoPacks(DKDDO ddo,Hashtable<String,RefAttrInfoPack> refAttrInfoHT,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.itemInfoPack.updateRefAttrInfoPacks("+obj2String(ddo)+","+obj2String(refAttrInfoHT)+","+obj2String(options)+")");

            ArrayList<DKChildCollection> childCollections = new ArrayList<DKChildCollection>(); // when obtaining data items, collect child collections.

            // List all Data Items
            for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
                Object value = null;
                Short  type  = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                switch(type.intValue()){
                    case DKConstant.DK_CM_DATAITEM_TYPE_DDOOBJECT:
                    case DKConstant.DK_CM_DATAITEM_TYPE_XDOOBJECT:
                    case DKConstant.DK_CM_DATAITEM_TYPE_DATAOBJECTBASE:
                        String name  = ddo.getDataName(dataid);
                        value = ddo.getData(dataid);
                        // Reference Attribute
                        // Find the Ref Attr Info Pack for this component's reference attr.
                        String originalItemID = ((DKPidICM)((DKDDO)ddo).getPidObject()).getItemId();
                        String originalCompID = ((DKPidICM)((DKDDO)ddo).getPidObject()).getComponentId();
                        String versionID      = ((DKPidICM)((DKDDO)ddo).getPidObject()).getVersionNumber();
                        String refAttrName    = name;
                        RefAttrInfoPack refAttrInfoPack = new RefAttrInfoPack(originalItemID, originalCompID, versionID, refAttrName, null,null);
                        refAttrInfoPack = refAttrInfoHT.get(refAttrInfoPack.getKey());
                        if(refAttrInfoPack!=null){
                            // Update Ref Attr Info Pack with the component DDO handle.
                            refAttrInfoPack.setNewComponentDDO(ddo);
                        }
                        // Null out attr value if not already.  Will put back on 2nd pass.
                        if(value!=null){
                            throw new Exception("Item found that has a refernece attribute set.  It should have been set to null by the export operation since it is tracked separately.");
                        }
                        break;
                    case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                        value = ddo.getData(dataid);
                        if(value!=null){
                            if(value instanceof DKChildCollection) // if it is a child collection, add it to a list for printing children later
                                childCollections.add((DKChildCollection)value);
                        }
                        break;
                    default:
                        break;
                }// end swith on data item type
            }// end for all data items in DDO
            
            // Handle Children
            for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
                DKChildCollection childCollection = childCollections.get(i); // get each child collection
                dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
                while(iter.more()){ // While there are children, print each
                    DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                        updateRefAttrInfoPacks(child, refAttrInfoHT,options);
                }
            }

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.itemInfoPack.updateRefAttrInfoPacks("+obj2String(ddo)+","+obj2String(refAttrInfoHT)+","+obj2String(options)+")");
        }

       /**
        * Wipe out all PIDs in the specified DDO, leaving each component with only 
        * object type set.  If the item is versioned, track component Pid Info.
        * This clears out all PID info that pertains only to the original source
        * system.  This function will be called recursively on all child components
        * until all are reviewed.
        *
        * If any existing component IDs are known such as if previous versions of
        * the same component ID were previously imported by the same Export Package
        * (stored in the verCompMapTree), map the known PIDs now, using the previous
        * PID and adjusting the version number.  However, there is no guarantee that
        * the component still exists in the target system.  At this point, Export
        * Package only knows if this is the same component in the source system and
        * where possible should be maintained in the target system.  However, if the
        * child was already deleted in the target system, it cannot be recreated.
        * This method simply notes known mappings from previous versions imported
        * with no knowledge of the current state of the target system.  Subsequent
        * method wipeComponentPidsIfNotExistInVersion() is called from importOverExisting()
        * that will double check against the corresponding existing DDO to see if
        * the component still exists in this version in the target system.  If not,
        * Export Package will attempt to work around the issue by simply replacing
        * it with a nearly identical similar child component.  However, if unique
        * attribute values collide, it will not be possible to import a subsequent
        * version once a child is deleted in the target system that needs to be
        * revived during an import from a source system where that child component
        * still exists.
        *
        * If a mapping is not immediately recognized at this point, that in no 
        * way means a mapping will not be found.  This is just the first point
        * that a mapping might be known.  Subsequent call to mapComponentsBasedOnUniqueAttrs()
        * called from importOverExisting() will employ a more complex algorithm to
        * attempt to find the identical component in the target system if necessary.
        *                                                               <BR><BR>
        * @param ddo            - DDO to review along with all subchildren.
        * @param isVersioned    - protected constant from TExportPackageICM:  YES, NO, UNKNOWN.  If Unknown, the parent must be a root, and it will be looked up.
        * @param verCompMapTree - TreeMap, sorted by componentid (key).  Contains CompInfoPacks.
        * @param options        - TExportPackageICM.Options object.
        **/
        private void wipeAllTrackAndMapVersionedComponentPIDs(DKDDO ddo,int isVersioned,TreeMap<String,CompInfoPack> verCompMapTree, TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.itemInfoPack.wipeAllTrackAndMapVersionedComponentPIDs("+obj2String(ddo)+","+isVersioned+","+obj2String(verCompMapTree)+","+obj2String(options)+")");

            // If isVersioned is UNKNOWN, find out and set correctly.
            if(isVersioned == UNKNOWN){
                if(isRoot(ddo,options)==false) // Must be the root component
                    throw new Exception("Internal Error.  funciton wipeAndTrackVersionedComponentPIDs requires that the root component only be used with the 'UNKNOWN' isVersioned option.");
                if(isVersioned(ddo,options))
                    isVersioned = YES;
                else
                    isVersioned = NO;
            }

            ArrayList<DKChildCollection> childCollections = new ArrayList<DKChildCollection>(); // when obtaining data items, collect child collections.

            // List all Data Items, find children.
            for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
                Object value = null;
                Short  type  = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                switch(type.intValue()){
                    case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                        value = ddo.getData(dataid);
                        if(value!=null){
                            if(value instanceof DKChildCollection) // if it is a child collection, add it to a list for printing children later
                                childCollections.add((DKChildCollection)value);
                        }
                        break;
                    default:
                        break;
                }// end swith on data item type
            }// end for all data items in DDO
            
            // Handle Children
            for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
                DKChildCollection childCollection = childCollections.get(i); // get each child collection
                dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
                while(iter.more()){ // While there are children, clear PIDs on each.
                    DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                    wipeAllTrackAndMapVersionedComponentPIDs(child,isVersioned,verCompMapTree,options);
                }
            }

            // If versioned, 
            if(isVersioned==YES){
                // Determine if already tracked.
                CompInfoPack compInfoPack = new CompInfoPack(ddo);
                String key = compInfoPack.getKey();
                // If yes, map it by replacing it with the known pid
                // An known versioned component mapping might already be known if a previous
                // version of this item was already imported with this Export Package operation.
                // This is only true when a previous version of this component was already imported.
                // As noted in the main comments for this method, this is just the first point
                // that a mapping might be known.  If one is known right now, it makes note of it
                // instead of clearing it as it prepares for a more complex detection procedure.
                if(verCompMapTree.containsKey(key)){ 
                    compInfoPack = verCompMapTree.get(key);
                    String versionNum = ((DKPidICM)ddo.getPidObject()).getVersionNumber();
                    ddo.setPidObject(compInfoPack.getPidObjectCopy(versionNum));
                } else { // If no, then track it, then wipe it.
                    // Track it
                    // Now that this component is found once, take note of it so any subsequent
                    // versions of the same component can be directly mapped in this method instead
                    // of relying on more complex detection procedures later.
                    verCompMapTree.put(key,compInfoPack);
                    // Now null PID Out, since it should not exist when DKDDO.add() is called.
                    DKPidICM blankPid = new DKPidICM();
                    blankPid.setObjectType(ddo.getPidObject().getObjectType());
                    ddo.setPidObject(blankPid);
                }
            } else { // else if not versioned, wipe the pid
                // Now null PID Out, since it should not exist when DKDDO.add() is called.
                DKPidICM blankPid = new DKPidICM();
                blankPid.setObjectType(ddo.getPidObject().getObjectType());
                ddo.setPidObject(blankPid);
            }

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.itemInfoPack.wipeAllTrackAndMapVersionedComponentPIDs("+obj2String(ddo)+","+isVersioned+","+obj2String(verCompMapTree)+","+obj2String(options)+")");
        }

       /**
        * Double check now to make sure that all initially-mapped components still exist
        * in the current version of the item.  If the initially-mapped component does
        * not actually exist in the target's existing copy of this version, wipe out the 
        * PID so that the Export Package can attempt to create it as a new component instead,
        * even though it was truly a subsequent version of the same component in the target
        * system.  It will be marked as not mapped, so the mapping function will take
        * over and attempt to map differently or report an error if it is going to be
        * a problem.
        *
        * Remember from the main comments for wipeAllTrackAndMapVersionedComponentPIDs(),
        * an initial mapping was attempted simply based on previous versions of the same
        * component that might have already been imported.  However, this is no guarantee
        * that the child component exists on this version in the target system.  See the
        * main comments for wipeAllTrackAndMapVersionedComponentPIDs() for more details.
        * 
        * This method is simply going to go over the importedDDO, the in-memory working
        * copy, to check the initial mappings against the existing children found and
        * sorted for convenience in the existingChildrenMap.  If any are not found, that
        * means that somewhere along the line in the target system, the child was deleted,
        * even though it was not deleted in the source system.  Deleted children cannot
        * be revived.  So Export Package will attempt to simply replace it as a new
        * nearly-identical child component.  However, if unique attribute values collide,
        * an error will result.        
        *                                                                           <BR><BR>
        * @param ddo                 - DDO to review along with all subchildren.
        * @param existingChildrenMap - 2-level TreeMap of existing children.  First level
        *                              sorts by component view type name, second sorts
        *                              by componentid.  Contains component DKDDO objects.
        * @param options             - TExportPackageICM.Options object.
        **/
        private void wipeComponentPidsIfNotExistInVersion(DKDDO ddo,TreeMap<String,TreeMap<String,DKDDO>> existingChildrenMap,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.itemInfoPack.wipeComponentPidsIfNotExistInVersion("+obj2String(ddo)+","+obj2String(existingChildrenMap)+","+obj2String(options)+")");

            ArrayList<DKChildCollection> childCollections = new ArrayList<DKChildCollection>(); // when obtaining data items, collect child collections.

            // List all Data Items, find children.
            for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
                Object value = null;
                Short  type  = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
                switch(type.intValue()){
                    case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                        value = ddo.getData(dataid);
                        if(value!=null){
                            if(value instanceof DKChildCollection) // if it is a child collection, add it to a list for printing children later
                                childCollections.add((DKChildCollection)value);
                        }
                        break;
                    default:
                        break;
                }// end swith on data item type
            }// end for all data items in DDO
            
            // Handle Children
            for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
                DKChildCollection childCollection = childCollections.get(i); // get each child collection
                dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
                while(iter.more()){ // While there are children, clear PIDs on each.
                    DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                    wipeComponentPidsIfNotExistInVersion(child,existingChildrenMap, options);
                }
            }

            // Look up in existing tree map.  This compares for the first time against the components
            // actually found on the existing item in the target system for this item version.
            // Look up all chldren of the specified object type.
            String objectType = ddo.getObjectType();
            TreeMap<String,DKDDO> existingChildrenSubMap = existingChildrenMap.get(objectType); // Get the sub-map of all components of that type, sorted by component id.
            // Determine if exists in that submap, based on key = component id.
            // If no component ID is found, then that means there is no initial mapping determined.
            // This does not mean no mapping will be found, just that the initial mapping based
            // previous versions imported is not valid in the target system.
            String compId = ((DKPidICM)ddo.getPidObject()).getComponentId();  // In V8.1 & V8.2, a blank component ID is an empty string, "".  In V8.3, a blank, unmapped component ID is a single-space string, " ".
            if((existingChildrenSubMap==null)||(existingChildrenSubMap.size()<=0)||(existingChildrenSubMap.containsKey(compId)==false)){ // If it doesn't exist in the current version,...   This checks the initially-mapped expected target component ID against those component IDs actually found in the target system's existing DDO for this item version.
                if(options.getPrintDebugEnable()) System.out.println("  Component type '"+objectType+"' of compId '"+compId+"' doesn't exists in this version.  Wiping out PID so that it may be considered a new component.");
                // Wipe out PID so that secondary mapping function may attempt to map or throw error.
                DKPidICM blankPid = new DKPidICM();
                blankPid.setObjectType(ddo.getPidObject().getObjectType());
                ddo.setPidObject(blankPid);
            }

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.itemInfoPack.wipeComponentPidsIfNotExistInVersion("+obj2String(ddo)+","+obj2String(existingChildrenMap)+","+obj2String(options)+")");
        }

    }//end ItemInfoPack class.
    
   /** 
    * Info Pack to describe folder information so that it may be rebuilt.  One pack
    * includes one folder to content relationship.                              
    **/
    class FolderInfoPack{

        String _packType = "FolderInfoPack";
        String _originalFolderItemID;
        String _originalContentItemID;
        String _anyFolderVersionExported;   // This item will have to access the imported items, and it needs a version id for the key.
        String _anyContentVersionExported;
       /**
        * @param originalFolderItemID      - Item ID of the exported folder.
        * @param originalContentItemID     - Item ID of the exported folder content.
        * @param anyFolderVersionExported  - Any source item version number that will be exported / imported.
        * @param anyContentVersionExported - Any target item version number that will be exported / imported.
        **/
        public FolderInfoPack(String originalFolderItemID,String originalContentItemID,String anyFolderVersionExported,String anyContentVersionExported){
            init(originalFolderItemID,originalContentItemID,anyFolderVersionExported,anyContentVersionExported);
        }
       /**
        * @param folderDDO  - Root DDO for the folder.
        * @param contentDDO - Root DDO for the content.
        **/
        public FolderInfoPack(DKDDO folderDDO,DKDDO contentDDO){
            init(((DKPidICM)folderDDO.getPidObject()).getItemId(),((DKPidICM)contentDDO.getPidObject()).getItemId(),((DKPidICM)folderDDO.getPidObject()).getVersionNumber(),((DKPidICM)contentDDO.getPidObject()).getVersionNumber());
        }
       /**
        * Create a Folder Info Pack from the data string in the exact format returned by toString().  <BR><BR>
        * @param dataStr - Data string in the exact format returned by toString().
        **/
        public FolderInfoPack(String dataStr) throws Exception{
            fromString(dataStr);    
        }
       /**
        * @param originalFolderItemID      - Item ID of the exported folder.
        * @param originalContentItemID     - Item ID of the exported folder content.
        * @param anyFolderVersionExported  - Any source item version number that will be exported / imported.
        * @param anyContentVersionExported - Any target item version number that will be exported / imported.
        **/
        private void init(String originalFolderItemID,String originalContentItemID,String anyFolderVersionExported, String anyContentVersionExported){
            _originalFolderItemID      = originalFolderItemID;
            _originalContentItemID     = originalContentItemID;
            _anyFolderVersionExported  = anyFolderVersionExported;
            _anyContentVersionExported = anyContentVersionExported;
        }
       /**
        * Add the information in this packet to the imported item and mark that item as
        * changed in phase 2 so that it may be updated.                             <BR><BR>
        * @param itemInfoTree   - TreeMap containing all ItemInfoPacks.
        * @param omittedItemMap - Item Map of all items omitted.  This pack should not
        *                         be applied if any referenced items appear in the omitted
        *                         map.  1-Level TreeMap containing ItemInfoPacks.  If 
        *                         'null' specified, none are omitted.
        * @param options        - TExportPackageICM.Options object.
        **/
        public void addToImportedItem(TreeMap<String, ItemInfoPack> itemInfoTree, TreeMap<String, ItemInfoPack> omittedItemMap,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.FolderInfoPack.addToImportedItem("+obj2String(itemInfoTree)+","+obj2String(omittedItemMap)+","+obj2String(options)+")");

            // Get the folder and content DDOs from the items list.  Since folders
            // are independent of versions, choose any version available.
            ItemInfoPack folderItemInfoPack  = new ItemInfoPack(false,_originalFolderItemID,_anyFolderVersionExported,null); // First, get the key.
            folderItemInfoPack = (ItemInfoPack) itemInfoTree.get(folderItemInfoPack.getKey());    // Get actual info pack using key.
            ItemInfoPack contentItemInfoPack  = new ItemInfoPack(false,_originalContentItemID,_anyContentVersionExported,null); // First, get the key.
            contentItemInfoPack = (ItemInfoPack) itemInfoTree.get(contentItemInfoPack.getKey());  // Get actual info pack using key.
            if(folderItemInfoPack==null)
                throw new Exception("FolderInfoPack refers to a Folder that cannot be found among the imported items list.");
            if(contentItemInfoPack==null)
                throw new Exception("FolderInfoPack refers to a Content that cannot be found among the imported items list.");
            // Only proceed if none are omitted
            if(    (omittedItemMap==null)
                || (    (!omittedItemMap.containsKey( folderItemInfoPack.getKey()))
                     && (!omittedItemMap.containsKey(contentItemInfoPack.getKey())) ) ){

                DKDDO folderDDO  = folderItemInfoPack.getImportedItem();
                DKDDO contentDDO = contentItemInfoPack.getImportedItem();
                // Get the DKFolder collection.
                short dataid = folderDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER); 
                if(dataid==0) // if it does not exist due to the unique import scenario, set it up.
                    dataid = setupFolder(folderDDO);
                DKFolder dkFolder = (DKFolder) folderDDO.getData(dataid); // Get the DKFolder collection.
                // Add the content to the folder.
                dkFolder.addElement(contentDDO);
                // Mark folder as changed in the info packet.
                folderItemInfoPack.setChangedInPhase2(true);
            }//end if(    (!omittedItemMap.containsKey(folderItemInfoPack.getKey()))

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.FolderInfoPack.addToImportedItem("+obj2String(itemInfoTree)+","+obj2String(omittedItemMap)+","+obj2String(options)+")");
        }
       /**
        * Get the key to this info pack.                                            <BR><BR>
        * @return Returns the key for this pack.
        **/
        public String getKey(){
            return(_originalFolderItemID+_originalContentItemID);
        }
       /**
        * Get the name of this information pack as it will appear in the toString() method.  <BR><BR>
        * @return Returns the info pack type name.
        **/
        public String getPackType(){
            return(_packType);   
        }
        /**
        * Setup the DDO as Folder.  This is automatically done when creating a new item using
        * the DKDatastoreICM.createDDO() method or during item retrieval for existing items.  
        * In the special case of Import from XML, this is not completed automatically.  This tool
        * will setup the folder for use during import.  Users are recommended to let
        * DKDatastoreICM::createDDO() or item retrieval to complete this properly for them.
        * The added folder from this method will only produce working folders on items created
        * with semantic type folder.  This method may not be used if the DDO already has any
        * part of the folder already setup.                                         <BR><BR>
        * @param ddo  DDO to setup with as a Folder
        * @return Returns the dataid of the new data item for the folder.
        **/
        private short setupFolder(DKDDO ddo) throws DKUsageError{
            short dataId = ddo.addData(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER); // add the data property or the first time.  This is not a system attribute, but added as a special attribute.
            ddo.addDataProperty(dataId, DKConstant.DK_CM_PROPERTY_TYPE, new Short(DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO));
            ddo.setData(dataId, new DKFolder()); // Set the attribute with the value passed in as a java.lang.Object.
            return(dataId);
        }//end setupFolder
                
       /**
        * Table Header for pringing a list of these packs.                          <BR><BR>
        * @return Returns column information for a table listing of these packs.
        **/
        public String tableHeader(){
            String newline = System.getProperty("line.separator");  // Get the system's newline separator.
            String line1 = "Pack Type        Folder Item ID                 Content Item ID               Any Version In Exported Items";
            String line2 = "---------------  ----------------------------   ----------------------------  --------------------------------";
            //              FolderInfoPack:  '12345678901234567890123456' \ '12345678901234567890123456'  (folder v'1' content v'1')
            return(line1+newline+line2);
        }
       /**
        * String representation of pack.                                            <BR><BR>
        * @return Returns a string representation of the pack.
        **/
        public String toString(){
            // FolderInfoPack: '12831432432' \ 'null' (folder v'1' content v'1')
            return(_packType+":  '"+_originalFolderItemID+"' -> '"+_originalContentItemID+"'  (folder v'"+_anyFolderVersionExported+"' content v'"+_anyContentVersionExported+"')");
        }
       /**
        * Sets properties to values from the information generated by the toString()
        * method.                                                                   <BR><BR>
        * @param dataStr - Data string in the exact format returned by toString().
        **/
        private void fromString(String dataStr) throws Exception{

            // FolderInfoPack: '12831432432' \ 'null'
            
            // Determine Beginning & End Index of Original Folder ItemID
            int beginIndex = dataStr.indexOf('\'') + 1;
            int endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalFolderItemID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of Original Content ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalContentItemID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of Any Folder Version Exported.
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String anyExportedFolderVersion = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of Any Cntent Versuib Exoirted.
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String anyExportedContentVersion = dataStr.substring(beginIndex,endIndex);

            // Initialize Object
            init(originalFolderItemID,originalContentItemID,anyExportedFolderVersion,anyExportedContentVersion);
                        
            // Set Any Remaining Variables
            // <none>
        }
    }

   /** 
    * Info Pack to describe link information so that it may be rebuilt.  One pack
    * includes one link from a source to target and an optional link item.
    **/
    class LinkInfoPack{

        String _packType = "LinkInfoPack";
        String _originalSourceItemID;
        String _originalTargetItemID;
        String _originalLinkItemID;
        String _linkTypeName;
        String _anySourceVersionExported;    // This item will have to access the imported items, and it needs a version id for the key.
        String _anyTargetVersionExported;
        String _anyLinkItemVersionExported;
       /**
        * @param linkTypeName               - Name of the Link Type.
        * @param originalSourceItemID       - Item ID of the exported source item of the link.
        * @param originalTargetItemID       - Item ID of the exported target item of the link.
        * @param originalLinkItemID         - Item ID of the exported optional link item of the link, or 'null'.
        * @param anySourceVersionExported   - Any source item version number that will be exported / imported.
        * @param anyTargetVersionExported   - Any target item version number that will be exported / imported.
        * @param anyLinkItemVersionExported - Any link item version number that will be exported / imported.
        **/
        public LinkInfoPack(String linkTypeName,String originalSourceItemID,String originalTargetItemID,String originalLinkItemID,String anySourceVersionExported, String anyTargetVersionExported, String anyLinkItemVersionExported){
            init(linkTypeName,originalSourceItemID,originalTargetItemID,originalLinkItemID,anySourceVersionExported,anyTargetVersionExported,anyLinkItemVersionExported);
        }
       /**
        * @param dkLink - DKLink object describing the link.
        **/
        public LinkInfoPack(DKLink dkLink){
            String linkTypeName = dkLink.getTypeName();
            DKDDO  sourceDDO    = (DKDDO) dkLink.getSource();
            DKDDO  targetDDO    = (DKDDO) dkLink.getTarget();
            DKDDO  linkDDO      = (DKDDO) dkLink.getLinkItem();
            
            if(linkDDO==null)
                init(linkTypeName,((DKPidICM)sourceDDO.getPidObject()).getItemId(),((DKPidICM)targetDDO.getPidObject()).getItemId(),null,((DKPidICM)sourceDDO.getPidObject()).getVersionNumber(),((DKPidICM)targetDDO.getPidObject()).getVersionNumber(),null);
            else
                init(linkTypeName,((DKPidICM)sourceDDO.getPidObject()).getItemId(),((DKPidICM)targetDDO.getPidObject()).getItemId(),((DKPidICM)linkDDO.getPidObject()).getItemId(),((DKPidICM)sourceDDO.getPidObject()).getVersionNumber(),((DKPidICM)targetDDO.getPidObject()).getVersionNumber(),((DKPidICM)linkDDO.getPidObject()).getVersionNumber());
        }
       /**
        * Create an Link Info Pack from the data string in the exact format returned by toString().   <BR><BR>
        * @param dataStr - Data string in the exact format returned by toString().
        **/
        public LinkInfoPack(String dataStr) throws Exception{
            fromString(dataStr);    
        }
       /**
        * @param linkTypeName         - Name of the Link Type.
        * @param originalSourceItemID       - Item ID of the exported source item of the link.
        * @param originalTargetItemID       - Item ID of the exported target item of the link.
        * @param originalLinkItemID         - Item ID of the exported optional link item of the link, or 'null'.
        * @param anySourceVersionExported   - Any source item version number that will be exported / imported.
        * @param anyTargetVersionExported   - Any target item version number that will be exported / imported.
        * @param anyLinkItemVersionExported - Any link item version number that will be exported / imported.
        **/
        private void init(String linkTypeName,String originalSourceItemID,String originalTargetItemID,String originalLinkItemID,String anySourceVersionExported,String anyTargetVersionExported,String anyLinkItemVersionExported){
            _linkTypeName               = linkTypeName;
            _originalSourceItemID       = originalSourceItemID;
            _originalTargetItemID       = originalTargetItemID;
            _originalLinkItemID         = originalLinkItemID;
            _anySourceVersionExported   = anySourceVersionExported;
            _anyTargetVersionExported   = anyTargetVersionExported;
            _anyLinkItemVersionExported = anyLinkItemVersionExported;
        }
       /**
        * Add the information in this packet to the imported item and mark that item as
        * changed in phase 2 so that it may be updated.                             <BR><BR>
        * @param itemInfoTree   - TreeMap containing all ItemInfoPacks.
        * @param omittedItemMap - Item Map of all items omitted.  This pack should not
        *                         be applied if any referenced items appear in the omitted
        *                         map.  1-Level TreeMap containing ItemInfoPacks.  If 
        *                         'null' specified, none are omitted.
        * @param options        - TExportPackageICM.Options object.
        **/
        public void addToImportedItem(TreeMap<String, ItemInfoPack> itemInfoTree,TreeMap<String, ItemInfoPack> omittedItemMap,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.LinkInfoPack.addToImportedItem("+obj2String(itemInfoTree)+","+obj2String(omittedItemMap)+","+obj2String(options)+")");

            // Get the source, target, & link item DDOs from the items list.  Since folders
            // are independent of versions, choose any version available.
            ItemInfoPack sourceItemInfoPack  = new ItemInfoPack(false,_originalSourceItemID,_anySourceVersionExported,null); // First, get the key.
            sourceItemInfoPack = (ItemInfoPack)itemInfoTree.get(sourceItemInfoPack.getKey());  // Get actual info pack using key.
            ItemInfoPack targetItemInfoPack  = new ItemInfoPack(false,_originalTargetItemID,_anyTargetVersionExported,null); // First, get the key.
            targetItemInfoPack = (ItemInfoPack)itemInfoTree.get(targetItemInfoPack.getKey());  // Get actual info pack using key.
            if(sourceItemInfoPack==null)
                throw new Exception("LinkInfoPack refers to a Source that cannot be found among the imported items list.");
            if(targetItemInfoPack==null)
                throw new Exception("LinkInfoPack refers to a Target that cannot be found among the imported items list.");
            // Only proceed if none are omitted
            if(    (omittedItemMap==null)
                || (    (!omittedItemMap.containsKey(sourceItemInfoPack.getKey()))
                     && (!omittedItemMap.containsKey(targetItemInfoPack.getKey())) ) ){

                DKDDO sourceDDO   = sourceItemInfoPack.getImportedItem();
                if(sourceDDO==null)
                    throw new Exception("Internal Error:  New DDO for Source not found in it's ItemInfoPack.");
                DKDDO targetDDO   = targetItemInfoPack.getImportedItem();
                if(targetDDO==null)
                    throw new Exception("Internal Error:  New DDO for Target not found in it's ItemInfoPack.");
                DKDDO linkItemDDO = null;
                ItemInfoPack linkItemInfoPack = null;
                boolean linkItemIsOmitted = false;  // If there is a link item and it is omitted, omit this pack.
                if(_originalLinkItemID!=null){ // if link item exists
                    linkItemInfoPack = new ItemInfoPack(false,_originalLinkItemID,_anyLinkItemVersionExported,null); // First, get the key.
                    linkItemInfoPack = (ItemInfoPack)itemInfoTree.get(linkItemInfoPack.getKey());  // Get actual info pack using key.
                    if(linkItemInfoPack==null)
                        throw new Exception("LinkInfoPack refers to a Link Item that cannot be found among the imported items list.");
                    // Only proceed if none are omitted
                    if((omittedItemMap==null) || !omittedItemMap.containsKey(sourceItemInfoPack.getKey()))
                        linkItemDDO = linkItemInfoPack.getImportedItem();
                    else // Otherwise the link item is omitted, tell outer loop to stop
                        linkItemIsOmitted = true;
                }//end if(_originalLinkItemID!=null){ // if link item exists
                // Only proceed if none are omitted.
                if(!linkItemIsOmitted){ // If no link item exists, will continue.
                    // Create a link,
                    DKLink dkLink = new DKLink(_linkTypeName,sourceDDO,targetDDO,linkItemDDO);
                    // Add Link to Source
                    SLinksICM.addLinkToDDO(dkLink,sourceDDO);
                    // Mark source as changed in the info packet.
                    sourceItemInfoPack.setChangedInPhase2(true);
                }//end if(!linkItemIsOmitted){ // If no link item exists, will continue.
            }//end if(    (!omittedItemMap.containsKey(sourceItemInfoPack.getKey()))

            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.LinkInfoPack.addToImportedItem("+obj2String(itemInfoTree)+","+obj2String(options)+")");
        }
       /**
        * Get the key to this info pack.                                            <BR><BR>
        * @return Returns the key for this pack.
        **/
        public String getKey(){
            return(_linkTypeName+_originalSourceItemID+_originalTargetItemID);
        }
       /**
        * Get the name of this information pack as it will appear in the toString() method.  <BR><BR>
        * @return Returns the info pack type name.
        **/
        public String getPackType(){
            return(_packType);   
        }
       /**
        * Table Header for pringing a list of these packs.                          <BR><BR>
        * @return Returns column information for a table listing of these packs.
        **/
        public String tableHeader(){
            String newline = System.getProperty("line.separator");  // Get the system's newline separator.
            String line1 = "Pack Type      Link Type Name     Source Item ID                  Target Item ID                Link Item ID (optional)       Any Version In Exported Items";
            String line2 = "-------------  -----------------  ----------------------------    ----------------------------  ----------------------------  -----------------------------";
            //              LinkInfoPack:  [Contains]         '12345678901234567890123456' -> '12345678901234567890123456', '12345678901234567890123456'  (Src v'1' Tgt v'1' Li v'1')
            return(line1+newline+line2);
        }
       /**
        * String representation of pack.                                            <BR><BR>
        * @return Returns a String representation of the pack.
        **/
        public String toString(){
            // Calculate white space padding to keep columns in order.
            String padding = "";
            for(int i=_linkTypeName.length(); i<32; i++)
                padding+=" ";
            String originalLinkItemIdMsgStr = null;
            if(_originalLinkItemID==null) originalLinkItemIdMsgStr = "null                      ";
            else                          originalLinkItemIdMsgStr = _originalLinkItemID;
            String anyLinkItemVersionExportedMsgStr = null;
            if(_anyLinkItemVersionExported==null) anyLinkItemVersionExportedMsgStr = "null";
            else                                  anyLinkItemVersionExportedMsgStr = _anyLinkItemVersionExported;
            // LinkInfoPack: [Contains] '12831432432' -> '1243843873', 'null'  (Src v'1' Tgt v'1' Li v'1')
            return(_packType+":  ["+_linkTypeName+"]"+padding+"  '"+_originalSourceItemID+"' -> '"+_originalTargetItemID+"', '"+originalLinkItemIdMsgStr+"'  (Src v'"+_anySourceVersionExported+"' Tgt v'"+_anyTargetVersionExported+"' Li v'"+anyLinkItemVersionExportedMsgStr+"')");
        }
       /**
        * Sets properties to values from the information generated by the toString()
        * method.                                                                   <BR><BR>
        * @param dataStr - Data string in the exact format returned by toString().
        **/
        private void fromString(String dataStr) throws Exception{

            // LinkInfoPack: [Contains] '12831432432' -> '1243843873', 'null'
            
            // Determine Beginning & End Index of Link Type Name
            int beginIndex = dataStr.indexOf('[') + 1;
            int endIndex   = dataStr.indexOf(']',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String linkTypeName = dataStr.substring(beginIndex,endIndex).trim();

            // Determine Beginning & End Index of Original Source ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalSourceItemID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of Original Source ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalTargetItemID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of Original Link Item ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalLinkItemID = dataStr.substring(beginIndex,endIndex).trim();
            if(originalLinkItemID.compareTo("null")==0) // If not set, make null.
                originalLinkItemID = null;

            // Determine Beginning & End Index of Original Link Item ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String anySourceVersionExported = dataStr.substring(beginIndex,endIndex).trim();
            if(anySourceVersionExported.compareTo("null")==0) // If not set, make null.
                anySourceVersionExported = null;

            // Determine Beginning & End Index of Original Link Item ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String anyTargetVersionExported = dataStr.substring(beginIndex,endIndex).trim();
            if(anyTargetVersionExported.compareTo("null")==0) // If not set, make null.
                anyTargetVersionExported = null;

            // Determine Beginning & End Index of Original Link Item ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String anyLinkItemVersionExported = dataStr.substring(beginIndex,endIndex).trim();
            if(anyLinkItemVersionExported.compareTo("null")==0) // If not set, make null.
                anyLinkItemVersionExported = null;

            // Initialize Object
            init(linkTypeName,originalSourceItemID,originalTargetItemID,originalLinkItemID,anySourceVersionExported,anyTargetVersionExported,anyLinkItemVersionExported);

            // Set Any Remaining Variables
            // <none>
        }
    }

   /** 
    * Info Pack to describe reference attr value information so that it may be rebuilt.
    * One pack includes the itemid, componentid, refattrname, and target itemid.
    **/
    class RefAttrInfoPack{

        String _packType = "RefAttrInfoPack";
        String _originalItemID;
        String _versionID;
        String _originalCompID;
        String _originalTargetItemID;
        String _targetVersionID;
        String _attrName;
        DKDDO  _newChildCompDDO;
       /**
        * @param originalItemID       - Original Item ID of the exported item.
        * @param originalCompID       - Original Component ID of the child component.
        * @param versionID            - Version ID of the exported item.
        * @param refAttrName          - Name of the reference attribute with the target value.
        * @param originalTargetItemID - Item ID of the exported target.
        * @param targetVersionID      - Version ID of the exported target.
        **/
        public RefAttrInfoPack(String originalItemID,String originalCompID,String versionID,String refAttrName,String originalTargetItemID,String targetVersionID){
            init(originalItemID, originalCompID, versionID, refAttrName, originalTargetItemID, targetVersionID);
        }
       /**
        * @param childDDO    - Child DDO that contains the reference attribute.
        * @param refAttrName - Name of the reference attribute with the target value.
        **/
        public RefAttrInfoPack(DKDDO childDDO,String refAttrName) throws DKUsageError, Exception{
            String originalItemID = ((DKPidICM)childDDO.getPidObject()).getItemId();
            String versionID      = ((DKPidICM)childDDO.getPidObject()).getVersionNumber();
            String originalCompID = ((DKPidICM)childDDO.getPidObject()).getComponentId();
            short dataid = childDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,refAttrName);
            if(dataid==0)
                throw new Exception("The specified child component (ITEMID = '"+originalItemID+"', COMPID = '"+originalCompID+"') does not contain the specified reference attribute '"+refAttrName+"'.");
            DKDDO targetDDO = (DKDDO) childDDO.getData(dataid); 
            if(targetDDO==null)
                throw new Exception("The value of the reference attribute '"+refAttrName+"' is 'null'.  Only reference attributes with non-null values need to be tracked.");
            String originalTargetItemID = ((DKPidICM)targetDDO.getPidObject()).getItemId();
            String targetVersionID      = ((DKPidICM)targetDDO.getPidObject()).getVersionNumber();
            init(originalItemID, originalCompID, versionID, refAttrName, originalTargetItemID, targetVersionID);
        }
       /**
        * Create a Ref Attr Info Pack from the data string in the exact format returned by toString().  <BR><BR>
        * @param dataStr - Data string in the exact format returned by toString().
        **/
        public RefAttrInfoPack(String dataStr) throws Exception{
            fromString(dataStr);    
        }
       /**
        * @param originalItemID       - Original Item ID of the exported item.
        * @param originalCompID       - Original Component ID of the child component.
        * @param versionID            - Version ID of the exported item.
        * @param refAttrName          - Name of the reference attribute with the target value.
        * @param originalTargetItemID - Item ID of the exported target.
        * @param targetVersionID      - Version ID of the exported target.
        **/
        private void init(String originalItemID,String originalCompID,String versionID,String refAttrName,String originalTargetItemID,String targetVersionID){
            _originalItemID       = originalItemID;
            _originalCompID       = originalCompID;
            _versionID            = versionID;
            _attrName             = refAttrName;
            _originalTargetItemID = originalTargetItemID;
            _targetVersionID      = targetVersionID;
            _newChildCompDDO      = null;
        }
       /**
        * Add the information in this packet to the imported item and mark that item as
        * changed in phase 2 so that it may be updated.                             <BR><BR>
        * @param itemInfoTree     - TreeMap containing all ItemInfoPacks.
        * @param completedItemMap - Item Map of all items that already completed this
        *                           phase in the import process.  If this pack originates
        *                           from a completed item in this phase, just skip.
        *                           1-Level TreeMap containing ItemInfoPacks.  If 
        *                           'null' specified, none are marked complete.
        * @param omittedItemMap   - Item Map of all items omitted.  This pack should not
        *                           be applied if any referenced items appear in the omitted
        *                           map.  1-Level TreeMap containing ItemInfoPacks.  If 
        *                           'null' specified, none are omitted.
        * @param options          - TExportPackageICM.Options object.
        **/
        public void addToImportedItem(TreeMap<String, ItemInfoPack> itemInfoTree, TreeMap<String, ItemInfoPack> completedItemMap, TreeMap omittedItemMap,TExportPackageICM.Options options) throws DKException, Exception{
            if(options.getPrintTraceEnable()) System.out.println("+TExportPackageICM.RefAttrInfoPack.addToImportedItem("+obj2String(itemInfoTree)+","+obj2String(omittedItemMap)+","+obj2String(options)+")");

            // Get the item and target DDOs from the items list.  
            ItemInfoPack itemInfoPack  = new ItemInfoPack(false,_originalItemID,_versionID,null); // First, get the key.
            itemInfoPack = (ItemInfoPack) itemInfoTree.get(itemInfoPack.getKey());                // Get actual info pack using key.
            ItemInfoPack targetItemInfoPack  = new ItemInfoPack(false,_originalTargetItemID,_targetVersionID,null); // Get the key.
            targetItemInfoPack = (ItemInfoPack) itemInfoTree.get(targetItemInfoPack.getKey());    // Get actual info pack using key.
            if(itemInfoPack==null)
                throw new Exception("RefAttrInfoPack refers to a Source that cannot be found among the imported items list.");
            if(targetItemInfoPack==null)
                throw new Exception("RefAttrInfoPack refers to a Target that cannot be found among the imported items list.");
            // Make sure that this pack isn't already completed
            if((completedItemMap==null) || (!completedItemMap.containsKey(itemInfoPack.getKey()))){
                // Only proceed if none are omitted
                if(    (omittedItemMap==null)
                    || (    (!omittedItemMap.containsKey(      itemInfoPack.getKey()))
                        && (!omittedItemMap.containsKey(targetItemInfoPack.getKey())) ) ){
                    DKDDO itemDDO   = itemInfoPack.getImportedItem();
                    DKDDO targetDDO = targetItemInfoPack.getImportedItem();
                    // Set the Reference Attribute in the Component DDO we already have a handle to.
                    short dataid = _newChildCompDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,_attrName); 
                    if(dataid==0) // If it does not exist, throw error.
                        throw new Exception("RefAttrInfoPack indicates that item '"+_originalItemID+"' component '"+_originalCompID+"', should have refernece attribute '"+_attrName+"', but it is not found.  The definition in the target system might be different.");
                    _newChildCompDDO.setData(dataid, targetDDO);
                    // Mark item as changed in the info packet.
                    itemInfoPack.setChangedInPhase2(true);
                    
                }//end if(    (!omittedItemMap.containsKey(itemInfoPack.getKey()))
            }//end if(((completedItemMap==null) || (!completedItemMap.containsKey(itemInfoPack.getKey()))){
            
            if(options.getPrintTraceEnable()) System.out.println("-TExportPackageICM.RefAttrInfoPack.addToImportedItem("+obj2String(itemInfoTree)+","+obj2String(omittedItemMap)+","+obj2String(options)+")");
        }
       /**
        * Get the key to this info pack.                                            <BR><BR>
        * @return Returns the key for this pack.
        **/
        public String getKey(){
            return(_originalItemID+_versionID+_originalCompID+_attrName);
        }
       /**
        * Get the name of this information pack as it will appear in the toString() method.  <BR><BR>
        * @return Returns the info pack type name.
        **/
        public String getPackType(){
            return(_packType);   
        }
       /**
        * Determines if the original target is the item version specified.          <BR><BR>
        * @param originalTargetItemID - Original Target Item ID.
        * @param targetVersionID      - Version ID of the original target.
        * @return Returns 'true' if that is the target, 'false' if not.
        **/
        public boolean isOriginalTarget(String originalTargetItemID, String targetVersionID){
            boolean retval = false;
            if(    (originalTargetItemID.compareTo(_originalTargetItemID)==0) 
                && (targetVersionID.compareTo(_targetVersionID)==0) ){
                retval = true;                    
                }
            else
                retval = false;
            
            return(retval);
        }
       /**
        * During import, once the new component DDO is known, this info pack
        * will be updated with that value so that it may be easily updated in the
        * 2nd pass.                                                                 <BR><BR>
        * @param compDDO - Reference to component DDO in the target system.
        **/
        public void setNewComponentDDO(DKDDO compDDO){
            _newChildCompDDO = compDDO;   
        }
        
       /**
        * Table Header for pringing a list of these packs.                          <BR><BR>
        * @return Returns column information for a table listing of these packs.
        **/
        public String tableHeader(){
            String newline = System.getProperty("line.separator");  // Get the system's newline separator.
            String line1 = "Pack Type         Ref Attr Name      Item ID                       Component ID          VersionID    Target Item ID                TargetVer";
            String line2 = "----------------  -----------------  ----------------------------  --------------------  ---------    ----------------------------  ---------";
            //              RefAttrInfoPack:  [myRef]            '12345678901234567890123456', '123456789012345678'  v'123456' -> '12345678901234567890123456'  v'123456' 
            return(line1+newline+line2);
        }
       /**
        * String representation of pack.                                            <BR><BR>
        * @return Returns a string representation of pack.
        **/
        public String toString(){
            // Calculate white space padding to keep columns in order.
            String attrNamePadding = "";
            for(int i=_attrName.length(); i<32; i++)
                attrNamePadding+=" ";
            String versionPadding = "";
            for(int i=_versionID.length(); i<6; i++)
                versionPadding+=" ";
            String targetVersionPadding = "";
            for(int i=_targetVersionID.length(); i<6; i++)
                targetVersionPadding+=" ";
            // RefAttrInfoPack: [myRef] '12831432432', '12094387173' v'123456' -> '1243843873' v'123456'
            return(_packType+":  ["+_attrName+"]"+attrNamePadding+"  '"+_originalItemID+"', '"+_originalCompID+"'  v'"+_versionID+"'"+versionPadding+" -> '"+_originalTargetItemID+"'  v'"+_targetVersionID+"'"+targetVersionPadding+"");
        }
       /**
        * Sets properties to values from the information generated by the toString()
        * method.                                                                   <BR><BR>
        * @param dataStr - Data string in the exact format returned by toString().
        **/
        private void fromString(String dataStr) throws Exception{

            // RefAttrInfoPack: [myRef] '12831432432', '12094387173' v'123456' -> '1243843873' v'123456'
            
            // Determine Beginning & End Index of Ref Attr Name
            int beginIndex = dataStr.indexOf('[') + 1;
            int endIndex   = dataStr.indexOf(']',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String refAttrName = dataStr.substring(beginIndex,endIndex).trim();

            // Determine Beginning & End Index of Original ItemID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalItemID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of Original Component ID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalCompID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of VersionID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String versionID = dataStr.substring(beginIndex,endIndex);

            // Determine Beginning & End Index of Target Item ID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String originalTargetItemID = dataStr.substring(beginIndex,endIndex).trim();
            if(originalTargetItemID.compareTo("null")==0) // If not set, make null.
                originalTargetItemID = null;

            // Determine Beginning & End Index of target VersionID
            beginIndex = dataStr.indexOf('\'',endIndex+1) + 1;
            endIndex   = dataStr.indexOf('\'',beginIndex);  // Continue from previous position in string.
            if((beginIndex==0) || (endIndex==-1))
                throw new Exception("Data string is invalid.  It is missing identifiers and/or information.  Data String:  "+dataStr);
            String targetVersionID = dataStr.substring(beginIndex,endIndex).trim();
            if(targetVersionID.compareTo("null")==0) // If not set, make null.
                targetVersionID = null;

            // Initialize Object
            init(originalItemID, originalCompID, versionID, refAttrName, originalTargetItemID, targetVersionID);
                        
            // Set Any Remaining Variables
            // <none>
        }
    }

   /**
    * Info Pack to describe component mapping information.  Specifically designed for
    * versioned component PID mapping with unique attributes.  Simply by creating and keeping
    * this info pack, the mapping will automatically be tracked because this will hang onto
    * the reference of the component DDO.  This info pack is only used on the import operation
    * and is not persistent or used like the other info packs are used.
    **/
    class CompInfoPack{
        
        private String   _packType = "CompInfoPack";
        private String   _originalCompID;
        private DKDDO    _componentDDO;
        
       /**
        * @param componentDDO - DKDDO of the component that still has the PID with the original component ID.
        **/
        public CompInfoPack(DKDDO componentDDO) throws Exception{
            init(componentDDO);
        }
       /**
        * @param componentDDO - DKDDO of the component that still has the PID with the original component ID.
        **/
        private void init(DKDDO componentDDO) throws Exception{
            DKPidICM pid      = (DKPidICM)componentDDO.getPidObject(); // Get the Pid object to get info from.

            _originalCompID   = pid.getComponentId();
            _componentDDO     = componentDDO;
            
            // Validate Pid
            if(_originalCompID==null)
                throw new Exception("Component ID is null.  Expected original Component ID from source system.");
            if(_originalCompID.compareTo("")==0)
                throw new Exception("Component ID is empty ('').  Expected original Component ID from source system.");
        }
       
       /**
        * Get the key to this info pack.                                            <BR><BR>
        * @return Returns the key for this pack.
        **/
        public String getKey(){
            return(_originalCompID);   
        }
       /**
        * Get a copy of the PID of the component on the target system.  Will set version
        * to the version specified.  Null if not yet imported.  Will get actual
        * reference to object held by this package.                                 <BR><BR>
        * @param versionNum - Set the PID reference to the specified version number.  
        * @return Returns the existing PID reference for the target datastore or 'null' if not yet imported.
        **/
        public DKPidICM getPidObjectCopy(String versionNum){
            DKPidICM pid = (DKPidICM)_componentDDO.getPidObject(); // Get the Pid object.
            if(pid!=null){
                // MUST CLONE / COPY, We need the original to remain in its original state.
                pid = (DKPidICM) pid.clone();
                pid.setVersionNumber(versionNum);
            }
            return(pid);
        }
       /**
        * Get the original Item ID of this item.                                    <BR><BR>
        * @Return returns the original Item ID of this item.
        **/
        public String getOriginalComponentId(){
            return(_originalCompID);   
        }
       /**
        * Get the name of this information pack as it will appear in the toString() method.  <BR><BR>
        * @return Returns the info pack type name.
        **/
        public String getPackType(){
            return(_packType);   
        }
  
       /**
        * Table Header for pringing a list of these packs.                          <BR><BR>
        * @return Returns column information for a table listing of these packs.
        **/
        public String tableHeader(){
            String newline = System.getProperty("line.separator");  // Get the system's newline separator.
            String line1 = "Pack Type      Object Type Name   Original Comp ID                New Comp ID                 ";
            String line2 = "-------------  -----------------  ----------------------------    ----------------------------";
            //              CompInfoPack:  [Book]             '12345678901234567890123456' -> '12345678901234567890123456'"
            return(line1+newline+line2);
        }
       /**
        * String representation of pack.                                            <BR><BR>
        * @return Returns a printable version of this package.
        **/
        public String toString(){
            // Calculate white space padding to keep columns in order.
            String objectType = _componentDDO.getObjectType();
            String objTypePadding = "";
            for(int i=objectType.length(); i<32; i++)
                objTypePadding+=" ";
            String newCompIdMsgStr = null;
            String newCompID = ((DKPidICM)_componentDDO.getPidObject()).getComponentId();
            if(newCompID==null) newCompIdMsgStr = "null                      ";
            else                newCompIdMsgStr = newCompID;
            String exportedFileNameMsgStr = null;
            //              CompInfoPack:  [Book]             '12345678901234567890123456' -> '12345678901234567890123456'"
            return(_packType+":  ["+objectType+"]"+objTypePadding+"  '"+_originalCompID+"' -> '"+newCompIdMsgStr+"'");
        }

    }//end CompInfoPack class.

   /**
    * This class specifies the core set of options that may be used by both
    * import and export.  ImportOptions and ExportOptions extend this class.
    **/
    public static class Options{

        // Internal Variables
        private boolean _printTraceEnable;
        private boolean _printDebugEnable;
       
       /** <pre>
        * Create a base options object, initializing with defaults.                 
        *
        * <u>Defaults:</u>
        *       Trace Enable = TExportPackageICM.OPTION_PRINT_TRACE_DEFAULT
        *       Debug Enable = TExportPackageICM.OPTION_PRINT_DEBUG_DEFAULT
        * </pre>                                                                    <BR><BR>
        **/
        public Options(){
            // Set Defaults
            init();
        }
       /**
        * Create an Options object with the settings specified in the 
        * given configuration file.  For any settings not specified, defaults will
        * be used.  For information on configuration file format, please refer to the
        * TExportPackageICM.Options.read() documention.  For information on
        * default settings, please refer to the primary constructor.                <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public Options(String iniFileName) throws Exception{
            // Set Defaults
            init();
            // Read File Settings
            read(iniFileName);
        }
       /**
        * Initialize with defaults documented in ImportOptions() constructors.      <BR><BR>
        **/
        private void init(){
            // Set Defaults
            _printTraceEnable   = TExportPackageICM.OPTION_PRINT_TRACE_DEFAULT;
            _printDebugEnable   = TExportPackageICM.OPTION_PRINT_DEBUG_DEFAULT;
        }
       /** 
        * Determines if trace entry/exit command line printing is enabled.          <BR><BR>
        * @return Returns true if trace entry/exit printing is enabled, false otherwise.
        **/
        public boolean getPrintTraceEnable(){
            return(_printTraceEnable);                    
        }
       /**
        * Determines if debug command line printing is enabled.                     <BR><BR>
        * @return Returns true if debug printing is enabled, false otherwise.
        **/
        public boolean getPrintDebugEnable(){
            return(_printDebugEnable);                    
        }
       /**
        * Turn trace entry/exit command line printing on or off.                    <BR><BR>
        * @param setting - Turn on by setting to true, turn off by setting to false.
        **/
        public void setPrintTraceEnable(boolean setting){
            _printTraceEnable = setting;        
        }
       /**
        * Turn debug command line printing on or off.                               <BR><BR>
        * @param setting - Turn on by setting to true, turn off by setting to false.
        **/
        public void    setPrintDebugEnable(boolean setting){
            _printDebugEnable = setting;        
        }
       /**
        * Read in settings in configuration file.  File may be created by the write()
        * function, modified externally, or created from stracth.  File will be scanned
        * for line entries the conform to the following syntax.                     <BR><BR>
        *
        * <u>File Syntax:</u>                                                           <BR>
        *
        * Setting entries are scanned from the top down, one setting entry per line.
        * A setting entry is specified as "<Property>=<Value>".  Properties are
        * case insensitive versions of the exact settings & policies provided through
        * "set" methods.  For example, function setPrintDebugEnable() is specified through
        * property "PrintDebugEnable".  Values are the exact constant names or if boolean
        * values are requried, the words "TRUE" or "FALSE".  For example,
        * "OPTION_CONFLICTS_ALWAYS_NEW" is an example of a value.  Any invalid entries or
        * irrelevant data will be ignored.  Comments may be entered with a "#".  
        * Consider using the write() function to create a template if writing the propeties
        * file from scratch.  Review the javadoc for the interface for properties &
        * values associated with each.                                              <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public void read(String iniFileName) throws Exception{
            // Validate Input
            if(iniFileName==null)            throw new Exception("Configuration file name specified to TExportPackageICM.Options.read() is null.");
            if(iniFileName.compareTo("")==0) throw new Exception("Configuration file name specified to TExportPackageICM.Options.read() is an empty string.");

            // Open File
            FileReader fileReader = new FileReader(iniFileName);
            BufferedReader file   = new BufferedReader(fileReader);

            // Scan line by line, reading entries applicable to this object.
            String line = null;
            while((line = file.readLine())!=null){ // Continue until reach end of file.
                int separatorLoc = line.indexOf("=");
                if(separatorLoc > 0){
                    String property = line.substring(0,separatorLoc).trim();
                    String value    = line.substring(separatorLoc+1).trim();
                    // Handle depending on property name
                    if(property.compareToIgnoreCase("PrintTraceEnable")==0){
                        if(value.compareToIgnoreCase("TRUE")==0)
                            setPrintTraceEnable(true);
                        else if(value.compareToIgnoreCase("FALSE")==0)
                            setPrintTraceEnable(false);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }
                    else if(property.compareToIgnoreCase("PrintDebugEnable")==0){
                        if(value.compareToIgnoreCase("TRUE")==0)
                            setPrintDebugEnable(true);
                        else if(value.compareToIgnoreCase("FALSE")==0)
                            setPrintDebugEnable(false);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }
                }
            }
            
            // Close File
            file.close();
        }

       /**
        * Returns a string representation of this object.                           <BR><BR>
        * @return Returns a string representation of this object.
        **/
        public String toString(){
            return("Options{pt"+_printTraceEnable+",pd"+_printDebugEnable+"}");    
        }

       /**
        * Write current settings to configuration file.                             <BR><BR>
        * 
        * <u>File Syntax</u>                                                            <BR>
        *      Please refer to the read() method.
        *                                                                           <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public void write(String iniFileName) throws Exception{
            // Validate Input
            if(iniFileName==null)            throw new Exception("Configuration file name specified to TExportPackageICM.Options.read() is null.");
            if(iniFileName.compareTo("")==0) throw new Exception("Configuration file name specified to TExportPackageICM.Options.read() is an empty string.");

            // Create New File
            FileWriter file = new FileWriter(iniFileName);
            // Write file to string buffer, then write the string buffer.
            StringBuffer fileStr = new StringBuffer();
            // Get the system's newline separator.
            String newline = System.getProperty("line.separator");

            // Write Header
            fileStr.append("# -------------------------------");
            fileStr.append(newline);
            fileStr.append("# TExportPackageICM.Options");
            fileStr.append(newline);
            fileStr.append("# -------------------------------");
            fileStr.append(newline);

            // Write <Property>=<Value>
            // Print Trace Enable
            fileStr.append("PrintTraceEnable");
            fileStr.append("=");
            if(getPrintTraceEnable())   fileStr.append("TRUE");
            else                        fileStr.append("FALSE");
            fileStr.append(newline);
            // Print Debug Enable
            fileStr.append("PrintDebugEnable");
            fileStr.append("=");
            if(getPrintDebugEnable())   fileStr.append("TRUE");
            else                        fileStr.append("FALSE");
            fileStr.append(newline);
            
            // Write the file
            file.write(fileStr.toString());
            
            // Close File
            file.close();
        }

    }//end class Options

   /**
    * This object specifies the matrix of export options.  It may be handed off to
    * the export methods and may be modified by the tool as needed.  This object is
    * responsible for the full state of export options, including the constants, 
    * setting them, checking them, and possibly prompting the user.
    **/
    public static class ExportOptions extends Options{
        
        // Internal Variables
        private int     _selectedItemPolicy;
        private int     _folderContentPolicy;
        private int     _linkedItemsPolicy;
        private int     _refAttrValuePolicy;
        private boolean _isRetrieveDenied;

       /** <pre>
        * Create an ExportOptions object, initializing with defaults.  For traceEnable
        * and debugEnable defaults, please refer to the javadoc for Options().
        *
        * <U>Defaults:</U>
        *       Selected Item Policy  = TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS 
        *       Folder Content Policy = TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS
        *       Linked Items Policy   = TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS  
        *       RefAttr Value Policy  = TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS 
        *       Retrieve Denied       = false
        * </pre>
        **/
        public ExportOptions(){
            // Set Defaults
            init();
        }
       /**
        * Create an ExportOptions object with the settings specified in the 
        * given configuration file.  For any settings not specified, defaults will
        * be used.  For information on configuration file format, please refer to the
        * TExportPackageICM.ExportOptions.read() documention.  For information on 
        * default settings, please refer to the primary constructor.                <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public ExportOptions(String iniFileName) throws Exception{
            // Set Defaults
            init();
            // Read File Settings
            this.read(iniFileName);
        }
       /**
        * Initialize with defaults documented in ExportOptions() constructors.
        **/
        private void init(){
            // Set Defaults
            _selectedItemPolicy  = TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS;
            _folderContentPolicy = TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS;
            _linkedItemsPolicy   = TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS;
            _refAttrValuePolicy  = TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS;
            _isRetrieveDenied    = false;
        }
       /**
        * Get the Policy on exporting versions for folder contents.                 <BR><BR>
        * @return Returns the current folder content version export policy.
        *         Options are listed in the set method's javadoc.
        **/
        public int getFolderContentPolicy(){
            return(_folderContentPolicy);
        }
       /**
        * Get the Policy on exporting versions for items reference in a link.       <BR><BR>
        * @return Returns the current linked item version export policy.
        *         Options are listed in the set method's javadoc.
        **/
        public int getLinkedItemsPolicy(){
            return(_linkedItemsPolicy);
        }
       /**
        * Get the Policy on exporting versions for items reference by reference attributes.  <BR><BR>
        * @return Returns the current reference attribute item version export policy.
        *         Options are listed in the set method's javadoc.
        **/
        public int getRefAttrValuePolicy(){
            return(_refAttrValuePolicy);
        }
       /**
        * Get the Policy on exporting versions of the items directly selected.      <BR><BR>
        * @return Returns the current selected item version export policy.
        *         Options are listed in the set method's javadoc.
        **/
        public int getSelectedItemPolicy(){
            return(_selectedItemPolicy);
        }
       /**
        * Get an answer for the folder content export policy.
        * The user will be prompted if policy is set to prompt, otherwise it will
        * return the normal policy.                                                 <BR><BR>
        * @param folderDesc             - Description of the folder that contains the content.
        * @param contentItemVersionList - Listing of versions found to display.
        **/
        public int getAnswer_folderContentPolicy(String folderDesc, String contentItemVersionList) throws IOException, Exception{
            int promptRetVal = promptCheck_folderContentPolicy(folderDesc,contentItemVersionList);
            if(promptRetVal == -1) // If not set to prompt, return standard policy.
                promptRetVal = _folderContentPolicy;
            return(promptRetVal);
        }
       /**
        * Get an answer for the linked items export policy.
        * The user will be prompted if policy is set to prompt, otherwise it will
        * return the normal policy.                                                 <BR><BR>
        * @param mainItemDesc         - Description of the main item that holds this link.
        * @param otherItemVersionList - Unselected Versions of Other item linked to, source if inbound, target if outbound.
        * @param linkItemVersionList  - Unselected Versions of Optional Description Item (Link Item).
        **/
        public int getAnswer_linkedItemsPolicy(String mainItemDesc,String otherItemVersionList, String linkItemVersionList) throws IOException, Exception{
            int promptRetVal = promptCheck_linkedItemsPolicy(mainItemDesc,otherItemVersionList,linkItemVersionList);
            if(promptRetVal == -1) // If not set to prompt, return standard policy.
                promptRetVal = _linkedItemsPolicy;
            return(promptRetVal);
        }
       /**
        * Get an answer for the reference attribute value export policy.
        * The user will be prompted if policy is set to prompt, otherwise it will
        * return the normal policy.                                                 <BR><BR>
        * @param mainCompDesc         - Description of the component containing the reference attribute.
        * @param refAttrName          - Reference Attribute name
        * @param valueItemVersionList - Unselected Versions of the value item of the reference attribute.
        **/
        public int getAnswer_refAttrValuePolicy(String mainCompDesc, String refAttrName,String valueItemVersionList) throws IOException, Exception{
            int promptRetVal = promptCheck_refAttrValuePolicy(mainCompDesc,refAttrName,valueItemVersionList);
            if(promptRetVal == -1) // If not set to prompt, return standard policy.
                promptRetVal = _refAttrValuePolicy;
            return(promptRetVal);
        }
       /**
        * Determines whether the selected item export policy is set to the
        * specified policy.  The user will be prompted if policy is set to prompt.  <BR><BR>
        * @param policySetting - Policy Setting to check for.
        * @param versionList   - Listing of versions found to display.
        **/
        public boolean getAnswer_isSelectedItemPolicy(int policySetting,String versionList) throws IOException, Exception{
            boolean retval = false;
            int     promptRetVal = promptCheck_selectedItemPolicy(versionList);
            if(    (_selectedItemPolicy==policySetting)
                || (       promptRetVal==policySetting) )
                retval = true;
            return(retval);
        }
       /**
        * Determines whether the selected item export policy is set to the
        * export only the latest version.  The user will be prompted if policy is set
        * to prompt.                                                                <BR><BR>
        * @param versionList - Listing of versions found to display.
        * @return Returns true if only the selected versions are to be exported
        **/
        public boolean getAnswer_isSelectedItemPolicy_exportSelected(String versionList) throws IOException, Exception{
            return(getAnswer_isSelectedItemPolicy(TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION,versionList));
        }
       /**
        * Determines whether the selected item export policy is set to the
        * export all version of items directly selected.  The user will be
        * prompted if policy is set to prompt.                                      <BR><BR>
        * @param versionList - Listing of versions found to display.
        * @return Returns true if all versions are to be exported
        **/
        public boolean getAnswer_isSelectedItemPolicy_exportAll(String versionList) throws IOException, Exception{
            return(getAnswer_isSelectedItemPolicy(TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS,versionList));
        }
       /**
        * Determines whether the folder content export policy is set to always prompt
        * the user.                                                                 <BR><BR>
        * @return Returns true if the policy is set to prompt always, otherwise false.
        **/
        public boolean isFolderContentPolicy_promptAlways() throws IOException, Exception{
            boolean retval = false;
            if(_folderContentPolicy==TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_ALWAYS)
                retval=true;
            else
                retval=false;
            return(retval);
        }
       /**
        * Determines whether the linked itemsexport policy is set to always prompt the user.  <BR><BR>
        * @return Returns true if the policy is set to prompt always, otherwise false.
        **/
        public boolean isLinkedItemsPolicy_promptAlways() throws IOException, Exception{
            boolean retval = false;
            if(_linkedItemsPolicy==TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_ALWAYS)
                retval=true;
            else
                retval=false;
            return(retval);
        }
       /**
        * Determines whether the reference attribute value export policy is set
        * to always prompt the user.                                                <BR><BR>
        * @return Returns true if the policy is set to prompt always, otherwise false.
        **/
        public boolean isRefAttrValuePolicy_promptAlways() throws IOException, Exception{
            boolean retval = false;
            if(_refAttrValuePolicy==TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_ALWAYS)
                retval=true;
            else
                retval=false;
            return(retval);
        }
       /**
        * [Advanced] Determine if subsequent retrieval operations by the Export
        * Package is denied.  The caller assumes *absolute* responsibility for
        * retrieving all data into the DKDDO objects that they are interested in.
        * Data will be lost in the exported data if all of the data is not retrieved.  
        *
        * This is an advanced feature for using this tool in a high-production
        * environment.
        *
        * @return  Returns 'true' if the option is specified that denies the Export
        *          package the ability to call retrieve.  'false' is returned if the
        *          tool is allowed to assume responsibility.
        **/
        public boolean isRetrieveDenied(){
            // Write Warning if Debug Turned on
            if(getPrintDebugEnable())
                System.out.println("WARNING:  Export Package is denied retrieve capability.  Caller assumes absolute responsbility.");
            return(_isRetrieveDenied);   
        }//end isRetrieveDenied()

       /**
        * If policy set to prompt, prompt the user.  Depending on the user answer, the
        * option may be changed from prompt to a permanent policy.  Returns -1 if not
        * set to prompt setting.                                                    <BR><BR>
        * @param folderDesc             - Description of the folder that contains the content.
        * @param contentItemVersionList - Listing of versions found to display.
        * @return Returns the user's policy answer.  In recursive calls, -1 is returned
        *         until the user answers with a valid option.
        **/
        private int promptCheck_folderContentPolicy(String folderDesc,String contentItemVersionList) throws IOException, Exception{
            if(    (_folderContentPolicy==TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_IF_VERSIONS)
                || (_folderContentPolicy==TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_ALWAYS)){
                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("Prompt:  Folder Content Policy");
                System.out.println("--------------------------------------------------");
                System.out.println("A content was found inside a folder.  Please    ");
                System.out.println("select the policy of your choice for exporting  ");
                System.out.println("versions of the contents.  Exporting the latest ");
                System.out.println("only may create a version gap if the previous   ");
                System.out.println("versions do not exist on the target system.");
                System.out.println("");
                System.out.println("   Folder:  "+folderDesc);
                System.out.println("");
                System.out.println("   Versions Not Selected");
                System.out.println("        - Content: "+contentItemVersionList);
                System.out.println("");
                System.out.println("Export All?");
                System.out.println("");
                System.out.println("    1 - Yes            | Export All               ");
                System.out.println("    2 - No, Latest     | Export Latest            ");
                System.out.println("    3 - No, Remove     | Remove From Exported Item");
                System.out.println("    4 - Yes To All     | Always Export All        ");
                System.out.println("    5 - Latest for All | Always Export Latest     ");
                System.out.println("    6 - No, Remove All | Always Remove From Exported Item");
                String response = promptUser("> ");
                System.out.println("--------------------------------------------------");
                System.out.println("");
                if(response.compareTo("1")==0)
                    return(TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS);
                else if(response.compareTo("2")==0)
                    return(TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION);
                else if(response.compareTo("3")==0)
                    return(TExportPackageICM.OPTION_FOLDER_CONTENT_REMOVE_VALUE);
                else if(response.compareTo("4")==0){
                    _folderContentPolicy = TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS;
                    return(_folderContentPolicy);
                } else if(response.compareTo("5")==0){
                    _folderContentPolicy = TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION;
                    return(_folderContentPolicy);
                } else if(response.compareTo("6")==0){
                    _folderContentPolicy = TExportPackageICM.OPTION_FOLDER_CONTENT_REMOVE_VALUE;
                    return(_folderContentPolicy);
                } else { // Invalid Command
                    System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                    System.out.println("             Please Select From List Provided.");
                    return(promptCheck_folderContentPolicy(folderDesc,contentItemVersionList));
                }
            } else // Not set to prompt.
                return(-1);
        }
       /**
        * If policy set to prompt, prompt the user.  Depending on the user answer, the
        * option may be changed from prompt to a permanent policy.  Returns -1 if not
        * set to prompt setting.                                                    <BR><BR>
        * @param mainItemDesc         - Description of the main item that holds this link.
        * @param otherItemVersionList - Unselected Versions of Other item linked to, source if inbound, target if outbound.
        * @param linkItemVersionList  - Unselected Versions of Optional Description Item (Link Item).
        * @return Returns the user's policy answer.  In recursive calls, -1 is returned
        *         until the user answers with a valid option.
        **/
        private int promptCheck_linkedItemsPolicy(String mainItemDesc,String otherItemVersionList,String linkItemVersionList) throws IOException, Exception{
            if(    (_linkedItemsPolicy==TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_IF_VERSIONS)
                || (_linkedItemsPolicy==TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_ALWAYS)){
                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("Prompt:  Linked Items Policy");
                System.out.println("--------------------------------------------------");
                System.out.println("A link was found.  Please select");
                System.out.println("the policy of your choice for exporting versions");
                System.out.println("of the contents.  Exporting the latest only may ");
                System.out.println("create a version gap if the previous versions do");
                System.out.println("not exist on the target system.");
                System.out.println("");
                System.out.println("   Current Item:  "+mainItemDesc);
                System.out.println("");
                System.out.println("   Versions Not Selected");
                System.out.println("      - Linked To: "+otherItemVersionList);
                System.out.println("      - Desc Item: "+linkItemVersionList);
                System.out.println("");
                System.out.println("Export All?");
                System.out.println("");
                System.out.println("    1 - Yes            | Export All               ");
                System.out.println("    2 - No, Latest     | Export Latest            ");
                System.out.println("    3 - No, Remove     | Remove From Exported Item");
                System.out.println("    4 - Yes To All     | Always Export All        ");
                System.out.println("    5 - Latest for All | Always Export Latest     ");
                System.out.println("    6 - No, Remove All | Always Remove From Exported Item");
                String response = promptUser("> ");
                System.out.println("--------------------------------------------------");
                System.out.println("");
                if(response.compareTo("1")==0)
                    return(TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS);
                else if(response.compareTo("2")==0)
                    return(TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION);
                else if(response.compareTo("3")==0)
                    return(TExportPackageICM.OPTION_LINKED_ITEMS_REMOVE_LINK);
                else if(response.compareTo("4")==0){
                    _linkedItemsPolicy = TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS;
                    return(_linkedItemsPolicy);
                } else if(response.compareTo("5")==0){
                    _linkedItemsPolicy = TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION;
                    return(_linkedItemsPolicy);
                } else if(response.compareTo("6")==0){
                    _linkedItemsPolicy = TExportPackageICM.OPTION_LINKED_ITEMS_REMOVE_LINK;
                    return(_linkedItemsPolicy);
                } else { // Invalid Command
                    System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                    System.out.println("             Please Select From List Provided.");
                    return(promptCheck_linkedItemsPolicy(mainItemDesc,otherItemVersionList,linkItemVersionList));
                }
            } else // Not set to prompt.
                return(-1);
        }
       /**
        * If policy set to prompt, prompt the user.  Depending on the user answer, the
        * option may be changed from prompt to a permanent policy.  Returns -1 if not
        * set to prompt setting.                                                    <BR><BR>
        * @param mainCompDesc         - Description of the component containing the reference attribute.
        * @param refAttrName          - Reference Attribute name
        * @param valueItemVersionList - Unselected Versions of the value item of the reference attribute.
        * @return Returns the user's policy answer.  In recursive calls, -1 is returned
        *         until the user answers with a valid option.
        **/
        private int promptCheck_refAttrValuePolicy(String mainCompDesc,String refAttrName,String valueItemVersionList) throws IOException, Exception{
            if(    (_refAttrValuePolicy==TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_IF_VERSIONS)
                || (_refAttrValuePolicy==TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_ALWAYS)){
                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("Prompt:  Ref Attr Value Policy");
                System.out.println("--------------------------------------------------");
                System.out.println("A Reference Attribute was found that references");
                System.out.println("another item.  Please selecte the policy of your");
                System.out.println("choice policy of your choice for exporting versions");
                System.out.println("of the contents.  Exporting the latest only may ");
                System.out.println("create a version gap if the previous versions do");
                System.out.println("not exist on the target system.");
                System.out.println("");
                System.out.println("    Current Comp:  "+mainCompDesc);
                System.out.println("   Ref Attr Name:  "+refAttrName);
                System.out.println("");
                System.out.println("   Versions Not Selected");
                System.out.println("         - Value: "+valueItemVersionList);
                System.out.println("");
                System.out.println("Export All?");
                System.out.println("");
                System.out.println("    1 - Yes            | Export All               ");
                System.out.println("    2 - No, Referenced | Export Referenced Version");
                System.out.println("    3 - No, Remove     | Remove From Exported Item");
                System.out.println("    4 - Yes To All     | Always Export All        ");
                System.out.println("    5 - Ref'd for All  | Always Export Ref'd Version");
                System.out.println("    6 - No, Remove All | Always Remove From Exported Item");
                String response = promptUser("> ");
                System.out.println("--------------------------------------------------");
                System.out.println("");
                if(response.compareTo("1")==0)
                    return(TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS);
                else if(response.compareTo("2")==0)
                    return(TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_REFERENCED_VERSION);
                else if(response.compareTo("3")==0)
                    return(TExportPackageICM.OPTION_REFATTR_VALUE_REMOVE_VALUE);
                else if(response.compareTo("4")==0){
                    _refAttrValuePolicy = TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS;
                    return(_refAttrValuePolicy);
                } else if(response.compareTo("5")==0){
                    _refAttrValuePolicy = TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_REFERENCED_VERSION;
                    return(_refAttrValuePolicy);
                } else if(response.compareTo("6")==0){
                    _refAttrValuePolicy = TExportPackageICM.OPTION_REFATTR_VALUE_REMOVE_VALUE;
                    return(_refAttrValuePolicy);
                } else { // Invalid Command
                    System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                    System.out.println("             Please Select From List Provided.");
                    return(promptCheck_refAttrValuePolicy(mainCompDesc,refAttrName,valueItemVersionList));
                }
            } else // Not set to prompt.
                return(-1);
        }
       /**
        * If policy set to prompt, prompt the user.  Depending on the user answer, the
        * option may be changed from prompt to a permanent policy.  Returns -1 if not
        * set to prompt setting.                                                    <BR><BR>
        * @param versionList - Listing of versions found to display.
        * @return Returns the user's policy answer.  In recursive calls, -1 is returned
        *         until the user answers with a valid option.
        **/
        private int promptCheck_selectedItemPolicy(String versionList) throws IOException, Exception{
            if(_selectedItemPolicy==TExportPackageICM.OPTION_SELECTED_ITEM_PROMPT_IF_VERSIONS){
                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("Prompt:  Selected Item Policy");
                System.out.println("--------------------------------------------------");
                System.out.println("An item was selected for import.  Do you want");
                System.out.println("to export only the version selected or do you");
                System.out.println("want to export all versions.  Exporting only ");
                System.out.println("particular versions may create a version gap ");
                System.out.println("if the previous versions do not exist on the ");
                System.out.println("target system.");
                System.out.println("");
                System.out.println("    Versions Available:  "+versionList);
                System.out.println("");
                System.out.println("Export All?");
                System.out.println("");
                System.out.println("    1 - Yes            | Export All               ");
                System.out.println("    2 - No             | Export Selected Version");
                System.out.println("    4 - Yes To All     | Always Export All        ");
                System.out.println("    5 - No To All      | Always Export Selected Version");
                String response = promptUser("> ");
                System.out.println("--------------------------------------------------");
                System.out.println("");
                if(response.compareTo("1")==0)
                    return(TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS);
                else if(response.compareTo("2")==0)
                    return(TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION);
                else if(response.compareTo("3")==0){
                    _selectedItemPolicy = TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS;
                    return(_selectedItemPolicy);
                } else if(response.compareTo("4")==0){
                    _selectedItemPolicy = TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION;
                    return(_selectedItemPolicy);
                } else { // Invalid Command
                    System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                    System.out.println("             Please Select From List Provided.");
                    return(promptCheck_selectedItemPolicy(versionList));
                }
            } else // Not set to prompt.
                return(-1);
        }
       /**
        * Prompt the user for input.                                                <BR><BR>
        * @param messageStr - Message to display to the user.
        * @return Returns input that the user entered.
        **/
        private String promptUser(String messageStr) throws java.io.IOException{
                
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
        * Set Folder Content Export Policy.  If a folder is selected for export
        * or is indirectly reference by any cascading chain of references, this policy 
        * determines which versions of the contents to export.  Folders will always
        * reference the latest version of an item, since folder content relationships
        * are not versioned in an item.  If only the latest version is exported, a
        * version gap may be created since the versions in between may not exist on the
        * target datastore.  The version gap handling in the import options may offer
        * an alternative too.                                                       <BR><BR>
        * @param policySetting - Policy options are: <BR>
        *   <UL>
        *       <LI>
        *            <code> TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION: </code><BR>
        *                      When an items are selected for export and its references
        *                      are being traversed to pull folder contents with it, if
        *                      those items are versioned, this option will select only
        *                      export the latest version.  The user program must add
        *                      any other items it desires explicitly.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS:   </code><BR>
        *                      When folder contents are refernced directly or indirectly,
        *                      all versions of each content are to be selected for export.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_FOLDER_CONTENT_REMOVE_VALUE:          </code><BR>
        *                      Folder contents will be removed from the exported item.
        *                      They will not exist on the target system.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_IF_VERSIONS:    </code><BR>
        *                      The user will be prompted on the command line if multiple
        *                      versions are detected.
        *      </LI>     
        *       <LI> <code> TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_ALWAYS:         </code><BR>
        *                      The user will be always be prompted on the command line
        *                      if a folder content is found.
        *      </LI>     
        *  </UL>
        **/
        public void setFolderContentPolicy(int policySetting) throws Exception{
            // If out of valid range for option, throw error
            if(    (policySetting < TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION)
                || (policySetting > TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_ALWAYS) )
                throw new Exception("Invalid Option '"+policySetting+"' for 'Folder Content Policy'");
            _folderContentPolicy = policySetting;
        }

       /**
        * Set Linked Items Export Policy.  If an item is selected for export
        * or is indirectly reference by any cascading chain of references, and it
        * contains links to other items, this policy determines which versions to export.
        * Links do not reference a specific version, and therefore by default will point
        * to the latest version.  The previous versions may not exist in the target system
        * and might not be selected by the user explicitly.  The version gap handling in
        * the import options may offer an alternative too.                          <BR><BR>
        * @param policySetting - Policy options are: <BR>
        *   <UL>
        *       <LI> <code> TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION:  </code><BR>
        *                      When an items are selected for export and its references
        *                      are being traversed to pull linked items with it, if
        *                      those items are versioned, this option will select only
        *                      export the latest version.  The user program must add
        *                      any other items it desires explicitly.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS:    </code><BR>
        *                      When linked items are refernced directly or indirectly,
        *                      all versions of each item are to be selected for export.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_LINKED_ITEMS_REMOVE_LINK:           </code><BR>
        *                      Links will be removed from the exported item.
        *                      They will not exist on the target system.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_IF_VERSIONS:     </code><BR>
        *                      The user will be prompted on the command line if multiple
        *                      versions are detected.
        *      </LI>     
        *       <LI> <code> TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_ALWAYS:          </code><BR>
        *                      The user will be always be prompted on the command line
        *                      if a link is found.
        *      </LI>     
        *  </UL>
        **/
        public void setLinkedItemsPolicy(int policySetting) throws Exception{
            // If out of valid range for option, throw error
            if(    (policySetting < TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION)
                || (policySetting > TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_ALWAYS) )
                throw new Exception("Invalid Option '"+policySetting+"' for 'Linked Items Policy'");
            _linkedItemsPolicy = policySetting;
        }

       /**
        * Set RefAttr Value Export Policy.  If an item is selected for export
        * or is indirectly reference by any cascading chain of references, and a 
        * reference attribute is found in its tree of components that references another
        * item of a specific version, this policy determines which versions to export.
        * Reference Attributes reference a specific version of an item.  The previous
        * versions may not exist in the target system and might not be selected by the
        * user explicitly.  The version gap handling in the import options may offer an
        * alternative too.                                                          <BR><BR>
        * @param policySetting - Policy options are: <BR>
        *   <UL>
        *       <LI> <code> TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_REFERENCED_VERSION: </code><BR>
        *                      When an items are selected for export and its references
        *                      are being traversed to pull items referenced by reference
        *                      attributes, if those items are versioned, this option will
        *                      select only export only the version referenced.  The user
        *                      program must add any other items it desires explicitly.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS:     </code><BR>
        *                      When reference attr values are refernced directly or
        *                      indirectly, all versions of each content are to be
        *                      selected for export.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_REFATTR_VALUE_REMOVE_VALUE:            </code><BR>
        *                      Referenence Attributes will be set to null, and will
        *                      remain null in the target system.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_IF_VERSIONS:      </code><BR>
        *                      The user will be prompted on the command line if multiple
        *                      versions are detected.
        *      </LI>     
        *       <LI> <code> TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_ALWAYS:           </code><BR>
        *                      The user will be always be prompted on the command line
        *                      if a reference attribute value is found.
        *      </LI>     
        *  </UL>
        **/
        public void setRefAttrValuePolicy(int policySetting) throws Exception{
            // If out of valid range for option, throw error
            if(    (policySetting < TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_REFERENCED_VERSION)
                || (policySetting > TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_ALWAYS) )
                throw new Exception("Invalid Option '"+policySetting+"' for 'Ref Attr Item Policy'");
            _refAttrValuePolicy = policySetting;
        }

       /**
        * [Advanced] Allow (default) or revoke the tools entire ability and
        * any responsibility to make subsequent retrieval operations in order
        * to process and export selected items.  If set to 'true', the caller
        * assumes *absolute* responsibility for retrieving all data into the
        * DKDDO objects that they are interested in.  The tool will not call
        * retrieve and will therefore work off of any data it is given.  Data
        * will be lost in the exported data if all of the data is not retrieved.  
        *
        * This is an advanced feature for using this tool in a high-production
        * environment.
        *
        * @param isDenied - Set to 'true' to deny the Export Package the ability
        *                   to call retrieve.  Set to 'false' if the tool is
        *                   allowed to assume responsibility.
        **/
        public void setRetrieveDenied(boolean isDenied){
            _isRetrieveDenied = isDenied;
        }//end setRetrieveDenied()

       /**
        * Set Selected Item Export Policy.  If an item is selected for export
        * directly by the user or is a direct result of a query selected for export,
        * This policy determines which versions to export.
        * If not all versions are selected for export, those versions not selected may
        * not exist in the target system, causing a gap to exist which is not allowed.
        * on import.  The version gap handling in the import options may offer an
        * alternative too.                                                          <BR><BR>
        * @param policySetting - Policy options are: <BR>
        *   <UL>
        *       <LI> <code> TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION:  </code><BR>
        *                      Only the versions of the items directly selected will be
        *                      exported.  Indirectly referenced item versions are handled
        *                      through separate policies.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS:      </code><BR>
        *                      Export all versions of any item directly selected.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_SELECTED_ITEM_PROMPT_IF_VERSIONS:       </code><BR>
        *                      The user will be prompted on the command line if multiple
        *                      versions are detected.
        *      </LI>     
        *  </UL>
        **/
        public void setSelectedItemPolicy(int policySetting) throws Exception{
            // If out of valid range for option, throw error
            if(    (policySetting < TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION)
                || (policySetting > TExportPackageICM.OPTION_SELECTED_ITEM_PROMPT_IF_VERSIONS) )
                throw new Exception("Invalid Option '"+policySetting+"' for 'Selected Item Policy'");
            _selectedItemPolicy = policySetting;
        }

       /**
        * Read in settings in configuration file.  File may be created by the write()
        * function, modified externally, or created from stracth.  Please refer to
        * the the TExportPackageICM.Options.read() function for more information.   <BR><BR>
        *
        * <u>File Syntax</u>                                                            <BR>
        *      Please refer to the javadoc on the TExportPackageICM.Options.read()
        *      function for complete information. 
        *                                                                           <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public void read(String iniFileName) throws Exception{
            // First, read in parent properties.
            super.read(iniFileName);

            // Open File
            FileReader fileReader = new FileReader(iniFileName);
            BufferedReader file   = new BufferedReader(fileReader);

            // Scan line by line, reading entries applicable to this object.
            String line = null;
            while((line = file.readLine())!=null){ // Continue until reach end of file.
                int separatorLoc = line.indexOf("=");
                if(separatorLoc > 0){
                    String property = line.substring(0,separatorLoc).trim();
                    String value    = line.substring(separatorLoc+1).trim();
                    // Handle depending on property name
                    // Folder Content Policy
                    if(property.compareToIgnoreCase("FolderContentPolicy")==0){
                        if(value.compareToIgnoreCase("OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION")==0)
                            setFolderContentPolicy(TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION);
                        else if(value.compareToIgnoreCase("OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS")==0)
                            setFolderContentPolicy(TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS);
                        else if(value.compareToIgnoreCase("OPTION_FOLDER_CONTENT_REMOVE_VALUE")==0)
                            setFolderContentPolicy(TExportPackageICM.OPTION_FOLDER_CONTENT_REMOVE_VALUE);
                        else if(value.compareToIgnoreCase("OPTION_FOLDER_CONTENT_PROMPT_IF_VERSIONS")==0)
                            setFolderContentPolicy(TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_IF_VERSIONS);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }
                    // Linked Items Policy
                    if(property.compareToIgnoreCase("LinkedItemsPolicy")==0){
                        if(value.compareToIgnoreCase("OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION")==0)
                            setLinkedItemsPolicy(TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION);
                        else if(value.compareToIgnoreCase("OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS")==0)
                            setLinkedItemsPolicy(TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS);
                        else if(value.compareToIgnoreCase("OPTION_LINKED_ITEMS_REMOVE_LINK")==0)
                            setLinkedItemsPolicy(TExportPackageICM.OPTION_LINKED_ITEMS_REMOVE_LINK);
                        else if(value.compareToIgnoreCase("OPTION_LINKED_ITEMS_PROMPT_IF_VERSIONS")==0)
                            setLinkedItemsPolicy(TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_IF_VERSIONS);
                        else if(value.compareToIgnoreCase("OPTION_LINKED_ITEMS_PROMPT_ALWAYS")==0)
                            setLinkedItemsPolicy(TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_ALWAYS);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }
                    
                    // RefAttrValuePolicy
                    if(property.compareToIgnoreCase("RefAttrValuePolicy")==0){
                        if(value.compareToIgnoreCase("OPTION_REFATTR_VALUE_EXPORT_REFERENCED_VERSION")==0)
                            setRefAttrValuePolicy(TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_REFERENCED_VERSION);
                        else if(value.compareToIgnoreCase("OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS")==0)
                            setRefAttrValuePolicy(TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS);
                        else if(value.compareToIgnoreCase("OPTION_REFATTR_VALUE_REMOVE_VALUE")==0)
                            setRefAttrValuePolicy(TExportPackageICM.OPTION_REFATTR_VALUE_REMOVE_VALUE);
                        else if(value.compareToIgnoreCase("OPTION_REFATTR_VALUE_PROMPT_IF_VERSIONS")==0)
                            setRefAttrValuePolicy(TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_IF_VERSIONS);
                        else if(value.compareToIgnoreCase("OPTION_REFATTR_VALUE_PROMPT_ALWAYS")==0)
                            setRefAttrValuePolicy(TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_ALWAYS);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }

                    // SelectedItemPolicy
                    if(property.compareToIgnoreCase("SelectedItemPolicy")==0){
                        if(value.compareToIgnoreCase("OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION")==0)
                            setSelectedItemPolicy(TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION);
                        else if(value.compareToIgnoreCase("OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS")==0)
                            setSelectedItemPolicy(TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS);
                        else if(value.compareToIgnoreCase("OPTION_SELECTED_ITEM_PROMPT_IF_VERSIONS")==0)
                            setSelectedItemPolicy(TExportPackageICM.OPTION_SELECTED_ITEM_PROMPT_IF_VERSIONS);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }
                    // RetrieveDenied
                    if(property.compareToIgnoreCase("RetrieveDenied")==0){
                        if(value.compareToIgnoreCase("TRUE")==0)
                            setRetrieveDenied(true);
                        else if(value.compareToIgnoreCase("FALSE")==0)
                            setRetrieveDenied(false);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }
                }
            }
            
            // Close File
            file.close();
        }

       /**
        * Returns a string representation of this object.                           <BR><BR>
        * @return Returns a string representation of this object.
        **/
        public String toString(){
            return(super.toString()+"+ExportOptions{si"+_selectedItemPolicy+",fc"+_folderContentPolicy+",li"+_linkedItemsPolicy+",rv"+_refAttrValuePolicy+",rd"+_isRetrieveDenied+"}");    
        }
        
       /**
        * Write current settings to configuration file.                             <BR><BR>
        *
        * <u>File Syntax</u>                                                            <BR>
        *      Please refer to the read() method.
        *                                                                           <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public void write(String iniFileName) throws Exception{
            // Write Parent Info First
            super.write(iniFileName);

            // Create New File
            FileWriter file = new FileWriter(iniFileName,true);
            // Write file to string buffer, then write the string buffer.
            StringBuffer fileStr = new StringBuffer();
            // Get the system's newline separator.
            String newline = System.getProperty("line.separator");

            // Write Header
            fileStr.append("# -------------------------------");
            fileStr.append(newline);
            fileStr.append("# TExportPackageICM.ExportOptions");
            fileStr.append(newline);
            fileStr.append("# -------------------------------");
            fileStr.append(newline);

            // Write <Property>=<Value>

            // FolderContentPolicy
            fileStr.append("FolderContentPolicy");
            fileStr.append("=");
            switch(_folderContentPolicy){
                case TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION:
                    fileStr.append("OPTION_FOLDER_CONTENT_EXPORT_LATEST_VERSION");
                    break;
                case TExportPackageICM.OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS:
                    fileStr.append("OPTION_FOLDER_CONTENT_EXPORT_ALL_VERSIONS");
                    break;
                case TExportPackageICM.OPTION_FOLDER_CONTENT_REMOVE_VALUE:
                    fileStr.append("OPTION_FOLDER_CONTENT_REMOVE_VALUE");
                    break;
                case TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_IF_VERSIONS:
                    fileStr.append("OPTION_FOLDER_CONTENT_PROMPT_IF_VERSIONS");
                    break;
                case TExportPackageICM.OPTION_FOLDER_CONTENT_PROMPT_ALWAYS:
                    fileStr.append("OPTION_FOLDER_CONTENT_PROMPT_ALWAYS");
                    break;
                default:
                    throw new Exception("Internal Error writing config file.  Unexpected & unknown value.");
            }
            fileStr.append(newline);

            // LinkedItemsPolicy
            fileStr.append("LinkedItemsPolicy");
            fileStr.append("=");
            switch(_linkedItemsPolicy){
                case TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION:
                    fileStr.append("OPTION_LINKED_ITEMS_EXPORT_LATEST_VERSION");
                    break;
                case TExportPackageICM.OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS:
                    fileStr.append("OPTION_LINKED_ITEMS_EXPORT_ALL_VERSIONS");
                    break;
                case TExportPackageICM.OPTION_LINKED_ITEMS_REMOVE_LINK:
                    fileStr.append("OPTION_LINKED_ITEMS_REMOVE_LINK");
                    break;
                case TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_IF_VERSIONS:
                    fileStr.append("OPTION_LINKED_ITEMS_PROMPT_IF_VERSIONS");
                    break;
                case TExportPackageICM.OPTION_LINKED_ITEMS_PROMPT_ALWAYS:
                    fileStr.append("OPTION_LINKED_ITEMS_PROMPT_ALWAYS");
                    break;
                default:
                    throw new Exception("Internal Error writing config file.  Unexpected & unknown value.");
            }
            fileStr.append(newline);

            // RefAttrValuePolicy
            fileStr.append("RefAttrValuePolicy");
            fileStr.append("=");
            switch(_refAttrValuePolicy){
                case TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_REFERENCED_VERSION:
                    fileStr.append("OPTION_REFATTR_ITEM_EXPORT_REFERENCED_VERSION");
                    break;
                case TExportPackageICM.OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS:
                    fileStr.append("OPTION_REFATTR_VALUE_EXPORT_ALL_VERSIONS");
                    break;
                case TExportPackageICM.OPTION_REFATTR_VALUE_REMOVE_VALUE:
                    fileStr.append("OPTION_REFATTR_VALUE_REMOVE_VALUE");
                    break;
                case TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_IF_VERSIONS:
                    fileStr.append("OPTION_REFATTR_VALUE_PROMPT_IF_VERSIONS");
                    break;
                case TExportPackageICM.OPTION_REFATTR_VALUE_PROMPT_ALWAYS:
                    fileStr.append("OPTION_REFATTR_VALUE_PROMPT_ALWAYS");
                    break;
                default:
                    throw new Exception("Internal Error writing config file.  Unexpected & unknown value.");
            }
            fileStr.append(newline);

            // SelectedItemPolicy
            fileStr.append("SelectedItemPolicy");
            fileStr.append("=");
            switch(_selectedItemPolicy){
                case TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION:
                    fileStr.append("OPTION_SELECTED_ITEM_EXPORT_SELECTED_VERSION");
                    break;
                case TExportPackageICM.OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS:
                    fileStr.append("OPTION_SELECTED_ITEM_EXPORT_ALL_VERSIONS");
                    break;
                case TExportPackageICM.OPTION_SELECTED_ITEM_PROMPT_IF_VERSIONS:
                    fileStr.append("OPTION_SELECTED_ITEM_PROMPT_IF_VERSIONS");
                    break;
                default:
                    throw new Exception("Internal Error writing config file.  Unexpected & unknown value.");
            }
            fileStr.append(newline);

            // RetrieveDenied
            fileStr.append("RetrieveDenied");
            fileStr.append("=");
            if(_isRetrieveDenied==true)
                fileStr.append("TRUE");
            else
                fileStr.append("FALSE");
            fileStr.append(newline);

            // Write the file
            file.write(fileStr.toString());

            // Close File
            file.close();
        }
        
    }//end class ExportOptions

   /**
    * This object specified the matrix of import options.  It may be handed off to
    * the import methods and may be modified by the tool as needed.  This object is
    * responsible for the full state of import options, including the constants, 
    * setting them, checking them, and possibly prompting the user.
    **/
    public static class ImportOptions extends Options{
        
        // Internal Variables
        private int     _fileVersionWarning;
        private int     _versionGapHandling;
        private int     _conflictHandling;
        private int     _uniqueDetectionPolicy;
        private String  _userSpecifiedAttr;
        private boolean _overwriteConfirmEnable;
        private String  _trackingFileName;         // Operation Tracking File.  Line by line report of completion status needed for restart capability.

       /** <pre>
        * Create an ImportOptions object, initializing with defaults.  For traceEnable
        * and debugEnable defaults, please refer to the javadoc for Options().         
        *
        * <u>Defaults:</u>
        *       File Version Warning       = TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION
        *       Version Gap Handling       = TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS
        *       Conflict Handling          = TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_NEW
        *       Unique Detection Policy    = TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_PROMPT
        *       User-Specified Unique Attr = null
        *       Overwrite Confirmation     = false
        *       Tracking File Name         = TExportPackageICM.OPTION_TRACKING_FILE_DEFAULT
        * </pre>
        **/
        public ImportOptions(){
            // Set Defaults
            init();
        }
       /**
        * Create an ImportOptions object with the settings specified in the 
        * given configuration file.  For any settings not specified, defaults will
        * be used.  For information on configuration file format, please refer to the
        * TExportPackageICM.ImportOptions.read() documention.  For information on 
        * default settings, please refer to the primary constructor.                <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public ImportOptions(String iniFileName) throws Exception{
            // Set Defaults
            init();
            // Read File Settings
            this.read(iniFileName);
        }
       /**
        * Initialize with defaults documented in ImportOptions() constructors.
        **/
        private void init(){
            // Set Defaults
            _fileVersionWarning     = TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION;
            _versionGapHandling     = TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS;
            _conflictHandling       = TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_NEW;
            _uniqueDetectionPolicy  = TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_PROMPT;
            _userSpecifiedAttr      = null;
            _overwriteConfirmEnable = false;
            _trackingFileName       = TExportPackageICM.OPTION_TRACKING_FILE_DEFAULT;
        }
       /**
        * Get the Conflict Handling Setting.                                        <BR><BR>
        * @return Returns the current Conflict Handling Setting.  Options are listed
        *         in the setConflictHandling() javadoc.
        **/
        public int getConflictHandling(){
            return(_conflictHandling);
        }
       /**
        * Get the File Version Warning Policy.                                      <BR><BR>
        * @return Returns the current File Version Warning policy.  Options are listed
        *         in the setFileVersionWarning() javadoc.
        **/
        public int getFileVersionWarning(){
            return(_fileVersionWarning);
        }
       /**
        * Get the Import Operation Tracking File Name or 'null' if disabled.        <BR><BR>
        * @return Returns the file name setting for the Import Operation Tracking.  For
        *         for information, please refer to the setTrackingFileName() Javadoc.
        **/
        public String getTrackingFileName(){
            return(_trackingFileName);
        }
       /**
        * Get the Uniqueness Detection Policy.                                      <BR><BR>
        * @return Returns the current Uniqueness Detection Policy.  Options are listed
        *         in the setUniqueDetection() javadoc.
        **/
        public int getUniqueDetectionPolicy(){
            return(_uniqueDetectionPolicy);
        }
       /**
        * Get the User-Specified Unique Attr, if any.                               <BR><BR>
        * @return Returns the user-specified unique attr or null if none specified.
        **/
        public String getUserSpecifiedUniqueAttr(){
            return(_userSpecifiedAttr);
        }
       /**
        * Get the Version Gap Handling Policy.                                      <BR><BR>
        * @return Returns the current Version Gap Handling policy.  Options are listed
        *         in the setVersionGapHandling() javadoc.
        **/
        public int getVersionGapHandling(){
            return(_versionGapHandling);
        }
       /**
        * Get an answer for Overwrite Confirmation.  Prompt the user if set to prompt,
        * otherwise just return true.                                               <BR><BR>
        * @param existingDDO - Existing DDO that is going to be overwritten.
        * @return Returns the users choice for overwrite confirmation if set to
        *         prompt, otherwise returns the normal policy.
        **/
        public boolean getAnswer_overwriteConfirm(DKDDO existingDDO) throws IOException, Exception{
            return(promptCheck_overwriteConfirm(existingDDO));
        }

       /**
        * Get an answer for the Uniqueness Detection Policy.
        * The user will be prompted if policy is set to prompt, otherwise it will
        * return the normal policy.                                                 <BR><BR>
        * @param itemDesc - Item being imported.
        * @return Returns the users choice for unique detection policy if set to 
        *         prompt, otherwise returns the normal policy.
        **/
        public int getAnswer_uniqueDetectionPolicy(String itemDesc) throws IOException, Exception{
            int promptRetVal = promptCheck_uniqueDetectionPolicy(itemDesc);
            if(promptRetVal == -1) // If not set to prompt, return standard policy.
                promptRetVal = _uniqueDetectionPolicy;
            return(promptRetVal);
        }
       /**
        * Determines whether or not file version warning policy is set to the specified policy.
        * The user will be prompted if policy is set to prompt.                     <BR><BR>
        * @param policySetting - Policy Setting to check for.
        * @param fileVersion   - Actual File Version.
        **/
        public boolean getAnswer_isFileVersionWarning(int policySetting,String fileVersion) throws IOException, Exception{
            boolean retval = false;
            int     promptRetVal = promptCheck_fileVersionWarning(fileVersion);
            if(    (_fileVersionWarning==policySetting)
                || (       promptRetVal==policySetting) )
                retval = true;
            return(retval);
        }
       /**
        * Determines whether or not file version warning policy is set to throw exception.
        * The user will be prompted if policy is set to prompt.                     <BR><BR>
        * @param fileVersion - Actual File Version.
        * @return Returns true if user wants exception thrown, false if not.
        **/
        public boolean getAnswer_isFileVersionWarning_exception(String fileVersion) throws IOException, Exception{
            return(getAnswer_isFileVersionWarning(TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION,fileVersion));
        }
       /**
        * Gets the Version Gap Handling Policy.
        * The user will be prompted if policy is set to prompt.                     <BR><BR>
        * @param itemId      - Item ID of the item in question.
        * @param objectType  - Object Type of the item in question.
        * @param lastVersion - Last Version.
        * @param nextVersion - Next Version Found.
        * @return Returns the users choice for Version Gap handling if set to 
        *         prompt, otherwise returns the normal policy.
        **/
        public int getAnswer_versionGapHandling(String itemid,String objectType,String lastVersion,String nextVersion) throws IOException, Exception{
            int promptRetVal = promptCheck_versionGapHandling(itemid,objectType,lastVersion,nextVersion);
            if( promptRetVal == -1) // If not set to prompt, return standard policy.
                promptRetVal = _versionGapHandling;
            return(promptRetVal);
        }
       /**
        * Determine if Overwrite Confirmation is enabled or not.                    <BR><BR>
        * @return Returns true if overwrite confirmation is enabled, false if not.
        **/
        public boolean getOverwriteConfirmEnable(){
            return(_overwriteConfirmEnable);
        }
       /**
        * If no items are found in the target system based on the uniqueness detection   
        * policy specified, this will prompt the user with the options available.   <BR><BR>
        * @param  itemDesc - Description of the item being imported.
        * @return Returns the user's answer.
        **/
        public int prompt_conflictHandling_noneFound(String itemDesc) throws IOException, Exception{
            System.out.println("");
            System.out.println("--------------------------------------------------");
            System.out.println("Prompt:  Conflict Handling");
            System.out.println("--------------------------------------------------");
            System.out.println("No items were found in the target system matching");
            System.out.println("the import item based on the uniqueness criteria.");
            System.out.println("");
            System.out.println("    Import Item:  "+itemDesc);
            System.out.println("");
            System.out.println("How do you want to proceed?");
            System.out.println("");
            System.out.println("    1 - Create New   | Unique Then New ");
            System.out.println("    2 - Skip         | Unique Then Skip");
            System.out.println("    3 - Throw Error  | Unique Then Error");
            System.out.println("    4 - Always New   | Always Unique Then New");
            System.out.println("    5 - Always Skip  | Always Unique Then Skip");
            System.out.println("    6 - Always Error | Always Unique Then Error");
            String response = promptUser("> ");
            System.out.println("--------------------------------------------------");
            System.out.println("");
            if(response.compareTo("1")==0)
                return(TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_NEW);
            else if(response.compareTo("2")==0)
                return(TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_SKIP);
            else if(response.compareTo("3")==0)
                return(TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_ERROR);
            else if(response.compareTo("4")==0){
                _conflictHandling = TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_NEW;
                return(_conflictHandling);
            } else if(response.compareTo("5")==0){
                _conflictHandling = TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_SKIP;
                return(_conflictHandling);
            } else if(response.compareTo("6")==0){
                _conflictHandling = TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_ERROR;
                return(_conflictHandling);
            } else { // Invalid Command
                System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                System.out.println("             Please Select From List Provided.");
                return(prompt_conflictHandling_noneFound(itemDesc));
            }
        }
       /**
        * If no items are found in the target system based on the uniqueness detection   
        * policy specified, this will prompt the user with the options available.   <BR><BR>
        * @param  itemDesc - Description of the item being imported.
        * @param  autoDetectedList - (Optional) List of auto-detected attributes that appear as good unique
        *                            constraints.
        * @return Returns the user's answer.
        **/
        public String prompt_userSpecifiedUniqueAttr(String itemDesc,String autoDetectedList) throws IOException, Exception{
            if(autoDetectedList==null)
                autoDetectedList = "<none>";
            if(autoDetectedList.trim().compareTo("")==0)
                autoDetectedList = "<none>";
            System.out.println("");
            System.out.println("--------------------------------------------------");
            System.out.println("Prompt:  Uniqueness Constraint");
            System.out.println("--------------------------------------------------");
            System.out.println("Please enter the name of an attribute that is     ");
            System.out.println("guaranteed to be unique in the target system.     ");
            System.out.println("Unique, Required attributes are often the best    ");
            System.out.println("");
            System.out.println("    Import Item:  "+itemDesc);
            System.out.println("  Auto-Detected:  "+autoDetectedList);
            System.out.println("");
            System.out.println("Please choose one from the list or enter your own:");
            System.out.println("");
            String response = promptUser("> ");
            System.out.println("--------------------------------------------------");
            System.out.println("");
            if(response.trim().compareTo("")!=0){
                _userSpecifiedAttr = response.trim();
                return(_userSpecifiedAttr);
            } else { // Invalid Command
                System.out.println("USER ERROR:  No attribute specified.  Please Specify.");
                return(prompt_userSpecifiedUniqueAttr(itemDesc,autoDetectedList));
            }
        }
       /**
        * If policy set to prompt, prompt the user.  Depending on the user answer, the
        * option may be changed from prompt to a permanent policy.  Returns -1 if not
        * set to prompt setting.                                                    <BR><BR>
        * @param fileVersion - Actual File Version.
        * @return Returns the user's policy answer.
        **/
        private int promptCheck_fileVersionWarning(String fileVersion) throws IOException, Exception{
            if(_fileVersionWarning==TExportPackageICM.OPTION_FILE_VERSION_WARNING_PROMPT){
                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("Prompt:  File Version Warning");
                System.out.println("--------------------------------------------------");
                System.out.println("A difference between versions was detected between");
                System.out.println("the file to be loaded and the the the current");
                System.out.println("version of the tool.");
                System.out.println("");
                System.out.println("    File Version:  "+fileVersion);
                System.out.println("    Tool Version:  "+TExportPackageICM.T_ICM_EXPORT_PACKAGE_VERSION);
                System.out.println("");
                System.out.println("Continue?");
                System.out.println("");
                System.out.println("    1 - Yes          | Ignore          ");
                System.out.println("    2 - No           | Exception       ");
                System.out.println("    3 - Yes To All   | Always Ignore   ");
                System.out.println("    4 - No To All    | Always Exception");
                String response = promptUser("> ");
                System.out.println("--------------------------------------------------");
                System.out.println("");
                if(response.compareTo("1")==0)
                    return(TExportPackageICM.OPTION_FILE_VERSION_WARNING_IGNORE);
                else if(response.compareTo("2")==0)
                    return(TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION);
                else if(response.compareTo("3")==0){
                    _fileVersionWarning = TExportPackageICM.OPTION_FILE_VERSION_WARNING_IGNORE;
                    return(_fileVersionWarning);
                } else if(response.compareTo("4")==0){
                    _fileVersionWarning = TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION;
                    return(_fileVersionWarning);
                } else { // Invalid Command
                    System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                    System.out.println("             Please Select From List Provided.");
                    return(promptCheck_fileVersionWarning(fileVersion));
                }
            } else // Not set to prompt.
                return(-1);
        }
       /**
        * If overwrite confirmation is enabled, prompt the user.  Returns true if
        * not set to prompt before overwrite.                                       <BR><BR>
        * @param existingDDO - Existing DDO that is going to be overwritten.
        * @return Returns the user's policy answer.
        **/
        private boolean promptCheck_overwriteConfirm(DKDDO existingDDO) throws IOException, Exception{
            if(_overwriteConfirmEnable){
                // Build Description of item
                String itemDesc = getItemDesc(existingDDO);
                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("Prompt:  Overwrite Confirmation");
                System.out.println("--------------------------------------------------");
                System.out.println("You are about to overwrite the following existing");
                System.out.println("item in the target datastore.  ");
                System.out.println("");
                System.out.println("    Item:  "+itemDesc);
                System.out.println("");
                System.out.println("Are you sure?");
                System.out.println("");
                System.out.println("    Y - Yes         | Overwrite");
                System.out.println("    N - No          | Not This Time");
                System.out.println("    A - Yes To All  | Always Overwrite");
                String response = promptUser("> ");
                System.out.println("--------------------------------------------------");
                System.out.println("");
                if(response.compareToIgnoreCase("Y")==0)
                    return(true);
                else if(response.compareToIgnoreCase("N")==0)
                    return(false);
                if(response.compareToIgnoreCase("1")==0)
                    return(true);
                else if(response.compareToIgnoreCase("2")==0)
                    return(false);
                else if(response.compareTo("A")==0){
                    _overwriteConfirmEnable = false;
                    return(_overwriteConfirmEnable);
                } else if(response.compareTo("3")==0){
                    _overwriteConfirmEnable = false;
                    return(_overwriteConfirmEnable);
                } else { // Invalid Command
                    System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                    System.out.println("             Please Select From List Provided.");
                    return(promptCheck_overwriteConfirm(existingDDO));
                }
            } else // Not set to prompt.
                return(true);
        }
       /**
        * If policy set to prompt, prompt the user.  Depending on the user answer, the
        * option may be changed from prompt to a permanent policy.  Returns -1 if not
        * set to prompt setting.                                                    <BR><BR>
        * @param itemDesc - Item being imported.
        * @return Returns the user's policy answer.
        **/
        private int promptCheck_uniqueDetectionPolicy(String itemDesc) throws IOException, Exception{
            if(_uniqueDetectionPolicy==TExportPackageICM.OPTION_UNIQUENESS_PROMPT){
                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("Prompt:  Uniqueness Detection Policy");
                System.out.println("--------------------------------------------------");
                System.out.println("The current setting request that the user be prompted");
                System.out.println("to enter the uniqueness constraint.  This setting");
                System.out.println("determines how the imported item(s) will be compared");
                System.out.println("against existing items in the target system.  This");
                System.out.println("is the policy to simply identify them.");
                System.out.println("");
                System.out.println("    Import Item:  "+itemDesc);
                if(_userSpecifiedAttr==null)
                    System.out.println("    Last User-Specified Attr:  <none>");
                else 
                    System.out.println("    Last User-Specified Attr:  "+_userSpecifiedAttr);
                System.out.println("");
                System.out.println("Please choose:");
                System.out.println("");
                System.out.println("    1 - User Specified                        ");
                System.out.println("    2 - Auto-Detect, Throw Error if None Found");
                System.out.println("    3 - Auto-Detect, Prompt if None Found     ");
                System.out.println("    4 - Always User Specified                 ");
                System.out.println("    5 - Always Detect, Then Error             ");
                System.out.println("    6 - Always Detect, Then Prompt            ");
                String response = promptUser("> ");
                System.out.println("--------------------------------------------------");
                System.out.println("");
                if(response.compareTo("1")==0){
                    prompt_userSpecifiedUniqueAttr(itemDesc,null);
                    return(TExportPackageICM.OPTION_UNIQUENESS_USER_SPECIFIED);
                }else if(response.compareTo("2")==0)
                    return(TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_ERROR);
                else if(response.compareTo("3")==0)
                    return(TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_PROMPT);
                else if(response.compareTo("4")==0){
                    _uniqueDetectionPolicy = TExportPackageICM.OPTION_UNIQUENESS_USER_SPECIFIED;
                    prompt_userSpecifiedUniqueAttr(itemDesc,null);
                    return(_uniqueDetectionPolicy);
                } else if(response.compareTo("5")==0){
                    _uniqueDetectionPolicy = TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_ERROR;
                    return(_uniqueDetectionPolicy);
                } else if(response.compareTo("6")==0){
                    _uniqueDetectionPolicy = TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_PROMPT;
                    return(_uniqueDetectionPolicy);
                } else { // Invalid Command
                    System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                    System.out.println("             Please Select From List Provided.");
                    return(promptCheck_uniqueDetectionPolicy(itemDesc));
                }
            } else // Not set to prompt.
                return(-1);
        }
       /**
        * If policy set to prompt, prompt the user.  Depending on the user answer, the
        * option may be changed from prompt to a permanent policy.  Returns -1 if not
        * set to prompt setting.                                                    <BR><BR>
        * @param itemId      - Item ID of the item in question.
        * @param objectType  - Object Type of the item in question.
        * @param lastVersion - Last Version.
        * @param nextVersion - Next Version Found.
        * @return Returns the user's policy answer.
        **/
        private int promptCheck_versionGapHandling(String itemid, String objectType, String lastVersion, String nextVersion) throws IOException, Exception{
            if(_versionGapHandling==TExportPackageICM.OPTION_VERSION_GAP_HANDLING_PROMPT){
                System.out.println("");
                System.out.println("--------------------------------------------------");
                System.out.println("Prompt:  Version Gap Handling");
                System.out.println("--------------------------------------------------");
                System.out.println("A gap was found in the versions of items being");
                System.out.println("Imported.  Gaps in versions cannot be maintained");
                System.out.println("when creating new items and new versions on the");
                System.out.println("target datastore.");
                System.out.println("");
                System.out.println("   Original Item:  '"+itemid+"' ("+objectType+")");
                System.out.println("    Last Version:  '"+lastVersion+"'");
                System.out.println("    Next Version:  '"+nextVersion+"'");
                System.out.println("");
                System.out.println("Eliminate Gap, Shifting All Version Imported Down?");
                System.out.println("");
                System.out.println("    1 - Yes         | Eliminate Gaps  ");
                System.out.println("    2 - No          | Enforce No Gaps ");
                System.out.println("    3 - Yes To All  | Always Eliminate Gaps");
                System.out.println("    4 - No To All   | Always Enforce No Gaps");
                String response = promptUser("> ");
                System.out.println("--------------------------------------------------");
                System.out.println("");
                if(response.compareTo("1")==0)
                    return(TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS);
                else if(response.compareTo("2")==0)
                    return(TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS);
                else if(response.compareTo("3")==0){
                    _versionGapHandling = TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS;
                    return(_versionGapHandling);
                } else if(response.compareTo("4")==0){
                    _versionGapHandling = TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS;
                    return(_versionGapHandling);
                } else { // Invalid Command
                    System.out.println("USER ERROR:  Invalid Command '"+response+"'.");
                    System.out.println("             Please Select From List Provided.");
                    return(promptCheck_versionGapHandling(itemid,objectType,lastVersion,nextVersion));
                }
            } else // Not set to prompt.
                return(-1);
        }
       /**
        * Prompt the user for input.                                                <BR><BR>
        * @param messageStr - Message to display to the user.
        * @return Returns input that the user entered.
        **/
        private String promptUser(String messageStr) throws java.io.IOException{
                
            java.io.InputStreamReader inputStreamReader = new java.io.InputStreamReader(System.in);      // create a new inputsream reader from the system's input stream
            java.io.BufferedReader    commandlineReader = new java.io.BufferedReader(inputStreamReader); // create a buffered reader to read from the command line
                
            // Prompt User
            System.out.println("");
            System.out.print(messageStr);
                
            // Read the input from the user.
            String responseStr = commandlineReader.readLine();
                
            return(responseStr);
            }

       /**
        * Set Conflict Handling Setting.  This setting determines if and how existing
        * items in the target datastore may be compared against those being imported.
        * Existing items in the target system may be matched up to the import items based
        * on attribute criteria and may be overwritten if desired.                  <BR><BR>
        * @param policySetting - Policy options are: <BR>
        *   <UL>
        *       <LI> <code> TExportPackageICM.OPTION_CONFLICTS_ALWAYS_NEW:                </code><BR>
        *                      Never check against existing items in the target system.
        *                      Always import as a new item.  If the item violates a    
        *                      unique attribute constraint, an exception will be thrown
        *                      from the DB2 Content Manager API.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_NEW:           </code><BR>
        *                      Based on the Uniqueness Determination Policy, 
        *                      check against existing items in the target system.
        *                      If the item is identified, it can be overwritten
        *                      or updated with the information in the imported items.
        *                      If an existing item is not found matching the uniqueness
        *                      criteria specified in that policy, then import it as
        *                      a new item.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_ERROR:         </code><BR>
        *                      Based on the Uniqueness Determination Policy, 
        *                      check against existing items in the target system.
        *                      If the item is identified, it can be overwritten
        *                      or updated with the information in the imported items.
        *                      If an existing item is not found, an exception will
        *                      be thrown.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_SKIP:          </code><BR>
        *                      Based on the Uniqueness Determination Policy, 
        *                      check against existing items in the target system.
        *                      If the item is identified, it can be overwritten
        *                      or updated with the information in the imported items.
        *                      If an existing item is not found, simply skip that item
        *                      and move on.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_PROMPT:        </code><BR>
        *                      Based on the Uniqueness Determination Policy, 
        *                      check against existing items in the target system.
        *                      If the item is identified, it can be overwritten
        *                      or updated with the information in the imported items.
        *                      If an existing item is not found, The user will be
        *                      prompted on the command line.
        *      </LI>     
        *  </UL>
        **/
        public void setConflictHandling(int policySetting) throws Exception{
            // If out of valid range for option, throw error
            if(    (policySetting < TExportPackageICM.OPTION_CONFLICTS_ALWAYS_NEW)
                || (policySetting > TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_PROMPT) )
                throw new Exception("Invalid Option '"+policySetting+"' for 'Conflict Handling'");
            _conflictHandling = policySetting;
        }
        
       /**
        * Set File Version Warning Policy.  If an Export Package file is read that does
        * not match the current version of this object, the user may be warned with an
        * exception.  With the warning, the user may they reissue function call with
        * warning turned off if desired.                                            <BR><BR>
        * @param policySetting - Policy options are: <BR>
        *   <UL>
        *       <LI> <code> TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION:      </code><BR>
        *                      If the version of the file does not exactly match the 
        *                      version of the tool, an exception will be thrown to warn
        *                      the user.  The user may then reissue the request with
        *                      this setting turned off if desired.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_FILE_VERSION_WARNING_IGNORE:         </code><BR>
        *                      Ignore any file version differences between that and
        *                      the version of the tool used.  File format changes
        *                      may be incompatable with this version of the tool.
        *                      Unexpected errors may occur.
        *       <LI> <code> TExportPackageICM.OPTION_FILE_VERSION_WARNING_PROMPT:         </code><BR>
        *                      The user will be prompted on the command line.
        *      </LI>     
        *  </UL>
        **/
        public void setFileVersionWarning(int policySetting) throws Exception{
            // If out of valid range for option, throw error
            if(    (policySetting < TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION)
                || (policySetting > TExportPackageICM.OPTION_FILE_VERSION_WARNING_PROMPT) )
                throw new Exception("Invalid Option '"+policySetting+"' for 'File Version Warning'");
            _fileVersionWarning = policySetting;
        }
       /**
        * Set or Disable Import Operation Tracking File Name.  By default import operation
        * tracking is enabled and writes line-by-line status of import success to the 
        * default file name stored in TExportPackageICM.OPTION_TRACKING_FILE_DEFAULT.  
        * Import operation tracking may be disabled by setting to 'null'.  An import
        * operation log is required in order to restart import if it failed or was 
        * terminated before import completed for all items selected.  Each import 
        * operation will overwrite file that already exists with the given name.
        * The file extension specified in TExportPackageICM.OPTION_TRACKING_FILE_EXT
        * should be used.  If no extension is specified, this extension will be appended.
        *                                                                           <BR><BR>
        * <u>File Syntax:</u>                                                           <BR>
        *      Please refer to the documentation in TExportPackageICM.restartImport().
        *                                                                           <BR><BR>
        * @param trackingFileName - Desired import operation tracking file name or 'null'
        *                           to disable.
        **/
        public void setTrackingFileName(String trackingFileName) throws Exception{
            if(trackingFileName.compareTo("")==0)
                throw new Exception("The import operation tracking file name specified was an empty string, ''.  To disable, use 'null'.  Otherwise, only valid names will be used.");
            // Check extension.  Set if not already set.
            // Create options object that will not do anything since it is required by the
            // function used below.
            TExportPackageICM.Options options = new TExportPackageICM.Options();
            options.setPrintDebugEnable(false);
            options.setPrintTraceEnable(false);
            String fileNameExt = getFileNameExt(trackingFileName.trim(),options);
            options = null; // Release reference.
            // If no extension specified, add the default.
            if(fileNameExt==null)
                trackingFileName += TExportPackageICM.OPTION_TRACKING_FILE_EXT;
            _trackingFileName = trackingFileName;
        }
       /**
        * Enable or Disable Overwrite Confirmation for existing items upon import.
        * Depending on the Conflict Handling Setting, if an existing item in the
        * target datstore is matched up with an item being imported, this will
        * determine if the user will be prompted to confirm overwrite.              <BR><BR>
        * @param setting - True to enable Overwrite Confirmation, false to disable.
        **/
        public void setOverwriteConfirmEnable(boolean setting) throws Exception{
            _overwriteConfirmEnable = setting;
        }

       /**
        * Set the Uniqueness Detection Policy.  This setting determines how the imported   
        * item(s) will be compared against existing items in the target system.  This 
        * is the policy to simply identify them.  This setting is used by the Conflict
        * Handling process depending on the setting.  The Conflict Handling Policy
        * is then responsible for determining how to proceed once a unique item is or
        * is not identified.                                                        <BR><BR>
        * @param policySetting - Policy options are: <BR>
        *   <UL>
        *       <LI> <code> TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_ERROR:       </code><BR>
        *                      Automatically detect the uniqueness determination
        *                      criteria.  Uniqueness will be detected by 
        *                      a single attribute on the root level of the ItemType
        *                      that is both unique and required (not nullable).  If
        *                      no unique attributes are required, then a single 
        *                      unique attribute attribute will be used.  If multiple
        *                      or no attributes are found during detection, an
        *                      exception will be thrown.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_PROMPT:      </code><BR>
        *                      Automatically detect the uniqueness determination
        *                      criteria.  Uniqueness will be detected by 
        *                      a single attribute on the root level of the ItemType
        *                      that is both unique and required (not nullable).  If
        *                      no unique attributes are required, then a single 
        *                      unique attribute attribute will be used.  If multiple
        *                      or no attributes are found during detection, the
        *                      user will be prompted on the command line to make a
        *                      choice.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_UNIQUENESS_USER_SPECIFIED:         </code><BR>
        *                      Uniqueness will be determined based on the attribute
        *                      name specified by the setUserSpecifiedUniqueAttr().
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_UNIQUENESS_PROMPT:                 </code><BR>
        *                      Always prompt the user on the command line for the 
        *                      Uniqueness Determination Policy.
        *      </LI>
        *  </UL>
        **/
        public void setUniqueDetectionPolicy(int policySetting) throws Exception{
            // If out of valid range for option, throw error
            if(    (policySetting < TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_ERROR)
                || (policySetting > TExportPackageICM.OPTION_UNIQUENESS_PROMPT) )
                throw new Exception("Invalid Option '"+policySetting+"' for 'Uniqueness Detection Policy'");
            _uniqueDetectionPolicy = policySetting;
        }

       /**
        * Set the User-Specified Unique Attribute to use in the Uniqueness Detection
        * Policy's 'USER_SPECIFIED' setting.  This attribute determines the exact
        * attribute for which the the imported item(s) will be compared against
        * existing items in the target system.  The Uniqueness Detection Policy
        * will automatically be set to TExportPackageICM.OPTION_UNIQUENESS_USER_SPECIFIED.  <BR><BR>
        * @param attrName - Name of attribute on root level that uniquely identifies
        *                   items in the target system.  It must exist on all Item Types
        *                   imported.
        **/
        public void setUserSpecifiedUniqueAttr(String attrName){
            if(attrName!=null)
                _uniqueDetectionPolicy = TExportPackageICM.OPTION_UNIQUENESS_USER_SPECIFIED;
            _userSpecifiedAttr = attrName;
        }
        
       /**
        * Set Version Gap Handling Policy.                                          <BR><BR>
        * @param policySetting - Policy options are: <BR>
        *   <UL>
        *       <LI> <code> TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS:   </code><BR>
        *                      If a gap in versions is detected, an exception
        *                      will be thrown.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS:    </code><BR>
        *                      If gaps are detected, they are eliminated by
        *                      shifting all versions down.
        *      </LI>
        *       <LI> <code> TExportPackageICM.OPTION_VERSION_GAP_HANDLING_PROMPT:            </code><BR>
        *                      The user will prompted at the command line
        *                      for decision if a gap in versions is detected.
        *      </LI>
        *  </UL>
        **/
        public void setVersionGapHandling(int policySetting) throws Exception{
            // If out of valid range for option, throw error
            if(    (policySetting < TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS)
                || (policySetting > TExportPackageICM.OPTION_VERSION_GAP_HANDLING_PROMPT) )
                throw new Exception("Invalid Option '"+policySetting+"' for 'Version Gap Handling'");
            _versionGapHandling = policySetting;
        }

       /**
        * Read in settings in configuration file.  File may be created by the write()
        * function, modified externally, or created from stracth.  Please refer to the
        * javadoc on the TExportPackageICM.Options.read() for more information.     <BR><BR>
        *
        * <u>File Syntax:</u>                                                           <BR>
        *      Please refer the TExportPackageICM.Options.read() for complete information.
        *                                                                           <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public void read(String iniFileName) throws Exception{
            // First, read in parent properties.
            super.read(iniFileName);

            // Open File
            FileReader fileReader = new FileReader(iniFileName);
            BufferedReader file   = new BufferedReader(fileReader);

            // Scan line by line, reading entries applicable to this object.
            String line = null;
            while((line = file.readLine())!=null){ // Continue until reach end of file.
                int separatorLoc = line.indexOf("=");
                if(separatorLoc > 0){
                    String property = line.substring(0,separatorLoc).trim();
                    String value    = line.substring(separatorLoc+1).trim();
                    // Handle depending on property name
                    // ConflictHandling
                    if(property.compareToIgnoreCase("ConflictHandling")==0){
                        if(value.compareToIgnoreCase("OPTION_CONFLICTS_ALWAYS_NEW")==0)
                            setConflictHandling(TExportPackageICM.OPTION_CONFLICTS_ALWAYS_NEW);
                        else if(value.compareToIgnoreCase("OPTION_CONFLICTS_UNIQUE_THEN_NEW")==0)
                            setConflictHandling(TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_NEW);
                        else if(value.compareToIgnoreCase("OPTION_CONFLICTS_UNIQUE_THEN_ERROR")==0)
                            setConflictHandling(TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_ERROR);
                        else if(value.compareToIgnoreCase("OPTION_CONFLICTS_UNIQUE_THEN_SKIP")==0)
                            setConflictHandling(TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_SKIP);
                        else if(value.compareToIgnoreCase("OPTION_CONFLICTS_UNIQUE_THEN_PROMPT")==0)
                            setConflictHandling(TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_PROMPT);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }

                    // FileVersionWarning
                    if(property.compareToIgnoreCase("FileVersionWarning")==0){
                        if(value.compareToIgnoreCase("OPTION_FILE_VERSION_WARNING_EXCEPTION")==0)
                            setFileVersionWarning(TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION);
                        else if(value.compareToIgnoreCase("OPTION_FILE_VERSION_WARNING_IGNORE")==0)
                            setFileVersionWarning(TExportPackageICM.OPTION_FILE_VERSION_WARNING_IGNORE);
                        else if(value.compareToIgnoreCase("OPTION_FILE_VERSION_WARNING_PROMPT")==0)
                            setFileVersionWarning(TExportPackageICM.OPTION_FILE_VERSION_WARNING_PROMPT);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }

                    // OverwriteConfirmEnable
                    if(property.compareToIgnoreCase("OverwriteConfirmEnable")==0){
                        if(value.compareToIgnoreCase("TRUE")==0)
                            setOverwriteConfirmEnable(true);
                        else if(value.compareToIgnoreCase("FALSE")==0)
                            setOverwriteConfirmEnable(false);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }
                    // TrackingFileName
                    if(property.compareToIgnoreCase("TrackingFileName")==0){
                        if(value.compareTo("")==0)     // If none specified, disable.
                            _trackingFileName = null;
                        else
                            _trackingFileName = value;
                    }
                    // UniueDetectionPolicy
                    if(property.compareToIgnoreCase("UniueDetectionPolicy")==0){
                        if(value.compareToIgnoreCase("OPTION_UNIQUENESS_DETECT_THEN_ERROR")==0)
                            setUniqueDetectionPolicy(TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_ERROR);
                        else if(value.compareToIgnoreCase("OPTION_UNIQUENESS_DETECT_THEN_PROMPT")==0)
                            setUniqueDetectionPolicy(TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_PROMPT);
                        else if(value.compareToIgnoreCase("OPTION_UNIQUENESS_USER_SPECIFIED")==0)
                            setUniqueDetectionPolicy(TExportPackageICM.OPTION_UNIQUENESS_USER_SPECIFIED);
                        else if(value.compareToIgnoreCase("OPTION_UNIQUENESS_PROMPT")==0)
                            setUniqueDetectionPolicy(TExportPackageICM.OPTION_UNIQUENESS_PROMPT);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }

                    // UserSpecifiedUniqueAttr
                    if(property.compareToIgnoreCase("UserSpecifiedUniqueAttr")==0){
                        _userSpecifiedAttr = value;
                    }

                    // VersionGapHandling
                    if(property.compareToIgnoreCase("VersionGapHandling")==0){
                        if(value.compareToIgnoreCase("OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS")==0)
                            setVersionGapHandling(TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS);
                        else if(value.compareToIgnoreCase("OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS")==0)
                            setVersionGapHandling(TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS);
                        else if(value.compareToIgnoreCase("OPTION_VERSION_GAP_HANDLING_PROMPT")==0)
                            setVersionGapHandling(TExportPackageICM.OPTION_VERSION_GAP_HANDLING_PROMPT);
                        else
                            throw new Exception("Invalid value '"+value+"' in configuration file for setting '"+property+"'.");
                    }
                }
            }
            
            // Close File
            file.close();
        }

       /**
        * Returns a string representation of this object.                           <BR><BR>
        * @return Returns a string representation of this object.
        **/
        public String toString(){
            String userSpecifiedAttrStr = null;
            if(_userSpecifiedAttr==null) userSpecifiedAttrStr = "Null";
            else                         userSpecifiedAttrStr = _userSpecifiedAttr;
            String overwriteConfirmEnableStr = null;
            if(_overwriteConfirmEnable)  overwriteConfirmEnableStr = "Y";
            else                         overwriteConfirmEnableStr = "N";
            return(super.toString()+"+ImportOptions{fvw"+_fileVersionWarning+",vgh"+_versionGapHandling+",ch"+_conflictHandling+",ud"+_uniqueDetectionPolicy+",usa'"+userSpecifiedAttrStr+"',oc"+overwriteConfirmEnableStr+",tfn'"+_trackingFileName+"'}");
        }

       /**
        * Write current settings to configuration file.                             <BR><BR>
        *
        * <u>File Syntax:</u>                                                           <BR>
        *      Please refer to the read() method.
        *                                                                           <BR><BR>
        * @param iniFileName - Name of configuration file.
        **/
        public void write(String iniFileName) throws Exception{
            // Write Parent Info First
            super.write(iniFileName);

            // Create New File
            FileWriter file = new FileWriter(iniFileName,true);
            // Write file to string buffer, then write the string buffer.
            StringBuffer fileStr = new StringBuffer();
            // Get the system's newline separator.
            String newline = System.getProperty("line.separator");

            // Write Header
            fileStr.append("# -------------------------------");
            fileStr.append(newline);
            fileStr.append("# TExportPackageICM.ImportOptions");
            fileStr.append(newline);
            fileStr.append("# -------------------------------");
            fileStr.append(newline);

            // Write <Property>=<Value>

            // ConflictHandling
            fileStr.append("ConflictHandling");
            fileStr.append("=");
            switch(_conflictHandling){
                case TExportPackageICM.OPTION_CONFLICTS_ALWAYS_NEW:
                    fileStr.append("OPTION_CONFLICTS_ALWAYS_NEW");
                    break;
                case TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_NEW:
                    fileStr.append("OPTION_CONFLICTS_UNIQUE_THEN_NEW");
                    break;
                case TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_ERROR:
                    fileStr.append("OPTION_CONFLICTS_UNIQUE_THEN_ERROR");
                    break;
                case TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_SKIP:
                    fileStr.append("OPTION_CONFLICTS_UNIQUE_THEN_SKIP");
                    break;
                case TExportPackageICM.OPTION_CONFLICTS_UNIQUE_THEN_PROMPT:
                    fileStr.append("OPTION_CONFLICTS_UNIQUE_THEN_PROMPT");
                    break;
                default:
                    throw new Exception("Internal Error writing config file.  Unexpected & unknown value.");
            }
            fileStr.append(newline);

            // FileVersionWarning
            fileStr.append("FileVersionWarning");
            fileStr.append("=");
            switch(_fileVersionWarning){
                case TExportPackageICM.OPTION_FILE_VERSION_WARNING_EXCEPTION:
                    fileStr.append("OPTION_FILE_VERSION_WARNING_EXCEPTION");
                    break;
                case TExportPackageICM.OPTION_FILE_VERSION_WARNING_IGNORE:
                    fileStr.append("OPTION_FILE_VERSION_WARNING_IGNORE");
                    break;
                case TExportPackageICM.OPTION_FILE_VERSION_WARNING_PROMPT:
                    fileStr.append("OPTION_FILE_VERSION_WARNING_PROMPT");
                    break;
                default:
                    throw new Exception("Internal Error writing config file.  Unexpected & unknown value.");
            }
            fileStr.append(newline);

            // OverwriteConfirmEnable
            fileStr.append("OverwriteConfirmEnable");
            fileStr.append("=");
            if(_overwriteConfirmEnable==true)
                fileStr.append("TRUE");
            else
                fileStr.append("FALSE");
            fileStr.append(newline);

            // TrackingFileName
            fileStr.append("TrackingFileName");
            fileStr.append("=");
            if(_trackingFileName!=null)
                fileStr.append(_trackingFileName);
            fileStr.append(newline);

            // UniueDetectionPolicy
            fileStr.append("UniueDetectionPolicy");
            fileStr.append("=");
            switch(_uniqueDetectionPolicy){
                case TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_ERROR:
                    fileStr.append("OPTION_UNIQUENESS_DETECT_THEN_ERROR");
                    break;
                case TExportPackageICM.OPTION_UNIQUENESS_DETECT_THEN_PROMPT:
                    fileStr.append("OPTION_UNIQUENESS_DETECT_THEN_PROMPT");
                    break;
                case TExportPackageICM.OPTION_UNIQUENESS_USER_SPECIFIED:
                    fileStr.append("OPTION_UNIQUENESS_USER_SPECIFIED");
                    break;
                case TExportPackageICM.OPTION_UNIQUENESS_PROMPT:
                    fileStr.append("OPTION_UNIQUENESS_PROMPT");
                    break;
                default:
                    throw new Exception("Internal Error writing config file.  Unexpected & unknown value.");
            }
            fileStr.append(newline);

            // UserSpecifiedUniqueAttr
            fileStr.append("UserSpecifiedUniqueAttr");
            fileStr.append("=");
            if(_userSpecifiedAttr==null)
                fileStr.append("");
            else
                fileStr.append(_userSpecifiedAttr);
            fileStr.append(newline);

            // VersionGapHandling
            fileStr.append("VersionGapHandling");
            fileStr.append("=");
            switch(_versionGapHandling){
                case TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS:
                    fileStr.append("OPTION_VERSION_GAP_HANDLING_ENFORCE_NO_GAPS");
                    break;
                case TExportPackageICM.OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS:
                    fileStr.append("OPTION_VERSION_GAP_HANDLING_ELIMINATE_GAPS");
                    break;
                case TExportPackageICM.OPTION_VERSION_GAP_HANDLING_PROMPT:
                    fileStr.append("OPTION_VERSION_GAP_HANDLING_PROMPT");
                    break;
                default:
                    throw new Exception("Internal Error writing config file.  Unexpected & unknown value.");
            }
            fileStr.append(newline);

            // Write the file
            file.write(fileStr.toString());

            // Close File
            file.close();
        }
        
    }//end class ImportOptions

   /**
    * This class contains the record of a successful import of one version of an item.
    * It contains a description of the completed import, original Item Id, and new 
    * Item Id.                                                                       <BR>
    *                                                                                <BR>
    * <B>Obtaining an Instance</B>                                                   <BR>
    * An array of these records are returned by TExportPackageICM.importItems().
    * If it is written to a String using the toString() method, it can be recreated
    * if that string is given to the constructor accepting that String.              <BR>
    *                                                                                <BR>
    * <B>Data Only Valid Until Next TExportPackageICM Import Operation</B>           <BR>
    * The record reference the current status of the TExportPackageICM object.  
    * If the same TExportPackageICM object instance is used to import into another
    * system, these records will be updated to reflect the new data.                 <BR>
    **/
    public static class ImportRecord{

        // Internal Variables
        private        ItemInfoPack      _itemInfoPack        = null; // The ItemInfoPack used by TExportPackageICM
                                                                      // to complete the import process.
        private static TExportPackageICM _staticExportPackage = null; // Used to recreate Item Info Packs.
       
       /** 
        * This class cannot be instantiated by an end-user.  It can only be created
        * by TExportPackageICM to be returned by TExportPackageICM.importItems().
        **/
        private ImportRecord() throws Exception{
            // No default constructor allowed
            throw new Exception("The TExportPackageICM.ImportRecord class cannot be created using the default constructor.");
        }//end CTOR()
        
       /** 
        * A new instance of this class cannot be instantiated by an end-user execpt
        * when recreating an existing ImportRecord.  New instances are only created
        * by TExportPackageICM to be returned by TExportPackageICM.importItems().
        * @param itemInfoPack - The ItemInfoPack that has completed import.
        **/
        ImportRecord(ItemInfoPack itemInfoPack) throws Exception{
            // Validate Input
            if(itemInfoPack==null)                     throw new InternalError("Internal Error: Error in TExportPackageICM.ImportRecord constructor.  The Import Record could not be created due to an illegal argument.  Parameter 'itemInfoPack' is 'null'.  This should only be created by TExportPackageICM.importItems() to create instances to return to the user.  TExportPackageICM.importItems() should have used a valid instance for the parameter.");
            if(itemInfoPack.getNewItemId()==null) throw new InternalError("Internal Error: Error in TExportPackageICM.ImportRecord constructor.  The Import Record could not be created due to an illegal argument.  The ItemInfoPack for which this record will represent is missing the new Item Id in the target system.  It was 'null'.  This should only be created by TExportPackageICM.importItems() to create instances to return to the user.  TExportPackageICM.importItems() should have used a valid instance for the parameter.");
            
            // Save values
            _itemInfoPack = itemInfoPack;
        }//end CTOR()

       /** 
        * Recreate an ImportRecord given a string confirming to the format
        * output by ImportRecord.toString().  This is the only way a user
        * can create an instance of this object, as recreation of one that
        * was already created by TExportPackageICM.
        * @param fromString - Recreate based on the string from toString() method.
        **/
        public ImportRecord(String fromString) throws Exception{
            // If needed not yet created, make a TExportPackageICM object
            if(_staticExportPackage==null)
                _staticExportPackage = new TExportPackageICM(0,null);
            // Recreate the ItemInfoPack
            _itemInfoPack = _staticExportPackage.recreateItemInfoPack("<path n/a>",fromString);
        }//end CTOR()

       /**
        * Get the root component DKDDO object to the imported item in the target
        * system.
        * @return Returns the root component DKDDO object to the imported item in
        *         the target system.
        **/
        public DKDDO getImportedItem(){
            return(_itemInfoPack.getImportedItem());
        }//end getImportedItem()

       /**
        * Get the item type name of the imported item.
        * @return Returns the item type name for the item as it was
        *         in both the original and target system.
        **/
        public String getItemType(){
            return(_itemInfoPack.getItemType());   
        }

       /**
        * Get a unique key for identifying this object in a sorted list.
        * @return Returns a unique key for identifying this object in a sorted list.
        **/
        public String getKey(){
            return(_itemInfoPack.getKey());
        }//end getKey()

       /**
        * If an instance of the ImportRecord object is not availalbe, this static
        * method can be used to construct the unique key for identifying this object
        * in a sorted list.
        * @param originalIemId - Original Item ID of the item that was imported.
        * @param verId  - Original Version Number of the item that was imported.
        * @param verId         - Original Version Number of the item that was imported.
        **/
        public static String getKey(String originalItemId, String verId){
            // Validate Input
            if(originalItemId==null)              throw new IllegalArgumentException("Invalid paramter to ItemInfoPack.getKey(String originalItemId, String verId).  Parameter 'originalItemId' is 'null'.");
            if(originalItemId.trim().length()==0) throw new IllegalArgumentException("Invalid paramter to ItemInfoPack.getKey(String originalItemId, String verId).  Parameter 'originalItemId' is an empty string '"+originalItemId+"'.");
            if(verId==null)                       throw new IllegalArgumentException("Invalid paramter to ItemInfoPack.getKey(String originalItemId, String verId).  Parameter 'verId' is 'null'.");
            if(verId.trim().length()==0)          throw new IllegalArgumentException("Invalid paramter to ItemInfoPack.getKey(String originalItemId, String verId).  Parameter 'verId' is an empty string '"+verId+"'.");

            return(originalItemId.toUpperCase()+verId);
        }//end getKey()

       /**
        * Get a brief description of the completed import.                      <BR>
        *                                                                       <BR>
        * <B>Format</B>                                                         <BR>
        * <code>                                                                <BR>
        *     Pack Type      Object Type Name   Original Item ID              VersionID    New Item ID                   Type  Exported File Name                          ";
        *     -------------  -----------------  ----------------------------  ---------    ----------------------------  ----  --------------------------------------------";
        *     ItemInfoPack:  [Book]             '12345678901234567890123456'  v'123456' -> '12345678901234567890123456'  'd'   "C:\temp\myfile.xml"
        * </code>                                                               <BR>
        * @return Returns a description of the completed import including the original
        *         Item Id and the new Item Id in the format described above.
        **/
        public String toString(){
            return(_itemInfoPack.toString());
        }//end toString()
    }//end class ImportRecord
    
}//end class TExportPackageICM
              
