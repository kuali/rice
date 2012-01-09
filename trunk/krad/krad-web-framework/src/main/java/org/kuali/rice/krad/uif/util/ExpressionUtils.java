/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.field.DataField;
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
     * Adjusts the property expressions for a given object. Any nested properties are moved to the parent
     * object. Binding adjust prefixes are replaced with the correct values.
     *
     * <p>
     * The org.kuali.rice.krad.uif.UifConstants#NO_BIND_ADJUST_PREFIX prefix will be removed
     * as this is a placeholder indicating that the property is directly on the form.
     * The org.kuali.rice.krad.uif.UifConstants#FIELD_PATH_BIND_ADJUST_PREFIX prefix will be replaced by
     * the object's field path - this is only applicable to DataFields. The
     * org.kuali.rice.krad.uif.UifConstants#DEFAULT_PATH_BIND_ADJUST_PREFIX prefix will be replaced
     * by the view's default path if it is set.
     * </p>
     *
     * @param view - the parent view of the object
     * @param object - Object to adjust property expressions on
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

            // replace the binding prefixes
            String adjustedExpression = replaceBindingPrefixes(view, object, expression);

            adjustedPropertyExpressions.put(propertyName, adjustedExpression);
        }

        // update property expressions map on object
        ObjectPropertyUtils.setPropertyValue(object, UifPropertyPaths.PROPERTY_EXPRESSIONS,
                adjustedPropertyExpressions);
    }

    /**
     * Adjusts the property expressions for a given object
     *
     * <p>
     * The org.kuali.rice.krad.uif.UifConstants#NO_BIND_ADJUST_PREFIX prefix will be removed
     * as this is a placeholder indicating that the property is directly on the form.
     * The org.kuali.rice.krad.uif.UifConstants#FIELD_PATH_BIND_ADJUST_PREFIX prefix will be replaced by
     * the object's field path - this is only applicable to DataFields. The
     * org.kuali.rice.krad.uif.UifConstants#DEFAULT_PATH_BIND_ADJUST_PREFIX prefix will be replaced
     * by the view's default path if it is set.
     * </p>
     *
     * @param view - the parent view of the object
     * @param object - Object to adjust property expressions on
     * @param expression - The expression to adjust
     * @return the adjusted expression String
     */
    public static String replaceBindingPrefixes(View view, Object object, String expression) {
        String adjustedExpression = StringUtils.replace(expression, UifConstants.NO_BIND_ADJUST_PREFIX, "");

        // replace the field path prefix for DataFields
        if (object instanceof DataField) {

            // Get the binding path from the object
            BindingInfo bindingInfo = ((DataField) object).getBindingInfo();
            String fieldPath = bindingInfo.getBindingPath();

            // Remove the property name from the binding path
            fieldPath = StringUtils.removeEnd(fieldPath, "." + bindingInfo.getBindingName());
            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.FIELD_PATH_BIND_ADJUST_PREFIX,
                    fieldPath + ".");
        } else {
            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.FIELD_PATH_BIND_ADJUST_PREFIX,
                    "");
        }

        // replace the default path prefix if there is one set on the view
        if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.DEFAULT_PATH_BIND_ADJUST_PREFIX,
                    view.getDefaultBindingObjectPath() + ".");

        } else {
            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.DEFAULT_PATH_BIND_ADJUST_PREFIX,
                    "");
        }

        // replace line path binding prefix with the actual line path
        if (object instanceof Component) {
            String linePath = getLinePathPrefixValue((Component) object);

            if (StringUtils.isNotEmpty(linePath)) {
                adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.LINE_PATH_BIND_ADJUST_PREFIX,
                        linePath + ".");
            }
        }

        return adjustedExpression;
    }

    /**
     * Determines the value for the org.kuali.rice.krad.uif.UifConstants#LINE_PATH_BIND_ADJUST_PREFIX binding prefix
     * based on collection group found in the component context
     *
     * @param component - component instance for which the prefix is configured on
     * @return String line binding path or empty string if path not found
     */
    protected static String getLinePathPrefixValue(Component component) {
        String linePath = "";

        CollectionGroup collectionGroup = (CollectionGroup) (component.getContext().get(
                UifConstants.ContextVariableNames.COLLECTION_GROUP));
        if (collectionGroup == null) {
            return linePath;
        }

        Object indexObj = component.getContext().get(UifConstants.ContextVariableNames.INDEX);
        if (indexObj != null) {
            int index = (Integer) indexObj;
            boolean addLine = false;
            Object addLineObj = component.getContext().get(UifConstants.ContextVariableNames.IS_ADD_LINE);

            if (addLineObj != null) {
                addLine = (Boolean) addLineObj;
            }

            if (addLine) {
                linePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
            } else {
                linePath = collectionGroup.getBindingInfo().getBindingPath() + "[" + index + "]";
            }
        }

        return linePath;
    }

    /**
     * Moves any nested property expressions to the parent object
     *
     * @param object - the object containing the expression
     * @param propertyName - the property the expression is on
     * @param expression - the epxression
     * @return
     */
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
        if (exp.startsWith("@{")) {
            exp = StringUtils.removeStart(exp, "@{");
            if (exp.endsWith("}")) {
                exp = StringUtils.removeEnd(exp, "}");
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

        if (StringUtils.isNotEmpty(stack)) {
            evaluateCurrentStack(stack.trim(), controlNames);
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
