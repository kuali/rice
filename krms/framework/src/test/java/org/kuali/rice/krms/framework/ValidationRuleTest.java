package org.kuali.rice.krms.framework;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.engine.ExecutionFlag;
import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.engine.TermResolutionEngine;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.engine.BasicExecutionEnvironment;
import org.kuali.rice.krms.framework.engine.ComparableTermBasedProposition;
import org.kuali.rice.krms.framework.engine.ComparisonOperator;
import org.kuali.rice.krms.framework.engine.Rule;
import org.kuali.rice.krms.framework.engine.TermResolutionEngineImpl;
import org.kuali.rice.krms.framework.engine.ValidationRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 *  Test of the @{link ValidationRule}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidationRuleTest {
    Term term = new Term(new TermSpecification("true", "String"));
    final SelectionCriteria testEvent = SelectionCriteria.createCriteria("testEvent", new DateTime(), Collections.EMPTY_MAP,
            Collections.EMPTY_MAP);
    Map<Term, Object> facts = new HashMap<Term, Object>();
    TermResolutionEngine termResolutionEngine = new TermResolutionEngineImpl();
	// Set execution options to log execution
    ExecutionOptions executionOptions = new ExecutionOptions().setFlag(ExecutionFlag.LOG_EXECUTION, true);

    @Before
    public void setUp() {
        ActionMock.resetActionsFired();
        facts.put(term, "true");
    }

    @Test
    public void testValidRuleActionDoesntFire() {
        Rule validationRule = new ValidationRule("ValidTest", new ComparableTermBasedProposition(ComparisonOperator.EQUALS, term, "true"), Collections
                .<Action>singletonList(new ActionMock("a1")));
        assertTrue(validationRule.evaluate(new BasicExecutionEnvironment(testEvent, facts, executionOptions,
                termResolutionEngine)));
        assertFalse(ActionMock.actionFired("a1"));
    }

    @Test
    public void testInvalidRuleActionFires() {
        Rule validationRule = new ValidationRule("InvalidTest", new ComparableTermBasedProposition(ComparisonOperator.EQUALS, term, "false"), Collections
                .<Action>singletonList(new ActionMock("a1")));
        assertFalse(validationRule.evaluate(new BasicExecutionEnvironment(testEvent, facts, executionOptions,
                termResolutionEngine)));
        assertTrue(ActionMock.actionFired("a1"));
    }
}
