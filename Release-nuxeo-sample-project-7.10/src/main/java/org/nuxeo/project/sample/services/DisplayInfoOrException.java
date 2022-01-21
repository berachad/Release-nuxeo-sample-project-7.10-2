package org.nuxeo.project.sample.services;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;

public class DisplayInfoOrException {
    
	public void sendMessage(OperationContext ctx, AutomationService service, String IDClass, String message) throws OperationException {
		// TODO Auto-generated method stub
		Map<String, Object> params = new HashMap<String, Object>();
		   params.put("severity" ,"WARN");
           params.put("message", message);
           service.run(ctx, IDClass, params); 
	}
	
}
