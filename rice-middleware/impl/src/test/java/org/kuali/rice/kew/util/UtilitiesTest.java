/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.util;

import org.junit.Test;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the various static methods in the {@link Utilities} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UtilitiesTest {

    /**
     * AR1 is active and AR2 is initialized and hence the expected result is AR1 comes first.
     */
    @Test
    public void RouteLogActionRequestSorterTest_AR1_Active() {
        ActionRequestValue ar1 = new ActionRequestValue();
        ActionRequestValue ar2 = new ActionRequestValue();

        ar1.setActionRequestId("Test1");
        ar1.setRouteLevel(1);
        ar1.setStatus("A");

        ar2.setActionRequestId("Test2");
        ar2.setRouteLevel(1);
        ar2.setStatus("I");

        List<ActionRequestValue> actionRequestValues = new ArrayList<ActionRequestValue>();
        actionRequestValues.add(ar1);
        actionRequestValues.add(ar2);

        Collections.sort(actionRequestValues, new Utilities.RouteLogActionRequestSorter());

        assertEquals("Test1", actionRequestValues.get(0).getActionRequestId());
    }

    /**
     * AR1 is Initialized and AR2 is Active and hence the expected result is AR2 comes first.
     */
    @Test
    public void RouteLogActionRequestSorterTest_AR2_Active() {
        ActionRequestValue ar1 = new ActionRequestValue();
        ActionRequestValue ar2 = new ActionRequestValue();

        ar1.setActionRequestId("Test1");
        ar1.setRouteLevel(1);
        ar1.setStatus("I");

        ar2.setActionRequestId("Test2");
        ar2.setRouteLevel(1);
        ar2.setStatus("A");

        List<ActionRequestValue> actionRequestValues = new ArrayList<ActionRequestValue>();
        actionRequestValues.add(ar1);
        actionRequestValues.add(ar2);

        Collections.sort(actionRequestValues, new Utilities.RouteLogActionRequestSorter());

        assertEquals("Test2", actionRequestValues.get(0).getActionRequestId());
    }

    /**
     * AR1 and AR2 both are active and hence the call should go to {@code PrioritySorter} and result should be based on
     * the priority Level.
     */
    @Test
    public void RouteLogActionRequestSorterTest_Both_Active() {
        ActionRequestValue ar1 = new ActionRequestValue();
        ActionRequestValue ar2 = new ActionRequestValue();
        ActionRequestValue ar3 = new ActionRequestValue();

        ar1.setActionRequestId("Test1");
        ar1.setRouteLevel(1);
        ar1.setStatus("A");
        ar1.setPriority(1);

        ar2.setActionRequestId("Test2");
        ar2.setRouteLevel(1);
        ar2.setStatus("A");
        ar2.setPriority(2);

        ar3.setActionRequestId("Test3");
        ar3.setRouteLevel(1);
        ar3.setStatus("I");

        List<ActionRequestValue> actionRequestValues = new ArrayList<ActionRequestValue>();
        actionRequestValues.add(ar1);
        actionRequestValues.add(ar2);
        actionRequestValues.add(ar3);

        Collections.sort(actionRequestValues, new Utilities.RouteLogActionRequestSorter());

        assertEquals("Test1", actionRequestValues.get(0).getActionRequestId());
        assertEquals("Test2", actionRequestValues.get(1).getActionRequestId());
        assertEquals("Test3", actionRequestValues.get(2).getActionRequestId());
    }
}
