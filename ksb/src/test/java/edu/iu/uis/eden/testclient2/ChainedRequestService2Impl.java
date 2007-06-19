package edu.iu.uis.eden.testclient2;

import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.remotedservices.ChainedRequestService;

public class ChainedRequestService2Impl implements ChainedRequestService {

	public String sendRequest(String value) {
		StringBuffer buffer = new StringBuffer(value);
		buffer.append(Core.getCurrentContextConfig().getMessageEntity());
		return buffer.toString();
	}

}
