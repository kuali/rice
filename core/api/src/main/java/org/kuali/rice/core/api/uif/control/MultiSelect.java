package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = MultiSelect.Constants.TYPE_NAME)
public class MultiSelect extends AbstractControl implements MultiSelectContract {
    @XmlElement(name = Elements.SIZE, required = false)
    private final Integer size;

    @XmlElement(name = Elements.DEFAULT_VALUES, required = false)
    private final Collection<String> defaultValues;

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public Collection<String> getDefaultValues() {
        return Collections.unmodifiableCollection(defaultValues);
    }

    private MultiSelect() {
        size = null;
        defaultValues = null;
    }

    private MultiSelect(Builder b) {
        super(b);
        size = b.size;
        defaultValues = b.defaultValues;
    }

    public static final class Builder extends AbstractControl.Builder implements MultiSelectContract {
        private Integer size;
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

            b.setSize(contract.getSize());
            b.setDefaultValues(contract.getDefaultValues());
            return b;
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
        public Collection<String> getDefaultValues() {
            return Collections.unmodifiableCollection(defaultValues);
        }

        public void setDefaultValues(Collection<String> defaultValues) {
            this.defaultValues = new ArrayList<String>(defaultValues);
        }

        @Override
        public MultiSelect build() {
            return new MultiSelect(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "SelectType";
    }

    static final class Elements {
        static final String SIZE = "size";
        static final String DEFAULT_VALUES = "defaultValues";
    }
}
