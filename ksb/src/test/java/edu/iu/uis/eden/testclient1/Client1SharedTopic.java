package edu.iu.uis.eden.testclient1;


import java.io.Serializable;

import edu.iu.uis.eden.messaging.KEWJavaService;
import edu.iu.uis.eden.messaging.remotedservices.ServiceCallInformationHolder;

public class Client1SharedTopic implements KEWJavaService {

	public void invoke(Serializable payLoad) {
		ServiceCallInformationHolder.stuff.put("Client1Called", Boolean.TRUE);
	}

}
