/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.impl.repository;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.engine.TermSpecification;
import org.kuali.rice.krms.api.repository.TermDefinition;
import org.kuali.rice.krms.api.repository.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.TermSpecificationDefinition;

/**
 * Implementation of {@link TermBoService}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class TermBoServiceImpl implements TermBoService {
	
	private BusinessObjectService businessObjectService;

	/**
	 * @param businessObjectService the businessObjectService to set
	 */
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}
	
	/**
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#getTermSpecificationById(java.lang.String)
	 */
	@Override
	public TermSpecificationDefinition getTermSpecificationById(String id) {
		TermSpecificationBo termSpecificationBo = 
			businessObjectService.findBySinglePrimaryKey(TermSpecificationBo.class, id);
		return TermSpecificationDefinition.Builder.create(termSpecificationBo).build();
	}
	
	/**
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#createTermSpecification(org.kuali.rice.krms.api.repository.TermSpecificationDefinition)
	 */
	@Override
	public void createTermSpecification(TermSpecificationDefinition termSpec) {
		if (!StringUtils.isBlank(termSpec.getId())) {
			throw new IllegalArgumentException("for creation, TermSpecification.id must be null");
		}
		
		TermSpecificationBo termSpecBo = TermSpecificationBo.from(termSpec);
		
		businessObjectService.save(termSpecBo);
	}
	
	/**
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#createTermDefinition(org.kuali.rice.krms.api.repository.TermDefinition)
	 */
	@Override
	public void createTermDefinition(TermDefinition termDef) {
		// TODO: !!!
		throw new UnsupportedOperationException();
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#createTermResolver(org.kuali.rice.krms.api.repository.TermResolverDefinition)
	 */
	@Override
	public void createTermResolver(TermResolverDefinition termResolver) {
		// TODO: !!!
		throw new UnsupportedOperationException();
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#getTermById(java.lang.String)
	 */
	@Override
	public TermDefinition getTermById(String id) {
		// TODO: !!!
		throw new UnsupportedOperationException();
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#getTermResolverById(java.lang.String)
	 */
	@Override
	public TermResolverDefinition getTermResolverById(String id) {
		// TODO: !!!
		throw new UnsupportedOperationException();
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#getTermResolversByContextId(java.lang.String)
	 */
	@Override
	public List<TermResolverDefinition> getTermResolversByContextId(String id) {
		// TODO: !!!
		throw new UnsupportedOperationException();
	}

}
