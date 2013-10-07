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
package org.kuali.rice.krad.uif.lifecycle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.RemoteFieldsHolder;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.ObjectPathExpressionParser;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.util.ObjectPathExpressionParser.PathEntry;
import org.kuali.rice.krad.uif.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lifecycle phase processing task for initializing a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InitializeComponentPhase extends AbstractViewLifecyclePhase {

    private final Logger LOG = LoggerFactory.getLogger(InitializeComponentPhase.class);

    /**
     * Create a new lifecycle phase processing task for performing initialization on a component.
     * 
     * @param component The component.
     * @param model The model
     */
    public InitializeComponentPhase(Component component, Object model) {
        super(component, model, Collections.<ViewLifecyclePhase> emptyList());
    }

    /**
     * Helper method for optimzing a call to
     * {@link ViewModelUtils#getPropertyTypeByClassAndView(View, String)} while parsing a path
     * expression for an attribute definition.
     *
     * @param formClass The view's form class.
     * @param modelClasses The view's model classes mapping.
     * @param rootPath The root path of the parse.
     * @param parentPath The parent path of the current parse entry.
     * @return The name of the dictionary entry to check at the current parse node.
     */
    private String getDictionaryEntryName(Object model, Map<String, Class<?>> modelClasses, String rootPath,
            String parentPath) {
        String modelClassPath = getModelClassPath(rootPath, parentPath);

        if (modelClassPath == null) {
            return null;
        }

        Class<?> dictionaryModelClass = modelClasses.get(modelClassPath);

        // full match
        if (dictionaryModelClass != null) {
            return dictionaryModelClass.getName();
        }

        // in case of partial match, holds the class that matched and the
        // property so we can get by reflection
        Class<?> modelClass = null;
        String modelProperty = modelClassPath;

        int bestMatchLength = 0;
        int modelClassPathLength = modelClassPath.length();

        // check if property path matches one of the modelClass entries
        for (Entry<String, Class<?>> modelClassEntry : modelClasses.entrySet()) {
            String path = modelClassEntry.getKey();
            int pathlen = path.length();

            if (modelClassPath.startsWith(path) && pathlen > bestMatchLength
                    && modelClassPathLength > pathlen && modelClassPath.charAt(pathlen + 1) == '.') {
                bestMatchLength = pathlen;
                modelClass = modelClassEntry.getValue();
                modelProperty = modelClassPath.substring(pathlen + 1);
            }
        }

        if (modelClass != null) {
            // if a partial match was found, look up the property type based on matched model class
            dictionaryModelClass = ObjectPropertyUtils.getPropertyType(modelClass, modelProperty);
        }

        if (dictionaryModelClass == null) {
            // If no full or partial match, look up based on the model directly
            dictionaryModelClass = ObjectPropertyUtils.getPropertyType(model, modelClassPath);
        }

        return dictionaryModelClass == null ? null : dictionaryModelClass.getName();
    }

    /**
     * Helper method for forming the model class path while parsing a path expression.
     *
     * @param rootPath The root parse path.
     * @param parentPath The parent path of the current parse node.
     * @return A model class path formed by concatenating the root path and parent path with a dot
     *         separator, then removing all collection index/key references.
     */
    private String getModelClassPath(String rootPath, String parentPath) {
        if (rootPath == null && parentPath == null) {
            return null;
        }

        StringBuilder modelClassPathBuilder = new StringBuilder();

        if (rootPath != null) {
            modelClassPathBuilder.append(rootPath);
        }

        if (parentPath != null) {
            if (rootPath != null) modelClassPathBuilder.append('.');
            modelClassPathBuilder.append(parentPath);
        }

        int bracketCount = 0;
        int leftBracketPos = -1;
        for (int i=0; i < modelClassPathBuilder.length(); i++) {
            char c = modelClassPathBuilder.charAt(i);

            if (c == '[') {
                bracketCount++;
                if (bracketCount == 1) leftBracketPos = i;
            }

           if (c == ']') {
               bracketCount--;

               if (bracketCount < 0) {
                   throw new IllegalArgumentException("Unmatched ']' at " + i + " " + modelClassPathBuilder);
               }

               if (bracketCount == 0) {
                   modelClassPathBuilder.delete(leftBracketPos, i + 1);
                   i -= i + 1 - leftBracketPos;
                   leftBracketPos = -1;
               }
           }
        }

        if (bracketCount > 0) {
            throw new IllegalArgumentException("Unmatched '[' at " + leftBracketPos + " " + modelClassPathBuilder);
        }

        return modelClassPathBuilder.toString();
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getStartViewStatus()
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.CREATED;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEndViewStatus()
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.INITIALIZED;
    }


    /**
     * Perform component initialization.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#performLifecyclePhase()
     */
    @Override
    protected void performLifecyclePhase() {
        Component component = getComponent();
        Object model = getModel();
        
        if (component == null) {
            return;
        }

        if (StringUtils.isBlank(component.getId())) {
            throw new RiceRuntimeException(
                    "Id is not set, this should not happen unless a component is misconfigured");
        }

        // TODO: duplicate ID check
        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        View view = viewLifecycle.getView();
        ViewHelperService helper = viewLifecycle.getHelper();

        LOG.debug("Initializing component: " + component.getId() + " with type: " + component.getClass());

        // add initial state to the view index for component refreshes
        if (!(component instanceof View)) {
            view.getViewIndex().addInitialComponentStateIfNeeded(component);
        }

        // the component can have an expression graph for which the expressions need pulled to
        // the list the expression service will evaluate
        ExpressionUtils.populatePropertyExpressionsFromGraph(component, true);

        // invoke component to initialize itself after properties have been set
        component.performInitialization(model);

        // move expressions on property replacers and component modifiers
        List<PropertyReplacer> componentPropertyReplacers = component.getPropertyReplacers();
        if (componentPropertyReplacers != null) {
            for (PropertyReplacer replacer : componentPropertyReplacers) {
                ExpressionUtils.populatePropertyExpressionsFromGraph(replacer, true);
            }
        }

        List<ComponentModifier> componentModifiers = component.getComponentModifiers();
        if (componentModifiers != null) {
            for (ComponentModifier modifier : component.getComponentModifiers()) {
                ExpressionUtils.populatePropertyExpressionsFromGraph(modifier, true);
            }
        }

        // for attribute fields, set defaults from dictionary entry
        if (component instanceof DataField) {
            initializeDataFieldFromDataDictionary(view, model, (DataField) component);
        }

        if (component instanceof Container) {
            // invoke hook point for adding components through code
            helper.addCustomContainerComponents(model, (Container) component);

            // process any remote fields holder that might be in the containers items, collection items will get
            // processed as the lines are being built
            if (!(component instanceof CollectionGroup)) {
                processAnyRemoteFieldsHolder(view, model, (Container) component);
            }
        }

        // for collection groups set defaults from dictionary entry
        if (component instanceof CollectionGroup) {
            // TODO: initialize from dictionary
        }

        // invoke initialize service hook
        helper.performCustomInitialization(component);

        // invoke component modifiers setup to run in the initialize phase
        viewLifecycle.runComponentModifiers(component, null, UifConstants.ViewPhases.INITIALIZE);

    }

    /**
     * Define all nested lifecycle components, and component prototypes, as successors.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#initializeSuccessors(java.util.List)
     */
    @Override
    protected void initializeSuccessors(List<ViewLifecyclePhase> successors) {
        Component component = getComponent();
        Object model = getModel();
        
        // initialize nested components
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            if (nestedComponent != null) {
                successors.add(new InitializeComponentPhase(nestedComponent, model));
            }
        }

        // initialize component prototypes
        for (Component nestedComponent : component.getComponentPrototypes()) {
            if (nestedComponent != null) {
                successors.add(new InitializeComponentPhase(nestedComponent, model));
            }
        }
    }

    /**
     * Iterates through the containers configured items checking for
     * <code>RemotableFieldsHolder</code>, if found the holder is invoked to retrieved the remotable
     * fields and translate to attribute fields. The translated list is then inserted into the
     * container item list at the position of the holder
     * 
     * @param view view instance containing the container
     * @param model object instance containing the view data
     * @param container container instance to check for any remotable fields holder
     */
    protected void processAnyRemoteFieldsHolder(View view, Object model, Container container) {
        List<Component> processedItems = new ArrayList<Component>();

        // check for holders and invoke to retrieve the remotable fields and translate
        // translated fields are placed into the container item list at the position of the holder
        for (Component item : container.getItems()) {
            if (item instanceof RemoteFieldsHolder) {
                List<InputField> translatedFields = ((RemoteFieldsHolder) item).fetchAndTranslateRemoteFields(view,
                        model, container);
                processedItems.addAll(translatedFields);
            } else {
                processedItems.add(item);
            }
        }

        // updated container items
        container.setItems(processedItems);
    }

    /**
     * Recursively drills down the property path (if nested) to find an AttributeDefinition, the
     * first attribute definition found will be returned
     * 
     * <p>
     * e.g. suppose parentPath is 'document' and propertyPath is 'account.subAccount.name', first
     * the property type for document will be retrieved using the view metadata and used as the
     * dictionary entry, with the propertyPath as the dictionary attribute, if an attribute
     * definition exists it will be returned. Else, the first part of the property path is added to
     * the parent, making the parentPath 'document.account' and the propertyPath 'subAccount.name',
     * the method is then called again to perform the process with those parameters. The recursion
     * continues until an attribute field is found, or the propertyPath is no longer nested
     * </p>
     * 
     * @param view view instance containing the field
     * @param field field we are attempting to find a supporting attribute definition for
     * @param propertyPath path of the property to use as dictionary attribute and to drill down on
     * @return AttributeDefinition if found, or Null
     */
    protected AttributeDefinition findNestedDictionaryAttribute(final View view, final Object model, DataField field,
            String propertyPath) {
        // attempt to find definition for parent and property
        String fieldBindingPrefix = null;
        String dictionaryAttributePath = propertyPath;

        if (field.getBindingInfo().isBindToMap()) {
            fieldBindingPrefix = "";
            if (!field.getBindingInfo().isBindToForm() && StringUtils.isNotBlank(
                    field.getBindingInfo().getBindingObjectPath())) {
                fieldBindingPrefix = field.getBindingInfo().getBindingObjectPath();
            }
            if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
                if (StringUtils.isNotBlank(fieldBindingPrefix)) {
                    fieldBindingPrefix += "." + field.getBindingInfo().getBindByNamePrefix();
                } else {
                    fieldBindingPrefix = field.getBindingInfo().getBindByNamePrefix();
                }
            }

            dictionaryAttributePath = field.getBindingInfo().getBindingName();
        }

        if (StringUtils.isEmpty(dictionaryAttributePath)) {
            return null;
        }

        final DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        final String rootPath = fieldBindingPrefix;
        final Map<String, Class<?>> modelClasses = view.getObjectPathToConcreteClassMapping();

        class AttributePathEntry implements PathEntry {
            AttributeDefinition attributeDefinition;
            String dictionaryAttributeName;
            String dictionaryObjectEntry;

            @Override
            public Object parse(String parentPath, Object node, String next) {
                if (next == null) {
                    return node;
                }

                if (attributeDefinition != null || node == null) {
                    return null;
                }

                String dictionaryEntryName =
                        getDictionaryEntryName(model, modelClasses, rootPath, parentPath);

                if (dictionaryEntryName != null) {
                    attributeDefinition = dataDictionaryService
                            .getAttributeDefinition(dictionaryEntryName, next);

                    if (attributeDefinition != null) {
                        dictionaryObjectEntry = dictionaryEntryName;
                        dictionaryAttributeName = next;
                        return null;
                    }
                }

                return node;
            }
        }

        AttributePathEntry attributePathEntry = new AttributePathEntry();
        ObjectPathExpressionParser
                .parsePathExpression(attributePathEntry, dictionaryAttributePath, attributePathEntry);

        // if a definition was found, update the fields dictionary properties
        if (attributePathEntry.attributeDefinition != null) {
            field.setDictionaryObjectEntry(attributePathEntry.dictionaryObjectEntry);
            field.setDictionaryAttributeName(attributePathEntry.dictionaryAttributeName);
        }

        return attributePathEntry.attributeDefinition;
    }

    /**
     * Sets properties of the <code>InputField</code> (if blank) to the corresponding attribute
     * entry in the data dictionary
     * 
     * @param view view instance containing the field
     * @param field data field instance to initialize
     */
    protected void initializeDataFieldFromDataDictionary(View view, Object model, DataField field) {
        AttributeDefinition attributeDefinition = null;

        String dictionaryAttributeName = field.getDictionaryAttributeName();
        String dictionaryObjectEntry = field.getDictionaryObjectEntry();

        // if entry given but not attribute name, use field name as attribute
        // name
        if (StringUtils.isNotBlank(dictionaryObjectEntry) && StringUtils.isBlank(dictionaryAttributeName)) {
            dictionaryAttributeName = field.getPropertyName();
        }

        // if dictionary entry and attribute set, attempt to find definition
        if (StringUtils.isNotBlank(dictionaryAttributeName) && StringUtils.isNotBlank(dictionaryObjectEntry)) {
            attributeDefinition = KRADServiceLocatorWeb.getDataDictionaryService()
                    .getAttributeDefinition(dictionaryObjectEntry, dictionaryAttributeName);
        }

        // if definition not found, recurse through path
        if (attributeDefinition == null) {
            
            BindingInfo fieldBindingInfo = field.getBindingInfo();
            String collectionPath = fieldBindingInfo.getCollectionPath();
            String propertyPath;
            
            if (StringUtils.isNotBlank(collectionPath)) {
                StringBuilder propertyPathBuilder = new StringBuilder();

                String bindingObjectPath = fieldBindingInfo.getBindingObjectPath();
                if (StringUtils.isNotBlank(bindingObjectPath)) {
                    propertyPathBuilder.append(bindingObjectPath).append('.');
                }

                propertyPathBuilder.append(collectionPath).append('.');

                String bindByNamePrefix = fieldBindingInfo.getBindByNamePrefix();
                if (StringUtils.isNotBlank(bindByNamePrefix)) {
                    propertyPathBuilder.append(bindByNamePrefix).append('.');
                }

                propertyPathBuilder.append(fieldBindingInfo.getBindingName());
                propertyPath = propertyPathBuilder.toString();

            } else {
                propertyPath = field.getBindingInfo().getBindingPath();
            }

            attributeDefinition = findNestedDictionaryAttribute(view, model, field, propertyPath);
        }

        // if a definition was found, initialize field from definition
        if (attributeDefinition != null) {
            field.copyFromAttributeDefinition(view, attributeDefinition);
        }

        // if control still null, assign default
        if (field instanceof InputField) {
            InputField inputField = (InputField) field;
            if (inputField.getControl() == null) {
                Control control = ComponentFactory.getTextControl();
                view.assignComponentIds(control);

                inputField.setControl(control);
            }
        }
    }

}
