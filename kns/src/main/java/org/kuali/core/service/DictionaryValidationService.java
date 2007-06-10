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
package org.kuali.core.service;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.ApcRuleDefinition;
import org.kuali.core.datadictionary.ReferenceDefinition;
import org.kuali.core.document.Document;


/**
 * Defines the API for the validating against the data dictionary.
 * 
 * 
 */
public interface DictionaryValidationService {

    /**
     * Validates the contents of a document (i.e. attributes within a document) against the data dictionary.
     * 
     * @param document - document to validate
     */
    public void validateDocument(Document document);

    /**
     * Validates the contents of a document (i.e. attributes within a document) against the data dictionary. Recursively checks
     * business objects of the document.
     * 
     * @param document - document to validate
     * @param depth - Specify how deep the recrusion should go (0 based). If a negative number is supplied, it's infinite.
     */
    public void validateDocumentRecursively(Document document, int depth);

    /**
     * Validates the specified attribute of the given document against the data dictionary.
     * 
     * @param document
     * @param attributeName
     * @param errorPrefix
     */
    public void validateDocumentAttribute(Document document, String attributeName, String errorPrefix);


    /**
     * Validates the business object primitive attributes against the data dictionary. Adds errors to the map as they are
     * encountered.
     * 
     * @param businessObject - business object to validate
     */
    public void validateBusinessObject(PersistableBusinessObject businessObject);

    /**
     * Validates the business object primitive attributes against the data dictionary. Adds errors to the map as they are
     * encountered.
     * 
     * @param businessObject - business object to validate
     * @param validateRequired - whether to execute required field checks
     */
    public void validateBusinessObject(PersistableBusinessObject businessObject, boolean validateRequired);
    
    /**
     * Encapsulates <code>{@link #validateBusinessObject(BusinessObject) and returns boolean so one doesn't need to check the 
     * ErrorMap.Validates the business object primitive attributes against the data dictionary. Adds errors to the map as they are
     * encountered.<br/>
     * <br/>
     * Makes no error path adjustments
     * 
     * @param businessObject - business object to validate
     * @return boolean validOrNot
     */
    public boolean isBusinessObjectValid(PersistableBusinessObject businessObject);

    /**
     * Encapsulates <code>{@link #validateBusinessObject(BusinessObject) and returns boolean so one doesn't need to check the 
     * ErrorMap.Validates the business object primitive attributes against the data dictionary. Adds errors to the map as they are
     * encountered.<br/>
     * <br/>
     * Makes no error path adjustments
     * 
     * @param businessObject - business object to validate
     * @param prefix - error prefix
     * @return boolean valid or not
     */
    public boolean isBusinessObjectValid(PersistableBusinessObject businessObject, String prefix);

    /**
     * Validates the business object against the dictionary, uses reflection to get any child business objects, and recursively
     * calls back. Adds errors to the map as they are encountered.
     * 
     * @param businessObject - business object to validate
     * @param depth - Specify how deep the recrusion should go (0 based). If a negative number is supplied, it's infinite.
     */
    public void validateBusinessObjectsRecursively(PersistableBusinessObject businessObject, int depth);

    /**
     * Validates an attribute of a given class for proper min, max length, syntax, and required.
     * 
     * @param entryName - name of the dd entry
     * @param attributeName - name of attribute in the bo class
     * @param attributeValue - current value to validate
     * @param errorKey - key to place to errors under
     */
    public void validateAttributeFormat(String entryName, String attributeName, String attributeValue, String errorKey);


    /**
     * Validates an attribute of a given class for required check.
     * 
     * @param entryName - name of the dd entry
     * @param attributeName - name of attribute in the bo class
     * @param attributeValue - current value to validate
     * @param errorKey - key to place to errors under
     */
    public void validateAttributeRequired(String entryName, String attributeName, Object attributeValue, Boolean forMaintenance, String errorKey);

    /**
     * 
     * This method examines the populated BusinessObject bo instance passed in for a member named by the referenceName. If this
     * member exists, and if this member is a descendent of BusinessObject, then an existence check proceeds.
     * 
     * First the foreign keys for this reference are gathered, and then examined to see if they have values. If they do not have
     * values, the method ends with a true return value. If they all have values, then an object with those primary keys is retrieve
     * from the database. If one is retrieve, then the reference exists, and True is returned. Otherwise, false is returned.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo - The bo whose reference is being tested.
     * @param reference - The ReferenceDefinition to be existence tested.
     * @return True if no exceptions occur and the object exists in the db, false otherwise.
     * 
     */
    public boolean validateReferenceExists(PersistableBusinessObject bo, ReferenceDefinition reference);

    /**
     * 
     * This method examines the populated BusinessObject bo instance passed in for a member named by the referenceName. If this
     * member exists, and if this member is a descendent of BusinessObject, then an existence check proceeds.
     * 
     * First the foreign keys for this reference are gathered, and then examined to see if they have values. If they do not have
     * values, the method ends with a true return value. If they all have values, then an object with those primary keys is retrieve
     * from the database. If one is retrieve, then the reference exists, and True is returned. Otherwise, false is returned.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo - The bo whose reference is being tested.
     * @param referenceName - The name of the member to be existence tested.
     * @return True if no exceptions occur and the object exists in the db, false otherwise.
     * 
     */
    public boolean validateReferenceExists(PersistableBusinessObject bo, String referenceName);

