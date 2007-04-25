<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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
<%@ include file="tldHeader.jsp"%>

<kul:page showDocumentInfo="false" headerTitle="JstlTest page" docTitle="JstlTest page" transactionalDocument="false" htmlFormAction="jstlTest">
	<div class="topblurb">

        <dd:dumpMap title="DataDictionary.KualiGeneralErrorCorrectionDocument" prefix="DataDictionary.KualiGeneralErrorCorrectionDocument" map="${DataDictionary.KualiGeneralErrorCorrectionDocument}" /> <br>
        <hr>

<%--
        <dd:dumpMap title="Constants" prefix="Constants" map="${Constants}" /> <br>
        <dd:dumpMap title="AuthorizationConstants" prefix="AuthorizationConstants" map="${AuthorizationConstants}" /> <br>
        <dd:dumpMap title="DataDictionary.KualiInternalBillingDocument" map="${DataDictionary.KualiInternalBillingDocument}" />
        <hr>
        <table>
            <tr>
                <th colspan=2>DataDictionary.KualiAccountMaintenanceDocument.maintainableSections</th>
            </tr>
            <dd:foreachEntry map='${DataDictionary.KualiAccountMaintenanceDocument.maintainableSections}' valueVar='section' >
                <tr><td colspan=2>section title: ${section.title}</td></tr>

	            <tr>
	                <th>maintainableItem name</th>
	                <th>values</th>
	            </tr>
                <dd:foreachEntry map='${section.maintainableItems}' valueVar='item' >
                    <tr>
                        <td>
                           <c:out value="${item.name}" />
                       </td>
	                    <c:choose>
	                        <c:when test="${item.field}">
                                <td>
                                   required: <c:out value="${item.required}" />
                                </td>
	                        </c:when>
	                        <c:when test="${item.collection}">
                                <td>
                                   businessObjectClass: <c:out value="${item.businessObjectClass}" />
                                </td>
	                        </c:when>
	                    </c:choose>
	                </tr>
                </dd:foreachEntry>
            </dd:foreachEntry>
        </table>

        <hr>
        <table>
            <tr>
                <th colspan=2>failure</th>
            </tr>
            <tr>
                <th>Key</th>
                <th>Value</th>
            </tr>
            <c:forEach items='${DataDictionary.KualiAccountMaintenanceDocument.sections}' var='section'>
	                <tr>
	                    <td>
	                       <c:out value="${item.key}" />
	                    </td>
	                    <td>
	                       <c:out value="${item.value}" />
	                    </td>
	                </tr>
            </c:forEach>
        </table>
    </div>

        <table>
            <tr>
                <th colspan=2>ConfigProperties</th>
            </tr>
            <tr>
                <th>Key</th>
                <th>Value</th>
            </tr>
            <c:forEach var='item' items='${ConfigProperties}'>
            <tr>
                <td>
                   <c:out value="${item.key}" />
                </td>
                <td>
                   <c:out value="${item.value}" />
                </td>
            </tr>
            </c:forEach>
        </table>
        <br>
        <table>
            <tr>
                <th colspan=2>ConfigProperties.errors</th>
            </tr>
            <tr>
                <th>Key</th>
                <th>Value</th>
            </tr>
            <c:forEach var='item' items='${ConfigProperties.errors}'>
            <tr>
                <td>
                   <c:out value="${item.key}" />
                </td>
                <td>
                   <c:out value="${item.value}" />
                </td>
            </tr>
            </c:forEach>
        </table>
--%>
</div>
</kul:page>
