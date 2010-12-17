/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.core.jpa.spring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.reflections.Reflections;

/**
 * Abstract class which exposes as JPA managed classes all of the business objects in a given package 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class PackagePersistableBusinessObjectClassExposer implements
		PersistableBusinessObjectClassExposer {

	/**
	 * Exposes all of the JPA annotated entities in the package given by the getPackageNameToExpose method
	 * to be managed by the PersistenceUnit calling this
	 * 
	 * @see org.kuali.rice.core.jpa.spring.PersistableBusinessObjectClassExposer#exposePersistableBusinessObjectClassNames()
	 */
	@Override
	public Set<String> exposePersistableBusinessObjectClassNames() {
		Set<String> exposedClassNames = new HashSet<String>();
		
		for (String packageNameToExpose: getPackageNamesToExpose()) {
			Reflections reflections = new Reflections(packageNameToExpose);
			final Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
			for (Class<?> entityClass : entities) {
				exposedClassNames.add(entityClass.getName());
			}
			
			final Set<Class<?>> mappedSuperclasses = reflections.getTypesAnnotatedWith(MappedSuperclass.class);
			for (Class<?> mappedSuperclassClass : mappedSuperclasses) {
				exposedClassNames.add(mappedSuperclassClass.getName());
			}
			
			final Set<Class<?>> embeddables = reflections.getTypesAnnotatedWith(Embeddable.class);
			for (Class<?> embeddableClass : embeddables) {
				// may this loop never be entered
				exposedClassNames.add(embeddableClass.getName());
			}
		}
		return exposedClassNames;
	}
	
	/**
	 * @return the name of the package to expose all JPA annotated entities in
	 */
	public abstract List<String> getPackageNamesToExpose();

}
