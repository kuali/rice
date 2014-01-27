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
import org.junit.Test;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.test.BaselineTestCase;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests basic {@code TravelPerDiemExpense} persistence.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
public class TravelPerDiemExpenseTest extends KRADTestCase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static String DOCUMENT_NUMBER;
    private static final String DOCUMENT_DESCRIPTION = "Test Travel Authorization Document";
    private static final String CELL_PHONE_NUMBER = "555-555-5555";

    private static String TRAVEL_DESTINATION_ID;
    private static final String DESTINATION_NAME = PostalStateCode.CA.getLabel();
    private static final String COUNTRY_CODE = PostalCountryCode.US.getCode();
    private static final String STATE_CODE = PostalStateCode.CA.getCode();

    private static String MILEAGE_RATE_ID;
    private static final String MILEAGE_RATE_CODE = "DO";
    private static final String MILEAGE_RATE_NAME = "Domestic";
    private static final BigDecimal MILEAGE_RATE = new BigDecimal("30");

    private static final String PER_DIEM_DATE = "2010-01-01";
    private static final BigDecimal BREAKFAST_VALUE = new BigDecimal("15.00");
    private static final BigDecimal LUNCH_VALUE = new BigDecimal("30.00");
    private static final BigDecimal DINNER_VALUE = new BigDecimal("45.00");
    private static final BigDecimal INCIDENTALS_VALUE = new BigDecimal("15.00");
    private static final BigDecimal ESTIMATED_MILEAGE = new BigDecimal("50");

    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(new UserSession("admin"));

        TravelDestination newTravelDestination = new TravelDestination();
        newTravelDestination.setTravelDestinationName(DESTINATION_NAME);
        newTravelDestination.setCountryCd(COUNTRY_CODE);
        newTravelDestination.setStateCd(STATE_CODE);
        TRAVEL_DESTINATION_ID = KRADServiceLocator.getDataObjectService().save(
                newTravelDestination, PersistenceOption.FLUSH).getTravelDestinationId();

        TravelMileageRate newTravelMileageRate = new TravelMileageRate();
        newTravelMileageRate.setMileageRateCd(MILEAGE_RATE_CODE);
        newTravelMileageRate.setMileageRateName(MILEAGE_RATE_NAME);
        newTravelMileageRate.setMileageRate(MILEAGE_RATE);
        MILEAGE_RATE_ID = KRADServiceLocator.getDataObjectService().save(
                newTravelMileageRate, PersistenceOption.FLUSH).getMileageRateId();

        Document newDocument = KRADServiceLocatorWeb.getDocumentService().getNewDocument(TravelAuthorizationDocument.class);
        newDocument.getDocumentHeader().setDocumentDescription(DOCUMENT_DESCRIPTION);
        TravelAuthorizationDocument newTravelAuthorizationDocument = (TravelAuthorizationDocument) newDocument;
        newTravelAuthorizationDocument.setCellPhoneNumber(CELL_PHONE_NUMBER);
        newTravelAuthorizationDocument.setTripDestinationId(TRAVEL_DESTINATION_ID);
        DOCUMENT_NUMBER = KRADServiceLocatorWeb.getDocumentService().saveDocument(
                newTravelAuthorizationDocument).getDocumentNumber();
    }

    @Override
    public void tearDown() throws Exception {
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(null);
        super.tearDown();
    }

    /**
     * Tests basic {@code TravelPerDiemExpense} persistence by saving it, reloading it, and checking the data.
     *
     * @throws java.lang.Exception for any exceptions occurring during creation
     */
    @Test
    public void testTravelPerDiemExpense() throws Exception {
        assertTrue(TravelPerDiemExpense.class.getName() + " is not mapped in JPA",
                KRADServiceLocator.getDataObjectService().supports(TravelPerDiemExpense.class));

        String id = createTravelPerDiemExpense();

        TravelPerDiemExpense travelPerDiemExpense = KRADServiceLocator.getDataObjectService().find(TravelPerDiemExpense.class, id);

        assertNotNull("Travel Per Diem Expense ID is null", travelPerDiemExpense.getTravelPerDiemExpenseId());
        assertEquals("Travel Per Diem Expense document ID is incorrect", DOCUMENT_NUMBER, travelPerDiemExpense.getTravelAuthorizationDocumentId());
        assertEquals("Travel Per Diem Expense destination ID is incorrect", TRAVEL_DESTINATION_ID, travelPerDiemExpense.getTravelDestinationId());
        assertEquals("Travel Per Diem Expense date is incorrect", DATE_FORMAT.parse(PER_DIEM_DATE), travelPerDiemExpense.getPerDiemDate());
        assertEquals("Travel Per Diem Expense breakfast value is incorrect", BREAKFAST_VALUE, travelPerDiemExpense.getBreakfastValue());
        assertEquals("Travel Per Diem Expense lunch value is incorrect", LUNCH_VALUE, travelPerDiemExpense.getLunchValue());
        assertEquals("Travel Per Diem Expense dinner value is incorrect", DINNER_VALUE, travelPerDiemExpense.getDinnerValue());
        assertEquals("Travel Per Diem Expense incidentals value is incorrect", INCIDENTALS_VALUE, travelPerDiemExpense.getIncidentalsValue());
        assertEquals("Travel Per Diem Expense mileage rate ID is incorrect", MILEAGE_RATE_ID, travelPerDiemExpense.getMileageRateId());
        assertEquals("Travel Per Diem Expense estimated mileage is incorrect", ESTIMATED_MILEAGE, travelPerDiemExpense.getEstimatedMileage());
    }

    private String createTravelPerDiemExpense() throws Exception {
        TravelPerDiemExpense travelPerDiemExpense = new TravelPerDiemExpense();
        travelPerDiemExpense.setTravelAuthorizationDocumentId(DOCUMENT_NUMBER);
        travelPerDiemExpense.setTravelDestinationId(TRAVEL_DESTINATION_ID);
        travelPerDiemExpense.setPerDiemDate(DATE_FORMAT.parse(PER_DIEM_DATE));
        travelPerDiemExpense.setBreakfastValue(BREAKFAST_VALUE);
        travelPerDiemExpense.setLunchValue(LUNCH_VALUE);
        travelPerDiemExpense.setDinnerValue(DINNER_VALUE);
        travelPerDiemExpense.setIncidentalsValue(INCIDENTALS_VALUE);
        travelPerDiemExpense.setMileageRateId(MILEAGE_RATE_ID);
        travelPerDiemExpense.setEstimatedMileage(ESTIMATED_MILEAGE);

        return KRADServiceLocator.getDataObjectService().save(travelPerDiemExpense, PersistenceOption.FLUSH).getTravelPerDiemExpenseId();
    }
}