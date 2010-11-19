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

<%@ attribute name="page" required="true" description="The page to be rendered" %>

<table>
  <tbody>
    <tr>
        <td width="1%">
            <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
                     alt="" width="20" height="20" />
        </td>
        <td>
         <div id="${page.styleClass}">
            <!----------------------------------- #PAGE HEADER --------------------------------------->
            <jsp:include page="${page.header.handler}">
                 <jsp:param name="header" value="${page.header}"/>
            </jsp:include>
            
            <!----------------------------------- #PAGE ERRORS --------------------------------------->
            <jsp:include page="${page.errors.handler}">
                 <jsp:param name="field" value="${errors}"/>
            </jsp:include>
            
            <br/>
            
            <%-- begin of section rendering --%>
            <c:forEach items="${page.items}" var="section" varStatus="sectionVarStatus">
                 <jsp:include page="${section.handler}">
                 <jsp:param name="section" value="${section}"/>
                 <jsp:param name="index" value="${sectionVarStatus.index}"/>
             </jsp:include>
            </c:forEach>  
            <%-- end of section rendering --%>

            <!----------------------------------- #PAGE FOOTER --------------------------------------->
             <layout:boxLayoutManager items="${page.actionFields}" orientation="horizontal" />

             <!-- TODO: need container for remaining errors -->
			 <p>&nbsp;</p>
		  </td>
		  <td width="20">
            <img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20"/>
          </td>
	   </tr>
    </tbody>        
</table>
