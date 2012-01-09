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
package org.kuali.rice.krad.uif.widget;

import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.view.HistoryEntry;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The breadcrumb widget contains various settings for setting up
 * Breadcrumb/History support on the view.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BreadCrumbs extends WidgetBase {
    private static final long serialVersionUID = -2864287914665842251L;

    private boolean displayHomewardPath;
    private boolean displayPassedHistory;
    private boolean displayBreadcrumbsWhenOne;
    private List<HistoryEntry> homewardPathList;

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li>Evaluate expression on howeward path list</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.Component#performApplyModel(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object)
     */
    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (homewardPathList != null) {
            Map<String, Object> context = new HashMap<String, Object>();
            context.putAll(view.getContext());

            for (HistoryEntry historyEntry : homewardPathList) {
                KRADServiceLocatorWeb.getExpressionEvaluatorService().evaluateObjectExpressions(historyEntry, model,
                        context);
            }
        }
    }

    /**
     * Determines if the homewardPath is to be displayed. Even when this is
     * setting is on the code may determine to turn off homewardPath display
     * based on user interaction and ui elements being displayed (ie lightbox)
     *
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
     * Determines if the passedHistory is to be displayed. In most cases this
     * should not be set through the xml as this is toggled off and on through
     * code during different ui procedures.
     *
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
     * The homewardPath to be displayed on this representative of the logical
     * "location" of the view within the site hierarchy, can be set to anything
     * desired.
     *
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

    /**
     * If true, breadcrumbs will not be displayed if only one breadcrumb is
     * going to be shown, this improves visual clarity of the page
     *
     * @return the displayBreadcrumbsWhenOne
     */
    public boolean isDisplayBreadcrumbsWhenOne() {
        return this.displayBreadcrumbsWhenOne;
    }

    /**
     * @param displayBreadcrumbsWhenOne the displayBreadcrumbsWhenOne to set
     */
    public void setDisplayBreadcrumbsWhenOne(boolean displayBreadcrumbsWhenOne) {
        this.displayBreadcrumbsWhenOne = displayBreadcrumbsWhenOne;
    }

}
