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

Prerequisite Informaiton:
    For information on Item Creation, Retrieval, Updating, & Deletion, please
    refer to the SItemCreationICM, SItemRetrievalICM, SItemUpdateICM, &
    SItemDeletionICM Samples.

Folders
    A Folder is a DDO of Semantic Type "Folder", which contains a DKFolder Object.
    Specifically, the DDO has an attribute named DKConstant.DK_CM_DKFOLDER which
    contains a "DKFolder" object, a dkCollection that can contain DDOs.

    Item Types
        An Item can be created as a Folder for any Item Type.  An Item Type, such as
        the "S_withChild" Item Type used in the SItemCreationICM sample may have DDOs
        created of any Item Property Type or Semantic Type, including Document, Folder,
        Item, etc.
    
        NOTE: Please refer to SItemCreationICM for "Item Property Type" &
              "Semantic Type" information.
    
    Folders Full DDO Support
        A Folder is a complete DDO representing an Item created of an Item
        Type.  It has the full hierarchial data structure of that Item Type.
        It may be used in all of the functions demonstrated in
        SItemCreationICM.  A Folder is a fully supported DDO with the
        additional functionality of a Folder.

Creating Folder Items
    The DKDatastoreICM.createDDO() method is used to create DDOs at runtime.  As shown
    in SItemCreationICM, when creating Folder Items, the "Item Property Type" or 
    "Semantic Type" simply needs to be specified as DKConstant.DK_CM_FOLDER.

    The DDO will automatically get a DKFolder attribute for the folder mechanism.

Updating Folder Contents
    Folder contents may be added or removed by manipulating the DKFolder collection
    within the DKFolder attribute.

    Incremental Changes Tracked
        All changes to the DKFolder collection object are tracked internally.  
        Each incremental change will be packaged up and sent to the Library Server
        in single call when the root component's DKDDO.update() function is called.
        It is important to note that the incremental changes are tracked and it is
        not the end result that is made persistent.  Optimal modifications rely on
        the fewest changes that result in the same end result.

    Adding Items to a Folder
        All contents placed in the DKFolder must be persistent in the datastore
        before an add() or update() is called on the Folder DDO.  This means that
        all Folder contents must have already had the DKDDO.add() operation called.
    
        The best and most efficient way to add Items to a Folder is to use the 
        DKFolder's addElement() function.  This allows any number of Folder 
        modifications to be grouped into a single call to the Library Server.

        REQUIREMENT:
            It is up to the User Program to use valid DDOs and to not add multiple
            copies of the same Item to the DKFolder.
    
        Immediate Results
            For Immediate results, use the DKFolder.addMember() or
            DKDatastoreExtICM's addToFolder() functions, which make the changes
            persistent immediately, but at an expensive cost of a call to the
            Library Server for every item added to the Folder.

    Removing Items from a Folder
        Removing an Item is performed simply by removing the undesired DKDDO
        object from the DKFolder collection, and then calling DKDDO.update()
        on the root item.
        
        The desired DDO must be located in the DKFolder collection by creating
        a dkIterator from the collection, and searching for it based on the ItemID
        in the PIDs of each DDO within.  If the collection is retrieved with
        attributes, the undesired DDO may be located based on User-Defined Attributes.
        DKFolder.removeElementAt(dkIterator) function may be used to remove the
        undesired DDO.

