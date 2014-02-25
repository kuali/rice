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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Manages the rendering of <code>Component</code> instances within a
 * <code>Container</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LayoutManager extends UifDictionaryBean, LifecycleElement, Serializable {

	/**
	 * The path to the JSP file that should be called to invoke the layout
	 * manager
	 *
	 * <p>
	 * The path should be relative to the web root. All layout manager templates
	 * receive the list of items of be placed, the configured layout manager,
	 * and the container to which the layout manager applies
	 * </p>
	 *
	 * <p>
	 * e.g. '/krad/WEB-INF/jsp/tiles/boxLayout.jsp'
	 * </p>
	 *
	 * @return String representing the template path
	 */
	public String getTemplate();

	/**
	 * Setter for the layout managers template
	 *
	 * @param template
	 */
	public void setTemplate(String template);

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
     * e.g. 'uif_grid'
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
	 * Determines what <code>Container</code> classes are supported by the
	 * <code>LayoutManager</code>
	 *
	 * @return Class<? extends Container> container class supported
	 */
	public Class<? extends Container> getSupportedContainer();

	/**
	 * CSS style string to be applied to the area (div) the layout manager
	 * generates for the items
	 *
	 * <p>
	 * Note the styleClass/style configured on the <code>Container</code>
	 * applies to all the container content (header, body, footer), while the
	 * styleClass/style configured on the <code>LayoutManager</code> only
	 * applies to the div surrounding the items placed by the manager (the
	 * container's body)
	 * </p>
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
	public String getStyle();

	/**
	 * Setter for the layout manager div style
	 *
	 * @param style
	 */
	public void setStyle(String style);

    public List<String> getLibraryCssClasses();

    public void setLibraryCssClasses(List<String> libraryClasses);

	/**
	 * CSS style class(s) to be applied to the area (div) the layout manager
	 * generates for the items
	 *
	 * <p>
	 * Note the styleClass/style configured on the <code>Container</code>
	 * applies to all the container content (header, body, footer), while the
	 * styleClass/style configured on the <code>LayoutManager</code> only
	 * applies to the div surrounding the items placed by the manager (the
	 * container's body)
	 * </p>
	 *
	 * <p>
	 * Declares additional style classes for the div. Multiple classes are
	 * specified with a space delimiter. This is used by the renderer to set the
	 * class attribute on the corresponding element. The class(s) declared must
	 * be available in the common style sheets or the style sheets specified for
	 * the view
	 * </p>
	 *
	 * <p>
	 * e.g. 'header left'
	 * </p>
	 *
	 * @return List<String> css style classes to apply
	 */
	public List<String> getCssClasses();

	/**
	 * Setter for the layout manager div style class
	 *
	 * @param styleClasses
	 */
	public void setCssClasses(List<String> styleClasses);

    public List<String> getAdditionalCssClasses();

    public void setAdditionalCssClasses(List<String> libraryClasses);

	/**
	 * This method adds a single style class to the list of css style classes on this component
	 *
	 * @param styleClass
	 */
	public void addStyleClass(String styleClass);

    /**
     * Appends to the inline style set on this layoutManager
     *
     * @param styleRules
     */
    public void appendToStyle(String styleRules);

	/**
	 * List of <code>PropertyReplacer</code> instances that will be
	 * evaluated during the view lifecycle to conditional set properties on the
	 * <code>LayoutManager</code> based on expression evaluations
	 *
	 * @return List<PropertyReplacer> replacers to evaluate
	 */
	public List<PropertyReplacer> getPropertyReplacers();

	/**
	 * Setter for the layout managers property substitutions
	 *
	 * @param propertyReplacers
	 */
	public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers);

}
