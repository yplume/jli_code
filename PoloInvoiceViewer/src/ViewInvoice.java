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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.FileInputStream.*;


public class ViewInvoice extends HttpServlet implements com.ibm.mm.sdk.common.DKConstantICM
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
        	System.out.println("Start ViewInvoice ...");
        	Properties prop = new Properties();
        	try {
        		FileInputStream fis = new FileInputStream("ViewConfig.properties");
        	    prop.load(fis);
        	    
        	} catch (IOException iex) {
        		System.out.println(iex);
        	    
        	}
			DKDatastoreICM dsICM = new DKDatastoreICM();  
			dsICM.connect("icmnlsdb","icmadmin","BigBlue123","");
			//dsICM.connect("icmnlsdb","icmadmin","BigBlue!@#","");
			//dsICM.connect("icmnlsdb","invlookup","invlookup1","");
			CMBConnection connection = new CMBConnection();
			connection.setDsType("ICM");
			connection.setServerName("icmnlsdb");
			connection.setUserid("icmadmin");
			connection.setPassword("BigBlue123");
			//connection.setPassword("BigBlue!@#");
			//connection.setUserid("invlookup");
			//connection.setPassword("invlookup1");
			connection.connect();
			String query = null;
			System.out.println("Tx_Number = "+httpservletrequest.getParameter("txnumber") +"     " + httpservletrequest.getParameter("itemtype"));
			
			// | /ClubMonaco8525 | /ClubMonaco8550 | /Corporate8600 | /Home8300 | /Legal_Invoices | /RLMedia8700 | /Retail8500 | /Wholesale8100
			if (httpservletrequest.getParameter("itemtype").equalsIgnoreCase("Gen_Invoices") || httpservletrequest.getParameter("itemtype").equalsIgnoreCase("SAP_Invoices"))
				query = "( (/" + httpservletrequest.getParameter("itemtype") + ") [@Tx_Number = \"" + httpservletrequest.getParameter("txnumber") + "\" and @Vendor_Number = \"" + httpservletrequest.getParameter("vendornumber") + "\"] )";
			if (httpservletrequest.getParameter("itemtype").equalsIgnoreCase("SAP_Ariba_Invoices") || httpservletrequest.getParameter("itemtype").equalsIgnoreCase("EU_Ariba_Invoices"))
				query = "( (/" + httpservletrequest.getParameter("itemtype") + ") [@AssignmentNumber = \"" + httpservletrequest.getParameter("txnumber") + "\" and @Vendor_Number = \"" + httpservletrequest.getParameter("vendornumber") + "\"] )";
			
			System.out.println("query =  "+query);
			
			/*if (httpservletrequest.getParameter("itemtype") == null)
				query = "( (/Gen_Invoices) [@Tx_Number = \"" + httpservletrequest.getParameter("Tx_Number") + "\"] )";
			else if (httpservletrequest.getParameter("itemtype").equals("1"))
				query = "( (/SAP_Invoices) [@Tx_Number = \"" + httpservletrequest.getParameter("Tx_Number") + "\"] )";
			else if (httpservletrequest.getParameter("itemtype").equals("2"))
				query = "( (/SAP_Ariba_Invoices) [@AssignmentNumber = \"" + httpservletrequest.getParameter("Tx_Number") + "\"] )";
			else 
				query = "(/EU_Ariba_Invoices [@AssignmentNumber = \"" + httpservletrequest.getParameter("Tx_Number") + "\"] )";
			*/DKNVPair options[] = new DKNVPair[3];
			options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "0"); // No Maximum (Default)                    
							// Specify max using a string value.
			options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE,    new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY)); 
							// Specify any Retrieval Options desired.  Default is ATTRONLY.
			options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);                                           
							// Must mark the end of the NVPair
			System.out.println("query in ViewInv="+query);
			
			dkResultSetCursor cursor = dsICM.execute(query, DKConstantICM.DK_CM_XQPE_QL_TYPE, options);
			DKDDO  ddo;
			String itemId = "";
			String dataValue="";
			short attnum ;
			boolean r = false;
			//String action = httpservletrequest.getParameter("action").trim().toLowerCase();
			//if ((!action.equals("view")) && (!action.equals("save")))
			//	action = "false";
			
			System.out.println("cursor="+cursor.cardinality());
			
			if ((ddo = cursor.fetchNext()) != null)	
			{
				/*try
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
							if (action.equals("view") | action.equals("false")) {
								httpservletresponse.setContentType(mimeType);
							} else {
								httpservletresponse.setHeader("Content-Disposition", "attachment; filename=Inv" + httpservletrequest.getParameter("Tx_Number") + ".tiff"); 
							}
							doc.write(servletoutputstream);
							dsICM.disconnect();
							connection.disconnect();
							dsICM.destroy();
							}
						catch(Exception exception)
						{
							servletoutputstream.println("HTTP Encoding Error!<br> " + exception.getMessage() + " -- " + exception.fillInStackTrace());
							exception.printStackTrace();
						}*/
				
				  DKPidICM pidx = new DKPidICM(((DKPidICM)ddo.getPidObject()).pidString());
		          System.out.println("pidx in ViewInv = " + pidx);
		          CMBItem itemBean = new CMBItem(pidx.toString());
		          itemBean.setConnection(connection);
		          CMBDataManagement dataManagement = connection.getDataManagement();
		          itemBean.setConnection(connection);
		          dataManagement.setDataObject(itemBean);
		          dataManagement.retrieveItem();
		          CMBDocumentServices documentServices = new CMBDocumentServices();
		          
		          documentServices.setDataManagement(dataManagement);
		          dataManagement.setDataObject(itemBean);
		          CMBDocument doc = documentServices.loadDocument(itemBean);
		          
		          String mimeType = doc.getMimeType();
		          

		          System.out.println("mimeType in ViewInv = " + mimeType);
		          

		            System.out.println("mimeType PDF = " + mimeType);
		            CMBObject docPart = dataManagement.getContent(0);
		            System.out.println("docPart = " + docPart.getOriginalFileName());
		            dataManagement.getItemMimeType();
		            byte[] pdfFile = docPart.getData();
		            System.out.println("pdfFile = " + pdfFile);
		            
		            ServletOutputStream out = httpservletresponse.getOutputStream();
		            System.out.println("After ServletOutputStream");
		            
		            out.write(pdfFile);
		            System.out.println("ch After write");
		            doc.write(out);
		            out.flush();
		            System.out.println("After flush");
		           
		            System.out.println("After servletoutputstream");
		            
		          dsICM.disconnect();
		          connection.disconnect();
		          dsICM.destroy();
				} else {
					httpservletresponse.sendRedirect("http://usncpdcmweb2v:500/PoloInvoiceViewer/NotFound.jsp?action=viewinvoice&transactionnumber=" + httpservletrequest.getParameter("Tx_Number"));
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