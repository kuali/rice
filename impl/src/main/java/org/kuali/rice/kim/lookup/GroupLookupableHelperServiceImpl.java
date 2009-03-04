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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimAttributeDataComparator;
import org.kuali.rice.kim.dao.KimGroupDao;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimGroupTypeService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.keyvalues.IndicatorValuesFinder;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.CollectionFormatter;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;

import edu.emory.mathcs.backport.java.util.Collections;

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
    private static String KIM_TYPE_ID_PROPERTY_NAME = "kimTypeId";
	private List<Row> grpRows = new ArrayList<Row>();
	private List<Row> attrRows = new ArrayList<Row>();
	private KimGroupDao groupDao;
	private String typeId = "";
	private AttributeDefinitionMap attrDefinitions;
	private Map<String, String> groupTypeValuesCache = new HashMap<String, String>();
	private static List<KeyLabelPair> groupTypeCache = null;

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String,String> fieldValues) {
    	List<KimGroupImpl> groups = groupDao.getGroups(fieldValues);

        for (KimGroupImpl group : groups) {
        	if (!group.getGroupAttributes().isEmpty()) {
                sort(group.getGroupAttributes(), new KimAttributeDataComparator());
        	}
        }

        return groups;
    }

    @Override
    public boolean checkForAdditionalFields(Map fieldValues) {
        List<Row> attributeRows = setupAttributeRows(fieldValues);
        if (attributeRows.isEmpty()) {
            setAttrRows(attributeRows);
        } else if (CollectionUtils.isEmpty(getAttrRows())) {
            setAttrRows(attributeRows);
        }
        if (getAttrRows().size() > 0) {
            return true;
        }
        return false;
    }


	@Override
	public List<Row> getRows() {
		List<Row> attributeRows = new ArrayList<Row>();
		if (getGrpRows().isEmpty()) {
			List<Row> rows = super.getRows();
			List<Row> returnRows = new ArrayList<Row>();
			for (Row row : rows) {
				Field field = (Field) row.getFields().get(0);
				if (field.getPropertyName().equals(KIM_TYPE_ID_PROPERTY_NAME)) {
					List<Field> fields = new ArrayList<Field>();
					Field typeField = new Field();
					typeField.setFieldLabel("Type");
					typeField.setPropertyName(KIM_TYPE_ID_PROPERTY_NAME);
					typeField.setFieldValidValues(getGroupTypeOptions());
					typeField.setFieldType(Field.DROPDOWN_REFRESH);
					fields.add(typeField);
					returnRows.add(new Row(fields));

				} else {
					returnRows.add(row);
				}
			}
			// principalName
			List<Field> fields = new ArrayList<Field>();
			Field typeField = new Field();
			typeField.setFieldLabel("Principal Name");
			typeField.setPropertyName(KIMPropertyConstants.Person.PRINCIPAL_NAME);
			typeField.setFieldType(Field.TEXT);
			typeField.setMaxLength(40);
			typeField.setSize(20);
			typeField.setQuickFinderClassNameImpl("org.kuali.rice.kim.bo.Person");
			typeField.setFieldConversions( "principalName:principalName" );
			typeField.setLookupParameters( "principalName:principalName" );
			fields.add(typeField);
			returnRows.add(new Row(fields));

			setGrpRows(returnRows);
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
        for (Row row : attrRows) {
            for (Field field : row.getFields()) {
                Column newColumn = new Column();
                newColumn.setColumnTitle(field.getFieldLabel());
                newColumn.setMaxLength(field.getMaxLength());
                newColumn.setPropertyName(field.getPropertyName());
                newColumn.setFormatter((Formatter) field.getFormatter());
                columns.add(newColumn);
            }
        }
        return columns;
	}

    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        setBackLocation((String) lookupForm.getFieldsForLookup().get(KNSConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KNSConstants.DOC_FORM_KEY));
        Collection displayList;

        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        HashMap<String,Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List returnKeys = getReturnKeys();
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();
            if(element instanceof PersistableBusinessObject){
                lookupForm.setLookupObjectId(((PersistableBusinessObject)element).getObjectId());
            }

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);

            String actionUrls = getActionUrls(element, pkNames, businessObjectRestrictions);
            //Fix for JIRA - KFSMI-2417
            if("".equals(actionUrls)){
                actionUrls = ACTION_URLS_EMPTY;
            }

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KNSConstants.EMPTY_STRING;
                Object prop = null;
                boolean skipPropTypeCheck = false;
                if (col.getPropertyName().matches("\\w+\\.\\d+$")) {
                    String id = col.getPropertyName().substring(col.getPropertyName().lastIndexOf('.') + 1); //.split("\\d+$"))[1];
                    prop = ((KimGroupImpl)element).getGroupAttributeById(id);
                }
                if (prop == null) {
                    prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
                } else {
                    skipPropTypeCheck = true;
                }

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(col.getPropertyName());
                if ( propClass == null /*&& !skipPropTypeCheck*/) {
                    try {
                        propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
                        propertyTypes.put( col.getPropertyName(), propClass );
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                    }
                }

                // formatters
                if (prop != null) {
                    // for Booleans, always use BooleanFormatter
                    if (prop instanceof Boolean) {
                        formatter = new BooleanFormatter();
                    }

                    // for Dates, always use DateFormatter
                    if (prop instanceof Date) {
                        formatter = new DateFormatter();
                    }

                    // for collection, use the list formatter if a formatter hasn't been defined yet
                    if (prop instanceof Collection && formatter == null) {
                    formatter = new CollectionFormatter();
                    }

                    if (formatter != null) {
                        propValue = (String) formatter.format(prop);
                    }
                    else {
                        propValue = prop.toString();
                        if (col.getPropertyName().equals(KIM_TYPE_ID_PROPERTY_NAME)) {
                            propValue = groupTypeValuesCache.get(prop.toString());
                        }
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));

                }
            }

            ResultRow row = new ResultRow(columns, returnUrl.constructCompleteHtmlTag(), actionUrls);
            row.setRowId(returnUrl.getName());
            row.setReturnUrlHtmlData(returnUrl);
            // because of concerns of the BO being cached in session on the ResultRow,
            // let's only attach it when needed (currently in the case of export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            if(element instanceof PersistableBusinessObject){
                row.setObjectId((((PersistableBusinessObject)element).getObjectId()));
            }


            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }

	@SuppressWarnings("unchecked")
	private List<KeyLabelPair> getGroupTypeOptions() {
		if ( groupTypeCache == null ) {
			List<KeyLabelPair> options = new ArrayList<KeyLabelPair>();
			groupTypeValuesCache = new HashMap<String, String>();
			options.add(new KeyLabelPair("", ""));
			options.add(new KeyLabelPair("1", "Default"));

			List<KimTypeImpl> kimGroupTypes = (List<KimTypeImpl>)getBusinessObjectService().findAll(KimTypeImpl.class);
			// get the distinct list of type IDs from all groups in the system
	        for (KimTypeImpl kimType : kimGroupTypes) {
                if (hasGroupTypeService(kimType) && groupTypeValuesCache.get(kimType.getKimTypeId()) == null) {
                    String value = kimType.getNamespaceCode().trim() + KNSConstants.FIELD_CONVERSION_PAIR_SEPARATOR + kimType.getName().trim();
                    options.add(new KeyLabelPair(kimType.getKimTypeId(), value));
                    groupTypeValuesCache.put(kimType.getKimTypeId(), value);
                }
	        }
	        Collections.sort(options, new Comparator<KeyLabelPair>() {
	           public int compare(KeyLabelPair k1, KeyLabelPair k2) {
	               return k1.getLabel().compareTo(k2.getLabel());
	           }
	        });
			groupTypeCache = options;
		}
		return groupTypeCache;
	}

	private List<Row> setupAttributeRows(Map fieldValues) {
		List<Row> returnRows = new ArrayList<Row>();
		for (Row row : getGrpRows()) {
			Field field = (Field) row.getFields().get(0);
			if (field.getPropertyName().equals(KIM_TYPE_ID_PROPERTY_NAME) && StringUtils.isNotBlank(field.getPropertyValue())) {
				if (!StringUtils.isBlank(getTypeId()) || !getTypeId().equals(field.getPropertyValue())) {
					setTypeId(field.getPropertyValue());
					setAttrRows(new ArrayList<Row>());
					Map<String,Object> pkMap = new HashMap<String,Object>();
					pkMap.put(KIM_TYPE_ID_PROPERTY_NAME, field.getPropertyValue());
					KimTypeImpl kimType = (KimTypeImpl)getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, pkMap);
					// TODO what if servicename is null.  also check other places that have similar issue
					// use default_service ?
					String serviceName = kimType.getKimTypeServiceName();
					if (StringUtils.isBlank(serviceName)) {
						serviceName = "kimTypeService";
					}
			        KimTypeService kimTypeService = (KimTypeService)KIMServiceLocator.getService(serviceName);
			        AttributeDefinitionMap definitions = kimTypeService.getAttributeDefinitions(kimType.getKimTypeId());
			        setAttrDefinitions(definitions);
		            for (AttributeDefinition definition  : definitions.values()) {
				        List<Field> fields = new ArrayList<Field>();
						Field typeField = new Field();

						String attrDefnId = getAttrDefnId(definition);
						typeField.setFieldLabel(definition.getLabel());
						typeField.setPropertyName(definition.getName()+"."+attrDefnId);
						typeField.setPropertyValue(fieldValues.get(typeField.getPropertyName()));
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
						} else if (definition.getControl().isText()){
							typeField.setMaxLength(definition.getMaxLength());
							if (definition.getControl().getSize() != null) {
							    typeField.setSize(definition.getControl().getSize());
							}
						    typeField.setFieldType(Field.TEXT);
						} else if (definition.getControl().isRadio()) {
						    try {
                                KeyValuesFinder finder = (KeyValuesFinder) definition.getControl().getValuesFinderClass().newInstance();
                                typeField.setFieldValidValues(finder.getKeyValues());
                                typeField.setFieldType(Field.RADIO);
                            }
                            catch (InstantiationException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                            catch (IllegalAccessException e) {
                                throw new RuntimeException(e.getMessage());
                            }
						} else if (definition.getControl().isCheckbox()) {
						    KeyValuesFinder finder = (KeyValuesFinder)new IndicatorValuesFinder();
                            typeField.setFieldValidValues(finder.getKeyValues());
                            typeField.setFieldType(Field.RADIO);
						    //typeField.setFieldType(Field.CHECKBOX);
						} else if (definition.getControl().isHidden()) {
						    typeField.setFieldType(Field.HIDDEN);
						} else if (definition.getControl().isLookupReadonly()) {
						    typeField.setFieldType(Field.LOOKUP_READONLY);
						} else if (definition.getControl().isTextarea()) {
						    typeField.setMaxLength(definition.getMaxLength());
                            if (definition.getControl().getSize() != null) {
                                typeField.setSize(definition.getControl().getSize());
                            }
                            typeField.setFieldType(Field.TEXT_AREA);
						}
						fields.add(typeField);
						returnRows.add(new Row(fields));
		            }
				} else {
					return getAttrRows();
				}
			} else if (field.getPropertyName().equals(KIM_TYPE_ID_PROPERTY_NAME) && StringUtils.isBlank(field.getPropertyValue())) {
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

    static boolean hasGroupTypeService(KimTypeImpl kimTypeImpl){
        return hasGroupTypeService(KimCommonUtils.getKimTypeService(kimTypeImpl));
    }

    static boolean hasGroupTypeService(KimTypeService kimTypeService){
        return kimTypeService instanceof KimGroupTypeService;
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

    @Override
    public void performClear(LookupForm lookupForm) {
        super.performClear(lookupForm);
        this.attrRows = new ArrayList<Row>();
    }
}
