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

<channel:portalChannelTop channelTitle="Vendor" />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Address Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.AddressType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Contact Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ContactType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Contract Manager" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ContractManager&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Ownership Category" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.OwnershipCategory&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Ownership Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.OwnershipType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Payment Term Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.PaymentTermType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Phone Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.PhoneType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Purchase Order Cost Source" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.PurchaseOrderCostSource&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Shipping Payment Terms" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ShippingPaymentTerms&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Shipping Special Condition" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ShippingSpecialCondition&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Shipping Title" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.ShippingTitle&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Supplier Diversity" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.SupplierDiversity&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Vendor Inactive Reason" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.VendorInactiveReason&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
        <li><portal:portalLink displayTitle="true" title="Vendor Type" url="lookup.do?methodToCall=start&businessObjectClassName=org.kuali.module.vendor.bo.VendorType&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>     
    </ul>
<channel:portalChannelBottom />