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
import org.kuali.rice.krms.api.repository.term.TermResolverDefinition;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;

/**
 * BO service for terms and related entities
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface TermBoService {
	
	// TODO: javadocs
	
	TermSpecificationDefinition getTermSpecificationById(String id);
	TermSpecificationDefinition createTermSpecification(TermSpecificationDefinition termSpec);
	
	TermDefinition getTermById(String id);
	TermDefinition createTermDefinition(TermDefinition termDef);
	
	TermResolverDefinition getTermResolverById(String id);
	List<TermResolverDefinition> getTermResolversByNamespace(String namespace);
	TermResolverDefinition createTermResolver(TermResolverDefinition termResolver);
}
