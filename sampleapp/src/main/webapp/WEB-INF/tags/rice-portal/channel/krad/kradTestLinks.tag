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
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-StandardLayout-KNS&methodToCall=start" />Standard Layout Demo</a></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-ValidationLayout-KNS&methodToCall=start" />Validation Framework Demo</a></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=Demo-ValidationServerSide-KNS&methodToCall=start" />ServerSide Constraint Validation Demo</a></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uilayouttest?viewId=UifLayoutView_KNS&methodToCall=start" />Uif Layout Test</a></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView2_KNS&methodToCall=foo" />Incident Report</a></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/configuration-test-view-uif-controller?viewId=ConfigurationTestView_KNS&methodToCall=start" />Configuration Test View</a></li>
	   <%--<li><portal:portalLink displayTitle="true" title="Test View 1 (old)" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView1_KNS&methodToCall=start" /></li>--%>
     <%--<li><portal:portalLink displayTitle="true" title="Test View 2 (old)" url="${ConfigProperties.application.url}/kr-krad/uitest?viewId=Travel-testView2_KNS&methodToCall=start" /></li>--%>
     <%--<li><portal:portalLink displayTitle="true" title="Test Open Authorization" url="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=TestOpenAuthView&methodToCall=start" /></li>--%>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/dialog-configuration-test?viewId=DialogTestView_KNS&methodToCall=start" />Dialog Test View</a></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=ConfigurationTestView-Collections_KNS&methodToCall=start"/>Collections Configuration Test View</a></li>
     <li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/uicomponents?viewId=RichMessagesView_KNS&methodToCall=start"/>Rich Messages</a></li>
     <%--<li><a class="portal_link" target="_blank" href="${ConfigProperties.application.url}/kr-krad/collegeapp?viewId=Training-CollegeApplicationView_KNS&methodToCall=start"/>Training - Student College Application</a></li>--%>
   </ul>

  <strong>Lookup Samples</strong>
  <ul class="chan">
    <li><portal:portalLink displayTitle="true" title="Lookup Sample"
          url="${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start&viewId=LookupSampleView&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li>
    <li><portal:portalLink displayTitle="true" title="Multi-Value Lookup Sample"
          url="${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start&viewId=LookupSampleView&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&multipleValuesSelect=true"/></li>
    <li><portal:portalLink displayTitle="true" title="Lookup sample with no specified results limit"
          url="${ConfigProperties.application.url}/kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewNoResultsLimit&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&multipleValuesSelect=true"/></li>
  </ul>

</div>
<channel:portalChannelBottom />
