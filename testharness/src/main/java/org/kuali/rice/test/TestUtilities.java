package org.kuali.rice.test;

import junit.framework.Assert;

public class TestUtilities {

	
	private static Thread exceptionThreader;
	

	
	/**
     * Waits "indefinately" for the exception routing thread to terminate.
     *
     * This actually doesn't wait forever but puts an upper bound of 5 minutes
     * on the time to wait for the exception routing thread to complete.  If a
     * document cannot go into exception routing within 5 minutes  then we got
     * problems.
     */
    public static void waitForExceptionRouting() {
    	waitForExceptionRouting(5*60*1000);
    }

    public static void waitForExceptionRouting(long milliseconds) {
    	try {
    		getExceptionThreader().join(milliseconds);
    	} catch (InterruptedException e) {
    		Assert.fail("This thread was interuppted while waiting for exception routing.");
    	}
    	if (getExceptionThreader().isAlive()) {
    		Assert.fail("Document was not put into exception routing within the specified amount of time " + milliseconds);
    	}
    }

    public static Thread getExceptionThreader() {
        return exceptionThreader;
    }

    public static void setExceptionThreader(Thread exceptionThreader) {
        TestUtilities.exceptionThreader = exceptionThreader;
    }	
    

}