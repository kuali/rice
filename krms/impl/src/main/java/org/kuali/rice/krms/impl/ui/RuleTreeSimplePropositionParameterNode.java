/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import java.util.List;

import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.impl.repository.PropositionBo;
import org.kuali.rice.krms.impl.repository.PropositionParameterBo;

/**
 * abstract data class for the rule tree {@link Node}s
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RuleTreeSimplePropositionParameterNode extends RuleTreeNode {
    protected String parameterDisplayString;
    
    public RuleTreeSimplePropositionParameterNode(PropositionBo proposition){
        super(proposition);
        setupParameterDisplayString();
    }
    
    private void setupParameterDisplayString(){
        if (proposition != null && proposition.getPropositionTypeCode().equalsIgnoreCase(PropositionType.SIMPLE.getCode())){
            // Simple Propositions should have 3 parameters ordered in reverse polish notation.
            // TODO: enhance to get term names for term type parameters.
            List<PropositionParameterBo> parameters = proposition.getParameters();
            if (parameters != null && parameters.size() == 3){
                setParameterDisplayString(parameters.get(0).getValue() 
                        + " " + parameters.get(2).getValue()
                        + " " + parameters.get(1).getValue());
            } else {
                // should not happen
            }
        }
    }
    
    /**
     * @return the parameterDisplayString
     */
    public String getParameterDisplayString() {
        return this.parameterDisplayString;
    }

    /**
     * @param parameterDisplayString the parameterDisplayString to set
     */
    public void setParameterDisplayString(String parameterDisplayString) {
        this.parameterDisplayString = parameterDisplayString;
    }

    
}
