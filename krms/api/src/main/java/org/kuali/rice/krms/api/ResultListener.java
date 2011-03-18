package org.kuali.rice.krms.api;

import java.util.EventListener;

public interface ResultListener extends EventListener {
	public void handleEvent(ResultEvent resultEvent);
}
