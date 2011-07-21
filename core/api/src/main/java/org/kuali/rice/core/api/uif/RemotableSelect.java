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
 * A select control type.
 */
@XmlRootElement(name = RemotableSelect.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableSelect.Constants.TYPE_NAME, propOrder = {
        RemotableSelect.Elements.KEY_LABELS,
        RemotableSelect.Elements.SIZE,
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class RemotableSelect extends RemotableAbstractControl implements Sized, KeyLabeled {

    @XmlElement(name = Elements.KEY_LABELS, required = true)
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    private final Map<String, String> keyLabels;

    @XmlElement(name = Elements.SIZE, required = false)
    private final Integer size;

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private RemotableSelect() {
        size = null;
        keyLabels = null;
    }

    private RemotableSelect(Builder b) {
        size = b.size;
        keyLabels = b.keyLabels;
    }

    @Override
    public Map<String, String> getKeyLabels() {
        return keyLabels;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    public static final class Builder extends RemotableAbstractControl.Builder implements Sized, KeyLabeled {
        private Integer size;
        private Map<String, String> keyLabels;

        private Builder(Map<String, String> keyLabels) {
            setKeyLabels(keyLabels);
        }

        public static Builder create(Map<String, String> keyLabels) {
            return new Builder(keyLabels);
        }

        @Override
        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            if (size != null && size < 1) {
                throw new IllegalArgumentException("size was < 1");
            }

            this.size = size;
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
        public RemotableSelect build() {
            return new RemotableSelect(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "SelectType";
        final static String ROOT_ELEMENT_NAME = "select";
    }

    static final class Elements {
        static final String SIZE = "size";
        static final String KEY_LABELS = "keyLabels";
    }
}
