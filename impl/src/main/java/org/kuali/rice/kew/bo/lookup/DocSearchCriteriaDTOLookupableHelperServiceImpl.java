/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.bo.lookup;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.CollectionFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.core.web.format.TimestampAMPMFormatter;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaBuilder;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaProcessor;
import org.kuali.rice.kew.docsearch.DocumentSearchEbo;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.SavedSearchResult;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.docsearch.service.DocumentSearchService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.lookup.valuefinder.SavedSearchValuesFinder;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lookupable helper class for Document Search.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocSearchCriteriaDTOLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final long serialVersionUID = -5162419674659967408L;
    private static final Pattern HREF_PATTERN = Pattern.compile("<a href=\"([^\"]+)\"");

    private static final String SAVED_SEARCH_NAME_PARAM = "savedSearchName";
    private static final String RESET_SAVED_SEARCH_PARAM = "resetSavedSearch";


	private DocumentLookupCriteriaProcessor documentLookupCriteriaProcessor;

	@Override
	public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {

    	Map<String, String[]> parameters = this.getParameters();

        String[] savedSearchNames = parameters.get(SAVED_SEARCH_NAME_PARAM);
        boolean savedSearch = savedSearchNames != null && savedSearchNames.length > 0 && StringUtils.isNotBlank(savedSearchNames[0]);

    	DocSearchCriteriaDTO criteria = null;
    	if (savedSearch) {
    		DocumentSearchService docSearchService = KEWServiceLocator.getDocumentSearchService();

    		String savedSearchName = savedSearchNames[0];

    		SavedSearchResult savedSearchResult = docSearchService.getSavedSearchResults(GlobalVariables.getUserSession().getPrincipalId(), savedSearchName);
    		if (savedSearchResult != null){
    			criteria = savedSearchResult.getDocSearchCriteriaDTO();
    		}
    	}

        // either it wasn't a saved search or the saved search failed to resolve
        if (criteria == null) {

    		Map<String,String[]> fixedParameters = new HashMap<String,String[]>();
    		Map<String,String> changedDateFields = preProcessRangeFields(lookupForm.getFieldsForLookup());
    		fixedParameters.putAll(parameters);
    		for (Map.Entry<String, String> prop : changedDateFields.entrySet()) {
				fixedParameters.remove(prop.getKey());
    			fixedParameters.put(prop.getKey(), new String[] { prop.getValue() });
			}

    		criteria = DocumentLookupCriteriaBuilder.populateCriteria(parameters);
    	}

    	Collection<DocumentSearchResult> displayList = null;

    	DocumentSearchResultComponents components = null;
    	try {
    		components = KEWServiceLocator.getDocumentSearchService().getList(GlobalVariables.getUserSession().getPrincipalId(), criteria);
    	} catch (WorkflowServiceErrorException wsee) {
    		for (WorkflowServiceError workflowServiceError : (List<WorkflowServiceError>)wsee.getServiceErrors()) {
				if(workflowServiceError.getMessageMap() != null && workflowServiceError.getMessageMap().hasErrors()){
					// merge the message maps
					GlobalVariables.getMessageMap().merge(workflowServiceError.getMessageMap());
				}else{
					//TODO: can we add something to this to get it to highlight the right field too?  Maybe in arg1
					GlobalVariables.getMessageMap().putError(workflowServiceError.getMessage(), RiceKeyConstants.ERROR_CUSTOM, workflowServiceError.getMessage());
				}
    		};
    	}

    	if(!GlobalVariables.getMessageMap().hasNoErrors()) {
        	throw new ValidationException("error with doc search");
        }

    	// check various warning conditions

    	if (criteria.isOverThreshold() && criteria.getSecurityFilteredRows() > 0) {
    	    GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES, "docsearch.DocumentSearchService.exceededThresholdAndSecurityFiltered", String.valueOf(components.getSearchResults().size()), String.valueOf(criteria.getSecurityFilteredRows()));
    	} else if (criteria.getSecurityFilteredRows() > 0) {
    	    GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES, "docsearch.DocumentSearchService.securityFiltered", String.valueOf(criteria.getSecurityFilteredRows()));
    	} else if (criteria.isOverThreshold()) {
    		GlobalVariables.getMessageMap().putWarning(KRADConstants.GLOBAL_MESSAGES,"docsearch.DocumentSearchService.exceededThreshold", String.valueOf(components.getSearchResults().size()));
    	}

    	for (Row row : this.getRows()) {
			for (Field field : row.getFields()) {
				if(StringUtils.equals(field.getPropertyName(),"fromDateCreated") && StringUtils.isEmpty(field.getPropertyValue())) {
					field.setPropertyValue(criteria.getFromDateCreated());
				}
			}
		}

    	List<DocumentSearchResult> result = components.getSearchResults();
		displayList = result;
        
        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        HashMap<String,Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List returnKeys = getReturnKeys();
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls

        DocumentSearchEbo element = new DocSearchCriteriaDTO();
        //TODO: additional BORestrictions through generator or component to lock down per document?
    	BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

        for (DocumentSearchResult documentSearchResult : result) {

        	String actionUrls = "";

            List<? extends Column> origColumns = components.getColumns();
            List<Column> newColumns = new ArrayList<Column>();
            List<KeyValueSort> keyValues = documentSearchResult.getResultContainers();
            for (int i = 0; i < origColumns.size(); i++) {

            	  Column col = (Column) origColumns.get(i);
            	  KeyValueSort keyValue = null;
            	  for (KeyValueSort keyValueFromList : keyValues) {
            		  if(StringUtils.equals(col.getPropertyName(), keyValueFromList.getKey())) {
            			  keyValue = keyValueFromList;
            			  break;
            		  }
            	  }
            	  if(keyValue==null) {
            		  //means we didn't find an indexed value for this, this seems bad but happens a lot we should research why
            		  keyValue = new KeyValueSort();
            	  }

            	  //Set values from keyvalue on column
            	  col.setPropertyValue(keyValue.getUserDisplayValue());

            	  String propertyName = col.getPropertyName();
				if(StringUtils.isEmpty(col.getColumnTitle())) {
            		  String labelMessageKey;
            		  if(StringUtils.equals(propertyName,KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG)) {
            			  //TODO: find a better place for this
            			  labelMessageKey = "Route Log";
            		  } else {
            			  //TODO: change this to an enum (or another dd property)
            			  propertyName=(StringUtils.equals(propertyName,"docTypeLabel"))?"docTypeFullName":propertyName;
            			  propertyName=(StringUtils.equals(propertyName,"docRouteStatusCodeDesc"))?"docRouteStatus":propertyName;
            			  propertyName=(StringUtils.equals(propertyName,"documentTitle"))?"docTitle":propertyName;
            			  labelMessageKey = getDataDictionaryService().getAttributeLabel(DocSearchCriteriaDTO.class,propertyName);
            		  }
            		  col.setColumnTitle(labelMessageKey);
            	  }

				if(StringUtils.equals(propertyName, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID)) {
					((DocSearchCriteriaDTO)element).setDocumentId(col.getPropertyValue());
				}

            	Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KRADConstants.EMPTY_STRING;
                Object prop=keyValue.getSortValue();

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(propertyName);
                if ( propClass == null ) {
                    try {
                    	if(prop!=null) {
                    		propertyTypes.put(propertyName, prop.getClass());
                    		propClass = prop.getClass();
                    	}

                    } catch (Exception e) {
                        // TODO why are we eating this?
                    }
                }


                //TODO: check exisiting formatter here, ideally we should be getting this formatter from col.getFormatter in most cases
                // formatters
                if (prop != null) {
                    if (formatter == null) {
                        // for Booleans, always use BooleanFormatter
                        if (prop instanceof Boolean) {
                            formatter = new BooleanFormatter();
                        }

                        // for Dates, always use DateFormatter
                        if (prop instanceof Date) {
                            formatter = new DateFormatter();
                        }
                        //#ADDED 3
                        if (prop instanceof Timestamp) {
                            formatter = new TimestampAMPMFormatter();
                        }

                        // for collection, use the list formatter if a formatter hasn't been defined yet
                        if (prop instanceof Collection && formatter == null) {
                            formatter = new CollectionFormatter();
                        }
                    }
                    if (formatter != null) {
                        //hack for Currency values as big decimal
                        if (prop instanceof BigDecimal  && formatter.getImplementationClass().equals("org.kuali.rice.krad.web.format.CurrencyFormatter")) {
                            prop = new KualiDecimal((BigDecimal)prop);
                        } else if (prop instanceof BigDecimal  && formatter.getImplementationClass().equals("org.kuali.rice.krad.web.format.PercentageFormatter")) {
                            prop = new KualiPercent((BigDecimal)prop);
                        }
                         propValue = (String) formatter.format(prop);
                    }
                    else {
                        propValue = prop.toString();
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                //TODO: can we call into a method in the result processor to get this (or set something on the criteria)
//                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
//                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
//ADDED (3 lines)
                	HtmlData.AnchorHtmlData anchor = new HtmlData.AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);
                	//TODO: change to grab URL from config variable
                	if(StringUtils.isNotEmpty(keyValue.getValue()) && StringUtils.equals("documentId", keyValue.getKey())) {
                	    String target = StringUtils.substringBetween(keyValue.getValue(), "target=\"", "\"");
                	    if (target == null) {
                	        target = "_self";
                	    }
                	    anchor.setTarget(target.trim());
                		if(!DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equals(criteria.getSuperUserSearch())) {
                			anchor.setHref(".."+KEWConstants.WEBAPP_DIRECTORY+"/"+StringUtils.substringBetween(keyValue.getValue(), "<a href=\"", "docId=")+"docId="+keyValue.getUserDisplayValue());
                		} else {
                			// KULRICE-3035: Append the doc search's returnLocation parameter to the superuser page URL.
    						String returnLoc = "";
    						if (this.getParameters().containsKey(KRADConstants.RETURN_LOCATION_PARAMETER)) {
    							returnLoc = (new StringBuilder()).append("&").append(KRADConstants.RETURN_LOCATION_PARAMETER).append("=").append(
    									((String[])this.getParameters().get(KRADConstants.RETURN_LOCATION_PARAMETER))[0]).toString();
    						}
    						else if (StringUtils.isNotBlank(this.getBackLocation())) {
    							returnLoc = (new StringBuilder()).append("&").append(KRADConstants.RETURN_LOCATION_PARAMETER).append("=").append(
    									this.getBackLocation()).toString();
    						}

                			anchor.setHref(".."+KEWConstants.WEBAPP_DIRECTORY+"/"+StringUtils.substringBetween(keyValue.getValue(), "<a href=\"", "documentId=")+"documentId="+keyValue.getUserDisplayValue() + returnLoc);
                		}
                        col.setMaxLength(100); //for now force this
                	} else if(StringUtils.isNotEmpty(keyValue.getValue()) && StringUtils.equals(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG, keyValue.getKey())) {
                		Matcher hrefMatcher = HREF_PATTERN.matcher(keyValue.getValue());
                		String matchedURL = "";
                		if (hrefMatcher.find()) {
                			matchedURL = hrefMatcher.group(1);
                		}
                		anchor.setHref(".."+KEWConstants.WEBAPP_DIRECTORY+"/"+matchedURL);
                		String target = StringUtils.substringBetween(keyValue.getValue(), "target=\"", "\"");
                        if (target == null) {
                            target = "_self";
                        }
                        anchor.setTarget(target.trim());
                		col.setMaxLength(100); //for now force this
                        keyValue.setValue(keyValue.getUserDisplayValue());
                        col.setEscapeXMLValue(false);
                	} else if (StringUtils.isNotEmpty(keyValue.getValue()) && StringUtils.equals(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR, keyValue.getKey())) {
                		anchor.setHref(StringUtils.substringBetween(keyValue.getValue(), "<a href=\"", "\" target=\"_blank\""));
                		col.setMaxLength(100); //for now force this
                	}

                	col.setColumnAnchor(anchor);

                }
                Column newCol = (Column)ObjectUtils.deepCopy(col);
                newColumns.add(newCol);

            }

            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
            ResultRow row = new ResultRow(newColumns, returnUrl.constructCompleteHtmlTag(), actionUrls);
            row.setRowId(returnUrl.getName());
            // because of concerns of the BO being cached in session on the ResultRow,
            // let's only attach it when needed (currently in the case of export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
            	//            	row.setBusinessObject(element);
            }

            //            boolean rowReturnable = isResultReturnable(element);
            //ADDED
            boolean rowReturnable = true;
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
            	hasReturnableRow = true;
            }
            resultTable.add(row);
        }


        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
		//####END COPIED CODE#########
	}

	@Override
	public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
		//FIXME: ctk - make sure and check that it's ok to do this here.  I may move this out to the doc search processor
		if(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_DOCUMENT_ID.equals(propertyName)) {

			HtmlData.AnchorHtmlData link = new HtmlData.AnchorHtmlData();
			DocumentRouteHeaderValue doc = (DocumentRouteHeaderValue)bo;
			//if !superuser
			String documentId = doc.getDocumentId();
			link.setDisplayText(documentId+"");

			String href = ConfigContext.getCurrentContextConfig().getKRBaseURL()+"/"+ KEWConstants.APP_CODE + "/" +
			KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KEWConstants.COMMAND_PARAMETER + "=" +
			KEWConstants.DOCSEARCH_COMMAND + "&" + KEWConstants.DOCUMENT_ID_PARAMETER + "=" + documentId;

			link.setHref(href);

			return link;
		}

		return super.getInquiryUrl(bo, propertyName);
	}

	@Override
	protected void setRows() {
	    this.setRows(new HashMap<String,String[]>(), null);
	}

	protected void setRows(Map fieldValues, String docTypeName) {
		// TODO chris - this method should call the criteria processor adapter which will
		//call the criteria processor (either standard or custom) and massage the data into the proper format
		//this is called by setbo in super(which is called by form) so should be called when the page needs refreshing

		//TODO: move over code that checks for doctype (actually should that be in the refresh, since that's where the doc type will be coming back to?)


		//###START LOOKUP ROW CODE Not sure if we need these but they may be valuable for eventually forcing all standard field customization in the xml
		if (getRows() == null) {
		    super.setRows();
		}
		List<Row> lookupRows = new ArrayList<Row>();
		//copy the current rows
		for (Row row : getRows()) {
			lookupRows.add(row);
		}
		//clear out
		getRows().clear();

		DocumentType docType = null;

		if(StringUtils.isNotEmpty(docTypeName)) {
			docType = getValidDocumentType((String)docTypeName);
		}

		boolean detailed=false;
		if(this.getParameters().containsKey("isAdvancedSearch")) {
			detailed = DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING.equalsIgnoreCase(((String[])this.getParameters().get("isAdvancedSearch"))[0]);
		} else if(fieldValues.containsKey("isAdvancedSearch")) {
			detailed = DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING.equalsIgnoreCase((String) fieldValues.get("isAdvancedSearch"));
		}

		boolean superSearch=false;
		if(this.getParameters().containsKey(("superUserSearch"))) {
			superSearch = DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equalsIgnoreCase(((String[])this.getParameters().get("superUserSearch"))[0]);
		} else if(fieldValues.containsKey("superUserSearch")) {
			superSearch = DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equalsIgnoreCase((String)fieldValues.get("superUserSearch"));
		}

		//call get rows
		List<Row> rows = getDocumentLookupCriteriaProcessor().getRows(docType,lookupRows, detailed, superSearch);

		BusinessObjectEntry boe = (BusinessObjectEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(this.getBusinessObjectClass().getName());
        int numCols = boe.getLookupDefinition().getNumOfColumns();
        if(numCols == 0) {
			numCols = KRADConstants.DEFAULT_NUM_OF_COLUMNS;
		}

		super.getRows().addAll(FieldUtils.wrapFields(this.getFields(rows), numCols));

	}

	 private List<Field> getFields(List<Row> rows){
	    	List<Field> rList = new ArrayList<Field>();

	    	for(Row r: rows){
	    		for(Field f: r.getFields()){
	    			rList.add(f);
	    		}
	    	}

	    	return rList;
	    }

	   private void setRowsAfterClear(DocSearchCriteriaDTO searchCriteria, Map<String,String[]> fieldValues) {
	        // TODO chris - this method should call the criteria processor adapter which will
	        //call the criteria processor (either standard or custom) and massage the data into the proper format
	        //this is called by setbo in super(which is called by form) so should be called when the page needs refreshing

	        //TODO: move over code that checks for doctype (actually should that be in the refresh, since that's where the doc type will be coming back to?)
	       if (getRows() == null) {
	            super.setRows();
	        }
	       List<Row> lookupRows = new ArrayList<Row>();
	        //copy the current rows
	        for (Row row : getRows()) {
	            lookupRows.add(row);
	        }
	        super.getRows().clear();

	        String docTypeName = searchCriteria.getDocTypeFullName();
	        DocumentType docType = null;

	        if(StringUtils.isNotEmpty(docTypeName)) {
	            docType = getValidDocumentType(docTypeName);
	        }

	        boolean detailed=false;
	        if(this.getParameters().containsKey("isAdvancedSearch")) {
	            detailed = DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING.equalsIgnoreCase(((String[])this.getParameters().get("isAdvancedSearch"))[0]);
	        } else if(fieldValues.containsKey("isAdvancedSearch")) {
	            detailed = DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING.equalsIgnoreCase((String) fieldValues.get("isAdvancedSearch")[0]);
	        }

	        boolean superSearch=false;
	        if(this.getParameters().containsKey(("superUserSearch"))) {
	            superSearch = DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equalsIgnoreCase(((String[])this.getParameters().get("superUserSearch"))[0]);
	        } else if(fieldValues.containsKey("superUserSearch")) {
	            superSearch = DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equalsIgnoreCase((String)fieldValues.get("superUserSearch")[0]);
	        }
	        //call get rows
	        List<Row> rows = getDocumentLookupCriteriaProcessor().getRows(docType, super.getRows(), detailed, superSearch);

	        super.getRows().addAll(rows);

	        //Set field values from DocSearchCriteria
	        if(StringUtils.isNotEmpty(docTypeName)) {
    	        for (Row row : super.getRows()) {
    	            for (Field field : row.getFields()) {
    	                //if from date, strip off prefix
    	                String propertyName = null;
    	                if(field.getPropertyName().startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
    	                    propertyName = StringUtils.remove(field.getPropertyName(), KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
    	                } else {
    	                    propertyName = field.getPropertyName();
    	                }
    	                //We don't need to set field if it was already empty
    	                if (fieldValues.get(propertyName) != null) {
        	                Object value = this.getDocSearchCriteriaDTOFieldValue(searchCriteria, field.getPropertyName());
        	                if (value instanceof String
        	                        && StringUtils.isNotEmpty((String)value)) {
        	                    field.setPropertyValue(value);
        	                } else if (value instanceof List){
        	                    field.setPropertyValues((String[])((List)value).toArray());
        	                }
    	                }
    	            }
    	        }
	        }
	    }


	@Override
	public void performClear(LookupForm lookupForm) {

        Map<String,String[]> fixedParameters = new HashMap<String,String[]>();
    	Map<String,String> changedDateFields = preProcessRangeFields(lookupForm.getFieldsForLookup());
    	fixedParameters.putAll(parameters);
    	for (Map.Entry<String, String> prop : changedDateFields.entrySet()) {
			fixedParameters.remove(prop.getKey());
    		fixedParameters.put(prop.getKey(), new String[] { prop.getValue() });
		}
        
		String docTypeName = getParameters().get("docTypeFullName")[0];

		DocumentType docType = getValidDocumentType(docTypeName);

		if(docType == null) {
		    super.performClear(lookupForm);

		    // Retrieve the detailed/superuser search statuses.
	        boolean detailed=false;
	        if(this.getParameters().containsKey("isAdvancedSearch")) {
	            detailed = DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING.equalsIgnoreCase(((String[])this.getParameters().get("isAdvancedSearch"))[0]);
	        }

	        boolean superSearch=false;
	        if(this.getParameters().containsKey(("superUserSearch"))) {
	            superSearch = DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equalsIgnoreCase(((String[])this.getParameters().get("superUserSearch"))[0]);
	        }

	        // Repopulate the fields indicating detailed/superuser search status.
	        int fieldsRepopulated = 0;
	        List<Row> rows = super.getRows();
	        int index = rows.size() - 1;
	        while (index >= 0 && fieldsRepopulated < 2) {
	        	for (Field tempField : rows.get(index).getFields()) {
	        		if ("isAdvancedSearch".equals(tempField.getPropertyName())) {
	        			tempField.setPropertyValue(detailed ? "YES" : "NO");
	        			fieldsRepopulated++;
	        		}
	        		else if ("superUserSearch".equals(tempField.getPropertyName())) {
	        			tempField.setPropertyValue(superSearch ? "YES" : "NO");
	        			fieldsRepopulated++;
	        		}
	        	}
	        	index--;
	        }
		} else {
    		DocSearchCriteriaDTO docCriteria = DocumentLookupCriteriaBuilder.populateCriteria(getParameters());
            DocSearchCriteriaDTO clearedCriteria = KEWServiceLocator.getDocumentLookupCustomizationMediator().customizeClearCriteria(docType, docCriteria);
            // if null, means custom clear was not needed, perform standard clear
            if (clearedCriteria == null) {
                clearedCriteria = docType.getDocumentSearchGenerator().clearSearch(docCriteria);
            }
            // if still null, just create an empty criteria object
            if (clearedCriteria == null) {
                clearedCriteria = new DocSearchCriteriaDTO();
            }

            this.setRowsAfterClear(clearedCriteria, getParameters());
		}

	}
	/**
	 *
	 * retrieve a document type. This is not a case sensitive search so "TravelRequest" == "Travelrequest"
	 *
	 * @param docTypeName
	 * @return
	 */
    private static DocumentType getValidDocumentType(String docTypeName) {
    	if (StringUtils.isNotEmpty(docTypeName)) {
            DocumentType dTypeCriteria = new DocumentType();
    		dTypeCriteria.setName(docTypeName.trim());
    		dTypeCriteria.setActive(true);
    		Collection<DocumentType> docTypeList = KEWServiceLocator.getDocumentTypeService().find(dTypeCriteria, null, false);

    		// Return the first valid doc type.
    		DocumentType firstDocumentType = null;
    		if(docTypeList != null){
    			for(DocumentType dType: docTypeList){
    			    if (firstDocumentType == null) {
    			        firstDocumentType = dType;
    			    }
    			    if (StringUtils.equals(docTypeName.toUpperCase(), dType.getName().toUpperCase())) {
    			        return dType;
    			    }
    			}
    			return firstDocumentType;
    		}
    	}

    	return null;
    }

	@Override
	public String getSupplementalMenuBar() {
		boolean detailed = false;
		if(this.getParameters().containsKey("isAdvancedSearch")) {
			detailed = DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING.equalsIgnoreCase(((String[])this.getParameters().get("isAdvancedSearch"))[0]);
		}

		boolean superSearch = false;
		if(this.getParameters().containsKey("superUserSearch")) {
			superSearch = DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equalsIgnoreCase(((String[])this.getParameters().get("superUserSearch"))[0]);
		}

		StringBuilder suppMenuBar = new StringBuilder();

		// Add the detailed-search-toggling button.
		suppMenuBar.append("<input type=\"image\" name=\"methodToCall.customLookupableMethodCall.(((").append(detailed ? "NO" : DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING).append("))).((`").append(superSearch ? DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING : "NO").append(
				"`))\" class=\"tinybutton\" src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append(detailed ? "/images/tinybutton-basicsearch.gif\" alt=\"basic search\" title=\"basic search\" />" : "/images/tinybutton-detailedsearch.gif\" alt=\"detailed search\" title=\"detailed search\" />");

		// Add the superuser-search-toggling button.
		suppMenuBar.append("&nbsp;").append("<input type=\"image\" name=\"methodToCall.customLookupableMethodCall.(((").append((!detailed && superSearch) ? "NO" : DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING).append("))).((`").append(superSearch ? "NO" : DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING).append(
				"`))\" class=\"tinybutton\" src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append(superSearch ? "/images/tinybutton-nonsupusearch.gif\" alt=\"non-superuser search\" title=\"non-superuser search\" />" : "/images/tinybutton-superusersearch.gif\" alt=\"superuser search\" title=\"superuser search\" />");

		// Add the "clear saved searches" button.
		suppMenuBar.append("&nbsp;").append("<input type=\"image\" name=\"methodToCall.customLookupableMethodCall.(([true]))\" class=\"tinybutton\" src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append("/images/tinybutton-clearsavedsearch.gif\" alt=\"clear saved searches\" title=\"clear saved searches\" />");

        Properties parameters = new Properties();
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, this.getBusinessObjectClass().getName());
        this.getParameters().keySet();
        for (Object parameter : this.getParameters().keySet()) {
			parameters.put(parameter, this.getParameters().get(parameter));
		}

		UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, parameters);
		return suppMenuBar.toString();
	}

	@Override
	public boolean shouldDisplayHeaderNonMaintActions() {
		return getDocumentLookupCriteriaProcessor().shouldDisplayHeaderNonMaintActions();
	}

	@Override
	public boolean shouldDisplayLookupCriteria() {
		return getDocumentLookupCriteriaProcessor().shouldDisplayLookupCriteria();
	}

	@Override
	public boolean checkForAdditionalFields(Map fieldValues) {
		String docTypeName = null;
		if(this.getParameters().get("docTypeFullName")!=null) {
			docTypeName = ((String[])this.getParameters().get("docTypeFullName"))[0];
		}
		else if(fieldValues.get("docTypeFullName")!=null) {
			docTypeName = (String)fieldValues.get("docTypeFullName");
		}
		if(!StringUtils.isEmpty(docTypeName)) {
		    DocumentType dType = getValidDocumentType(docTypeName);

            DocSearchCriteriaDTO criteria = DocumentLookupCriteriaBuilder.populateCriteria(getParameters());
            if (dType != null) {
	            MessageMap messages = getValidDocumentType(dType.getName()).getDocumentSearchGenerator().getMessageMap(criteria);
	            if (messages != null
	                    && messages.hasMessages()) {
	                GlobalVariables.mergeErrorMap(messages);
	            }
	            setRows(fieldValues,dType.getName());
	            return true;
            }
        }

		setRows(fieldValues, docTypeName);

		return true;
	}

	@Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);
        DocumentSearchService docSearchService = KEWServiceLocator.getDocumentSearchService();
        DocSearchCriteriaDTO criteria = DocumentLookupCriteriaBuilder.populateCriteria(getParameters());
        DocumentType docType = null;
        if(StringUtils.isNotEmpty(criteria.getDocTypeFullName())) {
			docType = getValidDocumentType((String)criteria.getDocTypeFullName());
		}
        DocumentSearchGenerator generator=null;
        if(docType!=null) {
        	generator = docType.getDocumentSearchGenerator();
        } else {
        	generator = KEWServiceLocator.getDocumentSearchService().getStandardDocumentSearchGenerator();
        }
        try {
        	docSearchService.validateDocumentSearchCriteria(generator, criteria);
		} catch (WorkflowServiceErrorException wsee) {
			for (WorkflowServiceError workflowServiceError : (List<WorkflowServiceError>)wsee.getServiceErrors()) {
				if(workflowServiceError.getMessageMap() != null && workflowServiceError.getMessageMap().hasErrors()){
					// merge the message maps
					GlobalVariables.getMessageMap().merge(workflowServiceError.getMessageMap());
				}else{
					//TODO: can we add something to this to get it to highlight the right field too?  Maybe in arg1
					GlobalVariables.getMessageMap().putError(workflowServiceError.getMessage(), RiceKeyConstants.ERROR_CUSTOM, workflowServiceError.getMessage());
				}
			};
		}
        if(!GlobalVariables.getMessageMap().hasNoErrors()) {
        	throw new ValidationException("errors in search criteria");
        }

    }

	@Override
	public boolean performCustomAction(boolean ignoreErrors) {
		DocumentSearchService docSearchService = KEWServiceLocator.getDocumentSearchService();

		Map<String,String> fieldValues = new HashMap<String,String>();
		Map<String,String[]> multFieldValues = new HashMap<String,String[]>();

		// Determine if there are any properties embedded in the methodToCall parameter, and retrieve them if so.
		String[] methodToCallArray = ((String[])this.getParameters().get(
                KRADConstants.DISPATCH_REQUEST_PARAMETER + ".customLookupableMethodCall"));
		if (methodToCallArray == null) {
    		for (String parameter: new ArrayList<String>(this.getParameters().keySet())) {
    		    String[] parameterSplit = StringUtils.split(parameter, ".");
    		    String parameterValue = "";
    		    if (StringUtils.contains(parameter, KRADConstants.DISPATCH_REQUEST_PARAMETER + ".customLookupableMethodCall.")) {
    		        if (parameter.trim().endsWith(".x")) {
    		            parameterValue = StringUtils.substringBetween(parameter, KRADConstants.DISPATCH_REQUEST_PARAMETER + ".customLookupableMethodCall.", ".x");
    		        } else if (parameter.trim().endsWith(".y")) {
    		            parameterValue = StringUtils.substringBetween(parameter, KRADConstants.DISPATCH_REQUEST_PARAMETER + ".customLookupableMethodCall.", ".y");
    		        } 
    		        if (StringUtils.isNotEmpty(parameterValue)) {
    		            methodToCallArray = new String[]{ parameterValue };
    	                break;
    		        }
    		    }
    		}
		}
		//String[] methodToCallArray = ((String[])this.getParameters().get(KRADConstants.DISPATCH_REQUEST_PARAMETER + ".customLookupableMethodCall"));
		if (ObjectUtils.isNotNull(methodToCallArray) && methodToCallArray.length > 0) {
			String methodToCallVal = methodToCallArray[0];
			if (StringUtils.isNotBlank(methodToCallVal)) {
				boolean resetRows = false;
				// Retrieve the isAdvancedSearch property, if present.
				String advancedSearchVal = StringUtils.substringBetween(methodToCallVal, KRADConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
				if (StringUtils.isNotBlank(advancedSearchVal)) {
					if (this.getParameters().containsKey("isAdvancedSearch")) {
						((String[]) this.getParameters().get("isAdvancedSearch"))[0] = advancedSearchVal;
					}
					else {
						fieldValues.put("isAdvancedSearch", advancedSearchVal);
					}
					resetRows = true;
				}
				// Retrieve the superUserSearch property, if present.
				String superUserSearchVal = StringUtils.substringBetween(methodToCallVal, KRADConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
				if (StringUtils.isNotBlank(superUserSearchVal)) {
					if (this.getParameters().containsKey("superUserSearch")) {
						((String[]) this.getParameters().get("superUserSearch"))[0] = superUserSearchVal;
					} else {
						fieldValues.put("superUserSearch", superUserSearchVal);
					}
					resetRows = true;
				}
				// Retrieve and act upon the resetSavedSearch property, if present and set to true.
				String resetSavedSearchVal = StringUtils.substringBetween(methodToCallVal, KRADConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KRADConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
				if (Boolean.parseBoolean(resetSavedSearchVal)) {
					docSearchService.clearNamedSearches(GlobalVariables.getUserSession().getPrincipalId());
					resetRows = true;
				}

				// If any of the above properties were found, reset the rows in a manner similar to KualiLookupAction.refresh, but with
				// enough modifications to prevent any changed isAdvancedSearch or superUserSearch values from being overridden again.
				if (resetRows) {
					Map<String,String> values = new HashMap<String,String>();
					for (Field tempField : getFields(this.getRows())) {
						values.put(tempField.getPropertyName(), tempField.getPropertyValue());
					}

			        for (Row row : this.getRows()) {
			        	for (Field field : row.getFields()) {
			        		if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
			        			if (this.getParameters().get(field.getPropertyName()) != null) {
			        				if(!Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
			        					field.setPropertyValue(((String[])this.getParameters().get(field.getPropertyName()))[0]);
			        				} else {
			        					//multi value, set to values
			        					field.setPropertyValues((String[])this.getParameters().get(field.getPropertyName()));
			        				}
			        			}
			        		}
			        		else if (values.get(field.getPropertyName()) != null) {
			        			field.setPropertyValue(values.get(field.getPropertyName()));
			        		}

			        		applyFieldAuthorizationsFromNestedLookups(field);

			        		fieldValues.put(field.getPropertyName(), field.getPropertyValue());
			        	}
			        }

			        if (checkForAdditionalFields(fieldValues)) {
			            for (Row row : this.getRows()) {
			                for (Field field : row.getFields()) {
			                    if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
			                        if (this.getParameters().get(field.getPropertyName()) != null) {
			                        	if(!Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
			            					field.setPropertyValue(((String[])this.getParameters().get(field.getPropertyName()))[0]);
			            				} else {
			            					//multi value, set to values
			            					field.setPropertyValues((String[])this.getParameters().get(field.getPropertyName()));
			            				}
			            				//FIXME: any reason this is inside this "if" instead of the outer one, like above - this seems inconsistent
			            				fieldValues.put(field.getPropertyName(), ((String[])this.getParameters().get(field.getPropertyName()))[0]);
			                        }
			                        else if (values.get(field.getPropertyName()) != null) {
			                        	field.setPropertyValue(values.get(field.getPropertyName()));
			                        }
			                    }
			                }
			            }
			        }
			        // Finally, return false to prevent the search from being performed and to skip the other custom processing below.
			        return false;
				}

			}
		} // End of methodToCall parameter retrieval.

		String[] resetSavedSearch = ((String[])getParameters().get(RESET_SAVED_SEARCH_PARAM));
		if(resetSavedSearch!=null) {
			docSearchService.clearNamedSearches(GlobalVariables.getUserSession().getPrincipalId());
			setRows(fieldValues,"");
			return false;
		}

		String savedSearchName = ((String[])getParameters().get(SAVED_SEARCH_NAME_PARAM))[0];
		if(StringUtils.isEmpty(savedSearchName) || "*ignore*".equals(savedSearchName)) {
			if(!ignoreErrors) {
				GlobalVariables.getMessageMap().putError(SAVED_SEARCH_NAME_PARAM, RiceKeyConstants.ERROR_CUSTOM, "You must select a saved search");
			} else {
				//if we're ignoring errors and we got an error just return, no reason to continue.  Also set false to indicate not to perform lookup
				return false;
			}
			//TODO: is there a better way to override a single field value?
			List<Row> rows = this.getRows();
			for (Row row : rows) {
				List<Field> fields = row.getFields();
				for (Field field : fields) {
					if(StringUtils.equals(field.getPropertyName(), SAVED_SEARCH_NAME_PARAM)) {
						field.setPropertyValue("");
						break;
					}
				}
			}
		}
        if (!GlobalVariables.getMessageMap().hasNoErrors()) {
            throw new ValidationException("errors in search criteria");
        }


		SavedSearchResult savedSearchResult = null;
		if(StringUtils.isNotEmpty(savedSearchName)) {
			savedSearchResult = docSearchService.getSavedSearchResults(GlobalVariables.getUserSession().getPrincipalId(), savedSearchName);
		}
		DocSearchCriteriaDTO criteria=null;
		if(savedSearchResult!=null){
			criteria = savedSearchResult.getDocSearchCriteriaDTO();
		}
		//put the doc type from the criteria in the map to send to setRows and then call setRows
		String docTypeName = criteria.getDocTypeFullName();


		setRows(fieldValues,docTypeName);

		fieldValues.put(SAVED_SEARCH_NAME_PARAM, "");


		for (SearchAttributeCriteriaComponent searchAtt : criteria.getSearchableAttributes()) {
			if(!searchAtt.isCanHoldMultipleValues()) {
				fieldValues.put(searchAtt.getSavedKey(), searchAtt.getValue());
			} else {
				List<String> values = searchAtt.getValues();
				String[] arrayValues = {};
				arrayValues = values.toArray(arrayValues);
				multFieldValues.put(searchAtt.getSavedKey(), arrayValues);
			}

		}
		Object fieldValue = null;
        for (Row row2 : getRows()) {
            Row row = (Row) row2;
            for (Field field2 : row.getFields()) {
                Field field = (Field) field2;
                if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                	if (fieldValues.get(field.getPropertyName()) != null) {
        				field.setPropertyValue(fieldValues.get(field.getPropertyName()));
                    } else if(multFieldValues.get(field.getPropertyName())!=null){
         				field.setPropertyValues(multFieldValues.get(field.getPropertyName()));
                    } else {
                    	//may be on the root of the criteria object, try looking there:
                    	try {
							fieldValue = PropertyUtils.getProperty(criteria, field.getPropertyName());
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
//							e.printStackTrace();
							//hmm what to do here, we should be able to find everything either in the search atts or at the base as far as I know.
						}
						if(fieldValue!=null) {
							field.setPropertyValue(fieldValue);
						}

                    }
                }
            }
        }

        return true;
	}

	/**
	 * @see org.kuali.rice.krad.lookup.AbstractLookupableHelperServiceImpl#getExtraField()
	 */
	@Override
	public Field getExtraField() {
		SavedSearchValuesFinder savedSearchValuesFinder = new SavedSearchValuesFinder();
		List<KeyValue> savedSearchValues = savedSearchValuesFinder.getKeyValues();

		Field savedSearch = new Field();
		savedSearch.setPropertyName(SAVED_SEARCH_NAME_PARAM);
		savedSearch.setFieldType(Field.DROPDOWN_SCRIPT);
		savedSearch.setScript("customLookupChanged()");
		savedSearch.setFieldValidValues(savedSearchValues);
		savedSearch.setFieldLabel("Saved Searches");
		return savedSearch;

	}

	private Object getDocSearchCriteriaDTOFieldValue (DocSearchCriteriaDTO searchCriteria, String fieldName) {
        Class<?> clazz = searchCriteria.getClass();
        String propertyName = fieldName;
        if(fieldName.startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
            propertyName = StringUtils.remove(fieldName, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
        }
        try {
            String methodName = new StringBuffer("get").append(propertyName.toUpperCase().charAt(0)).append(propertyName.substring(1)).toString();
            java.lang.reflect.Method method = clazz.getMethod(methodName);
            return method.invoke(searchCriteria);
        } catch (SecurityException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return getSearchableAttributeFieldValue(searchCriteria, fieldName);
        }
    }

	private Object getSearchableAttributeFieldValue(DocSearchCriteriaDTO searchCriteria, String fieldName) {
	    Object valueToReturn = null;
	    String propertyName = fieldName;
	    boolean isDateTime = false;
        if(fieldName.startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
            propertyName = StringUtils.remove(fieldName, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
        }
	    if (searchCriteria.getSearchableAttributes() != null) {
    	    for (SearchAttributeCriteriaComponent sa : searchCriteria.getSearchableAttributes()) {
    	        if (StringUtils.equals(propertyName, sa.getFormKey())) {
    	            if (StringUtils.equals(sa.getSearchableAttributeValue().getOjbConcreteClass(), "org.kuali.rice.kew.docsearch.SearchableAttributeDateTimeValue")) {
    	                isDateTime = true;
    	            }
    	            if (sa.isCanHoldMultipleValues()) {
    	                valueToReturn = sa.getValues();
    	            } else {
    	                valueToReturn = sa.getValue();
    	            }
    	            break;
    	        }
    	    }
	    }

	    if (valueToReturn != null
	            && valueToReturn instanceof String
	            && isDateTime) {
	        if(fieldName.startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
    	        if (StringUtils.contains((String)valueToReturn, SearchOperator.BETWEEN.op())) {
    	            valueToReturn = StringUtils.split((String)valueToReturn, SearchOperator.BETWEEN.op())[0];
    	        } else if (StringUtils.contains((String)valueToReturn, SearchOperator.GREATER_THAN_EQUAL.op())) {
    	            valueToReturn = StringUtils.split((String)valueToReturn, SearchOperator.GREATER_THAN_EQUAL.op())[0];
    	        } else {
    	            valueToReturn = null;
    	        }
	        } else {
	            if (StringUtils.contains((String)valueToReturn, SearchOperator.BETWEEN.op())) {
                    valueToReturn = StringUtils.split((String)valueToReturn, SearchOperator.BETWEEN.op())[1];
                } else if (StringUtils.contains((String)valueToReturn, SearchOperator.GREATER_THAN_EQUAL.op())) {
                    valueToReturn = null;
                } else if (StringUtils.contains((String)valueToReturn, SearchOperator.LESS_THAN_EQUAL.op())) {
                    valueToReturn = StringUtils.split((String)valueToReturn, SearchOperator.LESS_THAN_EQUAL.op())[0];
                }

	        }
	    }

	    return valueToReturn;
	}

	protected DocumentLookupCriteriaProcessor getDocumentLookupCriteriaProcessor() {
        return documentLookupCriteriaProcessor;
    }

    public void setDocumentLookupCriteriaProcessor(DocumentLookupCriteriaProcessor documentLookupCriteriaProcessor) {
        this.documentLookupCriteriaProcessor = documentLookupCriteriaProcessor;
    }

}
