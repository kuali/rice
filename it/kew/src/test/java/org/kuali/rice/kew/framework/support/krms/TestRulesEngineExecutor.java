package org.kuali.rice.kew.framework.support.krms;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.Facts;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple RulesEngineExecutor used by the integration tests which is hard-coded to select a context with
 * namespaceCode="KR-RULE" and name="MyContext".  It also is hardcoded to select an agenda from the context with an
 * event name of "workflow".
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestRulesEngineExecutor implements RulesEngineExecutor {

    private static final String CONTEXT_NAMESPACE_CODE = KrmsConstants.KRMS_NAMESPACE;
    private static final String CONTEXT_NAME = "MyContext";
    private static final String EVENT_NAME = "workflow";

    @Override
    public EngineResults execute(RouteContext routeContext, Engine engine) {
        Map<String, String> contextQualifiers = new HashMap<String, String>();
        contextQualifiers.put("namespaceCode", CONTEXT_NAMESPACE_CODE);
        contextQualifiers.put("name", CONTEXT_NAME);
        SelectionCriteria sectionCriteria = SelectionCriteria.createCriteria(null, contextQualifiers,
                Collections.singletonMap(AgendaDefinition.Constants.EVENT, EVENT_NAME));
        return engine.execute(sectionCriteria, Facts.EMPTY_FACTS, null);
    }
}
