<%--
 Copyright 2006-2007 The Kuali Foundation
 
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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp"%>

<tiles:useAttribute name="panel" classname="org.kuali.rice.kns.uif.decorator.PanelDecorator"/>
<tiles:useAttribute name="decoratorChain" classname="org.kuali.rice.kns.uif.decorator.DecoratorChain"/>
<tiles:useAttribute name="templateParameters" classname="java.util.Map"/>

<c:set var="panelHeader" value="${panel.panelHeader}"/>
<c:set var="panelHeaderText" value="${panelHeader.headerText}"/>
<c:if test="${empty panelHeaderText}">
  <c:set var="panelHeaderText" value="${decoratorChain.decoratedComponent.header.headerText}"/>
</c:if>

<c:set var="openPanelHeaderContents" value="<img src='${ConfigProperties.krad.externalizable.images.url}arrow-exp.png' width='16' height='16' alt='collapse'>${panelHeaderText}"/>
<c:set var="closedPanelHeaderContents" value="<img src='${ConfigProperties.krad.externalizable.images.url}arrow-col.png' width='16' height='16' alt='expand'>${panelHeaderText}"/>

<c:set var="panelToggleLink" value="panel_toogle_${decoratorChain.decoratedComponent.id}"/>
<c:set var="panelDiv" value="panel_${decoratorChain.decoratedComponent.id}_div"/>

<c:set var="isOpen" value="${panel.defaultOpen}"/>

<div id="${panelHeader.id}" class="${panelHeader.styleClass}">
  <h2><a href="#" id="${panelToggleLink}"></a></h2>
</div>

<%-- render next decorator or the decorated group --%>
<div id="${panelDiv}" class="${panel.styleClass}">
  <krad:template component="${decoratorChain.decoratedComponent}" templateParameters="${templateParameters}"/>
</div>

<script type="text/javascript">
  doPanel("${panelToggleLink}", "${openPanelHeaderContents}", "${closedPanelHeaderContents}", "${panelDiv}", ${isOpen});
</script>