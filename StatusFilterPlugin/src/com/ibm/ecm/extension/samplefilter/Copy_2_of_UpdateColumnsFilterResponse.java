package com.ibm.ecm.extension.samplefilter;

import javax.servlet.http.HttpServletRequest;

import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResultSetColumn;
import com.ibm.ecm.json.JSONResultSetResponse;
import com.ibm.ecm.json.JSONResultSetRow;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.mm.beans.CMBConnection;
import com.ibm.mm.beans.workflow.CMBDocRoutingQueryServiceICM;
import com.ibm.mm.beans.workflow.CMBWorkListICM;

import java.sql.*;

/**
 * Provides an abstract class that is extended to create a filter for responses
 * from a particular service. The response from the service is provided to the
 * filter in JSON format before it is returned to the web browser. The filter
 * can then modify that response, and the modified response is returned to the
 * web browser.
 */
public class Copy_2_of_UpdateColumnsFilterResponse extends PluginResponseFilter {

	/**
	 * Returns an array of the services that are extended by this filter.
	 * 
	 * @return A <code>String</code> array of names of the services. These are
	 *         the servlet paths or Struts action names.
	 */
	public String[] getFilteredServices() {
		return new String[] { "/cm/search","/cm/getWorkItems" };
	}

	/**
	 * Filters the response from the service.
	 * 
	 * @param serverType
	 *            A <code>String</code> that indicates the type of server that
	 *            is associated with the service. This value can be one or more
	 *            of the following values separated by commas:
	 *            <table border="1">
	 *            <tr>
	 *            <th>Server Type</th>
	 *            <th>Description</th>
	 *            </tr>
	 *            <tr>
	 *            <td><code>p8</code></td>
	 *            <td>IBM FileNet P8</td>
	 *            </tr>
	 *            <tr>
	 *            <td><code>cm</code></td>
	 *            <td>IBM Content Manager</td>
	 *            </tr>
	 *            <tr>
	 *            <td><code>od</code></td>
	 *            <td>IBM Content Manager OnDemand</td>
	 *            </tr>
	 *         	  <tr>
	 *         		<td><code>cmis</code></td>
	 *         		<td>Content Management Interoperability Services</td>
	 *         	  </tr>
	 *            <tr>
	 *            <td><code>common</code></td>
	 *            <td>For services that are not associated with a particular
	 *            server</td>
	 *            </tr>
	 *            </table>
	 * @param callbacks
	 *            An instance of the
	 *            <code>{@link com.ibm.ecm.extension.PluginServiceCallbacks PluginServiceCallbacks}</code>
	 *            class that contains functions that can be used by the service.
	 *            These functions provide access to plug-in configuration and
	 *            content server APIs.
	 * @param request
	 *            An <code>HttpServletRequest</code> object that provides the
	 *            request. The service can access the invocation parameters from
	 *            the request.
	 * @param jsonResponse
	 *            The <code>JSONObject</code> object that is generated by the
	 *            service. Typically, this object is serialized and sent as the
	 *            response. The filter modifies this object to change the
	 *            response that is sent.
	 * @throws Exception
	 *             For exceptions that occur when the service is running.
	 *             Information about the exception is logged as part of the
	 *             client logging and an error response is automatically
	 *             generated and returned.
	 */
	public void filter(String serverType, PluginServiceCallbacks callbacks,
			HttpServletRequest request, JSONObject jsonResponse) throws Exception {
		
		String user = callbacks.getUserId();
		System.out.println("user = "+user);
		JSONResultSetResponse jsonResultSetResponse = new JSONResultSetResponse();
		//jsonResultSetResponse.setPageSize(350);
		jsonResultSetResponse = (JSONResultSetResponse) jsonResponse;
		//JSONResultSetResponse jsonResultSetResponse = (JSONResultSetResponse) jsonResponse;
		System.out.println("jsonResponse size = "+jsonResponse.size());
			System.out.println("jsonResponse = "+jsonResponse);
			//check workflow items
			//JSONObject json = null;
			CMBConnection conn = new CMBConnection();
			conn.setDsType("ICM");
			//conn.setUserid(ConfigurationService.obtenerParametroSimple(callbacks,0));
			 //conn.setPassword(ConfigurationService.obtenerParametroSimple(callbacks,1));
			 //conn.setServerName(ConfigurationService.obtenerParametroSimple(callbacks,2));
			conn.setUserid("icmadmin");
			conn.setPassword("BigBlue1");
			//conn.setServerName("navigatorconn");
			conn.setServerName("LSConnection");
			
			conn.connect();
			CMBDocRoutingQueryServiceICM docRoutingQueryServiceICM = conn.getDocRoutingQueryServiceICM();
			System.out.println("***********jsonResultSetResponse.containsValue(SAP_Ariba_Invoices)********* = "+jsonResultSetResponse.containsValue("SAP_Ariba_Invoices"));
			System.out.println("docRoutingQueryServiceICM = "+docRoutingQueryServiceICM);
			
///////////////////////Add Inbox Count////////////////////////////
			JSONObject jsonData = (JSONObject)jsonResponse.get("datastore");
			 System.out.println("jsonData = "+jsonData);
			 
			 JSONArray jsonItems = (JSONArray)jsonData.get("items");
			 for(int i = 0; i < jsonItems.size(); i++){
				 JSONObject jsonWorkList = (JSONObject)jsonItems.get(i);
				 
				 System.out.println("jsonWorkList000 = "+jsonWorkList);
				 System.out.println("jsonWorkList111 = "+jsonWorkList.get("worklist_name"));
				 CMBWorkListICM lista = docRoutingQueryServiceICM.getWorkList((String)jsonWorkList.get("worklist_name"));
				 System.out.println("Lista = "+lista.getName());
				 System.out.println("getCount = "+docRoutingQueryServiceICM.getCount(lista.getName(),user));
				 String name = lista.getName() + " (" + docRoutingQueryServiceICM.getCount(lista.getName(),user) + ")";
				 jsonWorkList.put("worklist_name", name);
			 }
			 /////////////////
			
			if (!jsonResultSetResponse.containsValue("Bill_of_Lading")) {
			//if (jsonResultSetResponse.containsValue("SAP_Ariba_Invoices")||jsonResultSetResponse.containsValue("EU_Ariba_Invoices")||jsonResultSetResponse.containsValue("ITTCTest")) {
				System.out.println("**************Add new culomns *****************");
			//jsonResultSetResponse.setPageSize(500);
			JSONResultSetColumn  customColumn=null;
			JSONResultSetColumn  customColumn1=null;
			JSONResultSetColumn  customColumn2=null;
			JSONResultSetColumn  customColumn3=null;
			
			
			//if(request.getParameter("SampleColumWidth")!=null){
			//	customColumn = new JSONResultSetColumn("Sample", request.getParameter("SampleColumWidth"), "SampleColumn", null, true);
			//}else{
			customColumn1 = new JSONResultSetColumn("Validator", "100px", "Validator", null, null);
			customColumn2 = new JSONResultSetColumn("ScanTime", "100px", "ScanTime", null, null);
			customColumn3 = new JSONResultSetColumn("BatchName", "100px", "BatchName", null, null);
			customColumn = new JSONResultSetColumn("Status", "50px", "SampleColumn", null, null);
			//}
			System.out.println("jsonResultSetResponse.getRowCount = "+jsonResultSetResponse.getRowCount());
			
			

			
			//JSONObject structure = (JSONObject) jsonResponse.get("columns"); 
			
			
/*
 * 			///////////remove name///////////
			JSONArray cells = (JSONArray) structure.get("cells"); 

			if (cells.get(0) instanceof JSONArray) { 

				cells = (JSONArray) cells.get(0); 

			} 

			  

			for (int i = 0; i < cells.size(); i++) { 

				JSONObject column = (JSONObject) cells.get(i); 

				String columnName = (String) column.get("field"); 

			  

				//if (columnName != null && columnName.equals("multiStateIcon")) { 
	
				//	cells.remove(i); 

				//} 

				if (columnName != null && columnName.equals("{NAME}")) { 
					System.out.println("columnName = "+columnName);
					System.out.println("remove name column = "+column);
					column.put("name", customColumn); 
					//column.put("width", "60px"); 
					cells.remove(i); 
					
				} 

			} 
*/
			
         //Disable Insert Status column and add Kofax information
		 	
			//////////////////////
			//JSONResultSetResponse jsonesultSetResponseNew = (JSONResultSetResponse)jsonResultSetResponse.clone();
			//jsonResultSetResponse.clear();
			//jsonResultSetResponse.put("Status", customColumn);
			//jsonResultSetResponse.putAll(jsonesultSetResponseNew);
			
			Connection conn0 = null;
			Statement stmt =null;
			ResultSet rs = null;
			
			//jsonResultSetResponse.addColumn(customColumn);
			jsonResultSetResponse.addColumn(customColumn1);
			jsonResultSetResponse.addColumn(customColumn2);
			jsonResultSetResponse.addColumn(customColumn3);
			
			try{
			
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				conn0 = DriverManager.getConnection("jdbc:sqlserver://USNCPDKFXCAP1v;DatabaseName=ExportTestNew;user=test;password=test;integratedSecurity=false");
				
			for (int i = 0; i < jsonResultSetResponse.getRowCount(); i++) {
				JSONResultSetRow row = jsonResultSetResponse.getRow(i);
				//row.remove("NAME");
				//row.put("Status","Complete");
				System.out.println("row333 = "+row);
				System.out.println("row.getAttributeValue(id) = "+row.get("id"));
				System.out.println("row.getAttributeValue(itemid) = "+row.get("itemId"));
				
				
				String[] wppids = null;
				wppids = docRoutingQueryServiceICM.getWorkPackagePidStringsWithItem((String)row.get("id"));
				
				
				//add Kofax info
				String validator = null;
			    String scanTime = null;
			    String BatchName = null;
	
			    stmt = null;
			    rs = null;
			    
				
				//String isariba = null;
				//isariba = request.getParameter("isariba");
				//if (isariba!=null && isariba.equals("yes")) {
				//String docid = request.getParameter("docid");
				
			    //try{
				//DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
				//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				//conn0 = DriverManager.getConnection("jdbc:sqlserver://USNCPDKFXCAP1v;DatabaseName=ExportTestNew;user=test;password=test;integratedSecurity=false");
				
			    System.out.println("conn0="+conn0);
					    //DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
				    	    //conn = DriverManager.getConnection("jdbc:sqlserver://USNCPDKFXCAP1v;DatabaseName=Export;user=test;password=test;integratedSecurity=false");
					    if (conn0 != null) {
				    		stmt = conn0.createStatement();
				    		//String pidLike =  row.get("id").toString().substring(27);
				    		String pidLike =  row.get("itemId").toString();
				    		
				    		String sql = "SELECT Validator, ScanDateTime, BatchName FROM dbo.ExportAriba where RepositoryDocID like '%" + pidLike + "%'";
				       	 	rs = stmt.executeQuery(sql);
				       	 System.out.println("SQL="+sql);
					    }
								
	
					while (rs.next()) {
				      		validator = rs.getString("Validator");
				      		scanTime  = rs.getString("ScanDateTime");
							BatchName = rs.getString("BatchName");
						if (validator == null) validator = "";
						if (scanTime == null) scanTime = "";
						if (BatchName == null) BatchName = "";
						
						System.out.println("validator="+validator);
						
				      	}	
				  
				      conn = null;
				      stmt.close();
				      rs.close();
				   
				    /////////////////////////////////
						
				   // JSONResultSetRow rowNew = (JSONResultSetRow)row.clone();
					//row.clear();
				      System.out.println("wppids.length="+wppids.length+ "-- "+((String)row.get("id")).indexOf("WORKPACKAGE"));
				      //check in search
				      if ( wppids.length == 0 && (((String)row.get("id")).indexOf("WORKPACKAGE")==-1)) 
							row.addAttribute("SampleColumn", "Complete", JSONResultSetRow.TYPE_STRING, null, null);
				      //check in worklist
				      else if ( wppids.length == 0 && (((String)row.get("id")).indexOf("WORKPACKAGE")!=-1)) 
							row.addAttribute("SampleColumn", "", JSONResultSetRow.TYPE_STRING, null, null);
					  else
						  	row.addAttribute("SampleColumn", "", JSONResultSetRow.TYPE_STRING, null, null);
					  
			        
					//row.putAll(rowNew);
					//System.out.println("rowNew"+row);
					////////////////////////////////
					
					
					//} //end checking ariba
					row.addAttribute("Validator", validator, JSONResultSetRow.TYPE_STRING, null, null);
					row.addAttribute("ScanTime", scanTime, JSONResultSetRow.TYPE_STRING, null, null);
					row.addAttribute("BatchName", BatchName, JSONResultSetRow.TYPE_STRING, null, null);
					
				    
					//rowNew.clear();
				
			}
			
			/////insert status column in front ////////////////
			JSONArray columns = new JSONArray();
			int count = 0;
			for (Object column : this.getColumns(jsonResponse)) {
				if (count == 2) {
					columns.add(customColumn);
					
				}else{
					columns.add(column);
					
				}
				//System.out.println("column="+column);
				//System.out.println("this.getColumns(jsonResponse)="+this.getColumns(jsonResponse));
				
				count++;
			}
			this.getColumns(jsonResponse).clear();
			this.getColumns(jsonResponse).addAll(columns);
			
			//////////////////////////////////////////////
			
			 
			    
	} catch (Exception exc) {
		    	System.out.println("EditAttribute: Error in Exception " + exc);
    } finally {
	if (conn0 != null) {
		try{
		   conn0.close();
		}catch (SQLException sqle)	{
			System.out.println(" Error finally sqle ="+sqle.getMessage());
		}
                conn = null;
	}
	
	if (rs != null)	{
		try {
        	    rs.close();
		} catch (SQLException ex) {
			System.out.println(" Error finally ="+ex.getMessage());
		}
        	rs = null;
	}
				
	if (stmt != null) {
		try {
		    stmt.close();
		} catch (SQLException ex) {
			System.out.println(" Error finally ="+ex.getMessage());
		}
		stmt = null;
	}
    }
    
			}
		//}
		
	}
	
	private JSONArray getColumns(JSONObject jsonResponse) {
		JSONObject structure = (JSONObject) jsonResponse.get("columns");
		JSONArray columnSet0 = (JSONArray) structure.get("cells");
		JSONArray columns = (JSONArray) columnSet0.get(0);
		return columns;
	}
}