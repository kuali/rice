<%--
 Copyright 2005-2006 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/kr/WEB-INF/tlds/c.tld" %>
<%@ taglib uri="/kr/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/kr/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/kr/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/kr/WEB-INF/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags/kr" prefix="kul"%>

<%@ attribute name="documentTypeName" required="true" %>

<%@ attribute name="showDocumentInfo" required="false" %>
<%@ attribute name="headerMenuBar" required="false" %>
<%@ attribute name="headerTitle" required="false" %>
<%@ attribute name="htmlFormAction" required="false" %>
<%@ attribute name="renderMultipart" required="false" %>
<%@ attribute name="showTabButtons" required="false" %>
<%@ attribute name="headerDispatch" required="false" %>
<%@ attribute name="headerTabActive" required="false" %>
<%@ attribute name="feedbackKey" required="false" %>
<%@ attribute name="auditCount" required="false" %>

<%@ variable name-given="documentEntry" scope="NESTED" %>
<c:set var="documentEntry" value="${DataDictionary[documentTypeName]}" />

<kul:page docTitle="${documentEntry.label}" transactionalDocument="${documentEntry.transactionalDocument}"
  headerMenuBar="${headerMenuBar}" showDocumentInfo="${showDocumentInfo}" headerTitle="${headerTitle}" htmlFormAction="${htmlFormAction}" renderMultipart="${renderMultipart}" showTabButtons="${showTabButtons}" headerDispatch="${headerDispatch}" headerTabActive="${headerTabActive}" feedbackKey="${feedbackKey}" auditCount="${auditCount}">
    <jsp:doBody/>
</kul:page>