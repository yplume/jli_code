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
//import com.ibm.ecm.extension.samplefilter.*;

/**
 * Provides an abstract class that is extended to create a filter for requests to a particular service. The filter is provided with the 
 * request parameters before being examined by the service. The filter can change the parameters or reject the request.
 */
class WorklistCountRequest_withlogin extends PluginRequestFilter {

	public String[] getFilteredServices() {
	return new String[] { "/cm/getWorkItems" };
	}

	public JSONObject filter(PluginServiceCallbacks callbacks, HttpServletRequest request, JSONArtifact jsonRequest) throws Exception {
		JSONObject json = null;
		CMBConnection conn = new CMBConnection();
		conn.setDsType("ICM");
		conn.setUserid("icmadmin");
		conn.setServerName("LSConnection");
		conn.setPassword("BigBlue1");
		conn.connect();
		CMBDocRoutingQueryServiceICM docRoutingQueryServiceICM = conn.getDocRoutingQueryServiceICM();
		String realName = "";
		String requestName = request.getParameter("worklist_name");
		String[] workLists = docRoutingQueryServiceICM.getWorkListNames();
		for(int i = 0; i < workLists.length; i++){
			CMBWorkListICM lista = docRoutingQueryServiceICM.getWorkList(workLists[i]);
			String name = lista.getName();
			if(requestName.startsWith(name)){
				realName = lista.getName();
				System.out.println("Prod realName = " + realName);
				break;
			}
		}
		PluginRequestUtil.setRequestParameter(request,"worklist_name",realName);
		conn.disconnect();
		return json;
		}
	}