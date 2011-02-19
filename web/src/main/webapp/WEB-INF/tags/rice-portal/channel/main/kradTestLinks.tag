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

<channel:portalChannelTop channelTitle="KRAD Testing" />
<div class="body">
  
  <ul class="chan">
	 <li><portal:portalLink displayTitle="true" title="Test View 1" url="${ConfigProperties.application.url}/spring/uitest?viewId=Travel-testView1&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Travel Account Inquiry" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&number=a14&viewTypeName=INQUIRY&objectClassName=edu.sampleu.travel.bo.TravelAccount"/></li>
     <li><portal:portalLink displayTitle="true" title="Travel Account Inquiry (KNS Parms)" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&number=a14&businessObjectClassName=edu.sampleu.travel.bo.TravelAccount"/></li>
     <li><portal:portalLink displayTitle="true" title="Travel Account Maintenance (New)" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=start&viewTypeName=MAINTENANCE&objectClassName=edu.sampleu.travel.bo.TravelAccount"/></li> 
  </ul>
<br/>
  <ul class="chan">
     <li><portal:portalLink displayTitle="true" title="Spring - Lookup Travel Account" url="${ConfigProperties.application.url}/lookup.mvc?methodToCall=start&objectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&docFormKey=88888888" /></li>
  </ul>
  
</div>
<channel:portalChannelBottom />
