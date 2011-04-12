package org.kuali.rice.krms.framework.engine;

import javax.swing.event.EventListenerList;

import org.kuali.rice.krms.engine.ExecutionEnvironment;
import org.kuali.rice.krms.engine.ExecutionOptions;
import org.kuali.rice.krms.engine.ResultEvent;
import org.kuali.rice.krms.engine.ResultListener;
import org.kuali.rice.krms.framework.engine.result.EngineResultListener;
import org.kuali.rice.krms.framework.engine.result.Log4jResultListener;

public class ResultLogger {
	private EventListenerList listenerList = new EventListenerList();
	
	private ResultLogger(){}
	
	/*using inner class provides thread safety.	 */
	private static class KRMSLoggerLoader{
		private static final ResultLogger INSTANCE = new ResultLogger();
	}
	
	public static ResultLogger getInstance(){
		return KRMSLoggerLoader.INSTANCE;
	}
	
	public void addListener(ResultListener l) {
		listenerList.add(ResultListener.class, l);		
	}
	
	public void removeListener(ResultListener l){
		listenerList.remove(ResultListener.class, l);
	}
	
	/**
	 * This method is just for the POC.
	 * In the real world, adding listeners will be performed elsewhere.
	 */
	public void init(){
		addListener(new EngineResultListener());
		addListener(new Log4jResultListener());
	}
	
	public void logResult(ResultEvent event){
		if (isEnabled(event.getEnvironment())){
			// fire event to listeners
			Object[] listeners = listenerList.getListenerList();
			for (int i=1; i<listeners.length; i+=2){
				((ResultListener) listeners[i]).handleEvent(event);
			}
		}
	}

	public boolean isEnabled(ExecutionEnvironment environment){
		String logEnabled = environment.getExecutionOptions().get(ExecutionOptions.LOG_EXECUTION.toString()); 
		if (logEnabled != null ){
			return true;
		}
		return false;
	}
}
