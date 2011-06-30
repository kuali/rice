/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.krad.inquiry;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.BusinessObjectRelationship;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.InquirySectionDefinition;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.lookup.HtmlData;
import org.kuali.rice.krad.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.service.BusinessObjectAuthorizationService;
import org.kuali.rice.krad.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectMetaDataService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.InactiveRecordsHidingUtils;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.web.ui.Section;
import org.kuali.rice.krad.web.ui.SectionBridge;

/**
 * Kuali inquirable implementation. Implements methods necessary to retrieve the
 * business object and render the ui.
 * 
 * NOTE: this class is not thread safe. When using this class or any subclasses
 * in Spring, make sure that this is not a singleton service, or serious errors
 * may occur.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KualiInquirableImpl extends ViewHelperServiceImpl implements Inquirable {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiInquirableImpl.class);

	protected LookupService lookupService;
	protected BusinessObjectAuthorizationService businessObjectAuthorizationService;
	protected BusinessObjectDictionaryService businessObjectDictionaryService;
	protected BusinessObjectMetaDataService businessObjectMetaDataService;
	protected PersistenceStructureService persistenceStructureService;
	protected EncryptionService encryptionService;
	protected ConfigurationService kualiConfigurationService;
	protected static BusinessObjectService businessObjectService;

	protected Class<?> dataObjectClass;

	protected Map<String, Boolean> inactiveRecordDisplay;

	/**
	 * A list that can be used to define classes that are superclasses or
	 * superinterfaces of kuali objects where those objects' inquiry URLs need
	 * to use the name of the superclass or superinterface as the business
	 * object class attribute (see
	 * {@link RiceConstants#BUSINESS_OBJECT_CLASS_ATTRIBUTE)
	 */
	public static List<Class<?>> SUPER_CLASS_TRANSLATOR_LIST = new ArrayList<Class<?>>();
	public static final String INQUIRY_TITLE_PREFIX = "title.inquiry.url.value.prependtext";

	/**
	 * Default constructor, initializes services from spring
	 */
	public KualiInquirableImpl() {
		inactiveRecordDisplay = new HashMap<String, Boolean>();
	}

	/**
	 * TODO: generics do not match between call to module service and call to
	 * lookup service
	 * 
	 * @see org.kuali.rice.krad.inquiry.Inquirable#getDataObject(java.util.Map)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getDataObject(Map fieldValues) {
		if (getDataObjectClass() == null) {
			LOG.error("Data object class not set in inquirable.");
			throw new RuntimeException(
					"Data object class not set in inquirable.");
		}

		CollectionIncomplete<Object> searchResults = null;
		ModuleService moduleService = KRADServiceLocatorWeb
				.getKualiModuleService().getResponsibleModuleService(
						getDataObjectClass());
		if (moduleService != null
				&& moduleService.isExternalizable(getDataObjectClass())) {
			BusinessObject bo = moduleService.getExternalizableBusinessObject(
					getBusinessObjectClass(), fieldValues);
			if (bo != null) {
				ArrayList<Object> list = new ArrayList<Object>(1);
				list.add(bo);
				searchResults = new CollectionIncomplete<Object>(list, 1L);
			}
		} else {
			// TODO: If this is to get a single BO, why using the lookup
			// service?
			searchResults = (CollectionIncomplete<Object>) getLookupService()
					.findCollectionBySearch(getBusinessObjectClass(),
							fieldValues);
		}
		
		BusinessObject foundObject = null;
		if (searchResults != null && searchResults.size() > 0) {
			foundObject = (BusinessObject) searchResults.get(0);
		}
		
		return foundObject;
	}

    /**
	 * Return a business object by searching with map, the map keys should be a
	 * property name of the business object, with the map value as the value to
	 * search for.
	 */
    @Deprecated
	public BusinessObject getBusinessObject(Map fieldValues) {
		return (BusinessObject)getDataObject(fieldValues);
	}

	/**
	 * Objects extending KualiInquirableBase must specify the Section objects
	 * used to display the inquiry result.
	 */
	@Deprecated
	public List<Section> getSections(BusinessObject bo) {

		List<Section> sections = new ArrayList<Section>();
		if (getBusinessObjectClass() == null) {
			LOG.error("Business object class not set in inquirable.");
			throw new RuntimeException("Business object class not set in inquirable.");
		}

		InquiryRestrictions inquiryRestrictions = KRADServiceLocatorWeb.getBusinessObjectAuthorizationService()
				.getInquiryRestrictions(bo, GlobalVariables.getUserSession().getPerson());

		Collection<InquirySectionDefinition> inquirySections = getBusinessObjectDictionaryService().getInquirySections(
				getBusinessObjectClass());
		for (Iterator<InquirySectionDefinition> iter = inquirySections.iterator(); iter.hasNext();) {
			InquirySectionDefinition inquirySection = iter.next();
			if (!inquiryRestrictions.isHiddenSectionId(inquirySection.getId())) {
				Section section = SectionBridge.toSection(this, inquirySection, bo, inquiryRestrictions);
				sections.add(section);
			}
		}

		return sections;
	}

    /**
     * @see org.kuali.rice.krad.inquiry.Inquirable#buildInquirableLink(java.lang.Object,
     *      java.lang.String, org.kuali.rice.krad.uif.widget.Inquiry)
     */
    public void buildInquirableLink(Object dataObject, String propertyName, Inquiry inquiry) {
        Class<?> inquiryObjectClass = null;

        // inquiry into data object class if property is title attribute
        Class<?> objectClass = ObjectUtils.materializeClassForProxiedObject(dataObject);
        if (propertyName.equals(getBusinessObjectMetaDataService().getTitleAttribute(objectClass))) {
            inquiryObjectClass = objectClass;
        }
        else if (ObjectUtils.isNestedAttribute(propertyName)) {
            String nestedPropertyName = ObjectUtils.getNestedAttributePrefix(propertyName);
            Object nestedPropertyObject = ObjectUtils.getNestedValue(dataObject, nestedPropertyName);

            if (ObjectUtils.isNotNull(nestedPropertyObject)) {
                String nestedPropertyPrimitive = ObjectUtils.getNestedAttributePrimitive(propertyName);
                Class<?> nestedPropertyObjectClass = ObjectUtils.materializeClassForProxiedObject(nestedPropertyObject);

                if (nestedPropertyPrimitive.equals(getBusinessObjectMetaDataService().getTitleAttribute(nestedPropertyObjectClass))) {
                    inquiryObjectClass = nestedPropertyObjectClass;
                }
            }
        }

		// if not title, then get primary relationship
		BusinessObjectRelationship relationship = null;
		if (inquiryObjectClass == null) {
			relationship = getBusinessObjectMetaDataService().getDataObjectRelationship(dataObject, objectClass,
					propertyName, "", true, false, true);
			if (relationship != null) {
				inquiryObjectClass = relationship.getRelatedClass();
			}
		}

		// if haven't found inquiry class, then no inquiry can be rendered
		if (inquiryObjectClass == null) {
			inquiry.setRender(false);

			return;
		}

		if (DocumentHeader.class.isAssignableFrom(inquiryObjectClass)) {
			String documentNumber = (String) ObjectUtils.getPropertyValue(dataObject, propertyName);
			if (StringUtils.isNotBlank(documentNumber)) {
				inquiry.getInquiryLinkField().setHrefText(
						getKualiConfigurationService().getPropertyString(KRADConstants.WORKFLOW_URL_KEY)
								+ KRADConstants.DOCHANDLER_DO_URL + documentNumber + KRADConstants.DOCHANDLER_URL_CHUNK);
				inquiry.getInquiryLinkField().setLinkLabel(documentNumber);
				inquiry.setRender(true);
			}

			return;
		}

		synchronized (SUPER_CLASS_TRANSLATOR_LIST) {
			for (Class<?> clazz : SUPER_CLASS_TRANSLATOR_LIST) {
				if (clazz.isAssignableFrom(inquiryObjectClass)) {
					inquiryObjectClass = clazz;
					break;
				}
			}
		}

		if (!inquiryObjectClass.isInterface()
				&& ExternalizableBusinessObject.class.isAssignableFrom(inquiryObjectClass)) {
			inquiryObjectClass = ExternalizableBusinessObjectUtils
					.determineExternalizableBusinessObjectSubInterface(inquiryObjectClass);
		}
		
		// listPrimaryKeyFieldNames returns an unmodifiable list. So a copy is
		// necessary.
		List<String> keys = new ArrayList<String>(getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(
				inquiryObjectClass));

		if (keys == null) {
			keys = Collections.emptyList();
		}

		// build inquiry parameter mappings
		Map<String, String> inquiryParameters = new HashMap<String, String>();
		for (String keyName : keys) {
			String keyConversion = keyName;
			if (relationship != null) {
				keyConversion = relationship.getChildAttributeForParentAttribute(keyName);
			}
			else if (ObjectUtils.isNestedAttribute(propertyName)) {
				String nestedAttributePrefix = ObjectUtils.getNestedAttributePrefix(propertyName);
				keyConversion = nestedAttributePrefix + "." + keyName;
			}

			inquiryParameters.put(keyConversion, keyName);
		}

		inquiry.buildInquiryLink(dataObject, propertyName, inquiryObjectClass, inquiryParameters);
	}

	/**
	 * Helper method to build an inquiry url for a result field.
	 * 
	 * @param bo
	 *            the business object instance to build the urls for
	 * @param propertyName
	 *            the property which links to an inquirable
	 * @return String url to inquiry
	 */
	@Deprecated
	public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
		Properties parameters = new Properties();
		AnchorHtmlData hRef = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);
		parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "start");

		Class inquiryBusinessObjectClass = null;
		String attributeRefName = "";
		boolean isPkReference = false;

		boolean doesNestedReferenceHaveOwnPrimitiveReference = false;
		BusinessObject nestedBusinessObject = null;

		Class businessObjectClass = ObjectUtils.materializeClassForProxiedObject(businessObject);
		if (attributeName.equals(getBusinessObjectDictionaryService().getTitleAttribute(businessObjectClass))) {
			inquiryBusinessObjectClass = businessObjectClass;
			isPkReference = true;
		}
		else {
			if (ObjectUtils.isNestedAttribute(attributeName)) {
				// if we have a reference object, we should determine if we
				// should either provide an inquiry link to
				// the reference object itself, or some other nested primitive.

				// for example, if the attribute is
				// "referenceObject.someAttribute", and there is no primitive
				// reference for
				// "someAttribute", then an inquiry link is provided to the
				// "referenceObject". If it does have a primitive reference,
				// then
				// the inquiry link is directed towards it instead
				String nestedReferenceName = ObjectUtils.getNestedAttributePrefix(attributeName);
				Object nestedReferenceObject = ObjectUtils.getNestedValue(businessObject, nestedReferenceName);

				if (ObjectUtils.isNotNull(nestedReferenceObject) && nestedReferenceObject instanceof BusinessObject) {
					nestedBusinessObject = (BusinessObject) nestedReferenceObject;
					String nestedAttributePrimitive = ObjectUtils.getNestedAttributePrimitive(attributeName);
					Class nestedBusinessObjectClass = ObjectUtils
							.materializeClassForProxiedObject(nestedBusinessObject);

					if (nestedAttributePrimitive.equals(getBusinessObjectDictionaryService().getTitleAttribute(
							nestedBusinessObjectClass))) {
						// we are going to inquiry the record that contains the
						// attribute we're rendering an inquiry URL for
						inquiryBusinessObjectClass = nestedBusinessObjectClass;
						// I know it's already set to false, just to show how
						// this variable is set
						doesNestedReferenceHaveOwnPrimitiveReference = false;
					}
					else {
						Map primitiveReference = LookupUtils.getPrimitiveReference(nestedBusinessObject,
								nestedAttributePrimitive);
						if (primitiveReference != null && !primitiveReference.isEmpty()) {
							attributeRefName = (String) primitiveReference.keySet().iterator().next();
							inquiryBusinessObjectClass = (Class) primitiveReference.get(attributeRefName);
							doesNestedReferenceHaveOwnPrimitiveReference = true;
						}
						else {
							// we are going to inquiry the record that contains
							// the attribute we're rendering an inquiry URL for
							inquiryBusinessObjectClass = ObjectUtils
									.materializeClassForProxiedObject(nestedBusinessObject);
							// I know it's already set to false, just to show
							// how this variable is set
							doesNestedReferenceHaveOwnPrimitiveReference = false;
						}
					}
				}
			}
			else {
				Map primitiveReference = LookupUtils.getPrimitiveReference(businessObject, attributeName);
				if (primitiveReference != null && !primitiveReference.isEmpty()) {
					attributeRefName = (String) primitiveReference.keySet().iterator().next();
					inquiryBusinessObjectClass = (Class) primitiveReference.get(attributeRefName);
				}
			}
		}

		if (inquiryBusinessObjectClass != null && DocumentHeader.class.isAssignableFrom(inquiryBusinessObjectClass)) {
			String documentNumber = (String) ObjectUtils.getPropertyValue(businessObject, attributeName);
			if (!StringUtils.isBlank(documentNumber)) {
				// if NullPointerException on the following line, maybe the
				// Spring bean wasn't injected w/ KualiConfigurationException,
				// or if
				// instances of a sub-class of this class are not Spring
				// created, then override getKualiConfigurationService() in the
				// subclass
				// to return the configuration service from a Spring service
				// locator (or set it).
				hRef.setHref(getKualiConfigurationService().getPropertyString(KRADConstants.WORKFLOW_URL_KEY)
						+ KRADConstants.DOCHANDLER_DO_URL + documentNumber + KRADConstants.DOCHANDLER_URL_CHUNK);
			}
			return hRef;
		}

		if (inquiryBusinessObjectClass == null
				|| getBusinessObjectDictionaryService().isInquirable(inquiryBusinessObjectClass) == null
				|| !getBusinessObjectDictionaryService().isInquirable(inquiryBusinessObjectClass).booleanValue()) {
			return hRef;
		}

		synchronized (SUPER_CLASS_TRANSLATOR_LIST) {
			for (Class clazz : SUPER_CLASS_TRANSLATOR_LIST) {
				if (clazz.isAssignableFrom(inquiryBusinessObjectClass)) {
					inquiryBusinessObjectClass = clazz;
					break;
				}
			}
		}

		if (!inquiryBusinessObjectClass.isInterface()
				&& ExternalizableBusinessObject.class.isAssignableFrom(inquiryBusinessObjectClass)) {
			inquiryBusinessObjectClass = ExternalizableBusinessObjectUtils
					.determineExternalizableBusinessObjectSubInterface(inquiryBusinessObjectClass);
		}

		parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, inquiryBusinessObjectClass.getName());

		// listPrimaryKeyFieldNames returns an unmodifiable list. So a copy is
		// necessary.
		List<String> keys = new ArrayList<String>(getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(
				inquiryBusinessObjectClass));

		if (keys == null) {
			keys = Collections.emptyList();
		}

		BusinessObjectRelationship businessObjectRelationship = null;

		if (attributeRefName != null && !"".equals(attributeRefName)) {
			businessObjectRelationship = getBusinessObjectMetaDataService().getBusinessObjectRelationship(
					businessObject, attributeRefName);

			if (businessObjectRelationship != null && businessObjectRelationship.getParentToChildReferences() != null) {
				for (String targetNamePrimaryKey : businessObjectRelationship.getParentToChildReferences().values()) {
					keys.add(targetNamePrimaryKey);
				}
			}
		}
		// build key value url parameters used to retrieve the business object
		String keyName = null;
		String keyConversion = null;
		Map<String, String> fieldList = new HashMap<String, String>();
		for (Iterator iter = keys.iterator(); iter.hasNext();) {
			keyName = (String) iter.next();
			keyConversion = keyName;
			if (ObjectUtils.isNestedAttribute(attributeName)) {
				if (doesNestedReferenceHaveOwnPrimitiveReference) {
					String nestedAttributePrefix = ObjectUtils.getNestedAttributePrefix(attributeName);
					// String foreignKeyFieldName =
					// getBusinessObjectMetaDataService().getForeignKeyFieldName(
					// inquiryBusinessObjectClass.getClass(), attributeRefName,
					// keyName);

					String foreignKeyFieldName = getBusinessObjectMetaDataService().getForeignKeyFieldName(
							nestedBusinessObject.getClass(), attributeRefName, keyName);
					keyConversion = nestedAttributePrefix + "." + foreignKeyFieldName;
				}
				else {
					keyConversion = ObjectUtils.getNestedAttributePrefix(attributeName) + "." + keyName;
				}
			}
			else {
				if (isPkReference) {
					keyConversion = keyName;
				}
				else if (businessObjectRelationship != null) {
					// Using BusinessObjectMetaDataService instead of
					// PersistenceStructureService
					// since otherwise, relationship information from
					// datadictionary is not used at all
					// Also, BOMDS.getBusinessObjectRelationship uses
					// PersistenceStructureService,
					// so both datadictionary and the persistance layer get
					// covered
					/*
					 * BusinessObjectRelationship businessObjectRelationship =
					 * getBusinessObjectMetaDataService
					 * ().getBusinessObjectRelationship( businessObject,
					 * attributeRefName);
					 */
					BidiMap bidiMap = new DualHashBidiMap(businessObjectRelationship.getParentToChildReferences());
					keyConversion = (String) bidiMap.getKey(keyName);
					// keyConversion =
					// getPersistenceStructureService().getForeignKeyFieldName(businessObject.getClass(),
					// attributeRefName, keyName);
				}
			}
			Object keyValue = null;
			if (keyConversion != null) {
				keyValue = ObjectUtils.getPropertyValue(businessObject, keyConversion);
			}

			if (keyValue == null) {
				keyValue = "";
			}
			else if (keyValue instanceof java.sql.Date) { // format the date for
															// passing in url
				if (Formatter.findFormatter(keyValue.getClass()) != null) {
					Formatter formatter = Formatter.getFormatter(keyValue.getClass());
					keyValue = (String) formatter.format(keyValue);
				}
			}
			else {
				keyValue = keyValue.toString();
			}

			// Encrypt value if it is a field that has restriction that prevents
			// a value from being shown to user,
			// because we don't want the browser history to store the restricted
			// attribute's value in the URL
			AttributeSecurity attributeSecurity = KRADServiceLocatorWeb.getDataDictionaryService().getAttributeSecurity(
					businessObject.getClass().getName(), keyName);
			if (attributeSecurity != null && attributeSecurity.hasRestrictionThatRemovesValueFromUI()) {
				try {
					keyValue = getEncryptionService().encrypt(keyValue);
				}
				catch (GeneralSecurityException e) {
					LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
					throw new RuntimeException(e);
				}
			}

			parameters.put(keyName, keyValue);
			fieldList.put(keyName, keyValue.toString());
		}

		return getHyperLink(inquiryBusinessObjectClass, fieldList,
				UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
	}

	@Deprecated
	protected AnchorHtmlData getHyperLink(Class inquiryClass, Map<String, String> fieldList, String inquiryUrl) {
		AnchorHtmlData a = new AnchorHtmlData(inquiryUrl, KRADConstants.EMPTY_STRING);
		a.setTitle(HtmlData.getTitleText(this.createTitleText(inquiryClass), inquiryClass, fieldList));
		return a;
	}

	/**
	 * Gets text to prepend to the inquiry link title
	 * 
	 * @param dataObjectClass
	 *            - data object class being inquired into
	 * @return String title prepend text
	 */
	@Deprecated
	protected String createTitleText(Class<?> dataObjectClass) {
		String titleText = "";

		String titlePrefixProp = getKualiConfigurationService().getPropertyString(INQUIRY_TITLE_PREFIX);
		if (StringUtils.isNotBlank(titlePrefixProp)) {
			titleText += titlePrefixProp + " ";
		}

		String objectLabel = getDataDictionaryService().getDataDictionary()
				.getBusinessObjectEntry(dataObjectClass.getName()).getObjectLabel();
		if (StringUtils.isNotBlank(objectLabel)) {
			titleText += objectLabel + " ";
		}

		return titleText;
	}

	@Deprecated
	public void addAdditionalSections(List columns, BusinessObject bo) {
	}

	/**
	 * @see org.kuali.rice.krad.inquiry.Inquirable#getHtmlMenuBar()
	 */
	@Deprecated
	public String getHtmlMenuBar() {
		// TODO: replace with inquiry menu bar
		return getBusinessObjectDictionaryService().getLookupMenuBar(getBusinessObjectClass());
	}

	/**
	 * @see org.kuali.rice.krad.inquiry.Inquirable#getTitle()
	 */
	@Deprecated
	public String getTitle() {
		return getBusinessObjectDictionaryService().getInquiryTitle(getBusinessObjectClass());
	}

	public Class<?> getDataObjectClass() {
	    return dataObjectClass;
	}

	/**
	 * @see org.kuali.rice.krad.inquiry.Inquirable#setDataObjectClass(java.lang.Class)
	 */
	@Override
    public void setDataObjectClass(Class<?> dataObjectClass) {
	    this.dataObjectClass = dataObjectClass;
    }

    /**
	 * @param businessObjectClass
	 *            The businessObjectClass to set.
	 */
	@Deprecated
	public void setBusinessObjectClass(Class businessObjectClass) {
		this.dataObjectClass = businessObjectClass;
	}

	/**
     * @return Returns the businessObjectClass.
     */
    @Deprecated
    public Class getBusinessObjectClass() {
        return dataObjectClass;
    }
    
	/**
	 * @see org.kuali.rice.krad.inquiry.Inquirable#getInactiveRecordDisplay()
	 */
	@Deprecated
	public Map<String, Boolean> getInactiveRecordDisplay() {
		return inactiveRecordDisplay;
	}

	/**
	 * @see org.kuali.rice.krad.inquiry.Inquirable#getShowInactiveRecords(java.lang.String)
	 */
	@Deprecated
	public boolean getShowInactiveRecords(String collectionName) {
		return InactiveRecordsHidingUtils.getShowInactiveRecords(inactiveRecordDisplay, collectionName);
	}

	/**
	 * @see org.kuali.rice.krad.inquiry.Inquirable#setShowInactiveRecords(java.lang.String,
	 *      boolean)
	 */
	@Deprecated
	public void setShowInactiveRecords(String collectionName, boolean showInactive) {
		InactiveRecordsHidingUtils.setShowInactiveRecords(inactiveRecordDisplay, collectionName, showInactive);
	}

	protected LookupService getLookupService() {
		if (lookupService == null) {
			lookupService = KRADServiceLocatorWeb.getLookupService();
		}
		return lookupService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	protected BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
		if (businessObjectDictionaryService == null) {
			businessObjectDictionaryService = KRADServiceLocatorWeb.getBusinessObjectDictionaryService();
		}
		return businessObjectDictionaryService;
	}

	public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
		this.businessObjectDictionaryService = businessObjectDictionaryService;
	}

	protected PersistenceStructureService getPersistenceStructureService() {
		if (persistenceStructureService == null) {
			persistenceStructureService = KRADServiceLocator.getPersistenceStructureService();
		}
		return this.persistenceStructureService;
	}

	public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
		this.persistenceStructureService = persistenceStructureService;
	}

	protected EncryptionService getEncryptionService() {
		if (encryptionService == null) {
			encryptionService = CoreApiServiceLocator.getEncryptionService();
		}
		return this.encryptionService;
	}

	public void setEncryptionService(EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}

	protected ConfigurationService getKualiConfigurationService() {
		if (kualiConfigurationService == null) {
			kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
		}
		return this.kualiConfigurationService;
	}

	public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
		this.kualiConfigurationService = kualiConfigurationService;
	}

	protected BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
		if (businessObjectMetaDataService == null) {
			businessObjectMetaDataService = KRADServiceLocatorWeb.getBusinessObjectMetaDataService();
		}
		return this.businessObjectMetaDataService;
	}

	public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
		this.businessObjectMetaDataService = businessObjectMetaDataService;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if (businessObjectService == null) {
			businessObjectService = KRADServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	protected BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
		if (this.businessObjectAuthorizationService == null) {
			this.businessObjectAuthorizationService = KRADServiceLocatorWeb.getBusinessObjectAuthorizationService();
		}
		return this.businessObjectAuthorizationService;
	}

	public void setBusinessObjectAuthorizationService(
			BusinessObjectAuthorizationService businessObjectAuthorizationService) {
		this.businessObjectAuthorizationService = businessObjectAuthorizationService;
	}

	@Deprecated
	protected AnchorHtmlData getInquiryUrlForPrimaryKeys(Class clazz, Object businessObject, List<String> primaryKeys,
			String displayText) {
		if (businessObject == null)
			return new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);

		Properties parameters = new Properties();
		parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.START_METHOD);
		parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, clazz.getName());

		String titleAttributeValue;
		Map<String, String> fieldList = new HashMap<String, String>();
		for (String primaryKey : primaryKeys) {
			titleAttributeValue = (String) ObjectUtils.getPropertyValue(businessObject, primaryKey);
			parameters.put(primaryKey, titleAttributeValue);
			fieldList.put(primaryKey, titleAttributeValue);
		}
		if (StringUtils.isEmpty(displayText)) {
			return getHyperLink(clazz, fieldList, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
        }
		else {
			return getHyperLink(clazz, fieldList, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters),
					displayText);
        }
	}

	@Deprecated
	protected AnchorHtmlData getHyperLink(Class inquiryClass, Map<String, String> fieldList, String inquiryUrl,
			String displayText) {
		AnchorHtmlData a = new AnchorHtmlData(inquiryUrl, KRADConstants.EMPTY_STRING, displayText);
		a.setTitle(AnchorHtmlData.getTitleText(getKualiConfigurationService().getPropertyString(INQUIRY_TITLE_PREFIX)
				+ " "
				+ getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(inquiryClass.getName())
						.getObjectLabel() + " ", inquiryClass, fieldList));
		return a;
	}

}
