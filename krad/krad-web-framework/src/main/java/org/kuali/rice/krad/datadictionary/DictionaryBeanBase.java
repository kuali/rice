package org.kuali.rice.krad.datadictionary;

/**
 * Common base for all objects that can be configured in the dictionary
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DictionaryBeanBase implements DictionaryBean {
    
    private String namespaceCode;
    private String componentCode;

    public DictionaryBeanBase() {

    }

    /**
     * @see DictionaryBean#getNamespaceCode()
     */
    public String getNamespaceCode() {
        return namespaceCode;
    }

    /**
     * Setter for the bean's associated namespace code
     *
     * @param namespaceCode
     */
    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    /**
     * @see DictionaryBean#getComponentCode()
     */
    public String getComponentCode() {
        return componentCode;
    }

    /**
     * Setter for the bean's associated component code
     *
     * @param componentCode
     */
    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }
}
