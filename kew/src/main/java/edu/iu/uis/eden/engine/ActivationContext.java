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
package edu.iu.uis.eden.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the current Activation context of the workflow engine
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActivationContext implements Serializable {
    private static final long serialVersionUID = 5063689034941122774L;

    public static final boolean CONTEXT_IS_SIMULATION = true;
	
	boolean simulation = !CONTEXT_IS_SIMULATION;
    boolean actionsToPerform = false;
    List simulatedActionsTaken = new ArrayList();
    List generatedActionItems = new ArrayList();
    
	public ActivationContext(boolean simulation) {
		super();
		this.simulation = simulation;
	}
	
	public ActivationContext(boolean simulation, List simulatedActionsTaken) {
		super();
		this.simulation = simulation;
		this.simulatedActionsTaken = simulatedActionsTaken;
	}

    public boolean isActionsToPerform() {
        return actionsToPerform;
    }

    public void setActionsToPerform(boolean actionsToPerform) {
        this.actionsToPerform = actionsToPerform;
    }

    public List getGeneratedActionItems() {
        return generatedActionItems;
    }

    public void setGeneratedActionItems(List generatedActionItems) {
        this.generatedActionItems = generatedActionItems;
    }

    public List getSimulatedActionsTaken() {
        return simulatedActionsTaken;
    }

    public void setSimulatedActionsTaken(List simulatedActionsTaken) {
        this.simulatedActionsTaken = simulatedActionsTaken;
    }

    public boolean isSimulation() {
        return simulation;
    }

    public void setSimulation(boolean simulation) {
        this.simulation = simulation;
    }

}
