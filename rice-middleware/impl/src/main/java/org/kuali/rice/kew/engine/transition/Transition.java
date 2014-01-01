/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kew.engine.transition;

import org.kuali.rice.kew.engine.node.RouteNodeInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a transition to a set of Node Instances.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Transition {

	private List<RouteNodeInstance> nextNodeInstances = new ArrayList<RouteNodeInstance>();
	
	public Transition() {
	}
	
	public Transition(List<RouteNodeInstance> nextNodeInstances) throws Exception {
        this.nextNodeInstances = nextNodeInstances;
	}

	public List<RouteNodeInstance> getNextNodeInstances() {
		return nextNodeInstances;
	}

	protected void setNextNodeInstances(List<RouteNodeInstance> nextNodeInstances) {
		this.nextNodeInstances = nextNodeInstances;
	}
	
}
