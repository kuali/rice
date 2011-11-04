/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.framework.engine;

import javax.swing.event.EventListenerList;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.framework.engine.result.EngineResultListener;
import org.kuali.rice.krms.framework.engine.result.Log4jResultListener;
import org.kuali.rice.krms.framework.engine.result.ResultListener;

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
	    return (
	            environment != null 
	            && environment.getExecutionOptions() != null 
	            && environment.getExecutionOptions().getFlag(ExecutionFlag.LOG_EXECUTION)
	    );
	}
}
