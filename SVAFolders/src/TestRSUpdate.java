


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

Updating Resource Items
    Updating Resource Items for Resource Content is almost identical to
    creating them, except the "update()" method is used instead of "add()".
    Updating Resource Items for non-resource Content may be found in
    SItemRetrievalICM.
    
    Resource Content may be updated using any of the methods outlined in 
    SResourceItemCreationICM or SResourceItemRetrievalICM, regardless of the 
    original method used.
    
    Note: Update content from file or stream offers advantages over loading
    into memory first unless you need the resource content to remain
    loaded in memory in the DKLobICM for ongoing reference.  Especially
    when saving a number of resource items or very large content,
    store directly from stream or file instead of consuming memory
    for the life of the DKLobICM instance.  Storing in memory
    when no longer needed consumes unnecessary memory and can
    cause you to exceed the available memory in the JVM. 

*******************************************************************************/

// Imports
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;

import java.io.*;

/************************************************************************************************
 *          FILENAME: SResourceItemUpdateICM.java
 *                    ---------------------------------------------------------------------------
 *       DESCRIPTION: Updating Resource Items.
 *                    ---------------------------------------------------------------------------
 *     DEMONSTRATION: Updating Resource Items
 *                    Updating, Storing Directly from File
 *                    Updating, Storing Directly from Stream
 *                    Updating Using Content in Local Memory
 *                    ---------------------------------------------------------------------------
 * COMMANDLINE USAGE: java SResourceItemUpdateICM <database> <userName> <password>
 *                    ---------------------------------------------------------------------------
 *     PREREQUISITES: The Data Model must be defined.  If needed please run the following Samples  
 *                         - SAttributeDefinitionCreationICM
 *                         - SAttributeGroupDefCreationICM
 *                         - SReferenceAttrDefCreationICM
 *                         - SItemTypeCreationICM
 *                    The Item Types must be set to use the specified default Resource Manager.
 *                    Plese run the following samples.
 *                         - SResourceMgrDefSetDefaultICM
 *                         - SSMSCollectionDefSetDefaultICM
 *                    NOTE:  Sample SSampleModelBuildICM pulls together all of the above.  It 
 *                           must be edited first for the RM Confiuration information.
 *                    ---------------------------------------------------------------------------
 *    FILES REQUIRED: SConnectDisconnectICM.java
 *                    SResourceItemICM_Document1.doc
 *                    SResourceItemICM_Document2.doc
 *                    SResourceItemICM_Text1.txt
 *                    SResourceItemICM_Text2.txt
 *                    SResourceItemICM_Image1.bmp
 *                    SResourceItemICM_Image2.bmp
 ************************************************************************************************/
public class TestRSUpdate{

    // Using constants for File Names
    public static final String LOB_FILE1_FILENAME         = "SResourceItemICM_Document1.txt";
    public static final String LOB_FILE2_FILENAME         = "SResourceItemICM_Document2.txt";
    public static final String TEXT_FILE1_FILENAME        = "SResourceItemICM_Text1.txt";
    public static final String TEXT_FILE2_FILENAME        = "SResourceItemICM_Text2.txt";
    public static final String IMAGE_FILE1_FILENAME       = "SResourceItemICM_Picture1.bmp";
    public static final String IMAGE_FILE2_FILENAME       = "SResourceItemICM_Picture2.bmp";
    
