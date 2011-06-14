package org.kuali.rice.kew.api.document.actions;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

@XmlRootElement(name = ValidActions.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ValidActions.Constants.TYPE_NAME, propOrder = {
    ValidActions.Elements.VALID_ACTION_CODES,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ValidActions implements ModelObjectComplete, ValidActionsContract {

	private static final long serialVersionUID = 8074175291030982905L;

	@XmlElement(name = Elements.VALID_ACTION_CODE, required = false)
    private final Set<String> validActionCodes;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private ValidActions() {
        this.validActionCodes = null;
    }

    private ValidActions(Builder builder) {
        Set<ActionType> validActions = builder.getValidActions();
        Set<String> validActionCodes = new HashSet<String>();
        for (ActionType actionType : validActions) {
        	validActionCodes.add(actionType.getCode());
        }
        this.validActionCodes = validActionCodes;
    }

    @Override
    public Set<ActionType> getValidActions() {
    	if (validActionCodes == null) {
    		return Collections.emptySet();
    	}
    	Set<ActionType> validActions = new HashSet<ActionType>();
    	for (String validActionCode : validActionCodes) {
    		// ignore action codes that we don't understand because they could be coming from a later version of KEW
    		ActionType actionType = ActionType.fromCode(validActionCode, true);
    		if (actionType != null) {
    			validActions.add(actionType);
    		}
    	}
        return Collections.unmodifiableSet(validActions);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    /**
     * A builder which can be used to construct {@link ValidActions} instances.  Enforces the constraints of the {@link ValidActionsContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, ValidActionsContract {

		private static final long serialVersionUID = -3227993220281961077L;

		private Set<ActionType> validActions;

        private Builder() {
        	setValidActions(new HashSet<ActionType>());
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(ValidActionsContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setValidActions(contract.getValidActions());
            return builder;
        }

        public ValidActions build() {
            return new ValidActions(this);
        }

        @Override
        public Set<ActionType> getValidActions() {
            return this.validActions;
        }

        public void setValidActions(Set<ActionType> validActions) {
        	if (validActions == null) {
        		throw new IllegalArgumentException("validActions was null");
        	}
            this.validActions = new HashSet<ActionType>(validActions);
        }
        
        public void addValidAction(ActionType validAction) {
        	if (validAction == null) {
        		throw new IllegalArgumentException("validAction was null");
        	}
        	validActions.add(validAction);
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "validActions";
        final static String TYPE_NAME = "ValidActionsType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] { CoreConstants.CommonElements.FUTURE_ELEMENTS };
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String VALID_ACTION_CODES = "validActionCodes";
        final static String VALID_ACTION_CODE = "validActionCode";
    }

}

