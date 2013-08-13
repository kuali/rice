/*
 * Copyright 2006-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.location.impl.data;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.location.impl.campus.CampusBo;
import org.kuali.rice.location.impl.campus.CampusTypeBo;
import org.kuali.rice.location.impl.country.CountryBo;
import org.kuali.rice.location.impl.county.CountyBo;
import org.kuali.rice.location.impl.county.CountyId;
import org.kuali.rice.location.impl.postalcode.PostalCodeBo;
import org.kuali.rice.location.impl.postalcode.PostalCodeId;
import org.kuali.rice.location.impl.state.StateBo;
import org.kuali.rice.location.impl.state.StateId;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.*;

import javax.persistence.EntityManagerFactory;
import javax.validation.constraints.AssertTrue;
import java.util.UUID;

/**
 * Tests to confirm JPA mapping for the Location module data objects
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class LocationDataJPATest extends KRADTestCase {

    @Test
    public void testPostalCodeBoDataObject() throws Exception{
        assertTrue("PostalCodeBo is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(PostalCodeBo.class));
        setupCountryBoDataObjectAndSave();
        setupStateBoDataObjectAndSave();
        setupCountyBoDataObjectAndSave();
        setupPostalCodeBoDataObjectAndSave();

        PostalCodeBo postalCodeBo = KRADServiceLocator.getDataObjectService().find(
                                    PostalCodeBo.class,new PostalCodeId("US","47203"));
        assertTrue("PostalCode BO fetched after save", postalCodeBo != null && StringUtils.equals(postalCodeBo.getCode(),"47203"));
        assertTrue("PostalCode BO fetched Country BO correctly",postalCodeBo.getCountry()!=null
                        && StringUtils.equals(postalCodeBo.getCountry().getAlternateCode(),"USA"));
        assertTrue("PostalCode BO fetched State BO correctly",postalCodeBo.getState() != null
                        && StringUtils.equals(postalCodeBo.getState().getCode(),"IN"));
        assertTrue("PostalCode BO fetched County BO correctly",postalCodeBo.getCounty() != null
                        && StringUtils.equals(postalCodeBo.getCounty().getCode(),"MON"));
    }

    @Test
    public void testCountyBoDataObject() throws Exception{
        assertTrue("CountyBO is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(CountryBo.class));
        setupCountryBoDataObjectAndSave();
        setupStateBoDataObjectAndSave();
        setupCountyBoDataObjectAndSave();

        CountyBo countyBo = KRADServiceLocator.getDataObjectService().find(CountyBo.class,new CountyId("MON","US","IN"));
        assertTrue("County BO fetched after save",countyBo != null && StringUtils.equals(countyBo.getName(),"Monroe"));
        assertTrue("County Bo fetched State Bo correctly",countyBo.getState() != null
                                        && StringUtils.equals(countyBo.getState().getName(),"Indiana"));
        assertTrue("County Bo fetched Country Bo correctly",countyBo.getCountry() != null
                && StringUtils.equals(countyBo.getCountry().getAlternateCode(), "USA"));

    }

    @Test
    public void testStateBoDataObject() throws Exception{
        assertTrue("StateBO is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(StateBo.class));
        setupCountryBoDataObjectAndSave();
        setupStateBoDataObjectAndSave();

        StateBo stateBo = KRADServiceLocator.getDataObjectService().find(StateBo.class,new StateId("US","IN"));
        assertTrue("State BO fetched after save", stateBo != null && StringUtils.equals(stateBo.getName(),"Indiana"));
        assertTrue("State BO fetched Country BO correctly",stateBo.getCountry() != null && StringUtils.equals(stateBo.getCountry().getAlternateCode(),"USA"));
    }


    @Test
    public void testCountryBoDataObject() throws Exception{
        assertTrue("CountryBO is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(CountryBo.class));
        setupCountryBoDataObjectAndSave();

        CountryBo countryBo = KRADServiceLocator.getDataObjectService().find(CountryBo.class,"US");
        assertTrue("Country BO fetched after save", countryBo != null &&
                                StringUtils.equals(countryBo.getAlternateCode(),"USA"));
    }

    @Test
    public void testCampusBoDataObject() throws Exception{
       assertTrue("CampusBO is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(CampusBo.class));
       setupCampusTypeBoDataObjectAndSave();
       setupCampusBoDataObjectAndSave();

       CampusBo campusBo = KRADServiceLocator.getDataObjectService().find(CampusBo.class,"SE");
       assertTrue("Campus BO fetched after save", campusBo != null && StringUtils.equals(campusBo.getName(),
               "SouthEast"));
       assertTrue("Campus Type Bo fetched from Campus BO relationship", campusBo.getCampusType() != null
                            && StringUtils.equals(campusBo.getCampusType().getName(),"Commuter"));
    }

    @Test
    public void testCampusTypeBoDataObject() throws Exception{
        assertTrue("CampusTypeBo is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(
                CampusTypeBo.class));
        setupCampusTypeBoDataObjectAndSave();

        CampusTypeBo campusTypeBoFetched = KRADServiceLocator.getDataObjectService().find(CampusTypeBo.class,"C");
        assertTrue("Campus Type BO refetched after save", campusTypeBoFetched != null &&
                            campusTypeBoFetched.getName().equals("Commuter"));
    }

    private void setupPostalCodeBoDataObjectAndSave(){
        PostalCodeBo postalCodeBo = new PostalCodeBo();
        postalCodeBo.setActive(true);
        postalCodeBo.setCityName("Bloomington");
        postalCodeBo.setCode("47203");
        postalCodeBo.setCountryCode("US");
        postalCodeBo.setCountyCode("MON");
        postalCodeBo.setStateCode("IN");

        KRADServiceLocator.getDataObjectService().save(postalCodeBo);
    }

    private void setupCountyBoDataObjectAndSave(){
        CountyBo countyBo = new CountyBo();
        countyBo.setActive(true);
        countyBo.setCode("MON");
        countyBo.setCountryCode("US");
        countyBo.setName("Monroe");
        countyBo.setStateCode("IN");

        KRADServiceLocator.getDataObjectService().save(countyBo);
    }

    private void setupStateBoDataObjectAndSave(){
        StateBo stateBo = new StateBo();
        stateBo.setActive(true);
        stateBo.setCode("IN");
        stateBo.setCountryCode("US");
        stateBo.setName("Indiana");

        KRADServiceLocator.getDataObjectService().save(stateBo);
    }

    private void setupCountryBoDataObjectAndSave(){
        CountryBo countryBo = new CountryBo();
        countryBo.setActive(true);
        countryBo.setAlternateCode("USA");
        countryBo.setCode("US");
        countryBo.setName("United States of America");
        countryBo.setRestricted(false);

        KRADServiceLocator.getDataObjectService().save(countryBo);
    }

    private void setupCampusBoDataObjectAndSave(){
        CampusBo campusBo = new CampusBo();
        campusBo.setActive(true);
        campusBo.setCampusTypeCode("C");
        campusBo.setCode("SE");
        campusBo.setName("SouthEast");
        campusBo.setShortName("SouthE");

        KRADServiceLocator.getDataObjectService().save(campusBo);
    }

    private void setupCampusTypeBoDataObjectAndSave(){
        CampusTypeBo campusTypeBo = new CampusTypeBo();
        campusTypeBo.setActive(true);
        campusTypeBo.setCode("C");
        campusTypeBo.setName("Commuter");

        KRADServiceLocator.getDataObjectService().save(campusTypeBo);
    }



}
