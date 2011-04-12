package org.kuali.rice.krms.api.engine;

import java.util.Date;

public interface ResultEvent {
	public ExecutionEnvironment getEnvironment();

	public String getType();
	public Object getSource();
	public Date getTimestamp();
	public Boolean getResult();
	
	public static final String RuleEvaluated = "Rule Evaluated";
	public static final String PropositionEvaluated = "Proposition Evaluated";
	public static final String ActionExecuted = "Action Executed";
	public static final String TimingEvent = "Timing Event";
}
