package edu.iu.uis.eden.testclient2;

import edu.iu.uis.eden.messaging.remotedservices.EchoService;

public class EchoService2Impl implements EchoService {

	public String echo(String string) {	
		return string;
	}

	public String trueEcho(String string) {
		return string;
	}

}
