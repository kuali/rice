<%--
 Copyright 2005-2007 The Kuali Foundation
 
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

<c:choose>
  <c:when test="${KualiForm.renderFullView}">
  
     <%-- NOTE: Put application content here (such as a header, 
          or application navigation) to wrap all the views --%>
  
     <%-- render full view --%>
     <krad:template component="${KualiForm.view}"/>
     
  </c:when>
  <c:otherwise>
  
     <%-- render page only --%>
     <html>
       <s:nestedPath path="KualiForm">
         <div id="viewpage_div">
            <krad:template component="${KualiForm.view.currentPage}"/>
        
            <c:if test="${KualiForm.view.renderForm}">
              <form:hidden path="pageId"/>
              <form:hidden path="jumpToId"/>
          	  <form:hidden path="jumpToName"/>
            </c:if>
            <krad:script value="performJumpTo();"/>
         </div>
       </s:nestedPath>
     </html>
     
  </c:otherwise>
</c:choose>
