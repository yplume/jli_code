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
    For information on Item Creation, Retrieval, Updating, & Deletion, please refer to 
    the SItemCreationICM, SItemRetrievalICM, SItemUpdateICM, & SItemDeletionICM Samples.

Links
    Links are a very flexable way to link a source to a target.  A Link simply relates
    two Items and provides the means to access the Items it links to or that link to it.
    Unlike Reference Attributes, usage of Links is determined at runtime by the User 
    Program and any number of links may be used.
    
    Since Links were designed to be very flexable building blocks for User Applications to
    mold for their interests, Links are very open and non-restrictive.  The User 
    Application is responsible for any further restrictions that it chooses to place
    upon links.

    Item Types
        An Item of any Item Type can be linked to any Item, regardless of
        Item Type.  

    Root Components
        Since Links relate entire "Items", which represent an instance or data for the
        entire hierarchial tree structure of an Item Type, the root components must be
        used.  Only the Root Component of an Item may be linked to or linked from.
        
    Child Components
        As explained in the "Root Components" section, by design, links do not and can 
        not link to or from Child Components.  
        
    Version Control
        Links simply relate an entire Source Item to a Target Item, regardless of 
        version.  In order to maintain flexability of linking, links are not restricted
        to specific versions.  The User Applicaiton is responsible for choosing to
        place a Version Control mechanism on Links.  The optional descriptor item
        (also known as "link item"), a description DDO associated with the link,
        would be a good place to store such information.  
        
        Please refer to Reference Attributes as a viable alternative to Linking if
        Version Control is desired.
        
    Non-Ownership of a Link
    	A link itself does not belong to either the source or the target.  A link just
    	connects a source and target.  For example, If source 'A' is linked to target 
    	'B', 'A' will always be the source and 'B' will always be the target, regardless
    	of which DDO, 'A' or 'B', it is considered in reference to.

    In-Memory, System-Managed Consistency
        The DKConstantICM.DK_ICM_NO_LINKS_MEMORY_CURRENCY option can significantly
        improve performance when adding or updating a large number of links.
        
        By default (Java-only behavior), when you save an item with changes to     
        links, the links are made persistent and then all referenced DDOs          
        associated with the links are updated, in memory, to reflect these changes.
        Suppose you want to link DDO A with DDO B.  You create a DKLink in memory  
        with Source DDO A and Target DDO B.  To save the link, you simply need to  
        add the DKLink to *either* the source or the target and save the DDO (DDO  
        add or update operation).  In this case, you only added the link to DDO A, 
        therefore only DDO A knows about the link because you added it to the data 
        structures within DDO A.  DDO B has no record of the link unless you       
        refresh DDO B by re-retrieving with the inbound links retrieve option.     
        Since you had provided the reference to DDO B in the DKLink object, the    
        default link processing behavior in Java checks DDO B and adds the DKLink  
        (and any necessary DKLinkCollection) to DDO B.  This avoids a required     
        refresh in DDO B to see related information changed by the current session.
        
        The overhead of reflecting the changes in all DDOs is large when processing 
        large numbers of links (thousands of links).  Many applications have no need
        for in-memory currency for the other DDO and are not interested in the      
        current links information in memory.  For applications that do not need     
        currency, it is strongly recommended that you turn off links memory currency
        to enhance performance.  Memory currency remains on by default to maintain  
        backward compatibility.                                                     
        
        By default, both the Source & Target DDOs will contain copies of the same
        DKLink object reference.  Since the DKLink object contains a reference to
        both source & target, a DDO containing a link will contain a link that
        refers to itself as well as another.  For example, if Source A is linked
        to target B, both A & B will contain the same link which would look like
        the following:

            DKLink Defined 	      
            for A -> B          DDO A        DDO B
            --------------    ---------    ---------
            DKLink            DKLink       DKLink
                Src: A            Src: A       Src: A
                Tgt: B            Tgt: B       Tgt: B
        
        REQUIREMENT:
            - When adding or removing links, only perform the operation using one
              of the two items, source or target.  If you add to both and if you
              invoke add() or update() on both, the second attempt will report
              a duplicate persistent link.  The other item in the link will not
              have to be updated.  When in-memory link currency remains
              enabled (default), the changes will automatically affect the 
              other when persistently saved.  
            - If the Source & Target DDO References were retrieved independently
              or if in-memory link currency is disable for faster performance,
              the other DDO with the DKLinkCollection that was not updated must
              be refreshed with a re-retrieval if updated Link information
              if desired.
        	  
        Additional Notes:
            The system can only manage consistency of the actual DDOs reference
            in the DKLink objects.  Therefore, if you plan to minimize DDO
            instances and use the in-memory link currency, instead of retrieving
            both A & B separately, 'A' should be retrieved, and then 'B' should
            be obtained directly from the Link object found within 'A'.
            However, turning off in-memory currency is strongly recommended.

    Inbound & Outbound:
        As explained above, a Link is not owned by either the Source or Target.  However,
        the view of the link from either end of the link will appear different from the
        the perspective at each end.  
        
        Consider two people, Person A & Person B, standing in a street, throwing a baseball
        between them.  Person A throws the ball to Person B.  Person A considers the ball
        thrown in the air as moving away, while Person B considers the same ball as moving
        towards him.  Both are correct from each persons' perspective.
        
        Now consider a Link from A to B (A -> B), where 'A' is the Source and 'B' is the
        Target.  'A' will consider this link "to" 'B' as "Outbound".  'B' will consider
        the same link "from" 'A' as "Inbound".
        
        Outbound
            When standing at the Source of a link, all links to a target are considered
            "Outbound".
        
        Inbound
            When standing at the Target of a link, all links from some other source to it
            are considered "Inbound".
		    
    WARNING: Items Contain Cyclic Reference To Self
        As explained above, both the Source & Target can contain a copy of the
        same Link object.  The Link object consists of a reference to the Source
        DDO and a reference to the Target DDO.  Since the Link contains a
        reference to both Source & Target DDOs, one of the two must be the actual
        refernce back to itself.  Consider again the following example.  
		
            DKLink Defined 	      
            for A -> B          DDO A        DDO B
            --------------    ---------    ---------
            DKLink            DKLink       DKLink
                Src: A            Src: A       Src: A
                Tgt: B            Tgt: B       Tgt: B
        	
        Clearly 'A' contains a cyclic reference to itself through the DKLink's Source 
        reference.  Clearlly 'B' contains a cyclic reference to itself through the
        DKLink's Target reference.  If in-memory link currency remains enabled,
        traversing the links can loop through the same DDO instance and in other
        cases a separate DDO instance represents the same item (detected by 
        accessing the PID or PID string).

    Link Data Structure
    	Links are described in DKLink objects described here:

            DKLink
              LinkTypeName: The type of link.
                    Source: The Source Item of a link.
                    Target: The Target Item of a link.
                  LinkItem: (Optional) Descriptor Item.
		
    Link Type Names
        Link relationships have names.  Links are grouped into Link Collections within
        a DDO by this name.  Any number of User-Defined Link Type Names may be used or
        the generic system defined Link Type Name may be used, "Contains".  For more
        information on defining new Link Types, please refer to the Link Type Definition
        Sample(s).  
        
        System Defined Link Type Name:
        
        Link Type Name   Constant
        --------------   -------------------------------------------
           Contains      DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS
        
        SPECIAL LINK TYPE NAME: DKFolder
            A special Link Type Name is provided and used by the system to manage
            Folders.  The DKFolder object is a simplified interface to an Outbound
            Link Collection to make Folders as easy to use as possible.  However,
            Folders must still be Searched using Links, specifying this Link Type
            Name, "DKFolder".
            
            Special Link Type   Constant
            -----------------   ---------------------------------------------
                DKFolder        DKConstantICM.DK_ICM_LINKTYPENAME_DKFOLDER

            REQUIREMENT:
                The "DKFolder" Link Type must not be used to define or delete links, or
                be used in any way with a DKLinkCollection.  Please refer to the SFolderICM
                Sample for correct usage of Folders.
                
    Must Set "Is Linked" Property in DDO
        When you add links to any DDO, you must also set the DKDDO property
        DKConstantICM.DK_ICM_PROPERTY_IS_LINKED to Boolean.TRUE if you want
        add() and update() operations to process link updates in your DDO.
        You do not need to set this property to Boolean.FALSE after
        removing links.  If you previously retrieved links but zero were 
        found, this property is set to Boolean.FALSE and must be changed
        to Boolean.TRUE.
        
        The following static utility method is provided to easily set or
        change the "Is Linked" property in any DDO.        
          - SLinksICM.setPropertyIsLinked(DKDDO ddo,boolean value)        
   
