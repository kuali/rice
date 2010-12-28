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

<tiles:useAttribute name="page" classname="org.kuali.rice.kns.ui.container.Page"/>

<table>
  <tbody>
    <tr>
        <td width="1%">
            <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
                     alt="" width="20" height="20" />
        </td>
        <td>
        
         <c:if test="${page.renderHeader}"> 
           <!----------------------------------- #PAGE HEADER --------------------------------------->
           <tiles:insertTemplate template="${page.header.template}">
              <tiles:putAttribute name="${page.header.componentTypeName}" value="${page.header}"/>
           </tiles:insertTemplate>   
         </c:if>
      
         <%--TODO: change id to page.id once style sheet is fixed --%>
         <!----------------------------------- #PAGE BODY --------------------------------------->
         <div id="workarea" class="${page.styleClass}">
            <%-- render sections --%>
            <c:forEach items="${page.items}" var="section" varStatus="sectionVarStatus">
              <tiles:insertTemplate template="${section.template}">
                <tiles:putAttribute name="${section.componentTypeName}" value="${section}"/>
              </tiles:insertTemplate>
            </c:forEach>
         </div>
          
        </td>
        <td width="20">
            <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20"/>
        </td>
     </tr>
  </tbody>        
</table>          