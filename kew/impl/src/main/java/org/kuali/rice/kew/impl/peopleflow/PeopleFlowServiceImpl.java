package org.kuali.rice.kew.impl.peopleflow;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.kew.impl.KewImplConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PeopleFlowServiceImpl implements PeopleFlowService {

    private BusinessObjectService businessObjectService;

    @Override
    public PeopleFlowDefinition getPeopleFlow(String peopleFlowId) {
        return PeopleFlowBo.to(getPeopleFlowBo(peopleFlowId));
    }

    @Override
    public PeopleFlowDefinition getPeopleFlowByName(String namespaceCode, String name) {
        if (StringUtils.isBlank(namespaceCode)) {
			throw new RiceIllegalArgumentException("namespaceCode was a blank or null value");
		}
        if (StringUtils.isBlank(name)) {
			throw new RiceIllegalArgumentException("name was a blank or null value");
		}
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KewImplConstants.PropertyConstants.NAMESPACE_CODE, namespaceCode);
        criteria.put(KewImplConstants.PropertyConstants.NAME, name);
		Collection<PeopleFlowBo> peopleFlows = businessObjectService.findMatching(PeopleFlowBo.class, criteria);
        if (CollectionUtils.isEmpty(peopleFlows)) {
            return null;
        } else if (peopleFlows.size() > 0) {
            throw new IllegalStateException("Found more than one PeopleFlow with the given name: " + namespaceCode + ":" + name);
		} else {
            return PeopleFlowBo.to(peopleFlows.iterator().next());
        }
    }

    protected PeopleFlowBo getPeopleFlowBo(String peopleFlowId) {
        if (StringUtils.isBlank(peopleFlowId)) {
            throw new RiceIllegalArgumentException("peopleFlowId was a null or blank value");
        }
        return businessObjectService.findBySinglePrimaryKey(PeopleFlowBo.class, peopleFlowId);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
}
