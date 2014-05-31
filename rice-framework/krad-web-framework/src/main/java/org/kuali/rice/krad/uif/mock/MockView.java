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
package org.kuali.rice.krad.uif.mock;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.FormView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * View class for developing UI mocks.
 *
 * <p>Automatically binds data binding components to a dummy map property on the mock form.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "mockView", parent = "Uif-MockView")
public class MockView extends FormView {
    private static final long serialVersionUID = 3075358370551614649L;

    private static final String DATA_BINDING_PATH = "data";
    private static final String BOOLEAN_DATA_BINDING_PATH = "booleanData";

    public MockView() {
        super();
    }

    /**
     * Adjusts the binding paths for data binding components to match the generic data map.
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        DynaForm dynaForm = (DynaForm) model;

        List<DataBinding> bindingComponents = ViewLifecycleUtils.getElementsOfTypeDeep(this, DataBinding.class);
        for (DataBinding bindingComponent : bindingComponents) {
            adjustBindingPath(bindingComponent);
        }

        List<CollectionGroup> collectionGroups = ViewLifecycleUtils.getElementsOfTypeDeep(this, CollectionGroup.class);
        for (CollectionGroup collectionGroup : collectionGroups) {
            mockCollectionGroup(collectionGroup, dynaForm, null);
        }
    }

    /**
     * Creates sample data for read only data fields and defaults actions to call the refresh method.
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        List<DataField> dataFields = ViewLifecycleUtils.getElementsOfTypeDeep(this, DataField.class);
        for (DataField dataField : dataFields) {
            if ((!(dataField instanceof InputField)) && (dataField.getDefaultValue() == null)) {
                createSampleData(dataField, model);
            }
        }

        List<Action> actions = ViewLifecycleUtils.getElementsOfTypeDeep(this, Action.class);
        for (Action action : actions) {
            if (StringUtils.isBlank(action.getMethodToCall())) {
                action.setMethodToCall(UifConstants.MethodToCallNames.REFRESH);
            }
        }
    }

    /**
     * Adjusts the binding path for the given component to match the generic data map (or boolean data map).
     *
     * @param bindingComponent data binding component to adjust path for
     */
    protected void adjustBindingPath(DataBinding bindingComponent) {
        boolean isBooleanDataType = false;

        if (bindingComponent instanceof InputField) {
            InputField inputField = (InputField) bindingComponent;
            if ((inputField.getDataType() != null) && inputField.getDataType().equals(DataType.BOOLEAN)) {
                isBooleanDataType = true;
            } else if ((inputField.getControl() != null) && (inputField.getControl() instanceof CheckboxControl)) {
                isBooleanDataType = true;
            }
        }

        bindingComponent.getBindingInfo().setDefaults(this, bindingComponent.getPropertyName());

        if (isBooleanDataType) {
            bindingComponent.getBindingInfo().setBindByNamePrefix(BOOLEAN_DATA_BINDING_PATH);
        } else {
            bindingComponent.getBindingInfo().setBindByNamePrefix(DATA_BINDING_PATH);
        }

        bindingComponent.getBindingInfo().setBindToMap(true);
    }

    /**
     * Adjusts binding paths for the given collection group and sets the collection object class to be
     * {@link org.kuali.rice.krad.uif.mock.DynaDataObject}
     *
     * @param collectionGroup collection group to adjust
     * @param dynaForm form instance
     * @param bindingPrefix prefix for the collection group (in case of a sub-collection)
     */
    protected void mockCollectionGroup(CollectionGroup collectionGroup, DynaForm dynaForm, String bindingPrefix) {
        collectionGroup.setCollectionObjectClass(DynaDataObject.class);

        if (collectionGroup.getItems() != null) {
            for (Component item : collectionGroup.getItems()) {
                if (!(item instanceof DataBinding)) {
                    continue;
                }

                adjustBindingPath((DataBinding) item);
            }
        }

        String collectionPropertyName = collectionGroup.getPropertyName();
        if (StringUtils.isNotBlank(bindingPrefix)) {
            collectionPropertyName = bindingPrefix + "." + collectionPropertyName;
        }

        collectionGroup.getAddLineBindingInfo().setBindingPath(
                UifPropertyPaths.NEW_COLLECTION_LINES + "[" + collectionPropertyName + "]");

        if (dynaForm.isInitialGetRequest()) {
            createSampleLineData(collectionGroup, dynaForm);
        }

        if (collectionGroup.getSubCollections() != null) {
            for (CollectionGroup subCollectionGroup : collectionGroup.getSubCollections()) {
                subCollectionGroup.getBindingInfo().setBindingName(
                        DATA_BINDING_PATH + "[" + subCollectionGroup.getPropertyName() + "]");
                mockCollectionGroup(subCollectionGroup, dynaForm, collectionPropertyName);
            }
        }
    }

    /**
     * Creates sample data for the giving binding component.
     *
     * @param bindingComponent component to create data for
     * @param model form instance holding the view's data
     */
    protected void createSampleData(DataBinding bindingComponent, Object model) {
        String bindingPath = bindingComponent.getBindingInfo().getBindingPath();
        ObjectPropertyUtils.setPropertyValue(model, bindingPath, "data");
    }

    /**
     * Creates sample collection lines for the give collection group.
     *
     * @param collectionGroup collection group to create lines for
     * @param model form instance holding the view's data
     */
    protected void createSampleLineData(CollectionGroup collectionGroup, Object model) {
        String bindingPath = collectionGroup.getBindingInfo().getBindingPath();

        Collection<DynaDataObject> collection = ObjectPropertyUtils.getPropertyValue(model, bindingPath);

        if (collection == null) {
            collection = new ArrayList<DynaDataObject>();
            ObjectPropertyUtils.setPropertyValue(model, bindingPath, collection);
        }

        collection.add(new DynaDataObject());
        collection.add(new DynaDataObject());
    }
}
