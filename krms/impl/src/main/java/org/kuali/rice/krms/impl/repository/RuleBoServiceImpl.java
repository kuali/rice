/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.krms.impl.repository;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.RuleDefinition;
import org.kuali.rice.krms.api.repository.RuleAttribute;

import java.util.*;

public final class RuleBoServiceImpl implements RuleBoService {

    private BusinessObjectService businessObjectService;

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#createRule(org.kuali.rice.krms.api.repository.RuleDefinition)
	 */
	@Override
	public void createRule(RuleDefinition rule) {
		// TODO dseibert - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#updateRule(org.kuali.rice.krms.api.repository.RuleDefinition)
	 */
	@Override
	public void updateRule(RuleDefinition rule) {
		// TODO dseibert - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#getRuleByRuleId(java.lang.String)
	 */
	@Override
	public RuleDefinition getRuleByRuleId(String ruleId) {
		// TODO dseibert - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#createRuleAttribute(org.kuali.rice.krms.api.repository.RuleAttribute)
	 */
	@Override
	public void createRuleAttribute(RuleAttribute ruleAttribute) {
		// TODO dseibert - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.impl.repository.RuleBoService#updateRuleAttribute(org.kuali.rice.krms.api.repository.RuleAttribute)
	 */
	@Override
	public void updateRuleAttribute(RuleAttribute ruleAttribute) {
		// TODO dseibert - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   public RuleDefinition to(RuleBo bo) {
	   if (bo == null) { return null; }
	   // TODO implement
	   return null;
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* TODO: move to() and from() to impl service
	* @param im immutable object
	* @return the mutable bo
	*/
   public RuleBo from(RuleDefinition im) {
	   if (im == null) { return null; }

	   RuleBo bo = new RuleBo();
	   bo.setRuleId( im.getRuleId() );
	   bo.setNamespace( im.getNamespace() );
	   bo.setName( im.getName() );
	   bo.setTypeId( im.getTypeId() );
	   List<RuleAttributeBo> attrList = new ArrayList<RuleAttributeBo>();
	   for (RuleAttribute attr : im.getAttributes()){
		   attrList.add ( RuleAttributeBo.from(attr) );
	   }
	   bo.setAttributes(attrList);
	   return bo;
   }
 

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Converts a List<RuleBo> to an Unmodifiable List<Rule>
     *
     * @param RuleBos a mutable List<RuleBo> to made completely immutable.
     * @return An unmodifiable List<Rule>
     */
    List<RuleDefinition> convertListOfBosToImmutables(final Collection<RuleBo> ruleBos) {
        ArrayList<RuleDefinition> rules = new ArrayList<RuleDefinition>();
        for (RuleBo bo : ruleBos) {
            RuleDefinition rule = to(bo);
            rules.add(rule);
        }
        return Collections.unmodifiableList(rules);
    }

}
