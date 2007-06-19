package edu.iu.uis.eden.messaging.remotedservices;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for services deployed in the test harness and client applications to call.
 * This is useful when services are deployed in the test harness and therefore do not 
 * get called remotely and aren't recorded in the bam.
 * 
 * Holds a single hashmap that can hold whatever is needed for confirmation of call for 
 * testing.
 * 
 * @author rkirkend
 *
 */
public class ServiceCallInformationHolder {

	public static Map<String, Object> stuff = new HashMap<String, Object>();

}