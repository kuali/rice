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
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib tagdir="/WEB-INF/tags/bc" prefix="bc"%>

<c:set var="salarySettingAttributes"
	value="${DataDictionary['PendingBudgetConstructionAppointmentFunding'].attributes}" />
<c:set var="pbglAttributes"
	value="${DataDictionary['PendingBudgetConstructionGeneralLedger'].attributes}" />
<c:set var="readOnly" value="${KualiForm.editingMode['systemViewOnly'] || !KualiForm.editingMode['fullEntry']}" />

<kul:tabTop tabTitle="Quick Salary Setting" defaultOpen="true" tabErrorKey="${Constants.BUDGET_CONSTRUCTION_SALARY_SETTING_TAB_ERRORS}">
<div class="tab-container" align=center>
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable" summary="">
			<tbody>
				<tr>
                    <kul:htmlAttributeHeaderCell align="right" attributeEntry="${pbglAttributes.chartOfAccountsCode}" >
                        <html:hidden property="returnAnchor" />
                        <html:hidden property="returnFormKey" />
                    </kul:htmlAttributeHeaderCell>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${pbglAttributes.accountNumber}"
						useShortLabel="false" /></div>
					</th>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${pbglAttributes.subAccountNumber}"
						useShortLabel="false" /></div>
					</th>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${pbglAttributes.financialObjectCode}"
						useShortLabel="false" /></div>
					</th>
					<th>
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${pbglAttributes.financialSubObjectCode}"
						useShortLabel="false" /></div>
					</th>
<%-- TODO these literal labels should be changed to dd refs  --%>
                    <kul:htmlAttributeHeaderCell align="right" literalLabel="SubFundGrp" scope="col" />
                    <kul:htmlAttributeHeaderCell align="right" literalLabel="Organization" scope="col" />
				</tr>
				<tr>
					<td class="datacell">
                        <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
<%-- TODO fields on this row should probably be changed use bc:pbglLineDataCell.tag  --%>
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.documentNumber" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.universityFiscalYear" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.financialBalanceTypeCode"/>
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.financialObjectTypeCode"/>
						${KualiForm.pendingBudgetConstructionGeneralLedger.chartOfAccountsCode}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.chartOfAccountsCode" />
					</td>
					<td class="datacell">
						${KualiForm.pendingBudgetConstructionGeneralLedger.accountNumber}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.accountNumber" />
					</td>
					<td class="datacell">
                        ${KualiForm.pendingBudgetConstructionGeneralLedger.subAccountNumber}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.subAccountNumber" />
					</td>
					<td class="datacell">
						${KualiForm.pendingBudgetConstructionGeneralLedger.financialObjectCode}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.financialObjectCode" />
					</td>
					<td class="datacell">
                        ${KualiForm.pendingBudgetConstructionGeneralLedger.financialSubObjectCode}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.financialSubObjectCode" />
					</td>
					<td class="datacell">
<%-- TODO ref needs added  --%>
<%--
						${KualiForm.pendingBudgetConstructionGeneralLedger.subFundGroup.subFundGroupCode}
--%>
					</td>
					<td class="datacell">
<%-- TODO ref needs added  --%>
<%--
						${KualiForm.pendingBudgetConstructionGeneralLedger.org.organizationCode}
--%>
					</td>
				</tr>

			</tbody>
		</table>
		
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
			<th colspan="10">&nbsp;</th>
			<th colspan="4">Position</th>
			<th>&nbsp;</th>
			</tr>
			<tr>
                <kul:htmlAttributeHeaderCell literalLabel="Pos.Nbr" scope="col">
                </kul:htmlAttributeHeaderCell>
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
				<th>
					Action
				</th>
				
			</tr>
			<c:forEach items="${KualiForm.pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding}" var="item" varStatus="status">
				<tr>
					<td>
                        <a name="salaryexistingLineLineAnchor${status.index}"></a>
					    ${item.positionNumber}
<%-- TODO remove when using bc:pbglLineDataCell --%>
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].positionNumber" />

                        <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
