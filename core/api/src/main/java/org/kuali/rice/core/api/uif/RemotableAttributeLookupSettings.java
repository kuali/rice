/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.core.api.uif;

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
import org.w3c.dom.Element;

@XmlRootElement(name = RemotableAttributeLookupSettings.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableAttributeLookupSettings.Constants.TYPE_NAME, propOrder = {
        RemotableAttributeLookupSettings.Elements.IN_CRITERIA,
        RemotableAttributeLookupSettings.Elements.IN_RESULTS,
        RemotableAttributeLookupSettings.Elements.RANGED,
        RemotableAttributeLookupSettings.Elements.LOWER_BOUND_NAME,
        RemotableAttributeLookupSettings.Elements.LOWER_BOUND_LABEL,
        RemotableAttributeLookupSettings.Elements.LOWER_BOUND_INCLUSIVE,
        RemotableAttributeLookupSettings.Elements.UPPER_BOUND_NAME,
        RemotableAttributeLookupSettings.Elements.UPPER_BOUND_LABEL,
        RemotableAttributeLookupSettings.Elements.UPPER_BOUND_INCLUSIVE,
        RemotableAttributeLookupSettings.Elements.CASE_SENSITIVE,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RemotableAttributeLookupSettings extends AbstractDataTransferObject implements AttributeLookupSettings {

    @XmlElement(name = Elements.IN_CRITERIA, required = true)
    private final boolean inCriteria;

    @XmlElement(name = Elements.IN_RESULTS, required = true)
    private final boolean inResults;

    @XmlElement(name = Elements.RANGED, required = true)
    private final boolean ranged;

    @XmlElement(name = Elements.LOWER_BOUND_NAME, required = false)
    private final String lowerBoundName;

    @XmlElement(name = Elements.LOWER_BOUND_LABEL, required = false)
    private final String lowerBoundLabel;

    @XmlElement(name = Elements.LOWER_BOUND_INCLUSIVE, required = false)
    private final boolean lowerBoundInclusive;

    @XmlElement(name = Elements.UPPER_BOUND_NAME, required = false)
    private final String upperBoundName;

    @XmlElement(name = Elements.UPPER_BOUND_LABEL, required = false)
    private final String upperBoundLabel;

    @XmlElement(name = Elements.UPPER_BOUND_INCLUSIVE, required = false)
    private final boolean upperBoundInclusive;

    @XmlElement(name = Elements.CASE_SENSITIVE, required = false)
    private final Boolean caseSensitive;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private RemotableAttributeLookupSettings() {
        this.inCriteria = false;
        this.inResults = false;
        this.ranged = false;
        this.lowerBoundName = null;
        this.lowerBoundLabel = null;
        this.lowerBoundInclusive = false;
        this.upperBoundName = null;
        this.upperBoundLabel = null;
        this.upperBoundInclusive = false;
        this.caseSensitive = null;
    }

    private RemotableAttributeLookupSettings(Builder builder) {
        this.inCriteria = builder.isInCriteria();
        this.inResults = builder.isInResults();
        this.ranged = builder.isRanged();
        this.lowerBoundName = builder.getLowerBoundName();
        this.lowerBoundLabel = builder.getLowerBoundLabel();
        this.lowerBoundInclusive = builder.isLowerBoundInclusive();
        this.upperBoundName = builder.getUpperBoundName();
        this.upperBoundLabel = builder.getUpperBoundLabel();
        this.upperBoundInclusive = builder.isUpperBoundInclusive();
        this.caseSensitive = builder.isCaseSensitive();
    }

    @Override
    public boolean isInCriteria() {
        return inCriteria;
    }

    @Override
    public boolean isInResults() {
        return inResults;
    }

    @Override
    public boolean isRanged() {
        return ranged;
    }

    @Override
    public String getLowerBoundName() {
        return this.lowerBoundName;
    }

    @Override
    public String getLowerBoundLabel() {
        return this.lowerBoundLabel;
    }

    @Override
    public boolean isLowerBoundInclusive() {
        return this.lowerBoundInclusive;
    }

    @Override
    public String getUpperBoundName() {
        return this.upperBoundName;
    }

    @Override
    public String getUpperBoundLabel() {
        return this.upperBoundLabel;
    }

    @Override
    public boolean isUpperBoundInclusive() {
        return this.upperBoundInclusive;
    }

    @Override
    public Boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * A builder which can be used to construct {@link RemotableAttributeLookupSettings} instances.  Enforces the constraints of the {@link AttributeLookupSettings}.
     */
    public final static class Builder implements Serializable, ModelBuilder, AttributeLookupSettings {

        private boolean inCriteria;
        private boolean inResults;
        private boolean ranged;
        private String lowerBoundName;
        private String lowerBoundLabel;
        private boolean lowerBoundInclusive;
        private String upperBoundName;
        private String upperBoundLabel;
        private boolean upperBoundInclusive;
        private Boolean caseSensitive;

        private Builder() {
            setInCriteria(true);
            setInResults(true);
            setRanged(false);
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(AttributeLookupSettings contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setInCriteria(contract.isInCriteria());
            builder.setInResults(contract.isInResults());
            builder.setRanged(contract.isRanged());
            builder.setLowerBoundName(contract.getLowerBoundName());
            builder.setLowerBoundLabel(contract.getLowerBoundLabel());
            builder.setLowerBoundInclusive(contract.isLowerBoundInclusive());
            builder.setUpperBoundName(contract.getUpperBoundName());
            builder.setUpperBoundLabel(contract.getUpperBoundLabel());
            builder.setUpperBoundInclusive(contract.isUpperBoundInclusive());
            builder.setCaseSensitive(contract.isCaseSensitive());
            return builder;
        }

        public RemotableAttributeLookupSettings build() {
            return new RemotableAttributeLookupSettings(this);
        }

        @Override
        public boolean isInCriteria() {
            return inCriteria;
        }

        @Override
        public boolean isInResults() {
            return inResults;
        }

        @Override
        public boolean isRanged() {
            return ranged;
        }

        @Override
        public String getLowerBoundName() {
            return this.lowerBoundName;
        }

        @Override
        public String getLowerBoundLabel() {
            return this.lowerBoundLabel;
        }

        @Override
        public boolean isLowerBoundInclusive() {
            return this.lowerBoundInclusive;
        }

        @Override
        public String getUpperBoundName() {
            return this.upperBoundName;
        }

        @Override
        public String getUpperBoundLabel() {
            return this.upperBoundLabel;
        }

        @Override
        public boolean isUpperBoundInclusive() {
            return this.upperBoundInclusive;
        }

        @Override
        public Boolean isCaseSensitive() {
            return caseSensitive;
        }

        public void setInCriteria(boolean inCriteria) {
            this.inCriteria = inCriteria;
        }

        public void setInResults(boolean inResults) {
            this.inResults = inResults;
        }

        public void setRanged(boolean ranged) {
            this.ranged = ranged;
        }

        public void setLowerBoundName(String lowerBoundName) {
            if (StringUtils.isBlank(lowerBoundName)) {
                throw new IllegalArgumentException("lowerBoundName was null or blank");
            }
            this.lowerBoundName = lowerBoundName;
        }

        public void setLowerBoundLabel(String lowerBoundLabel) {
            this.lowerBoundLabel = lowerBoundLabel;
        }

        public void setLowerBoundInclusive(boolean lowerBoundInclusive) {
            this.lowerBoundInclusive = lowerBoundInclusive;
        }

        public void setUpperBoundName(String upperBoundName) {
            if (StringUtils.isBlank(upperBoundName)) {
                throw new IllegalArgumentException("upperBoundName was null or blank");
            }
            this.upperBoundName = upperBoundName;
        }

        public void setUpperBoundLabel(String upperBoundLabel) {
            this.upperBoundLabel = upperBoundLabel;
        }

        public void setUpperBoundInclusive(boolean upperBoundInclusive) {
            this.upperBoundInclusive = upperBoundInclusive;
        }

        public void setCaseSensitive(Boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "remotableAttributeLookupSettings";
        final static String TYPE_NAME = "RemotableAttributeLookupSettingsType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String IN_CRITERIA = "inCriteria";
        final static String IN_RESULTS = "inResults";
        final static String RANGED = "ranged";
        final static String LOWER_BOUND_NAME = "lowerBoundName";
        final static String LOWER_BOUND_LABEL = "lowerBoundLabel";
        final static String LOWER_BOUND_INCLUSIVE = "lowerBoundInclusive";
        final static String UPPER_BOUND_NAME = "upperBoundName";
        final static String UPPER_BOUND_LABEL = "upperBoundLabel";
        final static String UPPER_BOUND_INCLUSIVE = "upperBoundInclusive";
        final static String CASE_SENSITIVE = "caseSensitive";
    }

}
