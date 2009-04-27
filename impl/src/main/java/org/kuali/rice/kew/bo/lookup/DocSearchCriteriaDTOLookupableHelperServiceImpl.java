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
package org.kuali.rice.kew.bo.lookup;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaBuilder;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaProcessor;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaProcessorKEWAdapter;
import org.kuali.rice.kew.docsearch.DocumentRouteHeaderEBO;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.SavedSearchResult;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.SearchableAttributeDateTimeValue;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchCriteriaProcessor;
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
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.CollectionFormatter;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.format.TimestampAMPMFormatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;

/**
 * Lookupable helper class for new doc search
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocSearchCriteriaDTOLookupableHelperServiceImpl extends
KualiLookupableHelperServiceImpl {

	private static final long serialVersionUID = -5162419674659967408L;
	DateTimeService dateTimeService;
	DocumentLookupCriteriaProcessor processor;
	boolean savedSearch = false;

	/**
	 * @see org.kuali.rice.kew.bo.lookup.DocumentRouteHeaderValueLookupableHelperService#setDateTimeService(org.kuali.rice.kns.service.DateTimeService)
	 */
	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)
	 */
	@Override
	public Collection performLookup(LookupForm lookupForm,
			Collection resultTable, boolean bounded) {

		//TODO: ideally implement KNS updates to make this not require code from the parent

    	Map<String,String[]> parameters = this.getParameters();

    	DocSearchCriteriaDTO criteria = null;
    	if(savedSearch) {
    		//TODO: set the criteria on this from below method instead of this (so we're not calling out twice for the same object)
    		DocumentSearchService docSearchService = KEWServiceLocator.getDocumentSearchService();

    		String savedSearchName = ((String[])getParameters().get("savedSearchName"))[0];
    		SavedSearchResult savedSearchResult = null;
    		if(StringUtils.isNotEmpty(savedSearchName)) {
    			savedSearchResult = docSearchService.getSavedSearchResults(GlobalVariables.getUserSession().getPrincipalId(), savedSearchName);
    		}
    		if(savedSearchResult!=null){
    			criteria = savedSearchResult.getDocSearchCriteriaDTO();
    		}
    		savedSearch=false;
    	} else {
    		Map<String,String[]> fixedParameters = new HashMap<String,String[]>();
    		Map<String,String> changedDateFields = preprocessDateFields(lookupForm.getFieldsForLookup());
    		fixedParameters.putAll(this.getParameters());
    		for (Map.Entry<String,String> prop : changedDateFields.entrySet()) {
				fixedParameters.remove(prop.getKey());
    			fixedParameters.put(prop.getKey(), new String[]{prop.getValue()});
			}
    		criteria = DocumentLookupCriteriaBuilder.populateCriteria(fixedParameters);
    		
    	}

    	Collection displayList=null;
    	
    	DocumentSearchResultComponents components = null;
    	try {
    		components = KEWServiceLocator.getDocumentSearchService().getList(GlobalVariables.getUserSession().getPrincipalId(), criteria);	
    	} catch (WorkflowServiceErrorException wsee) {
    		for (WorkflowServiceError workflowServiceError : (List<WorkflowServiceError>)wsee.getServiceErrors()) {
    			GlobalVariables.getErrorMap().putError(workflowServiceError.getMessage(), RiceKeyConstants.ERROR_CUSTOM, workflowServiceError.getMessage());
    		};
    	}
    	
    	//FIXME: for now if not set set the create date back from the criteria, however eventually we should convert all
    	for (Row row : this.getRows()) {
			for (Field field : row.getFields()) {
				if(StringUtils.equals(field.getPropertyName(),"fromDateCreated") && StringUtils.isEmpty(field.getPropertyValue())) {
					field.setPropertyValue(criteria.getFromDateCreated());
				}
			}
		}

    	List<DocumentSearchResult> result = components.getSearchResults();
//    	for (DocumentSearchResult documentSearchResult : result) {
			displayList = result;//.getResultContainers();
//		}

		//####BEGIN COPIED CODE#########
        setBackLocation((String) lookupForm.getFieldsForLookup().get(KNSConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KNSConstants.DOC_FORM_KEY));

//###COMENTED OUT
//		  Collection displayList;
//        // call search method to get results
//        if (bounded) {
//            displayList = getSearchResults(lookupForm.getFieldsForLookup());
//        }
//        else {
//            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
//        }
//##COMENTED OUT

        HashMap<String,Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List returnKeys = getReturnKeys();
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls

//COMMENTING THIS OUT FOR NOW
//        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
//            BusinessObject element = (BusinessObject) iter.next();
//        	if(element instanceof PersistableBusinessObject){
//                lookupForm.setLookupObjectId(((PersistableBusinessObject)element).getObjectId());
//            }
        DocumentRouteHeaderEBO element = new DocSearchCriteriaDTO();
        //TODO: additional BORestrictions through generator or component to lock down per document?
    	BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);
            		
