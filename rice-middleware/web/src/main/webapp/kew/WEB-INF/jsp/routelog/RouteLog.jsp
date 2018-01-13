<%--

    Copyright 2005-2018 The Kuali Foundation

    Licensed under the Educational Community License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.opensource.org/licenses/ecl2.php

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<kul:page headerTitle="Route Log" transactionalDocument="false"
    showDocumentInfo="false" htmlFormAction="RouteLog" docTitle="Route Log" headerMenuBar="${KualiForm.headerMenuBar}">

    <kul:tabTop
        tabTitle="ID: ${routeHeader.documentId}"
        defaultOpen="true"
        >
        <div class="tab-container" align="center">
        <table width="100%" border="0" cellpadding="0" cellspacing="0"
            class="datatable">
            <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left">
                <bean-el:message
                    key="routeLog.RouteLog.header.label.documentTitle" />
                </kul:htmlAttributeHeaderCell>

                <td class="datacell" colspan="3"><c:out
                    value="${routeHeader.docTitle}" />&nbsp;</td>

            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left">
                    <bean-el:message key="routeLog.RouteLog.header.label.documentType" />
                </kul:htmlAttributeHeaderCell>
                <td width="25%" class="datacell">
                      <a href="<c:url value="DocumentConfigurationView.do">
                        <c:param name="methodToCall" value="start" />
                        <c:param name="documentTypeName" value="${routeHeader.documentType.name}"/>
                      </c:url>" target="_blank"><c:out
                    value="${routeHeader.documentType.label}" /></a>
                </td>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.created" /></kul:htmlAttributeHeaderCell>
                <td class="datacell" width="25%"><fmt:formatDate
                    value="${routeHeader.createDate}"
                    pattern="${RiceConstants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.initiator" /></kul:htmlAttributeHeaderCell>
                <td  class="datacell" width="25%">
                    <kul:inquiry boClassName="org.kuali.rice.kim.api.identity.Person" keyValues="principalId=${routeHeader.initiatorWorkflowId}" render="true"><c:out value="${routeHeader.initiatorDisplayName}" /></kul:inquiry>
                    &nbsp;</td>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.lastModified" /></kul:htmlAttributeHeaderCell>
                <td  class="datacell" width="25%"><fmt:formatDate
                    value="${routeHeader.dateModified}"
                    pattern="${RiceConstants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>

            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.routeStatus" /></kul:htmlAttributeHeaderCell>
                <td class="datacell" width="25%"><b>
                  	<c:out value="${routeHeader.routeStatusLabel}" />
                	</b>&nbsp;</td>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.lastApproved" /></kul:htmlAttributeHeaderCell>
                <td  class="datacell" width="25%"><fmt:formatDate
                    value="${routeHeader.approvedDate}"
                    pattern="${RiceConstants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>

            </tr>
          <c:if test="${routeHeader.docStatusPolicy == 'APP' || routeHeader.docStatusPolicy == 'BOTH'}">
            <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.appDocStatus" /></kul:htmlAttributeHeaderCell>
                <td class="datacell" width="25%"><b>
                  	<c:out value="${routeHeader.appDocStatus}" />
                	</b>&nbsp;</td>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.appStatusModified" /></kul:htmlAttributeHeaderCell>
                <td  class="datacell" width="25%"><fmt:formatDate
                    value="${routeHeader.appDocStatusDate}"
                    pattern="${RiceConstants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>
            </tr>
          </c:if>
            <tr>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.routeNodes" /></kul:htmlAttributeHeaderCell>
                <td class="datacell"><c:out
                    value="${routeHeader.currentRouteLevelName}" />&nbsp;</td>
                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
                    key="routeLog.RouteLog.header.label.finalized" /></kul:htmlAttributeHeaderCell>
                <td class="datacell" width="25%"><fmt:formatDate
                    value="${routeHeader.finalizedDate}"
                    pattern="${RiceConstants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>

            </tr>
        </table>
        </div>
    </kul:tabTop>
    <!-- Need to do this in order to pass the value into a tag attribute -->
    <bean:define id="actionsTakenLabel">
        <bean-el:message key="routeLog.RouteLog.actionsTaken.label.actionsTaken"/>
    </bean:define>


    <c:if test="${! empty routeHeader.actionsTaken}">
    <kul:tab
        tabTitle="${actionsTakenLabel}"
        defaultOpen="true">
        <div class="tab-container" align="center">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
                    <!-- might need to remove -->
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.action"/>
                    </kul:htmlAttributeHeaderCell>
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.takenBy"/>
                    </kul:htmlAttributeHeaderCell>
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.forDelegator"/>
                    </kul:htmlAttributeHeaderCell>
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.timeDate"/>
                    </kul:htmlAttributeHeaderCell>
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.annotation"/>
                    </kul:htmlAttributeHeaderCell>
                  </tr>

                    <c:forEach var="actionTaken" items="${routeHeader.actionsTaken}" varStatus="atStatus">
                            <tr >

                                <kul:htmlAttributeHeaderCell scope="col" align="left">
                                    <c:if test="${! empty actionTaken.actionRequests}">
                                    <a id="A<c:out value="${atStatus.count}" />" onclick="rend(this, false)">
                                      <img src="images/tinybutton-show.gif" title="show" alt="show" width="45" height="15" border="0"
                                      align="absmiddle" id="F<c:out value="${atStatus.count}" />"></a>
                                    </c:if>&nbsp;
                                </kul:htmlAttributeHeaderCell>

                                <td align="center" class="datacell">
                                   <b><c:out value="${actionTaken.actionTakenLabel}" /></b>
                                </td>
                                <td align="left" class="datacell">
                                    <kul:inquiry boClassName="org.kuali.rice.kim.api.identity.Person" keyValues="principalId=${actionTaken.principalId}" render="true"><c:out value="${actionTaken.principalDisplayName}" /></kul:inquiry>
                                    &nbsp;
                                </td>
                                <td align="left" class="headercell4">
                                  <c:choose>
                                    <c:when test="${not empty actionTaken.delegatorPrincipalId}">
                                        <kul:inquiry boClassName="org.kuali.rice.kim.api.identity.Person" keyValues="principalId=${actionTaken.delegatorPrincipalId}" render="true"><c:out value="${actionTaken.delegatorDisplayName}" /></kul:inquiry>
                                    </c:when>
                                    <c:when test="${not empty actionTaken.delegatorGroupId}">
                                        <kul:inquiry boClassName="org.kuali.rice.kim.framework.group.GroupEbo" keyValues="id=${actionTaken.delegatorGroupId}" render="true"><c:out value="${actionTaken.delegatorDisplayName}" /></kul:inquiry>
                                    </c:when>
                                    <c:when test="${not empty actionTaken.delegatorRoleId}">
                                        <kul:inquiry boClassName="org.kuali.rice.kim.framework.role.RoleEbo" keyValues="id=${actionTaken.delegatorRoleId}" render="true"><c:out value="${actionTaken.delegatorDisplayName}" /></kul:inquiry>
                                    </c:when>
                                  </c:choose>&nbsp;
                                 </td>
                                 <td align="center" class="headercell4">
                                     <b><fmt:formatDate type="date" value="${actionTaken.actionDate}" pattern="${RiceConstants.DEFAULT_DATE_FORMAT_PATTERN}" /></b>
                                 </td>
                                 <td align="left" class="headercell4">
                                     <c:out value="${actionTaken.annotation}" />&nbsp;
                                     <!--ActionTakenId:<c:out value="${actionTaken.actionTakenId}" />-->
                                 </td>

                            </tr>

                            <tr id="G<c:out value="${atStatus.count}" />" style="display: none;" >
                                <td class="bordercell-left"><img src="images/pixel_clear.gif" alt="" width="8" height="8"></td>
                                <td>&nbsp;</td>
                                <td colspan="4" style="padding: 0; border: 0;">
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding: 0; border: 0;">
                                      <tr>
                                        <th align="center" class="headercell3-b-l" width="5%">&nbsp;</th>
                                        <th align="center" width="15%" class="headercell3-b-l"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.action"/></th>
                                        <th align="center" width="15%" class="headercell3-b-l"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.requestedOf"/></th>
                                        <th align="center" width="22%" class="headercell3-b-l"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.timeDate"/></th>
                                        <th align="center" width="40%" class="headercell3-b-l"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.annotation"/></th>
                                      </tr>
                                    <c:forEach var="actionRequest" items="${actionTaken.actionRequests}" varStatus="arStatus">
                                        <c:if test="${actionRequest.parentActionRequest == null}">
                                            <c:set var="level" value="1" scope="request"/>
                                            <c:set var="index" value="${atStatus.count}z${arStatus.index}" scope="request" />
                                            <c:set var="actionRequest" value="${actionRequest}" scope="request"/>
                                            <c:set var="KualiForm" value="${KualiForm}"/>
                                            <c:set var="hasChildren" value="${! empty actionRequest.childrenRequests}" scope="request"/>
                                            <jsp:include page="ActionRequest.jsp" flush="true" />
                                        </c:if>
                                    </c:forEach>
                                    </table>
                                </td>
                            </tr>
                    </c:forEach>
                </table>
        </div>
        </kul:tab>
        </c:if>

         <c:if test="${KualiForm.pendingActionRequestCount > 0}">
        <kul:tab
        tabTitle="Pending Action Requests"
        defaultOpen="true">
        <div class="tab-container" align="center">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                              <tr>
                                <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
                                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.action"/></kul:htmlAttributeHeaderCell>
                                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.requestedOf"/></kul:htmlAttributeHeaderCell>
                                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.timeDate"/></kul:htmlAttributeHeaderCell>
                                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.annotation"/></kul:htmlAttributeHeaderCell>
                              </tr>

                                    <c:set var="shiftIndex" value="5000" scope="request"/>
                                    <c:forEach var="actionRequest" items="${KualiForm.rootRequests}" varStatus="arStatus">
                                        <c:if test="${actionRequest.pending}">
                                             <c:set var="level" value="0" scope="request"/>
                                             <c:set var="index" value="${arStatus.index + shiftIndex}" scope="request" />
                                             <c:set var="actionRequest" value="${actionRequest}" scope="request"/>
                                             <jsp:include page="ActionRequest.jsp" flush="true" />
                                          </c:if>
                                    </c:forEach>

                            </table>
        </div>
        </kul:tab>
        </c:if>

         <c:if test="${KualiForm.lookFuture}">

         <bean:define id="extraButton">
