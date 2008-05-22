/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.clientapp.vo;

import java.io.Serializable;

import edu.iu.uis.eden.web.KeyValue;

/**
 * This is a virtual object representing the {@link KeyValue} object.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KeyValueVO implements Serializable {

    private static final long serialVersionUID = 8488836176261012858L;

    private String key;
    private String value;

    public KeyValueVO() {
        super();
    }

    public KeyValueVO(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
