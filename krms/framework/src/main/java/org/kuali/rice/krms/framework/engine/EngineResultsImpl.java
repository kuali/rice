package org.kuali.rice.krms.framework.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.Term;


public class EngineResultsImpl implements EngineResults {
	
	private List<ResultEvent> results = new ArrayList<ResultEvent>();
	private Map<Object, Set<Term>> termPropositionMap;
	
	@Override
	public void addResult(ResultEvent result) {
		results.add(result);
	}

	@Override
	public List<ResultEvent> getAllResults() {		
		return new ArrayList<ResultEvent>(results); // shallow copy should be defensive enough
	}

	@Override
	public ResultEvent getResultEvent(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResultEvent> getResultsOfType(String type) {
		// TODO Auto-generated method stub
		ArrayList<ResultEvent> newList = new ArrayList<ResultEvent>();
		if (type == null) return newList;
		for (int i=0; i<results.size(); i++){
			if (type.equalsIgnoreCase(results.get(i).getType())){
				newList.add(results.get(i));
			}
		}
		return newList;
	}
}
