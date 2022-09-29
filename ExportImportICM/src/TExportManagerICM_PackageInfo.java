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
 
 Package Info
     For internal use by TExportManagerICM and TImportManagerICM only.
     
     This object is responsible for tracking information about a completed
     package as multiple batches are processed by the Import / Export
     Management Tools.
     
     For more information on the Import / Export Managmenet Tools, refer to
     the header doucmentation in the TExportManagerICM.java sample file.
 
 ******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;

/************************************************************************************************
 *          FILENAME: TExportManagerICM_PackageInfo.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Object responsible for tracking information about a completed package
 *                    used by TExportManagerICM and TImportManagerICM.
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: none.  Intenral use by TExportManagerICM & TImportManagerICM only.
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: none.
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: none.
 ************************************************************************************************/
public class TExportManagerICM_PackageInfo{
    
    // Constants
    private static String ITEMID_NULL_TAG = "<no previous package>";

    // Variables
    String  _afterItemId                = null;     // Item ID after which all of this packages item IDs are found.  This package does not include this item ID.  This is typically the last item ID of the previous package.  If this is 'null', then ethere is no previous package that this pick up after.  Set Notation:  (afterItemId,lastItemId]
    File    _centralPackageFile         = null;     // Central package file that coordinates the entire single package.  This is the persistent copy of TExportPackageICM.
    boolean _enforceFolderExists        = true;     // By default, the package folder must exist, allowing reading and writing data in the folder by the tool using this object.  This can be turned off for Completion Marker and Reconciler.
    File    _folder                     = null;     // Folder that the data is located at on the file system.
    Integer _key                        = null;     // Key for accessing this package in a sorted package info list.
    int     _packageNum                 = -1;       // Unique package number for this package.
    int     _numItems                   = -1;       // Actual number of items in this package.
    String  _lastItemId                 = null;     // Last Item ID of this package.  Set Notation:  (afterItemId,lastItemId]
    TExportPackageICM.Options _options  = null;     // Used for debug & trace print enable.


   /**
    * Preventing creation with no arguments.
    **/
    private TExportManagerICM_PackageInfo() throws Exception{
        throw new Exception("The TExportManagerICM_PackageInfo object cannot be created by the no-argument constructor.  Use the constructor that takes the main arguments.");
    }//end CTOR()

   /**
    * Create an instance of the Package Info.
    * @param packageNum    - Unique package number for this package.
    * @param numItems      - Actual number of items in this package.
    * @param afterItemId   - (afterItemId,lastItemId]: Item ID after which all of this packages item IDs are found.  This package does not include this item ID.  This is typically the last item ID of the previous package.  If this is 'null', then ethere is no previous package that this pick up after.  Set Notation:  (afterItemId,lastItemId]
    * @param lastItemId    - (afterItemId,lastItemId]: Last Item ID of this package.  Set Notation:  (afterItemId,lastItemId]
    * @param folder        - Folder that the data is located at on the file system.
    * @param options       - Options that enable this package to print debug and trace information.
    **/
    public TExportManagerICM_PackageInfo(int packageNum, int numItems, String afterItemId, String lastItemId, File folder, TExportPackageICM.Options options){
        // Constructor-Specific Settings
        boolean enforceFolderExists = true;   // When this constructor is used, existance
                                              // of the folder is required in order to read
                                              // and write data to the folder.  This 
                                              // constructor is not meant for tools like
                                              // Completion Marker & Reconciler that do not
                                              // need the data available anymore.
        initCTOR(packageNum,numItems,afterItemId,lastItemId,folder,enforceFolderExists,options);
    }//end CTOR(String name, String[] commandlineArgs)    

   /**
    * Create an instance of the Package Info from the string representation output
    * by the toString() method.
    * @param formattedString    - Formatted string output by the toString() method.
    * @param storageLocations[] - Storage Locations that the package data will be located in.
    *                             The original location can move.  This method will
    *                             find the correct volume that it belongs to.
    * @param options            - Options that enable this package to print debug and trace information.
    **/
    public TExportManagerICM_PackageInfo(String formattedString, File[] storageLocations, TExportPackageICM.Options options) throws Exception{

        // Recreate From String
        fromString(formattedString);
        
        // Reconcile any change in storage locations
        reconcileStorageLocation(storageLocations);

        // Constructor-Specific Settings
        boolean enforceFolderExists = true;   // When this constructor is used, existance
                                              // of the folder is required in order to read
                                              // and write data to the folder.  This 
                                              // constructor is not meant for tools like
                                              // Completion Marker & Reconciler that do not
                                              // need the data available anymore.
        
        // Pass in parsed values already stored.
        initCTOR(_packageNum,_numItems,_afterItemId,_lastItemId,_folder,enforceFolderExists,options);
    }//end CTOR(String formattedString, File[])    

