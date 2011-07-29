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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.springframework.util.CollectionUtils;

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
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#createTermSpecification(org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition)
	 */
	@Override
	public TermSpecificationDefinition createTermSpecification(TermSpecificationDefinition termSpec) {
		if (!StringUtils.isBlank(termSpec.getId())) {
			throw new IllegalArgumentException("for creation, TermSpecification.id must be null");
		}
		
		TermSpecificationBo termSpecBo = TermSpecificationBo.from(termSpec);
		
		businessObjectService.save(termSpecBo);
		
		return TermSpecificationBo.to(termSpecBo);
	}
	
	/**
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#createTermDefinition(org.kuali.rice.krms.api.repository.term.TermDefinition)
	 */
	@Override
	public TermDefinition createTermDefinition(TermDefinition termDef) {
		if (!StringUtils.isBlank(termDef.getId())) {
			throw new IllegalArgumentException("for creation, TermDefinition.id must be null");
		}
		
		TermBo termBo = TermBo.from(termDef);
		
		businessObjectService.save(termBo);
		
		return TermBo.to(termBo);
	}
	
	/**
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#createTermResolver(org.kuali.rice.krms.api.repository.term.TermResolverDefinition)
	 */
	@Override
	public TermResolverDefinition createTermResolver(TermResolverDefinition termResolver) {
		if (!StringUtils.isBlank(termResolver.getId())) {
			throw new IllegalArgumentException("for creation, TermResolverDefinition.id must be null");
		}
		
		TermResolverBo termResolverBo = TermResolverBo.from(termResolver);
		
		termResolverBo = (TermResolverBo)businessObjectService.save(termResolverBo);
		
		return TermResolverBo.to(termResolverBo);
	}
	
	/**
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#getTermById(java.lang.String)
	 */
	@Override
	public TermDefinition getTermById(String id) {
		TermDefinition result = null;
		
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id must not be blank or null");
		}
		TermBo termBo = businessObjectService.findBySinglePrimaryKey(TermBo.class, id);
		
		if (termBo != null) {
			result= TermBo.to(termBo);
		}
		
		return result;
	}
	
	/**
	 * @see org.kuali.rice.krms.impl.repository.TermBoService#getTermResolverById(java.lang.String)
	 */
	@Override
	public TermResolverDefinition getTermResolverById(String id) {
		TermResolverDefinition result = null;
		
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("id must not be blank or null");
		}
		TermResolverBo termResolverBo = businessObjectService.findBySinglePrimaryKey(TermResolverBo.class, id);
		
		if (termResolverBo != null) {
			result = TermResolverBo.to(termResolverBo);
		}
		
		return result;
	}
	
    @Override
    public List<TermResolverDefinition> getTermResolversByNamespace(String namespace) {
        List<TermResolverDefinition> results = null;

        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace must not be blank or null");
        }

        Map fieldValues = new HashMap();
        fieldValues.put("namespace", namespace);

        Collection<TermResolverBo> termResolverBos = businessObjectService.findMatching(TermResolverBo.class, fieldValues);

        if (!CollectionUtils.isEmpty(termResolverBos)) {
            results = new ArrayList<TermResolverDefinition>(termResolverBos.size());

            for (TermResolverBo termResolverBo : termResolverBos) if (termResolverBo != null) {
                results.add(TermResolverBo.to(termResolverBo));
            }
        } else {
            results = Collections.emptyList();
        }

        return results;
    }
}
