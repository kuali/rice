package edu.iu.uis.eden.messaging;

import org.junit.Test;

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