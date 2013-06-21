/**
 * Copyright 2005-2012 The Kuali Foundation
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

import java.util.List;

import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermRepositoryService;
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * BO service for terms and related entities
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface TermBoService extends TermRepositoryService {
	
	// TODO: javadocs
    //@Cacheable(value= TermSpecificationDefinition.Cache.NAME, key="'id=' + #p0")
	TermSpecificationDefinition getTermSpecificationById(String id);

    //@CacheEvict(value={TermSpecificationDefinition.Cache.NAME, TermDefinition.Cache.NAME}, allEntries = true)
	TermSpecificationDefinition createTermSpecification(TermSpecificationDefinition termSpec);

    //@Cacheable(value= TermDefinition.Cache.NAME, key="'id=' + #p0")
    TermDefinition getTerm(String id);

    //@CacheEvict(value={TermDefinition.Cache.NAME}, allEntries = true)
	TermDefinition createTerm(TermDefinition termDef);

    //@Cacheable(value= TermResolverDefinition.Cache.NAME, key="'id=' + #p0")
	TermResolverDefinition getTermResolverById(String id);

    /**
     * Get the {@link TermResolverDefinition}s for any term resolvers in the specified namespace that have the given
     * term specification as their output.
     *
     * @param id the id for the term specification
     * @param namespace the namespace to search
     * @return the List of term resolvers found.  If none are found, an empty list will be returned.
     */
    //@Cacheable(value= TermResolverDefinition.Cache.NAME, key="'id=' + #p0 + '|' + 'namespace=' + #p1")
    List<TermResolverDefinition> findTermResolversByOutputId(String id, String namespace);

    //@Cacheable(value= TermResolverDefinition.Cache.NAME, key="'namespace=' + #p0")
    List<TermResolverDefinition> findTermResolversByNamespace(String namespace);

    //@CacheEvict(value={TermResolverDefinition.Cache.NAME, TermDefinition.Cache.NAME}, allEntries = true)
	TermResolverDefinition createTermResolver(TermResolverDefinition termResolver);
}
