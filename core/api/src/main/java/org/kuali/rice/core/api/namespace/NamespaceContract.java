package org.kuali.rice.core.api.namespace;

/**
 * Defines the contract for a Namespace.  A namespace is a mechanism for partitioning of data into
 * areas of responsibility.  Since much of the Kuali Rice middleware is shared across multiple integrating
 * applications, this notion of a namespace is a critical element in keeping related data elements
 * grouped together and isolated from those to which they should have no relation or access.
 */
public interface NamespaceContract {

    /**
     * This is the code value for the namespace.  It cannot be null or a blank string.
     *
     * @return code
     */
    String getCode();

    /**
     * This the application code for the Namespace.  This cannot be null or a blank string.
     *
     * <p>
     * It is a way of assigning the Namespace to a specific rice application or rice ecosystem.
     * </p>
     *
     * @return application code
     */
    String getApplicationCode();

    /**
     * This the name for the Namespace.  This can be null or a blank string.
     *
     * @return name
     */
    String getName();

    /**
     * This the active flag for the Namespace.
     *
     * @return active
     */
    boolean isActive();

    /**
     * This the object version number for Namespace.
     *
     * @return the version number of the Namespace
     */
    Long getVersionNumber();
}
