package org.kuali.rice.kew.impl.peopleflow;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.kew.api.repository.type.KewTypeDefinition;
import org.kuali.rice.kew.api.repository.type.KewTypeRepositoryService;
import org.kuali.rice.kew.impl.KewImplConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PeopleFlowServiceImpl implements PeopleFlowService {

    private BusinessObjectService businessObjectService;
    private KewTypeRepositoryService kewTypeRepositoryService;

    @Override
    public PeopleFlowDefinition getPeopleFlow(String peopleFlowId) {
        return PeopleFlowBo.to(getPeopleFlowBo(peopleFlowId));
    }

    @Override
    public PeopleFlowDefinition getPeopleFlowByName(String namespaceCode, String name) {
        return PeopleFlowBo.to(getPeopleFlowBoByName(namespaceCode, name));
    }

    @Override
    public PeopleFlowDefinition createPeopleFlow(PeopleFlowDefinition peopleFlow) {
        validateForCreate(peopleFlow);
        KewTypeDefinition kewTypeDefinition = loadKewTypeDefinition(peopleFlow);
        PeopleFlowBo peopleFlowBo = PeopleFlowBo.from(peopleFlow, kewTypeDefinition);
        peopleFlowBo = savePeopleFlow(peopleFlowBo);
        return PeopleFlowBo.to(peopleFlowBo);
    }

    @Override
    public PeopleFlowDefinition updatePeopleFlow(PeopleFlowDefinition peopleFlow) {
        validateForUpdate(peopleFlow);
        KewTypeDefinition kewTypeDefinition = loadKewTypeDefinition(peopleFlow);
        PeopleFlowBo peopleFlowBo = PeopleFlowBo.from(peopleFlow, kewTypeDefinition);
        // note that the current list of people flow attributes should get deleted and new records inserted (with new ids)
        peopleFlowBo = savePeopleFlow(peopleFlowBo);
        return PeopleFlowBo.to(peopleFlowBo);
    }

    protected KewTypeDefinition loadKewTypeDefinition(PeopleFlowDefinition peopleFlow) {
        KewTypeDefinition kewTypeDefinition = null;
        if (peopleFlow.getTypeId() != null) {
            kewTypeDefinition = getKewTypeRepositoryService().getTypeById(peopleFlow.getTypeId());
            if (kewTypeDefinition == null) {
                throw new RiceIllegalArgumentException("Failed to locate a KewTypeDefinition for the given type id of '" + peopleFlow.getTypeId() + "'");
            }
        }
        return kewTypeDefinition;
    }

    protected void validateForCreate(PeopleFlowDefinition peopleFlow) {
        if (peopleFlow == null) {
            throw new RiceIllegalArgumentException("peopleFlow is null");
        }
        if (peopleFlow.getId() != null) {
            throw new RiceIllegalArgumentException("Attempted to create a new PeopleFlow definition with a specified peopleFlowId of '"
                    + peopleFlow.getId() + "'.  This is not allowed, when creating a new PeopleFlow definition, id must be null.");
        }
        if (peopleFlow.getVersionNumber() != null) {
            throw new RiceIllegalArgumentException("The version number on the given PeopleFlow definition was not null, value was " + peopleFlow.getVersionNumber() +
                    "  When creating a new PeopleFlow, the given version number must be null.");
        }
        if (getPeopleFlowBoByName(peopleFlow.getNamespaceCode(), peopleFlow.getName()) != null) {
            throw new RiceIllegalStateException("A PeopleFlow definition with the namespace code '" + peopleFlow.getNamespaceCode() +
            "' and name '" + peopleFlow.getName() + "' already exists.");
        }
    }

    protected void validateForUpdate(PeopleFlowDefinition peopleFlow) {
        if (peopleFlow == null) {
            throw new RiceIllegalArgumentException("peopleFlow is null");
        }
        if (StringUtils.isBlank(peopleFlow.getId())) {
            throw new RiceIllegalArgumentException("Attempted to update a PeopleFlow definition without a specified peopleFlowId, the id is required when performing an update.");
        }
        if (peopleFlow.getVersionNumber() == null) {
            throw new RiceIllegalArgumentException("The version number on the given PeopleFlow definition was null, a version number must be supplied when updating a PeopleFlow.");
        }
    }

    protected PeopleFlowBo getPeopleFlowBo(String peopleFlowId) {
        if (StringUtils.isBlank(peopleFlowId)) {
            throw new RiceIllegalArgumentException("peopleFlowId was a null or blank value");
        }
        return businessObjectService.findBySinglePrimaryKey(PeopleFlowBo.class, peopleFlowId);
    }

    protected PeopleFlowBo getPeopleFlowBoByName(String namespaceCode, String name) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was a null or blank value");
        }
        if (StringUtils.isBlank(name)) {
            throw new RiceIllegalArgumentException("name was a null or blank value");
        }
        Map<String,String> criteria = new HashMap<String,String>();
		criteria.put(KewImplConstants.PropertyConstants.NAMESPACE_CODE, namespaceCode);
        criteria.put(KewImplConstants.PropertyConstants.NAME, name);
		Collection<PeopleFlowBo> peopleFlows = businessObjectService.findMatching(PeopleFlowBo.class, criteria);
        if (CollectionUtils.isEmpty(peopleFlows)) {
            return null;
        } else if (peopleFlows.size() > 1) {
            throw new RiceIllegalStateException("Found more than one PeopleFlow with the given namespace code '" + namespaceCode + "' and name '" + name + "'");
		}
        return peopleFlows.iterator().next();
    }

    protected PeopleFlowBo savePeopleFlow(PeopleFlowBo peopleFlowBo) {
		if ( peopleFlowBo == null ) {
			return null;
		}
        return businessObjectService.save(peopleFlowBo);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public KewTypeRepositoryService getKewTypeRepositoryService() {
        return kewTypeRepositoryService;
    }

    public void setKewTypeRepositoryService(KewTypeRepositoryService kewTypeRepositoryService) {
        this.kewTypeRepositoryService = kewTypeRepositoryService;
    }

}
