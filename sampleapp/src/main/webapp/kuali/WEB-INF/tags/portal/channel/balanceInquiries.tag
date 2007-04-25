<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<channel:portalChannelTop channelTitle="Balance Inquiries" />
<div class="body">
    <strong>General Ledger</strong>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Available Balances" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.AccountBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Balances by Consolidation" url="${Constants.GL_ACCOUNT_BALANCE_BY_CONSOLIDATION_LOOKUP_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.AccountBalanceByConsolidation&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	   	<li><portal:portalLink displayTitle="true" title="Cash Balances" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.CashBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="General Ledger Balance" url="${Constants.GL_BALANCE_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.Balance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Entry" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.Entry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="General Ledger Pending Entry" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.GeneralLedgerPendingEntry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	   	<li><portal:portalLink displayTitle="true" title="Open Encumbrances" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.Encumbrance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>
    <strong>Labor Distribution</strong>
    <ul class="chan">
	<li><portal:portalLink displayTitle="true" title="Base Funds Account Status" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.AccountStatusBaseFunds&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>    
	<li><portal:portalLink displayTitle="true" title="CSF Tracker View" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	<li><portal:portalLink displayTitle="true" title="Current Year Funds Account Status" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.AccountStatusCurrentFunds&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
    <li> Effort Certification Document Print</li>
    <li> July 1 Funding</li>
	<li><portal:portalLink displayTitle="true" title="Labor Ledger Balance" url="${Constants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.LedgerBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>  
    <li> Labor Inquiry </li>
    <li> Labor Ledger Effort Certification View</li>
    <li> Labor Ledger View</li>
	<li><portal:portalLink displayTitle="true" title="Labor Ledger View" url="${Constants.GL_BALANCE_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.LedgerBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&financialBalanceTypeCode=AC" /></li>  
	<li><portal:portalLink displayTitle="true" title="Labor Ledger A21 View" url="${Constants.GL_BALANCE_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.LedgerA21Balance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&financialBalanceTypeCode=A2" /></li> 
    <li> Labor Listing by Organization</li>
    <li> Organization Report Number Outstanding Effort Certification Forms</li>
    <li> Person Funding</li>
    <li> Report Number Outstanding Effort Certification Forms</li>
    </ul>
    </div>
<channel:portalChannelBottom />