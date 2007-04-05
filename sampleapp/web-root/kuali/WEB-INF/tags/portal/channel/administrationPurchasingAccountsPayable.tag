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

<channel:portalChannelTop channelTitle="Purchasing/Accounts Payable" />
    <ul class="chan">
    	<li><portal:portalLink displayTitle="true" title="Address Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.AddressType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    	<li><portal:portalLink displayTitle="true" title="Auto Approve Exclude" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.AutoApproveExclude&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Billing Address" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.BillingAddress&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Campus Parameter" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.CampusParameter&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    	<li><portal:portalLink displayTitle="true" title="Capital Asset Transaction Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.CapitalAssetTransactionType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
    	<li><portal:portalLink displayTitle="true" title="Carrier" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.Carrier&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Contact Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.ContactType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Contract Manager" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.ContractManager&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Credit Memo Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.CreditMemoStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Delivery Required Date Reason" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.DeliveryRequiredDateReason&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>             
        <li><portal:portalLink displayTitle="true" title="Funding Source" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.FundingSource&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Item Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.ItemType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Organization Parameter" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.OrganizationParameter&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Ownership Category" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.OwnershipCategory&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Ownership Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.OwnershipType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Payment Request Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PaymentRequestStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Payment Term Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PaymentTermType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Phone Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PhoneType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Purchase Order Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Purchase Order Cost Source" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderCostSource&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Purchase Order Contract Language" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderContractLanguage&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Purchase Order Quote Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderQuoteStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Purchase Order Quote Lists" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderQuoteList&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Purchase Order Vendor Choice" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderVendorChoice&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Recurring Payment Frequency" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RecurringPaymentFrequency&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Purchase Order Transmission Method" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.PurchaseOrderTransmissionMethod&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Recurring Payment Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RecurringPaymentType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Requisition Source" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RequisitionSource&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Requisition Status" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RequisitionStatus&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Restricted Material" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.RestrictedMaterial&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>   
        <li><portal:portalLink displayTitle="true" title="Shipping Payment Terms" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.ShippingPaymentTerms&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Shipping Special Condition" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.ShippingSpecialCondition&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
		<li><portal:portalLink displayTitle="true" title="Shipping Title" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.ShippingTitle&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Supplier Diversity" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.SupplierDiversity&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Vendor Inactive Reason" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.VendorInactiveReason&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
        <li><portal:portalLink displayTitle="true" title="Vendor Stipulation" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.VendorStipulation&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Vendor Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.purap.bo.VendorType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
    </ul>
<channel:portalChannelBottom />