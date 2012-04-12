<%--

    Copyright 2005-2012 The Kuali Foundation

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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="element" classname="org.kuali.rice.krad.uif.element.DataTable"/>

<%--
    Element that creates a table element and then invokes datatables to complete
    the table based on configured source data
 --%>

<c:if test="${!empty element.styleClassesAsString}">
  <c:set var="styleClass" value="class=\"${element.styleClassesAsString}\""/>
</c:if>

<c:if test="${!empty element.style}">
  <c:set var="style" value="style=\"${element.style}\""/>
</c:if>

<table id="${element.id}" ${style} ${styleClass}>

</table>

<krad:script value="createTable('${element.id}', ${element.richTable.templateOptionsJSString}); "/>

