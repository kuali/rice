package org.kuali.rice.kim.framework.type;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kim.api.type.KimAttributeField;

import java.util.List;
import java.util.Map;

/**
 * This is the base service interface for handling type-specific behavior.  Types can be attached
 * to various objects (currently groups and roles) in KIM to add additional attributes and
 * modify their behavior.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface KimTypeService {

    /**
     * Gets the name of a workflow document type that should be passed to kew when resolving responsibilities for routing.
     *
     * This name will be passed as a qualifier with the "documentTypeName" key.
     * return null to indicate that there is no custom workflow document needed for this type.
     *
     * @return the doc type name or null.
     */
    String getWorkflowDocumentTypeName();

    /**
     * Gets an unmodifiable list of attribute names identifying the attribute qualifiers that are provided to
     * the KIM responsibility service when resolving responsibility-based routing at the node with the given name.
     *
     * Returns an empty list, indicating that no attributes from this
     * type should be passed to workflow.
     *
     * @param nodeName the name of the node to retrieve attribute names for.  Cannot be null or blank.
     * @return an unmodifiable list should not return null.
     * @throws IllegalArgumentException if the nodeName is null or blank.
     */
    List<String> getWorkflowRoutingAttributes(String nodeName) throws RiceIllegalArgumentException;

    /**
     * Gets a List of {@link KimAttributeField} for a kim type id.  The order of the attribute fields in the list
     * can be used as a hint to a ui framework consuming these attributes as to how to organize these fields.
     *
     * @param kimTypeId the kimTypeId to retrieve fields for. Cannot be null or blank.
     * @return an immutable list of KimAttributeField. Will not return null.
     * @throws IllegalArgumentException if the kimTypeId is null or blank
     */
    List<KimAttributeField> getAttributeDefinitions(String kimTypeId) throws RiceIllegalArgumentException;

    /**
     * This method validates the passed in attributes for a kimTypeId generating a List of {@link RemotableAttributeError}.
     *
     * The order of the attribute errors in the list
     * can be used as a hint to a ui framework consuming these errors as to how to organize these errors.
     *
     * @param kimTypeId the kimTypeId that is associated with the attributes. Cannot be null or blank.
     * @param attributes the kim type attributes to validate. Cannot be null.
     * @return an immutable list of RemotableAttributeError. Will not return null.
     * @throws IllegalArgumentException if the kimTypeId is null or blank or the attributes are null
     */
    List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String, String> attributes) throws RiceIllegalArgumentException;

    /**
     * This method validates the passed in attributes for a kimTypeId generating a List of {@link RemotableAttributeError}.
     * This method used the oldAttributes to aid in validation.  This is useful for validating "new" or "updated" attributes.
     *
     * The order of the attribute errors in the list
     * can be used as a hint to a ui framework consuming these errors as to how to organize these errors.
     *
     * @param kimTypeId the kimTypeId that is associated with the attributes. Cannot be null or blank.
     * @param newAttributes the kim type attributes to validate. Cannot be null.
     * @param oldAttributes the old kim type attributes to use for validation. Cannot be null.
     * @return an immutable list of RemotableAttributeError. Will not return null.
     * @throws IllegalArgumentException if the kimTypeId is null or blank or the newAttributes or oldAttributes are null
     */
    List<RemotableAttributeError> validateAttributesAgainstExisting(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes) throws RiceIllegalArgumentException;
}

