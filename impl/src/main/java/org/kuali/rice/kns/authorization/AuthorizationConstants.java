/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.authorization;

import org.kuali.rice.core.util.JSTLConstants;

/**
 * Defines constants used in authorization-related code.
 */
public class AuthorizationConstants extends JSTLConstants {
    private static final long serialVersionUID = 3415761227639600675L;

    public static class EditMode {
        public static final String UNVIEWABLE = "unviewable";
        public static final String VIEW_ONLY = "viewOnly";
        public static final String FULL_ENTRY = "fullEntry";
    }

    public static class MaintenanceEditMode extends EditMode {
        public static final String APPROVER_EDIT_ENTRY = "approverEditEntry";
    }

}
