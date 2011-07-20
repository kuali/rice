package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CheckboxGroup extends AbstractControl implements KeyLabeled {

    @XmlElement(name = Elements.KEY_LABELS, required = false)
    private final Map<String, String> keyLabels;

    @Override
    public Map<String, String> getKeyLabels() {
        return keyLabels;
    }

    private CheckboxGroup() {
        keyLabels = null;
    }

    private CheckboxGroup(Builder b) {
        keyLabels = b.keyLabels;
    }

    public static final class Builder extends AbstractControl.Builder implements KeyLabeled {
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
        public CheckboxGroup build() {
            return new CheckboxGroup(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "CheckboxGroupType";
    }

    static final class Elements {
        static final String KEY_LABELS = "keyLabels";
    }
}
