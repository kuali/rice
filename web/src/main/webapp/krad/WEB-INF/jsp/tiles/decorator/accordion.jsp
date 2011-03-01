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

<tiles:useAttribute name="decorator" classname="org.kuali.rice.kns.uif.decorator.AccordionDecorator"/>
<tiles:useAttribute name="decoratorChain" classname="org.kuali.rice.kns.uif.decorator.DecoratorChain"/>
<tiles:useAttribute name="templateParameters" classname="java.util.Map"/>

<c:set var="accordionHeader" value="${decorator.accordionHeader}"/>
<c:set var="accordionHeaderText" value="${accordionHeader.headerText}"/>
<c:if test="${empty accordionHeaderText}">
  <c:set var="accordionHeaderText" value="${decoratorChain.decoratedComponent.header.headerText}"/>
</c:if>

<c:set var="openAccordionHeaderContents" value="<img src='${ConfigProperties.krad.externalizable.images.url}arrow-exp.png' width='16' height='16' alt='collapse'>${accordionHeaderText}"/>
<c:set var="closedAccordionHeaderContents" value="<img src='${ConfigProperties.krad.externalizable.images.url}arrow-col.png' width='16' height='16' alt='expand'>${accordionHeaderText}"/>

<c:set var="accordionToggleLink" value="accordion_toogle_${decoratorChain.decoratedComponent.id}"/>
<c:set var="accordionDiv" value="accordion_${decoratorChain.decoratedComponent.id}_div"/>

<c:set var="isOpen" value="${decorator.defaultOpen}"/>

<div id="${accordionHeader.id}" class="${accordionHeader.styleClassesAsString}">
  <h2><a href="#" id="${accordionToggleLink}"></a></h2>
</div>

<%-- render next decorator or the decorated group --%>
<div id="${accordionDiv}" class="${decorator.styleClassesAsString} accordion">
  <krad:template component="${decoratorChain.decoratedComponent}" templateParameters="${templateParameters}"/>
</div>

<script type="text/javascript">
  createAccordion("${accordionToggleLink}", "${openAccordionHeaderContents}", "${closedAccordionHeaderContents}", "${accordionDiv}", ${isOpen});
</script>