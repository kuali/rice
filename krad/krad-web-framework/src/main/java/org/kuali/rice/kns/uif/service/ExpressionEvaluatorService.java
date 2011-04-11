/*
 * Copyright 2011 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.rice.kns.uif.service;

import java.util.Map;

/**
 * Provides evaluation of expression language statements against a given context
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ExpressionEvaluatorService {

    public void evaluateObjectProperties(Object object, Object contextObject, Map<String, Object> evaluationParameters);

    public String evaluateExpressionTemplate(Object contextObject, Map<String, Object> evaluationParameters,
            String expressionTemplate);

    public Object evaluateExpression(Object contextObject, Map<String, Object> evaluationParameters, String expression);
}
