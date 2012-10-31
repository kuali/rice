<%--
 Copyright 2007-2009 The Kuali Foundation
 
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
<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="KRAD Testing - KNS L&F" />
<div class="body">

   <strong>Screen Element Testing</strong>
   <ul class="chan">
  	 <li><portal:portalLink displayTitle="true" title="Uif Layout Test" appContextName="${ConfigProperties.app.context.name}" url="${ConfigProperties.application.url}/kr-krad/uilayouttest?viewId=UifLayoutView_KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Standard Layout Demo" appContextName="${ConfigProperties.app.context.name}" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-StandardLayout-KNS&methodToCall=start" /></li>
  	 <li><portal:portalLink displayTitle="true" title="Uif Components (Kitchen Sink)" appContextName="${ConfigProperties.app.context.name}" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=UifCompView_KNS&methodToCall=start&readOnlyFields=field91" /></li>
	   <li><portal:portalLink displayTitle="true" title="Test View 1" appContextName="${ConfigProperties.app.context.name}" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView1_KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Test View 2" appContextName="${ConfigProperties.app.context.name}" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView2_KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Incident Report" appContextName="${ConfigProperties.app.context.name}" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView2_KNS&methodToCall=foo" /></li>
     <li><portal:portalLink displayTitle="true" title="Test Open Authorization" appContextName="${ConfigProperties.app.context.name}" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=TestOpenAuthView&methodToCall=start" /></li>
   </ul>

</div>
<channel:portalChannelBottom />
