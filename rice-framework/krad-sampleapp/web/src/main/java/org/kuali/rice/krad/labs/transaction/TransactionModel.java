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
package org.kuali.rice.krad.labs.transaction;

import org.apache.commons.lang.RandomStringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dummy test object for testing
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TransactionModel implements Serializable {

    private static final long serialVersionUID = 5227859879033967666L;
    private String id;
    private String accountId;
    private String statusString = RandomStringUtils.randomAlphanumeric(4);

    private String idString = RandomStringUtils.randomAlphanumeric(4);

    private TransactionModel parentTransaction;
    private Date creationDate = new Date();
    private Date effectiveDate = new Date();
    private Date originationDate = new Date();
    private Date recognitionDate = new Date();

    private BigDecimal amount = new BigDecimal(Math.random() * 100, new MathContext(2));
    private String currencyCode = RandomStringUtils.randomAlphanumeric(4);
    private BigDecimal nativeAmount = new BigDecimal(Math.random() * 100, new MathContext(2));
    private String glEntryGenerated = RandomStringUtils.randomAlphanumeric(4);
    private String internal = RandomStringUtils.randomAlphanumeric(4);
    private BigDecimal allocatedAmount = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal lockedAllocatedAmount = new BigDecimal(Math.random() * 100, new MathContext(2));

    private String rollupDescription = RandomStringUtils.randomAlphanumeric(4);
    private String generalLedgerTypeDescription = RandomStringUtils.randomAlphanumeric(4);
    private String glOverridden = RandomStringUtils.randomAlphanumeric(4);

    private Date clearDate = new Date();
    private String paymentRefundable = RandomStringUtils.randomAlphanumeric(4);
    private String paymentRefundRule = RandomStringUtils.randomAlphanumeric(4);

    private BigDecimal originalAmount = new BigDecimal(Math.random() * 100, new MathContext(2));
    private Date defermentExpirationDate = new Date();

    private String chargeCancellationRule = RandomStringUtils.randomAlphanumeric(4);

    private BigDecimal chargeAmount = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal paymentAmount = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal defermentAmount = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal allocatedLockedAllocated = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal unallocatedAmount = new BigDecimal(Math.random() * 100, new MathContext(2));

    private BigDecimal chargeTotal = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal paymentTotal = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal defermentTotal = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal allocatedTotal = new BigDecimal(Math.random() * 100, new MathContext(2));
    private BigDecimal unallocatedTotal = new BigDecimal(Math.random() * 100, new MathContext(2));
    private String transactionDisplayType;
    private String transactionTypeDescription = "Description Description Description";

    private String tagList = "TAG LIST";
    private String newTag = "NEW TAG";

    private List<TransactionModel> subTransactions;
    private List<Tag> tagModels = new ArrayList<Tag>();

    public TransactionModel() {
        this.id = RandomStringUtils.randomAlphanumeric(4);
        this.accountId = RandomStringUtils.randomAlphanumeric(4);
        this.transactionDisplayType = "type" + RandomStringUtils.randomNumeric(2);
    }

    public TransactionModel(int subTransactionsItemNumber) {
        this();
        subTransactions = new ArrayList<TransactionModel>();
        parentTransaction = new TransactionModel();
        for (int i = 0; i < subTransactionsItemNumber; i++) {
            this.subTransactions.add(new TransactionModel(0));
        }

        for (int i = 0; i < subTransactionsItemNumber; i++) {
            this.tagModels.add(new Tag());
        }
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getChargeCancellationRule() {
        return chargeCancellationRule;
    }

    public void setChargeCancellationRule(String chargeCancellationRule) {
        this.chargeCancellationRule = chargeCancellationRule;
    }

    public Date getClearDate() {
        return clearDate;
    }

    public void setClearDate(Date clearDate) {
        this.clearDate = clearDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Date getDefermentExpirationDate() {
        return defermentExpirationDate;
    }

    public void setDefermentExpirationDate(Date defermentExpirationDate) {
        this.defermentExpirationDate = defermentExpirationDate;
    }

    public String getGeneralLedgerTypeDescription() {
        return generalLedgerTypeDescription;
    }

    public void setGeneralLedgerTypeDescription(String generalLedgerTypeDescription) {
        this.generalLedgerTypeDescription = generalLedgerTypeDescription;
    }

    public String getGlEntryGenerated() {
        return glEntryGenerated;
    }

    public void setGlEntryGenerated(String glEntryGenerated) {
        this.glEntryGenerated = glEntryGenerated;
    }

    public String getGlOverridden() {
        return glOverridden;
    }

    public void setGlOverridden(String glOverridden) {
        this.glOverridden = glOverridden;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getInternal() {
        return internal;
    }

    public void setInternal(String internal) {
        this.internal = internal;
    }

    public BigDecimal getLockedAllocatedAmount() {
        return lockedAllocatedAmount;
    }

    public void setLockedAllocatedAmount(BigDecimal lockedAllocatedAmount) {
        this.lockedAllocatedAmount = lockedAllocatedAmount;
    }

    public BigDecimal getNativeAmount() {
        return nativeAmount;
    }

    public void setNativeAmount(BigDecimal nativeAmount) {
        this.nativeAmount = nativeAmount;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public Date getOriginationDate() {
        return originationDate;
    }

    public void setOriginationDate(Date originationDate) {
        this.originationDate = originationDate;
    }

    public TransactionModel getParentTransaction() {
        return parentTransaction;
    }

    public void setParentTransaction(TransactionModel parentTransaction) {
        this.parentTransaction = parentTransaction;
    }

    public String getPaymentRefundable() {
        return paymentRefundable;
    }

    public void setPaymentRefundable(String paymentRefundable) {
        this.paymentRefundable = paymentRefundable;
    }

    public String getPaymentRefundRule() {
        return paymentRefundRule;
    }

    public void setPaymentRefundRule(String paymentRefundRule) {
        this.paymentRefundRule = paymentRefundRule;
    }

    public Date getRecognitionDate() {
        return recognitionDate;
    }

    public void setRecognitionDate(Date recognitionDate) {
        this.recognitionDate = recognitionDate;
    }

    public String getRollupDescription() {
        return rollupDescription;
    }

    public void setRollupDescription(String rollupDescription) {
        this.rollupDescription = rollupDescription;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public BigDecimal getAllocatedTotal() {
        return allocatedTotal;
    }

    public void setAllocatedTotal(BigDecimal allocatedTotal) {
        this.allocatedTotal = allocatedTotal;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getAllocatedLockedAllocated() {
        return allocatedLockedAllocated;
    }

    public void setAllocatedLockedAllocated(BigDecimal allocatedLockedAllocated) {
        this.allocatedLockedAllocated = allocatedLockedAllocated;
    }

    public BigDecimal getDefermentAmount() {
        return defermentAmount;
    }

    public void setDefermentAmount(BigDecimal defermentAmount) {
        this.defermentAmount = defermentAmount;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getUnallocatedAmount() {
        return unallocatedAmount;
    }

    public void setUnallocatedAmount(BigDecimal unallocatedAmount) {
        this.unallocatedAmount = unallocatedAmount;
    }

    public BigDecimal getChargeTotal() {
        return chargeTotal;
    }

    public void setChargeTotal(BigDecimal chargeTotal) {
        this.chargeTotal = chargeTotal;
    }

    public BigDecimal getDefermentTotal() {
        return defermentTotal;
    }

    public void setDefermentTotal(BigDecimal defermentTotal) {
        this.defermentTotal = defermentTotal;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPaymentTotal() {
        return paymentTotal;
    }

    public void setPaymentTotal(BigDecimal paymentTotal) {
        this.paymentTotal = paymentTotal;
    }

    public List<TransactionModel> getSubTransactions() {
        return subTransactions;
    }

    public void setSubTransactions(List<TransactionModel> subTransactions) {
        this.subTransactions = subTransactions;
    }

    public String getTagList() {
        return tagList;
    }

    public void setTagList(String tagList) {
        this.tagList = tagList;
    }

    public String getTransactionDisplayType() {
        return transactionDisplayType;
    }

    public void setTransactionDisplayType(String transactionDisplayType) {
        this.transactionDisplayType = transactionDisplayType;
    }

    public String getTransactionTypeDescription() {
        return transactionTypeDescription;
    }

    public void setTransactionTypeDescription(String transactionTypeDescription) {
        this.transactionTypeDescription = transactionTypeDescription;
    }

    public BigDecimal getUnallocatedTotal() {
        return unallocatedTotal;
    }

    public void setUnallocatedTotal(BigDecimal unallocatedTotal) {
        this.unallocatedTotal = unallocatedTotal;
    }

    public String getNewTag() {
        return newTag;
    }

    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }

    public List<Tag> getTagModels() {
        return tagModels;
    }

    public void setTagModels(List<Tag> tagModels) {
        this.tagModels = tagModels;
    }
}

