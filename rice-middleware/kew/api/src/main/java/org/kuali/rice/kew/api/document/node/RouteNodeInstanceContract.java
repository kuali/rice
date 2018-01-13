/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.api.document.node;

import java.util.List;

import org.kuali.rice.core.api.mo.common.Identifiable;

public interface RouteNodeInstanceContract extends Identifiable {

	String getDocumentId();

	String getBranchId();
	
	String getRouteNodeId();
	
	String getProcessId();
	
	String getName();
	
	boolean isActive();
	
	boolean isComplete();
	
	boolean isInitial();
	
	List<? extends RouteNodeInstanceStateContract> getState();

    List<? extends RouteNodeInstanceContract> getNextNodeInstances();
	
}
