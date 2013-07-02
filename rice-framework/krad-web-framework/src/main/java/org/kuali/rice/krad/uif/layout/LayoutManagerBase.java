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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.component.ReferenceCopy;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all layout managers
 *
 * <p>
 * Provides general properties of all layout managers, such as the unique id,
 * rendering template, and style settings
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class LayoutManagerBase extends UifDictionaryBeanBase implements LayoutManager {
    private static final long serialVersionUID = -2657663560459456814L;

    private String id;
    private String template;
    private String templateName;

    private String style;
    private List<String> libraryCssClasses;
    private List<String> cssClasses;
    private List<String> additionalCssClasses;

    @ReferenceCopy(newCollectionInstance = true)
    private Map<String, Object> context;

    private List<PropertyReplacer> propertyReplacers;

    public LayoutManagerBase() {
        super();

        cssClasses = new ArrayList<String>();
        context = new HashMap<String, Object>();
        propertyReplacers = new ArrayList<PropertyReplacer>();
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#performInitialization(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
     */
    @Override
    public void performInitialization(View view, Object model, Container container) {
        // set id of layout manager from container
        if (StringUtils.isBlank(id)) {
            id = container.getId() + "_layout";
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#performApplyModel(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
     */
    @Override
    public void performApplyModel(View view, Object model, Container container) {

    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
     */
    @Override
    public void performFinalize(View view, Object model, Container container) {

    }

    /**
     * Default Impl
     *
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getSupportedContainer()
     */
    @Override
    public Class<? extends Container> getSupportedContainer() {
        return Container.class;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        return new ArrayList<Component>();
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getComponentPrototypes()
     */
    @Override
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getId()
     */
    @Override
    @BeanTagAttribute(name = "id")
    public String getId() {
        return this.id;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getTemplate()
     */
    @Override
    @BeanTagAttribute(name = "template")
    public String getTemplate() {
        return this.template;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setTemplate(java.lang.String)
     */
    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getTemplateName()
     */
    @BeanTagAttribute(name = "tempateName")
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setTemplateName(java.lang.String)
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getStyle()
     */
    @Override
    @BeanTagAttribute(name = "Style")
    public String getStyle() {
        return this.style;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setStyle(java.lang.String)
     */
    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * @see LayoutManager#getLibraryCssClasses()
     */
    @Override
    @BeanTagAttribute(name = "libraryCssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getLibraryCssClasses() {
        return libraryCssClasses;
    }

    /**
     * @see LayoutManager#setLibraryCssClasses(java.util.List)
     */
    @Override
    public void setLibraryCssClasses(List<String> libraryCssClasses) {
        this.libraryCssClasses = libraryCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getCssClasses()
     */
    @Override
    @BeanTagAttribute(name = "cssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getCssClasses() {
        return this.cssClasses;
    }

    /**
     * @see LayoutManager#getAdditionalCssClasses()
     */
    @Override
    @BeanTagAttribute(name = "additionalCssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getAdditionalCssClasses() {
        return additionalCssClasses;
    }

    /**
     * @see LayoutManager#setAdditionalCssClasses(java.util.List)
     */
    @Override
    public void setAdditionalCssClasses(List<String> additionalCssClasses) {
        this.additionalCssClasses = additionalCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setCssClasses(java.util.List)
     */
    @Override
    public void setCssClasses(List<String> cssClasses) {
        this.cssClasses = cssClasses;
    }

    /**
     * Builds the HTML class attribute string by combining the styleClasses list
     * with a space delimiter
     *
     * @return class attribute string
     */
    public String getStyleClassesAsString() {
        if (cssClasses != null) {
            return StringUtils.join(cssClasses, " ");
        }

        return "";
    }

    /**
     * Sets the styleClasses list from the given string that has the classes
     * delimited by space. This is a convenience for configuration. If a child
     * bean needs to inherit the classes from the parent, it should configure as
     * a list and use merge="true"
     *
     * @param styleClasses
     */
    public void setStyleClasses(String styleClasses) {
        String[] classes = StringUtils.split(styleClasses);
        this.cssClasses = Arrays.asList(classes);
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#addStyleClass(java.lang.String)
     */
    @Override
    public void addStyleClass(String styleClass) {
        if (!cssClasses.contains(styleClass)) {
            cssClasses.add(styleClass);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#appendToStyle(java.lang.String)
     */
    @Override
    public void appendToStyle(String styleRules) {
        if (style == null) {
            style = "";
        }
        style = style + styleRules;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getContext()
     */
    @Override
    @BeanTagAttribute(name = "context", type = BeanTagAttribute.AttributeType.MAPBEAN)
    public Map<String, Object> getContext() {
        return this.context;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setContext(java.util.Map)
     */
    @Override
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#pushObjectToContext(java.lang.String,
     *      java.lang.Object)
     */
    @Override
    public void pushObjectToContext(String objectName, Object object) {
        if (this.context == null) {
            this.context = new HashMap<String, Object>();
        }

        this.context.put(objectName, object);
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getPropertyReplacers()
     */
    @Override
    @BeanTagAttribute(name = "propertyReplacers", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<PropertyReplacer> getPropertyReplacers() {
        return this.propertyReplacers;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setPropertyReplacers(java.util.List)
     */
    @Override
    public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers) {
        this.propertyReplacers = propertyReplacers;
    }


    @Override
    public <T extends LayoutManager> T copy() {
        try {
            T copiedClass = (T)this.getClass().newInstance();
            copyProperties(copiedClass);

            return copiedClass;
        }
        catch(Exception exception) {
            throw new RuntimeException();
        }
    }


    protected void copyProperties(LayoutManager layoutManager) {
        LayoutManagerBase componentBase = (LayoutManagerBase) layoutManager;

        //componentBase.setFoo(this.foo);

    }











    //    public LayoutManagerBase copy(LayoutManagerBase layoutManagerBaseOrig)
//    {
//        LayoutManagerBase layoutManagerBaseCopy = ((LayoutManagerBase)layoutManagerBaseOrig);
//        layoutManagerBaseCopy.setContext(layoutManagerBaseOrig.getContext());
//        layoutManagerBaseCopy.setCssClasses(layoutManagerBaseOrig.getCssClasses());
//        layoutManagerBaseCopy.setId(layoutManagerBaseOrig.getId());
//        layoutManagerBaseCopy.setPropertyReplacers(layoutManagerBaseOrig.getPropertyReplacers());
//        layoutManagerBaseCopy.setStyle(layoutManagerBaseOrig.getStyle());
//        layoutManagerBaseCopy.setStyleClasses(layoutManagerBaseOrig.getStyleClassesAsString());
//        layoutManagerBaseCopy.setTemplate(layoutManagerBaseOrig.getTemplate());
//        layoutManagerBaseCopy.setTemplateName(layoutManagerBaseOrig.getTemplateName());
//        layoutManagerBaseCopy.setComponentCode(layoutManagerBaseOrig.getComponentCode());
//        layoutManagerBaseCopy.setExpressionGraph(layoutManagerBaseOrig.getExpressionGraph());
//        layoutManagerBaseCopy.setNamespaceCode(layoutManagerBaseOrig.getNamespaceCode());
//        layoutManagerBaseCopy.setPropertyExpressions(layoutManagerBaseOrig.getPropertyExpressions());
//        layoutManagerBaseCopy.setRefreshExpressionGraph(layoutManagerBaseOrig.getRefreshExpressionGraph());
//        return layoutManagerBaseCopy;
//    }

}
