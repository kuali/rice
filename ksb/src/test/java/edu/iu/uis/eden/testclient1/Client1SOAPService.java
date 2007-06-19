package edu.iu.uis.eden.testclient1;


import edu.iu.uis.eden.messaging.remotedservices.SOAPService;
import edu.iu.uis.eden.messaging.remotedservices.ServiceCallInformationHolder;

/**
 *
 * @author rkirkend
 */
public class Client1SOAPService implements SOAPService {

	public String doTheThing(String param) {
		ServiceCallInformationHolder.stuff.put("Client1SOAPServiceCalled", Boolean.TRUE);
		return "Client1SOAPService";
	}

}
