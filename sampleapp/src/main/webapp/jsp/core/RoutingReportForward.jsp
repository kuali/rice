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
<%@ include file="tldHeader.jsp"%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<c:set var="routingReportUrl" value="${ConfigProperties.workflow.url}/RoutingReport.do"/>

<form name="routeReportForm" id="routeReportForm" method="post" action="${routingReportUrl}">
<input type="hidden" name="documentTypeParam" value="${documentTypeName}">
<input type="hidden" name="initiatorNetworkId" value="${initiatorNetworkId}">
<input type="hidden" name="documentContent" value="<c:out value="${documentContent}" escapeXml="true"/>">
Click this button to see the Routing Report:&nbsp;&nbsp;&nbsp;<input type="submit" value="View Report">
</form>

<script language ="javascript">
window.onload = document.routeReportForm.submit();
</script>

