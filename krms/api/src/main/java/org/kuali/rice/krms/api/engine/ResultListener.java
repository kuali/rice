package org.kuali.rice.krms.api.engine;

import java.util.EventListener;

public interface ResultListener extends EventListener {
	public void handleEvent(ResultEvent resultEvent);
}
