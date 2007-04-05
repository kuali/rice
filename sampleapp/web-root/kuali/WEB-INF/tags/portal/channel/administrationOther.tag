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

<channel:portalChannelTop channelTitle="Other" />
<div class="body">

    <strong>Other</strong>
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Budget Construction Account Reports" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionAccountReports&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>				
		<li><portal:portalLink displayTitle="true" title="Budget Construction Organization Reports" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionOrganizationReports&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>			
		<li><portal:portalLink displayTitle="true" title="Budget Construction Duration" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionDuration&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>			
		<li><portal:portalLink displayTitle="true" title="Budget Construction Position" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionPosition&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>			
		<li><portal:portalLink displayTitle="true" title="Budget Construction Intended Incumbent" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbent&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>			
		<li><portal:portalLink displayTitle="true" title="Calculated Salary Foundation Tracker Override" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.CalculatedSalaryFoundationTrackerOverride&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>			
		<li><portal:portalLink displayTitle="true" title="Budget Construction Appointment Funding Reason Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.budget.bo.BudgetConstructionAppointmentFundingReasonCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>			
     	<li><portal:portalLink displayTitle="true" title="BC Salary Setting (temp for dev)" url="budgetSalarySetting.do?methodToCall=docHandler&command=initiate&docTypeName=KualiSalarySettingDocument" /></li>
     	<li><portal:portalLink displayTitle="true" title="BC Document (temp for dev)" url="budgetBudgetConstruction.do?methodToCall=docHandler&command=initiate&docTypeName=KualiBudgetConstructionDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Bank" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.Bank&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Bank Account" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.BankAccount&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Building" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.Building&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Credit Card Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.CreditCardType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Credit Card Vendor" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.CreditCardVendor&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Employee Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.EmployeeStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Employee Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.core.bo.EmployeeType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Fiscal Year Function Control" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.FiscalYearFunctionControl&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>				
		<li><portal:portalLink displayTitle="true" title="Function Control Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.FunctionControlCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>				
		<li><portal:portalLink displayTitle="true" title="Message Of The Day" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.MessageOfTheDay&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>					
		<li><portal:portalLink displayTitle="true" title="Offset Account" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.OffsetAccount&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Organization Reversion" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.OrganizationReversion&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Organization Reversion Category" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.OrganizationReversionCategory&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Organization Reversion Detail" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.chart.bo.OrganizationReversionDetail&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Procurement Card Default" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.ProcurementCardDefault&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Service Billing Control" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.ServiceBillingControl&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="System Options" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.bo.Options&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>
    
    
    
    <strong>Research Administration</strong>
    <ul class="chan">
    	<li><portal:portalLink displayTitle="true" title="Appointment Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.AppointmentType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Control Attribute Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.ControlAttributeType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Graduate Assistant" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.GraduateAssistantRate&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    	<li><portal:portalLink displayTitle="true" title="Budget Base Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.BudgetBaseCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Due Date Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.DueDateType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Indirect Cost Lookup" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.IndirectCostLookup&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Keyword" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.Keyword&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Nonpersonnel Category" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.NonpersonnelCategory&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Nonpersonnel Object Code" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.NonpersonnelObjectCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Nonpersonnel Sub-Category" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.budget.bo.NonpersonnelSubCategory&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    	<li><portal:portalLink displayTitle="true" title="Research Risk Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.ResearchRiskType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Person Role" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.PersonRole&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Submission Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.SubmissionType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Project Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.ProjectType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Purpose" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.Purpose&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Research Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.ResearchTypeCode&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    	<li><portal:portalLink displayTitle="true" title="Question Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.kra.routingform.bo.QuestionType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>
    
    <strong>Contracts & Grants</strong>
    <ul class="chan">
 		<li><portal:portalLink displayTitle="true" title="Agency" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.Agency&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Agency Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.AgencyType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Award Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.AwardStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>				
		<li><portal:portalLink displayTitle="true" title="CFDA Reference" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.CatalogOfFederalDomesticAssistanceReference&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Grant Description" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.GrantDescription&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Letter of Credit Fund Group" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.LetterOfCreditFundGroup&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>					
 		<li><portal:portalLink displayTitle="true" title="Project Director" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.ProjectDirector&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Proposal/Award Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.ProposalAwardType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Proposal Purpose" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.ProposalPurpose&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Proposal Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.ProposalStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Subcontractor" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.cg.bo.Subcontractor&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>

    </div>
<channel:portalChannelBottom />
                