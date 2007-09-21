package edu.iu.uis.eden.messaging;

import org.junit.Ignore;
import org.junit.Test;

public class SimpleServiceCallNoJtaTest extends SimpleServiceCallTest {

        @Ignore
	@Test public void testAsyncJavaCall() throws Exception  {
		super.testAsyncJavaCall();
	}
	
        @Ignore
	@Test public void testAsyncXmlCall() throws Exception {
	    super.testAsyncXmlCall();
	}
	
	@Override
	protected boolean disableJta() {
		return true;
	}
}