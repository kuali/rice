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
package org.kuali.rice.krad.web.jsf.struts;

import java.io.IOException;
import javax.faces.FactoryFinder;
import javax.faces.application.ViewHandler;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.event.ActionEvent;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.apache.struts.action.InvalidCancelException;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ForwardConfig;
import org.kuali.rice.kns.web.struts.action.KualiRequestProcessor;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class FacesRequestProcessor extends KualiRequestProcessor {

    /**
     * <p>The log instance for this class.</p>
     */
    protected static Log log = LogFactory.getLog(FacesRequestProcessor.class);


    /**
     * <p>The lifecycle id.</p>
     */
    public static final String LIFECYCLE_ID_ATTR = "javax.faces.LIFECYCLE_ID";
    
    /**
     * <p>Set up a Faces Request if we are not already processing one.  Next,
     * create a new view if the specified <code>uri</code> is different from
     * the current view identifier.  Finally, cause the new view to be
     * rendered, and call <code>FacesContext.responseComplete()</code> to
     * indicate that this has already been done.</p>
     *
     * @param uri Context-relative path to forward to
     * @param request Current page request
     * @param response Current page response
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    protected void doForward(String uri,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws IOException, ServletException {

        if (log.isDebugEnabled()) {
            log.debug("doForward(" + uri + ")");
        }

        // Remove the current ActionEvent (if any)
//        request.removeAttribute(Constants.ACTION_EVENT_KEY);

        // Process a Struts controller request normally
        if (isStrutsRequest(uri)) {
            if (response.isCommitted()) {
                if (log.isTraceEnabled()) {
                    log.trace("  super.doInclude(" + uri + ")");
                }
                super.doInclude(uri, request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("  super.doForward(" + uri + ")");
                }
                super.doForward(uri, request, response);
            }
            return;
        }

        // Create a FacesContext for this request if necessary
        LifecycleFactory lf = (LifecycleFactory)
            FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle = 
            lf.getLifecycle(getLifecycleId());
        boolean created = false;
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            if (log.isTraceEnabled()) {
                log.trace("  Creating new FacesContext for '" + uri + "'");
            }
            created = true;
            FacesContextFactory fcf = (FacesContextFactory)
                FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            context = fcf.getFacesContext(servlet.getServletContext(),
                                          request, response, lifecycle);
        }

        // Create a new view root
        ViewHandler vh = context.getApplication().getViewHandler();
        if (log.isTraceEnabled()) {
            log.trace("  Creating new view for '" + uri + "'");
        }
        context.setViewRoot(vh.createView(context, uri));

        // Cause the view to be rendered
        if (log.isTraceEnabled()) {
            log.trace("  Rendering view for '" + uri + "'");
        }
        try {
            lifecycle.render(context);
        } finally {
            if (created) {
                if (log.isTraceEnabled()) {
                    log.trace("  Releasing context for '" + uri + "'");
                }
                context.release();
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("  Rendering completed");
                }
            }
        }

    }
    
    /**
     * <p>Populate the properties of the specified <code>ActionForm</code>
     * instance from the request parameters included with this request,
     * <strong>IF</strong> this is a non-Faces request.  For a Faces request,
     * this will have already been done by the <em>Update Model Values</em>
     * phase of the request processing lifecycle, so all we have to do is
     * recognize whether the request was cancelled or not.</p>
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param form The ActionForm instance we are populating
     * @param mapping The ActionMapping we are using
     *
     * @exception ServletException if thrown by RequestUtils.populate()
     */
    protected void processPopulate(HttpServletRequest request,
                                   HttpServletResponse response,
                                   ActionForm form,
                                   ActionMapping mapping)
        throws ServletException {

        // Are we processing a Faces request?
        String event = (String)
            request.getAttribute("FacesRequest");

        // Handle non-Faces requests in the usual way
       if (event == null) {
            if (log.isTraceEnabled()) {
                log.trace("Performing standard processPopulate() processing");
            }
            super.processPopulate(request, response, form, mapping);
            return;
       }

    }
    
    
    
    // --------------------------------------------------------- Private Methods


    /**
     * <p>Return the used Lifecycle ID (default or custom).</p>
     */
    private String getLifecycleId()
    {
        String lifecycleId = this.servlet.getServletContext().getInitParameter(LIFECYCLE_ID_ATTR);
        return lifecycleId != null ? lifecycleId : LifecycleFactory.DEFAULT_LIFECYCLE;
    }  

    /**
     * <p>Return <code>true</code> if the specified context-relative URI
     * specifies a request to be processed by the Struts controller servlet.</p>
     *
     * @param uri URI to be checked
     */
    private boolean isStrutsRequest(String uri) {

        int question = uri.indexOf("?");
        if (question >= 0) {
            uri = uri.substring(0, question);
        }
        
        return uri.endsWith("do") || uri.endsWith("jsp");
    }
}
