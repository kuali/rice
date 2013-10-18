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

import edu.sampleu.travel.options.ExpenseTypeKeyValuesFinder.ExpenseType;
import org.kuali.rice.krad.bo.DataObjectBase;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * This class provides the expense items
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class TravelExpenseItem extends DataObjectBase implements Serializable {

    private static final long serialVersionUID = -4092206384418712220L;

    private String travelExpenseItemId;

    private ExpenseType travelExpenseTypeCd;

    private String expenseDesc;

    private Date expenseDate;

    private BigDecimal expenseAmount;

    private boolean reimbursable;

    private boolean taxable;

    public String getTravelExpenseItemId() {
        return travelExpenseItemId;
    }

    public void setTravelExpenseItemId(String travelExpenseItemId) {
        this.travelExpenseItemId = travelExpenseItemId;
    }

    public ExpenseType getTravelExpenseTypeCd() {
        return travelExpenseTypeCd;
    }

    public void setTravelExpenseTypeCd(ExpenseType travelExpenseTypeCd) {
        this.travelExpenseTypeCd = travelExpenseTypeCd;
    }

    public String getExpenseDesc() {
        return expenseDesc;
    }

    public void setExpenseDesc(String expenseDesc) {
        this.expenseDesc = expenseDesc;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public BigDecimal getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(BigDecimal expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public boolean isReimbursable() {
        return reimbursable;
    }

    public void setReimbursable(boolean reimbursable) {
        this.reimbursable = reimbursable;
    }

    public boolean isTaxable() {
        return taxable;
    }

    public void setTaxable(boolean taxable) {
        this.taxable = taxable;
    }
}