Retrieving a Folder
    Folder contents are not retrieved by default.  This allows greater control by
    the User Program to control all performance implications.  A retrieval option
    must be set to retrieve the contents.  If Folder contents are not retrieved,
    the DKFolder object & attribute will not exist until the program chooses 
    explicitly to retrieve Folder contents.  

    Retrieve Options: Outbound Links
        Folders require outbound links to be turned on.  The DKFolder is
        actually another form of an link collection effectively containing
        only outbound links of a special predefined link type,
        DKConstantICM.DK_ICM_LINKTYPENAME_DKFOLDER ("DKFolder"), but only
        the target DDOs are stored in the collection instead of DKLink objects.
        Since folders are also technically links, link retrieve options must
        be specified.  All link retrieve options apply to folders, including
        the ability to retrieve the second level of links (contents of 
        subfolders) or subfolder content counts.  However, folder links
        cannot have descriptors.  Review link retrieve option information
        documented in the SLinksICM sample.
        
        However, if you only want to retrieve folder links, you can further
        restrict what link information you want to retrieve, such as by 
        excluding descriptors and filtering only for the folder link type
        since folder links never have descriptors and only use the predefined
        folder link type DKConstantICM.DK_ICM_LINKTYPENAME_DKFOLDER ("DKFolder").
        Unless you are also retrieving other link types, the following 
        restrictions should typically be requested.
        
          - DKRetrieveOptionsICM::linksDescriptors(false)   (default)
          - DKRetrieveOptionsICM::linksTypeFilter(DKConstantICM.DK_ICM_LINKTYPENAME_DKFOLDER)
        
        Since the DKFolder collection contains only the target DDOs of
        folder links, the DKFolder does not contain any inbound links.
        However, when retrieving a document, item, or subfolder, if you
        want to find the folders that contain that item (find the source DDOs
        of folder links), the following option can be enabled when used with
        an inbound link request.  Refer to the option documentation for
        more information on where the source folders are stored in the DDO.
        
          - DKRetrieveOptionsICM::linksInboundFolderSources(boolean)

        Outbound link requests require a request for system attributes
        to obtain the semantic type property to identify folders.
        This is only needed for outbound links.  You can also specify
        attribute filters to retrieve only the system attributes.
        See DKRetrieveOptionsICM::linksOutbound(boolean) documentation.
                
          - DKRetrieveOptionsICM::baseAttributes(boolean)
        
        IMPORTANT PERFORMANCE CONSIDERAITONS
            Review the detailed documentation, including important performance
            considerations for links retrieve options documented in the
            Application Programming Reference (Javadoc) for DKRetrieveOptionsICM
            to ensure the fastest performance of your application.
    
    Retrieve DKFolder Contents
        The retrieved folder contents will take the form of a DKFolder collection, 
        which contains non-retrieved DDOs.  The User Program must explicitly
        choose to retrieve each of the contents (DDOs) individually by calling 
        multi-item retrieve (DKDatastoreICM::retrieveObjects(dkCollection,options))
        or single item retrieve() operation on individual DDOs as necessary.
        
        Typically, applications that list folder contents need only the base
        attributes retrieved for all DDOs in the folder.  Simply set
        DKRetrieveOptionsICM::baseAttribute(true) and pass the DKFolder
        (a dkCollection interface) directly to multi-item retrieve.  If there
        are many folder contents that you display in several pages of information
        to the user, create your own DKSequentialCollection and submit the first
        set of folder contents to retrieve that you plan to display on the first
        page.

Searching / Querying with Folder Contents
    As already described in detail in the "Retrieving a Folder" section above,
    folder contents for the DKFolder collection are actually links
    (see referenced section for more information).  Therefore, when
    searching or querying folder contents, searches must treat them as
    searches for outbound links using the "DKFolder" link type defined
    in cosntant DKConstantICM.DK_ICM_LINKTYPENAME_DKFOLDER.

    Use the "DKFolder" link type for outbound links to query folder contents.

Obtaining Folders Containing a Particular Item
    The same item can be stored in multiple folders at the same time.

    As mentioned in the "Retrieve Options" section above, if you want to
    find the folders containing a particular item, retrieving inbound links
    alone do not gather this information.  The DKFolder exists only on
    the folders, not their contents (unless there is a subfolder, which
    can have its own DKFolder collection for its content).  A different
    construct and option requests the source folds of inbound links.
    As already mentioned, the following option can be enabled when used with
    an inbound link request.  Refer to the option documentation for
    more information on where the source folders are stored in the DDO.
        
          - DKRetrieveOptionsICM::linksInboundFolderSources(boolean)
    
    Alternatively, there is a datastore extension function,
    DKDatastoreExtICM::getFoldersContainingDDO(), which returns a
    collection of all folder items that contain the specified DDO.
    The extension method simply creates a temporary DDO object and uses
    the retrieve option above.  Therefore using retrieve directly simply
    avoid the small overhead to create and destroy a temporary DDO to
    call retrieve for you.
    
    C++ Memory Management Note:
        However, if you use the DKDatastoreExtICM method, you must iterate
        over the collection to free the contents and free the collection.
        The collection returned by this method does not automatically free
        the items within.  If you use the retrieve function and option, 
        the DDO owns the collection and will free the collection and contents.

