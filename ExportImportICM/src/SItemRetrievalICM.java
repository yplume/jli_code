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

Creating Blank DDOs for Existing Items
    Before you can retrieve data, you must obtain a DDO instance representing
    an item or component that you want to retrieve.  The SItemCreationICM
    sample demonstrated how to create DKDDO instances for new items and
    child components.  However, this time the items and child components 
    already exist in the system.  To obtain DDO instances to existing items
    or child components, you must either know the unique persistent
    identification (PID), use query/search to find the items, or already
    have the DDO in memory from a previous operation.  Query is demonstrated
    separately by the SSearchICM sample and can call retrieve on your
    behalf while returning the DDO instances you requested.  This sample
    focuses on recreating DKDDO instances to existing items.
    
    All DDOs must be created through the DKDatastoreICM.createDDO() functions.
    However, this time use the createDDOFromPID(...) variants to properly 
    recreate DDOs for existing items.  The PID is a Persistent Identification
    that uniquely identifies the particular component / row in the database.
    You can use a PID object or a PID string.  If you have access to the PID
    object, use DKDatastoreICM.createDDOFromPID(DKPidICM) to save a small
    application-side cost to parse and recreate a PID object.  Otherwise
    use DKDatastoreICM.createDDOFromPID(String pidString).  Note that the
    PID object is cloned so your original PID is not affected by the new
    DDO and similarly the new DDO is not affected by the original.

    Creating DDOs using "new" or any means of instantiating a DDO instance
    directly from a DDO class is *not* recommended.  It is technically
    possible to create a retrievable DDO by using the DKDDO constructor and
    manually setting the PID information, but it is strongly recommended and
    easiest to use the datastore's createDDO() functions (or query).  If you
    choose to use the constructor, be sure to create the correct subclass of
    DKDDO for specializations based on item type configuration (explained
    further in SResourceItemRetrievalICM). 
    
    You do not have to recreate blank DDO instances for DDOs already
    created and retrieved in memory.  You can call retrieve to refresh
    existing DDOs.

    Recommended For Recreating DDOs:
        DKDatastoreICM::createDDOFromPID(DKPidICM* pidObject)
        DKDatastoreICM::createDDOFromPID(DKString pidString)

        Do not use the deprecated createDDO(...) variations that take the
        same parameters.

        Each of these methods are described in more detail in the Application
        Programming Reference (Javadoc).

    Blank DDOs versus Initialized Metadata Structures  (WARNING for C++)
        When recreating DDOs for existing components from PID objects or PID strings,
        you should always create blank DDOs (with completed PIDs).  Then use
        retrieve with DKRetrieveOptionsICM to populate the persistent data
        into the blank recreated DDO.

        For existing items, always start out with a truly blank DDO and use
        retrieve to populate accurate data.  This way you can easily
        distinguish between an accurate value that has been retrieved from
        an initial or default state.  Furthermore, you can save
        performance costs and memory from creating unnecessary default
        structures and values that are not accurate for the current DDO
        and might be needed.
        
        More importantly, you can easily misinterpret the default values
        with the actual values or call update with some of the defaults
        still remaining in the DDO which could override the persisted
        values.  For example, a default ACL name, semantic type, and
        original default attribute values appear in the DDO but are not
        related to the actual value.  Additionally empty child, folder,
        and part collections can appear in the DDO by default, but this
        can lead to a misunderstanding that the DDO has no children,
        but perhaps you just didn't request children during retrieval.

        Without a truly blank DDO, you cannot necessarily tell whether
        any given data item is the actual retrieved value or just a
        default value pre-filled into the DDO when you recreated it.
        It is better to start with a truly blank DDO and then after
        retrieval, you can rely on any data that appears as the
        accurate state at the time of retrieval.

        Again, without a blank DDO, depending on your retrieve options,
        attribute filters (or projections) or other reasons partial data
        retrieval could leave accurate data mixed with remaining defaults
        and initialized structures.  For any attribute, property, child
        collection, parts collection, or folder collection in the DDO,
        you want to be certain that it is accurate if it exists in the DDO
        rather than being uncertain whether you are looking at the initial
        value or if it was verified and updated by the retrieve operation.

        For example, suppose you find an empty child collection in your DDO.
        If you had  initialized the metadata structures in a recreated DDO or
        if you used deprecated bitwise 'int' retrieve options (or defaults),
        you do not know whether the empty child collection means there are no
        children in the persistent item or you did not retrieve children of
        this child type.  However, if you uses a blank DDO and retrieve with
        DKRetrieveOptionsICM, you can trust that no collection means not yet
        known and a collection with cardinality '0' means that retrieve has
        checked and there are in fact '0' for that child type.  Similarly had
        you used attribute filters with a non-blank DDO, you would not know
        which attributes were retrieved and accurate among the initialized
        full list of attributes.

        Remember to use DKRetrieveOptionsICM with your retrieve request to
        enable optimizations and ensure that only features you request are
        updated within the DDO.  Using deprecated bitwise 'int' options or
        defaults can alter other aspects within the DDO that are not refreshed.

        Some deprecated createDDO() methods offer an additional boolean parameter,
        'initializeMetadataStructures' to control whether DDOs are pre-filled with
        defaults and initialized structures.  (See parameter documentation in the
        various methods for more information).  However, you should *always*
        initialize for new component DDOs and *never* initialize for recreating
        DDOs from PIDs.  All methods that offer non-recommended or ambiguous
        behavior has been deprecated.  Use only non-deprecated createDDO() methods
        and you will always be using the recommended solution.

        WARNING FOR C++
        Unlike Java, the default behavior for deprecated methods
        createDDO(String pidString) and createDDO(DKPidICM) in C++ do *not*
        create blank DDOs since it initializes metadata structures by default.
        Therefore C++ DDOs from these deprecated methods waste unnecessary memory
        and performance initializing data structures and suffer from ambiguity
        concerns mentioned previously.  These methods have been deprecated and
        replaced by createDDOFromPID() which offers the recommended behavior by
        default (does not initialize metadata structures).  Furthermore, old query
        or old retrieve (whenever you do *not* submit a DKRetrieveOptionsICM
        instance) use the deprecated default behavior internally which is another
        reason why you should always submit a DKRetrieveOptionsICM instance to
        query or retrieve. 

    If you do not have the PID object or PID string, instead use query
    (DKDatastoreICM::evaluate() or execute() -- see SSearchICM sample)
    to return recreated DDOs for existing items that match your search conditions.

