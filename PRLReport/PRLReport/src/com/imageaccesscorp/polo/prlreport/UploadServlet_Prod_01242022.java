package com.imageaccesscorp.polo.prlreport;

import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreExtICM;
import java.io.*;
import java.text.*;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Date;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*; 
import javax.servlet.http.*; 
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

public class UploadServlet_Prod_01242022 extends javax.servlet.http.HttpServlet implements Servlet
{
	String newLine = System.getProperty("line.separator");
	BufferedWriter bw = null;
	String metaData = "";
	String report 	= null;
	
  public UploadServlet_Prod_01242022() {}
  

  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
  {
    doPost(req, resp);
    System.out.println("[UploadServlet]Inside UploadServlet Get");
	
  }
  
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
  {
	  DKDatastoreICM dsICM = null;
  
	  try{
  
	  
	  System.out.println("[UploadServlet]Inside UploadServlet Post");
	  PrintWriter out = resp.getWriter(); 
	  String searchType = req.getParameter("UploadServletForm");
	  System.out.println("[UploadServlet]searchType = "+searchType);
	  //boolean ismultipart = ServletFileUpload.isMultipartContent(req);
	  String itemType	 		= req.getParameter("itemtype");
	  System.out.println("[UploadServlet]itemType = "+itemType);
	  String uploadfile	 		= req.getParameter("uploadfile");
	  System.out.println("[UploadServlet]uploadfile = "+uploadfile);
	  String nextJSP = "";
	  // if(itemType == null)
      processRequest(req, resp);
	  //else
    	//attributeSearch(req, resp);
	  
	  if(req.getAttribute("err")==null){
		  System.out.println("[UploadServlet]Report is done! go to Complete JSP.");
		  
		  metaData = "";
		  nextJSP = "/PRLReport/Complete.jsp";
		  resp.sendRedirect(nextJSP);
	  }else{
		  nextJSP = "/PRLReport/Error.jsp";
		  System.out.println("[UploadServlet]err == "+req.getAttribute("err"));
		  resp.sendRedirect(nextJSP);
  	  }
	  } catch (Exception localException1) {
		  req.setAttribute("err","100");
        	return;
	  }
  }
 
  
  protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
	  //System.out.println("dsICM in xlsSearch = "+dsICM);
	  System.out.println("[UploadServlet]xlsSearch");
	  
	  System.out.println("[UploadServlet]Inside xlsSearch");
	  
