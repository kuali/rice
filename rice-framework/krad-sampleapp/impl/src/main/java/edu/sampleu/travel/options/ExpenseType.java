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
package edu.sampleu.travel.options;

import org.kuali.rice.core.api.mo.common.Coded;

/**
 * Provides options to identify and categorize expense items
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum ExpenseType implements Coded {
    A("A", "Airfare"),
    L("L", "Lodging"),
    M("M", "Moving Equipment"),
    O("O", "Other"),
    R("R", "Automobile Rental"),
    T("T", "Taxi/Limousine Service"),
    PA("PA", "Prepaid Airfare"),
    PC("PC", "Conference Registration"),
    PL("PL", "Prepaid Lodging"),
    PM("PM", "Prepaid Moving Rental"),
    PO("PO", "Prepaid Auto Rental"),
    PR("PR", "Prepaid Tax/Limo Service"),
    HB("HB", "Hosted Meal – Breakfast"),
    HL("HL", "Hosted Meal – Lunch"),
    HD("HD", "Hosted Meal – Dinner"),
    MH("MH", "House hunting costs"),
    MT("MT", "Temporary living"),
    ML("ML", "Living allowances"),
    MF("MF", "Final move meals"),
    MM("MM", "Mileage allowed per mile threshold"),
    MD("MD", "Domestic storage over 30 days"),
    MI("MI", "International storage"),
    ME("ME", "Family Travel Expense"),
    MO("MO", "Misc. Expense"),
    EL("EL", "Light refreshments");

    private final String code;
    private final String label;

    ExpenseType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

}