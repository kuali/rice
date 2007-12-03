<%--
 Copyright 2005-2006 The Kuali Foundation.
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="travelAttributes" value="${DataDictionary.TravelRequest.attributes}" />
<c:set var="accountAttributes" value="${DataDictionary.TravelAccount.attributes}" />
<kul:documentPage 
	showDocumentInfo="true"
	htmlFormAction="travelDocument2"
	documentTypeName="TravelRequest" 
	renderMultipart="true"
	showTabButtons="true" 
	auditCount="0">

	<SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
    </SCRIPT>

 	<kul:hiddenDocumentFields isTransactionalDocument="false" isFinancialDocument="true" excludePostingYear="true"/>

	<kul:documentOverview editingMode="${KualiForm.editingMode}" /> 
	<kul:tab tabTitle="Travel Stuff" defaultOpen="true" tabErrorKey="bs">
		<div class="tab-container" align="center">
		<div class="h2-container">
		    <h2>Travel Request</h2>
		</div>
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
		    <tr>
        		<td colspan="4">
            		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
				 		<tr>
				 		<kul:htmlAttributeHeaderCell labelFor="document.traveler" attributeEntry="${travelAttributes.traveler}" align="left" />
				 		<td><kul:htmlControlAttribute property="document.traveler" attributeEntry="${travelAttributes.traveler}" readOnly="false" /></td>
				 		</tr>
				 		<tr>
				 		<kul:htmlAttributeHeaderCell labelFor="document.origin" attributeEntry="${travelAttributes.origin}" align="left" />
				 		<td><kul:htmlControlAttribute property="document.origin" attributeEntry="${travelAttributes.origin}" readOnly="false" /></td>
				 		</tr>
				 		<tr>
				 		<kul:htmlAttributeHeaderCell labelFor="document.destination" attributeEntry="${travelAttributes.destination}" align="left" />
				 		<td><kul:htmlControlAttribute property="document.destination" attributeEntry="${travelAttributes.destination}" readOnly="false" /></td>
				 		</tr>
				 		<tr>
				 		<kul:htmlAttributeHeaderCell labelFor="document.requestType" attributeEntry="${travelAttributes.requestType}" align="left" />
				 		<td><kul:htmlControlAttribute property="document.requestType" attributeEntry="${travelAttributes.requestType}" readOnly="false" /></td>
				 		</tr>
						<tr>
						<th align="left">
				 		&nbsp;&nbsp;* Travel Account  
				 		</th>
				 		<td>
				 		<kul:htmlControlAttribute property="travelAccount.number" attributeEntry="${accountAttributes.number}" readOnly="false" />
                        <kul:lookup boClassName="edu.sampleu.travel.bo.TravelAccount" fieldConversions="number:travelAccount.number" />
                        <kul:directInquiry boClassName="edu.sampleu.travel.bo.TravelAccount" inquiryParameters="travelAccount.number:number" />
						<html:image property="methodToCall.insertAccount" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item" title="Insert an Item" styleClass="tinybutton"/>
                        </td>
				 		</tr>
				 		<logic:iterate id="travAcct" name="KualiForm" property="document.travelAccounts" indexId="ctr">
					 		<tr>
					 			<th>&nbsp;</th>
					 			<td class="datacell">
					 			<kul:htmlControlAttribute attributeEntry="${accountAttributes.number}" property="document.travelAccount[${ctr}].number" readOnly="true"/>
					 			&nbsp;&nbsp;-&nbsp;&nbsp;
					 			<kul:htmlControlAttribute attributeEntry="${accountAttributes.name}" property="document.travelAccount[${ctr}].name" readOnly="true"/>
					 			</td>
					 		</tr>
				 		</logic:iterate>
				 		</tr>
			 		</table>
			 	</td>
			 </tr>
		</table>			 
		</div>
	</kul:tab>
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls transactionalDocument="false" />

</kul:documentPage>
