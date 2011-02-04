/**
 * Copyright 2010 The Kuali Foundation Licensed under the Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.osedu.org/licenses/ECL-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.kuali.rice.kns.datadictionary.validator;

import java.lang.reflect.InvocationTargetException;
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

import org.apache.log4j.Logger;
import org.kuali.rice.core.util.type.TypeUtils;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.dto.CaseConstraint;
import org.kuali.rice.kns.dto.Constrained;
import org.kuali.rice.kns.dto.ConstraintHolder;
import org.kuali.rice.kns.dto.DataType;
import org.kuali.rice.kns.dto.MustOccurConstraint;
import org.kuali.rice.kns.dto.RequiredConstraint;
import org.kuali.rice.kns.dto.ValidCharsConstraint;
import org.kuali.rice.kns.dto.WhenConstraint;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.workflow.service.WorkflowAttributePropertyResolutionService;


public class DefaultValidatorImpl implements Validator {
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
    
    private WorkflowAttributePropertyResolutionService workflowAttributePropertyResolutionService;
    
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
     * @param entryName TODO
     * @return
     */
//    public List<ValidationResultInfo> validateObject(Object data, BusinessObjectEntry objStructure, String entryName) {
//
//        Stack<String> elementStack = new Stack<String>();
//        return validateObject(data, objStructure, entryName, elementStack, true);
//    }
    
//    public class MaintenanceDocumentWrapperDataProvider implements AttributeValueReader {
//    	
//    	Map<String, Object> dataMap = null;
//    	
////		@Override
////		public String getObjectId() {
////			return (dataMap.containsKey("id") && null != dataMap.get("id")) ? dataMap.get("id").toString() : null;
////		}
//
//		@Override
//		public Object getValue(String fieldKey) {
//			return dataMap.get(fieldKey);
//		}
//
////		@Override
////		public Boolean hasField(String fieldKey) {
////			return Boolean.valueOf(dataMap.containsKey(fieldKey));
////		}
//
//		@Override
//		public void initialize(Object o) {
//			dataMap = new HashMap<String, Object>();
//			
//			MaintenanceDocumentWrapper wrapper = (MaintenanceDocumentWrapper)o;
//			BusinessObject businessObject = wrapper.getBusinessObject();
//			String docTypeName = wrapper.getDocTypeName();
//			MaintenanceDocumentEntry entry = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(docTypeName);
//			for (MaintainableSectionDefinition sectionDefinition : entry.getMaintainableSections()) {
//				List<? extends MaintainableItemDefinition> itemDefinitions = sectionDefinition.getMaintainableItems();
//				
//				for (MaintainableItemDefinition itemDefinition : itemDefinitions) {
//					if (itemDefinition instanceof MaintainableFieldDefinition) {
//						Boolean isAttributeDefined = getDataDictionaryService().isAttributeDefined(businessObject.getClass(), itemDefinition.getName());
//				        if (isAttributeDefined != null && isAttributeDefined.booleanValue()) {
//				            Object value = ObjectUtils.getPropertyValue(businessObject, itemDefinition.getName());
//				            if (value != null && StringUtils.isNotBlank(value.toString())) {
//					            Class<?> propertyType = ObjectUtils.getPropertyType(businessObject, itemDefinition.getName(), persistenceStructureService);
//					            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {
//					                // check value format against dictionary
//				                    if (!TypeUtils.isTemporalClass(propertyType)) {
//				                        //validateAttributeFormat(businessObject.getClass().getName(), itemDefinition.getName(), value.toString(), errorPrefix + itemDefinition.getName());
//				                    
//				                    	dataMap.put(itemDefinition.getName(), value.toString());
//				                    }
//					            }
//				            }
//				        }
//					}
//				}
//			}
//		}
//
//		
//		@Override
//		public String getPath() {
//			return "";
//		}
//    }
    
