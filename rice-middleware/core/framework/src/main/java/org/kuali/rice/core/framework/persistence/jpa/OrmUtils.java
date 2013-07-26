/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.core.framework.persistence.jpa;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A utility for common ORM related functions.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public final class OrmUtils {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrmUtils.class);
	
	private static Map<String, Boolean> cache = new HashMap<String, Boolean>();

	private OrmUtils() {
		throw new UnsupportedOperationException("do not call");
	}

	public static void populateAutoIncValue(Object entity, EntityManager manager) {
		throw new UnsupportedOperationException("Annotated your entity with @Sequence and use KRAD Data id generation abstraction");
	}

	public static void reattach(Object detached, Object attached) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Reattaching entity: " + attached.getClass().getName());
    	}
    	// Don't want to get parent fields if overridden in children since we are walking the tree from child to parent
    	Set<String> cachedFields = new HashSet<String>(); 
    	Class attachedClass = detached.getClass();
    	
    	do {
    		for (Field attachedField : attachedClass.getDeclaredFields()) {
    			try {
    				attachedField.setAccessible(true);
    				int mods = attachedField.getModifiers();
    				if (!cachedFields.contains(attachedField.getName()) && !Modifier.isFinal(mods) && !Modifier.isStatic(mods)) {
    					//detached.getClass().getDeclaredField(attachedField.getName()).get(attached)
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
    
    public static void merge(EntityManager manager, Object entity) {
        if(manager.contains(entity)) {
            manager.merge(entity);
        }
        else {
            OrmUtils.reattach(entity, manager.merge(entity));        	
        }
    }
}
