package edu.iu.uis.eden.messaging.remotedservices;

public class GenericTestService implements TestServiceInterface {

	public static int NUM_CALLS = 0;
	
	public void invoke() {
		NUM_CALLS++;
	}

}
