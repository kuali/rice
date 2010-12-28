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
       <td colspan="2">   
         <!----------------------------------- #VIEW HEADER --------------------------------------->
         <tiles:insertTemplate template="${View.header.template}">
            <tiles:putAttribute name="${View.header.componentTypeName}" value="${View.header}"/>
         </tiles:insertTemplate>
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
     
     <td>
       <%-- begin of page render --%>
       <tiles:insertTemplate template="${View.currentPage.template}">
            <tiles:putAttribute name="${View.currentPage.componentTypeName}" value="${View.currentPage}" />
       </tiles:insertTemplate>    
       <%-- end of page render --%>
    
       <%-- write out hiddens needed to maintain state 
       <tiles:insertTemplate template="${view.state.template}"--%>
     </td>
   </tr>
    
   <tr>
      <td colspan="2">   
        <!----------------------------------- #VIEW FOOTER --------------------------------------->
        <tiles:insertTemplate template="${View.footer.template}">
            <tiles:putAttribute name="${View.footer.componentTypeName}" value="${View.footer}" />
        </tiles:insertTemplate>  
      </td>
   </tr> 
  </table> 
</krad:html>
<!-- end of view render -->