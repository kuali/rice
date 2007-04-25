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
<%@ taglib prefix="bean" uri="/tlds/struts-bean.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ taglib prefix="logic" uri="/tlds/struts-logic.tld" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/cm" %>
<%@ taglib prefix="dd" tagdir="/WEB-INF/tags/dd" %>
<%@ taglib prefix="fin" tagdir="/WEB-INF/tags/fin" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>

<%@ attribute name="editingMode" required="true" type="java.util.Map"%>

<c:set var="allowAdditionalDeposits" value="${editingMode['allowAdditionalDeposits']}" />

<kul:tab tabTitle="Deposits" defaultOpen="true" tabErrorKey="${Constants.CASH_MANAGEMENT_DEPOSIT_ERRORS}">    
    <div class="tab-container" align=center>
        <c:if test="${allowAdditionalDeposits}">
            <div align=left style="padding-left: 10px">
                <html:image src="images/buttonsmall_addInterimDeposit.gif" style="border: none" property="methodToCall.addInterimDeposit" title="close" alt="close"/>
                <html:image src="images/buttonsmall_addFinalDeposit.gif" style="border: none" property="methodToCall.addFinalDeposit" title="close" alt="close"/>
            </div>
            <br>
        </c:if>

        <logic:iterate indexId="ctr" name="KualiForm" property="document.deposits" id="currentDeposit">
            <c:if test="${ctr == 0}">
                <hr>
            </c:if>

            <cm:deposit editingMode="${editingMode}" depositIndex="${ctr}" deposit="${currentDeposit}" />
                    
            <hr>
        </logic:iterate>
    </div>
</kul:tab>