    //-------------------------------------------------------------
    // Main
    //-------------------------------------------------------------
    /**
     * Run the Sample.
     * @param argv[] String Array containing arguments.  Optional arguments are <databse> <userName> <password>.
     */
    public static void main(String argv[]) throws DKException, Exception{
    
        // Defaults for connecting to the database.
        String database = "icmnlsdb";
        String userName = "icmadmin";
        String password = "Bigblue1";
        
        //------------------------------------------------------------
        // Checking for input parameters
        //--------------------------------------------------------------
        if (argv.length < 3) { // if not all 3 arguments were specified, use defaults and report correct usage.
            System.out.println("Usage: " );
            System.out.println("  java SResourceItemUpdateICM <database> <userName> <password>" );
            System.out.println("  *** Some parameters not specified, using defaults..." );
            System.out.println("");
        } else {  // otherwise enough parameters were specified, use the first 3 for the 3 parameters.
            if (argv.length > 0) database = argv[0];
            if (argv.length > 1) userName = argv[1];
            if (argv.length > 2) password = argv[2];
        }//end else
        String ver = "1.0";
        
        System.out.println("===========================================");
        System.out.println("IBM DB2 Content Manager                v"+ver);
        System.out.println("Sample Program:  SResourceItemUpdateICM");
        System.out.println("-------------------------------------------");
        System.out.println(" Database: "+database);
        System.out.println(" UserName: "+userName);
        System.out.println("===========================================");
        
        DKDatastoreICM dsICM = null;
        try{
        	dsICM = new DKDatastoreICM();
            //dsICM.connect("icmnlsdb", "icmadmin", "BigBlue1", "SCHEMA=ICMADMIN");
            dsICM.connect("icmnlsdb", "icmadmin", "Bigblue1", "SCHEMA=ICMADMIN");
            
            // Get the datastore extension object
            //
            //dsExtICM = (DKDatastoreExtICM) dsICM.getExtension(DKConstant.DK_CM_DATASTORE_EXT);

            // Set query params
            //
 //           DKResults results =null;
            
            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            
            DKNVPair options[] = new DKNVPair[3];
            options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0");                                            // Max number of search results. 0 means no max.
            options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_IDONLY));   // Retrieve only item id from server
            options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);                                           // Must mark the end of the NVPair
            // Search for ALL documents in source item-type
            //
            dkRetrieveOptions.resourceContent(true);
            
            String sDDO = "NOINDEX";
            //String query = "/" + sDDO;
            //String query = "/" + sDDO +"[@itemid =\"A1001001A19D30B33543E00000\" or itemid=\"A1001001A19D30B33554G00002\"]";
            String query = "/" + sDDO +"[@SOURCE=\"test124\"]";
            
            
            dkResultSetCursor cursor = dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
            
            dkIterator iter = null;
 
            	
            ////////////////////Move Items//////////////////
            //DKDDO ddoTarget = dsICM.createDDO("OSR", DKConstant.DK_CM_DOCUMENT);
            System.out.println("Create target DDO");
            DKAttrDefICM aDef;
            Object aObject;
            String attrName = null;
            String attrValue = null;
            System.out.println("retrive source DDO");
            
            DKDDO  itemDDO;
            /*itemDDO.retrieve();
			DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
            DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity("OSR");
            DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
            iter = allAttrs.createIterator();
            System.out.println("retrive source DDO att");*/
            
            while ( (itemDDO = cursor.fetchNext()) != null){ // for all attributes of the item
				// create an empty ddo
            	//DKDDO ddoTarget = dsICM.createDDO(tDDO, DKConstant.DK_CM_DOCUMENT);
				
				itemDDO.retrieve();
				DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
 	            DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity(sDDO);
 	            DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
 	            iter = allAttrs.createIterator();
 	            //DKSequentialCollection compTypes = (DKSequentialCollection) entityDef.listSubEntities();
				//dkIterator iter2 = compTypes.createIterator();
				
                //String[] subentity = null;
                //int numSubAttr = 0;
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
        	
            //-------------------------------------------------------------
            // Connect to datastore
            //-------------------------------------------------------------
            // See Sample SConnectDisconnectICM for more information
            System.out.println("Connecting to datastore (Database '"+database+"', UserName '"+userName+"')...");

                DKDatastoreICM dsICM = new DKDatastoreICM();  // Create new datastore object.
                dsICM.connect(database,userName,password,""); // Connect to the datastore.

            System.out.println("Connected to datastore (Database '"+dsICM.datastoreName()+"', UserName '"+dsICM.userName()+"').");

            //-------------------------------------------------------------
            // Create Resource Object for Retrieval Demonstration
            //-------------------------------------------------------------
            // In order to demonstrate Updating of Resource Items, a few
            // Resource Items must first be created.
            System.out.println("Creating Resource Objects...");

                DKLobICM     lob = (DKLobICM)   dsICM.createDDO("NOINDEX",   DKConstant.DK_CM_DOCUMENT); // May be any semantic type.
                DKTextICM   text = (DKTextICM)  dsICM.createDDO("NOINDEX",  DKConstant.DK_CM_ITEM);     // Resource Type determined by XDO Classification
                DKImageICM image = (DKImageICM) dsICM.createDDO("NOINDEX", DKConstant.DK_CM_ITEM);     // in the Item Type Definition.

                // Set Mime Type / Type of Content
                lob.setMimeType("application/msword");
                text.setMimeType("text/plain");
                image.setMimeType("image/bmp");
                
                // Store Resource Content & Save Resource Item
                lob.add("SResourceItemICM_Document1.doc");  // Add it to the datastore,
                text.add("SResourceItemICM_Text1.txt");     // storing the content from
                image.add("SResourceItemICM_Picture1.bmp"); // the specified file.

            System.out.println("Created Resource Objects.");

            //-------------------------------------------------------------
            // Checking Out / Locking for Update
            //-------------------------------------------------------------
            System.out.println("Checking Out / Locking Resource Item for Update...");

                dsICM.checkOut(lob);
                dsICM.checkOut(text);
                dsICM.checkOut(image);
        
            System.out.println("Checked Out / Locked Resource Item for Update.");

            //-------------------------------------------------------------
            // Store Content Directly From a File
            //-------------------------------------------------------------
            // In one operation, update the item along with the specified content.
            System.out.println("Updating Item, Storing Content Directly From File...");

                lob.update("SResourceItemICM_Document2.doc");  // Update it in the datastore,
                text.update("SResourceItemICM_Text2.txt");     // storing the content from
                image.update("SResourceItemICM_Picture2.bmp"); // the specified file.

            System.out.println("Updated Item, Storing Content Directly From File.");

            //-------------------------------------------------------------
            // Create Streams
            //-------------------------------------------------------------
            // In order to store from a stream, we must first create a stream
            // for this sample to use.  The following will create streams from
            // the content files.
            System.out.println("Creating Streams...");

                // First, let's get a stream that we can use
                InputStream iStream1 = new FileInputStream("SResourceItemICM_Document1.doc");
                InputStream iStream2 = new FileInputStream("SResourceItemICM_Text1.txt");
                InputStream iStream3 = new FileInputStream("SResourceItemICM_Picture1.bmp");
                // Determine the length of the file
                RandomAccessFile file1 = new RandomAccessFile("SResourceItemICM_Document1.doc","r");
                RandomAccessFile file2 = new RandomAccessFile("SResourceItemICM_Text1.txt","r");
                RandomAccessFile file3 = new RandomAccessFile("SResourceItemICM_Picture1.bmp","r");
                long stream1Length = file1.length();
                long stream2Length = file2.length();
                long stream3Length = file3.length();

            System.out.println("Created Streams.");
            
            //-------------------------------------------------------------
            // Update Content Directly From a Stream
            //-------------------------------------------------------------
            // In one operation, store content from a stream while saving 
            // to the persistent datastore
            System.out.println("Updating Item, Storing Content Directly From a Stream...");

                lob.update(iStream1,stream1Length);  // Now add to datastore,
                text.update(iStream2,stream2Length); // storing the content from
                image.update(iStream3,stream3Length);// the specified stream

            System.out.println("Updated Item, Stored Content Directly From a Stream.");

            //-------------------------------------------------------------
            // Load Content into Item's Local Memory.
            //-------------------------------------------------------------
            // Content may be loaded directly into the XDO's local memory.
            // When all modification is complete, it may then be updated in
            // the datastore. Note that this is not recommended for 
            // any application that has large files or a large number of users.
            System.out.println("Loading Content into Item's Local Memory...");
    
                lob.setContentFromClientFile("SResourceItemICM_Document2.doc"); // Load the file into memory.
                text.setContentFromClientFile("SResourceItemICM_Text2.txt");
                image.setContentFromClientFile("SResourceItemICM_Picture2.bmp");

            System.out.println("Loaded Content into Item's Local Memory.");

            //-------------------------------------------------------------
            // Update in Datastore With Content Already in Memory.
            //-------------------------------------------------------------
            // When all modification is complete, it may be updated in the
            // datastore.
            System.out.println("Updating in Datastore with Content Already in Memory...");

                lob.update();      // The content was already loaded into memory
                text.update();     // using the setContentFromClientFile(filename)
                image.update();    // function

            System.out.println("Updated in Datastore with Content Already in Memory.");

            //-------------------------------------------------------------
            // Checking In / Unlocking Items
            //-------------------------------------------------------------
            System.out.println("Checking In / Unlocking Items...");

                dsICM.checkIn(lob);
                dsICM.checkIn(text);
                dsICM.checkIn(image);
        
            System.out.println("Checked In / Unlocked Items.");

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
        //    SConnectDisconnectICM.printException(exc);  // Print the exception using the function listed below.
            throw(exc);
        } catch (Exception exc) {
         //   SConnectDisconnectICM.printException(exc);  // Print the exception using the function listed below.
            throw(exc);
        }
    }// end main

    //=================================================================
    // Wrapper Functions
    //=================================================================
    // The following are wrapper functions for functionality covered
    // in this sample.  These functions can be used by other samples.

    // No additional wrapper functions from this sample.

}//end class SResourceItemUpdateICM
              

