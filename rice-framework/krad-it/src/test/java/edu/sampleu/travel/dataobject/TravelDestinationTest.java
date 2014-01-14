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

import edu.sampleu.travel.options.PostalCountryCode;
import edu.sampleu.travel.options.PostalStateCode;
import edu.sampleu.travel.options.TripType;
import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests basic {@code TravelDestination} persistence.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
public class TravelDestinationTest extends KRADTestCase {

    private static final String DESTINATION_NAME = PostalStateCode.PR.getLabel();
    private static final String COUNTRY_CODE = PostalCountryCode.US.getCode();
    private static final String STATE_CODE = PostalStateCode.PR.getCode();

    /**
     * Tests basic {@code TravelDestination} persistence by saving it, reloading it, and checking the data.
     */
    @Test
    public void testTravelDestination() {
        assertTrue(TravelDestination.class.getName() + " is not mapped in JPA",
                KRADServiceLocator.getDataObjectService().supports(TravelDestination.class));

        String id = createTravelDestination();

        TravelDestination travelDestination = KRADServiceLocator.getDataObjectService().find(TravelDestination.class, id);
        assertNotNull("Travel Destination ID is null", travelDestination.getTravelDestinationId());
        assertEquals("Travel Destination name is incorrect", DESTINATION_NAME, travelDestination.getTravelDestinationName());
        assertEquals("Travel Destination country is incorrect", COUNTRY_CODE, travelDestination.getCountryCd());
        assertEquals("Travel Destination state is incorrect", STATE_CODE, travelDestination.getStateCd());
        assertTrue("Travel Destination is not active", travelDestination.isActive());
    }

    private String createTravelDestination() {
        TravelDestination travelDestination = new TravelDestination();
        travelDestination.setTravelDestinationName(DESTINATION_NAME);
        travelDestination.setCountryCd(COUNTRY_CODE);
        travelDestination.setStateCd(STATE_CODE);
        travelDestination.setActive(true);

        return KRADServiceLocator.getDataObjectService().save(travelDestination).getTravelDestinationId();
    }
}
