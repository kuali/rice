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

import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.element.ContentElementBase;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Pager widgets are used to page a set of information which has multiple pages.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.layout.StackedLayoutManager
 */
public abstract class Pager extends ContentElementBase {
    private static final long serialVersionUID = 4581039429463422458L;

    private String linkScript;

    private int numberOfPages;
    private int currentPage;

    private String prevText;
    private String nextText;

    public Pager() {
        super();
    }

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

        // if no pages or 1 page, do not render
        if (numberOfPages == 0 || numberOfPages == 1) {
            this.setRender(false);
        }

        this.linkScript = "e.preventDefault();" + this.linkScript;
    }

    /**
     * The script to execute when a link is clicked (should probably use the "this" var in most cases, to determine
     * page number selected - see retrieveStackedPage(linkElement, collectionId) js function)
     *
     * @return the script to execute when a link is clicked
     */
    @BeanTagAttribute
    public String getLinkScript() {
        return linkScript;
    }

    /**
     * Set the link js script
     *
     * @param linkScript the link js script
     */
    public void setLinkScript(String linkScript) {
        this.linkScript = linkScript;
    }

    /**
     * Number of pages TOTAL that make up the component being paged (this must be set by the framework based on some
     * list size)
     *
     * @return the number of pages used in this pager
     */
    public int getNumberOfPages() {
        return numberOfPages;
    }

    /**
     * Set the TOTAL number of pages
     *
     * @param numberOfPages
     */
    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    /**
     * The current page being shown by this pager widget (this must be set when the page is changed)
     *
     * @return the current page being shown
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Set the current page
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * The text to use on the previous link.
     *
     * @return the previous link text
     */
    @BeanTagAttribute
    public String getPrevText() {
        return prevText;
    }

    /**
     * @see Pager#getPrevText()
     */
    public void setPrevText(String prevText) {
        this.prevText = prevText;
    }

    /**
     * The text to use on the next link.
     *
     * @return the next link text
     */
    @BeanTagAttribute
    public String getNextText() {
        return nextText;
    }

    /**
     * @see Pager#getNextText()
     */
    public void setNextText(String nextText) {
        this.nextText = nextText;
    }
}
