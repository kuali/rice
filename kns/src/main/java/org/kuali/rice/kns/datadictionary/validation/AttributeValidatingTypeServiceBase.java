/**
 * Copyright 2005-2012 The Kuali Foundation
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
/*
* Copyright 2006-2012 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.validation;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableAttributeFieldContract;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceHelper;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.comparator.StringValueComparator;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryRemoteFieldService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.jws.WebParam;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * An abstract base class for type service implementations which provides default validation of attributes from the Data
 * Dictionary.  It attempts to remain module independent by leaving the translation of the attribute definitions to a
 * generic format that includes the required {@link RemotableAttributeField}s as an unimplemented template method,
 * see{@link #getTypeAttributeDefinitions(String)}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AttributeValidatingTypeServiceBase /*implements KimTypeService*/ {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AttributeValidatingTypeServiceBase.class);
    private static final String ANY_CHAR_PATTERN_S = ".*";
    private static final Pattern ANY_CHAR_PATTERN = Pattern.compile(ANY_CHAR_PATTERN_S);

	private DictionaryValidationService dictionaryValidationService;
	private DataDictionaryService dataDictionaryService;
    private DataDictionaryRemoteFieldService dataDictionaryRemoteFieldService;

    /**
     * Retrieves active type attribute definitions and translates them into a module-independent representation.  Note
     * that they should be returned in the order desired for display.
     * @param typeId
     * @return a correctly ordered List of active, module-independent type attribute definitions
     */
    protected abstract List<TypeAttributeDefinition> getTypeAttributeDefinitions(String typeId);

    protected abstract List<RemotableAttributeError>
    validateNonDataDictionaryAttribute(RemotableAttributeField attr, String key, String value);



    /**
     * This is the default implementation.  It calls into the service for each attribute to
     * validate it there.  No combination validation is done.  That should be done
     * by overriding this method.
     */
    public List<RemotableAttributeError> validateAttributes(String typeId, Map<String, String> attributes) {

        if (StringUtils.isBlank(typeId)) {
            throw new RiceIllegalArgumentException("typeId was null or blank");
        }

        if (attributes == null) {
            throw new RiceIllegalArgumentException("attributes was null or blank");
        }

        List<TypeAttributeDefinition> definitions = getTypeAttributeDefinitions(typeId);
        Map<String, TypeAttributeDefinition> typeAttributeDefinitionMap =
                buildTypeAttributeDefinitionMapByName(definitions);

        final List<RemotableAttributeError> validationErrors = new ArrayList<RemotableAttributeError>();

        for ( Map.Entry<String, String> entry : attributes.entrySet() ) {

            TypeAttributeDefinition typeAttributeDefinition = typeAttributeDefinitionMap.get(entry.getKey());

            final List<RemotableAttributeError> attributeErrors;
            if ( typeAttributeDefinition.getComponentName() == null) {
                attributeErrors = validateNonDataDictionaryAttribute(typeAttributeDefinition.getField(), entry.getKey(), entry.getValue());
            } else {
                attributeErrors = validateDataDictionaryAttribute(typeAttributeDefinition, entry.getKey(), entry.getValue());
            }

            if ( attributeErrors != null ) {
                validationErrors.addAll(attributeErrors);
            }
        }


        final List<RemotableAttributeError> referenceCheckErrors = validateReferencesExistAndActive(typeAttributeDefinitionMap, attributes, validationErrors);
        validationErrors.addAll(referenceCheckErrors);

        return Collections.unmodifiableList(validationErrors);
    }

    private Map<String, TypeAttributeDefinition> buildTypeAttributeDefinitionMapByName(
            List<TypeAttributeDefinition> definitions) {// throw them into a map by name
        Map<String, TypeAttributeDefinition> typeAttributeDefinitionMap;
        if (definitions == null || definitions.size() == 0) {
            typeAttributeDefinitionMap = Collections.<String, TypeAttributeDefinition>emptyMap();
        } else {
            typeAttributeDefinitionMap = new HashMap<String, TypeAttributeDefinition>();

            for (TypeAttributeDefinition definition : definitions) {
                typeAttributeDefinitionMap.put(definition.getName(), definition);
            }
        }
        return typeAttributeDefinitionMap;
    }


    //
    // Here there be dragons -- adapted from DataDictionaryTypeServiceBase, please excuse X-.
    //
	protected List<RemotableAttributeError> validateReferencesExistAndActive( Map<String, TypeAttributeDefinition> typeAttributeDefinitionMap, Map<String, String> attributes, List<RemotableAttributeError> previousValidationErrors) {

		Map<String, BusinessObject> componentClassInstances = new HashMap<String, BusinessObject>();
		List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();

        // Create an instance of each component and shove it into the componentClassInstances
		for ( String attributeName : attributes.keySet() ) {
			TypeAttributeDefinition attr = typeAttributeDefinitionMap.get(attributeName);

			if (StringUtils.isNotBlank(attr.getComponentName())) {
				if (!componentClassInstances.containsKey(attr.getComponentName())) {
					try {
						Class<?> componentClass = Class.forName(attr.getComponentName());
						if (!BusinessObject.class.isAssignableFrom(componentClass)) {
							LOG.warn("Class " + componentClass.getName() + " does not implement BusinessObject.  Unable to perform reference existence and active validation");
							continue;
						}
						BusinessObject componentInstance = (BusinessObject) componentClass.newInstance();
						componentClassInstances.put(attr.getComponentName(), componentInstance);
					} catch (Exception e) {
						LOG.error("Unable to instantiate class for attribute: " + attributeName, e);
					}
				}
			}
		}

		// now that we have instances for each component class, try to populate them with any attribute we can,
		// assuming there were no other validation errors associated with it
		for ( Map.Entry<String, String> entry : attributes.entrySet() ) {
			if (!RemotableAttributeError.containsAttribute(entry.getKey(), previousValidationErrors)) {
				for (Object componentInstance : componentClassInstances.values()) {
					try {
						ObjectUtils.setObjectProperty(componentInstance, entry.getKey(), entry.getValue());
					} catch (NoSuchMethodException e) {
						// this is expected since not all attributes will be in all components
					} catch (Exception e) {
						LOG.error("Unable to set object property class: " + componentInstance.getClass().getName() + " property: " + entry.getKey(), e);
					}
				}
			}
		}

		for (Map.Entry<String, BusinessObject> entry : componentClassInstances.entrySet()) {
			List<RelationshipDefinition> relationships = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(entry.getKey()).getRelationships();
			if (relationships == null) {
				continue;
			}

			for (RelationshipDefinition relationshipDefinition : relationships) {
				List<PrimitiveAttributeDefinition> primitiveAttributes = relationshipDefinition.getPrimitiveAttributes();

				// this code assumes that the last defined primitiveAttribute is the attributeToHighlightOnFail
				String attributeToHighlightOnFail = primitiveAttributes.get(primitiveAttributes.size() - 1).getSourceName();

				// TODO: will this work for user ID attributes?

                if (attributes.containsKey(attributeToHighlightOnFail)) {

                    TypeAttributeDefinition attr = typeAttributeDefinitionMap.get(attributeToHighlightOnFail);
                    if (attr != null) {
                        final String attributeDisplayLabel;
                        if (StringUtils.isNotBlank(attr.getComponentName())) {
                            attributeDisplayLabel = getDataDictionaryService().getAttributeLabel(attr.getComponentName(), attributeToHighlightOnFail);
                        } else {
                            attributeDisplayLabel = attr.getLabel();
                        }

                        getDictionaryValidationService().validateReferenceExistsAndIsActive(entry.getValue(), relationshipDefinition.getObjectAttributeName(),
                                attributeToHighlightOnFail, attributeDisplayLabel);
                    }
                    List<String> extractedErrors = extractErrorsFromGlobalVariablesErrorMap(attributeToHighlightOnFail);
                    if (CollectionUtils.isNotEmpty(extractedErrors)) {
                        errors.add(RemotableAttributeError.Builder.create(attributeToHighlightOnFail, extractedErrors).build());
                    }
                }
			}
		}
		return errors;
	}

    protected List<RemotableAttributeError> validateAttributeRequired(RemotableAttributeField field, String objectClassName, String attributeName, Object attributeValue, String errorKey) {
        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        // check if field is a required field for the business object

        if (attributeValue == null || (attributeValue instanceof String && StringUtils.isBlank((String) attributeValue))) {

            boolean required = field.isRequired();
            if (required) {
                // get label of attribute for message
                String errorLabel = getAttributeErrorLabel(field);
                errors.add(RemotableAttributeError.Builder.create(errorKey,
                        createErrorString(RiceKeyConstants.ERROR_REQUIRED, errorLabel)).build());
            }
        }

        return errors;
    }

    protected static String getAttributeErrorLabel(RemotableAttributeField definition) {
        String longAttributeLabel = definition.getLongLabel();
        String shortAttributeLabel = definition.getShortLabel();

        return longAttributeLabel + " (" + shortAttributeLabel + ")";
    }

    /** will create a string like the following:
     * errorKey:param1;param2;param3;
     *
     * @param errorKey the errorKey
     * @param params the error params
     * @return error string
     */
    protected static String createErrorString(String errorKey, String... params) {
        final StringBuilder s = new StringBuilder(errorKey).append(':');
        if (params != null) {
            for (String p : params) {
                if (p != null) {
                    s.append(p);
                    s.append(';');
                }
            }
        }
        return s.toString();
    }

	protected List<RemotableAttributeError> validateDataDictionaryAttribute(TypeAttributeDefinition typeAttributeDefinition, String entryName, Object object, PropertyDescriptor propertyDescriptor) {
		return validatePrimitiveFromDescriptor(typeAttributeDefinition.getField(), entryName, object, propertyDescriptor);
	}

    protected List<RemotableAttributeError> validatePrimitiveFromDescriptor(RemotableAttributeField field, String entryName, Object object, PropertyDescriptor propertyDescriptor) {
        List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();
        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor && getDataDictionaryService().isAttributeDefined(entryName, propertyDescriptor.getName())) {
            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class<?> propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        errors.addAll(validateAttributeFormat(field, entryName, propertyDescriptor.getName(), value.toString(), propertyDescriptor.getName()));
                    }
                }
                else {
                	// if it's blank, then we check whether the attribute should be required
                    errors.addAll(validateAttributeRequired(field, entryName, propertyDescriptor.getName(), value, propertyDescriptor.getName()));
                }
            }
        }
        return errors;
    }

    protected Pattern getAttributeValidatingExpression(RemotableAttributeField field) {
        if (field == null || StringUtils.isBlank(field.getRegexConstraint())) {
            return ANY_CHAR_PATTERN;
        }

        return Pattern.compile(field.getRegexConstraint());
     }

	protected Formatter getAttributeFormatter(RemotableAttributeField field) {
        if (field.getDataType() == null) {
            return null;
        }

        return Formatter.getFormatter(field.getDataType().getType());
    }

    protected List<RemotableAttributeError> validateAttributeFormat(RemotableAttributeField field, String objectClassName, String attributeName, String attributeValue, String errorKey) {
    	List<RemotableAttributeError> errors = new ArrayList<RemotableAttributeError>();

        String errorLabel = getAttributeErrorLabel(field);

        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("(bo, attributeName, attributeValue) = (" + objectClassName + "," + attributeName + "," + attributeValue + ")");
        }

        if (StringUtils.isNotBlank(attributeValue)) {
            Integer maxLength = field.getMaxLength();
            if ((maxLength != null) && (maxLength.intValue() < attributeValue.length())) {
                errors.add(RemotableAttributeError.Builder.create(errorKey,
                        createErrorString(RiceKeyConstants.ERROR_MAX_LENGTH, errorLabel, maxLength.toString())).build());
                return errors;
            }
            Pattern validationExpression = getAttributeValidatingExpression(field);
            if (!ANY_CHAR_PATTERN_S.equals(validationExpression.pattern())) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug("(bo, attributeName, validationExpression) = (" + objectClassName + "," + attributeName + "," + validationExpression + ")");
            	}

                if (!validationExpression.matcher(attributeValue).matches()) {
                    boolean isError=true;
                    final Formatter formatter = getAttributeFormatter(field);
                    if (formatter != null) {
                        Object o = formatter.format(attributeValue);
                        isError = !validationExpression.matcher(String.valueOf(o)).matches();
                    }
                    if (isError) {
                        // if createErrorString is abstracted or externalized, need to do it here too in place of
                        // field.getRegexConstraintMsg()
                        errors.add(RemotableAttributeError.Builder.create(errorKey, field.getRegexContraintMsg())
                                .build());
                    }
                    return errors;
                }
            }
            Double min = field.getMinValue();
            if (min != null) {
                try {
                    if (Double.parseDouble(attributeValue) < min) {
                        errors.add(RemotableAttributeError.Builder.create(errorKey, createErrorString(
                                RiceKeyConstants.ERROR_INCLUSIVE_MIN, errorLabel, min.toString())).build());
                        return errors;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a min for a non-numeric attribute
                }
            }
            Double max = field.getMaxValue();
            if (max != null) {
                try {

                    if (Double.parseDouble(attributeValue) > max) {
                        errors.add(RemotableAttributeError.Builder.create(errorKey, createErrorString(
                                RiceKeyConstants.ERROR_INCLUSIVE_MAX, errorLabel, max.toString())).build());
                        return errors;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a max for a non-numeric attribute
                }
            }
        }
        return errors;
    }


    /**
     * will create a list of errors in the following format:
     *
     * error_key:param1;param2;param3;
     */
	protected List<String> extractErrorsFromGlobalVariablesErrorMap(String attributeName) {
		Object results = GlobalVariables.getMessageMap().getErrorMessagesForProperty(attributeName);
		List<String> errors = new ArrayList<String>();
        if (results instanceof String) {
        	errors.add((String)results);
        } else if ( results != null) {
        	if (results instanceof List) {
	        	List<?> errorList = (List<?>)results;
	        	for (Object msg : errorList) {
	        		ErrorMessage errorMessage = (ErrorMessage)msg;
	        		errors.add(DataDictionaryTypeServiceHelper.createErrorString(errorMessage.getErrorKey(),
                            errorMessage.getMessageParameters()));
				}
	        } else {
	        	String [] temp = (String []) results;
	        	for (String string : temp) {
					errors.add(string);
				}
	        }
        }
        GlobalVariables.getMessageMap().removeAllErrorMessagesForProperty(attributeName);
        return errors;
	}


    protected List<RemotableAttributeError> validateDataDictionaryAttribute(TypeAttributeDefinition typeAttributeDefinition, String attributeName, String value) {
		try {
            // create an object of the proper type per the component
            Object componentObject = Class.forName( typeAttributeDefinition.getComponentName() ).newInstance();

            if ( attributeName != null ) {
                // get the bean utils descriptor for accessing the attribute on that object
                PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(componentObject, attributeName);
                if ( propertyDescriptor != null ) {
                    // set the value on the object so that it can be checked
                    Object attributeValue = getAttributeValue(propertyDescriptor, value);
                    propertyDescriptor.getWriteMethod().invoke( componentObject, attributeValue);
                    return validateDataDictionaryAttribute(typeAttributeDefinition,
                            typeAttributeDefinition.getComponentName(), componentObject, propertyDescriptor);
                }
            }
        } catch (Exception e) {
            throw new TypeAttributeValidationException(e);
        }
        return Collections.emptyList();
	}

    private Object getAttributeValue(PropertyDescriptor propertyDescriptor, String attributeValue){
        Object attributeValueObject = null;
        if(propertyDescriptor!=null && attributeValue!=null){
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            if (String.class.equals(propertyType)){
                attributeValueObject = KRADUtils
                        .createObject(propertyType, new Class[]{String.class}, new Object[]{attributeValue});
            } else {
                attributeValueObject = attributeValue;
            }
        }
        return attributeValueObject;
    }


    // lazy initialization holder class
    private static class DictionaryValidationServiceHolder {
        public static DictionaryValidationService dictionaryValidationService =
                KRADServiceLocatorWeb.getDictionaryValidationService();
    }

	protected DictionaryValidationService getDictionaryValidationService() {
		return DictionaryValidationServiceHolder.dictionaryValidationService;
	}

    // lazy initialization holder class
    private static class DataDictionaryServiceHolder {
        public static DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
    }

	protected DataDictionaryService getDataDictionaryService() {
		return DataDictionaryServiceHolder.dataDictionaryService;
	}

    // lazy initialization holder class
    private static class DataDictionaryRemoteFieldServiceHolder {
        public static DataDictionaryRemoteFieldService dataDictionaryRemoteFieldService =
                KRADServiceLocatorWeb.getDataDictionaryRemoteFieldService();
    }

    protected DataDictionaryRemoteFieldService getDataDictionaryRemoteFieldService() {
        return DataDictionaryRemoteFieldServiceHolder.dataDictionaryRemoteFieldService;
    }


    protected static class TypeAttributeValidationException extends RuntimeException {

        protected TypeAttributeValidationException(String message) {
            super( message );
        }

        protected TypeAttributeValidationException(Throwable cause) {
            super( cause );
        }

        private static final long serialVersionUID = 8220618846321607801L;

    }


    /**
     * A module-independent representation of a type attribute containing all the information that we need
     * in order to validate data dictionary-based attributes.
     */
    protected static class TypeAttributeDefinition {

        private final RemotableAttributeField field;
        private final String name;
        private final String componentName;
        private final String label;
        private final Map<String, String> properties;

        /**
         * Constructs a {@link TypeAttributeDefinition}
         * @param field the RemotableAttributeField corresponding to this definition.  Must not be null.
         * @param name the name for this attribute.  Must not be empty or null.
         * @param componentName The name of a data dictionary component that this field refers to. May be null.
         * @param label The label to use for this attribute.  May be null.
         */
        public TypeAttributeDefinition(RemotableAttributeField field, String name, String componentName, String label, Map<String, String> properties) {
            if (field == null) throw new RiceIllegalArgumentException("field must not be null");
            if (StringUtils.isEmpty(name)) throw new RiceIllegalArgumentException("name must not be empty or null");
            this.field = field;
            this.name = name;
            this.componentName = componentName;
            this.label = label;

            if (properties == null || properties.isEmpty()) {
                this.properties = Collections.emptyMap();
            } else {
                // make our local variable into a copy of the passed in Map
                properties = new HashMap<String, String>(properties);
                // assign in in immutable form to our class member variable
                this.properties = Collections.unmodifiableMap(properties);
            }
        }

        public RemotableAttributeField getField() {
            return field;
        }

        public String getName() {
            return name;
        }

        public String getComponentName() {
            return componentName;
        }

        public String getLabel() {
            return label;
        }

        /**
         * @return an unmodifiable map of properties for this attribute.  Will never be null.
         */
        public Map<String, String> getProperties() {
            return properties;
        }
    }
}
