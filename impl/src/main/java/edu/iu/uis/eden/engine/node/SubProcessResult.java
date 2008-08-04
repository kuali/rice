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

import edu.iu.uis.eden.engine.transition.SubProcessTransitionEngine;

/**
 * The result of the execution of a {@link SubProcessNode}.  The work of initiation and instantiation of
 * a sub process is handled by the {@link SubProcessTransitionEngine} so this result will always wrap
 * a {@link SimpleResult} with a completion value of true.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SubProcessResult extends SimpleResult {

	public SubProcessResult() {
		super(true);
	}
	
}
