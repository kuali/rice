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
package org.kuali.rice.krad.uif.util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentBase;
import org.kuali.rice.krad.uif.component.ReferenceCopy;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.CollectionGroupBase;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.BreadcrumbItem;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.DataFieldBase;
import org.kuali.rice.krad.uif.field.FieldBase;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.InputFieldBase;
import org.kuali.rice.krad.uif.widget.Tooltip;
import org.kuali.rice.krad.util.KRADConstants;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ComponentUtilsTest tests various ComponentUtils methods
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentUtilsTest {

    private String componentId;
    private Component component;

    @Before
    public void setup() {
        component = new InputFieldBase();
        componentId = "field1";
        component.setId(componentId);
    }

    // Initialization methods
    private CollectionGroup initializeCollectionGroup() {
        CollectionGroupBase collectionGroup = new CollectionGroupBase();
        collectionGroup = (CollectionGroupBase) initializeComponentBase(collectionGroup);

        DataField field1 = initializeDataField();
        DataField field2 = initializeDataField();
        List<DataField> fields = new ArrayList<DataField>();
        fields.add(field1);
        fields.add(field2);
        collectionGroup.setAddLineItems(fields);

        Action action1 = new Action();
        action1 = (Action) initializeComponentBase(action1);
        action1.setActionLabel("Action Label");
        action1.setActionScript("<script>Action script</script>");

        Action action2 = new Action();
        action2 = (Action) initializeComponentBase(action2);
        action2.setActionLabel("Action Label 2");
        action2.setActionScript("<script>Action script 2</script>");
        List<Action> addLineActions = new ArrayList<Action>();
        addLineActions.add(action1);
        addLineActions.add(action2);
        collectionGroup.setAddLineActions(addLineActions);

        return collectionGroup;
    }

    private FieldBase initializeFieldBase() {
        FieldBase fieldBase = new FieldBase();
        fieldBase = (FieldBase) initializeComponentBase(fieldBase);
        fieldBase.setShortLabel("Label");

        return fieldBase;
    }

    private DataField initializeDataField() {
        DataFieldBase dataField = new DataFieldBase();
        dataField = (DataFieldBase) initializeComponentBase(dataField);
        dataField.setAddHiddenWhenReadOnly(true);

        List<String> additionalHiddenPropertyNames = new ArrayList<String>();
        additionalHiddenPropertyNames.add("HiddenA");
        additionalHiddenPropertyNames.add("HiddenB");
        additionalHiddenPropertyNames.add("HiddenC");
        dataField.setAdditionalHiddenPropertyNames(additionalHiddenPropertyNames);

        dataField.setApplyMask(true);
        dataField.setDefaultValue("default");
        dataField.setDictionaryAttributeName("DictionaryName");
        dataField.setDictionaryObjectEntry("DictionaryObjectEntry");
        dataField.setEscapeHtmlInPropertyValue(true);
        dataField.setForcedValue("Forced");
        dataField.setMultiLineReadOnlyDisplay(true);

        return dataField;
    }

    private ComponentBase initializeComponentBase(ComponentBase componentBase) {
        List<String> additionalComponentsToRefresh = new ArrayList<String>();
        additionalComponentsToRefresh.add("A");
        additionalComponentsToRefresh.add("B");
        additionalComponentsToRefresh.add("C");
        componentBase.setAdditionalComponentsToRefresh(additionalComponentsToRefresh);

        List<String> additionalCssClasses = new ArrayList<String>();
        additionalCssClasses.add("Class1");
        additionalCssClasses.add("Class2");
        additionalCssClasses.add("Class3");
        componentBase.setAdditionalCssClasses(additionalCssClasses);

        componentBase.setAlign("right");

        List<String> cellCssClasses = new ArrayList<String>();
        cellCssClasses.add("CellClass1");
        cellCssClasses.add("CellClass2");
        cellCssClasses.add("CellClass3");
        componentBase.setWrapperCssClasses(cellCssClasses);

        componentBase.setWrapperStyle("Style1");
        componentBase.setCellWidth("20px");
        componentBase.setColSpan(2);
        componentBase.setConditionalRefresh("Refresh");

        List<String> cssClasses = new ArrayList<String>();
        cssClasses.add("CssClass1");
        cssClasses.add("CssClass2");
        cssClasses.add("CssClass3");
        componentBase.setCssClasses(cssClasses);

        Map<String, String> dataAttributes = new HashMap<String, String>();
        dataAttributes.put("One", "A");
        dataAttributes.put("Two", "B");
        dataAttributes.put("Three", "C");
        componentBase.setDataAttributes(dataAttributes);

        componentBase.setFinalizeMethodToCall("methodA");
        componentBase.setMethodToCallOnRefresh("methodB");
        componentBase.setOnBlurScript("onblurscript");
        componentBase.setOnChangeScript("onchangescript");
        componentBase.setOnClickScript("onclickscript");
        componentBase.setOnCloseScript("onclosescript");
        componentBase.setOnDblClickScript("ondblclickscript");
        componentBase.setOnDocumentReadyScript("ondocreadyscript");
        componentBase.setOnFocusScript("onfocusscript");
        componentBase.setOnKeyDownScript("onkeydownscript");
        componentBase.setOnKeyPressScript("onkeypressscript");
        componentBase.setOnKeyUpScript("onkeyupscript");
        componentBase.setOnLoadScript("onloadscript");
        componentBase.setOnMouseDownScript("onmousedownscript");
        componentBase.setOnMouseMoveScript("onmousemovescript");
        componentBase.setOnMouseOutScript("onmouseoutscript");
        componentBase.setOnMouseOverScript("onmouseoverscript");
        componentBase.setOnMouseUpScript("onmouseupscript");
        componentBase.setOnSubmitScript("onsubmitscript");
        componentBase.setOnUnloadScript("onunloadscript");
        componentBase.setOrder(5);
        componentBase.setPostRenderContent("PostRenderContent");
        componentBase.setPreRenderContent("PreRenderContent");
        componentBase.setProgressiveRender("ProgressiveRender");
        componentBase.setReadOnly(false);
        componentBase.setRefreshedByAction(false);
        componentBase.setRefreshTimer(12);

        List<String> refreshWhenChangedPropertyNames = new ArrayList<String>();
        refreshWhenChangedPropertyNames.add("property1");
        refreshWhenChangedPropertyNames.add("property2");
        refreshWhenChangedPropertyNames.add("property3");
        componentBase.setRefreshWhenChangedPropertyNames(refreshWhenChangedPropertyNames);

        componentBase.setRenderedHtmlOutput("<output>");
        componentBase.setRowSpan(3);
        componentBase.setStyle("slick");
        componentBase.setTemplate("TemplateA");
        componentBase.setTemplateName("TemplateName");

        Map<String, String> templateOptions = new HashMap<String, String>();
        templateOptions.put("Option1", "Value1");
        templateOptions.put("Option1", "Value2");
        templateOptions.put("Option1", "Value3");
        componentBase.setTemplateOptions(templateOptions);

        componentBase.setTemplateOptionsJSString("OptionsJS");
        componentBase.setTitle("Title");
        componentBase.setValign("middle");
        componentBase.setWidth("30px");

        return componentBase;
    }

    // End of Initialization methods

    /**
     * test that {@link ComponentUtils#updateIdWithSuffix} works ok
     */
    @Test
    public void testUpdateIdWithSuffix() {
        ComponentUtils.updateIdWithSuffix(component, null);
        assertTrue(component.getId().equalsIgnoreCase(componentId));

        String suffix = "_field";
        ComponentUtils.updateIdWithSuffix(component, suffix);
        assertTrue(component.getId().equalsIgnoreCase(componentId + suffix));
    }

    @Test
    /**
     * test {@link ComponentUtils#copyUsingCloning} using a FieldBase object
     */
    public void testCopyUsingCloningWithFieldBaseSucceeds() {
        FieldBase fieldBaseOriginal = initializeFieldBase();
        FieldBase fieldBaseCopy = CopyUtils.copy(fieldBaseOriginal);

        assertTrue(ComponentCopyPropertiesMatch(fieldBaseOriginal, fieldBaseCopy));
        assertTrue(fieldBaseOriginal.getShortLabel().equals(fieldBaseCopy.getShortLabel()));
    }

    @Test
    /**
     * test {@link ComponentUtils#copyUsingCloning} using a DataField object
     */
    public void testCopyUsingCloningWithDataFieldSucceeds() {
        DataField dataFieldOriginal = initializeDataField();

        DataField dataFieldCopy = CopyUtils.copy(dataFieldOriginal);
        assertTrue(ComponentCopyPropertiesMatch(dataFieldOriginal, dataFieldCopy));
    }

    /**
     * test {@link ComponentUtils#copyUsingCloning} using a CollectionGroup object
     */
    @Test
    public void testCopyUsingCloningWithSimpleCollectionGroupSucceeds() {
        CollectionGroup collectionGroupOriginal = initializeCollectionGroup();
        CollectionGroup collectionGroupCopy = CopyUtils.copy(collectionGroupOriginal);

        assertTrue(ComponentCopyPropertiesMatch(collectionGroupOriginal, collectionGroupCopy));

        for (int i = 0; i < collectionGroupOriginal.getAddLineItems().size(); i++) {
            assertTrue(ComponentCopyPropertiesMatch(
                    CopyUtils.unwrap((ComponentBase) collectionGroupOriginal.getAddLineItems().get(i)),
                    CopyUtils.unwrap((ComponentBase) collectionGroupCopy.getAddLineItems().get(i))));
        }

        for (int i = 0; i < collectionGroupOriginal.getAddLineActions().size(); i++) {
            assertTrue(ComponentCopyPropertiesMatch(
                    CopyUtils.unwrap((Component) collectionGroupOriginal.getAddLineActions().get(i)),
                    CopyUtils.unwrap((Component) collectionGroupCopy.getAddLineActions().get(i))));
        }
    }

    private boolean ComponentCopyPropertiesMatch(Component originalComponent, Component copiedComponent) {
        boolean result = true;

        List<String> missingComponentsToRefresh = originalComponent.getAdditionalComponentsToRefresh();
        if (missingComponentsToRefresh != null) {
            missingComponentsToRefresh = new ArrayList<String>(missingComponentsToRefresh);
            missingComponentsToRefresh.removeAll(copiedComponent.getAdditionalComponentsToRefresh());
            if (!missingComponentsToRefresh.isEmpty()) {
                result = false;
            }
        }

        List<String> missingAdditionalCssClasses = new ArrayList<String>(originalComponent.getAdditionalCssClasses());
        missingAdditionalCssClasses.removeAll(copiedComponent.getAdditionalCssClasses());
        if (!missingAdditionalCssClasses.isEmpty()) {
            result = false;
        }

        if (!originalComponent.getAlign().equals(copiedComponent.getAlign())) {
            result = false;
        }

        List<String> missingCellCssClasses = originalComponent.getWrapperCssClasses();
        if (missingCellCssClasses != null) {
            missingCellCssClasses.removeAll(copiedComponent.getWrapperCssClasses());
            if (!missingCellCssClasses.isEmpty()) {
                result = false;
            }
        }

        if (!originalComponent.getWrapperStyle().equals(copiedComponent.getWrapperStyle())) {
            result = false;
        }
        if (!originalComponent.getCellWidth().equals(copiedComponent.getCellWidth())) {
            result = false;
        }
        if (originalComponent.getColSpan() != copiedComponent.getColSpan()) {
            result = false;
        }
        if (!originalComponent.getConditionalRefresh().equals(copiedComponent.getConditionalRefresh())) {
            result = false;
        }

        List<String> missingCssClasses = new ArrayList<String>(originalComponent.getCssClasses());
        missingCssClasses.removeAll(copiedComponent.getCssClasses());
        if (!missingCssClasses.isEmpty()) {
            result = false;
        }

        Map<String, String> origDataAttributes = originalComponent.getDataAttributes();
        if (origDataAttributes != null) {
            Set<String> dataAttributes = new HashSet<String>(origDataAttributes.values());
            dataAttributes.removeAll(copiedComponent.getDataAttributes().values());
            if (!dataAttributes.isEmpty()) {
                result = false;
            }
        }

        if (!originalComponent.getFinalizeMethodToCall().equals(copiedComponent.getFinalizeMethodToCall())) {
            result = false;
        }
        if (originalComponent instanceof ComponentBase &&  copiedComponent instanceof ComponentBase){
            if (! (((ComponentBase)originalComponent).getMethodToCallOnRefresh().equals(((ComponentBase)copiedComponent).getMethodToCallOnRefresh()))) {
                result = false;
            }
        } else {
            result = false;
        }
        if (!originalComponent.getOnBlurScript().equals(copiedComponent.getOnBlurScript())) {
            result = false;
        }
        if (!originalComponent.getOnChangeScript().equals(copiedComponent.getOnChangeScript())) {
            result = false;
        }
        if (!originalComponent.getOnClickScript().equals(copiedComponent.getOnClickScript())) {
            result = false;
        }
        if (!originalComponent.getOnCloseScript().equals(copiedComponent.getOnCloseScript())) {
            result = false;
        }
        if (!originalComponent.getOnDblClickScript().equals(copiedComponent.getOnDblClickScript())) {
            result = false;
        }
        if (!originalComponent.getOnDocumentReadyScript().equals(copiedComponent.getOnDocumentReadyScript())) {
            result = false;
        }
        if (!originalComponent.getOnFocusScript().equals(copiedComponent.getOnFocusScript())) {
            result = false;
        }
        if (!originalComponent.getOnKeyDownScript().equals(copiedComponent.getOnKeyDownScript())) {
            result = false;
        }
        if (!originalComponent.getOnKeyPressScript().equals(copiedComponent.getOnKeyPressScript())) {
            result = false;
        }
        if (!originalComponent.getOnKeyUpScript().equals(copiedComponent.getOnKeyUpScript())) {
            result = false;
        }
        if (!originalComponent.getOnLoadScript().equals(copiedComponent.getOnLoadScript())) {
            result = false;
        }
        if (!originalComponent.getOnMouseDownScript().equals(copiedComponent.getOnMouseDownScript())) {
            result = false;
        }
        if (!originalComponent.getOnMouseMoveScript().equals(copiedComponent.getOnMouseMoveScript())) {
            result = false;
        }
        if (!originalComponent.getOnMouseOutScript().equals(copiedComponent.getOnMouseOutScript())) {
            result = false;
        }
        if (!originalComponent.getOnMouseOverScript().equals(copiedComponent.getOnMouseOverScript())) {
            result = false;
        }
        if (!originalComponent.getOnMouseUpScript().equals(copiedComponent.getOnMouseUpScript())) {
            result = false;
        }
        if (!originalComponent.getOnSubmitScript().equals(copiedComponent.getOnSubmitScript())) {
            result = false;
        }
        if (!originalComponent.getOnUnloadScript().equals(copiedComponent.getOnUnloadScript())) {
            result = false;
        }
        if (originalComponent.getOrder() != copiedComponent.getOrder()) {
            result = false;
        }
        if (!originalComponent.getPostRenderContent().equals(copiedComponent.getPostRenderContent())) {
            result = false;
        }
        if (!originalComponent.getPreRenderContent().equals(copiedComponent.getPreRenderContent())) {
            result = false;
        }
        if (!originalComponent.getProgressiveRender().equals(copiedComponent.getProgressiveRender())) {
            result = false;
        }
        if (originalComponent.getRequired() != copiedComponent.getRequired()) {
            result = false;
        }
        if (originalComponent.getRefreshTimer() != copiedComponent.getRefreshTimer()) {
            result = false;
        }

        List<String> missingRefreshWhenChangedPropertyNames = originalComponent.getRefreshWhenChangedPropertyNames();
        if (missingRefreshWhenChangedPropertyNames != null) {
            missingRefreshWhenChangedPropertyNames = new ArrayList<String>(missingRefreshWhenChangedPropertyNames);
            missingRefreshWhenChangedPropertyNames.removeAll(copiedComponent.getRefreshWhenChangedPropertyNames());
            if (!missingRefreshWhenChangedPropertyNames.isEmpty()) {
                result = false;
            }
        }

        if (!originalComponent.getRenderedHtmlOutput().equals(copiedComponent.getRenderedHtmlOutput())) {
            result = false;
        }
        if (originalComponent.getRowSpan() != copiedComponent.getRowSpan()) {
            result = false;
        }
        if (!originalComponent.getStyle().equals(copiedComponent.getStyle())) {
            result = false;
        }
        if (!originalComponent.getTemplate().equals(copiedComponent.getTemplate())) {
            result = false;
        }
        if (!originalComponent.getTemplateName().equals(copiedComponent.getTemplateName())) {
            result = false;
        }

        Map<String, String> origTemplateOptions = originalComponent.getTemplateOptions();
        if (origTemplateOptions != null) {
            Set<String> templateOptions = new HashSet<String>(origTemplateOptions.values());
            Map<String, String> copiedTemplateOptions = copiedComponent.getTemplateOptions();

            if (copiedTemplateOptions != null) {
                templateOptions.removeAll(copiedTemplateOptions.values());
            }

            if (!templateOptions.isEmpty()) {
                result = false;
            }
        }

        if (!originalComponent.getTemplateOptionsJSString().equals(copiedComponent.getTemplateOptionsJSString())) {
            result = false;
        }
        if (!originalComponent.getTitle().equals(copiedComponent.getTitle())) {
            result = false;
        }
        if (!originalComponent.getValign().equals(copiedComponent.getValign())) {
            result = false;
        }
        if (!originalComponent.getWidth().equals(copiedComponent.getWidth())) {
            result = false;
        }

        return result;
    }

    /**
     * Test {@link ContextUtils#cleanContextDeep} using a BreadcrumbItem object
     */
    @Test
    public void testCleanContextDeap() {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("contextkey", "value");
        context.put("contextkey2", "value2");

        BreadcrumbItem breadcrumbItem = new BreadcrumbItem();
        breadcrumbItem.setContext(context);

        InputField inputField = new InputFieldBase();
        inputField.setContext(context);

        Label fieldLabel = new Label();
        fieldLabel.setContext(context);

        Tooltip labelTootlip = new Tooltip();
        labelTootlip.setContext(context);
        fieldLabel.setToolTip(labelTootlip);

        inputField.setFieldLabel(fieldLabel);

        breadcrumbItem.setSiblingBreadcrumbComponent(inputField);

        Tooltip tooltip = new Tooltip();
        tooltip.setContext(context);

        breadcrumbItem.setToolTip(tooltip);

        ContextUtils.cleanContextDeep(breadcrumbItem);

        assertEquals(0, breadcrumbItem.getContext().size());
        assertEquals(0, inputField.getContext().size());
        assertEquals(0, fieldLabel.getContext().size());
        assertEquals(0, labelTootlip.getContext().size());
        assertEquals(0, tooltip.getContext().size());
    }

    @Ignore // Ignored for now, this is a proof of concept for using reflection to test copying
    @Test
    /**
     * test {@link ComponentUtils#copyUsingCloning} using a DataField object
     */
    public void testCopyUsingCloningWithDataTableSucceeds() {
        CheckboxControl dataTableOriginal = new CheckboxControl();

        initializeClass(dataTableOriginal);

        CheckboxControl dataTableCopy = CopyUtils.copy(dataTableOriginal);

        assertTrue(propertiesMatch(dataTableOriginal, dataTableCopy));
    }

    private void initializeClass(Object originalObject) {
        Class originalClass = originalObject.getClass();
        long index = 0L;

        for (Field field : originalClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ReferenceCopy.class)) {
                continue;
            }

            try {
                if (field.getType().equals(String.class)) {
                    field.setAccessible(true);
                    field.set(originalObject, "Test" + index);
                }

                if (field.getType().equals(long.class)) {
                    field.setAccessible(true);
                    field.setLong(originalObject, index);
                }

                if (field.getType().equals(int.class)) {
                    field.setAccessible(true);
                    field.setInt(originalObject, (int) index);
                }

                if (field.getType().equals(List.class)) {
                    field.setAccessible(true);
                    ParameterizedType myListType = ((ParameterizedType) field.getGenericType());
                    //myListType.getActualTypeArguments()[0].name;    // string value that looks like this: interface org.kuali.rice.krad.uif.component.Component
                    int something = 2;
                    //Class listClass = Class.forName(myListType.getActualTypeArguments()[0]);
                    Object[] objects = new Object[1];
                    objects[0] = new FieldBase();
                    List<?> fieldList = Arrays.asList((Object[]) objects);
                    field.set(originalObject, fieldList);
                    List<?> retrievedList = (List<?>) field.get(originalObject);
                    int somethingElse = 3;

                    //ArrayList<?> arrayList = new ArrayList<?>();

                }
            } catch (IllegalAccessException e) {
                // do nothing
            }

            ++index;
        }
    }

    private boolean propertiesMatch(Object originalObject, Object copiedObject) {
        Class originalClass = originalObject.getClass();
        Class copiedClass = copiedObject.getClass();

        for (Field field : originalClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ReferenceCopy.class)) {
                continue;
            }

            if (field.getType().equals(String.class)) {
                boolean propertiesMatch = stringPropertiesMatch(originalObject, copiedObject, copiedClass, field);
                if (!propertiesMatch) {
                    return false;
                }
            }

            if (field.getType().equals(long.class)) {
                boolean propertiesMatch = longPropertiesMatch(originalObject, copiedObject, copiedClass, field);
                if (!propertiesMatch) {
                    return false;
                }
            }

            if (field.getType().equals(int.class)) {
                boolean propertiesMatch = intPropertiesMatch(originalObject, copiedObject, copiedClass, field);
                if (!propertiesMatch) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean intPropertiesMatch(Object originalObject, Object copiedObject, Class copiedClass, Field field) {
        try {
            field.setAccessible(true);
            int oritinalInt = field.getInt(originalObject);
            Field copiedClassField = copiedClass.getDeclaredField(field.getName());
            copiedClassField.setAccessible(true);
            int copiedInt = copiedClassField.getInt(copiedObject);

            return oritinalInt == copiedInt;
        } catch (IllegalAccessException e) {
            return false;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private boolean longPropertiesMatch(Object originalObject, Object copiedObject, Class copiedClass, Field field) {
        try {
            field.setAccessible(true);
            Long originalLong = field.getLong(originalObject);
            Field copiedClassField = copiedClass.getDeclaredField(field.getName());
            copiedClassField.setAccessible(true);
            Long copiedLong = copiedClassField.getLong(copiedObject);

            return originalLong.equals(copiedLong);
        } catch (IllegalAccessException e) {
            return false;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    private boolean stringPropertiesMatch(Object originalObject, Object copiedObject, Class copiedClass, Field field) {
        try {
            field.setAccessible(true);
            String originalString = (String) field.get(originalObject);
            String copiedString = new String();
            Field copiedClassField = copiedClass.getDeclaredField(field.getName());
            copiedClassField.setAccessible(true);
            copiedString = (String) copiedClassField.get(copiedObject);

            return originalString.equals(copiedString);
        } catch (IllegalAccessException e) {
            return false;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
