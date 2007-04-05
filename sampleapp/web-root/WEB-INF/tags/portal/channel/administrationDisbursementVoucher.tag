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

<channel:portalChannelTop channelTitle="Disbursement Voucher" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Documentation Location" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.DisbursementVoucherDocumentationLocation&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Non Resident Alien Tax Percent" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.NonResidentAlienTaxPercent&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&suppressActions=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Ownership Type Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.OwnershipTypeCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Payment Reason Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.PaymentReasonCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Tax Control Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TaxControlCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Tax Income Class Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TaxIncomeClassCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Travel Expense Type Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TravelExpenseTypeCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Travel Mileage Rate" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TravelMileageRate&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Travel Per Diem" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TravelPerDiem&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Wire Charge" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.WireCharge&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>
    </div>
<channel:portalChannelBottom />