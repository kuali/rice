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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleLookupableHelperServiceImpl   extends KualiLookupableHelperServiceImpl {

	// need this so kimtypeId value can be retained in 'rows'
	// 1st pass populate the grprows
	// 2nd pass for jsp, no populate, so return the existing one. 
	private List<Row> roleRows = new ArrayList<Row>();
	private List<Row> attrRows = new ArrayList<Row>();
	private KimRoleDao roleDao; 
	private String typeId;
	private AttributeDefinitionMap attrDefinitions;
	
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String,String> fieldValues) {
//    	String principalName = fieldValues.get("principalName");
//    	fieldValues.put("principalName","");
        String kimTypeId = null;
        for (Map.Entry<String,String> entry : fieldValues.entrySet()) {
        	if (entry.getKey().equals("kimTypeId")) {
        		kimTypeId=entry.getValue();
        		break;
        	}
        }
  //  	List<KimRoleImpl> roles = roleDao.getRoles(fieldValues, kimTypeId);
        List<KimRoleImpl> baseLookup = (List<KimRoleImpl>)super.getSearchResults(fieldValues);

        return baseLookup;
    }

	private static List<KeyLabelPair>  roleTypeCache = null;


	@SuppressWarnings("unchecked")
	private List<KeyLabelPair> getRoleTypeOptions() {
		if ( roleTypeCache == null ) {
			List<KeyLabelPair> options = new ArrayList<KeyLabelPair>();
			options.add(new KeyLabelPair("", ""));
			//TODO : this is not efficient
			List<KimTypeImpl> kimTypes = (List<KimTypeImpl>)getBusinessObjectService().findAll(KimTypeImpl.class);
			List<KimRoleImpl> kimRoles = (List<KimRoleImpl>)getBusinessObjectService().findAll(KimRoleImpl.class);
	        List<String> typeIds = new ArrayList<String>();
	        for (KimRoleImpl role : kimRoles) {
	        	if (!typeIds.contains(role.getKimTypeId())) {
	        		typeIds.add(role.getKimTypeId());
	        	}
	        }
			for (KimTypeImpl kimType : kimTypes) {
				if (typeIds.contains(kimType.getKimTypeId())) {
					options.add(new KeyLabelPair(kimType.getKimTypeId(), kimType.getName()));
				}
			}
			roleTypeCache = options;
		}
		return roleTypeCache;
	}

	private List<Row> setupAttributeRows() {
		List<Row> returnRows = new ArrayList<Row>();
		for (Row row : getRoleRows()) {
			Field field = (Field) row.getFields().get(0);
			if (field.getPropertyName().equals("kimTypeId") && StringUtils.isNotBlank(field.getPropertyValue())) {
				if (StringUtils.isBlank(getTypeId()) || !getTypeId().equals(field.getPropertyValue())) {
					setTypeId(field.getPropertyValue());
					setAttrRows(new ArrayList<Row>());
										
					Map<String,Object> pkMap = new HashMap<String,Object>();
					pkMap.put("kimTypeId", field.getPropertyValue());
					KimTypeImpl kimType = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pkMap);
					// TODO what if servicename is null.  also check other places that have similar issue
					// use default_service ?
					String serviceName = kimType.getKimTypeServiceName();
					if (serviceName == null) {
						serviceName = "kimTypeService";
					}
			        KimTypeService kimTypeService = (KimTypeService)KIMServiceLocator.getService(serviceName);
			        AttributeDefinitionMap definitions = kimTypeService.getAttributeDefinitions(kimType.getKimTypeId());
			        setAttrDefinitions(definitions);
		            for ( AttributeDefinition definition : definitions.values()) {
				        List<Field> fields = new ArrayList<Field>();
						Field typeField = new Field();
						//String attrDefnId = mapEntry.getKey().substring(mapEntry.getKey().indexOf("."), mapEntry.getKey().length());
//						String attrDefnId = definition.getId();
						String attrDefnId = getAttrDefnId(definition);
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
    private String getAttrDefnId(AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	} else {
    		return ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();

    	}
    }
	
	public List<Row> getRoleRows() {
		return this.roleRows;
	}

	public void setRoleRows(List<Row> roleRows) {
		this.roleRows = roleRows;
	}

	public KimRoleDao getRoleDao() {
		return this.roleDao;
	}

	public void setRoleDao(KimRoleDao roleDao) {
		this.roleDao = roleDao;
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

	@Override
	public List<Row> getRows() {
		List<Row> attributeRows = new ArrayList<Row>();
		if (getRoleRows().isEmpty()) {
			List<Row> rows = super.getRows();
			List<Row> returnRows = new ArrayList<Row>();
			for (Row row : rows) {
				Field field = (Field) row.getFields().get(0);
				if (field.getPropertyName().equals("kimTypeId")) {
					List<Field> fields = new ArrayList<Field>();
					Field typeField = new Field();
					typeField.setFieldLabel("Type");
					typeField.setPropertyName("kimTypeId");
					typeField.setFieldValidValues(getRoleTypeOptions());
					typeField.setFieldType(Field.DROPDOWN_REFRESH);
					fields.add(typeField);
					// fields.add(new Field("Type", "", Field.DROPDOWN_REFRESH,
					// false, "kimTypeId", "", getGroupTypeOptions(), null));
					returnRows.add(new Row(fields));

				} else {
					returnRows.add(row);
				}
			}
			setRoleRows(returnRows);
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
			return getRoleRows();
		} else {
			List<Row> fullRows = new ArrayList<Row>();
			fullRows.addAll(getRoleRows());
			fullRows.addAll(getAttrRows());
			return fullRows;
		}
		
	}

	@Override
	protected List<? extends BusinessObject> getSearchResultsHelper(
			Map<String, String> fieldValues, boolean unbounded) {
        List searchResults;
    	Map<String,String> nonBlankFieldValues = new HashMap<String, String>();
    	boolean includeAttr = false;
    	for (String fieldName : fieldValues.keySet()) {
    		if (StringUtils.isNotBlank(fieldValues.get(fieldName)) ) {
    			nonBlankFieldValues.put(fieldName, fieldValues.get(fieldName));
    			if (fieldName.contains(".")) {
    				includeAttr = true;
    			}
    		}
    	}

    	if (includeAttr) {
        	ModuleService eboModuleService = KNSServiceLocator.getKualiModuleService().getResponsibleModuleService( getBusinessObjectClass() );
        	BusinessObjectEntry ddEntry = eboModuleService.getExternalizableBusinessObjectDictionaryEntry(getBusinessObjectClass());
        	Map<String,String> filteredFieldValues = new HashMap<String, String>();
        	for (String fieldName : nonBlankFieldValues.keySet()) {
        		if (ddEntry.getAttributeNames().contains(fieldName) || fieldName.contains(".")) {
        			filteredFieldValues.put(fieldName, nonBlankFieldValues.get(fieldName));
        		}
        	}
        	searchResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(getBusinessObjectClass(), (Map)filteredFieldValues, unbounded);

    	} else {
    		searchResults = super.getSearchResultsHelper(fieldValues, unbounded);
    	}
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;

	}
	
	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
	 */
	@Override
	public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
		HtmlData inqUrl = super.getInquiryUrl(bo, propertyName);
	    String href = ((AnchorHtmlData)inqUrl).getHref();
	    if (StringUtils.isNotBlank(href) && href.indexOf("&roleId=")!=-1) {
		    int idx1 = href.indexOf("&roleId=");
		    int idx2 = href.indexOf("&", idx1+1);
		    if (idx2 < 0) {
		    	idx2 = href.length();
		    }
		    ((AnchorHtmlData)inqUrl).setHref("../kim/identityManagementRoleDocument.do?command=initiate&docTypeName=IdentityManagementRoleDocument"+href.substring(idx1, idx2));
	    }
	    return inqUrl;
	}

}
