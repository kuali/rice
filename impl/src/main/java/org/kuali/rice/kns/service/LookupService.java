/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.service;

import java.util.Collection;
import java.util.Map;

/**
 * This interface defines methods that a Lookup Service must provide.
 * 
 * 
 */
public interface LookupService {
    
    /**
     * 
     * Returns a collection of objects based on the given search parameters. Will not limit results, so the returned Collection
     * could be huge.
     * 
     * @param example
     * @param formProps
     * @return
     */
    public Collection findCollectionBySearchUnbounded(Class example, Map formProps);


    /**
     * Returns a collection of objects based on the given search parameters.
     * 
     * @return Collection returned from the search
     */
    public Collection findCollectionBySearch(Class example, Map formProps);
    
    public Collection findCollectionBySearchHelper(Class example, Map formProperties, boolean unbounded);

    /**
     * Retrieves a Object based on the search criteria, which should uniquely identify a record.
     * 
     * @return Object returned from the search
     */
    public Object findObjectBySearch(Class example, Map formProps);
    
    public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class boClass, Map formProps);
}
