package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * This control is a group of checkboxes.  Checkboxes can have multiple selected values.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = CheckboxGroup.Constants.TYPE_NAME)
public class CheckboxGroup extends AbstractControl implements CheckboxGroupContract {
    @XmlElement(name = Elements.DEFAULT_VALUES, required = false)
    private final Collection<String> defaultValues;

    @Override
    public Collection<String> getDefaultValues() {
        return Collections.unmodifiableCollection(defaultValues);
    }

    private CheckboxGroup() {
        defaultValues = null;
    }

    private CheckboxGroup(Builder b) {
        super(b);
        defaultValues = b.defaultValues;
    }

    public static final class Builder extends AbstractControl.Builder implements CheckboxGroupContract {
        private Collection<String> defaultValues;

        private Builder(String name) {
            super(name);
        }

        public static Builder create(String name) {
            return new Builder(name);
        }

        public static Builder create(MultiSelectContract contract) {
            Builder b = create(contract.getName());

            partialCreate(contract, b);

            b.setDefaultValues(contract.getDefaultValues());
            return b;
        }

        @Override
        public Collection<String> getDefaultValues() {
            return Collections.unmodifiableCollection(defaultValues);
        }

        public void setDefaultValues(Collection<String> defaultValues) {
            this.defaultValues = new ArrayList<String>(defaultValues);
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
        static final String DEFAULT_VALUES = "defaultValues";
    }
}
