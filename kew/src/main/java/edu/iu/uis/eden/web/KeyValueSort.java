/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.web;

import edu.iu.uis.eden.docsearch.SearchableAttributeValue;


/**
 * A simple bean for storing key/value pairs that can be used for a number of
 * tasks. Right now it is used to hold information that will be display on a jsp
 * for drop down boxes.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KeyValueSort extends KeyValue {

	private static final long serialVersionUID = 3575440091286391804L;

	private Object sortValue;
    private Class sortClass;
    private SearchableAttributeValue searchableAttributeValue;

	public KeyValueSort() {
		super();
	}

	public KeyValueSort(String key, String value) {
		super(key,value);
	}

    public KeyValueSort(String key, String value, Object sortValue, SearchableAttributeValue searchableAttributeValue) {
        super(key,value);
        this.sortValue = sortValue;
        this.searchableAttributeValue = searchableAttributeValue;
    }

    public KeyValueSort(KeyValueSort kvs) {
        this(kvs.getkey(),kvs.getValue(),kvs.getSortValue(),kvs.getSearchableAttributeValue());
    }

	public Object getSortValue() {
		return sortValue;
	}

	public void setSortValue(Object sortValue) {
		this.sortValue = sortValue;
        this.sortClass = sortValue.getClass();
	}

    public Class getSortClass() {
        return sortClass;
    }

    public SearchableAttributeValue getSearchableAttributeValue() {
        return searchableAttributeValue;
    }

}