<%--
  ~ Copyright 2006-2014 The Kuali Foundation
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

<channel:portalChannelTop channelTitle="Legacy Testing" />
<div class="body">
  <strong>KNS OJB Documents</strong>
  <ul class="chan">
    <li><portal:portalLink displayTitle="true" title="Travel Company"
      url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.legacy.LegacyTravelCompanyOjb&docFormKey=88888888&showMaintenanceLinks=true"/>
      <br>(Maintenance Document with KNS and OJB. BO also mapped with JPA, see below.)</li>
    <li><portal:portalLink displayTitle="true" title="Travel Authorization" url="${ConfigProperties.application.url}/legacyTravelAuthorization.do?returnLocation=${ConfigProperties.application.url}/portal.do&methodToCall=docHandler&command=initiate" />
      <br> (Transactional Document with KNS and OJB.)</li>
  </ul>
  <strong>KRAD JPA Documents</strong>
  <ul class="chan">
    <li><portal:portalLink displayTitle="true" title="Travel Company"
      url="${ConfigProperties.krad.url}/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.legacy.LegacyTravelCompany&showMaintenanceLinks=true&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/>
      <br>(Maintenance Document with KNS and OJB. BO also mapped with JPA, see below.)</li>
    </ul>
</div>
<channel:portalChannelBottom />

