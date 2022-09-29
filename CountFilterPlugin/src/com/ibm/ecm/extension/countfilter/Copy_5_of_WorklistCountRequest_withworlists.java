package com.ibm.ecm.extension.countfilter;


import javax.servlet.http.HttpServletRequest;

import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginRequestUtil;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;
import com.ibm.mm.sdk.common.DKDocRoutingServiceICM;
import com.ibm.mm.sdk.common.DKDocRoutingServiceMgmtICM;
import com.ibm.mm.sdk.common.DKSequentialCollection;
import com.ibm.mm.sdk.common.DKWorkListICM;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreICM;

/**
 * Provides an abstract class that is extended to create a filter for requests to a particular service. The filter is provided with the 
 * request parameters before being examined by the service. The filter can change the parameters or reject the request.
 */
public  class Copy_5_of_WorklistCountRequest_withworlists extends PluginRequestFilter {

	/**
	 * Returns the names of the services that are extended by this filter.
	 * 
	 * @return A <code>String</code> array that contains the names of the services.
	 */
	public String[] getFilteredServices() {
		return new String[] { "/cm/getWorklists" };
	}

	/**
	 * Filters a request that is submitted to a service.
	 * 
	 * @param callbacks
	 *            An instance of <code>PluginServiceCallbacks</code> that contains several functions that can be used by the
	 *            service. These functions provide access to plug-in configuration and content server APIs.
	 * @param request
	 *            The <code>HttpServletRequest</code> object that provides the request. The service can access the invocation parameters from the
	 *            request. <strong>Note:</strong> The request object can be passed to a response filter to allow a plug-in to communicate 
	 *            information between a request and response filter.
	 * @param jsonRequest
	 *            A <code>JSONArtifact</code> that provides the request in JSON format. If the request does not include a <code>JSON Artifact</code>  
	 *            object, this parameter returns <code>null</code>.
	 * @return A <code>JSONObject</code> object. If this object is not <code>null</code>, the service is skipped and the
	 *            JSON object is used as the response.
	 */
	public  JSONObject filter(PluginServiceCallbacks callbacks, HttpServletRequest request, JSONArtifact jsonRequest) throws Exception {
		JSONObject json = new JSONObject();
		DKDatastoreICM dsICM = null;    
		
		 dsICM = callbacks.getCMDatastore("cm");

			
		 //dsICM = callbacks.getCMDatastore("CMRepository");
		 //System.out.println("after get dsICM");
		 DKDocRoutingServiceMgmtICM routingMgmt = new DKDocRoutingServiceMgmtICM(dsICM);
		
			
		 DKSequentialCollection workLists = (DKSequentialCollection) routingMgmt.listWorkLists();
			
		 String requestName = request.getParameter("worklist_name");
		 String realName = "";
		 String wlname = "";
		 //System.out.println("userid = "+callbacks.getUserId());
		 System.out.println("dsICM = "+dsICM);
		 
		 dkIterator iter = workLists.createIterator();
		 
		 DKWorkListICM workList; 
		 while (iter.more()) { // loop each worklist 
	        workList = (DKWorkListICM) iter.next();
		    System.out.println("requestName="+requestName);
		    System.out.println("workList.getName()="+workList.getName());
		    
		    if(requestName!=null && requestName.startsWith(workList.getName())){
		    	System.out.println("8888888");
		    	wlname = workList.getName();
		    	 System.out.println("9999999999");
		 		
				System.out.println("realName = "+realName);
				break;
			}
		 }
	     System.out.println("wlname = "+wlname);
	     PluginRequestUtil.setRequestParameter(request,"worklist_name",wlname);
	     dsICM.disconnect();
	     dsICM = null;
		
		 return json;
	}
	
}
