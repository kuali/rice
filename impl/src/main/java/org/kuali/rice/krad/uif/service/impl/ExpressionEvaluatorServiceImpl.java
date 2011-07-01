/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.uif.service.impl;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.core.PropertyReplacer;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.service.ExpressionEvaluatorService;
import org.kuali.rice.krad.uif.util.ExpressionFunctions;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Evaluates expression language statements using the Spring EL engine TODO:
 * Look into using Rice KRMS for evaluation
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExpressionEvaluatorServiceImpl implements ExpressionEvaluatorService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ExpressionEvaluatorServiceImpl.class);

    /**
     * @see org.kuali.rice.krad.uif.service.ExpressionEvaluatorService#evaluateObjectProperties(java.lang.Object,
     *      java.lang.Object, java.util.Map)
     */
    public void evaluateObjectProperties(Object object, Object contextObject, Map<String, Object> evaluationParameters) {
        if (object instanceof Component) {
            evaluatePropertyReplacers(object, contextObject, evaluationParameters);
        }
        visitPropertiesAndEvaluateExpressions(object, contextObject, evaluationParameters);
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ExpressionEvaluatorService#evaluateExpressionTemplate(java.lang.Object,
     *      java.util.Map, java.lang.String)
     */
    public String evaluateExpressionTemplate(Object contextObject, Map<String, Object> evaluationParameters,
            String expressionTemplate) {
        StandardEvaluationContext context = new StandardEvaluationContext(contextObject);
        context.setVariables(evaluationParameters);
        addCustomFunctions(context);

        ExpressionParser parser = new SpelExpressionParser();
        
        String result = null;
        try {
            Expression expression = null;
            if (StringUtils.contains(expressionTemplate, UifConstants.EL_PLACEHOLDER_PREFIX)) {
                expression = parser.parseExpression(expressionTemplate, new TemplateParserContext(
                        UifConstants.EL_PLACEHOLDER_PREFIX, UifConstants.EL_PLACEHOLDER_SUFFIX));
            } else {
                expression = parser.parseExpression(expressionTemplate);
            }

            result = expression.getValue(context, String.class);
        } catch (Exception e) {
            LOG.error("Exception evaluating expression: " + expressionTemplate);
            throw new RuntimeException("Exception evaluating expression: " + expressionTemplate, e);
        }

        return result;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ExpressionEvaluatorService#evaluateExpression(java.lang.Object,
     *      java.util.Map, java.lang.String)
     */
    public Object evaluateExpression(Object contextObject, Map<String, Object> evaluationParameters,
            String expressionStr) {
        StandardEvaluationContext context = new StandardEvaluationContext(contextObject);
        context.setVariables(evaluationParameters);
        addCustomFunctions(context);

        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expressionStr);

        Object result = null;
        try {
            result = expression.getValue(context);
        }
        catch (EvaluationException e) {
            LOG.error("Exception evaluating expression: " + expressionStr);
            throw new RuntimeException("Exception evaluating expression: " + expressionStr, e);
        }

        return result;
    }

    /**
     * Registers custom functions for el expressions with the given context
     *
     * @param context - context instance to register functions to
     */
    protected void addCustomFunctions(StandardEvaluationContext context) {
        try {
            context.registerFunction("isAssignableFrom", ExpressionFunctions.class
                    .getDeclaredMethod("isAssignableFrom", new Class[]{Class.class, Class.class}));
            context.registerFunction("empty", ExpressionFunctions.class
                    .getDeclaredMethod("empty", new Class[]{Object.class}));
        } catch (NoSuchMethodException e) {
            LOG.error("Custom function for el expressions not found: " + e.getMessage());
            throw new RuntimeException("Custom function for el expressions not found: " + e.getMessage(), e);
        }
    }

    /**
     * Iterates through any configured <code>PropertyReplacer</code> instances for the component and
     * evaluates the given condition. If the condition is met, the replacement value is set on the
     * corresponding property
     *
     * @param object - object instance with property replacers list, should be either a component or layout manager
     * @param contextObject - context for el evaluation
     * @param evaluationParameters - parameters for el evaluation
     */
    protected void evaluatePropertyReplacers(Object object, Object contextObject,
                                             Map<String, Object> evaluationParameters) {
        List<PropertyReplacer> replacers = null;
        if (Component.class.isAssignableFrom(object.getClass())) {
            replacers = ((Component) object).getPropertyReplacers();
        } else if (LayoutManager.class.isAssignableFrom(object.getClass())) {
            replacers = ((LayoutManager) object).getPropertyReplacers();
        }

        for (PropertyReplacer propertyReplacer : replacers) {
            String conditionEvaluation =
                    evaluateExpressionTemplate(contextObject, evaluationParameters, propertyReplacer.getCondition());
            boolean conditionSuccess = Boolean.parseBoolean(conditionEvaluation);
            if (conditionSuccess) {
                ObjectPropertyUtils.setPropertyValue(object, propertyReplacer.getPropertyName(),
                        propertyReplacer.getReplacement());
            }
        }
    }

    /**
     * Iterates through the properties of the given object and checks for property values that contain
     * an el expression. If an expression is found it will be evaluated and the result of that evaluation
     * set back into the property value
     *
     * <p>
     *  If the property contains an el template (part static text and part expression), only the expression
     *  part will be replaced with the result. More than one expressions may be contained within the template
     * </p>
     *
     * <p>
     *  A special check is done for Map property types. When found the Map is iterated over and expressions
     *  contained in the map value are evaluated
     * </p>
     *
     * @param object - object to evaluate properties for
     * @param contextObject - context for el evaluation
     * @param evaluationParameters - parameters for el evaluation
     */
    protected void visitPropertiesAndEvaluateExpressions(Object object, Object contextObject,
            Map<String, Object> evaluationParameters) {
        // iterate through object properties and check for expressions
        PropertyDescriptor[] propertyDescriptors = ObjectPropertyUtils.getPropertyDescriptors(object);
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];

            if (descriptor.getWriteMethod() == null) {
                continue;
            }

            String propertyName = descriptor.getName();
            if (String.class.isAssignableFrom(descriptor.getPropertyType())) {
                String propertyValue = ObjectPropertyUtils.getPropertyValue(object, propertyName);

                if (StringUtils.isNotBlank(propertyValue)
                        && (containsElPlaceholder(propertyValue) || StringUtils.startsWith(propertyName,
                                UifConstants.EL_CONDITIONAL_PROPERTY_PREFIX))) {

                    // evaluate any expressions and reset property value
                    propertyValue = evaluateExpressionTemplate(contextObject, evaluationParameters, propertyValue);

                    String propertyNameToSet = propertyName;
                    if (StringUtils.startsWith(propertyName, UifConstants.EL_CONDITIONAL_PROPERTY_PREFIX)) {
                        // get the target property by convention
                        propertyNameToSet = StringUtils.removeStart(propertyName,
                                UifConstants.EL_CONDITIONAL_PROPERTY_PREFIX);
                        propertyNameToSet = StringUtils.substring(propertyNameToSet, 0, 1).toLowerCase()
                                + StringUtils.substring(propertyNameToSet, 1, propertyNameToSet.length());
                    }

                    ObjectPropertyUtils.setPropertyValue(object, propertyNameToSet, propertyValue);
                }
            }
            else if (Map.class.isAssignableFrom(descriptor.getPropertyType())) {
                Map<Object, Object> propertyValue = ObjectPropertyUtils.getPropertyValue(object, propertyName);

                if (propertyValue != null) {
                    for (Entry<Object, Object> entry : propertyValue.entrySet()) {
                        if ((entry.getValue() != null) && String.class.isAssignableFrom(entry.getValue().getClass())
                                && containsElPlaceholder((String) entry.getValue())) {
                            String entryValue = evaluateExpressionTemplate(contextObject, evaluationParameters,
                                    (String) entry.getValue());
                            propertyValue.put(entry.getKey(), entryValue);
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.service.ExpressionEvaluatorService#containsElPlaceholder(java.lang.String)
     */
    public boolean containsElPlaceholder(String value) {
        boolean containsElPlaceholder = false;

        String elPlaceholder = StringUtils.substringBetween(value, UifConstants.EL_PLACEHOLDER_PREFIX,
                UifConstants.EL_PLACEHOLDER_SUFFIX);
        if (elPlaceholder != null) {
            containsElPlaceholder = true;
        }

        return containsElPlaceholder;
    }

}
