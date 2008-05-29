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
package org.kuali.rice.jpa.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.jpa.annotations.Sequence;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MetadataManager {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MetadataManager.class);
	
	private static Map<Class, EntityDescriptor> entitesByClass = new HashMap<Class, EntityDescriptor>();
	private static Map<String, EntityDescriptor> entitesByName = new HashMap<String, EntityDescriptor>();
	
	private MetadataManager() {}

	public static EntityDescriptor getEntityDescriptor(Class clazz) {
		if (clazz != null && clazz.getName().indexOf("$$EnhancerByCGLIB") > -1) {
			try {
				clazz = Class.forName(clazz.getName().substring(0, clazz.getName().indexOf("$$EnhancerByCGLIB")));
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}

		EntityDescriptor entityDescriptor = addEntity(clazz);
		return entityDescriptor;
	}
	
	public static Map<String, Object> getPersistableBusinessObjectPrimaryKeyValuePairs(Object object) {
		Map<String, Object> pks = new HashMap<String, Object>();
		EntityDescriptor descriptor = getEntityDescriptor(object.getClass());
		for (FieldDescriptor fieldDescriptor : descriptor.getPrimaryKeys()) {
			try {
				Field field = getField(object.getClass(), fieldDescriptor.getName());
				field.setAccessible(true);
				pks.put(fieldDescriptor.getName(), field.get(object));
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return pks;
	}
	
	private static Field getField(Class clazz, String name) throws NoSuchFieldException {
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
	
	private static EntityDescriptor addEntity(Class clazz) {
		EntityDescriptor entity = entitesByClass.get(clazz); 
		if (entity == null) {
			entity = construct(clazz);
			if (entity != null) {
				entitesByClass.put(entity.getClazz(), entity);
				entitesByName.put(entity.getName(), entity);
			}
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	private static EntityDescriptor construct(Class clazz) {
		if (!clazz.isAnnotationPresent(Entity.class)) {
			return null;
		}
		
		// Determine the base entity metadata
		EntityDescriptor entityDescriptor = new EntityDescriptor();
		entityDescriptor.setClazz(clazz);
		String defaultName = clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1);
		Entity entity = (Entity) clazz.getAnnotation(Entity.class);
		if (StringUtils.isBlank(entity.name())) {
			entityDescriptor.setName(defaultName);
		} else {
			entityDescriptor.setName(entity.name());
		}
		if (clazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) clazz.getAnnotation(Table.class);
			entityDescriptor.setTable(table.name());
		} else {
			entityDescriptor.setTable(defaultName);
		}
		if (clazz.isAnnotationPresent(IdClass.class)) {
			entityDescriptor.setIdClass(clazz.getAnnotation(IdClass.class).getClass());
		}
		if (clazz.isAnnotationPresent(Sequence.class)) {
			entityDescriptor.setSequence((Sequence)clazz.getAnnotation(Sequence.class));
		}		
		
		// Check for an "extension"
		try {
			Class extensionClass = Class.forName(clazz.getName() + "Extension");
			OneToOneDescriptor descriptor = new OneToOneDescriptor();
			descriptor.setCascade(new CascadeType[] { CascadeType.PERSIST });
			descriptor.setAttributeName("extension");
			descriptor.setTargetEntity(extensionClass);
			descriptor.setMappedBy("extension");
			EntityDescriptor extensionDescriptor = MetadataManager.getEntityDescriptor(extensionClass);
			for (FieldDescriptor fd : extensionDescriptor.getPrimaryKeys()) {
				descriptor.addFkField(fd.getName());
			}
			entityDescriptor.add(descriptor);
			FieldDescriptor extension = new FieldDescriptor();
			extension.setName("extension");
			extension.setClazz(extensionClass);
			entityDescriptor.add(extension);
		} catch (Exception e) {}
		
		
		List<Class> classes = new ArrayList<Class>();
		classes.add(clazz);
		Class c = clazz;
		while (!c.getSuperclass().equals(Object.class)) {
			c = c.getSuperclass();
			classes.add(c);
		}
		Collections.reverse(classes);
		
		// Determine the field/relationship metadata for all classes in the clazz hierarchy
		for (Class temp : classes) {
			extractFieldMetadata(temp, entityDescriptor);
			if (temp.isAnnotationPresent(AttributeOverrides.class)) {
				for (AttributeOverride override : ((AttributeOverrides)temp.getAnnotation(AttributeOverrides.class)).value()) {
					entityDescriptor.getFieldByName(override.name()).setColumn(override.column().name());
				}
			}
			if (temp.isAnnotationPresent(AttributeOverride.class)) {
				AttributeOverride override = (AttributeOverride) temp.getAnnotation(AttributeOverride.class);
				entityDescriptor.getFieldByName(override.name()).setColumn(override.column().name());					
			}
		}
				
		return entityDescriptor;
	}

	private static void extractFieldMetadata(Class clazz, EntityDescriptor entityDescriptor) {
    	// Don't want to get parent fields if overridden in children since we are walking the tree from child to parent
		Set<String> cachedFields = new HashSet<String>(); 
		do {
			for (Field field : clazz.getDeclaredFields()) {
				if (cachedFields.contains(field.getName())) {
					continue;
				}
				cachedFields.add(field.getName());
				
				int mods = field.getModifiers();
				if (Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods) || field.isAnnotationPresent(Transient.class)) {
					continue;
				}

				// Basic Fields
				FieldDescriptor fieldDescriptor = new FieldDescriptor();
				fieldDescriptor.setClazz(field.getType());
				fieldDescriptor.setName(field.getName());
				if (field.isAnnotationPresent(Id.class)) {
					fieldDescriptor.setId(true);
				}
				if (field.isAnnotationPresent(Column.class)) {
					Column column = field.getAnnotation(Column.class);
					fieldDescriptor.setColumn(column.name());
					fieldDescriptor.setInsertable(column.insertable());
					fieldDescriptor.setLength(column.length());
					fieldDescriptor.setNullable(column.nullable());
					fieldDescriptor.setPrecision(column.precision());
					fieldDescriptor.setScale(column.scale());
					fieldDescriptor.setUnique(column.unique());
					fieldDescriptor.setUpdateable(column.updatable());
				} else {
					fieldDescriptor.setColumn(field.getName());
				}
				if (field.isAnnotationPresent(Version.class)) {
					fieldDescriptor.setVersion(true);
				}
				if (field.isAnnotationPresent(Lob.class)) {
					fieldDescriptor.setLob(true);
				}
				if (field.isAnnotationPresent(Temporal.class)) {
					fieldDescriptor.setTemporal(true);
					fieldDescriptor.setTemporalType(field.getAnnotation(Temporal.class).value());
				}				

				// Relationships
				if (field.isAnnotationPresent(OneToOne.class)) {
					OneToOneDescriptor descriptor = new OneToOneDescriptor();
					OneToOne relation = field.getAnnotation(OneToOne.class);
					descriptor.setAttributeName(field.getName());
					if (relation.targetEntity().equals(void.class)) {
						descriptor.setTargetEntity(field.getType());
					} else {
						descriptor.setTargetEntity(relation.targetEntity());
					}
					descriptor.setCascade(relation.cascade());
					descriptor.setFetch(relation.fetch());
					descriptor.setMappedBy(relation.mappedBy());
					descriptor.setOptional(relation.optional());
					if (field.isAnnotationPresent(JoinColumn.class)) {
						JoinColumn jc = field.getAnnotation(JoinColumn.class);
						descriptor.addJoinColumnDescriptor(constructJoinDescriptor(jc));
						descriptor.addFkField(entityDescriptor.getFieldByColumnName(jc.name()).getName());
						descriptor.setInsertable(jc.insertable());
						descriptor.setUpdateable(jc.updatable());					
					}
					if (field.isAnnotationPresent(JoinColumns.class)) {
						JoinColumns jcs = field.getAnnotation(JoinColumns.class);
						for (JoinColumn jc : jcs.value()) {
							descriptor.addJoinColumnDescriptor(constructJoinDescriptor(jc));
							descriptor.addFkField(entityDescriptor.getFieldByColumnName(jc.name()).getName());
							descriptor.setInsertable(jc.insertable());
							descriptor.setUpdateable(jc.updatable());
						} 
					}
					entityDescriptor.add(descriptor);
				}

				if (field.isAnnotationPresent(OneToMany.class)) {
					OneToManyDescriptor descriptor = new OneToManyDescriptor();
					OneToMany relation = field.getAnnotation(OneToMany.class);
					descriptor.setAttributeName(field.getName());
					if (relation.targetEntity().equals(void.class)) {
						descriptor.setTargetEntity((Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
					} else {
						descriptor.setTargetEntity(relation.targetEntity());
					}
					descriptor.setCascade(relation.cascade());
					descriptor.setFetch(relation.fetch());
					descriptor.setMappedBy(relation.mappedBy());
					EntityDescriptor mappedBy = MetadataManager.getEntityDescriptor(descriptor.getTargetEntity());
					ObjectDescriptor od = mappedBy.getObjectDescriptorByName(descriptor.getMappedBy());
					if (od != null) {
						for (String fk : od.getForeignKeyFields()) {				
							descriptor.addFkField(fk);
						}
					}
					if (field.isAnnotationPresent(JoinTable.class)) {
						JoinTable jt = field.getAnnotation(JoinTable.class);
						for (JoinColumn jc : jt.joinColumns()) {
							descriptor.addFkField(entityDescriptor.getFieldByColumnName(jc.name()).getName());
							descriptor.setInsertable(jc.insertable());
							descriptor.setUpdateable(jc.updatable());
						} 
						for (JoinColumn jc : jt.inverseJoinColumns()) {
							descriptor.setInsertable(jc.insertable());
							descriptor.setUpdateable(jc.updatable());
							// TODO: Should we add inverse join columns?
						} 
					}
					entityDescriptor.add(descriptor);
				}

				if (field.isAnnotationPresent(ManyToOne.class)) {
					ManyToOne relation = field.getAnnotation(ManyToOne.class);
					ManyToOneDescriptor descriptor = new ManyToOneDescriptor();
					descriptor.setAttributeName(field.getName());
					if (relation.targetEntity().equals(void.class)) {
						descriptor.setTargetEntity(field.getType());
					} else {
						descriptor.setTargetEntity(relation.targetEntity());
					}
					descriptor.setCascade(relation.cascade());
					descriptor.setFetch(relation.fetch());
					descriptor.setOptional(relation.optional());
					if (field.isAnnotationPresent(JoinColumn.class)) {
						JoinColumn jc = field.getAnnotation(JoinColumn.class);
						descriptor.addJoinColumnDescriptor(constructJoinDescriptor(jc));
						descriptor.addFkField(entityDescriptor.getFieldByColumnName(jc.name()).getName());
						descriptor.setInsertable(jc.insertable());
						descriptor.setUpdateable(jc.updatable());
					}
					if (field.isAnnotationPresent(JoinColumns.class)) {
						JoinColumns jcs = field.getAnnotation(JoinColumns.class);
						for (JoinColumn jc : jcs.value()) {
							descriptor.addJoinColumnDescriptor(constructJoinDescriptor(jc));
							descriptor.addFkField(entityDescriptor.getFieldByColumnName(jc.name()).getName());
							descriptor.setInsertable(jc.insertable());
							descriptor.setUpdateable(jc.updatable());
						} 
					}
					entityDescriptor.add(descriptor);
				}

				if (field.isAnnotationPresent(ManyToMany.class)) {
					ManyToManyDescriptor descriptor = new ManyToManyDescriptor();
					ManyToMany relation = field.getAnnotation(ManyToMany.class);
					descriptor.setAttributeName(field.getName());
					if (relation.targetEntity().equals(void.class)) {
						descriptor.setTargetEntity((Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
					} else {
						descriptor.setTargetEntity(relation.targetEntity());
					}
					descriptor.setCascade(relation.cascade());
					descriptor.setFetch(relation.fetch());
					descriptor.setMappedBy(relation.mappedBy());
					if (field.isAnnotationPresent(JoinTable.class)) {
						JoinTable jt = field.getAnnotation(JoinTable.class);
						descriptor.setJoinTableName(jt.name());
						for (JoinColumn jc : jt.joinColumns()) {
							descriptor.addJoinColumnDescriptor(constructJoinDescriptor(jc));
							descriptor.addFkField(entityDescriptor.getFieldByColumnName(jc.name()).getName());
							descriptor.setInsertable(jc.insertable());
							descriptor.setUpdateable(jc.updatable());
						} 
						for (JoinColumn jc : jt.inverseJoinColumns()) {
							descriptor.addInverseJoinColumnDescriptor(constructJoinDescriptor(jc));
							descriptor.setInsertable(jc.insertable());
							descriptor.setUpdateable(jc.updatable());
							// TODO: Should we add inverse join columns?
						} 
					}
					entityDescriptor.add(descriptor);						
				}

				// Add the field to the entity
				entityDescriptor.add(fieldDescriptor);
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null && !(clazz.equals(Object.class)));
	}

	private static JoinColumnDescriptor constructJoinDescriptor(JoinColumn jc) {
		JoinColumnDescriptor join = new JoinColumnDescriptor();
		if (StringUtils.isBlank(jc.name())) {
			// TODO: Implement default name
			// See: http://www.oracle.com/technology/products/ias/toplink/jpa/resources/toplink-jpa-annotations.html#JoinColumn
			throw new RuntimeException("Default name for Join Column not yet implemented!");
		} else {
			join.setName(jc.name());
		}
		join.setInsertable(jc.insertable());
		join.setNullable(jc.nullable());
		join.setUnique(jc.unique());
		join.setUpdateable(jc.updatable());
		return join;
	}
	
}
