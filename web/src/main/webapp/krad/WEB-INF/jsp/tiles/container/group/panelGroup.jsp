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

<tiles:useAttribute name="group" classname="org.kuali.rice.kns.uif.decorator.PanelGroupDecorator"/>

<c:set var="panelHeader" value="${group.panelHeader}"/>
<c:set var="openPanelHeaderContents" value="<img src='${ConfigProperties.krad.externalizable.images.url}arrow-exp.png' width='16' height='16' alt='collapse'>${panelHeader.headerText}"/>
<c:set var="closedPanelHeaderContents" value="<img src='${ConfigProperties.krad.externalizable.images.url}arrow-col.png' width='16' height='16' alt='expand'>${panelHeader.headerText}"/>

<c:set var="panelToggleLink" value="panel_toogle_${group.decoratedGroup.id}"/>
<c:set var="panelDiv" value="panel_${group.decoratedGroup.id}_div"/>

<c:set var="isOpen" value="${group.defaultOpen}"/>

<div id="${panelHeader.id}" class="${panelHeader.styleClass}">
  <h2><a href="#" id="${panelToggleLink}"></a></h2>
</div>

<%-- render decorated group --%>
<div id="${panelDiv}" class="${group.styleClass}">
  <krad:template component="${group.decoratedGroup}"/>
</div>

<script type="text/javascript">
  doPanel("${panelToggleLink}", "${openPanelHeaderContents}", "${closedPanelHeaderContents}", "${panelDiv}", ${isOpen});
</script>