package edu.iu.uis.eden.messaging.remotedservices;

import java.io.Serializable;

import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.KEWJavaService;

public class TestHarnessSharedTopic implements KEWJavaService {
	
	public static int CALL_COUNT = 0;

	public void invoke(Serializable payLoad) {
		CALL_COUNT++;
		System.out.println("!!!TestHarnessSharedTopic called with M.E " + Core.getCurrentContextConfig().getMessageEntity() + " !!! ");
		ServiceCallInformationHolder.stuff.put("TestHarnessCalled", Boolean.TRUE);
	}

}
