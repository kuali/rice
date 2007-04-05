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

<%@ attribute name="boClassName" required="true" %>
<%@ attribute name="tabindexOverride" required="false" %>
<%@ attribute name="anchor" required="false" %>
<%@ attribute name="fieldLabel" required="false" description="this value is used in the icon's alt and title attributes to provide more descriptive information" %>
<%@ attribute name="iconLabel" required="false" description="the value of this String will be rendered before the icon itself" %>
<%@ attribute name="lookedUpCollectionName" required="true" description="the name of the collection being looked up, this value will be returned to the calling document" %>

<c:choose>
  <c:when test="${!empty tabindexOverride}">
    <c:set var="tabindex" value="${tabindexOverride}"/>
  </c:when>
  <c:otherwise>
    <c:set var="tabindex" value="${KualiForm.nextArbitrarilyHighIndex}"/>
  </c:otherwise>
</c:choose>

<c:if test="${!empty iconLabel}">
	<c:out value="${iconLabel}"/>
</c:if>

<input type="image" tabindex="${tabindex}" name="methodToCall.performLookup.(!!${boClassName}!!).(:;${lookedUpCollectionName};:).((%true%)).anchor${anchor}"
   src="images/searchicon.gif" border="0" class="tinybutton" valign="middle" alt="Multiple Value Search ${fieldLabel}" title="Multiple Value Search ${fieldLabel}" />