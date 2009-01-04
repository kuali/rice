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
package org.kuali.rice.kim.lookup;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimAttributeDataComparator;
import org.kuali.rice.kim.dao.KimGroupDao;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupLookupableHelperServiceImpl  extends KualiLookupableHelperServiceImpl {

	// need this so kimtypeId value can be retained in 'rows'
	// 1st pass populate the grprows
	// 2nd pass for jsp, no populate, so return the existing one. 
	private List<Row> grpRows = new ArrayList<Row>();
	private List<Row> attrRows = new ArrayList<Row>();
	private KimGroupDao groupDao; 
	private String typeId;
	private AttributeDefinitionMap attrDefinitions;
	
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String,String> fieldValues) {
//    	String principalName = fieldValues.get("principalName");
//    	fieldValues.put("principalName","");
    	List<KimGroupImpl> groups = groupDao.getGroups(fieldValues);
        //List<KimGroupImpl> baseLookup = (List<KimGroupImpl>)super.getSearchResults(fieldValues);

        for (KimGroupImpl group : groups) {
        	if (!group.getGroupAttributes().isEmpty()) {
                sort(group.getGroupAttributes(), new KimAttributeDataComparator());
        	}
        }

        return groups;
    }

	@Override
	public List<Row> getRows() {
		List<Row> attributeRows = new ArrayList<Row>();
		if (getGrpRows().isEmpty()) {
			List<Row> rows = super.getRows();
			List<Row> returnRows = new ArrayList<Row>();
			for (Row row : rows) {
				Field field = (Field) row.getFields().get(0);
				if (field.getPropertyName().equals("kimTypeId")) {
					List<Field> fields = new ArrayList<Field>();
					Field typeField = new Field();
					typeField.setFieldLabel("Type");
					typeField.setPropertyName("kimTypeId");
					typeField.setFieldValidValues(getGroupTypeOptions());
					typeField.setFieldType(Field.DROPDOWN_REFRESH);
					fields.add(typeField);
					// fields.add(new Field("Type", "", Field.DROPDOWN_REFRESH,
					// false, "kimTypeId", "", getGroupTypeOptions(), null));
					returnRows.add(new Row(fields));

				} else {
					returnRows.add(row);
				}
			}
			// principalName
			List<Field> fields = new ArrayList<Field>();
			Field typeField = new Field();
			typeField.setFieldLabel("Principal Name");
			typeField.setPropertyName("principalName");
			typeField.setFieldType(Field.TEXT);
			typeField.setMaxLength(40);
			typeField.setSize(20);
			fields.add(typeField);
			returnRows.add(new Row(fields));

			setGrpRows(returnRows);
			setAttrRows(setupAttributeRows());
		} else {			 
			attributeRows = setupAttributeRows();
			if (attributeRows.isEmpty()) {
				setAttrRows(attributeRows);				
			} else if (CollectionUtils.isEmpty(getAttrRows())) {
				setAttrRows(attributeRows);				
			}
		}
		if (getAttrRows().isEmpty()) {
			setAttrDefinitions(new AttributeDefinitionMap());
			return getGrpRows();
		} else {
			List<Row> fullRows = new ArrayList<Row>();
			fullRows.addAll(getGrpRows());
			fullRows.addAll(getAttrRows());
			return fullRows;
		}
		
	}

	
	@Override
	public List<Column> getColumns() {
		List<Column> columns =  super.getColumns();
		int i = 0;
		// TODO : only add attributes columns if attributes is specified ?
		for (Map.Entry<String, AttributeDefinition> mapEntry : attrDefinitions.entrySet()) {
	        Column column = new Column();
        	AttributeDefinition attrDefn = mapEntry.getValue();
        	Class formatterClass;
        	String attrDefnId = attrDefn.getId();
			if (attrDefn instanceof KimDataDictionaryAttributeDefinition) {
				AttributeDefinition definition = ((KimDataDictionaryAttributeDefinition) attrDefn).getDataDictionaryAttributeDefinition();
		        column.setPropertyName("groupAttributes["+i+"].attributeValue");
		        //column.setPropertyName("groupAttributes");
		        String columnTitle = definition.getLabel();
		        column.setColumnTitle(columnTitle);
		        column.setMaxLength(definition.getMaxLength());		
		        formatterClass = definition.getFormatterClass();
			} else {
		        column.setPropertyName("groupAttributes["+i+"].attributeValue");
		        String columnTitle = attrDefn.getLabel();
		        column.setColumnTitle(columnTitle);
		        column.setMaxLength(attrDefn.getMaxLength());		
		        formatterClass = attrDefn.getFormatterClass();
				
			}
	        if (formatterClass != null) {
	            try {
	                column.setFormatter((Formatter) formatterClass.newInstance());
	            }
	            catch (InstantiationException e) {
	                LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
	                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
	            }
	            catch (IllegalAccessException e) {
	                LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
	                throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
	            }
	        }
	        i++;
	        columns.add(column);
		}
		return columns;
	}

	private List getGroupTypeOptions() {
		List options = new ArrayList();
		options.add(new KeyLabelPair("", ""));
		//TODO : this is not efficient
		List<KimTypeImpl> kimTypes = (List<KimTypeImpl>)getBusinessObjectService().findAll(KimTypeImpl.class);
		List<KimGroupImpl> kimGroups = (List<KimGroupImpl>)getBusinessObjectService().findAll(KimGroupImpl.class);
        List<String> typeIds = new ArrayList();
        for (KimGroupImpl group : kimGroups) {
        	if (!typeIds.contains(group.getKimTypeId())) {
        		typeIds.add(group.getKimTypeId());
        	}
        }
		for (KimTypeImpl kimType : kimTypes) {
			if (typeIds.contains(kimType.getKimTypeId())) {
				options.add(new KeyLabelPair(kimType.getKimTypeId(), kimType.getName()));
			}
		}
		return options;
	}

	private List<Row> setupAttributeRows() {
		List<Row> returnRows = new ArrayList<Row>();
		for (Row row : getGrpRows()) {
			Field field = (Field) row.getFields().get(0);
			if (field.getPropertyName().equals("kimTypeId") && StringUtils.isNotBlank(field.getPropertyValue())) {
				if (StringUtils.isBlank(getTypeId()) || !getTypeId().equals(field.getPropertyValue())) {
					setTypeId(field.getPropertyValue());
					setAttrRows(new ArrayList<Row>());
					Map pkMap = new HashMap();
					pkMap.put("kimTypeId", field.getPropertyValue());
					KimTypeImpl kimType = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pkMap);
					// TODO what if servicename is null.  also check other places that have similar issue
					// use default_service ?
					String serviceName = kimType.getKimTypeServiceName();
					if (StringUtils.isBlank(serviceName)) {
						serviceName = "kimTypeService";
					}
			        KimTypeService kimTypeService = (KimTypeService)KIMServiceLocator.getService(serviceName);
			        AttributeDefinitionMap definitions = kimTypeService.getAttributeDefinitions(kimType);
			        setAttrDefinitions(definitions);
		            for (Map.Entry<String, AttributeDefinition> mapEntry : definitions.entrySet()) {
		            	AttributeDefinition attrDefn = mapEntry.getValue();
				        List<Field> fields = new ArrayList<Field>();
						Field typeField = new Field();
						//String attrDefnId = mapEntry.getKey().substring(mapEntry.getKey().indexOf("."), mapEntry.getKey().length());
						String attrDefnId = attrDefn.getId();
						if (attrDefn instanceof KimDataDictionaryAttributeDefinition) {
							AttributeDefinition definition = ((KimDataDictionaryAttributeDefinition) attrDefn).getDataDictionaryAttributeDefinition();
							// if it is DD, then attrDefn.getLabel() is null; has to get from DDAttrdefn
							typeField.setFieldLabel(definition.getLabel());
							// with suffix  in case name is the same as bo property 
							typeField.setPropertyName(definition.getName()+"."+attrDefnId);
							if (definition.getControl().isSelect()) {
						        try {
						            KeyValuesFinder finder = (KeyValuesFinder) definition.getControl().getValuesFinderClass().newInstance();
							        typeField.setFieldValidValues(finder.getKeyValues());
							        typeField.setFieldType(Field.DROPDOWN);
						        }
						        catch (InstantiationException e) {
						            throw new RuntimeException(e.getMessage());
						        }
						        catch (IllegalAccessException e) {
						            throw new RuntimeException(e.getMessage());
						        }
							} else {
								typeField.setMaxLength(definition.getMaxLength());
								typeField.setSize(definition.getControl().getSize());
								typeField.setFieldType(Field.TEXT);
							}
						} else {
							typeField.setFieldLabel(attrDefn.getLabel());
							typeField.setPropertyName(attrDefn.getName()+attrDefnId);
							typeField.setMaxLength(attrDefn.getMaxLength());
							typeField.setSize(10);
							typeField.setFieldType(Field.TEXT);
						}
						fields.add(typeField);
						returnRows.add(new Row(fields));
		            }
				} else {
					return getAttrRows();
				}
			} else if (field.getPropertyName().equals("kimTypeId") && StringUtils.isBlank(field.getPropertyValue())) {
				setTypeId("");
			}
		}
		return returnRows;

	}
	
	public List<Row> getGrpRows() {
		return this.grpRows;
	}

	public void setGrpRows(List<Row> grpRows) {
		this.grpRows = grpRows;
	}

	public KimGroupDao getGroupDao() {
		return this.groupDao;
	}

	public void setGroupDao(KimGroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public AttributeDefinitionMap getAttrDefinitions() {
		return this.attrDefinitions;
	}

	public void setAttrDefinitions(AttributeDefinitionMap attrDefinitions) {
		this.attrDefinitions = attrDefinitions;
	}

	public List<Row> getAttrRows() {
		return this.attrRows;
	}

	public void setAttrRows(List<Row> attrRows) {
		this.attrRows = attrRows;
	}

	public String getTypeId() {
		return this.typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}


}
