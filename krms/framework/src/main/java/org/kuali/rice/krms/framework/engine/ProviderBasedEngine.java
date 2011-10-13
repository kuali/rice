package org.kuali.rice.krms.framework.engine;

import java.util.Map;

import org.joda.time.DateTime;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.ResultEvent;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.framework.engine.result.TimingResult;

public class ProviderBasedEngine implements Engine {

	private static final Term effectiveExecutionTimeTerm = new Term(new TermSpecification("effectiveExecutionTime", "java.lang.Long"), null);
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProviderBasedEngine.class);
	private static final ResultLogger KLog = ResultLogger.getInstance();

	private ContextProvider contextProvider;

	@Override
	public EngineResults execute(SelectionCriteria selectionCriteria, Map<Term, Object> facts, ExecutionOptions executionOptions) {
		DateTime start, end;
		start = new DateTime();
		ExecutionEnvironment environment = establishExecutionEnvironment(selectionCriteria, facts, executionOptions);
		
		// set execution time
		Long effectiveExecutionTime = environment.getSelectionCriteria().getEffectiveExecutionTime();
		if (effectiveExecutionTime == null) { effectiveExecutionTime = System.currentTimeMillis(); }
		environment.publishFact(effectiveExecutionTimeTerm, effectiveExecutionTime);

		Context context = selectContext(selectionCriteria, facts, executionOptions);
		if (context == null) {
			LOG.info("Failed to locate a Context for the given qualifiers, skipping rule engine execution: " + selectionCriteria.getContextQualifiers());
			return null;
		}
		context.execute(environment);
		end = new DateTime();
		if (KLog.isEnabled(environment)){
			KLog.logResult(new TimingResult(ResultEvent.TimingEvent, this, environment, start, end));
		}
		return environment.getEngineResults();
	}
	
	protected ExecutionEnvironment establishExecutionEnvironment(SelectionCriteria selectionCriteria, Map<Term, Object> facts, ExecutionOptions executionOptions) {
		return new BasicExecutionEnvironment(selectionCriteria, facts, executionOptions, new TermResolutionEngineImpl());
	}
	
	protected Context selectContext(SelectionCriteria selectionCriteria, Map<Term, Object> facts, ExecutionOptions executionOptions) {
		if (contextProvider == null) {
			throw new IllegalStateException("No ContextProvider was configured.");
		}
		return contextProvider.loadContext(selectionCriteria, facts, executionOptions);
	}
	
	
	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}
	
}