//    public class BusinessObjectAttributeWrapper {
//    	private Object object;
//    	private BusinessObjectEntry objStructure;
//    	private AttributeDefinition field;
//    	private String value;
//    	
//    	public BusinessObjectAttributeWrapper(Object object, BusinessObjectEntry objStructure, AttributeDefinition field, String value) {
//    		this.object = object;
//    		this.objStructure = objStructure;
//    		this.field = field;
//    		this.value = value;
//    	}
//
//		/**
//		 * @return the object
//		 */
//		public Object getObject() {
//			return this.object;
//		}
//
//		/**
//		 * @param object the object to set
//		 */
//		public void setObject(Object object) {
//			this.object = object;
//		}
//
//		/**
//		 * @return the objStructure
//		 */
//		public BusinessObjectEntry getObjStructure() {
//			return this.objStructure;
//		}
//
//		/**
//		 * @param objStructure the objStructure to set
//		 */
//		public void setObjStructure(BusinessObjectEntry objStructure) {
//			this.objStructure = objStructure;
//		}
//
//		/**
//		 * @return the field
//		 */
//		public AttributeDefinition getField() {
//			return this.field;
//		}
//
//		/**
//		 * @param field the field to set
//		 */
//		public void setField(AttributeDefinition field) {
//			this.field = field;
//		}
//
//		/**
//		 * @return the value
//		 */
//		public String getValue() {
//			return this.value;
//		}
//
//		/**
//		 * @param value the value to set
//		 */
//		public void setValue(String value) {
//			this.value = value;
//		}
//    	
//    	
//    }
//    
//    public class MaintenanceDocumentWrapper {
//    	private BusinessObject businessObject;
//    	private String docTypeName;
//    	
//    	public MaintenanceDocumentWrapper(BusinessObject businessObject, String docTypeName) {
//    		this.businessObject = businessObject;
//    		this.docTypeName = docTypeName;
//    	}
//
//		/**
//		 * @return the businessObject
//		 */
//		public BusinessObject getBusinessObject() {
//			return this.businessObject;
//		}
//
//		/**
//		 * @param businessObject the businessObject to set
//		 */
//		public void setBusinessObject(BusinessObject businessObject) {
//			this.businessObject = businessObject;
//		}
//
//		/**
//		 * @return the docTypeName
//		 */
//		public String getDocTypeName() {
//			return this.docTypeName;
//		}
//
//		/**
//		 * @param docTypeName the docTypeName to set
//		 */
//		public void setDocTypeName(String docTypeName) {
//			this.docTypeName = docTypeName;
//		}
//    	
//    }
    
    @Override
	public List<ValidationResultInfo> validate(String entryName, AttributeValueReader valueReader, boolean checkIfRequired) {
    	Stack<String> elementStack = new Stack<String>();
    	
    	return validateObject(entryName, valueReader, elementStack, true, checkIfRequired);
    }
    
    @Override
	public List<ValidationResultInfo> validate(String entryName, String fieldName, AttributeValueReader valueReader, boolean checkIfRequired) {
    	Stack<String> elementStack = new Stack<String>();
    	
    	return validateField(entryName, fieldName, valueReader, elementStack, checkIfRequired);
    }
    
//    @Override
//    public List<ValidationResultInfo> validateBusinessObjectOnMaintenanceDocument(BusinessObject businessObject, String docTypeName) {
//        Stack<String> elementStack = new Stack<String>();
//        
//        String entryName = businessObject.getClass().getName();
//        BusinessObjectEntry objStructure = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(entryName);
//        
//        AttributeValueReader dataProvider = new MaintenanceDocumentWrapperDataProvider();
//        dataProvider.initialize(new MaintenanceDocumentWrapper(businessObject, docTypeName));
//        
//        return validateObject(businessObject, objStructure, entryName, dataProvider, elementStack, true);
//    }
//    
//    
//    public List<ValidationResultInfo> validateAttributeField(String entryName, String fieldName, Object businessObject, String value, DataType dataType) {
//    	Stack<String> elementStack = new Stack<String>();
//    	
//    	BusinessObjectEntry objStructure = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(entryName);
//    	AttributeDefinition field = getDataDictionaryService().getConstrained(entryName, fieldName);
//    	
//    	AttributeValueReader dataProvider = new DictionaryObjectAttributeValueReader();
//    	dataProvider.initialize(new BusinessObjectAttributeWrapper(businessObject, objStructure, field, value));
//    	return validateField(field, objStructure, entryName, dataProvider, elementStack, value, dataType);
//    }
    

