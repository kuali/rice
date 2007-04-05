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

<channel:portalChannelTop channelTitle="Chart of Accounts" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Account Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.AcctType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Accounting Period" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.AccountingPeriod&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="AICPA Function" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.AicpaFunction&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Balance Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.codes.BalanceTyp&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Budget Aggregation Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.codes.BudgetAggregationCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Campus" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.Campus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Campus Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.CampusType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Chart" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.Chart&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>	    
        <li><portal:portalLink displayTitle="true" title="Country" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.Country&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Document Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.DocumentType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Federal Function" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.FederalFunction&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>				
        <li><portal:portalLink displayTitle="true" title="Federal Funded Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.codes.FederalFundedCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Financial Reporting Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.ReportingCodes&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>       
        <li><portal:portalLink displayTitle="true" title="Fund Group" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.FundGroup&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Higher Education Function" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.HigherEdFunction&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Home Origination" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.HomeOrigination&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Indirect Cost Recovery Automated Entry" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.IcrAutomatedEntry&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Indirect Cost Recovery Exclusion by Account" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.IndirectCostRecoveryExclusionAccount&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Indirect Cost Recovery Exclusion by Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.IndirectCostRecoveryExclusionType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&suppressActions=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Indirect Cost Recovery Type Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.codes.ICRTypeCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Mandatory Transfer Elimination Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.codes.MandatoryTransferEliminationCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Object Consolidation" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.ObjectCons&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Object Level" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.ObjLevel&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Object Sub-Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.ObjSubTyp&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>					
        <li><portal:portalLink displayTitle="true" title="Object Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.ObjectType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Offset Definition" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.OffsetDefinition&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Organization Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.OrgType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <%--<li><portal:portalLink displayTitle="true" title="Organization Options" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.ar.bo.OrgOptions&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>--%>
        <li><portal:portalLink displayTitle="true" title="Origination Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.OriginationCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Postal Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.PostalZipCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Program Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.Program&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Responsibility Center" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.ResponsibilityCenter&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Restricted Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.RestrictedStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="State" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.State&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Sub-Fund Group" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.SubFundGroup&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Sub-Fund Group Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.SubFundGroupType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="University Budget Office Function" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.UniversityBudgetOfficeFunction&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>				
        <li><portal:portalLink displayTitle="true" title="University Date" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.UniversityDate&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>
    </div>
<channel:portalChannelBottom />
