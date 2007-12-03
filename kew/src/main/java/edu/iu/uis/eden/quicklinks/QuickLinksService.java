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
package edu.iu.uis.eden.quicklinks;

import java.util.List;

import edu.iu.uis.eden.user.WorkflowUser;

/**
 * A service providing data access to Quick Links information.  The Quick
 * Links provide quick information and access to various functions
 * in the Quick Links GUI of the web application.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface QuickLinksService {
    public List getWatchedDocuments(WorkflowUser workflowUser);
    public List getRecentSearches(WorkflowUser workflowUser);
    public List getNamedSearches(WorkflowUser workflowUser);
    public List getActionListStats(WorkflowUser workflowUser);
    public List getInitiatedDocumentTypesList(WorkflowUser workflowUser);
}