Retrieving Items
    With any valid DDO, you can retrieve or refresh its contents.
    You can directly retrieve any DDO, including root and child
    component DDOs.  An "item" is typically accessed through the
    root component DDO.  
    
    There are two types of retrieve methods, multi-item retrieve and
    single-item retrieve.  Multi-item retrieve offers a very significant
    performance advantage over making separate calls to single-item retrieve.
    Whenever you plan to retrieve more than one DDO, always use multi-item
    retrieve unless you require data that can only be retrieved through
    single-item retrieve.  For example, resource content (covered by
    SResourceItemRetrievalICM) can only be retrieved through single-item
    retrieve in order to acknowledge the performance cost of retrieving
    large objects which typically should be used on a single-item basis.
    In this example, an application should typically use multi-item retrieve
    for all other data, and when the resource content for a specific item
    is desired, that item should be submitted to single-item retrieve for
    its content.
    
        Multi-Item Retrieve (RECOMMENDED)
            DKDatastoreICM::retrieveObjects(dkCollection,DKNVPair[] retrieveOptions)
        Single-Item Retrieve
            DKDatastoreICM::retrieveObject(DKDDO,DKNVPair[] retrieveOptions)
            DKDDO::retrieve(DKNVPair[] retrieveOptions)
            DKLobICM::retrieve(DKNVPair[] retrieveOptions)
            ...and other DKDDO and DKLobICM subclasses...

    Retrieve options can be specified and are STRONGLY RECOMMENDED to
    specify exactly what subset of data is desired.  Retrieve only data
    you plan to use and omit requests for data that you will not use.
    You can push your performance significantly further by using retrieve
    options wisely and spending performance costs only for data you will
    use.  For data you might use only for one item, consider deferring
    additional retrieval until it is needed.  For example, if listing 
    10,000 query results, consider retrieving only minimal attributes for
    the first page of results and retrieve more data only if the user 
    requests the next page or clicks on a document to view.  You can
    avoid unecessary calls to the server, network communication, 
    memory usage and object counts for unecessary objects, and other
    overhead.  Performance implications of every retrieve option setting
    is documented in detail in the reference documentation (Javadoc)
    for each setting.  You are in complete control.  Retrieve exactly
    what you need, nothing more and nothing less, and you will squeeze
    the best possible performance.
    
    Retrieve options are provided through the DKRetrieveOptionsICM
    class, which includes detailed reference documentation (Javadoc).
    The Javadoc is your complete guide to retrieve options with 
    detailed information on performance considerations, behavior notes,
    data structures of where to find retrieved data within the DKDDO for
    any given option, and more.  In order to get the best possible
    performance, you should thoroughly understand the DKRetrieveOptionsICM
    Javadoc and use retrieve options as wisely as possible.
    
        (!) See DKRetrieveOptionsICM Javadoc

    You should always provide retrieve options, even if it is to simply
    specify that you plan to exclude everything and not retrieve any
    new data.  Retrieve option settings are built up incrementally, with
    nothing selected by default.  Each setting marked as 'true' is
    considered a specific request for data and any setting marked as
    'false' (default) is considered a specific request to exclude
    that data.  It is much better to submit an options object with no
    changes to clearly demonstrate that you request nothing than for
    the system to attempt to guess your intentions.  If no option
    is provided at all, as mentioned before, the default is one
    of the deprecated bitwise 'int' options and the default varies
    depending on which method you invoke.    

    This sample does not duplicate documentation for DKRetrieveOptionsICM.
    Refer to the primary class Javadoc at the top of DKRetrieveOptionsICM
    for a brief overview and a recommended order in which to consider
    various options.  The example code below provides a very brief peek
    at retrieving DDOs.
    
        // Given DKDatastoreICM instance as variable "dsICM".
        // Given DKDDO instances as variables "ddo1","ddo2", and "ddo3".
        DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
        dkRetrieveOptions.baseAttributes(true);
        dkCollection coll = new DKSequentialCollection();
        coll.addElement(ddo1);
        coll.addElement(ddo2);
        coll.addElement(ddo3);
        dsICM.retrieveObjects(coll,dkRetrieveOptions.dkNVPair());        
    
    You can reuse the same retrieve options object instance instead of
    creating a new one for every call.  However, remember to set undesired
    options to 'false' since they do not revert to their defaults between
    uses.

    Note, when re-retrieving (refreshing) an existing DDO instance,
    only data items that you requested to be refreshed (based on
    retrieve options) are cleared and updated to the current state.
    Retrieve does not modify data within the DDO that you did not
    request to refresh.
    
    Old Bitwise Retrieve Options
        In V8.3 Fix Pack 4, the DKRetrieveOptionsICM class was introduced
        in Java and the bitwise 'int' operations were deprecated.
         
        You might see examples of a deprecated form of retrieve options
        as bitwise 'int' retrieve options.  For example, an old style of
        of retrieve options accepted were specified through an 'int' 
        parameter type and accepted bitwise values from constants that
        could be added together.  For example, option
        DKConstantICM.DK_CM_CONTENT_ATTRONLY was a bitwise 'int' retrieve
        option.  Furthermore, retrieve operations with no options argument
        assume a default bitwise 'int' option and should also be considered
        deprecated.
        
        Always use DKRetrieveOptionsICM whenever possible over the 
        bitwise 'int' retrieve options or no-option variants.  Simply by
        using DKRetrieveOptionsICM, you enable a significant number of 
        optimizations and product updates that are not available when
        you use the old bitwise 'int' retrieve options, even if 
        equivalent retrieve settings.  If you had previously used the
        bitwise 'int' retrieve options, thoroughly read the 
        DKRetrieveOptionsICM reference and take advantage of faster
        performance by using the new granular options as wisely as 
        possible.
         
Retrieving Child Components
    It is possible to retrieve an individual child component, given its PID string, 
    However, items cannot be updated by retrieving the component and calling the
    DKDDO.update() method.  All Updates should be handled through the Root Item.
    The child component may be modified within the retrieved structure of the 
    Root Item, and then the DKDDO.update() method should be called on the Root Component.

