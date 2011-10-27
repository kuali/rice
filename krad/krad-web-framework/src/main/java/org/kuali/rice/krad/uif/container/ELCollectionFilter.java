package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.service.ExpressionEvaluatorService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection filter that evaluates a configured el expression against each line
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ELCollectionFilter implements CollectionFilter {
    private static final long serialVersionUID = 3273495753269940272L;

    private String expression = "";

    /**
     * Iterates through the collection and evaluates the el expression in context of the line. If the expression
     * evaluates to true, the line will remain, else be filtered out
     *
     * @see org.kuali.rice.krad.uif.container.CollectionFilter#filter(org.kuali.rice.krad.uif.view.View, Object,
     *      CollectionGroup)
     */
    @Override
    public List<Integer> filter(View view, Object model, CollectionGroup collectionGroup) {
        // get the collection for this group from the model
        List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(model,
                collectionGroup.getBindingInfo().getBindingPath());

        // iterate through and add index that pass the expression
        List<Integer> showIndexes = new ArrayList<Integer>();

        int lineIndex = 0;
        for (Object line : modelCollection) {
            Map<String, Object> context = new HashMap<String, Object>(collectionGroup.getContext());
            context.put(UifConstants.ContextVariableNames.LINE, line);

            Boolean conditionPasses = (Boolean) getExpressionEvaluatorService().evaluateExpression(model, context,
                    expression);
            if (conditionPasses) {
                showIndexes.add(lineIndex);
            }

            lineIndex++;
        }

        return showIndexes;
    }

    /**
     * Expression that will be evaluated for each line to determine whether the line should be filtered
     *
     * <p>
     * If expression passes, the line will remain in the collection, otherwise be filtered out. The expression given
     * should evaluate to a boolean
     * </p>
     *
     * @return String valid el expression that evaluates to a boolean.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Setter for the expression to use for filtering
     *
     * @param expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public ExpressionEvaluatorService getExpressionEvaluatorService() {
        return KRADServiceLocatorWeb.getExpressionEvaluatorService();
    }
}
