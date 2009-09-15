/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.lookup;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kns.lookup.Lookupable;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface GroupLookupable {
    public String getHtmlMenuBar();

    public List getRows();

    public String getTitle();

    public String getReturnLocation();

    public List getColumns();

    public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception;

    public String getNoReturnParams(Map fieldConversions);

    public String getLookupInstructions();

    public List getDefaultReturnType();

    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception;

    public void changeIdToName(Map fieldValues) throws Exception;

}