//          String actionUrls = getActionUrls(element, pkNames, businessObjectRestrictions);
//ADDED (4 lines)
        for (Iterator iter = result.iterator(); iter.hasNext();) {

        	

            

        	DocumentSearchResult docSearchResult = (DocumentSearchResult)iter.next();
//TODO: where to get these from?
//        	HtmlData returnUrl = new AnchorHtmlData();
        	String actionUrls = "";

//ADDED (3)
            List<? extends Column> origColumns = components.getColumns();//getColumns();
            List<Column> newColumns = new ArrayList<Column>();
            List<KeyValueSort> keyValues = docSearchResult.getResultContainers();
            for (int i = 0; i < origColumns.size(); i++) {

//            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

//                Column col = (Column) iterator.next();
//ADDED 3
            	  Column col = (Column) origColumns.get(i);
            	  KeyValueSort keyValue = null;
            	  for (KeyValueSort keyValueFromList : keyValues) {
            		  if(StringUtils.equals(col.getPropertyName(), keyValueFromList.getkey())) {
            			  keyValue = keyValueFromList;
            			  break;
            		  }
            	  }
            	  if(keyValue==null) {
            		  //means we didn't find an indexed value for this, this seems bad but happens a lot we should research why 
            		  keyValue = new KeyValueSort();
//            		  System.out.println("column: "+col.getPropertyName()+"has an empty KeyValue, this should never happen");
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

				if(StringUtils.equals(propertyName, KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID)) {
					((DocSearchCriteriaDTO)element).setRouteHeaderId(col.getPropertyValue());
				}
				
            	Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KNSConstants.EMPTY_STRING;
//                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
//ADDED
                Object prop=keyValue.getSortValue();

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(propertyName);
                if ( propClass == null ) {
                    try {
                    	//ADDED 3
                    	if(prop!=null) {
                    		propertyTypes.put(propertyName, prop.getClass());
                    	}

//                    	propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
//                    	propertyTypes.put( col.getPropertyName(), propClass );
                    } catch (Exception e) {
//                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                    }
                }

                
                //TODO: check exisiting formatter here, ideally we should be getting this formatter from col.getFormatter in most cases
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

                    //#ADDED 3
                    if (prop instanceof Timestamp) {
                    	formatter = new TimestampAMPMFormatter();
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
                	AnchorHtmlData anchor = new AnchorHtmlData(KNSConstants.EMPTY_STRING, KNSConstants.EMPTY_STRING);
                	//TODO: change to grab URL from config variable
                	if(StringUtils.isNotEmpty(keyValue.getValue()) && StringUtils.equals("routeHeaderId", keyValue.getkey())) {
                	    String target = StringUtils.substringBetween(keyValue.getValue(), "target=\"", "\"");
                	    if (target == null) {
                	        target = "_self";
                	    }
                	    anchor.setTarget(target.trim());
                		if(!DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING.equals(criteria.getSuperUserSearch())) {
                			anchor.setHref(".."+KEWConstants.WEBAPP_DIRECTORY+"/"+StringUtils.substringBetween(keyValue.getValue(), "<a href=\"", "docId=")+"docId="+keyValue.getUserDisplayValue());
                		} else {
                			anchor.setHref(".."+KEWConstants.WEBAPP_DIRECTORY+"/"+StringUtils.substringBetween(keyValue.getValue(), "<a href=\"", "routeHeaderId=")+"routeHeaderId="+keyValue.getUserDisplayValue());
                		}
                        col.setMaxLength(100); //for now force this
                	} else if(StringUtils.isNotEmpty(keyValue.getvalue()) && StringUtils.equals(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG, keyValue.getkey())) {
                		anchor.setHref(".."+KEWConstants.WEBAPP_DIRECTORY+"/"+StringUtils.substringBetween(keyValue.getValue(), "<a href=\"", "\"><img "));
                		String target = StringUtils.substringBetween(keyValue.getValue(), "target=\"", "\"");
                        if (target == null) {
                            target = "_self";
                        }
                        anchor.setTarget(target.trim());
                		col.setMaxLength(100); //for now force this
                        keyValue.setvalue(keyValue.getUserDisplayValue());
                        col.setEscapeXMLValue(false);
                	} else if (StringUtils.isNotEmpty(keyValue.getvalue()) && StringUtils.equals(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_INITIATOR, keyValue.getkey())) {
                		anchor.setHref("../kr/"+StringUtils.substringBetween(keyValue.getValue(), "<a href=\"", "\" target=\"_blank\""));
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





	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject, java.lang.String)
	 */
	@Override
	public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
		//FIXME: ctk - make sure and check that it's ok to do this here.  I may move this out to the doc search processor
		if(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID.equals(propertyName)) {

			AnchorHtmlData link = new AnchorHtmlData();
			DocumentRouteHeaderValue doc = (DocumentRouteHeaderValue)bo;
			//if !superuser
			Long routeHeaderId = doc.getRouteHeaderId();
			link.setDisplayText(routeHeaderId+"");
			String href = "../"+KEWConstants.APP_CODE + "/" + KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KEWConstants.COMMAND_PARAMETER + "=" + KEWConstants.DOCSEARCH_COMMAND + "&" + KEWConstants.ROUTEHEADER_ID_PARAMETER + "=" + routeHeaderId;
			link.setHref(href);

			return link;
		}

		return super.getInquiryUrl(bo, propertyName);
	}


	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
	 */
	@Override
	protected void setRows() {
		super.setRows();
		this.getRows().clear();
	}


	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
	 */
	protected void setRows(Map fieldValues, String docTypeName) {
		// TODO chris - this method should call the criteria processor adapter which will
		//call the criteria processor (either standard or custom) and massage the data into the proper format
		//this is called by setbo in super(which is called by form) so should be called when the page needs refreshing

		//TODO: move over code that checks for doctype (actually should that be in the refresh, since that's where the doc type will be coming back to?)


		//###START LOOKUP ROW CODE Not sure if we need these but they may be valuable for eventually forcing all standard field customization in the xml
		super.setRows();
		List<Row> lookupRows = new ArrayList<Row>();
		//copy the current rows
		for (Row row : super.getRows()) {
			lookupRows.add(row);
		}
		//clear out
		super.getRows().clear();

        processor = new DocumentLookupCriteriaProcessorKEWAdapter();


		DocumentType docType = null;

		if(StringUtils.isNotEmpty(docTypeName)) {
			docType = getValidDocumentType((String)docTypeName);
		}

		DocumentLookupCriteriaProcessorKEWAdapter documentLookupCriteriaProcessorKEWAdapter = (DocumentLookupCriteriaProcessorKEWAdapter)processor;
		if(processor != null && documentLookupCriteriaProcessorKEWAdapter.getCriteriaProcessor()!=null) {
			if(docType==null) {
				documentLookupCriteriaProcessorKEWAdapter.setCriteriaProcessor(new StandardDocumentSearchCriteriaProcessor());
			} else if(!StringUtils.equals(docTypeName, documentLookupCriteriaProcessorKEWAdapter.getCriteriaProcessor().getDocSearchCriteriaDTO().getDocTypeFullName())){
				documentLookupCriteriaProcessorKEWAdapter.setCriteriaProcessor(docType.getDocumentSearchCriteriaProcessor());
			}
		} else {
			if(docType==null) {
				documentLookupCriteriaProcessorKEWAdapter.setCriteriaProcessor(new StandardDocumentSearchCriteriaProcessor());
			} else {
				documentLookupCriteriaProcessorKEWAdapter.setCriteriaProcessor(docType.getDocumentSearchCriteriaProcessor());
			}
		}
		//TODO: This should probably be moved into spring injection since it's a constant
		documentLookupCriteriaProcessorKEWAdapter.setDataDictionaryService(getDataDictionaryService());

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
		List<Row> rows = processor.getRows(docType,lookupRows, detailed, superSearch);

		super.getRows().addAll(rows);

	}


	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performClear()
	 */
	@Override
	public void performClear(LookupForm lookupForm) {
		Map<String,String[]> fieldsToClear = new HashMap<String,String[]>();
		List<Field> critFields = new ArrayList<Field>();

		for (Row row : this.getRows()) {
			for (Field field : row.getFields()) {
				String[] propertyValue = {};
				if(!Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
					propertyValue = new String[]{field.getPropertyValue()};
				} else {
					propertyValue = field.getPropertyValues();
				}

				fieldsToClear.put(field.getPropertyName(), propertyValue);
				critFields.add(new Field(field.getPropertyName(),field.getFieldLabel()));
			}
		}
		//TODO: also check if standard here (maybe from object if use criteria)
		String docTypeName = fieldsToClear.get("docTypeFullName")[0];
		if(StringUtils.isEmpty(docTypeName)) {
			super.performClear(lookupForm);
		} else {
			DocSearchCriteriaDTO docCriteria = DocumentLookupCriteriaBuilder.populateCriteria(fieldsToClear);
			//TODO: Chris - (2 stage clear) set the isOnlyDocTypeFilled, to true if only doc type coming in (besides hidden) and false otherwise)
			docCriteria = getValidDocumentType(docTypeName).getDocumentSearchGenerator().clearSearch(docCriteria);
			//TODO: Chris - (2 stage clear) reset the isOnlyDocTypeFilled

			FieldUtils.populateFieldsFromBusinessObject(critFields, docCriteria);
			//TODO: we should probably do a set rows before doing the following so that the rows represent the doc type coming back from above (or none)
			for (Row row : this.getRows()) {
				for (Field field : row.getFields()) {
					for (Field critField : critFields) {
						if(StringUtils.equals(critField.getPropertyName(),field.getPropertyName())) {
							if(!Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType())) {
								field.setPropertyValue(critField.getPropertyValue());
							} else {
								//contains multivalue
								field.setPropertyValues(critField.getPropertyValues());
							}
						}
					}
				}
			}
		}


	}
	/**
	 *
	 * retrieve a document type
	 *
	 * @param docTypeName
	 * @return
	 */
    private static DocumentType getValidDocumentType(String docTypeName) {
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(docTypeName);
        if (documentType == null) {
            throw new RuntimeException("Document Type invalid : " + docTypeName);
        }
        return documentType;
    }


	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSupplementalMenuBar()
	 */
	@Override
	public String getSupplementalMenuBar() {
		String detailed="NO";
		if(this.getParameters().containsKey(("isAdvancedSearch"))) {
			detailed = ((String[])this.getParameters().get("isAdvancedSearch"))[0];
		}

		String superSearch="NO";
		if(this.getParameters().containsKey("superUserSearch")) {
			superSearch = ((String[])this.getParameters().get("superUserSearch"))[0];
		}

		StringBuilder suppMenuBar = new StringBuilder();
		if(DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING.equalsIgnoreCase(detailed)) {
			suppMenuBar.append("<a href=\"").append(getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_URL_KEY)).append("/kr/").append(KNSConstants.LOOKUP_ACTION).append(
					"?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&isAdvancedSearch=NO").append("&superUserSearch=").append(superSearch).append("\">").append(
							"<img src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append("/images/tinybutton-basicsearch.gif\" class=\"tinybutton\" alt=\"basic search\" title=\"basic search\" border=\"0\" />").append("</a>");
		} else {
			suppMenuBar.append("<a href=\"").append(getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_URL_KEY)).append("/kr/").append(KNSConstants.LOOKUP_ACTION).append(
					"?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&isAdvancedSearch=YES").append("&superUserSearch=").append(superSearch).append("\">").append(
							"<img src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append("/images/tinybutton-detailedsearch.gif\" class=\"tinybutton\" alt=\"detailed search\" title=\"detailed search\" border=\"0\" />").append("</a>");
		}


		if(DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING.equalsIgnoreCase(superSearch)) {
			suppMenuBar.append("&nbsp;").append("<a href=\"").append(getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_URL_KEY)).append("/kr/").append(KNSConstants.LOOKUP_ACTION).append(
					"?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&superUserSearch=NO").append("&isAdvancedSearch=").append(detailed).append("\">").append(
							"<img src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append("/images/tinybutton-nonsupusearch.gif\" class=\"tinybutton\" alt=\"non-superuser search\" title=\"non-superuser search\" border=\"0\" />").append("</a>");
		} else {
			suppMenuBar.append("&nbsp;").append("<a href=\"").append(getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_URL_KEY)).append("/kr/").append(KNSConstants.LOOKUP_ACTION).append(
					"?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&superUserSearch=YES").append("&isAdvancedSearch=").append(DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING).append("\">").append(
							"<img src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append("/images/tinybutton-superusersearch.gif\" class=\"tinybutton\" alt=\"superuser search\" title=\"superuser search\" border=\"0\" />").append("</a>");
		}

		suppMenuBar.append("&nbsp;").append("<a href=\"").append(getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_URL_KEY)).append("/kr/").append(KNSConstants.LOOKUP_ACTION).append(
				"?methodToCall=customLookupableMethodCall&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&superUserSearch=").append(superSearch).append("&isAdvancedSearch=").append(detailed).append("&resetSavedSearch=true").append("\">").append(
						"<img src=\"..").append(KEWConstants.WEBAPP_DIRECTORY).append("/images/tinybutton-clearsavedsearch.gif\" class=\"tinybutton\" alt=\"clear saved searches\" title=\"clear saved searches\" border=\"0\" />").append("</a>");

        Properties parameters = new Properties();
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, this.getBusinessObjectClass().getName());
        this.getParameters().keySet();
        for (Object parameter : this.getParameters().keySet()) {
			parameters.put(parameter, this.getParameters().get(parameter));
		}

		UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, parameters);
		return suppMenuBar.toString();
	}