Creating, Modifying, & Removing Links
    PLEASE USE the wrappers provided in this sample for addLinkToDDO() and
    removeLinkFromDDO() to make links as easy as possible to use.
    
    As stated above, links are grouped into Link Collection by the Link Type Name.  Link
    Collections consist of a DKLinkCollection object, which is a colleciton of DKLink
    objects.  Empty DKLinkCollections do not exist by default and must be created by the
    User Program.  The wrapper provided in this sample accomplishes this easily.

    Recommended, Most Efficient Method
        The most efficient method to add or remove any number of links is to group all
        additions and modifications into a single update to the Library Server.  This is
        accomplished by using the DKLinkCollections add "Element" or remove "Element" 
        functions.  When all modifications are complete, the DKDDO's add() or update()
        method may be called.
    
    Immediate Results
        For Immediate results, use the DKLinkCollection.addMember() or DKDatastoreExtICM's
        createLink() functions, which make the changes persistent immediately, but
        at an expensive cost of a call to the Library Server for every link added.

    Incremental Changes Tracked
        All changes to the each DKLinkCollection object are tracked internally.  
        Each incremental change will be packaged up and sent to the Library Server
        in single call when the root component's DKDDO.update() function is called.
        It is important to note that the incremental changes are tracked and it is
        not the end result that is made persistent.  Optimal modifications rely on
        the fewest changes that result in the same end result.

    Modifying a DKLink Object
        Links may only be added or removed.  They may not be modified.  Any modifications
        to existing DKLink objects within the DKLinkCollections will be ignored.  In
        order to modify a DKLink, remove the link and then create an entirely new 
        DKLink object and add it to the collection as a new link.  This new link may 
        be a modified form of the old link.
        
    The DK_ICM_NO_ERROR_FOR_DUP_OR_NOT_FOUND_LINKS option
        When creating or removing a link, the DK_ICM_NO_ERROR_FOR_DUP_OR_NOT_FOUND_LINKS
        option may be specified.  This option controls whether an exception is 
        generated in certain cases.  When creating a link, by default, the create will 
        fail if the link already exists.  If this option is used, the creation of the
        link will not give an error if the link already exists.  When removing a link,
        by default, an exception will be generated if the link does not exist.  If the
        DK_ICM_NO_ERROR_FOR_DUP_OR_NOT_FOUND_LINKS option is used, the removal of the 
        link will not fail if the link does not exist.       
        
        Typically, it is a good idea to use this option as most applications do not
        care whether the link removal or creation fails, they just care about the 
        end result.  For example, when removing a link, most application do not 
        care whether the link was removed by the current call or a previous call,
        they only care that the link no longer exists on the server after the call.

Retrieving Items with Links:
    Links are not retrieved by default.  This allows greater control by the User
    Program to control all performance implications.  A retrieval option must be
    set to retrieve links.  If Links are not retrieved, the DKLinkCollections
    will not exist until the program chooses explicitly to retrieve links.
    
    Links are retrieved through the same retrieve interfaces shown in
    SItemRetrievalICM.  See SItemRetrievalICM for important information about
    retrieve options in general.  Through the same interfaces that you can
    request attributes, children, and resource content, you can also request
    links to be listed by making additional selections using a
    DKRetrieveOptionsICM instance.
    
    An example of some of the links-related retrieve options are listed below.
      - DKRetrieveOptionsICM::linksInbound(boolean)  
      - DKRetrieveOptionsICM::linksInboundFolderSources(boolean)  
      - DKRetrieveOptionsICM::linksOutbound(boolean)    
      - DKRetrieveOptionsICM::linksDescriptors(boolean)
      - DKRetrieveOptionsICM::linksTypeFilter(String linkTypeNameRequested)
      - DKRetrieveOptionsICM::linksLevelTwo(boolean) (Java Only)
      - DKRetrieveOptionsICM::linksLevelTwoCount(boolean)

      * See DKRetrieveOptionsICM for full list of links-related retrieve
        options identified by names starting with links____().
        
      * Each option is explained in detailed reference documentation (Javadoc)
        in DKRetrieveOptionsICM, including detailed explanations, performance
        considerations, and more for each option.
        
      * Outbound link requests require a request for system attributes
        to obtain the semantic type property to identify folders.
        This is only needed for outbound links.  You can also specify
        attribute filters to retrieve only the system attributes.
        See DKRetrieveOptionsICM::linksOutbound(boolean) documentation.        
    
    IMPORTANT
        There are significant performance considerations to think about when
        deciding when to retrieve links.  Review the detailed performance
        considerations documented in DKRetrieveOptionsICM (Javadoc).
    
    Empty Link Collections Not Created
        Please note that empty DKLinkCollections will not be created during
        retrieval unless one or more links are retrieved for that particular
        link type.  The wrapper functions provided in this sample easily account
        for this.  If the wrappers provided are used, attention to this is not
        needed.

    Retrieve Option Combinations
        Links requests can be combined with other meta-data in the same 
        retrieve call.

    DKLink Object's Non-Retrieved DDOs
        When a Link is retrieved, a new DKLink object is created.  The if it is the Source,
        the DKLink's Source is set to the exact reference to itself and a non-retrieved
        DDO is created for the Target with the Pid information filled out.  If it is the
        Target, the DKLink's Target is set to the exact reference to itself and a non-
        retrieved DDO is created for the Source with complete PID information.
        
        The other item reference in the link is not retrieved.  It is up the User
        Application to explicitly choose to retrieve it.

OTHER REQUIREMENTS:
    - All other Items associated in a Link must be persistent in the datastore 
        before the DDO owning the Link Collection is added or updated.
    - It is up to the User Program to use valid DDOs and to not add multiple
        copies of the same link.
    - Please do not remove an added link in the Link Collection that has not
        yet been made persistent. 
    - Please do not add a link that was just removed, but not yet made persistent.

Element vs. Member
    The DKSequentialCollection's "Element" methods are very different from the
    "Member" methods.  "Element" Methods are the most efficient & recommended 
    methods, while "Member" Methods offer immediate results.
    
    Element Methods
        "Element" methods are the recommended and most efficient method to use.
        Changes are not made persistent until the DKDDO.add() or DKDDO.update()
        methods are called.  All operations are grouped into one call to the 
        Library Server.
    
    Member Methods
        For immediate results, use the "Member" methods.  Changes are made 
        persistent immediately, but at an expensive cost of a call to the 
        Library Server for every add or remove using these methods.
    
