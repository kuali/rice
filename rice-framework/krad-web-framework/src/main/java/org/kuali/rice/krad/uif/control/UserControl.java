/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.MethodInvokerConfig;
import org.kuali.rice.krad.uif.field.AttributeQuery;
import org.kuali.rice.krad.uif.widget.QuickFinder;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user control, which is a special control to handle
 * the input of a Person
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "kimPersonControl-bean", parent = "Uif-KimPersonControl")
public class UserControl extends TextControl implements FilterableLookupCriteriaControl {
    private static final long serialVersionUID = 7468340793076585869L;

    private String principalIdPropertyName;
    private String personNamePropertyName;
    private String personObjectPropertyName;

    public UserControl() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performApplyModel(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (!(parent instanceof InputField)) {
            return;
        }

        InputField field = (InputField) parent;
        field.getAdditionalHiddenPropertyNames().add(principalIdPropertyName);

        if (!field.isReadOnly()) {
            // add information fields
            if (StringUtils.isNotBlank(personNamePropertyName)) {
                field.getPropertyNamesForAdditionalDisplay().add(personNamePropertyName);
            } else {
                field.getPropertyNamesForAdditionalDisplay().add(personObjectPropertyName + ".name");
            }

            // setup script to clear id field when name is modified
            String idPropertyPath = field.getBindingInfo().getPropertyAdjustedBindingPath(principalIdPropertyName);
            String onChangeScript = "setValue('" + ScriptUtils.escapeName(idPropertyPath) + "','');";

            if (StringUtils.isNotBlank(getOnChangeScript())) {
                onChangeScript = getOnChangeScript() + onChangeScript;
            }
            setOnChangeScript(onChangeScript);
        }

        if (field.isReadOnly() && StringUtils.isBlank(field.getReadOnlyDisplaySuffixPropertyName())) {
            if (StringUtils.isNotBlank(personNamePropertyName)) {
                field.setReadOnlyDisplaySuffixPropertyName(personNamePropertyName);
            } else {
                field.setReadOnlyDisplaySuffixPropertyName(personObjectPropertyName + ".name");
            }
        }

        // setup field query for displaying name
        AttributeQuery attributeQuery = new AttributeQuery();

        MethodInvokerConfig methodInvokerConfig = new MethodInvokerConfig();
        PersonService personService = KimApiServiceLocator.getPersonService();
        methodInvokerConfig.setTargetObject(personService);

        attributeQuery.setQueryMethodInvokerConfig(methodInvokerConfig);
        attributeQuery.setQueryMethodToCall("getPersonByPrincipalName");
        attributeQuery.getQueryMethodArgumentFieldList().add(field.getPropertyName());
        attributeQuery.getReturnFieldMapping().put("principalId", principalIdPropertyName);

        if (StringUtils.isNotBlank(personNamePropertyName)) {
            attributeQuery.getReturnFieldMapping().put("name", personNamePropertyName);
        } else {
            attributeQuery.getReturnFieldMapping().put("name", personObjectPropertyName + ".name");
        }
        field.setAttributeQuery(attributeQuery);

        // setup field lookup
        QuickFinder quickFinder = field.getQuickfinder();
        if (quickFinder.isRender()) {
            if (StringUtils.isBlank(quickFinder.getDataObjectClassName())) {
                quickFinder.setDataObjectClassName(Person.class.getName());
            }

            if (quickFinder.getFieldConversions().isEmpty()) {
                quickFinder.getFieldConversions().put("principalId", principalIdPropertyName);

                if (StringUtils.isNotBlank(personNamePropertyName)) {
                    quickFinder.getFieldConversions().put("name", personNamePropertyName);
                } else {
                    quickFinder.getFieldConversions().put("name", personObjectPropertyName + ".name");
                }

                quickFinder.getFieldConversions().put("principalName", field.getPropertyName());
            }
        }
    }

    /**
     * The name of the property on the parent object that holds the principal id
     *
     * @return principalIdPropertyName
     */
    @BeanTagAttribute(name="principalIdPropertyName")
    public String getPrincipalIdPropertyName() {
        return principalIdPropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the principal id
     *
     * @param principalIdPropertyName
     */
    public void setPrincipalIdPropertyName(String principalIdPropertyName) {
        this.principalIdPropertyName = principalIdPropertyName;
    }

    /**
     * The name of the property on the parent object that holds the person name
     *
     * @return personNamePropertyName
     */
    @BeanTagAttribute(name="personNamePropertyName")
    public String getPersonNamePropertyName() {
        return personNamePropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the person name
     *
     * @param personNamePropertyName
     */
    public void setPersonNamePropertyName(String personNamePropertyName) {
        this.personNamePropertyName = personNamePropertyName;
    }

    /**
     * The name of the property on the parent object that holds the person object
     *
     * @return personObjectPropertyName
     */
    @BeanTagAttribute(name="personObjectPropertyName")
    public String getPersonObjectPropertyName() {
        return personObjectPropertyName;
    }

    /**
     * Setter for the name of the property on the parent object that holds the person object
     *
     * @param personObjectPropertyName
     */
    public void setPersonObjectPropertyName(String personObjectPropertyName) {
        this.personObjectPropertyName = personObjectPropertyName;
    }

    /**
     * @see FilterableLookupCriteriaControl#filterSearchCriteria(String, java.util.Map)
     */
    @Override
    public Map<String, String>  filterSearchCriteria(String propertyName, Map<String, String> searchCriteria) {
        Map<String, String> filteredSearchCriteria = new HashMap<String, String>(searchCriteria);

        // check valid principalName
        // ToDo: move the principalId check and setting to the validation stage.  At that point the personName should
        //       be set as well or an error be displayed to the user that the principalName is invalid.
        String principalName = searchCriteria.get(propertyName);
        if (StringUtils.isNotBlank(principalName)) {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(principalName);
            if (principal == null) {
                return null;
            } else {
                filteredSearchCriteria.put(principalIdPropertyName, principal.getPrincipalId());
            }
        }

        // filter
        filteredSearchCriteria.remove(propertyName);
        filteredSearchCriteria.remove(personNamePropertyName);

        return filteredSearchCriteria;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#copy()
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);
        UserControl userControlCopy = (UserControl) component;
        userControlCopy.setPrincipalIdPropertyName(this.getPrincipalIdPropertyName());
        userControlCopy.setPersonNamePropertyName(this.getPersonNamePropertyName());
        userControlCopy.setPersonObjectPropertyName(this.getPersonObjectPropertyName());
    }
}
