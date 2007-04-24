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

import java.io.Serializable;

/**
 * Simple bean used to indicate which operations are allowed for the current user on the associated document.
 */
public class DocumentActionFlags implements Serializable {
    private boolean canAnnotate;
    private boolean canReload;
    private boolean canSave;
    private boolean canRoute;
    private boolean canCancel;
    private boolean canClose;
    private boolean canBlanketApprove;
    private boolean canApprove;
    private boolean canDisapprove;
    private boolean canFYI;
    private boolean canAcknowledge;
    private boolean canAdHocRoute;
    private boolean canSupervise;
    private boolean canCopy;
    private boolean canPerformRouteReport;
    private boolean hasAmountTotal;


    /**
     * Default constructor.
     */
    public DocumentActionFlags() {
    }

    /**
     * Copy constructor.
     * 
     * @param flags
     */
    public DocumentActionFlags(DocumentActionFlags flags) {
        this.canAnnotate = flags.canAnnotate;
        this.canReload = flags.canReload;
        this.canSave = flags.canSave;
        this.canRoute = flags.canRoute;
        this.canCancel = flags.canCancel;
        this.canClose = flags.canClose;
        this.canBlanketApprove = flags.canBlanketApprove;
        this.canApprove = flags.canApprove;
        this.canDisapprove = flags.canDisapprove;
        this.canFYI = flags.canFYI;
        this.canAcknowledge = flags.canAcknowledge;
        this.canAdHocRoute = flags.canAdHocRoute;
        this.canSupervise = flags.canSupervise;
        this.canCopy = flags.canCopy;
        this.canPerformRouteReport = flags.canPerformRouteReport;
        this.hasAmountTotal = flags.hasAmountTotal;
    }

    /**
     * @return String
     */
    public String getClassName() {
        return this.getClass().getName();
    }

    /**
     * @return String
     */
    public boolean getCanAcknowledge() {
        return canAcknowledge;
    }

    /**
     * @param canAcknowledge
     */
    public void setCanAcknowledge(boolean canAcknowledge) {
        this.canAcknowledge = canAcknowledge;
    }

    /**
     * @return String
     */
    public boolean getCanAnnotate() {
        return canAnnotate;
    }

    /**
     * @param canAnnotate
     */
    public void setCanAnnotate(boolean canAnnotate) {
        this.canAnnotate = canAnnotate;
    }

    /**
     * @return String
     */
    public boolean getCanApprove() {
        return canApprove;
    }

    /**
     * @param canApprove
     */
    public void setCanApprove(boolean canApprove) {
        this.canApprove = canApprove;
    }

    /**
     * @return String
     */
    public boolean getCanBlanketApprove() {
        return canBlanketApprove;
    }

    /**
     * @param canBlanketApprove
     */
    public void setCanBlanketApprove(boolean canBlanketApprove) {
        this.canBlanketApprove = canBlanketApprove;
    }

    /**
     * @return String
     */
    public boolean getCanCancel() {
        return canCancel;
    }

    /**
     * @param canCancel
     */
    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    /**
     * @return String
     */
    public boolean getCanClose() {
        return canClose;
    }

    /**
     * @param canClose
     */
    public void setCanClose(boolean canClose) {
        this.canClose = canClose;
    }

    /**
     * @return String
     */
    public boolean getCanDisapprove() {
        return canDisapprove;
    }

    /**
     * @param canDisapprove
     */
    public void setCanDisapprove(boolean canDisapprove) {
        this.canDisapprove = canDisapprove;
    }

    /**
     * @return String
     */
    public boolean getCanFYI() {
        return canFYI;
    }

    /**
     * @param canFYI
     */
    public void setCanFYI(boolean canFYI) {
        this.canFYI = canFYI;
    }

    /**
     * @return String
     */
    public boolean getCanReload() {
        return canReload;
    }

    /**
     * @param canReload
     */
    public void setCanReload(boolean canReload) {
        this.canReload = canReload;
    }

    /**
     * @return String
     */
    public boolean getCanRoute() {
        return canRoute;
    }

    /**
     * @param canRoute
     */
    public void setCanRoute(boolean canRoute) {
        this.canRoute = canRoute;
    }

