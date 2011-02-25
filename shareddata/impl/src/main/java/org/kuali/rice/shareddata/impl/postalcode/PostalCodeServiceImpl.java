package org.kuali.rice.shareddata.impl.postalcode;

import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.shareddata.api.postalcode.PostalCode;
import org.kuali.rice.shareddata.api.postalcode.PostalCodeService;

import java.util.*;

public class PostalCodeServiceImpl implements PostalCodeService {

    private BusinessObjectService businessObjectService;

    @Override
    public PostalCode getPostalCode(String countryCode, String code) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("countryCode", countryCode);
        map.put("code", code);

        return PostalCodeBo.to(businessObjectService.findByPrimaryKey(PostalCodeBo.class, Collections.unmodifiableMap(map)));
    }

    @Override
    public List<PostalCode> getAllPostalCodes(String countryCode) {
        final Collection<PostalCodeBo> bos = businessObjectService.findMatching(PostalCodeBo.class, Collections.singletonMap("countryCode", countryCode));
        if (bos == null) {
            return Collections.emptyList();
        }

        final List<PostalCode> toReturn = new ArrayList<PostalCode>();
        for (PostalCodeBo bo : bos) {
            if (bo != null && bo.isActive()) {
                toReturn.add(PostalCodeBo.to(bo));
            }
        }

        return Collections.unmodifiableList(toReturn);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
