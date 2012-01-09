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
package org.kuali.rice.krms.api.repository.term;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

import java.util.List;

/**
 * Contract for {@link TermDefinition} and related objects.  
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface TermDefinitionContract extends Identifiable, Versioned {
	
	/**
	 * @return the associated {@link TermSpecificationDefinitionContract}
	 */
	TermSpecificationDefinitionContract getSpecification();

    /**
     * @return the description for this {@link TermDefinitionContract}
     */
    String getDescription();
	
	/**
	 * @return any parameters specified on this {@link TermDefinitionContract} 
	 */
	List<? extends TermParameterDefinitionContract> getParameters();
	
}
