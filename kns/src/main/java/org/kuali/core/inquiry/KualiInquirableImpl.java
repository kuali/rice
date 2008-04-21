/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.inquiry;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.InquirySectionDefinition;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.service.LookupService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.InactiveRecordsHidingUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.ui.Section;
import org.kuali.core.web.ui.SectionBridge;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Kuali inquirable implementation. Implements methods necessary to retrieve the business object and render the ui.
 * 
 * NOTE: this class is not thread safe.  When using this class or any subclasses in Spring, make sure that this is not a singleton service, or
 * serious errors may occur.
 */
public class KualiInquirableImpl implements Inquirable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiInquirableImpl.class);

    private LookupService lookupService;
    private BusinessObjectDictionaryService businessObjectDictionaryService;
    private PersistenceStructureService persistenceStructureService;
    private DataDictionaryService dataDictionaryService;
    private EncryptionService encryptionService;
    private UniversalUserService universalUserService;

    private Class businessObjectClass;

    private Map<String, Boolean> inactiveRecordDisplay;
    
    public static List<Class> HACK_LIST = new ArrayList<Class>();

    /**
     * Default constructor, initializes services from spring
     */
    public KualiInquirableImpl() {
        inactiveRecordDisplay = new HashMap<String, Boolean>();
    }

    /**
     * Return a business object by searching with map, the map keys should be a property name of the business object, with the map
     * value as the value to search for.
     */
    public BusinessObject getBusinessObject(Map fieldValues) {
        if (getBusinessObjectClass() == null) {
            LOG.error("Business object class not set in inquirable.");
            throw new RuntimeException("Business object class not set in inquirable.");
        }

        CollectionIncomplete searchResults = null;
        // user inquiries need to go through user service
        if (UniversalUser.class.equals(getBusinessObjectClass())) {
            searchResults = (CollectionIncomplete) getUniversalUserService().findUniversalUsers(fieldValues);
        }
        else {
            searchResults = (CollectionIncomplete) getLookupService().findCollectionBySearch(getBusinessObjectClass(), fieldValues);
        }

        BusinessObject foundObject = null;
        if (searchResults != null && searchResults.size() > 0) {
            foundObject = (BusinessObject) searchResults.get(0);
        }
        return foundObject;
    }


    /**
     * Objects extending KualiInquirableBase must specify the Section objects used to display the inquiry result.
     */
    public List<Section> getSections(BusinessObject bo) {
        
        List<Section> sections = new ArrayList<Section>();
        if (getBusinessObjectClass() == null) {
            LOG.error("Business object class not set in inquirable.");
            throw new RuntimeException("Business object class not set in inquirable.");
        }

        Collection inquirySections = getBusinessObjectDictionaryService().getInquirySections(getBusinessObjectClass());
        for (Iterator iter = inquirySections.iterator(); iter.hasNext();) {
            
            InquirySectionDefinition inquirySection = (InquirySectionDefinition) iter.next();
            Section section = SectionBridge.toSection(this, inquirySection, bo);
            sections.add(section);
            
        }

        return sections;
    }
    
    /**
     * Helper method to build an inquiry url for a result field.
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "start");

        Class inquiryBusinessObjectClass = null;
        String attributeRefName = "";
        boolean isPkReference = false;
        if (attributeName.equals(getBusinessObjectDictionaryService().getTitleAttribute(businessObject.getClass()))) {
            inquiryBusinessObjectClass = businessObject.getClass();
            isPkReference = true;
        }
        else {
            if (ObjectUtils.isNestedAttribute(attributeName)) {
                String nestedReferenceName = ObjectUtils.getNestedAttributePrefix(attributeName);
                Object nestedReferenceObject = ObjectUtils.getPropertyValue(businessObject, nestedReferenceName);
                
                if (ObjectUtils.isNotNull(nestedReferenceObject) && nestedReferenceObject instanceof BusinessObject) {
                    BusinessObject nestedBusinessObject = (BusinessObject) nestedReferenceObject;
                    String nestedAttributePrimitive = ObjectUtils.getNestedAttributePrimitive(attributeName);
                    
                    Map primitiveReference = LookupUtils.getPrimitiveReference(nestedBusinessObject, nestedAttributePrimitive);
                    if (primitiveReference != null && !primitiveReference.isEmpty()) {
                        attributeRefName = (String) primitiveReference.keySet().iterator().next();
                        inquiryBusinessObjectClass = (Class) primitiveReference.get(attributeRefName);
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

        if (inquiryBusinessObjectClass == null || getBusinessObjectDictionaryService().isInquirable(inquiryBusinessObjectClass) == null || !getBusinessObjectDictionaryService().isInquirable(inquiryBusinessObjectClass).booleanValue()) {
            return KNSConstants.EMPTY_STRING;
        }

        synchronized (HACK_LIST) {
            for (Class clazz : HACK_LIST) {
                if (clazz.isAssignableFrom(inquiryBusinessObjectClass)) {
                    inquiryBusinessObjectClass = clazz;
                    break;
                }
            }    
        }
        
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, inquiryBusinessObjectClass.getName());

        List keys = new ArrayList();
        if (getPersistenceStructureService().isPersistable(inquiryBusinessObjectClass)) {
            keys = getPersistenceStructureService().listPrimaryKeyFieldNames(inquiryBusinessObjectClass);
        }

        // build key value url parameters used to retrieve the business object
        String keyName = null;
        String keyConversion = null;
        String encryptedList = "";
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            keyName = (String) iter.next();
            keyConversion = keyName;
            if (ObjectUtils.isNestedAttribute(attributeName)) {
                keyConversion = ObjectUtils.getNestedAttributePrefix(attributeName) + "." + keyName;
            }
            else {
                if (isPkReference) {
                    keyConversion = keyName;
                }
                else {
                    keyConversion = getPersistenceStructureService().getForeignKeyFieldName(businessObject.getClass(), attributeRefName, keyName);
                }
            }
            Object keyValue = null;
            if (keyConversion != null) {
                keyValue = ObjectUtils.getPropertyValue(businessObject, keyConversion);
            }

            if (keyValue == null) {
                keyValue = "";
            } else if (keyValue instanceof java.sql.Date) { //format the date for passing in url
                if (Formatter.findFormatter(keyValue.getClass()) != null) {
                    Formatter formatter = Formatter.getFormatter(keyValue.getClass());
                    keyValue = (String) formatter.format(keyValue);
                }
            } else {
                keyValue = keyValue.toString();
            }

            // Encrypt value if it is a secure field
            String displayWorkgroup = getDataDictionaryService().getAttributeDisplayWorkgroup(businessObject.getClass(), keyName);
            if (StringUtils.isNotBlank(displayWorkgroup)) {
                try {
                    keyValue = getEncryptionService().encrypt(keyValue);
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
                    throw new RuntimeException(e);
                }

                // add to parameter list so that KualiInquiryAction can identify which parameters are encrypted
                if (encryptedList.equals("")) {
                    encryptedList = keyName;
                }
                else {
                    encryptedList = encryptedList + KNSConstants.FIELD_CONVERSIONS_SEPERATOR + keyName;
                }
            }

            parameters.put(keyName, keyValue);
        }

        // if we did encrypt a value (or values), add the list of those that are encrypted to the parameters
        if (!encryptedList.equals("")) {
            parameters.put(KNSConstants.ENCRYPTED_LIST_PREFIX, encryptedList);
        }

        return UrlFactory.parameterizeUrl(KNSConstants.INQUIRY_ACTION, parameters);
    }

    public void addAdditionalSections(List columns, BusinessObject bo) {
    }

    /**
     * @see org.kuali.core.inquiry.Inquirable#getHtmlMenuBar()
     */
    public String getHtmlMenuBar() {
        // TODO: replace with inquiry menu bar
        return getBusinessObjectDictionaryService().getLookupMenuBar(getBusinessObjectClass());
    }

    /**
     * @see org.kuali.core.inquiry.Inquirable#getTitle()
     */
    public String getTitle() {
        return getBusinessObjectDictionaryService().getInquiryTitle(getBusinessObjectClass());
    }

    /**
     * @return Returns the businessObjectClass.
     */
    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * @param businessObjectClass The businessObjectClass to set.
     */
    public void setBusinessObjectClass(Class businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    /**
     * Gets the kualiUserService attribute.
     * 
     * @return Returns the kualiUserService.
     */
    public UniversalUserService getUniversalUserService() {
	if ( universalUserService == null ) {
	    universalUserService = KNSServiceLocator.getUniversalUserService();
	}
        return universalUserService;
    }

    /**
     * @see org.kuali.core.inquiry.Inquirable#getInactiveRecordDisplay()
     */
    public Map<String, Boolean> getInactiveRecordDisplay() {
	return inactiveRecordDisplay;
}
    /**
     * @see org.kuali.core.inquiry.Inquirable#getShowInactiveRecords(java.lang.String)
     */
    public boolean getShowInactiveRecords(String collectionName) {
	return InactiveRecordsHidingUtils.getShowInactiveRecords(inactiveRecordDisplay, collectionName);
    }

    /**
     * @see org.kuali.core.inquiry.Inquirable#setShowInactiveRecords(java.lang.String, boolean)
     */
    public void setShowInactiveRecords(String collectionName, boolean showInactive) {
	InactiveRecordsHidingUtils.setShowInactiveRecords(inactiveRecordDisplay, collectionName, showInactive);
    }

    public LookupService getLookupService() {
	if ( lookupService == null ) {
	    lookupService = KNSServiceLocator.getLookupService();
	}
        return lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    public BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
	if ( businessObjectDictionaryService == null ) {
	    businessObjectDictionaryService = KNSServiceLocator.getBusinessObjectDictionaryService();
	}
        return businessObjectDictionaryService;
    }

    public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
        this.businessObjectDictionaryService = businessObjectDictionaryService;
    }

    public PersistenceStructureService getPersistenceStructureService() {
	if ( persistenceStructureService == null ) {
	    persistenceStructureService = KNSServiceLocator.getPersistenceStructureService();
	}
        return this.persistenceStructureService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public DataDictionaryService getDataDictionaryService() {
	if ( dataDictionaryService == null ) {
	    dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
	}
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public EncryptionService getEncryptionService() {
	if ( encryptionService == null ) {
	    encryptionService = KNSServiceLocator.getEncryptionService();
	}
        return this.encryptionService;
    }

    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }
    
    
}