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
package org.kuali.rice.krad.uif.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.widget.Help;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Content element that renders a header element and optionally a <code>Group</code> to
 * present along with the header text
 *
 * <p>
 * Generally the group is used to display content to the right of the header,
 * such as links for the group or other information
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "header", parent = "Uif-HeaderBase"),
        @BeanTag(name = "headerOne", parent = "Uif-HeaderOne"),
        @BeanTag(name = "headerTwo", parent = "Uif-HeaderTwo"),
        @BeanTag(name = "headerThree", parent = "Uif-HeaderThree"),
        @BeanTag(name = "headerFour", parent = "Uif-HeaderFour"),
        @BeanTag(name = "headerFive", parent = "Uif-HeaderFive"),
        @BeanTag(name = "headerSix", parent = "Uif-HeaderSix"),
        @BeanTag(name = "pageHeader", parent = "Uif-PageHeader"),
        @BeanTag(name = "sectionHeader", parent = "Uif-SectionHeader"),
        @BeanTag(name = "subSectionHeader", parent = "Uif-SubSectionHeader"),
        @BeanTag(name = "subCollectionHeader", parent = "Uif-SubCollectionHeader")})
public class Header extends ContentElementBase {
    private static final long serialVersionUID = -6950408292923393244L;

    private String headerText;
    private String headerLevel;

    private String headerTagStyle;
    private List<String> headerTagCssClasses;
    private boolean headerTagOnly;

    private Message richHeaderMessage;
    private List<Component> inlineComponents;

    private Group upperGroup;
    private Group rightGroup;
    private Group lowerGroup;

    private boolean renderWrapper;

    public Header() {
        super();

        headerTagCssClasses = new ArrayList<String>();
        renderWrapper = true;
    }

    /**
     * Sets up rich message content for the label, if any exists
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        if (richHeaderMessage == null && headerText != null && headerText.contains(
                KRADConstants.MessageParsing.LEFT_TOKEN) && headerText.contains(
                KRADConstants.MessageParsing.RIGHT_TOKEN)) {
            Message message = ComponentFactory.getMessage();
            message.setMessageText(headerText);
            message.setInlineComponents(inlineComponents);
            message.setRenderWrapperTag(false);
            
            this.setRichHeaderMessage(message);
        }
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Set render on header group to false if no items are configured</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        // don't render header groups if no items were configured
        if ((getUpperGroup() != null) && (getUpperGroup().getItems().isEmpty())) {
            getUpperGroup().setRender(false);
        }

        if ((getRightGroup() != null) && (getRightGroup().getItems().isEmpty())) {
            getRightGroup().setRender(false);
        }

        if ((getLowerGroup() != null) && (getLowerGroup().getItems().isEmpty())) {
            getLowerGroup().setRender(false);
        }

        //add preset styles to header groups
        if (getUpperGroup() != null) {
            getUpperGroup().addStyleClass("uif-header-upperGroup");
        }

        if (getRightGroup() != null) {
            getRightGroup().addStyleClass("uif-header-rightGroup");
        }

        if (getLowerGroup() != null) {
            getLowerGroup().addStyleClass("uif-header-lowerGroup");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAdditionalTemplates() {
        List<String> additionalTemplates = super.getAdditionalTemplates();

        Object parent = getContext().get(UifConstants.ContextVariableNames.PARENT);
        
        Help help = null;
        if (parent instanceof Group) {
            help = ((Group) parent).getHelp();
        } else if (parent instanceof View) {
            help = ((View) parent).getHelp();
        }
        
        if (help != null) {
            String helpTemplate = help.getTemplate();
            if (additionalTemplates.isEmpty()) {
                additionalTemplates = new ArrayList<String>();
            }
            additionalTemplates.add(helpTemplate);
        }
        
        return additionalTemplates;
    }

    /**
     * Text that should be displayed on the header
     *
     * @return header text
     */
    @BeanTagAttribute
    public String getHeaderText() {
        return this.headerText;
    }

    /**
     * Setter for the header text
     *
     * @param headerText
     */
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    /**
     * HTML header level (h1 ... h6) that should be applied to the header text
     *
     * @return header level
     */
    @BeanTagAttribute
    public String getHeaderLevel() {
        return this.headerLevel;
    }

    /**
     * Setter for the header level
     *
     * @param headerLevel
     */
    public void setHeaderLevel(String headerLevel) {
        this.headerLevel = headerLevel;
    }

    /**
     * Style classes that should be applied to the header text (h tag)
     *
     * <p>
     * Note the style class given here applies to only the header text. The
     * style class property inherited from the <code>Component</code> interface
     * can be used to set the class for the whole field div (which could
     * include a nested <code>Group</code>)
     * </p>
     *
     * @return list of style classes
     * @see org.kuali.rice.krad.uif.component.Component#getCssClasses()
     */
    @BeanTagAttribute
    public List<String> getHeaderTagCssClasses() {
        return this.headerTagCssClasses;
    }

    /**
     * Setter for the list of classes to apply to the header h tag
     *
     * @param headerTagCssClasses
     */
    public void setHeaderTagCssClasses(List<String> headerTagCssClasses) {
        this.headerTagCssClasses = headerTagCssClasses;
    }

    /**
     * Builds the HTML class attribute string by combining the headerStyleClasses list
     * with a space delimiter
     *
     * @return class attribute string
     */
    public String getHeaderStyleClassesAsString() {
        if (headerTagCssClasses != null) {
            return StringUtils.join(headerTagCssClasses, " ");
        }

        return "";
    }

