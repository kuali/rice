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
package org.kuali.core.document.authorization;


/**
 * MaintenanceDocument specific action flags.
 * 
 * 
 */
final public class MaintenanceDocumentActionFlags extends DocumentActionFlags {

    private boolean canReopen;

    /**
     * Default constructor.
     */
    public MaintenanceDocumentActionFlags() {
    }

    /**
     * Copy constructor.
     * 
     * @param flags
     */
    public MaintenanceDocumentActionFlags(DocumentActionFlags flags) {
        super(flags);

        if (flags instanceof MaintenanceDocumentActionFlags) {
            MaintenanceDocumentActionFlags mflags = (MaintenanceDocumentActionFlags) flags;

            this.canReopen = mflags.canReopen;
        }
    }

    /**
     * Gets the canReopen attribute.
     * 
     * @return Returns the canReopen.
     */
    public boolean isCanReopen() {
        return canReopen;
    }

    /**
     * Sets the canReopen attribute value.
     * 
     * @param canReopen The canReopen to set.
     */
    public void setCanReopen(boolean canReopen) {
        this.canReopen = canReopen;
    }

    /**
     * Debugging method, simplifies comparing another instance of this class to this one
     * 
     * @param other
     * @return String
     */
    public String diff(DocumentActionFlags other) {
        StringBuffer s = new StringBuffer(super.diff(other));

        if (other instanceof MaintenanceDocumentActionFlags) {
            MaintenanceDocumentActionFlags mOther = (MaintenanceDocumentActionFlags) other;

            if (this.canReopen != mOther.canReopen) {
                s.append("canReopen=(" + canReopen + "," + mOther.canReopen + ")");
            }
        }

        return s.toString();
    }
}
