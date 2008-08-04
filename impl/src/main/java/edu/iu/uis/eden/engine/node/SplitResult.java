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
package edu.iu.uis.eden.engine.node;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of the processing of a {@link SplitNode}.  Contains a List of branch names that
 * the document's route path should split to.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SplitResult extends SimpleResult {

	private List branchNames = new ArrayList();
	
	public SplitResult(List branchNames) {
		super(true);
		this.branchNames = branchNames;
	}

	public List getBranchNames() {
		return branchNames;
	}

	protected void setBranchNames(List branchNames) {
		this.branchNames = branchNames;
	}
	
	
	
}
