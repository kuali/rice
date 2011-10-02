package org.kuali.rice.kew.framework.support.krms;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;

/**
 * Serves as an interface to execution of a rules engine during execution of the workflow engine.
 *
 * TODO - this interface should really be part of the framework module, but depends on RouteContext which is currently
 * part of the impl module.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RulesEngineExecutor {

    EngineResults execute(RouteContext routeContext, Engine engine);

}
