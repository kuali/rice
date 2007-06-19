package edu.iu.uis.eden.messaging.remotedservices;

/**
 * A simple service which we can use to effectively "touch" the servers in a system
 * 
 * @author Eric Westfall
 */
public interface ChainedRequestService {

	/**
	 * Should take the given String value and append the message entity of the current server onto the end
	 * with a comma in between.  Then it should forward the call of to another server.
	 */
	public String sendRequest(String value);
	
}