    /**
     * Style that should be applied to the header h tag
     *
     * <p>
     * Note the style given here applies to only the header text. The style
     * property inherited from the <code>Component</code> interface can be used
     * to set the style for the whole header div (which could include a nested
     * <code>Group</code>)
     * </p>
     *
     * @return header style
     * @see org.kuali.rice.krad.uif.component.Component#getStyle()
     */
    @BeanTagAttribute
    public String getHeaderTagStyle() {
        return this.headerTagStyle;
    }

    /**
     * Setter for the header h tag style
     *
     * @param headerTagStyle
     */
    public void setHeaderTagStyle(String headerTagStyle) {
        this.headerTagStyle = headerTagStyle;
    }

    @BeanTagAttribute
    public boolean isHeaderTagOnly() {
        return headerTagOnly;
    }

    public void setHeaderTagOnly(boolean headerTagOnly) {
        this.headerTagOnly = headerTagOnly;
    }

    /**
     * Nested group instance that can be used to render contents above the header text
     *
     * <p>
     * The header group is useful for adding content such as links or actions that is presented with the header
     * </p>
     *
     * @return Group instance
     */
    @BeanTagAttribute
    public Group getUpperGroup() {
        return upperGroup;
    }

    /**
     * Setter for the header group instance that is rendered above the header text
     *
     * @param upperGroup
     */
    public void setUpperGroup(Group upperGroup) {
        this.upperGroup = upperGroup;
    }

    /**
     * Nested group instance that can be used to render contents to the right of the header text
     *
     * <p>
     * The header group is useful for adding content such as links or actions that is presented with the header
     * </p>
     *
     * @return Group instance
     */
    @BeanTagAttribute
    public Group getRightGroup() {
        return rightGroup;
    }

    /**
     * Setter for the header group instance that is rendered to the right of the header text
     *
     * @param rightGroup
     */
    public void setRightGroup(Group rightGroup) {
        this.rightGroup = rightGroup;
    }

    /**
     * Nested group instance that can be used to render contents below the header text
     *
     * <p>
     * The header group is useful for adding content such as links or actions that is presented with the header
     * </p>
     *
     * @return Group instance
     */
    @BeanTagAttribute
    public Group getLowerGroup() {
        return lowerGroup;
    }

    /**
     * Setter for the header group instance that is rendered below the header text
     *
     * @param lowerGroup
     */
    public void setLowerGroup(Group lowerGroup) {
        this.lowerGroup = lowerGroup;
    }

    /**
     * List of <code>Component</code> instances contained in the lower header group
     *
     * <p>
     * Convenience method for configuration to get the items List from the
     * lower header group
     * </p>
     *
     * @return List<? extends Component> items
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public List<? extends Component> getItems() {
        if (lowerGroup != null) {
            return lowerGroup.getItems();
        }

        return null;
    }

    /**
     * Setter for the lower group's items
     *
     * <p>
     * Convenience method for configuration to set the items List for the
     * lower header group
     * </p>
     *
     * @param items
     */
    public void setItems(List<? extends Component> items) {
        if (lowerGroup != null) {
            lowerGroup.setItems(items);
        }
    }

    /**
     * Gets the Message that represents the rich message content of the header if headerText is using rich message
     * tags.
     * <b>DO NOT set this
     * property directly unless you need full control over the message structure.</b>
     *
     * @return rich message structure, null if no rich message structure
     */
    @BeanTagAttribute
    public Message getRichHeaderMessage() {
        return richHeaderMessage;
    }

    /**
     * Sets the Message that represents the rich message content of the header if headerText is using rich message
     * tags.
     * <b>DO
     * NOT set this
     * property directly unless you need full control over the message structure.</b>
     *
     * @param richHeaderMessage
     */
    public void setRichHeaderMessage(Message richHeaderMessage) {
        this.richHeaderMessage = richHeaderMessage;
    }

    /**
     * Gets the inlineComponents used by index in a Header that has rich message component index tags in its headerText
     *
     * @return the Label's inlineComponents
     */
    @BeanTagAttribute
    public List<Component> getInlineComponents() {
        return inlineComponents;
    }

    /**
     * Sets the inlineComponents used by index in a Header that has rich message component index tags in its headerText
     *
     * @param inlineComponents
     */
    public void setInlineComponents(List<Component> inlineComponents) {
        this.inlineComponents = inlineComponents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that a correct header level is set
        String headerLevel = getHeaderLevel().toUpperCase();
        boolean correctHeaderLevel = false;
        if (headerLevel.compareTo("H1") == 0) {
            correctHeaderLevel = true;
        } else if (headerLevel.compareTo("H2") == 0) {
            correctHeaderLevel = true;
        } else if (headerLevel.compareTo("H3") == 0) {
            correctHeaderLevel = true;
        } else if (headerLevel.compareTo("H4") == 0) {
            correctHeaderLevel = true;
        } else if (headerLevel.compareTo("H5") == 0) {
            correctHeaderLevel = true;
        } else if (headerLevel.compareTo("H6") == 0) {
            correctHeaderLevel = true;
        } else if (headerLevel.compareTo("LABEL") == 0) {
            correctHeaderLevel = true;
        }
        if (!correctHeaderLevel) {
            String currentValues[] = {"headerLevel =" + getHeaderLevel()};
            tracer.createError("HeaderLevel must be of values h1, h2, h3, h4, h5, h6, or label", currentValues);
        }

        // Checks that header text is set
        if (getHeaderText() == null) {
            if (!Validator.checkExpressions(this, "headerText")) {
                String currentValues[] = {"headertText =" + getHeaderText()};
                tracer.createWarning("HeaderText should be set", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
