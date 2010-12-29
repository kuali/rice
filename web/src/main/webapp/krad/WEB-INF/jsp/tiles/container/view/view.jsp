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

<!-- begin of view render -->
<krad:html htmlFormAction="${pageContext.request.contextPath}/spring/${View.controllerRequestMapping}"
                  headerTitle="${View.title}" additionalScriptFiles="${View.additionalScriptFiles}"
                  renderMultipart="true">
        
  <table width="100%">
   <c:if test="${View.renderHeader}">    
     <tr>    
       <td colspan="4">   
         <!----------------------------------- #VIEW HEADER --------------------------------------->
         <krad:template component="${View.header}"/>
       </td>
     </tr>
   </c:if>  
   
   <tr>   
     <td width="30px">
       <!----------------------------------- #VIEW NAVIGATION --------------------------------------->
       <tiles:insertTemplate template="${View.navigation.template}">
            <tiles:putAttribute name="${View.navigation.componentTypeName}" value="${View.navigation}" />
            <tiles:putAttribute name="currentPageId" value="${View.currentPageId}" />
       </tiles:insertTemplate>    
     </td>
     
     <td width="1%">
       <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
                     alt="" width="20" height="20" />
     </td>
     
     <td>
       <%-- begin of page render --%>
       <krad:template component="${View.currentPage}"/>
       <%-- end of page render --%>
    
       <%-- write out hiddens needed to maintain state 
       <tiles:insertTemplate template="${view.state.template}"--%>
     </td>
     
     <td width="20">
       <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20"/>
     </td>     
   </tr>
    
   <tr>
      <td colspan="4">   
        <!----------------------------------- #VIEW FOOTER --------------------------------------->
        <krad:template component="${View.footer}"/>
      </td>
   </tr> 
  </table> 
</krad:html>
<!-- end of view render -->