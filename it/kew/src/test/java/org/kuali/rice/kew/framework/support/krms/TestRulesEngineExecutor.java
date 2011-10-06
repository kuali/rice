package org.kuali.rice.kew.framework.support.krms;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;

import java.util.HashMap;
import java.util.Map;

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
