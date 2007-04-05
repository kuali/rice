<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>
<%@ taglib tagdir="/WEB-INF/tags/portal/channel" prefix="channel" %>
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean" %>

<channel:portalChannelTop channelTitle="Labor Distribution" />
<div class="body">
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Benefits Calculation" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.BenefitsCalculation&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Benefits Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.BenefitsType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Effort Certification Help Text" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.A21HelpText&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Effort Certification Report Period" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.A21ReportPeriod&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" />
        <li><portal:portalLink displayTitle="true" title="Effort Certification Report Period Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.A21ReportPeriodStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Effort Certification Report Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.A21ReportType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Labor Object" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.LaborObject&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Position Object Benefits" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.PositionObjectBenefit&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>   
        <li><portal:portalLink displayTitle="true" title="Position Object Group" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.labor.bo.PositionObjectGroup&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    </ul>    
    </div>
<channel:portalChannelBottom />
                