	  try
	    {
	      //boolean ismultipart = ServletFileUpload.isMultipartContent(req);
	      //System.out.println("ismultipart = "+ismultipart);
	     
		  String formField 		= null;
          
          String itemTypeValue 	= null;
          
          String attribute 		= null;
          
          String attConvertStr 	= null;
          
          String attributeValue = null;
          
          String reportPath 	= null;
          
          String imagePath 		= null;
          
          String clientHost		= null;
          
          String num 			= null;
          
          String condition		= null;
          
          String addition		= null;
          
          String additionValue	= null;
          
          String addQueryString	= "";
          
          String dateQuery		= "";
          
          String startDate		= "";
          
          String endDate		= "";
          
          String dateControl	= null;
          
          //int numPerFolder 		= 0;
          HttpSession session = req.getSession(false);
          
  		  String userID = session.getAttribute("userid").toString();
  		  
      	  FileInputStream inputStream = null;
      	  
      	  String itemPath 		= null;
	      
	        FileItemFactory factory = new org.apache.commons.fileupload.disk.DiskFileItemFactory();
	        
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        
	        upload.setFileSizeMax(10000000L);
	        
	        List<FileItem> items = null;
	        	        
	        System.out.println("[UploadServlet]Upload = " + upload);
	        try
	        {
	        	
	        	
	          items = upload.parseRequest(req);
	          System.out.println("[UploadServlet]items size = " + items.size());
	          //Iterator itr = items.iterator();
	         
	          //while (itr.hasNext())
	          for (FileItem item : items) {

	            //FileItem item = (FileItem)itr.next();
	            System.out.println("[UploadServlet]Item = " + item.getFieldName());
	            
	            if (item.isFormField()) {
		              formField = item.getFieldName();
		              System.out.println("[UploadServlet]formField = " + formField);
		              if (formField.equalsIgnoreCase("itemtype")) {
				            itemTypeValue = item.getString();
				            System.out.println("[UploadServlet]itemTypeValue = " + itemTypeValue);
			          }
		              if (formField.equalsIgnoreCase("attribute")) {
			                //attribute = item.getString();
			                
			                attribute = attConvert(item.getString(), itemTypeValue);
			                
			                System.out.println("[UploadServlet]attribute = " + attribute);
		              }
		              if (formField.equalsIgnoreCase("path")) {
		            	  	
		            	  	reportPath = item.getString();
		            	    reportPath = reportPath + userID + "\\";
		            	    
		            	    File f = new File(reportPath);

			                System.out.println("[UploadServlet]reportPath1 = " + reportPath);
			                
			                //String reportDateTime = new Date().toString();
			                Date date = new Date() ;
			                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
			                
			                
			                reportPath = reportPath + dateFormat.format(date);
			                imagePath = reportPath + "\\images\\";
			                boolean success1 = (new File(reportPath + "\\")).mkdirs();
			                boolean success = (new File(imagePath + "\\")).mkdirs();				
			                System.out.println("[UploadServlet]reportPath2 = " + success1 +reportPath);
			                System.out.println("[UploadServlet]imagePath = " +success+ imagePath);
			                session.setAttribute("path", reportPath);
		              }
		              if (formField.equalsIgnoreCase("addition")) {
		            	  	//addition = item.getString();
		            	  System.out.println("[UploadServlet]item.getString() = " + item.getString());
		            	  System.out.println("[UploadServlet]itemTypeValue = " + itemTypeValue);
		            	  	addition = attConvert(item.getString(), itemTypeValue);
		            	  	//addQueryString = " and "+addition+"=";
			                System.out.println("[UploadServlet]addition = " + addition);
		              }
		              if (formField.equalsIgnoreCase("additionvalue")) {
		            	  	additionValue = item.getString();
		            	  	System.out.println("[UploadServlet]addQueryString inside  loop = " + addQueryString);
		              }
		              if (formField.equalsIgnoreCase("condition")) {
		            	  	condition = item.getString();
			                System.out.println("[UploadServlet]condition = " + condition);
		              }
		              if (formField.equalsIgnoreCase("startdate")) {
		            	  	startDate = item.getString();
		            	  	
			                System.out.println("[UploadServlet]startDate = " + startDate);
		              }
					  if (formField.equalsIgnoreCase("enddate")) {
		            	  	endDate = item.getString();
		            	  	
			                System.out.println("[UploadServlet]endDate = " + endDate);
		              }
		              if (formField.equalsIgnoreCase("datecontrol")) {
		            	  	dateControl = item.getString();
			            	
			                System.out.println("[UploadServlet]dateControl = " + dateControl);
		              }
		              
		              System.out.println("[UploadServlet]After retrieve form items. imagePath ="+imagePath);
	            } else {
		              itemPath = item.getName();
		              System.out.println("[UploadServlet]itemPath == " + itemPath);
		              
		              //only search on upload file
		              if(itemPath.trim().length()>0) {
		            	  itemPath = "C:/temp/newXlS.xlsx";
		            	  item.write(new File(itemPath));
			              inputStream = new FileInputStream(new File(itemPath));
		              }
		        }
	          }
	        }catch (Exception localException) {
	          	System.out.println("[UploadServlet]localException = "+localException.getMessage());
	          	req.setAttribute("err","103:"+localException.getMessage());
	          	return;
	        }
	        bw = new BufferedWriter(new FileWriter(reportPath + "\\" + "output.csv"));
	    	metaData = "Vendor Number,Vendor Name,Invoice Number,Tax Amount,Invoice Amount,Invoice Date,PO Number,Transaction,image path"+newLine;
	    	    
	    	if(additionValue.trim().length()>0 && condition.equalsIgnoreCase("exact")){
    	  		addQueryString = " AND @"+addition+"="+"\""+additionValue+"\"";
    	  	}
	    	if(additionValue.trim().length()>0 && condition.equalsIgnoreCase("begin")){
    	  		addQueryString = " AND @"+addition+" LIKE "+"\""+additionValue+"%\"";
    	  	}
	    	if(dateControl!=null){
	    		dateQuery = " AND @CREATETS>=\"" + startDate + " 00:00:00.001\" AND @CREATETS< \"" + endDate + " 23:59:59.999\"";
	  		}	
	    	System.out.println("[UploadServlet]addQueryString = " + addQueryString);
	    	System.out.println("[UploadServlet]dateQuery = " + dateQuery);
	    	
	        if(itemPath.trim().length()==0) {
	          	  System.out.println("[UploadServlet]itemPath is null");
	          	  searchResults(itemTypeValue, imagePath, null, null, addQueryString, dateQuery, false, req, resp);
	          	  //searchResults(itemTypeValue, imagePath, null, null, addition, additionValue, condition, dateControl, startDate + " 00:00:00.001", endDate + " 23:59:59.999", false, req);
            }else{
            	System.out.println("[UploadServlet]itemPath is not null");
	              inputStream = new FileInputStream(new File(itemPath));
            
	              processSheet(inputStream, itemPath, imagePath, itemTypeValue, attribute, addQueryString, dateQuery, req, resp);
	        //      processSheet(inputStream, itemPath, imagePath, itemTypeValue, attribute, addQueryString, startDate + " 00:00:00.001", endDate + " 23:59:59.999", req);
	        }
	        bw.write(metaData);
	        bw.flush();
	        bw.close();
	    } catch (Exception localException1) {
	    	req.setAttribute("err","104:"+localException1.getMessage());
          	return;
	    } 
	    
	  
  }
  
  /* Production
   * 	EU_Ariba_Online_Archive 1134
  		attr000001132 vendor name
	  	attr000001168 Vendor Num:
	  	attr000001154 Invoice Number 
	  	attr000001163 PO
  */
  
  protected String attConvert(String attribute, String itemtype){
	  String attCov = "";
	  System.out.println("[UploadServlet]attConvert attribute = " + attribute);
	//attr0000001055 dev
	//  if(attribute.equalsIgnoreCase("attr0000001055"))
	//attr0000001054 prod
	  if(attribute.equalsIgnoreCase("attr0000001054")) 
		  attCov = "Invoice_Num";
	//attr0000001031 prod & dev attr0000001072
	  //if(attribute.equalsIgnoreCase("attr0000001031") && itemtype.equalsIgnoreCase("Ariba_Online_Invoice_Archive"))
		//dev Vendor_Number\attr0000001031 Vendor_Num\attr0000001072 EU_Ariba_Online_Archive\1177001	
	  if(attribute.equalsIgnoreCase("attr0000001031") && (itemtype.equalsIgnoreCase("Ariba_Online_Invoice_Archive") || itemtype.equalsIgnoreCase("EU_Ariba_Online_Archive") || itemtype.equalsIgnoreCase("LegalTracker")))
		  attCov = "Vendor_Num";
	  if(attribute.equalsIgnoreCase("attr0000001031") && (!itemtype.equalsIgnoreCase("Ariba_Online_Invoice_Archive") && !itemtype.equalsIgnoreCase("EU_Ariba_Online_Archive") && !itemtype.equalsIgnoreCase("LegalTracker")))
		  attCov = "Vendor_Number";
	//attr0000001066 dev
	//  if(attribute.equalsIgnoreCase("attr0000001166"))
	//attr0000001063 prod 
	  if(attribute.equalsIgnoreCase("attr0000001163"))
		  attCov = "PO_Number";	  
	  return attCov;
  }
  protected void processSheet(FileInputStream inputStream, String itemPath, String folderPath, String itemTypeValue, String attribute, String addQueryString, String dateQuery, HttpServletRequest req, HttpServletResponse resp) throws IOException{
	  System.out.println("[UploadServlet]itemPath in processSheet servlet = "+itemPath);
	   	
      try{
		  Workbook wb = null;
    	  if (itemPath.endsWith("xlsx")) {
    	    wb = new XSSFWorkbook(inputStream);
    	  } else if (itemPath.endsWith("xls")) {
	        wb = new HSSFWorkbook(inputStream);
    	  } else {
	        throw new IllegalArgumentException("The specified file is not Excel file");
    	  }

          //Workbook wb = new XSSFWorkbook(file); 
          System.out.println("[UploadServlet]wb = " + wb);
          Sheet sh = wb.getSheetAt(0);  

          //HSSFSheet sh = workbook.getSheetAt(0);
          
          System.out.println("[UploadServlet]Sheet = " + sh);
          Iterator<Row> rowIterator = sh.rowIterator();
          
          System.out.println("[UploadServlet]rowIterator = " + rowIterator);
          Iterator<Cell> cellIterator;
          DataFormatter formatter = new DataFormatter();

          for (; rowIterator.hasNext(); cellIterator.hasNext())
          {
            //HSSFRow row = (HSSFRow)rowIterator.next();
        	Row nextRow = rowIterator.next();
        	cellIterator = nextRow.cellIterator();
        	System.out.println("[UploadServlet]cellIterator = " + cellIterator);
            //cellIterator = row.cellIterator();
        	Cell cell = cellIterator.next();
            //continue;
            
            //HSSFCell cell = (HSSFCell)cellIterator.next();
        	String val = formatter.formatCellValue(cell);

            System.out.println("[UploadServlet]Cell val jsp= " + val);
                        
            searchResults(itemTypeValue, folderPath, attribute, val, addQueryString, dateQuery, true, req, resp);
            
          }
      }catch(Exception ex){
    	  System.out.println("[UploadServlet]err in processSheet = "+ex.getMessage());
    	  req.setAttribute("err","105");
    	  //resp.sendRedirect("Error.jsp");
    	  return;
      }   						

  }

  //protected void searchResults(String itemType, String path, String attribute, String attributeValue, String addition, String additionValue, String condition, String dateControl, String startDate, String endDate, boolean isUpload, HttpServletRequest req) throws IOException{
  protected void searchResults(String itemType, String path, String attribute, String attributeValue, String addQueryString, String dateQuery, boolean isUpload, HttpServletRequest req, HttpServletResponse resp) throws IOException{
	  	HashMap<String, Object> hm = new HashMap<String, Object>(); 
		DKDDO ddo = null;
	    DKResults results = null;
	    //String folderName = null;
	    String queryString = "";
	    
	    DKDatastoreExtICM dsExtICM = null;
	    System.out.println("[UploadServlet]isUpload = "+isUpload);
	    if(isUpload){
  			//queryString = "/" + itemType + "[@VERSIONID = latest-version(.) AND @" + attribute + "=\"" + attributeValue + "\"" +  addQueryString + "]";
  			queryString = "/" + itemType + "[@VERSIONID = latest-version(.) AND @" + attribute + "=\"" + attributeValue + "\"" +  addQueryString + dateQuery +"]";
  			//queryString = "/" + itemType + "[@" + attribute + "=\"" + attributeValue + "\" AND @CREATETS>\"2007-01-01 00:00:00.000001\" AND @CREATETS<\"2017-11-01 00:00:00.000001\"]";
	    }else{
			//queryString = "/" + itemType + "[@VERSIONID = latest-version(.) AND @" + attribute + "=\"" + attributeValue + "\" AND @CREATETS >\"2009-10-16-15.10.14.781420\" AND @CREATETS < \"2017-10-16-15.10.14.781420\"]";
	    	queryString = "/" + itemType + "[@VERSIONID = latest-version(.) " + addQueryString + dateQuery + "]";
	    		//queryString = "/" + itemType + "[@VERSIONID = latest-version(.) AND @" + attribute + "=\"" + attributeValue + "\" AND @" + attribute + "=\"" + attributeValue + "\" AND @CREATETS>=\"" + startDate + "\" AND @CREATETS< \"" + endDate + "\"]";
	 	    //else
	 	    //	queryString = "/" + itemType + "[@VERSIONID = latest-version(.) AND @" + attribute + " LIKE \"%" + attributeValue + "%\" AND @CREATETS>=\"" + startDate + "\" AND @CREATETS< \"" + endDate + "\"]";
	    
  	  	}
	    System.out.println("[UploadServlet]queryString = "+queryString);
		DKNVPair parms[] = new DKNVPair[2];
	    parms[0] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
	    // Specify any Retrieval Options desired.  Default is ATTRONLY.
	    parms[1] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
	    // Evaluate the query, seting the results into (results)
		DKDatastoreICM dsICM = null;
		try {
			
			System.out.println("[UploadServlet]Connecting to CM ... -c->");
			dsICM = new DKDatastoreICM();
			dsICM = ICMConnectionPool.getConnection(req,resp);
			System.out.println("Get Connection from Pool!!"+dsICM);
	    	DKRetrieveOptionsICM dkRetrieveOptions = DKRetrieveOptionsICM.createInstance(dsICM);
	    	dkRetrieveOptions.resourceContent(true);
            
		    results = (DKResults)dsICM.evaluate(queryString, DKConstant.DK_CM_XQPE_QL_TYPE, parms);
		    
	        System.out.println("[UploadServlet]get results = " + results.cardinality());
	   
	      if (results.cardinality() == 0) {
	        System.out.println("[UploadServlet]getImgage: Could not find item. ");
	        //req.setAttribute("err","112: Could not find item.");
	        
	      } else {
	    	  System.out.println("[UploadServlet]results is not null.");
	        dkIterator iter = (dkIterator)results.createIterator();
	        try
	        {
	        System.out.println("[UploadServlet]getImgage ......");
	          
		      int counter = 0;
	          //String metaData = "";
	          // Loop thru rows
	          while (iter.more())
	          {
	        	String imageName = "";
	            
	        	String imgPath = "";
	        	
	            String dataValue = "";
	            
	            counter++;
	            
	            DKDatastoreDefICM dsDefICM = new DKDatastoreDefICM(dsICM);
	             
	            DKItemTypeDefICM entityDef = (DKItemTypeDefICM)dsDefICM.retrieveEntity(itemType);
	             
	            DKSequentialCollection allAttrs = (DKSequentialCollection)entityDef.listAllAttributes();
	            
	            dkIterator attIter = allAttrs.createIterator();
	            
	            ddo = (DKDDO)iter.next();
	            ddo.retrieve(DKConstant.DK_CM_CONTENT_ONELEVEL);
	            short dataid = ddo.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, DKConstant.DK_CM_DKPARTS);
	           
	            //dataValue = "";
	            // Loop thru each attribute of a line item
	            while (attIter.more()) {
	              DKAttrDefICM aDef = (DKAttrDefICM)attIter.next();
	              
	              try {
	            	  System.out.println("[UploadServlet]ready to HM->"+aDef.getName());
	            	  dataValue =  "";
		            	  	//String att = "";
		                //att = aDef.getName();
		                if (aDef.getName().equals("Vendor_Name")){
		                	System.out.println("[UploadServlet]ready to Vendor_Name");
		                	if(ddo.getDataByName(aDef.getName()) != null ) 
		                		dataValue = ddo.getDataByName("Vendor_Name").toString();
		                	else 
		    		           	dataValue = "";
		                	System.out.println("[UploadServlet]VN add to HM");
		                	hm.put("Vendor_Name", "\"" + dataValue + "\"");
		                }
		                if(aDef.getName().equals("Vendor_Number")){
		                
		                	 if(ddo.getDataByName(aDef.getName()) != null ){
		                		dataValue = ddo.getDataByName("Vendor_Number").toString();
				                imageName += "_" + dataValue;
		                	 }else
		                		 dataValue = "";
		                	 System.out.println("[UploadServlet] imageName#1="+imageName);
			                 hm.put("Vendor_Number", dataValue);
		                }	
		                if(aDef.getName().equals("Vendor_Num")){
			                
		                	 if(ddo.getDataByName(aDef.getName()) != null ){
		                		dataValue = ddo.getDataByName("Vendor_Num").toString();
				                imageName += "_" + dataValue;
		                	 }else
		                		 dataValue = "";
		                	 System.out.println("[UploadServlet] imageName#11="+imageName);
			                 hm.put("Vendor_Num", dataValue);
		                }
		                if(aDef.getName().equals("Invoice_Num")){
		                	 if(ddo.getDataByName(aDef.getName()) != null ){
		                		dataValue = ddo.getDataByName("Invoice_Num").toString();
		                		//dataValue = dataValue.replace("/", " ");;
		                		dataValue = dataValue.replaceAll("\\W", " "); 
		                		imageName += "_" + dataValue;
		                	 }else
		                		dataValue = "";
		                	 System.out.println("[UploadServlet] imageName#2="+imageName);
		                	 hm.put("Invoice_Num", dataValue);
		                }	
		                if (aDef.getName().equals("Tax_Amount")){
		                	if(ddo.getDataByName(aDef.getName()) != null ) 
		                		dataValue = ddo.getDataByName("Tax_Amount").toString();
		                	else 
		    		           	dataValue = "";
		                	hm.put("Tax_Amount", dataValue);
		                }
		                if (aDef.getName().equals("Invoice_Amount")){
		                	if(ddo.getDataByName(aDef.getName()) != null ) 
		                		dataValue = ddo.getDataByName("Invoice_Amount").toString();
		                	else 
		    		           	dataValue = "";
		                	hm.put("Invoice_Amount", dataValue);
		                }
		                if (aDef.getName().equals("Invoice_Date")){
		                	if(ddo.getDataByName(aDef.getName()) != null ) 
		                		dataValue = ddo.getDataByName("Invoice_Date").toString();
		                	else 
		    		           	dataValue = "";
		                	hm.put("Invoice_Date", dataValue);
		                }
		                if (aDef.getName().equals("PO_Number")){
		                	if(ddo.getDataByName(aDef.getName()) != null ){ 
		                		dataValue = ddo.getDataByName("PO_Number").toString();
		                		dataValue = dataValue.replaceAll("\\W", " "); 
		                		imageName += "_" + dataValue;
		                	}else 
		    		           	dataValue = "";
		                	System.out.println("[UploadServlet] imageName#3="+imageName);
		                	hm.put("PO_Number", dataValue);
		                }
		                if ((itemType.equals("SAP_Ariba_Invoices") || itemType.equals("EU_Ariba_Invoices") || itemType.equals("Ariba_Online_Invoice_Archive") || itemType.equals("EU_Ariba_Online_Archive")) && aDef.getName().equals("AssignmentNumber")){
		                	if(ddo.getDataByName(aDef.getName()) != null ){ 
		                		dataValue = ddo.getDataByName("AssignmentNumber").toString();
		                		imageName = dataValue;
		                	}else 
		    		           	dataValue = "";
		                	hm.put("TransactionNumber", dataValue);
		                }
		                if ((itemType.equals("SAP_Invoices") || itemType.equals("Gen_Invoices")) && aDef.getName().equals("Tx_Number")){
		                	System.out.println("[UploadServlet]ready to Tx_Number->"+ddo.getDataByName(aDef.getName()));
		                	if(ddo.getDataByName(aDef.getName()) != null ){ 
		                		dataValue = ddo.getDataByName("Tx_Number").toString();
		                		imageName += dataValue;
		                	}else 
		    		           	dataValue = "";
		                	
		                	System.out.println("[UploadServlet] imageName#4="+imageName);
		                	hm.put("TransactionNumber", dataValue);
		                }
		                
		                //System.out.println("[UploadServlet]ddo.getDataByName(aDef.getName())====" + aDef.getName()+"--"+ddo.getDataByName(aDef.getName()));    
		               
			             
	              }catch (DKException localDKException1) {
	            	  System.out.println("[UploadServlet]Err CM1 ");
	            	  req.setAttribute("err","106");
	            	  //resp.sendRedirect("Error.jsp");
	            	  return;
	              }
	              
	            }
	            System.out.println("[UploadServlet]hm="+hm);
	            	metaData += hm.get("Vendor_Number") + (String)hm.get("Vendor_Num") + "," + hm.get("Vendor_Name") + "," + hm.get("Invoice_Num") + "," + hm.get("Tax_Amount") + "," + hm.get("Invoice_Amount") + "," + hm.get("Invoice_Date") +  "," + hm.get("PO_Number") +  "," + hm.get("TransactionNumber")  + "," ;
	            
	            //System.out.println("[UploadServlet]metaData  att =" + metaData);
	            System.out.println("[UploadServlet]counter =" + counter);
	            String extension = "";
	            if (dataid > 0) {
	              DKParts dkParts = (DKParts)ddo.getData(dataid);
	              if (dkParts.cardinality() != 0) { dkParts.cardinality();
	              }
	              
	              dkIterator iter3 = dkParts.createIterator();
	              System.out.println("[UploadServlet]parts: " + dkParts.cardinality());
	              int partCounter = 0;
	              
	              int read = 0;
	              
	              byte[] bytes = new byte[1000];
	              while (iter3.more()) {
	                DKLobICM part = (DKLobICM)iter3.next();
	                
	                part.retrieve(dkRetrieveOptions.dkNVPair());
	                System.out.println("[UploadServlet]Mime type = "+part.getMimeType());
	                 
	                //if(part.getMimeType().indexOf("pdf")!=-1)
	                //	extension = "pdf";
	                //else
	                //	extension = "tiff";
	                if(part.getMimeType().indexOf("pdf")!=-1)
	                	extension = "pdf";
	                else if(part.getMimeType().indexOf("tiff")!=-1)
	                	extension = "tiff";
	                else if(part.getMimeType().indexOf("msword")!=-1)
	                	extension = "doc";
	                else if(part.getMimeType().indexOf("vnd.ms-excel")!=-1)
	                	extension = "xls";
	                else if(part.getMimeType().indexOf("spreadsheetml.sheet")!=-1)
	                	extension = "xlsx";
	                else if(part.getMimeType().indexOf("jpeg")!=-1)
	                	extension = "jpeg";
	                else if(part.getMimeType().indexOf("jpg")!=-1)
	                	extension = "jpg";
	                else if(part.getMimeType().indexOf("png")!=-1)
	                	extension = "png";
	                else if(part.getMimeType().indexOf("xml")!=-1)
	                	extension = "xml";
	                else if(part.getMimeType().indexOf("vnd.ms-outlook")!=-1)
	                	extension = "msg";
	                else if(part.getMimeType().indexOf("application/octet-stream")!=-1){
	                	
	                	int findExtensionPos = part.getOrgFileName().indexOf(".");
	                		
	                	extension = part.getOrgFileName().substring(findExtensionPos + 1);
	                	
	                	System.out.println("[UploadServlet]extension = "+ extension);
		   	            
	                }
	                OutputStream out = new FileOutputStream(new File(path + imageName + "." + extension));
	                //OutputStream out = new FileOutputStream(new File(path + "\\" + imageName + ".tiff"));
	                System.out.println("[UploadServlet]out file = "+path + imageName + "." + extension);
	                InputStream inputStream = part.getInputStream(dkRetrieveOptions.dkNVPair(), -1, -1);
	                System.out.println("[UploadServlet]get image ...");
	                while ((read = inputStream.read(bytes)) != -1) {
	                  out.write(bytes, 0, read);
	                }
	                
	                inputStream.close();
	                out.flush();
	                out.close();
	                
		          }
	           
	            }
	            else
	            {
	
	              System.out.println("[UploadServlet]readImage: Could not find item.");
	              req.setAttribute("err","111: Could not find image.");
	            }
	            //String newLine = System.getProperty("line.separator");
	            imgPath = path.replace("\\\\", "\\");;
	            //path = path.replace("\\\\", "\\");
	            metaData += imgPath + imageName + "." + extension + newLine;
	            System.out.println("[UploadServlet]metaData = "+metaData);
	          }
	          iter = null;
	          
	        }
	        catch (IOException localIOException) {
	        	System.out.println("[UploadServlet]getImgage 2 ");
	        	req.setAttribute("err","107:"+localIOException.getMessage());
	        	
	        	return;
	        	
	        }
	      }
	      
	     results = null;
	     queryString = null;
	     parms = (DKNVPair[])null;
	    }
	    catch (DKException dke) {
	      dke.printStackTrace();
	      req.setAttribute("err","108: "+dke.getMessage());
	      
	      if (dsICM != null) {
	        try {
	          dsICM = null;
	        } catch (Exception e) {
	          System.out.println("[UploadServlet]Error returning connection to the pool." + e.getMessage());
	        }
	      }
	      return;
	    }
	    catch (InstantiationException ie)
	    {
	      ie.printStackTrace();
	      req.setAttribute("err","109");
	      if (dsICM != null) {
	        try {
	          dsICM = null;
	        } catch (Exception e) {
	          System.out.println("[UploadServlet]Error returning connection to the pool." + e.getMessage());
	        }
	      }
	      return;
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	      req.setAttribute("err","110");
	      if (dsICM != null) {
	        try {
	          dsICM = null;
	        } catch (Exception e) {
	          System.out.println("[UploadServlet]Error returning connection to the pool." + e.getMessage());
	        }
	      }
	      return;
	    }
	    
	    finally {
			if (dsICM != null) {
				
				try {
                    ICMConnectionPool.returnConnection(dsICM);
                    dsICM = null;
                } catch (Exception e) {
                    System.out.println("[UploadServlet]Error returning connection to the pool." + e.getMessage());
                }
			}
			if (results != null) {
				results = null;
			}
			if (parms != null) {
				parms = null;
			}
			
		}
	  }
   
}
