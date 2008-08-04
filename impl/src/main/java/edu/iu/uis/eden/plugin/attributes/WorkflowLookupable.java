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
package edu.iu.uis.eden.plugin.attributes;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for all lookupables to implement.  A WorkflowLookupable is used to
 * provide and render a search screen within the GUI of the KEW application.
 *
 * <p>TODO: It is important that when these are created that a new instance of the
 * lookupable be created for each search.  This is because (currently) the
 * rows of the lookupables are stateful and need to maintain search criteria
 * state for each search.  For this reason also, the rows cannot be created and
 * stored statically on this class.  Instead, the Rows need to get created in the
 * constructor of the lookupable.  The columns, however, can be created once and
 * stored in a static List on this class.  This is really unfortunate and I hope
 * we can address this in the Rice framework and it's lookupable implementation
 * when it is complete.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkflowLookupable {

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