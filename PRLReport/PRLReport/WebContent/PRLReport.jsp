<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="Content-Style-Type" content="text/css">
<link rel="stylesheet" href="/PRLReport/theme/blue.css" type="text/css">
<TITLE>Polo Report</TITLE>
</head>
<body>
      
<table width="760" cellspacing="0" cellpadding="0" border="0">
   <tbody>
      <tr>
         <td valign="top">
         <table class="header" cellspacing="0" cellpadding="0" border="0" width="100%">
            <tbody>
               <tr>
                  <td width="160"><IMG border="0" width="160"
							height="50" alt="Company's LOGO"
							src="/PRLReport/theme/prllogo.jpg"></td>
                  <td width="250" style="color: white; font-family:Times New Roman;font-size:large;"> Invoices Report</td>
               </tr>
            </tbody>
         </table>
         </td>
      </tr>
      <tr>
         <td valign="top" class="nav_head" height="20"></td>
      </tr>
      <form action = "UploadServlet" method = "post">
      
      <tr class="content-area">
         <td style="padding:10px 10px 10px 150px;" border-top: 2px dotted #999;" valign="top">Select Item Type: 
         			<select name="itemtype"><option value="Gen_Invoices">Gen_Invoices</option>
							<option value="SAP_Invoices">SAP_Invoices</option>
							<option value="SAP_Ariba_Invoices">SAP_Ariba_Invoices</option>
					</select> </td>
      </tr>
	  <tr class="content-area">
         <td style="padding:10px 10px 10px 150px; valign="top">Search For: 
         			<select name="attribute">
         					<option value="Invoice_Num">Invoice Number</option>
         					<option value="Vendor_Number">Vendor Number</option>
         					<option value="PO_Number">PO Number</option>
         			</select>&nbsp;<input type="text" name="attvalue">
         			<center><input type = "submit" name = "action" value = "Search" /></center>
         			</td>
         			
      </tr>
      
      </form>
      <form action = "UploadServlet" method = "post" enctype = "multipart/form-data">
      <tr class="content-area">
         <td style="padding:10px 10px 10px 150px; border-top: 2px dotted #999;" valign="top">Select Item Type: 
         			<select name="itemtype"><option value="Gen_Invoices">Gen_Invoices</option>
							<option value="SAP_Invoices">SAP_Invoices</option>
							<option value="SAP_Ariba_Invoices">SAP_Ariba_Invoices</option>
					</select> </td>
      </tr>
	  
      <tr class="content-area" >
         <td style="padding:10px 10px 10px 150px;" valign="top">Search For: 
         			<select name="attribute">
         					<option value="Invoice_Num">Invoice Number</option>
         					<option value="Vendor_Number">Vendor Number</option>
         					<option value="PO_Number">PO Number</option>
         			</select>&nbsp;<input type="text" name="attvalue"></td>
      </tr>
	  <tr class="content-area">
         <td style="padding:10px 10px 10px 150px" valign="top">Upload Searching File:  
         <input type="file" name="file" size="50" style="width: 250px" />
						<br>
					
        <center> <input type = "submit"  name = "action" value = "Upload & Search" /></center>
      
	  </td>
      </tr>
	 </form>
	  <tr class="content-area">
         <td style="padding:10px 10px 10px 150px"  valign="top">&nbsp;</td>
      </tr>
      <tr>
         <td valign="top" height="20" class="footer"></td>
      </tr>
   </tbody>
</table>
 
</body>
</html>