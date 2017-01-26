/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.location.impl.campus;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusQueryResults;
import org.kuali.rice.location.api.campus.CampusService;
import org.kuali.rice.location.api.campus.CampusType;
import org.kuali.rice.location.api.campus.CampusTypeQueryResults;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonMap;

public class CampusServiceImpl implements CampusService {
    private DataObjectService dataObjectService;
	
	/**
     * @see org.kuali.rice.location.api.campus.CampusService#getCampus(String code)
     */
	@Override
	public Campus getCampus(String code) {
		if (StringUtils.isBlank(code)) {
            throw new RiceIllegalArgumentException("code is blank");
        }
        CampusBo campusBo = getDataObjectService().find(CampusBo.class, code);
        return CampusBo.to(campusBo);
	}

	/**
     * @see org.kuali.rice.location.api.campus.CampusService#findAllCampuses
     */
	@Override
	public List<Campus> findAllCampuses() {
        QueryByCriteria qbc = QueryByCriteria.Builder.forAttribute("active", true).build();
        QueryResults<CampusBo> campusBos = getDataObjectService().findMatching(CampusBo.class,qbc);
        return this.convertListOfCampusBosToImmutables(campusBos.getResults());
	}

	/**
     * @see org.kuali.rice.location.api.campus.CampusService#getCampusType(String code)
     */
	@Override
	public CampusType getCampusType(String code) {
		if (StringUtils.isBlank(code)) {
            throw new RiceIllegalArgumentException("code is blank");
        }
        CampusTypeBo campusTypeBo = dataObjectService.find(CampusTypeBo.class, code);
        return CampusTypeBo.to(campusTypeBo);
	}

	/**
     * @see org.kuali.rice.location.api.campus.CampusService#findAllCampusTypes
     */
	@Override
	public List<CampusType> findAllCampusTypes() {
        QueryByCriteria qbc = QueryByCriteria.Builder.forAttribute("active", true).build();
        QueryResults<CampusTypeBo> campusTypeBos = dataObjectService.findMatching(CampusTypeBo.class,qbc);
		return this.convertListOfCampusTypesBosToImmutables(campusTypeBos.getResults());
	}

    @Override
    public CampusQueryResults findCampuses(QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        incomingParamCheck(queryByCriteria, "queryByCriteria");

        QueryResults<CampusBo> results = dataObjectService.findMatching(CampusBo.class, queryByCriteria);

        CampusQueryResults.Builder builder = CampusQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<Campus.Builder> ims = new ArrayList<Campus.Builder>();
        for (CampusBo bo : results.getResults()) {
            ims.add(Campus.Builder.create(bo));
        }

        builder.setResults(ims);
        return builder.build();
    }

    @Override
    public CampusTypeQueryResults findCampusTypes(QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        incomingParamCheck(queryByCriteria, "query");

        QueryResults<CampusTypeBo> results = dataObjectService.findMatching(CampusTypeBo.class, queryByCriteria);

        CampusTypeQueryResults.Builder builder = CampusTypeQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<CampusType.Builder> ims = new ArrayList<CampusType.Builder>();
        for (CampusTypeBo bo : results.getResults()) {
            ims.add(CampusType.Builder.create(bo));
        }

        builder.setResults(ims);
        return builder.build();
    }

    private List<Campus> convertListOfCampusBosToImmutables(List<CampusBo> campusBos) {
        ArrayList<Campus> campuses = new ArrayList<Campus>();
        for (CampusBo bo : campusBos) {
            Campus campus = CampusBo.to(bo);
            campuses.add(campus) ;
        }
        return Collections.unmodifiableList(campuses);
    }
    
    private List<CampusType> convertListOfCampusTypesBosToImmutables(List<CampusTypeBo> campusTypeBos) {
        ArrayList<CampusType> campusTypes = new ArrayList<CampusType>();
        for (CampusTypeBo bo : campusTypeBos) {
            CampusType campusType = CampusTypeBo.to(bo);
            campusTypes.add(campusType) ;
        }
        return Collections.unmodifiableList(campusTypes);
    }

    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            throw new RiceIllegalArgumentException(name + " was null");
        } else if (object instanceof String
                && StringUtils.isBlank((String) object)) {
            throw new RiceIllegalArgumentException(name + " was blank");
        }
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
