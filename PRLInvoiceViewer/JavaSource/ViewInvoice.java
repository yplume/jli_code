import com.ibm.mm.beans.CMBConnection;
import com.ibm.mm.beans.CMBDataManagement;
import com.ibm.mm.beans.CMBDocumentServices;

import com.ibm.mm.beans.CMBItem;
import com.ibm.mm.beans.CMBObject;
import com.ibm.mm.sdk.common.DKConstantICM;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKNVPair;
import com.ibm.mm.sdk.common.DKPidICM;
import com.ibm.mm.sdk.common.dkResultSetCursor;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.ibm.mm.sdk.common.*;

import com.ibm.mm.viewer.CMBDocument;
import com.ibm.mm.viewer.CMBStreamingDocServices;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.OutputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

public class ViewInvoice extends HttpServlet implements com.ibm.mm.sdk.common.DKConstantICM
{
  DKDatastoreICM dsICM;
  
  public ViewInvoice() {}
  
  public synchronized void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse) throws ServletException, IOException
  {
	  ServletOutputStream servletoutputstream = null;
	  //ServletOutputStream servletoutputstream = httpservletresponse.getOutputStream();
	    
    try
    {
    
    	System.out.println("Before connection...");
    	
      DKDatastoreICM dsICM = new DKDatastoreICM();
      //dsICM.connect("icmnlsdb", "icmadmin", "bigblue1", "");
      dsICM.connect("icmnlsdb", "icmadmin", "BigBlue1", "");
      System.out.println("after connection...");
  	
      CMBConnection connection = new CMBConnection();
      connection.setDsType("ICM");
      connection.setServerName("icmnlsdb");
      connection.setUserid("icmadmin");
      connection.setPassword("BigBlue1");
      
      System.out.println("set CMB connection...");
    	
      connection.connect();
      String query = null;
      String tranAttr = null;
      String tranNum = null;
      String itemType = null;
      String vendorNum = null;
      
      if((itemType == "SAP_Ariba_Invoices") || (itemType == "EU_Ariba_Invoices")){
    	  tranAttr = "AssignmentNumber";
    	  tranNum = httpservletrequest.getParameter("AssignmentNumber");
    	  vendorNum = httpservletrequest.getParameter("Vendor_Number");
      }else if((itemType == "Gen_Invoices") || (itemType == "SAP_Invoices")){
    	  tranAttr = "Tx_Number";
    	  tranNum = httpservletrequest.getParameter("Tx_Number");
    	  vendorNum = httpservletrequest.getParameter("Vendor_Number");
      }else if((itemType == "Ariba_Online_Invoice_Archive") || (itemType == "EU_Ariba_Online_Archive") || (itemType == "LegalTracker")){
    	  tranAttr = "AssignmentNumber";
    	  tranNum = httpservletrequest.getParameter("AssignmentNumber");
    	  vendorNum = httpservletrequest.getParameter("Vendor_Num");
      }
      
      System.out.println("VendorNumber=="+vendorNum);
      System.out.println("tranNum=="+tranNum);
      
      query = "/" + itemType + "[@VERSIONID = latest-version(.) AND @" + tranAttr + "=\"" + tranNum + "\"]";
      //query = "/" + httpservletrequest.getParameter("itemtype") + "[@VERSIONID = latest-version(.) AND @BarCode=\"" + httpservletrequest.getParameter("BarCode") + "\"]";
      
      DKNVPair options[] = new DKNVPair[3];
      options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default)                    
					// Specify max using a string value.
      options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY)); 
					// Specify any Retrieval Options desired.  Default is ATTRONLY.
      options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);                                           
					// Must mark the end of the NVPair
      System.out.println("httpservletrequest.getParameter(itemtype)=="+httpservletrequest.getParameter("itemtype"));
      System.out.println("query in ViewInv="+query);
	
      dkResultSetCursor cursor = dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
      DKDDO  ddo;
      String itemId = "";
      String dataValue="";
	  short attnum ;
	  
	  //String action = httpservletrequest.getParameter("eHit").trim().toLowerCase();
	  //String pid = httpservletrequest.getParameter("pid");
	  //System.out.println("PID = "+pid);
	  //if ((!action.equals("view")) && (!action.equals("save")))
		// 	action = "false";
	  //if (pid != null)
		//	action = "false";
		  
	  System.out.println("Cursor = "+cursor.cardinality());
	  //System.out.println("pid is null = "+pid == null);
		
	  PrintWriter pw = null;
	  
	  /*if(cursor.cardinality() > 1 && pid == null){
		  
		  httpservletresponse.setContentType("text/html");
	  
		  pw = httpservletresponse.getWriter();
		  
		  pw.println("<tr>");
			
		  pw.println("<td><b>Click on below link to open or save file.</b><br/><br/>");
		  
		  pw.println("</td>");
			
		  pw.println("</tr>");
  		
	  }*/
	  //if ((ddo = cursor.fetchNext()) != null)	
	  while ((ddo = cursor.fetchNext()) != null)	
	  {
		  System.out.println("Inside fetch next.");
		
		try
		{
		//	if(count==cursor.cardinality()){
		  DKPidICM pidx = new DKPidICM(((DKPidICM)ddo.getPidObject()).pidString());
		  String spid = pidx.toString();
		  
		  System.out.println("spid in ViewInv = " + spid);
		  CMBItem itemBean = new CMBItem(spid);
          itemBean.setConnection(connection);
          CMBDataManagement dataManagement = connection.getDataManagement();
          itemBean.setConnection(connection);
          dataManagement.setDataObject(itemBean);
          dataManagement.retrieveItem();
          CMBDocumentServices documentServices = new CMBDocumentServices();
          documentServices.setDataManagement(dataManagement);
          dataManagement.setDataObject(itemBean);
          CMBDocument doc = documentServices.loadDocument(itemBean);
          
          String mimeType = doc.getMimeType().trim();
          

          System.out.println("mimeType in ViewInvoice = " + mimeType);
          /*if (mimeType.equalsIgnoreCase("image/tiff"))
          {
            httpservletresponse.setContentType(mimeType);
            doc.write(servletoutputstream);

          }
          else
          {*/

            System.out.println("mimeType PDF = " + mimeType);
            CMBObject docPart = dataManagement.getContent(0);
            System.out.println("docPart = " + docPart.getOriginalFileName());
            dataManagement.getItemMimeType();
            
            byte[] pdfFile = docPart.getData();
            
            System.out.println("pdfFile = " + pdfFile);
            
            OutputStream outputStream = null;
            
            System.out.println("PDF is "+mimeType.contains("pdf"));
            
            int index = docPart.getOriginalFileName().lastIndexOf("\\");
            
            if(!mimeType.contains("pdf")){
            	System.out.println("Not pdf");
            	////////////for xls//////////////
            		
            	//httpservletresponse.setHeader("Content-disposition", "attachment;filename=" + docPart.getOriginalFileName().substring(index + 1));
            	httpservletresponse.setHeader("Content-disposition", "inline;filename=" + docPart.getOriginalFileName().substring(index + 1));
	            httpservletresponse.setHeader("charset", "iso-8859-1");
	            httpservletresponse.setContentType(mimeType);
	            System.out.println("After ContenType");
	            httpservletresponse.setContentLength(pdfFile.length);
	            httpservletresponse.setStatus(HttpServletResponse.SC_OK);
	            System.out.println("After SC_OK");
	            try{
	            	outputStream = httpservletresponse.getOutputStream();
	            	System.out.println("After outputStream");
	            	outputStream.write(pdfFile);
	                System.out.println("-non pdf After write--");
	                outputStream.flush();
	                //doc.write(out);
	                System.out.println("-non pdf After out-");
	                outputStream.close();

	                //httpservletresponse.flushBuffer();
	                //out.flush();
                } catch (Throwable e) {
                	System.out.println("Throwable="+e.getMessage());
                }
            	
            	
            }else{ //if is pdf
            	
            	ServletOutputStream out = httpservletresponse.getOutputStream();
                
	            System.out.println("After ServletOutputStream");
	            try{
		            out.write(pdfFile);
		            System.out.println("-pdf After write--");
		            doc.write(out);
		            System.out.println("-pdf After out-");
		            out.close();
	            //out.flush();
	            } catch (Throwable e) {
	            	System.out.println("Throwable="+e.getMessage());
	            }
	            System.out.println("After flush-close");
            }
	           
            
            
          /*dsICM.disconnect();
          System.out.println("1");
          connection.disconnect();
          System.out.println("2");
          dsICM.destroy();
          System.out.println("3");*/
		//}
        }
        catch (Exception exception)
        {
          servletoutputstream.println("HTTP Encoding Error!<br> " + exception.getMessage() + " -- " + exception.fillInStackTrace());
          exception.printStackTrace();
        }
      }
	  if(cursor.cardinality()==0) {
        httpservletresponse.sendRedirect("http://usncdvcmweb1v:367/ViewInvoice/NotFound.jsp?action=view&BarCode=" + httpservletrequest.getParameter("BarCode"));
      }
	  httpservletresponse.flushBuffer();
      
	  dsICM.disconnect();
      
      connection.disconnect();
      
      dsICM.destroy();
      
      System.out.println("After distory dsICM!");
    }
    catch (DKException dkexception)
    {

      //servletoutputstream.println(dkexception.name() + "Search Error!<br>" + dkexception.getMessage());
      dkexception.printStackTrace();
    }
    catch (Exception exception)
    {
      //servletoutputstream.println("Error!<br> " + exception.getMessage());
      exception.printStackTrace();
    }
  }
  

  public synchronized void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
    throws ServletException, IOException
  {
    doGet(httpservletrequest, httpservletresponse);
  }
  
  public void init(ServletConfig servletconfig)
    throws ServletException
  {
    super.init(servletconfig);
  }
}