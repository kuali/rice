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
package org.kuali.rice.krad.uif.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.WhenConstraint;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.field.AttributeField;

/**
 * This class contains all the methods necessary for generating the js required to perform validation client side.
 * The processAndApplyConstraints(AttributeField field, View view) is the key method of this class used by
 * AttributeField to setup its client side validation mechanisms.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ClientValidationUtils {
	// used to give validation methods unique signatures
	private static int methodKey = 0;
	
	// list used to temporarily store mustOccurs field names for the error
	// message
	private static List<List<String>> mustOccursPathNames;
	
	public static final String LABEL_KEY_SPLIT_PATTERN = ",";
	
	public static final String VALIDATION_MSG_KEY_PREFIX = "validation.";
	public static final String PREREQ_MSG_KEY = "prerequisite";
	public static final String POSTREQ_MSG_KEY = "postrequisite";
	public static final String MUSTOCCURS_MSG_KEY = "mustOccurs";
	public static final String GENERIC_FIELD_MSG_KEY = "general.genericFieldName";
	
	public static final String ALL_MSG_KEY = "general.all";
	public static final String ATMOST_MSG_KEY = "general.atMost";
	public static final String AND_MSG_KEY = "general.and";
	public static final String OR_MSG_KEY = "general.or";
	
	private static ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();
	
	//Enum representing names of rules provided by the jQuery plugin
	public static enum ValidationMessageKeys{
		REQUIRED("required"), 
		MIN_EXCLUSIVE("minExclusive"), 
		MAX_INCLUSIVE("maxInclusive"),
		MIN_LENGTH("minLengthConditional"),
		MAX_LENGTH("maxLengthConditional"),
		EMAIL("email"),
		URL("url"),
		DATE("date"),
		NUMBER("number"),
		DIGITS("digits"),
		CREDITCARD("creditcard"),
		LETTERS_WITH_BASIC_PUNC("letterswithbasicpunc"),
		ALPHANUMERIC("alphanumeric"),
		LETTERS_ONLY("lettersonly"),
		NO_WHITESPACE("nowhitespace"),
		INTEGER("integer"),
		PHONE_US("phoneUS"),
		TIME("time");
		
		private ValidationMessageKeys(String name) {
			this.name = name;
		}
		
		private final String name;
		
		@Override
		public String toString() {
			return name;
		}
		
		public static boolean contains(String name){
            for (ValidationMessageKeys element : EnumSet.allOf(ValidationMessageKeys.class)) {
                if (element.toString().equalsIgnoreCase(name)) {
                    return true;
                }
            }
            return false;
		}
	}
	
	public static String generateMessageFromLabelKey(String labelKey){
		String message = "NO MESSAGE";
		if(StringUtils.isNotEmpty(labelKey)){
			if(labelKey.contains(LABEL_KEY_SPLIT_PATTERN)){
				message = "";
				String[] tokens = labelKey.split(LABEL_KEY_SPLIT_PATTERN);
				int i = 0;
				for(String s: tokens){
					String ps = configService.getPropertyString(VALIDATION_MSG_KEY_PREFIX + s);
					i++;
					if(i != tokens.length){
						message = message + ps + ", ";
					}
					else{
						message = message + ps;
					}
				}
			}
			else{
				message = configService.getPropertyString(VALIDATION_MSG_KEY_PREFIX + labelKey);
			}
		}
		return message;
	}

	/**
	 * Generates the js object used to override all default messages for validator jquery plugin with custom
	 * messages derived from the configService.
	 * 
	 * @return
	 */
	public static String generateValidatorMessagesOption(){
		String mOption = "";
		String keyValuePairs = "";
		for(ValidationMessageKeys element : EnumSet.allOf(ValidationMessageKeys.class)){
			String key = element.toString();
			String message = configService.getPropertyString(VALIDATION_MSG_KEY_PREFIX + key);
			if(StringUtils.isNotEmpty(message)){
				keyValuePairs = keyValuePairs + "\n" + key + ": '"+ message + "',";
				
			}
			
		}
		keyValuePairs = StringUtils.removeEnd(keyValuePairs, ",");
		if(StringUtils.isNotEmpty(keyValuePairs)){
			mOption="{" + keyValuePairs + "}";
		}
		
		return mOption;
	}
	
	/**
	 * Returns the add method jquery validator call for the regular expression
	 * stored in validCharactersConstraint.
	 * 
	 * @param validCharactersConstraint
	 * @return js validator.addMethod script
	 */
	public static String getRegexMethod(AttributeField field, ValidCharactersConstraint validCharactersConstraint) {
		String message = generateMessageFromLabelKey(validCharactersConstraint.getLabelKey());
		String key = "validChar-" + field.getBindingInfo().getBindingPath() + methodKey;
		
		return "\njQuery.validator.addMethod(\"" + key
				+ "\", function(value, element) {\n" + " return this.optional(element) || "
				+ "/" + validCharactersConstraint.getValue() + "/.test(value); " + "}, \""
				+ message + "\");";
	}
	

	/**
	 * This method processes a single CaseConstraint. Internally it makes calls
	 * to processWhenConstraint for each WhenConstraint that exists in this
	 * constraint. It adds a "dependsOn" css class to this field for the field
	 * which the CaseConstraint references.
	 * 
	 * @param view
	 * @param andedCase
	 *            the boolean logic to be anded when determining if this case is
	 *            satisfied (used for nested CaseConstraints)
	 */
	public static void processCaseConstraint(AttributeField field, View view, CaseConstraint constraint, String andedCase) {
		if (constraint.getOperator() == null) {
			constraint.setOperator("equals");
		}

		String operator = "==";
		if (constraint.getOperator().equalsIgnoreCase("not_equals")) {
			operator = "!=";
		}
		else if (constraint.getOperator().equalsIgnoreCase("greater_than_equal")) {
			operator = ">=";
		}
		else if (constraint.getOperator().equalsIgnoreCase("less_than_equal")) {
			operator = "<=";
		}
		else if (constraint.getOperator().equalsIgnoreCase("greater_than")) {
			operator = ">";
		}
		else if (constraint.getOperator().equalsIgnoreCase("less_than")) {
			operator = "<";
		}
		else if (constraint.getOperator().equalsIgnoreCase("has_value")) {
			operator = "";
		}
		// add more operator types here if more are supported later

		field.getControl().addStyleClass("dependsOn-" + constraint.getFieldPath());

		if (constraint.getWhenConstraint() != null && !constraint.getWhenConstraint().isEmpty()) {
			for (WhenConstraint wc : constraint.getWhenConstraint()) {
				processWhenConstraint(field, view, constraint, wc, constraint.getFieldPath(), operator, andedCase);
			}
		}
	}
	


	/**
	 * This method processes the WhenConstraint passed in. The when constraint
	 * is used to create a boolean statement to determine if the constraint will
	 * be applied. The necessary rules/methods for applying this constraint are
	 * created in the createRule call. Note the use of the use of coerceValue js
	 * function call.
	 * 
	 * @param view
	 * @param wc
	 * @param fieldPath
	 * @param operator
	 * @param andedCase
	 */
	private static void processWhenConstraint(AttributeField field, View view, CaseConstraint caseConstraint, WhenConstraint wc, String fieldPath,
			String operator, String andedCase) {
		String ruleString = "";
		// prerequisite constraint

		String booleanStatement = "";
		if (wc.getValues() != null) {

			String caseStr = "";
			if (!caseConstraint.isCaseSensitive()) {
				caseStr = ".toUpperCase()";
			}
			for (int i = 0; i < wc.getValues().size(); i++) {
				if (operator.isEmpty()) {
					// has_value case
					if (wc.getValues().get(i) instanceof String
							&& ((String) wc.getValues().get(i)).equalsIgnoreCase("false")) {
						booleanStatement = booleanStatement + "!(coerceValue('" + fieldPath + "'))";
					}
					else {
						booleanStatement = booleanStatement + "(coerceValue('" + fieldPath + "'))";
					}
				}
				else {
					// everything else
					booleanStatement = booleanStatement + "(coerceValue('" + fieldPath + "')" + caseStr + " "
							+ operator + " \"" + wc.getValues().get(i) + "\"" + caseStr + ")";
				}
				if ((i + 1) != wc.getValues().size()) {
					booleanStatement = booleanStatement + " || ";
				}
			}

		}

		if (andedCase != null) {
			booleanStatement = "(" + booleanStatement + ") && (" + andedCase + ")";
		}

		if (wc.getConstraint() != null && StringUtils.isNotEmpty(booleanStatement)) {
			ruleString = createRule(field, wc.getConstraint(), booleanStatement, view);
		}

		if (StringUtils.isNotEmpty(ruleString)) {
			addScriptToPage(view, field, ruleString);
		}
	}
	


	/**
	 * Adds the script to the view to execute on a jQuery document ready event.
	 * 
	 * @param view
	 * @param script
	 */
	public static void addScriptToPage(View view, AttributeField field, String script) {
        String prefixScript = "";
        
        if (field.getOnDocumentReadyScript() != null) {
            prefixScript = field.getOnDocumentReadyScript();
        }
        field.setOnDocumentReadyScript(prefixScript + "\n" + "runValidationScript(function(){" + script + "});");
	}
	
	/**
	 * Determines which fields are being evaluated in a boolean statement, so handlers can be
	 * attached to them if needed, returns these names in a list.
	 * 
	 * @param statement
	 * @return
	 */
	private static List<String> parseOutFields(String statement){
	    List<String> fieldNames = new ArrayList<String>();
	    String[] splits = StringUtils.splitByWholeSeparator(statement, "coerceValue('");
	    for(String s: splits){
	        String fieldName = StringUtils.substringBefore(s, "'");
	        fieldNames.add(fieldName);
	    }
	    return fieldNames;
	}

	/**
	 * This method takes in a constraint to apply only when the passed in
	 * booleanStatement is valid. The method will create the necessary addMethod
	 * and addRule jquery validator calls for the rule to be applied to the
	 * field when the statement passed in evaluates to true during runtime and
	 * this field is being validated. Note the use of custom methods for min/max
	 * length/value.
	 * 
	 * @param applyToField
	 *            the field to apply the generated methods and rules to
	 * @param constraint
	 *            the constraint to be applied when the booleanStatement
	 *            evaluates to true during validation
	 * @param booleanStatement
	 *            the booleanstatement in js - should return true when the
	 *            validation rule should be applied
	 * @param view
	 * @return
	 */
	@SuppressWarnings("boxing")
	private static String createRule(AttributeField field, Constraint constraint, String booleanStatement, View view) {
		String rule = "";
		int constraintCount = 0;
		if (constraint instanceof BaseConstraint && ((BaseConstraint) constraint).getApplyClientSide()) {
			if (constraint instanceof SimpleConstraint) {
				if (((SimpleConstraint) constraint).getRequired()) {
					rule = rule + "required: function(element){\nreturn (" + booleanStatement + ");}";
					//special requiredness indicator handling
					String showIndicatorScript = "";
					for(String checkedField: parseOutFields(booleanStatement)){
					    showIndicatorScript = showIndicatorScript + 
					        "setupShowReqIndicatorCheck('"+ checkedField +"', '" + field.getBindingInfo().getBindingPath() + "', " + "function(){\nreturn (" + booleanStatement + ");});\n";
					}
					addScriptToPage(view, field, showIndicatorScript);
					
					constraintCount++;
				}
				if (((SimpleConstraint) constraint).getMinLength() != null) {
					if (constraintCount > 0) {
						rule = rule + ",\n";
					}
					rule = rule + "minLengthConditional: [" + ((SimpleConstraint) constraint).getMinLength()
							+ ", function(){return " + booleanStatement + ";}]";
				}
				if (((SimpleConstraint) constraint).getMaxLength() != null) {
					if (constraintCount > 0) {
						rule = rule + ",\n";
					}
					rule = rule + "maxLengthConditional: [" + ((SimpleConstraint) constraint).getMaxLength()
							+ ", function(){return " + booleanStatement + ";}]";
				}

				if (((SimpleConstraint) constraint).getExclusiveMin() != null) {
					if (constraintCount > 0) {
						rule = rule + ",\n";
					}
					rule = rule + "minExclusive: [" + ((SimpleConstraint) constraint).getExclusiveMin()
							+ ", function(){return " + booleanStatement + ";}]";
				}

				if (((SimpleConstraint) constraint).getInclusiveMax() != null) {
					if (constraintCount > 0) {
						rule = rule + ",\n";
					}
					rule = rule + "maxInclusive: [" + ((SimpleConstraint) constraint).getInclusiveMax()
							+ ", function(){return " + booleanStatement + ";}]";
				}

				rule = "jq('[name=\"" + field.getBindingInfo().getBindingPath() + "\"]').rules(\"add\", {" + rule + "\n});";
			}
			else if (constraint instanceof ValidCharactersConstraint) {
				String regexMethod = "";
				String methodName = "";
				if(StringUtils.isNotEmpty(((ValidCharactersConstraint)constraint).getValue())) {
					regexMethod = ClientValidationUtils.getRegexMethod(field, (ValidCharactersConstraint) constraint) + "\n";
					methodName = "validChar-" + field.getBindingInfo().getBindingPath() + methodKey;
					methodKey++;
				}
				else {
					if(StringUtils.isNotEmpty(((ValidCharactersConstraint)constraint).getLabelKey())){
						methodName = ((ValidCharactersConstraint)constraint).getLabelKey();
					}
				}
				if (StringUtils.isNotEmpty(methodName)) {
					rule = regexMethod + "jq('[name=\"" + field.getBindingInfo().getBindingPath() + "\"]').rules(\"add\", {\n\"" + methodName
							+ "\" : function(element){return (" + booleanStatement + ");}\n});";
				}
			}
			else if (constraint instanceof PrerequisiteConstraint) {
				processPrerequisiteConstraint(field, (PrerequisiteConstraint) constraint, view, booleanStatement);
			}
			else if (constraint instanceof CaseConstraint) {
				processCaseConstraint(field, view, (CaseConstraint) constraint, booleanStatement);
			}
			else if (constraint instanceof MustOccurConstraint) {
				processMustOccurConstraint(field, view, (MustOccurConstraint) constraint, booleanStatement);
			}
		}
		return rule;
	}
	


	/**
	 * This method is a simpler version of processPrerequisiteConstraint
	 * 
	 * @see AttributeField#processPrerequisiteConstraint(PrerequisiteConstraint,
	 *      View, String)
	 * @param constraint
	 * @param view
	 */
	public static void processPrerequisiteConstraint(AttributeField field, PrerequisiteConstraint constraint, View view) {
		processPrerequisiteConstraint(field, constraint, view, "true");
	}

	/**
	 * This method processes a Prerequisite constraint that should be applied
	 * when the booleanStatement passed in evaluates to true.
	 * 
	 * @param constraint
	 *            prerequisiteConstraint
	 * @param view
	 * @param booleanStatement
	 *            the booleanstatement in js - should return true when the
	 *            validation rule should be applied
	 */
	public static void processPrerequisiteConstraint(AttributeField field, PrerequisiteConstraint constraint, View view, String booleanStatement) {
		if (constraint != null && constraint.getApplyClientSide().booleanValue()) {
			addScriptToPage(view, field, getPrerequisiteStatement(field, view, constraint, booleanStatement)
					+ getPostrequisiteStatement(field, constraint, booleanStatement));
	        //special requiredness indicator handling
	        String showIndicatorScript = "setupShowReqIndicatorCheck('"+ field.getBindingInfo().getBindingPath() +"', '" + constraint.getAttributePath() + "', " + "function(){\nreturn (coerceValue('" + field.getBindingInfo().getBindingPath() + "') && " + booleanStatement + ");});\n";
	        addScriptToPage(view, field, showIndicatorScript);
		}
	}

	/**
	 * This method creates the script necessary for executing a prerequisite
	 * rule in which this field occurs after the field specified in the
	 * prerequisite rule - since it requires a specific set of UI logic. Builds
	 * an if statement containing an addMethod jquery validator call. Adds a
	 * "dependsOn" css class to this field for the field specified.
	 * 
	 * @param constraint
	 *            prerequisiteConstraint
	 * @param booleanStatement
	 *            the booleanstatement in js - should return true when the
	 *            validation rule should be applied
	 * @return
	 */
	private static String getPrerequisiteStatement(AttributeField field, View view, PrerequisiteConstraint constraint, String booleanStatement) {
		methodKey++;
		String message = "";
		if(StringUtils.isEmpty(constraint.getLabelKey())){
			message = configService.getPropertyString(VALIDATION_MSG_KEY_PREFIX + "prerequisite");
		}
		else{
			message = generateMessageFromLabelKey(constraint.getLabelKey());
		}
		if(StringUtils.isEmpty(message)){
			message = "prerequisite - No message";
		}
		else{
			AttributeField requiredField = view.getViewIndex().getAttributeFieldByPath(constraint.getAttributePath());
			if(requiredField != null && StringUtils.isNotEmpty(requiredField.getLabel())){
				message = MessageFormat.format(message, requiredField.getLabel());
			}
			else{
				message = MessageFormat.format(message, configService.getPropertyString(GENERIC_FIELD_MSG_KEY));
			}
		}
		
		// field occurs before case
		String dependsClass = "dependsOn-" + constraint.getAttributePath();
		String methodName = "prConstraint-" + field.getBindingInfo().getBindingPath()+ methodKey;
		String addClass = "jq('[name=\""+ field.getBindingInfo().getBindingPath() + "\"]').addClass('" + dependsClass + "');\n" +
			"jq('[name=\""+ field.getBindingInfo().getBindingPath() + "\"]').addClass('" + methodName + "');\n";
		String method = "\njQuery.validator.addMethod(\""+ methodName +"\", function(value, element) {\n" +
			" if(" + booleanStatement + "){ return (this.optional(element) || (coerceValue('" + constraint.getAttributePath() + "')));}else{return true;} " +
			"}, \"" + message + "\");";
		
		String ifStatement = "if(occursBefore('" + constraint.getAttributePath() + "','" + field.getBindingInfo().getBindingPath() + 
		"')){" + addClass + method + "}";
		return ifStatement;
	}

	/**
	 * This method creates the script necessary for executing a prerequisite
	 * rule in which this field occurs before the field specified in the
	 * prerequisite rule - since it requires a specific set of UI logic. Builds
	 * an if statement containing an addMethod jquery validator call.
	 * 
	 * @param constraint
	 *            prerequisiteConstraint
	 * @param booleanStatement
	 *            the booleanstatement in js - should return true when the
	 *            validation rule should be applied
	 * @return
	 */
	private static String getPostrequisiteStatement(AttributeField field, PrerequisiteConstraint constraint, String booleanStatement) {
		// field occurs after case
		String message = "";
		if(StringUtils.isEmpty(constraint.getLabelKey())){
			message = configService.getPropertyString(VALIDATION_MSG_KEY_PREFIX + "postrequisite");
		}
		else{
			message = generateMessageFromLabelKey(constraint.getLabelKey());
		}
		
		if(StringUtils.isEmpty(constraint.getLabelKey())){
			if(StringUtils.isNotEmpty(field.getLabel())){
				message = MessageFormat.format(message, field.getLabel());
			}
			else{
				message = MessageFormat.format(message, configService.getPropertyString(GENERIC_FIELD_MSG_KEY));
			}
			
		}
		
		String function = "function(element){\n" +
			"return (coerceValue('"+ field.getBindingInfo().getBindingPath() + "') && " + booleanStatement + ");}";
		String postStatement = "\nelse if(occursBefore('" + field.getBindingInfo().getBindingPath() + "','" + constraint.getAttributePath() + 
			"')){\njq('[name=\""+ constraint.getAttributePath() + 
			"\"]').rules(\"add\", { required: \n" + function 
			+ ", \nmessages: {\nrequired: \""+ message +"\"}});}\n";
		
		return postStatement;

	}

	/**
	 * This method processes the MustOccurConstraint. The constraint is only
	 * applied when the booleanStatement evaluates to true during validation.
	 * This method creates the addMethod and add rule calls for the jquery
	 * validation plugin necessary for applying this constraint to this field.
	 * 
	 * @param view
	 * @param mc
	 * @param booleanStatement
	 *            the booleanstatement in js - should return true when the
	 *            validation rule should be applied
	 */
	public static void processMustOccurConstraint(AttributeField field, View view, MustOccurConstraint mc, String booleanStatement) {
		methodKey++;
		mustOccursPathNames = new ArrayList<List<String>>();
		// TODO make this show the fields its requiring
		String methodName = "moConstraint-" + field.getBindingInfo().getBindingPath() + methodKey;
		String method = "\njQuery.validator.addMethod(\""+ methodName +"\", function(value, element) {\n" +
		" if(" + booleanStatement + "){return (this.optional(element) || ("+ getMustOccurStatement(field, mc) + "));}else{return true;}" +
		"}, \"" + getMustOccursMessage(view, mc) +"\");";
		String rule = method + "jq('[name=\""+ field.getBindingInfo().getBindingPath() + "\"]').rules(\"add\", {\n\"" + methodName + "\": function(element){return (" + booleanStatement + ");}\n});";
		addScriptToPage(view, field, rule);
	}

	/**
	 * This method takes in a MustOccurConstraint and returns the statement used
	 * in determining if the must occurs constraint has been satisfied when this
	 * field is validated. Note the use of the mustOccurCheck method. Nested
	 * mustOccurConstraints are ored against the result of the mustOccurCheck by
	 * calling this method recursively.
	 * 
	 * @param constraint
	 * @return
	 */
	@SuppressWarnings("boxing")
	private static String getMustOccurStatement(AttributeField field, MustOccurConstraint constraint) {
		String statement = "";
		List<String> attributePaths = new ArrayList<String>();
		if (constraint != null && constraint.getApplyClientSide()) {
			String array = "[";
			if (constraint.getPrerequisiteConstraints() != null) {
				for (int i = 0; i < constraint.getPrerequisiteConstraints().size(); i++) {
					field.getControl().addStyleClass("dependsOn-"
							+ constraint.getPrerequisiteConstraints().get(i).getAttributePath());
					array = array + "'" + constraint.getPrerequisiteConstraints().get(i).getAttributePath() + "'";
					attributePaths.add(constraint.getPrerequisiteConstraints().get(i).getAttributePath());
					if (i + 1 != constraint.getPrerequisiteConstraints().size()) {
						array = array + ",";
					}

				}
			}
			array = array + "]";
			statement = "mustOccurTotal(" + array + ", " + constraint.getMin() + ", " + constraint.getMax() + ")";
			//add min to string list
			if(constraint.getMin()!=null){
				attributePaths.add(constraint.getMin().toString());
			}
			else{
				attributePaths.add(null);
			}
			//add max to string list
			if(constraint.getMax()!=null){
				attributePaths.add(constraint.getMax().toString());
			}
			else{
				attributePaths.add(null);
			}
			
			mustOccursPathNames.add(attributePaths);
			if(StringUtils.isEmpty(statement)){
				statement = "0";
			}
			if (constraint.getMustOccurConstraints() != null) {
				for (MustOccurConstraint mc : constraint.getMustOccurConstraints()) {
					statement = "mustOccurCheck(" + statement + " + " + getMustOccurStatement(field, mc) +
						", " + constraint.getMin() + ", " + constraint.getMax() + ")";
				}
			}
			else{
				statement = "mustOccurCheck(" + statement +
				", " + constraint.getMin() + ", " + constraint.getMax() + ")";
			}
		}
		return statement;
	}

	
	/**
	 * Generates a message for the must occur constraint (if no label key is specified).  
	 * This message is most accurate when must occurs is a single
	 * or double level constraint.  Beyond that, the message will still be accurate but may be confusing for
	 * the user - this auto-generated message however will work in MOST use cases.
	 * 
	 * @param view
	 * @return
	 */
	private static String getMustOccursMessage(View view, MustOccurConstraint constraint){
		String message = "";
		if(StringUtils.isNotEmpty(constraint.getLabelKey())){
			message = generateMessageFromLabelKey(constraint.getLabelKey());
		}
		else{
			String and = configService.getPropertyString(AND_MSG_KEY);
			String all = configService.getPropertyString(ALL_MSG_KEY);
			String atMost = configService.getPropertyString(ATMOST_MSG_KEY);
			String genericLabel = configService.getPropertyString(GENERIC_FIELD_MSG_KEY);
			String mustOccursMsg = configService.getPropertyString(VALIDATION_MSG_KEY_PREFIX + MUSTOCCURS_MSG_KEY);
			//String postfix = configService.getPropertyString(VALIDATION_MSG_KEY_PREFIX + MUSTOCCURS_POST_MSG_KEY);
			String statement="";
			for(int i=0; i< mustOccursPathNames.size(); i++){
				String andedString = "";
				
				List<String> paths = mustOccursPathNames.get(i);
				if(!paths.isEmpty()){
					//note that the last 2 strings are min and max and rest are attribute paths
					String min = paths.get(paths.size()-2);
					String max = paths.get(paths.size()-1);
					for(int j=0; j<paths.size()-2;j++){
						AttributeField field = view.getViewIndex().getAttributeFieldByPath(paths.get(j).trim());
						String label = genericLabel;
						if(field != null && StringUtils.isNotEmpty(field.getLabel())){
							label = field.getLabel();
						}
						if(min.equals(max)){
							if(j==0){
								andedString = label;
							}
							else if(j==paths.size()-3){
								andedString = andedString + " " + and + " " + label;
							}
							else{
								andedString = andedString + ", " + label;
							}
						}
						else{
							andedString = andedString + "<li>" + label + "</li>";
						}
					}
					if(min.equals(max)){
						andedString = "<li>" + andedString + "</li>";
					}
					andedString="<ul>" + andedString + "</ul>";
				
					if(StringUtils.isNotEmpty(min) && StringUtils.isNotEmpty(max) && !min.equals(max)){
						andedString = MessageFormat.format(mustOccursMsg, min + "-" + max) + "<br/>" +andedString;
					}
					else if(StringUtils.isNotEmpty(min) && StringUtils.isNotEmpty(max) && min.equals(max) && i==0){
						andedString = MessageFormat.format(mustOccursMsg, all) + "<br/>" +andedString;
					}
					else if(StringUtils.isNotEmpty(min) && StringUtils.isNotEmpty(max) && min.equals(max) && i!=0){
						//leave andedString as is
					}
					else if(StringUtils.isNotEmpty(min)){
						andedString = MessageFormat.format(mustOccursMsg, min) + "<br/>" +andedString;
					}
					else if(StringUtils.isNotEmpty(max)){
						andedString = MessageFormat.format(mustOccursMsg, atMost + " " + max) + "<br/>" +andedString;
					}
				}
				if(StringUtils.isNotEmpty(andedString)){
					if(i==0){
						statement = andedString;
					}
					else{
						statement = statement + andedString;
					}
				}
			}
			if(StringUtils.isNotEmpty(statement)){
				message = statement;
			}
		}
		
		return message;
	}

	/**
	 * This method processes all the constraints on the AttributeField passed in and adds all the necessary
	 * jQuery and js required (validator's rules, methods, and messages) to the View's onDocumentReady call.
	 * The result is js that will validate all the constraints contained on an AttributeField during user interaction
	 * with the field using the jQuery validation plugin and custom code.
	 * 
	 * @param attributeField
	 */
	@SuppressWarnings("boxing")
	public static void processAndApplyConstraints(AttributeField field, View view) {
		methodKey = 0;
		if ((field.getRequired() != null) && (field.getRequired().booleanValue())) {
			field.getControl().addStyleClass("required");
		}

		if (field.getExclusiveMin() != null) {
			if (field.getControl() instanceof TextControl && ((TextControl) field.getControl()).getDatePicker() != null) {
				((TextControl) field.getControl()).getDatePicker().getComponentOptions().put("minDate", field.getExclusiveMin());
			}
			else{
				String rule = "jq('[name=\""+ field.getBindingInfo().getBindingPath() + "\"]').rules(\"add\", {\n minExclusive: ["+ field.getExclusiveMin() + "]});";
				addScriptToPage(view, field, rule);
			}
		}

		if (field.getInclusiveMax() != null) {
			if (field.getControl() instanceof TextControl && ((TextControl) field.getControl()).getDatePicker() != null) {
				((TextControl) field.getControl()).getDatePicker().getComponentOptions().put("maxDate", field.getInclusiveMax());
			}
			else{
				String rule = "jq('[name=\""+ field.getBindingInfo().getBindingPath() + "\"]').rules(\"add\", {\n maxInclusive: ["+ field.getInclusiveMax() + "]});";
				addScriptToPage(view, field, rule);
			}
		}

		if (field.getValidCharactersConstraint() != null && field.getValidCharactersConstraint().getApplyClientSide()) {
			if(StringUtils.isNotEmpty(field.getValidCharactersConstraint().getValue())) {
				// set regex value takes precedence
				addScriptToPage(view, field, ClientValidationUtils.getRegexMethod(field, field.getValidCharactersConstraint()));
				field.getControl().addStyleClass("validChar-" + field.getBindingInfo().getBindingPath()+ methodKey);
				methodKey++;
			}
			else {
				//blindly assume that if there is no regex value defined that there must be a method by this name
				if(StringUtils.isNotEmpty(field.getValidCharactersConstraint().getLabelKey())){
					field.getControl().addStyleClass(field.getValidCharactersConstraint().getLabelKey());
				}
			}
		}

		if (field.getCaseConstraint() != null && field.getCaseConstraint().getApplyClientSide()) {
			processCaseConstraint(field, view, field.getCaseConstraint(), null);
		}

		if (field.getDependencyConstraints() != null) {
			for (PrerequisiteConstraint prc : field.getDependencyConstraints()) {
				processPrerequisiteConstraint(field, prc, view);
			}
		}

		if (field.getMustOccurConstraints() != null) {
			for (MustOccurConstraint mc : field.getMustOccurConstraints()) {
				processMustOccurConstraint(field, view, mc, "true");
			}
		}
		
	}

}