//    /**
//     * This method is called by performLookup method to generate supplemental action urls.
//     * It calls the method getCustomActionUrls to get html data, calls getMaintenanceUrl to get the actual html tag,
//     * and returns a formatted/concatenated string of action urls.
//     *
//     * @see org.kuali.core.lookup.LookupableHelperService#getActionUrls(org.kuali.core.bo.BusinessObject)
//     */
//    public String getSupplementalActionUrls(List<HtmlData> htmlDataList) {
//        StringBuffer actions = new StringBuffer();
//
//        for(HtmlData htmlData: htmlDataList){
//        	actions.append(getMaintenanceUrl(businessObject, htmlData, pkNames, businessObjectRestrictions));
//            if(htmlData.getChildUrlDataList()!=null){
//            	if(htmlData.getChildUrlDataList().size()>0){
//                    actions.append(ACTION_URLS_CHILDREN_STARTER);
//            		for(HtmlData childURLData: htmlData.getChildUrlDataList()){
//	                	actions.append(getMaintenanceUrl(businessObject, childURLData, pkNames, businessObjectRestrictions));
//	                    actions.append(ACTION_URLS_CHILDREN_SEPARATOR);
//	            	}
//            		if(actions.toString().endsWith(ACTION_URLS_CHILDREN_SEPARATOR))
//            			actions.delete(actions.length()-ACTION_URLS_CHILDREN_SEPARATOR.length(), actions.length());
//                    actions.append(ACTION_URLS_CHILDREN_END);
//            	}
//            }
//        	actions.append(ACTION_URLS_SEPARATOR);
//        }
//        if(actions.toString().endsWith(ACTION_URLS_SEPARATOR))
//        	actions.delete(actions.length()-ACTION_URLS_SEPARATOR.length(), actions.length());
//        return actions.toString();
//    }

	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#shouldDisplayHeaderNonMaintActions()
	 */
	@Override
	public boolean shouldDisplayHeaderNonMaintActions() {
		return this.processor.shouldDisplayHeaderNonMaintActions();
	}


	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#shouldDisplayLookupCriteria()
	 */
	@Override
	public boolean shouldDisplayLookupCriteria() {
		return this.processor.shouldDisplayLookupCriteria();
	}


	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#checkForAdditionalFields(java.util.Map)
	 */
	@Override
	public boolean checkForAdditionalFields(Map fieldValues) {
		// TODO chris - THIS METHOD NEEDS JAVADOCS
//		return super.checkForAdditionalFields(fieldValues);
		String docTypeName = null;
		if(this.getParameters().get("docTypeFullName")!=null) {
			docTypeName = ((String[])this.getParameters().get("docTypeFullName"))[0];
		}
		else if(fieldValues.get("docTypeFullName")!=null) {
			docTypeName = (String)fieldValues.get("docTypeFullName");
		}
		setRows(fieldValues,docTypeName);
		return true;
	}

	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
	 */
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
				//TODO: can we add something to this to get it to highlight the right field too?  Maybe in arg1
				GlobalVariables.getErrorMap().putError(workflowServiceError.getMessage(), RiceKeyConstants.ERROR_CUSTOM, workflowServiceError.getMessage());
			};
		}
        if(!GlobalVariables.getErrorMap().hasNoErrors()) {
        	throw new ValidationException("errors in search criteria");
        }

    }


	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performCustomAction(boolean)
	 */
	@Override
	public boolean performCustomAction(boolean ignoreErrors) {
		DocumentSearchService docSearchService = KEWServiceLocator.getDocumentSearchService();

		Map<String,String> fieldValues = new HashMap<String,String>();
		Map<String,String[]> multFieldValues = new HashMap<String,String[]>();


		String[] resetSavedSearch = ((String[])getParameters().get("resetSavedSearch"));
		if(resetSavedSearch!=null) {
			docSearchService.clearNamedSearches(GlobalVariables.getUserSession().getPrincipalId());
			setRows(fieldValues,"");
			return false;
		}

		String savedSearchName = ((String[])getParameters().get("savedSearchName"))[0];
		if(StringUtils.isEmpty(savedSearchName)||"*ignore*".equals(savedSearchName)) {
			if(!ignoreErrors) {
				GlobalVariables.getErrorMap().putError("savedSearchName", RiceKeyConstants.ERROR_CUSTOM, "You must select a saved search");
			} else {
				//if we're ignoring errors and we got an error just return, no reason to continue.  Also set false to indicate not to perform lookup
				return false;
			}
			//TODO: is there a better way to override a single field value?
			List<Row> rows = this.getRows();
			for (Row row : rows) {
				List<Field> fields = row.getFields();
				for (Field field : fields) {
					if(StringUtils.equals(field.getPropertyName(), "savedSearchName")) {
						field.setPropertyValue("");
						break;
					}
				}
			}
		}
        if (!GlobalVariables.getErrorMap().hasNoErrors()) {
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

		//set field values (besides search atts) here
//		fieldValues.put("docTypeFullName", docTypeName);
//		fieldValues.put("fromDateCreated", criteria.getFromDateCreated());
		fieldValues.put("savedSearchName", "");


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
        for (Iterator iter = getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
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
        //indicate to subsequent actions (search in this case) that a saved search was just populated
        savedSearch=true;

        return true;
	}

	/**
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getExtraField()
	 */
	@Override
	public Field getExtraField() {
		SavedSearchValuesFinder savedSearchValuesFinder = new SavedSearchValuesFinder();
		List<KeyLabelPair> savedSearchValues = savedSearchValuesFinder.getKeyValues();

		Field savedSearch = new Field();
		savedSearch.setPropertyName("savedSearchName");
		savedSearch.setFieldType(Field.DROPDOWN_SCRIPT);
		savedSearch.setScript("customLookupChanged()");
		savedSearch.setFieldValidValues(savedSearchValues);
		savedSearch.setFieldLabel("Saved Searches");
		return savedSearch;

	}
}
