package edu.iu.uis.eden.testclient1;

import edu.iu.uis.eden.messaging.remotedservices.EchoService;

public class EchoService1Impl implements EchoService {

	public String echo(String string) {
		return string;
	}

	public String trueEcho(String string) {
		System.out.println("I Was echoed!!!!");
		return "hi mom";
	}
}