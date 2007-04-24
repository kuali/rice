/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.util;

/**
 * This class converts the "Y" or "N" value from the database into a true or false in Java.
 * 
 * 
 * @deprecated Use OjbCharBooleanConversion instead
 */
public final class OjbCharBooleanFieldConversion extends OjbCharBooleanFieldConversionBase {
    private static final long serialVersionUID = 5192588414458129183L;
    private static String S_TRUE = "Y";
    private static String S_FALSE = "N";

    /**
     * no args constructor
     */
    public OjbCharBooleanFieldConversion() {
        super();
    }

    protected String getTrueValue() {
        return "Y";
    }

    protected String getFalseValue() {
        return "N";
    }
}
