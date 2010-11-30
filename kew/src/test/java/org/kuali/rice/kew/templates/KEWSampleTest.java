/*
 * Copyright 2005-2010 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kew.templates;

import org.junit.Test;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.test.SQLDataLoader;

import java.util.List;

/**
 * An example class for KEW test cases. Handles exposing test utilities and
 * provides templates for custom tests.
 */
public class KEWSampleTest extends KEWTestCase {

    /*
    * This method allows you to load test data as a resource. This example file is located at
    * kew/src/test/resources/org/kuali/rice/kew/templates/SampleTemplateConfig.xml
    */
    protected void loadTestData() throws Exception {
		loadXmlFile("SampleTemplateConfig.xml");
        new SQLDataLoader("classpath:org/kuali/rice/kew/templates/documentUpdate.sql", ";").runSql();


	}

    /*
    * This overrides the parent method to perform setup work inside of a database transaction.
    *
    */
    protected void setUpAfterDataLoad() throws Exception {
		super.setUpAfterDataLoad();
        ActionListService actionListService = KEWServiceLocator.getActionListService();
	}

    /**
     * Overridden to introduce our own client-side beans
     * @see org.kuali.rice.kew.test.KEWTestCase#getKEWBootstrapSpringFile()
     */
    @Override
    protected String getKEWBootstrapSpringFile() {
        return "classpath:org/kuali/rice/kew/templates/OverridingTestSpringBeans.xml";
    }


    /*
    * We want to test that our document type that we created within the SampleTemplateConfig can be accessed by the
    * KEW Unit Test framework and then we want to assert that it routes properly */
    @Test
    public void testKEWSampleWorks() throws Exception {
        assertTrue(true);
        String tempPrincipalId = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("arh14").getPrincipalId();
        WorkflowDocument document = new WorkflowDocument(tempPrincipalId, "SampleDocumentType");
        document.routeDocument("");
        TestUtilities.assertNotInActionList(tempPrincipalId, document.getRouteHeaderId() );
        TestUtilities.assertInActionList("admin", document.getRouteHeaderId() );
        TestUtilities.assertNumberOfPendingRequests(document.getRouteHeaderId(), 1);
    }


}
