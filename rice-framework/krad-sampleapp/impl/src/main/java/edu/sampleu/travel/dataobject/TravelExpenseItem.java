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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.InheritProperties;
import org.kuali.rice.krad.data.provider.annotation.InheritProperty;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.Relationship;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHints;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * This class provides the expense items.
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
    @Label("Id")
    @Description("Unique identifier for item")
    @UifValidCharactersConstraintBeanName("AlphaNumericPatternConstraint")
    private String travelExpenseItemId;

    @Column(name="TRVL_AUTH_DOC_ID", length=40)
    @Label("Travel Authorization Document Id")
    @UifDisplayHints({
            @UifDisplayHint(UifDisplayHintType.NO_LOOKUP_RESULT),
            @UifDisplayHint(UifDisplayHintType.NO_INQUIRY)})
    private String travelAuthorizationDocumentId;

    @Relationship(foreignKeyFields="travelAuthorizationDocumentId")
    @ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.REFRESH})
    @JoinColumn(name = "TRVL_AUTH_DOC_ID", referencedColumnName = "TRVL_AUTH_DOC_ID", insertable = false, updatable = false)
    @InheritProperties({
            @InheritProperty(name="documentNumber",
                    label=@Label("Travel Authorization Document"),
                    displayHints=@UifDisplayHints(@UifDisplayHint(UifDisplayHintType.NO_LOOKUP_CRITERIA)))})
    private TravelAuthorizationDocument travelAuthorizationDocument;

    @Column(name = "TRVL_CO_NM")
    private String travelCompanyName;

    @Column(name = "EXP_TYP_CD", length = 10)
    @Label("Expense Type")
    @Description("Type of expense")
    private String travelExpenseTypeCd;

    @Column(name = "EXP_DESC", length = 10)
    @Label("Expense Description")
    @Description("Description of expense")
    private String expenseDesc;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXP_DT")
    @Label("Expense Date")
    @Description("Date of expense")
    private Date expenseDate;

    @Column(name = "EXP_AMT", length = 10)
    @Label("Expense Amount")
    @Description("Amount of expense")
    private BigDecimal expenseAmount;

    @Column(name = "EXP_REIMB", nullable = false, length = 1)
    @Convert(converter = BooleanYNConverter.class)
    @Label("Reimbursable")
    @Description("Whether expense is reimbursed to traveler")
    private boolean reimbursable;

    @Column(name = "EXP_TXBL", nullable = false, length = 1)
    @Convert(converter = BooleanYNConverter.class)
    @Label("Taxable")
    @Description("Whether expense is taxed")
    private boolean taxable;

    public String getTravelExpenseItemId() {
        return travelExpenseItemId;
    }

    public void setTravelExpenseItemId(String travelExpenseItemId) {
        this.travelExpenseItemId = travelExpenseItemId;
    }

    public String getTravelAuthorizationDocumentId() {
        if (StringUtils.isBlank(travelAuthorizationDocumentId)
                && this.travelAuthorizationDocument != null) {
            return this.travelAuthorizationDocument.getDocumentNumber();
        }

        return travelAuthorizationDocumentId;
    }

    public void setTravelAuthorizationDocumentId(String travelAuthorizationDocumentId) {
        this.travelAuthorizationDocumentId = travelAuthorizationDocumentId;
    }

    public TravelAuthorizationDocument getTravelAuthorizationDocument() {
        return travelAuthorizationDocument;
    }

    public void setTravelAuthorizationDocument(TravelAuthorizationDocument travelAuthorizationDocument) {
        this.travelAuthorizationDocument = travelAuthorizationDocument;
    }

    public String getTravelCompanyName() {
        return travelCompanyName;
    }

    public void setTravelCompanyName(String travelCompanyName) {
        this.travelCompanyName = travelCompanyName;
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
