/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.edl;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

public class EDocLiteScriptedTest extends KEWTestCase {
    /**
     * Would be nice to be able to specify data to load from the script
     * but it has to be loaded here...
     */
    protected void loadTestData() throws Exception {
        //loadXmlFile("EDocLiteStyle.xml");
        loadXmlFile("edlstyle.xml");
        loadXmlFile("widgets.xml");
        // supply Group1 and Group2 for the test
        loadXmlFile("GradSchoolTestUsersAndGroups.xml");
        loadXmlFile("GradSchoolRoutingConfiguration.xml");
        loadXmlFile("GradSchoolRules.xml");
        loadXmlFile("GradSchoolEDL.xml");
    }

    @Ignore("This test needs to be implemented!")
    @Test public void test() throws Exception {
//        Map context = new HashMap();
//        EDocLitePostProcessorListener listener = new EDocLitePostProcessorListener(context);
//        listener.startListening();
//        Thread t = new Thread(listener);
//        t.setDaemon(true);
//        t.start();
//
//        Servlet servlet = new DelegatingWorkflowServlet(new EDocLiteServlet());
//        Script script = new Script(getClass().getResourceAsStream("GradSchoolScript.xml"),
//                                   new LocalWorkflowInteractionController(servlet));
//
//        script.run(context);
//
//        RouteHeaderService rhs = (RouteHeaderService) getService(SpringServiceLocator.DOC_ROUTE_HEADER_SRV);
//        DocumentRouteHeaderValue v = rhs.getRouteHeader(new Long((String) context.get("docId")));
//        //assertTrue(v.isApproved());
//        assertTrue(v.isFinal());
    }
}