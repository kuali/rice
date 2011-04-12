package org.kuali.rice.krms.framework.engine.result;

import org.kuali.rice.krms.engine.ResultEvent;
import org.kuali.rice.krms.engine.ResultListener;

public class Log4jResultListener  implements ResultListener {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Log4jResultListener.class);

	public Log4jResultListener(){}
	
	@Override
	public void handleEvent(ResultEvent resultEvent) {
		// TODO Auto-generated method stub
		if (LOG.isInfoEnabled()){
			LOG.info(resultEvent);
		}
		
	}

}
