/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.bo;


import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiPercent;


/**
 * Attribute Reference Dummy Business Object
 */
public class AttributeReferenceDummy extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 6582568341825342401L;
    private String oneDigitTextCode;
    private String twoDigitTextCode;
    private String genericSystemId;
    private Date genericDate;
    private Timestamp genericTimestamp;
    private boolean genericBoolean;
    private boolean activeIndicator;
    private KualiDecimal genericAmount;
    private String genericBigText;
    private String emailAddress;
    private Integer transactionEntrySequenceId;
    private String universityFiscalAccountingPeriod;
    private Integer genericFiscalYear;
    private String documentExplanation;
    private Date createDate;
    private String initiatorNetworkId;
    private KualiPercent percent;
    private String maxDollarAmount;
    private String minDollarAmount;
    private String totalDollarAmount;
    private boolean newCollectionRecord;


    public String getMinDollarAmount() {
        return minDollarAmount;
    }

    public void setMinDollarAmount(String minDollarAmount) {
        this.minDollarAmount = minDollarAmount;
    }

    public String getTotalDollarAmount() {
        return totalDollarAmount;
    }

    public void setTotalDollarAmount(String totalDollarAmount) {
        this.totalDollarAmount = totalDollarAmount;
    }

    public String getMaxDollarAmount() {
        return maxDollarAmount;
    }

    public void setMaxDollarAmount(String maxDollarAmount) {
        this.maxDollarAmount = maxDollarAmount;
    }

    /**
     * 
     * Constructs a AttributeReferenceDummy.java.
     * 
     */
    public AttributeReferenceDummy() {
    }

    /**
     * Gets the percent attribute.
     * 
     * @return Returns the percent.
     */
    public KualiPercent getPercent() {
        return percent;
    }

    /**
     * Sets the percent attribute value.
     * 
     * @param percent The percent to set.
     */
    public void setPercent(KualiPercent percent) {
        this.percent = percent;
    }


    /**
     * Gets the genericSystemId attribute.
     * 
     * @return Returns the genericSystemId.
     */
    public String getGenericSystemId() {
        return genericSystemId;
    }

    /**
     * Sets the genericSystemId attribute value.
     * 
     * @param genericSystemId The genericSystemId to set.
     */
    public void setGenericSystemId(String genericSystemId) {
        this.genericSystemId = genericSystemId;
    }

    /**
     * Gets the oneDigitTextCode attribute.
     * 
     * @return Returns the oneDigitTextCode.
     */
    public String getOneDigitTextCode() {
        return oneDigitTextCode;
    }

    public Timestamp getGenericTimestamp() {
        return genericTimestamp;
    }

    public void setGenericTimestamp(Timestamp genericTimestamp) {
        this.genericTimestamp = genericTimestamp;
    }

    /**
     * Sets the oneDigitTextCode attribute value.
     * 
     * @param oneDigitTextCode The oneDigitTextCode to set.
     */
    public void setOneDigitTextCode(String oneDigitTextCode) {
        this.oneDigitTextCode = oneDigitTextCode;
    }

    /**
     * Gets the twoDigitTextCode attribute.
     * 
     * @return Returns the twoDigitTextCode.
     */
    public String getTwoDigitTextCode() {
        return twoDigitTextCode;
    }

    /**
     * Sets the twoDigitTextCode attribute value.
     * 
     * @param twoDigitTextCode The twoDigitTextCode to set.
     */
    public void setTwoDigitTextCode(String twoDigitTextCode) {
        this.twoDigitTextCode = twoDigitTextCode;
    }

    /**
     * Gets the genericDate attribute.
     * 
     * @return Returns the genericDate.
     */
    public Date getGenericDate() {
        return genericDate;
    }

    /**
     * Sets the genericDate attribute value.
     * 
     * @param genericDate The genericDate to set.
     */
    public void setGenericDate(Date genericDate) {
        this.genericDate = genericDate;
    }

    /**
     * Gets the genericBoolean attribute.
     * 
     * @return Returns the genericBoolean.
     */
    public boolean isGenericBoolean() {
        return genericBoolean;
    }

    /**
     * Sets the genericBoolean attribute value.
     * 
     * @param genericBoolean The genericBoolean to set.
     */
    public void setGenericBoolean(boolean genericBoolean) {
        this.genericBoolean = genericBoolean;
    }

    /**
     * Gets the activeIndicator attribute.
     * 
     * @return Returns the activeIndicator.
     */
    public boolean isActiveIndicator() {
        return activeIndicator;
    }

    /**
     * Sets the activeIndicator attribute value.
     * 
     * @param activeIndicator The activeIndicator to set.
     */
    public void setActiveIndicator(boolean activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    /**
     * Gets the genericAmount attribute.
     * 
     * @return Returns the genericAmount.
     */
    public KualiDecimal getGenericAmount() {
        return genericAmount;
    }

    /**
     * Sets the genericAmount attribute value.
     * 
     * @param genericAmount The genericAmount to set.
     */
    public void setGenericAmount(KualiDecimal genericAmount) {
        this.genericAmount = genericAmount;
    }

    /**
     * Gets the genericBigText attribute.
     * 
     * @return Returns the genericBigText.
     */
    public String getGenericBigText() {
        return genericBigText;
    }

    /**
     * Sets the genericBigText attribute value.
     * 
     * @param genericBigText The genericBigText to set.
     */
    public void setGenericBigText(String genericBigText) {
        this.genericBigText = genericBigText;
    }

    /**
     * Gets the emailAddress attribute.
     * 
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the emailAddress attribute value.
     * 
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return Integer
     */
    public Integer getTransactionEntrySequenceId() {
        return transactionEntrySequenceId;
    }

    /**
     * @param transactionEntrySequenceId
     */
    public void setTransactionEntrySequenceId(Integer transactionEntrySequenceId) {
        this.transactionEntrySequenceId = transactionEntrySequenceId;
    }

    /**
     * @return String
     */
    public String getUniversityFiscalAccountingPeriod() {
        return universityFiscalAccountingPeriod;
    }

    /**
     * @param universityFiscalAccountingPeriod
     */
    public void setUniversityFiscalAccountingPeriod(String universityFiscalAccountingPeriod) {
        this.universityFiscalAccountingPeriod = universityFiscalAccountingPeriod;
    }

    /**
     * @return Integer
     */
    public Integer getGenericFiscalYear() {
        return genericFiscalYear;
    }

    /**
     * @param universityFiscalYear
     */
    public void setGenericFiscalYear(Integer universityFiscalYear) {
        this.genericFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the documentExplanation attribute.
     * 
     * @return Returns the documentExplanation.
     */
    public String getDocumentExplanation() {
        return documentExplanation;
    }

    /**
     * Sets the documentExplanation attribute value.
     * 
     * @param documentExplanation The documentExplanation to set.
     */
    public void setDocumentExplanation(String documentExplanation) {
        this.documentExplanation = documentExplanation;
    }

    /**
     * Gets the createDate attribute.
     * 
     * @return Returns the createDate.
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the createDate attribute value.
     * 
     * @param createDate The createDate to set.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets the initiatorNetworkId attribute.
     * 
     * @return Returns the initiatorNetworkId.
     */
    public String getInitiatorNetworkId() {
        return initiatorNetworkId;
    }

    /**
     * Sets the initiatorNetworkId attribute value.
     * 
     * @param initiatorNetworkId The initiatorNetworkId to set.
     */
    public void setInitiatorNetworkId(String initiatorNetworkId) {
        this.initiatorNetworkId = initiatorNetworkId;
    }
    

    /**
     * Gets the newCollectionRecord attribute. 
     * @return Returns the newCollectionRecord.
     */
    public boolean isNewCollectionRecord() {
        return newCollectionRecord;
    }

    /**
     * Sets the newCollectionRecord attribute value.
     * @param newCollectionRecord The newCollectionRecord to set.
     */
    public void setNewCollectionRecord(boolean newCollectionRecord) {
        this.newCollectionRecord = newCollectionRecord;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("hashCode", Integer.toHexString(hashCode()));

        return m;
    }
}