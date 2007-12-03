/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.test.web.framework;

/**
 * Represents a property that can be accessed relative to a specific "scheme" 
 */
public final class Property {
    /**
     * The property scheme (e.g. var:, url:, file:
     */
    public String scheme;
    /**
     * The property locator; in the case of a variable it is a name; in the case of a url, a url;
     * in the case of a file, a file path
     */
    public String locator;

    /**
     * Parses the scheme and locator from a property name/locator string
     * @param string the property name/locator
     */
    public Property(String string) {
        int i = string.indexOf(':');
        if (i == -1) {
            locator = string;
        } else {
            if (i > 0) {
                scheme = string.substring(0, i);
            }
            locator = string.substring(i + 1);
        }
    }

    /**
     * Initialized the property with specified scheme and locator
     * @param scheme the scheme
     * @param locator the locator
     */
    public Property(String scheme, String locator) {
        this.scheme = scheme;
        this.locator = locator;
    }

    public String toString() {
        return "[Property: scheme=" + scheme + ", locator=" + locator + "]";
    }
}