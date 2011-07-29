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
package org.kuali.rice.krms.api.repository.term;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krms.api.repository.category.CategoryDefinitionContract;

import java.util.List;

/**
 * Interface for classes specifying term specifications
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface TermSpecificationDefinitionContract extends Identifiable, Versioned {

    /**
     * @return the name for this {@link TermSpecificationDefinitionContract}
     */
	String getName();

    /**
	 * This is the namespace of the {@link TermSpecificationDefinitionContract}
	 *
	 * <p>
	 * The namespace of the TermSpecificationDefinitionContract
	 * </p>
	 * @return the namespace of the TermSpecificationDefinitionContract
	 */
	public String getNamespace();

    /**
     * @return the fully qualified class name for this {@link TermSpecificationDefinitionContract}
     */
	String getType();

    /**
     * @return the description for this {@link TermSpecificationDefinitionContract}
     */
    String getDescription();

    /**
     * Returns an ordered, immutable list of the categories which this term specification
     * definition requires.  This list can be empty (in the case of a term
     * which has no arguments) but will never be null.
     *
     * @return the list of categories for this term specification definition
     */
    List<? extends CategoryDefinitionContract> getCategories();

}
