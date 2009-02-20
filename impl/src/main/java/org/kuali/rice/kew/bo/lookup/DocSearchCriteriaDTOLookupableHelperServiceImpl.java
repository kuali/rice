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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaBuilder;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaProcessor;
import org.kuali.rice.kew.docsearch.DocumentLookupCriteriaProcessorKEWAdapter;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchCriteriaProcessor;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.DateTimeService;
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
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - chris don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocSearchCriteriaDTOLookupableHelperServiceImpl extends
KualiLookupableHelperServiceImpl {

	private static final long serialVersionUID = -5162419674659967408L;
	DateTimeService dateTimeService;
	DocumentLookupCriteriaProcessor processor;
	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.bo.lookup.DocumentRouteHeaderValueLookupableHelperService#setDateTimeService(org.kuali.rice.kns.service.DateTimeService)
	 */
	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
	 */
	@Override
	public List<HtmlData> getCustomActionUrls(BusinessObject businessObject,
			List pkNames) {
		// TODO chris - THIS METHOD NEEDS JAVADOCS
		return super.getCustomActionUrls(businessObject, pkNames);
	}



	



	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)
	 */
	@Override
	public Collection performLookup(LookupForm lookupForm,
			Collection resultTable, boolean bounded) {
		
		//TODO: KNS updates to make this not require code from the parent
    	

    	Map<String,String> fieldsForLookup = lookupForm.getFieldsForLookup();
    	DocSearchCriteriaDTO criteria = DocumentLookupCriteriaBuilder.populateCriteria(fieldsForLookup);
    	
    	
    	//TODO: move this into actual adapter as well
    	Collection displayList=null;
    	DocumentSearchResultComponents components = KEWServiceLocator.getDocumentSearchService().getList(GlobalVariables.getUserSession().getPrincipalId(), criteria);
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
//        	BusinessObject element = null;
//        	BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);
        	
//            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
//          String actionUrls = getActionUrls(element, pkNames, businessObjectRestrictions);
//ADDED (4 lines)
        for (Iterator iter = result.iterator(); iter.hasNext();) {
        	DocumentSearchResult docSearchResult = (DocumentSearchResult)iter.next();
//TODO: where to get these from?
        	HtmlData returnUrl = new AnchorHtmlData();
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
            	  KeyValueSort keyValue = keyValues.get(i);
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
            	  
            	Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KNSConstants.EMPTY_STRING;
//                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
//ADDED
                Object prop=col.getPropertyValue();
                
                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(propertyName);
                if ( propClass == null ) {
                    try {
//                    	propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
//                    	propertyTypes.put( col.getPropertyName(), propClass );
                    } catch (Exception e) {
//                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
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
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));
                
//                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
//                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
//ADDED (3 lines)
                	AnchorHtmlData anchor = new AnchorHtmlData(KNSConstants.EMPTY_STRING, KNSConstants.EMPTY_STRING);
                	//TODO: change to grab URL from config variable
                	if(StringUtils.isNotEmpty(keyValue.getValue()) && StringUtils.equals("routeHeaderId", keyValue.getkey())) {
                		anchor.setHref("../en/"+StringUtils.getNestedString(keyValue.getValue(), "<a href=\"", "docId=")+"docId="+keyValue.getUserDisplayValue());
                        col.setMaxLength(100); //for now force this
                	} else if(StringUtils.isNotEmpty(keyValue.getvalue()) && StringUtils.equals(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_LOG, keyValue.getkey())) {
                		anchor.setHref("../en/"+StringUtils.getNestedString(keyValue.getValue(), "<a href=\"", "\"><img "));
                        col.setMaxLength(100); //for now force this
                        keyValue.setvalue(keyValue.getUserDisplayValue());
                        col.setEscapeXMLValue(false);
                	}
                	
                	col.setColumnAnchor(anchor);
                	
                }
                Column newCol = (Column)ObjectUtils.deepCopy(col);
                newColumns.add(newCol);
            }    

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
	 * This overridden method ...
	 * 
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
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
	 */
	@Override
	protected void setRows() {
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
		//TODO: fix this, lookup is not returning a string
		String docTypeName = null;
		if(this.getParameters().get("docTypeFullName")!=null) {
			docTypeName = ((String[])this.getParameters().get("docTypeFullName"))[0];
		}
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
		
		//call get rows
		List<Row> rows = processor.getRows(docType,lookupRows);
		super.getRows().addAll(rows);

		//are we in basic or detailed, are we in super.
		//TODO: ctk Add this code back in when KNS impacting changes are worked back
	}


	/**
	 * This overridden method allows for overriding what the clear logic does.
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#performClear()
	 */
	@Override
	public void performClear(LookupForm lookupForm) {
		Map<String,String> fieldsToClear = new HashMap<String,String>();
		
		for (Row row : this.getRows()) {
			for (Field field : row.getFields()) {
				fieldsToClear.put(field.getPropertyName(), field.getPropertyValue());	
			}
		}
		//TODO: also check if standard here (maybe from object if use criteria)
		String docTypeName = fieldsToClear.get("documentTypeFullName");
		if(StringUtils.isEmpty(docTypeName)) {
			super.performClear(lookupForm);
		} else {
			DocSearchCriteriaDTO docCriteria = DocumentLookupCriteriaBuilder.populateCriteria(fieldsToClear);
			getValidDocumentType(docTypeName).getDocumentSearchGenerator().clearSearch(docCriteria);
			//TODO: method to update values on rows from criteria object (i.e. reverse of lookupform.populate)
		}
	}
	/**
	 * 
	 * This method is taken from DocSearch to retrieve a document type
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
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSupplementalMenuBar()
	 */
	@Override
	public String getSupplementalMenuBar() {
		Boolean detailed = false;
		if(this.getParameters().get("isAdvancedSearch")!=null) {
			detailed = new Boolean(((String[])this.getParameters().get("isAdvancedSearch"))[0]);
		}
		String suppMenuBar = "";
		//TODO: replace server info with parameter
		if(detailed) {
			suppMenuBar = "<a href=\"http://localhost:8080/kr-dev/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&isAdvancedSearch=false\">basic</a>";
		} else {
			suppMenuBar = "<a href=\"http://localhost:8080/kr-dev/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&isAdvancedSearch=true\">detailed</a>";	
		}
		Boolean superSearch = false;
		if(this.getParameters().get("isSuperSearch")!=null) {
			superSearch = new Boolean(((String[])this.getParameters().get("isSuperSearch"))[0]); 
		}
		if(superSearch) {
			suppMenuBar = suppMenuBar + "&nbsp" + "<a href=\"http://localhost:8080/kr-dev/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&isSuperSearch=false\">super</a>";
		} else {
			suppMenuBar = suppMenuBar + "&nbsp" + "<a href=\"http://localhost:8080/kr-dev/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO&docFormKey=88888888&returnLocation=http://localhost:8080/kr-dev/portal.do&hideReturnLink=true&isSuperSearch=true\">non-super</a>";
		}
		return suppMenuBar;
	}


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#shouldDisplayHeaderNonMaintActions()
	 */
	@Override
	public boolean shouldDisplayHeaderNonMaintActions() {
		return this.processor.shouldDisplayHeaderNonMaintActions();
	}


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#shouldDisplayLookupCriteria()
	 */
	@Override
	public boolean shouldDisplayLookupCriteria() {
		return this.processor.shouldDisplayLookupCriteria();
	}


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#checkForAdditionalFields(java.util.Map)
	 */
	@Override
	public boolean checkForAdditionalFields(Map fieldValues) {
		// TODO chris - THIS METHOD NEEDS JAVADOCS
		return super.checkForAdditionalFields(fieldValues);
	}
    
    
}
