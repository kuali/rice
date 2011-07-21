package org.kuali.rice.core.api.uif;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A quick finder widget that can be used by a TextInput, HiddenInput, Select, or MultiSelect control types.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = QuickFinder.Constants.TYPE_NAME)
public final class QuickFinder extends AbstractWidget {

    @XmlElement(name = Elements.BASE_LOOKUP_URL, required = true)
    private final String baseLookupUrl;

    @XmlElement(name = Elements.DATA_OBJECT_CLASS, required = true)
    private final String dataObjectClass;

    @XmlElement(name = Elements.LOOKUP_PARAMETERS, required = false)
    private final Map<String, String> lookupParameters;

    @XmlElement(name = Elements.FIELD_CONVERSIONS, required = false)
    private final Map<String, String> fieldConversions;

    private QuickFinder() {
        baseLookupUrl = null;
        dataObjectClass = null;
        lookupParameters = null;
        fieldConversions = null;
    }

    private QuickFinder(Builder b) {
        baseLookupUrl = b.baseLookupUrl;
        dataObjectClass = b.dataObjectClass;
        lookupParameters = b.lookupParameters;
        fieldConversions = b.fieldConversions;
    }

    public String getBaseLookupUrl() {
        return baseLookupUrl;
    }

    public String getDataObjectClass() {
        return dataObjectClass;
    }

    public Map<String, String> getLookupParameters() {
        return Collections.unmodifiableMap(lookupParameters);
    }

    public Map<String, String> getFieldConversions() {
        return Collections.unmodifiableMap(fieldConversions);
    }

    public static final class Builder extends AbstractWidget.Builder {
        private String baseLookupUrl;
        private String dataObjectClass;
        private Map<String, String> lookupParameters = Collections.emptyMap();
        private Map<String, String> fieldConversions = Collections.emptyMap();

        private Builder(String baseLookupUrl, String dataObjectClass) {
            setBaseLookupUrl(baseLookupUrl);
            setDataObjectClass(dataObjectClass);
        }

        public static Builder create(String baseLookupUrl, String dataObjectClass) {
            return new Builder(baseLookupUrl, dataObjectClass);
        }

        public String getBaseLookupUrl() {
            return baseLookupUrl;
        }

        public void setBaseLookupUrl(String baseLookupUrl) {
            if (StringUtils.isBlank(baseLookupUrl)) {
                throw new IllegalArgumentException("baseLookupUrl is blank");
            }

            this.baseLookupUrl = baseLookupUrl;
        }

        public String getDataObjectClass() {
            return dataObjectClass;
        }

        public void setDataObjectClass(String dataObjectClass) {
            if (StringUtils.isBlank(baseLookupUrl)) {
                throw new IllegalArgumentException("dataObjectClass is blank");
            }

            this.dataObjectClass = dataObjectClass;
        }

        public Map<String, String> getLookupParameters() {
            return Collections.unmodifiableMap(lookupParameters);
        }

        public void setLookupParameters(Map<String, String> lookupParameters) {
            if (lookupParameters != null) {
                this.lookupParameters = new HashMap<String, String>(lookupParameters);
            }
        }

        public Map<String, String> getFieldConversions() {
            return Collections.unmodifiableMap(fieldConversions);
        }

        public void setFieldConversions(Map<String, String> fieldConversions) {
            if (fieldConversions != null) {
                this.fieldConversions = new HashMap<String, String>(fieldConversions);
            }
        }

        @Override
        public QuickFinder build() {
            return new QuickFinder(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "QuickFinderType";
    }

    static final class Elements {
        static final String BASE_LOOKUP_URL = "baseLookupUrl";
        static final String DATA_OBJECT_CLASS = "dataObjectClass";
        static final String LOOKUP_PARAMETERS = "lookupParameters";
        static final String FIELD_CONVERSIONS = "fieldConversions";
    }
}
