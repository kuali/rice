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
package edu.sampleu.travel.dataobject;

import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests basic {@code TravelCompany} persistence.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
public class TravelCompanyTest extends KRADTestCase {

    private static final String COMPANY_NAME = "Get Away! Travel";

    /**
     * Tests basic {@code TravelCompany} persistence by saving it, reloading it, and checking the data.
     */
    @Test
    public void testTravelCompany() {
        assertTrue(TravelCompany.class.getName() + " is not mapped in JPA",
                KRADServiceLocator.getDataObjectService().supports(TravelCompany.class));

        String id = createTravelCompany();

        TravelCompany travelCompany = KRADServiceLocator.getDataObjectService().find(TravelCompany.class, id);
        assertNotNull("Travel Company ID is null", travelCompany.getTravelCompanyId());
        assertEquals("Travel Company name is incorrect", COMPANY_NAME, travelCompany.getTravelCompanyName());
        assertTrue("Travel Company is not active", travelCompany.isActive());
    }

    private String createTravelCompany() {
        TravelCompany travelCompany = new TravelCompany();
        travelCompany.setTravelCompanyName(COMPANY_NAME);
        travelCompany.setActive(true);

        return KRADServiceLocator.getDataObjectService().save(travelCompany).getTravelCompanyId();
    }
}
