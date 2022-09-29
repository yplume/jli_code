// 
// Production code
// 

package com.ibm.ecm.extension.countfilter;

import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.ibm.mm.sdk.common.DKWorkListICM;
import com.ibm.json.java.JSONArray;
import com.ibm.mm.sdk.common.DKDocRoutingServiceICM;
import com.ibm.mm.sdk.common.DKSequentialCollection;
import com.ibm.mm.sdk.common.dkDatastore;
import com.ibm.mm.sdk.common.DKDocRoutingServiceMgmtICM;
import com.ibm.json.java.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.extension.PluginResponseFilter;

public class WorklistCountResponse_wirhlogin extends PluginResponseFilter
{
    public String[] getFilteredServices() {
        return new String[] { "/cm/getWorklists" };
    }
    
    public void filter(final String serverType, final PluginServiceCallbacks callbacks, final HttpServletRequest request, final JSONObject jsonResponse) {
        try {
            String user = callbacks.getUserId();
            System.out.println("user = " + user);
            DKDatastoreICM dsICM = null;
            //dsICM = callbacks.getCMDatastore("CMRepository"); //prod
            dsICM = callbacks.getCMDatastore("cm"); //dev
            DKDocRoutingServiceMgmtICM routingMgmt = new DKDocRoutingServiceMgmtICM((dkDatastore)dsICM);
            DKSequentialCollection workLists = (DKSequentialCollection)routingMgmt.listWorkLists();
            DKDocRoutingServiceICM routingService = new DKDocRoutingServiceICM((dkDatastore)dsICM);
            System.out.println("dsICM = " + dsICM);
            JSONObject jsonData = (JSONObject)jsonResponse.get((Object)"datastore");
            JSONArray jsonItems = (JSONArray)jsonData.get((Object)"items");
            int i = 0;
            final dkIterator iter = workLists.createIterator();
            while (iter.more()) {
                DKWorkListICM workList = (DKWorkListICM)iter.next();
                JSONObject jsonWorkList = (JSONObject)jsonItems.get(i);
                String name = String.valueOf(workList.getName()) + " (" + routingService.getCount(workList.getName(), "") + ")";
                System.out.println("worklist name = " + name);
                jsonWorkList.put((Object)"worklist_name", (Object)name);
                ++i;
            }
            dsICM.disconnect();
            dsICM = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}