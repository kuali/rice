<%--
 Copyright 2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>
<%@ taglib tagdir="/WEB-INF/tags/portal/channel" prefix="channel" %>
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean" %>

<channel:portalChannelTop channelTitle="Financial Transactions" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Advance Deposit" url="financialAdvanceDeposit.do?methodToCall=docHandler&command=initiate&docTypeName=KualiAdvanceDepositDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Auxiliary Voucher" url="financialAuxiliaryVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=KualiAuxiliaryVoucherDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Budget Adjustment" url="financialBudgetAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=KualiBudgetAdjustmentDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Cash Management" url="financialCashManagement.do?methodToCall=docHandler&command=initiate&docTypeName=KualiCashManagementDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Cash Receipt" url="financialCashReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=KualiCashReceiptDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Credit Card Receipt" url="financialCreditCardReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=KualiCreditCardReceiptDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher" url="financialDisbursementVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=KualiDisbursementVoucherDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Distribution of Income and Expense" url="financialDistributionOfIncomeAndExpense.do?methodToCall=docHandler&command=initiate&docTypeName=KualiDistributionOfIncomeAndExpenseDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="General Error Correction" url="financialGeneralErrorCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=KualiGeneralErrorCorrectionDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Correction Process" url="generalLedgerCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=KualiGeneralLedgerErrorCorrectionDocument" /></li>									
		<li><portal:portalLink displayTitle="true" title="Indirect Cost Adjustment" url="financialIndirectCostAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=KualiIndirectCostAdjustmentDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Internal Billing" url="financialInternalBilling.do?methodToCall=docHandler&command=initiate&docTypeName=KualiInternalBillingDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Journal Voucher" url="financialJournalVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=KualiJournalVoucherDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Non-Check Disbursement" url="financialNonCheckDisbursement.do?methodToCall=docHandler&command=initiate&docTypeName=KualiNonCheckDisbursementDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Pre-Encumbrance" url="financialPreEncumbrance.do?methodToCall=docHandler&command=initiate&docTypeName=KualiPreEncumbranceDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Service Billing" url="financialServiceBilling.do?methodToCall=docHandler&command=initiate&docTypeName=KualiServiceBillingDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Transfer of Funds" url="financialTransferOfFunds.do?methodToCall=docHandler&command=initiate&docTypeName=KualiTransferOfFundsDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End Budget Adjustment" url="financialYearEndBudgetAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=KualiYearEndBudgetAdjustmentDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End Distribution of Income and Expense" url="financialYearEndDistributionOfIncomeAndExpense.do?methodToCall=docHandler&command=initiate&docTypeName=KualiYearEndDistributionOfIncomeAndExpenseDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End General Error Correction" url="financialYearEndGeneralErrorCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=KualiYearEndGeneralErrorCorrectionDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Year End Transfer of Funds" url="financialYearEndTransferOfFunds.do?methodToCall=docHandler&command=initiate&docTypeName=KualiYearEndTransferOfFundsDocument" /></li>
    </ul>
  </div>
<channel:portalChannelBottom />