Links Alternative
    User Applications have the freedom to implement their own folder mechanism.
    Links are a great tool with great versatility that can be easily adapted to
    implement behaviors such as folders.
    
    You could also define your own special folder item type with a child
    component type that contains a reference attribute.  You can add as many
    children with this reference attribute to effectively model a folder using
    a multi-value reference attribute (through child components).  This offers
    faster retrieve performance and version-specific references, but also with
    added complexity maintaining folder relationships especially when versioning
    is enabled and has different behavior.  Both folder links and 
    the custom application-controlled reference attribute-modelled folders
    are viable solutions.

Element vs. Member
    The DKFolder's "Member" methods are very different from the "Element"
    methods from the dkCollection interface.  
    
    Element Methods
        "Element" methods are the recommended and most efficient method to
        use.  Changes are not made persistent until the DKDDO.add() or
        DKDDO.update() methods are called.  All operations are grouped into
        one call to the Library Server.
    
    Member Methods
        For Immediate results, use the "Member" methods.  Changes are made 
        persistent immediately, but at an expensive cost of a call to the 
        Library Server for every add or remove using these methods.
    
addElement() vs. addMember()
    This is an example of the "Element vs. Member" section above.

removeElement() vs. removeMember()
    This is an example of the "Element vs. Member" section above.

DKDatastoreExtICM.addToFolder() vs. addElement()
    The Datastore Extention's addToFolder() function is very much like the
    "Member" operations on the DKFolder.  This is a slightly different
    immediate, but expensive method.

DKDatastoreExtICM.addToFolder() vs. addMember()
    There is very little difference between these two methods.  The Datastore
    Extension's addToFolder() method may not affect the contents of the
    DKFolder object.

*******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;

/************************************************************************************************
 *          FILENAME: SFolderICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Creating Folders, Adding Items to a Folder, Retrieving Folders, Retrieving
 *                    Folder Contents / Data, Element vs. Member, Brief on Searching Folders, &
 *                    Obtaining Folders Containing a Particular Item
 *                    ---------------------------------------------------------------------------
 *     DEMONSTRATION: Creating Folders
 *                    Adding Items to a Folder
 *                    Retrieving Folders
 *                    Retrieving Folder Contents / Data
 *                    Element vs. Member
 *                    Brief Explanation of Searching Folders
 *                    Obtaining Folders Containing a Particular Item
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java SFolderICM <database> <userName> <password>
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: The Data Model must be defined.  If needed please run the following Samples  
 *                        - SAttributeDefinitionCreationICM
 *                        - SAttributeGroupDefCreationICM
 *                        - SReferenceAttrDefCreationICM
 *                        - SItemTypeCreationICM
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 ************************************************************************************************/
public class SFolderICM{
    
