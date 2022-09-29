package com.imageaccesscorp.polo.prlreport;

import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.common.DKAttrDefICM;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKDatastoreDefICM;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKItemTypeDefICM;
import com.ibm.mm.sdk.common.DKLobICM;
import com.ibm.mm.sdk.common.DKNVPair;
import com.ibm.mm.sdk.common.DKParts;
import com.ibm.mm.sdk.common.DKResults;
import com.ibm.mm.sdk.common.DKRetrieveOptionsICM;
import com.ibm.mm.sdk.common.DKSequentialCollection;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.collections4.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.openxml4j.opc.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.DataFormatter;

public class CopyOfUploadServlet extends javax.servlet.http.HttpServlet implements Servlet
{
  String saveFile = "C:/temp/";
  public final String OUTPUT_FILE_PATH = "c:\\ExportInvImages\\";
  



  public CopyOfUploadServlet() {}
  



  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
  {
    doPost(req, resp);
  }
  

  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
  {
    String searchType = req.getParameter("action");
    System.out.println("searchType = "+searchType);
    if(searchType != null && searchType.equalsIgnoreCase("Search"))
    	attributeSearch(req);
    else
    	xlsSearch(req);
  }
  
  protected void attributeSearch(HttpServletRequest req)
  {
	  System.out.println("attributeSearch");
	  
	  String itemTypeValue = req.getParameter("itemtype");
	  String attribute = req.getParameter("attribute");
	  String attributeValue = req.getParameter("attvalue");
	  searchResults(itemTypeValue, attribute, attributeValue);
  }
  
  protected void xlsSearch(HttpServletRequest req)
  {
	  System.out.println("xlsSearch");
	  
	  try
	    {
	      boolean ismultipart = ServletFileUpload.isMultipartContent(req);
	      if (ismultipart)
	      {

	        FileItemFactory factory = new org.apache.commons.fileupload.disk.DiskFileItemFactory();
	        
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        
	        upload.setFileSizeMax(10000000L);
	        
	        List<FileItem> items = null;
	        System.out.println("Upload = " + upload);
	        try
	        {
	          items = upload.parseRequest(req);
	          
	          //Iterator itr = items.iterator();
	          String formField = null;
	          
	          String itemTypeValue = null;
	          
	          String attribute = null;
	          
	          String attributeValue = null;
	          
	          //while (itr.hasNext())
	          for (FileItem item : items) {

	            //FileItem item = (FileItem)itr.next();
	            System.out.println("Item = " + item);
	            
	            
	            if (item.isFormField()) {
		              formField = item.getFieldName();
		              System.out.println("formField = " + formField);
		              if (formField.equalsIgnoreCase("itemtype")) {
		                itemTypeValue = item.getString();
		                System.out.println("itemTypeValue = " + itemTypeValue);
		              }
		              if (formField.equalsIgnoreCase("attribute")) {
		                attribute = item.getString();
		                System.out.println("attribute = " + attribute);
		              }
		              /*if (formField.equalsIgnoreCase("attvalue")) {
		                attributeValue = item.getString();
		                System.out.println("attributeValue = " + attributeValue);
		              }*/
		              //getImgage(itemTypeValue, attribute, attributeValue);
	            } else {
		              String itemPath = item.getName();
		              System.out.println("itemPath = " + itemPath);
		              //String filename = FilenameUtils.getName(itemPath);
		              //System.out.println("filename = " + filename);
		              
	
		              item.write(new File("C:/temp/newXlS.xlsx"));
		              
	
		              FileInputStream inputStream = new FileInputStream(new File(itemPath));
		              
	              
	              try{
	              //NPOIFSFileSystem fs = new NPOIFSFileSystem(file); //used for .xls
	              //OPCPackage pkg = OPCPackage.open(new File("C:/temp/BOL1.xls"));
	              //System.out.println("pkg = " + pkg);
	              //HSSFWorkbook workbook = new HSSFWorkbook(fs.getRoot(), true);
	            	  Workbook wb = null;
	            	  if (itemPath.endsWith("xlsx")) {
	            	        wb = new XSSFWorkbook(inputStream);
	            	    } else if (itemPath.endsWith("xls")) {
	            	        wb = new HSSFWorkbook(inputStream);
	            	    } else {
	            	        throw new IllegalArgumentException("The specified file is not Excel file");
	            	    }

	              //Workbook wb = new XSSFWorkbook(file); 
	              System.out.println("wb = " + wb);
	              Sheet sh = wb.getSheetAt(0);  

	              //HSSFSheet sh = workbook.getSheetAt(0);
	              
	              System.out.println("Sheet = " + sh);
	              Iterator<Row> rowIterator = sh.rowIterator();
	              
	              System.out.println("rowIterator = " + rowIterator);
	              Iterator<Cell> cellIterator;
	              DataFormatter formatter = new DataFormatter();

	              for (; rowIterator.hasNext(); cellIterator.hasNext())
	              {
	                //HSSFRow row = (HSSFRow)rowIterator.next();
	            	Row nextRow = rowIterator.next();
	            	cellIterator = nextRow.cellIterator();
	            	System.out.println("cellIterator = " + cellIterator);
	                //cellIterator = row.cellIterator();
	            	Cell cell = cellIterator.next();
	                //continue;
	                
	                //HSSFCell cell = (HSSFCell)cellIterator.next();
	            	String val = formatter.formatCellValue(cell);

	                System.out.println("val = " + val);
	                

	                searchResults(itemTypeValue, attribute, val);
	              }
	              }catch(Exception ex){
	            	  System.out.println("err="+ex.getMessage());
	              }
	            }
	          }
	        }
	        catch (Exception localException) {}
	      }
	      
	      return;
	    } catch (Exception localException1) {}
	  
  }
  
