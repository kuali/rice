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

import edu.sampleu.travel.options.ExpenseType;
import edu.sampleu.travel.options.PostalCountryCode;
import edu.sampleu.travel.options.PostalStateCode;
import edu.sampleu.travel.options.TripType;
import org.junit.Test;
import org.kuali.rice.core.api.util.type.KualiDecimal;
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
 * Tests basic {@code TravelAuthorizationDocument} persistence.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
public class TravelAuthorizationDocumentTest extends KRADTestCase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String DOCUMENT_DESCRIPTION = "Test Travel Authorization Document";
    private static final String TRIP_BEGIN = "2010-01-01";
    private static final String TRIP_END = "2010-02-01";
    private static final String TRIP_DESCRIPTION = "Test Trip";
    private static final String TRAVEL_TYPE_CODE = TripType.OS.getCode();
    private static final KualiDecimal EXPENSE_LIMIT = new KualiDecimal(10000);
    private static final String CELL_PHONE_NUMBER = "555-555-5555";

    private static String TRAVELER_DETAIL_ID;
    private static final String PRINCIPAL_ID = "admin";

    private static String TRAVEL_DESTINATION_ID;
    private static final String DESTINATION_NAME = PostalStateCode.CA.getLabel();
    private static final String COUNTRY_CODE = PostalCountryCode.US.getCode();
    private static final String STATE_CODE = PostalStateCode.CA.getCode();

    private static String MILEAGE_RATE_ID;
    private static final String MILEAGE_RATE_CODE = "DO";
    private static final String MILEAGE_RATE_NAME = "Domestic";
    private static final BigDecimal MILEAGE_RATE = new BigDecimal("1994.88");

    private static final String PER_DIEM_DATE = "2010-01-01";
    private static final BigDecimal BREAKFAST_VALUE = new BigDecimal("15.00");
    private static final BigDecimal LUNCH_VALUE = new BigDecimal("30.00");
    private static final BigDecimal DINNER_VALUE = new BigDecimal("45.00");
    private static final BigDecimal INCIDENTALS_VALUE = new BigDecimal("15.00");
    private static final BigDecimal ESTIMATED_MILEAGE = new BigDecimal("50");

    private static final String EXPENSE_TYPE = ExpenseType.A.getCode();
    private static final String EXPENSE_DESCRIPTION = ExpenseType.A.getLabel();
    private static final String TRAVEL_COMPANY_NAME = "Zorba's Travel";
    private static final String EXPENSE_DATE = "2010-01-01";
    private static final BigDecimal EXPENSE_AMOUNT = new BigDecimal("1236.49");

    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(new UserSession("admin"));

        TravelerDetail newTravelerDetail = new TravelerDetail();
        newTravelerDetail.setPrincipalId(PRINCIPAL_ID);
        TRAVELER_DETAIL_ID = KRADServiceLocator.getDataObjectService().save(
                newTravelerDetail, PersistenceOption.FLUSH).getId();

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
    }

    @Override
    public void tearDown() throws Exception {
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(null);
        super.tearDown();
    }

    /**
     * Tests basic {@code TravelAuthorizationDocument} persistence by saving it, reloading it, and checking the data.
     *
     * @throws java.lang.Exception for any exceptions occurring during creation
     */
    @Test
    public void testTravelExpenseItem() throws Exception {
        assertTrue(TravelExpenseItem.class.getName() + " is not mapped in JPA",
                KRADServiceLocator.getDataObjectService().supports(TravelAuthorizationDocument.class));

        String id = createTravelAuthorizationDocument();

        TravelAuthorizationDocument document = (TravelAuthorizationDocument) KRADServiceLocatorWeb.getDocumentService().getByDocumentHeaderId(id);
        String documentNumber = document.getDocumentNumber();
        assertNotNull("Travel Authorization Document is null", document);
        assertNotNull("Travel Authorization Document ID is null", documentNumber);
        assertEquals("Travel Authorization Document trip begin is incorrect", DATE_FORMAT.parse(TRIP_BEGIN), document.getTripBegin());
        assertEquals("Travel Authorization Document trip end is incorrect", DATE_FORMAT.parse(TRIP_END), document.getTripEnd());
        assertEquals("Travel Authorization Document trip description is incorrect", TRIP_DESCRIPTION, document.getTripDescription());
        assertEquals("Travel Authorization Document trip destination ID is incorrect", TRAVEL_DESTINATION_ID, document.getTripDestinationId());
        assertEquals("Travel Authorization Document traveler detail ID is incorrect", TRAVELER_DETAIL_ID, document.getTravelerDetailId());
        assertEquals("Travel Authorization Document travel type code is incorrect", TRAVEL_TYPE_CODE, document.getTravelTypeCode());
        assertEquals("Travel Authorization Document expense limit is incorrect", EXPENSE_LIMIT, document.getExpenseLimit());
        assertEquals("Travel Authorization Document cell phone number is incorrect", CELL_PHONE_NUMBER, document.getCellPhoneNumber());

        assertTrue("Travel Authorization Document daily expense estimates is empty", !document.getDailyExpenseEstimates().isEmpty());
        TravelPerDiemExpense travelPerDiemExpense = document.getDailyExpenseEstimates().get(0);
        String travelPerDiemExpenseDocumentNumber = travelPerDiemExpense.getTravelAuthorizationDocumentId();
        assertEquals("Travel Per Diem Expense document ID is incorrect", travelPerDiemExpenseDocumentNumber, documentNumber);

        assertTrue("Travel Authorization Document actual expense items is empty", !document.getActualExpenseItems().isEmpty());
        TravelExpenseItem travelExpenseItem = document.getActualExpenseItems().get(0);
        String travelExpenseItemDocumentNumber = travelExpenseItem.getTravelAuthorizationDocumentId();
        assertEquals("Travel Expense Item document ID is incorrect", travelExpenseItemDocumentNumber, documentNumber);
    }

    private String createTravelAuthorizationDocument() throws Exception {
        Document newDocument = KRADServiceLocatorWeb.getDocumentService().getNewDocument(TravelAuthorizationDocument.class);
        newDocument.getDocumentHeader().setDocumentDescription(DOCUMENT_DESCRIPTION);
        TravelAuthorizationDocument newTravelAuthorizationDocument = (TravelAuthorizationDocument) newDocument;
        newTravelAuthorizationDocument.setTripBegin(new java.sql.Date(DATE_FORMAT.parse(TRIP_BEGIN).getTime()));
        newTravelAuthorizationDocument.setTripEnd(new java.sql.Date(DATE_FORMAT.parse(TRIP_END).getTime()));
        newTravelAuthorizationDocument.setTripDescription(TRIP_DESCRIPTION);
        newTravelAuthorizationDocument.setTripDestinationId(TRAVEL_DESTINATION_ID);
        newTravelAuthorizationDocument.setTravelerDetailId(TRAVELER_DETAIL_ID);
        newTravelAuthorizationDocument.setTravelTypeCode(TRAVEL_TYPE_CODE);
        newTravelAuthorizationDocument.setExpenseLimit(EXPENSE_LIMIT);
        newTravelAuthorizationDocument.setCellPhoneNumber(CELL_PHONE_NUMBER);

        TravelPerDiemExpense travelPerDiemExpense = new TravelPerDiemExpense();
        travelPerDiemExpense.setTravelAuthorizationDocumentId(newTravelAuthorizationDocument.getDocumentNumber());
        travelPerDiemExpense.setTravelDestinationId(TRAVEL_DESTINATION_ID);
        travelPerDiemExpense.setPerDiemDate(DATE_FORMAT.parse(PER_DIEM_DATE));
        travelPerDiemExpense.setBreakfastValue(BREAKFAST_VALUE);
        travelPerDiemExpense.setLunchValue(LUNCH_VALUE);
        travelPerDiemExpense.setDinnerValue(DINNER_VALUE);
        travelPerDiemExpense.setIncidentalsValue(INCIDENTALS_VALUE);
        travelPerDiemExpense.setMileageRateId(MILEAGE_RATE_ID);
        travelPerDiemExpense.setEstimatedMileage(ESTIMATED_MILEAGE);
        newTravelAuthorizationDocument.getDailyExpenseEstimates().add(travelPerDiemExpense);

        TravelExpenseItem travelExpenseItem = new TravelExpenseItem();
        travelExpenseItem.setTravelAuthorizationDocumentId(newTravelAuthorizationDocument.getDocumentNumber());
        travelExpenseItem.setTravelExpenseTypeCd(EXPENSE_TYPE);
        travelExpenseItem.setExpenseDesc(EXPENSE_DESCRIPTION);
        travelExpenseItem.setTravelCompanyName(TRAVEL_COMPANY_NAME);
        travelExpenseItem.setExpenseDate(DATE_FORMAT.parse(EXPENSE_DATE));
        travelExpenseItem.setExpenseAmount(EXPENSE_AMOUNT);
        travelExpenseItem.setReimbursable(true);
        travelExpenseItem.setTaxable(false);
        newTravelAuthorizationDocument.getActualExpenseItems().add(travelExpenseItem);

        return KRADServiceLocatorWeb.getDocumentService().saveDocument(newTravelAuthorizationDocument).getDocumentNumber();
    }
}
