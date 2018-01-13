/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.uif.component;

import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.uif.layout.CssGridSizes;
import org.kuali.rice.krad.uif.lifecycle.RunComponentModifiersTask;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.widget.Tooltip;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Component defines basic properties and methods that all rendering element implement
 *
 * <p>
 * All classes of the UIF that are used as a rendering element implement the
 * component interface. All components within the framework have the
 * following structure:
 * <ul>
 * <li>Dictionary Configuration/Composition</li>
 * <li>Java Class (the Component implementation</li>
 * <li>>JSP Template Renderer</li>
 * </ul>
 * </p>
 * <p>
 * There are three basic types of components:
 * <ul>
 * <li>Container Components: {@code View}, {@code Group}</li>
 * <li>Field Components: {@code Field}</li>
 * <li>Widget Components: {@code Widget}</li>
 * </ul>
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.container.Container
 * @see org.kuali.rice.krad.uif.field.Field
 * @see org.kuali.rice.krad.uif.widget.Widget
 */
public interface Component extends UifDictionaryBean, LifecycleElement, Serializable, Ordered, ScriptEventSupport {

    /**
     * The name for the component type
     *
     * <p>
     * This is used within the rendering layer to pass the component instance into the template. The component instance
     * is exported under the name given by this method.
     * </p>
     *
     * @return String type name
     */
    String getComponentTypeName();

    /**
     * Indicates whether the component has been fully rendered.
     *
     * @return True if the component has fully rendered, false if not.
     */
    boolean isRendered();

    /**
     * Invoked by the view lifecycle after expressions are evaluated at the apply model phase.
     *
     * <p>
     * In general, this method is preferred to {@link #performApplyModel(Object, LifecycleElement)}
     * for populating model data via code, since it is called before client-side state is synchronize.
     * </p>
     */
    void afterEvaluateExpression();

    /**
     * Set the view lifecycle processing status for this component, explicitly.
     *
     * @param status The view status for this component.
     */
    void setViewStatus(String status);

    /**
     * The path to the JSP file that should be called to render the component
     *
     * <p>
     * The path should be relative to the web root. An attribute will be available to the component to use under the
     * name given by the method {@code getComponentTypeName}. Based on the component type, additional attributes could
     * be available for use. See the component documentation for more information on such attributes.
     * </p>
     *
     * <p>
     * e.g. '/krad/WEB-INF/jsp/tiles/component.jsp'
     * </p>
     *
     * @return String representing the template path
     */
    String getTemplate();

    /**
     * Setter for the components template
     *
     * @param template
     */
    void setTemplate(String template);

    /**
     * Gets additional templates that will be required during the rendering of this component.
     *
     * <p>
     * If a parent or sibling component is referred to by this component's template,
     * include that component's template here to ensure that it has been compiled already during
     * bottom-up inline rendering.
     * </p>
     *
     * @return additional templates required during rendering
     */
    List<String> getAdditionalTemplates();

    /**
     * The name for which the template can be invoked by
     *
     * <p>
     * Whether the template name is needed depends on the underlying rendering engine being used. In the example of
     * Freemarker, the template points to the actual source file, which then loads a macro. From then on the macro is
     * simply invoked to execute the template
     * </p>
     *
     * <p>
     * e.g. 'uif_text'
     * </p>
     *
     * @return template name
     */
    public String getTemplateName();

    /**
     * Setter for the name of the template (a name which can be used to invoke)
     *
     * @param templateName
     */
    public void setTemplateName(String templateName);

    /**
     * The component title
     *
     * <p>
     * Depending on the component can be used in various ways. For example with a Container component the title is
     * used to set the header text. For components like controls other other components that render an HTML element it
     * is used to set the HTML title attribute.
     * </p>
     *
     * @return String title for component
     */
    String getTitle();

    /**
     * Setter for the component's title
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * List of components that are contained within the List of {@code PropertyReplacer} in component
     *
     * <p>
     * Used to get all the nested components in the property replacers.
     * </p>
     *
     * @return List<Component> {@code PropertyReplacer} child components
     */
    List<Component> getPropertyReplacerComponents();

    /**
     * {@code ComponentModifier} instances that should be invoked to
     * initialize the component
     *
     * <p>
     * These provide dynamic initialization behavior for the component and are
     * configured through the components definition. Each initializer will get
     * invoked by the initialize method.
     * </p>
     *
     * @return List of component modifiers
     * @see RunComponentModifiersTask
     */
    List<ComponentModifier> getComponentModifiers();

    /**
     * Setter for the components List of {@code ComponentModifier}
     * instances
     *
     * @param componentModifiers
     */
    void setComponentModifiers(List<ComponentModifier> componentModifiers);

    /**
     * When true, this component will render as a placeholder component instead of rendering normally because the
     * content will be later retrieved through manually ajax retrieval calls in the js
     *
     * <p>This flag does not imply any automation, there must be a js call invoked for the content to be retrieved
     * by the server, but this does mark it with a placeholder component which KRAD js uses during these calls.
     * This placeholder component is used for ajax retrievals.  In particular, this flag is useful for use in
     * combination with the <b>showLightboxComponent</b> js function which will automatically retrieve the
     * real content of a component through ajax if a placeholder component is detected.  This allows for the full
     * content to only be retrieved when the lightbox is first opened.
     * When this flag is set to true, the forceSessionPersistence
     * flag is set to true AUTOMATICALLY because it is implied that this component will be retrieved by an ajax call
     * in the future.  This may also be useful for direct custom calls to <b>retrieveComponent</b> function,
     * as well, which also relies on the placeholder being present.</p>
     *
     * @return true if this component is being rendered as a placeholder for use in replacement during and ajax call,
     * false otherwise
     */
    public boolean isRetrieveViaAjax();

    /**
     * When true, this component will render as a placeholder component instead of rendering normally because the
     * content will be later retrieved through manually ajax retrieval calls in the js
     *
     * @param useAjaxCallForContent
     */
    public void setRetrieveViaAjax(boolean useAjaxCallForContent);

    /**
     * Indicates whether the component should be hidden in the UI
     *
     * <p>
     * How the hidden data is maintained depends on the views persistence mode.
     * If the mode is request, the corresponding data will be rendered to the UI
     * but not visible. If the mode is session, the data will not be rendered to
     * the UI but maintained server side.
     * </p>
     *
     * <p>
     * For a {@code Container} component, the hidden setting will apply to
     * all contained components (making a section hidden makes all fields within
     * the section hidden)
     * </p>
     *
     * @return boolean true if the component should be hidden, false if it
     * should be visible
     */
    boolean isHidden();

    /**
     * Setter for the hidden indicator
     *
     * @param hidden
     */
    void setHidden(boolean hidden);

    /**
     * Indicates whether the component can be edited
     *
     * <p>
     * When readOnly the controls and widgets of {@code Field} components
     * will not be rendered. If the Field has an underlying value it will be
     * displayed readOnly to the user.
     * </p>
     *
     * <p>
     * For a {@code Container} component, the readOnly setting will apply
     * to all contained components (making a section readOnly makes all fields
     * within the section readOnly).
     * </p>
     *
     * @return boolean true if the component should be readOnly, false if is
     * allows editing
     */
    Boolean getReadOnly();

    /**
     * Setter for the read only indicator
     *
     * @param readOnly
     */
    void setReadOnly(Boolean readOnly);

    /**
     * Indicates whether the component should be cleared on copy
     *
     * <p>
     *  By default this property is false. ReadOnly components are cleared on a copy operation.
     *  If set this prevents the component from being cleared.
     * </p>
     * @return
     */
    Boolean getCanCopyOnReadOnly();

    /**
     * Setter for the canCopyOnReadOnly indicator
     *
     * @param canCopyOnReadOnly
     */
    void setCanCopyOnReadOnly(Boolean canCopyOnReadOnly);
    /**
     * Indicates whether the component is required
     *
     * <p>
     * At the general component level required means there is some action the
     * user needs to take within the component. For example, within a section it
     * might mean the fields within the section should be completed. At a field
     * level, it means the field should be completed. This provides the ability
     * for the renderers to indicate the required action.
     * </p>
     *
     * @return boolean true if the component is required, false if it is not
     * required
     */
    Boolean getRequired();

    /**
     * Setter for the required indicator
     *
     * @param required
     */
    void setRequired(Boolean required);

    /**
     * Horizontal alignment of the component within its container
     *
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies how the component
     * should be aligned horizontally within the container. During the finalize
     * phase the CSS text-align style will be created for the align setting.
     * </p>
     *
     * @return String horizontal align
     * @see org.kuali.rice.krad.uif.CssConstants.TextAligns
     */
    public String getAlign();

    /**
     * Sets the components horizontal alignment
     *
     * @param align
     */
    public void setAlign(String align);

    /**
     * Vertical alignment of the component within its container
     *
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies how the component
     * should be aligned vertically within the container. During the finalize
     * phase the CSS vertical-align style will be created for the valign
     * setting.
     * </p>
     *
     * @return String vertical align
     * @see org.kuali.rice.krad.uif.CssConstants.VerticalAligns
     */
    public String getValign();

    /**
     * Setter for the component's vertical align
     *
     * @param valign
     */
    public void setValign(String valign);

    /**
     * Width the component should take up in the container
     *
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies a width the component
     * should take up in the Container. This is not applicable for all layout
     * managers. During the finalize phase the CSS width style will be created
     * for the width setting.
     * </p>
     *
     * <p>
     * e.g. '30%', '55px'
     * </p>
     *
     * @return String width string
     */
    public String getWidth();

    /**
     * Setter for the components width
     *
     * @param width
     */
    public void setWidth(String width);

    /**
     * CSS style string to be applied to the component
     *
     * <p>
     * Any style override or additions can be specified with this attribute.
     * This is used by the renderer to set the style attribute on the
     * corresponding element.
     * </p>
     *
     * <p>
     * e.g. 'color: #000000;text-decoration: underline;'
     * </p>
     *
     * @return String css style string
     */
    String getStyle();

    /**
     * Setter for the components style
     *
     * @param style
     */
    void setStyle(String style);

    public List<String> getLibraryCssClasses();

    public void setLibraryCssClasses(List<String> libraryCssClasses);

    /**
     * CSS style class(s) to be applied to the component
     *
     * <p>
     * Declares style classes for the component. Multiple classes are specified
     * with a space delimiter. This is used by the renderer to set the class
     * attribute on the corresponding element. The class(s) declared must be
     * available in the common style sheets or the style sheets specified for
     * the view
     * </p>
     *
     * @return List<String> css style classes to appear on the 'class' attribute
     */
    List<String> getCssClasses();

    /**
     * Setter for the components style classes
     *
     * @param styleClasses
     */
    void setCssClasses(List<String> styleClasses);

    /**
     * Convenience property for adding css class names to the end of the list of cssClasses that may already exist on
     * this Component (this is to avoid explicitly having to set list merge in the bean definition)
     *
     * @return the additionalCssClasses
     */
    List<String> getAdditionalCssClasses();

    /**
     * Set the additionalCssClasses
     *
     * @param styleClasses
     */
    void setAdditionalCssClasses(List<String> styleClasses);

    /**
     * Adds a single style to the list of styles on this component
     *
     * @param styleClass
     */
    void addStyleClass(String styleClass);

    /**
     * Appends to the inline style set on a component
     *
     * @param itemStyle
     */
    void appendToStyle(String itemStyle);

    /**
     * Number of places the component should take up horizontally in the
     * container; when using a CssGridLayoutManager this is converted to the appropriate medium size.
     *
     * <p>
     * All components belong to a {@code Container} and are placed using a
     * {@code LayoutManager}. This property specifies how many places
     * horizontally the component should take up within the container. This is
     * only applicable for table based layout managers. Default is 1
     * </p>
     *
     * TODO: this should not be on component interface since it only applies if
     * the layout manager supports it, need some sort of layoutOptions map for
     * field level options that depend on the manager
     *
     * @return int number of columns to span
     */
    int getColSpan();

    /**
     * Setter for the components column span
     *
     * @param colSpan
     */
    void setColSpan(int colSpan);

    /**
     * Number of places the component should take up vertically in the container
     *
     * <p>
     * All components belong to a {@code Container} and are placed using a
     * {@code LayoutManager}. This property specifies how many places
     * vertically the component should take up within the container. This is
     * only applicable for table based layout managers. Default is 1
     * </p>
     *
     * TODO: this should not be on component interface since it only applies if
     * the layout manager supports it, need some sort of layoutOptions map for
     * field level options that depend on the manager
     *
     * @return int number of rows to span
     */
    int getRowSpan();

    /**
     * Setter for the component row span
     *
     * @param rowSpan
     */
    void setRowSpan(int rowSpan);

    /**
     * The cellCssClasses property defines the classes that will be placed on the corresponding td (or th) elements
     * relating to this component when used in a table backed layout.  This property has no effect on other layouts.
     *
     * @return the css classes to apply to the wrapping td (or th) element for this component
     */
    public List<String> getWrapperCssClasses();

    /**
     * Set the cellCssClasses property which defines the classes that will be placed on the corresponding td (or th)
     * relating to this component when used in a table backed layout.  This property has no effect on other layouts.
     *
     * @param cellCssClasses
     */
    public void setWrapperCssClasses(List<String> cellCssClasses);

    /**
     * Add a cell css class to the cell classes list
     *
     * @param cssClass the name of the class to add
     */
    public void addWrapperCssClass(String cssClass);

    /**
     * CSS style string to be applied to the cell containing the component (only applies within
     * table based layouts)
     *
     * <p>
     * e.g. 'align: right;'
     * </p>
     *
     * @return String css style string
     */
    public String getWrapperStyle();

    /**
     * Setter for the cell style attribute
     *
     * @param cellStyle
     */
    public void setWrapperStyle(String cellStyle);

    /**
     * Width setting for the cell containing the component (only applies within table based
     * layouts)
     *
     * @return String width ('25%', '155px')
     */
    public String getCellWidth();

    /**
     * Setter for the containing cell width
     *
     * @param cellWidth
     */
    public void setCellWidth(String cellWidth);

    /**
     * CssGridSizes represent the size (width) the content's div "cell" will take up in the "row" at each screen
     * size (extra small, small, medium, large) when using a group backed by a CssGridLayoutManager.
     *
     * <p>
     * This object is NOT used by other layouts.
     * For specifics of how css grids work, refer to the krad guide and bootstrap css grid documentation.
     * </p>
     *
     * @return
     */
    public CssGridSizes getCssGridSizes();

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getCssGridSizes()
     */
    public void setCssGridSizes(CssGridSizes cssGridSizes);

    /**
     * Context map for the component
     *
     * <p>
     * Any el statements configured for the components properties (e.g.
     * title="@{foo.property}") are evaluated using the el context map. This map
     * will get populated with default objects like the model, view, and request
     * from the {@code ViewHelperService}. Other components can push
     * further objects into the context so that they are available for use with
     * that component. For example, {@code Field} instances that are part
     * of a collection line as receive the current line instance
     * </p>
     *
     * <p>
     * Context map also provides objects to methods that are invoked for
     * {@code GeneratedField} instances
     * </p>
     *
     * <p>
     * The Map key gives the name of the variable that can be used within
     * expressions, and the Map value gives the object instance for which
     * expressions containing the variable should evaluate against
     * </p>
     *
     * <p>
     * NOTE: Calling getContext().putAll() will skip updating any configured property replacers for the
     * component. Instead you should call #pushAllToContextDeep
     * </p>
     *
     * @return Map<String, Object> context
     */
    Map<String, Object> getContext();

    /**
     * Setter for the context Map
     *
     * @param context
     */
    void setContext(Map<String, Object> context);

    /**
     * gets a list of {@code PropertyReplacer} instances
     *
     * <p>They will be evaluated
     * during the view lifecycle to conditionally set properties on the
     * {@code Component} based on expression evaluations</p>
     *
     * @return List<PropertyReplacer> replacers to evaluate
     */
    List<PropertyReplacer> getPropertyReplacers();

    /**
     * Setter for the components property substitutions
     *
     * @param propertyReplacers
     */
    void setPropertyReplacers(List<PropertyReplacer> propertyReplacers);

    /**
     * The options that are passed through to the Component renderer
     *
     * <p>
     * The Map key is the option name, with the Map value as the option value. See
     * documentation on the particular widget render for available options.
     * </p>
     *
     * @return Map<String, String> options
     */
    Map<String, String> getTemplateOptions();

    /**
     * Setter for the template's options
     *
     * @param templateOptions
     */
    void setTemplateOptions(Map<String, String> templateOptions);

    /**
     * Builds a string from the underlying <code>Map</code> of template options that will export that options as a
     * JavaScript Map for use in js and jQuery plugins
     *
     * <p>
     * See documentation on the particular component render for available options.
     * </p>
     *
     * @return String options
     */
    String getTemplateOptionsJSString();

    /**
     * Setter for the template's options
     *
     * @param templateOptionsJSString
     */
    void setTemplateOptionsJSString(String templateOptionsJSString);

    /**
     * Order of a component within a List of other components
     *
     * <p>Lower numbers are placed higher up in the list, while higher numbers are placed
     * lower in the list</p>
     *
     * @return int ordering number
     * @see org.springframework.core.Ordered#getOrder()
     */
    int getOrder();

    /**
     * Setter for the component's order
     *
     * @param order
     */
    void setOrder(int order);

    /**
     * The Tooltip widget that renders a tooltip with additional information about the element on
     * specified trigger event
     *
     * @return Tooltip
     */
    Tooltip getToolTip();

    /**
     * Setter for the component tooltip widget instance
     *
     * @param toolTip
     */
    void setToolTip(Tooltip toolTip);

    /**
     * String containing JavaScript code for registering event handlers for this component
     * (blur, focus, click, etc.)
     *
     * @return JS event handler script
     */
    public String getEventHandlerScript();

    /**
     * The name of the method that should be invoked for finalizing the component
     * configuration (full method name, without parameters or return type)
     *
     * <p>
     * Note the method can also be set with the finalizeMethodInvoker
     * targetMethod property. If the method is on the configured
     * {@code ViewHelperService}, only this property needs to be configured
     * </p>
     *
     * <p>
     * The model backing the view will be passed as the first argument method and then
     * the {@code Component} instance as the second argument. If any additional method
     * arguments are declared with the finalizeMethodAdditionalArguments, they will then
     * be passed in the order declared in the list
     * </p>
     *
     * <p>
     * If the component is selfRendered, the finalize method can return a string which
     * will be set as the component's renderOutput. The selfRendered indicator will also
     * be set to true on the component.
     * </p>
     *
     * @return String method name
     */
    String getFinalizeMethodToCall();

    /**
     * The List of Object instances that should be passed as arguments to the finalize method
     *
     * <p>
     * These arguments are passed to the finalize method after the standard model and component
     * arguments. They are passed in the order declared in the list
     * </p>
     *
     * @return List<Object> additional method arguments
     */
    List<Object> getFinalizeMethodAdditionalArguments();

    /**
     * {@code MethodInvokerConfig} instance for the method that should be invoked
     * for finalizing the component configuration
     *
     * <p>
     * MethodInvoker can be configured to specify the class or object the method
     * should be called on. For static method invocations, the targetClass
     * property can be configured. For object invocations, that targetObject
     * property can be configured
     * </p>
     *
     * <p>
     * If the component is selfRendered, the finalize method can return a string which
     * will be set as the component's renderOutput. The selfRendered indicator will also
     * be set to true on the component.
     * </p>
     *
     * @return MethodInvokerConfig instance
     */
    MethodInvokerConfig getFinalizeMethodInvoker();

    /**
     * Indicates whether the component contains its own render output (through
     * the renderOutput property)
     *
     * <p>
     * If self rendered is true, the corresponding template for the component
     * will not be invoked and the renderOutput String will be written to the
     * response as is.
     * </p>
     *
     * @return boolean true if component is self rendered, false if not (renders
     * through template)
     */
    boolean isSelfRendered();

    /**
     * Setter for the self render indicator
     *
     * @param selfRendered
     */
    void setSelfRendered(boolean selfRendered);

    /**
     * Rendering output for the component that will be sent as part of the
     * response (can contain static text and HTML)
     *
     * @return String render output
     */
    String getRenderedHtmlOutput();

    /**
     * Setter for the component's render output
     *
     * @param renderOutput
     */
    void setRenderedHtmlOutput(String renderOutput);

    /**
     * Disables the storage of the component in session (when the framework determines it needs to be due to a
     * refresh condition)
     *
     * <p>
     * When the framework determines there is a condition on the component that requires it to keep around between
     * posts, it will store the component instance in session. This flag can be set to disable this behavior (which
     * would require custom application logic to support behavior such as refresh)
     * </p>
     *
     * @return boolean true if the component should not be stored in session, false if session storage is allowed
     */
    boolean isDisableSessionPersistence();

    /**
     * Setter for disabling storage of the component in session
     *
     * @param disableSessionPersistence
     */
    void setDisableSessionPersistence(boolean disableSessionPersistence);

    /**
     * Indicates whether the component should be stored with the session view regardless of configuration
     *
     * <p>
     * By default the framework nulls out any components that do not have a refresh condition or are needed for
     * collection processing. This can be a problem if custom application code is written to refresh a component
     * without setting the corresponding component flag. In this case this property can be set to true to force the
     * framework to keep the component in session. Defaults to false
     * </p>
     *
     * @return boolean true if the component should be stored in session, false if not
     */
    boolean isForceSessionPersistence();

    /**
     * Setter for the indicator to force persistence of the component in session
     *
     * @param persistInSession
     */
    void setForceSessionPersistence(boolean persistInSession);

    /**
     * Security object that indicates what authorization (permissions) exist for the component
     *
     * @return ComponentSecurity instance
     */
    ComponentSecurity getComponentSecurity();

    /**
     * Setter for the components security object
     *
     * @param componentSecurity
     */
    void setComponentSecurity(ComponentSecurity componentSecurity);

    /**
     * Spring EL expression, which when evaluates to true, makes this component visible
     *
     * @return the SpEl expression string
     */
    String getProgressiveRender();

    /**
     * Setter for progressiveRender Spring EL expression
     *
     * @param progressiveRender the progressiveRender to set
     */
    void setProgressiveRender(String progressiveRender);

    /**
     * Spring EL expression, which when evaluates to true, causes this component to be refreshed
     *
     * @return the SpEl expression string
     */
    String getConditionalRefresh();

    /**
     * Setter for conditionalRefresh Spring EL expression
     *
     * @param conditionalRefresh the conditionalRefresh to set
     */
    void setConditionalRefresh(String conditionalRefresh);

    /**
     * List of control names (ids) extracted from {@link #getProgressiveRender()}
     *
     * @return the list of control names
     */
    List<String> getProgressiveDisclosureControlNames();

    /**
     * A JavaScript expression constructed from {@link #getConditionalRefresh()}
     *
     * <p>The script can be executed on the client to determine if the original exp was satisfied before
     * interacting with the server.</p>
     *
     * @return the JS script
     */
    String getProgressiveDisclosureConditionJs();

    /**
     * A JavaScript expression constructed from {@link #getProgressiveRender()}
     *
     * <p>The script can be executed on the client to determine if the original exp was satisfied before
     * interacting with the server.</p>
     *
     * @return the JS script
     */
    String getConditionalRefreshConditionJs();

    /**
     * The list of control names (ids) extracted from {@link #getConditionalRefresh()}
     *
     * @return the list of control names
     */
    List<String> getConditionalRefreshControlNames();

    /**
     * Indicates whether the component will be stored on the client, but hidden, after the first retrieval
     *
     * <p>
     * The component will not be rendered hidden after first retrieval if this flag is set to true. The component will
     * then be fetched via an ajax call when it should be rendered.
     * </p>
     *
     * @return the progressiveRenderViaAJAX
     */
    boolean isProgressiveRenderViaAJAX();

    /**
     * Setter for the progressiveRenderViaAJAX flag
     *
     * @param progressiveRenderViaAJAX the progressiveRenderViaAJAX to set
     */
    void setProgressiveRenderViaAJAX(boolean progressiveRenderViaAJAX);

    /**
     * Determines whether the component will always be retrieved from the server and shown
     *
     * <p>
     * If true, when the progressiveRender condition is satisfied, the component
     * will always be retrieved from the server and shown(as opposed to being
     * stored on the client, but hidden, after the first retrieval as is the
     * case with the progressiveRenderViaAJAX option). <b>By default, this is
     * false, so components with progressive render capabilities will always be
     * already within the client html and toggled to be hidden or visible.</b> </p>
     *
     * @return the progressiveRenderAndRefresh
     */
    boolean isProgressiveRenderAndRefresh();

    /**
     * Setter for the progressiveRenderAndRefresh flag
     *
     * @param progressiveRenderAndRefresh the progressiveRenderAndRefresh to set
     */
    void setProgressiveRenderAndRefresh(boolean progressiveRenderAndRefresh);

    /**
     * Specifies a property by name that when its value changes will automatically perform
     * a refresh on this component
     *
     * <p>
     * This can be a comma
     * separated list of multiple properties that require this component to be
     * refreshed when any of them change. <Br>DO NOT use with progressiveRender
     * unless it is know that progressiveRender condition will always be
     * satisfied before one of these fields can be changed.
     * </p>
     *
     * @return List property names that should trigger a refresh when their values change
     */
    List<String> getRefreshWhenChangedPropertyNames();

    /**
     * Setter for the list of property names that trigger a refresh
     *
     * @param refreshWhenChangedPropertyNames
     */
    void setRefreshWhenChangedPropertyNames(List<String> refreshWhenChangedPropertyNames);

    /**
     * Returns a list of componentIds which will be also be refreshed when this component is refreshed
     *
     * <p>
     * This will be a comma separated list of componentIds that need to be refreshed when a refresh
     * condition has been set on this component.
     * </p>
     *
     * @return List<String>
     */
    public List<String> getAdditionalComponentsToRefresh();

    /**
     * Setter for alsoRefreshComponents
     *
     * @param additionalComponentsToRefresh
     */
    public void setAdditionalComponentsToRefresh(List<String> additionalComponentsToRefresh);

    /**
     * Returns a string for representing the list of additional components to be refreshed as
     * a JavaScript value
     *
     * @return String representation of the list of componentIds for the components that need to be refreshed
     */
    public String getAdditionalComponentsToRefreshJs();

    /**
     * Indicates the component can be refreshed by an action
     *
     * <p>
     * This is set by the framework for configured ajax action buttons, should not be set in
     * configuration
     * </p>
     *
     * @return boolean true if the component is refreshed by an action, false if not
     */
    boolean isRefreshedByAction();

    /**
     * Setter for the refreshed by action indicator
     *
     * <p>
     * This is set by the framework for configured ajax action buttons, should not be set in
     * configuration
     * </p>
     *
     * @param refreshedByAction
     */
    void setRefreshedByAction(boolean refreshedByAction);

    /**
     * If true if this component is disclosed by an action in js, a placeholder will be put in this components place
     * if render is also false.
     *
     * @return true if this component is disclosed by an action
     */
    public boolean isDisclosedByAction();

    /**
     * Set if this component is disclosed by some outside action
     *
     * @param disclosedByAction
     */
    public void setDisclosedByAction(boolean disclosedByAction);

    /**
     * Indicates whether data contained within the component should be reset (set to default) when the
     * component is refreshed
     *
     * @return boolean true if data should be refreshed, false if data should remain as is
     */
    boolean isResetDataOnRefresh();

    /**
     * Setter for the reset data on refresh indicator
     *
     * @param resetDataOnRefresh
     */
    void setResetDataOnRefresh(boolean resetDataOnRefresh);

    /**
     * Time in seconds after which the component is automatically refreshed
     *
     * @return time in seconds
     */
    int getRefreshTimer();

    /**
     * Setter for refreshTimer
     *
     * @param refreshTimer
     */
    void setRefreshTimer(int refreshTimer);

    /**
     * The DataAttributes that will be written to the html element for this component as data-
     *
     * <p>They can be access through .data() call in jQuery.</p>
     *
     * @return map of data attributes, where key is data attribute name and the map value is the data
     * attribute value
     */
    Map<String, String> getDataAttributes();

    /**
     * Setter for data attributes to include for the component
     *
     * @param dataAttributes
     */
    void setDataAttributes(Map<String, String> dataAttributes);

    /**
     * Setter for script data attributes to include for the component
     *
     * @param dataAttributes
     */
    void setScriptDataAttributes(Map<String, String> dataAttributes);

    /**
     * The DataAttributes that will be written to the html as a script call to data for this component (these cannot be
     * used for jQuery selection directly)
     *
     * <p>They can be accessed through .data() call in jQuery.</p>
     *
     * @return map of data attributes, where key is data attribute name and the map value is the data
     * attribute value
     */
    Map<String, String> getScriptDataAttributes();

    /**
     * Add a data attribute to the dataAttributes map
     *
     * @param key
     * @param value
     */
    void addDataAttribute(String key, String value);

    /**
     * Add a script data attribute to the scriptDataAttributes map
     *
     * @param key
     * @param value
     */
    void addScriptDataAttribute(String key, String value);

    /**
     * The string that can be put into a the tag of a component to add data attributes inline
     *
     * @return html string for data attributes as html formatted element attributes
     */
    String getSimpleDataAttributes();

    /**
     * Returns a js string that can be used to right js data attributes to for the component
     *
     * @return html string for the js required to add the script data attributes
     */
    String getScriptDataAttributesJs();

    /**
     * The role attribute of this component, use to define aria roles
     *
     * @return the role attribute
     */
    String getRole();

    /**
     * @see Component#getRole()
     */
    void setRole(String role);

    /**
     * The aria attributes of this component and their values
     * (without "aria-", this is automatically appended during rendering)
     *
     * @return the aria attributes of this component
     */
    Map<String, String> getAriaAttributes();

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getAriaAttributes()
     */
    void setAriaAttributes(Map<String, String> ariaAttributes);

    /**
     * Add an aria attribute to the ariaAttributes list
     *
     * @param key the attribute (no "aria-" prefix)
     * @param value the attribute's value
     */
    void addAriaAttribute(String key, String value);

    /**
     * Get the aria attributes as a String that can be used during template output
     *
     * @return the aria attributes as a string
     */
    String getAriaAttributesAsString();

    /**
     * Validates different requirements of component compiling a series of reports detailing information on errors
     * found in the component.  Used by the RiceDictionaryValidator.
     *
     * @param tracer Record of component's location
     */
    void completeValidation(ValidationTrace tracer);

    /**
     * Raw html or string content to render before this component renders
     *
     * @return the preRenderContent string
     */
    public String getPreRenderContent();

    /**
     * Set the preRenderContent
     *
     * @param preRenderContent
     */
    public void setPreRenderContent(String preRenderContent);

    /**
     * Raw html or string content to render after this component renders
     *
     * @return the postRenderContent string
     */
    public String getPostRenderContent();

    /**
     * Set the postRenderContent
     *
     * @param postRenderContent
     */
    public void setPostRenderContent(String postRenderContent);

    /**
     * Gets the method to call on refresh.
     *
     * @return method to call
     */
    String getMethodToCallOnRefresh();

    /**
     * Limits the field data to send on a refresh methodToCall server call to the names/group id/field id
     * specified in this list.
     *
     * <p>The names in the list should be the propertyNames of the fields sent with this request.  A wildcard("*")
     * can be used at the END of a name to specify all fields with names that begin with the string
     * before the wildcard.  If the array contains 1 item with the keyword "NONE", then no form fields are sent.
     * In addition, a group id or field id with the "#" id selector prefix can be used to send all input data which
     * are nested within them. Note that this only limits the fields which exist on the form and data required
     * by the KRAD framework is still sent (eg, methodToCall, formKey, sessionId, etc.)</p>
     *
     * @return the only input fields to send by name with the action request
     */
    public List<String> getFieldsToSendOnRefresh();

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getFieldsToSendOnRefresh()
     */
    public void setFieldsToSendOnRefresh(List<String> fieldsToSendOnRefresh);

    /**
     * Gets a string representing all CSS style classes.
     *
     * @return string representation of CSS classes
     */
    String getStyleClassesAsString();

    /**
     * Names a model property path, which if set and resolves to true, indicates that this component
     * should be excluded from the lifecycle at the initialize phase.
     *
     * <p>
     * If prefixed with the '#' character, this path will be relative to the view's "pre-model"
     * context rather than the model.
     * </p>
     *
     * <p>
     * This property is superseded by {@link #getExcludeUnless()}; when both resolve to true, the
     * component will be included. When neither property is set, the component is unconditionally
     * included.
     * </p>
     *
     * @return model property path
     * @see ViewLifecycleUtils#isExcluded(Component)
     */
    String getExcludeIf();

    /**
     * Names a model property path, which if set and resolves to null or false, indicates that this
     * component should be excluded from the lifecycle at the initialize phase.
     *
     * <p>
     * If prefixed with the '#' character, this path will be relative to the view's "pre-model"
     * context rather than the model.
     * </p>
     *
     * <p>
     * This property supersedes {@link #getExcludeIf()}; when both resolve to true, the component
     * will be included.  When neither property is set, the component is unconditionally included.
     * </p>
     *
     * @return model property path
     * @see ViewLifecycleUtils#isExcluded(Component)
     */
    String getExcludeUnless();

    /**
     * Whether to omit fields from form post unless they are explicitly specified by the
     * {@link org.kuali.rice.krad.uif.element.Action#fieldsToSend} property.
     *
     * @return whether fields will be omitted from form post
     */
    boolean isOmitFromFormPost();

    /**
     * @see #isOmitFromFormPost()
     */
    void setOmitFromFormPost(boolean omitFromFormPost);


}
