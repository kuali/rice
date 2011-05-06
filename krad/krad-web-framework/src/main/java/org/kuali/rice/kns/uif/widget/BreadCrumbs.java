/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.uif.widget;

import java.util.List;

import org.kuali.rice.kns.uif.history.HistoryEntry;

/**
 * The breadcrumb widget contains various settings for setting up Breadcrumb/History support
 * on the view.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class BreadCrumbs extends WidgetBase {
	private static final long serialVersionUID = -2864287914665842251L;

	private boolean displayHomewardPath;
	private boolean displayPassedHistory;
	private int maxCrumbs;
	private List<HistoryEntry> homewardPathList;
	
	
    /**
     * Determines if the homewardPath is to be displayed.  Even when this is setting is on
     * the code may determine to turn off homewardPath display based on user interation and
     * ui elements being displayed (ie lightbox)
     * @return the displayHomewardPath
     */
    public boolean isDisplayHomewardPath() {
        return this.displayHomewardPath;
    }
    /**
     * @param displayHomewardPath the displayHomewardPath to set
     */
    public void setDisplayHomewardPath(boolean displayHomewardPath) {
        this.displayHomewardPath = displayHomewardPath;
    }
    /**
     * The maximum number of breadcrumbs to display
     * TODO this currently does nothing - may be removed
     * @return the maxCrumbs
     */
    public int getMaxCrumbs() {
        return this.maxCrumbs;
    }
    /**
     * @param maxCrumbs the maxCrumbs to set
     */
    public void setMaxCrumbs(int maxCrumbs) {
        this.maxCrumbs = maxCrumbs;
    }
    /**
     * Determines if the passedHistory is to be displayed.  In most cases this should not
     * be set through the xml as this is toggled off and on through code during different
     * ui procedures.
     * @return the displayPassedHistory
     */
    public boolean isDisplayPassedHistory() {
        return this.displayPassedHistory;
    }
    /**
     * @param displayPassedHistory the displayPassedHistory to set
     */
    public void setDisplayPassedHistory(boolean displayPassedHistory) {
        this.displayPassedHistory = displayPassedHistory;
    }
    /**
     * The homewardPath to be displayed on this representative of the logical "location" of
     * the view within the site hierarchy, can be set to anything desired.
     * @return the homewardPathList
     */
    public List<HistoryEntry> getHomewardPathList() {
        return this.homewardPathList;
    }
    /**
     * @param homewardPathList the homewardPathList to set
     */
    public void setHomewardPathList(List<HistoryEntry> homewardPathList) {
        this.homewardPathList = homewardPathList;
    }
}
