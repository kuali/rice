/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.travel.krad.test;

import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.bo.TravelAccountExtension;
import edu.sampleu.travel.bo.TravelAccountType;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.location.impl.campus.CampusTypeBo;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.test.BaseMaintenanceDocumentTest;
/*
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.test.BaseMaintenanceDocumentTest;
*/

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;

/**
 * TravelAccountMaintenanceDocumentTest tests that the TravelAccountMaintenanceDocument can be routed to final
 *
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class TravelAccountMaintenanceDocumentTest extends BaseMaintenanceDocumentTest {
    private BusinessObjectService businessObjectService;
    String travAcctNumber = "8097";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        businessObjectService = KRADServiceLocator.getBusinessObjectService();

    }

    @Override
    protected Object getNewMaintainableObject()  {
        // create new account
        TravelAccount ac = new TravelAccount();
        ac.setName("unit-test-acc");
        ac.setSubsidizedPercent(new KualiPercent(0.02));
        ac.setNumber(travAcctNumber);

        //set the account type
        TravelAccountExtension travExtension = new TravelAccountExtension();

        HashMap<String, String> primaryKeys = new HashMap<String, String>(1);
        primaryKeys.put("accountTypeCode", "EAT");
        TravelAccountType expAcctType = businessObjectService.findByPrimaryKey(TravelAccountType.class, primaryKeys);
        travExtension.setAccountType(expAcctType);
        travExtension.setNumber(travAcctNumber);

        ac.setExtension(travExtension);


        return ac;
    }

    @Override
    protected String getDocumentTypeName() {
        return "TravelAccountMaintenanceDocument";
    }

    @Override
    protected String getInitiatorPrincipalName() {
        return "admin";
    }

    @Override
    protected Object getOldMaintainableObject() {
        return getNewMaintainableObject();
    }

    @Test
    /**
     * test that a validation error occurs when a business object is missing required fields
     */
    public void testRouteNewDoc() throws WorkflowException {
        setupNewAccountMaintDoc(getDocument());
        KRADServiceLocatorWeb.getDocumentService().routeDocument(getDocument(), "submit", null);
        Assert.assertTrue(getDocument().getDocumentHeader().getWorkflowDocument().isFinal());
        //check for account
        HashMap<String, String> primaryKeys = new HashMap<String, String>(1);
        primaryKeys.put("number", travAcctNumber);
        TravelAccount travAcct = businessObjectService.findByPrimaryKey(TravelAccount.class, primaryKeys);
        assertNotNull(travAcct);
    }
}
