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
<%
/*
* User Preferences
* Quicklinks
* Routing Rules
* Routing Rule Delegations
* Routing and Identity Management Document Type Hierarchy
* Document Type
*/
%>
<channel:portalChannelTop channelTitle="Workflow" />
<div class="body">
	
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="User Preferences" url="arCashControlDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CTRL" /></li>
        <li><portal:portalLink displayTitle="true" title="Quicklinks" url="arCustomerCreditMemoDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CRM" /></li>
        <li><portal:portalLink displayTitle="true" title="Routing Rules" url="arCustomerInvoiceDocument.do?methodToCall=docHandler&command=initiate&docTypeName=INV" /></li>
        <li><portal:portalLink displayTitle="true" title="Routing Rules Deligation" url="arCustomerInvoiceWriteoffDocument.do?methodToCall=docHandler&command=initiate&docTypeName=INVW" /></li>
		<li><portal:portalLink displayTitle="true" title="Routing and Identity Management Document Type Hierarchy" url="arCustomerInvoiceWriteoffLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Document Type" url="arPaymentApplicationDocument.do?methodToCall=docHandler&command=initiate&docTypeName=APP" /></li>
    </ul>

    
</div>
<channel:portalChannelBottom />
