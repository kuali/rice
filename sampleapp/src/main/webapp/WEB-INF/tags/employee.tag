<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>

<%@ attribute name="userIdFieldName" required="true" %>
<%@ attribute name="userNameFieldName" required="true" %>

<%@ attribute name="label" required="false" %>
<%@ attribute name="fieldConversions" required="false" %>
<%@ attribute name="lookupParameters" required="false" %>
<%@ attribute name="referencesToRefresh" required="false" %>

<%@ attribute name="renderOtherFields" required="false" %>

<%@ attribute name="hasErrors" required="false" %>
<%@ attribute name="readOnly" required="false" %>
<%@ attribute name="onblur" required="false" %>

<%@ attribute name="helpLink" required="false" %>

<%@ attribute name="highlight" required="false"
              description="boolean indicating if this field is rendered as highlighted (to indicate old/new value change)" %> 

<script language="JavaScript" type="text/javascript" src="dwr/interface/LaborUserService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/labor/objectInfo.js"></script>

<%-- set the border color when has errors --%>
<c:if test="${hasErrors}">
	<c:set var="textStyle" value="border-color: red" />
</c:if>
<kul:htmlControlAttribute property="${userIdFieldName}" 
                    attributeEntry="${DataDictionary['UniversalUser'].attributes.personPayrollIdentifier}"
                    onblur="loadEmplInfo( '${userIdFieldName}', '${userNameFieldName}' );${onblur}" readOnly="${readOnly}"/>

<kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" 
	        fieldConversions="${fieldConversions}" 
			lookupParameters="${lookupParameters}" 
			fieldLabel="${label}" 
			referencesToRefresh="${referencesToRefresh}"
			anchor="${currentTabIndex}" />
<c:choose>
	<c:when test="${readOnly}">
		<div>${userName}</div>
	</c:when>
	<c:otherwise>
		${helpLink}
		<div id="${userNameFieldName}.div">
            <html:hidden write="true" property="${userNameFieldName}"/>&nbsp;        
        </div>
	</c:otherwise>
</c:choose>
	
<c:if test="${renderOtherFields}">
	<c:if test="${!empty universalIdFieldName}">
		<input type="hidden" name="${universalIdFieldName}" value="${universalId}" />
	</c:if>
	<c:if test="${!empty userNameFieldName}">
		<input type="hidden" name="${userNameFieldName}" value="${userName}" />
	</c:if>
</c:if>

<c:if test="${highlight}">
<kul:fieldShowChangedIcon/>
</c:if>
