<%--
  ~ Copyright 2006-2011 The Kuali Foundation
  ~
  ~ Licensed under the Educational Community License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.opensource.org/licenses/ecl2.php
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ include file="/rice-portal/jsp/sys/riceTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Sample Travel Application - KNS L&F" />
<div class="body">

  <!-- the portal_lightbox class is used to designate links that open in a lightbox - inquiry links -->
  <strong>BO Class Tests</strong>
  <ul class="chan">
    <li><a id="trav_acc_inq_bo" class="portal_link portal_lightbox" href="${ConfigProperties.application.url}/kr-krad/inquiry?methodToCall=start&number=a14&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&dialogMode=true"/>Travel Account Inquiry</a></li>
    <li><portal:portalLink displayTitle="true" title="Travel Account Maintenance (New)" url="${ConfigProperties.application.url}/kr-krad/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    <li><portal:portalLink displayTitle="true" title="Travel Account Maintenance (Edit)" url="${ConfigProperties.application.url}/kr-krad/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    <li><portal:portalLink displayTitle="true" title="Travel Account Lookup" url="${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&criteriaFields['number']=a*&readOnlyFields=number&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li><portal:portalLink displayTitle="true" title="Travel Account Type Lookup" url="${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccountType&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li><portal:portalLink displayTitle="true" title="Travel Account Multi-Value Lookup" url="${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&multipleValuesSelect=true&suppressActions=true&conversionFields=number:foo,name:foo" /></li>
  </ul>
  <br/>
  <strong>Non BO Class Tests</strong>
  <ul class="chan">
    <li><a id="fisc_off_inq_1" class="portal_link portal_lightbox"  href="${ConfigProperties.application.url}/kr-krad/inquiry?methodToCall=start&id=2&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&dialogMode=true"/>FiscalOfficerInfo Inquiry</a></li>
    <li><a id="fisc_off_inq_2" class="portal_link portal_lightbox"  href="${ConfigProperties.application.url}/kr-krad/inquiry?methodToCall=start&id=2&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&viewName=FiscalOfficerInfoInquiry2&dialogMode=true"/>FiscalOfficerInfo Inquiry 2</a></li>
    <li><a id="fisc_off_inq_3" class="portal_link portal_lightbox"  href="${ConfigProperties.application.url}/kr-krad/inquiry?methodToCall=start&id=2&viewId=FiscalOfficerInfoInquiry3&dialogMode=true"/>FiscalOfficerInfo Inquiry 3</a></li>
    <br/>
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Lookup" url="${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Lookup 2" url="${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start&viewId=FiscalOfficerInfoLookupViewUsername&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <br/>
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Maintenance (New)" url="${ConfigProperties.application.url}/kr-krad/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Maintenance (Edit)" url="${ConfigProperties.application.url}/kr-krad/maintenance?methodToCall=maintenanceEdit&id=2&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo"/></li>
    <br>
    <li><a id="trav_acc_inq_no_bo" class="portal_link portal_lightbox" href="${ConfigProperties.application.url}/kr-krad/inquiry?methodToCall=start&number=a2&dataObjectClassName=edu.sampleu.travel.dto.TravelAccountInfo&dialogMode=true"/>TravelAccountInfo Inquiry</a></li>
  </ul>

</div>
<channel:portalChannelBottom />