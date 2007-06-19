package edu.iu.uis.eden.messaging.remotedservices;

import java.io.Serializable;

import edu.iu.uis.eden.messaging.KEWJavaService;

public class TestRepeatMessageQueue implements KEWJavaService {

	public static int CALL_COUNT = 0;
	
	public void invoke(Serializable payLoad) {
		CALL_COUNT++;
		System.out.println("!!!TestRepeatMessageQueue called!!!");
	}

}