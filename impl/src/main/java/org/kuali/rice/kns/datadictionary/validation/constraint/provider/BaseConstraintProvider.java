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
package org.kuali.rice.kns.datadictionary.validation.constraint.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.kns.datadictionary.validation.constraint.Constraint;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class BaseConstraintProvider<T extends Constrainable> implements ConstraintProvider<T> {
	
	
	private Map<Class<? extends Constraint>, ConstraintResolver<T>> resolverMap;
	
	public void init() {
		resolverMap = new HashMap<Class<? extends Constraint>, ConstraintResolver<T>>();
		initializeResolverMap(resolverMap);
	}
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.provider.ConstraintProvider#getConstraints(org.kuali.rice.kns.datadictionary.validation.capability.Constrainable, java.lang.Class)
	 */
	@Override
	public List<Constraint> getConstraints(T definition, Class<? extends Constraint> constraintType) {
		if (resolverMap == null)
			init();
		
		ConstraintResolver<T> resolver = resolverMap.get(constraintType);

		if (resolver == null)
			return null;
		
		return resolver.resolve(definition);
	}

	protected abstract void initializeResolverMap(Map<Class<? extends Constraint>, ConstraintResolver<T>> resolverMap);
	
}
