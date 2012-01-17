/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kew.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidActions implements Serializable {

    private static final long serialVersionUID = -727218907121562270L;

    private List<String> actionTakenCodes;

    public ValidActions() {
        super();
    }

    public void addActionTakenCode(String code) {
        if (actionTakenCodes == null) {
            actionTakenCodes = new ArrayList<String>();
        }
        actionTakenCodes.add(code);
    }
    
    public List<String> getActionTakenCodes() {
        return actionTakenCodes;
    }

    public void setActionTakenCodes(List<String> actionTakenCodes) {
        this.actionTakenCodes = actionTakenCodes;
    }
}
