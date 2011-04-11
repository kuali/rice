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

<tiles:useAttribute name="items" classname="java.util.List"/>
<tiles:useAttribute name="manager" classname="org.kuali.rice.kns.uif.layout.StackedLayoutManager"/>
<tiles:useAttribute name="container" classname="org.kuali.rice.kns.uif.container.ContainerBase"/>

<%--
    Stacked Layout Manager:
    
      Uses a box manager for rendering the collection groups
 --%>
 
<%-- just pass through for now using the items built in the manager --%>
<tiles:insertTemplate template="box.jsp">
   <tiles:putAttribute name="items" value="${manager.stackedGroups}"/>
   <tiles:putAttribute name="manager" value="${manager}"/>
   <tiles:putAttribute name="container" value="${container}"/>
</tiles:insertTemplate>  