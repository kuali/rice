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

<%@ attribute name="componentId" required="true" 
              description="Id for the component the parameter insertion event should be associated with"%> 
<%@ attribute name="parameters" required="true" 
              description="The Map of parameter key/value pairs to insert into the DOM" 
              type="java.util.Map"%>
              
<%--Creates an on click handler for the component with the given id to write out 
all the parameters of the given map to the DOM when the component is clicked. The 
parameters are written as hiddens with the parameter key as the input
name and the parameter value as the input value --%>

<c:if test="${!empty parameters}">            
  <script type="text/javascript">
    $("#" + "${componentId}").click(function() {
  	  <c:forEach items="${parameters}" var="parameter">
  	    $("<input type='hidden' name='${parameter.key}' value='${parameter.value}'/>").appendTo($("#formComplete"));
    	</c:forEach>
	  });
  </script>
</c:if>