/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.krad.uif.lifecycle;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.freemarker.FreeMarkerInlineRenderUtils;
import org.kuali.rice.krad.uif.layout.LayoutManager;

import freemarker.core.Environment;
import freemarker.core.Macro;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Lifecycle phase processing task for applying the model to a component.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RenderComponentPhase extends AbstractViewLifecyclePhase {

    private RenderComponentPhase parent;
    private List<RenderComponentPhase> siblings;
    
    /**
     * Assert that all siblings have the same parent object.
     * 
     * <p>
     * This method will only execute when assertions are enabled for this class.
     * </p>
     * 
     * @return True if all siblings have the same parent.
     */
    private boolean testSameParent() {
        for (RenderComponentPhase sibling : siblings) {
            assert parent == sibling.parent;
        }
        return true;
    }
    
    /**
     * Create a new lifecycle phase processing task for finalizing a component.
     * 
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     */
    public RenderComponentPhase(Component component, Object model, FinalizeComponentPhase finalizer,
            RenderComponentPhase parent, List<RenderComponentPhase> siblings) {
        super(component, model, finalizer == null
                ? Collections.<ViewLifecyclePhase> emptyList()
                : Collections.<ViewLifecyclePhase> singletonList(finalizer));
        this.parent = parent;
        this.siblings = Collections.unmodifiableList(siblings);
        assert testSameParent();
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getViewPhase()
     */
    @Override
    public String getViewPhase() {
        return UifConstants.ViewPhases.RENDER;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getStartViewStatus()
     */
    @Override
    public String getStartViewStatus() {
        return UifConstants.ViewStatus.FINAL;
    }

    /**
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecyclePhase#getEndViewStatus()
     */
    @Override
    public String getEndViewStatus() {
        return UifConstants.ViewStatus.RENDERED;
    }

    /**
     * Perform rendering on the given component.
     * 
     * @param view view instance the component belongs to
     * @param component the component instance that should be updated
     * @param model top level object containing the data
     * @param parent parent component for the component being finalized
     */
    @Override
    protected void performLifecyclePhase() {
        Component component = getComponent();
        if (component == null || !component.isRender() || component.getTemplate() == null) {
            return;
        }

        ViewLifecycle viewLifecycle = ViewLifecycle.getActiveLifecycle();
        Environment env = viewLifecycle.getFreeMarkerEnvironment();
        viewLifecycle.importFreeMarkerTemplate(component.getTemplate());
        
        if (component instanceof Group) {
            LayoutManager layoutManager = ((Group) component).getLayoutManager();
            
            if (layoutManager != null) {
                viewLifecycle.importFreeMarkerTemplate(layoutManager.getTemplate());
            }
        }
        
        viewLifecycle.clearRenderingBuffer();

        try {
            
            // Check for a single-arg macro, with the parameter name "component"
            // defer for parent rendering if not found
            Macro fmMacro = (Macro) env.getMainNamespace().get(component.getTemplateName());
            
            if (fmMacro == null) {
                return;
            }
            
            String[] args = fmMacro.getArgumentNames();
            if (args == null || args.length != 1 || !component.getComponentTypeName().equals(args[0])) {
                return;
            }

            FreeMarkerInlineRenderUtils.renderTemplate(env, component,
                    null, false, false, Collections.<String, TemplateModel> emptyMap());
        } catch (TemplateException e) {
            throw new IllegalStateException("Error rendering component " + component.getId(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Error rendering component " + component.getId(), e);
        }

        component.setSelfRendered(true);
        component.setRenderedHtmlOutput(viewLifecycle.getRenderedOutput());
    }

    /**
     * Define all nested lifecycle components, and component prototypes, as successors.
     * 
     * @see org.kuali.rice.krad.uif.lifecycle.AbstractViewLifecyclePhase#initializeSuccessors(java.util.List)
     */
    @Override
    protected void initializeSuccessors(List<ViewLifecyclePhase> successors) {
        if (parent == null) {
            return;
        }
        
        for (RenderComponentPhase sibling : siblings) {
            if (!sibling.isProcessed()) {
                return;
            }
        }
        
        successors.add(parent);
    }

}