    //-------------------------------------------------------------
    // Main
    //-------------------------------------------------------------
    /**
     * Run the Sample.
     * @param argv[] String Array containing arguments.  Optional arguments are <databse> <userName> <password>.
     */
    public static void main(String argv[]) throws DKException, Exception{
    
        // Defaults for connecting to the database.
        String database = "icmnlsdb";//SConnectDisconnectICM.DEFAULT_DATABASE;
        String userName = "icmadmin";//SConnectDisconnectICM.DEFAULT_USERNAME;
        String password = "Bigblue1";//SConnectDisconnectICM.DEFAULT_PASSWORD;
        
        //------------------------------------------------------------
        // Checking for input parameters
        //--------------------------------------------------------------
        if (argv.length < 3) { // if not all 3 arguments were specified, use defaults and report correct usage.
            System.out.println("Usage: " );
            System.out.println("  java SFolderICM <database> <userName> <password>" );
            System.out.println("  *** Some parameters not specified, using defaults..." );
            System.out.println("");
        } else {  // otherwise enough parameters were specified, use the first 3 for the 3 parameters.
            if (argv.length > 0) database = argv[0];
            if (argv.length > 1) userName = argv[1];
            if (argv.length > 2) password = argv[2];
        }//end else
        String ver = "8.5";//SConnectDisconnectICM.VERSION;
        
        System.out.println("===========================================");
        System.out.println("IBM DB2 Content Manager                v"+ver);
        System.out.println("Sample Program:  SFolderICM");
        System.out.println("-------------------------------------------");
        System.out.println(" Database: "+database);
        System.out.println(" UserName: "+userName);
        System.out.println("===========================================");
        
        try{
            //-------------------------------------------------------------
            // Connect to datastore
            //-------------------------------------------------------------
            // See Sample SConnectDisconnectICM for more information
            System.out.println("Connecting to datastore (Database '"+database+"', UserName '"+userName+"')...");

                DKDatastoreICM dsICM = new DKDatastoreICM();  // Create new datastore object.
                dsICM.connect(database,userName,password,""); // Connect to the datastore.

            System.out.println("Connected to datastore (Database '"+dsICM.datastoreName()+"', UserName '"+dsICM.userName()+"').");

            //-------------------------------------------------------------
            // Creating a Folder
            //-------------------------------------------------------------
            // Simply specify "DK_CM_FOLDER" for the secont parameter of createDDO.
            System.out.println("Creating a Folder...");

	            DKDDO ddoFolder  = dsICM.createDDO("S_simple", DKConstant.DK_CM_FOLDER);

	            ddoFolder.add(); // Save the DDO in the persistent datastore.

            System.out.println("Created a Folder.");

            //-------------------------------------------------------------
            // Create Contents to Place in Folder
            //-------------------------------------------------------------
            // create 3 Items, one of Item Property type Document, one Folder, & one Item.
            System.out.println("Creating & Saving Sample Items...");

	            DKDDO ddoDocument = dsICM.createDDO("S_simple", DKConstant.DK_CM_DOCUMENT);
	            DKDDO ddoFolder2  = dsICM.createDDO("S_simple", DKConstant.DK_CM_FOLDER);   
	            DKDDO ddoItem     = dsICM.createDDO("S_simple", DKConstant.DK_CM_ITEM);     

	            ddoDocument.add();
	            ddoItem.add();
	            ddoFolder2.add();

            System.out.println("Created & Saved Sample Items.");

            //-------------------------------------------------------------
            // Accessing the DKFolder Attribute
            //-------------------------------------------------------------
            // The DKFolder attribute is accessed the same way shown in 
            // SItemCreationICM & SItemRetrievalICM.
            //
            // This attribute can be found programatically as described in
            // Sample SItemRetrievalICM.
            System.out.println("Accessing the DKFolder Attribute...");
            
                DKFolder dkFolder = (DKFolder) ddoFolder.getData(ddoFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER)); 
                
                // Important:  If the DDO is not retrieved with Folder contents,
                //             the above will fail due to a null pointer exception.
                //             The following accounts for this.
                
                short dataid = ddoFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER); 
                
                if(dataid==0)
                    throw new Exception("No DKFolder Attribute Found!  DDO is either not a Folder or Folder Contents have not been explicitly retrieved.");
                    
                dkFolder = (DKFolder) ddoFolder.getData(dataid); 

            System.out.println("Accessed the DKFolder Attribute.");

            //-------------------------------------------------------------
            // Check out the Folder for Modification
            //-------------------------------------------------------------
            System.out.println("Checking Out / Locking Folder Before Update...");

                dsICM.checkOut(ddoFolder);  // Must check out / lock before updating.

            System.out.println("Checked Out / Locked Folder Before Update.");

            //-------------------------------------------------------------
            // Adding Contents to Folder
            //-------------------------------------------------------------
            // As explained in the header, the "Element" methods below may
            // be replaced with "Member" methods, which are an Immediately
            // presistent result which does not require the following 
            // DKDDO.update(), but is very expive.  
            System.out.println("Adding Contents to Folder...");
            
                dkFolder.addElement(ddoDocument);
                dkFolder.addElement(ddoItem);
                dkFolder.addElement(ddoFolder2);  // Note, Folders can contain sub-folders.

            System.out.println("Added Contents to Folder.");

            //-------------------------------------------------------------
            // Make changes persistent
            //-------------------------------------------------------------
            // Remember from SLinksICM, it is also recommended that you use
            // DKConstantICM.DK_ICM_NO_LINKS_MEMORY_CURRENCY when updating links.
            System.out.println("Making Changes Persistent...");
        
