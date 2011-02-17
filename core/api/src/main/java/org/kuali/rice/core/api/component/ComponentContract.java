package org.kuali.rice.core.api.component;

/**
 * This is the contract for a Parameter Component.  It is a way of assigning a parameter to a functional component
 * within a rice application or rice ecosystem.
 */
public interface ComponentContract {

    /**
     * This is the code value for the component.  It cannot be null or a blank string.
     * @return code
     */
    String getCode();

    /**
     * This is the name value for the component.  It cannot be null of a blank string.
     * @return name
     */
    String getName();

    /**
     * This is the namespace for the component.  It cannot be null of a blank string.
     * <p/>
     * <p>
     * It is a way of assigning the component to a logical grouping within a rice application or rice ecosystem.
     * </p>
     *
     * @return namespace code
     */
    String getNamespaceCode();

    /**
     * Whether this is a virtual or derived component.
     * @return virtual
     */
    boolean isVirtual();

    /**
     * This the active flag for the ParameterComponent.
     *
     * @return active
     */
    boolean isActive();
}
