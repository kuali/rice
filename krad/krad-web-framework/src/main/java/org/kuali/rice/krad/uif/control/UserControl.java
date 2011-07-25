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
package org.kuali.rice.krad.uif.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.core.MethodInvokerConfig;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.AttributeQuery;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

/**
 * Represents a user control, which is a special control to handle
 * the input of a Person
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UserControl extends TextControl {

    private static final long serialVersionUID = 7468340793076585869L;
    private String principalIdPropertyName;
    private String personNamePropertyName;
    private String personObjectPropertyName;

    public UserControl() {
        super();
    }

    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (!(parent instanceof AttributeField)) {
            return;
        }

        AttributeField field = (AttributeField) parent;
        field.getHiddenPropertyNames().add(principalIdPropertyName);

        if (!field.isReadOnly()) {
            // add information fields
            if (StringUtils.isNotBlank(personNamePropertyName)) {
                field.getInformationalDisplayPropertyNames().add(personNamePropertyName);
            } else {
                field.getInformationalDisplayPropertyNames().add(personObjectPropertyName + ".name");
            }

            // setup script to clear id field when name is modified
            String idPropertyPath = field.getBindingInfo().getPropertyAdjustedBindingPath(principalIdPropertyName);
            String onChangeScript = "setValue('" + idPropertyPath + "','');";

            if (StringUtils.isNotBlank(field.getOnChangeScript())) {
                onChangeScript = field.getOnChangeScript() + onChangeScript;
            }
            field.setOnChangeScript(onChangeScript);
        }

        if (field.isReadOnly() && StringUtils.isBlank(field.getAdditionalDisplayPropertyName())) {
            field.setAdditionalDisplayPropertyName(personObjectPropertyName + ".name");
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
        field.setFieldAttributeQuery(attributeQuery);

        // setup field lookup
        QuickFinder quickFinder = field.getFieldLookup();
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
     * @return String principalIdPropertyName
     */
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
     * @return String personNamePropertyName
     */
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
     * @return String personObjectPropertyName
     */
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
}
