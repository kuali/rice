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

import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;

/**
 * A node which is responsible for joining 1 or more branches in the route path of a document.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface JoinNode extends Node {
	
	public JoinResult process(RouteContext context, RouteHelper helper) throws Exception;
	
}
