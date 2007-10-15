/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.workgroup.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.workgroup.BaseWorkgroup;
import edu.iu.uis.eden.workgroup.Workgroup;

public interface BaseWorkgroupDAO {

	public void save(Workgroup workgroup);
	public List search(Workgroup workgroup, Map<String, String> extensionValues);
	public List find(Workgroup workgroup, Map<String, String> extensionValues, WorkflowUser user);
	public BaseWorkgroup findByName(String workgroupName);
	public BaseWorkgroup findByWorkgroupId(Long workgroupId);
	public BaseWorkgroup findByDocumentId(Long documentId);
	public BaseWorkgroup findEnrouteWorkgroupByName(String workgroupName);
	public BaseWorkgroup findEnrouteWorkgroupById(Long workgroupId);
	public List<Long> getImmediateWorkgroupsGroupIds(Long workgroupId);
	public Set<String> findWorkgroupNamesForUser(String workflowId);
	public Set<Long> findWorkgroupIdsForUser(String workflowId);

}
