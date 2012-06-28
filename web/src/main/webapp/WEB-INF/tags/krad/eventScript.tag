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
              type="org.kuali.rice.krad.uif.component.ScriptEventSupport"%>
              
<%-- creates the script event registration code for the events 
supported and configured on the component --%>     

<krad:buffer>
<jsp:attribute name="fragment">
  <c:if test="${!empty component.onLoadScript}">
    jQuery('#' + '${component.id}').load(function() {
     ${component.onLoadScript}
    });
  </c:if>

  <c:if test="${!empty component.onDocumentReadyScript}">
    jQuery(document).ready(function() {
     ${component.onDocumentReadyScript}
    });
  </c:if>

  <c:if test="${!empty component.onUnloadScript}">
    jQuery('#' + '${component.id}').unload(function() {
     ${component.onUnloadScript}
    });
  </c:if>

  <c:if test="${!empty component.onBlurScript}">
    jQuery('#' + '${component.id}').blur(function() {
     ${component.onBlurScript}
    });
  </c:if>

  <c:if test="${!empty component.onChangeScript}">
    jQuery('#' + '${component.id}').change(function() {
     ${component.onChangeScript}
    });
  </c:if>

  <c:if test="${!empty component.onClickScript}">
    jQuery('#' + '${component.id}').click(function(e) {
     ${component.onClickScript}
    });
  </c:if>


  <c:if test="${!empty component.onDblClickScript}">
    jQuery('#' + '${component.id}').dblclick(function() {
     ${component.onDblClickScript}
    });
  </c:if>

  <c:if test="${!empty component.onFocusScript}">
    jQuery('#' + '${component.id}').focus(function() {
     ${component.onFocusScript}
    });
  </c:if> 

  <c:if test="${!empty component.onKeyPressScript}">
    jQuery('#' + '${component.id}').keypress(function() {
     ${component.onKeyPressScript}
    });
  </c:if> 

  <c:if test="${!empty component.onKeyUpScript}">
    jQuery('#' + '${component.id}').keyup(function() {
     ${component.onKeyUpScript}
    });
  </c:if> 

  <c:if test="${!empty component.onKeyDownScript}">
    jQuery('#' + '${component.id}').keydown(function() {
     ${component.onKeyDownScript}
    });
  </c:if>    

  <c:if test="${!empty component.onKeyDownScript}">
    jQuery('#' + '${component.id}').keydown(function() {
     ${component.onKeyDownScript}
    });
  </c:if>  

  <c:if test="${!empty component.onMouseOverScript}">
    jQuery('#' + '${component.id}').mouseover(function() {
     ${component.onMouseOverScript}
    });
  </c:if>  
    
  <c:if test="${!empty component.onMouseOutScript}">
    jQuery('#' + '${component.id}').mouseout(function() {
     ${component.onMouseOutScript}
    });
  </c:if>  
 
  <c:if test="${!empty component.onMouseUpScript}">
    jQuery('#' + '${component.id}').mouseup(function() {
     ${component.onMouseUpScript}
    });
  </c:if>

  <c:if test="${!empty component.onMouseDownScript}">
    jQuery('#' + '${component.id}').mousedown(function() {
     ${component.onMouseDownScript}
    });
  </c:if>

  <c:if test="${!empty component.onMouseMoveScript}">
    jQuery('#' + '${component.id}').mousemove(function() {
     ${component.onMouseMoveScript}
    });
  </c:if>
</jsp:attribute>
</krad:buffer>

<c:if test="${!empty fn:trim(bufferOut)}">
    <krad:script component="${component}" value="${fn:trim(bufferOut)}" />
</c:if>