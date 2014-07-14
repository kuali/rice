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
package org.kuali.rice.krad.uif.freemarker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.util.UifRenderHelperMethods;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;

import freemarker.cache.TemplateCache;
import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Encapsulates a FreeMarker environment for rendering within the view lifecycle.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LifecycleRenderingContext {
    
    /**
     * The FreeMarker environment to use for rendering.
     */
    private final Environment environment;

    /**
     * Set of imported FreeMarker templates.
     */
    private final Set<String> importedTemplates;

    /**
     * The buffer to use for capturing rendered output.
     */
    private final ByteArrayOutputStream buffer;

    /**
     * The FreeMarker writer, for passing character data to the output buffer.
     */
    private final PrintWriter writer;

    /**
     * Create FreeMarker environment for rendering within the view lifecycle.
     * 
     * @param request The active servlet request.
     * @param rawresponse The active servlet response.
     */
    public LifecycleRenderingContext(Object model, HttpServletRequest request) {
        try {
            ProcessLogger.countBegin("render");
            Map<String, Object> modelAttrs = new HashMap<String, Object>();
            modelAttrs.put(UifConstants.DEFAULT_MODEL_NAME, model);
            modelAttrs.put(KRADConstants.USER_SESSION_KEY, GlobalVariables.getUserSession());

            request.setAttribute(UifConstants.DEFAULT_MODEL_NAME, model);
            request.setAttribute(KRADConstants.USER_SESSION_KEY, GlobalVariables.getUserSession());
            modelAttrs.put(UifParameters.REQUEST, request);

            Map<String, String> properties = CoreApiServiceLocator.getKualiConfigurationService().getAllProperties();
            modelAttrs.put(UifParameters.CONFIG_PROPERTIES, properties);

            modelAttrs.put(UifParameters.RENDER_HELPER_METHODS, new UifRenderHelperMethods());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            Configuration config = FreeMarkerInlineRenderBootstrap.getFreeMarkerConfig();
            Template template = new Template("", new StringReader(""), config);

            ServletContext servletContext = FreeMarkerInlineRenderBootstrap.getServletContext();
            ObjectWrapper objectWrapper = FreeMarkerInlineRenderBootstrap.getObjectWrapper();
            ServletContextHashModel servletContextHashModel = FreeMarkerInlineRenderBootstrap
                    .getServletContextHashModel();
            TaglibFactory taglibFactory = FreeMarkerInlineRenderBootstrap.getTaglibFactory();

            Response response = new Response(new MockHttpServletResponse());
            AllHttpScopesHashModel global =
                    new AllHttpScopesHashModel(objectWrapper, servletContext, request);
            global.put(FreemarkerServlet.KEY_JSP_TAGLIBS, taglibFactory);
            global.put(FreemarkerServlet.KEY_APPLICATION, servletContextHashModel);
            global.put(FreemarkerServlet.KEY_SESSION,
                    new HttpSessionHashModel(request.getSession(), objectWrapper));
            global.put(FreemarkerServlet.KEY_REQUEST,
                    new HttpRequestHashModel(request, response, objectWrapper));
            global.put(FreemarkerServlet.KEY_REQUEST_PARAMETERS,
                    new HttpRequestParametersHashModel(request));
            global.put(AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE,
                    new RequestContext(request, response, servletContext, modelAttrs));

            global.put(UifParameters.VIEW, ViewLifecycle.getView());
            
//            Map<String, String> properties = CoreApiServiceLocator.getKualiConfigurationService()
//                    .getAllProperties();
//            global.put(UifParameters.CONFIG_PROPERTIES, properties);
//            global.put(UifParameters.RENDER_HELPER_METHODS, new UifRenderHelperMethods());

            Environment env = template.createProcessingEnvironment(global, writer);
            env.importLib("/krad/WEB-INF/ftl/lib/krad.ftl", "krad");
            env.importLib("/krad/WEB-INF/ftl/lib/spring.ftl", "spring");
            
            environment = env;
            buffer = out;
            importedTemplates = new HashSet<String>();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize FreeMarker for rendering", e);
        } catch (TemplateException e) {
            throw new IllegalStateException("Failed to initialize FreeMarker for rendering", e);
        } finally {
            ProcessLogger.countEnd("render", model.getClass().getName());
        }
    }

    /**
     * Get the FreeMarker environment for processing the rendering phase, initializing the
     * environment if needed.
     * 
     * @return The FreeMarker environment for processing the rendering phase, initializing the
     *         environment if needed.
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Clear the output buffer used during rendering, in preparation for rendering another component
     * using the same environment.
     */
    public void clearRenderingBuffer() {
        buffer.reset();
    }

    /**
     * Get all output rendered in the FreeMarker environment.
     */
    public String getRenderedOutput() {
        try {
            writer.flush();
            buffer.flush();
            return buffer.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 is unsupported", e);
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected exception flusing buffer", e);
        }
    }

    /**
     * Import a FreeMarker template for rendering into the current environment.
     * 
     * @param template The path to the FreeMarker template.
     */
    public void importTemplate(String template) {
        if (isImported(template)) {
            return;
        }

        try {
            String templateNameString = TemplateCache.getFullTemplatePath(environment, "", template);
            environment.include(environment.getTemplateForInclusion(templateNameString, null, true));
        } catch (ParseException e) {
            throw new IllegalStateException("Error parsing imported template " + template, e);
        } catch (TemplateException e) {
            throw new IllegalStateException("Error importing template " + template, e);
        } catch (IOException e) {
            throw new IllegalStateException("Error importing template " + template, e);
        }
    }
    
    public boolean isImported(String template) {
        return template == null || !importedTemplates.add(template);
    }
    
    /**
     * Wraps the lifcycle's servlet response in order to redirect output to the rendering context
     * buffer.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private class Response extends HttpServletResponseWrapper {

        /**
         * Constructor.
         * 
         * @param response The response to wrap.
         */
        public Response(HttpServletResponse response) {
            super(response);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    buffer.write(b);
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getBufferSize() {
            return buffer.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void flushBuffer() throws IOException {
            writer.flush();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void reset() {
            throw new UnsupportedOperationException("reset() should not be used during rendering");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void resetBuffer() {
            writer.flush();
            buffer.reset();
        }

    }
}