   /**
    * Create an instance of the Package Info from the string representation output
    * by the toString() method.  This contructor can only be used for operations
    * that do not involve importing or exporting package data.
    * @param formattedString    - Formatted string output by the toString() method.
    * @param disablePackageData - Only allowed option with this contructor is to not
    *                             reconcile against actual storage locations.  
    * @param options            - Options that enable this package to print debug and trace information.
    **/
    public TExportManagerICM_PackageInfo(String formattedString, boolean disablePackageData, TExportPackageICM.Options options) throws Exception{

        // Validate input unique to this constructor
        if(disablePackageData==false) throw new InternalError("Internal Error:  Incorrect constructor was used to create a PackageInfo.  The parameters the constructor that does not accept storage locations received input not to disable all actual package data access.  This constructor cannot be used if package data access is not disallowed.");

        // Recreate From String
        fromString(formattedString);

        // Constructor-Specific Settings
        boolean enforceFolderExists = false;  // When this constructor is used, existance
                                              // of the folder NOT is required.  This 
                                              // constructor is meant for tools like
                                              // Completion Marker & Reconciler that do not
                                              // need the data available anymore.
                                              // This is the opposite of disablePackageData
                                              // parameter to this constructor.
        
        // Pass in parsed values already stored.
        initCTOR(_packageNum,_numItems,_afterItemId,_lastItemId,_folder,enforceFolderExists, options);
    }//end CTOR(String formattedString, boolean)    
    
   /**
    * Initialize all variables.  This should be called by every valid constructor.
    * @param packageNum          - Unique package number for this package.
    * @param numItems            - Actual number of items in this package.
    * @param afterItemId         - (afterItemId,lastItemId]: Item ID after which all of this packages item IDs are found.  This package does not include this item ID.  This is typically the last item ID of the previous package.  If this is 'null', then ethere is no previous package that this pick up after.  Set Notation:  (afterItemId,lastItemId]
    * @param lastItemId          - (afterItemId,lastItemId]: Last Item ID of this package.  Set Notation:  (afterItemId,lastItemId]
    * @param folder              - Folder that the data is located at on the file system.
    * @param enforceFolderExists - Whether or not existance of the folder is expected
    *                              locally on the machine by the tool using this object.
    *                              The folder must exist by this point for both Export Manager
    *                              and Import Manager.  Tools like Completion Marker &
    *                              Reconciler that do not need the data available anymore.
    * @param options             - Options that enable this package to print debug and trace information.
    **/
    public void initCTOR(int packageNum, int numItems, String afterItemId, String lastItemId, File folder, boolean enforceFolderExists, TExportPackageICM.Options options){
        // Start off with defaults
        initByDefaults();
        // Save Parameter Variables
        _packageNum          = packageNum;
        _numItems            = numItems;
        _afterItemId         = afterItemId;
        _lastItemId          = lastItemId;
        _folder              = folder;
        _key                 = new Integer(packageNum);
        _enforceFolderExists = enforceFolderExists;
        _options             = options;
        
        // Validate Data
        validateSettings();
        
        // After input was validated, create dependent file objects
        _centralPackageFile         = new File(_folder,TExportManagerICM.COMMON_EXPORT_PACKAGE_NAME );
        
    }//end init(ParentManager, String name, String[] commandlineArgs)    

   /**
    * Start off with the defaults.  These can be later overridden.
    **/
    private void initByDefaults(){
        // Set defaults for optional commandline arguments.
        // _afterItemId     : No defaults.  Required Value
        // _packageNum      : No defaults.  Required Value
        // _numItems        : No defaults.  Required Value
        // _lastItemId      : No defaults.  Required Value
        // _folder          : No defaults.  Required Value
    }//end initMissingOptionsByDefaults()

   /**
    * Create any object needed.
    **/
    private void initObjects(){
        // Create Objects
        // No objects to be created
    }//end initObjects()

