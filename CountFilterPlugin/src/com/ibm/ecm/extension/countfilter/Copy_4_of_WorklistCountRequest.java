package com.ibm.ecm.extension.countfilter;


import javax.servlet.http.HttpServletRequest;

import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginRequestUtil;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.*;
import com.ibm.mm.beans.*;
import com.ibm.mm.beans.workflow.*;
import com.ibm.ecm.extension.*;
import com.ibm.mm.sdk.server.*;
import com.ibm.mm.sdk.common.*;

/**
 * Provides an abstract class that is extended to create a filter for requests to a particular service. The filter is provided with the 
 * request parameters before being examined by the service. The filter can change the parameters or reject the request.
 */
public  class Copy_4_of_WorklistCountRequest extends PluginRequestFilter {

	/**
	 * Returns the names of the services that are extended by this filter.
	 * 
	 * @return A <code>String</code> array that contains the names of the services.
	 */
	public String[] getFilteredServices() {
		return new String[] { "/cm/getWorkItems" };
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
	public JSONObject filter(PluginServiceCallbacks callbacks, HttpServletRequest request, JSONArtifact jsonRequest) throws Exception {
		JSONObject json = null;
		String requestName = request.getParameter("worklist_name");
		String wlname = requestName.substring(0, requestName.indexOf("(")).trim();
		
		System.out.println("jsonObject wlname = "+wlname);
		
		DKDatastoreICM dsICM = null;    
		 //conn.setDsType("ICM");
		//conn.setUserid(ConfigurationService.obtenerParametroSimple(callbacks,0));
		 //conn.setPassword(ConfigurationService.obtenerParametroSimple(callbacks,1));
		 //conn.setServerName(ConfigurationService.obtenerParametroSimple(callbacks,2));
		 //conn.setUserid("icmadmin");
		// conn.setUserid(callbacks.getUserId());
		 //conn.setPassword("BigBlue1");
		 
		 dsICM = callbacks.getCMDatastore("cm");
		 //dsICM = callbacks.getCMDatastore("CMRepository");
		 //System.out.println("after get dsICM");
		 //DKDocRoutingServiceMgmtICM routingMgmt = new DKDocRoutingServiceMgmtICM(dsICM);
		 //DKSequentialCollection workLists = (DKSequentialCollection) routingMgmt.listWorkLists();
		 //DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
		 
		PluginRequestUtil.setRequestParameter(request,"worklist_name",wlname);
		//PluginRequestUtil.setRequestParameter(request,"worklist_name",realName);
		 //conn.disconnect();
		 return json;
	}
	
}
