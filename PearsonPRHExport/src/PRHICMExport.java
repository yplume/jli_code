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

public class PRHICMExport {

	public final String  OUTPUT_FILE_PATH   = "c:\\CMExport\\";
	
	public PRHICMExport (String arg[]) {
		
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
		if(counter>2) {
    		packageNum++;
    		File newPackages = new File("C:\\cmexport\\package"+packageNum);
    		newPackages.mkdir();
    	}
	}
	
	public void getImgage (String itemType, String startTS, String endTS) {
		
		DKDDO ddo = null;
		DKAttrDefICM aDef;
        DKResults results = null;
    	HashMap<Integer, ArrayList<String>> lineAtt = new HashMap<Integer, ArrayList<String>>();
    	
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
            
			
			
            results = (DKResults) dsICM.evaluate(queryString, DKConstant.DK_CM_XQPE_QL_TYPE, parms);
            System.out.println("Get total results: "+results.cardinality());
            
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
                	BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH + "output.csv"));
                    int counter = 0;
                    
                    while (iter.more()) {
                    	ArrayList<String> parentAttributes = new ArrayList<String>();
                    	String metaData = "";
                    	String dataValue ="";
                    	String itemID ="";
                    	String att ="";
                    	
	                	counter++;
	                	makeFolder(counter);
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
	        		        	parentAttributes.add(dataValue);
	        		        	metaData += att + " = " + dataValue + ",";
	        		        	System.out.println("metaData  att ="+metaData);
	    	        			System.out.println("Attribute counter ="+counter);
	    	        			
	        		        } catch (DKException exc) {
	        		        	
	        		        }
	        		        //metaData += att + " " + dataValue + ",";
	        			}
	        			
	        			getSubEntities(entityDef, ddo, parentAttributes, lineAtt);
	        			exportImage(dkRetrieveOptions, dataid, ddo, itemID);
	                    
	        			
	        			
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
	
	public void exportImage(DKRetrieveOptionsICM dkRetrieveOptions, short dataid, DKDDO ddo, String itemID) {
		
		try {
			
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
                    
                }
            } else {
                System.out.println("readImage: Could not find item.");
            }
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
	    } 
    }
	public void getChild(String[][] childrenValues, int countChildren, DKChildCollection children, String subattName, ArrayList<String> parentAttributes, HashMap<Integer, ArrayList<String>> lineAtt) {
		int count = 1;
		String childValue = null;
		try {
			dkIterator iter1 = children.createIterator(); // Create an iterator to go through Child Collection
	        System.out.println("children.cardinality() ="+children.cardinality() +"++++");
	        while(iter1.more()){ // list all sub-attributes for each entity
	            DKDDO child = (DKDDO) iter1.next(); // Move pointer to next child & return that object.
	       	 //	System.out.println("   child =    "+ child.getDataByName("SSName") );
	            ArrayList tempAL = new ArrayList();
	            System.out.println(" parentAttributesALLL="+parentAttributes);
	            tempAL.add(parentAttributes);
	            child.retrieve(DKConstant.DK_CM_CONTENT_ATTRONLY);
	            if (child.getDataByName(subattName)!=null) {
	            	System.out.println("   childdddddddddddddddddddd =    "+ child.getDataByName(subattName));
	            	System.out.println("   lineAtt.size() =    "+ lineAtt.size());
	            	childValue = child.getDataByName(subattName).toString();
	            	tempAL.add(childValue);
	            	//childrenValues[countChildren][count] = childValue;
	            	
	            	System.out.println("count="+count);
	            	System.out.println("countChildren="+countChildren);
	            	
            		if (countChildren>1){
	            		if (lineAtt.size()<count){
	            			lineAtt.put(count, tempAL);
	            			System.out.println("add lineAtt1");
	            		}else{	
	            			lineAtt.get(count).add(childValue);
	            			System.out.println("lineAtt.get");
		            		
	            		}
	            	}else{
	            		lineAtt.put(count, tempAL);
	            		System.out.println("add lineAtt2");
	            		
	            	}
	            	System.out.println("add childValue to childrenValues="+childrenValues);
	            }
	            
	            //attributes[count][childValue]
	            //lineAtt.put(count, tempAL);
	            count++;
	            //parentAttributes = null;
	            tempAL = null;
	            //System.out.println("HashMap1="+lineAtt.get(1));
	            System.out.println("childrenValues="+childrenValues);
		    }
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
	    } 
    }
	
	public void getSubEntities(DKItemTypeDefICM entityDef, DKDDO ddo, ArrayList<String> parentAttributes, HashMap<Integer, ArrayList<String>> lineAtt) {
		String subEntity ="";
    	String subattName ="";
    	int hasC;

		try {
			DKSequentialCollection compTypes = (DKSequentialCollection) entityDef.listSubEntities();
			dkIterator iter = compTypes.createIterator();
			
			//compTypes.cardinality();
			//create a string[][] array to contain all children values
			String childrenValues [][] = new String[5][200];
			int countChildren = 1;//count for every sub att
			while (iter.more()) { // list all sub-entities
				DKComponentTypeDefICM compType = (DKComponentTypeDefICM) iter.next();
				subEntity = compType.getName();
				System.out.println("   subEntity =    "+ compType.getName() +": "+compType.getDescription() );
				
				DKSequentialCollection subattrColl = (DKSequentialCollection) compType.listAllAttributes();
				dkIterator iter1 = subattrColl.createIterator();
				
				while (iter1.more()) { // look thru each sub attribute
					DKAttrDefICM attr4 = (DKAttrDefICM) iter1.next(); // Move pointer to next element & return that object.
					subattName = attr4.getName();
					
					System.out.println("subattName="+subattName);
					//get value for each sub attribute
					short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_CHILD,subEntity);
                    DKChildCollection children = (DKChildCollection) ddo.getData(dataid);
                    
                    getChild(childrenValues, countChildren, children, subattName, parentAttributes, lineAtt);
                    
                    countChildren++;
				
				}
				for(int i=1; i<lineAtt.size(); i++) {
					ArrayList test = lineAtt.get(i);
					System.out.println("test="+test);
					
				}
        	}
			System.out.println("HashMap size="+lineAtt.size());
			
				
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
	    	PRHICMExport export = new PRHICMExport(arg);
	    	
	 		
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
	
}
