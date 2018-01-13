/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;

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

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

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

    @Override
    public Map<String, String> getKeyLabels() {
        return keyLabels;
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
            // keep previously SortedMaps (such as by sequence number) sorted. 
            if (keyLabels instanceof SortedMap) {
                this.keyLabels = Collections.unmodifiableSortedMap((SortedMap)keyLabels);
            } else {
                this.keyLabels = Collections.unmodifiableMap(new LinkedHashMap<String, String>(keyLabels));
            }
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
