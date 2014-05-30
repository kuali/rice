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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ErrorReport;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.widget.LightBox;

/**
 * Content element that renders a link
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "link", parent="Uif-Link")
public class Link extends ContentElementBase {
    private static final long serialVersionUID = 8989868231938336068L;

    private String linkText;
    private String target;
    private String href;

    private String iconClass;
    private String linkIconPlacement;

    private boolean openInLightbox;

    private LightBox lightBox;

    public Link() {
        super();
        linkIconPlacement = UifConstants.Position.LEFT.name();
    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li>Initialize the nested lightBox widget if open in lightbox is true</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        if (openInLightbox && (lightBox == null)) {
            lightBox = ComponentFactory.getLightBox();
        }
    }

    /**
     * Special handling for lightbox links to add and onclick data attribute to be handled by a global handler
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        MessageService messageService = KRADServiceLocatorWeb.getMessageService();

        if (lightBox != null && lightBox.isRender()){
            this.addDataAttribute(UifConstants.DataAttributes.ONCLICK, "handleLightboxOpen(jQuery(this), " +
                    lightBox.getTemplateOptionsJSString() + ", " + lightBox.isAddAppParms() + ", e);");
            this.addDataAttribute(UifConstants.DataAttributes.ROLE, UifConstants.RoleTypes.ACTION);
            lightBox.setRender(false);
        }

        // when icon only is set, add the icon class to the action
        if (StringUtils.isNotBlank(iconClass) && (UifConstants.ICON_ONLY_PLACEMENT.equals(linkIconPlacement)
                || StringUtils.isBlank(linkText))) {
            getCssClasses().add(iconClass);

            // force icon only placement
            linkIconPlacement = UifConstants.ICON_ONLY_PLACEMENT;
        }

        if (target.equals(UifConstants.HtmlAttributeValues.TARGET_BLANK)) {
            String title = this.getTitle();
            if (StringUtils.isNotBlank(title)) {
                this.setTitle(title + " - " + messageService.getMessageText("accessibility.link.opensTab"));
            }
            else{
                this.setTitle(messageService.getMessageText("accessibility.link.opensTab"));
            }
        }
    }

    /**
     * Returns the label of the link
     *
     * @return The link label
     */
    @BeanTagAttribute
    public String getLinkText() {
        return linkText;
    }

    /**
     * Setter for the link label
     *
     * @param linkText
     */
    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    /**
     * Returns the target that will be used to specify where to open the href
     *
     * @return The target
     */
    @BeanTagAttribute
    public String getTarget() {
        return target;
    }

    /**
     * Setter for the link target
     *
     * @param target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Returns the href text
     *
     * @return The href text
     */
    @BeanTagAttribute
    public String getHref() {
        return href;
    }

    /**
     * Setter for the hrefText
     *
     * @param href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Indicates whether the link URL should be opened in a lightbox
     *
     * <p>
     * If set the target attribute is ignored and the URL is opened in a lightbox instead
     * </p>
     *
     * @return true to open link in a lightbox, false if not (follow standard target attribute)
     */
    public boolean isOpenInLightbox() {
        return openInLightbox;
    }

    /**
     * Setter that indicates whether the link should be opened in a lightbox
     *
     * @param openInLightbox
     */
    public void setOpenInLightbox(boolean openInLightbox) {
        this.openInLightbox = openInLightbox;
    }

    /**
     * Returns the <code>LightBox</code> used to open the link in
     *
     * @return The <code>LightBox</code>
     */
    @BeanTagAttribute(type= BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public LightBox getLightBox() {
        return lightBox;
    }

    /**
     * Setter for the lightBox
     *
     * @param lightBox
     */
    public void setLightBox(LightBox lightBox) {
        this.lightBox = lightBox;
    }

    /**
     * Icon Class for the link
     *
     * <p>
     * Bootstrap Icon Class to be rendered on this Link
     * </p>
     *
     * @return label for action
     */
    @BeanTagAttribute
    public String getIconClass() {
        return iconClass;
    }

    /**
     * Setter for the Icon Class
     *
     * @param iconClass
     */
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    /**
     * Set to LEFT, RIGHT to position image at that location within the button. When set to blank/null/ICON_ONLY, the icon
     * itself will be the Action, if no value is set the default is ALWAYS LEFT, you must explicitly set
     * blank/null/ICON_ONLY to use ONLY the image as the Action.
     *
     * @return Action Icon Placement
     */
    @BeanTagAttribute
    public String getLinkIconPlacement() {
        return linkIconPlacement;
    }

    /**
     * Setter for the Link Icon Placement
     *
     * @param linkIconPlacement
     */
    public void setLinkIconPlacement(String linkIconPlacement) {
        this.linkIconPlacement = linkIconPlacement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer){
        ArrayList<ErrorReport> reports=new ArrayList<ErrorReport>();
        tracer.addBean(this);

        if(tracer.getValidationStage()== ValidationTrace.BUILD){

            // Checks that href is set
            if(getHref()==null){
                if(!Validator.checkExpressions(this, "href")){
                    String currentValues [] = {"href ="+getHref()};
                    tracer.createError("Href must be set",currentValues);
                }
            }

            // Checks that the text is set
            if(getLinkText()==null){
                if(!Validator.checkExpressions(this, "linkText")){
                    String currentValues [] = {"linkText = "+getLinkText()};
                    tracer.createError("LinkText must be set",currentValues);
                }
            }

        }

        super.completeValidation(tracer.getCopy());
    }
}
