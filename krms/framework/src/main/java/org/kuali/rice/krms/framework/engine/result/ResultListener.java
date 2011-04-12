package org.kuali.rice.krms.framework.engine.result;

import java.util.EventListener;

import org.kuali.rice.krms.api.engine.ResultEvent;

public interface ResultListener extends EventListener {
	public void handleEvent(ResultEvent resultEvent);
}
