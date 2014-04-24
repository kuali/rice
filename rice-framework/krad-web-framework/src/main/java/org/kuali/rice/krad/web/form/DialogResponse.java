/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.web.form;

import java.io.Serializable;

/**
 * Holds response data for a basic dialog that has been triggered from a server call.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DialogResponse implements Serializable {
    private static final long serialVersionUID = -3533683391767027067L;

    protected static final String TRUE_VALUES = "/true/yes/y/on/1/";

    private String dialogId;

    private String response;
    private String explanation;

    /**
     * Default Constructor.
     */
    public DialogResponse() {
    }

    /**
     * Constructor taking dialog id, response, and explanation.
     */
    public DialogResponse(String dialogId, String response, String explanation) {
        this.dialogId = dialogId;
        this.response = response;
        this.explanation = explanation;
    }

    /**
     * Id for the dialog whose response has been captured.
     *
     * @return dialog id
     */
    public String getDialogId() {
        return dialogId;
    }

    /**
     * @see DialogResponse#getDialogId()
     */
    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    /**
     * String response for the dialog action (button) that was chosen.
     *
     * @return String dialog response
     */
    public String getResponse() {
        return response;
    }

    /**
     * Returns the response for the dialog as a boolean.
     *
     * @return boolean dialog response
     * @see DialogResponse#TRUE_VALUES
     */
    public boolean getResponseAsBoolean() {
        if (response != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("/").append(response.toLowerCase()).append("/");

            if (TRUE_VALUES.contains(builder.toString())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @see DialogResponse#getResponse()
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * If the dialog contained an explanation field that binds to the generic form property, the value (if any)
     * given by the user.
     *
     * <p>Note if the explanation field was found to a different model property, its contents will not be
     * available here. It should be retrieved from the corresponding model property.</p>
     *
     * @return string explanation value
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * @see DialogResponse#getExplanation()
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
