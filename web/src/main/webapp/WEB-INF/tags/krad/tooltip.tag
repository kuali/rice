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

<%-- Add Tooltip if the tooltip content is not empty --%>
<c:if test="${(!empty component.toolTip.tooltipContent)}">
  <krad:script component="${component}" value="createTooltip('${component.id}', '${component.toolTip.tooltipContent}', ${component.toolTip.templateOptionsJSString}, ${component.toolTip.onMouseHover}, ${component.toolTip.onFocus});" />
  <krad:script component="${component}" value="addAttribute('${component.id}', 'class', 'uif-tooltip', true);"/>
</c:if>
