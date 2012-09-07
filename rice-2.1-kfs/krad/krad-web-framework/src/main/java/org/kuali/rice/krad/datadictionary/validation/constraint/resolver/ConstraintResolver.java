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
package org.kuali.rice.krad.datadictionary.validation.constraint.resolver;

import org.kuali.rice.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.Constraint;

import java.util.List;

/**
 * An interface that provides a lookup of constraints for a specific constrainable attribute definition. Implemented by constraint
 * providers as a mechanism to store functional lookups in a map, keyed by constraint type, for example. 
 * 
 * {@see AttributeDefinitionConstraintProvider} for a number of examples. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @param <T>
 * @since 1.1
 */
public interface ConstraintResolver<T extends Constrainable> {
	
	public <C extends Constraint> List<C> resolve(T definition);
	
}