//    private List<ValidationResultInfo> validateObject(Object data, BusinessObjectEntry objStructure, String entryName, Stack<String> elementStack, boolean isRoot) {
//
//         AttributeValueReader dataProvider = new BeanConstraintDataProvider();
//         dataProvider.initialize(data);
//         
//         return validateObject(data, objStructure, entryName, dataProvider, elementStack, isRoot);
//    }

    
    private List<ValidationResultInfo> validateObject(String entryName, AttributeValueReader attributeValueReader, Stack<String> elementStack, boolean isRoot, boolean checkIfRequired) {

       List<ValidationResultInfo> results = new ArrayList<ValidationResultInfo>();

        // Push object structure to the top of the stack
        StringBuilder objXPathElement = new StringBuilder(attributeValueReader.getPath());

        if(!isRoot && !objXPathElement.toString().isEmpty()){
        	elementStack.push(objXPathElement.toString());
        }

        List<Constrained> definitions = attributeValueReader.getDefinitions();
     
        // Do nothing if the attribute value reader doesn't contain a dictionary entry or has no child definitions
        if (null == attributeValueReader.getEntry() || null == definitions) {
            return results;
        }
        
        
        for (Constrained definition : definitions) {
        	if (definition == null)
        		continue;
        	
        	String attributeName = definition.getName();
        	
            List<ValidationResultInfo> l = validateField(definition, entryName, attributeName, attributeValueReader, elementStack, checkIfRequired);

            if (l != null && l.size() > 0) {
            	String errorLabel = getDataDictionaryService().getAttributeErrorLabel(entryName, attributeName);
            	GlobalVariables.getMessageMap().putError(attributeName, RiceKeyConstants.ERROR_MAX_LENGTH, new String[] { errorLabel, "LENGTH" });
            }
            
            results.addAll(l);

            // Use Custom Validators
// FIXME: JLR - turning off custom validators
//            String customValidatorClass = f.getCustomValidatorClass(); //f.getConstraint() != null ? f.getConstraint().getCustomValidatorClass() : null;
//            if (customValidatorClass != null) {
//            	Validator customValidator = validatorFactory.getValidator(customValidatorClass);
//            	if(customValidator==null){
//            		throw new RuntimeException("Custom Validator "+customValidatorClass+" was not configured in this context");
//            	}
//            	l = customValidator.validateObject(f.getName(),data,elementStack);
//            	results.addAll(l);
//            }
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
    
    public List<ValidationResultInfo> validateField(String entryName, String attributeName, AttributeValueReader attributeValueReader, Stack<String> elementStack, boolean checkIfRequired) throws AttributeValidationException {
		Constrained definition = attributeValueReader.getDefinition(attributeName);
        return validateField(definition, entryName, attributeName, attributeValueReader, elementStack, checkIfRequired);
    }

    public List<ValidationResultInfo> validateField(Constrained definition, String entryName, String attributeName, AttributeValueReader attributeValueReader, Stack<String> elementStack, boolean checkIfRequired) throws AttributeValidationException {
    	Object value;
		try {
			value = attributeValueReader.getValue(attributeName);
		} catch (IllegalArgumentException e) {
			throw new AttributeValidationException("Could not validate attribute \"" + attributeName + "\" for entry \"" + entryName + "\" - unable to get the value for validation.", e);
		} catch (IllegalAccessException e) {
			throw new AttributeValidationException("Could not validate attribute \"" + attributeName + "\" for entry \"" + entryName + "\" - unable to get the value for validation.", e);
		} catch (InvocationTargetException e) {
			throw new AttributeValidationException("Could not validate attribute \"" + attributeName + "\" for entry \"" + entryName + "\" - unable to get the value for validation.", e);
		}

    	return validateField(definition, entryName, attributeName, value, attributeValueReader, elementStack, checkIfRequired);
    }
    
    public List<ValidationResultInfo> validateField(Constrained definition, String entryName, String attributeName, Object value, AttributeValueReader attributeValueReader, Stack<String> elementStack, boolean checkIfRequired) throws AttributeValidationException {

    	List<ValidationResultInfo> results = new ArrayList<ValidationResultInfo>();

    	// Since the attribute definition is where we keep constraints -- if the reader can't provide one, then it's an exception
    	if (definition == null)
    		throw new AttributeValidationException("Unable to validate constraints for attribute \"" + attributeName + "\" on entry \"" + entryName + "\" because no attribute definition can be found.");
    	
    	// Handle null values in field
    	// FIXME: JLR - removing empty string check here - need to re-add somewhere else, most likely
    	if (value == null) { // || "".equals(value.toString().trim())) {
    		processConstraint(results, definition, entryName, attributeName, value, attributeValueReader, elementStack);
    		return results;
    	}

    	// Check to see if this attribute has been declared to have a specific 'data type' like they do in Kuali Student
    	DataType dataType = definition.getDataType();
    	
    	// As per the KS-code, the only constraints that apply to complex objects structures are 1. TypeStateCase, 2. MinOccurs, and 3. MaxOccurs
    	if (dataType != null && dataType.equals(DataType.COMPLEX)) {
    		String childEntryName = definition.getChildEntryName();

    		DataDictionaryEntry childEntry = childEntryName != null ? getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(childEntryName) : null;

    		if (childEntry == null)
    			throw new AttributeValidationException("No valid child entry of the name " + childEntryName + " can be found in the data dictionary");
    		
    		elementStack.push(childEntryName);
    		
    		AttributeValueReader nestedAttributeValueReader = new DictionaryObjectAttributeValueReader(value, childEntryName, childEntry);
    		
    		if (value instanceof Collection) {

    			// FIXME: JLR - doesn't seem to be used. 
                String xPathForCollection = getElementXpath(elementStack) + "/*";

                int i=0;
                for (Object o : (Collection<?>) value) {
                	// FIXME: JLR - in code below this is Integer.toBinaryString()  -- should they be the same? 
                	elementStack.push(Integer.toString(i));
                	results.addAll(validateObject(childEntryName, nestedAttributeValueReader, elementStack, false, checkIfRequired));
                    elementStack.pop();
                    i++;
                }
                if (checkIfRequired)
                	checkFieldRequired(definition, entryName, ((Collection<?>) value).size());

                checkFieldTooMany(definition, entryName, ((Collection<?>) value).size());
            } else {
            	results.addAll(validateObject(childEntryName, nestedAttributeValueReader, elementStack, false, checkIfRequired));
            }
        } else { // If non complex data type

            if (value instanceof Collection) {

                if (((Collection<?>)value).isEmpty()) {
                    processConstraint(results, definition, entryName, attributeName, null, attributeValueReader, elementStack);
                }

            	int i = 0;
                for (Object o : (Collection<?>) value) {
                	elementStack.push(Integer.toBinaryString(i));
                    processConstraint(results, definition, entryName, attributeName, o, attributeValueReader, elementStack);
                    elementStack.pop();
                    i++;
                }

                String xPath = getElementXpath(elementStack) + "/" + attributeName + "/*";
                if (checkIfRequired)
                	checkFieldRequired(definition, entryName, ((Collection<?>) value).size());
                
                checkFieldTooMany(definition, entryName, ((Collection<?>) value).size());
            } else {
                processConstraint(results, definition, entryName, attributeName, value, attributeValueReader, elementStack);
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

//    protected void processNestedObjectStructure(List<ValidationResultInfo> results, Object value, DataDictionaryEntry nestedObjStruct, Constrained field, String entryName, Stack<String> elementStack) {
//
//        results.addAll(validateObject(value, nestedObjStruct, entryName, elementStack, false));
//
//    }

    protected void processConstraint(List<ValidationResultInfo> valResults, Constrained definition, String entryName, String attributeName, Object value, AttributeValueReader attributeValueReader, Stack<String> elementStack) {

    	try {
    		Constrained child = null;
    		// Process Case Constraint
    		// Case Constraint are only evaluated on the field. Nested case constraints are currently ignored
    		CaseConstraint caseConstraint = definition.getCaseConstraint();
    		if (null != caseConstraint) {
    			child = processCaseConstraint(valResults, caseConstraint, definition, entryName, attributeName, value, attributeValueReader, elementStack);
    		}
    		
    		Constrained nestedDefinition = (null != child) ? child : definition;

    		processBaseConstraints(valResults, nestedDefinition, entryName, attributeName, value, attributeValueReader, elementStack);

    		// Stop other checks if value is null
    		if (value == null) { // || "".equals(value.toString().trim())) {
    			return;
    		}

    		String elementPath = getElementXpath(elementStack) + "/" + attributeName;


    		// Process Valid Chars Constraint
    		ValidCharsConstraint validCharsConstraint = nestedDefinition.getValidChars();
    		if (null != validCharsConstraint) {
    			ValidationResultInfo val = processValidCharConstraint(validCharsConstraint, nestedDefinition, entryName, attributeName, value, attributeValueReader, elementPath);
    			if (null != val) {
    				valResults.add(val);
    			}
    		}

    		// Process Require Constraints (only if this field has value)
    		List<RequiredConstraint> requiredConstraints = nestedDefinition.getRequireConstraint();
    		if (null != requiredConstraints && requiredConstraints.size() > 0) {
    			for (RequiredConstraint requiredConstraint : requiredConstraints) {
    				ValidationResultInfo val;

    				val = processRequireConstraint(requiredConstraint, nestedDefinition, entryName, attributeName, attributeValueReader, elementPath);
    				if (null != val) {
    					valResults.add(val);
    				}
    			}
    		}

    		// Process Occurs Constraint
    		List<MustOccurConstraint> mustOccurConstraints = nestedDefinition.getOccursConstraint();
    		if (null != mustOccurConstraints && mustOccurConstraints.size() > 0) {
    			for (MustOccurConstraint occursConstraint : mustOccurConstraints) {
    				ValidationResultInfo val = processOccursConstraint(occursConstraint, nestedDefinition, entryName, attributeName, attributeValueReader, elementPath);
    				if (null != val) {
    					valResults.add(val);
    				}
    			}
    		}

    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
    	} catch (IllegalAccessException e) {
    		e.printStackTrace();
    	} catch (InvocationTargetException e) {
    		e.printStackTrace();
    	}

    	// Process lookup Constraint
    	//        if (null != constraint.getLookupDefinition()) {
    	//            processLookupConstraint(valResults, constraint.getLookupDefinition(), field, elementStack, dataProvider);
    	//        }
    }

    protected ValidationResultInfo processRequireConstraint(RequiredConstraint constraint, Constrained definition, String entryName, String attributeName, AttributeValueReader dataProvider, String element) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        ValidationResultInfo val = null;

        String fieldName = constraint.getFieldPath();
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
        	
        	Constrained attributeDefinition = getDataDictionaryService().getAttributeDefinition(entryName, fieldName);
        	if (attributeDefinition != null)
        		fieldName = attributeDefinition.getLabel();
        	
        	setFieldError(entryName, definition, RiceKeyConstants.ERROR_REQUIRES_FIELD, fieldName);
        }

        return val;
    }

    /**
     * Process caseConstraint tag and sets any of the base constraint items if any of the when condition matches
     * @param caseConstraint TODO
     * @param definition TODO
     * @param entryName TODO
     * @param attributeName TODO
     * @param constraint
     * @param caseConstraint
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    protected Constrained processCaseConstraint(List<ValidationResultInfo> valResults, CaseConstraint caseConstraint, Constrained definition, String entryName, String attributeName, Object value, AttributeValueReader attributeValueReader, Stack<String> elementStack) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        CaseConstraint constraint = definition.getCaseConstraint();

        if (null == constraint) {
            return null;
        }

        String operator = (hasText(constraint.getOperator())) ? constraint.getOperator() : "EQUALS";
        AttributeValueReader nestedReader = (hasText(constraint.getFieldPath())) ? ValidatorUtils.getDefinition(constraint.getFieldPath(), attributeValueReader) : null;

        // TODO: What happens when the field is not in the dataProvider?
        Constrained caseField = (null != nestedReader) ? nestedReader.getDefinition(nestedReader.getCurrentName()) : null;
        Object fieldValue = (null != nestedReader) ? nestedReader.getValue(nestedReader.getCurrentName()) : value;
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

    protected ValidationResultInfo processValidCharConstraint(ValidCharsConstraint validCharsConstraint, Constrained definition, String entryName, String attributeName, Object value, AttributeValueReader attributeValueReader, String element) {

        ValidationResultInfo val = null;

        StringBuilder fieldValue = new StringBuilder();
        String validChars = validCharsConstraint.getValue();

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
            	if (validCharsConstraint.getLabelKey()!=null) {
            		// FIXME: This shouldn't surface label key itself to the user - it should look up the label key, but this needs to be implemented in Rice
            		setFieldError(entryName, definition, RiceKeyConstants.ERROR_CUSTOM, validCharsConstraint.getLabelKey());
            	} else {
            		setFieldError(entryName, definition, RiceKeyConstants.ERROR_INVALID_FORMAT, fieldValue.toString());
            	}
            }
        }

        return val;
    }

    /**
     * Computes if all the filed required in the occurs clause are between the min and max
     * @param constraint
     * @param definition
     * @param entryName
     * @param attributeName TODO
     * @param dataProvider
     * @param valResults
     * @param type
     * @param state
     *
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    protected ValidationResultInfo processOccursConstraint(MustOccurConstraint constraint, Constrained definition, String entryName, String attributeName, AttributeValueReader dataProvider, String element) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        boolean result = false;
        int trueCount = 0;

        ValidationResultInfo val = null;

        for (RequiredConstraint rc : constraint.getRequiredFields()) {
            trueCount += (processRequireConstraint(rc, definition, entryName, attributeName, dataProvider, "") != null) ? 1 : 0;
        }

        for (MustOccurConstraint oc : constraint.getOccurs()) {
            trueCount += (processOccursConstraint(oc, definition, entryName, attributeName, dataProvider, "") != null) ? 1 : 0;
        }

        result = (trueCount >= constraint.getMin() && trueCount <= constraint.getMax()) ? true : false;

        if (!result) {
         // TODO: figure out what data should go here instead of null
//            val = new ValidationResultInfo(element, null);
//            val.setError(getMessage("validation.occurs"));
        	setFieldError(definition, entryName, RiceKeyConstants.ERROR_OCCURS);
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

    protected void processBaseConstraints(List<ValidationResultInfo> valResults, Constrained definition, String entryName, String attributeName, Object value, AttributeValueReader attributeValueReader, Stack<String> elementStack) {

        if (value == null || "".equals(value.toString().trim())) {
//            if ((constraint.getMinOccurs() != null && constraint.getMinOccurs() > 0)
//            		|| (field.isRequired() != null && field.isRequired().booleanValue())) {
//                ValidationResultInfo val = new ValidationResultInfo(getElementXpath(elementStack) + "/" + name, value);
//                val.setError(getMessage("validation.required"));
//                valResults.add(val);
//            	setFieldRequiredError(field, objStructure);
//            }
        	checkFieldRequired(definition, entryName);
            return;
        }

        String elementPath = getElementXpath(elementStack) + "/" + attributeName;

        DataType dataType = definition.getDataType();
        
        // FIXME: JLR - this needs to be refactored extensively to take advantage of the fact that we probably already know the data type at this point
        // and so don't have to do all of this heuristical stuff with strings
        if (dataType == null) {
        	Class<?> attributeType = attributeValueReader.getType(attributeName);
            if (TypeUtils.isStringClass(attributeType)) 
            	dataType = DataType.STRING;
            else if (TypeUtils.isIntegralClass(attributeType))
            	dataType = DataType.INTEGER;
            else if (TypeUtils.isDecimalClass(attributeType)) 
            	dataType = DataType.DOUBLE;
            else if (TypeUtils.isTemporalClass(attributeType))
            	dataType = DataType.DATE;
            else
            	dataType = DataType.STRING;
        }
        
        if (DataType.STRING.equals(dataType)) {
            validateString(definition, entryName, value, elementPath, valResults);
        } else if (DataType.INTEGER.equals(dataType)) {
            validateInteger(definition, entryName, value, elementPath, valResults);
        } else if (DataType.LONG.equals(dataType)) {
            validateLong(definition, entryName, value, elementPath, valResults);
        } else if (DataType.DOUBLE.equals(dataType)) {
            validateDouble(definition, entryName, value, elementPath, valResults);
        } else if (DataType.FLOAT.equals(dataType)) {
            validateFloat(definition, entryName, value, elementPath, valResults);
        } else if (DataType.BOOLEAN.equals(dataType)) {
            validateBoolean(definition, entryName, value, elementPath, valResults);
        } else if (DataType.DATE.equals(dataType)) {
            validateDate(definition, entryName, value, elementPath, valResults, dateParser);
        }
    }

    protected void validateBoolean(Constrained field, String entryName, Object value, String element, List<ValidationResultInfo> results) {
        if (!(value instanceof Boolean)) {
            try {
                Boolean.valueOf(value.toString());
            } catch (Exception e) {
//                ValidationResultInfo val = new ValidationResultInfo(element, value);
//                val.setError(getMessage("validation.mustBeBoolean"));
//                results.add(val);
            	setFieldError(field, entryName, RiceKeyConstants.ERROR_BOOLEAN);
            }
        }
    }

    protected void validateDouble(Constrained field, String entryName, Object value, String element, List<ValidationResultInfo> results) {
        Double v = null;

        ValidationResultInfo val = new ValidationResultInfo(element, value);

        if (value instanceof Number) {
            v = ((Number) value).doubleValue();
        } else {
            try {
                v = Double.valueOf(value.toString());
            } catch (Exception e) {
//                val.setError(getMessage("validation.mustBeDouble"));
            	setFieldError(field, entryName, RiceKeyConstants.ERROR_BIG_DECIMAL);
            }
        }

        if (val.isOk()) {
            Double maxValue = ValidatorUtils.getDouble(field.getInclusiveMax());
            Double minValue = ValidatorUtils.getDouble(field.getExclusiveMin());

            if (maxValue != null && minValue != null) {
                // validate range
                if (v > maxValue || v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v > maxValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected void validateFloat(Constrained field, String entryName, Object value, String element, List<ValidationResultInfo> results) {
        Float v = null;

        ValidationResultInfo val = new ValidationResultInfo(element, value);
        if (value instanceof Number) {
            v = ((Number) value).floatValue();
        } else {
            try {
                v = Float.valueOf(value.toString());
            } catch (Exception e) {
//                val.setError(getMessage("validation.mustBeFloat"));
                setFieldError(field, entryName, RiceKeyConstants.ERROR_BIG_DECIMAL);
            }
        }

        if (val.isOk()) {
            Float maxValue = ValidatorUtils.getFloat(field.getInclusiveMax());
            Float minValue = ValidatorUtils.getFloat(field.getExclusiveMin());

            if (maxValue != null && minValue != null) {
                // validate range
                if (v > maxValue || v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v > maxValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected void validateLong(Constrained field, String entryName, Object value, String element, List<ValidationResultInfo> results) {
        Long v = null;

        ValidationResultInfo val = new ValidationResultInfo(element, value);
        if (value instanceof Number) {
            v = ((Number) value).longValue();
        } else {
            try {
                v = Long.valueOf(value.toString());
            } catch (Exception e) {
            	setFieldError(field, entryName, RiceKeyConstants.ERROR_LONG);
            }
        }

        if (val.isOk()) {
            Long maxValue = ValidatorUtils.getLong(field.getInclusiveMax());
            Long minValue = ValidatorUtils.getLong(field.getExclusiveMin());

            if (maxValue != null && minValue != null) {
                // validate range
                if (v > maxValue || v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v > maxValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }

    }

    protected void validateInteger(Constrained field, String entryName, Object value, String element, List<ValidationResultInfo> results) {
        Integer v = null;

        ValidationResultInfo val = new ValidationResultInfo(element, value);

        if (value instanceof Number) {
            v = ((Number) value).intValue();
        } else {
            try {
                v = Integer.valueOf(value.toString());
            } catch (Exception e) {
            	setFieldError(field, entryName, RiceKeyConstants.ERROR_INTEGER);
            }
        }

        if (val.isOk()) {
            Integer maxValue = ValidatorUtils.getInteger(field.getInclusiveMax());
            Integer minValue = ValidatorUtils.getInteger(field.getExclusiveMin());

            if (maxValue != null && minValue != null) {
                // validate range
                if (v > maxValue || v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v > maxValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v < minValue) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected void validateDate(Constrained field, String entryName, Object value, String element, List<ValidationResultInfo> results, DateParser dateParser) {
        ValidationResultInfo val = new ValidationResultInfo(element, value);

        Date v = null;

        if (value instanceof Date) {
            v = (Date) value;
        } else {
            try {
                v = dateParser.parseDate(value.toString());
            } catch (Exception e) {
            	setFieldError(field, entryName, RiceKeyConstants.ERROR_DATE);
            }
        }

        if (val.isOk()) {
            Date maxValue = ValidatorUtils.getDate(field.getInclusiveMax(), dateParser);
            Date minValue = ValidatorUtils.getDate(field.getExclusiveMin(), dateParser);

            if (maxValue != null && minValue != null) {
                // validate range
                if (v.getTime() > maxValue.getTime() || v.getTime() < minValue.getTime()) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.outOfRange"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_OUT_OF_RANGE, field.getExclusiveMin(), field.getInclusiveMax());
                }
            } else if (maxValue != null) {
                if (v.getTime() > maxValue.getTime()) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.maxValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_INCLUSIVE_MAX, field.getInclusiveMax());
                }
            } else if (minValue != null) {
                if (v.getTime() < minValue.getTime()) {
//                    val.setError(MessageUtils.interpolate(getMessage("validation.minValueFailed"), toMap(constraint)));
                	setFieldError(entryName, field, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, field.getExclusiveMin());
                }
            }
        }

        if (!val.isOk()) {
            results.add(val);
        }
    }

    protected void validateString(Constrained field, String entryName, Object value, String element, List<ValidationResultInfo> results) {

        String s = value == null ? "" : value.toString().trim();

        ValidationResultInfo val = new ValidationResultInfo(element, value);

        Integer maxLength = field.getMaxLength(); //tryParse(field.getMaxLength());
        if (maxLength != null && field.getMinLength() != null && field.getMinLength().intValue() > 0) {
            if (s.length() > maxLength.intValue() || s.length() < field.getMinLength().intValue()) {
//                val.setError(MessageUtils.interpolate(getMessage("validation.lengthOutOfRange"), toMap(constraint)));
            	setFieldError(entryName, field, RiceKeyConstants.ERROR_LENGTH_OUT_OF_RANGE, String.valueOf(field.getMinLength()), String.valueOf(field.getMaxLength()));
            }
        } else if (maxLength != null) {
            if (s.length() > field.getMaxLength().intValue()) {
//                val.setError(MessageUtils.interpolate(getMessage("validation.maxLengthFailed"), toMap(constraint)));
            	setFieldError(entryName, field, RiceKeyConstants.ERROR_MAX_LENGTH, String.valueOf(field.getMaxLength()));
            }
        } else if (field.getMinLength() != null && field.getMinLength().intValue() > 0) {
            if (s.length() < field.getMinLength().intValue()) {
//                val.setError(MessageUtils.interpolate(getMessage("validation.minLengthFailed"), toMap(constraint)));
            	setFieldError(entryName, field, RiceKeyConstants.ERROR_MIN_LENGTH, String.valueOf(field.getMinLength()));
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

    protected Map<String, Object> toMap(ConstraintHolder c) {
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
    
    private void checkFieldRequired(Constrained field, String entryName) {
    	checkFieldRequired(field, entryName, 0);
    }
    
    private void checkFieldRequired(Constrained field, String entryName, int numberOfOccurrences) {
        if (field.getMinOccurs() != null && field.getMinOccurs().intValue() > numberOfOccurrences) {
        	setFieldError(entryName, field, RiceKeyConstants.ERROR_MIN_OCCURS, String.valueOf(field.getMinOccurs()));
        } else if (field.isRequired() != null && field.isRequired().booleanValue()) {
        	setFieldRequiredError(field, entryName);
        }
    }
    
    private void checkFieldTooMany(Constrained field, String entryName, int numberOfOccurrences) {
    	Integer maxOccurs = field.getMaxOccurs();
        if (maxOccurs != null && maxOccurs.intValue() < numberOfOccurrences) {
            setFieldError(entryName, field, RiceKeyConstants.ERROR_MAX_OCCURS, String.valueOf(field.getMaxOccurs()));
        }
    }
    
    private void setFieldRequiredError(Constrained field, String entryName) {
    	setFieldError(field, entryName, RiceKeyConstants.ERROR_REQUIRED);
    }
    
    private void setFieldError(Constrained field, String entryName, String key) {
    	String errorLabel = getDataDictionaryService().getAttributeErrorLabel(entryName, field.getName());
    	GlobalVariables.getMessageMap().putError(field.getName(), key, errorLabel);
    }
    
    private void setFieldError(String entryName, Constrained field, String key, String ... args) {
    	String errorLabel = getDataDictionaryService().getAttributeErrorLabel(entryName, field.getName());
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

//    @Override
//	public List<ValidationResultInfo> validateObject(String fieldName,
//			Object o,Stack<String> elementStack) {
//		
//		// TODO: look up Constrained and BusinessObjectEntry 
//		
//		return null;
//	}

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


	/**
	 * @return the workflowAttributePropertyResolutionService
	 */
	public WorkflowAttributePropertyResolutionService getWorkflowAttributePropertyResolutionService() {
    	if (workflowAttributePropertyResolutionService == null) {
    		workflowAttributePropertyResolutionService = KNSServiceLocator.getWorkflowAttributePropertyResolutionService();
    	}
    	return workflowAttributePropertyResolutionService;
	}


	/**
	 * @param workflowAttributePropertyResolutionService the workflowAttributePropertyResolutionService to set
	 */
	public void setWorkflowAttributePropertyResolutionService(
			WorkflowAttributePropertyResolutionService workflowAttributePropertyResolutionService) {
		this.workflowAttributePropertyResolutionService = workflowAttributePropertyResolutionService;
	}
}
