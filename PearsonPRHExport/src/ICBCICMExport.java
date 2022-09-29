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
import com.ibm.mm.sdk.server.DKDatastoreICM;
import java.io.*;
import java.util.*;

public class ICBCICMExport {

	public final String  OUTPUT_FILE_PATH   = "c:\\CMExport\\";
	
	public ICBCICMExport (String arg[]) {
		
		exportCM(arg);
	
	}
	
	public void exportCM (String arg[]) {
		String contractNum = null;
	    String itemType = arg[0];
        String s = arg[1];
        String e = arg[2];
    	try {
	    	//BufferedReader in = new BufferedReader(new FileReader(OUTPUT_FILE_PATH + "test1.txt"));
			//System.out.println("000"+in.readLine());
			//while ((contractNum = in.readLine()) != null) {
			//    System.out.println("contract#: "+contractNum);
			   
			    getImgage(itemType, s, e);
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
        DKResults results = null;
    
        String queryString = "/" + itemType + "[@VERSIONID = latest-version(.) AND @CREATETS>=\"" + startTS + "\" AND @CREATETS<  \"" + endTS +"\"]";
        //String queryString = "/JTest[@VERSIONID = latest-version(.) AND (@ZipCode=\"" + contractNum + "\")]";
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
            dsICM.connect("icmnlsdb","administrator","icbc","");
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
                	BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH + "output.txt"));
                    int counter = 0;
                    
                    while (iter.more()) {
                    //while (packages>1 && iter.more()) {
        	            //packages = packages -1;
	                	String metaData = "";
	                	String dataValue ="";
	                	String att ="";
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
	        			while (attIter.more()) {
	        				aDef = (DKAttrDefICM)attIter.next();
	        		        att = "";
	        		        dataValue = "";
	        		        try {
	        		        	att = aDef.getName();
	        		        	dataValue = ddo.getDataByName(aDef.getName()).toString();
	        		        	if (att.equalsIgnoreCase("AccountNumber"))
	        		        		imageName = dataValue;
	        		        	if (att.equalsIgnoreCase("CheckNumber"))
	        		        		imageName += "_" + dataValue;
	        		        } catch (DKException exc) {
	        		        	
	        		        }
	        		        metaData += att + " " + dataValue + ",";
	        			}
	        			System.out.println("metaData  att ="+metaData);
	        			System.out.println("counter ="+counter);
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
	                            
	                            OutputStream out = new FileOutputStream(new File(OUTPUT_FILE_PATH  + imageName +".tiff"));
	                            InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(),-1,-1); 
	                            System.out.println("get image ...");
	                            while ((read = inputStream.read(bytes)) != -1) {
	                        		out.write(bytes, 0, read);
	                        	}
	                         
	                        	inputStream.close();
	                        	out.flush();
	                        	out.close();
	                            
	                            System.out.println("image file path : " + OUTPUT_FILE_PATH  + imageName+".tiff");
	                            //	mime type must be pdf otherwise convertion from pdf to image will fail
	                           // if (!part.getMimeType().equalsIgnoreCase("image/tiff")) {			//"application/pdf"
	                            //    System.out.println("readImage: -Exception: Mime type = " + part.getMimeType());
	                           // }
	                            
	                        }
	                    } else {
	                        System.out.println("readImage: Could not find item.");
	                    }
	                    String newLine = System.getProperty("line.separator"); 

	                    metaData += "Image_Path " + OUTPUT_FILE_PATH  + imageName +".tiff"+ newLine; 
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
	    	ICBCICMExport export = new ICBCICMExport(arg);
	    	
	 		
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
