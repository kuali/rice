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
package org.kuali.rice.kns.uif.service.impl;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.core.util.type.TypeUtils;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.RelationshipDefinition;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.UifPropertyPaths;
import org.kuali.rice.kns.uif.container.LookupView;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.GeneratedField;
import org.kuali.rice.kns.uif.service.LookupViewHelperService;
import org.kuali.rice.kns.uif.util.ViewModelUtils;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.spring.controller.MaintenanceDocumentController;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupViewHelperServiceImpl extends ViewHelperServiceImpl implements LookupViewHelperService {
	protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupViewHelperServiceImpl.class);

	protected static final String TITLE_ACTION_URL_PREPENDTEXT_PROPERTY = "title.action.url.value.prependtext";
	protected static final String ACTION_URLS_CHILDREN_SEPARATOR = "&nbsp;|&nbsp;";
	protected static final String ACTION_URLS_CHILDREN_STARTER = "&nbsp;[";
	protected static final String ACTION_URLS_CHILDREN_END = "]";
	protected static final String ACTION_URLS_SEPARATOR = "&nbsp;&nbsp;";
	protected static final String ACTION_URLS_EMPTY = "&nbsp;";

	protected LookupService lookupService;
	// TODO delyea - investigate if encryptionService is even needed due to new spring binding
	protected EncryptionService encryptionService;
	protected BusinessObjectDictionaryService businessObjectDictionaryService;
	protected BusinessObjectMetaDataService businessObjectMetaDataService;
	private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
	protected BusinessObjectAuthorizationService businessObjectAuthorizationService;
	protected PersistenceStructureService persistenceStructureService;

	protected Class<?> dataObjectClass;
	protected String backLocation;
	protected String docFormKey;
	protected String referencesToRefresh;
	protected boolean searchUsingOnlyPrimaryKeyValues = false;
	protected Map<String, String> fieldConversions;
	protected Map parameters;
	protected List<String> readOnlyFieldsList;
	private List<String> defaultSortAttributeNames;
	// TODO delyea: where to take into account the sort ascending value (old KNS appeared to ignore?)
	protected boolean sortAscending;

	/**
	 * Default Constructor
	 * 
	 */
	public LookupViewHelperServiceImpl() {
		super();
	}

	/**
	 * Initialization of Lookupable requires that the business object class be set for the {@link #initializeAttributeFieldFromDataDictionary(View, AttributeField)} method
	 * 
	 * @see org.kuali.rice.kns.uif.service.impl.ViewHelperServiceImpl#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		if (!LookupView.class.isAssignableFrom(view.getClass())) {
			throw new IllegalArgumentException("View class '" + view.getClass() + " is not assignable from the '" + LookupView.class + "'");
		}
		LookupView lookupView = (LookupView) view;
		initializeLookupViewHelperService(lookupView);
		super.performInitialization(view);
	}

	public void initializeLookupViewHelperService(LookupView lookupView) {
		setDefaultSortAttributeNames(lookupView.getDefaultSortAttributeNames());
		setSortAscending(lookupView.isSortAscending());
		setDataObjectClass(lookupView.getDataObjectClassName());
	}

	/**
	 * After initial Data Dictionary initialization of an {@link AttributeField} , the binding path will need to be changed if the initial 'binding object path' is set to
	 * {@link UifConstants.LookupModelPropertyNames#CRITERIA_FIELDS}. The exact 'binding path' that needs to be set will be to convert a nested property path to one that allows for mapping to a
	 * {@link Map} object.
	 * 
	 * eg: If the existing 'binding object path' is set to 'criteriaMap' and the 'binding name' is 'subAccount' then the default 'binding path' will be 'criteriaMap.subAccount'. In the case of lookups
	 * we need the 'binding path' to be set to 'criteriaMap[subAccount]'.
	 * 
	 * @see org.kuali.rice.kns.uif.service.impl.ViewHelperServiceImpl#initializeAttributeFieldFromDataDictionary(org.kuali.rice.kns.uif.container.View, org.kuali.rice.kns.uif.field.AttributeField)
	 */
	@Override
	protected void initializeAttributeFieldFromDataDictionary(View view, AttributeField field) {
		super.initializeAttributeFieldFromDataDictionary(view, field);
		if (StringUtils.equals(UifPropertyPaths.CRITERIA_FIELDS, field.getBindingInfo().getBindingObjectPath())) {
			field.getBindingInfo().setBindingPath(field.getBindingInfo().getBindingObjectPath() + "[" + field.getBindingInfo().getBindingName() + "]");
		}
	}

	/**
	 * When the dictionary model class is retrieved for an AttributeField where the BindingObjectPath is equal to the {@link org.kuali.rice.kns.web.spring.form.LookupForm} criteria fields object...
	 * the BusinessObjectClass needs to be returned to fetch the proper data dictionary entry.
	 * 
	 * @see org.kuali.rice.kns.uif.service.impl.ViewHelperServiceImpl#getDictionaryModelClass(org.kuali.rice.kns.uif.container.View, org.kuali.rice.kns.uif.field.AttributeField)
	 */
	@Override
	protected Class<?> getDictionaryModelClass(View view, AttributeField field) {
		// if the field binding object path matches the map name on the
		// LookupForm model then use BO class rather than looking up dictionary
		// model class
		if (StringUtils.equals(UifPropertyPaths.CRITERIA_FIELDS, field.getBindingInfo().getBindingObjectPath())) {
			return getDataObjectClass();
		}
		return ViewModelUtils.getPropertyType(view, field.getBindingInfo().getBindingObjectPath());
	}

	public void validateSearchParameters(Map<String, String> fieldValues) {
		List<String> lookupFieldAttributeList = null;
		if (getBusinessObjectMetaDataService().isLookupable(getDataObjectClass())) {
			lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(getDataObjectClass());
		}
		if (lookupFieldAttributeList == null) {
			throw new RuntimeException("Lookup not defined for business object " + getDataObjectClass());
		}
		for (Iterator<String> iter = lookupFieldAttributeList.iterator(); iter.hasNext();) {
			String attributeName = iter.next();
			if (fieldValues.containsKey(attributeName)) {
				// get label of attribute for message
				String attributeLabel = getDataDictionaryService().getAttributeLabel(getDataObjectClass(), attributeName);

				String attributeValue = fieldValues.get(attributeName);

				// check for required if field does not have value
				if (StringUtils.isBlank(attributeValue)) {
					if ((getBusinessObjectDictionaryService().getLookupAttributeRequired(getDataObjectClass(), attributeName)).booleanValue()) {
						GlobalVariables.getMessageMap().putError(attributeName, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
					}
				}
				validateSearchParameterWildcardAndOperators(attributeName, attributeValue);
			}
		}

		if (GlobalVariables.getMessageMap().hasErrors()) {
			throw new ValidationException("errors in search criteria");
		}
	}

	protected void validateSearchParameterWildcardAndOperators(String attributeName, String attributeValue) {
		if (StringUtils.isBlank(attributeValue))
			return;

		// make sure a wildcard/operator is in the value
		boolean found = false;
		for (int i = 0; i < KNSConstants.QUERY_CHARACTERS.length; i++) {
			String queryCharacter = KNSConstants.QUERY_CHARACTERS[i];

			if (attributeValue.contains(queryCharacter)) {
				found = true;
			}
		}
		if (!found)
			return;

		String attributeLabel = getDataDictionaryService().getAttributeLabel(getDataObjectClass(), attributeName);
		if (getBusinessObjectDictionaryService().isLookupFieldTreatWildcardsAndOperatorsAsLiteral(getDataObjectClass(), attributeName)) {
			Object dataObjectExample = null;
			try {
				dataObjectExample = getDataObjectClass().newInstance();
			} catch (Exception e) {
				LOG.error("Exception caught instantiating " + getDataObjectClass().getName(), e);
				throw new RuntimeException("Cannot instantiate " + getDataObjectClass().getName(), e);
			}

			Class propertyType = ObjectUtils.getPropertyType(dataObjectExample, attributeName, getPersistenceStructureService());
			if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {
				GlobalVariables.getMessageMap().putError(attributeName, RiceKeyConstants.ERROR_WILDCARDS_AND_OPERATORS_NOT_ALLOWED_ON_FIELD, attributeLabel);
			}
			if (TypeUtils.isStringClass(propertyType)) {
				GlobalVariables.getMessageMap().putInfo(attributeName, RiceKeyConstants.INFO_WILDCARDS_AND_OPERATORS_TREATED_LITERALLY, attributeLabel);
			}
		} else {
			if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(getDataObjectClass(), attributeName)) {
				if (!attributeValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
					// encrypted values usually come from the DB, so we don't
					// need to filter for wildcards

					// wildcards are not allowed on restricted fields, because
					// they are typically encrypted, and wildcard searches
					// cannot be performed without
					// decrypting every row, which is currently not supported by
					// KNS

					GlobalVariables.getMessageMap().putError(attributeName, RiceKeyConstants.ERROR_SECURE_FIELD, attributeLabel);
				}
			}
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.LookupViewHelperService#performClear(java.util.Map)
	 */
	public Map<String, String> performClear(Map<String, String> fieldsForLookup) {
		Map<String, String> newFieldsForLookup = new HashMap<String, String>();
		if (fieldsForLookup != null) {
			for (Map.Entry<String, String> entry : fieldsForLookup.entrySet()) {
				// check here to see if this field is a criteria element on the form
				newFieldsForLookup.put(entry.getKey(), entry.getValue());
			}
		}
		return newFieldsForLookup;
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.LookupViewHelperService#performSearch(java.util.Map, boolean)
	 */
	public Collection<?> performSearch(Map<String, String> criteriaFieldsForLookup, boolean bounded) {
		Collection<?> displayList;

		preprocessDateFields(criteriaFieldsForLookup);

		// TODO delyea: switch the bounded flag to be unbounded to match underlying method calls in getSearchResultsWithBounding()
		displayList = getSearchResultsWithBounding(LookupUtils.forceUppercase(getDataObjectClass(), criteriaFieldsForLookup), !bounded);

		return displayList;
	}

	/**
	 * changes from/to dates into the range operators the lookupable dao expects ("..",">" etc) this method modifies the passed in map and returns a list containing only the modified fields
	 * 
	 * @param lookupFormFields
	 */
	protected Map<String, String> preprocessDateFields(Map<String, String> lookupFormFields) {
		Map<String, String> fieldsToUpdate = new HashMap<String, String>();
		Set<String> fieldsForLookup = lookupFormFields.keySet();
		for (String propName : fieldsForLookup) {
			if (propName.startsWith(KNSConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
				String from_DateValue = lookupFormFields.get(propName);
				String dateFieldName = StringUtils.remove(propName, KNSConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
				String to_DateValue = lookupFormFields.get(dateFieldName);
				String newPropValue = to_DateValue;// maybe clean above with
				// ObjectUtils.clean(propertyValue)
				if (StringUtils.isNotEmpty(from_DateValue) && StringUtils.isNotEmpty(to_DateValue)) {
					newPropValue = from_DateValue + KNSConstants.BETWEEN_OPERATOR + to_DateValue;
				} else if (StringUtils.isNotEmpty(from_DateValue) && StringUtils.isEmpty(to_DateValue)) {
					newPropValue = ">=" + from_DateValue;
				} else if (StringUtils.isNotEmpty(to_DateValue) && StringUtils.isEmpty(from_DateValue)) {
					newPropValue = "<=" + to_DateValue;
				} // could optionally continue on else here

				fieldsToUpdate.put(dateFieldName, newPropValue);
			}
		}
		// update lookup values from found date values to update
		Set<String> keysToUpdate = fieldsToUpdate.keySet();
		for (String updateKey : keysToUpdate) {
			lookupFormFields.put(updateKey, fieldsToUpdate.get(updateKey));
		}
		return fieldsToUpdate;
	}

	protected List<? extends Object> getSearchResultsWithBounding(Map<String, String> fieldValues, boolean unbounded) {
		// remove hidden fields
		LookupUtils.removeHiddenCriteriaFields(getDataObjectClass(), fieldValues);

		searchUsingOnlyPrimaryKeyValues = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getDataObjectClass(), fieldValues);

		setBackLocation(fieldValues.get(KNSConstants.BACK_LOCATION));
		setDocFormKey(fieldValues.get(KNSConstants.DOC_FORM_KEY));
		setReferencesToRefresh(fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH));
		List<?> searchResults;
		Map<String, String> nonBlankFieldValues = new HashMap<String, String>();
		for (String fieldName : fieldValues.keySet()) {
			String fieldValue = fieldValues.get(fieldName);
			if (StringUtils.isNotBlank(fieldValue)) {
				if (fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
					String encryptedValue = StringUtils.removeEnd(fieldValue, EncryptionService.ENCRYPTION_POST_PREFIX);
					try {
						fieldValue = getEncryptionService().decrypt(encryptedValue);
					} catch (GeneralSecurityException e) {
						LOG.error("Error decrypting value for business object class " + getDataObjectClass() + " attribute " + fieldName, e);
						throw new RuntimeException("Error decrypting value for business object class " + getDataObjectClass() + " attribute " + fieldName, e);
					}
				}
				nonBlankFieldValues.put(fieldName, fieldValue);
			}
		}

		// If this class is an EBO, just call the module service to get the
		// results
		if (ExternalizableBusinessObject.class.isAssignableFrom(getDataObjectClass())) {
			ModuleService eboModuleService = KNSServiceLocator.getKualiModuleService().getResponsibleModuleService(getDataObjectClass());
			BusinessObjectEntry ddEntry = eboModuleService.getExternalizableBusinessObjectDictionaryEntry(getDataObjectClass());
			Map<String, String> filteredFieldValues = new HashMap<String, String>();
			for (String fieldName : nonBlankFieldValues.keySet()) {
				if (ddEntry.getAttributeNames().contains(fieldName)) {
					filteredFieldValues.put(fieldName, nonBlankFieldValues.get(fieldName));
				}
			}
			searchResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(getDataObjectClass(), (Map) filteredFieldValues, unbounded);
			// if any of the properties refer to an embedded EBO, call the EBO
			// lookups first and apply to the local lookup
		} else if (hasExternalBusinessObjectProperty(getDataObjectClass(), nonBlankFieldValues)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("has EBO reference: " + getDataObjectClass());
				LOG.debug("properties: " + nonBlankFieldValues);
			}
			// remove the EBO criteria
			Map<String, String> nonEboFieldValues = removeExternalizableBusinessObjectFieldValues(getDataObjectClass(), nonBlankFieldValues);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Non EBO properties removed: " + nonEboFieldValues);
			}
			// get the list of EBO properties attached to this object
			List<String> eboPropertyNames = getExternalizableBusinessObjectProperties(getDataObjectClass(), nonBlankFieldValues);
			if (LOG.isDebugEnabled()) {
				LOG.debug("EBO properties: " + eboPropertyNames);
			}
			// loop over those properties
			for (String eboPropertyName : eboPropertyNames) {
				// extract the properties as known to the EBO
				Map<String, String> eboFieldValues = getExternalizableBusinessObjectFieldValues(eboPropertyName, nonBlankFieldValues);
				if (LOG.isDebugEnabled()) {
					LOG.debug("EBO properties for master EBO property: " + eboPropertyName);
					LOG.debug("properties: " + eboFieldValues);
				}
				// run search against attached EBO's module service
				ModuleService eboModuleService = KNSServiceLocator.getKualiModuleService().getResponsibleModuleService(getExternalizableBusinessObjectClass(getDataObjectClass(), eboPropertyName));
				// KULRICE-4401 made eboResults an empty list and only filled if
				// service is found.
				List eboResults = Collections.emptyList();
				if (eboModuleService != null) {
					eboResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(getExternalizableBusinessObjectClass(getDataObjectClass(), eboPropertyName), (Map) eboFieldValues,
					        unbounded);
				} else {
					LOG.debug("EBO ModuleService is null: " + eboPropertyName);
				}
				// get the mapping/relationship between the EBO object and it's
				// parent object
				// use that to adjust the fieldValues

				// get the parent property type
				Class eboParentClass;
				String eboParentPropertyName;
				if (ObjectUtils.isNestedAttribute(eboPropertyName)) {
					eboParentPropertyName = StringUtils.substringBeforeLast(eboPropertyName, ".");
					try {
						eboParentClass = PropertyUtils.getPropertyType(getDataObjectClass().newInstance(), eboParentPropertyName);
					} catch (Exception ex) {
						throw new RuntimeException("Unable to create an instance of the business object class: " + getDataObjectClass().getName(), ex);
					}
				} else {
					eboParentClass = getDataObjectClass();
					eboParentPropertyName = null;
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("determined EBO parent class/property name: " + eboParentClass + "/" + eboParentPropertyName);
				}
				// look that up in the DD (BOMDS)
				// find the appropriate relationship
				// CHECK THIS: what if eboPropertyName is a nested attribute -
				// need to strip off the eboParentPropertyName if not null
				RelationshipDefinition rd = getBusinessObjectMetaDataService().getBusinessObjectRelationshipDefinition(eboParentClass, eboPropertyName);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Obtained RelationshipDefinition for " + eboPropertyName);
					LOG.debug(rd);
				}

				// copy the needed properties (primary only) to the field values KULRICE-4446 do so only if the relationship definition exists
				// NOTE: this will work only for single-field PK unless the ORM
				// layer is directly involved
				// (can't make (field1,field2) in ( (v1,v2),(v3,v4) ) style
				// queries in the lookup framework
				if (ObjectUtils.isNotNull(rd)) {
					if (rd.getPrimitiveAttributes().size() > 1) {
						throw new RuntimeException("EBO Links don't work for relationships with multiple-field primary keys.");
					}
					String boProperty = rd.getPrimitiveAttributes().get(0).getSourceName();
					String eboProperty = rd.getPrimitiveAttributes().get(0).getTargetName();
					StringBuffer boPropertyValue = new StringBuffer();
					// loop over the results, making a string that the lookup
					// DAO will convert into an
					// SQL "IN" clause
					for (Object ebo : eboResults) {
						if (boPropertyValue.length() != 0) {
							boPropertyValue.append("|");
						}
						try {
							boPropertyValue.append(PropertyUtils.getProperty(ebo, eboProperty).toString());
						} catch (Exception ex) {
							LOG.warn("Unable to get value for " + eboProperty + " on " + ebo);
						}
					}
					if (eboParentPropertyName == null) {
						// non-nested property containing the EBO
						nonEboFieldValues.put(boProperty, boPropertyValue.toString());
					} else {
						// property nested within the main searched-for BO that
						// contains the EBO
						nonEboFieldValues.put(eboParentPropertyName + "." + boProperty, boPropertyValue.toString());
					}
				}
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Passing these results into the lookup service: " + nonEboFieldValues);
			}
			// add those results as criteria
			// run the normal search (but with the EBO critieria added)
			searchResults = (List<?>) getLookupService().findCollectionBySearchHelper(getDataObjectClass(), nonEboFieldValues, unbounded);
		} else {
			searchResults = (List<?>) getLookupService().findCollectionBySearchHelper(getDataObjectClass(), nonBlankFieldValues, unbounded);
		}

		if (searchResults == null) {
			searchResults = new ArrayList<Object>();
		}

		// sort list if default sort column given
		List<String> defaultSortColumns = getDefaultSortAttributeNames();
		if ((defaultSortColumns != null) && (defaultSortColumns.size() > 0)) {
			Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
		}
		return searchResults;
	}

	public boolean isSearchUsingOnlyPrimaryKeyValues() {
		return searchUsingOnlyPrimaryKeyValues;
	}

	/**
	 * Checks whether any of the fieldValues being passed refer to a property within an ExternalizableBusinessObject.
	 */
	protected boolean hasExternalBusinessObjectProperty(Class boClass, Map<String, String> fieldValues) {
		try {
			Object sampleBo = boClass.newInstance();
			for (String key : fieldValues.keySet()) {
				if (isExternalBusinessObjectProperty(sampleBo, key)) {
					return true;
				}
			}
		} catch (Exception ex) {
			LOG.debug("Unable to check " + boClass + " for EBO properties.", ex);
		}
		return false;
	}

	/**
	 * Check whether the given property represents a property within an EBO starting with the sampleBo object given. This is used to determine if a criteria needs to be applied to the EBO first,
	 * before sending to the normal lookup DAO.
	 */
	protected boolean isExternalBusinessObjectProperty(Object sampleBo, String propertyName) {
		try {
			if (propertyName.indexOf(".") > 0 && !StringUtils.contains(propertyName, "add.")) {
				Class propertyClass = PropertyUtils.getPropertyType(sampleBo, StringUtils.substringBeforeLast(propertyName, "."));
				if (propertyClass != null) {
					return ExternalizableBusinessObjectUtils.isExternalizableBusinessObjectInterface(propertyClass);
				} else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("unable to get class for " + StringUtils.substringBeforeLast(propertyName, ".") + " on " + sampleBo.getClass().getName());
					}
				}
			}
		} catch (Exception e) {
			LOG.debug("Unable to determine type of property for " + sampleBo.getClass().getName() + "/" + propertyName, e);
		}
		return false;
	}

	/**
	 * Returns a map stripped of any properties which refer to ExternalizableBusinessObjects. These values may not be passed into the lookup service, since the objects they refer to are not in the
	 * local database.
	 */
	protected Map<String, String> removeExternalizableBusinessObjectFieldValues(Class boClass, Map<String, String> fieldValues) {
		Map<String, String> eboFieldValues = new HashMap<String, String>();
		try {
			Object sampleBo = boClass.newInstance();
			for (String key : fieldValues.keySet()) {
				if (!isExternalBusinessObjectProperty(sampleBo, key)) {
					eboFieldValues.put(key, fieldValues.get(key));
				}
			}
		} catch (Exception ex) {
			LOG.debug("Unable to check " + boClass + " for EBO properties.", ex);
		}
		return eboFieldValues;
	}

	/**
	 * Return the EBO fieldValue entries explicitly for the given eboPropertyName. (I.e., any properties with the given property name as a prefix.
	 */
	protected Map<String, String> getExternalizableBusinessObjectFieldValues(String eboPropertyName, Map<String, String> fieldValues) {
		Map<String, String> eboFieldValues = new HashMap<String, String>();
		for (String key : fieldValues.keySet()) {
			if (key.startsWith(eboPropertyName + ".")) {
				eboFieldValues.put(StringUtils.substringAfterLast(key, "."), fieldValues.get(key));
			}
		}
		return eboFieldValues;
	}

	/**
	 * Get the complete list of all properties referenced in the fieldValues that are ExternalizableBusinessObjects.
	 * 
	 * This is a list of the EBO object references themselves, not of the properties within them.
	 */
	protected List<String> getExternalizableBusinessObjectProperties(Class boClass, Map<String, String> fieldValues) {
		Set<String> eboPropertyNames = new HashSet<String>();
		try {
			Object sampleBo = boClass.newInstance();
			for (String key : fieldValues.keySet()) {
				if (isExternalBusinessObjectProperty(sampleBo, key)) {
					eboPropertyNames.add(StringUtils.substringBeforeLast(key, "."));
				}
			}
		} catch (Exception ex) {
			LOG.debug("Unable to check " + boClass + " for EBO properties.", ex);
		}
		return new ArrayList<String>(eboPropertyNames);
	}

	/**
	 * Given an property on the main BO class, return the defined type of the ExternalizableBusinessObject. This will be used by other code to determine the correct module service to call for the
	 * lookup.
	 * 
	 * @param boClass
	 * @param propertyName
	 * @return
	 */
	protected Class<? extends ExternalizableBusinessObject> getExternalizableBusinessObjectClass(Class boClass, String propertyName) {
		try {
			return PropertyUtils.getPropertyType(boClass.newInstance(), StringUtils.substringBeforeLast(propertyName, "."));
		} catch (Exception e) {
			LOG.debug("Unable to determine type of property for " + boClass.getName() + "/" + propertyName, e);
		}
		return null;
	}

	/**
	 * Returns the maintenance document type associated with the business object class or null if one does not exist.
	 * 
	 * @return String representing the maintenance document type name
	 */
	protected String getMaintenanceDocumentTypeName() {
		MaintenanceDocumentDictionaryService dd = getMaintenanceDocumentDictionaryService();
		String maintDocTypeName = dd.getDocumentTypeName(getDataObjectClass());
		return maintDocTypeName;
	}

	/**
	 * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance actions.
	 * 
	 * @return true if bo has maint doc that allows new or copy actions
	 */
	public boolean allowsMaintenanceNewOrCopyAction() {
		boolean allowsNewOrCopy = false;

		String maintDocTypeName = getMaintenanceDocumentTypeName();
		Class boClass = getDataObjectClass();

		if (StringUtils.isNotBlank(maintDocTypeName)) {
			allowsNewOrCopy = getBusinessObjectAuthorizationService().canCreate(boClass, GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
		}
		return allowsNewOrCopy;
	}

	protected boolean allowsMaintenanceEditAction(Object dataObject) {
		boolean allowsEdit = false;

		String maintDocTypeName = getMaintenanceDocumentTypeName();

		if (StringUtils.isNotBlank(maintDocTypeName)) {
			allowsEdit = getBusinessObjectAuthorizationService().canMaintain(dataObject, GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
		}
		return allowsEdit;
	}

	protected boolean allowsMaintenanceDeleteAction(Object dataObject) {

		boolean allowsMaintain = false;
		boolean allowsDelete = false;

		String maintDocTypeName = getMaintenanceDocumentTypeName();

		if (StringUtils.isNotBlank(maintDocTypeName)) {
			allowsMaintain = getBusinessObjectAuthorizationService().canMaintain(dataObject, GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
		}

		allowsDelete = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getAllowsRecordDeletion(getDataObjectClass());

		return allowsDelete && allowsMaintain;
	}

	/**
	 * @see org.kuali.rice.kns.uif.service.LookupViewHelperService#getCreateNewUrl()
	 */
	public String getCreateNewUrl(GeneratedField generatedField) {
		String url = "";

		if (allowsMaintenanceNewOrCopyAction()) {
			Properties parameters = new Properties();
			// TODO delyea - DOCUMENT THE FOLLOWING CHANGES (next 5 lines)
//			parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.MAINTENANCE_NEW_METHOD_TO_CALL);
//			parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, getDataObjectClass().getName());
			parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, MaintenanceDocumentController.METHOD_TO_CALL_NEW);
			parameters.put(UifConstants.ViewTypeParameterNames.OBJECT_CLASS_NAME, getDataObjectClass().getName());
			parameters.put(UifParameters.VIEW_TYPE_NAME, UifConstants.ViewType.MAINTENANCE);

			// TODO delyea - DOCUMENT THE FOLLOWING CHANGES (next 2 lines)
//			url = UrlFactory.parameterizeUrl(KNSConstants.MAINTENANCE_ACTION, parameters);
			url = UrlFactory.parameterizeUrl(MaintenanceDocumentController.REQUEST_MAPPING_MAINTENANCE, parameters);
			url = "<a id=\"" + generatedField.getId() + "\" href=\"" + url + "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"create new\" width=\"70\" height=\"15\"/></a>";
		}

		return url;
	}

	public String getActionUrlsFromField(GeneratedField generatedField) {
		Object bo = generatedField.getContext().get(UifConstants.ContextVariableNames.LINE);
		if (bo == null) {
			LOG.error("***************************************************************");
			LOG.error("**** THERE IS NO BO TO PROCESS - THIS SHOULD NEVER HAPPEN *****");
			LOG.error("***************************************************************");
			return "";
		}
		String actionUrls = getActionUrls(bo, getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getDataObjectClass()), getBusinessObjectAuthorizationService().getLookupResultRestrictions(bo,
		        GlobalVariables.getUserSession().getPerson()));
		return (StringUtils.isNotBlank(actionUrls)) ? actionUrls : ACTION_URLS_EMPTY;
	}

	/**
	 * This method is called by performLookup method to generate action urls. It calls the method getCustomActionUrls to get html data, calls getMaintenanceUrl to get the actual html tag, and returns
	 * a formatted/concatenated string of action urls.
	 * 
	 * @see org.kuali.rice.kns.lookup.LookupableHelperService#getActionUrls(org.kuali.rice.kns.bo.BusinessObject)
	 */
	protected String getActionUrls(Object dataObject, List<String> pkNames, BusinessObjectRestrictions dataObjectRestrictions) {
		StringBuffer actions = new StringBuffer();
		List<HtmlData> htmlDataList = getCustomActionUrls(dataObject, pkNames);
		for (HtmlData htmlData : htmlDataList) {
			actions.append(getMaintenanceUrl(dataObject, htmlData, pkNames, dataObjectRestrictions));
			if (htmlData.getChildUrlDataList() != null) {
				if (htmlData.getChildUrlDataList().size() > 0) {
					actions.append(ACTION_URLS_CHILDREN_STARTER);
					for (HtmlData childURLData : htmlData.getChildUrlDataList()) {
						actions.append(getMaintenanceUrl(dataObject, childURLData, pkNames, dataObjectRestrictions));
						actions.append(ACTION_URLS_CHILDREN_SEPARATOR);
					}
					if (actions.toString().endsWith(ACTION_URLS_CHILDREN_SEPARATOR))
						actions.delete(actions.length() - ACTION_URLS_CHILDREN_SEPARATOR.length(), actions.length());
					actions.append(ACTION_URLS_CHILDREN_END);
				}
			}
			 actions.append(ACTION_URLS_SEPARATOR);
		}
		if (actions.toString().endsWith(ACTION_URLS_SEPARATOR))
			actions.delete(actions.length() - ACTION_URLS_SEPARATOR.length(), actions.length());
		return actions.toString();
	}

	/**
	 * Child classes should override this method if they want to return some other action urls.
	 * 
	 * @returns This default implementation returns links to edit and copy maintenance action for the current maintenance record if the business object class has an associated maintenance document.
	 *          Also checks value of allowsNewOrCopy in maintenance document xml before rendering the copy link.
	 */
	public List<HtmlData> getCustomActionUrls(Object dataObject, List<String> pkNames) {
		List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
		// TODO delyea - DOCUMENT THE FOLLOWING CHANGES FROM KNSConstants to UifConstants
		if (allowsMaintenanceEditAction(dataObject)) {
//			htmlDataList.add(getUrlData(dataObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
			htmlDataList.add(getUrlData(dataObject, MaintenanceDocumentController.METHOD_TO_CALL_EDIT, pkNames));
		}
		if (allowsMaintenanceNewOrCopyAction()) {
//			htmlDataList.add(getUrlData(dataObject, KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
			htmlDataList.add(getUrlData(dataObject, MaintenanceDocumentController.METHOD_TO_CALL_COPY, pkNames));
		}
		if (allowsMaintenanceDeleteAction(dataObject)) {
//			htmlDataList.add(getUrlData(dataObject, KNSConstants.MAINTENANCE_DELETE_METHOD_TO_CALL, pkNames));
			htmlDataList.add(getUrlData(dataObject, MaintenanceDocumentController.METHOD_TO_CALL_DELETE, pkNames));
		}
		return htmlDataList;
	}

	/**
	 * 
	 * This method calls its overloaded method with displayText as methodToCall
	 * 
	 * @param dataObject
	 * @param methodToCall
	 * @param pkNames
	 * @return
	 */
	protected AnchorHtmlData getUrlData(Object dataObject, String methodToCall, List pkNames) {
		return getUrlData(dataObject, methodToCall, methodToCall, pkNames);
	}

	/**
	 * 
	 * This method constructs an AnchorHtmlData. This method can be overriden by child classes if they want to construct the html data in a different way. Foe example, if they want different type of
	 * html tag, like input/image.
	 * 
	 * @param dataObject
	 * @param methodToCall
	 * @param displayText
	 * @param pkNames
	 * @return
	 */
	protected AnchorHtmlData getUrlData(Object dataObject, String methodToCall, String displayText, List pkNames) {

		String href = getActionUrlHref(dataObject, methodToCall, pkNames);
		// String title = StringUtils.isBlank(href)?"":getActionUrlTitleText(dataObject, displayText, pkNames);
		AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, methodToCall, displayText);
		return anchorHtmlData;
	}

	/**
	 * 
	 * This method generates and returns href for the given parameters. This method can be overridden by child classes if they have to generate href differently. For example, refer to
	 * IntendedIncumbentLookupableHelperServiceImpl
	 * 
	 * @param dataObject
	 * @param methodToCall
	 * @param pkNames
	 * @return
	 */
	protected String getActionUrlHref(Object dataObject, String methodToCall, List pkNames) {
		Properties parameters = new Properties();
		parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
		parameters.putAll(getParametersFromPrimaryKey(dataObject, pkNames));
		if (StringUtils.isNotBlank(getReturnLocation())) {
			parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());
		}
		// TODO delyea - DOCUMENT THE FOLLOWING CHANGES (next 3 lines)
