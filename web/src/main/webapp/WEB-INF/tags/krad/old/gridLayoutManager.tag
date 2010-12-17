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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="section" required="true" description="The section to be rendered." %>

<c:set var="cellwidth" value="${100/section.numberOfColumns}"/>

<table width="100%" cellpadding="0" cellspacing="0" class="datatable">

  <c:forEach items="${section.groups}" var="group" varStatus="groupVarStatus">
     <c:if test="${groupVarStatus.first || (groupVarStatus.count%section.numberOfColumns=1)}">
       <tr>
     </c:if>
     
       <td width="${cellWidth}">
         <!--  delegate to group layout manager -->
         <jsp:include page="${group.layoutManager.tagFileHandler}">
            <jsp:param name="group" value="${group}"/>
         </jsp:include>
       </td>
       
     <c:if test="${groupVarStatus.last || (groupVarStatus.count%section.numberOfColumns=0)}">
       </tr>
     </c:if>  
  </c:forEach>

</table>