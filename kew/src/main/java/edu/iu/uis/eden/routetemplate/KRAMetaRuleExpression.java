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

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Expression implementation for KRA "meta rules".  A KRA "meta rule" consists of a sequence of
 * subordinate rule evaluations each processed according to an associated modifier:
 * <dl>
 *   <dt>next<dt>
 *   <dd>proceed with rule evaluation</dd>
 *   <dt>true</dt>
 *   <dd>if this rule evaluates to true, then return the "map" (responsibilities) associated with the rule</dd>
 *   <dt>false</dt>
 *   <dd>if this rule evaluates to false, then return the "map" (responsibilities) associated with the rule</dd>
 * </dl>
 * E.g.
 * <div><tt>bizRule1: next; bizRule2: true; bizRule3: false</tt></div>
 * @author Aaron Hamid (arh14 at cornell dot edu)
 */
public class KRAMetaRuleExpression implements RuleExpression {

    public RuleExpressionResult evaluate() throws EdenUserNotFoundException, WorkflowException {
        return null;
    }
}