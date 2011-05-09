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

<channel:portalChannelTop channelTitle="KRMS Rules" />
<div class="body">
  <strong>Maintenance Docs</strong>
  <ul class="chan">
    <li>todo<!-- portal:portalLink displayTitle="true" title="Context Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.ContextBo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" / --></li>
    <li><portal:portalLink displayTitle="true" title="KRMS Editor Maintenance" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.EditorDocument&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li> 
    <li><portal:portalLink displayTitle="true" title="Agenda Maintenance" url="${ConfigProperties.application.url}/spring/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.AgendaBo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true"/></li> 
  </ul>
  <strong>Lookups</strong>
  <ul class="chan">
    <li><portal:portalLink displayTitle="true" title="Context Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.ContextBo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li><portal:portalLink displayTitle="true" title="Agenda Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.AgendaDefinitionBo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li><portal:portalLink displayTitle="true" title="Agenda Item Lookup" url="${ConfigProperties.application.url}/spring/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.repository.AgendaItemBo&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
  </ul>
  <strong>Views</strong>
  <ul class="chan">
    <li><portal:portalLink displayTitle="true" title="Agenda Editor" url="${ConfigProperties.application.url}/spring/krmsEditor?viewId=EditorView&methodToCall=start"/></li> 
  </ul>
</div>
<channel:portalChannelBottom />
