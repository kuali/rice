package org.kuali.rice.core.api.uif;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A radio button group control type.
 */
@XmlRootElement(name = RemotableRadioButtonGroup.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableRadioButtonGroup.Constants.TYPE_NAME, propOrder = {
        RemotableRadioButtonGroup.Elements.KEY_LABELS,
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class RemotableRadioButtonGroup extends RemotableAbstractControl implements KeyLabeled {

    @XmlElement(name = Elements.KEY_LABELS, required = true)
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    private final Map<String, String> keyLabels;

    @Override
    public Map<String, String> getKeyLabels() {
        return keyLabels;
    }
    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private RemotableRadioButtonGroup() {
        keyLabels = null;
    }

    private RemotableRadioButtonGroup(Builder b) {
        keyLabels = b.keyLabels;
    }

    public static final class Builder extends RemotableAbstractControl.Builder implements KeyLabeled {
        private Map<String, String> keyLabels;

        private Builder(Map<String, String> keyLabels) {
            setKeyLabels(keyLabels);
        }

        public static Builder create(Map<String, String> keyLabels) {
            return new Builder(keyLabels);
        }

        @Override
        public Map<String, String> getKeyLabels() {
            return keyLabels;
        }

        public void setKeyLabels(Map<String, String> keyLabels) {
            if (keyLabels == null || keyLabels.isEmpty()) {
                throw new IllegalArgumentException("keyLabels must be non-null & non-empty");
            }

            this.keyLabels = Collections.unmodifiableMap(new HashMap<String, String>(keyLabels));
        }

        @Override
        public RemotableRadioButtonGroup build() {
            return new RemotableRadioButtonGroup(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "RadioButtonGroupType";
        final static String ROOT_ELEMENT_NAME = "radioButtonGroup";
    }

    static final class Elements {
        static final String KEY_LABELS = "keyLabels";
    }
}