    /**
     * @return String
     */
    public boolean getCanSave() {
        return canSave;
    }

    /**
     * @param canSave
     */
    public void setCanSave(boolean canSave) {
        this.canSave = canSave;
    }

    /**
     * @param canAdHocRoute
     */
    public void setCanAdHocRoute(boolean canAdHocRoute) {
        this.canAdHocRoute = canAdHocRoute;
    }

    /**
     * @return String
     */
    public boolean getCanAdHocRoute() {
        return canAdHocRoute;
    }

    /**
     * @return String
     */
    public boolean getCanSupervise() {
        return canSupervise;
    }

    /**
     * @param canSupervise
     */
    public void setCanSupervise(boolean canSupervise) {
        this.canSupervise = canSupervise;
    }

    /**
     * @return boolean
     */
    public boolean getCanCopy() {
        return canCopy;
    }

    /**
     * @param canCopy
     */
    public void setCanCopy(boolean canCopy) {
        this.canCopy = canCopy;
    }

    /**
     * @return boolean
     */
    public boolean isCanPerformRouteReport() {
        return canPerformRouteReport;
    }

    /**
     * @param canPerformRouteReport
     */
    public void setCanPerformRouteReport(boolean canPerformRouteReport) {
        this.canPerformRouteReport = canPerformRouteReport;
    }
    
    /**
     * Gets the hasAmountTotal attribute. 
     * @return Returns the hasAmountTotal.
     */
    public boolean isHasAmountTotal() {
        return hasAmountTotal;
    }

    /**
     * Sets the hasAmountTotal attribute value.
     * @param hasAmountTotal The hasAmountTotal to set.
     */
    public void setHasAmountTotal(boolean hasAmountTotal) {
        this.hasAmountTotal = hasAmountTotal;
    }

    /**
     * Debugging method, simplifies comparing another instance of this class to this one
     * 
     * @param other
     * @return String
     */
    public String diff(DocumentActionFlags other) {
        StringBuffer s = new StringBuffer();

        if (canAnnotate != other.canAnnotate) {
            s.append("canAnnotate=(" + canAnnotate + "," + other.canAnnotate + ")");
        }
        if (canReload != other.canReload) {
            s.append("canReload=(" + canReload + "," + other.canReload + ")");
        }
        if (canSave != other.canSave) {
            s.append("canSave=(" + canSave + "," + other.canSave + ")");
        }
        if (canRoute != other.canRoute) {
            s.append("canRoute=(" + canRoute + "," + other.canRoute + ")");
        }
        if (canCancel != other.canCancel) {
            s.append("canCancel=(" + canCancel + "," + other.canCancel + ")");
        }
        if (canClose != other.canClose) {
            s.append("canClose=(" + canClose + "," + other.canClose + ")");
        }
        if (canBlanketApprove != other.canBlanketApprove) {
            s.append("canBlanketApprove=(" + canBlanketApprove + "," + other.canBlanketApprove + ")");
        }
        if (canApprove != other.canApprove) {
            s.append("canApprove=(" + canApprove + "," + other.canApprove + ")");
        }
        if (canDisapprove != other.canDisapprove) {
            s.append("canDisapprove=(" + canDisapprove + "," + other.canDisapprove + ")");
        }
        if (canFYI != other.canFYI) {
            s.append("canFYI=(" + canFYI + "," + other.canFYI + ")");
        }
        if (canAcknowledge != other.canAcknowledge) {
            s.append("canAcknowledge=(" + canAcknowledge + "," + other.canAcknowledge + ")");
        }
        if (canAdHocRoute != other.canAdHocRoute) {
            s.append("canAdHocRoute=(" + canAdHocRoute + "," + other.canAdHocRoute + ")");
        }
        if (canSupervise != other.canSupervise) {
            s.append("canSupervise=(" + canSupervise + "," + other.canSupervise + ")");
        }
        if (canCopy != other.canCopy) {
            s.append("canCopy=(" + canCopy + "," + other.canCopy + ")");
        }
        if (canPerformRouteReport != other.canPerformRouteReport) {
            s.append("canPerformRouteReport=(" + canPerformRouteReport + "," + other.canPerformRouteReport + ")");
        }
        
        return s.toString();
    }
}