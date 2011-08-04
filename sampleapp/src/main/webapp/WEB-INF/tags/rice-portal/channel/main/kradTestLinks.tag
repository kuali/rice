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

<channel:portalChannelTop channelTitle="KRAD Testing - KS L&F" />
<div class="body">
  <strong>Screen Element Testing</strong>
  <ul class="chan">
  	<li><portal:portalLink displayTitle="true" title="Layout Test" url="${ConfigProperties.application.url}/spring/uilayouttest?viewId=LayoutTestView&methodToCall=start" /></li>
  	<li><portal:portalLink displayTitle="true" title="Uif Components (Kitchen Sink)" url="${ConfigProperties.application.url}/spring/uicomponents?viewId=UifComponentsView&methodToCall=start" /></li>
	 <li><portal:portalLink displayTitle="true" title="Test View 1" url="${ConfigProperties.application.url}/spring/uitest?viewId=Travel-testView1&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Test View 2" url="${ConfigProperties.application.url}/spring/uitest?viewId=Travel-testView2&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Incident Report" url="${ConfigProperties.application.url}/spring/uitest?viewId=Travel-testView2&methodToCall=foo" /></li>
   </ul>
   <br/>
   <strong>BO Class Tests</strong>
   <ul class="chan">
     <li><portal:portalLink displayTitle="true" title="Travel Account Inquiry" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&number=a14&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&viewName=ks_inquiry&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
     <li><portal:portalLink displayTitle="true" title="Travel Account Maintenance (New)" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&viewName=ks_maintenance&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
     <li><portal:portalLink displayTitle="true" title="Travel Account Maintenance (Edit)" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&viewName=ks_maintenance&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
     <li><portal:portalLink displayTitle="true" title="Travel Account Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&viewName=ks_lookup&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  </ul>
</div>
<channel:portalChannelBottom />
