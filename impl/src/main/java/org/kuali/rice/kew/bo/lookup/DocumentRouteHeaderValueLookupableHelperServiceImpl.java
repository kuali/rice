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
import org.kuali.rice.core.DateTimeService;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.CollectionFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
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
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - chris don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentRouteHeaderValueLookupableHelperServiceImpl extends
KualiLookupableHelperServiceImpl {

	private static final long serialVersionUID = -5162419674659967408L;
	DateTimeService dateTimeService;
	DocumentLookupCriteriaProcessor processor;


	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kew.bo.lookup.DocumentRouteHeaderValueLookupableHelperService#setDateTimeService(org.kuali.rice.core.DateTimeService)
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
    	DocSearchCriteriaDTO criteria = constructCriteria(fieldsForLookup);

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
        for (DocumentSearchResult aResult : result)
        {
            //TODO: where to get these from?
            HtmlData returnUrl = new AnchorHtmlData();
            String actionUrls = "";

//TODO: convert columns either here or in the getColumns method
//ADDED (3)
            List<? extends Column> columns = components.getColumns();//getColumns();
            List<KeyValueSort> keyValues = aResult.getResultContainers();
            for (int i = 0; i < columns.size(); i++)
            {

//            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

//                Column col = (Column) iterator.next();
//ADDED 3
                Column col = (Column) columns.get(i);
                KeyValueSort keyValue = keyValues.get(i);
//Set values from keyvalue on column
                col.setPropertyValue(keyValue.getUserDisplayValue());

                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KNSConstants.EMPTY_STRING;
//                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
//ADDED
                Object prop = col.getPropertyValue();

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(col.getPropertyName());
                if (propClass == null)
                {
                    try
                    {
//                    	propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
//                    	propertyTypes.put( col.getPropertyName(), propClass );
                    } catch (Exception e)
                    {
//                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                    }
                }

                // formatters
                if (prop != null)
                {
                    // for Booleans, always use BooleanFormatter
                    if (prop instanceof Boolean)
                    {
                        formatter = new BooleanFormatter();
                    }

                    // for Dates, always use DateFormatter
                    if (prop instanceof Date)
                    {
                        formatter = new DateFormatter();
                    }

                    // for collection, use the list formatter if a formatter hasn't been defined yet
                    if (prop instanceof Collection && formatter == null)
                    {
                        formatter = new CollectionFormatter();
                    }

                    if (formatter != null)
                    {
                        propValue = (String) formatter.format(prop);
                    } else
                    {
                        propValue = prop.toString();
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

//                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue))
                {
//                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
//ADDED (3 lines)
                    AnchorHtmlData anchor = new AnchorHtmlData(KNSConstants.EMPTY_STRING, KNSConstants.EMPTY_STRING);
                    //TODO: change to grab URL from config variable
                    if (StringUtils.isNotEmpty(keyValue.getValue()) && StringUtils.equals("routeHeaderId", keyValue.getKey()))
                    {
                        anchor.setHref(StringUtils.substringBetween(keyValue.getValue(), "<a href=\"", "docId=") + "docId=" + keyValue.getUserDisplayValue());
                        col.setMaxLength(100); //for now force this
                    }

                    col.setColumnAnchor(anchor);
                }
            }

            ResultRow row = new ResultRow((List<Column>) columns, returnUrl.constructCompleteHtmlTag(), actionUrls);
            row.setRowId(returnUrl.getName());
            // because of concerns of the BO being cached in session on the ResultRow,
            // let's only attach it when needed (currently in the case of export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass()))
            {
//            	row.setBusinessObject(element);
            }

//            boolean rowReturnable = isResultReturnable(element);
//ADDED
            boolean rowReturnable = true;
            row.setRowReturnable(rowReturnable);
            if (rowReturnable)
            {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
		//####END COPIED CODE#########
	}


	/**
	 * This method ...
	 *
	 * @param lookupForm
	 * @return
	 */
	private DocSearchCriteriaDTO constructCriteria(Map<String,String> fieldsForLookup) {
		//TODO: move this into adapter
    	DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
    	Map<String,String> fieldsToSet = new HashMap<String,String>();
		for (String formKey : fieldsForLookup.keySet()) {
			if(!(formKey.equalsIgnoreCase(KNSConstants.BACK_LOCATION) ||
			   formKey.equalsIgnoreCase(KNSConstants.DOC_FORM_KEY)) && StringUtils.isNotEmpty(fieldsForLookup.get(formKey))) {
				fieldsToSet.put(formKey, fieldsForLookup.get(formKey));
			}
		}
		//if we use DocSearchCriteriaDTO as object we shouldn't need this conversion stuff
    	for (String fieldToSet : fieldsToSet.keySet()) {
			//need translation code here for certain fields
    		String valueToSet = fieldsToSet.get(fieldToSet);
			try {
				//TODO: temporary work around until is a criteria
				if(fieldToSet.equals("documentType.name")) {
					fieldToSet = "docTypeFullName";
				}
				PropertyUtils.setNestedProperty(criteria, fieldToSet, valueToSet);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return criteria;
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
			
			String href = ConfigContext.getCurrentContextConfig().getKRBaseURL()+"/"+
			KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KEWConstants.COMMAND_PARAMETER + "=" + 
			KEWConstants.DOCSEARCH_COMMAND + "&" + KEWConstants.ROUTEHEADER_ID_PARAMETER + "=" + routeHeaderId;
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
		//###END LOOKUP ROW CODE TODO: do something with lookupRows or delete above code


        processor = new DocumentLookupCriteriaProcessorKEWAdapter();



		//TODO: get this from parameters (or bo?)
		DocumentType docType = null;
		if(processor != null) {
			//TODO: same Doc type
//			if(docType==oldDocType) {
			((DocumentLookupCriteriaProcessorKEWAdapter)processor).setCriteriaProcessor(new StandardDocumentSearchCriteriaProcessor());
//			} else {

//			}
		} else {
			//TODO: same Doc type
			if(docType==null) {
				((DocumentLookupCriteriaProcessorKEWAdapter)processor).setCriteriaProcessor(new StandardDocumentSearchCriteriaProcessor());
			}// else {

//			}
		}

		//call get rows
		List<Row> rows = processor.getRows(docType,lookupRows,false,false);
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
		if(this.processor==null) {
			super.performClear(lookupForm);
		} else {
			DocSearchCriteriaDTO docCriteria = constructCriteria(fieldsToClear);
			if(StringUtils.isNotEmpty("documentType.name")) {
				//TODO: move these strings to constants (probably after moving this to criteria
				getValidDocumentType(fieldsToClear.get("documentType.name")).getDocumentSearchGenerator().clearSearch(docCriteria);;
			}
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
}