                ddoFolder.update();        // Remember to explicitly check in when finished.
                dsICM.checkIn(ddoFolder);  // Can specify DKConstant.DK_CM_CHECKIN option or use the datastore method.

            System.out.println("Made Changes Persistent.");

            //-------------------------------------------------------------
            // Printing Folder Contents
            //-------------------------------------------------------------
            System.out.println("Printing Folder Contents...");
                printFolderContents(ddoFolder);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed Folder Contents.");

            //-------------------------------------------------------------
            // Check out the Folder for Modification
            //-------------------------------------------------------------
            System.out.println("Checking Out / Locking Folder Before Update...");

                dsICM.checkOut(ddoFolder);  // Must check out / lock before updating.

            System.out.println("Checked Out / Locked Folder Before Update.");

            //-------------------------------------------------------------
            // Remove Contents from a Folder        
            //-------------------------------------------------------------
            // Please refer to the Application Programming Reference for 
            // a complete list of "DKFolder" Operations.
            System.out.println("Removing Contents from a Folder...");

                // Look for the Item to Remove.  In this case, we will look for 
                // the ddoItem.
                dkIterator iter = dkFolder.createIterator();
                while(iter.more()){                      // while there are still items, continue searching
                    DKDDO ddo = (DKDDO) iter.next();     // Move pointer to next element & return that object.
                    if(ddo.getPidObject().pidString().compareTo(ddoItem.getPidObject().pidString())==0){

                        dkFolder.removeElementAt(iter);  // Now that we found the ddoItem, remove it.

                    }
                }
            
            System.out.println("Removed Contents from a Folder.");

            //-------------------------------------------------------------
            // Make Changes Persistent
            //-------------------------------------------------------------
            // Remember from SLinksICM, it is also recommended that you use
            // DKConstantICM.DK_ICM_NO_LINKS_MEMORY_CURRENCY when updating links.
            System.out.println("Making Changes Persistent...");            
        
                ddoFolder.update(DKConstantICM.DK_ICM_NO_LINKS_MEMORY_CURRENCY);  // Remember to explicitly check in when finished.
                dsICM.checkIn(ddoFolder);                                         // Can specify DKConstant.DK_CM_CHECKIN option or use the datastore method.

            System.out.println("Made Changes Persistent.");

            //-------------------------------------------------------------
            // Recreate DDO to Demonstrate Retrieval
            //-------------------------------------------------------------
            System.out.println("Recreating Folder DDO...");
            
                String ddoFolderPidString = ddoFolder.getPidObject().pidString();  // Get the PID String
                ddoFolder = dsICM.createDDOFromPID(ddoFolderPidString);            // Recreate Blank DDO Using PID String.

            System.out.println("Recreated Folder DDO.");

            //-------------------------------------------------------------
            // Retrieving Folder
            //-------------------------------------------------------------
            // This section covers retrieving a Folder Item with the 
            // DKFolder attribute / collection.
            //
            // Outbound link requests require a request for system attributes
            // to obtain the semantic type property to identify folders.
            // This is only needed for outbound links.  You can also specify
            // attribute filters to retrieve only the system attributes.
            // See DKRetrieveOptionsICM::linksOutbound(boolean) documentation.
            System.out.println("Retrieving Folder...");
            
                DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
                dkRetrieveOptions.linksOutbound(true);
                dkRetrieveOptions.baseAttributes(true);       // Can also specify attribute filters to request system-attributes only.
                dkRetrieveOptions.linksTypeFilter(DKConstantICM.DK_ICM_LINKTYPENAME_DKFOLDER);
            
                ddoFolder.retrieve(dkRetrieveOptions.dkNVPair());  // Outbound Links must be specified.

            System.out.println("Retrieved Folder.");

            //-------------------------------------------------------------
            // Printing Folder Contents
            //-------------------------------------------------------------
            System.out.println("Printing Folder Contents...");
                printFolderContents(ddoFolder);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed Folder Contents.");

