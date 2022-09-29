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

Pre-requisit Informaiton:
    For information on any of the following in terms of definition creation,
    accessing, listing, printing, updating, or deleting, please refer to the
    SItemTypeCreationICM Sample.
        - Datamodeling Concepts
        - Child Component Explanation
        - Multivalue Attribute Explanation
        - Item Type Classifications & Explanations
        - Resource XDO Classes
        - Item Types that store Resource Content
        - Delete Rules
        - Sample Data Model:  Insurance Model
        - +++
    
    This sample will assume knowledge and understanding of the concepts above and those
    outlined in the headers, wrappers, and functions in the pre-requisit samples:
        - SConnectDisconnectICM                  - SReferenceAttrDefCreationICM
        - SAttributeDefinitionCreationICM        - SReferenceAttrDefRetrievalICM	 
        - SAttributeDefinitionRetrievalICM       - SReferenceAttrDefUpdateICM
        - SAttributeDefinitionUpdateICM          - SReferenceAttrDefDeletionICM 
        - SAttributeDefinitionDeletionICM        - SItemTypeCreationICM
        - SAttributeGroupDefCreationICM          - SItemTypeRetrievalICM 
        - SAttributeGroupDefRetrievalICM         - SItemTypeUpdateICM  
        - SAttributeGroupDefUpdateICM            - SItemTypeDeletionICM   
        - SAttributeGroupDefDeletionICM    

Definitions:
    
    - "Items" refer in general to an instance of any Item Type.  
    - "Components" make up the hierarchial tree of data that makes up an Item.  
      Components are also the instances of multivalue attributes.  Each component is
      one value of a multi-value attribute.
    - "Root Component" refers to the highest level or parent DDO from which all child
      components are derived.
    - "Child" or "Child Component" refers to a sub-component of a parent.
    - "DDO", or "Dynamic Data Object" refers to an instance of the DKDDO class object
      which make up one Component (including Root Components).
    - "XDO", or "Extended Data Object" extends DDO objects for Resource Functionality.

