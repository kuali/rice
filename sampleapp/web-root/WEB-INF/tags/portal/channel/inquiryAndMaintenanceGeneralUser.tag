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

<channel:portalChannelTop channelTitle="Inquiry and Maintenance" />
<div class="body">
    <strong>Chart of Accounts</strong><br />

    <ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Account" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.Account&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Account Delegate" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.Delegate&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /> / <portal:portalLink displayTitle="true" title="Model" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.OrganizationRoutingModelName&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Object Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.ObjectCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Organization Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.Org&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Project Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.ProjectCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Sub-Account" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.SubAccount&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Sub-Object Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.SubObjCd&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	</ul>
    <strong>Disbursement Voucher</strong><br />
	<ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Payee" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.Payee&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Travel Company" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.TravelCompanyCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	</ul>
    <strong>User and Workgroup</strong><br />
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="User" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.user.UniversalUser&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>				
		<li><portal:portalLink displayTitle="true" title="Workgroup" url="${ConfigProperties.workflow.url}/Lookup.do?lookupableImplServiceName=WorkGroupLookupableImplService" /></li>
	</ul>
    <strong>Contracts & Grants</strong><br />
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Award" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.Award&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Proposal" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.Proposal&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	</ul>
    <strong>Vendor</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Vendor" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.VendorDetail&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>
    </div>
<channel:portalChannelBottom />