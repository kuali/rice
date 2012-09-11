package org.kuali.rice.krad.datadictionary;

/**
 * Common interface for all objects that can be configured in the dictionary
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DictionaryBean {

    /**
     * Namespace code (often an application or module code) that dictionary bean is associated with
     *
     * <p>
     * Note this may be assigned through the bean definition itself, or associated by the module configuration
     * and its dictionary files
     * </p>
     *
     * @return String namespace code
     */
    public String getNamespaceCode();

    /**
     * A code within the namespace that identifies a component or group the bean is associated with
     *
     * @return String representing a component code
     */
    public String getComponentCode();
}
