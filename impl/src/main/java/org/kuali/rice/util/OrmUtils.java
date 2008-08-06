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
package org.kuali.rice.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.kuali.rice.core.Core;
import org.kuali.rice.core.database.platform.Platform;
import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;

/**
 * A utility for common ORM related functions.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OrmUtils {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrmUtils.class);
	
	private static Map<String, Boolean> cache = new HashMap<String, Boolean>();

	public static void populateAutoIncValue(Object entity, Long value) {
    	try {	    		
    		if (entity.getClass().isAnnotationPresent(Sequence.class)) {
    			Sequence sequence = entity.getClass().getAnnotation(Sequence.class);   
    			Field field = entity.getClass().getDeclaredField(sequence.property());
    			field.setAccessible(true);
    			field.set(entity, value);
    		} 
    	} catch (Exception e) {
    		LOG.error(e.getMessage(), e);
    	}
	}

	public static void populateAutoIncValue(Object entity, EntityManager manager) {
		if (entity.getClass().isAnnotationPresent(Sequence.class)) {
			Sequence sequence = entity.getClass().getAnnotation(Sequence.class); 
			populateAutoIncValue(entity, getNextAutoIncValue(sequence, manager));
		} 
	}
	
	private static Long getNextAutoIncValue(Sequence sequence, EntityManager manager) {
		Long value = -1L;
		try {
			Platform platform = (Platform) GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
			value = platform.getNextValSQL(sequence.name(), manager);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return value;
	}
	
    public static void reattach(Object attached, Object detached) {
    	// Don't want to get parent fields if overridden in children since we are walking the tree from child to parent
    	Set<String> cachedFields = new HashSet<String>(); 
    	Class attachedClass = attached.getClass();
    	do {
    		for (Field attachedField : attachedClass.getDeclaredFields()) {
    			try {
    				attachedField.setAccessible(true);
    				int mods = attachedField.getModifiers();
    				if (!cachedFields.contains(attachedField.getName()) && !Modifier.isFinal(mods) && !Modifier.isStatic(mods)) {
    					attachedField.set(attached, attachedField.get(detached));
    					cachedFields.add(attachedField.getName());
    				}
    			} catch (Exception e) {
    				LOG.error(e.getMessage(), e);
    			}
    		}
    		attachedClass = attachedClass.getSuperclass();
    	} while (attachedClass != null && !(attachedClass.equals(Object.class)));
    }
    
    public static boolean isJpaAnnotated(Class clazz) {
    	if (clazz == null) {
    		return false;
    	}
    	if (!cache.containsKey(clazz.getName())) {
    		if (clazz.getName().indexOf("EnhancerByCGLIB") > -1) {
    			try {
    				// Strip a proxy if found
    				clazz = Class.forName(clazz.getName().substring(0, clazz.getName().indexOf("$$EnhancerByCGLIB")));
    			} catch (Exception e) {
    				LOG.error(e.getMessage(), e);
    			}
    		}
    		cache.put(clazz.getName(), new Boolean(clazz.isAnnotationPresent(Entity.class)));
    	}
    	return cache.get(clazz.getName()).booleanValue();
    }
    
    public static boolean isJpaEnabled() {
    	return "true".equalsIgnoreCase(Core.getCurrentContextConfig().getProperty(RiceConstants.RICE_JPA_ENABLED));
    }
	
}
