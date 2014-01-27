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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests basic {@code TravelExpenseItem} persistence.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.ROLLBACK_CLEAR_DB)
public class TravelExpenseItemTest extends KRADTestCase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static String DOCUMENT_NUMBER;
    private static final String DOCUMENT_DESCRIPTION = "Test Travel Authorization Document";
    private static final String CELL_PHONE_NUMBER = "555-555-5555";

    private static String TRAVEL_DESTINATION_ID;
    private static final String TRAVEL_COMPANY_NAME = "Zorba's Travel";
    private static final String DESTINATION_NAME = PostalStateCode.CA.getLabel();
    private static final String COUNTRY_CODE = PostalCountryCode.US.getCode();
    private static final String STATE_CODE = PostalStateCode.CA.getCode();

    private static final String EXPENSE_TYPE = ExpenseType.A.getCode();
    private static final String EXPENSE_DESCRIPTION = ExpenseType.A.getLabel();
    private static final String EXPENSE_DATE = "2010-01-01";
    private static final BigDecimal EXPENSE_AMOUNT = new BigDecimal("1236.49");

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
     * Tests basic {@code TravelExpenseItem} persistence by saving it, reloading it, and checking the data.
     *
     * @throws java.lang.Exception for any exceptions occurring during creation
     */
    @Test
    public void testTravelExpenseItem() throws Exception {
        assertTrue(TravelExpenseItem.class.getName() + " is not mapped in JPA",
                KRADServiceLocator.getDataObjectService().supports(TravelExpenseItem.class));

        String id = createTravelExpenseItem();

        TravelExpenseItem travelExpenseItem = KRADServiceLocator.getDataObjectService().find(TravelExpenseItem.class, id);
        assertNotNull("Travel Expense Item ID is null", travelExpenseItem.getTravelExpenseItemId());
        assertEquals("Travel Expense Item document ID is incorrect", DOCUMENT_NUMBER, travelExpenseItem.getTravelAuthorizationDocumentId());
        assertEquals("Travel Expense Item type is incorrect", EXPENSE_TYPE, travelExpenseItem.getTravelExpenseTypeCd());
        assertEquals("Travel Company name is incorrect", TRAVEL_COMPANY_NAME, travelExpenseItem.getTravelCompanyName());
        assertEquals("Travel Expense Item description is incorrect", EXPENSE_DESCRIPTION, travelExpenseItem.getExpenseDesc());
        assertEquals("Travel Expense Item date is incorrect", DATE_FORMAT.parse(EXPENSE_DATE), travelExpenseItem.getExpenseDate());
        assertEquals("Travel Expense Item amount is incorrect", EXPENSE_AMOUNT, travelExpenseItem.getExpenseAmount());
        assertTrue("Travel Expense Item is not reimbursable", travelExpenseItem.isReimbursable());
        assertFalse("Travel Expense Item is taxable", travelExpenseItem.isTaxable());
    }

    private String createTravelExpenseItem() throws Exception {
        TravelExpenseItem travelExpenseItem = new TravelExpenseItem();
        travelExpenseItem.setTravelAuthorizationDocumentId(DOCUMENT_NUMBER);
        travelExpenseItem.setTravelExpenseTypeCd(EXPENSE_TYPE);
        travelExpenseItem.setExpenseDesc(EXPENSE_DESCRIPTION);
        travelExpenseItem.setTravelCompanyName(TRAVEL_COMPANY_NAME);
        travelExpenseItem.setExpenseDate(DATE_FORMAT.parse(EXPENSE_DATE));
        travelExpenseItem.setExpenseAmount(EXPENSE_AMOUNT);
        travelExpenseItem.setReimbursable(true);
        travelExpenseItem.setTaxable(false);

        return KRADServiceLocator.getDataObjectService().save(travelExpenseItem, PersistenceOption.FLUSH).getTravelExpenseItemId();
    }
}
