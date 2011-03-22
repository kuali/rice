package org.kuali.rice.krms.framework.engine;

import java.util.Date;
import java.util.Map;

import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.Context;
import org.kuali.rice.krms.api.ContextProvider;
import org.kuali.rice.krms.api.Engine;
import org.kuali.rice.krms.api.EngineResults;
import org.kuali.rice.krms.api.ExecutionEnvironment;
import org.kuali.rice.krms.api.ResultEvent;
import org.kuali.rice.krms.api.SelectionCriteria;
import org.kuali.rice.krms.framework.engine.result.TimingResult;

public class ProviderBasedEngine implements Engine {

	private static final Asset effectiveExecutionTimeAsset = new Asset("effectiveExecutionTime", "java.lang.Long");
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProviderBasedEngine.class);
	private static final ResultLogger KLog = ResultLogger.getInstance();

	private ContextProvider contextProvider;
	
	@Override
	public EngineResults execute(SelectionCriteria selectionCriteria, Map<Asset, Object> facts, Map<String, String> executionOptions) {
		Date start, end;
		start = new Date();
		ExecutionEnvironment environment = establishExecutionEnvironment(selectionCriteria, facts, executionOptions);
		
		// set execution time
		Long effectiveExecutionTime = environment.getSelectionCriteria().getEffectiveExecutionTime();
		if (effectiveExecutionTime == null) { effectiveExecutionTime = System.currentTimeMillis(); }
		environment.publishFact(effectiveExecutionTimeAsset, effectiveExecutionTime);

		Context context = selectContext(selectionCriteria, facts, executionOptions);
		if (context == null) {
			LOG.info("Failed to locate a Context for the given qualifiers, skipping rule engine execution: " + selectionCriteria.getContextQualifiers());
			return null;
		}
		context.execute(environment);
		end = new Date();
		if (KLog.isEnabled(environment)){
			KLog.logResult(new TimingResult(ResultEvent.TimingEvent, this, environment, start, end));
		}
		return environment.getEngineResults();
	}
	
	protected ExecutionEnvironment establishExecutionEnvironment(SelectionCriteria selectionCriteria, Map<Asset, Object> facts, Map<String, String> executionOptions) {
		return new BasicExecutionEnvironment(selectionCriteria, facts, executionOptions);
	}
	
	protected Context selectContext(SelectionCriteria selectionCriteria, Map<Asset, Object> facts, Map<String, String> executionOptions) {
		if (contextProvider == null) {
			throw new IllegalStateException("No ContextProvider was configured.");
		}
		return contextProvider.loadContext(selectionCriteria, facts, executionOptions);
	}
	
	
	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

}
