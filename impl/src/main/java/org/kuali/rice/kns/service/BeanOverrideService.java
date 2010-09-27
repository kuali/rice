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
package org.kuali.rice.kns.service;

/**
 * This interface is used to find all beans of the BeanOverride type and modify the contents of a  
 * Data Dictionary bean as specified by the BeanOverride. It must be called after *all* Data Dictionary 
 * files have been parsed. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface BeanOverrideService {

    /**
     * Find all beans of the BeanOverride type and modify the contents of a Data Dictionary bean as specified by the BeanOverride.
     *
     */
	void performOverrides();
}
