package org.kuali.rice.shareddata.impl.state;


import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.shareddata.api.state.State;
import org.kuali.rice.shareddata.api.state.StateService;

import java.util.*;

public class StateServiceImpl implements StateService {

    private BusinessObjectService businessObjectService;

    @Override
    public State getState(String countryCode, String code) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("countryCode", countryCode);
        map.put("code", code);

        return StateBo.to(businessObjectService.findByPrimaryKey(StateBo.class, Collections.unmodifiableMap(map)));
    }

    @Override
    public List<State> getAllStates(String countryCode) {
        final Collection<StateBo> bos = businessObjectService.findMatching(StateBo.class, Collections.singletonMap("countryCode", countryCode));
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
