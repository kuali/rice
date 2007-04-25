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
package org.kuali.core.web.struts.form;

import org.kuali.core.util.TableRenderUtil;

/**
 * This class holds the metadata necessary to render a table when displaytag is not being used.
 */
public class KualiTableRenderFormMetadata {
    private int viewedPageNumber;
    private int totalNumberOfPages;
    private int firstRowIndex;
    private int lastRowIndex;
    private int switchToPageNumber;
    
    /**
     * The number of rows that match the query criteria
     */
    private int resultsActualSize;
    
    /**
     * The number of rows that match the query criteria or
     *  the max results limit size (if applicable), whichever is less
     */
    private int resultsLimitedSize;
    
    /**
     * when the looked results screen was rendered, the index of the column that the results were sorted on.  -1 for unknown, index numbers
     * starting at 0
     */
    private int previouslySortedColumnIndex;
    
    /**
     * Comment for <code>columnToSortIndex</code>
     */
    private int columnToSortIndex;

    private boolean sortDescending;
    
    public KualiTableRenderFormMetadata() {
        sortDescending = false;
    }
    
    /**
     * Gets the columnToSortIndex attribute. 
     * @return Returns the columnToSortIndex.
     */
    public int getColumnToSortIndex() {
        return columnToSortIndex;
    }

    /**
     * Sets the columnToSortIndex attribute value.
     * @param columnToSortIndex The columnToSortIndex to set.
     */
    public void setColumnToSortIndex(int columnToSortIndex) {
        this.columnToSortIndex = columnToSortIndex;
    }

    /**
     * Gets the previouslySortedColumnIndex attribute. 
     * @return Returns the previouslySortedColumnIndex.
     */
    public int getPreviouslySortedColumnIndex() {
        return previouslySortedColumnIndex;
    }

    /**
     * Sets the previouslySortedColumnIndex attribute value.
     * @param previouslySortedColumnIndex The previouslySortedColumnIndex to set.
     */
    public void setPreviouslySortedColumnIndex(int previouslySortedColumnIndex) {
        this.previouslySortedColumnIndex = previouslySortedColumnIndex;
    }

    /**
     * Gets the resultsActualSize attribute. 
     * @return Returns the resultsActualSize.
     */
    public int getResultsActualSize() {
        return resultsActualSize;
    }

    /**
     * Sets the resultsActualSize attribute value.
     * @param resultsActualSize The resultsActualSize to set.
     */
    public void setResultsActualSize(int resultsActualSize) {
        this.resultsActualSize = resultsActualSize;
    }

    /**
     * Gets the resultsLimitedSize attribute. 
     * @return Returns the resultsLimitedSize.
     */
    public int getResultsLimitedSize() {
        return resultsLimitedSize;
    }

    /**
     * Sets the resultsLimitedSize attribute value.
     * @param resultsLimitedSize The resultsLimitedSize to set.
     */
    public void setResultsLimitedSize(int resultsLimitedSize) {
        this.resultsLimitedSize = resultsLimitedSize;
    }

    /**
     * Gets the switchToPageNumber attribute. 
     * @return Returns the switchToPageNumber.
     */
    public int getSwitchToPageNumber() {
        return switchToPageNumber;
    }

    /**
     * Sets the switchToPageNumber attribute value.
     * @param switchToPageNumber The switchToPageNumber to set.
     */
    public void setSwitchToPageNumber(int switchToPageNumber) {
        this.switchToPageNumber = switchToPageNumber;
    }

    /**
     * Gets the viewedPageNumber attribute. 
     * @return Returns the viewedPageNumber.
     */
    public int getViewedPageNumber() {
        return viewedPageNumber;
    }

    /**
     * Sets the viewedPageNumber attribute value.
     * @param viewedPageNumber The viewedPageNumber to set.
     */
    public void setViewedPageNumber(int viewedPageNumber) {
        this.viewedPageNumber = viewedPageNumber;
    }

    /**
     * Gets the totalNumberOfPages attribute. 
     * @return Returns the totalNumberOfPages.
     */
    public int getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    /**
     * Sets the totalNumberOfPages attribute value.
     * @param totalNumberOfPages The totalNumberOfPages to set.
     */
    public void setTotalNumberOfPages(int totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }

    /**
     * Gets the firstRowIndex attribute. 
     * @return Returns the firstRowIndex.
     */
    public int getFirstRowIndex() {
        return firstRowIndex;
    }

    /**
     * Sets the firstRowIndex attribute value.
     * @param firstRowIndex The firstRowIndex to set.
     */
    public void setFirstRowIndex(int firstRowIndex) {
        this.firstRowIndex = firstRowIndex;
    }

    /**
     * Gets the lastRowIndex attribute. 
     * @return Returns the lastRowIndex.
     */
    public int getLastRowIndex() {
        return lastRowIndex;
    }

    /**
     * Sets the lastRowIndex attribute value.
     * @param lastRowIndex The lastRowIndex to set.
     */
    public void setLastRowIndex(int lastRowIndex) {
        this.lastRowIndex = lastRowIndex;
    }

    /**
     * Gets the sortDescending attribute. 
     * @return Returns the sortDescending.
     */
    public boolean isSortDescending() {
        return sortDescending;
    }

    /**
     * Sets the sortDescending attribute value.
     * @param sortDescending The sortDescending to set.
     */
    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }
    
    /**
     * Sets the paging form parameters to go to the first page of the list
     * 
     * @param listSize size of table being rendered
     * @param maxRowsPerPage
     */
    public void jumpToFirstPage(int listSize, int maxRowsPerPage) {
        jumpToPage(0, listSize, maxRowsPerPage);
    }

    /**
     * Sets the paging form parameters to go to the last page of the list
     * 
     * @param listSize size of table being rendered
     * @param maxRowsPerPage
     */
    public void jumpToLastPage(int listSize, int maxRowsPerPage) {
        jumpToPage(TableRenderUtil.computeTotalNumberOfPages(listSize, maxRowsPerPage) - 1, listSize, maxRowsPerPage);
    }
    
    /**
     * Sets the paging form parameters to go to the specified page of the list
     * 
     * @param pageNumber first page is 0, must be non-negative.  If the list is not large enough to have the page specified, then
     *   this method will be equivalent to calling jumpToLastPage.
     * @param listSize size of table being rendered
     * @param maxRowsPerPage
     * 
     * @see KualiTableRenderFormMetadata#jumpToLastPage(int, int)
     */
    public void jumpToPage(int pageNumber, int listSize, int maxRowsPerPage) {
        int totalPages = TableRenderUtil.computeTotalNumberOfPages(listSize, maxRowsPerPage);
        setTotalNumberOfPages(totalPages);
        if (pageNumber >= totalPages) {
            pageNumber = totalPages - 1;
        }
        setViewedPageNumber(pageNumber);
        setFirstRowIndex(TableRenderUtil.computeStartIndexForPage(pageNumber, listSize, maxRowsPerPage));
        setLastRowIndex(TableRenderUtil.computeLastIndexForPage(pageNumber, listSize, maxRowsPerPage));
    }
    
}
