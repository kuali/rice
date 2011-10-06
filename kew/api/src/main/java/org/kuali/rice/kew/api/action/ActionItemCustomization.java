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
package org.kuali.rice.kew.api.action;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.kew.api.action.ActionItem.Elements;
import org.kuali.rice.kew.api.actionlist.DisplayParameters;
import org.w3c.dom.Element;

@XmlRootElement(name = ActionItemCustomization.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ActionItemCustomization.Constants.TYPE_NAME, propOrder = {
        ActionItemCustomization.Elements.ID,
        ActionItemCustomization.Elements.ACTION_SET,
        ActionItemCustomization.Elements.DISPLAY_PARAMETERS,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class ActionItemCustomization extends AbstractDataTransferObject implements ActionItemCustomizationContract {

    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlElement(name = Elements.ACTION_SET, required = true)
    private final ActionSet actionSet;
    @XmlElement(name = Elements.DISPLAY_PARAMETERS, required = true)
    private final DisplayParameters displayParameters;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
    
    /**
     * Private constructor used only by JAXB.
     * 
     */
    private ActionItemCustomization() {
        this.id = null;
        this.actionSet = null;
        this.displayParameters = null;
    }
    
    private ActionItemCustomization(Builder builder) {
        this.id = builder.getId();
        this.actionSet = builder.getActionSet();
        this.displayParameters = builder.getDisplayParameters();
    }

    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public ActionSet getActionSet() {
        return this.actionSet;
    }

    @Override
    public DisplayParameters getDisplayParameters() {
        return this.displayParameters;
    }
    
    /**
     * A builder which can be used to construct {@link ActionItemCustomization} instances.  
     * Enforces the constraints of the {@link ActionItemCustomizationContract}. 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, ActionItemCustomizationContract
    {
        
        private String id;        
        private ActionSet actionSet;        
        private DisplayParameters displayParameters;
        
        private Builder(ActionSet actionSet, DisplayParameters displayParameters) {
            setActionSet(actionSet);
            setDisplayParameters(displayParameters);
        }
        
        public static Builder create(ActionSet actionSet, DisplayParameters displayParameters) {
            return new Builder(actionSet, displayParameters);
        }
        
        public static Builder create(ActionItemCustomizationContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
            Builder builder = create(contract.getActionSet(), contract.getDisplayParameters());
            builder.setId(contract.getId());
            return builder;
        }
                    
        @Override
        public ActionItemCustomization build() {
            return new ActionItemCustomization(this);
        }

        @Override
        public ActionSet getActionSet() {
            return this.actionSet;
        }

        public DisplayParameters getDisplayParameters() {
            return this.displayParameters;
        }

        public String getId() {
            return this.id;
        }
        
        public void setId(String id) {
            if (StringUtils.isWhitespace(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }
        
        public void setActionSet(ActionSet actionSet) {
            if (actionSet == null) {
                throw new IllegalArgumentException("actionSet is null");
            }
            this.actionSet = actionSet;
        }
        
        public void setDisplayParameters(DisplayParameters displayParameters) {
            if (displayParameters == null) {
                throw new IllegalArgumentException("displayParameters is null");
            }
            this.displayParameters = displayParameters;
        }
    }    
    
    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "actionItemCustomization";
        final static String TYPE_NAME = "ActionItemCustomizationType";
    }

    /**
      * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
      * 
      */
    static class Elements {
        final static String ID = "id";
        final static String ACTION_SET = "actionSet";
        final static String DISPLAY_PARAMETERS = "displayParameters";
    }
}
