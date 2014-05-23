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
package org.kuali.rice.krad.rules.rule.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nigupta on 4/28/2014.
 */
abstract public class RuleEventBase implements RuleEvent {

    private String name;
    private final String description;
    private final String errorPathPrefix;
    private Map<String, Object> facts = new HashMap<String, Object>();
    private String ruleMethodName;

    /**
     * As a general rule, business rule classes should not change the original object. This constructor was created so
     * that PreRulesCheckEvent, a UI level rule checker, can make changes.
     *
     * @param description
     * @param errorPathPrefix
     */
    public RuleEventBase( String description, String errorPathPrefix ) {
        this.description = description;
        this.errorPathPrefix = errorPathPrefix;
    }

    public void addFact( String key, Object object ) {
        facts.put( key, object );
    }

    /**
     * the name of this event
     * @return - the event name
     */
    public String getName() {
        return name;
    }

    /**
     * @see RuleEventBase#getName()
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return a description of this event
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @return the error path prefix for this event
     */
    public String getErrorPathPrefix() {
        return errorPathPrefix;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getFacts() {
        return facts;
    }

    /**
     * @see RuleEventBase#getFacts()
     */
    public void setFacts( Map<String, Object> facts ) {
        this.facts = facts;
    }

    /**
     * {@inheritDoc}
     */
    public String getRuleMethodName() {
        return ruleMethodName;
    }

    /**
     * @see RuleEventBase#getRuleMethodName()
     */
    public void setRuleMethodName( String name ) {
        this.ruleMethodName = name;
    }

    /**
     * @see org.kuali.rice.krad.rules.rule.event.RuleEvent#generateEvents()
     */
    @Override
    public List<RuleEvent> generateEvents() {
        return new ArrayList<RuleEvent>();
    }
}
