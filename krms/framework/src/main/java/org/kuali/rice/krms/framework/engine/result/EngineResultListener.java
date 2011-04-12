package org.kuali.rice.krms.framework.engine.result;

import org.kuali.rice.krms.api.engine.ResultEvent;

public class EngineResultListener implements ResultListener {

	public EngineResultListener(){}

	public void handleEvent (ResultEvent event){
			event.getEnvironment().getEngineResults().addResult(event);
	}
	
}
