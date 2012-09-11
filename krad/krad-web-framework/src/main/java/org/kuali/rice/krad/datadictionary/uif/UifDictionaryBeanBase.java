package org.kuali.rice.krad.datadictionary.uif;

import org.kuali.rice.krad.datadictionary.DictionaryBeanBase;
import org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Common base class for dictionary objects that can contain dynamic expressions within the
 * property value
 *
 * <p>
 * Should be extended by other classes to provide property expression support
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifDictionaryBeanBase extends DictionaryBeanBase implements UifDictionaryBean {

    private Map<String, String> expressionGraph;
    private Map<String, String> refreshExpressionGraph;
    private Map<String, String> propertyExpressions;

    public UifDictionaryBeanBase() {
        expressionGraph = new HashMap<String, String>();
        refreshExpressionGraph = new HashMap<String, String>();
        propertyExpressions = new HashMap<String, String>();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#getExpressionGraph()
     */
    public Map<String, String> getExpressionGraph() {
        return expressionGraph;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#setExpressionGraph(java.util.Map<java.lang.String,java.lang.String>)
     */
    public void setExpressionGraph(Map<String, String> expressionGraph) {
        this.expressionGraph = expressionGraph;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#getRefreshExpressionGraph()
     */
    public Map<String, String> getRefreshExpressionGraph() {
        return refreshExpressionGraph;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#setRefreshExpressionGraph(java.util.Map<java.lang.String,java.lang.String>)
     */
    public void setRefreshExpressionGraph(Map<String, String> refreshExpressionGraph) {
        this.refreshExpressionGraph = refreshExpressionGraph;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#getPropertyExpressions
     */
    public Map<String, String> getPropertyExpressions() {
        return propertyExpressions;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#setPropertyExpressions
     */
    public void setPropertyExpressions(Map<String, String> propertyExpressions) {
        this.propertyExpressions = propertyExpressions;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.uif.UifDictionaryBean#getPropertyExpression
     */
    public String getPropertyExpression(String propertyName) {
        if (this.propertyExpressions.containsKey(propertyName)) {
            return this.propertyExpressions.get(propertyName);
        }

        return null;
    }
}
