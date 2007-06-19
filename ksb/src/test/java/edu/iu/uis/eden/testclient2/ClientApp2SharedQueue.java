package edu.iu.uis.eden.testclient2;

import java.io.Serializable;

import edu.iu.uis.eden.messaging.ClientAppServiceSharedPayloadObj;
import edu.iu.uis.eden.messaging.KEWJavaService;


/**
 * A service that is registered as a queue for both the client apps.  Used to test queue 
 * call scenarios.
 * 
 * @author rkirkend
 *
 */
public class ClientApp2SharedQueue implements KEWJavaService {
	
	
	public void invoke(Serializable payLoad) {

		ClientAppServiceSharedPayloadObj sharedPayload = (ClientAppServiceSharedPayloadObj) payLoad;
		if (sharedPayload.isThrowException()) {
			throw new RuntimeException("ClientAppSharedQueue throwing exception.");
		}
	}
}