            //-------------------------------------------------------------
            // Retrieving Folder Contents' Data
            //-------------------------------------------------------------
            // The DKFolder object will contain a list of non-retrieved 
            // DDOs.  Each must be retrieved if desired.  Multi-item retrieve
            // may be substituted.
            System.out.println("Retrieving Folder Contents' Data...");
            
                // Get the DKFolder object.        
                dataid = ddoFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER); 
                dkFolder = (DKFolder) ddoFolder.getData(ddoFolder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER)); 
                
                // Use Multi-Item Retrieve for Maximum Performance
                dkRetrieveOptions.baseAttributes(true); // Suppose we just want attributes for all within the folder.
                dkRetrieveOptions.linksOutbound(false); // Since we reused the options instance, we can turn off outbound links if we do not need them for all contents within the folder at this time. 
                dsICM.retrieveObjects(dkFolder,dkRetrieveOptions.dkNVPair());

            System.out.println("Retrieved Folder Contents' Data.");
            
            //-------------------------------------------------------------
            // Obtaining All Folders Containing DDO
            //-------------------------------------------------------------
            // If you want to find out which folders contain a particular 
            // DDO, since contents can be stored in multiple places at the
            // same time, refer to the following.
            // 
            // As described in the header documentation section            
            // "Obtaining Folders Containing a Particular Item",
            // there are two ways to find all folders that contain
            // a particular item.  First, you can use the retrieve
            // option mentioned in the reference section.  Alternatively,
            // you can use the datastore extension method shown below.
            // Refer to the DKRetrieveOptionsICM Javadoc for details on
            // using the retrieve option. 
            //
            // Remember, there is slightly less overhead if you instead
            // use retrieve directly with the retrieve option to request
            // this information.
            System.out.println("Obtaining All Folders Containing DDO...");

                DKDatastoreExtICM dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object
                dkCollection list = dsExtICM.getFoldersContainingDDO(ddoDocument); // Search for all folders that contain the ddoDocument Item.
                
                // Print List of IDs
                System.out.println("Folders Found:");
                iter = list.createIterator();        // create an iterator to go through list
                while(iter.more()){                  // while there are still items in the list, continue
                    DKDDO ddo = (DKDDO) iter.next(); // Move pointer to next element & return that object.
                    System.out.println("      - Item ID:  "+((DKPidICM)ddo.getPidObject()).getItemId());
                }                

            System.out.println("Obtained All Folders Containing DDO.");

            //-------------------------------------------------------------
            // Clean Up Database After Sample
            //-------------------------------------------------------------
            // This sample will clean up / undo everything that stored
            // in the database.
            System.out.println("Cleaning Up...");

                ddoItem.del();
                ddoFolder2.del();
                ddoDocument.del();
                ddoFolder.del();

            System.out.println("Cleaned Up.");
            
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
            //SConnectDisconnectICM.printException(exc);  // Print the exception using the function listed below.
            throw(exc);
        } catch (Exception exc) {
            //SConnectDisconnectICM.printException(exc);  // Print the exception using the function listed below.
            throw(exc);
        }
    }// end main

    //=================================================================
    // Wrapper Functions
    //=================================================================
    // The following are wrapper functions for functionality covered
    // in this sample.  These functions can be used by other samples.

   /**
    * Print Folder Contents
    * @param folder  DDO of Semantic Type Folder.
    **/
    public static void printFolderContents(DKDDO folder) throws Exception{
        
        String folderItemId = ((DKPidICM)folder.getPidObject()).getItemId();

        // Get the DKFolder object.        
        short dataid = folder.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER); 
        if(dataid==0)
            throw new Exception("No DKFolder Attribute Found!  DDO is either not a Folder or Folder Contents have not been explicitly retrieved.");
        DKFolder dkFolder = (DKFolder) folder.getData(dataid); 
        
        // Print the list        
        System.out.println("Folder ("+folderItemId+") Contents:");
        dkIterator iter = dkFolder.createIterator(); // Create an Iterator go to through DDO Collection.
        while(iter.more()){                          // While there are items still to be printed, continue
            DKDDO ddo = (DKDDO) iter.next();         // Move pointer to next element & return that object.
            System.out.println("     Item Id:  "+((DKPidICM)ddo.getPidObject()).getItemId() +" ("+ddo.getPidObject().getObjectType()+")");
        }
    }

}//end class SFolderICM
              