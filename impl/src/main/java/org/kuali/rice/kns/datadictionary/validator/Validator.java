package org.kuali.rice.kns.datadictionary.validator;

import java.util.List;

public interface Validator {

	/*public List<ValidationResultInfo> validateObject(Object o, BusinessObjectEntryDTO objStructure);
	public List<ValidationResultInfo> validateObject(AttributeDefinitionDTO field,Object o, BusinessObjectEntryDTO objStructure,Stack<String> elementStack);
		
	public List<ValidationResultInfo> validateField(AttributeDefinitionDTO field, BusinessObjectEntryDTO objStruct, ConstraintDataProvider dataProvider, Stack<String> elementStack);
	*/
	
	public List<ValidationResultInfo> validate(String entryName, AttributeValueReader valueReader, boolean checkIfRequired);
	
	public void validate(String entryName, String fieldName, AttributeValueReader valueReader, boolean checkIfRequired);
	
	
//	public List<ValidationResultInfo> validateObject(String fieldName, Object businessObject, Stack<String> elementStack);
//	
//	public List<ValidationResultInfo> validateBusinessObjectOnMaintenanceDocument(BusinessObject businessObject, String docTypeName);
//	
//	public List<ValidationResultInfo> validateAttributeField(String objectClassName, String fieldName, Object businessObject, String value, DataType dataType);
//	
}
