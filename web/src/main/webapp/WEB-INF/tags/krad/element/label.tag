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

<%@ attribute name="item" required="true" description="The item the label should be rendered for." %>
<%@ attribute name="renderCell" required="false" description="Indicates whether a table cell should be rendered for the label." %>

<c:if test="${empty renderCell}">
  <c:set var="renderCell" value="true"/>
</c:if>

<c:if test="${renderCell}">
  <th class="grid" style="${item.labelStyle}" align="${item.labelAlign}" width="${item.labelWidth}" colspan="${item.labelColSpan}">
</c:if>

  <c:if test="${!item.readOnly}">
    <label id="${item.name}.label" for="${item.name}">
  </c:if>

  <c:choose>  	
	<c:when test="${!(empty item.label)}">     	
		<c:if test="${item.required && !item.readOnly}">
             ${Constants.REQUIRED_FIELD_SYMBOL}&nbsp;
        </c:if>
       
        <c:out value="${item.label}" />
        <c:if test="${item.renderLabelColon}">:</c:if>          	
	</c:when>            
	<c:otherwise>
       &nbsp;
    </c:otherwise>
  </c:choose>
 
  <c:if test="${!item.readOnly}">    	
   	</label>
  </c:if>

<c:if test="${renderCell}"> 
  </th>
</c:if>