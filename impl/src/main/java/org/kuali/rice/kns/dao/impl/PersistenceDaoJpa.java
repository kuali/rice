/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.dao.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.core.jpa.metadata.CollectionDescriptor;
import org.kuali.rice.core.jpa.metadata.EntityDescriptor;
import org.kuali.rice.core.jpa.metadata.MetadataManager;
import org.kuali.rice.core.jpa.metadata.ObjectDescriptor;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.PersistenceDao;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class PersistenceDaoJpa implements PersistenceDao {

	/**
	 * @see org.kuali.rice.kns.dao.PersistenceDao#clearCache()
	 */
	public void clearCache() {}

	/**
	 * @see org.kuali.rice.kns.dao.PersistenceDao#resolveProxy(java.lang.Object)
	 */
	public Object resolveProxy(Object o) {
		return o;
	}

	/**
	 * @see org.kuali.rice.kns.dao.PersistenceDao#retrieveAllReferences(java.lang.Object)
	 */
	public void retrieveAllReferences(Object o) {
		EntityDescriptor ed = MetadataManager.getEntityDescriptor(o.getClass());
		for (ObjectDescriptor od : ed.getObjectRelationships()) {
			retrieveReference(o, od.getAttributeName());
		}
		for (CollectionDescriptor cd : ed.getCollectionRelationships()) {
			retrieveReference(o, cd.getAttributeName());
		}
	}

	/**
	 * @see org.kuali.rice.kns.dao.PersistenceDao#retrieveReference(java.lang.Object, java.lang.String)
	 */
	public void retrieveReference(Object o, String referenceName) {
		try {
			Field field = getField(o.getClass(), referenceName);
			field.setAccessible(true);
			
			String fk = null;

			EntityDescriptor ed = MetadataManager.getEntityDescriptor(o.getClass());
			CollectionDescriptor cd = ed.getCollectionDescriptorByName(referenceName);
			if (cd == null) {
				ObjectDescriptor od = ed.getObjectDescriptorByName(referenceName);
				fk = od.getForeignKeyFields().get(0);
			} else {
				fk = cd.getForeignKeyFields().get(0);
			}
			
			Field field2 = null;
			try {
				field2 = getField(o.getClass(), fk);
			} catch (Exception e) {
				String pk = ed.getPrimaryKeys().iterator().next().getName();				
				field2 = getField(o.getClass(), pk);
			}
			field2.setAccessible(true);
			Object fkFieldValue = field2.get(o);

			if ("java.util.List".equals(field.getType().getCanonicalName())) {	
				Field field3 = getField(o.getClass(), ed.getPrimaryKeys().iterator().next().getName());
				field3.setAccessible(true);
				if (field3.get(o) == null) {
					field.set(o, new ArrayList());
				} else {
					Map pk = new HashMap();
					pk.put(cd.getForeignKeyFields().get(0), field3.get(o));
					field.set(o, KNSServiceLocator.getBusinessObjectService().findMatching(cd.getTargetEntity(), pk));
				}
			} else {
				PersistableBusinessObject pbo = (PersistableBusinessObject) Class.forName(field.getType().getCanonicalName()).newInstance();
	            Map<String, Object> keys = MetadataManager.getPersistableBusinessObjectPrimaryKeyValuePairs(pbo);
				Field field3 = getField(pbo.getClass(), keys.keySet().iterator().next());
				field3.setAccessible(true);
				field3.set(pbo, fkFieldValue);
				field.set(o, KNSServiceLocator.getBusinessObjectService().retrieve(pbo));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private Field getField(Class clazz, String name) throws NoSuchFieldException {
		if (clazz.equals(Object.class)) {
			throw new NoSuchFieldException(name);
		}
		Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (Exception e) {}
		if (field == null) {
			field = getField(clazz.getSuperclass(), name);
		}
		return field;
	}

}
