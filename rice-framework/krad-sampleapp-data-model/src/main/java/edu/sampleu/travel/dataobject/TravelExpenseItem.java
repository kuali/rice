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

import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * This class provides the expense items
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@Entity
@Table(name = "TRVL_EXP_ITM_T")
@UifAutoCreateViews({UifAutoCreateViewType.INQUIRY, UifAutoCreateViewType.LOOKUP})
public class TravelExpenseItem extends DataObjectBase implements Serializable {

    private static final long serialVersionUID = -4092206384418712220L;

    @Id @Column(name = "EXP_ITM_ID", length = 10)
    @GeneratedValue(generator = "TRVL_EXP_ITM_ID_S")
    @PortableSequenceGenerator(name = "TRVL_EXP_ITM_ID_S")
    @Label("id")
    @Description(
            "Unique identifier for item")
    @UifValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
    private String travelExpenseItemId;

    @Column(name = "EXP_TYP_CD", length = 10)
    @Label("Expense type")
    @Description("type of expense")

    private String travelExpenseTypeCd;

    @Column(name = "EXP_DESC", length = 10)
    @Label("Expense Description")
    @Description("Description of expense")
    private String expenseDesc;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXP_DT")
    @Label("Date of Expense")
    private Date expenseDate;

    @Column(name = "EXP_AMT", length = 10)
    @Label("Expense Amount")
    @Description("amount of expense")
    private BigDecimal expenseAmount;

    @Column(name = "EXP_REIMB", nullable = false, length = 1)
    @javax.persistence.Convert(converter = BooleanYNConverter.class)
    @Label("Reimbursable")
    @Description("Whether expense is reimbursed to traveler")
    private boolean reimbursable;

    @Column(name = "EXP_TXBL", nullable = false, length = 1)
    @javax.persistence.Convert(converter = BooleanYNConverter.class)
    @Label("Taxable")
    @Description("Whether expense is taxed")
    private boolean taxable;

    public String getTravelExpenseItemId() {
        return travelExpenseItemId;
    }

    public void setTravelExpenseItemId(String travelExpenseItemId) {
        this.travelExpenseItemId = travelExpenseItemId;
    }

    public String getTravelExpenseTypeCd() {
        return travelExpenseTypeCd;
    }

    public void setTravelExpenseTypeCd(String travelExpenseTypeCd) {
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
