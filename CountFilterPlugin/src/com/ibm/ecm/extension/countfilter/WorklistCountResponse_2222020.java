package com.ibm.ecm.extension.countfilter;

import javax.servlet.http.HttpServletRequest;

import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.*;
import com.ibm.mm.beans.*;
import com.ibm.mm.beans.workflow.*;
import com.ibm.mm.sdk.server.*;
import com.ibm.mm.sdk.common.*;

/**
 * Provides an abstract class that is extended to create a filter for responses
 * from a particular service. The response from the service is provided to the
 * filter in JSON format before it is returned to the web browser. The filter
 * can then modify that response, and the modified response is returned to the
 * web browser.
 */
public class WorklistCountResponse_2222020 extends PluginResponseFilter {

	public String[] getFilteredServices() {
		return new String[] { "/cm/getWorklists" };
	} 

	public void filter(String serverType, PluginServiceCallbacks callbacks,
		HttpServletRequest request, JSONObject jsonResponse) {
		try{
			String user = callbacks.getUserId();
			System.out.println("Get user = "+user);
            
			DKDatastoreICM dsICM = new DKDatastoreICM();  // Create new datastore object.
            dsICM.connect("LSConnection","icmadmin","BigBlue1",""); 
            /*conn.setDsType("ICM");
			conn.setUserid("icmadmin");
			conn.setServerName("LSConnection");
			conn.setPassword("BigBlue1");
			conn.connect();
			CMBDocRoutingQueryServiceICM docRoutingQueryServiceICM = conn.getDocRoutingQueryServiceICM();
			*/
            DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);     //
            DKDocRoutingServiceMgmtICM routingMgmt = new DKDocRoutingServiceMgmtICM(dsICM);
            dkCollection workLists = routingMgmt.listWorkLists(); 
            dkIterator iter = workLists.createIterator();
			JSONObject jsonData = (JSONObject)jsonResponse.get("datastore");
			JSONArray jsonItems = (JSONArray)jsonData.get("items");
			/*for(int i = 0; i < jsonItems.size(); i++){
				JSONObject jsonWorkList = (JSONObject)jsonItems.get(i);
				DKWorkListICM workList = routingService.getCount(arg0, arg1);
				CMBWorkListICM lista = routingMgmt..getWorkList((String)jsonWorkList.get("worklist_name"));
				lista.setSelectionFilterOnOwner(DKConstantICM.DK_ICM_DR_SELECTION_FILTER_YES);
				System.out.print("get owner = "+lista.getSelectionFilterOnOwner());
				//lista..SelectionFilterOnOwner(DKConstantICM.DK_ICM_DR_SELECTION_FILTER_YES);
				String name = lista.getName() + " (" + routingService.getCount((lista.getName(),user) + ")";
				jsonWorkList.put("worklist_name", name);
			}*/
			System.out.println("get jsonItems size = "+jsonItems.size());
			System.out.println("get jsonResponse = "+jsonResponse);
			
			while(iter.more()){
	            DKWorkListICM workList = (DKWorkListICM) iter.next();  // Move pointer to next element and obtain that next element.   
	            workList.setSelectionFilterOnOwner(DKConstantICM.DK_ICM_DR_SELECTION_FILTER_YES);
	            System.out.println("get getSelectionFilterOnOwner = "+workList.getSelectionFilterOnOwner());
	            
	            System.out.println("     - "+workList.getName()+":  "+workList.getDescription());
	            for(int i = 0; i < jsonItems.size(); i++){
	            	
					JSONObject jsonWorkList = (JSONObject)jsonItems.get(i);
					System.out.println("get worklist_name = "+(String)jsonWorkList.get("worklist_name"));
					
					if (workList.getName().equalsIgnoreCase((String)jsonWorkList.get("worklist_name").toString().trim())) {
						System.out.println("get owner = "+workList.getSelectionFilterOnOwner()+user);
						System.out.println("get count = "+routingService.getCount(workList.getName(),user));
						
						//lista..SelectionFilterOnOwner(DKConstantICM.DK_ICM_DR_SELECTION_FILTER_YES);
						String name = workList.getName() + " (" + routingService.getCount(workList.getName(),user) + ")";
						jsonWorkList.put("worklist_name", name);
					}
				}
	        }
	        
			dsICM.disconnect();
			dsICM = null;
			}catch (Exception e){
			e.printStackTrace();
			}
			
			
		/*HttpServletRequest request, JSONObject jsonResponse) {
		 try{
			 String user = callbacks.getUserId();
			 System.out.println("User = "+user);
			 DKDatastoreICM dsICM = null;    
			 
			 dsICM = callbacks.getCMDatastore("cm");
			 //dsICM = callbacks.getCMDatastore("CMRepository");
			 //System.out.println("after get dsICM");
			 System.out.println("1111");
			 DKDocRoutingServiceMgmtICM routingMgmt = new DKDocRoutingServiceMgmtICM(dsICM);
			 System.out.println("222");
			 
			 DKSequentialCollection workLists = (DKSequentialCollection) routingMgmt.listWorkLists();
			 System.out.println("333");
			 DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM(dsICM);
			 System.out.println("444");
			 
			 //System.out.println("userid = "+callbacks.getUserId());
			 System.out.println("dsICM = "+dsICM);
			 
			 
			 JSONObject jsonData = (JSONObject)jsonResponse.get("datastore");
			 JSONArray jsonItems = (JSONArray)jsonData.get("items");
			 int i = 0;
			 dkIterator iter = workLists.createIterator();
			 DKWorkListICM workList; 
		     while (iter.more()) { // loop each worklist 
			    workList = (DKWorkListICM) iter.next();
			    JSONObject jsonWorkList = (JSONObject)jsonItems.get(i);   
			    String name = workList.getName() + " (" + routingService.getCount(workList.getName(), user) + ")";
				System.out.println("name = "+name);
				jsonWorkList.put("worklist_name", name);
				i++;
			 }
		     dsICM.disconnect();
		     dsICM = null;
		 }catch (Exception e){
			 e.printStackTrace();
		 }	*/
		 
	}
}
