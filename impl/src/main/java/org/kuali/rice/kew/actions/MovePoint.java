/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.actions;

/**
 * Represents a point to move to in the route path.  Used by the 
 * {@link MoveDocumentAction}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MovePoint {

    private String startNodeName;
    private int stepsToMove;
    
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
