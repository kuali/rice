/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests basic {@code TravelPerDiemExpense} persistence.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class TravelPerDiemExpenseTest extends KRADTestCase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");

    private static final String TYPE_CODE = TripType.OS.getCode();
    private static final String DESTINATION_NAME = PostalStateCode.CA.getLabel();
    private static final String COUNTRY_CODE = PostalCountryCode.US.getCode();
    private static final String STATE_CODE = PostalStateCode.CA.getCode();

    private static final String MILEAGE_RATE_CODE = "DO";
    private static final String MILEAGE_RATE_NAME = "Domestic";
    private static final BigDecimal MILEAGE_RATE = new BigDecimal("30");

    private static final String PER_DIEM_DATE = "2010-01-01";
    private static final BigDecimal BREAKFAST_VALUE = new BigDecimal("15.00");
    private static final BigDecimal LUNCH_VALUE = new BigDecimal("30.00");
    private static final BigDecimal DINNER_VALUE = new BigDecimal("45.00");
    private static final BigDecimal INCIDENTALS_VALUE = new BigDecimal("15.00");
    private static final BigDecimal ESTIMATED_MILEAGE = new BigDecimal("50");

    /**
     * Tests basic {@code TravelPerDiemExpense} persistence by saving it, reloading it, and checking the data.
     *
     * @throws java.text.ParseException when the date fails to parse
     */
    @Test
    public void testTravelPerDiemExpense() throws ParseException {
        assertTrue(TravelPerDiemExpense.class.getName() + " is not mapped in JPA",
                KRADServiceLocator.getDataObjectService().supports(TravelPerDiemExpense.class));

        String id = createTravelPerDiemExpense();

        TravelPerDiemExpense travelPerDiemExpense = KRADServiceLocator.getDataObjectService().find(TravelPerDiemExpense.class, id);
        assertNotNull("Travel Per Diem Expense ID is null", travelPerDiemExpense.getTravelPerDiemExpenseId());

        TravelDestination travelDestination = travelPerDiemExpense.getTravelDestination();
        assertNotNull("Travel Per Diem Expense destination is null", travelDestination);
        assertNotNull("Travel Destination ID is null", travelDestination.getTravelDestinationId());
        assertEquals("Travel Destination type is incorrect", TYPE_CODE, travelDestination.getTravelTypeCode());
        assertEquals("Travel Destination name is incorrect", DESTINATION_NAME, travelDestination.getTravelDestinationName());
        assertEquals("Travel Destination country is incorrect", COUNTRY_CODE, travelDestination.getCountryCd());
        assertEquals("Travel Destination state is incorrect", STATE_CODE, travelDestination.getStateCd());

        assertEquals("Travel Per Diem Expense date is incorrect", DATE_FORMAT.parse(PER_DIEM_DATE), travelPerDiemExpense.getPerDiemDate());
        assertEquals("Travel Per Diem Expense breakfast value is incorrect", BREAKFAST_VALUE, travelPerDiemExpense.getBreakfastValue());
        assertEquals("Travel Per Diem Expense lunch value is incorrect", LUNCH_VALUE, travelPerDiemExpense.getLunchValue());
        assertEquals("Travel Per Diem Expense dinner value is incorrect", DINNER_VALUE, travelPerDiemExpense.getDinnerValue());
        assertEquals("Travel Per Diem Expense incidentals value is incorrect", INCIDENTALS_VALUE, travelPerDiemExpense.getIncidentalsValue());

        TravelMileageRate travelMileageRate = travelPerDiemExpense.getMileageRate();
        assertNotNull("Travel Per Diem Expense mileage rate is null", travelMileageRate);
        assertNotNull("Travel Mileage Rate ID is null", travelMileageRate.getMileageRateId());
        assertEquals("Travel Mileage Rate code is incorrect", MILEAGE_RATE_CODE, travelMileageRate.getMileageRateCd());
        assertEquals("Travel Mileage Rate name is incorrect", MILEAGE_RATE_NAME, travelMileageRate.getMileageRateName());
        assertEquals("Travel Mileage Rate amount is incorrect", MILEAGE_RATE, travelMileageRate.getMileageRate());

        assertEquals("Travel Per Diem Expense estimated mileage is incorrect", ESTIMATED_MILEAGE, travelPerDiemExpense.getEstimatedMileage());
    }

    private String createTravelPerDiemExpense() throws ParseException {
        TravelDestination newTravelDestination = new TravelDestination();
        newTravelDestination.setTravelTypeCode(TYPE_CODE);
        newTravelDestination.setTravelDestinationName(DESTINATION_NAME);
        newTravelDestination.setCountryCd(COUNTRY_CODE);
        newTravelDestination.setStateCd(STATE_CODE);
        TravelDestination travelDestination = KRADServiceLocator.getDataObjectService().save(newTravelDestination);

        TravelMileageRate newTravelMileageRate = new TravelMileageRate();
        newTravelMileageRate.setMileageRateCd(MILEAGE_RATE_CODE);
        newTravelMileageRate.setMileageRateName(MILEAGE_RATE_NAME);
        newTravelMileageRate.setMileageRate(MILEAGE_RATE);
        TravelMileageRate travelMileageRate = KRADServiceLocator.getDataObjectService().save(newTravelMileageRate);

        TravelPerDiemExpense travelPerDiemExpense = new TravelPerDiemExpense();
        travelPerDiemExpense.setTravelDestinationId(travelDestination.getTravelDestinationId());
        travelPerDiemExpense.setPerDiemDate(DATE_FORMAT.parse(PER_DIEM_DATE));
        travelPerDiemExpense.setBreakfastValue(BREAKFAST_VALUE);
        travelPerDiemExpense.setLunchValue(LUNCH_VALUE);
        travelPerDiemExpense.setDinnerValue(DINNER_VALUE);
        travelPerDiemExpense.setIncidentalsValue(INCIDENTALS_VALUE);
        travelPerDiemExpense.setMileageRateId(travelMileageRate.getMileageRateId());
        travelPerDiemExpense.setEstimatedMileage(ESTIMATED_MILEAGE);

        return KRADServiceLocator.getDataObjectService().save(travelPerDiemExpense).getTravelPerDiemExpenseId();
    }
}