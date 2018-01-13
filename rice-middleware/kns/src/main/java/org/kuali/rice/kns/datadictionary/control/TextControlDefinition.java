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
package org.kuali.rice.kns.datadictionary.control;


/**
 * The text element defines an HTML text control element.  The size attribute defines the size of the field. If the
 * datePicker option is entered, then  the user will be able to select a date from a popup calendar.
 *
 * @deprecated Use {@link org.kuali.rice.krad.uif.control.TextControl}.
 */
@Deprecated
public class TextControlDefinition extends ControlDefinitionBase {
    private static final long serialVersionUID = 6075633623507085548L;

	public TextControlDefinition() {
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isText()
     */
    public boolean isText() {
        return true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "TextControlDefinition";
    }
}
