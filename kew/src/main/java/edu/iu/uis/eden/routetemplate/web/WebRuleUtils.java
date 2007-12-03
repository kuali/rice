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
package edu.iu.uis.eden.routetemplate.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;

/**
 * Some utilities which are utilized by the {@link Rule2Action}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WebRuleUtils {

	/**
	 * Copies the existing rule onto the current document.  This is used within the web-based rule GUI to make a
	 * copy of a rule on the existing document.  Essentially, this method makes a copy of the rule and all
	 * delegates but preserves the document ID of the original rule.
	 */
    public static WebRuleBaseValues copyRuleOntoExistingDocument(WebRuleBaseValues rule) throws Exception {
        WebRuleBaseValues ruleCopy = new WebRuleBaseValues();
        PropertyUtils.copyProperties(ruleCopy, rule);
        ruleCopy.setPreviousVersionId(null);
        ruleCopy.setCurrentInd(null);
        ruleCopy.setVersionNbr(null);

        List responsibilities = new ArrayList();
        for (Iterator iter = ruleCopy.getResponsibilities().iterator(); iter.hasNext();) {
            WebRuleResponsibility responsibility = (WebRuleResponsibility) iter.next();
            WebRuleResponsibility responsibilityCopy = new WebRuleResponsibility();
            PropertyUtils.copyProperties(responsibilityCopy, responsibility);

            responsibilityCopy.setResponsibilityId(null);
            responsibilityCopy.setRuleResponsibilityKey(null);
            
            List delegations = new ArrayList();
            for (Iterator iterator = responsibilityCopy.getDelegationRules().iterator(); iterator.hasNext();) {
                RuleDelegation delegation = (RuleDelegation) iterator.next();
                RuleDelegation delegationCopy = new RuleDelegation();
                PropertyUtils.copyProperties(delegationCopy, delegation);

                delegationCopy.setDelegateRuleId(null);
                delegationCopy.setLockVerNbr(null);
                delegationCopy.setRuleDelegationId(null);
                delegationCopy.setRuleResponsibilityId(null);

                WebRuleBaseValues delegationRule = ((WebRuleBaseValues) delegation.getDelegationRuleBaseValues());
                WebRuleBaseValues ruleDelegateCopy = new WebRuleBaseValues();
                PropertyUtils.copyProperties(ruleDelegateCopy, delegationRule);

                ruleDelegateCopy.setPreviousVersionId(null);
                ruleDelegateCopy.setCurrentInd(null);
                ruleDelegateCopy.setVersionNbr(null);

                List delegateResps = new ArrayList();
                for (Iterator iterator1 = ruleDelegateCopy.getResponsibilities().iterator(); iterator1.hasNext();) {
                    WebRuleResponsibility delegateResp = (WebRuleResponsibility) iterator1.next();
                    WebRuleResponsibility delegateRespCopy = new WebRuleResponsibility();
                    PropertyUtils.copyProperties(delegateRespCopy, delegateResp);

                    delegateRespCopy.setResponsibilityId(null);
                    delegateRespCopy.setRuleResponsibilityKey(null);
                    delegateResps.add(delegateRespCopy);
                }
                ruleDelegateCopy.setResponsibilities(delegateResps);
                delegationCopy.setDelegationRuleBaseValues(ruleDelegateCopy);
                delegations.add(delegationCopy);
            }
            responsibilityCopy.setDelegationRules(delegations);
            responsibilities.add(responsibilityCopy);
        }
        ruleCopy.setResponsibilities(responsibilities);
        return ruleCopy;
    }
    
    /**
     * Makes a copy of the rule and clears the document id on the rule and any of its delegates.
     * This method is used for making a copy of a rule for a new document.  It essentially calls
     * the copyRuleOntoExistingDocument method and then clears out the document IDs.
     * 
     * @param webRuleBaseValues
     */
    public static WebRuleBaseValues copyToNewRule(WebRuleBaseValues webRuleBaseValues) throws Exception {
    	WebRuleBaseValues newRule = copyRuleOntoExistingDocument(webRuleBaseValues);
    	// clear out all document IDs on the rule and it's delegates
    	newRule.setRouteHeaderId(null);
    	for (Iterator iterator = newRule.getResponsibilities().iterator(); iterator.hasNext(); ) {
			RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
			for (Iterator iterator2 = responsibility.getDelegationRules().iterator(); iterator2.hasNext(); ) {
				RuleDelegation delegation = (RuleDelegation) iterator2.next();
				delegation.getDelegationRuleBaseValues().setRouteHeaderId(null);
			}
		}
    	return newRule;
    }

}
