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
package org.kuali.rice.krad.datadictionary.validation.constraint.provider;

import java.util.List;

import org.kuali.rice.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.Constraint;

/**
 * An object that determines a list of constraints for a given Constrainable definition for an attribute 
 * in the data dictionary. The ConstraintProvider interface must be implemented by any class that contributes
 * Constraints to the DictionaryValidationService. Multiple ConstraintProviders can be registered simultaneously,
 * and each can contribute constraints for any number of constraint types. 
 * 
 * These constraints can be looked up in a variety of ways. They may be:
 * 
 * (1) member variables of the Constrainable definition itself {@see CaseConstrainable.class}
 * (2) the Constrainable definition itself may extend Constraint {@see LengthConstrainable.class}
 * (3) provided from some external source, or generated on the fly
 * 
 * The goal here is to provide a mechanism that enables implementing institutions to inject new Constraints and ConstraintProcessor
 * classes into the DictionaryValidationService implementation via dependency injection. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
public interface ConstraintProvider<T extends Constrainable> {

	public List<Constraint> getConstraints(T definition, Class<? extends Constraint> constraintType);
	
	public boolean isSupported(Constrainable definition);
	
}
