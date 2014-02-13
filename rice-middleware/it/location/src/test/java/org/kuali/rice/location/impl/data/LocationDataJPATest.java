/*
 * Copyright 2005-2014 The Kuali Foundation
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
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.rice.coreservice.impl.parameter.ParameterBo;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusQueryResults;
import org.kuali.rice.location.api.campus.CampusType;
import org.kuali.rice.location.api.campus.CampusTypeQueryResults;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryQueryResults;
import org.kuali.rice.location.api.county.County;
import org.kuali.rice.location.api.county.CountyQueryResults;
import org.kuali.rice.location.api.postalcode.PostalCode;
import org.kuali.rice.location.api.postalcode.PostalCodeQueryResults;
import org.kuali.rice.location.api.services.LocationApiServiceLocator;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateQueryResults;
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
import java.util.List;
import java.util.UUID;

/**
 * Tests to confirm JPA mapping for the Location module data objects
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class LocationDataJPATest extends KRADTestCase {

    @Test
    public void testPostalCodeBoDataObject() throws Exception{
        assertTrue("PostalCodeBo is mapped in JPA", KRADServiceLocator.getDataObjectService().supports(PostalCodeBo.class));
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
        setupStateBoDataObjectAndSave();

        StateBo stateBo = KRADServiceLocator.getDataObjectService().find(StateBo.class,new StateId("IN","US"));
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

    @Test
    public void testCampusServiceImplJPA() throws Exception{
        setupCampusBoDataObjectAndSave();
        Campus campusBo = LocationApiServiceLocator.getCampusService().getCampus("SE");
        assertTrue("getCampusService retrieved correct call", campusBo != null
                && StringUtils.equals(campusBo.getCode(),"SE"));
        List<Campus> activeCampuses = LocationApiServiceLocator.getCampusService().findAllCampuses();
        assertTrue("findAllCampuses returned result", activeCampuses.size() > 0);

        CampusType campusType = LocationApiServiceLocator.getCampusService().getCampusType("C");
        assertTrue("getCampusType retrieved correctly",campusType != null
                   && StringUtils.equals(campusType.getName(), "Commuter"));

        List<CampusType> campusTypeList = LocationApiServiceLocator.getCampusService().findAllCampusTypes();
        assertTrue("findAllCampusTypes retrieved correctly",campusTypeList.size() > 0);

        CampusQueryResults results = LocationApiServiceLocator.getCampusService().
                findCampuses(QueryByCriteria.Builder.forAttribute("code", "SE").build());
        assertTrue("findCampuses retrieved correctly",results != null && results.getResults().size() == 1);

        CampusTypeQueryResults resultsType = LocationApiServiceLocator.getCampusService().
                findCampusTypes(QueryByCriteria.Builder.forAttribute("code", "C").build());
        assertTrue("findCampuses retrieved correctly",resultsType != null && resultsType.getResults().size() == 1);
    }

    @Test
    public void testCountryServiceImplJPA() throws Exception{
        setupCountryBoDataObjectAndSave();
        Country countryBo = LocationApiServiceLocator.getCountryService().getCountry("US");
        assertTrue("getCountry retrieved correct", countryBo != null &&
                StringUtils.equals(countryBo.getAlternateCode(),"USA"));
        Country country = LocationApiServiceLocator.getCountryService().getCountryByAlternateCode("USA");
        assertTrue("getCountryByAlternateCode retrieved correctly", country != null &&
                  StringUtils.equals(country.getCode(), "US"));
        List<Country> countryList = LocationApiServiceLocator.getCountryService().findAllCountries();
        assertTrue("findAllCountries retrieved correctly",countryList != null && countryList.size() == 1);

        countryList = LocationApiServiceLocator.getCountryService().findAllCountriesNotRestricted();
        assertTrue("findAllCountriesNotRestricted retrieved correctly",countryList != null && countryList.size() == 1);

        CountryQueryResults results = LocationApiServiceLocator.getCountryService().
                findCountries(QueryByCriteria.Builder.forAttribute("code", "US").build());
        assertTrue("findCountries retrieved correctly",results != null && results.getResults().size() == 1);
    }

    @Test
    public void testCountyServiceImplJPA() throws Exception{
        setupCountyBoDataObjectAndSave();

        County county = LocationApiServiceLocator.getCountyService().getCounty("US","IN","MON");
        assertTrue("getCounty retrieved correctly",county != null && StringUtils.equals("MON", county.getCode()));

        CountyQueryResults results = LocationApiServiceLocator.getCountyService().findCounties(
                    QueryByCriteria.Builder.forAttribute("code","MON").build());
        assertTrue("findCounties retrieved correctly",results != null && results.getResults().size() == 1);

        List<County> counties = LocationApiServiceLocator.getCountyService().
                            findAllCountiesInCountryAndState("US","IN");
        assertTrue("findAllCountiesInCountryAndState retrieved correctly",counties != null && counties.size() == 1);
    }

    @Test
    public void testStateServiceImplJPA() throws Exception{
        setupStateBoDataObjectAndSave();

        State state = LocationApiServiceLocator.getStateService().getState("US","IN");
        assertTrue("getState retrieved correctly", state != null && StringUtils.equals("IN",state.getCode()));
        List<State> stateList = LocationApiServiceLocator.getStateService().findAllStatesInCountry("US");
        assertTrue("findAllStatesInCountry retrieved correctly", stateList != null && stateList.size() == 1);
        stateList = LocationApiServiceLocator.getStateService().findAllStatesInCountryByAltCode("USA");
        assertTrue("findAllStatesInCountryByAltCode retrieved correctly", stateList != null && stateList.size() == 1);
        StateQueryResults results = LocationApiServiceLocator.getStateService().findStates(
                        QueryByCriteria.Builder.forAttribute("code","IN").build());
        assertTrue("findStates retrieved correctly",results != null && results.getResults().size() == 1);
    }

    @Test
    public void testPostalCodeServiceImplJPA() throws Exception{
        setupPostalCodeBoDataObjectAndSave();

        PostalCode postalCode = LocationApiServiceLocator.getPostalCodeService().getPostalCode("US","47203");
        assertTrue("getPostalCode retrieved correctly",postalCode != null &&
                                StringUtils.equals(postalCode.getCode(),"47203"));
        List<PostalCode> postalCodeList = LocationApiServiceLocator.getPostalCodeService().
                findAllPostalCodesInCountry("US");
        assertTrue("findAllPostalCodesInCountry retrieved correctly",
                postalCodeList != null && postalCodeList.size() == 1);
        PostalCodeQueryResults results = LocationApiServiceLocator.getPostalCodeService().
                findPostalCodes(QueryByCriteria.Builder.forAttribute("code", "47203").build());
        assertTrue("findPostalCodes retrieved correctly",results != null && results.getResults().size()==1);
    }

    private void setupPostalCodeBoDataObjectAndSave(){
        setupCountyBoDataObjectAndSave();
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
        setupStateBoDataObjectAndSave();
        CountyBo countyBo = new CountyBo();
        countyBo.setActive(true);
        countyBo.setCode("MON");
        countyBo.setCountryCode("US");
        countyBo.setName("Monroe");
        countyBo.setStateCode("IN");

        KRADServiceLocator.getDataObjectService().save(countyBo);
    }

    private void setupStateBoDataObjectAndSave(){
        setupCountryBoDataObjectAndSave();
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
        setupCampusTypeBoDataObjectAndSave();
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
