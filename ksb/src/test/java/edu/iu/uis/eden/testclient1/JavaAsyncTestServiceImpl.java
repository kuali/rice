package edu.iu.uis.eden.testclient1;

import java.io.Serializable;

import edu.iu.uis.eden.messaging.KEWJavaService;

public class JavaAsyncTestServiceImpl implements KEWJavaService {

	public void invoke(Serializable payLoad) {
		System.out.println("Payload was recieved " + payLoad);
	}	
}
