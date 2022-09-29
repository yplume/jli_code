/*
 * Created on Mar 7, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.net.URL;
import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.text.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class Copy_3_of_XMLReader  {
	//public static final String MAIL_ROUTER_XML_CONFIG_FILE_NAME = "PoloConfigAriba.xml";
	
	private int m_ReaderDelay;
	private String vendorName = "";
	private String vendorNum = "";
	private String invoiceNum = "";
	private String invoiceID = "";
	private String invoiceAmount = "";
	private String tax = "";
	private String invoiceDate = "";
	private String PONum = "";
	private String payeeNum = "";
	private String payeeName = "";
	private String paymentMetod = "";
	private String partnerBankType = "";
	private String buyerEmailAddress = "";
	private String pstTax = "";
	private String gstTax = "";
	private String hstTax = "";
	private String vatTax = "";
	private String curr = "";
	private String pdfPath = "";
	private String invoiceSubmissionMethod = "";
	private static String extractFileName = "";
	private String systemID;
	
	public String getSystemID() {
        return this.systemID;
    }
    
    public void setSystemID(String SystemID) {
        this.systemID = SystemID;
    }
	public String getPDFPath() {
		return pdfPath;
	}
	public void setPDFPath(String PDFPath) {
		this.pdfPath = PDFPath;
	}
	
	public String getVatTax() {
		return vatTax;
	}
	public void setVatTax(String VatTax) {
		this.vatTax = VatTax;
	}
	public String getCurrency() {
		return curr;
	}
	public void setCurrency(String currency) {
		this.curr = currency;
	}
	public String getPstTax() {
		return pstTax;
	}
	public void setPstTax(String PstTax) {
		this.pstTax = PstTax;
	}
	public String getGstTax() {
		return gstTax;
	}
	public void setGstTax(String GstTax) {
		this.gstTax = GstTax;
	}
	public String getHstTax() {
		return hstTax;
	}
	public void setHstTax(String HstTax) {
		this.hstTax = HstTax;
	}
	
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vName) {
		this.vendorName = vName;
	}
	
	
	public String getVendorNum() {
		return vendorNum;
	}
	public void setVendorNum(String vNum) {
		this.vendorNum = vNum;
	}
	
	public String getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(String invID) {
		this.invoiceID = invID;
	}
	
	public String getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(String invAmount) {
		this.invoiceAmount = invAmount;
	}
	
	
	public String getTax() {
		return tax;
	}
	public void setTax(String Tax) {
		this.tax= Tax;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invDate) {
		this.invoiceDate= invDate;
	}
	public String getPONum() {
		return PONum;
	}
	public void setPONum(String poNum) {
		this.PONum= poNum;
	}
	public String getPayeeNum() {
		return payeeNum;
	}
	public void setPayeeNum(String payeeNumber) {
		this.payeeNum= payeeNumber;
	}
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String PayeeName) {
		this.payeeName= PayeeName;
	}
	public String getBuyerEmailAddress() {
		return buyerEmailAddress;
	}
	public void setBuyerEmailAddress(String BuyerEmailAddress) {
		this.buyerEmailAddress= BuyerEmailAddress;
	}
	public String getPaymentMetod() {
		return paymentMetod;
	}
	public void setPaymentMetod(String PaymentMetod) {
		this.paymentMetod= PaymentMetod;
	}
	public String getPartnerBankType() {
		return partnerBankType;
	}
	public void setPartnerBankType(String PartnerBankType) {
		this.partnerBankType= PartnerBankType;
	}
	public String getInvoiceSubmissionMethod() {
		return invoiceSubmissionMethod;
	}
	public void setInvoiceSubmissionMethod(String InvoiceSubmissionMethod) {
		this.invoiceSubmissionMethod = InvoiceSubmissionMethod;
	}
	
	
	public void readXMLConfig(String xmlName, String extractFileName1)	{
		//System.out.println("xmlFile before loader");
		final ClassLoader loader = this.getClass().getClassLoader(); 
		//System.out.println("xmlFile after loader");
		URL url = loader.getResource(xmlName);
		File xmlFile = new File(xmlName);	
		//File xmlFile = new File("c:\\wd\\JTestout\\JTest\\"+ xmlName);	
		//File xmlFile = new File("c:\\wd\\zipoutput\\2018-6-10-24\\"+ xmlName);	
		extractFileName = extractFileName1;
		
		
		System.out.println("xmlFile in xmlreader = "+xmlFile.getPath());
		//setPDFPath(xmlFile.getPath().replaceAll("xml", "pdf"));
		setPDFPath(xmlFile.getPath().replace("xml", "pdf"));
		
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
		try {

			String[] xmlFields = {
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoicePartner/Contact[@role='from']/Name/text()",
					"/cXML/Header/From/Credential[@domain='VendorID']/Identity/text()",
					"/cXML/Header/To/Credential[@domain='SystemID']/Identity/text()", 
            		"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/@invoiceID",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/SubtotalAmount/Money/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/SubtotalAmount/Money/@currency",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/@invoiceDate",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailOrder/InvoiceDetailOrderInfo/OrderReference/@orderID",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/Money/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailOrder/InvoiceDetailItem/InvoiceDetailLineShipping/InvoiceDetailShipping/Contact[@role='shipTo']/Email/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoicePartner/Contact[@role='soldTo']/Email/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoiceDetailShipping/Contact[@role='shipTo']/Email/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/Extrinsic[@name='invoiceSubmissionMethod']/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoicePartner/Contact[@role='from']/Name/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoicePartner/Contact[@role='remitTo']/@addressID",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoicePartner/Contact[@role='remitTo']/Name/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail[@category='pst']/TaxAmount/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail[@category='gst']/TaxAmount/text()", 
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail[@category='hst']/TaxAmount/text()",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail[@category='vst']/TaxAmount/text()" 
					//"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail[@category='pst']/TaxAmount" 
					/*"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoicePartner/Contact (Payment Method) first char of last 4 addressID",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoicePartner/Contact last 4 char of addressID(Partner Bank Type)",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail only category=sales(PST Tax)",
					"/cXML/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail only category=sales(GST Tax)",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail only category=sales(HST Tax)",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/Tax/TaxDetail only category=sales(VAT Tax)",
					"/cXML/Request/InvoiceDetailRequest/InvoiceDetailSummary/InvoicePartner/Contact last 4 char of addressID(Invoice Currency)"		*/			
					
					
			};
			docBuildFact.setIgnoringElementContentWhitespace(false);
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(xmlFile);
			
			////////////////////adding xpath/////////////////////////
			doc.getDocumentElement().normalize();
			
			XPath xPath =  XPathFactory.newInstance().newXPath();
			
			//String expression = "/cXML/Request/InvoiceDetailRequest/InvoiceDetailRequestHeader/InvoicePartner/Contact";	       
			//String expression = "";
			
			for(int j=0; j<xmlFields.length; j++){
				//NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
				NodeList nodeList = (NodeList) xPath.compile(xmlFields[j]).evaluate(doc, XPathConstants.NODESET);
				
				try{
				if(nodeList.getLength()>0){
					for (int i = 0; i < nodeList.getLength(); i++) {
			            Node nNode = nodeList.item(i);
			            System.out.println("\nCurrent Element :" + nNode.getNodeName());
			            
			            Element eElement = null;
			            //try{
			            //if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			            	if (nNode.getNodeType() == Node.ELEMENT_NODE) 
					                eElement = (Element) nNode;
			               //System.out.println("Contact role :" + eElement.getAttribute("role"));
			               //System.out.println("Credential  Domain :" + nNode.getAttribute("Domain"));
			               
			               //if(eElement.getAttribute("role")!=null && eElement.getAttribute("role").equalsIgnoreCase("from")) {
			            	if(xmlFields[j].contains("invoiceSubmissionMethod")){
				            	   //vendorName = eElement.getElementsByTagName("Name").item(0).getTextContent();
			            		invoiceSubmissionMethod = nNode.getNodeValue();
				            	setInvoiceSubmissionMethod(invoiceSubmissionMethod);
				            	System.out.println("invoiceSubmissionMethod :" + invoiceSubmissionMethod);
				            }
			            	if(xmlFields[j].contains("role='from'")){
				            	   //vendorName = eElement.getElementsByTagName("Name").item(0).getTextContent();
				            	   vendorName = nNode.getNodeValue();
				            	   setVendorName(vendorName);
				            	   System.out.println("vendorName :" + vendorName);
				           }
			               if(xmlFields[j].contains("domain='VendorID'")){
				            	   vendorNum = nNode.getNodeValue();
				            	   setVendorNum(vendorNum);
				            	   
				            	   System.out.println("VendorID :" + vendorNum);
			               }
			               if (xmlFields[j].contains("domain='SystemID'")) {
			            	   systemID = nNode.getNodeValue();
			            	   setSystemID(systemID);
                               
                               System.out.println("SystemID :" + systemID);
                           }
                           if(xmlFields[j].contains("invoiceID")){
				            	   System.out.println("Inside  invoiceID");
			            	   invoiceID = nNode.getNodeValue();
			            	   setInvoiceID(invoiceID);
			            	   System.out.println("invoiceID :" + invoiceID);
			               }
			               if(xmlFields[j].contains("orderID")){
			                  System.out.println("Inside  orderID");
				            	  
			            	   PONum = nNode.getNodeValue();
			            	   setPONum(PONum);
			            	   System.out.println("PONum :" + PONum);
			               }
			               if(xmlFields[j].contains("Money")) {
			            	   System.out.println("Inside  Money");
				            	
		            	       //double f = Double.parseDouble(eElement.getFirstChild().getNodeValue());
		            	   
			            	   if(xmlFields[j].contains("Tax")){
			            		   double f = convertDecimals(nNode.getNodeValue());
				            	   
			            		   tax = String.format("%.2f", new BigDecimal(f));
			            		   setTax(tax);
			            		   System.out.println("tax :" + tax);
			            	   }else if (xmlFields[j].contains("currency")){
			            		   curr  = nNode.getNodeValue();
				            	   setCurrency(curr);
				            	   System.out.println("curr :" + curr);
			            		   
			            	   }else{
			            		   double f = convertDecimals(nNode.getNodeValue());
				            	   
			            		   invoiceAmount = String.format("%.2f", new BigDecimal(f));
			            		   setInvoiceAmount(invoiceAmount);
			            		   System.out.println("invoiceAmt :" + invoiceAmount);
			            	   }
		            	   
			            	   //invoiceAmount = eElement.getFirstChild().getNodeValue();
			            	  
			            	   
			            	   System.out.println();
				           }
			               if(xmlFields[j].contains("invoiceDate")) {
			            	   System.out.println("Inside  invoiceDate");
			            	   
			            	   invoiceDate = nNode.getNodeValue();
			            	   setInvoiceDate(invoiceDate);
			            	   System.out.println("invoiceDate :" + invoiceDate);
			            	   System.out.println();
				               
			               }
			               if(xmlFields[j].contains("shipTo") || xmlFields[j].contains("soldTo")) {
			            	   System.out.println("Inside  buyerEmailAddress");
			            	   
			            	   buyerEmailAddress = nNode.getNodeValue();
			            	   setBuyerEmailAddress(buyerEmailAddress);
			            	   System.out.println("Email :" + buyerEmailAddress);
			               }
			               //if(eElement.getAttribute("role")!=null && eElement.getAttribute("role").equalsIgnoreCase("soldTo")) {
			            	//   buyerEmailAddress = eElement.getElementsByTagName("Email").item(0).getTextContent();
			            	//   System.out.println("Email :" + buyerEmailAddress);
			               //}
			               //System.out.println("nNode.getNodeValue() = "+xmlFields[j]);
			               if(xmlFields[j].contains("addressID") && nNode.getNodeValue().indexOf("_")!=-1) {
			            	   System.out.println("Inside  payeeNum");
			            	   
			            	   payeeNum = nNode.getNodeValue();
			            	   setPayeeNum(payeeNum);
			            	   System.out.println("payeeNum :" + payeeNum);
			            	   System.out.println();
			            	   
			            	   int iMethod = payeeNum.indexOf("_");
			            	   paymentMetod = payeeNum.substring(iMethod +1, iMethod + 2);
			            	   setPaymentMetod(paymentMetod);
			            	   System.out.println("paymentMetod :" + paymentMetod);
			            	   
			            	   partnerBankType = payeeNum.substring(payeeNum.length() - 4);
			            	   setPartnerBankType(partnerBankType);
			            	   System.out.println("partnerBankType :" + partnerBankType);
			               }
			               if(xmlFields[j].contains("[@role='remitTo']/Name")){
			            	   payeeName = nNode.getNodeValue();
			            	   setPayeeName(payeeName);
			            	   System.out.println("payeeName :" + payeeName);
			            	   
			               }
			               //System.out.println("Before pstTaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx :" + eElement.getElementsByTagName("Money").item(0).getTextContent());
			               if(xmlFields[j].contains("pst")){
			            	  
			            	   double t = convertDecimals(nNode.getNodeValue());
			            	   pstTax = String.format("%.2f", new BigDecimal(t));
			            	   //pstTax = eElement.getElementsByTagName("Money").item(0).getTextContent();
			            	   setPstTax(pstTax);
			            	   System.out.println("pstTax :" + pstTax);
			               }
			               if(xmlFields[j].contains("hst")){
			            	   
			            	   double t = convertDecimals(nNode.getNodeValue());
			            	   hstTax = String.format("%.2f", new BigDecimal(t));
			            	  
			            	   //hstTax = eElement.getElementsByTagName("Money").item(0).getTextContent();
			            	   setHstTax(hstTax);
			            	   System.out.println("hstTax :" + hstTax);
			               }
			               if(xmlFields[j].contains("gst")){
			            	   
			            	   double t = convertDecimals(nNode.getNodeValue());
			            	   gstTax = String.format("%.2f", new BigDecimal(t));
			            	  
			            	   //gstTax = eElement.getElementsByTagName("Money").item(0).getTextContent();
			            	   setGstTax(gstTax);
			            	   System.out.println("gstTax :" + gstTax);
			               }
			               if(xmlFields[j].contains("vat")){
			            	   double t = convertDecimals(nNode.getNodeValue());
			            	   vatTax = String.format("%.2f", new BigDecimal(t));
			            	  
			            	   //vatTax = eElement.getElementsByTagName("Money").item(0).getTextContent();
			            	   setVatTax(vatTax);
			            	   System.out.println("vatTax :" + vatTax);
			               }
			               
		            	   
			            //}
			            /*}catch(Exception ex){
			            	System.out.println("ex :" + ex.getMessage());
			            }*/
			        }
				}
				}catch(Exception ex){
	            	System.out.println("ex :" + ex.getMessage());
	            	ex.printStackTrace();
	            	System.out.println(ex);
	            	//File file = new File("\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\xmlreaderror1_" +extractFileName + ".txt");
	                //new File("\\usncdvkfxcrtr2v\\ExportToKofax\\ZipInput\\" + extractFileName).renameTo(new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
	                File file = new File("\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\xmlreaderror1_"+ extractFileName +".txt");
	            	new File("\\usncpdktmrtr3v\\ExportToKofax\\ZipInput\\" + extractFileName + ".zip").renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
	    			BufferedWriter output = new BufferedWriter(new FileWriter(file));
					output.write(ex.getMessage());
					output.close();
				}
			}
			
			System.out.println("XMLReader Finished ");

		} catch (XPathExpressionException e) {
	         e.printStackTrace();
	      
		} catch (ParserConfigurationException pce)	{
			System.out.println("XML Config file Parser failed with " + pce.toString());			
//			logger.fatal("XML Config file Parser failed with ", pce);
			//File file = new File("\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\xmlreaderror2_" + extractFileName + ".txt");
            //new File("\\usncdvkfxcrtr2v\\ExportToKofax\\ZipInput\\" + extractFileName).renameTo(new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
            File file = new File("\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\xmlreaderror2_"+ extractFileName +".txt");
			new File("\\usncpdktmrtr3v\\ExportToKofax\\ZipInput\\" + extractFileName + ".zip").renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
			try{
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(pce.getMessage());
			output.close();
        	}catch(Exception exception){
        		System.out.println("exception2: " + exception);
        	}
		} catch (SAXException se) {
			System.out.println("XML Config file Parser method failed with SAX exception " + se.toString());
			//			logger.fatal("XML Config file Parser method failed with SAX exception ", se);
			//File file = new File("\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\xmlreaderror2_" + extractFileName + ".txt");
            //new File("\\usncdvkfxcrtr2v\\ExportToKofax\\ZipInput\\" + extractFileName).renameTo(new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
            File file = new File("\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\xmlreaderror3_"+ extractFileName +".txt");
			new File("\\usncpdktmrtr3v\\ExportToKofax\\ZipInput\\" + extractFileName + ".zip").renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
			try{
        	BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(se.getMessage());
			output.close();
			}catch(Exception exception){
	    		System.out.println("exception3: " + exception);
	    	}
		} catch (IOException ioe)	{
			System.out.println("XML Config file Parser method failed with io exception " + ioe.toString());
//			logger.fatal("XML Config file Parser method failed with io exception ", ioe);	
			//File file = new File("\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\xmlreaderror4_" + extractFileName + ".txt");
            //new File("\\usncdvkfxcrtr2v\\ExportToKofax\\ZipInput\\" + extractFileName).renameTo(new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
            File file = new File("\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\xmlreaderror4_"+ extractFileName +".txt");
			new File("\\usncpdktmrtr3v\\ExportToKofax\\ZipInput\\" + extractFileName + ".zip").renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
			try{
        	BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(ioe.getMessage());
			output.close();
			}catch(Exception exception){
	    		System.out.println("exception4: " + exception);
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
 		   //File file = new File("\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\xmlreaderror5_" + extractFileName + ".txt");
           //new File("\\usncdvkfxcrtr2v\\ExportToKofax\\ZipInput\\" + extractFileName).renameTo(new File("\\\\usncdvkfxcrtr2v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
           File file = new File("\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\xmlreaderror5_"+ extractFileName +".txt");
 		   new File("\\usncpdktmrtr3v\\ExportToKofax\\ZipInput\\" + extractFileName  + ".zip").renameTo(new File("\\\\usncpdktmrtr3v\\ExportToKofax\\Ziperror\\" + extractFileName + ".zip"));
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
	

	public static void main(String[] args){
		Copy_3_of_XMLReader xr = new Copy_3_of_XMLReader();
		try{
			
			
		//SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		//SimpleDateFormat formatter=new SimpleDateFormat("mm/dd/yyyy");  
        //String InvoiceDate = "2018-05-01T08:21:02-07:00";
        //Date date = outputFormat.parse(InvoiceDate);
        //InvoiceDate = InvoiceDate.substring(0,InvoiceDate.indexOf("T"));
        //System.out.println(InvoiceDate);
        //Date dateValue = formatter.parse(InvoiceDate);
        //System.out.println(formatter.format(date));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
    	String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
    	System.out.println("timeStamp = "+timeStamp);
    	
		xr.readXMLConfig("2018_05_14_AN01051654024_CR_Copy_NR_200_2018_CM.xml", "2019-2-1");
	}
}
