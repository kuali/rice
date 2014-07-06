/**
 * Copyright 2005-2014 The Kuali Foundation
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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.MethodInvokerConfig;
import org.kuali.rice.krad.uif.field.AttributeQuery;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.widget.QuickFinder;

/**
 * Represents a user control, which is a special control to handle the input of a Person.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "kimPersonControl", parent = "Uif-KimPersonControl")
public class UserControl extends TextControlBase implements FilterableLookupCriteriaControl {
    private static final long serialVersionUID = 7468340793076585869L;

    private String principalIdPropertyName;
    private String personNamePropertyName;
    private String personObjectPropertyName;

    public UserControl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        if (!(parent instanceof InputField)) {
            return;
        }

        InputField field = (InputField) parent;
        field.getAdditionalHiddenPropertyNames().add(principalIdPropertyName);

        if (isRender() && !isHidden() && !Boolean.TRUE.equals(getReadOnly())) {
            ViewLifecycle.getViewPostMetadata().addAccessibleBindingPath(principalIdPropertyName);
        }

        if (!Boolean.TRUE.equals(field.getReadOnly())) {
            // add information fields
            if (StringUtils.isNotBlank(personNamePropertyName)) {
                field.getPropertyNamesForAdditionalDisplay().add(personNamePropertyName);
            } else {
                field.getPropertyNamesForAdditionalDisplay().add(
                        personObjectPropertyName + "." + KimConstants.AttributeConstants.NAME);
            }

            // setup script to clear id field when name is modified
            String idPropertyPath = field.getBindingInfo().getPropertyAdjustedBindingPath(principalIdPropertyName);
            String onChangeScript = UifConstants.JsFunctions.SET_VALUE
                    + "('"
                    + ScriptUtils.escapeName(idPropertyPath)
                    + "','');";

            if (StringUtils.isNotBlank(getOnChangeScript())) {
                onChangeScript = getOnChangeScript() + onChangeScript;
            }
            setOnChangeScript(onChangeScript);
        }

        if (Boolean.TRUE.equals(field.getReadOnly()) && StringUtils.isBlank(field.getReadOnlyDisplaySuffixPropertyName())) {
            if (StringUtils.isNotBlank(personNamePropertyName)) {
                field.setReadOnlyDisplaySuffixPropertyName(personNamePropertyName);
            } else {
                field.setReadOnlyDisplaySuffixPropertyName(
                        personObjectPropertyName + "." + KimConstants.AttributeConstants.NAME);
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
        attributeQuery.getReturnFieldMapping().put(KimConstants.AttributeConstants.PRINCIPAL_ID,
                principalIdPropertyName);

        if (StringUtils.isNotBlank(personNamePropertyName)) {
            attributeQuery.getReturnFieldMapping().put(KimConstants.AttributeConstants.NAME, personNamePropertyName);
        } else {
            attributeQuery.getReturnFieldMapping().put(KimConstants.AttributeConstants.NAME,
                    personObjectPropertyName + "." + KimConstants.AttributeConstants.NAME);
        }
        field.setAttributeQuery(attributeQuery);

        buildUserQuickfinder(model, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> filterSearchCriteria(String propertyName, Map<String, String> searchCriteria,
            FilterableLookupCriteriaControlPostData postData) {
        Map<String, String> filteredSearchCriteria = new HashMap<String, String>(searchCriteria);

        UserControlPostData userControlPostData = (UserControlPostData) postData;

        // check valid principalName
        // ToDo: move the principalId check and setting to the validation stage.  At that point the personName should
        // be set as well or an error be displayed to the user that the principalName is invalid.
        String principalName = searchCriteria.get(propertyName);
        if (StringUtils.isNotBlank(principalName)) {
            if (!StringUtils.contains(principalName, "*")) {
                Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(
                        principalName);
                if (principal == null) {
                    return null;
                } else {
                    filteredSearchCriteria.put(userControlPostData.getPrincipalIdPropertyName(),
                            principal.getPrincipalId());
                }
            } else {
                List<Person> people = KimApiServiceLocator.getPersonService().findPeople(Collections.singletonMap(
                        KimConstants.AttributeConstants.PRINCIPAL_NAME, principalName));
                if (people != null && people.size() == 0) {
                    return null;
                }
            }
        }

        if (!StringUtils.contains(principalName, "*")) {
            // filter
            filteredSearchCriteria.remove(propertyName);
            filteredSearchCriteria.remove(userControlPostData.getPersonNamePropertyName());
        }

        return filteredSearchCriteria;
    }

    /**
     * Configures the field's quickfinder for a user lookup
     *
     * @param model object containing the view's data
     * @param field field instance the quickfinder should be associated with
     */
    protected void buildUserQuickfinder(Object model, InputField field) {
        QuickFinder quickFinder = field.getQuickfinder();

        // don't build quickfinder if explicitly turned off
        if (!field.isEnableAutoQuickfinder()) {
            return;
        }

        if (quickFinder == null) {
            quickFinder = ComponentFactory.getQuickFinder();
            field.setQuickfinder(quickFinder);
        }

        if (StringUtils.isBlank(quickFinder.getDataObjectClassName())) {
            quickFinder.setDataObjectClassName(Person.class.getName());
        }

        if (quickFinder.getFieldConversions().isEmpty()) {
            quickFinder.getFieldConversions().put(KimConstants.AttributeConstants.PRINCIPAL_ID,
                    principalIdPropertyName);

            if (StringUtils.isNotBlank(personNamePropertyName)) {
                quickFinder.getFieldConversions().put(KimConstants.AttributeConstants.NAME, personNamePropertyName);
            } else {
                quickFinder.getFieldConversions().put(KimConstants.AttributeConstants.NAME,
                        personObjectPropertyName + "." + KimConstants.AttributeConstants.NAME);
            }

            quickFinder.getFieldConversions().put(KimConstants.AttributeConstants.PRINCIPAL_NAME,
                    field.getPropertyName());
        }
    }

    /**
     * The name of the property on the parent object that holds the principal id
     *
     * @return principalIdPropertyName
     */
    @BeanTagAttribute
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
    @BeanTagAttribute
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
    @BeanTagAttribute
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
     * {@inheritDoc}
     */
    @Override
    public UserControlPostData getPostData(String propertyName) {
        return new UserControlPostData(propertyName, this);
    }

    /**
     * Holds post data for the {@link UserControl}.
     */
    public static class UserControlPostData implements FilterableLookupCriteriaControlPostData, Serializable {

        private static final long serialVersionUID = 3895010942559014164L;

        private String propertyName;

        private String principalIdPropertyName;
        private String personNamePropertyName;
        private String personObjectPropertyName;

        /**
         * Constructs the post data from the property name and the {@link UserControl}.
         *
         * @param propertyName the name of the property to filter
         * @param userControl the control to pull data from
         */
        public UserControlPostData(String propertyName, UserControl userControl) {
            this.propertyName = propertyName;
            this.principalIdPropertyName = userControl.getPrincipalIdPropertyName();
            this.personNamePropertyName = userControl.getPersonNamePropertyName();
            this.personObjectPropertyName = userControl.getPersonObjectPropertyName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Class<? extends FilterableLookupCriteriaControl> getControlClass() {
            return UserControl.class;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPropertyName() {
            return propertyName;
        }

        /**
         * @see UserControl#getPrincipalIdPropertyName()
         */
        public String getPrincipalIdPropertyName() {
            return principalIdPropertyName;
        }

        /**
         * @see UserControl#getPersonNamePropertyName()
         */
        public String getPersonNamePropertyName() {
            return personNamePropertyName;
        }

        /**
         * @see UserControl#getPersonObjectPropertyName()
         */
        public String getPersonObjectPropertyName() {
            return personObjectPropertyName;
        }

    }

}