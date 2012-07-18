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
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=UifCompView_KNS&methodToCall=start&readOnlyFields=field91" />Uif Components (Kitchen Sink)</a></li>
     <li><portal:portalLink displayTitle="true" title="Standard Layout Demo" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-StandardLayout-KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Validation Framework Demo" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-ValidationLayout-KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="ServerSide Constraint Validation Demo" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-ValidationServerSide-KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Uif Layout Test" url="${ConfigProperties.application.url}/kr-krad/uilayouttest?viewId=UifLayoutView_KNS&methodToCall=start" /></li>
	   <li><portal:portalLink displayTitle="true" title="Test View 1 (old)" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView1_KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Test View 2 (old)" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView2_KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Incident Report" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView2_KNS&methodToCall=foo" /></li>
     <li><portal:portalLink displayTitle="true" title="Test Open Authorization" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=TestOpenAuthView&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Configuration Test View" url="${ConfigProperties.application.url}/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView_KNS&methodToCall=start" /></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/dialog-configuration-test?viewId=DialogTestView_KNS&methodToCall=start" />Dialog Test View</a></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=ConfigurationTestView-Collections_KNS&methodToCall=start"/>Collections Configuration Test View</a></li>
   </ul>

</div>
<channel:portalChannelBottom />
