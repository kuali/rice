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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * The NumberedPager widget is used to display a list of links horizontally in a page selection user interface.
 * The user can select a page to jump to, go to prev/next page, or go to the first or last page.  This widget needs to
 * know the numberOfPages total, and the currentPage the user is on currently, so this widget must be fed this
 * information from the code.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.layout.StackedLayoutManager
 */
@BeanTag(name = "numberedPager", parent = "Uif-NumberedPager")
public class NumberedPager extends Pager {
    private static final long serialVersionUID = -6495003633052595157L;

    private int maxNumberedLinksShown;
    private boolean renderPrevNext;
    private boolean renderFirstLast;

    protected int pagesStart;
    protected int pagesEnd;

    private String firstText;
    private String lastText;

    /**
     * performFinalize calculates the pagesStart and pagesEnd properties (using numberOfPages, currentPage, and
     * maxNumberedLinksShown - these must be set) which determines pages shown by the widget
     *
     * @param model the current model
     * @param parent parent container
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (maxNumberedLinksShown >= this.getNumberOfPages()) {
            // Show all pages if possible to do so
            pagesStart = 1;
            pagesEnd = this.getNumberOfPages();
        } else {
            // Determine how many pages max shown before an after the current page
            int beforeAfterShown = (int) Math.floor((double) maxNumberedLinksShown / 2.0);
            pagesStart = this.getCurrentPage() - beforeAfterShown;
            pagesEnd = this.getCurrentPage() + beforeAfterShown;

            // If maxNumberedLinksShown is even and cannot have an equal amount of pages showing before
            // and after the current page, so trim one off the end
            if (pagesEnd - pagesStart == maxNumberedLinksShown) {
                pagesEnd = pagesEnd - 1;
            }

            // The pagesEnd is within range of numberOfPages total, therefore show the last pages
            if (pagesEnd > this.getNumberOfPages()) {
                pagesEnd = this.getNumberOfPages();
                pagesStart = this.getNumberOfPages() - maxNumberedLinksShown + 1;
            }

            // The pageStart is within range, therefore show the first pages
            if (pagesStart < 1) {
                pagesStart = 1;
                if (maxNumberedLinksShown < this.getNumberOfPages()) {
                    pagesEnd = maxNumberedLinksShown;
                }
            }
        }
    }

    /**
     * The maximum number of NUMBERED links shown at once for pages, if number of pages that exist exceed this value,
     * the pager omits some pages before and/or after the current page (which are revealed during while
     * navigating using a carousel effect)
     *
     * @return the maximum number of NUMBERED links to show
     */
    @BeanTagAttribute
    public int getMaxNumberedLinksShown() {
        return maxNumberedLinksShown;
    }

    /**
     * Set the maximum number of NUMBERED links shown
     *
     * @param maxNumberedLinksShown
     */
    public void setMaxNumberedLinksShown(int maxNumberedLinksShown) {
        this.maxNumberedLinksShown = maxNumberedLinksShown;
    }

    /**
     * Returns true if this pager widget is rendering the "First" and "Last" links
     *
     * @return true if rendering "First" and "Last" links
     */
    @BeanTagAttribute
    public boolean isRenderFirstLast() {
        return renderFirstLast;
    }

    /**
     * Set renderFirstLast
     *
     * @param renderFirstLast
     */
    public void setRenderFirstLast(boolean renderFirstLast) {
        this.renderFirstLast = renderFirstLast;
    }

    /**
     * Returns true if this pager widget is rendering the "Prev" and "Next" links
     *
     * @return true if rendering "First" and "Last" links
     */
    @BeanTagAttribute
    public boolean isRenderPrevNext() {
        return renderPrevNext;
    }

    /**
     * Set renderPrevNext
     *
     * @param renderPrevNext
     */
    public void setRenderPrevNext(boolean renderPrevNext) {
        this.renderPrevNext = renderPrevNext;
    }

    /**
     * The first page number to render; this is set by the framework
     *
     * @return pages start
     */
    public int getPagesStart() {
        return pagesStart;
    }

    /**
     * The last page number to render; this is set by the framework
     *
     * @return last page number to render
     */
    public int getPagesEnd() {
        return pagesEnd;
    }

    /**
     * The text to use on the first link.
     *
     * @return the first link text
     */
    @BeanTagAttribute
    public String getFirstText() {
        return firstText;
    }

    /**
     * @see NumberedPager#getFirstText()
     */
    public void setFirstText(String firstText) {
        this.firstText = firstText;
    }

    /**
     * The text to use for the last link.
     *
     * @return the last link text
     */
    @BeanTagAttribute
    public String getLastText() {
        return lastText;
    }

    /**
     * @see NumberedPager#getLastText()
     */
    public void setLastText(String lastText) {
        this.lastText = lastText;
    }

}
