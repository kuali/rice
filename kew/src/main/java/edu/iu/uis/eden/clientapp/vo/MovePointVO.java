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
package edu.iu.uis.eden.clientapp.vo;

import java.io.Serializable;

import edu.iu.uis.eden.actions.MovePoint;

/**
 * Transport object for the {@link MovePoint}
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 * @workflow.webservice-object
 */
public class MovePointVO implements Serializable {

    private static final long serialVersionUID = 6682919843672420498L;
    
    private String startNodeName;
    private int stepsToMove;

    public MovePointVO() {}
    
    /**
     * Defines a move point which starts at the given fromNodeName and moves the
     * number of steps defined.  This can be a negative value to move backwards,
     * 0 to stay at the current position (refresh) or a positive value to move forward.
     */
    public MovePointVO(String fromNodeName, int stepsToMove) {
        this.startNodeName = fromNodeName;
        this.stepsToMove = stepsToMove;
    }

    public String getStartNodeName() {
        return startNodeName;
    }

    public void setStartNodeName(String fromNodeName) {
        this.startNodeName = fromNodeName;
    }

    public int getStepsToMove() {
        return stepsToMove;
    }

    public void setStepsToMove(int stepsToMove) {
        this.stepsToMove = stepsToMove;
    }

}
