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
package org.kuali.rice.location.impl.county;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.location.api.county.County;
import org.kuali.rice.location.api.county.CountyQueryResults;
import org.kuali.rice.location.api.county.CountyService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountyServiceImpl implements CountyService {
    private DataObjectService dataObjectService;

    @Override
    public County getCounty(String countryCode, String stateCode, String code) {
        if (StringUtils.isBlank(countryCode)) {
            throw new RiceIllegalArgumentException(("countryCode is null"));
        }

        if (StringUtils.isBlank(code)) {
            throw new RiceIllegalArgumentException(("code is null"));
        }

        if (StringUtils.isBlank(stateCode)) {
            throw new RiceIllegalArgumentException(("stateCode is null"));
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("countryCode", countryCode);
        map.put("stateCode", stateCode);
        map.put("code", code);

        return CountyBo.to(getDataObjectService().find(CountyBo.class, new CompoundKey(map)));
    }

    @Override
    public List<County> findAllCountiesInCountryAndState(String countryCode, String stateCode) {
        if (StringUtils.isBlank(countryCode)) {
            throw new RiceIllegalArgumentException(("countryCode is null"));
        }

        if (StringUtils.isBlank(stateCode)) {
            throw new RiceIllegalArgumentException(("stateCode is null"));
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("countryCode", countryCode);
        map.put("stateCode", stateCode);
        map.put("active", Boolean.TRUE);

        QueryResults<CountyBo> countyBos = getDataObjectService().findMatching(CountyBo.class,
                QueryByCriteria.Builder.andAttributes(map).build());

        if (countyBos == null) {
            return Collections.emptyList();
        }

        final List<County> toReturn = new ArrayList<County>();

        List<CountyBo> countyBoList = countyBos.getResults();


        for(CountyBo countyBo : countyBoList){
            if(countyBo != null && countyBo.isActive()){
                toReturn.add(CountyBo.to(countyBo));
            }
        }

//        for (CountyBo bo : countyBoList) {
//            if (bo != null && bo.isActive()) {
//                toReturn.add(CountyBo.to(bo));
//            }
//        }

        return Collections.unmodifiableList(toReturn);
    }

    @Override
    public CountyQueryResults findCounties(QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        incomingParamCheck(queryByCriteria, "queryByCriteria");

        QueryResults<CountyBo> results = getDataObjectService().findMatching(CountyBo.class, queryByCriteria);

        CountyQueryResults.Builder builder = CountyQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<County.Builder> ims = new ArrayList<County.Builder>();
        for (CountyBo bo : results.getResults()) {
            ims.add(County.Builder.create(bo));
        }

        builder.setResults(ims);
        return builder.build();
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
