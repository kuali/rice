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

<!-- state maintenance for returning the user to the action list if they started there -->
<logic:present name="KualiForm" property="returnToActionList">
      <html:hidden name="KualiForm" property="returnToActionList" />
</logic:present>

<html:hidden property="tabStatesSize" value="${KualiForm.currentTabIndex - 1}" />
<html:hidden name="KualiForm" property="dialogMode" />