//		parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, dataObject.getClass().getName());
		parameters.put(UifConstants.ViewTypeParameterNames.OBJECT_CLASS_NAME, dataObject.getClass().getName());
		parameters.put(UifParameters.VIEW_TYPE_NAME, UifConstants.ViewType.MAINTENANCE);
		// TODO delyea - DOCUMENT THE FOLLOWING CHANGES (next 2 lines)
//		return UrlFactory.parameterizeUrl(KNSConstants.MAINTENANCE_ACTION, parameters);
		return UrlFactory.parameterizeUrl(MaintenanceDocumentController.REQUEST_MAPPING_MAINTENANCE, parameters);
	}

	protected Properties getParametersFromPrimaryKey(Object dataObject, List pkNames) {
		Properties parameters = new Properties();
		for (Iterator iter = pkNames.iterator(); iter.hasNext();) {
			String fieldNm = (String) iter.next();

			Object fieldVal = ObjectUtils.getPropertyValue(dataObject, fieldNm);
			if (fieldVal == null) {
				fieldVal = KNSConstants.EMPTY_STRING;
			}
			if (fieldVal instanceof java.sql.Date) {
				String formattedString = "";
				if (Formatter.findFormatter(fieldVal.getClass()) != null) {
					Formatter formatter = Formatter.getFormatter(fieldVal.getClass());
					formattedString = (String) formatter.format(fieldVal);
					fieldVal = formattedString;
				}
			}

			// Encrypt value if it is a secure field
			if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(dataObject.getClass(), fieldNm)) {
				try {
					fieldVal = getEncryptionService().encrypt(fieldVal) + EncryptionService.ENCRYPTION_POST_PREFIX;
				} catch (GeneralSecurityException e) {
					LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
					throw new RuntimeException(e);
				}

			}

			parameters.put(fieldNm, fieldVal.toString());
		}
		return parameters;
	}

	/**
	 * Build a maintenance url.
	 * 
	 * @param bo
	 *            - business object representing the record for maint.
	 * @param methodToCall
	 *            - maintenance action
	 * @return
	 */
	final public String getMaintenanceUrl(Object dataObject, HtmlData htmlData, List<String> pkNames, BusinessObjectRestrictions dataObjectRestrictions) {
		htmlData.setTitle(getActionUrlTitleText(dataObject, htmlData.getDisplayText(), pkNames, dataObjectRestrictions));
		return htmlData.constructCompleteHtmlTag();
	}

	/**
	 * 
	 * This method generates and returns title text for action urls. Child classes can override this if they want to generate the title text differently. For example, refer to
	 * BatchJobStatusLookupableHelperServiceImpl
	 * 
	 * @param dataObject
	 * @param displayText
	 * @param pkNames
	 * @return
	 */
	protected String getActionUrlTitleText(Object dataObject, String displayText, List pkNames, BusinessObjectRestrictions dataObjectRestrictions) {
		String prependTitleText = displayText + " " + getDataDictionaryService().getDataDictionary().getDataObjectEntry(getDataObjectClass().getName()).getObjectLabel() + " "
		        + KNSServiceLocator.getKualiConfigurationService().getPropertyString(TITLE_ACTION_URL_PREPENDTEXT_PROPERTY);
		return HtmlData.getTitleText(prependTitleText, dataObject, pkNames, dataObjectRestrictions);
	}

	/**
	 * @return a List of the names of fields which are marked in data dictionary as return fields.
	 */
	public List getReturnKeys() {
		List returnKeys;
		if (fieldConversions != null && !fieldConversions.isEmpty()) {
			returnKeys = new ArrayList(fieldConversions.keySet());
		} else {
			returnKeys = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getDataObjectClass());
		}

		return returnKeys;
	}

	public String getReturnLocation() {
		return backLocation;
	}

	public String getBackLocation() {
		return this.backLocation;
	}

	public void setBackLocation(String backLocation) {
		this.backLocation = backLocation;
	}

    public Class getDataObjectClass() {
		return this.dataObjectClass;
	}

	public void setDataObjectClass(Class dataObjectClass) {
		this.dataObjectClass = dataObjectClass;
	}

	public String getDocFormKey() {
		return this.docFormKey;
	}

	public void setDocFormKey(String docFormKey) {
		this.docFormKey = docFormKey;
	}

	public String getReferencesToRefresh() {
		return this.referencesToRefresh;
	}

	public void setReferencesToRefresh(String referencesToRefresh) {
		this.referencesToRefresh = referencesToRefresh;
	}

	public List<String> getDefaultSortAttributeNames() {
		return this.defaultSortAttributeNames;
	}

	public void setDefaultSortAttributeNames(List<String> defaultSortAttributeNames) {
		this.defaultSortAttributeNames = defaultSortAttributeNames;
	}

	public boolean isSortAscending() {
		return this.sortAscending;
	}

	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}

	public Map getParameters() {
		return this.parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	/**
	 * @see org.kuali.core.lookup.LookupableHelperService#setFieldConversions(java.util.Map)
	 */
	public void setFieldConversions(Map<String, String> fieldConversions) {
		this.fieldConversions = fieldConversions;
	}

	/**
	 * Gets the readOnlyFieldsList attribute.
	 * 
	 * @return Returns the readOnlyFieldsList.
	 */
	public List<String> getReadOnlyFieldsList() {
		return readOnlyFieldsList;
	}

	/**
	 * Sets the readOnlyFieldsList attribute value.
	 * 
	 * @param readOnlyFieldsList
	 *            The readOnlyFieldsList to set.
	 */
	public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
		this.readOnlyFieldsList = readOnlyFieldsList;
	}

	protected LookupService getLookupService() {
		return lookupService != null ? lookupService : KNSServiceLocator.getLookupService();
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	protected EncryptionService getEncryptionService() {
		return encryptionService != null ? encryptionService : KNSServiceLocator.getEncryptionService();
	}

	public void setEncryptionService(EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}

	/**
	 * Gets the businessObjectDictionaryService attribute.
	 * 
	 * @return Returns the businessObjectDictionaryService.
	 */
	public BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
		return businessObjectDictionaryService != null ? businessObjectDictionaryService : KNSServiceLocator.getBusinessObjectDictionaryService();
	}

	/**
	 * Sets the businessObjectDictionaryService attribute value.
	 * 
	 * @param businessObjectDictionaryService
	 *            The businessObjectDictionaryService to set.
	 */
	public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
		this.businessObjectDictionaryService = businessObjectDictionaryService;
	}

	/**
	 * Gets the businessObjectMetaDataService attribute.
	 * 
	 * @return Returns the businessObjectMetaDataService.
	 */
	public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
		return businessObjectMetaDataService != null ? businessObjectMetaDataService : KNSServiceLocator.getBusinessObjectMetaDataService();
	}

	/**
	 * Sets the businessObjectMetaDataService attribute value.
	 * 
	 * @param businessObjectMetaDataService
	 *            The businessObjectMetaDataService to set.
	 */
	public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
		this.businessObjectMetaDataService = businessObjectMetaDataService;
	}

	/**
	 * Gets the persistenceStructureService attribute.
	 * 
	 * @return Returns the persistenceStructureService.
	 */
	protected PersistenceStructureService getPersistenceStructureService() {
		return persistenceStructureService != null ? persistenceStructureService : KNSServiceLocator.getPersistenceStructureService();
	}

	/**
	 * Sets the persistenceStructureService attribute value.
	 * 
	 * @param persistenceStructureService
	 *            The persistenceStructureService to set.
	 */
	public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
		this.persistenceStructureService = persistenceStructureService;
	}

	public BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
		return businessObjectAuthorizationService != null ? businessObjectAuthorizationService : KNSServiceLocator.getBusinessObjectAuthorizationService();
	}

	public void setBusinessObjectAuthorizationService(BusinessObjectAuthorizationService businessObjectAuthorizationService) {
		this.businessObjectAuthorizationService = businessObjectAuthorizationService;
	}

	public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		return maintenanceDocumentDictionaryService != null ? maintenanceDocumentDictionaryService : KNSServiceLocator.getMaintenanceDocumentDictionaryService();
    }

	public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
    	this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

}
