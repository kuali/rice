package org.kuali.rice.kew.framework.support.krms;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;

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

    @Override
    public EngineResults execute(RouteContext routeContext, Engine engine) {
        Map<String, String> contextQualifiers = new HashMap<String, String>();
        contextQualifiers.put("namespaceCode", "KR-RULE");
        contextQualifiers.put("name", "MyContext");
        SelectionCriteria sectionCriteria = SelectionCriteria.createCriteria("workflow", null, contextQualifiers, null);
        return engine.execute(sectionCriteria, new HashMap<Term, Object>(), null);
    }
}
