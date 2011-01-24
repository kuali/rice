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

<tiles:useAttribute name="view" classname="org.kuali.rice.kns.uif.container.View"/>

<!-- begin of view render -->
<krad:html htmlFormAction="${pageContext.request.contextPath}/spring/${view.controllerRequestMapping}"
                  headerTitle="${view.title}" additionalScriptFiles="${view.additionalScriptFiles}"
                  renderForm="${view.renderForm}" renderMultipart="true">
        
  <table width="100%">
   <c:if test="${view.renderHeader}">    
     <tr>    
       <td colspan="4">   
         <!----------------------------------- #VIEW HEADER --------------------------------------->
         <krad:template component="${view.header}"/>
       </td>
     </tr>
   </c:if>  
   
   <tr>   
     <td width="30px">
       <!----------------------------------- #VIEW NAVIGATION --------------------------------------->
       <c:if test="${!empty view.navigation}">
         <tiles:insertTemplate template="${view.navigation.template}">
              <tiles:putAttribute name="${view.navigation.componentTypeName}" value="${view.navigation}" />
              <tiles:putAttribute name="currentPageId" value="${view.currentPageId}" />
         </tiles:insertTemplate>    
       </c:if>
     </td>
     
     <td width="1%">
       <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
                     alt="" width="20" height="20" />
     </td>
     
     <td>
       <%-- begin of page render --%>
       <krad:template component="${view.currentPage}"/>
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
        <krad:template component="${view.footer}"/>
      </td>
   </tr> 
  </table> 
</krad:html>
<!-- end of view render -->