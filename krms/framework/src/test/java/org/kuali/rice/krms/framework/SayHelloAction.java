package org.kuali.rice.krms.framework;

import org.kuali.rice.krms.api.Action;
import org.kuali.rice.krms.api.ExecutionEnvironment;
import org.kuali.rice.krms.api.ResultEvent;
import org.kuali.rice.krms.framework.engine.ResultLogger;
import org.kuali.rice.krms.framework.engine.result.BasicResult;

/**
 * A test action class for the KRMS POC
 *
 */
public class SayHelloAction implements Action {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SayHelloAction.class);
	private static final ResultLogger KLog = ResultLogger.getInstance();
	
	public SayHelloAction(){}
	
	@Override
	public void execute(ExecutionEnvironment environment) {
		LOG.info("Hello!  Im executing an action.");
		KLog.logResult(new BasicResult(ResultEvent.ActionExecuted, this, environment));
	}
	
	@Override
	public void executeSimulation(ExecutionEnvironment environment) {
		throw new UnsupportedOperationException();
	}

	public String toString(){
		return getClass().getSimpleName();
	}
}