addElement() vs. addMember()
    This is an example of the "Element vs. Member" section above.

removeElement() vs. removeMember()
    This is an example of the "Element vs. Member" section above.

DKDatastoreExtICM.createLink() vs. addElement()
    The Datastore Extention's createLink() function is very much like the
    "Member" operations on the DKLinkCollection.  This is a slightly different
    immediate, but expensive method.

DKDatastoreExtICM.createLink() vs. addMember()
    There is very little difference between these two methods.  

*******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;

/************************************************************************************************
 *          FILENAME: SLinksICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Creating, Retrieving, Modifying, Deleting, Printing Links.  In-Memory 
 *                    System-Managed Consistency, Inbound & Outbound Concepts, Link Types,
 *                    Retrieving Link Content / Data, Element vs. Member Methods
 *                    ---------------------------------------------------------------------------
 *     DEMONSTRATION: Creating Links, 
 *                    Modifying Links,
 *                    Deleting Links, 
 *                    In-Memory, System-Managed Consistency
 *                    Inbound & Outbound Concepts
 *                    Link Types
 *                    Retrieving Items with Links
 *                    Link Retrieval Options
 *                    Accessing DKLinkCollections
 *                    Inbound Iterator
 *                    Outbound Iterator
 *                    Element vs. Member Methods
 *                    Printing Links
 *                    Retrieving Link Contents / Data
 *                    Get All Links in a DDO from any Link Collection
 *                    Retrieve All Link Contents in a DDO from any Link Collection
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java SLinksICM <database> <userName> <password>
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: The Data Model must be defined.  If needed please run the following Samples  
 *                        - SAttributeDefinitionCreationICM
 *                        - SAttributeGroupDefCreationICM
 *                        - SReferenceAttrDefCreationICM
 *                        - SItemTypeCreationICM
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 ************************************************************************************************/
public class SLinksICM{
    
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
            System.out.println("  java SLinksICM <database> <userName> <password>" );
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
        System.out.println("Sample Program:  SLinksICM");
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
            // Create DDOs
            //-------------------------------------------------------------
            // create 4 Items, one of Item Property type Document, one Folder, & three of Item.
            System.out.println("Creating & Saving Sample Items...");

	            DKDDO ddoA = dsICM.createDDO("S_simple", DKConstant.DK_CM_DOCUMENT);
	            DKDDO ddoB = dsICM.createDDO("S_simple", DKConstant.DK_CM_FOLDER);   
	            DKDDO ddoC = dsICM.createDDO("S_simple", DKConstant.DK_CM_ITEM);     
	            DKDDO ddoD = dsICM.createDDO("S_simple", DKConstant.DK_CM_ITEM);
	            DKDDO ddoDescriptor = dsICM.createDDO("S_simple", DKConstant.DK_CM_ITEM);

	            ddoA.add();
	            ddoB.add();
	            ddoC.add();
	            ddoD.add();
	            ddoDescriptor.add();

            System.out.println("Created & Saved Sample Items.");

            //-------------------------------------------------------------
            // Check out an Item for Modification
            //-------------------------------------------------------------
            System.out.println("Checking Out / Locking DDO 'A' Before Update...");

                dsICM.checkOut(ddoA);  // Must check out / lock before updating.

            System.out.println("Checked Out / Locked DDO 'A' Before Update.");

            //-------------------------------------------------------------
            // Creating a Link Object
            //-------------------------------------------------------------
            // Creates a DKLink object from ddoA to ddoB, A -> B.
            // This sample will use the System-Defined Link Type "Contains".  
            // User-Defined Link Types may be substituted.
            //
            // Usage: DKLink(<Link Type Name>, <Source DDO>, <Target DDO>);
            System.out.println("Creating a Link from 'A' to -> 'B'...");

                DKLink dkLink = new DKLink(DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS, ddoA, ddoB);  // Usage: DKLink(<Link Type Name>, <Source DDO>, <Target DDO>);
            
            System.out.println("Created a Link from 'A' to -> 'B'.");

            //-------------------------------------------------------------
            // Add Link to one of the DDOs
            //-------------------------------------------------------------
            // When the DDO that the link is added to is updated to make 
            // the changes persistent, the other DDO will automatically
            // get a copy of this link.
            //
            // DO NOT add it to both, unless the other DDO will definately
            // not be modified.  Please refer to the header documentation
            // section labeled "In-Memory, System-Managed Consistency".
            //
            // Simply using the wrapper funciton provided in this sample 
            // is the esiest & recommended way.  Please refer to the wrapper
            // function to learn about the specifics of what would be needed
            // to do this without the wrapper function.
            //
            // In this case, the link will be added to the Source DDO.  Since
            // no ownership is implied, it does not matter which it is added to.
            System.out.println("Adding Link to DDO 'A'...");

                addLinkToDDO(dkLink,ddoA); // Simply use wrapper function provided.
                
                // NOTE:  'A' sees this as an Outbound Link,
                //        'B' sees this same link as an Inbound Link.

            System.out.println("Added Link to DDO 'A'.");

            //-------------------------------------------------------------
            // Make changes persistent
            //-------------------------------------------------------------
            // RECOMMENDED: Disable links memory currency.
            // As explained in the header documentation earlier,
            // disable links memory currency as shown below unless
            // needed.  However, for demonstration purposes, links memory
            // currency is shown to help display mutual ownership between
            // the source and target as shown in the printed links after
            // saving. 
            //
            // Note: The DK_ICM_NO_ERROR_FOR_DUP_OR_NOT_FOUND_LINKS option 
            // may also be specified to prevent an exception if the link
            // already exists.
            System.out.println("Making Changes Persistent...");

                // RECOMMENDED: Disable links memory currency as shown.
                //              However, for demonstration, it is not disabled.
                //ddoA.update(DKConstantICM.DK_ICM_NO_LINKS_MEMORY_CURRENCY);
            
                ddoA.update();         // Remember to explicitly check in when finished.
                dsICM.checkIn(ddoA);   // Can specify DKConstant.DK_CM_CHECKIN option or use the datastore method.

            System.out.println("Made Changes Persistent.");

