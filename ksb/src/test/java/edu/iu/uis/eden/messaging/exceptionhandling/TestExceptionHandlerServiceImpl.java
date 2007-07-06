package edu.iu.uis.eden.messaging.exceptionhandling;

import org.kuali.rice.test.TestUtilities;

import edu.iu.uis.eden.messaging.PersistedMessage;

public class TestExceptionHandlerServiceImpl extends DefaultExceptionServiceImpl {

	public void placeInExceptionRouting(Throwable throwable, PersistedMessage message, Object service) {
		ExceptionThreader exceptionThreader = new ExceptionThreader(throwable, message, service, this);
		exceptionThreader.start();
	}
	
	private static class ExceptionThreader extends Thread {

		private Throwable throwable;
		private PersistedMessage message;
		private Object service;
		private TestExceptionHandlerServiceImpl testExceptionService;
		
		public ExceptionThreader(Throwable throwable, PersistedMessage message, Object service, TestExceptionHandlerServiceImpl testExceptionService) {
			this.throwable = throwable;
			this.message = message;
			this.service = service;
			this.testExceptionService = testExceptionService;
			TestUtilities.setExceptionThreader(this);
		}

		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    this.testExceptionService.callRealPlaceInExceptionRouting(this.throwable, this.message, this.service);
		}
	}
	
	public void callRealPlaceInExceptionRouting(Throwable throwable, PersistedMessage message, Object service) {
		super.placeInExceptionRouting(throwable, message, service);
	}
	
}
