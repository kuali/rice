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

<tiles:useAttribute name="group" classname="org.kuali.rice.kns.uif.container.Group"/>

<krad:div component="${group}">

  <!----------------------------------- #GROUP '${group.id}' HEADER --------------------------------------->
  <krad:template component="${group.header}"/>
  
  <div id="${group.id}_accordion">
    <%-- group summary text --%>
    <krad:template component="${group.summaryMessageField}"/>
    <krad:template component="${group.errorsField}"/>
  
    <%-- render items through layout manager --%>
    <tiles:insertTemplate template="${group.layoutManager.template}">
        <tiles:putAttribute name="items" value="${group.items}"/>
        <tiles:putAttribute name="manager" value="${group.layoutManager}"/>
        <tiles:putAttribute name="container" value="${group}"/>
    </tiles:insertTemplate>

    <!----------------------------------- #GROUP '${group.id}' FOOTER --------------------------------------->
    <krad:template component="${group.footer}"/>
  </div>
    
</krad:div>

<%-- render group accordion --%>
<krad:template component="${group.accordion}" parent="${group}"/>