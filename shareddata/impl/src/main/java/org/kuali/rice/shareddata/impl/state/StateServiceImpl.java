/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.shareddata.impl.state;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.shareddata.api.state.State;
import org.kuali.rice.shareddata.api.state.StateService;

import java.util.*;

public class StateServiceImpl implements StateService {

    private BusinessObjectService businessObjectService;

    @Override
    public State getState(String countryCode, String code) {
        if (StringUtils.isBlank(countryCode)) {
            throw new IllegalArgumentException(("countryCode is null"));
        }

        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException(("code is null"));
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("countryCode", countryCode);
        map.put("code", code);

        return StateBo.to(businessObjectService.findByPrimaryKey(StateBo.class, Collections.unmodifiableMap(map)));
    }

    @Override
    public List<State> findAllStatesInCountry(String countryCode) {
        if (StringUtils.isBlank(countryCode)) {
            throw new IllegalArgumentException(("countryCode is null"));
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("countryCode", countryCode);
        map.put("active", Boolean.TRUE);

        final Collection<StateBo> bos = businessObjectService.findMatching(StateBo.class, Collections.unmodifiableMap(map));
        if (bos == null) {
            return Collections.emptyList();
        }

        final List<State> toReturn = new ArrayList<State>();
        for (StateBo bo : bos) {
            if (bo != null && bo.isActive()) {
                toReturn.add(StateBo.to(bo));
            }
        }

        return Collections.unmodifiableList(toReturn);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
