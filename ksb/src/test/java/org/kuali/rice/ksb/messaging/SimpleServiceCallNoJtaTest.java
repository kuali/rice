package org.kuali.rice.ksb.messaging;

import org.junit.Ignore;
import org.junit.Test;

/**
 * This test is currently being ignored.  See KULRICE-1852 for details.
 */
@Ignore
public class SimpleServiceCallNoJtaTest extends SimpleServiceCallTest {

	@Test public void testAsyncJavaCall() throws Exception  {
		super.testAsyncJavaCall();
	}
	
	@Test public void testAsyncXmlCall() throws Exception {
	    super.testAsyncXmlCall();
	}
	
	@Override
	protected boolean disableJta() {
		return true;
	}
}