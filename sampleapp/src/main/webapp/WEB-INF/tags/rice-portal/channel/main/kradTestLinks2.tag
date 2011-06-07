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
	 <li><portal:portalLink displayTitle="true" title="Test View 1" url="${ConfigProperties.application.url}/spring/uitest?viewId=Travel-testView1_KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Test View 2" url="${ConfigProperties.application.url}/spring/uitest?viewId=Travel-testView2_KNS&methodToCall=start" /></li>
     <li><portal:portalLink displayTitle="true" title="Incident Report" url="${ConfigProperties.application.url}/spring/uitest?viewId=Travel-testView2_KNS&methodToCall=foo" /></li>
   </ul>
   <br/>
   <strong>BO Class Tests</strong>
   <ul class="chan">
     <li><portal:portalLink displayTitle="true" title="Travel Account Inquiry" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&number=a14&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
     <li><portal:portalLink displayTitle="true" title="Travel Account Maintenance (New)" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li> 
     <li><portal:portalLink displayTitle="true" title="Travel Account Maintenance (Edit)" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li> 
     <li><portal:portalLink displayTitle="true" title="Travel Account Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccount&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
     <li><portal:portalLink displayTitle="true" title="Travel Account Type Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.bo.TravelAccountType&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  </ul>
  <br/>
  <strong>Non BO Class Tests</strong>
  <ul class="chan">
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Inquiry" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&id=2&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Inquiry 2" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&id=2&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&viewId=FiscalOfficerInfoInquiry2&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Inquiry 3" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&id=2&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&viewId=FiscalOfficerInfoInquiry3&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    <br/>
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <br/>
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Maintenance (New)" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li> 
    <li><portal:portalLink displayTitle="true" title="FiscalOfficerInfo Maintenance (Edit)" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=maintenanceEdit&id=2&dataObjectClassName=edu.sampleu.travel.dto.FiscalOfficerInfo"/></li> 
  </ul>
  <br/>
  <strong>KNS Inquiries</strong>
   <ul class="chan">
	 <li><portal:portalLink displayTitle="true" title="Component" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&namespaceCode=KR-WKFLW&code=ActionList&dataObjectClassName=org.kuali.rice.core.impl.component.ComponentBo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
     <li><portal:portalLink displayTitle="true" title="Group" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&id=1&dataObjectClassName=org.kuali.rice.kim.impl.group.GroupBo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
	 <li><portal:portalLink displayTitle="true" title="Permission" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&permissionId=140&dataObjectClassName=org.kuali.rice.kim.bo.impl.PermissionImpl&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
     <li><portal:portalLink displayTitle="true" title="Responsibility" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&responsibilityId=1&dataObjectClassName=org.kuali.rice.kim.impl.responsibility.UberResponsibilityBo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
     <!-- <li><portal:portalLink displayTitle="true" title="Role" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&roleId=1&dataObjectClassName=org.kuali.rice.kim.bo.impl.RoleImpl&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li> -->
     <li><portal:portalLink displayTitle="true" title="Rule" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&ruleBaseValuesId=1103&dataObjectClassName=org.kuali.rice.kew.rule.RuleBaseValues&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
     <li><portal:portalLink displayTitle="true" title="Rule Delegation" url="${ConfigProperties.application.url}/spring/inquiry?methodToCall=start&ruleDelegationId=1641&dataObjectClassName=org.kuali.rice.kew.rule.RuleDelegation&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
  </ul>
  <strong>Financial</strong>
  <ul class="chan">
   <li><portal:portalLink displayTitle="true" title="Address Type Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.financial.bo.AddressType&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
   <li><portal:portalLink displayTitle="true" title="Ownership Type Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.financial.bo.OwnershipType&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
   <li><portal:portalLink displayTitle="true" title="Payment Term Type Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.financial.bo.PaymentTermType&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
   <li><portal:portalLink displayTitle="true" title="Vendor Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.financial.bo.VendorDetail&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
   <li><portal:portalLink displayTitle="true" title="Vendor Maintenance" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.financial.bo.VendorDetail&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
  </ul>
</div>
<channel:portalChannelBottom />
