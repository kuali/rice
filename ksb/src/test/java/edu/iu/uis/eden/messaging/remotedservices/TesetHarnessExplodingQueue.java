package edu.iu.uis.eden.messaging.remotedservices;

import java.io.Serializable;

import org.kuali.rice.exceptions.RiceRuntimeException;

import edu.iu.uis.eden.messaging.KEWJavaService;

/**
 * A service that throws exceptions
 * @author rkirkend
 *
 */
public class TesetHarnessExplodingQueue implements KEWJavaService {
	
	public static int NUM_CALLS = 0;

	public void invoke(Serializable payLoad) {
		NUM_CALLS++;
		throw new RiceRuntimeException("I'm the exploder!!!  Face the explosion!!!");
	}

}
