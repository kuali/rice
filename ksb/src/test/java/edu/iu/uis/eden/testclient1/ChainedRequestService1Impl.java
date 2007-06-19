package edu.iu.uis.eden.testclient1;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.messaging.remotedservices.ChainedRequestService;


public class ChainedRequestService1Impl implements ChainedRequestService {

	public String sendRequest(String value) {
		StringBuffer buffer = new StringBuffer(value);
		buffer.append(Core.getCurrentContextConfig().getMessageEntity()).append(",");
		ChainedRequestService service = (ChainedRequestService)GlobalResourceLoader.getService(new QName("TestCl2", "chainedRequestService2"));
		return service.sendRequest(buffer.toString());
	}

}