            //-------------------------------------------------------------
            // Printing Source DDO (A) Links
            //-------------------------------------------------------------
            System.out.println("Printing Source DDO's (A's) Links...");
                printLinks(ddoA);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed Source DDO's (A's) Links.");

            //-------------------------------------------------------------
            // Printing Target DDO (B) Links
            //-------------------------------------------------------------
            // Notice how both have the exact same DKLink object.  The system
            // updated the other Item in the link using the reference used
            // in the DKLink object.
            System.out.println("Printing Target DDO's (B's) Links...");
                printLinks(ddoB);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed Target DDO's (B's) Links.");

            //-------------------------------------------------------------
            // Check Out the Target of the Last Link for Modification
            //-------------------------------------------------------------
            System.out.println("Checking Out / Locking Target (B) of Last Link Before Update...");

                dsICM.checkOut(ddoB);  // Must check out / lock before updating.

            System.out.println("Checked Out / Locked Target (B) of Last Link Before Update.");

            //-------------------------------------------------------------
            // Creating a Link Object with a Descriptor Item / Link Item
            //-------------------------------------------------------------
            // Creates a DKLink object from ddoB to ddoC, B -> C and
            // a DKLink object from ddoB to ddoD, B -> D.  
            // However, Link B -> C will have a descriptor associated with the link.
            //
            // Usage: DKLink(<Link Type Name>, <Source DDO>, <Target DDO>, (Optional) <Descriptor Item / Link Item>);
            System.out.println("Creating a Link B -> C w/Desc, & B -> D...");

                       dkLink  = new DKLink(DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS, ddoB, ddoC, ddoDescriptor);  // Usage: DKLink(<Link Type Name>, <Source DDO>, <Target DDO>, (Optional) <Descriptor Item / Link Item>);
                DKLink dkLink2 = new DKLink(DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS, ddoB, ddoD); 
            
            System.out.println("Created a Link B -> C w/Desc, & B -> D.");

            //-------------------------------------------------------------
            // Add Link to one of the DDOs
            //-------------------------------------------------------------
            // In this case, the link will be added to the Source DDO.  Since
            // no ownership is implied, it does not matter which it is added to.
            System.out.println("Adding Links to DDO 'B'...");

                addLinkToDDO(dkLink,ddoB);  // Simply use wrapper function provided.
                addLinkToDDO(dkLink2,ddoB); 
                
                // NOTE:  'B' sees these as Outbound Links, since it is the Source of both.
                //        'C' sees the same link as an Inbound Link.
                //        'D' sees the same link as an Inbound Link.

            System.out.println("Added Links to DDO 'B'.");

            //-------------------------------------------------------------
            // Make changes persistent
            //-------------------------------------------------------------
            // Any number of Link additions or removals can be grouped into
            // a single update() call.
            System.out.println("Making Changes Persistent...");

                // RECOMMENDED: Disable links memory currency as shown.
                //              However, for demonstration, it is not disabled.
                //ddoB.update(DKConstantICM.DK_ICM_NO_LINKS_MEMORY_CURRENCY);
            
                ddoB.update();         // Remember to explicitly check in when finished.
                dsICM.checkIn(ddoB);   // Can specify DKConstant.DK_CM_CHECKIN option or use the datastore method.

            System.out.println("Made Changes Persistent.");

            //-------------------------------------------------------------
            // Printing DDO B's Links
            //-------------------------------------------------------------
            System.out.println("Printing DDO B's Links...");
                printLinks(ddoB);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed DDO B's Links.");

            //-------------------------------------------------------------
            // Printing DDO C's Links
            //-------------------------------------------------------------
            System.out.println("Printing DDO C's Links...");
                printLinks(ddoC);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed DDO C's Links.");

            //-------------------------------------------------------------
            // Printing DDO D's Links
            //-------------------------------------------------------------
            System.out.println("Printing DDO D's Links...");
                printLinks(ddoD);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed DDO D's Links.");

            //-------------------------------------------------------------
            // Accessing a DKLinkCollection
            //-------------------------------------------------------------
            // Links are grouped by Link Type.  Each Link Type has its own
            // DKLinksCollection.  DKLinkCollections are accessed just like
            // attributes, using the Link Type Name, but using the Link 
            // namespace.
            //
            // Link Collections may be detected and read programatically as
            // shown in SItemRetrievalICM.printDDO().  The Data Items of the
            // may be read, checking for any that are of type Link Collection.
            //
            // Another way to programatically access link collections is to check
            // for the existance of each link type available in the system for this
            // ddo as shown in the printLinks() function at the bottom of this sample.
            System.out.println("Accessing B's 'Contains' Link Collection...");

                short dataidB = ddoB.dataId(DKConstant.DK_CM_NAMESPACE_LINK,DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS); // get the dataid of DDO B's "Contains" Link Collection.

                // If the dataid is 0, the Link Collection does not exist either 
                // because no links of that Link Type Name have been added yet or
                // the DDO has not yet been retrieved with Link retrieval options.
                
                // Please refer to the addLinkToDDO() function for a demonstration
                // of what to do in this case if you want to create an empty one
                // in its place.
                
                if(dataidB <= 0) // we should have a valid dataid, since it was just added in the addLinkToDDO() function.
                    throw new Exception("The 'Contains' Link Collection should exist in this case.  Link Colleciton not found.");

                DKLinkCollection linkCollectionB = (DKLinkCollection) ddoB.getData(dataidB); // get the link Collection in DDO B for link type "Contains".

            System.out.println("Accessed B's 'Contains' Link Collection.");

            //-------------------------------------------------------------
            // Accessing DKLinks within a Link Collection
            //-------------------------------------------------------------
            // NOTE: If only Outbound links were retrieved, only Outbound 
            //       links will be found.  If only Inbound links are retrieved,
            //       and the full iterator is used, it will still only find the
            //       retrieved Inbound links.
            System.out.println("Accessing DKLinks within a Link Collection...");
            
                // Access only what this DDO considers as Inbound Links:
                dkIterator inboundIter = linkCollectionB.createInboundIterator();
                
                // Access only what this DDO considers as Outbound Links:
                dkIterator outboundIter = linkCollectionB.createOutboundIterator();
                
                // Access all links, Inbound & Outbound.
                dkIterator iter = linkCollectionB.createIterator();
                
                // Access Each Retrieved Inbound Link
                System.out.println("Links in B's 'Contains' Link Collection:");
                System.out.println("  Inbound Links:");
                while(inboundIter.more()){
                    dkLink = (DKLink) inboundIter.next(); // Move pointer to next element & return that object.

                    String typeName   =         dkLink.getTypeName(); // Link Type Name
                    DKDDO  source     = (DKDDO) dkLink.getSource();   // Should be DDO A
                    DKDDO  target     = (DKDDO) dkLink.getTarget();   // Should be DDO B, itself.
                    DKDDO  descriptor = (DKDDO) dkLink.getLinkItem(); // The optional descriptor.  Null if not specified.

                    // Print
                    System.out.println("    Link:");
                    System.out.println("          Link Type:  "+typeName);
                    if(source != null)
                        System.out.println("             Source:  "+((DKPidICM)(source.getPidObject())).getItemId()+"  ("+source.getPidObject().getObjectType()+")");
                    else
                        System.out.println("             Source:  null");
                    if(target != null)
                        System.out.println("             Target:  "+((DKPidICM)(target.getPidObject())).getItemId()+"  ("+target.getPidObject().getObjectType()+")");
                    else
                        System.out.println("             Source:  null");
                    if(descriptor != null)
                        System.out.println("         Descriptor:  "+((DKPidICM)(descriptor.getPidObject())).getItemId()+"  ("+descriptor.getPidObject().getObjectType()+")");
                    else
                        System.out.println("         Descriptor:  null");
                }

                // Access Each Retrieved Outbound Link
                System.out.println("  Outbound Links:");
                while(outboundIter.more()){
                    dkLink = (DKLink) outboundIter.next(); // Move pointer to next element & return that object.
                    printLink(dkLink);                     // Use the wrapper provided to print the link.                  
                }

                // Access All Retrieved Links, Inbound & Outbound
                System.out.println("  All Links:");
                while(iter.more()){
                    dkLink = (DKLink) iter.next();         // Move pointer to next element & return that object.
                    printLink(dkLink);                     // Use the wrapper provided to print the link.                  
                }
            
            System.out.println("Accessed DKLinks within a Link Collection.");

            //-------------------------------------------------------------
            // Check out an Item for Modification
            //-------------------------------------------------------------
            System.out.println("Checking Out / Locking DDO 'B' Before Update...");

                dsICM.checkOut(ddoB);  // Must check out / lock before updating.

            System.out.println("Checked Out / Locked DDO 'B' Before Update.");
            
            //-------------------------------------------------------------
            // Breaking / Deleting a Link
            //-------------------------------------------------------------
            // Simply remove it from the DKLinkCollection.  When all modifications
            // are complete, make the changes persistent using DKDDO.update().
            //
            // The following will use the "Element" Method.  Please refer to the
            // header documentation for information on the differences.  For
            // Immediately Persistent, but expensive results (if used for more than
            // one in a row), The "Member" method may be used or the Datastore 
            // Extension's deleteLink() method may be used.  In those cases, no
            // DKDDO.update() is needed.
            System.out.println("Breaking / Deleting a Link ( B -> D )...");

                // Remove Link B -> D.
                //
                // Simply Use the Wrapper Funciton Provided.  However, it is heavy with
                // string compares, comparing the source & target Item IDs for a match.
                removeLinkFromDDO(new DKLink(DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS, ddoB, ddoD),ddoB);

            System.out.println("Broke / Deleted a Link ( B -> D ).");

            //-------------------------------------------------------------
            // Make changes persistent
            //-------------------------------------------------------------
            // Any number of Link additions or removals can be grouped into
            // a single update() call.
            //
            // Note: The DK_ICM_NO_ERROR_FOR_DUP_OR_NOT_FOUND_LINKS option 
            // may also be specified to prevent an exception if the link
            // does not exist.
            System.out.println("Making Changes Persistent...");
        
                // RECOMMENDED: Disable links memory currency as shown.
                //              However, for demonstration, it is not disabled.
                //ddoB.update(DKConstantICM.DK_ICM_NO_LINKS_MEMORY_CURRENCY);
            
                ddoB.update();        // Don't forget to explicitly check in when finished.
                dsICM.checkIn(ddoB);  // May specify DKConstant.DK_CM_CHECKIN option or use the datastore method.

            System.out.println("Made Changes Persistent.");

            //-------------------------------------------------------------
            // Printing DDO B's Links
            //-------------------------------------------------------------
            System.out.println("Printing DDO B's Links...");
                printLinks(ddoB);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed DDO B's Links.");

            //-------------------------------------------------------------
            // Printing DDO D's Links
            //-------------------------------------------------------------
            System.out.println("Printing DDO D's Links...");
                printLinks(ddoD);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed DDO D's Links.");

            //-------------------------------------------------------------
            // Recreate DDO to Demonstrate Retrieval
            //-------------------------------------------------------------
            System.out.println("Recreating DDO B...");
            
                String ddoBPidString = ddoB.getPidObject().pidString();  // Get the PID String
                ddoB = dsICM.createDDOFromPID(ddoBPidString);            // Recreate Blank DDO Using PID String.

            System.out.println("Recreated DDO B.");

            //-------------------------------------------------------------
            // Retrieving An Item With Links
            //-------------------------------------------------------------
            // Since retrieval options are binary, they may be combined with
            // other retrieval options explained in SItemRetrievalICM.  Below
            // are common retrieval options used with Links.
            //
            // Outbound link requests require a request for system attributes
            // to obtain the semantic type property to identify folders.
            // This is only needed for outbound links.  You can also specify
            // attribute filters to retrieve only the system attributes.
            // See DKRetrieveOptionsICM::linksOutbound(boolean) documentation.
            System.out.println("Retrieving Item with Links (DDO B)...");
            
                DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
                
                dkRetrieveOptions.linksOutbound(true);
                dkRetrieveOptions.baseAttributes(true);       // Can also specify attribute filters to request system-attributes only.
                ddoB.retrieve(dkRetrieveOptions.dkNVPair());  // Retrieve with Outbound Links.
                
                // OR
                
                dkRetrieveOptions.linksInbound(true);
                dkRetrieveOptions.linksOutbound(false);
                ddoB.retrieve(dkRetrieveOptions.dkNVPair());  // Retrieve with Inbound Links.
                
                // OR

                dkRetrieveOptions.linksInbound(true);
                dkRetrieveOptions.linksOutbound(true);
                dkRetrieveOptions.linksDescriptors(true);
                dkRetrieveOptions.baseAttributes(true);       // Can also specify attribute filters to request system-attributes only.
                dkRetrieveOptions.linksTypeFilter(DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS);
                ddoB.retrieve(dkRetrieveOptions.dkNVPair());  // Retrieve with Inbound & Outbound Links.

                // OR

                dkRetrieveOptions.baseAttributes(true);
                dkRetrieveOptions.childListOneLevel(true);
                dkRetrieveOptions.childAttributes(true);
                dkRetrieveOptions.linksInbound(true);
                dkRetrieveOptions.linksOutbound(true);
                dkRetrieveOptions.linksDescriptors(true);
                dkRetrieveOptions.linksTypeFilter("*");
                ddoB.retrieve(dkRetrieveOptions.dkNVPair());

            System.out.println("Retrieved Item with Links  (DDO B).");

            //-------------------------------------------------------------
            // Printing DDO B's Links
            //-------------------------------------------------------------
            System.out.println("Printing DDO B's Links...");
                printLinks(ddoB);  // Use Wrapper Provided At Bottom of Sample
            System.out.println("Printed DDO B's Links.");

            //-------------------------------------------------------------
            // Retrieving Link Contents / Data            
            //-------------------------------------------------------------
            // The DKLink objects are rebuilt.  The Source, Target, & Descriptor
            // are recreated as new, non-retrieved DDOs with complete PID information.
            // In the case when the DDO being retrieved is the Source or Target, its
            // own reference to itself is used.
            //
            // This sample wills imply retrieve all non-retrieve contents that are in
            // the "Contains" Link Collection.
            System.out.println("Retrieving 'Contains' Link Contents / Data...");

                short dataid = ddoB.dataId(DKConstant.DK_CM_NAMESPACE_LINK,DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS); // get the dataid of DDO B's "Contains" Link Collection.
                if(dataid > 0){ // Only need to retrieve any if a link colleciton exists
                
                    DKLinkCollection linkCollection = (DKLinkCollection) ddoB.getData(dataid); // get the link Collection in DDO B for link type "Contains".
                    iter = linkCollection.createIterator();  // Access all links, Inbound & Outbound.
                
                    // Suppose you want to simply retrieve attributes for all linked items.
                    dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM); // Recreate a blank options object (all set to false).
                    dkRetrieveOptions.baseAttributes(true);
                
                    while(iter.more()){                // Go through all links in the list
                        dkLink = (DKLink) iter.next(); // Move pointer to next element & return that object.

                        DKDDO  source     = (DKDDO) dkLink.getSource();  
                        DKDDO  target     = (DKDDO) dkLink.getTarget();  
                        DKDDO  descriptor = (DKDDO) dkLink.getLinkItem(); // The optional descriptor.  Null if not specified.

                        // If the DDOs are not a reference to itself, retrieve them.
                        if(source!=ddoB)
                            source.retrieve(dkRetrieveOptions.dkNVPair());
                        if(target!=ddoB)
                            target.retrieve(dkRetrieveOptions.dkNVPair());
                        if(descriptor!=null)
                            descriptor.retrieve(dkRetrieveOptions.dkNVPair());
                    }//end while
                }//end if "Contains" Link Collections Exists.

            System.out.println("Retrieved 'Contains' Link Contents / Data.");

            //-------------------------------------------------------------
            // Immediately Persistent, but Expensive
            //-------------------------------------------------------------
            // As explained in the header documentation, "Element" methods
            // are the best and most efficient way to group multiple modifications
            // into a single update.  
            // 
            // However, if an immediately persistent result is desired or if the 
            // changes are few and infrequent, the "Member" methods and Datastore
            // Extension Linking methods may be used at the cost of a call to the
            // server for every modification.
            //
            // No following DKDDO.update() method is required.
            System.out.println("Using Immediately Persistent, But Expensive Operations...");

                // Create a Link Object to link B -> D.
                dkLink = new DKLink(DKConstantICM.DK_ICM_LINKTYPENAME_CONTAINS, ddoB, ddoD); // Create a Link Object for B -> D.
                
                // Create a new Datastore Extension object.
                DKDatastoreExtICM dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT); // Get the Datastore Extension Object

                // Use the Datastore Extension Methods
                dsExtICM.addLink(dkLink);    // No further action is required
                dsExtICM.removeLink(dkLink); // No further action is required
                
                // ALTERNATIVE:  The "Element" methods in functions addLinkToDDO()
                //               may be switched with "Member" alternatives.  Please
                //               refer to the Application Programming Reference for
                //               complete information.

            System.out.println("Used Immediately Persistent, But Expensive Operations.");

            //-------------------------------------------------------------
            // Clean Up Database After Sampe
            //-------------------------------------------------------------
            // This sample will clean up / undo everything that stored
            // in the database.
            System.out.println("Cleaning Up...");

                ddoA.del();
                ddoB.del();
                ddoC.del();
                ddoD.del();

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
    * Print the specified Link.  The DDOs within the Link will be printed with Item ID & Object
    * Type in parentheses.
    * @param dkLink  DKLink object to print.
    **/
    public static void printLink(DKLink dkLink){
        String typeName   =         dkLink.getTypeName(); // Link Type Name
        DKDDO  source     = (DKDDO) dkLink.getSource();   // Should be DDO A
        DKDDO  target     = (DKDDO) dkLink.getTarget();   // Should be DDO B, itself.
        DKDDO  descriptor = (DKDDO) dkLink.getLinkItem(); // The optional descriptor.  Null if not specified.

        // Print The Link Information
        System.out.println("    Link:");
        System.out.println("          Link Type:  "+typeName);
        if(source != null)
            System.out.println("             Source:  "+((DKPidICM)(source.getPidObject())).getItemId()+"  ("+source.getPidObject().getObjectType()+")");
        else
            System.out.println("             Source:  null");
        if(target != null)
            System.out.println("             Target:  "+((DKPidICM)(target.getPidObject())).getItemId()+"  ("+target.getPidObject().getObjectType()+")");
        else
            System.out.println("             Source:  null");
        if(descriptor != null)
            System.out.println("         Descriptor:  "+((DKPidICM)(descriptor.getPidObject())).getItemId()+"  ("+descriptor.getPidObject().getObjectType()+")");
        else
            System.out.println("         Descriptor:  null");
    }//end printLink

   /**
    * Print all Links for a DDO.
    * @param ddo  DKDDO to print all Links that currently exist in the DDO.
    **/
    public static void printLinks(DKDDO ddo) throws Exception{
     
        System.out.println("");
        System.out.println("Links in DDO '"+((DKPidICM)(ddo.getPidObject())).getItemId()+"' ("+ddo.getPidObject().getObjectType()+")");

        System.out.println("  Inbound Links:");

        java.util.ArrayList<DKLink> linksList = getInboundLinks(ddo); // Get all Inbound Links within the DDO.
        System.out.println("   Total:  "+linksList.size());
        for(int i=0; i<linksList.size(); i++)              // go through all links in the ArrayList.
            printLink(linksList.get(i));     // print each link.

        System.out.println("  Outbound Links:");

        linksList = getOutboundLinks(ddo);             // Get all Outbound Links within the DDO.
        System.out.println("   Total:  "+linksList.size());
        for(int i=0; i<linksList.size(); i++)          // go through all links in the ArrayList.
            printLink(linksList.get(i)); // print each link.
        
        System.out.println("");
    } // end printLinks

   /**
    * Add the DKLink to the owner DDO's Link Collecitons.  The next time the DDO's add or 
    * update operations are called, it will be added to the link table.  This function will
    * take care of any other setup for data structures & settings within the DDO.
    * @param dklink	The link to add to the owner DDO.
    * @param ownerDDO	The owner DDO who has the links to the other DDO.
    */
    public static void addLinkToDDO(DKLink linkToAdd, DKDDO ownerDDO) throws DKException, Exception  {
	
        // get the link colleciton for this DDO.
        short dataid = ownerDDO.dataId(DKConstant.DK_CM_NAMESPACE_LINK,linkToAdd.getTypeName());
        if (dataid == 0) // if none exists, go ahead and create a link collection for it.
            dataid = createNewLinkCollection(ownerDDO, linkToAdd.getTypeName()); //add a link collection for this.
        DKLinkCollection linkCollection = (DKLinkCollection) ownerDDO.getData(dataid);            // get the link Collection
        if(linkCollection==null){ // if there is no link collection, create one (This is just a double check)
            dataid = createNewLinkCollection(ownerDDO, linkToAdd.getTypeName()); //add a link collection for this.
            linkCollection = (DKLinkCollection) ownerDDO.getData(dataid);        // get the link Collection
        }

        // add the link to the link collection
        linkCollection.addElement(linkToAdd);
        
        // set the DDO "Is Linked" property
        setPropertyIsLinked(ownerDDO,true);
        
    }//end addLinkToDDO

   /**
    * Initialize the link collection for the DDO and set the DDO properties for the link collection.
    * @param ownerDDO	The DDO that contains the link to another ddo.
    * @param linkTypeName	The name of the Link Type.
    * @return	Returns the dataid of the new collection.
    */
    public static short createNewLinkCollection(DKDDO ownerDDO, String linkTypeName) throws DKException,Exception, DKUsageError
    {
        // "Creating new link collection of link type '"+linkTypeName+"'."
        short dataid = ownerDDO.dataId(DKConstant.DK_CM_NAMESPACE_LINK,linkTypeName);
        if(dataid == 0) // if none exists, add one.
            dataid = ownerDDO.addData(DKConstant.DK_CM_NAMESPACE_LINK,linkTypeName); //add a property to the ddo with that link type.

        //set a data property of the ddo to indicate that it has a collection
        short propid = ownerDDO.dataPropertyId(dataid,DKConstant.DK_CM_PROPERTY_TYPE);
        if(propid == 0){
            ownerDDO.addDataProperty(dataid, DKConstant.DK_CM_PROPERTY_TYPE, new Short(DKConstant.DK_CM_DATAITEM_TYPE_LINKCOLLECTION));
        }else{
            ownerDDO.setDataProperty(dataid, propid, new Short(DKConstant.DK_CM_DATAITEM_TYPE_LINKCOLLECTION));
        }

        DKLinkCollection linkCollection = new DKLinkCollection();

        //add the link collection as a data property to the ddo.
        ownerDDO.setData(dataid, linkCollection);

        // "Created new link collection of link type '"+linkTypeName+"'.	Dataid = '"+dataid+"'."

        return(dataid);
    }//end createNewLinkCollection
	
  /**
   * Set the DKDDO property DKConstantICM.DK_ICM_PROPERTY_IS_LINKED
   * to Boolean.TRUE if value requested is 'true' and Boolean.FALSE
   * if value requested is 'false'.  When you add links to a DDO, it
   * is important to set this property to notify DKDDO::add() and update()
   * methods that there could be links to be processed.  Otherwise 
   * add() and update() can skip processing link updates if the DDO
   * is not marked containing links.  This function does not check
   * the DDO for links and simply and blindly adds the property or updates
   * any existing value found.
   */
   public static void setPropertyIsLinked(DKDDO ddo,boolean value) throws DKUsageError{
       if(value){
           // If not exists, add it.
           // If already exists, set as Boolean(true)
           short propid = ddo.propertyId(DKConstantICM.DK_ICM_PROPERTY_IS_LINKED);
           if(propid <= 0){ // If not exists
               ddo.addProperty(DKConstantICM.DK_ICM_PROPERTY_IS_LINKED,Boolean.TRUE);
           }else{ // Else update existing
               ddo.setProperty(propid,Boolean.TRUE);
           }
       }else{ // Oterwise if new behavior, also mark as 'false' if none found.
           // If not exists, add it.
           // If already exists, set as Boolean(true)
           short propid = ddo.propertyId(DKConstantICM.DK_ICM_PROPERTY_IS_LINKED);
           if(propid <= 0){ // If not exists
               ddo.addProperty(DKConstantICM.DK_ICM_PROPERTY_IS_LINKED,Boolean.FALSE);
           }else{ // Else update existing
               ddo.setProperty(propid,Boolean.FALSE);
           }
       }//end if(value)
    }//end setPropertyIsLinked()
	   
    /**
    * Remove the specified link from the DDO's Link Collections, based on Source & Target 
    * Item ID.  It does not have to be the same exact DKLink object reference, but instead
    * just describte the link to remove.  String comparisons of the Source & Target DDOs to 
    * find it in the lists.  Please not that this function is heavy with string compares.
    * The DKDDO.update() function must still be used to make the changes persistent.
    *
    * @param dklinkToRemove  Link to remove from the DDO, based on Source & Target Item IDs, not DKLink object reference.
    * @param ddo             DDO to remove the link from.
    * @return Returns true if the Link was found and removed, false if not found.
    */
    public static boolean removeLinkFromDDO(DKLink dklinkToRemove, DKDDO ddo) throws Exception{

        String linkSourcePid;
        String linkTargetPid;
        String linkToRemoveSourcePid = dklinkToRemove.getSource().getPidObject().pidString();
        String linkToRemoveTargetPid = dklinkToRemove.getTarget().getPidObject().pidString();

        // Determine if DKLinkCollection for this Link Type exists, and if so, obtain it.
        short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_LINK,dklinkToRemove.getTypeName()); // Get the data id of the collection
        if(dataid <= 0){    // since the end goal is a DDO without this DKLink in it, simply return.
            return(false);  // if no Link Collection was found for this Link Type, then the link cannot be found to be removed.
        }
        DKLinkCollection linkCollection = (DKLinkCollection) ddo.getData(dataid);  // Get the Link Collection.

        // Go through all links or until the desired link to be removed is found.
        int collectionSize = linkCollection.cardinality();
        if (collectionSize > 0) {  // if there are links in the link collection, continue searching.
            dkIterator iter = linkCollection.createIterator();
            while (iter.more()) {           // While there are still links to iterate through, continue
                DKLink link = (DKLink)iter.next(); // Obtain each link object from the individual collection
                linkSourcePid = link.getSource().getPidObject().pidString();
                linkTargetPid = link.getTarget().getPidObject().pidString();
                
                // Compare Pid Strings to determine if this is the link described.
                if(    (linkSourcePid.compareTo(linkToRemoveSourcePid)==0) // if it is the link we want to remove, remove it and exit
                    && (linkTargetPid.compareTo(linkToRemoveTargetPid)==0)
                ){
                    linkCollection.removeElementAt(iter);
                    return(true); // Exit now since it is found.
                }
            }//end while more
        }//end if any are in collection
        return(false); // Otherwise the link was not found.
    }//end removeLinkFromDDO

   /**
    * Obtain All Links within the DDO, combined from all Link Collections for each Link Type.
    * @param ddo  DDO to obtain all links from.
    * @return  ArrayList containing all of the DKLink objects in the entire DDO.
    */
    public static java.util.ArrayList<DKLink> getLinks(DKDDO ddo) throws Exception{
        
        // Veriable Declarations
        java.util.ArrayList<DKLink> linksList = new java.util.ArrayList<DKLink>();
        DKDatastoreICM dsICM = (DKDatastoreICM) ddo.getDatastore();
        if(dsICM==null) throw new Exception("DDO's Datastore Not Set.  DDO should be created using DKDatastoreICM.createDDO().");
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef(); // Get the datastore definition object from the connected datastore.

        // Obtain a list of all Link Type Names defined in the System.  These will be all of the possible DKLinkCollections that can exist.
        String[] possibleLinkTypeNames = dsDefICM.listLinkTypeNames(); // List of all Link Type Names Possible in the System.

        // For each possible Link Type Name, check for it in the DDO.
        for (int i = 0; i < possibleLinkTypeNames.length; i++) {
            String linkTypeName = possibleLinkTypeNames[i]; // get a copy of the next name to check.
            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_LINK,possibleLinkTypeNames[i]);  // If 0 is returned, it does not exist in this DDO.
            if (dataid != 0){ // if it exists, then go through it.
                DKLinkCollection linkCollection = (DKLinkCollection) ddo.getData(dataid); // get the link Collection of this Link Type.

                // Go through all in that collection and get the link objects.
                int collectionSize = linkCollection.cardinality();
                if (collectionSize > 0) {      // if there are any items in the Link Collection, go through each.
                    dkIterator iter = linkCollection.createIterator(); // Create an iterator go go through all Inbound & Outbound Links.
                    while (iter.more()) {      // while there are items left to iteratate through, continue
                        DKLink link = (DKLink)iter.next(); // get each link object from the individual collection and add to our single list
                        linksList.add(link); // add link to our link list.
                    }//end while
	            }//end if collectionSize > 0
            }//end if dataid != 0
        }//end for all possible link types

        return(linksList);
    }//end getLinks

   /**
    * Obtain All Inbound Links within the DDO, combined from all Link Collections for each Link
    * Type.  The only difference between this and getLinks() is that this uses an Inbound
    * Iterator and checks for null returned from next();
    * @param ddo  DDO to obtain all Inbound Links from.
    * @return  ArrayList containing all of the DKLink objects in the entire DDO that would be considered "Inbound".
    */
    public static java.util.ArrayList<DKLink> getInboundLinks(DKDDO ddo) throws Exception{
        
        // Veriable Declarations
        java.util.ArrayList<DKLink> linksList = new java.util.ArrayList<DKLink>();
	    
        DKDatastoreICM dsICM = (DKDatastoreICM) ddo.getDatastore();
        if(dsICM==null) throw new Exception("DDO's Datastore Not Set.  DDO should be created using DKDatastoreICM.createDDO().");
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef(); // Get the datastore definition object from the connected datastore.

        // Obtain a list of all Link Type Names defined in the System.  These will be all of the possible DKLinkCollections that can exist.
        String[] possibleLinkTypeNames = dsDefICM.listLinkTypeNames(); // List of all Link Type Names Possible in the System.

        // For each possible Link Type Name, check for it in the DDO.
        for (int i = 0; i < possibleLinkTypeNames.length; i++) {
            String linkTypeName = possibleLinkTypeNames[i]; // get a copy of the next name to check.
            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_LINK,possibleLinkTypeNames[i]);  // If 0 is returned, it does not exist in this DDO.
            if (dataid != 0){ // if it exists, then go through it.
                DKLinkCollection linkCollection = (DKLinkCollection) ddo.getData(dataid); // get the link Collection of this Link Type.

                // Go through all in that collection and get the link objects.
                int collectionSize = linkCollection.cardinality();
                if (collectionSize > 0) { // if there are any items in the Link Collection, go through each.
                    dkIterator iter = linkCollection.createInboundIterator(); // Create an Inbound Iterator go go through all Inbound Links.
                    while (iter.more()) { // while there are items left to iteratate through, continue
                        DKLink link = (DKLink)iter.next(); // get each link object from the individual collection and add to our single list
                        if(link==null)    // if null is returned from next() when dealing with Inbound Iterators, it means that there are not more Inbound Links.
                            break;
                        linksList.add(link); // add link to our link list.
                    }//end while
	            }//end if collectionSize > 0
            }//end if dataid != 0
        }//end for all possible link types

        return(linksList);
    }//end getInboundLinks

   /**
    * Obtain All Outbound Links within the DDO, combined from all Link Collections for each 
    * Link Type.  The only difference between this and getLinks() is that this uses an Outbound
    * Iterator and checks for null returned from next();
    * @param ddo  DDO to obtain all Inbound Links from.
    * @return  ArrayList containing all of the DKLink objects in the entire DDO that would be considered "Outbound".
    */
    public static java.util.ArrayList<DKLink> getOutboundLinks(DKDDO ddo) throws Exception{
        
        // Veriable Declarations
        java.util.ArrayList<DKLink> linksList = new java.util.ArrayList<DKLink>();
	    
        DKDatastoreICM dsICM = (DKDatastoreICM) ddo.getDatastore();
        if(dsICM==null) throw new Exception("DDO's Datastore Not Set.  DDO should be created using DKDatastoreICM.createDDO().");
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef(); // Get the datastore definition object from the connected datastore.

        // Obtain a list of all Link Type Names defined in the System.  These will be all of the possible DKLinkCollections that can exist.
        String[] possibleLinkTypeNames = dsDefICM.listLinkTypeNames(); // List of all Link Type Names Possible in the System.

        // For each possible Link Type Name, check for it in the DDO.
        for (int i = 0; i < possibleLinkTypeNames.length; i++) {
   	        String linkTypeName = possibleLinkTypeNames[i]; // get a copy of the next name to check.
   	        short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_LINK,possibleLinkTypeNames[i]);  // If 0 is returned, it does not exist in this DDO.
   	        if (dataid != 0){ // if it exists, then go through it.
   	   	        DKLinkCollection linkCollection = (DKLinkCollection) ddo.getData(dataid); // get the link Collection of this Link Type.

   	   	        // Go through all in that collection and get the link objects.
   	   	        int collectionSize = linkCollection.cardinality();
   	   	        if (collectionSize > 0) { // if there are any items in the Link Collection, go through each.
                    dkIterator iter = linkCollection.createOutboundIterator(); // Create an Inbound Iterator go go through all Inbound Links.
   	    	        while (iter.more()) {  // while there are items left to iteratate through, continue
   	                    DKLink link = (DKLink)iter.next(); // get each link object from the individual collection and add to our single list
   	                    if(link==null)  // if null is returned from next() when dealing with Outbound Iterators, it means that there are not more Outbound Links.
   	   	                    break;
   	                    linksList.add(link); // add link to our link list.
   	                }//end while
	            }//end if collectionSize > 0
   	   	    }//end if dataid != 0
   	    }//end for all possible link types

	    return(linksList);
    }//end getOutboundLinks

   /**
    * Retrieve all Link Content / Data / DDOs within every Link that aren't iteself.  This 
    * function goes through all links.  If the source it not itself, the source is retrieved 
    * with the specified retrieval options.  Similarly, if the target is not itself, the target
    * is retrieved with the specified retrieval options.  Lastly, if the descriptor is not null, 
    * it too is retrieved.
    *
    * Note that this example does not handle duplicate DDO references (when the same item
    * appears in more than one link reference.  Only the first occurrence of any given item
    * DDO is actually retrieved and duplicates are marked as failed (see DKRetrieveOptionsICM
    * for more information on success/fail indicators.
    *
    * @param ddo  DDO to retrieve all obtain all links from.
    * @param dkRetrieveOptions  Retrieval Options to use when retrieving the Link Contents.
    */
    public static void retrieveAllLinkContents(DKDDO ddo, DKRetrieveOptionsICM dkRetrieveOptions) throws Exception{
        
        DKDatastoreICM dsICM = (DKDatastoreICM) ddo.getDatastore();
        if(dsICM==null) throw new Exception("DDO's Datastore Not Set.  DDO should be created using DKDatastoreICM.createDDO().");
        DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef(); // Get the datastore definition object from the connected datastore.

        // Obtain a list of all Link Type Names defined in the System.  These will be all of the possible DKLinkCollections that can exist.
        String[] possibleLinkTypeNames = dsDefICM.listLinkTypeNames(); // List of all Link Type Names Possible in the System.

        // Create a collection of all DDOs to be retrieved.
        // Then use multi-item retrieve for optimized retrieval.
        dkCollection ddoColl = new DKSequentialCollection();
	    
        // For each possible Link Type Name, check for it in the DDO.
        for (int i = 0; i < possibleLinkTypeNames.length; i++) {
   		    String linkTypeName = possibleLinkTypeNames[i]; // get a copy of the next name to check.
            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_LINK,possibleLinkTypeNames[i]);  // If 0 is returned, it does not exist in this DDO.
            if (dataid != 0){ // if it exists, then go through it.
        	    DKLinkCollection linkCollection = (DKLinkCollection) ddo.getData(dataid); // get the link Collection of this Link Type.

                // Go through all in that collection.
                int collectionSize = linkCollection.cardinality();
                if (collectionSize > 0) { // if there are any items in the Link Collection, go through each.
                    dkIterator iter = linkCollection.createIterator();  // Access all links, Inbound & Outbound.

                    while(iter.more()){ // Go through all links in the list
                        DKLink dkLink = (DKLink) iter.next(); // Move pointer to next element & return that object.

                        DKDDO  source     = (DKDDO) dkLink.getSource();  
                        DKDDO  target     = (DKDDO) dkLink.getTarget();  
                        DKDDO  descriptor = (DKDDO) dkLink.getLinkItem(); // The optional descriptor.  Null if not specified.

                        // If the DDOs are not a reference to itself, retrieve them.
                        if(source!=ddo)
                            ddoColl.addElement(source);
                        if(target!=ddo)
                            ddoColl.addElement(target);
                        if(descriptor!=null)
                            ddoColl.addElement(descriptor);
                    }//end while
                }//end if collectionSize > 0
            }//end if dataid != 0
        }//end for all possible link types
	    
        // Now that we gathered all DDOs, if any DDOs were found in
        // links, then make one call to multi-item retrieve.
        if(ddoColl.cardinality()>0){
            dsICM.retrieveObjects(ddoColl,dkRetrieveOptions.dkNVPair());
        }
	    
    }//end retrieveAllLinkContents

}//end class SLinksICM              
