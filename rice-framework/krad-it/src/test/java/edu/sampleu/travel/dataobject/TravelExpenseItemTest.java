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

import edu.sampleu.travel.options.ExpenseType;
import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.test.BaselineTestCase;

import java.math.BigDecimal;
import java.text.ParseException;
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
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class TravelExpenseItemTest extends KRADTestCase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");

    private static final String EXPENSE_TYPE = ExpenseType.A.getCode();
    private static final String EXPENSE_DESCRIPTION = ExpenseType.A.getLabel();
    private static final String EXPENSE_DATE = "2010-01-01";
    private static final BigDecimal EXPENSE_AMOUNT = new BigDecimal("1236.49");

    /**
     * Tests basic {@code TravelExpenseItem} persistence by saving it, reloading it, and checking the data.
     *
     * @throws java.text.ParseException when the date fails to parse
     */
    @Test
    public void testTravelExpenseItem() throws ParseException {
        assertTrue(TravelExpenseItem.class.getName() + " is not mapped in JPA",
                KRADServiceLocator.getDataObjectService().supports(TravelExpenseItem.class));

        String id = createTravelExpenseItem();

        TravelExpenseItem travelExpenseItem = KRADServiceLocator.getDataObjectService().find(TravelExpenseItem.class, id);
        assertNotNull("Travel Expense Item ID is null", travelExpenseItem.getTravelExpenseItemId());
        assertEquals("Travel Expense Item type is incorrect", EXPENSE_TYPE, travelExpenseItem.getTravelExpenseTypeCd());
        assertEquals("Travel Expense Item description is incorrect", EXPENSE_DESCRIPTION, travelExpenseItem.getExpenseDesc());
        assertEquals("Travel Expense Item date is incorrect", DATE_FORMAT.parse(EXPENSE_DATE), travelExpenseItem.getExpenseDate());
        assertEquals("Travel Expense Item amount is incorrect", EXPENSE_AMOUNT, travelExpenseItem.getExpenseAmount());
        assertTrue("Travel Expense Item is not reimbursable", travelExpenseItem.isReimbursable());
        assertFalse("Travel Expense Item is taxable", travelExpenseItem.isTaxable());
    }

    private String createTravelExpenseItem() throws ParseException {
        TravelExpenseItem travelExpenseItem = new TravelExpenseItem();
        travelExpenseItem.setTravelExpenseTypeCd(EXPENSE_TYPE);
        travelExpenseItem.setExpenseDesc(EXPENSE_DESCRIPTION);
        travelExpenseItem.setExpenseDate(DATE_FORMAT.parse(EXPENSE_DATE));
        travelExpenseItem.setExpenseAmount(EXPENSE_AMOUNT);
        travelExpenseItem.setReimbursable(true);
        travelExpenseItem.setTaxable(false);

        return KRADServiceLocator.getDataObjectService().save(travelExpenseItem).getTravelExpenseItemId();
    }
}
