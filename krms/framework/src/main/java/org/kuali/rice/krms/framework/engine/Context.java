package org.kuali.rice.krms.framework.engine;

import java.util.List;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.TermResolver;

/**
 * The context represents the area(s) of an organization's activity where a
 * rule applies and where the terms used to create the rule are defined and relevant.
 * An equivalent phrase often used is business domain. Rules only make sense in a
 * particular context and because they must be evaluated against the information in
 * that domain or context.
 * 
 * <p>For example, rules that are specifically authored and
 * that are meaningful in an application on a Research Proposal would be most
 * unlikely to make sense or be relevant in the context of a Student Record even
 * if the condition could be evaluated.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface Context {

	void execute(ExecutionEnvironment environment);
	
	List<TermResolver<?>> getTermResolvers();
	
	boolean appliesTo(ExecutionEnvironment environment);
	
}