Reading Attributes / Data Items Programmatically
    The user program does not need to know the type of java.lang.Object
    that each attribute is.  It can be determined either through the
    attribute definition (see SAttributeDefinitionCreation/RetrievalICM),
    or by the data property type.  Valid data property types are listed below.

    Note: The following applies to all connectors.  A subset is used
          by the DB2 Content Manager connector API.
          
    Data Type         Constant                             Object
    ----------------  -----------------------------------  ----------------------------
    Undefined         DK_CM_DATAITEM_TYPE_UNDEFINED        java.lang.Object (Or Other)
    String (Varchar)  DK_CM_DATAITEM_TYPE_STRING           java.lang.String
    Short             DK_CM_DATAITEM_TYPE_SHORT            java.lang.Short
    Long              DK_CM_DATAITEM_TYPE_LONG             java.lang.Long
    Float             DK_CM_DATAITEM_TYPE_FLOAT            java.lang.Double
    Decimal           DK_CM_DATAITEM_TYPE_DECIMAL          java.math.BigDecimal
    Date              DK_CM_DATAITEM_TYPE_DATE             java.sql.Date
    Time              DK_CM_DATAITEM_TYPE_TIME             java.sql.Time
    Timestamp         DK_CM_DATAITEM_TYPE_TIMESTAMP        java.sql.Timestamp
    Double            DK_CM_DATAITEM_TYPE_DOUBLE           java.lang.Double
    Byte Array        DK_CM_DATAITEM_TYPE_BYTEARRAY        byte bytes[]
    DDO               DK_CM_DATAITEM_TYPE_DDOOBJECT        com.ibm.mm.sdk.common.DKDDO
    XDO               DK_CM_DATAITEM_TYPE_XDOOBJECT        com.ibm.mm.sdk.common.DKLobICM / dkXDO / or Sub-Class
    Data Object Base  DK_CM_DATAITEM_TYPE_DATAOBJECTBASE   com.ibm.mm.sdk.common.dkAbstractDataObjectBase
    Collection        DK_CM_DATAITEM_TYPE_COLLECTION       com.ibm.mm.sdk.common.DKSequentialCollection, or dkCollection
    DDO Collection    DK_CM_DATAITEM_TYPE_COLLECTION_DDO   com.ibm.mm.sdk.common.DKChildCollection, DKFolder, DKSequentialCollection, dkCollection, or other.
    XDO Collection    DK_CM_DATAITEM_TYPE_COLLECTION_XDO   com.ibm.mm.sdk.common.DKParts, DKSequentialCollection, dkCollection, or other.
    Link Collection   DK_CM_DATAITEM_TYPE_LINKCOLLECTION   com.ibm.mm.sdk.common.DKLinkCollection
    Array             DK_CM_DATAITEM_TYPE_ARRAY            java.lang.Object[]
    ----------------------------------------------------------------------------------
    ** Constants Defined in com.ibm.mm.sdk.common.DKConstant

Detecting Nonexistent Data Items
    When obtaining the dataid of any attribute, property, etc. of a DDO, if
    '0' is returned, it indicates that the data item is nonexistent in this
    ddo object.

*******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;

/************************************************************************************************
 *          FILENAME: SItemRetrievalICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Retrieving Items, PID Information, Retrieving & Accessing Child Components
 *                    & Multivalue Attributes, Retrieval Options, Accessing Attributes
 *                    Explicitly & Programmatically, Detecting Nonexistent Attributes & 
 *                    Properties in DDO, Printing DDOs.
 *                    ---------------------------------------------------------------------------
 *     DEMONSTRATION: PID Information
 *                    Retrieving Items
 *                    Retrieving Child Components
 *                    Accessing Child Components / Multivalue Attributes
 *                    Retrieval Options
 *                    Accessing Attributes
 *                    Accessing Attributes Programmatically Using the Component Definition
 *                    Accessing All DDO Data Items Programmatically
 *                    Detecting Nonexistent Attributes / Properties in DDO
 *                    Printing DDOs
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java SItemRetrievalICM <database> <userName> <password>
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: The Data Model must be defined.  If needed please run the following Samples  
 *                        - SAttributeDefinitionCreationICM
 *                        - SAttributeGroupDefCreationICM
 *                        - SReferenceAttrDefCreationICM
 *                        - SItemTypeCreationICM
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 *                    SItemCreationICM.java
 ************************************************************************************************/
public class SItemRetrievalICM{
    
