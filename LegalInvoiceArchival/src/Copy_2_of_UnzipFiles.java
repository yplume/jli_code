import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.*;
import java.util.*;
import java.io.FileNotFoundException;
import java.text.*;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;


public class Copy_2_of_UnzipFiles {

	private static XMLReader m_pmxr = null;
	private static String extractFileName = "";
	//private static String zipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\ZipInput\\";
	//private static String zipErrDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\";
	//private static String zipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\ZipInput\\";
	private static String zipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\tempZip\\";
	private static String zipErrDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\";
	public Copy_2_of_UnzipFiles(){
		System.out.println("Inside UnzipFiles() constructor");
		
	}
	public void UnzipFiles1() {
		
		try{
		System.out.println("Inside UnzipFiles ...");
        //System.out.println("Calling Monitor Pre My WorkList: " +date);
		//String zipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\ZipInput\\";
        
        String unzipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\ZipOutput\\";
        
        File zipErrFile = new File("\\\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\");
        
        //String movezipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\movezipoutput\\";
        
        //String newDestDir = "c:/WD/NewJTestout/";
        String newDestDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\OnlineImages\\";
        String newDestKofaxDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\";
        System.out.println("new network DestDir ="+newDestDir);
        parsezipfolder(zipFileDir, unzipFileDir, newDestDir, newDestKofaxDir);
        //try{
	        //FileUtils.moveDirectory(new File(zipFileDir), new File(movezipFileDir));
        
        System.out.println("zipErrprFile isDirectory() = "+zipErrFile.isDirectory());
        System.out.println("zipErrprFile length = "+zipErrFile.list().length);
	       
        /*if(zipErrFile.isDirectory() && zipErrFile.list().length > 0) {
        	System.out.println("Having error and copy Zipinput to Zipreprocess.");
        	FileUtils.copyDirectory(new File(zipFileDir), new File("c:/ZipTest/ZipBackup/"));
        	//new File(zipFileDir).renameTo(new File("c:/ZipTest/ZipBackup/"));
        	FileUtils.copyDirectory(zipErrFile, new File("c:/ZipTest/logs/"));
        	
        	System.out.println("Finish copy");
        	zipErrFile.renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\logs\\")); 
        	deleteDirectory(zipErrFile);
        }	*/
        	System.out.println("delete zip file");
        	
	        deleteDirectory(new File(zipFileDir));
	        
	        System.out.println("delete unzipFileDir file");
		    
	        deleteDirectory(new File(unzipFileDir));
	        
	        //new File(unzipFileDir + extractFileName).delete();
	        
	        System.out.println("Finish Deleting folders.");
        //}else{
        //	new File(zipFileDir).renameTo(new File("\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
        //}
	        
		} catch (Exception e) {
			System.out.println(e.getMessage());	
			File file = new File(zipErrDir + "UnzipFiles1_" + extractFileName + ".txt");
			new File(zipFileDir + extractFileName + ".zip").renameTo(new File(zipErrDir + extractFileName + ".zip"));
        	try{
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(e.getMessage());
				output.close();
        	}catch(Exception exception){
        		System.out.println("UnzipFiles1: " + exception);
        	}
		}	
    	
    }
	
    
	public static void deleteDirectory(File directory) {
    	System.out.println("delete directory files = "+directory);
    	try{
        if(directory.exists()){
            File[] files = directory.listFiles();
            System.out.println("deleteDirectory files = "+files.length +directory.toString()+ directory.toString().contains("output"));
            
            //if(directory.toString().contains("output") && files.length == 0)
            //	directory.delete();
            	
           // else{
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                	System.out.println("deleteDirectory file[i] = "+files[i]);
                    
                    if(files[i].isDirectory()) {
                    	System.out.println("deleteDirectory "+files[i]+" is directory --- "+(files.length == 0));
                        //if(files.length == 0)
                        //	files[i].delete();
                        //else
                        	deleteDirectory(files[i]);
                    }
                    else {
                    	System.out.println("deleteDirectory "+files[i]+" will be deleted");
                        files[i].delete();
                    }
                }
                if(directory.toString().contains("Output")){
	                System.out.println("Delete ZipOutput dirs ..."+directory);
	                directory.delete();
                }
            }   
           // }
        }
        System.out.println("Unzipped Folders are deleted in deleteDirectory");
        
    	} catch (Exception e) {
    		System.out.println(e.getMessage());	
    		File file = new File(zipErrDir + "deleteDirectory_" + extractFileName + ".txt");
    		new File(zipFileDir + extractFileName + ".zip").renameTo(new File(zipErrDir + extractFileName + ".zip"));
        	try{
    			BufferedWriter output = new BufferedWriter(new FileWriter(file));
    			output.write(e.getMessage());
    			output.close();
        	}catch(Exception exception){
        		System.out.println("deleteDirectory: " + exception);
        	}
    	}	
    }
	
   
    private static void parsezipfolder(String zipFilePath, String unzipFileDir, String newDestDir, String newDestKofaxDir){
    	
    	try{
	    	File zipPath = new File(zipFilePath);
	    	//System.out.println("zipPath="+zipPath);
	    	//System.out.println("destDir="+unzipFileDir);
	    	File[] fList = zipPath.listFiles();
	    	//for(int i=0; i<fList.length; i++)
	    		
		    //	System.out.println("fList="+fList[i]);
	    	String zipFileName = "";
	    	extractFileName = "";
	    	for (File file : fList){
	            if (file.isFile()){
	            	
	            	zipFileName = file.getName();
	            	
	                System.out.println("zipFileName = "+zipFileName);
	                
	                int pos = zipFileName.lastIndexOf(".");
	                
	                extractFileName = zipFileName.substring(0, pos);
	                
	                System.out.println("extractFileName = "+extractFileName);
	                
	                //remove extract folder 2018-6-10-24
	                //unzip(zipFilePath +  zipFileName, unzipFileDir + extractFileName, newDestDir + extractFileName);
	                unzip(zipFilePath +  zipFileName, unzipFileDir + extractFileName, newDestDir );
	                
	            }
	            if(extractFileName.length()==0){
		            //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		            System.out.println("get time = "+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		            extractFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()).toString();
		            //method 2 - via Date
		            //Date date = new Date();
		            //System.out.println(new Timestamp(date.getTime()));
	
		            //format timestamp
		            //System.out.println(sdf.format(timestamp));
	            }
	            
	            System.out.println("newDestDir extractFileName = "+ newDestDir );
	            findxmlfiles(unzipFileDir + extractFileName, newDestDir, newDestKofaxDir);
	            deleteDirectory(new File(unzipFileDir + extractFileName));
	        }	
    	} catch (Exception e) {
    		System.out.println(e.getMessage());	
    		File file = new File(zipErrDir + "parsezipfolder_" + extractFileName + ".txt");
    		System.out.println("zipErrDir & extractFileName = "+zipErrDir + extractFileName);	
			new File(zipFileDir + extractFileName + ".zip").renameTo(new File(zipErrDir + extractFileName + ".zip"));
        	try{
    			BufferedWriter output = new BufferedWriter(new FileWriter(file));
    			output.write(e.getMessage());
    			output.close();
        	}catch(Exception exception){
        		System.out.println("parsezipfolder: " + exception);
        	}
    	}	
    }

    public static void findxmlfiles(String unzipFileDir, String newXMLDir, String newKofaxXMLDir){
    	//ArrayList al = new ArrayList();
    	try{
    		File extractDirectory = new File(unzipFileDir);
    		System.out.println("newXMLDir = "+newXMLDir);
    		System.out.println("extractDirectory findxmlfiles = "+extractDirectory);
	    	System.out.println("extractFileName in findxmlfiles = "+extractFileName);
	    	//File[] fList0 = directory.listFiles();
	    	File[] fList = extractDirectory.listFiles();
	    	String fileName1 = "";
	    	String fileName2 = "";
	    	
	    	//for(int i=0; i<=fList.length; i++){
    		//System.out.println("fList.length="+fList.length);
	    	//search and find xmls file with same name pdfs	
	    	for(int i=0; i<fList.length; i++){
	    		
	    		System.out.println("fList = "+ fList[i]);
	    		//check if file is folders
	    		if (fList[i].isDirectory()) {
	    			System.out.println(fList[i] + " is directory....");
		    		
	    			findxmlfiles(fList[i].toString(), newXMLDir, newKofaxXMLDir);
	                 
	    	    	
	            } 
	    		
	    		int pos = 0;
	    		fileName1 = fList[i].getName();
	    		pos = fileName1.lastIndexOf(".");
	    		
	    		if(pos >= 0){
		    		fileName1 = fileName1.substring(0, pos);
		            
		    		//System.out.println("count ="+i);
		    		//System.out.println("fileName1="+fileName1);
		    		for(int k=i+1; k<fList.length-1; k++){
		    			int pos1 = 0;
		    			fileName2 = fList[k].getName(); 
		    			//System.out.println("fileName2 ="+fileName2);
		    			pos1 = fileName2.lastIndexOf(".");
		    			
		    			if(pos1 >= 0){
			    			fileName2 = fileName2.substring(0, pos1);
			               
			    			//System.out.println("fileName22="+fileName2);
			    			//search for two same file names different extension, one .xml & .pdf
			    			if(fileName2.equalsIgnoreCase(fileName1)){
			    				System.out.println("same file found!");
			    				//al.add(fileName2);
			    				m_pmxr = new XMLReader();
			    				
			    				/*ArrayList attachAL = new ArrayList();
			    				
			    				File cXML = new File(extractDirectory + "\\" + fileName2 +".xml");
			    			
			    				cXML.renameTo(new File(newXMLDir + "\\" + fileName2 +"_cXML.xml"));
			    				
			    				attachAL.add(new File(newXMLDir + "\\" + fileName2 +"_cXML.xml"));
				    			*/
			    				///////if found first 2 matches then add 3rd checking for attachment/////////
				    			String attachmentfileName = "";
				    			
				    			
				    			boolean found3rd = false;
				    			System.out.println("fileName2==="+fileName2);
			    				
				    			ArrayList attachAL = new ArrayList();
					    		
				    			//move and create new cXML file in online image folder
				    			new File(extractDirectory +"\\"+ fileName2 + ".xml").renameTo(new File(newXMLDir + "\\" + fileName2 +"_cXML.xml"));
				    			
				    			//add cXML in the xml
				    			attachAL.add(new File(newXMLDir + "\\" + fileName2 +"_cXML.xml"));
				    	    	
				    			File attachPDFFileName = new File(extractDirectory + "\\" + fileName2 +".pdf");
				    			attachPDFFileName.renameTo(new File(newXMLDir + fileName2 +".pdf"));
				    			
			    	    		attachAL.add(attachPDFFileName);
			    	    		
			    	    		for(int j=k+1; j<fList.length; j++){
				    	    		int pos2 = 0;
				    	    		int pos3 = 0;
				    	    		attachmentfileName = fList[j].getName();
				    	    		//System.out.println("attachmentfileName = "+attachmentfileName);
				    	    		//System.out.println("check attached pdf = "+fileName2 + "_attachments0");
				        			
				    	    		//pos = fileName1.lastIndexOf(".");
				    	            if(attachmentfileName.indexOf(fileName2 + "_attachments")!=-1){
				    	            	
				    	            	System.out.println("Found PDF attachment");
				    	            	
				    	            	pos2 = attachmentfileName.indexOf('_', attachmentfileName.indexOf("_attachments") + 1);
				    	            	//pos2 = attachmentfileName.indexOf("_attachments");
				    	            	
				    	            	pos3 = attachmentfileName.lastIndexOf(".");
				    	            	
				    	            	System.out.println("pos2 = "+pos2);
				    	            	//attachmentfileName = attachmentfileName.substring(0, pos2 + 13) + attachmentfileName.substring(pos3);
				    	            	attachmentfileName = attachmentfileName.substring(0, pos2) + attachmentfileName.substring(pos3);
				    	            	
				    	            	System.out.println("attachmentfileName = "+attachmentfileName);
				    	            	
				    	            	//xmlpdfFileName = attachmentfileName.substring(0,pos);
				    	            	
				    	            	//File attachPDFFileName = new File(extractDirectory + "\\" + xmlpdfFileName +".pdf");
				    	            	//File attachPDFFileName = new File(extractDirectory + "\\" + fileName2 +".pdf");
				    	            	
				    	            	System.out.println("attachPDFFileName = "+attachPDFFileName);
				    	            	//System.out.println("xmlpdfFileName = "+xmlpdfFileName);
				    	            	
				    	            	m_pmxr = new XMLReader();
				    	            	//attachPDFFileName.renameTo(new File(newXMLDir + xmlpdfFileName +".pdf"));
				    	            	//attachPDFFileName.renameTo(new File(newXMLDir + fileName2 +".pdf"));
				    	            	File newXMLDirectory = new File(newXMLDir + attachmentfileName);
				    	    			fList[j].renameTo(newXMLDirectory);
				    	    			//attachPDFFileName.renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\" + xmlpdfFileName +".pdf"));
				    	    			//fList[i].renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\" + attachmentfileName));
				    	    			System.out.println("fList[j] = "+fList[j]);
				    	    			
				    	            	found3rd = true;
				    	    			attachAL.add(newXMLDirectory);
				    	    			//createNewXML(m_pmxr, newKofaxXMLDir + fileName2 + ".xml", newXMLDir +"\\"+ fileName2 + "_cXML.xml", attachAL);
				    	            	
				    	            }
				    	            
				    			}
			    	            if(!found3rd){// if not found 3rd 
			    	            	/*File pdfFileName = new File(extractDirectory + "\\" + fileName2 +".pdf");
			    	            	System.out.println("extractDirectory = "+extractDirectory);
			    	            	System.out.println("fileName2 = "+fileName2);
			    	            	System.out.println("newXMLDir = "+newXMLDir);
			    	            	
			    	            	pdfFileName.renameTo(new File(newXMLDir + fileName2 +".pdf"));
			    	    			*/
			    	            	System.out.println("not found 3rd");
			    	            	//attachAL.add(pdfFileName);
			    	            	//createNewXML(m_pmxr, newKofaxXMLDir +"\\"+ fileName2 + ".xml", newXMLDir +"\\"+ fileName2 + "_cXML.xml", attachAL);
			    	            	//createNewXML(m_pmxr, newKofaxXMLDir + fileName2 + ".xml", newXMLDir +"\\"+ fileName2 + "_cXML.xml", attachAL);
			    	            }
			    	            createNewXML(m_pmxr, newKofaxXMLDir + fileName2 + ".xml", newXMLDir +"\\"+ fileName2 + "_cXML.xml", attachAL);
			    	            
			    	        ///////////////////////////////////////////////
			    			}//end if
			    						    			
		        		}
		        		
		    		}
		    	
	    		}
	    	}
    	} catch (Exception e) {
			System.out.println("findxmlfiles Exception: "+ e.getMessage());	
			File file = new File(zipErrDir + "unzipfileserror1_" + extractFileName + ".txt");
			System.out.println("zipErrDir & extractFileName = "+zipErrDir + extractFileName);	
			new File(zipFileDir + extractFileName + ".zip").renameTo(new File(zipErrDir + extractFileName + ".zip"));
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(e.getMessage());
				output.close();
        	}catch(IOException exception){
        		System.out.println("unzipfileserror1 IOException: " + exception.getMessage());
        	}
		}
    	
    }
    
    public static void createNewXML(XMLReader xmlreader, String newKofaxXml, String cXML, ArrayList <File> pdfFileNameAL){
    //public static void createNewXML(XMLReader xmlreader, String unzipedXml, String newKofaxXml, String cXML, ArrayList <File> pdfFileNameAL){
    	
    	try{
    	File file = new File(newKofaxXml);
        System.out.println("newKofaxXml  = "+newKofaxXml);	
        //System.out.println("unzipedXml = "+unzipedXml);	
        System.out.println("cXML = "+cXML);	
        System.out.println("extractFileName = "+extractFileName);	
    	
        xmlreader.readXMLConfig(cXML, extractFileName);
        //xmlreader.readXMLConfig(unzipedXml);
    	
    	//new File(unzipedXml).renameTo(new File(cXML));
    	
    	//add cXML in the xml
    	//pdfFileNameAL.add(new File(cXML));
    	//String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
    	//System.out.println("timeStamp = "+timeStamp);
    	String InvoiceDateOld = xmlreader.getInvoiceDate();
    	String InvoiceDateNew = "";
    	String payeeNum = "";
		
    	//if InvoiceSubmissionMethod not equal PaperViaICS
    	if(!xmlreader.getInvoiceSubmissionMethod().equalsIgnoreCase("PaperViaICS")){
	    	//try{
	    		InvoiceDateOld = InvoiceDateOld.substring(0,InvoiceDateOld.indexOf("T"));
				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd"); // or "YYYY-MM-DDThh:mm:ss±0000"
				Date date = inputFormat.parse(InvoiceDateOld);
				SimpleDateFormat outputFormat = new SimpleDateFormat("mm/dd/yyyy");
				InvoiceDateNew = outputFormat.format(date);
				System.out.println("InvoiceDateNew = "+InvoiceDateNew);
	    	/*}catch(Exception e){
				System.out.println(e.getMessage());
				File file2 = new File(zipFileDir +"unzipfileserror4_" + extractFileName + ".txt");
				new File(zipFileDir + extractFileName).renameTo(new File(zipErrDir + extractFileName + ".zip"));
				try{
					BufferedWriter output = new BufferedWriter(new FileWriter(file2));
					output.write(e.getMessage());
					output.close();
	        	}catch(Exception exception){
	        		System.out.println("unzipfileserror4: " + exception);
	        	}
			}*/
	    	
	    	payeeNum = xmlreader.getPayeeNum();
	    	
	    	if(payeeNum.indexOf("_")!=-1)
	    		payeeNum = payeeNum.substring(0,payeeNum.indexOf("_"));
			
	    	System.out.println("payeeNum subtring = "+payeeNum);
	    	
	    	String vendorName = xmlreader.getVendorName();
	    	String invNum = xmlreader.getInvoiceID();
	    	String payeeName = xmlreader.getPayeeName();
	    	
	    	if(vendorName.contains("&")){
	    		vendorName = vendorName.replace("&","&amp;").toString().trim();
	    	}
	    	if(invNum.contains("&")){
	    		invNum = invNum.replace("&","&amp;").toString().trim();
	    	}
	    	if(payeeName.contains("&")){
	    		payeeName = payeeName.replace("&","&amp;").toString().trim();
	    	}
	    	String xmlBody = "<ImportSession>\n";
	    	StringBuilder sb = new StringBuilder(xmlBody); 
	    	
	    	sb.append("\t<Batches>\n");
	    	sb.append("\t\t<Batch BatchClassName=\"Ariba Invoice\" Priority=\"1\">\n");
	    	sb.append("\t\t\t<Documents>\n");
	    	
	    	for(int i = 0; i<pdfFileNameAL.size(); i++){
		    	sb.append("\t\t\t\t<Document FormTypeName=\"FT_Ariba Invoice\">\n");
				sb.append("\t\t\t\t\t<IndexFields>\n");
				//sb.append("\t\t\t\t\t\t<IndexField Name=\"Assignment Number\" Value=\"" + timeStamp + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Vendor Name\" Value=\"" + vendorName + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Vendor Number\" Value=\"" + xmlreader.getVendorNum() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Invoice Number\" Value=\"" + invNum + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Invoice Amount\" Value=\"" + xmlreader.getInvoiceAmount() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Invoice Date\" Value=\"" + InvoiceDateNew + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"PO Number\" Value=\"" + xmlreader.getPONum() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Tax_Amount\" Value=\"" + xmlreader.getTax() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Buyer Email Address\" Value=\"" + xmlreader.getBuyerEmailAddress() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Payee_Name\" Value=\"" + payeeName + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Payee Number\" Value=\"" + payeeNum + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Payment Method\" Value=\"" + xmlreader.getPaymentMetod() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Partner Bank Type\" Value=\"" + xmlreader.getPartnerBankType() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"PST Tax\" Value=\"" + xmlreader.getPstTax() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"GST Tax\" Value=\"" + xmlreader.getGstTax() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"HST Tax\" Value=\"" + xmlreader.getHstTax() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"VAT Tax\" Value=\"" + xmlreader.getVatTax() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Invoice Currency\" Value=\"" + xmlreader.getCurrency() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Original file name\" Value=\"" + extractFileName + "\"/>\n");
				
				sb.append("\t\t\t\t\t</IndexFields>\n");
				sb.append("\t\t\t\t\t<Pages>\n");
				
				//for(int i = 0; i<pdfFileNameAL.size(); i++){
				sb.append("\t\t\t\t\t\t<Page ImportFileName=\"" + "\\\\usncpdktmrtr3v\\ExportToKofax\\OnlineImages\\" + pdfFileNameAL.get(i).getName()  + "\"/>\n");
				//}
				sb.append("\t\t\t\t\t</Pages>\n");
				sb.append("\t\t\t\t</Document>\n");
	    	}
			sb.append("\t\t\t</Documents>\n");
			sb.append("\t\t</Batch>\n");
			sb.append("\t</Batches>\n");
			sb.append("</ImportSession>\n");
			//try{
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(sb.toString());
				output.close();
				
				
			/*} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				File file1 = new File("\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\unzipfileserror2.txt");
	        	try{
					BufferedWriter output = new BufferedWriter(new FileWriter(file1));
					output.write(e.getMessage());
					output.close();
	        	}catch(Exception exception){
	        		System.out.println("unzipfileserror2: " + exception);
	        	}
			}*/
    	}else{
    		//delete the the pdf created
    		for(int i = 0; i<pdfFileNameAL.size(); i++){
    			pdfFileNameAL.get(i).delete();
    			System.out.println("pdfFileName deleted: "+pdfFileNameAL.get(i));
			}
    		//pdfFileName.delete();
    		
    	}
    	}catch(Exception e){
			System.out.println(e.getMessage());
			File file = new File(zipFileDir +"unzipfileserror2_" + extractFileName + ".txt");
			new File(zipFileDir + extractFileName + ".zip").renameTo(new File(zipErrDir + extractFileName + ".zip"));
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(e.getMessage());
				output.close();
        	}catch(Exception exception){
        		System.out.println("unzipfileserror2: " + exception);
        	}
		}
    }
    private static void unzip(String zipFilePath, String destDir, String newDir) {
    	
    	File dir = new File(destDir);
    	File newXMLDir = new File(newDir);
        
        //System.out.println("zipFilePath unzip="+zipFilePath);
        //System.out.println("destDir unzip ="+destDir);
        //System.out.println("newDir unzip="+newDir);
        
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        
        if(!newXMLDir.exists()) newXMLDir.mkdirs();
        
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                
                fileName = fileName.replace(":", "_");
                
                File newFile = new File(destDir + File.separator + fileName);
                //System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            File file = new File(zipFileDir +"unzipfileserror3_" + extractFileName + ".txt");
			new File(zipFileDir + extractFileName).renameTo(new File(zipErrDir + extractFileName + ".zip"));
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(e.getMessage());
				output.close();
        	}catch(Exception exception){
        		System.out.println("unzipfileserror3: " + exception);
        	}
        }        
    }
}