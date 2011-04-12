package org.kuali.rice.krms.engine;

import java.util.List;

public interface EngineResults {

	// TODO - need to determine what goes here...
	public ResultEvent getResultEvent(int index);
	public List<ResultEvent> getAllResults();
	public List<ResultEvent> getResultsOfType(String type);
	
	public void addResult(ResultEvent result);
}
