<%--
 Copyright 2005-2007 The Kuali Foundation
 
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

<%@ attribute name="transactionalDocument" required="true" description="Boolean value of whether this is a transactional document the buttons are being displayed on or not." %>
<%@ attribute name="saveButtonOverride" required="false" description="Overrides the methodToCall for the save button." %>
<%@ attribute name="suppressRoutingControls" required="false" description="Boolean value of whether any buttons which result in routing - Submit, Approve, etc - should be displayed." %>
<%@ attribute name="suppressCancelButton" required="false" description="Boolean value of whether the cancel button should be displayed." %>
<%@ attribute name="extraButtonSource" required="false" description="The image src of a single extra button." %>
<%@ attribute name="extraButtonProperty" required="false" description="The methodToCall property of a single extra button." %>
<%@ attribute name="extraButtonAlt" required="false" description="The alt description of a single extra button." %>
<%@ attribute name="extraButtons" required="false" type="java.util.List" description="A List of org.kuali.rice.kns.web.ui.ExtraButton objects to render before the standard button." %>
<%@ attribute name="viewOnly" required="false" description="Boolean value of whether this document is view only, which means in effect the save button would be suppressed." %>

<c:set var="tabindex" value="0" />

<c:set var="documentTypeName" value="${KualiForm.docTypeName}" />
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />
        <c:set var="saveButtonValue" value="save" />
        <c:if test="${not empty saveButtonOverride}"><c:set var="saveButtonValue" value="${saveButtonOverride}" /></c:if>
		
		<c:if test="${not KualiForm.suppressAllButtons}">
	        <div id="globalbuttons" class="globalbuttons">
	        	<c:if test="${!empty extraButtonSource}">
	        		<html:submit property="${extraButtonProperty}" value="${extraButtonAlt}" alt="${extraButtonAlt}"/>
	        	</c:if>
	        	<c:if test="${!empty extraButtons}">
		        	<c:forEach items="${extraButtons}" var="extraButton">
        				<html:submit property="${extraButton.extraButtonProperty}" value="${extraButton.extraButtonAltText}" title="${extraButton.extraButtonAltText}" alt="${extraButton.extraButtonAltText}"  onclick="${extraButton.extraButtonOnclick}" />
		        	</c:forEach>
	        	</c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_PERFORM_ROUTE_REPORT] and not suppressRoutingControls}">
				    <html:submit property="methodToCall.performRouteReport" value="Perform Route Report" title="Perform Route Report" alt="Perform Route Report" />
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS] and not suppressRoutingControls}">
	                <html:submit property="methodToCall.sendAdHocRequests" value="Send AdHoc Requests" title="Send AdHoc Requests" alt="Send AdHoc Requests"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_ROUTE] and not suppressRoutingControls}">
	                <html:submit property="methodToCall.route" value="submit" title="submit" alt="submit"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_SAVE] and not viewOnly}">
	                <html:submit property="methodToCall.${saveButtonValue}" value="save" title="save" alt="save"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_RELOAD]}">
	                <html:submit property="methodToCall.reload" value="reload" title="reload" alt="reload" onclick="excludeSubmitRestriction=true"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_BLANKET_APPROVE] and not suppressRoutingControls}">
	                <html:submit property="methodToCall.blanketApprove" value="blanket approve" title="blanket approve" alt="blanket approve"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_APPROVE] and not suppressRoutingControls}">
	                <html:submit property="methodToCall.approve" value="approve" title="approve" alt="approve"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_DISAPPROVE] and not suppressRoutingControls}">
	                <html:submit property="methodToCall.disapprove" value="disapprove" title="disapprove" alt="disapprove"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_FYI] and not suppressRoutingControls}">
	                <html:submit property="methodToCall.fyi" value="fyi" title="fyi" alt="fyi"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_ACKNOWLEDGE] and not suppressRoutingControls}">
	                <html:submit property="methodToCall.acknowledge" value="acknowledge" title="acknowledge" alt="acknowledge"/>
	            </c:if>
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_CLOSE]}">
	                <html:submit property="methodToCall.close" value="close" title="close" alt="close"/>
	            </c:if>            
	            <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_CANCEL] and not suppressCancelButton}">
	                <html:submit property="methodToCall.cancel" value="cancel" title="cancel" alt="cancel"/>
	            </c:if>
                <c:if test="${!empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_COPY]}">
                   <html:submit property="methodToCall.copy" value="Copy current document" title="Copy current document" alt="Copy current document"/>
                </c:if>
	        </div>
        </c:if>
        
