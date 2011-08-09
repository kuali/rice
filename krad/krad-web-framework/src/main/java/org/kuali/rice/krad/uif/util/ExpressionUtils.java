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
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.layout.LayoutManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for UIF expressions
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExpressionUtils {

    /**
     * @param view
     * @param object
     */
    public static void adjustPropertyExpressions(View view, Object object) {
        if (object == null) {
            return;
        }

        // get the map of property expressions to adjust
        Map<String, String> propertyExpressions = new HashMap<String, String>();
        if (Component.class.isAssignableFrom(object.getClass())) {
            propertyExpressions = ((Component) object).getPropertyExpressions();
        } else if (LayoutManager.class.isAssignableFrom(object.getClass())) {
            propertyExpressions = ((LayoutManager) object).getPropertyExpressions();
        } else if (BindingInfo.class.isAssignableFrom(object.getClass())) {
            propertyExpressions = ((BindingInfo) object).getPropertyExpressions();
        }

        boolean defaultPathSet = StringUtils.isNotBlank(view.getDefaultBindingObjectPath());

        Map<String, String> adjustedPropertyExpressions = new HashMap<String, String>();
        for (Map.Entry<String, String> propertyExpression : propertyExpressions.entrySet()) {
            String propertyName = propertyExpression.getKey();
            String expression = propertyExpression.getValue();

            // if property name is nested, need to move the expression to the parent object
            if (StringUtils.contains(propertyName, ".")) {
                boolean expressionMoved = moveNestedPropertyExpression(object, propertyName, expression);

                // if expression moved, skip rest of control statement so it is not added to the adjusted map
                if (expressionMoved) {
                    continue;
                }
            }

            adjustedPropertyExpressions.put(propertyName, expression);
        }

        // update property expressions map on object
        ObjectPropertyUtils.setPropertyValue(object, UifPropertyPaths.PROPERTY_EXPRESSIONS,
                adjustedPropertyExpressions);

        // TODO: In progress, adjusting paths in expressions
//            if (defaultPathSet) {
//                String adjustedExpression = "";
//
//                // check for expression placeholder wrapper or multiple expressions (template)
//                if (StringUtils.contains(expression, "@{") && StringUtils.contains(expression, "}")) {
//                    String remainder = expression;
//
//                    while (StringUtils.isNotBlank(remainder) && StringUtils.contains(remainder, "@{") && StringUtils
//                            .contains(remainder, "}")) {
//                        String beforeLiteral = StringUtils.substringBefore(remainder, "@{");
//                        String afterBeginDelimiter = StringUtils.substringAfter(remainder, "@{");
//                        String nestedExpression = StringUtils.substringBefore(afterBeginDelimiter, "}");
//                        remainder = StringUtils.substringAfter(afterBeginDelimiter, "}");
//
//                        if (StringUtils.isNotBlank(beforeLiteral)) {
//                            adjustedExpression += beforeLiteral;
//                        }
//                        adjustedExpression += "@{";
//
//                        if (StringUtils.isNotBlank(nestedExpression)) {
//                            String adjustedNestedExpression = processExpression(nestedExpression,
//                                    view.getDefaultBindingObjectPath());
//                            adjustedExpression += adjustedNestedExpression;
//                        }
//                        adjustedExpression += "}";
//                    }
//
//                    // add last remainder if was a literal (did not contain expression placeholders)
//                    if (StringUtils.isNotBlank(remainder)) {
//                        adjustedExpression += remainder;
//                    }
//                } else {
//                    // treat expression as one
//                    adjustedExpression = processExpression(expression, view.getDefaultBindingObjectPath());
//                }
//
//                adjustedPropertyExpressions.put(propertyName, adjustedExpression);
//            } else {
//                adjustedPropertyExpressions.put(propertyName, expression);
//            }
//        }
//

    }

    protected static String processExpression(String expression, String pathPrefix) {
        String processedExpression = "";

        Tokenizer tokenizer = new Tokenizer(expression);
        tokenizer.process();

        Tokenizer.Token previousToken = null;
        for (Tokenizer.Token token : tokenizer.getTokens()) {
            if (token.isIdentifier()) {
                // if an identifier, verify it is a model property name (must be at beginning of expression of
                // come after a space)
                String identifier = token.stringValue();
                if ((previousToken == null) || (previousToken.isIdentifier() && StringUtils.isBlank(
                        previousToken.stringValue()))) {
                    // append path prefix unless specified as form property
                    if (!StringUtils.startsWith(identifier, "form")) {
                        identifier = pathPrefix + "." + identifier;
                    }
                }
                processedExpression += identifier;
            } else if (token.getKind().tokenChars.length != 0) {
                processedExpression += new String(token.getKind().tokenChars);
            } else {
                processedExpression += token.stringValue();
            }

            previousToken = token;
        }

        // remove special binding prefixes
        processedExpression = StringUtils.replace(processedExpression, UifConstants.NO_BIND_ADJUST_PREFIX, "");

        return processedExpression;
    }

    protected static boolean moveNestedPropertyExpression(Object object, String propertyName, String expression) {
        boolean moved = false;

        // get the parent object for the property
        String parentPropertyName = StringUtils.substringBeforeLast(propertyName, ".");
        String propertyNameInParent = StringUtils.substringAfterLast(propertyName, ".");

        Object parentObject = ObjectPropertyUtils.getPropertyValue(object, parentPropertyName);
        if ((parentObject != null) && ObjectPropertyUtils.isReadableProperty(parentObject,
                UifPropertyPaths.PROPERTY_EXPRESSIONS) && ((parentObject instanceof Component)
                || (parentObject instanceof LayoutManager)
                || (parentObject instanceof BindingInfo))) {
            Map<String, String> propertyExpressions = ObjectPropertyUtils.getPropertyValue(parentObject,
                    UifPropertyPaths.PROPERTY_EXPRESSIONS);
            if (propertyExpressions == null) {
                propertyExpressions = new HashMap<String, String>();
            }

            // add expression to map on parent object
            propertyExpressions.put(propertyNameInParent, expression);
            ObjectPropertyUtils.setPropertyValue(parentObject, UifPropertyPaths.PROPERTY_EXPRESSIONS,
                    propertyExpressions);
            moved = true;
        }

        return moved;
    }

    /**
     * Takes in an expression and a list to be filled in with names(property names)
     * of controls found in the expression. This method returns a js expression which can
     * be executed on the client to determine if the original exp was satisfied before
     * interacting with the server - ie, this js expression is equivalent to the one passed in.
     *
     * There are limitations on the Spring expression language that can be used as this method.
     * It is only used to parse expressions which are valid case statements for determining if
     * some action/processing should be performed.  ONLY Properties, comparison operators, booleans,
     * strings, matches expression, and boolean logic are supported.  Properties must
     * be a valid property on the form, and should have a visible control within the view.
     *
     * Example valid exp: account.name == 'Account Name'
     *
     * @param exp
     * @param controlNames
     * @return
     */
    public static String parseExpression(String exp, List<String> controlNames) {
        // clean up expression to ease parsing
        exp = exp.trim();
        if(exp.startsWith("@{")){
            exp = StringUtils.removeStart(exp, "@{");
            if(exp.endsWith("}")){
                exp = StringUtils.removeEnd(exp,"}");
            }
        }

        exp = StringUtils.replace(exp, "!=", " != ");
        exp = StringUtils.replace(exp, "==", " == ");
        exp = StringUtils.replace(exp, ">", " > ");
        exp = StringUtils.replace(exp, "<", " < ");
        exp = StringUtils.replace(exp, "<=", " <= ");
        exp = StringUtils.replace(exp, ">=", " >= ");


        String conditionJs = exp;
        String stack = "";

        boolean expectingSingleQuote = false;
        boolean ignoreNext = false;
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (!expectingSingleQuote && !ignoreNext && (c == '(' || c == ' ' || c == ')')) {
                evaluateCurrentStack(stack.trim(), controlNames);
                //reset stack
                stack = "";
                continue;
            } else if (!ignoreNext && c == '\'') {
                stack = stack + c;
                expectingSingleQuote = !expectingSingleQuote;
            } else if (c == '\\') {
                stack = stack + c;
                ignoreNext = !ignoreNext;
            } else {
                stack = stack + c;
                ignoreNext = false;
            }
        }

        conditionJs = conditionJs.replaceAll("\\s(?i:ne)\\s", " != ").replaceAll("\\s(?i:eq)\\s", " == ").replaceAll(
                "\\s(?i:gt)\\s", " > ").replaceAll("\\s(?i:lt)\\s", " < ").replaceAll("\\s(?i:lte)\\s", " <= ")
                .replaceAll("\\s(?i:gte)\\s", " >= ").replaceAll("\\s(?i:and)\\s", " && ").replaceAll("\\s(?i:or)\\s",
                        " || ").replaceAll("\\s(?i:not)\\s", " != ").replaceAll("\\s(?i:null)\\s?", " '' ").replaceAll(
                        "\\s?(?i:#empty)\\((.*?)\\)", "isValueEmpty($1)");

        if (conditionJs.contains("matches")) {
            conditionJs = conditionJs.replaceAll("\\s+(?i:matches)\\s+'.*'", ".match(/" + "$0" + "/) != null ");
            conditionJs = conditionJs.replaceAll("\\(/\\s+(?i:matches)\\s+'", "(/");
            conditionJs = conditionJs.replaceAll("'\\s*/\\)", "/)");
        }

        for (String propertyName : controlNames) {
            conditionJs = conditionJs.replace(propertyName, "coerceValue(\"" + propertyName + "\")");
        }

        return conditionJs;
    }

    /**
     * Used internally by parseExpression to evalute if the current stack is a property
     * name (ie, will be a control on the form)
     *
     * @param stack
     * @param controlNames
     */
    public static void evaluateCurrentStack(String stack, List<String> controlNames) {
        if (StringUtils.isNotBlank(stack)) {
            if (!(stack.equals("==")
                    || stack.equals("!=")
                    || stack.equals(">")
                    || stack.equals("<")
                    || stack.equals(">=")
                    || stack.equals("<=")
                    || stack.equalsIgnoreCase("ne")
                    || stack.equalsIgnoreCase("eq")
                    || stack.equalsIgnoreCase("gt")
                    || stack.equalsIgnoreCase("lt")
                    || stack.equalsIgnoreCase("lte")
                    || stack.equalsIgnoreCase("gte")
                    || stack.equalsIgnoreCase("matches")
                    || stack.equalsIgnoreCase("null")
                    || stack.equalsIgnoreCase("false")
                    || stack.equalsIgnoreCase("true")
                    || stack.equalsIgnoreCase("and")
                    || stack.equalsIgnoreCase("or")
                    || stack.contains("#empty")
                    || stack.startsWith("'")
                    || stack.endsWith("'"))) {

                boolean isNumber = false;
                if ((StringUtils.isNumeric(stack.substring(0, 1)) || stack.substring(0, 1).equals("-"))) {
                    try {
                        Double.parseDouble(stack);
                        isNumber = true;
                    } catch (NumberFormatException e) {
                        isNumber = false;
                    }
                }

                if (!(isNumber)) {
                    if (!controlNames.contains(stack)) {
                        controlNames.add(stack);
                    }
                }
            }
        }
    }

}