    //-------------------------------------------------------------
    // Main
    //-------------------------------------------------------------
    /**
     * Run the Sample.
     * @param argv[] String Array containing arguments.  Optional arguments are <databse> <userName> <password>.
     */
    public static void main(String argv[]) throws DKException, Exception{
    
        // Defaults for connecting to the database.
        String database = SConnectDisconnectICM.DEFAULT_DATABASE;
        String userName = SConnectDisconnectICM.DEFAULT_USERNAME;
        String password = SConnectDisconnectICM.DEFAULT_PASSWORD;
        
        //------------------------------------------------------------
        // Checking for input parameters
        //--------------------------------------------------------------
        if (argv.length < 3) { // if not all 3 arguments were specified, use defaults and report correct usage.
            System.out.println("Usage: " );
            System.out.println("  java SItemRetrievalICM <database> <userName> <password>" );
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
        System.out.println("Sample Program:  SItemRetrievalICM");
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
            // Create an Sample Items
            //-------------------------------------------------------------
            // create 3 Items, one of Item Property type Document, one Folder, & one Item.
            // Will use the function for creating a simple item with children, with the
            // attribute data set from the SItemCreationICM Sample.
            System.out.println("Creating & Saving Sample Items...");

	            DKDDO ddoDocument = SItemCreationICM.createSimpleItemWithChildren(dsICM, DKConstant.DK_CM_DOCUMENT);
	            DKDDO ddoFolder   = SItemCreationICM.createSimpleItemWithChildren(dsICM, DKConstant.DK_CM_FOLDER);   
	            DKDDO ddoItem     = SItemCreationICM.createSimpleItemWithChildren(dsICM, DKConstant.DK_CM_ITEM);     

                // Set the reference attribute for ddoItem before it is added, and after the others were added.
                dsICM.checkOut(ddoItem);   // Covered in SItemUpdateICM
                ddoItem.setData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_myRef"),ddoFolder); // Using DKDDO named 'ddoFolder' made persistent above.
                ddoItem.update();          // Covered in SItemUpdateICM

            System.out.println("Created & Saved Sample Items.");

            //-------------------------------------------------------------
            // Obtain the Pid Strings so that the DDOs may be recreated.
            //-------------------------------------------------------------
            // First, obtain the DKPidICM object using the getPidObject() operation.
            // If the DDO was created by DKDatastoreICM, it may be cast from dkPid to DKPidICM.
            // Next, call the pidString() function for a String representation.
            System.out.println("Obtaining PID Information...");

                String ddoDocumentPidString = ddoDocument.getPidObject().pidString();
                String ddoFolderPidString   = ddoFolder.getPidObject().pidString();
                String ddoItemPidString     = ddoItem.getPidObject().pidString();
                
            System.out.println("Obtained PID Information.");

            //-------------------------------------------------------------
            // Re-create Blank DDOs for Existing Items
            //-------------------------------------------------------------
            // As stated in the header documentation, this is one valid 
            // means to re-create a blank ddo for an single existing item 
            // and should be used over using the DKDDO() constructor.
            // Query/Search is the most popular and useful approach to 
            // obtaining empty items with Pid information filled out.
            //
            // For existing items, always recreate as blank DDOs and use
            // retrieve to populate accurate data.  This way you can easily
            // distinguish between an accurate value that has been retrieved
            // from an initialial or default state.  See SItemRetrievalICM
            // sample for more information on this important topic.
            System.out.println("Re-creating Blank DDOs for Existing Items...");

                ddoDocument = dsICM.createDDOFromPID(ddoDocumentPidString);   // ALWAYS use "FromPID" variation of createDDOFromPID for recreating DDOs.
                ddoFolder   = dsICM.createDDOFromPID(ddoFolderPidString);
                ddoItem     = dsICM.createDDOFromPID(ddoItemPidString);

            System.out.println("Re-created Blank DDOs for Existing Items.");

            //-------------------------------------------------------------
            // Create a Collection for Calling Multi-Item Retrieve
            //-------------------------------------------------------------
            // In order to call multi-item retrieve, a collection of
            // all DDO instance to be retrieved is needed.
            // Simply create any collection supporting the 
            // dkCollection interface.  One such impelmentation
            // provided is DKSequentialCollection.
            System.out.println("Creating a Collection for Calling Multi-Item Retrieve...");
            
                dkCollection ddoColl = new DKSequentialCollection();
                ddoColl.addElement(ddoDocument);
                ddoColl.addElement(ddoFolder);
                ddoColl.addElement(ddoItem);
            
            System.out.println("Created a Collection for Calling Multi-Item Retrieve.");
            
            //-------------------------------------------------------------
            // Retrieve Items (Multi-Item Retrieve)
            //-------------------------------------------------------------
            // Retrieve the items, specifying Retrieval Options.
            // For more information on retrieve options, see header documentation.
            System.out.println("Retrieving Items (Multi-Item Retrieve)...");
            
                // Create Retrieve Options and Make Selections
                DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
                dkRetrieveOptions.baseAttributes(true);
                
                // Call Multi-Item Retrieve
                dsICM.retrieveObjects(ddoColl,dkRetrieveOptions.dkNVPair());      

            System.out.println("Retrieved Items (Multi-Item Retrieve).");

            //-------------------------------------------------------------
            // Retrieve Items (Single-Item Retrieve)
            //-------------------------------------------------------------
            // Use multi-item retrieve whenever possible when you have more
            // than one DDO to retrieve.  However, for demonstration
            // purposes, the following demonstrates single-item retrieve.
            //
            // You can reuse the same retrieve options object instance
            // instead of creating a new one for every call.  However,
            // remember to set undesired options to 'false' since they do
            // not revert to their defaults between uses.
            System.out.println("Retrieving Items (Single-Item Retrieve)...");
            
                // You can reuse an existing DKRetrieveOptionsICM object.
                // This example uses the same settings with baseAttributes(true)
                ddoDocument.retrieve(dkRetrieveOptions.dkNVPair());
                
                // This time retrieve a folder with attributes and folder contents (outbound links)
                dkRetrieveOptions.baseAttributes(true);
                dkRetrieveOptions.linksOutbound(true);
                dkRetrieveOptions.linksTypeFilter(DKConstantICM.DK_ICM_LINKTYPENAME_DKFOLDER);
                
                ddoFolder.retrieve(dkRetrieveOptions.dkNVPair());
                
                // This time retrieve all levels of children and reset the links
                // request to false
                dkRetrieveOptions.baseAttributes(true);
                dkRetrieveOptions.childListOneLevel(true);
                dkRetrieveOptions.childListAllLevels(true);
                dkRetrieveOptions.childAttributes(true);
                dkRetrieveOptions.linksOutbound(false);         // But suppose we do not want links which was set to 'true' earlier.
                           
                ddoItem.retrieve(dkRetrieveOptions.dkNVPair());

            System.out.println("Retrieved Items (Single-Item Retrieve).");
            
            //-------------------------------------------------------------
            // Reading Attribute Values, Knowing Type
            //-------------------------------------------------------------
            // If the type of each attribute is known by the user program, the returned
            // java.lang.Object values can be cast into the correct value.
            System.out.println("Reading Attribute Values, Knowing Type...");

                String  attrVal1 = (String) ddoItem.getData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_varchar")); 
                Integer attrVal2 = (Integer)ddoItem.getData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_integer")); 
                Double  attrVal3 = (Double) ddoItem.getData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_double")); 

                System.out.println("Attr 'S_varchar' value: "+attrVal1);
                System.out.println("Attr 'S_integer' value: "+attrVal2);
                System.out.println("Attr 'S_double'  value: "+attrVal3);

            System.out.println("Read Attribute Values, Knowing Type.");

            //-------------------------------------------------------------
            // Read / Access Attribute Groups
            //-------------------------------------------------------------
            // As explained in both SAttributeGroupDefCreationICM and in
            // the Set / Modify Attributes portion of SItemCreationICM,
            // the correct format for accessing Attributes Belonging to
            // Attribute Groups is: Format:  <Attribute Group Name>.<Attribute Name>
            System.out.println("Read / Access Attribute Groups...");

                String  attrVal4 = (String) ddoItem.getData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_street")); 
                String  attrVal5 = (String) ddoItem.getData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_city")); 
                String  attrVal6 = (String) ddoItem.getData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_state")); 
                String  attrVal7 = (String) ddoItem.getData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_zipCode")); 

                System.out.println("Group 'S_address', Attr 'S_street' value: "+attrVal4);
                System.out.println("Group 'S_address', Attr 'S_city'   value: "+attrVal5);
                System.out.println("Group 'S_address', Attr 'S_state'  value: "+attrVal6);
                System.out.println("Group 'S_address', Attr 'S_zipCode value: "+attrVal7);

            System.out.println("Read / Accessed Attribute Groups.");

            //-------------------------------------------------------------
            // Read / Access Reference Attributes
            //-------------------------------------------------------------
            // As explained in SItemCreationICM & SReferenceAttrDefICM, usage
            // of Reference Attributes is just like regurlar Attributes.  A
            // DKDDO value is contained.  The system will manage everything.
            System.out.println("Read / Access Reference Attributes...");

                DKDDO ddo = (DKDDO) ddoItem.getData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_myRef")); 

                if(ddo!=null){
                    System.out.println("             Reference:     Type:  "+ddo.getObjectType());
                    System.out.println("                         Item ID:  "+ ( (DKPidICM)ddo.getPidObject() ).getItemId());
                }
                else
                    System.out.println("             Reference:  null");

            System.out.println("Read / Accessed Reference Attributes.");

            //-------------------------------------------------------------
            // Reading Attribute Values Programmatically: All Data Items
            //-------------------------------------------------------------
            // The type of attribute may be determined Programmatically.  A
            // mechanism is built in, or Java's "instanceof" operator may be
            // used. 
            //
            // All data items can be listed, which will obtain more than
            // the user-defined attributes.  It will find all data members
            // of the DDO.
            System.out.println("Reading All DDO Data Items Programmatically...");

                System.out.println("Number of Attributes / Data Items: "+ddoItem.dataCount());
                for(short dataid=1; dataid<=ddoItem.dataCount(); dataid++) { // go through all attributes in the ddo
                    String name  = ddoItem.getDataName(dataid);
                    Object value = ddoItem.getData(dataid);
                    Short  type  = (Short) ddoItem.getDataPropertyByName(dataid,DKConstant.DK_CM_PROPERTY_TYPE); 
                    String typeDescription = "To Be Determined...";
                    switch(type.intValue()){
                        case DKConstant.DK_CM_DATAITEM_TYPE_UNDEFINED:
                            typeDescription = "Undefined";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_STRING:
                            typeDescription = "String";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_SHORT:
                            typeDescription = "Short";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_LONG:
                            typeDescription = "Long";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_FLOAT:
                            typeDescription = "Float";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_DECIMAL:
                            typeDescription = "Decimal";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_DATE:
                            typeDescription = "Date";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_TIME:
                            typeDescription = "Time";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_TIMESTAMP:
                            typeDescription = "Timestamp";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_DOUBLE:
                            typeDescription = "Double";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_BYTEARRAY:
                            typeDescription = "Byte Array";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_DDOOBJECT:
                            typeDescription = "DDO";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_XDOOBJECT:
                            typeDescription = "XDO";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_DATAOBJECTBASE:
                            typeDescription = "Data Object Base";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION:
                            typeDescription = "Collection";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                            typeDescription = "DDO Collection";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_XDO:  
                            typeDescription = "XDO Colleciton";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_LINKCOLLECTION:
                            typeDescription = "DKLinkCollection";
                            break;
                        case DKConstant.DK_CM_DATAITEM_TYPE_ARRAY:
                            typeDescription = "Array";
                            break;
                        default:
                            typeDescription = "Unknown ("+type+")";
                            break;
                    }// end swith on data item type
                    
                    if(value!=null)
                        System.out.println("Data Item '"+name+"' of type '"+typeDescription+"' with value: "+value);
                    else
                        System.out.println("Data Item '"+name+"' of type '"+typeDescription+"' with value: null");
                }// end for all data items in DDO

            System.out.println("Read All DDO Data Items Programmatically.");

            //-------------------------------------------------------------
            // Access Child Components
            //-------------------------------------------------------------
            System.out.println("Accessing Child Components...");

                // First, get the child colleciton within the parent for the child object type.
                short dataid = ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_CHILD,"S_simpleChild");
                DKChildCollection children = (DKChildCollection) ddoItem.getData(dataid);

                if(children!=null)
                    System.out.println("Found '"+children.cardinality()+"' Children of type 'S_address'");
                else
                    System.out.println("Found 'null' Children of type 'S_simpleChild'");

            System.out.println("Accessed Child Components.");
            
            //-------------------------------------------------------------
            // Print DDO & Child components
            //-------------------------------------------------------------
            // Print a DDO using the wrapper at the bottom of this sample.            
            System.out.println("Printing DDO...");
                printDDO(ddoItem); // Use wrapper defined at bottom of sample.
            System.out.println("Printed DDO.");

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
            throw(exc);
        } catch (Exception exc) {
            SConnectDisconnectICM.printException(exc);  // Print the exception using the function listed below.
            throw(exc);
        }
    }// end main

    //=================================================================
    // Wrapper Functions
    //=================================================================
    // The following are wrapper functions for functionality covered
    // in this sample.  These functions can be used by other samples.

   /**
    * Print the specified DDO.
    * @param ddo  DKDDO to print.
    **/
    public static void printDDO(DKDDO ddo) throws DKException, Exception{
    
        if(ddo==null){  // If NULL, print NULL information.
            System.out.println("    *** DDO is 'null' *** ");
            return;
        }
    
        java.util.ArrayList<DKChildCollection> childCollections = new java.util.ArrayList<DKChildCollection>(); // when obtaining data items, collect child collections.
        java.util.ArrayList<DKLinkCollection>  linkCollections  = new java.util.ArrayList<DKLinkCollection>();  // when obtaining data items, collect link collections.

        DKPidICM pid = (DKPidICM) ddo.getPidObject(); // Obtain the Pid Information
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) ddo.getDatastore().datastoreDef(); // Get the datastore definition object from the connected datastore.
        DKComponentTypeDefICM compType = (DKComponentTypeDefICM) dsDefICM.retrieveComponentTypeView(pid.getObjectType());
        
        System.out.println("--------------------------------------------------");
        System.out.println("                       DDO");
        System.out.println("--------------------------------------------------");
        System.out.println("PID:");
        System.out.println("        Component Type:  "+pid.getObjectType());
        System.out.println("               Item ID:  "+pid.getItemId());
        System.out.println("          Component ID:  "+pid.getComponentId());
        System.out.println("     Component Type ID:  "+pid.getComponentTypeId());
        System.out.println("            Primary ID:  "+pid.getPrimaryId());
        System.out.println("        Datastore Type:  "+pid.getDatastoreType());
        System.out.println("               Version:  "+pid.getVersionNumber());
        System.out.println("Item Info:");
        if(compType.isRoot()==false){ // if it isn't the root
            if(ddo.getParent()==null) // if no parent is specified and it is a child, this means that this DDO was retrieved separately from the parent and therefore the parent is not known to this object in memory.  Obtaining the parent and retrieving "One Level" or "Item Tree" would set this property.  Alternatively, query/search may be used to find the parent, as shown in the retrieveParent() function provided in this sample.  Refer to Web Based Technical Support (WBTS) document #1113777 for DB2 Information Integrator for Content (package that APIs officially belong to available with DB2 Content Manager) for more information.
                System.out.println("                Parent:  Exists, But Unknown to Object in Memory");
            else{
                System.out.println("                Parent:     Type:  "+ddo.getParent().getObjectType());
                System.out.println("                         Item ID:  "+((DKPidICM)((DKDDO)ddo.getParent()).getPidObject()).getItemId());
            }
        }else
            System.out.println("                Parent:  None, Is Root");
        if(ddo instanceof dkResource){
            if(ddo instanceof DKTextICM)
                System.out.println("          Resource XDO:  Yes - Text");
            else if(ddo instanceof DKImageICM)
                System.out.println("          Resource XDO:  Yes - Image");
            else if(ddo instanceof DKVideoStreamICM)
                System.out.println("          Resource XDO:  Yes - Video Stream");
            else if(ddo instanceof DKStreamICM)
                System.out.println("          Resource XDO:  Yes - Stream");
            else if(ddo instanceof DKLobICM)
                System.out.println("          Resource XDO:  Yes - Lob");
            else
                System.out.println("          Resource XDO:  Yes - dkResource");
        }
        else
            System.out.println("          Resource XDO:  No");
        
        if(compType.isRoot()){ // only applicable to roots
            short propIdSemanticType     = ddo.propertyId(DKConstantICM.DK_ICM_PROPERTY_SEMANTIC_TYPE);
            short propIdItemPropertyType = ddo.propertyId(DKConstantICM.DK_CM_PROPERTY_ITEM_TYPE);
            int semanticType     = -2;
            int itemPropertyType = -2;
            if(propIdSemanticType>0)     semanticType     = ((Integer) ddo.getProperty(propIdSemanticType)).intValue();
            if(propIdItemPropertyType>0) itemPropertyType = ((Short)   ddo.getProperty(propIdItemPropertyType)).shortValue();

            switch(itemPropertyType){
                case DKConstant.DK_CM_DOCUMENT:
                    System.out.println("         Item Property:  Document");
                    break;
                case DKConstant.DK_CM_FOLDER:
                    System.out.println("         Item Property:  Folder");
                    break;
                case DKConstant.DK_CM_ITEM:
                    System.out.println("         Item Property:  Item");
                    break;
                case -2:
                    System.out.println("         Item Property:  <not retrieved>");
                    break;
                default:
                    System.out.println("         Item Property:  Other ("+itemPropertyType+")");
                    break;
            }//end switch(itemPropertyType)
            switch(semanticType){
                case DKConstantICM.DK_ICM_SEMANTIC_TYPE_DOCUMENT:
                    System.out.println("         Semantic Type:  Document");
                    break;
                case DKConstantICM.DK_ICM_SEMANTIC_TYPE_FOLDER:
                    System.out.println("         Semantic Type:  Folder");
                    break;
                case DKConstantICM.DK_ICM_SEMANTIC_TYPE_CONTAINER:
                    System.out.println("         Semantic Type:  Folder");
                    break;
                case DKConstantICM.DK_ICM_SEMANTIC_TYPE_ANNOTATION:
                    System.out.println("         Semantic Type:  Annotation");
                    break;
                case DKConstantICM.DK_ICM_SEMANTIC_TYPE_HISTORY:
                    System.out.println("         Semantic Type:  History");
                    break;
                case DKConstantICM.DK_ICM_SEMANTIC_TYPE_NOTE:
                    System.out.println("         Semantic Type:  Note");
                    break;
                case DKConstantICM.DK_ICM_SEMANTIC_TYPE_BASE:
                    System.out.println("         Semantic Type:  Base");
                    break;
                case -2:
                    System.out.println("         Semantic Type:  <not retrieved>");
                    break;
                default:
                    System.out.println("         Semantic Type:  Other ("+semanticType+")");
                    break;
            }//end switch(semanticType)
        }// end if is root

        System.out.println("Properties:");
        System.out.println("                Number:  "+ ddo.propertyCount());
        // List all Properties
        for(short propid=1; propid<=ddo.propertyCount(); propid++) {
            String name  = ddo.getPropertyName(propid);
            Object value = ddo.getProperty(propid);
            System.out.println("              Property:     Name:  "+name);
            System.out.println("                           Value:  "+obj2String(value));
        }//end for(short propid=1; propid<=ddo.propertyCount(); propid++) {

        System.out.println("Attributes / Data Items:");
        System.out.println("                Number:  "+ ddo.dataCount());
        // List all Data Items
        for(short dataid=1; dataid<=ddo.dataCount(); dataid++) { // go through all attributes in the ddo
            System.out.println("                  Data:     Name:  "+ddo.getDataName(dataid)+"  (" + ddo.getDataNameSpace(dataid) + ")");
            String name   = ddo.getDataName(dataid);
            String namesp = ddo.getDataNameSpace(dataid);
            Object value  = ddo.getData(dataid);
            Short  type   = (Short) ddo.getDataPropertyByName(dataid, DKConstant.DK_CM_PROPERTY_TYPE);
            switch(type.intValue()){
                case DKConstant.DK_CM_DATAITEM_TYPE_UNDEFINED:
                    System.out.println("                            Type:  Undefined");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_STRING:
                    System.out.println("                            Type:  String");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_SHORT:
                    System.out.println("                            Type:  Short");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_LONG:
                    System.out.println("                            Type:  Long");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_FLOAT:
                    System.out.println("                            Type:  Float");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_DECIMAL:
                    System.out.println("                            Type:  Decimal");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_DATE:
                    System.out.println("                            Type:  Date");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_TIME:
                    System.out.println("                            Type:  Time");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_TIMESTAMP:
                    System.out.println("                            Type:  Timestamp");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_DOUBLE:
                    System.out.println("                            Type:  Double");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_BYTEARRAY:
                    System.out.println("                            Type:  Byte Array (Blob)");
                    if(value!=null) 
                        System.out.println("                           Value:  <Byte Array Object>");
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_DDOOBJECT:
                    System.out.println("                            Type:  DDO");
                    if(value!=null){ 
                        System.out.println("                           Value:  DDO of type '"+((DKDDO)value).getPidObject().getObjectType()+"'");
                        System.out.println("                                       Item ID '"+((DKPidICM)((DKDDO)value).getPidObject()).getItemId()+"'");
                    }
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_XDOOBJECT:
                    System.out.println("                            Type:  XDO");
                    if(value!=null){
                        System.out.println("                           Value:  XDO of type '"+((DKDDO)value).getPidObject().getObjectType()+"'");
                        System.out.println("                                       Item ID '"+((DKPidICM)((DKDDO)value).getPidObject()).getItemId()+"'");
                    }
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_DATAOBJECTBASE:
                    System.out.println("                            Type:  Data Object Base");
                    if(value!=null) 
                        System.out.println("                           Value:  <Data Object Base Object>");
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION:
                    System.out.println("                            Type:  Collection");
                    if(value!=null) 
                        System.out.println("                           Value:  Collection with '"+((dkCollection)value).cardinality()+"' Objects.");
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_DDO:
                    System.out.println("                            Type:  DDO Collection");
                    if(value!=null){
                        System.out.println("                           Value:  DDO Collection with '"+((dkCollection)value).cardinality()+"' DDOs.");
                        if(value instanceof DKChildCollection) // if it is a child collection, add it to a list for printing children later
                            childCollections.add((DKChildCollection)value);
                    }
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_COLLECTION_XDO:  
                    System.out.println("                            Type:  XDO Collection");
                    if(value!=null)
                        System.out.println("                           Value:  XDO Collection with '"+((dkCollection)value).cardinality()+"' XDOs.");
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_LINKCOLLECTION:
                    System.out.println("                            Type:  Link Collection");
                    if(value!=null){
                        System.out.println("                           Value:  Link Collection with '"+((dkCollection)value).cardinality()+"' Links.");
                        if(value instanceof DKLinkCollection) // if it is a link collection, add it to a list for printing Links later
                            linkCollections.add((DKLinkCollection)value);
                    }
                    break;
                case DKConstant.DK_CM_DATAITEM_TYPE_ARRAY:
                    System.out.println("                            Type:  Array");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
                default:
                    System.out.println("                            Type:  Unknown ("+type+")");
                    if(value!=null) 
                        System.out.println("                           Value:  "+value);
                    break;
            }// end swith on data item type

            if(value==null) 
                    System.out.println("                           Value:  null");
            System.out.println("");
        }// end for all data items in DDO

        // Print Children (Brief)
        System.out.println("Children / Multivalue Attributes:");
        for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
            DKChildCollection childCollection = (DKChildCollection) childCollections.get(i); // get each child collection
            dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
            while(iter.more()){ // While there are children, print list
                DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                System.out.println("                 Child:     Type:  "+child.getPidObject().getObjectType());
                System.out.println("                         Item ID:  "+((DKPidICM)child.getPidObject()).getItemId());
                System.out.println("");
            }
        }
            
        // Print Parts, if a Parts Collection Exists (See Document Model Samples for more information)
        short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS);
        if(dataid > 0){ // if a DKParts collection exists, print contents
            DKParts dkParts = (DKParts) ddo.getData(dataid); // obtain the DKParts collection
            System.out.println("Parts:");
            System.out.println("                Number:  "+dkParts.cardinality());
            dkIterator iter = dkParts.createIterator(); // Create an iterator to go through Collection
            while(iter.more()){ // While there are children, print list
                DKDDO part = (DKDDO)iter.next(); // Move pointer to next part & return that object.
                System.out.println("                  Part:     Type:  "+part.getPidObject().getObjectType());
                System.out.println("                         Item ID:  "+((DKPidICM)part.getPidObject()).getItemId());
                System.out.println("");
            }
        }

        // Print Folder Contents, if a DKFolder Collection Exists (See Folder Samples for more information)
        dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKFOLDER);
        if(dataid > 0){ // if a DKFolder collection exists, print contents
            DKFolder dkFolder = (DKFolder) ddo.getData(dataid); // obtain the DKFolder collection
            System.out.println("Folder Contents:");
            System.out.println("                Number: "+dkFolder.cardinality());
            dkIterator iter = dkFolder.createIterator(); // Create an iterator to go through Collection
            while(iter.more()){ // While there are children, print list
                DKDDO contents = (DKDDO)iter.next(); // Move pointer to next element & return that object.
                System.out.println("                  Item:     Type:  "+contents.getPidObject().getObjectType());
                System.out.println("                         Item ID:  "+((DKPidICM)contents.getPidObject()).getItemId());
                System.out.println("");
            }
        }
        
        // Print Links
        System.out.println("Links:");
        for(int i=0; i<linkCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
            DKLinkCollection linkCollection = linkCollections.get(i); // get each Link Collection
            dkIterator iter = linkCollection.createIterator(); // Create an iterator to go through Link Collection
            while(iter.more()){ // While there are links, print list
                DKLink dkLink = (DKLink)iter.next(); // Move pointer to next element & return that object.
                System.out.println("                  Link:     Type:  "+dkLink.getTypeName());
                System.out.println("                       Source ID:  "+((DKPidICM)((DKDDO)dkLink.getSource()).getPidObject()).getItemId()+"("+((DKDDO)dkLink.getSource()).getPidObject().getObjectType()+")");
                System.out.println("                       Target ID:  "+((DKPidICM)((DKDDO)dkLink.getTarget()).getPidObject()).getItemId()+"("+((DKDDO)dkLink.getTarget()).getPidObject().getObjectType()+")");
                if(dkLink.getLinkItem()!=null)
                    System.out.println("                     LinkItem ID:  "+((DKPidICM)((DKDDO)dkLink.getLinkItem()).getPidObject()).getObjectType());
                else
                    System.out.println("                     LinkItem ID:  null");
                System.out.println("");
            }
        }
        System.out.println("--------------------------------------------------------");
        System.out.println("");
        
        // Print Full Children
        for(int i=0; i<childCollections.size(); i++){  // go through all collection we gathered when listing the data items of the DDO.
            DKChildCollection childCollection = childCollections.get(i); // get each child collection
            dkIterator iter = childCollection.createIterator(); // Create an iterator to go through Child Collection
            while(iter.more()){ // While there are children, print each
                DKDDO child = (DKDDO) iter.next(); // Move pointer to next child & return that object.
                printDDO(child);
            }
        }
    }// end printDDO

   /**
    * Retrieve the parent DKDDO of the specified child component
    * if one exists.  If the parent reference is set in the 
    * child component, it will be returned from memory without
    * performing a search.  If the DKDDO is discovered to be
    * a root component, NULL will be returned before searching.
    * The following query will be executed if this is a child
    * component.  
    * 
    * //A[@ITEMID="B" AND (./C[@COMPONENTID="D" AND @VERSIONID=E])]
    * A:  Parent Component View Type Name or *.
    * B:  Item ID (All components of the same item share the same Item ID).
    * C:  Child Component View Type Name.
    * D:  Child Component ID.
    * E:  Version ID (All components of the same item version share the same version ID). 
    *
    * Faster solution exist.  This function covers one alternative
    * to retrieve the parent when DKDDO.getParent() returns 'null'.
    * Please refer to Web Based Technical Support (WBTS) document #1113777
    * for package DB2 Information Integrator for Content for more information
    * on obtaining the parent of a child component.
    *
    * @param ddo - DDO to get the parent of.
    * @return Returns the parent with completed PID information or
    *         NULL if no parent exists.
    **/
    public static DKDDO retrieveParent(DKDDO ddo) throws DKException, Exception{

        System.out.println("Retrieving Parent DDO...");

        // First Check if Parent Reference is Set
        DKDDO parentDDO = (DKDDO) ddo.getParent();
        if(parentDDO!=null){ // If it was set, return it.
            System.out.println("Parent reference was set in memory.  Returned local reference.");
            return(parentDDO);   
        }

        // Gether Information
        DKPidICM pid = (DKPidICM) ddo.getPidObject();
        String itemId        = pid.getItemId();        // B
        String childViewName = pid.getObjectType();    // C
        String compId        = pid.getComponentId();   // D
        String verId         = pid.getVersionNumber(); // E

        // Next, check if it is a root component.
        DKDatastoreICM    dsICM    = (DKDatastoreICM)    ddo.getDatastore();
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
        DKComponentTypeViewDefICM childViewDef = (DKComponentTypeViewDefICM) dsDefICM.retrieveComponentTypeView(childViewName);
        if(childViewDef.isRoot()){ // If it is a root component, return 'null'.
            System.out.println("Type '"+childViewName+"' determined to be a root component.  Returning 'null' since no parent can exist.");
            return(null);
        }
        
        // Look up parent component view type name.
        DKComponentTypeViewDefICM parentViewDef = (DKComponentTypeViewDefICM) childViewDef.retrieveParent();
        String parentViewName = parentViewDef.getName(); // A

        // Build Query String
        StringBuffer query = new StringBuffer();
        query.append("//");
        query.append(parentViewName);
        query.append("[@ITEMID=\"");
        query.append(itemId);
        query.append("\" AND (./");
        query.append(childViewName);
        query.append("[@COMPONENTID=\"");
        query.append(compId);
        query.append("\" AND @VERSIONID=");
        query.append(verId);
        query.append("])]");
            
        // Build Options
        DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
        DKNVPair[] options = new DKNVPair[3];
        options[0]         = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "1");                                            // Specify max using a string value.
        options[1]         = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    dkRetrieveOptions);
        options[2]         = new DKNVPair(DKConstant.DK_CM_PARM_END,         null);                                           // Must mark the end of the NVPair

        // Process Query
        System.out.println("Evaluating Query:  "+query.toString());
        DKResults results = (DKResults)dsICM.evaluate(query.toString(), DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
            
        // Return Parent
        dkIterator iter = results.createIterator();
        parentDDO       = (DKDDO) iter.next();
        
        if(parentDDO!=null) System.out.println("Parent DDO found:  Type = '"+parentDDO.getObjectType()+"', CompID = '"+((DKPidICM)parentDDO.getPidObject()).getComponentId()+"'");
        else                System.out.println("No parent found.");
        
        return(parentDDO);
    }//end retrieveParent

   /**
    * Use for quick & easy printing an Object to show its type and value in a simple
    * concise format.  This method returns a concise, meaninful string for many Object
    * instance returned from a DDO for quick demonstration purposes.  This method
    * provides more meaninful detail than many of the toString() method.
    * @param object  Object to check for quick, meaningful string conversion printing
    * @Return  Returns a quick, concise string representing the object type and value.
    **/
    public static String obj2String(Object object){
          if(object == null)
            return ("NULL");
          else if(object.getClass().getName().compareTo(String.class.getName()) == 0)
            return ((String) object);
          else if(object.getClass().getName().compareTo(DKFolder.class.getName()) == 0)
            return ("DKFolder[Size: " + ((DKFolder) object).cardinality() + "]");
          else if(object.getClass().getName().compareTo(DKLinkCollection.class.getName()) == 0)
            return ("DKLinkCollection[Size: " + ((DKLinkCollection) object).cardinality() + "]");
          else if(object.getClass().getName().compareTo(DKChildCollection.class.getName()) == 0)
            return ("DKChildeCollection[Size: " + ((DKChildCollection) object).cardinality() + "]");
          else if(object.getClass().getName().compareTo(DKParts.class.getName()) == 0)
              return ("DKParts[Size: " + ((DKParts) object).cardinality() + "]");
          else if(object.getClass().getName().compareTo(DKSequentialCollection.class.getName()) == 0)
            return ("DKSequentialCollection[Size: " + ((DKSequentialCollection) object).cardinality() + "]");
          else if(object instanceof dkCollection)
            return ("dkCollection[Size: " + ((dkCollection) object).cardinality() + "]");
          else if(object.getClass().getName().compareTo(java.util.Vector.class.getName()) == 0)
            return ("Vector[Size: " + ((java.util.Vector) object).size() + "]");
          else if(object.getClass().getName().compareTo(java.util.ArrayList.class.getName()) == 0)
            return ("ArrayList[Size: " + ((java.util.ArrayList) object).size() + "]");
          else if(object.getClass().getName().compareTo(DKNVPair[].class.getName()) == 0)
              return ("DKNVPair[" + ((DKNVPair[]) object).length + "]");
          else if(object.getClass().getName().compareTo(DKNVPair.class.getName()) == 0)
              return ("DKNVPair{\"" + obj2String(((DKNVPair)object).getName()) + "\" = '"+obj2String(((DKNVPair)object).getValue())+"'}");
          else if(object.getClass().getName().compareTo(DKDatastoreICM.class.getName()) ==0){
            try{
              return ("DKDatastoreICM[name='"+((DKDatastoreICM)object).datastoreName()+"',type='"+((DKDatastoreICM)object).datastoreType()+"',more="+(DKDatastoreICM)object+"]");
            }catch(Exception exc){
              return ("DKDatastoreICM("+(DKDatastoreICM)object+")");
            }
          }else if(object.getClass().getName().compareTo(DKRetrieveOptionsICM.class.getName()) == 0)
              return (object.toString());
          else if(object.getClass().getName().compareTo(DKProjectionListICM.class.getName()) == 0)
              return (object.toString());
          else
          {
            return (object.getClass().getName() + ' ' + object.toString());
          }
    }
        
}//end class SItemRetrievalICM
              
