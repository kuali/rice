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

<tiles:useAttribute name="group" classname="org.kuali.rice.kns.ui.container.PanelGroup"/>

<c:set var="panelHeader" value="${group.panelHeader}"/>

<div class="${panelHeader.styleClass}">
  <h2><a href="#" id="show_tab-${group.id}-div"><img src="${ConfigProperties.krad.externalizable.images.url}arrow-col.png" width="16" height="16" alt="collapse">${panelHeader.title}</a></h2>
</div>

<div id="tab-${group.id}-div">
  <%-- render default group --%>
  <tiles:insertTemplate template="${group.wrappedGroupTemplate}">
        <tiles:putAttribute name="group" value="${group}"/>
  </tiles:insertTemplate>
</div>

<c:set var="isOpen" value="${group.defaultOpen}"/>

<script type="text/javascript">
  $(document).ready(function() {
     <c:if test="${isOpen}">
       $("#tab-${group.id}-div").slideDown(000);
     </c:if>
       
     <c:if test="${!isOpen}">
       $("#tab-${group.id}-div").slideUp(000);
     </c:if>
  
      $("#show_tab-${group.id}-div").toggle(
        function() {
          $("#tab-${group.id}-div").slideUp(600);
          $("#show_tab-${group.id}-div").html("<img src='${ConfigProperties.krad.externalizable.images.url}arrow-col.png' width='16' height='16' alt='collapse'>${panelHeader.title}");
        }, function() {
          $("#tab-${group.id}-div").slideDown(600);
          $("#show_tab-${group.id}-div").html("<img src='${ConfigProperties.krad.externalizable.images.url}arrow-exp.png' width='16' height='16' alt='expand'>${panelHeader.title}");
        }
      );
  });
</script>