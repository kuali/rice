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

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests basic {@code TravelMileageRate} persistence.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
public class TravelMileageRateTest extends KRADTestCase {

    private static final String MILEAGE_RATE_CODE = "IL";
    private static final String MILEAGE_RATE_NAME = "International";
    private static final BigDecimal MILEAGE_RATE = new BigDecimal("50");

    /**
     * Tests basic {@code TravelMileageRate} persistence by saving it, reloading it, and checking the data.
     */
    @Test
    public void testTravelMileageRate() {
        assertTrue(TravelMileageRate.class.getName() + " is not mapped in JPA",
                KRADServiceLocator.getDataObjectService().supports(TravelMileageRate.class));

        String id = createTravelMileageRate();

        TravelMileageRate travelMileageRate = KRADServiceLocator.getDataObjectService().find(TravelMileageRate.class, id);
        assertNotNull("Travel Mileage Rate ID is null", travelMileageRate.getMileageRateId());
        assertEquals("Travel Mileage Rate code is incorrect", MILEAGE_RATE_CODE, travelMileageRate.getMileageRateCd());
        assertEquals("Travel Mileage Rate name is incorrect", MILEAGE_RATE_NAME, travelMileageRate.getMileageRateName());
        assertEquals("Travel Mileage Rate amount is incorrect", MILEAGE_RATE, travelMileageRate.getMileageRate());
        assertTrue("Travel Mileage Rate is not active", travelMileageRate.isActive());
    }

    private String createTravelMileageRate() {
        TravelMileageRate travelMileageRate = new TravelMileageRate();
        travelMileageRate.setMileageRateCd(MILEAGE_RATE_CODE);
        travelMileageRate.setMileageRateName(MILEAGE_RATE_NAME);
        travelMileageRate.setMileageRate(MILEAGE_RATE);
        travelMileageRate.setActive(true);

        return KRADServiceLocator.getDataObjectService().save(travelMileageRate).getMileageRateId();
    }
}
