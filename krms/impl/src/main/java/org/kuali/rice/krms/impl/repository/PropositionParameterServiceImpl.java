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

package org.kuali.rice.krms.impl.repository;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.PropositionParameter;
import org.kuali.rice.krms.api.repository.PropositionParameterService;

import java.util.*;

public final class PropositionParameterServiceImpl implements PropositionParameterService {

    private BusinessObjectService businessObjectService;

	/**
	 * This overridden method creates a PropositionParameter if it does not 
	 * already exist in the repository.
	 * 
	 * @see org.kuali.rice.krms.api.repository.PropositionParameterService#createParameter(org.kuali.rice.krms.api.repository.PropositionParameter)
	 */
	@Override
	public void createParameter(PropositionParameter parameter) {
		if (parameter == null){
	        throw new IllegalArgumentException("parameter is null");
		}
		final String propIdKey = parameter.getPropId();
		final Integer seqNoKey = parameter.getSequenceNumber();
		final PropositionParameter existing = getParameterByPropIdAndSequenceNumber(propIdKey, seqNoKey);
		if (existing != null && existing.getPropId().equals(propIdKey) && existing.getSequenceNumber().equals(seqNoKey)){
            throw new IllegalStateException("the parameter to create already exists: " + parameter);			
		}
		
		businessObjectService.save(PropositionParameterBo.from(parameter));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.PropositionParameterService#updateParameter(org.kuali.rice.krms.api.repository.PropositionParameter)
	 */
	@Override
	public void updateParameter(PropositionParameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("parameter is null");
        }
		final String propIdKey = parameter.getPropId();
		final Integer seqNoKey = parameter.getSequenceNumber();
		final PropositionParameter existing = getParameterByPropIdAndSequenceNumber(propIdKey, seqNoKey);
        if (existing == null) {
            throw new IllegalStateException("the parameter does not exist: " + parameter);
        }
        final PropositionParameter toUpdate;
        if (!existing.getId().equals(parameter.getId())){
        	final PropositionParameter.Builder builder = PropositionParameter.Builder.create(parameter);
        	builder.setId(existing.getId());
        	toUpdate = builder.build();
        } else {
        	toUpdate = parameter;
        }
        
        businessObjectService.save(PropositionParameterBo.from(toUpdate));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.PropositionParameterService#getParameters(java.lang.Long)
	 */
	@Override
	public List<PropositionParameter> getParameters(String propId) {
		if (StringUtils.isBlank(propId)) {
            throw new IllegalArgumentException("propId is null or blank");
		}
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("propId", propId);
		List<PropositionParameterBo> bos = (List<PropositionParameterBo>) businessObjectService.findMatchingOrderBy(PropositionParameterBo.class, map, "sequenceNumber", true);
		return PropositionParameterBo.to(bos);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.PropositionParameterService#getParameterById(java.lang.String)
	 */
	@Override
	public PropositionParameter getParameterById(String id) {
		if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id is null or blank");
		}
		PropositionParameterBo bo = businessObjectService.findBySinglePrimaryKey(PropositionParameterBo.class, id);
		return PropositionParameterBo.to(bo);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.PropositionParameterService#getParameterByPropIdAndSequenceNumber(java.lang.String, java.lang.String)
	 */
	@Override
	public PropositionParameter getParameterByPropIdAndSequenceNumber(
			String propId, Integer sequenceNumber) {
		if (StringUtils.isBlank(propId)) {
            throw new IllegalArgumentException("propId is null or blank");
		}
		if (sequenceNumber == null) {
            throw new IllegalArgumentException("sequenceNumber is null");
		}
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("propId", propId);
        map.put("sequenceNumber", sequenceNumber);
		PropositionParameterBo bo = businessObjectService.findByPrimaryKey(PropositionParameterBo.class, map);
		return PropositionParameterBo.to(bo);
	}

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