    /**
     * 
     * This method retrieves the reference from the DB, and then tests whether the object is active.
     * 
     * It will return false if there is no activeIndicator field on this object, if the object doesnt exist in the DB, if the field
     * doesnt exist or cannot be cast as a boolean, if the field value is null, or if the field value is false.
     * 
     * It will only return true if the reference bo is present, the field is present, it is a boolean and non-null, and the value is
     * true.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo
     * @param reference
     * @return
     * 
     */
    public boolean validateReferenceIsActive(PersistableBusinessObject bo, ReferenceDefinition reference);

    /**
     * 
     * This method retrieves the reference from the DB, and then tests whether the object is active.
     * 
     * It will return false if there is no activeIndicator field on this object, if the object doesnt exist in the DB, if the field
     * doesnt exist or cannot be cast as a boolean, if the field value is null, or if the field value is false.
     * 
     * It will only return true if the reference bo is present, the field is present, it is a boolean and non-null, and the value is
     * true.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo
     * @param referenceName
     * @param activeIndicatorAttributeName
     * @param activeIndicatorReversed
     * @return
     * 
     */
    public boolean validateReferenceIsActive(PersistableBusinessObject bo, String referenceName, String activeIndicatorAttributeName, boolean activeIndicatorReversed);

    /**
     * 
     * This method intelligently tests the designated reference on the bo for both existence and active status, where appropriate.
     * 
     * It will not test anything if the foreign-key fields for the given reference arent filled out with values, and it will not
     * test active status if the reference doesnt exist.
     * 
     * Further, it will only test active status where the correct flag is set.
     * 
     * On failures of either sort, it will put the relevant errors into the GlobalVariables errorMap, and return a false. If there
     * are no failures, or nothing can be tested because the foreign-key fields arent fully filled out, it will return true and add
     * no errors.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo - the BusinessObject instance to be tested.
     * @param reference - the ReferenceDefinition to control the nature of the testing.
     * @return true or false as per the criteria above
     * 
     */
    public boolean validateReferenceExistsAndIsActive(PersistableBusinessObject bo, ReferenceDefinition reference);

    /**
     * 
     * This method intelligently tests the designated reference on the bo for both existence and active status, where appropriate.
     * 
     * It will not test anything if the foreign-key fields for the given reference arent filled out with values, and it will not
     * test active status if the reference doesnt exist.
     * 
     * Note that it will not fail or raise any error if all of the foreign-keys are filled with a value. If this needs to be tested
     * (ie, the 'if any field is filled, then all must be filled' rule), you'll have to do that separately.
     * 
     * Further, it will only test active status where the correct flag is set.
     * 
     * On failures of either sort, it will put the relevant errors into the GlobalVariables errorMap, and return a false. If there
     * are no failures, or nothing can be tested because the foreign-key fields arent fully filled out, it will return true and add
     * no errors.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo - the BusinessObject instance to be tested.
     * @param referenceName - the member name on the bo to be tested for existence and active-state
     * @param activeIndicatorAttributeName - the name on the class of the referenceName member to indicate active status
     * @param activeIndicatorReversed - a flag to indicate if the flag means closed or inactive, rather than active
     * @param activeIndicatorSet - a flag to control whether active state testing happens at all
     * @param attributeToHighlightOnFail - the fieldName to highlight with the error message on a failure
     * @param displayFieldName - the human-readable display name of the failed field, to go in the error message
     * @return true or false as per the criteria above
     */
    public boolean validateReferenceExistsAndIsActive(PersistableBusinessObject bo, String referenceName, String activeIndicatorAttributeName, boolean activeIndicatorReversed, boolean activeIndicatorSet, String attributeToHighlightOnFail, String displayFieldName);

    /**
     * 
     * This method does an existence check against all references of a BusinessObject as defined in the MaintenanceDocument.xml file
     * for that business object.
     * 
     * Appropriate errors will also be placed in the GlobalVariables.ErrorMap.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo - BusinessObject instance that should be tested
     * @return true if all passed existence tests, false if any failed
     * 
     */
    public boolean validateDefaultExistenceChecks(PersistableBusinessObject bo);

    /**
     * 
     * This method applies a specific rule against the given BusinessObject as defined in the MaintenanceDocument.xml file.
     * 
     * Appropriate errors will also be placed in the GlobalVariables.ErrorMap.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo
     * @param apcRule
     * @return true if rule passes
     */
    public boolean validateApcRule(PersistableBusinessObject bo, ApcRuleDefinition apcRule);

    /**
     * This method applies all rules against the given BusinessObject as defined in the MaintenanceDocument.xml file.
     * 
     * Appropriate errors will also be placed in the GlobalVariables.ErrorMap.
     * 
     * This method assumes that you already have the errorPath set exactly as desired, and adds new errors to the errorMap with no
     * prefix, other than what has already been pushed onto the errorMap.
     * 
     * @param bo
     * @return true if rule passes
     */
    public boolean validateApcRules(PersistableBusinessObject bo);
}