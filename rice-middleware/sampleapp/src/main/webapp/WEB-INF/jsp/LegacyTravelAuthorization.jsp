<%--

    Copyright 2005-2014 The Kuali Foundation

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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="travelAttributes" value="${DataDictionary.LegacyTravelAuthorizationKns.attributes}"/>

<kul:documentPage showDocumentInfo="true"
                  documentTypeName="LegacyTravelAuthorizationKns"
                  htmlFormAction="legacyTravelAuthorization"
                  renderMultipart="true"
                  showTabButtons="true">

  <kul:hiddenDocumentFields />
  <kul:documentOverview editingMode="${KualiForm.editingMode}" />

  <kul:tab tabTitle="Travel Authorization" defaultOpen="true" tabErrorKey="document.traveler,document.origin,document.destination,document.requestType,travelAccount.number">
    <div class="tab-container" align="center">
      <div class="h2-container">
        <h2>Travel Request</h2>
      </div>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
        <tr>
          <td colspan="4">
            <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
              <tr>
                <kul:htmlAttributeHeaderCell labelFor="document.tripBegin" attributeEntry="${travelAttributes.tripBegin}" align="left" />
                <td><kul:htmlControlAttribute property="document.tripBegin" attributeEntry="${travelAttributes.tripBegin}" readOnly="${readOnly}" /></td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell labelFor="document.tripEnd" attributeEntry="${travelAttributes.tripEnd}" align="left" />
                <td><kul:htmlControlAttribute property="document.tripEnd" attributeEntry="${travelAttributes.tripEnd}" readOnly="${readOnly}" /></td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell labelFor="document.tripDescription" attributeEntry="${travelAttributes.tripDescription}" align="left" />
                <td><kul:htmlControlAttribute property="document.tripDescription" attributeEntry="${travelAttributes.tripDescription}" readOnly="${readOnly}" /></td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell labelFor="document.tripDestinationId" attributeEntry="${travelAttributes.tripDestinationId}" align="left" />
                <td><kul:htmlControlAttribute property="document.tripDestinationId" attributeEntry="${travelAttributes.tripDestinationId}" readOnly="${readOnly}" /></td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell labelFor="document.travelerDetailId" attributeEntry="${travelAttributes.travelerDetailId}" align="left" />
                <td><kul:htmlControlAttribute property="document.travelerDetailId" attributeEntry="${travelAttributes.travelerDetailId}" readOnly="${readOnly}" /></td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell labelFor="document.travelTypeCode" attributeEntry="${travelAttributes.travelTypeCode}" align="left" />
                <td><kul:htmlControlAttribute property="document.travelTypeCode" attributeEntry="${travelAttributes.travelTypeCode}" readOnly="${readOnly}" /></td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell labelFor="document.expenseLimit" attributeEntry="${travelAttributes.expenseLimit}" align="left" />
                <td><kul:htmlControlAttribute property="document.expenseLimit" attributeEntry="${travelAttributes.expenseLimit}" readOnly="${readOnly}" /></td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell labelFor="document.cellPhoneNumber" attributeEntry="${travelAttributes.cellPhoneNumber}" align="left" />
                <td><kul:htmlControlAttribute property="document.cellPhoneNumber" attributeEntry="${travelAttributes.cellPhoneNumber}" readOnly="${readOnly}" /></td>
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

  <kul:documentControls transactionalDocument="true" />

</kul:documentPage>
