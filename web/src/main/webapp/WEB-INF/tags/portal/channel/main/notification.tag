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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>


<channel:portalChannelTop channelTitle="Notification" />
<div class="body">
 	
    <ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Notification Search" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.AccountBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Channel Subscriptions" url="${KFSConstants.GL_ACCOUNT_BALANCE_BY_CONSOLIDATION_LOOKUP_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.AccountBalanceByConsolidation&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	   	<li><portal:portalLink displayTitle="true" title="Delivery Types" url="${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.CashBalance&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	    
    </ul>
    
 	
</div>
<channel:portalChannelBottom />
