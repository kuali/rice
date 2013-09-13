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
package org.kuali.rice.krad.uif.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentBase;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.layout.StackedLayoutManager;
import org.kuali.rice.krad.uif.widget.Disclosure;
import org.kuali.rice.krad.uif.widget.Pager;
import org.kuali.rice.krad.uif.widget.Tooltip;
import org.springframework.util.StringUtils;

import freemarker.core.Environment;
import freemarker.core.KualiTemplateUtils;
import freemarker.core.Macro;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Inline FreeMarker rendering utilities.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FreeMarkerInlineRenderUtils {

    /**
     * Resolve a FreeMarker environment variable as a Java object.
     * 
     * @param env The FreeMarker environment.
     * @param name The name of the variable.
     * @return The FreeMarker variable, resolved as a Java object.
     * @see #resolve(Environment, String, Class) for the preferred means to resolve variables for
     *      inline rendering.
     */
    @SuppressWarnings("unchecked")
    public static <T> T resolve(Environment env, String name) {
        TemplateModel tm = resolveModel(env, name);
        try {
            return (T) getBeansWrapper(env).unwrap(tm);
        } catch (TemplateModelException e) {
            throw new IllegalArgumentException("Failed to unwrap " + name + ", template model " + tm, e);
        }
    }

    /**
     * Resolve a FreeMarker environment variable as a Java object, with type enforcement.
     * 
     * <p>
     * This method is the preferred means to resolve variables for inline rendering.
     * </p>
     * 
     * @param env The FreeMarker environment.
     * @param name The name of the variable.
     * @param type The expected type of the variable.
     * @return The FreeMarker variable, resolved as a Java object of the given type.
     */
    public static <T> T resolve(Environment env, String name, Class<T> type) {
        Object rv = resolve(env, name);

        if ((rv instanceof Collection) && !Collection.class.isAssignableFrom(type)) {
            Collection<?> rc = (Collection<?>) rv;
            if (rc.isEmpty()) {
                return null;
            } else {
                rv = rc.iterator().next();
            }
        }

        if ("".equals(rv) && !String.class.equals(type)) {
            return null;
        } else {
            return type.cast(rv);
        }
    }

    /**
     * Get the object wrapper from the FreeMarker environment, as a {@link BeansWrapper}.
     * 
     * @param env The FreeMarker environment.
     * @return The object wrapper from the FreeMarker environment, type-cast as {@link BeansWrapper}
     *         .
     */
    public static BeansWrapper getBeansWrapper(Environment env) {
        ObjectWrapper wrapper = env.getObjectWrapper();

        if (!(wrapper instanceof BeansWrapper)) {
            throw new UnsupportedOperationException("FreeMarker environment uses unsupported ObjectWrapper " + wrapper);
        }

        return (BeansWrapper) wrapper;
    }

    /**
     * Resovle a FreeMarker variable as a FreeMarker template model object.
     * 
     * @param env The FreeMarker environment.
     * @param name The name of the variable.
     * @return The FreeMarker variable, resolved as a FreeMarker template model object.
     * @see #resolve(Environment, String, Class) for the preferred means to resolve variables for
     *      inline rendering.
     */
    public static TemplateModel resolveModel(Environment env, String name) {
        try {
            return env.getVariable(name);
        } catch (TemplateModelException e) {
            throw new IllegalArgumentException("Failed to resolve " + name + " in current freemarker environment", e);
        }
    }

    /**
     * Render a KRAD component template inline.
     * 
     * <p>
     * This method originated as template.ftl, and supercedes the previous content of that template.
     * </p>
     * 
     * @param env The FreeMarker environment.
     * @param component The component to render a template for.
     * @param body The nested body.
     * @param componentUpdate True if this is an update, false for full view.
     * @param includeSrc True to include the template source in the environment when rendering,
     *        false to skip inclusion.
     * @param tmplParms Additional parameters to pass to the template macro.
     * @throws TemplateException If FreeMarker rendering fails.
     * @throws IOException If rendering is interrupted due to an I/O error.
     */
    public static void renderTemplate(Environment env, Component component, String body,
            boolean componentUpdate, boolean includeSrc, Map<String, TemplateModel> tmplParms)
            throws TemplateException, IOException {

        if (component == null) {
            return;
        }

        String s;
        Writer out = env.getOut();
        if ((component.isRender() && (!component.isRetrieveViaAjax() || componentUpdate))
                ||
                (component.getProgressiveRender() != null && !component.getProgressiveRender().equals("")
                        && !component.isProgressiveRenderViaAJAX() && !component.isProgressiveRenderAndRefresh())) {

            if (StringUtils.hasText(s = component.getPreRenderContent())) {
                out.write(StringEscapeUtils.escapeHtml(s));
            }

            if (component.isSelfRendered()) {
                out.write(component.getRenderedHtmlOutput());
            } else {
                if (includeSrc) {
                    env.include(component.getTemplate(), env.getTemplate().getEncoding(), true);
                }

                Macro fmMacro = (Macro) env.getMainNamespace().get(component.getTemplateName());

                if (fmMacro == null) {
                    throw new TemplateException("No macro found using " + component.getTemplateName(), env);
                }

                Map<String, Object> args = new java.util.HashMap<String, Object>();
                args.put(component.getComponentTypeName(), component);

                if (tmplParms != null) {
                    args.putAll(tmplParms);
                }

                if (StringUtils.hasText(body)) {
                    args.put("body", body);
                }

                KualiTemplateUtils.invokeMacro(env, fmMacro, args, null);
            }

            if (StringUtils.hasText(s = component.getEventHandlerScript())) {
                renderScript(s, component, null, out);
            }

            if (StringUtils.hasText(s = component.getPostRenderContent())) {
                out.append(StringEscapeUtils.escapeHtml(s));
            }

        }

        if (componentUpdate) {
            return;
        }

        if (StringUtils.hasText(s = component.getProgressiveRender())) {
            if (!component.isRender()
                    && (component.isProgressiveRenderViaAJAX() || component.isProgressiveRenderAndRefresh())) {
                out.write("<span id=\"");
                out.write(component.getId());
                out.write("\" data-role=\"placeholder\" class=\"uif-placeholder\"></span>");
            }

            for (String cName : component.getProgressiveDisclosureControlNames()) {
                renderScript(
                        "var condition = function(){return ("
                                + component.getProgressiveDisclosureConditionJs()
                                + ");};setupProgressiveCheck('" + StringEscapeUtils.escapeJavaScript(cName)
                                + "', '" + component.getId() + "', '" + component.getBaseId() + "', condition,"
                                + component.isProgressiveRenderAndRefresh() + ", '"
                                + ((component instanceof ComponentBase) ? ((ComponentBase) component)
                                        .getMethodToCallOnRefresh() : "") + "');"
                        , component, null, out);
            }

            renderScript("hiddenInputValidationToggle('" + component.getId() + "');", null, null, out);
        }

        if ((component.isProgressiveRenderViaAJAX() && !StringUtils.hasLength(component.getProgressiveRender())) ||
                (!component.isRender() && (component.isDisclosedByAction() || component.isRefreshedByAction())) ||
                component.isRetrieveViaAjax()) {
            out.write("<span id=\"");
            out.write(component.getId());
            out.write("\" data-role=\"placeholder\" class=\"uif-placeholder\"></span>");
        }

        if (StringUtils.hasText(component.getConditionalRefresh())) {
            for (String cName : component.getConditionalRefreshControlNames()) {
                renderScript(
                        "var condition = function(){return ("
                                + StringEscapeUtils.escapeJavaScript(component.getConditionalRefreshConditionJs())
                                + ");};setupRefreshCheck('" + StringEscapeUtils.escapeJavaScript(cName) + "', '"
                                + component.getId() + "', condition,'"
                                + ((component instanceof ComponentBase) ? ((ComponentBase) component)
                                        .getMethodToCallOnRefresh() : "") + "');", null, null, out);
            }
        }

        List<String> refreshWhenChanged = component.getRefreshWhenChangedPropertyNames();
        if (refreshWhenChanged != null) {
            for (String cName : refreshWhenChanged) {
                renderScript(
                        "setupOnChangeRefresh('" + StringEscapeUtils.escapeJavaScript(cName) + "', '"
                                + component.getId()
                                + "','" + ((component instanceof ComponentBase) ? ((ComponentBase) component)
                                        .getMethodToCallOnRefresh() : "") + "');", null, null, out);
            }
        }

        renderTooltip(component, out);
    }

    /**
     * Render a KRAD tooltip component.
     * 
     * <p>
     * This method originated as template.ftl, and supercedes the previous content of that template.
     * </p>
     * 
     * @param component
     * @param out
     * @throws IOException
     */
    public static void renderTooltip(Component component, Writer out) throws IOException {
        Tooltip tt = component.getToolTip();
        if (tt != null && StringUtils.hasText(tt.getTooltipContent())) {
            String templateOptionsJSString = tt.getTemplateOptionsJSString();
            renderScript("createTooltip('" + component.getId() + "', '" + tt.getTooltipContent() + "', "
                    + (templateOptionsJSString == null ? "''" : templateOptionsJSString) + ", " + tt.isOnMouseHover()
                    + ", " + tt.isOnFocus() + ");", component, null, out);
            renderScript("addAttribute('" + component.getId() + "', 'class', 'uif-tooltip', true);", component, null,
                    out);
        }
    }

    public static void renderScript(String script, Component component, String role, Writer out) throws IOException {
        if (script == null || "".equals(script.trim()))
            return;
        out.write("<input name=\"script\" type=\"hidden\" data-role=\"");
        out.write(role == null ? "script" : role);
        out.write("\" ");

        if (component != null && component.getId() != null) {
            out.write("data-for=\"");
            out.write(component.getId());
            out.write("\" ");
        }

        out.write("value=\"");
        out.write(StringEscapeUtils.escapeHtml(script));
        out.write("\" />");
    }

    public static void renderAttrBuild(Component component, Writer out) throws IOException {
        String s;
        if (component instanceof ComponentBase) {
            ComponentBase componentBase = (ComponentBase) component;
            if (StringUtils.hasText(s = componentBase.getStyleClassesAsString())) {
                out.write(" class=\"");
                out.write(s);
                out.write("\"");
            }
        }

        if (StringUtils.hasText(s = component.getStyle())) {
            out.write(" style=\"");
            out.write(s);
            out.write("\"");
        }

        if (StringUtils.hasText(s = component.getTitle())) {
            out.write(" title=\"");
            out.write(s);
            out.write("\"");
        }
    }

    public static void renderOpenDiv(Component component, Writer out) throws IOException {
        out.write("<div id=\"");
        out.write(component.getId());
        out.write("\"");
        renderAttrBuild(component, out);
        out.write(">");
    }

    public static void renderCloseDiv(Writer out) throws IOException {
        out.write("</div>");
    }

    public static void renderOpenGroupWrap(Environment env, Group group) throws IOException, TemplateException {
        Writer out = env.getOut();
        renderOpenDiv(group, out);
        renderTemplate(env, group.getHeader(), null, false, false, null);
        Disclosure disclosure = group.getDisclosure();
        if (disclosure != null && disclosure.isRender()) {
            out.write("<div id=\"");
            out.write(group.getId());
            out.write("\" data-role=\"disclosureContent\" data-open=\"");
            out.write(Boolean.toString(disclosure.isDefaultOpen()));
            out.write("\" class=\"uif-disclosureContent\">");
        }
        renderTemplate(env, group.getValidationMessages(), null, false, false, null);
        renderTemplate(env, group.getInstructionalMessage(), null, false, false, null);
    }

    public static void renderCloseGroupWrap(Environment env, Group group) throws IOException, TemplateException {
        Writer out = env.getOut();
        renderTemplate(env, group.getFooter(), null, false, false, null);
        Disclosure disclosure = group.getDisclosure();
        if (disclosure != null && disclosure.isRender()) {
            out.write("</div>");
            Map<String, TemplateModel> tmplParms = new HashMap<String, TemplateModel>();
            tmplParms.put("parent", env.getObjectWrapper().wrap(group));
            renderTemplate(env, disclosure, null, false, false, tmplParms);
        }
        renderCloseDiv(out);
    }

    public static void renderCollectionGroup(Environment env, CollectionGroup group) throws IOException,
            TemplateException {
        renderOpenGroupWrap(env, group);

        Map<String, TemplateModel> tmplParms = new HashMap<String, TemplateModel>();
        tmplParms.put("componentId", env.getObjectWrapper().wrap(group.getId()));
        renderTemplate(env, group.getCollectionLookup(), null, false, false, tmplParms);

        if ("TOP".equals(group.getAddLinePlacement())) {
            if (group.isRenderAddBlankLineButton()) {
                renderTemplate(env, group.getAddBlankLineAction(), null, false, false, null);
            }

            if (group.isAddViaLightBox()) {
                renderTemplate(env, group.getAddViaLightBoxAction(), null, false, false, null);
            }
        }

        LayoutManager layoutManager = group.getLayoutManager();
        String managerTemplateName = layoutManager.getTemplateName();
        List<? extends Component> items = group.getItems();

        if ("uif_stacked".equals(managerTemplateName)) {
            renderStacked(env, items, (StackedLayoutManager) layoutManager, group);
        } else {
            Macro fmMacro = (Macro) env.getMainNamespace().get(layoutManager.getTemplateName());

            if (fmMacro == null) {
                throw new TemplateException("No macro found using " + layoutManager.getTemplateName(), env);
            }

            Map<String, Object> args = new java.util.HashMap<String, Object>();
            args.put("items", items);
            args.put("manager", group.getLayoutManager());
            args.put("container", group);
            KualiTemplateUtils.invokeMacro(env, fmMacro, args, null);
        }

        if ("BOTTOM".equals(group.getAddLinePlacement())) {
            if (group.isRenderAddBlankLineButton()) {
                renderTemplate(env, group.getAddBlankLineAction(), null, false, false, null);
            }

            if (group.isAddViaLightBox()) {
                renderTemplate(env, group.getAddViaLightBoxAction(), null, false, false, null);
            }
        }

        renderCloseGroupWrap(env, group);
    }

    public static void renderStacked(Environment env, List<? extends Component> items, StackedLayoutManager manager,
            CollectionGroup container) throws IOException,
            TemplateException {
        String s;
        Writer out = env.getOut();

        Pager pager = manager.getPagerWidget();
        Map<String, TemplateModel> pagerTmplParms = null;
        if (pager != null && container.isUseServerPaging()) {
            pagerTmplParms = new HashMap<String, TemplateModel>();
            pagerTmplParms.put("parent", env.getObjectWrapper().wrap(container));
            renderTemplate(env, pager, null, false, false, pagerTmplParms);
        }

        out.write("<div id=\"");
        out.write(manager.getId());

        if (StringUtils.hasText(s = manager.getStyle())) {
            out.write(" style=\"");
            out.write(s);
            out.write("\"");
        }

        if (StringUtils.hasText(s = manager.getStyleClassesAsString())) {
            out.write(" class=\"");
            out.write(s);
            out.write("\"");
        }

        out.write(">");

        Group wrapperGroup = manager.getWrapperGroup();
        if (wrapperGroup != null) {
            renderTemplate(env, wrapperGroup, null, false, false, null);
        } else {
            for (Group item : manager.getStackedGroups()) {
                renderTemplate(env, item, null, false, false, null);
            }
        }

        out.write("</div>");

        if (pager != null && container.isUseServerPaging()) {
            pagerTmplParms = new HashMap<String, TemplateModel>();
            pagerTmplParms.put("parent", env.getObjectWrapper().wrap(container));
            renderTemplate(env, pager, null, false, false, pagerTmplParms);
        }
    }

}
