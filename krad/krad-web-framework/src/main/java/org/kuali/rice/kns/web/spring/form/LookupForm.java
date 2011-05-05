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
package org.kuali.rice.kns.web.spring.form;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.uif.UifConstants.ViewType;
import org.kuali.rice.kns.uif.UifParameters;
import org.kuali.rice.kns.uif.container.LookupView;
import org.kuali.rice.kns.uif.service.LookupViewHelperService;
import org.kuali.rice.kns.uif.service.ViewHelperService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class is the model for Lookups
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupForm extends UifFormBase {
	private static final long serialVersionUID = -7323484966538685327L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquiryForm.class);

	private String dataObjectClassName;
	private String docNum;
	private Map<String, String> criteriaFields;
	private Map<String, String> criteriaFieldsForLookup;
	private String conversionFields;
	private Map<String, String> fieldConversions;
	
	private Collection<?> searchResults;

    public LookupForm() {
    	super();
    	setViewTypeName(ViewType.LOOKUP);
    }

	public String getDataObjectClassName() {
    	return this.dataObjectClassName;
    }

	public void setDataObjectClassName(String dataObjectClassName) {
    	this.dataObjectClassName = dataObjectClassName;
    }

	public String getDocNum() {
    	return this.docNum;
    }

	public void setDocNum(String docNum) {
    	this.docNum = docNum;
    }

	public Map<String, String> getCriteriaFields() {
    	return this.criteriaFields;
    }

	public void setCriteriaFields(Map<String, String> criteriaFields) {
    	this.criteriaFields = criteriaFields;
    }

	public Map<String, String> getCriteriaFieldsForLookup() {
    	return this.criteriaFieldsForLookup;
    }

	public void setCriteriaFieldsForLookup(Map<String, String> criteriaFieldsForLookup) {
    	this.criteriaFieldsForLookup = criteriaFieldsForLookup;
    }

	public String getConversionFields() {
    	return this.conversionFields;
    }

	public void setConversionFields(String conversionFields) {
    	this.conversionFields = conversionFields;
    }

	public Map<String, String> getFieldConversions() {
    	return this.fieldConversions;
    }

	public void setFieldConversions(Map<String, String> fieldConversions) {
    	this.fieldConversions = fieldConversions;
    }

	public Collection<?> getSearchResults() {
    	return this.searchResults;
    }

	public void setSearchResults(Collection<?> searchResults) {
    	this.searchResults = searchResults;
    }

	protected LookupViewHelperService getLookupViewHelperServiceFromModel() {
        ViewHelperService viewHelperService = getView().getViewHelperService();
        if (viewHelperService == null) {
            LOG.error("ViewHelperService is null.");
            throw new RuntimeException("ViewHelperService is null.");
        }
        if (!LookupViewHelperService.class.isAssignableFrom(viewHelperService.getClass())) {
            LOG.error("ViewHelperService class '" + viewHelperService.getClass().getName() + "' is not assignable from '" + LookupViewHelperService.class + "'");
            throw new RuntimeException("ViewHelperService class '" + viewHelperService.getClass().getName() + "' is not assignable from '" + LookupViewHelperService.class + "'");
        }
        return (LookupViewHelperService) viewHelperService;
	}

	/**
	 * Picks out business object name from the request to get retrieve a
	 * lookupable and set properties.
	 */
	@Override
	public void postBind(HttpServletRequest request) {
		super.postBind(request);

		try {
			LookupViewHelperService localLookupViewHelperService = getLookupViewHelperServiceFromModel();

			if (localLookupViewHelperService == null) {
				LOG.error("LookupViewHelperService not found for view id " + getView().getId());
				throw new RuntimeException("LookupViewHelperService not found for view id " + getView().getId());
			}

			// check the doc form key is empty before setting so we don't
			// override a restored lookup form
//			if (request.getAttribute(KNSConstants.DOC_FORM_KEY) != null && StringUtils.isBlank(this.getFormKey())) {
//				setFormKey((String) request.getAttribute(KNSConstants.DOC_FORM_KEY));
//			} else if (request.getParameter(KNSConstants.DOC_FORM_KEY) != null && StringUtils.isBlank(this.getFormKey())) {
//				setFormKey(request.getParameter(KNSConstants.DOC_FORM_KEY));
//			}

            // if showMaintenanceLinks is not already true, only show maintenance links if the lookup was called from the portal (or index.html for the generated applications)
            if (!((LookupView)getView()).isShowMaintenanceLinks()) {
            	// TODO delyea - is this the best way to decide whether to display the maintenance actions?
            	if (StringUtils.contains(getReturnLocation(), "/"+KNSConstants.PORTAL_ACTION) 
            			|| StringUtils.contains(getReturnLocation(), "/index.html")) {
            		((LookupView)getView()).setShowMaintenanceLinks(true);
            	}
            }
            
			// this used to be in the Form as a property but has been moved for KRAD
//			String hideReturnLink = request.getParameter("hideReturnLink");
//			Boolean hideReturnLinkValue = processBooleanParameter(hideReturnLink);
//			if (hideReturnLinkValue != null) {
//				localLookupViewHelperService.setHideReturnLink(hideReturnLinkValue.booleanValue());
//			}

//			if (request.getParameter("conversionFields") != null) {
//				setConversionFields(request.getParameter("conversionFields"));
//			}
//			String value = request.getParameter("multipleValues");
//			if (value != null) {
//				if ("YES".equals(value.toUpperCase())) {
//					setMultipleValues(true);
//				} else {
//					setMultipleValues(new Boolean(request.getParameter("multipleValues")).booleanValue());
//				}
//			}
//			if (request.getParameter(KNSConstants.REFERENCES_TO_REFRESH) != null) {
//				setReferencesToRefresh(request.getParameter(KNSConstants.REFERENCES_TO_REFRESH));
//			}

			if (request.getParameter(KNSConstants.LOOKUP_READ_ONLY_FIELDS) != null) {
				setReadOnlyFields(request.getParameter(KNSConstants.LOOKUP_READ_ONLY_FIELDS));
				setReadOnlyFieldsList(LookupUtils.translateReadOnlyFieldsToList(getReadOnlyFields()));
				localLookupViewHelperService.setReadOnlyFieldsList(getReadOnlyFieldsList());
			}

			// init lookupable with bo class
			Class<?> boClass = Class.forName(getDataObjectClassName());
			localLookupViewHelperService.setDataObjectClass(boClass);
			Map<String, String> fieldValues = new HashMap<String, String>();
			Map<String, String> formFields = getCriteriaFields();

			if (formFields != null) {
				for (Map.Entry<String, String> entry : formFields.entrySet()) {
					// check here to see if this field is a criteria element on the form
		            fieldValues.put(entry.getKey(), LookupUtils.forceUppercase(boClass, entry.getKey(), entry.getValue()));
	            }
			}
			
			// populate values into the localLookupable Field list
//			for (Iterator iter = localLookupable.getRows().iterator(); iter.hasNext();) {
//				Row row = (Row) iter.next();
//
//				for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
//					Field field = (Field) iterator.next();
//
//					// check whether form already has value for field
//					if (formFields != null && formFields.containsKey(field.getPropertyName())) {
//						field.setPropertyValue(formFields.get(field.getPropertyName()));
//					}
//
//					field.setPropertyValue(LookupUtils.forceUppercase(boClass, field.getPropertyName(), field.getPropertyValue()));
//					fieldValues.put(field.getPropertyName(), field.getPropertyValue());
//					localLookupable.applyFieldAuthorizationsFromNestedLookups(field);
//				}
//			}

			// check the lookupableImpl to see if there are additional fields
//			if (localLookupable.checkForAdditionalFields(fieldValues)) {
//				// populate values into the localLookupable Field list again because additional rows may have been added
//				for (Iterator iter = localLookupable.getRows().iterator(); iter.hasNext();) {
//					Row row = (Row) iter.next();
//
//					for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
//						Field field = (Field) iterator.next();
//
//						// check whether form already has value for field
//						if (formFields != null && formFields.containsKey(field.getPropertyName())) {
//							field.setPropertyValue(formFields.get(field.getPropertyName()));
//						}
//
//						// override values with request
//						if (request.getParameter(field.getPropertyName()) != null) {
//							if (!Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
//								field.setPropertyValue(request.getParameter(field.getPropertyName()).trim());
//							} else {
//								// multi value, set to values
//								field.setPropertyValues(request.getParameterValues(field.getPropertyName()));
//							}
//						}
//						fieldValues.put(field.getPropertyName(), field.getPropertyValue());
//					}
//				}
//
//			}
			fieldValues.put(UifParameters.RETURN_FORM_KEY, getReturnFormKey());
			fieldValues.put(UifParameters.RETURN_LOCATION, getReturnLocation());
			if (StringUtils.isNotBlank(getDocNum())) {
				fieldValues.put(KNSConstants.DOC_NUM, getDocNum());
			}
//			if (StringUtils.isNotBlank(getReferencesToRefresh())) {
//				fieldValues.put(KNSConstants.REFERENCES_TO_REFRESH, this.getReferencesToRefresh());
//			}

			this.setCriteriaFields(fieldValues);

			setFieldConversions(LookupUtils.translateFieldConversions(getConversionFields()));
			localLookupViewHelperService.setFieldConversions(getFieldConversions());
			localLookupViewHelperService.setDocNum(getDocNum());
//			setLookupViewHelperService(localLookupViewHelperService);
			setCriteriaFieldsForLookup(fieldValues);

			// if showMaintenanceLinks is not already true, only show
			// maintenance links if the lookup was called from the portal (or
			// index.html for the generated applications)
			// TODO delyea - how to handle whether to show maintenance links or return links
//			if (!isShowMaintenanceLinks()) {
//				if (StringUtils.contains(getBackLocation(), "/" + KNSConstants.PORTAL_ACTION) || StringUtils.contains(getBackLocation(), "/index.html")) {
//					showMaintenanceLinks = true;
//				}
//			}
		} catch (ClassNotFoundException e) {
			LOG.error("Object class " + getDataObjectClassName() + " not found");
			throw new RuntimeException("Object class " + getDataObjectClassName() + " not found", e);
		}
	}

	protected Boolean processBooleanParameter(String parameterValue) {
		if (StringUtils.isNotBlank(parameterValue)) {
			if ("YES".equals(parameterValue.toUpperCase())) {
				return Boolean.TRUE;
			}
			return new Boolean(parameterValue);
		}
		return null;
	}

	/**
	 * BELOW COPIED FROM LookupForm
	 */

    private String readOnlyFields;
    private List<String> readOnlyFieldsList;

	public String getReadOnlyFields() {
    	return this.readOnlyFields;
    }

	public void setReadOnlyFields(String readOnlyFields) {
    	this.readOnlyFields = readOnlyFields;
    }

	public List<String> getReadOnlyFieldsList() {
    	return this.readOnlyFieldsList;
    }

	public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
    	this.readOnlyFieldsList = readOnlyFieldsList;
    }

}
