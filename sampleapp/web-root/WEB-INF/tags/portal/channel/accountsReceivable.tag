<%--
 Copyright 2006 The Kuali Foundation.
 
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

<channel:portalChannelTop channelTitle="Accounts Receivable" />
<div class="body">
    <ul class="chan">
	      <%--
	          TODO: these will eventually be portal links like the example below:
	               <portal:portalLink displayTitle="true" title="Application" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.gl.bo.CashBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" />
	      --%>
	      <li>Application</li>
	      <li>Cash Control</li>
	      <li>Credit Memo</li>
	      <li>Customer</li>
	      <li>Invoice</li>
	      <li>Organization Accounting Defaults</li>
	      <li>Organization Options</li>
	</ul>
</div>
<channel:portalChannelBottom />