  protected void searchResults(String itemType, String attribute, String attributeValue) { 
		DKDDO ddo = null;
	    DKResults results = null;
	    String folderName = null;
	    String queryString = "/" + itemType + "[@VERSIONID = latest-version(.) AND @" + attribute + "=\"" + attributeValue + "\"]";
	   
	    DKNVPair[] parms = new DKNVPair[2];
	    System.out.println("queryString = " + queryString);
	    parms[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
	    // Specify any Retrieval Options desired.  Default is ATTRONLY.
	    parms[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
	    
	    DKDatastoreICM dsICM = null;
	    try {
	      dsICM = new DKDatastoreICM();
	      
	      System.out.println("getImgage: create & connect to dsICM ");
	      dsICM.connect("icmnlsdb", "icmadmin", "BigBlue1", "");
	      DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
	      dkRetrieveOptions.resourceContent(true);
	      dkRetrieveOptions.baseAttributes(true);
	      
	
	      //results = (DKResults)dsICM.evaluate(queryString, (short)7, parms);
	      results = (DKResults)dsICM.evaluate(queryString, DKConstant.DK_CM_XQPE_QL_TYPE, parms);
	      
	      System.out.println("get results ... " + results.cardinality());
	   
	      if (results == null) {
	        System.out.println("getImgage: Could not find item. ");
	      } else {
	        dkIterator iter = results.createIterator();
	        try
	        {
	          BufferedWriter bw = new BufferedWriter(new FileWriter("c:\\ExportInvImages\\output.csv"));
	          String header = "Vendor Number,Vendor Name,Invoice Number,Invoice Number,Invoice Date,PO Number,Transaction,image path";
	          int counter = 0;
	          String metaData = "";
	          // Loop thru rows
	          while (iter.more())
	          {
	        	String imageName = "";
	            
	            String dataValue = "";
	            
	            counter++;
	            
	            DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
	             
	            DKItemTypeDefICM entityDef = (DKItemTypeDefICM)dsDefICM.retrieveEntity(itemType);
	             
	            DKSequentialCollection allAttrs = (DKSequentialCollection)entityDef.listAllAttributes();
	            
	            dkIterator attIter = allAttrs.createIterator();
	            
	            ddo = (DKDDO)iter.next();
	            ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
	            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
	           
	            dataValue = "";
	            // Loop thru each attribute of a line item
	            while (attIter.more()) {
	              DKAttrDefICM aDef = (DKAttrDefICM)attIter.next();
	              
	              try {
	            	  	//String att = "";
		                //att = aDef.getName();
		                if (ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "Vendor_Name") 
		                	dataValue = ddo.getDataByName("Vendor_Name").toString() + ",";
		                else 
		                	dataValue = ",";
		                if (ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "Vendor_Number") {
		                	dataValue += ddo.getDataByName("Vendor_Number").toString()+ ",";
		                	imageName = dataValue;
		                }else 
		                	dataValue = ",";
		                
		                if (ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "Invoice_Num")  {
		                	dataValue += ddo.getDataByName("Invoice_Num").toString()+ ",";
		                	imageName += "_" + dataValue;
		                }else 
		                	dataValue = ",";
		                
		                if (ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "Tax_Amount") 
		                	dataValue += ddo.getDataByName("Tax_Amount").toString()+ ",";
		                else 
		                	dataValue = ",";
		                
		                if (ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "Invoice_Amount") 
		                	dataValue += ddo.getDataByName("Invoice_Amount").toString()+ ",";
		                else 
		                	dataValue = ",";
		                
		                if (ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "Invoice_Date")
		                	dataValue += ddo.getDataByName("Invoice_Date").toString()+ ",";
		                else 
		                	dataValue = ",";
		                
		                if (ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "PO_Number") {
		                	dataValue += ddo.getDataByName("PO_Number").toString()+ ",";
		                	imageName += "_" + dataValue;
		                }else 
		                	dataValue = ",";
		                
		                if (itemType == "SAP_Ariba_Invoices" && ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "AssignmentNumber") 
		                	dataValue += ddo.getDataByName("AssignmentNumber").toString();
		                else 
		                	dataValue = ",";
		                
		                if ((itemType == "SAP_Ariba_Invoices" || itemType == "SAP_Invoices") && ddo.getDataByName(aDef.getName()) != null && ddo.getDataByName(aDef.getName()) == "Tx_Number") 
		                	dataValue += ddo.getDataByName("Tx_Number").toString();
		                else 
		                	dataValue = ",";
		                System.out.println("dataValue =" + dataValue);    
		             
	              }catch (DKException localDKException1) {}
	              
	              metaData = dataValue + imageName;
	            }
	            System.out.println("metaData  att =" + metaData);
	            System.out.println("counter =" + counter);
	            if (dataid > 0) {
	              DKParts dkParts = (DKParts)ddo.getData(dataid);
	              if (dkParts.cardinality() != 0) { dkParts.cardinality();
	              }
	              
	              dkIterator iter3 = dkParts.createIterator();
	              System.out.println("parts: " + dkParts.cardinality());
	              int partCounter = 0;
	              
	              int read = 0;
	              byte[] bytes = new byte[1000];
	              while (iter3.more()) {
	                DKLobICM part = (DKLobICM)iter3.next();
	                
	                part.retrieve(dkRetrieveOptions.dkNVPair());
	                
	                OutputStream out = new FileOutputStream(new File("c:\\ExportInvImages\\" + imageName + ".tiff"));
	                InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(), -1, -1);
	                System.out.println("get image ...");
	                while ((read = inputStream.read(bytes)) != -1) {
	                  out.write(bytes, 0, read);
	                }
	                
	                inputStream.close();
	                out.flush();
	                out.close();
	                
	                System.out.println("image file path : c:\\ExportInvImages\\" + imageName + ".tiff");
	              }
	              
	
	            }
	            else
	            {
	
	              System.out.println("readImage: Could not find item.");
	            }
	            String newLine = System.getProperty("line.separator");
	            
	            metaData = header + metaData + "Image_Path c:\\ExportInvImages\\" + imageName + ".tiff" + newLine;
	            System.out.println("before write metaData =" + metaData);
	            bw.write(metaData);
	          }
	          iter = null;
	          bw.flush();
	          bw.close();
	        }
	        catch (IOException localIOException) {}
	      }
	      
	     results = null;
	      queryString = null;
	      parms = (DKNVPair[])null;
	    }
	    catch (DKException dke) {
	      dke.printStackTrace();
	 
	      if (dsICM != null) {
	        try {
	          dsICM = null;
	        } catch (Exception e) {
	          System.out.println("Error returning connection to the pool." + e.getMessage());
	        }
	      }
	    }
	    catch (InstantiationException ie)
	    {
	      ie.printStackTrace();
	      
	      if (dsICM != null) {
	        try {
	          dsICM = null;
	        } catch (Exception e) {
	          System.out.println("Error returning connection to the pool." + e.getMessage());
	        }
	      }
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	  
	      if (dsICM != null) {
	        try {
	          dsICM = null;
	        } catch (Exception e) {
	          System.out.println("Error returning connection to the pool." + e.getMessage());
	        }
	      }
	    }
	    finally
	    {
	      if (dsICM != null) {
	        try {
	          dsICM = null;
	        } catch (Exception e) {
	          System.out.println("Error returning connection to the pool." + e.getMessage());
	        }
	      }
	    }
	  }
}
