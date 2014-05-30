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
package org.kuali.rice.krad.uif.field;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Field wrapper for a Message.
 *
 * <p>
 * The <code>Message</code> is used to display static text in the user
 * interface
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "messageField", parent = "Uif-MessageField")
public class MessageField extends FieldBase {
    private static final long serialVersionUID = -7045208136391722063L;

    private Message message;

    public MessageField() {
        super();
    }

    /**
     * PerformFinalize override - calls super, corrects the field's Label for attribute to point to this field's content
     *
     * @param model the model
     * @param parent the parent component
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        //determine what id to use for the for attribute of the label, if present
        if(this.getFieldLabel() != null && this.getMessage() != null
                && StringUtils.isNotBlank(this.getMessage().getId())){

            if(this.getMessage().getMessageComponentStructure() != null
                    && !this.getMessage().getMessageComponentStructure().isEmpty()){
                //wrapper will be a rich message div - no suffix
                this.getFieldLabel().setLabelForComponentId(this.getMessage().getId());
            }
            else{
                //wrapper will be a normal message span - add suffix
                this.getFieldLabel().setLabelForComponentId(this.getMessage().getId() + UifConstants.IdSuffixes.SPAN);
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Message#getMessageText()
     */
    @BeanTagAttribute
    public String getMessageText() {
        if (message != null) {
            return message.getMessageText();
        }

        return null;
    }

    /**
     * @see MessageField#getMessageText()
     */
    public void setMessageText(String messageText) {
        if (message != null) {
            message.setMessageText(messageText);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Message#getInlineComponents()
     * @return
     */
    @BeanTagAttribute
    public List<Component> getInlineComponents() {
        if (message != null) {
            return message.getInlineComponents();
        }

        return null;
    }

    /**
     * @see MessageField#getInlineComponents()
     */
    public void setInlineComponents(List<Component> inlineComponents) {
        if (message != null) {
            message.setInlineComponents(inlineComponents);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Message#getMessageText()
     */
    @BeanTagAttribute(type= BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Message getMessage() {
        return message;
    }

    /**
     * @see MessageField#getMessage()
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer){
        tracer.addBean(this);

        // Checks that the message is set
        if(getMessage()==null){
            if(Validator.checkExpressions(this, "message")){
                String currentValues [] = {"message ="+getMessage()};
                tracer.createWarning("Message should not be null",currentValues);
            }
        }

        // Checks that the label is set
        if(getLabel()==null){
            if(Validator.checkExpressions(this, "label")){
                String currentValues [] = {"label ="+getLabel(),"Message ="+getMessage()};
                tracer.createWarning("Label is null, message should be used instead",currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
