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

package org.kuali.rice.kns.datadictionary.control;


/**
    The textarea element defines an HTML textarea control element.
    The rows and cols attributes define the size of the field.
 */
public class TextareaControlDefinition extends ControlDefinitionBase {

    public TextareaControlDefinition() {
    }

    /**
     * @see org.kuali.rice.kns.datadictionary.control.ControlDefinition#isTextarea()
     */
    public boolean isTextarea() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "TextAreaControlDefinition";
    }
}