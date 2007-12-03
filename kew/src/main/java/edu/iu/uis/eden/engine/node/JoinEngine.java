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

/**
 * The JoinEngine is responsible for maintaining join state and determining when the join condition
 * has been satisfied.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface JoinEngine {

    public void addExpectedJoiner(RouteNodeInstance nodeInstance, Branch branch);
    
    public void addActualJoiner(RouteNodeInstance nodeInstance, Branch branch);
    
    public boolean isJoined(RouteNodeInstance nodeInstance);
    
    public void createExpectedJoinState(RouteContext context, RouteNodeInstance joinInstance, RouteNodeInstance previousNodeInstance);
}
