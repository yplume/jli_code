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
import java.math.BigDecimal;
import java.text.*;
import java.sql.Timestamp;
import java.text.Normalizer;

import org.apache.commons.io.FileUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class UnzipLegalFiles_Dev {

	FileInputStream inputStream = null;
	private static XMLReader m_pmxr = null;
	private static String extractFileName = "";
	private static String zipFileDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\LZipInput\\";
	private static String zipErrDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\LZiperror\\";
	//// static String zipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\LZipInput\\";
	////private static String zipErrDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\LZiperror\\";
	//private static String zipFileDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\ZipInput\\";
	//private static String zipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\tempZip\\";
	///private static String zipFileDir = "C:\\ZipTest\\LegalZipinput\\";
	///private static String zipErrDir = "C:\\ZipTest\\LZiperror\\";
	public UnzipLegalFiles_Dev(){
		System.out.println("------------------------------------------------------ Inside LUnzipFiles() constructor ---------------------------");
		UnzipFiles1();
	}
	public void UnzipFiles1() {
		
		try{
			
		System.out.println("Inside LUnzipFiles ...");
        //System.out.println("Calling Monitor Pre My WorkList: " +date);
		//String zipFileDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\ZipInput\\";
        
		String unzipFileDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\LegalZipOutput\\";
        
        File zipErrFile = new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\LegalzipErr\\");
        
        ////String unzipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\LegalZipOutput\\";
        
        ////File zipErrFile = new File("\\\\usncpdktmrtr3v\\ExportToKofax\\LegalzipErr\\");
		
		//String unzipFileDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\ZipOutput\\";
        
        //File zipErrFile = new File("\\\\usncpdktmrtr3v\\ExportToKofax\\LZiperror\\");
        
        ///String unzipFileDir = "C:\\ZipTest\\LegalZipOutput\\";
        
        ///File zipErrFile = new File("C:\\ZipTest\\LegalzipErr\\");
        
        //String movezipFileDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\movezipoutput\\";
        
        //String newDestDir = "c:/WD/NewJTestout/";
        String newDestDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\LOnlineImages\\";
        String newDestKofaxDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\";
        ////String newDestDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\LOnlineImages\\";
        ////String newDestKofaxDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\";
        //String newDestKofaxDir = "\\\\usncdvkfxcrtr2v\\ExportToKofax\\LTKofaxDir\\";
        //String newDestDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\";
        //String newDestKofaxDir = "\\\\usncpdktmrtr3v\\ExportToKofax\\";
      ///String newDestDir = "C:\\ZipTest\\LOnlineImages\\";
      ///String newDestKofaxDir = "C:\\ZipTest\\LExportToKofax\\";
        System.out.println("new network DestDir ="+newDestDir);
        
        FileUtils.copyDirectory(new File(zipFileDir), new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\LZipBackup\\"));
        ////FileUtils.copyDirectory(new File(zipFileDir), new File("\\\\usncpdktmrtr3v\\ExportToKofax\\LZipBackup\\"));
        //FileUtils.copyDirectory(new File(zipFileDir), new File("\\\\usncpdktmrtr3v\\ExportToKofax\\LZipBackup\\"));
        ///FileUtils.copyDirectory(new File(zipFileDir), new File("C:\\ZipTest\\LZipBackup\\"));
        
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
        	zipErrFile.renameTo(new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\logs\\")); 
        	deleteDirectory(zipErrFile);
        }	*/
        	System.out.println("delete zip file");
        	
	        deleteDirectory(new File(zipFileDir));
	        
	        System.out.println("delete unzipFileDir file");
		    
	        deleteDirectory(new File(unzipFileDir));
	        
	        //new File(unzipFileDir + extractFileName).delete();
	        
	        System.out.println("Finish Deleting folders.");
        //}else{
        //	new File(zipFileDir).renameTo(new File("\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
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
            System.out.println("deleteDirectory files = Lenght="+files.length + " " +directory.toString()+ " " + directory.toString().contains("output"));
            
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
	
   
    private  void parsezipfolder(String zipFilePath, String unzipFileDir, String newDestDir, String newDestKofaxDir){
    	
    	try{
	    	File zipPath = new File(zipFilePath);
	    	System.out.println("zipPath="+zipPath);
	    	System.out.println("destDir="+unzipFileDir);
	    	System.out.println("newDestDir="+newDestDir);
	    	System.out.println("newDestKofaxDir="+newDestKofaxDir);
	    	ArrayList<String[]> metaAL = new ArrayList();
	    	File[] fList = zipPath.listFiles();
	    	String zipFileName = "";
	    	String csvZipName = "";
	    	//for(int i=0; i<fList.length; i++)
	    	//locate xls file	
	    	for (File file1 : fList){
	    	
	    		zipFileName = file1.getName();
	    		
	    		csvZipName = zipFilePath + zipFileName;
	    		
	    		System.out.println("[UnzipLegalTracker]csvZipName= " + csvZipName);
	    		
	    		//check if xls or zipfile, if it's xls file then parse it
                if(csvZipName.trim().length()!=0 && (csvZipName.endsWith("csv") || csvZipName.endsWith("CSV"))) {
  	          	  
  	              //inputStream = new FileInputStream(new File(csvZipName));
              
	  	            try {
	  	            	BufferedReader br = new BufferedReader(new FileReader(csvZipName));
	  	            	String line;
	  	            	while ((line = br.readLine()) != null) {
	  	                  
	  	                  String[] splitText = line.split("\\|");
	  	                  System.out.println("[UnzipLegalTracker]splitText size = " + splitText.length);
		              	
	  	                  if(splitText[0].equals("L")){
		              		metaAL.add(splitText);
		              		System.out.println("[UnzipLegalTracker]metaAL lenght = " + (metaAL.size()));
		              		//System.out.println("[UnzipLegalTracker]AL = "  + (al.get(1)));
	  	                  }
	  	            	}
	  	            	br.close();
		  	         }catch(Exception exception){
		          		System.out.println("parsezipfolder read csv error: " + exception);
		  	         }
          		  
                }
	    	
	    	}
		    System.out.println("fList = "+fList.length);
	    	//String zipFileName = "";
	    	extractFileName = "";
	    	//if multiple zip files
	    	for (File file : fList){
	            if (file.isFile()){
	            	System.out.println("[UnzipLegalTracker]AL size = "  + ((metaAL.size())));
	            	System.out.println("[UnzipLegalTracker]AL = "  + metaAL.get(0));
	            	String[] test = (String[])metaAL.get(0);
	            	System.out.println("[UnzipLegalTracker]test = "  + test.length);
	            	System.out.println("[UnzipLegalTracker]AL1 = "  + test[1]);
	            	System.out.println("[UnzipLegalTracker]AL2 = "  + test[2]);
	            	System.out.println("[UnzipLegalTracker]AL3 = "  + test[3]);
	            	System.out.println("[UnzipLegalTracker]AL4 = "  + test[4]);
	            	System.out.println("[UnzipLegalTracker]AL5 = "  + test[test.length-1]);
	            	
	            	zipFileName = file.getName();
		    		
	            	csvZipName = zipFilePath + zipFileName;
		    		System.out.println("[UnzipLegalTracker]zipFileName = "  + zipFileName);
		    		System.out.println("[UnzipLegalTracker]xlszipName = "  + csvZipName);
	            	
		    		//check if xls or zipfile, if it's xls file then parse it
	                if(csvZipName.trim().length()!=0 && (csvZipName.endsWith("zip"))) {
	  	          	
		                System.out.println("zipFileName = "+zipFileName);
		                
		                int pos = zipFileName.lastIndexOf(".");
		                
		                extractFileName = zipFileName.substring(0, pos);
		                
		                System.out.println("extractFileName = "+extractFileName);
		                
		                //remove extract folder 2018-6-10-24
		                //unzip(zipFilePath +  zipFileName, unzipFileDir + extractFileName, newDestDir + extractFileName);
		                unzip(zipFilePath +  zipFileName, unzipFileDir + extractFileName, newDestDir );
		                
		                System.out.println("newDestDir extractFileName = "+ newDestDir );
			            findpdffiles(metaAL, unzipFileDir + extractFileName, newDestDir, newDestKofaxDir);
	                }
	            //}
	            /*
	            if(extractFileName.length()==0){
		            //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		            System.out.println("get time = "+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
		            extractFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()).toString();
		            
	            }*/
	            
	            
	            deleteDirectory(new File(unzipFileDir + extractFileName));
	            }
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

    public  void findpdffiles(ArrayList AL, String unzipFileDir, String newXMLDir, String newKofaxXMLDir){
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
    		System.out.println("fList.length="+fList.length);
	    	//search and find xmls file with same name pdfs	
    		if(AL!=null && AL.size()!=0) {
	    		for(int i=0; i<AL.size(); i++){
	    			
	    			String [] metaDate = null;
	        		metaDate = (String[])AL.get(i);
	        		fileName1 = metaDate[4].replace("/", "");
		    		System.out.println("fList = "+ fList[i]);
		    		//check if file is folders
		    		if (fList[i].isDirectory()) {
		    			System.out.println(fList[i] + " is directory....");
			    		
		    			findpdffiles(AL, fList[i].toString(), newXMLDir, newKofaxXMLDir);
		    	    	
		            } 
		    		
		    		int pos = 0;
		    		//fileName1 = fList[i].getName();
		    		//if(pos >= 0){
			    		//fileName1 = fileName1.substring(0, pos);
			            
			    		//System.out.println("count ="+i);
			    		System.out.println("fileName1="+fileName1);
			    		for(int k=0; k<fList.length; k++){
			    			int pos1 = 0;
			    			fileName2 = fList[k].getName(); 
			    			System.out.println("fileName2 ="+fileName2);
			    			pos1 = fileName2.lastIndexOf(".");
			    			
			    			boolean isPDF = fileName2.endsWith("pdf");
				    		pos = fileName1.lastIndexOf(".");
				    		
				    		if(isPDF && fileName2.endsWith(fileName1 + ".pdf")){
				    		  
				    			System.out.println("fileName22="+fileName2);
				    			//search for two same file names different extension, one .xml & .pdf
				    			//if(fileName2.equalsIgnoreCase(fileName1)){
				    				System.out.println("same file found!");
				    				//al.add(fileName2);
				    				//m_pmxr = new XMLReader();
				    				
				    				///////if found first 2 matches then add 3rd checking for attachment/////////
					    			String attachmentfileName = "";
					    			
					    			
					    			//boolean found3rd = false;
					    			System.out.println("fileName2==="+fileName2);
				    				
					    			ArrayList attachAL = new ArrayList();
						    		
					    			//move and create new cXML file in online image folder
					    			//new File(extractDirectory +"\\"+ fileName2 + ".txt").renameTo(new File(newXMLDir + "\\" + fileName2 +".txt"));
					    			
					    			//add cXML in the xml
					    			//attachAL.add(new File(newXMLDir + "\\" + fileName2 +".txt"));
					    	    	
					    			System.out.println("fileName1111111==="+extractDirectory + "\\" + fileName2);
				    				
					    			System.out.println("fileName2222222==="+newXMLDir + fileName2);
				    				
					    			File attachPDFFileName = new File(extractDirectory + "\\" + fileName2);
					    			attachPDFFileName.renameTo(new File(newXMLDir + fileName2));
					    			
				    	    		attachAL.add(attachPDFFileName);

				    				
				    	    		
				    	            //createNewXML(m_pmxr, newKofaxXMLDir + fileName2 + ".xml", newXMLDir +"\\"+ fileName2 + ".txt", attachAL);
				    	            ////////////////will open below do it 11/23//////////////////
				    	    		createNewXML(metaDate, newKofaxXMLDir + fileName1 + ".xml", newKofaxXMLDir, attachAL);
				    	        ///////////////////////////////////////////////
				    			}//end if
			    	
		    		}
		    	}
    		}else
    			System.out.println("------------MetaData ArrayList is empty!----------------");
    		
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
    
    public  void createNewXML(String[] metaData, String newKofaxXml, String KofaxXMLDir, ArrayList <File> pdfFileNameAL){
    //public static void createNewXML(XMLReader xmlreader, String unzipedXml, String newKofaxXml, String cXML, ArrayList <File> pdfFileNameAL){
    	
    	try{
    		File file = new File(newKofaxXml);
//    		File txtFile = new File(txt);
//    		
           // System.out.println("newKofaxXml  = "+newKofaxXml);	
        //System.out.println("unzipedXml = "+unzipedXml);	
	       // System.out.println("txtFile = "+txtFile);	
	       // System.out.println("extractFileName = "+extractFileName);	
	        	
        //xmlreader.readXMLConfig(cXML, extractFileName);
        //xmlreader.readXMLConfig(unzipedXml);
    	
    	//new File(unzipedXml).renameTo(new File(cXML));
    	
    	//add cXML in the xml
    	//pdfFileNameAL.add(new File(cXML));
    	//String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
    	//System.out.println("timeStamp = "+timeStamp);
    	//String InvoiceDateOld = xmlreader.getInvoiceDate();
    	
		
    	//if InvoiceSubmissionMethod not equal PaperViaICS
    		//try{
	    		//InvoiceDateOld = InvoiceDateOld.substring(0,InvoiceDateOld.indexOf("T"));
				//SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd"); // or "YYYY-MM-DDThh:mm:ss±0000"
				//Date date = inputFormat.parse(InvoiceDateOld);
				//SimpleDateFormat outputFormat = new SimpleDateFormat("mm/dd/yyyy");
				//InvoiceDateNew = outputFormat.format(date);
				//System.out.println("InvoiceDateNew = "+InvoiceDateNew);
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
			}
	    	
				
	    	payeeNum = xmlreader.getPayeeNum();
	    	
	    	if(payeeNum.indexOf("_")!=-1)
	    		payeeNum = payeeNum.substring(0,payeeNum.indexOf("_"));
			
	    	System.out.println("payeeNum subtring = "+payeeNum);
	    	
	    	String vendorName = xmlreader.getVendorName();
	    	String invNum = xmlreader.getInvoiceID();
	    	String payeeName = xmlreader.getPayeeName();
	    	String systemID = xmlreader.getSystemID();
	    	
	    	if(vendorName.contains("&")){
	    		vendorName = vendorName.replace("&","&amp;").toString().trim();
	    	}
	    	if(invNum.contains("&")){
	    		invNum = invNum.replace("&","&amp;").toString().trim();
	    	}
	    	if(payeeName.contains("&")){
	    		payeeName = payeeName.replace("&","&amp;").toString().trim();
	    	}
	    	
	    	*/
	    	//parse txt file with header/////////////
           /*
    		String line = null;
            
            BufferedReader inTxt = new BufferedReader(new InputStreamReader(new FileInputStream(txtFile)));

            //ignor 1st line
            System.out.println(inTxt.readLine());
            //if ((line = in.readLine()) != null) {
            //    headers = line.split("\\|");
            //}
            System.out.println("txtFile =  " + txtFile);
            String[] headers = (inTxt.readLine()).split("\\|");
            System.out.println("headers: " + headers[0]);
            System.out.println("headers: " + headers[1]);
            System.out.println("headers: " + headers[2]);
            int CLIENT_ID = 0,PO = 0,INVOICE_NUMBER = 0,INVOICE_DATE = 0,INVOICE_TOTAL = 0, count = 0;
            String InvDate = null;
            
            for(String header : headers) {
	            if(header.equals("CLIENT_ID")) {
	            	CLIENT_ID = count;
	            } else if (header.equals("INVOICE_NUMBER")) {
	            	INVOICE_NUMBER = count;
	            	
	            } else if (header.equals("INVOICE_DATE")) {
	            	INVOICE_DATE = count;
	            	
	            } else if (header.equals("INVOICE_TOTAL")) {
	            	INVOICE_TOTAL = count;
	            	
	            } else if (header.equals("PO_NUMBER")) {
	            	PO = count;
	            }
	            count++;
	            
            }
            String[] attributes = inTxt.readLine().split("\\|");
            
            String vendorNum 	= attributes[CLIENT_ID];
            String poNum 		= attributes[PO];
            String invoiceNum 	= attributes[INVOICE_NUMBER];
            String invoiceDate 	= attributes[INVOICE_DATE];
            String invoiceAmount= attributes[INVOICE_TOTAL];
            invoiceDate = invoiceDate.substring(0, 4) + "/" + invoiceDate.substring(4, 6) + "/" + invoiceDate.substring(6, 8);
           */

    		String invoiceDate = metaData[2].substring(0, 4) + "/" + metaData[2].substring(4, 6) + "/" + metaData[2].substring(6, 8);
            String invoiceAmount = "";
            double amount = convertDecimals(metaData[10]);
            invoiceAmount = String.format("%.2f", new BigDecimal(amount));
     	   
            System.out.println("invoiceDate = "+invoiceDate);
            System.out.println("invoiceAmount = "+invoiceAmount);
            
            
            ///////////////////////////////////
            
          
	    	
	    	String xmlBody = "<ImportSession>\n";
	    	StringBuilder sb = new StringBuilder(xmlBody); 
	    	
	    	sb.append("\t<Batches>\n");
	    	sb.append("\t\t<Batch BatchClassName=\"Legal Tracker\" Priority=\"1\">\n");
	    	/*
	    	if(systemID.equalsIgnoreCase("EU"))
				
	    		sb.append("\t\t<Batch BatchClassName=\"EU_Ariba_Online_Invoice\" Priority=\"1\">\n");
		   	
	    	else
	    		
	    		sb.append("\t\t<Batch BatchClassName=\"Ariba Invoice\" Priority=\"1\">\n");
	    	*/
	    	sb.append("\t\t\t<Documents>\n");
	    	
	    	for(int i = 0; i<pdfFileNameAL.size(); i++){
		    	sb.append("\t\t\t\t<Document FormTypeName=\"FT_LegalTracker\">\n");
				sb.append("\t\t\t\t\t<IndexFields>\n");
				//sb.append("\t\t\t\t\t\t<IndexField Name=\"Assignment Number\" Value=\"" + timeStamp + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Vendor Name\" Value=\"" + metaData[8].replace("&","&amp;").toString().trim() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Vendor Number\" Value=\"" + metaData[7].replace("&","&amp;").toString().trim() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Invoice Number\" Value=\"" + metaData[4].replace("&","&amp;").toString().trim() + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Invoice Amount\" Value=\"" + invoiceAmount + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Invoice Date\" Value=\"" + invoiceDate + "\"/>\n");
				sb.append("\t\t\t\t\t\t<IndexField Name=\"Original file name\" Value=\"" + extractFileName + "\"/>\n");
				
				sb.append("\t\t\t\t\t</IndexFields>\n");
				sb.append("\t\t\t\t\t<Pages>\n");
				
				//for(int i = 0; i<pdfFileNameAL.size(); i++){
				sb.append("\t\t\t\t\t\t<Page ImportFileName=\"" + "\\\\usncdvkfxcrtr2v\\ExportToKofax\\LOnlineImages\\" + pdfFileNameAL.get(i).getName().replace("&","&amp;").toString().trim()  + "\"/>\n");
				//sb.append("\t\t\t\t\t\t<Page ImportFileName=\"" + "\\\\usncpdktmrtr3v\\ExportToKofax\\LOnlineImages\\" + pdfFileNameAL.get(i).getName()  + "\"/>\n");
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
				File file1 = new File("\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\unzipfileserror2.txt");
	        	try{
					BufferedWriter output = new BufferedWriter(new FileWriter(file1));
					output.write(e.getMessage());
					output.close();
	        	}catch(Exception exception){
	        		System.out.println("unzipfileserror2: " + exception);
	        	}
			}*/
    	
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
    
    public double convertDecimals(String inputNumber){
		double f = 0;
		try{
     	   NumberFormat format = NumberFormat.getInstance(Locale.US);
     	   Number number = format.parse(inputNumber);
     	   f = number.doubleValue();
     	   
     	  
 	   } catch (ParseException e) {
 		   System.out.println("Number Format execption: "+e.getMessage());
 		   File file = new File("\\usncdvkfxcrtr2v\\ExportToKofax\\LZiperror\\xmlreaderror5_" + extractFileName + ".txt");
           new File("\\usncdvkfxcrtr2v\\ExportToKofax\\ZipInput\\" + extractFileName).renameTo(new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\LZiperror\\" + extractFileName + ".zip"));
           ///File file = new File("\\usncpdktmrtr3v\\ExportToKofax\\LZiperror\\xmlreaderror5_"+ extractFileName +".txt");
 		   ///new File("\\usncpdktmrtr3v\\ExportToKofax\\LZipInput\\" + extractFileName  + ".zip").renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\LZiperror\\" + extractFileName + ".zip"));
			try{
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(e.getMessage());
				output.close();
			}catch(Exception exception){
	    		System.out.println("exception5: " + exception);
	    	}
 	   }
 	   return  f;
		
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
                
                //fileName = fileName.replace(":", "_");
                
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
    public static void main (String[] arg){
    	UnzipLegalFiles_Dev uz = new UnzipLegalFiles_Dev();
    	
    	
    }
}