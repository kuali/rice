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
package org.kuali.workflow.tools.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SpreadsheetOutput {

	private Map<String, Workgroup> workgroups = new HashMap<String, Workgroup>();
	private List<Rule> rules = new ArrayList<Rule>();

	public List<Rule> getRules() {
		return rules;
	}
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
	public Map<String, Workgroup> getWorkgroups() {
		return workgroups;
	}
	public void setWorkgroups(Map<String, Workgroup> workgroups) {
		this.workgroups = workgroups;
	}

	public void merge(SpreadsheetOutput output) {
		workgroups.putAll(output.getWorkgroups());
		rules.addAll(output.getRules());
	}

}
