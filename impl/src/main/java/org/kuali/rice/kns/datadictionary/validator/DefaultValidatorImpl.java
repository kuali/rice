/**
 * Copyright 2010 The Kuali Foundation Licensed under the Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.osedu.org/licenses/ECL-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.kuali.rice.kns.datadictionary.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.type.TypeUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.dto.CaseConstraint;
import org.kuali.rice.kns.dto.Constraint;
import org.kuali.rice.kns.dto.DataType;
import org.kuali.rice.kns.dto.MustOccurConstraint;
import org.kuali.rice.kns.dto.RequiredConstraint;
import org.kuali.rice.kns.dto.ValidCharsConstraint;
import org.kuali.rice.kns.dto.WhenConstraint;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;


public class DefaultValidatorImpl extends BaseAbstractValidator {
    final static Logger LOG = Logger.getLogger(DefaultValidatorImpl.class);

//    private MessageService messageService = null;

//    private SearchDispatcher searchDispatcher;
//
//    private String messageLocaleKey = "en";
//
//    private String messageGroupKey = "validation";
//
    private DateParser dateParser = new ServerDateParser();
    
    private DataDictionaryService dataDictionaryService;
    
    private PersistenceStructureService persistenceStructureService;
    
//
//    private boolean serverSide = true;

//    public MessageService getMessageService() {
//        return messageService;
//    }
//
//    public void setMessageService(MessageService messageService) {
//        this.messageService = messageService;
//    }
//
//    public String getMessageLocaleKey() {
//        return messageLocaleKey;
//    }
//
//    public void setMessageLocaleKey(String messageLocaleKey) {
//        this.messageLocaleKey = messageLocaleKey;
//    }
//
//    public String getMessageGroupKey() {
//        return messageGroupKey;
//    }
//
//    public void setMessageGroupKey(String messageGroupKey) {
//        this.messageGroupKey = messageGroupKey;
//    }
//
//    public void setDateParser(DateParser dateParser) {
//        this.dateParser = dateParser;
//    }
//
//	/**
//     * @return the serverSide
//     */
//    public boolean isServerSide() {
//        return serverSide;
//    }
//
//    /**
//     * @param serverSide
//     *            the serverSide to set
//     */
//    public void setServerSide(boolean serverSide) {
//        this.serverSide = serverSide;
//    }
//
//    /**
//     * @return the dateParser
//     */
//    public DateParser getDateParser() {
//        return dateParser;
//    }

    /**
     * Validate Object and all its nested child objects for given type and state
     *
     * @param data
     * @param objStructure
     * @return
     */
    public List<ValidationResultInfo> validateObject(Object data, BusinessObjectEntry objStructure) {

        Stack<String> elementStack = new Stack<String>();
        return validateObject(data, objStructure, elementStack, true);
    }
    
    public class SectionDefinitionDataProvider implements ConstraintDataProvider {
    	
    	Map<String, Object> dataMap = null;
    	
		@Override
		public String getObjectId() {
			return (dataMap.containsKey("id") && null != dataMap.get("id")) ? dataMap.get("id").toString() : null;
		}

		@Override
		public Object getValue(String fieldKey) {
			return dataMap.get(fieldKey);
		}

		@Override
		public Boolean hasField(String fieldKey) {
			return Boolean.valueOf(dataMap.containsKey(fieldKey));
		}

		@Override
		public void initialize(Object o) {
			dataMap = new HashMap<String, Object>();
			
			MaintenanceDocumentWrapper wrapper = (MaintenanceDocumentWrapper)o;
			BusinessObject businessObject = wrapper.getBusinessObject();
			String docTypeName = wrapper.getDocTypeName();
			MaintenanceDocumentEntry entry = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(docTypeName);
			for (MaintainableSectionDefinition sectionDefinition : entry.getMaintainableSections()) {
				List<? extends MaintainableItemDefinition> itemDefinitions = sectionDefinition.getMaintainableItems();
				
				for (MaintainableItemDefinition itemDefinition : itemDefinitions) {
					if (itemDefinition instanceof MaintainableFieldDefinition) {
						Boolean isAttributeDefined = getDataDictionaryService().isAttributeDefined(businessObject.getClass(), itemDefinition.getName());
				        if (isAttributeDefined != null && isAttributeDefined.booleanValue()) {
				            Object value = ObjectUtils.getPropertyValue(businessObject, itemDefinition.getName());
				            if (value != null && StringUtils.isNotBlank(value.toString())) {
					            Class<?> propertyType = ObjectUtils.getPropertyType(businessObject, itemDefinition.getName(), persistenceStructureService);
					            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {
					                // check value format against dictionary
				                    if (!TypeUtils.isTemporalClass(propertyType)) {
				                        //validateAttributeFormat(businessObject.getClass().getName(), itemDefinition.getName(), value.toString(), errorPrefix + itemDefinition.getName());
				                    
				                    	dataMap.put(itemDefinition.getName(), value.toString());
				                    }
					            }
				            }
				        }
					}
				}
			}
		}

		@Override
		public String getPath() {
			return "";
		}
    }
    
    public class MaintenanceDocumentWrapper {
    	private BusinessObject businessObject;
    	private String docTypeName;
    	
    	public MaintenanceDocumentWrapper(BusinessObject businessObject, String docTypeName) {
    		this.businessObject = businessObject;
    		this.docTypeName = docTypeName;
    	}

		/**
		 * @return the businessObject
		 */
		public BusinessObject getBusinessObject() {
			return this.businessObject;
		}

		/**
		 * @param businessObject the businessObject to set
		 */
		public void setBusinessObject(BusinessObject businessObject) {
			this.businessObject = businessObject;
		}

		/**
		 * @return the docTypeName
		 */
		public String getDocTypeName() {
			return this.docTypeName;
		}

		/**
		 * @param docTypeName the docTypeName to set
		 */
		public void setDocTypeName(String docTypeName) {
			this.docTypeName = docTypeName;
		}
    	
    }
    
    @Override
    public List<ValidationResultInfo> validateBusinessObjectOnMaintenanceDocument(BusinessObject businessObject, String docTypeName) {
        Stack<String> elementStack = new Stack<String>();
        
        BusinessObjectEntry objStructure = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObject.getClass().getName());
        
        ConstraintDataProvider dataProvider = new SectionDefinitionDataProvider();
        dataProvider.initialize(new MaintenanceDocumentWrapper(businessObject, docTypeName));
        
        return validateObject(businessObject, objStructure, dataProvider, elementStack, true);
    }
    
    
    public List<ValidationResultInfo> validateAttributeField(BusinessObject businessObject, String fieldName) {
    	Stack<String> elementStack = new Stack<String>();
    	
    	BusinessObjectEntry objStructure = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObject.getClass().getName());
    	AttributeDefinition field = getDataDictionaryService().getAttributeDefinition(businessObject.getClass().getName(), fieldName);
    	
    	ConstraintDataProvider dataProvider = new BeanConstraintDataProvider();
    	dataProvider.initialize(businessObject);
    	return validateField(field, objStructure, dataProvider, elementStack);
    }
    

    private List<ValidationResultInfo> validateObject(Object data, BusinessObjectEntry objStructure, Stack<String> elementStack, boolean isRoot) {

         ConstraintDataProvider dataProvider = new BeanConstraintDataProvider();
         dataProvider.initialize(data);
         
         return validateObject(data, objStructure, dataProvider, elementStack, isRoot);
    }
    
    private List<ValidationResultInfo> validateObject(Object data, BusinessObjectEntry objStructure, ConstraintDataProvider dataProvider, Stack<String> elementStack, boolean isRoot) {

       List<ValidationResultInfo> results = new ArrayList<ValidationResultInfo>();

        // Push object structure to the top of the stack
        StringBuilder objXPathElement = new StringBuilder(dataProvider.getPath());

        if(!isRoot && !objXPathElement.toString().isEmpty()){
        	elementStack.push(objXPathElement.toString());
        }

        /*
         * Do nothing if the object to be validated is not type/state or if the objectstructure with constraints is not
         * provided
         */
        if (null == objStructure) {
            return results;
        }

        for (AttributeDefinition f : objStructure.getAttributes()) {
        	if (f/*.getConstraint()*/ == null)
        		continue;
        	
            List<ValidationResultInfo> l = validateField(f, objStructure, dataProvider, elementStack);

            if (l != null && l.size() > 0) {
            	String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objStructure.getBusinessObjectClass().getName(), f.getName());
            	GlobalVariables.getMessageMap().putError(f.getName(), RiceKeyConstants.ERROR_MAX_LENGTH, new String[] { errorLabel, "LENGTH" });
            }
            
            results.addAll(l);

            // Use Custom Validators
            String customValidatorClass = f.getCustomValidatorClass(); //f.getConstraint() != null ? f.getConstraint().getCustomValidatorClass() : null;
            if (customValidatorClass != null) {
            	Validator customValidator = validatorFactory.getValidator(customValidatorClass);
            	if(customValidator==null){
            		throw new RuntimeException("Custom Validator "+customValidatorClass+" was not configured in this context");
            	}
            	l = customValidator.validateObject(f.getName(),data,elementStack);
            	results.addAll(l);
            }
        }
        if (!isRoot && !objXPathElement.toString().isEmpty()){
        	elementStack.pop();
        }

        /* All Field validations are returned right now */
        // List<ValidationResultInfo> resultsBuffer = new
        // ArrayList<ValidationResultInfo>();
        // for (ValidationResultContainer vc : results) {
        // if (skipFields.contains(vc.getElement()) == false) {
        // resultsBuffer.add(vc);
        // }
        // }
        // results = resultsBuffer;
        return results;
    }
    

    public List<ValidationResultInfo> validateField(AttributeDefinition field, BusinessObjectEntry objStruct, ConstraintDataProvider dataProvider, Stack<String> elementStack) {
        Object value = dataProvider.getValue(field.getName());
    	List<ValidationResultInfo> results = new ArrayList<ValidationResultInfo>();

    	// Handle null values in field
    	if (value == null || "".equals(value.toString().trim())) {
    		processConstraint(results, field, objStruct, value, dataProvider, elementStack);
    		return results;
    	}

        /*
         * For complex object structures only the following constraints apply 1. TypeStateCase 2. MinOccurs 3. MaxOccurs
         */
        if (DataType.COMPLEX.equals(field.getDataType())) {
            BusinessObjectEntry nestedObjStruct = null;

//            if (null != field.getDataObjectStructure()) {
//                nestedObjStruct = field.getDataObjectStructure();
//            }

            elementStack.push(field.getName());

            if (value instanceof Collection) {

                String xPathForCollection = getElementXpath(elementStack) + "/*";

                int i=0;
                for (Object o : (Collection<?>) value) {
                	elementStack.push(Integer.toString(i));
                    processNestedObjectStructure(results, o, nestedObjStruct, field, elementStack);
                    elementStack.pop();
                    i++;
                }
//                if (field.getConstraint().getMinOccurs() != null && field.getConstraint().getMinOccurs() > ((Collection<?>) value).size()) {
//                    ValidationResultInfo valRes = new ValidationResultInfo(xPathForCollection, value);
//                    valRes.setError(MessageUtils.interpolate(getMessage("validation.minOccurs"), toMap(field.getConstraint())));
//                    results.add(valRes);
//                }
                checkFieldRequired(field, objStruct, ((Collection<?>) value).size());

//                Integer maxOccurs = tryParse(field.getConstraint().getMaxOccurs());
//                if (maxOccurs != null && maxOccurs < ((Collection<?>) value).size()) {
//                    ValidationResultInfo valRes = new ValidationResultInfo(xPathForCollection, value);
//                    valRes.setError(MessageUtils.interpolate(getMessage("validation.maxOccurs"), toMap(field.getConstraint())));
//                    results.add(valRes);
//                }
                checkFieldTooMany(field, objStruct, ((Collection<?>) value).size());
            } else {
                processNestedObjectStructure(results, value, nestedObjStruct, field, elementStack);
            }

            elementStack.pop();

        } else { // If non complex data type

            if (value instanceof Collection) {

                if(((Collection<?>)value).isEmpty()){
                    processConstraint(results, field, objStruct, "", dataProvider, elementStack);
                }

            	int i = 0;
                for (Object o : (Collection<?>) value) {
                	elementStack.push(Integer.toBinaryString(i));
                    processConstraint(results, field, objStruct, o, dataProvider, elementStack);
                    elementStack.pop();
                    i++;
                }

                String xPath = getElementXpath(elementStack) + "/" + field.getName() + "/*";
//                if (field.getConstraint().getMinOccurs() != null && field.getConstraint().getMinOccurs() > ((Collection<?>) value).size()) {
//                    ValidationResultInfo valRes = new ValidationResultInfo(xPath, value);
//                    valRes.setError(MessageUtils.interpolate(getMessage("validation.minOccurs"), toMap(field.getConstraint())));
//                    results.add(valRes);
//                }
                checkFieldRequired(field, objStruct, ((Collection<?>) value).size());

//                Integer maxOccurs = tryParse(field.getConstraint().getMaxOccurs());
//                if (maxOccurs != null && maxOccurs < ((Collection<?>) value).size()) {
//                    ValidationResultInfo valRes = new ValidationResultInfo(xPath, value);
//                    valRes.setError(MessageUtils.interpolate(getMessage("validation.maxOccurs"), toMap(field.getConstraint())));
//                    results.add(valRes);
//                }
                checkFieldTooMany(field, objStruct, ((Collection<?>) value).size());
            } else {
                processConstraint(results, field, objStruct, value, dataProvider, elementStack);
            }

        }
        return results;
    }

    protected Integer tryParse(String s) {
        Integer result = null;
        if (s != null) {
            try {
                result = Integer.valueOf(s);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
        return result;
    }

    protected void processNestedObjectStructure(List<ValidationResultInfo> results, Object value, BusinessObjectEntry nestedObjStruct, AttributeDefinition field, Stack<String> elementStack) {

        results.addAll(validateObject(value, nestedObjStruct, elementStack, false));

    }

    protected void processConstraint(List<ValidationResultInfo> valResults, AttributeDefinition field, BusinessObjectEntry objStructure, Object value, ConstraintDataProvider dataProvider, Stack<String> elementStack) {

        // Process Case Constraint
        // Case Constraint are only evaluated on the field. Nested case constraints are currently ignored
        Constraint caseConstraint = processCaseConstraint(valResults, field, objStructure, value, dataProvider, elementStack);

        //Constraint constraint = (null != caseConstraint) ? caseConstraint : field.getConstraint();

        processBaseConstraints(valResults, field, objStructure, field.getDataType(), field.getName(), value, elementStack);

        // Stop other checks if value is null
        if (value == null || "".equals(value.toString().trim())) {
            return;
        }

        String elementPath = getElementXpath(elementStack) + "/" + field.getName();

        // Process Valid Chars
        if (null != field.getValidChars()) {
            ValidationResultInfo val = processValidCharConstraint(field, objStructure, elementPath, field.getValidChars(), dataProvider, value);
            if (null != val) {
                valResults.add(val);
            }
        }

        // Process Require Constraints (only if this field has value)
        if (value != null && !"".equals(value.toString().trim())) {
            if (null != field.getRequireConstraint() && field.getRequireConstraint().size() > 0) {
                for (RequiredConstraint rc : field.getRequireConstraint()) {
                    ValidationResultInfo val = processRequireConstraint(elementPath, rc, field, objStructure, dataProvider);
                    if (null != val) {
                        valResults.add(val);
                    }
                }
            }
        }

        // Process Occurs Constraint
        if (null != field.getOccursConstraint() && field.getOccursConstraint().size() > 0) {
            for (MustOccurConstraint oc : field.getOccursConstraint()) {
                ValidationResultInfo val = processOccursConstraint(elementPath, oc, field, objStructure, dataProvider);
                if (null != val) {
                    valResults.add(val);
                }
            }
        }

        // Process lookup Constraint
//        if (null != constraint.getLookupDefinition()) {
//            processLookupConstraint(valResults, constraint.getLookupDefinition(), field, elementStack, dataProvider);
//        }
    }

    protected ValidationResultInfo processRequireConstraint(String element, RequiredConstraint constraint, AttributeDefinition field, BusinessObjectEntry objStructure, ConstraintDataProvider dataProvider) {

        ValidationResultInfo val = null;

        String fieldName = constraint.getFieldPath();// TODO parse fieldname from here
        Object fieldValue = dataProvider.getValue(fieldName);

        boolean result = true;

        if (fieldValue instanceof java.lang.String) {
            result = hasText((String) fieldValue);
        } else if (fieldValue instanceof Collection) {
            result = (((Collection<?>) fieldValue).size() > 0);
        } else {
            result = (null != fieldValue) ? true : false;
        }

        if (!result) {
//            Map<String, Object> rMap = new HashMap<String, Object>();
//            rMap.put("field1", field.getName());
//            rMap.put("field2", fieldName);
//            val = new ValidationResultInfo(element, fieldValue);
//            val.setError(MessageUtils.interpolate(getMessage("validation.requiresField"), rMap));
        	
        	AttributeDefinition attributeDefinition = getDataDictionaryService().getAttributeDefinition(objStructure.getBusinessObjectClass().getName(), fieldName);
        	if (attributeDefinition != null)
        		fieldName = attributeDefinition.getLabel();
        	
        	setFieldError(field, objStructure, RiceKeyConstants.ERROR_REQUIRES_FIELD, fieldName);
        }

        return val;
    }

    /**
     * Process caseConstraint tag and sets any of the base constraint items if any of the when condition matches
     *
     * @param constraint
     * @param caseConstraint
     * @param field
     */
    protected Constraint processCaseConstraint(List<ValidationResultInfo> valResults, AttributeDefinition field, BusinessObjectEntry objStructure, Object value, ConstraintDataProvider dataProvider, Stack<String> elementStack) {

        CaseConstraint constraint = field./*getConstraint().*/getCaseConstraint();

        if (null == constraint) {
            return null;
        }

        String operator = (hasText(constraint.getOperator())) ? constraint.getOperator() : "EQUALS";
        AttributeDefinition caseField = (hasText(constraint.getFieldPath())) ? ValidatorUtils.getField(constraint.getFieldPath(), objStructure) : null;

        // TODO: What happens when the field is not in the dataProvider?
        Object fieldValue = (null != caseField) ? dataProvider.getValue(caseField.getName()) : value;
        DataType fieldDataType = (null != caseField ? caseField.getDataType():null);

        // If fieldValue is null then skip Case check
        if(null == fieldValue) {
            return null;
        }

        // Extract value for field Key
        for (WhenConstraint wc : constraint.getWhenConstraint()) {

            List<Object> whenValueList = wc.getValues();

            for (Object whenValue : whenValueList) {
                if (ValidatorUtils.compareValues(fieldValue, whenValue, fieldDataType, operator, constraint.isCaseSensitive(), dateParser) && null != wc.getConstraint()) {
                    return wc.getConstraint();
                }
            }
        }

        return null;
    }

    protected ValidationResultInfo processValidCharConstraint(AttributeDefinition field, BusinessObjectEntry objStructure, String element, ValidCharsConstraint vcConstraint, ConstraintDataProvider dataProvider, Object value) {

        ValidationResultInfo val = null;

        StringBuilder fieldValue = new StringBuilder();
        String validChars = vcConstraint.getValue();

        fieldValue.append(ValidatorUtils.getString(value));

        int typIdx = validChars.indexOf(":");
        String processorType = "regex";
        if (-1 == typIdx) {
            validChars = "[" + validChars + "]*";
        } else {
            processorType = validChars.substring(0, typIdx);
            validChars = validChars.substring(typIdx + 1);
        }

        if ("regex".equalsIgnoreCase(processorType)) {
            if (fieldValue == null || !fieldValue.toString().matches(validChars)) {
//            	val = new ValidationResultInfo(element, fieldValue);
//                if(vcConstraint.getLabelKey()!=null){
//                	val.setError(getMessage(vcConstraint.getLabelKey()));
//                }else{
//                	val.setError(getMessage("validation.validCharsFailed"));
//                }
            	if (vcConstraint.getLabelKey()!=null) {
            		// FIXME: This shouldn't surface label key itself to the user - it should look up the label key, but this needs to be implemented in Rice
            		setFieldError(field, objStructure, RiceKeyConstants.ERROR_CUSTOM, vcConstraint.getLabelKey());
            	} else {
            		setFieldError(field, objStructure, RiceKeyConstants.ERROR_INVALID_FORMAT, fieldValue.toString());
            	}
            }
        }

        return val;
    }

    /**
     * Computes if all the filed required in the occurs clause are between the min and max
     *
     * @param valResults
     * @param constraint
     * @param field
     * @param type
     * @param state
     * @param objStructure
     * @param dataProvider
     * @return
     */
    protected ValidationResultInfo processOccursConstraint(String element, MustOccurConstraint constraint, AttributeDefinition field, BusinessObjectEntry objStructure, ConstraintDataProvider dataProvider) {

        boolean result = false;
        int trueCount = 0;

        ValidationResultInfo val = null;

        for (RequiredConstraint rc : constraint.getRequiredFields()) {
            trueCount += (processRequireConstraint("", rc, field, objStructure, dataProvider) != null) ? 1 : 0;
        }

        for (MustOccurConstraint oc : constraint.getOccurs()) {
            trueCount += (processOccursConstraint("", oc, field, objStructure, dataProvider) != null) ? 1 : 0;
        }

        result = (trueCount >= constraint.getMin() && trueCount <= constraint.getMax()) ? true : false;

        if (!result) {
         // TODO: figure out what data should go here instead of null
//            val = new ValidationResultInfo(element, null);
//            val.setError(getMessage("validation.occurs"));
        	setFieldError(field, objStructure, RiceKeyConstants.ERROR_OCCURS);
        }

        return val;
    }

    // TODO: Implement lookup constraint
//    protected void processLookupConstraint(List<ValidationResultInfo> valResults, LookupConstraint lookupConstraint, AttributeDefinition field, Stack<String> elementStack, ConstraintDataProvider dataProvider) {
//        if (lookupConstraint == null) {
//            return;
//        }
//
//        // Create search params based on the param mapping
//        List<SearchParam> params = new ArrayList<SearchParam>();
//        Object fieldValue = null;
//        for (CommonLookupParam paramMapping : lookupConstraint.getParams()) {
//            SearchParam param = new SearchParam();
//
//            param.setKey(paramMapping.getKey());
//
//            // If the value of the search param comes form another field then get it
//            if (paramMapping.getFieldPath() != null && !paramMapping.getFieldPath().isEmpty()) {
//                fieldValue = dataProvider.getValue(paramMapping.getFieldPath());
//                if (fieldValue instanceof String) {
//                    param.setValue((String) fieldValue);
//                } else if (fieldValue instanceof List<?>) {
//                    param.setValue((List<String>) fieldValue);
//                }
//            } else if (paramMapping.getDefaultValueString() != null) {
//                param.setValue(paramMapping.getDefaultValueString());
//            } else {
//                param.setValue(paramMapping.getDefaultValueList());
//            }
//            params.add(param);
//        }
//
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.setMaxResults(1);
//        searchRequest.setStartAt(0);
//        searchRequest.setNeededTotalResults(false);
//        searchRequest.setSearchKey(lookupConstraint.getSearchTypeId());
//        searchRequest.setParams(params);
//
//        SearchResult searchResult = null;
//        try {
//            searchResult = searchDispatcher.dispatchSearch(searchRequest);
//        } catch (Exception e) {
//            LOG.info("Error calling Search", e);
//        }
//        if (searchResult == null || searchResult.getRows() == null || searchResult.getRows().isEmpty()) {
//            ValidationResultInfo val = new ValidationResultInfo(getElementXpath(elementStack) + "/" + field.getName(), fieldValue);
//            val.setError(getMessage("validation.lookup"));
//            valResults.add(val);
//        }
//    }

    protected void processBaseConstraints(List<ValidationResultInfo> valResults, AttributeDefinition field, BusinessObjectEntry objStructure, DataType dataType, String name, Object value, Stack<String> elementStack) {

        if (value == null || "".equals(value.toString().trim())) {
//            if ((constraint.getMinOccurs() != null && constraint.getMinOccurs() > 0)
//            		|| (field.isRequired() != null && field.isRequired().booleanValue())) {
//                ValidationResultInfo val = new ValidationResultInfo(getElementXpath(elementStack) + "/" + name, value);
//                val.setError(getMessage("validation.required"));
//                valResults.add(val);
//            	setFieldRequiredError(field, objStructure);
//            }
        	checkFieldRequired(field, objStructure);
            return;
        }

        String elementPath = getElementXpath(elementStack) + "/" + name;

        if (DataType.STRING.equals(dataType)) {
            validateString(field, objStructure, value, elementPath, valResults);
        } else if (DataType.INTEGER.equals(dataType)) {
            validateInteger(field, objStructure, value, elementPath, valResults);
        } else if (DataType.LONG.equals(dataType)) {
            validateLong(field, objStructure, value, elementPath, valResults);
        } else if (DataType.DOUBLE.equals(dataType)) {
            validateDouble(field, objStructure, value, elementPath, valResults);
        } else if (DataType.FLOAT.equals(dataType)) {
            validateFloat(field, objStructure, value, elementPath, valResults);
        } else if (DataType.BOOLEAN.equals(dataType)) {
            validateBoolean(field, objStructure, value, elementPath, valResults);
        } else if (DataType.DATE.equals(dataType)) {
            validateDate(field, objStructure, value, elementPath, valResults, dateParser);
        }
    }

    protected void validateBoolean(AttributeDefinition field, BusinessObjectEntry objStructure, Object value, String element, List<ValidationResultInfo> results) {
        if (!(value instanceof Boolean)) {
            try {
                Boolean.valueOf(value.toString());
            } catch (Exception e) {
//                ValidationResultInfo val = new ValidationResultInfo(element, value);
//                val.setError(getMessage("validation.mustBeBoolean"));
//                results.add(val);
            	setFieldError(field, objStructure, RiceKeyConstants.ERROR_BOOLEAN);
            }
        }
    }

    protected void validateDouble(AttributeDefinition field, BusinessObjectEntry objStructure, Object value, String element, List<ValidationResultInfo> results) {
        Double v = null;

        ValidationResultInfo val = new ValidationResultInfo(element, value);

        if (value instanceof Number) {
            v = ((Number) value).doubleValue();
        } else {
            try {
                v = Double.valueOf(value.toString());
            } catch (Exception e) {
//                val.setError(getMessage("validation.mustBeDouble"));
            	setFieldError(field, objStructure, RiceKeyConstants.ERROR_BIG_DECIMAL);
            }
        }

        if (val.isOk()) {
            Double maxValue = ValidatorUtils.getDouble(field.getInclusiveMax());
            Double minValue = ValidatorUtils.getDouble(field.getExclusiveMin());

            if (maxValue != null && minValue != null) {
                // validate range
                if (v > maxValue || v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v > maxValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected void validateFloat(AttributeDefinition field, BusinessObjectEntry objStructure, Object value, String element, List<ValidationResultInfo> results) {
        Float v = null;

        ValidationResultInfo val = new ValidationResultInfo(element, value);
        if (value instanceof Number) {
            v = ((Number) value).floatValue();
        } else {
            try {
                v = Float.valueOf(value.toString());
            } catch (Exception e) {
//                val.setError(getMessage("validation.mustBeFloat"));
                setFieldError(field, objStructure, RiceKeyConstants.ERROR_BIG_DECIMAL);
            }
        }

        if (val.isOk()) {
            Float maxValue = ValidatorUtils.getFloat(field.getInclusiveMax());
            Float minValue = ValidatorUtils.getFloat(field.getExclusiveMin());

            if (maxValue != null && minValue != null) {
                // validate range
                if (v > maxValue || v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v > maxValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected void validateLong(AttributeDefinition field, BusinessObjectEntry objStructure, Object value, String element, List<ValidationResultInfo> results) {
        Long v = null;

        ValidationResultInfo val = new ValidationResultInfo(element, value);
        if (value instanceof Number) {
            v = ((Number) value).longValue();
        } else {
            try {
                v = Long.valueOf(value.toString());
            } catch (Exception e) {
            	setFieldError(field, objStructure, RiceKeyConstants.ERROR_LONG);
            }
        }

        if (val.isOk()) {
            Long maxValue = ValidatorUtils.getLong(field.getInclusiveMax());
            Long minValue = ValidatorUtils.getLong(field.getExclusiveMin());

            if (maxValue != null && minValue != null) {
                // validate range
                if (v > maxValue || v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v > maxValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }

    }

    protected void validateInteger(AttributeDefinition field, BusinessObjectEntry objStructure, Object value, String element, List<ValidationResultInfo> results) {
        Integer v = null;

        ValidationResultInfo val = new ValidationResultInfo(element, value);

        if (value instanceof Number) {
            v = ((Number) value).intValue();
        } else {
            try {
                v = Integer.valueOf(value.toString());
            } catch (Exception e) {
            	setFieldError(field, objStructure, RiceKeyConstants.ERROR_INTEGER);
            }
        }

        if (val.isOk()) {
            Integer maxValue = ValidatorUtils.getInteger(field.getInclusiveMax());
            Integer minValue = ValidatorUtils.getInteger(field.getExclusiveMin());

            if (maxValue != null && minValue != null) {
                // validate range
                if (v > maxValue || v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v > maxValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected void validateDate(AttributeDefinition field, BusinessObjectEntry objStructure, Object value, String element, List<ValidationResultInfo> results, DateParser dateParser) {
        ValidationResultInfo val = new ValidationResultInfo(element, value);

        Date v = null;

        if (value instanceof Date) {
            v = (Date) value;
        } else {
            try {
                v = dateParser.parseDate(value.toString());
            } catch (Exception e) {
            	setFieldError(field, objStructure, RiceKeyConstants.ERROR_DATE);
            }
        }

        if (val.isOk()) {
            Date maxValue = ValidatorUtils.getDate(field.getInclusiveMax(), dateParser);
            Date minValue = ValidatorUtils.getDate(field.getExclusiveMin(), dateParser);

            if (maxValue != null && minValue != null) {
                // validate range
                if (v.getTime() > maxValue.getTime() || v.getTime() < minValue.getTime()) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v.getTime() > maxValue.getTime()) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v.getTime() < minValue.getTime()) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(field, objStructure, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected void validateString(AttributeDefinition field, BusinessObjectEntry objStructure, Object value, String element, List<ValidationResultInfo> results) {

        String s = value == null ? "" : value.toString().trim();

        ValidationResultInfo val = new ValidationResultInfo(element, value);

        Integer maxLength = field.getMaxLength(); //tryParse(field.getMaxLength());
        if (maxLength != null && field.getMinLength() != null && field.getMinLength().intValue() > 0) {
            if (s.length() > maxLength.intValue() || s.length() < field.getMinLength().intValue()) {
//                val.setError(MessageUtils.interpolate(getMessage("validation.lengthOutOfRange"), toMap(constraint)));
            	setFieldError(field, objStructure, RiceKeyConstants.ERROR_LENGTH_OUT_OF_RANGE, String.valueOf(field.getMinLength()), String.valueOf(field.getMaxLength()));
            }
        } else if (maxLength != null) {
            if (s.length() > field.getMaxLength().intValue()) {
//                val.setError(MessageUtils.interpolate(getMessage("validation.maxLengthFailed"), toMap(constraint)));
            	setFieldError(field, objStructure, RiceKeyConstants.ERROR_MAX_LENGTH, String.valueOf(field.getMaxLength()));
            }
        } else if (field.getMinLength() != null && field.getMinLength().intValue() > 0) {
            if (s.length() < field.getMinLength().intValue()) {
//                val.setError(MessageUtils.interpolate(getMessage("validation.minLengthFailed"), toMap(constraint)));
            	setFieldError(field, objStructure, RiceKeyConstants.ERROR_MIN_LENGTH, String.valueOf(field.getMinLength()));
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected String getMessage(String messageId) {
    	
    	
        // FIXME: JLR - rework this for KNS
    	/*if (null == messageService) {
            return messageId;
        }

        Message msg = messageService.getMessage(messageLocaleKey, messageGroupKey, messageId);

        return msg.getValue();*/
    	
    	return messageId;
    }

    protected String getElementXpath(Stack<String> elementStack) {
        StringBuilder xPath = new StringBuilder();
        Iterator<String> itr = elementStack.iterator();
        while (itr.hasNext()) {
            xPath.append(itr.next());
            if(itr.hasNext()){
            	xPath.append("/");
            }
        }

        return xPath.toString();
    }

    /*
     * Homemade has text so we dont need outside libs.
     */
    protected boolean hasText(String string) {

        if (string == null || string.length() < 1) {
            return false;
        }
        int stringLength = string.length();

        for (int i = 0; i < stringLength; i++) {
            char currentChar = string.charAt(i);
            if (' ' != currentChar || '\t' != currentChar || '\n' != currentChar) {
                return true;
            }
        }

        return false;
    }

    protected Map<String, Object> toMap(Constraint c) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("minOccurs", c.getMinOccurs());
        result.put("maxOccurs", c.getMaxOccurs());
        result.put("minLength", c.getMinLength());
        result.put("maxLength", c.getMaxLength());
        result.put("minValue", c.getExclusiveMin());
        result.put("maxValue", c.getInclusiveMax());
        // result.put("dataType", c.getDataType());

        return result;
    }
    
    private void checkFieldRequired(AttributeDefinition field, BusinessObjectEntry objStructure) {
    	checkFieldRequired(field, objStructure, 0);
    }
    
    private void checkFieldRequired(AttributeDefinition field, BusinessObjectEntry objStructure, int numberOfOccurrences) {
        if (field.getMinOccurs() != null && field.getMinOccurs().intValue() > numberOfOccurrences) {
        	setFieldError(field, objStructure, RiceKeyConstants.ERROR_MIN_OCCURS, String.valueOf(field.getMinOccurs()));
        } else if (field.isRequired() != null && field.isRequired().booleanValue()) {
        	setFieldRequiredError(field, objStructure);
        }
    }
    
    private void checkFieldTooMany(AttributeDefinition field, BusinessObjectEntry objStructure, int numberOfOccurrences) {
    	Integer maxOccurs = tryParse(field.getMaxOccurs());
        if (maxOccurs != null && maxOccurs.intValue() < numberOfOccurrences) {
            setFieldError(field, objStructure, RiceKeyConstants.ERROR_MAX_OCCURS, field.getMaxOccurs());
        }
    }
    
    private void setFieldRequiredError(AttributeDefinition field, BusinessObjectEntry objStructure) {
    	setFieldError(field, objStructure, RiceKeyConstants.ERROR_REQUIRED);
    }
    
    private void setFieldError(AttributeDefinition field, BusinessObjectEntry objStructure, String key) {
    	String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objStructure.getBusinessObjectClass().getName(), field.getName());
    	GlobalVariables.getMessageMap().putError(field.getName(), key, errorLabel);
    }
    
    private void setFieldError(AttributeDefinition field, BusinessObjectEntry objStructure, String key, String ... args) {
    	String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objStructure.getBusinessObjectClass().getName(), field.getName());
    	// FIXME: There's got to be a cleaner way of doing this.
    	List<String> list = new LinkedList<String>();
    	list.add(errorLabel);
    	list.addAll(Arrays.asList(args));
    	String[] array = new String[list.size()];
    	array = list.toArray(array);
    	GlobalVariables.getMessageMap().putError(field.getName(), key, array);
    }
    

//    public SearchDispatcher getSearchDispatcher() {
//        return searchDispatcher;
//    }
//
//    public void setSearchDispatcher(SearchDispatcher searchDispatcher) {
//        this.searchDispatcher = searchDispatcher;
//    }

    @Override
	public List<ValidationResultInfo> validateObject(String fieldName,
			Object o,Stack<String> elementStack) {
		
		// TODO: look up AttributeDefinition and BusinessObjectEntry 
		
		return null;
	}

	/**
	 * @return the dataDictionaryService
	 */
	public DataDictionaryService getDataDictionaryService() {
		return this.dataDictionaryService;
	}

	/**
	 * @param dataDictionaryService the dataDictionaryService to set
	 */
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

	/**
	 * @return the persistenceStructureService
	 */
	public PersistenceStructureService getPersistenceStructureService() {
		return this.persistenceStructureService;
	}

	/**
	 * @param persistenceStructureService the persistenceStructureService to set
	 */
	public void setPersistenceStructureService(
			PersistenceStructureService persistenceStructureService) {
		this.persistenceStructureService = persistenceStructureService;
	}
}
