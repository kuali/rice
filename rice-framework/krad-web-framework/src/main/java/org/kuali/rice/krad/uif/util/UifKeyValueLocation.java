/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;

/**
 * KeyValue that has an additional location property that takes a Url object.  When this is used with a dropdown or
 * an optionList control, those options become navigation controls.
 */
@BeanTag(name = "keyValueLocation-bean", parent = "Uif-KeyValueLocation")
public class UifKeyValueLocation extends UifKeyValue {

    private UrlInfo location;

    private boolean disabled;

    private boolean optionDivider;
    private boolean optionHeader;

    /**
     * Base constructor
     */
    public UifKeyValueLocation() {
        super();
    }

    public UifKeyValueLocation(String key, String value) {
        super(key, value);
    }

    /**
     * KeyValueLocation constructor
     *
     * @param key the key
     * @param value the value
     * @param location the url location object
     */
    public UifKeyValueLocation(String key, String value, UrlInfo location) {
        this.key = key;
        this.value = value;
        this.location = location;
    }

    /**
     * Get the url object representing the location
     *
     * @return the url location object
     */
    @BeanTagAttribute(name = "location", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public UrlInfo getLocation() {
        return location;
    }

    /**
     * Set the url location object
     *
     * @param location
     */
    public void setLocation(UrlInfo location) {
        this.location = location;
    }

    /**
     * Convenience setter for setting the href (full URL) of the location object
     *
     * @param href URL for location option
     */
    public void setHref(String href) {
        if (this.location == null) {
            ComponentFactory.getUrlInfo();
        }

        this.location.setHref(href);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isOptionDivider() {
        return optionDivider;
    }

    public void setOptionDivider(boolean optionDivider) {
        this.optionDivider = optionDivider;
    }

    public boolean isOptionHeader() {
        return optionHeader;
    }

    public void setOptionHeader(boolean optionHeader) {
        this.optionHeader = optionHeader;
    }
}
