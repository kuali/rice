/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kns.web.ui;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * 
 */
public class KeyLabelPair implements Serializable {
    private static final long serialVersionUID = 6167007653464395634L;
    public Object key;
    public String label;
    public int numPaddedSpaces;
    
    public KeyLabelPair() {
    	numPaddedSpaces = 0;
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
    
    public void setNumPaddedSpaces(int numPaddedSpaces) {
    	this.numPaddedSpaces = numPaddedSpaces;
    }
 
    public String getHtmlSpacePadding() {
    	return StringUtils.repeat("&nbsp;", numPaddedSpaces);
    }
}
