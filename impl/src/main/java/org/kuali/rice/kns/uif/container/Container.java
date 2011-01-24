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
package org.kuali.rice.kns.uif.container;

import java.util.List;
import java.util.Set;

import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.field.ErrorsField;
import org.kuali.rice.kns.uif.field.HeaderField;
import org.kuali.rice.kns.uif.field.MessageField;
import org.kuali.rice.kns.uif.layout.LayoutManager;
import org.kuali.rice.kns.uif.widget.Help;

/**
 * Type of component that contains a collection of other components. All
 * templates for <code> Container</code> components must use a
 * <code>LayoutManager</code> to render the contained components. Each container
 * has the following parts in addition to the contained components:
 * <ul>
 * <li><code>HeaderField</code></li>
 * <li>Summary <code>MessageField</code></li>
 * <li>Help component</li>
 * <li>Errors container</li>
 * <li>Footer <code>Group</code></li>
 * </ul>
 * Container implementations are free to add additional content as needed.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 * @see org.kuali.rice.kns.uif.Component
 */
public interface Container extends Component {

	/**
	 * <code>List</code> of <code>Component</code> instances that are held by
	 * the container
	 * <p>
	 * Contained components are rendered within the section template by calling
	 * the associated <code>LayoutManager</code>
	 * </p>
	 * 
	 * @return List component instances
	 */
	public List<? extends Component> getItems();

	/**
	 * <code>Set</code> of <code>Component</code> classes that may be placed
	 * into the container
	 * <p>
	 * If an empty or null list is returned, it is assumed the container
	 * supports all components. The returned set will be used by dictionary
	 * validators and allows renders to make assumptions about the contained
	 * components
	 * </p>
	 * 
	 * @return Set component classes
	 */
	public Set<Class<? extends Component>> getSupportedComponents();

	/**
	 * <code>LayoutManager</code> that should be used to layout the components
	 * in the container
	 * <p>
	 * The template associated with the layout manager will be invoked passing
	 * in the List of components from the container. This list is exported under
	 * the attribute name 'items'
	 * </p>
	 * 
	 * @return LayoutManager instance
	 */
	public LayoutManager getLayoutManager();

	/**
	 * <code>HeaderField</code> associated with the container
	 * <p>
	 * Header fields are generally rendered at the beginning of the container to
	 * indicate a grouping, although this is determined by the template
	 * associated with the container. The actual rendering configuration (style
	 * and so on) is configured within the HeaderField instance
	 * </p>
	 * <p>
	 * Header is only rendered if <code>Container#isRenderHeader</code> is true
	 * and getHeader() is not null
	 * </p>
	 * 
	 * @return HeaderField instance or Null
	 */
	public HeaderField getHeader();

	/**
	 * Indicates whether the <code>HeaderField</code> associated with the
	 * <code>Container</code> should be rendered
	 * <p>
	 * For nested groups (like Field Groups) it is often necessary to only show
	 * the container body (the contained components). This method allows the
	 * header to not be displayed
	 * </p>
	 * 
	 * @return boolean true if the header should be rendered, false if it should
	 *         not be
	 */
	public boolean isRenderHeader();

	/**
	 * Footer <code>Group</code> associated with the container
	 * <p>
	 * The footer is usually rendered at the end of the container. Often this is
	 * a place to put actions (buttons) for the container.
	 * </p>
	 * <p>
	 * Footer is only rendered if <code>Container#isRenderFooter</code> is true
	 * and getFooter is not null
	 * </p>
	 * 
	 * @return Group footer instance or Null
	 */
	public Group getFooter();

	/**
	 * Indicates whether the footer associated with the <code>Container</code>
	 * should be rendered
	 * <p>
	 * For nested groups it is often necessary to only show the container body
	 * (the contained components). This method allows the footer to not be
	 * displayed
	 * </p>
	 * 
	 * @return boolean true if the footer should be rendered, false if it should
	 *         not be
	 */
	public boolean isRenderFooter();

	/**
	 * Text for the container that provides a summary description or
	 * instructions
	 * <p>
	 * Text is encapsulated in a <code>MessageField</code> that contains
	 * rendering configuration.
	 * </p>
	 * <p>
	 * Summary <code>MessageField</code> only rendered if this methods does not
	 * return null
	 * </p>
	 * 
	 * @return MessageField instance or Null
	 */
	public MessageField getSummaryMessageField();

	/**
	 * Field that contains the error messages for the container
	 * <p>
	 * Containers can collect the errors for the contained component and display
	 * either all the messages or counts. This <code>Field</code> is used to
	 * render those messages. Styling and other configuration is done through
	 * the <code>ErrorsField</code>
	 * </p>
	 * 
	 * @return ErrorsField holding the container errors
	 */
	public ErrorsField getErrorsField();

	/**
	 * Help configuration object for the container
	 * <p>
	 * External help information can be configured for the container. The
	 * <code>Help</code> object can the configuration for rendering a link to
	 * that help information.
	 * </p>
	 * 
	 * @return Help for container
	 */
	public Help getHelp();

}