Creating Items
    Items area created as DKDDOs.  DKDDOs should always be created using the 
    DKDatastoreICM's createDDO() methods.  Using the DKDatastoreICM's createDDO methods
    allow the system to automatically set up important information in the DKDDO structure 
    and which allows for easier & greater functionality such as Resources, 
    Document Model, & Folders.  Creating DDOs through the DKDatastoreICM is the 
    official and recommended way to create items.  Creating DDOs using "new" or
    any means of instantiating a DDO instance directly from a DDO class is *not*
    recommended.    

    Recommended CreateDDO Methods:
        DKDatastoreICM::createDDO(String itemTypeViewName, int semanticType)
        DKDatastoreICM::createChildDDO(String itemTypeView, String childTypeView)
        DKDatastoreICM::createDDOFromPID(DKPidICM pidObject)
        DKDatastoreICM::createDDOFromPID(String   pidString)

        You should learn when to use each of these methods.  Each of these methods
        are described in more detail in the Application Programming Reference (Javadoc).
        The first two are demonstrated in this sample and the last two are demonstrated
        in the SItemRetrievalICM sample.

    DKDatastoreICM.createDDO(<Overall Item Type>, <Item Property Type / Semantic Type>):
        Overall Item Type (Parameter 1):
            The first parameter takes the Overall Item Type, defining the entire Item.  
            This may also be considered the Root Item Type, since the DDO returned is
            the Root Component DDO.
        Item Property / Semantic Type (Parameter 2):
            The second parameter determines the Item Property & Semantic Type of the DDO, 
            such as Document, Folder, Item, or User-Defined.  For more information, 
            refer to the Item Property & Semantic Type sections below.
        Returned:
            The DDO returned is a new DKDDO of the Root Component.
    
    Resource Items
        The Item Type Definition determines what type of DDO is created.  If it is 
        a resource, the correct XDO will be returned, depending on the XDO Class 
        specified in the Item Type Definition.  The returned DDO may be cast to the
        correct structure, since will be set up correctly according to the Item Type
        Definition.  
        
        Please refer to the SItemTypeCreationICM sample for more information on Resource
        Classification, & XDO Classes.
    
    Item Properties / Item Type Properties
        An Item or DDO must have a generic "type" property associated with it.  This
        generic type of the DDO is Document, Folder, or Item.  When creating Items,
        the Item Property is specified through the second parameter of the datastore's
        createDDO function mixed in with the semantic type.  You submit only one value,
        but the Item Property and Semantic Type values are derived and set separately
        in the DDO.  The Item Property types are a subset of Semantic Types.  Be sure
        to read and understand the "Semantic Type" section as well.  

        This value is stored in the DDO's property named, "DK_CM_PROPERTY_ITEM_TYPE",
        which should not be confused with the overall Item Type Definition that
        describes this the structure of this Item.  "PROPERTY_ITEM_TYPE" refers to
        the "Item Property", not the Item Type Definition which fully describes the
        item structure and settings.

        Available Item Property Types:

        Item Property   Constant        Definition  
        -------------   --------------  ------------------------------------------------------
        Document        DK_CM_DOCUMENT  Item generically represents a document.
                                        This can be considered a "common document" since it
                                        does not rigidly mean an implementation of a specific
                                        document model.  This does not necessarily mean a 
                                        document with parts using the DB2 Content Manager V8
                                        "document model" which is instead controlled by the
                                        "document" classification in the item type definition.
        ......................................................................................
        Folder          DK_CM_FOLDER    Item represents a folder that can contain other items.
                                        This item features a built-in DKFolder collection
                                        which can hold folder contents which form folder links
                                        to other items.  An empty DKFolder collection is
                                        automatically added.  All items with this value are
                                        capable of utilizing the DKFolder mechanism. However,
                                        this value does not necessarily mean that a solution
                                        utilizes the DKFolder mechanism and could instead
                                        implement its own folder solution.  This value means
                                        any interpretation of a folder, but the built-in
                                        DKFolder is available to such items.  Note that you
                                        can create non-folder links and references from
                                        items of any semantic type or item property type.
        ......................................................................................
        Item (Default)  DK_CM_ITEM      Generic item.  This is the Item Property Type for all
                                        Semantic Types that are not Document or Folder.
        --------------------------------------------------------------------------------------
        Note:  Constants available in com.ibm.mm.sdk.common.DKConstant.

	Semantic Type
        When creating items, a semantic type can be specified as the second parameter
        of the datastore's createDDO function.  A semantic type in general is a
        metaphor for an item.  Semantic types are primarily a feature for application
        use and are typically not enforced or especially meaningful within the DB2
        Content Manager server.  This gives items an enforceable characteristic for
        some basic operations and provides applications with the ability to apply a
        semantic.  Where applicable, semantic types are enforced within DB2 Content
        Manager, but primarily the only semantic type that has any meaning,
        validation, or enforcement is FOLDER.

        WARNING
        Be careful of relying on semantic type values if your application reads items
        created by other applications or tools since the meaning and enforcement can
        vary among applications.  For example, do not rely on the various part semantic
        types since parts are allowed to be set with any semantic type value.  Instead
        rely first on the PID's object type which identifies the exact item type which
        you might recognize or you can retrieve the definition to tell you more, such as
        classification as a part, XDO class, etc.  You can rely on the semantic type
        as a secondary measure if you do not recognize the object type.

        The value is stored in the DDO's property DKConstantICM.DK_ICM_PROPERTY_SEMANTIC_TYPE,
        which in many cases may contain the same value as the Item Property Type.
	    
        Available Semantic Types:

        Semantic Type   Constant          Definition  
        -------------   ----------------  ------------------------------------------------------
        <Any>           <User Specified>  CreateDDO will assign any value specified to
                                          the semantic type value.  Since applications can
                                          enforce semantics as they wish, no values are
                                          disallowed, except for item types classified
                                          as "document" (document model).
        ........................................................................................
        Document        DK_ICM_SEMANTIC_  Item generically represents a document.
                        TYPE_DOCUMENT     This can be considered a "common document" since it
                                          does not rigidly mean an implementation of a specific
                                          document model.  This does not necessarily mean a 
                                          document with parts using the DB2 Content Manager V8
                                          "document model" which is instead controlled by the
                                          "document" classification in the item type definition.
                                          Equivalent to item property type DK_CM_DOCUMENT. 
        ........................................................................................
        Folder          DK_ICM_SEMANTIC_  Item represents a folder that can contain other items.
                        TYPE_FOLDER       This item features a built-in DKFolder collection
                                          which can hold folder contents which form folder links
                                          to other items.  An empty DKFolder collection is
                                          automatically added.  All items with this value are
                                          capable of utilizing the DKFolder mechanism. However,
                                          this value does not necessarily mean that a solution
                                          utilizes the DKFolder mechanism and could instead
                                          implement its own folder solution.  This value means
                                          any interpretation of a folder, but the built-in
                                          DKFolder is available to such items.  Note that you
                                          can create non-folder links and references from
                                          items of any semantic type or item property type.
                                          Equivalent to item property type DK_CM_FOLDER.
        ........................................................................................
        Container       DK_ICM_SEMANTIC_  Item represents a generic container or generic item.
                        TYPE_CONTAINER    Equivalent to item property type DK_CM_ITEM. Solutions
                                          can implement a containment relationship through any
                                          solution of their choice except for the built-in
                                          DKFolder mechanism (folder links).  You can use links
                                          of any other link type, reference attributes, or any
                                          other custom solution of your choice.
        ........................................................................................
        Base            DK_ICM_SEMANTIC_  Alleged to most nearly resemble document part of type
                        TYPE_BASE         "BASE" for use in the built-in document model where
                                          parts are created and added to make up a document.
                                          However, this value does *not* uniquely identify or
                                          make this a BASE part.  This value is not validated
                                          or enforced.  The item type of the part (identified
                                          in the PID's object type for the part DDO) is what
                                          makes this part a BASE part.  BASE parts can be 
                                          created with any semantic type of your choice.  Do set
                                          this semantic type for BASE parts, but when detecting
                                          part types, use the PID's object type first.  Use this
                                          value if you created your own custom part item types
                                          so that other applications that might not know your
                                          custom part type and could map it to the nearest
                                          similar part type that it understands.
        ........................................................................................
        Annotation      DK_ICM_SEMANTIC_  Same as BASE (See "Base" documentation for details)
                        TYPE_ANNOTATION   except alleged to most nearly resembles "ANNOTATION".
        ........................................................................................
        History         DK_ICM_SEMANTIC_  Same as BASE (See "Base" documentation for details)
                        TYPE_HISTORY      except alleged to most nearly resembles "HISTORY".
        ........................................................................................
        Note            DK_ICM_SEMANTIC_  Same as BASE (See "Base" documentation for details)
                        TYPE_NOTE         except alleged to most nearly resembles "NOTE".
        ........................................................................................
        <User Defined>  <User Defined>    A User Defined Semantic Type.  Please refer to
                                          the Semantic Type Samples for more information.
        ----------------------------------------------------------------------------------------
        Note:  Constants available in com.ibm.mm.sdk.common.DKConstantICM.

Creating Child Components
    Child components are created as DKDDOs.  For the same reasons as Creating Items
    explained above, the DKDatastoreICM.createChildDDO() function.  It requires two
    parameters, the overall Item Type Name that it will be a part of, and the specific
    Component Type Name.  

    DKDatastoreICM.createChildDDO(<Overall Item Type>, <Child Component Type>):
        Overall Item Type (Parameter 1):
            The first parameter takes the Overall Item Type that defines the entire Item.  
            This may also be considered the Root Item Type.  It does not refer to the
            Item or Component Type of the parent.  Child Component Types have unique names,
            which will allow the system to figure out where it fits in the overall hierarchy.
        Child Component Type (Parameter 2):
            The second parameter takes the specific component type of the child component 
            to create.
        Returned:
            The DDO returned is a new DKDDO / Component of the specified Component Type.

Setting / Modifying Attributes
    When setting individual attributes, use the individual attribute definition name.
    In order to access attributes that belong to an Attribute Group, the correct 
    attribute name follows the following format:
    
        Grouped Attribute's Name Format:  <Attribute Group Name>.<Attribute Name>
        
        For more information, please refer to the SAttributeGroupDefCreationICM Sample.

    Set the attributes with the value passed in & retrieved as a java.lang.Object.
    
    As stated in SAttributeDefinitionCreationICM, the following objects are used depending
    on the Attribute Type:
    
	Attr Type   Constant                    Object               Format (Java Only)
	----------- --------------------------- -------------------- --------------------------
	Blob        DKConstant.DK_CM_BLOB       byte bytes[]         n/a
	Char        DKConstant.DK_CM_CHAR       java.lang.String     n/a
	Clob        DKConstant.DK_CM_CLOB       java.lang.String     n/a
	Date        DKConstant.DK_CM_DATE       java.sql.Date        yyyy-mm-dd
	Decimal     DKConstant.DK_CM_DECIMAL    java.math.BigDecimal n/a
	Double      DKConstant.DK_CM_DOUBLE     java.lang.Double     n/a
	Integer     DKConstant.DK_CM_INTEGER    java.lang.Integer    n/a
	Short       DKConstant.DK_CM_SHORT      java.lang.Short      n/a
	Time        DKConstant.DK_CM_TIME       java.sql.Time	     hh:mm:ss
	Timestamp   DKConstant.DK_CM_TIMESTAMP  java.sql.Timestamp   yyyy-mm-dd hh:mm:ss.nnnnnn
	Varchar     DKConstant.DK_CM_VARCHAR    java.lang.String     n/a

Setting / Modifying Reference Attributes
    Reference Attributes appear in a DDO just as attributes do.  They are set, 
    modified, and accessed just as attributes are, not attribute groups.  In 
    this case, a DKDDO object is passed in or retrieved.

        Item Types
            Reference Attributes may not reference an Item of the same Item Type
            without restriction.  References are meant to reference Items of 
            different Item Types.  It is possible to reference an Item of the same
            Item Type, but setting & modifying the reference attribute must made
            on an item with all other changes already persistent, and modifications 
            to the reference attribute value only made persistent without other changes.
            
            Example Procedure For This Special Case:
                1. Create DDO A & B.
                2. Set any other values in A & B, including Adding Child Components.
                3. Add BOTH A & B to the datastore..
                4. Check Out / Lock A.
                5. Set A's Reference Attribute to B.
                6. Update A.

    Important:  At the time the Reference Attribute is set, the ddo it will reference must
                be persistent in the datastore.  This means that the DKDDO.add() operation
                has been completed on the DKDDO that it will reference, or it has been
                recreated given the full PIDs string (See SItemRetrievalICM Sample), or
                it has been retrieved through Retrieval or Query/Search.
    
    For a detailed explanation of Reference Attributes please refer to 
    SReferenceAttrDefCreationICM Sample.

Detecting Non-Existant Attributes in DDOs
    If an attribute does not exist in a DDO, either because it was not defined in the 
    Item / Component Type Definition, or because the DDO was not created through the 
    DKDatastoreICM.createDDO / createChildDDO function, the dataId() function will 
    return '0'.

Namespace
    Attribute names, Item / Component Type names, Link Type Name, etc., are in separate 
    namespaces.  Names only need to be unique among their own namespace.  Therefore, when
    using names, the correct namespace must be used when common methods are used.  The 
    Attribute Namespace is the default.

    Namespaces needed when using DDOs:  (Not included here are those already separated by
    the system.)

    Namespace   Constant
    ---------   --------------------------------
    Attribute   DKConstant.DK_CM_NAMESPACE_ATTR  (default)
    Link        DKConstant.DK_CM_NAMESPACE_LINK
    Child       DKConstant.DK_CM_NAMESPACE_CHILD

Adding / Saving New Items to the Persistent Datastore
    When new Items are created, they are only in local memory.  Once modification
    is complete, the entire item is added to the datastore through the root
    component / item.  Each child component should not be added individually, but
    instead simply the root item needs to be added.  The system will handle the rest.
    
    When modifying, updating, & deleting items, the root item / component should be
    used in all cases as a starting point for modification and end point for the add(),
    update(), and del() operations.

*******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import java.io.*;

/************************************************************************************************
 *          FILENAME: SItemCreationICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Creating Items, Item Property, Semantic Type, Namespace, Setting Attributes,
 *                    Setting Attribute Group Values, Creating Child Components / Multivalue
 *                    Attributes, Setting Reference Attributes.
 *                    ---------------------------------------------------------------------------
 *     DEMONSTRATION: Item Properties / Item Type Properties
 *                    Semantic Types
 *                    Creating a Simple Item
 *                    Creating Items of Different Item Properties
 *                    Namespace
 *                    Setting Attributes (All Attribute Types)
 *                    Setting Attributes that belong to Attribute Groups
 *                    Setting / Modifying Reference Attributes
 *                    Creating an Item with a child component / multi-value attribute
 *                    Creating an Item with multiple Children / Values for a Mult-value attribute
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java SItemCreationICM <database> <userName> <password>
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: The Data Model must be defined.  If needed please run the following Samples  
 *                        - SAttributeDefinitionCreationICM
 *                        - SAttributeGroupDefCreationICM
 *                        - SReferenceAttrDefCreationICM
 *                        - SItemTypeCreationICM
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 ************************************************************************************************/
public class SItemCreationICM{
    
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
            System.out.println("  java SItemCreationICM <database> <userName> <password>" );
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
        System.out.println("Sample Program:  SItemCreationICM");
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
            // Create Items / DDOs / Root Components
            //-------------------------------------------------------------
            // create 3 Items, one of Item Property type Document, one Folder, & one Item.
            // See the documentation in the header for more information on Item Properties and
            // Semantic Types.  This section will use the simple Item Type with a Child, 
            // S_withChild Item Type created in SItemTypeCreationICM.
            //
            // User-Defined Semantic Types may be substituted here.  The Item Property will
            // default to "Item".
            //
            // For explanations of the parameters of the createDDO function, refer to the
            // "Creating Items" section in the header of this sample.
            System.out.println("Creating Items / DDOs / Root Components...");

	            DKDDO ddoDocument = dsICM.createDDO("S_withChild", DKConstant.DK_CM_DOCUMENT); //createDDO(<Overall Item Type>, <Item Property / Semantic Type>);
	            DKDDO ddoFolder   = dsICM.createDDO("S_withChild", DKConstant.DK_CM_FOLDER);   //   For more information, refer to header of this sample.
	            DKDDO ddoItem     = dsICM.createDDO("S_withChild", DKConstant.DK_CM_ITEM);     // User-Defined Semantic Types may be substituted here.

            System.out.println("Created Items / DDOs / Root Components.");

            //-------------------------------------------------------------
            // Create child components / DDOs / Multivalue Attributes
            //-------------------------------------------------------------
            // Create a child of component type "S_simpleChild" for the "S_withChild" Item Type.
            //
            // As explained in the SItemTypeCreationICM Sample, Multivalue Attributes are created
            // through Child Components.  Multile Child components for a single Component Type
            // demonstrate the multiple values of a multi-value attribute.
            //
            // For explanations of the parameters of the createChildDDO function, refer to the
            // "Creating Child Components" section in the header of this sample.
            System.out.println("Creating child component / DDOs...");    

	            DKDDO ddoDocumentChild1 = dsICM.createChildDDO("S_withChild", "S_simpleChild");   // createChildDDO(<Overall Item Type>, <Child Component Type>);
	            DKDDO ddoDocumentChild2 = dsICM.createChildDDO("S_withChild", "S_simpleChild");   //    For more information, refer to header of this sample.
	            DKDDO ddoDocumentChild3 = dsICM.createChildDDO("S_withChild", "S_simpleChild");  

            System.out.println("Created child component / DDOs.");    

            //-------------------------------------------------------------
            // Adding Child Components / Multivalue Attributes to DDOs
            //-------------------------------------------------------------
            // Add the children created above to the Document DDO created in the first section.
            System.out.println("Adding Child Components to DDOs...");
            
                // First, get the child colleciton within the parent for the child object type.
                short dataid = ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_CHILD,ddoDocumentChild1.getObjectType());
                DKChildCollection children = (DKChildCollection) ddoDocument.getData(dataid);

                if(children==null){ // if no collection exists, create one.
                    children = new DKChildCollection();
                    ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_CHILD, ddoDocumentChild1.getObjectType()), children);
                }
        
                children.addElement(ddoDocumentChild1); // add children to the collection.  These children
                children.addElement(ddoDocumentChild2); // are all of the same object type, so they belong
                children.addElement(ddoDocumentChild3); // to the same children collection.

            System.out.println("Added Child Components to DDOs.");

            //-------------------------------------------------------------
            // Setting / Modifying Attributes
            //-------------------------------------------------------------
            // Set the attribute with the value passed in as a java.lang.Object.
            //
            // For a list of java.lang.Object subclasses to use depending on the attribute type,
            // please refer to the section in the header named "Setting / Modifying Attributes".
            //
            // Note that the Attribute Namespace is used.  Attribute names, Item / Component 
            // Type names, Link Type Name, etc., are in separate namespaces.  Names only need 
            // to be unique among their own namespace.  Therefore, when using names, the correct
            // namespace must be used when common methods are used.  For more info, see header.
            System.out.println("Setting / Modifying Attributes...");

                // Define byte for value to set in Blob attribute.
                byte picture_bytes[] = {(byte)19,(byte)18,(byte)17,(byte)255,(byte)16,(byte)15,(byte)14,(byte)13,(byte)12,(byte)11,(byte)0,(byte)110,(byte)122, (byte)164, (byte)0, (byte)127, (byte)128, (byte)255,(byte)1 };

                // Set Attribute Values
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_varchar"), "this is a string value"); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_blob"),    picture_bytes); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_char"),    "A");  // Char is specified with a String
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_clob"),    "Large amount of text can go here.  Size depends on attribute definition."); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_date"),    java.sql.Date.valueOf("2001-08-12")); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_decimal"), new java.math.BigDecimal("12.45")); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_double"),  new Double("123")); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_integer"), new Integer("123")); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_short"),   new Short("5"));
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_time"),    java.sql.Time.valueOf("10:00:00")); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_timestamp"),java.sql.Timestamp.valueOf("2001-08-12 10:00:00.123456")); 

            System.out.println("Set / Modified Attributes.");

            //-------------------------------------------------------------
            // Detecting Non-Existant Attributes in DDOs
            //-------------------------------------------------------------
            // If an attribute does not exist in a DDO, either because it was not defined
            // in the Item / Component Type Definition, or because it was not created through
            // the DKDatastoreICM.createDDO function, the dataId() function will return '0'.
            System.out.println("Detecting Non-Existant Attributes in DDOs...");

                // The Attribute "S_notInItem" was not defined for this Item Type.
                dataid = ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_notInItem");
                if(dataid == 0)
                    System.out.println("Attribute S_notInItem is not part of this DDO.");
                else
                    System.out.println("Attribute S_notInItem is part of this DDO.");
    
            System.out.println("Detected Non-Existant Attributes in DDOs.");

            //-------------------------------------------------------------
            // Setting / Modifying Attributes Belonging to Attribute Groups
            //-------------------------------------------------------------
            // When accessing attributes, use the individual attribute definition names. 
            // In order to access attributes that belong to a particular attribute group,
            // the correct attribute name follows the following format:
            //
            // Grouped Attribute's Name Format:  <Attribute Group Name>.<Attribute Name>
            System.out.println("Setting / Modifying Attributes Belonging to Attribute Groups...");

                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_street"), "123 First Street"); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_city"),   "Kingville"); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_state"),  "CA"); 
                ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_zipCode"),"12345-6789"); 

            System.out.println("Set / Modified Attributes Belonging to Attribute Groups.");

            //-------------------------------------------------------------
            // Add / Save items to persistent datastore
            //-------------------------------------------------------------
            // When all modification is complete, the items can be added 
            // to the persistent datastore, which can be later accessed
            // if the PID is known or through Query.  See Item Retrieval
            // Sample and Query sample for more information.
            //
            // Only the root items need to be added.  The system will take 
            // care of the rest.
            System.out.println("Adding / Saving Items to Persistent Datastore...");

                ddoDocument.add(); // Adds entire item tree, including all children
                ddoFolder.add();
                
            System.out.println("Added / Saved Items to Persistent Datastore...");

            //-------------------------------------------------------------
            // Setting / Modifying Reference Attributes
            //-------------------------------------------------------------
            // When using Reference Attributes, they are set as regular
            // attributes are set.  A Reference Attribute is set using
            // a ddo.  At the time the Reference Attribute is set, the
            // ddo it will reference must be persistent in the datastore.
            // In this example, it will use the ddoDocument DKDDO object
            // that the "add()" operation was processed above.
            //
            // For explanation of Reference Attributes, please refer to 
            // SReferenceAttrDefCreationICM Sample.
            System.out.println("Setting / Modifying Reference Attributes...");

                ddoItem.setData(ddoItem.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_myRef"),ddoDocument); // Using DKDDO named 'ddoDocument' made persistent above.

            System.out.println("Seting / Modified Reference Attributes...");

            //-----------------------------------------------------------------
            // Add / Save Item with Reference Attribute to persistent datastore
            //-----------------------------------------------------------------
            System.out.println("Adding / Saving Item to Persistent Datastore...");

                ddoItem.add();
                
            System.out.println("Added / Saved Item to Persistent Datastore...");

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
    * Create a simple Item Type with 3 child components for use with Samples.
    * @param dsICM         Connected DKDatastoreICM object.
    * @param itemProperty  Item Property or Semantic Type of Item (DKConstant.DK_CM_xxxxxx  Ex: DOCUMENT, FOLDER, ITEM, <user defined>).
    * @return  Returns a created Item of Item Type "S_withChild" with a Root DDO and three Child Components / DDOs.
    **/
    public static DKDDO createSimpleItemWithChildren(DKDatastoreICM dsICM, short itemPropertyOrSemanticType) throws DKException, Exception{
        System.out.println("Creating simple Item with 3 Children for Sample Purposes...");

            // Create an item / DDO / Root Component
	        DKDDO ddo = dsICM.createDDO("S_withChild", itemPropertyOrSemanticType); //createDDO(<Overall Item Type>, <Item Property / Semantic Type>);

            // Create child components / DDOs / Multivalue Attributes
	        DKDDO ddoChild1 = dsICM.createChildDDO("S_withChild", "S_simpleChild");   // createChildDDO(<Overall Item Type>, <Child Component Type>);
	        DKDDO ddoChild2 = dsICM.createChildDDO("S_withChild", "S_simpleChild");   //    For more information, refer to header of this sample.
	        DKDDO ddoChild3 = dsICM.createChildDDO("S_withChild", "S_simpleChild");  

            // Adding Child Components / Multivalue Attributes to DDOs
            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_CHILD,ddoChild1.getObjectType());
            DKChildCollection children = (DKChildCollection) ddo.getData(dataid);
            if(children==null){ // if no collection exists, create one.
                children = new DKChildCollection();
                ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_CHILD, ddoChild1.getObjectType()), children);
            }
            children.addElement(ddoChild1); // add children to the collection.  These children
            children.addElement(ddoChild2); // are all of the same object type, so they belong
            children.addElement(ddoChild3); // to the same children collection.

            // Setting / Modifying Attributes
            byte picture_bytes[] = {(byte)19,(byte)18,(byte)17,(byte)255,(byte)16,(byte)15,(byte)14,(byte)13,(byte)12,(byte)11,(byte)0,(byte)110,(byte)122, (byte)164, (byte)0, (byte)127, (byte)128, (byte)255,(byte)1 }; // Define byte for value to set in Blob attribute.
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_varchar"), "this is a string value"); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_blob"),    picture_bytes); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_char"),    "A");  // Char is specified with a String
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_clob"),    "Large amount of text can go here.  Size depends on attribute definition."); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_date"),    java.sql.Date.valueOf("2001-08-12")); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_decimal"), new java.math.BigDecimal("12.45")); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_double"),  new Double("123")); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_integer"), new Integer("123")); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_short"),   new Short("5"));
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_time"),    java.sql.Time.valueOf("10:00:00")); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_timestamp"),java.sql.Timestamp.valueOf("2001-08-12 10:00:00.123456"));

            // Setting / Modifying Attributes Belonging to Attribute Groups
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_street"), "123 First Street"); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_city"),   "Kingville"); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_state"),  "CA"); 
            ddo.setData(ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,"S_address.S_zipCode"),"12345-6789"); 

            // Add / Save item to persistent datastore
            ddo.add(); // Adds entire item tree, including all children

        System.out.println("Created Simple Item with 3 Children for Sample Purposes.");
        return(ddo);
    }
}//end class SItemCreationICM
              
