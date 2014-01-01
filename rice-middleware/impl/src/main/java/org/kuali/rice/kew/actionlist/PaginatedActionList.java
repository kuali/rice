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
package org.kuali.rice.kew.actionlist;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;
import org.kuali.rice.kew.actionitem.ActionItemBase;

import java.util.List;

/**
 * Implements the display tags paginated list to provide effecient paging for the action list.
 * This allows us not to have to fetch an entire action list each time a user pages their list.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PaginatedActionList implements PaginatedList {

	private final List<? extends ActionItemBase> list;
	private final int fullListSize;
	private final int pageNumber;
	private final int objectsPerPage;
	private final String searchId;
	private final String sortCriterion;
	private final SortOrderEnum sortDirection;

	public PaginatedActionList(List<? extends ActionItemBase> list, int fullListSize, int pageNumber, int objectsPerPage, String searchId, String sortCriterion, SortOrderEnum sortDirection) {
		this.list = list;
		this.fullListSize = fullListSize;
		this.pageNumber = pageNumber;
		this.objectsPerPage = objectsPerPage;
		this.searchId = searchId;
		this.sortCriterion = sortCriterion;
		this.sortDirection = sortDirection;
	}

	@Override
    public int getFullListSize() {
		return fullListSize;
	}

	@Override
    public List getList() {
		return list;
	}

	@Override
    public int getObjectsPerPage() {
		return objectsPerPage;
	}

	@Override
    public int getPageNumber() {
		return pageNumber;
	}

	@Override
    public String getSearchId() {
		return searchId;
	}

	@Override
    public String getSortCriterion() {
		return sortCriterion;
	}

	@Override
    public SortOrderEnum getSortDirection() {
		return sortDirection;
	}

}
