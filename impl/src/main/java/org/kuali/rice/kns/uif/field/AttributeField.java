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
package org.kuali.rice.kns.uif.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.datadictionary.validation.constraint.BaseConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.Constraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.WhenConstraint;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.uif.BindingInfo;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.control.CheckboxControl;
import org.kuali.rice.kns.uif.control.Control;
import org.kuali.rice.kns.uif.control.MultiValueControlBase;
import org.kuali.rice.kns.uif.control.RadioGroupControl;
import org.kuali.rice.kns.uif.control.SelectControl;
import org.kuali.rice.kns.uif.control.TextAreaControl;
import org.kuali.rice.kns.uif.control.TextControl;
import org.kuali.rice.kns.uif.widget.DatePicker;
import org.kuali.rice.kns.uif.widget.Inquiry;
import org.kuali.rice.kns.uif.widget.QuickFinder;
import org.kuali.rice.kns.web.format.Formatter;

/**
 * Field that encapsulates data input/output captured by an attribute within the
 * application
 * 
 * <p>
 * The <code>AttributField</code> provides the majority of the data input/output
 * for the screen. Through these fields the model can be displayed and updated.
 * For data input, the field contains a <code>Control</code> instance will
 * render an HTML control element(s). The attribute field also contains a
 * <code>LabelField</code>, summary, and widgets such as a quickfinder (for
 * looking up values) and inquiry (for getting more information on the value).
 * <code>AttributeField</code> instances can have associated messages (errors)
 * due to invalid input or business rule failures. Security can also be
 * configured to restrict who may view the fields value.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeField extends FieldBase implements DataBinding {
	private static final long serialVersionUID = -3703656713706343840L;

	// value props
	private String defaultValue;

	//Constraint variables
	protected String customValidatorClass;
	protected ValidCharactersConstraint validCharactersConstraint;
	protected CaseConstraint caseConstraint;
	protected List<PrerequisiteConstraint> dependencyConstraints;
	protected List<MustOccurConstraint> mustOccurConstraints;
	protected SimpleConstraint simpleConstraint;
	//used to give validation methods unique signatures
	private static int methodKey = 0;
	//list used to temporarily store mustOccurs field names for the error message
	private List<String> mustOccurFieldNames;
	
	private Formatter formatter;
	private KeyValuesFinder optionsFinder;

	// binding
	private String propertyName;
	private BindingInfo bindingInfo;

	private String dictionaryAttributeName;
	private String dictionaryObjectEntry;

	// display props
	private Control control;

	private String errorMessagePlacement;
	private ErrorsField errorsField;

	// messages
	private String summary;
	private String constraint;

	private String description;

	private AttributeSecurity attributeSecurity;
	private MessageField summaryMessageField;
	private MessageField constraintMessageField;

	// widgets
	private Inquiry fieldInquiry;
	private QuickFinder fieldLookup;

	public AttributeField() {
		super();
		simpleConstraint = new SimpleConstraint();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set defaults for binding</li>
	 * <li>Set the control id if blank to the field id</li>
	 * <li>Default the model path if not set</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		// update ids so they all match the attribute
		String id = getId();
		if (getControl() != null) {
			getControl().setId(id);
		}
		if (getErrorsField() != null) {
			getErrorsField().setId(id + UifConstants.IdSuffixes.ERRORS);
		}
		if (getLabelField() != null) {
			getLabelField().setId(id + UifConstants.IdSuffixes.LABEL);
		}
		if (getSummaryMessageField() != null) {
			getSummaryMessageField().setId(id + UifConstants.IdSuffixes.SUMMARY);
		}
		if (getConstraintMessageField() != null) {
			getConstraintMessageField().setId(id + UifConstants.IdSuffixes.CONSTRAINT);
		}
		if (getFieldLookup() != null) {
			getFieldLookup().setId(id + UifConstants.IdSuffixes.QUICK_FINDER);
		}
		setId(id + UifConstants.IdSuffixes.ATTRIBUTE);

		if (bindingInfo != null) {
			bindingInfo.setDefaults(view, getPropertyName());
		}

		if (StringUtils.isNotBlank(summary)) {
			summaryMessageField.setMessageText(summary);
		}

		if (StringUtils.isNotBlank(constraint)) {
			constraintMessageField.setMessageText(constraint);
		}

		// TODO: remove later, this should be done within the service lifecycle
		if ((optionsFinder != null) && (control != null) && control instanceof MultiValueControlBase) {
			MultiValueControlBase multiValueControl = (MultiValueControlBase) control;
			if ((multiValueControl.getOptions() == null) || multiValueControl.getOptions().isEmpty()) {
				multiValueControl.setOptions(optionsFinder.getKeyValues());
			}
		}
	}

	/**
	 * Defaults the properties of the <code>AttributeField</code> to the
	 * corresponding properties of its <code>AttributeDefinition</code>
	 * retrieved from the dictionary (if such an entry exists). If the field
	 * already contains a value for a property, the definitions value is not
	 * used.
	 * 
	 * @param attributeDefinition
	 *            - AttributeDefinition instance the property values should be
	 *            copied from
	 */
	public void copyFromAttributeDefinition(AttributeDefinition attributeDefinition) {
		// label
		if (StringUtils.isEmpty(getLabel())) {
			setLabel(attributeDefinition.getLabel());
		}

		// short label
		if (StringUtils.isEmpty(getShortLabel())) {
			setShortLabel(attributeDefinition.getShortLabel());
		}

		// max length
		if (getMaxLength() == null) {
			setMaxLength(attributeDefinition.getMaxLength());
		}

		// max length
		if (getMinLength() == null) {
			setMinLength(attributeDefinition.getMinLength());
		}

		// valid characters
		if (getValidCharactersConstraint() == null) {
			setValidCharactersConstraint(attributeDefinition.getValidCharactersConstraint());
		}
		
		if (getCaseConstraint() == null){
			setCaseConstraint(attributeDefinition.getCaseConstraint());
		}
		
		if (getDependencyConstraints() == null){
			setDependencyConstraints(attributeDefinition.getPrerequisiteConstraints());
		}
		
		if (getMustOccurConstraints() == null){
			setMustOccurConstraints(attributeDefinition.getMustOccurConstraints());
		}

		// required
		if (getRequired() == null) {
			setRequired(attributeDefinition.isRequired());
		}

		// control
		if (getControl() == null) {
			setControl(attributeDefinition.getControlField());
		}

		// summary
		if (StringUtils.isEmpty(getSummary())) {
			setSummary(attributeDefinition.getSummary());
			getSummaryMessageField().setMessageText(attributeDefinition.getSummary());
		}

		// description
		if (StringUtils.isEmpty(getDescription())) {
			setDescription(attributeDefinition.getDescription());
		}

		// security
		if (getAttributeSecurity() == null) {
			setAttributeSecurity(attributeDefinition.getAttributeSecurity());
		}

		// constraint
		if (StringUtils.isEmpty(getConstraint())) {
			setConstraint(attributeDefinition.getConstraint());
			getConstraintMessageField().setMessageText(attributeDefinition.getConstraint());
		}

	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(control);
		components.add(errorsField);
		components.add(fieldLookup);
		components.add(fieldInquiry);

		return components;
	}

	/**
	 * @see org.kuali.rice.kns.uif.DataBinding#getPropertyName()
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * Setter for the component's property name
	 * 
	 * @param propertyName
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * Default value for the model property the field points to
	 * 
	 * <p>
	 * When a new <code>View</code> instance is requested, the corresponding
	 * model will be newly created. During this initialization process the value
	 * for the model property will be set to the given default value (if set)
	 * </p>
	 * 
	 * @return String default value
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Setter for the fields default value
	 * 
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * <code>Formatter</code> instance that should be used when displaying and
	 * accepting the field's value in the user interface
	 * 
	 * <p>
	 * Formatters can provide conversion between datatypes in addition to
	 * special string formatting such as currency display
	 * </p>
	 * 
	 * @return Formatter instance
	 * @see org.kuali.rice.kns.web.format.Formatter
	 */
	public Formatter getFormatter() {
		return this.formatter;
	}

	/**
	 * Setter for the field's formatter
	 * 
	 * @param formatter
	 */
	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	/**
	 * @see org.kuali.rice.kns.uif.DataBinding#getBindingInfo()
	 */
	public BindingInfo getBindingInfo() {
		return this.bindingInfo;
	}

	/**
	 * Setter for the field's binding info
	 * 
	 * @param bindingInfo
	 */
	public void setBindingInfo(BindingInfo bindingInfo) {
		this.bindingInfo = bindingInfo;
	}

	/**
	 * <code>Control</code> instance that should be used to input data for the
	 * field
	 * 
	 * <p>
	 * When the field is editable, the control will be rendered so the user can
	 * input a value(s). Controls typically are part of a Form and render
	 * standard HTML control elements such as text input, select, and checkbox
	 * </p>
	 * 
	 * @return Control instance
	 */
	public Control getControl() {
		return this.control;
	}

	/**
	 * Setter for the field's control
	 * 
	 * @param control
	 */
	public void setControl(Control control) {
		this.control = control;
	}

	public String getErrorMessagePlacement() {
		return this.errorMessagePlacement;
	}

	public void setErrorMessagePlacement(String errorMessagePlacement) {
		this.errorMessagePlacement = errorMessagePlacement;
	}

	/**
	 * Field that contains the messages (errors) for the attribute field. The
	 * <code>ErrorsField</code> holds configuration on associated messages along
	 * with information on rendering the messages in the user interface
	 * 
	 * @return ErrorsField instance
	 */
	public ErrorsField getErrorsField() {
		return this.errorsField;
	}

	/**
	 * Setter for the attribute field's errors field
	 * 
	 * @param errorsField
	 */
	public void setErrorsField(ErrorsField errorsField) {
		this.errorsField = errorsField;
	}

	/**
	 * Name of the attribute within the data dictionary the attribute field is
	 * associated with
	 * 
	 * <p>
	 * During the initialize phase for the <code>View</code>, properties for
	 * attribute fields are defaulted from a corresponding
	 * <code>AttributeDefinition</code> in the data dictionary. Based on the
	 * propertyName and parent object class the framework attempts will
	 * determine the attribute definition that is associated with the field and
	 * set this property. However this property can also be set in the fields
	 * configuration to use another dictionary attribute.
	 * </p>
	 * 
	 * <p>
	 * The attribute name is used along with the dictionary object entry to find
	 * the <code>AttributeDefinition</code>
	 * </p>
	 * 
	 * @return String attribute name
	 */
	public String getDictionaryAttributeName() {
		return this.dictionaryAttributeName;
	}

	/**
	 * Setter for the dictionary attribute name
	 * 
	 * @param dictionaryAttributeName
	 */
	public void setDictionaryAttributeName(String dictionaryAttributeName) {
		this.dictionaryAttributeName = dictionaryAttributeName;
	}

	/**
	 * Object entry name in the data dictionary the associated attribute is
	 * apart of
	 * 
	 * <p>
	 * During the initialize phase for the <code>View</code>, properties for
	 * attribute fields are defaulted from a corresponding
	 * <code>AttributeDefinition</code> in the data dictionary. Based on the
	 * parent object class the framework will determine the object entry for the
	 * associated attribute. However the object entry can be set in the field's
	 * configuration to use another object entry for the attribute
	 * </p>
	 * 
	 * <p>
	 * The attribute name is used along with the dictionary object entry to find
	 * the <code>AttributeDefinition</code>
	 * </p>
	 * 
	 * @return
	 */
	public String getDictionaryObjectEntry() {
		return this.dictionaryObjectEntry;
	}

	/**
	 * Setter for the dictionary object entry
	 * 
	 * @param dictionaryObjectEntry
	 */
	public void setDictionaryObjectEntry(String dictionaryObjectEntry) {
		this.dictionaryObjectEntry = dictionaryObjectEntry;
	}

	/**
	 * Instance of <code>KeyValluesFinder</code> that should be invoked to
	 * provide a List of values the field can have. Generally used to provide
	 * the options for a multi-value control or to validate the submitted field
	 * value
	 * 
	 * @return KeyValuesFinder instance
	 */
	public KeyValuesFinder getOptionsFinder() {
		return this.optionsFinder;
	}

	/**
	 * Setter for the field's KeyValuesFinder instance
	 * 
	 * @param optionsFinder
	 */
	public void setOptionsFinder(KeyValuesFinder optionsFinder) {
		this.optionsFinder = optionsFinder;
	}



	/**
	 * Brief statement of the field (attribute) purpose. Used to display helpful
	 * information to the user on the form
	 * 
	 * @return String summary message
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * Setter for the summary message
	 * 
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Full explanation of the field (attribute). Used in help contents
	 * 
	 * @return String description message
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Setter for the description message
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Holds security configuration for the attribute field. This triggers
	 * corresponding permission checks in KIM and can result in an update to the
	 * field state (such as read-only or hidden) and masking of the value
	 * 
	 * @return AttributeSecurity instance configured for field or Null if no
	 *         restrictions are defined
	 */
	public AttributeSecurity getAttributeSecurity() {
		return this.attributeSecurity;
	}

	/**
	 * Setter for the AttributeSecurity instance that defines restrictions for
	 * the field
	 * 
	 * @param attributeSecurity
	 */
	public void setAttributeSecurity(AttributeSecurity attributeSecurity) {
		this.attributeSecurity = attributeSecurity;
	}



	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getSupportsOnLoad()
	 */
	@Override
	public boolean getSupportsOnLoad() {
		return true;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	@Override
	public void performFinalize(View view, Object model) {
		super.performFinalize(view, model);

		if (this.getRequired()) {
			control.addStyleClass("required");
		}
		
		if (this.getExclusiveMin() != null){
			if(control instanceof TextControl && ((TextControl) control).getDatePicker() != null){
				((TextControl) control).getDatePicker().getComponentOptions().put("minDate", this.getExclusiveMin());
			}
			else{
				String rule = "$('[name=\""+ propertyName + "\"]').rules(\"add\", {\n minExclusive: ["+ this.getExclusiveMin() + "]});";
				addScriptToView(view, rule);
			}
		}
		
		if (this.getInclusiveMax() != null){
			if(control instanceof TextControl && ((TextControl) control).getDatePicker() != null){
				((TextControl) control).getDatePicker().getComponentOptions().put("maxDate", this.getInclusiveMax());
			}
			else{
				String rule = "$('[name=\""+ propertyName + "\"]').rules(\"add\", {\n maxInclusive: ["+ this.getInclusiveMax() + "]});";
				addScriptToView(view, rule);
			}
		}
		
		if(validCharactersConstraint != null && validCharactersConstraint.getApplyClientSide()){
			if(validCharactersConstraint.getJsValue() != null){
				//set jsValue takes precedence
				this.addScriptToView(view, getRegexMethod(validCharactersConstraint));
				control.addStyleClass(validCharactersConstraint.getLabelKey());
			}
			else {
				// attempt to find key in the map of known supported
				// validCharacter methods
				String methodName = UifConstants.validCharactersMethods.get(validCharactersConstraint.getLabelKey());
				if (StringUtils.isNotEmpty(methodName)) {
					control.addStyleClass(methodName);
				}
			}
		}

		if (caseConstraint != null && caseConstraint.getApplyClientSide()) {
			processCaseConstraint(view, null);
		}
		
		if(dependencyConstraints != null){
			for(PrerequisiteConstraint prc: dependencyConstraints){
				processPrerequisiteConstraint(prc, view);
			}
		}
		
		if(mustOccurConstraints != null){
			for(MustOccurConstraint mc: mustOccurConstraints){
				processMustOccurConstraint(view, mc, "true");
			}
		}

		//Sets message 
		if (StringUtils.isNotBlank(summary)) {
			summaryMessageField.setMessageText(summary);
			summaryMessageField.setRender(true);
		}
		else {
			summaryMessageField.setRender(false);
		}

		//Sets constraints
		if (StringUtils.isNotBlank(constraint)) {
			constraintMessageField.setMessageText(constraint);
			summaryMessageField.setRender(true);
		}
		else {
			constraintMessageField.setRender(false);
		}

	}
	
	private String getRegexMethod(ValidCharactersConstraint validCharactersConstraint){
		//TODO instead of getLabelKey here it would get the actual message from somewhere for the error
		//TODO should it use the labelKey here?
		//TODO Does this message need to be prefixed by label name? - probably
		return "\njQuery.validator.addMethod(\""+ validCharactersConstraint.getLabelKey() +"\", function(value, element) {\n" +
			" return this.optional(element) || " + validCharactersConstraint.getJsValue() + ".test(value); " +
			"}, \"" + validCharactersConstraint.getLabelKey() + "\");";
	}
	
	private void processCaseConstraint(View view, String andedCase){		
		if(caseConstraint.getOperator() == null){
			caseConstraint.setOperator("equals");
		}
		
		String operator = "==";
		if(caseConstraint.getOperator().equalsIgnoreCase("not_equals")){
			operator = "!=";
		}
		//add more operator types here if more are supported later
		
		control.addStyleClass("dependsOn-" + caseConstraint.getFieldPath());
		
		if (caseConstraint.getWhenConstraint() != null && !caseConstraint.getWhenConstraint().isEmpty()) {
			for (WhenConstraint wc : caseConstraint.getWhenConstraint()) {
				processWhenConstraint(view, wc, caseConstraint.getFieldPath(), operator, andedCase);
			}
		}
	}
	
	private void processWhenConstraint(View view, WhenConstraint wc, String fieldPath, String operator, String andedCase){
		String ruleString = "";
		//prerequisite constraint
		
		String booleanStatement = "";
		//size 1 value list - not sure we need to support lists here
		if(wc.getValues() != null && wc.getValues().size() == 1){
			
			String caseStr = "";
			if(!caseConstraint.isCaseSensitive()){
				caseStr = ".toUpperCase()";
			}
			for(int i=0; i < wc.getValues().size(); i++){
				booleanStatement = booleanStatement + "(coerceValue('" + fieldPath +"')"+ caseStr +" "+ operator + " \"" + wc.getValues().get(i) + "\"" + caseStr +")";
				if((i + 1) != wc.getValues().size()){
					booleanStatement = booleanStatement + " || ";
				}
			}
			
		}
		
		if(andedCase != null){
			booleanStatement = "(" + booleanStatement + ") && (" + andedCase + ")";
		}
		
		if(wc.getConstraint() != null && StringUtils.isNotEmpty(booleanStatement)){
			ruleString = createRule(this.getPropertyName(), wc.getConstraint(), booleanStatement, view);
		}
		
		if(StringUtils.isNotEmpty(ruleString)){
			addScriptToView(view, ruleString);
		}
	}
	
	private void addScriptToView(View view, String script){
		String prefixScript = "";
		if(view.getOnDocumentReadyScript() != null){
			prefixScript = view.getOnDocumentReadyScript();
		}
		
		view.setOnDocumentReadyScript(prefixScript + "\n" + script);
	}
	
	@SuppressWarnings("boxing")
	private String createRule(String applyToField, Constraint constraint, String booleanStatement, View view){
		String rule = "";
		int constraintCount = 0;
		if(constraint instanceof BaseConstraint && ((BaseConstraint)constraint).getApplyClientSide()){
			if(constraint instanceof SimpleConstraint){
				if(((SimpleConstraint) constraint).getRequired()){
					rule = rule + "required: function(element){\nreturn (" + booleanStatement + ");}";
					constraintCount++;
				}
				if(((SimpleConstraint) constraint).getMinLength() != null){
					if(constraintCount > 0){
						rule = rule + ",\n";
					}
					rule = rule + "minLengthConditional: [" + ((SimpleConstraint) constraint).getMinLength() + ", function(){return " + booleanStatement + ";}]";
				}
				if(((SimpleConstraint) constraint).getMaxLength() != null){
					if(constraintCount > 0){
						rule = rule + ",\n";
					}
					rule = rule + "maxLengthConditional: [" + ((SimpleConstraint) constraint).getMaxLength() + ", function(){return "  + booleanStatement + ";}]";
				}
				
				if(((SimpleConstraint) constraint).getExclusiveMin() != null){
					if(constraintCount > 0){
						rule = rule + ",\n";
					}
					rule = rule + "minExclusive: [" + ((SimpleConstraint) constraint).getExclusiveMin() + ", function(){return "  + booleanStatement + ";}]";
				}
				
				if(((SimpleConstraint) constraint).getInclusiveMax() != null){
					if(constraintCount > 0){
						rule = rule + ",\n";
					}
					rule = rule + "maxInclusive: [" + ((SimpleConstraint) constraint).getInclusiveMax() + ", function(){return "  + booleanStatement + ";}]";
				}
				
				rule = "$('[name=\""+ applyToField + "\"]').rules(\"add\", {" + rule + "\n});";
			}
			else if(constraint instanceof ValidCharactersConstraint){
				String regexMethod = "";
				String methodName = ((BaseConstraint)constraint).getLabelKey();
				if(validCharactersConstraint.getJsValue() != null){
					regexMethod = getRegexMethod((ValidCharactersConstraint) constraint) + "\n";
				}
				else{
					methodName = UifConstants.validCharactersMethods.get(validCharactersConstraint.getLabelKey());
				}
				if(StringUtils.isNotEmpty(methodName)){
					rule = regexMethod + "$('[name=\""+ applyToField + "\"]').rules(\"add\", {\n" + methodName + ": function(element){return (" + booleanStatement + ");}\n});";
				}
			}
			else if(constraint instanceof PrerequisiteConstraint){
				processPrerequisiteConstraint((PrerequisiteConstraint)constraint, view, booleanStatement);
			}
			else if(constraint instanceof CaseConstraint){
				processCaseConstraint(view, booleanStatement);
			}
			else if(constraint instanceof MustOccurConstraint){
				processMustOccurConstraint(view, (MustOccurConstraint)constraint, booleanStatement);
			}
		}	
		return rule;
	}
	
	private void processPrerequisiteConstraint(PrerequisiteConstraint constraint, View view){
		processPrerequisiteConstraint(constraint, view, "true");
	}
	
	private void processPrerequisiteConstraint(PrerequisiteConstraint constraint, View view, String booleanStatement){
		if(constraint != null && constraint.getApplyClientSide()){
			this.addScriptToView(view, getPrerequisiteStatement(constraint, booleanStatement) + getPostrequisiteStatement(constraint, booleanStatement));
		}
	}
	
	
	private String getPrerequisiteStatement(PrerequisiteConstraint constraint, String booleanStatement){
		methodKey++;
		//field occurs before case
		String dependsClass = "dependsOn-" + constraint.getAttributePath();
		String methodName = "prConstraint" + methodKey;
		//TODO make it a unique methodName
		String addClass = "$('[name=\""+ propertyName + "\"]').addClass('" + dependsClass + "');\n" +
			"$('[name=\""+ propertyName + "\"]').addClass('" + methodName + "');\n";
		String method = "\njQuery.validator.addMethod(\""+ methodName +"\", function(value, element) {\n" +
			" if(" + booleanStatement + "){ return (this.optional(element) || (coerceValue('" + constraint.getAttributePath() + "')));}else{return true;} " +
			"}, \"This field requires " + constraint.getAttributePath() +"\");";
		
		String ifStatement = "if(occursBefore('" + constraint.getAttributePath() + "','" + propertyName + 
		"')){" + addClass + method + "}";
		return ifStatement;
	}
	
	private String getPostrequisiteStatement(PrerequisiteConstraint constraint, String booleanStatement){
		//field occurs after case
		String rule = "";
		String function = "function(element){\n" +
			"return (coerceValue('"+ this.getPropertyName() + "') && " + booleanStatement + ");}";
		String postStatement = "\nelse if(occursBefore('" + propertyName + "','" + constraint.getAttributePath() + 
			"')){\n$('[name=\""+ constraint.getAttributePath() + 
			"\"]').rules(\"add\", { required: \n" + function 
			+ ", \nmessages: {\nrequired: \"Required by field: "+ this.getLabel() +"\"}});}\n";
		
		return postStatement;
		
	}

	private void processMustOccurConstraint(View view, MustOccurConstraint mc, String booleanStatement){
		methodKey++;
		mustOccurFieldNames = new ArrayList<String>();
		//TODO make this show the fields its requiring
		String methodName = "moConstraint" + methodKey;
		String method = "\njQuery.validator.addMethod(\""+ methodName +"\", function(value, element) {\n" +
		" if(" + booleanStatement + "){return (this.optional(element) || ("+ getMustOccurStatement(mc) + "));}else{return true;}" +
		"}, \"This field requires something else" + "" +"\");";
		String rule = method + "$('[name=\""+ propertyName + "\"]').rules(\"add\", {\n" + methodName + ": function(element){return (" + booleanStatement + ");}\n});";
		addScriptToView(view, rule);
	}

	private String getMustOccurStatement(MustOccurConstraint constraint){
		String statement = "";
		if(constraint != null && constraint.getApplyClientSide()){
			String array = "[";
			if(constraint.getPrerequisiteConstraints() != null){
				for(int i = 0; i < constraint.getPrerequisiteConstraints().size(); i++){
					control.addStyleClass("dependsOn-" + constraint.getPrerequisiteConstraints().get(i).getAttributePath());
					array = array + "'" + constraint.getPrerequisiteConstraints().get(i).getAttributePath() + "'";
					if(i + 1 != constraint.getPrerequisiteConstraints().size()){
						array = array + ",";
					}
					
				}
			}
			array = array + "]";
			mustOccurFieldNames.add(array);
			statement = "mustOccurCheck(" + array +", " + constraint.getMin() + ", " + constraint.getMax() + ")";
			
			if(constraint.getMustOccurConstraints() != null){
				for(MustOccurConstraint mc: constraint.getMustOccurConstraints()){
					statement = statement + " || " + getMustOccurStatement(mc);
				}
			}
		}
		return statement;
	}

	/**
	 * Lookup finder widget for the field
	 * 
	 * <p>
	 * The quickfinder widget places a small icon next to the field that allows
	 * the user to bring up a search screen for finding valid field values. The
	 * <code>Widget</code> instance can be configured to point to a certain
	 * <code>LookupView</code>, or the framework will attempt to associate the
	 * field with a lookup based on its metadata (in particular its
	 * relationships in the model)
	 * </p>
	 * 
	 * @return QuickFinder lookup widget
	 */
	public QuickFinder getFieldLookup() {
		return this.fieldLookup;
	}

	/**
	 * Setter for the lookup widget
	 * 
	 * @param fieldLookup
	 */
	public void setFieldLookup(QuickFinder fieldLookup) {
		this.fieldLookup = fieldLookup;
	}

	/**
	 * Inquiry widget for the field
	 * 
	 * <p>
	 * The inquiry widget will render a link for the field value when read-only
	 * that points to the associated inquiry view for the field. The inquiry can
	 * be configured to point to a certain <code>InquiryView</code>, or the
	 * framework will attempt to associate the field with a inquiry based on its
	 * metadata (in particular its relationships in the model)
	 * </p>
	 * 
	 * @return Inquiry field inquiry
	 */
	public Inquiry getFieldInquiry() {
		return this.fieldInquiry;
	}

	/**
	 * Setter for the inquiry widget
	 * 
	 * @param fieldInquiry
	 */
	public void setFieldInquiry(Inquiry fieldInquiry) {
		this.fieldInquiry = fieldInquiry;
	}

	/**
	 * @return the summaryField
	 */
	public MessageField getSummaryMessageField() {
		return this.summaryMessageField;
	}

	/**
	 * Sets the summary message field. Developers can use the setSummary method which would set the summary text.
	 * 
	 * @param summary field to set
	 * @see setSummary
	 */
	public void setSummaryMessageField(MessageField summaryField) {
		this.summaryMessageField = summaryField;
	}

	/**
	 * Returns the contraint set on the field
	 * 
	 * @return the constraint
	 */
	public String getConstraint() {
		return this.constraint;
	}

	/**
	 * Sets the constraint text. This text will be displayed below the component.
	 *  
	 * @param constraint for this field
	 */
	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	/**
	 * Sets the constraint message field. Developers can use the setContraint method which would set the constraint text.
	 * 
	 * @param constraint field to set
	 * @see setContraint
	 */
	public void setConstraintMessageField(MessageField constraintMessageField) {
		this.constraintMessageField = constraintMessageField;
	}
	
	/**
	 * Returns the contraint message field. 
	 * 
	 * @return constraint Message Field
	 */
	public MessageField getConstraintMessageField() {
		return this.constraintMessageField;
	}

	/**
	 * Valid character constraint that defines regular expressions for the valid
	 * characters for this field
	 * 
	 * @return the validCharactersConstraint
	 */
	public ValidCharactersConstraint getValidCharactersConstraint() {
		return this.validCharactersConstraint;
	}

	/**
	 * @param validCharactersConstraint
	 *            the validCharactersConstraint to set
	 */
	public void setValidCharactersConstraint(ValidCharactersConstraint validCharactersConstraint) {
		this.validCharactersConstraint = validCharactersConstraint;
	}

	/**
	 * @return the caseConstraint
	 */
	public CaseConstraint getCaseConstraint() {
		return this.caseConstraint;
	}

	/**
	 * @param caseConstraint
	 *            the caseConstraint to set
	 */
	public void setCaseConstraint(CaseConstraint caseConstraint) {
		this.caseConstraint = caseConstraint;
	}

	/**
	 * @return the dependencyConstraints
	 */
	public List<PrerequisiteConstraint> getDependencyConstraints() {
		return this.dependencyConstraints;
	}

	/**
	 * @param dependencyConstraints the dependencyConstraints to set
	 */
	public void setDependencyConstraints(
			List<PrerequisiteConstraint> dependencyConstraints) {
		this.dependencyConstraints = dependencyConstraints;
	}

	/**
	 * @return the mustOccurConstraints
	 */
	public List<MustOccurConstraint> getMustOccurConstraints() {
		return this.mustOccurConstraints;
	}

	/**
	 * @param mustOccurConstraints the mustOccurConstraints to set
	 */
	public void setMustOccurConstraints(
			List<MustOccurConstraint> mustOccurConstraints) {
		this.mustOccurConstraints = mustOccurConstraints;
	}

	/**
	 * A simple constraint which store the values for required, min/max length, and min/max value
	 * @return the simpleConstraint
	 */
	public SimpleConstraint getSimpleConstraint() {
		return this.simpleConstraint;
	}

	/**
	 * When a simple constraint is set on this object ALL simple validation constraints set
	 * directly will be overridden - recommended to use this or the other gets/sets for defining
	 * simple constraints, not both
	 * @param simpleConstraint the simpleConstraint to set
	 */
	public void setSimpleConstraint(SimpleConstraint simpleConstraint) {
		this.simpleConstraint = simpleConstraint;
	}
	
	/**
	 * Maximum number of the characters the attribute value is allowed to have.
	 * Used to set the maxLength for supporting controls. Note this can be
	 * smaller or longer than the actual control size
	 * 
	 * @return Integer max length
	 */
	public Integer getMaxLength() {
		return simpleConstraint.getMaxLength();
	}

	/**
	 * Setter for attributes max length
	 * 
	 * @param maxLength
	 */
	public void setMaxLength(Integer maxLength) {
		simpleConstraint.setMaxLength(maxLength);
	}
	
	/**
	 * @return the minLength
	 */
	public Integer getMinLength() {
		return simpleConstraint.getMinLength();
	}

	/**
	 * @param minLength
	 *            the minLength to set
	 */
	public void setMinLength(Integer minLength) {
		simpleConstraint.setMinLength(minLength);
	}

	/**
	 * @return the required
	 */
	@Override
	public Boolean getRequired() {
		return simpleConstraint.getRequired();
	}

	/**
	 * @param required the required to set
	 */
	@Override
	public void setRequired(Boolean required) {
		simpleConstraint.setRequired(required);
	}
	
	/**
	 * The exclusiveMin element determines the minimum allowable value for data
	 * entry editing purposes. Value can be an integer or decimal value such as
	 * -.001 or 99.
	 */
	public String getExclusiveMin() {
		return simpleConstraint.getExclusiveMin();
	}

	/**
	 * @param minValue the minValue to set
	 */
	public void setExclusiveMin(String exclusiveMin) {
		simpleConstraint.setExclusiveMin(exclusiveMin);
	}

	/**
	 * The inclusiveMax element determines the maximum allowable value for data
	 * entry editing purposes. Value can be an integer or decimal value such as
	 * -.001 or 99.
	 * 
	 * JSTL: This field is mapped into the field named "exclusiveMax".
	 */
	public String getInclusiveMax() {
		return simpleConstraint.getInclusiveMax();
	}

	/**
	 * @param maxValue the maxValue to set
	 */
	public void setInclusiveMax(String inclusiveMax) {
		simpleConstraint.setInclusiveMax(inclusiveMax);
	}
	
}
