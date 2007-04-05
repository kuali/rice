<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<kul:tab tabTitle="Employee Lookup" defaultOpen="true"
	tabErrorKey="${Constants.EMPLOYEE_LOOKUP_ERRORS}">
	<div class="tab-container" align=center>
	Select Employee:
		<kul:lookup
			boClassName="org.kuali.core.bo.user.UniversalUser"
			fieldConversions="personPayrollIdentifier:document.emplid" />

		Employee Id:
        <kul:htmlControlAttribute property="document.emplid" attributeEntry="${UniversalUser.personUniversalIdentifier}" readOnly="true"/>        
								
		<p>
		<div class="h2-container"></div>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="view/edit pending entries">

		</table>
	</div>
</kul:tab>