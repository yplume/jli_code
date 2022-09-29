/*
 * Created on Jan 06, 2012
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.io.BufferedReader;
import java.io.FileReader;
import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;

import java.io.*;
import java.util.*;

public class CopyOfPRHICMExport {

	public final String  OUTPUT_FILE_PATH   = "c:\\CMExport\\";
	
	public CopyOfPRHICMExport (String arg[]) {
		
		exportCM(arg);
	
	}
	
	public void exportCM (String arg[]) {
		String contractNum = null;
	    String itemType = arg[0];
       // String s = arg[1];
        //String e = arg[2];
    	try {
	    	//BufferedReader in = new BufferedReader(new FileReader(OUTPUT_FILE_PATH + "test1.txt"));
			//System.out.println("000"+in.readLine());
			//while ((contractNum = in.readLine()) != null) {
			//    System.out.println("contract#: "+contractNum);
			   
    	    //getImgage(itemType, s, e);
    	    getImgage(itemType, null, null);
			//}
		} catch (Exception ex) {
			
		}
	}
	
	public void makeFolder (int counter) {
		int packageNum = 1;
		if(counter>10) {
    		packageNum++;
    		File newPackages = new File("C:\\cmexport\\package"+packageNum);
    		newPackages.mkdir();
    	}
	}
	
	public void getImgage (String itemType, String startTS, String endTS) {
		
		DKDDO ddo = null;
		DKDDO ddoSub = null;
        DKResults results = null;
    
        //String queryString = "/" + itemType + "[@VERSIONID = latest-version(.) AND @CREATETS>=\"" + startTS + "\" AND @CREATETS<  \"" + endTS +"\"]";
        String queryString = "/" + itemType + "[@VERSIONID = latest-version(.)]";
        //String queryString = "/Contracts[@VERSIONID = latest-version(.) AND (@Contract_Number=\"" + contractNum + "\")]";
        DKNVPair parms[] = new DKNVPair[2];
        System.out.println("queryString = "+queryString);
        parms[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
        // Specify any Retrieval Options desired.  Default is ATTRONLY.
        parms[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
        // Evaluate the query, seting the results into (results)
        DKDatastoreICM dsICM = null;
        try {
            dsICM = new DKDatastoreICM();
         
            System.out.println("getImgage: create & connect to dsICM ");
            dsICM.connect("icmnlsdb","icmadmin","BigBlue1","");
            //dsICM.connect("icmnlsdb","administrator","icbc","");
            
            DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
            dkRetrieveOptions.resourceContent(true);
            dkRetrieveOptions.baseAttributes(true);
            
			DKAttrDefICM aDef;
			
            results = (DKResults) dsICM.evaluate(queryString, DKConstant.DK_CM_XQPE_QL_TYPE, parms);
            System.out.println("get results ... "+results.cardinality());
            
            //int itemsPerPackage = 10;
            
            //int packages = results.cardinality()/10;
            
           // System.out.println("mod packages="+packages);
            if (results == null) {
                System.out.println("getImgage: Error!!");
            }
            // Set up variables for progress monitoring screen
            if (results.cardinality() == 0) {
                System.out.println("getImgage: Could not find item. ");
            } else {
                dkIterator iter = results.createIterator();
                try {
                	 System.out.println("111");
                	 
                	BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH + "output.csv"));
                    int counter = 0;
                    
                    while (iter.more()) {
                    //while (packages>1 && iter.more()) {
        	            //packages = packages -1;
                    	System.out.println("22222");
                   	 
                    	String metaData = "";
                    	String dataValue ="";
                    	String itemID ="";
                    	String att ="";
                    	String subEntity ="";
                    	String subattName ="";
                    	String subattValue ="";
                    	
	                	counter++;
	                	//makeFolder(counter);
	                	
	                	
	                	DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
	    			    DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemType);
	    			    DKSequentialCollection allAttrs = (DKSequentialCollection) entityDef.listAllAttributes();
	    			    dkIterator attIter = allAttrs.createIterator();
	    			    
	                	ddo = (DKDDO) iter.next();
	                    ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
	                    short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
	                    String imageName = "";
	                    
	                    itemID = ((DKPidICM)ddo.getPidObject()).getItemId();
	        			while (attIter.more()) {
	        				aDef = (DKAttrDefICM)attIter.next();
	        		        att = "";
	        		        dataValue = "";
	        		        try {
	        		        	att = aDef.getName();
	        		        	System.out.println("att = "+att.toString());
	        		        	if (ddo.getDataByName(aDef.getName())!=null)
	        		        		dataValue = ddo.getDataByName(aDef.getName()).toString();
	        		        	
	        		        	metaData += att + " = " + dataValue + ",";
	        		        	System.out.println("metaData  att ="+metaData);
	    	        			System.out.println("Attribute counter ="+counter);
	    	        			

	        		        	if (att.equalsIgnoreCase("AccountNumber"))
	        		        		imageName = dataValue;
	        		        	if (att.equalsIgnoreCase("CheckNumber"))
	        		        		imageName += "_" + dataValue;
	        		        } catch (DKException exc) {
	        		        	
	        		        }
	        		        //metaData += att + " " + dataValue + ",";
	        			}
	        			//System.out.println("metaData  att ="+metaData);
	        			//System.out.println("counter ="+counter);
	        			
	        			//loop thru subcomponents
	        			//DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
	        			//DKItemTypeDefICM entityDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemType);
	        			DKSequentialCollection compTypes = (DKSequentialCollection) entityDef.listSubEntities();
	        			dkIterator iter0 = compTypes.createIterator();
	        			
	        			while (iter0.more()) { // list all sub-entities
	        				DKComponentTypeDefICM compType = (DKComponentTypeDefICM) iter0.next();
	        				subEntity = compType.getName();
	        				System.out.println("   subEntity =    "+ compType.getName() +": "+compType.getDescription() );
	        				
	        				DKSequentialCollection subattrColl = (DKSequentialCollection) compType.listAllAttributes();
	        				dkIterator iter3 = subattrColl.createIterator();
	        				while (iter3.more()) { // while there are still items in the list, continue
	        					DKAttrDefICM attr4 = (DKAttrDefICM) iter3.next(); // Move pointer to next element & return that object.
	        					subattName = attr4.getName();
	        					
	        						System.out.println("subattName="+subattName);
	        					
	        				}
	        				
	        				
	        				
	        				short dataid0 = ddo.dataId(DKConstant.DK_CM_NAMESPACE_CHILD,subEntity);
	                        DKChildCollection children = (DKChildCollection) ddo.getData(dataid0);
	                        dkIterator iter1 = children.createIterator(); // Create an iterator to go through Child Collection
	                        System.out.println("children.cardinality() ="+children.cardinality() +"++++");
	                        while(iter1.more()){ // list all sub-attributes for each entity
	                            DKDDO child = (DKDDO) iter1.next(); // Move pointer to next child & return that object.
	                       	 //	System.out.println("   child =    "+ child.getDataByName("SSName") );
	                            
	                            child.retrieve(DKConstant.DK_CM_CONTENT_ATTRONLY);
	                            if (child.getDataByName("ViewID")!=null)
	                            	System.out.println("   child =    "+ child.getDataByName("ViewID"));
	                
	                        }
	                	}
	        			
	        			
	                    if (dataid > 0) {
	                        DKParts dkParts = (DKParts) ddo.getData(dataid);
	                        if (dkParts.cardinality() == 0 || dkParts.cardinality() > 1) {
	                            // log an error
	                        }
	                        dkIterator iter3 = dkParts.createIterator();
	                        System.out.println("parts: "+dkParts.cardinality());
	                        int partCounter = 0;
	                        
	                        int read = 0;
	                    	byte[] bytes = new byte[1024];
	                        while (iter3.more()) {
	                            DKLobICM part = (DKLobICM) iter3.next();
	                            //part.retrieve(OUTPUT_FILE_PATH  + contractNum+".tiff",dkRetrieveOptions.dkNVPair());
	                            part.retrieve(dkRetrieveOptions.dkNVPair());
	                            
	                            OutputStream out = new FileOutputStream(new File(OUTPUT_FILE_PATH  + itemID +".tiff"));
	                            InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(),-1,-1); 
	                            System.out.println("get image ...");
	                            while ((read = inputStream.read(bytes)) != -1) {
	                        		out.write(bytes, 0, read);
	                        	}
	                         
	                        	inputStream.close();
	                        	out.flush();
	                        	out.close();
	                            
	                            System.out.println("image file path : " + OUTPUT_FILE_PATH  + itemID+".tiff");
	                            //	mime type must be pdf otherwise convertion from pdf to image will fail
	                           // if (!part.getMimeType().equalsIgnoreCase("image/tiff")) {			//"application/pdf"
	                            //    System.out.println("readImage: -Exception: Mime type = " + part.getMimeType());
	                           // }
	                            
	                        }
	                    } else {
	                        System.out.println("readImage: Could not find item.");
	                    }
	                    String newLine = System.getProperty("line.separator"); 

	                    metaData += "Image_Path " + OUTPUT_FILE_PATH  + itemID +".tiff"+ newLine; 
	                    System.out.println("before write metaData ="+metaData);
	                    bw.write(metaData);        
	                }
	                iter = null;
	                bw.flush();
	                bw.close();

                }catch (IOException ex){
                	
                }
                
            }
            //04/09/08 handle memory leak
            results = null;
            queryString = null;
            parms = null;

        } catch (DKException dke) {
            dke.printStackTrace();
            try {
            } catch (Exception e) {
                System.out.println(" Error Alert Email Exception =" + e.getMessage());
            }
        } catch (InstantiationException ie) {
            ie.printStackTrace();
            try {
            } catch (Exception e) {
                System.out.println(" Error Alert Email Exception =" + e.getMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
            } catch (Exception e) {
                System.out.println(" Error Alert Email Exception =" + e.getMessage());
            }
        } finally {
            if (dsICM != null) {
                try {
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("Error returning connection to the pool." + e.getMessage());
                }
            }
        }

	}
	public static void main (String arg []) {
			
		DKDatastoreICM dsICM = null;    
		DKResults results;
		DKDDO itemDDo = null;
		String querystring = null;
		String itemType = null;
		String contractNum = null;
		
	    try {
	    	CopyOfPRHICMExport export = new CopyOfPRHICMExport(arg);
	    	
	 		
	    } //end try
	    //-----------------------------------------------------
	    
	     catch (Exception exc) {
	     
	      exc.printStackTrace();
		} finally {
		  	
			if (dsICM != null) {
				try {
					
					dsICM = null;
				} catch (Exception e) { 
					System.out.println("[ICMGetSearchResultOnCM]Error returning connection to the pool." + e.getMessage());
				}
			}
		}	
	}
	
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
               // case DKConstantICM.DK_ICM_SEMANTIC_TYPE_BASETEXT:
               //     System.out.println("         Semantic Type:  Base Text");
               //     break;
               // case DKConstantICM.DK_ICM_SEMANTIC_TYPE_BASESTREAM:
               //     System.out.println("         Semantic Type:  Base Stream");
                //    break;
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
            System.out.println("                           Value:  "+ obj2String(value));
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
}
