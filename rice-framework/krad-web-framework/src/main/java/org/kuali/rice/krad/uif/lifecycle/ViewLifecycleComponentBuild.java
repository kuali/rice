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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.LightTable;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ExpressionUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.web.form.UifFormBase;

/**
 * TODO mark don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewLifecycleComponentBuild implements Runnable {

    private final String origId;
    private final Component component;

    /**
     * Constructor.
     * 
     * @param origId The ID of the original component, which component was based on.
     * @param component The component to build.
     */
    public ViewLifecycleComponentBuild(String origId, Component component) {
        this.origId = origId;
        this.component = component;
    }

    /**
     * 
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        ViewLifecycleProcessor processor = ViewLifecycle.getProcessor();
        View view = ViewLifecycle.getView();
        Object model = ViewLifecycle.getModel();

        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("begin-component-lifecycle:" + component.getId());
        }
        
        Component newComponent = component;
        Component origComponent = view.getViewIndex().getComponentById(origId);

        // check if the component is nested in a box layout in order to
        // reapply the layout item style
        List<String> origCss = origComponent.getCssClasses();
        if (origCss != null && (model instanceof UifFormBase)
                && ((UifFormBase) model).isUpdateComponentRequest()) {

            if (origCss.contains(UifConstants.BOX_LAYOUT_HORIZONTAL_ITEM_CSS)) {
                component.addStyleClass(UifConstants.BOX_LAYOUT_HORIZONTAL_ITEM_CSS);
            } else if (origCss.contains(UifConstants.BOX_LAYOUT_VERTICAL_ITEM_CSS)) {
                component.addStyleClass(UifConstants.BOX_LAYOUT_VERTICAL_ITEM_CSS);
            }
        }

        Map<String, Object> origContext = origComponent.getContext();

        Component parent = origContext == null ? null : (Component) origContext
                .get(UifConstants.ContextVariableNames.PARENT);

        // update context on all components within the refresh component to catch context set by parent
        if (origContext != null) {
            newComponent.pushAllToContext(origContext);

            List<Component> nestedComponents = ComponentUtils.getAllNestedComponents(newComponent);
            for (Component nestedComponent : nestedComponents) {
                nestedComponent.pushAllToContext(origContext);
            }
        }

        // make sure the dataAttributes are the same as original
        newComponent.setDataAttributes(origComponent.getDataAttributes());

        // initialize the expression evaluator
        view.getViewHelperService().getExpressionEvaluator().initializeEvaluationContext(model);

        // the expression graph for refreshed components is captured in the view index (initially it might expressions
        // might have come from a parent), after getting the expression graph then we need to populate the expressions
        // on the configurable for which they apply
        Map<String, String> expressionGraph = view.getViewIndex().getComponentExpressionGraphs().get(
                newComponent.getBaseId());
        newComponent.setExpressionGraph(expressionGraph);
        ExpressionUtils.populatePropertyExpressionsFromGraph(newComponent, false);

        // binding path should stay the same
        if (newComponent instanceof DataBinding) {
            ((DataBinding) newComponent).setBindingInfo(((DataBinding) origComponent).getBindingInfo());
            ((DataBinding) newComponent).getBindingInfo().setBindingPath(
                    ((DataBinding) origComponent).getBindingInfo().getBindingPath());
        }

        // copy properties that are set by parent components in the full view lifecycle
        if (newComponent instanceof Field) {
            ((Field) newComponent).setLabelRendered(((Field) origComponent).isLabelRendered());
        }

        if (origComponent.isRefreshedByAction()) {
            newComponent.setRefreshedByAction(true);
        }

        // reset data if needed
        if (newComponent.isResetDataOnRefresh()) {
            // TODO: this should handle groups as well, going through nested data fields
            if (newComponent instanceof DataField) {
                // TODO: should check default value

                // clear value
                ObjectPropertyUtils.initializeProperty(model,
                        ((DataField) newComponent).getBindingInfo().getBindingPath());
            }
        }
        
        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("ready:" + newComponent.getId());
        }
        
        processor.performPhase(LifecyclePhaseFactory.initialize(newComponent, model));

        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("initialize:" + newComponent.getId());
        }

        // adjust IDs for suffixes that might have been added by a parent component during the full view lifecycle
        String suffix = StringUtils.replaceOnce(origComponent.getId(), origComponent.getBaseId(), "");
        if (StringUtils.isNotBlank(suffix)) {
            ComponentUtils.updateIdWithSuffix(newComponent, suffix);
            ComponentUtils.updateChildIdsWithSuffixNested(newComponent, suffix);
        }

        // for collections that are nested in the refreshed group, we need to adjust the binding prefix and
        // set the sub collection id prefix from the original component (this is needed when the group being
        // refreshed is part of another collection)
        if (newComponent instanceof Group || newComponent instanceof FieldGroup) {
            List<CollectionGroup> origCollectionGroups = ComponentUtils.getComponentsOfTypeShallow(
                    origComponent,
                    CollectionGroup.class);
            List<CollectionGroup> collectionGroups = ComponentUtils.getComponentsOfTypeShallow(newComponent,
                    CollectionGroup.class);

            for (int i = 0; i < collectionGroups.size(); i++) {
                CollectionGroup origCollectionGroup = origCollectionGroups.get(i);
                CollectionGroup collectionGroup = collectionGroups.get(i);

                String prefix = origCollectionGroup.getBindingInfo().getBindByNamePrefix();
                if (StringUtils.isNotBlank(prefix) && StringUtils.isBlank(
                        collectionGroup.getBindingInfo().getBindByNamePrefix())) {
                    ComponentUtils.prefixBindingPath(collectionGroup, prefix);
                }

                String lineSuffix = origCollectionGroup.getSubCollectionSuffix();
                collectionGroup.setSubCollectionSuffix(lineSuffix);
            }

            // Handle LightTables, as well
            List<LightTable> origLightTables = ComponentUtils.getComponentsOfTypeShallow(origComponent,
                    LightTable.class);
            List<LightTable> lightTables = ComponentUtils.getComponentsOfTypeShallow(newComponent,
                    LightTable.class);

            for (int i = 0; i < lightTables.size(); i++) {
                LightTable origLightTable = origLightTables.get(i);
                LightTable lightTable = lightTables.get(i);

                String prefix = origLightTable.getBindingInfo().getBindByNamePrefix();
                if (StringUtils.isNotBlank(prefix) && StringUtils.isBlank(
                        lightTable.getBindingInfo().getBindByNamePrefix())) {
                    ComponentUtils.prefixBindingPath(lightTable, prefix);
                }
            }
        }

        // if disclosed by action and request was made, make sure the component will display
        if (newComponent.isDisclosedByAction()) {
            ComponentUtils.setComponentPropertyFinal(newComponent, UifPropertyPaths.RENDER, true);
            ComponentUtils.setComponentPropertyFinal(newComponent, UifPropertyPaths.HIDDEN, false);
        }

        processor.performPhase(LifecyclePhaseFactory.applyModel(newComponent, model, parent));

        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("apply-model:" + newComponent.getId());
        }

        // adjust nestedLevel property on some specific collection cases
        if (newComponent instanceof Container) {
            ComponentUtils.adjustNestedLevelsForTableCollections((Container) newComponent, 0);
        } else if (newComponent instanceof FieldGroup) {
            ComponentUtils.adjustNestedLevelsForTableCollections(((FieldGroup) newComponent).getGroup(), 0);
        }

        processor.performPhase(LifecyclePhaseFactory.finalize(newComponent, model, parent));

        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("finalize:" + newComponent.getId());
        }

        // make sure id, binding, and label settings stay the same as initial
        if (newComponent instanceof Group || newComponent instanceof FieldGroup) {
            List<Component> nestedGroupComponents = ComponentUtils.getAllNestedComponents(newComponent);
            List<Component> originalNestedGroupComponents = ComponentUtils
                    .getAllNestedComponents(origComponent);

            for (Component nestedComponent : nestedGroupComponents) {
                Component origNestedComponent = ComponentUtils.findComponentInList(
                        originalNestedGroupComponents,
                        nestedComponent.getId());

                if (origNestedComponent != null) {
                    // update binding
                    if (nestedComponent instanceof DataBinding) {
                        ((DataBinding) nestedComponent).setBindingInfo(
                                ((DataBinding) origNestedComponent).getBindingInfo());
                        ((DataBinding) nestedComponent).getBindingInfo().setBindingPath(
                                ((DataBinding) origNestedComponent).getBindingInfo().getBindingPath());
                    }

                    // update label rendered flag
                    if (nestedComponent instanceof Field) {
                        ((Field) nestedComponent).setLabelRendered(((Field) origNestedComponent)
                                .isLabelRendered());
                    }

                    if (origNestedComponent.isRefreshedByAction()) {
                        nestedComponent.setRefreshedByAction(true);
                    }
                }
            }
        }

        // get script for generating growl messages
        String growlScript = ViewLifecycle.getHelper().buildGrowlScript();
        ((ViewModel) model).setGrowlScript(growlScript);

        view.getViewIndex().indexComponent(newComponent);

        PageGroup page = view.getCurrentPage();
        // regenerate server message content for page
        page.getValidationMessages().generateMessages(false, view, model, page);

        if (ViewLifecycle.isTrace()) {
            ProcessLogger.trace("end-component-lifecycle:" + newComponent.getId());
        }
    }

}
