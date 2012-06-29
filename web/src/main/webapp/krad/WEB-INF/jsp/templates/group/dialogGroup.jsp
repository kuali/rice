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

<tiles:useAttribute name="group" classname="org.kuali.rice.krad.uif.container.DialogGroup"/>

<krad:group group="${group}">


  <%-- get layout manager styles --%>
  <c:if test="${!empty group.layoutManager.styleClassesAsString}">
    <c:set var="styleClass" value="class=\"${group.layoutManager.styleClassesAsString}\""/>
  </c:if>

  <c:if test="${!empty group.layoutManager.style}">
    <c:set var="style" value="style=\"${group.layoutManager.style}\""/>
  </c:if>

  <%-- render items via layout manager --%>
  <div id="${group.layoutManager.id}_boxLayout" ${style} ${styleClass}>
    <krad:template component="${group.prompt}" parent="${group}"/>
    <krad:template component="${group.explanation}" parent="${group}"/>
    <c:forEach items="${group.items}" var="item" varStatus="itemVarStatus">
      <krad:template component="${item}"/>
    </c:forEach>
    <krad:template component="${group.responseInputField}" parent="${group}"/>
  </div>

</krad:group>
