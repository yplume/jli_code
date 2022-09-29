

import com.ibm.mm.sdk.common.*;
import com.ibm.mm.sdk.server.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.ibm.mm.beans.*;
import com.ibm.mm.beans.gui.*;
import com.ibm.mm.viewer.*;
import com.ibm.mm.sdk.common.*;
import java.io.*;
import java.lang.String.*;
import java.net.URLDecoder;


public class ViewInvoice3182018 extends HttpServlet
    implements DKConstantICM
{

/*    public ViewInvoice()
    {
    }
*/

    public synchronized void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse)
        throws ServletException, IOException
    {
        ServletOutputStream servletoutputstream = httpservletresponse.getOutputStream();
        try
        {
        	
			DKDatastoreICM dsICM = new DKDatastoreICM();  
			dsICM.connect("icmnlsdb","icmadmin","bigblue1","");
			//dsICM.connect("icmnlsdb","invlookup","invlookup1","");
			CMBConnection connection = new CMBConnection();
			connection.setDsType("ICM");
			connection.setServerName("icmnlsdb");
			connection.setUserid("icmadmin");
			connection.setPassword("bigblue1");
			//connection.setUserid("invlookup");
			//connection.setPassword("invlookup1");
			connection.connect();
			String query = null;

			// | /ClubMonaco8525 | /ClubMonaco8550 | /Corporate8600 | /Home8300 | /Legal_Invoices | /RLMedia8700 | /Retail8500 | /Wholesale8100
			if (httpservletrequest.getParameter("itemtype") == null)
				//query = "( (/Gen_Invoices) [@Tx_Number = \"" + httpservletrequest.getParameter("Tx_Number") + "\"] )";
				query = "/" + httpservletrequest.getParameter("itemtype") + "[@BarCode=\"" + httpservletrequest.getParameter("BarCode") + "\"]";
		    /*else if (httpservletrequest.getParameter("itemtype").equalsIgnoreCase("1"))
			//else if (httpservletrequest.getParameter("itemtype") == "1")
				query = "( (/SAP_Invoices) [@Tx_Number = \"" + httpservletrequest.getParameter("Tx_Number") + "\"] )";
			else if (httpservletrequest.getParameter("itemtype").equalsIgnoreCase("2"))
				query = "(/SAP_Ariba_Invoices [@AssignmentNumber = \"" + httpservletrequest.getParameter("Tx_Number") + "\"] )";
			else 
				query = "(/EU_Ariba_Invoices [@AssignmentNumber = \"" + httpservletrequest.getParameter("Tx_Number") + "\"] )";
			*/
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
			boolean r = false;
			String action = httpservletrequest.getParameter("action").trim().toLowerCase();
			if ((!action.equals("view")) && (!action.equals("save")))
				action = "false";
			
			System.out.println("cursor = "+cursor.cardinality());
			
			if ((ddo = cursor.fetchNext()) != null)	
			{
				System.out.println("Inside fetch next...");
				
				try
						{
							DKPidICM pidx = new DKPidICM(((DKPidICM)ddo.getPidObject()).pidString());
							CMBItem itemBean = new CMBItem(pidx.toString());
						 	itemBean.setConnection(connection);
 							CMBDataManagement dataManagement = connection.getDataManagement();
							itemBean.setConnection(connection);
							dataManagement.setDataObject(itemBean);        
							dataManagement.retrieveItem();
							CMBDocumentServices documentServices = new CMBDocumentServices();
							documentServices.setDataManagement(connection.getDataManagement());
							documentServices.setDataManagement(dataManagement);
							dataManagement.setDataObject(itemBean);
							CMBDocument doc = documentServices.loadDocument(itemBean);
							String mimeType = doc.getWriteMimeType();
							System.out.println("mimeType in ViewInv = "+mimeType);
							/*
		                    if (mimeType.equalsIgnoreCase("image/tiff")) {
		                        if (action.equals("view") | action.equals("false")) {
		                            httpservletresponse.setContentType(mimeType);
		                        } else {
		                            httpservletresponse.setHeader("Content-Disposition", "attachment; filename=Inv" + httpservletrequest.getParameter("Tx_Number") + ".tiff");
		                        }
		                        doc.write((OutputStream)servletoutputstream);
		                    } else {
		                        System.out.println("mimeTypePDF=" + mimeType);
		                        CMBObject docPart = dataManagement.getContent(0);
		                        dataManagement.getItemMimeType();
		                        byte[] pdfFile = docPart.getData();
		                        ServletOutputStream out = httpservletresponse.getOutputStream();
		                        out.write(pdfFile);
		                        out.flush();
		                    }
							doc.write(servletoutputstream);
							*/
							httpservletresponse.setContentType(mimeType);
							
							dsICM.disconnect();
							connection.disconnect();
							dsICM.destroy();
							}
						catch(Exception exception)
						{
							servletoutputstream.println("HTTP Encoding Error!<br> " + exception.getMessage() + " -- " + exception.fillInStackTrace());
							exception.printStackTrace();
						}
				} else {
					httpservletresponse.sendRedirect("http://usnymetia4:234/ViewInvoice/NotFound.jsp?action=viewinvoice&transactionnumber=" + httpservletrequest.getParameter("BarCode"));
					//httpservletresponse.sendRedirect("http://prl-invoiceapproval.poloralphlauren.com/InvoiceApproval/NotFound.asp?action=viewinvoice&transactionnumber=" + httpservletrequest.getParameter("Tx_Number"));
				}//endif-curor.fetchNext()
				
				
        } //endif-Cursor.fetchnext
        catch(DKException dkexception)
        {
            servletoutputstream.println(dkexception.name() + "Search Error!<br>" + dkexception.getMessage());
            dkexception.printStackTrace();
        }
        catch(Exception exception)
        {
            servletoutputstream.println("Error!<br> " + exception.getMessage());
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

	DKDatastoreICM dsICM;
}