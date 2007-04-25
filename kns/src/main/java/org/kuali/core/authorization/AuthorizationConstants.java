/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.authorization;

import org.kuali.core.JstlConstants;

/**
 * Defines constants used in authorization-related code.
 * 
 * 
 */
public class AuthorizationConstants extends JstlConstants {
    public static class EditMode {
        public static final String UNVIEWABLE = "unviewable";
        public static final String VIEW_ONLY = "viewOnly";
        public static final String FULL_ENTRY = "fullEntry";
    }

    public static class TransactionalEditMode extends EditMode {
        public static final String EXPENSE_ENTRY = "expenseEntry";
        public static final String EXPENSE_SPECIAL_ENTRY = "expenseSpecialEntry";
    }

    public static class DisbursementVoucherEditMode extends TransactionalEditMode {
        public static final String TAX_ENTRY = "taxEntry";
        public static final String FRN_ENTRY = "frnEntry";
        public static final String WIRE_ENTRY = "wireEntry";
        public static final String TRAVEL_ENTRY = "travelEntry";
        public static final String ADMIN_ENTRY = "adminEntry";
    }

    public static class MaintenanceEditMode extends EditMode {
        public static final String APPROVER_EDIT_ENTRY = "approverEditEntry";
        public static final String SSN_EDIT_ENTRY = "ssnEditEntry";
    }

    public static class CashManagementEditMode extends EditMode {
        public static final String ALLOW_ADDITIONAL_DEPOSITS = "allowAdditionalDeposits";
        public static final String ALLOW_CANCEL_DEPOSITS = "allowCancelDeposits";
    }

    public static class BudgetAdjustmentEditMode extends EditMode {
        public static final String BASE_AMT_ENTRY = "baseAmtEntry";
    }

    public static class BudgetConstructionEditMode extends EditMode {
        public static final String SYSTEM_VIEW_ONLY = "systemViewOnly";
        public static final String USER_BELOW_DOC_LEVEL = "userBelowDocLevel";
    }

}