<a href="
                                <c:url value="RouteLog.do">
                                    <c:param name="showFuture" value="${!KualiForm.showFuture}" />
                                    <c:param name="showNotes" value="${KualiForm.showNotes}" />
                                    <c:param name="documentId" value="${KualiForm.documentId}" />
                                    <c:param name="showBackButton" value="${KualiForm.showBackButton}" />
                                    <c:param name="internalNavCount" value="${KualiForm.nextNavCount}" />
                                </c:url>">
                                    <c:if test="${KualiForm.showFuture}">
                                        <img src="images/tinybutton-hide1.gif" title="hide">
                                    </c:if>
                                    <c:if test="${!KualiForm.showFuture}">
                                        <img src="images/tinybutton-show.gif" title="show">
                                    </c:if>
                                </a>
                                      </bean:define>

        <kul:tab
        tabTitle="Future Action Requests"
        defaultOpen="true"
        midTabClassReplacement="${extraButton}">
        <div class="tab-container" align="center">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">

                    <tr>
                        <c:choose>
                            <c:when test="${KualiForm.showFuture}">
                                <td style="padding: 0; border: 0;">
                                    <c:choose>
                                        <c:when test="${KualiForm.showFutureHasError}">
                                            <div class="exception-error-div"><span class="exception-error"><c:out value="${KualiForm.showFutureError}"/></span></div>
                                        </c:when>
                                        <c:otherwise>
                                            <table width="100%" border="0" cellspacing="0" cellpadding="0" style="padding: 0; border: 0;">
                                              <tr>
                                                <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
                                                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.action"/></kul:htmlAttributeHeaderCell>
                                                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.requestedOf"/></kul:htmlAttributeHeaderCell>
                                                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.timeDate"/></kul:htmlAttributeHeaderCell>
                                                <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.annotation"/></kul:htmlAttributeHeaderCell>
                                              </tr>
                                                <c:set var="shiftIndex" value="6000" scope="request"/>
                                                <c:forEach var="actionRequest" items="${KualiForm.futureRootRequests}" varStatus="arStatus">
                                                    <c:if test="${actionRequest.pending}">
                                                         <c:set var="level" value="0" scope="request"/>
                                                         <c:set var="index" value="${arStatus.index + shiftIndex}" scope="request" />
                                                         <c:set var="actionRequest" value="${actionRequest}" scope="request"/>
                                                         <jsp:include page="ActionRequest.jsp" flush="true" />
                                                    </c:if>
                                                </c:forEach>
                                            </table>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </c:when>
                            <c:otherwise>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </table>
        </div>
        </kul:tab>
        </c:if>
        
        <c:if test="${KualiForm.enableLogAction}">
         <kul:tab
          tabTitle="Log Action Message"
          defaultOpen="true">
           <div class="tab-container" align="center">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                 <tr>
                   <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.newActionMessage.label.actionMessage"/></kul:htmlAttributeHeaderCell>
                   <td class="datacell">
                       <html-el:hidden property="documentId" value="${routeHeader.documentId}" />
                       <html-el:hidden property="showBackButton" value="${KualiForm.showBackButton}" />
                       <html-el:hidden property="internalNavCount" value="${KualiForm.internalNavCount}" />
                       <html-el:text property="newRouteLogActionMessage" size="40" />&nbsp;
                       <html-el:image src="${ConfigProperties.kr.url}/images/buttonsmall_log.gif" property="methodToCall.logActionMessageInRouteLog" styleClass="tinybutton" />
                   </td>
                 </tr>
            </table>
           </div>
          </kul:tab>
        </c:if>
    <kul:panelFooter />

    <div class="globalbuttons">
        <c:if test="${KualiForm.showBackButton}"><a
                href="javascript:history.go(${KualiForm.backCount})" title="back"><img
                src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_back.gif" class="tinybutton" alt="back" title="back"
                border="0" /></a></c:if>
    </div>

</kul:page>


