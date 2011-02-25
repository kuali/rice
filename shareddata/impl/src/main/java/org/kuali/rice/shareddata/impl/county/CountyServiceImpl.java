package org.kuali.rice.shareddata.impl.county;

import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.shareddata.api.county.County;
import org.kuali.rice.shareddata.api.county.CountyService;

import java.util.*;

public class CountyServiceImpl implements CountyService {

    private BusinessObjectService businessObjectService;

    @Override
    public County getCounty(String countryCode, String stateCode, String code) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("countryCode", countryCode);
        map.put("stateCode", stateCode);
        map.put("code", code);

        return CountyBo.to(businessObjectService.findByPrimaryKey(CountyBo.class, Collections.unmodifiableMap(map)));
    }

    @Override
    public List<County> getAllPostalCodes(String countryCode, String stateCode) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("countryCode", countryCode);
        map.put("stateCode", stateCode);

        final Collection<CountyBo> bos = businessObjectService.findMatching(CountyBo.class, Collections.unmodifiableMap(map));
        if (bos == null) {
            return Collections.emptyList();
        }

        final List<County> toReturn = new ArrayList<County>();
        for (CountyBo bo : bos) {
            if (bo != null && bo.isActive()) {
                toReturn.add(CountyBo.to(bo));
            }
        }

        return Collections.unmodifiableList(toReturn);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
