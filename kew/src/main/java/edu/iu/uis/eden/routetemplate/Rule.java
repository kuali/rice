/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.routetemplate;

/**
 * A Rule wraps a rule definition (RuleBaseValues) and exposes it as a {@link RuleExpression}.
 * 'Rule' is defined as an interface to make the contract in the system clear and independent of implementation.
 * @see RuleBaseValues
 * @see RuleExpression
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
public interface Rule extends RuleExpression {
    /**
     * @return the rule definition this rule is associated with
     */
    public RuleBaseValues getDefinition();
}