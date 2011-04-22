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
package org.kuali.rice.kns.uif.container;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.UifPropertyPaths;
import org.kuali.rice.kns.uif.UifConstants.ViewType;
import org.kuali.rice.kns.uif.control.HiddenControl;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.service.LookupViewHelperService;
import org.kuali.rice.kns.uif.util.LookupInquiryUtils;

/**
 * TODO delyea: Fill in javadocs here
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupView extends FormView {
	private static final long serialVersionUID = 716926008488403616L;

	private Class<?> dataObjectClassName;

	private Group criteriaGroup;
	private CollectionGroup resultsGroup;
	private Field resultsActionsField;
	private Field resultsReturnField;

	private List<Component> criteriaFields;
	private List<Component> resultFields;
	private List<String> defaultSortAttributeNames;
	protected boolean sortAscending = true;

	public LookupView() {
		super();
		setViewTypeName(ViewType.LOOKUP);
		setValidateDirty(false);
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the abstractTypeClasses map for the lookup object path</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		initializeGroups();
		if (getItems().isEmpty()) {
			setItems(Arrays.asList(getCriteriaGroup(), getResultsGroup()));
		}
		super.performInitialization(view);

        getAbstractTypeClasses().put(UifPropertyPaths.CRITERIA_FIELDS, getDataObjectClassName());
        if (StringUtils.isNotBlank(getDefaultBindingObjectPath())) {
            getAbstractTypeClasses().put(getDefaultBindingObjectPath(), getDataObjectClassName());
        }
	}

	protected void initializeGroups() {
		if ((getCriteriaGroup() != null) && (getCriteriaGroup().getItems().isEmpty())) {
			getCriteriaGroup().setItems(getCriteriaFields());
		}
		if (getResultsGroup() != null) {
			if ( (getResultsGroup().getItems().isEmpty()) && (getResultFields() != null) ) {
				getResultsGroup().setItems(getResultFields());
			}
			if (getResultsGroup().getCollectionObjectClass() == null) {
				getResultsGroup().setCollectionObjectClass(getDataObjectClassName());
			}
		}
	}

	/**
     * @see org.kuali.rice.kns.uif.container.ContainerBase#performApplyModel(org.kuali.rice.kns.uif.container.View, java.lang.Object)
     */
    @Override
    public void performApplyModel(View view, Object model) {
	    if (isRenderActionsFields()) {
	        ((List<Field>)getResultsGroup().getItems()).add(0, getResultsActionsField());
	    }
	    if (isRenderReturnFields()) {
	        ((List<Field>)getResultsGroup().getItems()).add(0, getResultsReturnField());
	    }
	    applyConditionalLogicForFieldDisplay();
	    super.performApplyModel(view, model);
    }

	public void applyConditionalLogicForFieldDisplay() {
	    LookupViewHelperService lookupViewHelperService = (LookupViewHelperService) getViewHelperService();
		Set<String> readOnlyFields = lookupViewHelperService.getConditionallyReadOnlyPropertyNames();
		Set<String> requiredFields = lookupViewHelperService.getConditionallyRequiredPropertyNames();
		Set<String> hiddenFields = lookupViewHelperService.getConditionallyHiddenPropertyNames();
		if ( (readOnlyFields != null && !readOnlyFields.isEmpty()) ||
			 (requiredFields != null && !requiredFields.isEmpty()) ||
			 (hiddenFields != null && !hiddenFields.isEmpty()) 
			) {
			for (Field field : getResultsGroup().getItems()) {
				if (AttributeField.class.isAssignableFrom(field.getClass())) {
					AttributeField attributeField = (AttributeField) field;
					if (readOnlyFields != null && readOnlyFields.contains(attributeField.getBindingInfo().getBindingName())) {
						attributeField.setReadOnly(true);
					}
					if (requiredFields != null && requiredFields.contains(attributeField.getBindingInfo().getBindingName())) {
						attributeField.setRequired(Boolean.TRUE);
					}
					if (hiddenFields != null && hiddenFields.contains(attributeField.getBindingInfo().getBindingName())) {
						attributeField.setControl(LookupInquiryUtils.generateCustomLookupControlFromExisting(HiddenControl.class, null));
					}
				}
	        }
		}
	}

	/**
     * @see org.kuali.rice.kns.uif.container.View#performFinalize(org.kuali.rice.kns.uif.container.View, java.lang.Object)
     */
	@SuppressWarnings("unchecked")
	@Override
    public void performFinalize(View view, Object model, Component parent) {
	    super.performFinalize(view, model, parent);
//	    if (isRenderActionsFields()) {
//	        ((List<Field>)getResultsGroup().getItems()).add(0, getResultsActionsField());
//	    }
//	    if (isRenderReturnFields()) {
//	        ((List<Field>)getResultsGroup().getItems()).add(0, getResultsReturnField());
//	    }
    }

	public boolean isRenderActionsFields(){
		/* if - 
		 *   KualiForm.actionUrlsExist = true
		 *   KualiForm.suppressActions != true
		 *   !KualiForm.multipleValues
		 *   KualiForm.showMaintenanceLinks = true
		 *   
		 *  if - 
		 *   row.actionsUrls != ''
		 */
		LookupViewHelperService lookupHelperService = (LookupViewHelperService) getViewHelperService();
		return ( (lookupHelperService.isAtLeastOneRowHasActions()) &&
				 (!lookupHelperService.isSuppressActions()) &&
				 (lookupHelperService.isShowMaintenanceLinks())
				);
    }
	
	public boolean isRenderReturnFields(){
		/* if - 
		 *   KualiForm.formKey != ''
		 *   KualiForm.hideReturnLink = false
		 *   !KualiForm.multipleValues   
		 *   KualiForm.backLocation is not empty
		 *   
		 *  if -
		 *   row.rowReturnable
		 */
		LookupViewHelperService lookupHelperService = (LookupViewHelperService) getViewHelperService();
		return ( (StringUtils.isNotBlank(lookupHelperService.getReturnFormKey())) &&
				 (StringUtils.isNotBlank(lookupHelperService.getReturnLocation())) &&
				 (!lookupHelperService.isHideReturnLink())
				);
		/* if - 
		 *   KualiForm.formKey != ''   ----   @see LookupViewHelperService#getDocFormKey()
		 *   KualiForm.hideReturnLink = false   ----   
		 *   !KualiForm.multipleValues   ----   
		 *   KualiForm.backLocation is not empty   ----   
		 *   
		 *  if -
		 *   row.rowReturnable   ----   @see LookupViewHelperService#getActionUrlsFromField
		 */
	}
	/**
	 * Class name for the object the lookup applies to
	 * 
	 * <p>
	 * The object class name is used to pick up a dictionary entry which will
	 * feed the attribute field definitions and other configuration. In addition
	 * it is to configure the <code>Lookupable</code> which will carry out the
	 * lookup action
	 * </p>
	 * 
	 * @return Class<?> lookup data object class
	 */
	public Class<?> getDataObjectClassName() {
		return this.dataObjectClassName;
	}

	/**
	 * Setter for the object class name
	 * 
	 * @param dataObjectClassName
	 */
	public void setDataObjectClassName(Class<?> dataObjectClassName) {
		this.dataObjectClassName = dataObjectClassName;
	}

	/**
     * @return the resultsActionsField
     */
    public Field getResultsActionsField() {
    	return this.resultsActionsField;
    }

	/**
     * @param resultsActionsField the resultsActionsField to set
     */
    public void setResultsActionsField(Field resultsActionsField) {
    	this.resultsActionsField = resultsActionsField;
    }

	/**
     * @return the resultsReturnField
     */
    public Field getResultsReturnField() {
    	return this.resultsReturnField;
    }

	/**
     * @param resultsReturnField the resultsReturnField to set
     */
    public void setResultsReturnField(Field resultsReturnField) {
    	this.resultsReturnField = resultsReturnField;
    }

	public Group getCriteriaGroup() {
		return this.criteriaGroup;
	}

	public void setCriteriaGroup(Group criteriaGroup) {
		this.criteriaGroup = criteriaGroup;
	}

	public CollectionGroup getResultsGroup() {
		return this.resultsGroup;
	}

	public void setResultsGroup(CollectionGroup resultsGroup) {
		this.resultsGroup = resultsGroup;
	}

	public List<Component> getCriteriaFields() {
		return this.criteriaFields;
	}

	public void setCriteriaFields(List<Component> criteriaFields) {
		this.criteriaFields = criteriaFields;
	}

	public List<Component> getResultFields() {
		return this.resultFields;
	}

	public void setResultFields(List<Component> resultFields) {
		this.resultFields = resultFields;
	}

	public List<String> getDefaultSortAttributeNames() {
    	return this.defaultSortAttributeNames;
    }

	public void setDefaultSortAttributeNames(List<String> defaultSortAttributeNames) {
    	this.defaultSortAttributeNames = defaultSortAttributeNames;
    }

	public boolean isSortAscending() {
    	return this.sortAscending;
    }

	public void setSortAscending(boolean sortAscending) {
    	this.sortAscending = sortAscending;
    }

}
