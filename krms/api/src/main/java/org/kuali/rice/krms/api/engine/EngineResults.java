package org.kuali.rice.krms.api.engine;

import java.util.List;

public interface EngineResults {

	// TODO - need to determine what goes here...
	public ResultEvent getResultEvent(int index);
	public List<ResultEvent> getAllResults();
	public List<ResultEvent> getResultsOfType(String type);
	public Object getAttribute(String key);
	public void setAttribute(String key, Object attr);
	
	public void addResult(ResultEvent result);
}
