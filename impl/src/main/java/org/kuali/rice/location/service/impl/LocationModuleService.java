/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.location.service.impl;

import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.framework.group.GroupEbo;
import org.kuali.rice.kim.framework.role.RoleEbo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.service.impl.ModuleServiceBase;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.county.County;
import org.kuali.rice.location.api.county.CountyService;
import org.kuali.rice.location.api.postalcode.PostalCode;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.api.services.LocationApiServiceLocator;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.county.CountyEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;
import org.kuali.rice.location.impl.campus.CampusBo;
import org.kuali.rice.location.impl.country.CountryBo;
import org.kuali.rice.location.impl.county.CountyBo;
import org.kuali.rice.location.impl.postalcode.PostalCodeBo;
import org.kuali.rice.location.impl.state.StateBo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

public class LocationModuleService extends ModuleServiceBase {

    private CampusService campusService;
    private StateService stateService;
    private CountryService countryService;
    private CountyService countyService;
    private PostalCodeService postalCodeService;


    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        if(CampusEbo.class.isAssignableFrom(businessObjectClass)){
            if(fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.CODE)){
                Campus campus = getCampusService().getCampus((String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.CODE));
                return (T) CampusEbo.from(campus);
            }
        } else if(StateEbo.class.isAssignableFrom(businessObjectClass)){
            if(fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE)
                    && fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.STATE_CODE)) {
                State state = getStateService().getState((String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.COUNTRY_CODE), (String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.STATE_CODE));
                return (T) StateEbo.from(state);
            }
        } else if(CountryEbo.class.isAssignableFrom(businessObjectClass)){
            if(fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.CODE)) {
                Country country = getCountryService().getCountry((String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.CODE));
                return (T) CountryEbo.from(country);
            }
        } else if (CountyEbo.class.isAssignableFrom(businessObjectClass)) {
            if (fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.CODE)
                    && fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE)
                    && fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.STATE_CODE)) {
                County county = getCountyService().getCounty((String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.COUNTRY_CODE), (String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.STATE_CODE), (String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.CODE));
                return (T)CountyEbo.from(county);
            }
        } else if (PostalCodeEbo.class.isAssignableFrom(businessObjectClass)) {
            if (fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.CODE)
                    && fieldValues.containsKey(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE)) {
                PostalCode postalCode = getPostalCodeService().getPostalCode((String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.COUNTRY_CODE), (String) fieldValues.get(
                        LocationConstants.PrimaryKeyConstants.CODE));
                return (T)PostalCodeEbo.from(postalCode);
            }
        }
        // otherwise, use the default implementation
        return super.getExternalizableBusinessObject( businessObjectClass, fieldValues );
    }

    /**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#getExternalizableBusinessObjectsList(java.lang.Class, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
			Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues) {

		if ( StateEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<StateBo> states = getBusinessObjectService().findMatching(StateBo.class, fieldValues);
            List<StateEbo> stateEbos = new ArrayList<StateEbo>(states.size());
            for (StateBo state : states) {
                stateEbos.add(StateEbo.from(State.Builder.create(state).build()));
            }
            return (List<T>)stateEbos;
		} else if ( CampusEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<CampusBo> campuses = getBusinessObjectService().findMatching(CampusBo.class, fieldValues);
            List<CampusEbo> campusEbos = new ArrayList<CampusEbo>(campuses.size());
            for (CampusBo campus : campuses) {
                campusEbos.add(CampusEbo.from(Campus.Builder.create(campus).build()));
            }
            return (List<T>)campusEbos;
		} else if ( CountryEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<CountryBo> countries = getBusinessObjectService().findMatching(CountryBo.class, fieldValues);
            List<CountryEbo> countryEbos = new ArrayList<CountryEbo>(countries.size());
            for (CountryBo country : countries) {
                countryEbos.add(CountryEbo.from(Country.Builder.create(country).build()));
            }
            return (List<T>)countryEbos;
		} else if ( CountyEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<CountyBo> counties = getBusinessObjectService().findMatching(CountyBo.class, fieldValues);
            List<CountyEbo> countyEbos = new ArrayList<CountyEbo>(counties.size());
            for (CountyBo county : counties) {
                countyEbos.add(CountyEbo.from(County.Builder.create(county).build()));
            }
            return (List<T>)countyEbos;
		} else if ( PostalCodeEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<PostalCodeBo> postalCodes = getBusinessObjectService().findMatching(PostalCodeBo.class, fieldValues);
            List<PostalCodeEbo> postalCodeEbos = new ArrayList<PostalCodeEbo>(postalCodes.size());
            for (PostalCodeBo postalCode : postalCodes) {
                postalCodeEbos.add(PostalCodeEbo.from(PostalCode.Builder.create(postalCode).build()));
            }
            return (List<T>)postalCodeEbos;
		}
		// otherwise, use the default implementation
		return super.getExternalizableBusinessObjectsList( externalizableBusinessObjectClass, fieldValues );
	}

	/***
	 * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObjectsListForLookup(java.lang.Class, java.util.Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(
			Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {

        Map<String, String> searchCriteria = new HashMap<String, String>();
			for (Map.Entry<String, Object> fieldValue : fieldValues.entrySet()) {
				if (fieldValue.getValue() != null) {
					searchCriteria.put(fieldValue.getKey(), fieldValue.getValue().toString());
				}
				else {
					searchCriteria.put(fieldValue.getKey(), null);
				}
			}
		// for Person objects (which are not real PersistableBOs) pull them through the person service
		if ( StateEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<StateBo> states = getLookupService().findCollectionBySearchHelper(StateBo.class, searchCriteria,
                    unbounded);
            List<StateEbo> stateEbos = new ArrayList<StateEbo>(states.size());
            for (StateBo state : states) {
                stateEbos.add(StateEbo.from(State.Builder.create(state).build()));
            }
            return (List<T>)stateEbos;
		} else if ( CampusEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<CampusBo> campuses = getLookupService().findCollectionBySearchHelper(CampusBo.class,
                    searchCriteria, unbounded);
            List<CampusEbo> campusEbos = new ArrayList<CampusEbo>(campuses.size());
            for (CampusBo campus : campuses) {
                campusEbos.add(CampusEbo.from(Campus.Builder.create(campus).build()));
            }
            return (List<T>)campusEbos;
		} else if ( CountryEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<CountryBo> countries = getLookupService().findCollectionBySearchHelper(CountryBo.class,
                    searchCriteria, unbounded);
            List<CountryEbo> countryEbos = new ArrayList<CountryEbo>(countries.size());
            for (CountryBo country : countries) {
                countryEbos.add(CountryEbo.from(Country.Builder.create(country).build()));
            }
            return (List<T>)countryEbos;
		} else if ( CountyEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<CountyBo> counties = getLookupService().findCollectionBySearchHelper(CountyBo.class,
                    searchCriteria, unbounded);
            List<CountyEbo> countyEbos = new ArrayList<CountyEbo>(counties.size());
            for (CountyBo county : counties) {
                countyEbos.add(CountyEbo.from(County.Builder.create(county).build()));
            }
            return (List<T>)countyEbos;
		} else if ( PostalCodeEbo.class.isAssignableFrom( externalizableBusinessObjectClass ) ) {
            Collection<PostalCodeBo> postalCodes = getLookupService().findCollectionBySearchHelper(PostalCodeBo.class,
                    searchCriteria, unbounded);
            List<PostalCodeEbo> postalCodeEbos = new ArrayList<PostalCodeEbo>(postalCodes.size());
            for (PostalCodeBo postalCode : postalCodes) {
                postalCodeEbos.add(PostalCodeEbo.from(PostalCode.Builder.create(postalCode).build()));
            }
            return (List<T>)postalCodeEbos;
		}
		// otherwise, use the default implementation
		return super.getExternalizableBusinessObjectsListForLookup(externalizableBusinessObjectClass, fieldValues, unbounded);
	}


    protected CampusService getCampusService() {
        if (campusService == null) {
            campusService = LocationApiServiceLocator.getCampusService();
        }
        return campusService;
    }

    protected StateService getStateService() {
        if (stateService == null) {
            stateService = LocationApiServiceLocator.getStateService();
        }
        return stateService;
    }

    protected CountryService getCountryService() {
        if (countryService == null) {
            countryService = LocationApiServiceLocator.getCountryService();
        }
        return countryService;
    }

    protected CountyService getCountyService() {
        if (countyService == null) {
            countyService = LocationApiServiceLocator.getCountyService();
        }
        return countyService;
    }

    protected PostalCodeService getPostalCodeService() {
        if (postalCodeService == null) {
            postalCodeService = LocationApiServiceLocator.getPostalCodeService();
        }
        return postalCodeService;
    }

    private QueryByCriteria toQuery(Map<String,?> fieldValues) {
        Set<Predicate> preds = new HashSet<Predicate>();
        for (Map.Entry<String, ?> entry : fieldValues.entrySet()) {
            preds.add(equal(entry.getKey(), entry.getValue()));
        }
        Predicate[] predicates = new Predicate[0];
        predicates = preds.toArray(predicates);
        return QueryByCriteria.Builder.fromPredicates(predicates);
    }
}
