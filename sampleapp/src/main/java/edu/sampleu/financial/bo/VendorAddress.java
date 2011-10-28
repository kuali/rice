/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package edu.sampleu.financial.bo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.services.LocationApiServiceLocator;
import org.kuali.rice.location.api.state.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Address to be associated with a particular Vendor.
 */
public class VendorAddress extends PersistableBusinessObjectBase implements MutableInactivatable {
    private static Logger LOG = Logger.getLogger(VendorAddress.class);

    private Integer vendorAddressGeneratedIdentifier;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorAddressTypeCode;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorZipCode;
    private String vendorCountryCode;
    private String vendorAttentionName;
    private String vendorAddressInternationalProvinceName;
    private String vendorAddressEmailAddress;
    private String vendorBusinessToBusinessUrlAddress;
    private String vendorFaxNumber;
    private boolean vendorDefaultAddressIndicator;
    private boolean active;

    private List<VendorDefaultAddress> vendorDefaultAddresses;

    private VendorDetail vendorDetail;
    private AddressType vendorAddressType;
    private State vendorState;
    private Country vendorCountry;

    /**
     * Default constructor.
     */
    public VendorAddress() {
        vendorDefaultAddresses = new ArrayList();
    }

    public Integer getVendorAddressGeneratedIdentifier() {

        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    public String getVendorAddressEmailAddress() {
        return vendorAddressEmailAddress;
    }

    public void setVendorAddressEmailAddress(String vendorAddressEmailAddress) {
        this.vendorAddressEmailAddress = vendorAddressEmailAddress;
    }

    public String getVendorAddressTypeCode() {
        return vendorAddressTypeCode;
    }

    public void setVendorAddressTypeCode(String vendorAddressTypeCode) {
        this.vendorAddressTypeCode = vendorAddressTypeCode;
    }

    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    public String getVendorCityName() {
        return vendorCityName;
    }

    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    public String getVendorStateCode() {
        return vendorStateCode;
    }

    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    public String getVendorZipCode() {
        return vendorZipCode;
    }

    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }

    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    public String getVendorAttentionName() {
        return vendorAttentionName;
    }

    public void setVendorAttentionName(String vendorAttentionName) {
        this.vendorAttentionName = vendorAttentionName;
    }

    public String getVendorBusinessToBusinessUrlAddress() {
        return vendorBusinessToBusinessUrlAddress;
    }

    public void setVendorBusinessToBusinessUrlAddress(String vendorBusinessToBusinessUrlAddress) {
        this.vendorBusinessToBusinessUrlAddress = vendorBusinessToBusinessUrlAddress;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public AddressType getVendorAddressType() {
        return vendorAddressType;
    }

    public void setVendorAddressType(AddressType vendorAddressType) {
        this.vendorAddressType = vendorAddressType;
    }

    public State getVendorState() {
        if ((vendorState == null) || (StringUtils.equals(vendorState.getCode(), vendorStateCode))) {
            vendorState = LocationApiServiceLocator.getStateService().getState(vendorCountryCode, vendorStateCode);
        }

        return vendorState;
    }

    public void setVendorState(State vendorState) {
        this.vendorState = vendorState;
    }

    public Country getVendorCountry() {
        String postalCountryCode = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(
                KRADConstants.KRAD_NAMESPACE,
                KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KRADConstants.SystemGroupParameterNames.DEFAULT_COUNTRY);
        vendorCountry = LocationApiServiceLocator.getCountryService().getCountry(postalCountryCode);

        return vendorCountry;
    }

    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public String getVendorFaxNumber() {
        return vendorFaxNumber;
    }

    public void setVendorFaxNumber(String vendorFaxNumber) {
        this.vendorFaxNumber = vendorFaxNumber;
    }

    public boolean isVendorDefaultAddressIndicator() {
        return vendorDefaultAddressIndicator;
    }

    public void setVendorDefaultAddressIndicator(boolean vendorDefaultAddressIndicator) {
        this.vendorDefaultAddressIndicator = vendorDefaultAddressIndicator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<VendorDefaultAddress> getVendorDefaultAddresses() {
        return this.vendorDefaultAddresses;
    }

    public void setVendorDefaultAddresses(List<VendorDefaultAddress> vendorDefaultAddresses) {
        this.vendorDefaultAddresses = vendorDefaultAddresses;
    }
}
