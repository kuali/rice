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
package org.kuali.rice.krad.uif.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.Copyable;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBeanBase;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.component.ReferenceCopy;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleTask;
import org.kuali.rice.krad.uif.util.LifecycleAwareList;
import org.kuali.rice.krad.uif.util.LifecycleAwareMap;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;

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
    
    private boolean cached;

    public LayoutManagerBase() {
        super();
        context = Collections.emptyMap();
        cssClasses = Collections.emptyList();
        libraryCssClasses = Collections.emptyList();
        additionalCssClasses = Collections.emptyList();
    }

    /**
     * @see LifecycleElement#checkMutable(boolean)
     */
    public void checkMutable(boolean legalDuringInitialization) {
        if (cached) {
            ViewLifecycle.reportIllegalState("Cached layout manager " + getClass() + " " + getId()
                    + " is immutable, use copy() to get a mutable instance");
            return;
        }
    }

    /**
     * @see LifecycleElement#isMutable(boolean)
     */
    public boolean isMutable(boolean legalDuringInitialization) {
        return !cached;
    }
    
    /**
     * @param mutable the mutable to set
     */
    public void setCached(boolean cached) {
        this.cached = cached;
    }

    /**
     * @see LifecycleElement#performInitialization(Object)
     */
    @Override
    public void performInitialization(Object model) {
        checkMutable(false);
        
        // set id of layout manager from container
        if (StringUtils.isBlank(id)) {
            Container container = (Container) ViewLifecycle.getPhase().getComponent();
            id = container.getId() + "_layout";
        }
    }

    /**
     * @see LifecycleElement#performApplyModel(Object, Component)
     */
    @Override
    public void performApplyModel(Object model, Component component) {
        checkMutable(false);
    }

    /**
     * @see LifecycleElement#performFinalize(Object, Component)
     */
    @Override
    public void performFinalize(Object model, Component component) {
        checkMutable(false);

        // put together all css class names for this component, in order
        List<String> finalCssClasses = new ArrayList<String>();
        
        View view = ViewLifecycle.getView();

        if (this.libraryCssClasses != null && view.isUseLibraryCssClasses()) {
            finalCssClasses.addAll(libraryCssClasses);
        }

        if (this.cssClasses != null) {
            finalCssClasses.addAll(cssClasses);
        }

        if (this.additionalCssClasses != null) {
            finalCssClasses.addAll(additionalCssClasses);
        }

        cssClasses = finalCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.util.LifecycleElement#initializePendingTasks(org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase, java.util.Queue)
     */
    @Override
    public void initializePendingTasks(ViewLifecyclePhase phase, Queue<ViewLifecycleTask> pendingTasks) {
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
        checkMutable(true);
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
        checkMutable(true);
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
        checkMutable(true);
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
        checkMutable(true);
        this.style = style;
    }

    /**
     * Additional css classes that come before css classes listed in the cssClasses property
     * 
     * <p>
     * These are used by the framework for styling with a library (for example, bootstrap), and
     * should normally not be overridden.
     * </p>
     * 
     * @return the library cssClasses
     */
    public List<String> getLibraryCssClasses() {
        if (libraryCssClasses == Collections.EMPTY_LIST && isMutable(true)) {
            libraryCssClasses = new LifecycleAwareList<String>(this);
        }
        
        return libraryCssClasses;
    }

    /**
     * Set the libraryCssClasses
     * 
     * @param libraryCssClasses
     */
    public void setLibraryCssClasses(List<String> libraryCssClasses) {
        checkMutable(true);

        if (libraryCssClasses == null) {
            this.libraryCssClasses = Collections.emptyList();
        } else {
            this.libraryCssClasses = new LifecycleAwareList<String>(this, libraryCssClasses);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getCssClasses()
     */
    @BeanTagAttribute(name = "cssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getCssClasses() {
        if (cssClasses == Collections.EMPTY_LIST && isMutable(true)) {
            cssClasses = new LifecycleAwareList<String>(this);
        }
        
        return cssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setCssClasses(java.util.List)
     */
    public void setCssClasses(List<String> cssClasses) {
        checkMutable(true);
        if (cssClasses == null) {
            this.cssClasses = Collections.emptyList();
        } else {
            this.cssClasses = new LifecycleAwareList<String>(this, cssClasses);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#getAdditionalCssClasses()
     */
    @BeanTagAttribute(name = "additionalCssClasses", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getAdditionalCssClasses() {
        if (additionalCssClasses == Collections.EMPTY_LIST && isMutable(true)) {
            additionalCssClasses = new LifecycleAwareList<String>(this);
        }
        
        return additionalCssClasses;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setAdditionalCssClasses(java.util.List)
     */
    public void setAdditionalCssClasses(List<String> additionalCssClasses) {
        checkMutable(true);
        if (additionalCssClasses == null) {
            this.additionalCssClasses = Collections.emptyList();
        } else {
            this.additionalCssClasses = new LifecycleAwareList<String>(this, additionalCssClasses);
        }
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
        checkMutable(true);
        String[] classes = StringUtils.split(styleClasses);
        this.cssClasses = Arrays.asList(classes);
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#addStyleClass(java.lang.String)
     */
    @Override
    public void addStyleClass(String styleClass) {
        checkMutable(false);
        if (cssClasses == null || cssClasses.isEmpty()) {
            cssClasses = new ArrayList<String>();
        }
        
        if (!cssClasses.contains(styleClass)) {
            cssClasses.add(styleClass);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#appendToStyle(java.lang.String)
     */
    @Override
    public void appendToStyle(String styleRules) {
        checkMutable(false);
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
        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new LifecycleAwareMap<String, Object>(this);
        }
        
        return context;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#setContext(java.util.Map)
     */
    @Override
    public void setContext(Map<String, Object> context) {
        checkMutable(true);

        if (context == null) {
            this.context = Collections.emptyMap();
        } else {
            this.context = new LifecycleAwareMap<String, Object>(this, context);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#pushObjectToContext(java.lang.String,
     *      java.lang.Object)
     */
    @Override
    public void pushObjectToContext(String objectName, Object object) {
        checkMutable(false);
        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new LifecycleAwareMap<String, Object>(this);
        }

        context.put(objectName, object);
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.LayoutManager#pushAllToContext(java.util.Map)
     */
    @Override
    public void pushAllToContext(Map<String, Object> sourceContext) {
        checkMutable(false);
        if (sourceContext == null || sourceContext.isEmpty()) {
            return;
        }
        
        if (context == Collections.EMPTY_MAP && isMutable(true)) {
            context = new LifecycleAwareMap<String, Object>(this);
        }

        this.context.putAll(sourceContext);
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
        checkMutable(true);
        this.propertyReplacers = propertyReplacers;
    }

    @Override
    public LayoutManagerBase clone() throws CloneNotSupportedException {
        LayoutManagerBase copy = (LayoutManagerBase) super.clone();
        copy.cached = false;
        return copy;
    }
    
    /**
     * Mark this instance as cached to prevent modification.
     * 
     * @see Copyable#preventModification()
     */
    @Override
    public void preventModification() {
        this.cached = true;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T layoutManager) {
        super.copyProperties(layoutManager);

        LayoutManagerBase layoutManagerBaseCopy = (LayoutManagerBase) layoutManager;

        layoutManagerBaseCopy.cached = false;

        layoutManagerBaseCopy.setId(this.id);
        layoutManagerBaseCopy.setTemplate(this.template);
        layoutManagerBaseCopy.setTemplateName(this.templateName);
        layoutManagerBaseCopy.setStyle(this.style);

        if (libraryCssClasses != null) {
            layoutManagerBaseCopy.setLibraryCssClasses(new ArrayList<String>(libraryCssClasses));
        }

        if (cssClasses != null) {
            layoutManagerBaseCopy.setCssClasses(new ArrayList<String>(cssClasses));
        }

        if (additionalCssClasses != null) {
            layoutManagerBaseCopy.setAdditionalCssClasses(new ArrayList<String>(additionalCssClasses));
        }

        if (getPropertyReplacers() != null) {
            List<PropertyReplacer> propertyReplacersCopy = new ArrayList<PropertyReplacer>();
            for (PropertyReplacer propertyReplacer : propertyReplacers) {
                propertyReplacersCopy.add((PropertyReplacer) propertyReplacer.copy());
            }

            layoutManagerBaseCopy.setPropertyReplacers(propertyReplacersCopy);
        }
    }
}
