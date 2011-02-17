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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.kns.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.CollectionSizeConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.Constraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.DataTypeConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.ExistenceConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.LengthConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.ValidCharactersConstraint;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeDefinitionConstraintProvider implements ConstraintProvider<AttributeDefinition> {

	private ConstraintResolver<AttributeDefinition> CASE_CONSTRAINT_RESOLVER = new ConstraintResolver<AttributeDefinition>() {
		@Override
		public <C extends Constraint> List<C> resolve(AttributeDefinition definition) {
			@SuppressWarnings("unchecked")
			C caseConstraint = (C)definition.getCaseConstraint();
			return Collections.singletonList(caseConstraint);
		}
	};
	
	// In many cases AttributeDefinition is both a Constrainable object and a Constraint -- that is, it has constraint members
	private ConstraintResolver<AttributeDefinition> DEFINITION_CONSTRAINT_RESOLVER = new ConstraintResolver<AttributeDefinition>() {
		@Override
		public <C extends Constraint> List<C> resolve(AttributeDefinition definition) {
			@SuppressWarnings("unchecked")
			C constraint = (C)definition;
			return Collections.singletonList(constraint);
		}
	};
	
	private ConstraintResolver<AttributeDefinition> MUST_OCCUR_CONSTRAINT_RESOLVER = new ConstraintResolver<AttributeDefinition>() {
		@SuppressWarnings("unchecked")
		@Override
		public <C extends Constraint> List<C> resolve(AttributeDefinition definition) {
			return (List<C>) definition.getMustOccurConstraints();
		}
	};
	
	private ConstraintResolver<AttributeDefinition> PREREQUISITE_CONSTRAINT_RESOLVER = new ConstraintResolver<AttributeDefinition>() {
		@SuppressWarnings("unchecked")
		@Override
		public <C extends Constraint> List<C> resolve(AttributeDefinition definition) {
			return (List<C>) definition.getPrerequisiteConstraints();
		}
	};
	
	private ConstraintResolver<AttributeDefinition> VALID_CHARACTERS_CONSTRAINT_RESOLVER = new ConstraintResolver<AttributeDefinition>() {
		@Override
		public <C extends Constraint> List<C> resolve(AttributeDefinition definition) {
			@SuppressWarnings("unchecked")
			C caseConstraint = (C)definition.getValidCharactersConstraint();
			return Collections.singletonList(caseConstraint);
		}
	};
	
	private Map<Class<? extends Constraint>, ConstraintResolver<AttributeDefinition>> resolverMap;
	
	public void init() {
		resolverMap = new HashMap<Class<? extends Constraint>, ConstraintResolver<AttributeDefinition>>();
		resolverMap.put(CaseConstraint.class, CASE_CONSTRAINT_RESOLVER);
		resolverMap.put(ExistenceConstraint.class, DEFINITION_CONSTRAINT_RESOLVER);
		resolverMap.put(DataTypeConstraint.class, DEFINITION_CONSTRAINT_RESOLVER);
		resolverMap.put(LengthConstraint.class, DEFINITION_CONSTRAINT_RESOLVER);
		resolverMap.put(ValidCharactersConstraint.class, VALID_CHARACTERS_CONSTRAINT_RESOLVER);
		resolverMap.put(PrerequisiteConstraint.class, PREREQUISITE_CONSTRAINT_RESOLVER);
		resolverMap.put(MustOccurConstraint.class, MUST_OCCUR_CONSTRAINT_RESOLVER);
		resolverMap.put(CollectionSizeConstraint.class, DEFINITION_CONSTRAINT_RESOLVER);
	}
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.provider.ConstraintProvider#getConstraints(org.kuali.rice.kns.datadictionary.validation.capability.Constrainable, java.lang.Class)
	 */
	@Override
	public List<Constraint> getConstraints(AttributeDefinition definition, Class<? extends Constraint> constraintType) {
		if (resolverMap == null)
			init();
		
		ConstraintResolver<AttributeDefinition> resolver = resolverMap.get(constraintType);

		if (resolver == null)
			return null;
		
		return resolver.resolve(definition);
	}
	
	public Set<Class<? extends Constraint>> getConstraintTypes() {
		if (resolverMap == null)
			init();
		
		return resolverMap.keySet();
	}
	
	public interface ConstraintResolver<T extends Constrainable> {
		
		public <C extends Constraint> List<C> resolve(T definition);
		
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.provider.ConstraintProvider#isSupported(org.kuali.rice.kns.datadictionary.validation.capability.Constrainable)
	 */
	@Override
	public boolean isSupported(Constrainable definition) {
		
		if (definition instanceof AttributeDefinition)
			return true;
		
		return false;
	}

}
