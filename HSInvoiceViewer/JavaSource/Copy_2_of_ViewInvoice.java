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
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Copy_2_of_ViewInvoice extends HttpServlet implements com.ibm.mm.sdk.common.DKConstantICM
{
  DKDatastoreICM dsICM;
  
  public Copy_2_of_ViewInvoice() {}
  
  public synchronized void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse) throws ServletException, IOException
  {
    ServletOutputStream servletoutputstream = httpservletresponse.getOutputStream();
    
    try
    {
    
    	System.out.println("Before connection...");
    	
      DKDatastoreICM dsICM = new DKDatastoreICM();
      dsICM.connect("icmnlsdb", "icmadmin", "bigblue1", "");
      System.out.println("after connection...");
  	
      CMBConnection connection = new CMBConnection();
      connection.setDsType("ICM");
      connection.setServerName("icmnlsdb");
      connection.setUserid("icmadmin");
      connection.setPassword("bigblue1");
      
      System.out.println("set CMB connection...");
    	
      connection.connect();
      String query = null;
      System.out.println("BarCode=="+httpservletrequest.getParameter("BarCode"));
      System.out.println("eHit=="+httpservletrequest.getParameter("eHit"));
      
      query = "/" + httpservletrequest.getParameter("itemtype") + "[@VERSIONID = latest-version(.) AND @BarCode=\"" + httpservletrequest.getParameter("BarCode") + "\"]";
      
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
	  String action = httpservletrequest.getParameter("eHit").trim().toLowerCase();
	  if ((!action.equals("view")) && (!action.equals("save")))
		action = "false";
	
	  System.out.println("cursor = "+cursor.cardinality());
	
	  if ((ddo = cursor.fetchNext()) != null)	
	  //while ((ddo = cursor.fetchNext()) != null)	
	  {
	System.out.println("Inside fetch next.");
		
		try
		{
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
            
            ServletOutputStream out = httpservletresponse.getOutputStream();
            System.out.println("After ServletOutputStream");
            
            out.write(pdfFile);
            System.out.println("ch After write");
            doc.write(out);
            out.flush();
            System.out.println("After flush");
            
           // doc.write(servletoutputstream);
          //}
            System.out.println("After servletoutputstream");
            
          dsICM.disconnect();
          connection.disconnect();
          dsICM.destroy();
        }
        catch (Exception exception)
        {
          servletoutputstream.println("HTTP Encoding Error!<br> " + exception.getMessage() + " -- " + exception.fillInStackTrace());
          exception.printStackTrace();
        }
      } else {
        httpservletresponse.sendRedirect("http://usnymetia5:235/ViewInvoice/NotFound.jsp?action=viewinvoice&transactionnumber=" + httpservletrequest.getParameter("BarCode"));
      }
      

    }
    catch (DKException dkexception)
    {

      servletoutputstream.println(dkexception.name() + "Search Error!<br>" + dkexception.getMessage());
      dkexception.printStackTrace();
    }
    catch (Exception exception)
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
}