   /**
    * Load this Package Information from the string representation output
    * by the toString() method.
    * @param formattedString    - Formatted string output by the toString() method.
    **/
    private void fromString(String dataStr) throws Exception{

        // For error reporting, keep track of last index location.
        int lastSuccessfulIndex = 0;

        // "<package Num (8-char, right-align)>"
        // ": '"
        // - characters up to first ':'
        int beginIndex = 0;
        int endIndex   = dataStr.indexOf(':',beginIndex);     // Continue along string.
        if((beginIndex<=-1) || (endIndex<=0))
            throw new Exception("Package Info data string is invalid.  Error parsing package number.  It is missing identifiers and/or information.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        String valueStr = dataStr.substring(beginIndex,endIndex).trim();
        // - convert to num
        try{
            _packageNum = Integer.valueOf(valueStr).intValue();
            // - validate num
            if(_packageNum < 1) throw new Exception("Package number '"+_packageNum+"' is less than '1'. Only posative package numbers allowed."); // Will be re-thrown from catch block below with even more information.
        }
        catch(Exception exc){
            throw new Exception("Package Info data string is invalid.  It contains an invalid package number '"+valueStr+"'.  Expected positive integar > 0.  Received error \""+exc.getMessage()+"\" during conversion.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        }
        lastSuccessfulIndex = endIndex; // Save index for error reporting if the following fails.
        // ": '"
        // "<# Items (8-char, right-align)>"
        // "' ('"
        beginIndex = dataStr.indexOf('\'',endIndex+1) + 1; // Continue from last position in string
        endIndex   = dataStr.indexOf('\'',beginIndex);
        if((beginIndex<=0) || (endIndex<=0))
            throw new Exception("Package Info data string is invalid.  Error parsing # of items.  It is missing identifiers and/or information.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        valueStr = dataStr.substring(beginIndex,endIndex).trim();
        // - convert to num
        try{
            _numItems = Integer.valueOf(valueStr).intValue();
            // - validate num
            if(_numItems < 1) throw new Exception("Number of items, '"+_numItems+"', in package '"+_packageNum+"' is less than '1'. Only posative numbers in pacages are allowed.  No empty packages should have been created."); // Will be re-thrown from catch block below with even more information.
        }
        catch(Exception exc){
            throw new Exception("Package Info data string is invalid.  It contains an invalid number of items, '"+valueStr+"' in package '"+_packageNum+"'.  Expected positive integar > 0.  Received error \""+exc.getMessage()+"\" during conversion.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        }
        lastSuccessfulIndex = endIndex; // Save index for error reporting if the following fails.
        // "' ('"
        // "<After Item ID (26-char, right-align)>"
        // "', '"
        beginIndex = dataStr.indexOf('\'',endIndex+1) + 1; // Continue from last position in string
        endIndex   = dataStr.indexOf('\'',beginIndex);
        if((beginIndex<=0) || (endIndex<=0))
            throw new Exception("Package Info data string is invalid.  Error parsing the item Id after which all itmes in the package are selected.  It is missing identifiers and/or information.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        valueStr = dataStr.substring(beginIndex,endIndex).trim();
        // - validate
        if((valueStr==null)||(valueStr.length()==0)) 
            throw new Exception("Package Info data string is invalid.  It contains an invalid value for the item Id after which all items in this package are selected, '"+valueStr+"' in package '"+_packageNum+"'.  Expected a non-null and non-empty string, or the no previous package identifier.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        // Save value
        if(valueStr.compareToIgnoreCase(ITEMID_NULL_TAG)==0)
            _afterItemId = null;
        else
            _afterItemId = valueStr;
        lastSuccessfulIndex = endIndex; // Save index for error reporting if the following fails.
        // "', '"
        // "<Last Item ID (26-char, right-align)>"
        // "'] "
        beginIndex = dataStr.indexOf('\'',endIndex+1) + 1; // Continue from last position in string
        endIndex   = dataStr.indexOf('\'',beginIndex);
        if((beginIndex<=0) || (endIndex<=0))
            throw new Exception("Package Info data string is invalid.  Error parsing the last item Id of this package.  It is missing identifiers and/or information.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        _lastItemId = dataStr.substring(beginIndex,endIndex).trim();
        // - validate
        if((_lastItemId==null)||(_lastItemId.length()==0)) 
            throw new Exception("Package Info data string is invalid.  It contains an invalid value for the last Item Id of this package, '"+_lastItemId+"' in package '"+_packageNum+"'.  Expected a non-null, non-empty string.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        lastSuccessfulIndex = endIndex; // Save index for error reporting if the following fails.
        // "'] "
        // "<Package Folder Path (left-align)>"
        beginIndex = dataStr.indexOf(']',endIndex+1) + 1; // Continue from last position in string
        endIndex   = dataStr.length(); // up to end of string
        if((beginIndex<=-1) || (endIndex<=0))
            throw new Exception("Package Info data string is invalid.  Error parsing the package folder.  It is missing identifiers and/or information.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        valueStr = dataStr.substring(beginIndex,endIndex).trim();
        // - validate
        if((valueStr==null)||(valueStr.length()==0)) 
            throw new Exception("Package Info data string is invalid.  It contains an package folder directory, '"+valueStr+"' in package '"+_packageNum+"'.  Expected a non-null, non-empty string folder directory.  Error after index '"+lastSuccessfulIndex+"'.  Data String:  "+dataStr);
        // - Save folder informaiton (will be validated later)
        _folder = new File(valueStr);
        lastSuccessfulIndex = endIndex; // Save index for error reporting if the following fails.
        
    }//end fromString()

   /**
    * Get the Item ID after which all of the items in this package are found.
    * This is typically the last item Id of the previous package.
    * @return Returns an Item ID or 'null' if this package contains all
    *         items up to the specified last Item ID since there was no
    *         previous package.
    **/
    public String getAfterItemId(){
        return(_afterItemId);
    }//end getFolder()

   /**
    * Get the folder that the package is stored in.
    * @return Returns the folder that it is currently stored in.
    **/
    public File getFolder(){
        return(_folder);   
    }//end getFolder()

   /**
    * Gets the key for use with the PackageInfo sorted TreeMap lists.
    * @return Returns the key to find this package in the package lists.
    **/
    public Integer getKey(){
        return(_key);
    }//end getPackageKey()

   /**
    * If the TExportManagerICM_PackageInfo object instance is not available to use
    * its PackageInfo.getKey() method, this message can construct a key to get the
    * PackageInfo object from the TreeMap lists.
    * @param packageNum - Package number to get the key for.
    * @return Returns the key to find the package by this number in the package lists.
    **/
    public static Integer getKey(int packageNum){
        // validate input
        if(packageNum <= 0) throw new InternalError("Internal Error:  Invalid package number specified for a request to get a key to the Package Info Lists.  Expected package number >= 1, but instead found '"+packageNum+"'.");
        return(new Integer(packageNum));
    }//end getPackageKey()

   /**
    * Get the last Item ID of this package.
    * @return Returns the last item ID of this package.
    **/
    public String getLastItemId(){
        return(_lastItemId);   
    }//end getLastItemID()

   /**
    * Get the number of items in this package.
    * @return Returns the number of items in this package.
    **/
    public int getNumItems(){
        return(_numItems);   
    }//end getNumITems()

   /**
    * Get the central package file for this Export Package.
    * @return Returns the central package file required to reload the TExportPackageICM object.
    **/
    public File getPackageFile(){
        return(_centralPackageFile);
    }//end getPackageFile()

   /**
    * Get the package number.
    * @return Returns the number of this package.
    **/
    public int getPackageNum(){
        return(_packageNum);   
    }//end getPackageNum()

   /**
    * Get a string of the specified size or larger.  If the 
    * specified string is smaller than the length specified, pad
    * with spaces.
    * @param minLength - Minimum size to pad string up to.
    * @param str       - String to pad.
    * @param prependAppend - 'p' | 'r' : Prepend spaces to the beginning of the string | Right Align
    *                        'a' | 'l' : Append spaces at the end of the string. | Left Align
    **/
    private String padString(int minLength, String str, char prependAppend) throws InternalError{
        // validate input
        if(minLength<0) throw new InternalError("Internal Error: Error in input to padString().  Input parameter 'minLength' was negative ('"+minLength+"').  Only posative string lengths are allowed.");
        if(str==null)   throw new InternalError("Internal Error: Error in input to padString().  Input parameter 'str' is 'null'.  A valid string instance was expected.");
        if( (prependAppend!='a')&&(prependAppend!='p')&&(prependAppend!='r')&&(prependAppend!='l'))throw new InternalError("Internal Error: Error in input to padString().  Input parameter 'prependAppend' is not a valid value ('"+prependAppend+"').  Expected 'a' (append/left-align), 'p' (prepend/right-align), 'l' (left-align/append), or 'r' (right-align/prepend).  Review the Javadoc for this method.");
        
        StringBuffer paddedString = new StringBuffer(str);
        
        // Pad with spaces while not large enough.
        while(paddedString.length() < minLength){
            // If append, add to beginning
            if((prependAppend=='a')||(prependAppend=='l')){
                paddedString.insert(0,' ');
            }
            // else if prepend, add to end
            else if((prependAppend=='p')||(prependAppend=='r')){
                paddedString.append(' ');
            }
            // else throw unexpected error
            else throw new InternalError("Internal Error:  Unexpected 'prependAppend' value '"+prependAppend+"'.");
        }//end while(paddedString.length() < minLength){
        return(paddedString.toString());        
    }//end padString

   /**
    * Get a string of the specified size or larger.  If the 
    * specified string is smaller than the length specified, pad
    * with spaces.
    * @param minLength - Minimum size to pad string up to.
    * @param num       - Number to convert to string and then pad.
    * @param prependAppend - 'p' | 'r' : Prepend spaces to the beginning of the string | Right Align
    *                        'a' | 'l' : Append spaces at the end of the string. | Left Align
    **/
    private String padString(int minLength, int num, char prependAppend) throws InternalError{
        // Convert num to String
        Integer numObj = new Integer(num);
        String  numStr = numObj.toString();
        numObj  = null; // drop reference
        // Pad
        String  paddedString = padString(minLength,numStr,prependAppend);
        numStr  = null; // drop reference
        // Return
        return(paddedString);
    }//end padString

   /**
    * If debug printing is enabled in the Export Options, print the
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
    * If trace printing is enabled in the Export Options, print the
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
    * Reconcile any change in storage location.  If the current folder
    * is no longer found, search among available storage locations for
    * this uniquely named folder.  If found, change folder to point to
    * the new location.  If not found, throw error.
    * @param storageLocations - Storage locations that are available.  These are
    *                           assumed to be validated.
    **/
    private void reconcileStorageLocation(File[] storageLocations) throws Exception{
        // If the current storage location does exist, do nothing
        if(_folder.exists()){
            // Do nothing
        }//end if(_folder.exists()){
        // Otherwise try to fix it.
        else{
            // Try all other starage locations until we find a home
            boolean found = false;
            for(int i=0; (i<storageLocations.length) && (found==false); i++){
                File masterFolder = new File(storageLocations[i],TExportManagerICM.MASTER_FOLDER_NAME);
                File tryingFolder = new File(masterFolder,_folder.getName());
                if(tryingFolder.exists()){
                    found = true;   
                    // Validate
                    // <will be validated by validateSettings()>
                    System.out.println("WARNING:  Package folder '"+_folder.getAbsolutePath()+"' could no longer be located.  The folder was instead found at '"+tryingFolder.getAbsolutePath()+"'.  Assuming new location.");
                    // Save New Value
                    _folder = tryingFolder;
                }//end if(tryingFolder.exists()){
            }//end 
            // If not found, throw error.
            if(found==false){ // Not found, throw error
                // Prepare storage location list for error message
                StringBuffer storageLocationsStr = new StringBuffer();
                for(int i=0; i<storageLocations.length; i++){
                    storageLocationsStr.append(storageLocations[i]);
                    // If there is another, add delimiter
                    if((i+1)<storageLocations.length)
                        storageLocationsStr.append(", ");
                }//end for(int i=0; i<storageLocations.length; i++){
                throw new Exception("Package folder '"+_folder.getAbsolutePath()+"' could no longer be located.  It does not exist at its original location, '"+_folder.getAbsolutePath()+"', or among any of the available storage locations '"+storageLocationsStr+"'.  Expected to find package folder '"+_folder.getName()+"' in the '"+TExportManagerICM.MASTER_FOLDER_NAME+"' subdirectory of any available storage location.");
            }//end if(found==false){ // Not found, throw error
        }//end else of if(_folder.exists()){
        
    }//end reconcileStorageLocation()
    
   /**
    * Write the contents to a String
    * 
    * Format:
    *     
    *     <package Num (8-char, right-align)>: <# Items (8-char, right-align)> (<After Item ID (26-char, right-align)>,  <Last Item ID (26-char, right-align)>] <Package Folder Path (left-align)>
    *
    * Example:
    *
    *     Package#   # Items    After Item ID                 Last Item ID                  Folder
    *     --------  ----------  ----------------------------  ----------------------------  ------------------------------
    *     30000000: '10000000' ('12345678901234567890123456', '12345678901234567890123456'] C:\temp\master\package30000000
    *
    * @return Returns a string in the format above.
    **/
    public String toString(){
        StringBuffer str = new StringBuffer();
        
        // "<package Num (8-char, right-align)>"
        str.append(padString(8,_packageNum,'r'));
        // ": '"
        str.append(": '");
        // "<# Items (8-char, right-align)>"
        str.append(padString(8,_numItems,'r'));
        // "' ('"
        str.append("' ('");
        // "<After Item ID (26-char, right-align)>"
        if(_afterItemId==null) // If this is the first, use tag
            str.append(padString(26,ITEMID_NULL_TAG,'r'));
        else
            str.append(padString(26,_afterItemId,'r'));
        // "', '"
        str.append("', '");
        // "<Last Item ID (26-char, right-align)>"
        str.append(padString(26,_lastItemId,'r'));
        // "'] "
        str.append("'] ");
        // "<Package Folder Path (left-align)>"
        str.append(_folder.getAbsolutePath());
        
        return(str.toString());
    }//end toString()

   /**
    * Validate the settings of this object.
    **/
    private void validateSettings(){
        // Validate Input
        if(_packageNum<=0)                      throw new InternalError("Internal Error Creating PackageInfo:  Variable '_packageNum' has an invalid value '"+_packageNum+"'.  This number is expected to be greater than zero.");
        if(_numItems<=0)                        throw new InternalError("Internal Error Creating PackageInfo:  Variable '_numItems' has an invalid value '"+_numItems+"'.  This number is expected to be greater than zero.  Only packages with at least one item should be actually saved as a completed package.");
        if((_afterItemId!=null)&&(_afterItemId.trim().length()==0)) throw new InternalError("Internal Error Creating PackageInfo:  Variable '_afterItemId' is an empty string ('"+_afterItemId+"').  Expected 'null' if there is no starting item ID or a valid value.");
        if(_lastItemId==null)                   throw new InternalError("Internal Error Creating PackageInfo:  Variable '_lastItemId' is 'null'.  There must be a last item ID for this package.");
        if(_lastItemId.trim().length()==0)      throw new InternalError("Internal Error Creating PackageInfo:  Variable '_lastItemId' is an empty string ('"+_lastItemId+"').  There must be a last item ID for this package.");
        if(_enforceFolderExists){ // Only validate that folder exists if it is needed for this instance
            if(_folder==null)                       throw new InternalError("Internal Error Creating PackageInfo:  Variable '_folder' is 'null'.  There must be a folder in which this package resides.");
            if(_folder.isAbsolute()==false)         throw new InternalError("Internal Error Creating PackageInfo:  Variable '_folder' has a path, '"+_folder.getPath()+"' that is not an absolute path.  The folder should have been created with an absolute path.");
            if(_folder.exists()==false)             throw new InternalError("Internal Error Creating PackageInfo:  Variable '_folder' represents a folder, '"+_folder.getAbsolutePath()+"', that does not exist.  This should point to a valid folder.");
            if(_folder.isDirectory()==false)        throw new InternalError("Internal Error Creating PackageInfo:  Variable '_folder' represents a file, '"+_folder.getAbsolutePath()+"', that is not a folder.  It is likely a file.");
        }//end if(_enforceFolderExists){ // Only validate that folder exists if it is needed for this instance
        if(_key==null)                          throw new InternalError("Internal Error Creating PackageInfo:  Variable '_key' is 'null'.  A key to the package info lists should have been generated.");
        if(_key.intValue()!=_packageNum)        throw new InternalError("Internal Error Creating PackageInfo:  Variable '_key' is does not match the expected key value.  Expected key '"+_packageNum+"' but instead found '"+_key+"'.");
        if(_options==null)                      throw new InternalError("Internal Error Creating PackageInfo:  Variable '_options' is 'null'.  A valid instance of an options object is required.");
    }//end validateSettings()
   
}//end class TExportManagerICM_PackageInfo
              
