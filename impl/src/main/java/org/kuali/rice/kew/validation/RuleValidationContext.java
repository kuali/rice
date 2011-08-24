/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.validation;

import javassist.SerialVersionUID;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.kew.api.rule.RuleContract;
import org.kuali.rice.kew.api.rule.RuleDelegationContract;
import org.kuali.rice.kew.api.rule.RuleResponsibility;
import org.kuali.rice.kew.api.rule.RuleResponsibilityContract;
import org.kuali.rice.kew.api.validation.RuleValidationContextContract;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.krad.UserSession;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The RuleValidationContext represents the context under which to validate a Rule which is being entered
 * into the system, be it through the web-based Rule GUI or via an XML import.
 * 
 * The ruleAuthor is the UserSession of the individual who is entering or editing the rule.  This may
 * be <code>null</code> if the rule is being run through validation from the context of an XML rule
 * import.
 * 
 * The RuleDelegation represents the pointer to the rule from it's parent rule's RuleResponsibility.
 * This will be <code>null</code> if the rule being entered is not a delegation rule.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = RuleValidationContext.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RuleValidationContext.Constants.TYPE_NAME, propOrder = {
    RuleValidationContext.Elements.RULE,
    RuleValidationContext.Elements.RULE_DELEGATION,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class RuleValidationContext
    extends AbstractDataTransferObject
    implements RuleValidationContextContract {

    @XmlElement(name = Elements.RULE, required = true)
	private final RuleContract rule;
    @XmlElement(name = Elements.RULE_DELEGATION, required = true)
	private final RuleDelegationContract ruleDelegation;
    //@XmlElement(name = Elements.RULE_AUTHOR, required = false)
	private final UserSession ruleAuthor;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private RuleValidationContext() {
        this.rule = null;
        this.ruleDelegation = null;
        this.ruleAuthor = null;
    }

    private RuleValidationContext(Builder builder) {
        this.rule = builder.getRule();
        this.ruleDelegation = builder.getRuleDelegation();
        this.ruleAuthor = null;
    }

	/**
	 * Construct a RuleValidationContext under which to validate a rule.  The rule must be non-null, the delegation
	 * and author can be <code>null</code> given the circumstances defined in the description of this class.
     * @deprecated use Builder instead
	 */
	public RuleValidationContext(RuleContract rule, RuleDelegationContract ruleDelegation, UserSession ruleAuthor) {
		this.ruleAuthor = ruleAuthor;
		this.rule = rule;
		this.ruleDelegation = ruleDelegation;
	}

	/**
	 * Retrieve the rule which is being validated.
	 */
    @Override
	public RuleContract getRule() {
		return rule;
	}

	/**
	 * Retrieve the UserSession of the individual entering the rule into the system.  May be null in the
	 * case of an XML rule import. 
	 */
	public UserSession getRuleAuthor() {
		return ruleAuthor;
	}

	/**
	 * Retrieve the RuleDelegation representing the parent of the rule being validated.  If the rule is
	 * not a delegation rule, then this will return null;
	 */
    @Override
	public RuleDelegationContract getRuleDelegation() {
		return ruleDelegation;
	}

    /**
     * A builder which can be used to construct {@link RuleValidationContext} instances.  Enforces the constraints of the {@link RuleValidationContextContract}.
     *
     */
    public final static class Builder
        implements Serializable, ModelBuilder, RuleValidationContextContract
    {

        private RuleContract rule;
	    private RuleDelegationContract ruleDelegation;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(RuleValidationContextContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setRule(contract.getRule());
            builder.setRuleDelegation(contract.getRuleDelegation());
            return builder;
        }

        public RuleValidationContext build() {
            return new RuleValidationContext(this);
        }

        @Override
        public RuleContract getRule() {
            return this.rule;
        }

        @Override
        public RuleDelegationContract getRuleDelegation() {
            return this.ruleDelegation;
        }

        public void setRule(RuleContract rule) {
            this.rule = rule;
        }

        public void setRuleDelegation(RuleDelegationContract ruleDelegation) {
            this.ruleDelegation = ruleDelegation;
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "ruleValidationContext";
        final static String TYPE_NAME = "RuleValidationContextType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String RULE = "rule";
        final static String RULE_DELEGATION = "ruleDelegation";
        //final static String RULE_AUTHOR = "ruleAuthor";
    }
}