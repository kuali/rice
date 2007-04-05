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
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<c:set var="salarySettingAttributes"
	value="${DataDictionary['KualiSalarySettingDocument'].attributes}" />

<kul:tabTop tabTitle="Quick Salary Setting" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_SALARY_SETTING_TAB_ERRORS}">
<div class="tab-container" align=center>
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable" summary="">
			<tbody>
				<tr>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${salarySettingAttributes.chart}"
						useShortLabel="false" /></div>
					</th>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${salarySettingAttributes.account}"
						useShortLabel="false" /></div>
					</th>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${salarySettingAttributes.subAccount}"
						useShortLabel="false" /></div>
					</th>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${salarySettingAttributes.objectCode}"
						useShortLabel="false" /></div>
					</th>					
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${salarySettingAttributes.subFundGroup}"
						useShortLabel="false" /></div>
					</th>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${salarySettingAttributes.org}"
						useShortLabel="false" /></div>
					</th>
				</tr>
				<tr>
					<td class="datacell">
						${KualiForm.document.chart.chartOfAccountsCode}
					</td>
					<td class="datacell">
						${KualiForm.document.account.accountNumber}
					</td>
					<td class="datacell">
						${KualiForm.document.subAccount.subAccountName}
					</td>
					<td class="datacell">
						${KualiForm.document.objectCode.financialObjectCode}
					</td>
					<td class="datacell">
						${KualiForm.document.subFundGroup.subFundGroupCode}
					</td>
					<td class="datacell">
						${KualiForm.document.org.organizationCode}
					</td>
				</tr>

			</tbody>
		</table>
		
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
			<th colspan="10">&nbsp;</th>
			<th colspan="4">Position</th>
			</tr>
			<tr>
				<th>
					Pos.Nbr
				</th>
				<th>
					Name
				</th>
				<th>
					Cls
				</th>
				<th>
					CSF Amt
				</th>
				<th>
					CSF FTE
				</th>
				<th>
					RM
				</th>
				<th>
					Req.Sal
				</th>
				<th>
					Req.FTE
				</th>
				<th>
					Del
				</th>
				<th>
					%Chg
				</th>
				<th>
					SPln
				</th>
				<th>
					Grd
				</th>
				<th>
					WM
				</th>
				<th>
					PM
				</th>
				
			</tr>
			<c:forEach items="${KualiForm.document.pendingBudgetConstructionAppointmentFunding}" var="item" varStatus="status">
				<tr>
					<td>${item.positionNumber}</td>
						<c:choose>
						<c:when test="${item.emplid != 'VACANT'}">
							<td>${item.budgetConstructionIntendedIncumbent.personName}</td>
							<td>${item.budgetConstructionIntendedIncumbent.iuClassificationLevel}</td>
						</c:when>
						<c:otherwise>
							<td>VACANT</td>
							<td>&nbsp;</td>
						</c:otherwise>
						</c:choose>
					<td>
						<c:choose>
						<c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
							${item.bcnCalculatedSalaryFoundationTracker[0].csfAmount}
						</c:when>
						<c:otherwise>&nbsp;</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
						<c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
							${item.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}
						</c:when>
						<c:otherwise>&nbsp;</c:otherwise>
						</c:choose>
					</td>
					<td>${item.appointmentFundingMonth}</td>	
					<td>${item.appointmentRequestedAmount}</td>		
					<td>${item.appointmentRequestedFteQuantity}</td>	
					<td>${item.appointmentFundingDeleteIndicator}</td>	
					<td valign=top nowrap><div align="right"><span>
						<fmt:formatNumber value="${item.percentChange}" type="number" groupingUsed="true" minFractionDigits="2" />&nbsp;
					</span></div></td>
					<td>${item.budgetConstructionPosition.positionSalaryPlanDefault}</td>
					<td>${item.budgetConstructionPosition.positionGradeDefault}</td>
					<td>${item.budgetConstructionPosition.iuNormalWorkMonths}</td>
					<td>${item.budgetConstructionPosition.iuPayMonths}</td>
										
				</tr>
			</c:forEach>
		</table>
			<input type="hidden" name="testAmount" value="${KualiForm.testAmount}"/>
			${KualiForm.testAmount}
			<html:image property="methodToCall.incrementTestAmount" value="clearValues"	src="images/buttonsmall_clear.gif" styleClass="tinybutton"	alt="clear" title="clear" border="0" />
</div>
</kul:tabTop>