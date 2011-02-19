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

<%@ attribute name="component" required="true" 
              description="The UIF component for which the script will be generated" 
              type="org.kuali.rice.kns.uif.ScriptEventSupport"%>
              
<%-- creates the script event registration code for the events 
supported and configured on the component --%>     

<krad:buffer>
<jsp:attribute name="fragment">
  <c:if test="${component.supportsOnLoad && (!empty component.onLoadScript)}">
    $("#" + "${component.id}").load(function() {
     ${component.onLoadScript}
    });
  </c:if>

  <c:if test="${component.supportsOnUnload && (!empty component.onUnloadScript)}">
    $("#" + "${component.id}").unload(function() {
     ${component.onUnloadScript}
    });
  </c:if>

  <c:if test="${component.supportsOnBlur && (!empty component.onBlurScript)}">
    $("#" + "${component.id}").blur(function() {
     ${component.onBlurScript}
    });
  </c:if>

  <c:if test="${component.supportsOnChange && (!empty component.onChangeScript)}">
    $("#" + "${component.id}").change(function() {
     ${component.onChangeScript}
    });
  </c:if>

  <c:if test="${component.supportsOnClick && (!empty component.onClickScript)}">
    $("#" + "${component.id}").click(function() {
     ${component.onClickScript}
    });
  </c:if>

  <c:if test="${component.supportsOnDblClick && (!empty component.onDblClickScript)}">
    $("#" + "${component.id}").dblclick(function() {
     ${component.onDblClickScript}
    });
  </c:if>

  <c:if test="${component.supportsOnFocus && (!empty component.onFocusScript)}">
    $("#" + "${component.id}").focus(function() {
     ${component.onFocusScript}
    });
  </c:if> 

  <c:if test="${component.supportsOnKeyPress && (!empty component.onKeyPressScript)}">
    $("#" + "${component.id}").keypress(function() {
     ${component.onKeyPressScript}
    });
  </c:if> 

  <c:if test="${component.supportsOnKeyUp && (!empty component.onKeyUpScript)}">
    $("#" + "${component.id}").keyup(function() {
     ${component.onKeyUpScript}
    });
  </c:if> 

  <c:if test="${component.supportsOnKeyDown && (!empty component.onKeyDownScript)}">
    $("#" + "${component.id}").keydown(function() {
     ${component.onKeyDownScript}
    });
  </c:if>    

  <c:if test="${component.supportsOnKeyDown && (!empty component.onKeyDownScript)}">
    $("#" + "${component.id}").keydown(function() {
     ${component.onKeyDownScript}
    });
  </c:if>  

  <c:if test="${component.supportsOnMouseOver && (!empty component.onMouseOverScript)}">
    $("#" + "${component.id}").mouseover(function() {
     ${component.onMouseOverScript}
    });
  </c:if>  
    
  <c:if test="${component.supportsOnMouseOut && (!empty component.onMouseOutScript)}">
    $("#" + "${component.id}").mouseout(function() {
     ${component.onMouseOutScript}
    });
  </c:if>  
 
  <c:if test="${component.supportsOnMouseUp && (!empty component.onMouseUpScript)}">
    $("#" + "${component.id}").mouseup(function() {
     ${component.onMouseUpScript}
    });
  </c:if>

  <c:if test="${component.supportsOnMouseDown && (!empty component.onMouseDownScript)}">
    $("#" + "${component.id}").mousedown(function() {
     ${component.onMouseDownScript}
    });
  </c:if>

  <c:if test="${component.supportsOnMouseMove && (!empty component.onMouseMoveScript)}">
    $("#" + "${component.id}").mousemove(function() {
     ${component.onMouseMoveScript}
    });
  </c:if>
</jsp:attribute>
</krad:buffer>

<c:if test="${!empty fn:trim(bufferOut)}">
  <script type="text/javascript">
    ${fn:trim(bufferOut)}
  </script>
</c:if>