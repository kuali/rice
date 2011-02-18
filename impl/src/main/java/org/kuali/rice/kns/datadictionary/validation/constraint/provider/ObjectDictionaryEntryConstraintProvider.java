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

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.datadictionary.ObjectDictionaryEntry;
import org.kuali.rice.kns.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.kns.datadictionary.validation.constraint.Constraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.MustOccurConstraint;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ObjectDictionaryEntryConstraintProvider extends BaseConstraintProvider<ObjectDictionaryEntry> {

	private ConstraintResolver<ObjectDictionaryEntry> MUST_OCCUR_CONSTRAINT_RESOLVER = new ConstraintResolver<ObjectDictionaryEntry>() {
		@SuppressWarnings("unchecked")
		@Override
		public <C extends Constraint> List<C> resolve(ObjectDictionaryEntry definition) {
			return (List<C>) definition.getMustOccurConstraints();
		}
	};

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.provider.ConstraintProvider#isSupported(org.kuali.rice.kns.datadictionary.validation.capability.Constrainable)
	 */
	@Override
	public boolean isSupported(Constrainable definition) {
		
		if (definition instanceof ObjectDictionaryEntry)
			return true;
		
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.provider.BaseConstraintProvider#initializeResolverMap(java.util.Map)
	 */
	@Override
	protected void initializeResolverMap(Map<Class<? extends Constraint>, ConstraintResolver<ObjectDictionaryEntry>> resolverMap) {
		resolverMap.put(MustOccurConstraint.class, MUST_OCCUR_CONSTRAINT_RESOLVER);
	}

}
