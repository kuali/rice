/*
 * Copyright 2006-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.kew.impl.peopleflow;

import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.framework.support.krms.RulesEngineExecutor;
import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.EngineResults;
import org.kuali.rice.krms.framework.engine.EngineResultsImpl;
import org.kuali.rice.krms.impl.peopleflow.PeopleFlowActionTypeService;

import java.lang.reflect.Field;

/**
 * A test RulesEngineExecutor that doesn't actually call KRMS, it just dummies up some EngineResults and passes them
 * back.
 */
public class RulesEngineExecutorMock implements RulesEngineExecutor {

    private static String peopleFlowId = null;

    public static void setPeopleFlowId(String ppfId) {
        peopleFlowId = ppfId;
    }

    @Override
    public EngineResults execute(RouteContext routeContext, Engine engine) {
        EngineResultsImpl engineResults = new EngineResultsImpl();

        String ppfAttributeName = "";

        try {
            Field field = PeopleFlowActionTypeService.class.getDeclaredField("PEOPLE_FLOWS_SELECTED_ATTRIBUTE");
            field.setAccessible(true);
            ppfAttributeName = (String)field.get(new PeopleFlowActionTypeService(PeopleFlowActionTypeService.Type.APPROVAL));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        engineResults.setAttribute(ppfAttributeName, ActionType.APPROVE.getCode() + ":" + peopleFlowId);

        return engineResults;
    }

}