<%-- TODO need to work out what to do to refresh on return from position or incumbent salary setting since the set of rows can change --%>
<%-- use of hiddens only make sense here for saving updated data, refresh() will need to reload from the database --%>
<%-- so eventually only need hiddens for bcaf record itself --%>
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].universityFiscalYear" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].chartOfAccountsCode" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].accountNumber" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].subAccountNumber" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].financialObjectCode" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].financialSubObjectCode" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].versionNumber" />
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].emplid" />
					</td>
						<c:choose>
						<c:when test="${item.emplid != 'VACANT'}">
							<td>${item.budgetConstructionIntendedIncumbent.personName}</td>
							<td>${item.budgetConstructionIntendedIncumbent.iuClassificationLevel}</td>
                            <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionIntendedIncumbent.personName" />
                            <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionIntendedIncumbent.iuClassificationLevel" />
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
                            <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfAmount" />
						</c:when>
						<c:otherwise>&nbsp;</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
						<c:when test="${!empty item.bcnCalculatedSalaryFoundationTracker}">
							${item.bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity}
                            <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].bcnCalculatedSalaryFoundationTracker[0].csfFullTimeEmploymentQuantity" />
						</c:when>
						<c:otherwise>&nbsp;</c:otherwise>
						</c:choose>
					</td>
					<td>
					    ${item.appointmentFundingMonth}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentFundingMonth" />
					</td>
					<td>
					    ${item.appointmentRequestedAmount}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedAmount" />
					</td>
					<td>
					    ${item.appointmentRequestedFteQuantity}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentRequestedFteQuantity" />
					</td>
					<td>
					    ${item.appointmentFundingDeleteIndicator}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].appointmentFundingDeleteIndicator" />
					</td>
					<td valign=top nowrap><div align="right"><span>
						<fmt:formatNumber value="${item.percentChange}" type="number" groupingUsed="true" minFractionDigits="2" />&nbsp;
					</span></div></td>
					<td>
					    ${item.budgetConstructionPosition.positionSalaryPlanDefault}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionPosition.positionSalaryPlanDefault" />
					</td>
					<td>
					    ${item.budgetConstructionPosition.positionGradeDefault}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionPosition.positionGradeDefault" />
					</td>
					<td>
					    ${item.budgetConstructionPosition.iuNormalWorkMonths}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionPosition.iuNormalWorkMonths" />
					</td>
					<td>
					    ${item.budgetConstructionPosition.iuPayMonths}
                        <html:hidden property="pendingBudgetConstructionGeneralLedger.pendingBudgetConstructionAppointmentFunding[${status.index}].budgetConstructionPosition.iuPayMonths" />
					</td>

                    <td class="datacell" nowrap>
                        <div align="center">
                          <html:image property="methodToCall.performPositionSalarySetting.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="images/tinybutton-salarysetting.gif" title="Position Salary Setting For Line ${status.index}" alt="Position Salary Setting For Line ${status.index}" styleClass="tinybutton" />
                          <c:if test="${item.emplid != 'VACANT'}">
                            <br>
                            <html:image property="methodToCall.performIncumbentSalarySetting.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="images/tinybutton-salarysetting.gif" title="Incumbent Salary Setting For Line ${status.index}" alt="Incumbent Salary Setting For Line ${status.index}" styleClass="tinybutton" />
                            <c:if test="${!readOnly}">
                                <br>
                                <html:image property="methodToCall.performVacateSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="images/tinybutton-clear1.gif" title="Vacate Salary Setting Line ${status.index}" alt="Vacate Salary Setting Line ${status.index}" styleClass="tinybutton" />
                            </c:if>
                          </c:if>
                          <c:if test="${!empty item.bcnCalculatedSalaryFoundationTracker && !readOnly}">
                            <br>
                            <html:image property="methodToCall.performPercentAdjustmentSalarySettingLine.line${status.index}.anchorsalaryexistingLineLineAnchor${status.index}" src="images/tinybutton-percentincdec.gif" title="Percent Adjustment For Line ${status.index}" alt="Percent Adjustment For Line ${status.index}" styleClass="tinybutton" />
                          </c:if>
                        </div>
                    </td>
										
				</tr>
			</c:forEach>
		</table>
</div>
</kul:tabTop>