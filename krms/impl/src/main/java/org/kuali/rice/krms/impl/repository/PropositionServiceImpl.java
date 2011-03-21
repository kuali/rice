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
import org.kuali.rice.krms.api.repository.Proposition;
import org.kuali.rice.krms.api.repository.PropositionService;

public final class PropositionServiceImpl implements PropositionService {

    private BusinessObjectService businessObjectService;

	/**
	 * This overridden method creates a Proposition if it does not 
	 * already exist in the repository.
	 * 
	 * @see org.kuali.rice.krms.api.repository.PropositionService#createProposition(org.kuali.rice.krms.api.repository.Proposition)
	 */
	@Override
	public void createProposition(Proposition prop) {
		if (prop == null){
	        throw new IllegalArgumentException("proposition is null");
		}
		final String propIdKey = prop.getPropId();
		final Proposition existing = getPropositionById(propIdKey);
		if (existing != null && existing.getPropId().equals(propIdKey)){
            throw new IllegalStateException("the proposition to create already exists: " + prop);			
		}
		
		businessObjectService.save(PropositionBo.from(prop));
	}

	/**
	 * This overridden method updates an existing proposition
	 * 
	 * @see org.kuali.rice.krms.api.repository.PropositionService#updateProposition(org.kuali.rice.krms.api.repository.Proposition)
	 */
	@Override
	public void updateProposition(Proposition prop) {
        if (prop == null) {
            throw new IllegalArgumentException("proposition is null");
        }
		final String propIdKey = prop.getPropId();
		final Proposition existing = getPropositionById(propIdKey);
        if (existing == null) {
            throw new IllegalStateException("the proposition does not exist: " + prop);
        }
        final Proposition toUpdate;
        if (!existing.getPropId().equals(prop.getPropId())){
        	final Proposition.Builder builder = Proposition.Builder.create(prop);
        	builder.setPropId(existing.getPropId());
        	toUpdate = builder.build();
        } else {
        	toUpdate = prop;
        }
        
        businessObjectService.save(PropositionBo.from(toUpdate));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.PropositionService#getPropositionById(java.lang.String)
	 */
	@Override
	public Proposition getPropositionById(String propId) {
		if (StringUtils.isBlank(propId)){
            throw new IllegalArgumentException("propId is null or blank");			
		}
		PropositionBo bo = businessObjectService.findBySinglePrimaryKey(PropositionBo.class, propId);
		return PropositionBo.to(bo);
	}

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
