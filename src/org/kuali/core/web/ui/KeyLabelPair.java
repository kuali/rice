/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.web.ui;

import java.io.Serializable;

/**
 * 
 */
public class KeyLabelPair implements Serializable {
    private static final long serialVersionUID = 6167007653464395634L;
    public Object key;
    public String label;

    public KeyLabelPair() {
    }

    public KeyLabelPair(Object key, String label) {
        this.key = key;
        this.label = label;
    }

    public Object getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setKey(Object key) {
        this.key = key;
    }
}

/*
 * Copyright 2003 The Trustees of Indiana University. All rights reserved. This file is part of the EDEN software package. For
 * license information, see the LICENSE file in the top level directory of the EDEN source distribution.
 */
