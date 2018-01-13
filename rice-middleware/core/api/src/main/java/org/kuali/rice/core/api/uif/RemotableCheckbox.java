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
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * A checkbox control type.
 */
@XmlRootElement(name = RemotableCheckbox.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableCheckbox.Constants.TYPE_NAME, propOrder = {
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class RemotableCheckbox extends RemotableAbstractControl {

    private static final RemotableCheckbox INSTANCE = new RemotableCheckbox(Builder.INSTANCE);

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private RemotableCheckbox() {
    }

    private RemotableCheckbox(Builder b) {
    }

    public static final class Builder extends RemotableAbstractControl.Builder {

        private static final Builder INSTANCE = new Builder();

        private Builder() {
            super();
        }

        //no important state in these classes so returning a singleton
        public static Builder create() {
            return INSTANCE;
        }

        @Override
        public RemotableCheckbox build() {
            return RemotableCheckbox.INSTANCE;
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "CheckboxType";
        final static String ROOT_ELEMENT_NAME = "checkbox";
    }

    static final class Elements {
    }
}
