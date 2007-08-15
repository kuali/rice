/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.lookup;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.RiceConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.BusinessObjectAttributeEntry;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.BeanPropertyComparator;
import org.kuali.rice.KNSServiceLocator;

public class DictionaryLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final long serialVersionUID = 970484069493741447L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DictionaryLookupableHelperServiceImpl.class);
    private static final List IGNORED_FIELDS = Arrays.asList(new String[] { "class", "validationNumber" });


    
    @Override
    public List getSearchResults(Map<String, String> fieldValues) {
        throw new UnsupportedOperationException("getSearchResults not supported for DictionaryLookupableHelperServiceImpl");
    }


    /**
     * Overrides the default lookupable search to provide a search against the BusinessObjectDictionaryService to retrieve attribute
     * definitions.
     */
    public List getSearchResults(Map fieldValues, Map fieldConversions) {
        setBackLocation((String) fieldValues.get(RiceConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(RiceConstants.DOC_FORM_KEY));

        List searchResults = new ArrayList();
        DataDictionaryService dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        try {
            String boClassName = (String) fieldValues.get(RiceConstants.DICTIONARY_BO_NAME);

            // get bo class to query on
            Class boClass = Class.forName(boClassName);

            // use reflection to get the properties for the bo
            // TODO: Get list of attributes from dictionary service
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(boClass);
            for (int i = 0; i < descriptors.length; ++i) {
                PropertyDescriptor propertyDescriptor = descriptors[i];

                // skip fields in IGNORED_FIELDS
                if (IGNORED_FIELDS.contains(propertyDescriptor.getName())) {
                    continue;
                }


                // ignore collection attributes for now
                // Set BusinessObjectAttributeEntry by querying dictionary service
                if (!Collection.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    String propertyName = propertyDescriptor.getName();

                    BusinessObjectAttributeEntry attributeEntry = new BusinessObjectAttributeEntry();
                    attributeEntry.setAttributeName(propertyName);
                    attributeEntry.setAttributeLabel(dataDictionaryService.getAttributeLabel(boClass, propertyName));
                    attributeEntry.setAttributeSummary(dataDictionaryService.getAttributeSummary(boClass, propertyName));

                    Integer maxLength = dataDictionaryService.getAttributeMaxLength(boClass, propertyName);
                    if (maxLength != null) {
                        attributeEntry.setAttributeMaxLength(maxLength.toString());
                    }

                    Pattern validationExpression = dataDictionaryService.getAttributeValidatingExpression(boClass, propertyName);
                    if (validationExpression != null) {
                        attributeEntry.setAttributeValidatingExpression(validationExpression.pattern());
                    }
                    ControlDefinition controlDef = dataDictionaryService.getAttributeControlDefinition(boClass, propertyName);
                    if (controlDef != null) {
                        attributeEntry.setAttributeControlType(controlDef.toString());
                    }
                    Class formatterClass = dataDictionaryService.getAttributeFormatter(boClass, propertyName);
                    if (formatterClass != null) {
                        attributeEntry.setAttributeFormatterClassName(formatterClass.getName());
                    }

                    // add to result list
                    searchResults.add(attributeEntry);
                }

            }

            // sort list if default sort column given
            List defaultSortColumns = getDefaultSortColumns();
            if (defaultSortColumns.size() > 0) {
                Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
            }
        }
        catch (ClassNotFoundException e) {
            LOG.error("Class not found for bo search name" + e.getMessage());
            throw new RuntimeException("Class not found for bo search name" + e.getMessage());
        }

        return searchResults;
    }


    /**
     * @see org.kuali.core.lookup.Lookupable#getReturnUrl(java.lang.Object, java.util.Map, java.lang.String)
     */
    @Override
    public String getReturnUrl(BusinessObject bo, Map fieldConversions, String lookupImpl) {
        return RiceConstants.EMPTY_STRING;
    }

    /**
     * @see org.kuali.core.lookup.KualiLookupableImpl#getMaintenanceUrl(java.lang.Object, java.lang.String)
     */
    @Override
    public String getMaintenanceUrl(BusinessObject bo, String methodToCall) {
        return RiceConstants.EMPTY_STRING;
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getDefaultReturnType()
     */
    public List getDefaultReturnType() {
        return new ArrayList();
    }

    /**
     * @see org.kuali.core.lookup.KualiLookupableImpl#getReturnKeys()
     */
    @Override
    public List getReturnKeys() {
        return new ArrayList();
    }
}
